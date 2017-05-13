# post build process
# Run the jmeter 3.0 dashboard reporting.
#mkdir -p /usr/local/tomcat/default/webapps/ELIS-dashboard
#sh /home/jmeter/apache-jmeter-3.0/bin/jmeter.sh -g "$WORKSPACE/uneditedresults/AETAS_JM_20Thread_1Minute_N400LockboxCaseSubmission_$BUILD_NUMBER.jtl" -o "/usr/local/tomcat/default/webapps/ELIS-dashboard/$BUILD_NUMBER"

#ln -sfn "/usr/local/tomcat/default/webapps/ELIS-dashboard/$BUILD_NUMBER" "/usr/local/tomcat/default/webapps/ELIS-dashboard/latest"

#echo "You can find the results at http://10.103.130.223:8080/ELIS-dashboard/$BUILD_NUMBER/"
