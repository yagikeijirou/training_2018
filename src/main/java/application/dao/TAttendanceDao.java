package application.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

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

	public Optional<TAttendance> selectByPk(Integer userId) {
		//TODO: return Optional.ofNullable(sqlTemplate.forObject("sql/TAttendanceDao/selectByPk.sql", TAttendance.class, userId));
		return null;
	}

	/**
	 * PKで勤怠情報を取得する。
	 * @param userId ユーザID
	 * @return 勤怠情報
	 */
	public TAttendance getByPk(Integer userId) {
		if (userId == null) {
			return null;
		}
		Optional<TAttendance> select = selectByPk(userId);
		TAttendance res = null;
		if (select.isPresent()) {
			res = select.get();
		}
		return res;
	}

	/**
	 * 勤怠区分で勤怠情報を取得する。
	 * @param attendanceCd 勤怠区分コード
	 * @return 勤怠情報
	 */
	public Optional<TAttendance> selectByAttendanceCd(String attendanceCd) {
		//TODO: return Optional.ofNullable(sqlTemplate.forObject("sql/TAttendanceDao/selectByAttendanceCd.sql", TAttendance.class, userId));
		return null;
	}

	/**
	 * 勤怠区分で勤怠情報を取得する。
	 * @param attendanceCd 勤怠区分コード
	 * @return 勤怠情報
	 */
	public TAttendance getByAttendanceCd(String attendanceCd) {
		if (attendanceCd == null) {
			return null;
		}
		Optional<TAttendance> select = selectByAttendanceCd(attendanceCd);
		TAttendance res = null;
		if (select.isPresent()) {
			res = select.get();
		}
		return res;
	}

	/**
	 * 出勤日で勤怠情報を取得する。
	 * @param attendanceDay 出勤日
	 * @return 勤怠情報
	 */
	public Optional<TAttendance> selectByAttendanceDay(String attendanceDay) {
		//TODO: return Optional.ofNullable(sqlTemplate.forObject("sql/TAttendanceDao/selectByAttendanceDay.sql", TAttendance.class, userId));
		return null;
	}

	/**
	 * 出勤日で勤怠情報を取得する。
	 * @param attendanceDay 出勤日
	 * @return 勤怠情報
	 */
	public TAttendance getByAttendanceDay(String attendanceDay) {
		if (attendanceDay == null) {
			return null;
		}
		Optional<TAttendance> select = selectByAttendanceDay(attendanceDay);
		TAttendance res = null;
		if (select.isPresent()) {
			res = select.get();
		}
		return res;
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