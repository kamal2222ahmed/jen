#!/bin/bash
export https_proxy=http://10.76.225.15:80
export http_proxy=http://10.76.225.15:80

aws ec2 describe-instances --filter Name=tag:op_env,Values=${ENVIRONMENT} > ${WORKSPACE}/${ENVIRONMENT}.json

cd ${WORKSPACE}
instance_id1=`cat ${WORKSPACE}/${ENVIRONMENT}.json | jq '.Reservations[0].Instances[0].InstanceId' | sed 's;";;g'`
instance_id2=`cat ${WORKSPACE}/${ENVIRONMENT}.json | jq '.Reservations[1].Instances[0].InstanceId' | sed 's;";;g'`

if [ $STATE == OFF ]; then
    echo "shutting down ${ENVIRONMENT} - instances $instance_id1 & $instance_id2"
    sleep 25
    aws ec2 stop-instances --instance-ids $instance_id1 $instance_id2
    sleep 65
    aws ec2 describe-instance-status --instance-ids $instance_id1 $instance_id2
    sleep 15
    echo "${ENVIRONMENT} instances stopped"
fi

if [ $STATE == ON ]; then
    echo "starting up ${ENVIRONMENT} - instances $instance_id1 & $instance_id2"
    sleep 25
    aws ec2 start-instances --instance-ids $instance_id1 $instance_id2
    sleep 65
    aws ec2 describe-instance-status --instance-ids $instance_id1 $instance_id2
    sleep 15
fi
