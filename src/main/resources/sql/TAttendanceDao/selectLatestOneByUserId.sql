SELECT *
 FROM t_attendance
WHERE user_id = :userId
 AND attendance_time = (
    SELECT max(attendance_time)
     FROM t_attendance
 )
 AND del_flg = '0'