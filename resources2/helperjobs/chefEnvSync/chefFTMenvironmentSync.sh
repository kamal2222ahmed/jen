knife environment from file environments/FTM_TEMPLATE.json

IFS=","
envs=`mysql -D jenkins -u ${MYSQL_USER} -p${MYSQL_CRED} -h ${MYSQL_HOST} --disable-column-name --execute 'select group_concat(name) as list from team_environments where status != '\''DISABLED'\'' and (codeline = '\''hotfix'\'' or codeline = '\''master'\'' or codeline = '\''staging'\'')'`

for i in $envs
do
  echo $i
  knife exec utils/updateFTEnvironment.krb FTM_TEMPLATE $i
done
