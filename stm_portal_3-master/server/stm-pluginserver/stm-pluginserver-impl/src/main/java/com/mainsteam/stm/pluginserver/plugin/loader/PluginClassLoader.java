/**
 * 
 */
package com.mainsteam.stm.pluginserver.plugin.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.util.OSUtil;

/** 
 * @author 作者：ziw
 * @date 创建时间：2016年10月25日 上午9:35:40
 * @version 1.0
 */
/** 
 */
public class PluginClassLoader {

	private static final Log logger = LogFactory.getLog(PluginClassLoader.class);

	/**
	 * 存放插件的目录
	 */
	private static final String PLUGIN_LIB_DIRECTORY = "plugin_lib";

	private static final String PLUGIN_KEY = "CaplibPlugin";
	private static final String PLUGIN_ID_KEY = "Plugin-Class";

	private ClassLoader defaultPluginClassLoader;

	private Map<String, ClassLoader> specialPluginClassLoader;

	/**
	 * 
	 */
	public PluginClassLoader() {
	}

	public void start() {
		constructDefaultPluginClassLoader();
		constructSpecialClassLoader();
	}

	private List<String> findPluginIdFromJarFiles(File[] jarFiles, String dirpath) {
		// int pluginIdCount = 0;
		String[] pluginIdJars = null;
		List<String> pluginIds = new ArrayList<>();
		for (int i = 0; i < jarFiles.length; i++) {
			File f = jarFiles[i];
			pluginIdJars = loadPluginJar(f);
			if (pluginIdJars == null || pluginIdJars.length <= 0) {
				continue;
			}
			// else if (pluginId != "") {
			// pluginIdCount++;
			// }
			// if (pluginIdCount > 1) {// 一个目录，不能有超过2个plugin出现。
			// if (logger.isErrorEnabled()) {
			// logger.error("loadPluginJars.The directory has at most 2 plugin
			// jars which is not permitted.dir="
			// + dirpath);
			// }
			// pluginId = null;
			// break;
			// }
			for (String pluginId : pluginIdJars) {
				if (pluginId.trim().equals("")) {
					continue;
				}
				pluginIds.add(pluginId);
			}
		}
		return pluginIds;
	}

	private URL[] selectValidJarFiles(List<File> findedJarFileList, File[] jarFiles) {
		URL[] validJarFiles = null;
		validJarFiles = new URL[jarFiles.length];
		for (int i = 0; i < validJarFiles.length; i++) {
			try {
				validJarFiles[i] = jarFiles[i].toURI().toURL();
			} catch (MalformedURLException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadPluginJars", e);
				}
			}
		}
		return validJarFiles;
	}

	private void constuctPluginClassLoaders(File dir) {
		File[] jarFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		List<File> jarFileList = null;
		List<String> pluginIdList = null;
		URL[] validJarFiles = null;
		if (jarFiles != null && jarFiles.length > 0) {
			jarFileList = new ArrayList<>(jarFiles.length);
			pluginIdList = findPluginIdFromJarFiles(jarFiles, dir.getAbsolutePath());
			validJarFiles = selectValidJarFiles(jarFileList, jarFiles);
		}
		if (pluginIdList != null && pluginIdList.size() > 0) {
			ClassLoader loader = new URLClassLoader(validJarFiles, defaultPluginClassLoader);
			for (String pluginId : pluginIdList) {
				if (logger.isInfoEnabled()) {
					logger.info("constuctPluginClassLoaders pluginId=" + pluginId + " dir=" + dir.getAbsolutePath());
				}
				this.specialPluginClassLoader.put(pluginId, loader);
			}
		}
	}

	private String[] loadPluginJar(File f) {
		JarFile file = null;
		Manifest m = null;
		String pluginIdStr = null;
		try {
			file = new JarFile(f);
			m = file.getManifest();
			if (m == null) {
				return null;
			}
			Attributes versionAttr = m.getAttributes(PLUGIN_KEY);
			if (versionAttr == null || versionAttr.size() <= 0) {
				return null;
			}
			pluginIdStr = versionAttr.getValue(PLUGIN_ID_KEY);
			if (logger.isInfoEnabled()) {
				logger.info("loadPluginJar find plugin jar." + f.getName() + " pluginIds=" + pluginIdStr);
			}
			return pluginIdStr.split(",");
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("loadPluginJar.jarFile is " + f, e);
			}
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadPluginJar.jarFile is " + f, e);
				}
			}
		}
		return null;
	}

	public ClassLoader getPluginClassLoader(String pluginId) {
		if (specialPluginClassLoader.containsKey(pluginId)) {
			if (logger.isInfoEnabled()) {
				logger.info("getPluginClassLoader pluginId=" + pluginId + " load from specialPluginClassLoader.");
			}
			return specialPluginClassLoader.get(pluginId);
		}
		return defaultPluginClassLoader;
	}

	private void constructDefaultPluginClassLoader() {
		defaultPluginClassLoader = this.getClass().getClassLoader();
	}

	private void constructSpecialClassLoader() {
		specialPluginClassLoader = new HashMap<String, ClassLoader>(50);
		File pluginLibDir = null;
		String dirPath = OSUtil.getEnv("SERVER_HOME");
		if (dirPath != null) {
			pluginLibDir = new File(dirPath, PLUGIN_LIB_DIRECTORY);
			if (!pluginLibDir.exists()) {
				pluginLibDir = new File(getHomeDirectory(), PLUGIN_LIB_DIRECTORY);
			}
		} else {
			pluginLibDir = new File(getHomeDirectory(), PLUGIN_LIB_DIRECTORY);
		}
		if (pluginLibDir != null && pluginLibDir.exists()) {
			File[] pluginDirs = pluginLibDir.listFiles();
			if (pluginDirs.length > 0) {
				for (File f : pluginDirs) {
					if (f.isDirectory()) {
						constuctPluginClassLoaders(f);
					}
				}
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("constructSpecialClassLoader pluginLibDir is not exist." + pluginLibDir);
			}
		}
	}

	private String getHomeDirectory() {
		return System.getProperty("user.dir") + "/..";
	}

	public static void main(String[] args) {
		// System.out.println(System.getProperty("user.dir"));
		PluginClassLoader p = new PluginClassLoader();
//		p.loadPluginJar(new File("target/mainsteam-stm-vmplugin-4.2.3.jar"));
		p.loadPluginJar(new File("g:/work/mainsteam-stm-snmpplugin-4.2.3.jar"));
	}
}
