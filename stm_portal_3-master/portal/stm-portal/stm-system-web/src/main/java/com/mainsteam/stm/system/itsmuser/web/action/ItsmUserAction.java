package com.mainsteam.stm.system.itsmuser.web.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.itsmuser.api.IItsmUserApi;
import com.mainsteam.stm.system.itsmuser.bo.ItsmSystemBo;

@Controller
@RequestMapping("/system/itsmUser/")
public class ItsmUserAction extends BaseAction{
	
	@Autowired
	private IItsmUserApi itsmUserApi;
	
	/**
	 * 保存第三方系统配置信息
	 * @param itsmSystemBo
	 * @return
	 */
	@RequestMapping("saveItsmSystem")
	public JSONObject saveItsmSystem(ItsmSystemBo itsmSystemBo) {
		//判断URL是否存在
		boolean isExsit = itsmUserApi.isWsdlURLExist(itsmSystemBo);
		if (isExsit) {
			return toFailForGroupNameExsit("保存失败,该wsdlURL已经存在");
		}
		itsmSystemBo.setIsOpen(1);
		return toSuccess(itsmUserApi.saveItsmSystem(itsmSystemBo));
	}
	
	/**
	 * 分页查询配置信息
	 * @param page
	 * @return
	 */
	@RequestMapping("queryItsmSystem")
	public JSONObject queryItsmSystem(Page<ItsmSystemBo, ItsmSystemBo> page) {
		List<ItsmSystemBo> listSystem = itsmUserApi.queryItsmSystem(page);
		page.setDatas(listSystem);;
		return toSuccess(page);
	}
	
	/**
	 * 批量删除配置信息
	 * @param delIds
	 * @return
	 */
	@RequestMapping("removeItsmSystem")
	public JSONObject removeItsmSystem(Long[] delIds){
		int delNum = this.itsmUserApi.batchDel(delIds);
		return toSuccess(delNum > 0 ? true : false);
	}
	
	/**
	 * 根据主键ID查询配置信息
	 * @param id
	 * @return
	 */
	@RequestMapping("getItsmSystemById")
	public JSONObject getItsmSystemById(Long id) {
		ItsmSystemBo itsmSystemBo = this.itsmUserApi.getItsmSystemById(id);
		return toSuccess(itsmSystemBo);
	}
	
	/**
	 * 更新第三方系统配置信息
	 * @param itsmSystemBo
	 * @return
	 */
	@RequestMapping("updateItsmSystem")
	public JSONObject updateItsmSystem(ItsmSystemBo itsmSystemBo) {
		//判断URL是否存在
		boolean isExsit = itsmUserApi.isWsdlURLExist(itsmSystemBo);
		if (isExsit) {
			return toFailForGroupNameExsit("保存失败,该wsdlURL已经存在");
		}
		int updateNum = this.itsmUserApi.updateItsmSystem(itsmSystemBo);
		return toSuccess(updateNum);
	}
	
	/**
	 * 更新是开启还是关闭
	 * @return
	 */
	@RequestMapping("updateSystemStartState")
	public JSONObject updateSystemStartState(Long[] ids, int isOpen) {
		int updateNum = this.itsmUserApi.updateSystemStartState(ids, isOpen);
		return toSuccess(updateNum);
	}
}
