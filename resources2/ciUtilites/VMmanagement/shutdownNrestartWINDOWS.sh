set +x

human=`echo ${emailAddress} | tr '[:upper:]' '[:lower:]'`
if [ $human == "" ];
then
   echo "YOU NEED TO enter your correct email address: eg: jason.r.you@uscis.dhs.gov!!!!"
   exit 1
fi

export https_proxy=http://10.236.225.15:80

#get the instance id so that we can start or stop the instance

instance=`aws ec2 describe-instances --filter "Name=private-ip-address,Values=${IPaddress}" | grep InstanceId | sed 's;                    \"InstanceId\": \";;g' | sed 's;",;;g'`
ipaddress=${IPaddress}



code=0
echo "about to $STATE your AWS Instance  $human - $ipaddress"
sleep 20

if [ $STATE == "START" ];
then
   aws ec2 start-instances --instance-ids=$instance
   aws ec2 start-instances --instance-ids=$instance
   instanceState="false"
   while [ $instanceState == "false" ]
   do
       code=`aws ec2 describe-instances --filters --instance-id=$instance | grep -m 1 '\"Code\"' | sed 's;                        "Code": ;;g' | sed 's;,;;g'`
       echo "code=$code"
       echo "starting machine $human"
       sleep 5
       if [ $code == "16" ];
       then
          instanceState="true"
          echo "Machine is now running = $code"
       fi
   done
fi

if [ $STATE == "STOP" ];
then
   aws ec2 stop-instances --instance-ids=$instance
   aws ec2 stop-instances --instance-ids=$instance
   instanceState="false"
   while [ $instanceState == "false" ]
   do
       code=0
       code=`aws ec2 describe-instances --filters --instance-id=$instance | grep -m 1 '\"Code\"' | sed 's;                        "Code": ;;g' | sed 's;,;;g'`
       echo "code=$code"
       echo "stopping machine $human - retrying status in 5 seconds"
       sleep 5
       if [ $code == "80" ];
       then
          instanceState="true"
          echo "Machine is in the stop state = $code"
       fi
   done
fi

if [ $STATE == "REBOOT" ];
then
   instanceState="false"
   aws ec2 stop-instances --instance-ids=$instance
   aws ec2 stop-instances --instance-ids=$instance
   while [ $instanceState == "false" ]
   do
       code=0
       code=`aws ec2 describe-instances --filters --instance-id=$instance | grep -m 1 '\"Code\"' | sed 's;                        "Code": ;;g' | sed 's;,;;g'`
       echo "code=$code"
       echo "stopping machine $human - retrying status in 5 seconds"
       sleep 5
       if [ $code == "80" ];
       then
          instanceState="true"
          echo "Machine is in the stop state = $code"
       fi
   done

   aws ec2 start-instances --instance-ids=$instance
   aws ec2 start-instances --instance-ids=$instance
   instanceState="false"
   while [ $instanceState == "false" ]
   do
       code=`aws ec2 describe-instances --filters --instance-id=$instance | grep -m 1 '\"Code\"' | sed 's;                        "Code": ;;g' | sed 's;,;;g'`
       echo "code=$code"
       echo "starting machine $human - retrying status in 5 seconds"
       sleep 5
       if [ $code == "16" ];
       then
          instanceState="true"
          echo "Machine is now running = $code"
       fi
   done

fi
