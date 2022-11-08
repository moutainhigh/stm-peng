package com.mainsteam.stm.system.skin.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.image.api.IImageApi;
import com.mainsteam.stm.system.image.bo.ImageBo;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;

/**
 * <li>文件名称: SkinAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日
 * @author   ziwenwen
 */
@Controller
@RequestMapping("/system/skin/")
public class SkinAction extends BaseAction {
	
	@Autowired
	private ISystemConfigApi configApi;
	
	@Resource(name="stm_system_right_impl")
	IRightApi rightApi;

	@Resource(name="systemImageApi")
	private IImageApi imageApi;
	
	private static long cfgId=SystemConfigConstantEnum.SYSTEM_CONFIG_SKIN.getCfgId(); 
	
	/**
	 * <pre>
	 * 获取所有皮肤以及系统皮肤设置
	 * </pre>
	 * @return
	 */
	@RequestMapping("get")
	public JSONObject getSkin(){
		return toSuccess(configApi.getSystemConfigById(cfgId).getContent());
	}
	
	/**
	 * <pre>
	 * 修改保存皮肤设置
	 * </pre>
	 * @return
	 */
	@RequestMapping("save")
	public JSONObject saveSkin(String skin){
		List<Right> rightList = changeImg(skin);
		SystemConfigBo config=new SystemConfigBo();
		config.setId(cfgId);
		config.setContent(skin);
		configApi.updateSystemConfig(config);
		return toSuccess(rightList);
	}
	
	private List<Right> changeImg(String skin){
		List<Right> newRightList = new ArrayList<Right>();
		Map skinMap = JSONObject.parseObject(skin, Map.class);
		Map oldSkinMap = JSONObject.parseObject(configApi.getSystemConfigById(cfgId).getContent(), Map.class);
		if(skinMap.containsKey("selected")){
			List<Right> rightList = rightApi.getAll4Skin();
			Map<Long, Right> rightMap = new HashMap<Long, Right>();
			for(int i = 0; rightList != null && i < rightList.size(); i++){
				Right right = rightList.get(i);
				rightMap.put(right.getId(), right);
			}
			String selectedSkin = skinMap.get("selected").toString(), oldSelectedSkin = oldSkinMap.get("selected").toString();
			List<FileConstantEnum> fileConstantEnumList = FileConstantEnum.getFileConstantEnum(selectedSkin);
			for(int i = 0; fileConstantEnumList != null && i < fileConstantEnumList.size(); i++){
				FileConstantEnum fileConstantEnum = fileConstantEnumList.get(i);
				if(rightMap.containsKey(fileConstantEnum.getMenuId())){
					Right right = rightMap.get(fileConstantEnum.getMenuId());
					right.setFileId(fileConstantEnum.getFileId());
					rightApi.update(right);
					newRightList.add(right);
				}
			}
			// 系统图片管理配置文件
			ImageBo imgBo = imageApi.get();
			if("darkgreen".equals(selectedSkin)){
				imgBo.setSystemDefaultLogo("resource/themes/blue/images/logo.png");
				imgBo.setLoginDefaultLogo("resource/themes/blue/images/comm/table/login-logo.png");
			}else{
				imgBo.setSystemDefaultLogo("resource/themes/" + selectedSkin + "/images/logo.png");
				imgBo.setLoginDefaultLogo("resource/themes/" + selectedSkin + "/images/comm/table/login-logo.png");
			}
			imageApi.update(imgBo);
		}
		return newRightList;
	}
	
	@RequestMapping("getCurrentSkin")
	public JSONObject getCurrentSkin(){
		return toSuccess(configApi.getCurrentSkin());
	}
}


