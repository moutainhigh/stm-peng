package com.mainsteam.stm.plugin.icegrid;

import Ice.Communicator;
import Ice.Util;
import IceGrid.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IceGridCollector {

    private static final Log LOGGER = LogFactory.getLog(IceGridCollector.class);
    private final static String TRUE = "1";
    private final static String FALSE = "0";

    private Communicator communicator;
    private AdminSessionPrx adminSessionPrx;
    private AdminPrx adminPrx;

    public IceGridCollector(String endpoints, String instanceName, String username, String password) throws PermissionDeniedException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Collector init start.");
        communicator = Util.initialize();
        RegistryPrx registryPrx = RegistryPrxHelper.checkedCast(communicator.stringToProxy(instanceName + "/Registry:" + endpoints));
        adminSessionPrx = registryPrx.createAdminSession(username, password);
        adminPrx = adminSessionPrx.getAdmin();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Collector init finish.");
    }

    public String getGridName() {
        return adminSessionPrx.getReplicaName();
    }

    public String getGridAvailability() {
        boolean flag = false;
        for (String registryName : adminPrx.getAllRegistryNames()) {
            try {
                flag |= adminPrx.pingRegistry(registryName);
            } catch (RegistryNotExistException e) {
            }
            if (flag)
                break;
        }
        if (!flag)
            return FALSE;
        flag = false;
        for (String nodeName : adminPrx.getAllNodeNames()) {
            try {
                flag |= adminPrx.pingNode(nodeName);
            } catch (NodeNotExistException e) {
            }
            if (flag)
                break;
        }
        if (!flag)
            return FALSE;
        return TRUE;
    }

    public double getGridCpuRate() throws NodeNotExistException, NodeUnreachableException {
        double ret = 0;
        for (String nodeName : adminPrx.getAllNodeNames()) {
            ret += adminPrx.getNodeLoad(nodeName).avg1;
        }
        return ret * 100 / adminPrx.getAllNodeNames().length;
    }

    public String[] getAllRegistryNames() {
        return adminPrx.getAllRegistryNames();
    }

    public String getRegistryAvailability(String registryName) throws RegistryNotExistException {
        if (adminPrx.pingRegistry(registryName)) {
            return TRUE;
        }
        return FALSE;
    }

    public String getRegistryHostname(String registryName) throws RegistryNotExistException, RegistryUnreachableException {
        return adminPrx.getRegistryInfo(registryName).hostname;
    }

    public String[] getAllNodeNames() {
        return adminPrx.getAllNodeNames();
    }

    public String getNodeAvailability(String nodeName) throws NodeNotExistException {
        if (adminPrx.pingNode(nodeName)) {
            return TRUE;
        }
        return FALSE;
    }

    public String getNodeDataDir(String nodeName) throws NodeNotExistException, NodeUnreachableException {
        return adminPrx.getNodeInfo(nodeName).dataDir;
    }

    public String getNodeHostname(String nodeName) throws NodeNotExistException, NodeUnreachableException {
        return adminPrx.getNodeInfo(nodeName).hostname;
    }

    public String getNodeOperatingSystem(String nodeName) throws NodeNotExistException, NodeUnreachableException {
        return adminPrx.getNodeInfo(nodeName).os + " " + adminPrx.getNodeInfo(nodeName).release + " " + adminPrx.getNodeInfo(nodeName).version;
    }

    public String getNodeMachineType(String nodeName) throws NodeNotExistException, NodeUnreachableException {
        return adminPrx.getNodeInfo(nodeName).machine + " with " + adminPrx.getNodeInfo(nodeName).nProcessors + " CPU";
    }

    public float getNodeLoadAvg1(String nodeName) throws NodeNotExistException, NodeUnreachableException {
        return adminPrx.getNodeLoad(nodeName).avg1 * 100;
    }

    public float getNodeLoadAvg5(String nodeName) throws NodeNotExistException, NodeUnreachableException {
        return adminPrx.getNodeLoad(nodeName).avg5 * 100;
    }

    public float getNodeLoadAvg15(String nodeName) throws NodeNotExistException, NodeUnreachableException {
        return adminPrx.getNodeLoad(nodeName).avg15 * 100;
    }

    public String[] getAllServerIds() {
        return adminPrx.getAllServerIds();
    }

    public String getServerAvailability(String serverId) throws DeploymentException, NodeUnreachableException, ServerNotExistException {
        if (adminPrx.isServerEnabled(serverId)) {
            return TRUE;
        }
        return FALSE;
    }

    public ServerState getServerState(String serverId) throws DeploymentException, NodeUnreachableException, ServerNotExistException {
        return adminPrx.getServerState(serverId);
    }

    public int getServerPid(String serverId) throws DeploymentException, NodeUnreachableException, ServerNotExistException {
        return adminPrx.getServerPid(serverId);
    }

    public String getServerApplication(String serverId) throws ServerNotExistException {
        return adminPrx.getServerInfo(serverId).application;
    }

    public String getServerNode(String serverId) throws ServerNotExistException {
        return adminPrx.getServerInfo(serverId).node;
    }

    public String getServerActivation(String serverId) throws ServerNotExistException {
        return adminPrx.getServerInfo(serverId).descriptor.activation;
    }

    public void dispose() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Collector dispose start.");
        try {
            if (adminSessionPrx != null)
                adminSessionPrx.destroy();
            adminSessionPrx = null;
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Destroy adminSession succeed.");
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Destroy adminSession failed", e);
        }
        try {
            if (communicator != null)
                communicator.destroy();
            communicator = null;
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Destroy communicator succeed.");
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Destroy communicator failed", e);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Collector dispose finish.");
    }
}
