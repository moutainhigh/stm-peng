package com.mainsteam.stm.plugin.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
import com.mainsteam.stm.plugin.demo.DemoDataGenerator;
import com.mainsteam.stm.plugin.demo.InformationDataGenerator;
import com.mainsteam.stm.plugin.demo.PerformanceDataGenerator;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.util.PropertiesFileUtil;

public class DemoDataProcessor implements PluginResultSetProcessor {

	private static final Log LOGGER = LogFactory.getLog(DemoDataProcessor.class);
	private static final String DELIMITER = "\\s*@@@\\s*";
	private static final String BEGIN = "information|performance|available";
	private static final int COLUMN = 4;
	private static final Map<String, Map<String, DemoDataGenerator>> unmodifiableDemoMap;

	static {
		HashMap<String, Map<String, DemoDataGenerator>> demoMap = new HashMap<String, Map<String, DemoDataGenerator>>();
		try {
			Properties properties = PropertiesFileUtil.getProperties("demo.properties");
			if (properties == null) {
				properties = new Properties();
				InputStream in = ClassLoader.getSystemResourceAsStream("demo.properties");
				if (in != null) {
					properties.load(in);
				}
			}
			String file = properties.getProperty("stm.demo.templateFilePath");
			try (Scanner scanner = new Scanner(new File(file), "UTF-8")) {
				scanner.useDelimiter(DELIMITER);
				while (scanner.hasNext()) {
					try {
						String begin = scanner.next();
						if (!begin.matches(BEGIN)) {
							continue;
						}
					} catch (NoSuchElementException e) {
						break;
					}
					// elements[] = key (resourceId or instanceId), metricId, max, min
					String elements[] = new String[COLUMN];
					int len;
					for (len = 0; len < COLUMN; len++) {
						if (scanner.hasNext()) {
							try {
								elements[len] = scanner.next();
							} catch (NoSuchElementException e) {
								break;
							}
						}
					}
					// debug elements[]
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("elements = " + Arrays.toString(elements));
					}
					if (len >= 3) {
						if (demoMap.get(elements[0]) == null) {
							demoMap.put(elements[0], new HashMap<String, DemoDataGenerator>());
						}
						if (StringUtils.isNotEmpty(elements[3])) {
							try {
								demoMap.get(elements[0]).put(elements[1], new PerformanceDataGenerator(Double.valueOf(elements[2]), Double.valueOf(elements[3])));
							} catch (NumberFormatException e) {
								LOGGER.error("fail with elements = " + Arrays.toString(elements));
							}
						} else {
							demoMap.get(elements[0]).put(elements[1], new InformationDataGenerator(elements[2]));
						}
					} else {
						LOGGER.error("fail with elements = " + Arrays.toString(elements));
					}
				}

			} catch (NullPointerException | FileNotFoundException e) {
				LOGGER.error("can't open the demo file: " + file, e);
			}
		} catch (Exception e) {
			LOGGER.error("can't open the config file: ", e);
		}
		unmodifiableDemoMap = Collections.unmodifiableMap(demoMap);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("demoMap = " + JSON.toJSONString(unmodifiableDemoMap));
	}

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
		try {
			resultSet.putValue(0, 0, unmodifiableDemoMap.get(String.valueOf(context.getResourceInstanceId())).get(context.getMetricId()).getDemoData());
		} catch (NullPointerException instanceE) {
			try {
				resultSet.putValue(0, 0, unmodifiableDemoMap.get(context.getResourceId()).get(context.getMetricId()).getDemoData());
			} catch (NullPointerException resourceE) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("no demo data generator for: instanceId = " + context.getResourceInstanceId() + ", resourceId = " + context.getResourceId()
							+ ", metricId = " + context.getMetricId());
				}
			}
		}

	}

}
