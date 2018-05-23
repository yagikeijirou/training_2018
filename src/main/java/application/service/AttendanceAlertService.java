package application.service;

import java.text.SimpleDateFormat;
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
			System.out.println("打刻漏れ防止アラートが設定されていません。");
			return 0;
		}

		// 今がアラートを出すときではないならここで終了、出すときなら続行
		if ((alertMode = alertModeChecker(beginTime, endTime)) == 0) {
			System.out.println("打刻漏れ防止アラートを出す時刻ではありません。");
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



	private void kashiwara() {
		logger.debug("kashiwara()");


		String lineId = "U242fce147b05106f3d5f31e7b82c7747";
		String replyToken = "";

		/*取得したLINE識別子からユーザを取得*/
		//MUserDao mUserDao = new MUserDao();
		MUser mUser = mUserDao.getByLineId(lineId);

		/*取得したユーザのユーザIDを取得*/
		//mUser.getUserId();


		/*取得したLINE識別子からLINEステータスを取得*/
		//TLineStatusDao tLineStatusDao = new TLineStatusDao();
		TLineStatus tLineStatus = tLineStatusDao.getByPk(lineId);

		/*LINEステータスからリクエスト時刻を取得*/
		//linestatus.getRequestTime();


		/*リクエスト時刻をString型に変換*/
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		String reqtime = sdf2.format(tLineStatus.getRequestTime());

		/*取得したユーザID,勤怠区分コード,リクエスト時刻から勤怠情報エンティティを取得*/
		TAttendance t_attendance = tAttendanceDao.getByPk(mUser.getUserId(), "02", reqtime);

		System.out.println("年月日は"+t_attendance);

			/*勤怠情報の勤怠時刻に既に出勤打刻が登録されていないか確認*/
			if ((t_attendance == null) ) {

				/*リクエスト時刻を退勤時刻として登録する*/
				t_attendance.setAttendanceTime(tLineStatus.getRequestTime()) ;

				tAttendanceDao.insert(t_attendance);

				/*メッセージをLINEアプリ上で表示させて処理を終了する*/
				String msg = AppMesssageSource.getMessage("line.clockOut");
				//LineAPIService.repryMessage(replyToken, msg);

				System.out.println("登録できました");

			} else {
				/*退勤時刻登録しないでエラーメッセージ表示する*/
				String msg = AppMesssageSource.getMessage("line.api.err.savedClockOut");
				//LineAPIService.repryMessage(replyToken, msg);

				System.out.println("登録できませんでした");
			}
	}

}