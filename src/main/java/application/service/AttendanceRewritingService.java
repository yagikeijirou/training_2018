package application.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.dao.TAttendanceDao;
import application.dao.TLineStatusDao;
import application.entity.TAttendance;
import application.entity.TLineStatus;

/**
 * 勤怠情報「修正」操作サービス。
 * @author 須永寛紀
 *
 *  *         <pre>
 * [更新履歴]
 * 更新日付   - 更新者       - 更新内容
 * 2018/05/21-須永寛紀-新規コーディング
 * 2018/05/22-須永寛紀-javadoc追記
 *         </pre>
 */
@Service
@Transactional
public class AttendanceRewritingService extends AbstractAttendanceService {
	/** このクラスのロガー。 */
	private static final Logger logger = LoggerFactory.getLogger(AttendanceRewritingService.class);

	/** LINEステータス情報DAO。 */
	@Autowired
	private TLineStatusDao tLineStatusDao;

	/** 勤怠情報DAO。 */
	@Autowired
	private TAttendanceDao tAttendanceDao;



	/** 勤怠情報情報エンティティ。 */
	@Autowired
	private TAttendance tAttendance;

	//1,LINEステータス情報を検索する。
	//2,メニューコードとアクション名が適切か確認する。
	/**
	 * リッチメニューから「修正」が選択された時の処理。
	 * @param replyToken リプライトークン
	 */
	public void startRewriting(String replyToken) {
		logger.info("開始");
		//3,LINE APIを用いて「修正する月日(yyyymmdd)を入力してください」というテキストを送信する。
		String msg = AppMesssageSource.getMessage("line.editMonthDate");
		//LineAPIService.repryMessage(replyToken, msg);
		logger.info("終了");
	}

	/**
	 * 修正内容を勤怠情報に反映させる。
	 * @param lineId line識別子
	 * @param replyToken リプライトークン
	 * @param lineStatus lineステータス情報エンティティ
	 * @param text テキスト情報
	 */
	public void editAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
		logger.info("開始");
		//4,LINEステータス情報を検索する。
		//5,メニューコードとアクション名が適切か確認する。
		if (lineStatus.getMenuCd().equals("03")
				&& lineStatus.getActionName().equals("editDate")) {
			logger.info("開始");
			//6,フォーマットが適切かどうか確認する。
			try {
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				if (text.equals(df.format(df.parse(text)))) {
					//7,POSTされた出勤日の情報を勤怠情報エンティティの「attendanceDay」として格納する。
					tAttendance.setAttendanceDay(text);
					//8,LINE APIのボタンテンプレートを用いて、「勤怠区分を選択してください」という質問のテキストを送信する。
					//選択肢は「出勤」か「退勤」とする。
					String msg = AppMesssageSource.getMessage("line.selectAttendanceCd");
					//LineAPIService.repryMessage(replyToken, msg);
				} else {
					String msg = AppMesssageSource.getMessage("line.editMonthDate");
					//LineAPIService.repryMessage(replyToken, msg);
				}

			} catch (ParseException e) {
				String msg = AppMesssageSource.getMessage("line.editMonthDate");
				//LineAPIService.repryMessage(replyToken, msg);
			}logger.info("終了");
		}

		//9,LINEステータス情報を検索する。
		//10,メニューコードとアクション名が適切か確認する。
		if (lineStatus.getMenuCd().equals("03")
				&& lineStatus.getActionName().equals("editTypeSelection")) {
			logger.info("開始");

			//11,フォーマットが適切かどうか確認する。
			if (text.equals("出勤")) {
				//12,POSTされた勤怠区分コードの情報を勤怠情報エンティティ「attendanceCd」として格納する。
				tAttendance.setAttendanceCd("0");
				//13,LINE APIを用いて「新しい{0}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");											//{0}ってなんだ？
				//LineAPIService.repryMessage(replyToken, msg);
			} else if (text.equals("退勤")) {
				//12,POSTされた勤怠区分コードの情報を勤怠情報エンティティ「attendanceCd」として格納する。
				tAttendance.setAttendanceCd("1");
				//13,LINE APIを用いて「新しい{1}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
				//LineAPIService.repryMessage(replyToken, msg);
			} else {
				String msg = AppMesssageSource.getMessage("line.selectAttendanceCd");
				//LineAPIService.repryMessage(replyToken, msg);
			}
			logger.info("終了");
		}

		//14,LINEステータス情報を検索する。
		//15,メニューコードとアクション名が適切か確認する。
		if (lineStatus.getMenuCd().equals("03")
				&& lineStatus.getActionName().equals("editInputTimeArrival")
				|| lineStatus.getActionName().equals("editInputTimeClockout")) {
			logger.info("開始");

			//16,フォーマットが適切かどうか確認する。
			DateFormat df = new SimpleDateFormat("kkmm");
			df.setLenient(false);
			try {
				if (text.equals(df.format(df.parse(text)))) {
					//17,POSTされた勤怠時刻の情報を勤怠情報エンティティの「attendanceTime」として格納する。
					tAttendance.setAttendanceTime(df.parse(text));
					//修正フラグを勤怠情報エンティティに格納する。
					tAttendance.setEditFlg("1");
					//18,勤怠情報を更新する。
					tAttendanceDao.update(tAttendanceDao.getByPk(tAttendance.getUserId(), tAttendance.getAttendanceCd(),
							tAttendance.getAttendanceDay()));
					//19,更新が完了したらLINE APIを用いて「{0} {1}を保存しました」というテキストを送信する。
					String msg = AppMesssageSource.getMessage("line.saveAttendance");
					//LineAPIService.repryMessage(replyToken, msg);
				} else {
					//13,LINE APIを用いて「新しい{1}時刻(hhmm)を入力してください	」というテキストを送信する。
					String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
					//LineAPIService.repryMessage(replyToken, msg);
				}
			} catch (ParseException e) {
				//13,LINE APIを用いて「新しい{1}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
				//LineAPIService.repryMessage(replyToken, msg);
			}logger.info("終了");
		}
		logger.info("終了");
	}

}
