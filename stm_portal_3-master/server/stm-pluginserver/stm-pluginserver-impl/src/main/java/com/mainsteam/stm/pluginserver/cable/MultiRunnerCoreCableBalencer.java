/**
 * 
 */
package com.mainsteam.stm.pluginserver.cable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 根据每个多芯数据处理线路的线路率来均衡。
 * 
 * @author ziw
 * 
 */
public class MultiRunnerCoreCableBalencer {

	private static final Log logger = LogFactory
			.getLog(MultiRunnerCoreCableBalencer.class);

	private MultiRunnerCoreCable[] multiCoreCables;

	private MultiRunnerCoreCable discoveryMultiRunnerCoreCable;

	private boolean started;

	private MultiRunnerCoreCableBalencerRunner balencerRunner;

	private RunnerCoreCableManager coreCableManager;
	
	/**
	 * 
	 */
	public MultiRunnerCoreCableBalencer() {
		multiCoreCables = new MultiRunnerCoreCable[4];
		multiCoreCables[0] = new MultiRunnerCoreCable(
				RunnerCoreCableRangeLevel.FIRST.getMaxActiveSize(),
				RunnerCoreCableRangeLevel.FIRST.getMinIdel(),
				RunnerCoreCableRangeLevel.FIRST, "MultiRunnerCoreCable-"
						+ RunnerCoreCableRangeLevel.FIRST.name());
		multiCoreCables[1] = new MultiRunnerCoreCable(
				RunnerCoreCableRangeLevel.SECOND.getMaxActiveSize(),
				RunnerCoreCableRangeLevel.SECOND.getMinIdel(),
				RunnerCoreCableRangeLevel.SECOND, "MultiRunnerCoreCable-"
						+ RunnerCoreCableRangeLevel.SECOND.name());
		multiCoreCables[2] = new MultiRunnerCoreCable(
				RunnerCoreCableRangeLevel.THREAD.getMaxActiveSize(),
				RunnerCoreCableRangeLevel.THREAD.getMinIdel(),
				RunnerCoreCableRangeLevel.THREAD, "MultiRunnerCoreCable-"
						+ RunnerCoreCableRangeLevel.THREAD.name());
		multiCoreCables[3] = new MultiRunnerCoreCable(
				RunnerCoreCableRangeLevel.LATEST.getMaxActiveSize(),
				RunnerCoreCableRangeLevel.LATEST.getMinIdel(),
				RunnerCoreCableRangeLevel.LATEST, "MultiRunnerCoreCable-"
						+ RunnerCoreCableRangeLevel.LATEST.name());

		multiCoreCables[0].setNext(multiCoreCables[1]);

		multiCoreCables[1].setPre(multiCoreCables[0]);
		multiCoreCables[1].setNext(multiCoreCables[2]);

		multiCoreCables[2].setPre(multiCoreCables[1]);
		multiCoreCables[2].setNext(multiCoreCables[3]);

		multiCoreCables[3].setPre(multiCoreCables[2]);

		discoveryMultiRunnerCoreCable = new MultiRunnerCoreCable(
				RunnerCoreCableRangeLevel.DISCOVERY.getMaxActiveSize(),
				RunnerCoreCableRangeLevel.DISCOVERY.getMinIdel(),
				RunnerCoreCableRangeLevel.DISCOVERY, "MultiRunnerCoreCable-"
						+ RunnerCoreCableRangeLevel.DISCOVERY.name());
	}

	/**
	 * @param coreCableManager
	 *            the coreCableManager to set
	 */
	public final void setCoreCableManager(
			RunnerCoreCableManager coreCableManager) {
		this.coreCableManager = coreCableManager;
	}

	public MultiRunnerCoreCable getMultiRunnerCoreCable(
			RunnerCoreCableRangeLevel level) {
		if (level == RunnerCoreCableRangeLevel.DISCOVERY) {
			return discoveryMultiRunnerCoreCable;
		}
		return multiCoreCables[level.ordinal()];
	}

