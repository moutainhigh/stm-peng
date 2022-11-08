package com.mainsteam.stm.plugin.ssh.shell;

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
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.future.OpenFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.ChannelPipedInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lich
 */
public class SSHShellSession implements PluginSession {

    public static final int DEFAULT_PORT = 22;
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final int DEFAULT_LOGIN_TIMEOUT = 10000;
    public static final int DEFAULT_RESPONSE_TIMEOUT = 60000;
    public static final int DEFAULT_SO_TIMEOUT = 200;
    public static final Charset DEFAULT_CHARSET;
    public static final int INIT_BUFFER_SIZE = 2048;

    private static final String IP = "IP";
    private static final String PORT = "port";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PROMPT = "prompt";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String COMMAND = "COMMAND";
    private static final String GET_UNAME = "GetUname";
    private static final Log LOGGER = LogFactory.getLog(SSHShellSession.class);
    private static final String[] HOST_SERIES = new String[]{"Linux", "FreeBSD", "UnixWare", "SunOS", "AIX", "SCO_SV", "HP-UX"};
    private static final String[] DEVICE_SERIES = new String[]{"Cisco", "CiscoExtend", "Huawei", "H3C"};

    static {
        Charset tmp = Charset.defaultCharset();
        try {
            tmp = Charset.forName("UTF-8");
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Do no support UTF-8.");
        }
        DEFAULT_CHARSET = tmp;
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Charset = " + DEFAULT_CHARSET);
    }

    private String ip, username, password;
    private int port = DEFAULT_PORT;
    private SshClient client;
    private ClientSession session;
    private ChannelShell shell;
    private String prompt;
    private String deviceType;

