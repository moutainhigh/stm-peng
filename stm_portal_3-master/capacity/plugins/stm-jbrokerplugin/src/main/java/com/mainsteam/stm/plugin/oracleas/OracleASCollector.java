package com.mainsteam.stm.plugin.oracleas;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.j2ee.statistics.BoundedRangeStatistic;
import javax.management.j2ee.statistics.CountStatistic;
import javax.management.j2ee.statistics.RangeStatistic;
import javax.management.j2ee.statistics.Stats;
import javax.management.remote.JMXConnector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oracle.oc4j.admin.management.shared.statistic.JTAStatsImpl;
import oracle.oc4j.sql.mbean.JDBCConnectionPoolStatsImpl;


public class OracleASCollector {
	private final static Log log = LogFactory.getLog(OracleASCollector.class);
	private String oc4jInstanceName;
	private MBeanServerConnection connection;
	private MBeanServerConnection clusterConnection;
	
	private OracleASCollector(String oc4jInstanceName,MBeanServerConnection connection,MBeanServerConnection clusterConnection) {
		this.oc4jInstanceName = oc4jInstanceName;
		this.connection=connection;
		this.clusterConnection=clusterConnection;
	}
	
	public static OracleASCollector getInstance(String oc4jInstance,JMXConnector connection,JMXConnector clusterConnection) {
		MBeanServerConnection conn=null;
		MBeanServerConnection clusterConn=null;
		try {
			 conn=connection.getMBeanServerConnection();
			 clusterConn=clusterConnection.getMBeanServerConnection();
		} catch (IOException e) {
			log.error("get MBeanServerConnection from JMXConnector Fail ",e);
		}	
		OracleASCollector coll = new OracleASCollector(oc4jInstance,conn,clusterConn);
		return coll;
	}
	
	/**
	 * Oracle AS名称
	 * @return
	 */
	public String getServerName() {
		try {
			return getJMXAttr("ias:j2eeType=J2EEServer,*,J2EEServerGroup="+oc4jInstanceName, "instanceName", "cluster").toString();
		} catch (Exception e) {
			log.error("getServerName failed!", e);
			return "";
		} 
	}
	
	/**
	 * 安装路径
	 * @return
	 */
	public String getInstallDirectory() {
		try {
			return getJMXAttr("ias:j2eeType=J2EEServer,*", "oracleHome", "cluster").toString();
		} catch (Exception e) {
			log.error("getInstallDirectory failed!", e);
			return "";
		} 
	}
	
	/**
	 * 版本
	 * @return
	 */
	public String getServerVersion() {
		try {
			return getJMXAttr("ias:j2eeType=J2EEServer,*", "serverVersion", "cluster").toString();
		} catch (Exception e) {
			log.error("getServerVersion failed!", e);
			return "";
		} 
	}
	
	/**
	 * 服务器可用性
	 * @return
	 */
	public String getAvailability() {
		try {
			return getJMXAttr("ias:j2eeType=J2EEServer,*", "state", "cluster").toString();
		} catch (Exception e) {
			log.error("getAvailability failed!", e);
			return "";
		} 
	}
	
	/**
	 * Oracle AS JVM内存利用率
	 * @return
	 */
	public float getJvmMemRate() {
		try {
			long totalMem = (Long)getJMXAttr("oc4j:j2eeType=JVM,name=single,J2EEServer=standalone", "totalMemory", oc4jInstanceName);
			long freeMemory = (Long)getJMXAttr("oc4j:j2eeType=JVM,name=single,J2EEServer=standalone", "freeMemory", oc4jInstanceName);
			float tmp = ((float)(totalMem-freeMemory))/totalMem*100;
			BigDecimal bd = new BigDecimal(tmp);
			tmp = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			return tmp;
		}catch(Exception ex) {
			log.error("getJvmMemRate failed!", ex);
			return 0f;
		}
	}
	
//	public String getOc4jInstances() {
//		try {
//			String objName = "ias:j2eeType=J2EEServerGroup,*";
//			List<Object> names = getJMXAttrList(objName, "objectName", "cluster");
//			StringBuilder result = new StringBuilder();
//			for(Object obj : names) {
//				String objectName = obj.toString();
//				String insName = objectName.substring(objectName.indexOf("name=") + 5);
//				result.append(insName + ",");
//			}
//			if(result.length()!=0) {
//				result.deleteCharAt(result.length() - 1);
//			}
//			return result.toString();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return "";
//		}
//	}
	
//	
//	
//	public float getHeapUtilRatio(String instanceName) {
//		String objName = "oc4j:j2eeType=JVM,*" + instanceName;
//		long totalMem = (Long)getJMXAttr(objName, "totalMemory");
//		long freeMemory = (Long)getJMXAttr(objName, "freeMemory");
//		long usedMem = totalMem - freeMemory;
//		return ((float)usedMem)/1000000;
//	}
	
//	/**
//	 * JMS活动连接数
//	 * @return
//	 */
//	public long getActiveConnCount() {
//		try {
//			String objName = "oc4j:j2eeType=JMSResource,*";
//			Stats stats = (Stats) getJMXAttr(objName, "stats", "home");
//			RangeStatistic rs = (RangeStatistic) stats.getStatistic("activeConnections");
//			return rs.getCurrent();
//		}catch(Exception ex) {
//			ex.printStackTrace();
//			return 0;
//		}
//	}
	
