package com.mainsteam.stm.plugin.vmware.collector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.vmware.constants.VMWareVMConstants;
import com.mainsteam.stm.plugin.vmware.util.VSphereUtil;
import com.mainsteam.stm.pluginsession.parameter.JBrokerParameter;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.GuestDiskInfo;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineToolsStatus;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.util.PropertyCollectorUtil;

/**
 * {class description} <br>
 * <p>
 * Create on : 2013-7-18<br>
 * </p>
 * <br>
 * 
 * @author demo<br>
 * @version 630-mainsteam-plugins-vmware v6.2.0 <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          --------------------------------------------<br>
 *          <br>
 */
public class VMWareVMCollector extends VMWareBaseCollector {
	public SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Log LOGGER = LogFactory.getLog(VMWareVMCollector.class);
	private static final String GET = "get";
	private static final String NAME = "name";
	private static final String EXCEP_AVAIL_VM = "EXCEP_AVAIL_VM:";
	private static final String SUMMARY_RUNTIME_POWER_STATE = "summary.runtime.powerState";

	private static final String[] PROPTITLE_DAYS = new String[] { VMWareVMConstants.C_DISKASSIGNEDSPACE, //
			VMWareVMConstants.C_CPUVIRTUALNUM, //
			VMWareVMConstants.C_MEMVMSIZE, //
			VMWareVMConstants.C_IPADDRESS, //
			VMWareVMConstants.C_EVCMODE, //
			VMWareVMConstants.C_VMOS, //
			VMWareVMConstants.C_VMVERSION, //
			VMWareVMConstants.C_VMTOOLSSTATUS, //
			VMWareVMConstants.C_NAME, //
			VMWareVMConstants.C_GUESTDISKUSEDSPACE, //
			VMWareVMConstants.C_VMSTATUS, //
	};
	private static final String[] PROPTITLE_MINS = new String[] {
			VMWareVMConstants.C_DISKUSEDSPACE, //
			VMWareVMConstants.C_DISKUNSHAREDSPACE, //
			VMWareVMConstants.C_MEMUSED, //
			VMWareVMConstants.C_GUESTDISKCAPACITY, //
	};
	private static final String[] PROP_FIELDS_MIN = { "summary.quickStats.hostMemoryUsage", // getMEMUsed
			"summary.storage.committed", // getDiskUsedSpace
			"summary.storage.unshared", // getDiskUnsharedSpace
			"guest.disk",// getGuestDiskCapacity
			"name", // getName
			"config"
	};
	private static final String[] PROP_FIELDS_DAY = { "summary.storage.committed", // getDiskAssignedSpace
			"summary.storage.uncommitted", // getDiskAssignedSpace
			"summary.config.numCpu", // getCPUVirtualNum
			"summary.config.memorySizeMB", // getMEMVMSize
			"summary.guest.ipAddress", // getVMIP
			"summary.runtime.minRequiredEVCModeKey", // getEVCMode
			"config.guestFullName", // getVMOS
			"config.version", // getVMVersion
			"summary.guest.toolsStatus", // getVMToolsStatus
			"name", // getName
			"config"
	};
	private static final String[] PERFTITLE = new String[] { VMWareVMConstants.C_CPURATE, //
			VMWareVMConstants.C_CPUUSED, //
			VMWareVMConstants.C_MEMACTIVESIZE, //
			VMWareVMConstants.C_MEMVIRTUALINCREASE, //
			VMWareVMConstants.C_MEMSWAPINRATE, //
			VMWareVMConstants.C_MEMSWAPOUTRATE, //
			VMWareVMConstants.C_MEMRATE, //
			VMWareVMConstants.C_NETWORKUSED, //
			VMWareVMConstants.C_DISKUSED, //
			VMWareVMConstants.C_MEMVMUSEDSIZE };

	private static final String[] PERFCOUNTER = new String[] { "cpu.usage.average", //
			"cpu.usagemhz.average", //
			"mem.active.average", //
			"mem.vmmemctl.average", //
			"mem.swapinRate.average", //
			"mem.swapoutRate.average", //
			"mem.usage.average", //
			"mem.overheadMax.average", //
			"disk.usage.average", //
			"net.usage.average" };

