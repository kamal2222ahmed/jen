echo ${GIT_BRANCH_NAME}
pwd
ls
cd Apps/
perl ../jenkinsutil/git_polling/polling.pl 10.103.130.42:8080/jenkins
rm -rf ../Apps
