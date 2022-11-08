package com.mainsteam.stm.plugin.pop3;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class Pop3MailAuth extends Authenticator {
	private String userName;
	private String userPassword;

	public Pop3MailAuth() {
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


	public Pop3MailAuth(String userName, String userPassword) {
		super();
		setUserName(userName);
		setUserPassword(userPassword);
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, userPassword);
	}
}
