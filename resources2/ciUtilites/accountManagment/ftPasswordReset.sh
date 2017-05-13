knife search 'chef_environment: FT*' -a fqdn | grep fqdn | cut -f2 -d':'
knife ssh "chef_environment:FT*" -C 1 -a ipaddress -x chef "sudo passwd -d ec2-user; sudo passwd -d socscan; sudo passwd -d chef"
knife ssh "chef_environment:FT* AND role:*tomcat-nonpm" -C 1 -a ipaddress -x chef "sudo passwd -d tomcat7"
