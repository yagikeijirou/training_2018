package application.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MOrgDao;
import application.entity.MOrg;

/**
 * 組織サービス。
 * @author 黄倉大輔
 */
@Service
@Transactional
public class OrgService {

    @Autowired
    MOrgDao morgDao;

    /**
     * 組織コードをもとに組織を取得する。
     * @param orgCd 組織コード
     * @return ユーザ情報
     */
    public Optional<MOrg> getOrgByOrgCd(String orgCd) {
        return Optional.ofNullable(morgDao.getByPk(orgCd));
    }

    /**
     * 全組織を取得する。
     *
     * @return ユーザ情報
     */
    public Optional<MOrg> getOrg() {
        return Optional.ofNullable(morgDao.getAll());
    }

    /**
     * 組織を登録する。
     * @param mOrg 組織マスタエンティティ
     */
    public void registerOrg(MOrg mOrg) {
        morgDao.insert(mOrg);
    }

    /**
     * 組織を更新する。
     * @param mOrg 組織マスタエンティティ
     */
    public void updateOrg(MOrg mOrg) {
        morgDao.update(mOrg);
    }

    /**
     * 組織を削除する。
     * @param mOrg 組織マスタエンティティ
     */
    public void deleteOrg(MOrg mOrg) {
        morgDao.delete(mOrg);//ゲットでいいのか？
    }

}
