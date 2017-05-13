# ELIS (N400 lockbox case submission) - AETAS_JM_20Thread_1Minute_N400LockboxCaseSubmission.jmx

mkdir -p $WORKSPACE/results/
mkdir -p $WORKSPACE/uneditedresults/

sh /home/jmeter/apache-jmeter-3.0/bin/jmeter.sh -n -t "$WORKSPACE/ELIS/Scripts/AETAS_JM_20Thread_1Minute_N400LockboxCaseSubmission.jmx" -l "$WORKSPACE/uneditedresults/AETAS_JM_20Thread_1Minute_N400LockboxCaseSubmission_$BUILD_NUMBER.jtl" -q "$WORKSPACE/ELIS/Scripts/ppe1.properties"
