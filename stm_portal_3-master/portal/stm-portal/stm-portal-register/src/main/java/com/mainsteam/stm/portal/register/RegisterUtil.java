package com.mainsteam.stm.portal.register;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.util.ClassPathUtil;
import com.mainsteam.stm.util.XmlUtil;

public class RegisterUtil {
	
	//门户注册方法
	private static final String REG_METHOD = "registrationApplication";
	
	//门户注销方法
	private static final String UN_REG_METHOD ="cancellationRegistration";
	
	private static final String FLAG_FILE = ClassPathUtil.getTomcatHome() +  "ITM_REGISER.CODE";
	
	String respCode = "0";
	
	/**
	 * 注册
	 * @param portalUrl	 * @param portalWsdl
	 * @param itmUrl
	 * @param itmWsdl
	 * @param codeFlag
	 * @return
	 */
	public boolean register(RegisterBean registerBean){
		
		
		String portalUrl = registerBean.getPortalUrl();
		String portalWsdl = registerBean.getPortalWsdl();
		String itmUrl  = registerBean.getItmUrl();
		String itmWsdl = registerBean.getItmWsdl();
		String codeFlag = registerBean.getCodeFlag();
		
		
		String reqXML = reqXML(registerBean.getItmUrl(),itmWsdl,codeFlag);
		
		
		String newCodeFlag = registerToPortal(portalWsdl ,reqXML);
		if(newCodeFlag==null || "".equals(newCodeFlag)){
			return false;
		}
		
		try {
			appendFilter(portalUrl);
		} catch (IOException e) {
			return false;
		}
		
		
		
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("portalUrl", portalUrl);
		jsonObj.put("portalWsdl", portalWsdl);
		jsonObj.put("itmUrl", itmUrl);
		jsonObj.put("itmWsdl", itmWsdl);
		jsonObj.put("codeFlag", newCodeFlag);

		try {
			saveRegInfo(jsonObj.toJSONString());
		} catch (Exception e) {
			System.out.println("Warning : 存储身份标识失败，请检查当前安装用户是否具有权限！");
			return false;
		}
		

		
		return true;
		
	}
	
	
	/**
	 * 注册到门户
	 * @param portalWsdl
	 * @param reqXML
	 * @return  
	 */
	public String registerToPortal(String portalWsdl ,String reqXML){
		System.out.println("INFO : 新增/更新ITManager门户菜单  ... ");
		String codeFlag = null;
		
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		
		Client client = null;
		try{
			client = dcf.createClient(portalWsdl);
		}catch (Exception e) {
			System.out.println("Error : 连接门户失败，请检查输入门户IP和端口 ！ ");
			System.exit(0);
		}

		Object[] objects = null;
		try {
			objects = client.invoke(REG_METHOD, reqXML);
		} catch (Exception e) {
			System.out.println("Error : 连接门户失败，请检查输入门户IP和端口 ！  ");
			System.exit(0);
		}
		
		if (objects != null && objects.length > 0) {
			JSONArray respJsonArr = JSONArray.parseArray(objects[0].toString());
			if (respJsonArr != null && respJsonArr.size() > 0) {
				respCode = respJsonArr.getJSONObject(0).get("code").toString();
				if ("200".equals(respCode)){
					codeFlag = respJsonArr.getJSONObject(0).get("codeFlag").toString();
					System.out.println("INFO : 新增/更新ITManager门户菜单 【成功】 ");
				}else{
					System.out.println("Error : 新增/更新ITManager门户菜单 【失败】 ");
				}
				
			}
		}
		
		return codeFlag;
		
	}
	
