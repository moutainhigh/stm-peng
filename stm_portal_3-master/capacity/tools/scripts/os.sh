#!/bin/sh
PATH="/usr/bin:/usr/local/bin:/usr/sbin:/usr/local/sbin:${PATH}"
export PATH
LANG=POSIX
export LANG

ossp="";
hostname="";
memsize="";
osversion=""
systeminfo=""
macaddress=""

OS=`uname`

echo "<wiserv-os>"|awk '{printf $0}'

case $OS in
AIX)
ossp=`oslevel -r 2>/dev/null|awk 'BEGIN{out="";}{out=$1;}END{if(length(out)<1)exit 1;else printf "%s",out;}'`
if [ $? != 0 ];then
ossp=`oslevel 2>/dev/null|awk 'BEGIN{out="-";}{out=$1;}END{printf "%s",out}'`
fi;
ossp=`uname -a 2>/dev/null|awk '{printf "%s %s",$1,"'$ossp'"}'`
hostname=`uname -n`
memsize=`/usr/sbin/prtconf -m 2>/dev/null| awk '{printf "%d",$3}'`
osversion=`oslevel`
systeminfo=`uname -a`
macaddress=`entstat ent0|grep Hardware|awk '{printf $3}'`
;;
SunOS)
ossp=`uname -a 2>/dev/null|awk '{printf "%s %s", $1,$3}'`
hostname=`uname -n`
memsize=`prtconf|awk '/^Memory [Ss]ize/{printf "%d",$3}'`
osversion=`uname -r`
systeminfo=`uname -a`
macaddress=`ifconfig -a | grep ether | awk 'NR==1{printf $2}'`
;;
Linux)
ossp=`cat /etc/redflag-release 2>/dev/null ||
cat /etc/redhat-release 2>/dev/null ||
head -n 1 /etc/SuSE-release 2>/dev/null ||
cat /etc/mvl-release 2>/dev/null ||
cat /etc/wrs-release 2>/dev/null ||
if [ -f /etc/debian_version ];then 
cat /etc/debian_version 2>/dev/null | awk '
NR==1{if(length($0)==0)exit 1;
if($0 ~ /debian/)printf "%s",$0; else printf "debian linux %s",$1;}'; 
else cat /etc/debian_version 2>/dev/null; fi ||
if [ -r /etc/lsb-release ];then
cat /etc/lsb-release | awk -F= 'BEGIN{count=0;}{if($1~/DISTRIB_DESCRIPTION/){count = split($2,one,"\"");if(count==1){print one[1];}else{print one[2]}}}'
else
echo "Unknown Release"|awk '{printf $0}'
fi`
hostname=`uname -n`
memsize=`cat /proc/meminfo |grep -i MemTotal |awk '{printf "%d",$2/1024}'`
osversion=`uname -r`
systeminfo=`uname -s -r -n`
macaddress=`export LANG=uc_EN;LC_CTYPE=c;PATH=$PATH:/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin;ifconfig -a |grep HWaddr|awk 'NR==1{print $5}'`
;;
HP-UX)
ossp=`uname -a|awk '{printf "%s %s",$1,$3}'`
hostname=`uname -n`
memsize=`dmesg|awk '/lockable:/{printf "%d",$2/1024}'`
osversion=`uname -r`
systeminfo=`uname -a`
macaddress=`export PATH=$PATH:/bin:/sbin:/usr/bin:/usr/sbin;lanscan |awk 'NR==3{printf $2}'`
;;
SCO_SV|UnixWare)
ossp=`uname -svn|awk '{printf "%s %s",$1,$3}'`
hostname=`uname -n`
memsize=`memsize|awk '{printf "%d",$1/1024/1024}'`
osversion=`uname -v`
systeminfo=`uname -a`
macaddress=`ifconfig -a | grep ether | awk 'NR==1{printf $2}'`
;;
FreeBSD)
ossp=`uname -srn | awk '{printf "%s %s",$1,$3}'`
hostname=`uname -n`
memsize=`cat /var/run/dmesg.boot | grep -i "real memory" | awk '{printf "%d",$4/1024/1024}'`
osversion=`uname -r`
systeminfo=`uname -a`
macaddress=`ifconfig -a | grep ether | awk 'NR==1{printf $2}'`
;;
*)
;;
esac

echo "<os>$ossp</os><osversion>$osversion</osversion><systeminfo>$systeminfo</systeminfo><macaddress>$macaddress</macaddress><hostname>$hostname</hostname><memsize>$memsize</memsize>"|awk '{printf $0}'

echo "</wiserv-os>"|awk '{printf $0}END{print "\r\n"}'