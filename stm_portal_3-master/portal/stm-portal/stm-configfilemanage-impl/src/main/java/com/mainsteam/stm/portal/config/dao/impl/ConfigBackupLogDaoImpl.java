package com.mainsteam.stm.portal.config.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigBackupLogBo;
import com.mainsteam.stm.portal.config.dao.IConfigBackupLogDao;
/**
 * <li>文件名称: ConfigBackupLogDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   caoyong
 */
public class ConfigBackupLogDaoImpl extends BaseDao<ConfigBackupLogBo> implements IConfigBackupLogDao {
	/**
	 * 构造器
	 * @param session
	 */
	public ConfigBackupLogDaoImpl(SqlSessionTemplate session) {
		super(session, IConfigBackupLogDao.class.getName());
	}
	@Override
	public void selectByPage(Page<ConfigBackupLogBo, ConfigBackupLogBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	@Override 
	public Long getNewlyFileIdById(Map<String, Object> map){
		return getSession().selectOne("getNewlyFileIdById", map);
	}
	@Override
	public int batchDelCBLByResourceIds(Long[] ids) {
		return getSession().delete("batchDelCBLByResourceIds", ids);
	}
	@Override
	public List<ConfigBackupLogBo> getConfigHistoryByResourceId(long id) {
		return getSession().selectList(getNamespace()+"getConfigHistoryByResourceId", id);
	}

}
