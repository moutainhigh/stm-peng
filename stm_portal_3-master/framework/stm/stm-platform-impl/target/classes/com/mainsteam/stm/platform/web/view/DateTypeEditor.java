package com.mainsteam.stm.platform.web.view;

import java.beans.PropertyEditorSupport;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.mainsteam.stm.util.DateUtil;

/**
 * <li>文件名称: DateTypeEditor.java</li>
 * <li>文件描述: 日期编辑器</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年8月18日
 */
public class DateTypeEditor extends PropertyEditorSupport {
	/**
	 * 短类型日期长度
	 */
	public static final int SHORT_DATE = 10;

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		text = text.trim();
		if (!StringUtils.hasText(text)) {
			setValue(null);
			return;
		}
		String partton = text.length()==SHORT_DATE ? DateUtil.DEFAULT_DATE_FORMAT : DateUtil.DEFAULT_DATETIME_FORMAT;
		try {
			setValue(DateUtil.parseDate(text, partton));
		} catch (Exception ex) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"Could not parse date: " + ex.getMessage());
			iae.initCause(ex);
			throw iae;
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	@Override
	public String getAsText() {
		Date value = (Date) getValue();
		return (value != null ? DateUtil.format(value, DateUtil.DEFAULT_DATETIME_FORMAT) : "");
	}
}
