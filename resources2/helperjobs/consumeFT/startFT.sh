#!/bin/bash
export https_proxy=http://preproxy.uscis.dhs.gov:80
export http_proxy=http://preproxy.uscis.dhs.gov:80

aws ec2 describe-instances --filter Name=tag:op_env,Values=${ENVIRONMENT} > ${WORKSPACE}/${ENVIRONMENT}.json

cd ${WORKSPACE}
instance_id1=`cat ${WORKSPACE}/${ENVIRONMENT}.json | jq '.Reservations[0].Instances[0].InstanceId' | sed 's;";;g'`
instance_id2=`cat ${WORKSPACE}/${ENVIRONMENT}.json | jq '.Reservations[1].Instances[0].InstanceId' | sed 's;";;g'`


    echo "starting up ${ENVIRONMENT} - instances $instance_id1 & $instance_id2"
 
    aws ec2 start-instances --instance-ids $instance_id1 $instance_id2
    sleep 30
    aws ec2 describe-instance-status --instance-ids $instance_id1 $instance_id2
    
    sleep 300
    
aws ec2 describe-instance-status --instance-ids $instance_id1 > ${WORKSPACE}/$instance_id1.json
aws ec2 describe-instance-status --instance-ids $instance_id2 > ${WORKSPACE}/$instance_id2.json

status1=`cat ${WORKSPACE}/$instance_id1.json | jq '.InstanceStatuses[0].SystemStatus.Status' | sed 's;";;g'`
status2=`cat ${WORKSPACE}/$instance_id1.json | jq '.InstanceStatuses[0].InstanceStatus.Status' | sed 's;";;g'`
status3=`cat ${WORKSPACE}/$instance_id2.json | jq '.InstanceStatuses[0].SystemStatus.Status' | sed 's;";;g'`
status4=`cat ${WORKSPACE}/$instance_id2.json | jq '.InstanceStatuses[0].InstanceStatus.Status' | sed 's;";;g'`

echo "Status checks are '$status1 $status2 $status3 $status4'"

if [[ $status1 == "ok" && $status2 == "ok" && $status3 == "ok" && $status4 == "ok" ]]
  then
    echo "${ENVIRONMENT} PASSED the AWS console status check"
    echo "Waiting one min for oracle to start up"
    sleep 60
  else
    echo "${ENVIRONMENT} FAILED the AWS console status check"
fi

