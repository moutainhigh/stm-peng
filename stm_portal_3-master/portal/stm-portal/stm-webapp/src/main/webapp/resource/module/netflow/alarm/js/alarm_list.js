(function($){
	
	var mainDiv = $('#oc_netflow_alarm_main').attr('id', oc.util.generateId());
	//init datagrid
	var datagridDiv = mainDiv.find('#netflow_alarm_list_datagrid');
	var user = oc.index.getUser();
	var datagridtoolbar = mainDiv.find("#netflow-alarm");
	var netflowAlarmDatagrid = null;
	if(user.systemUser){
	         netflowAlarmDatagrid = oc.ui.datagrid({
			selector: datagridDiv,
			url: oc.resource.getUrl('netflow/alarm/getAllAlarms.htm'),
			hideReset:true,
			hideSearch:true,
			pagination:true,
			queryParams: {
			},
			octoolbar:{
				left  : [datagridtoolbar,{
					text : '查询',
					iconCls : 'icon-search',
					onClick : function() {
						netflowAlarmDatagrid.selector.datagrid("load", {
							AlarmsName:$('#netflow-alarm-managment').val(),
						});
					}
				},
				{
					text : '重置',
						iconCls : 'icon-back',
						onClick : function() {
							mainDiv.find('#netflow-alarm-managment').val('');
						}
					}],
				right: [
						
				    {
					text:'添加',
					iconCls: 'fa fa-plus',
					onClick: function() {
						oc.resource.loadScript('resource/module/netflow/alarm/js/alarm_detail.js',function(){
							oc.module.netflow.alarm.open({
								type: 'add',
								refresh: function() {
									netflowAlarmDatagrid.selector.datagrid("reload");
								}
							});
						});
					}
				}, {
					text: '删除',
					iconCls:'fa fa-trash-o',
					onClick: function() {
						
							var ids = '';
							var selected = datagridDiv.datagrid('getSelections');
							if(!selected || selected.length == 0) {
								alert('请选择要删除的告警设置');
							} else {
								oc.ui.confirm('确认删除选中的告警设置吗?',function(){
									$.each(selected, function(i, t) {
										ids += t.id + ',';
									});
									if(ids != '') {
										ids = ids.substring(0, ids.length - 1);
									}
									oc.util.ajax({
							    		 url:oc.resource.getUrl('netflow/alarm/delProfiles.htm'),
							    		 data: {
							    			 ids:ids
							    		 },
							    		 failureMsg:'删除告警记录失败！',
							    		 async:false,
							    		 success: function(data) {
							    			 netflowAlarmDatagrid.selector.datagrid("reload");
							    		 }
									});
								});
							}
						
					}
				}]
			},
			columns:[[
			          {field: ' ', checkbox: true},
			          {field: 'id',title:'',hidden:true},
			          {
			        	  field: 'state', title: '状态',sortable : false,width:5,
			        	  formatter: function(value, row, index) {
			        		  return "<span class='oc-top0 locate-left plan-status-ope status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"'></span>";
			        	  }
			          },
			          {
			        	  field: 'netflowAlarmConfigName', title: '配置名称', sortable : true,width: 10,
			        	  formatter: function(value, row, index) {
			        		  return '<span title="编辑告警设置" profileid="'+row.id+'" class="netflow-alarm-setting-edit">'+value+'</span>';
			        	  }
			          },
			          {
			        	  field: 'netflowAlarmDesc', title: '描述', sortable : false, width: 20,
			        	  formatter: function(value, row, index) {
			        		  return '<span title="'+value+'">'+value+'</span>';
			        	  }
			          },
			          {field: 'oneHoureAlarm', title: '最近一小时告警', sortable : false, width: 5},
			          {field: 'allCountAlarm', title: '所有告警', sortable : false,width: 5}
			]],
			onClickCell: function(rowIndex, field, value){
		       	 var row = netflowAlarmDatagrid.selector.datagrid("getRows")[rowIndex];
		       	 if(field=='state'){
		       		 var type = (value+1)%2;
		       		 var status = type;
		       		_updateStatus(row.id, status);
		       		row.status = type;
		       		netflowAlarmDatagrid.selector.datagrid("updateRow",{
						index : rowIndex,
						row : row
		       		});
		       		netflowAlarmDatagrid.selector.datagrid('reload');
		       	}
	       },
	       onLoadSuccess: function(data) {
				mainDiv.find(".status").each(function(){
	        		var dom = $(this),
	        		data = dom.data("data");
	        		data!=1&&dom.parent().parent().parent().addClass("grey_color");
	        	});
				
				mainDiv.find('.netflow-alarm-setting-edit').bind('click', function(e) {
					var profileId = $(this).attr('profileid');
					oc.resource.loadScript('resource/module/netflow/alarm/js/alarm_detail.js',function(){
						oc.module.netflow.alarm.open({
							type: 'edit',
							id: profileId
						});
					});
					e.stopPropagation();
				});
			}
		});
	}else{
		  netflowAlarmDatagrid = oc.ui.datagrid({
				selector: datagridDiv,
				url: oc.resource.getUrl('netflow/alarm/getAllAlarms.htm'),
				hideReset:true,
				hideSearch:true,
				pagination:true,
				queryParams: {
					
				},
				octoolbar:{
					left  : [datagridtoolbar,
					         {
						text : '查询',
							iconCls : 'icon-search',
							onClick : function() {
								netflowAlarmDatagrid.selector.datagrid("load", {
									AlarmsName:$('#netflow-alarm-managment').val(),
								});
							}
						},
						{
							text : '重置',
								iconCls : 'icon-back',
								onClick : function() {
									mainDiv.find('#netflow-alarm-managment').val('');
								}
							}]
				},
				columns:[[
				          {
				        	  field: 'state', title: '状态',sortable : false,width:5,
				        	  formatter: function(value, row, index) {
				        		  return "<span class='oc-top0 locate-left plan-status-ope status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"'></span>";
				        	  }
				          },
				          {
				        	  field: 'netflowAlarmConfigName', title: '配置名称', sortable : true,width: 10,
				        	  formatter: function(value, row, index) {
				        		  return '<span title="编辑告警设置" profileid="'+row.id+'" class="netflow-alarm-setting-edit">'+value+'</span>';
				        	  }
				          },
				          {
				        	  field: 'netflowAlarmDesc', title: '描述', sortable : false, width: 20,
				        	  formatter: function(value, row, index) {
				        		  return '<span title="'+value+'">'+value+'</span>';
				        	  }
				          },
				          {field: 'oneHoureAlarm', title: '最近一小时告警', sortable : false, width: 5},
				          {field: 'allCountAlarm', title: '所有告警', sortable : false,width: 5}
				]],
		       onLoadSuccess: function(data) {
				
					mainDiv.find(".status").each(function(){
		        		var dom = $(this),
		        		data = dom.data("data");
		        		data!=1&&dom.parent().parent().parent().addClass("grey_color");
		        	});
					
					mainDiv.find('.netflow-alarm-setting-edit').bind('click', function(e) {
						var profileId = $(this).attr('profileid');
						oc.resource.loadScript('resource/module/netflow/alarm/js/alarm_detail.js',function(){
							oc.module.netflow.alarm.open({
								type: 'edit',
								id: profileId
							});
						});
						e.stopPropagation();
					});
				}
			});
	}

	
	function _updateStatus(id, status) {
		if(id) {
			oc.util.ajax({
				url : oc.resource.getUrl('netflow/alarm/updateStatus.htm'),
				data : {
					profileId: id,
					status: status
				},
				async:false,
				successMsg : null,
				success : function(data) {
					if(data.code == 200 && data.data > 0) {
						if(status) {
							alert('启用成功!');
						} else {
							alert("停用成功!");
						}
					} else {
						alert('更新状态失败!');
					}
				}
			});
		}
	}
	
})(jQuery);