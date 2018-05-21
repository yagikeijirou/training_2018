package application.service;

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

	@Autowired
	StringBuilder msg;

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
		if (user.getAuthCd().equals("1")) {
			//一般の場合
			//勤怠情報検索
			//List<TAttendance> tattendance = tattendanceDao.getByAttendanceMonth(user.getUserId(), text);

			//メッセージ作成(mm/dd(D) hh:mm ~ hh:mm #{"修正"}||#{""})
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(text.split("/")[0]));
			cal.set(Calendar.MONTH, Integer.parseInt(text.split("/")[1]));
			int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);

			TAttendance tattendance;
			for (int i = 1; i <= lastDayOfMonth; i++) {
				msg.append(text.split("/")[1]);
				msg.append("/");
				msg.append(String.valueOf(i));
				msg.append("(");
				//曜日
				msg.append(")");
				tattendance = tattendanceDao.getByPk(user.getUserId(), "01", text + "/" + i);
				if(tattendance.getEditFlg() == "1") {
					editFlg = true;
				}
				msg.append(tattendance.getAttendanceTime());
				msg.append("～");
				tattendance = tattendanceDao.getByPk(user.getUserId(), "02", text + "/" + i);
				if(tattendance.getEditFlg() == "1") {
					editFlg = true;
				}
				msg.append(tattendance.getAttendanceTime());
				if(editFlg) {
					msg.append("修正");
				}
				msg.append(System.getProperty("line.separator"));
			}

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
