package com.mainsteam.stm.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUtil {
	private static final Log logger = LogFactory.getLog(FileUtil.class);

	/**
	 * createFile
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean createFile(File file) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		return file.createNewFile();
	}

	/**
	 * makeDir
	 * 
	 * @param dir
	 */
	public static void makeDir(File dir) {
		if (!dir.getParentFile().exists()) {
			makeDir(dir.getParentFile());
		}
		dir.mkdir();
	}

	/**
	 * remove File
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean removeFile(File file) throws IOException {
		if (!file.exists()) {
			return true;
		}
		return file.delete();
	}

	/**
	 * get file size
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static long getFileSizes(File f) throws IOException {// 取得文件大小
		long size = 0;
		FileInputStream fis = null;
		try {
			if (f.exists()) {
				fis = new FileInputStream(f);
				size = fis.available();
			} else {
				throw new IOException("文件不存在");
			}
		} finally {
			fis.close();
		}
		return size;
	}

	/**
	 * get file Mimetype
	 * 
	 * @param file
	 * @return
	 */
	public static String getMimetype(File file) {
		return new MimetypesFileTypeMap().getContentType(file);
	}

	/**
	 * get ext Name
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExt(String fileName) {
		String extName = "";
		if (fileName.indexOf(".") != -1) {
			extName = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		return extName;
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		if (!targetFile.exists()) {
			createFile(targetFile);
		}

		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null) {
				inBuff.close();
			}

			if (outBuff != null) {
				outBuff.close();
			}
		}
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(
						new File(targetDir).getAbsolutePath() + File.separator
								+ file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + File.separator + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + File.separator + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	/**
	 * 将输入流读入磁盘文件
	 * 
	 * @param in
	 *            输入流
	 * @param outFilePath
	 *            输出磁盘文件路径
	 * @param size
	 *            缓冲区大小
	 * @author ziwen
	 * @date 2019年10月13日
	 */
	public static Boolean inputStreamToFile(InputStream in, String outFilePath,
			int size) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(outFilePath));
			byte[] buffer = new byte[size];
			while (in.read(buffer) != -1) {
				os.write(buffer);
			}
			return true;
		} catch (FileNotFoundException e) {
			logger.error("数据文件不存在：" + e.getMessage());
		} catch (IOException e) {
			logger.error("文件写入失败：" + e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				logger.error("文件流关闭失败：" + e.getMessage());
			}
		}
		return false;
	}

	/**
	 * 将输入流转成二进制数组
	 * 
	 * @param in
	 * @return
	 * @author ziwen
	 * @param bufSize
	 *            缓冲数组大小（大小根据自己定义）
	 * @date 2019年10月16日
	 */
	public static byte[] inputStreamToBytes(InputStream in, int bufSize) {
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			byte[] buf = new byte[bufSize];
			while (in.read(buf) != -1) {
				os.write(buf);
			}
			return os.toByteArray();
		} catch (IOException e) {
			logger.error("输入流读取失败：" + e.getMessage());
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				logger.error("文件流关闭失败：" + e.getMessage());
			}
		}

	}

	/**
	 * @param dbPath
	 * @author ziwen
	 * @date 2019年10月16日
	 */
	public static InputStream getFileToInputStream(String dbPath)
			throws IOException {
		return new FileInputStream(new File(dbPath));
	}

	/**
	 * 获取文件Md5值
	 * 
	 * @param in
	 *            文件转入流
	 * @return
	 */
	public static String getFileMd5(FileInputStream in) {
		if (in == null) {
			return null;
		}
		MessageDigest digest = null;
		byte buffer[] = new byte[8192];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			while ((len = in.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			BigInteger bigInt = new BigInteger(1, digest.digest());
			return bigInt.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过文件流的MD5比较文件内容是否一致
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static boolean compareFile(FileInputStream f1, FileInputStream f2) {
		String f1Md5 = FileUtil.getFileMd5(f1);
		return f1Md5 == null ? false : f1Md5.equals(FileUtil.getFileMd5(f2));
	}
}
