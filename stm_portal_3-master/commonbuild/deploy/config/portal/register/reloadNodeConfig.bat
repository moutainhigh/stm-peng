@echo off
setlocal
set "SELF=%~dp0%register.bat"
set "CURRENT_DIR=%~dp0%"
cd /d "%CURRENT_DIR%"
cd ..
set "SERVER_HOME=%cd%"

set JAVA_HOME=%SERVER_HOME%/../jdk1.7
set CLASS_PATH=%SERVER_HOME%/common/classes
set JAR_FILE_PATH=%CURRENT_DIR%/mainsteam-stm-launch-4.1.0.jar;%CURRENT_DIR%/mainsteam-stm-node-register-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-node-api-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-node-impl-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-route-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-rpc-client-api-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-rpc-client-impl-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-util-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-common-util-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-platform-api-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-platform-impl-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-lock-api-4.1.0.jar;%SERVER_HOME%/common/lib/mainsteam-stm-lock-impl-4.1.0.jar;%SERVER_HOME%/common/thirdlib
@rem set JAR_FILE_PATH=%JAR_FILE_PATH%;
set STARTUP_CLASS=com.mainsteam.stm.launch.SpringLauncher
set serverType=portal
rem set caplibs.path=../../cap_libs

"%JAVA_HOME%/bin/java" "-Dcatalina.base=%SERVER_HOME%" -cp "%CLASS_PATH%;%CURRENT_DIR%/commons-logging-1.1.3.jar;%CURRENT_DIR%/log4j-1.2.17.jar;%CURRENT_DIR%/mainsteam-stm-bootstrap-api-4.1.0.jar;%CURRENT_DIR%/mainsteam-stm-bootstrap-impl-4.1.0.jar" "com.mainsteam.stm.bootstrap.BootStrapActor" "com.mainsteam.stm.node.LocalNodeTableRecover" %*
@pause