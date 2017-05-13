echo ${GIT_BRANCH_NAME}
pwd
ls
cd elis_image_transmission/
perl ../jenkinsutil/git_polling/micro_polling.pl elis-jenkins.uscis.dhs.gov
rm -rf ../elis_image_transmission
