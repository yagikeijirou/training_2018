package application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MOrgDao;
import application.dao.MUserDao;
import application.entity.MOrg;
import application.entity.MUser;

/**
 * ユーザ情報操作サービスクラス。
 * @author 黄倉大輔(一部編集)
 */
@Service
@Transactional
public class UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	MUserDao muserDao;

	@Autowired
	MOrgDao morgDao;

	/**
	 * ユーザIDをもとにユーザを取得し、連想配列(Map)に変換する。
	 * @param userId ユーザID
	 * @return Map<String,Object> ユーザ情報リスト
	 */
	public Map<String, Object> getUserMapByUserId(Integer userId) {
		MUser mUser = muserDao.getByPk(userId);//ユーザIDからユーザエンティティを取得
		MOrg mOrg = morgDao.getByPk(mUser.getOrgCd());//組織コードから所属している組織エンティティを取得
		MUser manager = muserDao.getByPk(mUser.getManagerId());//上司IDから上司のエンティティを取得

		Map<String, Object> map = new HashMap<>();
		map.put("userId", mUser.getUserId());
		map.put("name", mUser.getName());
		map.put("mail", mUser.getMail());
		map.put("orgCd", mUser.getOrgCd());
		map.put("orgName", mOrg.getOrgName());//組織名
		map.put("authCd", mUser.getAuthCd());
		map.put("authName", muserDao.getAuthNameByAuthCd(mUser.getAuthCd()));//権限名
		map.put("managerId", mUser.getManagerId());
		map.put("managerName", manager.getName());//上司名
		return map;
	}

	/**
     * 組織コードをもとに全所属ユーザを取得し、連想配列(Map)に変換する。
     * @param orgCd 組織コード
     * @return Map<String,Object> ユーザ情報リスト
     */
    public Map<String, Object> getUser(String orgCd) {
    	List<MUser> mUsers = muserDao.getAll();
		Map<String, Object> map2 = new HashMap<String, Object>();
		MOrg mOrg = morgDao.getByPk(orgCd);
		int i = 0;

		for(MUser mu : mUsers) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", mu.getUserId());
			map.put("name", mu.getName());
			map.put("mail", mu.getMail());
			map.put("authName", muserDao.getAuthNameByAuthCd(mu.getAuthCd()));
			map.put("orgName", mOrg.getOrgName());
			map2.put(String.valueOf(i++), map);
		}
		return map2;
	}


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

	/**
	 * ユーザを登録する。
	 * @param mUser ユーザマスタエンティティ
	 * @author 黄倉大輔
	 */
	public void registerUser(MUser mUser) {
		muserDao.insert(mUser);
	}

	/**
	 * ユーザを更新する。
	 * @param mUser ユーザマスタエンティティ
	 * @author 黄倉大輔
	 */
	public void updateUser(MUser mUser) {
		muserDao.updateAsNullIsExclude(mUser);
	}

	/**
	 * 指定されたユーザIDについて、ユーザマスタから論理削除する。
	 * @param userIds 削除したいユーザのユーザIDリスト
	 * @author 黄倉大輔
	 * @author 菅一生
	 */
	public void deleteSomeUsers(List<Integer> userIds) {
		// List<MUser> mUsersを全て論理削除する
		//for (MUser eachUser : mUsers) {
		//	muserDao.delete(eachUser);
		//}
	}
}
