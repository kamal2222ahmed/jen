set +x
human=`echo ${emailAddress} | tr '[:upper:]' '[:lower:]'`
export https_proxy=http://10.76.225.15:80

if [ $human == "anonymous" ];
then
   echo "YOU NEED TO enter your correct email address: eg: jason.r.you@uscis.dhs.gov!!!!"
   exit 1
fi
instance=`aws ec2 describe-instances --filter "Name=tag:email,Values=${human}" "Name=tag:type,Values=ELIS2LinuxDevVM" | grep InstanceId | sed 's;                    \"InstanceId\": \";;g' | sed 's;",;;g'`
ipaddress=`aws ec2 describe-instances --filter "Name=tag:email,Values=${human}" "Name=tag:type,Values=ELIS2LinuxDevVM" | grep -m 1 PrivateDnsName | sed 's;                    \"PrivateDnsName\": \";;g' | sed 's;",;;g'| sed 's;.ec2.internal;;g'| sed 's;ip-;;g' | sed 's;-;.;g'`
code=0
echo "about to $STATE your Linux machine $human - $ipaddress"
sleep 20

if [ $STATE = "START" ];
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
       echo "stopping machine $human"
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
       echo "stopping machine $human"
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
       echo "starting machine $human"
       sleep 5
       if [ $code == "16" ];
       then
          instanceState="true"
          echo "Machine is now running = $code"
       fi
   done

fi
