package application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.context.AppMesssageSource;
import application.entity.TLineStatus;

/**
 * 勤怠情報「リスト」操作サービス。
 */
@Service
@Transactional
public class AttendanceListService extends AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceListService.class);

    public void test() {
    	System.out.println("chk");
    }

    /**
     * リッチメニューから選択された時の処理。
     * @param replyToken リプライトークン
     */
	public void startList(String replyToken) {


		//メッセージの送信
		String msg = AppMesssageSource.getMessage("line.listYearMonth");
        LineAPIService.repryMessage(replyToken, msg);
	}

    /**
     * リストが押された時の処理をする。
     * @param lineId LINE識別子
     * @param replyToken リプライトークン
     * @param lineStatus LINEステータス
     * @param text テキスト
     */
	public void listAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
