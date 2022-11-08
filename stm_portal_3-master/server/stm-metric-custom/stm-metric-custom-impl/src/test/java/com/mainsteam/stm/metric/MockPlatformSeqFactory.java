/**
 * 
 */
package com.mainsteam.stm.metric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * @author ziw
 * 
 */
public class MockPlatformSeqFactory {

	private static final String MODULE_FILE = "/sequence.properties";

	private Properties prop;

	private static Map<String, ISequence> seqs = new HashMap<>();

	/**
	 * 
	 */
	public MockPlatformSeqFactory() {
		init();
	}

	private void init() {
		prop = new Properties();
		try (InputStream in = MockPlatformSeqFactory.class
				.getResourceAsStream(MODULE_FILE)) {
			if (in != null) {
				prop.load(in);
				System.out.println("load sequence from prop .");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ISequence getSeq(String module) {
		return createSeq(module);
	}

	private ISequence createSeq(String module) {
		ISequence s = seqs.get(module);
		if (s == null) {
			synchronized (seqs) {
				s = seqs.get(module);
				if (s == null) {
					s = new FileSequence(module);
					seqs.put(module, s);
				}
			}
		}
		return s;
	}

	private String readModule(String module) {
		return prop.getProperty(module);
	}

	private void write(String module, long value) {
		prop.put(module, String.valueOf(value));
		URL url = MockPlatformSeqFactory.class.getResource(MODULE_FILE);
		File f = null;
		if (url == null) {
			String classPath = MockPlatformSeqFactory.class.getResource("/")
					.getFile();
			f = new File(classPath, MODULE_FILE);
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		} else {
			f = new File(url.getFile());
		}
		try (Writer out = new FileWriter(f)) {
			prop.store(out, "Time:" + new Date().toString());
			System.out.println("write module=" + value + " to " + f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class FileSequence implements ISequence {
		private String module;
		private long current;
		private long space;

		public FileSequence(String module) {
			super();
			this.module = module;
			init();
		}

		private void requestNextSpace() {
			space = current + 500;
			write(module, space);
		}

		private void init() {
			String value = MockPlatformSeqFactory.this.readModule(module);
			if (value == null) {
				current = 0;
				space = 500;
				write(module, space);
			} else {
				current = Long.parseLong(value);
				requestNextSpace();
			}
		}

		@Override
		public long current() {
			return current;
		}

		@Override
		public synchronized long next() {
			if (current >= space) {
				requestNextSpace();
			}
			return ++current;
		}
	}

	public static void main(String[] args) {
		System.out.println(new MockPlatformSeqFactory().getSeq("test").next());
	}
}
