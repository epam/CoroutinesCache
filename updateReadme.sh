#!/bin/bash
# updateReadme.sh

# Updates CoroutineCache versions in Readme.md

tmp=`grep -e "appVersionName=[0-9|.]*" gradle.properties`
versionName=`expr substr $tmp 16 20`
echo "New version is $versionName"

sed -e "s/coroutinecache:[0-9|.]*'/coroutinecache:$versionName'/" -e "s/<version>[0-9|.]*<\/version>/<version>$versionName<\/version>/" Readme.md > Readme.md.tmp
mv Readme.md.tmp Readme.md