package com.mainsteam.stm.topo.realPanelXml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.bo.BackbordBo;
import com.mainsteam.stm.topo.dao.IBackbordBaseDao;

/**
 * 解析3.5xml数据到DB
 * @author zwx
 */
public class ImportXml{
	
	/**
	 * 导出xml数据
	 * @throws FileNotFoundException 
	 * @throws DocumentException 
	 */
	public void importXml(IBackbordBaseDao dao) throws FileNotFoundException, DocumentException {
		//1.读取xml(将realPanelXml放在test的resources下即可)
		//xmlUrl=E:/workspace/Portal/oc-portal/oc-topo-impl/target/test-classes/realPanelXml
// 		String xmlUrl = Thread.currentThread().getContextClassLoader().getResource("realPanelXml").getFile();
 		String xmlUrl = "E:/realPanelXml";
 		List<Map<String , Object>> files = readXmlFileList(xmlUrl);
		
		//2.解析xml
 		List<Map<String, Object>> xmlMapList = readXml(files);
 		
		//3.封装json
 		List<BackbordBo> list = packBackbordInfo(xmlMapList);
 		
		//4.插入db
 		dao.deleteAll();
 		for(BackbordBo bo:list){
 			dao.save(bo);
 		}
	}
	
	private List<BackbordBo> packBackbordInfo(List<Map<String, Object>> xmlMapList){
		List<BackbordBo> list = new ArrayList<BackbordBo>();
		for(Map<String, Object> xmlMap:xmlMapList){
			if(null == xmlMap.get("backgrounds")){
				xmlMap.put("backgrounds", new String[0]);
			}
			if(null == xmlMap.get("powers")){
				xmlMap.put("powers", new String[0]);
			}
			if(null == xmlMap.get("cards")){
				xmlMap.put("cards", new String[0]);
			}
			if(null == xmlMap.get("ports")){
				xmlMap.put("ports", new String[0]);
			}
			
			BackbordBo info = new BackbordBo();
			info.setVendor((String) xmlMap.get("vendor"));
			info.setType((String) xmlMap.get("type"));
			xmlMap.remove("vendor");
			xmlMap.remove("type");
			info.setInfo(JSONObject.toJSONString(xmlMap));
			
			list.add(info);
		}
		
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	private List<Map<String, Object>> readXml(List<Map<String , Object>> files) throws FileNotFoundException, DocumentException{
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
 		for(Map<String , Object> m:files){
 			File f = (File) m.get("file");
// 			System.out.println(f.getAbsolutePath()+",	厂商："+m.get("vendor")+", 型号："+m.get("type"));
 			if(!f.getName().endsWith(".xml")) continue;
 			
 			List<Map<String, Object>> backgrounds = new ArrayList<Map<String,Object>>();
 			List<Map<String, Object>> cards = new ArrayList<Map<String,Object>>();
 			List<Map<String, Object>> ports = new ArrayList<Map<String,Object>>();
 			Map<String, Object> map = new HashMap<String, Object>();
 			
 			map.put("vendor", m.get("vendor"));
 			map.put("type", m.get("type"));
 			
 			InputStream is = new FileInputStream(f);
 			SAXReader reader = new SAXReader();
			Document document = reader.read(is);

			Element root = document.getRootElement();
			for (Iterator i = root.elementIterator(); i.hasNext();) {
	    		Element element = (Element) i.next();
	    		if ("properties".equalsIgnoreCase(element.getName())){	//背景图信息
	    			for (Iterator c = element.elementIterator(); c.hasNext();) {
	    				Element e = (Element)c.next();
	    				if ("image".equals(e.getName())){
	    					Map<String, Object> background = new HashMap<String, Object>();
	    					background.put("imgUrl", parseImgUrl(e.attributeValue("url")));
	    					background.put("width", parse2Double(e.attributeValue("width")));
	    					background.put("height", parse2Double(e.attributeValue("height")));
	    					
	    					backgrounds.add(background);
	    				}
	    			}
	    			
	    			map.put("backgrounds", backgrounds);
	    		}
	    		
	    		if("cards".equalsIgnoreCase(element.getName())){	//板卡信息
	    			for (Iterator c = element.elementIterator(); c.hasNext();) {
	    				Element e = (Element)c.next();
	    				if ("card".equals(e.getName())){
	    					Map<String, Object> card = new HashMap<String, Object>();
	    					card.put("index", parse2Double(e.attributeValue("index")));
	    					if(e.elementIterator().hasNext()){
	    						//3.5具体某个背板保存格式
	    						card.put("x", parse2Double(e.attributeValue("x")));
	    						card.put("y", parse2Double(e.attributeValue("y")));
	    						card.put("width", parse2Double(e.attributeValue("width")));
	    						card.put("height", parse2Double(e.attributeValue("height")));
	    						card.put("imgUrl", e.attributeValue("bgimgfile"));
	    					}else{
	    						//3.5默认背板保存格式
	    						for(Iterator ce = e.elementIterator(); ce.hasNext();){
	    							Element e1 = (Element)ce.next();
	    							if("image".equals(e1.getName())){
	    								card.put("width", parse2Double(e1.attributeValue("widht")));
	    								card.put("height", parse2Double(e1.attributeValue("height")));
	    								card.put("imgUrl", parseImgUrl(e1.attributeValue("url")));
	    							}
	    							if("location".equals(e1.getName())){
	    								card.put("x", parse2Double(e1.attributeValue("x")));
	    								card.put("y", parse2Double(e1.attributeValue("y")));
	    							}
	    						}
	    					}
	    					
	    					cards.add(card);
	    				}
	    			}
	    			map.put("cards", cards);
	    		}
	    		
	    		if("ports".equalsIgnoreCase(element.getName())){	//接口信息
	    			for (Iterator c = element.elementIterator(); c.hasNext();) {
	    				Element e = (Element)c.next();
	    				if ("port".equals(e.getName())){
	    					Map<String, Object> port = new HashMap<String, Object>();
	    					for(Iterator ce = e.elementIterator(); ce.hasNext();){
	    						Element e1 = (Element)ce.next();
	    						if("portIndex".equals(e1.getName())){
	    							port.put("index", parse2Double(e1.getTextTrim()));
	    						}
	    						if("rectangle".equals(e1.getName())){
	    							port.put("width", Double.parseDouble(e1.attributeValue("width")));
	    							port.put("height", Double.parseDouble(e1.attributeValue("height")));
	    						}
	    						if("location".equals(e1.getName())){
	    							port.put("x", parse2Double(e1.attributeValue("x")));
	    							port.put("y", parse2Double(e1.attributeValue("y")));
	    						}
	    						if("image".equals(e1.getName())){
	    							port.put("onImg", parseImgUrl(e1.attributeValue("onIcon")));
	    							port.put("offImg", parseImgUrl(e1.attributeValue("offIcon")));
	    							port.put("disableImg", parseImgUrl(e1.attributeValue("disableIcon")));
	    						}
	    					}
	    					ports.add(port);
	    				}
	    				
	    				map.put("ports", ports);
	    			}
	    		}
	    	}
			list.add(map);
 		}
 		
 		return list;
	}
	
	/**
	 * 转换图片地址
	 * @param url
	 * @return
	 */
	private String  parseImgUrl(String url) {
//		System.out.println("原始地址："+url);
		String rootImg = "themes/blue/images/topo";
		String rootImgUrl = rootImg+"/realPanel";
		if(url.contains("img/")){
			url = url.replace("img", rootImgUrl);
		}else{
			url = rootImg +"/"+ url;
		}
//		System.out.println("准换后地址："+url);
		
		return url;
	}
	/**
	 * 字符串转换为doubel
	 * @param str
	 * @return
	 */
	private double parse2Double(String param){
		return Double.parseDouble(StringUtils.isBlank(param)?"0":param);
	}
	
	private List<Map<String , Object>> readXmlFileList(String rootUrl){
		List<Map<String , Object>> list = new ArrayList<Map<String , Object>>();
		
		File rootFile = new File(rootUrl);
		String[] files = rootFile.list();
		for(String f:files){
			File file = new File(rootUrl + "/" + f);
			String[] file1s = file.list();
			for(String f1:file1s){
				if(!f1.endsWith(".xml")) continue;
				
				File ff = new File(rootUrl + "/" +f+ "/" +f1);
				
				Map<String , Object> map = new HashMap<String, Object>();
				map.put("vendor", f);	//厂商
				map.put("type", f1.substring(0,f1.lastIndexOf(".")));	//型号
				map.put("file", ff);	//背板信息文件
				
				list.add(map);
			}
		}
		
		return list;
	}
	
}
