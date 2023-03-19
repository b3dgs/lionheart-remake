#!/bin/sh

export VERSION=1.3.0
export PARAM="-server -splash:splash.png -jar lionheart-pc-"$VERSION".jar"

cd data
jre64/bin/java $PARAM
