package application.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MUserDao;
import application.entity.MUser;

/**
 * ユーザ情報操作サービスクラス。
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    MUserDao muserDao;

    /**
     * ユーザIDをもとにユーザを取得する。
     * @param userId ユーザID
     * @return ユーザ情報
     */
    public Optional<MUser> getUserByUserId(Integer userId) {
        return Optional.ofNullable(muserDao.getByPk(userId));
    }

    /**
     * メールアドレスをもとにユーザを取得する。
     * @param mail メールアドレス
     * @return ユーザ情報
     */
    public Optional<MUser> getUserByMail(String mail) {
        return muserDao.selectByMail(mail);
    }

    /**
     * LINE IDをもとにユーザを取得する。
     * @param lineId LINE ID
     * @return ユーザ情報
     */
    public Optional<MUser> getUserByLineId(String lineId) {
        return muserDao.selectByLineId(lineId);
    }

    /**
     * LINE IDを登録する。
     * @param userId 対象ユーザID
     * @param lineId LINE ID
     * @return LINEアカウントが使用済のため登録できない場合
     */
    public boolean registerLineId(Integer userId, String lineId) {
        MUser user = muserDao.getByPk(userId);
        if (StringUtils.equals(user.getLineId(), lineId)) {
            // 既に登録済
            return true;
        }

        MUser userChk = muserDao.getByLineId(lineId);
        if (userChk == null) {
            // 通常の登録
            MUser entity = new MUser();
            entity.setUserId(userId);
            entity.setLineId(lineId);
            muserDao.updateAsNullIsExclude(entity);
        } else {
            // 他ユーザでLINEアカウントを利用済
            return false;
        }
        return true;
    }
}
