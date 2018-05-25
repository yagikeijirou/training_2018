package application.service;

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
import application.emuns.AttenanceCd;
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
	 * @return 0以上の場合、アラートを出した人数。0未満の場合、終了コード。
	 */
	public int pushAlerts(Date beginTime, Date endTime) {

		logger.debug("pushAlerts()");

		/** beginTimeをCalendar型で持つための変数 **/
		Calendar beginCl = Calendar.getInstance();
		beginCl.setTime(beginTime);

		/** アラートモード（1：出勤打刻防止、2：退勤打刻防止、0：アラート不要 **/
		int alertMode = 0;
		/** アラートを出した人の数 **/
		int alertCounter = 0;
		/** 設定マスタのエンティティ **/
		MSetting ms = mSettingDao.get();
		/** ユーザマスタの全ユーザのエンティティリスト **/
		List<MUser> muList = mUserDao.getAll();
		/** アラート出力用一時勤怠情報 **/
		TAttendance tmpTa = new TAttendance();

		// アラートフラグがアラートなしならここで終了、アラートありなら続行
		if (ms.getAlertFlag().equals("0")) {
			logger.debug("打刻漏れ防止アラートが設定されていません。");
			return 0;
		}

		// 本日が営業日じゃないならここで終了、営業日なら続行
		if (!getBusinessDay(ms).contains(beginCl.get(Calendar.DAY_OF_WEEK))) {
			logger.debug("本日は営業日ではありません。");
			return 0;
		}

		// 今がアラートを出す時間ではないならここで終了、出す時間なら、出退どちらのアラートかを保持し続行
		if ((alertMode = alertModeChecker(beginTime, endTime)) == 0) {
			logger.debug("打刻漏れ防止アラートを出す時刻ではありません。");
			return 0;
		}

		// ユーザマスタにある全ユーザのリストがmuList、その1つをeachUserに入れてそれぞれ処理
		for (MUser eachUser : muList) {
			// LINE識別子を持っていないユーザの場合、次へ進む
			if (eachUser.getLineId() == null) {
				continue;
			}

			// 出勤アラートを出すとき
			if (alertMode == 1) {
				tmpTa = tAttendanceDao.getByPk(eachUser.getUserId(), AttenanceCd.getByName("出勤").getCode(),
						CommonUtils.toYyyyMmDd(beginTime));
				if (tmpTa == null && eachUser.getLineId() != null) {
					logger.debug(eachUser.getLineId());
					LineAPIService.pushMessage(eachUser.getLineId(),
							AppMesssageSource.getMessage("line.alertNotFoundAttendance", "出勤"));
					logger.debug(AppMesssageSource.getMessage("line.alertNotFoundAttendance", "出勤"));
					logger.debug("push to lineId: " + eachUser.getLineId());

					// 送信カウンタを1増やす
					alertCounter++;
				}
			}

			// 退勤アラートを出すとき
			else if (alertMode == 2) {
				tmpTa = tAttendanceDao.getByPk(eachUser.getUserId(), AttenanceCd.getByName("退勤").getCode(),
						CommonUtils.toYyyyMmDd(beginTime));
				if (tmpTa == null && eachUser.getLineId() != null) {
					logger.debug(eachUser.getLineId());
					LineAPIService.pushMessage(eachUser.getLineId(),
							AppMesssageSource.getMessage("line.alertNotFoundAttendance", "退勤"));
					logger.debug(AppMesssageSource.getMessage("line.alertNotFoundAttendance", "退勤"));
					logger.debug("push to lineId: " + eachUser.getLineId());
					// 送信カウンタを1増やす
					alertCounter++;
				}
			}

		}

		logger.debug("打刻漏れ防止アラートを " + alertCounter + "人 に送信しました。");

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