$(function () {
	var selectData;
	var queryId;
	function init(row,dayProducedDialog,type){
		var dataGridDiv = dayProducedDialog.find('.oc_module_resource_snmp_producedNum');
		var formDiv=dayProducedDialog.find('.oc-form').first();
		var timeDiv = dayProducedDialog.find('.oc-form').find('.timeDiv');
		var datagrid;
		if(type=="total") {
			timeDiv.show();
		}
		if(row.resourceId==0){
			queryId = row.resourceIp;
		}else {
			queryId = row.resourceId;
		}
		selectData = oc.util.getDict("snmptrap_type");
//		selectData.unshift({code:-1,name:'-- 日志级别 --'});
		var queryForm=oc.ui.form({
			selector:formDiv,
			combobox:[{
				selector:'[name=snmptrapType]',
				valueField:'code',
				data:selectData,
				placeholder:"-- 日志级别 --"//取消请选择
			}],
			datetimebox : [{
				selector:'[name="startTime"]'
			}, {
				selector:'[name="endTime"]'
			}]
		});
		queryForm.find("input[name='id']").val(queryId);
		queryForm.find(".searchBtn").first().linkbutton('RenderLB', {
			iconCls : 'icon-search',
			text : '搜索',
			onClick : function(){
				datagrid.reLoad(queryForm.val());
			}
		});
		datagrid=oc.ui.datagrid({
			selector:dataGridDiv,
			url:oc.resource.getUrl((type=="total") ? 'resourcelog/snmptrap/getTotalSnmptrap.htm': 'resourcelog/snmptrap/getSnmptrapHistory.htm'),
//			pagination : false,
			queryParams:{id:queryId},
			sortName:'occurredTime',
			sortOrder:'desc',
			octoolbar:{
				left:[queryForm.selector]
			},
			columns:[[
				{
					field : 'occurredTime',
					title : '发生时间',
					sortable : true,
					width : '20%',
					formatter : function(value, row, index) {
						var date = new Date();
						date.setTime(value);
						return formatDate(date);
					}
				},
				{
					field : 'snmptrapType',
					title : '级别',
					sortable : true,
					width : '30%',
					formatter:function(value,row,index){
						var logLevelArr = value.split(",");
						var returnValue = [];
						for(var i in selectData) {
							for(var j in logLevelArr){
								if(selectData[i].code == logLevelArr[j]) {
									returnValue[j] = selectData[i].name;
								}
							}
						}
						return returnValue.join();
					}
			    },{
					field : 'msgContent',
					title : '消息内容',
					sortable : true,
					width : '50%',
					ellipsis:true
			    }]]
		});
		return datagrid;
	}
	
	/**
	 * 打开syslog操作的dialog
	 */
	function open(row,type){
		var _mainDialog=$('<div/>');
		var _dayProducedDialog=_mainDialog.dialog({
		    title: '告警列表',
		    flt:false,
		    width: 900, 
		    height: 500,
		    buttons:[{
		    	text: '确定',
		    	handler:function(){
		    		_mainDialog.dialog('close');
		    	}
		    },{
		    	text: '取消',
		    	handler:function(){
		    		_mainDialog.dialog('close');;
		    	}
		    }],
		    href: oc.resource.getUrl('resource/module/resource-management/dailyRecord/snmpProducedNum.html'),
		    onLoad:function(){
		    	init(row,_dayProducedDialog,type);
		    },
		    onClose:function(){
//		    	selectData.shift();
		    }
		});
		return _dayProducedDialog;
	}
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
	
	 //命名空间
	oc.ns('oc.snmp.produced');
	oc.snmp.produced.dialog={
		open:function(row,type){
			return open(row,type);
		}
	};
});
