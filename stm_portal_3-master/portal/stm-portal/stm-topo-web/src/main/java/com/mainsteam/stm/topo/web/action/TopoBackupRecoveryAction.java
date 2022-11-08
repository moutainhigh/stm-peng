package com.mainsteam.stm.topo.web.action;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.bean.FileModelQuery;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.ITopoBackupRecoveryApi;


/**
 * <li>拓扑模块数据备份恢复</li>
 * <li>文件名称: TopoBackupRecoveryAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年4月14日
 * @author zwx
 */
@Controller
@RequestMapping(value="/topo/datas")
public class TopoBackupRecoveryAction extends BaseAction{
	
	private final Logger logger = LoggerFactory.getLogger(TopoBackupRecoveryAction.class);
	private final String TOPO_FILE_GROUP = "STM_TOPO_BACKUP";	//拓扑文件上传组名称
	@Autowired
	private ITopoBackupRecoveryApi backupRecoveryApi;
	
	/**
	 * 删除已备份的数据文件
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public JSONObject delete(Long[] ids) {
		try {
			backupRecoveryApi.deleteFiles(ids);
			return super.toSuccess("删除成功！");
		} catch (Exception e) {
			logger.error("删除拓扑备份数据失败",e);
			return toJsonObject(700, "删除失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * @param recoveryFile
	 * @return
	 */
	@RequestMapping(value="/recovery", method=RequestMethod.POST)
	public JSONObject recovery(Long fileId){
		try {
			if(null != fileId){
				backupRecoveryApi.deleteAndRecovery(fileId);
			}else {
				logger.error("未选择要恢复的文件!");
				return toJsonObject(700, "未选择要恢复的文件");
			}
			return super.toSuccess("数据恢复成功");
		} catch (Exception e) {
			logger.error("Topo数据恢复失败!",e);
			return toJsonObject(700, "Topo数据恢复失败");
		}
	}
	
	/**
	 * 获取已备份的Topo数据文件
	 * @return
	 */
	@RequestMapping(value="/files", method=RequestMethod.POST)
	public JSONObject getBackupFiles(Page<FileModel, FileModelQuery> page, FileModelQuery params){
		  try {
			  params.setFileGroup(TOPO_FILE_GROUP);
			  page.setCondition(params);
			  backupRecoveryApi.getBackupFiles(page);
			  return super.toSuccess(page);
	        } catch (Exception e) {
	        	logger.error("获取拓备份文件失败!",e);
	        	return toJsonObject(700, "获取拓备份文件失败");
	        }
	}
	
	/**
	 * 备份当前拓扑模块所有数据
	 * @return
	 */
	@RequestMapping(value="/backup", method=RequestMethod.POST)
	public JSONObject backup(){
		  try {
			  backupRecoveryApi.backup();
			  return super.toSuccess("备份成功");
	        } catch (Exception e) {
	        	logger.error("Topo备份数据失败!",e);
	        	return toJsonObject(700, "Topo数据备份失败");
	        }
	}
}
