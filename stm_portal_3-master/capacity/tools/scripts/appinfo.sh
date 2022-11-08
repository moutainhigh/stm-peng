#!/bin/sh
PATH="/usr/bin:/usr/local/bin:/usr/sbin:/usr/local/sbin:${PATH}"
export PATH
LANG=POSIX
export LANG

flage=0;

if [ -n "$1" ];
then
    DIR=$1
fi

if [ -n "$2" ];
then
    THEOWNER=$2
fi

if [ ! -n "$DIR" ];
then
    DIR=$THEOWNER
    flage=1;
fi

if [ -n "$THEOWNER" ];
then
    haveuserflag=`cat /etc/passwd | awk 'BEGIN{out="";}/^'$THEOWNER':/{out=1;}END{print out;}'`
    if [ ! -n "$haveuserflag" ];
    then
        echo "";
        exit 1;
    fi
fi

OS=`uname`

outpronums=0;

case $OS in
HP-UX|AIX|SunOS|Linux|FreeBSD)
syscpuloadmemfree=`vmstat 1 2 2>/dev/null | awk 'BEGIN{id_flag=0;idl=0;flag_fre=0;memfree=0;}
NR>1{if($0~/id/){for(i=1;i<=NF;i++){if($i~/id/)id_flag=i;if($i~/fre/)flag_fre=i;}}
idl=$id_flag;memfree=$flag_fre;}
END{printf "%.2f,%s", 100.00-idl, memfree}'`
syscpuload=`echo $syscpuloadmemfree | awk -F, '{printf "%s",$1;}'`
sysmemfreesize=`echo $syscpuloadmemfree | awk -F, '{printf "%s",$2;}'`
;;
SCO_SV|UnixWare)
syscpuload=`sar -u 1 2|awk 'BEGIN{idle=0;idloff=0;}
{if($0~/idle/){for(i=1;i<=NF;i++){if($i~/idle/)idloff=i;}}
else if(NF>1){idle=$idloff;}
}END{syscpuload=100.00-idle;
if(syscpuload>100)syscpuload=100.00;else if(syscpuload<0)syscpuload=0.00;
printf "%0.f", syscpuload;}'`
sysmemfreesize=`sar -r 1 | awk 'BEGIN{memfree=0}
{while(getline >0){memfree=$2;}}
END{printf "%.0f",memfree*4}'`
;;
*)
;;
esac

##########
while true
do
##############################################
case $OS in
HP-UX)
if [ ! -n "$DIR" ];
then
    break
fi
appcpumemload=`sh -c "ps -eflx 2>/dev/null |egrep \"$DIR\"|egrep -v egrep | cut -c1-2048 ; echo \"===endflage====\";\
 UNIX95= ps -eo \"pid pcpu\" 2>/dev/null " |awk '
BEGIN{procpu=0;realmem=0;endflage=0;pronums=0;}
{
if($0=="===endflage===="){endflage=1;getline;}
if((("'$THEOWNER'"=="") || ($3=="'$THEOWNER'")) && (endflage==0))
{
thepids[$4]=1;
realmem=realmem+$10;
pronums++;
}
if(endflage==1)
{
    if(thepids[$1] == 1)
    {
       procpu=procpu+$2;
    }
}
}
END{printf "%.02f,%.02f,%.0f",realmem * 4, procpu, pronums}'`
;;
AIX)
if [ ! -n "$DIR" ];
then
    break
