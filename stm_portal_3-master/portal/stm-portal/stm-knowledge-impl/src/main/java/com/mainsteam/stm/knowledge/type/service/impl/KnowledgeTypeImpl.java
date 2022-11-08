package com.mainsteam.stm.knowledge.type.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.AccidentKMType;
import com.mainsteam.stm.caplib.dict.KMTypeEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.knowledge.type.api.IKnowledgeTypeApi;
import com.mainsteam.stm.knowledge.type.bo.AccidentMetricBo;
import com.mainsteam.stm.knowledge.type.bo.KnowledgeTypeBo;
import com.mainsteam.stm.knowledge.type.dao.IKnowledgeTypeDao;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;

@Service("knowledgeTypeApi")
public class KnowledgeTypeImpl implements IKnowledgeTypeApi {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(KnowledgeTypeImpl.class);
	private static final String KNOWLEDGE_TYPE_CODE_CACHE = "IKNOWLEDGE_TYPE_CODE_CACHE";
	private static final String ACCIDENTKMTYPES_ALL_DATA_CACHE="ACCIDENTKMTYPES_ALL_DATA_CACHE";
	IMemcache<List> cache = MemCacheFactory
			.getLocalMemCache(List.class);
	
	
	@Autowired
	@Qualifier("knowledgeTypeDao")
	private IKnowledgeTypeDao knowledgeTypeDao;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	

	@Override
	public List<AccidentKMType> getAccidentKMTypes() {
		List<AccidentKMType> accidentKMTypes = cache.get(ACCIDENTKMTYPES_ALL_DATA_CACHE);
		if(accidentKMTypes==null){
			accidentKMTypes = capacityService.getAccidentKMTypes();
			cache.set(ACCIDENTKMTYPES_ALL_DATA_CACHE, accidentKMTypes);
		}
		return accidentKMTypes;
	}
	
	public List<AccidentKMType> queryParentAccidentKMTypes(){
		List<AccidentKMType> accidentKMTypes = this.getAccidentKMTypes();
		List<AccidentKMType> resultAccidentKMTypeBos = new ArrayList<AccidentKMType>();
		if(accidentKMTypes!=null){
			for (AccidentKMType accidentKMType : accidentKMTypes) {
				if(!licenseCapacityCategory.isAllowCategory(accidentKMType.getId())){
					continue;
				}
				//第二级type
				if(accidentKMType.getLevel()==2){
					resultAccidentKMTypeBos.add(accidentKMType);
					//第二级的所有子节点
					List<AccidentKMType> subAccidentKMTypes = this.queryAccidentKMTypesByPid(accidentKMType.getId());
					resultAccidentKMTypeBos.addAll(subAccidentKMTypes);
//					List<AccidentKMType> subAccidentKMTypes = this.queryAccidentKMTypesByPid(accidentKMType.getId());
//					for (AccidentKMType level2SubAccidentKMType : subAccidentKMTypes) {
//						resultAccidentKMTypeBos.add(level2SubAccidentKMType);
//						List<AccidentKMType> sub3AccidentKMTypes = this.queryAccidentKMTypesByPid(level2SubAccidentKMType.getId());
//						for (AccidentKMType level3SubAccidentKMType : sub3AccidentKMTypes) {
//							resultAccidentKMTypeBos.add(level3SubAccidentKMType);
//							List<AccidentKMType> sub4accAccidentKMTypes =this.queryAccidentKMTypesByPid(level3SubAccidentKMType.getId());
//							for (AccidentKMType level4SubAccidentKMType : sub4accAccidentKMTypes) {
//								if(!level4SubAccidentKMType.getKmTypeEnum().equals(KMTypeEnum.Metric)){
//									resultAccidentKMTypeBos.add(level4SubAccidentKMType);
//								}
//							}
//						}
//					}
				}
			}
		}
		return resultAccidentKMTypeBos;
	}
	
	private List<AccidentKMType> queryAccidentKMTypesByPid(String pid){
		List<AccidentKMType> accidentKMTypes = this.getAccidentKMTypes();
		List<AccidentKMType> subAccidentKMTypes = null;
		if(accidentKMTypes!=null){
			subAccidentKMTypes = new ArrayList<AccidentKMType>();
			for (AccidentKMType accidentKMType : accidentKMTypes) {
				if(accidentKMType.getParentId().equals(pid)){
					subAccidentKMTypes.add(accidentKMType);
				}
			}
		}
		return subAccidentKMTypes;
	}
	
