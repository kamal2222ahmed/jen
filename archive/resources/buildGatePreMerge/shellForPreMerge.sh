#!/bin/bash +x
BRANCHTOCHECK=$GIT_BRANCH_NAME

ANSWER=`echo $BRANCHTOCHECK | sed -n -e '/^CI-/p'`
ANSWER2=`echo $BRANCHTOCHECK | sed -n -e '/^CI501-/p'`

echo "answer: $ANSWER"
echo "answer2: $ANSWER2"


if [ -z "$ANSWER" ] && [ -z "$ANSWER2" ];
then

echo 'BRANCH NAME DOES NOT MATCH NAMING CONVENTION!!'
exit 1

fi
    
