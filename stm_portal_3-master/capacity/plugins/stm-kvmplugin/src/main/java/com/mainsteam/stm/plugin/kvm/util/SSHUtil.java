package com.mainsteam.stm.plugin.kvm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * 
 * @author yuanlb TODO desc 下午3:16:46
 */
public class SSHUtil {
	@SuppressWarnings("unused")
	private static final org.apache.commons.logging.Log log = LogFactory
			.getLog(SSHUtil.class);

	public String vmname = null;
	public String username = null;
	public String password = null;
	Connection conn = null;
	Session sess = null;

	public static void main(String[] args) throws IOException {
		System.out.println("CONN KVM：----->");
		new SSHUtil("192.168.1.186", "root", "123456").login();
	}

	public SSHUtil(String vmname, String username, String password) {
		this.vmname = vmname;
		this.username = username;
		this.password = password;
	}

	public void login() throws IOException {
		if (conn != null) {
			closeConn();

		}
		/* Create a connection instance */

		conn = new Connection(vmname);

		/* Now connect */

		conn.connect();

		/*
		 * Authenticate. If you get an IOException saying something like
		 * "Authentication method password not supported by the server at this stage."
		 * then please check the FAQ.
		 */

		boolean isAuthenticated = conn.authenticateWithPassword(username,
				password);
		// System.out.println("conn info=====>"+conn.getHostname()+" port:"+conn.getPort()+" conn info:"+conn.getConnectionInfo()+"  conn mac"+conn.getAvailableMACs().getClass().getName());
		// for (String mac : conn.getAvailableMACs()) {
		// System.out.println("MAC:=====>"+mac.CASE_INSENSITIVE_ORDER);
		// }
		// System.out.println("isAuthenticated=====>"+isAuthenticated);
		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");
	}

	public void writeCmd(String[] cmds) throws IOException {
		if (sess != null) {
			closeSess();
		}
		/* Create a session */

		sess = conn.openSession();

		sess.startShell();

		OutputStream out = sess.getStdin();

		for (int i = 0; i < cmds.length; i++) {
			try {
				java.lang.Thread.sleep(2 * 1000);
			} catch (Exception e) {
				// null
			}
			if (cmds[i].endsWith("{noenter}")) {
				out.write((cmds[i]).replace("{noenter}", "").getBytes());
			} else {
				out.write((cmds[i] + "\n").getBytes());
			}
			out.flush();
		}
		try {
			java.lang.Thread.sleep(2 * 1000);
		} catch (Exception e) {
			// null
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}

		}
	}

	public String getOut() throws IOException {
		InputStream stdout = null;
		BufferedReader br = null;
		StringBuffer buf = null;
		try {
			stdout = new StreamGobbler(sess.getStdout());

			br = new BufferedReader(new InputStreamReader(stdout));
			buf = new StringBuffer();
			// System.out.println(stdout.available());
			while (true) {
				int i = br.read();

				if (i == -1)
					break;
				buf.append((char) i);
			}
			// System.out.println(buf.toString());

			/* Show exit status, if available (otherwise "null") */

			// System.out.println("ExitCode: " + sess.getExitStatus());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (br != null) {
				try {
					br.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					br = null;
				}
			}

			if (stdout != null) {
				try {
					stdout.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					stdout = null;
				}
			}

		}
		return buf.toString();
	}

	public void closeConn() {
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}

	public void closeSess() {
		if (sess != null) {
			sess.close();
			sess = null;
		}
	}

	public void close() {
		closeSess();
		closeConn();
	}
}
