package com.mainsteam.stm.plugin.vmware.collector;

import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.vmware.constants.CommonConstants;
import com.mainsteam.stm.plugin.vmware.constants.VMWareVMConstants;
import com.mainsteam.stm.plugin.vmware.util.VSphereUtil;
import com.mainsteam.stm.plugin.vmware.vo.Metric;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PhysicalNic;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ManagedObject;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.PropertyCollectorUtil;

/**
 * 2015-04-1 It's a funny day
 * 
 * @author Xiaopf
 *
 */
public class VMWareBaseCollector {
	static final String SUMMARY_CONFIG_INSTANCE_UUID = "summary.config.instanceUuid";

	/**
	 * <code>C_LOGGER</code> - {description}.
	 */
	private static final Log C_LOGGER = LogFactory
			.getLog(VMWareBaseCollector.class);
	/**
	 * <code>C_VIRTUALPORTGROUP</code> - {description}.
	 */
	static final String C_VIRTUALPORTGROUP = "VirtualPortGroup";
	/**
	 * <code>C_PHYSICALNIC</code> - {description}.
	 */
	static final String C_PHYSICALNIC = "PhysicalNic";

	/**
	 * <code>C_DISTRIBUTEDVIRTUALPORTGROUP</code> - {description}.
	 */
	static final String C_DISTRIBUTEDVIRTUALPORTGROUP = "DistributedVirtualPortgroup";
	/**
	 * <code>C_UPLINKPORTGROUP</code> - {description}.
	 */
	static final String C_UPLINKPORTGROUP = "UpLinkPortgroup";
	/**
	 * <code>C_DATACENTER</code> - {description}.
	 */
	public static final String C_DATACENTER = "Datacenter";

	public static final String C_VCENTER = "VCenter";
	/**
	 * <code>C_HOST</code> - {description}.
	 */
    public static final String C_HOST = "HostSystem";

    public static final String C_ESXSTORAGEADAPTER = "EsxStorageAdapter";

    public static final String C_ESXNETWORKADAPTER = "EsxNetWorkAdapter";
	/**
	 * <code>C_NETWORK</code> - {description}.
	 */
	static final String C_NETWORK = "Network";
	/**
	 * <code>C_CLUSTER</code> - {description}.
	 */
	public static final String C_CLUSTER = "ClusterComputeResource";
	/**
	 * <code>C_DATASTORE</code> - {description}.
	 */
	public static final String C_DATASTORE = "Datastore";
	/**
	 * <code>C_VM</code> - {description}.
	 */
	public static final String C_VM = "VirtualMachine";

	   public static final String C_RESOURCE = "ResourcePool";
	   
	static final String C_FOLDER = "Folder";

	public static final String C_NET_INTERFACE = "NetInterface";

	public static final String C_CPU = "CPU";

	/**
	 * <code>C_100</code> - {description}.
	 */
	static final int C_100 = 100;
	/**
	 * <code>C_1024</code> - {description}.
	 */
	static final int C_1024 = 1024;
	/**
	 * <code>C_1000</code> - {description}.
	 */
	static final int C_1000 = 1000;

	static final String SUMMARY_HARDWARE_UUID = "summary.hardware.uuid";

	static final Map<String, ManagedObjectReference> C_DATASTOREMAP = new ConcurrentHashMap<String, ManagedObjectReference>();
	/**
	 * <code>C_VSWITCHSTOREMAP</code> - {Map of Datastore references indexed by
	 * UUID}.
	 */
	static final Map<String, HostVirtualSwitch> C_VSWITCHMAP = new ConcurrentHashMap<String, HostVirtualSwitch>();
	/**
	 * <code>C_CLUSTERMAP</code> - {Map of ClusterComputerResource references
	 * indexed by UUID}.
	 */
	static final Map<String, ManagedObjectReference> C_CLUSTERMAP = new ConcurrentHashMap<String, ManagedObjectReference>();
	/**
	 * <code>C_CLUSTERINITIALIZED</code> - {Flag used to indicate whether the
	 * C_CLUSTERMAP is initialized or not}.
	 */
	private static boolean s_clusterInitialized = Boolean.FALSE;
	/**
	 * <code>C_VMINITIALIZED</code> - {Flag used to indicate whether the C_VMMAP
	 * is initialized or not}.
	 */
	// private static boolean s_vmInitialized = Boolean.FALSE;
	/**
	 * <code>C_VMINITIALIZED</code> - {Flag used to indicate whether the C_VMMAP
	 * is initialized or not}.
	 */
	private static boolean s_vsInitialized = Boolean.FALSE;

	/**
	 * {Get the first Datacenter from VCenter}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @return Datacenter
	 */
	Datacenter getDatacenter(final ServiceInstance si) {
		final ManagedEntity[] t_mes = getManagedEntity(si, si.getRootFolder(),
				C_DATACENTER);
		if (null != t_mes[0]) {
			return (Datacenter) t_mes[0];
		}
		return null;
	}

	/**
	 * {Get All Datastores from VCenter, need be converted to Datastore}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @return ManagedEntity[]
	 */
	ManagedEntity[] getDatastores(final ServiceInstance si) {
		return getManagedEntity(si, si.getRootFolder(), C_DATASTORE);
	}

