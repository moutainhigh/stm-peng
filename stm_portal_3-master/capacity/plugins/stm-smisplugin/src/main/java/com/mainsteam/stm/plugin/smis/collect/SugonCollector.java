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
import com.mainsteam.stm.plugin.smis.device.BatteryComponent;
import com.mainsteam.stm.plugin.smis.device.Chassis;
import com.mainsteam.stm.plugin.smis.device.DiskDrive;
import com.mainsteam.stm.plugin.smis.device.DiskExtent;
import com.mainsteam.stm.plugin.smis.device.FCPort;
import com.mainsteam.stm.plugin.smis.device.Fan;
import com.mainsteam.stm.plugin.smis.device.ParentStoragePool;
import com.mainsteam.stm.plugin.smis.device.PowerSupply;
import com.mainsteam.stm.plugin.smis.device.PowerSupplyComponent;
import com.mainsteam.stm.plugin.smis.device.StoragePool;
import com.mainsteam.stm.plugin.smis.device.StorageProcessorSystem;
import com.mainsteam.stm.plugin.smis.device.StorageSystem;
import com.mainsteam.stm.plugin.smis.device.StorageVolume;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import com.mainsteam.stm.plugin.smis.object.SMISDevice;
/**
 * 曙光
 * @author yet
 *
 */
public class SugonCollector extends SMISCollector {
	public SugonCollector(SMISProvider provider) throws WBEMException {
        super(provider);
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
    }

    public SugonCollector(SMISProvider provider, String name) throws WBEMException, SMISException {
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
        storageSystem.getAdjacentDevices().addAll(initDiskExtent(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initChassis(storageSystem));
//        storageSystem.getAdjacentDevices().addAll(initFan(storageSystem));
        
        base = storageSystem;
    }
    
    private Set<Chassis> initChassis(StorageSystem storageSystem) throws WBEMException {
		HashSet<Chassis> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "LSISSI_ComputerSystemPackage", "LSISSI_Chassis", "Dependent", "Antecedent", true, null);
		while (iterator.hasNext()) {
			Chassis chassis = addDevice(Chassis.class, iterator.next());
			chassis.getAdjacentDevices().add(storageSystem);
			chassis.getAdjacentDevices().addAll(initBatteryComponent(chassis));
			chassis.getAdjacentDevices().addAll(initPowerSupplyComponent(chassis));
			result.add(chassis);
		}
		iterator.close();
		return result;
	}
    
    private Set<PowerSupplyComponent> initPowerSupplyComponent(Chassis chassis) throws WBEMException {
		HashSet<PowerSupplyComponent> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(chassis.getInstance().getObjectPath(), "LSISSI_PackageInChassis", "LSISSI_PowerSupplyComponent", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			PowerSupplyComponent powerSupplyComponent = addDevice(PowerSupplyComponent.class, iterator.next());
			powerSupplyComponent.getAdjacentDevices().add(chassis);
			powerSupplyComponent.getAdjacentDevices().addAll(initPowerSupply(powerSupplyComponent));
			result.add(powerSupplyComponent);
		}
		iterator.close();
		return result;
	}
    
    private Set<PowerSupply> initPowerSupply(PowerSupplyComponent powerSupplyComponent) throws WBEMException {
		HashSet<PowerSupply> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(powerSupplyComponent.getInstance().getObjectPath(), "LSISSI_ProductPhysicalComponent", "LSISSI_PowerSupplyProduct", "PartComponent", "GroupComponent", true, null);
		while (iterator.hasNext()) {
			PowerSupply powerSupply = addDevice(PowerSupply.class, iterator.next());
			powerSupply.getAdjacentDevices().add(powerSupplyComponent);
			result.add(powerSupply);
		}
		iterator.close();
		return result;
	}
    
    private Set<BatteryComponent> initBatteryComponent(Chassis chassis) throws WBEMException {
		HashSet<BatteryComponent> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(chassis.getInstance().getObjectPath(), "LSISSI_PackageInChassis", "LSISSI_BatteryComponent", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			BatteryComponent batteryComponent = addDevice(BatteryComponent.class, iterator.next());
			batteryComponent.getAdjacentDevices().add(chassis);
			batteryComponent.getAdjacentDevices().addAll(initBattery(batteryComponent));
			result.add(batteryComponent);
		}
		iterator.close();
		return result;
	}

    private Set<Battery> initBattery(BatteryComponent batteryComponent) throws WBEMException {
		HashSet<Battery> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(batteryComponent.getInstance().getObjectPath(), "LSISSI_Realizes", "LSISSI_Battery", "Antecedent", "Dependent", true, null);
		while (iterator.hasNext()) {
			Battery battery = addDevice(Battery.class, iterator.next());
			battery.getAdjacentDevices().add(batteryComponent);
			result.add(battery);
		}
		iterator.close();
		return result;
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
		while (iterator.hasNext()) {
			Fan fan = addDevice(Fan.class, iterator.next());
			fan.getAdjacentDevices().add(storageSystem);
			result.add(fan);
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
			result.add(storageProcessorSystem);
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
