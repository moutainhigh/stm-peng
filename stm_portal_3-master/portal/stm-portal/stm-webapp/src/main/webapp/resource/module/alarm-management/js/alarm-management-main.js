(function($) {
	var refreshTime=30000;//定时刷新时间
	var loadIndex=new Array();
	function alarmManMainClass() {
	}
	alarmManMainClass.prototype = {
		constructor : alarmManMainClass,
		cfg : undefined,
		mainDiv : undefined,
		tabs : undefined,
		queryForm : undefined,
		alarmDataGrid : undefined,
		flag : false,
		freshBtn : undefined,
		ipMenuButton : undefined,
		isSwitchTabs : false,
		onselects: false,
		myInterval : undefined,
		Idx:0,
		open : function() {
			this.mainDiv = $('#alarm_management_main_id').attr('id', oc.util.generateId());
			var that = this;
		
			var tabDiv = this.mainDiv.find("#alarm_management_tabs");
			this.tabs = tabDiv.tabs({
				fit : false,
				width : "100%",
				onSelect : function(title,index) {
										Idx=index;
					var url = oc.resource.getUrl('alarm/alarmManagement/getNotRestoreAlarm.htm');
					var user = oc.index.getUser();
					
					if(index == 0){
						that.removeStatusLightActive();
						url = oc.resource.getUrl('alarm/alarmManagement/getNotRestoreAlarm.htm');
						$('.lightAlarmClass').show();
						if(that.onselects){
							var option = $('#alarm_datagrid').datagrid("options");
							option.columns[0][9].hidden = true;
							option.columns[0][8].hidden = false;
							option.columns[0][7].hidden = false;
							option.columns[0][10].hidden = false;
							option.columns[0][11].hidden = true;
//							option.columns[0][12].hidden = false; cs
							option.columns[0][6].hidden = false;
							option.columns[0][2].width = '29%';
							option.columns[0][3].width = '12%';
							option.columns[0][9].width = '10%';
							option.columns[0][7].width = '10%';
							if(user.domainUser || user.systemUser){
								option.columns[0][0].hidden = false;
							}else{
								option.columns[0][0].hidden = true;
							}
							//查询所有
							that.queryForm._initVal.queryTime=0;
						}
					}else if(index == 1){
						that.removeStatusLightActive();
						url = oc.resource.getUrl('alarm/alarmManagement/getRestoreAlarm.htm');
						$('.lightAlarmClass').hide();
						var option = $('#alarm_datagrid').datagrid("options");
						option.columns[0][2].width = '43%';
						option.columns[0][3].width = '21%';
						option.columns[0][9].width = '15%';
						option.columns[0][6].hidden = true;
						option.columns[0][7].hidden = true;
						option.columns[0][8].hidden = true;
						option.columns[0][9].hidden = false;
						option.columns[0][10].hidden = false;
						option.columns[0][11].hidden = true;
//						option.columns[0][12].hidden = true;
						option.columns[0][0].hidden = true;
						that.onselects = true;
						//已恢复告警默认查询1天的数据
						that.queryForm._initVal.queryTime=4;
						var user = oc.index.getUser();
					
					}else if(index == 2){
						that.removeStatusLightActive();
						url = oc.resource.getUrl('alarm/alarmManagement/getSyslogAlarm.htm');
						$('.lightAlarmClass').show();
						var option = $('#alarm_datagrid').datagrid("options");
						option.columns[0][2].width = '43%';
						option.columns[0][3].width = '21%';
						option.columns[0][7].width = '15%';
						option.columns[0][6].hidden = true;
						option.columns[0][7].hidden = false;
						option.columns[0][9].hidden = true;
						option.columns[0][8].hidden = true;
						option.columns[0][10].hidden = false;
						option.columns[0][11].hidden = true;
//						option.columns[0][12].hidden = true;
						option.columns[0][0].hidden = true;
						that.onselects = true;
						//查询所有
						that.queryForm._initVal.queryTime=0;
					}else if(index == 3){
						that.removeStatusLightActive();
						url = oc.resource.getUrl('alarm/alarmManagement/getThirdPartyAlarm.htm');
						$('.lightAlarmClass').show();
						var option = $('#alarm_datagrid').datagrid("options");
						option.columns[0][2].width = '40%';
						option.columns[0][3].width = '21%';
						option.columns[0][7].width = '15%';
						option.columns[0][6].hidden = true;
						option.columns[0][7].hidden = false;
						option.columns[0][9].hidden = true;
						option.columns[0][8].hidden = true;
						option.columns[0][10].hidden = false;
						option.columns[0][11].hidden = true;
//						option.columns[0][12].hidden = true;
						if(user.domainUser || user.systemUser){
							option.columns[0][0].hidden = false;
						}else{
							option.columns[0][0].hidden = true;
						}
						that.onselects = true;
						//查询所有
						that.queryForm._initVal.queryTime=0;
					}
					oc.ui.progress();
					that.mainDiv.find('.customTimeBucket').hide();
					that.mainDiv.find("[name='prentCategory']").val('');
					that.mainDiv.find("[name='childCategory']").val('');
					that.loadAlarmDataGrid(url);
					var lefts=$('#resource_alarm div.left');
					if(Idx==1 || Idx==2){//无告警确认
						$.each(lefts,function(i,item){
							
							if(item.lastChild.id=='res_sure_Btn'  ){
								$(this).hide();
							}
						});
					}else{
						$.each(lefts,function(i,item){
							var user = oc.index.getUser();
							if(user.domainUser || user.systemUser){
								if(item.lastChild.id=='res_sure_Btn'  ){
									$(this).show();
								}
							}else{//无权限操作
								$.each(lefts,function(i,item){
								
									if(item.lastChild.id=='res_sure_Btn' || item.lastChild.id=='res_export_Btn' ){
										$(this).remove();
									}
								});
							}
						});
						
				
				}
					
				}
			});
			
			
			// 表格查询表单对象
			this.queryForm = oc.ui.form({
				selector : $('#alarm_management_main_form'),
				combotree : [{
					selector : $('#alarm_management_main_form').find('.prentCategorySelect'),
					url:oc.resource.getUrl('alarm/alarmManagement/getPrentStrategyType.htm'),
					width:130,
					placeholder:'    --请选择--',
					filter:function(data){
						var treeNode_business_parent = {"id":"BUSSINESS","name":"业务","state":"closed","type":"1"};
						var treeNode_IP_MAC_PORT_parent = {"id":"IP_MAC_PORT","name":"IP-MAC-PORT","state":"closed","type":"1"};
						var treeNode_syslog_parent = {"id":"SYSLOG","name":"Syslog","state":"closed","type":"1"};
						var treeNode_trap_parent = {"id":"TRAP","name":"Trap","state":"closed","type":"1"};
						var treeNode_config_parent = {"id":"CONFIG_MANAGER","name":"配置文件","state":"closed","type":"1"};
						
						if(Idx==0 || Idx==1){
							data.data.push(treeNode_business_parent);
						/*	if(Idx==1){
								data.data.push(treeNode_IP_MAC_PORT_parent);
								data.data.push(treeNode_syslog_parent);
								data.data.push(treeNode_trap_parent);
								data.data.push(treeNode_config_parent);	
							}*/
						}else if(Idx==2){
							data.data.splice(0,data.data.length);
							data.data.push(treeNode_IP_MAC_PORT_parent);
							data.data.push(treeNode_syslog_parent);
							data.data.push(treeNode_trap_parent);
							data.data.push(treeNode_config_parent);
						}else if(Idx==3){
							data.data.splice(0,data.data.length);
						}
						return data.data;
					},
					onChange : function(newValue, oldValue, newJson, oldJson){
						if(newJson.type == '1'){
							that.mainDiv.find("[name='prentCategory']").val(newJson.id);
							that.mainDiv.find("[name='childCategory']").val('');
						}else if(newJson.type == '2'){
							that.mainDiv.find("[name='prentCategory']").val('');
							that.mainDiv.find("[name='childCategory']").val(newJson.id);
						}else{
							that.mainDiv.find("[name='prentCategory']").val('');
							that.mainDiv.find("[name='childCategory']").val('');
						}
					}
				}],
				combobox : [
				    {
						selector : '[name=queryTime]',
						fit : false,
						width:88,
						value : 1,
						data : [{
							id : '0',
							name : '全部时间',
							selected : true
						}, {
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
						}, {
							id : '6',
							name : '自定义时间'
						} ],
						placeholder:null,
						onHidePanel:function(){
							//-------------用于绑定enter查询事件---------------
							that.mainDiv.find('#iPorNameId').focus();
						},
						onChange : function(id) {
							if(id == '6'){
								that.mainDiv.find('.customTimeBucket').show();
							}else{
								that.mainDiv.find('.customTimeBucket').hide();
							}
						}
					} ],
					datetimebox : [ {
						selector:'[name="startTime"]',
						editable:false,
						width:150
					}, {
						selector:'[name="endTime"]',
						editable:false,
						width:150
					}]
			});
			
			
			//-------------绑定enter查询事件开始---------------
			var inputObj = this.mainDiv.find('#iPorNameId');
			inputObj.keyup(function(e){
				if(e.keyCode==13){
					if($('#alarm_management_main_form_isCheckedRadioTwo').val()=="isChecked"){
						var startStr = $('#alarm_management_main_form').find('#startTime').datetimebox('getValue').trim();
						var endStr = $('#alarm_management_main_form').find('#endTime').datetimebox('getValue').trim();
						var dateStart = new Date(startStr.replace(/-/g,"/"));
						var dateEnd = new Date(endStr.replace(/-/g,"/"));
						var timeSub = dateEnd.getTime() - dateStart.getTime();
						
						if(timeSub < 0){
							alert('开始日期不能晚于结束日期');
						}else{
							that.alarmDataGrid.reLoad();
						}
					}else{
						that.alarmDataGrid.reLoad();
					}
				}
			});
			
			//-------------绑定enter查询事件结束---------------
			
			
			$('.lightAlarmClass').each(function(i){
				switch ($(this).attr('id')) {
				case 'all':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.loadState('all');
					});
					break;
				case 'down':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.loadState('down');
					});
					break;
				case 'metric_error':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.loadState('metric_error');
					});
					break;
				case 'metric_warn':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.loadState('metric_warn');
					});
					break;	
				/*case 'metric_recover':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.loadState('metric_recover');
					});
					break;*/
				case 'metric_unkwon':
					$(this).on('click', function(){ 
						that.addStatusLightActive(this);
						that.loadState('metric_unkwon');
					});
					break;
				}
			});
		/*	var user = oc.index.getUser();
			if(user.domainUser || user.systemUser){
				$(".queryBtn")
			}*/
			$(".queryBtn").linkbutton('RenderLB', {
				iconCls : 'fa fa-search',
				onClick : function(){
					if($('#alarm_management_main_form_isCheckedRadioTwo').val()=="isChecked"){
						var startStr = $('#alarm_management_main_form').find('#startTime').datetimebox('getValue').trim();
						var endStr = $('#alarm_management_main_form').find('#endTime').datetimebox('getValue').trim();
						var dateStart = new Date(startStr.replace(/-/g,"/"));
						var dateEnd = new Date(endStr.replace(/-/g,"/"));
						var timeSub = dateEnd.getTime() - dateStart.getTime();
						
						if(timeSub < 0){
							alert('开始日期不能晚于结束日期');
						}else{
							that.alarmDataGrid.reLoad();
						}
					}else{
						that.alarmDataGrid.reLoad();
					}
				}
			});
			$(".resetBtn").linkbutton('RenderLB', {
				iconCls : 'l-btn-icon icon-reset',
				onClick : function(){
					that.removeStatusLightActive();
					that.queryForm.reset();
					that.mainDiv.find("[name='prentCategory']").val('');
					that.mainDiv.find("[name='childCategory']").val('');
					$('#searchIpInput').val('');
					$('#queryIpInput').val('');
				}
			});
			var datagirdHeight = parseFloat(this.mainDiv.height()) - parseFloat(tabDiv.height())-40 + 'px';
			$('#alarm_management_main_form_instanceStatus').val('all');
			
			function formatDate(date){
				return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'  '+(date.getHours()<10?'0'+String(date.getHours()):String(date.getHours()))+':'+(date.getMinutes()<10?'0'+String(date.getMinutes()):String(date.getMinutes()))+':'+(date.getSeconds()<10?'0'+String(date.getSeconds()):String(date.getSeconds()));
			}
			oc.ui.progress();
			var user = oc.index.getUser();
			
			this.alarmDataGrid = oc.ui.datagrid({
				selector : this.mainDiv.find("#alarm_datagrid"),
				queryForm : this.queryForm,
				url : oc.resource.getUrl('alarm/alarmManagement/getNotRestoreAlarm.htm'),
				fit : false,
				pageSize:$.cookie('pageSize_alarm-management-main')==null ? 15 : $.cookie('pageSize_alarm-management-main'),
				hideReset:true,
				hideSearch:true,
				width : "100%",
				height : datagirdHeight,
				columns : [[ {
					field : 'alarmId',
					align : 'left',
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
					formatter:function(value, row, index){
				    	var formatterString = '';
						console.info(row);
						formatterString='<input type="checkbox" name="alarmId" value="'+row.metricId+","+row.resourceId+'"/>';
						return formatterString;
			        }*/
				},{
					field : 'dataClass',
					align : 'left',
					title : '',
					hidden : true
				},{
					field : 'alarmContent',
					title : '告警内容',
//					sortable : true,
					align : 'left',
					width : '29%',
					ellipsis:true,
			        formatter:function(value,row,rowIndex){
//			        	console.info(row);
//			        	if(row.handType=='CONFIRM'){//确认恢复告警
//			        		
//			        	}else{//自动恢复
//			        		
//			        	}
			        	 var formatterString = '';
							if(value == null){
								formatterString = '<label style="cursor:pointer;" class="light-ico '+row.instanceStatus+'light" > </label>';
							}else{
								if(row.dataClass!=1){//其他
									if(row.dataClass==2){
										 if(value.length > 85){
										if(row.handType=='CONFIRM'){//确认恢复告警
												formatterString = '<label style="cursor:pointer;" class="ico recovery-time-ico" title="已确认"><label style="cursor:pointer;" title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars().substr(0, 85)+'...'+'</label>';
				                        	}else{
				                        		formatterString = '<label style="cursor:pointer;" class="ico recovery-ico" title="已恢复"><label style="cursor:pointer;"  title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars().substr(0, 85)+'...'+'</label>';
				                        	}
										 }else{
											 if(row.handType=='CONFIRM'){//确认恢复告警
												 formatterString = '<label style="cursor:pointer;" class="ico recovery-time-ico" title="已确认"></label><label style="cursor:pointer;"  title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars()+'</label>';
					                        	}else{
					                        		formatterString = '<label style="cursor:pointer;" class="ico recovery-ico" title="已恢复"></label><label style="cursor:pointer;"  title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars()+'</label>';
					                        	} 
											 
										 }
									}else{
										 if(value.length > 85){
					                        	formatterString = '<label style="cursor:pointer;" class="light-ico '+row.instanceStatus+'light" title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars().substr(0, 85)+'...'+'</label>';
					                        }else{
					                        	formatterString = '<label style="cursor:pointer;" class="light-ico '+row.instanceStatus+'light" title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars()+'</label>';
					                        }	
									}
									 
								}else{//告警未恢复
									  if(value.length > 45){
				                        	formatterString = '<label style="cursor:pointer;" class="light-ico '+row.instanceStatus+'light" title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars().substr(0, 45)+'...'+'</label>';
				                        }else{
				                        	formatterString = '<label style="cursor:pointer;" class="light-ico '+row.instanceStatus+'light" title="' + value.htmlspecialchars() + ' ">'+value.htmlspecialchars()+'</label>';
				                        }
								}
						 	}
							return formatterString; 		
			         }
				}, {
					field : 'resourceName',
					title : '告警来源',
					align : 'left',
					sortable : true,
					width : '12%',
					ellipsis:true,
			        formatter:function(value,row,rowIndex){
			        	var formatterString = '';
			        	if(value != null){
//			        		if(value.length > 15){
//			        			formatterString = '<label style="cursor:pointer;" title="' + value.htmlspecialchars() + '">'+ value.htmlspecialchars().substr(0, 15)+'...' +'</label>'
//			        		}else{
			        			formatterString = '<label  style="cursor:pointer;" title="' + value.htmlspecialchars() + '">'+ value.htmlspecialchars() +'</label>'
//			        		}
			        	}
			        	return formatterString; 
			        }
				}, {
					field : 'ipAddress',
					title : 'IP地址<span id="ipSearchInputButton" class="datagrid-title-ico">&nbsp;</span>',
					width : '8%'
				}, {
					field : 'alarmType',
					title : '监控类型',
					align : 'left',
					sortable : true,
					width : '10%'
				}
				, {
					field : 'itmsData',
					title : '工单状态',
					align : 'left',
					sortable : true,
					width : '6%'
				}, {
					field : 'collectionTime',
					title : '产生时间',
					align : 'left',
		        	 formatter:function(value,row,index){
		        		 if(value != null){
		        			 var formatterStr=  formatDate(new Date(value));
			        			return	 '<label  style="cursor:pointer;" title="' + formatterStr.htmlspecialchars() + '">'+ formatterStr.htmlspecialchars() +'</label>';
		        		 }
		        		 return null;
		        	 },sortable:true,width:'10%'
				}, {
					field : 'acquisitionTime',
					align : 'left',
					title : '最近采集时间',
		        	 formatter:function(value,row,index){
		        		 if(value != null){
		        		var formatterStr=  formatDate(new Date(value));
		        			return	 '<label  style="cursor:pointer;" title="' + formatterStr.htmlspecialchars() + '">'+ formatterStr.htmlspecialchars() +'</label>';
		        		 }
		        		 return null;
		            },width:'10%'
				}, {
					field : 'recoveryTime',
					align : 'left',
					title : '告警恢复时间',
					hidden:  true,
		        	 formatter:function(value,row,index){
		        		 if(value != null){
		        			 var formatterStr=  formatDate(new Date(value));
			        			return	 '<label  style="cursor:pointer;" title="' + formatterStr.htmlspecialchars() + '">'+ formatterStr.htmlspecialchars() +'</label>';
		        		 }
		        		 return null;
		            },sortable:true,width:'10%'
				}, 
				//需求要求回退为原来的样式
				{
				field : 'snapShotJSON',
				align : 'left',
				title : '操作',
				width : '10%', formatter:function(value,row,rowIndex){ 	//将操作三列合并
						if(Idx==0){
							if(value == null || value == undefined || value == ''){
								return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a><a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_repository" title="知识库"></a><a id="'+row.alarmId +'" class="snapgray" title="快照" ></a>';	
							}else{
								return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a><a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_repository" title="知识库"></a><a id="'+row.alarmId +'" class="snapblue" rowIndex='+value+' title="快照"  ></a>';
							}
							
						}else if(Idx==1){
							return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a>';
							
						}else if(Idx==2){
							return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a>';
						}else if(Idx==3){
							return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a>';	
						}
						
        	     }
			}, 
			/*	{
					field : 'yy',
					title : '发送记录',
//					hidden:true,
					align : 'left',
					width : '6%', formatter:function(value,row,rowIndex){ 	         
						return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail"></a>'; 
	        	     }
				},*//* {
					field : 'ss',
					align : 'left',
//					hidden:true,
					title : '知识库',
					width : '5%', formatter:function(value,row,rowIndex){ 	         
						return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_repository"></a>'; 
	        	     }
				},*/
//				{
//					field : 'snapShotJSON',
//					align : 'left',
//					title : '操作',
//					width : '10%', formatter:function(value,row,rowIndex){ 	//将操作三列合并
//							if(Idx==0){
//								if(value == null || value == undefined || value == ''){
//									return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a><a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_repository" title="知识库"></a><a id="'+row.alarmId +'" class="ico_styleaa ico_fast_photo_gray ico-fast_photo_alarm" title="快照" ></a>';	
//								}else{
//									return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a><a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_repository" title="知识库"></a><a id="'+row.alarmId +'" class="ico_styleaa ico_fast_photo ico-fast_photo_alarm" rowIndex='+rowIndex+' title="快照"  ></a>';
//								}
//								
//							}else if(Idx==1){
//								return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a><a id="'+row.alarmId +'" class="ico_styleaa ico_fast_photo_alarm_gray ico-fast_photo_alarm" title="快照"  ></a>';
//							}else if(Idx==2){
//								return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a>';
//							}else if(Idx==3){
//								return '<a id="'+row.alarmId+'" name="'+row.dataClass +'" class="alarm_detail" title="发送记录"></a>';	
//							}
//							
//	        	     }
//				}, 
				/*{
					field : 'snapShotJSON',
					align : 'left',
//					hidden:true,
					title : '快照',
					width : '4%', formatter:function(value,row,rowIndex){
						if(value == null || value == undefined || value == ''){
							return '<a id="'+row.alarmId +'" class="snapgray "></a>';
						}else{
							return '<a id="'+row.alarmId +'" class="snapblue "></a>';
						}
						
	        	     }
				},*/{
					field : 'json',
					align : 'left',
					hidden:true,
					title : '',
					width : '4%',
					formatter:function(value, row, index){
				    	var formatterString = '';
						
//						formatterString='<input alarmId="'+row.alarmId+'" style="text-align:left;height:auto;" class="datagrid-cell datagrid-cell-c2-json" value="'+row.alarmId+","+row.metricId+','+row.resourceId+'"/>'
						formatterString='<input alarmId="'+row.alarmId+'" style="text-align:left;height:auto;" class="datagrid-cell datagrid-cell-c2-json" alarmId="'+row.alarmId+'" metricId="'+row.metricId+'"  resourceId="'+row.resourceId+'" />'
//						formatterString='<input alarmId="'+row.alarmId+'" style="text-align:left;height:auto;" class="datagrid-cell datagrid-cell-c2-json" value="{alarmId:'+row.alarmId+',resourceId:'+row.resourceId+',metricId:'+row.metricId+'}"/>'
						return formatterString;
			        }
				} ]],
				onLoadSuccess:function(){
					var user = oc.index.getUser();
					if(user.domainUser || user.systemUser){
					}else{
						$(".oc-toolbar").eq(1).remove();
					}
			
					$.messager.progress('close');
					if(that.isSwitchTabs){
						//创建文本搜索框(IP)
						var filterMenu = $('<div style="width:175px;"></div>');
						filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter text-box" placeholder="搜索IP地址" id="searchIpInput" type="text" ></input></label></div>');
						
						that.ipMenuButton = that.mainDiv.find('#ipSearchInputButton').menubutton({
							menu: filterMenu
						});
						filterMenu.find('#searchIpInput').on('click',function(e){
							e.stopPropagation();
						});

						//支持ie回车事件 add by sunhailiang on 20170628
			            if(oc.util.isIE()){
						   filterMenu.find('#searchIpInput').keypress(function(e){
						   	  e = e || window.event;
							  e.stopPropagation();
							  if(e.keyCode == 13){ //绑定回车 
								  $(this).trigger('change');
							  }
						   });
						}

						filterMenu.find('#searchIpInput').on('change',function(e){
							//过滤资源实例
							var queryIp = $(e.target).val();
							if(queryIp){
								that.mainDiv.find('#queryIpInput').val(queryIp);
							}else{
								that.mainDiv.find('#queryIpInput').val('');
							}
							that.alarmDataGrid.reLoad();
						});
						if(that.mainDiv.find('#queryIpInput').val()){
							filterMenu.find('#searchIpInput').val(that.mainDiv.find('#queryIpInput').val());
						}
						that.isSwitchTabs = false;
					}
					$(".alarm_detail").linkbutton({ plain:true, iconCls:'light_blue fa fa-paper-plane' });
					$('.alarm_detail').on('click', function(){   
						var alarmId =$(this).attr('id');
						var type =$(this).attr('name');
						oc.resource.loadScript('resource/module/alarm-management/js/alarm-management-detail.js',function(){
							oc.alarm.detail.dialog.open(alarmId,type,true);
						});
					});
					$(".alarm_repository").linkbutton({ plain:true, iconCls:'light_blue icon-knowledge' });/* fa fa-graduation-cap*/
					$('.alarm_repository').on('click', function(){
						var alarmId =$(this).attr('id');
						oc.resource.loadScript('resource/module/alarm-management/js/alarm-knowledge-base.js',function(){
							oc.alarm.knowledge.base.open(alarmId);
						});
					});
					    //modify by sunhailiang on 2017.6.3
						$(".snapgray").linkbutton({ plain:true, iconCls:'fa fa-camera-retro blue_gray' });
						$(".snapblue").linkbutton({ plain:true, iconCls:'light_blue  fa  fa-camera-retro ' });
						$(".snapblue").on('click',function(){
							//debugger;
							var rowIndexTemp = $(this).attr('rowIndex');
							var row={};
							var snapShotJSON = rowIndexTemp;
							row.snapShotJSON=snapShotJSON;
							if(snapShotJSON != null && snapShotJSON != undefined){
								oc.resource.loadScript('resource/module/alarm-management/js/alarm-querySnapShot.js', function(){
									oc.module.alarm.management.querysnapshot.open(row);
								});
							}
						});
		/*			$('.ico-fast_photo_alarm').on('click', function(){
						var rowIndexTemp = $(this).attr('rowIndex');
						if(!rowIndexTemp){
							return ;
						}
						var row = $(this).datagrid('getRows')[rowIndexTemp];
						var snapShotJSON = row.snapShotJSON;
						if(snapShotJSON != null && snapShotJSON != undefined){
							oc.resource.loadScript('resource/module/alarm-management/js/alarm-querySnapShot.js', function(){
								oc.module.alarm.management.querysnapshot.open(row);
							});
						}
					});*/
						var lefts=$('#resource_alarm div.left');
						$.each(lefts,function(i,item){
							var user = oc.index.getUser();
							if(user.domainUser || user.systemUser){
								
							}else{
								if(item.lastChild.id=='res_sure_Btn' || item.lastChild.id=='res_export_Btn' ){
									$(this).remove();
								}	
							}
						/*	if(item.lastChild.id=='res_sure_Btn' || item.lastChild.id=='res_export_Btn' ){
								$(this).remove();
							}*/
							
						});
					if(that.myInterval != undefined){
						clearInterval(that.myInterval);
					}
					that.myInterval = setInterval(function(){//定时刷新告警信息 //clearInterval
						that.alarmDataGrid.reLoad();
					},refreshTime);
					var tasks = oc.index.indexLayout.data("tasks");
					if(tasks && tasks.length > 0){
						oc.index.indexLayout.data("tasks").push(that.myInterval);
					}else{
						tasks = new Array();
						tasks.push(that.myInterval);
						oc.index.indexLayout.data("tasks", tasks);
					}
			
				},
				onLoadError: function () {
			        alert("告警数据获取失败，请检查服务器状态！");
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
					};
					if(field == 'resourceName'){
						var row = $(this).datagrid('getRows')[rowIndex];
						var resourceId = row.resourceId;
						var sysID=row.sysID;
						if(sysID=="LINK"){
							oc.resource.loadScripts([
						                                "resource/module/topo/unitTransform.js",
						                                "resource/module/topo/contextMenu/topoLinkInfoChart.js",
						                                "resource/module/topo/contextMenu/TopoLinkInfo.js"
						                            ], function () {
						                                new TopoLinkInfo({
						                                    type: "map",
						                                    onLoad: function () {
						                                        var $this = this;

						                                        var showMetric = $('#showMetric');
						                                        var showMetricChartsDiv = showMetric.find('#showMetricChartsDiv');
						                                        var showHideChart = showMetric.find('#infoCharts').html('指标信息图表');
						                                        showMetricChartsDiv.height(showHideChart.parent().height());
						                                        var showMetricDatagridDivHeight;
						                                        showMetricDatagridDivHeight = showMetric.height() - 10;
						                                        var showMetricDatagridDiv = showMetric.find('#showMetricDatagridDiv').height(showMetricDatagridDivHeight);
						                                        showMetricDatagridDiv.find('#showMetricDatagridParent').height(showMetricDatagridDivHeight - showHideChart.parent().height() - 15);
						                                        showMetricDatagridDiv.find('#showMetricDatagridParent').attr('style', 'width:100%;height:99%;');

						                                        //初始化曲线图
						                                        var chart = new TopoLinkInfoChart(this.fields.chart, 1);
						                                        showMetricChartsDiv.attr('style', 'display:none;');
						                                        //绑定chartDiv收缩
						                                        showMetricChartsDiv.find('.w-table-title').on('click', function () {
						                                            $this.showHideChartMethod(showMetric, showMetricDatagridDiv, showMetricChartsDiv, showHideChart);
						                                            showMetricChartsDiv.attr('style', 'display:none;');
						                                        });
						                                        this.root.on("click", ".topo_link_cell", function () {
						                                            var tmp = $(this);

						                                            showMetricChartsDiv.attr('style', 'display:block;');
						                                            showMetric.find(".ico-chartred").each(function () {
						                                                $(this).addClass("ico-chart");
						                                                $(this).removeClass("ico-chartred");
						                                            });
						                                            tmp.addClass("ico-chartred");
						                                            tmp.removeClass("ico-chart");

						                                            var metricId = tmp.attr("metricId");
						                                            var instanceId = tmp.attr("instanceId");

						                                            chart.setIds(metricId, instanceId);

						                                            $this.showHideChartMethod(showMetric, showMetricDatagridDiv, showMetricChartsDiv, showHideChart, 'show');
						                                        });
						                                        $this.load(resourceId);
						                                    }
						                                });
						                            });
						}else{
							oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
								oc.module.resmanagement.resdeatilinfonew.open({instanceId:resourceId});
							});	
						}
						
					};
				/*	if(field == 'snapShotJSON'){
						var row = $(this).datagrid('getRows')[rowIndex];
						var snapShotJSON = row.snapShotJSON;
						if(snapShotJSON != null && snapShotJSON != undefined){
							oc.resource.loadScript('resource/module/alarm-management/js/alarm-querySnapShot.js', function(){
								oc.module.alarm.management.querysnapshot.open(row);
							});
						}
					}*/
					if(field == 'itmsData' && value != null && value != '' && value != '已处理'){
						var row = $(this).datagrid('getRows')[rowIndex];
						var severity = '';
						if(row.instanceStatus == 'red'){
							severity = '致命';
						}else if(row.instanceStatus == 'orange'){
							severity = '严重';
						}else if(row.instanceStatus == 'yellow'){
							severity = '警告';
						}
						var lastoccurTime = new Date(row.collectionTime);
						var year = lastoccurTime.getFullYear();     
						var month = lastoccurTime.getMonth()+1;     
						var date = lastoccurTime.getDate();     
						var hour = lastoccurTime.getHours();     
						var minute = lastoccurTime.getMinutes();     
						var second = lastoccurTime.getSeconds(); 
						oc.util.ajax({
							url:oc.resource.getUrl('system/itsm/get.htm'),
							success:function(d){
								
			    				oc.util.ajax({
			    					url : oc.resource.getUrl('system/login/getSessionId.htm'),
			    					successMsg : null,
			    					async:false,
			    					success : function(seesionId) {
										if(value == '未下发'){
											oc.util.ajax({
												url:oc.resource.getUrl('system/itsm/getWebService.htm'),
												success:function(data){
													var itmInfo = eval('(' + data.data + ')');
													$('<div id="dialogId"></div>').dialog({
														title: '处理工单',
														width: 900, 
														height: 500,
														content:'<iframe width="888" height="460" src="http://' + d.data.ip + ':' + d.data.port + '/itsm/bpm/showAddWOByITMBPMExecAction.action?' + 
														'userID=' + oc.index.getUser().account + '&' + 
														'itmId=' + itmInfo.CODE + '&' +
														'ciid=' + row.resourceId + '&' +
														'hostname=' + row.resourceName + '&' +
														'ipaddr=' + row.ipAddress + '&' +
														'seq=' + row.alarmId + '&' + 
														'lastoccur=' + year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second  + '&' +
														'severity=' + severity + '&' +
														'message=' + encodeURI(row.alarmContent) + '&sysVer=_S"><div id="shenmeqingkuang"></div></iframe>'
													});
												}
											});
										}else if(value == '处理中'){
											$('<div id="dialogId"></div>').dialog({
												title: '处理工单',
												width: 900, 
												height: 500,
												content:'<iframe width="888" height="460" src="http://' + d.data.ip + ':' + d.data.port + '/itsm/bpm/showDealProcessBPMExecAction.action?' + 
												'processInstanceID=' + row.itsmOrderId + '&ITMCreateCase=1&userID=' + oc.index.getUser().account + '&sysVer=_S"></iframe>'
												//&sessionid=' + seesionId.data + '
											});
										}
			    					}
			    				});

							}
						});

	 		    	}
				},
				octoolbar : {
					left : [$('.lightAlarmClass'), this.queryForm.selector,
					        {
						id : 'queryBtn',
						iconCls:'fa fa-search',
						text : '查询',
						onClick : function(){
							if($('#alarm_management_main_form_isCheckedRadioTwo').val()=="isChecked"){
								var startStr = $('#alarm_management_main_form').find('#startTime').datetimebox('getValue').trim();
								var endStr = $('#alarm_management_main_form').find('#endTime').datetimebox('getValue').trim();
								var dateStart = new Date(startStr.replace(/-/g,"/"));
								var dateEnd = new Date(endStr.replace(/-/g,"/"));
								var timeSub = dateEnd.getTime() - dateStart.getTime();
								
								if(timeSub < 0){
									alert('开始日期不能晚于结束日期');
								}else{
									that.alarmDataGrid.reLoad();
								}
							}else{
								that.alarmDataGrid.reLoad();
							}
							
						}
					},{
						id : 'resetBtn',
						iconCls:'l-btn-icon icon-reset',
						text : '重置',
						onClick : function(){
							that.removeStatusLightActive();
							that.queryForm.reset();
							that.mainDiv.find("[name='prentCategory']").val('');
							that.mainDiv.find("[name='childCategory']").val('');
							$('#searchIpInput').val('');
							$('#queryIpInput').val('');
						}
					}],
					left :(user.domainUser || user.systemUser)? [{
						id : 'res_sure_Btn',
						iconCls:'fa fa-check',
						text : '确认告警',
						onClick : function(){
							//Idx
							var alarmInstanceIds = new Array();
							var sourceDiv=$("#resource_alarm");
					
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
						    					that.alarmDataGrid.reLoad();
						    				}else if(data.data=="done"){
						    					alert("确认告警成功！");
						    					that.alarmDataGrid.reLoad();
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
					},{
						id : 'res_export_Btn',
						iconCls:'fa fa-sign-out',
						text : '导出',
						onClick : function(){
						var formValue=	that.queryForm.val();
							var Vo=JSON.stringify(formValue)
								var alarminfo = new Array();
							var info = {};
							info.alarmChecked=formValue.alarmChecked;
							info.endTime=formValue.endTime;
							info.instanceStatus=formValue.instanceStatus;
							info.isCheckedRadioOne=formValue.isCheckedRadioOne;
							info.queryTime=formValue.queryTime;
							info.startTime=formValue.startTime;
							info.iPorName=formValue.iPorName;
							alarminfo.push(info);
							var url=oc.resource.getUrl('alarm/alarmManagement/exportNotRestoreAlarm.htm');
//							that.loadAlarmDataGrid(url);
						//	window.location.href=oc.resource.getUrl('alarm/alarmManagement/exportNotRestoreAlarm.htm?alarmChecked='+formValue.alarmChecked+'&endTime='+formValue.endTime+'&instanceStatus='+formValue.instanceStatus+'&isCheckedRadioOne='+formValue.isCheckedRadioOne+'&queryTime='+formValue.queryTime+'&startTime='+formValue.startTime);
							
							window.location.href=oc.resource.getUrl('alarm/alarmManagement/exportNotRestoreAlarm.htm?Vo='+Vo+'&index='+Idx);
						/*	oc.util.ajax({
					    		url: oc.resource.getUrl('alarm/alarmManagement/exportNotRestoreAlarm.htm'),
					    		data:{alarmPageVo:formValue},
					    		successMsg:null,
					    		success:function(data){
					    			if(data.code==200){
					    				
					    			}else{
					    			//	alert("请选择需要确认的告警！");
					    			}
					    		}
					    	});*/
						}
					}]:[]
				}
			});
			
			//创建文本搜索框(IP)
			var filterMenu = $('<div style="width:175px;"></div>');
			filterMenu.append('<div ><label class="datagridFilter" ><input class="datagridFilter text-box" placeholder="搜索IP地址" id="searchIpInput" type="text" ></input></label></div>');
			
			this.ipMenuButton = this.mainDiv.find('#ipSearchInputButton').menubutton({
		  		 menu: filterMenu
			});
			filterMenu.find('#searchIpInput').on('click',function(e){
				e.stopPropagation();
			});
			 
			filterMenu.find('#searchIpInput').on('change',function(e){
				//过滤资源实例
				var queryIp = $(e.target).val();
				if(queryIp){
					that.mainDiv.find('#queryIpInput').val(queryIp);
				}else{
					that.mainDiv.find('#queryIpInput').val('');
				}
				that.alarmDataGrid.reLoad();
			});

			//支持ie回车事件 add by sunhailiang on 20170628
            if(oc.util.isIE()){
			   filterMenu.find('#searchIpInput').keypress(function(e){
			   	  e = e || window.event;
				  e.stopPropagation();
				  if(e.keyCode == 13){ //绑定回车 
					  $(this).trigger('change');
				  }
			   });
			}
			
			this.addFreshBtn();
			
			//cookie记录pagesize
			var paginationObject = this.mainDiv.find("#alarm_datagrid").datagrid('getPager');
			if(paginationObject){
				paginationObject.pagination({
					onChangePageSize:function(pageSize){
						$.cookie('pageSize_alarm-management-main',pageSize);
					}
				});
			}
		},
		addFreshBtn : function(){
			var that = this;
			//tabs头上加一个刷新按钮
			var tabsWrap = this.mainDiv.find(".tabs-header > .tabs-wrap");
			var tabs = tabsWrap.find(".tabs").css("float", "left");
			this.freshBtn = $("<span/>").css({
				height : '33px',
				float : "right",
				"margin-right" : "10px"
			}).append($("<span>").addClass('fa fa-refresh').css("margin-top", "9px").attr("title", "刷新"));
			tabsWrap.append(this.freshBtn);
			this.freshBtn.find("span").on('click', function(){
				that.alarmDataGrid.reLoad();
			});
		},
		removeStatusLightActive : function(){
			$('#alarm_management_main_form_instanceStatus').val('all');
			
			$('.lightAlarmClass').find("a").removeClass("active");
		},
		addStatusLightActive : function(light){
		//	$(light).removeClass('active');
			$(".active").each(function(){
				$(this).removeClass("active");
				
				});
			$(light).find("a").addClass('active');
		},
		loadAlarmDataGrid : function(url,tabIndex){
			
			if(this.flag){
				var comboTree111 = this.queryForm.ocui[1];
				
				this.queryForm.reset();
				
				this.isSwitchTabs = true;
				
				this.ipMenuButton.menubutton('destroy');
				
				this.mainDiv.find("#alarm_datagrid").datagrid({
					url : url
				});
//				$('.prentCategorySelect').combotree('reload');
				
				comboTree111.reLoad(oc.resource.getUrl('alarm/alarmManagement/getPrentStrategyType.htm'));
				
				//selector : $('#alarm_management_main_form').find('.prentCategorySelect'),
				//url:oc.resource.getUrl('alarm/alarmManagement/getPrentStrategyType.htm'),
			}
			// 简单临时处理隐藏<第三方告警>中请选择SELECT框
			if(Idx == '3'){
				$('#alarm_management_main_form').find('.prentCategorySelect').parent().parent().hide();
			} else {
				$('#alarm_management_main_form').find('.prentCategorySelect').parent().parent().show();
			}
			this.flag = true;
		},
		loadState :function(state){
			
			this.isSwitchTabs = true;
			
			this.ipMenuButton.menubutton('destroy');
			
			$('#alarm_management_main_form_instanceStatus').val(state);
			this.mainDiv.find("#alarm_datagrid").datagrid({
			});
		}
	};
	
	oc.ns('oc.module.alarm.management');
	
	new alarmManMainClass().open();
	
})(jQuery);