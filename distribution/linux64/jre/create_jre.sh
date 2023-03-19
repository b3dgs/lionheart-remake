#!/bin/sh

export javahome=/home/djthunder/jdk-17.0.6+10
export param="--compress=2 --strip-debug --no-header-files --no-man-pages --add-modules java.base,java.xml,jdk.xml.dom,java.prefs,java.desktop,java.logging"

$javahome/bin/jlink $param --output jre64
