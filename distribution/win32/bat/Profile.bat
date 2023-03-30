rem ----------------------- DOCUMENTATION -----------------------
rem game modes: -game [story, training, speedrun, battle, versus]
rem      stage: -stage {story=[original, beginner, veteran], training=[original-X, beginner-X, veteran-X], speedrun=[1], battle=[1], versus=[1]}
rem difficulty: -difficulty [beginner, normal, hard, lionhard]
rem    players: -player {story=[1], training=[1], speedrun=[1, 2, 3, 4], battle=[1, 2, 3, 4], versus=[2, 3, 4]}
rem -------------------------------------------------------------

rem ------------------------- EXAMPLES --------------------------
rem  default: set ARGS=
rem    story: set ARGS=-game story -stage beginner -difficulty beginner
rem training: set ARGS=-game training -stage veteran-6 -difficulty lionhard
rem speedrun: set ARGS=-game speedrun -stage 1 -players 2
rem   battle: set ARGS=-game battle -stage 1 -players 3
rem   versus: set ARGS=-game versus -stage 1 -players 4
rem -------------------------------------------------------------



set ARGS=-game story -stage original



rem ----------------------- DO NOT CHANGE -----------------------
set VERSION=1.3.0
set PARAM=-Xverify:none -server -splash:splash.png -jar lionheart-pc-%VERSION%.jar %ARGS%

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
rem -------------------------------------------------------------