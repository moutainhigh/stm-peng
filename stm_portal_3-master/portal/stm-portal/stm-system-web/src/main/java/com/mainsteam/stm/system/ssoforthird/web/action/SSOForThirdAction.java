package com.mainsteam.stm.system.ssoforthird.web.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.ssoforthird.api.ISSOForThirdApi;
import com.mainsteam.stm.system.ssoforthird.bo.SSOForThirdBo;

@Controller
@RequestMapping("/system/SSOForThird/")
public class SSOForThirdAction extends BaseAction {
	@Autowired
	private ISSOForThirdApi stm_sysmanage_Api;
	
	/**
	 * 保存第三方系统配置信息
	 * @param itsmSystemBo
	 * @return
	 */
	@RequestMapping("saveSSOForThird")
	public JSONObject saveSSOForThird(SSOForThirdBo ssoForThirdBo) {
		//判断URL是否存在
		boolean isExsit = stm_sysmanage_Api.isWsdlURLExist(ssoForThirdBo);
		if (isExsit) {
			return toFailForGroupNameExsit("保存失败,该wsdlURL已经存在");
		}
		ssoForThirdBo.setIsOpen(1);
		String userName = getLoginUser().getAccount();
		ssoForThirdBo.setUserName(userName);
		return toSuccess(stm_sysmanage_Api.saveSSOForThird(ssoForThirdBo));
	}
	
	/**
	 * 分页查询配置信息
	 * @param page
	 * @return
	 */
	@RequestMapping("querySSOForThird")
	public JSONObject querySSOForThird(Page<SSOForThirdBo, SSOForThirdBo> page) {
		List<SSOForThirdBo> listforThirdBos = stm_sysmanage_Api.querySSOForThird(page);
		page.setDatas(listforThirdBos);;
		return toSuccess(page);
	}
	
	/**
	 * 批量删除配置信息
	 * @param delIds
	 * @return
	 */
	@RequestMapping("removeSystem")
	public JSONObject removeSystem(Long[] delIds){
		int delNum = this.stm_sysmanage_Api.batchDel(delIds);
		return toSuccess(delNum > 0 ? true : false);
	}
	
	/**
	 * 根据主键ID查询配置信息
	 * @param id
	 * @return
	 */
	@RequestMapping("getSSOForThirdById")
	public JSONObject getSSOForThirdById(Long id) {
		SSOForThirdBo ssoForThirdBo = this.stm_sysmanage_Api.getSSOForThirdById(id);
		return toSuccess(ssoForThirdBo);
	}
	
	/**
	 * 更新第三方系统配置信息
	 * @param itsmSystemBo
	 * @return
	 */
	@RequestMapping("updateSSOForThird")
	public JSONObject updateSSOForThird(SSOForThirdBo ssoForThirdBo) {
		//判断URL是否存在
		boolean isExsit = stm_sysmanage_Api.isWsdlURLExist(ssoForThirdBo);
		if (isExsit) {
			return toFailForGroupNameExsit("保存失败,该wsdlURL已经存在");
		}
		int updateNum = this.stm_sysmanage_Api.updateSSOForThird(ssoForThirdBo);
		return toSuccess(updateNum);
	}
	
	/**
	 * 更新是开启还是关闭
	 * @return
	 */
	@RequestMapping("updateSSOForThirdStartState")
	public JSONObject updateSSOForThirdStartState(Long[] ids, int isOpen) {
		int updateNum = this.stm_sysmanage_Api.updateSSOForThirdStartState(ids, isOpen);
		return toSuccess(updateNum);
	}
}
