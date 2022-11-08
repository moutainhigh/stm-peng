package com.mainsteam.stm.knowledge.zip.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.exception.OriginalException;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;
import com.mainsteam.stm.knowledge.zip.annotation.Table;
import com.mainsteam.stm.knowledge.zip.api.IKnowledgeZipApi;
import com.mainsteam.stm.knowledge.zip.bo.CloudyKnowledge;
import com.mainsteam.stm.knowledge.zip.constants.KnowledgeZipConstants;
import com.mainsteam.stm.knowledge.zip.constants.LocalKnowledgeConstants;
import com.mainsteam.stm.knowledge.zip.dao.IKnowledgeZipDao;
import com.mainsteam.stm.knowledge.zip.dao.sqlite.IKnowledgeZipSqliteDao;
import com.mainsteam.stm.lucene.api.IIndexApi;
import com.mainsteam.stm.lucene.api.IIndexKeyConstant;
import com.mainsteam.stm.lucene.api.IIndexManageApi;
import com.mainsteam.stm.lucene.bo.IndexObj;
import com.mainsteam.stm.lucene.bo.IndexPage;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.util.ClassPathUtil;
import com.mainsteam.stm.util.FileUtil;

/**
 * <li>文件名称: com.mainsteam.stm.knowledge.zip.service.impl.IKnowledgeZipImpl.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月13日
 */
@Service
@SuppressWarnings({"rawtypes","unchecked"})
public class KnowledgeZipImpl implements IKnowledgeZipApi{
	
	@Autowired
	private IKnowledgeZipSqliteDao zipSqliteDao;
	
	@Resource(name="IKnowledgeZipDao")
	private IKnowledgeZipDao zipDao;
	
	@Autowired
	private IFileClientApi fileClientApi;
	
	private ISequence seq;	//本地知识库seq
	
	private ISequence resolveSeq;	//解决方案Seq
	
	private IIndexApi indexApi;
	/**
	 * 云端知识库表
	 */
	private static final Class[] CLAZZS = {
		CloudyKnowledge.class
	};
	
	@Value("${jdbc.sqlite.url}")
	private String dbPath;
	
	private static String SQLITE_START_URL = "jdbc:sqlite:";
	
	@Value("${stm.knowledge.isInitIndex}")
	private int initKnowledgeIndex;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	public KnowledgeZipImpl(SequenceFactory sequenceFactory, IIndexManageApi manageApi){
		seq = sequenceFactory.getSeq(SEQ.SEQNAME_STM_KNOWLEDGE);
		resolveSeq = sequenceFactory.getSeq(SEQ.SEQNAME_STM_KNOWLEDGE_SOURCE_RESOLVE_REL);
		try {
			indexApi = manageApi.getIndexApi(IIndexKeyConstant.KEY_KNOWLEDGE);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new OriginalException(e);
		}
	}
	
