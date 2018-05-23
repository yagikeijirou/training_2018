SELECT *
 FROM t_attendance
WHERE attendance_time = (
    SELECT max(attendance_time)
     FROM t_attendance
     WHERE user_id = :userId
 )
 AND del_flg = '0'