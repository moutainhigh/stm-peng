package com.mainsteam.stm.lucene.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.lucene.api.IIndexApi;
import com.mainsteam.stm.lucene.api.IIndexKeyConstant;
import com.mainsteam.stm.lucene.api.IIndexManageApi;
import com.mainsteam.stm.lucene.bo.IndexObj;
import com.mainsteam.stm.lucene.mmsegext.OCMMSegAnalyzer;

/**
 * <li>文件名称: IndexImplTest.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   ziwenwen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:META-INF/services/lucene-platform-beans.xml"})
public class IndexImplTest {

	@Resource(name="IIndexManageApi")
	IIndexManageApi mApi;
	
	IIndexApi api;

	int i=1;
	Map<String,Map<String,String>> objs=new HashMap<String, Map<String,String>>();
	
	@Test
	public void testDeleteAll() {
		api.deleteAll();
	}
	
	private Map<String,String> getData(String title,String keywords,String content){
		String id=String.valueOf(i++);
		Map<String,String> kn=new HashMap<String, String>();
		kn.put("id", id);
		kn.put("title",title);
		kn.put("keywords",keywords);
		kn.put("content",content);
		objs.put(id,kn);
		return kn;
	}

	@Test
	public void testCreateIndexObject() {
//		api.createIndex(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了"));
//		api.createIndex(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一"));

		System.out.println("testCreateIndexObject建立索引的对象："+objs);
	}
	
	@Test
	public void testCreateIndexObjects() {
		List<IndexObj> ios=new ArrayList<IndexObj>();
		ios.add(new IndexObj(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了")));
		ios.add(new IndexObj(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一")));
		ios.add(new IndexObj(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了")));
		ios.add(new IndexObj(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一")));
		ios.add(new IndexObj(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了")));
		ios.add(new IndexObj(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一")));
		ios.add(new IndexObj(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了")));
		ios.add(new IndexObj(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一")));
		ios.add(new IndexObj(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了")));
		ios.add(new IndexObj(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一")));
		ios.add(new IndexObj(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了")));
		ios.add(new IndexObj(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一")));
		ios.add(new IndexObj(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了")));
		ios.add(new IndexObj(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一")));
		ios.add(new IndexObj(getData("中华人民共和国", "国家元首", "宣告中国人民站起来了")));
		ios.add(new IndexObj(getData("台湾是中国的一部分", "不可分离", "台湾回归，祖国统一")));
		api.createIndex(ios);
		System.out.println("testCreateIndexObjects建立索引的对象："+objs);
	}

	
	
	@Test
	public void testSearch() {
		long[] docs=api.search("中华人民共和国 祖国统一",20);
		System.out.println(docs.length);
		for(int i=0,len=docs.length;i<len;i++){
			System.out.println(docs[i]);
		}
	}
	
	@Test
	public void testUpdateIndexObjectInputStreamArray() {
//		api.updateIndex(kn);
	}

	@Test
	public void testDeleteIndex() {
//		api.deleteIndex(IConstant.TEMP_KEY_VAL);
	}

	@Before
	public void init() throws IOException{
		api=mApi.getIndexApi(IIndexKeyConstant.KEY_KNOWLEDGE);
	}
    
    public static void testAnaly(){
    	Analyzer a=new OCMMSegAnalyzer();
    	try {
			TokenStream ts=a.tokenStream("title","中华人民共和国 国家元首台湾回归，祖国统一");
			while(ts.incrementToken()){
				System.out.println(ts.getAttribute(CharTermAttribute.class));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
    	testAnaly();
	}
}


