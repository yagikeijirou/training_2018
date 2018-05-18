INSERT INTO t_attendance (
   user_id
  ,attendance_cd
  ,attendance_day
  ,attendance_time
  ,regist_date
  ,regist_user_id
  ,regist_func_cd
) VALUES (
  :userId
 ,:attendanceDay
 ,:attendanceTime
 ,:registDate
 ,:registUserId
 ,:registFuncCd
)