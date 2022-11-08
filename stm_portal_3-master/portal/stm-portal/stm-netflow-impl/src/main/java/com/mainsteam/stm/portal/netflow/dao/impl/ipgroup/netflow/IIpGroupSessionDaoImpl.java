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
import com.mainsteam.stm.portal.netflow.dao.ipgroup.netflow.IIpGroupSessionDao;

/**
 * <li>文件名称: IIpGroupSessionDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月18日
 * @author   lil
 */
public class IIpGroupSessionDaoImpl extends BaseDao<NetflowBo> implements IIpGroupSessionDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIpGroupSessionDaoImpl(SqlSessionTemplate session) {
		super(session, IIpGroupSessionDao.class.getName());
	}

	@Override
	public Whole getTotalIpGroupSessionNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIpGroupSessionNetflows", bo);
	}

	@Override
	public List<NetflowBo> ipGroupSessionPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ipGroupSessionPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIpGroupSessionChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIpGroupSessionChartData", bo);
	}

}
