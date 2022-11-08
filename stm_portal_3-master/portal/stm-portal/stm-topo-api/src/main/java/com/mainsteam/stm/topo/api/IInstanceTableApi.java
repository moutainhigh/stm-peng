package com.mainsteam.stm.topo.api;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;

/**
 * <li>一览表管理接口定义</li>
 * <li>文件名称: IScheduleApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月31日
 * @author zwx
 */
public interface IInstanceTableApi {
	
	/**
	 * 修改链路告规则
	 * @param enables
	 */
	public void updateAlarmRules(String alarmRules);
	
	/**
	 * 改变链路告规则发送条件是否启用
	 * @param enables
	 */
	public void changeLinkAlarmConditionEnabled(String enables);
	
	/**
	 * 获取链路告警设置信息
	 * @return JSONArray
	 */
	public JSONArray getLinkAlarmSetting();
	
	/**
	 * 保存链路告警设置信息
	 * @param alarmSetting
	 */
	public void saveLinkAlarmSetting(String alarmSetting);
	
	/**
	 * 根据资源实例ids获取链路数据
	 * @param resourceIds	资源实例ids
	 * @param type			类型（map）
	 * @return List<LinkBo>
	 */
	public List<LinkBo> getLinksByInstanceIds(long[] resourceIds,String type) throws InstancelibException;
	
	/**
	 * 根据条件分页查询资源链路列表
	 * @param page
	 * @return List<LinkBo>
	 * @throws InstancelibException
	 */
	public void selectLinkByPage(Page<LinkBo, LinkBo> page) throws InstancelibException;
	
	/**
	 * 根据条件分页查询资源实例设备列表
	 * @param page
	 * @return void
	 * @throws InstancelibException 
	 */
	public void selectDeviceByPage(Page<NodeBo, NodeBo> page) throws InstancelibException;
	/**
	 * 首页根据条件分页查询资源链路列表
	 * @param page
	 * @return List<LinkBo>
	 * @throws InstancelibException
	 */
	public void homeSelectLinkByPage(Page<LinkBo, LinkBo> page) throws InstancelibException;
}
