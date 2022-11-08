package com.mainsteam.stm.home.layout.bo;

import java.sql.Timestamp;

/**
 * 用户首页布局授权域关系
 * <li>文件名称: HomeLayoutDomainBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月13日 上午10:40:54
 * @author   dfw
 */
public class HomeLayoutDomainBo {
	
    /** ID */
    private long id;

    /** 布局ID */
    private long layoutId;

    /** 用户ID */
    private long userId;

    /** 域ID */
    private long domainId;

    /** 创建时间 */
    private Timestamp createTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(long layoutId) {
		this.layoutId = layoutId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
    
}
