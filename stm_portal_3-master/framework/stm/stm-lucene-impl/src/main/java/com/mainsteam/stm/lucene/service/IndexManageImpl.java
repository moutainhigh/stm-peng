package com.mainsteam.stm.lucene.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.lucene.api.IIndexApi;
import com.mainsteam.stm.lucene.api.IIndexManageApi;
import com.mainsteam.stm.lucene.mapper.Doc;
import com.mainsteam.stm.lucene.mapper.Lucene;
import com.mainsteam.stm.util.ClassPathUtil;

/**
 * <li>文件名称: IndexManageImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
@Service("IIndexManageApi")
public class IndexManageImpl implements IIndexManageApi {

	private static final Map<String, IIndexApi> indexApies=new HashMap<String, IIndexApi>();

	private static final Map<String, Doc> docMap=new HashMap<String, Doc>();
	
	
	@Override
	public IIndexApi getIndexApi(String doc_obj) throws IOException {
		IIndexApi api=indexApies.get(doc_obj);
		if(api==null){
			Doc doc=docMap.get(doc_obj);
			if(doc==null)throw new NoMapperDocException(doc_obj);
			indexApies.put(doc_obj, api=new IndexImpl(doc));
		}
		return api;
	}

	@Override
	public IIndexApi getIndexApi(String doc_obj, String autoKey) throws IOException {
		String key=doc_obj+autoKey;
		IIndexApi api=indexApies.get(key);
		if(api==null){
			Doc doc=docMap.get(doc_obj);
			if(doc==null)throw new NoMapperDocException(doc_obj);
			Doc doc1=new Doc();
			try {
				BeanUtils.copyProperties(doc1, doc);
				doc1.setObj(key);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			indexApies.put(key, api=new IndexImpl(doc1));
		}
		return api;
	}
	private String luceneConfigPath;
	
	@Value("${stm.file.root_path}")
	private String luceneStorePath;
	
	@Value("${stm.file.islocal}")
	private String islocal;
	
	public IndexManageImpl(){}
	
	@PostConstruct
	public void init()  throws IOException{
		if(islocal.equals("on"))luceneStorePath=ClassPathUtil.getTomcatHome()+"lucene"+File.separatorChar;
		
		luceneConfigPath=ClassPathUtil.getCommonClasses()
			+"config"+File.separatorChar
			+"lucene"+File.separatorChar
			+"lucene.xml";
		
		Lucene lucene=new Lucene(luceneConfigPath, luceneStorePath+File.separatorChar+"lucene");
		Doc[] docs=lucene.getDocs();
		Doc doc;
		for(int i=0,len=docs.length;i<len;i++){
			doc=docs[i];
			docMap.put(doc.getObj(),doc);
		}
	}
}
