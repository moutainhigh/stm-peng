package com.mainsteam.stm.plugin.wps.util.jmx.internalimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.PmiModuleConfig;
import com.ibm.websphere.pmi.stat.StatDescriptor;
import com.ibm.websphere.pmi.stat.WSStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.ws.pmi.stat.BoundedRangeStatisticImpl;
import com.ibm.ws.pmi.stat.CountStatisticImpl;
import com.ibm.ws.pmi.stat.RangeStatisticImpl;
import com.ibm.ws.pmi.stat.TimeStatisticImpl;
import com.mainsteam.stm.plugin.wps.PluginException;
import com.mainsteam.stm.plugin.wps.WPSConnectionHelper;
import com.mainsteam.stm.plugin.wps.WPSConnectionInfo;
import com.mainsteam.stm.plugin.wps.util.jmx.Statement;

/**
 * 
 * PMI ȡֵ���÷���
 * <br>
 *  
 * <p>
 * Create on : 2012-9-13<br>
 * <p>
 * </p>
 * <br>
 * @author chuzunying@qzserv.com.cn<br>
 * @version qzserv.mserver.plugins.wps-6.2.0 v1.0
 * <p>
 *<br>
 * <strong>Modify History:</strong><br>
 * user     modify_date    modify_content<br>
 * -------------------------------------------<br>
 * <br>
 */
public class PMIStatement extends Statement {

    /**
     * property in cmds.xml
     */
    private static final String S_FILTER_VALUE = "filterValue";

    /**
     * the table which hold all available attributes
     */
    private static HashSet<String> s_attributes = new HashSet<String>();
    static {
        s_attributes.add("threadPoolModule");
        s_attributes.add("jvmRuntimeModule");
        s_attributes.add("transactionModule");
        s_attributes.add("connectionPoolModule");
        s_attributes.add("servletSessionsModule");
        s_attributes.add("webAppModule");
    }
    
    /**
     * Pref MBean instance.
     */
    private ObjectName m_perfBean;
    
    /**
     * config list on server
     */
    private PmiModuleConfig[] m_configs;
    
    /**
     * 
     */
    private List<String> m_subStr = new ArrayList<String>();
    
    /**
     * 
     * Constructors.
     * @param helper 
     * @param connInfo 
     */
    public PMIStatement(final WPSConnectionHelper helper, final WPSConnectionInfo connInfo) {
        super(helper, connInfo);
    }
    
    /**
     * 
     * the getter or subStrList.
     * @return m_subStr
     */
    public List<String> getSubStr() {
        return m_subStr;
    }

    /**
     * 
     * the setter of subStrList.
     * @param subStr 
     */
    public void setSubStr(final List<String> subStr) {
        this.m_subStr = subStr;
    }



    @SuppressWarnings("rawtypes")
	@Override
    public Object execute(final String operation, final Map<String, String> params, final String subname)
        throws PluginException {
        try {
            if (m_configs == null) {
                initPmiEnv();
            }
            
            WSStats t_pmiStats = collectStatsViaPerfMbean(operation, true);
            
            if (subname != null && subname.length() > 0) {
                HashMap<String, String> t_result = new HashMap<String, String>();
                if ("webapp".equals(subname)) {
            
                    StringBuilder t_moduleName = new StringBuilder("WebSphere:*,");
                    t_moduleName.append("type").append('=').append("WebModule").append(',');
                    
                    t_moduleName.append("cell=").append(m_cellName).append(',');
                    t_moduleName.append("node=").append(m_nodeName).append(',');
                    t_moduleName.append("process=").append(m_serverName);

                    ObjectName t_oModule = new ObjectName(t_moduleName.toString());
                    Set t_oNames = m_helper.getBaseClient().queryNames(t_oModule, null);
                    
                    m_subStr = getSubModelName(t_oNames, "J2EEName");
                    
                    t_result = getSubModelMetric(m_subStr, t_pmiStats, params.get(S_FILTER_VALUE));

                }
                if ("datasource".equals(subname)) {
                    StringBuilder t_moduleName = new StringBuilder("WebSphere:*,");
//                    t_moduleName.append("type").append('=').append("DataSource").append(',');
                    t_moduleName.append("type").append('=').append("JDBCProvider").append(',');
                    
                    t_moduleName.append("cell=").append(m_cellName).append(',');
                    t_moduleName.append("node=").append(m_nodeName).append(',');
                    t_moduleName.append("process=").append(m_serverName);

                    ObjectName t_oModule = new ObjectName(t_moduleName.toString());
                    Set t_oNames = m_helper.getBaseClient().queryNames(t_oModule, null);
//                    t_subStr = getSubModelName(t_oNames, "JDBCProvider");
                    m_subStr = getSubModelName(t_oNames, "name");
                    t_result = getSubModelMetric(m_subStr, t_pmiStats, params.get(S_FILTER_VALUE));
                    
                }
                if ("portlet".equals(subname)) {
                    StringBuilder t_moduleName = new StringBuilder("WebSphere:*,");
                    t_moduleName.append("j2eeType").append('=').append("WebModule").append(',');
                    t_moduleName.append("cell=").append(m_cellName).append(',');
                    t_moduleName.append("node=").append(m_nodeName).append(',');
                    t_moduleName.append("process=").append(m_serverName);
                    
                    ObjectName t_oModule = new ObjectName(t_moduleName.toString());
                    Set t_oNames = m_helper.getBaseClient().queryNames(t_oModule, null);
                    m_subStr = getSubModelName(t_oNames, "J2EEName");
                    t_result = getPortletMetric(m_subStr, t_pmiStats, params.get(S_FILTER_VALUE));
                }
                return t_result;
            } else {
                Map<String,Map<String, Object>> t_result = processStatistic(t_pmiStats);
                return t_result;
            }
            
        } catch (Throwable t_e) {
            throw new PluginException(t_e);
        }
    }


