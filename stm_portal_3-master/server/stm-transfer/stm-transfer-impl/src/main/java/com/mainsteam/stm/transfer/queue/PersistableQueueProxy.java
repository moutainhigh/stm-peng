/**
 * 
 */
package com.mainsteam.stm.transfer.queue;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.transfer.obj.InnerTransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;
import com.mainsteam.stm.util.OSUtil;
import com.github.xsonorg.XSON;
import com.google.code.fqueue.FQueue;

/**
 * @author ziw
 * 
 */
public class PersistableQueueProxy implements PersistableQueue<InnerTransferData> {
	private static final Log logger = LogFactory.getLog(PersistableQueueProxy.class);
	public static final String QUEUE_PERSIST_PATH = "TRANSFER_QUEUE_PERSIST_PATH";
	public static final String SERVER_HOME = "SERVER_HOME";
	private LinkedBlockingQueue<InnerTransferData> memoryQueue;
	private FQueue fsQueue;
	private PersistableQueue<InnerTransferData> innerQueue;

	public LinkedBlockingQueue<InnerTransferData> getMemoryQueue() {
		return memoryQueue;
	}

	public synchronized void replaceMemoryQueue(LinkedBlockingQueue<InnerTransferData> memoryQueue) {
		InnerTransferData d = null;
		do {
			d = this.memoryQueue.poll();
		} while (memoryQueue.offer(d));
		this.memoryQueue = memoryQueue;
	}

