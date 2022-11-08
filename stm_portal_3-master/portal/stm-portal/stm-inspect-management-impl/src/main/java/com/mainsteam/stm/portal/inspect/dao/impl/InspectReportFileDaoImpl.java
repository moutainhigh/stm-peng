package com.mainsteam.stm.portal.inspect.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.inspect.bo.InspectReportFileBo;
import com.mainsteam.stm.portal.inspect.dao.IInspectReportFileDao;

/**
 * <li>文件名称: InspectReportFileDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月10日 下午4:40:20
 * @author   dfw
 */
public class InspectReportFileDaoImpl extends BaseDao<InspectReportFileBo> implements IInspectReportFileDao {

	public InspectReportFileDaoImpl(SqlSessionTemplate session) {
		super(session, IInspectReportFileDao.class.getName());
	}

	@Override
	public InspectReportFileBo getByReportId(long inspectReportId) {
		return get("getByReportId", inspectReportId);
	}

	@Override
	public int deleteByReportId(InspectReportFileBo inspectReportFileBo) {
		return del("deleteByReportId", inspectReportFileBo);
	}

}
