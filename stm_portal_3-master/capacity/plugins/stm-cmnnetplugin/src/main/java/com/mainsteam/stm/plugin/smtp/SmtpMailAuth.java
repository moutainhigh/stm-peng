package com.mainsteam.stm.plugin.smtp;

import javax.mail.PasswordAuthentication;


public class SmtpMailAuth extends javax.mail.Authenticator {
	private String userName;
	private String userPassword;

	public SmtpMailAuth() {
		super();
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserPassword() {
		return userPassword;
	}


	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}


	public SmtpMailAuth(String userName, String userPassword) {
		super();
		setUserName(userName);
		setUserPassword(userPassword);
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, userPassword);
	}
}
