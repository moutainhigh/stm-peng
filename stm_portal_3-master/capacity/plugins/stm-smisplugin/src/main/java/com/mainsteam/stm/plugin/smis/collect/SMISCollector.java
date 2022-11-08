package com.mainsteam.stm.plugin.smis.collect;

import com.mainsteam.stm.plugin.smis.capabilities.StatisticsCapabilities;
import com.mainsteam.stm.plugin.smis.data.StatisticalData;
import com.mainsteam.stm.plugin.smis.object.SMISConstraint;
import com.mainsteam.stm.plugin.smis.object.SMISDevice;
import com.mainsteam.stm.plugin.smis.service.StatisticsService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.*;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;

public abstract class SMISCollector {

    private static final Log LOGGER = LogFactory.getLog(SMISCollector.class);

    protected SMISProvider provider;
    protected WBEMClient client;
    protected Map<CIMObjectPath, SMISDevice> devicePool = new HashMap<>();
    protected SMISDevice base;

    protected SMISCollector(SMISProvider provider) throws WBEMException {
        this.provider = provider;
        client = WBEMClientFactory.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
    }

    protected Subject getSubject() {
        Subject subject = new Subject();
        subject.getPrincipals().add(new UserPrincipal(provider.getUsername()));
        subject.getPrivateCredentials().add(new PasswordCredential(provider.getPassword()));
        return subject;
    }

    protected CIMObjectPath getInitPath() {
        CIMObjectPath path = new CIMObjectPath(provider.getProtocol(), provider.getIp(), provider.getPort(), null, null, null);
        return path;
    }

    public SMISDevice getBase() throws WBEMException {
        updateDevice(base);
        return base;
    }

    public List<SMISDevice> getDevice(String type) throws ClassNotFoundException, WBEMException {
        ArrayList<SMISDevice> result = new ArrayList<SMISDevice>();
        Class<?> clazz = Class.forName("com.mainsteam.stm.plugin.smis.device." + type);
        for (Entry<CIMObjectPath, SMISDevice> entry : devicePool.entrySet()) {
            SMISDevice device = entry.getValue();
            if (clazz.isInstance(device)) {
                result.add(device);
            }
        }
        updateDevice(result.toArray(new SMISDevice[result.size()]));
        return result;
    }

    private void updateDevice(SMISDevice... devices) throws WBEMException {
        HashMap<CIMObjectPath, SMISDevice> updateDevices = new HashMap<>();
        HashSet<CIMObjectPath> uniquePathSet = new HashSet<>();
        for (SMISDevice device : devices) {
            CIMObjectPath path = device.getInstance().getObjectPath();
            updateDevices.put(path, device);
            CIMObjectPath uniquePath = new CIMObjectPath(path.getScheme(), path.getHost(), path.getPort(), path.getNamespace(), path.getObjectName(), null);
            uniquePathSet.add(uniquePath);
        }
        StringBuilder sb = new StringBuilder();
        if (LOGGER.isDebugEnabled()) {
            int count = 0;
            for (CIMObjectPath path : uniquePathSet) {
                sb.append(System.lineSeparator()).append(path.toString());
                if (++count >= 20)
                    break;
            }
            LOGGER.debug("update device, size = " + devices.length + ", uniquePath = " + uniquePathSet.size() + " :" + sb);
        }
        for (CIMObjectPath path : uniquePathSet) {
            CloseableIterator<CIMInstance> iterator = enumerateInstances(path, true, false, false, null);
            while (iterator.hasNext()) {
                CIMInstance instance = iterator.next();
                CIMObjectPath instancePath = instance.getObjectPath();
                SMISDevice device = updateDevices.get(instancePath);
                if (device != null) {
                    device.setInstance(instance);
                    updateDevices.remove(instancePath);
                }
            }
            iterator.close();
        }
        sb.setLength(0);
        for (SMISDevice device : updateDevices.values()) {
            CIMInstance instance = device.getInstance();
            device.setInstance(new CIMInstance(instance.getObjectPath(), null));
            if (LOGGER.isDebugEnabled())
                sb.append(System.lineSeparator()).append(device.getInstance().getObjectPath());
        }
        if (updateDevices.size() > 0 && LOGGER.isDebugEnabled())
            LOGGER.debug("Unable to update " + updateDevices.size() + " :" + sb);
    }

