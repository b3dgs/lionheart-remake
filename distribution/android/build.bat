@echo off

SET KEYSTORE="C:\Users\djthunder\keystores\android.jks"

rmdir /S /Q build
mkdir build

cd "..\..\lionheart-android"

echo "Compile project"

if "%OS%"=="Windows_NT" setlocal
set DIRNAME=.
set APP_BASE_NAME=lionheart-android
set APP_HOME=%DIRNAME%
set DEFAULT_JVM_OPTS=
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
call java %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain clean assembleRelease

del /Q /F /S "app\build\outputs\apk\release\app-release-*"

echo "Align APK"
call zipalign -v -p 4 "app\build\outputs\apk\release\app-release.apk" "app\build\outputs\apk\release\app-release-aligned.apk"

echo "Sign APK"
call apksigner sign --ks %KEYSTORE% --out "app\build\outputs\apk\release\app-release-signed.apk" "app\build\outputs\apk\release\app-release-aligned.apk"

echo "Copy final APK"
copy "app\build\outputs\apk\release\app-release-signed.apk" "..\distribution\android\build\lionheart-remake-1.4.0.apk"