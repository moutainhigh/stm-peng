/**
 * 
 */
package com.mainsteam.stm.plugin.weblogic;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

/**
 * @author lij
 * 封装常用的weblogic的JMX方式的采集方法
 */
public class WeblogicJMXcollectUtil {
	private static final Log debug = LogFactory.getLog(WeblogicJMXcollectUtil.class);
	private static final String S_REGEX = "@#";
	private static final String S_TYPE = "Type";
    /**
     * <pre>
     * 通过给定的MBean(RuntimeServiceMBean->ServerRuntime下)名称
     * 获取weblogic主资源的指标值
     * 如果para是父子级的MBean，则返回最后代的MBean的值
     * eg1.
     * para为ThreadPoolRuntime@#ExecuteThreadIdleCount时
     * 首先获取到当前监控的ServerRuntime假设为
     * 返回ThreadPoolRuntime.getAttribute("ExecuteThreadIdleCount");
     * eg2.
     * para为Name时
     * 返回ServerRuntime.getAttribute("Name");
     * </pre>
     * @param weblogicClient WeblogicClient
     * @param para String	String	以@#符号分割的parentMBean下的多个mbean，最后一个为mbean的attribute，通过attribute可以获取到指标的值
     * @return String		指标值，如果异常返回null
     * @throws Exception 异常
     * @see #getMainResValueBySpecisMBeanServer(WeblogicClient, String, String)
     */
    public static String getMainResValue(JBrokerParameter obj, final String para){
    	return getMainResValueBySpecisMBeanServer(obj, WeblogicConstant.SERVER_RUNTIMES, para);
    }
    
    /**
     * <pre>
     * 通过给定的父级MBean值parentMBean(RuntimeServiceMBean->ServerRuntime下)
     * 获取父级MBean值下面多个子MBean的值
     * 例如parentMBean为JVMRuntime、childMBean为HeapFreeCurrent@#HeapSizeCurrent时
     * 返回{JVMRuntime.getAttribute(HeapFreeCurrent), JVMRuntime.getAttribute(HeapSizeCurrent)}
     * </pre>
     * @param weblogicClient WeblogicClient
     * @param parentMBean	para的父级MBean
     * @param childMBean	String	以@#符号分割的parentMBean下的多个MBean
     * @return String[]		多个MBean的值，遇到异常返回null
     * @throws Exception 异常
     * @see #getMainResValueListBySpecisMBeanServer(WeblogicClient, String, String)
     */
    public static String[] getMainResValueList(JBrokerParameter obj, final String parentMBean, final String childMBean){
    	return getMainResValueListBySpecisMBeanServer(obj, WeblogicConstant.SERVER_RUNTIMES, parentMBean, childMBean);
    }
    
