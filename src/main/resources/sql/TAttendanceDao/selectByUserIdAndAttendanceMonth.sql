SELECT *
 FROM t_attendance
WHERE user_id = :userId
  AND (
    attendance_time >= to_date(:aMonth, 'YYYYMMDD')
      AND
    attendance_time < LAST_DAY(to_date(:aMonth, 'YYYYMMDD')) + 1
    )
  AND del_flg = '0'