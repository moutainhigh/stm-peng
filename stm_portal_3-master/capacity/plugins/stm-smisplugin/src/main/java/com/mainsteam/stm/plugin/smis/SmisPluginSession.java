package com.mainsteam.stm.plugin.smis;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.smis.collect.SMISCollector;
import com.mainsteam.stm.plugin.smis.collect.SMISProvider;
import com.mainsteam.stm.plugin.smis.collect.factory.SMISCollectorFactory;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import com.mainsteam.stm.plugin.smis.object.SMISDevice;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.wbem.WBEMException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SmisPluginSession implements PluginSession {

    private static final Log LOGGER = LogFactory.getLog(SmisPluginSession.class);

    private static final String SMISPLUGIN_DEVICE = "Device";
    private static final String SMISPLUGIN_PROPERTY = "Property";
    private static final String SMISPLUGIN_STATISTICALDATA = "StatisticalData";
    private static final String SMISPLUGIN_STATISTICSSERVICE = "getStatisticsService";
    private static final String SMISPLUGIN_STATISTICSCAPABILITIES = "StatisticsCapabilities";

    private boolean available;
    private SMISCollector collector;

    @Override
    public void init(PluginInitParameter initParameters) throws PluginSessionRunException {
        Parameter[] parameters = initParameters.getParameters();
        initParameters.getParameterValueByKey("IP");
        SMISProvider provider = new SMISProvider();
        String name = "";
        for (Parameter parameter : parameters) {
            switch (parameter.getKey()) {
                case SMISProvider.IP:
                    provider.setIp(parameter.getValue());
                    break;
                case SMISProvider.PORT:
                    provider.setPort(parameter.getValue());
                    break;
                case SMISProvider.NAMESPACE:
                    provider.setNameSpace(parameter.getValue());
                    break;
                case SMISProvider.USERNAME:
                    provider.setUsername(parameter.getValue());
                    break;
                case SMISProvider.PASSWORD:
                    provider.setPassword(parameter.getValue());
                    break;
                case SMISProvider.PROTOCOL:
                    provider.setProtocol(parameter.getValue());
                    break;
                case SMISProvider.VENDOR:
                    provider.setVendor(parameter.getValue());
                    break;
                case "name":
                    name = parameter.getValue();
                    break;
                case SMISProvider.SMISIP:
                	provider.setSmisip(parameter.getValue());
                	break;
                default:
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Unknown InitParameter " + parameter);
                    break;
            }
        }
        if (provider.getSmisip().equals("-1")) {
        	if (LOGGER.isDebugEnabled())
                LOGGER.debug("Provider smisip is -1");
		}else {
			if (provider.getSmisip().isEmpty()||(provider.getSmisip()==null)||(provider.getSmisip().trim().length()==0)) {
	        	provider.setSmisip(provider.getIp());
			}else {
				provider.setIp(provider.getSmisip());
			}
		}
        
        
        try {
            collector = SMISCollectorFactory.createDiskArrayCollector(provider, name);
            available = true;
        } catch (WBEMException e) {
            LOGGER.error("SMI-S connection failed", e);
            throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED, e);
        } catch (SMISException e) {
            LOGGER.error("Can't find the specific base system", e);
            throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED, e);
        }
    }

    @Override
    public boolean check(PluginInitParameter initParameters) throws PluginSessionRunException {
        return false;
    }

    @Override
    public void destory() {

    }

    @Override
    public void reload() {
    }

    @Override
    public boolean isAlive() {
        return available;
    }

    @Override
    public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
        PluginResultSet resultSet = new PluginResultSet();
        if (executorParameter instanceof PluginArrayExecutorParameter) {
            Parameter[] parameters = ((PluginArrayExecutorParameter) executorParameter).getParameters();
            // initialize the current devices
            ArrayList<Set<SMISDevice>> curDevices = new ArrayList<Set<SMISDevice>>();
            HashSet<SMISDevice> t = new HashSet<SMISDevice>();
            try {
                t.add(collector.getBase());
            } catch (WBEMException e) {
                LOGGER.error(e);
                throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, e);
            }
            curDevices.add(t);
            int column = 0, row;
            for (Parameter parameter : parameters) {
                switch (parameter.getKey()) {
                    case SMISPLUGIN_DEVICE:
                        String value = parameter.getValue();
                        int commaIndex = value.indexOf(",");
                        String deviceType,
                                constraint;
                        if (commaIndex > 0) {
                            deviceType = value.substring(0, commaIndex);
                            constraint = value.substring(commaIndex + 1);
                        } else {
                            deviceType = value;
                            constraint = null;
                        }
                        curDevices.clear();
                        try {
                            for (SMISDevice device : collector.getDevice(deviceType, constraint)) {
                                t = new HashSet<SMISDevice>();
                                t.add(device);
                                curDevices.add(t);
                            }
                        } catch (Exception e) {
                            LOGGER.error("get device failed, ", e);
//                            throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, e);
                        }
                        break;
                    case SMISPLUGIN_PROPERTY:
                        row = 0;
                        for (Set<SMISDevice> devices : curDevices) {
                            StringBuilder ret = new StringBuilder();
                            for (SMISDevice device : devices) {
                                ret.append(device.getPropertyValue(parameter.getValue())).append(',');
                            }
                            if (ret.length() > 0) {
                                resultSet.putValue(row, column, ret.deleteCharAt(ret.length() - 1).toString());

                            }
                            row++;
                        }
                        column++;
                        break;
                    case SMISPLUGIN_STATISTICALDATA:
                        row = 0;
                        for (Set<SMISDevice> devices : curDevices) {
                            StringBuilder ret = new StringBuilder();
                            for (SMISDevice device : devices) {
                                try {
                                    ret.append(collector.getStatisticalData(device).getPropertyValue(parameter.getValue())).append(',');
                                } catch (WBEMException e) {
                                    if (LOGGER.isWarnEnabled())
                                        LOGGER.warn(e);
                                }
                            }
                            if (ret.length() > 0) {
                                resultSet.putValue(row, column, ret.deleteCharAt(ret.length() - 1).toString());
                            }
                            row++;
                        }
                        column++;
                        break;
                    case SMISPLUGIN_STATISTICSSERVICE:
                        row = 0;
                        for (Set<SMISDevice> devices : curDevices) {
                            StringBuilder ret = new StringBuilder();
                            for (SMISDevice device : devices) {
                                try {
                                    ret.append(collector.getStatisticsService(device).getPropertyValue(parameter.getValue())).append(',');
                                } catch (WBEMException e) {
                                    if (LOGGER.isWarnEnabled())
                                        LOGGER.warn(e);
                                }
                            }
                            resultSet.putValue(row, column, ret.deleteCharAt(ret.length() - 1).toString());
                            row++;
                        }
                        column++;
                        break;
                    case SMISPLUGIN_STATISTICSCAPABILITIES:
                        row = 0;
                        for (Set<SMISDevice> devices : curDevices) {
                            StringBuilder ret = new StringBuilder();
                            for (SMISDevice device : devices) {
                                try {
                                    ret.append(collector.getStatisticsCapabilities(device).getPropertyValue(parameter.getValue())).append(',');
                                } catch (WBEMException e) {
                                    if (LOGGER.isWarnEnabled())
                                        LOGGER.warn(e);
                                }
                            }
                            resultSet.putValue(row, column, ret.deleteCharAt(ret.length() - 1).toString());
                            row++;
                        }
                        column++;
                        break;
                    default:
                        break;
                }
            }
        }
        return resultSet;
    }

}
