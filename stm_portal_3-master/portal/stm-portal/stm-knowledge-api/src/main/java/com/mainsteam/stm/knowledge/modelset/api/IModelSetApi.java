package com.mainsteam.stm.knowledge.modelset.api;

import com.mainsteam.stm.caplib.dict.CaplibAPIResult;
import com.mainsteam.stm.knowledge.modelset.bo.ModuleBo;
import com.mainsteam.stm.knowledge.modelset.bo.ModuleQueryBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: com.mainsteam.stm.knowledge.modelset.api.ModelSetApi.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年12月2日
 */
public interface IModelSetApi {

	/**
	 * 获取模型配置列表
	 * @param page
	 * @author	ziwen
	 * @date	2019年12月3日
	 */
	void getPage(Page<ModuleBo, ModuleQueryBo> page);

	/**
	 * 保存模型
	 * @param moduleBo
	 * @return
	 * @author	ziwen
	 * @date	2019年12月4日
	 */
	CaplibAPIResult save(ModuleBo moduleBo);

	/**
	 * 通过sysOid获取模型
	 * @param sysOid
	 * @return
	 * @author	ziwen
	 * @date	2019年1月7日
	 */
	Boolean getModuleBoBysisOid(String sysOid);

	/**
	 * 删除模型
	 * @param moduleBo
	 * @return
	 * @author	ziwen
	 * @date	2019年1月20日
	 */
	CaplibAPIResult delete(String sysOid);

}