	/**
	 * 
	 */
	public PersistableQueueProxy(LinkedBlockingQueue<InnerTransferData> dataQueue, TransferDataType dataType) {
		this.memoryQueue = dataQueue;
		if (dataType.equals(TransferDataType.MetricData)) {
			String parentPath = OSUtil.getEnv(QUEUE_PERSIST_PATH);
			if (parentPath == null) {
				parentPath = OSUtil.getEnv(SERVER_HOME);
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(parentPath + ".PersistableQueueImpl read parentPath from " + QUEUE_PERSIST_PATH);
				}
			}
			if (parentPath == null) {
				parentPath = OSUtil.getEnv("CATALINA_HOME");
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(parentPath + ".PersistableQueueImpl read parentPath from " + SERVER_HOME);
				}
			}
			if (parentPath == null) {
				parentPath = OSUtil.getEnv("java.io.temp");
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(parentPath + ".PersistableQueueImpl read parentPath from java.io.temp");
				}
			}
			if (parentPath == null) {
				parentPath = "./";
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(parentPath + ".PersistableQueueImpl read parentPath magicstring");
				}
			}
			String dirPath = parentPath + File.separator + "transferqueue" + File.separator + dataType.name();
			File distDir = new File(dirPath);
			if (logger.isInfoEnabled()) {
				logger.info("PersistableQueueProxy fsQueue path = " + distDir.getAbsolutePath());
			}
			try {
				fsQueue = new FQueue(distDir.getAbsolutePath());
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("error create fsQueue.", e);
				}
				if (distDir.exists()) {
					distDir.delete();
					try {
						fsQueue = new FQueue(distDir.getAbsolutePath());
					} catch (Exception e1) {
						if (logger.isErrorEnabled()) {
							logger.error("error recreate fsQueue.use memory queue only.", e);
						}
					}
				}
			}
		}
		if (fsQueue != null) {
			innerQueue = new PersistableQueueImpl();
			Runnable dataPipeDriver = new Runnable() {
				@Override
				public void run() {
					while (true) {
						if (PersistableQueueProxy.this.memoryQueue.remainingCapacity() > 0) {
							byte[] content = null;
							try {
								content = fsQueue.poll();
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("run datapipe driver error.", e);
								}
							}
							if (content == null) {
								((PersistableQueueImpl) innerQueue).tryChangeToMemQueue();
								synchronized (this) {
									try {
										this.wait(5);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							} else {
								InnerTransferData data = null;
								try {
									data = XSON.parse(content);
								} catch (RuntimeException e) {
									continue;
								}
								boolean addResultFlag = true;
								int count = 30;
								do {
									try {
										PersistableQueueProxy.this.memoryQueue.add(data);// 這裡可能會拋出異常，造成該線程終止。
										addResultFlag = false;
									} catch (Exception e) {
										addResultFlag = true;
										synchronized (this) {
											try {
												this.wait(5);// block here.
											} catch (InterruptedException e1) {
											}
										}
									}
								} while (addResultFlag && count > 0);
							}
						} else {
							synchronized (this) {
								try {
									this.wait(5);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			};
			new Thread(dataPipeDriver, "persistQueueDataPipeDriver").start();
		} else {
			innerQueue = new PersistableQueue<InnerTransferData>() {
				@Override
				public void add(InnerTransferData simpleObj) {
					PersistableQueueProxy.this.memoryQueue.add(simpleObj);
				}

				@Override
				public InnerTransferData take() throws InterruptedException {
					return PersistableQueueProxy.this.memoryQueue.take();
				}

				@Override
				public int size() {
					return PersistableQueueProxy.this.memoryQueue.size();
				}

				@Override
				public LinkedBlockingQueue<InnerTransferData> getMemoryQueue() {
					return PersistableQueueProxy.this.getMemoryQueue();
				}

				@Override
				public void replaceMemoryQueue(LinkedBlockingQueue<InnerTransferData> memoryQueue) {
					PersistableQueueProxy.this.replaceMemoryQueue(memoryQueue);
				}
			};
		}
	}

	private class PersistableQueueImpl implements PersistableQueue<InnerTransferData> {

		private volatile boolean persistFirst = true;

		@Override
		public InnerTransferData take() throws InterruptedException {
			return PersistableQueueProxy.this.memoryQueue.take();
		}

		@Override
		public void add(InnerTransferData simpleObj) {
			synchronized (this) {
				if (persistFirst) {
					try {
						fsQueue.add(XSON.write(simpleObj));
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("add", e);
						}
					}
					return;
				}
			}
			try {
				PersistableQueueProxy.this.memoryQueue.add(simpleObj);
			} catch (Exception e) {
				if (logger.isInfoEnabled()) {
					logger.info("add", e);
				}
				changeToPersistQueue();
				try {
					fsQueue.add(XSON.write(simpleObj));
				} catch (Exception e1) {
					if (logger.isErrorEnabled()) {
						logger.error("add", e1);
					}
				}
			}
		}

		public void changeToPersistQueue() {
			synchronized (this) {
				if (!persistFirst) {
					persistFirst = true;
					if (logger.isInfoEnabled()) {
						logger.info("changeToPersistQueue ok.change to fsQueue.");
					}
				}
			}
		}

		public void tryChangeToMemQueue() {
			synchronized (this) {
				if (persistFirst && PersistableQueueProxy.this.memoryQueue.remainingCapacity() > 0
						&& fsQueue.size() < PersistableQueueProxy.this.memoryQueue.remainingCapacity()) {
					if (fsQueue.size() > 0) {
						byte[] cc = null;
						while ((cc = fsQueue.poll()) != null) {
							PersistableQueueProxy.this.memoryQueue.add((InnerTransferData) XSON.parse(cc));
						}
					}
					persistFirst = false;
					if (logger.isInfoEnabled()) {
						logger.info("tryChangeToMemQueue ok.change to memqueue.");
					}
				}
			}
		}

		@Override
		public int size() {
			return PersistableQueueProxy.this.memoryQueue.size() + fsQueue.size();
		}

		@Override
		public LinkedBlockingQueue<InnerTransferData> getMemoryQueue() {
			return PersistableQueueProxy.this.memoryQueue;
		}

		@Override
		public void replaceMemoryQueue(LinkedBlockingQueue<InnerTransferData> memoryQueue) {
			PersistableQueueProxy.this.replaceMemoryQueue(memoryQueue);
		}
	}

	@Override
	public synchronized void add(InnerTransferData simpleObj) {
		innerQueue.add(simpleObj);
	}

	@Override
	public InnerTransferData take() throws InterruptedException {
		return innerQueue.take();
	}

	@Override
	public int size() {
		return innerQueue.size();
	}

	public static void main(String[] args) {
		LinkedBlockingQueue<InnerTransferData> dataQueue = new LinkedBlockingQueue<InnerTransferData>(1);
		PersistableQueueProxy x = new PersistableQueueProxy(dataQueue, TransferDataType.MetricData);
		for (int i = 0; i < 10; i++) {
			InnerTransferData d = new InnerTransferData();
			x.add(d);
		}

	}
}
