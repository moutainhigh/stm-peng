package com.mainsteam.stm.plugin.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.mainsteam.stm.util.PropertiesFileUtil;

public class DemoPluginSession implements PluginSession {

	private static final Log LOGGER = LogFactory.getLog(DemoPluginSession.class);

	private static final String CONFIG_FILE = "demoConfigFile";
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final String DELIMITER = "\\s*@@@\\s*";
	private static final String INFORMATION = "information";
	private static final String PERFORMANCE = "performance";
	private static final String AVAILABLE = "available";
	private static final String BEGIN = "information|performance|available";
	private static final int COLUMN = 4;

	private static String templateFilePath = null;

//	static {
//		try {
//			Properties properties = PropertiesFileUtil.getProperties("demo.properties");
//			if (properties == null) {
//				properties = new Properties();
//				InputStream in = ClassLoader.getSystemResourceAsStream("demo.properties");
//				if (in != null) {
//					properties.load(in);
//				}
//			}
//			templateFilePath = properties.getProperty("stm.demo.templateFilePath");
//		} catch (Exception ignore) {
//		}
//	}

	private boolean avail;
	private Map<String, Map<String, DemoDataGenerator>> unmodifiableDemoMap;

	@Override
	public void init(PluginInitParameter initParameters) throws PluginSessionRunException {
//		String file = templateFilePath;
//		for (Parameter parameter : initParameters.getParameters()) {
//			switch (parameter.getKey()) {
//			case CONFIG_FILE:
//				if (StringUtils.isNotEmpty(parameter.getValue())) {
//					file = parameter.getValue();
//				}
//				break;
//			default:
//				break;
//			}
//		}
//		try (Scanner scanner = new Scanner(new File(file), "UTF-8")) {
//			scanner.useDelimiter(DELIMITER);
//			HashMap<String, Map<String, DemoDataGenerator>> demoMap = new HashMap<String, Map<String, DemoDataGenerator>>();
//			while (scanner.hasNext()) {
//				try {
//					String begin = scanner.next();
//					if (!begin.matches(BEGIN)) {
//						continue;
//					}
//				} catch (NoSuchElementException e) {
//					break;
//				}
//				// elements[] = key (resourceId or instanceId), metricId, max, min
//				String elements[] = new String[COLUMN];
//				int len;
//				for (len = 0; len < COLUMN; len++) {
//					if (scanner.hasNext()) {
//						try {
//							elements[len] = scanner.next();
//						} catch (NoSuchElementException e) {
//							break;
//						}
//					}
//				}
//				// debug elements[]
//				if (LOGGER.isDebugEnabled()) {
//					LOGGER.debug("elements = " + Arrays.toString(elements));
//				}
//				if (len >= 3) {
//					if (demoMap.get(elements[0]) == null) {
//						demoMap.put(elements[0], new HashMap<String, DemoDataGenerator>());
//					}
//					if (StringUtils.isNotEmpty(elements[3])) {
//						try {
//							demoMap.get(elements[0]).put(elements[1], new PerformanceDataGenerator(Double.valueOf(elements[2]), Double.valueOf(elements[3])));
//						} catch (NumberFormatException e) {
//							LOGGER.error("fail with elements = " + Arrays.toString(elements));
//						}
//					} else {
//						demoMap.get(elements[0]).put(elements[1], new InformationDataGenerator(elements[2]));
//					}
//				} else {
//					LOGGER.error("fail with elements = " + Arrays.toString(elements));
//				}
//			}
//			unmodifiableDemoMap = Collections.unmodifiableMap(demoMap);
//			if (LOGGER.isDebugEnabled())
//				LOGGER.debug("demoMap = " + JSON.toJSONString(unmodifiableDemoMap));
//			avail = true;
//		} catch (NullPointerException | FileNotFoundException e) {
//			LOGGER.error("can't open the demo file: " + file, e);
//		}

	}

	@Override
	public boolean check(PluginInitParameter initParameters) throws PluginSessionRunException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void destory() {
		avail = false;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAlive() {
//		return avail;
		return true;
	}

	@Override
	public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
		PluginResultSet resultSet = new PluginResultSet();
//		try {
//			resultSet.putValue(0, 0, unmodifiableDemoMap.get(String.valueOf(context.getResourceInstanceId())).get(context.getMetricId()).getDemoData());
//		} catch (NullPointerException instanceE) {
//			try {
//				resultSet.putValue(0, 0, unmodifiableDemoMap.get(context.getResourceId()).get(context.getMetricId()).getDemoData());
//			} catch (NullPointerException resourceE) {
//				if (LOGGER.isWarnEnabled()) {
//					LOGGER.warn("no demo data generator for: instanceId = " + context.getResourceInstanceId() + ", resourceId = " + context.getResourceId()
//							+ ", metricId = " + context.getMetricId());
//				}
//			}
//		}
		return resultSet;
	}

}
