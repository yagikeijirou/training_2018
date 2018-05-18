package application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 勤怠情報「出勤・退勤」操作サービス。
 */
@Service
@Transactional
public class AttendanceInOutService extends AbstractAttendanceService {
	/** このクラスのロガー。 */
	private static final Logger logger = LoggerFactory.getLogger(AttendanceInOutService.class);

	public void putArrivalNow(String lineId, String replyToken) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void putClockOutNow(String lineId, String replyToken) {
		// TODO 自動生成されたメソッド・スタブ

	}

}