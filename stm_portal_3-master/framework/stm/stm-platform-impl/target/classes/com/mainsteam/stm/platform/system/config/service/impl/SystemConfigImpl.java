package com.mainsteam.stm.platform.system.config.service.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.platform.dict.api.IDictApi;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.dao.ISystemConfigDao;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;

/**
 * <li>文件名称: SystemConfigImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月16日 上午9:18:55
 * @author   俊峰
 */
public class SystemConfigImpl implements ISystemConfigApi {
	private ISystemConfigDao systemConfigDao;
	
	/**缓存KEY名称*/
	private static final Log logger = LogFactory.getLog(SystemConfigImpl.class);
	IMemcache<SystemConfigBo> cache = MemCacheFactory
			.getRemoteMemCache(SystemConfigBo.class);
	public void setSystemConfigDao(ISystemConfigDao systemConfigDao) {
		this.systemConfigDao = systemConfigDao;
	}
	
	@Override
	public int insertSystemConfig(SystemConfigBo config) {
		
		cache.set(String.valueOf(config.getId()),config);
		return systemConfigDao.insert(config);
	}

	@Override
	public SystemConfigBo getSystemConfigById(long id) {
		String idStr=String.valueOf(id);
//		logger.info("getSystemConfigById="+id);
		SystemConfigBo bo=	 cache.get(idStr);
		if(bo==null){
			bo= systemConfigDao.get(id);
			if(bo!=null){
				cache.set(idStr, bo);
			}else{
				if(id==1){//判断如果是告警设置信息，如果查询为空，默认告警抑制期和基本信息
					SystemConfigBo configBo= new SystemConfigBo();	
					configBo.setId(1);
					configBo.setDescription("系统告警设置配置信息");
					configBo.setContent("{'connectCount':'3','manufacturer':'WAVECOM MODEM','model':'MULTIBAND  900E  1800','time':'60','timeOut':'10'}");
					systemConfigDao.insert(configBo);
					cache.set("1", configBo);
				}
			}
//			logger.info("db="+id);2546448746
		}
		return bo;
	}

	@Override
	public int updateSystemConfig(SystemConfigBo config) {
		String idStr=String.valueOf(config.getId());
		if(cache.get(idStr)!=null){
			cache.update(idStr,config);
		}
		return systemConfigDao.update(config);
	}

	@Autowired
	IDictApi dictApi;
	
	@Override
	public void insertTransaction(long id) {
		SystemConfigBo bo=new SystemConfigBo();
		bo.setId(id);
	
		this.insertSystemConfig(bo);
		
		
		try {
			dictApi.insert();
		} catch (Exception e) {
			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			e.printStackTrace();
		}
		
		bo.setId(id+1);
		this.insertSystemConfig(bo);
	}

	@Override
	public String getCurrentSkin() {
		long cfgId=SystemConfigConstantEnum.SYSTEM_CONFIG_SKIN.getCfgId();
		Map skinMap = JSONObject.parseObject(getSystemConfigById(cfgId).getContent(), Map.class);
		if(skinMap.containsKey("selected")){
			if("darkgreen".equals(skinMap.get("selected"))){
				return "default";
			}else{
				return skinMap.get("selected").toString();
			}
		}
		return "default";
	}

}