    private boolean available;
    private boolean isReady;

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
                case PROMPT:
                    prompt = value;
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
        } catch (IOException e) {
            throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED, "SSH login failed", e);
        }
    }

    private void login() throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.debug("Login begin.");
        destory();
        client = SshClient.setUpDefaultClient();
        client.start();
        try {
            ConnectFuture connectFuture = client.connect(username, ip, port);
            connectFuture.verify(DEFAULT_CONNECT_TIMEOUT);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Connect Successfully.");
            session = connectFuture.getSession();
            session.addPasswordIdentity(password);
            try {
                AuthFuture authFuture = session.auth();
                authFuture.verify(DEFAULT_CONNECT_TIMEOUT);
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Auth Successfully.");
                try {
                    shell = session.createShellChannel();
                    shell.setPtyLines(0xff);
                    shell.setPtyColumns(0xff);
                    shell.setPtyHeight(0xff);
                    shell.setPtyWidth(0xff);
//                    Map<PtyMode, Integer> ptyModes = Collections.unmodifiableMap(new EnumMap<PtyMode, Integer>(PtyMode.class) {
//                        {
//                            put(PtyMode.ISIG, 1);
//                            put(PtyMode.ICANON, 0);
//                            put(PtyMode.ECHO, 0);
//                            put(PtyMode.ECHOE, 0);
//                            put(PtyMode.ECHOK, 0);
//                            put(PtyMode.ECHONL, 0);
//                            put(PtyMode.ECHOCTL, 0);
//                            put(PtyMode.ECHOKE, 0);
//                            put(PtyMode.NOFLSH, 0);
//                        }
//                    });
//                    shell.setPtyModes(ptyModes);
                    OpenFuture openFuture = shell.open();
                    openFuture.verify(DEFAULT_CONNECT_TIMEOUT);
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("Open shell Successfully.");
                    // add "\n" at the head of welcome message in case that message has only one line
                    String message = "\n" + expect(null, DEFAULT_LOGIN_TIMEOUT);
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("Welcome message:" + message);
                    prompt = StringUtils.trim(StringUtils.substringAfterLast(message, "\n"));
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("prompt is set to: \"" + prompt + "\"");
                    isReady = true;
                    available = true;
                } catch (IOException e) {
                    throw new IOException("Opening shell failed.", e);
                }
            } catch (IOException e) {
                throw new IOException("Auth failed.", e);
            }
        } catch (IOException e) {
            throw new IOException("Connection failed.", e);
        }
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
        if (shell != null) {
            try {
                shell.close(true);
            } catch (Exception ignored) {
            }
            shell = null;
        }
        if (session != null) {
            try {
                session.close(true);
            } catch (Exception ignored) {
            }
            session = null;
        }
        if (client != null) {
            client.stop();
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
            String message = expect(prompt, DEFAULT_LOGIN_TIMEOUT);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Data before executing command \"" + command + "\":\n" + message);
            if (!isReady)
                throw new IOException("Can't find the operation prompt, message:\n" + message);
        }
        OutputStream out = shell.getInvertedIn();
        out.write((command + "\n").getBytes(DEFAULT_CHARSET));
        out.flush();
        String message = expect(prompt, DEFAULT_RESPONSE_TIMEOUT);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Raw result of command \"" + command + "\":\n" + message);
        message = messageTrim(message, command, prompt);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Formatted result of command \"" + command + "\":\n" + message);
        return message;
    }

    private String expect(final String target, long timeout) {
        isReady = false;
        final ByteArrayOutputStream byteArray = new ByteArrayOutputStream(INIT_BUFFER_SIZE);
        final Object lock = new Object();
        synchronized (lock) {
            Thread stdoutReaderThread = new Thread(Thread.currentThread().getName() + "-StdoutReader") {
                @Override
                public void run() {
                    ChannelPipedInputStream stdout = (ChannelPipedInputStream) shell.getInvertedOut();
                    stdout.setTimeout(DEFAULT_SO_TIMEOUT);
                    byte[] buffer = new byte[INIT_BUFFER_SIZE];
                    String tmp = "";
                    while (!isInterrupted()) {
                        try {
                            try {
                                // the read method can be interrupted.
                                int size = stdout.read(buffer);
                                if (size < 0)
                                    break;
                                byteArray.write(buffer, 0, size);
                            } catch (SocketException e) {
                                if (!StringUtils.startsWith(e.getMessage(), "Timeout ("))
                                    throw e;
                                if (target != null) {
                                    tmp = StringUtils.trim(new String(byteArray.toByteArray(), DEFAULT_CHARSET));
                                    if (StringUtils.endsWith(tmp, target)) {
                                        isReady = true;
                                        break;
                                    }
                                }
                            }
                        } catch (Throwable ignored) {
                            LOGGER.error("Internal Exception while expecting \"" + target + "\".", ignored);
                            break;
                        }
                    }
                    tmp = StringUtils.trim(new String(byteArray.toByteArray(), DEFAULT_CHARSET));
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Internal Result of expecting target \"" + target + "\":\n" + tmp);
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            };

            Thread stderrReaderThread = new Thread(Thread.currentThread().getName() + "-StderrReader") {
                @Override
                public void run() {
                    ChannelPipedInputStream stderr = (ChannelPipedInputStream) shell.getInvertedErr();
                    byte[] buffer = new byte[INIT_BUFFER_SIZE];
                    try {
                        // the read method can be interrupted.
                        while (!isInterrupted()) {
                            int size = stderr.read(buffer);
                            if (size < 0)
                                break;
                            byteArray.write(buffer, 0, size);
                        }
                    } catch (InterruptedIOException ignored) {
                    } catch (Throwable e) {
                        LOGGER.error("Internal Exception while expecting \"" + target + "\".", e);
                    }

                }
            };
            stdoutReaderThread.start();
            stderrReaderThread.start();
            try {
                lock.wait(timeout);
            } catch (InterruptedException ignored) {
            }
            stderrReaderThread.interrupt();
            stdoutReaderThread.interrupt();
        }

        return StringUtils.trim(new String(byteArray.toByteArray(), DEFAULT_CHARSET));
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
