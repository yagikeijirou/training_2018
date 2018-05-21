select *
 from m_user
where manager_id = ?
  and del_flg = '0'