	public synchronized void start() {
		if (started) {
			return;
		}
		started = true;

		for (MultiRunnerCoreCable c : multiCoreCables) {
			c.start();
			if (logger.isInfoEnabled()) {
				logger.info("start MultiRunnerCoreCable for "
						+ c.getCoreCableRange());
			}
		}
		discoveryMultiRunnerCoreCable.start();
		if (logger.isInfoEnabled()) {
			logger.info("start MultiRunnerCoreCable for "
					+ discoveryMultiRunnerCoreCable.getCoreCableRange());
		}
		balencerRunner = new MultiRunnerCoreCableBalencerRunner();
		new Thread(balencerRunner, "MultiRunnerCoreCableBalencerRunner")
				.start();
	}

	public MultiRunnerCoreCable selectMultiRunnerCoreCableForBanlencer() {
		/**
		 * 查找繁忙度最小的MultiRunnerCoreCable
		 */
		float min = Float.MAX_VALUE;
		MultiRunnerCoreCable minisizeMultiRunnerCoreCable = null;
		for (MultiRunnerCoreCable c : multiCoreCables) {
			float corecableSizePercent = ((float) c.getRunnerCoreCableSize())
					/ (float) c.getMaxActivity();
			if (corecableSizePercent < min) {
				min = corecableSizePercent;
				minisizeMultiRunnerCoreCable = c;
			}
		}
		return minisizeMultiRunnerCoreCable;
	}

	/**
	 * 获取为发现准备的数据处理线路
	 * 
	 * @return
	 */
	public MultiRunnerCoreCable getMultiRunnerCoreCableForDiscovery() {
		return discoveryMultiRunnerCoreCable;
	}

	public boolean judgeMoveCable(MultiRunnerCoreCable fromLevel,
			MultiRunnerCoreCable toLevel) {
		/**
		 * 根据已经拥有的corecable的百分比来确定.值越大，越忙。如果繁忙度相同倾向于级别低的corecable。
		 */
		MultiRunnerCoreCable higherCoreCable = null;
		MultiRunnerCoreCable lowerCoreCable = null;
		if (fromLevel.getCoreCableRange().ordinal() > toLevel
				.getCoreCableRange().ordinal()) {
			higherCoreCable = fromLevel;
			lowerCoreCable = toLevel;
		} else {
			higherCoreCable = toLevel;
			lowerCoreCable = fromLevel;
		}
		float higherCorecableSizePercent = (float) higherCoreCable
				.getRunnerCoreCableSize()
				/ (float) higherCoreCable.getMaxActivity();

		float lowerCorecableSizePercent = (float) lowerCoreCable
				.getRunnerCoreCableSize()
				/ (float) lowerCoreCable.getMaxActivity();
		return lowerCorecableSizePercent <= higherCorecableSizePercent ? lowerCoreCable == toLevel
				: higherCoreCable == toLevel;
	}

	private int compareBusy(MultiRunnerCoreCable fromLevel,
			MultiRunnerCoreCable toLevel) {
		float fromCorecableSizePercent = (float) fromLevel
				.getRunnerCoreCableSize() / (float) fromLevel.getMaxActivity();

		float toCorecableSizePercent = (float) toLevel.getRunnerCoreCableSize()
				/ (float) toLevel.getMaxActivity();
		return fromCorecableSizePercent == toCorecableSizePercent ? 0
				: (fromCorecableSizePercent < toCorecableSizePercent ? -1 : 1);
	}

