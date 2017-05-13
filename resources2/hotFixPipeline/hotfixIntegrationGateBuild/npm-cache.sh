#!/bin/bash
#***********************************************
npm config set proxy "http://proxy.apps.dhs.gov:80"

cd ${WORKSPACE}/Apps/InternalApp/InternalApp/
npm-cache install

cd ${WORKSPACE}/Apps/Shared/Metis/
npm-cache install
