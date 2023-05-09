set javahome=C:\Eclipse\
set jdk=jdk-17.0.6+10
set param=--compress=0 --strip-debug --no-header-files --no-man-pages --add-modules java.base,java.xml,jdk.xml.dom,java.prefs,java.desktop,java.logging

rmdir /S /Q jre_win32-x86_64
%javahome%%jdk%\bin\jlink %param% --output jre_win32-x86_64

rmdir /S /Q jre_win32-x86
%javahome%%jdk%_32\bin\jlink %param% --output jre_win32-x86
del jre_win32-x86\bin\client /S /Q
rmdir jre_win32-x86\bin\client /S /Q