    public List<SMISDevice> getDevice(String type, String constraintString) throws ClassNotFoundException, WBEMException {
        if (constraintString == null)
            return getDevice(type);
        ArrayList<SMISDevice> ret = new ArrayList<>();
        ArrayList<SMISConstraint> constraints = new ArrayList<>();
        for (String cString : constraintString.split(",")) {
            int equalIndex = cString.indexOf("=");
            constraints.add(new SMISConstraint(cString.substring(0, equalIndex).trim(), cString.substring(equalIndex + 1).trim()));
        }
        for (SMISDevice device : getDevice(type)) {
            boolean flag = true;
            for (SMISConstraint constraint : constraints)
                if (!StringUtils.equalsIgnoreCase(device.getPropertyValue(constraint.key), constraint.value)) {
                    flag = false;
                    break;
                }
            if (flag)
                ret.add(device);
        }
        return ret;
    }

    public StatisticalData getStatisticalData(SMISDevice device) throws WBEMException {
        CloseableIterator<CIMInstance> iterator = associatorInstances(device.getInstance().getObjectPath(), "CIM_ElementStatisticalData",
                "CIM_BlockStorageStatisticalData", "ManagedElement", "Stats", false, null);
        while (iterator.hasNext()) {
            return new StatisticalData(iterator.next());
        }
        iterator.close();
        return null;
    }

    public StatisticsService getStatisticsService(SMISDevice device) throws WBEMException {
        CloseableIterator<CIMInstance> iterator = associatorInstances(device.getInstance().getObjectPath(), "CIM_HostedService", "CIM_BlockStatisticsService",
                "Antecedent", "Dependent", false, null);
        while (iterator.hasNext()) {
            return new StatisticsService(iterator.next());
        }
        iterator.close();
        return null;
    }

    public StatisticsCapabilities getStatisticsCapabilities(SMISDevice device) throws WBEMException {
        StatisticsService service = getStatisticsService(device);
        CloseableIterator<CIMInstance> iterator = associatorInstances(service.getInstance().getObjectPath(), "CIM_ElementCapabilities",
                "CIM_BlockStatisticsCapabilities", "ManagedElement", "Capabilities", false, null);
        while (iterator.hasNext()) {
            return new StatisticsCapabilities(iterator.next());
        }
        iterator.close();
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T extends SMISDevice> T addDevice(Class<T> clazz, CIMInstance instance) {
        SMISDevice device = devicePool.get(instance.getObjectPath());
        if (device == null) {
            try {
                Constructor<T> constructor = clazz.getConstructor(CIMInstance.class);
                T newDevice = constructor.newInstance(instance);
                devicePool.put(instance.getObjectPath(), newDevice);
                return newDevice;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (device.getClass().isAssignableFrom(clazz) && device.getClass() != clazz) {
            try {
                Constructor<T> constructor = clazz.getConstructor(SMISDevice.class);
                T newDevice = constructor.newInstance(device);
                devicePool.put(instance.getObjectPath(), newDevice);
                return newDevice;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (T) device;
    }

    protected CloseableIterator<CIMInstance> enumerateInstances(CIMObjectPath pPath, boolean pDeep, boolean pLocalOnly, boolean pIncludeClassOrigin, String[] pPropertyList) throws WBEMException {
        long startTime = System.currentTimeMillis();
        CloseableIterator<CIMInstance> result = client.enumerateInstances(pPath, pDeep, pLocalOnly, pIncludeClassOrigin, pPropertyList);
        long endTime = System.currentTimeMillis();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("enumerateInstances path = " + pPath.getObjectName() + ", costTime = " + (endTime - startTime));
        return result;
    }

    protected CloseableIterator<CIMInstance> associatorInstances(CIMObjectPath pObjectName, String pAssociationClass, String pResultClass, String pRole, String pResultRole, boolean pIncludeClassOrigin, String[] pPropertyList) throws WBEMException{
        long startTime = System.currentTimeMillis();
        CloseableIterator<CIMInstance> result = null;
        try {
        	result = client.associatorInstances(pObjectName, pAssociationClass, pResultClass, pRole, pResultRole, pIncludeClassOrigin, pPropertyList);
		} catch (WBEMException e) {
			long endTime = System.currentTimeMillis();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("CIM_ERR. associatorInstances objectName = " + pObjectName.getObjectName() + ", associationClass = " + pAssociationClass + ", resultClass = " + pResultClass+ ", costTime = " + (endTime - startTime)+", error message =  "+e.toString());
		}
        return result;
    }

    protected CIMInstance getInstance(CIMObjectPath pName, boolean pLocalOnly, boolean pIncludeClassOrigin, String[] pPropertyList) throws WBEMException {
        long startTime = System.currentTimeMillis();
        CIMInstance result = client.getInstance(pName, pLocalOnly, pIncludeClassOrigin, pPropertyList);
        long endTime = System.currentTimeMillis();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("getInstance objectName = " + pName.getObjectName() + ", costTime = " + (endTime - startTime));
        return result;
    }

}
