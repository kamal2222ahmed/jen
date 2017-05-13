#!/bin/bash
export https_proxy=http://preproxy.uscis.dhs.gov:80
export http_proxy=http://preproxy.uscis.dhs.gov:80

TEAM=`echo ${BRANCH%%-*} | tr '[:upper:]' '[:lower:]' | grep -v '_'`
TEAM2=`echo ${BRANCH%%_*} | tr '[:upper:]' '[:lower:]'| grep -v '-'`

echo "TEAM= $TEAM"
echo "TEAM2= $TEAM2"

if [ -z $TEAM ];
then
   TEAM=$TEAM2
else
  echo "$TEAM"
fi

echo $TEAM

instance_id=""
case $TEAM in
 "skynet")
    instance_id="i-2cd027af"
    ;;
 "aetas")
    instance_id="i-09d82f8a"
    ;;
 "synergy")
    instance_id="i-17d92e94"
    ;;
 "olmec")
    instance_id="i-3d8b7cbe"
    ;;
 "aria51")
    instance_id="i-478671c4"
    ;;
 "initech")
    instance_id="i-4b8275c8"
    ;;
 "allianc")
    instance_id="i-4f996ecc"
    ;;
 "sparta")
    instance_id="i-65dd2ae6"
    ;;
 "atomics")
    instance_id="i-6f8572ec"
    ;;
 "roti")
    instance_id="i-91d02712"
    ;;
 "mib")
    instance_id="i-a28e7921"
    ;;
 "ion")
    instance_id="i-a48f7827"
    ;;
 "protean")
    instance_id="i-c1ea1d42"
    ;;
 "modulus")
    instance_id="i-cb8e7948"
    ;;
 "huns")
    instance_id="i-d6847355"
    ;;
 "nuka")
    instance_id="i-642204f9"
    ;;
 "cobra")
    instance_id="i-642204f9"
    ;;
 "nok")
    instance_id="i-e9220474"
    ;;
 "carbon")
    instance_id="i-057e3b54bae2520d4"
    ;;
  *)
    instance_id="NULL"
    echo "You need to create a jira ticket if you are a new team and assign it to Skynet. Also if you didn't follow the branch convention you will get this error"
    exit 1
esac


if [ $STATE == DISABLE ]; then
    echo "shutting down $TEAM ft - instance $instance_id"
    sleep 25
    aws ec2 stop-instances --instance-ids $instance_id
    sleep 65
    aws ec2 describe-instance-status --instance-ids $instance_id
    sleep 15
    echo "$TEAM instance stopped"
fi

if [ $STATE == ENABLE ]; then
    echo "starting up $TEAM ft - instance $instance_id"
    sleep 25
    aws ec2 start-instances --instance-ids $instance_id
    sleep 65
    aws ec2 describe-instance-status --instance-ids $instance_id
    sleep 15
fi
