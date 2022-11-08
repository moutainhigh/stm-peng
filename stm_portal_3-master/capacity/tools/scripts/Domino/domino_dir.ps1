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

function collect_dir(){
	
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
	$get_srv_directory = $run_path+"\domino_tmp_server_direcotry$rand.log"
	#script_printf($domino_app_path)
	nserver -c "show directory >$get_srv_directory"  2>$null
	Start-Sleep -m 500
	
	#show directory
	$last=""
	$datasubdircount =0
	$datachilddirpath=""
	$datachilddirname=""
	$datachildsubdircount=""
	$childsubdircount=1
	$datachilddirsize=""
	$parts = ""
	if ($domino_data_path -ne "") {
	$parts = $domino_data_path.split('\')
	}
	$tempath=""
	foreach ($part in $parts)
	{
	$tempath+=$part + "\\"
	}

	$data = 0
	script_debug $tempath $debug_switch
	do {
	$tryCount++
	if (Test-Path $get_srv_directory ) {
	   $server_inner_dirs = get-content $get_srv_directory
	   script_debug  $server_inner_dirs $debug_switch
	   $datachildsubfilecount="" 
	   foreach ($server_inner_dir in $server_inner_dirs) {
		 
		   switch -regex ($server_inner_dir) {
						  "$tempath(([^\\\?\/\*\|<>:""]+)\\)+" {
																	  if ($last -match $matches[2]) {
																		   $childsubdircount++
																	   }else {
																		   $childsubdircount = 0
																		   $last = $matches[2]
																		   $data_path=$matches[0]
																		   $ret= $matches[0] -match '([^\\\?\/\*\|<>:""]+)\\(w+)'
																		   $lastDir = $matches[2]
																		   if ($lastDirs -match $lastDir)
																		   {
																			  $sameDirPath++
																		   }else {
																				$datasubdircount++
																				$lastDirs +=  $lastDir + '#'
																				#if ($datachilddirpath -eq "") { $datachilddirpath = $matches[0] }else{ $datachilddirpath +="#"+$matches[0] }
																				$data = $matches[0]
																				$dir_total_size = (get-childitem $matches[0] | measure-object length -sum).sum                                                                           
																				#if ($datachilddirsize -eq "") { $datachilddirsize = "$dir_total_size" }else{ $datachilddirsize +="#$dir_total_size" }
																				$data += "\t" + $dir_total_size
																				$dir_files_count ="0"
																				$tmpDirs=get-childItem $data_path | where-object {$_.mode -match "d"}   
																				$tmpFirle=get-childItem $data_path | where-object {$_.mode -match "a"}
																				if ($tmpFirle.Count -eq "") { 
																				   $fileCount=0
																				} else {
																					$fileCount=$tmpFirle.Count 
																				}
																				if ($tmpDirs.Count -eq "") { 
																				   $dirCount=0
																				} else {
																					$dirCount=$tmpDirs.Count 
																				}
																				$dir_files_count = $fileCount - $dirCount
																				#if ($datachildsubfilecount -eq "") { $datachildsubfilecount = "$dir_files_count" }else{ $datachildsubfilecount +="#"+ "$dir_files_count" } 
																				$data += "\t" + $dir_files_count
																				#if ($datachilddirname -eq "") { $datachilddirname = $lastDir }else{ $datachilddirname +="#"+$lastDir }
																				$data += "\t" + $lastDir
																				$theDirMap=get-childItem $data_path | where-object {$_.mode -match "d"} 
																				if ($theDirMap.Count -gt 0) {
																					 #if ($datachildsubdircount -eq "") { $datachildsubdircount =  $theDirMap.Count }else{ $datachildsubdircount +="#" +$theDirMap.Count }   
																					 $data += "\t" + $theDirMap.Count
																				} else  {
																					 #if ($datachildsubdircount -eq "") { $datachildsubdircount = "0" }else{ $datachildsubdircount +="#0" }
																					 $data += "\t" + 0
																				}
																				script_printf("<data>$datasubdircount\t$data</data>")
																				
																			}
																	   }
												  }
			}
		   
		   
		}    
	}
	if ($tryCount -gt 10) {
		$tryCount=0
		break
	}
	}while($datachilddirpath -eq "")
	script_printf("<datasubdircount>$datasubdircount</datasubdircount>")
	
	
	
	if (Test-Path $get_srv_directory) {
		del $get_srv_directory
    }
	
}

$paramOutFile=$args[1]
$prarmOutFileTmp="$paramOutFile.tmp"
$paramDominoPath=$args[0]

collect_dir
if (Test-Path $paramOutFile) {
	del $paramOutFile
}
rename-item $prarmOutFileTmp $paramOutFile