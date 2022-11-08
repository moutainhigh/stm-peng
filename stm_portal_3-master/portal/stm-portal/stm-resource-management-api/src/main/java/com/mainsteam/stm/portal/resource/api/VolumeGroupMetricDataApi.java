package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.VolumeGroupMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.VolumeGroupMetricDataPageBo;

/**
 * <li>文件名称: FileMetricDataApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月2日
 * @author   tongpl
 */

public interface VolumeGroupMetricDataApi {
	
	/**
	 * 获取指标实时数据
	 */
	public VolumeGroupMetricDataPageBo queryRealTimeMetricDatas(Long mainInstanceId);
	
	/**
	 * 获取实时数据
	 */
	public VolumeGroupMetricDataPageBo scanVolumeGroupData(long mainInstanceId);
	
	/**
	 * 添加监控
	 * @param processIds
	 * @return
	 */
	public AddInstanceResult addVolumeGroupMonitor(List<VolumeGroupMetricDataBo> logicalVolumeList, long mainInstanceId, ILoginUser user);
	
	/**
	 * 删除卷组子资源
	 * @param volumeGroupInstanceIds
	 * @return
	 */
	public boolean deleteVolumeGroupInstance(String volumeGroupInstanceIds);
	
	
}
