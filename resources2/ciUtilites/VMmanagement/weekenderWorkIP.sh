#!/bin/bash
export https_proxy=http://10.76.225.15:80
set +x
human=`echo ${emailAddress} | tr '[:upper:]' '[:lower:]'`

if [ $human == "anonymous" ];
then
   echo "YOU NEED TO enter your correct email address: eg: jason.r.you@uscis.dhs.gov!!!!"
   exit 1
fi

if [ -n "${IPAddress}" ];
then
  instance=`aws ec2 describe-instances --filter "Name=tag:email,Values=${human}" "Name=private-ip-address,Values=${IPAddress}" | grep InstanceId | sed 's;                    \"InstanceId\": \";;g' | sed 's;",;;g'`
  ipaddress=${IPAddress}
else
  instance=`aws ec2 describe-instances --filter "Name=tag:email,Values=${human}" "Name=tag:type,Values=ELIS2WindowsDeveloperMachine" | grep InstanceId | sed 's;                    \"InstanceId\": \";;g' | sed 's;",;;g'`
  ipaddress=`aws ec2 describe-instances --filter "Name=tag:email,Values=${human}" "Name=tag:type,Values=ELIS2WindowsDeveloperMachine" | grep -m 1 PrivateDnsName | sed 's;                    \"PrivateDnsName\": \";;g' | sed 's;",;;g'| sed 's;.ec2.internal;;g'| sed 's;ip-;;g' | sed 's;-;.;g'`
fi


aws ec2 create-tags --resources $instance --tags "Key=weekender,Value=true"
