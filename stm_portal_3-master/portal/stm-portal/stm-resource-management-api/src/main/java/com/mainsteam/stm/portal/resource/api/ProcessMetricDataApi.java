package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.ProcessDeadLockDataPageBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataPageBo;


/**
 * <li>文件名称: ProcessMetricDataApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月13日
 * @author   pengl
 */
public interface ProcessMetricDataApi {
	
	/**
	 * 获取指标实时数据
	 */
	public ProcessMetricDataPageBo queryRealTimeMetricDatas(Long mainInstanceId);
	
	/**
	 * 获取进程实时数据
	 */
	public ProcessMetricDataPageBo scanCurrentProcessData(long mainInstanceId);
	
	/**
	 * 添加监控
	 * @param processIds
	 * @return
	 */
	public AddInstanceResult addProcessMonitor(List<ProcessMetricDataBo> processList,long mainInstanceId,int type,ILoginUser user);
	
	/**
	 * 更新进程子资源的备注
	 */
	public boolean updateProcessInstanceRemark(long mainInstanceId,String newRemark);
	
	/**
	 * 删除进程子资源
	 * @param processInstanceIds
	 * @return
	 */
	public boolean deleteProcessInstance(String processInstanceIds);
	
	/**
	 * 获取僵死进程实时数据
	 */
	public ProcessMetricDataPageBo diedProcessData(long mainInstanceId);
	
	/**
	 * 获取死锁进程实时数据
	 */
	public ProcessDeadLockDataPageBo deadLockInfoData(long mainInstanceId);
}
