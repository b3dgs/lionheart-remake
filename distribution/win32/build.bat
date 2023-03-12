@echo off

SET VERSION=1.3.0-SNAPSHOT
SET DEST=%TMP%\lionheart-remake-%VERSION%_win32

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
robocopy jre32 "%DEST%\data\jre32" /E /MOVE
robocopy jre64 "%DEST%\data\jre64" /E /MOVE
cd ..