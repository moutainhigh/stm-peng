package com.mainsteam.stm.simple.search.web.ation;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.platform.web.vo.IRight;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.simple.search.vo.SearchConditionsVo;

/**
 * <li>文件名称: com.mainsteam.stm.simple.search.web.ation.SearchAction.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月29日
 */
@Controller
@RequestMapping(value="/simple/search")
public class SearchAction extends BaseAction{

	@Autowired
	private ISearchApi searchApi;
	
	@RequestMapping
	public JSONObject index(Page<ResourceBizRel, SearchConditionsVo> page, SearchConditionsVo condition, HttpSession session){
		ILoginUser user = this.getLoginUser(session);
		List<IRight> rights = user.getRights();
		Boolean flag = true;
		for(IRight right : rights){
			if(condition.getType().equals(right.getId())){
				flag = false;
				break;
			}
		}
		if(flag){
			toSuccess(-1);
		}
		
		page.setCondition(condition);
		if(condition.getType()==ILoginUser.RIGHT_ALARM){
			return toSuccess(searchApi.searchAlarm(page));
		}
		return toSuccess(searchApi.search(page));
	}
	
	@RequestMapping(value="/knowledge")
	public JSONObject knowledge(SearchConditionsVo conditions, Page<Byte, Byte> page){
		return toSuccess(searchApi.searchKnowledge(conditions, page));
	}
	
	@RequestMapping(value="/init")
	public JSONObject init(SearchConditionsVo condition){
		return toSuccess(searchApi.initResource(condition));
	}
	
}
