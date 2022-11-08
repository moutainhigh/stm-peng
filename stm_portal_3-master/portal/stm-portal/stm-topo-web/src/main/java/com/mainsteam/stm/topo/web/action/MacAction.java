package com.mainsteam.stm.topo.web.action;


import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.topo.api.IIpMacAlarmTaskApi;
import com.mainsteam.stm.topo.api.IMacApi;
import com.mainsteam.stm.topo.api.ISettingApi;
import com.mainsteam.stm.topo.api.TopoAlarmExApi;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.bo.MacHistoryBo;
import com.mainsteam.stm.topo.bo.MacLatestBo;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * <li>设备IP_MAC_PORT管理</li>
 * <li>文件名称: MacAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年9月10日
 * @author zwx
 */
@Controller
@RequestMapping(value="/topo/mac")
public class MacAction extends BaseAction{
	
	private final Logger logger = LoggerFactory.getLogger(MacAction.class);
	@Autowired
	private IMacApi macApi;
	@Autowired
	private IIpMacAlarmTaskApi alarmTaskApi;
	@Autowired
	private TopoAlarmExApi alarmExApi;
	@Autowired
	private ISettingApi settingApi;
	
	/**
	 * 根据上联设备的ip和接口批量查询下联设备的ip-mac-port列表信息
	 * @return
	 */
	@RequestMapping("/updevice/batchInfos")
	public JSONObject getBatchUpDeviceMacInfos(String[] ipInterfaces){
		logger.info("批量查询下联设备参数：\n"+JSONObject.toJSONString(ipInterfaces));
		try{
			if(null != ipInterfaces && ipInterfaces.length > 0){
				List<JSONObject> list = macApi.getBatchUpDeviceMacInfos(ipInterfaces);
				return super.toSuccess(list);
			}else{
				return super.toJsonObject(700, "缺少查询参数");
			}
		}catch(Exception e){
			logger.error("MacAction方法getBatchUpDeviceMacInfos,根据上联设备的ip和接口批量查询下联设备的ip-mac-port列表信息发生异常",e);
			return super.toJsonObject(700, "查询数据异常");
		}
	}
	
	/**
	 * 根据上联设备的ip和接口查询下联设备的ip-mac-port列表信息
	 * @param ip
	 * @param intface
	 * @return
	 */
	@RequestMapping("/updevice/infos")
	public JSONObject getUpDeviceMacInfos(String ip,String intface){
		try{
			if(StringUtils.isNotBlank(ip) && StringUtils.isNotBlank(intface)){
				List<MacRuntimeBo> list = macApi.getUpDeviceMacInfos(ip, intface);
				Page<MacRuntimeBo, MacRuntimeBo> page = new Page<MacRuntimeBo, MacRuntimeBo>();
				page.setDatas(list);
				return super.toSuccess(page);
			}else{
				return super.toJsonObject(700, "缺少查询参数ip或intface");
			}
		}catch(Exception e){
			logger.error("MacAction方法getUpDeviceMacInfos,根据上联设备的ip和接口查询下联设备的ip-mac-port列表信息发生异常",e);
			return super.toJsonObject(700, "查询数据异常");
		}
	}
	
	/**
	 * 导入基准表数据excel
	 * @return
	 */
	@RequestMapping("/import")
	public String importBaseMacsExcel(MultipartFile file) {
		try {
			String rst = "数据导入成功";
			if(null != file){
				int flag = macApi.importMacExcel(file);
				if(1 == flag){
					rst = "不是标准模板,请使用导出的模板编辑后再导入";
				}
			}else {
				rst = "未选择基准表excel";
				logger.info(rst);
			}
			return super.toSuccess(rst).toJSONString();
		} catch (Exception e) {
			logger.error("导入基准MAC数据失败!",e);
			return toJsonObject(700, "数据导入失败").toJSONString();
		}
	}
	
