(function($) {
	var info;
	var Idx = 0;
	function alarmManMainClass(cfg) {
		this.cfg=$.extend({},this.cfg,cfg);
	}
	function formatDate(date){
		return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
	}
	alarmManMainClass.prototype = {
		constructor : alarmManMainClass,
		mainDiv : undefined,
		alarmDataGrid : undefined,
		open : function() {
			this.mainDiv = $('#alarm_management_main_id').attr('id', oc.util.generateId());
			var that = this;

			var tabDiv = this.mainDiv.find("#alarm_management_main_tabs");

			// 表格查询表单对象
			var queryForm = oc.ui.form({
				selector : this.mainDiv.find('#bizAlarm_resource_form'),
				combobox : [{
					selector : this.mainDiv.find('[name=queryTime]'),
					fit : false,
					value : "1",
					data : [ {
						id : '1',
						name : '最近1小时',
						selected : true
					},{
						id : '2',
						name : '最近2小时'
					}, {
						id : '3',
						name : '最近4小时'
					}, {
						id : '4',
						name : '最近1天'
					}, {
						id : '5',
						name : '最近7天'
					}],
					placeholder:null,
					onSelect: function(){
						$('#resourceRadioOne').click();
					}
				}],
				datetimebox : [{
					selector:this.mainDiv.find('[name="startTime"]'),
					editable:false,
					onSelect:function(){
						 $('#resourceRadioTwo').click();
					}
					},{
					selector:this.mainDiv.find('[name="endTime"]'),
					editable:false,
					onSelect:function(){
						 $('#resourceRadioTwo').click();
					}
				}]
			});
			
			this.tabs = tabDiv.tabs({
				fit : false,
				width : "100%",
				onSelect : function(title,index) {
										Idx=index;
					var url = that.cfg.nodeId==undefined?oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
						that.cfg.id+"&name="+that.cfg.name+"&restore="+'false'):oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
								that.cfg.id+"&name="+that.cfg.name+"&nodeId="+that.cfg.nodeId+"&restore="+'false');
					var user = oc.index.getUser();
					
					if(index == 0){
						
						url = that.cfg.nodeId==undefined?oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
						that.cfg.id+"&name="+that.cfg.name+"&restore="+'false'):oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
								that.cfg.id+"&name="+that.cfg.name+"&nodeId="+that.cfg.nodeId+"&restore="+'false');
						//$('.bizLightAlarmClass').show();
						if(that.onselects){
							var option = $('#alarm_datagrid').datagrid("options");

							//查询所有
							that.queryForm._initVal.queryTime=0;
						}
						$('#csTime').show();
						$('#hfTime').hide();
					}else if(index == 1){
						
						url = that.cfg.nodeId==undefined?oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
						that.cfg.id+"&name="+that.cfg.name+"&restore="+'true'):oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
								that.cfg.id+"&name="+that.cfg.name+"&nodeId="+that.cfg.nodeId+"&restore="+'true');

						//($('.bizLightAlarmClass').height());
						var option = $('#alarm_datagrid').datagrid("options");
						
						var user = oc.index.getUser();
						$('#csTime').hide();
						$('#hfTime').show();
					}

					oc.ui.progress();
					that.loadAlarmDataGrid(url);
									}
			});
			var rightButtonArray = [queryForm.selector,{
				id : 'bizQueryBtn',
				iconCls:'icon-search',
				text : '查询',
				onClick : function(){
					if($('#bizAlarm_resource_form_isCheckedRadioTwo').val()=="isChecked"){
						var startStr = $('#bizAlarm_resource_form').find('#startTime').datetimebox('getValue').trim();
						var endStr = $('#bizAlarm_resource_form').find('#endTime').datetimebox('getValue').trim();
						var reg = /^(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[13579][26])00))-02-29) (20|21|22|23|[0-1]?\d):[0-5]?\d:[0-5]?\d$/
						if(!reg.test(startStr)||!reg.test(endStr)){
							alert('输入日期时间格式不正确');
						}else{
							var dateStart = new Date(startStr.replace(/-/g,"/"));
							var dateEnd = new Date(endStr.replace(/-/g,"/"));
							var timeSub = dateEnd.getTime() - dateStart.getTime();
							if(timeSub < 0){
								alert('开始日期不能晚于结束日期');
							}else{
								that.alarmDataGrid.selector.datagrid('reload');
							}
						}
					}else{
						that.alarmDataGrid.selector.datagrid('reload');
					}
				  }
				 },{
				 id : 'bizResetBtn',
					iconCls:'icon-reset',
					text : '重置',
					onClick : function(){
						queryForm.reset();
						
						$('#bizAlarm_resource_form_isCheckedRadioTwo').val('');
						$('#bizAlarm_resource_form_isCheckedRadioOne').val('');
					 }
				 }];

			this.alarmDataGrid = oc.ui.datagrid({
				pagination:true,
				selector : this.mainDiv.find("#alarm_datagrid"),
/*				url :this.cfg.nodeId==undefined?oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
						this.cfg.id+"&name="+this.cfg.name):oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
								this.cfg.id+"&name="+this.cfg.name+"&nodeId="+this.cfg.nodeId),
				fit : true,*/
				queryForm : queryForm,
				queryConditionPrefix:'',
				url:that.cfg.nodeId==undefined?oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
						that.cfg.id+"&name="+that.cfg.name+"&restore="+'false'):oc.resource.getUrl('portal/business/alarmInfo/selectWarnViewPage.htm?id='+
								that.cfg.id+"&name="+that.cfg.name+"&nodeId="+that.cfg.nodeId+"&restore="+'false'),
				fit : true,
				columns : [[{field : 'id', title : '', hidden : true },
		           { field : 'content', title : '告警内容', sortable : true, align : 'left', width : '35%',
					formatter:function(value,row,index){
						var result = "";
						if(row.level=="CRITICAL"){
							result+="<span class='light-ico redlight'></span><span title='"+value+"'>"+value+"</span>";
						}else if(row.level=="SERIOUS"){
							result+="<span class='light-ico orangelight'></span><span title='"+value+"'>"+value+"</span>";
						}else if(row.level=="WARN"){
							result+="<span class='light-ico yellowlight'></span><span title='"+value+"'>"+value+"</span>";
						}else if(row.level=="NORMAL"){
							result+="<span class='light-ico greenlight'></span><span title='"+value+"'>"+value+"</span>";
						}
						//已恢复告警
						if(row.dataClass == 2){
							result ="<span class='ico recovery-ico'></span><span title='"+value+"'>"+value+"</span>"
						}
						return result;
					}
		           }, { field : 'name', title : '告警来源', sortable : true, align : 'left', width : '20%'
		           }, { field : 'warnTime', title : '产生时间', align : 'left', width : '25%',
					formatter:function(value,row,index){
		        		 if(value != null){
				             return  formatDate(new Date(value));
		        		 }
		        		 return null;
		        	 },sortable:true
		           }, { field : 'operate', title : '发送记录', align : 'left', width : '20%', 
					formatter:function(value,row,rowIndex){ 	         
		        		 return '<a id="'+row.id+'" class="alarm_detail"></a>'; 
					}
				}]],
			    onLoadSuccess:function(data){

					$('#resourceToolBar').parent().find('.easyui-fluid>.datagrid-wrap>.datagrid-toolbar').children().eq(3).hide();
					$('#resourceToolBar').parent().find('.easyui-fluid>.datagrid-wrap>.datagrid-toolbar').children().eq(4).hide();

					//高度调整
					var paperHgt = $('#resourceToolBar').parent().find('.easyui-fluid>.datagrid-wrap>.datagrid-pager').height();
					
					var fluid = $('#resourceToolBar').parent().find('.easyui-fluid>.datagrid-wrap>.datagrid-view');
					$(fluid).height(335);
					var fluidHgt = $('#resourceToolBar').parent().find('.easyui-fluid>.datagrid-wrap>.datagrid-view').height();

					var bodyHgt = $('#resourceToolBar').parent().find('.easyui-fluid>.datagrid-wrap>.datagrid-view>.datagrid-view2>.datagrid-body').height();
					var bodyDiv = $('#resourceToolBar').parent().find('.easyui-fluid>.datagrid-wrap>.datagrid-view>.datagrid-view2>.datagrid-body');

					var toolBarDiv = $('#resourceToolBar');
					var toolBarHgt = $('#resourceToolBar').height();

					//bizAlarm_resource_form
					
					//this.mainDiv.find('.easyui-fluid>.datagrid-wrap>.datagrid-view').height('335px;');
					//console.log(this.mainDiv.find('.easyui-fluid>.datagrid-wrap>.datagrid-view'));
					
					if(Idx==1){
						$(toolBarDiv).attr('style','display:none;');					
						$(fluid).height(fluidHgt+toolBarHgt);
						$(bodyDiv).height(bodyHgt-paperHgt);
					}else{
						$(toolBarDiv).attr('style','display:block;');
						$(bodyDiv).height(bodyHgt-paperHgt-toolBarHgt);
					}

					 $('#resourceRadioOne').on('click', function(){  
			    		 $('#bizAlarm_resource_form_isCheckedRadioOne').val('isChecked');
			    		 $('#bizAlarm_resource_form_isCheckedRadioTwo').val('');
			    	 });
	 		    	 $('#resourceRadioTwo').on('click', function(){  
			    		 $('#bizAlarm_resource_form_isCheckedRadioTwo').val('isChecked');
			    		 $('#bizAlarm_resource_form_isCheckedRadioOne').val('');
			    	 });
					
					 $(".alarm_detail").linkbutton({ plain:true, iconCls:'light_blue icon-tactics' });
					 $('.alarm_detail').on('click', function(){   
						 var alarmId =$(this).attr('id');
                          oc.resource.loadScript('resource/module/alarm-management/js/alarm-management-detail.js',function(){
                        	  oc.alarm.detail.dialog.open(alarmId,null,false);
      	     			});
		         	  });
				},
				octoolbar : {
					left : rightButtonArray
				}
			});
				$('#alarm_management_main_tabs').tabs('resize',{ 
					width: 796,
					height:33,
				});
		
			$('.bizLightAlarmClass').each(function(i){
				switch ($(this).attr('id')) {
				case 'all':
					$(this).on('click', function(){
						that.addStatusLightActive(this);
						that.alarmDataGrid.selector.datagrid('reload',{status:status});
					});
					break;
				case 'down':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.alarmDataGrid.selector.datagrid('reload',{status:'down'});
					});
					break;
				case 'metric_error':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.alarmDataGrid.selector.datagrid('reload',{status:'metric_error'});
					});
					break;
				case 'metric_warn':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.alarmDataGrid.selector.datagrid('reload',{status:'metric_warn'});
					});
					break;	
				case 'metric_recover':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.alarmDataGrid.selector.datagrid('reload',{status:'metric_recover'});
					});
					break;
				}
			});
		},

		loadAlarmDataGrid : function(url){
				$('#alarm_datagrid').datagrid({
					url : url
				});
		},
		addStatusLightActive : function(light){
			$(light).parent().parent().find("a").removeClass('active');
			$(light).find("a").addClass('active');
		}
	};
	
	oc.ns('oc.module.biz.ser.alarm');
	oc.module.biz.ser.alarm={
		open:function(cfg){
			new alarmManMainClass(cfg).open();
		}
	}
})(jQuery);