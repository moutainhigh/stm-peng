#=============================================================================
#     domino windows platform collection scritp
# Note:This script through creating Windows PowerShell 2.0, it implementation 
#       collect domino information by domino inner command.
#
# Author:daihw
# Date:2012-07-24
# Version:1.0
#=============================================================================

#/##
# script output console
#/
function script_printf([string]$contents) {
    #Write-Host $contents
	$contents | out-file -filepath $prarmOutFileTmp -append -encoding UTF8
}

#/##
# script output console for debug
#/
function script_debug([string]$contents , [bool]$needout) {
    if ($needout -eq $true) {
        Write-Host $contents
    }
}

#/##
# clear tmadmin console output contents 
#/
function console_clear{
    clear
}

function collect_info(){
	$serverstatus=0
	$amgrstatus=0
	$httpstatus=0
	$avail = 0
	$debug_switch = $False
	$domino_srv_port=""
	
	
	#check param.txt and then set env and change path 
	$MyInvocation = (Get-Variable MyInvocation -Scope 1).Value
	$run_path = Split-Path $MyInvocation.MyCommand.Path
	$shell_fname = $MyInvocation.MyCommand.Name  

	$domino_data_path=$paramDominoPath
	$tokens=$paramDominoPath.split('+')
	switch ($tokens.Count) 
	{
		2
		{
			$domino_app_path=$tokens[0]
			$domino_data_path=$tokens[1] 
		}
	}
	#add $domino_app_path to PATH
	$env:PATH="%$env:PATH%;C:\Windows\System32;$domino_app_path"
	#get a random 
	$random = New-Object System.Random
	$rand = $random.Next("1000")
	#change  path to domino data
	cd $domino_app_path
	$get_srv_ver_nodes = $run_path+"\domino_tmp_server_vernodes$rand.log"  
	script_debug $get_srv_ver_nodes $debug_switch 
	nserver -c "show stat Server.Version.Notes >$get_srv_ver_nodes" 2>$null
	#nserver是以异步的方式启动，此处的while起到阻塞的作用，为防止死循环还需要设定最长循环时间(10s)
	$Start1 = Get-Date
	while($True){
		$End1 = Get-Date
		$TimeSpan1 = New-Timespan $Start1 $End1
		#script_printf("Extract Str Use Time1 ---  " + $TimeSpan1.TotalSeconds.ToString())
		$pro = Get-Process -name nserver
		#script_printf($pro.ToString())
		if((Test-Path $get_srv_ver_nodes) -or ($TimeSpan1.TotalSeconds -ge 5)){
			break
		}
		
	}
	$get_srv_ver_os = $run_path+"\domino_tmp_server_veros$rand.log"  
	script_debug $get_srv_ver_os $debug_switch 
	nserver -c "show stat Server.Version.OS >$get_srv_ver_os" 2>$null
	$Start2 = Get-Date
	while($True){
		$End2 = Get-Date
		$TimeSpan2 = New-Timespan $Start2 $End2
		#script_printf("Extract Str Use Time2 ---  " + $TimeSpan2.TotalSeconds.ToString())
		if((Test-Path $get_srv_ver_os) -or ($TimeSpan2.TotalSeconds -ge 5)){
			break
		}
		
	}
	$get_srv_task = $run_path+"\domino_tmp_server_task$rand.log"  
	nserver -c "show task >$get_srv_task"  2>$null
	$Start3 = Get-Date
	while($True){
		$End3 = Get-Date
		$TimeSpan3 = New-Timespan $Start3 $End3
		#script_printf("Extract Str Use Time3 ---  " + $TimeSpan3.TotalSeconds.ToString())
		if((Test-Path $get_srv_task) -or ($TimeSpan3.TotalSeconds -ge 5)){
			break
		}
	}
	$get_srv_tasks= $run_path+"\domino_tmp_server_tasks$rand.log"  
	nserver -c "show stat server.tasks >$get_srv_tasks"  2>$null
	$Start4 = Get-Date	
	while($True){
		$End4 = Get-Date
		$TimeSpan4 = New-Timespan $Start4 $End4
		#script_printf("Extract Str Use Time4 ---  " + $TimeSpan4.TotalSeconds.ToString())
		if((Test-Path $get_srv_tasks) -or ($TimeSpan4.TotalSeconds -ge 5)){
			break
		}
	}
	
	  #show stat Server.Version.Notes
	script_debug $get_srv_ver_nodes $debug_switch
	do {
		$tryCount++
		if (Test-Path $get_srv_ver_nodes) {
		 $server_version_nodes = get-content $get_srv_ver_nodes
		 script_debug $server_version_nodes $debug_switch
		 foreach ($server_version_node in $server_version_nodes) {
			 script_debug $server_version_node $debug_switch
			 if ($server_version_node -match 'Server.Version.Notes = Release ([\d+\.\d]*)') {
				 $domion_version_num = $matches[1]
				 script_debug $domion_version_num $debug_switch            
			 }
		 }    
		}
		if ($tryCount -gt 10) {
		  $tryCount=0
		  break
		}
	} while ($domion_version_num  -eq "")
	
	#show stat Server.Version.OS

	do {
	$tryCount++
	if (Test-Path $get_srv_ver_os) {
		$server_version_os = get-content $get_srv_ver_os
		foreach ($server_os in $server_version_os) {
			 if ($server_os -match 'Server.Version.OS\s?=\s?(.*)') {
				 $osinfo = $matches[1]
			 }
		}
	}  
	if ($tryCount -gt 10) {
		$tryCount=0
		break
	}
	}while($osinfo -eq "")
	
	if (Test-Path $get_srv_task) {
		$server_tasks = get-content $get_srv_task
		script_debug  "show task=$server_tasks"  $debug_switch
		foreach ($server_task in $server_tasks) {
			if ($server_task -match 'HTTP Server\s*.*Port:(\d+)') {
				$http_port = $matches[1]
			}
		}
    }

	$isMatched = "0"
	do {
		$tryCount++
		#show stat server.tasks  
		if (Test-Path $get_srv_tasks) {
		 $stat_server_tasks = get-content $get_srv_tasks
		 foreach ($stat_server_task in $stat_server_tasks) {
			switch -regex ($stat_server_task) {
				   "Server.Tasks\s?=\s?(\d+)"  { $activetaskcount = $matches[1]
												 $isMatched = "1"
												 break
												}           
			}
		 }     
		} 
		if ($tryCount -gt 10) {
		  $tryCount=0
		  break
		}
	}while(Test-Path $get_srv_tasks)
  
    script_printf( "<activetaskcount>$activetaskcount</activetaskcount>" )	
	script_printf( "<httpport>$http_port</httpport>" )
	script_printf( "<osinfo>$osinfo</osinfo>" )
	script_printf( "<version>$domion_version_num</version>" )
	
	
	if (Test-Path $get_srv_ver_nodes) {
		del $get_srv_ver_nodes
    }
	if (Test-Path  $get_srv_ver_os) {
		del  $get_srv_ver_os
    } 
    if (Test-Path  $get_srv_task) {
		del  $get_srv_task
    }
	if (Test-Path  $get_srv_tasks) {
		del  $get_srv_tasks
    }
}

$paramOutFile=$args[1]
$prarmOutFileTmp="$paramOutFile.tmp"
$paramDominoPath=$args[0]

collect_info
if (Test-Path $paramOutFile) {
	del $paramOutFile
}
rename-item $prarmOutFileTmp $paramOutFile