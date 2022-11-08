/**
 * 
 */
package com.mainsteam.stm.knowledge.local.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.local.dao.ILocalKnowledgeDao;
import com.mainsteam.stm.knowledge.service.bo.FaultBo;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: LocalKnowledgeDao</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月14日 下午2:08:14
 * @author   俊峰
 */
@Repository("localKnowledgeDao")
public class LocalKnowledgeDaoImpl extends BaseDao<KnowledgeBo> implements ILocalKnowledgeDao {

	@Autowired
	public LocalKnowledgeDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, ILocalKnowledgeDao.class.getName());
	}

	@Override
	public List<KnowledgeBo> pageSelect(Page<KnowledgeBo, KnowledgeBo> page) {
		return getSession().selectList(getNamespace()+"pageSelect",page);
	}
	
	@Override
	public List<KnowledgeBo> getAll() {
		return getSession().selectList(getNamespace()+"getAll");
	}
	
	
	@Override
	public List<KnowledgeBo> queryKnowledgeByType(FaultBo fault) {
		return getSession().selectList(getNamespace()+"queryKnowledgeByType",fault);
	}
}
