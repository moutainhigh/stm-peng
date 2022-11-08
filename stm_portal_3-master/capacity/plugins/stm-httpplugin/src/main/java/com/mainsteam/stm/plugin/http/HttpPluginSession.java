package com.mainsteam.stm.plugin.http;

import com.mainsteam.stm.plugin.http.collector.HttpCollector;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.parsers.SAXParserFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author lich
 */
public class HttpPluginSession implements PluginSession {

    private static final Log LOGGER = LogFactory.getLog(HttpPluginSession.class);

    static {
        try {
            LOGGER.info("javax.xml.parsers.SAXParserFactory = " + System.getProperty("javax.xml.parsers.SAXParserFactory"));
            LOGGER.info("SAXParserFactory = " + SAXParserFactory.newInstance().getClass().getName());
            LOGGER.info("SAXParser = " + SAXParserFactory.newInstance().newSAXParser().getClass().getName());
            LOGGER.info("XMLReader = " + SAXParserFactory.newInstance().newSAXParser().getXMLReader().getClass().getName());
        } catch (Exception ignored) {

        }
    }

    private static final String SERVICE_TYPE = "serviceType";
    private static final String METHOD = "method";
    private static final String COLLECTOR_PACKAGE_PREFIX = "com.mainsteam.stm.plugin.http.collector.";
    private static final String COLLECTOR_SUFFIX = "Collector";
    private HttpCollector collector;

    @Override
    public void init(PluginInitParameter initParameters) throws PluginSessionRunException {
        HashMap<String, String> initMap = new HashMap<>();
        for (Parameter parameter : initParameters.getParameters()) {
            if (parameter.getKey().equals(SERVICE_TYPE)) {
                try {
                    collector = newCollector(parameter.getValue());
                } catch (Exception e) {
                    throw new PluginSessionRunException("Can not new the specified collector: " + parameter.getValue(), e);
                }
            } else {
                initMap.put(parameter.getKey(), parameter.getValue());
            }
        }
        try {
            collector.init(initMap);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new PluginSessionRunException(e);
        }
    }

    private HttpCollector newCollector(String collectorName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<? extends HttpCollector> clazz = (Class<? extends HttpCollector>) Class.forName(COLLECTOR_PACKAGE_PREFIX + collectorName + COLLECTOR_SUFFIX);
        return clazz.newInstance();
    }

    @Override
    public boolean check(PluginInitParameter initParameters) throws PluginSessionRunException {
        return true;
    }

    @Override
    public void destory() {
        collector.close();
    }

    @Override
    public void reload() {

    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
        if (executorParameter instanceof PluginArrayExecutorParameter) {
            for (Parameter parameter : ((PluginArrayExecutorParameter) executorParameter).getParameters()) {
                switch (parameter.getKey()) {
                    case METHOD:
                        try {
                            Method method = collector.getClass().getMethod(parameter.getValue(), new Class<?>[]{});
                            return (PluginResultSet) method.invoke(collector, new Object[]{});
                        } catch (NoSuchMethodException e) {
                            LOGGER.error(e.getMessage(), e);
                            throw new PluginSessionRunException(e);
                        } catch (InvocationTargetException e) {
                            LOGGER.error(e.getTargetException().getMessage(), e.getTargetException());
                            throw new PluginSessionRunException(e.getTargetException());
                        } catch (IllegalAccessException e) {
                            LOGGER.error(e.getMessage(), e);
                            throw new PluginSessionRunException(e);
                        }
                }
            }
        }
        return null;
    }
}
