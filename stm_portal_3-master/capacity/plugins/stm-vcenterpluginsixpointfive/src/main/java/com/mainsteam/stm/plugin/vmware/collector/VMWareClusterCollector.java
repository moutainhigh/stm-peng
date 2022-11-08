package com.mainsteam.stm.plugin.vmware.collector;

import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.CryptoPrimitive;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.vmware.constants.CommonConstants;
import com.mainsteam.stm.plugin.vmware.constants.VMWareClusterConstants;
import com.mainsteam.stm.plugin.vmware.util.VSphereUtil;
import com.mainsteam.stm.pluginsession.parameter.JBrokerParameter;
import com.vmware.vim25.ClusterComputeResourceSummary;
import com.vmware.vim25.DatastoreHostMount;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.HostSystemConnectionState;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ClusterComputeResource;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.MorUtil;
import com.vmware.vim25.mo.util.PropertyCollectorUtil;

/**
 * {class description} <br>
 * <p>
 * Create on : 2013-7-18<br>
 * </p>
 * <br>
 * 
 * @author demo<br>
 * @version 3-mainsteam-plugins-vmware v6.2.0 <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          --------------------------------------------<br>
 *          <br>
 */
public class VMWareClusterCollector extends VMWareBaseCollector {
	public SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Log LOGGER = LogFactory.getLog(VMWareClusterCollector.class);
	// private static final String EXP_AVAIL_CULSTER =
	// "EXP_AVAIL_CULSTER:";summary.runtime.connectionState
	private static final java.lang.String SUMMARY_RUNTIME_CONNECTION_STATE = "summary.runtime.connectionState";
	private static final String GET = "get";

	private static final String[] PERFCOUNTER = new String[] { "cpu.usagemhz.average", //
			"mem.consumed.average", //
	};

	private static final String[] PROPTITLE = new String[] { VMWareClusterConstants.C_CPUPERCENT, // CPU
																									// 百分比
			VMWareClusterConstants.C_MEMPERCENT, // 内存 百分比
			// VMWareClusterConstants.C_VMLIST, //
			// VMWareClusterConstants.C_DATASTORELIST, //
			VMWareClusterConstants.C_HASTATUS, //
			VMWareClusterConstants.C_DRSSTATUS, //
			VMWareClusterConstants.C_EVCMODE, //
			VMWareClusterConstants.C_CPUNUM, //
			VMWareClusterConstants.C_MEMNUM, //
			// VMWareClusterConstants.C_STORAGENUM, //
			VMWareClusterConstants.C_HOSTNUM, //
			// VMWareClusterConstants.C_HOSTLIST, //

			VMWareClusterConstants.C_CPUAVERAGE, //
			VMWareClusterConstants.C_MEMCONSUME, //

			// VMWareClusterConstants.C_VMNUM, //
			VMWareClusterConstants.C_PROCESSORNUM, //
			VMWareClusterConstants.C_VMOTIONNUM, //
			VMWareClusterConstants.C_TOTALCPU, //
			VMWareClusterConstants.C_USEDCPU, //
			VMWareClusterConstants.C_AVAILCPU, //
			VMWareClusterConstants.C_TOTALMEM, //
			VMWareClusterConstants.C_USEDMEM, //
			VMWareClusterConstants.C_AVAILMEM, //
			VMWareClusterConstants.C_RESDISPLAYNAME //
	};

	private static final String[] PROP_TITLE_FILEDS = { "summary.totalMemory", "summary.effectiveCpu",
			"summary.numCpuCores", "summary.numHosts", "summary.totalMemory", "summary.totalCpu",
			"configuration.drsConfig.enabled", "configuration.dasConfig.enabled", "name", "summary","resourcePool" };
	private static final String[] PROP_POOL_TITLE_FILEDS = { "summary.runtime.cpu.overallUsage",
			"summary.runtime.cpu.unreservedForPool", "summary.runtime.memory.overallUsage",
			"summary.runtime.memory.unreservedForPool" };
	/**
	 * <code>C_PERFTITLE</code> - {description}.
	 */
	private static final String[] PERFTITLE = new String[] {};