    /**
     * 
     * ��ȡ����Դָ����Ϣ
     * @param subStr  
     * @param pmiStats 
     * @param filterValue 
     * @return t_res
     */
    private HashMap<String, String> getSubModelMetric(final List<String> subStr, final WSStats pmiStats,
        final String filterValue) {

        HashMap<String, String> t_res = new HashMap<String, String>();

        for (int t_i = 0; t_i < subStr.size(); t_i++) {
        	String subName = subStr.get(t_i);
            WSStats[] t_pmiStatVal = pmiStats.getSubStats();
            WSStats t_psv = null;
            for(WSStats tmp : t_pmiStatVal) {
            	if(tmp.getName().equals(subName)) {
            		t_psv = tmp;
            		break;
            	}
            }
            if(t_psv == null) {
            	continue;
            }
            WSStatistic t_wss = t_psv.getStatistic(filterValue);

            String[] t_array = getStatistic(t_wss);

            t_res.put(subStr.get(t_i), t_array.length > 0 ? t_array[0] : "");

        }
        return t_res;

    }

    /**
     * 
     * ��ȡPortlet����Դָ����Ϣ
     * @param subStr 
     * @param pmiStats 
     * @param filterValue 
     * @return t_res
     */
    private HashMap<String, String> getPortletMetric(final List<String> subStr, final WSStats pmiStats,
        final String filterValue) {

        HashMap<String, String> t_res = new HashMap<String, String>();

        for (int t_i = 0; t_i < subStr.size(); t_i++) {
            WSStats[] t_pmiStatVal = pmiStats.getSubStats();
            WSStats t_psv = t_pmiStatVal[t_i];
            WSStats[] t_pmiSubVal = t_psv.getSubStats();
            WSStats t_psubv = t_pmiSubVal[0];

//            String[] t_p = t_psubv.getStatisticNames();

            WSStatistic t_wss = t_psubv.getStatistic(filterValue);

            String[] t_array = getStatistic(t_wss);

            t_res.put(subStr.get(t_i), t_array.length > 0 ? t_array[0] : "");

        }
//        System.out.println(t_res.toString());
        return t_res;

    }

    /**
     * 
     * {method description}.
     * @param oNames 
     * @param kName 
     * @return t_appStr
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private List<String> getSubModelName(final Set oNames, final String kName) {
    
        List<String> t_appStr = new ArrayList<String>();
        
        if (oNames == null || oNames.size() == 0) {
            return t_appStr;
        }
        
        List<Map> t_ret = new ArrayList<Map>();
        
        for (Object t_oName : oNames) {
            ObjectName t_each = (ObjectName) t_oName;
            Hashtable<String, String> t_map = t_each.getKeyPropertyList();

            t_ret.add(t_map);
        }
        
        for (Map t_row : t_ret) {

            String t_field = (String) t_row.get(kName);
            t_appStr.add(t_field);
        } 
        
        // listȥ���ظ�
        HashSet t_h = new HashSet(t_appStr);
        t_appStr.clear();
        t_appStr.addAll(t_h);

        return t_appStr;
    }

    /**
     * Init pmi, found the perfMBean from WebSphere MBean Server. Notes: If the
     * websphere mbean server is not eable the pmi, The perf Mbean will not be
     * found.
     * @throws ConnectorException 
     * @throws ReflectionException 
     * @throws MBeanException 
     * @throws InstanceNotFoundException 
     */
    private void initPmiEnv() 
        throws InstanceNotFoundException, MBeanException, ReflectionException, ConnectorException {

        m_perfBean = getMbean("Perf");
        if (m_perfBean != null) {
            m_configs = (PmiModuleConfig[]) m_helper.getBaseClient().invoke(m_perfBean, "getConfigs", null, null);
            
        }
    }
    
