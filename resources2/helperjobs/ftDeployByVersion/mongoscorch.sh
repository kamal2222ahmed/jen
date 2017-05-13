mongo ${tcip}:27017/FormDB ${WORKSPACE}/jenkinsutil/mongo_scorch/scorch_formdb.js || true

VERSION=${PROD_VERSION}
ssh -i $keyToUse -t ec2-user@${dbip} "ls -lart; rm -rf yep.sh; curl -O http://10.193.129.166:8081/repository/releases/gov/dhs/uscis/elis2/dbExport/LATEST/dbExport-LATEST-yep.sh; ls -lart; chmod a+x *.sh; ./yep.sh ${VERSION}"
