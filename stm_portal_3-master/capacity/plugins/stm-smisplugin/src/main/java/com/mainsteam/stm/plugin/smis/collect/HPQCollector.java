package com.mainsteam.stm.plugin.smis.collect;

import com.mainsteam.stm.plugin.smis.device.*;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import java.util.*;

public class HPQCollector extends SMISCollector {
    Log log = LogFactory.getLog(HPQCollector.class);

    public HPQCollector(SMISProvider provider) throws WBEMException {
        super(provider);
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
    }

    public HPQCollector(SMISProvider provider, String name) throws WBEMException, SMISException {
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
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "DHS_TopComputerSystem", null);
        CloseableIterator<CIMInstance> computerSystemIterator = enumerateInstances(path, true, false, true, null);
        List<StorageSystem> ret = new ArrayList<StorageSystem>();
        while (computerSystemIterator.hasNext()) {
            CIMInstance computerSystemInstance = computerSystemIterator.next();
            ret.add(new StorageSystem(computerSystemInstance));
        }
        computerSystemIterator.close();
        return ret;
    }

    public void initStorageSystem(StorageSystem storageSystem) throws WBEMException {
        devicePool.clear();
        addDevice(storageSystem.getClass(), storageSystem.getInstance());
        storageSystem.getAdjacentDevices().addAll(initStorageProcessorSystem(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStoragePool(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initFan(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initPower(storageSystem));
        base = storageSystem;
    }

    protected Set<StoragePool> initStoragePool(StorageSystem storageSystem) throws WBEMException {
        HashSet<StoragePool> ret = new HashSet<StoragePool>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_HostedStoragePool", "DHS_StoragePool",
                "GROUPCOMPONENT", "PARTCOMPONENT", true, null);
        while (iterator.hasNext()) {
            StoragePool storagePool = addDevice(StoragePool.class, iterator.next());
            storagePool.getAdjacentDevices().add(storageSystem);
            ret.add(storagePool);
        }
        iterator.close();
        return ret;
    }

    protected Set<StorageVolume> initStorageVolume(StorageSystem storageSystem) throws WBEMException {
        HashSet<StorageVolume> ret = new HashSet<StorageVolume>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice",
                "CIM_StorageVolume", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
            storageVolume.getAdjacentDevices().add(storageSystem);
            ret.add(storageVolume);
        }
        iterator.close();
        return ret;
    }

    protected Set<DiskDrive> initDiskDrive(StorageSystem storageSystem) throws WBEMException {
        HashSet<DiskDrive> ret = new HashSet<DiskDrive>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "DHS_DiskDrive",
                "GROUPCOMPONENT", "PARTCOMPONENT", true, null);
        while (iterator.hasNext()) {
            DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
            diskDrive.getAdjacentDevices().add(storageSystem);
            ret.add(diskDrive);
        }
        iterator.close();
        return ret;
    }

    protected Set<StorageProcessorSystem> initStorageProcessorSystem(StorageSystem storageSystem) throws WBEMException {
        HashSet<StorageProcessorSystem> ret = new HashSet<StorageProcessorSystem>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_ComponentCS", "CIM_ComputerSystem",
                "GROUPCOMPONENT", "PARTCOMPONENT", true, null);
        while (iterator.hasNext()) {
            StorageProcessorSystem storageProcessorSystem = addDevice(StorageProcessorSystem.class, iterator.next());
            storageProcessorSystem.getAdjacentDevices().add(storageSystem);
            storageProcessorSystem.getAdjacentDevices().addAll(initFCPort(storageProcessorSystem));
            ret.add(storageProcessorSystem);
        }
        iterator.close();
        return ret;
    }

    protected Set<Fan> initFan(StorageSystem storageSystem) throws WBEMException {
        HashSet<Fan> ret = new HashSet<Fan>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_Fan",
                "GROUPCOMPONENT", "PARTCOMPONENT", true, null);
        while (iterator.hasNext()) {
            Fan fan = addDevice(Fan.class, iterator.next());
            fan.getAdjacentDevices().add(storageSystem);
            ret.add(fan);
        }
        iterator.close();
        return ret;
    }

    protected Set<Power> initPower(StorageSystem storageSystem) throws WBEMException {
        HashSet<Power> ret = new HashSet<Power>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_PowerSupply",
                "GROUPCOMPONENT", "PARTCOMPONENT", true, null);
        while (iterator.hasNext()) {
            Power power = addDevice(Power.class, iterator.next());
            power.getAdjacentDevices().add(storageSystem);
            ret.add(power);
        }
        iterator.close();
        return ret;
    }

    protected Set<FCPort> initFCPort(StorageProcessorSystem storageProcessorSystem) throws WBEMException {
        HashSet<FCPort> ret = new HashSet<FCPort>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageProcessorSystem.getInstance().getObjectPath(), "CIM_SystemDevice", "CIM_SASPort",
                "GROUPCOMPONENT", "PARTCOMPONENT", true, null);
        while (iterator.hasNext()) {
            FCPort fcPort = addDevice(FCPort.class, iterator.next());
            fcPort.getAdjacentDevices().add(storageProcessorSystem);
            ret.add(fcPort);
        }
        iterator.close();
        return ret;
    }

}
