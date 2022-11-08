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
import com.mainsteam.stm.plugin.smis.device.FCPort;
import com.mainsteam.stm.plugin.smis.device.Node;
import com.mainsteam.stm.plugin.smis.device.StoragePool;
import com.mainsteam.stm.plugin.smis.device.StorageSystem;
import com.mainsteam.stm.plugin.smis.device.StorageVolume;
import com.mainsteam.stm.plugin.smis.exception.SMISException;


public class HP3ParCollector extends SMISCollector{

	protected HP3ParCollector(SMISProvider provider) throws WBEMException {
		super(provider);
		client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
	}

	public HP3ParCollector(SMISProvider provider,String name) throws WBEMException, SMISException{
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
		CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "TPD_StorageSystem", null);
		CloseableIterator<CIMInstance> computerSystemIterator = enumerateInstances(path, true, false, true, null);
		List<StorageSystem> ret = new ArrayList<>();
		while (computerSystemIterator.hasNext()) {
			ret.add(new StorageSystem(computerSystemIterator.next()));
		}
		computerSystemIterator.close();
		return ret;
	}
	
	private void initStorageSystem(StorageSystem storageSystem) throws WBEMException{
		devicePool.clear();;
        addDevice(storageSystem.getClass(), storageSystem.getInstance());
        storageSystem.getAdjacentDevices().addAll(initNode(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStoragePool(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
        base = storageSystem;
	}
	
	private Set<DiskDrive> initDiskDrive(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskDrive> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "TPD_SystemDisk", "TPD_DiskDrive", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
			diskDrive.getAdjacentDevices().add(storageSystem);
			result.add(diskDrive);
		}
		iterator.close();
		return result;
	}

	private Set<StoragePool> initStoragePool(StorageSystem storageSystem) throws WBEMException {
		HashSet<StoragePool> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "TPD_HostedStoragePool", "TPD_StoragePool", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StoragePool storagePool = addDevice(StoragePool.class, iterator.next());
			storagePool.getAdjacentDevices().add(storageSystem);
			result.add(storagePool);
		}
		iterator.close();
		return result;
	}

	private Set<StorageVolume> initStorageVolume(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageVolume> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "TPD_SystemVolume", "TPD_StorageVolumn", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
			storageVolume.getAdjacentDevices().add(storageSystem);
			result.add(storageVolume);
		}
		iterator.close();
		return result;
	}

	public Set<Node> initNode(StorageSystem storageSystem) throws WBEMException{
		HashSet<Node> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_ComponentCS", "TPD_NodeSystem", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			Node node = addDevice(Node.class, iterator.next());
			node.getAdjacentDevices().add(storageSystem);
			node.getAdjacentDevices().addAll(initFCPort(node));
			result.add(node);
		}
		iterator.close();
		return result;
	}

	private Set<FCPort> initFCPort(Node node) throws WBEMException {
		HashSet<FCPort> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(node.getInstance().getObjectPath(), "TPD_SystemFCPort", "TPD_FCPort", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			FCPort fcPort = addDevice(FCPort.class, iterator.next());
			fcPort.getAdjacentDevices().add(node);
			result.add(fcPort);
		}
		iterator.close();
		return result;
	}

} 
