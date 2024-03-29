set PARAM=-server -splash:splash.png -jar lionheart-pc-%%INPUT_APPV%%.jar

if "%PROCESSOR_ARCHITECTURE%" == "AMD64" (
  set JRE=jre_win32-x86_64
) else (
  if "%PROCESSOR_ARCHITEW6432%" == "AMD64" (
    set JRE=jre_win32-x86_64
  ) else (
    set JRE=jre_win32-x86
  )
)

set path=%~dp0data\%JRE%\bin\;%path%
if "%LaunchPath%" == "" (cd data) else (cd %LaunchPath%\data)
start "" javaw %PARAM%