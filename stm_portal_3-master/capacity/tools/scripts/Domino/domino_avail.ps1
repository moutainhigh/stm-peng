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

function collect_avail(){
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
	$task_list = @(Get-Process)
	foreach ($task_infor in $task_list) {   
		$task_process_name = $task_infor.ProcessName           
		if ( $task_process_name -match 'nserver' ) {
			 $nserver_run = 1   
			 $amgrstatus = 1 
			 $serverstatus = 1
		}           
	}
	if (($nserver_run -eq 0) -and ( $avail -eq 0)) {
		script_printf( "<domino>server down</domino>" )
		Exit
	}
	
	$current_running_service = @(Get-Service)
	foreach ($service_infor in $current_running_service) {
		if ($service_infor.DisplayName -match 'Lotus Domino Server(\s*\S*)') {
			script_debug  $service_infor.Status  $debug_switch
			if($service_infor.Status -eq "Stopped"){           
				$avail = 0
			} elseif ($service_infor.Status -eq "Running") {
				$avail = 1
			} 
		} 
	}
	cd $domino_app_path
	$get_conf_ipaddr_file = $run_path+"\domino_tmp_config_ipaddr$rand.log" 
	script_debug $get_conf_ipaddr_file $debug_switch 
	nserver -c "show configuration TCPIP_TCPIPADDRESS >$get_conf_ipaddr_file" 2>$null
	
	$tryCount=0
	do {
		$tryCount++
	  if (Test-Path $get_conf_ipaddr_file) {
		 $tcpip_infors = get-content $get_conf_ipaddr_file 
		 foreach ($tcpip_infor in $tcpip_infors) {
			
			 if ($tcpip_infor -match 'TCPIP_TCPIPADDRESS=\d+,(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}):(\d+)') {
				 $domino_srv_ip = $matches[1]
				 $domino_srv_port = $matches[2] 
			 }
		 }
	  }
	  if ($tryCount -gt 10) {
		  $tryCount=0
		  break
		}
	} while ( $domino_srv_ip -eq "")
	
	
	#TCP Port listen status 
	$portlink = netstat -an 2>$null
	foreach ($portobject in $portlink) {
		if ($portobject -match '(\w+)\s*(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}):(\d+)\s*(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}):(\d+)\s*(\w*)') {  
			$listen_port=$matches[3]  
			$link_status=$matches[6]
			if ($listen_port -eq $domino_srv_port){
				if ($link_status -match 'LISTENING'){
					$httpstatus = 1
				}elseif ($link_status -match 'ESTABLISHED'){
					$httpstatus = 1
				}
				break
			}
		}
	}
	script_printf("<serverstatus>$serverstatus</serverstatus>")
	script_printf("<avail>$avail</avail>")
	script_printf("<amgrstatus>$amgrstatus</amgrstatus>")
	script_printf("<httpstatus>$httpstatus</httpstatus>")
	
	if (Test-Path $get_conf_ipaddr_file) {
		del $get_conf_ipaddr_file
    }
}

$paramOutFile=$args[1]
$prarmOutFileTmp="$paramOutFile.tmp"
$paramDominoPath=$args[0]

collect_avail

if (Test-Path $paramOutFile) {
	del $paramOutFile
}
rename-item $prarmOutFileTmp $paramOutFile