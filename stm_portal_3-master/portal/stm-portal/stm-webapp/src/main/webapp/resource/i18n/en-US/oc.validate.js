oc.ns('oc.local');

$.fn.validatebox.defaults.missingMessage = '输入项为必填项';
$.fn.validatebox.defaults.rules.length.message = '输入内容长度必须介于{0}和{1}之间';

oc.local.valid={
	min:'输入值不能小于{0}',
	max:'输入值不能小大于{0}',
	range:'输入值须介于{0}和{1}之间',
	minLength:'输入最小长度为{0}',
	maxLength:'输入最大长度为{0}',
	int:'输入值必须为整数',
	digit:'输入值必须为数字',
	pint:'输入值必须为正整数',
	nint:'输入值必须为负整数',
	pdigit:'输入值不能为负数',
	ndigit:'输入值不能为正数'
};