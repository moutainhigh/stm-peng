package com.mainsteam.stm.capvalidate;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.util.XMLErrorHandler;

import com.mainsteam.stm.capvalidate.dict.XsdEnum;


public class CapacitySchemaValidate {
	
	public static void main(String[] args) {
		String xmlFile = "E:/oc/Capacity/modules/oc-capvalidate/src/test/java";
		xmlFile += "/com.mainsteam.stm/capvalidate/Category.xml";
		new CapacitySchemaValidate().validateXML(xmlFile, XsdEnum.Category);
	}
	
	public List<String> validate(String caplibsPath) {
		if (!caplibsPath.endsWith(File.separator)) {
			caplibsPath += File.separator;
		}
		
		
		String caplibsPathDict = caplibsPath + File.separator +"cap_libs" + File.separator + "dictionary" + File.separator;
		String caplibsPathWiserv = caplibsPath + File.separator + "tools" + File.separator + "4.1docs" + File.separator;
		String caplibsPluginsPath = caplibsPath + File.separator + "cap_libs" + File.separator + "plugins";
		
		List<String> allErrors = new ArrayList<String>();
		
		// caplibs/dict
		String xmlFileName = caplibsPathDict + "Category.xml";
		List<String> categoryError = this.validateXML(xmlFileName, XsdEnum.Category);
		if (categoryError != null && categoryError.size() > 0) {
			allErrors.addAll(categoryError);
		}
		
		// plugin
		File pluginsFile = new File(caplibsPluginsPath);
		Collection<File> fileNames = FileUtils.listFiles(pluginsFile,new String[] {"xml", "XML"}, false);
		for (File file : fileNames) {
			List<String> pluginError = this.validateXML(file.getPath(), XsdEnum.Plugin);
			if (pluginError != null && pluginError.size() > 0) {
				allErrors.addAll(pluginError);
			}
		}
		
		// 加载collect和resource
		IOFileFilter fileFilterCollect = new RegexFileFilter("collect.xml");
		IOFileFilter all = FileFilterUtils.trueFileFilter();
		Collection<File> collectFiles = FileUtils.listFiles(new File(caplibsPathWiserv), fileFilterCollect, all);
		for (File collectFile : collectFiles) {
			List<String> collectError = this.validateXML(collectFile.getPath(), XsdEnum.Collect);
			if (collectError != null && collectError.size() > 0) {
				allErrors.addAll(collectError);
			}
			// resource
			File resourceFile = new File(collectFile.getParent() + File.separator + "resource.xml");
			List<String> resourceError = this.validateXML(resourceFile.getPath(), XsdEnum.Resource);
			if (resourceError != null && resourceError.size() > 0) {
				allErrors.addAll(resourceError);
			}
		}
		return allErrors;
	}
	
    /** 
     * 通过XSD（XML Schema）校验XML 
     */ 
    private List<String> validateXML(String xmlFileName, XsdEnum xsdEnum) {
    	String xsdFileName = System.getProperty("caplibs.path") + File.separator + "tools" + File.separator + "schema" + File.separator;
    	if (xsdEnum == XsdEnum.Category)
    		xsdFileName += "Category.xsd";
    	else if (xsdEnum == XsdEnum.Plugin)
    		xsdFileName += "Plugin.xsd";
    	else if (xsdEnum == XsdEnum.Collect)
    		xsdFileName += "Collect.xsd";
    	else if (xsdEnum == XsdEnum.Resource)
    		xsdFileName += "Resource.xsd";
    	
        try { 
            //创建默认的XML错误处理器 
            XMLErrorHandler errorHandler = new XMLErrorHandler(); 
            //获取基于 SAX 的解析器的实例 
            SAXParserFactory factory = SAXParserFactory.newInstance(); 
            //解析器在解析时验证 XML 内容。 
            factory.setValidating(true); 
            //指定由此代码生成的解析器将提供对 XML 名称空间的支持。 
            factory.setNamespaceAware(true); 
            //使用当前配置的工厂参数创建 SAXParser 的一个新实例。 
            SAXParser parser = factory.newSAXParser(); 
            //创建一个读取工具 
            SAXReader xmlReader = new SAXReader(); 
            //获取要校验xml文档实例 
            Document xmlDocument = (Document) xmlReader.read(new File(xmlFileName)); 
            //设置 XMLReader 的基础实现中的特定属性。核心功能和属性列表可以在 [url]http://sax.sourceforge.net/?selected=get-set[/url] 中找到。 
            parser.setProperty( 
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
                    "http://www.w3.org/2001/XMLSchema"); 
            parser.setProperty( 
                    "http://java.sun.com/xml/jaxp/properties/schemaSource", 
                    "file:" + xsdFileName); 
            //创建一个SAXValidator校验工具，并设置校验工具的属性 
            SAXValidator validator = new SAXValidator(parser.getXMLReader()); 
            //设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。 
            validator.setErrorHandler(errorHandler); 
            //校验 
            validator.validate(xmlDocument); 

            //XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());
            //如果错误信息不为空，说明校验失败，打印错误信息 
            if (errorHandler.getErrors().hasContent()) { 
                //System.out.println("XML文件通过XSD文件校验失败！"); 
                //writer.write(errorHandler.getErrors());
                return convertElement(errorHandler.getErrors());
            }
        } catch (Exception ex) { 
            //System.out.println("XML文件: " + xmlFileName + " 通过XSD文件:" + xsdFileName + "检验失败。原因： " + ex.getMessage()); 
            String msg = ex.getMessage();
            List<String> errorsList = new ArrayList<String>();
            errorsList.add(msg);
            return errorsList;
        }
        
        return null;
    }
    
    private List<String> convertElement(Element element) {
    	List<String> errorList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Element> errorElements = element.elements("error");
    	for (Element errorElement : errorElements) {
    		String columnValue = errorElement.attributeValue("column");
    		String lineValue = errorElement.attributeValue("line");
    		String systemIDValue = errorElement.attributeValue("systemID");
    		String context = errorElement.getText();
    		errorList.add("所在文件：" + systemIDValue);
    		errorList.add("所在列:" + columnValue + "   " + "所在行：" + lineValue);
    		errorList.add("错误信息：" + context);
    	}
    	return errorList;
    }
    
}
