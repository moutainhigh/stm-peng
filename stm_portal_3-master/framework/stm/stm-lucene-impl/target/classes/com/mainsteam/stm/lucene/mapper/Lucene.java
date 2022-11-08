package com.mainsteam.stm.lucene.mapper;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.util.IConstant;
import com.mainsteam.stm.util.Util;
/**
 * <li>文件名称: Lucene.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class Lucene {
	static Pattern emptyPattern=Pattern.compile("\\S");
	String path;
	String lang;
	Doc[] docs;
	private static final SAXReader reader = new SAXReader();
	
	@SuppressWarnings("unchecked")
	public Lucene(String luceneConfigPath,String luceneStorePath){
		//luceneConfigPath = "/root/TongWeb6.1/common/classes/config/lucene/lucene.xml";
		Element lucene=getRootElement(luceneConfigPath);
		
		this.path=luceneStorePath+File.separator;
		this.lang=lucene.attributeValue(IConstant.str_lang);
		
		List<Element> docsE=lucene.elements();
		this.docs=parseDoc(docsE);
	}
	
	public Doc[] getDocs(){
		return docs;
	}
	
	private static Element getRootElement(final String filePath) {
		File file=new File(filePath);
		Element root=null;
    	try {
    		root=reader.read(file).getRootElement();
		} catch (DocumentException e) {
			System.out.println("解析lucene xml配置文件错误！");
			e.printStackTrace();
		}
		return root;
	}
	
	@SuppressWarnings("unchecked")
	private Doc[] parseDoc(List<Element> docsE){
		int dSize=docsE.size();
		Doc[] docs=new Doc[dSize];
		for(int j=0;j<dSize;j++){
			Element docE=docsE.get(j);
			List<Element> indexesE=docE.elements();
			Index[] indexes=parseIndex(indexesE);
			
			String lang=docE.attributeValue(IConstant.str_lang);
			lang=Util.isEmpty(lang)?this.lang:lang;
			
			String maxFiles=docE.attributeValue(IConstant.str_maxFiles);
			docs[j]=new Doc(
				docE.attributeValue(IConstant.str_obj),
				docE.attributeValue(IConstant.str_keyField),
				lang,
				Integer.parseInt(maxFiles),
				this.path+docE.attributeValue(IConstant.str_obj),
				Integer.parseInt(docE.attributeValue(IConstant.str_searchMaxDocs)),
				indexes);
		}
		return docs;
	}
	
	private Index[] parseIndex(List<Element> indexesE){
		int size=indexesE.size();
		Index[] indexes=new Index[size];
		for(int i=0;i<size;i++){
			Element indexE=indexesE.get(i);
			indexes[i]=new Index(
				indexE.attributeValue(IConstant.str_field), 
				indexE.attributeValue(IConstant.str_type), 
				Boolean.parseBoolean(indexE.attributeValue(IConstant.str_store))
			);
		}
		return indexes;
	}
}


