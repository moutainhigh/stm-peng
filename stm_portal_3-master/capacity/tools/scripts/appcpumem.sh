#!/bin/sh
PATH="/usr/bin:/usr/local/bin:/usr/sbin:/usr/local/sbin:${PATH}"
export PATH
LANG=POSIX
export LANG

if [ -n "$1" ];
then
    DIR=$1
fi

if [ -n "$2" ];
then
    THEOWNER=$2
fi

if [ -n "$THEOWNER" ];
then
    haveuserflag=`cat /etc/passwd | awk 'BEGIN{out="";}/^'$THEOWNER':/{out=1;}END{print out;}'`
    if [ -z "$haveuserflag" ];
    then
        echo "<wiserv-appperf>user not exist</wiserv-appperf>";
        exit 1;
    fi
fi

OS=`uname`

memsize=0;

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
cpuload=`sar -u 1 2 2>/dev/null|awk 'BEGIN{idle=0;idloff=0;}
{if($0~/idle/){for(i=1;i<=NF;i++){if($i~/idle/)idloff=i;}}
else if(NF>1){idle=$idloff;}
}END{printf "%.2f", 100.00-idle}'`
memfree=`sar -r 1 2>/dev/null|awk 'BEGIN{memfree=0}
{while(getline >0){memfree=$2;}}
END{printf "%d",memfree*4}'`
memsize=`memsize 2>/dev/null|awk '{printf "%d",$1/1024}'`
;;
*)
;;
esac

case $OS in
SunOS)
memsize=`prtconf 2>/dev/null|awk '/^Memory [Ss]ize/{printf "%d",$3*1024}'`
cpumem=`ps -eo "user pcpu rss vsz args" 2>/dev/null|egrep "$DIR"|egrep -v egrep|cut -c1-512`
;;
Linux)
memsize=`cat /proc/meminfo 2>/dev/null|grep -i MemTotal |awk '{printf "%d",$2}'`
cpumem=`ps awwx -o "user pcpu rss vsz args" 2>/dev/null|egrep "$DIR"|egrep -v egrep|cut -c1-512`
;;
FreeBSD)
memsize=`cat /var/run/dmesg.boot 2>/dev/null| grep -i "real memory" | awk '{printf "%d",$4/1024}'`
cpumem=`ps -awwxo "user pcpu rss vsz args" 2>/dev/null|egrep "$DIR"|egrep -v egrep|cut -c1-512`
;;
AIX)
memsize=`/usr/sbin/prtconf -m 2>/dev/null| awk '{printf "%d",$3*1024}'`
cpumem=`ps -eo "user pcpu rssize vsz args" 2>/dev/null|egrep "$DIR"|egrep -v egrep|cut -c1-512`
;;
HP-UX)
memsize=`dmesg 2>/dev/null|awk '/lockable:/{printf "%d",$2}'`
cpumem=`UNIX95= ps -eo "user pcpu sz vsz args" 2>/dev/null|egrep "$DIR"|egrep -v egrep|cut -c1-512`
;;
SCO_SV)
cpumem=`ps -elo "user pcpu size vsz args" 2>/dev/null|egrep "$DIR"|egrep -v egrep|cut -c1-512`
;;
UnixWare)
cpu=`ps -eo "pid pcpu" 2>/dev/null|awk '{printf $0;printf "\n"}'`
mem=`ps -elyf 2>/dev/null|egrep "$DIR"|egrep -v egrep|cut -c1-64|awk '{printf $0;printf "\n"}'`
cpupids=`echo "$cpu"|awk 'BEGIN{pids="#"}NR>1{pids=pids$1"#"}END{printf "%s",pids}'`
;;
*)
;;
esac

if [ -n "$THEOWNER" ]
then
case $OS in
Linux|UnixWare)
uid=`id "$THEOWNER" 2>/dev/null|awk '{if($0~/uid=/){split($1,one,"(");split(one[1],two,"=");if(length("'$THEOWNER'")>8)printf "%s",two[2];else printf "%s","'$THEOWNER'"}}'`
THEOWNER=$uid
;;
*)
;;
esac
fi

