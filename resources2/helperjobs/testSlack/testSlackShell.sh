#!/opt/chef/embedded/bin/ruby
require "rubygems"
require "slack-ruby-client"
#get the latest ELIS2 Production Version number
channel = `echo $CHANNEL`.strip
message_text = `echo $MESSAGE_TEXT`.strip
username = `echo $USERNAME`.strip
slackIntToken = `echo $SLACK_INT_TOKEN`.strip
color = `echo $COLOR`.strip
message_title = `echo $TITLE`.strip
build_url = `echo $BUILDURL`.strip
client = Slack::Web::Client.new(proxy: 'http://10.76.225.15:80', user_agent: 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chromium/41.0.2228.0 Safari/537.36 (compatible; Windows NT 6.1; WOW64; C15IE72011A)', token: "#{slackIntToken}")
client.auth_test

client.chat_postMessage(channel: "#{channel}", username: "#{username}", icon_url: 'https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTP8eB5Syt4lxvtFmWA8CPHfIf3wJiDM4-V1UPZHrSqKXwVH37o-g', attachments: [
  {
    fallback: "#{message_text}",
    pretext: "",
    title: "#{message_title}",
    title_link: "#{build_url}",
    text: "#{message_text}",
    color: "#{color}"
  }
  ]
 )
