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

function current_computer_name{
    get-content env:computername
}

#/##
# clear tmadmin console output contents 
#/
function console_clear{
    clear
}

function collect_perf(){
	
	$debug_switch = $False	
	
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
	$get_svr_sessions = $run_path+"\domino_tmp_server_sessions$rand.log"  
	nserver -c "show stat Server.Sessions.Dropped >$get_svr_sessions" 2>$null 
	Start-Sleep -m 500
	$get_http_reqtimes = $run_path+"\domino_tmp_http_requesttime$rand.log"  
	nserver -c "show stat Http.Worker.Total.Http.RequestTime >$get_http_reqtimes" 2>$null
	Start-Sleep -m 500
	$get_users = $run_path+"\domino_tmp_get_users$rand.log"  
	nserver -c "show users >$get_users"  2>$null
    
	
	#show stat Server.Sessions.Dropped
	if (Test-Path $get_svr_sessions ) {
	   $server_sessions = get-content $get_svr_sessions
	   foreach ($server_session in $server_sessions) {
		   if ($server_session -match 'Server.Sessions.Dropped\s?=\s?(\d*)') {
			   $dropsessioncount = $matches[1]
			   break
		   }
	   }      
	}
	script_printf( "<dropsessioncount>$dropsessioncount</dropsessioncount>") 
	#show stat Http.Worker.Total.Http.RequestTime 
	$httpresponsetime =0
	if (Test-Path $get_http_reqtimes ) {
	   $http_reqtimes = get-content $get_http_reqtimes
	   foreach ($http_reqtime in $http_reqtimes) {
		   if ($http_reqtime -match 'Http.Worker.Total.Http.RequestTime\s?=\s?(\d*)') {
			   $httpresponsetime = $matches[1]
			   break
		   }
	   }
	}
	script_printf( "<httpresponsetime>$httpresponsetime</httpresponsetime>")
	$sessioncount=0
	if (Test-Path $get_users ) {
	   $domino_users = get-content $get_users
	   foreach ($domino_user in $domino_users) {
		   if ($domino_user -match '(\w+/\w+)[\s*(\w+)\s*(\w*)]?') {
			   $sessioncount++           
		   }
	   }      
	}
	script_printf("<sessioncount>$sessioncount</sessioncount>")
	
	#nhttp.exe,nserver.exe 
	$current_computer = current_computer_name

	$process_domino_cpurate = 0.0
	$process_domino_memcount = 0
	$process_http_cpurate = 0.0
	$process_http_memcount = 0
	$process_http_id = 0
	$process_nserver_id = 0 
	$task_list = @(Get-Process)
	#get nhttp and nserver process id 
	foreach ($task_infor in $task_list) {   
	  $task_process_name = $task_infor.ProcessName                     
	  if ( $task_process_name -match 'nhttp' ) {
			 #$process_http_cpurate = $task_infor.CPU
			 $process_http_id = $task_infor.ID
			 $process_http_memcount = ($task_infor.WS/1024)  
	  }
	  if ( $task_process_name -match 'nserver' ) {
			 #$process_domino_cpurate = $task_infor.CPU
			 $process_nserver_id = $task_infor.ID
			 $process_domino_memcount = ($task_infor.WS/1024)  
	  }                     
	}  
	$Nhttp1 = $Dhttp1 =$Nhttp2 = $Dhttp2 = 0
	$Ndomino1 = $Ddomino1 =  $Ndomino2 = $Ddomino2 = 0 
	#calculate nhttp process cpu used rate 
	if ($process_http_id -gt 0) {
		$objInst_http_proc = gwmi -ComputerName $current_computer -class win32_perfformatteddata_perfproc_process | Where-Object { $_.IDProcess -eq "$process_http_id"}| select PercentProcessorTime,TimeStamp_Sys100NS
		$Nhttp1 = $objInst_http_proc.PercentProcessorTime
		$Dhttp1 = $objInst_http_proc.TimeStamp_Sys100NS  

		$objInst_http_proc2 = gwmi -ComputerName $current_computer -class win32_perfformatteddata_perfproc_process | Where-Object { $_.IDProcess -eq "$process_http_id"}| select PercentProcessorTime,TimeStamp_Sys100NS
		$Nhttp2 = $objInst_http_proc2.PercentProcessorTime
		$Dhttp2 = $objInst_http_proc2.TimeStamp_Sys100NS

		$Ndhttp = ($Nhttp2 - $Nhttp1)
		$Ddhttp = ($Dhttp2- $Dhttp1)
		if ($Ddhttp -eq 0) {
			$process_http_cpurate = 0
		} else {
			$process_http_cpurate = (($Ndhttp/$Ddhttp))  * 100
		}
	}

	#calculate nserver process cpu used rate   
	if($process_nserver_id -gt 0) {
		$objInst_domino_proc = gwmi -ComputerName $current_computer -class win32_perfformatteddata_perfproc_process | Where-Object { $_.IDProcess -eq "$process_nserver_id"} | select PercentProcessorTime,TimeStamp_Sys100NS
		$Ndomino1 = $objInst_domino_proc.PercentProcessorTime
		$Ddomino1 = $objInst_domino_proc.TimeStamp_Sys100NS

		$objInst_domino_proc2 = gwmi -ComputerName $current_computer -class win32_perfformatteddata_perfproc_process | Where-Object { $_.IDProcess -eq "$process_nserver_id"} | select PercentProcessorTime,TimeStamp_Sys100NS
		$Ndomino2 = $objInst_domino_proc2.PercentProcessorTime
		$Ddomino2 = $objInst_domino_proc2.TimeStamp_Sys100NS
		  
		$Ndomino = ($Ndomino2 - $Ndomino1)
		$Ddomino = ($Ddomino2- $Ddomino1)
		if ($Ddomino -eq 0) {
			$process_domino_cpurate = 0
		 } else { 
		   $process_domino_cpurate = (($Ndomino/$Ddomino))  * 100
		 }  
	}
	  
	script_debug  $process_http_cpurate  $debug_switch
	script_debug  $process_http_memcount $debug_switch
	script_debug  $process_domino_cpurate  $debug_switch
	script_debug  $process_domino_memcount $debug_switch
	script_debug  $current_computer $debug_switch
	#get computer TotalVisibleMemorySize 
	$mem = gwmi -ComputerName $current_computer win32_OperatingSystem
	
	$memload = "{0:0.0}" -f ((($mem.TotalVisibleMemorySize-$mem.FreePhysicalMemory)/$mem.TotalVisibleMemorySize)*100)
	$cpu_obj = gwmi -ComputerName $current_computer win32_Processor
	Start-Sleep -m 100 
	script_debug  $cpu_obj.LoadPercentage $debug_switch
	$cpuload ="1.0"
	foreach ($info in $cpu_obj) {
		if($info.LoadPercentage -gt 0) {
		   $cpuload = "{0:0.0}" -f $info.LoadPercentage
		}
	}

	$http_Permem = "{0:f2}" -f (($process_http_memcount/$mem.TotalVisibleMemorySize)) 
	$domino_Permem = "{0:f2}" -f (($process_domino_memcount/$mem.TotalVisibleMemorySize)) 
	$process_http_cpurate = "{0:f2}" -f $process_http_cpurate
	$process_domino_cpurate = "{0:f2}" -f $process_domino_cpurate

	script_printf("<cpurate>"+$cpuload+"</cpurate>")
	script_printf("<memrate>"+$memload+"</memrate>")
	script_printf("<appcpurate>"+$process_domino_cpurate+"</appcpurate>")
	script_printf("<appmemrate>"+$domino_Permem+"</appmemrate>")
	script_printf("<httpcpurate>"+$process_http_cpurate+"</httpcpurate>")
	script_printf("<httpmemrate>"+$http_Permem+"</httpmemrate>")
	
	if (Test-Path $get_svr_sessions){
		del  $get_svr_sessions
    }
    if (Test-Path $get_http_reqtimes) {
		del $get_http_reqtimes
    }
    if (Test-Path  $get_users) {
		del  $get_users
    }	
	
}

$paramOutFile=$args[1]
$prarmOutFileTmp="$paramOutFile.tmp"
$paramDominoPath=$args[0]

collect_perf
if (Test-Path $paramOutFile) {
	del $paramOutFile
}
rename-item $prarmOutFileTmp $paramOutFile