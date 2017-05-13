knife search 'chef_environment:DEMOLOCKBOX' -a fqdn | grep fqdn | cut -f2 -d':'
knife ssh "chef_environment:DEMOLOCKBOX" -C 1 -a ipaddress -x chef -i "${jenkins_id}" "sudo passwd -d ec2-user"
knife ssh "chef_environment:DEMOLOCKBOX" -C 1 -a ipaddress -x chef -i "${jenkins_id}" "sudo passwd -d socscan"
knife ssh "chef_environment:DEMOLOCKBOX" -C 1 -a ipaddress -x chef -i "${jenkins_id}" "sudo passwd -d chef"
knife ssh "chef_environment:DEMOLOCKBOX AND role:*tomcat-nonpm" -C 1 -a ipaddress -x chef -i "${jenkins_id}" "sudo passwd -d tomcat7"

