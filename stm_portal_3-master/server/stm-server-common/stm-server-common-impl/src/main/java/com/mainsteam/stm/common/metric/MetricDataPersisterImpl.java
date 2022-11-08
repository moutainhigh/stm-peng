package com.mainsteam.stm.common.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricDataPO;
import com.mainsteam.stm.common.metric.obj.MetricInfoDataPO;

public class MetricDataPersisterImpl implements MetricDataBatchPersister,
		MetricDataPersisterMonitor, Runnable {

	private static final Log logger = LogFactory
			.getLog(MetricDataBatchPersister.class);

	private SqlSessionFactory myBatisSqlSessionFactory;

	private MetricTableNameManager metricTableNameCache;

	private int capaticy;

	private String[] sessionNames;

	private ArrayBlockingQueue<MetricData> queue;

	private boolean metricSplitTable;

	// private int remainingSize = 0;
	private int activeSize = 0;
	private long overSumSize;
	private long lossTime = 0;
	private int averageSpeed = -1;
	private long errCount;
	private long batchLossTime = -1;
	private int batchMetricSpeed;
	private int batchMetricCount;
	private Date batchDateTime;

	private Date startDateTime;

	private ThreadPoolExecutor threadPool;
	
	public MetricDataPersisterImpl(int capaticy, boolean metricSplitTable,
			SqlSessionFactory myBatisSqlSessionFactory,
			MetricTableNameManager metricTableNameCache,
			int fixed_core_threads, int fixed_max_threads,
			final String... sessionNames) {
		this.sessionNames = sessionNames;
		this.queue = new ArrayBlockingQueue<>(capaticy * 10, true);
		this.capaticy = capaticy;
		this.metricSplitTable = metricSplitTable;
		this.metricTableNameCache = metricTableNameCache;
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;

		// 线程池默认开户10个，最大线程数暂定200。
		if (fixed_core_threads > 0 && fixed_max_threads > 0
				&& fixed_max_threads >= fixed_core_threads) {
			threadPool = new ThreadPoolExecutor(fixed_core_threads,
					fixed_max_threads, 0, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(fixed_max_threads),
					new ThreadFactory() {
						private int number = 0;

						@Override
						public Thread newThread(Runnable r) {
							StringBuilder b = new StringBuilder();
							b.append("MetricDataPersister-");
							b.append(Arrays.toString(sessionNames)).append('-');
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
		}
	}

	// private synchronized void increaseRemain(int size) {
	// this.remainingSize += size;
	// }

	@Override
	public long getErrCount() {
		return errCount;
	}

	@Override
	public long getBatchLossTime() {
		return batchLossTime;
	}

	@Override
	public int getBatchMetricCount() {
		return batchMetricCount;
	}

	@Override
	public int getRemainingSize() {
		return queue.size();
	}

	@Override
	public int getActiveSize() {
		return activeSize;
	}

	@Override
	public long getOverSumSize() {
		return overSumSize;
	}

	// private synchronized void decreaseRemain(int size) {
	// this.remainingSize -= size;
	// }

	private synchronized void increaseActive(int size) {
		this.activeSize += size;
	}

	@Override
	public Date getBatchDateTime() {
		return batchDateTime;
	}

	@Override
	public boolean saveData(MetricData data) {
		try {
			queue.put(data);
			// increaseRemain(1);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void singleTableRun(final List<MetricData> metricDatas) {
		// threadPool.execute(new Runnable() {
		// @Override
		// public void run() {
		long temp = System.currentTimeMillis();
		SqlSession session = getBatchSqlSession();
		try {
			for (String sessionName : sessionNames) {
				for (MetricData metricData : metricDatas) {
					if (metricData instanceof MetricDataPO
							|| metricData instanceof MetricInfoDataPO) {
						session.insert(sessionName, metricData);
					} else {
						MetricDataPO po = MetricDataPO.convert(metricData);
						session.insert(sessionName, po);
					}
				}
				// session.flushStatements();
				session.commit();
			}
		} catch (Exception e) {
			session.rollback();
			errCount += metricDatas.size();
			logger.error("", e);
		} finally {
			session.close();
		}
		computeSum(temp, metricDatas.size());
		// }
		// });
	}

	private synchronized void computeSum(long startTime, int dataSize) {
		this.batchLossTime = System.currentTimeMillis() - startTime;
		this.batchMetricCount = dataSize;
		this.batchMetricSpeed = (int) (this.batchLossTime / this.batchMetricCount);
		this.lossTime += this.batchLossTime;
		this.activeSize -= dataSize;
		this.overSumSize += dataSize;
		this.averageSpeed = (int) (this.lossTime / this.overSumSize);
	}

	private void spliteTableRun(List<MetricData> metricDatas) {
		/**
		 * 保存实时数据
		 */
		Map<String, List<MetricData>> groupedMetricData = new HashMap<>(1000);

		/*
		 * 指标数据分组
		 */
		for (MetricData metricData : metricDatas) {
			List<MetricData> dataList = groupedMetricData.get(metricData
					.getMetricId());
			if (dataList == null) {
				dataList = new ArrayList<>();
				groupedMetricData.put(metricData.getMetricId(), dataList);
			}
			dataList.add(metricData);
		}
		for (Entry<String, List<MetricData>> listEntry : groupedMetricData
				.entrySet()) {
			final String metricId = listEntry.getKey();
			final List<MetricData> datas = listEntry.getValue();
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					long temp = System.currentTimeMillis();
					List<MetricDataPO> dataPs = new ArrayList<>(datas.size());
					SqlSession session = getBatchSqlSession();
					List<MetricData> toInsertDatas = null;
					try {
						String tableName = metricTableNameCache
								.getRealtimePerfMetricTable(metricId);
						if (null == tableName) {
							tableName = metricTableNameCache
									.createRealtimeMetricTable(metricId,
											session);
							session.commit();
							session.close();
							session = getBatchSqlSession();
						}
						if (null == tableName) {
							if (logger.isErrorEnabled()) {
								logger.error("metric's realtime table not exist."
										+ metricId);
							}
						} else {
							for (MetricData metricData : datas) {
								MetricDataPO po = MetricDataPO
										.convert(metricData);
								dataPs.add(po);
								po.setTableName(tableName);
								if (metricTableNameCache
										.isPerformantMetricDataExist(
												metricId,
												metricData
														.getResourceInstanceId())) {
									session.update("updateRealTimeMetricData",
											po);
								} else {
									session.insert("insertRealTimeMetricData",
											po);
									if (toInsertDatas == null) {
										toInsertDatas = new ArrayList<>();
									}
									toInsertDatas.add(metricData);
								}
							}
							session.flushStatements();
							session.commit();
							session.close();
							session = getBatchSqlSession();
						}

						/**
						 * 历史数据
						 */
						tableName = metricTableNameCache
								.getHistoryPerfMetricTable(metricId);
						if (null == tableName) {
							tableName = metricTableNameCache
									.createHistoryMetricTable(metricId, session);
							session.commit();
							session.close();
							session = getBatchSqlSession();
						}
						if (null == tableName) {
							if (logger.isErrorEnabled()) {
								logger.error("metric's history table not exist."
										+ metricId);
							}
						} else {
							for (MetricDataPO po : dataPs) {
								/**
								 * 空数据不参与汇总计算，也不再记录。
								 * 
								 * modify by ziw at 2016年8月5日 上午9:36:41
								 */
								if(po.getMetricData() == null){
									continue;
								}
								po.setTableName(tableName);
								session.insert("addHistoryMetricData", po);
							}
							session.flushStatements();
							session.commit();
						}
					} catch (Throwable e) {
						session.rollback();
						logger.error("persistPerformanceMetricData metric["
								+ metricId + "] error:" + e.getMessage(), e);
					} finally {
						if (session != null) {
							session.close();
						}
						computeSum(temp, datas.size());
					}
					if (toInsertDatas != null) {
						for (MetricData metricData : toInsertDatas) {
							metricTableNameCache
									.updatePerformanceMetricDataExist(metricId,
											metricData.getResourceInstanceId());
						}
					}
				}
			});
		}
	}

	@Override
	public void run() {
		while (true) {
			List<MetricData> metricDatas = new ArrayList<>(this.capaticy);
			try {
				metricDatas.add(queue.take());
			} catch (InterruptedException e) {
				logger.error(ArrayUtils.toString(sessionNames), e);
				metricDatas.clear();
				metricDatas = null;
				continue;
			}
			queue.drainTo(metricDatas);
			if (startDateTime == null) {
				startDateTime = new Date();
				batchDateTime = startDateTime;
			} else {
				batchDateTime = new Date();
			}
			int dataSize = metricDatas.size();
			// decreaseRemain(dataSize);
			increaseActive(dataSize);
			// SqlSession session = null;
			try {
				if (metricSplitTable) {
					spliteTableRun(metricDatas);
				} else {
					singleTableRun(metricDatas);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("run metric data persist.", e);
				}
			} finally {
				// if (session != null) {
				// session.close();
				// }
			}
		}
	}

	@Override
	public long getLossTime() {
		return lossTime;
	}

	private SqlSession getBatchSqlSession() {
		return myBatisSqlSessionFactory.openSession(ExecutorType.BATCH, false);
	}

	@Override
	public Date getStartDateTime() {
		return startDateTime;
	}

	@Override
	public int getBatchMetricSpeed() {
		return batchMetricSpeed;
	}

	@Override
	public int getAverageSpeed() {
		return averageSpeed;
	}
}
