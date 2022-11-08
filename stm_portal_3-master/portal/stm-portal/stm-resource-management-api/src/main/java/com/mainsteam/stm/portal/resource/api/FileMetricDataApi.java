package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.FileMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.FileMetricDataPageBo;


/**
 * <li>文件名称: FileMetricDataApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月13日
 * @author   pengl
 */
public interface FileMetricDataApi {
	
	/**
	 * 获取指标实时数据
	 */
	public FileMetricDataPageBo queryRealTimeMetricDatas(Long mainInstanceId);
	
	/**
	 * 获取文件实时数据
	 */
	public FileMetricDataPageBo scanCurrentFileData(long mainInstanceId,String queryPath);
	
	/**
	 * 添加监控
	 * @param fileIds
	 * @return
	 */
	public AddInstanceResult addFileMonitor(List<FileMetricDataBo> FileList,long mainInstanceId,String filePath,ILoginUser user);
	
	/**
	 * 删除文件子资源
	 * @param fileInstanceIds
	 * @return
	 */
	public boolean deleteFileInstance(String fileInstanceIds);
	
}
