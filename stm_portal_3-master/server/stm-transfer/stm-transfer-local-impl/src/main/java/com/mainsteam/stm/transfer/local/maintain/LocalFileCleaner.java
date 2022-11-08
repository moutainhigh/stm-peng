package com.mainsteam.stm.transfer.local.maintain;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.transfer.local.config.LocalConfig;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年6月14日 下午1:24:35
 * @version 1.0
 */
public class LocalFileCleaner implements Runnable {

	private static final Log logger = LogFactory.getLog(LocalFileCleaner.class);

	private static final int MIN_TIMEOUT = 60000; // 1 minute
	private long interval = 3600000;
	private String dataDir;
	private long timeout;

	public LocalFileCleaner(String dataDir, long timeout) {
		this.dataDir = dataDir;
		this.timeout = timeout;
		if (this.timeout <= MIN_TIMEOUT) {
			this.timeout = MIN_TIMEOUT;
		}
		if (this.timeout >= 12 * 60 * 60 * 1000) {
			this.interval = 3600000;// scan per hour.
		} else if (this.timeout >= 6 * 60 * 60 * 1000) {
			this.interval = 1800000;// scan 30 minute.
		} else if (this.timeout >= 3 * 60 * 60 * 1000) {
			this.interval = 600000;// scan 10 minute.
		} else if (this.timeout >= 1 * 60 * 60 * 1000) {
			this.interval = 300000;// scan 5 minute.
		} else {
			this.interval = 60000;// scan 1 minute.
		}
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				try {
					this.wait(interval);
				} catch (InterruptedException e) {
				}
			}
			scanAndRemoveFile();
		}
	}

	private void recursivilyScanAndRemoveFile(File dir, long currentTime) {
		File[] toscans = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						|| pathname.isFile() && pathname.getName().matches(LocalConfig.DATA_FILE_FORMAT);
			}
		});
		for (File f : toscans) {
			if (f.isDirectory()) {
				recursivilyScanAndRemoveFile(f, currentTime);
			} else if ((currentTime - f.lastModified()) >= timeout) {
				try {
					FileUtils.forceDelete(f);
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("scanAndRemoveFile", e);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("scanAndRemoveFile f" + f.getAbsolutePath());
				}
			}
		}
	}

	public void scanAndRemoveFile() {
		File dir = new File(dataDir);
		if (dir.exists() && dir.isDirectory()) {
			recursivilyScanAndRemoveFile(dir, System.currentTimeMillis());
		}
	}
}
