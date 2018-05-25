package application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MOrgDao;
import application.entity.MOrg;

/**
 * 組織情報操作サービスクラス。
 * @author 黄倉大輔
 */
@Service
@Transactional
public class OrgService {

	/** このクラスのロガー。 */
	private static final Logger logger = LoggerFactory.getLogger(OrgService.class);

	/** 組織マスタ用DAO **/
	@Autowired
	MOrgDao morgDao;

	/**
	 * 組織コードをもとに組織を取得し、連想配列(Map)に変換する。
	 * @param orgCd 組織コード
	 * @return Map<String,Object> 組織情報リスト
	 */
	public Map<String, Object> getOrgMapByOrgCd(String orgCd) {

		logger.debug("getOrgMapByOrgCd()");

		MOrg mOrg = morgDao.getByPk(orgCd);
		Map<String, Object> map = new HashMap<>();
		map.put("results", mOrg);

		return map;
	}

	/**
	 * 全組織を取得し、連想配列(Map)に変換する。
	 *
	 * @return Map<String,Object> 組織情報リスト
	 */
	public Map<String, Object> getOrg() {

		logger.debug("getOrg()");

		List<MOrg> mOrgs = morgDao.getAll();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("results", mOrgs);

		return map;
	}

	/**
	 * 組織コードをもとに組織を取得する。
	 * @param orgCd 組織コード
	 * @return 組織マスタエンティティ
	 */
	public MOrg getOrgByOrgCd(String orgCd) {
		logger.debug("getOrgByOrgCd()");
		return morgDao.getByPk(orgCd);
	}

	/**
	 * 組織を登録する。
	 * @param mOrg 組織マスタエンティティ
	 */
	public void registerOrg(MOrg mOrg) {
		logger.debug("registerOrg()");
		morgDao.insert(mOrg);
	}

	/**
	 * 組織を更新する。
	 * @param mOrg 組織マスタエンティティ
	 */
	public void updateOrg(MOrg mOrg) {
		logger.debug("updateOrg()");
		morgDao.update(mOrg);
	}

	/**
	 * 組織を削除する。
	 * @param mOrg 組織マスタエンティティ
	 */
	public void deleteOrg(MOrg mOrg) {
		logger.debug("deleteOrg()");
		morgDao.delete(mOrg);
	}

	/**
	 * select2用の組織配列を返す。
	 * @return Map<String,List<Map<String,Object>>> 組織情報リスト
	 */
	public Map<String, Object> select2OrgList() {

		logger.debug("select2OrgList()");

		List<MOrg> mOrgs = morgDao.getAll();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map2 = new HashMap<String, Object>();

		for (MOrg mo : mOrgs) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", mo.getOrgCd());
			map.put("text", mo.getOrgName());
			list.add(map);
		}
		map2.put("results", list);

		return map2;
	}

}
