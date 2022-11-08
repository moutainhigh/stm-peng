package com.mainsteam.stm.plugin.vmware.util;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.HostNetworkInfo;
import com.vmware.vim25.HostVirtualNic;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.ClusterComputeResource;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostNetworkSystem;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class VSphereUtil {

	// private static final String GSX = "gsx";

	public static final String TYPE_HOST_SYSTEM = "HostSystem";
	public static final String TYPE_POOL = "ResourcePool";
	public static final String TYPE_VM = "VirtualMachine";
	public static final String TYPE_DATASTORE = "Datastore";

	private static final Log LOGGER = LogFactory.getLog(VSphereUtil.class);

	// static final String PROP_URL = VSphereCollector.PROP_URL;
	// static final String PROP_HOSTNAME = VSphereCollector.PROP_HOSTNAME;
	// static final String PROP_USERNAME = VSphereCollector.PROP_USERNAME;
	// static final String PROP_PASSWORD = VSphereCollector.PROP_PASSWORD;

	private static final Log S_LOGGER = LogFactory.getLog(VSphereUtil.class);

	public static boolean isESX(ServiceInstance si) {
		return si.getAboutInfo().getProductLineId().contains("sx");
	}

	public static InventoryNavigator getNavigator(ServiceInstance si) {
		return new InventoryNavigator(si.getRootFolder());
	}

	public static ManagedEntity findByUuid(ServiceInstance si, String type, String uuid) {

		ManagedEntity obj = null;
		try {
			ManagedEntity[] entities = find(si, type);
			for (int i = 0; entities != null && i < entities.length; i++) {
				ManagedEntity entity = entities[i];
				if (uuid.equals(getUuid(entity))) {
					obj = entity;
					break;
				}
			}
		} catch (Exception e) {
			S_LOGGER.error(e.getMessage(), e);
		}

		return obj;
	}

	public static HostSystem findHostByUuid(ServiceInstance si, String uuid) {
		HostSystem host = VmwareBeansCachedecorator.getHost(si, uuid);
		if(host!=null){
			return host;
		}
		final ManagedEntity t_mo = findByUuid(si, TYPE_HOST_SYSTEM, uuid);
		if (null != t_mo) {
			ManagedObjectReference t_mor = t_mo.getMOR();
			final HostSystem t_hs = new HostSystem(si.getServerConnection(), t_mor);
			VmwareBeansCachedecorator.cacheHost(uuid, t_hs);
			return t_hs;
		}
		return null;
	}

	public static Object t_lock = new Object();

	public static VirtualMachine findVMByUuid(ServiceInstance si, String uuid) {
		VirtualMachine cacheVM = VmwareBeansCachedecorator.getVm(si, uuid);
		if (cacheVM != null) {
			return cacheVM;
		} else {
			final ManagedEntity t_mo = findByUuid(si, TYPE_VM, uuid);
			if (null != t_mo) {
				final VirtualMachine t_vm = (VirtualMachine) t_mo;
				VmwareBeansCachedecorator.cacheVm(uuid, t_vm);
				return t_vm;
			}
			return null;
		}
	}
	
	public static Datastore findDatastoreByUuid(ServiceInstance si, String uuid) {
		Datastore datastore = VmwareBeansCachedecorator.getDatastore(si, uuid);
		if (datastore != null) {
			return datastore;
		} else {
			final ManagedEntity t_mo = findByUuid(si, TYPE_DATASTORE, uuid);
			if (null != t_mo) {
				ManagedObjectReference t_mor = t_mo.getMOR();
				final Datastore ds = new Datastore(si.getServerConnection(), t_mor);
				VmwareBeansCachedecorator.cacheDatastore(uuid, ds);
				return ds;
			}
			return null;
		}
	}

	public static ManagedEntity find(ServiceInstance si, String type, String name) {

		ManagedEntity obj = null;
		try {
			// the vijava api will return the first instance of the
			// entity type with the given name
			obj = getNavigator(si).searchManagedEntity(type, name);
		} catch (Exception e) {
			S_LOGGER.error(e.getMessage(), e);
		}
		return obj;
	}

	public static ManagedEntity[] find(ServiceInstance si, String type) {
		ManagedEntity[] obj = null;
		try {
			obj = getNavigator(si).searchManagedEntities(type);
		} catch (Exception e) {
			S_LOGGER.error(e.getMessage(), e);
		}
		return obj;
	}

	/**
	 * {Disconnect to VCenter}.
	 * 
	 * @param si
	 *            ServiceInstance
	 */
	public static void dropServiceInstance(final ServiceInstance si) {
		if (null != si) {
			try {
				si.getServerConnection().logout();
			} catch (Exception e) {
				S_LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public static HostSystem getHost(ServiceInstance si, String hostName) {
		return (HostSystem) find(si, TYPE_HOST_SYSTEM, hostName);
	}

	public static String createDatastoreUUID(final String... str) {
		String x = str[0];
		int uIdx = StringUtils.lastIndexOf(x, '/', x.length() - 2);
		String uuid = "";
		if (StringUtils.endsWith(x, "/")) {
			uuid = x.substring(uIdx + 1, x.length() - 1);
		} else {
			uuid = x.substring(uIdx + 1);
		}
		if (StringUtils.contains(uuid, "-") && uuid.length() == 35) {
			return uuid;
		} else {
			return createUuid(uuid);
		}
	}

	public static String getHostIp(HostSystem hostSystem) {
		TreeSet<String> addresses = null;
		String ipAddress = null;
		try {
			ipAddress = InetAddress.getByName(hostSystem.getName()).getHostAddress();
			addresses = getHostSystemIpAddresses(hostSystem);
		} catch (Exception e) {
			LOGGER.error("Can't resolve the IP address from " + hostSystem.getName());
		}
		if (null != addresses) {
			if (ipAddress == null) {
				return addresses.first();
			} else {
				return addresses.contains(ipAddress) ? ipAddress : addresses.first();
			}
		} else {
			if (null != ipAddress) {
				return ipAddress;
			}
			// 如果是vcenter会获取到vcenter的ip
			return (String) hostSystem.getPropertyByPath("summary.managementServerIp");
		}
	}

	public static TreeSet<String> getHostSystemIpAddresses(HostSystem hostSystem) throws RemoteException {
		TreeSet<String> ipAddresses = new TreeSet<String>();
		HostNetworkSystem hostNetworkSystem = hostSystem.getHostNetworkSystem();

		if (hostNetworkSystem != null) {
			HostNetworkInfo hostNetworkInfo = hostNetworkSystem.getNetworkInfo();
			if (hostNetworkInfo != null) {
				HostVirtualNic[] hostVirtualNics = hostNetworkInfo.getConsoleVnic();
				if (hostVirtualNics != null) {
					for (HostVirtualNic hostVirtualNic : hostVirtualNics) {
						ipAddresses.add(hostVirtualNic.getSpec().getIp().getIpAddress());
					}
				}
				hostVirtualNics = hostNetworkInfo.getVnic();
				if (hostVirtualNics != null) {
					for (HostVirtualNic hostVirtualNic : hostVirtualNics) {
						ipAddresses.add(hostVirtualNic.getSpec().getIp().getIpAddress());
					}
				}
			}
		}
		return ipAddresses;
	}

	public static String getDatastoreUUID(Datastore ds) {
		String url = getDatastoreUrl(ds);
		return createDatastoreUUID(url);
	}

	// ------------------------------------------------------
	public static String createResourcePoolUUID(final String... str) {
		String x = str[0];
		int uIdx = StringUtils.lastIndexOf(x, '/', x.length() - 2);
		String uuid = "";
		if (StringUtils.endsWith(x, "/")) {
			uuid = x.substring(uIdx + 1, x.length() - 1);
		} else {
			uuid = x.substring(uIdx + 1);
		}
		if (StringUtils.contains(uuid, "-") && uuid.length() == 35) {
			return uuid;
		} else {
			return createUuid(uuid);
		}
	}

	public static String getResourcePoolUuid(ResourcePool pool) {
		String url = getResourcePoolUrl(pool);
		return createResourcePoolUUID(url);
	}

	public static String getResourcePoolUrl(ResourcePool pool) {
		return pool.getSummary().getName();// ---------
	}

	// ------------------------------------------------------
	public static String getDatastoreUrl(Datastore ds) {
		return ds.getSummary().getUrl();
	}

	public static String createUuid(final String... str) {
		final StringBuilder t_sb = new StringBuilder();
		for (final String t_s : str) {
			t_sb.append(t_s);
		}
		return UUID.nameUUIDFromBytes(t_sb.toString().getBytes()).toString();
	}

	public static String getHostUuid(HostSystem host) {
		HostListSummary summary = host.getSummary();
		String uuid = summary.getHardware().getUuid();
		String name = host.getName();
		String serverIP = summary.getManagementServerIp();
//		String ip = getHostIp(host);
		return createUuid(uuid,name,serverIP);
	}

	// 没有用到，先用下面的方法
	// public static String getVMUuid(VirtualMachine vm) {
	// //???
	// return vm.getConfig().getUuid();
	// }

	public static String getVMUuid(VirtualMachine vm) {
		return vm.getConfig().getInstanceUuid();
	}

	public static String getVMIP(final VirtualMachine vm) {
		if (null == vm) {
			return "";
		}
		final String path = "summary.guest.ipAddress";
		final String r = (String) vm.getPropertyByPath(path);
		if (null == r) {
			return "";
		}
		return r;
	}

	//
	public static String getUuid(ManagedEntity entity) {
		String uuid = null;
		if (entity instanceof HostSystem) {
			uuid = getHostUuid((HostSystem) entity);
		} else if (entity instanceof VirtualMachine) {
			uuid = getVMUuid((VirtualMachine) entity);
		} else if(entity instanceof Datastore){
			uuid = getDatastoreUUID((Datastore) entity);
		}else if(entity instanceof ClusterComputeResource){
			uuid = createUuid(entity.getName());
		}else if(entity instanceof ResourcePool){
			uuid = getResourcePoolUuid((ResourcePool) entity);
		}
		return uuid;
	}
	
	public static Integer getCounter(ServiceInstance si,String name){
		return VmwareBeansCachedecorator.getCounter(si, name);
	}
	public static String getCounterName(ServiceInstance si,Integer c){
		return VmwareBeansCachedecorator.getCounterName(si, c);
	}
	

	public static void main(String... str) {
		String x = "ESX:/vmfs/volumes/53e4ff62-61430f07-55b1-000af72b7782";
		System.out.println(VSphereUtil.createDatastoreUUID(new String[] { x }));
	}
}
