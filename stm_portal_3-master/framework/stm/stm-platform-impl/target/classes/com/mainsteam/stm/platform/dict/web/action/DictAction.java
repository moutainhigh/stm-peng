package com.mainsteam.stm.platform.dict.web.action;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.dict.api.IDictApi;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: DictAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月11日
 * @author   ziwenwen
 */
@RequestMapping("/dict")
@Controller
public class DictAction extends BaseAction {

	@Resource(name="dictApi")
	IDictApi dictApi;
	
	@RequestMapping("/get")
	public JSONObject get(String type){
		return toSuccess(dictApi.get(type));
	}
}