	public static void main(String[] args) throws Exception {
		JBrokerParameter parameter = new JBrokerParameter();
		/*
		 * parameter.setIp("172.16.7.74");
		 * parameter.setUsername("Administrator");
		 * parameter.setPassword("password!1");
		 */
		parameter.setIp("192.168.10.113");
		parameter.setUsername("root");
		parameter.setPassword("root3306");

		URL url = new URL("https", parameter.getIp(), "/sdk");
		ServiceInstance serviceInstance = new ServiceInstance(url, parameter.getUsername(), parameter.getPassword(),
				true);
		parameter.setConnection(serviceInstance);

//		parameter.setUuid("526995de-d478-86b9-256b-b070046df55c");//RHEL6.0-64-192.168.10.108
		parameter.setUuid("525ccc7b-feed-148f-02c6-7a4145418110");//RHEL6.0-64-192.168.10.112-oracletools

		// parameter.setUuid("52acb145-51e7-2a66-409f-e4ecee31c860"); //数据中心2
		// 新建虚拟机 uuid：52acb145-51e7-2a66-409f-e4ecee31c860 电源关闭的
		// parameter.setUuid("52c85f56-210d-1a07-e6a8-b9751201364e"); //数据中心1
		// 新建虚拟机 uuid: 52c85f56-210d-1a07-e6a8-b9751201364e 电源关闭的

		VMWareVMCollector vmColl = new VMWareVMCollector();
		System.out.println("prop:");
		String str2 = vmColl.getPropValue(parameter);
		System.out.println(str2);

		System.out.println("perf:");
		// System.out.println("cpuRate:");
		String str3 = vmColl.getPerfValue(parameter);
		System.out.println(str3);
		System.out.println("avai:");
		String str4 = vmColl.getAvailability(parameter);
		System.out.println(str4);
	}
	
