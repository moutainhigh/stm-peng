package com.mainsteam.stm.plugin.smis.collect;

import com.mainsteam.stm.plugin.smis.device.*;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import com.mainsteam.stm.plugin.smis.object.SMISDevice;
import org.apache.commons.lang.StringUtils;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import java.util.*;

public class DiskArrayCollector extends SMISCollector {

    public DiskArrayCollector(SMISProvider provider) throws WBEMException {
        super(provider);
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
    }

    public DiskArrayCollector(SMISProvider provider, String name) throws WBEMException, SMISException {
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
        CloseableIterator<CIMInstance> computerSystemIterator = enumerateInstances(path, true, false, false, null);
        List<StorageSystem> ret = new ArrayList<StorageSystem>();
        while (computerSystemIterator.hasNext()) {
            CIMInstance computerSystemInstance = computerSystemIterator.next();
            CloseableIterator<CIMInstance> storageSystemIterator = associatorInstances(computerSystemInstance.getObjectPath(), "CIM_ComponentCS",
                    "CIM_ComputerSystem", "PARTCOMPONENT", "GROUPCOMPONENT", false, null);
            while (storageSystemIterator.hasNext()) {
                ret.add(new StorageSystem(storageSystemIterator.next()));
            }
            storageSystemIterator.close();
        }
        computerSystemIterator.close();
        return ret;
    }

    public void initStorageSystem(StorageSystem storageSystem) throws WBEMException {
        devicePool.clear();
        addDevice(storageSystem.getClass(), storageSystem.getInstance());
        storageSystem.getAdjacentDevices().addAll(initStorageProcessorSystem(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStoragePool(storageSystem));
//        storageSystem.getAdjacentDevices().addAll(initLogicalDisk(storageSystem));
        base = storageSystem;
    }

    private Set<LogicalDisk> initLogicalDisk(StorageSystem storageSystem) throws WBEMException {
        HashSet<LogicalDisk> ret = new HashSet<LogicalDisk>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_LogicalDisk",
                "GroupComponent", "PartComponent", false, null);
        while (iterator.hasNext()) {
            LogicalDisk logicalDisk = addDevice(LogicalDisk.class, iterator.next());
            logicalDisk.getAdjacentDevices().add(storageSystem);
            ret.add(logicalDisk);
        }
        iterator.close();
        return ret;
    }

