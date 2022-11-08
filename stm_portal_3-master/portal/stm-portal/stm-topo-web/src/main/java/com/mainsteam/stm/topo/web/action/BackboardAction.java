package com.mainsteam.stm.topo.web.action;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.topo.api.IBackbordApi;
import com.mainsteam.stm.topo.api.ThirdService;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * <li>背板管理</li>
 * <li>文件名称: BackboardAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年9月23日
 * @author zwx
 */
@Controller
@RequestMapping(value="/topo/backboard")
public class BackboardAction extends BaseAction{
	
	private final Logger logger = LoggerFactory.getLogger(BackboardAction.class);
	@Autowired
	private IBackbordApi backbordApi;
	@Autowired
	private ThirdService thirdSvc;
	/**
	 * 导入背板信息
	 * @param id
	 */
	@Deprecated
	@RequestMapping(value="/import", method=RequestMethod.POST)
	public String importBackbordXml(MultipartFile file){
		try {
			if(null != file){
				backbordApi.importBackbordXml(file);
			}else {
				logger.error("未选择背板xml文件!");
			}
			return super.toSuccess("数据导入成功").toJSONString();
		} catch (Exception e) {
			logger.error("导入背板xml数据失败,可能原因：背板xml文件数据格式错误,请检查格式!",e);
			return toJsonObject(700, "文件内容或格式错误，数据导入失败").toJSONString();
		}
	}
	
	/**
	 * 导出背板信息
	 * @param id
	 */
	@Deprecated
	@RequestMapping(value="/export", method=RequestMethod.GET)
	public void exportBackbordInfo(Long instanceId,String ip,HttpServletResponse response){
		  try {
	            response.setCharacterEncoding("UTF-8");
	    		response.setContentType("application/xml");
	    		
	    		OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
	    		xmlFormat.setEncoding("UTF-8");
	    		
	    		XMLWriter writer = null;	//写XML的对象
	    		OutputStream stream = response.getOutputStream();
	    		writer = new XMLWriter(stream, xmlFormat);		//xml转入输出流
	    		
	    		Object[] objs = backbordApi.createBackborcXmlDom(instanceId,ip);	//创建dom
	    		response.setHeader("Content-Disposition","attachment;filename="+objs[1].toString());
	    		
	    		writer.write(objs[0]);
	    		writer.close();
	    		stream.flush();
	    		stream.close();
	        } catch (Exception e) {
	        	logger.error("导出背板信息失败!",e);
	        }
	}
	
	/**
	 * 获取设备背板信息
	 * @param instanceId
	 * @return
	 */
	@RequestMapping(value="/get",produces="text/html;charset=UTF-8")
	public String getBackboard(Long instanceId) {
		try {
			if(null != instanceId){
				return backbordApi.getBackbordBo(instanceId);
			}else {
				logger.error("缺少参数[instanceId],无法查询数据");
				return toJsonObject(700,"该设备没有关联上资源，无法查询数据!").toString(); 
			}
		} catch (Exception e) {
			logger.error("获取设备背板信息数据失败!",e);
			return toJsonObject(700, "数据查询失败!").toJSONString();
		}
	}
	
	/**
	 * 保存背板信息
	 * @param instanceId资源实例id
	 * @param info		背板信息（json）
	 * @param isBatch	是否批量保存
	 * @return
	 */
	@RequestMapping(value="/save",method={RequestMethod.POST})
	public JSONObject save(Long instanceId,String info,Boolean isBatch){
		try {
			backbordApi.saveBackbord(instanceId, info, isBatch);
			return super.toSuccess("保存成功！");
		} catch (Exception e) {
			logger.error("保存背板数据失败!",e);
			return toJsonObject(700, "保存背板信息失败");
		}
	}
	
	/**
	 * 获取分类设备列表
	 * @return 
	 */
	@RequestMapping(value="devices",method={RequestMethod.POST,RequestMethod.GET})
	public String categoryDeviceInfo(){
		Set<Long> domainSet = this.getLoginUserDomains();
		return backbordApi.categoryDeviceInfo(domainSet);
	}
	
	/**
	 * 获取登录用户所属域集合
	 * @return
	 */
	private Set<Long> getLoginUserDomains(){
		ILoginUser user = getLoginUser();
		Set<IDomain> domains = user.getDomains();
		
		Set<Long> domainSet = new HashSet<Long>();
		for(IDomain domain:domains){
			domainSet.add(domain.getId());
		}
		return domainSet;
	}
	
	/**
	 * 过滤查询转为链路，获取所有设备接口
	 * @return
	 */
	@ResponseBody
	@RequestMapping("interface/all")
	public JSONArray getAllInterfaces(Long instanceId,String searchVal){
		return backbordApi.getInterfaceBySearchVal(instanceId, searchVal);
	}
	
	/**
	 * 获取所有设备接口
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="allInfs",method=RequestMethod.POST)
	public JSONArray getAllIfs(Long instanceId){
        JSONArray rst = backbordApi.getAllIfs2(instanceId);
		return rst;
	}
	
	@RequestMapping(value="{instanceId}/detailInfo")
	public String getDetailInfo(@PathVariable(value="instanceId") Long instanceId){
		return backbordApi.getDetailInfo(instanceId);
	}
	/**
	 * 获取接口tooltip信息
	 * @param instanceId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="tip",method=RequestMethod.POST)
	public JSONObject getPortTipInfo(Long instanceId){
		return backbordApi.getPortTipInfo(instanceId);
	}
	@ResponseBody
	@RequestMapping(value="downinfo",method=RequestMethod.POST)
	public JSONObject downinfo(Long mainInstanceId,Long subInstanceId){
		Assert.notNull(mainInstanceId);
		Assert.notNull(subInstanceId);
		return backbordApi.downDeviceInfo(mainInstanceId,subInstanceId);
	}
}