	/**
	 * {Get All Datastores from VCenter, need be converted to Datastore}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @return ManagedEntity[]
	 */
	ManagedEntity[] getVSwitch(final ServiceInstance si) {
		return getManagedEntity(si, si.getRootFolder(), "HostVirtualSwitch");
	}

	/**
	 * {Get All VirtualMachines from VCenter, need be converted to
	 * VirtualMachine}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @return ManagedEntity[]
	 */
	ManagedEntity[] getVirtualMachines(final ServiceInstance si) {
		return getManagedEntity(si, si.getRootFolder(), C_VM);
	}

	/**
	 * {Get All HostSystems from VCenter, need be converted to HostSystem}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @return ManagedEntity[]
	 */
	ManagedEntity[] getHostSystems(final ServiceInstance si) {
		return getManagedEntity(si, si.getRootFolder(), C_HOST);
	}

	/**
	 * {Get All ClusterComputeResources from VCenter, need be converted to
	 * ClusterComputeResource}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @return ManagedEntity[]
	 */
	ManagedEntity[] getClusterComputeResources(final ServiceInstance si) {
		return getManagedEntity(si, si.getRootFolder(), C_CLUSTER);
	}

	/**
	 * {Get all ManagedEntities for specified type from the current entity}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param entity
	 *            ManagedEntity
	 * @param type
	 *            String
	 * @return ManagedEntity[]
	 */
	ManagedEntity[] getManagedEntity(final ServiceInstance si,
			final ManagedEntity entity, final String type) {
		ManagedEntity[] t_mes = new ManagedEntity[0];
		try {
			t_mes = new InventoryNavigator(entity).searchManagedEntities(type);
		} catch (final RemoteException t_e) {
			C_LOGGER.error("RemoteException ", t_e);
		}
		return t_mes;
	}

	/**
	 * {Get counter id by counter name}.
	 * 
	 * @param name
	 *            String
	 * @param si
	 *            ServiceInstance
	 * @return int
	 */
	int getCounterIdByName(final String name, final ServiceInstance si) {
		Integer t_id = VSphereUtil.getCounter(si, name);
		if (null == t_id) {
			C_LOGGER.error(name + " Counter not found.");
			return -1;
		}
		return t_id.intValue();
	}

	/**
	 * {Get counter name by counter id}.
	 * 
	 * @param id
	 *            int
	 * @param si
	 *            ServiceInstance
	 * @return String
	 */
	String getCounterNameById(final int id, final ServiceInstance si) {
		String t_name =VSphereUtil.getCounterName(si, id);
		if (null == t_name) {
			C_LOGGER.error(id + " Counter not found.");
		}
		return t_name;
	}

	/**
	 * {Build a PerfQuerySpec of Multiply counters for a ManagedEntity.}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param counters
	 *            String[]
	 * @param isInstance
	 *            boolean
	 * @param entity
	 *            ManagedEntity
	 * @param interval
	 *            int
	 * @return PerfQuerySpec
	 */
	PerfQuerySpec buildPerfQuerySpecForMultiCounter(final ServiceInstance si,
			final String[] counters, final boolean isInstance,
			final ManagedEntity entity, final int interval) {
		if (null == counters || counters.length <= 0) {
			return new PerfQuerySpec();
		}
		final PerfQuerySpec t_qs = new PerfQuerySpec();
		t_qs.setEntity(entity.getMOR());
		final PerfMetricId[] t_pmis = new PerfMetricId[counters.length];
		for (int t_i = 0; t_i < t_pmis.length; t_i++) {
			final PerfMetricId t_pmi = new PerfMetricId();
			final int t_id = getCounterIdByName(counters[t_i], si);
			t_pmi.setCounterId(t_id);
			if (isInstance) {
				t_pmi.setInstance("*");
			} else {
				t_pmi.setInstance("");
			}
			t_pmis[t_i] = t_pmi;
		}
		t_qs.setMetricId(t_pmis);
		t_qs.setFormat("normal");
		// if we need a historical data, set interval to 300 second (5min)
		t_qs.setIntervalId(interval);
		t_qs.setMaxSample(1);
		return t_qs;
	}

	/**
	 * {Get the performance results for multiply PerfQuerySpec Each
	 * PerfQuerySpec has a PerfEntityMetric, so the length of PerfEntityMetric[]
	 * is equal to PerfQuerySpec[] in the normal. }.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param pqss
	 *            PerfQuerySpec[]
	 * @return PerfEntityMetric[]
	 */
	PerfEntityMetric[] getPerfMetricSeriesForMultiSpec(
			final ServiceInstance si, final PerfQuerySpec[] pqss) {
		if (null == pqss || pqss.length <= 0) {
			return new PerfEntityMetric[0];
		}
		PerfEntityMetric[] t_pems = new PerfEntityMetric[0];
		final PerformanceManager t_pm = si.getPerformanceManager();
		PerfEntityMetricBase[] t_pembs = new PerfEntityMetricBase[0];
		try {
			t_pembs = t_pm.queryPerf(pqss);
		} catch (final RemoteException t_e) {
			C_LOGGER.error("RemoteException "+net.sf.json.JSONArray.fromObject(pqss).toString(), t_e);
		}
		if (null != t_pembs && t_pembs.length > 0) {
			t_pems = new PerfEntityMetric[t_pembs.length];
			for (int t_i = 0; t_i < t_pems.length; t_i++) {
				t_pems[t_i] = (PerfEntityMetric) t_pembs[t_i];
			}
		}
		return t_pems;
	}

