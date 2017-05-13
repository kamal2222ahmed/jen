branch=`echo ${TEAM} | tr '[:upper:]' '[:lower:]'`


array=('allianc' 'aria51' 'aetas' 'atomics' 'carbon' 'huns' 'initech' 'ion' 'mib' 'modulus' 'nok' 'olmec' 'protean' 'roti' 'skynet' 'sparta' 'synergy' 'nuka' 'cobra');

for i in ${array[@]} ; do
  if [ $i == $branch ]; then
    exit 0
  fi
done
  echo "BRANCH NAME DOES NOT MATCH NAMING CONVENTION!!"
  echo "BRANCH must be '<team name>-' E.G Skynet-SKY9999 case insensitive"
  exit 1
