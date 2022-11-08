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
import com.mainsteam.stm.portal.netflow.dao.inf.IIfSessionDao;

/**
 * <li>文件名称: IIfSessionDaoImpl.java</li>
 * <li>公　　import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public class IIfSessionDaoImpl extends BaseDao<NetflowBo> implements IIfSessionDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIfSessionDaoImpl(SqlSessionTemplate session) {
		super(session, IIfSessionDao.class.getName());
	}

	@Override
	public List<NetflowBo> ifSessionPageSelect(Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifSessionPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIfSessionChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfSessionChartData", bo);
	}

	@Override
	public long getTotalIfSessionNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfSessionNetflows", bo);
	}

	@Override
	public Whole getIfSessionTotals(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getIfSessionTotals", bo);
	}

}
