package com.mainsteam.stm.caplib.dict;

/**
 * 
 * <li>文件名称: MetricIdConsts.java</li><br/>
 * <li>文件描述: 指标ID常量类</li><br/>
 * <li>版权所有: 版权所有(C)2014</li><br/>
 * <li>公司: 美新翔盛</li> <br/>
 * <li>内容摘要:指标ID常量类</li> <br/>
 * <li>其他说明:</li><br/>
 * <li>完成日期：2014.8.12</li><br/>
 * <li>修改记录1:新建</li>
 * 
 * @version 3
 * @author sunsht
 */
public final class MetricIdConsts {

	/**
	 * 资源名称
	 */
	public static final String METRIC_NAME = "Name";

	/**
	 * CPU利用率
	 */
	public static final String METRIC_CPU_RATE = "cpuRate";
	/**
	 * 内存利用率
	 */
	public static final String METRIC_MEME_RATE = "memRate";

	/**
	 * CPU利用率
	 */
	public static final String METRIC_APP_CPU_RATE = "appCpuRate";
	/**
	 * 内存利用率
	 */
	public static final String METRIC_APP_MEME_RATE = "appMemRate";

	/**
	 * mac地址
	 */
	public static final String METRIC_MACADDRESS = "macAddress";

	/**
	 * 可用性
	 */
	public static final String METRIC_AVAILABLE = "availability";
	/**
	 * 响应时间
	 */
	public static final String METRIC_RESPONSE_TIME = "icmpDelayTime";

	/**
	 * 进程列表
	 */
	public static final String FULL_PROCESS = "fullProcess";
	
	/**
	 * 文件列表
	 */
	public static final String FULL_FILE = "fullFile";

	/**
	 * NetStat工具
	 * 
	 */
	public static final String NETSTAT = "netstat";

	/**
	 * arp表
	 */
	public static final String ARP_TABLE = "arpTable";

	/**
	 * 路由表
	 */
	public static final String ROUTE_TABLE = "ipRouteTable";

	/**
	 * baseport表
	 */
	public static final String BASEPORT_TABLE = "basePortTable";

	/**
	 * ip地址表
	 */
	public static final String IPADDR_TABLE = "ipAddrTable";
	
	/**
	 * vlan表
	 */
	public static final String VLAN_TABLE = "vlanTable";

	/**
	 * ip，所有ip地址，用分号隔开
	 */
	public static final String METRIC_IP = "ip";

	/**
	 * 连续运行时间
	 */
	public static final String SYS_UPTIME = "sysUpTime";
	
	public static final String SYS_OBJECT_ID = "sysObjectID";

	// 主机分区子资源指标
	/**
	 * 挂载点或者分区名称
	 */
	public static final String PARTITION_FILENAME = "fileSysName";
	/**
	 * 分区容量
	 */
	public static final String PARTITION_FILETOTALSIZE = "fileSysTotalSize";
	/**
	 * 分区已用容量
	 */
	public static final String PARTITION_FILEUSEDSIZE = "fileSysUsedSize";
	/**
	 * 分区可用容量
	 */
	public static final String PARTITION_FILEFREESIZE = "fileSysFreeSize";
	/**
	 * 分区利用率
	 */
	public static final String PARTITION_FILERATE = "fileSysRate";
	/**
	 * 带宽利用率
	 */
	public static final String IF_IFBANDWIDTHUTIL = "ifBandWidthUtil";

	/**
	 * 吞吐量(设备)
	 */
	public static final String METRIC_THROUGHPUT = "throughput";
	/**
	 * 总流量(接口)
	 */
	public static final String METRIC_IFOCTETSSPEED = "ifOctetsSpeed";

	/**
	 * 版本
	 */
	public static final String VERSION = "Version";

	/**
	 * 服务端口(中间件和应用)
	 */
	public static final String SERVICE_PORT = "servicePort";
}