fi
appcpumemload_pids=`ps auwwx 2>/dev/null |egrep "$DIR"|egrep -v egrep | cut -c1-2048 |awk '
BEGIN{procpu=0;pmem=0;minmem=0;count=0;realmem=0;thepids="";}
(("'$THEOWNER'"=="") || ($1=="'$THEOWNER'")){if(thepids==""){thepids=$2;}else{thepids=thepids "#" $2;}
if(count==0)minmem=$4;if(minmem>$4)minmem=$4;pmem=pmem+$4;count++;procpu=procpu+$3;}
END{if(count>0)realmem=pmem;printf "%.02f,%.02f,#%s#,%.0f",realmem, procpu, thepids, count}'`
realmem=`echo "$appcpumemload_pids" | awk -F, '{printf "%s",$1;}'`
pronums=`echo "$appcpumemload_pids" | awk -F, '{printf "%s",$4;}'`
pids=`echo "$appcpumemload_pids" | awk -F, '{printf "%s",$3;}'`
procpu=`sh -c "echo \"$pids\";ps -eo \"pid cpu\" 2>/dev/null" | awk '
BEGIN{i=0;pids="";procpu=0;syscpuload='"$syscpuload"';allcpu=0;}
{
    if(i==0){i++;pids=$0;getline}
    allcpu=allcpu+$2;
    if(index(pids, "#" $1 "#") > 0)
    {
       procpu=procpu+$2;
    }
}END{if(allcpu > 0){procpu = syscpuload * procpu/allcpu;}printf "%.02f", procpu;}'`
appcpumemload=`echo $realmem","$procpu","$pronums | awk '{printf "%s", $0;}'`
;;
SunOS)
if [ ! -n "$DIR" ];
then
    break
fi
appcpumemload=`/usr/ucb/ps auwwx 2>/dev/null|egrep "$DIR"|egrep -v egrep | cut -c1-2048 |awk '
BEGIN{procpu=0;pmem=0;minmem=0;count=0;realmem=0;}
(("'$THEOWNER'"=="") || ($1=="'$THEOWNER'")){if(count==0)minmem=$4;if(minmem>$4)minmem=$4;pmem=pmem+$4;count++;procpu=procpu+$3;}
END{if(count>0)realmem=pmem;printf "%.02f,%.02f,%.0f",realmem, procpu, count}'`
;;
Linux)
pids=`ps auwwx 2>/dev/null |egrep "$DIR"|egrep -v egrep | cut -c1-2048 |awk '
BEGIN{thepids="";}
(("'$THEOWNER'"=="") || ($1=="'$THEOWNER'")){if(thepids==""){thepids=$2;}else{thepids=thepids "," $2;}}
END{printf "%s",thepids}'`
appcpumemload=`sh -c "echo \"$pids\";top -bS -d 1 -n 2  2>/dev/null" |awk '
BEGIN{i=0;pids="";procpu=0;pmem=0;minmem=0;
count=0;realmem=0;indexpos=0;cpupos=-1;mempos=-1;}{
if(i==0){i++;pids="," $0 ",";getline}
if($1=="PID")
{
    indexpos=indexpos+1;
    for(i=1;i<=NF;i++)
    {
        if($i=="%CPU")cpupos=i;else if($i=="%MEM")mempos=i;
    }
}
else if(indexpos == 1)
{
    if(index(pids, "," $1 ",") > 0)
    {
     if(count==0)minmem=$mempos;
     if(minmem>$mempos)minmem=$mempos;
     pmem=pmem+$mempos;
     count++;procpu=procpu+$cpupos;
    }
}
}END{if(count>0)realmem=pmem-minmem*(count-1);printf "%.02f,%.02f,%.0f",realmem, procpu, count}'`
;;
SCO_SV)
appcpumemload=`ps -eo "user pid vsz pcpu args" | egrep "$DIR"|egrep -v egrep | cut -c1-2048 | awk '
BEGIN{pidpos=0; virmempos=0; proccpupos=0; procpuload=0.00; promemload=0;count=0;}
(("'$THEOWNER'"=="") || ($1=="'$THEOWNER'")){
    proccpupos=4;
    virmempos=3;
    promemload = promemload + $virmempos;
    procpuload = procpuload + $proccpupos;
    count++;
}END{printf "%.02f,%.02f,%.0f",promemload, procpuload, count}'`;
;;
UnixWare)
appcpumemload=`sh -c "ps -eo 'user pid vsz pcpu args' | egrep \"$DIR\" | egrep -v egrep;\
echo \"====unixware====\";ps -ely 2>/dev/null;" \
| cut -c1-2048 | awk 'BEGIN{sign=0;pidpos=0; proccpupos=0; procpuload=0.00; promemload=0;phymempos=0;count=0;}
{
	if($0=="====unixware===="){sign=1;}
	if(sign==0)
	{
		pidpos=2;
		proccpupos=4;
		if(($2~/^[0-9][0-9]*$/) && ($3~/^[0-9][0-9]*$/) && (("'$THEOWNER'"=="") || ($1=="'$THEOWNER'")))
		{
			processid[$pidpos]=1;
			procpuload = procpuload + $proccpupos;
			count++;
		}
	}
	else
	{
		if($3~/PID/)
		{
		    for(i=1;i<=NF;i++)
		    {
		        if($i=="PID")pidpos=i;
		        else if($i=="RSS")phymempos=i;
		    }
		}
		else
		{
		   if((processid[$pidpos] == 1)&&($NF!="<defunct>")){promemload = promemload + $phymempos;}
		}
	}
}
END{printf "%.02f,%.02f,%.0f",promemload, procpuload, count}'`;
;;

