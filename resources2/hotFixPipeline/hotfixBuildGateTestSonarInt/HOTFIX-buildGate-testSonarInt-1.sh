token=4
rm -f ${WORKSPACE}/deploy.properties
echo DATABASE=${token} >> ${WORKSPACE}/deploy.properties
echo GRADLE_USER_HOME=/opt/.gradle/private-${token} >> ${WORKSPACE}/deploy.properties

sudo mkdir -p ${GRADLE_USER_HOME}
sudo chown -R jenkins:jenkins ${GRADLE_USER_HOME}  

npm config set proxy "http://proxy.apps.dhs.gov:80"

cd ${WORKSPACE}/Apps/InternalApp/InternalApp/
npm-cache install

cd ${WORKSPACE}/Apps/Shared/Metis/

npm-cache install

