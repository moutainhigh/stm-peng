package com.mainsteam.stm.plugin.vmware;

import java.math.BigDecimal;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginsession.parameter.JBrokerParameter;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.PhysicalNic;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;


public class ESXiCollect {

	
	private static final Log logger = LogFactory.getLog(ESXiCollect.class);
	//虚拟机
	private static final String VIRTUAL_MACHINE = "VirtualMachine";
	//主机系统
	private static final String HOST_SYSTEM = "HostSystem";
	//可用性指标状态值，1表示可用，0表示不可用
	private static final String AVAIL = "1";
	//不可用
	private static final String UNAVAIL = "0";
	//虚拟机当前状态可用
	private static final String VIRTUAL_MACHINE_CONNECTED = "connected";
	//电源状态可用
	private static final String VIRTUAL_MACHINE_POWERED_ON = "poweredOn";
	//网卡状态可用
	private static final String VIRTUAL_MACHINE_IF_OK = "ok";
	//分隔符
	private static final String CN = "\n";
	private static final String CNT = "\t";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		JBrokerParameter parameter = new JBrokerParameter();
		parameter.setIp("172.16.7.122");
		parameter.setUsername("root");
		parameter.setPassword("passw0rd");
		
		URL url = new URL("https", parameter.getIp(), "/sdk");
		ServiceInstance serviceInstance = new ServiceInstance(url, parameter.getUsername(), parameter.getPassword(), true);
		
		parameter.setConnection(serviceInstance);
		
		String str = getGeneralProperties(parameter);
		
		System.out.println(str);
		
		System.out.println("==========================");
		
		String str2 = getVirtualMachineProperties(parameter);
		
