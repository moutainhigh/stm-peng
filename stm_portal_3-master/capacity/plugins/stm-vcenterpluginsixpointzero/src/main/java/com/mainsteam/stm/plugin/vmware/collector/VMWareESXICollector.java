package com.mainsteam.stm.plugin.vmware.collector;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
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

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.plugin.vmware.constants.CommonConstants;
import com.mainsteam.stm.plugin.vmware.constants.VMWareESXIConstants;
import com.mainsteam.stm.plugin.vmware.util.VSphereUtil;
import com.mainsteam.stm.plugin.vmware.vo.ResourceTree;
import com.mainsteam.stm.pluginsession.parameter.JBrokerParameter;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostHostBusAdapter;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.HostStorageDeviceInfo;
import com.vmware.vim25.HostSystemConnectionState;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PhysicalNic;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.ScsiLun;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.util.PropertyCollectorUtil;

/**
 * Created at 2016.09.27
 * 
 * @author yuanlb
 *
 */
public class VMWareESXICollector extends VMWareBaseCollector {
	public SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("unused")
	private static final String SUMMARY_GUEST_IP_ADDRESS = "summary.guest.ipAddress";

	private static final Log LOGGER = LogFactory.getLog(VMWareESXICollector.class);

	private static final String GET = "get";

	private static final String VM = "vm";

	private static final String SUMMARY_HARDWARE_VENDOR = "summary.hardware.vendor";

	private static final String SUMMARY_CONFIG_INSTANCE_UUID = "summary.config.instanceUuid";

	@SuppressWarnings("unused")
	private static final String NAME = "name";

	private static final String SUMMARY_CONFIG_PRODUCFULL_NAME = "summary.config.product.fullName";

	private static final String SUMMARY_CONFIG_VMOTION_ENABLED = "summary.config.vmotionEnabled";

	private static final String SUMMARY_CONFIG_FAULTOLERANCE_ENABLED = "summary.config.faultToleranceEnabled";

	private static final String SUMMARY_HARDWARE_NUM_CPU_PKGS = "summary.hardware.numCpuPkgs";

	private static final String SUMMARY_HARDWARE_NUM_CPU_CORES = "summary.hardware.numCpuCores";

	private static final java.lang.String SUMMARY_RUNTIME_CONNECTION_STATE = "summary.runtime.connectionState";

	private static final String EXP_AVAIL_EXS = "EXP_AVAIL_EXS:";

	// 主机系统

	/**
	 * 性能指标对应的PerfCounterInfo
	 */
	private static final String[] PERFCOUNTER = new String[] { "cpu.usage.average", "cpu.usagemhz.average",
			"mem.vmmemctl.average", "mem.granted.average", "mem.swapinRate.average", "mem.swapoutRate.average",
			"mem.usage.average", "mem.consumed.average", "disk.maxTotalLatency.latest", "disk.usage.average",
			"net.usage.average" };

	/**
	 * 主资源信息指标及其对应的get方法
	 */
	private static final String[] PROPTITLE = new String[] {
			// VMWareESXIConstants.C_VMLIST,
			// VMWareESXIConstants.C_DATASTORELIST,
			VMWareESXIConstants.C_NUMBEROFVM, VMWareESXIConstants.C_VMOTION, VMWareESXIConstants.C_EVC,
			VMWareESXIConstants.C_HA, VMWareESXIConstants.C_FT, VMWareESXIConstants.C_MANUFATURE,
			VMWareESXIConstants.C_MODEL, VMWareESXIConstants.C_COREOFCPU, VMWareESXIConstants.C_SENSORSTATE,
			VMWareESXIConstants.C_CPUTYPE, VMWareESXIConstants.C_SLOTOFPROCESSOR, VMWareESXIConstants.C_CORENUMOFCPU,
			VMWareESXIConstants.C_NUMOFLOGICALPROCESSOR, VMWareESXIConstants.C_HT, VMWareESXIConstants.C_NUMBEROFNIC,
			VMWareESXIConstants.C_MEMSIZE, VMWareESXIConstants.C_SYSUPTIME,
			// VMWareESXIConstants.C_DATASTORESIZE,
			VMWareESXIConstants.C_CPUTOTAL, };
	private static final String[] PROP_TITLE_FILEDS = { "summary.hardware.vendor", // getManufature
			"summary", // getSysUpTime
			"summary.config.product.fullName", // getModel
			"summary.config.vmotionEnabled", // getVMotion
			"summary.config.faultToleranceEnabled", // getFT
			"summary.hardware.numCpuCores", // getCoreOfCPU
			"summary.hardware.numCpuPkgs", // getCoreNumOfCPU
			"summary.hardware.numCpuCores", // getCoreNumOfCPU
			"summary.hardware.numCpuThreads", // getSlotOfProcessor
			"summary.hardware.numCpuThreads", // getNumOfLogicalProcessor
			"config.hyperThread.active", // getHT
			"summary.currentEVCModeKey", // getEVC
			"vm", // getNumberOfVM
			"summary.hardware.cpuModel", // getCPUType
			"summary.overallStatus", // getSensorState
			"hardware.memorySize", // getMEMSize
			"summary.hardware.numNics", // getNumberOfNIC
			"summary.runtime.dasHostState.state", // getHA
			"hardware", // getCPUTotal
			"name" };
	/**
	 * 主资源性能指标及其对应的get方法 pef
	 */
	private static final String[] PERFTITLE = new String[] { VMWareESXIConstants.C_CPURATE, //
			VMWareESXIConstants.C_CPUUSED, //
			VMWareESXIConstants.C_MEMVIRTUALINCREASE, //
			VMWareESXIConstants.C_MEMSWAPINRATE, //
			VMWareESXIConstants.C_MEMSWAPOUTRATE, //
			VMWareESXIConstants.C_MEMRATE, //
			VMWareESXIConstants.C_MEMUSED, //
			VMWareESXIConstants.C_MAXDISKLATENCY, //
			VMWareESXIConstants.C_DISKUSED, //
			VMWareESXIConstants.C_NETWORKUSED //
	};

