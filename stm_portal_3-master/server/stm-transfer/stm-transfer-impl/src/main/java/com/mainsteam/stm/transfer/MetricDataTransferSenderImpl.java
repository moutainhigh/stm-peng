package com.mainsteam.stm.transfer;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.transfer.obj.InnerTransferData;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;
import com.mainsteam.stm.transfer.queue.PersistableQueue;
import com.mainsteam.stm.transfer.queue.PersistableQueueProxy;
import com.mainsteam.stm.util.DateUtil;
import com.mainsteam.stm.util.PropertiesFileUtil;

public class MetricDataTransferSenderImpl implements MetricDataTransferSender {
	private static final Log logger = LogFactory
			.getLog(MetricDataTransferSender.class);

	/**
	 * 数据缓存队列
	 */
	private PersistableQueue<InnerTransferData> datasCacheQueue;

	/**
	 * 中间数据队列
	 */
	private ArrayBlockingQueue<InnerTransferData> middleQueue;

	/**
	 * 数据发送工作线程
	 */
	private Runnable sendDriver;

	/**
	 * 中间数据驱动线程
	 */
	private Runnable middleDataDispatcher;

	private boolean started = false;

	// 缓存分钟数
	private int cacheMinute;
	private long cachedDataExpireTimes;

	/**
	 * 一次上传的最大
	 */
	private int batch_send_max_size = 1000;

	/**
	 * 最大内存数据长度
	 */
	private static final int metricMaxSize = 50000;

	/**
	 * 如果待发送的队列为空，等待的时间长度
	 */
	private long interval = 10;// ms

	private String transferDataSenderClass;

	private DataSender sender;

	public long getQueueSize() {
		return this.datasCacheQueue.size();
	}
	
	public long getMiddleSize() {
		return this.middleQueue.size();
	}

	public void setTransferDataSenderClass(String transferDataSenderClass) {
		this.transferDataSenderClass = transferDataSenderClass;
	}
	
	public synchronized void start() throws IOException {
		if (System.getProperty("testCase") != null) {
			return;
		}
		if (started) {
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("MetricDataTransferSender init start.");
		}
		if(StringUtils.isNotEmpty(transferDataSenderClass)){
			try {
				@SuppressWarnings("rawtypes")
				Class c = ClassUtils.getClass(transferDataSenderClass);
				this.sender = (DataSender) c.newInstance();
			} catch (ClassNotFoundException e) {
				if (logger.isErrorEnabled()) {
					logger.error("not found transferDataSenderClass " + transferDataSenderClass, e);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				if (logger.isErrorEnabled()) {
					logger.error("error to Instantiation " + transferDataSenderClass, e);
				}
			}
		}
		if (this.sender == null) {
			this.sender = new CommonDataSender();
		}
		if (logger.isInfoEnabled()) {
			logger.info(
					"MetricDataTransferSender init start.transferDataSenderClass=" + this.sender.getClass().getName());
		}
//		blockAndCreateConnection(true);
		cacheMinute = Integer.parseInt(PropertiesFileUtil.getProperties(
				MetricDataTransferSender.class.getClassLoader(),
				"properties/stm.properties").getProperty(
				"stm.metric_cache_minute", "15"));
		if (logger.isInfoEnabled()) {
			logger.info("start metric data expire time=" + cacheMinute
					+ " minute.");
		}
		cachedDataExpireTimes = cacheMinute > 0 ? cacheMinute * 60000
				: 15 * 60000;

		LinkedBlockingQueue<InnerTransferData> calculateProxyDataQueues = new LinkedBlockingQueue<>(
				metricMaxSize);
		if (logger.isInfoEnabled()) {
			logger.info("start max memory cached data length=" + metricMaxSize);
		}
		middleQueue = new ArrayBlockingQueue<>(
				((int) (batch_send_max_size * 1.5)));
		datasCacheQueue = new PersistableQueueProxy(calculateProxyDataQueues,
				TransferDataType.MetricData);

		sendDriver = new TransferSendDriver();
		middleDataDispatcher = new MiddleDataDispatch();

		started = true;
		new Thread(sendDriver, "TransferSendDriver").start();
		new Thread(middleDataDispatcher, "middleDataDispatcher").start();
		if (logger.isInfoEnabled()) {
			logger.info("MetricDataTransferSender init end");
		}
	}

	/*
	 * 实时数据发送
	 */
	private int doSend(List<InnerTransferData> datas) {
		try {
			this.middleQueue.drainTo(datas, this.batch_send_max_size);
			if (datas.size() > 0) {
				if (isConnectionValid()) {
					if (datas.size() > 0) {
						return sendData0(datas);
					}
				} else {
					logger.info("Send real time data,can't connect to DHS,time="
							+ DateUtil
									.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					// drop this batch data list.
					blockAndCreateConnection(true);
				}
			}
		} catch (ClosedChannelException e) {
			logger.error(e.getMessage(), e);
			blockAndCreateConnection(false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}

	private boolean isConnectionValid() {
		try {
			return sender.isValid();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("isConnectionValid", e);
			}
		}
		return false;
	}

	private int sendData0(List<InnerTransferData> datas) throws IOException {
		return sender.sendData(datas);
	}

	private void closeInvalidConnection() {
		try {
			sender.close();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("closeInvalidConnection", e);
			}
		}
	}

	/**
	 * 探测，直到能够连接成功。
	 */
	private void blockAndCreateConnection(boolean validCheck) {
		if (validCheck && isConnectionValid()) {
			if (logger.isWarnEnabled()) {
				logger.warn("blockAndCreateConnection connection is valid.stop to create a new one.");
			}
			return;
		} else {
			closeInvalidConnection();
		}
		int connectCount = 1;

		while (true) {
			try {
				sender.init();
				break;
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"start connection to processor fail.retry.connectCount="
									+ connectCount++, e);
				}
				synchronized (this) {
					try {
						this.wait(5000);
					} catch (InterruptedException e1) {
					}
				}
			}
		}
	}