	public float getCpuRate() {
		
		return 0;
	}
	
	/**
	 * 空闲的JDBC连接数
	 * @param oc4jInstanceName
	 * @return
	 */
	public long getFreeJDBCConnCnt() {
		try {
			String objName = "oc4j:j2eeType=JDBCResource,*";
			
			List<Object> attrs = getJMXAttrList(objName, "stats", oc4jInstanceName);
			long cnt = 0;
			for(Object obj : attrs) {
				Stats stats = (Stats)obj;
				BoundedRangeStatistic brs = (BoundedRangeStatistic) stats.getStatistic("FreePoolSize");
				cnt += brs.getCurrent();
			}
			return cnt;
			
		}catch(Exception ex) {
			log.error("getFreeJDBCConnCnt failed!", ex);
			return 0;
		}
	}
	
	/**
	 * 打开的JDBC连接数
	 * @param oc4jInstanceName
	 * @return
	 */
	public long getOpendJDBCConnCnt() {
		try {
			String objName = "oc4j:j2eeType=JDBCResource,*";
			
			List<Object> attrs = getJMXAttrList(objName, "stats", oc4jInstanceName);
			long cnt = 0;
			for(Object obj : attrs) {
				BoundedRangeStatistic total = (BoundedRangeStatistic) ((Stats)obj).getStatistic("PoolSize");
				BoundedRangeStatistic free = (BoundedRangeStatistic) ((Stats)obj).getStatistic("FreePoolSize");
				long tmp = total.getCurrent() - free.getCurrent();
				cnt += tmp;
			}
			return cnt;
			
		}catch(Exception ex) {
			log.error("getOpendJDBCConnCnt failed!", ex);
			return 0;
		}
	}
	
	/**
	 * 总JDBC连接数
	 * @param oc4jInstanceName
	 * @return
	 */
	public long getJDBCConnCnt() {
		try {
			String objName = "oc4j:j2eeType=JDBCResource,*";
			List<Object> attrs = getJMXAttrList(objName, "stats", oc4jInstanceName);
			long cnt = 0;
			for(Object obj : attrs) {
				BoundedRangeStatistic total = (BoundedRangeStatistic) ((Stats)obj).getStatistic("PoolSize");
				cnt += total.getCurrent();
			}
			return cnt;
			
		}catch(Exception ex) {
			log.error("getJDBCConnCnt failed!", ex);
			return 0;
		}
	}
	
	/**
	 * JTA活动事务数
	 * @param oc4jInstanceName
	 * @return
	 */
	public long getActiveTxCnt() {
		try {
			String objName = "oc4j:j2eeType=JTAResource,*";
			List<Object> attrs = getJMXAttrList(objName, "stats", oc4jInstanceName);
			long txCnt = 0;
			for(Object obj : attrs) {
				Stats stats = (Stats) obj;
				CountStatistic brs = (CountStatistic) (stats).getStatistic("ActiveCount");
				txCnt += brs.getCount();
			}
			return txCnt;
		}catch(Exception ex) {
			log.error("getActiveTxCnt failed!", ex);
			return 0;
		}
	}
	
	/**
	 * JTA提交事务数
	 * @param oc4jInstanceName
	 * @return
	 */
	public long getCommitTxCnt() {
		try {
			String objName = "oc4j:j2eeType=JTAResource,*";
			List<Object> attrs = getJMXAttrList(objName, "stats", oc4jInstanceName);
			long cnt = 0;
			for(Object obj : attrs) {
				CountStatistic brs = (CountStatistic) ((Stats)obj).getStatistic("CommittedCount");
				cnt += brs.getCount();
			}
			return cnt;
		}catch(Exception ex) {
			log.error("getCommitTxCnt failed!", ex);
			return 0;
		}
	}
	
