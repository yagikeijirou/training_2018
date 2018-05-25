package application.service;

import java.text.SimpleDateFormat;

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
 * 勤怠情報「出勤・退勤」操作サービス。
 * @author 柏原亮太朗
 */
@Service
@Transactional
public class AttendanceInOutService extends AbstractAttendanceService {
	/** このクラスのロガー。 */
	private static final Logger logger = LoggerFactory.getLogger(AttendanceInOutService.class);

	@Autowired
	MUserDao mUserDao;

	@Autowired
	TLineStatusDao tLineStatusDao;

	@Autowired
	TAttendanceDao tAttendanceDao;

	/**出勤ボタンが押された場合の処理
	 *
	 * @param lineId
	 * @param replyToken
	 */
	public void putArrivalNow(String lineId, String replyToken) {
		logger.debug("putArrivalNow");

		/*取得したLINE識別子からユーザを取得*/
		MUser mUser = mUserDao.getByLineId(lineId);

		/*取得したユーザマスタからユーザIDを取得*/
		//mUser.getUserId();

		/*取得したLINE識別子からLINEステータスを取得*/
		TLineStatus tLineStatus = tLineStatusDao.getByPk(lineId);

		/*LINEステータスからリクエスト時刻を取得*/
		//linestatus.getRequestTime();

		/*リクエスト時刻をString型に変換*/
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String reqday = sdf.format(tLineStatus.getRequestTime());

		/*取得したユーザID,勤怠区分コード,リクエスト時刻から勤怠情報エンティティを取得*/
		TAttendance t_attendance = tAttendanceDao.getByPk(mUser.getUserId(), "01", reqday);

		//System.out.println("勤怠情報は"+tAttendanceDao.getByPk(mUser.getUserId(), "01", reqday));

		/*勤怠情報の勤怠時刻に既に出勤打刻が登録されていないか確認*/
		if (t_attendance == null) {

			t_attendance = new TAttendance();

			/*勤怠情報のユーザIDに登録*/
			t_attendance.setUserId(mUser.getUserId());

			/*勤怠情報の勤怠区分コードに登録*/
			t_attendance.setAttendanceCd("01");

			/*勤怠情報の出勤日に登録*/
			t_attendance.setAttendanceDay(reqday);

			/*リクエスト時刻を出勤時刻として登録する*/
			t_attendance.setAttendanceTime(tLineStatus.getRequestTime());

			/*勤怠情報の修正フラグに0を登録*/
			t_attendance.setEditFlg("0");

			/**勤怠情報エンティティをDBに新規登録*/
			tAttendanceDao.save(t_attendance);
			logger.debug("saveTAttendance",t_attendance);

			//System.out.println("処理後の勤怠情報は"+tAttendanceDao.getByPk(mUser.getUserId(), "01", reqday));

			if(tAttendanceDao.getByPk(mUser.getUserId(), "01", reqday) != null) {
			/*メッセージをLINEアプリ上で表示させて処理を終了する*/
			String msg = AppMesssageSource.getMessage("line.arrival");
			LineAPIService.repryMessage(replyToken, msg);

			//System.out.println(msg);
			}else {
				//System.out.println("登録が何らかの理由で失敗しました");
			}

		} else {
			/*出勤時刻登録しないでエラーメッセージ表示する*/
			String msg = AppMesssageSource.getMessage("line.api.err.savedArrival");
			LineAPIService.repryMessage(replyToken, msg);

			//System.out.println("処理後の勤怠情報は"+tAttendanceDao.getByPk(mUser.getUserId(), "01", reqday));
			//System.out.println(msg);
		}
		logger.debug("putArrivalNow_End");
	}

	/**退勤ボタンが押された場合の処理
	 *
	 * @param lineId
	 * @param replyToken
	 */
	public void putClockOutNow(String lineId, String replyToken) {
		logger.debug("putClockOutNow");

		/*取得したLINE識別子からユーザを取得*/
		MUser mUser = mUserDao.getByLineId(lineId);

		/*取得したユーザマスタからユーザIDを取得*/
		//mUser.getUserId();

		/*取得したLINE識別子からLINEステータスを取得*/
		TLineStatus tLineStatus = tLineStatusDao.getByPk(lineId);

		/*LINEステータスからリクエスト時刻を取得*/
		//linestatus.getRequestTime();

		/*リクエスト時刻をString型に変換*/
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String reqday = sdf.format(tLineStatus.getRequestTime());

		/*取得したユーザID,勤怠区分コード,リクエスト時刻から勤怠情報エンティティを取得*/
		TAttendance t_attendance = tAttendanceDao.getByPk(mUser.getUserId(), "02", reqday);

		//System.out.println("勤怠情報は"+tAttendanceDao.getByPk(mUser.getUserId(), "02", reqday));

		/*勤怠情報の勤怠時刻に既に出勤打刻が登録されていないか確認*/
		if ((t_attendance == null)) {

			t_attendance = new TAttendance();

			/*勤怠情報のユーザIDに登録*/
			t_attendance.setUserId(mUser.getUserId());

			/*勤怠情報の出勤日に登録*/
			t_attendance.setAttendanceDay(reqday);

			/*リクエスト時刻を出勤時刻として登録する*/
			t_attendance.setAttendanceTime(tLineStatus.getRequestTime());

			/*勤怠情報の勤怠区分コードに登録*/
			t_attendance.setAttendanceCd("02");

			/*勤怠情報の修正フラグに0を登録*/
			t_attendance.setEditFlg("0");

			/**勤怠情報エンティティをDBに新規登録*/
			tAttendanceDao.save(t_attendance);
			logger.debug("saveTAttendance",t_attendance);

			//System.out.println("処理後の勤怠情報は"+tAttendanceDao.getByPk(mUser.getUserId(), "02", reqday));

			if(tAttendanceDao.getByPk(mUser.getUserId(), "02", reqday) != null) {
			/*メッセージをLINEアプリ上で表示させて処理を終了する*/
			String msg = AppMesssageSource.getMessage("line.clockOut");
			LineAPIService.repryMessage(replyToken, msg);

			//System.out.println(msg);
			}else {
				//System.out.println("登録が何らかの理由で失敗しました");
			}

		} else {
			/*退勤時刻登録しないでエラーメッセージ表示する*/
			String msg = AppMesssageSource.getMessage("line.api.err.savedClockOut");
			LineAPIService.repryMessage(replyToken, msg);

			//System.out.println("処理後の勤怠情報は"+tAttendanceDao.getByPk(mUser.getUserId(), "02", reqday));
			//System.out.println(msg);
		}
		logger.debug("putClockOutNow_End");
	}
}
