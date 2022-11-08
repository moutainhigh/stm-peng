/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.manager.LocaleTableManager;
import com.mainsteam.stm.util.OSUtil;

/**
 * @author ziw
 * 
 */
public class LocaleTableManagerImpl implements LocaleTableManager {

	private static final String NODE_TABLE_CONFIG = "/node/nodeTables.xml";
	private static final String NODE_TABLE_CONFIG_BAK = "/node/nodeTablesRecover.data";

	private static final Log logger = LogFactory
			.getLog(LocaleTableManagerImpl.class);

	private Node currentNode;
	private NodeGroup currentGroup;
	private NodeTable nodeTable;
	private int currentNodeId = -1;

	/**
	 * 
	 */
	public LocaleTableManagerImpl() {
	}

	private File getConfigResource(String configFile) {
		URL url = this.getClass().getResource(configFile);
		if (url != null && url.getProtocol().equals("file")) {
			String path = url.getPath();
			try {
				path = URLDecoder.decode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
			return new File(path);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("getConfigResource is not exist.resourceURL="
						+ configFile);
			}
		}
		return null;
	}

	private static byte int3(int x) {
		return (byte) (x >> 24);
	}

	private static byte int2(int x) {
		return (byte) (x >> 16);
	}

	private static byte int1(int x) {
		return (byte) (x >> 8);
	}

	private static byte int0(int x) {
		return (byte) (x);
	}

