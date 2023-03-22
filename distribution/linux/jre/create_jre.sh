#!/bin/sh

export JAVA_HOME=/home/djthunder/jdk-17.0.6+10
export param="--compress=2 --strip-debug --no-header-files --no-man-pages --add-modules java.base,java.xml,jdk.xml.dom,java.prefs,java.desktop,java.logging"

$JAVA_HOME/bin/jlink $param --output jre_linux-x86_64
