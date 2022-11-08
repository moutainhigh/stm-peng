package com.mainsteam.stm.plugin.was;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.python.modules.newmodule;

import utils.system;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.StatDescriptor;
import com.ibm.websphere.pmi.stat.WSAverageStatistic;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSCountStatistic;
import com.ibm.websphere.pmi.stat.WSEJBStats;
import com.ibm.websphere.pmi.stat.WSJCAConnectionPoolStats;
import com.ibm.websphere.pmi.stat.WSJDBCConnectionPoolStats;
import com.ibm.websphere.pmi.stat.WSJTAStats;
import com.ibm.websphere.pmi.stat.WSJVMStats;
import com.ibm.websphere.pmi.stat.WSRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.websphere.pmi.stat.WSThreadPoolStats;
import com.ibm.websphere.pmi.stat.WSWebAppStats;

/**
 * <li>文件名称: Websphere6Collect.java</li>
 * <li>文件描述: 将采集添加到定时任务中</li>
 * <li>版权所有: 版权所有(C)2005-2014</li>
 * <li>公司: 美新翔盛</li>
 * <li>内容摘要: // 简要描述本文件的内容，包括主要模块、函数及其功能的说明</li>
 * <li>其他说明: // 其它内容的说明</li>
 * <li>完成日期: // 输入完成日期，例：2000年2月25日</li>
 * <li>修改记录: // 修改历史记录，包括修改日期、修改者及修改内容</li>
 * @version 1.0
 * @author huguangbin
 */
@SuppressWarnings("rawtypes")
public class Websphere6Collect{// implements IAppCollector {
	private static Log logger = LogFactory.getLog(Websphere6Collect.class);
	private static final String CLIENT_TYPE_BASE = "Base";
	private static final String CLIENT_TYPE_ND = "ND";
	private static Map<WasConnInfo, AdminClient> baseClientMap = new ConcurrentHashMap<WasConnInfo, AdminClient>();
	private static Map<WasConnInfo, AdminClient> ndClientMap = new ConcurrentHashMap<WasConnInfo, AdminClient>();
	

    private AdminClient ac = null;
    private AdminClient ndClient = null;

    private ObjectName perfOName = null;

    private ObjectName jvmOName = null;

    private ObjectName ejbOName = null;

    private ObjectName jtaOName = null;
    
    private ConfigCollector confCollector;

    public static String getAllResult(JBrokerParameter parameter) {
    	Websphere6Collect collect = new Websphere6Collect();
    	WasConnInfo connInfo = getConnInfo(parameter);
    	String result = collect.collectAll(connInfo);
    	logger.info("Websphere all result:" + result);
    	return result;
    }
    
    public static String getWasAvail(JBrokerParameter parameter) {
    	Websphere6Collect collect = new Websphere6Collect();
    	WasConnInfo connInfo = getConnInfo(parameter);
    	String result = collect.collectAvail(connInfo);
    	logger.info("Websphere is avail:" + result);
    	return result;
    }
    
    private static WasConnInfo getConnInfo(JBrokerParameter parameter) {
    	String wasServerType = parameter.getIBMWebSphereParameter().getDeployType().toUpperCase();
		String isSecOn = parameter.getIBMWebSphereParameter().getIsSecurity();
		
    	WasConnInfo connInfo = new WasConnInfo(parameter.getIp(), parameter.getPort(), 
    			parameter.getUsername(), parameter.getPassword());
    	
    	connInfo.setAppNdIp(parameter.getIBMWebSphereParameter().getAppDmgrIp());
    	connInfo.setAppNdPort(parameter.getIBMWebSphereParameter().getAppDmgrPort());
    	
    	connInfo.setWasServerType(wasServerType);
    	boolean enableSecurity = isSecOn.equals("1");
		connInfo.setEnableSecurity(enableSecurity);
		
		connInfo.setKeyStore(parameter.getIBMWebSphereParameter().getKeyStorePath());
		connInfo.setTrustStore(parameter.getIBMWebSphereParameter().getTrustStorePath());
		connInfo.setKeyStorePW(parameter.getIBMWebSphereParameter().getKeyStorePassword());
		connInfo.setTrustStorePW(parameter.getIBMWebSphereParameter().getTrustStorePassword());
    	
    	return connInfo;
    }

