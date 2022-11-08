#=============================================================================
# IBM Websphere MQ Windows platform collection script
# Note:The script through creating Windows PowerShell 2.0, it implementation 
#       collect IBM Webshpere MQ information via console command.
#
# Author:xiaopf@mainsteam.com
# Date:2014-12-25
# Version:0.1
# Update:2018-1-29
# Upadte Author:wangsh@mainsteam.com
# Update the MQ version information is not a problem.
#=============================================================================

#/##
# script output console
#/
function script_printf([string]$contents) {
  Write-Host $contents
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

function get_Version(){
	$version=""
	$debug_switch = $False
	
	#check param.txt and then set env and change path 
	$MyInvocation = (Get-Variable MyInvocation -Scope 1).Value
	$run_path = Split-Path $MyInvocation.MyCommand.Path
	$shell_fname = $MyInvocation.MyCommand.Name
    
	$app_path=$paramPath

	if(!$app_path.EndsWith("bin")) {
		if($app_path.EndsWith("\") -or $app_path.EndsWith("/")) {
			$app_path = $app_path + "bin"		
		}else{
			$app_path = $app_path + "\bin"
		}
	}
	
	#add $app_path to PATH
	$env:PATH="%$env:PATH%;C:\Windows\System32;$app_path"
	    
	cd $app_path
	
	$VersionResultPath = $run_path + "getMQVersion.log"

	if(Test-Path "mqver.exe") {
		#$VersionResultPath = $run_path + "getMQVersion.log"
		mqver -a > $VersionResultPath 2>$null
	}elseif(Test-Path "DSPMQVER.exe"){
		#$VersionResultPath = $run_path + "getMQVersion.log"
		dspmqver > $VersionResultPath 2>$null
	}
	
	#sleep 2

	script_debug $VersionResultPath $debug_switch 
	
  if (Test-Path $VersionResultPath) {
	 $infors = get-content $VersionResultPath
     
	 foreach ($info in $infors) {
	 	if ($info -match 'Version:\s+(\S+)') {
		 $version = $matches[1]
         $str15 = "version==========$version"
		 script_printf("<version>$version</version>")
	 	}
	 }		
	 
  } 
  if (Test-Path $VersionResultPath) {
		del $VersionResultPath
  }   
}

$paramOutFile=$args[1]

$prarmOutFileTmp="$paramOutFile.tmp"

$paramPath=$args[0]

get_Version
if (Test-Path $paramOutFile) {
	del $paramOutFile
}
rename-item $prarmOutFileTmp $paramOutFile


