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

	}

	public void editAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
