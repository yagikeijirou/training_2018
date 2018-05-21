package application.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.dao.TLineStatusDao;
import application.entity.MUser;
import application.entity.TAttendance;
import application.entity.TLineStatus;

/**
 * 勤怠情報「リスト」操作サービス。
 * @author 隅田穂高
 */
@Service
@Transactional
public class AttendanceListService extends AbstractAttendanceService {
	/** このクラスのロガー。 */
	private static final Logger logger = LoggerFactory.getLogger(AttendanceListService.class);

	@Autowired
	MUserDao muserDao;

	@Autowired
	TAttendanceDao tattendanceDao;

    /** LINEステータス情報DAO。 */
    @Autowired
    TLineStatusDao tLineStatusDao;

	/**
	 * リッチメニューから「リスト」が選択された時の処理。
	 * @param replyToken リプライトークン
	 */
	public void startList(String replyToken) {

		//メッセージの送信
		String msg = AppMesssageSource.getMessage("line.listYearMonth");
		LineAPIService.repryMessage(replyToken, msg);
	}

	/**
	 * テキスト情報が入力された時の処理をする。
	 * @param lineId LINE識別子
	 * @param replyToken リプライトークン
	 * @param lineStatus LINEステータス
	 * @param text ユーザの入力情報
	 */
	public void listAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {

		//入力値チェック(yyyy/mmかどうか、テンプレートメッセージかどうか)
		//ユーザマスタ検索
		MUser user = muserDao.getByLineId(lineId);

		//修正フラグ
		boolean editFlg = false;

		//一般・上司・管理者チェック
		StringBuilder msg = new StringBuilder();
		//if (user.getAuthCd().equals("01")) {
		if (user.getAuthCd().equals("01") || (lineStatus!=null && lineStatus.getActionName().equals(ACTION_LIST_USER_SELECTION))) {
			//一般の場合
			//勤怠情報検索
//			List<TAttendance> tattendance = tattendanceDao.getByAttendanceMonth(user.getUserId(), text);

			//メッセージ作成(mm/dd(D) hh:mm ~ hh:mm #{"修正"}||#{""})
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(text.split("/")[0]));
			cal.set(Calendar.MONTH, Integer.parseInt(text.split("/")[1]));
			int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);

			SimpleDateFormat sdf1 = new SimpleDateFormat("E");
			SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm");

			TAttendance arrival_t, clock_out_t;
			for (int i = 1; i <= lastDayOfMonth; i++) {

				arrival_t = tattendanceDao.getByPk(user.getUserId(), "01", text.replace("/", "") + i);
				clock_out_t = tattendanceDao.getByPk(user.getUserId(), "02", text.replace("/", "") + i);
				msg.append(text.split("/")[1]);
				msg.append("/");
				msg.append(String.valueOf(i));
				msg.append("(");
				cal.set(Calendar.DATE, i);
				msg.append(sdf1.format(cal.getTime()));
				//曜日
				msg.append(") ");

				if (arrival_t != null && clock_out_t != null) {
					//出勤レコード
					if (arrival_t.getEditFlg() == "1") {
						editFlg = true;
					}
					msg.append(sdf2.format(arrival_t.getAttendanceTime()));
					msg.append("～");

					//退勤レコード
					if (clock_out_t.getEditFlg() == "1") {
						editFlg = true;
					}
					msg.append(sdf2.format(clock_out_t.getAttendanceTime()));
					if (editFlg) {
						msg.append("修正");
					}
				} else if (arrival_t != null) {//出勤レコードのみ
					if (arrival_t.getEditFlg() == "1") {
						editFlg = true;
					}
					msg.append(sdf2.format(arrival_t.getAttendanceTime()));
					msg.append("～");
				} else if (clock_out_t != null) {//退勤レコードのみ
					msg.append("～");
					if (clock_out_t.getEditFlg() == "1") {
						editFlg = true;
					}
					msg.append(sdf2.format(clock_out_t.getAttendanceTime()));
				} else {

					msg.append("---");
				}
				msg.append(System.getProperty("line.separator"));
			}
			System.out.println(msg.toString());

			//LINEステータス更新
			lineStatus.setMenuCd("empty");
			lineStatus.setActionName(null);
			lineStatus.setContents(text);
			tLineStatusDao.save(lineStatus);

			//メッセージの送信
			LineAPIService.repryMessage(replyToken, msg.toString());
		} else {
			//上司・管理者の場合
			//部下情報検索
			List<MUser> junior = muserDao.getByManagerId(user.getUserId());

			//テンプレートメッセージ作成
			List<String> msgList = new ArrayList<String>();
			for (MUser mu : junior) {
				msgList.add(mu.getName());
			}

			//テンプレートメッセージ送信
			LineAPIService.pushButtons(lineId, AppMesssageSource.getMessage("line.selectMenu"), msgList);

		}
	}
}
