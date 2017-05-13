set +x

#token=FT134
token=FT300
echo $token

rm -f ${WORKSPACE}/env.properties
echo ENVIRONMENT=${token} >> ${WORKSPACE}/env.properties
