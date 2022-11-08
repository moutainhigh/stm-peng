package com.mainsteam.stm.portal.config.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.config.api.IConfigScriptApi;
import com.mainsteam.stm.portal.config.api.IConfigScriptDirectoryApi;
import com.mainsteam.stm.portal.config.bo.ConfigScriptBo;
import com.mainsteam.stm.portal.config.bo.ConfigScriptDirectoryBo;
import com.mainsteam.stm.portal.config.bo.ConfigScriptLevelEnum;
import com.mainsteam.stm.portal.config.vo.ScriptVo;
import com.mainsteam.stm.portal.config.web.vo.ConfigScriptTreeVo;

/**
 * 
 * <li>文件名称: ConfigScriptManagementAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月24日
 * @author   tongpl
 */
@Controller
@RequestMapping("/portal/config/configscriptmanage")
public class ConfigScriptManagementAction extends BaseAction{
	@Resource
	private IConfigScriptApi configScriptApi;
	@Resource
	private IConfigScriptDirectoryApi configScriptDirectoryApi;
	
	/**
	 * 获取所有资源实例
	 * @return
	 */
	@RequestMapping("/getTreeView")
	public JSONObject getTreeView(){
		
		List<ConfigScriptDirectoryBo> csdList = configScriptDirectoryApi.getAllConfigScriptDirectory();
		if(null==csdList || csdList.size()==0){
			//用户未创建文件夹,初始化
			ConfigScriptDirectoryBo csdb = new ConfigScriptDirectoryBo();
			csdb.setLevelEnum(ConfigScriptLevelEnum.ONE);
			csdb.setName("备份恢复脚本");
			csdb.setParentId(-1);
			configScriptDirectoryApi.addORupdate(csdb);
			
			configScriptApi.synXmlData(csdb.getId());
			
			csdList = configScriptDirectoryApi.getAllConfigScriptDirectory();
		}else{
			for(ConfigScriptDirectoryBo csdb:csdList){
				if(csdb.getLevelEnum()==ConfigScriptLevelEnum.ONE){
					configScriptApi.synXmlData(csdb.getId());
					break;
				}
			}
			
		}
		List<ConfigScriptBo> csbList = configScriptApi.getAllConfigScript();
		ConfigScriptTreeVo cstv = new ConfigScriptTreeVo(csbList,csdList,true);
		
		return toSuccess(cstv.getChildren());
	}
	
	/**
	 * 模糊查询配置脚本文件
	 * @return
	 */
	@RequestMapping("/queryConfigScript")
	public JSONObject queryConfigScript(String queryCode,boolean flag){
		ConfigScriptBo csb = new ConfigScriptBo();
		csb.setName(queryCode);
		csb.setOid(queryCode);
		List<ConfigScriptBo> csbList = configScriptApi.queryByBo(csb);
		List<ConfigScriptDirectoryBo> csdList = configScriptDirectoryApi.getAllConfigScriptDirectory();
		
		ConfigScriptTreeVo cstv = new ConfigScriptTreeVo(csbList,csdList,flag);
		return toSuccess(cstv.getChildren());
	}
	
	/**
	 * check OID
	 * @return
	 */
	@RequestMapping("/getConfigScriptByOID")
	public JSONObject getConfigScriptByOID(String oid){
		ConfigScriptBo csb = new ConfigScriptBo();
		csb.setOid(oid);
		List<ConfigScriptBo> csbList = configScriptApi.queryEqualsByBo(csb);
		if(null!=csbList && csbList.size()>0){
			return toSuccess(csbList);
		}
		return toSuccess(null);
	}
	
	/**
	 * 添加目录
	 * @return
	 */
	@RequestMapping("/addConfigScriptDirectory")
	public JSONObject addConfigScriptDirectory(String name,long parentId,int level,long directoryId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		ConfigScriptDirectoryBo csdb = new ConfigScriptDirectoryBo();
		csdb.setName(name);
		if(directoryId>0){
			csdb.setId(directoryId);
		}else{
			//检察name是否有重复
			ConfigScriptDirectoryBo csdbTemp = configScriptDirectoryApi.selectByName(name);
			if(null!=csdbTemp){
				resultMap.put("nameIsRepeat", true);
				return toSuccess(resultMap);
			}
			csdb.setParentId(parentId);
			csdb.setDirLevel(level);
		}
		resultMap.put("saveState", configScriptDirectoryApi.addORupdate(csdb));
		return toSuccess(resultMap);
	}
	
