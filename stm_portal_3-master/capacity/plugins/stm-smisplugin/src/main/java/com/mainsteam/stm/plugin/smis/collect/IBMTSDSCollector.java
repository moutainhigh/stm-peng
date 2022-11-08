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
import com.mainsteam.stm.plugin.smis.device.FCPort;
import com.mainsteam.stm.plugin.smis.device.StorageSystem;
import com.mainsteam.stm.plugin.smis.device.StorageVolume;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
/**
 * IBMDS
 * @author yet
 * DS 8000系列
 * 
 */
public class IBMTSDSCollector extends SMISCollector {
	public IBMTSDSCollector(SMISProvider provider) throws WBEMException {
        super(provider);
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
    }

    public IBMTSDSCollector(SMISProvider provider, String name) throws WBEMException, SMISException {
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
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "IBMTSDS_StorageSystem", null);
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
        storageSystem.getAdjacentDevices().addAll(initFCPort(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskExtent(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
        base = storageSystem;
    }

	private Set<FCPort> initFCPort(StorageSystem storageSystem) throws WBEMException {
		HashSet<FCPort> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "IBMTSDS_FCPort", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			FCPort fcPort = addDevice(FCPort.class, iterator.next());
			fcPort.getAdjacentDevices().add(storageSystem);
			result.add(fcPort);
		}
		iterator.close();
		return result;
	}
    
	private Set<DiskDrive> initDiskDrive(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskDrive> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "IBMTSDS_DiskDrive", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
			diskDrive.getAdjacentDevices().add(storageSystem);
			result.add(diskDrive);
		}
		iterator.close();
		return result;
	}
	
	private Set<DiskExtent> initDiskExtent(StorageSystem storageSystem) throws WBEMException {
		HashSet<DiskExtent> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "IBMTSDS_DiskExtent", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			DiskExtent diskExtent = addDevice(DiskExtent.class, iterator.next());
			diskExtent.getAdjacentDevices().add(storageSystem);
			result.add(diskExtent);
		}
		iterator.close();
		return result;
	}

	private Set<StorageVolume> initStorageVolume(StorageSystem storageSystem) throws WBEMException {
		HashSet<StorageVolume> result = new HashSet<>();
		CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "IBMTSDS_Volume", "GroupComponent", "PartComponent", true, null);
		while (iterator.hasNext()) {
			StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
			storageVolume.getAdjacentDevices().add(storageSystem);
			result.add(storageVolume);
		}
		iterator.close();
		return result;
	}
}
