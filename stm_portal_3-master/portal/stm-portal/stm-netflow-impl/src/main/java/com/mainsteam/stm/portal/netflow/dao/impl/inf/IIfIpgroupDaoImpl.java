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
import com.mainsteam.stm.portal.netflow.dao.inf.IIfIpgroupDao;

/**
 * <li>文件名称: IIfIpgroupDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public class IIfIpgroupDaoImpl extends BaseDao<NetflowBo> implements IIfIpgroupDao {

	/**
	 * @param Ipgroup
	 * @param iDaoNamespace
	 */
	public IIfIpgroupDaoImpl(SqlSessionTemplate Ipgroup) {
		super(Ipgroup, IIfIpgroupDao.class.getName());
	}

	@Override
	public List<NetflowBo> ifIpgroupPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifIpgroupPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIfIpgroupChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfIpgroupChartData", bo);
	}

	@Override
	public long getTotalIfIpgroupNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfIpgroupNetflows", bo);
	}

	@Override
	public Whole getIfIpgroupTotals(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getIfIpgroupTotals", bo);
	}

}
