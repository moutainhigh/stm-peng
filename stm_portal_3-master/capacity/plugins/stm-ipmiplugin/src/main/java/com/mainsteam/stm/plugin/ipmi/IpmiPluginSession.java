package com.mainsteam.stm.plugin.ipmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.mainsteam.stm.util.OSUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class IpmiPluginSession implements PluginSession {

    private static final Log logger = LogFactory.getLog(IpmiPluginSession.class);

    private static final String IPMI_IP = "IP";
    private static final String IPMI_PORT = "port";
    private static final String IPMI_LEVEL = "level";
    private static final String IPMI_AUTHTYPE = "authtype";
    private static final String IPMI_USERNAME = "username";
    private static final String IPMI_PASSWORD = "password";
    private static final String IPMI_KG = "KGValue";
    private static final String IPMI_COMMAND = "command";
    private static final String IPMI_INTF = "interface";
    private static final String IPMI_TOOL = "tool";
    private static final String TOOLS = "tools";
    private static final String AUTHTYPE_NONE = "NONE";
    private static final String AUTHTYPE_PASSWORD = "PASSWORD";
    private static final String CAPLIB_PATH = "caplibs.path";
    private static final String GET_SERVICE_PORT = "Get_ServicePort";
    private static final String OS_NAME_LINUX = "linux";
    private static final String OS_NAME_WINDOWS = "windows";

    private static final String IPMITOOL = "ipmitool";
    private static final String IPMISH = "ipmish";

    private String ip, level, authtype, username, password, kg, tool, command, intf;
    private int port = 623; //默认端口

    @Override
    public void init(PluginInitParameter initParameters) throws PluginSessionRunException {
        Parameter[] parameters = initParameters.getParameters();
        for (Parameter parameter : parameters) {
            switch (parameter.getKey()) {
                case IPMI_IP:
                    ip = StringUtils.trimToEmpty(parameter.getValue());
                    break;
                case IPMI_PORT:
                    port = Integer.valueOf(parameter.getValue());
                case IPMI_USERNAME:
                    username = StringUtils.trimToEmpty(parameter.getValue());
                    break;
                case IPMI_PASSWORD:
                    password = StringUtils.trimToEmpty(parameter.getValue());
                    break;
                case IPMI_LEVEL:
                    level = StringUtils.trimToEmpty(parameter.getValue());
                    break;
                case IPMI_AUTHTYPE:
                    authtype = StringUtils.trimToEmpty(parameter.getValue());
                    break;
                case IPMI_KG:
                    kg = StringUtils.trimToEmpty(parameter.getValue());
                    break;
                case IPMI_INTF:
                    intf = StringUtils.trimToEmpty(parameter.getValue());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean check(PluginInitParameter initParameters) throws PluginSessionRunException {
        return false;
    }

    @Override
    public void destory() {
    }

    @Override
    public void reload() {
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
        if (executorParameter instanceof PluginArrayExecutorParameter) {
            Parameter[] parameters = ((PluginArrayExecutorParameter) executorParameter).getParameters();
            Map<String, String> paramMap = new HashMap<String, String>(2, 0.5f);
            for (Parameter parameter : parameters) {
                switch (parameter.getKey()) {
                    case IPMI_COMMAND:
                        command = parameter.getValue();
                        break;
                    case IPMI_TOOL:
                        tool = parameter.getValue();
                        break;
                    default:
                        paramMap.put(parameter.getKey(), parameter.getValue());
                        break;
                }
            }
            Set<String> keySet = paramMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                command = StringUtils.replace(command, "${" + key + "}", paramMap.get(key));
            }
        }
        PluginResultSet resultSet = new PluginResultSet();
        if (StringUtils.equals(GET_SERVICE_PORT, command)) { //获取服务端口
            resultSet.putValue(0, 0, this.port + "");
            return resultSet;
        }

        String commandLine = OSUtil.getEnv(CAPLIB_PATH) + File.separator + TOOLS + File.separator + tool;

        switch (tool) {
            case IPMITOOL:
                String osName = System.getProperty("os.name").toLowerCase();
                if (StringUtils.contains(osName, OS_NAME_LINUX)) {
                    commandLine += File.separator + OS_NAME_LINUX;
                } else if (StringUtils.contains(osName, OS_NAME_WINDOWS))
                    commandLine += File.separator + OS_NAME_WINDOWS;
                commandLine += File.separator + tool;

                if (StringUtils.equalsIgnoreCase(authtype, AUTHTYPE_NONE)) {
                    commandLine += " -H " + ip + " -p " + port + " -L " + level + " -U " + username;
                } else {
                    commandLine += " -H " + ip + " -p " + port + " -L " + level + " -U " + username + " -P " + password;
                }
//			}else if(StringUtils.equalsIgnoreCase(authtype, AUTHTYPE_PASSWORD)) {
//				commandLine += " -H " + ip + " -p " + port + " -L " + level + " -U " + username + " -P " + password;
//
//			}else
//				commandLine += " -H " + ip + " -p " + port + " -L " + level + " -A " + authtype + " -U " + username + " -P " + password + " -k " + kg;

//			if(StringUtils.contains(osName, OS_NAME_WINDOWS)){
                commandLine += " -I " + intf;
//			}

                break;
            case IPMISH:
                commandLine += File.separator + tool + " -ip " + ip + " -u " + username + " -p " + password + " -k " + kg;
            default:
                break;
        }
        commandLine += " " + command;

        String result = executeCommand(commandLine);

        if (logger.isInfoEnabled()) {
//			if(StringUtils.isNotBlank(password)){
//				commandLine = StringUtils.replace(commandLine, " -P " + password, " -P nothing");
//			}
            logger.info("IPMI Result:" + result + ". Command:" + commandLine);
        }

        resultSet.putValue(0, 0, result);
        return resultSet;
    }

    private String executeCommand(String command) {
        Process process = null;
        ProcessBuilder processBuilder = null;
        InputStream input = null;
        StringBuffer result = null;
        
        StringTokenizer localStringTokenizer = new StringTokenizer(command);
	    String[] arrayOfString = new String[localStringTokenizer.countTokens()];
	    for (int i = 0; localStringTokenizer.hasMoreTokens(); i++) {
	      arrayOfString[i] = localStringTokenizer.nextToken();
	    }
        try {
//            process = Runtime.getRuntime().exec(command);
        	processBuilder = new ProcessBuilder(arrayOfString);
        	processBuilder.redirectErrorStream(true);
        	process = processBuilder.start();
            input = process.getInputStream();
            if (input == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line = null;
            do {
                if (line != null) {
                    if (result == null) {
                        result = new StringBuffer();
                    }
                    result.append(line + "\r\n");
                }
                line = reader.readLine();
            } while (line != null);

            if (result != null) {
                return result.toString();
            }
            return null;
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                if (StringUtils.isNotBlank(password)) {
                    command = StringUtils.replace(command, " -P " + password, " -P nothing");
                }
                logger.error("IPMI IO Error. Command is " + command + ". Error Message:" + e.getMessage(), e);
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                if (StringUtils.isNotBlank(password)) {
                    command = StringUtils.replace(command, " -P " + password, " -P nothing");
                }
                logger.error("IPMI execute exception. Command is " + command + ". Error Message:" + e.getMessage(), e);
            }
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }

        }
        return null;
    }

}
