package com.mainsteam.stm.lucene.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.util.Version;

import com.mainsteam.stm.lucene.api.IIndexApi;
import com.mainsteam.stm.lucene.bo.IndexObj;
import com.mainsteam.stm.lucene.bo.IndexPage;
import com.mainsteam.stm.lucene.mapper.Doc;
import com.mainsteam.stm.lucene.mapper.Index;
import com.mainsteam.stm.lucene.mmsegext.OCMMSegAnalyzer;
import com.mainsteam.stm.util.IConstant;
import com.mainsteam.stm.util.Util;

/**
 * <li>文件名称: IndexImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class IndexImpl implements IIndexApi {

	private static final Version version=Version.LUCENE_4_9;
	
	private static final Logger LOGGER=Logger.getLogger(IndexImpl.class);
	
	/**
	 * 中文分词器
	 */
	private static final Analyzer analyzer=new OCMMSegAnalyzer();
	
	private static final FieldType keyFT=new FieldType();
	static{
		keyFT.setNumericType(FieldType.NumericType.LONG);
		keyFT.setIndexed(false);
		keyFT.setTokenized(false);
		keyFT.setStored(true);
	}
	
	private IndexWriter iw;
	
	private MultiFieldQueryParser mfqp;
	
	private Directory directory;
	
	private final String DSTM_KEY_FIELD;
	
	private final int DSTM_SEARCH_MAX_DOCS;
	
	private final Index[] DSTM_INDEXES;
	
	private final String[] DSTM_FILES;
	
	private IndexReader ir;
	
	private IndexSearcher is;
	private static final InputStream[] EMPTY_INPUTSTREAM= {};
	
	@Override
	public void createIndex(Object obj,InputStream[]inps) {
		try {
			this.ready();
			iw.addDocument(this.createDocument(obj,inps));
			this.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void createIndex(Object obj) {
		createIndex(obj, EMPTY_INPUTSTREAM);
	}
	
	@Override
	public void createIndexes(Collection<IndexObj> objs) {
		try {
			IndexObj obj;
			IndexObj[] iObjs=new IndexObj[0];
			iObjs=objs.toArray(iObjs);
			this.ready();
			for(int i=0,len=objs.size();i<len;i++){
				obj=iObjs[i];
				iw.addDocument(this.createDocument(obj.getObj(),obj.getInps()));
				if(i/1000==0){
					this.commit();
				}
			}
			this.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteIndex(long tableKey) {
		try {
			this.ready();
			iw.deleteDocuments(NumericRangeQuery.newLongRange(DSTM_KEY_FIELD,tableKey,tableKey,true,true));
			this.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteIndex(Collection<Long> keyFieldValue) {
		long keyV;
		Long[] keys=new Long[0];
		keys=keyFieldValue.toArray(keys);
		try {
			this.ready();
			for(int i=0,len=keyFieldValue.size();i<len;i++){
				keyV=keys[i];
				iw.deleteDocuments(NumericRangeQuery.newLongRange(DSTM_KEY_FIELD,keyV,keyV,true,true));
			}
			this.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteAll() {
		try {
			this.ready();
			this.iw.deleteAll();
			this.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateIndex(Object obj,InputStream...inps) {
		this.deleteIndex(getKeyFieldVal(obj));
		this.createIndex(obj, inps);
	}

	@Override
	public void updateIndexes(Collection<IndexObj> objs) {
		int size=objs.size();
		IndexObj[] indexObjs=new IndexObj[size];
		indexObjs=objs.toArray(indexObjs);
		Collection<Long> keys=new ArrayList<Long>(size);
		for(int i=0;i<size;i++){
			keys.add(getKeyFieldVal(indexObjs[i].getObj()));
		}
		deleteIndex(keys);
		createIndexes(objs);
	}

	@Override
	public long[] search(String words, int count) {
		long[] keys={};
		try {
			if(this.is==null)this.resetIndexSearch();
			ScoreDoc[] docs=this.is.search(this.mfqp.parse(words.trim()),count).scoreDocs;
			int len=docs.length;
			keys=new long[docs.length];
			for(int i=0;i<len;i++){
				keys[i]=Long.parseLong(this.is.doc(docs[i].doc).get(DSTM_KEY_FIELD));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keys;
	}

	@Override
	public long[] search(String words) {
		return search(words, DSTM_SEARCH_MAX_DOCS);
	}

	@Override
	public IndexPage search(String words, int begin, int count) {
		long[] keys=search(words, DSTM_SEARCH_MAX_DOCS);
		IndexPage page=new IndexPage();
		page.setTotal(keys.length);
		if(begin<page.getTotal()){
			long len=page.getTotal()-begin;
			if(count>len)count=(int)len;
			long[] keys1=new long[count];
			for(int i=0;i<count;i++){
				keys1[i]=keys[begin+i];
			}
			page.setKeys(keys1);
		}else{
			page.setKeys(new long[0]);
		}
		return page;
	}

	public IndexImpl(Doc doc){
		DSTM_KEY_FIELD=doc.getKeyField();
		DSTM_SEARCH_MAX_DOCS=doc.getSearchMaxDocs();
		DSTM_INDEXES=doc.getIndexes();
		DSTM_FILES=doc.getFiles();
		
		try {
			this.directory=NIOFSDirectory.open(new File(doc.getDirectory()),NoLockFactory.getNoLockFactory());
			this.mfqp=new MultiFieldQueryParser(version,doc.getQueryFields(),analyzer);
			this.mfqp.setDefaultOperator(Operator.OR);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	private void ready(){
		if(this.iw==null){
			try {
				this.iw=new IndexWriter(this.directory,new IndexWriterConfig(version,analyzer));
			} catch (IOException e) {
				LOGGER.error(e);
				e.printStackTrace();
			}
		}
	}
	
	private void commit(){
		try {
			this.iw.commit();
			if(IndexWriter.isLocked(directory)){
				IndexWriter.unlock(directory);
			}
			this.resetIndexSearch();
		} catch (IOException e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}
	
	private Long getKeyFieldVal(Object obj){
		if(obj==null)return IConstant.TEMP_KEY_VAL;
		try {
			obj=BeanUtils.getProperty(obj,DSTM_KEY_FIELD);
			return obj==null?IConstant.TEMP_KEY_VAL:Long.parseLong(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return IConstant.TEMP_KEY_VAL;
	}
	
	private String getFieldVal(Object obj,String field){
		if(obj==null)return null;
		try {
			return BeanUtils.getProperty(obj,field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Document createDocument(Object obj,InputStream[] inps){
		Document doc=new Document();
		doc.add(new LongField(DSTM_KEY_FIELD,getKeyFieldVal(obj), keyFT));
		String val=null;
		for(Index idx:DSTM_INDEXES){
			if(Util.isEmpty(val=getFieldVal(obj,idx.getField())))continue;
			IndexableField idxField=new TextField(idx.getField(), val.toString(),Store.NO);
			doc.add(idxField);
		}
		if(inps!=null){
			int len=inps.length;
			for(int i=0;i<len;i++){
				InputStream is=inps[i];
				doc.add(new TextField(DSTM_FILES[i],new InputStreamReader(is)));
			}
		}
		return doc;
	}
	
	private void resetIndexSearch(){
		try {
			if(this.ir!=null)this.ir.close();
			this.ir=DirectoryReader.open(this.directory);
		} catch (IOException e) {
			this.createIndex(null);
			try {
				this.ir=DirectoryReader.open(this.directory);
			} catch (IOException e1) {
			}
			this.deleteIndex(IConstant.TEMP_KEY_VAL);
		}
		this.is=new IndexSearcher(this.ir);
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(IndexWriter.isLocked(directory)){
			IndexWriter.unlock(directory);
		}
		this.iw.close();
		super.finalize();
	}
}
