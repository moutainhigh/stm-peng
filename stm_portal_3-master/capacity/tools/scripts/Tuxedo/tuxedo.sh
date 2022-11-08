#!/bin/sh

#./tuxedo.sh /home/tuxedo
tuxedo_bin=`echo $1 | awk '{split($1,directory,"@");printf directory[1]}'`
#export system environment variable 
PATH="/usr/bin:/usr/local/bin:/usr/sbin:/usr/local/sbin:${tuxedo_bin}:${PATH}"
export PATH
OS=`uname`
if [ $OS = "AIX" ]
then
LANG=C
else
LANG=POSIX
fi
export LANG

#availability
if [ -n "`echo bbs | tmadmin -r 2>&1 | grep 'No bulletin board exists'`" ]
then
avail=0
elif [ -n "`echo bbs | tmadmin -r 2>&1 | grep 'Oracle'`" ]
then
avail=1
else
echo "<tuxedo>not tuxedo environment</tuxedo>"
exit 1
fi

maininfo=`tmadmin -v 2>&1 | awk '{split($3,one,",");split($5,two,",");printf "%s %s,%s",$2,one[1],two[1]}'`
#for(i=1;i<NF;i++){if($i=="Version"){split($(i+1),info,",");version=info[1];break}}
resource=`echo $maininfo | awk '{split($0,info,",");printf info[1]}'`
version=`echo $maininfo | awk '{split($0,info,",");printf info[2]}'`

if [ $avail = 0 ]
then
echo "tux_avail=$avail\n"
echo "tux_ver=$version\n"
echo "tux_res=$resource\n"
exit 1
fi

#main resource info
maininfo=`echo bbs | tmadmin -r 2>/dev/null | awk 'BEGIN{srv=0;svc=0;rq=0;gp=0;inf=0}
{if($0~/Current number of servers/)srv=$NF;
if($0~/Current number of services/)svc=$NF;
if($0~/Current number of request queues/)rq=$NF;
if($0~/Current number of server groups/)gp=$NF;
if($0~/Current number of interfaces/)inf=$NF;
}END{printf "%d,%d,%d,%d,%d", srv, svc, rq, gp, inf}'`
srv_num=`echo $maininfo | awk '{split($0,info,",");printf info[1]}'`
svc_num=`echo $maininfo | awk '{split($0,info,",");printf info[2]}'`
rq_num=`echo $maininfo | awk '{split($0,info,",");printf info[3]}'`
gp_num=`echo $maininfo | awk '{split($0,info,",");printf info[4]}'`
gp_infs=`echo $maininfo | awk '{split($0,info,",");printf info[5]}'`

host=`uname -n`
maininfo=`tmunloadcf | awk 'BEGIN{section="";model="";domainid="";lmid="";lmname="'$host'";mname="";maxclients=0;maxaccessers=0;mid="";nlsport=0;master="";backup=""}
{if(NF==1){if(index($1,"*")==1)section=$1}
if(section=="*RESOURCES"){
if($1=="MASTER"){if(split($2,node,",")>1){split(node[2],back,"\"");backup=back[2]}split(node[1],mas,"\"");master=mas[2]}
if($1=="DOMAINID"){split($2,domain,"\"");domainid=domain[2]}
if($1=="MODEL"){model=$2}
if($1=="MAXACCESSERS"){maxaccessers=$2}}
if(section=="*MACHINES"){
if($0~/LMID=/){split($1,name,"\"");mname=name[2];if(mname==lmname){split($2,id,"\"");lmid=id[2]}}
if($0~/MAXWSCLIENTS=/){split($1,max,"=");maxclients=max[2]}}
if(section=="*NETWORK"){
if(index($1,"\"")==1){split($1,ids,"\"");mid=ids[2]}
if(mid==lmid){if($0~/NLSADDR=/){split($1,port,":");nlsport=port[2]}}}
}END{printf "%s,%s,%s,%s,%d,%d,%s,%s,%d", model, domainid, lmid, lmname, maxclients, nlsport, master, backup, maxaccessers}'`
model=`echo $maininfo | awk '{split($0,info,",");printf info[1]}'`
domainid=`echo $maininfo | awk '{split($0,info,",");printf info[2]}'`
lmid=`echo $maininfo | awk '{split($0,info,",");printf info[3]}'`
lmname=`echo $maininfo | awk '{split($0,info,",");printf info[4]}'`
maxclients=`echo $maininfo | awk '{split($0,info,",");printf "%d", info[5]}'`
nlsport=`echo $maininfo | awk '{split($0,info,",");if(info[6]>0)printf "%d", info[6]}'`
master=`echo $maininfo | awk '{split($0,info,",");printf info[7]}'`
backup=`echo $maininfo | awk '{split($0,info,",");printf info[8]}'`
maxaccessers=`echo $maininfo | awk '{split($0,info,",");printf info[9]}'`

