package com.mainsteam.stm.platform.web.view;

import java.util.Date;

import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

/**
 * <li>文件名称: BindingInitializer.java</li>
 * <li>文件描述: 数据绑定初始化类</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年8月18日
 */
public class BindingInitializer implements WebBindingInitializer {
	private ConversionService conversionService;
	@Override
	public void initBinder(WebDataBinder binder, WebRequest request) {
		binder.setConversionService(conversionService);
		binder.registerCustomEditor(Date.class, new DateTypeEditor());	//对日期字段绑定格式化
	}
	
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
