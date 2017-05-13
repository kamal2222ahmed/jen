find ${WORKSPACE}/Database-${PROD_VERSION}/oracle/schemas -type f \( -iname "*.sql" \) -exec sed -i "s/\${branchName}/${TEAM}\${branchName}/g" {} +
find ${WORKSPACE}/Database-${ARTIFACT_VERSION}/oracle/schemas -type f \( -iname "*.sql" \) -exec sed -i "s/\${branchName}/${TEAM}\${branchName}/g" {} +
branchName=develop
find ${WORKSPACE}/Database-${PROD_VERSION}/oracle/toggle -type f \( -iname "*.sql" \) -exec sed -i "s/${branchName}/${TEAM}${branchName}/g" {} +
find ${WORKSPACE}/Database-${ARTIFACT_VERSION}/oracle/toggle -type f \( -iname "*.sql" \) -exec sed -i "s/${branchName}/${TEAM}${branchName}/g" {} +
