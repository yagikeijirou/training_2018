package application.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.dao.MSettingDao;
import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.dao.TLineStatusDao;
import application.entity.MSetting;
import application.entity.MUser;
import application.entity.TAttendance;
import application.entity.TLineStatus;
import application.utils.CommonUtils;

/**
 * 勤怠の「アラート」操作サービス。
 * @author 菅一生
 */
@Service
@Transactional
public class AttendanceAlertService extends AbstractAttendanceService {
	/** このクラスのロガー。 */
	private static final Logger logger = LoggerFactory.getLogger(AttendanceAlertService.class);

	/** 設定マスタ用DAO **/
	@Autowired
	private MSettingDao mSettingDao;

	/** ユーザマスタ用DAO **/
	@Autowired
	private MUserDao mUserDao;

	/** 勤怠情報用DAO **/
	@Autowired
	private TAttendanceDao tAttendanceDao;

	/////

	@Autowired
	MUserDao muserDao;

	@Autowired
	TAttendanceDao tattendanceDao;

	/** LINEステータス情報DAO。 */
	@Autowired
	TLineStatusDao tLineStatusDao;

	/////

	/**
	 * ある時間範囲に、打刻漏れ防止アラートを出すよう設定された時刻が含まれていた場合、アラートを出すメソッド。
	 *
	 * @param beginTime ある時間範囲の開始時刻
	 * @param endTime ある時間範囲の終了時刻
	 * @return アラートを出した人数
	 */
	public int pushAlerts(Date beginTime, Date endTime) {
		// テスト用、最後に削除すること。
		tests();
		//hodaka();
		kashiwara();

		logger.debug("pushAlerts()");

		/** アラートモード（1：出勤打刻防止、2：退勤打刻防止、0：アラート不要）**/
		int alertMode = 0;
		/** アラートを出した人の数 **/
		int alertCounter = 0;
		/** ユーザマスタの全ユーザのエンティティリスト **/
		List<MUser> mu = mUserDao.getAll();
		/** アラート出力用一時勤怠情報 **/
		TAttendance tmpTa = null;

		// フラグが0ならここで終了、1なら続行
		if (mSettingDao.get().getAlertFlag().equals("0")) {
			return 0;
		}

		// 今がアラートを出すときではないならここで終了、出すときなら続行
		if ((alertMode = alertModeChecker(beginTime, endTime)) == 0) {
			return 0;
		}

		// ユーザマスタにある全ユーザのリストがmu、その1つをeachUserに入れてそれぞれ処理
		for (MUser eachUser : mu) {
			tmpTa = tAttendanceDao.getLatestOneByUserId(eachUser.getUserId());

			// 指定ユーザの勤怠情報が未登録の時、次のユーザへ進む
			if (tmpTa == null) {
				continue;
			}

			if (alertMode == 1 && (tmpTa.getAttendanceCd().equals("02"))) {
				// 最新勤怠情報が退勤 -> 出勤漏れ -> 出勤打刻漏れ防止アラート
				//LineAPIService.pushMessage(eachUser.getLineId(), AppMesssageSource.getMessage("line.alertNotFoundAttendance", "出勤"));
				System.out.println("★★★★★★★★★★★★★★★★★★★★★");
				System.out.println(AppMesssageSource.getMessage("line.alertNotFoundAttendance", "出勤"));
				System.out.println("lineId: " + eachUser.getLineId());
				System.out.println("★★★★★★★★★★★★★★★★★★★★★");
			} else if (alertMode == 2 && (tmpTa.getAttendanceCd().equals("01"))) {
				// 最新勤怠情報が出勤 -> 退勤漏れ -> 退勤打刻漏れ防止アラート
				//LineAPIService.pushMessage(eachUser.getLineId(), AppMesssageSource.getMessage("line.alertNotFoundAttendance", "退勤"));
				System.out.println("★★★★★★★★★★★★★★★★★★★★★");
				System.out.println(AppMesssageSource.getMessage("line.alertNotFoundAttendance", "退勤"));
				System.out.println("lineId: " + eachUser.getLineId());
				System.out.println("★★★★★★★★★★★★★★★★★★★★★");
			}

			// 送信カウンタを1増やす
			alertCounter++;

		}

		return alertCounter;
	}

