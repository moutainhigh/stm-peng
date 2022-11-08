/**
 * 
 */
package com.mainsteam.stm.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author ziw
 * 
 */
public class SpringStarter {

	/**
	 * 
	 */
	public SpringStarter() {
	}

	public void testStart() {
		ClassPathXmlApplicationContext c = new ClassPathXmlApplicationContext("classpath:test.xml");
		c.start();
		try (BufferedReader reader = new BufferedReader(new FileReader(new File("test.log")))) {
			String line = reader.readLine();
			System.out.println("line=" + line);
			assertEquals("hello", line);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			c.close();
		}
	}

	public void main(String[] args) {
		new SpringStarter().testStart();
		for(String arg:args){
			System.out.println("get args:"+arg);
		}
	}
}
