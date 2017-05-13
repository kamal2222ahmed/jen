npm config set proxy "http://proxy.apps.dhs.gov:80"

cd ${WORKSPACE}/Prod/InternalApp/InternalApp/
npm-cache install
cd ${WORKSPACE}/Prod/Shared/Metis/
npm-cache install
