package com.mainsteam.stm.plugin.icegrid;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class IceGridPluginSession implements PluginSession {

    private static final Log LOGGER = LogFactory.getLog(IceGridPluginSession.class);

    private static final String ENDPOINTS = "IceEndpoints";
    private static final String INSTANCE_NAME = "IceInstanceName";
    private static final String USERNAME = "IceUsername";
    private static final String PASSWORD = "IcePassword";
    private static final String COMMAND = "COMMAND";
    private static final String PARA = "PARA";
    private static final String GETGRID = "getGrid";
    private static final String GETALL = "getAll";

    private IceGridCollector collector;

    @Override
    public void init(PluginInitParameter initParameters) throws PluginSessionRunException {
        Parameter[] parameters = initParameters.getParameters();
        String endpoints = null, instanceName = null, username = null, password = null;
        for (Parameter parameter : parameters) {
            switch (parameter.getKey()) {
                case ENDPOINTS:
                    endpoints = parameter.getValue();
                    break;
                case INSTANCE_NAME:
                    instanceName = parameter.getValue();
                    break;
                case USERNAME:
                    username = parameter.getValue();
                    break;
                case PASSWORD:
                    password = parameter.getValue();
                    break;
                default:
                    if (LOGGER.isTraceEnabled())
                        LOGGER.trace("Unknown initParameter: " + parameter.getKey() + " = " + parameter.getValue());
                    break;
            }
        }
        try {
            collector = new IceGridCollector(endpoints, instanceName, username, password);
        } catch (Exception e) {
            LOGGER.error("Session init failed.", e);
            throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED, e);
        }
    }

    @Override
    public boolean check(PluginInitParameter initParameters) throws PluginSessionRunException {
        return false;
    }

    @Override
    public void destory() {
        if (collector != null)
            collector.dispose();
        collector = null;
    }

    @Override
    public void reload() {
    }

    @Override
    public boolean isAlive() {
        try {
            collector.getGridName();
        } catch (Exception e) {
            LOGGER.debug("isAlive = false, caused by", e);
            return false;
        }
        return true;
    }

    @Override
    public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
        PluginResultSet resultSet = new PluginResultSet();
        if (executorParameter instanceof PluginArrayExecutorParameter) {
            Parameter[] parameters = ((PluginArrayExecutorParameter) executorParameter).getParameters();
            String cmd = null, para = null;
            for (Parameter parameter : parameters) {
                switch (parameter.getKey()) {
                    case COMMAND:
                        cmd = parameter.getValue();
                        break;
                    case PARA:
                        para = parameter.getValue();
                        break;
                    default:
                        if (LOGGER.isTraceEnabled())
                            LOGGER.trace("Unknown execParameter: " + parameter.getKey() + " = " + parameter.getValue());
                        break;
                }
            }
            try {
                if (StringUtils.startsWithIgnoreCase(cmd, GETGRID)) {
                    Method method = collector.getClass().getMethod(cmd);
                    resultSet.putValue(0, 0, method.invoke(collector).toString());
                } else if (StringUtils.startsWithIgnoreCase(cmd, GETALL)) {
                    Method method = collector.getClass().getMethod(cmd);
                    String[] ret = (String[]) method.invoke(collector);
                    for (int i = 0; i < ret.length; ++i)
                        resultSet.putValue(i, 0, ret[i]);
                } else {
                    Method method = collector.getClass().getMethod(cmd, String.class);
                    resultSet.putValue(0, 0, method.invoke(collector, para).toString());
                }
            } catch (Exception e) {
                LOGGER.error("Session execute failed.", e);
                throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, e);
            }
        }

        return resultSet;
    }

}
