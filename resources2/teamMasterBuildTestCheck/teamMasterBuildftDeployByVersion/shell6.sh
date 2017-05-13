knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" -C 1 -a ipaddress -x chef "sudo chef-client -o utilities::stop_tomcats"
knife ssh "chef_environment:${ENVIRONMENT} AND role:*tomcat*" -C 1 -a ipaddress -x chef "sudo chef-client -o tomcat::remove_tomcat"
