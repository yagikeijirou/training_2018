package application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 勤怠の「アラート」操作サービス。
 */
@Service
@Transactional
public class AttendanceAlertService extends AbstractAttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceAlertService.class);

}