	/**
	 * アラートモードチェックメソッド
	 * <pre>
	 * 現在時刻を確認し、出勤打刻漏れ防止アラートを出すのか、
	 * 退勤打刻漏れ防止アラートを出すのか、アラートは不要なのかをチェックする。
	 * </pre>
	 * @param beginTime
	 * 		/api/alertsが呼ばれたシステム時刻の6分前
	 * @param endTime
	 * 		/api/alertsが呼ばれたシステム時刻の6分後
	 * @return int アラートモード（1：出勤打刻防止、2：退勤打刻防止、0：アラート不要）
	 */
	private int alertModeChecker(Date beginTime, Date endTime) {
		logger.debug("alertModeChecker()");

		MSetting mSetting = mSettingDao.get();
		String openTime = mSetting.getAlertOpenTime();
		String openMinutes = mSetting.getAlertOpenMinutes();
		String closeTime = mSetting.getAlertCloseTime();
		String closeMinutes = mSetting.getAlertCloseMinutes();
		Date openDate = CommonUtils.parseDate(CommonUtils.toYyyyMmDd() + openTime + openMinutes, "yyyyMMddHHmm");
		Date closeDate = CommonUtils.parseDate(CommonUtils.toYyyyMmDd() + closeTime + closeMinutes, "yyyyMMddHHmm");

		// 日付確認用
		/**
		System.out.println("- cron日付 -----------------");
		System.out.println(beginTime);
		System.out.println(endTime);
		System.out.println("----------------------------");
		System.out.println("- DB日付 -------------------");
		System.out.println(CommonUtils.parseDate(CommonUtils.toYyyyMmDd() + openTime + openMinutes, "yyyyMMddHHmm"));
		System.out.println(CommonUtils.parseDate(CommonUtils.toYyyyMmDd() + closeTime + closeMinutes, "yyyyMMddHHmm"));
		System.out.println("----------------------------");
		 **/

		// cron時刻範囲が出勤打刻漏れ防止アラート設定時刻を含むとき、return 1
		if (openDate.after(beginTime) && openDate.before(endTime)) {
			return 1;
		}

		// cron時刻範囲が出勤打刻漏れ防止アラート設定時刻を含むとき、return 2
		if (closeDate.after(beginTime) && closeDate.before(endTime)) {
			return 2;
		}

		// cron時刻範囲が打刻漏れ防止アラート設定時刻外のとき、return 0
		return 0;
	}

	/**
	 * 菅テスト用メソッド
	 * @author 菅一生
	 */
	private void tests() {
		logger.debug("tests()");
		int userId = 201618;
		String lineId = "U242fce147b05106f3d5f31e7b82c7747";
		String attendanceCd = "02";
		String attendanceDay = "20180330";
		String attendanceMonth = "201803";
		//TAttendance ta = tAttendanceDao.getByPk(userId, attendanceCd, attendanceDay);
		//List<TAttendance> ta = tAttendanceDao.getByUserIdAndAttendanceMonth(userId, attendanceMonth);
		List<TAttendance> ta = tAttendanceDao.getByAttendanceMonth(attendanceMonth);
		List<MUser> mu = mUserDao.getAll();

		/**
		System.out.println("--ANSWER BELOW----------------------------------------");
		//System.out.println(ta);

		for (TAttendance result : ta) {
			System.out.println(result);
		}

		System.out.println("--ANSWER ABOVE----------------------------------------");
		**/

		/**
		System.out.println("--ANSWER BELOW----------------------------------------");

		for (MUser result : mu) {
			System.out.println(result.getUserId());
		}

		System.out.println("--ANSWER ABOVE----------------------------------------");
		**/

		/**
		System.out.println("--ANSWER BELOW----------------------------------------");

		System.out.println(tAttendanceDao.getLatestOneByUserId(201618));

		System.out.println("--ANSWER ABOVE----------------------------------------");
		**/
	}

