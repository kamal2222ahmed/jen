#!/bin/sh

array=("ALTER INDEX" "ALTER PROCEDURE" "ALTER FUNCTION" "ALTER PACKAGE" "ALTER SEQUENCE" "ALTER MATERIALIZED VIEW" "ALTER TRIGGER" "ALTER OPERATOR" "ALTER TABLE" "ALTER TYPE" "ALTER VIEW" "DROP CLUSTER" "DROP CONTEXT" "DROP DATABASE LINK" "DROP DIMENSION" "DROP DIRECTORY" "DROP DISKGROUP" "DROP FUNCTION" "DROP INDEX" "DROP JAVA" "DROP LIBRARY" "DROP MATERIALIZED VIEW" "DROP OPERATOR" "DROP OUTLINE" "DROP PACKAGE" "DROP PROCEDURE" "DROP PROFILE" "DROP SEQUENCE" "DROP SYNONYM" "DROP TABLE" "DROP TRIGGER" "DROP TYPE" "DROP VIEW" "FOR UPDATE" "LOCK TABLE" "RENAME" "RENAME" "TRUNCATE")
dir=()
DISRUPTIVE=false
REPO=${WORKSPACE}/Apps
cd ${REPO}
for x in `git diff -p ${PRODUCTION_VERSION}..HEAD --name-only ./Database/oracle/schemas | grep -v "changelog.xml"`; do
  dir+=("$x")
done

echo "PRODUCTION VERSION is ${PRODUCTION_VERSION}"
echo "THESE SQL FILES HAVE CHANGED IN THIS RELEASE..."
echo "#####################################"
for x in ${dir[@]}; do
  echo $x
done
echo "#####################################"

#Checks if changes to the Database project includes any distructive SQL commands
for x in "${dir[@]}"; do
  for i in "${array[@]}"; do
    result=`git diff -p ${PRODUCTION_VERSION}..HEAD ${REPO}/$x |  grep "^+" | grep -i "$i"`
    if [ $? -eq 0 ]; then
      printf "found $i in $x\n"
      DISRUPTIVE=true
    fi
  done
done

ELIS_VERSION_ATRIFACT=`echo ${ELIS_VERSION} | tr '-' '.'`
STATUS_FILE=Database-Deployment-Type-${ELIS_VERSION_ATRIFACT}.txt
NEXUS_TARGET=http://${NEXUS}/repository/releases/gov/dhs/uscis/elis2/Database/${ELIS_VERSION_ATRIFACT}

echo "#####################################"
if [ $DISRUPTIVE = true ]; then
  echo "Changes DOES include disruptive database commands"
  echo "DISRUPTIVE=true" >  ${STATUS_FILE}
  curl -v -o /dev/null -X DELETE ${NEXUS_TARGET}/${STATUS_FILE} --user jenkins:${NexusAdminPassword}
  curl -v -u jenkins:${NexusAdminPassword} --upload-file ${STATUS_FILE} ${NEXUS_TARGET}/${STATUS_FILE}
  ssh -tt -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" ec2-user@10.103.136.38 "sudo -u apache /var/www/sync/releases_sync.sh ${ELIS_VERSION_ATRIFACT}"
  exit 1
else
  echo "Changes DOES NOT include disruptive database commands"
  echo "DISRUPTIVE=false" >  ${STATUS_FILE}
  curl -v -o /dev/null -X DELETE ${NEXUS_TARGET}/${STATUS_FILE} --user jenkins:${NexusAdminPassword}
  curl -v -u jenkins:${NexusAdminPassword} --upload-file ${STATUS_FILE} ${NEXUS_TARGET}/${STATUS_FILE}
  ssh -tt -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" ec2-user@10.103.136.38 "sudo -u apache /var/www/sync/releases_sync.sh ${ELIS_VERSION_ATRIFACT}"
  exit 0
fi
echo "#####################################"
