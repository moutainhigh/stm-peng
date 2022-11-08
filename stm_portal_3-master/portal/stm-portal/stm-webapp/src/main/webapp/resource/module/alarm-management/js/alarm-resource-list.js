(function($) {
	function alarmListClass() {
	}
	alarmListClass.prototype = {
		constructor : alarmListClass,	
		onselects: false,
		open : function(resourceId) {
			var Idx=0;
			var resourceMain = $('#alarm_resource_list_id').attr('id', oc.util.generateId());
			var that = this;
			$('#alarm_resource_form_resourceId').val(resourceId);
			$('#alarm_resource_form_onClick').val('isOnClick');
			function formatDate(date){
				return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
			}
			// 表格查询表单对象
			var queryForm = oc.ui.form({
				selector : resourceMain.find('#alarm_resource_form'),
				combobox : [{
					selector : resourceMain.find('[name=queryTime]'),
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
					selector:resourceMain.find('[name="startTime"]'),
					editable:false,
					onSelect:function(){
						 $('#resourceRadioTwo').click();
					}
					},{
					selector:resourceMain.find('[name="endTime"]'),
					editable:false,
					onSelect:function(){
						 $('#resourceRadioTwo').click();
					}
				}]
			});
			resourceMain.find('#alarm_resource_tabs').attr('id',resourceMain.domId).addClass("window-tabsbg").tabs({
				fit : false,
				width : "100%",
				onSelect : function(title,index) {
				
					Idx=index;
					var url = oc.resource.getUrl('alarm/alarmManagement/getNotRestoreAlarm.htm');
					if(index == 0){
						url = oc.resource.getUrl('alarm/alarmManagement/getNotRestoreAlarm.htm');
						if(that.onselects){
							var option = $('#alarm_resource_datagrid').datagrid("options");
							option.columns[0][2].width = '10%';
							option.columns[0][3].width = '15%';
							option.columns[0][4].width = '15%';
							option.columns[0][6].width = '9%';
							option.columns[0][7].width = '9%';
							option.columns[0][2].hidden = false;
							option.columns[0][3].hidden = false;
							option.columns[0][4].hidden = false;
							option.columns[0][5].hidden = true;
							option.columns[0][7].hidden = false;
							option.columns[0][0].hidden = false;
							option.columns[0][8].hidden = false;
							option.columns[0][9].hidden = false;
							option.columns[0][10].hidden = true;
							option.columns[0][11].hidden = true;
							option.columns[0][12].hidden = true;
							$('#csTime').show();
							$('#hfTime').hide();
						}
					}else if(index == 1){
						url = oc.resource.getUrl('alarm/alarmManagement/getRestoreAlarm.htm');
						var option = $('#alarm_resource_datagrid').datagrid("options");
						option.columns[0][2].width = '10%';
						option.columns[0][5].width = '20%';
						option.columns[0][7].width = '9%';
						option.columns[0][9].width = '45%';
						option.columns[0][2].hidden = false;
						option.columns[0][3].hidden = true;
						option.columns[0][4].hidden = true;
						option.columns[0][5].hidden = false;
						option.columns[0][6].hidden = false;
						option.columns[0][8].hidden = true;//modify by sunhailiang on 2017.6.3
						option.columns[0][7].hidden = true;
						option.columns[0][9].hidden = false;
						option.columns[0][0].hidden = true;
						option.columns[0][10].hidden = true;
						option.columns[0][11].hidden = true;
						option.columns[0][12].hidden = true;
						that.onselects = true;
						$('#csTime').hide();
						$('#hfTime').show();
						
					}else if(index == 2){
						url = oc.resource.getUrl('alarm/alarmManagement/getSyslogAlarm.htm');
					
						var option = $('#alarm_resource_datagrid').datagrid("options");
						option.columns[0][9].width = '33%';
						option.columns[0][10].width = '15%';
						option.columns[0][9].hidden = false;
						option.columns[0][10].hidden = false;
						option.columns[0][11].hidden = false;
						option.columns[0][12].hidden = false;
						option.columns[0][3].hidden = false;
						option.columns[0][2].hidden = true;
						option.columns[0][4].hidden = true;
						option.columns[0][5].hidden = true;
						option.columns[0][7].hidden = true;
						option.columns[0][8].hidden = true;
						option.columns[0][0].hidden = false;
						that.onselects = true;
						//查询所有
						$('#csTime').show();
						$('#hfTime').hide();
						
					}else if(index == 3){//第三方告警查询
						url = oc.resource.getUrl('alarm/alarmManagement/getThirdPartyAlarm.htm');
						var option = $('#alarm_resource_datagrid').datagrid("options");
//						option.columns[0][2].width = '10%';
//						option.columns[0][3].width = '15%';
						option.columns[0][9].width = '33%';
						option.columns[0][10].width = '15%';
						option.columns[0][9].hidden = false;
						option.columns[0][10].hidden = false;
						option.columns[0][11].hidden = false;
						option.columns[0][12].hidden = false;
						option.columns[0][3].hidden = false;
						option.columns[0][2].hidden = true;
						option.columns[0][4].hidden = true;
						option.columns[0][5].hidden = true;
						option.columns[0][7].hidden = true;
						option.columns[0][8].hidden = true;
						option.columns[0][0].hidden = false;
						that.onselects = true;
						$('#csTime').show();
						$('#hfTime').hide();
					}
					that.loadAlarmDataGrid(url);
					var rights=$("#res_alarm div.right");
					var lefts=$("#res_alarm div.left");
					if(index==1 || index==2){
						$.each(rights,function(i,item){
							if(item.lastChild.id=='queryBtn' || item.lastChild.id=='resetBtn'){
								$(this).show();
							}
							if(item.lastChild.id=='sureBtn'){
								$(this).hide();
							}
						});
					}else{
						$.each(rights,function(i,item){
							var user = oc.index.getUser();
							if(user.domainUser || user.systemUser){
								if(item.lastChild.id=='sureBtn'){
									$(this).show();
									$(this).attr('style','padding-left:60px');
								}
							}else{
								if(item.lastChild.id=='sureBtn'){
									$(this).remove();
								}
							}
							
							
						});
					}
				}
			});
			var datagridDiv=$('#alarm_resource_datagrid');
			var rightButtonArray = [queryForm.selector,{
				id : 'queryBtn',
				iconCls:'icon-search',
				text : '查询',
				onClick : function(){
					if($('#alarm_resource_form_isCheckedRadioTwo').val()=="isChecked"){
						var startStr = $('#alarm_resource_form').find('#startTime').datetimebox('getValue').trim();
						var endStr = $('#alarm_resource_form').find('#endTime').datetimebox('getValue').trim();
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
								dataGrid.reLoad();
							}
						}
					}else{
						dataGrid.reLoad();
					}
				  }
				 },{
				 id : 'resetBtn',
					iconCls:'icon-reset',
					text : '重置',
					onClick : function(){
						queryForm.reset();
						 $('#alarm_resource_form_isCheckedRadioTwo').val('');
						 $('#alarm_resource_form_isCheckedRadioOne').val('');
					 }
				 }];
			
			if(oc.index._activeRight != 4){
				rightButtonArray.push({
					 id : 'sureBtn',
						iconCls:'fa fa-check',
						text : '确认告警',
						onClick : function(){
							//Idx
							var alarmInstanceIds = new Array();
							var sourceDiv=$("#res_alarm");
					
							sourceDiv.find("input[name='alarmId']:checked").each(function(){
								var info = {};
								var alarmId=$(this).val();
								var alarmId=$("input[alarmId='"+alarmId+"']").attr("alarmId") ;
								var resourceId=$("input[alarmId='"+alarmId+"']").attr("resourceId") ;
								var metricId=$("input[alarmId='"+alarmId+"']").attr("metricId") ;
								info.alarmId=alarmId;
								info.resourceId=resourceId;
								info.metricId=metricId;
								alarmInstanceIds.push(info);
							});
						
						if(alarmInstanceIds.length==0){
							alert("请选择要确认的告警信息！");
						}else{
							oc.ui.confirm("是否对选中告警进行确认操作？",function() {
								oc.util.ajax({
						    		url: oc.resource.getUrl('alarm/alarmManagement/SureAlarmByIds.htm'),
						    		data:{alarmInstanceIds:alarmInstanceIds,type:Idx},
						    		successMsg:null,
						    		success:function(data){
						    			if(data.code==200){
						    				if(data.data=="success"){
						    					alert("确认告警成功！");
						    					dataGrid.reLoad();
						    				}else if(data.data=="done"){
						    					alert("确认告警成功！");
						    					dataGrid.reLoad();
						    				}else{
						    					alert("确认告警失败！");
						    				}
						    				
						    			}else{
						    				alert("请选择需要确认的告警！");
						    			}
						    		}
						    	});
							});
					
						}
						  
							//alarm/alarmManagement/SureAlarmByIds
						}
					 });
			}
			
			var dataGrid = oc.ui.datagrid({
	 			selector:datagridDiv,
	 			hideSearch:true,
	 			hideReset:true,
	 			octoolbar:{
	 				right:rightButtonArray
	 				
	 			},
	 			queryForm:queryForm,
	 			url:oc.resource.getUrl('alarm/alarmManagement/getNotRestoreAlarm.htm'),
	 			columns:[[
	 			     {
	 			    	 field : 'alarmId',
	 			    	 title : '',
	 			    	 hidden : false,
	 			    	 checkbox:true,
	 					isDisabled:function(value, row, index){
							if(row.sysID=="BUSSINESS"){
								return true;
							}else{
								return false;
							}
				        }/*,
	 					isDisabled:function(value, row, index){
							return !row.hasRight;
				        }*/
	 			     },
	 			     {
	 			    	 field : 'dataClass',
	 			    	 title : '',
	 			    	 hidden : true
	 			     },
	 			     {
	 			    	 field:'alarmStatus',
	 			    	 title:'级别',
	 			    	 sortable:true,
	 			    	 align:'left',
	 			    	 width:'10%',
	 			    	 formatter:function(value,row,rowIndex){
	 			    		 if(row.instanceStatus=="red"){
	 			    			 value = "致命";
	 			    		 }else if(row.instanceStatus == "orange"){
	 			    			 value ="严重";
	 			    		 }else if(row.instanceStatus == "yellow"){
	 			    			 value ="警告";
	 			    		 }else if(row.instanceStatus == "green"){
	 			    			 value ="正常";
	 			    		 }else{
//	 			    			 value ="未知"; 
	 			    			 value ="正常";
	 			    		 }
	 			    		 if(row.dataClass==2){//告警已恢复
	 			    			 if(row.handType=="CONFIRM"){//确认告警
	 			    				 return '<label class="" title="已确认" ><span class="fa fa-history paddingr5 light_blue"></span>'+value+'</label>';//ico recovery-time-ico
	 			    			 }else{
	 			    				 return '<label class="" title="已恢复"><span class="fa fa-retweet paddingr5 light_blue"></span>'+value+'</label>'; //ico recovery-ico
	 			    			 }
	 			    		 }else{
	 			    			 return '<label class="light-ico '+row.instanceStatus+'light" >'+value+'</label>';
	 			    		 	}
	 			    		 }
	 			     },
	 		         {
			        	 field : 'collectionTime',
			        	 title : '产生时间',
			        	 formatter:function(value,row,index){
			        		 if(value != null){
			        			 return  formatDate(new Date(value));
			        		 }
			        		 return null;
			        	 },
			        	 sortable:true,
			        	 width:'15%',
			        	 align:'left'
	 		         },
	 		         {
	 		        	 field : 'acquisitionTime',
	 		        	 title : '最近采集时间',
	 		        	 formatter:function(value,row,index){
	 		        		 if(value != null){
	 		        			 return  formatDate(new Date(value));
	 		        		 }
	 		        		 return null;
	 		        	 },
	 		        	 width:'15%',
	 		        	 align:'left'
	 		         },
	 			 	 {
	 		        	 field : 'recoveryTime',
	 		        	 title : '告警恢复时间',
	 		        	 hidden:true,
	 		        	 formatter:function(value,row,index){
	 		        		 if(value != null){
	 		        			 return  formatDate(new Date(value));
	 		        		 }
	 		        		 return null;
	 		        	 },
	 		        	 sortable:true,
	 		        	 width:'15%',
	 		        	 align:'left'
	 			 	 },	 		 			         
	 			 	 {
	 			 		 field:'alarmType',
	 			 		 title:'监控类型',
	 			 		 sortable:true,
	 			 		 align:'left',
	 			 		 width:'20%'
	 			 	 },
	 			 	 {
	 			 		 field : 'itmsData',
	 			 		 title : '工单状态',
	 			 		 sortable : true,
	 			 		 width : '9%'
	 			 	 },
	 			 	 {
	 			 		 field:'snapShotJSON',
	 			 		 title:'快照',
	 			 		 align:'left',
	 			 		 width:'5%',
	 			 		 ellipsis:true,
	 			 		 formatter : function(value, row, index) {
	 						if(value == null || value == undefined || value == ''){
								return '<a id="'+row.alarmId +'" class="fa  fa-camera-retro blue_gray"></a>';
							}else{
								return '<a id="'+row.alarmId +'" class="fa  fa-camera-retro light_blue"></a>';
							}
		        	     }
	 			 	 },
	 			 	 {
	 			 		 field:'alarmContent',
	 			 		 title:'告警内容',
	 			 		 sortable:true,
	 			 		 align:'left',
	 			 		 width:'26%',
	 			 		 ellipsis:true,
	 			 		 formatter : function(value, row, index) {
	 			 			 return '<span style="cursor:pointer;" title="' + value + '">' + value + '</span>';
	 			 		 }
	 			 	 }, {
	 			 		 field:'resourceName',
	 			 		 title:'告警来源',
	 			 		 sortable:true,
	 			 		 align:'left',
	 			 		 width:'10%',
	 			 		hidden:true,
	 			 		 ellipsis:true,
	 			 		 formatter : function(value, row, index) {
	 			 			 return '<span style="cursor:pointer;" title="' + value + '">' + value + '</span>';
	 			 		 }
	 			 	 }, {
	 			 		 field:'ipAddress',
	 			 		 title:'IP地址',
	 			 		 sortable:true,
	 			 		 align:'left',
	 			 		 width:'10%',
	 			 		hidden:true,
	 			 		 ellipsis:true,
	 			 		 formatter : function(value, row, index) {
	 			 			 return '<span style="cursor:pointer;" title="' + value + '">' + value + '</span>';
	 			 		 }
	 			 	 }, {
	 			 		field : 'yy',
						title : '发送记录',
						hidden:true,
						align : 'left',
						width : '5%', formatter:function(value,row,rowIndex){ 	         
							return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail"></a>'; 
		        	     }
	 			 	 },{
	 					field : 'json',
						align : 'left',
						hidden:true,
						title : '',
						width : '4%',
						formatter:function(value, row, index){
					    	var formatterString = '';
							
//							formatterString='<input alarmId="'+row.alarmId+'" style="text-align:left;height:auto;" class="datagrid-cell datagrid-cell-c2-json" value="'+row.alarmId+","+row.metricId+','+row.resourceId+'"/>'
							formatterString='<input alarmId="'+row.alarmId+'" style="text-align:left;height:auto;" class="datagrid-cell datagrid-cell-c2-json" alarmId="'+row.alarmId+'" metricId="'+row.metricId+'" resourceId="'+row.resourceId+'" />'
//							formatterString='<input alarmId="'+row.alarmId+'" style="text-align:left;height:auto;" class="datagrid-cell datagrid-cell-c2-json" value="{alarmId:'+row.alarmId+',resourceId:'+row.resourceId+',metricId:'+row.metricId+'}"/>'
							return formatterString;
				        }
					} 
	 		     ]],
	 		     onLoadSuccess:function(data){
	 		    	 $('#resourceRadioOne').on('click', function(){  
			    		 $('#alarm_resource_form_isCheckedRadioOne').val('isChecked');
			    		 $('#alarm_resource_form_isCheckedRadioTwo').val('');
			    	 });
	 		    	 $('#resourceRadioTwo').on('click', function(){  
			    		 $('#alarm_resource_form_isCheckedRadioTwo').val('isChecked');
			    		 $('#alarm_resource_form_isCheckedRadioOne').val('');
			    	 });
	 		    	$(".alarm_detail").linkbutton({ plain:true, iconCls:'ico-mark' });
					$('.alarm_detail').on('click', function(){   
						var alarmId =$(this).attr('id');
						var type =$(this).attr('name');
						oc.resource.loadScript('resource/module/alarm-management/js/alarm-management-detail.js',function(){
							oc.alarm.detail.dialog.open(alarmId,type,true);
						});
					});
	 		    	var rights=$("#res_alarm div.right");
					var lefts=$("#res_alarm div.left");

					$.each(rights,function(i,item){
						var user = oc.index.getUser();
						if(user.domainUser || user.systemUser){
							if(Idx==0 && item.lastChild.id=='sureBtn'){
								$(this).attr('style','padding-left:60px');
							}
						}else{
							if(item.lastChild.id=='sureBtn'){
								$(this).hide();
							}
							$('#res_alarm [name="alarmId"]').hide();
							$('#res_alarm [field="alarmId"]').hide()
						}
						
						
					});
	 		    	 
	 		     },
	 		     onClickCell : function(rowIndex, field, value){
	 		    	 if(field == 'alarmContent'){
	 		    		 var row = $(this).datagrid('getRows')[rowIndex];
	 		    		 var alarmId = row.alarmId;
	 		    		 var type = row.dataClass;
	 		    		var sysId = row.sysID;
	 		    		 oc.resource.loadScript('resource/module/alarm-management/js/alarm-detailed-information.js',function(){
	 		    			 oc.alarm.detail.inform.open(alarmId,type,sysId);
	 		    		 });
	 		    		 
	 		    	 }
	 		    	 if(field == 'snapShotJSON'){
	 		    		 var row = $(this).datagrid('getRows')[rowIndex];
	 		    		 var snapShotJSON = row.snapShotJSON;
	 		    		 if(snapShotJSON != null && snapShotJSON != undefined){
	 		    			 oc.resource.loadScript('resource/module/alarm-management/js/alarm-querySnapShot.js', function(){
	 		    				 oc.module.alarm.management.querysnapshot.open(row);
	 		    			 });
	 		    		 }
	 		    	 }
	 		     }
	 		});
		},
		loadAlarmDataGrid : function(url){
				$('#alarm_resource_datagrid').datagrid({
					url : url
				});
		}
	};

	 //命名空间
	oc.ns('oc.alarm.resource.list');
	oc.alarm.resource.list={
			open:function(resourceId){
				return new alarmListClass().open(resourceId);
			}
	};
	
})(jQuery);