	public static void main(String[] args) throws Exception {
		JBrokerParameter parameter = new JBrokerParameter();

		parameter.setIp("192.168.31.129");
		parameter.setUsername("administrator@vsphere.local");
		parameter.setPassword("Root123!@#");

		URL url = new URL("https", parameter.getIp(), "/sdk");
		ServiceInstance serviceInstance = new ServiceInstance(url, parameter.getUsername(), parameter.getPassword(),
				true);
		parameter.setConnection(serviceInstance);
		//新建集群cluster@
//		parameter.setUuid("1c45b7d0-1e7f-3b60-af2c-5563356b1cce");
		//新建群集1
//		parameter.setUuid("e9eb0346-8464-39c8-b5e1-c7878bf1c95c");
		//新建群集2
		parameter.setUuid("9f4f378e-15c7-3af7-9880-019f77511bf1");
		
		VMWareClusterCollector cluster = new VMWareClusterCollector();
		System.out.println("可用性:===================================>" + cluster.getAvailability(parameter));
		System.out.println("prop:");
		String str2 = cluster.getPropValue(parameter);
		System.out.println(str2);
		System.out.println("perf:");
		String str3 = cluster.getPerfValue(parameter);
		System.out.println(str3);
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
	 * @author yuanlb
	 * @param si
	 * @param cluster
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getDatastoreList(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		ManagedEntity[] t_hss = getClusterComputeResources(si);
		if (t_hss != null && t_hss.length > 0) {
			Map<String, Object>[] storeProps = null;
			try {
				storeProps = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
						new String[] { "datastore", "name" });
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
					if (storeProps[i].get("datastore") instanceof ManagedObjectReference[]) {
						ManagedObjectReference[] tdataStores = (ManagedObjectReference[]) storeProps[i]
								.get("datastore");
						String uuid = VSphereUtil.createUuid((String) storeProps[i].get("name"));
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
	 * {method description}. 虚拟机列表
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getVMList(JBrokerParameter parameter) {
		StringBuilder sb = new StringBuilder();
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			ManagedEntity[] t_hss = getClusterComputeResources(si);
			if (t_hss != null && t_hss.length > 0) {
				Map<String, Object>[] storeProps = null;
				try {
					storeProps = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
							new String[] { "host", "name" });
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getVMList", e);
					}
				}
				if (storeProps != null) {
					Map<String, ManagedObjectReference[]> hostRefMap = new HashMap<>(storeProps.length);
					List<HostSystem> hosts = new ArrayList<>();
					for (Map<String, Object> map : storeProps) {
						ManagedObjectReference[] hostRefs = (ManagedObjectReference[]) map.get("host");
						if (hostRefs != null && hostRefs.length > 0) {
							for (ManagedObjectReference hr : hostRefs) {
								hosts.add(new HostSystem(si.getServerConnection(), hr));
							}
						}
					}
					if (hosts.size() > 0) {
						Map<String, Object>[] hostProps = null;

						HostSystem[] hostSystems = new HostSystem[hosts.size()];
						hosts.toArray(hostSystems);
						try {
							hostProps = PropertyCollectorUtil.retrieveProperties(hostSystems,
									hostSystems[0].getMOR().getType(), new String[] { "vm" });
						} catch (RemoteException e) {
							if (LOGGER.isErrorEnabled()) {
								LOGGER.error("getVMList", e);
							}
						}
						if (hostProps != null && hostProps.length > 0) {
							for (int i = 0; i < hostProps.length; i++) {
								if (hostProps[i].get("vm") instanceof ManagedObjectReference[]) {
									ManagedObjectReference[] vms = (ManagedObjectReference[]) hostProps[i].get("vm");
									if (vms != null && vms.length > 0) {
										hostRefMap.put(hostSystems[0].getMOR().getVal(), vms);
									}
								}
							}
						}
					}
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
						if (storeProps[i].get("host") instanceof ManagedObjectReference[]) {
							ManagedObjectReference[] hostList = (ManagedObjectReference[]) storeProps[i].get("host");
							String uuid = VSphereUtil.createUuid((String) storeProps[i].get("name"));
							sb.append('<').append(uuid).append('>');
							if (hostList != null && hostList.length > 0) {
								String prefix = null;
								for (int j = 0; j < hostList.length; j++) {
									ManagedObjectReference[] vms = (ManagedObjectReference[]) hostRefMap
											.get(hostList[j].getVal());
									if (vms != null && vms.length > 0) {
										for (int k = 0; k < vms.length; k++) {
											if (prefix != null) {
												sb.append(prefix).append(uuidRefMap.get(vms[k].getVal()));
											} else {
												sb.append(uuidRefMap.get(vms[k].getVal()));
												prefix = ",";
											}
										}
									}
								}
							}
							sb.append('<').append('/').append(uuid).append('>');
						}
					}
				}
			}
		}
		return sb.toString();
	}

