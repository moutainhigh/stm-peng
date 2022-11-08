package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

/**
 * <li>文件名称: BizDepBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月23日
 * @author   caoyong
 */
public class BizStatusSelfBo implements Serializable {
	private static final long serialVersionUID = 482569427032836788L;
	/** 主键ID **/
	private Long id;
	/** 业务ID **/
	private long biz_main_id;
	/** 类型0：致命；1：严重；2：告警； **/
	private int type;
	/** 资源实例ID **/
	private long instance_id;
	/** 资源实例ID **/
	private String metric_id;
	/** 资源（指标）状态 **/
	private String state;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public long getBiz_main_id() {
		return biz_main_id;
	}
	public void setBiz_main_id(long biz_main_id) {
		this.biz_main_id = biz_main_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getInstance_id() {
		return instance_id;
	}
	public void setInstance_id(long instance_id) {
		this.instance_id = instance_id;
	}
	public String getMetric_id() {
		return metric_id;
	}
	public void setMetric_id(String metric_id) {
		this.metric_id = metric_id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
