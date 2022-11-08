/**
 * 
 */
package com.mainsteam.stm.sso.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author wxh
 * 
 */
public class PropertiesFileUtil {
	public static Properties getProperties(ClassLoader loader,
			String packageProfileFileName) {
		if (packageProfileFileName==null || "".equals(packageProfileFileName)) {
			return null;
		}
		int lastIndex = packageProfileFileName.lastIndexOf('.');
		if (lastIndex < 0) {
			lastIndex = 0;
		}
		packageProfileFileName = packageProfileFileName.substring(0, lastIndex)
				.replace('.', '/')
				+ packageProfileFileName.substring(lastIndex);
		InputStream in = loader.getResourceAsStream(packageProfileFileName);
		Properties p = null;
		if (in != null) {
			p = new Properties();
			try {
				p.load(in);
			} catch (IOException e) {
				e.printStackTrace();
				p = null;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return p;
	}

	public static Properties getProperties(String packageProfileFileName) {
		return getProperties(Thread.currentThread().getContextClassLoader(),
				packageProfileFileName);
	}

	public static void main(String[] args) {
		Properties p = getProperties(PropertiesFileUtil.class.getClassLoader(),
				"jmx_config.properties");
		System.out.println(p);
	}
}
