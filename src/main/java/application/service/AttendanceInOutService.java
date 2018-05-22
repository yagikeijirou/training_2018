package application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.dao.MUserDao;
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

	/**出勤ボタンが押された場合の処理*/
	public void putArrivalNow(String lineId, String replyToken) {

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


		TAttendance tAttendance = new TAttendance();

		/*勤怠情報からユーザIDを取得*/
		//tA.getUserId();

		/*勤怠情報から勤怠時刻を取得*/
		//tA.getAttendanceTime();


		/*ユーザマスタのユーザIDと勤怠情報のユーザIDが一致するレコードを検索*/
		if (tAttendance.getUserId() == mUser.getUserId()) {

			/*勤怠情報の勤怠時刻に既に出勤打刻が登録されていないか確認*/
			if ((tAttendance.getAttendanceTime() == null) && (tAttendance.getAttendanceCd() == "01")) {

				/*リクエスト時刻を出勤時刻として登録する*/
				tAttendance.setAttendanceTime(tLineStatus.getRequestTime()) ;

				/*メッセージをLINEアプリ上で表示させて処理を終了する*/
				String msg = AppMesssageSource.getMessage("line.arrival");
				LineAPIService.repryMessage(replyToken, msg);

				System.out.println("登録できました");

			} else {
				/*出勤時刻登録しないでエラーメッセージ表示する*/
				String msg = AppMesssageSource.getMessage("line.api.err.savedArrival");
				LineAPIService.repryMessage(replyToken, msg);

				System.out.println("登録できませんでした");
			}
		}
	}


	/**退勤ボタンが押された場合の処理*/
	public void putClockOutNow(String lineId, String replyToken) {

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


		TAttendance tAttendance = new TAttendance();

		/*勤怠情報からユーザIDを取得*/
		//tA.getUserId();

		/*勤怠情報から勤怠時刻を取得*/
		//tA.getAttendanceTime();


		/*ユーザマスタのユーザIDと勤怠情報のユーザIDが一致するレコードを検索*/
		if (tAttendance.getUserId() == mUser.getUserId()) {

			/*勤怠情報の勤怠時刻に既に出勤打刻が登録されていないか確認*/
			if ((tAttendance.getAttendanceTime() == null) && (tAttendance.getAttendanceCd() == "02")) {

				/*リクエスト時刻を退勤時刻として登録する*/
				tAttendance.setAttendanceTime(tLineStatus.getRequestTime()) ;

				/*メッセージをLINEアプリ上で表示させて処理を終了する*/
				String msg = AppMesssageSource.getMessage("line.clockOut");
				LineAPIService.repryMessage(replyToken, msg);

				System.out.println("登録できました");

			} else {
				/*退勤時刻登録しないでエラーメッセージ表示する*/
				String msg = AppMesssageSource.getMessage("line.api.err.savedClockOut");
				LineAPIService.repryMessage(replyToken, msg);

				System.out.println("登録できませんでした。");
			}
		}
	}
}