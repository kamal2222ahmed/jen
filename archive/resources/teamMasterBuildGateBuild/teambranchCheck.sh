#!/bin/bash

TEAM=`echo ${GIT_BRANCH_NAME%%-*} | tr '[:upper:]' '[:lower:]' | grep -v '_'`
TEAM2=`echo ${GIT_BRANCH_NAME%%_*} | tr '[:upper:]' '[:lower:]'| grep -v '-'`

echo "TEAM= $TEAM"
echo "TEAM2= $TEAM2"


if [ -z $TEAM ];
then
   TEAM=$TEAM2
else
  echo "$TEAM"
fi

echo $TEAM

if [ $TEAM == "ci" ] || [ $TEAM == "ci501" ] || [ $TEAM == "" ];
then
   echo "This pipeline is for pre-testing branches before you put them into the main ci pipeline. Please refrain from pushing CI- or CI501- branches to the Team Master Build Test check Build flow. Branch $BRANCH is not for this pipeline"
   exit 1
fi

if [ $TEAM == "alliance" ];
then
  TEAM="allianc"
fi


branch=`echo $TEAM | tr '[:upper:]' '[:lower:]'`

array=('allianc' 'aria51' 'aetas' 'atomics' 'carbon' 'huns' 'initech' 'ion' 'mib' 'modulus' 'nok' 'olmec' 'protean' 'roti' 'skynet' 'sparta' 'synergy' 'nuka' 'cobra');

for i in ${array[@]} ; do
  if [ $i == $TEAM ]; then
    exit 0
  fi
done
echo "Branch name was not accepted"
exit 1
