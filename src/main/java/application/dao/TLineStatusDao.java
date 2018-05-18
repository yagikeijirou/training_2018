package application.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.TLineStatus;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * ユーザマスタDAO。
 * @author 作成者氏名
 */
@Component
public class TLineStatusDao extends AbstractDao<TLineStatus> {
    //    /** このクラスのロガー。 */
    //    private static final Logger logger = LoggerFactory.getLogger(TLineStatusDao.class);

    @Autowired
    private SqlTemplate sqlTemplate;

    public Optional<TLineStatus> selectByPk(Integer userId) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/TLineStatusDao/selectByPk.sql", TLineStatus.class, userId));
    }

    /**
     * PKでユーザを取得する。
     * @param userId ユーザID
     * @return ユーザ
     */
    public TLineStatus getByPk(Integer userId) {
        if (userId == null) {
            return null;
        }
        Optional<TLineStatus> select = selectByPk(userId);
        TLineStatus res = null;
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
    public Optional<TLineStatus> selectByMail(String mail) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/TLineStatusDao/selectByMail.sql", TLineStatus.class, mail));
    }

    /**
     * LINE IDでユーザを取得する。
     * @param lineId LINE ID
     * @return ユーザ
     */
    public Optional<TLineStatus> selectByLineId(String lineId) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/TLineStatusDao/selectByLineId.sql", TLineStatus.class, lineId));
    }

    /**
     * ユーザを取得する。
     * @param lineId LINE識別子
     * @return ユーザ
     */
    public TLineStatus getByLineId(String lineId) {
        Optional<TLineStatus> select = selectByLineId(lineId);
        TLineStatus res = null;
        if (select.isPresent()) {
            res = select.get();
        }
        return res;
    }

    /**
     * ユーザを新規登録する。
     * @param ユーザエンティティ
     */
    public int insert(TLineStatus entity) {
        setInsertColumns(entity);
        return sqlTemplate.update("sql/TLineStatusDao/insert.sql", entity);
    }

    /**
     * ユーザを更新する。
     * @param ユーザエンティティ
     */
    public int update(TLineStatus entity) {
        setUpdateColumns(entity);
        return sqlTemplate.update("sql/TLineStatusDao/update.sql", entity);
    }

    /**
     * ユーザを更新する(null値は更新対象外)。
     * @param ユーザエンティティ
     */
    public int updateAsNullIsExclude(TLineStatus entity) {
        setUpdateColumns(entity);
        return sqlTemplate.update("sql/TLineStatusDao/updateAsNullIsExclude.sql", entity);
    }
}
