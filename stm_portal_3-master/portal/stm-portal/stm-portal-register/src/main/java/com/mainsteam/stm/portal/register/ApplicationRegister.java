package com.mainsteam.stm.portal.register;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.mainsteam.stm.util.ClassPathUtil;
import com.mainsteam.stm.util.XmlUtil;

/**
 * 注册到门户
 * 
 * @author wxh
 *
 */
public class ApplicationRegister {

	private static final Log logger = LogFactory.getLog(ApplicationRegister.class);
	
	private static String STM_APPLICATION_NAME = "";
	
	private static String MSIP = null; 
	private static String MSPort = null; 
	private static String stmIP = null;
	private static String stmPort = null;
	
	/**
	 * Main
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args ) throws Exception{
		
		if(args==null || args.length==0){
			info("INFO : 参数缺失，请检查脚本！");
			exit();
		}
		
		if(args[0].equals("register")){
			register(args);
		}else if(args[0].equals("cancellation")){
			cancellation();
		}

		
		exit();
	}
	
	
	/**
	 * 注册门户
	 * @param args
	 */
	public static void register(String[] args){

		String errorMsg = "Error : 注册参数错误，请输入正确的门户IP和端口进行注册！";
		if (args.length < 3 || args.length > 7 ) {
			error(errorMsg);
			exit();
		}
		
		switch(args.length) { 
			case 3: 
				MSIP = args[1];
				MSPort = args[2];
				loadInetAddress();
				break; 
			case 4: 
				MSIP = args[1];
				MSPort = args[2];
				STM_APPLICATION_NAME = args[3];
				loadInetAddress();
				break; 
			case 5:
				error(errorMsg);
				exit();
				break; 
			case 6: 
				stmIP = args[1];
				stmPort = args[2];
				MSIP = args[4];
				MSPort = args[5];
				break;
//			case 7: 
//				stmIP = args[1];
//				stmPort = args[2];
//				MSIP = args[4];
//				MSPort = args[5];
//				STM_APPLICATION_NAME = args[6];
//				break; 
		}
		
		String portalUrl = "http://" + MSIP + ":" + MSPort;
		String portalWsdl = portalUrl + "/schemas/RegistrationApplicationServices?wsdl";
			
		String stmUrl = "http://" + stmIP + ":" + stmPort + "/resource/itm.html";
		String stmWsdl = "http://" + stmIP + ":" + stmPort	+ "/soap/registerService?wsdl";

		String stmHtmlPath = ClassPathUtil.getTomcatHome() + "webapps" + File.separator + "ROOT" + File.separator + "resource" + File.separator + "stm-config.js";
		String msAddr = MSIP + ":" + MSPort;
		if(STM_APPLICATION_NAME != null && !STM_APPLICATION_NAME.equals("")){
			msAddr += "/" + STM_APPLICATION_NAME;
		}
		System.out.println("msAddr : " + msAddr);
		writeItm(stmHtmlPath, readItm(stmHtmlPath, msAddr));
		
		RegisterUtil regUtil=new RegisterUtil();
		
		RegisterBean registerBean =regUtil.loadeRegInfo();
		registerBean.setItmUrl(stmUrl);
		registerBean.setItmWsdl(stmWsdl);
		registerBean.setPortalUrl(portalUrl);
		registerBean.setPortalWsdl(portalWsdl);
		
		boolean isRegister=regUtil.register(registerBean);
		
		if(isRegister){
			info("INFO : ====【注册成功】====");
		}else{
			info("INFO : ====【注册失败】====");
		}
		
	}
	
    /** 
     * 读取文件内容 
     *  
     * @param filePath 
     * @return 
     */  
    private static String readItm(String filePath,String msAddr) {
        BufferedReader br = null;  
        String line = null;  
        StringBuffer buf = new StringBuffer();  
          
        try {  
            // 根据文件路径创建缓冲输入流  
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));  
              
