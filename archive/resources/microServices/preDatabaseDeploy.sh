set x-
export http_proxy=http://10.76.225.15:80
export https_proxy=http://10.76.225.15:80
set x+
sed -i 's;dbHost=localhost;dbHost=el2-ftits-idb2.c8rl98oavklx.us-east-1.rds.amazonaws.com;g' ${WORKSPACE}/src/main/resources/application.properties
sed -i 's;databaseHost=localhost;databaseHost=el2-ftits-idb2.c8rl98oavklx.us-east-1.rds.amazonaws.com;g' ${WORKSPACE}/gradle.properties
sed -i 's;server.port=80;server.port=9191;g' ${WORKSPACE}/src/main/resources/application.properties

cat ${WORKSPACE}/src/main/resources/application.properties

cat ${WORKSPACE}/gradle.properties

