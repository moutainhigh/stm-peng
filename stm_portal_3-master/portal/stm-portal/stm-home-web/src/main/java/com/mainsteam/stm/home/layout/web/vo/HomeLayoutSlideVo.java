package com.mainsteam.stm.home.layout.web.vo;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutSlideBo;

/**
 * <li>文件名称: HomeLayoutSlideVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午1:34:08
 * @author   dengfuwei
 */
public class HomeLayoutSlideVo {

	private List<HomeLayoutSlideBo> homeLayoutSlideBoList;
	
	private int slideTime;
	
	private String animation;

	public List<HomeLayoutSlideBo> getHomeLayoutSlideBoList() {
		return homeLayoutSlideBoList;
	}

	public void setHomeLayoutSlideBoList(
			List<HomeLayoutSlideBo> homeLayoutSlideBoList) {
		this.homeLayoutSlideBoList = homeLayoutSlideBoList;
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
	
}
