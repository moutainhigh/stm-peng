/**
 * 
 */
package com.mainsteam.stm.bootstrap;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author ziw
 * 
 */
public class ClassLoaderUtil {

	private static final Log logger = LogFactory.getLog(BootStrap.MODULE);

	/**
	 * 
	 */
	public ClassLoaderUtil() {
	}

	public static URLClassLoader buildClassLoaderEnv(File[] classPathDir,
			File[] jarFilePath) throws MalformedURLException {
		/**
		 * 加载ClassPath
		 */
		URL[] urls = loadURLs(classPathDir, jarFilePath);
		return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
	}

	private static URL[] loadURLs(File[] classPathDir, File[] jarFilePath)
			throws MalformedURLException {
		List<URL> urls = new ArrayList<>(200);
		for (int i = 0; i < classPathDir.length; i++) {
			urls.add(convertToURL(classPathDir[i]));
		}

		/**
		 * 加载jar包
		 */
		for (int i = 0; i < jarFilePath.length; i++) {
			if (jarFilePath[i].isFile()) {
				urls.add(convertToURL(jarFilePath[i]));
			} else {
				File[] jarFiles = jarFilePath[i].listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						String name = pathname.getName();
						return pathname.isFile()
								&& (name.endsWith(".jar") || name
										.endsWith(".zip"));
					}
				});
				for (int j = 0; j < jarFiles.length; j++) {
					urls.add(convertToURL(jarFiles[j]));
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("loadURLs urls="+urls);
		}
		return urls.toArray(new URL[urls.size()]);
	}

	private static URL convertToURL(File f) throws MalformedURLException {
		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("loadURLs " + f.toURI(), e);
			}
			throw e;
		}
	}

	public static Class<?> readClass(ClassLoader loader, String className)
			throws ClassNotFoundException {
		if (logger.isInfoEnabled()) {
			logger.info("readClass className=" + className);
		}
		try {
			return loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("readClass className=" + className, e);
			}
			throw e;
		}
	}

}