    /**
     * get stats via PerfMbean
     * @param moduleName 
     * @param recursive 
     * @return result
     * @throws InstanceNotFoundException 
     * @throws MBeanException 
     * @throws ReflectionException 
     * @throws ConnectorException 
     */
    public WSStats collectStatsViaPerfMbean(final String moduleName, final boolean recursive)
        throws InstanceNotFoundException, MBeanException, ReflectionException, ConnectorException {
        StatDescriptor[] t_sd = new StatDescriptor[] { new StatDescriptor(new String[] { moduleName }) };

        Object[] t_params = new Object[] { t_sd, recursive };
        String[] t_sigs = new String[] { "[Lcom.ibm.websphere.pmi.stat.StatDescriptor;", "java.lang.Boolean" };
        WSStats[] t_pmiStats = (WSStats[]) m_helper.getBaseClient().invoke(m_perfBean, "getStatsArray", t_params,
                t_sigs);

        return t_pmiStats[0];
    }
    
    /**
     * process statistic data. 
     * @param stats 
     * @return result
     */
    private Map<String,Map<String, Object>> processStatistic(final WSStats stats) {
        Map<String,Map<String, Object>> t_result = new HashMap<String,Map<String, Object>>();

        Map<String,Object> t_map = getStatisticMap(stats);
        // check if module is recordeable.
        String t_modelName = stats.getName();
        if (s_attributes.contains(t_modelName))
            t_result.put(t_modelName, t_map);

        return t_result;
    }
    
    /**
     * convert statistic result to map
     * @param stats 
     * @return result
     */
    public Map<String,Object> getStatisticMap(final WSStats stats) {
        
        if (stats == null) {
            return new HashMap<String,Object>();
        }
        
        Map<String,Object> t_result = new HashMap<String,Object>();

        String[] t_statisticsNames = stats.getStatisticNames();
        
        for (String t_name : t_statisticsNames) {
            if (t_name != null) {
                String[] t_values = getStatistic(stats.getStatistic(t_name));
                t_result.put(t_name, t_values);

            }
        }
        
        WSStats[] t_subs = stats.getSubStats();
        if (t_subs != null && t_subs.length > 0) {

            for (WSStats t_sub : t_subs) {
                Map<String,Object> t_subMap = getStatisticMap(t_sub);
                t_result.put(t_sub.getName(), t_subMap);
            }                
        }

        return t_result;
    }
    
    /**
     * convert statistic result to String 
     * @param statistic 
     * @return result
     */
    private static String[] getStatistic(final WSStatistic statistic) {

        List<String> t_ret = new ArrayList<String>();
        
        // check the data type and cast the data to the right type
        if (statistic instanceof CountStatisticImpl) {
            // cast it to CountStatistic and call the API to get the data
            // value
            CountStatisticImpl t_cs = (CountStatisticImpl) statistic;
            t_ret.add(String.valueOf(t_cs.getCount()));

        } else if (statistic instanceof TimeStatisticImpl) {
            // cast it to TimeStatistic and call the API to get the data
            // value
            TimeStatisticImpl t_ts = (TimeStatisticImpl) statistic;
            t_ret.add(String.valueOf(t_ts.getCount()));

        } else if (statistic instanceof BoundedRangeStatisticImpl) {
            // cast it to RangeStatistic and call the API to get the data
            // value
            BoundedRangeStatisticImpl t_bs = (BoundedRangeStatisticImpl) statistic;
            
            t_ret.add(String.valueOf(t_bs.getCurrent()));
//            t_ret.add(String.valueOf(t_bs.getLowWaterMark()));
//            t_ret.add(String.valueOf(t_bs.getHighWaterMark()));
            
        } else if (statistic instanceof RangeStatisticImpl) {
            // cast it to RangeStatistic and call the API to get the data
            // value
            RangeStatisticImpl t_rs = (RangeStatisticImpl) statistic;

            t_ret.add(String.valueOf(t_rs.getCurrent()));
//            t_ret.add(String.valueOf(t_rs.getLowWaterMark()));
//            t_ret.add(String.valueOf(t_rs.getHighWaterMark()));
        }
        
        return t_ret.toArray(new String[0]);
    }

}
