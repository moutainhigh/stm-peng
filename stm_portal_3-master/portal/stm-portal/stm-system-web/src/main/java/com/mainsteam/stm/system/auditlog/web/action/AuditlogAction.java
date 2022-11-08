package com.mainsteam.stm.system.auditlog.web.action;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.excel.ExcelHeader;
import com.mainsteam.stm.export.excel.ExcelUtil;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.auditlog.api.IAuditlogApi;
import com.mainsteam.stm.system.auditlog.api.IModualActionMethodApi;
import com.mainsteam.stm.auditlog.bo.AuditlogBo;
import com.mainsteam.stm.auditlog.bo.AuditlogQueryBo;
import com.mainsteam.stm.auditlog.bo.AuditlogRuleBo;
import com.mainsteam.stm.system.auditlog.bo.MethodEntity;
import com.mainsteam.stm.system.license.api.ILicenseApi;
import com.mainsteam.stm.system.license.bo.PortalModule;
@Controller
@RequestMapping("/system/auditlog")
public class AuditlogAction extends BaseAction{
	
	@Resource(name="stm_system_AuditlogApi")
	private IAuditlogApi stm_system_AuditlogApi;
	
	@Autowired
	private IModualActionMethodApi modualActionMethodApi;
	
	
	@Autowired
	private ILicenseApi licenseApi;
	
	@RequestMapping("/get")
	public JSONObject get(long id){
		AuditlogBo auditlogBo=stm_system_AuditlogApi.get(id);
		return toSuccess(auditlogBo);
	}
	
	/**
	* @Title: getOperModule
	* @Description: 获取审计日志操作模块
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getOperModule")
	public JSONObject getOperModule(){
		List<MethodEntity> modules = modualActionMethodApi.getMethodEntityList();
		List<PortalModule> portalModules = null;
		try {
			portalModules = licenseApi.getPortalModules();
		} catch (LicenseCheckException e) {
		}
		if(portalModules!=null){
			List<MethodEntity> delModules = new ArrayList<MethodEntity>();
			for (MethodEntity module : modules) {
				boolean flag = true;
				for (PortalModule portalModule : portalModules) {
					if(module.getModuleId().equals(String.valueOf(portalModule.getId())) && !portalModule.isAuthor()){
						flag = false;
					}
				}
				if(!flag){
					delModules.add(module);
				}
			}
			modules.removeAll(delModules);
		}
		return toSuccess(modules);
	}
	@RequestMapping("/batchDel")
	public JSONObject batchDel(long[] ids){
		int count=stm_system_AuditlogApi.batchDel(ids);
		return toSuccess(count);
	}
	@RequestMapping("/pageSelect")
	public JSONObject pageSelect(Page<AuditlogBo, AuditlogQueryBo> page,AuditlogQueryBo condition){
		if(condition!=null){
			if(condition.getKeyword()!=null&&condition.getKeyword().equals("搜索用户名/IP/操作对象")){
				
				condition.setKeyword("");
			}
			page.setCondition(condition);
		}
		stm_system_AuditlogApi.pageSelect(page);
		return toSuccess(page);
	}
	
	@RequestMapping(value="exportlog")
	public void exportLog(HttpServletResponse response, HttpServletRequest request,AuditlogQueryBo condition) throws IOException{
		ExcelUtil<AuditlogBo> exportUtil = new ExcelUtil<AuditlogBo>();
		List<ExcelHeader> headers = new ArrayList<>();
		headers.add(new ExcelHeader("oper_date","操作时间"));
		headers.add(new ExcelHeader("oper_user","操作人"));
		headers.add(new ExcelHeader("oper_ip","客户端IP"));
		headers.add(new ExcelHeader("oper_module","操作模块"));
		headers.add(new ExcelHeader("oper_type","操作"));
		headers.add(new ExcelHeader("oper_object","操作对象"));
		if(condition!=null){
			try {
				condition.setBeginDate(condition.getBeginDate()==null?null:URLDecoder.decode(condition.getBeginDate(),"utf-8"));
				condition.setEndDate(condition.getEndDate()==null?null:URLDecoder.decode(condition.getEndDate(),"utf-8"));
				condition.setKeyword(condition.getKeyword()==null?null:URLDecoder.decode(condition.getKeyword(),"utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		List<AuditlogBo> dataset = stm_system_AuditlogApi.selectAllList(condition);
		if(dataset.size()==0){
			AuditlogBo ab = new AuditlogBo();
			dataset.add(ab);
		}
		WebUtil.initHttpServletResponse("审计日志.xlsx", response, request);
		exportUtil.exportExcel("审计日志", headers, dataset, response.getOutputStream());
	}
	/**
	 * 删除选中日志
	 * @return
	 * @param ids
	 * @author jinshengkai
	 */
	@RequestMapping("/deleteSelect")
	public JSONObject deleteSelect(long[] id){
		int result = stm_system_AuditlogApi.deleteSelect(id);
		String flag = "";
		if(result==1){
			flag = "TRUE";
		}else{
			flag = "FALSE";
		}
		return toSuccess(flag);
	}
	
