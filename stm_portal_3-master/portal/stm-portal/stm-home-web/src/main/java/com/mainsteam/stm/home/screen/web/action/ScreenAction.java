package com.mainsteam.stm.home.screen.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.screen.api.IScreenApi;
import com.mainsteam.stm.home.screen.api.ITopoApi;
import com.mainsteam.stm.home.screen.bo.Biz;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: ScreenAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月20日
 * @author   ziwenwen
 */
@Controller
@RequestMapping("/home/screen")
public class ScreenAction extends BaseAction {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ScreenAction.class);
	
	@Autowired
	@Qualifier("homeScreenTopoApi")
	private ITopoApi topoApi;
	
	@Autowired
	@Qualifier("bizMainApi")
	private ITopoApi bizApi;
	
	@Autowired
	IScreenApi screenApi;

	private Map<String,Object> data=new HashMap<String, Object>();

	/**
	 * <pre>
	 * 保存用户大屏设置
	 * </pre>
	 * @param uBizRels
	 * @return
	 */
	@RequestMapping("/saveUserBizRels")
	public JSONObject saveUserBizRels(String userBizRel){
		Long userId=getLoginUser().getId();
		List<Biz> uBizRels=JSONArray.parseArray(userBizRel, Biz.class);
		screenApi.saveBizs(userId, uBizRels);
		return toSuccess(screenApi.getBizs(userId));
	}
	
	/**
	 * <pre>
	 * 加载所有用户设置数据
	 * </pre>
	 * @return
	 */
	@RequestMapping("/getScreenSetData")
	public JSONObject getScreenSetData(HttpServletRequest request){
		ILoginUser user=getLoginUser();
		data.put("biz", bizApi.getBizs(user, request));
		data.put("top", topoApi.getBizs(user, request));
		try {
			int num = License.checkLicense().checkModelAvailableNum(LicenseModelEnum.stmModelBusi);
		} catch (LicenseCheckException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Biz> bizList = screenApi.getBizs(user.getId());
		if(bizList!=null && bizList.size()!=0){
			for (int i=0;i<bizList.size();i++) {
				if(bizList.get(i).getBizType()==4){
					if(String.valueOf(bizList.get(i).getBizId()).length()>6){
						String idStr =String.valueOf(bizList.get(i).getBizId());
						String level =idStr.substring(0, 1);
						String id =idStr.substring(1, idStr.length());
						bizList.get(i).setBizId(Long.parseLong(id));
						bizList.get(i).setShowlevel(level);
					}
					
					
				}
			}
		}
		data.put("user", bizList);
		data.put("ifHaveBizAuthority",checkModularAuthority(LicenseModelEnum.stmModelBusi));
		return toSuccess(data);
	}
	
	@RequestMapping("/getBizSetData")
	public JSONObject getBizSetData(HttpServletRequest request){
		ILoginUser user=getLoginUser();
		data.put("biz", bizApi.getBizs(user, request));
		return toSuccess(data);
	}
	
	/**
	 * <pre>
	 * 验证模块权限
	 * </pre>
	 * @return
	 */
	@RequestMapping("/ifHaveBizAuthority")
	public JSONObject ifHaveBizAuthority(){
		return toSuccess(checkModularAuthority(LicenseModelEnum.stmModelBusi));
	}
	private boolean checkModularAuthority(LicenseModelEnum lme){
		try {
			int num = License.checkLicense().checkModelAvailableNum(lme);
			if(num>0){
				return true;
			}
		} catch (LicenseCheckException e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	
	/**
	 * <pre>
	 * 加载所有用户设置数据
	 * </pre>
	 * @return
	 */
	@RequestMapping("/updateUserBizRel")
	public JSONObject updateUserBizRel(Biz ubr){
		return toSuccess(screenApi.updateBiz(ubr));
	}
	
	/**
	 * <pre>
	 * 加载所有用户设置数据
	 * </pre>
	 * @return
	 */
	@RequestMapping("/loadUserData")
	public JSONObject loadUserData(){
		return toSuccess(screenApi.getBizs(getLoginUser().getId()));
	}
	@RequestMapping("/updateUserBizRelSelect")
	public JSONObject updateUserBizRelSelect(Long bizid,Long level,Long id,String title){
		Biz biz = new Biz();
		biz.setId(id);
		biz.setTitle(title);
		String bizStr=String.valueOf(level)+String.valueOf(bizid);
		biz.setBizId(Long.parseLong(bizStr));
		return toSuccess(screenApi.updateBiz(biz));
	}
	
	@RequestMapping("/getBizByID")
	public JSONObject getBizByID(Long id){
		Biz biz=screenApi.getBizByID(id);
		if(biz!=null){
			if(String.valueOf(biz.getBizId()).length()>6){
				String idStr =String.valueOf(biz.getBizId());
				String level =idStr.substring(0, 1);
				String idLong =idStr.substring(1, idStr.length());
				biz.setBizId(Long.parseLong(idLong));
				biz.setShowlevel(level);
			}
		}
	

		return toSuccess(biz);
	}
	
}


