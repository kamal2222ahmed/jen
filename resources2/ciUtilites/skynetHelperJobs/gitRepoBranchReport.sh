#!/bin/bash

for k in `git branch -r|awk '{print $1}'`;
#do echo `git show --pretty=format:"%Cgreen%ci %Cblue%cr %Cred%cn %Creset" $k|head -n 1`\\t$k;
do echo `git show --pretty=format:"%ci %cr %h %cn " $k|head -n 1`$k;
done|sort -r

echo "If you're unable to
