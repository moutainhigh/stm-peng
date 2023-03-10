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

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricDataPO;

public abstract class MetricDataPersister implements MetricDataBatchPersister,
		MetricDataPersisterMonitor, Runnable {

	private static final Log logger = LogFactory
			.getLog(MetricDataPersister.class);

	private SqlSessionFactory myBatisSqlSessionFactory;

	private MetricTableNameManager metricTableNameCache;

	public MetricTableNameManager getMetricTableNameCache() {
		return metricTableNameCache;
	}

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
	
	public MetricDataPersister(int capaticy, boolean metricSplitTable,
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

		// ?????????????????????10???????????????????????????200???
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
	public abstract String insertSessionName(); 
	
	public abstract String updateSessionName(); 
	
	public abstract boolean isExists(String metricId,long instanceId);
	
	public abstract void updateMetricDataExist(String metricId,long instanceId);
	
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
		long temp = System.currentTimeMillis();
		SqlSession session = getBatchSqlSession();
		List<MetricDataPO> customHistory = null;
		List<MetricDataPO> update = null;
		try {
			List<MetricData> toInsertDatas = null;
			for (MetricData metricData : metricDatas) {
				MetricDataPO po = MetricDataPO.convert(metricData);
				if(metricData.isCustomMetric()){
					if(customHistory == null){
						customHistory = new ArrayList<MetricDataPO>();
					}
					customHistory.add(po);
				}
				if(!isExists(metricData.getMetricId(), metricData.getResourceInstanceId())){
					session.insert(insertSessionName(), po);
					if (toInsertDatas == null) {
						toInsertDatas = new ArrayList<>();
					}
					toInsertDatas.add(metricData);
				}else{
					if(update == null){
						update = new ArrayList<MetricDataPO>();
					}
					update.add(po);
				}
			}
			session.commit();
			if(toInsertDatas != null){
				for (MetricData metricData : toInsertDatas) {
					updateMetricDataExist(metricData.getMetricId(), metricData.getResourceInstanceId());
				}
			}
			toInsertDatas = null;
		} catch (Exception e) {
			session.rollback();
			errCount += metricDatas.size();
			logger.error("", e);
		} finally {
			session.close();
		}
		try {
			if(update != null){
				session = getBatchSqlSession();
				//????????????????????????
				for (MetricDataPO metricDataPO : update) {
					session.update(updateSessionName(), metricDataPO);
				}
				session.commit();
				update = null;
			}
		} catch (Exception e) {
			session.rollback();
			errCount += metricDatas.size();
			logger.error("", e);
		} finally {
			session.close();
		}
		try {
			if(customHistory != null){
				session = getBatchSqlSession();
				//???????????????????????????
				for (MetricDataPO metricDataPO : customHistory) {
					session.insert("addCustomerHistoryData", metricDataPO);
				}
				session.commit();
				customHistory = null;
			}
		} catch (Exception e) {
			session.rollback();
			errCount += metricDatas.size();
			logger.error("", e);
		} finally {
			session.close();
		}
		computeSum(temp, metricDatas.size());
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
		 * ??????????????????
		 */
		Map<String, List<MetricData>> groupedMetricData = new HashMap<>(1000);

		/*
		 * ??????????????????
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
								if (isExists(metricId,metricData.getResourceInstanceId())) {
									session.update(updateSessionName(),po);
								} else {
									session.insert(insertSessionName(),po);
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
						 * ????????????
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
								 * ???????????????????????????????????????????????????
								 * 
								 * modify by ziw at 2016???8???5??? ??????9:36:41
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
						StringBuilder str = new StringBuilder(1000);
						for (MetricDataPO po : dataPs) {
							if(po.getMetricData() == null){
								continue;
							}
							str.append("[instanceId:").append(po.getResourceInstanceId());
							str.append("resouceId:").append(po.getResourceId());
							str.append("data:").append(po.getMetricData());
							str.append("collect_time:").append(po.getCollectTime());
							str.append("],");
						}
						logger.error("persistPerformanceMetricData metric["
								+ metricId + "] error:" + str + e.getMessage(), e);
					} finally {
						if (session != null) {
							session.close();
						}
						computeSum(temp, datas.size());
					}
					if (toInsertDatas != null) {
						for (MetricData metricData : toInsertDatas) {
							updateMetricDataExist(metricId,metricData.getResourceInstanceId());
						}
					}
					toInsertDatas = null;
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
