SELECT *
 FROM t_attendance
WHERE user_id = :userId
  AND attendance_day = :attendanceDay
  AND del_flg = '0'