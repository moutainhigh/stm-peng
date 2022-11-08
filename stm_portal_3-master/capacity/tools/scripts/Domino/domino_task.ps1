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

function collect_task(){
	
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
	#$random = New-Object System.Random
	#$rand = $random.Next("1000")
	#change  path to domino data
	#cd $domino_app_path
	
	#read notes.ini
	$tokens=$null 
	$notes_ini_file = $domino_app_path+"\"+"notes.ini"
	script_debug  "notes.ini = $notes_ini_file"  $debug_switch
	if (Test-Path $notes_ini_file) {
	  $notes_ini_contents = get-content $notes_ini_file
	  script_debug  "notes.ini.contents = $notes_ini_contents"  $debug_switch
	  foreach ($notes_ini_content in $notes_ini_contents) {
		  script_debug  "notes.ini.line = $notes_ini_content"  $debug_switch
		  switch -regex ($notes_ini_content) {
			  "ServerTasks\s?=\s?(\S*)" {  
											$tasks=$matches[1]
											$tokens = $tasks.split(',')
											foreach ($token in $tokens) {
												if ($taskname -eq "") { $taskname = $token }else{ $taskname +="#"+$token }
											}
											break
										 }
		  }
	  }
	}  
	script_debug  "taskname = $taskname"  $debug_switch
	$current_process_list = @(Get-Process)
	$tmptask=$taskname
	$tokens = $tmptask.split('#')
	$task_in_process = 0
	script_debug  "tokens.Count= $tokens"  $debug_switch
	foreach ($task_single_name in $tokens) {      
	  $task_in_process = 0
	  script_debug  $task_single_name.ToLower()  $debug_switch
	  foreach ($task_infor in $current_process_list) {
		  $task_process_name = $task_infor.ProcessName
		  script_debug  "task_process_name = $task_process_name "  $debug_switch
		  if ( $task_process_name -match $task_single_name.ToLower()) { 
			   $task_in_process = 1
			   if ($taskstatus -eq "") { $taskstatus = "1" }else{ $taskstatus +="#1" }   
			   if ($taskenablestate -eq "") { $taskenablestate = "1" }else{ $taskenablestate +="#1" }
			   break
		  }          
	  } 
	  if ($task_in_process -eq 0) {
		   if ($taskstatus -eq "") { $taskstatus = "0" }else{ $taskstatus +="#0" }    
	  }   
	}

	$taskname_count = 0
	$taskname_tokens = $taskname.split('#')
	#转换成数组
	$taskstatus_tokens = $taskstatus.split('#')
	$taskenablestate_tokens = $taskenablestate.split('#')
	foreach ($status in $taskstatus_tokens) {
		$name_temp = $taskname_tokens[$taskname_count]
		if($name_temp -eq $null -or $name_temp -eq ''){
			$name_temp = "No Name"
		}
		
		$status_temp = $taskstatus_tokens[$taskname_count]
		if($status_temp -eq $null -or $status_temp -eq ''){
			$status_temp = "0"
		}
			
		$enable_temp = $taskenablestate_tokens[$taskname_count]
		if($enable_temp -eq $null -or $enable_temp -eq ''){
			$enable_temp = "0"
		}
		script_printf("<task>$taskname_count\t$name_temp\t$status_temp\t$enable_temp</task>")
		$taskname_count ++

	}
}

$paramOutFile=$args[1]
$prarmOutFileTmp="$paramOutFile.tmp"
$paramDominoPath=$args[0]

collect_task
if (Test-Path $paramOutFile) {
	del $paramOutFile
}
rename-item $prarmOutFileTmp $paramOutFile