	/**
	 * 
	 * @param codeFlag
	 * @return
	 */
	public boolean cancellation(RegisterBean registerBean){
		
		String portalWsdl=registerBean.getPortalWsdl();
		
		JSONObject jb=new JSONObject();
		jb.put("code", registerBean.getCodeFlag());
		
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		
		Client client = null;
		try{
			client = dcf.createClient(portalWsdl);
		}catch (Exception e) {
		}
		
		Object[] objects = null;
		try {
			objects = client.invoke(UN_REG_METHOD, jb.toJSONString());
		} catch (Exception e) {
		}
		
		if (objects != null && objects.length > 0) {
			JSONArray respJsonArr = JSONArray.parseArray(objects[0].toString());
			if (respJsonArr != null && respJsonArr.size() > 0) {
				
				respCode = respJsonArr.getJSONObject(0).get("resultCode").toString();
				if ("200".equals(respCode)){
					File file = new File(FLAG_FILE);
					file.delete();
					System.out.println("INFO : 移除注册文件！");
					
					try {
						removeFilter();
					} catch (IOException e) {
						return false;
					}
					
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	
	private String reqXML(String itmUrl,String itmWsdl,String codeFlag) {

		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append("\"interfaceInfo\": [");
		sb.append("{");
		sb.append("\"methodLabel\": \"addUserMehtod\",");
		sb.append("\"methodName\": \"addUser\",");
		sb.append("\"url\": \"" + itmWsdl + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"methodLabel\": \"deleteUserMehtod\",");
		sb.append("\"methodName\": \"deleteUser\",");
		sb.append("\"url\": \"" + itmWsdl + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"methodLabel\": \"changeSkinMethod\",");
		sb.append("\"methodName\": \"changeSkin\",");
		sb.append("\"url\": \"" + itmWsdl + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"methodLabel\": \"cancellationRegistrationMethod\",");
		sb.append("\"methodName\": \"cancellationRegistration\",");
		sb.append("\"url\": \"" + itmWsdl + "\"");
		sb.append("}");
		sb.append("],");
		sb.append("\"applicationInfo\": {");
		sb.append("\"internalSystem\": \"1\",");
		sb.append("\"code\": \"" + codeFlag + "\",");
		sb.append("\"openMode\": \"ajax\",");
		sb.append("\"describe\": \"ITM Portal Home Page!\",");
		sb.append("\"applicationName\": \"ITManager\",");
		sb.append("\"mainUrl\": \"" + itmUrl + "\"");
		sb.append("}");
		sb.append("}");

		return sb.toString();

	}
	
	/*
	 * 单点登录配置文件更新,添加过滤器
	 */
	private void appendFilter(String portalUrl) throws IOException{
		System.out.println("INFO : 添加/更新  Filter ... ");
		
		String filePath=ClassPathUtil.getTomcatHome() + "webapps" + File.separator + "ROOT" + File.separator + "WEB-INF"+ File.separator + "web.xml";
		
		System.out.println("INFO : WEB.XML="+filePath);
		
		File webXML=new File(filePath);
		if(webXML.exists() && webXML.isFile()){
			Document doc = XmlUtil.getDoc(filePath);
			Element root = doc.getRootElement();
			
		    Node rootNode=doc.selectSingleNode("/web-app");  
		    Map<String,String> nsMap=new HashMap<String,String>();  
		    nsMap.put("xmlns","http://java.sun.com/xml/ns/javaee");  

		    XPath xpath=doc.createXPath("xmlns:filter/xmlns:init-param[xmlns:param-name=\"serverUrl\"]/xmlns:param-value");  
		    xpath.setNamespaceURIs(nsMap);  
		    
		    Node node = xpath.selectSingleNode(rootNode);
		    
		    //验证web.xml 是否已经添加过注册的过滤器
		    if(node!=null){
		    	System.out.println("<param-value>"+portalUrl +"</param-value>");
			    node.setText(portalUrl);
			    
			    
		    }else{
		    	System.out.println("INFO : 添加Filter");
				
		    	//listener
				Element listener = DocumentHelper.createElement(QName.get("listener", root.getNamespace ())); 
				Element listenerClass=listener.addElement("listener-class");
				listenerClass.setText("com.jghong.sso.token.client.web.SSOSessionListener");
				
				//filter
				Element filter = DocumentHelper.createElement(QName.get("filter", root.getNamespace ())); 
				filter.addElement("filter-name").setText("ssoClientFilter");
				filter.addElement("filter-class").setText("com.jghong.sso.token.client.web.filter.SSOClientFilter");;
				Element initParam =  filter.addElement("init-param");
				initParam.addElement("param-name").setText("serverUrl");
				initParam.addElement("param-value").setText(portalUrl);
				
				//filter-mapping
				Element filterMapping = DocumentHelper.createElement(QName.get("filter-mapping", root.getNamespace ())); 
				filterMapping.addElement("filter-name").setText("ssoClientFilter");
				filterMapping.addElement("url-pattern").setText("/resource/itm.html");
				filterMapping.addElement("url-pattern").setText("/resource/index.html");
				filterMapping.addElement("url-pattern").setText("/resource/login.html");
				root.elements().add(4, listener);
				root.elements().add(5, filter);
				root.elements().add(6, filterMapping);
				
		    }
		    
		    FileOutputStream fo = null;
			XMLWriter  writer = null;
			
			try{
				OutputFormat format = OutputFormat.createPrettyPrint();  
				format.setEncoding("utf-8"); 
				
				fo=new FileOutputStream(filePath);
				writer = new XMLWriter(fo,format);  
				writer.write(doc); 
				
	            fo.flush();
	            writer.flush();
	            
			}catch (Exception e){
				System.out.println("Error : 添加/更新  Filter 【失败】 ");
			}finally{
	            if(fo != null){
	    			fo.close();
	            }
	            if(writer != null){
		            writer.close(); 
	            }
			}


			System.out.println("INFO : 添加/更新  Filter 【成功】 ");
			
		}else{
			System.out.println("Error : 未找到"+filePath+" ,【异常】 ");
		}
		
	}
	
	
	private void removeFilter() throws IOException {
		System.out.println("INFO : 移除  Filter ... ");
		
		String filePath=ClassPathUtil.getTomcatHome() + "webapps" + File.separator + "ROOT" + File.separator + "WEB-INF"+ File.separator + "web.xml";
		
		System.out.println("INFO : WEB.XML="+filePath);
		
		File webXML=new File(filePath);
		if(webXML.exists() && webXML.isFile()){
			Document doc = XmlUtil.getDoc(filePath);
			Element root = doc.getRootElement();
			
		    Node rootNode=doc.selectSingleNode("/web-app");  
		    Map<String,String> nsMap=new HashMap<String,String>();  
		    nsMap.put("xmlns","http://java.sun.com/xml/ns/javaee");  

		    XPath xpath=doc.createXPath("xmlns:filter/xmlns:init-param[xmlns:param-name=\"serverUrl\"]");  
		    xpath.setNamespaceURIs(nsMap);  
		    Node node = xpath.selectSingleNode(rootNode).getParent();
		    root.remove(node);
		    

		    xpath=doc.createXPath("xmlns:listener[xmlns:listener-class=\"com.jghong.sso.token.client.web.SSOSessionListener\"]");
		    xpath.setNamespaceURIs(nsMap);  
		    node = xpath.selectSingleNode(rootNode);
		    root.remove(node);
		    

		    xpath=doc.createXPath("xmlns:filter-mapping[xmlns:filter-name=\"ssoClientFilter\"]");
		    xpath.setNamespaceURIs(nsMap);  
		    node = xpath.selectSingleNode(rootNode);
		    root.remove(node);
		    
		    
		    FileOutputStream fo = null;
			XMLWriter  writer = null;
			
			try{
				OutputFormat format = OutputFormat.createPrettyPrint();  
				format.setEncoding("utf-8"); 
				
				fo=new FileOutputStream(filePath);
				writer = new XMLWriter(fo,format);  
				writer.write(doc); 
				
	            fo.flush();
	            writer.flush();
	            
			}catch (Exception e){
				System.out.println("Error : 移除  Filter 【失败】 ");
			}finally{
	            if(fo != null){
	    			fo.close();
	            }
	            if(writer != null){
		            writer.close(); 
	            }
			}

			System.out.println("INFO : 移除  Filter 【成功】 ");
			
		}else{
			System.out.println("Error : 未找到"+filePath+" ,【异常】 ");
		}
		
	}
	
	// 存储当前注册标识
	private void saveRegInfo(String regInfo) throws Exception{
		File file = new File(FLAG_FILE);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter writer = new FileWriter(file, false);
		writer.write(regInfo);
		writer.flush();
		writer.close();
	}
	
	// 读取当前注册标识
	public RegisterBean loadeRegInfo() {
		File file = new File(FLAG_FILE);
		if (file.exists() && file.isFile()) {
			StringBuilder result = new StringBuilder();
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String s = null;
				while ((s = br.readLine()) != null) {
					result.append(s);
				}
				br.close();
			} catch (Exception e) {
				System.out.println("读取注册码失败！");
			}
			
			return JSONObject.parseObject(result.toString(), RegisterBean.class);
			
		}
		return new RegisterBean();
	}
	
	
	
	
//	/**
//	 * 更新远程门户的js文件地址
//	 */
//	private static void updateRemoteJS(){
//		info("-------------------------");
//		info("INFO : 3.注册Remote JS 文件 ！");
//		String filePath=ClassPathUtil.getTomcatHome() + "webapps" + File.separator + "ROOT" + File.separator + "resource"+ File.separator + "index.html";
//		File indexFile = new File(filePath);
//		if(indexFile.exists() && indexFile.isFile()){
//			//TO DO
//			info("INFO : 注册Remote JS 文件 【成功】 ！");
//		}else{
//			error("Error : 未找到"+filePath+" ,注册异常！");
//		}
//	}



	public static void main(String[] args){
		Document doc = XmlUtil.getDoc("D:\\Apache\\apache-tomcat-7.0.40\\webapps\\ROOT\\WEB-INF\\web.xml");
		Element root = doc.getRootElement();
		
	    Node rootNode=doc.selectSingleNode("/web-app");  
	    Map<String,String> nsMap=new HashMap<String,String>();  
	    nsMap.put("xmlns","http://java.sun.com/xml/ns/javaee");  

	    XPath xpath=doc.createXPath("xmlns:filter/xmlns:init-param[xmlns:param-name=\"serverUrl\"]");  
	    xpath.setNamespaceURIs(nsMap);  
	    Node node = xpath.selectSingleNode(rootNode).getParent();
	    root.remove(node);
	    

	    xpath=doc.createXPath("xmlns:listener[xmlns:listener-class=\"com.jghong.sso.token.client.web.SSOSessionListener\"]");
	    xpath.setNamespaceURIs(nsMap);  
	    node = xpath.selectSingleNode(rootNode);
	    root.remove(node);
	    

	    xpath=doc.createXPath("xmlns:filter-mapping[xmlns:filter-name=\"ssoClientFilter\"]");
	    xpath.setNamespaceURIs(nsMap);  
	    node = xpath.selectSingleNode(rootNode);
	    root.remove(node);
	    

	}
	
	
	
}
