
md D:\OC4-deploy\Installer\
xcopy D:\workspace\OC4-Deploy\commonbuild\deploy\config  D:\OC4-deploy\ /D /E /Y /H /K
xcopy D:\workspace\OC4-Deploy\install\memcached  D:\OC4-deploy\memcached\   /D /E /Y /H /K
xcopy D:\workspace\OC4-Deploy\install\WMIAgent  D:\OC4-deploy\WMIAgent\   /D /E /Y /H /K
xcopy D:\workspace\OC4-Deploy\install\tomcat-7.0.53  D:\OC4-deploy\portal\   /D /E /Y /H /K
xcopy D:\workspace\OC4-Deploy\install\installer_nsis  D:\OC4-deploy\Installer   /D /E /Y /H /K

xcopy D:\workspace\OC4-Deploy\Framework\tomcat  D:\OC4-deploy\portal\ /D /E /Y /H /K

xcopy D:\workspace\OC4-Deploy\Portal\oc-portal\oc-webapp\src\main\webapp   D:\OC4-deploy\portal\webapps\oc\   /D /E /Y /H /K
xcopy D:\workspace\OC4-Deploy\Portal\oc-portal\oc-webapp\src\main\resources\META-INF D:\OC4-deploy\portal\webapps\oc\WEB-INF\classes\META-INF\  /D /E /Y /H /K

xcopy D:\workspace\OC4-Deploy\Capacity\cap_libs  D:\OC4-deploy\portal\common\classes\cap_libs\  /D /E /Y /H /K
xcopy D:\workspace\OC4-Deploy\Capacity\cap_libs  D:\OC4-deploy\server_collector\cap_libs\   /D /E /Y /H /K
xcopy D:\workspace\OC4-Deploy\Capacity\cap_libs  D:\OC4-deploy\server_processer\cap_libs\   /D /E /Y /H /K
xcopy D:\workspace\OC4-Deploy\Capacity\cap_libs  D:\OC4-deploy\trap\cap_libs\   /D /E /Y /H /K

xcopy D:\workspace\OC4-Deploy\Doc\dbscript D:\OC4-deploy\dbscript\  /D /E /Y /H /K


rem set CurDate=%time%
rem set Day=%CurTime:~0,10%
rem "C:\Program Files\WinRAR\WinRAR.exe" a  D:\OC4-deploy\deploy_oc%Day%.rar D:\OC4-deploy

"C:\Program Files\WinRAR\WinRAR.exe" a  D:\OC4-deploy\deploy_oc.rar D:\OC4-deploy

copy D:\OC4-deploy\deploy_oc.rar d:\FTP_Root\ /y
