@echo off

rem ################################################################# PROPERTIES #################################################################
SET VERSION=1.3.0
SET FOLDER=lionheart-remake-%VERSION%_win32-x86
SET DEST=build\%FOLDER%
SET SIGNTOOL="C:\Program Files (x86)\Windows Kits\10\bin\10.0.20348.0\x64\signtool.exe"
SET PFX=""
SET PASS=""
rem ##############################################################################################################################################

rem ############################################################### Signing option ###############################################################
echo PFX (enter to skip sign)
set/p "PFX=>"
echo %PFX%

echo Password (enter to skip sign)
if %PFX%=="" (echo skip) else (set/p "PASS=>")
rem ##############################################################################################################################################

rem ############################################################## Compile project ###############################################################
echo --------------------------------------------- Compile game ---------------------------------------------
call mvn clean install -f ..\..\lionheart-parent\pom.xml -P pc,sign
echo --------------------------------------------------------------------------------------------------------

echo -------------------------------------------- Compile editor --------------------------------------------
call mvn clean verify -f ..\..\lionheart-editor-parent\pom.xml -P release
echo --------------------------------------------------------------------------------------------------------
rem ##############################################################################################################################################

rem ################################################################# Copy data ##################################################################
echo -------------------------------------------- Copy game data --------------------------------------------
rmdir /S /Q build
mkdir build
mkdir %DEST%
mkdir %DEST%\data
mkdir %DEST%\data\assets
echo -------------------------------------------- Copy splash
copy "..\data\splash.png" "%DEST%\data"
echo -------------------------------------------- Copy certificate
copy "..\data\b3dgs.cer" "%DEST%\data"
echo -------------------------------------------- Copy properties
copy "..\data\.lionengine" "%DEST%\data"
echo -------------------------------------------- Copy doc
robocopy "..\doc" "%DEST%\doc" /E /NFL /NDL /NJH /NJS /nc /ns /np
echo -------------------------------------------- Copy assets
robocopy "..\..\lionheart-assets\src\main\resources\com\b3dgs\lionheart" "%DEST%\data\assets" /MIR /NFL /NDL /NJH /NJS /nc /ns /np
echo -------------------------------------------- Remove levels rip
del /Q /F /S "stage*.png"
echo -------------------------------------------- Copy jar
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar" "%DEST%\data"
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar.asc" "%DEST%\data"
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar" "%DEST%\data"
echo -------------------------------------------- Copy exe
copy "src\Lionheart Remake\Release\Lionheart Remake.exe" "%DEST%"
copy "src\Lionheart Remake Configure\Release\Configure.exe" "%DEST%"
echo -------------------------------------------- Sign
if %PASS%=="" (echo skip) else (%SIGNTOOL% sign /f %PFX% /p %PASS% /tr http://timestamp.digicert.com /td SHA256 /fd SHA256 "%DEST%\Lionheart Remake.exe")
if %PASS%=="" (echo skip) else (%SIGNTOOL% sign /f %PFX% /p %PASS% /tr http://timestamp.digicert.com /td SHA256 /fd SHA256 "%DEST%\Configure.exe")
echo -------------------------------------------- Copy bat
copy "bat\Lionheart Remake.bat" "%DEST%"
copy "bat\Configure.bat" "%DEST%"
copy "bat\Profile.bat" "%DEST%"
echo --------------------------------------------------------------------------------------------------------
rem ##############################################################################################################################################

rem ################################################################## Make JRE ##################################################################
echo --------------------------------------------- Generate JRE ---------------------------------------------
cd jre
call create_jre.bat
cd ..
robocopy jre\jre_win32-x86 "%DEST%\data\jre_win32-x86" /E /MOVE /NFL /NDL /NJH /NJS /nc /ns /np
robocopy jre\jre_win32-x86_64 "%DEST%\data\jre_win32-x86_64" /E /MOVE /NFL /NDL /NJH /NJS /nc /ns /np
echo --------------------------------------------------------------------------------------------------------
rem ##############################################################################################################################################

rem ################################################################## Make ZIP ##################################################################
echo ---------------------------------------------- Create ZIP ----------------------------------------------
cd build
7z a -t7z -mf=off -m0=lzma2 -mx=9 "%FOLDER%.7z" "%FOLDER%"
rmdir /S /Q "%FOLDER%"
cd ..
echo --------------------------------------------------------------------------------------------------------
rem ##############################################################################################################################################

rem ################################################################## Make SFX ##################################################################
echo ---------------------------------------------- Create SFX ----------------------------------------------
echo ---------------------------------------------- Click "Make SFX"
echo ---------------------------------------------- Click "Close"
echo ---------------------------------------------- Close window
"C:\Program Files (x86)\7z SFX Builder\7z SFX Builder.exe" src\sfxmaker.txt
del "%DEST%.7z"
echo --------------------------------------------------------------------------------------------------------
rem ##############################################################################################################################################

rem ################################################################## ADD INFO ##################################################################
echo ------------------------------------ Update VersionInfo and Language -----------------------------------
echo 
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe" -open "%DEST%.exe" -save "%DEST%.exe" -action delete -mask versioninfo, 1,
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe" -open "%DEST%.exe" -save "%DEST%.exe" -action add -res src\versioninfo.res -mask versioninfo, 1,
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe" -open "%DEST%.exe" -save "%DEST%.exe" -action changelanguage(1033)
echo --------------------------------------------------------------------------------------------------------
rem ##############################################################################################################################################

rem #################################################################### SIGN ####################################################################
echo ------------------------------------------------- Sign -------------------------------------------------
if %PASS%=="" (echo skip) else (%SIGNTOOL% sign /f %PFX% /p %PASS% /tr http://timestamp.digicert.com /td SHA256 /fd SHA256 "%DEST%.exe")
echo --------------------------------------------------------------------------------------------------------
rem ##############################################################################################################################################

rem ################################################################ Make Editor #################################################################
echo ------------------------------------------- Copy editor data -------------------------------------------
mkdir "%DEST%"
mkdir "%DEST%\data"
mkdir "%DEST%\data\assets"
mkdir "%DEST%\data\editor"
mkdir "%DEST%\data\editor\plugins"
robocopy "..\..\com.b3dgs.lionheart.editor.product\target\products\com.b3dgs.lionheart.editor.product\win32\win32\x86_64" "%DEST%\data\editor" /MIR /NFL /NDL /NJH /NJS /nc /ns /np

echo -------------------------------------------- Copy levels rip
cd "..\..\lionheart-assets\src\main\resources\com\b3dgs\lionheart"
xcopy stage*.png ..\..\..\..\..\..\..\distribution\win32\build\%FOLDER%\data\assets /s /q /i
cd "..\..\..\..\..\..\..\distribution\win32\"
echo ---------------------------------------------- Create ZIP editor
cd build
7z a -t7z -mf=off -m0=lzma2 -mx=9 %FOLDER%_editor.7z %FOLDER%
rmdir /S /Q %FOLDER%
echo --------------------------------------------------------------------------------------------------------