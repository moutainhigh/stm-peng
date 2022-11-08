package com.mainsteam.stm.plugin.smis.collect;

import com.mainsteam.stm.plugin.smis.device.*;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import org.apache.commons.lang.StringUtils;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import java.util.*;

public class EMCCollector extends SMISCollector {

    public EMCCollector(SMISProvider provider) throws WBEMException {
        super(provider);
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
    }

    public EMCCollector(SMISProvider provider, String name) throws WBEMException, SMISException {
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
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "EMC_StorageSystem", null);
        CloseableIterator<CIMInstance> iterator = enumerateInstances(path, true, false, false, null);
        List<StorageSystem> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(new StorageSystem(iterator.next()));
        }
        iterator.close();
        return result;
    }

    private void initStorageSystem(StorageSystem storageSystem) throws WBEMException {
        devicePool.clear();
        addDevice(storageSystem.getClass(), storageSystem.getInstance());
        storageSystem.getAdjacentDevices().addAll(initStorageProcessorSystem(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStoragePool(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initPowerSupply(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initBattery(storageSystem));
        base = storageSystem;
    }

    private Set<StorageProcessorSystem> initStorageProcessorSystem(StorageSystem storageSystem) throws WBEMException {
        HashSet<StorageProcessorSystem> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "EMC_ComponentCS", "EMC_StorageProcessorSystem", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            StorageProcessorSystem storageProcessorSystem = addDevice(StorageProcessorSystem.class, iterator.next());
            storageProcessorSystem.getAdjacentDevices().add(storageSystem);
            storageProcessorSystem.getAdjacentDevices().addAll(initFrontEndFCPort(storageProcessorSystem));
            result.add(storageProcessorSystem);
        }
        iterator.close();
        return result;
    }

    private Set<StoragePool> initStoragePool(StorageSystem storageSystem) throws WBEMException {
        HashSet<StoragePool> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "EMC_HostedStoragePool", "EMC_StoragePool", "GroupComponent", "PartComponent", false, null);
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
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "EMC_SystemDevice", "EMC_StorageVolume", "GroupComponent", "PartComponent", false, null);
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
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "EMC_SystemDevice", "EMC_DiskDrive", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
            diskDrive.getAdjacentDevices().add(storageSystem);
            result.add(diskDrive);
        }
        iterator.close();
        return result;
    }

    private Set<PowerSupply> initPowerSupply(StorageSystem storageSystem) throws WBEMException {
        HashSet<PowerSupply> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "EMC_SystemDevice", "EMC_PowerSupply", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            PowerSupply powerSupply = addDevice(PowerSupply.class, iterator.next());
            powerSupply.getAdjacentDevices().add(storageSystem);
            result.add(powerSupply);
        }
        iterator.close();
        return result;
    }

    private Set<Battery> initBattery(StorageSystem storageSystem) throws WBEMException {
        HashSet<Battery> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "EMC_SystemDevice", "EMC_Battery", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            Battery battery = addDevice(Battery.class, iterator.next());
            battery.getAdjacentDevices().add(storageSystem);
            result.add(battery);
        }
        iterator.close();
        return result;
    }

    private Set<FrontEndFCPort> initFrontEndFCPort(StorageProcessorSystem storageProcessorSystem) throws WBEMException {
        HashSet<FrontEndFCPort> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageProcessorSystem.getInstance().getObjectPath(), "EMC_SystemDevice", "EMC_FrontEndFCPort", "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            FrontEndFCPort frontEndFCPort = addDevice(FrontEndFCPort.class, iterator.next());
            frontEndFCPort.getAdjacentDevices().add(storageProcessorSystem);
            result.add(frontEndFCPort);
        }
        iterator.close();
        return result;
    }

}
