package application.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.MUser;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * ユーザマスタDAO。
 * @author 黄倉大輔(一部編集)
 */
@Component
public class MUserDao extends AbstractDao<MUser> {
	//    /** このクラスのロガー。 */
	//    private static final Logger logger = LoggerFactory.getLogger(MUserDao.class);

	@Autowired
	private SqlTemplate sqlTemplate;

	/**
	 * PKでユーザ情報を取得する
	 * @param userId ユーザID
	 * @return ユーザエンティティ
	 */
	public Optional<MUser> selectByPk(Integer userId) {
		return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByPk.sql", MUser.class, userId));
	}

	/**
	 * PKでユーザを取得する。
	 * @param userId ユーザID
	 * @return ユーザ
	 */
	public MUser getByPk(Integer userId) {
		if (userId == null) {
			return null;
		}
		Optional<MUser> select = selectByPk(userId);
		MUser res = null;
		if (select.isPresent()) {
			res = select.get();
		}
		return res;
	}

	/**
	 * メールアドレスでユーザを取得する。
	 * @param mail
	 * @return ユーザ
	 */
	public Optional<MUser> selectByMail(String mail) {
		return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByMail.sql", MUser.class, mail));
	}

	/**
	 * LINE IDでユーザを取得する。
	 * @param lineId LINE ID
	 * @return ユーザ
	 */
	public Optional<MUser> selectByLineId(String lineId) {
		return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByLineId.sql", MUser.class, lineId));
	}

	/**
	 * LINE IDでユーザを取得する。
	 * @param lineId LINE識別子
	 * @return ユーザ
	 */
	public MUser getByLineId(String lineId) {
		Optional<MUser> select = selectByLineId(lineId);
		MUser res = null;
		if (select.isPresent()) {
			res = select.get();
		}
		return res;
	}

	/**
	 * 上司IDでユーザを取得する。
	 *
	 * @param managerId ユーザID
	 * @return ユーザエンティティリスト
	 * @author 隅田穂高
	 */
	public List<MUser> getByManagerId(int managerId) {
		return sqlTemplate.forList("sql/MUserDao/selectByManagerId.sql", MUser.class, managerId);
	}

	/**
	 * 全てのユーザを取り出す。
	 *
	 * @return ユーザエンティティリスト
	 * @author 菅一生
	 */
	public List<MUser> getAll() {
		return sqlTemplate.forList("sql/MUserDao/selectAll.sql", MUser.class);
	}

	/**
	 * ユーザ氏名でユーザを取得する。
	 * @param name ユーザ氏名
	 * @return ユーザ
	 * @author 菅一生
	 */
	public Optional<MUser> selectByName(String name) {
		return Optional.ofNullable(sqlTemplate.forObject("sql/MUserDao/selectByName.sql", MUser.class, name));
	}

	/**
	 * ユーザ氏名でユーザを取得する。
	 * @param name ユーザ氏名
	 * @return ユーザ
	 * @author 菅一生
	 */
	public MUser getByName(String name) {
		Optional<MUser> select = selectByName(name);
		MUser res = null;
		if (select.isPresent()) {
			res = select.get();
		}
		return res;
	}

	/**
	 * 組織コードから全てのユーザを取り出す。
	 * @param orgCd 組織コード
	 * @return ユーザエンティティリスト
	 * @author 黄倉大輔
	 */
	public List<MUser> getAllUserByOrgCd(String orgCd) {
		return sqlTemplate.forList("sql/MUserDao/selectAllByOrgCd.sql", MUser.class, orgCd);
	}

	/**
	 * 権限コードから権限名を取得する。
	 * @param authCd 権限コード
	 * @return authName 権限名
	 * @author 黄倉大輔
	 */
	public String getAuthNameByAuthCd(String authCd) {
		switch (authCd) {
		case "1":
			return "一般";
		case "2":
			return "上長";
		case "3":
			return "管理者";
		default:
			return "権限未設定";
		}
	}

	/**
	 * ユーザを新規登録する。
	 * @param ユーザエンティティ
	 */
	public int insert(MUser entity) {
		setInsertColumns(entity);
		return sqlTemplate.update("sql/MUserDao/insert.sql", entity);
	}

	/**
	 * ユーザを更新する。
	 * @param ユーザエンティティ
	 */
	public int update(MUser entity) {
		setUpdateColumns(entity);
		return sqlTemplate.update("sql/MUserDao/update.sql", entity);
	}

	/**
	 * ユーザを更新する(null値は更新対象外)。
	 * @param ユーザエンティティ
	 */
	public int updateAsNullIsExclude(MUser entity) {
		setUpdateColumns(entity);
		return sqlTemplate.update("sql/MUserDao/updateAsNullIsExclude.sql", entity);
	}
}
