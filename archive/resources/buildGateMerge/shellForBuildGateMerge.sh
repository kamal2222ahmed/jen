cd ${WORKSPACE}/Apps
#git push origin ${GIT_BRACH_HASH}:Staging
echo "git branch hash = ${GIT_BRANCH_HASH}"
hash=${GIT_BRANCH_HASH}
if [ -z $hash ];
then
   echo "no hash, no merge :-P"
   exit 1
else
    exit 0
fi
