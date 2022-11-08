package com.mainsteam.stm.caplib.dict;

public enum CaplibAPIErrorCode {

	OK, // 没有错误

	ADD_DEVICE_TYPE_01, // 表示参数为空错误

	ADD_DEVICE_TYPE_02, // 表示已经存在

	ADD_DEVICE_TYPE_03, // 表示写文件错误

	ADD_DEVICE_TYPE_04, // 表示要删除的sysoid不存在

	ADD_DEVICE_TYPE_05;// 表示要删除的sysoid是系统默认，不允许删除
}
