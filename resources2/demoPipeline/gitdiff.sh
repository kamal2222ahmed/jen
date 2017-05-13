cd ${WORKSPACE}/Apps
git log --pretty=oneline ${LAST_DEPLOY_VERSION}..${ELIS2_VERSION} > ${WORKSPACE}/gitdiff.txt
cut -d " " -f 2- ${WORKSPACE}/gitdiff.txt> ${WORKSPACE}/gitdiff2.txt
sed '/^Merge /d' ${WORKSPACE}/gitdiff2.txt > ${WORKSPACE}/gitdiff3.txt
