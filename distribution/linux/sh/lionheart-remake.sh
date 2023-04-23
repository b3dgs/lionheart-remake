#!/bin/bash

export VERSION=%%INPUT_APPV%%
export PARAM="-server -splash:splash.png -jar lionheart-pc-"$VERSION".jar"

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_path"

cd data
jre_linux-x86_64/bin/java $PARAM
