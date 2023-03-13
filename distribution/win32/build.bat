@echo off

SET VERSION=1.3.0
SET FOLDER=lionheart-remake-%VERSION%_win32
SET DEST=build\%FOLDER%
SET SIGNTOOL="C:\Program Files (x86)\Windows Kits\10\bin\10.0.20348.0\x64\signtool.exe"
SET PFX=""
SET PASS=""

echo PFX (enter to skip sign)
set/p "PFX=>"
echo %PFX%

echo Password (enter to skip sign)
if %PFX%=="" (echo skip) else (set/p "PASS=>")

echo Compile project
call mvn clean install -f ../../lionheart-parent/pom.xml -P pc-signed

echo Copy data
robocopy "..\data" "%DEST%\data" /E
robocopy "..\doc" "%DEST%\doc" /E
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar" "%DEST%\data"
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar.asc" "%DEST%\data"
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar" "%DEST%\data"
copy "src\Lionheart Remake\Release\Lionheart Remake.exe" "%DEST%"
copy "src\Lionheart Remake Configure\Release\Configure.exe" "%DEST%"
copy "bat\Lionheart Remake.bat" "%DEST%"
copy "bat\Configure.bat" "%DEST%"
copy "bat\Profile.bat" "%DEST%"

echo Generate JRE
cd jre
call create_jre.bat
cd ..
robocopy jre\jre32 "%DEST%\data\jre32" /E /MOVE
robocopy jre\jre64 "%DEST%\data\jre64" /E /MOVE

cd build
echo Create Zip
7z a -t7z -m0=lzma2 -mx=7 -mfb=64 -md=1024m -ms=on %FOLDER%.7z %FOLDER%

cd ..
echo Create SFX
"C:\Program Files (x86)\7z SFX Builder\7z SFX Builder.exe" src\sfxmaker.txt

echo Update VersionInfo and Language
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe" -open "%DEST%.exe" -save "%DEST%.exe" -action delete -mask versioninfo, 1,
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe"  -open "%DEST%.exe" -save "%DEST%.exe" -action add -res src\versioninfo.res -mask versioninfo, 1,
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe"  -open "%DEST%.exe" -save "%DEST%.exe" -action changelanguage(1033)

echo Sign
if %PASS%=="" (echo skip) else (%SIGNTOOL% sign /f %PFX% /p %PASS% /tr http://timestamp.digicert.com /td SHA256 /fd SHA256 "%DEST%.exe")