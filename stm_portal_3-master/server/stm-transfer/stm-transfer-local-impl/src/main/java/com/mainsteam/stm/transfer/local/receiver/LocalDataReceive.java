package com.mainsteam.stm.transfer.local.receiver;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.transfer.local.config.LocalConfig;
import com.mainsteam.stm.transfer.local.maintain.LocalFileCleaner;
import com.mainsteam.stm.transfer.local.sender.LocalDataSender;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年6月13日 下午5:44:44
 * @version 1.0
 */
public class LocalDataReceive {
	private static final Log logger = LogFactory.getLog(LocalDataSender.class);
	private static final String MARK_FILE = "ftp_file_name.mk";
	private LocalConfig config = LocalConfig.getSelf();
	private LocalFileCleaner localFileCleaner;
	private Properties marks;
	private FileOutputStream out;

	public LocalDataReceive() throws IOException {
		marks = new Properties();
		InputStream in = null;
		try {
			final File f = new File(config.getLocalDataDir(), MARK_FILE);
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				try {
					f.createNewFile();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("LocalDataReceive", e);
					}
					throw e;
				}
			}
			in = new FileInputStream(f);
			marks.load(in);
			in.close();
			Runnable marksWriter = new Runnable() {
				@Override
				public void run() {
					try {
						out = new FileOutputStream(f);
						int counter = 0;
						final int MAX = 500;
						do {
							try {
								marks.store(out, "");
								out.flush();
							} catch (IOException e) {
								if (logger.isErrorEnabled()) {
									logger.error("LocalDataReceive", e);
								}
							}
							counter++;
							if (counter >= MAX) {
								counter = 0;
								out.close();
								out = new FileOutputStream(f);
							}
							synchronized (LocalDataReceive.this) {
								try {
									LocalDataReceive.this.wait(60000);
								} catch (Exception e1) {
									if (logger.isErrorEnabled()) {
										logger.error("LocalDataReceive", e1);
									}
								}
							}
						} while (true);
					} catch (Exception e1) {
						if (logger.isErrorEnabled()) {
							logger.error("LocalDataReceive", e1);
						}
						return;
					} finally {
						try {
							out.close();
						} catch (IOException e) {
							if (logger.isErrorEnabled()) {
								logger.error("LocalDataReceive", e);
							}
						}
					}
				}
			};
			new Thread(marksWriter, "LocalDataReceive.marksWriter").start();
			localFileCleaner = new LocalFileCleaner(this.config.getLocalDataDir(), config.getLocalDataTimeout());
			new Thread(localFileCleaner, "localFileCleaner").start();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("LocalDataReceive", e);
			}
//			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public String receiveOneFile() throws Exception {
		String dataFile = scanAndTakeNextFile();
		return dataFile;
	}

	private void markLastestFile(String dcsDir, String remoteFile) throws IOException {
		marks.put(dcsDir, remoteFile);
	}

	private String readlastestFile(String dcsDir) {
		return marks.getProperty(dcsDir);
	}

	public String takeFile(String remoteFile) throws Exception {
		String localDataDir = config.getLocalDataDir();
		File rf = new File(remoteFile);
		File dist = new File(localDataDir, rf.getParentFile().getName());
		try {
			FileUtils.moveFileToDirectory(rf, dist, true);
			if (logger.isInfoEnabled()) {
				logger.info("takeFile " + remoteFile + " to " + dist.getAbsolutePath());
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("takeFile", e);
			}
		}
		if (rf.exists()) {
			rf.delete();
		}
		return dist.getAbsolutePath() + File.separator + rf.getName();
	}

	private File[] getDcsDataDir() {
		String ftpDataDir = config.getFtpDataDir();
		File dir = new File(ftpDataDir);
		File[] dcsDirs = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		return dcsDirs;
	}

	private List<String> listFiles(File dcsDir) {
		List<String> dataFileList = new ArrayList<>();

		File[] dataFiles = dcsDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(LocalConfig.DATA_FILE_FORMAT);
			}
		});
		if (dataFiles != null && dataFiles.length > 0) {
			for (File file : dataFiles) {
				dataFileList.add(file.getAbsolutePath());
			}
		}

		return dataFileList;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("20170918_090801.oc".matches(LocalConfig.DATA_FILE_FORMAT));
		LocalDataReceive receive = new LocalDataReceive();

		System.out.println(receive.receiveOneFile());
	}

	private String scanAndTakeNextFile() throws Exception {
		File[] dcsDirs = getDcsDataDir();
		String localFile = null;
		if (dcsDirs != null && dcsDirs.length > 0) {
			for (File dcsDir : dcsDirs) {
				String remoteFile = null;
				List<String> allRemoteFiles = listFiles(dcsDir);
				if (allRemoteFiles != null && allRemoteFiles.size() > 0) {
					Collections.sort(allRemoteFiles, new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.compareTo(o2);
						}
					});
					// String lastestFile = readlastestFile(dcsDir.getName());
					// if (lastestFile != null) {
					// for (int i = 0; i < allRemoteFiles.size(); i++) {
					// if (allRemoteFiles.get(i).compareTo(lastestFile) > 0) {
					// remoteFile = allRemoteFiles.get(i);
					// break;
					// }
					// }
					// } else {
					remoteFile = allRemoteFiles.get(0);
					// }
				}
				if (remoteFile != null) {
					localFile = takeFile(remoteFile);
					markLastestFile(dcsDir.getName(), remoteFile);
					break;
				}
			}
		}
		return localFile;
	}
}
