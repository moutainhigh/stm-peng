package com.mainsteam.stm.portal.business.web.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * <li>文件名称: ResourceInstanceVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月21日
 * @author   caoyong
 */
public class ResourceInstanceVo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String text;
	private String state;
	private boolean checked;
	private Map<String, String> attributes;
	private List<ResourceInstanceVo> children;
	private String resourceId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public List<ResourceInstanceVo> getChildren() {
		return children;
	}
	public void setChildren(List<ResourceInstanceVo> children) {
		this.children = children;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

}
