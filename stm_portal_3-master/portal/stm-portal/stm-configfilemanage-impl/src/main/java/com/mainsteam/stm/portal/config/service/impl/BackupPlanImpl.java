package com.mainsteam.stm.portal.config.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.config.api.IBackupPlanApi;
import com.mainsteam.stm.portal.config.api.IConfigDeviceApi;
import com.mainsteam.stm.portal.config.bo.BackupPlanBo;
import com.mainsteam.stm.portal.config.bo.ConfigDeviceBo;
import com.mainsteam.stm.portal.config.dao.IBackupPlanDao;
import com.mainsteam.stm.portal.config.job.ConfigEngine;
/**
 * 
 * <li>文件名称: BackupPlanImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   liupeng
 */
public class BackupPlanImpl implements IBackupPlanApi{
	private static Logger logger = Logger.getLogger(IBackupPlanApi.class.getName());
	private IBackupPlanDao backupPlanDao;
	private ISequence seq;
	private ConfigEngine configEngine;
	
	public void setBackupPlanDao(IBackupPlanDao backupPlanDao) {
		this.backupPlanDao = backupPlanDao;
	}
	public void setSeq(ISequence seq) {
		this.seq = seq;
	}
	@Resource
	private IConfigDeviceApi configDeviceApi;
	@Override
	public BackupPlanBo addOrUpdatePlan(BackupPlanBo backupPlanBo) {
		try{
			List<BackupPlanBo> boList = backupPlanDao.getByName(backupPlanBo.getName());
			//名称已经存在
			if(boList != null && boList.size()>0 && !boList.get(0).getId().equals(backupPlanBo.getId())){
				return null;
			}
			//作更新操作
			if(backupPlanBo != null && backupPlanBo.getId() != null){
				backupPlanDao.update(backupPlanBo);
				//开启自动备份配置文件的Job
				configEngine.updateJob(backupPlanBo);
			}else{
				backupPlanBo.setId(seq.next());
				backupPlanDao.insert(backupPlanBo);
				//开启自动备份配置文件的Job
				configEngine.startJob(backupPlanBo);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return backupPlanBo;
	}
	
	public void setConfigEngine(ConfigEngine configEngine) {
		this.configEngine = configEngine;
	}
	@Override
	public int upateDevicePlan(BackupPlanBo bo) {
		int count = backupPlanDao.upateDevicePlan(bo);
		if(bo != null && bo.getId() != null){
			//开启自动备份配置文件的Job
			try {
				BackupPlanBo backupPlanBo = backupPlanDao.get(bo.getId());
				configEngine.updateJob(backupPlanBo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		return count;
	}
	
	@Override
	public int batchRemovePlan(long[] planIds) {		
		try {
			//将设备的备份计划删除
			for(long id : planIds){
				BackupPlanBo bo = queryPlanById(id);
				//查询计划下面绑定的资源
				List<ConfigDeviceBo> bos = configDeviceApi.getDeviceByPlanId(id);
				if(bos != null && bos.size()>0){
					for(ConfigDeviceBo b : bos){
						bo.getResourceIds().add(b.getId());
					}
					bo.setId(null);
					upateDevicePlan(bo);
				}
			}						
			int rows = backupPlanDao.batchDelPlan(planIds);
			//停止备份计划Job
			configEngine.stopJob(planIds);
			return rows;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public BackupPlanBo queryPlanById(long id) {
		BackupPlanBo bo = backupPlanDao.get(id);
		return bo;
	}

	@Override
	public void queryPlanByPager(Page<BackupPlanBo, Object> page) {
		if("entryTimeStr".equals(page.getSort())){
			page.setSort("ENTRY_DATETIME");
		}else if("entryName".equals(page.getSort())){
			page.setSort("b.Name");
		}else if("desc".equals(page.getSort())){
			page.setSort("BACKUP_DESC");
		}
		backupPlanDao.pageSelect(page);
	}

}
