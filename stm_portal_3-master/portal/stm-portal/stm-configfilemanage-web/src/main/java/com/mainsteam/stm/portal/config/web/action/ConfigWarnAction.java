package com.mainsteam.stm.portal.config.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.export.excel.ExcelHeader;
import com.mainsteam.stm.export.excel.ExcelUtil;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.portal.config.api.IConfigWarnApi;
import com.mainsteam.stm.portal.config.bo.ConfigWarnBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnResourceBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnRuleBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnViewBo;
import com.mainsteam.stm.portal.config.vo.ConditionVo;
import com.mainsteam.stm.portal.config.web.vo.DeviceResourceVo;
/**
 * <li>文件名称: ConfigWarnAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月23日
 * @author   caoyong
 */
@Controller
@RequestMapping("/portal/config/warn/config")
public class ConfigWarnAction extends BaseAction {
	private Logger logger = Logger.getLogger(ConfigWarnAction.class);
	@Autowired
	private IConfigWarnApi configWarnApi;
	@Autowired
	private CapacityService capacityService;
	
	/**
	 * 告警设置分页数据
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/getPage", method=RequestMethod.POST)
	public JSONObject getPage(Page<ConfigWarnBo, ConfigWarnBo> page){
		try {
			configWarnApi.selectByPage(page);
			logger.info("portal.config.warn.config.getPage successful");
		} catch (Exception e) {
			logger.error("portal.config.warn.config.getPage failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(page);
	}
	/**
	 * 查询告警一览分页数据
	 * @param page
	 * @param all 是否全部数据(true所有,false当前页数据)
	 * @param export(是否导出Excel操作：true/false)
	 * @throws Exception
	 */
	@RequestMapping(value="/getWarnViewPage")
	public JSONObject getWarnViewPage(Page<ConfigWarnViewBo, ConfigWarnViewBo> page,ConditionVo condition){
		try {
			configWarnApi.selectWarnViewPage(page,false,false,condition);
			logger.info("portal.config.warn.config.getWarnViewPage successful");
		} catch (Exception e) {
			logger.error("portal.config.warn.config.getWarnViewPage failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(page);
	}
	/**
	 * 添加告警
	 * @param configWarnBo
	 * @return
	 */
	
	@RequestMapping("/addWarn")
	public JSONObject addWarn(String data) {
		try {
			int count = configWarnApi.insert(this.convertDataToConfigWarnBo(data));
			logger.info("portal.config.warn.config.addWarn successful");
			if(count == -1){
				return toFailForGroupNameExsit(null);
			}else{
				return toSuccess(count);
			}
		} catch (Exception e) {
			logger.error("portal.config.warn.config.addWarn failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "添加告警失败");
		}
	}
	
	/**
	 * 编辑告警
	 * @param bo
	 * @return
	 */
	@RequestMapping("/updateWarn")
	public JSONObject updateWarn(String data) {
		try {
			int count = configWarnApi.update(this.convertDataToConfigWarnBo(data));
			logger.info("portal.config.warn.config.updateWarn successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.config.warn.config.updateWarn failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "编辑告警失败");
		}
	}
	
	/**
	 * 获取告警详情
	 * @param id
	 * @return
	 */
	@RequestMapping("/getWarn")
	public JSONObject getWarn(long id){
		try {
			ConfigWarnBo bo = configWarnApi.get(id);
			logger.info("portal.config.warn.config.getWarn successful");
			return toSuccess(bo);
		} catch (Exception e) {
			logger.error("portal.config.warn.config.getWarn failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "获取告警失败");
		}
	}
	
	/**
	 * 获取右侧数据
	 * @param id
	 * @return
	 */
	@RequestMapping("/getRightResourceInstanceList")
	public JSONObject getRightResourceInstanceList(Long id){
		List<ResourceInstance> resourceInstanceList = null;
		Page<DeviceResourceVo, Object> page = new Page<DeviceResourceVo, Object>();
		try {
			resourceInstanceList = configWarnApi.getRightResourceInstanceList(id);
			page.setDatas(this.convertInstances2DeviceResourceVo(resourceInstanceList));
			logger.info("portal.config.warn.config.getRightResourceInstanceList successful");
		} catch (Exception e) {
			logger.error("portal.config.warn.config.getRightResourceInstanceList failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(JSONObject.toJSON(page));
	}
	
	/**
	 * 获取左侧数据
	 * @param id
	 * @param searchKey
	 * @return
	 */
	@RequestMapping("/getLeftResourceInstanceList")
	public JSONObject getLeftResourceInstanceList(Long id, String searchKey){
		List<ResourceInstance> resourceInstanceList = null;
		Page<DeviceResourceVo, Object> page = new Page<DeviceResourceVo, Object>();
		try {
			resourceInstanceList = configWarnApi.getLeftResourceInstanceList(id, searchKey);
			page.setDatas(this.convertInstances2DeviceResourceVo(resourceInstanceList));
			logger.info("portal.config.warn.config.getLeftResourceInstanceList successful");
		} catch (Exception e) {
			logger.error("portal.config.warn.config.getLeftResourceInstanceList failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(JSONObject.toJSON(page));
	}
	
	/**
	 * 资源实例集合转换成配置文件管理设备vo
	 * @param instances
	 * @return
	 */
	private List<DeviceResourceVo> convertInstances2DeviceResourceVo(List<ResourceInstance> instances){
		List<DeviceResourceVo> returnList = new ArrayList<DeviceResourceVo>();
		for(int i=0;i<instances.size();i++){
			DeviceResourceVo vo = new DeviceResourceVo();
			ResourceInstance instance = instances.get(i);
			if(null == instance) continue;
			vo.setId(instances.get(i).getId());
			vo.setResourceTypeId(capacityService.getResourceDefById(instance.getResourceId()).getName());
			vo.setIntanceName(instance.getShowName());
			vo.setIpAddress(instance.getShowIP());
			returnList.add(vo);
		}
		return returnList;
	}
	
	/**
	 * 根据告警id查询所有的关联的接收规则（接收人和接收方式）
	 * @param id
	 * @return
	 */
	@RequestMapping("/getWarnRulesById")
	public JSONObject getWarnRulesById(Long id){
		Page<ConfigWarnRuleBo, ConfigWarnRuleBo> page = new Page<ConfigWarnRuleBo, ConfigWarnRuleBo>();
		try {
			List<ConfigWarnRuleBo> datas = configWarnApi.getWarnRulesById(id);
			if(datas==null){
				datas = new ArrayList<ConfigWarnRuleBo>();
			}
			page.setDatas(datas);
			page.setTotalRecord(datas.size());
			logger.info("portal.config.warn.config.getWarnRulesById successful");
		} catch (Exception e) {
			logger.error("portal.config.warn.config.getWarnRulesById failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(page);
	}
	
	/**
	 * 转换前端接收的json字符串为configWarnBo
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private ConfigWarnBo convertDataToConfigWarnBo(String data){
		ConfigWarnBo bo = new ConfigWarnBo();
		Map map = JSONObject.parseObject(data, HashMap.class);
		String name = map.get("name").toString();
		if(null!=map.get("id")){
			bo.setId(Long.parseLong(map.get("id").toString()));
		}
		String configWarnResourceBos = map.get("configWarnResourceBos").toString();
		String configWarnRuleBos = map.get("configWarnRuleBos").toString();
		List<ConfigWarnResourceBo> resourceBos = JSONObject.parseArray(configWarnResourceBos, ConfigWarnResourceBo.class);
		List<ConfigWarnRuleBo> ruleBos = JSONObject.parseArray(configWarnRuleBos, ConfigWarnRuleBo.class);
		bo.setName(name);
		bo.setConfigWarnResourceBos(resourceBos);
		bo.setConfigWarnRuleBos(ruleBos);
		return bo;
	}
	/**
	 * 根据告警id批量删除告警设置
	 * @param ids
	 * @return
	 */
	@RequestMapping("/batchDel")
	public JSONObject batchDel(Long[] ids){
		try {
			int count = configWarnApi.batchDel(ids);
			logger.info("portal.config.warn.config.batchDel successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.config.warn.config.batchDel failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "删除告警失败");
		}
	}
	/**
	 * 导出告警一览excel
	 * @param response 响应
	 * @param page 分页对象
	 * @param all 是否导出全部
	 * @param rowCount 分页当前页条数
	 * @param startRow 分页当前页开始数
	 * @param total    分页全部条数
	 * @throws IOException 
	 */
	@RequestMapping(value="exportWarnView")
	public void exportWarnView(HttpServletResponse response, HttpServletRequest request,Page<ConfigWarnViewBo, ConfigWarnViewBo> page,
			boolean all,Long rowCount,Long startRow,Long total,ConditionVo condition) throws IOException{
		ExcelUtil<ConfigWarnViewBo> exportUtil = new ExcelUtil<ConfigWarnViewBo>();
		List<ExcelHeader> headers = new ArrayList<>();
//		headers.add(new ExcelHeader("id","主键ID"));
		headers.add(new ExcelHeader("name","设备名称"));
		headers.add(new ExcelHeader("ipAddress","IP地址"));
		headers.add(new ExcelHeader("content","告警内容"));
		headers.add(new ExcelHeader("userNames","接收人"));
		headers.add(new ExcelHeader("warnTime","告警时间"));
		try {
			page.setRowCount(rowCount);
			page.setStartRow(startRow);
			page.setTotalRecord(total);
			configWarnApi.selectWarnViewPage(page,all,true,condition);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		List<ConfigWarnViewBo> dataset = page.getDatas();
		WebUtil.initHttpServletResponse("告警一览.xlsx", response, request);
		exportUtil.exportExcel("告警一览", headers, dataset, response.getOutputStream());
	}
	
	@RequestMapping(value="exportCheckData")
	public JSONObject exportCheckData(HttpServletResponse response, HttpServletRequest request,Page<ConfigWarnViewBo, ConfigWarnViewBo> page,
			boolean all,Long rowCount,Long startRow,Long total,ConditionVo condition) throws IOException{
		try {
			page.setRowCount(rowCount);
			page.setStartRow(startRow);
			page.setTotalRecord(total);
			configWarnApi.selectWarnViewPage(page,all,true,condition);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return toSuccess(false);
		}
		List<ConfigWarnViewBo> dataset = page.getDatas();
		if(null==dataset || dataset.size()==0){
			return toSuccess(false);
		}else{
			return toSuccess(true);
		}
	}
}