	/**
	 * 清空日志
	 * @return
	 * @author jinshengkai
	 */
	@RequestMapping("/deleteAll")
	public JSONObject deleteAll(){
		int result = stm_system_AuditlogApi.deleteAll();
		String flag = "";
		if(result==1){
			flag = "TRUE";
		}else{
			flag = "FALSE";
		}
		return toSuccess(flag);
	}
	
	/**
	 * 备份审核日志
	 * @return
	 * @author jinshengkai
	 */
	@RequestMapping("/backUpAudit")
	public JSONObject backUpAudit(){
		try{
			stm_system_AuditlogApi.insertBuAuditlog();
		}catch(Exception e){
			e.printStackTrace();
		}
		return toSuccess("TRUE");
	}
	
	/**
	 * 获取备份列表
	 * @param page
	 * @param condition
	 * @return
	 */
	@RequestMapping("/getBuList")
	public JSONObject getBuList(Page<AuditlogBo, AuditlogQueryBo> page,AuditlogQueryBo condition){
		if(condition!=null){
			if(condition.getKeyword()!=null){
				condition.setKeyword("");
			}
			page.setCondition(condition);
		}
		stm_system_AuditlogApi.queryBuList(page);
		return toSuccess(page);
	}
	/**
	 * 恢复
	 * @param oper_date
	 * @return
	 */
	@RequestMapping("/batchUpdate")
	public JSONObject batchUpdate(String oper_date){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date butime = null;
		try {
			//上次的备份时间
			butime = format.parse(oper_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AuditlogBo auditlogBo = new AuditlogBo();
		auditlogBo.setOper_date(butime);
		int result = stm_system_AuditlogApi.updateAuditlog(auditlogBo);
		String flag = "";
		if(result>=1){
			flag = "TRUE";
		}else{
			flag = "FALSE";
		}
		return toSuccess(flag);
	}
	
	/**
	 * 修改备份规则
	 * @param auditlogRule
	 * @return
	 */
	@RequestMapping("/insertRule")
	public JSONObject insertRule(String auditlogRule) throws Exception{
		if(auditlogRule.length()==0){
			return null;
		}
		AuditlogRuleBo auditlogRuleBo = JSONObject.parseObject(auditlogRule, AuditlogRuleBo.class);
		Date thisTime = new Date();
		auditlogRuleBo.setId((long) 1);
		if(auditlogRuleBo.isOpen()){
			auditlogRuleBo.setStatus("0");
		}else{
			auditlogRuleBo.setStatus("1");
		}
		auditlogRuleBo.setOpen_date(thisTime);
		auditlogRuleBo.setBackup_time("[{\"hour\":\""+auditlogRuleBo.getBackupDateHour()+"\",\"minute\":\""+auditlogRuleBo.getBackupDateMinute()+"\"}]");
		AuditlogRuleBo auditlogRuleBos = stm_system_AuditlogApi.selectAuditlogRule();
		String flag = "FALSE";
		int result = 0;
		if(auditlogRuleBos.getStatus()!=null){
			//update
			result = stm_system_AuditlogApi.updateAuditlogRule(auditlogRuleBo);
			if(result>=1){
				flag = "TRUE";
			}
		}
		return toSuccess(flag);
	}
	
	/**
	 * 获取自动备份规则
	 * @return
	 */
	@RequestMapping("/getRule")
	public JSONObject getRule() throws Exception{
		AuditlogRuleBo auditlogRuleBos = stm_system_AuditlogApi.selectAuditlogRule();
		if(auditlogRuleBos.getStatus().equals("0")){
			auditlogRuleBos.setOpen(true);
		}
		return toSuccess(auditlogRuleBos);
	}
}
