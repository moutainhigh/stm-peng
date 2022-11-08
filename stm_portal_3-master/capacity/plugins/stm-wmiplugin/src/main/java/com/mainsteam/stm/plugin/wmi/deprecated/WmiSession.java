package com.mainsteam.stm.plugin.wmi.deprecated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * WMI采集插件，由我们的插件通过socket通道向另一台装有wmi代理的机器发送wql语句，wmi会开启一个监听端口（12345），当它监听到
 * 采集请求时会根据我们的命令去采集，然后再通过socket通道返回回来。
 * 
 * @author xiaopf
 * 
 */
public class WmiSession implements PluginSession {

	private static final String INDEX = "INDEX";

	private static final String COMMAND = "COMMAND";

	public static final String WMIPLUGIN_PASSWORD = "password";

	public static final String WMIPLUGIN_USERNAME = "username";

	public static final String WMIPLUGIN_IP = "IP";
	// 用这条WQL测试WMI是否可连接
	private static final String TEST_CONNECT = "root\\cimv2::select Name from Win32_ComputerSystem";

	private static final Log logger = LogFactory.getLog(WmiSession.class);
	/**
	 * 目标机器ip
	 */
	private String ip;
	/**
	 * 登录用户名
	 */
	private String username;
	/**
	 * 登录密码
	 */
	private String password;

	private boolean isAlive;

	public WmiSession() {

	}

	public WmiSession(String ip, String username, String password) {
		this.ip = ip;
		this.username = username;
		this.password = password;
	}

	@Override
	public void destory() {
	}

