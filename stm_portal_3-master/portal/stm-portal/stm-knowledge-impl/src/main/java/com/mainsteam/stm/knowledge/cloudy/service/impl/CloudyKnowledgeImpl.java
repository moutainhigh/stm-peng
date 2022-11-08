package com.mainsteam.stm.knowledge.cloudy.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mainsteam.stm.knowledge.cloudy.api.ICloudyKnowledgeApi;
import com.mainsteam.stm.knowledge.cloudy.bo.CKnowledgeStaBo;
import com.mainsteam.stm.knowledge.cloudy.dao.ICloudyKnowledgeDao;
import com.mainsteam.stm.knowledge.type.api.IKnowledgeTypeApi;
import com.mainsteam.stm.knowledge.type.bo.KnowledgeTypeBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.util.DateUtil;

/**
 * <li>文件名称: CloudyKnowledgeImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日 下午4:01:22
 * @author   俊峰
 */
@Service("cloudyKnowledgeApi")
public class CloudyKnowledgeImpl implements ICloudyKnowledgeApi {

	@Resource(name="cloudyKnowledgeDao")
	private ICloudyKnowledgeDao cloudyKnowledgeDao;

	@Resource(name="knowledgeTypeApi")
	private IKnowledgeTypeApi knowledgeTypeApi;
	@Override
	public List<CKnowledgeStaBo> cloudyKnowledgeSta(Page<CKnowledgeStaBo, CKnowledgeStaBo> page) {
		List<CKnowledgeStaBo> knowledgeStaBos = cloudyKnowledgeDao.pageSelect(page);
		if(knowledgeStaBos!=null){
			for (CKnowledgeStaBo cKnowledgeStaBo : knowledgeStaBos) {
				KnowledgeTypeBo type = knowledgeTypeApi.getKnowledgeTypeByCode(cKnowledgeStaBo.getTypeCode());
				if(type!=null){
					cKnowledgeStaBo.setTypeName(type.getName());
				}
			}
			page.setDatas(knowledgeStaBos);
		}
		return knowledgeStaBos;
	}
	@Override
	public Map<String, String> getCloudyUpdateTimeAndCount() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("updateTime", cloudyKnowledgeDao.cloudyKnowledgeUpdateTime()==null?"":DateUtil.format(cloudyKnowledgeDao.cloudyKnowledgeUpdateTime(), "yyyy-MM-dd"));
		result.put("count", String.valueOf(cloudyKnowledgeDao.countCloudyKnowledgeTotal()));
		return result;
	}

}
