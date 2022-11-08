package com.mainsteam.stm.plugin.icmp;

import com.mainsteam.stm.plugin.icmp.core.PingEngine;
import com.mainsteam.stm.plugin.icmp.core.Target;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author lich
 */
public class IcmpPluginSession implements PluginSession {

    public static final String IP = "IP";
    public static final String ICMP_RETRY = "IcmpRetry";
    public static final String ICMP_TIMEOUT = "IcmpTimeout";

    public static final int MIN_RETRY = 1;
    public static final int MAX_RETRY = 100;
    public static final int MIN_TIMEOUT = 10;
    public static final int MAX_TIMEOUT = 10000;

    private static final Log LOGGER = LogFactory.getLog(IcmpPluginSession.class);

    static {
        boolean result = PingEngine.init();
        if (LOGGER.isInfoEnabled())
            LOGGER.info("PingEngine init result = " + result);
    }

    private String ip;
    private int timeout = Target.DEFAULT_TIMEOUT;
    private int retry = Target.DEFAULT_RETRY;
    private boolean alive;

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void init(PluginInitParameter initParameter) throws PluginSessionRunException {
        for (Parameter parameter : initParameter.getParameters()) {
            String key = parameter.getKey();
            String value = parameter.getValue();
            switch (key) {
                case IP:
                    ip = value;
                    break;
                case ICMP_TIMEOUT:
                    try {
                        timeout = Integer.valueOf(value);
                        if (timeout < MIN_TIMEOUT || timeout > MAX_TIMEOUT) {
                            timeout = Target.DEFAULT_TIMEOUT;
                            if (LOGGER.isWarnEnabled())
                                LOGGER.warn("Invalid parameter: " + key + " = " + value);
                        }
                    } catch (NumberFormatException e) {
                        if (LOGGER.isWarnEnabled())
                            LOGGER.warn("Invalid parameter: " + key + " = " + value);
                    }
                    break;
                case ICMP_RETRY:
                    try {
                        retry = Integer.valueOf(value);
                        if (retry < MIN_RETRY || retry > MAX_RETRY) {
                            retry = Target.DEFAULT_RETRY;
                            if (LOGGER.isWarnEnabled())
                                LOGGER.warn("Invalid parameter: " + key + " = " + value);
                        }
                    } catch (NumberFormatException e) {
                        if (LOGGER.isWarnEnabled())
                            LOGGER.warn("Invalid parameter: " + key + " = " + value);
                    }
                    break;
            }
        }
        addToEngine();
    }

    private void addToEngine() {
        Target target = new Target(ip, timeout, retry);
        try {
            if (PingEngine.addTarget(target))
                alive = true;
            else
                LOGGER.error("Fail to add target  = " + target);
        } catch (Exception e) {
            LOGGER.error("Fail to add target  = " + target, e);
        }
    }

    @Override
    public void destory() {
        alive = false;
    }

    @Override
    public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
        PluginResultSet result = new PluginResultSet();
        Target bean = PingEngine.getResult(ip);
        if (bean == null) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(ip + " is out of date, add to engine again.");
            }
            alive = false;
            addToEngine();
        } else {
            result.putValue(0, 0, bean.getIp());
            result.putValue(0, 1, bean.getState());
            result.putValue(0, 2, bean.getLatency());
            result.putValue(0, 3, bean.getPacketLoss());
            result.putValue(0, 4, bean.getJitter());
        }
        return result;
    }

    @Override
    public void reload() {
    }

    @Override
    public boolean check(PluginInitParameter initParameters)
            throws PluginSessionRunException {
        return false;
    }
}