	PhysicalNic[] getPhysicalNics(final HostSystem hs) {
		PhysicalNic[] t_pnics = new PhysicalNic[0];
		if (null != hs) {
			t_pnics = (PhysicalNic[]) hs
					.getPropertyByPath("config.network.pnic");
		}
		return t_pnics;
	}

	PerfMetricId[] getAvailPerfMetric(final ManagedEntity entity,
			final int interval, final ServiceInstance si) {
		PerfMetricId[] t_pmis = null;
		final PerformanceManager t_pm = si.getPerformanceManager();
		final Calendar t_cl = Calendar.getInstance();
		t_cl.set(Calendar.HOUR_OF_DAY, 0);
		try {
			t_pmis = t_pm.queryAvailablePerfMetric(entity, t_cl,
					Calendar.getInstance(), interval);
		} catch (final RemoteException t_e) {
			C_LOGGER.error("RemoteException ", t_e);
		}
		return t_pmis;
	}

	/**
	 * {Convert double to string, the formatter is "0.00"}.
	 * 
	 * @param value
	 *            double
	 * @return String
	 */
	String convert2DecimalString(final double value) {
		DecimalFormat t_decimalFormat = new DecimalFormat("0.00");
		t_decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		return t_decimalFormat.format(value);
	}

	/**
	 * {Get the performance data for multiply entities. It is used for main
	 * resource task. Map<String, HashMap<String, long[]>> The first map
	 * contains uuid/Mors pairs The second map contains counter name/value pairs
	 * }.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param uuidMap
	 *            Map<String, ManagedObjectReference>
	 * @param counters
	 *            String[]
	 * @param isInstance
	 *            boolean true for Main resource task, false for sub resource
	 *            task
	 * @param interval
	 *            int
	 * @return Map<String, HashMap<String, long[]>>
	 */
	Map<String, HashMap<String, long[]>> getPerfDataForMultiEntity(
			final ServiceInstance si,
			final Map<String, ManagedObjectReference> uuidMap,
			final String[] counters, final boolean isInstance,
			final int interval) {
		if (null == si || null == counters || counters.length <= 0) {
			return new HashMap<String, HashMap<String, long[]>>();
		}
		final Set<Entry<String, ManagedObjectReference>> t_entry = uuidMap
				.entrySet();
		final int t_size = t_entry.size();
		final String[] t_uuids = new String[t_size];
		final ManagedEntity[] t_mes = new ManagedEntity[t_size];
		setUuidAndManagedEntityArray(si, t_entry, t_mes, t_uuids);
		final Map<String, HashMap<String, long[]>> t_allMap = new HashMap<String, HashMap<String, long[]>>();
		final PerfQuerySpec[] t_pqss = new PerfQuerySpec[t_size];
		for (int t_i = 0; t_i < t_pqss.length; t_i++) {
			t_pqss[t_i] = buildPerfQuerySpecForMultiCounter(si, counters,
					isInstance, t_mes[t_i], interval);
		}
		final PerfEntityMetric[] t_pems = getPerfMetricSeriesForMultiSpec(si,
				t_pqss);
		if (null != t_pems && t_pems.length > 0) {
			if (t_pems.length != t_mes.length) {
				C_LOGGER.error("Some entity is not available, Query "
						+ t_mes.length + " entities, but return "
						+ t_pems.length + " entities");
			}
			for (int t_i = 0; t_i < t_pems.length; t_i++) {
				final HashMap<String, long[]> t_map = new HashMap<String, long[]>();
				final PerfMetricSeries[] t_pmss = t_pems[t_i].getValue();
				for (int t_j = 0; t_j < t_pmss.length; t_j++) {
					final PerfMetricIntSeries t_pmis = (PerfMetricIntSeries) t_pmss[t_j];
					final int t_id = t_pmis.getId().getCounterId();
					filterLessThanZero(t_pmis);
					t_map.put(getCounterNameById(t_id, si), t_pmis.getValue());
				}
				int t_index = -1;
				final ManagedObjectReference t_mor = t_pems[t_i].getEntity();
				if (t_mor.getType().equals(t_mes[t_i].getMOR().getType())
						&& t_mor.get_value().equals(
								t_mes[t_i].getMOR().get_value())) {
					t_index = t_i;
				} else {
					t_index = findIndex(t_mes, t_mor);
					if (t_index == -1) {
						C_LOGGER.error("Unexpected managed object in result: "
								+ t_mor.getType() + ":" + t_mor.get_value());
						return new HashMap<String, HashMap<String, long[]>>();
					}
				}
				t_allMap.put(t_uuids[t_index], t_map);
			}
		}
		return t_allMap;
	}

