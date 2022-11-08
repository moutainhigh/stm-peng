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

import com.mainsteam.stm.plugin.smis.device.Battery;
import com.mainsteam.stm.plugin.smis.device.DiskDrive;
import com.mainsteam.stm.plugin.smis.device.DiskExtent;
import com.mainsteam.stm.plugin.smis.device.FCPort;
import com.mainsteam.stm.plugin.smis.device.Fan;
import com.mainsteam.stm.plugin.smis.device.ParentStoragePool;
import com.mainsteam.stm.plugin.smis.device.PowerSupply;
import com.mainsteam.stm.plugin.smis.device.StoragePool;
import com.mainsteam.stm.plugin.smis.device.StorageProcessorSystem;
import com.mainsteam.stm.plugin.smis.device.StorageSystem;
import com.mainsteam.stm.plugin.smis.device.StorageVolume;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import com.mainsteam.stm.plugin.smis.object.SMISDevice;
/**
 * IBMDS
 * @author yet
 *
 */
public class IBMLSICollector extends SMISCollector {
	public IBMLSICollector(SMISProvider provider) throws WBEMException {
        super(provider);
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
    }

    public IBMLSICollector(SMISProvider provider, String name) throws WBEMException, SMISException {
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
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "LSISSI_StorageSystem", null);
        CloseableIterator<CIMInstance> computerSystemIterator = enumerateInstances(path, true, false, true, null);
        List<StorageSystem> ret = new ArrayList<StorageSystem>();
        while (computerSystemIterator.hasNext()) {
            ret.add(new StorageSystem(computerSystemIterator.next()));
        }
        computerSystemIterator.close();
        return ret;
    }

    public void initStorageSystem(StorageSystem storageSystem) throws WBEMException {
        devicePool.clear();
        addDevice(storageSystem.getClass(), storageSystem.getInstance());
        storageSystem.getAdjacentDevices().addAll(initStorageProcessorSystem(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initParentStoragePool(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initPowerSupply(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initFan(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskExtent(storageSystem));
        base = storageSystem;
    }

	private Set<DiskExtent> initDiskExtent(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskExtent> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "LSISSI_StorageSystemDevice", "LSISSI_DiskExtent", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskExtent diskExtent = addDevice(DiskExtent.class, iterator.next());
			diskExtent.getAdjacentDevices().add(storageSystem);
			result.add(diskExtent);
		}
		iterator.close();
		return result;
	}

	private Set<Fan> initFan(StorageSystem storageSystem) throws WBEMException {
		HashSet<Fan> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "LSISSI_StorageSystemDevice", "LSISSI_Fan", "GroupComponent", "PartComponent", true, null);
		if (iterator == null) {//判断当关联类或被关联到的功能类找不到时，直接返回空数据结果.这样可防止某个关联类或功能类发现不到而导致资源发现失败问题，这两者任意一个发现不到只会表示为相关子资源没有而已。下列类似表示方式意义相同
			return result;
		}
		while (iterator.hasNext()) {
			Fan fan = addDevice(Fan.class, iterator.next());
			fan.getAdjacentDevices().add(storageSystem);
			result.add(fan);
		}
		iterator.close();
		return result;
	}

	private Set<PowerSupply> initPowerSupply(StorageSystem storageSystem) throws WBEMException {
		HashSet<PowerSupply> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "LSISSI_StorageSystemDevice", "LSISSI_PowerSupply", "GroupComponent", "PartComponent", true, null);
		if (iterator == null) {
			return result;
		}
		while (iterator.hasNext()) {
			PowerSupply powerSupply = addDevice(PowerSupply.class, iterator.next());
			powerSupply.getAdjacentDevices().add(storageSystem);
			result.add(powerSupply);
		}
		iterator.close();
		return result;
	}

	private Set<DiskDrive> initDiskDrive(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskDrive> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "LSISSI_StorageSystemDevice", "LSISSI_DiskDrive", "GroupComponent", "PartComponent", true, null);
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
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "LSISSI_StorageSystemDevice", "LSISSI_StorageVolume", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
			storageVolume.getAdjacentDevices().add(storageSystem);
			result.add(storageVolume);
		}
		iterator.close();
		return result;
	}

	private Set<ParentStoragePool> initParentStoragePool(StorageSystem storageSystem) throws WBEMException {
		HashSet<ParentStoragePool> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "LSISSI_HostedStoragePool", "LSISSI_ParentStoragePool", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			ParentStoragePool parentStoragePool = addDevice(ParentStoragePool.class, iterator.next());
			parentStoragePool.getAdjacentDevices().add(storageSystem);
			parentStoragePool.getAdjacentDevices().addAll(initStoragePool(parentStoragePool));
			result.add(parentStoragePool);
		}
		iterator.close();
		return result;
	}

	private Set<StoragePool> initStoragePool(ParentStoragePool parentStoragePool) throws WBEMException {
		HashSet<StoragePool> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(parentStoragePool.getInstance().getObjectPath(), "LSISSI_AllocatedFromStoragePool", "LSISSI_StoragePool", "Antecedent", "Dependent", true, null);
		while (iterator.hasNext()) {
			StoragePool storagePool = addDevice(StoragePool.class, iterator.next());
			storagePool.getAdjacentDevices().add(parentStoragePool);
			result.add(storagePool);
		}
		iterator.close();
		return result;
	}

	private Set<StorageProcessorSystem> initStorageProcessorSystem(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageProcessorSystem> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "LSISSI_ComponentCS", "LSISSI_StorageProcessorSystem", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageProcessorSystem storageProcessorSystem = addDevice(StorageProcessorSystem.class, iterator.next());
			storageProcessorSystem.getAdjacentDevices().add(storageSystem);
			storageProcessorSystem.getAdjacentDevices().addAll(initFCPort(storageProcessorSystem));
			storageProcessorSystem.getAdjacentDevices().addAll(initBattery(storageProcessorSystem));
			result.add(storageProcessorSystem);
		}
		iterator.close();
		return result;
	}

	private Set<Battery> initBattery(StorageProcessorSystem storageProcessorSystem) throws WBEMException {
		HashSet<Battery> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageProcessorSystem.getInstance().getObjectPath(), "LSISSI_AssociatedBattery", "LSISSI_Battery", "Dependent", "Antecedent", true, null);
		if (iterator == null) {
			return result;
		}
		while (iterator.hasNext()) {
			Battery battery = addDevice(Battery.class, iterator.next());
			battery.getAdjacentDevices().add(storageProcessorSystem);
			result.add(battery);
		}
		iterator.close();
		return result;
	}
	
	private Set<FCPort> initFCPort(StorageProcessorSystem storageProcessorSystem) throws WBEMException {
		HashSet<FCPort> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageProcessorSystem.getInstance().getObjectPath(), "LSISSI_StorageProcessorDevice", "LSISSI_FCPort", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			FCPort fcPort = addDevice(FCPort.class, iterator.next());
			fcPort.getAdjacentDevices().add(storageProcessorSystem);
			result.add(fcPort);
		}
		iterator.close();
		return result;
	}
}
