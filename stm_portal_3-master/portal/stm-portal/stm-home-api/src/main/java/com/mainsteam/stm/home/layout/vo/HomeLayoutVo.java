package com.mainsteam.stm.home.layout.vo;

import java.util.List;

public class HomeLayoutVo {
	   /** 用户ID */
    private long userId;
    /**域id**/
    private List<Long> domainids;
    
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public List<Long> getDomainids() {
		return domainids;
	}

	public void setDomainids(List<Long> domainids) {
		this.domainids = domainids;
	}



    
}
