package com.mainsteam.stm.knowledge.capacity.service.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.deploy.FileSenderService;
import com.mainsteam.stm.deploy.obj.DeployRecord;
import com.mainsteam.stm.knowledge.capacity.api.ICapacityKnowledgeApi;
import com.mainsteam.stm.knowledge.capacity.bo.CapacityKnowledgeBo;
import com.mainsteam.stm.knowledge.capacity.bo.DeployResultBo;
import com.mainsteam.stm.knowledge.capacity.dao.ICapacityKnowledgeDao;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.util.DateUtil;

@Service("capacityKnowlwdgeApi")
public class CapacityKnowledgeImpl implements ICapacityKnowledgeApi {

	@Resource(name = "capacityKnowledgeDao")
	private ICapacityKnowledgeDao capacityKnowledgeDao;
	
	@Resource(name="fileSenderService")
	private FileSenderService fileSenderService;
	
	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private IFileClientApi fileClient;
	
	@Autowired
	private ISystemConfigApi systemConfigApi;

	private ISequence sequence;
	@Autowired
	public CapacityKnowledgeImpl(SequenceFactory sequenceFactory){
		sequence = sequenceFactory.getSeq(SEQ.SEQNAME_STM_KNOWLEDGE_CAPACITY);
	}
	
	@Override
	public CapacityKnowledgeBo deploy(CapacityKnowledgeBo capacityKnowledgeBo) {
		try {
			if(capacityKnowledgeBo!=null && capacityKnowledgeBo.getFileId()!=null){
				capacityKnowledgeBo.setId(sequence.next());
				FileModel file = fileClient.getFileModelByID(capacityKnowledgeBo.getFileId());
				capacityKnowledgeBo.setName(file.getFileName());
				capacityKnowledgeBo.setDeployTime(DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss"));
				fileSenderService.sendFile(file.getFilePath(), file.getFileName(), String.valueOf(capacityKnowledgeBo.getId()));
				capacityKnowledgeDao.insert(capacityKnowledgeBo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return capacityKnowledgeBo;
	}


	@Override
	public CapacityKnowledgeBo getCapacityKnowledgeById(Long id){
		CapacityKnowledgeBo capacityKnowledgeBo = null;
		if(null!=id)
			capacityKnowledgeBo = capacityKnowledgeDao.get(id);
		return capacityKnowledgeBo;
	}
	
	@Override
	public List<DeployResultBo> getDeployResults(Long capacityKnowledgeId) {
		List<DeployResultBo> deployResultBos = new ArrayList<DeployResultBo>();
		try {
			//获取所有节点
			NodeTable nodeTable = nodeService.getNodeTable();
			List<NodeGroup> groupList = nodeTable.getGroups();
			DeployResultBo deployResultBo = null;
			for (NodeGroup group : groupList) {
				List<Node> nodes = group.getNodes();
				for (Node node : nodes) {
					boolean result = this.getNodeDeployRescord(String.valueOf(capacityKnowledgeId), node.getId());
					deployResultBo = new DeployResultBo();
					deployResultBo.setId(node.getId());
					deployResultBo.setName(node.getFunc().name());
					deployResultBo.setStatus(result);
					deployResultBos.add(deployResultBo);
				}
			}
		} catch (NodeException e) {
			e.printStackTrace();
		}
		return deployResultBos;
	}
	
	/**
	* @Title: getNodeDeployRescord
	* @Description: 通过节点ID，和能力知识ID获取节点是否部署成功
	* @param knowledgeId
	* @param nodeId
	* @return  boolean
	* @throws
	*/
	private boolean getNodeDeployRescord(String knowledgeId,int nodeId){
		List<DeployRecord> deployRecords = fileSenderService.getDeployRecordBySourceID(knowledgeId);
		for (DeployRecord deployRecord : deployRecords) {
			if(deployRecord.getNodeID()==nodeId){
				return deployRecord.getResult();
			}
		}
		return false;
	}
	

	@Override
	public boolean reDeploy(CapacityKnowledgeBo capacityKnowledgeBo) {
		try {
			List<DeployResultBo> resultBos = this.getDeployResults(capacityKnowledgeBo.getId());
			capacityKnowledgeBo = this.getCapacityKnowledgeById(capacityKnowledgeBo.getId());
			FileModel file = fileClient.getFileModelByID(capacityKnowledgeBo.getFileId());
			for (DeployResultBo deployResultBo : resultBos) {
				if (!deployResultBo.getStatus()) {
					Node node = nodeService.getNodeById(deployResultBo.getId());
					fileSenderService.sendFile(file.getFilePath(), file.getFileName(), node, String.valueOf(capacityKnowledgeBo.getId()));
				}
			}
			capacityKnowledgeBo.setDeployTime(DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss"));
			capacityKnowledgeDao.update(capacityKnowledgeBo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<CapacityKnowledgeBo> query(Page<CapacityKnowledgeBo, CapacityKnowledgeBo> page) {
		return capacityKnowledgeDao.pageSelect(page);
	}

	@Override
	public String getCapacityKnowledgeDownloadAddr() {
		SystemConfigBo config = systemConfigApi.getSystemConfigById(SystemConfigConstantEnum.SYSTEM_CONFIG_CAPACITY_KNOWLEDGE_DOWNLOAD_ADDR.getCfgId());
		return config.getContent()==null?"":config.getContent();
	}
	
	

}