	public String getName(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder b = new StringBuilder();
		if (null != si) {
			final ManagedEntity[] t_clusters = getClusterComputeResources(si);
			Map<String, Object>[] cpropValues = null;
			try {
				cpropValues = PropertyCollectorUtil.retrieveProperties(t_clusters, t_clusters[0].getMOR().getType(),
						new String[] { "name" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			for (int i = 0; i < cpropValues.length; i++) {
				if (null == cpropValues[i]) {
					continue;
				}
				String hsName = (String) cpropValues[i].get("name");
				if (hsName == null) {
					continue;
				}
				String uuid = VSphereUtil.createUuid(hsName);
				b.append('<').append(uuid).append('>');
				b.append(uuid);
				b.append('<').append('/').append(uuid).append('>');
			}
		}
		return b.toString();
	}

	/**
	 * 
	 * @param conn
	 *            VMWareConnectInfo
	 * @param task
	 *            CollTask4ResPojo
	 * @return Matric
	 */
	@SuppressWarnings("unchecked")
	public String getPropValue(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			StringBuilder sb = new StringBuilder();
			long temp = System.currentTimeMillis();
			final ManagedEntity[] t_clusters = getClusterComputeResources(si);
			ResourcePool[] pools = new ResourcePool[t_clusters.length];
			
			Map<String, Object>[] cpropValues = null;
			try {
				cpropValues = PropertyCollectorUtil.retrieveProperties(t_clusters, t_clusters[0].getMOR().getType(),
						PROP_TITLE_FILEDS);
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			for (int i = 0; i < pools.length; i++) {
				if (cpropValues[i].get("resourcePool") instanceof ManagedObjectReference) {
					pools[i] = (ResourcePool) MorUtil.createExactManagedObject(si.getServerConnection(),
							(ManagedObjectReference) cpropValues[i].get("resourcePool"));
				}
			}
			Map<String, Object>[] poolpropValues = null;
			try {
				poolpropValues = PropertyCollectorUtil.retrieveProperties(pools, pools[0].getMOR().getType(),
						PROP_POOL_TITLE_FILEDS);
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			if (poolpropValues == null || poolpropValues.length <= 0) {
				return null;
			}
			for (int k = 0; k < t_clusters.length; k++) {
				final ClusterComputeResource t_cluster = (ClusterComputeResource) t_clusters[k];
				String t_clusterName = "";
				if (null != t_cluster) {
					t_clusterName = t_cluster.getName();
				}
				String uuid = VSphereUtil.getUuid(t_cluster);
				sb.append("<" + uuid + ">");
				Map<String, Object> propMap = cpropValues[k];
				Map<String, Object> propMap1 = poolpropValues[k];
				propMap.putAll(propMap1);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("[Debug Cluster(" + t_clusterName + ")] PerfMetric Come On ");
				}
				String[] paths = new String[PROPTITLE.length];
				for (int i = 0; i < PROPTITLE.length; i++) {
					final String methodName = GET + PROPTITLE[i];
					paths[i] = methodName;
					Method method;
					try {
						method = VMWareClusterCollector.class.getMethod(methodName, ServiceInstance.class,
								ClusterComputeResource.class, Map.class);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("[Debug ESXi(" + t_clusterName + ")]  " + methodName + " come on ");
						}
						String result = (String) method.invoke(this, si, t_cluster, propMap);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("[Debug ESXi(" + t_clusterName + ")]  " + methodName + " result = " + result);
						}
						sb.append("<").append(PROPTITLE[i]).append(">").append(result).append("</").append(PROPTITLE[i])
								.append(">");
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
				sb.append("</" + uuid + ">");
			}
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("getPropValue Current ResultSet is [" + sb.toString() + "]. ip is " + parameter.getIp()
						+ " lossTime=" + (System.currentTimeMillis() - temp));
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 获取性能指标
	 * 
	 * @param parameter
	 * @return
	 */
	public String getPerfValue(JBrokerParameter parameter) {
		// LOGGER.info(df.format(new Date()));
		return null;

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
	public String getAvailability(JBrokerParameter parameter) {
		StringBuilder b = new StringBuilder();
		try {
			ServiceInstance si = (ServiceInstance) parameter.getConnection();
			if (null != si) {
				ManagedEntity[] t_mes = getClusterComputeResources(si);
				if (null == t_mes || t_mes.length == 0) {
					return null;
				}
				Map<String, Object>[] props = null;
				try {
					props = PropertyCollectorUtil.retrieveProperties(t_mes, t_mes[0].getMOR().getType(),
							new String[] { "host", "name" });
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("isAvail", e);
					}
				}
				ManagedEntity[] t_hosts = getHostSystems(si);
				Map<String, HostSystemConnectionState> availMap = null;
				Map<String, HostSystemConnectionState>[] values = null;
				availMap = new HashMap<>(t_hosts.length);
				try {
					values = PropertyCollectorUtil.retrieveProperties(t_hosts, t_hosts[0].getMOR().getType(),
							new String[] { SUMMARY_RUNTIME_CONNECTION_STATE });
					for (int i = 0; i < values.length; i++) {
						availMap.put(t_hosts[i].getMOR().getVal(), values[i].get(SUMMARY_RUNTIME_CONNECTION_STATE));
					}
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("isAvail", e);
					}
				}
				for (int i = 0; i < t_mes.length; i++) {
					String cluserName = (String) props[i].get("name");
					ManagedObjectReference[] hosts = (ManagedObjectReference[]) props[i].get("host");
					boolean isAvail = false;
					if (hosts != null && hosts.length > 0) {
						if (LOGGER.isInfoEnabled()) {
							LOGGER.info("getAvailability hosts.length=" + hosts.length);
						}
						for (ManagedObjectReference managedEntity : hosts) {
							if (HostSystemConnectionState.connected.equals(availMap.get(managedEntity.getVal()))) {
								isAvail = true;
								break;
							}
						}
					} else {
						if (LOGGER.isInfoEnabled()) {
							LOGGER.info("getAvailability hosts.length=0");
						}
					}
					String uuid = VSphereUtil.createUuid(cluserName);
					b.append('<').append(uuid).append('>');
					b.append(isAvail ? CommonConstants.AVAIL : CommonConstants.UN_AVAIL);
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
	public String getHAStatus(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		final String t_path = "configuration.dasConfig.enabled";
		final Boolean t_r = (Boolean) propMap.get(t_path);
		return t_r == null ? null : t_r.toString();
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
	public String getDRSStatus(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		final String t_path = "configuration.drsConfig.enabled";
		final Boolean t_r = (Boolean) propMap.get(t_path);
		return t_r == null ? null : t_r.toString();
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
	public String getEVCMode(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		final String t_path = "summary";
		final ClusterComputeResourceSummary t_ccrs = (ClusterComputeResourceSummary) propMap.get(t_path);
		if (t_ccrs != null) {
			final String t_r = t_ccrs.getCurrentEVCModeKey();
			if (StringUtils.isEmpty(t_r)) {
				return "disabled";
			}
			return t_r;
		} else {
			return null;
		}
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
	public String getCPUNum(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		// mhz
		final String t_path = "summary.totalCpu";
		Number t_value = (Number) propMap.get(t_path);
		if (t_value != null) {
			final String t_r = convert2DecimalString(t_value.doubleValue() / C_1000);
			return t_r;
		} else {
			return null;
		}
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

	public String getMEMNum(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		final String t_path = "summary.totalMemory";
		Number t_value = (Number) propMap.get(t_path);
		if (t_value != null) {
			final String t_r = convert2DecimalString(t_value.doubleValue() / C_1024 / C_1024 / C_1024);
			return t_r;
		} else {
			return null;
		}
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
	public String getStorageNum(JBrokerParameter parameter) {
		// bytes
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		ManagedEntity[] t_hss = getClusterComputeResources(si);
		if (t_hss != null && t_hss.length > 0) {
			Map<String, Object>[] storeProps = null;
			try {
				storeProps = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
						new String[] { "datastore", "name" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getVMList", e);
				}
			}
			if (storeProps != null) {
				ManagedEntity[] t_vss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
				Map<String, Number> uuidRefMap = null;
				Map<String, Number>[] values = null;
				try {
					values = PropertyCollectorUtil.retrieveProperties(t_vss, t_vss[0].getMOR().getType(),
							new String[] { "summary.capacity" });
					uuidRefMap = new HashMap<>(values.length);
					for (int i = 0; i < values.length; i++) {
						uuidRefMap.put(t_vss[i].getMOR().getVal(), values[i].get("summary.capacity"));
					}
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getDatastoreList", e);
					}
				}
				for (int i = 0; i < storeProps.length; i++) {
					ManagedObjectReference[] tdataStores = (ManagedObjectReference[]) storeProps[i].get("datastore");
					String uuid = VSphereUtil.createUuid((String) storeProps[i].get("name"));
					sb.append('<').append(uuid).append('>');
					long t_l = 0L;
					if (tdataStores != null && tdataStores.length > 0) {
						t_l += uuidRefMap.get(tdataStores[0].getVal()).longValue();
						for (int j = 1; j < tdataStores.length; j++) {
							t_l += uuidRefMap.get(tdataStores[j].getVal()).longValue();
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
	public String getHostNum(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		final String t_path = "summary.numHosts";
		Integer t_value = (Integer) propMap.get(t_path);
		return t_value == null ? null : t_value.toString();
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
	public String getProcessorNum(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		final String t_path = "summary.numCpuCores";
		Short t_value = (Short) propMap.get(t_path);
		return t_value == null ? null : t_value.toString();
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
	public String getVMNum(JBrokerParameter parameter) {
		StringBuilder b = new StringBuilder();
		final String t_path[] = { "host", "name" };
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			ManagedEntity[] t_css = getClusterComputeResources(si);
			for (ManagedEntity cluster : t_css) {
				Hashtable<String, Object> prop = null;
				try {
					prop = cluster.getPropertiesByPaths(t_path);
				} catch (RemoteException e1) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getVMNum", e1);
					}
				}
				if (prop != null) {

					ManagedObjectReference[] t_r = (ManagedObjectReference[]) prop.get("host");
					int number = 0;
					if (null != t_r && t_r.length > 0) {
						HostSystem[] t_hss = new HostSystem[t_r.length];
						for (int i = 0; i < t_hss.length; i++) {
							t_hss[i] = new HostSystem(si.getServerConnection(), t_r[i]);
						}
						Map<String, Object>[] storeProps = null;
						try {
							storeProps = PropertyCollectorUtil.retrieveProperties(t_hss, t_r[0].getType(),
									new String[] { "vm" });
						} catch (RemoteException e) {
							if (LOGGER.isErrorEnabled()) {
								LOGGER.error("getHostList", e);
							}
						}
						number = storeProps == null ? 0 : storeProps.length;
						String uuid = VSphereUtil.createUuid((String) prop.get("name"));
						b.append('<').append(uuid).append('>');
						b.append(number);
						b.append('<').append('/').append(uuid).append('>');
					}
				}
			}
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
	public String getVMotionNum(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		final String t_path = "summary";
		ClusterComputeResourceSummary t_ccrs = (ClusterComputeResourceSummary) propMap.get(t_path);
		if (t_ccrs != null) {
			final int t_r = t_ccrs.getNumVmotions();
			return String.valueOf(t_r);
		}
		return null;
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
	public String getTotalCPU(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		// Mhz
		final String t_path = "summary.effectiveCpu";
		Integer t_value = (Integer) propMap.get(t_path);
		if (t_value != null) {
			t_value = (t_value) / C_1000;
			return t_value.toString();
		}
		return null;
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
	public String getUsedCPU(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		// Mhz
		// final String t_path = "resourcePool";
		// ManagedObjectReference t_mor = (ManagedObjectReference) cluster
		// .getPropertyByPath(t_path);
		// final ResourcePool t_rp = new ResourcePool(si.getServerConnection(),
		// t_mor);
		/*
		 * final Number t_r = (Number) t_rp
		 * .getPropertyByPath("summary.runtime.cpu.reservationUsed");
		 */
		// 解决11751 PRJ-SL-14131@中国检验检疫总局项目 BUG oc4虚拟化模块 集群cpu、mem百分比与实际数据不符
		// (待处理)
		// 解决11638 PRJ-SL-14105@常德云项目 BUG 虚拟化--虚拟主机内存采集值不准确 (处理中)
		final Number t_r = (Number) propMap.get("summary.runtime.cpu.overallUsage");
		return t_r == null ? null : t_r.toString();
	}

	/**
	 * 获取cpu百分比 解决8085
	 * 
	 * @param si
	 * @param cluster
	 * @return
	 */
	public String getCPUPercent(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		Double doubleUsedCpu = Double.parseDouble(getUsedCPU(si, cluster, propMap));
		Double doubleTotalCPU = Double.parseDouble(getTotalCPU(si, cluster, propMap));
		String strCPUPercent = "0.0";
		try {
			if (doubleUsedCpu != null && doubleTotalCPU != null) {
				strCPUPercent = convert2DecimalString(doubleUsedCpu * 100 / (doubleTotalCPU * C_1000));
			}
			return strCPUPercent;
		} catch (Exception e) {
			LOGGER.error("jason say: getCPUPercent error!!! " + e);
		}
		return "0.0";
	}

	/**
	 * 获取内存百分比
	 * 
	 * @jason:全新方式采集
	 * @param si
	 * @param cluster
	 * @return
	 * @throws RemoteException
	 * @throws RuntimeFault
	 * @throws InvalidProperty
	 */
	public String getMEMPercent(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		Double doubleUsedMem = Double.parseDouble(getUsedMEM(si, cluster, propMap));
		Double doubleTotalMem = Double.parseDouble(getTotalMEM(si, cluster, propMap));
		String strMEMPercent = "0.0";
		try {
			if (doubleUsedMem != null && doubleTotalMem != null) {
				strMEMPercent = convert2DecimalString(doubleUsedMem * 100 / doubleTotalMem / 1024);
			}
			return strMEMPercent;
		} catch (Exception e) {
			LOGGER.error("jason say: getMEMPercent error!!! " + e);
		}
		return "0.0";
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
	public String getAvailCPU(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		// Mhz
		final Number t_unReserved = (Number) propMap.get("summary.runtime.cpu.unreservedForPool");
		return t_unReserved == null ? null : t_unReserved.toString();
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
	public String getTotalMEM(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		// MB
		/* final String t_path = "summary.effectiveMemory"; */
		final String t_path = "summary.totalMemory";
		Number t_value = (Number) propMap.get(t_path);
		if (t_value != null) {
			final String r = convert2DecimalString(t_value.doubleValue() / C_1024 / C_1024 / C_1024);
			return r.toString();
		} else {
			return null;
		}
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
	public String getUsedMEM(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		// bytes
		final Number t_r = (Number) propMap.get("summary.runtime.memory.overallUsage");
		if (t_r != null) {

			final String t_s = convert2DecimalString(t_r.doubleValue() / C_1024 / C_1024);
			return t_s;
		} else {
			return null;
		}
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
	public String getAvailMEM(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		// MB
		final Number t_unReserved = (Number) propMap.get("summary.runtime.memory.unreservedForPool");
		if (t_unReserved != null) {
			final String t_s = convert2DecimalString(t_unReserved.doubleValue() / C_1024 / C_1024);
			return t_s;
		} else {
			return null;
		}
	}

	// instance metric
	/**
	 * {method description}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return String
	 */
	public String getResDisplayName(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) {
		final String t_path = "name";
		String t_value = (String) propMap.get(t_path);
		return t_value;
	}

	// 解决7296:cpu使用率情况和已消耗内存 没有采集到的问题

	/**
	 * {method description}. 获取cpu使用情况
	 * 
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String
	 * @throws RemoteException
	 * @throws RuntimeFault
	 * @throws InvalidProperty
	 * @author Terrans-Force-X911
	 */
	public String getCPUAverage(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) throws InvalidProperty, RuntimeFault, RemoteException {
		// final String t_counter = "cpu.usagemhz.average";
		ClusterComputeResourceSummary summary = (ClusterComputeResourceSummary) propMap.get("summary");
		// HostSystem hs[] = cluster.getHosts();//为什么要根据host循环啊？？因为性能有问题，暂时注释。
		int cPUAverage = 0;
		// for (int i = 0; i < hs.length; i++) {
		if (summary != null) {
			cPUAverage = summary.getEffectiveCpu();// cpu使用率(情况)
		}
		return convert2DecimalString(cPUAverage);
	}

	/**
	 * 获取 已经消耗内存 解决8189
	 * 
	 * @param si
	 * @param cluster
	 * @return
	 * @throws InvalidProperty
	 * @throws RuntimeFault
	 * @throws RemoteException
	 */
	public String getMEMConsume(final ServiceInstance si, final ClusterComputeResource cluster,
			Map<String, Object> propMap) throws InvalidProperty, RuntimeFault, RemoteException {
		ClusterComputeResourceSummary summary = (ClusterComputeResourceSummary) propMap.get("summary");
		if (summary != null) {
			double effClusterMem = summary.getEffectiveMemory() / C_1024;
			return convert2DecimalString(effClusterMem);
		} else {
			return null;
		}
	}

	/**
	 * getHostList.
	 * 
	 * @param JBrokerParameter
	 *            parameter
	 * @return String
	 */

	@SuppressWarnings("unchecked")
	public String getHostList(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		if (null != si) {
			ManagedEntity[] t_dss = getClusterComputeResources(si);
			if (t_dss != null && t_dss.length > 0) {
				Map<String, Object>[] storeProps = null;
				try {
					storeProps = PropertyCollectorUtil.retrieveProperties(t_dss, t_dss[0].getMOR().getType(),
							new String[] { "host", "name" });
				} catch (RemoteException e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("getHostList", e);
					}
				}
				if (storeProps != null) {
					ManagedEntity[] t_hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);
					Map<String, HostListSummary>[] uuids = null;
					try {
						uuids = PropertyCollectorUtil.retrieveProperties(t_hss, t_hss[0].getMOR().getType(),
								new String[] { "summary" });
					} catch (RemoteException e) {
						if (LOGGER.isErrorEnabled()) {
							LOGGER.error("getVMList", e);
						}
					}
					Map<String, String> uuidRefMap = new HashMap<>(t_hss.length);
					for (int i = 0; i < t_hss.length; i++) {
						uuidRefMap.put(t_hss[i].getMOR().getVal(), uuids[i].get("summary").getHardware().getUuid());
					}
					for (int i = 0; i < storeProps.length; i++) {
						if (storeProps[i].get("host") instanceof DatastoreHostMount[]) {
							DatastoreHostMount[] hosts = (DatastoreHostMount[]) storeProps[i].get("host");
							String name = (String) storeProps[i].get("name");
							String uuid = VSphereUtil.createUuid(name);
							sb.append('<').append(uuid).append('>');
							if (hosts != null && hosts.length > 0) {
								sb.append(uuidRefMap.get(hosts[0].getKey().getVal()));
								for (int j = 1; j < hosts.length; j++) {
									sb.append(',').append(uuidRefMap.get(hosts[j].getKey().getVal()));
								}
							}
							sb.append('<').append('/').append(uuid).append('>');
						}
					}
				}
			}
		}
		return sb.toString();
	}
}
