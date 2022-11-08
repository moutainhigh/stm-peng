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

import com.mainsteam.stm.plugin.smis.device.ConcreteStoragePool;
import com.mainsteam.stm.plugin.smis.device.DiskExtent;
import com.mainsteam.stm.plugin.smis.device.FrontEndFCPort;
import com.mainsteam.stm.plugin.smis.device.StorageControllerSystem;
import com.mainsteam.stm.plugin.smis.device.StorageSystem;
import com.mainsteam.stm.plugin.smis.device.StorageVolume;
import com.mainsteam.stm.plugin.smis.exception.SMISException;

public class HUAWEIVCollector extends SMISCollector{

	public HUAWEIVCollector(SMISProvider provider) throws WBEMException {
		super(provider);
		client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
	}
	
	public HUAWEIVCollector(SMISProvider provider,String name) throws WBEMException, SMISException {
		this(provider);
		List<StorageSystem> storageSystems = discoveryStorageSystem();
		List<StorageSystem> baseSystems = new ArrayList<StorageSystem>();
        for (StorageSystem storageSystem : storageSystems) {
            if (StringUtils.equals(storageSystem.getPropertyValue("name"), name)) {
                baseSystems.add(storageSystem);
            }
        }
        if (baseSystems.size() < 1) {
            throw new SMISException("Can't find the specific StorageSystem.");
        }
        initStorageSystem(baseSystems.get(0));
	}

	public List<StorageSystem> discoveryStorageSystem() throws WBEMException {
		CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "HuaSy_StorageSystem", null);
        CloseableIterator<CIMInstance> computerSystemIterator = enumerateInstances(path, true, false, true, null);
        List<StorageSystem> ret = new ArrayList<StorageSystem>();
        while (computerSystemIterator.hasNext()) {
            ret.add(new StorageSystem(computerSystemIterator.next()));
        }
        computerSystemIterator.close();
        return ret;
	}

	private void initStorageSystem(StorageSystem storageSystem) throws WBEMException{
		devicePool.clear();
		addDevice(storageSystem.getClass(), storageSystem.getInstance());
		storageSystem.getAdjacentDevices().addAll(initConcreteStoragePool(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initDiskExtent(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initStorageControllerSystem(storageSystem));
		base=storageSystem;
	}

	private Set<StorageVolume> initStorageVolume(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageVolume> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HuaSy_SystemDevice", "HuaSy_StorageVolume", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
			storageVolume.getAdjacentDevices().add(storageSystem);
			result.add(storageVolume);
		}
		iterator.close();
		return result;
	}

	private Set<StorageControllerSystem> initStorageControllerSystem(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageControllerSystem> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HuaSy_ComponentCS", "HuaSy_StorageControllerSystem", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageControllerSystem storageControllerSystem = addDevice(StorageControllerSystem.class, iterator.next());
			storageControllerSystem.getAdjacentDevices().add(storageSystem);
			storageControllerSystem.getAdjacentDevices().addAll(initFrontEndFCPort(storageControllerSystem));
			result.add(storageControllerSystem);
		}
		iterator.close();
		return result;
	}

	private Set<FrontEndFCPort> initFrontEndFCPort(StorageControllerSystem storageControllerSystem) throws WBEMException {
		HashSet<FrontEndFCPort> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageControllerSystem.getInstance().getObjectPath(), "HuaSy_SystemDevice", "HuaSy_FrontEndFCPort", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			FrontEndFCPort frontEndFCPort = addDevice(FrontEndFCPort.class, iterator.next());
			frontEndFCPort.getAdjacentDevices().add(storageControllerSystem);
			result.add(frontEndFCPort);
		}
		iterator.close();
		return result;
	}


	private Set<DiskExtent> initDiskExtent(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskExtent> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HuaSy_SystemDevice", "HuaSy_DiskExtent", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskExtent diskExtent = addDevice(DiskExtent.class, iterator.next());
			diskExtent.getAdjacentDevices().add(storageSystem);
			result.add(diskExtent);
		}
		iterator.close();
		return result;
	}

	private Set<ConcreteStoragePool> initConcreteStoragePool(StorageSystem storageSystem) throws WBEMException {
		HashSet<ConcreteStoragePool> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HuaSy_HostedStoragePool", "HuaSy_ConcreteStoragePool", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			ConcreteStoragePool concreteStoragePool = addDevice(ConcreteStoragePool.class, iterator.next());
			concreteStoragePool.getAdjacentDevices().add(storageSystem);
			result.add(concreteStoragePool);
		}
		iterator.close();
		return result;
	}

}
