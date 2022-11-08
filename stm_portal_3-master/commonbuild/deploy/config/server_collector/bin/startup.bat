@echo off
setlocal
title "COLLECTOR:"%~dp0
set "SELF=%~dp0%startup.bat"
set "CURRENT_DIR=%~dp0%"
cd /d "%CURRENT_DIR%"
cd ..
set "SERVER_HOME=%cd%"
cd /d "%CURRENT_DIR%"

set "JAVA_HOME=%SERVER_HOME%\..\jdk1.7"
set "CLASS_PATH=%SERVER_HOME%/config"
set "JAR_FILE_PATH=%SERVER_HOME%\lib;%SERVER_HOME%\third_lib"
set STARTUP_CLASS=com.mainsteam.stm.launch.SpringLauncher
set serverType=collector
set "caplibs.path=%SERVER_HOME%\cap_libs"
set "fping.path=%SERVER_HOME%\cap_libs/tools"
set java_options=-Dsun.lang.ClassLoader.allowArraySyntax=true -Dcom.sun.management.jmxremote.port=8077 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Xmx1024m -Xms512m -XX:MaxPermSize=128m -XX:+HeapDumpOnOutOfMemoryError
set CHECK_PARENT_NODE=true

"%JAVA_HOME%/bin/java" %java_options% -cp "%SERVER_HOME%\config;commons-logging-1.1.3.jar;log4j-1.2.17.jar;mainsteam-stm-bootstrap-api-<STM_JAR_VERSION>.jar;mainsteam-stm-bootstrap-impl-<STM_JAR_VERSION>.jar" "com.mainsteam.stm.bootstrap.BootStrapActor" "%*"
