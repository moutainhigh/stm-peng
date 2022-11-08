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
import com.mainsteam.stm.portal.netflow.dao.IDeviceProtoDao;

/**
 * <li>文件名称: IDeviceProtoDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月3日
 * @author   lil
 */
public class IDeviceProtoDaoImpl extends BaseDao<NetflowBo> implements IDeviceProtoDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IDeviceProtoDaoImpl(SqlSessionTemplate session) {
		super(session, IDeviceProtoDao.class.getName());
	}

	@Override
	public List<NetflowBo> deviceProtoNetflowPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "deviceProtoNetflowPageSelect", page);
	}

	@Override
	public Whole getTotalProtoNetflowsOfDevice(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalProtoNetflowsOfDevice", bo);
	}

	@Override
	public List<NetflowBo> queryDeviceProtoNetflowOfTimepoint(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "queryDeviceProtoNetflowOfTimepoint", bo);
	}

}