	private void hodaka() {
		logger.debug("hodaka()");
		String lineId = "U242fce147b05106f3d5f31e7b82c7747";
		String replyToken = "";
		TLineStatus lineStatus = null;
		String text = "2018/3";

		//入力値チェック(yyyy/mmかどうか、テンプレートメッセージかどうか)
		//ユーザマスタ検索
		MUser user = muserDao.getByLineId(lineId);
		//		System.out.println(user.getName());
		//		System.out.println(user.getUserId());

		//修正フラグ
		boolean editFlg;

		//一般・上司・管理者チェック
		StringBuilder msg = new StringBuilder();
		if (user.getAuthCd().equals("01")
				|| (lineStatus != null && lineStatus.getActionName().equals(ACTION_LIST_USER_SELECTION))) {
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
			for (int i = 1; i <= lastDayOfMonth + 1; i++) {
				editFlg = false;

				arrival_t = tattendanceDao.getByPk(user.getUserId(), "01",
						CommonUtils.toYearMonth(text) + String.format("%02d", i));
				clock_out_t = tattendanceDao.getByPk(user.getUserId(), "02",
						CommonUtils.toYearMonth(text) + String.format("%02d", i));
				msg.append(text.split("/")[1]);
				msg.append("/");
				msg.append(String.format("%02d", i));
				msg.append("(");
				cal.set(Calendar.DATE, i);
				msg.append(sdf1.format(cal.getTime()));
				//曜日
				msg.append(") ");

				if (arrival_t != null && clock_out_t != null) {
					//出勤レコード
					if (arrival_t.getEditFlg().equals("1")) {
						editFlg = true;
					}
					msg.append(sdf2.format(arrival_t.getAttendanceTime()));
					msg.append("～");

					//退勤レコード
					if (clock_out_t.getEditFlg().equals("1")) {
						editFlg = true;
					}
					msg.append(sdf2.format(clock_out_t.getAttendanceTime()));
				} else if (arrival_t != null) {//出勤レコードのみ
					if (arrival_t.getEditFlg().equals("1")) {
						editFlg = true;
					}
					msg.append(sdf2.format(arrival_t.getAttendanceTime()));
					msg.append("～");
				} else if (clock_out_t != null) {//退勤レコードのみ
					msg.append("～");
					if (clock_out_t.getEditFlg().equals("1")) {
						editFlg = true;
					}
					msg.append(sdf2.format(clock_out_t.getAttendanceTime()));
				} else {

					msg.append("---");
				}
				if (editFlg) {
					msg.append(" 修正");
				}
				msg.append(System.getProperty("line.separator"));
			}
			System.out.println(msg.toString());

			//LINEステータス更新
			//			lineStatus = getLineSutatus(lineId);
			//			lineStatus.setMenuCd("empty");
			//			lineStatus.setActionName(null);
			//			lineStatus.setContents(text);
			//			tLineStatusDao.save(lineStatus);

			//メッセージの送信
			//			LineAPIService.repryMessage(replyToken, msg.toString());
		} else {
			//上司・管理者の場合
			//部下情報検索
			List<MUser> junior = muserDao.getByManagerId(new Integer(200911));

			//テンプレートメッセージ作成
			List<String> msgList = new ArrayList<String>();
			msgList.add("自分");

			if (msgList != null) {
				for (MUser mu : junior) {
					msgList.add(mu.getName());
				}
			}

			for (String str : msgList) {
				System.out.println("-----------------------------");
				System.out.println(str);
				System.out.println("-----------------------------");
			}

			//LINEステータス更新
			//			lineStatus = getLineSutatus(lineId);
			//			lineStatus.setMenuCd("03");
			//			lineStatus.setActionName(ACTION_LIST_USER_SELECTION);
			//			lineStatus.setContents(text);
			//			tLineStatusDao.save(lineStatus);

			//テンプレートメッセージ送信
			//			LineAPIService.pushButtons(lineId, AppMesssageSource.getMessage("line.selectUserByList"), msgList);

		}

	}

	private void kashiwara() {
		logger.debug("kashiwara()");


		String lineId = "U242fce147b05106f3d5f31e7b82c7747";
		String replyToken = "";

		/*取得したLINE識別子からユーザを取得*/
		MUser mUser = mUserDao.getByLineId(lineId);

		/*取得したユーザのユーザIDを取得*/
		//mUser.getUserId();

		/*取得したLINE識別子からLINEステータスを取得*/
		TLineStatus tLineStatus = tLineStatusDao.getByPk(lineId);

		/*LINEステータスからリクエスト時刻を取得*/
		//linestatus.getRequestTime();

		/*リクエスト時刻をString型に変換*/
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String reqtime = sdf1.format(tLineStatus.getRequestTime());

		/*取得したユーザID,勤怠区分コード,リクエスト時刻から勤怠情報エンティティを取得*/
		TAttendance t_attendance = tAttendanceDao.getByPk(mUser.getUserId(), "01", reqtime);

		System.out.println("年月日は"+t_attendance);

		/*勤怠情報の勤怠時刻に既に出勤打刻が登録されていないか確認*/
		if (t_attendance == null) {

			/*勤怠情報のユーザIDに登録*/
			t_attendance.setUserId(mUser.getUserId());

			/*勤怠情報の出勤日に登録*/
			t_attendance.setAttendanceDay(reqtime);

			/*リクエスト時刻を出勤時刻として登録する*/
			t_attendance.setAttendanceTime(tLineStatus.getRequestTime());

			/*勤怠情報の勤怠区分コードに登録*/
			t_attendance.setAttendanceCd("01");

			/*勤怠情報の修正フラグに0を登録*/
			//t_attendance.setEditFlg("0");


			/**勤怠情報エンティティをDBに新規登録*/
			tAttendanceDao.insert(t_attendance);

			/*メッセージをLINEアプリ上で表示させて処理を終了する*/
			String msg = AppMesssageSource.getMessage("line.arrival");
			//LineAPIService.repryMessage(replyToken, msg);

			System.out.println("登録できました");
		} else {
			/*出勤時刻登録しないでエラーメッセージ表示する*/
			String msg = AppMesssageSource.getMessage("line.api.err.savedArrival");
			//LineAPIService.repryMessage(replyToken, msg);

			System.out.println("登録できませんでした");
		}
	}

}