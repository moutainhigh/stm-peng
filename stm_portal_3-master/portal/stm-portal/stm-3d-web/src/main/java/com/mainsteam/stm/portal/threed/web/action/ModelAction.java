package com.mainsteam.stm.portal.threed.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.threed.api.IModelApi;
import com.mainsteam.stm.portal.threed.bo.ModelBo;
import com.mainsteam.stm.portal.threed.xfire.exception.ThreeDException;

@Controller
@RequestMapping("/portal/3d/model")
public class ModelAction extends BaseAction{
	@Autowired
	IModelApi modelApi;
	/**
	 * 得到所有的型号关联信息
	 * @return
	 */
	@RequestMapping("/getAll")
	public JSONObject getAll(){
		return toSuccess(modelApi.getAll());
	}
	/**
	 * 获取3D产品信息
	 * @return
	 */
	@RequestMapping("/getProductInfo")
	public JSONObject getProductInfo(){
		try {
			String product = modelApi.getProductInfo();
			return toSuccess(product);
		} catch (ThreeDException e) {
			return toJsonObject(1000, e.getMessage());
		} catch (Exception e) {
			return toJsonObject(299, "数据库操作失败");
		}
	}
	
	/**
	 * 更新资源类型和设备型号之间的关联关系
	 * @param type
	 * @param model
	 * @return
	 */
	@RequestMapping("/update")
	public JSONObject update(String type,String model){
		ModelBo bo = new ModelBo();
		bo.setModel(model);
		bo.setType(type);
		int row = modelApi.update(bo);
		return toSuccess(row);
	}
}
