package com.mainsteam.stm.home.layout.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.layout.api.HomeLayoutApi;
import com.mainsteam.stm.home.layout.api.HomeLayoutSlideApi;
import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutDefaultBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutSlideBo;
import com.mainsteam.stm.home.layout.web.vo.HomeLayoutSlideVo;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: HomeLayoutSlideAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午4:51:51
 * @author   dengfuwei
 */
@Controller
@RequestMapping("system/home/layout/slide")
public class HomeLayoutSlideAction extends BaseAction {
	
	@Resource
	private HomeLayoutSlideApi homeLayoutSlideApi;
	
	@Resource(name="ocProtalHomeLayoutSlideSeq")
	private ISequence homeLayoutSlideSeq;
    @Resource(name="homeLayoutApi")
    private HomeLayoutApi homeLayoutApi;
	/**
	 * 获取轮播页面
	 * @return
	 */
	@RequestMapping("getSlide")
    public JSONObject getSlide(){
		ILoginUser user = getLoginUser();

		List<HomeLayoutSlideBo> lists= new ArrayList<HomeLayoutSlideBo>();
		List<HomeLayoutSlideBo> list = homeLayoutSlideApi.get(user.getId());
	HomeLayoutDefaultBo defaultBo=	homeLayoutApi.getLayoutDefault(user.getId());
	/**
	 * 一：有默认布局，
	 * 1.不在轮播页面中
	 * 2.在轮播页面中
	 * 二：无默认布局
	 * 
	 */
	Boolean bl=false;// false 不在 true 在
	if(list.size()!=0){
		if(defaultBo!=null){//判断默认首页是否在轮播页面中
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getLayoutId()==defaultBo.getDefaultLayoutId()){//
					bl=true;//包含了默认页面
				}
			}
			if(bl==false){
				HomeLayoutSlideBo slideBo= new HomeLayoutSlideBo();
				slideBo.setLayoutId(defaultBo.getDefaultLayoutId());
				HomeLayoutBo hbo=	homeLayoutApi.getLayoutById(defaultBo.getDefaultLayoutId());
				if(hbo!=null){
					slideBo.setName(hbo.getName());	
				}
				slideBo.setDefaultLayout(0);
				slideBo.setAnimation(list.get(0).getAnimation());
				slideBo.setSlideTime(30);
				//slideBo.setSortNum(1);
				lists.add(slideBo);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			HomeLayoutBo bo=	homeLayoutApi.getLayoutById(list.get(i).getLayoutId());
			if(bo!=null){
				list.get(i).setName(bo.getName());
				if(defaultBo!=null && defaultBo.getDefaultLayoutId()==list.get(i).getLayoutId()){//默认页面
					list.get(i).setDefaultLayout(0);

				if(bl==true){//包含默认页面，按原有list组装数据
					lists.add(list.get(i));
				}
				}else{
					list.get(i).setDefaultLayout(1);
					lists.add(list.get(i));
				}
				
			}
		
		}
	}else{
		if(defaultBo!=null){
			HomeLayoutSlideBo slideBo= new HomeLayoutSlideBo();
			slideBo.setLayoutId(defaultBo.getDefaultLayoutId());
			HomeLayoutBo hbo=	homeLayoutApi.getLayoutById(defaultBo.getDefaultLayoutId());
			if(hbo!=null){
				slideBo.setName(hbo.getName());	
			}
			slideBo.setDefaultLayout(0);
			slideBo.setAnimation("nochoose");
			slideBo.setSlideTime(30);
		//	slideBo.setSortNum(1);
			lists.add(slideBo);
		}
	}
	
		Map<String, Object> result = new HashMap<>();
        result.put("list", lists);
        return toSuccess(result);
    }
	
	/**
	 * 保存轮播页面
	 * @param homeLayoutSlideBoList
	 * @param slideTime
	 * @return
	 */
	@RequestMapping("saveSlide")
    public JSONObject saveSlide(String checkDivids,String slideTime,String animation){
		int num=0;
		if(checkDivids == null || slideTime==null ||  animation==null){
			return toSuccess("参数为空");
		}
		ILoginUser user = getLoginUser();
		long userId = user.getId();
		HomeLayoutSlideVo homeLayoutSlideVo= new HomeLayoutSlideVo();
		List<HomeLayoutSlideBo> listbo= new ArrayList<HomeLayoutSlideBo>();
	
	String[] layoutids=	checkDivids.split(",");
	if(layoutids.length!=0){
		for (int i=0;i<layoutids.length;i++) {
			HomeLayoutSlideBo bo = new HomeLayoutSlideBo();
			bo.setId(homeLayoutSlideSeq.next());
			bo.setSlideTime(Integer.parseInt(slideTime));
			bo.setAnimation(animation);
			bo.setUserId(userId);
			bo.setSortNum(i+1);
			bo.setLayoutId(Long.parseLong(layoutids[i]));
			listbo.add(bo);
		}
		homeLayoutSlideVo.setHomeLayoutSlideBoList(listbo);
	}
	
		homeLayoutSlideApi.save(userId, homeLayoutSlideVo.getHomeLayoutSlideBoList());
		Map<String, Object> result = new HashMap<>();
        result.put("list", homeLayoutSlideVo.getHomeLayoutSlideBoList());
        return toSuccess(result);
    }
}
