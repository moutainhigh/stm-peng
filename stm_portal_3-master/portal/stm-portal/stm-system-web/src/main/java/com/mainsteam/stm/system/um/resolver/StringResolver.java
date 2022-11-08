package com.mainsteam.stm.system.um.resolver;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

/**
 * <li>文件名称: StringResolver.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class StringResolver extends AbstractCachingViewResolver{
	public StringResolver(){
//		System.out.println("StringResolver初始化 ****************");
	}
	
	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		return new StringView(viewName);
	}
	
	private class StringView implements View{
		private static final String contentType="text/plain;charset=UTF-8";
		private String viewName;
		
		public StringView(String viewName) {
			this.viewName=viewName;
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public void render(Map<String, ?> model, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
//			System.out.println(viewName);
//			System.out.println(model);
			response.setContentType(contentType);
			PrintWriter pout=response.getWriter();
//			Object val=model.get(Constant.str_data);
			pout.print(viewName);
			pout.close();
		}
	}
}
