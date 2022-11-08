package com.mainsteam.stm.plugin.was;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.management.Attribute;
import javax.management.AttributeList;


public class AttributeListCollector {


    /**
     * An inner class to hold bean path
     */
    private static class AttributePath {

        /**
         * javabean xpath
         */
        final StringBuffer m_path = new StringBuffer();

        /**
         * the raw data from WAS
         */
        AttributeList m_list;

        /**
         * Constructors.
         * @param basePath - root
         * @param list - raw data
         */
        public AttributePath(final String basePath, final AttributeList list) {
            m_path.append(basePath);
            this.m_list = list;
        }

        /**
         * the getter for list 
         * @return result
         */
        public AttributeList getList() {
            return m_list;
        }

        /**
         * Get current filter process data path. Notes the process is filter in
         * a object atrribte tree.
         *
         * @return the path cf current filter process,split by "."
         */
        public String getPath() {
            return m_path.toString();
        }

        /**
         * meet a sub attribute
         * @param subAttribute 
         */
        public void enter(final String subAttribute) {
            if (subAttribute != null) {
                if (m_path.length() > 0)
                    m_path.append(".").append(subAttribute);
                else
                    m_path.append(subAttribute);
            }

        }

    }

    /**
     * stack
     */
    Stack<AttributePath> m_stack = new Stack<AttributePath>();

    /**
     * The result of data filtered by select condition. if not data match,
     * return a zero size list.
     */
    @SuppressWarnings("rawtypes")
    Map m_resultMap = new HashMap();

    /**
     * valid attributs
     */
    private Set<String> m_attributesSet;

    /**
     * AttributeListFilter will filter the WebSphere Config object.
     *
     * Example Code:
     *
     *
     * AttributeList list = configService.getAttributes(session, AppNameID[0],
     * null, true); //get the config attribute.
     *
     * HashMap conditions = new HashMap();
     * conditions.put("deploymentTargets[name]", "server2"); // the result must
     * be match deploymentTargets[name]=server2 <br>
     * AttributeListFilter filter = new AttributeListFilter( conditions); <br>
     * filter.doFilter(list, keyValue); //do filter.
     *
     * @param attriSet 
     */
    public AttributeListCollector(final Set<String> attriSet) {
        this.m_attributesSet = attriSet;

    }

    /**
     * Get the filter result.
     *
     * @return the filter result in arraylist.
     */

    @SuppressWarnings("rawtypes")
    public Map getResultMap() {
        return m_resultMap;
    }

    /**
     * Filter the attributeList in config object.
     * 
     * @param basePath 
     * @param list 
     */
    @SuppressWarnings({ "rawtypes" })
    public void doFilter(final String basePath, final AttributeList list) {

        if (list == null) {
            return;
        }

        AttributePath t_ap = new AttributePath(basePath, list);

        m_stack.push(t_ap);
        while (!m_stack.isEmpty()) {
            
            AttributePath t_stackAP = m_stack.pop();

            String t_workPath = t_stackAP.getPath().toString();
            List t_workList = t_stackAP.getList();

            for (Object t_item : t_workList) {
                
                // AtrribulteList also is a ArrayList
                if (t_item instanceof AttributeList) {

                    AttributePath t_enterAttributePath = new AttributePath(t_workPath.toString(),
                            (AttributeList) t_item);
                    m_stack.push(t_enterAttributePath);
                    continue;
                }

                if (t_item instanceof Attribute) {

                    foundAttribute((Attribute) t_item, t_workPath);
                } 
            }
        }
    }
    
    /**
     * create an attribute
     * @param attr 
     * @param root 
     */
    @SuppressWarnings("unchecked")
    private void foundAttribute(final Attribute attr, final String root) {

        String t_attributeName = attr.getName();
        
        Object t_attributeValue = attr.getValue();
        
        if ("LDAPUserRegistry".equals(root) && "hosts".equals(t_attributeName)) {
            List<AttributeList> t_arrayValue = (List<AttributeList>) attr.getValue();
            if (t_arrayValue != null && t_arrayValue.size() > 0) {
                t_attributeValue = t_arrayValue.get(0);
            }
        }
        
        
        if (t_attributeValue instanceof AttributeList) {

            AttributePath t_enterAttributePath = new AttributePath(root.toString(),
                    (AttributeList) t_attributeValue);
            t_enterAttributePath.enter(t_attributeName);
            m_stack.push(t_enterAttributePath);

        } else {

            String t_attributePath = new StringBuffer(root).append("[").append(t_attributeName).append("]")
                    .toString();

            // Not record all config,
            if (m_attributesSet != null) {
                if (m_attributesSet.contains(t_attributePath))
                    m_resultMap
                            .put(t_attributePath, t_attributeValue == null ? null : String.valueOf(t_attributeValue));

            } else {

                m_resultMap.put(t_attributePath, t_attributeValue == null ? null : String.valueOf(t_attributeValue));
            }

        }
    }

}