	@Override
	public void sendData(TransferData calculateData) {
		if (logger.isTraceEnabled()) {
			logger.trace("sendMetricData start dataType="
					+ calculateData.getDataType());
		}
		InnerTransferData innerTransferData = new InnerTransferData();
		innerTransferData.setData(calculateData.getData());
		innerTransferData.setDataType(calculateData.getDataType().ordinal());
		innerTransferData.setCacheTime(System.currentTimeMillis());
		datasCacheQueue.add(innerTransferData);
		if (logger.isTraceEnabled()) {
			logger.trace("sendMetricData end");
		}
	}

	private class TransferSendDriver implements Runnable {
		@Override
		public void run() {
			List<InnerTransferData> datas = new ArrayList<>(batch_send_max_size);
			while (started) {
				try {
					int size = doSend(datas);
					if (size < 0) {
						// TODO:重试发送数据，定义重试的次数
						// continue;
					} else if (size == 0) {
						synchronized (this) {
							try {
								this.wait(interval);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				} finally {
					datas.clear();
				}
			}
		}
	}

	private class MiddleDataDispatch implements Runnable {
		private boolean lossMode = false;// lossTime模式是否开启，如果开启，循环消耗超时的指标数据。

		@Override
		public void run() {
			InnerTransferData data = null;
			while (started) {
				try {
					data = datasCacheQueue.take();
				} catch (InterruptedException e1) {
				}
				if (lossMode && !isConnectionValid()) {
					try {
						do {
							lossMode = !middleQueue.offer(data, 5,
									TimeUnit.NANOSECONDS);
						} while (lossMode && isDataValid(data));
					} catch (InterruptedException e) {
					}
				} else if (isDataValid(data)) {
					try {
						boolean result = middleQueue.offer(data, 500,
								TimeUnit.MILLISECONDS);
						if (result) {
							lossMode = false;
							continue;
						} else if (!isConnectionValid()) {
							lossMode = true;
							try {
								do {
									lossMode = !middleQueue.offer(data, 5,
											TimeUnit.NANOSECONDS);
								} while (lossMode && isDataValid(data));
							} catch (InterruptedException e) {
							}
						}
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("run MiddleDataDispatch error.", e);
						}
					}
				}
			}
		}
	}

	private boolean isDataValid(InnerTransferData data) {
		return data != null
				&& ((System.currentTimeMillis() - data.getCacheTime()) < cachedDataExpireTimes);
	}
}