    /**
     * <pre>
     * 在指定的mbeanServer下
     * 根据给定的父子级mbean和attribute，获取到最幼代mbean的attribute的值
     * 层级关系如：mbeanServer->para
     * eg1.
     * 当mbeanServer为ServerRuntimes，para为ThreadPoolRuntime@#ExecuteThreadIdleCount时
     * 首先获取DomainRuntimeServer.attribute("ServerRunTimes")下面的ServerRuntimes
     * 然后获取到监控的ServerRuntime的ObjectName，假设为serverRuntime
     * 然后获取serverRuntime.attribure("ThreadPoolRuntime")的ObjectName，假设为threadPoolRunTime
     * 最后返回threadPoolRunTime.attribute("ExecuteThreadIdleCount");
     * eg2.
     * 当mbeanServer为DomainConfiguration，para为Name
     * 首先获取DomainRunTimeServer.attribute("DomainConfiguration");假设为domainConfiguration
     * 然后返回domainConfiguration.attribute("Name");
     * </pre>
     * 
     * @param weblogicClient WeblogicClient
     * @param mbeanServer	MBeanServer,type下的MBServer，ServerRuntimes或者同级的其他MBServer
     * @param para	String	以@#符号分割的mbean，最后一个为mbean的attribute，通过attribute可以获取到指标的值
     * 						例如ThreadPoolRuntime@#ExecuteThreadIdleCount，表示获取ThreadPoolRuntimeMBean的ExecuteThreadIdleCount值
     * @return String[]		指标值
     * @throws Exception 异常
     */
    public static String getMainResValueBySpecisMBeanServer(JBrokerParameter obj, final String mbeanServer, final String para){
    	Object result = null;
        JMXConnector t_connect = obj.getWeblogicBo().getJmxConnector();
        MBeanServerConnection t_mBean;
        ObjectName t_RTService;
        ObjectName[] t_Services;
        ObjectName t_objName = null;
        
		try {
			if(t_connect != null){
				t_mBean = t_connect.getMBeanServerConnection();
				t_RTService = new ObjectName(WeblogicConstant.DOMAINRUNTIME_SERVICE_MBEAN);
				if(mbeanServer.equals(WeblogicConstant.SERVER_RUNTIMES)){
					t_Services = (ObjectName[]) t_mBean.getAttribute(t_RTService, mbeanServer);
					if(t_Services == null || t_Services.length < 1){
						debug.warn("get runtimeServices error. ip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
								 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename());
						return "";
					}
					if("".equals(obj.getWeblogicBo().getInstancename())){
						t_objName = t_Services[0];
					}else{
						for(int i = 0; i<t_Services.length; i++){
							String instanceName = (String) t_mBean.getAttribute(t_Services[i], "Name");
							if(instanceName != null && obj.getWeblogicBo().getInstancename().equals(instanceName.trim())){
								t_objName = t_Services[i];
								break;
							}
						}
					}
				}else{
					t_objName = (ObjectName) t_mBean.getAttribute(t_RTService, mbeanServer);
				}
				if(t_objName != null && para != null && !"".equals(para)){
					String[] t_paras = para.split(S_REGEX);
					for(int i = 0; i<t_paras.length; i++){
						if (t_objName == null) {
							continue;
						}
						if(i == t_paras.length-1){
							result = t_mBean.getAttribute(t_objName, t_paras[i]);
						}else{
							t_objName = (ObjectName) t_mBean.getAttribute(t_objName, t_paras[i]);
						}
					}
				}
			}
		} catch (Exception e){
			debug.warn(e.getMessage() + "\r\nip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
					 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename(), e);
		}
        return result == null?"":result.toString();
    }
    
    /**
     * <pre>
     * 通过给定的父级MBean值parentMBean
     * 在mbeanServer下获取父级MBean值下面多个attribute的值
     * 层级关系如：mbeanServer->pareandMBean->childMBeans
     * eg1.
     * mbeanServer=RuntimeServers,parentMBean=JVMRuntime,childMBean=HeapFreeCurrent@#HeapSizeCurrent
     * 首先获取DomainRuntimeServer.attribute("ServerRunTimes")下面的ServerRuntimes
     * 然后获取到监控的ServerRuntime的ObjectName，假设为serverRuntime
     * 然后获取serverRuntime.attribute("JVMRuntime"),假设为JVMRuntime
     * 最后返回{JVMRuntime.attribute("HeapFreeCurrent"), JVMRuntime.attribute("HeapSizeCurrent")}
     * </pre>
     * @param weblogicClient WeblogicClient
     * @param mbeanServer	MBeanServer,type下的MBServer，ServerRuntimes或者同级的其他MBServer
     * @param parentMBean	para的父级MBean
     * @param childMBean	String	以@#符号分割的parentMBean下的多个attribute
     * @return String[]		多个attribute的值，遇到异常返回null
     * @throws Exception 异常
     */
    public static String[] getMainResValueListBySpecisMBeanServer(JBrokerParameter obj, final String mbeanServer, final String parentMBean, final String childMBeans){
    	String[] result = null;
    	JMXConnector t_connect = obj.getWeblogicBo().getJmxConnector();
    	MBeanServerConnection t_mBean;
    	ObjectName t_RTService;
    	ObjectName t_objName = null;
    	ObjectName[] t_Services;
    	ObjectName t_parentName;
    	try {
    		if(t_connect != null){
    			t_mBean = t_connect.getMBeanServerConnection();
    			t_RTService = new ObjectName(WeblogicConstant.DOMAINRUNTIME_SERVICE_MBEAN);
    			if(mbeanServer.equals(WeblogicConstant.SERVER_RUNTIMES)){
					t_Services = (ObjectName[]) t_mBean.getAttribute(t_RTService, mbeanServer);
					if(t_Services == null || t_Services.length < 1){
						debug.warn("get runtimeServices error. ip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
								 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename());
						return null;
					}
					if("".equals(obj.getWeblogicBo().getInstancename())){
						t_objName = t_Services[0];
					}else{
						for(int i = 0; i<t_Services.length; i++){
							String instanceName = (String) t_mBean.getAttribute(t_Services[i], "Name");
							if(instanceName != null && obj.getWeblogicBo().getInstancename().equals(instanceName.trim())){
								t_objName = t_Services[i];
								break;
							}
						}
					}
				}else{
					t_objName = (ObjectName) t_mBean.getAttribute(t_RTService, mbeanServer);;
				}
    			t_parentName = (ObjectName) t_mBean.getAttribute(t_objName, parentMBean);
    			if(childMBeans != null && !"".equals(childMBeans)){
    				String[] t_paras = childMBeans.split(S_REGEX);
    				result = new String[t_paras.length];
    				for(int i = 0; i<t_paras.length; i++){
    					result[i] = String.valueOf(t_mBean.getAttribute(t_parentName, t_paras[i]));
    				}
    			}
    		}
    	} catch (Exception e) {
    		debug.warn(e.getMessage() + "\r\nip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
					 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename(), e);
    		result = null;
    	} 
    	return result;
    }
    
