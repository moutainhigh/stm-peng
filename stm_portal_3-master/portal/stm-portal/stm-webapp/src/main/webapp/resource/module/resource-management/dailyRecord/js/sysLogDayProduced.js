$(function () {
	var selectData;
	function init(row,dayProducedDialog,type){
		var dataGridDiv = dayProducedDialog.find('.oc_module_resource_syslog_dayProduced');
		var formDiv=dayProducedDialog.find('.oc-form').first();
		var timeDiv = dayProducedDialog.find('.oc-form').find('.timeDiv');
		if(type=="total") {
			timeDiv.show();
		}
		var queryForm = undefined;
		selectData = oc.util.getDict("syslog_level");
//		selectData.unshift({code:-1,name:'-- 日志级别 --'});
		if(!queryForm) {
			queryForm=oc.ui.form({
				selector:formDiv,
				combobox:[{
					selector:'[name=level]',
					valueField:'code',
					data:selectData,
					placeholder:"-- 日志级别 --"//取消请选择
				}],
				datetimebox : [ {
					selector:'[name="startTime"]'
				}, {
					selector:'[name="endTime"]'
				}]
			});
		}
		queryForm.find("input[name='id']").val(row.resourceId);
		queryForm.find(".searchBtn").first().linkbutton('RenderLB', {
			iconCls : 'icon-search',
			text : '搜索',
			onClick : function(){
				datagrid.reLoad(queryForm.val());
			}
		});
		var datagrid=oc.ui.datagrid({
			selector:dataGridDiv,
			hideReset:true,//是否隐藏重置按钮
			url:oc.resource.getUrl((type=="total") ? 'portal/syslog/getTotalAlarm.htm': 'portal/syslog/getAlarmList.htm'),
//			pagination : false,
			queryParams:{id:row.resourceId},
			sortName:'occurredTime',
			sortOrder:'desc',
			fit:true,
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
//						return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+' '+(date.getHours()==0?'00':String(date.getHours()))+':'+(date.getMinutes()==0?'00':String(date.getMinutes()))+':'+(date.getSeconds()==0?'00':String(date.getSeconds()));
					}
				},{
					field : 'component',
					title : '部件',
					sortable : false,
//					align:'center',
					width : '15%',
					formatter:function(value,row,index) {
						return "本地保留"
					}
			    },
				{
					field : 'level',
					title : '级别',
					sortable : true,
					width : '15%',
					formatter:function(value,row,index){
						var logLevelArr = value.split(",");
						var returnValue = [];
						var object = oc.util.getDictObj('syslog_level');
						for(var i = 0; i < logLevelArr.length; i++) {
							returnValue[i] = object[logLevelArr[i]].name;
						}
						return returnValue.join();
					}
			    },{
					field : 'msgContent',
					title : '消息内容',
					sortable : true,
//					align:'center',
					width : '45%',
					ellipsis:true
			    },{
					field : 'keyWords',
					title : '关键字',
					sortable : true,
//					align:'center',
					width : '15%',
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
		    width: 950, 
		    height: 500,
		    // resizable : true,
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
		    href: oc.resource.getUrl('resource/module/resource-management/dailyRecord/sysLogDayProduced.html'),
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
	oc.ns('oc.syslog.dayproduced');
	oc.syslog.dayproduced.dialog={
		open:function(row,type){
			return open(row,type);
		}
	};
});
