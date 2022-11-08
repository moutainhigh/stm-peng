package com.mainsteam.stm.portal.business.web.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.business.api.IBizSelfApi;
import com.mainsteam.stm.portal.business.bo.BizSelfBo;
/**
 * <li>文件名称: BizSelfAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月20日
 * @author   liupeng
 */
@Controller
@RequestMapping("/portal/business/service/self")
public class BizSelfAction extends BaseAction{
	@Autowired
	private IBizSelfApi bizSelfApi;

	@Autowired
	private ISystemConfigApi configApi;
	
	@RequestMapping("/insert")
	public JSONObject insert(@ModelAttribute BizSelfBo bizSelfBo){
		bizSelfApi.insert(bizSelfBo);
		return toSuccess(bizSelfBo);
	}
	@RequestMapping("/getList")
	public JSONObject getList(){
		return toSuccess(bizSelfApi.getList());
	}
	@RequestMapping("/remove")
	public JSONObject remove(@RequestParam long id){
		bizSelfApi.deleteById(id);
		return toSuccess("删除成功");
	}
	
	@RequestMapping("getCSkin")
	public JSONObject getCSkin(){
		Map<String, Object> skinmap= new HashMap<String, Object>();
		skinmap.put("skin", configApi.getCurrentSkin());
		//与当前皮肤作对比
		if(configApi.getCurrentSkin().equals("blue")){
			//获取图片所在位置
			String path=getHttpServletRequest().getSession().getServletContext().getRealPath("/resource/themes/blue/images/bizser/background");
			String[] file= new File(path).list();
			List<String> blues= new ArrayList<String>();
			List<String> backgroundimg= new ArrayList<String>();
			for (String s : file) {
				blues.add(s.toString());
			}
			for(int i=0;i<blues.size();i++){
				String img="";
				if(i==0){
					img="系统默认";	
				}else{
					 img="背景"+i;
				}
				
				backgroundimg.add(img);
			}
			skinmap.put("backgroundimg", backgroundimg);
			skinmap.put("skinarr", blues);
			//组装blue下的图片
			
		}else{
			String path=getHttpServletRequest().getSession().getServletContext().getRealPath("/resource/themes/blue/images/bizser/background");
			String[] file= new File(path).list();
			List<String> defalts= new ArrayList<String>();
			List<String> backgroundimg= new ArrayList<String>();
			for (String s : file) {
				defalts.add(s.toString());
			}
			for(int i=0;i<defalts.size();i++){
				String img="";
				if(i==0){
					img="系统默认";	
				}else{
					 img="背景"+i;
				}
				
				backgroundimg.add(img);
			}
			skinmap.put("backgroundimg", backgroundimg);
			skinmap.put("skinarr", defalts);
			//组装defalut下的图片
		}
		
		return toSuccess(skinmap);
	}
	
}
