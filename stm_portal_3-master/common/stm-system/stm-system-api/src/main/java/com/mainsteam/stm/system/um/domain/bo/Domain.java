package com.mainsteam.stm.system.um.domain.bo;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.platform.web.vo.IDomain;

/**
 * 
 * <li>文件名称: Domain.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 业务逻辑层域的传输对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月19日  上午11:10:52
 * @author   xch
 */
public class Domain implements IDomain{
	
	private static final long serialVersionUID = 6973110153021297438L;
	
	private long id;
	// 域名称
	private String name;
	//描述
	private String description;
	//创建人ID
	private long creatorId;
	//创建时间
	private Date createdTime;
	//状态
	private int status;
	
	/**域管理员*/
	private String adminUser;
	
	/**域下DCS集合 */
	private List<Node> dcsNodes;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List<Node> getDcsNodes() {
		return dcsNodes;
	}
	public void setDcsNodes(List<Node> dcsNodes) {
		this.dcsNodes = dcsNodes;
	}
	public String getAdminUser() {
		return adminUser;
	}
	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}
	@Override
	public int hashCode() {
		return (getName()+getId()).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hashCode()==obj.hashCode();
	}
}
