package com.mainsteam.stm.simple.search.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.simple.search.vo.SearchConditionsVo;

/**
 * <li>文件名称: com.mainsteam.stm.simple.search.dao.impl.SearchDao.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月28日
 */
public interface ISearchDao {

	/**
	 * 保存搜索信息
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int saveResourceBizRel(ResourceBizRel model);

	/**
	 * 删除搜索信息
	 * @param model
	 * @return
	 * @author	ziwen
	 * @date	2019年10月28日
	 */
	int delResourceBizRel(ResourceBizRel model);
	/**
	 * 删除资源组
	 * @param model
	 * @return
	 */
	int delResourcesBizRel(ResourceBizRel model);

	/**
	 * 查询满足条件的搜索结果
	 * @param page
	 * @return
	 * @author	ziwen
	 * @date	2019年10月30日
	 */
	List<ResourceBizRel> search(Page<ResourceBizRel,SearchConditionsVo> page);

	/**
	 * 保存搜索信息集合
	 * @param rels
	 * @return
	 * @author	ziwen
	 * @date	2019年10月31日
	 */
	int saveResourceBizRels(List<ResourceBizRel> rels);
	
	/**
	 * 删除搜索信息集合(多个资源ID)
	 * @param rels
	 * @return
	 * @author	ziwen
	 * @date	2019年10月31日
	 */
	int delResourceBizRels(ResourceBizRel model);

	
	/**
	* @Title: delReportResourceBizRel
	* @Description: 删除报表管理资源
	* @param model
	* @return  int
	* @throws
	*/
	int delReportResourceBizRel(ResourceBizRel model);
	
	int delAllByResource(long resourceId);
	
	/**
	* @Title: checkResourceBizRelIsExist
	* @Description: 验证关联关系是否存在，返回结果大于0表示关系存在
	* @param model
	* @return  int
	* @throws
	*/
	int checkResourceBizRelIsExist(ResourceBizRel model);
}
