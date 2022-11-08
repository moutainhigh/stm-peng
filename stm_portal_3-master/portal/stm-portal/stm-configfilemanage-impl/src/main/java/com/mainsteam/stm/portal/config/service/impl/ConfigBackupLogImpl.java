package com.mainsteam.stm.portal.config.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.api.IConfigBackupLogApi;
import com.mainsteam.stm.portal.config.bo.ConfigBackupLogBo;
import com.mainsteam.stm.portal.config.dao.IConfigBackupLogDao;
/**
 * <li>文件名称: ConfigBackupLogImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   caoyong
 */
public class ConfigBackupLogImpl implements IConfigBackupLogApi{
	private static Logger logger = Logger.getLogger(ConfigBackupLogImpl.class);
	private IConfigBackupLogDao configBackupLogDao;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private IFileClientApi fileClient;
	
	@Override
	public void selectByPage(Page<ConfigBackupLogBo, ConfigBackupLogBo> page,
			Long resourceId,boolean all,String searchKey) throws Exception{
		if(resourceId!=null){
			ConfigBackupLogBo condition = new ConfigBackupLogBo();
			condition.setId(resourceId);
			condition.setAll(all);
			condition.setFileName(searchKey);
			page.setCondition(condition);
		}
		configBackupLogDao.selectByPage(page);
		logger.info("configBackupLogDao.selectByPage successful!");
	}
	@Override
	public int insert(ConfigBackupLogBo bo) throws Exception{
		int count = configBackupLogDao.insert(bo);
		return count;
	}
	@Override
	public Long getNewlyFileIdById(Map<String, Object> map) throws Exception{
		return configBackupLogDao.getNewlyFileIdById(map);
	}
	
	public IConfigBackupLogDao getConfigBackupLogDao() {
		return configBackupLogDao;
	}

	public void setConfigBackupLogDao(IConfigBackupLogDao configBackupLogDao) {
		this.configBackupLogDao = configBackupLogDao;
	} 
}
