/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl.inf;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.inf.IIfNextHopDao;

/**
 * <li>文件名称: IIfNextHopDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public class IIfNextHopDaoImpl extends BaseDao<NetflowBo> implements IIfNextHopDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIfNextHopDaoImpl(SqlSessionTemplate session) {
		super(session, IIfNextHopDao.class.getName());
	}

	@Override
	public List<NetflowBo> ifNextHopPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifNextHopPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIfNextHopChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfNextHopChartData", bo);
	}

	@Override
	public long getTotalIfNextHopNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfNextHopNetflows", bo);
	}

	@Override
	public Whole getIfNextHopTotal(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getIfNextHopTotal", bo);
	}

}
