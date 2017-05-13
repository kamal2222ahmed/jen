knife environment from file environments/FT_TEMPLATE.json

IFS=","
envs=`mysql -D jenkins -u ${MYSQL_USER} -p${MYSQL_CRED} -h ${MYSQL_HOST} --disable-column-name --execute 'select group_concat(name) as list from environments where status != '\''DISABLED'\'' and (codeline = '\''hotfix'\'' or codeline = '\''master'\'' or codeline = '\''staging'\'')'`

for i in $envs
do
  echo $i
  knife exec utils/updateFTEnvironment.krb FT_TEMPLATE $i
done

#environments/makeFor2FT.sh
#knife environment from file environments/FT_TEMPLATE_2.json

#for i in $envs
#do
#  if [ $i == "FT133" ] || [ $i == "FT134" ] || [ $i == "FT106" ] || [ $i == "FT123" ] || [ $i == "FT115" ] || [ $i == "FT129" ] || [ $i == "FT113" ] || [ $i == "FT114" ] || [ $i == "FT102" ] || [ $i == "FT103" ] || [ $i == "FT109" ] || [ $i == "FT200" ] || [ $i == "FT201" ] || [ $i == "FT202" ] || [ $i == "FT117" ] || [ $i == "FT105" ] || [ $i == "FT124" ] || [ $i == "FT125" ]; then
#    echo "special FT 2 box config for $i"
#    knife exec utils/updateFTEnvironment.krb FT_TEMPLATE_2 $i
#  else
#    echo $i
#    knife exec utils/updateFTEnvironment.krb FT_TEMPLATE $i
#  fi
#done
