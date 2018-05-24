package application.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 * 2018/05/23-須永寛紀-log追記
 * 2018/05/23-須永寛紀-POSTされた情報をlineステータス情報に保存するコードに修正
 * 2018/05/24-須永寛紀-Date型にyyyyMMddkkmmを格納するコードを追記
 *
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

	//	/** 勤怠情報情報エンティティ。 */
	private TAttendance tAttendance = null;

	//1,LINEステータス情報を検索する。
	//2,メニューコードとアクション名が適切か確認する。
	/**
	 * リッチメニューから「修正」が選択された時の処理。
	 * @param replyToken リプライトークン
	 * @return
	 */
	public void startRewriting(String replyToken) {
		logger.debug("startRewriting()");
		//3,LINE APIを用いて「修正する月日(yyyy/mm/dd)を入力してください」というテキストを送信する。
		String msg = AppMesssageSource.getMessage("line.editMonthDate");
		System.out.println(msg);
		//LineAPIService.repryMessage(replyToken, msg);
		logger.debug("startRewriting()end");

	}

	/**
	 * 修正内容を勤怠情報に反映させる。
	 * @param lineId line識別子
	 * @param replyToken リプライトークン
	 * @param lineStatus lineステータス情報エンティティ
	 * @param text テキスト情報
	 */
	public void editAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
		logger.debug("editAction()");

		if(text == null) {
			//3,LINE APIを用いて「修正する月日(yyyy/mm/dd)を入力してください」というテキストを送信する。
			String msg = AppMesssageSource.getMessage("line.editMonthDate");
			//LineAPIService.repryMessage(replyToken, msg);
			return;

		}
		//4,LINEステータス情報を検索する。
		//5,メニューコードとアクション名が適切か確認する。
		if (lineStatus.getMenuCd().equals("03")
				&& lineStatus.getActionName().equals(ACTION_EDIT_DATE)) {
			//6,フォーマットが適切かどうか確認する。
			try {
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				System.out.println(df.format(df.parse(text)));
				if (text.equals(df.format(df.parse(text)))) {
					//7,POSTされた出勤日の情報をlineステータス情報の「contents」として格納する。
					lineStatus.setContents(text);
					lineStatus.setActionName(ACTION_EDIT_TYPE_SELECTION);
					tLineStatusDao.update(lineStatus);
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
			}
		}

		//9,LINEステータス情報を検索する。
		//10,アクション名が適切か確認する。
		if (lineStatus.getActionName().equals(ACTION_EDIT_TYPE_SELECTION)) {

			//11,フォーマットが適切かどうか確認する。
			if (text.equals("出勤")) {
				//12,POSTされた勤怠区分コードの情報をlineステータス情報の「ActionName」として格納する。
				lineStatus.setActionName(ACTION_EDIT_INPUT_TIME_ARRIVAL);
				tLineStatusDao.update(lineStatus);
				//13,LINE APIを用いて「新しい{0}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
				//LineAPIService.repryMessage(replyToken, msg);
			} else if (text.equals("退勤")) {
				//12,POSTされた勤怠区分コードの情報を勤怠情報エンティティ「attendanceCd」として格納する。
				lineStatus.setActionName(ACTION_EDIT_INPUT_TIME_CLOCKOUT);
				tLineStatusDao.update(lineStatus);
				//13,LINE APIを用いて「新しい{1}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
				//LineAPIService.repryMessage(replyToken, msg);
			} else {
				String msg = AppMesssageSource.getMessage("line.selectAttendanceCd");
				//LineAPIService.repryMessage(replyToken, msg);
			}

		}

		//14,LINEステータス情報を検索する。
		//15,メニューコードとアクション名が適切か確認する。
		if (lineStatus.getMenuCd().equals("03")
				&& (lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_ARRIVAL)
						|| lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_CLOCKOUT))) {

			//16,フォーマットが適切かどうか確認する。
			DateFormat df = new SimpleDateFormat("kk:mm");
			df.setLenient(false);
			try {
				if (text.equals(df.format(df.parse(text)))) {

					//17,POSTされた勤怠時刻の情報を勤怠情報エンティティの「attendanceTime」として格納する。
					if (lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_ARRIVAL)) {

						tAttendance = tAttendanceDao.getByPk(lineStatus.getUserId(), "01",
								lineStatus.getContents().replaceAll("/", ""));

					} else {
						tAttendance = tAttendanceDao.getByPk(lineStatus.getUserId(), "02",
								lineStatus.getContents().replaceAll("/", ""));
					}

					if (tAttendance == null) {
						String msg = "勤怠情報がありません";
						System.out.println(msg);
						//LineAPIService.repryMessage(replyToken, msg);
						return;
					}
					tAttendance.setEditFlg("1");
					String dateString = lineStatus.getContents().replaceAll("/", "")
							+ text.replaceAll(":", "");
					SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddkkmm");
					Date date = sdFormat.parse(dateString);
					tAttendance.setAttendanceTime(date);
					//出勤時刻よりも退勤時間が早くないか確認
					System.out.println("check1");
					if (tAttendanceDao.getByPk(lineStatus.getUserId(), "01",
							lineStatus.getContents().replaceAll("/", ""))
							.getAttendanceTime()
							.before(tAttendanceDao.getByPk(lineStatus.getUserId(), "02",
									lineStatus.getContents().replaceAll("/", ""))
									.getAttendanceTime())) {
						tAttendanceDao.update(tAttendance);
						//19,更新が完了したらLINE APIを用いて「{0} {1}を保存しました」というテキストを送信する。
						String msg = AppMesssageSource.getMessage("line.saveAttendance");
						//LineAPIService.repryMessage(replyToken, msg);
					} else {
						String msg = AppMesssageSource.getMessage("line.newAttendanceInput");

						//LineAPIService.repryMessage(replyToken, msg);
					}

				} else {
					//13,LINE APIを用いて「新しい{1}時刻(hhmm)を入力してください	」というテキストを送信する。
					String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
					System.out.println(msg);
					//LineAPIService.repryMessage(replyToken, msg);
				}
			} catch (ParseException e) {

				//13,LINE APIを用いて「新しい{1}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
				//LineAPIService.repryMessage(replyToken, msg);
			}

		}
		logger.debug("editAction()end");
	}

}
