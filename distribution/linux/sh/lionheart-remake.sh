#!/bin/sh

export VERSION=%%APPV%%
export PARAM="-server -splash:splash.png -jar lionheart-pc-"$VERSION".jar"

cd data
jre_linux-x86_64/bin/java $PARAM
