/**
 * COPYRIGHT (C) 2010 LY. ALL RIGHTS RESERVED.
 *
 * No part of this publication may be reproduced, stored in a retrieval system,
 * or transmitted, on any form or by any means, electronic, mechanical, photocopying,
 * recording, or otherwise, without the prior written permission of 3KW.
 *
 * Created By: zzqiang
 * Created On: 2013-10-17
 *
 * Amendment History:
 * 
 * Amended By       Amended On      Amendment Description
 * ------------     -----------     ---------------------------------------------
 *
 **/
package com.mainsteam.stm.message.sgip.constant;

import com.mainsteam.stm.message.MsgSettingInfo;
import com.mainsteam.stm.message.MsgSettingManager;
import com.mainsteam.stm.message.sgip.interf.MessageHandler;

public class ClientConstant {

	private MsgSettingInfo setting =new MsgSettingManager().getMsgSetting();
	
	public String getCompanyCode(){
		return "00000";
	}

	public String getAreaCode(){
		return "0731";
	}

	public String getLoginName(){
		return setting.getMessageLoginAccount();
	}

	public String getLoginPwd(){
		return setting.getMessagePassword();
	}

	public String getSpNumber(){
		return setting.getSpCode();
	}
	
	public String getServerIp(){
		return setting.getMessageGatewayIp();	
	}

	public String getServerPort(){
		return setting.getMessagePort();
	}

	public static int SGIP_SUBMIT_MAX_USER_NUMBER = 50;

	public int getResponseTimeout(){
		return Integer.valueOf(setting.getTimeOut());
	}

	public int getLoalhostSGIPPort(){
		return Integer.valueOf(setting.getMessagePort());
	}

	public static String DEFAULT_SERVICE_TYPE = "defaultype";

	public static MessageHandler SGIP_MSG_HANDLER = null;

	public static String PERMIT_IP = "127.0.0.1";

	public static String IS_NIO = "y";

	
}