	static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (((b3) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
	}

	private void backupNodeConfig(File in) {
		File backFile = getConfigResource(NODE_TABLE_CONFIG_BAK);
		if (backFile == null) {
			backFile = new File(in.getParentFile(),
					NODE_TABLE_CONFIG_BAK.substring(
							NODE_TABLE_CONFIG_BAK.lastIndexOf('/') + 1,
							NODE_TABLE_CONFIG_BAK.length()));
			try {
				backFile.createNewFile();
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("backupNodeConfig " + backFile, e);
				}
			}
		} else if (!backFile.exists()) {
			backFile.getParentFile().mkdirs();
			try {
				backFile.createNewFile();
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("backupNodeConfig " + backFile, e);
				}
			}
		}
		try {
			if (logger.isInfoEnabled()) {
				logger.info("backupNodeConfig from " + in + " to " + backFile);
			}
			byte[] fileContent = FileUtils.readFileToByteArray(in);
			byte[] encodedContent = Base64.encodeBase64(fileContent);
			byte[] sum = encryptEncode(encodedContent);
			fileContent = null;
			byte[] toWriteCotent = new byte[4 + encodedContent.length
					+ sum.length];
			int ll = encodedContent.length;
			toWriteCotent[0] = int3(ll);
			toWriteCotent[1] = int2(ll);
			toWriteCotent[2] = int1(ll);
			toWriteCotent[3] = int0(ll);
			System.arraycopy(encodedContent, 0, toWriteCotent, 4,
					encodedContent.length);
			System.arraycopy(sum, 0, toWriteCotent, 4 + encodedContent.length,
					sum.length);
			FileUtils.writeByteArrayToFile(backFile, toWriteCotent);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("backupNodeConfig", e);
			}
		}
	}

	private static byte[] encryptEncode(byte[] data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(data);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private boolean recoverConfigFile() {
		boolean result = false;
		File backFile = getConfigResource(NODE_TABLE_CONFIG_BAK);
		if (backFile == null) {
			if (logger.isErrorEnabled()) {
				logger.error("recoverConfigFile not exist."
						+ NODE_TABLE_CONFIG_BAK);
			}
		} else if (!backFile.exists()) {
			if (logger.isErrorEnabled()) {
				logger.error("recoverConfigFile not exist." + backFile);
			}
		} else {
			File originalFile = getConfigResource(NODE_TABLE_CONFIG);
			if (originalFile == null || !originalFile.exists()) {
				originalFile = new File(backFile.getParentFile(),
						NODE_TABLE_CONFIG.substring(
								NODE_TABLE_CONFIG.lastIndexOf('/') + 1,
								NODE_TABLE_CONFIG.length()));
				try {
					originalFile.createNewFile();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("backupNodeConfig " + backFile, e);
					}
				}
			}
			try {
				byte[] fileContent = FileUtils.readFileToByteArray(backFile);
				if (fileContent != null && fileContent.length > 4) {
					int ll = makeInt(fileContent[0], fileContent[1],
							fileContent[2], fileContent[3]);
					if (ll > 0 && ll < fileContent.length - 4) {
						byte[] originalContent = new byte[ll];
						System.arraycopy(fileContent, 4, originalContent, 0, ll);
						byte[] md5encode = encryptEncode(originalContent);
						byte[] fileTail = new byte[fileContent.length - ll - 4];
						System.arraycopy(fileContent, 4 + ll, fileTail, 0,
								fileTail.length);
						boolean unmodifyed = ArrayUtils.isEquals(md5encode,
								fileTail);
						if (unmodifyed) {
							byte[] decodedContent = Base64
									.decodeBase64(originalContent);
							FileUtils.writeByteArrayToFile(originalFile,
									decodedContent);
							result = true;
						} else {
							if (logger.isErrorEnabled()) {
								logger.error("recoverConfigFile recoverfile's content has been modify invalid.recoverfile="
										+ backFile
										+ " ll="
										+ ll
										+ " fileContent.length="
										+ fileContent.length);
							}
						}
					} else {
						if (logger.isErrorEnabled()) {
							logger.error("recoverConfigFile recoverfile content  format is not invalid.recoverfile="
									+ backFile);
						}
					}
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("recoverConfigFile recoverfile content is empty or format is not invalid.recoverfile="
								+ backFile
								+ " file.contentLength="
								+ (fileContent == null ? "null" : String
										.valueOf(fileContent.length)));
					}
				}
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("backupNodeConfig", e);
				}
			}
		}
		return result;
	}

	private void validBackupFile(File in) {
		File backFile = getConfigResource(NODE_TABLE_CONFIG_BAK);
		if (backFile == null) {
			if (logger.isErrorEnabled()) {
				logger.error("recoverConfigFile not exist."
						+ NODE_TABLE_CONFIG_BAK);
			}
			backupNodeConfig(in);
		} else if (!backFile.exists()) {
			if (logger.isErrorEnabled()) {
				logger.error("recoverConfigFile not exist." + backFile);
			}
			backupNodeConfig(in);
		}
		try {
			byte[] fileContent = null;
			if(backFile!=null && backFile.exists()){
				fileContent = FileUtils.readFileToByteArray(backFile);
			}
			if (fileContent != null && fileContent.length > 4) {
				int ll = makeInt(fileContent[0], fileContent[1],
						fileContent[2], fileContent[3]);
				if (ll > 0 && ll < fileContent.length - 4) {
					byte[] originalContent = new byte[ll];
					System.arraycopy(fileContent, 4, originalContent, 0, ll);
					byte[] md5encode = encryptEncode(originalContent);
					byte[] fileTail = new byte[fileContent.length - ll - 4];
					System.arraycopy(fileContent, 4 + ll, fileTail, 0,
							fileTail.length);
					boolean unmodifyed = ArrayUtils.isEquals(md5encode,
							fileTail);
					if (unmodifyed == false) {
						if (logger.isWarnEnabled()) {
							logger.error("recoverfile's content has been modify invalid.recoverfile="
									+ backFile
									+ " ll="
									+ ll
									+ " fileContent.length="
									+ fileContent.length);
						}
						backupNodeConfig(in);
					}
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("recoverConfigFile recoverfile content is format is not invalid,backup it again.recoverfile="
								+ backFile
								+ " file.contentLength="
								+ (fileContent == null ? "null" : String
										.valueOf(fileContent.length)));
					}
					backupNodeConfig(in);
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("recoverConfigFile recoverfile content is empty or format is not invalid,backup it again.recoverfile="
							+ backFile
							+ " file.contentLength="
							+ (fileContent == null ? "null" : String
									.valueOf(fileContent.length)));
				}
				backupNodeConfig(in);
			}
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("backupNodeConfig", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean loadTableConfig(File in) throws NodeException, IOException {
		String fileContent = FileUtils.readFileToString(in, "UTF-8");
		if (StringUtils.isEmpty(fileContent)) {
			return false;
		}
		fileContent = fileContent.trim();
		if (StringUtils.isEmpty(fileContent)) {
			return false;
		}
		if (logger.isInfoEnabled()) {
			logger.info("loadTableConfig fileContent=" + fileContent);
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(fileContent);
			// doc = reader.read(fileContent);
		} catch (DocumentException e) {
			if (logger.isErrorEnabled()) {
				logger.error("start", e);
			}
			throw new NodeException(e);
		}
		/**
		 * 解析当前节点id
		 */
		Element rootElement = doc.getRootElement();
		Element currentNode = rootElement.element("CurrentNode");
		if (currentNode != null) {
			String nodeIdValue = currentNode.attributeValue("nodeId");
			if (nodeIdValue != null && NumberUtils.isNumber(nodeIdValue)) {
				this.currentNodeId = NumberUtils.toInt(nodeIdValue);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("loadTableConfig currentNodeId=" + this.currentNodeId);
		}
		long updateTime = -1;
		Element updateTimeElement = rootElement.element("UpdateTime");
		if (updateTimeElement != null) {
			String timeValue = updateTimeElement.getTextTrim();
			if (timeValue != null && NumberUtils.isNumber(timeValue)) {
				updateTime = NumberUtils.toLong(timeValue);
			}
		}

		Map<Integer, NodeGroup> nodeGroup = new HashMap<Integer, NodeGroup>();
		Map<Integer, Integer> parentIdGroup = new HashMap<>();
		List<Element> nodeList = rootElement.selectNodes("Groups/Group");
		if (nodeList != null && nodeList.size() > 0) {
			for (Element node : nodeList) {
				org.dom4j.Node level = node.selectSingleNode("Level");
				String levelValue = level.getText();
				int levelInt = toInt(levelValue);

				org.dom4j.Node func = node.selectSingleNode("Func");
				String funcValue = func.getText();
				NodeFunc f = NodeFunc.valueOf(funcValue);

				String groupId = node.attributeValue("id");
				int groupIdInt = toInt(groupId);

				String groupParentId = node.attributeValue("pre");
				int groupParentIdInt = toInt(groupParentId);

				String nodeName = node.attributeValue("name");
				NodeGroup g = new NodeGroup();
				g.setFunc(f);
				g.setId(groupIdInt);
				g.setName(nodeName);
				g.setNodeLevel(levelInt);

				nodeGroup.put(groupIdInt, g);
				if (groupParentIdInt >= 0) {
					parentIdGroup.put(groupIdInt, groupParentIdInt);
				}
			}
			for (Integer groupId : parentIdGroup.keySet()) {
				NodeGroup child = nodeGroup.get(groupId);
				NodeGroup parent = nodeGroup.get(parentIdGroup.get(groupId));
				if (parent != null) {
					child.setPre(parent);
					if (parent.getNextGroups() == null) {
						parent.setNextGroups(new ArrayList<NodeGroup>());
					}
					parent.getNextGroups().add(child);
				}
			}
		}
		NodeGroup currentNodeGroupTmp = null;
		Node currentNodeTmp = null;
		nodeList = rootElement.selectNodes("Nodes/Node");
		if (nodeList != null && nodeList.size() > 0) {
			for (Element node : nodeList) {
				String idValue = node.selectSingleNode("Id").getText().trim();
				int idIntValue = toInt(idValue);
				String name = node.selectSingleNode("Name").getText().trim();

				String ip = node.selectSingleNode("Ip").getText().trim();
				String portValue = node.selectSingleNode("Port").getText()
						.trim();
				int portIntValue = toInt(portValue);
				String priority = node.selectSingleNode("Priority").getText()
						.trim();
				int priorityIntValue = toInt(priority);

				String funcValue = node.selectSingleNode("Func").getText()
						.trim();
				NodeFunc func = NodeFunc.valueOf(funcValue);

				String groupIdValue = node.selectSingleNode("GroupId")
						.getText().trim();
				Integer groupIdIntValue = toInt(groupIdValue);
				if (nodeGroup.containsKey(groupIdIntValue)) {
					NodeGroup group = nodeGroup.get(groupIdIntValue);
					if (group.getNodes() == null) {
						group.setNodes(new ArrayList<Node>());
					}
					Node n = new Node();
					n.setFunc(func);
					n.setId(idIntValue);
					n.setName(name);
					n.setGroupId(groupIdIntValue.intValue());
					n.setIp(ip);
					n.setPort(portIntValue);
					n.setPriority(priorityIntValue);
					group.getNodes().add(n);
					if (this.currentNodeId == idIntValue) {
						currentNodeTmp = n;
						currentNodeGroupTmp = group;
					}
				}
			}
		}
		List<NodeGroup> groups = new ArrayList<>(nodeGroup.values());
		NodeTable t = new NodeTable();
		if (updateTime > 0) {
			t.setUpdateTime(new Date(updateTime));
		}
		t.setGroups(groups);
		this.nodeTable = t;
		this.currentNode = currentNodeTmp;
		this.currentGroup = currentNodeGroupTmp;
		if (logger.isInfoEnabled()) {
			logger.info("loadTableConfig this.currentGroup="
					+ JSON.toJSONString(this.currentGroup) + " currentNode="
					+ this.currentNode);
		}
		return true;
	}

	public void start() throws NodeException {
		if (logger.isInfoEnabled()) {
			logger.info("start load node table.");
		}
		File configFile = getConfigResource(NODE_TABLE_CONFIG);
		boolean recoverConfigFile = false;
		if (configFile == null) {
			recoverConfigFile();
			recoverConfigFile = true;
			configFile = getConfigResource(NODE_TABLE_CONFIG);
		}
		if (configFile != null && configFile.exists() && configFile.isFile()) {
			if (logger.isInfoEnabled()) {
				logger.info("load configFile=" + configFile.getAbsolutePath());
			}
			try {
				boolean result = loadTableConfig(configFile);
				if (result == false && recoverConfigFile == false) {
					if (recoverConfigFile()) {
						configFile = getConfigResource(NODE_TABLE_CONFIG);
						if (configFile != null && configFile.exists()
								&& configFile.isFile()) {
							result = loadTableConfig(configFile);
							if (result) {
								validBackupFile(configFile);
							}
						}
					}
				} else if(result) {
					validBackupFile(configFile);
				}
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("start", e);
				}
				throw new NodeException(e);
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("start not found file of " + NODE_TABLE_CONFIG
						+ " at " + configFile);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("load node table end");
		}
	}

	private int toInt(String value) {
		if (value != null && NumberUtils.isNumber(value)) {
			return NumberUtils.toInt(value);
		}
		return -1;
	}

	public synchronized void saveNodeTable(Node currentNode, NodeTable table)
			throws NodeException {
		write(createDocument(currentNode, table));
		this.currentNode = currentNode;
		this.nodeTable = table;
		List<NodeGroup> groups = table.getGroups();
		if (groups != null && groups.size() > 0) {
			for (NodeGroup nodeGroup : groups) {
				if (this.currentNode.getGroupId() == nodeGroup.getId()) {
					this.currentGroup = nodeGroup;
					if (logger.isInfoEnabled()) {
						logger.info("saveNodeTable this.currentGroup="
								+ JSON.toJSONString(nodeGroup)
								+ " this.currentNode"
								+ JSON.toJSONString(this.currentNode));
					}
					break;
				}
			}
		}
	}

	private Document createDocument(Node currentNode, NodeTable table) {
		DocumentFactory factory = DocumentFactory.getInstance();
		Document d = factory.createDocument("UTF-8");
		Element root = factory.createElement("NodeTable");
		Element current = factory.createElement("CurrentNode");
		Attribute t = factory.createAttribute(current, "nodeId",
				String.valueOf(currentNode.getId()));
		current.add(t);
		root.add(current);
		Element updateTimeElement = factory.createElement("UpdateTime");
		updateTimeElement
				.setText(String.valueOf(table.getUpdateTime() == null ? ""
						: table.getUpdateTime().getTime()));
		root.add(updateTimeElement);

		Element groupsElement = factory.createElement("Groups");
		Element nodes = factory.createElement("Nodes");

		List<NodeGroup> groups = table.getGroups();
		if (groups != null && groups.size() > 0) {
			for (NodeGroup nodeGroup : groups) {
				Element groupNode = factory.createElement("Group");
				Attribute id = factory.createAttribute(groupNode, "id",
						String.valueOf(nodeGroup.getId()));
				groupNode.add(id);
				Attribute name = factory.createAttribute(groupNode, "name",
						String.valueOf(nodeGroup.getName()));
				groupNode.add(name);
				if (nodeGroup.getPre() != null) {
					Attribute parentGroupId = factory.createAttribute(
							groupNode, "pre",
							String.valueOf(nodeGroup.getPre().getId()));
					groupNode.add(parentGroupId);
				}

				Element level = createTextElement(factory, "Level",
						String.valueOf(nodeGroup.getNodeLevel()));
				groupNode.add(level);
				Element func = createTextElement(factory, "Func", nodeGroup
						.getFunc().name());
				groupNode.add(func);

				groupsElement.add(groupNode);

				List<Node> nodeList = nodeGroup.getNodes();
				if (nodeList != null && nodeList.size() > 0) {
					for (Node node : nodeList) {
						Element nodeNode = factory.createElement("Node");
						nodeNode.add(createTextElement(factory, "Id",
								String.valueOf(node.getId())));
						nodeNode.add(createTextElement(factory, "Name",
								node.getName()));
						nodeNode.add(createTextElement(factory, "Ip",
								node.getIp()));
						nodeNode.add(createTextElement(factory, "Func", node
								.getFunc().name()));
						nodeNode.add(createTextElement(factory, "Port",
								String.valueOf(node.getPort())));
						nodeNode.add(createTextElement(factory, "GroupId",
								String.valueOf(node.getGroupId())));
						nodeNode.add(createTextElement(factory, "Priority",
								String.valueOf(node.getPriority())));

						nodes.add(nodeNode);
					}
				}
			}
		}

		root.add(groupsElement);
		root.add(nodes);

		d.add(root);
		if (logger.isInfoEnabled()) {
			logger.info("createDocument d=" + d.asXML());
		}
		return d;
	}

	private void write(Document doc) throws NodeException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");// 设置XML文件的编码格式
		File f = getConfigResource(NODE_TABLE_CONFIG);
		if (f == null) {
			String parent = OSUtil.getEnv("CLASS_PATH");
			if (parent == null) {
				f = new File(NODE_TABLE_CONFIG);
			} else {
				f = new File(parent, NODE_TABLE_CONFIG);
			}
		}
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
				if (logger.isInfoEnabled()) {
					logger.info("make node config file.=" + f.getAbsolutePath());
				}
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("write", e);
				}
				throw new NodeException(f.getAbsolutePath(), e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("write udpate node tables at " + f.getAbsolutePath());
		}
		BufferedOutputStream out = null;
		XMLWriter writer;
		try {
			out = new BufferedOutputStream(new FileOutputStream(f));
			writer = new XMLWriter(out, format);
			writer.write(doc);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("write", e);
			}
			throw new NodeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("write close file.", e);
					}
				}
				backupNodeConfig(f);
			}
		}

	}

	private Element createTextElement(DocumentFactory factory, String nodeName,
			String text) {
		Element e = factory.createElement(nodeName);
		e.setText(text);
		return e;
	}

	public Node getCurrentNode() {
		return this.currentNode;
	}

	public NodeTable getNodeTable() {
		return this.nodeTable;
	}

	/**
	 * @return the currentGroup
	 */
	public final NodeGroup getCurrentGroup() {
		return currentGroup;
	}

	@Override
	public int getCurrentNodeId() {
		return this.currentNodeId;
	}
}
