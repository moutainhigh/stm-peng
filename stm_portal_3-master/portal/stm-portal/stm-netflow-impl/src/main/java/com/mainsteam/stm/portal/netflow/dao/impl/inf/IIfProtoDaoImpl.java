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
import com.mainsteam.stm.portal.netflow.dao.inf.IIfProtoDao;

/**
 * <li>文件名称: IIfProtoDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public class IIfProtoDaoImpl extends BaseDao<NetflowBo> implements IIfProtoDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIfProtoDaoImpl(SqlSessionTemplate session) {
		super(session, IIfProtoDao.class.getName());
	}

	@Override
	public long getTotalIfProtoNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfProtoNetflows", bo);
	}

	@Override
	public List<NetflowBo> ifProtoPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifProtoPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIfProtoChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfProtoChartData", bo);
	}

	@Override
	public Whole getIfProtoTotals(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getIfProtoTotals", bo);
	}

}
