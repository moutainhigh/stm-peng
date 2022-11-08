package com.mainsteam.stm.home.layout.bo;

/**
 * 轮播页面
 * <li>文件名称: HomeLayoutSlideBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月13日 上午10:41:11
 * @author   dfw
 */
public class HomeLayoutSlideBo {
	
    /** ID */
    private long id;

    /** 用户ID */
    private long userId;

    /** 布局ID */
    private long layoutId;

    /** 顺序号 */
    private int sortNum;

    /** 轮播间隔时间，单位 秒 */
    private int slideTime;

    /** 切换动画效果 */
    private String animation;
    /**页面名称**/
    private String name;
/***是否是默认页面 0 是 1 不是***/
    private int defaultLayout;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(long layoutId) {
		this.layoutId = layoutId;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public int getSlideTime() {
		return slideTime;
	}

	public void setSlideTime(int slideTime) {
		this.slideTime = slideTime;
	}

	public String getAnimation() {
		return animation;
	}

	public void setAnimation(String animation) {
		this.animation = animation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDefaultLayout() {
		return defaultLayout;
	}

	public void setDefaultLayout(int defaultLayout) {
		this.defaultLayout = defaultLayout;
	}
    
}
