package com.mainsteam.stm.plugin.smis.collect;

import com.mainsteam.stm.plugin.smis.device.*;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import org.apache.commons.lang.StringUtils;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import java.util.*;

public class ONTAPCollector extends SMISCollector {

    public ONTAPCollector(SMISProvider provider) throws WBEMException {
        super(provider);
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
    }

    public ONTAPCollector(SMISProvider provider, String name) throws WBEMException, SMISException {
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
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "ONTAP_StorageSystem", null);
        CloseableIterator<CIMInstance> storageSystemIterator = enumerateInstances(path, true, false, true, null);
        List<StorageSystem> result = new ArrayList<>();
        while (storageSystemIterator.hasNext()) {
            result.add(new StorageSystem(storageSystemIterator.next()));
        }
        storageSystemIterator.close();
        return result;
    }

    private void initStorageSystem(StorageSystem storageSystem) throws WBEMException {
        devicePool = new HashMap<>();
        addDevice(storageSystem.getClass(), storageSystem.getInstance());
        storageSystem.getAdjacentDevices().addAll(initNode(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initPrimordialStoragePool(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initConcreteStoragePool(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initStorageVolume(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initFlexVolExtent(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initConcreteExtent(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initDiskDrive(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initFCPort(storageSystem));
        storageSystem.getAdjacentDevices().addAll(initEthernetPort(storageSystem));
        base = storageSystem;
    }

    private Set<Node> initNode(StorageSystem storageSystem) throws WBEMException {
        HashSet<Node> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_StorageSystemNode", "ONTAP_Node", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            Node node = addDevice(Node.class, iterator.next());
            node.getAdjacentDevices().add(storageSystem);
            result.add(node);
        }
        iterator.close();
        return result;
    }

    private Set<PrimordialStoragePool> initPrimordialStoragePool(StorageSystem storageSystem) throws WBEMException {
        HashSet<PrimordialStoragePool> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_HostedPrimordialPool", "ONTAP_DiskPrimordialPool", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            PrimordialStoragePool primordialStoragePool = addDevice(PrimordialStoragePool.class, iterator.next());
            primordialStoragePool.getAdjacentDevices().add(storageSystem);
            result.add(primordialStoragePool);
        }
        iterator.close();
        return result;
    }

    private Set<ConcreteStoragePool> initConcreteStoragePool(StorageSystem storageSystem) throws WBEMException {
        HashSet<ConcreteStoragePool> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_HostedConcretePool", "ONTAP_ConcretePool", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            ConcreteStoragePool concreteStoragePool = addDevice(ConcreteStoragePool.class, iterator.next());
            concreteStoragePool.getAdjacentDevices().add(storageSystem);
            result.add(concreteStoragePool);
        }
        iterator.close();
        return result;
    }

    private Set<StorageVolume> initStorageVolume(StorageSystem storageSystem) throws WBEMException {
        HashSet<StorageVolume> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_StorageSystemVolume", "ONTAP_StorageVolume", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
            storageVolume.getAdjacentDevices().add(storageSystem);
            result.add(storageVolume);
        }
        iterator.close();
        return result;
    }

    private Set<FlexVolExtent> initFlexVolExtent(StorageSystem storageSystem) throws WBEMException {
        HashSet<FlexVolExtent> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_StorageSystemFlexVolExtent", "ONTAP_FlexVolExtent", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            FlexVolExtent flexVolExtent = addDevice(FlexVolExtent.class, iterator.next());
            flexVolExtent.getAdjacentDevices().add(storageSystem);
            result.add(flexVolExtent);
        }
        iterator.close();
        return result;
    }

    private Set<ConcreteExtent> initConcreteExtent(StorageSystem storageSystem) throws WBEMException {
        HashSet<ConcreteExtent> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_StorageSystemConcreteExtent", "ONTAP_ConcreteExtent", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            ConcreteExtent concreteExtent = addDevice(ConcreteExtent.class, iterator.next());
            concreteExtent.getAdjacentDevices().add(storageSystem);
            result.add(concreteExtent);
        }
        iterator.close();
        return result;
    }

    private Set<DiskDrive> initDiskDrive(StorageSystem storageSystem) throws WBEMException {
        HashSet<DiskDrive> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_StorageSystemDisk", "ONTAP_DiskDrive", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
            diskDrive.getAdjacentDevices().add(storageSystem);
            result.add(diskDrive);
        }
        iterator.close();
        return result;
    }

    private Set<FCPort> initFCPort(StorageSystem storageSystem) throws WBEMException {
        HashSet<FCPort> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_StorageSystemFCTargetPort", "ONTAP_FCTargetPort", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            FCPort fcPort = addDevice(FCPort.class, iterator.next());
            fcPort.getAdjacentDevices().add(storageSystem);
            result.add(fcPort);
        }
        iterator.close();
        return result;
    }

    private Set<EthernetPort> initEthernetPort(StorageSystem storageSystem) throws WBEMException {
        HashSet<EthernetPort> result = new HashSet<>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(storageSystem.getInstance().getObjectPath(), "ONTAP_StorageSystemEthernetPort", "ONTAP_EthernetPort", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            EthernetPort ethernetPort = addDevice(EthernetPort.class, iterator.next());
            ethernetPort.getAdjacentDevices().add(storageSystem);
            result.add(ethernetPort);
        }
        iterator.close();
        return result;
    }


}
