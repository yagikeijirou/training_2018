package application.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

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
import application.utils.CommonUtils;

/**
 * 勤怠情報「リスト」操作サービス。
 * @author 隅田穂高
 */
@Service
@Transactional
public class AttendanceListService extends AbstractAttendanceService {
	/** このクラスのロガー。 */
	private static final Logger logger = LoggerFactory.getLogger(AttendanceListService.class);

	/** 勤怠情報DAO。 */
	@Autowired
	private TAttendanceDao tAttendanceDao;

	/** LINEステータス情報DAO。 */
	@Autowired
	private TLineStatusDao tLineStatusDao;

	/** ユーザマスタDAO。 */
	@Autowired
	private MUserDao mUserDao;

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

		//ユーザマスタ
		MUser user = null;

		//年月
		String ym = null;

		//修正フラグ
		boolean editFlg;

		//年月入力？
		if (CommonUtils.toYearMonth(text) != null) {
			if (lineStatus.getActionName().equals(ACTION_LIST_USER_SELECTION)) {
				LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.inputError"));
				return;
			}

			user = mUserDao.getByLineId(lineId);
			ym = CommonUtils.toYearMonth(text);
			//リスト選択？
		} else if (lineStatus.getActionName() != null) {
			if (lineStatus.getActionName().equals(ACTION_LIST_USER_SELECTION)) {

				try {
					user = mUserDao.getByPk(Integer.parseInt(text.split(" ")[0]));
				} catch (NumberFormatException e) {
					LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.inputError"));
					return;
				}

				if (user == null) {
					LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.inputError"));
					return;
				}
				ym = CommonUtils.toYearMonth(lineStatus.getContents());
			} else {
				LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.inputError"));
				return;
			}

		} else {
			LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.inputError"));
			return;
		}

		if (user == null) {
			LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.api.err.userNotExists"));
			return;
		}

		if (user.getAuthCd().equals("01") || (lineStatus.getActionName() != null
				&& lineStatus.getActionName().equals(ACTION_LIST_USER_SELECTION))) {

			//直接入力に対するエラー処理
			if (mUserDao.getByLineId(lineId).getAuthCd().equals("02")) {
				if (!user.getUserId().equals(lineStatus.getUserId())) {
					if (user.getManagerId() == null) {
						LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.inputError"));
						return;
					}
					if (!user.getManagerId().equals(lineStatus.getUserId())) {
						LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.inputError"));
						return;
					}
				}
			}

			//月単位の勤怠情報
			StringBuilder msg = new StringBuilder();

			//出勤、退勤情報
			TAttendance arrival_t, clock_out_t;

			//カレンダー
			Calendar cal;
			int lastDayOfMonth;
			try {
				cal = new GregorianCalendar(Integer.parseInt(ym.substring(0, 4)),
						Integer.parseInt(ym.substring(4, 6)) - 1, 1);
				cal.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

				lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
			} catch (NumberFormatException e) {
				LineAPIService.repryMessage(replyToken, AppMesssageSource.getMessage("line.inputError"));
				return;
			}

			//曜日フォーマット
			SimpleDateFormat sdf = new SimpleDateFormat("E");

			//メッセージ作成(mm/dd(D) hh:mm ~ hh:mm #{"修正"}||#{""})
			for (int i = 1; i <= lastDayOfMonth; i++) {

				editFlg = false;

				arrival_t = tAttendanceDao.getByPk(user.getUserId(), "01", ym + String.format("%02d", i));
				clock_out_t = tAttendanceDao.getByPk(user.getUserId(), "02", ym + String.format("%02d", i));
				cal.set(Calendar.DATE, i);

				//勤怠情報（月日・曜日）
				msg.append(ym.substring(4, 6));
				msg.append("/");
				msg.append(String.format("%02d", i));
				msg.append("(");
				msg.append(sdf.format(cal.getTime()));
				msg.append(") ");

				//勤怠情報（時刻）
				if (arrival_t != null && clock_out_t != null) {
					//出勤レコード
					msg.append(CommonUtils.toHMm(arrival_t.getAttendanceTime()));
					msg.append("～");
					//退勤レコード
					msg.append(CommonUtils.toHMm(clock_out_t.getAttendanceTime()));
					if ((arrival_t.getEditFlg().equals("1")) || (clock_out_t.getEditFlg().equals("1"))) {
						editFlg = true;
					}
				} else if (arrival_t != null) {//出勤レコードのみ
					msg.append(CommonUtils.toHMm(arrival_t.getAttendanceTime()));
					msg.append("～");
					if (arrival_t.getEditFlg().equals("1")) {
						editFlg = true;
					}
				} else if (clock_out_t != null) {//退勤レコードのみ
					msg.append("～");
					msg.append(CommonUtils.toHMm(clock_out_t.getAttendanceTime()));
					if (clock_out_t.getEditFlg().equals("1")) {
						editFlg = true;
					}
				} else {
					msg.append("---");
				}
				if (editFlg) {
					msg.append(" 修正");
				}
				if (i != lastDayOfMonth) {
					msg.append(System.getProperty("line.separator"));
				}
			}

			//logger.debug("msg {}", msg);

			//LINEステータス更新
			lineStatus.setMenuCd("empty");
			lineStatus.setActionName(null);
			lineStatus.setContents(null);
			lineStatus.setRequestTime(new Date());
			logger.debug("saveLineSutatus() {}", lineStatus);
			tLineStatusDao.save(lineStatus);

			//メッセージの送信
			LineAPIService.repryMessage(replyToken, msg.toString());
		} else {

			//部下情報検索
			List<MUser> junior;
			if (user.getAuthCd().equals("02")) {
				junior = mUserDao.getByManagerId(user.getUserId());
			} else {
				junior = mUserDao.getAll();
			}

			//テンプレートメッセージ作成
			List<String> msgList = new ArrayList<String>();
			msgList.add(user.getUserId().toString() + " " + "自分");
			if (junior != null) {
				for (MUser mu : junior) {
					if (!user.getUserId().equals(mu.getUserId())) {
						msgList.add(mu.getUserId().toString() + " " + mu.getName());
					}
				}
			}

			//logger.debug("msgList {}", msgList);

			//LINEステータス更新
			lineStatus.setActionName(ACTION_LIST_USER_SELECTION);
			lineStatus.setContents(text);
			lineStatus.setRequestTime(new Date());
			logger.debug("saveLineSutatus() {}", lineStatus);
			tLineStatusDao.save(lineStatus);

			//テンプレートメッセージ送信
			LineAPIService.pushButtons(lineId, AppMesssageSource.getMessage("line.selectUserByList"), msgList);
		}
	}
}