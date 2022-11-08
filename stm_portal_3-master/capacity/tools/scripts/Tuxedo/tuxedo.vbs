Dim WShell
Set WShell = CreateObject("WScript.Shell")
Set objRegExp = New RegExp
Dim strCmd
Dim TUXBIN

Dim newFso
Dim paramFile
Set newFso = CreateObject("Scripting.FileSystemObject")
Set paramFile = newFso.getfile(wscript.scriptfullname)

Dim FileText
Dim OutputFile
Set InputParam = WScript.Arguments
If InputParam.Count = 1 Then
    FileText = InputParam(0)
End If

TUXBIN = ""
tux_avail = ""
tux_res = ""
tux_ver = ""
tux_maxclients = ""
tux_master = ""
tux_backup = ""
tux_domainid =""
tux_model = ""
tux_lhost = ""
tux_lmid = ""

num_grp = "0"
num_infs = "0"
num_svr = "0"
num_svc = "0"
num_rq = "0"

svr_lines = ""
svc_lines= ""
pq_lines = ""

removeFile = False

Sub Log(Message)
    'With CreateObject("Scripting.FileSystemObject")
        'CurrentPath = .GetParentFolderName(WScript.ScriptFullName)
        'Name = .GetBaseName(WScript.ScriptFullName)
        'FilePath = .BuildPath(CurrentPath, Name & ".log")
        'If Not removeFile Then
            'TODO
            'removeFile = True
        'End If
        'With .OpenTextFile(FilePath, 8, True)
            '.WriteLine Message
            '.Close
        'End With
    'End With
End Sub

Function Exec(File, Arguments, ArgsLogging)
    On Error Resume Next
    execOutput = ""
    execError  = ""
    
    myCmdText = "cmd /c " & """" & File & " " & Arguments  & """"
    If ArgsLogging Then
        Log "Running " & myCmdText
    End If
    Set objExec = WShell.Exec(myCmdText)
    'objExec.Sleep 100
    Do Until objExec.StdOut.AtEndOfStream
        Line = objExec.StdOut.ReadLine()
        If ArgsLogging Then
            Log "'" & Line
        End If
        execOutput = execOutput & Line & VbCrLf
    Loop
    Log VbCrLr

   If objExec.ExitCode <> 0 Then
        Log "Program '" & File & "' failed with code " & objExec.ExitCode & "."
        Log "StdOut" & execOutput
        
        Set fso = CreateObject("Scripting.FileSystemObject") 
        Set stdout = fso.GetStandardStream(1)
        stdout.WriteLine execOutput & " " & execError
        WScript.Quit objExec.ExitCode
    End If
    Exec = execOutput & execError
End Function

Sub CollVer
    strCmd = "echo quit | " & TUXBIN & "\tmadmin"
    strArg = " -v 2>&1"
    strRet = Exec(strCmd, strArg, True)
    'WScript.echo "xxx:" & strRet
    objRegExp.Pattern =  "\s(\S+\s\S+)\,\s\w+\s(\d+\.\d+\.\d+\.\d+)"
    Set matches = objRegExp.Execute(strRet)
    If (matches.Count > 0) Then
        Set match = matches(0)
        If match.SubMatches.Count > 1 Then
            tux_res = match.SubMatches(0)
            tux_ver = match.SubMatches(1)
        End If
    End If
End Sub

Function getNum(line)
    getNum = "0"
    objRegExp.Pattern =  "\s*(\d+)"
    Set tokens = objRegExp.Execute(line)
    If (tokens.Count = 1) Then
        Set token = tokens.Item(0)
        getNum = Trim(token)
    End If
End Function

Sub CollBbs
    Log "CollBbs"
    strCmd = "echo bbs |" & TUXBIN & "\tmadmin"
    strArg = "2>&1"
    strRet = Exec(strCmd, strArg, True)
    arr = Split(strRet, vbNewLine)
    For Each line In arr
        objRegExp.Pattern = "servers:\s*(\d+)"
        If objRegExp.test(line) Then
            num_svr = getNum(line)
        End If
        
        objRegExp.Pattern = "services:\s*(\d+)"
        If objRegExp.test(line) Then
            num_svc = getNum(line)
        End If
        
        objRegExp.Pattern = "queues:\s*(\d+)"
        If objRegExp.test(line) Then
            num_rq = getNum(line)
        End If
        
        objRegExp.Pattern = "groups:\s*(\d+)"
        If objRegExp.test(line) Then
            num_grp = getNum(line)
        End If

        objRegExp.Pattern = "interfaces:\s*(\d+)"
        If objRegExp.test(line) Then
            num_infs = getNum(line)
        End If
    Next
