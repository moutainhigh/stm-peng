package com.mainsteam.stm.platform.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * <li>文件名称: ExecuteTimeAop.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月2日
 * @author   ziwenwen
 */
@Component
@Aspect
public class ExecuteTimeAop {
	
	private static final Logger LOG=Logger.getLogger(ExecuteTimeAop.class);
	
	@Around("execution(* com.mainsteam.stm..service..*Impl.*(..))")
	public Object runTimeLog(ProceedingJoinPoint jp)throws Throwable{
		long begin=System.currentTimeMillis();
		
		Object temp=jp.proceed(jp.getArgs());
		
		long end=System.currentTimeMillis();
		
		long runTime=end-begin;
		
		if(runTime>3000){
			LOG.debug("调用方法："+jp.getTarget().getClass().getName()+'.'+jp.getSignature().getName()+"用时："+(end-begin));
		}
		
		return temp;
	}
}


