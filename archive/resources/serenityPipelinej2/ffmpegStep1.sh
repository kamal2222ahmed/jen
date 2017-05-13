pkill ffmpeg || (exit 0);

knife exec ChefRepo/utils/buildGradleProperties.krb ${ENVIRONMENT} ${WORKSPACE}/Apps/Tests/Serenity/gradle.properties -c /home/jenkins/.chef/knife.rb

echo "DISPLAY:${DISPLAY}"
echo "USER:${USER}"
echo "PWD:$(pwd)"
#export DISPLAY=:98

echo -n "" > stop.log
chmod 666 stop.log
export BUILD_ID="DontKillMe"
timeout -s SIGINT 90m ffmpeg -strict experimental -an -y -s 1024x768 -r 5 -f x11grab -i ${DISPLAY} -b 360k -bt 416k -vcodec libx264 -vpre slow -profile none -pix_fmt yuv420p -f mp4 serenity-recording.mp4 < stop.log > /dev/null 2> ffmpeg.log &
echo $(ps aux | grep ffmpeg)
