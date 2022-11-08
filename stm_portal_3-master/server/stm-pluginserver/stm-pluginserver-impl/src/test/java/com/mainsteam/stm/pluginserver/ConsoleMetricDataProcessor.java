///**
// * 
// */
//package com.mainsteam.stm.pluginserver;
//
//import java.util.Arrays;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.mainsteam.stm.dataprocess.MetricCalculateData;
//import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
//import com.mainsteam.stm.dataprocess.engine.MetricDataProcessorRegister;
//
///**
// * @author ziw
// * 
// */
//public class ConsoleMetricDataProcessor implements MetricDataProcessor {
//
//	private static final Log logger = LogFactory.getLog(ConsoleMetricDataProcessor.class);
//
//	private MetricDataProcessorRegister processorRegister;
//
//	/**
//	 * 
//	 */
//	public ConsoleMetricDataProcessor() {
//	}
//
//	public void setProcessorRegister(
//			MetricDataProcessorRegister processorRegister) {
//		this.processorRegister = processorRegister;
//	}
//
//	public void start() {
//		processorRegister.registerMetricDataProcessor(this);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.mainsteam.stm.dataprocess.engine.MetricDataProcessor#process(com
//	 * .mainsteam.oc.dataprocess.MetricData)
//	 */
//	@Override
//	public void process(MetricCalculateData data) {
//		System.out.println("process metric data.");
//		StringBuilder b = new StringBuilder();
//		b.append("resourceInstId=").append(data.getResourceInstanceId()).append('\r');
//		b.append("metricId=").append(data.getMetricId()).append('\r');
//		b.append("metricData=").append(Arrays.asList(data.getMetricData()));
//		if (logger.isDebugEnabled()) {
//			logger.debug("process metric data." + b.toString());
//		}
//	}
//}
