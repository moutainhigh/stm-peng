package com.mainsteam.stm.plugin.vmware.collector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.vmware.constants.VMWareDatastoreConstants;
import com.mainsteam.stm.plugin.vmware.util.VSphereUtil;
import com.mainsteam.stm.pluginsession.parameter.JBrokerParameter;
import com.vmware.vim25.DatastoreHostMount;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.util.PropertyCollectorUtil;

public class VMWareDatastoreCollector extends VMWareBaseCollector {
	private static final Log LOGGER = LogFactory.getLog(VMWareDatastoreCollector.class);
	@SuppressWarnings("unused")
	private static final String EXCEP_AVAIL_DS = "EXCEP_AVAIL_DS:";
	@SuppressWarnings("unused")
	private static final String SUMMARY_ACCESSIBLE = "summary.accessible";
	private static final int C_INTERVAL_20 = 20;

	private static final String[] C_PERFCOUNTER = new String[] { "datastore.datastoreIops.average",
			"datastore.sizeNormalizedDatastoreLatency.average", };
	private static final String[] PROPTITLE = new String[] {
			// VMWareDatastoreConstants.C_HOSTLIST,
			// VMWareClusterConstants.C_VMLIST,

			VMWareDatastoreConstants.C_DATASTORAGEALLOCATEDSPACE, VMWareDatastoreConstants.C_DATASTORAGEUSEDSPACE,
			VMWareDatastoreConstants.C_DATASTORAGEVOLUME, VMWareDatastoreConstants.C_DATASTORAGEFREESPACE,
			VMWareDatastoreConstants.C_LOCATION, VMWareDatastoreConstants.C_TYPE,
			VMWareDatastoreConstants.C_NUMBEROFHOST, VMWareDatastoreConstants.C_VMANDTEMPLATES,
			VMWareDatastoreConstants.C_NAME, VMWareDatastoreConstants.C_DISKUSAGEPERCENTAGE,
			VMWareDatastoreConstants.C_DISKCONFIGUREDPERCENTAGE, VMWareDatastoreConstants.C_DATASTORAGEALERTSTATUS };
	private static final String[] PROP_TITLE_FIELDS = { "summary", "overallStatus", "name", "host" };
	private static final String[] C_PERFTITLE = new String[] { VMWareDatastoreConstants.C_SUMMARIZEDIOPS,
			VMWareDatastoreConstants.C_NORMALIZEDLATENCY };

	public static void main(String[] args) throws Exception {
		JBrokerParameter parameter = new JBrokerParameter();
		parameter.setIp("192.168.1.235");
		parameter.setUsername("administrator");
		parameter.setPassword("root");

		URL url = new URL("https", parameter.getIp(), "/sdk");
		ServiceInstance serviceInstance = new ServiceInstance(url, parameter.getUsername(), parameter.getPassword(),
				true);
		parameter.setConnection(serviceInstance);
		parameter.setUuid("50291fd6-1c0efca0-f54a-008cfa0c6a49");

		VMWareDatastoreCollector esxColl = new VMWareDatastoreCollector();
		System.out.println("prop:");
		String str2 = esxColl.getPropValue(parameter);
		System.out.println(str2);
		System.out.println("perf:");
		String str3 = esxColl.getPerfValue(parameter);
		System.out.println(str3);

		new VMWareDatastoreCollector().getAvailability(parameter);
	}