*)
break
;;
esac

appmemload=`echo "$appcpumemload" | awk -F, '{printf "%s",$1;}'`
appcpuload=`echo "$appcpumemload" | awk -F, '{printf "%s",$2;}'`
outpronums=`echo "$appcpumemload" | awk -F, '{printf "%s",$3;}'`

##############################################
case $OS in
HP-UX)
sysmemsize=`grep Physical /var/adm/syslog/syslog.log 2>/dev/null |awk 'BEGIN{pmemsize=0;}
{for(i=1;i<=NF;i++){if($i~/Physical:/){pmemsize=$(i+1)}}}
END{if(pmemsize==0)exit 1;
printf pmemsize}'||dmesg|grep Physical 2>/dev/null|awk 'BEGIN{pmemsize=0;}
{for(i=1;i<=NF;i++){if($i~/Physical:/){pmemsize=$(i+1)}}}
END{if(pmemsize==0)exit 1;
printf pmemsize}'||echo 'phys_mem_pages/D'|adb /stand/vmunix /dev/kmem 2>/dev/null|awk 'BEGIN{pmemsize=0;}
{if(NF>1){pmemsize=$2*4;}}
END{if(pmemsize==0||length(pmemsize)==0)exit 1;
printf "%0.f", pmemsize;}'||echo 'phys_mem_pages/2D'|adb -o /stand/vmunix /dev/kmem 2>/dev/null|awk 'BEGIN{pmemsize=0;}
{if(NF>2){pmemsize=$3*4;}}
END{if(pmemsize==0||length(pmemsize)==0)exit 1;
printf "%0.f", pmemsize;}'`
if [ $? != 0 ];
then 
break;
fi;
sysmemload=`echo $sysmemsize $sysmemfreesize|awk '{printf "%.02f",100*(($1-$2*4)/$1);}'`
appmemload=`echo $sysmemsize $appmemload|awk '{printf "%.02f",100*($2/$1);}'`
;;
AIX)
sysmemsize=`/usr/sbin/prtconf 2>/dev/null| awk '
BEGIN{count=0;}/^Memory [Ss]ize/{count=$3*1024;}
END{printf "%.0f",count}'`;
sysmemload=`echo $sysmemsize $sysmemfreesize|awk '{printf "%.02f",100*(($1-$2*4)/$1);}'`
if [ $flage != 0 ]; then 
appmemload_tmp=`sh -c "sudo svmon -U $DIR 2>/dev/null || svmon -U $DIR 2>/dev/null" | grep $DIR | awk '
BEGIN{pmem=0;}{if($1=="'$DIR'"){pmem=4*$2;}}
END{if(!pmem){exit 1;}printf "%.02f",100*pmem/'$sysmemsize';}'`
if [ $? = 0 ]; then
appmemload=$appmemload_tmp
fi
fi;
;;
SunOS)
sysmemsize=`/usr/sbin/prtconf 2>/dev/null| awk '
BEGIN{count=0;}/^Memory [Ss]ize/{count=$3*1024;}
END{printf "%.02f", count}'`;
sysmemload=`echo $sysmemsize $sysmemfreesize|awk '{printf "%.02f",100*(($1-$2)/$1);}'`
;;
Linux)
memload=`cat /proc/meminfo 2>/dev/null |awk '
BEGIN{total=0;free=0;}
{if($1=="MemTotal:")total=$2;
if($1=="MemFree:")free=free+$2;
if($1=="Buffers:")free=free+$2;
if($1=="Cached:")free=free+$2;}
END{printf "%.2f %.2f", total, 100-100*free/total}'`
sysmemload=`echo $memload|awk '{printf "%.2f", $2}'`
;;
SCO_SV|UnixWare)
sysmemsize=`ls /tmp/memsize >/dev/null 2>&1
if [ $? != 0 ]; then
cp /etc/memsize /tmp/memsize >/dev/null 2>&1
chmod a+x /tmp/memsize >/dev/null 2>&1
fi
/tmp/memsize |awk 'BEGIN{pmemsize=0;}
{pmemsize=$1;}END{if(pmemsize==0)exit 1;printf "%.0f",pmemsize/1024}'`;
sysmemload=`echo $sysmemsize $sysmemfreesize|awk '{printf "%.02f",100*(($1-$2)/$1);}'`
appmemload=`echo $sysmemsize $appmemload|awk '{printf "%.02f",100*($2/$1);}'`
;;
*)
;;
esac

