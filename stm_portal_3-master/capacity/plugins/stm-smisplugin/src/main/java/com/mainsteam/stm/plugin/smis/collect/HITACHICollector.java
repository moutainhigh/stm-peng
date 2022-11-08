package com.mainsteam.stm.plugin.smis.collect;

import com.mainsteam.stm.plugin.smis.device.*;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import org.apache.commons.lang.StringUtils;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import java.util.*;

public class HITACHICollector extends SMISCollector {

    public HITACHICollector(SMISProvider provider) throws WBEMException {
        super(provider);
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
    }

    public HITACHICollector(SMISProvider provider, String name) throws WBEMException, SMISException {
        this(provider);
        List<StorageSystem> storageSystems = discoveryStorageSystem();
        List<StorageSystem> baseSystems = new ArrayList<>();
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

    private List<StorageSystem> discoveryStorageSystem() throws WBEMException {
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "HITACHI_StorageSystem", null);
        CloseableIterator<CIMInstance> storageSystemIterator = enumerateInstances(path, true, false, false, null);
        List<StorageSystem> result = new ArrayList<>();
        while (storageSystemIterator.hasNext()) {
            result.add(new StorageSystem(storageSystemIterator.next()));
        }
        storageSystemIterator.close();
        return result;
    }

    private void initStorageSystem(StorageSystem storageSystem) throws WBEMException {
        devicePool.clear();
        addDevice(storageSystem.getClass(), storageSystem.getInstance());
        storageSystem.getAdjacentDevices().addAll(initStorageProcessorSystem(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initConcreteStoragePool(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initThinProvisioningPool(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
        base = storageSystem;
    }

    private Set<StorageProcessorSystem> initStorageProcessorSystem(StorageSystem storageSystem) throws WBEMException {
        HashSet<StorageProcessorSystem> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HITACHI_ComponentCS", "HITACHI_StorageProcessorSystem", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            StorageProcessorSystem storageProcessorSystem = addDevice(StorageProcessorSystem.class, iterator.next());
            storageProcessorSystem.getAdjacentDevices().add(storageSystem);
            storageProcessorSystem.getAdjacentDevices().addAll(initFCPort(storageProcessorSystem));
            result.add(storageProcessorSystem);
        }
        iterator.close();
        return result;
    }

    private Set<ConcreteStoragePool> initConcreteStoragePool(StorageSystem storageSystem) throws WBEMException {
        HashSet<ConcreteStoragePool> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HITACHI_HostedStoragePool", "HITACHI_StoragePool", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            ConcreteStoragePool concreteStoragePool = addDevice(ConcreteStoragePool.class, iterator.next());
            concreteStoragePool.getAdjacentDevices().add(storageSystem);
            result.add(concreteStoragePool);
        }
        iterator.close();
        return result;
    }

    private Set<ThinProvisioningPool> initThinProvisioningPool(StorageSystem storageSystem) throws WBEMException {
        HashSet<ThinProvisioningPool> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HITACHI_HostedThinProvisioningPool", "HITACHI_ThinProvisioningPool", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            ThinProvisioningPool thinProvisioningPool = addDevice(ThinProvisioningPool.class, iterator.next());
            thinProvisioningPool.getAdjacentDevices().add(storageSystem);
            result.add(thinProvisioningPool);
        }
        iterator.close();
        return result;
    }

    private Set<StorageVolume> initStorageVolume(StorageSystem storageSystem) throws WBEMException {
        HashSet<StorageVolume> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HITACHI_StorageSystemDeviceStorageVolume", "HITACHI_StorageVolume", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
            storageVolume.getAdjacentDevices().add(storageSystem);
            result.add(storageVolume);
        }
        iterator.close();
        return result;
    }

    private Set<DiskDrive> initDiskDrive(StorageSystem storageSystem) throws WBEMException {
        HashSet<DiskDrive> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "HITACHI_StorageSystemDeviceDiskDrive", "HITACHI_DiskDrive", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
            diskDrive.getAdjacentDevices().add(storageSystem);
            result.add(diskDrive);
        }
        iterator.close();
        return result;
    }

    private Set<FCPort> initFCPort(StorageProcessorSystem storageProcessorSystem) throws WBEMException {
        HashSet<FCPort> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageProcessorSystem.getInstance().getObjectPath(), "HITACHI_StorageProcessorSystemDeviceFCPort", "HITACHI_FCPort", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            FCPort fcPort = addDevice(FCPort.class, iterator.next());
            fcPort.getAdjacentDevices().add(storageProcessorSystem);
            result.add(fcPort);
        }
        iterator.close();
        return result;
    }

}
