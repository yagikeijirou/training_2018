package application.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.MOrg;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 組織マスタDAO。
 * @author 隅田穂高
 */
@Component
public class MOrgDao extends AbstractDao<MOrg> {
    //    /** このクラスのロガー。 */
    //    private static final Logger logger = LoggerFactory.getLogger(MUserDao.class);

    @Autowired
    private SqlTemplate sqlTemplate;

    public Optional<MOrg> selectByPk(Integer orgId) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MOrgDao/selectByPk.sql", MOrg.class, orgId));
    }

    /**
     * 組織情報を取得する
     *
     * @return 組織エンティティ
     */
    public Optional<MOrg> select() {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MOrgDao/select.sql", MOrg.class));
    }


    /**
     * 全組織を取得する
     * @param 組織エンティティ
     */
    public MOrg getAll() {
         Optional<MOrg> select = select();
         MOrg res = null;
         if (select.isPresent()) {
             res = select.get();
         }
         return res;
    }

    /**
     * PKで組織を取得する。
     * @param orgId 組織ID
     * @return 組織
     */
    public MOrg getByPk(Integer orgId) {
        if (orgId == null) {
            return null;
        }
        Optional<MOrg> select = selectByPk(orgId);
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
