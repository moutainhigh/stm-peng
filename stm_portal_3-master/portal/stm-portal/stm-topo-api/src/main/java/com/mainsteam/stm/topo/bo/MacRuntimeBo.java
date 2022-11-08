package com.mainsteam.stm.topo.bo;

import java.util.Date;

import com.mainsteam.stm.util.DateUtil;

/**
 * <li>实时表bo(stm_topo_mac_runtime)</li>
 * <li>文件名称: RuntimeBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年9月10日
 * @author zwx
 */
public class MacRuntimeBo extends MacBaseBo{
	private Date updateTime;
	
	/**扩展属性-用于业务处理*/
	
	/*更新时间string*/
	private String updateTimeStr;
	
	/*是否存在(0：新增，1：历史)*/
	private int exist;
	
	public int getExist() {
		return exist;
	}
	public void setExist(int exist) {
		this.exist = exist;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateTimeStr() {
		if(null != updateTime){
			updateTimeStr = DateUtil.format(updateTime);
		}
		return updateTimeStr;
	}
	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}
}
