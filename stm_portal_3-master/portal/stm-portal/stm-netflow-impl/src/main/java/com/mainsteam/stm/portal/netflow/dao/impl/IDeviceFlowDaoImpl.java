/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.IDeviceFlowDao;

/**
 * <li>文件名称: IDeviceDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月26日
 * @author   lil
 */
public class IDeviceFlowDaoImpl extends BaseDao<NetflowBo> implements IDeviceFlowDao {

	/**
	 * @param session
	 */
	public IDeviceFlowDaoImpl(SqlSessionTemplate session) {
		super(session, IDeviceFlowDao.class.getName());
	}

	@Override
	public List<NetflowBo> deviceListPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "deviceListPageSelect", page);
	}

	@Override
	public Whole findDeviceListTotalFlows(NetflowParamBo bo) {
		return  getSession().selectOne(getNamespace() + "findDeviceListTotalFlows", bo);
	}

	@Override
	public List<NetflowBo> queryDeviceFlowOfTimePoint(
			NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "queryDeviceFlowOfTimePoint", bo);
	}

}
