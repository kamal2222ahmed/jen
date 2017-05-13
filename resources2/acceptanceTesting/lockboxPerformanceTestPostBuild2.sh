# creating splunk output.
jmeter2splunk -f "$WORKSPACE/uneditedresults/AETAS_JM_20Thread_1Minute_N400LockboxCaseSubmission_$BUILD_NUMBER.jtl" -c "/home/jmeter/apache-jmeter-3.0/lib/ext/CMDRunner.jar" -o "$WORKSPACE"
