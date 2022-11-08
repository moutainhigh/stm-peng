package com.mainsteam.stm.plugin.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.intersys.classes.RegisteredObject;
import com.intersys.objects.CacheException;
import com.intersys.objects.Database;
import com.intersys.objects.reflect.CacheClass;
import com.intersys.objects.reflect.CacheMethod;

public class CacheCollector {

	private static final Log logger = LogFactory.getLog(CacheCollector.class);
	
	public String getDBBlockSize(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "BlockSize");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBBlockFormat(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "BlockFormat");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBBlocks(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "Blocks");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBBlocksPerMap(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "BlocksPerMap");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBClusterMounted(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "ClusterMounted");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBCurrentMaps(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "CurrentMaps");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBDirectoryBlock(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "DirectoryBlock");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	
	public String getDBEncryptedDB(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "EncryptedDB");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBEncryptionKeyID(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "EncryptionKeyID");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBExpanding(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "Expanding");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBExpansionSize(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "ExpansionSize");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBFull(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "Full");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBMaxSize(Object... one) throws Exception{
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "MaxSize");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBMirrored(Object... one) {
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "Mirrored");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBMirrorDBName(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "MirrorDBName");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBMirrorNoWrite(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "MirrorNoWrite");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBMirrorActivationRequired(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "MirrorActivationRequired");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
		
	}
	
	public String getDBMirrorFailoverDB(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "MirrorFailoverDB");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBInActiveMirror(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "InActiveMirror");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBMounted(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "Mounted");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBNewGlobalGrowthBlock(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "NewGlobalGrowthBlock");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBSize(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "Size");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBVolumes(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "Volumes");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBResourceName(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "ResourceName");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getDBReadOnly(Object... one){
		if(one[0] instanceof Database) {
			return getKPIValueByKey(((Database)one[0]), one[1].toString(), "ReadOnly");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}


	public String getMaxConn(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ReadOnly");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
		
	}
	
	public String getActConn(Object... one){
		
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ActConn");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getByteSent(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ByteSent");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getByteRcvd(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ByteRcvd");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getResponseTime(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ResponseTime");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getECPAppSvrKPIValueByKey(Database dbconnection, String key){
		CacheClass clazz;
		Object obj = "";
		try {
			clazz = dbconnection.getCacheClass("SYS.Stats.ECPAppSvr");
			CacheMethod method = clazz.getMethod("Sample");
			RegisteredObject registObj = (RegisteredObject)method.invoke(null, null);
			obj = registObj.getField(key);
		} catch (CacheException e) {
			if(logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			}
		} 
		
		return obj+"";
	}
	
	
	public String getECPDataSvrMaxConn(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "MaxConn");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getECPDataSvrActConn(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ActConn");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getReqRcvd(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ReqRcvd");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getReqBuff(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ReqBuff");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getECPDataSvrByteSent(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ByteSent");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getECPDataSvrByteRcvd(Object... one){
		if(one[0] instanceof Database) {
			return getECPAppSvrKPIValueByKey(((Database)one[0]), "ByteRcvd");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getSystemUpTime(Object... one){
		if(one[0] instanceof Database) {
			return getSYSStatsDashboardByKey(((Database)one[0]), "SystemUpTime");
			
		}else{
			logger.warn("Cache Collector can not get Database instance.");
			return null;
		}
	}
	
	public String getECPDataSvrKPIValueByKey(Database dbconnection, String key){
		CacheClass clazz;
		Object obj = null;
		try {
			clazz = dbconnection.getCacheClass("SYS.Stats.ECPDataSvr");
			CacheMethod method = clazz.getMethod("Sample");
			RegisteredObject registObj = (RegisteredObject)method.invoke(null, null);
			obj = registObj.getField(key);
		} catch (CacheException e) {
			if(logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			}
		}
		
		if(obj != null)
			return obj.toString();
		else
			return null;
	}
	
	public String getKPIValueByKey(Database dbconnection, String dbName, String key){
		
		Object obj = null;
		try {
			CacheClass clazz =  dbconnection.getCacheClass("SYS.Database");
			Object p = clazz.newInstance(dbName);
			obj = clazz.getField(key).get(p);
		} catch (Exception e) {
			if(logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			}
		}
		if(obj != null)
			return obj.toString();
		else
			return null;
		
	}
	
	public String getSYSStatsDashboardByKey(Database dbconnection, String key){
		CacheClass clazz;
		Object obj = null;
		try {
			clazz = dbconnection.getCacheClass("SYS.Stats.Dashboard");
			CacheMethod method = clazz.getMethod("Sample");
			RegisteredObject registObj = (RegisteredObject)method.invoke(null, null);
			obj = registObj.getField(key);
		} catch (CacheException e) {
			if(logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			}
		}
		
		if(obj != null)
			return obj.toString();
		else
			return null;
	}
	
}