	/**
	 * {method description}.
	 * 
	 * @param t_pmis
	 * @return long[]
	 */
	private long[] filterLessThanZero(final PerfMetricIntSeries t_pmis) {
		// 过滤小于0的数据
		long[] t_value = t_pmis.getValue();
		if (null != t_value) {
			for (int t_i2 = 0; t_i2 < t_value.length; t_i2++) {
				if (t_value[t_i2] < 0L) {
					t_value[t_i2] = 0L;
				}
			}
		}
		return t_value;
	}

	/**
	 * {Get the performance data for multiply entities. It is used for sub
	 * resource task. Map<String, HashMap<String, HashMap<String,long[]>>> The
	 * first map contains uuid/MOR pairs The second map contains counter
	 * name/value pairs The third map contains sub instance/value pairs }.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param uuidMap
	 *            Map<String, ManagedObjectReference>
	 * @param counters
	 *            String[]
	 * @param isInstance
	 *            boolean ture for Main resource task, false for sub resource
	 *            task
	 * @param interval
	 *            int
	 * @return Map<String, HashMap<String, HashMap<String,long[]>>>
	 */
	Map<String, HashMap<String, HashMap<String, long[]>>> getSubPerfDataForMultiEntity(
			final ServiceInstance si,
			final Map<String, ManagedObjectReference> uuidMap,
			final String[] counters, final boolean isInstance,
			final int interval) {
		if (null == si || null == counters || counters.length <= 0) {
			return new HashMap<String, HashMap<String, HashMap<String, long[]>>>();
		}
		final Set<Entry<String, ManagedObjectReference>> t_entry = uuidMap
				.entrySet();
		final int t_size = t_entry.size();
		final String[] t_uuids = new String[t_size];
		final ManagedEntity[] t_mes = new ManagedEntity[t_size];
		setUuidAndManagedEntityArray(si, t_entry, t_mes, t_uuids);
		final HashMap<String, HashMap<String, HashMap<String, long[]>>> t_allMap = new HashMap<String, HashMap<String, HashMap<String, long[]>>>();
		final PerfQuerySpec[] t_pqss = new PerfQuerySpec[t_size];
		for (int t_i = 0; t_i < t_pqss.length; t_i++) {
			t_pqss[t_i] = buildPerfQuerySpecForMultiCounter(si, counters,
					isInstance, t_mes[t_i], interval);
		}
		final PerfEntityMetric[] t_pems = getPerfMetricSeriesForMultiSpec(si,
				t_pqss);
		if (null != t_pems && t_pems.length > 0) {
			if (t_pems.length != t_mes.length) {
				C_LOGGER.error("Some entity is not available, Query "
						+ t_mes.length + " entities, but return "
						+ t_pems.length + " entities");
			}
			for (int t_i = 0; t_i < t_pems.length; t_i++) {
				final HashMap<String, HashMap<String, long[]>> t_map = new HashMap<String, HashMap<String, long[]>>();
				final PerfMetricSeries[] t_pmss = t_pems[t_i].getValue();
				for (int t_j = 0; t_j < t_pmss.length; t_j++) {
					final PerfMetricIntSeries t_pmis = (PerfMetricIntSeries) t_pmss[t_j];
					final int t_id = t_pmis.getId().getCounterId();
					final String t_key = getCounterNameById(t_id, si);
					if (t_map.containsKey(t_key)) {
						t_map.get(t_key).put(t_pmis.getId().getInstance(),
								t_pmis.getValue());
					} else {
						final HashMap<String, long[]> t_map2 = new HashMap<String, long[]>();
						t_map2.put(t_pmis.getId().getInstance(),
								t_pmis.getValue());
						t_map.put(t_key, t_map2);
					}
				}
				int t_index = -1;
				final ManagedObjectReference t_mor = t_pems[t_i].getEntity();
				if (t_mor.getType().equals(t_mes[t_i].getMOR().getType())
						&& t_mor.get_value().equals(
								t_mes[t_i].getMOR().get_value())) {
					t_index = t_i;
				} else {
					t_index = findIndex(t_mes, t_mor);
					if (t_index == -1) {
						C_LOGGER.error("Unexpected managed object in result: "
								+ t_mor.getType() + ":" + t_mor.get_value());
						return new HashMap<String, HashMap<String, HashMap<String, long[]>>>();
					}
				}
				t_allMap.put(t_uuids[t_index], t_map);
			}
		}
		return t_allMap;
	}

	/**
	 * {Get the index of ManagedObjectReference in ManagedObject[]}.
	 * 
	 * @param mos
	 *            ManagedObject[]
	 * @param mor
	 *            ManagedObjectReference
	 * @return int
	 */
	private int findIndex(final ManagedObject[] mos,
			final ManagedObjectReference mor) {
		for (int t_i = 0; t_i < mos.length; t_i++) {
			if (mor.getType().equals(mos[t_i].getMOR().getType())
					&& mor.get_value().equals(mos[t_i].getMOR().get_value()))
				return t_i;
		}
		return -1;
	}