            // 循环读取文件的每一行, 对需要修改的行进行修改, 放入缓冲对象中  
            while ((line = br.readLine()) != null) {  
            	if (line.contains("stmAddr")) {
                    buf.append("stmAddr = '" + msAddr + "';");
                }else {  
                    buf.append(line);  
                }  
                buf.append(System.getProperty("line.separator"));  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error(e.getMessage(),e);
        } finally {  
            // 关闭流  
            if (br != null) {  
                try {  
                    br.close();  
                } catch (IOException e) {  
                    br = null;  
                    logger.error(e.getMessage(),e);
                    e.printStackTrace();
                }  
            }  
        }  
          
        return buf.toString();  
    }
	
    /** 
     * 将内容回写到文件中 
     *  
     * @param filePath 
     * @param content 
     */ 
    private static void writeItm(String filePath, String content) {  
        Writer writer = null;
          
        try {  
            // 根据文件路径创建缓冲输出流  
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
            // 将内容写入文件中  
            writer.write(content);  
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error(e.getMessage(),e);
        } finally {  
            // 关闭流  
            if (writer != null) {  
                try {  
                	writer.close();  
                } catch (IOException e) {  
                	writer = null;  
                    logger.error(e.getMessage(),e);
                    e.printStackTrace();
                }  
            }  
        }  
    }  
	
	/**
	 * 注销门户
	 * @param args
	 */
	public static void cancellation(){

		RegisterUtil regUtil=new RegisterUtil();
		RegisterBean registerBean =regUtil.loadeRegInfo();
		boolean isCancellation=regUtil.cancellation(registerBean);
		if(isCancellation){
			info("INFO : ====【注销成功】====");
		}else{
			info("INFO : ====【注销失败】====");
		}
	}
	

	

	/**
	 * 获取 Tomcat 的端口号
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String getPortalPort() {
		//Tomcat server.xml patrh
		String serverXML = ClassPathUtil.getTomcatHome() + "conf"  + File.separator + "server.xml";

		try {
			File f = new File(serverXML);

			if (f.exists() && f.isFile()) {
				
				Document doc = XmlUtil.getDoc(serverXML);

				// 查询属性 protocol="HTTP/1.1"
				String xpath = "/Server/Service/Connector[@protocol=\"HTTP/1.1\"]";
				List<Element> list = doc.selectNodes(xpath);
				if (list != null && list.size() > 0) {
					return list.get(0).attributeValue("port");
				}
			} else {
				logger.error("Can not found server.xml:" + serverXML);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "80";
	}

	

	
	private static void loadInetAddress(){
		try {
			if(isWindowsOS()){
				stmIP =  InetAddress.getLocalHost().getHostAddress();
			}else{
				stmIP = getLinuxLocalIp();
			}

			stmPort = getPortalPort();
		} catch (Exception e) {
			error("Error : 获取当前服务器IP/Port失败！");
		}
	}
	
	/**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     * @throws SocketException
     */
    private static String getLinuxLocalIp() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                                System.out.println(ipaddress);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("获取ip地址异常");
            ip = "127.0.0.1";
            ex.printStackTrace();
        }
        System.out.println("IP:"+ip);
        return ip;
    }
 

    
    /**
    * 判断操作系统是否是Windows
    *
    * @return
    */
   public static boolean isWindowsOS() {
       boolean isWindowsOS = false;
       String osName = System.getProperty("os.name");
       if (osName.toLowerCase().indexOf("windows") > -1) {
           isWindowsOS = true;
       }
       return isWindowsOS;
   }
	private static void exit() {
		System.exit(0);
	}

	protected static void info(String msg) {
		System.out.println(msg);
		if (logger.isInfoEnabled()) {
			logger.info(msg);
		}
	}

	protected static void error(String msg) {
		System.err.println(msg);
		if (logger.isErrorEnabled()) {
			logger.error(msg);
		}
	}
}