##############################################
case $OS in
HP-UX)
syspromemload=`ps -eflx 2>/dev/null | cut -c1-2048 |awk '
BEGIN{pmem=0;}{pmem=pmem+$10;}END{printf "%.02f", 100*(pmem * 4/'$sysmemsize');}'`
;;
SunOS|Linux|AIX)
syspromemload=`ps -eo "pmem"|awk 'BEGIN{val=0.00;}
{val += $1;}
END{printf "%.02f",val;}'`
;;
SCO_SV)
syspromemload=`ps -eo "user pid vsz pcpu args" | cut -c1-2048 | awk '
BEGIN{virmempos=0; promemload=0;}
{
    virmempos=3;
    if(NF > 2)
    {
        promemload = promemload + $virmempos;
    }
}END{printf "%.02f", 100*(promemload/'$sysmemsize');}'`;
;;
UnixWare)
syspromemload=`ps -ely 2>/dev/null | cut -c1-2048 | awk '
BEGIN{promemload=0;phymempos=0;}
{
    if($3~/PID/)
    {
        for(i=1;i<=NF;i++)
        {
            if($i=="RSS")phymempos=i;
        }
    }
    else
    {
        if($NF!="<defunct>"){promemload = promemload + $phymempos;}
    }
}END{printf "%.02f", 100*(promemload/'$sysmemsize');}'`;
;;
*)
;;
esac

break
done
##########

appmemoutput=`echo $appmemload $sysmemload $syspromemload|awk 'BEGIN{provalue=0;sysvalue=0;syspromemload=0;}
{provalue=$1;sysvalue=$2;syspromemload=$3;}
END{if(provalue>100.00)provalue=99.00;if(provalue<0)provalue=0.00;
if(sysvalue>100.00)sysvalue=99.00;if(sysvalue<0)sysvalue=0.00;
if(provalue>sysvalue){if(syspromemload>provalue)
{provalue=sysvalue * provalue/syspromemload;}else{provalue=sysvalue * 0.8;}}
if(provalue<0)provalue=0.00;
printf "%.02f %.02f", provalue, sysvalue;
}'`
appmem=`echo "$appmemoutput"|awk '{printf "%.2f",$1}'`;
sysmem=`echo "$appmemoutput"|awk '{printf "%.2f",$2}'`;

appcpuoutput=`echo $appcpuload $syscpuload|awk 'BEGIN{provalue=0;sysvalue=0;}
{provalue=$1;sysvalue=$2;}
END{if(provalue>100.00)provalue=99.00;if(provalue<0)provalue=0.00;
if(sysvalue>100.00)sysvalue=99.00;if(sysvalue<0)sysvalue=0.00;
if(provalue>sysvalue)provalue=sysvalue - 1.00;
if(provalue<0)provalue=0.00;
printf "%.02f %.02f", provalue, sysvalue;
}'`
appcpu=`echo "$appcpuoutput"|awk '{printf "%.2f",$1}'`;
syscpu=`echo "$appcpuoutput"|awk '{printf "%.2f",$2}'`;

printf "<wiserv-appperf><get_pmemrate>$sysmem</get_pmemrate><get_appmemrate>$appmem</get_appmemrate><get_cpurate>$syscpu</get_cpurate><get_appcpurate>$appcpu</get_appcpurate></wiserv-appperf>\r\n"
