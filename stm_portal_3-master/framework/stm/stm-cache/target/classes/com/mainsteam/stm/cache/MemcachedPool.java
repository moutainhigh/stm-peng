package com.mainsteam.stm.cache;


/**
 * 
 * <li>文件名称: MemcachedPool.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月19日
 * @author wangxinghao
 */
public class MemcachedPool {

	// 服务器列表
	private String[] servers;
	// 权重, 默认 1
	private Integer[] weights;
	

	// 初始连接数
	int initConn = 10;

	// 最小连接数
	int minConn = 10;

	// 最大连接数
	int maxConn = 20;

	// 最大处理时间
	int maxIdle = 1000 * 60 * 30;
	// 设置连接池守护线程的睡眠时间
	int maintSleep = 1000 * 10;

	// 设置是否使用Nagle算法 ,默认是true
	boolean nagle = true;

	// 设置socket的读取等待超时值，默认60
	int socketTO = 1000 * 3;

	// 设置socket的连接等待超时值,默认60
	int socketConnectTO = 1000 * 3;

	// 设置连接心跳监测开关。
	// 设为true则每次通信都要进行连接是否有效的监测，造成通信次数倍增，加大网络负载，因此该参数应该在对HA要求比较高的场合设为TRUE，默认状态是false。
	boolean aliveCheck = false;

	// 设置连接失败恢复开关 , 默认状态是true
	// 设置为TRUE，当宕机的服务器启动或中断的网络连接后，这个socket连接还可继续使用，否则将不再使用。
	boolean failback = true;

	// 设置容错开关,默认状态是true
	// 设置为TRUE，当当前socket不可用时，程序会自动查找可用连接并返回，否则返回NULL，，建议保持默认。
	boolean failover = true;

	/*
	 * 设置hash算法 alg=0 使用String.hashCode()获得hash code,该方法依赖JDK alg=1 使用original
	 * 兼容hash算法，兼容其他客户端 alg=2 使用CRC32兼容hash算法，兼容其他客户端，性能优于original算法 alg=3 使用MD5
	 * hash算法
	 * 采用前三种hash算法的时候，查找cache服务器使用余数方法。采用最后一种hash算法查找cache服务时使用consistent方法。
	 */
	int hashingAlg = 0;

	public String[] getServers() {
		return servers;
	}

	public void setServers(String[] servers) {
		this.servers = servers;
	}

	public Integer[] getWeights() {
		return weights;
	}

	public void setWeights(Integer[] weights) {
		this.weights = weights;
	}

	public int getInitConn() {
		return initConn;
	}

	public void setInitConn(int initConn) {
		this.initConn = initConn;
	}

	public int getMinConn() {
		return minConn;
	}

	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaintSleep() {
		return maintSleep;
	}

	public void setMaintSleep(int maintSleep) {
		this.maintSleep = maintSleep;
	}

	public boolean getNagle() {
		return nagle;
	}

	public void setNagle(boolean nagle) {
		this.nagle = nagle;
	}

	public int getSocketTO() {
		return socketTO;
	}

	public void setSocketTO(int socketTO) {
		this.socketTO = socketTO;
	}

	public int getSocketConnectTO() {
		return socketConnectTO;
	}

	public void setSocketConnectTO(int socketConnectTO) {
		this.socketConnectTO = socketConnectTO;
	}

	public boolean isAliveCheck() {
		return aliveCheck;
	}

	public void setAliveCheck(boolean aliveCheck) {
		this.aliveCheck = aliveCheck;
	}

	public boolean isFailback() {
		return failback;
	}

	public void setFailback(boolean failback) {
		this.failback = failback;
	}

	public boolean isFailover() {
		return failover;
	}

	public void setFailover(boolean failover) {
		this.failover = failover;
	}

	public int getHashingAlg() {
		return hashingAlg;
	}

	public void setHashingAlg(int hashingAlg) {
		this.hashingAlg = hashingAlg;
	}

	
}
