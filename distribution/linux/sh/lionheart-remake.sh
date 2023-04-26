#!/bin/bash

VERSION=%%INPUT_APPV%%
PARAM="-server -splash:splash.png -jar lionheart-pc-"$VERSION".jar"

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"/data; jre_linux-x86_64/bin/java ${PARAM}
