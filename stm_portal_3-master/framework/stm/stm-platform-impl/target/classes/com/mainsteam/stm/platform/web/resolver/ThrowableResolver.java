package com.mainsteam.stm.platform.web.resolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.exception.BaseRuntimeException;
import com.mainsteam.stm.util.ResponseUtil;

/**
 * <li>文件名称: ThrowableResolver.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class ThrowableResolver implements HandlerExceptionResolver {

	private static final String msg="服务器升级中...";

	private static final Logger LOGGER=Logger.getLogger(ThrowableResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		
		ModelAndView mv=new ModelAndView();
		
		if(ex instanceof BaseException){
			BaseException e= (BaseException) ex;
			mv.addAllObjects(e.toJsonObject());
		}else if(ex instanceof BaseRuntimeException){
			BaseRuntimeException re=(BaseRuntimeException) ex;
			mv.addAllObjects(re.toJsonObject());
		}else{
			mv.addAllObjects(ResponseUtil.toJsonObject(500,msg));
		}

		LOGGER.error("Action异常捕获日志",ex);
		
		return mv;
	}
}