	/**
	 * 导出基准表数据
	 * @param ids
	 * @param exportType (selected:导出选择数据，all：导出所有数据)
	 * @return
	 */
	@RequestMapping("/export")
	public JSONObject exportBaseMacs(Long[] ids,String exportType,HttpServletResponse response) {
		try {
			Workbook wb = macApi.exportBaseMacs(ids,exportType);
			export(wb,response);
			return super.toSuccess("数据导出成功");
		} catch (Exception e) {
			logger.error("导出基准MAC数据失败!",e);
			return toJsonObject(700, "导出基准MAC数据失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 输出excel
	 * @param wb
	 * @throws IOException 
	 */
	private void export(Workbook wb,HttpServletResponse response) throws IOException{
		String fileName = new String("基准表".getBytes("gbk"), "iso-8859-1");
		response.setContentType("application/vnd.ms-excel") ;
		response.setHeader("Content-Disposition","attachment;filename="+fileName+".xls");
		OutputStream os = response.getOutputStream();

		os.flush();
  		wb.write(os);
		os.close();
	}
	
	/**
	 * 刷新新增mac数据
	 * @return
	 */
	@RequestMapping("/latest/refresh")
	public JSONObject refreshLatestMacs() {
		try {
			macApi.refreshLatestIpPortMac();
			return super.toSuccess("数据刷新中，请耐心等待……");
		} catch (Exception e) {
			logger.error("刷新新增MAC数据失败!",e);
			return toJsonObject(700, "刷新数据失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 开启ip-mac-port告警调度job
	 * TODO:预留的启动job功能（暂时未使用）
	 * @return
	 */
	@RequestMapping(value="/alarm/job/start",method=RequestMethod.GET)
	public String startAlarmJob() {
		try {
			Integer groupId = settingApi.getTopoFindGroupId();
			if(null == groupId){
				return "拓扑发现未配置数据采集器DCS,无法采集数据";
			}else{
				alarmTaskApi.startIpMacAlarmJobTask();
				return "告警调度已开启,系统会自动分析发送告警信息。";
			}
		} catch (Exception e) {
			logger.error("开启ip-mac-port告警调度job失败!",e);
			return "开启ip-mac-port告警调度job失败";
		}
	}
	
	/**
	 * 删除ip-mac-port告警调度job
	 * TODO:预留的删除job功能（暂时未使用）
	 * @return
	 */
	@RequestMapping(value="/alarm/job/del",method=RequestMethod.GET)
	public String delAlarmJob() {
		try {
			alarmTaskApi.delIpMacAlarmJobTask();
			return "删除ip-mac-port告警调度job成功";
		} catch (Exception e) {
			return "删除ip-mac-port告警调度job失败"+e.getMessage();
		}
	}
	
	/**
	 * 刷新mac实时数据
	 * @return
	 */
	@RequestMapping("/refresh")
	public JSONObject refreshMacs() {
		try {
			Integer groupId = settingApi.getTopoFindGroupId();
			if(null == groupId){
				return super.toSuccess("拓扑发现未配置数据采集器DCS");
			}else{
				alarmTaskApi.refreshIpMacPort(groupId);
				return super.toSuccess("数据刷新可能要几分钟，请耐心等待后刷新页面……");
			}
		} catch (Exception e) {
			logger.error("刷新MAC数据失败!",e);
			return toJsonObject(700, "刷新数据失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 获取告警人
	 * @return
	 */
	@RequestMapping("/warn/senders")
	public JSONObject getWarnSenders(String alarmType,String type) {
		try {
			List<User> users = alarmExApi.getAlarmSenders(alarmType,type);
			Page<User, User> page = new Page<User, User>();
			page.setDatas(users);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询系统所有用户列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 获取ip-mac-port告警配置信息
	 */
	@RequestMapping("/ipMacAlarmSetting")
	public String getAlarmInfo(){
		String retn = macApi.getAlarmSetting("ipMacAlarmSetting");
		return retn;
	}
	
	/**
	 * 删除mac历史变更数据
	 * @param ids
	 * @return
	 */
	@RequestMapping("/history/delIds")
	public JSONObject deleteHistoryByIds(Long[] ids) {
		try {
			macApi.deleteMacHistoryByIds(ids);
			return super.toSuccess("删除成功");
		} catch (Exception e) {
			logger.error("删除MAC历史变更表数据失败!",e);
			return toJsonObject(700, "删除失败");
		}
	}
	 
	/**
	 * 删除mac历史变更数据
	 * @param macs
	 * @return
	 */
	@RequestMapping("/history/delMacs")
	public JSONObject deleteHistoryMac(String[] macs) {
		try {
			macApi.deleteMacHistoryByMacs(macs);
			return super.toSuccess("删除成功");
		} catch (Exception e) {
			logger.error("删除MAC历史变更表数据失败",e);
			return toJsonObject(700, "删除失败");
		}
	}
	
	/**
	 * 保存基本表数据
	 * @param macBaseBo
	 * @return
	 */
	@RequestMapping("/base/save")
	public JSONObject saveBaseMac(MacBaseBo macBaseBo) {
		try {
			macApi.saveBaseMac(macBaseBo);
			return super.toSuccess("保存成功！");
		} catch (Exception e) {
			logger.error("保存基准表数据失败!",e);
			return toJsonObject(700, "保存失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 删除基本表数据
	 * @param ids
	 * @return
	 */
	@RequestMapping("/base/del")
	public JSONObject deleteBaseMac(Long[] ids) {
		try {
			macApi.deleteBaseMac(ids);
			return super.toSuccess("删除成功");
		} catch (Exception e) {
			logger.error("删除基准表数据失败!",e);
			return toJsonObject(700, "删除失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 全部加入基准表
	 */
	@RequestMapping("/addbse/all")
	public JSONObject addAllTobase() {
		try {
			macApi.addAllBaseMac();
			return super.toSuccess("全部加入成功");
		} catch (Exception e) {
			logger.error("新增MAC数据全部加入基准表失败!",e);
			return toJsonObject(700, "加入失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 加入基准表前验证是否已经加入
	 * @param ids
	 * @param addType
	 * @return
	 */
	@RequestMapping("/exist")
	public JSONObject checkMacExist(Long[] ids,String addType) {
		try {
			boolean exist = macApi.checkMacExist(ids, addType);
			if(exist){
				return super.toSuccess("exist");
			}else {
				return this.addTobase(ids, addType);
			}
		} catch (Exception e) {
			logger.error("实时表加入基准表失败!",e);
			e.printStackTrace();
			return toJsonObject(700, "加入失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 加入基准表
	 * @param ids
	 * @param addType(runtime:实时表,history:历史表,latest:新增mac表)
	 * @return
	 */
	@RequestMapping("/addbse")
	public JSONObject addTobase(Long[] ids,String addType) {
		try {
			macApi.addBaseMac(ids, addType);
			return super.toSuccess("加入成功");
		} catch (Exception e) {
			logger.error("实时表加入基准表失败!",e);
			return toJsonObject(700, "加入失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 分页查询[历史变更]列表数据
	 * @param page
	 * @param conditions
	 * @return
	 */
	@RequestMapping("/subHistory/list")
	public JSONObject getSubHistoryPageList(Page<MacHistoryBo, MacHistoryBo> page, MacHistoryBo params) {
		try {
			page.setCondition(params);
			macApi.selectMacSubHistoryByPage(page);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询MAC历史变更记录列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 分页查询[历史变更]最新数据列表数据
	 * @param page
	 * @param conditions
	 * @return
	 */
	@RequestMapping("/history/list")
	public JSONObject getHistoryPageList(Page<MacHistoryBo, MacHistoryBo> page, MacHistoryBo params) {
		try {
			//封装查询条件
			page.setCondition(params);
			//查询数据
			macApi.selectMacHistoryByPage(page);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询MAC历史变更列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 分页查询[新增MAC]列表数据
	 * @param page
	 * @param conditions
	 * @return
	 */
	@RequestMapping("/newmac/list")
	public JSONObject getLatestMacPageList(Page<MacLatestBo, MacLatestBo> page, MacLatestBo params) {
		try {
			//封装查询条件
			page.setCondition(params);
			//查询数据
			macApi.selectLatestMacByPage(page);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询新增MAC列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 分页查询[基准表]列表数据
	 * @param page
	 * @param conditions
	 * @return
	 */
	@RequestMapping("/base/list")
	public JSONObject getMacBasePageList(Page<MacBaseBo, MacBaseBo> page, MacBaseBo params) {
		try {
			//封装查询条件
			page.setCondition(params);
			//查询数据
			macApi.selectMacBaseByPage(page);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询基准表列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 分页查询[实时表]列表数据
	 * @param page
	 * @param conditions
	 * @return
	 */
	@RequestMapping("/runtime/list")
	public JSONObject getRuntimePageList(Page<MacRuntimeBo, MacRuntimeBo> page, MacRuntimeBo params) {
		try {
			//封装查询条件
			page.setCondition(params);
			//查询数据
			macApi.selectMacRuntimeByPage(page);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询实时表列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}

    @RequestMapping("/updateUpRemarks")
    public JSONObject updateUpRemarks(Long id, String upRemarks) {
        try {
            boolean b = macApi.updateUpRemarks(id, upRemarks);
            return super.toSuccess(b);
        } catch (Exception e) {
            logger.error("更新上联备注信息失败!", e);
            return toJsonObject(700, "更新失败");
        }
    }
}
