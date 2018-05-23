package application.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	 * @param attendanceDay 出勤日（yyyyMMdd）
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
	 * @param attendanceDay 出勤日（yyyyMMdd）
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
	 * @param attendanceMonth 出勤年月（yyyyMM）
	 * @return 勤怠情報エンティティリスト
	 */
	public List<TAttendance> getByUserIdAndAttendanceMonth(Integer userId, String attendanceMonth) {
		Date aMonth;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

		try {
			aMonth = sdf.parse(attendanceMonth);
		} catch (ParseException e) {
			e.printStackTrace();
			aMonth = null;
		}

		Map<String, Object> cond = new HashMap<>();
		cond.put("userId", userId);
		cond.put("aMonth", new SimpleDateFormat("yyyyMMdd").format(aMonth));
		return sqlTemplate.forList("sql/TAttendanceDao/selectByUserIdAndAttendanceMonth.sql", TAttendance.class, cond);
	}

	/**
	 * 出勤日で勤怠情報を取得する。（＝全ユーザの指定月の勤怠情報一覧）
	 *
	 * @param attendanceMonth 出勤年月（yyyyMM）
	 * @return 勤怠情報エンティティリスト
	 */
	public List<TAttendance> getByAttendanceMonth(String attendanceMonth) {
		Date aMonth;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

		try {
			aMonth = sdf.parse(attendanceMonth);
		} catch (ParseException e) {
			e.printStackTrace();
			aMonth = null;
		}

		Map<String, Object> cond = new HashMap<>();
		cond.put("aMonth", new SimpleDateFormat("yyyyMMdd").format(aMonth));
		return sqlTemplate.forList("sql/TAttendanceDao/selectByAttendanceMonth.sql", TAttendance.class, cond);
	}

	/**
	 * 指定ユーザの最新の勤怠情報を取得する。
	 *
	 * @param userId ユーザID
	 * @return 勤怠情報エンティティ
	 * @author 菅一生
	 */
	public Optional<TAttendance> selectLatestOneByUserId(Integer userId) {
		Map<String, Object> cond = new HashMap<>();
		cond.put("userId", userId);
		return Optional.ofNullable(sqlTemplate.forObject("sql/TAttendanceDao/selectLatestOneByUserId.sql", TAttendance.class, cond));
	}

	/**
	 * 指定ユーザの最新の勤怠情報を取得する。
	 *
	 * @param userId ユーザID
	 * @return 勤怠情報エンティティ
	 * @author 菅一生
	 */
	public TAttendance getLatestOneByUserId(Integer userId) {
		if (userId == null) {
			return null;
		}
		Optional<TAttendance> select = selectLatestOneByUserId(userId);
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
		return sqlTemplate.update("sql/TAttendanceDao/insert.sql", entity);
	}

	/**
	 * 勤怠情報を更新する。
	 * @param 勤怠情報エンティティ
	 */
	public int update(TAttendance entity) {
		setUpdateColumns(entity);
		return sqlTemplate.update("sql/TAttendanceDao/update.sql", entity);
	}

}