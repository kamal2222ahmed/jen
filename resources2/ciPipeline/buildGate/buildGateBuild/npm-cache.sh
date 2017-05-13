token=1
rm -f ${WORKSPACE}/deploy.properties
echo DATABASE=${token} >> ${WORKSPACE}/deploy.properties
echo GRADLE_USER_HOME=/opt/.gradle/private-${token} >> ${WORKSPACE}/deploy.properties

sudo mkdir -p ${GRADLE_USER_HOME}
sudo chown -R jenkins:jenkins ${GRADLE_USER_HOME}

#***********************************************
npm config set proxy "http://proxy.apps.dhs.gov:80"

cd ${WORKSPACE}/Apps/InternalApp/InternalApp/
#sudo npm install phantomjs-prebuilt --save-dev
#sudo chown -R jenkins:jenkins .
npm-cache install

#sudo chown -R jenkins:jenkins ~/.npm

#time sudo npm-cache install gulp jshint watch run-sequence del bundle htmlreplace karma-threshold-reporter notify config path@0.12.7 ngGraph fs merge toSingleQuotes environments uglify jshintHtml server plato karma-jasmine karma-chrome-launcher karma-commonjs karma-phantomjs-launcher karma-firefox launcher karma-coverage  karma-ng-html2js-preprocessor eslint karma-eslint &

cd ${WORKSPACE}/Apps/Shared/Metis/
#sudo chown -R jenkins:jenkins .
npm-cache install
#sudo npm install phantomjs-prebuilt --save-dev

#time sudo npm-cache install gulp jshint watch run-sequence karma-threshold-reporter del bundle htmlreplace notify config path@0.12.7 ngGraph fs merge toSingleQuotes environments uglify jshintHtml server plato karma-jasmine karma-chrome-launcher karma-commonjs karma-phantomjs-launcher karma-firefox launcher karma-coverage  karma-ng-html2js-preprocessor eslint karma-eslint &

#wait
