package com.mainsteam.stm.knowledge.capacity.bo;

import java.io.Serializable;


/**
 * <li>文件名称: CapacityKnowledgeBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月2日
 * @author   ziwenwen
 */
public class CapacityKnowledgeBo implements Serializable{
	private static final long serialVersionUID = -7292814523353613297L;
	private Long id;
	private String name;
	private Long fileId;
	private String deployTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	
	public String getDeployTime() {
		return deployTime;
	}
	
	public void setDeployTime(String deployTime) {
		this.deployTime = deployTime;
	}
}


