package com.mainsteam.stm.capvalidate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceInstedDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricSetting;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.caplib.resource.ThresholdDef;

public class ResourceUtil {

	private static final String DESCRIPTION = "description";
	private static final String CATEGORY = "category";
	private static final String ID = "id";
	private Map<String, String> parentIdMap = new HashMap<String, String>();
	private Map<String, String> categoryMap = new HashMap<String, String>();

	private List<String> validateError = new ArrayList<String>();

	public ResourceUtil() {}

	public ResourceDef loadResource(String filePath) {

		try {

			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(new File(filePath));
			Element root = doc.getRootElement();

			Element settingElement = root.element("GlobalMetricSetting");
			ResourceMetricSetting gloableSetting = this.initResourceMetricSetting(settingElement);

			@SuppressWarnings("unchecked")
			List<Element> resElementList = root.elements("Resource");
			ResourceDef[] resourceDefs = new ResourceDef[resElementList.size()];
			for (int i = 0; i < resourceDefs.length; i++) {
				resourceDefs[i] = this.initResourceDef(resElementList.get(i), gloableSetting, filePath);
			}

			ResourceDef rootResourceDef = null;
			for (int i = 0; i < resourceDefs.length; i++) {
				String parentId = parentIdMap.get(resourceDefs[i].getId());
				if (resourceDefs[i].getId().equals(parentId)) {
					this.validateError.add("文件" + filePath + "同一个节点"
							+ resElementList.get(0).getPath() + "的属性parentid值"
							+ parentId + "和属性id值不能相同");
				}
				if (parentId == null) {
					if (rootResourceDef == null) {
						rootResourceDef = resourceDefs[i];
					} else {
						this.validateError.add("文件" + filePath + "节点"
								+ resElementList.get(0).getPath()
								+ "属性parentid只能配置一个为NULL");
					}
				}
				if (parentId == null) {
					String type = resourceDefs[i].getType();
					if (type != null && !type.trim().equals("")) {
						this.validateError.add("文件" + filePath + "节点"
								+ resElementList.get(0).getPath() + "属性type值"
								+ type + "不需要配置(原因：属性parentid为空,是主资源)");
					}
				} else {
					String category = categoryMap.get(resourceDefs[i].getId());
					if (category != null && !category.trim().equals("")) {
						this.validateError.add("文件" + filePath + "节点"
								+ resElementList.get(0).getPath()
								+ "属性category值" + category
								+ "不需要配置(原因：属性parentid不为空,是子资源)");
					}
				}
			}
			if (null == rootResourceDef) {
				return null;
			}
			ResourceDef[] childResourceDefs = this.getChildResourceDefs(
					rootResourceDef, resourceDefs);
			rootResourceDef.setChildResourceDefs(childResourceDefs);
			return rootResourceDef;
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return null;
	}

	private ResourceDef[] getChildResourceDefs(ResourceDef resourceDef,
			ResourceDef[] resourceDefs) {
		List<ResourceDef> childResourceDefList = new ArrayList<ResourceDef>();
		for (int i = 0; i < resourceDefs.length; i++) {
			String parentId = parentIdMap.get(resourceDefs[i].getId());
			if (resourceDef.getId().equalsIgnoreCase(parentId)) {
				resourceDefs[i].setParentResourceDef(resourceDef);
				resourceDefs[i].setChildResourceDefs(getChildResourceDefs(
						resourceDefs[i], resourceDefs));
				childResourceDefList.add(resourceDefs[i]);
			}
		}

		if (childResourceDefList.isEmpty())
			return null;

		ResourceDef[] childResourceDefs = new ResourceDef[childResourceDefList
				.size()];
		for (int i = 0; i < childResourceDefs.length; i++)
			childResourceDefs[i] = childResourceDefList.get(i);

		return childResourceDefs;
	}

	@SuppressWarnings("unchecked")
	private ResourceDef initResourceDef(Element element,ResourceMetricSetting gloableSetting, String filePath) {
 		ResourceDef resourceDef = new ResourceDef();

		resourceDef.setId(element.attributeValue(ID));
		categoryMap.put(resourceDef.getId(), element.attributeValue(CATEGORY));
		resourceDef.setDescription(element.attributeValue(DESCRIPTION));
		resourceDef.setName(element.attributeValue("name"));
		resourceDef.setIcon(element.attributeValue("icon"));
		resourceDef.setType(element.attributeValue("type"));
		String parentid = element.attributeValue("parentid");
		parentIdMap.put(resourceDef.getId(), parentid);

		List<Element> metricElementList = element.element("Metrics").elements("Metric");
		ResourceMetricDef[] metricDefs = new ResourceMetricDef[metricElementList.size()];
		for (int i = 0; i < metricDefs.length; i++) {
			metricDefs[i] = this.initResourceMetricDef(metricElementList.get(i), resourceDef, gloableSetting);
		}
		resourceDef.setMetricDefs(metricDefs);

		if (null != element.element("Properties")) {
			List<Element> propElementList = element.element("Properties").elements("Property");
			if (null != propElementList) {
				ResourcePropertyDef[] propertyDefs = new ResourcePropertyDef[propElementList
						.size()];
				for (int i = 0; i < propertyDefs.length; i++) {
					propertyDefs[i] = this.initResourcePropertyDef(
							propElementList.get(i), metricDefs);
					
					//Property中的metricid必须在Metric存在
					boolean isExist = false;
					for(ResourceMetricDef def : metricDefs){
						if(propertyDefs[i].getResourceMetric()==null){
							break;
						}else if(StringUtils.equals(def.getId(), propertyDefs[i].getResourceMetric().getId())){
							isExist = true;
							break;
						}
					}
					if(!isExist && !StringUtils.equals("processRemark", propertyDefs[i].getId()) &&
							!StringUtils.equals("instanceIdKeyValues", propertyDefs[i].getId())){
						this.validateError.add("文件" + filePath + "中，属性的metricid在resource中找不到，属性id为" + propertyDefs[i].getId());
					}
				}
				resourceDef.setPropertyDefs(propertyDefs);
			}
		}else{
			this.validateError.add("文件" + filePath + "中，resource的id为" + resourceDef.getId() + "缺少Properties，这是必须存在的");
		}
		
		Element insElement = element.element("Instantiation");
		if (insElement != null){
			ResourceInstedDef instance = this.initResourceInstantiationDef(insElement);
			String instanceName = instance.getInstanceName();
			ResourcePropertyDef[] pros = resourceDef.getPropertyDefs();
			if(StringUtils.isBlank(instanceName)){
				this.validateError.add("文件" + filePath + "中，resource的id为" + resourceDef.getId() 
						+ "，其中Instantiation的InstanceName为空");
			}else{
				boolean isExistName = false;
				for(ResourcePropertyDef def : pros){
					if(StringUtils.equals(def.getId(), instanceName)){
						isExistName = true;
						break;
					}
				}
				if(!isExistName){
					this.validateError.add("文件" + filePath + "中，resource的id为" + resourceDef.getId() 
							+ ",其中Instantiation的InstanceName在Properties中找不到");
				}
			}
			
			String[] ids = instance.getInstanceId();
			if(ids == null || ids.length == 0){
				this.validateError.add("文件" + filePath + "中，resource的id为" + resourceDef.getId()
						+ "其中Instantiation的InstanceId是必须存在的");
			}else{
				//判断是否存在
				String notExistIds = "";
				for(String id : ids){
					boolean isExist = false;
					for(ResourcePropertyDef def : pros){
						if(StringUtils.equals(id, def.getId())){
							isExist = true;
							break;
						}
					}
					if(!isExist){
						notExistIds += id + ";";
					}
				}
				
				if(StringUtils.isNotBlank(notExistIds)){
					this.validateError.add("文件" + filePath + "中，resource的id为" + resourceDef.getId() 
							+ ",其中Instantiation的InstanceId 【" + notExistIds + "】 在Properties中找不到");
				}
			}
			
			
			resourceDef.setInstantiationDef(instance);
		}else{
			this.validateError.add("文件" + filePath + "中，resource的id为" + resourceDef.getId() + "缺少Instantiation，这是必须存在的");
		}

		return resourceDef;
	}

	private ResourceInstedDef initResourceInstantiationDef(Element element) {
		ResourceInstedDef insDef = new ResourceInstedDef();
		insDef.setInstanceId(element.attributeValue("InstanceId"));
		insDef.setInstanceName(element.attributeValue("InstanceName"));
		return insDef;
	}

	private ResourcePropertyDef initResourcePropertyDef(Element element,
			ResourceMetricDef[] metricDefs) {
		ResourcePropertyDef propertyDef = new ResourcePropertyDef();

		propertyDef.setId(element.attributeValue(ID));
		propertyDef.setName(element.attributeValue("name"));
		String metricidStr = element.attributeValue("metricid");
		for (int i = 0; i < metricDefs.length; i++) {
			if (metricDefs[i].getId().equalsIgnoreCase(metricidStr)) {
				propertyDef.setResourceMetric(metricDefs[i]);
				break;
			}
		}

		return propertyDef;
	}

	private ResourceMetricDef initResourceMetricDef(Element element,
			ResourceDef resourceDef, ResourceMetricSetting gloableSetting) {
		ResourceMetricDef metricDef = new ResourceMetricDef();
		metricDef.setId(element.attributeValue(ID));

		String isDisplayStr = element.attributeValue("isDisplay");
		Boolean isDisplay = Boolean.valueOf(isDisplayStr);
		metricDef.setDisplay(isDisplay);
		metricDef.setDisplayOrder(element.attributeValue("displayOrder"));

		String metricTypeStr = element.attributeValue("style");
		if (StringUtils.isNotEmpty(metricTypeStr)) {
			MetricTypeEnum metricType = MetricTypeEnum.valueOf(metricTypeStr);
			metricDef.setMetricType(metricType);
		}

		metricDef.setDescription(element.attributeValue(DESCRIPTION));
		metricDef.setName(element.attributeValue("name"));
		metricDef.setUnit(element.attributeValue("unit"));
		String isMonitorStr = element.elementTextTrim("IsMonitor");
		if (isMonitorStr != null)
			metricDef.setMonitor(Boolean.parseBoolean(isMonitorStr));
		else
			metricDef.setMonitor(gloableSetting.isDefaultMonitor());
		String isEditStr = element.elementTextTrim("IsEdit");
		if (isEditStr != null)
			metricDef.setEdit(Boolean.parseBoolean(isEditStr));
		else
			metricDef.setEdit(gloableSetting.isDefaultEditable());
		String isAlertStr = element.elementTextTrim("IsAlert");
		if (isAlertStr != null)
			metricDef.setAlert(Boolean.parseBoolean(isAlertStr));
		else
			metricDef.setAlert(gloableSetting.isDefaultAlert());
		String defaultMonitorFreqStr = element.elementTextTrim("DefaultMonitorFreq");
		if (defaultMonitorFreqStr != null) {
			FrequentEnum frequentEnum = FrequentEnum.valueOf(defaultMonitorFreqStr);
			metricDef.setDefaultMonitorFreq(frequentEnum);
		} else {
			metricDef.setDefaultMonitorFreq(gloableSetting.getDefaultMonitorFreq());
		}
		String supportMonitorFreqStr = element.elementTextTrim("SupportMonitorFreq");
		if (supportMonitorFreqStr != null) {
			String[] suppStrs = StringUtils.split(supportMonitorFreqStr, ",");
			FrequentEnum[] frequentEnums = new FrequentEnum[suppStrs.length];
			for (int i = 0; i < suppStrs.length; i++) {
				FrequentEnum frequentEnum = FrequentEnum.valueOf(suppStrs[i]);
				frequentEnums[i] = frequentEnum;
			}
			metricDef.setSupportMonitorFreqs(frequentEnums);
		} else {
			metricDef.setSupportMonitorFreqs(gloableSetting
					.getSupportMonitorFreqs());
		}
		metricDef.setResourceDef(resourceDef);
		Element thersholdsElement = element.element("Thresholds");
		if (thersholdsElement != null) {
			@SuppressWarnings("unchecked")
			List<Element> thersholdList = thersholdsElement
					.elements("Threshold");
			ThresholdDef[] thresHoldDefs = new ThresholdDef[thersholdList
					.size()];
			for (int i = 0; i < thresHoldDefs.length; i++) {
				thresHoldDefs[i] = this.initThresHoldDef(thersholdList.get(i));
			}
			metricDef.setThresholdDefs(thresHoldDefs);
		}

		return metricDef;
	}

	private ThresholdDef initThresHoldDef(Element element) {
		ThresholdDef thresHoldDef = new ThresholdDef();

		thresHoldDef.setDefaultvalue(element.attributeValue("defaultvalue"));
		String operatorStr = element.attributeValue("operator");
		if (operatorStr.equalsIgnoreCase(">="))
			thresHoldDef.setOperator(OperatorEnum.GreatEqual);
		else if (operatorStr.equalsIgnoreCase(">"))
			thresHoldDef.setOperator(OperatorEnum.Great);
		else if (operatorStr.equalsIgnoreCase("="))
			thresHoldDef.setOperator(OperatorEnum.Equal);
		else if (operatorStr.equalsIgnoreCase("<="))
			thresHoldDef.setOperator(OperatorEnum.LessEqual);
		else if (operatorStr.equalsIgnoreCase("<"))
			thresHoldDef.setOperator(OperatorEnum.Less);
		String stateidStr = element.attributeValue("stateid");

		thresHoldDef.setState(PerfMetricStateEnum.valueOf(stateidStr));
		return thresHoldDef;
	}

	private ResourceMetricSetting initResourceMetricSetting(Element element) {
		ResourceMetricSetting metricSetting = new ResourceMetricSetting();

		metricSetting.setDefaultAlert(Boolean.parseBoolean(element
				.elementTextTrim("GlobalIsAlert")));
		metricSetting.setDefaultEditable(Boolean.parseBoolean(element
				.elementTextTrim("GlobalIsEdit")));
		metricSetting.setDefaultMonitor(Boolean.parseBoolean(element
				.elementTextTrim("GlobalIsMonitor")));
		String defaultMonitorFreqStr = element
				.elementTextTrim("GlobalDefaultMonitorFreq");

		FrequentEnum freqEnum = FrequentEnum.valueOf(defaultMonitorFreqStr);
		metricSetting.setDefaultMonitorFreq(freqEnum);

		String supportMonitorFreqStr = element
				.elementTextTrim("GlobalsupportMonitorFreq");

		if (supportMonitorFreqStr != null) {
			String[] suppStrs = StringUtils.split(supportMonitorFreqStr, ",");
			FrequentEnum[] frequentEnums = new FrequentEnum[suppStrs.length];
			for (int i = 0; i < suppStrs.length; i++) {
				FrequentEnum frequentEnum = FrequentEnum.valueOf(suppStrs[i]);
				frequentEnums[i] = frequentEnum;
			}
			metricSetting.setSupportMonitorFreqs(frequentEnums);
		}
		return metricSetting;
	}

	public Map<String, String> getCategoryMap() {
		return categoryMap;
	}

	public List<String> getValidateError() {
		return validateError;
	}

}