End Sub

function StartsWith(sLiteral, sToken)
    if (Len(sLiteral) = 0) or (Len(sToken) = 0) then
        StartsWith = false
    else
        StartsWith = InStr(LCase(sLiteral), LCase(sToken)) = 1
    end if
end function

SET mainMap = CreateObject("Scripting.Dictionary")

'*SERVERS
'"simpserv" SRVGRP="GROUP1" SRVID=1
'   CLOPT="-A" 
'   RQPERM=0666 REPLYQ=N    RPPERM=0666 MIN=1   MAX=1   CONV=N
'   SYSTEM_ACCESS=FASTPATH
'   MAXGEN=1    GRACE=86400 RESTART=N
'   MINDISPATCHTHREADS=0    MAXDISPATCHTHREADS=1    THREADSTACKSIZE=0
'   SICACHEENTRIESMAX="500"
Sub CollInfos
    Log "CollInfos"
    strCmd = TUXBIN & "\tmunloadcf"
    strArg = ""
    strRet = Exec(strCmd, strArg, True)
    arr = Split(strRet, vbNewLine)

    flagEntry = 0
    skip = False
    For Each line In arr
        If 1 = InStr(line, "*RESOURCES") Then
            flagEntry = 1
            skip = True
        ElseIf 1 = InStr(line, "*MACHINES") Then
            flagEntry = 2
            skip = True
        ElseIf 1 = InStr(line, "*SERVERS")Then
            flagEntry = 3
            skip = True
        ElseIf 1 = InStr(line, "*SERVICES") Then
            flagEntry = 4
            skip = True
        ElseIf 1 = InStr(line, "*SERVICES") Then
            flagEntry = 5
            skip = True
        ElseIf StartsWith(line, "*") Then
            flagEntry = 6
            skip = True
        Else 
            skip = False
        End If
        
        If (Not Skip) And flagEntry < 6 Then
         Select Case flagEntry
            Case 1
                objRegExp.Pattern = "(\w+)\s+(.+)"
                If objRegExp.test(line) Then
                    objRegExp.Global = True
                    objRegExp.Pattern =  "(\w+)\s+(.+)"
                    Set matches = objRegExp.Execute(line)
                    If (matches.Count > 0) Then
                        Set match = matches(0)
                        mainMap.add match.SubMatches(0) , match.SubMatches(1)
                    End If
                 End If
            Case 2
                '"WIN2003-TUXEDO" LMID="simple"
                'WScript.echo "zzzz:" & line
                objRegExp.Pattern = "LMID="
                If objRegExp.test(line) Then
                    objRegExp.Global = True
                    objRegExp.Pattern =  "\" & Chr(34) & "(\S+)" & "\" & Chr(34) & "\s+LMID=" & "\" & Chr(34) & "(\w+)" & "\" & Chr(34)
                    Set matches = objRegExp.Execute(line)
                    If (matches.Count > 0) Then
                        Set match = matches(0)
                        mainMap.add "LHOST", match.SubMatches(0)
                        mainMap.add "LMID", match.SubMatches(1)
                    End If
                 End If
            Case 3
            Case 4
            Case 5
            Case Else
            End Select
        End If
    Next
    tux_masterBoth = mainMap("MASTER")
    'WScript.echo "both:" & tux_masterBoth
        If StartsWith(tux_masterBoth,Chr(34)) Then
            tux_masterBoth = Mid(tux_masterBoth,2,Len(tux_masterBoth) - 2)
        End If
    'WScript.echo "bothafter:" & tux_masterBoth
    If InStr(tux_masterBoth,",") > 1 Then
        tux_master=Split(tux_masterBoth, ",")(0)
        tux_backup=Trim(Split(tux_masterBoth, ",")(1))
    Else
        tux_master = tux_masterBoth
    End If
    tux_model = mainMap("MODEL")
    tux_domainid = mainMap("DOMAINID")
    If StartsWith(tux_domainid,Chr(34)) Then
            tux_domainid = Mid(tux_domainid,2,Len(tux_domainid) - 2)
    End If
    tux_maxclients = mainMap("MAXWSCLIENTS")
    tux_lhost = mainMap("LHOST")
    tux_lmid = mainMap("LMID")
End Sub

Sub CollCliCnt
    Log "CollCliCnt"
    strCmd = "echo pclt |" & TUXBIN & "\tmadmin"
    strArg = " 2>&1"
    strRet = Exec(strCmd, strArg, True)
    arr = Split(strRet, vbNewLine)
    For Each line In arr
        
    Next
End Sub

