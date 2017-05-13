cd ${WORKSPACE}/Apps/Backend/Elis2Services
sed -i "s/activitidatadevelop/activitidata${DATABASE}develop/g" src/main/resources/META-INF/properties/commonServices.database.properties
sed -i "s/elis2datadevelop/elis2data${DATABASE}develop/g" src/main/resources/META-INF/properties/commonServices.database.properties

cd ${WORKSPACE}/Apps/InternalApp/InternalApp
sed -i "s/elis2datadevelop/elis2data${DATABASE}develop/g" src/main/resources/META-INF/properties/commonServices.database.properties
sed -i "s/activitidatadevelop/activitidata${DATABASE}develop/g" src/main/resources/META-INF/properties/commonServices.database.properties

cd ${WORKSPACE}/Apps/Backend/ExternalAPI
sed -i "s/elis2datadevelop/elis2data${DATABASE}develop/g" src/main/resources/META-INF/properties/externalapi.properties

find ${WORKSPACE}/Apps/Database/oracle/schemas -type f -exec sed -i "s/\${branchName}/${DATABASE}\${branchName}/g" {} +

find ${WORKSPACE}/Database-${PROD_VERSION}/oracle/schemas -type f -exec sed -i "s/\${branchName}/${DATABASE}\${branchName}/g" {} +
find ${WORKSPACE}/Database-${ARTIFACT_VERSION}/oracle/schemas -type f -exec sed -i "s/\${branchName}/${DATABASE}\${branchName}/g" {} +
find ${WORKSPACE}/Database-${PROD_VERSION}/oracle/toggle -type f -exec sed -i "s/\${branchName}/${DATABASE}\${branchName}/g" {} +
find ${WORKSPACE}/Database-${ARTIFACT_VERSION}/oracle/toggle -type f -exec sed -i "s/\${branchName}/${DATABASE}\${branchName}/g" {} +


cp /opt/.g/gradle.properties ./gradle.properties
