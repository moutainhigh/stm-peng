package com.mainsteam.stm.portal.config.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigDeviceBo;
import com.mainsteam.stm.portal.config.dao.IConfigDeviceDao;
/**
 * <li>文件名称: ConfigDeviceDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   caoyong
 */
public class ConfigDeviceDaoImpl extends BaseDao<ConfigDeviceBo> implements IConfigDeviceDao {

	public ConfigDeviceDaoImpl(SqlSessionTemplate session) {
		super(session, IConfigDeviceDao.class.getName());
	}

	@Override
	public List<ConfigDeviceBo> selectByPage(ConfigDeviceBo cdBo) {
		return getSession().selectList(getNamespace()+"pageSelect4ConfigDevice",cdBo);
	}
	
	@Override
	public int batchInsert(List<ConfigDeviceBo> list) {
		return this.batchInsert("batchInsert", list);
	}

	@Override
	public int batchDel(Long[] ids) {
		return getSession().update("batchDel", ids);
	}

	@Override
	public List<ConfigDeviceBo> getByPlanId(Long planId) {
		return getSession().selectList(getNamespace()+"getByPlanId",planId);
	}
	public List<Long> getAllResourceIds(){
		return getSession().selectList(getNamespace() + "getAllResourceIds",null);
	}
	@Override
	public int batchDelConfigDevice(Long[] ids) {
		return getSession().delete("batchDelConfigDevice", ids);
	}
	
	@Override
	public int deleteResourceByResourceIds(Long[] ids) {
		return getSession().delete("deleteResourceByResourceIds", ids);
	}

	@Override
	public List<ConfigDeviceBo> getExcept(ConfigDeviceBo bo) {
		return getSession().selectList(getNamespace()+"getExcept",bo);
	}

	@Override
	public List<ConfigDeviceBo> getAllResources() {
		return getSession().selectList(getNamespace()+"getAllResources");
	}
}