Sub CollSvrs
    Log "CollSvrs"
    svr_lines = ""
    strCmd = "echo psr |" & TUXBIN & "\tmadmin"
    strArg = " 2>&1"
    strRet = Exec(strCmd, strArg, True)
    arr = Split(strRet, vbNewLine)
    flagStart = False
    For Each line In arr
        If Len(line) > 2 Then
            If flagStart Then
                'WScript.echo "LLLLLL:" & line
                objRegExp.Pattern = "(\S+)\s*(\S+)\s*(\S+)\s*(\S+)\s*(\S+)\s*(\S+)\s*\("
                Set matches = objRegExp.Execute(line)
                If (matches.Count > 0) Then
                    Set match = matches(0)
                    tmp_prog = match.SubMatches(0)
                    tmp_queue = match.SubMatches(1)
                    tmp_grp = match.SubMatches(2)
                    tmp_id = match.SubMatches(3)
                    tmp_rqdone= match.SubMatches(4)
                    tmp_loaddone = match.SubMatches(5)
                    tmp_line = tmp_prog & "`" & tmp_queue & "`" & tmp_grp & "`" & tmp_id & "`" & tmp_rqdone & "`" & tmp_loaddone & "`SVREND"
                    svr_lines = svr_lines & tmp_line & VbCrLf
                End If

            Else
                If StartsWith(line,"---") Then
                    flagStart = True
                End If
            End If
        End If
    Next
End Sub

'
'> tmadmin - Copyright (c) 2007-2008 Oracle.
'Portions * Copyright 1986-1997 RSA Data Security, Inc.
'All Rights Reserved.
'Distributed under license by Oracle.
'Tuxedo is a registered trademark.
'Service Name Routine Name Prog Name  Grp Name  ID    Machine  # Done Status
'------------ ------------ ---------  --------  --    -------  ------ ------
'TOUPPER      TOUPPER      simpserv.+ GROUP1     1     simple       0 AVAIL
'
'> 
Sub CollSvcs
    Log "CollSvcs"
    svc_lines = ""
    strCmd = "echo psc |" & TUXBIN & "\tmadmin"
    strArg = " 2>&1"
    strRet = Exec(strCmd, strArg, True)
    arr = Split(strRet, vbNewLine)
    flagStart = False
    For Each line In arr
        If Len(line) > 2 Then
            If flagStart Then
                objRegExp.Pattern = "(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)"
                Set matches = objRegExp.Execute(line)
                If (matches.Count > 0) Then
                    Set match = matches(0)
                    
                    tmp_svc_name = match.SubMatches(0)
                    tmp_routine_name = match.SubMatches(1)
                    tmp_prog_name =match.SubMatches(2)
                    tmp_grp_name = match.SubMatches(3)
                    tmp_svc_id = match.SubMatches(4)
                    tmp_svc_machine = match.SubMatches(5)
                    tmp_svc_done = match.SubMatches(6)
                    tmp_svc_status = match.SubMatches(7)
                    tmp_line = tmp_svc_name & "`" & tmp_routine_name & "`" & tmp_prog_name & "`" & tmp_grp_name & "`" & tmp_svc_id & "`" & tmp_svc_machine & "`" & tmp_svc_done & "`" & tmp_svc_status & "`SVCEND"
                    svc_lines = svc_lines & tmp_line & VbCrLf
                End If

            Else
                If StartsWith(line,"---") Then
                    flagStart = True
                End If
            End If
        End If
    Next
End Sub

'
'> tmadmin - Copyright (c) 2007-2008 Oracle.
'Portions * Copyright 1986-1997 RSA Data Security, Inc.
'All Rights Reserved.
'Distributed under license by Oracle.
'Tuxedo is a registered trademark.
'TMADMIN_CAT:199: WARN: Cannot become administrator.Limited set of commands available.
'Prog Name      Queue Name  # Serve Wk Queued  # Queued  Ave. Len    Machine
'---------      ------------------- ---------  --------  --------    -------
'simpserv.exe   00001.00001       1         -         0         -     simple
'BBL.exe        123456            1         -         0         -     simple
'
'> 
Sub CollPqs
    Log "CollPqs"
    pq_lines = ""
    strCmd = "echo pq |" & TUXBIN & "\tmadmin"
    strArg = " -r 2>&1"
    strRet = Exec(strCmd, strArg, True)
    arr = Split(strRet, vbNewLine)
    flagStart = False
    For Each line In arr
        If Len(line) > 2 Then
            If flagStart Then
                tmp_line = ""
                objRegExp.Pattern = "(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)"
                Set matches = objRegExp.Execute(line)
                If (matches.Count > 0) Then
                    Set match = matches(0)
                    tmp_q_prog = match.SubMatches(0)
                    tmp_q_queue = match.SubMatches(1)
                    tmp_q_serve =match.SubMatches(2)
                    tmp_q_wk_queued = match.SubMatches(3)
                    tmp_q_queued = match.SubMatches(4)
                    tmp_q_ave_len = match.SubMatches(5)
                    tmp_q_machine = match.SubMatches(6)
                    tmp_line = tmp_q_prog & "`" & tmp_q_queue & "`" & tmp_q_serve & "`" & tmp_q_wk_queued & "`" & tmp_q_queued & "`" & tmp_q_ave_len & "`" & tmp_q_machine & "`PQEND"            
                End If
                pq_lines = pq_lines & tmp_line & VbCrLf
            Else
                If StartsWith(line,"---") Then
                    flagStart = True
                End If
            End If
        End If
    Next
