package application.service;

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
import application.entity.MSetting;
import application.entity.MUser;
import application.entity.TAttendance;
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

	/**
	 * 出退勤打刻漏れ防止アラート送信メソッド
	 * <pre>
	 * ある時間範囲に、打刻漏れ防止アラートを出すよう
	 * 設定された時刻が含まれていた場合、アラートを出すメソッド。
	 * </pre>
	 *
	 * @param beginTime ある時間範囲の開始時刻
	 * @param endTime ある時間範囲の終了時刻
	 * @return アラートを出した人数
	 */
	public int pushAlerts(Date beginTime, Date endTime) {
		logger.debug("pushAlerts()");

		/** アラートモード（1：出勤打刻防止、2：退勤打刻防止、0：アラート不要）**/
		int alertMode = 0;
		/** アラートを出した人の数 **/
		int alertCounter = 0;
		/** ユーザマスタの全ユーザのエンティティリスト **/
		List<MUser> mu = mUserDao.getAll();
		/** アラート出力用一時勤怠情報 **/
		TAttendance tmpTa = new TAttendance();

		// フラグが0ならここで終了、1なら続行
		if (mSettingDao.get().getAlertFlag().equals("0")) {
			System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
			System.out.println("打刻漏れ防止アラートが設定されていません。");
			System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
			return 0;
		}

		// 今がアラートを出すときではないならここで終了、出すときなら続行
		if ((alertMode = alertModeChecker(beginTime, endTime)) == 0) {
			System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
			System.out.println("打刻漏れ防止アラートを出す時刻ではありません。");
			System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
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
				System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
				System.out.println(AppMesssageSource.getMessage("line.alertNotFoundAttendance", "出勤"));
				System.out.println("lineId: " + eachUser.getLineId());
				System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
				// 送信カウンタを1増やす
				alertCounter++;
			} else if (alertMode == 2 && (tmpTa.getAttendanceCd().equals("01"))) {
				// 最新勤怠情報が出勤 -> 退勤漏れ -> 退勤打刻漏れ防止アラート
				//LineAPIService.pushMessage(eachUser.getLineId(), AppMesssageSource.getMessage("line.alertNotFoundAttendance", "退勤"));
				System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
				System.out.println(AppMesssageSource.getMessage("line.alertNotFoundAttendance", "退勤"));
				System.out.println("lineId: " + eachUser.getLineId());
				System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
				// 送信カウンタを1増やす
				alertCounter++;
			}

		}

		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");
		System.out.println("打刻漏れ防止アラートを " + alertCounter + "人 に送信しました。");
		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★");

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
		/*
		System.out.println("- cron日付 -----------------");
		System.out.println(beginTime);
		System.out.println(endTime);
		System.out.println("----------------------------");
		System.out.println("- DB日付 -------------------");
		System.out.println(CommonUtils.parseDate(CommonUtils.toYyyyMmDd() + openTime + openMinutes, "yyyyMMddHHmm"));
		System.out.println(CommonUtils.parseDate(CommonUtils.toYyyyMmDd() + closeTime + closeMinutes, "yyyyMMddHHmm"));
		System.out.println("----------------------------");
		*/

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