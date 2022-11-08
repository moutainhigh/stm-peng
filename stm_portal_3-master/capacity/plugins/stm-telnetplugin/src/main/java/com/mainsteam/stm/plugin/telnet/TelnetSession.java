package com.mainsteam.stm.plugin.telnet;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelnetSession implements PluginSession {

    public static final String DEFAULT_TERMINAL = "ANSI";
    public static final int DEFAULT_PORT = 23;
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final int DEFAULT_LOGIN_TIMEOUT = 10000;
    public static final int DEFAULT_RESPONSE_TIMEOUT = 60000;
    public static final int DEFAULT_SO_TIMEOUT = 200;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final int INIT_BUFFER_SIZE = 2048;

    private static final String IP = "IP";
    private static final String PORT = "port";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    //    private static final String TIMEOUT = "timeout";
    private static final String USERNAME_PROMPT = "userprompt";
    private static final String PASSWORD_PROMPT = "passprompt";
    private static final String OPERATION_PROMPT = "opprompt";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String COMMAND = "COMMAND";
    private static final String GET_UNAME = "GetUname";
    private static final Log LOGGER = LogFactory.getLog(TelnetSession.class);
    private static final String[] HOST_SERIES = new String[]{"Linux", "FreeBSD", "UnixWare", "SunOS", "AIX", "SCO_SV", "HP-UX"};
    private static final String[] DEVICE_SERIES = new String[]{"Cisco", "CiscoExtend", "Huawei", "H3C"};
    private static final Pattern[] MORE_CONTENT_PATTERN = new Pattern[]{Pattern.compile(".*?((-+)\\s*[Mm]ore\\s*\\2)\\s*")};
    private static final String[] MORE_CONTENT_INPUT = new String[]{" "};

    private TelnetClient client;
    private String ip;
    private int port = DEFAULT_PORT;
    private String username;
    private String password;
    //    private int timeout = DEFAULT_RESPONSE_TIMEOUT;
    private String usernamePrompt;
    private String passwordPrompt;
    private String operationPrompt;
    private String deviceType;

    private boolean available = false;
    private volatile boolean isReady;

    @Override
    public void init(PluginInitParameter pluginInitParameter) throws PluginSessionRunException {
        for (Parameter parameter : pluginInitParameter.getParameters()) {
            String value = parameter.getValue();
            switch (parameter.getKey()) {
                case IP:
                    ip = value;
                    break;
                case PORT:
                    try {
                        port = Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        LOGGER.warn("Invalid port: " + value);
                    }
                    break;
                case USERNAME:
                    username = value;
                    break;
                case PASSWORD:
                    password = value;
                    break;
//                case TIMEOUT:
//                    try {
//                        timeout = Integer.valueOf(value);
//                    } catch (NumberFormatException e) {
//                        LOGGER.warn("Invalid timeout: " + value);
//                    }
//                    break;
                case USERNAME_PROMPT:
                    usernamePrompt = value;
                    break;
                case PASSWORD_PROMPT:
                    passwordPrompt = value;
                    break;
                case OPERATION_PROMPT:
                    operationPrompt = value;
                    break;
                case DEVICE_TYPE:
                    deviceType = value;
                    break;
            }
        }
        try {
            login();
            if (StringUtils.isEmpty(deviceType))
                deviceType = executeCommand("uname");
        } catch (Exception e) {
            throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED, "Telnet login failed", e);
        }
    }

    private void login() throws IOException, InvalidTelnetOptionException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Login begin.");
        destory();
        client = new TelnetClient(DEFAULT_TERMINAL);
        client.registerNotifHandler(new TelnetNotificationHandler() {
            @Override
            public void receivedNegotiation(int negotiation_code, int option_code) {
                String command;
                switch (negotiation_code) {
                    case TelnetNotificationHandler.RECEIVED_DO:
                        command = "DO";
                        break;
                    case TelnetNotificationHandler.RECEIVED_DONT:
                        command = "DONT";
                        break;
                    case TelnetNotificationHandler.RECEIVED_WILL:
                        command = "WILL";
                        break;
                    case TelnetNotificationHandler.RECEIVED_WONT:
                        command = "WONT";
                        break;
                    case TelnetNotificationHandler.RECEIVED_COMMAND:
                        command = "COMMAND";
                        break;
                    default:
                        command = Integer.toString(negotiation_code); // Should not happen
                        break;
                }
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Received " + command + " for option " + TelnetOption.getOption(option_code) + ", option code = " + option_code);
            }
        });
        client.setCharset(DEFAULT_CHARSET);
        client.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        client.setDefaultTimeout(DEFAULT_SO_TIMEOUT);
        client.addOptionHandler(new EchoOptionHandler(true, true, true, true));
        client.addOptionHandler(new SuppressGAOptionHandler(true, true, true, true));
        client.addOptionHandler(new WindowSizeOptionHandler(0xffff, 0xffff, true, true, true, true));
        client.addOptionHandler(new TerminalTypeOptionHandler(DEFAULT_TERMINAL, true, true, true, true));
        client.connect(ip, port);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Connect Successfully.");
        if (StringUtils.isNotEmpty(username)) {
            String message = expect(usernamePrompt, DEFAULT_LOGIN_TIMEOUT);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Try to input username, message:\n" + message);
            if (!isReady)
                throw new IOException("Can't find the username prompt, message:\n" + message);
            OutputStream out = client.getOutputStream();
            out.write((username + "\n").getBytes(DEFAULT_CHARSET));
            out.flush();
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Input username Successfully.");
        }
        if (StringUtils.isNotEmpty(password)) {
            String message = expect(passwordPrompt, DEFAULT_LOGIN_TIMEOUT);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Try to input password, message:\n" + message);
            if (!isReady)
                throw new IOException("Can't find the password prompt, message:\n" + message);
            OutputStream out = client.getOutputStream();
            out.write((password + "\n").getBytes(DEFAULT_CHARSET));
            out.flush();
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Input password Successfully.");
        }
        String message = expect(operationPrompt, DEFAULT_LOGIN_TIMEOUT);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Try to find the operation prompt, message:\n" + message);
        if (!isReady) {
            throw new IOException("Can't find the operation prompt, message:\n" + message);
        }
        operationPrompt = StringUtils.trim(StringUtils.substringAfterLast("\n" + message, "\n"));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("prompt is set to: \"" + operationPrompt + "\"");
        available = true;
    }

    @Override
    public boolean check(PluginInitParameter pluginInitParameter) throws PluginSessionRunException {
        return false;
    }

    @Override
    public void destory() {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Destroy begin");
        available = false;
        if (client != null) {
            try {
                client.disconnect();
            } catch (IOException e) {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("IOException when client disconnects", e);
            }
            client = null;
        }
    }

    @Override
    public void reload() {
    }

    @Override
    public boolean isAlive() {
        return available;
    }

    @Override
    public PluginResultSet execute(PluginExecutorParameter<?> pluginExecutorParameter, PluginSessionContext pluginSessionContext) throws PluginSessionRunException {
        PluginResultSet resultSet = new PluginResultSet();
        if (pluginExecutorParameter instanceof PluginArrayExecutorParameter) {
            ArrayList<String> commandList = new ArrayList<>();
            HashMap<String, String> placeHolderMap = new HashMap<>();
            boolean getUName = false;//主要用于计算内存利用率需要主机类型
            for (Parameter parameter : ((PluginArrayExecutorParameter) pluginExecutorParameter).getParameters()) {
                String key = parameter.getKey();
                String value = parameter.getValue();
                if (key.equals(COMMAND)) {
                    commandList.add(value);
                } else if (key.equals(GET_UNAME)) {
                    getUName = true;
                } else if (key.equalsIgnoreCase(deviceType)) {
                    commandList.add(value);
                } else if (StringUtils.endsWithAny(key, HOST_SERIES) || StringUtils.endsWithAny(key, DEVICE_SERIES)) {
                } else if (StringUtils.startsWith(key, "NOT_EMPTY")) {
                    if (StringUtils.isNotEmpty(value))
                        placeHolderMap.put(key, value);
                } else {
                    placeHolderMap.put(key, value);
                }
            }

            if (commandList.isEmpty() && placeHolderMap.get("returnParameterValue") != null) {
                String[] parameterValues = placeHolderMap.get("returnParameterValue").split(",");
                for (int p = 0; p < parameterValues.length; p++) {
                    resultSet.putValue(0, p, placeHolderMap.get(parameterValues[p]));
                }
            }

            replaceHolders(commandList, placeHolderMap);
            int p = 0;
            for (String command : commandList) {
                try {
                    resultSet.putValue(0, p, executeCommand(command));
                    p++;
                } catch (Exception once) {
                    LOGGER.error("Exception while executing \"" + command + "\", retry.", once);
                    try {
                        login();
                        resultSet.putValue(0, p, executeCommand(command));
                        p++;
                    } catch (Exception again) {
                        destory();
                        LOGGER.error("Exception while executing \"" + command + "\" again.", again);
                        throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED, "Exception while executing \"" + command + "\"", again);
                    }
                }
            }
            if (getUName) {//有些主机的内存利用率需要进行特殊计算
                resultSet.putValue(0, p, deviceType);
            }
        }
        return resultSet;
    }

    private String executeCommand(String command) throws IOException {
        if (!isReady) {
            String message = expect(operationPrompt, DEFAULT_LOGIN_TIMEOUT);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Data before executing command \"" + command + "\":\n" + message);
            if (!isReady)
                throw new IOException("Can't find the operation prompt, message:\n" + message);
        }
        OutputStream out = client.getOutputStream();
        out.write((command + "\n").getBytes(DEFAULT_CHARSET));
        out.flush();
        String message = expect(operationPrompt, DEFAULT_RESPONSE_TIMEOUT);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Raw result of command \"" + command + "\":\n" + message);
        message = messageTrim(message, command, operationPrompt);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Formatted result of command \"" + command + "\":\n" + message);
        return message;
    }

    private String expect(final String target, final int timeout) throws IOException {
        isReady = false;
        final ByteArrayOutputStream byteArray = new ByteArrayOutputStream(INIT_BUFFER_SIZE);
        final Object lock = new Object();
        synchronized (lock) {
            Thread readerThread = new Thread(Thread.currentThread().getName() + "-Reader") {
                @Override
                public void run() {
                    InputStream in = client.getInputStream();
                    byte[] buffer = new byte[INIT_BUFFER_SIZE];
                    String tmp;
                    int size = 0;
                    while (!isInterrupted()) {
                        try {
                            try {
                                size = in.read(buffer);
                                if (size < 0)
                                    break;
                                byteArray.write(buffer, 0, size);
                            } catch (SocketTimeoutException e) {
                                if (size > 0) {
                                    tmp = StringUtils.trim(new String(byteArray.toByteArray(), DEFAULT_CHARSET));
                                    if (target != null) {
                                        if (StringUtils.endsWith(tmp, target)) {
                                            isReady = true;
                                            break;
                                        }
                                    }
                                    for (int i = 0; i < MORE_CONTENT_PATTERN.length; ++i) {
                                        Matcher matcher = MORE_CONTENT_PATTERN[i].matcher(StringUtils.substringAfterLast("\n" + tmp, "\n"));
                                        if (matcher.matches()) {
                                            OutputStream out = client.getOutputStream();
                                            out.write((MORE_CONTENT_INPUT[i]).getBytes(DEFAULT_CHARSET));
                                            out.flush();
                                        }
                                    }
                                    size = 0;
                                }
                            }
                        } catch (Throwable ignored) {
                            LOGGER.error("Internal Exception while expecting \"" + target + "\".", ignored);
                            break;
                        }
                    }
                    if (LOGGER.isDebugEnabled()) {
                        tmp = StringUtils.trim(new String(byteArray.toByteArray(), DEFAULT_CHARSET));
                        LOGGER.debug("Internal Result of expecting target \"" + target + "\":\n" + tmp);
                    }
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            };
            readerThread.start();
            try {
                lock.wait(timeout);
            } catch (InterruptedException ignored) {
            }
            readerThread.interrupt();
        }


        if (LOGGER.isDebugEnabled()) {
            String tmp = StringUtils.trim(new String(byteArray.toByteArray(), DEFAULT_CHARSET));
            LOGGER.debug("Raw Result of expecting target \"" + target + "\":\n" + tmp);

        }
        VirtualTerminalAnsiOutputStream ansiOutputStream = new VirtualTerminalAnsiOutputStream();
        for (byte b : byteArray.toByteArray()) {
            ansiOutputStream.write(b);
        }
        return StringUtils.trim(new String(ansiOutputStream.toByteArray(), DEFAULT_CHARSET));
    }

    private String messageTrim(String message, String command, String prompt) {
        if (StringUtils.startsWith(message, command)) {
            message = StringUtils.substringAfter(message, command);
        }
        if (StringUtils.endsWith(message, prompt)) {
            message = StringUtils.substringBeforeLast(message, prompt);
        }
        return StringUtils.trim(message);
    }

    private void replaceHolders(List<String> commandList, Map<String, String> placeHolderMap) {
        String searchList[] = new String[placeHolderMap.size()];
        String replaceList[] = new String[placeHolderMap.size()];
        int p = 0;
        for (Map.Entry<String, String> entry : placeHolderMap.entrySet()) {
            searchList[p] = "${" + entry.getKey() + "}";
            replaceList[p] = entry.getValue();
            p++;
        }
        for (p = 0; p < commandList.size(); ++p) {
            commandList.set(p, StringUtils.trim(StringUtils.replaceEach(commandList.get(p), searchList, replaceList)));
        }
    }

}
