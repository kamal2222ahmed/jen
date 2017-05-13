# remove the cache 
# remove workspace/ELIS2-HOTFIX-BuildGateBuild/caches/*

cd ${WORKSPACE}
cd ..
pwd
ls -d ./ELIS*/caches/* | xargs rm -rf


#cd ELIS2-BuildGateBuild/caches
#pwd
#rm -rf *
