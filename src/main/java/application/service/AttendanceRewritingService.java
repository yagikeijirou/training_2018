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

		if (text == null) {

			return;
		}

		//4,LINEステータス情報を検索する。
		//5,メニューコードとアクション名が適切か確認する。
		if (lineStatus.getMenuCd().equals("03")
				&& lineStatus.getActionName().equals(ACTION_EDIT_DATE)) {
			String MMdd = CommonUtils.toMonthDate(text);
			if (MMdd == null) {
				String msg = AppMesssageSource.getMessage("line.editMonthDate");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
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

			logger.debug("witchAttendance?");

		}

		//9,LINEステータス情報を検索する。
		//10,アクション名が適切か確認する。
		else if (lineStatus.getMenuCd().equals("03")
				&& lineStatus.getActionName().equals(ACTION_EDIT_TYPE_SELECTION)) {

			//11,フォーマットが適切かどうか確認する。
			if (text.equals("出勤")) {
				//12,POSTされた勤怠区分コードの情報をlineステータス情報の「ActionName」として格納する。
				lineStatus.setActionName(ACTION_EDIT_INPUT_TIME_ARRIVAL);
				tLineStatusDao.update(lineStatus);
				//13,LINE APIを用いて「新しい{0}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
			} else if (text.equals("退勤")) {
				//12,POSTされた勤怠区分コードの情報を勤怠情報エンティティ「attendanceCd」として格納する。
				lineStatus.setActionName(ACTION_EDIT_INPUT_TIME_CLOCKOUT);
				tLineStatusDao.update(lineStatus);
				//13,LINE APIを用いて「新しい{1}時刻(hhmm)を入力してください	」というテキストを送信する。
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
			} else {
				String msg = AppMesssageSource.getMessage("line.selectAttendanceCd");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
				return;
			}

		}

		//14,LINEステータス情報を検索する。
		//15,メニューコードとアクション名が適切か確認する。
		else if (lineStatus.getMenuCd().equals("03")
				&& (lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_ARRIVAL)
						|| lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_CLOCKOUT))) {

			String HourMinute = CommonUtils.toHourMinute(text);
			if (HourMinute == null) {
				String msg = AppMesssageSource.getMessage("line.newAttendanceInput");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
				return;
			}

			String YyyyMmDd = lineStatus.getContents();
			Date yyyyMMddHHmm = CommonUtils.parseDate(YyyyMmDd + HourMinute, "yyyyMMddHHmm");

			//17,POSTされた勤怠時刻の情報を勤怠情報エンティティの「attendanceTime」として格納する。
			if (lineStatus.getActionName().equals(ACTION_EDIT_INPUT_TIME_ARRIVAL)) {

				tAttendance = tAttendanceDao.getByPk(lineStatus.getUserId(), "01",
						YyyyMmDd);

			} else {
				tAttendance = tAttendanceDao.getByPk(lineStatus.getUserId(), "02",
						YyyyMmDd);
			}

			if (tAttendance == null) {
				String msg = AppMesssageSource.getMessage("line.api.err.notFoundAttendance");
				LineAPIService.repryMessage(replyToken, msg);
				logger.debug(msg);
				return;
			}
			tAttendance.setAttendanceTime(yyyyMMddHHmm);
			tAttendance.setEditFlg("1");
			tAttendanceDao.update(tAttendance);
			//修正済みメッセージを送信
			String msg = AppMesssageSource.getMessage("line.saveAttendance");
			LineAPIService.repryMessage(replyToken, msg);
			logger.debug(msg);

		}
		logger.debug("editAction()end");

	}

}
