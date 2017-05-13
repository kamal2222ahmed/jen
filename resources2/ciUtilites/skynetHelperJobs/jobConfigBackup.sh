find /extra/jobs -maxdepth 2 -iname "config.xml" -exec tar -rvf jobs$BUILD_ID.tar {} \;
curl --upload-file jobs$BUILD_ID.tar -u admin:notadmin123 -v http://10.193.129.166:8081/repository/releases/gov/dhs/uscis/elis2/jenkins-backup/
