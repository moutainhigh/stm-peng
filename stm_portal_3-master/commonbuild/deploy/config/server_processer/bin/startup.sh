export CURRENT_DIR=$(pwd)
export SERVER_HOME=$(cd ..;pwd)
export CLASS_PATH=$SERVER_HOME/cap_libs
export JAR_FILE_PATH="$SERVER_HOME/lib;$SERVER_HOME/third_lib"
export STARTUP_CLASS=com.mainsteam.stm.launch.SpringLauncher
export serverType=processer
export java_options="-Xmx1024m -Xms512m -Dcaplibs.path=$SERVER_HOME/cap_libs"
export console_log="$SERVER_HOME/logs/stdout.log"
eval "/opt/OneCenter4/jdk1.7/bin/java" $java_options -cp "$SERVER_HOME/config:commons-logging-1.1.3.jar:log4j-1.2.17.jar:mainsteam-stm-bootstrap-api-4.1.0.jar:mainsteam-stm-bootstrap-impl-4.1.0.jar" "com.mainsteam.stm.bootstrap.BootStrapActor" "$@" >"$console_log" 2>&1 "&"

ps -ef |grep WiservOCDHS | awk 'NR==1 {print$2}' | xargs echo >/var/run/OneCenter4/WiservOCDHS