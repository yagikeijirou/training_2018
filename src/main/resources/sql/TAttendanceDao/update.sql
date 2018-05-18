UPDATE t_attendance
   SET attendance_time = :attendanceTime
      ,edit_flg = :editFlg
    <#IF delFlg??>
      ,del_flg = :delFlg
    </#IF>
      ,update_date = :updateDate
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd
WHERE user_id = :userId