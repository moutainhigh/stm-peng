package com.mainsteam.stm.portal.resource.web.action;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.FileMetricDataApi;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.FileMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.FileMetricDataPageBo;
import com.mainsteam.stm.portal.resource.web.vo.FileParameterVo;


/**
 * 
 * <li>文件名称: FileMetricDataAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年4月23日
 * @author   pengl
 */

@Controller
@RequestMapping("/portal/resourceManager/fileMetricData")
public class FileMetricDataAction extends BaseAction {
	
	private static final Log logger = LogFactory.getLog(FileMetricDataAction.class);
	
	@Autowired
	private FileMetricDataApi fileMetricDataApi;
	
	/**
	 * 获取指标实时数据
	 */
	@RequestMapping("/getFileData")
	public JSONObject getFileData(Long mainInstanceId) {
		
		FileMetricDataPageBo pageBo = fileMetricDataApi.queryRealTimeMetricDatas(mainInstanceId);
		
		return toSuccess(pageBo);
		
	}
	
	/**
	 * 获取文件实时数据
	 */
	@RequestMapping("/scanFileData")
	public JSONObject scanFileData(long mainInstanceId,String queryPath) {
		
		FileMetricDataPageBo pageBo = fileMetricDataApi.scanCurrentFileData(mainInstanceId,queryPath);
		
		return toSuccess(pageBo);
		
	}
	
	/**
	 * 删除文件子资源
	 */
	@RequestMapping("/deleteFileInstance")
	public JSONObject deleteFileInstance(String instanceIds) {
		
		boolean result = fileMetricDataApi.deleteFileInstance(instanceIds);
		
		return toSuccess(result);
		
	}
	
	/**
	 * 加入监控
	 */
	@RequestMapping("/addFileToMonitor")
	public JSONObject addFileToMonitor(FileParameterVo parameter,HttpSession session) {
		//当前用户
		ILoginUser user = getLoginUser(session);
		
		List<FileMetricDataBo> fileMetricDataBoList = JSONObject.parseArray(parameter.getFileList(), FileMetricDataBo.class);
		
		AddInstanceResult result = fileMetricDataApi.addFileMonitor(fileMetricDataBoList,parameter.getMainInstanceId(),parameter.getFilePath(),user);
		
		return toSuccess(result);
		
	}
	
}
