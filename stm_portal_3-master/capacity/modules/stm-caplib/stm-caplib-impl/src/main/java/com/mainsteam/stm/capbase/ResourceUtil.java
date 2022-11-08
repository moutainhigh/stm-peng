package com.mainsteam.stm.capbase;

import com.mainsteam.stm.caplib.dict.*;
import com.mainsteam.stm.caplib.resource.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.*;

public class ResourceUtil {

    private static final String UNIT = "unit";
    private static final String METRICID = "metricid";
    private static final String INSTANCE_NAME = "InstanceName";
    private static final String INSTANCE_ID = "InstanceId";
    private static final String INSTANTIATION = "Instantiation";
    private static final String PROPERTY = "Property";
    private static final String PROPERTIES = "Properties";
    private static final String METRIC = "Metric";
    private static final String METRICS = "Metrics";
    private static final String PARENTID = "parentid";
    private static final String TYPE = "type";
    private static final String ICON = "icon";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String CATEGORY = "category";
    private static final String AUTODISCOVERY = "autodiscovery";
    private static final String ID = "id";
    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";
    private Map<String, String> parentIdMap = new HashMap<String, String>();
    private Map<String, String> categoryMap = new HashMap<String, String>();
    private boolean isEncrypt = false;
    private static final Log logger = LogFactory.getLog(ResourceUtil.class);

    public ResourceUtil() {
    }

