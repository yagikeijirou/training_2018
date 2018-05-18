package application.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.TAttendance;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 勤怠情報DAO。
 * @author 菅一生
 */
@Component
public class TAttendanceDao extends AbstractDao<TAttendance> {
	//  /** このクラスのロガー。 */
	//    private static final Logger logger = LoggerFactory.getLogger(TAttendanceDao.class);

	@Autowired
	private SqlTemplate sqlTemplate;

	/**
	 * PKで勤怠情報を取得する。
	 *
	 * @param userId ユーザID
	 * @param attendanceCd 勤怠区分コード
	 * @param attendanceDay 出勤日
	 * @return 勤怠情報エンティティ
	 */
	public Optional<TAttendance> selectByPk(Integer userId, String attendanceCd, String attendanceDay) {
		Map<String, Object> cond = new HashMap<>();
		cond.put("userId", userId);
		cond.put("attendanceCd", attendanceCd);
		cond.put("attendanceDay", attendanceDay);
		return Optional.ofNullable(sqlTemplate.forObject("sql/TAttendanceDao/selectByPk.sql", TAttendance.class, cond));
	}

	/**
	 * PKで勤怠情報を取得する。
	 *
	 * @param userId ユーザID
	 * @param attendanceCd 勤怠区分コード
	 * @param attendanceDay 出勤日
	 * @return 勤怠情報エンティティ
	 */
	public TAttendance getByPk(Integer userId, String attendanceCd, String attendanceDay) {
		if (userId == null) {
			return null;
		}
		Optional<TAttendance> select = selectByPk(userId, attendanceCd, attendanceDay);
		TAttendance res = null;
		if (select.isPresent()) {
			res = select.get();
		}
		return res;
	}

	/**
	 * ユーザID、出勤日で勤怠情報を取得する。（＝あるユーザの指定月の勤怠情報一覧）
	 *
	 * @param userId ユーザID
	 * @param attendanceDay 出勤日
	 * @return 勤怠情報エンティティリスト
	 */
	//FIXME: 現状、attendanceDayで指定した年月日のみ表示（その月全部ではない）
	public List<TAttendance> getByUserIdAndAttendanceDay(Integer userId, String attendanceDay) {
		Map<String, Object> cond = new HashMap<>();
		cond.put("userId", userId);
		cond.put("attendanceDay", attendanceDay);
		return sqlTemplate.forList("sql/TAttendanceDao/selectByUserIdAndAttendanceDay.sql", TAttendance.class, cond);
	}

	/**
	 * 出勤日で勤怠情報を取得する。（＝全ユーザの指定月の勤怠情報一覧）
	 *
	 * @param attendanceDay 出勤日
	 * @return 勤怠情報エンティティリスト
	 */
	//FIXME: 現状、attendanceDayで指定した年月日のみ表示（その月全部ではない）
	public List<TAttendance> getByAttendanceDay(Integer userId, String attendanceDay) {
		Map<String, Object> cond = new HashMap<>();
		cond.put("attendanceDay", attendanceDay);
		return sqlTemplate.forList("sql/TAttendanceDao/selectByAttendanceDay.sql", TAttendance.class, cond);
	}


	/**
	 * 勤怠情報を新規登録する。
	 * @param 勤怠情報エンティティ
	 */
	public int insert(TAttendance entity) {
		setInsertColumns(entity);
		return 0;
	}

	/**
	 * 勤怠情報を更新する。
	 * @param 勤怠情報エンティティ
	 */
	public int update(TAttendance entity) {
		setUpdateColumns(entity);
		return 0;
	}

}