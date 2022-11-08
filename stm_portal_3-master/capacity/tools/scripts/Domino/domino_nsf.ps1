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

#/##
# get current computer name
#/
function current_computer_name{
    get-content env:computername
}

#/##
# run script to collection domino8.5 running information
#/
function collection_domino_nsf {
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
   #check service availablility
  $current_running_service = @(Get-Service)
  foreach ($service_infor in $current_running_service) {
    if ($service_infor.DisplayName -match 'Lotus Domino Server(\s*\S*)') {
        if($service_infor.Status -eq "Stopped"){        
            $avail = 0           
        } elseif ($service_infor.Status -eq "Running") {
            $avail = 1
        } 
    } 
  }  
  $serverstatus = 0
  $nserver_run = 0
   #amgrstatus 
  $task_list = @(Get-Process)
  foreach ($task_infor in $task_list) {   
    $task_process_name = $task_infor.ProcessName                    
    if ( $task_process_name -match 'nserver' ) {
         $nserver_run = 1
         $avail = 1
         $serverstatus = 1		 
    }   
 } 
 if (($nserver_run  -eq 0) -and ( $avail -eq 0)) {
     script_printf( "<domino>server down</domino>" )
     Exit
 }
 
  ########################################################################################
  #					get a random number                                                  #
  ########################################################################################
  $random = New-Object System.Random
  $rand = $random.Next("1000")
  
  #change  path to domino data
  cd $domino_app_path
  
  $get_dir_nsf_file = $run_path+"\"+"directory_nsf_$rand.log"
  #script_printf($get_dir_nsf_file)
  #new-item -path $get_dir_nsf_file -itemtype file -force
  nserver -c "show directory nsf >$get_dir_nsf_file" 
  
  #sleep -m 500
  
  $nsfcount=0
  $nsfpath=""
  $Start1 = Get-Date
  while($True){
	$End1 = Get-Date
	$Timespan1 = New-Timespan $Start1 $End1
	if (Test-Path $get_dir_nsf_file) {		
	#script_printf($run_path)
		$nsf_dir_list = (Get-Content $get_dir_nsf_file) | Sort-Object
		foreach ($nsf_dir in $nsf_dir_list) {
			script_debug $nsf_dir $debug_switch
			#script_printf($nsf_dir)
			if ($nsf_dir -match '([a-zA-Z]:\\[^/:*?"<>|\r\n]*\.\w+)(.*)') {
				#script_printf("hello world.")
				$nsfcount++ 
				if ($nsfpath -eq "") {$nsfpath=$matches[1]} else {$nsfpath+="#"+$matches[1]}
			} 
		}
		break	
		
	}elseif($Timespan1.TotalSeconds -ge 5){
		break
	}
  }
  if(Test-Path $get_dir_nsf_file){
		del $get_dir_nsf_file
  }
  
  
  $nsfname=""  
  $get_lotus_database_file = $run_path+"\"+"database_nsf_$rand.log" 
  $index=0
  
  script_debug $nsfpath $debug_switch
  $dir_list =$nsfpath.split('#')
  $dirsStartDate = Get-Date
  foreach ($directory in $dir_list) {
        script_debug $directory $debug_switch
      if ($directory -match '[a-z]:\\[^/:*?"<>|\r\n]*data\\(\S*\.\w+)') {
          $index++
          $value = $matches[1]
          $get_lotus_database_file = $run_path+"\"+"wiserv_tmp"+$index+"_"+$rand+".log" 
		  if ($directory -match '[a-z]:\\[^/:*?"<>|\r\n]*\\(\S*\.\w+)') {
			if ($nsfname -eq "") {$nsfname=$matches[1]} else {$nsfname+="#"+$matches[1]} 
		  }
          nserver -c "show database  $value >$get_lotus_database_file"
		  $Start2 = Get-Date
		  while($true){
			$End2 = Get-Date
			$timespan2 = New-Timespan $Start2 $End2
			if((Test-Path $get_lotus_database_file) -or ($timespan2.TotalSeconds -ge 5)){
				break
			}	
		  }
      #if (Test-Path $get_lotus_database_file) {
      #      del $get_lotus_database_file
      #  }	
          #sleep -m 600 
          $databaseCount ++
          
      }
  }
  $dirEndDate = Get-Date
  $subDate = New-Timespan $dirsStartDate $dirEndDate
  #script_printf("Show-Datebase Use Time ---- " + $subDate.TotalSeconds.ToString())  

  #sleep 10
  $nsftitle=""
  $nsfdocument=""
  $count=0  
  $line=0
  $extractStart = Get-Date
  while( $count -lt $nsfcount+1 ) {  
      $count++
      $lotus_database_file = $run_path+"\wiserv_tmp"+$count+"_"+$rand+".log" 
	  
	  #script_printf("$lotus_database_file")
      #write-host  "all data path" $lotus_database_file
      
      if (Test-Path $lotus_database_file) {	
       # write-host "in path exist:"  $lotus_database_file
          $nsfinfors = Get-Content $lotus_database_file
          foreach($nsfinfo in $nsfinfors) {
              $line++
              if ($line -eq 1) {
                  if ($nsftitle -eq "") {$nsftitle=$nsfinfo} else {$nsftitle+="#"+$nsfinfo}
              } 
			  
              if ($nsfinfo -match 'Documents \s*(\d+(\,?\d+)?)\s*(\d+(\,?\d+)?)') {
			  
                  $tmp1=$matches[1] 
                 # if ($tmp1-match '(\d+)(\,(\d+))?') {
                 #   $ret = $matches[1]+$matches[3]
				 # 	if ($nsfdocument -eq "") {$nsfdocument=$ret} else {$nsfdocument+="#"+$ret}
                 # } else {
				  if ($nsfdocument -eq "") {$nsfdocument=$tmp1} else {$nsfdocument+="#"+$tmp1}
				  #}				  
               
                  break 
              } 
            
          }#end foreach
		$line=0
		del $lotus_database_file	
      }      
  }
  $extractEnd = Get-Date
  $extractSpan = New-Timespan $extractStart $extractEnd
  #script_printf("Extract Str Use Time ---  " + $extractSpan.TotalSeconds.ToString())
  
  
  $appendStart = Get-Date
  
  $nsfpath_array = $nsfpath.split("#")
  $nsfname_array = $nsfname.split("#")
  $nsftitle_array = $nsftitle.split("#")
  $nsfdoc_array = $nsfdocument.split("#")
  $appendCount = 0
  $result = ""
  foreach($obj in $nsfname_array){
	$nsftitle_temp = "No Title"
    if(!($appendCount -ge $nsftitle_array.length -or $nsftitle_array[$appendCount] -eq $null -or $nsftitle_array[$appendCount] -eq '')){
		$nsftitle_temp = $nsftitle_array[$appendCount]
	}
	$nsfpath_temp = "No Path"
	if(!($appendCount -ge $nsfpath_array.length -or $nsfpath_array[$appendCount] -eq $null -or $nsfpath_array[$appendCount] -eq '')){
		$nsfpath_temp = $nsfpath_array[$appendCount]
	}
	$nsfdoc_temp = "0"
	if(!($appendCount -ge $nsfname_array.length -or $nsfdoc_array[$appendCount] -eq $null -or $nsfdoc_array[$appendCount] -eq '')){
		$nsfdoc_temp = $nsfdoc_array[$appendCount].replace(",", "")
	}
	
	$result ="<nsf>$appendCount\t" + $obj + "\t" + $nsftitle_temp + "\t" + $nsfpath_temp + "\t" + $nsfdoc_temp + "</nsf>"
	script_printf($result)
	$appendCount ++
  }
  
  $appendEnd = Get-Date
  $appendSpan = New-Timespan $appendStart $appendEnd
  #script_printf("Append Use Time ---  " + $appendSpan.TotalSeconds.ToString())
  
  #script_printf( "<domino><nsfcount>$nsfcount</nsfcount><nsfname>$nsfname</nsfname><nsfpath>$nsfpath</nsfpath>" )
  #script_printf("<nsftitle>$nsftitle</nsftitle>")
  #script_printf( $result)
  
}

$paramOutFile=$args[1]
$prarmOutFileTmp="$paramOutFile.tmp"
$paramDominoPath=$args[0]

collection_domino_nsf
if (Test-Path $paramOutFile) {
  del $paramOutFile
}
rename-item $prarmOutFileTmp $paramOutFile