#syntactic error
#if [ -z "$nlsport" ]; then 
#echo "sed error---before"
#eval `tmunloadcf | grep -1 WSL | tail -1 | sed -r "s:^[^[:digit:]]*([[:digit:].]*)[^[:digit:]]*([[:digit:].]*).*([[:digit:]*]*)$:IP=\1 nlsport=\2:"`
#echo "sed error---after"
#fi

#syntactic error
#clientcount=`echo pclt | tmadmin -r 2>/dev/null | grep "$lmid" | grep -v WSH | grep -v tmadmin | wc -l`
#clientrate=`echo $clientcount $maxclients | awk '{printf "%.2f", 100.00*($1/$2)}'`

#svr info
svr_lines=`echo psr | tmadmin -r 2>/dev/null | awk 'BEGIN{start=0;maininfo="";del="\`";rt="\`SVREND\n"}
{if($0~/Prog Name/){start=1;getline;getline}
if(start=1){if(NF>1){
if(maininfo=="")maininfo=$1 del $2 del $3 del $4 del $5 del $6 rt;else maininfo=maininfo $1 del $2 del $3 del $4 del $5 del $6 rt;}}
}END{printf "%s", maininfo}'`

#svc info
svc_lines=`echo psc | tmadmin -r 2>/dev/null | awk 'BEGIN{start=0;maininfo="";del="\`";rt="\`SVCEND\n"}
{if($0~/Prog Name/){start=1;getline;getline}
if(start=1){if(NF>1){
if(maininfo=="")maininfo=$1 del $2 del $3 del $4 del $5 del $6 del $7 del $8 rt;else maininfo=maininfo $1 del $2 del $3 del $4 del $5 del $6 del $7 del $8 rt;}}
}END{printf "%s", maininfo}'`

#queue info
pq_lines=`echo pq | tmadmin -r 2>/dev/null | awk 'BEGIN{start=0;maininfo="";del="\`";rt="\`PQEND\n"}
{if($0~/Prog Name/){start=1;getline;getline}
if(start=1){if(NF>1){
if(maininfo=="")maininfo=$1 del $2 del $3 del $4 del $5 del $6 del $7 rt;else maininfo=maininfo $1 del $2 del $3 del $4 del $5 del $6 del $7 rt;}}
}END{printf "%s", maininfo}'`

#cpu_memory
pids=`(tmadmin -r 2>/dev/null<<!
echo
page
v
psr
!
) | awk 'BEGIN{pids="#";pid=0}
{if($0~/Process ID:/){split($3,p,",");pid=p[1];pids=pids pid "#"}}END{printf "%s", pids}'`

OS=`uname`
CMD=""

case $OS in
SunOS)
memsize=`prtconf 2>/dev/null|awk '/^Memory [Ss]ize/{printf "%d",$3*1024}'`
;;
Linux)
memsize=`cat /proc/meminfo 2>/dev/null|grep -i MemTotal |awk '{printf "%d",$2}'`
;;
FreeBSD)
memsize=`cat /var/run/dmesg.boot 2>/dev/null| grep -i "real memory" | awk '{printf "%d",$4/1024}'`
;;
AIX)
memsize=`/usr/sbin/prtconf -m 2>/dev/null| awk '{printf "%d",$3*1024}'`
;;
HP-UX)
memsize=`dmesg 2>/dev/null|awk '/lockable:/{printf "%d",$2}'`
;;
*)
;;
esac

