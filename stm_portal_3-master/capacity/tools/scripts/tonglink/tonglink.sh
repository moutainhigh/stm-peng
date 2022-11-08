#!/bin/sh

PATH="/usr/bin:/usr/local/bin:/usr/sbin:/usr/local/sbin:${PATH}"
export PATH
LANG=POSIX
export LANG

dir=$1/bin

if [ -z "$dir" ]
then
printf "status=0"
exit 0
fi

rd=`date +%Y%m%d%H%M%S`
status=0
config=`$dir/tlqstat -Z1 -c -1 -2 | sed "/^\s*$/d" | awk '$1=$1' | sed 's/\ //g'| grep '[Config]'`
if [ $? -eq 0 ]
then
    status=1
fi

maininfo=`$dir/tlqstat -Z1 -c -1 -2 | sed "/^\s*$/d" | awk '$1=$1' | sed 's/\ //g'|awk -F'=' 'BEGIN{TLQVer="";SelfPort="";MyName="";Alias="";UpNodeNum="";DnNodeNum=""}
{
if($0~/TLQVer\=/)TLQVer=$NF;
if($0~/SelfPort\=/)SelfPort=$NF;
if($0~/MyName\=/)MyName=$NF;
if($0~/Alias\=/)Alias=$NF;
if($0~/UpNodeNum\=/)UpNodeNum=$NF;
if($0~/DnNodeNum\=/)DnNodeNum=$NF;
}END{printf "TLQVer=%d\tSelfPort=%d\tMyName=%s\tAlias=%s\tUpNodeNum=%d\tDnNodeNum=%d",TLQVer,SelfPort,MyName,Alias,UpNodeNum,DnNodeNum}'`
maininfo="status=$status\n$maininfo"
#rec queue details
$dir/tlqstat -Z1 -e | awk '{L[NR]=$0}END{for (i=3;i<=NR-2;i++){print L[i]}}' > /tmp/qz_rec_tmp$rd.txt

totalNum=0
totalmax=0
cat /tmp/qz_rec_tmp$rd.txt | while read line
do
	recname=`echo $line | awk -F' ' '{print $2}'|sed  's/\[//g;s/\]//g'`
	$dir/tlqstat -Z1 -r $recname >/tmp/qz_rec_detail$rd.txt
	num=`cat /tmp/qz_rec_detail$rd.txt | grep RcvMsgsLinkHandle | awk -F ' ' '{print $4}' |awk -F '=' '{print $2}'`
	max=`cat /tmp/qz_rec_detail$rd.txt | grep RcvMsgsLinkHandle | awk -F ' ' '{print $5}' |awk -F '=' '{print $2}'`
	numOfMessageEvent=`cat /tmp/qz_rec_detail$rd.txt | awk 'NR==1'|awk -F' ' '{print $7}' | awk -F'=' '{print $2}'`
	if [ "X$num" != "X" ]; then
		let "totalNum+=num"
	fi
	if [ "X$max" != "X" ]; then
		let "totalmax+=max"
	fi
	echo "recqueue $line  $numOfMessageEvent" >> /tmp/qz_recresult$rd.txt 
done
recRatio=0
if [ $totalmax -ne 0 ]
then
	echo abc
	recRatio=`awk 'BEGIN{printf '$totalNum'/'$totalmax'}'`
fi
cat /tmp/qz_recresult$rd.txt | sed "s/$/& $recRatio/g" >>/tmp/qz_tlq_result$rd.txt

printf "\n"
#send queue details
$dir/tlqstat -Z1 -d | awk '{L[NR]=$0}END{for (i=3;i<=NR-5;i++){print L[i]}}' > /tmp/qz_send_tmp$rd.txt
sendNum=0
sendMax=0

#touch /tmp/qz_sendresult$rd.txt
cat /tmp/qz_send_tmp$rd.txt | while read line
do
	sendname=`echo $line | awk -F' ' '{print $2}'|sed  's/\[//g;s/\]//g'`
	$dir/tlqstat -Z1 -s $sendname >/tmp/qz_send_detail$rd.txt
	#num=`cat /tmp/qz_send_detail.txt | grep SNDMSGLINKHEAD | awk -F ' ' '{print $5}' |awk -F '=' '{print $2}'`
	cat /tmp/qz_send_detail$rd.txt | grep SNDMSGLINKHEAD | awk -F ' ' '{print $5}' > /tmp/qz_send_num$rd.txt
	cat /tmp/qz_send_num$rd.txt | while read ll 
	do
		sendN=`echo $ll | awk -F '=' '{print $2}'`
		let "sendNum+=sendN"
	done
	#max=`cat /tmp/qz_send_detail.txt | grep SNDMSGLINKHEAD | awk -F ' ' '{print $6}' |awk -F '=' '{print $2}'`
	cat /tmp/qz_send_detail$rd.txt | grep SNDMSGLINKHEAD | awk -F ' ' '{print $6}' > /tmp/qz_send_tot$rd.txt
	cat /tmp/qz_send_tot$rd.txt | while read ll 
	do
		sendT=`echo $ll | awk -F '=' '{print $2}'`
		let "sendMax+=sendT"
	done
	sendMessageNum=`cat /tmp/qz_send_detail$rd.txt | awk 'NR==1'|awk -F' ' '{print $6}' | awk -F'=' '{print $2}'`
	echo "sendqueue $line  $numOfMessageEvent" >> /tmp/qz_sendresult$rd.txt
	echo "$line"
done
sendRatio=0
if [ $sendMax -ne 0 ]
then
	echo abc
	recRatio=`awk 'BEGIN{printf '$sendNum'/'$sendMax'}'`
fi
sed "s/$/& $recRatio/g" /tmp/qz_sendresult$rd.txt >>/tmp/qz_tlq_result$rd.txt
echo $maininfo
cat /tmp/qz_tlq_result$rd.txt

rm -f /tmp/qz_*$rd.txt