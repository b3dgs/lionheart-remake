#!/bin/bash

VERSION=%%INPUT_APPV%%
PARAM="-server -splash:splash.png -cp lionheart-pc-"$VERSION".jar com.b3dgs.lionheart.Launcher"

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"/data; jre_linux-x86_64/bin/java ${PARAM}