	@Override
	public PluginResultSet execute(PluginExecutorParameter<?> executorParam,
			PluginSessionContext arg1) throws PluginSessionRunException {

		if (executorParam instanceof PluginArrayExecutorParameter) {

			PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParam;
			Parameter[] parameters = arrayP.getParameters();
			List<String> wmiCmd = new ArrayList<String>(2); // 命令集合
			List<String> indexColumns = new ArrayList<String>(2);// 索引列集合，需要根据两个结果集中共同的列名合并成一个结果集
			Map<String, String> params = new HashMap<String, String>(2, 0.5f); // 参数列表
			for (Parameter parameter : parameters) {
				if (parameter.getKey().equalsIgnoreCase(COMMAND)) {
					wmiCmd.add(parameter.getValue());
				} else if (parameter.getKey().equalsIgnoreCase(INDEX)) {
					indexColumns.add(parameter.getValue());
				} else {
					params.put(parameter.getKey(), parameter.getValue());
				}
			}
			if (wmiCmd.isEmpty()) {
				throw new PluginSessionRunException(
						CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLCOMMAND,
						"没有wmi采集命令");
			}
			PluginResultSet resultSet = new PluginResultSet();
			List<ArrayList<ArrayList<ResponseCell>>> mutilDatas = new ArrayList<ArrayList<ArrayList<ResponseCell>>>(
					2);
			for (String cmd : wmiCmd) {
				if (!params.isEmpty()) { // 参数替换
					Set<String> keySet = params.keySet();
					Iterator<String> iterator = keySet.iterator();
					while (iterator.hasNext()) {
						String key = iterator.next();
						cmd = StringUtils.replace(cmd, "${" + key + "}",
								params.get(key));
					}
				}
				try {
					ArrayList<ArrayList<ResponseCell>> result = executeCmd(cmd);
					if (indexColumns.size() == 2) {
						mutilDatas.add(result);
					} else {
						this.putValues(result, resultSet);
					}
				} catch (Exception e) {
					if (logger.isWarnEnabled()) {
						logger.warn("WMI COLLECT ERROR:" + e.getMessage(), e);
					}
				}
			}
			if (mutilDatas.size() == 2 && indexColumns.size() == 2) { // 只能在两个结果集中分别拥有相同列进行合并
				this.mergeResultSet(mutilDatas, resultSet, indexColumns);
			}
			if (logger.isInfoEnabled()) {
				logger.info("WMI_CMD:" + wmiCmd.toString());
				if (null != resultSet) {
					logger.info("return size:" + resultSet.getRowLength() + ","
							+ resultSet.getColumnLength());
				}
			}
			return resultSet;
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("EMPTY WMI COLLECT PARAMETER" + this.ip + "/"
						+ this.username);
			}
			return null;
		}
	}

	private ArrayList<ArrayList<ResponseCell>> executeCmd(String wmiCmd)
			throws Exception {
		try {
			WmiFromAgentProtocol wmi = WmiFromAgentProtocol
					.getDefaultInstance();
			RequestMessage message = new RequestMessage();
			message.setWmi_domain_user(this.username);
			message.setWmi_namespace(this.ip);
			message.setWmi_password(this.password);
			message.setWmi_query_cmd(wmiCmd);
			ArrayList<ArrayList<ResponseCell>> result = wmi
					.queryFormat(message);

			return result;

		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	public void init(PluginInitParameter init) throws PluginSessionRunException {

		Parameter[] initParameters = init.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case WMIPLUGIN_IP:
				this.ip = initParameters[i].getValue();
				break;
			case WMIPLUGIN_USERNAME:
				this.username = initParameters[i].getValue();
				break;
			case WMIPLUGIN_PASSWORD:
				this.password = initParameters[i].getValue();
				break;

			default:
				if (logger.isWarnEnabled()) {
					logger.warn("warn:unkown initparameter "
							+ initParameters[i].getKey() + "="
							+ initParameters[i].getValue());
				}
				break;
			}
		}
		try {
			executeCmd(TEST_CONNECT);
			this.isAlive = true;
		} catch (Exception e) {
			this.isAlive = false;
			logger.error("wmi插件初始化失败，" + e.getMessage(), e);
			int errorCode = CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED;
			String errorMessage = e.getMessage();
			if (StringUtils.equals("Unable to connect Wmi Agent",
					e.getMessage())) {
				errorCode = CapcityErrorCodeConstant.ERR_CAPCITY_WMI_AGENT_FAILED;
				errorMessage = "连接WMI Agent失败，请确保WMI Agent服务已启动，以及连接WMI Agent的IP和端口的正确性";
			} else if (StringUtils.equals("Query timeout!", e.getMessage())) {
				errorCode = CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_METRIC_TIMEOUT;
				errorMessage = "WMI查询超时";

			}
			throw new PluginSessionRunException(errorCode, errorMessage, e);
		}

	}

	@Override
	public boolean isAlive() {
		return this.isAlive;
	}

	@Override
	public void reload() {

	}

	/**
	 * 分行,结果处理
	 * 
	 * @param result
	 *            执行命令后的返回值
	 * @param ret
	 *            返回ResultSet
	 */
	private PluginResultSet putValues(ArrayList<ArrayList<ResponseCell>> data,
			PluginResultSet resultSet) {
		// 查询出最长的一行
		int maxlength = 0;
		if (data != null && data.size() > 0) {
			for (List<ResponseCell> row : data) {
				if (row.size() > maxlength) {
					maxlength = row.size();
				}
			}
		}
		int columnCount = resultSet.getColumnLength();
		if (maxlength == 0 && columnCount == 0) {
			resultSet.putValue(0, 0, null);
		}
		if (null != data) {
			for (int i = 0; i < data.size(); i++) {
				try {
					ArrayList<ResponseCell> childs = data.get(i);
					for (int j = 0; j < maxlength; j++) {
						if (j > childs.size())
							resultSet.putValue(i, j + columnCount, null);
						else {
							ResponseCell cell = childs.get(j);
							if (cell != null) {
								List<String> finalValue = cell
										.getWmi_res_value();
								try {
									String str = finalValue.toString();
									str = str.substring(1, str.length() - 1);
									resultSet.putValue(i, j + columnCount, str);
								} catch (Exception e) {
									resultSet
											.putValue(i, j + columnCount, null);
								}
							} else {
								resultSet.putValue(i, j + columnCount, null);
							}
						}

					}
				} catch (Exception e) {
					if (logger.isWarnEnabled())
						logger.warn("设置wmi结果集错误" + e.getMessage(), e);
					continue;
				}
			}

		}
		return resultSet;
	}

	/**
	 * 根据两个结果集中两个相同的列进行合并
	 */
	private void mergeResultSet(
			List<ArrayList<ArrayList<ResponseCell>>> result,
			PluginResultSet resultSet, List<String> columns) {

		ArrayList<ArrayList<ResponseCell>> firstResult = result.get(0);
		ArrayList<ArrayList<ResponseCell>> secondResult = result.get(1);

		String firstCol = columns.get(0); // 第一个结果集中相同列
		String secondCol = columns.get(1); // 第二个结果集中相同列

		for (ArrayList<ResponseCell> firstRows : firstResult) {
			boolean flag = false;
			// 一行数据
			// 保存匹配到相同行的行号
			int commonRow = 0; // 第二个结果集中相同列的行数
			for (ResponseCell cell : firstRows) {
				String column = cell.getWmi_res_key();
				List<String> strs = cell.getWmi_res_value();
				if (strs == null)
					continue;
				if (StringUtils.equalsIgnoreCase(firstCol, column)) { // 在第一个结果集中遍历到相同列名
					// 接下来去第二个结果集中遍历出相同的列，然后比较值，如果相等，则说明属于同一行的数据
					for (ArrayList<ResponseCell> secondRows : secondResult) {
						for (ResponseCell cell2 : secondRows) {
							String otherColumn = cell2.getWmi_res_key();
							if (StringUtils.equalsIgnoreCase(otherColumn,
									secondCol)) { // 在第二个结果集中找到相同的列
								List<String> otherStrs = cell2
										.getWmi_res_value();
								if (otherStrs == null)
									break;
								if (StringUtils.equals(strs.toString(),
										otherStrs.toString())) { // 两列值相同
									flag = true;
									break;
								}
							}
						}
						if (flag) {
							break;
						}
						commonRow++;
					}

				}
				if (flag)
					break;

			}

			if (flag) { // 先添加第一个结果集，然后添加另外一个数据集匹配的行数
				try {

					int currentRow = resultSet.getRowLength();
					int currentColumn = 0;
					for (ResponseCell cell : firstRows) {
						List<String> strs = cell.getWmi_res_value();
						String str = strs.toString();
						str = str.substring(1, str.length() - 1);
						resultSet.putValue(currentRow, currentColumn, str);
						currentColumn++;
					}

					ArrayList<ResponseCell> otherCells = secondResult
							.get(commonRow);
					for (ResponseCell cell : otherCells) {
						List<String> strs = cell.getWmi_res_value();
						String str = strs.toString();
						str = str.substring(1, str.length() - 1);
						resultSet.putValue(currentRow, currentColumn, str);
						currentColumn++;
					}
				} catch (Exception e) {
					if (logger.isWarnEnabled()) {
						logger.warn("合并wmi结果集失败，" + e.getMessage(), e);
					}
					continue;
				}
			}

		}

	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return false;
	}
}
