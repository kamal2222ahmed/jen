#deploy ELIS App
knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" -C 1 -a ipaddress -x chef "sudo chef-client -j \"http://${NEXUS}/repository/public/gov/dhs/uscis/elis2/InternalApp/${Version}/InternalApp-${Version}.json\""

