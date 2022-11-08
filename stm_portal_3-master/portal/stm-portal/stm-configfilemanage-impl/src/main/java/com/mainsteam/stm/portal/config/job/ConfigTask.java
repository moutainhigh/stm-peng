package com.mainsteam.stm.portal.config.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.portal.config.api.IConfigDeviceApi;
import com.mainsteam.stm.portal.config.bo.BackupPlanBo;
import com.mainsteam.stm.portal.config.bo.ConfigDeviceBo;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * <li>文件名称: ConfigTask.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月29日
 * @author   caoyong
 */
public class ConfigTask {
	
	/**
	 * 日志
	 */
	private  final Log logger = LogFactory.getLog(ConfigTask.class);
	private IConfigDeviceApi configDeviceApi;
	
	public ConfigTask(){
	}
	/**
	 * MaBean获取远程bean 执行备份脚本，拿到配置文件上传至文件服务器，
	 * 比较当前备份文件与上次备份文件是否产生变化，产生告警，备份当前记录存入数据表中
	 * @throws Exception
	 */
	public void start(BackupPlanBo plan) throws Exception {
		logger.info("config job start...................");
		configDeviceApi = (IConfigDeviceApi) SpringBeanUtil.getObject("configDeviceApi");
		List<ConfigDeviceBo> configDeviceBos = configDeviceApi.getDeviceByPlanId(plan.getId());
		Long[] ids = new Long[configDeviceBos.size()];
		for(int i=0;i<configDeviceBos.size();i++){
			ids[i] = configDeviceBos.get(i).getId();
		}
		if(ids.length>0){
			configDeviceApi.backupResourcesByIds(ids,false);
		}
		logger.info("config job end...................");
	}
}
