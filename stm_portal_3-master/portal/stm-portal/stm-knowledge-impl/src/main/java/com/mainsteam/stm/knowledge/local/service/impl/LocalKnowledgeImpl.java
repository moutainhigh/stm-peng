package com.mainsteam.stm.knowledge.local.service.impl;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.local.api.IKnowledgeResolveApi;
import com.mainsteam.stm.knowledge.local.api.ILocalKnowledgeApi;
import com.mainsteam.stm.knowledge.local.dao.ILocalKnowledgeDao;
import com.mainsteam.stm.knowledge.service.bo.FaultBo;
import com.mainsteam.stm.knowledge.type.api.IKnowledgeTypeApi;
import com.mainsteam.stm.knowledge.type.bo.KnowledgeTypeBo;
import com.mainsteam.stm.lucene.api.IIndexApi;
import com.mainsteam.stm.lucene.api.IIndexKeyConstant;
import com.mainsteam.stm.lucene.api.IIndexManageApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.util.SortList;

/**
 * <li>文件名称: LocalKnowledgeImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日 下午4:01:35
 * @author   俊峰
 */
@Service("localKnowledgeApi")
public class LocalKnowledgeImpl implements ILocalKnowledgeApi {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(LocalKnowledgeImpl.class);

	
	
	@Resource
	private CapacityService capacityService;
	
	@Resource(name="localKnowledgeDao")
	private ILocalKnowledgeDao localKnowledgeDao;
	
	@Resource(name="knowledgeResolveApi")
	private IKnowledgeResolveApi knowledgeResolveApi;
	
	@Resource(name="knowledgeTypeApi")
	private IKnowledgeTypeApi knowledgeTypeApi;
	
	@Autowired
	private ISystemConfigApi systemconfigApi;
	
	@Autowired
	@Qualifier("IIndexManageApi")
	private IIndexManageApi indexManagerApi;
	
	private IIndexApi indexApi;
	
	@PostConstruct
	public void init(){
		try {
			indexApi =indexManagerApi.getIndexApi(IIndexKeyConstant.KEY_KNOWLEDGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private ISequence sequence;
	
	@Autowired
	public LocalKnowledgeImpl(SequenceFactory sequenceFactory){
		sequence = sequenceFactory.getSeq(SEQ.SEQNAME_STM_KNOWLEDGE);
	}
	
	@Override
	public KnowledgeBo addKnowledge(KnowledgeBo kb) {
		
		kb.setId(sequence.next());
		kb.setCreateTime(Calendar.getInstance().getTime());
		int result = localKnowledgeDao.insert(kb);
		indexApi.createIndex(kb);
		return kb;
	}

	
	
	@Override
	public int batchDelKnowledge(long[] ids) {
		for (long id : ids) {
			knowledgeResolveApi.deleteResolveByKnowledge(id);
			indexApi.deleteIndex(id);
		}
		return localKnowledgeDao.batchDel(ids);
	}

	@Override
	public int updateKnowledge(KnowledgeBo kb) {
		indexApi.updateIndex(kb);
		return localKnowledgeDao.update(kb);
	}

	@Override
	public KnowledgeBo queryKnowledge(long id) {
		List<KnowledgeResolveBo> resolves = knowledgeResolveApi.queryKnowledgeResolves(id);
		KnowledgeBo knowledge = localKnowledgeDao.get(id);
		knowledge.setResolves(resolves);
		return knowledge;
	}

	@Override
	public List<KnowledgeBo> queryLocalKnowledgeBos(Page<KnowledgeBo, KnowledgeBo> page,String search) {
		//在keywords不为空的情况下遍历集合，不包含关键字的移除
		if(!StringUtils.isEmpty(search)){
			String sort="";
			List<KnowledgeBo> knowledgeBos = localKnowledgeDao.getAll();
			List<KnowledgeBo> searchList = new ArrayList<>();
			if(knowledgeBos!=null){
				for (KnowledgeBo knowledge : knowledgeBos) {
					KnowledgeTypeBo type = knowledgeTypeApi.getKnowledgeTypeByCode(knowledge.getKnowledgeTypeCode());
					if(type!=null){
						knowledge.setKnowledgeTypeName(type.getName());
					}
					if((""+knowledge.getId()).toLowerCase().contains(search.toLowerCase()) 
							|| knowledge.getKnowledgeTypeCode().toLowerCase().contains(search.toLowerCase()) 
							|| knowledge.getKnowledgeTypeName().toLowerCase().contains(search.toLowerCase()) 
							|| knowledge.getSourceContent().toLowerCase().contains(search.toLowerCase())){
						searchList.add(knowledge);
					}
				}
			}
			if(!StringUtils.isEmpty(sort)){
				SortList.sort(searchList, sort, page.getOrder().toLowerCase());
			}
			//做分页
			page.setTotalRecord(searchList.size());
			List<KnowledgeBo> cList = new ArrayList<KnowledgeBo>();
			//结果集是否超出当前页面显示条数
			for(int i = (int) page.getStartRow();i<(page.getStartRow()+page.getRowCount()<searchList.size()?page.getStartRow()+page.getRowCount():searchList.size());i++){
				if(searchList.get(i)!=null){
					cList.add((KnowledgeBo)searchList.get(i));
				}
			}
			knowledgeBos = cList;
			page.setDatas(knowledgeBos);
			return knowledgeBos;
		}else{
			List<KnowledgeBo> knowledgeBos = localKnowledgeDao.pageSelect(page);
			if(knowledgeBos!=null){
				for (KnowledgeBo knowledge : knowledgeBos) {
					KnowledgeTypeBo type = knowledgeTypeApi.getKnowledgeTypeByCode(knowledge.getKnowledgeTypeCode());
					if(type!=null){
						knowledge.setKnowledgeTypeName(type.getName());
					}
				}
			}
			page.setDatas(knowledgeBos);
			return knowledgeBos;
		}
	}

	@Override
	public List<KnowledgeBo> queryKnowledgeByType(FaultBo fault) {
		return localKnowledgeDao.queryKnowledgeByType(fault);
	}

	@Override
	public String getFaultKnowledgeDownloadAddr() {
		SystemConfigBo config = systemconfigApi.getSystemConfigById(SystemConfigConstantEnum.SYSTEM_CONFIG_FAULT_KNOWLEDGE_DOWNLOAD_ADDR.getCfgId());
		return config.getContent()==null?"":config.getContent();
	}
}
