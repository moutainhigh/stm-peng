@echo off
set run_path=%~dp0
set shell_name=%~1
set shell_param=%~2
set report_name=%~3
set pshell_file=%run_path%%shell_name%
set pshell_report=%run_path%%report_name%

rem echo %run_path%
rem echo %shell_name%
rem echo %shell_param%
rem echo %pshell_file%

echo %shell_name% | findstr ".*\.vbs\>" > nul && goto vbs
echo %shell_name% | findstr ".*\.ps1\>" > nul && goto ps1
goto end

:vbs
rem set param_file="%run_path%%shell_name%.txt"
rem echo %shell_param%>%param_file%
cscript //NOLOGO %pshell_file% %shell_param% > %pshell_report%
goto end

:ps1
rem set param_file="%run_path%param_%shell_name%.txt"
rem echo %shell_param%>%param_file%
powershell.exe Set-ExecutionPolicy Unrestricted
powershell.exe %pshell_file% %shell_param% %pshell_report%
goto end

:end
rem del %param_file%
rem del *.log
exit