case $OS in
SunOS|Linux|FreeBSD|AIX|HP-UX|SCO_SV)
cpumem=`echo "$cpumem"|awk 'BEGIN{cpu=0;rss=0;i=0}
NR>0{if((length($0)>0)&&(("'$THEOWNER'"=="")||($1=="'$THEOWNER'"))){if(("'$OS'"=="FreeBSD")&&($5=="[idle]"));else{cpu=cpu+$2;rss=rss+$3;i++}}
}END{if("'$OS'"=="HP-UX")rss=rss*4;printf "%.2f %d %d",cpu,rss,i}'`
appcpu=`echo $cpumem|awk '{printf "%.2f",$1}'`
appmem=`echo $cpumem|awk '{printf "%d",$2}'`
;;
UnixWare)
pids=`echo "$mem"|awk 'BEGIN{ids="#"}
NR>1{if(("'$THEOWNER'"=="")||($2=="'$THEOWNER'")){if(index("'$cpupids'","#"$3"#")>0)ids=ids$3"#"}
}END{printf "%s",ids}'`
appcpu=`echo "$cpu"|awk 'BEGIN{cpu=0;}NR>1{if((length($0)>0)&&(index("'$pids'","#"$1"#")>0))cpu=cpu+$2;
}END{printf "%.2f",cpu}'`
appmem=`echo "$mem"|awk 'BEGIN{mem=0;}NR>1{if((length($0)>0)&&(index("'$pids'","#"$3"#")>0)){if($0~/<defunct>/);else mem=mem+$9}
}END{printf "%d",mem}'`
;;
*)
;;
esac

case $OS in
AIX)
memfree=`echo $memfree | awk 'BEGIN{free=0}{free=$1}{printf "%d",free*4}'`
;;
FreeBSD)
memfree=`echo $memfree | awk 'BEGIN{free=0}{free=int($1)}{printf "%d",free}'`
;;
esac

#echo "*memfree=$memfree*appmem=$appmem*memsize=$memsize*
#*cpuload=$cpuload*appcpu=$appcpu*"
syscpuload=`echo $cpuload|awk 'BEGIN{load=0}{load=$1;if($1>100)load=100.00;else if($1<0)load=0.00}END{printf "%.2f",load}'`
sysmemload=`echo $memfree $memsize|awk 'BEGIN{load=0}{load=100*($2-$1)/$2;if(load>100)load=100.00;else if(load<0)load=0.00}END{printf "%.2f",load}'`

case $OS in
AIX)
sysmemload=`vmstat -v 2>/dev/null | awk 'BEGIN{totalpages=0;freepages=0;nocomp=0.0}
{if($0~/numperm percentage/)nocomp=$1;if($0~/memory pages/)totalpages=$1;if($0~/free pages/)freepages=$1}
END{printf "%.02f",100*(totalpages-freepages)/totalpages-nocomp}'`
;;
Linux)
sysmemload=`cat /proc/meminfo | awk 'BEGIN{total=0;free=0}
{if($1=="MemTotal:")total=$2;if($1=="MemFree:")free+=$2;if($1=="Buffers:")free+=$2;if($1=="Cached:")free+$2}
END{printf "%.02f",100*(total-free)/total}'`
;;
esac

appcpuload=`echo $appcpu|awk 'BEGIN{load=0}{load=$1;if(load>100)load=100.00}END{printf "%.2f",load}'`
appmemload=`echo $appmem $memsize|awk 'BEGIN{load=0}{load=100*$1/$2;if(load>'$sysmemload')load='$sysmemload'}END{printf "%.2f",load}'`

printf "<wiserv-appperf><get_pmemrate>$sysmemload</get_pmemrate><get_appmemrate>$appmemload</get_appmemrate>\
<get_cpurate>$syscpuload</get_cpurate><get_appcpurate>$appcpuload</get_appcpurate></wiserv-appperf>"