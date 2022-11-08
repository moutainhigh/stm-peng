/**
 * 
 */
package com.mainsteam.stm.pluginserver.cable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mainsteam.stm.pluginserver.PluginSessionExecutorRunner;

/**
 * @author ziw
 * 
 */
public class MultiRunnerCoreCable {
	private final int maxActivity;
	private final int minIdel;
	private MultiRunnerCoreCable next;
	private MultiRunnerCoreCable pre;
	private MultiRunnerCoreCableDriver multiCoreCableDriver;
	private List<RunnerCoreCable> runnerCoreCables;
	private int visitIndex = 0;
	private final String name;
	private final RunnerCoreCableRangeLevel coreCableRange;

	/**
	 * @param maxActivity
	 * @param minIdel
	 * @param nextLevel
	 * @param preLevel
	 * @param level
	 * @param threadSize
	 */
	public MultiRunnerCoreCable(int maxActivity, int minIdel,
			RunnerCoreCableRangeLevel coreCableRange, String name) {
		this.maxActivity = maxActivity;
		this.minIdel = minIdel;
		this.coreCableRange = coreCableRange;
		this.runnerCoreCables = new CopyOnWriteArrayList<>();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the coreCableRange
	 */
	public final RunnerCoreCableRangeLevel getCoreCableRange() {
		return coreCableRange;
	}

	public int getRunnerCoreCableSize() {
		return runnerCoreCables.size();
	}

	public RunnerCoreCable getRunnerCoreCable(int index) {
		return runnerCoreCables.get(index);
	}

	/**
	 * 启动方法，外部保证线程安全。
	 */
	public void start() {
		if (multiCoreCableDriver == null) {
			multiCoreCableDriver = new MultiRunnerCoreCableDriver(this, name);
			new Thread(multiCoreCableDriver, multiCoreCableDriver.getName())
					.start();
		}
	}

	/**
	 * @return the next
	 */
	public final MultiRunnerCoreCable getNext() {
		return next;
	}

	/**
	 * @param next
	 *            the next to set
	 */
	protected final void setNext(MultiRunnerCoreCable next) {
		this.next = next;
	}

	/**
	 * @return the pre
	 */
	public final MultiRunnerCoreCable getPre() {
		return pre;
	}

	/**
	 * @param pre
	 *            the pre to set
	 */
	protected final void setPre(MultiRunnerCoreCable pre) {
		this.pre = pre;
	}

	/**
	 * @return the maxActivity
	 */
	public final int getMaxActivity() {
		return maxActivity;
	}

	/**
	 * @return the minIdel
	 */
	public final int getMinIdel() {
		return minIdel;
	}

	/**
	 * @return the nextLevel
	 */
	public final MultiRunnerCoreCable getNextLevel() {
		return next;
	}

	/**
	 * @return the preLevel
	 */
	public final MultiRunnerCoreCable getPreLevel() {
		return pre;
	}

	/**
	 * 取下一个处理线路
	 * 
	 * @return
	 */
	public RunnerCoreCable next() {
		RunnerCoreCable cable = null;
		if (this.runnerCoreCables.size() == 0) {
			return cable;
		} else if (visitIndex >= this.runnerCoreCables.size()) {
			visitIndex = 0;
		}
		while (visitIndex < this.runnerCoreCables.size()) {
			RunnerCoreCable coreCable = this.runnerCoreCables.get(visitIndex++);
			if (coreCable.isClosed()) {
				visitIndex--;
				this.runnerCoreCables.remove(visitIndex);
			} else {
				cable = coreCable;
				break;
			}
		}
		return cable;
	}

	/**
	 * 
	 * @param time
	 * @return >0:比当前级别要求的慢,<0:比当前级别要求的快
	 */
	public int isOutofLevel(long time) {
		if (time < coreCableRange.getRange()[0]) {
			return -1;
		} else if (time > coreCableRange.getRange()[1]) {
			return 1;
		}
		return 0;
	}

	/**
	 * 取下一个请求可执行对象
	 * 
	 * @return
	 */
	public PluginSessionExecutorRunner nextRunner() {
		RunnerCoreCable coreCable = next();
		return coreCable == null ? null : coreCable.poll();
	}

	/**
	 * 加入一个线路
	 * 
	 * @param coreCable
	 */
	public void addCoreCable(RunnerCoreCable coreCable) {
		if (coreCable.isClosed()) {
			return;
		}
		this.runnerCoreCables.add(coreCable);
		coreCable.setMultiCoreCable(this);
	}

	/**
	 * 删除一个线路
	 * 
	 * @param coreCable
	 */
	public synchronized void removeCoreCable(RunnerCoreCable coreCable) {
		if (this.runnerCoreCables.remove(coreCable)) {
			coreCable.setMultiCoreCable(null);
		}
	}

	public int getActiveCount() {
		return this.multiCoreCableDriver.getActiveCount();
	}

	public int getPoolSize() {
		return this.multiCoreCableDriver.getPoolSize();
	}

	public long getAllTaskSize() {
		return this.multiCoreCableDriver.getAllTaskSize();
	}
}
