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

import com.mainsteam.stm.plugin.smis.device.Battery;
import com.mainsteam.stm.plugin.smis.device.DiskDrive;
import com.mainsteam.stm.plugin.smis.device.EthernetPort;
import com.mainsteam.stm.plugin.smis.device.FCPort;
import com.mainsteam.stm.plugin.smis.device.Fan;
import com.mainsteam.stm.plugin.smis.device.PowerSupply;
import com.mainsteam.stm.plugin.smis.device.StorageExtent;
import com.mainsteam.stm.plugin.smis.device.StoragePool;
import com.mainsteam.stm.plugin.smis.device.StorageProcessorSystem;
import com.mainsteam.stm.plugin.smis.device.StorageSystem;
import com.mainsteam.stm.plugin.smis.device.StorageVolume;
import com.mainsteam.stm.plugin.smis.exception.SMISException;

/**
 * 宏衫存储
 * 以下以MG3000G2-标准版开发
 * @author yet
 *
 */
public class MacroSANCollector extends SMISCollector{

	public MacroSANCollector(SMISProvider provider) throws WBEMException {
		super(provider);
		client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
	}
	
	public MacroSANCollector(SMISProvider provider, String name) throws WBEMException, SMISException{
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
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "CIM_ComputerSystem", null);
        CloseableIterator<CIMInstance> computerSystemIterator = enumerateInstances(path, true, false, true, null);
        List<StorageSystem> ret = new ArrayList<StorageSystem>();
        while (computerSystemIterator.hasNext()) {
            ret.add(new StorageSystem(computerSystemIterator.next()));
        }
        computerSystemIterator.close();
        return ret;
    }
	
	private void initStorageSystem(StorageSystem storageSystem) throws WBEMException {
		devicePool.clear();
		addDevice(storageSystem.getClass(), storageSystem.getInstance());
		storageSystem.getAdjacentDevices().addAll(initStorageProcessorSystem(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initStorageExtent(storageSystem));
//		storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initEthernetPort(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initFCPort(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initPowerSupply(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initFan(storageSystem));
		storageSystem.getAdjacentDevices().addAll(initBattery(storageSystem));
		base=storageSystem;
	}

	private Set<StorageProcessorSystem> initStorageProcessorSystem(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageProcessorSystem> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_ComponentCS", "CIM_ComputerSystem", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageProcessorSystem storageProcessorSystem = addDevice(StorageProcessorSystem.class, iterator.next());
			storageProcessorSystem.getAdjacentDevices().add(storageSystem);
			result.add(storageProcessorSystem);
		}
		iterator.close();
		return result;
	}
	
	private Set<DiskDrive> initDiskDrive(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskDrive> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "MacroSAN_DiskDrive", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
			diskDrive.getAdjacentDevices().add(storageSystem);
			result.add(diskDrive);
		}
		iterator.close();
		return result;
	}
	
	/**
	 * CIM_StorageExtent中有具体磁盘的容量相关数据，此处与MacroSAN_DiskDrive没有对的上的索引，但能一一对应上
	 * 有CIM_StorageVolume数据
	 * @param storageSystem
	 * @return
	 * @throws WBEMException
	 */
	private Set<StorageExtent> initStorageExtent(StorageSystem storageSystem) throws WBEMException {
        HashSet<StorageExtent> result = new HashSet<StorageExtent>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_StorageExtent","GROUPCOMPONENT", "PARTCOMPONENT", true, null);
        while (iterator.hasNext()) {
            StorageExtent storageExtent = addDevice(StorageExtent.class, iterator.next());
            storageExtent.getAdjacentDevices().add(storageSystem);
            result.add(storageExtent);
        }
        iterator.close();
        return result;
    }
	
	private Set<StorageVolume> initStorageVolume(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageVolume> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "MacroSAN_StorageVolume", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
			storageVolume.getAdjacentDevices().add(storageSystem);
			result.add(storageVolume);
		}
		iterator.close();
		return result;
	}
	
	private Set<EthernetPort> initEthernetPort(StorageSystem storageSystem) throws WBEMException {
        HashSet<EthernetPort> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_EthernetPort", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            EthernetPort ethernetPort = addDevice(EthernetPort.class, iterator.next());
            ethernetPort.getAdjacentDevices().add(storageSystem);
            result.add(ethernetPort);
        }
        iterator.close();
        return result;
    }
	
	private Set<FCPort> initFCPort(StorageSystem storageSystem) throws WBEMException {
		HashSet<FCPort> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_FCPort", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			FCPort fcPort = addDevice(FCPort.class, iterator.next());
			fcPort.getAdjacentDevices().add(storageSystem);
			result.add(fcPort);
		}
		iterator.close();
		return result;
	}
	
	private Set<PowerSupply> initPowerSupply(StorageSystem storageSystem) throws WBEMException {
		HashSet<PowerSupply> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_PowerSupply", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			PowerSupply powerSupply = addDevice(PowerSupply.class, iterator.next());
			powerSupply.getAdjacentDevices().add(storageSystem);
			result.add(powerSupply);
		}
		iterator.close();
		return result;
	}
	
	private Set<Fan> initFan(StorageSystem storageSystem) throws WBEMException {
		HashSet<Fan> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_Fan", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			Fan fan = addDevice(Fan.class, iterator.next());
			fan.getAdjacentDevices().add(storageSystem);
			result.add(fan);
		}
		iterator.close();
		return result;
	}
	
	private Set<Battery> initBattery(StorageSystem storageSystem) throws WBEMException {
		HashSet<Battery> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "MacroSAN_Battery", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			Battery battery = addDevice(Battery.class, iterator.next());
			battery.getAdjacentDevices().add(storageSystem);
			result.add(battery);
		}
		iterator.close();
		return result;
	}
}
