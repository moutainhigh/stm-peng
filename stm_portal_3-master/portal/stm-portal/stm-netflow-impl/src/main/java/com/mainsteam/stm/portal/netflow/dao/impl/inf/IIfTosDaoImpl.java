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
import com.mainsteam.stm.portal.netflow.dao.inf.IIfTosDao;

/**
 * <li>文件名称: IIfTosDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月15日
 * @author   lil
 */
public class IIfTosDaoImpl extends BaseDao<NetflowBo> implements IIfTosDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIfTosDaoImpl(SqlSessionTemplate session) {
		super(session, IIfTosDao.class.getName());
	}

	@Override
	public List<NetflowBo> ifTosPageSelect(Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifTosPageSelect", page);
	}

	@Override
	public long getTotalIfTosNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfTosNetflows", bo);
	}

	@Override
	public List<NetflowBo> getIfTosChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfTosChartData", bo);
	}

	@Override
	public Whole getIfTosTotals(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getIfTosTotals", bo);
	}

}
