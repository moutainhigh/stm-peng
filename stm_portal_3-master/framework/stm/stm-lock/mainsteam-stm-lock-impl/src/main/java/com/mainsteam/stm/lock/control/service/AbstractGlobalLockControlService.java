package com.mainsteam.stm.lock.control.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.lock.exception.LockConfickException;
import com.mainsteam.stm.lock.obj.LockRequest;
import com.mainsteam.stm.lock.obj.LockTable;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年12月22日 下午4:40:45
 * @version 1.0
 */
public abstract class AbstractGlobalLockControlService implements GlobalLockControlService {

	private static final Log logger = LogFactory.getLog(AbstractGlobalLockControlService.class);
	private volatile boolean running = true;
	private NodeSupporter nodeSupporter;
	private LockRequestWoker lockRequestWorker;
	private LockRequestSender lockRequestSender;
	private LockRequestHeartbeat lockRequestHeartbeat;
	private LockTable lockTable = new LockTable();
	private AtomicLong step1Offset = new AtomicLong(0);
	private AtomicLong step2Offset = new AtomicLong(0);
	private AtomicLong step3Offset = new AtomicLong(0);
	private AtomicLong step4Offset = new AtomicLong(0);
	private AtomicLong step5Offset = new AtomicLong(0);
	private GlobalLockControlService _self;// Be careful to use this.
	private String currentNode;
	public void setNodeSupporter(NodeSupporter nodeSupporter) {
		this.nodeSupporter = nodeSupporter;
	}

	public void setProxy(GlobalLockControlService proxy) {
		this._self = proxy;
	}

	public void start() {
		this.currentNode = nodeSupporter.getCurrentNode();
		lockRequestWorker.start();
		lockRequestSender.start();
		lockRequestHeartbeat.start();
	}

	public void stop() {
		this.running = false;
		lockRequestWorker.interrupt();
		lockRequestSender.interrupt();
		lockRequestHeartbeat.interrupt();
		try {
			lockRequestWorker.join();
		} catch (InterruptedException e) {
		}
		try {
			lockRequestSender.join();
		} catch (InterruptedException e) {
		}
		try {
			lockRequestHeartbeat.join();
		} catch (InterruptedException e) {
		}
	}

	public AbstractGlobalLockControlService() {
		lockRequestWorker = new LockRequestWoker();
		lockRequestSender = new LockRequestSender();
		lockRequestHeartbeat = new LockRequestHeartbeat();
		lockRequestWorker.setDaemon(false);
		lockRequestSender.setDaemon(false);
		lockRequestHeartbeat.setDaemon(false);
	}

	public void lock(LockRequest lockRequest) {
		long tmp = System.currentTimeMillis();
		lockRequest.setNode(nodeSupporter.getCurrentNode());
		if (lockTable.makeLock(lockRequest.getName())) {
			return;
		}
		if (lockRequest.isGreed()) {
			// send request to request queue
			lockRequestSender.handleRequest(lockRequest);
			step1Offset.addAndGet(System.currentTimeMillis() - tmp);
			waitResponse(lockRequest);
		} else {
			fightLock(lockRequest);
		}
	}

	public void waitFightLock(LockRequest lockRequest) {
		LockStatus status = new LockStatus(lockRequest);
		do {
			long tmp = System.currentTimeMillis();
			boolean result = _self.insertLock(status);
			if (result) {
				if (logger.isDebugEnabled()) {
					logger.debug("fightLock request success." + lockRequest);
				}
				step5Offset.addAndGet(System.currentTimeMillis() - tmp);
				lockTable.addLock(status);
				break;
			} else {
				LockStatus lockedStatus = _self.getLock(lockRequest.getName());
				if (lockedStatus != null && lockedStatus.isGreed()) {
					throw new LockConfickException();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		} while (true);
	}

	private void fightLock(final LockRequest lockRequest) {
		final CountDownLatch fire = new CountDownLatch(1);
		final AtomicReference<RuntimeException> exception = new AtomicReference<>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					waitFightLock(lockRequest);
				} catch (RuntimeException e) {
					exception.set(e);
				} finally {
					fire.countDown();
				}
			}
		}, "fightLock-" + lockRequest.getName()).start();
		try {
			fire.await();
		} catch (InterruptedException e) {
		}
		RuntimeException t = exception.get();
		if (t != null) {
			throw t;
		}
	}

	private void waitResponse(LockRequest lockRequest) {
		while (!lockTable.makeLock(lockRequest.getName())) {
			synchronized (lockRequest) {
				try {
					lockRequest.wait(10);
				} catch (Exception e) {
				}
			}
		}
	}

	public void unlock(String name) {
		final LockStatus status = lockTable.getLock(name);
		if (status != null) {
			if (!status.isGreed()) {
				final CountDownLatch fire = new CountDownLatch(1);
				/**
				 * 对于非贪心锁，立即释放远程锁定状态。
				 */
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							_self.removeLock(status);
						} finally {
							fire.countDown();
						}
					}
				}, "unlock-" + name).start();
				try {
					fire.await();
				} catch (InterruptedException e) {
				}
