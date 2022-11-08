/**
 * 
 */
package com.mainsteam.stm.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.springframework.stereotype.Service;

/**
 * 
 * 装载系统的ClassPath，并执行启动类
 * 
 * @author ziw
 * 
 */
public class BootStrapActor implements BootStrap {

	private static final Log logger = LogFactory.getLog(BootStrap.MODULE);

	/**
	 * jar文件夹路径的变量名称
	 */
	public static final String VAR_JAR_FILE_PATH = "JAR_FILE_PATH";

	/**
	 * ClassPath的变量名称
	 */
	public static final String VAR_CLASS_PATH = "CLASS_PATH";

	/**
	 * 启动类的变量名称
	 */
	public static final String VAR_STARTUP_CLASS = "STARTUP_CLASS";

	/**
	 * 变量多值的分隔符
	 */
	public static final String VAR_SPLIT_CHAR = ";,";

	@SuppressWarnings("rawtypes")
	private static Class startupInstance;

	// private static final String MAC = "mac";
	private static final String WIN = "win";
	protected static final String OS_NAME = "os.name";

	private File[] jarFilePath;
	private File[] classPathDir;
	private String startUpClassName;
	private URLClassLoader classLoader;
	private BootStrapEvn bootStrapEvn;

	/**
	 * 
	 */
	public BootStrapActor() {
	}

	public BootStrapEvn getBootStrapEvn() {
		if (bootStrapEvn == null) {
			bootStrapEvn = new DefaultBootStrapEvn();
		}
		return bootStrapEvn;
	}

