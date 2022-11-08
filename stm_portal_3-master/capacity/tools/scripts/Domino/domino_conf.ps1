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

function collect_conf(){
	
	$debug_switch = $False	
	
	#check param.txt and then set env and change path 
	$MyInvocation = (Get-Variable MyInvocation -Scope 1).Value
	#script_printf($MyInvocation)
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
	#script_printf($domino_app_path)
	$get_server_infor = $run_path+"\domino_tmp_server$rand.log"  
    nserver -c "show server >$get_server_infor"  2>$null
	Start-Sleep -m 500

	$get_dir_nsf_file = $run_path+"\domino_tmp_directory_nsf$rand.log"
    script_debug $get_dir_nsf_file $debug_switch 
    nserver -c "show directory nsf >$get_dir_nsf_file" 2>$null
	Start-Sleep -m 500
	
	$get_srv_memory = $run_path+"\domino_tmp_server_memory$rand.log"  
	nserver -c "show memory >$get_srv_memory"  2>$null
	Start-Sleep -m 500
    
	
	script_debug  "show servers file path=$get_server_infor"  $debug_switch
	do {
		$tryCount++
		if (Test-Path $get_server_infor ) {
		
		 $domino_servers = get-content $get_server_infor 
		 script_debug  "show servers=$domino_servers"  $debug_switch
		 foreach ($domino_server in $domino_servers) {
	
			switch -regex ($domino_server) {
			   "Server name: \s*(\S*)\s*(\S*)\s*(\S*)"  {
															 $name = $matches[1]
															 $title = $matches[3]
														}
			   "Elapsed time: \s*(.*)"         {
															 $uptime = $matches[1]   
														}
			   "Server directory:  \s*(.*)\\"           {
															 $datapath = $matches[1]
														}
				}
			}   
		} 
		if ($tryCount -gt 10) {
		  $tryCount=0
		  break
		}  
	}while($name -eq "") 
	
	$nsfname = ""
	do {
		$tryCount++
		if (Test-Path $get_dir_nsf_file ) {
		   $directory_nsf_contents = get-content $get_dir_nsf_file
		   foreach ($directory_nsf_content in $directory_nsf_contents) {
			 script_debug  "directory_nsf_content = $directory_nsf_content"  $debug_switch
			   if ($directory_nsf_content -match '[a-z]:\\[^/:*?"<>|\r\n]*\\(\S*\.\w+)'){
			   script_debug  "matches = $matches[1]"  $debug_switch
				  if ($nsfname -eq "") {$nsfname=$matches[1]} else {$nsfname+="#"+$matches[1]} 
			   }           
			   if ($directory_nsf_content -match 'Total files:\s(\d*)') {
				   $nsfcount = $matches[1]
				   break
			   }
		   }     
		}
		if ($tryCount -gt 10) {
		  $tryCount=0
		  break
		}
	}while($nsfname -eq "")
	
	$memorysize ="0"
	do {
		$tryCount++
		if (Test-Path $get_srv_memory ) {
		   $server_memorys = get-content $get_srv_memory
		   foreach ($server_memory in $server_memorys) {
			   if ($server_memory -match 'Memory Available\s*\S*\s?\S*:\s*(.*)Mbytes') {
				   $memorysize = $matches[1]
					if ($memorysize -match '(\d+)(\,)?(\d+)(\.\d+)?') {
					if ($matches[4] -eq "") {
						$tmpsize=$matches[1]+$matches[3]
					}
					else 
					{
						$tmpsize=$matches[1]+$matches[3]+$matches[4]
					}
				 
				}
				$memorysize = "{0:f2}" -f ($tmpsize/1)
				break
			   }
		   }     
		}
		if ($tryCount -gt 10) {
		  $tryCount=0
		  break
		}
	}while( $memorysize -eq "")
	
	script_printf("<name>$name</name>")
	script_printf("<title>$title</title>")
	script_printf("<uptime>$uptime</uptime>")
	script_printf("<datapath>$datapath</datapath>")
	script_printf("<nsfcount>$nsfcount</nsfcount>")
	script_printf("<memorysize>$memorysize</memorysize>")
	
	if (Test-Path $get_server_infor) {
		del $get_server_infor
    }
	if (Test-Path $get_dir_nsf_file) {
		del $get_dir_nsf_file
    }	
	if (Test-Path $get_srv_memory) {
		del $get_srv_memory
    }
}

$paramOutFile=$args[1]
$prarmOutFileTmp="$paramOutFile.tmp"
$paramDominoPath=$args[0]

collect_conf
if (Test-Path $paramOutFile) {
	del $paramOutFile
}
rename-item $prarmOutFileTmp $paramOutFile