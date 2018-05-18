update m_user
   set name = :name
      ,password = :password
      ,mail = :mail
      ,auth_cd = :authCd
      ,org_cd = :orgCd
      ,manager_id = :managerId
      ,line_id = :lineId
    <#if delFlg??>
      ,del_flg = :delFlg
    </#if>
      ,update_date = :updateDate
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd
where user_id = :userId