    /**
     * <pre>
     * 获取子资源的值
     * 参数para必须是二级的父子结构的mbean，其中父级mbean是子资源，子级mbean是要获取的值
     * 按照子资源名称=指标值\n子资源名称=指标值\n...子资源名称=指标值的格式返回
     * eg1.
     * para="ExecuteQueueRuntimes@#ExecuteThreadCurrentIdleCount"
     * 首先获取DomainRuntimeServer.attribute("ServerRunTimes")下面的ServerRuntimes
     * 然后获取到监控的ServerRuntime的ObjectName，假设为serverRuntime
     * 然后得到ObjectName[] executeQueueRuntimes=serverRuntime.attribute("ExecuteQueueRuntimes");
     * 然后遍历executeQueueRuntimes
     * 对于ExecuteQueueRuntime executeQueueRuntime:executeQueueRuntimes
     * 取executeQueueRuntime.attribute("Name")假设为nameN
     * 取executeQueueRuntime.attribute("ExecuteThreadCurrentIdleCount")假设为executeThreadCurrentIdleCountN
     * 最后返回name1=executeThreadCurrentIdleCount1\nname2=executeThreadCurrentIdleCount2\n.....nameN=executeThreadCurrentIdleCountN\n
     * </pre>
     * @param weblogicClient
     * @param para				以@#符号分割的mbean和attribute，mbean为子资源，attribute为指标字段
     * @return					以\n分割的各个子资源的  子资源名称=指标值  格式的指标值，
     * @see #getChildResValueBySpecisMBeanServer(WeblogicClient, String, String)
     */
    public static String getChildResValueBySpecisMBeanServer(JBrokerParameter obj, final String para){
    	return getChildResValueBySpecisMBeanServer(obj, WeblogicConstant.SERVER_RUNTIMES, para);
    }
    /**
     * @see #getChildResValueBySpecisMBeanServer(WeblogicClient, String)
     * @see #getChildResValueBySpecisMBeanServer(WeblogicClient, String, String, String[])
     * @param weblogicClient
     * @param para
     * @param types
     * @return
     */
    public static String getChildResValueBySpecisMBeanServer(JBrokerParameter obj, final String para, final String[] types){
    	return getChildResValueBySpecisMBeanServer(obj, WeblogicConstant.SERVER_RUNTIMES, para, types);
    }
    /**
     * <pre>
     * 获取子资源的值
     * 参数para必须是二级的父子结构的mbean，其中父级mbean是子资源，子级mbean是要获取的值
     * 按照子资源名称=指标值\n子资源名称=指标值\n...子资源名称=指标值的格式返回
     * eg1.
     * mbeanServer="RuntimeServers", para="ExecuteQueueRuntimes@#ExecuteThreadCurrentIdleCount"
     * 首先获取DomainRuntimeServer.attribute("ServerRunTimes")下面的ServerRuntimes
     * 然后获取到监控的ServerRuntime的ObjectName，假设为serverRuntime
     * 然后得到ObjectName[] executeQueueRuntimes=serverRuntime.attribute("ExecuteQueueRuntimes");
     * 然后遍历executeQueueRuntimes
     * 对于ExecuteQueueRuntime executeQueueRuntime:executeQueueRuntimes
     * 取executeQueueRuntime.attribute("Name")假设为nameN
     * 取executeQueueRuntime.attribute("ExecuteThreadCurrentIdleCount")假设为executeThreadCurrentIdleCountN
     * 最后返回name1=executeThreadCurrentIdleCount1\nname2=executeThreadCurrentIdleCount2\n.....nameN=executeThreadCurrentIdleCountN\n
     * </pre>
     * @param weblogicClient
     * @param mbeanServer		MBeanServer,type下的MBServer，ServerRuntimes或者同级的其他MBServer
     * @param para				以@#符号分割的mbean和attribute，mbean为子资源，attribute为指标字段
     * @return					以\n分割的各个子资源的  子资源名称=指标值 格式的指标值，
     */
    public static String getChildResValueBySpecisMBeanServer(JBrokerParameter obj, final String mbeanServer, final String para){
    	Object result = null;
    	JMXConnector t_connect = obj.getWeblogicBo().getJmxConnector();
    	MBeanServerConnection t_mBean;
    	ObjectName t_RTService;
    	ObjectName[] t_Services;
    	ObjectName t_objName = null;
    	try {
    		if(t_connect != null){
    			t_mBean = t_connect.getMBeanServerConnection();
    			t_RTService = new ObjectName(WeblogicConstant.DOMAINRUNTIME_SERVICE_MBEAN);
    			if(mbeanServer.equals(WeblogicConstant.SERVER_RUNTIMES)){
    				t_Services = (ObjectName[]) t_mBean.getAttribute(t_RTService, mbeanServer);
    				if(t_Services == null || t_Services.length < 1){
    					debug.warn("get runtimeServices error. ip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
								 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename());
    					return "";
    				}
    				if("".equals(obj.getWeblogicBo().getInstancename())){
    					t_objName = t_Services[0];
    				}else{
    					for(int i = 0; i<t_Services.length; i++){
    						String instanceName = (String) t_mBean.getAttribute(t_Services[i], "Name");
    						if(instanceName != null && obj.getWeblogicBo().getInstancename().equals(instanceName.trim())){
    							t_objName = t_Services[i];
    							break;
    						}
    					}
    				}
    			}else{
    				t_objName = (ObjectName) t_mBean.getAttribute(t_RTService, mbeanServer);;
    			}
    			if(t_objName != null && para != null && !"".equals(para)){
    				String[] t_paras = para.split(S_REGEX);
    				if(t_paras != null && t_paras.length == 2){
    					StringBuffer sb = new StringBuffer();
    					ObjectName[] t_childObjects = (ObjectName[]) t_mBean.getAttribute(t_objName, t_paras[0]);
    					if("Name".equals(t_paras[1].trim())){
    						for(int i = 0; i<t_childObjects.length; i++){
    							sb.append(String.valueOf(t_mBean.getAttribute(t_childObjects[i], "Name")));
    							sb.append("\n");
    						}
    					}else{
    						for(int i = 0; i<t_childObjects.length; i++){
    							sb.append(String.valueOf(t_mBean.getAttribute(t_childObjects[i], "Name")));
    							sb.append("=");
    							sb.append(String.valueOf(t_mBean.getAttribute(t_childObjects[i], t_paras[1])));
    							sb.append("\n");
    						}
    					}
    					result = sb.toString();
    				}
    			}
    		}
    	} catch (Exception e) {
    		debug.warn(e.getMessage() + "\r\nip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
					 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename(), e);
    	} 
    	return result == null?"":result.toString();
    }

