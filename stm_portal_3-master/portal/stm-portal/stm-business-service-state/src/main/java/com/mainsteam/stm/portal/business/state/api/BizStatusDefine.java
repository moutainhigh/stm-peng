package com.mainsteam.stm.portal.business.state.api;

public final class BizStatusDefine {

	//节点未监控时,无状态
	public final static int NONE_STATUS = -1;
	
	//正常状态
	public final static int NORMAL_STATUS = 0;
	
	//警告状态
	public final static int WARN_STATUS = 1;
	
	//严重状态
	public final static int SERIOUS_STATUS = 2;
	
	//致命状态
	public final static int DEATH_STATUS = 3;
	
}
