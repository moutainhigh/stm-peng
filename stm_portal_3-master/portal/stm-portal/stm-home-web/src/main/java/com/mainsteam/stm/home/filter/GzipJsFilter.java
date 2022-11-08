package com.mainsteam.stm.home.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * <li>文件名称: GzipJsFilter.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: 用来过滤 .gzip压缩后的文件</li>
 * @version  ms.stm
 * @since   2017年5月12日
 * @author   zhouw
 */
public class GzipJsFilter implements Filter{
	Map<String,Object> headers = new HashMap<>();    
	
	public void destroy() {      
    }      
    public void doFilter(ServletRequest req, ServletResponse res,      
            FilterChain chain) throws IOException, ServletException {      
        if(req instanceof HttpServletRequest) {      
            doFilter((HttpServletRequest)req, (HttpServletResponse)res, chain);      
        }else {      
            chain.doFilter(req, res);      
        }      
    }      
    public void doFilter(HttpServletRequest request,      
            HttpServletResponse response, FilterChain chain)      
            throws IOException, ServletException {      
            request.setCharacterEncoding("UTF-8");      
            for(Iterator<?> it = headers.entrySet().iterator();it.hasNext();) {      
                Map.Entry<?,?> entry = (Map.Entry<?,?>)it.next();      
                response.addHeader((String)entry.getKey(),(String)entry.getValue());      
            }      
            chain.doFilter(request, response);      
    }      
      
    public void init(FilterConfig config) throws ServletException {      
        String headersStr = config.getInitParameter("headers");      
        String[] headers = headersStr.split(",");      
        for(int i = 0; i < headers.length; i++) {      
            String[] temp = headers[i].split("=");      
            this.headers.put(temp[0].trim(), temp[1].trim());      
        }      
    }    

}