	private String spellPropValue(Map<String, Object> propValue,VirtualMachine vm,String[] proptitles){
		Map<String, Object> propValues = new HashMap<>(propValue);
		for (Iterator<Entry<String, Object>> iterator = propValues.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			if(entry.getValue() == PropertyCollectorUtil.NULL){
				entry.setValue(null);
			}
		}
		String vmName = (String) propValue.get("name");
		StringBuilder sb = new StringBuilder();
		VirtualMachineConfigInfo config = (VirtualMachineConfigInfo)propValue.get("config");
		if(config == null){
			return sb.toString();
		}
		String vmUuid = config.getInstanceUuid();
		sb.append("<"+vmUuid+">");
		for (int i = 0; i < proptitles.length; i++) {
			final String methodName = "get" + proptitles[i];
			Method method;
			try {
				method = VMWareVMCollector.class.getMethod(methodName, Map.class);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("[Debug VM(" + vmName + ")]  " + methodName + " come on ");
				}
				String result = (String) method.invoke(this, propValues);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("[Debug ESXi(" + vmName + ")]  " + methodName + " result = " + result);
				}
				sb.append("<").append(proptitles[i]).append(">").append(result).append("</").append(proptitles[i])
						.append(">");
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		sb.append("</"+vmUuid+">");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private String getPropValue0(JBrokerParameter parameter, final String[] propFields, final String[] PROPTITLE) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		final String uuid = parameter.getUuid();
		if (uuid == null) {
			LOGGER.error("null uuid. " + parameter.getIp());
		} else {
			LOGGER.debug("current uuid: " + uuid + ", ip:" + parameter.getIp());
		}
		if (null != si) {
			StringBuffer sb = new StringBuffer();
			long temp = System.currentTimeMillis();
			final ManagedEntity[] vms = VSphereUtil.find(si, VSphereUtil.TYPE_VM);
			// LOGGER.error("==================虚机_getPerfValue_vm_uuid:"+uuid+"=============");
			if (null == vms||vms.length<=0) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("getPropValue0 no vms.");
				}
				return null;
			}
			Map<String, Object>[] vmsPropValues = null;
			try {
				vmsPropValues = PropertyCollectorUtil.retrieveProperties(vms, vms[0].getMOR().getType(), propFields);
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			if (vmsPropValues == null || vmsPropValues.length <= 0) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("getPropValue0 no vmsPropValues.");
				}
				return null;
			}
			for (int i = 0; i < vmsPropValues.length; i++) {
				if (vmsPropValues[i] != null) {
					sb.append(spellPropValue(vmsPropValues[i], (VirtualMachine) vms[i], PROPTITLE));
				}
			}
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("getPropValue0 Current ResultSet is [" + sb.toString() + "]. ip is " + parameter.getIp()+" lossTime="+(System.currentTimeMillis()-temp));
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * {Specific collect method for property metric }.
	 * 
	 * @param parameter
	 *            JBrokerParameter
	 * @return String
	 */
	public String getPropValue(JBrokerParameter parameter) {
		return getPropValue0(parameter, PROP_FIELDS_DAY,PROPTITLE_DAYS);
	}
	
	public String getPropValueMin(JBrokerParameter parameter) {
		return getPropValue0(parameter, PROP_FIELDS_MIN,PROPTITLE_MINS);
	}

	/**
	 * {Specific collect method for performance metric}.
	 * 
	 * @param conn
	 *            VMWareConnectInfo
	 * @param task
	 *            CollTask4ResPojo
	 * @return Matric
	 */
	/**
	 * 获取性能指标
	 * 
	 * @param parameter
	 * @return
	 */
	public String getPerfValue(JBrokerParameter parameter) {
		// LOGGER.info("jason:getPerfValueStartTime方法开始...."+df.format(new
		// Date()));
		// System.out.println("getPerfValueStartTime: " + df.format(new
		// Date()));

		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			StringBuilder sb = new StringBuilder();
			long temp = System.currentTimeMillis();
			final ManagedEntity[] vms = VSphereUtil.find(si, VSphereUtil.TYPE_VM);
			// LOGGER.error("==================虚机_getPerfValue_vm_uuid:"+uuid+"=============");
			if (null == vms || vms.length <= 0) {
				return null;
			}
			Map<String, ManagedObjectReference> s_morMap = new HashMap<String, ManagedObjectReference>();
			Map<String, Object>[] vmsPropValues = null;
			try {
				vmsPropValues = PropertyCollectorUtil.retrieveProperties(vms, vms[0].getMOR().getType(), new String[]{"config"});
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			if (vmsPropValues == null || vmsPropValues.length <= 0) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("getPropValue0 no vmsPropValues.");
				}
				return null;
			}
			for (int i = 0; i < vms.length; i++) {
				VirtualMachineConfigInfo config = (VirtualMachineConfigInfo)vmsPropValues[i].get("config");
				if(config == null){
					continue;
				}
				s_morMap.put(config.getInstanceUuid(), vms[i].getMOR());
			}
			final Map<String, HashMap<String, long[]>> resultMap = getPerfDataForMultiEntity(si, s_morMap, PERFCOUNTER,
					false, 20);

			if (resultMap != null && resultMap.size() > 0) {

				Set<Map.Entry<String, HashMap<String, long[]>>> resultSet = resultMap.entrySet();
				Iterator<Entry<String, HashMap<String, long[]>>> iterator = resultSet.iterator();

				while (iterator.hasNext()) {
					Entry<String, HashMap<String, long[]>> entry = iterator.next();
					sb.append('<').append(entry.getKey()).append('>');
					for (String perfMethod : PERFTITLE) {
						final String methodName = GET + perfMethod;
						Method method;
						try {
							method = this.getClass().getMethod(methodName, HashMap.class);
						} catch (NoSuchMethodException e) {
							LOGGER.error(e.getMessage(), e);
							continue;
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
							continue;
						}

						String result = null;
						try {
							HashMap<String, long[]> valMap = entry.getValue();
							result = (String) method.invoke(this, valMap);
						} catch (IllegalAccessException e) {
							LOGGER.error(e.getMessage(), e);
						} catch (IllegalArgumentException e) {
							LOGGER.error(e.getMessage(), e);
						} catch (InvocationTargetException e) {
							LOGGER.error(e.getMessage(), e);
						} catch (Throwable e) {
							LOGGER.error(e.getMessage(), e);
						}
						sb.append("<").append(perfMethod).append(">").append(result).append("</").append(perfMethod)
								.append(">");
					}
					sb.append('<').append('/').append(entry.getKey()).append('>');
				}

			}
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("getPerfValue Current ResultSet is [" + sb.toString() + "]. ip is " + parameter.getIp()+" lossTime="+(System.currentTimeMillis()-temp));
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 可用性指标
	 * 
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getAvailability(JBrokerParameter parameter) {
		StringBuilder b = new StringBuilder();
		try {
			ServiceInstance si = (ServiceInstance) parameter.getConnection();
			final ManagedEntity[] vms = VSphereUtil.find(si, VSphereUtil.TYPE_VM);
			// LOGGER.error("==================虚机_getPerfValue_vm_uuid:"+uuid+"=============");
			if (null == vms||vms.length<=0) {
				return null;
			}
			Map<String, Object>[] vmsPropValues = null;
			try {
				vmsPropValues = PropertyCollectorUtil.retrieveProperties(vms, vms[0].getMOR().getType(), new String[]{SUMMARY_RUNTIME_POWER_STATE,"config"});
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			if (vmsPropValues == null || vmsPropValues.length <= 0) {
				return null;
			}
			for (int i = 0; i < vmsPropValues.length; i++) {
				if (vmsPropValues[i] != null) {
					VirtualMachineConfigInfo config = (VirtualMachineConfigInfo)vmsPropValues[i].get("config");
					if(config == null){
						continue;
					}
					VirtualMachinePowerState vmps = (VirtualMachinePowerState) vmsPropValues[i]
							.get(SUMMARY_RUNTIME_POWER_STATE);
					String uuid = config.getInstanceUuid();
					b.append('<').append(uuid).append('>');
					if (null != vmps && vmps.equals(VirtualMachinePowerState.poweredOn)) {
						b.append('1');
					}else{
						b.append('0');
					}
					b.append('<').append('/').append(uuid).append('>');
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		return b.toString();
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getVMStatus(Map<String, Object> propValues) {
		String r = VirtualMachinePowerState.poweredOff.name();
		final String path = SUMMARY_RUNTIME_POWER_STATE;
		VirtualMachinePowerState vmps = (VirtualMachinePowerState) propValues.get(path);
		if (null == vmps) {
			return r;
		} else {
			return vmps.name();
		}
	}

	public String getZhengZe(String str) {
		try {
			// String str =
			// "<xx>ds:///vmfs/volumes/51541391-7babf79d-ca10-0007130443ec/,ds:///vmfs/volumes/4f28989c-6b8a8370-f342-001517de7011/</xx><yy>4f28989c-6b8a8370-f342-001517de7034</yy>";
			String outReg = "<DatastoreList>.*</DatastoreList>";
			String innerReg = "[0-9a-z]{8}\\-[0-9a-z]{8}\\-[0-9a-z]{4}\\-[0-9a-z]{12}";
			StringBuffer needUUIDStr = new StringBuffer();
			Pattern outPat = Pattern.compile(outReg);
			Matcher outMat = outPat.matcher(str);

			while (outMat.find()) {
				String innerStr = outMat.group();
				Pattern innerPat = Pattern.compile(innerReg);
				Matcher innerMat = innerPat.matcher(innerStr);
				while (innerMat.find()) {
					needUUIDStr = needUUIDStr.append(innerMat.group());
					System.out.println(needUUIDStr);
					needUUIDStr.append(",");
				}
			}
			return needUUIDStr.toString().substring(0, needUUIDStr.toString().length() - 1);
		} catch (Exception e) {
			LOGGER.error("jason say:Regular expression conversion error!!! " + e);
		}
		return "";
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getDatastoreList(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		ManagedEntity[] t_hss = VSphereUtil.find(si, VSphereUtil.TYPE_VM);
		if (t_hss != null && t_hss.length > 0) {
			Map<String, Object>[] storeProps = null;
			try {
				storeProps = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
						new String[] { "datastore","config" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getVMList", e);
				}
			}
			if (storeProps != null) {
				ManagedEntity[] t_vss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
				Map<String, String> uuidRefMap = null;
				Map<String, DatastoreSummary>[] uuidMaps = null;
				try {
					uuidMaps = PropertyCollectorUtil.retrieveProperties(t_vss, t_vss[0].getMOR().getType(),
							new String[] {  "summary" });
					uuidRefMap = new HashMap<>(uuidMaps.length);
					for (int i = 0; i < uuidMaps.length; i++) {
						DatastoreSummary uuid = uuidMaps[i].get( "summary");
						if (uuid != null) {
							uuidRefMap.put(t_vss[i].getMOR().getVal(), VSphereUtil.createDatastoreUUID(uuid.getUrl()));
						}
					}
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getVMList", e);
					}
				}
				for (int i = 0; i < storeProps.length; i++) {
					if(storeProps[i].get("datastore") instanceof ManagedObjectReference[]){
						ManagedObjectReference[] tdataStores = (ManagedObjectReference[]) storeProps[i].get("datastore");
						String uuid = ((VirtualMachineConfigInfo)storeProps[i].get("config")).getInstanceUuid();
						sb.append('<').append(uuid).append('>');
						if (tdataStores != null && tdataStores.length > 0) {
							sb.append(uuidRefMap.get(tdataStores[0].getVal()));
							for (int j = 1; j < tdataStores.length; j++) {
								sb.append(',').append(uuidRefMap.get(tdataStores[j].getVal()));
							}
						}
						sb.append('<').append('/').append(uuid).append('>');
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getCPURate(final HashMap<String, long[]> map) {
		// LOGGER.error("进入获取虚机CPU平均使用率方法..jason:vm_getCPURateStartTime方法开始...."+df.format(new
		// Date())+"\n 参数map值value:"+map+" ");
		// expressed as a hundredth of a percent (1 = 0.01%), A value between 0
		// and 10,000.
		String r2 = null;
		try {
			final String counter = "cpu.usage.average";
			// LOGGER.error("jason:vm_getCPURate_counter值value:"+counter+"\n");

			final long r = map.get(counter)[0];
			// r2= convert2DecimalString((Long.valueOf(r).doubleValue() / C_100)
			// < 0 ? 0: (Long.valueOf(r).doubleValue() / C_100));
			// #10687 加了try{}catch(){}，调整了代码默认值
			// LOGGER.error("jason:vm_getCPURate_r值value:"+r+"\n");

			r2 = convert2DecimalString((Long.valueOf(r).doubleValue() / C_100));
			// LOGGER.error("jason:vm_getCPURateEndTime方法结束...."+df.format(new
			// Date())+"值value: "+r2+"\n");
		} catch (Exception e) {
			LOGGER.error("jason:vm_getCpuRate方法异常:" + e.getMessage(), e);
		}
		return r2;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getCPUUsed(final HashMap<String, long[]> map) {
		// megaHertz
		final String counter = "cpu.usagemhz.average";
		final long r = map.get(counter)[0];
		return String.valueOf(r);
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getMEMActiveSize(final HashMap<String, long[]> map) {
		// kiloBytes
		final String counter = "mem.active.average";
		final long r = map.get(counter)[0];
		final String r2 = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024);
		return r2;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getMEMVirtualIncrease(final HashMap<String, long[]> map) {
		// kiloBytes
		final String counter = "mem.vmmemctl.average";
		final long r = map.get(counter)[0];
		final String r2 = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024);
		return r2;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getMEMSwapInRate(final HashMap<String, long[]> map) {
		// kiloBytesPerSecond
		final String counter = "mem.swapinRate.average";
		final long r = map.get(counter)[0];
		final String r2 = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024);
		return r2;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getMEMSwapOutRate(final HashMap<String, long[]> map) {
		// kiloBytesPerSecond
		final String counter = "mem.swapoutRate.average";
		final long r = map.get(counter)[0];
		final String r2 = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024);
		return r2;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getMEMRate(final HashMap<String, long[]> map) {
		// LOGGER.error("进入获取虚机内存利用率方法...jason:vm_getMEMRateStartTime方法开始...."+df.format(new
		// Date())+"\n map 参数map的值value:"+map+" ");
		// expressed as a hundredth of a percent (1 = 0.01%), A value between 0
		// and 10,000.
		String r2 = null;
		try {
			final String counter = "mem.usage.average";
			// LOGGER.error("jason:vm_getMEMRateStart_counter
			// 值value:...."+counter+"\n");
			final long r = map.get(counter)[0];
			// LOGGER.error("jason:vm_getMEMRateStart_r 值value:...."+r+"\n");

			r2 = convert2DecimalString(
					(Long.valueOf(r).doubleValue() / C_100) < 0 ? 0 : (Long.valueOf(r).doubleValue() / C_100));
			// LOGGER.error("jason:vm_getMEMRateStartTime方法结束...."+df.format(new
			// Date())+"\n");

		} catch (Exception e) {
			LOGGER.error("jason:vm_getMEMRate方法异常:" + e.getMessage(), e);
		}
		return r2;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getMEMUsed(Map<String, Object> propValues) {
		// "mem.consumed.average" Amount of guest physical memory consumed by
		// the virtual machine for guest memory.
		// MB
		final String path = "summary.quickStats.hostMemoryUsage";
		final Integer r = (Integer) propValues.get(path);
		// final Integer r = getPropValueFromHashtable(hashtable, path,
		// Integer.class);
		if(r==null){
			return null;
		}
		return r.toString();
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getDiskUsed(final HashMap<String, long[]> map) {
		// kiloBytesPerSecond
		final String counter = "disk.usage.average";
		if (null != map) {
			final long r = map.get(counter)[0];
			return String.valueOf(r);
		} else {
			return "0";
		}
	}

	/**
	 * {method description}. 解决7842BUG;yuanlb;
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getNetworkUsed(final HashMap<String, long[]> map) {
		// kiloBytesPerSecond
		final String counter = "net.usage.average";
		// final String counter="net.usage.maximum";
		final long r = map.get(counter)[0];
		final String r2 = convert2DecimalString(Long.valueOf(r).doubleValue());
		return r2;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getVMAlarmState(final ServiceInstance si, final VirtualMachine vm) {
		final String path = "summary.overallStatus";
		final ManagedEntityStatus mes = (ManagedEntityStatus) vm.getPropertyByPath(path);
		// final ManagedEntityStatus mes =
		// getPropValueFromHashtable(hashtable, path,
		// ManagedEntityStatus.class);
		// green, yellow, gray, red
		return mes.toString();
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getDiskAssignedSpace(Map<String, Object> propValues) {
		// bytes
		final String path = "summary.storage.committed";
		final String path2 = "summary.storage.uncommitted";
		final Long comm = (Long) propValues.get(path);
		final Long unComm = (Long) propValues.get(path2);
		/*
		 * final Long comm = getPropValueFromHashtable(hashtable, path,
		 * Long.class); final Long unComm = getPropValueFromHashtable(hashtable,
		 * path2, Long.class);
		 */
		final long r2 = comm.longValue() + unComm.longValue();
		final String value = convert2DecimalString(Long.valueOf(r2).doubleValue() / C_1024 / C_1024 / C_1024);
		return value;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getDiskUsedSpace(Map<String, Object> propValues) {
		// bytes
		final String path = "summary.storage.committed";
		final Long r = (Long) propValues.get(path);
		// final Long r = getPropValueFromHashtable(hashtable, path,
		// Long.class);
		final String value = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024 / C_1024 / C_1024);
		return value;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getDiskUnsharedSpace(Map<String, Object> propValues) {
		// bytes
		final String path = "summary.storage.unshared";
		final Long r = (Long) propValues.get(path);
		// final Long r = getPropValueFromHashtable(hashtable, path,
		// Long.class);
		if(r==null){
			return null;
		}
		final String value = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024 / C_1024 / C_1024);
		return value;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getCPUVirtualNum(Map<String, Object> propValues) {
		final String path = "summary.config.numCpu";
		final Integer r = (Integer) propValues.get(path);
		// final Integer r = getPropValueFromHashtable(hashtable, path,
		// Integer.class);
		return r.toString();
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getMEMVMSize(Map<String, Object> propValues) {
		// megabytes
		final String path = "summary.config.memorySizeMB";
		final Integer r = (Integer) propValues.get(path);
		// final Integer r = getPropValueFromHashtable(hashtable, path,
		// Integer.class);
		return r.toString();
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 */
	public String getMEMVMUsedSize(final HashMap<String, long[]> map) {
		// kiloBytes
		final String counter = "mem.overheadMax.average";
		final long r = map.get(counter)[0];
		final String r2 = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024);
		return r2;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getIPAddress(Map<String, Object> propValues) {
		return (String) propValues.get("summary.guest.ipAddress");
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getEVCMode(Map<String, Object> propValues) {
		final String path = "summary.runtime.minRequiredEVCModeKey";
		final String r = (String) propValues.get(path);
		// final String r = getPropValueFromHashtable(hashtable, path,
		// String.class);
		if (StringUtils.isEmpty(r)) {
			return "disabled";
		}
		return r;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getHostName(final ServiceInstance si, final VirtualMachine vm) {
		final String path = "summary.runtime.host";
		final ManagedObjectReference mor = (ManagedObjectReference) vm.getPropertyByPath(path);
		// final ManagedObjectReference mor =
		// getPropValueFromHashtable(hashtable, path,
		// ManagedObjectReference.class);
		final HostSystem hs = new HostSystem(si.getServerConnection(), mor);
		final String r = (String) hs.getPropertyByPath(NAME);
		return r;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getVMOS(Map<String, Object> propValues) {
		final String path = "config.guestFullName";
		final String r = (String) propValues.get(path);
		// final String r = getPropValueFromHashtable(hashtable, path,
		// String.class);
		return r;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getVMVersion(Map<String, Object> propValues) {
		final String path = "config.version";
		final String r = (String) propValues.get(path);
		// final String r = getPropValueFromHashtable(hashtable, path,
		// String.class);
		return r;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getVMToolsStatus(Map<String, Object> propValues) {
		final String path = "summary.guest.toolsStatus";
		final VirtualMachineToolsStatus ts = (VirtualMachineToolsStatus) propValues.get(path);
		/*
		 * final VirtualMachineToolsStatus ts =
		 * getPropValueFromHashtable(hashtable, path,
		 * VirtualMachineToolsStatus.class);
		 */
		return ts.name();
		/*
		 * String r = ""; switch (ts) { case toolsNotInstalled: r =
		 * "NotRunning(Not Installed)"; break; case toolsNotRunning: r =
		 * "NotRunning(Installed)"; break; case toolsOk: r =
		 * "Running(Latest Version)"; break; case toolsOld: r =
		 * "Running(Old Version)"; break; default: } return r;
		 */
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	// instance metric
	public String getName(Map<String, Object> propValues) {
		return (String) propValues.get("name");
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getGuestDiskCapacity(Map<String, Object> propValues) {
		final String path = "guest.disk";
		GuestDiskInfo[] gdis = (GuestDiskInfo[]) propValues.get(path);
		long cap = 0L;
		String r = "";
		/*
		 * GuestDiskInfo[] gdis = null; try { gdis =
		 * GuestDiskInfo[].class.cast(hashtable.get(path)); } catch (final
		 * Exception e) { LOGGER.error("We can not get virtual disk."); }
		 */
		if (null != gdis && gdis.length > 0) {
			for (final GuestDiskInfo gdi : gdis) {
				cap += gdi.getCapacity().longValue();
			}
			r = convert2DecimalString(Long.valueOf(cap).doubleValue() / C_1024 / C_1024 / C_1024);
		}
		return r;
	}

	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getGuestDiskUsedSpace(Map<String, Object> propValues) {
		// bytes
		final String path = "guest.disk";
		GuestDiskInfo[] gdis = (GuestDiskInfo[]) propValues.get(path);
		long cap = 0L;
		long free = 0L;
		String r = "";
		// GuestDiskInfo[] gdis = null;
		/*
		 * try { gdis = GuestDiskInfo[].class.cast(hashtable.get(path)); } catch
		 * (final Exception e) { LOGGER.error("We can not get virtual disk."); }
		 */
		if (null != gdis && gdis.length > 0) {
			for (final GuestDiskInfo gdi : gdis) {
				cap += gdi.getCapacity().longValue();
				free += gdi.getFreeSpace().longValue();
			}
			r = convert2DecimalString(Long.valueOf(cap - free).doubleValue() / C_1024 / C_1024 / C_1024);
		}
		return r;
	}
}
