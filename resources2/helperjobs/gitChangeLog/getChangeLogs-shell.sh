rm -f changelog.txt
git log --no-merges  --pretty="%s - %ae" ${FirstTag}...${LastTag} | sort  >> changelog.txt
