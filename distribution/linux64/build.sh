#!/bin/sh

export VERSION=1.3.0
export FOLDER="lionheart-remake-"$VERSION"_linux64"
export DEST=build/$FOLDER

echo Compile project
#mvn clean install -f ../../lionheart-parent/pom.xml -P pc-signed

rm -r -f build/

echo Copy data
mkdir build
mkdir $DEST
mkdir $DEST/data
cp -R ../data $DEST
cp -R ../doc $DEST
cp "../../lionheart-pc/target/lionheart-pc-"$VERSION".jar" $DEST/data
cp "../../lionheart-pc/target/lionheart-pc-"$VERSION".jar.asc" $DEST/data
cp "../../lionheart-pc/target/lionheart-pc-"$VERSION".jar" $DEST/data
cp sh/lionheart-remake.sh $DEST
cp sh/configure.sh $DEST
cp sh/profile.sh $DEST

echo Generate JRE
cd jre
./create_jre.sh
cd ..
mv jre/jre64 $DEST/data/jre64

cd build
echo Create Zip
tar -cf - $FOLDER | xz -1ze -T0 >$FOLDER.tar.xz
