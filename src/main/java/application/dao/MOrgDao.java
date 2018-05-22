package application.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.MOrg;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 組織マスタDAO。
 * @author 隅田穂高(追記編集：黄倉大輔)
 */
@Component
public class MOrgDao extends AbstractDao<MOrg> {
    //    /** このクラスのロガー。 */
    //    private static final Logger logger = LoggerFactory.getLogger(MUserDao.class);

    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * PKで組織情報を取得する
     * @param orgCd 組織コード
     * @return 組織エンティティ
     */
    public Optional<MOrg> selectByPk(String orgCd) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MOrgDao/selectByPk.sql", MOrg.class, orgCd));
    }

    /**
     * 組織情報を取得する
     *
     * @return 組織エンティティ
     */
    public Optional<MOrg> select() {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MOrgDao/selectAll.sql", MOrg.class));
    }


    /**
     * 全組織を取得する
     * @param 組織エンティティ
     */
    public List<MOrg> getAll() {

         return sqlTemplate.forList("sql/MOrgDao/selectAll.sql", MOrg.class);
    }

    /**
     * PKで組織を取得する。
     * @param orgCd 組織コード
     * @return 組織
     */
    public MOrg getByPk(String orgCd) {
        if (orgCd == null) {
            return null;
        }
        Optional<MOrg> select = selectByPk(orgCd);
        MOrg res = null;
        if (select.isPresent()) {
            res = select.get();
        }
        return res;
    }

    /**
     * 組織を新規登録する。
     * @param 組織エンティティ
     */
    public int insert(MOrg entity) {
        setInsertColumns(entity);
        return sqlTemplate.update("sql/MOrgDao/insert.sql", entity);
    }

    /**
     * 組織を更新する。
     * @param 組織エンティティ
     */
    public int update(MOrg entity) {
        setUpdateColumns(entity);
        return sqlTemplate.update("sql/MOrgDao/update.sql", entity);
    }


}
