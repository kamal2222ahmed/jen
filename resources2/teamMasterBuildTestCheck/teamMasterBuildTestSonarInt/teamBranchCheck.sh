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

if [ -z $TEAM ];
then
   echo "This pipeline is for pre-testing branches before you put them into the main ci pipeline. Please refrain from pushing CI- or CI501- branches to the Team Master Build Test check Build flow. Branch $BRANCH is not for this pipeline. If you are a new team you need to make yourself formally known to SKYNET so that we can create a TEAM FT for you| Code-TeamEmpty"
   exit 1
fi

if [ $TEAM == "ci" ] || [ $TEAM == "ci501" ];
then
   echo "This pipeline is for pre-testing branches before you put them into the main ci pipeline. Please refrain from pushing CI- or CI501- branches to the Team Master Build Test check Build flow. Branch $BRANCH is not for this pipeline. If you are a new team you need to make yourself formally known to SKYNET so that we can create a TEAM FT for you"
   exit 1
fi

array=('allianc' 'aria51' 'aetas' 'atomics' 'huns' 'initech' 'ion' 'mib' 'modulus' 'nok' 'olmec' 'protean' 'roti' 'skynet' 'sparta' 'synergy' 'nuka', 'cobra');
for i in ${array[@]} ; do
  if [ $i == $TEAM ]; then
    exit 0
  fi
done

