@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

rem ---------------------------------------------------------------------------
rem NT Service Install/Uninstall script
rem
rem Options
rem install                Install the service using Tomcat7 as service name.
rem                        Service is installed using default settings.
rem remove                 Remove the service from the System.
rem
rem name        (optional) If the second argument is present it is considered
rem                        to be new service name
rem ---------------------------------------------------------------------------

setlocal
set "SELF=%~dp0%service.bat"
rem Guess SERVER_HOME if not defined
set "CURRENT_DIR=%~dp0%"
if not "%SERVER_HOME%" == "" goto gotHome
set "SERVER_HOME=%CURRENT_DIR%"
if exist "%SERVER_HOME%\bin\server_dcs.exe" goto okHome
rem CD to the upper dir
cd /d "%CURRENT_DIR%"
cd ..
set "SERVER_HOME=%cd%"
:gotHome
if exist "%SERVER_HOME%\bin\server_dcs.exe" goto okHome
echo The server_dcs.exe was not found...
echo The SERVER_HOME environment variable is not defined correctly.
echo This environment variable is needed to run this program
goto end
:okHome
set JAVA_HOME=%SERVER_HOME%\..\jdk1.7
rem Make sure prerequisite environment variables are set
if not "%JAVA_HOME%" == "" goto gotJdkHome
if not "%JRE_HOME%" == "" goto gotJreHome
echo Neither the JAVA_HOME nor the JRE_HOME environment variable is defined
echo Service will try to guess them from the registry.
goto okJavaHome
:gotJreHome
if not exist "%JRE_HOME%\bin\java.exe" goto noJavaHome
if not exist "%JRE_HOME%\bin\javaw.exe" goto noJavaHome
goto okJavaHome
:gotJdkHome
if not exist "%JAVA_HOME%\jre\bin\java.exe" goto noJavaHome
if not exist "%JAVA_HOME%\jre\bin\javaw.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\javac.exe" goto noJavaHome
if not "%JRE_HOME%" == "" goto okJavaHome
set "JRE_HOME=%JAVA_HOME%\jre"
goto okJavaHome
:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
echo NB: JAVA_HOME should point to a JDK not a JRE
goto end
:okJavaHome
if not "%SERVER_BASE%" == "" goto gotBase
set "SERVER_BASE=%SERVER_HOME%"
set "CLASS_PATH=%SERVER_HOME%/cap_libs"
set "JAR_FILE_PATH=%SERVER_HOME%\lib,%SERVER_HOME%\third_lib"
set "STARTUP_CLASS=com.mainsteam.stm.launch.SpringLauncher"
set "serverType=collector"
set "caplibs.path=%SERVER_HOME%/cap_libs"
set "CHECK_PARENT_NODE=true"

:gotBase

set "EXECUTABLE=%SERVER_HOME%\bin\server_dcs.exe"

rem Set default Service name
set SERVICE_NAME=<OC4_DCS_SERVICE_NAME>
set DISPLAYNAME=<OC4_DCS_SERVICE_DISPLAY>

if "x%1x" == "xx" goto displayUsage
set SERVICE_CMD=%1
shift
if "x%1x" == "xx" goto checkServiceCmd
:checkUser
if "x%1x" == "x/userx" goto runAsUser
if "x%1x" == "x--userx" goto runAsUser
set SERVICE_NAME=%1
set DISPLAYNAME=Apache Tomcat 7 %1
shift
if "x%1x" == "xx" goto checkServiceCmd
goto checkUser
:runAsUser
shift
if "x%1x" == "xx" goto displayUsage
set SERVICE_USER=%1
shift
runas /env /savecred /user:%SERVICE_USER% "%COMSPEC% /K \"%SELF%\" %SERVICE_CMD% %SERVICE_NAME%"
goto end
:checkServiceCmd
if /i %SERVICE_CMD% == install goto doInstall
if /i %SERVICE_CMD% == remove goto doRemove
if /i %SERVICE_CMD% == uninstall goto doRemove
echo Unknown parameter "%1"
:displayUsage
echo.
echo Usage: service.bat install/remove [service_name] [/user username]
goto end

:doRemove
rem Remove the service
echo Removing the service '%SERVICE_NAME%' ...
echo Using SERVER_BASE:    "%SERVER_BASE%"

"%EXECUTABLE%" //DS//%SERVICE_NAME% ^
    --LogPath "%SERVER_BASE%\logs"
if not errorlevel 1 goto removed
echo Failed removing '%SERVICE_NAME%' service
goto end
:removed
echo The service '%SERVICE_NAME%' has been removed
goto end

:doInstall
rem Install the service
echo Installing the service '%SERVICE_NAME%' ...
echo Using SERVER_HOME:    "%SERVER_HOME%"
echo Using SERVER_BASE:    "%SERVER_BASE%"
echo Using JAVA_HOME:        "%JAVA_HOME%"
echo Using JRE_HOME:         "%JRE_HOME%"

rem Set the server jvm from JAVA_HOME
set "JVM=%JRE_HOME%\bin\server\jvm.dll"
if exist "%JVM%" goto foundJvm
rem Set the client jvm from JAVA_HOME
set "JVM=%JRE_HOME%\bin\client\jvm.dll"
if exist "%JVM%" goto foundJvm
set JVM=auto
:foundJvm
echo Using JVM:              "%JVM%"

set "CLASSPATH=%SERVER_HOME%\config;%SERVER_HOME%\bin\log4j-1.2.17.jar;%SERVER_HOME%\bin\commons-logging-1.1.3.jar;%SERVER_HOME%\bin\mainsteam-stm-bootstrap-api-<STM_JAR_VERSION>.jar;%SERVER_HOME%\bin\mainsteam-stm-bootstrap-impl-<STM_JAR_VERSION>.jar"

"%EXECUTABLE%" //IS//%SERVICE_NAME% ^
    --Description "��Ϣ�ɼ���" ^
    --DisplayName "%DISPLAYNAME%" ^
    --Install "%EXECUTABLE%" ^
    --LogPath "%SERVER_BASE%\logs" ^
    --StdOutput auto ^
    --StdError auto ^
    --Classpath "%CLASSPATH%" ^
    --Jvm "%JVM%" ^
    --StartMode jvm ^
    --StopMode jvm ^
    --StartPath "%SERVER_HOME%\bin" ^
    --StopPath "%SERVER_HOME%\bin" ^
    --StartClass com.mainsteam.stm.bootstrap.BootStrapActor ^
    --StopClass com.mainsteam.stm.bootstrap.BootStrapActor ^
    --StartParams "" ^
    --StopParams stop ^
    --JvmOptions "-XX:+HeapDumpOnOutOfMemoryError;-XX:MaxPermSize=128m;-Dcom.sun.management.jmxremote.port=8077;-Dcom.sun.management.jmxremote.authenticate=false;-Dcom.sun.management.jmxremote.ssl=false;-DCLASS_PATH=%CLASS_PATH%;-DSTARTUP_CLASS=%STARTUP_CLASS%;-DJAR_FILE_PATH=%JAR_FILE_PATH%;-DserverType=%serverType%;-Dcaplibs.path=%caplibs.path%;-DCHECK_PARENT_NODE=%CHECK_PARENT_NODE%" ^
    --JvmMs 768 ^
    --JvmMx 1024
if not errorlevel 1 goto installed
echo Failed installing '%SERVICE_NAME%' service
goto end
:installed
echo The service '%SERVICE_NAME%' has been installed.

:end
cd "%CURRENT_DIR%"
