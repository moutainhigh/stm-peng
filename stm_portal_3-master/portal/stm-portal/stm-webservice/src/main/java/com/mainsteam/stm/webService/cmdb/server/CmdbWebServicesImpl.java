package com.mainsteam.stm.webService.cmdb.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;

@WebService
public class CmdbWebServicesImpl implements CmdbWebServices {

	private static final Logger logger = LoggerFactory
			.getLogger(CmdbWebServicesImpl.class);

	@Resource
	private CapacityService capacityService;

	@Override
	public String getMoType() {
		// 获取根
		CategoryDef categoryDef = capacityService.getRootCategory();
		// 一级菜单
		CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
		
		logger.debug("baseCategoryDef size : " + baseCategoryDef.length);

		List<MOType> listMoTypes = new ArrayList<MOType>();
		
		if (null != baseCategoryDef && baseCategoryDef.length > 0) {
			for (int i = 0; i < baseCategoryDef.length; i++) {
				CategoryDef category = baseCategoryDef[i];
				MOType moType = new MOType();
				moType.setMoTypeId(category.getId());
				moType.setMoTypeName(category.getName());
				
				CategoryDef[] categoryArr ;
				
				if("VM".equals(category.getId())){
					CategoryDef[] cdIn = category.getChildCategorys();
					List<CategoryDef> cdList = new ArrayList<CategoryDef>();
					for(CategoryDef cdf:cdIn){
						CategoryDef[] cdChild =cdf.getChildCategorys();
						if(null!=cdChild && cdChild.length>0){
							for(CategoryDef cdfTemp:cdChild){
								cdList.add(cdfTemp);
							}
						}
					}
					categoryArr = new CategoryDef[cdList.size()];
					cdList.toArray(categoryArr);
				}else{
					categoryArr = category.getChildCategorys();
				}
				
				Set<AttributeDefination> set = new HashSet<AttributeDefination>();
				if (categoryArr == null) {
					continue;
				}
				for (CategoryDef cat : categoryArr) {
					ResourceDef[] resArr = cat.getResourceDefs();
					if (resArr == null) {
						continue;
					}
					for (ResourceDef resourceDef : resArr) {
						ResourceMetricDef[] resMetricDef = resourceDef.getMetricDefs();
						if (resMetricDef == null) {
							continue;
						}
						for (ResourceMetricDef resourceMetricDef : resMetricDef) {
							//判断是否是信息指标
							if (resourceMetricDef.getMetricType() == MetricTypeEnum.InformationMetric) {
								AttributeDefination attr = new AttributeDefination();
								if("sysName".equals(resourceMetricDef.getId())){
									continue;
								}
								attr.setAttributeId(resourceMetricDef.getId());
								attr.setAttributeName(resourceMetricDef.getName());
								set.add(attr);
							}
						}
					}
				}
				List<AttributeDefination> definationList = new ArrayList<AttributeDefination>();
				definationList.addAll(set);
				moType.setDefinations(definationList);
//				for (AttributeDefination attributeDefination : definationList) {
//					System.out.println(attributeDefination.getAttributeId()+"=="+attributeDefination.getAttributeName());
//				}
				listMoTypes.add(moType);
			}
		}

		BusinessModule[] businessModules = BusinessModule.values();
		for(BusinessModule businessModule : businessModules) {
			MOType moType = new MOType();
			moType.setMoTypeId(businessModule.getKey());
			moType.setMoTypeName(businessModule.getValue());
			moType.setDefinations(null);
			listMoTypes.add(moType);
		}

		Result<List<MOType>> result = new Result<List<MOType>>();
		result.setResultCode(ResultCode.SUCCESS);
		result.setData(listMoTypes);
		return JSONObject.toJSONString(result);
	}

	/*第三方业务模块
	 */
	enum BusinessModule {

		TRAP("TRAP","TRAP"),SYSLOG("SYSLOG","SYSLOG"),IP_MAC_PORT("IP_MAC_PORT","IP_MAC_PORT");
		private String key;
		private String value;

		BusinessModule(String key, String val) {
			this.key = key;
			this.value = val;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
}
