package com.mainsteam.stm.network.ssh;

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

/**
 * @author lich
 */
public class SSHShellSession{

    public static final int DEFAULT_PORT = 22;
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final int DEFAULT_LOGIN_TIMEOUT = 10000;
    public static final int DEFAULT_RESPONSE_TIMEOUT = 5000;
    public static final int DEFAULT_SO_TIMEOUT = 200;
    public static final Charset DEFAULT_CHARSET;
    public static final int INIT_BUFFER_SIZE = 2048;

    private static final Log LOGGER = LogFactory.getLog(SSHShellSession.class);

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

    private boolean isReady;

	public SSHShellSession(String userName,String passWord,String ip){
		this.username = userName;
		this.password = passWord;
		this.ip = ip;
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

    public void destory() {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Destroy begin");
        if (shell != null) {
            try {
                shell.close();
            } catch (IOException ignored) {
            }
            shell = null;
        }
        if (session != null) {
            try {
                session.close();
            } catch (IOException ignored) {
            }
            session = null;
        }
        if (client != null) {
            client.stop();
            client = null;
        }
    }

    public void runCmd(String[] commands) throws IOException {
    	login();
    	for(String command : commands){
    		command = command.trim();
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
    	}
    }
    
	public String getRemoteInfo(){
		return expect(prompt, DEFAULT_RESPONSE_TIMEOUT);
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
                            		if (LOGGER.isDebugEnabled())
                            			LOGGER.debug("Anlysis command ends : \"" + tmp);
                                    if (StringUtils.endsWith(tmp, target)
                                    		|| StringUtils.endsWith(tmp, ">")
                                    		|| StringUtils.endsWith(tmp, "/$")
                                    		|| StringUtils.endsWith(tmp, "#")
                                    		|| StringUtils.endsWith(tmp, ":")
                                    		|| StringUtils.endsWith(tmp, "?")) {
                                        isReady = true;
                                        break;
                                    }
                                }
                            }
                        } catch (Throwable ignored) {
                            LOGGER.error("Internal Exception while expecting", ignored);
                            break;
                        }
                    }
                    tmp = StringUtils.trim(new String(byteArray.toByteArray(), DEFAULT_CHARSET));
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Internal Result of expecting target :\n" + tmp);
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

}
