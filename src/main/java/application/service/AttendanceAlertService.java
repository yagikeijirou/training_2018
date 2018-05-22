package application.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		hodaka();

		logger.debug("pushAlerts()");

		/** アラートモード（1：出勤打刻防止、2：退勤打刻防止、0：アラート不要）**/
		int alertMode = 0;

		/** アラートを出した人の数 **/
		int alertCounter = 0;

		// フラグが0ならここで終了、1なら続行
		if (mSettingDao.get().getAlertFlag().equals("0")) {
			return 0;
		}

		// 今がアラートを出すときではないならここで終了、出すときなら続行
		if ((alertMode = alertModeChecker(beginTime, endTime)) == 0) {
			return 0;
		}

		// TODO: アラートを出し、出した人数をカウント

		/**
		for (MUser result : mu) {
			System.out.println(result.getUserId());
		}
		**/

		while (true) {
			//if mode = 1
			//出勤プッシュメソッド
			//if mode = 2
			//退勤プッシュメソッド
			break;
		}

		System.err.println("Hello");
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
		return 1;
		// return 0; 上はテスト用、最終的に正しいのはこれ
	}

	/**
	 * 出勤プッシュメソッド
	 */

	/**
	 * 退勤プッシュメソッド
	 */

	/**
	 * 菅テスト用メソッド
	 * @author 菅一生
	 */
	private void tests() {
		logger.debug("tests()");
		int userId = 201618;
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

		System.out.println("--ANSWER BELOW----------------------------------------");

		System.out.println(tAttendanceDao.getLatestOneByUserId(201618));

		System.out.println("--ANSWER ABOVE----------------------------------------");

	}

	private void hodaka() {
		logger.debug("hodaka()");
		String lineId = "U242fce147b05106f3d5f31e7b82c7747";
		String replyToken = "";
		TLineStatus lineStatus = null;
		String text = "2018/03";

		//入力値チェック(yyyy/mmかどうか、テンプレートメッセージかどうか)
		//ユーザマスタ検索
		MUser user = muserDao.getByLineId(lineId);
		System.out.println(user.getName());
		System.out.println(user.getUserId());

		//修正フラグ
		boolean editFlg = false;

		//一般・上司・管理者チェック
		StringBuilder msg = new StringBuilder();
		//if (user.getAuthCd().equals("1")) {
		System.out.println("if_before");
		if (true) {
			//一般の場合
			//勤怠情報検索
			//List<TAttendance> tattendance = tattendanceDao.getByAttendanceMonth(user.getUserId(), text);

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
			lineStatus = getLineSutatus(lineId);
			lineStatus.setMenuCd("empty");
			lineStatus.setActionName(null);
			lineStatus.setContents(text);
			tLineStatusDao.save(lineStatus);

			//メッセージの送信
			//LineAPIService.repryMessage(replyToken, msg.toString());
		} else {
			//			//上司・管理者の場合
			//			//部下情報検索
			//			List<MUser> junior = muserDao.getByManagerId(user.getUserId());
			//
			//			//テンプレートメッセージ作成
			//			List<String> msgList = new ArrayList<String>();
			//			for (MUser mu : junior) {
			//				msgList.add(mu.getName());
			//			}
			//
			//			//テンプレートメッセージ送信
			//			LineAPIService.pushButtons(lineId, AppMesssageSource.getMessage("line.selectMenu"), msgList);
		}

	}

	/**
	 * 前回のLINE操作を取得する。
	 * @param lineId 送信元LINE識別子
	 * @return LINEステータス。存在しない場合、初期値をセットした新規行
	 */
	private TLineStatus getLineSutatus(String lineId) {
		TLineStatus res = tLineStatusDao.getByPk(lineId);
		if (res == null) {
			res = new TLineStatus();
			res.setLineId(lineId);
			MUser user = mUserDao.getByLineId(lineId);
			res.setUserId(user.getUserId());
		}
		return res;
	}

}