package com.mainsteam.stm.portal.threed.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.mainsteam.stm.portal.threed.api.IModelApi;
import com.mainsteam.stm.portal.threed.api.IUrlApi;
import com.mainsteam.stm.portal.threed.bo.ModelBo;
import com.mainsteam.stm.portal.threed.bo.UrlBo;
import com.mainsteam.stm.portal.threed.dao.IModelDao;
import com.mainsteam.stm.portal.threed.dao.impl.Adapter3DInterfaceImpl;
import com.mainsteam.stm.portal.threed.dao.impl.DataStreamImpl;

public class ModelImpl implements IModelApi {
	IModelDao modelDao;
	IUrlApi urlApi;
//	DataStreamImpl dataStreamImpl;
	@Resource
	private Adapter3DInterfaceImpl adapter3DInterfaceImpl;
	public void setModelDao(IModelDao modelDao) {
		this.modelDao = modelDao;
	}

	public void setUrlApi(IUrlApi urlApi) {
		this.urlApi = urlApi;
	}

//	public void setDataStreamImpl(DataStreamImpl dataStreamImpl) {
//		this.dataStreamImpl = dataStreamImpl;
//	}

	@Override
	public List<ModelBo> getAll() {
		return modelDao.getAll();
	}

	@Override
	public int update(ModelBo bo) {
		return modelDao.update(bo);
	}

	@Override
	public String getProductInfo() throws Exception{
		String productInfo = adapter3DInterfaceImpl.getProductInfo();
		// 更新到数据库
		UrlBo bo = new UrlBo();
		bo.setProductInfo(productInfo);
		urlApi.update(bo);
		return productInfo;
	}
}
