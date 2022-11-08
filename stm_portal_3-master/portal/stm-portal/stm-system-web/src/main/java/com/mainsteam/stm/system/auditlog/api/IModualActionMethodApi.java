package com.mainsteam.stm.system.auditlog.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.system.auditlog.bo.MethodEntity;

/**
 * <li>文件名称: com.mainsteam.stm.system.auditlog.api.IModualActionMethodApi.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年12月15日
 */
public interface IModualActionMethodApi {

	/**
	 * 重新初始化mapper中的数据
	 * @author	ziwen
	 * @date	2019年12月15日
	 */
	void reLoadXmlMapper();
	/**
	 * 获取模块的信息
	 * @return
	 * @author	ziwen
	 * @date	2019年12月15日
	 */
	List<MethodEntity> getMethodEntityList();
	/**
	 * 获取需要拦截的所有方法
	 * @return
	 * @author	ziwen
	 * @date	2019年12月15日
	 */
	Map<String, MethodEntity> getMethodEntityMap();
}
