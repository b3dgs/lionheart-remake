#!/bin/sh

export VERSION=%%APPV%%
export PARAM="-server -splash:splash.png -cp lionheart-pc-"$VERSION".jar com.b3dgs.lionheart.Launcher"

cd data
jre_linux-x86_64/bin/java $PARAM
