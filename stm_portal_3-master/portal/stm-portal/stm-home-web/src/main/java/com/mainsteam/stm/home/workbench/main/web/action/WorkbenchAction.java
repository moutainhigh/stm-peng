package com.mainsteam.stm.home.workbench.main.web.action;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.workbench.main.api.IUserWorkBenchApi;
import com.mainsteam.stm.home.workbench.main.bo.WorkBench;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: WorkbenchAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月18日
 * @author   ziwenwen
 */

@Controller
@RequestMapping("/home/workbench/main")
public class WorkbenchAction extends BaseAction {

	@Autowired
	private IUserWorkBenchApi userWorkBenchApi;
	
	@RequestMapping("/getAllWorkbenchs")
	public JSONObject getAllWorkbenchs(){
		return toSuccess(userWorkBenchApi.getAllWorkBench());
	}
	
	@RequestMapping("/getUserWorkbenchs")
	public JSONObject getUserWorkbenchs(HttpSession session){
		ILoginUser loginUser=getLoginUser(session);
		return toSuccess(userWorkBenchApi.getUserWorkBenchs(loginUser.getId()));
	}
	
	@RequestMapping("/setUserWorkbenchs")
	public JSONObject setUserWorkbenchs(String uws,HttpSession session){
		Long userId=getLoginUser(session).getId();
		return toSuccess(userWorkBenchApi.setUserWorkBenchs(userId,JSONArray.parseArray(uws, WorkBench.class)));
	}
	
	@RequestMapping("/setExt")
	public JSONObject setExt(WorkBench wb,HttpSession session){
		wb.setUserId(getLoginUser(session).getId());
		return toSuccess(userWorkBenchApi.setExt(wb));
	}
	
	@RequestMapping("/delSingleUserWorkbench")
	public JSONObject delSingleUserWorkbench(WorkBench uw,HttpSession session){
		uw.setUserId(getLoginUser(session).getId());
		return toSuccess(userWorkBenchApi.delUserWorkBench(uw));
	}
	

	@RequestMapping("/insertUserAllWorkbenchs")
	public JSONObject insertUserAllWorkbenchs(Long userId){
		return toSuccess(userWorkBenchApi.insertUserAllWorkbenchs(userId));
	}

	@RequestMapping("/delUserAllWorkbenchs")
	public JSONObject delUserAllWorkbenchs(Long userId){
		return toSuccess(userWorkBenchApi.delUserAllWorkbenchs(userId));
	}
	@RequestMapping("/setUserWorkBenchByDefaultId")
	public JSONObject setUserWorkBenchByDefaultId(Long workbenchId,int sort,Long interfaceid){
	Long userid=	BaseAction.getLoginUser().getId();
		WorkBench bench= new WorkBench();
		bench.setUserId(userid);
		bench.setSort(sort);
		bench.setWorkbenchId(workbenchId);
		if(interfaceid==0){
			bench.setDefaultId(null);
		}else{
			bench.setDefaultId(interfaceid);
		}
		userWorkBenchApi.setUserWorkBenchByDefaultId(bench);
	return toSuccess(0);
	 
 }
	@RequestMapping("/checkLicenseByBiz")
	public JSONObject checkLicenseByBiz(){
		int num=0;
		try {
			 num = License.checkLicense().checkModelAvailableNum(LicenseModelEnum.stmModelBusi);
		} catch (LicenseCheckException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toSuccess(num);
	}
	
}
