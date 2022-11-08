package com.mainsteam.stm.system.um.right.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;

/**
 * <li>文件名称: RightAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月6日
 * @author   ziwenwen
 */

@RequestMapping("/system/right")
@Controller
public class RightAction extends BaseAction {

	@Resource(name="stm_system_right_impl")
	IRightApi rightApi;
	
	/**
	 * 获取所有权限
	 * @return
	 * @throws LicenseCheckException 
	 */
	@RequestMapping("/getAll")
	JSONObject getAll() throws LicenseCheckException{
		//过滤子菜单
		List<Right> rightList = rightApi.getAll();
		
		
		List<Right> resultList = new ArrayList<Right>();
		
		for(Right right:rightList){
			try{
			if((right.getPid()==null?0:right.getPid()) <1){
				resultList.add(right);
			}
			}
			catch(Exception e){
				e.toString();
				
			}
			
		}
		
		
		
		return toSuccess(resultList);
	}
	
	/**
	 * 根据域类型获取域集合 0：内部域 1：外部域
	 * @return
	 */
	@RequestMapping("/getRightByType")
	JSONObject getRightByType(int type){
		return toSuccess(rightApi.getRightByType(type));
	}
	
	/**
	 * 根据权限id获取权限详情
	 * @param id
	 * @return
	 */
	@RequestMapping("/get")
	JSONObject get(Long id){
		return toSuccess(rightApi.get(id));
	}
	
	/**
	 * 修改权限
	 * @param rightBo
	 * @return
	 */
	@RequestMapping("/update")
	JSONObject update(Right right){
		return toSuccess(rightApi.update(right));
	}
	
	/**
	 * 修改权限
	 * @param rightBo
	 * @return
	 */
	@RequestMapping("/updateSort")
	JSONObject update(String datas){
		return toSuccess(rightApi.updateSort(JSONArray.parseArray(datas,Right.class)));
	}
	
	/**
	 * 删除权限
	 * @param id
	 * @return
	 */
	@RequestMapping("/del")
	JSONObject del(Long id){
		return toSuccess(rightApi.updateDelStatus(id));
	}
	
	/**
	 * 新增权限
	 * @param rightBo
	 * @return
	 */
	@RequestMapping("/insert")
	JSONObject insert(Right right){
		return toSuccess(rightApi.insert(right));
	}
}


