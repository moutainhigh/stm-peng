package com.mainsteam.stm.plugin.fusioncompute;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.fusioncompute.bo.ConnectionInfo;
import com.mainsteam.stm.plugin.fusioncompute.collect.FusionComputeCollector;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * huawei virtualization acquistion plugin
 * 
 * @author yuanlb
 * @2016年1月14日 上午10:56:44
 */

public class FCPluginSession implements PluginSession {

    private static final Log LOGGER = LogFactory.getLog(FCPluginSession.class);

    private static final String HOST = "IP";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String Port = "7443";

    private static final String COMMAND = "COMMAND";

    private static final String UUID = "uuid";

    private static final String GET_RESOURCETREE = "getResourceTree";

    private static final String GET_POOL = "getSite";

    /*
     * @SuppressWarnings("unused") private static final String
     * GET_CLUSTER="getCluster";
     * 
     * @SuppressWarnings("unused") private static final String GET_HOST =
     * "getHost";
     * 
     * @SuppressWarnings("unused") private static final String GET_VM = "getVm";
     * 
     * @SuppressWarnings("unused") private static final String GET_DATASTORE =
     * "getDataStore";
     */

    @SuppressWarnings("unused")
    private static final String GET_UUIDPERF = "getUuidPerf";

    private FusionComputeCollector fusionComputeCollector;

    @Override
    public void init(PluginInitParameter initParameters)
            throws PluginSessionRunException {
        Parameter[] parameters = initParameters.getParameters();
        String serverIp = null, username = null, password = null;
        for (Parameter parameter : parameters) {
            switch (parameter.getKey()) {
            case HOST:
                serverIp = parameter.getValue();
                break;
            case USERNAME:
                username = parameter.getValue();
                break;
            case PASSWORD:
                password = parameter.getValue();
                break;
            default:
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Unkown initParameter: " + parameter.getKey()
                            + " = " + parameter.getValue());
            }
        }
        try {
            fusionComputeCollector = new FusionComputeCollector(
                    new ConnectionInfo(serverIp, Port, username, password));

        } catch (Exception e) {
            throw new PluginSessionRunException(
                    CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED
                            + " jason say:init connection faild！！！"
                            + "  time: "
                            + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(new Date()),
                    e);
        }
    }

    @Override
    public boolean check(PluginInitParameter initParameters)
            throws PluginSessionRunException {
        return false;
    }

    @Override
    public void destory() {
        if (fusionComputeCollector != null)
            fusionComputeCollector.dispose();
        fusionComputeCollector = null;
    }

    @Override
    public void reload() {
    }

    @Override
    public boolean isAlive() {
        return fusionComputeCollector != null;
    }

    @Override
    public PluginResultSet execute(
            PluginExecutorParameter<?> executorParameter,
            PluginSessionContext context) throws PluginSessionRunException {
        PluginResultSet resultSet = new PluginResultSet();
        if (executorParameter instanceof PluginArrayExecutorParameter) {
            Parameter[] parameters = ((PluginArrayExecutorParameter) executorParameter)
                    .getParameters();
            String cmd = null, uuid = null;
            for (Parameter parameter : parameters) {
                switch (parameter.getKey()) {
                case COMMAND:
                    cmd = parameter.getValue();

                    break;
                case UUID:
                    uuid = parameter.getValue();

                    break;
                default:
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Unkown execParameter: "
                                + parameter.getKey() + " = "
                                + parameter.getValue());
                    break;
                }
            }
            try {

                if (StringUtils.startsWithIgnoreCase(cmd, GET_POOL)
                        || StringUtils.equalsIgnoreCase(cmd, GET_RESOURCETREE)) {
                    Method method = fusionComputeCollector.getClass()
                            .getMethod(cmd);
                    resultSet.putValue(0, 0,
                            method.invoke(fusionComputeCollector).toString());
                } else {
                    Method method = fusionComputeCollector.getClass()
                            .getMethod(cmd, String.class);
                    resultSet.putValue(0, 0,
                            method.invoke(fusionComputeCollector, uuid)
                                    .toString());
                }
            } catch (Exception e) {
                LOGGER.error(e);
                throw new PluginSessionRunException(
                        CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, e);
            }
        }
        return resultSet;
    }
}