	/**
	 * {Get property data for multiply entities. It is used for both Main and
	 * Sub resource task Map<String, Hashtable<String, Object>> The first map
	 * contains uuid/mor pairs. The second map contains propPath/value pairs. }.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param uuidMap
	 *            Map<String, ManagedObjectReference>
	 * @param propPath
	 *            String[]
	 * @return Map<String, Hashtable<String, Object>>
	 */
	@SuppressWarnings("unchecked")
	Map<String, Hashtable<String, Object>> getPropDataForMultiEntity(
			final ServiceInstance si,
			final Map<String, ManagedObjectReference> uuidMap,
			final String[] propPath) {
		final Map<String, Hashtable<String, Object>> t_allMap = new HashMap<String, Hashtable<String, Object>>();
		final Set<Entry<String, ManagedObjectReference>> t_entry = uuidMap
				.entrySet();
		final int t_size = t_entry.size();
		final String[] t_uuids = new String[t_size];
		final ManagedEntity[] t_mes = new ManagedEntity[t_size];
		setUuidAndManagedEntityArray(si, t_entry, t_mes, t_uuids);
		Hashtable<String, Object>[] t_hts = null;
		try {
			t_hts = PropertyCollectorUtil.retrieveProperties(t_mes, t_mes[0]
					.getMOR().getType(), propPath);
		} catch (final RemoteException t_e) {
			C_LOGGER.error("RemoteException " + t_e);
		}
		if (null != t_hts) {
			for (int t_i = 0; t_i < t_uuids.length; t_i++) {
				t_allMap.put(t_uuids[t_i], t_hts[t_i]);
			}
		}
		return t_allMap;
	}

	/**
	 * {Build ManagedEntity[] and String[] according to the Set}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param entry
	 *            Set<Entry<String, ManagedObjectReference>>
	 * @param mes
	 *            ManagedEntity[]
	 * @param uuids
	 *            String[] 可见 map的key是uuid，value是被管对象
	 */
	private void setUuidAndManagedEntityArray(final ServiceInstance si,
			final Set<Entry<String, ManagedObjectReference>> entry,
			final ManagedEntity[] mes, final String[] uuids) {
		final Iterator<Entry<String, ManagedObjectReference>> t_it = entry
				.iterator();
		for (int t_i = 0; t_it.hasNext(); t_i++) {
			final Entry<String, ManagedObjectReference> t_next = t_it.next();
			uuids[t_i] = t_next.getKey();
			mes[t_i] = new ManagedEntity(si.getServerConnection(),
					t_next.getValue());
		}
	}

	// /**
	// * {Initialize the C_ESXIMAP}.
	// *
	// * @param si ServiceInstance
	// */
	// @SuppressWarnings("unchecked")
	// void initESXIUuidMap(final ServiceInstance si) {
	// if (null != si) {
	// synchronized (C_ESXIMAP) {
	// if (!VMWareBaseCollector.s_esxiInitialized) {
	// final ManagedEntity[] t_mes = getHostSystems(si);
	// Hashtable<String, String>[] t_hts = null;
	// try {
	// t_hts =
	// PropertyCollectorUtil.retrieveProperties(t_mes,
	// t_mes[0].getMOR().getType(),
	// new String[] { "summary.hardware.uuid" });
	// } catch (final RemoteException t_e) {
	// C_LOGGER.error("RemoteException " + t_e);
	// }
	// if (null != t_hts) {
	// for (int t_i = 0; t_i < t_hts.length; t_i++) {
	// C_ESXIMAP.put(t_hts[t_i].get("summary.hardware.uuid"),
	// t_mes[t_i].getMOR());
	// }
	// VMWareBaseCollector.s_esxiInitialized = Boolean.TRUE;
	// }
	// }
	// }
	// }
	// }

	/**
	 * {Initialize the C_DATASTOREMAP}.
	 * 
	 * @param si
	 *            ServiceInstance
	 */
	void initDatastoreUuidMap(final ServiceInstance si) {
		if (null != si) {
			synchronized (C_DATASTOREMAP) {
				final ManagedEntity[] t_mes = getDatastores(si);
				for (ManagedEntity me : t_mes) {
					final String uuidDS = VSphereUtil.getDatastoreUUID((Datastore) me);
					C_DATASTOREMAP.put(uuidDS, me.getMOR());
				}
			}
		}
	}

