export CURRENT_DIR=$(pwd)
export SERVER_HOME=$(cd ..;pwd)
export CLASS_PATH="$SERVER_HOME/cap_libs"
export JAR_FILE_PATH="$SERVER_HOME/lib/mainsteam-stm-launch-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-server-stopper-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-node-api-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-node-impl-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-route-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-rpc-client-api-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-rpc-client-impl-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-util-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-common-util-4.1.0.jar;$SERVER_HOME/lib/mainsteam-stm-platform-api-4.1.0.jar;$SERVER_HOME/third_lib"
export STARTUP_CLASS="com.mainsteam.stm.launch.SpringLauncher"
export serverType="commons"
export java_options=
"/opt/OneCenter4/jdk1.7/bin/java" $java_options  -cp "$SERVER_HOME/config:commons-logging-1.1.3.jar:log4j-1.2.17.jar:mainsteam-stm-bootstrap-api-4.1.0.jar:mainsteam-stm-bootstrap-impl-4.1.0.jar" "com.mainsteam.stm.bootstrap.BootStrapActor" "com.mainsteam.stm.node.server.SpringContextStopper"

cat /var/run/OneCenter4/WiservOCDCS | xargs kill -9
rm /var/run/OneCenter4/WiservOCDCS -rf