/**
 * 
 */
package com.mainsteam.stm.pluginserver;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.pluginserver.util.ReentrantLock;

/**
 * @author ziw
 * 
 */
public class ReentrantLockTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		final ReentrantLock lock = new ReentrantLock();
		int count = 10;
		final List<Date> ocurrTimeList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			final int index = i;
			Runnable r = new Runnable() {
				@Override
				public void run() {
					lock.lock();
					System.out.println("I am " + index);
					ocurrTimeList.add(new Date());
					synchronized (this) {
						try {
							this.wait(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					lock.unlock();
				}
			};
			new Thread(r).start();
		}
		try {
			Thread.sleep(15000 + 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(4, ocurrTimeList.size());
		lock.close();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 1; i < 4; i++) {
			Date last = ocurrTimeList.get(i - 1);
			Date current = ocurrTimeList.get(i);
			long offset = current.getTime() - last.getTime();
			assertTrue(offset >= 5000);
			assertTrue(offset < 10000);
		}
		assertEquals(count, ocurrTimeList.size());
		for (int i = 4; i < count; i++) {
			Date last = ocurrTimeList.get(i - 1);
			Date current = ocurrTimeList.get(i);
			long offset = current.getTime() - last.getTime();
			assertTrue(offset < 1000);
		}
		for (int i = 1; i < count; i++) {
			Date last = ocurrTimeList.get(i - 1);
			Date current = ocurrTimeList.get(i);
			long offset = current.getTime() - last.getTime();
			System.out.println(offset);
		}
	}

}
