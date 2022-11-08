@echo off
setlocal
set "SELF=%~dp0%unRegister.bat"
set "CURRENT_DIR=%~dp0%"
cd /d "%CURRENT_DIR%"
cd ..
set "SERVER_HOME=%cd%"
cd /d "%CURRENT_DIR%"

set JAVA_HOME=%SERVER_HOME%/../jdk1.7
set CLASS_PATH=%SERVER_HOME%/config
set JAR_FILE_PATH=%SERVER_HOME%/lib/mainsteam-stm-launch-<STM_JAR_VERSION>.jar;%CURRENT_DIR%/mainsteam-stm-node-register-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-node-api-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-node-impl-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-route-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-rpc-client-api-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-rpc-client-impl-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-util-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-common-util-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-platform-api-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-platform-impl-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-lock-api-<STM_JAR_VERSION>.jar;%SERVER_HOME%/lib/mainsteam-stm-lock-impl-<STM_JAR_VERSION>.jar;%SERVER_HOME%/third_lib
set STARTUP_CLASS=com.mainsteam.stm.launch.SpringLauncher
set serverType=processer

"%JAVA_HOME%/bin/java" -cp "%CLASS_PATH%;%CURRENT_DIR%/commons-logging-1.1.3.jar;%CURRENT_DIR%/log4j-1.2.17.jar;%CURRENT_DIR%/mainsteam-stm-bootstrap-api-<STM_JAR_VERSION>.jar;%CURRENT_DIR%/mainsteam-stm-bootstrap-impl-<STM_JAR_VERSION>.jar" "com.mainsteam.stm.bootstrap.BootStrapActor" "com.mainsteam.stm.node.NodeUnRegister" %*