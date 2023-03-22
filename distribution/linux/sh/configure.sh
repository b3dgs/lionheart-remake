#!/bin/sh

export VERSION=1.3.0
export PARAM="-server -splash:splash.png -cp lionheart-pc-"$VERSION".jar com.b3dgs.lionheart.Launcher"

cd data
jre_linux-x86_64/bin/java $PARAM
