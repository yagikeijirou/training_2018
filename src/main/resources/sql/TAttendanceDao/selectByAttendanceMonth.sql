SELECT *
 FROM t_attendance
WHERE attendance_day = :attendanceDay
  AND del_flg = '0'