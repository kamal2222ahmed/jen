knife exec ChefRepo/utils/buildGradleProperties.krb ${ENVIRONMENT} ${WORKSPACE}/Apps/Tests/Serenity/gradle.properties -c /home/jenkins/.chef/knife.rb


APPS=/Apps
sed -i "s;databaseActivitiDataUser=activitidata;databaseActivitiDataUser=activitidata${TEAM};g; s;databaseElis2DataUser=elis2data;databaseElis2DataUser=elis2data${TEAM};g; s;databaseServer=localhost;databaseServer=el2-ft-idb1.aws.uscis.dhs.gov;g" ${WORKSPACE}${APPS}/gradle.properties
find ${WORKSPACE}${APPS} -path ./.git -prune -o -type f -exec sed -i "s;elis2datadevelop;elis2data${TEAM}develop;g; s;activitidatadevelop;activitidata${TEAM}develop;g; s;database.host=el2-ft-idb1.aws.uscis.dhs.gov;database.host=el2-ft-idb1.aws.uscis.dhs.gov;g" {} +
