package application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.entity.TLineStatus;

/**
 * 勤怠情報「修正」操作サービス。
 */
@Service
@Transactional
public class AttendanceRewritingService extends AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceRewritingService.class);

	public void startRewriting(String replyToken) {
		// TODO 自動生成されたメソッド・スタブ

		//1,LINEステータス情報を検索する。

		//2,メニューコードとアクション名が適切か確認する。

		//3,LINE APIを用いて「修正する月日(yyyymmdd)を入力してください」というテキストを送信する。

		//4,LINEステータス情報を検索する。

		//5,メニューコードとアクション名が適切か確認する。

		//6,フォーマットが適切かどうか確認する。

		//7,POSTされた出勤日の情報を勤怠情報エンティティの「attendanceDay」として格納する。

		//8,LINE APIのボタンテンプレートを用いて、「勤怠区分を選択してください」という質問のテキストを送信する。
		//選択肢は「出勤」か「退勤」とする。

		//9,LINEステータス情報を検索する。

		//10,メニューコードとアクション名が適切か確認する。

		//11,フォーマットが適切かどうか確認する。

		//12,POSTされた勤怠区分コードの情報を勤怠情報エンティティ「attendanceCd」として格納する。

		//13,LINE APIを用いて「新しい{0} {1}時刻(hhmm)を入力してください	」というテキストを送信する。

		//14,LINEステータス情報を検索する。

		//15,メニューコードとアクション名が適切か確認する。

		//16,フォーマットが適切かどうか確認する。

		//17,POSTされた勤怠時刻の情報を勤怠情報エンティティの「attendanceTime」として格納する。

		//18,勤怠情報を更新する。

		//19,更新が完了したらLINE APIを用いて「{0} {1}を保存しました」というテキストを送信する。



	}

	public void editAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
