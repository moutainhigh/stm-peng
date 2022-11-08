$(function() {
	function discoverfailure(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		var id = oc.util.generateId();
		this.failDom = $("#singleDisc_result_failure").attr('id', id);
	}
	discoverfailure.prototype = {
		constructor : discoverfailure,
		form : undefined, // 操作表单
		cfg : undefined, // 外界传入的配置参数 json对象格式
		discFailureDiv : undefined,
		open : function() {
			var data = this.cfg.data;
			var usetime = this.failDom.find(".useTime");
			usetime.html(usetime.html() + data.time);
			// 失败信息
			var failCode = this.failDom.find(".failCode");
			var failMsg = this.failDom.find(".failMsg").hide();
			var failDetailMsg = this.failDom.find(".failDetailMsg").hide();
			failCode.html(failCode.html() + (data.failCode == null ? "" : data.failCode));
			failMsg.html(failMsg.html() + data.failMsg);
			failDetailMsg.html(failDetailMsg.html() + data.failDetailMsg);
			
			var datagridHeight = '355px';
			// 如果是数据库发现失败显示特殊提示信息
			if("Database" == data.categoryId){
				this.failDom.find(".databaseTooltipsInfo").show();
				datagridHeight = '315px';
			}else{
				this.failDom.find(".databaseTooltipsInfo").hide();
				datagridHeight = '350px';
			}
			// 添加按钮事件
			//this._createLinkButton();
			this._createDatagrid([data.failCode], datagridHeight);
		},
		_createDatagrid : function(failCodes, datagridHeight){
			var failMsgDatagrid = oc.ui.datagrid({
				selector : this.failDom.find(".failStackDatagrid"),
				fit : false,
				height : datagridHeight,
				pagination : false,
				singleSelect : true,
				fitColumns : true,
				columns : [ [ {
					field : 'content',
					title : '诊断内容',
					width : '45%',
					align : 'center',
					ellipsis : true
				}, {
					field : 'result',
					title : '诊断结果',
					width : '15%',
					align : 'center'
				}, {
					field : 'advise',
					title : '处理建议',
					width : '40%',
					align : 'center',
					formatter:function(value,row,rowIndex){
						return '<label title=' + value + ' >'+value+'</label>';
					}
				}]]
			});
			var failMsgs = new Array();
			for(var i = 0; i < failCodes.length; i ++){
				var content = oc.local.module.resource.discovery['code_' + failCodes[i]];
				failMsgs.push({
					content : content == undefined ? '未知错误' : content,
					result : '失败',
					advise : ''
				});
			}
			var datagridData ={
				code : 200,
				data : {
					total : failMsgs.length,
					rows : failMsgs
				}
			};
			failMsgDatagrid.selector.datagrid('loadData', datagridData);
		},
		_defaults : {}
	};
	// 命名空间
	oc.ns('oc.module.resmanagement.discresource');
	// 对外提供入口方法
	oc.module.resmanagement.discresource.discoverfailure = {
		open : function(cfg) {
			var failure = new discoverfailure(cfg);
			failure.open();
		}
	};
});