	/**
	 * {Initialize the C_DATASTOREMAP}.
	 * 
	 * @param si
	 *            ServiceInstance
	 */
	// @SuppressWarnings("unchecked")
	void initVSwitchUuidMap(final ServiceInstance si) {
		if (null != si) {
			synchronized (C_VSWITCHMAP) {
				if (!VMWareBaseCollector.s_vsInitialized) {
					final ManagedEntity[] t_mes = getHostSystems(si);
					for (ManagedEntity t_me : t_mes) {
						HostSystem t_hs = new HostSystem(
								si.getServerConnection(), t_me.getMOR());
						if (null != t_hs) {
							String t_hsUuid = (String) t_hs
									.getPropertyByPath(SUMMARY_HARDWARE_UUID);
							HostVirtualSwitch[] t_vswitchs = t_hs.getConfig()
									.getNetwork().getVswitch();
							if (null != t_vswitchs)
								for (HostVirtualSwitch t_vswitch : t_vswitchs) {
									C_VSWITCHMAP.put(VSphereUtil
											.createUuid(t_hsUuid + "/"
													+ t_vswitch.getName()),
											t_vswitch);
								}
						}
					}
					VMWareBaseCollector.s_vsInitialized = Boolean.TRUE;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	ManagedObjectReference getClustMO(final ServiceInstance si, String uuid) {
		if (null != si) {
			if (!VMWareBaseCollector.s_clusterInitialized) {
				final ManagedEntity[] t_mes = getClusterComputeResources(si);
				if (null == t_mes || t_mes.length == 0) {
					return null;
				}
				Hashtable<String, String>[] t_hts = null;
				try {
					t_hts = PropertyCollectorUtil.retrieveProperties(t_mes,
							t_mes[0].getMOR().getType(),
							new String[] { "name" });
				} catch (final RemoteException t_e) {
					C_LOGGER.error("RemoteException " + t_e);
				}
				if (null != t_hts) {
					for (int t_i = 0; t_i < t_hts.length; t_i++) {

						String myUuid = VSphereUtil.createUuid(t_hts[t_i]
								.get("name"));
						if (uuid.equals(myUuid)) {
							return t_mes[t_i].getMOR();
						}
					}
					VMWareBaseCollector.s_clusterInitialized = Boolean.TRUE;
				}
			}
		}
		return null;
	}

	/**
	 * {Initialize the C_CLUSTERMAP}.
	 *
	 * @param si
	 *            ServiceInstance
	 */
	@SuppressWarnings("unchecked")
	void initClusterUuidMap(final ServiceInstance si) {
		if (null != si) {
			synchronized (C_CLUSTERMAP) {
				if (!VMWareBaseCollector.s_clusterInitialized) {
					final ManagedEntity[] t_mes = getClusterComputeResources(si);
					Hashtable<String, String>[] t_hts = null;
					try {
						// now we use name as uuid temporarily
						t_hts = PropertyCollectorUtil.retrieveProperties(t_mes,
								t_mes[0].getMOR().getType(),
								new String[] { "name" });
					} catch (final RemoteException t_e) {
						C_LOGGER.error("RemoteException " + t_e);
					}
					if (null != t_hts) {
						for (int t_i = 0; t_i < t_hts.length; t_i++) {
							C_CLUSTERMAP.put(VSphereUtil.createUuid(t_hts[t_i]
									.get("name")), t_mes[t_i].getMOR());
						}
						VMWareBaseCollector.s_clusterInitialized = Boolean.TRUE;
					}
				}
			}
		}
	}

	// /**
	// * {Initialize the C_VMMAP}.
	// *
	// * @param si
	// * ServiceInstance
	// */
	// @SuppressWarnings("unchecked")
	// void initVMUuidMap(final ServiceInstance si) {
	// if (null != si) {
	// synchronized (C_VMMAP) {
	// if (!VMWareBaseCollector.s_vmInitialized) {
	// final ManagedEntity[] t_mes = getVirtualMachines(si);
	// Hashtable<String, String>[] t_hts = null;
	// try {
	// t_hts = PropertyCollectorUtil.retrieveProperties(t_mes,
	// t_mes[0].getMOR().getType(),
	// new String[] { "summary.config.instanceUuid" });
	// } catch (final RemoteException t_e) {
	// C_LOGGER.error("RemoteException " + t_e);
	// }
	// if (null != t_hts) {
	// for (int t_i = 0; t_i < t_hts.length; t_i++) {
	// if (null != t_hts[t_i]
	// .get("summary.config.instanceUuid")) {
	// C_VMMAP.put(t_hts[t_i]
	// .get("summary.config.instanceUuid"),
	// t_mes[t_i].getMOR());
	// }
	// }
	// VMWareBaseCollector.s_vmInitialized = Boolean.TRUE;
	// }
	// }
	// }
	// }
	// }

	/**
	 * {Create a uuid}.
	 * 
	 * @param str
	 *            String[]
	 * @return String
	 */


	/**
	 * {Set the cached property Metric map using result map}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param titles
	 *            String[]
	 * @param MetricMap
	 *            Map<String, Metric>
	 * @param resultMap
	 *            Map<String, Hashtable<String, Object>>
	 */
	void setPropMetricMap(final ServiceInstance si, final String[] titles,
			final Map<String, Metric> MetricMap,
			final Map<String, Hashtable<String, Object>> resultMap) {
		if (null != si) {
			final Iterator<Entry<String, Hashtable<String, Object>>> t_it = resultMap
					.entrySet().iterator();
			while (t_it.hasNext()) {
				final Entry<String, Hashtable<String, Object>> t_entry = t_it
						.next();
				MetricMap.put(
						t_entry.getKey(),
						buildPropMetricFromHashTable(si, titles,
								t_entry.getValue()));
			}
		}
	}

	/**
	 * {Build the property Metric from hashtable}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param titles
	 *            String[]
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @return Metric
	 */
	private Metric buildPropMetricFromHashTable(final ServiceInstance si,
			final String[] titles, final Hashtable<String, Object> hashtable) {
		final Metric t_Metric = new Metric();
		t_Metric.addTitles(titles);
		t_Metric.addRow(CommonConstants.C_DEFAULT_INDEX,
				getPropMetricValue(si, titles, hashtable));
		return t_Metric;
	}

	/**
	 * {Get the property metric value from ht according titles}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param titles
	 *            String[]
	 * @param ht
	 *            Hashtable<String, Object>
	 * @return String[]
	 */
	private String[] getPropMetricValue(final ServiceInstance si,
			final String[] titles, final Hashtable<String, Object> ht) {
		final String[] t_values = new String[titles.length];
		final Class<? extends VMWareBaseCollector> t_class = this.getClass();
		for (int t_i = 0; t_i < titles.length; t_i++) {
			final String t_name = "get" + titles[t_i];
			Method t_method = null;
			try {
				t_method = t_class.getMethod(t_name, ServiceInstance.class,
						Hashtable.class);
				t_values[t_i] = (String) t_method.invoke(this, si, ht);
			} catch (final Exception t_e) {
				C_LOGGER.error("getPropMetricValue: methodName = " + t_name,
						t_e);
			}
		}
		return t_values;
	}

	public void putMetricToMap(Map<String, Metric> map, String key, Metric value) {
		value.setExecuteRealTime(System.currentTimeMillis());
		map.put(key, value);
	}

	// public void addRowToMatix(Metric matrix, String index, String... values)
	// {
	// matrix.addRow(index, values);
	// matrix.setExecuteRealTime(System.currentTimeMillis());
	// }

	/**
	 * {Set the cached performance Metric map using result map}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param titles
	 *            String[]
	 * @param MetricMap
	 *            Map<String, Metric>
	 * @param resultMap
	 *            Map<String, HashMap<String, long[]>>
	 */
	void setPerfMetricMap(final ServiceInstance si, final String[] titles,
			final Map<String, Metric> MetricMap,
			final Map<String, HashMap<String, long[]>> resultMap) {
		if (null != si) {
			final Iterator<Entry<String, HashMap<String, long[]>>> t_it = resultMap
					.entrySet().iterator();
			while (t_it.hasNext()) {
				final Entry<String, HashMap<String, long[]>> t_entry = t_it
						.next();
				MetricMap.put(t_entry.getKey(),
						buildPerfMetricFromMap(titles, t_entry.getValue()));
			}
		}
	}

	/**
	 * {Build the performance Metric from HashMap}.
	 * 
	 * @param titles
	 *            String[]
	 * @param map
	 *            HashMap<String, long[]>
	 * @return Metric
	 */
	Metric buildPerfMetricFromMap(final String[] titles,
			final HashMap<String, long[]> map) {
		final Metric t_Metric = new Metric();
		t_Metric.addTitles(titles);
		t_Metric.addRow(CommonConstants.C_DEFAULT_INDEX,
				getPerfMetricValue(titles, map));
		t_Metric.setExecuteRealTime(System.currentTimeMillis());
		return t_Metric;
	}

	/**
	 * {Get the performance metric value from map according titles}.
	 * 
	 * @param titles
	 *            String[]
	 * @param map
	 *            HashMap<String, long[]>
	 * @return String[]
	 */
	private String[] getPerfMetricValue(final String[] titles,
			final HashMap<String, long[]> map) {
		final Class<? extends VMWareBaseCollector> t_cl = this.getClass();
		final String[] t_values = new String[titles.length];
		for (int t_i = 0; t_i < titles.length; t_i++) {
			final String t_name = "get" + titles[t_i];
			Method t_method = null;
			try {
				t_method = t_cl.getMethod(t_name, HashMap.class);
				String t_value = (String) t_method.invoke(this, map);
				t_values[t_i] = contactData(titles[t_i], t_value);
			} catch (final Exception t_e) {
				C_LOGGER.error("getPerfMetricValue: methodName = " + t_name,
						t_e);
			}
		}
		return t_values;
	}

	/**
	 * {Get sub property values from hashtable according to titles and
	 * instance}.
	 * 
	 * @param si
	 *            ServiceInstance
	 * @param titles
	 *            String[]
	 * @param instance
	 *            String
	 * @param ht
	 *            Hashtable<String, Object>
	 * @return String[]
	 */
	@SuppressWarnings("unused")
	private String[] getSubPropMetricValue(final ServiceInstance si,
			final String[] titles, final String instance,
			final Hashtable<String, Object> ht) {
		final String[] t_values = new String[titles.length];
		final Class<? extends VMWareBaseCollector> t_class = this.getClass();
		for (int t_i = 0; t_i < titles.length; t_i++) {
			final String t_name = "get" + titles[t_i];
			Method t_method;
			try {
				t_method = t_class.getMethod(t_name, ServiceInstance.class,
						String.class, Hashtable.class);
				String t_result = (java.lang.String) t_method.invoke(this, si,
						instance, ht);
				t_values[t_i] = contactData(titles[t_i], t_result);
			} catch (final Exception t_e) {
				C_LOGGER.error("getSubPropMetricValue: methodName = " + t_name,
						t_e);
			}
		}
		return t_values;
	}

	/**
	 * {convert Date to String, the format is "yyyy-MM-dd hh:mm:ss"}.
	 * 
	 * @param d
	 *            Date
	 * @return String
	 */
	@SuppressWarnings("unused")
	private String formatDate(final Date d) {
		if (null == d) {
			return "";
		}
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
	}

	/**
	 * {Get the value according the path, and cast the value to type T Note that
	 * it will throw ClassCastException if the object is not null and is not
	 * assignable to the type T.}.
	 * 
	 * @param <T>
	 *            T
	 * @param hashtable
	 *            Hashtable<String, Object>
	 * @param path
	 *            String
	 * @param type
	 *            Class<T>
	 * @return T
	 */
	<T> T getPropValueFromHashtable(final Hashtable<String, Object> hashtable,
			final String path, final Class<T> type) {
		try {
			return type.cast(hashtable.get(path));
		} catch (Exception t_t_e) {
			C_LOGGER.error("Exception Cast Error.  castType : " + type
					+ ",hashtableObject : " + hashtable.get(path) + " ,path : "
					+ path);
			return null;
		}
	}

	/**
	 * 获取存储器
	 * 
	 * @param hts
	 * @return
	 */
	String buildDatastoreListXML(final Hashtable<String, Object>[] hts) {
		StringBuffer sb = new StringBuffer();
		for (final Hashtable<String, Object> ht : hts) {
			sb.append("<Datastore>");
			String name = (String) ht.get("summary.name");
			if (StringUtils.isEmpty(name)) {
				name = "";
			} else {
				name = handleSpechars(name);
			}

			Object capObj = ht.get("summary.capacity");
			String capacity = "0";
			if (null != capObj) {
				capacity = (String) capObj;
			}

			Object freeObj = ht.get("summary.freeSpace");
			String freeSpace = "0";
			if (null != freeObj) {
				freeSpace = (String) freeObj;
			}
			boolean accessible = false;
			Object acceObj = ht.get("summary.accessible");
			if (null != acceObj) {
				accessible = (boolean) acceObj;
			}
			sb.append(name).append("\t").append(capacity).append("\t")
					.append(freeSpace).append("\t")
					.append(accessible ? "1" : "0");
			sb.append("</Datastore>\n");
		}
		return sb.toString();
	}

	/**
	 * {build VMList XML using hashtable array}.
	 * 
	 * @param hts
	 *            Hashtable<String, Object>[]
	 * @return String
	 */
	String buildVMListXML(final Hashtable<String, Object>[] hts) {
		final StringBuilder t_sb = new StringBuilder();
		t_sb.append("<Items>");
		for (final Hashtable<String, Object> t_ht : hts) {
			String t_name = (String) t_ht.get("name");
			t_name = handleSpechars(t_name);
			final String t_uuid = (String) t_ht
					.get("summary.config.instanceUuid");
			t_sb.append("<Item name=\"").append(t_name).append("\" uuid=\"")
					.append(t_uuid).append("\"></Item>");
		}
		t_sb.append("</Items>");
		return t_sb.toString();
	}

	/**
	 * {build VMList XML using hashtable array}.
	 * 
	 * @param hts
	 *            Hashtable<String, Object>[]
	 * @return String
	 */
	String buildVSwitchListXML(final String parentUuid,
			final HostVirtualSwitch[] hss) {
		final StringBuilder t_sb = new StringBuilder();
		t_sb.append("<Items>");
		for (HostVirtualSwitch t_hs : hss) {
			String t_name = handleSpechars(t_hs.getName());
			final String t_uuid = VSphereUtil.createUuid(parentUuid + "/"
					+ t_name);
			t_sb.append("<Item name=\"").append(t_name).append("\" uuid=\"")
					.append(t_uuid).append("\"></Item>");
		}
		t_sb.append("</Items>");
		return t_sb.toString();
	}

	/**
	 * {build VMList XML using hashtable array}.
	 * 
	 * @param hss
	 *            hss
	 * @return String
	 */
	String buildHostListXML(final ManagedEntity[] hss) {
		final StringBuilder t_sb = new StringBuilder();
		t_sb.append("<Items>");
		for (ManagedEntity t_hs : hss) {
			String t_name = handleSpechars(t_hs.getName());
			final String t_uuid = (String) t_hs
					.getPropertyByPath(SUMMARY_HARDWARE_UUID);
			t_sb.append("<Item name=\"").append(t_name).append("\" uuid=\"")
					.append(t_uuid).append("\"></Item>");
		}
		t_sb.append("</Items>");
		return t_sb.toString();
	}

	/**
	 * <code>C_SPECHARS</code> - {description}.
	 */
	private static final Map<String, String> C_SPECHARS = new HashMap<String, String>();
	static {
		C_SPECHARS.put("<", "&lt;");
		C_SPECHARS.put(">", "&gt;");
		C_SPECHARS.put("&", "&amp;");
		C_SPECHARS.put("'", "&apos;");
		C_SPECHARS.put("\"", "&quot;");
	}

	/**
	 * handleSpechars.
	 * 
	 * @param str
	 *            str
	 * @return String
	 */
	public static String handleSpechars(final String str) {
		if (null == str) {
			return "";
		}
		String t_result = str;
		Iterator<String> t_iterator = C_SPECHARS.keySet().iterator();
		while (t_iterator.hasNext()) {
			String t_key = t_iterator.next();
			String t_val = C_SPECHARS.get(t_key);
			t_result = t_result.replaceAll(t_key, t_val);
		}
		return t_result;
	}

	/**
	 * contactData.
	 * 
	 * @param title
	 *            title
	 * @param value
	 *            value
	 * @return String
	 */
	public String contactData(String title, String value) {
		return title + VMWareVMConstants.C_SPLIT + value;
	}
}
