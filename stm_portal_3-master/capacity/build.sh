currPath=`pwd`
echo "currPath:$currPath"

echo "building..."
cd $currPath/modules/stm-caplib/stm-caplib-api
ant
cd $currPath

cd $currPath/modules/stm-caplib/stm-caplib-impl
ant
cd $currPath

cd $currPath/modules/stm-pluginsession
ant
cd $currPath

cd $currPath/modules/stm-pluginprocessor
ant
cd $currPath

cd $currPath/plugins/stm-plugincommon
ant
cd $currPath

cd $currPath/plugins/stm-icmpplugin
ant
cd $currPath

cd $currPath/plugins/stm-snmpplugin
ant
cd $currPath

cd $currPath/plugins/stm-telnetplugin
ant
cd $currPath

cd $currPath/plugins/stm-sshplugin
ant
cd $currPath

cd $currPath/plugins/stm-wmiplugin
ant
cd $currPath


cd $currPath/plugins/stm-jdbcplugin
ant
cd $currPath

echo "copying files..."

destPath="/tmp/ms6/Server"
mkdir /tmp/ms6
mkdir $destPath
rm -fr $destPath/*

mkdir $destPath/plugins
mkdir $destPath/bin
mkdir $destPath/logs
destPathCap="$destPath/caplib"
mkdir "$destPathCap"
destLibPath="$destPath/lib"
mkdir "$destLibPath"



#linux cp -i
find $currPath/../repository/local -name "mainsteam*.jar" | xargs -I{} cp {} "$destLibPath"

cp -fr $currPath/cap_libs/dictionary $destPathCap
cp -fr $currPath/cap_libs/plugins $destPathCap
cp -fr $currPath/cap_libs/resources $destPathCap

echo "finish"