    /**
     * <pre>
     * <code>getChildResValueBySpecisMBeanServer(WeblogicClient, String, String)</code>方法的重载
     * 只是增加了一个types参数
     * 这个参数用来标识para中的子类型，与para一一对应，不需要type的para传入空字符串
     * eg.
     * para为ApplicationRuntimes@#ComponentRuntimes@#Status时
     * 因为ApplicationRuntimeMBean.ComponentRuntimes包含了所有种类的ComponentRuntimes
     * 而我们需要的仅仅是WebAppComponentRuntime这种类型的ComponentRuntime，所以需要types来标识出需要的WebAppComponentRuntime
     * 因为types与para一一对应，对于ApplicationRuntimes和Status不需要type
     * 所以我们最后传入的types数组如下
     * {"", "WebAppComponentRuntime", ""}
     * </pre>
     * @see #getChildResValueBySpecisMBeanServer(WeblogicClient, String, String)
     * @param weblogicClient
     * @param mbeanServer		MBeanServer,type下的MBServer，ServerRuntimes或者同级的其他MBServer
     * @param para				以@#符号分割的mbean和attribute，mbean为子资源，attribute为指标字段
     * @return					以\n分割的各个子资源的  子资源名称=指标值 格式的指标值，
     */
    public static String getChildResValueBySpecisMBeanServer(JBrokerParameter obj, final String mbeanServer, final String para, final String[] types){
    	String result = "";
    	JMXConnector t_connect = obj.getWeblogicBo().getJmxConnector();
    	MBeanServerConnection t_mBean;
    	ObjectName t_RTService;
    	ObjectName[] t_Services;
    	ObjectName t_objName = null;
    	try {
    		if(t_connect != null){
    			t_mBean = t_connect.getMBeanServerConnection();
    			t_RTService = new ObjectName(WeblogicConstant.DOMAINRUNTIME_SERVICE_MBEAN);
    			if(mbeanServer.equals(WeblogicConstant.SERVER_RUNTIMES)){
    				t_Services = (ObjectName[]) t_mBean.getAttribute(t_RTService, mbeanServer);
    				if(t_Services == null || t_Services.length < 1){
    					debug.warn("get runtimeServices error. ip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
								 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename());
    					return "";
    				}
    				if("".equals(obj.getWeblogicBo().getInstancename())){
    					t_objName = t_Services[0];
    				}else{
    					for(int i = 0; i<t_Services.length; i++){
    						String instanceName = (String) t_mBean.getAttribute(t_Services[i], "Name");
    						if(instanceName != null && obj.getWeblogicBo().getInstancename().equals(instanceName.trim())){
    							t_objName = t_Services[i];
    							break;
    						}
    					}
    				}
    			}else{
    				t_objName = (ObjectName) t_mBean.getAttribute(t_RTService, mbeanServer);;
    			}
    			if(t_objName != null && para != null && !"".equals(para)){
    				String[] t_paras = para.split(S_REGEX);
    				if(t_paras.length > types.length){
    					debug.warn("length of paras is longer than types, will return \"\"; para:" + para + "  types:" + types.length);
    					return "";
    				}
    				List<ObjectName> onList = new ArrayList<ObjectName>();
    				Object t_childObjects = t_mBean.getAttribute(t_objName, t_paras[0]);
    				if(t_childObjects instanceof ObjectName){
    					if("".equals(types[0]) || null == types[0]){
    						onList.add((ObjectName)t_childObjects);
    					}else{
    						if(types[0].equals(t_mBean.getAttribute((ObjectName)t_childObjects, S_TYPE))){
    							onList.add((ObjectName)t_childObjects);
    						}
    					}
    				}else if(t_childObjects instanceof ObjectName[]){
    					ObjectName[] ons = (ObjectName[]) t_childObjects;
    					for(int i = 0; i<ons.length; i++){
    						if("".equals(types[0]) || null == types[0]){
    							onList.add(ons[i]);
    						}else{
    							if(types[0].equals(t_mBean.getAttribute(ons[i], S_TYPE))){
    								onList.add(ons[i]);
    							}
    						}
    					}
    				}
    				for(int i = 1; i<t_paras.length; i++){
    					if(i+1 == t_paras.length){
    						StringBuffer sb = new StringBuffer();
    						if("Name".equals(t_paras[i]) || "ServletName".equals(t_paras[i])){
    							for(int j = 0; j<onList.size(); j++){
    								sb.append((String) t_mBean.getAttribute(onList.get(j), "Name"));
    								sb.append("\n");
    							}
    						}else{
    							for(int j = 0; j<onList.size(); j++){
									sb.append((String) t_mBean.getAttribute(onList.get(j), "Name"));
									sb.append("=");
									sb.append(String.valueOf(t_mBean.getAttribute(onList.get(j), t_paras[i])));
									sb.append("\n");
    							}
    						}
    						result = sb.toString();
    					}else{
    						List<ObjectName> tempONList = new ArrayList<ObjectName>();
    						for(int j = 0; j<onList.size(); j++){
    							t_childObjects = t_mBean.getAttribute(onList.get(j), t_paras[i]);
    							if(t_childObjects instanceof ObjectName){
    								if("".equals(types[i])){
    									tempONList.add((ObjectName)t_childObjects);
    								}else{
    									if(types[i].equals(t_mBean.getAttribute((ObjectName)t_childObjects, S_TYPE))){
    										tempONList.add((ObjectName)t_childObjects);
    	    							}
    								}
    		    				}else if(t_childObjects instanceof ObjectName[]){
    		    					ObjectName[] ons = (ObjectName[]) t_childObjects;
    		    					for(int k = 0; k<ons.length; k++){
    		    						if("".equals(types[i])){
    		    							tempONList.add(ons[k]);
    		    						}else{
    		    							if(types[i].equals(t_mBean.getAttribute(ons[k], S_TYPE))){
        										tempONList.add(ons[k]);
        	    							}
    		    						}
    		    					}
    		    				}
    						}
    						onList = tempONList;
    					}
    				}
    			}
    		}
    	} catch (Exception e) {
    		debug.warn(e.getMessage() + "\r\nip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
					 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename(), e);
    	} 
    	return result;
    }
    /**
     * <pre>
     * 相当于是#getChildResValueBySpecisMBeanServer(WeblogicClient, String, String, String[])方法的重载
     * 因为webapp他们的name超长，无法入库，所以单独写出来，对name进行处理
     * </pre>
     * @see #getChildResValueBySpecisMBeanServer(WeblogicClient, String, String, String[])
     * @param weblogicClient
     * @param mbeanServer		MBeanServer,type下的MBServer，ServerRuntimes或者同级的其他MBServer
     * @param para				以@#符号分割的mbean和attribute，mbean为子资源，attribute为指标字段
     * @return					以\n分割的各个子资源的  子资源名称=指标值 格式的指标值，
     */
    public static String getWebAppValues(JBrokerParameter obj, final String para, final String[] types){
    	Object result = null;
    	JMXConnector t_connect = obj.getWeblogicBo().getJmxConnector();
    	MBeanServerConnection t_mBean;
    	ObjectName t_RTService;
    	ObjectName[] t_Services;
    	ObjectName t_objName = null;
    	try {
    		if(t_connect != null){
    			t_mBean = t_connect.getMBeanServerConnection();
    			t_RTService = new ObjectName(WeblogicConstant.DOMAINRUNTIME_SERVICE_MBEAN);
				t_Services = (ObjectName[]) t_mBean.getAttribute(t_RTService, WeblogicConstant.SERVER_RUNTIMES);
				if(t_Services == null || t_Services.length < 1){
					debug.warn("get runtimeServices error. ip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
							 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename());
					return "";
				}
				if("".equals(obj.getWeblogicBo().getInstancename())){
					t_objName = t_Services[0];
				}else{
					for(int i = 0; i<t_Services.length; i++){
						String instanceName = (String) t_mBean.getAttribute(t_Services[i], "Name");
						if(instanceName != null && obj.getWeblogicBo().getInstancename().equals(instanceName.trim())){
							t_objName = t_Services[i];
							break;
						}
					}
				}
    			if(t_objName != null && para != null && !"".equals(para)){
    				String[] t_paras = para.split(S_REGEX);
    				if(t_paras.length > types.length){
    					debug.warn("length of paras is longer than types, will return \"\"; para:" + para + "  types:" + types.length);
    					return "";
    				}
    				List<ObjectName> onList = new ArrayList<ObjectName>();
    				Object t_childObjects = t_mBean.getAttribute(t_objName, t_paras[0]);
    				if(t_childObjects instanceof ObjectName){
    					if("".equals(types[0]) || null == types[0]){
    						onList.add((ObjectName)t_childObjects);
    					}else{
    						if(types[0].equals(t_mBean.getAttribute((ObjectName)t_childObjects, S_TYPE))){
    							onList.add((ObjectName)t_childObjects);
    						}
    					}
    				}else if(t_childObjects instanceof ObjectName[]){
    					ObjectName[] ons = (ObjectName[]) t_childObjects;
    					for(int i = 0; i<ons.length; i++){
    						if("".equals(types[0]) || null == types[0]){
    							onList.add(ons[i]);
    						}else{
    							if(types[0].equals(t_mBean.getAttribute(ons[i], S_TYPE))){
    								onList.add(ons[i]);
    							}
    						}
    					}
    				}
    				for(int i = 1; i<t_paras.length; i++){
    					if(i+1 == t_paras.length){
    						StringBuffer sb = new StringBuffer();
							for(int j = 0; j<onList.size(); j++){
								if(types.length == 4 && "WebAppComponentRuntime".equals(types[1])){
									//这里是对servlets做一个特殊处理，因为servlets的key需要用name和上下文路径来一起确定
									StringBuffer servletsid = new StringBuffer();
									servletsid.append((String) t_mBean.getAttribute(onList.get(j), "Name"));
									servletsid.append((String) t_mBean.getAttribute(onList.get(j), "ContextPath"));
									sb.append(geneWebAppKpiid(servletsid.toString()));
								}else{
									sb.append(geneWebAppKpiid((String) t_mBean.getAttribute(onList.get(j), "Name")));
								}
								sb.append("=");
								t_childObjects = t_mBean.getAttribute(onList.get(j), t_paras[i]);
								if(t_childObjects instanceof ObjectName[]){
									ObjectName[] ons = (ObjectName[]) t_childObjects;
									sb.append(ons.length);
									sb.append("\n");
								}else{
									if(t_childObjects != null){
										sb.append(String.valueOf(t_childObjects));
									}
									sb.append("\n");
								}
							}
    						result = sb.toString();
    					}else{
    						List<ObjectName> tempONList = new ArrayList<ObjectName>();
    						for(int j = 0; j<onList.size(); j++){
    							t_childObjects = t_mBean.getAttribute(onList.get(j), t_paras[i]);
    							if(t_childObjects instanceof ObjectName){
    								if("".equals(types[i])){
    									tempONList.add((ObjectName)t_childObjects);
    								}else{
    									if(types[i].equals(t_mBean.getAttribute((ObjectName)t_childObjects, S_TYPE))){
    										tempONList.add((ObjectName)t_childObjects);
    									}
    								}
    							}else if(t_childObjects instanceof ObjectName[]){
    								ObjectName[] ons = (ObjectName[]) t_childObjects;
    								for(int k = 0; k<ons.length; k++){
    									if("".equals(types[i])){
    										tempONList.add(ons[k]);
    									}else{
    										if(types[i].equals(t_mBean.getAttribute(ons[k], S_TYPE))){
    											tempONList.add(ons[k]);
    										}
    									}
    								}
    							}
    						}
    						onList = tempONList;
    					}
    				}
    			}
    		}
    	} catch (Exception e) {
    		debug.warn(e.getMessage() + "\r\nip/port:" + obj.getIp() + "/" + obj.getPort() + ",user is "
					 + obj.getUsername() + ",instance is " + obj.getWeblogicBo().getInstancename(), e);
    	} 
    	return result==null?"":result.toString();
    }
    
    /**
     * 生成webApp的kpiid
     * 生成规则如下
     * 如果name为null，返回空字符串
     * 如果name的长度小于等于39直接返回
     * 如果name的长度小于等于79则每隔一位取一个字符组成的字符串返回
     * 如果name的长度小于等于119则每隔两位取一个字组成的字符串返回
     * 如果name的长度大于119则取末尾39个字符组成的字符串返回
     * @param name
     * @return
     */
    public static String geneWebAppKpiid(String name){
    	String result = "";
    	if(name == null){
    		return "";
    	}
    	if(name.length() <= 50){
    		return name;
    	}else if(name.length() <= 150){
    		StringBuffer sb = new StringBuffer();
    		for(int i = 0; i< name.length(); i++){
    			if(i%2 == 0){
    				sb.append(name.charAt(i));
    			}
    		}
    		result = sb.toString();
    	}else if(name.length() <= 300){
    		StringBuffer sb = new StringBuffer();
    		for(int i = 0; i< name.length(); i++){
    			if(i%3 == 0){
    				sb.append(name.charAt(i));
    			}
    		}
    		result = sb.toString();
    	}else{
    		result = name.substring(name.length()-39, name.length());
    	}
    	return result;
    }
}
