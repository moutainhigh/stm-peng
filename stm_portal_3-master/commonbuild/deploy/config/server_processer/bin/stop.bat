@echo off
title "STOP COLLECTOR:"%~dp0
set JAVA_HOME=../../jdk1.7
set CLASS_PATH=../cap_libs
set JAR_FILE_PATH=../lib/mainsteam-stm-launch-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-server-stopper-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-node-api-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-node-impl-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-route-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-rpc-client-api-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-rpc-client-impl-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-util-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-common-util-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-platform-api-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-platform-impl-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-lock-api-<STM_JAR_VERSION>.jar;../lib/mainsteam-stm-lock-impl-<STM_JAR_VERSION>.jar;../third_lib
set STARTUP_CLASS=com.mainsteam.stm.launch.SpringLauncher
set serverType=commons
set java_options=
"%JAVA_HOME%/bin/java" %java_options%  -cp "../config;commons-logging-1.1.3.jar;log4j-1.2.17.jar;mainsteam-stm-bootstrap-api-<STM_JAR_VERSION>.jar;mainsteam-stm-bootstrap-impl-<STM_JAR_VERSION>.jar" "com.mainsteam.stm.bootstrap.BootStrapActor" "com.mainsteam.stm.node.server.SpringContextStopper"