	@PostConstruct
	public void init() throws Exception{
		// 统信 改 end
		if(initKnowledgeIndex==1){
			if(logger.isDebugEnabled()){
				logger.debug("开始初始化知识库索引。。。");
			}
			List<KnowledgeBo> knowledges = zipDao.getAllKnowledges();
			List<IndexObj> indexs = new ArrayList<IndexObj>();
			for(KnowledgeBo bo : knowledges){
				indexs.add(new IndexObj(bo));
			}
			indexApi.deleteAll();
			indexApi.createIndexes(indexs);
			logger.debug("初始化知识库索引结束。。。");
		}

		this.dbPath = this.dbPath.replace(SQLITE_START_URL, "");
		// 统信 改 end
		if(!new File(this.dbPath).exists()){
			throw new Exception("jdbc.sqlite.url配置错误！！！");
		}
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.knowledge.zip.api.IKnowledgeZipApi#importCloudyZip(java.io.InputStream)
	 */
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	@Override
	public int importCloudyZip(InputStream in, ILoginUser user) {
		if(FileUtil.inputStreamToFile(in, dbPath, 1024)){
			/***************同步云端知识表和知识附件表start****************/
			List<CloudyKnowledge> cloudyKnowledges = zipSqliteDao.getAllCloudyKnowledge();	//获取缓存数据库中的云端知识
			List<Long> fileIds = zipDao.selectLocalCloudyKnowledgeAttachmentFileIds(KnowledgeZipConstants.KNOWLEDGE_TYPE_CLOUDY);	//fileIds用于文件服务器删除文件
			try {
				if(fileIds.size()>0){
//					fileClientApi.deleteFiles(fileIds, KnowledgeZipConstants.KNOWLEDGE_FILE_GROUP_NAME);
					fileClientApi.deleteFiles(fileIds);
				}
			} catch (IOException e1) {
				logger.error("KnowledgeZipImpl.importCloudyZip: Attachment delete failed!" + e1.getMessage());
			}
			List<KnowledgeBo> knowledgeBos = new ArrayList<KnowledgeBo>();
			List<KnowledgeResolveBo> resolveBos = new ArrayList<KnowledgeResolveBo>();
			List<KnowledgeAttachmentBo> attachmentBos = new ArrayList<KnowledgeAttachmentBo>();
			Iterator<CloudyKnowledge> iter = cloudyKnowledges.iterator();
			while(iter.hasNext()){
				CloudyKnowledge knowledge = iter.next();
				KnowledgeBo localKnowledge = null;
				if(StringUtils.isNotBlank(knowledge.getSourceContent())){
					try{
						localKnowledge = zipDao.getKnowledgeBySourceContent(knowledge.getSourceContent());
					}catch(Exception e){
						logger.error("importCloudyZip: 本地知识原因重复"+ e.getMessage());
						continue;
					}
				}else{
					continue;
				}
				KnowledgeBo knowledgeBo = new KnowledgeBo();
				if(localKnowledge==null){
					try {
						BeanUtils.copyProperties(knowledgeBo, knowledge);
					} catch (Exception e) {
						logger.error("KnowledgeZipImpl.importCloudyZip: copyProperties Error!" + e.getMessage());
					}
					knowledgeBo.setId(seq.next());
					knowledgeBo.setIsCloudy(KnowledgeZipConstants.KNOWLEDGE_TYPE_CLOUDY);
					knowledgeBo.setCreateUserId(user.getId());
					knowledgeBo.setCreateTime(new Date());
					knowledgeBos.add(knowledgeBo);
				}
				KnowledgeResolveBo resolve = new KnowledgeResolveBo();
				if(StringUtils.isNotBlank(knowledge.getResolveContent())){
					if(zipDao.getResolveCountByContent(knowledge.getResolveContent())>0){
						continue;
					}
					resolve.setId(resolveSeq.next());
					resolve.setIsScript(isScript(knowledge) ? LocalKnowledgeConstants.RESOLVE_TYPE_SCRIPT
							: LocalKnowledgeConstants.RESOLVE_TYPE_WORD);
					resolve.setKnowledgeId(localKnowledge==null ? knowledgeBo.getId() : localKnowledge.getId());
					resolve.setResolveContent(knowledge.getResolveContent());
					resolve.setResolveTitle(knowledge.getTitle());
					resolveBos.add(resolve);
				}else{
					continue;
				}
				if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName1())){
					attachmentBos.add(uploadFile(new KnowledgeAttachmentBo(),
							knowledge.getResolveAttachmentName1(),
							knowledge.getResolveAttachmentContent1(),
							resolve.getId(),
							1));
				}
				if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName2())){
					attachmentBos.add(uploadFile(new KnowledgeAttachmentBo(),
							knowledge.getResolveAttachmentName2(),
							knowledge.getResolveAttachmentContent2(),
							resolve.getId(),
							2));
				}
				if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName3())){
					attachmentBos.add(uploadFile(new KnowledgeAttachmentBo(),
							knowledge.getResolveAttachmentName3(),
							knowledge.getResolveAttachmentContent3(),
							resolve.getId(),
							3));
				}
				if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName4())){
					attachmentBos.add(uploadFile(new KnowledgeAttachmentBo(),
							knowledge.getResolveAttachmentName4(),
							knowledge.getResolveAttachmentContent4(),
							resolve.getId(),
							4));
				}
				if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName5())){
					attachmentBos.add(uploadFile(new KnowledgeAttachmentBo(),
							knowledge.getResolveAttachmentName5(),
							knowledge.getResolveAttachmentContent5(),
							resolve.getId(),
							5));
				}
			}
			
			for(int i=0, len=knowledgeBos.size(); i<len;){
				int end = (i+LocalKnowledgeConstants.MAX_INSERT_RECODES)<knowledgeBos.size() ? 
						(i+LocalKnowledgeConstants.MAX_INSERT_RECODES) : knowledgeBos.size();
				zipDao.saveKnowledgeBo(knowledgeBos.subList(i, end));
				i += LocalKnowledgeConstants.MAX_INSERT_RECODES;
			}
			for(int i=0,len=resolveBos.size(); i<len;){
				int end = (i+LocalKnowledgeConstants.MAX_INSERT_RECODES)<resolveBos.size() ? 
						(i+LocalKnowledgeConstants.MAX_INSERT_RECODES) : resolveBos.size();
				zipDao.saveKnowledgeResolveBo(resolveBos.subList(i, end));
				i += LocalKnowledgeConstants.MAX_INSERT_RECODES;
			}
			for(int i=0,len=attachmentBos.size(); i<len;){
				int end = (i+LocalKnowledgeConstants.MAX_INSERT_RECODES)<attachmentBos.size() ? 
						(i+LocalKnowledgeConstants.MAX_INSERT_RECODES) : attachmentBos.size();
				zipDao.saveResolveAttachment(attachmentBos.subList(i, end));
				i += LocalKnowledgeConstants.MAX_INSERT_RECODES;
			}
			/***************同步云端知识表和知识附件表end****************/
			
			/***************建立搜索索引start****************/
			try{
				List<IndexObj> indexs = new ArrayList<IndexObj>();
				for(KnowledgeBo bo : knowledgeBos){
					indexs.add(new IndexObj(bo));
				}
				indexApi.createIndexes(indexs);
			}catch(Exception e){
				logger.error("KnowledgeZipImpl.importCloudyZip: toIndex Error!" + e.getMessage());
			}
			/***************建立搜索索引end****************/
			return knowledgeBos.size();
		}
		return -1;
	}
	
	private Boolean isScript(CloudyKnowledge knowledge){
		if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName1())){
			String ext = knowledge.getResolveAttachmentName1().toLowerCase();
			ext = ext.substring(ext.lastIndexOf(".")+1, ext.length());
			if(KnowledgeResolveBo.SCRIPT_FILE_EXT.indexOf(ext)!=-1){
				return true;
			}
		}
		if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName2())){
			String ext = knowledge.getResolveAttachmentName2().toLowerCase();
			ext = ext.substring(ext.lastIndexOf(".")+1, ext.length());
			if(KnowledgeResolveBo.SCRIPT_FILE_EXT.indexOf(ext)!=-1){
				return true;
			}
		}
		if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName3())){
			String ext = knowledge.getResolveAttachmentName3().toLowerCase();
			ext = ext.substring(ext.lastIndexOf(".")+1, ext.length());
			if(KnowledgeResolveBo.SCRIPT_FILE_EXT.indexOf(ext)!=-1){
				return true;
			}
		}
		if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName4())){
			String ext = knowledge.getResolveAttachmentName4().toLowerCase();
			ext = ext.substring(ext.lastIndexOf(".")+1, ext.length());
			if(KnowledgeResolveBo.SCRIPT_FILE_EXT.indexOf(ext)!=-1){
				return true;
			}
		}
		if(StringUtils.isNotBlank(knowledge.getResolveAttachmentName5())){
			String ext = knowledge.getResolveAttachmentName5().toLowerCase();
			ext = ext.substring(ext.lastIndexOf(".")+1, ext.length());
			if(KnowledgeResolveBo.SCRIPT_FILE_EXT.indexOf(ext)!=-1){
				return true;
			}
		}
		return false;
	}
	
	private KnowledgeAttachmentBo uploadFile(KnowledgeAttachmentBo attachmentBo, String name, byte[] content, Long id, int sort){
		attachmentBo.setResolveId(id);
		try {
			Long fileId = fileClientApi.upLoadFile(KnowledgeZipConstants.KNOWLEDGE_FILE_GROUP_NAME, 
					new ByteArrayInputStream(content), 
					name);
			attachmentBo.setFileId(fileId);
			attachmentBo.setFileName(name);
			attachmentBo.setUploadDate(new Date());
			attachmentBo.setSort(sort);
		} catch (IOException e) {
			logger.warn("KnowledgeZipImpl.importCloudyZip: upLoadFile Error!" + e.getMessage());
		}
		return attachmentBo;
	}
	
	/* (non-Javadoc)
	 * @see com.mainsteam.stm.knowledge.zip.api.IKnowledgeZipApi#exprotLocalZip()
	 */
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	@Override
	public InputStream exprotLocalZip() {
		for(Class clazz : CLAZZS){
			Table table = (Table)clazz.getAnnotation(Table.class);
			if(table!=null){
				zipSqliteDao.deleteByTableName(table.name());
			}
		}
		List<CloudyKnowledge> knowledges = zipDao.getKnowledgeResolve(KnowledgeZipConstants.KNOWLEDGE_TYPE_LOCAL);
		for(CloudyKnowledge cloudyKnowledge : knowledges){
			 List<KnowledgeAttachmentBo> attachments = zipDao.getKnowledgeAttachment(cloudyKnowledge.getResolveId());
			 setAttachments(attachments, cloudyKnowledge);
			 cloudyKnowledge.setCloudyId(cloudyKnowledge.getResolveId().toString());
			 zipSqliteDao.saveKnowledges(cloudyKnowledge);
		}
//		zipSqliteDao.saveKnowledges(knowledges);
		try {
			return FileUtil.getFileToInputStream(dbPath);
		} catch (IOException e) {
			logger.error("KnowledgeZipImpl.exprotLocalZip: File is not find!" + e.getMessage());
			return null;
		}
	}

	private void setAttachments(List<KnowledgeAttachmentBo> attachments, CloudyKnowledge cloudyKnowledge){
		for(int i=0,len=attachments.size(); i<len&&i<5; i++){
			FileInputStream in = null;
			try {
				File file = fileClientApi.getFileByID(attachments.get(i).getFileId());
				in = new FileInputStream(file);
				int length = in.available();
				byte[]buf = new byte[length];
				in.read(buf);
				switch (i) {
				case 0:
					cloudyKnowledge.setResolveAttachmentContent1(buf);
					cloudyKnowledge.setResolveAttachmentName1(file.getName());
					break;
				case 1:
					cloudyKnowledge.setResolveAttachmentContent2(buf);
					cloudyKnowledge.setResolveAttachmentName2(file.getName());
					break;
				case 2:
					cloudyKnowledge.setResolveAttachmentContent3(buf);
					cloudyKnowledge.setResolveAttachmentName3(file.getName());
					break;
				case 3:
					cloudyKnowledge.setResolveAttachmentContent4(buf);
					cloudyKnowledge.setResolveAttachmentName4(file.getName());
					break;
				case 4:
					cloudyKnowledge.setResolveAttachmentContent5(buf);
					cloudyKnowledge.setResolveAttachmentName5(file.getName());
					break;
				default:
					break;
				}
			} catch (Exception e) {
				logger.warn("KnowledgeZipImpl.exprotLocalZip: getFileByID Error!" + e.getMessage());
			}finally{
				try {
					if(in!=null){
						in.close();
					}
				} catch (IOException e) {
					logger.warn("KnowledgeZipImpl.exprotLocalZip: in.close() Error!" + e.getMessage());
				}
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.knowledge.zip.api.IKnowledgeZipApi#save(com.mainsteam.stm.knowledge.zip.bo.CloudyKnowledge)
	 */
	@Override
	public void save(CloudyKnowledge knowledge) {
		zipSqliteDao.save(knowledge);
	}


	/* (non-Javadoc)
	 * @see com.mainsteam.stm.knowledge.zip.api.IKnowledgeZipApi#searchKnowledge()
	 */
	@Override
	public List<KnowledgeBo> searchKnowledge(String keyword,Page<Byte, Byte> page) {
		IndexPage indexPage = indexApi.search(keyword, Integer.valueOf(page.getStartRow() + ""), Integer.valueOf(page.getRowCount() + ""));
		long [] knowledgeIds = indexPage.getKeys();
		if(knowledgeIds.length>0){
			return zipDao.getKnowledgeByIds(knowledgeIds);
		}
		return Collections.EMPTY_LIST;
	}
	
	/***********************读取txt文件中的知识start*****************************/
	private static final String FILE_DIRECTORY_PATH = ClassPathUtil.getCommonClasses()
			+"config"+File.separatorChar
			+"knowledge";
	/**
	 * 初始化本地知识库
	 * @throws IOException
	 * @author	ziwen
	 * @date	2019年12月22日
	 */
//	@PostConstruct
	public void loadKnowledge() throws IOException{
		File directory = new File(FILE_DIRECTORY_PATH);
		List<String> knowledgeContents = new ArrayList<String>();
		if(directory.exists()&&directory.isDirectory()){
			File[] files = directory.listFiles();
			Arrays.sort(files, new FileComparator());
			for(File file : files){
				StringBuilder sb = new StringBuilder();
				if(file.getName().toLowerCase().endsWith(".txt")){
					String code = getCharset(file);
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), code));
					String buf = null;
					while((buf = br.readLine())!=null){
						sb.append(buf);
					}
					br.close();
					knowledgeContents.add(sb.toString());
				}
			}
		}else{
			logger.error("没有初始化数据");
			return ;
		}
		
		List<KnowledgeBo> knowledgeBos = new ArrayList<KnowledgeBo>();
		Long seqCode = 101163L;	//默认ID在10W至20W之间
		for(String content : knowledgeContents){
			
			KnowledgeBo knowledge = new KnowledgeBo();
			knowledge.setId(seqCode++);
			knowledge.setSourceContent(content);
			knowledge.setIsCloudy(KnowledgeZipConstants.KNOWLEDGE_TYPE_LOCAL);
			knowledge.setCreateUserId(0L);
			knowledge.setCreateTime(new Date());
			knowledgeBos.add(knowledge);
		}
		if(knowledgeBos.size()==0){
			return ;
		}
		zipDao.saveLocalKnowledges(knowledgeBos);
	}
	
	static class FileComparator implements Comparator<File>{
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(File o1, File o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
	private static String getCharset(File fileName) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
        byte[] b = new byte[10];
        bin.read(b, 0, b.length);
        String first = toHex(b);
        //这里可以看到各种编码的前几个字符是什么，gbk编码前面没有多余的
        String code = null;
        if (first.startsWith("EFBBBF")) {
            code = "UTF-8";
        } else if (first.startsWith("FEFF00")) {
            code = "UTF-16BE";
        } else if (first.startsWith("FFFE")) {
            code = "Unicode";
        }else {
            code = "GBK";
        }
        bin.close();
        return code;
    }
	public static String toHex(byte[] byteArray) {
	     int i;
	     StringBuffer buf = new StringBuffer("");
	     int len = byteArray.length;
	     for (int offset = 0; offset < len; offset++) {
	         i = byteArray[offset];
	         if (i < 0)
	             i += 256;
	         if (i < 16)
	             buf.append("0");
	         buf.append(Integer.toHexString(i));
	     }
	     return buf.toString().toUpperCase();
	}
	/***********************读取txt文件中的知识end*****************************/
	
	/********************读取xml文件中的知识start******************************/
	private static final SAXReader READER = new SAXReader();
	private static final String XML_FILE_PATH = "d:"+File.separator+"1.xml";
	private Document getDocument(String filePath){
		try {
			return READER.read(new File(filePath));
		} catch (DocumentException e) {
			if(logger.isInfoEnabled()){
				logger.error("xml文件不存在-" + e.getMessage());
			}
			return null;
		}
	}
//	@PostConstruct
	public void init1(){
		Document document = getDocument(XML_FILE_PATH);
		if(document==null){
			logger.error("未进行初始化");
			return ;
		}
		List<Element> elements = document.selectNodes("/kpis/kpi");
		List<KnowledgeBo> knowledgeBos = new ArrayList<KnowledgeBo>();
		List<KnowledgeResolveBo> resolveBos = new ArrayList<KnowledgeResolveBo>();
		Long seqCode = 102993L;	//默认ID在10W至20W之间
		Long resolveCode = 100000L;
		for(Element element : elements){
			KnowledgeBo knowledge = new KnowledgeBo();
			knowledge.setId(seqCode++);
			String content = element.getTextTrim();
			String []contents = content.split("<br/>");
			for(int i=0, len=contents.length; i<len; i++){
				if(i==0){
					knowledge.setSourceContent(contents[i].replaceAll("<b>[^</b>]{1,}</b>", ""));
				}else{
					KnowledgeResolveBo resolve = new KnowledgeResolveBo();
					resolve.setId(resolveCode++);
					resolve.setIsScript(LocalKnowledgeConstants.RESOLVE_TYPE_WORD);
					resolve.setKnowledgeId(knowledge.getId());
					resolve.setResolveContent(contents[i].replaceAll("<b>[^</b>]{1,}</b>", ""));
					resolve.setResolveTitle("处理建议");
					resolveBos.add(resolve);
				}
			}
			knowledge.setIsCloudy(KnowledgeZipConstants.KNOWLEDGE_TYPE_LOCAL);
			knowledge.setCreateUserId(0L);
			knowledge.setCreateTime(new Date());
			knowledgeBos.add(knowledge);
		}
		
		if(knowledgeBos.size()==0){
			return ;
		}
		zipDao.saveLocalKnowledges(knowledgeBos);
		if(resolveBos.size()==0){
			return ;
		}
		zipDao.saveKnowledgeResolveBo(resolveBos);
		
	}
	/********************读取xml文件中的知识end******************************/
}
