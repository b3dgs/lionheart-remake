set VERSION=1.3.0
set PARAM=-server -splash:splash.png -cp lionheart-pc-%VERSION%.jar com.b3dgs.lionheart.Launcher

if %PROCESSOR_ARCHITECTURE% == AMD64 (set JRE=jre_win32-x86_64) else (set JRE=jre_win32-x86)
set path=%~dp0data\%JRE%\bin\;%path%
if "%LaunchPath%" == "" (cd data) else (cd %LaunchPath%\data)
start "" javaw %PARAM%