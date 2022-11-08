package com.mainsteam.stm.portal.config.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.BackupPlanBo;
import com.mainsteam.stm.portal.config.dao.IBackupPlanDao;

/**
 * 
 * <li>文件名称: BackupPlanDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   liupeng
 */
public class BackupPlanDaoImpl extends BaseDao<BackupPlanBo> implements IBackupPlanDao {

	public BackupPlanDaoImpl(SqlSessionTemplate session) {
		super(session, IBackupPlanDao.class.getName());
	}
	@Override
	public void pageSelect(Page<BackupPlanBo, Object> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	@Override
	public int upateDevicePlan(BackupPlanBo bo) {
		return getSession().update(getNamespace()+"upateDevicePlan",bo);
	}
	@Override
	public int batchDelPlan(long[] ids) {
		return getSession().delete(getNamespace()+"batchDelPlan",ids);
	}
	@Override
	public List<BackupPlanBo> getByName(String name) {
		return getSession().selectList(getNamespace()+"getByName",name);
	}
}
