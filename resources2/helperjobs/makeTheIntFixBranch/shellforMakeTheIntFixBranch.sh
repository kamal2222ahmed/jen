git checkout ${RELEASE_CANDIDATE}
git checkout -b CI-IntFix-${RELEASE_CANDIDATE} || true
git push -u git@git.uscis.dhs.gov:uscis/elis-apps.git CI-IntFix-${RELEASE_CANDIDATE}
