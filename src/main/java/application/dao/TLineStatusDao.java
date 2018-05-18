package application.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.TLineStatus;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * LINEステータス情報DAO。
 * @author 菅一生
 */
@Component
public class TLineStatusDao extends AbstractDao<TLineStatus> {
    //    /** このクラスのロガー。 */
    //    private static final Logger logger = LoggerFactory.getLogger(TLineStatusDao.class);

    @Autowired
    private SqlTemplate sqlTemplate;

    public Optional<TLineStatus> selectByPk(String lineId) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/TLineStatusDao/selectByPk.sql", TLineStatus.class, lineId));
    }

    /**
     * PKでLINEステータス情報を取得する。
     * @param lineId LINE識別子
     * @return LINEステータス情報
     */
    public TLineStatus getByPk(String lineId) {
        if (lineId == null) {
            return null;
        }
        Optional<TLineStatus> select = selectByPk(lineId);
        TLineStatus res = null;
        if (select.isPresent()) {
            res = select.get();
        }
        return res;
    }

    /**
     * ユーザIDでLINEステータス情報を取得する。
     * @param userId ユーザID
     * @return LINEステータス情報
     */
    public Optional<TLineStatus> selectByUserId(Integer userId) {
        return Optional.ofNullable(sqlTemplate.forObject("sql/TLineStatusDao/selectByUserId.sql", TLineStatus.class, userId));
    }


    /**
     * ユーザIDでLINEステータス情報を取得する。
     * @param userId ユーザID
     * @return LINEステータス情報
     */
    public TLineStatus getByUserId(Integer userId) {
        Optional<TLineStatus> select = selectByUserId(userId);
        TLineStatus res = null;
        if (select.isPresent()) {
            res = select.get();
        }
        return res;
    }

    /**
     * LINEステータス情報を新規登録する。
     * @param LINEステータス情報エンティティ
     */
    public int insert(TLineStatus entity) {
        setInsertColumns(entity);
        return sqlTemplate.update("sql/TLineStatusDao/insert.sql", entity);
    }

    /**
     * LINEステータス情報を更新する。
     * @param LINEステータス情報エンティティ
     */
    public int update(TLineStatus entity) {
        setUpdateColumns(entity);
        return sqlTemplate.update("sql/TLineStatusDao/update.sql", entity);
    }

}
