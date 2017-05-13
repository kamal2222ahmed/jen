#!/bin/bash

#Encrypt and upload certificate to desired bucket 
/usr/bin/aws s3 cp ${WORKSPACE}/Certificate_to_Upload  s3://${S3_BUCKET}/services/certificates/${S3_SUBFOLDER}/${Certificate_to_Upload} --sse aws:kms --sse-kms-key-id 2086b824-f7bb-45cc-b54d-5780e42d2e55

#Add expiration date tag to certifcate object previously uploaded 
/usr/bin/aws s3api put-object-tagging --bucket ${S3_BUCKET} --key services/certificates/${S3_SUBFOLDER}/${Certificate_to_Upload} --tagging  'TagSet=[{Key=Environment,Value='$ENVIRONMENT_TAG'},{Key=Expiration Date,Value='$EXPIRATION_DATE_TAG'}]'

#Confirm certificate upload
/usr/bin/aws s3 ls s3://${S3_BUCKET}/services/certificates/${S3_SUBFOLDER}  --recursive --human-readable --summarize
