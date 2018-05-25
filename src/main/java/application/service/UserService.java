package application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MOrgDao;
import application.dao.MUserDao;
import application.dto.UserInfoDto;
import application.entity.MOrg;
import application.entity.MUser;

/**
 * ユーザ情報操作サービスクラス。
 * @author 黄倉大輔(一部編集)
 */
@Service
@Transactional
public class UserService {

	/** このクラスのロガー。 */
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	//	@Autowired
	//	private PasswordEncoder passwordEncoder;

	/** ユーザマスタ用DAO **/
	@Autowired
	MUserDao muserDao;

	/** 組織マスタ用DAO **/
	@Autowired
	MOrgDao morgDao;

	/**
	 * ユーザIDをもとにユーザを取得し、連想配列(Map)に変換する。
	 * @param userId ユーザID
	 * @return Map<String,Object> ユーザ情報リスト
	 */
	public Map<String, Object> getUserMapByUserId(Integer userId) {

		logger.debug("getUserMapByUserId()");

		MUser mUser = muserDao.getByPk(userId);
		MOrg mOrg = morgDao.getByPk(mUser.getOrgCd());
		Map<String, Object> map = new HashMap<>();

		map.put("results", mUser);
		map.put("orgName", mOrg.getOrgName());
		map.put("authName", muserDao.getAuthNameByAuthCd(mUser.getAuthCd()));
		if (mUser.getManagerId() != null) {
			map.put("managerName", muserDao.getByPk(mUser.getManagerId()).getName());
		}

		return map;
	}

	/**
	 * 組織コードをもとに全所属ユーザを取得し、連想配列(Map)に変換する。
	 * @param orgCd 組織コード
	 * @return Map<String,Object> ユーザ情報リスト
	 */
	public Map<String, Object> getUser(String orgCd) {

		logger.debug("getUser()");

		Map<String, Object> map = new HashMap<String, Object>();
		List<MUser> mUsers = muserDao.getAllUserByOrgCd(orgCd);
		ArrayList<UserInfoDto> uids = new ArrayList<UserInfoDto>();
		MOrg mOrg = morgDao.getByPk(orgCd);

		for (MUser mu : mUsers) {
			UserInfoDto uid = new UserInfoDto();
			uid.setUserId(mu.getUserId().toString());
			uid.setName(mu.getName());
			uid.setAuthName(muserDao.getAuthNameByAuthCd(mu.getAuthCd()));
			uid.setOrgName(mOrg.getOrgName());
			uid.setMail(mu.getMail());
			uids.add(uid);
		}
		map.put("results", uids);

		return map;
	}

	/**
	 * ユーザIDをもとにユーザを取得する。
	 * @param userId ユーザID
	 * @return ユーザ情報
	 */
	public Optional<MUser> getUserByUserId(Integer userId) {
		logger.debug("getUserByUserId()");
		return Optional.ofNullable(muserDao.getByPk(userId));
	}

	/**
	 * メールアドレスをもとにユーザを取得する。
	 * @param mail メールアドレス
	 * @return ユーザ情報
	 */
	public Optional<MUser> getUserByMail(String mail) {
		logger.debug("getUserByMail()");
		return muserDao.selectByMail(mail);
	}

	/**
	 * LINE IDをもとにユーザを取得する。
	 * @param lineId LINE ID
	 * @return ユーザ情報
	 */
	public Optional<MUser> getUserByLineId(String lineId) {
		logger.debug("getUserByLineId()");
		return muserDao.selectByLineId(lineId);
	}

	/**
	 * LINE IDを登録する。
	 * @param userId 対象ユーザID
	 * @param lineId LINE ID
	 * @return LINEアカウントが使用済のため登録できない場合
	 */
	public boolean registerLineId(Integer userId, String lineId) {
		logger.debug("registerLineId()");
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
		logger.debug("registerUser()");
		muserDao.insert(mUser);
	}

	/**
	 * ユーザを更新する。
	 * @param mUser ユーザマスタエンティティ
	 * @author 黄倉大輔
	 */
	public void updateUser(MUser mUser) {
		logger.debug("updateUser()");
		muserDao.update(mUser);
	}

	/**
	 * 指定されたユーザIDについて、ユーザマスタから論理削除する。
	 * @param userIds 削除したいユーザのユーザIDリスト
	 * @author 黄倉大輔
	 * @author 菅一生
	 * @throws NullPointerException 既に論理削除されているユーザを論理削除しようとした時
	 */
	public void deleteSomeUsers(List<Integer> userIds) {
		logger.debug("deleteSomeUsers()");
		for (Integer eachId : userIds) {
			muserDao.delete(muserDao.getByPk(eachId));
		}
	}

	/**
	 * select2用の上司配列を返す。
	 * @return Map<String,List<Map<String,Object>>> 上司情報リスト
	 */
	public Map<String, Object> select2ManagerList(String orgCd) {

		logger.debug("select2ManagerList()");

		List<MUser> mUsers = muserDao.getAllManagerByOrgCd(orgCd);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map2 = new HashMap<String, Object>();

		for (MUser mu : mUsers) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", mu.getUserId());
			map.put("text", mu.getName());
			list.add(map);
		}
		map2.put("results", list);

		return map2;
	}

	/**
	 * select2用の権限配列を返す。
	 * @return Map<String,List<Map<String,Object>>> 権限情報リスト
	 */
	public Map<String, Object> select2AuthList() {

		logger.debug("select2AuthList()");

		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map4 = new HashMap<String, Object>();

		map1.put("id", "01");
		map1.put("text", "一般");
		list.add(map1);
		map2.put("id", "02");
		map2.put("text", "上長");
		list.add(map2);
		map3.put("id", "03");
		map3.put("text", "管理者");
		list.add(map3);
		map4.put("results", list);

		return map4;
	}
}
