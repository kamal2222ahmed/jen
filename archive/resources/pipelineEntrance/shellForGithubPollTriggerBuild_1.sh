#!/bin/bash +x
echo "${GIT_BRANCH} is the branch we just got triggered by with the hash ${GIT_COMMIT}"
echo "${GIT_LOCAL_BRANCH} is the branch locally we just got triggered with the hash of ${GIT_COMMIT}"
echo "branchToBuild=${GIT_BRANCH#*/}" >> branch-${BUILD_NUMBER}.txt
