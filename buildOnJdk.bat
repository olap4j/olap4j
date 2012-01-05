@echo off
rem $Id: //open/mondrian/buildOnJdk.bat#2 $
rem
rem Licensed to Julian Hyde under one or more contributor license
rem agreements. See the NOTICE file distributed with this work for
rem additional information regarding copyright ownership.
rem
rem Julian Hyde licenses this file to you under the Apache License,
rem Version 2.0 (the "License"); you may not use this file except in
rem compliance with the License. You may obtain a copy of the License at:
rem
rem http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

rem **** This program takes 1 argument and a series of other arguments to pass to Ant.
rem ****  - buildOnJdk.bat [jdk version] [ant arguments]

rem **** The value of [jdk version] must be one of:
rem ****  - jdk1.5
rem ****  - jdk1.6
rem ****  - jdk1.7

rem **** It assumes the following environment variables are set.
rem ****  - JAVA_HOME_15: Home directory of a JDK 1.5.X.
rem ****  - JAVA_HOME_16: Home directory of a JDK 1.6.X.
rem ****  - JAVA_HOME_17: Home directory of a JDK 1.7.X.

rem **** It also assumes that Ant is on the classpath.

rem =============================================================================
rem ===== You can set some environment variables right here if needed ===========

rem Change the following line to point to your JDK 1.5 home.
set JAVA_HOME_15=C:\apps\java\jdk1.5.0_22

rem Change the following line to point to your JDK 1.6 home.
set JAVA_HOME_16=C:\apps\java\jdk1.6.0_27

rem Change the following line to point to your JDK 1.7 home.
set JAVA_HOME_17=C:\apps\java\jdk1.7.0_01

rem Change the following line to point to your ant home.
rem set ANT_HOME=C:\apps\ant\1.7.1
rem set ANT_HOME=C:\apps\ant\1.8.1

rem ======================================================
rem ===== Don't touch anything below this line ===========

if %1==jdk1.5 (
set JAVA_HOME=%JAVA_HOME_15%
)
if %1==jdk1.6 (
set JAVA_HOME=%JAVA_HOME_16%
)
if %1==jdk1.7 (
set JAVA_HOME=%JAVA_HOME_17%
)

set ANT_ARGUMENTS=
for %%A in (%*) do (
set ANT_ARGUMENTS=%ANT_ARGUMENTS% %%A
)

rem We set JAVACMD for the benefit of Ant.
set JAVACMD=%JAVA_HOME%\bin\java.exe

rem Some debug info
echo Using ANT_HOME: %ANT_HOME%
echo Using JAVA_HOME: %JAVA_HOME%
echo Using JAVACMD: %JAVACMD%
echo Using Ant arguments: %ANT_ARGUMENTS%

ant %ANT_ARGUMENTS%

rem End buildJdk16.bat

