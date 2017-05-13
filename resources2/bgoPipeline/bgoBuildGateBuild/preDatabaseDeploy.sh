token=4

rm -f ${WORKSPACE}/deploy.properties
echo DATABASE=${token} >> ${WORKSPACE}/deploy.properties
echo GRADLE_USER_HOME=/opt/.gradle/private-${token} >> ${WORKSPACE}/deploy.properties

mkdir -p ${GRADLE_USER_HOME}
