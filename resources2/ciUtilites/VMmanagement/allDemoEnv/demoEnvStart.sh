#!/bin/bash

rm -rf $WORKSPACE/demoEnv.txt
fileList=$WORKSPACE/demoEnv.txt
pwd
export https_proxy=http://10.76.225.15:80
aws ec2 describe-instances --filter "Name=tag:shutdown,Values=true" | grep InstanceId | sed 's;                    \"InstanceId\": \";;g' | sed 's;",;;g' > $fileList

while read p; do
list="$list $p"
done<$fileList
echo $list
numberOfaadss=`cat $fileList| wc -l`
echo "about to start $numberOfdemoEnv"
sleep 20
aws ec2 start-instances --instance-ids $list
