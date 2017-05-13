#ELIS Jmeter Performance Testing to the Left
mkdir -p $WORKSPACE/results/
mkdir -p $WORKSPACE/uneditedresults/
chmod u+rwx $WORKSPACE/performance-testing/General/jmeter2splunk/jmeter2splunk/splunkConversionScript.py
export tcip=`knife search node "chef_environment:${ENVIRONMENT} AND role:tomcat-nonpm" -F json -a ipaddress | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}'`
echo tcip=${tcip} >> ${WORKSPACE}/performance-testing/ELIS/Scripts/ppe1.properties
echo "TOMCAT SERVER IP ADDRESS IS: " ${tcip}

python -u $WORKSPACE/performance-testing/ELIS/Scripts/testrunfile.py --ifile=$WORKSPACE/performance-testing/ELIS/Scripts/testConfiguration.conf --section='DEFAULT2' --target=${tcip} --port='8080'

