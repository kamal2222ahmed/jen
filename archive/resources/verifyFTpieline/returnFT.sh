set +x
mysql -D jenkins -u ${MYSQL_USER} -p${MYSQL_CRED} -h ${MYSQL_HOST} --disable-column-name --execute "CALL produceFTEnv('${ENVIRONMENT}')"
