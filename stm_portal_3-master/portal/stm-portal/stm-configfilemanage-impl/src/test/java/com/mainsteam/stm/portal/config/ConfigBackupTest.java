package com.mainsteam.stm.portal.config;

import com.mainsteam.stm.portal.config.collector.mbean.ConfigBackup;
import com.mainsteam.stm.portal.config.collector.mbean.bean.ConfigReq;
import com.mainsteam.stm.portal.config.collector.mbean.bean.ConfigRsp;
import com.mainsteam.stm.portal.config.util.ScriptUtil;
import com.mainsteam.stm.portal.config.util.jaxb.Model;
import com.mainsteam.stm.portal.config.util.jaxb.Script;

public class ConfigBackupTest {
	public static void main(String[] args) {
		ConfigBackup configBackup = new ConfigBackup();
		try {
			//System.out.println(configBackup.getBySnmp("172.16.10.254", 161, "public"));
			Model model = ScriptUtil.getModel("1.3.6.1.4.1.9");
			for(Script script : model.getScripts()){
				ConfigReq req = new ConfigReq();
				req.setIp("172.16.10.254");
				req.setEnablePassword("password");
				req.setEnableUserName("");
				req.setPassword("test");
				req.setUserName("test");
				req.setCmd(script.getCmd());
				req.setFileName(script.getFileName());
				ConfigRsp rsp = configBackup.getByTelnet(req);
				System.out.println(rsp.getFile());
				System.out.println(rsp.getRemoteInfo());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
