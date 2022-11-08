package com.mainsteam.stm.lock;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mainsteam.stm.lock.control.service.AbstractGlobalLockControlService;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年1月26日 上午10:04:00
 * @version 1.0
 */
public class PressTest {

	private static final Log logger = LogFactory.getLog(PressTest.class);

	public PressTest() {
	}
	
	@Test
	public void testOneProcessor(){
		final String[] xmpPath = {
				"classpath*:META-INF/services/public-*-beans.xml",
				"classpath*:META-INF/services/server-processer-*-beans.xml" };
		final String lockModuleName = "testOneProcessor";

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				xmpPath);
		context.start();
		final int LSTM_COUNT = 100;
		long temp = System.currentTimeMillis();
		LockService lockService = context
				.getBean(LockService.class);
		Lock lock = lockService.getlock(lockModuleName);
		int lockCount = LSTM_COUNT;
		while (lockCount-- > 0) {
			// try {
			lock.lock();
			// if (logger.isInfoEnabled()) {
//			logger.info(msg);
			String msg = lockModuleName+" index=" + lockCount
					+ " do work.";
			System.out.println(msg);
			// }
			// } finally {
			lock.unlock();
			// }
		}
		
		long offset = System.currentTimeMillis() - temp;
		System.out.println("loss time=" + offset);
		System.out.println("throughput is " + (LSTM_COUNT)
				/ (float) offset * 1000);
		AbstractGlobalLockControlService controlService = (AbstractGlobalLockControlService) ((LockImpl)lock).getLockControlService();
		int index = 1;
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep1Offset());
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep2Offset());
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep3Offset());
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep4Offset());
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep5Offset());
		context.close();
	}
	

	@Test
	public void testGreedLockOneProcessor(){
		final String[] xmpPath = {
				"classpath*:META-INF/services/public-*-beans.xml",
				"classpath*:META-INF/services/server-processer-*-beans.xml" };
		final String lockModuleName = "testOneProcessor";

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				xmpPath);
		context.start();
		final int LSTM_COUNT = 10000;
		LockService lockService = context
				.getBean(LockService.class);
		Lock lock = lockService.getGreedlock(lockModuleName);
		int lockCount = LSTM_COUNT;
		long temp = System.currentTimeMillis();
		while (lockCount-- > 0) {
			// try {
			lock.lock();
			// if (logger.isInfoEnabled()) {
//			logger.info(msg);
			String msg = lockModuleName+" index=" + lockCount
					+ " do work.";
			System.out.println(msg);
			// }
			// } finally {
			lock.unlock();
			// }
		}
		
		long offset = System.currentTimeMillis() - temp;
		System.out.println("loss time=" + offset);
		System.out.println("throughput is " + (LSTM_COUNT)
				/ (float) offset * 1000);
		AbstractGlobalLockControlService controlService = (AbstractGlobalLockControlService) ((LockImpl)lock).getLockControlService();
		int index = 1;
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep1Offset());
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep2Offset());
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep3Offset());
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep4Offset());
		System.out.println("Step"+(index++)+"Offset="+controlService.getStep5Offset());
		context.close();
	}

	/**
	 * Database Lock implement :<br>
	 * LSTM_COUNT = 10;processerCount = 4; loss time=6157 throughput is 6.4966707<br>
	 * LSTM_COUNT = 100;processerCount = 4; loss time=39704 throughput is
	 * 10.074552<br>
	 * LSTM_COUNT = 1000;processerCount = 4; loss time=345025 throughput is
	 * 11.593363<br>
	 * LSTM_COUNT = 1000;processerCount = 4; loss time=6694766 throughput is
	 * 5.974817<br>
	 * 
	 */
	@Test
	public void testMultiProcessor() {
		// final int LSTM_COUNT = 100000;
		final int LSTM_COUNT = 100;
		// final int LSTM_COUNT = 1;
		final String lockModuleName = "testMultiProcessor";
		int processerCount = 4;
		final String[] xmpPath = {
				"classpath*:META-INF/services/public-*-beans.xml",
				"classpath*:META-INF/services/server-processer-*-beans.xml" };
		final CountDownLatch latch = new CountDownLatch(processerCount);
		final ClassPathXmlApplicationContext[] contexts = new ClassPathXmlApplicationContext[processerCount];
		for (int i = 0; i < processerCount; i++) {
			@SuppressWarnings("resource")
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					xmpPath);
			context.start();
			contexts[i] = context;
		}

		long temp = System.currentTimeMillis();
		for (int i = 0; i < processerCount; i++) {
			@SuppressWarnings("unused")
			final int index = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					LockService lockService = contexts[index]
							.getBean(LockService.class);
					int lockCount = LSTM_COUNT;
					String msg = "testMultiProcessor index=" + index
							+ " do work.";
					Lock lock = lockService.getlock(lockModuleName);
					while (lockCount-- > 0) {
						// try {
						lock.lock();
						// if (logger.isInfoEnabled()) {
//						logger.info(msg);
						System.out.println(msg);
						// }
						// } finally {
						lock.unlock();
						// }
					}
					latch.countDown();
					contexts[index].stop();
					contexts[index].close();
				}
			}).start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error("testMultiProcessor", e);
			}
		}
		long offset = System.currentTimeMillis() - temp;
		System.out.println("loss time=" + offset);
		System.out.println("throughput is " + (LSTM_COUNT * processerCount)
				/ (float) offset * 1000);
	}
	
	@Test
	public void testprint(){
		final int LSTM_COUNT = 10000;
		// final int LSTM_COUNT = 1;
		final String lockModuleName = "testMultiProcessor";
		int processerCount = 4;
		long temp = System.currentTimeMillis();
		for (int i = 0; i < LSTM_COUNT*processerCount; i++) {
			logger.info(lockModuleName);
		}
		long offset = System.currentTimeMillis() - temp;
		System.out.println("loss time=" + offset);
		System.out.println("throughput is " + (LSTM_COUNT * processerCount)
				/ (float) offset * 1000);
	}
}
