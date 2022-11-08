@echo on&setlocal EnableDelayedExpansion
echo hello
for /r E:\work\code\repository\local %%i in (mainsteam-stm-*.jar) do copy %%i "../lib"
for /r D:\.ivy2\OC4 %%i in (mainsteam-stm-*.jar) do copy %%i "../lib"

move /Y "../lib/mainsteam-stm-bootstrap-api-<STM_JAR_VERSION>.jar" "mainsteam-stm-bootstrap-api-<STM_JAR_VERSION>.jar"
move /Y "../lib/mainsteam-stm-bootstrap-impl-<STM_JAR_VERSION>.jar" "mainsteam-stm-bootstrap-impl-<STM_JAR_VERSION>.jar"

for /r D:\.ivy2\OC4 %%i in (*.jar) do copy %%i "../third_lib"
move /Y "../third_lib/commons-logging-1.1.3.jar" "commons-logging-1.1.3.jar"
move /Y "../third_lib/log4j-1.2.13.jar" "log4j-1.2.13.jar"

del /q "../third_lib/mainsteam-stm-*.jar"