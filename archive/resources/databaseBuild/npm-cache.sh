#!/bin/bash
#***********************************************
cd ${WORKSPACE}/Prod/InternalApp/InternalApp/
time sudo npm-cache install gulp jshint watch runSequence del bundle htmlreplace notify config path ngGraph fs merge toSingleQuotes environments uglify jshintHtml server plato karma-jasmine karma-chrome-launcher karma-commonjs karma-phantomjs-launcher karma-firefox launcher karma-coverage karma-ng-html2js-preprocessor &

cd ${WORKSPACE}/Prod/Shared/Metis/

time sudo npm-cache install gulp jshint watch runSequence del bundle htmlreplace notify config path ngGraph fs merge toSingleQuotes environments uglify jshintHtml server plato karma-jasmine karma-chrome-launcher karma-commonjs karma-phantomjs-launcher karma-firefox launcher karma-coverage karma-ng-html2js-preprocessor &

wait