package application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 組織コードをもとに組織を取得し、連想配列(Map)に変換する。
     * @param orgCd 組織コード
     * @return Map<String,Object> 組織情報リスト
     */
    public Map<String,Object> getOrgMapByOrgCd(String orgCd) {
//    	MOrg mOrg = morgDao.getByPk(orgCd);
//    	Map<String, Object> map = new HashMap<>();
//    	map.put("orgCd", mOrg.getOrgCd());
//    	map.put("orgName", mOrg.getOrgName());
//    	map.put("dispSeq", mOrg.getDispSeq());
//        return map;
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
//    	List<MOrg> mOrgs = morgDao.getAll();
//		Map<String, Object> map2 = new HashMap<String, Object>();
//		int i = 0;
//
//		for(MOrg mo : mOrgs) {//mOrgsという連想配列にList<MOrg>の情報を格納
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("OrgCd", mo.getOrgCd());
//			map.put("OrgName", mo.getOrgName());
//			map2.put(String.valueOf(i++), map);
//		}
//		return map2;
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
        return morgDao.getByPk(orgCd);
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
        morgDao.delete(mOrg);
    }

}
