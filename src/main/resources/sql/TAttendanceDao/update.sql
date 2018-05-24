UPDATE t_attendance
   SET attendance_time = :attendanceTime
      ,edit_flg = :editFlg
      ,update_date = :updateDate
      ,update_user_id = :updateUserId
      ,update_func_cd = :updateFuncCd
WHERE user_id = :userId
 AND attendance_cd = :attendanceCd
 AND attendance_day = :attendanceDay