	public List<AccidentMetricBo> queryMetricAccidentKMTypeByParent(String parentId){
		List<AccidentMetricBo> resultMetrics = new ArrayList<AccidentMetricBo>();
//		ResourceDef resourceDef = capacityService.getResourceDefById(parentId);
//		if (resourceDef!=null) {
//			ResourceMetricDef[] resourceMetricDefs = resourceDef.getMetricDefs();
//			if(resourceMetricDefs!=null){
//				for (ResourceMetricDef resourceMetricDef : resourceMetricDefs) {
//					if (resourceMetricDef.getMetricType().equals(MetricTypeEnum.AvailabilityMetric) || resourceMetricDef.getMetricType().equals(MetricTypeEnum.PerformanceMetric)) {
//						resultMetrics.add(new AccidentMetricBo(resourceMetricDef.getId(), resourceMetricDef.getName()));
//					}
//					
//				}
//			}
//		}
		
		CategoryDef categoryDef = capacityService.getCategoryById(parentId);
		if(categoryDef!=null){
			ResourceDef[] resourceDefs = categoryDef.getResourceDefs();
			if(resourceDefs!=null){
				for (ResourceDef resourceDef : resourceDefs) {
					ResourceMetricDef[] metricDef = resourceDef.getMetricDefs();
					if(metricDef!=null){
						for (ResourceMetricDef resourceMetricDef : metricDef) {
							if(resourceMetricDef!=null){
								if (resourceMetricDef.getMetricType().equals(MetricTypeEnum.AvailabilityMetric) || resourceMetricDef.getMetricType().equals(MetricTypeEnum.PerformanceMetric)) {
									resultMetrics.add(new AccidentMetricBo(resourceMetricDef.getId(), resourceMetricDef.getName()));
								}
							}
						}
					}
				}
			}
		}
				
		List<String> delIds = new ArrayList<String>();
		List<AccidentMetricBo> delMetricBos = new ArrayList<AccidentMetricBo>();
		for (AccidentMetricBo accidentMetricBo : resultMetrics) {
			if(delIds.contains(accidentMetricBo.getId())){
				delMetricBos.add(accidentMetricBo);
			}else{
				delIds.add(accidentMetricBo.getId());
			}
		}
		resultMetrics.removeAll(delMetricBos);
		return resultMetrics;
	}
	
	@Override
	public KnowledgeTypeBo getKnowledgeTypeByCode(String code) {
		if(code!=null){
			String[] types = code.split("-");
			if(types!=null){
				String typeCode="";
				String typeName="";
				for(int i=0;i<types.length;i++){
					if(i==(types.length-1) && i>0){
						List<AccidentMetricBo> metricBos = queryMetricAccidentKMTypeByParent(types[i-1]);
						for(AccidentMetricBo metricBo :metricBos){
							if(metricBo.getId().equals(types[i])){
								typeCode+=metricBo.getId()+"-";
								typeName+=metricBo.getName()+"-";
							}
						}
					}else{
						AccidentKMType accidentKMType = this.getParentAccidentKMType(types[i]);
						if(accidentKMType!=null){
							typeCode+=accidentKMType.getId()+"-";
							typeName+=accidentKMType.getName()+"-";
						}
					}
				}
				if(typeCode!=""){
					typeCode = typeCode.substring(0,typeCode.length()-1);
				}
				if(typeName!=""){
					typeName = typeName.substring(0,typeName.length()-1);
				}
				KnowledgeTypeBo knowledgeTypeBo = new KnowledgeTypeBo();
				knowledgeTypeBo.setCode(typeCode);
				knowledgeTypeBo.setName(typeName);
				return knowledgeTypeBo;
			}
		}
		return null;
	}

	@Override
	public String createKnowledgeTypeCodeByMetric(String metricId,String parentId) {
		String code = "";
		AccidentKMType accidentKMType = this.getParentAccidentKMType(parentId);
		if(accidentKMType!=null && accidentKMType.getLevel()>2){
			code = accidentKMType.getParentId()+"-"+accidentKMType.getId()+"-"+metricId;
		}else{
			code = parentId+"-"+metricId;
		}
		return code;
	}
	
	private AccidentKMType getParentAccidentKMType(String id){
		List<AccidentKMType> accidentKMTypes = this.getAccidentKMTypes();
		for (AccidentKMType accidentKMType : accidentKMTypes) {
			if(accidentKMType.getId().equals(id)){
				return accidentKMType;
			}
		}
		return null;
	}
	
	private AccidentKMType getParentAccidentKMType(String id,String parentId){
		List<AccidentKMType> accidentKMTypes = this.getAccidentKMTypes();
		for (AccidentKMType accidentKMType : accidentKMTypes) {
			if(accidentKMType.getId().equals(id) && accidentKMType.getParentId().equals(parentId)){
				return accidentKMType;
			}
		}
		return null;
	}
	
}
