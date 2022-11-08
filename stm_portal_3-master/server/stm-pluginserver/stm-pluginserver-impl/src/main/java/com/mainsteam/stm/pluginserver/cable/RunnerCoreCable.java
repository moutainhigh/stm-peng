/**
 * 
 */
package com.mainsteam.stm.pluginserver.cable;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.mainsteam.stm.pluginserver.PluginSessionExecutorRunner;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.pool.PluginSessionPool;

/**
 * 数据处理线路
 * 
 * 
 * 
 * @author ziw
 * 
 */
public class RunnerCoreCable {

	private volatile MultiRunnerCoreCable multiCoreCable;

	private final String key;

	private final int maxActivity;

	private AtomicInteger activity = new AtomicInteger(0);

	/**
	 * 计算次数计数器
	 */
	private AtomicLong computeCounter = new AtomicLong(0);

	/**
	 * 计算的平均时间
	 */
	private long avgComputeOffsetTime;

	/**
	 * 累计执行时间
	 */
	private AtomicLong sumComputeOffsetTime = new AtomicLong(0);

	/**
	 * 吞吐量时间
	 * 
	 * 从队列为空，开始计算时间偏移量，直到队列再次为空
	 */
	private long throughputTime = 0;

	/**
	 * 吞吐的数据个数
	 */
	private AtomicLong throughputComputeCount = new AtomicLong(0);

	/**
	 * 吞吐量，单位为个/秒
	 */
	private float throughput;

	/**
	 * 计算吞吐时间偏移量的开始时间。
	 */
	private long throughputFistComputeTime = 0;

	/**
	 * 最近一次执行时间
	 */
	private long lastComputeTime;

	private final ConcurrentLinkedDeque<PluginSessionExecutorRunner> queue;

	private volatile boolean closed = false;

	private volatile long toDropBatch = Long.MIN_VALUE;

	/**
	 * @param key
	 * @param maxActivity
	 * @param minIdel
	 */
	public RunnerCoreCable(String key, PluginSessionPool pool, MultiRunnerCoreCable coreCable) {
		this.key = key;
		this.multiCoreCable = coreCable;
		this.maxActivity = pool.getMaxActive();
		this.activity = new AtomicInteger(0);
		this.queue = new ConcurrentLinkedDeque<>();
		this.multiCoreCable.addCoreCable(this);
	}

	/**
	 * @return the maxActivity
	 */
	public final int getMaxActivity() {
		return maxActivity;
	}

	/**
	 * @return the closed
	 */
	public final boolean isClosed() {
		return closed;
	}

	/**
	 * @return the activity
	 */
	public final int getActivity() {
		return activity.get();
	}

	/**
	 * 获取待处理的数据个数
	 * 
	 * @return
	 */
	public final int getRemainingSize() {
		return this.queue.size();
	}

	/**
	 * @return the key
	 */
	public final String getKey() {
		return key;
	}

	public void setMultiCoreCable(MultiRunnerCoreCable coreCable) {
		this.multiCoreCable = coreCable;
	}

	public MultiRunnerCoreCable getMultiCoreCable() {
		return this.multiCoreCable;
	}

	/**
	 * 输入一个plugin执行请求
	 * 
	 * @param request
	 * @return true:成功,false:失败
	 * @throws InterruptedException
	 */
	public void put(PluginSessionExecutorRunner request) {
		/**
		 * 立即执行指标，优先处理。immediate数据，先入后出。
		 */
		if (request.getPluginRequest().getPluginRequestType() == PluginRequestEnum.immediate) {
			queue.addFirst(request);
		} else {
			queue.addLast(request);
		}
	}

	/**
	 * 
	 * 取出一个plugin执行请求
	 * 
	 * @return
	 */
	public PluginSessionExecutorRunner poll() {
		PluginSessionExecutorRunner req = null;
		long activityCount = activity.get();
		if (activityCount >= maxActivity) {
			return null;
		}
		req = queue.poll();
		if (req != null) {
			// if (maxActivity > 1) {
			// activity.incrementAndGet();
			// } else {
			activity.incrementAndGet();
			// }
			/**
			 * 准备计算吞吐量时间
			 */
			if (throughputFistComputeTime == 0) {
				throughputFistComputeTime = System.currentTimeMillis();
				throughputComputeCount.set(0);
			}
		} else if (activityCount == 0) {
			/**
			 * 队列内容处理完毕，重新开始计算吞吐量时间。
			 */
			throughputFistComputeTime = 0;
		}
		return req;
	}

	private void computeAvgExecuteTime0(long offsetTime) {
		// if (activity.get() > 0) {
		// }
		long current = System.currentTimeMillis();
		lastComputeTime = current;
		long count = computeCounter.incrementAndGet();
		long sum = sumComputeOffsetTime.addAndGet(offsetTime);
		avgComputeOffsetTime = sum / count;
		if (throughputFistComputeTime > 0) {
			throughputTime = current - throughputFistComputeTime;
			long tcc = throughputComputeCount.incrementAndGet();
			if (throughputTime == 0) {
				throughput = 0;
			} else {
				throughput = (float) (tcc * 1000) / (throughputTime);
			}
		}
		activity.decrementAndGet();
	}

	public void computeAvgExecuteTime(long offsetTime) {
		// if (maxActivity > 1) {
		// synchronized (this) {
		// computeAvgExecuteTime0(offsetTime);
		// }
		// } else {
		computeAvgExecuteTime0(offsetTime);
		// }
	}

	/**
	 * @return the throughputTime
	 */
	public final long getThroughputTime() {
		return throughputTime;
	}

	/**
	 * @return the throughputComputeCount
	 */
	public final long getThroughputComputeCount() {
		return throughputComputeCount.get();
	}

	/**
	 * @return the throughput
	 */
	public final float getThroughput() {
		return throughput;
	}

	/**
	 * @return the throughputFistComputeTime
	 */
	public final long getThroughputFistComputeTime() {
		return throughputFistComputeTime;
	}

	/**
	 * @return the computeCounter
	 */
	public final long getComputeCounter() {
		return computeCounter.get();
	}

	/**
	 * @return the lastComputeTime
	 */
	public final long getLastComputeTime() {
		return lastComputeTime;
	}

	/**
	 * @return the avgComputeOffsetTime
	 */
	public final long getAvgComputeOffsetTime() {
		return avgComputeOffsetTime;
	}

	/**
	 * @return the sumComputeOffsetTime
	 */
	public final long getSumComputeOffsetTime() {
		return sumComputeOffsetTime.get();
	}

	public void close() {
		this.closed = true;
		if (queue != null) {
			queue.clear();
			// queue = null;
		}
	}

	/**
	 * @return the toDropBatch
	 */
	public final long getToDropBatch() {
		return toDropBatch;
	}

	/**
	 * @param toDropBatch
	 *            the toDropBatch to set
	 */
	public final void setToDropBatch(long toDropBatch) {
		this.toDropBatch = toDropBatch;
	}
}