		System.out.println(str2);
	}
	
	/**
	 * 获取虚拟机相关指标(信息类指标)
	 * @throws Exception
	 */
	public static String getVirtualMachineProperties(JBrokerParameter parameter) throws Exception {

		ServiceInstance si = (ServiceInstance)parameter.getConnection();
		Folder rootFolder = si.getRootFolder();
		InventoryNavigator inav = new InventoryNavigator(rootFolder);
		ManagedEntity[] esxs;
		
		StringBuffer sb = new StringBuffer();//返回结果
		/*
		 * 以XML的格式返回数据
		 * 返回格式顺序为：
		 * <VirtualMachine>虚拟机ID	虚拟机名称	MAC地址	配置路径	CPU个数	最大内存	操作系统	网卡状态	虚拟机当前状态	电源状态</VirtualMachine>
		 * 
		 */
		try {
			esxs = inav.searchManagedEntities(VIRTUAL_MACHINE);
			for (ManagedEntity esx : esxs) {
				
				VirtualMachine machine = (VirtualMachine) esx;
				//虚拟机名称
				String machineName = machine.getName();
				VirtualEthernetCard netInterface = getMac(machine.getConfig().getHardware());
				//虚拟机的Mac地址
				String macAddress = netInterface.getMacAddress();
				//虚拟机网卡连接状态
				String ifState = netInterface.getConnectable().getStatus();
				ifState = StringUtils.equals(ifState, VIRTUAL_MACHINE_IF_OK)?AVAIL:UNAVAIL;
				//虚拟机配置路径
				String machinePath = machine.getConfig().getFiles().getVmPathName();
				//虚拟机ID
				String machineId = machine.getMOR().get_value();
				//虚拟机CPU个数
				int numCPU = machine.getSummary().getConfig().getNumCpu();
				//虚拟机当前状态
				String connectState = machine.getRuntime().getConnectionState().toString();
				connectState = StringUtils.equals(connectState, VIRTUAL_MACHINE_CONNECTED)?AVAIL:UNAVAIL;
				//虚拟机电源状态
				String powerState = machine.getRuntime().getPowerState().toString();
				powerState = StringUtils.equals(powerState, VIRTUAL_MACHINE_POWERED_ON)?AVAIL:UNAVAIL;
				VirtualMachineConfigInfo config = machine.getConfig();
				//虚拟机最大内存,需转换成GB，保留小数点后两位数
				double maxMemory = config.getHardware().getMemoryMB();
				maxMemory = maxMemory/1024;
				BigDecimal bg = new BigDecimal(maxMemory);
				double dMemory = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				//虚拟机操作系统
				String OS = config.getGuestFullName();
				//拼接字符串
				sb.append("<VirtualMachine>").append(machineId).append(CNT).append(machineName).append(CNT).append(macAddress).
				append(CNT).append(machinePath).append(CNT).append(numCPU).append(CNT).append(dMemory).
				append(CNT).append(OS).append(CNT).append(ifState).append(CNT).append(connectState).
				append(CNT).append(powerState).append("</VirtualMachine>").append(CN);
				
			}
		} catch (InvalidProperty e) {
			logger.warn("获取虚拟机指标错误", e);
			throw new Exception(e);
		} catch (RuntimeFault e) {
			logger.warn("获取虚拟机指标错误", e);
			throw new Exception(e);
		} 
		
		return sb.toString();
		
	}
	
	/**
	 * 获取主机系统相关参数
	 */
	public static String getGeneralProperties(JBrokerParameter parameter) throws Exception {
		
		ServiceInstance si = (ServiceInstance)parameter.getConnection();
		//ESXi版本
		String version = si.getAboutInfo().getVersion();
		//设备描述（ESXi）
		String systemInfo = si.getAboutInfo().getFullName();
		//设备名称
		String Name = si.getAboutInfo().getName();
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<version>").append(version).append("</version>").append("<systemInfo>").
			append(systemInfo).append("</systemInfo>").append("<name>").append(Name).append("</name>");
		
		Folder rootFolder = si.getRootFolder();
		
		InventoryNavigator inav = new InventoryNavigator(rootFolder);
		
		ManagedEntity[] esxs;
		try {
			esxs = inav.searchManagedEntities(HOST_SYSTEM);
			for (ManagedEntity esx : esxs) {
				HostSystem hs = (HostSystem) esx;
				double cpuTotal = hs.getSummary().getHardware().getCpuMhz();//CPU主频
				int cpuCount = hs.getSummary().getHardware().getNumCpuCores();//CPU核数
				double cpuUsage = hs.getSummary().getQuickStats().getOverallCpuUsage();
				double cpuRate = cpuUsage/(cpuTotal*cpuCount)*100;
				//CPU利用率
				BigDecimal bg = new BigDecimal(cpuRate);
				cpuRate = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				sb.append("<cpuRate>").append(cpuRate).append("</cpuRate>");
				
				//内存利用率
				double memorySizeBytes = hs.getHardware().getMemorySize(); //内存总容量
				double overallMemoryUsage = hs.getSummary().getQuickStats().getOverallMemoryUsage(); //内存使用容量(MB)
				double memRate = overallMemoryUsage*1024*1024/memorySizeBytes*100; //内存利用率
				BigDecimal bg1 = new BigDecimal(memRate);
				memRate = bg1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				sb.append("<memRate>").append(memRate).append("</memRate>");
				
				//系统运行时间
				int sysUptime = hs.getSummary().getQuickStats().getUptime();
				sb.append("<sysUpTime>").append(sysUptime).append("</sysUpTime>");
				
				//Mac地址
				PhysicalNic[] nics = hs.getConfig().getNetwork().getPnic();
				for(PhysicalNic nic : nics) {
					sb.append("<macAddress>").append(nic.getMac() + " ").append("</macAddress>");
				}
				
			}
		} catch (InvalidProperty e) {
			logger.warn("获取虚拟机指标错误", e);
		} catch (RuntimeFault e) {
			logger.warn("获取虚拟机指标错误", e);
		}
		return sb.toString();
	}
	
	/**
	 * 获取虚拟机的Mac地址
	 * @param hardware
	 * @return
	 */
	private static VirtualEthernetCard getMac(VirtualHardware hardware) {
		VirtualDevice[] devices = hardware.getDevice();
		for (VirtualDevice device : devices) {
		if (device instanceof VirtualEthernetCard) {
				return ((VirtualEthernetCard) device);
			}
		}
		return null;
	}

}