	@SuppressWarnings("unchecked")
	public void stop() {
		if (startupInstance != null) {
			try {
				Method m = startupInstance.getDeclaredMethod("stop");
				m.invoke(startupInstance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setBootStrapEvn(BootStrapEvn bootStrapEvn) {
		this.bootStrapEvn = bootStrapEvn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.bootstrap.BootStrap#setJarFilePath(java.io.File[])
	 */
	@Override
	public void setJarFilePath(File[] jarFilePath) {
		this.jarFilePath = jarFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.bootstrap.BootStrap#setClassPath(java.io.File[])
	 */
	@Override
	public void setClassPath(File[] classPathDir) {
		this.classPathDir = classPathDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.bootstrap.BootStrap#setStartUpClass(java.lang.Class)
	 */
	@Override
	public void setStartUpClass(String startUpClass) {
		this.startUpClassName = startUpClass;
		if (this.startUpClassName == null || this.startUpClassName.equals("")) {
			showError(VAR_STARTUP_CLASS, "emtpy input.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.bootstrap.BootStrap#startServer(java.lang.String[])
	 */
	@Override
	public void startServer(String[] args) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("startServer begin.");
		}
		boolean isProcessExist =  validteProcessAlone();
		if (isProcessExist) {
			if (logger.isErrorEnabled()) {
				logger.error("startServer error.the server has started already.");
			}
			System.exit(0);
		}
		this.classLoader = ClassLoaderUtil.buildClassLoaderEnv(classPathDir,
				jarFilePath);
		if (logger.isDebugEnabled()) {
			logger.debug("startServer classLoader=" + classLoader);
		}
		Thread.currentThread().setContextClassLoader(this.classLoader);
		Class<?> startClass = ClassLoaderUtil.readClass(this.classLoader,
				startUpClassName);
		logger.debug("startUpClassName=" + startUpClassName);
		logger.debug("read over "
				+ ClassLoaderUtil
						.readClass(this.classLoader,
								"org.springframework.beans.factory.config.BeanDefinition"));
		/**
		 * 执行startup类的main方法.
		 */
		Method mainMethod = startClass
				.getDeclaredMethod("main", String[].class);
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("startServer execute parameters=");
			for (int i = 0; i < args.length; i++) {
				b.append(args[i]).append(' ');
			}
			logger.debug(b.toString());
		}
		startupInstance = startClass;
		mainMethod.invoke(startClass, (Object) args);
	}

	private boolean validteProcessAlone() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String currentPid = name.split("@")[0];

		String os = System.getProperty(OS_NAME);
		List<String> plistList = null;
		if (os != null && os.toLowerCase().indexOf(WIN) == -1) {
			/**
			 * Linux
			 */
			plistList = runCmd(new String[] { "ps", "-ef" });
			if (plistList != null && plistList.size() > 0) {
				String currentServerPath = System.getenv("SERVER_HOME");
				if (currentServerPath == null) {
					currentServerPath = System.getProperty("user.dir");
					if (currentServerPath != null
							&& (currentServerPath.endsWith("bin") || currentServerPath
									.endsWith("bin/"))) {
						int lastFlagIndex = currentServerPath.lastIndexOf('/',
								currentServerPath.length() - 3);
						if (lastFlagIndex > 0) {
							currentServerPath = currentServerPath.substring(0,
									lastFlagIndex);
						}
					}
				}
				if (currentServerPath != null) {
					String regex = "^[^\\s]+\\s+(\\d)+.+\\d{2}:\\d{2}:\\d{2}\\s+(.+)$";
					Pattern p = Pattern.compile(regex);
					for (String pinfo : plistList) {
						Matcher m = p.matcher(pinfo);
						if (m.find()) {
							String pid = m.group(1);
							if (currentPid.equals(pid)) {
								continue;
							}
							String cmd = m.group(2);
							if (cmd.indexOf(currentServerPath) != -1
									&& cmd.indexOf(startUpClassName) != -1) {
								if (logger.isErrorEnabled()) {
									logger.error("find process has started.pinfo="
											+ pinfo);
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private List<String> runCmd(final String[] cmd) {
		final List<String> plist = new ArrayList<>();
		final Process proc;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			// 处理InputStream的线程
			Thread outThread = new Thread() {
				@Override
				public void run() {
					BufferedReader in = new BufferedReader(
							new InputStreamReader(proc.getInputStream()));
					String line = null;
					try {
						while ((line = in.readLine()) != null) {
							plist.add(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			outThread.start();

			Thread errorThread = new Thread() {
				@Override
				public void run() {
					BufferedReader err = new BufferedReader(
							new InputStreamReader(proc.getErrorStream()));
					StringBuilder builder = new StringBuilder();
					String line = null;
					try {
						while ((line = err.readLine()) != null) {
							builder.append(line).append('\n');
						}
						if (logger.isErrorEnabled()) {
							logger.error("runCmd cmd=" + Arrays.toString(cmd)
									+ " errMsg=" + builder.toString());
						}
					} catch (IOException e) {
						if (logger.isErrorEnabled()) {
							logger.error("run", e);
						}
					} finally {
						try {
							err.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			errorThread.start();
			proc.waitFor();
			errorThread.join();
			outThread.join();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("runplistCmd", e);
			}
		}
		return plist;
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("bootstrap start.");
		}
		boolean isStart = true;
		if (args.length > 0) {
			isStart = args[0].equals("stop") ? false : true;
		}
		if (isStart) {
			if (startupInstance != null) {
				return;
			}
			BootStrapActor boot = new BootStrapActor();
			/**
			 * 读取初始化参数
			 */
			boot.setClassPath(boot.readFileSystemProperty(VAR_CLASS_PATH));
			boot.setJarFilePath(boot.readFileSystemProperty(VAR_JAR_FILE_PATH));
			boot.setStartUpClass(boot.getBootStrapEvn().getEnv(
					VAR_STARTUP_CLASS));

			/**
			 * 启动服务器
			 */
			try {
				boot.startServer(args);
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error("main", e);
				}
			}
		} else {
			BootStrapActor boot = new BootStrapActor();
			boot.stop();
		}
		if (logger.isInfoEnabled()) {
			logger.info("bootstrap end.");
		}
	}

	private File[] readFileSystemProperty(String key) {
		if (logger.isDebugEnabled()) {
			logger.debug("readFileSystemProperty read " + key);
		}
		String keyValue = getBootStrapEvn().getEnv(key);
		if (keyValue != null) {

			StringTokenizer tokenizer = new StringTokenizer(keyValue,
					VAR_SPLIT_CHAR, false);
			List<String> fileNamesList = new ArrayList<>();
			while (tokenizer.hasMoreElements()) {
				String token = (String) tokenizer.nextElement();
				fileNamesList.add(token);
			}
			String[] fileNames = fileNamesList.toArray(new String[fileNamesList
					.size()]);
			File[] systemFiles = new File[fileNames.length];
			for (int i = 0; i < fileNames.length; i++) {
				File inputFile = new File(fileNames[i]);
				if (inputFile.exists()) {
					systemFiles[i] = inputFile;
				} else {
					throw new RuntimeException("invalid path " + fileNames[i]);
				}
			}
			return systemFiles;
		}
		return null;
	}

	private static void showError(String key, String msg) {
		if (logger.isErrorEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append(key);
			b.append(" has invalid value=");
			b.append(System.getenv(key));
			b.append(",");
			b.append(msg);
			logger.error(b);
			throw new RuntimeException(b.toString());
		}
	}
}
