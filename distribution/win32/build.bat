@echo off

rem ################################################################# PROPERTIES #################################################################
SET VERSION=1.3.0
SET VERSIONI=1,3,0
SET FOLDER=lionheart-remake-%VERSION%_win32-x86
SET DEST=build\%FOLDER%
SET MSBUILD="C:\Program Files\Microsoft Visual Studio\2022\Community\Msbuild\Current\Bin\msbuild.exe"
SET POWERSHELL="C:\Program Files\PowerShell\7\pwsh.exe"
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
copy "..\..\CHANGELOG" "%DEST%\doc\CHANGELOG.txt"
copy "..\..\LICENSE" "%DEST%\doc\LICENSE.txt"
echo -------------------------------------------- Copy assets
robocopy "..\..\lionheart-assets\src\main\resources\com\b3dgs\lionheart" "%DEST%\data\assets" /MIR /NFL /NDL /NJH /NJS /nc /ns /np
echo -------------------------------------------- Remove levels rip
del /Q /F /S "stage*.png"
echo -------------------------------------------- Copy jar
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar" "%DEST%\data"
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar.asc" "%DEST%\data"
copy "..\..\lionheart-pc\target\lionheart-pc-%VERSION%.jar" "%DEST%\data"
echo -------------------------------------------- Copy bat
%POWERSHELL% -Command "(gc 'src\Lionheart Remake.bat') -replace '%%%%INPUT_APPV%%%%', '%VERSION%' | Out-File -encoding utf8NoBOM '%DEST%\Lionheart Remake.bat'"
%POWERSHELL% -Command "(gc 'src\Configure.bat') -replace '%%%%INPUT_APPV%%%%', '%VERSION%' | Out-File -encoding utf8NoBOM '%DEST%\Configure.bat'"
%POWERSHELL% -Command "(gc 'src\Profile.bat') -replace '%%%%INPUT_APPV%%%%', '%VERSION%' | Out-File -encoding utf8NoBOM '%DEST%\Profile.bat'"

robocopy "src\Lionheart Remake" "%TMP%\Lionheart Remake" /MIR /NFL /NDL /NJH /NJS /nc /ns /np
robocopy "src\Lionheart Remake Configure" "%TMP%\Lionheart Remake Configure" /MIR /NFL /NDL /NJH /NJS /nc /ns /np
echo -------------------------------------------- Copy exe
%POWERSHELL% -Command "(gc '%TMP%\Lionheart Remake\Lionheart Remake\Lionheart Remake.rc') -replace '%%%%INPUT_APPV%%%%', '%VERSION%' | Out-File -encoding utf8NoBOM '%TMP%\Lionheart Remake\Lionheart Remake\Lionheart Remake.rc'"
%POWERSHELL% -Command "(gc '%TMP%\Lionheart Remake\Lionheart Remake\Lionheart Remake.rc') -replace '%%%%INPUT_APPVI%%%%', '%VERSIONI%' | Out-File -encoding utf8NoBOM '%TMP%\Lionheart Remake\Lionheart Remake\Lionheart Remake.rc'"
%POWERSHELL% -Command "(gc '%TMP%\Lionheart Remake\Lionheart Remake\main.cpp') -replace '%%%%INPUT_APPV%%%%', '%VERSION%' | Out-File -encoding utf8NoBOM '%TMP%\Lionheart Remake\Lionheart Remake\main.cpp'"

%POWERSHELL% -Command "(gc '%TMP%\Lionheart Remake Configure\Lionheart Remake Configure\Lionheart Remake Configure.rc') -replace '%%%%INPUT_APPV%%%%', '%VERSION%' | Out-File -encoding utf8NoBOM '%TMP%\Lionheart Remake Configure\Lionheart Remake Configure\Lionheart Remake Configure.rc'"
%POWERSHELL% -Command "(gc '%TMP%\Lionheart Remake Configure\Lionheart Remake Configure\Lionheart Remake Configure.rc') -replace '%%%%INPUT_APPVI%%%%', '%VERSIONI%' | Out-File -encoding utf8NoBOM '%TMP%\Lionheart Remake Configure\Lionheart Remake Configure\Lionheart Remake Configure.rc'"
%POWERSHELL% -Command "(gc '%TMP%\Lionheart Remake Configure\Lionheart Remake Configure\main.cpp') -replace '%%%%INPUT_APPV%%%%', '%VERSION%' | Out-File -encoding utf8NoBOM '%TMP%\Lionheart Remake Configure\Lionheart Remake Configure\main.cpp'"

%MSBUILD% "%TMP%\Lionheart Remake\Lionheart Remake\Lionheart Remake.vcxproj" -t:rebuild -property:Configuration=Release
%MSBUILD% "%TMP%\Lionheart Remake Configure\Lionheart Remake Configure\Lionheart Remake Configure.vcxproj" -t:rebuild -property:Configuration=Release

copy "%TMP%\Lionheart Remake\Lionheart Remake\Release\Lionheart Remake.exe" "%DEST%"
copy "%TMP%\Lionheart Remake Configure\Lionheart Remake Configure\Release\Configure.exe" "%DEST%"
echo -------------------------------------------- Sign
if %PASS%=="" (echo skip) else (%SIGNTOOL% sign /f %PFX% /p %PASS% /tr http://timestamp.digicert.com /td SHA256 /fd SHA256 "%DEST%\Lionheart Remake.exe")
if %PASS%=="" (echo skip) else (%SIGNTOOL% sign /f %PFX% /p %PASS% /tr http://timestamp.digicert.com /td SHA256 /fd SHA256 "%DEST%\Configure.exe")
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
%POWERSHELL% -Command "(gc 'src\sfxmaker.txt') -replace '%%%%INPUT_APPV%%%%', '%VERSION%' | Out-File -encoding utf8NoBOM 'build\sfxmaker.txt'"
copy "src\7zsd_LZMA2.sfx" "build"
cd build
7z a -t7z -mf=off -m0=lzma2 -mx=9 "%FOLDER%.7z" "%FOLDER%"
7z a -tzip -m0=deflate -mx=9 %FOLDER%.zip %FOLDER%
copy /b "7zsd_LZMA2.sfx" + "sfxmaker.txt" + "%FOLDER%.7z" "%FOLDER%.exe"
sha256sum %FOLDER%.exe > %FOLDER%.exe.sha256sum
sha256sum %FOLDER%.zip > %FOLDER%.zip.sha256sum
del "%FOLDER%.7z"
cd ..
echo --------------------------------------------------------------------------------------------------------
rem ##############################################################################################################################################

rem ################################################################## ADD INFO ##################################################################
echo ------------------------------------ Update VersionInfo and Language -----------------------------------
echo 
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe" -open "build\%FOLDER%\Lionheart Remake.exe" -save info.res -action extract -mask *,*,
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe" -open "build\%FOLDER%.exe" -save "build\%FOLDER%.exe" -action delete -mask *,*,
"C:\Program Files (x86)\Resource Hacker\resourcehacker.exe" -open "build\%FOLDER%.exe" -save "build\%FOLDER%.exe" -action addoverwrite -res info.res -mask *,*,
del info.res
cd build
rmdir /S /Q "%FOLDER%"
del 7zsd_LZMA2.sfx
del sfxmaker.txt
cd ..
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
sha256sum %FOLDER%_editor.7z > %FOLDER%_editor.7z.sha256sum
rmdir /S /Q %FOLDER%
cd ..
echo --------------------------------------------------------------------------------------------------------