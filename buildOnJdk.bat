@echo off
rem $Id: buildJdk16.bat 207 2009-05-05 14:58:00Z lucboudreau $
rem Called recursively from 'ant release' to build the files which can only be
rem built under JDK 1.6.

rem Change the following line to point to your JDK 1.6 home.
set JAVA_HOME=C:\jdk1.6.0_11
rem set JAVA_HOME=C:\java\jdk1.6.0_13

rem Change the following line to point to your ant home.
set ANT_HOME=C:\open\thirdparty\ant
rem set ANT_HOME=C:\ant\ant-1.7.0

set PATH=%JAVA_HOME%\bin;%PATH%
%ANT_HOME%\bin\ant compile.compile

# End buildJdk16.bat
