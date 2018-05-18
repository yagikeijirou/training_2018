package application.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MSettingDao;
import application.dao.MUserDao;
import application.utils.CommonUtils;

/**
 * 勤怠の「アラート」操作サービス。
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



	public int pushAlerts(Date beginTime, Date endTime) {
		logger.debug("pushAlerts()");
		int alertMode = 0;
		// beginTimeとendTimeを受け取って、アラートを出す人数を返す

		// TODO: フラグが0ならここで終了、1なら続行
		if (mSettingDao.get().getAlertFlag().equals("0")) {
			return 0;
		}

		// TODO: 今がアラートを出すときではないならここで終了、出すときなら続行
		if ((alertMode = alertModeChecker(beginTime, endTime)) == 0) {
			return 0;
		}

		// TODO: アラートを出し、出した人数をカウント


		return 0;
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

		final String openTime = mSettingDao.get().getAlertOpenTime();
		final String openMinutes = mSettingDao.get().getAlertOpenMinutes();
		final String closeTime = mSettingDao.get().getAlertCloseTime();
		final String closeMinutes = mSettingDao.get().getAlertCloseMinutes();
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

}
