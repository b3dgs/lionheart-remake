#!/bin/sh

###################### PROPERTIES ######################
export VERSION=1.4.0
export FOLDER="lionheart-remake-"$VERSION"_linux-x86_64"
export DEST=build/$FOLDER
export M2_HOME=/home/djthunder/apache-maven-3.9.1
export JAVA_HOME=/home/djthunder/jdk-17.0.6+10
########################################################

################### Compile project ####################
echo --------------------------------------------- Compile game ---------------------------------------------
$M2_HOME/bin/mvn clean install -f ../../lionheart-parent/pom.xml -P pc,sign
echo --------------------------------------------------------------------------------------------------------

echo -------------------------------------------- Compile editor --------------------------------------------
$M2_HOME/bin/mvn clean verify -f ../../lionheart-editor-parent/pom.xml -P release
echo --------------------------------------------------------------------------------------------------------
########################################################

###################### Copy data #######################
rm -r -f build/
echo ----------------------------------------------- Copy data ----------------------------------------------
mkdir build
mkdir $DEST
mkdir $DEST/data
mkdir $DEST/data/assets
echo -------------------------------------------- Copy splash
cp ../data/splash.png $DEST/data
echo -------------------------------------------- Copy certificate
cp ../data/b3dgs.cer $DEST/data
echo -------------------------------------------- Copy properties
cp ../data/.lionengine $DEST/data
echo -------------------------------------------- Copy doc
cp -R ../doc $DEST
echo -------------------------------------------- Copy assets
cp -R ../../lionheart-assets/src/main/resources/com/b3dgs/lionheart/. $DEST/data/assets
echo -------------------------------------------- Remove levels rip
find . -name 'stage*.png' -delete
echo -------------------------------------------- Copy jar
cp "../../lionheart-pc/target/lionheart-pc-"$VERSION".jar" $DEST/data
cp "../../lionheart-pc/target/lionheart-pc-"$VERSION".jar.asc" $DEST/data
cp "../../lionheart-pc/target/lionheart-pc-"$VERSION".jar" $DEST/data
echo -------------------------------------------- Copy scripts
cp sh/lionheart-remake.sh $DEST
cp sh/configure.sh $DEST
cp sh/profile.sh $DEST
echo --------------------------------------------------------------------------------------------------------
########################################################

####################### Make JRE #######################
echo --------------------------------------------- Generate JRE ---------------------------------------------
cd jre
./create_jre.sh
cd ..
mv jre/jre_linux-x86_64 $DEST/data/jre_linux-x86_64
echo --------------------------------------------------------------------------------------------------------
########################################################

####################### Make ZIP #######################
echo ---------------------------------------------- Create ZIP ----------------------------------------------
cd build
tar -cf - $FOLDER | xz -1ze -T0 >$FOLDER.tar.xz
rm -r -f $FOLDER
cd ..
echo --------------------------------------------------------------------------------------------------------
########################################################

###################### Make Editor #####################
echo ------------------------------------------- Copy editor data -------------------------------------------
mkdir $DEST
mkdir $DEST/data
mkdir $DEST/data/assets
mkdir $DEST/data/editor
mkdir $DEST/data/editor/plugins
cp -R ../../com.b3dgs.lionheart.editor.product/target/products/com.b3dgs.lionheart.editor.product/linux/gtk/x86_64/. $DEST/data/editor

echo -------------------------------------------- Copy levels rip
cd ../../lionheart-assets/src/main/resources/com/b3dgs/lionheart
find . -type f -name "stage*.png" | xargs -i -exec cp --parents {} ../../../../../../../distribution/linux/$DEST/data/assets/
cd ../../../../../../../distribution/linux

echo -------------------------------------------- Create ZIP editor
cd build
tar -cf - $FOLDER | xz -1ze -T0 >$FOLDER"_editor.tar.xz"
rm -r -f $FOLDER
echo --------------------------------------------------------------------------------------------------------
########################################################
