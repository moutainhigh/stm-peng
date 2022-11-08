package com.mainsteam.stm.caplib.dict;

/**
 * 进程列表标题对应的列常量
 */
public final class ProcessColumnConsts {

	/**
	 * 进程ID
	 */
	public static final String PROCESS_PID = "processId";
	/**
	 * 进程所属用户
	 */
	public static final String PROCESS_USER = "userName";
	/**
	 * 进程占用物理内存
	 */
	public static final String PROCESS_PHYSICALMEM = "processPhysicalMem";
	/**
	 *进程占用虚拟内存
	 */
	public static final String PROCESS_VIRTUALMEM = "processVirtualMap";
	/**
	 * 进程CPU利用率
	 */
	public static final String PROCESS_CPU = "procMulCpuRate";
	/**
	 * 进程内存利用率
	 */
	public static final String PROCESS_MEM = "procMulMemRate";
	/**
	 * 进程状态
	 */
	public static final String PROCESS_STATE = "procMulStat";
	/**
	 * 进程启动时间
	 */
	public static final String PROCESS_STARTTIME = "procMulStime";
	/**
	 * 进程名称（命令）
	 */
	public static final String PROCESS_NAME = "processCommand";
	/**
	 * 进程命令行所在路径（windows下）
	 */
	public static final String PROCESS_CMD = "procCommandLine";
	/**
	 * 进程可用性
	 */
	public static final String PROCESS_AVAIL = "processAvail";
	
	//以下是进程属性所需的常量

	/**
	 * 进程id的属性
	 */
	public static final String PROPERTY_PROCESSID = "processId";

	/**
	 * 进程备注的属性
	 */
	public static final String PROPERTY_PROCESSREMARK = "processRemark";

	/**
	 * 进程实例Key的属性
	 */
	public static final String PROPERTY_INSTANCEIDKEYVALUES = "instanceIdKeyValues";
	/**
	 * 进程命令Key的属性
	 */
	public static final String PROPERTY_PROCESSCOMMAND = "processCommand";
}
