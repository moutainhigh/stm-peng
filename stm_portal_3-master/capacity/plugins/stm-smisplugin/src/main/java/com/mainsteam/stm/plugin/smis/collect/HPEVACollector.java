package com.mainsteam.stm.plugin.smis.collect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.plugin.smis.device.DiskDrive;
import com.mainsteam.stm.plugin.smis.device.DiskExtent;
import com.mainsteam.stm.plugin.smis.device.DiskGroupExtent;
import com.mainsteam.stm.plugin.smis.device.FCPort;
import com.mainsteam.stm.plugin.smis.device.Host;
import com.mainsteam.stm.plugin.smis.device.ParentStoragePool;
import com.mainsteam.stm.plugin.smis.device.StoragePool;
import com.mainsteam.stm.plugin.smis.device.StorageProcessorSystem;
import com.mainsteam.stm.plugin.smis.device.StorageSystem;
import com.mainsteam.stm.plugin.smis.device.StorageVolume;
import com.mainsteam.stm.plugin.smis.exception.SMISException;

public class HPEVACollector extends SMISCollector{
	
	protected HPEVACollector(SMISProvider provider) throws WBEMException {
		super(provider);
		client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
	}

	public HPEVACollector(SMISProvider provider,String name) throws WBEMException, SMISException{
		this(provider);
		List<StorageSystem> storageSystems = discoveryStorageSystem();
		List<StorageSystem> baseSystems = new ArrayList<>();
		for (StorageSystem storageSystem : storageSystems) {
			if (StringUtils.equals(storageSystem.getPropertyValue("name"), name)) {
				baseSystems.add(storageSystem);
			}
		}
		if (baseSystems.size()<1) {
			throw new SMISException("Can't find the specific StorageSystem."); 
		}
		initStorageSystem(baseSystems.get(0));
	}
	
	private List<StorageSystem> discoveryStorageSystem() throws WBEMException{
		CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "HPEVA_StorageSystem", null);
		CloseableIterator<CIMInstance> computerSystemIterator = enumerateInstances(path, true, false, true, null);
		List<StorageSystem> ret = new ArrayList<>();
		while (computerSystemIterator.hasNext()) {
			ret.add(new StorageSystem(computerSystemIterator.next()));
		}
		computerSystemIterator.close();
		return ret;
	}
	
	private void initStorageSystem(StorageSystem storageSystem) throws WBEMException{
		devicePool.clear();
        addDevice(storageSystem.getClass(), storageSystem.getInstance());
        storageSystem.getAdjacentDevices().addAll(initStorageProcessorSystem(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskExtent(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskGroupExtent(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initHost(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initFCPort(storageSystem));
        base = storageSystem;
	}
	
	private Set<FCPort> initFCPort(StorageSystem storageSystem) throws WBEMException {
		HashSet<FCPort> result = new HashSet<>();
		//CIM_FCPort包括HPEVA_HostFCPort和HPEVA_DiskFCPort
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HPEVA_SystemDevice", "CIM_FCPort", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			FCPort fcPort = addDevice(FCPort.class, iterator.next());
			fcPort.getAdjacentDevices().add(storageSystem);
			result.add(fcPort);
		}
		iterator.close();
		return result;
	}
	
	/**
	 * Host
	 * @param storageSystem
	 * @return
	 * @throws WBEMException
	 */
	private Set<Host> initHost(StorageSystem storageSystem) throws WBEMException {
		HashSet<Host> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HPEVA_SystemDevice", "HPEVA_ViewProtocolController", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			Host host = addDevice(Host.class, iterator.next());
			host.getAdjacentDevices().add(storageSystem);
			result.add(host);
		}
		iterator.close();
		return result;
	}
	
	private Set<DiskGroupExtent> initDiskGroupExtent(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskGroupExtent> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HPEVA_SystemDevice", "HPEVA_DiskGroupExtent", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskGroupExtent diskGroupExtent = addDevice(DiskGroupExtent.class, iterator.next());
			diskGroupExtent.getAdjacentDevices().add(storageSystem);
			diskGroupExtent.getAdjacentDevices().addAll(initStoragePool(diskGroupExtent));
			result.add(diskGroupExtent);
		}
		iterator.close();
		return result;
	}
	
	private Set<StoragePool> initStoragePool(DiskGroupExtent diskGroupExtent) throws WBEMException {
		HashSet<StoragePool> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(diskGroupExtent.getInstance().getObjectPath(), "HPEVA_StoragePoolDiskGroupComponent", "HPEVA_StoragePool", "PartComponent", "GroupComponent", true, null);
		while (iterator.hasNext()) {
			StoragePool storagePool = addDevice(StoragePool.class, iterator.next());
			storagePool.getAdjacentDevices().add(diskGroupExtent);
			result.add(storagePool);
		}
		iterator.close();
		return result;
	}
	
	private Set<DiskExtent> initDiskExtent(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskExtent> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HPEVA_SystemDevice", "HPEVA_DiskExtent", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskExtent diskExtent = addDevice(DiskExtent.class, iterator.next());
			diskExtent.getAdjacentDevices().add(storageSystem);
			result.add(diskExtent);
		}
		iterator.close();
		return result;
	}
	
	private Set<DiskDrive> initDiskDrive(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskDrive> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HPEVA_SystemDevice", "HPEVA_DiskDrive", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
			diskDrive.getAdjacentDevices().add(storageSystem);
			result.add(diskDrive);
		}
		iterator.close();
		return result;
	}
	
	private Set<StorageVolume> initStorageVolume(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageVolume> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HPEVA_SystemDevice", "HPEVA_StorageVolume", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
			storageVolume.getAdjacentDevices().add(storageSystem);
			result.add(storageVolume);
		}
		iterator.close();
		return result;
	}
	
	private Set<StorageProcessorSystem> initStorageProcessorSystem(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageProcessorSystem> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HPEVA_ComponentCS", "HPEVA_StorageProcessorSystem", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageProcessorSystem storageProcessorSystem = addDevice(StorageProcessorSystem.class, iterator.next());
			storageProcessorSystem.getAdjacentDevices().add(storageSystem);
			result.add(storageProcessorSystem);
		}
		iterator.close();
		return result;
	}
}
