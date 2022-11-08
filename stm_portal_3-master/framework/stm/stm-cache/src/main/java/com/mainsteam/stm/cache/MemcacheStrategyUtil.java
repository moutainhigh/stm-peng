package com.mainsteam.stm.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.util.ClassPathUtil;

/**
 * 
 * <li>文件名称: MemcacheStrategyUtil.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: 缓存策略分配工具，初始化所有的缓存策略</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月30日
 * @author wangxinghao
 */
@SuppressWarnings("unchecked")
public class MemcacheStrategyUtil {
	
	private static final String CONFILE = "config"+File.separator+"cacheConfig.xml";
	
	private static final Log logger = LogFactory.getLog(MemcacheStrategyUtil.class);
	
	private static MemcachedPool memcachedPool = null;

	private static MemcacheStrategyUtil memcacheStrategyUtil;

	private MemcacheStrategyUtil() {}

	public static MemcacheStrategyUtil getInstance() {
		if (memcacheStrategyUtil == null) {
			synchronized (MemcacheStrategyUtil.class) {
				if (memcacheStrategyUtil == null) {
//					loadCacheConfig();
					memcacheStrategyUtil = new MemcacheStrategyUtil();
				}
			}
		}
		return memcacheStrategyUtil;
	}

	public MemcachedPool getMemcachedPool(){
		if(memcachedPool!=null){
			return memcachedPool;
		}
		
		memcachedPool = new MemcachedPool();
		
		String cacheFilePath = ClassPathUtil.getCommonClasses()+CONFILE;
		File cacheFile=new File(cacheFilePath);

		InputStream ins = MemcacheStrategyUtil.class.getClassLoader().getResourceAsStream(CONFILE);   
        SAXReader reader = new SAXReader();    
        Document doc=null;
		try {
			
			if(cacheFile.exists() && cacheFile.isFile()){
				doc = reader.read(cacheFile);
			}else if(ins!=null){
				doc = reader.read(ins);
			}
			
			if(doc==null){
				logger.error("Can't not read cacheConfig.xml");
				return null;
			}
			
			
			//local server info
			List<Element> serversElement=doc.selectNodes("config/servers/server");
			String[] servers=new String[serversElement.size()];
	        Integer[] weights = new Integer[serversElement.size()];
			
	        for(int i=0;i<serversElement.size();i++){
	        	Element e=serversElement.get(i);
				String ip=e.attribute("IP").getValue();
				String port=e.attribute("port").getValue();
				String weight=e.attribute("weight").getValue();
	        	servers[i]=ip+":"+port;
	        	weights[i]=Integer.parseInt(weight);
	        }
	        
	        memcachedPool.setServers(servers);
	        memcachedPool.setWeights(weights);
	        
        	Element initConn=(Element)doc.selectObject("config/initConn");
        	Element minConn=(Element)doc.selectObject("config/minConn");
        	Element maxConn=(Element)doc.selectObject("config/maxConn");
        	Element maintSleep=(Element)doc.selectObject("config/maintSleep");
        	Element nagle=(Element)doc.selectObject("config/nagle");
        	Element socketTO=(Element)doc.selectObject("config/socketTO");
        	Element socketConnectTO=(Element)doc.selectObject("config/socketConnectTO");
        	Element aliveCheck=(Element)doc.selectObject("config/aliveCheck");
        	Element hashingAlg=(Element)doc.selectObject("config/hashingAlg");
        	Element maxIdle=(Element)doc.selectObject("config/maxIdle");
        	
        	memcachedPool.setInitConn(Integer.parseInt(initConn.getText()));
        	memcachedPool.setMinConn(Integer.parseInt(minConn.getText()));
        	memcachedPool.setMaxConn(Integer.parseInt(maxConn.getText()));
        	memcachedPool.setMaintSleep(Integer.parseInt(maintSleep.getText()));
        	memcachedPool.setNagle(Boolean.getBoolean(nagle.getText()));
        	memcachedPool.setSocketTO(Integer.parseInt(socketTO.getText()));
        	memcachedPool.setSocketConnectTO(Integer.parseInt(socketConnectTO.getText()));
        	memcachedPool.setAliveCheck(Boolean.getBoolean(aliveCheck.getText()));
        	memcachedPool.setHashingAlg(Integer.parseInt(hashingAlg.getText()));
        	memcachedPool.setMaxIdle(Integer.parseInt(maxIdle.getText()));
        	
		} catch (DocumentException e1) {
			if (logger.isErrorEnabled()) {
				logger.error("DateUtil.parseDate : ", e1);
			}
			
		}finally{
			if(ins!=null){
				try {
					ins.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("DateUtil.parseDate : ", e);
					}
				}
			}
		}	
		
		
		return memcachedPool;
	}
	
	public static void main(String args[]){
//		MemcacheStrategyUtil u=MemcacheStrategyUtil.getInstance();
		
//		u.getMemcachedPool();
		
	}


}