	/**
	 * JTA回退事务数
	 * @param oc4jInstanceName
	 * @return
	 */
	public long getRollbackTxCnt() {
		try {
			String objName = "oc4j:j2eeType=JTAResource,*";
			List<Object> attrs = getJMXAttrList(objName, "stats", oc4jInstanceName);
			long cnt = 0;
			for(Object obj : attrs) {
				JTAStatsImpl stats = (JTAStatsImpl) obj;
				CountStatistic brs = (CountStatistic) (stats).getStatistic("RolledbackCount");
				cnt += brs.getCount();
			}
			return cnt;
		}catch(Exception ex) {
			log.error("getRollbackTxCnt failed!", ex);
			return 0;
		}
	}
	
	/**
	 * JMS活动句柄数
	 * @param oc4jInstanceName
	 * @return
	 */
	public long getActiveHandlerCnt() {
		try {
			String objName = "oc4j:j2eeType=JMSResource,*";
			List<Object> attrs = getJMXAttrList(objName, "stats", oc4jInstanceName);
			long cnt = 0;
			for(Object obj : attrs) {
				RangeStatistic brs = (RangeStatistic) ((Stats)obj).getStatistic("activeHandlers");
				cnt += brs.getCurrent();
			}
			return cnt;
		}catch(Exception ex) {
			log.error("getActiveHandlerCnt failed!", ex);
			return 0;
		}
	}
	
	/**
	 * JMS活动连接数
	 * @return
	 */
	public long getActiveConnCnt() {
		try {
			String objName = "oc4j:j2eeType=JMSResource,*";
			List<Object> attrs = getJMXAttrList(objName, "stats", oc4jInstanceName);
			long cnt = 0;
			for(Object obj : attrs) {
				RangeStatistic brs = (RangeStatistic) ((Stats)obj).getStatistic("activeConnections");
				cnt += brs.getCurrent();
			}
			return cnt;
		}catch(Exception ex) {
			log.error("getActiveConnCnt failed!", ex);
			return 0;
		}
	}
	
	/**
	 * OC4J使用内存容量
	 * @param oc4jInstanceName
	 * @return
	 */
	public float getTotalMem() {
		try {
			String objName = "oc4j:j2eeType=JVM,*";
			long mem = (Long)getJMXAttr(objName, "totalMemory", oc4jInstanceName);
			return ((float)mem)/1024/1024;
		}catch(Exception ex) {
			log.error("getTotalMem failed!", ex);
			return 0;
		}
	}
	
	/**
	 * 堆使用量
	 * @param oc4jInstanceName
	 * @return
	 */
	public float getHeapUtilRatio() {
		try {
			String objName = "oc4j:j2eeType=JVM,*";
			long totalMem = (Long)getJMXAttr(objName, "totalMemory", oc4jInstanceName);
			long freeMemory = (Long)getJMXAttr(objName, "freeMemory", oc4jInstanceName);
			long usedMem = totalMem - freeMemory;
			return ((float)usedMem)/1024/1024;
		} catch (Exception ex) {
			log.error("getHeapUtilRatio failed!", ex);
			return 0;
		}
	}
	public boolean check(){
		String check=getOc4jAvailability();
		if(StringUtils.equals(check, "1")){
			return true;
		}
		else {
			return false;
		}
	}
	public String getOc4jAvailability() {
		log.info("oracleas获取可用性方法开始");
		log.info("oracleas instancename-->"+oc4jInstanceName);
		String stateStr;
		String objName = "oc4j:j2eeType=J2EEServer,name=standalone";
		try {
			int state = (Integer) getJMXAttr(objName, "state", oc4jInstanceName);
			log.info("oracleas-availability-state=="+state);
			switch (state) {
			case 0:
				stateStr = "1";
				break;
			case 1:
				stateStr = "1";
				break;
			case 2:
				stateStr = "0";
				break;
			case 3:
				stateStr = "0";
				break;
			default:
				stateStr = "0";
				break;
			}
			return stateStr;
		} catch (Exception ex) {
			log.error("getOc4jAvailability failed!", ex);
			return stateStr = "0";
		}
	}
	