	public String getNone(JBrokerParameter parameter) {
		return "";
	}

	public String getUUID(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		LOGGER.error("getUUID----->" + parameter.getUuid());
		if (null != si) {
			// 需要判断是不是esxi
			if (VSphereUtil.isESX(si)) {
				ManagedEntity[] esxs = VSphereUtil.find(si, C_HOST);
				for (ManagedEntity esx : esxs) {
					HostSystem hs = (HostSystem) esx;
					String hostuuid = VSphereUtil.getHostUuid(hs);
					if (null != hostuuid)
						// LOGGER.error("getUUID:" + hostuuid);
						return hostuuid;
				}
			} else {
				LOGGER.error("getUUID:NOT RUN TO HERE");
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getName(JBrokerParameter parameter) {
		StringBuilder sb = new StringBuilder();
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			ManagedEntity[] esxs = VSphereUtil.find(si, C_HOST);
			Map<String, String>[] props = null;
			try {
				props = PropertyCollectorUtil.retrieveProperties(esxs, esxs[0].getMOR().getType(),
						new String[] { "name" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getName", e);
				}
			}
			if (props != null) {
				for (int i = 0; i < props.length; i++) {
					String uuid = VSphereUtil.getHostUuid((HostSystem) esxs[i]);
					sb.append('<').append(uuid).append('>');
					sb.append(props[i].get("name"));
					sb.append('<').append('/').append(uuid).append('>');
				}
			}
		}
		return sb.toString();
	}

	public String getResourceTree(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			try {
				ManagedEntity[] esxs = VSphereUtil.find(si, C_HOST);

				for (ManagedEntity esx : esxs) {
					HostSystem hs = (HostSystem) esx;
					String hsName = hs.getName();
					String hsUUID = VSphereUtil.getHostUuid(hs);
					if (StringUtils.isEmpty(hsUUID)) {
						// LOGGER.error("NULL UUID of HOST");
						continue;
					}
					ResourceTree rootResource = new ResourceTree(hsName, C_HOST, hsUUID, parameter.getIp(), hsName);

					Datastore[] datastores = hs.getDatastores();
					if (datastores != null && datastores.length > 0) {
						for (Datastore tDatastore : datastores) {
							String dtName = tDatastore.getName();
							final String uuidDS = VSphereUtil.getDatastoreUUID(tDatastore);
							if (StringUtils.isNotEmpty(uuidDS)) {
								ResourceTree dtResource = new ResourceTree(dtName, C_DATASTORE, uuidDS, "", hsName);
								rootResource.addResource(dtResource);
							}
						}
					}

					for (final VirtualMachine vm : hs.getVms()) {
						String vmName = vm.getName();
						final String vmIp = VSphereUtil.getVMIP(vm);
						final String vmUUID = (String) ((VirtualMachine) vm)
								.getPropertyByPath(SUMMARY_CONFIG_INSTANCE_UUID);
						if (StringUtils.isNotEmpty(vmUUID)) {
							ResourceTree vmResource = new ResourceTree(vmName, C_VM, vmUUID, vmIp, hsName);
							rootResource.addResource(vmResource);
						}
					}
					String rr = JSON.toJSONString(rootResource);
					return rr;
				}
			} catch (InvalidProperty e1) {
				e1.printStackTrace();
			} catch (RuntimeFault e1) {
				e1.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getPropValue(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			long temp = System.currentTimeMillis();
			StringBuffer sb = new StringBuffer();
			final ManagedEntity[] hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);
			Map<String, Object>[] dsPropValues = null;
			try {
				dsPropValues = PropertyCollectorUtil.retrieveProperties(hss, hss[0].getMOR().getType(),
						PROP_TITLE_FILEDS);
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			if (dsPropValues == null || dsPropValues.length <= 0) {
				return null;
			}
			for (int k = 0; k < dsPropValues.length; k++) {
				final HostSystem hs = (HostSystem) hss[k];
				String hsName = "";
				if (dsPropValues[k] == null || dsPropValues[k].size() <= 0) {
					return null;
				}
				hsName = (String) dsPropValues[k].get("name");
				Map<String, Object> propValues = new HashMap<>(dsPropValues[k]);
				for (Iterator<Entry<String, Object>> iterator = propValues.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, Object> entry = iterator.next();
					if (entry.getValue() == PropertyCollectorUtil.NULL) {
						entry.setValue(null);
					}
				}
				if (!(dsPropValues[k].get("summary") instanceof HostListSummary)) {
					continue;
				}
				String dsUrl = ((HostListSummary) dsPropValues[k].get("summary")).getHardware().getUuid();
				String serverIP = ((HostListSummary) dsPropValues[k].get("summary")).getManagementServerIp();
				String uuid = VSphereUtil.createUuid(dsUrl,hsName,serverIP);
//				String uuid = VSphereUtil.getHostUuid(hs);
				sb.append('<').append(uuid).append('>');
				for (int i = 0; i < PROPTITLE.length; i++) {
					final String methodName = GET + PROPTITLE[i];
					Method method;
					try {
						method = VMWareESXICollector.class.getMethod(methodName, ServiceInstance.class,
								HostSystem.class, Map.class);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("[Debug ESXi(" + hsName + ")]  " + methodName + " come on ");
						}
						String result = (String) method.invoke(this, si, hs, propValues);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("[Debug ESXi(" + hsName + ")]  " + methodName + " result = " + result);
						}
						sb.append("<").append(PROPTITLE[i]).append(">").append(result).append("</").append(PROPTITLE[i])
								.append(">");
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
				sb.append('<').append('/').append(uuid).append('>');
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getPropValue Current ResultSet is [" + sb.toString() + "]. ip is " + parameter.getIp()
						+ " lossTime=" + (System.currentTimeMillis() - temp));
			}
			return sb.toString();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getPerfValue(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			long temp = System.currentTimeMillis();
			StringBuilder sb = new StringBuilder();
			final ManagedEntity[] hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);
			Map<String, ManagedObjectReference> s_morMap = new HashMap<String, ManagedObjectReference>();
			Map<String, Object>[] dsPropValues = null;
			try {
				dsPropValues = PropertyCollectorUtil.retrieveProperties(hss, hss[0].getMOR().getType(),
						new String[]{"summary","name"});
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			if (dsPropValues == null || dsPropValues.length <= 0) {
				return null;
			}
			for (int i = 0; i < hss.length; i++) {
				if (!(dsPropValues[i].get("summary") instanceof HostListSummary)) {
					continue;
				}
				
				String hsName = (String) dsPropValues[i].get("name");
				String dsUrl = ((HostListSummary) dsPropValues[i].get("summary")).getHardware().getUuid();
				String serverIP = ((HostListSummary) dsPropValues[i].get("summary")).getManagementServerIp();
				String uuid = VSphereUtil.createUuid(dsUrl,hsName,serverIP);
				s_morMap.put(uuid, hss[i].getMOR());
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
						} catch (SecurityException e) {
							LOGGER.error(e.getMessage(), e);
							continue;
						}
						String result = null;
						try {
							result = (String) method.invoke(this, entry.getValue());
						} catch (IllegalAccessException e) {
							LOGGER.error(e.getMessage(), e);
						} catch (IllegalArgumentException e) {
							LOGGER.error(e.getMessage(), e);
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
						}
						sb.append("<").append(perfMethod).append(">").append(result).append("</").append(perfMethod)
								.append(">");
					}
					sb.append('<').append('/').append(entry.getKey()).append('>');
				}

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getPerfValue Current ResultSet is [" + sb.toString() + "]. ip is " + parameter.getIp()
						+ " lossTime=" + (System.currentTimeMillis() - temp));
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
			final ManagedEntity[] hosts = getHostSystems(si);
			// LOGGER.error("==================虚机_getPerfValue_vm_uuid:"+uuid+"=============");
			if (null == hosts || hosts.length <= 0) {
				return null;
			}
			Map<String, Object>[] propValues = null;
			try {
				propValues = PropertyCollectorUtil.retrieveProperties(hosts, hosts[0].getMOR().getType(),
						new String[] { SUMMARY_RUNTIME_CONNECTION_STATE,"summary","name" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			if (propValues == null || propValues.length <= 0) {
				return null;
			}
			for (int i = 0; i < propValues.length; i++) {
				if (propValues[i] != null) {
					HostSystemConnectionState st = (HostSystemConnectionState) propValues[i]
							.get(SUMMARY_RUNTIME_CONNECTION_STATE);
					String hsName = (String) propValues[i].get("name");
					String dsUrl = ((HostListSummary) propValues[i].get("summary")).getHardware().getUuid();
					String serverIP = ((HostListSummary) propValues[i].get("summary")).getManagementServerIp();
					String uuid = VSphereUtil.createUuid(dsUrl,hsName,serverIP);
//					String uuid = VSphereUtil.getHostUuid((HostSystem) hosts[i]);
					b.append('<').append(uuid).append('>');
					if (null != st && st.equals(HostSystemConnectionState.connected)) {
						b.append(CommonConstants.AVAIL);
					} else {
						b.append(CommonConstants.UN_AVAIL);
					}
					b.append('<').append('/').append(uuid).append('>');
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		return b.toString();
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

	@SuppressWarnings("unchecked")
	public String getDatastoreList(final JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		ManagedEntity[] t_hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);
		if (t_hss != null && t_hss.length > 0) {
			Map<String, ManagedObjectReference[]>[] storeProps = null;
			try {
				storeProps = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
						new String[] { "datastore" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getDatastoreList", e);
				}
			}
			if (storeProps != null) {
				ManagedEntity[] t_vss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
				Map<String, String> uuidRefMap = null;
				Map<String, DatastoreSummary>[] uuidMaps = null;
				try {
					uuidMaps = PropertyCollectorUtil.retrieveProperties(t_vss, t_vss[0].getMOR().getType(),
							new String[] { "summary" });
					uuidRefMap = new HashMap<>(uuidMaps.length);
					for (int i = 0; i < uuidMaps.length; i++) {
						DatastoreSummary uuid = uuidMaps[i].get("summary");
						if (uuid != null) {
							uuidRefMap.put(t_vss[i].getMOR().getVal(), VSphereUtil.createDatastoreUUID(uuid.getUrl()));
						}
					}
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getDatastoreList", e);
					}
				}
				for (int i = 0; i < storeProps.length; i++) {
					ManagedObjectReference[] tdataStores = storeProps[i].get("datastore");
					String uuid = VSphereUtil.getHostUuid((HostSystem) t_hss[i]);
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
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public String getVMList(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		ManagedEntity[] t_hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);
		if (t_hss != null && t_hss.length > 0) {
			Map<String, Object>[] storeProps = null;
			try {
				storeProps = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
						new String[] { "vm" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getVMList", e);
				}
			}
			if (storeProps != null) {
				ManagedEntity[] t_vss = VSphereUtil.find(si, VSphereUtil.TYPE_VM);
				Map<String, String> uuidRefMap = null;
				Map<String, String>[] uuidMaps = null;
				try {
					uuidMaps = PropertyCollectorUtil.retrieveProperties(t_vss, t_vss[0].getMOR().getType(),
							new String[] { SUMMARY_CONFIG_INSTANCE_UUID });
					uuidRefMap = new HashMap<>(uuidMaps.length);
					for (int i = 0; i < uuidMaps.length; i++) {
						String uuid = uuidMaps[i].get(SUMMARY_CONFIG_INSTANCE_UUID);
						if (uuid != null) {
							uuidRefMap.put(t_vss[i].getMOR().getVal(), uuid);
						}
					}
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getVMList", e);
					}
				}
				for (int i = 0; i < storeProps.length; i++) {
					ManagedObjectReference[] tVMs = (ManagedObjectReference[]) storeProps[i].get("vm");
					String uuid = VSphereUtil.getHostUuid((HostSystem) t_hss[i]);
					sb.append('<').append(uuid).append('>');
					if (tVMs != null && tVMs.length > 0) {
						sb.append(uuidRefMap.get(tVMs[0].getVal()));
						for (int j = 1; j < tVMs.length; j++) {
							sb.append(',').append(uuidRefMap.get(tVMs[j].getVal()));
						}
					}
					sb.append('<').append('/').append(uuid).append('>');
				}
			}
		}
		return sb.toString();
	}

	public String getNICList(JBrokerParameter parameter) {
		StringBuilder sb = new StringBuilder();
		sb.append("vmnic1").append("\t");
		sb.append("2").append("\t");
		sb.append("3").append("\t");
		sb.append("4").append("\t");
		sb.append("5").append("\t");
		sb.append("6");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public String getStorageAdapterList(JBrokerParameter parameter)
			throws InvalidProperty, RuntimeFault, RemoteException, UnsupportedEncodingException {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		ManagedEntity[] t_hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);

		StringBuilder sb = new StringBuilder();
		try {
			if (null != t_hss) {
				Map<String, Object>[] props = null;
				try {
					props = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
							new String[] { "config","summary","name" });
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getVMList", e);
					}
				}
				if (props != null && props.length > 0) {
					for (int i = 0; i < props.length; i++) {
						HostSystem hs = (HostSystem) t_hss[i];
//						String uuid = VSphereUtil.getHostUuid(hs);
						String hsName = (String) props[i].get("name");
						String dsUrl = ((HostListSummary) props[i].get("summary")).getHardware().getUuid();
						String serverIP = ((HostListSummary) props[i].get("summary")).getManagementServerIp();
						String uuid = VSphereUtil.createUuid(dsUrl,hsName,serverIP);
						HostConfigInfo configInfo = (HostConfigInfo) props[i].get("config");
						HostStorageDeviceInfo hostStorageDeviceInfo = configInfo.getStorageDevice();
						HostHostBusAdapter[] hostHostBusAdapters = hostStorageDeviceInfo.getHostBusAdapter();
						sb.append('<').append(uuid).append('>');
						// two
						for (HostHostBusAdapter dtAdapter : hostHostBusAdapters) {
							/*************** 子子:详细信息 **********/
							// three
							for (ScsiLun threeLayerItem : hostStorageDeviceInfo.getScsiLun()) {
								/*************************************/
								String storageAdapterName = dtAdapter.getDevice();// 名称
								String storageAdapterId = (VSphereUtil.createUuid(storageAdapterName) + "-"
										+ threeLayerItem.getDeviceName());
								// String storageAdapterModle =
								// dtAdapter.getModel();// 型号
								String opState = null;
								/*************************************/
								if (threeLayerItem.getDeviceName().substring(24, 31).equals(storageAdapterName)
										|| threeLayerItem.getDeviceName().substring(24, 30).equals(storageAdapterName)
										|| threeLayerItem.getDeviceName().substring(24, 32)
												.equals(storageAdapterName)) {
									System.out.println("进入...................................");
									// 索引-3、设备名-2、所属主机-1、标识符0、运行时名称1、操作状况2、LUN3
									// String singleStorageAdapterUuid =
									// storageAdapterId+
									// "-" + threeLayerItem.getDeviceName();
									for (String opstaate : threeLayerItem.operationalState) {
										if (null != opstaate) {
											opState = opstaate;
										}
									}

									sb.append("<StorageAdapter>")
											// 列-3索引
											.append(storageAdapterId).append("\t")
											// 列-2设备名
											.append(storageAdapterName).append("\t")
											// 列-1所属主机
											// .append(storageAdapterModle)
											// .append("\t")
											// 列0标识符
											.append(threeLayerItem.getCanonicalName()).append("\t")
											// 列1运行时名称
											.append(threeLayerItem.getCanonicalName().substring(4,
													threeLayerItem.getCanonicalName().length()))
											.append("\t")
											// 列2操作状况
											.append(opState.equals("ok") ? "已挂载" : "未知").append("\t")
											// 列3LUN
											.append(threeLayerItem.lunType.equals("cdrom") ? 0 : threeLayerItem.lunType)
											.append("</StorageAdapter>");
								}
							}
						}
						sb.append('<').append('/').append(uuid).append('>');
					}
				}
			}
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("getStorageAdapterList", e);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getStorageAdapterList返回值：------->+sb" + sb.toString());
		}
		return sb.toString();
	}

	/**
	 * 网络适配器 设备名称列表
	 * 
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getNetWorkAdapterList(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		ManagedEntity[] t_hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);
		if (null != t_hss) {
			Map<String, Object>[] props = null;
			try {
				props = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
						new String[] { "config","summary","name" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getNetWorkAdapterList", e);
				}
			}
			if (props != null && props.length > 0) {
				for (int i = 0; i < props.length; i++) {
					HostSystem hs = (HostSystem) t_hss[i];
//					String uuid = VSphereUtil.getHostUuid(hs);
					String hsName = (String) props[i].get("name");
					String dsUrl = ((HostListSummary) props[i].get("summary")).getHardware().getUuid();
					String serverIP = ((HostListSummary) props[i].get("summary")).getManagementServerIp();
					String uuid = VSphereUtil.createUuid(dsUrl,hsName,serverIP);
					HostConfigInfo configInfo = (HostConfigInfo) props[i].get("config");
					PhysicalNic[] physicalNics = configInfo.getNetwork().getPnic();
					sb.append('<').append(uuid).append('>');
					for (PhysicalNic physicalNic : physicalNics) {
						String netWorkAdapterName = physicalNic.getDevice();
						String netWorkAdapterId = VSphereUtil.createUuid(netWorkAdapterName);
						int netWorkSpeedMb = 0;
						if (null != physicalNic.getLinkSpeed()) {
							netWorkSpeedMb = physicalNic.getLinkSpeed().getSpeedMb();
						}
						// ++是否已配置
						String netWrokAdapterIsNoneSupportConfig = physicalNic.autoNegotiateSupported == true ? "协商"
								: "未协商";
						/*
						 * if(null!=physicalNic.fcoeConfiguration){
						 * netWrokAdapterIsNoneSupportConfig =
						 * physicalNic.fcoeConfiguration.fcoeActive == true ?
						 * "已配置": "未配置"; } else {
						 * netWrokAdapterIsNoneSupportConfig ="协商"; }
						 */
						// String netWrokAdapterIsNoneSupportConfig =
						// physicalNic.fcoeConfiguration.getCapabilities() ==
						// true ? "已配置":
						// "未配置";
						System.out.println(netWrokAdapterIsNoneSupportConfig);
						// ++是否支持LAN唤醒
						// String netWorkAdpaterIsNoneSupportLanRouse =
						// physicalNic.autoNegotiateSupported == true ? "是":
						// "否";
						String netWorkAdpaterIsNoneSupportLanRouse = physicalNic.wakeOnLanSupported == true ? "是" : "否";
						System.out.println(netWorkAdpaterIsNoneSupportLanRouse);

						String netWorkAdapterMac = physicalNic.getMac();
						// sb.append("<NetWorkAdapter>").append(netWorkAdapterId).append("\t")
						// .append(netWorkAdapterName).append("\t")
						// .append(netWorkSpeedMb).append("\t")
						// .append(netWorkAdapterMac).append("</NetWorkAdapter>");
						sb.append("<NetWorkAdapter>").append(netWorkAdapterId).append("\t").append(netWorkAdapterName)
								.append("\t").append(netWorkSpeedMb).append("\t").append(netWorkAdapterMac).append("\t")
								.append(netWrokAdapterIsNoneSupportConfig).append("\t")
								.append(netWorkAdpaterIsNoneSupportLanRouse).append("</NetWorkAdapter>");
						System.out.println("网络--->" + sb);
					}
					sb.append('<').append('/').append(uuid).append('>');
				}
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getNetWorkAdapterList返回值：------->+sb" + sb.toString());
		}
		return sb.toString();
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
	public String getManufature(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		// summary.config.product.vendor
		final String r = (String) propValues.get(SUMMARY_HARDWARE_VENDOR);
		return r;
	}

	public String getSysUpTime(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		// summary.config.product.vendor
		final String r = ((HostListSummary) propValues.get("summary")).getQuickStats().getUptime() + "";
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
	public String getModel(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = SUMMARY_CONFIG_PRODUCFULL_NAME;
		final String r = (String) propValues.get(path);
		String arr[] = StringUtils.split(r, " ");
		if (arr != null && arr.length > 2) {
			return arr[2];
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
	public String getVMotion(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = SUMMARY_CONFIG_VMOTION_ENABLED;
		final Boolean r = (Boolean) propValues.get(path);
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
	public String getFT(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = SUMMARY_CONFIG_FAULTOLERANCE_ENABLED;
		final Boolean r = (Boolean) propValues.get(path);
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
	public String getCoreOfCPU(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = SUMMARY_HARDWARE_NUM_CPU_CORES;
		final Short r = (Short) propValues.get(path);
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
	public String getCoreNumOfCPU(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = SUMMARY_HARDWARE_NUM_CPU_CORES;
		final String path2 = SUMMARY_HARDWARE_NUM_CPU_PKGS;
		final Short core = (Short) propValues.get(path);
		final Short pkg = (Short) propValues.get(path2);
		if (0 != pkg) {
			final int r = core / pkg;
			return String.valueOf(r);
		}
		return String.valueOf(core);
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
	// 7997
	public String getSlotOfProcessor(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		// final String path = SUMMARY_HARDWARE_NUM_CPU_PKGS;
		final String path = "summary.hardware.numCpuThreads";
		final Short r = (Short) propValues.get(path);
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
	public String getNumOfLogicalProcessor(final ServiceInstance si, final HostSystem hs,
			Map<String, Object> propValues) {
		final String path = "summary.hardware.numCpuThreads";
		final Short r = (Short) propValues.get(path);
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
	public String getHT(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		// config.hyperThread.available
		final String path = "config.hyperThread.active";
		final Boolean r = (Boolean) propValues.get(path);
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
	public String getEVC(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = "summary.currentEVCModeKey";
		final String r = (String) propValues.get(path);
		if (StringUtils.isEmpty(r)) {
			return "disabled";
		}
		return r;
	}

	/**
	 * {method description}. #8715
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getNumberOfVM(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = VM;
		try {
			if (null != hs) {
				final ManagedObjectReference[] r = (ManagedObjectReference[]) propValues.get(path);
				if (null != r) {
					return String.valueOf(r.length);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
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
	public String getCPUType(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = "summary.hardware.cpuModel";
		final String r = (String) propValues.get(path);
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
	public String getSensorState(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = "summary.overallStatus";
		final ManagedEntityStatus mes = (ManagedEntityStatus) propValues.get(path);
		return mes.toString();
	}

	/**
	 * {method description}.
	 * 
	 * @param conn
	 *            VMWareConnectInfo
	 * @param task
	 *            CollTask4ResPojo
	 * @return Matric
	 */
	public String getMEMSize(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		if (null != si && null != hs) {
			Double ass = ((Long) propValues.get("hardware.memorySize")).doubleValue() / C_1024 / C_1024;
			return ass + "";
		}
		return null;
	}

	// TODO:
	@SuppressWarnings("unchecked")
	public String getDatastoreSize(final JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		ManagedEntity[] t_hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);
		if (t_hss != null && t_hss.length > 0) {
			Map<String, Object>[] storeProps = null;
			try {
				storeProps = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
						new String[] { "datastore","summary","name" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getVMList", e);
				}
			}
			if (storeProps != null) {
				ManagedEntity[] t_vss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
				Map<String, Long> uuidRefMap = null;
				Map<String, Long>[] uuidMaps = null;
				try {
					uuidMaps = PropertyCollectorUtil.retrieveProperties(t_vss, t_vss[0].getMOR().getType(),
							new String[] { "summary.capacity" });
					uuidRefMap = new HashMap<>(uuidMaps.length);
					for (int i = 0; i < uuidMaps.length; i++) {
						uuidRefMap.put(t_vss[i].getMOR().getVal(), uuidMaps[i].get("summary.capacity"));
					}
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getVMList", e);
					}
				}
				for (int i = 0; i < storeProps.length; i++) {
					ManagedObjectReference[] tdataStores = (ManagedObjectReference[]) storeProps[i].get("datastore");
//					String uuid = VSphereUtil.getHostUuid((HostSystem) t_hss[i]);
					String hsName = (String) storeProps[i].get("name");
					String dsUrl = ((HostListSummary) storeProps[i].get("summary")).getHardware().getUuid();
					String serverIP = ((HostListSummary) storeProps[i].get("summary")).getManagementServerIp();
					String uuid = VSphereUtil.createUuid(dsUrl,hsName,serverIP);
					sb.append('<').append(uuid).append('>');
					long t_l = 0L;
					if (tdataStores != null && tdataStores.length > 0) {
						for (int j = 0; j < tdataStores.length; j++) {
							ManagedObjectReference dtr = tdataStores[j];
							Long value = uuidRefMap.get(dtr.getVal());
							if (value != null) {
								t_l += value.longValue();
							}
						}
					}
					String t_r2 = convert2DecimalString(
							Long.valueOf(t_l).doubleValue() / C_1024 / C_1024 / C_1024 / C_1024);
					sb.append(t_r2);
					sb.append('<').append('/').append(uuid).append('>');
				}
			}
		}
		return sb.toString();
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
	public String getNumberOfNIC(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = "summary.hardware.numNics";
		final Integer r = (Integer) propValues.get(path);
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
	public String getHA(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		final String path = "summary.runtime.dasHostState.state";
		final String r = (String) propValues.get(path);
		return r;
	}

	/**
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 * @7434;yuanlb
	 */
	public String getCPURate(final HashMap<String, long[]> map) {
		// expressed as a hundredth of a percent (1 = 0.01%), A value between 0
		// and 10,000.
		final String counter = "cpu.usage.average";
		final long r = map.get(counter)[0];
		final String str = convert2DecimalString(Long.valueOf(r).doubleValue() / C_100);
		// System.out.println("+++++++++++++++++++" + str);
		return str;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 */
	public String getCPUUsed(final HashMap<String, long[]> map) {
		// summary.quickStats.overallCpuUsage(MHZ)
		// megaHertz
		final String counter = "cpu.usagemhz.average";
		final long r = map.get(counter)[0];
		return String.valueOf(r);
	}

	public String getCPUTotal(final ServiceInstance si, final HostSystem hs, Map<String, Object> propValues) {
		/*
		 * if (null == hs) { return "0"; } HostCpuPackage[] CpuPackages =
		 * hs.getHardware().getCpuPkg(); Long allSpeed = 0L; int cpuNum = 0; for
		 * (HostCpuPackage cpu : CpuPackages) { cpuNum = cpu.threadId.length;
		 * Long speed = cpu.getHz(); allSpeed += speed; System.out.println(); }
		 * final String str =
		 * convert2DecimalString(cpuNum*allSpeed.doubleValue() / C_1000 / C_1000
		 * / C_1000);
		 * 
		 * return str;
		 */
		if (null == hs) {
			return "0";
		}
		// hs.getHardware().getCpuInfo().getNumCpuCores();//cores
		// hs.getHardware().getCpuInfo().getHz();//频率
		// System.out.println(hs.getHardware().getCpuInfo().getNumCpuCores());
		// System.out.println(hs.getHardware().getCpuInfo().getHz());
		Long allSpeed = 0L;
		int cpuNum = 0;
		cpuNum = ((HostHardwareInfo) propValues.get("hardware")).getCpuInfo().getNumCpuCores();// cores
		allSpeed = hs.getHardware().getCpuInfo().getHz();// 频率
		final String str = convert2DecimalString(cpuNum * allSpeed.doubleValue() / C_1000 / C_1000 / C_1000);
		return str;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 */
	public String getMEMVirtualIncrease(final HashMap<String, long[]> map) {
		// kiloBytes
		final String counter = "mem.vmmemctl.average";
		final long r = map.get(counter)[0];
		final String str = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024);
		return str;
	}

	/**
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 */
	public String getMEMSwapInRate(final HashMap<String, long[]> map) {
		final String counter = "mem.swapinRate.average";
		final long r = map.get(counter)[0];
		final String str = convert2DecimalString(Long.valueOf(r).doubleValue());
		return str;
	}

	/*  *//**
			 * {method description}.
			 * 
			 * @param map
			 *            HashMap<String, long[]> map
			 * @return String
			 */
	/*
	 * public String getMEMSwapInRate(final HashMap<String, long[]> map) { //
	 * kiloBytesPerSecond // mem.swapinRate.average","mem.swapoutRate.average"
	 * // final String counter = "mem.swapinRate.average"; final String counter
	 * = "mem.swaptarget.average"; final long r = map.get(counter)[0]; final
	 * String str = convert2DecimalString(Long.valueOf(r).doubleValue() /
	 * C_1024); return str; }
	 */

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 */
	public String getMEMSwapOutRate(final HashMap<String, long[]> map) {
		// kiloBytesPerSecond
		final String counter = "mem.swapoutRate.average";
		final long r = map.get(counter)[0];
		/*
		 * final String str =
		 * convert2DecimalString(Long.valueOf(r).doubleValue()/ C_1024);
		 */
		final String str = convert2DecimalString(Long.valueOf(r).doubleValue());
		return str;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 * @7434;yuanlb
	 */
	public String getMEMRate(final HashMap<String, long[]> map) {
		// expressed as a hundredth of a percent (1 = 0.01%), A value between 0
		// and 10,000.
		final String counter = "mem.usage.average";
		final long r = map.get(counter)[0];
		final String str = convert2DecimalString(Long.valueOf(r).doubleValue() / C_100);
		return str;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 */
	public String getMEMUsed(final HashMap<String, long[]> map) {
		// summary.quickStats.overallMemoryUsage(MB)
		// kiloBytes
		final String counter = "mem.consumed.average";
		final long r = map.get(counter)[0];
		final String str = convert2DecimalString(Long.valueOf(r).doubleValue() / C_1024);
		return str;
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 */
	public String getMaxDiskLatency(final HashMap<String, long[]> map) {
		// millisecond
		final String counter = "disk.maxTotalLatency.latest";
		final long r = map.get(counter)[0];
		return String.valueOf(r);
	}

	/**
	 * {method description}.
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 */
	public String getDiskUsed(final HashMap<String, long[]> map) {
		// kiloBytesPerSecond
		final String counter = "disk.usage.average";
		final long r = map.get(counter)[0];
		return String.valueOf(r);
	}

	/**
	 * 
	 * @param map
	 *            HashMap<String, long[]> map
	 * @return String
	 */
	public String getNetworkUsed(final HashMap<String, long[]> map) {
		// kiloBytesPerSecond
		final String counter = "net.usage.average";
		final long r = map.get(counter)[0];
		final String str = convert2DecimalString(Long.valueOf(r).doubleValue());
		return str;
	}

	/**
	 * {method description}.
	 * 
	 * @param conn
	 *            VMWareConnectInfo
	 * @param task
	 *            CollTask4ResPojo
	 * @return Matric
	 */
	public String getSensorStateList(JBrokerParameter parameter) {
		return null;
	}
}
