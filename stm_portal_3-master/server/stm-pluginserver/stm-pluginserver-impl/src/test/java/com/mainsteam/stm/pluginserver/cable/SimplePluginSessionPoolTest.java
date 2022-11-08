package com.mainsteam.stm.pluginserver.cable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimplePluginSessionPoolTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	class BorrowCounter {
		int count = 0;
	}

	@Test
	public void testBorrowNull() {
		int size = 10;
		final BorrowCounter counter = new BorrowCounter();
		final SimpletPluginSessoinTest test = new SimpletPluginSessoinTest(size);
		for (int i = 0; i < size * 2; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String o = test.borrowSession();
						System.out.println(o);
						synchronized (SimpletPluginSessoinTest.class) {
							if (o == null) {
								counter.count++;
								return;
							}
							SimpletPluginSessoinTest.class.wait(1000);
						}
						test.returnSession(o);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		synchronized (this) {
			try {
				this.wait(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertEquals(size, counter.count);
	}

	@Test
	public void testDestory() throws Exception {
		int size = 10;
		SimpletPluginSessoinTest test = new SimpletPluginSessoinTest(size);
		for (int i = 0; i < size; i++) {
			String session = test.borrowSession();
			assertEquals(String.valueOf(0), session);
			test.destory(session);
			assertEquals(-1, test.getTop());
			assertEquals(0, test.getValidateLength());
			String[] stack = test.getStack();
			for (int j = 0; j < stack.length; j++) {
				assertNull(stack[j]);
			}
		}

		String session1 = test.borrowSession();
		String sessoin2 = test.borrowSession();
		String session3 = test.borrowSession();

		assertEquals(-1, test.getTop());
		assertEquals(3, test.getValidateLength());
		test.returnSession(session1);
		test.returnSession(sessoin2);
		test.returnSession(session3);

		String[] stack = test.getStack();
		for (int j = 0; j < test.getValidateLength(); j++) {
			System.out.println(j + "=" + stack[j]);
			assertNotNull(stack[j]);
		}
		for (int j = test.getValidateLength(); j < stack.length; j++) {
			assertNull(stack[j]);
		}
		test.destory(sessoin2);

		for (int j = 0; j < stack.length; j++) {
			assertNotEquals(sessoin2, stack[j]);
		}
		for (int j = 0; j < test.getValidateLength(); j++) {
			assertNotNull(stack[j]);
		}
		assertEquals(2,test.getValidateLength());
	}

	public static class SimpletPluginSessoinTest {
		private String[] stack;
		private int top = -1;
		private int validateLength = 0;

		public SimpletPluginSessoinTest(int maxActive) {
			stack = new String[maxActive];
		}

		/**
		 * @return the stack
		 */
		public final String[] getStack() {
			return stack;
		}

		/**
		 * @return the top
		 */
		public final int getTop() {
			return top;
		}

		/**
		 * @return the validateLength
		 */
		public final int getValidateLength() {
			return validateLength;
		}

		public synchronized String borrowSession() throws Exception {
			if (top < 0) {
				if (validateLength < stack.length) {
					String session = String.valueOf(validateLength);
					returnSession(session);
					validateLength++;
				} else {
					return null;
				}
			}
			String session = stack[top];
			stack[top] = null;
			top--;
			return session;
		}

		public synchronized void returnSession(String session) throws Exception {
			stack[++top] = session;
		}

		public synchronized void destory(String session) throws Exception {
			boolean find = false;
			for (int i = 0; i < validateLength; i++) {
				if (stack[i].endsWith(session)) {
					stack[i] = null;
					if (i < validateLength - 1) {
						System.arraycopy(stack, i + 1, stack, i, validateLength
								- i - 1);
					}
					validateLength--;
					find = true;
					break;
				}
			}
			if (find) {
				return;
			} else {
				validateLength--;
			}
		}
	}
}
