set VERSION=1.3.0
set PARAM=-Xverify:none -server -splash:splash.png -jar lionheart-pc-%VERSION%.jar

if %PROCESSOR_ARCHITECTURE% == AMD64 (set ARCH=64) else (set ARCH=32)

set path=%~dp0data\jre%ARCH%\bin\;%path%
if "%LaunchPath%" == "" (cd data) else (cd %LaunchPath%\data)
start "" javaw %PARAM%