	private class MultiRunnerCoreCableBalencerRunner implements Runnable {
		@Override
		public void run() {
			do {
				synchronized (this) {
					try {
						this.wait(30000);
					} catch (InterruptedException e) {
					}
				}
				Map<RunnerCoreCableRangeLevel, List<CoreCableMoveWrapper>> cableMoveWrappers = new HashMap<>(
						multiCoreCables.length);
				try {
					for (MultiRunnerCoreCable multiRunnerCoreCable : multiCoreCables) {
						adjustmentBalancer(multiRunnerCoreCable,
								cableMoveWrappers);
						if (logger.isDebugEnabled()) {
							int size = cableMoveWrappers
									.get(multiRunnerCoreCable
											.getCoreCableRange()) == null ? -1
									: cableMoveWrappers.get(
											multiRunnerCoreCable
													.getCoreCableRange())
											.size();
							StringBuilder b = new StringBuilder(
									"run cableMoveWrappers.");
							b.append(" level=").append(
									multiRunnerCoreCable.getCoreCableRange());
							b.append(" size=").append(size);
							logger.debug(b.toString());
						}
					}
					doMove(cableMoveWrappers);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("run", e);
					}
				}
			} while (started);
		}
	}

	private void doMove(
			Map<RunnerCoreCableRangeLevel, List<CoreCableMoveWrapper>> cableMoveWrappers) {
		for (Entry<RunnerCoreCableRangeLevel, List<CoreCableMoveWrapper>> levelWrappersEntry : cableMoveWrappers
				.entrySet()) {
			RunnerCoreCableRangeLevel fromLevel = levelWrappersEntry.getKey();
			List<CoreCableMoveWrapper> wrappers = levelWrappersEntry.getValue();
			MultiRunnerCoreCable src = multiCoreCables[fromLevel.ordinal()];

			for (Iterator<CoreCableMoveWrapper> iterator = wrappers.iterator(); iterator
					.hasNext();) {
				CoreCableMoveWrapper srcWrapper = (CoreCableMoveWrapper) iterator
						.next();
				int compareResult = compareBusy(src, srcWrapper.distMutilCable);
				if (compareResult > 0) {
					move(src, srcWrapper.distMutilCable, srcWrapper.cable);
					iterator.remove();
				} else if (cableMoveWrappers
						.containsKey(srcWrapper.distMutilCable
								.getCoreCableRange())) {
					List<CoreCableMoveWrapper> distLevelWrappers = cableMoveWrappers
							.get(srcWrapper.distMutilCable.getCoreCableRange());
					if (distLevelWrappers.size() > 0) {
						for (Iterator<CoreCableMoveWrapper> iterator2 = distLevelWrappers
								.iterator(); iterator2.hasNext();) {
							CoreCableMoveWrapper distWrapper = (CoreCableMoveWrapper) iterator2
									.next();
							if (distWrapper.distMutilCable == src) {
								// swap the srcWrapper.cable and
								// distWrapper.cable
								move(src, srcWrapper.distMutilCable,
										srcWrapper.cable);
								iterator.remove();
								move(srcWrapper.distMutilCable, src,
										distWrapper.cable);
								iterator2.remove();
								break;
							}
						}
					}
				}
			}
		}
	}

	private void move(MultiRunnerCoreCable src, MultiRunnerCoreCable dist,
			RunnerCoreCable c) {
		src.removeCoreCable(c);
		dist.addCoreCable(c);
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder("move the RunnerCoreCable ");
			b.append(c.getKey());
			b.append(" from ").append(src.getCoreCableRange()).append(" to ")
					.append(dist.getCoreCableRange());
			logger.info(b.toString());
		}
	}

	private boolean removeTimeoutRunnerCoreCable(
			MultiRunnerCoreCable multiRunnerCoreCable, RunnerCoreCable c) {
		long currentTime = System.currentTimeMillis();
		boolean removed = false;
		if ((c.getLastComputeTime() > 0)
				&& (currentTime - c.getLastComputeTime() > RunnerCoreCableRangeLevel
						.getMaxRangeTop())) {
			if (coreCableManager != null) {
				removed = coreCableManager
						.closeRunnerCoreCable(
								c,
								multiRunnerCoreCable.getCoreCableRange() == RunnerCoreCableRangeLevel.DISCOVERY);
			} else {
				multiRunnerCoreCable.removeCoreCable(c);
				c.close();
				removed = true;
			}
		}
		return removed;
	}

	private void fillMoveWrapper(
			RunnerCoreCable c,
			MultiRunnerCoreCable dist,
			RunnerCoreCableRangeLevel level,
			Map<RunnerCoreCableRangeLevel, List<CoreCableMoveWrapper>> cableMoveWrappers) {
		CoreCableMoveWrapper moveWrapper = new CoreCableMoveWrapper();
		moveWrapper.cable = c;
		moveWrapper.distMutilCable = dist;
		if (cableMoveWrappers.containsKey(level)) {
			cableMoveWrappers.get(level).add(moveWrapper);
		} else {
			List<CoreCableMoveWrapper> wrappers = new ArrayList<>();
			wrappers.add(moveWrapper);
			cableMoveWrappers.put(level, wrappers);
		}
	}

	private MultiRunnerCoreCable selectMoveableMultiRunnerCoreCable(
			MultiRunnerCoreCable pre, MultiRunnerCoreCable next,
			MultiRunnerCoreCable src, RunnerCoreCable c) {
		int rangeFlag = src.isOutofLevel(c.getAvgComputeOffsetTime());
		if (rangeFlag > 0) {
			/**
			 * 将当前的处理线路降级
			 */
			if (next != null && compareBusy(src, next) > 0) {
				return next;
			}
		}
		if (rangeFlag < 0) {
			/**
			 * 将当前的处理线路升级
			 */
			if (pre != null && compareBusy(src, pre) > 0) {
				return pre;
			}
		}
		return null;
	}

	/**
	 * 调整多芯处理线路里内部的处理线路的分配
	 * 
	 * @param multiRunnerCoreCable
	 */
	private void adjustmentBalancer(
			MultiRunnerCoreCable multiRunnerCoreCable,
			Map<RunnerCoreCableRangeLevel, List<CoreCableMoveWrapper>> cableMoveWrappers) {
		int length = multiRunnerCoreCable.getRunnerCoreCableSize();
		RunnerCoreCableRangeLevel level = multiRunnerCoreCable
				.getCoreCableRange();
		MultiRunnerCoreCable pre = multiRunnerCoreCable.getPre();
		MultiRunnerCoreCable next = multiRunnerCoreCable.getNext();
		if (pre == null
				&& next == null
				|| multiRunnerCoreCable.getActiveCount() <= multiRunnerCoreCable
						.getMaxActivity()) {
			return;
		}
		for (int i = 0; i < length; i++) {
			RunnerCoreCable c = multiRunnerCoreCable.getRunnerCoreCable(i);
			/**
			 * 空闲时间超过最大值，则移除该数据处理线路
			 */
			if (removeTimeoutRunnerCoreCable(multiRunnerCoreCable, c)) {
				/**
				 * 如果移除失败，则指针不回退，否则永远执行不完。
				 */
				i--;
				length = multiRunnerCoreCable.getRunnerCoreCableSize();
				if (logger.isInfoEnabled()) {
					logger.info("adjustmentBalancer timeout reason to close corecable."
							+ c.getKey());
				}
				continue;
			}
			MultiRunnerCoreCable moveableMultiRunnerCoreCable = selectMoveableMultiRunnerCoreCable(
					pre, next, multiRunnerCoreCable, c);
			if (next != null) {
				fillMoveWrapper(c, moveableMultiRunnerCoreCable, level,
						cableMoveWrappers);
				if (logger.isDebugEnabled()) {
					logger.debug("ready move to the RunnerCoreCable "
							+ c.getKey() + " from "
							+ multiRunnerCoreCable.getCoreCableRange() + " to "
							+ moveableMultiRunnerCoreCable.getCoreCableRange());
				}
			}
		}
	}

	private class CoreCableMoveWrapper {
		private RunnerCoreCable cable;
		private MultiRunnerCoreCable distMutilCable;
	}
}
