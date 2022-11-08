package com.mainsteam.stm.cache;

import org.apache.log4j.Logger;

import com.mainsteam.stm.cache.daemon.MemcachedDaemon;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * 
 * <li>文件名称: MemcacheManager.java.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有: 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月19日
 * @author wangxinghao
 */
public class MemcacheManager {

	// 默认编码格式
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static Logger logger = Logger.getLogger(MemcacheManager.class);

	// 默认MemCachedClient
	private static MemCachedClient memCachedClient;
	private static MemcachedDaemon memcachedDaemon;

	private static SockIOPool getSockIOPool() {
		SockIOPool sockIOPool = SockIOPool.getInstance();
		MemcachedPool pool = MemcacheStrategyUtil.getInstance()
				.getMemcachedPool();

		if (pool == null) {
			logger.warn("Memcached Pool is null----------");
			return null;
		}

		sockIOPool.setServers(pool.getServers());
		sockIOPool.setWeights(pool.getWeights());
		sockIOPool.setNagle(pool.getNagle());
		sockIOPool.setInitConn(pool.getInitConn());
		sockIOPool.setMinConn(pool.getMinConn());
		sockIOPool.setMaxConn(pool.getMaxConn());
		sockIOPool.setMaxIdle(pool.getMaxIdle());
		sockIOPool.setMaintSleep(pool.getMaintSleep());
		sockIOPool.setSocketTO(pool.getSocketTO());
		sockIOPool.setSocketConnectTO(pool.getSocketConnectTO());
		sockIOPool.setFailover(pool.isFailover());
		sockIOPool.setAliveCheck(pool.isAliveCheck());
		sockIOPool.setHashingAlg(pool.getHashingAlg());

		return sockIOPool;
	}

	/**
	 * 初始化客户端
	 */
	public static synchronized MemCachedClient initMemCachedClient() {
		if (memCachedClient == null) {
			SockIOPool sockIOPool = getSockIOPool();
			if(sockIOPool == null){
				throw new RuntimeException("init memcached pool fail.");
			}
			sockIOPool.initialize();
			memCachedClient = new MemCachedClient();
			// 设置编码格式
			memCachedClient.setDefaultEncoding(DEFAULT_ENCODING);
			// toString
			memCachedClient.setPrimitiveAsString(true);
			memcachedDaemon = new MemcachedDaemon(memCachedClient);

			logger.info("================MemCachedClient Init==================");
		}
		return memCachedClient;
	}

	/**
	 * 清理所以缓存
	 * 
	 * @return
	 */
	protected static boolean flushAll() {
		return memCachedClient.flushAll();
	}

	/**
	 * 关闭链接
	 * 
	 * @param sockIOPool
	 */
	public static void sockIOPoolShutDown(SockIOPool sockIOPool) {
		sockIOPool.shutDown();
	}

	public static MemcachedDaemon getMemcachedDaemon() {
		return memcachedDaemon;
	}
}
