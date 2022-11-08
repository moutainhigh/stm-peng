package com.mainsteam.stm.portal.resource.bo;

import java.util.List;
import java.util.Map;


/**
 * 
 * <li>文件名称: zTreeBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: zTree 辅助实体</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   pengl
 */
public class zTreeBo {

	/**节点ID*/
	private String id;
	/**节点父ID*/
	private String pId;
	/**节点名称*/
	private String name;
	/**是否为父节点*/
	private Boolean isParent = false;
	/**是否展开节点 */
	private Boolean open;
	/**是否选中节点*/
	private Boolean checked;
	/**是否不可选*/
	private Boolean nocheck;
	/**扩展属性*/
	private Map<String, String> extendAttribute;
	private List<zTreeBo> children;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPId() {
		return pId;
	}
	public void setPId(String pId) {
		this.pId = pId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public Boolean getNocheck() {
		return nocheck;
	}
	public void setNocheck(Boolean nocheck) {
		this.nocheck = nocheck;
	}
	public List<zTreeBo> getChildren() {
		return children;
	}
	public void setChildren(List<zTreeBo> children) {
		this.children = children;
	}
	public Map<String, String> getExtendAttribute() {
		return extendAttribute;
	}
	public void setExtendAttribute(Map<String, String> extendAttribute) {
		this.extendAttribute = extendAttribute;
	}
	@Override
	public int hashCode() {
		
		int result = 17;
		result = 37 * result + id.hashCode();
		result = 37 * result + name.hashCode();
		
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof zTreeBo)){
			return false;
		}
		
		zTreeBo tree = (zTreeBo)obj;
		
		if(tree.getId().equals(id) && tree.getName().equals(name)){
			return true;
		}
		
		return false;
	}
	
	
	
}