//				removeLock(status);
				lockTable.removeLock(name);
			} else {
				/**
				 * 对于贪心锁，只修改其本地锁定状态，不释放远程锁定状态。
				 */
				lockTable.releaseLock(name);
			}
		}
	}

	private class LockRequestSender extends Thread {
		private LinkedBlockingQueue<LockRequest> lockRequests = new LinkedBlockingQueue<>();

		public LockRequestSender() {
			super("LockRequestSender");
		}

		@Override
		public void run() {
			String node = nodeSupporter.getCurrentNode();
			while (running) {
				LockRequest request = null;
				try {
					request = lockRequests.take();
				} catch (InterruptedException e) {
				}
				if (request != null) {
					request.setNode(node);
					try {
						if (logger.isDebugEnabled()) {
							logger.debug("LockRequestSender send lockRequest=" + request);
						}
						lockTable.addLockRequest(request);
						long tmp = System.currentTimeMillis();
						_self.addLockRequest(request);
						step2Offset.addAndGet(System.currentTimeMillis() - tmp);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("addLockRequest error:name=" + request.getName(), e);
						}
						lockTable.removeLockRequest(request.getName());
						/**
						 * 失败的请求，重新插入请求队列。
						 */
						lockRequests.add(request);
					}
				}
			}
		}

		public void handleRequest(LockRequest request) {
			lockRequests.add(request);
		}
	}

	public AtomicLong getStep1Offset() {
		return step1Offset;
	}

	public AtomicLong getStep2Offset() {
		return step2Offset;
	}

	public AtomicLong getStep3Offset() {
		return step3Offset;
	}

	public AtomicLong getStep4Offset() {
		return step4Offset;
	}

	public AtomicLong getStep5Offset() {
		return step5Offset;
	}

	/**
	 * 将本地Request加入数据请求表，针对请求表排序，加锁。
	 */
	private class LockRequestWoker extends Thread {
		private ThreadPoolExecutor executor;

		public LockRequestWoker() {
			super("LockRequestWoker");
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10, new ThreadFactory() {
				private int counter = 1;

				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "dealLockRequest-" + counter++);
				}
			});
		}

		@Override
		public void run() {
			final String node = AbstractGlobalLockControlService.this.currentNode;
			while (running) {
				if (_self == null) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					continue;
				}
				long tmp = System.currentTimeMillis();
				java.util.List<LockRequest> list = _self.selectTopLockRequests();
				step3Offset.addAndGet(System.currentTimeMillis() - tmp);
				tmp = System.currentTimeMillis();
				if (list != null && list.size() > 0) {
					/**
					 * 首先根据name分组，然后根据time排序.
					 */
					Collections.sort(list, new Comparator<LockRequest>() {
						@Override
						public int compare(LockRequest o1, LockRequest o2) {
							int timeCompare = o1.getRequestTime().compareTo(o2.getRequestTime());
							int nameCompare = o1.getName().compareTo(o2.getName());
							return nameCompare == 0 ? timeCompare : nameCompare;
						}
					});
					List<LockRequest> topRequests = new ArrayList<>();
					/**
					 * 先请求先得到锁。
					 */
					LockRequest checkRequest = list.get(0);
					topRequests.add(checkRequest);
					// dealLockRequest(checkRequest, node);
					for (int i = 1; i < list.size(); i++) {
						/**
						 * 判断锁是否新的锁列表。
						 */
						if (list.get(i).getName().equals(checkRequest.getName())) {
							continue;
						}
						checkRequest = list.get(i);
						topRequests.add(checkRequest);
						// dealLockRequest(checkRequest, node);
					}
					step4Offset.addAndGet(System.currentTimeMillis() - tmp);
					int topSize = topRequests.size();
					final CountDownLatch countDownLatch = new CountDownLatch(topSize);
					for (final LockRequest lockRequest : topRequests) {
						executor.execute(new Runnable() {
							@Override
							public void run() {
								Thread t = Thread.currentThread();
								String name = t.getName();
								t.setName(name + "--" + lockRequest.getName());
								try {
									dealLockRequest(lockRequest, node);
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("LockRequestWoker dealLockRequest", e);
									}
								} finally {
									countDownLatch.countDown();
									t.setName(name);
								}
							}
						});
					}
					try {
						countDownLatch.await();
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("LockRequestWoker countDownLatch", e);
						}
					}
				}
				// Thread.yield();
				try {
					Thread.sleep(_self.getCheckFreq());
				} catch (InterruptedException e) {
				}
			}
			executor.shutdownNow();
		}

		private void dealLockRequest(LockRequest checkRequest, String node) {
			if (checkRequest.getNode().equals(node)) {
				/**
				 * 如果是本地请求排在第一位，则得到锁。
				 */
				tryMakeSureLockRequest(checkRequest, node);
			} else {
				LockStatus lockStatus = lockTable.getLock(checkRequest.getName());
				/**
				 * 如果有其它节点请求该锁，而且该锁被本地持有，且本地当前没有使用该锁，则释放当前锁。
				 */
				if (lockStatus != null && lockTable.tryRemoveLock(checkRequest.getName())) {
					_self.removeLock(lockStatus);
				}
			}
		}

		/**
		 * 尝试本地请求得到锁。
		 * 
		 * @param checkRequest
		 */
		private void tryMakeSureLockRequest(LockRequest checkRequest, String node) {
			if (logger.isDebugEnabled()) {
				logger.debug("tryMakeSureLockRequest " + checkRequest);
			}
			LockRequest nativeRequest = lockTable.getLockRequest(checkRequest.getName());
			if (nativeRequest != null) {
				LockStatus status = new LockStatus(checkRequest);
				long tmp = System.currentTimeMillis();
				boolean result = _self.insertLock(status);
				if (result) {
					if (logger.isDebugEnabled()) {
						logger.debug("tryMakeSureLockRequest request success." + checkRequest);
					}
					try {
						_self.removeLockRequest(checkRequest);
						step5Offset.addAndGet(System.currentTimeMillis() - tmp);
						if (logger.isDebugEnabled()) {
							logger.debug("tryMakeSureLockRequest removeLockRequest from table.");
						}
					} finally {
						lockTable.removeLockRequest(checkRequest.getName());
						lockTable.addLock(status);
						synchronized (nativeRequest) {
							nativeRequest.notify();
						}
					}
				} else {
					/**
					 * 查看是不是本地系统重启过，而且已经申请成功过锁。
					 * 因为，每次重启产生的nodeId为随机字符串，所以，目前先忽略此处逻辑。
					 * 等nodeSupporter的返回值为节点IP加端口时，再考虑放开这出逻辑代码。
					 */
					// LockStatus persistLockStatus = lockTable
					// .getLock(checkRequest.getName());
					// if (persistLockStatus != null
					// && persistLockStatus.getNode().equals(node)) {
					// lockTable.addLock(status);
					// synchronized (nativeRequest) {
					// nativeRequest.notify();
					// }
					// removeLockRequest(checkRequest);
					// lockTable.removeLockRequest(checkRequest.getName());
					// }

					// 检查该lock是否存活，不过不再存活
					// ，将其删除。，然后重新加锁.删除不活动锁的逻辑放到自动维护记得活动状态的task中。
					// LockStatus lockStatus = getLock(checkRequest.getName());
					// if(lockStatus!=null
					// &&lockStatus.getCurrentTime().after(lockStatus.getExpireTime())){
					//
					// }
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"tryMakeSureLockRequest removeLockRequest because request is not used." + checkRequest);
				}
				_self.removeLockRequest(checkRequest);
			}
		}
	}

	private class LockRequestHeartbeat extends Thread {

		private LockRequestHeartbeat() {
			super("LockHeartbeat");
		}

		@Override
		public void run() {
			long timeout = 30;// 30 seond
			long hearbeatFreq = 5000;// 10sencond.
			final String node = AbstractGlobalLockControlService.this.currentNode;
			while (running) {
				try {
					if (_self != null) {
						_self.addHeartbeatForLockRequest(node);
						_self.addHeartbeatForLock(node);
						_self.deleteTimeoutLock(timeout);
						_self.deleteTimeoutLockRequest(timeout);
					}
					Thread.sleep(hearbeatFreq);
				} catch (InterruptedException e) {
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("run", e);
					}
				}
			}
		}
	}

	public abstract void deleteTimeoutLock(long timeout);

	public abstract void deleteTimeoutLockRequest(long timeout);

	/**
	 * 发送心跳数据
	 * 
	 * @param node
	 */
	public abstract void addHeartbeatForLockRequest(String node);

	/**
	 * 发送心跳数据
	 * 
	 * @param node
	 */
	public abstract void addHeartbeatForLock(String node);

	/**
	 * 获取检查LockRequest的频度
	 * 
	 * @return
	 */
	public abstract long getCheckFreq();

	/**
	 * 加载所有的LockRequest请求
	 * 
	 * @return
	 */
	public abstract java.util.List<LockRequest> selectTopLockRequests();

	/**
	 * 保存新增加的LockRequest
	 * 
	 * @param lockRequest
	 * @return
	 */
	public abstract boolean addLockRequest(LockRequest lockRequest);

	/**
	 * 删除LockRequest
	 * 
	 * @param request
	 */
	public abstract void removeLockRequest(LockRequest request);

	/**
	 * 保存锁
	 * 
	 * @param lockRequest
	 * @return
	 */
	public abstract boolean insertLock(LockStatus lockRequest);

	/**
	 * 查询锁
	 * 
	 * @param key
	 * @return
	 */
	public abstract LockStatus getLock(String key);

	/**
	 * 删除锁
	 * 
	 * @param lockRequest
	 * @return
	 */
	public abstract boolean removeLock(LockStatus lockRequest);
}
