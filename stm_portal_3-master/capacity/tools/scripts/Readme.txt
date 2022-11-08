这个目录下的shell脚本是需要手动拷贝到目标机器上，其他的例如powershell脚本（xxx.ps1）和vb脚本（xxx.vbs）等windows下运行的脚本会
WMIAgent自动拷贝到目标机器上，故不需要手动去操作。

其中，os.sh, appinfo.sh, appcpumem.sh适用于主机类的指标，所以类Unix或者Unix平台的数据库、中间件、应用服务器等均需要这三个脚本，
手动拷贝到目标机器上的/tmp目录下（注意：不能有子目录,例如：/tmp/host/os.sh），并且设置777的权限（使用chmod 777 xxx.sh命令）。

其他子目录下的脚本是针对特定的服务器所使用的，例如Domino下的脚本是采集domino服务器的脚本。使用方法同上。注意：不能使用子目录。例如直接
将Domino整个目录直接拷贝到/tmp下，形成/tmp/Domino/xxx.sh。这个路径我们是识别不了的。务必只能有/tmp目录。

powershell脚本和vbs脚本请配置管理工程师在制作安装包时，将相应的脚本拷贝到WMI Agent的安装目录下的script下即可。注意，不要使用子目录。