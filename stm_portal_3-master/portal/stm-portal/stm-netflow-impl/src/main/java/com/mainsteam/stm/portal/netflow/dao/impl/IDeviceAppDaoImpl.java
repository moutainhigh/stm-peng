/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ApplicationBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.IDeviceAppDao;

/**
 * <li>文件名称: IDeviceAppDaoImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月16日
 * @author lil
 */
public class IDeviceAppDaoImpl extends BaseDao<NetflowBo> implements
		IDeviceAppDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IDeviceAppDaoImpl(SqlSessionTemplate session) {
		super(session, IDeviceAppDao.class.getName());
	}

	@Override
	public List<NetflowBo> deviceAppNetflowPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(
				getNamespace() + "deviceAppNetflowPageSelect", page);
	}

	@Override
	public Whole getTotalAppNetflowsOfDevice(NetflowParamBo bo) {
		return getSession().selectOne(
				getNamespace() + "getTotalAppNetflowsOfDevice", bo);
	}

	@Override
	public List<NetflowBo> queryDeviceAppNetflowOfTimepoint(NetflowParamBo bo) {
		return getSession().selectList(
				getNamespace() + "queryDeviceAppNetflowOfTimepoint", bo);
	}

	@Override
	public List<ApplicationBo> getAppId(long startRow, long number) {
		Map<String, Object> map = new HashMap<>();
		map.put("startRow", startRow);
		map.put("number", number);
		return getSession().selectList(getNamespace() + "device_app_getAppId",
				map);
	}

}
