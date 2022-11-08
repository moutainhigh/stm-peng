package com.mainsteam.stm.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.mainsteam.stm.transfer.config.TransferConfig;
import com.mainsteam.stm.transfer.config.TransferConfigChangeListener;
import com.mainsteam.stm.transfer.config.TransferConfigManager;
import com.mainsteam.stm.transfer.obj.InnerTransferData;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;
import com.mainsteam.stm.transfer.queue.PersistableQueue;
import com.mainsteam.stm.transfer.queue.PersistableQueueProxy;

/**
 * @author ziw
 * 
 */
public class TransferDataDispatcherImpl implements BeanPostProcessor,
		ApplicationListener<ContextRefreshedEvent>,
		TransferConfigChangeListener,TransferDataDispatcher {

	private static final Log logger = LogFactory
			.getLog(TransferDataDispatcherImpl.class);

	/**
	 * 线程池
	 */
	// private ThreadPoolExecutor threadPool;

	private LinkedBlockingQueue<InnerTransferData>[] calculateDataQueues;

	private List<TransferDataHandler>[] transferDataHandlers;

	private DataDispatch[] dispatchDrivers;

	private boolean driverStarted;

	private TransferDataType[] transferTypes = TransferDataType.values();

	private TransferConfigManager transferConfigManager;

	public TransferDataDispatcherImpl() {
	}

	public void setTransferConfigManager(
			TransferConfigManager transferConfigManager) {
		this.transferConfigManager = transferConfigManager;
		this.transferConfigManager.registerListener(this);
	}

	@SuppressWarnings("unchecked")
	public synchronized void start() {
		if (calculateDataQueues != null) {
			if (logger.isWarnEnabled()) {
				logger.warn("start TransferDataDispatcher has already start.");
			}
			return;
		}

		dispatchDrivers = new DataDispatch[transferTypes.length];
		calculateDataQueues = new LinkedBlockingQueue[transferTypes.length];
		transferDataHandlers = new List[transferTypes.length];
		for (int i = 0; i < transferTypes.length; i++) {
			final TransferDataType transferDataType = transferTypes[i];
			TransferConfig config = transferConfigManager
					.getTransferConfig(transferDataType);
			int fixed_max_threads = config.getMaxThreads();
			int fixed_core_threads = config.getCoreThreads();
			calculateDataQueues[i] = new LinkedBlockingQueue<>(
					config.getTransferQueueMaxSize());
			transferDataHandlers[i] = new ArrayList<>();
			dispatchDrivers[i] = new DataDispatch(
					"TransferDataDispatcherDriver-" + transferTypes[i].name());

			ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
					fixed_core_threads, fixed_max_threads, 0,
					TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
							100), new ThreadFactory() {
						private int number = 0;

						@Override
						public Thread newThread(Runnable r) {
							StringBuilder b = new StringBuilder();
							b.append("TransferDataDispatcher-");
							b.append(transferDataType.name()).append('-');
							b.append(number++);
							return new Thread(r, b.toString());
						}
					});
			threadPool
					.setRejectedExecutionHandler(new RejectedExecutionHandler() {
						@Override
						public void rejectedExecution(Runnable r,
								ThreadPoolExecutor executor) {
							try {
								executor.getQueue().put(r);
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error(
											"rejectedExecution:"
													+ e.getMessage(), e);
								}
							}
						}
					});
			dispatchDrivers[i].transferDataHandlers = transferDataHandlers[i];
			dispatchDrivers[i].calculateDataQueue = new PersistableQueueProxy(
					calculateDataQueues[i], transferDataType);
			dispatchDrivers[i].calculateDataQueueCapacity = config
					.getTransferQueueMaxSize();
			dispatchDrivers[i].threadPool = threadPool;
		}
	}

	public void resizeMaxThreads(TransferDataType dataType, int maxThreads) {
	}

	public List<Integer> getDispatchListSize() {
		List<Integer> list = new ArrayList<>(transferTypes.length);
		for (int i = 0; i < transferTypes.length; i++) {
			list.add(dispatchDrivers[i].calculateDataQueue.size());
		}
		return list;
	}

	public DataDispatchMonitorResult getDataDispatchMonitorResults(
			TransferDataType dataType) {
		if (dataType != null) {
			DataDispatch d = dispatchDrivers[dataType.ordinal()];
			if (d != null && d.monitor) {
				DataDispatchMonitorResult result = new DataDispatchMonitorResult();
				result.setActiveCount(d.getAliveCount());
				result.setAllFlowCount(d.allDataCount);
				result.setDataType(this.transferTypes[dataType.ordinal()]);
				result.setMonitor(d.monitor);
				result.setRemainingCount(d.getRemainingSize());
				result.setThroughput(d.throughput);
				return result;
			}
		}
		return null;
	}

	public void startMonitor(TransferDataType dataType) {
		if (dataType != null) {
			DataDispatch d = dispatchDrivers[dataType.ordinal()];
			d.startMonitor();
		}
	}

	public void stopMonitor(TransferDataType dataType) {
		if (dataType != null) {
			DataDispatch d = dispatchDrivers[dataType.ordinal()];
			d.stopMonitor();
		}
	}

	public synchronized void stop() {
		driverStarted = false;
		for (int i = 0; i < dispatchDrivers.length; i++) {
			dispatchDrivers[i].threadPool.shutdown();
		}
	}

	public void dispatch(InnerTransferData[] datas) {
		for (InnerTransferData data : datas) {
			dispatchDrivers[data.getDataType()].calculateDataQueue.add(data);
		}
	}

	private class DataDispatch extends Thread {
		private PersistableQueue<InnerTransferData> calculateDataQueue;
		private List<TransferDataHandler> transferDataHandlers;
		private ThreadPoolExecutor threadPool;
		private int calculateDataQueueCapacity;
		private long throughputStartTime;
		private long allDataCount;
		private long throughputDataCount;
		private float throughput = -0.1f;
		private boolean monitor;
		private long interval = 60000L;// 60second

		DataDispatch(String name) {
			super.setName(name);
		}

		/**
		 * @param monitor
		 *            the startMonitor to set
		 */
		public final void startMonitor() {
			this.monitor = true;
			this.throughputStartTime = 0;
		}

		public final void stopMonitor() {
			this.monitor = false;
		}

		public final int getRemainingSize() {
			return calculateDataQueue.size();
		}

		public final int getAliveCount() {
			return threadPool.getActiveCount();
		}

		@Override
		public void run() {
			while (driverStarted) {
				InnerTransferData data = null;
				try {
					data = calculateDataQueue.take();
				} catch (InterruptedException e1) {
				}
				if (transferDataHandlers == null
						|| transferDataHandlers.size() <= 0) {
					allDataCount++;
					continue;
				}
				if (data == null) {
					allDataCount++;
					continue;
				}
				final TransferData transferData = new TransferData();
				transferData.setData(data.getData());
				transferData.setDataType(transferTypes[data.getDataType()]);
				Runnable r = new Runnable() {
					@Override
					public void run() {
						try {
							for (TransferDataHandler handler : transferDataHandlers) {
								handler.handle(transferData);
							}
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error(
										"DataDispatch error:" + e.getMessage(),
										e);
							}
						}
					}
				};
				if (monitor && throughputStartTime <= 0) {
					throughputStartTime = System.currentTimeMillis();
				}
				threadPool.execute(r);
				allDataCount++;
				if (monitor) {
					throughputDataCount++;
					long current = System.currentTimeMillis();
					long offset = current - throughputStartTime;
					if (offset >= interval && current != offset) {
						throughput = throughputDataCount * 1000 / offset;
						throughputDataCount = 0;
						throughputStartTime = 0;
					}
				}
			}
		}
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String name)
			throws BeansException {
		if (bean instanceof TransferDataHandler) {
			TransferDataHandler h = (TransferDataHandler) bean;
			this.transferDataHandlers[h.getDataTransferType().ordinal()].add(h);
			if (logger.isInfoEnabled()) {
				StringBuffer b = new StringBuffer();
				b.append("register TransferDataHandler beanId=");
				b.append(name);
				b.append(" beanClass=");
				b.append(bean.getClass());
				b.append(" dataTransferType=");
				b.append(h.getDataTransferType());
				if (logger.isInfoEnabled()) {
					logger.info(b.toString());
				}
			}
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String name)
			throws BeansException {
		return bean;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		driverStarted = true;
		if (event.getApplicationContext().getParent() == null) {
			TransferDataType[] transferTypes = TransferDataType.values();
			for (int i = 0; i < transferTypes.length; i++) {
				dispatchDrivers[i].start();
				if (logger.isInfoEnabled()) {
					logger.info("start  dispatchDrivers for "
							+ transferTypes[i]);
				}
			}
		}
	}

	@Override
	public synchronized void change() {
		for (int i = 0; i < transferTypes.length; i++) {
			TransferConfig config = transferConfigManager.getTransferConfig(
					transferTypes[i]).clone();
			dispatchDrivers[i].threadPool.setCorePoolSize(config
					.getCoreThreads());
			dispatchDrivers[i].threadPool.setMaximumPoolSize(config
					.getMaxThreads());
			if (dispatchDrivers[i].calculateDataQueueCapacity != config
					.getTransferQueueMaxSize()) {
				LinkedBlockingQueue<InnerTransferData> newQueue = new LinkedBlockingQueue<>(
						config.getTransferQueueMaxSize());
				dispatchDrivers[i].calculateDataQueue
						.replaceMemoryQueue(newQueue);
			}
		}
	}
}
