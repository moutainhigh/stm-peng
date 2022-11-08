/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl.ipgroup.netflow;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.ipgroup.netflow.IIpGroupTosDao;

/**
 * <li>文件名称: IIpGroupTosDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月22日
 * @author   lil
 */
public class IIpGroupTosDaoImpl extends BaseDao<NetflowBo> implements IIpGroupTosDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIpGroupTosDaoImpl(SqlSessionTemplate session) {
		super(session, IIpGroupTosDao.class.getName());
	}

	@Override
	public List<NetflowBo> ipGroupTosPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ipGroupTosPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIpGroupTosChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIpGroupTosChartData", bo);
	}

	@Override
	public Whole getTotalIpGroupTosNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIpGroupTosNetflows", bo);
	}

}
