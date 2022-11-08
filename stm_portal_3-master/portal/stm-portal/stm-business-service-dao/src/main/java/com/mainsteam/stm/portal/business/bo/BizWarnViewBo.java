package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;
/**
 * <li>文件名称: BizWarnViewBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月20日
 * @author   caoyong
 */
public class BizWarnViewBo implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 2131588513206589287L;
	/**
	 * 告警记录ID
	 */
	private Long id;
	/**
	 * 告警业务ID
	 */
	private Long sourceId;
	/**
	 * 告警内容
	 */
	private String content;
	/**
	 * 告警时间
	 */
	private Date warnTime;
	/**
	 * 告警级别
	 */
	private String level;
	/**
	 * 告警业务名称
	 */
	private String name;
	/**
	 * 告警类型
	 */
	private String dataClass;
	public String getDataClass() {
		return dataClass;
	}
	public void setDataClass(String dataClass) {
		this.dataClass = dataClass;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getWarnTime() {
		return warnTime;
	}
	public void setWarnTime(Date warnTime) {
		this.warnTime = warnTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
}