    /**
     * 采集开始
     * @param ele
     */
    private String collectAll(WasConnInfo connInfo) {
    	StringBuffer result = new StringBuffer();
        ac = getAdminClient(connInfo, CLIENT_TYPE_BASE);
        if(CLIENT_TYPE_ND.equals(connInfo.getWasServerType())) {
        	ndClient = getAdminClient(connInfo, CLIENT_TYPE_ND);
        }
        confCollector = new ConfigCollector(ac, ndClient);
        if (null != ac) {
        	String avail = collectAvail(connInfo);
        	if(avail.equals("0"))
        		return result.toString();
        	
            getObjectNames();
            boolean v6 = !(new Boolean(System.getProperty("websphereV5Statistics"))).booleanValue();
            if (v6) {
            	String ip = connInfo.getIp();
            	String server = serverParse();
                String jvm = jvmParse();// 1.所有的JVM性能指标，支持监测JVM堆栈大小、JVM堆栈利用率等
                String jta = jtaParse();// 2.所有的JTA性能指标，支持监测资源错误导致回滚的事务数、系统错误导致回滚的事务数、应用程序导致回滚的事务数、全部的回滚事务比例等
                String jdbc = jdbcParse();// 3. 所有JDBC Pool监测：支持监测活动连接数、等待连接数等(有2级)
                String thread = threadPoolParse();// 4. 所有Thread Pool监测：支持监测总共线程数、空闲线程数、运行线程数、等待线程数等(有2级)
                String ejb = ejbParse();// 5. 所有EJB监测：支持监测EJB实例数目、响应时间、缓存大小等 (X)
                String jms = jmsParse();// 6.所有JMS监测：支持监测JMS消息服务状态、消息队列大小等 (X)
                String webapp = webAppParse();// 7.所有WEB应用：支持监测WEB应用工作状态、WEB请求并发数、Servlet响应时间等(有2级)
                result.append(server).append(jvm).append(jta).append(jdbc).append(thread)
                	.append(ejb).append(jms).append(webapp).append("ip=" + ip + "\n");
                System.out.println(result);
            }

        } 
        return result.toString();
    }

    public String collectAvail(WasConnInfo connInfo) {
    	try {
    		ac = getAdminClient(connInfo, CLIENT_TYPE_BASE);
    		String avail = ac.getAttribute(ac.getServerMBean(), "state").toString();
    		return "STARTED".equals(avail) ? "1" : "0"; 
    	} catch(Exception e) {
    		if (logger.isErrorEnabled()){
    			logger.error("Fail to collect availibility", e);
    		}
    	}
    	return "0";
    }
    
