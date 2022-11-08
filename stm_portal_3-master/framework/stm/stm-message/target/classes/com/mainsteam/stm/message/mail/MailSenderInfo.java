package com.mainsteam.stm.message.mail;          

/**
 * <li>文件名称: AlarmInfo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 发送邮件需要使用的基本信息  </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月18日
 * @author   zhangjunfeng
 */
public class MailSenderInfo {        
    // 邮件接收者的地址      
    private String[] toAddress;            
    // 邮件主题      
    private String subject;      
    // 邮件的文本内容      
    private String content;      
    // 邮件附件的文件名    （路径）  
    private String[] attachFileNames;        
    public String[] getAttachFileNames() {      
      return attachFileNames;      
    }     
    public void setAttachFileNames(String[] fileNames) {      
      this.attachFileNames = fileNames;      
    }     
   
    public String[] getToAddress() {      
      return toAddress;      
    }      
    public void setToAddress(String[] toAddress) {      
      this.toAddress = toAddress;      
    }      
   
    public String getSubject() {      
      return subject;      
    }     
    public void setSubject(String subject) {      
      this.subject = subject;      
    }     
    public String getContent() {      
      return content;      
    }     
    public void setContent(String textContent) {      
      this.content = textContent;      
    }      
}