case $OS in
HP-UX|AIX|SunOS|Linux|FreeBSD)
cpuloadmemfree=`vmstat 1 2 2>/dev/null|awk 'BEGIN{id_flag=0;idl=0;flag_fre=0;memfree=0;}
NR>1{if($0~/id/){for(i=1;i<=NF;i++){if($i~/id/)id_flag=i;if($i~/fre/)flag_fre=i;}}
idl=$id_flag;memfree=$flag_fre;}
END{printf "%.2f,%d", 100.00-idl, memfree}'`
cpuload=`echo $cpuloadmemfree | awk -F, '{printf "%.2f",$1;}'`
memfree=`echo $cpuloadmemfree | awk -F, '{printf "%d",$2}'`
;;
SCO_SV|UnixWare)
memsize=`memsize 2>/dev/null|awk '{printf "%d",$1/1024}'`
cpuload=`sar -u 1 2 2>/dev/null|awk 'BEGIN{idle=0;idloff=0;}
{if($0~/idle/){for(i=1;i<=NF;i++){if($i~/idle/)idloff=i;}}
else if(NF>1){idle=$idloff;}
}END{printf "%.2f", 100.00-idle}'`
memfree=`sar -r 1 2>/dev/null|awk 'BEGIN{memfree=0}
{while(getline >0){memfree=$2;}}
END{printf "%d",memfree*4}'`
;;
*)
;;
esac

case $OS in
SunOS|Linux|FreeBSD)
CMD="ps -eo 'pid pcpu rss vsz'"
;;
AIX)
CMD="ps -Ao 'pid pcpu rssize vsz'"
;;
HP-UX)
CMD="UNIX95= ps -eo 'pid pcpu sz vsz'"
;;
SCO_SV)
CMD="ps -elo 'pid pcpu size vsz'"
;;
UnixWare)
;;
*)
;;
esac

case $OS in
SunOS|Linux|FreeBSD|AIX|HP-UX|SCO_SV)
appcpumem=`sh -c "$CMD" 2>/dev/null|awk 'BEGIN{cpu=0.0;mem=0}
{if(index("'$pids'","#"$1"#")>0){cpu+=$2;mem+=$3}}END{printf "%.2f,%d",cpu,mem}'`
appcpu=`echo $appcpumem | awk '{split($0,info,",");printf info[1]}'`
appmemsize=`echo $appcpumem | awk '{split($0,info,",");printf info[2]}'`
;;
UnixWare)
appcpu=`ps -eo "pid pcpu" 2>/dev/null|awk 'BEGIN{cpu=0.0}
{if(index("'$pids'","#"$1"#")>0)cpu+=$2}END{printf "%.2f",cpu}'`
appmemsize=`ps -elyf 2>/dev/null|awk 'BEGIN{mem=0}
{if(index("'$pids'","#"$3"#")>0)mem+=$9}END{printf "%d",mem}'`
;;
*)
;;
esac

meminfo=`echo $memsize $memfree $appmemsize | awk '{printf "%.2f,%.2f",100.0*($1-$2)/$1,100.0*$3/$1}'`
memload=`echo $meminfo | awk '{split($0,info,",");printf info[1]}'`
appmem=`echo $meminfo | awk '{split($0,info,",");printf info[2]}'`

#output
printf "tux_avail=$avail\n"
printf "tux_res=$resource\n"
printf "tux_ver=$version\n"
printf "tux_maxclients=$maxaccessers\n"
printf "tux_master=$master\n"
printf "tux_backup=$backup\n"
printf "tux_domainid=$domainid\n"
printf "tux_model=$model\n"
printf "tux_lhost=$lmname\n"
printf "tux_lmid=$lmid\n"
printf "num_grp=$gp_num\n"
printf "num_infs=$gp_infs\n"
printf "num_svr=$srv_num\n"
printf "num_svc=$svc_num\n"
printf "num_rq=$rq_num\n"
printf "$svr_lines\n"
printf "$svc_lines\n"
printf "$pq_lines\n"