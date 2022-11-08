package com.mainsteam.stm.resourcelog.util;

import org.apache.log4j.Logger;

import com.adventnet.snmp.mibs.MibModule;
import com.adventnet.snmp.mibs.MibOperations;

public class MibUtil {
	public static final MibOperations mibOps;
	private static MibModule mib;
	
	private static final Logger LOGGER = Logger.getLogger(MibUtil.class);
	static {
		mibOps = new MibOperations();
		 try {
			//此方法利用Adventnet SNMP API类,装载MIB库文件
			mib = mibOps.loadMibModule("RFC1213-MIB.mib");					
		} catch (Exception e) {
			LOGGER.debug("装载RFC1213-MIB.mib库文件失败", e);
		} 
	}

	
	//通过IsmName查询OID
	public static String getOID(String mibName) throws Exception {
		return mib.getMibNode(mibName).getNumberedOIDString();
	}
	
	//通过OID查询IsmName
	public static String getIsmName(String oid)  {
		/*//此方法利用Adventnet SNMP  API的MibTree等类，装载MIB模型，显示MIB树
		MibOperations mibOps = new MibOperations();
		MibModule mib = mibOps.loadMibModule("ism3602.mib");//进行编译的MIB库*/			
		String name = "";
		try 
		{
			 name = mib.getMibNode(oid).toString();
		}
		
		catch (Exception e)
		{
			name = "此MID库不能解析的OID";
		}
		return name;
	}
	
	public static void main(String args[])
	{
		try
		{
		  System.out.println(MibUtil.getIsmName(".1.3.6.1.2.1.2.2.1.1.2"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
