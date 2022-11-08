/**
 * 
 */
package com.mainsteam.stm.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * @author ziw
 * 
 */
public class PropertiesFileUtil {
	public static Properties getProperties(ClassLoader loader,
			String packageProfileFileName) {
		if (StringUtils.isEmpty(packageProfileFileName)) {
			return null;
		}
		int lastIndex = packageProfileFileName.lastIndexOf('.');
		if (lastIndex < 0) {
			lastIndex = 0;
		}
		packageProfileFileName = packageProfileFileName.substring(0, lastIndex)
				.replace('.', '/')
				+ packageProfileFileName.substring(lastIndex);
		Properties p=null;
		try {
			InputStream in = loader.getResourceAsStream(packageProfileFileName);
			 BufferedReader bf = new BufferedReader(new InputStreamReader(in, "utf-8"));
			p = null;
			if (in != null) {
				p = new Properties();
				try {
					p.load(bf);
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
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	public static Properties getProperties(String packageProfileFileName) {
		return getProperties(Thread.currentThread().getContextClassLoader(),
				packageProfileFileName);
	}
	public static Properties getPropertie(String packageProfileFileName) {
		  Properties properties = new Properties();
	        try {
	            InputStream inputStream = new FileInputStream(packageProfileFileName);
	            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
	          
	            properties.load(bf);
	            System.out.println(properties.get("stm.insplanName"));
	            inputStream.close(); // 关闭流
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return properties;
	    }

	public static void main(String[] args) {
		Properties p = getProperties(PropertiesFileUtil.class.getClassLoader(),
				"com.mainsteam.stm.util.test.test.properties");
		System.out.println(p.get("a"));
	}
}
