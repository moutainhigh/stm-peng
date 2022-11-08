package com.mainsteam.stm.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.lock.control.service.NodeSupporter;
import com.mainsteam.stm.lock.exception.LockConfickException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class LockServiceTest {

	private static final Log logger = LogFactory.getLog(LockServiceTest.class);
	@Resource(name = "lockService")
	private LockService lockService;

//	@Autowired
//	private NodeSupporter nodeSupporter;

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath*:META-INF/services/public-*-beans.xml",
				"classpath*:META-INF/services/server-processer-*-beans.xml");
		context.start();
		System.out.println(context.getBean("lockService"));
		context.stop();
	}

	@Test
	public void testSync() {
		lockService.sync("testsync", new LockCallback<Object>() {
			@Override
			public Object doAction() {
				int i = 30;
				while (i-- > 0) {
					logger.info("testSync running...");
					System.out.println("testSync running...");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
				return null;
			}
		}, 1);
	}

	@Test
	public void testLock() {
		Lock lk = lockService.getlock("testLock");
		lk.lock();
		int i = 30;
		while (i-- > 0) {
			logger.info("testSync running...");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		lk.unlock();
	}

	@Test
	public void testMultisync() {
		for (int i = 0; i < 2; i++) {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					lockService.sync("testsync", new LockCallback<Object>() {
						@Override
						public Object doAction() {
							int i = 10;
							while (i-- > 0) {
								logger.info("testSync running...");
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									logger.error(e.getMessage(), e);
								}
							}
							return null;
						}
					}, 2);
				}
			});
			th.setName("testMultisync" + i);
			th.setDaemon(false);
			th.start();
		}

		try {
			Thread.sleep(50 * 1000);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void testLockConfick() {
		final String[] xmpPath = {
				"classpath*:META-INF/services/public-*-beans.xml",
				"classpath*:META-INF/services/server-processer-*-beans.xml" };
		final ClassPathXmlApplicationContext context1 = new ClassPathXmlApplicationContext(
				xmpPath);
		context1.start();
		final ClassPathXmlApplicationContext context2 = new ClassPathXmlApplicationContext(
				xmpPath);
		context2.start();
		final boolean[] result = new boolean[1];
		result[0] = false;
		final String conflickKey = "confickLock";
		final CountDownLatch latch = new CountDownLatch(2);
		Runnable greedRunnable = new Runnable() {
			@Override
			public void run() {
				Lock lock = context1.getBean(LockService.class).getGreedlock(
						conflickKey);
				lock.lock();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("greed lock execute.");
				lock.unlock();
				latch.countDown();
			}
		};
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					Lock lock = context2.getBean(LockService.class).getlock(
							conflickKey);
					lock.lock();
					System.out.println("lock execute.");
					lock.unlock();
				} catch (LockConfickException e) {
					e.printStackTrace();
					result[0] = true;
				} finally {
					latch.countDown();
				}
			}
		};
		new Thread(greedRunnable).start();
		new Thread(runnable).start();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assert (result[1]);
		final CountDownLatch latch1 = new CountDownLatch(1);
		result[0] = false;
		runnable = new Runnable() {
			@Override
			public void run() {
				System.out.println("to lock again.");
				try {
					Lock lock = context2.getBean(LockService.class).getGreedlock(
							conflickKey);
					lock.lock();
					System.out.println("lock execute.");
					lock.unlock();
					result[0] = true;
				} catch (LockConfickException e) {
					e.printStackTrace();
				} finally {
					latch1.countDown();
				}
			}
		};
		new Thread(runnable).start();
		try {
			latch1.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assert (result[1]);
	}

	@Test
	public void testMultiLock() {
//		final String node = nodeSupporter.getCurrentNode();
		final String node = "";
		List<Thread> thlist = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			final int threadIndex = i;
			Thread th = new Thread(new Runnable() {
				private int index = threadIndex;

				@Override
				public void run() {

					Lock lk = lockService.getlock("testsync", 2);
					lk.lock();
					int i = 10;
					while (i-- > 0) {
						logger.info("testSync running...node=" + node
								+ " currentTime=" + System.currentTimeMillis()
								+ " threadIndex=" + index);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							logger.error(e.getMessage(), e);
						}
					}

					lk.unlock();

				}
			});
			th.setName("testMultisync" + i);
			th.start();
			thlist.add(th);
		}
		for (Thread thread : thlist) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