    private Set<StoragePool> initStoragePool(StorageSystem storageSystem) throws WBEMException {
        HashSet<StoragePool> ret = new HashSet<StoragePool>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_HostedStoragePool",
                "CIM_StoragePool", "GROUPCOMPONENT", "PARTCOMPONENT", false, null);
        while (iterator.hasNext()) {
            StoragePool storagePool = addDevice(StoragePool.class, iterator.next());
            storagePool.getAdjacentDevices().add(storageSystem);
            if (StringUtils.equalsIgnoreCase(storagePool.getPropertyValue("Primordial"), "true")) {
                storagePool.getAdjacentDevices().addAll(initStorageExtent(storagePool));
            } else {
                storagePool.getAdjacentDevices().addAll(initStorageVolume(storagePool));
            }
            ret.add(storagePool);
        }
        iterator.close();
        return ret;
    }

    private Set<StorageVolume> initStorageVolume(StoragePool storagePool) throws WBEMException {
        HashSet<StorageVolume> ret = new HashSet<StorageVolume>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storagePool.getInstance().getObjectPath(), "CIM_AllocatedFromStoragePool",
                "CIM_StorageVolume", "ANTECEDENT", "DEPENDENT", false, null);
        while (iterator.hasNext()) {
            StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
            storageVolume.getAdjacentDevices().add(storagePool);
            storageVolume.getAdjacentDevices().addAll(initStorageExtent(storageVolume));
            storageVolume.getAdjacentDevices().addAll(initSCSIProtocolController(storageVolume));
            ret.add(storageVolume);
        }
        iterator.close();
        return ret;
    }

    private Set<SCSIProtocolController> initSCSIProtocolController(StorageVolume storageVolume) throws WBEMException {
        HashSet<SCSIProtocolController> ret = new HashSet<SCSIProtocolController>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageVolume.getInstance().getObjectPath(), "CIM_ProtocolControllerForUnit",
                "CIM_SCSIProtocolController", "DEPENDENT", "ANTECEDENT", false, null);
        while (iterator.hasNext()) {
            SCSIProtocolController scsiProtocolController = addDevice(SCSIProtocolController.class, iterator.next());
            scsiProtocolController.getAdjacentDevices().add(storageVolume);
            ret.add(scsiProtocolController);
        }
        iterator.close();
        return ret;
    }

    private Collection<? extends SMISDevice> initStorageExtent(StorageVolume storageVolume) throws WBEMException {
        HashSet<StorageExtent> ret = new HashSet<StorageExtent>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageVolume.getInstance().getObjectPath(), "CIM_BasedOn", "CIM_StorageExtent",
                "DEPENDENT", "ANTECEDENT", false, null);
        while (iterator.hasNext()) {
            StorageExtent storageExtent = addDevice(StorageExtent.class, iterator.next());
            storageExtent.getAdjacentDevices().add(storageVolume);
            storageExtent.getAdjacentDevices().addAll(initDiskDrive(storageExtent));
            // storageExtent.getAdjacentDevices().addAll(initPhysicalMedia(storageExtent));
            ret.add(storageExtent);
        }
        iterator.close();
        return ret;
    }

    private Set<StorageExtent> initStorageExtent(StoragePool storagePool) throws WBEMException {
        HashSet<StorageExtent> ret = new HashSet<StorageExtent>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storagePool.getInstance().getObjectPath(), "CIM_ConcreteComponent", "CIM_StorageExtent",
                "GROUPCOMPONENT", "PARTCOMPONENT", false, null);
        while (iterator.hasNext()) {
            StorageExtent storageExtent = addDevice(StorageExtent.class, iterator.next());
            storageExtent.getAdjacentDevices().add(storagePool);
            storageExtent.getAdjacentDevices().addAll(initDiskDrive(storageExtent));
            // storageExtent.getAdjacentDevices().addAll(initPhysicalMedia(storageExtent));
            ret.add(storageExtent);
        }
        iterator.close();
        return ret;
    }

    private Set<PhysicalMedia> initPhysicalMedia(StorageExtent storageExtent) throws WBEMException {
        HashSet<PhysicalMedia> ret = new HashSet<PhysicalMedia>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageExtent.getInstance().getObjectPath(), "CIM_RealizesExtent", "CIM_PhysicalMedia",
                "DEPENDENT", "ANTECEDENT", false, null);
        while (iterator.hasNext()) {
            PhysicalMedia physicalMedia = addDevice(PhysicalMedia.class, iterator.next());
            physicalMedia.getAdjacentDevices().add(storageExtent);
            ret.add(physicalMedia);
        }
        iterator.close();
        return ret;
    }

    private Set<DiskDrive> initDiskDrive(StorageExtent storageExtent) throws WBEMException {
        HashSet<DiskDrive> ret = new HashSet<DiskDrive>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageExtent.getInstance().getObjectPath(), "CIM_MediaPresent", "CIM_DiskDrive",
                "DEPENDENT", "ANTECEDENT", false, null);
        while (iterator.hasNext()) {
            DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
            diskDrive.getAdjacentDevices().add(storageExtent);
            diskDrive.getAdjacentDevices().addAll(initPhysicalPackage(diskDrive));
            ret.add(diskDrive);
        }
        iterator.close();
        return ret;
    }

    private Set<PhysicalPackage> initPhysicalPackage(DiskDrive diskDrive) throws WBEMException {
        HashSet<PhysicalPackage> ret = new HashSet<PhysicalPackage>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(diskDrive.getInstance().getObjectPath(), "CIM_Realizes", "CIM_PhysicalPackage", "DEPENDENT",
                "ANTECEDENT", false, null);
        while (iterator.hasNext()) {
            PhysicalPackage physicalPackage = addDevice(PhysicalPackage.class, iterator.next());
            physicalPackage.getAdjacentDevices().add(diskDrive);
            ret.add(physicalPackage);
        }
        iterator.close();
        return ret;
    }

    private Set<StorageProcessorSystem> initStorageProcessorSystem(StorageSystem storageSystem) throws WBEMException {
        HashSet<StorageProcessorSystem> ret = new HashSet<StorageProcessorSystem>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_ComponentCS", "CIM_ComputerSystem",
                "GROUPCOMPONENT", "PARTCOMPONENT", false, null);
        while (iterator.hasNext()) {
            StorageProcessorSystem storageProcessorSystem = addDevice(StorageProcessorSystem.class, iterator.next());
            storageProcessorSystem.getAdjacentDevices().add(storageSystem);
            storageProcessorSystem.getAdjacentDevices().addAll(initFCPort(storageProcessorSystem));
            ret.add(storageProcessorSystem);
        }
        iterator.close();
        return ret;
    }

    private Set<FCPort> initFCPort(StorageProcessorSystem storageProcessorSystem) throws WBEMException {
        HashSet<FCPort> ret = new HashSet<FCPort>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageProcessorSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_FCPort",
                "GROUPCOMPONENT", "PARTCOMPONENT", false, null);
        while (iterator.hasNext()) {
            FCPort fcPort = addDevice(FCPort.class, iterator.next());
            fcPort.getAdjacentDevices().add(storageProcessorSystem);
            fcPort.getAdjacentDevices().addAll(initSCSIProtocolController(fcPort));
            ret.add(fcPort);
        }
        iterator.close();
        return ret;
    }

    private Set<SCSIProtocolController> initSCSIProtocolController(FCPort fcPort) throws WBEMException {
        HashSet<SCSIProtocolController> ret = new HashSet<SCSIProtocolController>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(fcPort.getInstance().getObjectPath(), "CIM_ProtocolControllerForPort",
                "CIM_SCSIProtocolController", "DEPENDENT", "ANTECEDENT", false, null);
        while (iterator.hasNext()) {
            SCSIProtocolController scsiProtocolController = addDevice(SCSIProtocolController.class, iterator.next());
            scsiProtocolController.getAdjacentDevices().add(fcPort);
            ret.add(scsiProtocolController);
        }
        iterator.close();
        return ret;
    }

}
