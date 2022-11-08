package com.mainsteam.stm.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.mainsteam.stm.capbase.TextUtil;
import com.mainsteam.stm.caplib.dict.CapacityConst;


/**
 * 生成一份网络设备模型
 * 该模型唯一的不同之处在于它的接口子资源以操作状态为可用性状态
 * 而不是管理状态
 * @author Xiaopf
 *
 */
public class CreateNetwork {
	
	private static final String COLLECT_XML = "\\S*?collect\\S*?.xml";
	
	public static void main(String[] args) {
		
		CreateNetwork creation = new CreateNetwork();
		if(args == null || args.length !=3)
			System.out.println("Enter source path and dist path.");
		else{
			
			try{
				creation.createOtherNetwork(args[0], args[1], args[2]);
			}catch(Exception e){
				e.printStackTrace();
			}
				
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void createOtherNetwork(String sourcePath, String adminPath, String operPath) throws Exception {
		
		try {
			File adminFile = new File(adminPath);
			File operFile = new File(operPath);
			if(!adminFile.exists()){
				adminFile.mkdirs();
			} else {
				FileUtils.cleanDirectory(adminFile);
			}
			FileUtils.copyDirectoryToDirectory(new File(sourcePath), adminFile);
			if(!operFile.exists()){
				operFile.mkdirs();
			} else {
				FileUtils.cleanDirectory(operFile);
			}
			FileUtils.copyDirectoryToDirectory(new File(sourcePath), operFile);
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}

		IOFileFilter fileFilterCollect = new RegexFileFilter(COLLECT_XML);
		IOFileFilter all = FileFilterUtils.trueFileFilter();
		Collection<File> collectionFiles = FileUtils.listFiles(new File(adminPath), fileFilterCollect, all);
		
		System.out.println("Processing ......");
		for (File file : collectionFiles) {
			
			try {
				System.out.println(file.getPath());
				SAXReader reader = new SAXReader();
				InputStream ifile = new FileInputStream(file);
				InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
				reader.setEncoding("UTF-8");
				Document document = reader.read(ir);//读取XML文件
				Element root = document.getRootElement();//得到根节点
				
				//判断是否加密
				boolean isEncrypted = false;
				Node encryptionNode = root.selectSingleNode("//GlobalMetricSetting");
				String encryptionStr = ((Element)encryptionNode).attributeValue("isEncrypt");
				if(encryptionNode != null &&  encryptionStr!= null) {
					isEncrypted = Boolean.valueOf(encryptionStr);
				}
				
				String ifOperStatusStr = "ifAdminStatus";
				String arrayType = "ArrayType";
				String keyStr = "";
				if(isEncrypted) {
					ifOperStatusStr = TextUtil.encrypt(ifOperStatusStr, CapacityConst.ENDECODER_KEY);
					arrayType = TextUtil.encrypt(arrayType, CapacityConst.ENDECODER_KEY);
					keyStr = TextUtil.encrypt(keyStr, CapacityConst.ENDECODER_KEY);
				}
				
				Node parameter = root.selectSingleNode("//MetricPlugin[@metricid='"+ifOperStatusStr+"']/PluginParameter[@type='"+arrayType+"']/Parameter[@key='"+keyStr+"']");
				String value = "1.3.6.1.2.1.2.2.1.7";
				if(parameter != null) {
					Element parameterE = (Element)parameter;
					value = parameterE.attributeValue("value");
				}else {
					if(isEncrypted)
						value = TextUtil.encrypt(value, CapacityConst.ENDECODER_KEY);
				}
				
				String ifAvailabilityStr = "ifAvailability";
				if(isEncrypted){
					ifAvailabilityStr = TextUtil.encrypt(ifAvailabilityStr, CapacityConst.ENDECODER_KEY);
				}
				
				Node metric = root.selectSingleNode("//MetricPlugin[@metricid='"+ifAvailabilityStr+"']/PluginParameter[@type='"+arrayType+"']/Parameter[@key='"+keyStr+"']");
				
				if(metric !=null) {
					Element metricE = (Element)metric;
					metricE.setAttributeValue("value", value);
						
				}else{
					System.out.println("Cant't find metric[ifAvailability].");
				}
				
				OutputFormat  format   =   OutputFormat.createPrettyPrint(); 
				
				format.setEncoding( "UTF-8"); 
				
				XMLWriter writer1 = new XMLWriter(new FileOutputStream(file),format); 
				
				writer1.write(document); 
				
				writer1.close(); 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		collectionFiles = FileUtils.listFiles(new File(operPath), fileFilterCollect, all);
		
		System.out.println("Processing ......");
		for (File file : collectionFiles) {
			
			try {
				System.out.println(file.getPath());
				SAXReader reader = new SAXReader();
				InputStream ifile = new FileInputStream(file);
				InputStreamReader ir = new InputStreamReader(ifile, "UTF-8");
				reader.setEncoding("UTF-8");
				Document document = reader.read(ir);//读取XML文件
				Element root = document.getRootElement();//得到根节点
				
				//判断是否加密
				boolean isEncrypted = false;
				Node encryptionNode = root.selectSingleNode("//GlobalMetricSetting");
				String encryptionStr = ((Element)encryptionNode).attributeValue("isEncrypt");
				if(encryptionNode != null &&  encryptionStr!= null) {
					isEncrypted = Boolean.valueOf(encryptionStr);
				}
				
				String ifOperStatusStr = "ifOperStatus";
				String arrayType = "ArrayType";
				String keyStr = "";
				if(isEncrypted) {
					ifOperStatusStr = TextUtil.encrypt(ifOperStatusStr, CapacityConst.ENDECODER_KEY);
					arrayType = TextUtil.encrypt(arrayType, CapacityConst.ENDECODER_KEY);
					keyStr = TextUtil.encrypt(keyStr, CapacityConst.ENDECODER_KEY);
				}
				
				Node parameter = root.selectSingleNode("//MetricPlugin[@metricid='"+ifOperStatusStr+"']/PluginParameter[@type='"+arrayType+"']/Parameter[@key='"+keyStr+"']");
				String value = "1.3.6.1.2.1.2.2.1.8";
				if(parameter != null) {
					Element parameterE = (Element)parameter;
					value = parameterE.attributeValue("value");
				}else {
					if(isEncrypted)
						value = TextUtil.encrypt(value, CapacityConst.ENDECODER_KEY);
				}
				
				String ifAvailabilityStr = "ifAvailability";
				if(isEncrypted){
					ifAvailabilityStr = TextUtil.encrypt(ifAvailabilityStr, CapacityConst.ENDECODER_KEY);
				}
				
				Node metric = root.selectSingleNode("//MetricPlugin[@metricid='"+ifAvailabilityStr+"']/PluginParameter[@type='"+arrayType+"']/Parameter[@key='"+keyStr+"']");
				
				if(metric !=null) {
					Element metricE = (Element)metric;
					metricE.setAttributeValue("value", value);
						
				}else{
					System.out.println("Cant't find metric[ifAvailability].");
				}
				
				OutputFormat  format   =   OutputFormat.createPrettyPrint(); 
				
				format.setEncoding( "UTF-8"); 
				
				XMLWriter writer1 = new XMLWriter(new FileOutputStream(file),format); 
				
				writer1.write(document); 
				
				writer1.close(); 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
