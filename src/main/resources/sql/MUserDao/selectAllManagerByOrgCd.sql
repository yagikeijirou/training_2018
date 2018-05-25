SELECT *
 FROM m_user
WHERE org_cd = ?
AND del_flg = '0'
AND (auth_cd = 02 OR auth_cd = 03)