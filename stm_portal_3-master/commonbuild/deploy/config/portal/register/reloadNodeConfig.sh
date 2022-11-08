export SELF="$0"
export CURRENT_DIR=$(pwd)
export SERVER_HOME=$(cd ..;pwd)
export CLASS_PATH="$SERVER_HOME/common/classes"
export JAR_FILE_PATH="$CURRENT_DIR/mainsteam-stm-launch-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-node-api-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-node-impl-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-route-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-rpc-client-api-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-rpc-client-impl-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-util-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-common-util-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-platform-api-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-platform-impl-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-lock-api-4.1.0.jar;$SERVER_HOME/common/lib/mainsteam-stm-lock-impl-4.1.0.jar;$SERVER_HOME/common/thirdlib"
export STARTUP_CLASS=com.mainsteam.stm.launch.SpringLauncher
export serverType=portal
echo $SELF
echo $CURRENT_DIR
echo $SERVER_HOME
echo $JAR_FILE_PATH
"/opt/OneCenter4/jdk1.7/bin/java" "-Dcatalina.base=$SERVER_HOME" -cp ".:$CLASS_PATH:$CURRENT_DIR/commons-logging-1.1.3.jar:$CURRENT_DIR/log4j-1.2.17.jar:$CURRENT_DIR/mainsteam-stm-bootstrap-api-4.1.0.jar:$CURRENT_DIR/mainsteam-stm-bootstrap-impl-4.1.0.jar" "com.mainsteam.stm.bootstrap.BootStrapActor" "com.mainsteam.stm.node.LocalNodeTableRecover" $@
