/**
 * 
 */
package com.mainsteam.stm.bootstrap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author ziw
 * 
 */
public class FileWriterTest {

	/**
	 * 
	 */
	public FileWriterTest() {
	}

	public void start() throws IOException {
		File logFile = new File("test.log");
		System.out.print("filePath:"+logFile.getAbsolutePath());
		try (FileWriter w = new FileWriter(logFile)) {
			w.write("hello");
		}
	}
}
