package com.mainsteam.stm.plugin.ssh;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.sshtools.net.SocketTransport;
import com.sshtools.ssh.*;
import com.sshtools.ssh.components.ComponentManager;
import com.sshtools.ssh.components.jce.JCEComponentManager;
import com.sshtools.ssh.components.jce.JCEProvider;
import com.sshtools.ssh2.Ssh2Client;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class SSHSession implements PluginSession {

    public static final int DEFAULT_PORT = 22;
    public static final int DEFAULT_RESPONSE_TIMEOUT = 60000; //ms
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000; //ms
    public static final Charset DEFAULT_CHARSET;
    public static final int INIT_BUFFER_SIZE = 2048;

    private static final String IP = "IP";
    private static final String PORT = "port";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    //    public static final String TIMEOUT = "HostTimeout";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String COMMAND = "COMMAND";
    private static final String GET_UNAME = "GetUname";
    private static final Log LOGGER = LogFactory.getLog(SSHSession.class);
    private static final String[] HOST_SERIES = new String[]{"Linux", "FreeBSD", "UnixWare", "SunOS", "AIX", "SCO_SV", "HP-UX"};
    private static final String[] DEVICE_SERIES = new String[]{"Cisco", "CiscoExtend", "Huawei", "H3C"};

    static {
        Charset tmp = Charset.defaultCharset();
        try {
            tmp = Charset.forName("UTF-8");
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Do not support UTF-8.");
        }
        DEFAULT_CHARSET = tmp;
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Charset = " + DEFAULT_CHARSET);
        try {
            JCEProvider.initializeDefaultProvider(new BouncyCastleProvider());
            JCEComponentManager componentManager = (JCEComponentManager) ComponentManager.getInstance();
            componentManager.installCBCCiphers(componentManager.supportedSsh2CiphersCS());
            componentManager.installCBCCiphers(componentManager.supportedSsh2CiphersSC());
            componentManager.installArcFourCiphers(componentManager.supportedSsh2CiphersSC());
            componentManager.installArcFourCiphers(componentManager.supportedSsh2CiphersSC());
        } catch (Throwable e) {
            LOGGER.error("Fail to add Ciphers", e);
        }
    }

    private Ssh2Client client;
    private String ip;
    private int port = DEFAULT_PORT;
    private String username;
    private String password;
    private int responseTimeout = DEFAULT_RESPONSE_TIMEOUT;
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    private String deviceType;

    private boolean available = false;


    private void login() throws SshException, IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Login begin.");
        destory();
        SshConnector connector = SshConnector.createInstance();
        connector.getContext().setSocketTimeout(socketTimeout);
        SocketTransport transport = new SocketTransport(ip, port);
        client = connector.connect(transport, username, true, null);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Connect Successfully.");
        PasswordAuthentication pwd = new PasswordAuthentication();
        pwd.setPassword(password);
        if (client.authenticate(pwd) == SshAuthentication.COMPLETE && client.isConnected() && client.isAuthenticated()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Auth Successfully.");
            available = true;
        }
    }

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
            throw new PluginSessionRunException(e);
        }
    }

    @Override
    public void destory() {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Destroy begin.");
        available = false;
        if (client != null) {
            client.disconnect();
            client = null;
        }
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


    private String executeCommand(final String command) {
        final ByteArrayOutputStream byteArray = new ByteArrayOutputStream(INIT_BUFFER_SIZE);
        final Object lock = new Object();
        final AtomicReference<SshSession> sessionReference = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        synchronized (lock) {
            Thread stdoutReaderThread = new Thread(Thread.currentThread().getName() + "-StdoutReader") {
                @Override
                public void run() {
                    try {
                        sessionReference.set(client.openSessionChannel());
                        sessionReference.get().executeCommand(command);
                        latch.countDown();
                        InputStream in = sessionReference.get().getInputStream();
                        byte[] buffer = new byte[INIT_BUFFER_SIZE];
                        int size;
                        // the read method can't be interrupted, but will throw exception when session closes.
                        while (!isInterrupted() && (size = in.read(buffer)) >= 0)
                            byteArray.write(buffer, 0, size);
                    } catch (Throwable ignored) {
                        LOGGER.error("Internal Exception while executing \"" + command + "\".", ignored);
                    }
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Internal Result of command \"" + command + "\":\n" + new String(byteArray.toByteArray(), DEFAULT_CHARSET));
                    synchronized (lock) {
                        lock.notify();
                    }
                    silentClose(sessionReference.get());
                }
            };
            Thread stderrReaderThread = new Thread(Thread.currentThread().getName() + "-StderrReader") {
                @Override
                public void run() {
                    try {
                        latch.await();
                        InputStream err = sessionReference.get().getStderrInputStream();
                        byte[] buffer = new byte[INIT_BUFFER_SIZE];
                        int size;
                        // the read method can't be interrupted, but will throw exception when session closes.
                        while (!isInterrupted() && (size = err.read(buffer)) >= 0)
                            byteArray.write(buffer, 0, size);
                    } catch (Throwable ignored) {
                        LOGGER.error("Internal Exception while executing \"" + command + "\".", ignored);
                    }
                }
            };
            stdoutReaderThread.start();
            stderrReaderThread.start();
            try {
                lock.wait(responseTimeout);
            } catch (InterruptedException ignored) {
            }
            stderrReaderThread.interrupt();
            stdoutReaderThread.interrupt();
        }
        String result = StringUtils.trim(new String(byteArray.toByteArray(), DEFAULT_CHARSET));
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Result of command \"" + command + "\":\n" + result);
        if (sessionReference.get() == null)
            throw new RuntimeException("Opening session failed while executing \"" + command + "\".");
        else
            silentClose(sessionReference.get());
        return result;
    }

    private void silentClose(SshSession sshSession) {
        if (sshSession != null) {
            try {
                sshSession.close();
            } catch (Throwable ignored) {

            }
        }
    }

    @Override
    public boolean isAlive() {
        return available;
    }

    @Override
    public void reload() {
        //TODO
    }

    @Override
    public boolean check(PluginInitParameter initParameters)
            throws PluginSessionRunException {
        //TODO
        return true;
    }

}
