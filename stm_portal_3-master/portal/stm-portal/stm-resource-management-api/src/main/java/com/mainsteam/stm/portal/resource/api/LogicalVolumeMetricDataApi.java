package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.LogicalVolumeMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.LogicalVolumeMetricDataPageBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataPageBo;


/**
 * <li>文件名称: LogicalVolumeMetricDataApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月2日
 * @author   tongpl
 */
public interface LogicalVolumeMetricDataApi {
	/**
	 * 获取指标实时数据
	 */
	public LogicalVolumeMetricDataPageBo queryRealTimeMetricDatas(Long mainInstanceId);
	
	/**
	 * 获取进程实时数据
	 */
	public LogicalVolumeMetricDataPageBo scanLogicalVolumeData(long mainInstanceId,String volumnGroupName);
	
	/**
	 * 添加监控
	 * @param processIds
	 * @return
	 */
	public AddInstanceResult addLogicalVolumeMonitor(List<LogicalVolumeMetricDataBo> logicalVolumeList, long mainInstanceId, ILoginUser user);
	
	/**
	 * 删除逻辑卷子资源
	 * @param logicalInstanceIds
	 * @return
	 */
	public boolean deleteLogicalInstance(String logicalInstanceIds);
	
	
}
