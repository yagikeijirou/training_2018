update m_org
   set org_name = :orgName
      ,disp_seq = :dispSeq
      ,del_flg = :delFlg
      ,update_date = :updateDate
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd
where org_cd = :orgCd