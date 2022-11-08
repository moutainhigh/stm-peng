package com.mainsteam.stm.state.ext.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/17.
 */
@Deprecated
//@Component("eventTemplateUtil")
public class EventTemplateUtil implements ApplicationListener<ContextRefreshedEvent>{

    private static final Log logger = LogFactory.getLog(EventTemplateUtil.class);

    private static final Map<String, String> eventMap = new HashMap<>(10);

    private void start() throws DocumentException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("event_template.xml");
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(resourceAsStream);

            List<Node> metricNodes = document.selectNodes("/fragments/resource/metric");
            if(null != metricNodes && !metricNodes.isEmpty()) {
                for(Node node : metricNodes) {
                    Element element = (Element)node;
                    List<Element> events = element.elements();
                    if(null != events && !events.isEmpty()) {
                        String resourceName = element.getParent().attributeValue("id");
                        for(Element event : events) {
                            String metricId = element.attributeValue("id");
                            String metricType = element.attributeValue("type");
                            String eventType = event.getName();
                            String eventContent = event.getTextTrim();
                            eventMap.put(resourceName + "_" + metricId + "_" + metricType + "_"+ eventType, eventContent);
                        }
                    }
                }
            }

            if(logger.isDebugEnabled()) {
                logger.debug("alarm event templates : " + eventMap);
            }

        } catch (DocumentException e) {
            throw e;
        }
    }

    public String getTemplate(String key) {
        return eventMap.get(key);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(null == contextRefreshedEvent.getApplicationContext().getParent()){
            try {
                start();
            } catch (DocumentException e) {
                if(logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
