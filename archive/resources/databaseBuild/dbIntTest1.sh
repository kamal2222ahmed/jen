mkdir -p ${WORKSPACE}/Database-${PROD_VERSION}
mkdir -p ${WORKSPACE}/Database-${ARTIFACT_VERSION}
mkdir -p ${WORKSPACE}/DataLoader-${PROD_VERSION}


echo "Downloading Database-${PROD_VERSION}.zip"
wget --quiet --no-check-certificate "http://${NEXUS}/nexus/service/local/artifact/maven/redirect?r=public&g=gov.dhs.uscis.elis2&a=Database&v=${PROD_VERSION}&p=zip" -O Database-${PROD_VERSION}.zip

echo "Downloading Database-${ARTIFACT_VERSION}.zip"
wget --quiet --no-check-certificate "http://${NEXUS}/nexus/service/local/artifact/maven/redirect?r=public&g=gov.dhs.uscis.elis2&a=Database&v=${ARTIFACT_VERSION}&p=zip" -O Database-${ARTIFACT_VERSION}.zip

echo "Downloading DataLoader-${PROD_VERSION}.tar"
wget --quiet --no-check-certificate "http://${NEXUS}/nexus/service/local/artifact/maven/redirect?r=public&g=gov.dhs.uscis.elis2&a=DataLoader&v=${PROD_VERSION}&p=tar" -O DataLoader-${PROD_VERSION}.tar

unzip -qu ${WORKSPACE}/Database-${PROD_VERSION}.zip -d ${WORKSPACE}/Database-${PROD_VERSION}
unzip -qu ${WORKSPACE}/Database-${ARTIFACT_VERSION}.zip -d ${WORKSPACE}/Database-${ARTIFACT_VERSION}
tar -xf ${WORKSPACE}/DataLoader-${PROD_VERSION}.tar -C ${WORKSPACE}/DataLoader-${PROD_VERSION}

echo "Copying gradle.properties"

cp ${WORKSPACE}/ft/gradle.properties ${WORKSPACE}/Database-${PROD_VERSION}
cp ${WORKSPACE}/ft/gradle.properties ${WORKSPACE}/Database-${ARTIFACT_VERSION}
cp ${WORKSPACE}/ft/gradle.properties ${WORKSPACE}/DataLoader-${PROD_VERSION}