	@SuppressWarnings("unchecked")
	public String getPropValue(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		long temp = System.currentTimeMillis();
		final ManagedEntity[] t_dss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
		if (null != t_dss) {
			StringBuffer sb = new StringBuffer();
			Map<String, Object>[] dsPropValues = null;
			try {
				dsPropValues = PropertyCollectorUtil.retrieveProperties(t_dss, t_dss[0].getMOR().getType(),
						PROP_TITLE_FIELDS);
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPropValue0", e);
				}
			}
			if (dsPropValues == null || dsPropValues.length <= 0) {
				return null;
			}
			for (int k = 0; k < dsPropValues.length; k++) {
				Datastore t_ds = (Datastore) t_dss[k];
				Map<String, Object> propValues = new HashMap<>(dsPropValues[k]);
				for (Iterator<Entry<String, Object>> iterator = propValues.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, Object> entry = iterator.next();
					if (entry.getValue() == PropertyCollectorUtil.NULL) {
						entry.setValue(null);
					}
				}
				if (!(dsPropValues[k].get("summary") instanceof DatastoreSummary)) {
					continue;
				}
				String dsUrl = ((DatastoreSummary) dsPropValues[k].get("summary")).getUrl();
				String uuid = VSphereUtil.createDatastoreUUID(dsUrl);
//				String uuid = VSphereUtil.getDatastoreUUID(t_ds);
				sb.append("<" + uuid + ">");
				for (int i = 0; i < PROPTITLE.length; i++) {
					final String methodName = "get" + PROPTITLE[i];
					Method t_method;
					try {
						t_method = VMWareDatastoreCollector.class.getMethod(methodName, ServiceInstance.class,
								Datastore.class, Map.class);
						String t_dsName = "";
						if (null != t_ds) {
							t_dsName = t_ds.getName();
						}
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("[Debug Datastore(" + t_dsName + ")]  " + methodName + " come on ");
						}
						String result = (String) t_method.invoke(this, si, t_ds, propValues);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("[Debug Datastore(" + t_dsName + ")]  " + methodName + " result = " + result);
						}
						sb.append("<").append(PROPTITLE[i]).append(">").append(result).append("</").append(PROPTITLE[i])
								.append(">");
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
				sb.append("</" + uuid + ">");
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
		// final String uuid = parameter.getUuid();
		if (null != si) {
			StringBuilder sb = new StringBuilder();
			// initDatastoreUuidMap(si);
			// final Datastore t_ds = new Datastore(si.getServerConnection(),
			// C_DATASTOREMAP.get(uuid));
			// Datastore t_ds = VSphereUtil.findDatastoreByUuid(si, uuid);
			long temp = System.currentTimeMillis();
			final ManagedEntity[] t_dss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
			if (t_dss == null || t_dss.length <= 0) {
				return null;
			}
			final ManagedEntity[] t_hss = VSphereUtil.find(si, VSphereUtil.TYPE_HOST_SYSTEM);
			if (t_hss == null || t_hss.length <= 0) {
				return null;
			}
			// final HostSystem[] t_hss = getHostInDatastore(si, t_ds);
			Map<String, Object>[] storeProps;
			try {
				storeProps = PropertyCollectorUtil.retrieveProperties(t_dss, t_dss[0].getMOR().getType(),
						new String[] { "host", "summary" });
			} catch (RemoteException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPerfValue", e);
				}
				return "";
			}
			List<Map<String, long[]>>[] storeValuesList = new List[t_dss.length];
			try {
				final List<ManagedEntity> t_hsList = Arrays.asList(t_hss);
				final Map<String, HashMap<String, long[]>> t_all = getDatastorePerfDataForMultiEntity(si, t_hsList,
						C_PERFCOUNTER, false, C_INTERVAL_20);
				for (int i = 0; i < storeProps.length; i++) {
					Map<String, Object> hostMap = storeProps[i];
					if (hostMap.get("host") instanceof DatastoreHostMount[]) {
						DatastoreHostMount[] mts = (DatastoreHostMount[]) hostMap.get("host");
						if (mts != null) {
							List<Map<String, long[]>> storeValues = new ArrayList<>(mts.length);
							for (DatastoreHostMount datastoreHostMount : mts) {
								HashMap<String, long[]> value = t_all.get(datastoreHostMount.key.getVal());
								if (value != null) {
									storeValues.add(value);
								}
							}
							storeValuesList[i] = storeValues;
						}
					}
				}
			} catch (Exception e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("getPerfValue", e);
				}
				return "";
			}
			final Class<? extends VMWareDatastoreCollector> t_cl = this.getClass();
			for (int i = 0; i < t_dss.length; i++) {
				if (!(storeProps[i].get("summary") instanceof DatastoreSummary)) {
					continue;
				}
				String dsUrl = ((DatastoreSummary) storeProps[i].get("summary")).getUrl();
				String uuid = VSphereUtil.createDatastoreUUID(dsUrl);
				List<Map<String, long[]>> storeValues = storeValuesList[i];
				sb.append('<').append(uuid).append('>');
				for (int t_i = 0; t_i < C_PERFTITLE.length; t_i++) {
					final String t_name = "get" + C_PERFTITLE[t_i];
					Method method = null;
					try {
						method = t_cl.getMethod(t_name, List.class);
						String result = (String) method.invoke(this, storeValues);
						sb.append("<").append(t_name).append(">").append(result).append("</").append(t_name)
								.append(">");
					} catch (IllegalAccessException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (IllegalArgumentException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (NoSuchMethodException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (SecurityException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (Throwable e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
				sb.append('<').append('/').append(uuid).append('>');
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getPerfValue Current ResultSet is [" + sb.toString() + "]. ip is " + parameter.getIp()
						+ " lossTime=" + (System.currentTimeMillis() - temp));
			}
			return sb.toString();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getAvailability(JBrokerParameter parameter) {
		StringBuilder sb = new StringBuilder();
		try {
			ServiceInstance si = (ServiceInstance) parameter.getConnection();
			if (null != si) {
				// initDatastoreUuidMap(si);
				// Datastore t_ds = new Datastore(si.getServerConnection(),
				// (ManagedObjectReference)C_DATASTOREMAP.get(uuid));
				ManagedEntity[] t_dss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
				if (t_dss != null && t_dss.length > 0) {
					Map<String, DatastoreSummary>[] storeProps = PropertyCollectorUtil.retrieveProperties(t_dss,
							t_dss[0].getMOR().getType(), new String[] { "summary" });
					for (int i = 0; i < storeProps.length; i++) {
						DatastoreSummary summary = storeProps[i].get("summary");
						String uuid = VSphereUtil.createDatastoreUUID(summary.getUrl());
						sb.append('<').append(uuid).append('>');
						Boolean isVail = summary.isAccessible();
						sb.append(isVail.booleanValue() ? 1 : 0);
						sb.append('<').append('/').append(uuid).append('>');
					}
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public String getVMList(JBrokerParameter parameter) {
		StringBuilder sb = new StringBuilder();
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		if (null != si) {
			ManagedEntity[] t_dss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
			if (t_dss != null && t_dss.length > 0) {
				Map<String, Object>[] storeProps = null;
				try {
					storeProps = PropertyCollectorUtil.retrieveProperties(t_dss, t_dss[0].getMOR().getType(),
							new String[] { "vm", "summary" });
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
						if (storeProps[i].get("vm") instanceof ManagedObjectReference[]) {
							ManagedObjectReference[] tVMs = (ManagedObjectReference[]) storeProps[i].get("vm");
							DatastoreSummary summary = (DatastoreSummary) storeProps[i].get("summary");
							String uuid = VSphereUtil.createDatastoreUUID(summary.getUrl());
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
			}
		}
		return sb.toString();
	}

	public String getDataStorageVolume(final ServiceInstance si, final Datastore ds, Map<String, Object> propValues) {
		final String t_path = "summary.capacity";
		DatastoreSummary summary = (DatastoreSummary) propValues.get("summary");
		String t_value = "";
		if (null != summary) {
			long t_cap = (Long) summary.getCapacity();
			double t_valueOf = (double) t_cap;
			t_value = convert2DecimalString(t_valueOf / C_1024 / C_1024 / C_1024);
		}
		return t_value;
	}

	public String getDataStorageUsedSpace(final ServiceInstance si, final Datastore ds,
			Map<String, Object> propValues) {
		final String t_path = "summary.capacity";
		final String t_path2 = "summary.freeSpace";
		DatastoreSummary summary = (DatastoreSummary) propValues.get("summary");
		String t_value = "";
		if (null != summary) {
			final long t_cap = summary.getCapacity();
			final long t_free = summary.getFreeSpace();
			final long t_used = t_cap - t_free;
			t_value = convert2DecimalString(((double) t_used) / C_1024 / C_1024 / C_1024);
		}
		return t_value;
	}

	public String getDataStorageFreeSpace(final ServiceInstance si, final Datastore ds,
			Map<String, Object> propValues) {
		final String t_path2 = "summary.freeSpace";
		DatastoreSummary summary = (DatastoreSummary) propValues.get("summary");
		String t_value = "";
		if (null != summary) {
			t_value = convert2DecimalString(((double) summary.getFreeSpace()) / C_1024 / C_1024 / C_1024);
		}
		return t_value;
	}

	public String getDataStorageAllocatedSpace(final ServiceInstance si, final Datastore ds,
			Map<String, Object> propValues) {
		final String t_path = "summary.capacity";
		final String t_path2 = "summary.freeSpace";
		final String t_path3 = "summary.uncommitted";
		DatastoreSummary summary = (DatastoreSummary) propValues.get("summary");

		String t_value = "";
		if (null != summary) {
			final long t_cap = summary.getCapacity();
			final long t_free = summary.getFreeSpace();
			final long t_unCom = summary.getUncommitted() == null ? 0 : summary.getUncommitted().longValue();
			final long t_allocated = t_cap - t_free + t_unCom;
			t_value = convert2DecimalString(((double) t_allocated) / C_1024 / C_1024 / C_1024);
		}
		return t_value;
	}

	public String getLocation(final ServiceInstance si, final Datastore ds, Map<String, Object> propValues) {
		final String t_path = "summary.url";
		DatastoreSummary summary = (DatastoreSummary) propValues.get("summary");
		String t_r = null;
		if (null != summary) {
			t_r = summary.getUrl();
		}
		return t_r;
	}

	public String getType(final ServiceInstance si, final Datastore ds, Map<String, Object> propValues) {
		final String t_path = "summary.type";
		DatastoreSummary summary = (DatastoreSummary) propValues.get("summary");
		String t_r = null;
		if (null != summary) {
			t_r = summary.getType();
		}
		return t_r;
	}

	public String getNumberOfHost(final ServiceInstance si, final Datastore ds, Map<String, Object> propValues) {
		final String t_path = "host";
		final DatastoreHostMount[] t_r = (DatastoreHostMount[]) propValues.get(t_path);
		if (t_r == null) {
			return "0";
		}
		return String.valueOf(t_r.length);
	}

	public String getVMandTemplates(final ServiceInstance si, final Datastore ds, Map<String, Object> propValues) {
		final String t_path = "vm";
		String t_result = "";
		final Object t_vms = propValues.get(t_path);
		ManagedObjectReference[] t_r = new ManagedObjectReference[0];
		if (t_vms != null) {
			try {
				t_r = ManagedObjectReference[].class.cast(t_vms);
			} catch (final Exception t_e) {
				LOGGER.error("The datastore has no VirtualMachine");
			}
		}
		if (null != t_r) {
			t_result = String.valueOf(t_r.length);
		}
		return t_result;
	}

	public String getName(final ServiceInstance si, final Datastore ds, Map<String, Object> propValues) {
		final String t_path = "name";
		final String t_r = (String) propValues.get(t_path);
		if (null == t_r) {
			return "";
		}
		return t_r;
	}

	private HostSystem[] getHostInDatastore(final ServiceInstance si, final Datastore ds) {
		if (null != ds) {
			final DatastoreHostMount[] t_dshms = (DatastoreHostMount[]) ds.getPropertyByPath("host");
			final HostSystem[] t_hss = new HostSystem[t_dshms.length];
			for (int t_i = 0; t_i < t_dshms.length; t_i++) {
				final ManagedObjectReference t_mor = t_dshms[t_i].getKey();
				t_hss[t_i] = new HostSystem(si.getServerConnection(), t_mor);
			}
			return t_hss;
		}
		return new HostSystem[0];
	}

	@SuppressWarnings("unchecked")
	public String getHostList(JBrokerParameter parameter) {
		ServiceInstance si = (ServiceInstance) parameter.getConnection();
		StringBuilder sb = new StringBuilder();
		if (null != si) {

			ManagedEntity[] t_dss = VSphereUtil.find(si, VSphereUtil.TYPE_DATASTORE);
			if (t_dss != null && t_dss.length > 0) {
				Map<String, Object>[] storeProps = null;
				try {
					storeProps = PropertyCollectorUtil.retrieveProperties(t_dss, t_dss[0].getMOR().getType(),
							new String[] { "host", "summary" });
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
							DatastoreSummary summary = (DatastoreSummary) storeProps[i].get("summary");
							String uuid = VSphereUtil.createDatastoreUUID(summary.getUrl());
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

	@SuppressWarnings("unused")
	private String[] getDatastorePerfMatricValue(final String[] titles, final List<HashMap<String, long[]>> list) {
		final Class<? extends VMWareDatastoreCollector> t_cl = this.getClass();
		final String[] t_values = new String[titles.length];
		for (int t_i = 0; t_i < titles.length; t_i++) {
			final String t_name = "get" + titles[t_i];
			Method t_method = null;
			try {
				t_method = t_cl.getMethod(t_name, List.class);
				t_values[t_i] = (String) t_method.invoke(this, list);
			} catch (final Exception t_e) {
				LOGGER.error("getDatastorePerfMatricValue: methodName = " + t_name, t_e);
			}
		}
		return t_values;
	}

	private Map<String, HashMap<String, long[]>> getDatastorePerfDataForMultiEntity(final ServiceInstance si,
			final List<? extends ManagedEntity> meList, final String[] counters, final boolean isInstance,
			final int interval) {
		if (null == si || null == counters || counters.length <= 0) {
			return new HashMap<String, HashMap<String, long[]>>();
		}
		final ManagedEntity[] t_mes = meList.toArray(new ManagedEntity[0]);
		final Map<String, HashMap<String, long[]>> t_all = new HashMap<>();
		final PerfQuerySpec[] t_pqss = new PerfQuerySpec[t_mes.length];
		for (int t_i = 0; t_i < t_pqss.length; t_i++) {
			t_pqss[t_i] = buildPerfQuerySpecForMultiCounter(si, counters, isInstance, t_mes[t_i], interval);
		}
		final PerfEntityMetric[] t_pems = getPerfMetricSeriesForMultiSpec(si, t_pqss);
		if (null != t_pems && t_pems.length > 0) {
			if (t_pems.length != t_mes.length) {
				LOGGER.error("Some entity is not available, Query " + t_mes.length + " entities, but return "
						+ t_pems.length + " entities");
			}
			for (int t_i = 0; t_i < t_pems.length; t_i++) {
				final HashMap<String, long[]> t_map = new HashMap<String, long[]>();
				final PerfMetricSeries[] t_pmss = t_pems[t_i].getValue();
				for (int t_j = 0; t_j < t_pmss.length; t_j++) {
					final PerfMetricIntSeries t_pmis = (PerfMetricIntSeries) t_pmss[t_j];
					final int t_id = t_pmis.getId().getCounterId();
					t_map.put(getCounterNameById(t_id, si), t_pmis.getValue());
				}
				t_all.put(t_pems[t_i].getEntity().getVal(), t_map);
			}
		}
		return t_all;
	}

	public String getSummarizedIOPS(final List<HashMap<String, long[]>> list) {
		final String t_counter = "datastore.datastoreIops.average";
		long t_l = 0L;
		if (null != list) {
			for (final HashMap<String, long[]> t_map : list) {
				t_l += t_map.get(t_counter)[0];
			}
		}
		return String.valueOf(t_l);
	}

	public String getNormalizedLatency(final List<HashMap<String, long[]>> list) {
		final String t_counter = "datastore.sizeNormalizedDatastoreLatency.average";
		long t_l = 0L;
		if (null != list) {
			for (final HashMap<String, long[]> t_map : list) {
				t_l += t_map.get(t_counter)[0];
			}
		}
		return String.valueOf(t_l);
	}

	public String getDiskUsagePercentage(final ServiceInstance si, final Datastore ds, Map<String, Object> propValues) {
		final String t_path = "summary.capacity";
		final String t_path2 = "summary.freeSpace";
		DatastoreSummary summary = (DatastoreSummary) propValues.get("summary");
		String t_value = "";
		if (summary != null) {
			final long t_cap = summary.getCapacity();
			final long t_free = summary.getFreeSpace();
			final long t_used = t_cap - t_free;
			t_value = convert2DecimalString(t_used / ((double) t_cap) * C_100);
		}
		return t_value;
	}

	public String getDiskConfiguredPercentage(final ServiceInstance si, final Datastore ds,
			Map<String, Object> propValues) {
		final String t_path = "summary.capacity";
		final String t_path2 = "summary.freeSpace";
		final String t_path3 = "summary.uncommitted";
		final DatastoreSummary summary = (DatastoreSummary) propValues.get("summary");
		String t_value = "";
		if (null != summary) {
			final long t_cap = summary.getCapacity();
			final long t_free = summary.getFreeSpace();
			final long t_unCom = summary.getUncommitted() == null ? 0 : summary.getUncommitted().longValue();
			final long t_configured = t_cap - t_free + t_unCom;
			t_value = convert2DecimalString(((double) t_configured) * 0.1 / C_1024 / C_1024 / C_1024);
		}
		return t_value;
	}

	public String getDataStorageAlertStatus(final ServiceInstance si, final Datastore ds,
			Map<String, Object> propValues) {
		final String t_path = "overallStatus";
		final ManagedEntityStatus t_mes = (ManagedEntityStatus) propValues.get(t_path);
		return t_mes.toString();
	}
}
