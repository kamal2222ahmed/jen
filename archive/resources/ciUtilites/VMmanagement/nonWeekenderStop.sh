#!/bin/bash
rm -rf $WORKSPACE/weekenders.txt
fileList=$WORKSPACE/weekenders.txt
pwd
export https_proxy=http://10.76.225.15:80
aws ec2 describe-instances --filter "Name=tag:weekender,Values=true" | grep InstanceId | sed 's;                    \"InstanceId\": \";;g' | sed 's;",;;g' > $WORKSPACE/weekenders.txt
echo "I got here #1"
while read p; do
list="$list $p"
done<$WORKSPACE/weekenders.txt
echo $list

numberOfWeekenders=`cat $WORKSPACE/weekenders.txt| wc -l`

echo "about to stop $numberOfWeekenders"
sleep 5
aws ec2 stop-instances --instance-ids $list
