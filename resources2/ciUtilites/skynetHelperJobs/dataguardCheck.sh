#/bin/bash
 function deny_exit
{
	echo "LAST COMMITTER($1) NOT IN DATAGUARD"
	exit 1
}
cd Apps
printf "shawn.d.lassiter@uscis.dhs.gov\nphani.muthyam@uscis.dhs.gov\njeffrey.e.stahl@uscis.dhs.gov\nsolomon.john@uscis.dhs.gov\nadedoyin.a.akinnurun@uscis.dhs.gov" > dataguard
git log --pretty="%H" Database > dbHashes
git cherry origin/master | gawk '/+/{print $2}' > cherryHashes
last=`comm -23 cherryHashes dbHashes | tail -n1`
echo "LAST_DATABASE_HASH: $last"

committer=`git log --pretty="%ce" $last | head -n1`

echo "LAST COMMITER:$committer"
grep -i $committer dataguard || deny_exit $committer
