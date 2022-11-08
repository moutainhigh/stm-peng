package com.mainsteam.stm.platform.web.view;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mainsteam.stm.util.IConstant;

/**
 * <li>文件名称: JsonView.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月30日
 * @author   ziwenwen
 */
public class JsonView extends MappingJackson2JsonView {
	
	@Override
	protected Object filterModel(Map<String, Object> model) {
		Map<String, Object> result = new HashMap<String, Object>(model.size());
		result.put(IConstant.str_code, model.get(IConstant.str_code));
		result.put(IConstant.str_data, model.get(IConstant.str_data));
		return result;
	}
}
