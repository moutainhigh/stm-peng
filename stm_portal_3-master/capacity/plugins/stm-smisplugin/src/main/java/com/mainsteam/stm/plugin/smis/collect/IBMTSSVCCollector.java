package com.mainsteam.stm.plugin.smis.collect;

import com.mainsteam.stm.plugin.smis.device.*;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import com.mainsteam.stm.plugin.smis.object.SMISDevice;
import org.sblim.cimclient.WBEMConfigurationProperties;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import java.util.*;

public class IBMTSSVCCollector extends SMISCollector {

    public IBMTSSVCCollector(SMISProvider provider) throws WBEMException, SMISException {
        super(provider);
        client.setProperty(WBEMConfigurationProperties.SSL_CLIENT_PROTOCOL, "TLSv1.2");
        client.initialize(getInitPath(), getSubject(), new Locale[]{Locale.US});
        List<Cluster> clusters = discoveryCluster();
        if (clusters.size() < 1) {
            throw new SMISException("More than one Cluster.");
        }
        initCluster(clusters.get(0));
    }

    public IBMTSSVCCollector(SMISProvider provider, String name) throws WBEMException, SMISException {
        this(provider);
    }

    public List<Cluster> discoveryCluster() throws WBEMException {
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), provider.getNameSpace(), "IBMTSSVC_Cluster", null);
        CloseableIterator<CIMInstance> clusterIterator = enumerateInstances(path, true, false, true, null);
        List<Cluster> ret = new ArrayList<Cluster>();
        while (clusterIterator.hasNext()) {
            ret.add(new Cluster(clusterIterator.next()));
        }
        clusterIterator.close();
        return ret;
    }

    public void initCluster(Cluster cluster) throws WBEMException {
        devicePool.clear();
        addDevice(cluster.getClass(), cluster.getInstance());
        cluster.getAdjacentDevices().addAll(initNode(cluster));
        cluster.getAdjacentDevices().addAll(initPrimordialStoragePool(cluster));
        cluster.getAdjacentDevices().addAll(initDiskDrive(cluster));
        base = cluster;
    }

    private Set<Node> initNode(Cluster cluster) throws WBEMException {
        HashSet<Node> ret = new HashSet<Node>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(cluster.getInstance().getObjectPath(), "CIM_ComponentCS", "IBMTSSVC_Node", "GroupComponent",
                "PartComponent", true, null);
        while (iterator.hasNext()) {
            Node node = addDevice(Node.class, iterator.next());
            node.getAdjacentDevices().add(cluster);
            node.getAdjacentDevices().addAll(initFCPort(node));
            ret.add(node);
        }
        iterator.close();
        return ret;
    }

    private Set<FCPort> initFCPort(Node node) throws WBEMException {
        HashSet<FCPort> ret = new HashSet<FCPort>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(node.getInstance().getObjectPath(), "IBMTSSVC_SystemFCPort", "IBMTSSVC_FCPort",
                "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            FCPort fcPort = addDevice(FCPort.class, iterator.next());
            node.getAdjacentDevices().add(node);
            ret.add(fcPort);
        }
        iterator.close();
        return ret;
    }

    private Set<PrimordialStoragePool> initPrimordialStoragePool(Cluster cluster) throws WBEMException {
        HashSet<PrimordialStoragePool> ret = new HashSet<PrimordialStoragePool>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(cluster.getInstance().getObjectPath(), "IBMTSSVC_HostedPrimordialPool",
                "IBMTSSVC_PrimordialStoragePool", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            PrimordialStoragePool primordialStoragePool = addDevice(PrimordialStoragePool.class, iterator.next());
            primordialStoragePool.getAdjacentDevices().add(cluster);
            primordialStoragePool.getAdjacentDevices().addAll(initConcreteStoragePool(primordialStoragePool));
            ret.add(primordialStoragePool);
        }
        iterator.close();
        return ret;
    }

    private Set<ConcreteStoragePool> initConcreteStoragePool(PrimordialStoragePool primordialStoragePool) throws WBEMException {
        HashSet<ConcreteStoragePool> ret = new HashSet<ConcreteStoragePool>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(primordialStoragePool.getInstance().getObjectPath(), "IBMTSSVC_AllocatedFromPrimordialPool",
                "IBMTSSVC_ConcreteStoragePool", "Antecedent", "Dependent", true, null);
        while (iterator.hasNext()) {
            ConcreteStoragePool concreteStoragePool = addDevice(ConcreteStoragePool.class, iterator.next());
            concreteStoragePool.getAdjacentDevices().add(primordialStoragePool);
            concreteStoragePool.getAdjacentDevices().addAll(initStorageVolume(concreteStoragePool));
            concreteStoragePool.getAdjacentDevices().addAll(initBackendVolume(concreteStoragePool));
            ret.add(concreteStoragePool);
        }
        iterator.close();
        return ret;
    }

    private Set<BackendVolume> initBackendVolume(ConcreteStoragePool concreteStoragePool) throws WBEMException {
        HashSet<BackendVolume> ret = new HashSet<BackendVolume>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(concreteStoragePool.getInstance().getObjectPath(), "IBMTSSVC_StoragePoolComponent",
                "IBMTSSVC_BackendVolume", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            BackendVolume backendVolume = addDevice(BackendVolume.class, iterator.next());
            backendVolume.getAdjacentDevices().add(concreteStoragePool);
            backendVolume.getAdjacentDevices().addAll(initArray(backendVolume));
            ret.add(backendVolume);
        }
        iterator.close();
        return ret;
    }

    private Set<StorageVolume> initStorageVolume(ConcreteStoragePool concreteStoragePool) throws WBEMException {
        HashSet<StorageVolume> ret = new HashSet<StorageVolume>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(concreteStoragePool.getInstance().getObjectPath(), "IBMTSSVC_AllocatedFromConcretePool",
                "IBMTSSVC_StorageVolume", "Antecedent", "Dependent", true, null);
        while (iterator.hasNext()) {
            StorageVolume storageVolume = addDevice(StorageVolume.class, iterator.next());
            storageVolume.getAdjacentDevices().add(concreteStoragePool);
            ret.add(storageVolume);
        }
        iterator.close();
        return ret;
    }

    private Set<Array> initArray(BackendVolume backendVolume) throws WBEMException {
        HashSet<Array> ret = new HashSet<Array>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(backendVolume.getInstance().getObjectPath(), "IBMTSSVC_ArrayIsABackendVolume",
                "IBMTSSVC_Array", "SystemElement", "SameElement", true, null);
        while (iterator.hasNext()) {
            Array array = addDevice(Array.class, iterator.next());
            array.getAdjacentDevices().add(backendVolume);
            array.getAdjacentDevices().addAll(initDiskDriveExtent(array));
            ret.add(array);
        }
        iterator.close();
        return ret;
    }

    private Set<DiskDriveExtent> initDiskDriveExtent(Array array) throws WBEMException {
        HashSet<DiskDriveExtent> ret = new HashSet<DiskDriveExtent>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(array.getInstance().getObjectPath(), "IBMTSSVC_ArrayBasedOnDiskDrive",
                "IBMTSSVC_DiskDriveExtent", "Dependent", "Antecedent", true, null);
        while (iterator.hasNext()) {
            DiskDriveExtent diskDriveExtent = addDevice(DiskDriveExtent.class, iterator.next());
            diskDriveExtent.getAdjacentDevices().add(array);
            diskDriveExtent.getAdjacentDevices().addAll(initDiskDrive(diskDriveExtent));
            ret.add(diskDriveExtent);
        }
        iterator.close();
        return ret;
    }

    private Set<DiskDrive> initDiskDrive(DiskDriveExtent diskDriveExtent) throws WBEMException {
        HashSet<DiskDrive> ret = new HashSet<DiskDrive>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(diskDriveExtent.getInstance().getObjectPath(), "IBMTSSVC_DiskDrivePresent",
                "IBMTSSVC_DiskDrive", "Dependent", "Antecedent", true, null);
        while (iterator.hasNext()) {
            DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
            diskDrive.getAdjacentDevices().add(diskDriveExtent);
            ret.add(diskDrive);
        }
        iterator.close();
        return ret;
    }

    private Set<DiskDrive> initDiskDrive(Cluster cluster) throws WBEMException {
        HashSet<DiskDrive> ret = new HashSet<DiskDrive>();
        CloseableIterator<CIMInstance> iterator = associatorInstances(cluster.getInstance().getObjectPath(), "IBMTSSVC_StorageSystemToDiskDrive",
                "IBMTSSVC_DiskDrive", "GroupComponent", "PartComponent", true, null);
        while (iterator.hasNext()) {
            DiskDrive diskDrive = addDevice(DiskDrive.class, iterator.next());
            diskDrive.getAdjacentDevices().add(cluster);
            ret.add(diskDrive);
        }
        iterator.close();
        return ret;
    }
}