	/**
	 * 添加脚本
	 * @return
	 */
	@RequestMapping("/addConfigScript")
	public JSONObject addConfigScript(String name,String oid,long id,long scriptId){
		ConfigScriptBo csb = new ConfigScriptBo();
		csb.setName(name);
		csb.setOid(oid);
		if(scriptId>0){
			csb.setId(scriptId);
		}else{
			csb.setDirectoryId(id);
		}
		
		return toSuccess(configScriptApi.addORupdate(csb));
	}
	
	/**
	 * 恢复,备份脚本
	 * @return
	 */
	@RequestMapping("/addConfigScriptModelByType")
	public JSONObject addConfigScriptModelByType(String saveType,long configScriptId,String fileName,String type,String cmd){
		boolean flag = false;
		switch(saveType){
		case "backUp" :
			flag = configScriptApi.updateConfigScriptBackUpScript(configScriptId, fileName, type, cmd);
			break;
		case "recovery" :
			flag = configScriptApi.updateConfigScriptRecoveryScript(configScriptId, fileName, type, cmd);
			break;
		}
		
		return toSuccess(flag);
	}
	
	/**
	 * 删除恢复,备份脚本
	 * @return
	 */
	@RequestMapping("/delConfigScriptModelByType")
	public JSONObject delConfigScriptModelByType(String saveType,long configScriptId,String[] fileNameArr){
		boolean flag = false;
		switch(saveType){
		case "backUp" :
			flag = configScriptApi.delBackUpModelScript(configScriptId, fileNameArr);
			break;
		case "recovery" :
			flag = configScriptApi.delRecoveryModelScript(configScriptId, fileNameArr);
			break;
		}
		
		return toSuccess(flag);
	}
	
	/**
	 * 删除配置
	 * @return
	 */
	@RequestMapping("/delConfigScriptById")
	public JSONObject delConfigScriptById(long configScriptId){
		boolean flag = configScriptApi.deleteConfigScriptById(configScriptId);
		
		return toSuccess(flag);
	}
	
	/**
	 * 删除配置
	 * @return
	 */
	@RequestMapping("/delConfigScriptDirectory")
	public JSONObject delConfigScriptDirectory(long configScriptDirectoryId,String delType){
		boolean flag = true;
		switch (delType) {
		case "delAll":
			flag = configScriptDirectoryApi.deleteConfigScriptDirectoryALL(configScriptDirectoryId);
			break;
		case "directoryOnly":
			flag = configScriptDirectoryApi.deleteConfigScriptDirectoryONLY(configScriptDirectoryId);
			break;
		}
		
		
		return toSuccess(flag);
	}
	
	/**
	 * 根据ConfigScriptId获取恢复,备份脚本
	 * @return
	 */
	@RequestMapping("/getConfigScriptModelByConfigScriptId")
	public JSONObject getConfigScriptModelByConfigScriptId(long configScriptId){
		
		Map<String,List<ScriptVo>> scriptListMap = configScriptApi.getConfigScriptModelByConfigScriptId(configScriptId);
		
		return toSuccess(scriptListMap);
	}
	
	/**
	 * 根据ConfigScriptId获取脚本基本信息
	 * @return
	 */
	@RequestMapping("/getConfigScriptByConfigScriptId")
	public JSONObject getConfigScriptByConfigScriptId(long configScriptId){
		
		ConfigScriptBo csb = configScriptApi.getConfigScriptById(configScriptId);
				
		
		return toSuccess(csb);
	}
	
	/**
	 * 根据ConfigDirectoryId获取目录基本信息
	 * @return
	 */
	@RequestMapping("/getConfigDirectoryByConfigDirectoryId")
	public JSONObject getConfigDirectoryByConfigDirectoryId(long configDirectoryId){
		
		ConfigScriptDirectoryBo csdb = configScriptDirectoryApi.getConfigScriptDirectoryById(configDirectoryId);
		
		return toSuccess(csdb);
	}
	
	/**
	 * 获取脚本script
	 * @return
	 */
	@RequestMapping("/getConfigScript")
	public JSONObject getConfigScript(String scriptName,long scriptConfigId,String scriptType){
		
		ScriptVo spt = configScriptApi.getModelScript(scriptConfigId, scriptName, scriptType);
		
		return toSuccess(spt);
	}
}
