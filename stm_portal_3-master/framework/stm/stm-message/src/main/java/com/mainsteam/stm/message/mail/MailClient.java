package com.mainsteam.stm.message.mail;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.mainsteam.stm.message.MsgSettingInfo;
import com.mainsteam.stm.message.MsgSettingManager;

/**
 * <li>文件名称: AlarmInfo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 邮件发送器  </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月18日
 * @author   zhangjunfeng
 */
public class MailClient {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MailClient.class);
	
	private static MsgSettingManager manager = new MsgSettingManager();
	
	private static Random random = new Random();
	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件的信息
	 */
	public static boolean sendTextMail(MailSenderInfo mailInfo) {
		if (logger.isDebugEnabled()) {
			logger.debug("sendTextMail(MailSenderInfo) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		int sn =random.nextInt(100000)%(100000-0);
		logger.info(sn+"-开始发送文本邮件:邮件内容（"+mailInfo.getContent()+")");

		MsgSettingInfo setting =manager.getMsgSetting();
		try {
//			final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			Properties pro = new Properties();
		    pro.put("mail.smtp.host", setting.getEmailSmtp());      
		    pro.put("mail.smtp.port", setting.getEmailPort()); 
		    pro.put("mail.smtp.auth", "true");
		    //SSL
//		    pro.put("mail.smtp.socketFactory.class", SSL_FACTORY);
//		    pro.put("mail.smtp.socketFactory.fallback", false);
//		    pro.put("mail.smtp.socketFactory.port", setting.getEmailPort());
			// 如果需要身份认证，则创建一个密码验证器
	 		MyAuthenticator authenticator = new MyAuthenticator(setting.getEmailSendEmail(),setting.getEmailPassword());
			// 根据邮件会话属性和密码验证器构造一个发送邮件的session
			Session sendMailSession = Session.getInstance(pro, authenticator);
			
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(setting.getEmailSendEmail());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			InternetAddress[] sendTo = new InternetAddress[mailInfo.getToAddress().length];  
	        for (int i = 0; i < mailInfo.getToAddress().length; i++) {  
	            sendTo[i] = new InternetAddress(mailInfo.getToAddress()[i]);  
	        }  
			mailMessage.setRecipients(RecipientType.TO,sendTo);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			
			String mailContent = mailInfo.getContent();
			Multipart mp = new MimeMultipart();  
            MimeBodyPart mbp = new MimeBodyPart();  
            // 设置邮件消息的主要内容
            mbp.setContent(mailContent, "text/html;charset=gb2312");  
            mp.addBodyPart(mbp);
            //判断有没有附件，有附件添加附件
            if(null!=mailInfo.getAttachFileNames() && mailInfo.getAttachFileNames().length>0){//有附件  
            	for (String file : mailInfo.getAttachFileNames()) {
            		 mbp=new MimeBodyPart();  
                     FileDataSource fds=new FileDataSource(file); //得到数据源  
                     mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
                     mbp.setFileName(MimeUtility.encodeText(fds.getName(),"GBK",null));  //得到文件名同样至入BodyPart  
                     mp.addBodyPart(mbp);  
				}
            }   
            mailMessage.setContent(mp); //Multipart加入到信件 
            mailMessage.setHeader("X-Mailer", "ITManager Mailer");
            mailMessage.setHeader("From", "<" + from + ">");
			mailMessage.setSentDate(Calendar.getInstance().getTime());
			// 发送邮件
			Transport.send(mailMessage);

			if (logger.isDebugEnabled()) {
				logger.debug("sendTextMail(MailSenderInfo) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
			}
			logger.info(sn+"-文本邮件发送成功！");
			return true;
		}  catch (MessagingException ex) {
			logger.error("sendHtmlMail(MailSenderInfo) - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), ex); //$NON-NLS-1$ //$NON-NLS-2$
			logger.info(sn+"-文本邮件发送出错！");
			ex.printStackTrace();
			Exception nextException = ex.getNextException();
			if(nextException!=null){
				if(nextException instanceof FileNotFoundException){
					if(nextException.getMessage().contains("IOException while sending message")){
						throw new RuntimeException("邮件附件不存在，请检查邮件地");
					}
				}else if(nextException instanceof ConnectException){
					throw new RuntimeException("无法链接SMTP服务器："+setting.getEmailSmtp()+",端口："+setting.getEmailPort()+";\r\n请检查SMTP服务器和端口是否正确！");
				}else if(nextException instanceof UnknownHostException){
					throw new RuntimeException("未知的SMTP服务器："+setting.getEmailSmtp());
				}
			}else{
				if(ex instanceof AuthenticationFailedException){
					throw new RuntimeException("发件邮件“"+setting.getEmailSendEmail()+"”被锁定，无法发送邮件，请检查邮箱smtp/pop协议是否开通！");
				}
				if(ex.getMessage().equals("535 Authentication failed\n") || ex.getMessage().contains("454 Error")){
					throw new RuntimeException("邮箱验证失败，请检查账号和密码！");
				}
				if(ex.getMessage().contains("503 Error")){
					throw new RuntimeException("服务器拒绝接受发件人的电子邮件地址，邮件无法发送。服务器响应："+ex.getMessage());
				}
				if(ex.getMessage().lastIndexOf("response: 554")>0){
					throw new RuntimeException("SMTP服务器因邮件内容而拒绝接收此邮件");
				}
			}
		}catch (UnsupportedEncodingException e) {
			logger.error("sendHtmlMail(MailSenderInfo) - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), e); //$NON-NLS-1$ //$NON-NLS-2$
			e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("sendTextMail(MailSenderInfo) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		logger.info(sn+"-文本邮件发送失败！");
		return false;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		if (logger.isDebugEnabled()) {
			logger.debug("sendHtmlMail(MailSenderInfo) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		int sn =random.nextInt(100000)%(100000-0);
		logger.info(sn+"-开始发送HTML邮件:邮件内容（"+mailInfo.getContent()+")");
		MsgSettingInfo setting = manager.getMsgSetting();
		try {
			Properties pro = new Properties();
		    pro.put("mail.smtp.host", setting.getEmailSmtp());      
		    pro.put("mail.smtp.port", setting.getEmailPort());      
		    pro.put("mail.smtp.auth", "true");
				// 如果需要身份认证，则创建一个密码验证器
		    
		    MyAuthenticator authenticator = new MyAuthenticator(setting.getEmailSendEmail(),setting.getEmailPassword());
			// 根据邮件会话属性和密码验证器构造一个发送邮件的session
			Session sendMailSession = Session.getInstance(pro, authenticator);
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(setting.getEmailSendEmail());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			InternetAddress[] sendTo = new InternetAddress[mailInfo.getToAddress().length];  
	        for (int i = 0; i < mailInfo.getToAddress().length; i++) {  
	            sendTo[i] = new InternetAddress(mailInfo.getToAddress()[i]);  
	        }  
	        // Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipients(RecipientType.TO,sendTo);
			
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart("related");
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			//判断有没有附件，有附件 添加附件
			 if(null!=mailInfo.getAttachFileNames() && mailInfo.getAttachFileNames().length>0){//有附件  
            	for (String file : mailInfo.getAttachFileNames()) {
            		html=new MimeBodyPart();  
                     FileDataSource fds=new FileDataSource(file); //得到数据源  
                     html.setFileName(MimeUtility.encodeText(fds.getName(),"GBK",null));  //得到文件名同样至入BodyPart  
                     html.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart 
                     mainPart.addBodyPart(html);  
				}
                
            }   
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			mailMessage.setHeader("X-Mailer", "ITManager Mailer");
            mailMessage.setHeader("From", "<" + from + ">");
			mailMessage.setSentDate(Calendar.getInstance().getTime());
			// 发送邮件
			Transport.send(mailMessage);

			if (logger.isDebugEnabled()) {
				logger.debug("sendHtmlMail(MailSenderInfo) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
			}
			logger.info(sn+"-HTML邮件发送成功！");
			return true;
		} catch (MessagingException ex) {
			logger.error("sendHtmlMail(MailSenderInfo) - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), ex); //$NON-NLS-1$ //$NON-NLS-2$
			logger.info(sn+"-HTML邮件发送出错！");
			ex.printStackTrace();
			Exception nextException = ex.getNextException();
			if(nextException!=null){
				if(nextException instanceof FileNotFoundException){
					if(nextException.getMessage().contains("IOException while sending message")){
						throw new RuntimeException("邮件附件不存在，请检查邮件地");
					}
				}else if(nextException instanceof ConnectException){
					throw new RuntimeException("无法链接SMTP服务器："+setting.getEmailSmtp()+",端口："+setting.getEmailPort()+";\r\n请检查SMTP服务器和端口是否正确！");
				}else if(nextException instanceof UnknownHostException){
					throw new RuntimeException("未知的SMTP服务器："+setting.getEmailSmtp());
				}
			}else{
				if(ex instanceof AuthenticationFailedException){
					throw new RuntimeException("发件邮件“"+setting.getEmailSendEmail()+"”被锁定，无法发送邮件，请检查邮箱smtp/pop协议是否开通！");
				}
				if(ex.getMessage().equals("535 Authentication failed\n") || ex.getMessage().contains("454 Error")){
					throw new RuntimeException("邮箱验证失败，请检查账号和密码！");
				}
				if(ex.getMessage().contains("503 Error")){
					throw new RuntimeException("服务器拒绝接受发件人的电子邮件地址，邮件无法发送。服务器响应："+ex.getMessage());
				}
				if(ex.getMessage().lastIndexOf("response: 554")>0){
					throw new RuntimeException("SMTP服务器因邮件内容而拒绝接收此邮件!");
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("sendHtmlMail(MailSenderInfo) - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), e); //$NON-NLS-1$ //$NON-NLS-2$
			logger.info(sn+"-HTML邮件发送失败！");
			e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("sendHtmlMail(MailSenderInfo) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		logger.info(sn+"-HTML邮件发送失败！");
		return false;
	}
}