    /**
     * get AdminClient using the given host, port, and connector
     */
    public AdminClient getAdminClient(WasConnInfo connInfo, String clientType) {
        try {
        	logger.info(connInfo);
        	if (CLIENT_TYPE_ND.equals(clientType)) {
        		ndClient = ndClientMap.get(connInfo);
        		if(ndClient==null) {
        			ndClient = AdminClientFactory.createAdminClient(connInfo.toNDProperties());
        			ndClientMap.put(connInfo, ndClient);
        		}
        		return ndClient;
        	} else {
        		ac = baseClientMap.get(connInfo);
        		if(ac == null) {
        			ac = AdminClientFactory.createAdminClient(connInfo.toBaseProperties());
        			baseClientMap.put(connInfo, ac);
        		}
        		return ac;
        	}
        } catch (Exception ex) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to getAdminClient", ex);
        	}
        }
        return null;
    }

    /**
     * get all the ObjectNames.
     */
   
	public void getObjectNames() {
        try {
            // Get a list of object names
            javax.management.ObjectName on = new javax.management.ObjectName("WebSphere:*");

            // get all objectnames for this server
            Set objectNameSet = ac.queryNames(on, null);

            if (objectNameSet != null) {
                Iterator i = objectNameSet.iterator();
                String onType = "";
                while (i.hasNext()) {
                    on = (ObjectName) i.next();
                    onType = on.getKeyProperty("type");
                    // find the MBeans we are interested
                    if (onType != null && onType.equals("Perf")) {
                        perfOName = on;
                    }
                    if (onType != null && onType.equals("JVM")) {
                        jvmOName = on;
                    }
                    if (onType != null && onType.equals("EJBContainer")) {
                        ejbOName = on;
                    }
//                    if (onType != null && onType.equals("JDBCDriver")) {
//                    }
//                    if (onType != null && onType.equals("JMSProvider")) {
//                    }
                    if (onType != null && onType.equals("TransactionService")) {
                        jtaOName = on;
                    }
                }
            } else {
                if (logger.isErrorEnabled()){
                	logger.error("main: ERROR: no object names found");
                }
            }
        } catch (Exception ex) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to getObjectNames", ex);
        	}
        }

    }

    private String serverParse() {
    	StringBuffer result = new StringBuffer();

    	try {
    
			String pid = ac.getAttribute(ac.getServerMBean(), "pid").toString();
			result.append("pid=" + pid + "\n");
			
			String serverName = ac.getAttribute(ac.getServerMBean(), "name").toString();
    		result.append("serverName=" + serverName + "\n");
    		
    		String version = ac.getServerMBean().getKeyProperty("version");
    		result.append("version=" + version + "\n");
    		
    		String nodeName = ac.getAttribute(ac.getServerMBean(), "nodeName").toString();
    		result.append("nodeName=" + nodeName + "\n");
    		
    		String cellName = ac.getAttribute(ac.getServerMBean(), "cellName").toString();
    		result.append("cellName=" + cellName + "\n");
    		
    		String port = "0";
    		List<Map> portConf = confCollector.execute("NamedEndPoint", "NamedEndPoint[endPointName]", "WC_defaulthost");
    		if(portConf.size() > 0) {
    			port = portConf.get(0).get("NamedEndPoint.endPoint[port]").toString();
    		}
    		result.append("httpPort=" + port + "\n");  		
		} catch (Exception e) {
			if (logger.isErrorEnabled()){
        		logger.error("Fail to parse server", e);
        	}
		}
    	return result.toString();
    }
    
    /**
     * 1.所有的JVM性能指标，支持监测JVM堆栈大小、JVM堆栈利用率等
     */
    private String jvmParse() {
        StringBuilder result = new StringBuilder();
        try {
            Object[] params;
            String[] signature;

            if (logger.isInfoEnabled()){
            	logger.info("\nGet JVM statistics using JVM MBean\n-----------------------------------");
            }
            signature = new String[] { "javax.management.ObjectName", "java.lang.Boolean" };
            params = new Object[] { jvmOName, new Boolean(false) };
            WSStats jvmStats = (WSStats) ac.invoke(perfOName, "getStatsObject", params, signature);

            long heapSize = 0;
            long freeMemory = 0;
            long usedMemory = 0;
            long upTime = 0;

            // JVM堆栈大小
            WSRangeStatistic jvmHeapStatistic = (WSRangeStatistic) jvmStats.getStatistic(WSJVMStats.HeapSize);
            if (null != jvmHeapStatistic) {
                heapSize = jvmHeapStatistic.getCurrent();
            }

            WSCountStatistic freeMemoryStatistic = (WSCountStatistic) jvmStats.getStatistic(WSJVMStats.FreeMemory);
            if (null != freeMemoryStatistic) {
                freeMemory = freeMemoryStatistic.getCount();
            }

            WSCountStatistic usedMemoryStatistic = (WSCountStatistic) jvmStats.getStatistic(WSJVMStats.UsedMemory);
            if (null != usedMemoryStatistic) {
                usedMemory = usedMemoryStatistic.getCount();
            }

            WSCountStatistic upTimeStatistic = (WSCountStatistic) jvmStats.getStatistic(WSJVMStats.UpTime);
            if (null != upTimeStatistic) {
                upTime = upTimeStatistic.getCount();
            }
            
            result.append("freeMemory=" + freeMemory + "\n");
            result.append("usedMemory=" + usedMemory + "\n");
            result.append("heapSize=" + heapSize + "\n");
            result.append("upTime=" + upTime + "\n");
            double jvmMemRate = 0;
            DecimalFormat df = new DecimalFormat("#.##");
            if(heapSize!=0) {      	
            	jvmMemRate = ((double)usedMemory/(double)heapSize)*100;           	
            }
            result.append("jvmMemRate=" + df.format(jvmMemRate) + "\n");
        } catch (Exception e) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to parse JVM", e);
        	}
        }
        return result.toString();
    }

    /**
     * 2. 所有的JTA性能指标，支持监测资源错误导致回滚的事务数、系统错误导致回滚的事务数、应用程序导致回滚的事务数、全部的回滚事务比例等 TransactionService
     */
    private String jtaParse() {
    	StringBuilder result = new StringBuilder();
        try {
            Object[] params;
            String[] signature;

            // get statistics from all thread pools
            if (logger.isInfoEnabled()){
            	logger.info("\nGet JTA statistics using JTA MBean\n-----------------------------------");
            }
            signature = new String[] { "javax.management.ObjectName", "java.lang.Boolean" };
            params = new Object[] { jtaOName, new Boolean(false) };
            WSStats jtaStats = (WSStats) ac.invoke(perfOName, "getStatsObject", params, signature);
            long activeCount = 0;
            long committedCount = 0;
            long rolledbackCount = 0;

            // 事务总数
            WSCountStatistic activeCountStatistic = (WSCountStatistic) jtaStats.getStatistic(WSJTAStats.ActiveCount);
            if (null != activeCountStatistic) {
                activeCount = activeCountStatistic.getCount();
            }

            // 事务提交总数
            WSCountStatistic committedCountStatistic = (WSCountStatistic) jtaStats
                    .getStatistic(WSJTAStats.CommittedCount);
            if (null != committedCountStatistic) {
                committedCount = committedCountStatistic.getCount();
            }

            // 事务回滚总数
            WSCountStatistic rolledbackCountStatistic = (WSCountStatistic) jtaStats
                    .getStatistic(WSJTAStats.RolledbackCount);
            if (null != rolledbackCountStatistic) {
                rolledbackCount = rolledbackCountStatistic.getCount();
            }
            result.append("activeCount="+activeCount+"\n");
            result.append("committedCount="+committedCount+"\n");
            result.append("rolledbackCount="+rolledbackCount+"\n");
        } catch (Exception e) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to parse JTA", e);
        	}
        }
        return result.toString();
    }

    /**
     * 3. 所有JDBC Pool监测：支持监测活动连接数、等待连接数等(有2级) DataSource
     */
    private String jdbcParse() {
    	StringBuffer result = new StringBuffer();
        try {
            Object[] params;
            String[] signature;
            com.ibm.websphere.pmi.stat.WSStats[] wsStats = null;
            // get statistics from all thread pools
            if (logger.isInfoEnabled()){
            	logger.info("\nGet JDBC statistics using JDBC MBean\n-----------------------------------");
            }
            StatDescriptor jdbcSD = new StatDescriptor(new String[] { WSJDBCConnectionPoolStats.NAME });
            signature = new String[] { "[Lcom.ibm.websphere.pmi.stat.StatDescriptor;", "java.lang.Boolean" };
            params = new Object[] { new StatDescriptor[] { jdbcSD }, new Boolean(true) };
            wsStats = (com.ibm.websphere.pmi.stat.WSStats[]) ac.invoke(perfOName, "getStatsArray", params, signature);
            WSStats jdbcStats = wsStats[0];
            
            WSStats[] subJdbcArr = jdbcStats.getSubStats();
            
            List<Map> jdbcConfig = confCollector.execute("DataSource", null, null);
            ObjectName dsConfHelper = getDSConfHelper();
            
            for(Map conf : jdbcConfig) {
            	String id = conf.get("DataSource[_Websphere_Config_Data_Id]").toString();
            	String jndiName = conf.get("DataSource[jndiName]").toString();
            	String providerName = conf.get("DataSource[PARENT_NAME]").toString();
            	
            	Object avail;
                try {
                	avail = ac.invoke(dsConfHelper, "testConnection",  new String[] {id}, new String[] { "java.lang.String" });
                } catch (Throwable t_e) {
                	avail = "-1";
                }
                avail = "0".equals(avail.toString()) ? 1 : 0;

                long poolSize = 0;
                long freePoolSize = 0;
                long useTime = 0;
                long waitTime = 0;
                long waitingThreadCount = 0;
                String statName = "";
                if (null != subJdbcArr && 0 < subJdbcArr.length) {
                    for (int i = 0; i < subJdbcArr.length; i++) {
                        WSStats subJdbcStats = subJdbcArr[i];
                        statName = subJdbcStats.getName();
                        if(!providerName.equals(statName))
                        	break;

                        // 监测活动连接数，总数
                        WSBoundedRangeStatistic freePpoolSizeStatistic = (WSBoundedRangeStatistic) subJdbcStats
                                .getStatistic(WSJDBCConnectionPoolStats.PoolSize);
                        if (null != freePpoolSizeStatistic) {
                            poolSize = freePpoolSizeStatistic.getCurrent();
                        }

                        // 监测活动连接数，可用数
                        WSBoundedRangeStatistic freePoolSizeStatistic = (WSBoundedRangeStatistic) subJdbcStats
                                .getStatistic(WSJDBCConnectionPoolStats.FreePoolSize);
                        if (null != freePoolSizeStatistic) {
                            freePoolSize = freePoolSizeStatistic.getCurrent();
                        }

                        // 使用时间
                        WSAverageStatistic useTimeStatistic = (WSAverageStatistic) subJdbcStats
                                .getStatistic(WSJDBCConnectionPoolStats.UseTime);
                        if (null != useTimeStatistic) {
                            useTime = useTimeStatistic.getCount();
                        }

                        // 等待时间
                        WSAverageStatistic waitTimeStatistic = (WSAverageStatistic) subJdbcStats
                                .getStatistic(WSJDBCConnectionPoolStats.WaitTime);
                        if (null != waitTimeStatistic) {
                            waitTime = waitTimeStatistic.getCount();
                        }

                        // 等待连接数
                        WSRangeStatistic waitingThreadCountStatistic = (WSRangeStatistic) subJdbcStats
                                .getStatistic(WSJDBCConnectionPoolStats.WaitingThreadCount);
                        if (null != waitingThreadCountStatistic) {
                            waitingThreadCount = waitingThreadCountStatistic.getCurrent();
                        }
                        
                    }
                }
                
                result.append("jdbcpool " + DigestUtils.md5Hex(id) + "\t" + avail + "\t" + jndiName + "\t" 
                		+ providerName + "\t" + poolSize + "\t" + freePoolSize + "\t" + useTime + "\t" 
                		+ waitTime + "\t" + waitingThreadCount + "\n");
            }

        } catch (Exception e) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to parse JDBC", e);
        	}
        }
        return result.toString();
    }
    
    /**
     * 获取JDBC数据源可用性测试对象
     * @return
     */
    private ObjectName getDSConfHelper() {
		try {

	    	ObjectName t_queryName = new ObjectName("WebSphere:type=DataSourceCfgHelper,*");
	        Set<?> t_set = ac.queryNames(t_queryName, null);
	        Iterator<?> t_it = t_set.iterator();
	        while (t_it.hasNext()) {
	            // use the first MBean that is found
	            return (ObjectName) t_it.next();
	        }
		} catch (Exception e) {
			if (logger.isErrorEnabled()){
        		logger.error("Fail to getDSConfHelper", e);
        	}
		} 
        return null;
    }

    /**
     * 4. 所有Thread Pool监测：支持监测总共线程数、空闲线程数、运行线程数、等待线程数等(有2级)
     */
    public String threadPoolParse() {
    	StringBuffer result = new StringBuffer();
        try {
        	List<Map> threadConf = confCollector.execute("ThreadPool", "ThreadPool[name]", "WebContainer");
        	if(threadConf.size()>0) {
        		result.append("ThreadMaxSize=" + threadConf.get(0).get("ThreadPool[maximumSize]") + "\n");
        	}
        	
            Object[] params;
            String[] signature;
            com.ibm.websphere.pmi.stat.WSStats[] wsStats = null;

            // get statistics from all thread pools
            if (logger.isInfoEnabled()){
            	logger.info("\nGet statistics from all thread pools:\n-----------------------------------");
            }
            StatDescriptor threadPoolSD = new StatDescriptor(new String[] { WSThreadPoolStats.NAME });
            signature = new String[] { "[Lcom.ibm.websphere.pmi.stat.StatDescriptor;", "java.lang.Boolean" };
            params = new Object[] { new StatDescriptor[] { threadPoolSD }, new Boolean(true) };

            wsStats = (com.ibm.websphere.pmi.stat.WSStats[]) ac.invoke(perfOName, "getStatsArray", params, signature);
            com.ibm.websphere.pmi.stat.WSStats wssArr = wsStats[0];

            // // 所有Thread Pool监测：支持监测总共线程数、空闲线程数、运行线程数、等待线程数等(有2级)
            // // 线程池总的统计
            // // 运行线程数
             WSRangeStatistic activeCountStatistict = (WSRangeStatistic) wsStats[0].getStatistic(WSThreadPoolStats.ActiveCount);
             long activeCount = 0;
             if(activeCountStatistict!=null) {
            	activeCount = activeCountStatistict.getCurrent();
             }
             result.append("ActiveThreadCount=" + activeCount + "\n");
            // long activeCountt = activeCountStatistict.getCurrent();
            // // 总共线程数
            //WSRangeStatistic poolSizeStatistict = (WSRangeStatistic) wsStats[0].getStatistic(WSThreadPoolStats.PoolSize);
            // long poolSizet = poolSizeStatistict.getCurrent();
            // // 空闲线程数(取不到，只能：总的-运行的)
            // long freePoolSize = poolSizet - activeCountt;
            // // 等待线程数(6.0取不到，没此指标；7.0未知)
            //
            // System.out.println("总计：" + wsStats[0].getName() + " activeCount:"
            // + activeCountt + " poolSize:" + poolSizet
            // + " freePoolSize:" + freePoolSize);
             //线程池使用百分比
             WSRangeStatistic poolUsedRateStatistic = (WSRangeStatistic)wsStats[0].getStatistic("PercentMaxed");
             long percentMaxed = 0;
             if(poolUsedRateStatistic!=null) {
            	 percentMaxed = poolUsedRateStatistic.getCurrent();
             }
             result.append("PoolUsedRate=" + percentMaxed + "\n");
            // 线程池详细子统计
            WSStats[] subwsArr = wssArr.getSubStats();
            String statName = "";
            if (null != subwsArr && 0 < subwsArr.length) {
                for (int i = 0; i < subwsArr.length; i++) {
                    WSStats wss = subwsArr[i];
                    statName = wss.getName();
                    activeCount = 0;
                    long poolSize = 0;

                    String id = DigestUtils.md5Hex(statName);
                    WSRangeStatistic activeCountStatistic = (WSRangeStatistic) wss
                            .getStatistic(WSThreadPoolStats.ActiveCount);
                    if (null != activeCountStatistic) {
                        activeCount = activeCountStatistic.getCurrent();
                    }
                    WSRangeStatistic poolSizeStatistic = (WSRangeStatistic) wss
                            .getStatistic(WSThreadPoolStats.PoolSize);
                    if (null != poolSizeStatistic) {
                        poolSize = poolSizeStatistic.getCurrent();
                    }

                    long freePoolSize = poolSize - activeCount;
                    result.append("threadpool " + id + "\t" + statName + "\t" + poolSize + "\t" + activeCount + "\t" + freePoolSize + "\n");
                }
            }

        } catch (Exception e) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to parse threadPool", e);
        	}
        }
        return result.toString();
    }

    /**
     * 5.所有EJB监测：支持监测EJB实例数目、响应时间、缓存大小等
     */
    private String ejbParse() {
    	StringBuilder result = new StringBuilder();
        try {
            Object[] params;
            String[] signature;

            // get statistics from all thread pools
            if (logger.isInfoEnabled()){
            	logger.info("\nGet EJB statistics using EJB MBean\n-----------------------------------");
            }
            signature = new String[] { "javax.management.ObjectName", "java.lang.Boolean" };
            params = new Object[] { ejbOName, new Boolean(false) };
            WSStats ejbStats = (WSStats) ac.invoke(perfOName, "getStatsObject", params, signature);
            long instantiateCount = 0;
            long methodResponseTime = 0;
            long storeCount = 0;

            if (null != ejbStats) {
                // EJB实例数目
                WSCountStatistic instantiateCountStatistic = (WSCountStatistic) ejbStats
                        .getStatistic(WSEJBStats.InstantiateCount);
                if (null != instantiateCountStatistic) {
                    instantiateCount = instantiateCountStatistic.getCount();

                }

                // 响应时间
                WSCountStatistic methodResponseTimeStatistic = (WSCountStatistic) ejbStats
                        .getStatistic(WSEJBStats.MethodResponseTime);
                if (null != methodResponseTimeStatistic) {
                    methodResponseTime = methodResponseTimeStatistic.getCount();

                }

                // 缓存大小
                WSCountStatistic storeCountStatistic = (WSCountStatistic) ejbStats.getStatistic(WSEJBStats.StoreCount);
                if (null != storeCountStatistic) {
                    storeCount = storeCountStatistic.getCount();
                }
            }
            result.append("instantiateCount="+instantiateCount+"\n");
            result.append("methodResponseTime="+methodResponseTime+"\n");
            result.append("storeCount="+storeCount+"\n");
        } catch (Exception e) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to parse EJB", e);
        	}
        }
        return result.toString();
    }

    /**
     * 6.所有JMS监测：支持监测JMS消息服务状态、消息队列大小等
     */
    private String jmsParse() {
    	StringBuffer result = new StringBuffer();
//        sibJmsParse();
        String jcaResult = jcaJmsParse();
        result.append(jcaResult);
        return result.toString();
    }

    /**
     * JMS的SIB指标解析(暂未通过JMX实现)
     * @param wsi
     */
