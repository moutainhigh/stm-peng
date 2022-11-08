package com.mainsteam.stm.portal.inspect.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import com.mainsteam.stm.portal.inspect.api.InspectReportFileApi;
import com.mainsteam.stm.portal.inspect.api.InspectXMLHandlerApi;
import com.mainsteam.stm.portal.inspect.bo.InspectReportData;
import com.mainsteam.stm.portal.inspect.bo.InspectReportFileBo;
import com.mainsteam.stm.portal.inspect.dao.IInspectReportFileDao;

/**
 * <li>文件名称: InspectReportFileImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午4:53:54
 * @author   dengfuwei
 */
public class InspectReportFileImpl implements InspectReportFileApi {
	
	@Resource(name="stm_inspect_inspectReportFileDao")
	private IInspectReportFileDao inspectReportFileDao;
	
	@Resource(name="stm_inspect_inspectXMLHandlerApi")
	private InspectXMLHandlerApi inspectXMLHandlerApi;
	
	@Override
	public void insert(InspectReportFileBo inspectReportFileBo) {
		inspectReportFileDao.insert(inspectReportFileBo);
	}

	@Override
	public InspectReportFileBo getByReportId(long inspectReportId) {
		return inspectReportFileDao.getByReportId(inspectReportId);
	}

	@Override
	public void deleteByReportId(InspectReportFileBo inspectReportFileBo) {
		inspectReportFileDao.deleteByReportId(inspectReportFileBo);
	}

	@Override
	public void saveFile(InspectReportData inspectReportData) {
		//保存文件
		long fileId = inspectXMLHandlerApi.createITDXmlFile(inspectReportData);
		InspectReportFileBo inspectReportFileBo = new InspectReportFileBo();
		inspectReportFileBo.setFileId(fileId);
		inspectReportFileBo.setInspectReportId(Long.valueOf(inspectReportData.getData().getId()));
		inspectReportFileBo.setRepGenerateTime(new Date());
		inspectReportFileDao.insert(inspectReportFileBo);
	}

}
