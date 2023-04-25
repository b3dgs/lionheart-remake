#!/bin/bash

VERSION=%%INPUT_APPV%%
PARAM="-server -splash:splash.png -jar lionheart-pc-"$VERSION".jar"

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd "${DIR}"

cd data
jre_linux-x86_64/bin/java ${PARAM}