//    private void sibJmsParse(WebsphereSvrInfo wsi, int[] cleanIds) {
//
//        Object[] params;
//        String[] signature;
//        com.ibm.websphere.pmi.stat.WSStats[] wsStats = null;
//        // logger.error("\nGet sib jms \n--------------------");
//        signature = new String[] { "[Lcom.ibm.websphere.pmi.stat.StatDescriptor;", "java.lang.Boolean" };
//    }

    /**
     * JMS的JCA指标解析
     * @param wsi
     */
    private String jcaJmsParse() {
    	StringBuffer result = new StringBuffer();
        try {
            Object[] params;
            String[] signature;
            com.ibm.websphere.pmi.stat.WSStats[] wsStats = null;
            // logger.error("\nGet jca jms \n--------------------");
            StatDescriptor threadPoolSD = new StatDescriptor(new String[] { WSJCAConnectionPoolStats.NAME });
            params = new Object[] { new StatDescriptor[] { threadPoolSD }, new Boolean(true) };
            signature = new String[] { "[Lcom.ibm.websphere.pmi.stat.StatDescriptor;", "java.lang.Boolean" };
            wsStats = (com.ibm.websphere.pmi.stat.WSStats[]) ac.invoke(perfOName, "getStatsArray", params, signature);
            WSStats jmsAppStats = wsStats[0];
            if(jmsAppStats==null) {
            	return "";
            }
            WSStats[] subAppArr = jmsAppStats.getSubStats();
            String statName = "";
            for (int i = 0; i < subAppArr.length; i++) {
                WSStats wsjmsStats = subAppArr[i];
                for (int j = 0; j < wsjmsStats.getSubStats().length; j++) {
                    WSStats subAppStatsObj = wsjmsStats.getSubStats()[j];
                    statName = subAppStatsObj.getName();
                    long createCount = -1;
                    long closeCount = -1;
                    long allocateCount = -1;
                    long freedCount = -1;
                    long waitingThreadCount = -1;
                    WSCountStatistic createCountStatistic = (WSCountStatistic) subAppStatsObj
                            .getStatistic(WSJCAConnectionPoolStats.CreateCount);
                    WSCountStatistic closeCountStatistic = (WSCountStatistic) subAppStatsObj
                            .getStatistic(WSJCAConnectionPoolStats.CloseCount);
                    WSCountStatistic allocateCountStatistic = (WSCountStatistic) subAppStatsObj
                            .getStatistic(WSJCAConnectionPoolStats.AllocateCount);
                    WSCountStatistic freeCountStatistic = (WSCountStatistic) subAppStatsObj
                            .getStatistic(WSJCAConnectionPoolStats.FreedCount);
                    WSRangeStatistic wtRangeStatistic = (WSRangeStatistic) subAppStatsObj
                            .getStatistic(WSJCAConnectionPoolStats.WaitingThreadCount);
                    WSBoundedRangeStatistic fpCountStatistic = (WSBoundedRangeStatistic) subAppStatsObj
                            .getStatistic(WSJCAConnectionPoolStats.FreePoolSize);
                    WSBoundedRangeStatistic poolCountStatistic = (WSBoundedRangeStatistic) subAppStatsObj
                            .getStatistic(WSJCAConnectionPoolStats.PoolSize);
                    if (null != createCountStatistic) {
                        createCount = createCountStatistic.getCount();
                    }
                    if (null != closeCountStatistic) {
                        closeCount = closeCountStatistic.getCount();
                    }
                    if (null != allocateCountStatistic) {
                        allocateCount = allocateCountStatistic.getCount();
                    }
                    if (null != freeCountStatistic) {
                        freedCount = freeCountStatistic.getCount();
                    }
                    if (null != wtRangeStatistic) {
                        waitingThreadCount = wtRangeStatistic.getCurrent();
                    }
                    if (null != fpCountStatistic) {
                        fpCountStatistic.getCurrent();
                    }
                    if (null != poolCountStatistic) {
                        poolCountStatistic.getCurrent();
                    }
                    
                    result.append("jmsData " + statName + "\t" + createCount + "\t" + closeCount + "\t" +
                    			allocateCount + "\t" + freedCount + "\t" + waitingThreadCount + "\n");
                }
            }
        } catch (Exception e) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to parse JCA", e);
        	}
        }
        return result.toString();
    }

    /**
     * 7. 所有WEB应用：支持监测WEB应用工作状态、WEB请求并发数、Servlet响应时间等(有2级)
     */
    private String webAppParse() {
    	StringBuffer result = new StringBuffer();
        try {
            Object[] params;
            String[] signature;
            com.ibm.websphere.pmi.stat.WSStats[] wsStats = null;

            // get statistics from all thread pools
            if (logger.isInfoEnabled()){
            	logger.info("\nGet webApp statistics using webApp MBean\n-----------------------------------");
            }
            StatDescriptor webAppSD = new StatDescriptor(new String[] { WSWebAppStats.NAME });
            signature = new String[] { "[Lcom.ibm.websphere.pmi.stat.StatDescriptor;", "java.lang.Boolean" };
            params = new Object[] { new StatDescriptor[] { webAppSD }, new Boolean(true) };
            wsStats = (com.ibm.websphere.pmi.stat.WSStats[]) ac.invoke(perfOName, "getStatsArray", params, signature);
            WSStats webAppStats = wsStats[0];
            if (webAppStats == null){
            	return result.toString();
            }

            // WEB请求并发数
            // WSCountStatistic totalRequestCountStatistic = (WSCountStatistic) webAppStats
            // .getStatistic(WSWebAppStats.ServletStats.RequestCount);
            // long totalRequestCount = totalRequestCountStatistic.getCount();
            // System.out.println("   totalRequestCount:" + totalRequestCount
            // + " " + totalRequestCountStatistic.getUnit());

            WSStats[] subAppArr = webAppStats.getSubStats();
            String statName = "";
            if (null != subAppArr && 0 < subAppArr.length) {
                for (int i = 0; i < subAppArr.length; i++) {
                    WSStats subAppStats = subAppArr[i];
                    // 统计类名称(工作状态名称)
                    WSStats subAppStatsObj = subAppStats.getSubStats()[0];
                    statName = subAppStats.getName();
                    long requestCount = 0;
                    long serviceTime = 0;
                    String appName = "";
                    
                    int index = statName.indexOf("#");
                    if(index!=-1) {
                    	appName = statName.substring(index+1);
                    }
                    
                    String id = DigestUtils.md5Hex(statName);

                    // WEB请求并发数
                    WSCountStatistic requestCountStatistic = (WSCountStatistic) subAppStatsObj
                            .getStatistic(WSWebAppStats.ServletStats.RequestCount);
                    if (null != requestCountStatistic) {
                        requestCount = requestCountStatistic.getCount();
                    }

                    // Servlet响应时间
                    WSAverageStatistic ServiceTimeStatistic = (WSAverageStatistic) webAppStats
                            .getStatistic(WSWebAppStats.ServletStats.ServiceTime);
                    if (null != ServiceTimeStatistic) {
                        serviceTime = ServiceTimeStatistic.getCount();
                    }
                    
                    result.append("webappData " + id+"\t"+appName+"\t"+statName+"\t"+requestCount+"\t"+serviceTime+"\n");
                }
            }
        } catch (Exception e) {
        	if (logger.isErrorEnabled()){
        		logger.error("Fail to parse WebApp", e);
        	}
        }
        return result.toString();
    }
    
    public boolean checkConnectInfo(WasConnInfo connInfo) throws AttributeNotFoundException, 
    				InstanceNotFoundException, MBeanException, ReflectionException, ConnectorException {
    	ac = getAdminClient(connInfo, CLIENT_TYPE_BASE);
		String avail = ac.getAttribute(ac.getServerMBean(), "state").toString();
		return avail.equalsIgnoreCase("STARTED");
	}
}
