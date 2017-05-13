#!/bin/bash
rm -rf $WORKSPACE/aads.txt
fileList=$WORKSPACE/aads.txt
pwd
export https_proxy=http://10.76.225.15:80
aws ec2 describe-instances --filter "Name=tag:shutdown,Values=true" | grep InstanceId | sed 's;                    \"InstanceId\": \";;g' | sed 's;",;;g' > $WORKSPACE/aads.txt

while read p; do
list="$list $p"
done<$WORKSPACE/aads.txt
echo $list

numberOfaadss=`cat $WORKSPACE/aads.txt| wc -l`

echo "about to stop $numberOfaads"
sleep 5
aws ec2 stop-instances --instance-ids $list
