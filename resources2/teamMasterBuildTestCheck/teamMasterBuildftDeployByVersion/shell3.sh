echo "Downloading Database-${PROD_VERSION}.zip"
wget --quiet --no-check-certificate "http://${NEXUS}/repository/public/gov/dhs/uscis/elis2/Database/${PROD_VERSION}/Database-${PROD_VERSION}.zip" -O Database-${PROD_VERSION}.zip

echo "Downloading Database-${ARTIFACT_VERSION}.zip"
wget --quiet --no-check-certificate "http://${NEXUS}/repository/public/gov/dhs/uscis/elis2/Database/${ARTIFACT_VERSION}/Database-${ARTIFACT_VERSION}.zip" -O Database-${ARTIFACT_VERSION}.zip

echo "Downloading DataLoader-${ARTIFACT_VERSION}.tar"
wget --quiet --no-check-certificate "http://${NEXUS}/repository/public/gov/dhs/uscis/elis2/DataLoader/${ARTIFACT_VERSION}/DataLoader-${ARTIFACT_VERSION}.tar" -O DataLoader-${ARTIFACT_VERSION}.tar

unzip -q ${WORKSPACE}/Database-${PROD_VERSION}.zip -d ${WORKSPACE}/Database-${PROD_VERSION}
unzip -q ${WORKSPACE}/Database-${ARTIFACT_VERSION}.zip -d ${WORKSPACE}/Database-${ARTIFACT_VERSION}
tar -xf ${WORKSPACE}/DataLoader-${ARTIFACT_VERSION}.tar -C ${WORKSPACE}/DataLoader-${ARTIFACT_VERSION}

echo "Copying gradle.properties"

cp ${WORKSPACE}/ft/gradle.properties ${WORKSPACE}/Database-${PROD_VERSION}
cp ${WORKSPACE}/ft/gradle.properties ${WORKSPACE}/Database-${ARTIFACT_VERSION}
cp ${WORKSPACE}/ft/gradle.properties ${WORKSPACE}/DataLoader-${ARTIFACT_VERSION}
