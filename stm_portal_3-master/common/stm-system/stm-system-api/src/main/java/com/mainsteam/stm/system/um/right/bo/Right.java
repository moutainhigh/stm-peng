package com.mainsteam.stm.system.um.right.bo;

import java.util.Set;

import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.IRight;

/**
 * <li>文件名称: RightBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月5日
 * @author   ziwenwen
 */
public class Right implements IRight{
	private static final long serialVersionUID = 6033433531832320402L;
	private Long id;
	private String name;
	private Long fileId;
	private String  url;
	private String description;
	private Integer type = 1;
	private int sort;
	private int status = 1;	//1表示启用	0表示停用
	private Long pid;
	private int isRoleUsed;
	private int isNewTag;
	private String icon;
	private Set<IDomain> domains;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public int getIsRoleUsed() {
		return isRoleUsed;
	}
	public void setIsRoleUsed(int isRoleUsed) {
		this.isRoleUsed = isRoleUsed;
	}
	@Override
	public Set<IDomain> getDomains() {
		return domains;
	}
	
	public void setDomains(Set<IDomain> domains){
		this.domains=domains;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIsNewTag() {
		return isNewTag;
	}
	public void setIsNewTag(int isNewTag) {
		this.isNewTag = isNewTag;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj instanceof Right) {
        	Right right = (Right) obj;
            return (id.equals(right.id));
        }
        return false;
	}
	@Override
	public int hashCode() {
        return id.hashCode();
            
    }


	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}


