/**
 * 
 */
package com.mainsteam.stm.persister;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.util.OSUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author ziw
 * 
 */
public class DefaultObjectFileDatabase<T> implements ObjectFileDatabase<T> {

	private static final Log logger = LogFactory
			.getLog(ObjectFileDatabase.class);

	private String moduleKey;

	private File baseDirector;
	
	private XStream xs = new XStream(new DomDriver());
	
	private Map<String, Class<?>> alias;

	public static final String ENCODING = "UTF-8";

	/**
	 * 
	 */
	public DefaultObjectFileDatabase(String moduleKey,Map<String, Class<?>> alias) {
		this.moduleKey = moduleKey;
		this.alias = alias;
		init();
	}
	
	public void setAlias(Map<String, Class<?>> alias) {
		this.alias = alias;
	}



	private void init() {
		// 初始化文件目录
		initBaseDir();
		loadAlias();
	}

	private void initBaseDir() {
		String baseDir = OSUtil.getEnv("ObjectFileDatabase.Base", "FileData");
		String server_home = OSUtil.getEnv("SERVER_HOME", "..");
		baseDir = server_home + File.separator + baseDir+ File.separator+this.moduleKey;
		baseDirector = new File(baseDir);
		if (!baseDirector.exists()) {
			if (logger.isInfoEnabled()) {
				logger.info("ready to initBaseDir.baseDir=" + baseDir);
			}
			baseDirector.mkdirs();
			if (logger.isInfoEnabled()) {
				logger.info("initBaseDir make baseDir ok.");
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("initBaseDir load baseDir ok.baseDir=" + baseDir);
		}
	}

	@Override
	public String saveObject(String objId, T obj) throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("saveObject start.key=" + moduleKey + " id=" + objId);
		}
		// 写入文件
		String result = xs.toXML(obj);
		String path = writeFile(result, objId);
		if (logger.isInfoEnabled()) {
			logger.info("saveObject end.path=" + path);
		}
		return path;
	}
	
	private void loadAlias(){
		if (alias != null) {
			for (Entry<String, Class<?>> aliasOne : alias.entrySet()) {
				xs.alias(aliasOne.getKey(), aliasOne.getValue());
			}
		}
	}

	private synchronized String writeFile(String content, String objId)
			throws IOException {
		File toWriteFile = new File(baseDirector, objId + ".obj");
		if (!toWriteFile.exists() || !toWriteFile.isFile()) {
			toWriteFile.createNewFile();
		}
		FileUtils.write(toWriteFile, content, ENCODING);
		return toWriteFile.getAbsolutePath();
	}

	private String readContent(String objId) throws IOException {
		File toReadFile = new File(baseDirector, objId + ".obj");
		if (!toReadFile.exists() || !toReadFile.isFile()) {
			return null;
		}
		return FileUtils.readFileToString(toReadFile, ENCODING);
	}

	@SuppressWarnings("unchecked")
	private T fromXml(String content) {
		T obj = (T) xs.fromXML(content);
		return obj;
	}

	@Override
	public T loadObjectFromId(String objId) throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("loadObjectFromId start.moduleKey="+this.moduleKey+" objId="+objId);
		}
		T pObj = null;
		String xml = readContent(objId);
		if (xml != null && xml.length() > 0) {
			try {
				pObj = fromXml(xml);
			} catch (ClassCastException e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadObjectFromId", e);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("loadObjectFromId", e);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("loadObjectFromId end");
		}
		return pObj;
	}

	@Override
	public List<T> loadAllObjects() throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("loadAllObjects start.moduleKey="+this.moduleKey);
		}
		List<T> listObjs = new ArrayList<>();
		File[] list = baseDirector.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".obj");
			}
		});
		if (list != null) {
			for (File file : list) {
				String xml = FileUtils.readFileToString(file, ENCODING);
				if (xml != null && xml.length() > 0) {
					try {
						T pObj = fromXml(xml);
						listObjs.add(pObj);
					} catch (ClassCastException e) {
						if (logger.isErrorEnabled()) {
							logger.error("loadObjectFromId", e);
						}
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("loadObjectFromId", e);
						}
					}
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("loadAllObjects end.listObjs.size="+listObjs.size());
		}
		return listObjs;
	}

	@Override
	public void removeObjectById(String objId) throws IOException{
		StringBuilder b = new StringBuilder(500);
		b.append(baseDirector);
		b.append(File.separator);
		b.append(objId);
		b.append(".obj");
		File file = new File(b.toString());
		if(file.exists()){
			file.delete();
		}
	}

}
