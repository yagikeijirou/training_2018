package application.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import application.utils.CommonUtils;

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
 * 2018/05/25-須永寛紀-CommonUtilを使ったコードに変更
 * 2018/05/25-須永寛紀-lineStatus.getActionName()がnullたっだ場合に対応
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
		//3,LINE APIを用いて「修正する月日(mmdd)を入力してください」というテキストを送信する。
		String msg = AppMesssageSource.getMessage("line.editMonthDate");
		LineAPIService.repryMessage(replyToken, msg);
		logger.debug(msg);

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
		//出退勤を区別するフィールド。"出勤"か"退勤"が入る。
		String attendanceCd = null;
		logger.debug(lineStatus.getActionName());
		LineAPIService.repryMessage(replyToken, "test");
		if (text == null) {
			String msg = AppMesssageSource.getMessage("word.noneInput");
			LineAPIService.repryMessage(replyToken, msg);
			logger.debug(msg);
			logger.debug("editAction()textIsNull");
			return;
		}

		if (lineStatus.getActionName() == null) {
			lineStatus.setActionName(ACTION_EDIT_DATE);
			tLineStatusDao.update(lineStatus);
			logger.debug("editAction()ChangeActionName");
		}

		logger.debug(lineStatus.getActionName());

		//4,LINEステータス情報を検索する。
		//5,メニューコードとアクション名が適切か確認する。
		if (lineStatus.getActionName().equals(ACTION_EDIT_DATE)) {
			logger.debug("editAction()editDate");
			String MMdd = CommonUtils.toMonthDate(text);
			if (MMdd == null) {
				String msg = AppMesssageSource.getMessage("line.editMonthDate");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
				logger.debug("editAction()editDateFormatErr");
				return;
			}
			String YyyyMmDd = CommonUtils.toYyyyMmDdByMmDd(MMdd);

			lineStatus.setContents(YyyyMmDd);
			lineStatus.setActionName(ACTION_EDIT_TYPE_SELECTION);
			tLineStatusDao.update(lineStatus);
			//8,LINE APIのボタンテンプレートを用いて、「勤怠区分を選択してください」という質問のテキストを送信する。
			//選択肢は「出勤」か「退勤」とする。

			//テンプレートメッセージ作成
			List<String> msglist = new ArrayList<>(Arrays.asList("出勤", "退勤"));
			LineAPIService.pushButtons(lineId, "line.selectAttendanceCd", msglist);
			logger.debug("editAction()editDateEnd");

		}

		//9,LINEステータス情報を検索する。
		//10,アクション名が適切か確認する。
		else if (lineStatus.getActionName().equals(ACTION_EDIT_TYPE_SELECTION)) {
			logger.debug("editAction()editTypeSelection");
			//11,フォーマットが適切かどうか確認する。
			if (text.equals("出勤")) {
				attendanceCd = "出勤";
				//12,POSTされた勤怠区分コードの情報をlineステータス情報の「ActionName」として格納する。
				lineStatus.setActionName(ACTION_EDIT_INPUT_TIME_ARRIVAL);
				tLineStatusDao.update(lineStatus);
				//13,LINE APIを用いて「新しい{0}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput",attendanceCd);
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
				logger.debug("editAction()editTypeSelectionAllival");
			} else if (text.equals("退勤")) {
				attendanceCd = "退勤";
				//12,POSTされた勤怠区分コードの情報を勤怠情報エンティティ「attendanceCd」として格納する。
				lineStatus.setActionName(ACTION_EDIT_INPUT_TIME_CLOCKOUT);
				tLineStatusDao.update(lineStatus);
				//13,LINE APIを用いて「新しい{1}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput",attendanceCd);
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
				logger.debug("editAction()editTypeSelectionClockOut");
			} else {
				String msg = AppMesssageSource.getMessage("line.selectAttendanceCd");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
				logger.debug("editAction()editTypeSelectionFormatErr");
				return;
			}
			logger.debug("editAction()editTypeSelectionEnd");
		}

		//14,LINEステータス情報を検索する。
		//15,メニューコードとアクション名が適切か確認する。
		else if (lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_ARRIVAL)
				|| lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_CLOCKOUT)) {


			logger.debug("editAction()editTime");
			String HourMinute = CommonUtils.toHourMinute(text);
			if (HourMinute == null) {
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput","勤怠");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
				logger.debug("editAction()editTimeFormatErr");
				return;
			}


			String YyyyMmDd = lineStatus.getContents();
			Date yyyyMMddHHmm = CommonUtils.parseDate(YyyyMmDd + HourMinute, "yyyyMMddHHmm");

			//17,POSTされた勤怠時刻の情報を勤怠情報エンティティの「attendanceTime」として格納する。
			if (lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_ARRIVAL)) {
				attendanceCd = "出勤";
				tAttendance = tAttendanceDao.getByPk(lineStatus.getUserId(), "01",
						YyyyMmDd);
				logger.debug("editAction()editInputTimeArrival");
			} else {
				attendanceCd = "退勤";
				tAttendance = tAttendanceDao.getByPk(lineStatus.getUserId(), "02",
						YyyyMmDd);
				logger.debug("editAction()editInputTimeClockout");
			}

			if (tAttendance == null) {
				String msg = AppMesssageSource.getMessage("line.api.err.notFoundAttendance");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug("editAction()AttendanceNotFound");
				return;
			}
			tAttendance.setAttendanceTime(yyyyMMddHHmm);
			tAttendance.setEditFlg("1");
			tAttendanceDao.update(tAttendance);
			lineStatus.setActionName(null);
			lineStatus.setContents(null);
			tLineStatusDao.update(lineStatus);
			//修正済みメッセージを送信
			String msg = AppMesssageSource.getMessage("line.saveAttendance",attendanceCd,"");
			LineAPIService.repryMessage(replyToken, msg);
			logger.debug(msg);
			logger.debug("editAction()editTimeEnd");

		}
		logger.debug("editAction()end");

	}

}
