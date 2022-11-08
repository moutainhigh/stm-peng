(function($) {
	var datagrid = null;
	var mainDiv = $('#oc_module_resource_management_snmptrap').attr('id',
			oc.util.generateId()).panel({
		fit : true,
		isOcAutoWidth : true
	}), 
	datagridDiv = mainDiv.find('.oc_module_resource_management_snmptrap_datagrid').first(), 
	user = oc.index.getUser();
	loginUserType = user.userType, 
	loginUserId = user.id, 
	form = mainDiv.find(".oc-form").first();
	var domain = $.extend({},{
		selector:'[name=domainId]',
		placeholder:'-- 请选择 --',
		onSelect:function(r){
			datagrid.reLoad(queryForm.val());
		}
	},
	loginUserType!=2 ? {data:oc.index.getDomains(),
		filter:function(data){
//			data.unshift({id:0,name:"all"});//添加到数组的开头
			return data;
		}} : {url:oc.resource.getUrl('system/user/getLoginUserDomains.htm'),
		filter:function(data){
			var domainData = data.data;
//			domainData.unshift({id:0,name:"all"});//添加到数组的开头
			return domainData;
		}
	});
	queryForm = oc.ui.form({
		selector : form,
		combobox : [domain]
	});
	var _dialog = $('<div class="oc-dialog-float"/>');
	var userTypes = oc.util.getDictObj('user_type'), toolbar = [ {
		text : '开始监听',
		iconCls : 'fa fa-eye',
		onClick : function() {
			var ids = datagrid.getSelectIds();
			if(ids.length < 1) {
				alert("请至少选择一条数据!!!","danger");
				return;
			}
    		oc.util.ajax({
				url : oc.resource.getUrl('resourcelog/snmptrap/updateMonitorState.htm'),
				data : {id:ids.join(),isMonitor:1},
				async:false,
				success : function(data) {
					datagrid.reLoad();
				},
				successMsg:"监听成功"
			});
		}
	}, {
		text : '取消监听',
		iconCls : 'fa fa-times-circle',
		onClick : function() {
			var ids = datagrid.getSelectIds();
			if(ids.length < 1) {
				alert("请至少选择一条数据!!!","danger");
				return;
			}
    		oc.util.ajax({
				url : oc.resource.getUrl('resourcelog/snmptrap/updateMonitorState.htm'),
				data : {id:ids.join(),isMonitor:0},
				async:false,
				success : function(data) {
					datagrid.reLoad();
				},
				successMsg:"取消监听成功"
			});
		}
	} ];
	datagrid = oc.ui.datagrid({
		selector : datagridDiv,
		queryForm : queryForm,
		queryConditionPrefix : '',
		url : oc.resource.getUrl('resourcelog/snmptrap/querySyslogList.htm'),
		queryParams:{typeId:2},//snmptrap类型2
		octoolbar : {
			right : toolbar,
			left : [ queryForm.selector ]
		},
		hideReset : true,
		hideSearch : true,
		columns : [ [
				{
					field : 'id',
					title : '-',
					checkbox : true,
					width : 20
				},
				{
					field : 'name',
					title : '显示名称',
					remoteSort: false,
					sortable : true,
					width : 40,
					formatter:function(value, row, index){
						if(0==row.isMonitor){
							return "<span class='ico ico-monitor'>"+value +"</span>";
						}else if(1==row.isMonitor){
							return "<span class='ico ico-greenmonitor'>"+value+"</span>";
						}
					}
				},
				{
					field : 'resourceIp',
					title : 'IP地址',
					sortable : true,
					width : 30
				},
				{
					field : 'curDateCount',
					title : '当日产生数量',
					width : 20,
					formatter:function(value,row,index){
						var nowDate = new Date();
						var lastDate = new Date();
						lastDate.setTime(row.lastDate);
						
						var nowDateStr = nowDate.toLocaleDateString();
//						console.log(nowDateStr);
						var lastDateStr = lastDate.toLocaleDateString();
						if(nowDateStr != lastDateStr) {
							value = 0;
						}
						return "<span title='点我查看当日产生数量' class='oc-pointer-operate'><font color='#D8CC1'>" + value + "</font></span>";
					}
				},
				{
					field : 'allCount',
					title : '总计',
					width : 20,
					formatter:function(value,row,index){
						return "<span title='点我查看产生总量' class='oc-pointer-operate'><font color='#D8CC1'>" + value + "</font></span>";
					}
				},
				{
					field : 'lastDate',
					title : '最后产生时间',
					width : 40,
					formatter:function(value, row, index){
						if(row.curDateCount == 0 && row.allCount == 0){
							return "";
						}else {
							var date = new Date();
							date.setTime(value);
							return formatDate(date);
						}
					}
				},
				{
					field : 'strategyName',
					title : '策略',
					width : 30,
					ellipsis:true
				}, {
					field : 'operating',
					title : '更改策略',
					sortable : false,
					width : 20,
					formatter : function(value, row, index) {
						var userObject = oc.index.getUser();
						if(userObject.systemUser){
							return "<span class='fa fa-cog locate-left' data='"						//r-set
							+ row.id
							+ "'></span>";
						}else if(userObject.domainUser && userObject.id == row.userId){
							return "<span class='fa fa-cog locate-left' data='"
							+ row.id
							+ "'></span>";
						}else{
							return "无权限";
						}
					}
				}, ] ],
		onClickCell : function(rowIndex, field, value, e) {
			var row = datagrid.selector.datagrid("getRows")[rowIndex];
			if(field=="operating"){
				oc.resource.loadScript('resource/module/resource-management/dailyRecord/js/snmptrapOperating.js', function() {
					oc.syslogoperating.dialog.open({
						row:row,
						callback:function(){
							datagrid.reLoad();
						}
					});
				});
			}
			//当日产生数量
			else if(field=="curDateCount"){
				oc.resource.loadScript('resource/module/resource-management/dailyRecord/js/snmpProducedNum.js', function() {
					oc.snmp.produced.dialog.open(row,"dayProduced");
				});
			}
			//总计
			else if(field=="allCount"){
				oc.resource.loadScript('resource/module/resource-management/dailyRecord/js/snmpProducedNum.js', function() {
					oc.snmp.produced.dialog.open(row,"total");
				});
			}
		}
	});
	function formatDate(date){
		var currentDate = "";
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		
		var hour = date.getHours();
		var minutes = date.getMinutes();
		var sencondes = date.getSeconds();
		currentDate = year+'-'+month+'-'+day
		if(hour<10){
			currentDate+=' '+'0'+hour;
		}else{
			currentDate+=' '+hour;
		}
		if(minutes<10){
			currentDate+=':'+'0'+minutes;
		}else{
			currentDate+=':'+minutes;
		}
		if(sencondes<10){
			currentDate+=':'+'0'+sencondes;
		}else{
			currentDate+=':'+sencondes;
		}
		return currentDate;
	}
})(jQuery);