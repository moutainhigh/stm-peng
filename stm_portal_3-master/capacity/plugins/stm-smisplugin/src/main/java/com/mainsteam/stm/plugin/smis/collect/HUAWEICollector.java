package com.mainsteam.stm.plugin.smis.collect;

import java.util.ArrayList;
import java.util.Collection;
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
import com.mainsteam.stm.plugin.smis.device.DiskStoragePool;
import com.mainsteam.stm.plugin.smis.device.FrontEndEthernetPort;
import com.mainsteam.stm.plugin.smis.device.FrontEndFCPort;
import com.mainsteam.stm.plugin.smis.device.StorageControllerSystem;
import com.mainsteam.stm.plugin.smis.device.StorageEngineSystem;
import com.mainsteam.stm.plugin.smis.device.StorageSystem;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import com.mainsteam.stm.plugin.smis.object.SMISDevice;

public class HUAWEICollector extends SMISCollector{

	public HUAWEICollector(SMISProvider provider) throws WBEMException {
		super(provider);
		client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
	}
	
	public HUAWEICollector(SMISProvider provider,String name) throws WBEMException, SMISException {
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
		storageSystem.getAdjacentDevices().addAll(initDiskStoragePool(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initConcreteStoragePool(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initStorageEngineSystem(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initStorageControllerSystem(storageSystem));
		base=storageSystem;
	}

	private Set<StorageControllerSystem> initStorageControllerSystem(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageControllerSystem> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HuaSy_ComponentCS", "HuaSy_StorageControllerSystem", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageControllerSystem storageControllerSystem = addDevice(StorageControllerSystem.class, iterator.next());
			storageControllerSystem.getAdjacentDevices().add(storageSystem);
			storageControllerSystem.getAdjacentDevices().addAll(initFrontEndFCPort(storageControllerSystem));
			storageControllerSystem.getAdjacentDevices().addAll(initFrontEndEthernetPort(storageControllerSystem));
			
			result.add(storageControllerSystem);
		}
		iterator.close();
		return result;
	}

	private Set<FrontEndEthernetPort> initFrontEndEthernetPort(StorageControllerSystem storageControllerSystem) throws WBEMException {
		HashSet<FrontEndEthernetPort> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageControllerSystem.getInstance().getObjectPath(), "HuaSy_SystemDevice", "HuaSy_FrontEndEthernetPort", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			FrontEndEthernetPort frontEndEthernetPort = addDevice(FrontEndEthernetPort.class, iterator.next());
			frontEndEthernetPort.getAdjacentDevices().add(storageControllerSystem);
			result.add(frontEndEthernetPort);
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

	private Set<StorageEngineSystem> initStorageEngineSystem(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageEngineSystem> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HuaSy_ComponentCS", "HuaSy_StorageEngineSystem", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageEngineSystem storageEngineSystem = addDevice(StorageEngineSystem.class, iterator.next());
			storageEngineSystem.getAdjacentDevices().add(storageSystem);
			storageEngineSystem.getAdjacentDevices().addAll(initDiskExtent(storageEngineSystem));
			result.add(storageEngineSystem);
		}
		iterator.close();
		return result;
	}

	private Set<DiskExtent> initDiskExtent(StorageEngineSystem storageEngineSystem) throws WBEMException {
		HashSet<DiskExtent> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageEngineSystem.getInstance().getObjectPath(), "HuaSy_SystemDevice", "HuaSy_DiskExtent", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskExtent diskExtent = addDevice(DiskExtent.class, iterator.next());
			diskExtent.getAdjacentDevices().add(storageEngineSystem);
			result.add(diskExtent);
		}
		iterator.close();
		return result;
	}

	private Set<ConcreteStoragePool> initConcreteStoragePool(StorageSystem storageSystem) throws WBEMException {
		HashSet<ConcreteStoragePool> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HuaSy_HostedStoragePool", "Nex_ConcreteStoragePool", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			ConcreteStoragePool concreteStoragePool = addDevice(ConcreteStoragePool.class, iterator.next());
			concreteStoragePool.getAdjacentDevices().add(storageSystem);
			result.add(concreteStoragePool);
		}
		iterator.close();
		return result;
	}

	private Set<DiskStoragePool> initDiskStoragePool(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskStoragePool> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HuaSy_HostedStoragePool", "HuaSy_DiskStoragePool", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskStoragePool diskStoragePool = addDevice(DiskStoragePool.class, iterator.next());
			diskStoragePool.getAdjacentDevices().add(storageSystem);
			result.add(diskStoragePool);
		}
		iterator.close();
		return result;
	}
}
