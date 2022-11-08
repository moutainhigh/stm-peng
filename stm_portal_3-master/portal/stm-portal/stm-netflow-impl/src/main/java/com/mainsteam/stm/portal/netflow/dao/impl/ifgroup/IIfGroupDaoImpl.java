/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl.ifgroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.OptionBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.ifgroup.IIfGroupDao;

/**
 * <li>文件名称: IIfGroupDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月17日
 * @author   lil
 */
public class IIfGroupDaoImpl extends BaseDao<NetflowBo> implements IIfGroupDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIfGroupDaoImpl(SqlSessionTemplate session) {
		super(session, IIfGroupDao.class.getName());
	}

	@Override
	public List<NetflowBo> ifGroupPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifGroupPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIfGroupChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfGroupChartData", bo);
	}

	@Override
	public long getTotalIfGroupNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfGroupNetflows", bo);
	}

	@Override
	public String getIfIdsByGroupId(Long ifGroupId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", ifGroupId);
		return getSession().selectOne(getNamespace() + "getIfIdsByGroupId", map);
	}

	@Override
	public List<OptionBo> getIfGroupIfIds() {
		return getSession().selectList(getNamespace() + "getIfGroupIfIds"); 
	}

	@Override
	public Whole getIfGroupTotals(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getIfGroupTotals", bo);
	}
	
}
