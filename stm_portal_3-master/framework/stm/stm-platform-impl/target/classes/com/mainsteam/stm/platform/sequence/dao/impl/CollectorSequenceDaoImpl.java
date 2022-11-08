package com.mainsteam.stm.platform.sequence.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.platform.sequence.dao.ISequenceDao;
import com.mainsteam.stm.platform.sequence.po.SequencePo;

/**
 * <li>文件名称: SequenceDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年4月15日
 * @author   xiaoruqiang
 */
public class CollectorSequenceDaoImpl implements ISequenceDao{
	
	private static Map<String,SequencePo> allSeq = new HashMap<>();
	
	private static String path;
	
	private int step;
	private long min;
	private long max;
	
	public CollectorSequenceDaoImpl() {
	}

	private static final Log logger = LogFactory
			.getLog(CollectorSequenceDaoImpl.class);


	private static final String XML_AND_SEQ_DIRECTORY = "/config/seq/";
	
	private static final String XML_FILENAME = "stm_config_sequence.xml";
	
	private static final String SEQ_FILE_SUFFIX = ".seq";
	
	
	
	public void start() throws Exception{
		if (logger.isInfoEnabled()) {
			logger.info("load /config/seq/oc_config_sequence.xml start");
		}
	    Document document = null;
		InputStream inputStream = null;
		try {
			File file = new File(".");
			StringBuilder seqFilePath = new StringBuilder(100);
			seqFilePath.append(file.getCanonicalFile().getParentFile().getCanonicalPath());
			seqFilePath.append(XML_AND_SEQ_DIRECTORY);
			path = seqFilePath.toString();
			File collectorSeqAbsoluteFile = new File(path);
			if(collectorSeqAbsoluteFile.exists()){
				logger.info("Isolated mode is activated");
				//String seqPath = this.getClass().getResource("/seq/" + XML_FILENAME).getPath();
				//seq 最小值，最大值，缓存值
				inputStream = this.getClass().getResourceAsStream("/seq/" + XML_FILENAME);
				SAXReader reader = new SAXReader();
				document = reader.read(inputStream);
				Element root = document.getRootElement();
				Element minElement =  root.element("min");
				Element maxElement =  root.element("max");
				Element stepElement =  root.element("step");
				step = Integer.parseInt(stepElement.getTextTrim());
				min = Long.parseLong(minElement.getTextTrim());
				max = Long.parseLong(maxElement.getTextTrim());
				String[] seqs = collectorSeqAbsoluteFile.list();
			    for (String name : seqs) {
			    	if(!name.endsWith(SEQ_FILE_SUFFIX)){
			    		continue;
			    	}
				    File tempFile = new File(path + name);
				    String seqName = name.substring(0,name.indexOf(SEQ_FILE_SUFFIX));
				    BufferedReader br = new BufferedReader(new FileReader(tempFile));
				    try {
				    	String seqValue= br.readLine();
				    	SequencePo seqPo = new SequencePo();
				    	seqPo.setCacheCount(step);
				    	seqPo.setSeqName(seqName);
				    	seqPo.setCurVal(Long.valueOf(seqValue));
				    	allSeq.put(seqName, seqPo);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error(e.getMessage(), e);
						}
				    }finally{
				    	br.close();
				    }
			    }
			    if (logger.isInfoEnabled()) {
			    	StringBuilder info = new StringBuilder(1000);
			    	for(Entry<String,SequencePo> item : allSeq.entrySet()){
			    		info.append("load seqName=").append(item.getKey());
			    		info.append(" seqValue=").append(item.getValue().getCurVal()).append("\n");
				    }
			    	logger.info(info);
				}
			}else{
				logger.info("Normal mode is activated");
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("get initLoadResourceInstanceProp.xml error!", e);
			}
			throw e;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("start", e);
					}
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("load /config/seq/oc_config_sequence.xml end");
		}
	}
	
	@Override
	public SequencePo get(String seqName) {
		return allSeq.get(seqName);
	}

	@Override
	public List<SequencePo> getAll() {
		return new ArrayList<SequencePo>(allSeq.values());
	}

	@Override
	public int insert(String seqName) {
		int result = 0;
		String name  = seqName.trim();
		StringBuilder seqPath = new StringBuilder();
		seqPath.append(path).append(name).append(SEQ_FILE_SUFFIX);
		File collectorSeqFile = new File(seqPath.toString());
		if(!collectorSeqFile.exists()){
			result = writeSeqValueToFile(name,min);
			if(result == 1){
				SequencePo po = new SequencePo();
				po.setCacheCount(step);
				po.setSeqName(name);
				po.setCurVal(min);
				allSeq.put(name, po);
			}
		}else{
			result = 1;
		}
		return result;
	}

	@Override
	public int update(SequencePo seq) {
		if(seq.getCurVal().intValue() > max){
			throw new RuntimeException("update failed. seq more than max value =" + max);
		}
		int result = writeSeqValueToFile(seq.getSeqName(),seq.getCurVal());
		if(result == 1){
			//修改缓存值
			allSeq.put(seq.getSeqName(), seq);
		}
		return result;
	}
	
	private int writeSeqValueToFile(String seqName,Long seqValue){
		//修改seq文件内容
		StringBuilder seqPath = new StringBuilder();
		seqPath.append(path).append(seqName.trim()).append(SEQ_FILE_SUFFIX);
		File file = new File(seqPath.toString());
		if(!file.exists()){
			return 0;
		}
		PrintWriter pw = null;
		int result = 0;
		try {
			pw = new PrintWriter(new FileWriter(file),true);
			pw.println(seqValue);
			result = 1; 
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		}finally{
			if(pw != null){
				pw.close();
			}
		}
		return result;
	}
	
}


