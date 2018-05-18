UPDATE t_line_status
   SET menu_cd = :menuCd
      ,action_name = :actionName
      ,contents = :contents
      ,request_time = :requestTime
    <#if delFlg??>
      ,del_flg = :delFlg
    </#if>
      ,update_date = :updateDate
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd
WHERE line_id = :lineId