token=4
rm -f ${WORKSPACE}/deploy.properties
echo DATABASE=${token} >> ${WORKSPACE}/deploy.properties
echo GRADLE_USER_HOME=/opt/.gradle/private-${token} >> ${WORKSPACE}/deploy.properties

sudo mkdir -p ${GRADLE_USER_HOME}
sudo chown -R jenkins:jenkins ${GRADLE_USER_HOME}

#***********************************************
cd ${WORKSPACE}/Apps/InternalApp/InternalApp/
time sudo npm-cache install gulp jshint watch runSequence del bundle htmlreplace notify config path ngGraph fs merge toSingleQuotes environments uglify jshintHtml server plato karma-jasmine karma-chrome-launcher karma-commonjs karma-phantomjs-launcher karma-firefox-launcher karma-coverage karma-ng-html2js-preprocessor eslint karma-eslint &

cd ${WORKSPACE}/Apps/Shared/Metis/

time sudo npm-cache install gulp jshint watch runSequence del bundle htmlreplace notify config path ngGraph fs merge toSingleQuotes environments uglify jshintHtml server plato karma-jasmine karma-chrome-launcher karma-commonjs karma-phantomjs-launcher karma-firefox-launcher karma-coverage karma-ng-html2js-preprocessor eslint karma-eslint &

wait