End Sub

Sub CollOther
    CollVer
    CollBbs
    CollCliCnt
    CollInfos
    CollSvrs
    CollSvcs
    CollPqs
End Sub


Function OrgRet
    OrgRet = "mainRes:" & tuxedo_res & VbCrLf
    OrgRet = OrgRet & "mainVer:" & tuxedo_ver & VbCrLf
    OrgRet = OrgRet & "mainVer:" & tuxedo_ver & VbCrLf
End Function

Function CollAvail
    Log "CollAvail BEG"
    CollAvail = "-1"
    strCmd = "echo quit | " & TUXBIN & "\tmadmin"
    strArg = " 2>&1"
    strRet = Exec(strCmd, strArg, True)
	'WScript.echo "strRet:" & strRet
    If InStr(strRet, "No bulletin board exists") > 0 Then
        CollAvail = "0"
    ElseIf InStr(strRet, "Oracle") > 0 Then
        CollAvail = "1"
    Else
        CollAvail = "-1"
    End If
    Log "CollAvail END"
End Function

Function OutLines
    lines_op = ""
    lines_op = lines_op & "tux_res=" & tux_res & VbCrLf

    lines_op = lines_op & "tux_ver=" & tux_ver & VbCrLf
    lines_op = lines_op & "tux_maxclients=" & tux_maxclients  & VbCrLf
    lines_op = lines_op & "tux_master=" & tux_master & VbCrLf

    lines_op = lines_op & "tux_backup=" & tux_backup & VbCrLf
    lines_op = lines_op & "tux_domainid=" & tux_domainid & VbCrLf
    lines_op = lines_op & "tux_model=" & tux_model & VbCrLf
    lines_op = lines_op & "tux_lhost=" & tux_lhost & VbCrLf
    lines_op = lines_op & "tux_lmid=" & tux_lmid & VbCrLf
    lines_op = lines_op & "num_grp=" & num_grp & VbCrLf
    lines_op = lines_op & "num_infs=" & num_infs & VbCrLf
    lines_op = lines_op & "num_svr=" & num_svr & VbCrLf
    lines_op = lines_op & "num_svc=" & num_svc & VbCrLf
    lines_op = lines_op & "num_rq=" & num_rq & VbCrLf
    lines_op = lines_op & svr_lines
    lines_op = lines_op & svc_lines
    lines_op = lines_op & pq_lines
    OutLines = lines_op
End Function

Log FileText

Set WshProEnv = WShell.Environment("PROCESS")
Idx = InStr(FileText,"+")
If Idx > 0 Then
    strTuxDir = Split(FileText,"+")(0)
    strAppDir = Split(FileText,"+")(1)
    WshProEnv.Item("TUXDIR")=strTuxDir
    WshProEnv.Item("TUXBIN")=strTuxDir & "\bin"
    WshProEnv.Item("APPDIR")=strAppDir
    WshProEnv.Item("TUXCONFIG")=strAppDir & "\tuxconfig"
    'WScript.echo "bin:" & WshProEnv.Item("TUXBIN")
    'WScript.echo "config:" & WshProEnv.Item("TUXCONFIG")
End If

'Set WshProEnv = WShell.Environment("PROCESS")
TUXBIN = WshProEnv.Item("TUXBIN")
TUXCONFIG = WshProEnv.Item("TUXCONFIG")

If "" = TUXCONFIG Or Null = TUXCONFIG Then
    tux_avail = -2
Else
    TUXBIN = WshProEnv.Item("TUXBIN")
    objRegExp.Global = True
    tux_avail=CollAvail
    'WScript.echo "tux_avail::" & tux_avail
End If

If tux_avail = "1" Then
    CollOther
End If
lines = "tux_avail=" & tux_avail & VbCrLf
lines = lines & OutLines
WScript.echo lines