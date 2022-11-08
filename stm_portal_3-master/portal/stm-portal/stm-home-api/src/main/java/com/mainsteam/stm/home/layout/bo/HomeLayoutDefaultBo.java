package com.mainsteam.stm.home.layout.bo;

/**
 * 用户首页默认布局
 * <li>文件名称: HomeLayoutDefaultBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月13日 上午10:40:48
 * @author   dfw
 */
public class HomeLayoutDefaultBo {
	
    /** 用户ID */
    private long userId;

    /** 默认布局ID */
    private long defaultLayoutId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getDefaultLayoutId() {
		return defaultLayoutId;
	}

	public void setDefaultLayoutId(long defaultLayoutId) {
		this.defaultLayoutId = defaultLayoutId;
	}

}
