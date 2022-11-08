package com.mainsteam.stm.portal.business.report.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.report.dao.IBizSerReportDao;
import com.mainsteam.stm.portal.business.report.obj.BizSerReport;

/**
 * <li>文件名称: BizSerReportDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月18日
 * @author   caoyong
 */
public class BizSerReportDaoImpl extends BaseDao<BizSerReport> implements IBizSerReportDao {
	public BizSerReportDaoImpl(SqlSessionTemplate session) {
		super(session, IBizSerReportDao.class.getName());
	}
	@Override
	public List<BizSerReport> getBizSerReports(String searchKey) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("searchKey", searchKey);
		return getSession().selectList(getNamespace() + "getBizSerReportsBySearchKey",map);
	}

	@Override
	public BizSerReport getBizSerReport(Long id) {
		return getSession().selectOne(getNamespace() + "getBizSerReport",id);
	}

	@Override
	public List<BizSerReport> getBizSerReports(Long[] ids) {
		return getSession().selectList(getNamespace() + "getBizSerReportsByIds",ids);
	}
	
}