	public String getConnPoolNames() {
		try {
			String objName = "oc4j:j2eeType=JDBCResource,*";
			List<Object> attrs = getJMXAttrList(objName, "connectionPoolName", oc4jInstanceName);
			StringBuilder sb = new StringBuilder();
			for(Object obj : attrs) {
				String str = obj.toString().replaceAll(" ", "_");
				sb.append(str + "\n");
			}
			return sb.toString();
		}catch(Exception ex) {
			log.error("getConnPoolNames failed!", ex);
			return "";
		}
	}
	
	public String getConnPoolSizes() {
		try {
			String objName = "oc4j:j2eeType=JDBCResource,*";
			List<List<Object>> attrs = getJMXAttrList(objName, new String[]{"connectionPoolName", "stats"}, oc4jInstanceName);
			StringBuilder sb = new StringBuilder();
			for(List<Object> row : attrs) {
				String poolName = ((String) row.get(0)).replaceAll(" ", "_");
				JDBCConnectionPoolStatsImpl stats = (JDBCConnectionPoolStatsImpl) row.get(1);
				BoundedRangeStatistic stat = (BoundedRangeStatistic) (stats).getStatistic("PoolSize");
				long poolSize = stat.getCurrent();
				sb.append(poolName + "="+ poolSize + "\n");
			}
			return sb.toString();
		}catch(Exception ex) {
			log.error("getConnPoolSizes failed!", ex);
			return "";
		}
	}
	
	public String getThreedPoolNames() {
		try {
			String objName = "oc4j:j2eeType=ThreadPool,*";
			List<Object> attrs = getJMXAttrList(objName, "objectName", oc4jInstanceName);
			StringBuilder sb = new StringBuilder();
			for(Object obj : attrs) {
				String objStr = obj.toString();
				String poolName = objStr.substring(objStr.indexOf(",name=")+ 6, objStr.indexOf(",J2EEServer"));
				sb.append(poolName + "\n");
			}
			return sb.toString();
		}catch(Exception ex) {
			log.error("getThreedPoolNames failed!", ex);
			return "";
		}
	}
	
	public String getThreedPoolSizes() {
		try {
			String objName = "oc4j:j2eeType=ThreadPool,*";
			List<List<Object>> attrs = getJMXAttrList(objName, new String[]{"objectName", "poolSize"}, oc4jInstanceName);
			StringBuilder sb = new StringBuilder();
			for(List<Object> row : attrs) {
				String objStr = row.get(0).toString();
				String poolName = objStr.substring(objStr.indexOf(",name=")+ 6, objStr.indexOf(",J2EEServer"));
				long poolSize = (Integer)row.get(1);
				sb.append(poolName + "="+ poolSize + "\n");
			}
			return sb.toString();
		}catch(Exception ex) {
			log.error("getThreedPoolNames failed!", ex);
			return "";
		}
	}
	
	public Object getJMXAttr(String objNameStr, String attrName, String domain) throws MalformedURLException, IOException, MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
			MBeanServerConnection conn = null;
			if(domain.equals("cluster")){
				conn=this.clusterConnection;
			}
			else {
				conn=this.connection;
			}
			
			ObjectName objName = new ObjectName(objNameStr);
			Set<ObjectInstance> names = conn.queryMBeans(objName, null);
			if(names.size() == 0) {
				throw new IllegalArgumentException("Can not find MBean: " + objNameStr);
			}
			objName = names.iterator().next().getObjectName();
			return conn.getAttribute(objName, attrName);
	}
	
	public List<List<Object>> getJMXAttrList(String objNameStr, String[] attrNames, String domain) throws MalformedURLException, IOException, MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
		MBeanServerConnection conn =connection;
		ObjectName objName = new ObjectName(objNameStr);
		Set<ObjectInstance> names = conn.queryMBeans(objName, null);
		if(names.size() == 0) {
			throw new IllegalArgumentException("Can not find MBean: " + objNameStr);
		}
//		objName = names.iterator().next().getObjectName();
		List<List<Object>> result = new ArrayList<List<Object>>();
		for(ObjectInstance oi : names) {
			List<Object> row = new ArrayList<Object>();
			for(String attrName : attrNames) {
				Object o = conn.getAttribute(oi.getObjectName(), attrName);
				row.add(o);
			}
			result.add(row);
		}
		return result;
	}

	public List<Object> getJMXAttrList(String objNameStr, String attrName, String domain) throws MalformedURLException, IOException, MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
		List<Object> result = new ArrayList<Object>();
		for(List<Object> row : getJMXAttrList(objNameStr, new String[]{attrName}, domain)) {
			result.add(row.get(0));
		}
		return result;
	}
}
