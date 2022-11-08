#!/bin/sh

PATH="/usr/bin:/usr/local/bin:/usr/sbin:/usr/local/sbin:${PATH}"
export PATH
LANG=POSIX
export LANG

dir=$1/bin

if [ -z "$dir" ]
then
printf "<cics><status>-1</status></cics>\r\n"
exit 0
fi

cics=`"$dir"/cicscli /L 2>/dev/null |awk 'BEGIN{env=0;status=0}{if($1=="CCL8001I")env=1;if($1=="CCL8041I")status=1}END{printf "%d,%d",env,status}'`
env=`echo $cics | awk -F, '{printf "%d",$1}'`
status=`echo $cics | awk -F, '{printf "%d",$2}'`
echo $env
echo $cics

if [ $env -eq 0 ]
then
printf "<cics>no cics environment</cics>\r\n"
exit 0
fi

printf "<cics><status>$status</status></cics>\r\n"
