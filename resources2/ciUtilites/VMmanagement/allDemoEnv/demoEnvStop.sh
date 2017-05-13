#!/bin/bash
rm -rf $WORKSPACE/demoEnv.txt
fileList=$WORKSPACE/demoStop.txt
pwd
export https_proxy=http://10.76.225.15:80
aws ec2 describe-instances --filter "Name=tag:shutdown,Values=true" | grep InstanceId | sed 's;                    \"InstanceId\": \";;g' | sed 's;",;;g' > $WORKSPACE/demoEnv.txt

while read p; do
list="$list $p"
done<$WORKSPACE/demoEnv.txt
echo $list

numberOfaadss=`cat $WORKSPACE/demoEnv.txt| wc -l`

echo "about to stop $numberOfdemoEnv"
sleep 5
aws ec2 stop-instances --instance-ids $list