    /**
     * 加载resource的xml文件，因为xml的元素都是叶子节点，通过parentid来指定从属关系，所以只需要遍历根节点下的 所有子节点即可。
     *
     * @param filePath 文件路径
     * @return 返回所有的分类
     */
    public ResourceDef loadResource(String filePath) {
        try {

            SAXReader reader = new SAXReader();
            InputStream ifile = new FileInputStream(filePath);
            InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
            reader.setEncoding("UTF-8");
            Document document = reader.read(ir);// 读取XML文件
            Element root = document.getRootElement();// 得到根节点

            // SAXReader saxReader = new SAXReader();
            // saxReader.setEncoding("UTF-8");
            // Document doc = saxReader.read(new File(filePath));
            // Element root = doc.getRootElement();

            Element settingElement = root.element("GlobalMetricSetting");
            ResourceMetricSetting gloableSetting = this
                    .initResourceMetricSetting(settingElement);

            Attribute isEncryptElem = settingElement
                    .attribute(CapacityConst.IS_ENCRYPT);

            if (null != isEncryptElem) {
                if (TRUE.equalsIgnoreCase(isEncryptElem.getText())) {
                    isEncrypt = true;
                }
            }

            @SuppressWarnings("unchecked")
            List<Element> resElementList = root.elements("Resource");
            ResourceDef[] resourceDefs = new ResourceDef[resElementList.size()];
            for (int i = 0; i < resourceDefs.length; i++) {
                resourceDefs[i] = this.initResourceDef(resElementList.get(i),
                        gloableSetting);
            }

            ResourceDef rootResourceDef = null;
            for (int i = 0; i < resourceDefs.length; i++) {
                String parentId = parentIdMap.get(resourceDefs[i].getId());
                if (StringUtils.isEmpty(parentId)) {
                    rootResourceDef = resourceDefs[i];
                    rootResourceDef.setMain(true);
                    break;
                }
            }
            if (null == rootResourceDef) {
                logger.error("null RootResourceDef:" + filePath);
                return null;
            }
            ResourceDef[] childResourceDefs = this.getChildResourceDefs(
                    rootResourceDef, resourceDefs);
            rootResourceDef.setChildResourceDefs(childResourceDefs);
            return rootResourceDef;
        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 返回当前分类下的一级子分类
     *
     * @param resourceDef  指定当前的分类
     * @param resourceDefs 包含所有的分类
     * @return 返回所有分类中，父类属于ResourceDef的子分类
     */
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

    /**
     * 初始化一个ResourceDef对象，包括id，name，并将它放入id-parentid对应关系的map中
     *
     * @param element        一个dom树元素
     * @param gloableSetting 一个全局元素
     * @return 返回一个新的ResourceDef对象
     */
    @SuppressWarnings("unchecked")
    private ResourceDef initResourceDef(Element element,
                                        ResourceMetricSetting gloableSetting) {
        ResourceDef resourceDef = new ResourceDef();

        String resourceIdAttrValue = getAttrValue(element, ID);
        resourceDef.setId(resourceIdAttrValue);
        categoryMap.put(resourceDef.getId(), getAttrValue(element, CATEGORY));
        resourceDef.setDescription(element.attributeValue(DESCRIPTION));
        String attrValue = element.attributeValue(NAME);
        resourceDef.setName(attrValue);
        resourceDef.setIcon(getAttrValue(element, ICON));
        resourceDef.setType(getAttrValue(element, TYPE));
        if (StringUtils.equalsIgnoreCase(getAttrValue(element, AUTODISCOVERY), FALSE))
            resourceDef.setAutodiscovery(false);
        parentIdMap.put(resourceDef.getId(), getAttrValue(element, PARENTID));

        List<Element> metricElementList = element.element(METRICS).elements(
                METRIC);
        ResourceMetricDef[] metricDefs = new ResourceMetricDef[metricElementList
                .size()];
        for (int i = 0; i < metricDefs.length; i++) {
            metricDefs[i] = this.initResourceMetricDef(
                    metricElementList.get(i), resourceDef, gloableSetting);
        }
        resourceDef.setMetricDefs(metricDefs);

        if (null != element.element(PROPERTIES)) {
            List<Element> propElementList = element.element(PROPERTIES)
                    .elements(PROPERTY);
            if (null != propElementList) {
                ResourcePropertyDef[] propertyDefs = new ResourcePropertyDef[propElementList
                        .size()];
                for (int i = 0; i < propertyDefs.length; i++) {
                    propertyDefs[i] = this.initResourcePropertyDef(
                            propElementList.get(i), metricDefs);
                }
                resourceDef.setPropertyDefs(propertyDefs);
            }
        }
        Element insElement = element.element(INSTANTIATION);
        if (insElement != null)
            resourceDef.setInstantiationDef(this
                    .initResourceInstantiationDef(insElement));

        return resourceDef;
    }


    /**
     * 初始化一个ResourceInstedDef对象
     *
     * @param element Instantiation树节点
     * @return 返回一个新的ResourceInstedDef对象
     */
    private ResourceInstedDef initResourceInstantiationDef(Element element) {
        ResourceInstedDef insDef = new ResourceInstedDef();
        insDef.setInstanceId(getAttrValue(element, INSTANCE_ID));
        insDef.setInstanceName(getAttrValue(element, INSTANCE_NAME));
        return insDef;
    }

    /**
     * 初始化一个ResourcePropertyDef对象
     *
     * @param element    Property树节点
     * @param metricDefs 指标集合
     * @return 返回一个新的ResourcePropertyDef对象
     */
    private ResourcePropertyDef initResourcePropertyDef(Element element,
                                                        ResourceMetricDef[] metricDefs) {
        ResourcePropertyDef propertyDef = new ResourcePropertyDef();

        propertyDef.setId(getAttrValue(element, ID));
        propertyDef.setName(element.attributeValue(NAME));
        String metricidStr = getAttrValue(element, METRICID);
        for (int i = 0; i < metricDefs.length; i++) {
            if (metricDefs[i].getId().equalsIgnoreCase(metricidStr)) {
                propertyDef.setResourceMetric(metricDefs[i]);
                break;
            }
        }

        return propertyDef;
    }

    /**
     * 初始化一个ResourceMetricDef对象
     *
     * @param element        Metric树节点
     * @param resourceDef    资源实例
     * @param gloableSetting 全局设置参数
     * @return 返回一个新的ResourceMetricDef对象
     */
    private ResourceMetricDef initResourceMetricDef(Element element, ResourceDef resourceDef, ResourceMetricSetting gloableSetting) {
        ResourceMetricDef metricDef = new ResourceMetricDef();
        metricDef.setId(getAttrValue(element, ID));

        String isDisplayStr = getTextValue(element, "IsDisplay");
        Boolean isDisplay = Boolean.valueOf(isDisplayStr);
        metricDef.setDisplay(isDisplay);

        metricDef.setDisplayOrder(getAttrValue(element.element("IsDisplay"),
                "displayOrder"));

        String metricTypeStr = getAttrValue(element, "style");
        if (StringUtils.isNotEmpty(metricTypeStr)) {
            MetricTypeEnum metricType = MetricTypeEnum.valueOf(metricTypeStr);
            metricDef.setMetricType(metricType);
        }
        String isTableStr = getAttrValue(element, "isTable");
        if (StringUtils.isNotEmpty(isTableStr)
                && TRUE.equalsIgnoreCase(isTableStr)) {
            metricDef.setIsTable(true);
        }
        metricDef.setDescription(element.attributeValue(DESCRIPTION));
        metricDef.setName(element.attributeValue(NAME));
        metricDef.setUnit(element.attributeValue(UNIT));
        String isMonitorStr = getTextValue(element, "IsMonitor");
        if (isMonitorStr != null)
            metricDef.setMonitor(Boolean.parseBoolean(isMonitorStr));
        else
            metricDef.setMonitor(gloableSetting.isDefaultMonitor());
        String isEditStr = getTextValue(element, "IsEdit");
        if (isEditStr != null)
            metricDef.setEdit(Boolean.parseBoolean(isEditStr));
        else
            metricDef.setEdit(gloableSetting.isDefaultEditable());
        String isAlertStr = getTextValue(element, "IsAlert");
        if (isAlertStr != null)
            metricDef.setAlert(Boolean.parseBoolean(isAlertStr));
        else
            metricDef.setAlert(gloableSetting.isDefaultAlert());

        metricDef.setDefaultFlapping(gloableSetting.getDefaultFlapping());
        String defaultFlapping = getTextValue(element, "DefaultFlapping");
        if (defaultFlapping != null) {
            metricDef.setDefaultFlapping(Integer.parseInt(defaultFlapping));
        }

        String defaultMonitorFreqStr = getTextValue(element,
                "DefaultMonitorFreq");
        if (defaultMonitorFreqStr != null) {
            FrequentEnum frequentEnum = FrequentEnum
                    .valueOf(defaultMonitorFreqStr);
            metricDef.setDefaultMonitorFreq(frequentEnum);
        } else {
            metricDef.setDefaultMonitorFreq(gloableSetting
                    .getDefaultMonitorFreq());
        }
        String supportMonitorFreqStr = getTextValue(element,
                "SupportMonitorFreq");
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

        // version 7.3.1 require default threshold of all levels.
        EnumSet<PerfMetricStateEnum> stateEnumMap = EnumSet.of(PerfMetricStateEnum.Normal, PerfMetricStateEnum.Minor, PerfMetricStateEnum.Major, PerfMetricStateEnum.Critical);
        ThresholdDef[] thresholdDefs = new ThresholdDef[4];
        int p = 0;

        Element thresholdsElement = element.element("Thresholds");
        if (thresholdsElement != null) {
            List<Element> thresholdList = thresholdsElement.elements("Threshold");
            for (Element thresholdElements : thresholdList) {
                thresholdDefs[p] = initThresHoldDef(thresholdElements);
                stateEnumMap.remove(thresholdDefs[p].getState());
                if (StringUtils.isEmpty(thresholdDefs[p].getThresholdExpression()))
                    thresholdDefs[p].setThresholdExpression(metricDef.getId() + " " + thresholdDefs[p].getOperator().getOprateChar() + " " + thresholdDefs[p].getDefaultvalue());
                p++;
            }
        }

        for (PerfMetricStateEnum state : stateEnumMap) {
            thresholdDefs[p] = new ThresholdDef();
            thresholdDefs[p].setState(state);
            switch (metricDef.getMetricType()) {
                case AvailabilityMetric:
                    switch (state) {
                        case Critical:
                            thresholdDefs[p].setThresholdExpression(metricDef.getId() + " == 0 OR " + metricDef.getId() + " == 16");
                            break;
                        default:
                            thresholdDefs[p].setThresholdExpression("");
                    }
                    break;
                case InformationMetric:
//                    switch (state) {
//                        case Minor:
//                            thresholdDefs[p].setThresholdExpression(metricDef.getId() + " HAS_CHANGED");
//                            break;
//                        default:
                            thresholdDefs[p].setThresholdExpression("");
//                    }
                    break;
                case PerformanceMetric:
                    thresholdDefs[p].setThresholdExpression("");
                    break;
            }
            p++;
        }
        metricDef.setThresholdDefs(thresholdDefs);
        return metricDef;
    }

    /**
     * 初始化一个ThresholdDef对象
     *
     * @param element Threshold树节点
     * @return 返回一个新的ThresholdDef对象
     */
    private ThresholdDef initThresHoldDef(Element element) {
        ThresholdDef thresHoldDef = new ThresholdDef();

        thresHoldDef.setDefaultvalue(getAttrValue(element, "defaultvalue"));
        String operatorStr = element.attributeValue("operator");
        if (operatorStr != null) {
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
        }
        String stateidStr = getAttrValue(element, "stateid");
        thresHoldDef.setState(PerfMetricStateEnum.valueOf(stateidStr));

        String expression = element.attributeValue("expression");
//        if (expression == null)
//            expression = "";
        thresHoldDef.setThresholdExpression(expression);

        return thresHoldDef;
    }

    /**
     * 初始化一个ResourceMetricSetting对象
     *
     * @param element GlobalMetricSetting树节点
     * @return 返回一个新的ResourceMetricSetting对象
     */
    private ResourceMetricSetting initResourceMetricSetting(Element element) {
        ResourceMetricSetting metricSetting = new ResourceMetricSetting();

        metricSetting.setDefaultAlert(Boolean.parseBoolean(element
                .elementTextTrim("GlobalIsAlert")));
        metricSetting.setDefaultFlapping(Integer.parseInt(element
                .elementTextTrim("GlobalDefaultFlapping")));
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

    /**
     * 获取Category Map集合
     *
     * @return
     */
    public Map<String, String> getCategoryMap() {
        return categoryMap;
    }

    private String getAttrValue(Element element, String attrKey) {
        if (null == element) {
            return "";
        }
        String text = element.attributeValue(attrKey);
        if (this.isEncrypt) {
            return TextUtil.decrypt(text, CapacityConst.ENDECODER_KEY);
        }
        return text;
    }

    private String getTextValue(Element element, String attrKey) {
        if (null == element) {
            return "";
        }
        String text = element.elementTextTrim(attrKey);
        if (this.isEncrypt) {
            return TextUtil.decrypt(text, CapacityConst.ENDECODER_KEY);
        }
        return text;
    }
}
