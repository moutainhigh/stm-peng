/**
 * 
 */
package com.mainsteam.stm.pluginserver.cable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginserver.PluginSessionExecutorRunner;

/**
 * @author ziw
 * 
 */
public class MultiRunnerCoreCableDriver implements Runnable {

	private static final Log logger = LogFactory
			.getLog(MultiRunnerCoreCableDriver.class);

	private final MultiRunnerCoreCable multiCoreCable;

	private final ThreadPoolExecutor executorService;

//	private final PluginSessionResultCacheByBatch pluginSessionResultCacheByBatch;

	private final String name;

	private final static long wait_count_time_lower = 10;

	private final static long wait_count_time_faster = 3;

	private// volatile
	boolean running;

	/**
	 * 
	 */
	public MultiRunnerCoreCableDriver(MultiRunnerCoreCable multiCoreCable,
			String name) {
		this.multiCoreCable = multiCoreCable;
		this.name = name + "Driver";
//		this.pluginSessionResultCacheByBatch = pluginSessionResultCacheByBatch;
		ThreadFactory t = new ThreadFactory() {
			int counter = 0;

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, MultiRunnerCoreCableDriver.this.name + "_"
						+ counter++ + "-");
			}
		};
		RejectedExecutionHandler handler = new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r,
					ThreadPoolExecutor executor) {
				try {
					executor.getQueue().put(r);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		int max = multiCoreCable.getMaxActivity() > multiCoreCable.getMinIdel() ? multiCoreCable
				.getMaxActivity() : multiCoreCable.getMinIdel() + 1;
		int core = max;
		executorService = new ThreadPoolExecutor(core, max, 90,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(max), t,
				handler);
		running = true;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	@Override
	public void run() {
		int default_max_count = multiCoreCable.getCoreCableRange()
				.getMaxActiveSize();
		int max_count = default_max_count;
		// int max_count = max_wait_count;
		int waitCount = 0;
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder("start this driver.");
			b.append(" driver.name=").append(this.name);
			b.append(" driver.max_wait_count=").append(max_count);
			b.append(" driver.default_max_count=").append(default_max_count);
			b.append(" driver.wait_count_time_faster(ms)=").append(
					wait_count_time_faster);
			b.append(" driver.wait_count_time_lower(ms)=").append(
					wait_count_time_lower);
			logger.info(b.toString());
		}
		int runnerCoreCableSize = multiCoreCable.getRunnerCoreCableSize();
		int emptyWait = 0;
		final int countFactor = 10;
		max_count = runnerCoreCableSize * countFactor;
		while (running) {
			PluginSessionExecutorRunner run = null;
			long offsetTime = 0;
			try {
				int runnerCoreCableSizeNew = multiCoreCable
						.getRunnerCoreCableSize();
				if (runnerCoreCableSizeNew != runnerCoreCableSize) {
					max_count = runnerCoreCableSizeNew * countFactor;
					runnerCoreCableSize = runnerCoreCableSizeNew;
				}
				run = multiCoreCable.nextRunner();
				waitCount++;
				if (run != null) {
					emptyWait = 0;
					offsetTime = System.currentTimeMillis();
					executorService.execute(run);
					offsetTime = System.currentTimeMillis() - offsetTime;
				} else {
					emptyWait++;
				}
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error("run", e);
				}
			}
			if (offsetTime >= wait_count_time_faster) {
				Thread.yield();
				emptyWait = 0;
				waitCount = 0;
			} else if (waitCount >= max_count) {
				long wait_count_time = wait_count_time_faster;
				if (run == null && emptyWait >= runnerCoreCableSize) {
					wait_count_time = wait_count_time_lower;
				}
				/**
				 * 等待5ms，等待cpu。
				 */
				// synchronized (this) {
				try {
					Thread.sleep(wait_count_time);
					// this.wait(wait_count_time);
				} catch (InterruptedException e) {
				}
				// }
				waitCount = 0;
				emptyWait = 0;
			}
		}
	}

	public void stop() {
		this.running = false;
	}

	public int getActiveCount() {
		return this.executorService.getActiveCount();
	}

	public int getPoolSize() {
		return this.executorService.getPoolSize();
	}

	public long getAllTaskSize() {
		return this.executorService.getTaskCount();
	}
}
