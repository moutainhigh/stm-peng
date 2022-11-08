$(function(){
	var mainDiv=$('#oc_module_config_device_main').attr('id',oc.util.generateId()).panel({
		fit:true,
		isOcAutoWidth:true
	}),
	datagridDiv=mainDiv.find('.oc_module_config_device_datagrid');
	
	var treeMenuTitleName = null;
	var chooseGroupId = null;
	var chooseGroupName = null;
	
	if(oc.module){
		if(oc.module.config){
			if(oc.module.config.index){
				chooseGroupId = oc.module.config.index.getChooseGroupId();
				chooseGroupName = oc.module.config.index.getChooseGroupName();
			}
		}
	}

	var highlight = $('#sidebar').find('#mainMenu').find('li.module-href.highlight').find('li.highlight:first');
	if(highlight.hasClass('open')){
		treeMenuTitleName = highlight.find('li.highlight:first').find('a:first').attr('rightmarkname');
	}else{
		treeMenuTitleName = highlight.find('a:first').attr('rightmarkname');
	}
	
	if(treeMenuTitleName == '设备一览'){
		chooseGroupId = null;
		chooseGroupName = null;
		$('#mainMenu').find('a[rightmarkname="自定义配置组"]:first').attr('hasClickDevice','true');
//		$('#collapse_btn').hide();
//		$('#collapse_btn').next().css('left','0px');
//		$('#collapse_btn').next().css('width','100%');
	}else{
//		collapse_btn();
	}
	
	user = oc.index.getUser();
	var datagrid=null;
	var _dialog = $('<div class="oc-dialog-float"/>');
	var toolbar = [];
	var form = $('<form class="oc-form float"/>')
		.append($('<div class="form-group"/>')
				.append('<input type="text" class="oc-enter" name="ipOrName" placeholder="名称/IP"/>')
				.append('<input type="text" name="config-hidden-text" style="display:none;"/>'))
		.append($('<div class="form-group"/>').append('<a class="queryBtn">查询</a>'))
		.append($('<div class="form-group"/>').append('<a class="resetBtn">重置</a>'));
	var queryForm = oc.ui.form({ selector : form });
	form.fixPlaceHolder();
	form.find('.oc-enter').keypress(function(e,i){
		if(e.keyCode==13)
			datagrid.reLoad({ipOrName:$("input[name='ipOrName']").val()});
	});
	form.find(".queryBtn").linkbutton('RenderLB',{
		iconCls:'icon-search',
		onClick : function(){
			datagrid.reLoad({ipOrName:$("input[name='ipOrName']").val()});
		}
	});
	form.find(".resetBtn").linkbutton('RenderLB',{
		iconCls:'icon-reset',
		onClick : function(){
			queryForm.reset();
		}
	});
	if(user.systemUser==true){//只能系统管理员有导入(删除)资源权限
		toolbar.push({
			text:'导入资源',
			iconCls:'fa fa-sign-in',
			onClick:function(){
				addDevice();
			}
		},{
			text:'删除',
			iconCls:'fa fa-trash-o',
			onClick:function(){
				delDevice();
			}
		});
	}
	toolbar.push({
		text:'备份',
		iconCls:'icon-backup',
		onClick:function(){
			backupDevice();
		}
	},{
		text:'全部备份',
		iconCls:'icon-allbackup',
		onClick:function(){
			var rows = datagrid.selector.datagrid('getData').datas;
			if(rows.length<1){
				alert("没有设备数据");
				return;
			}else{
				for(var i=0;i<rows.length;i++){
					if(rows[i].isSave!=1){
						alert(rows[i].intanceName+"未设置连接信息，请先设置连接信息后再执行备份");
						return;
					}
				}
			}
			oc.ui.confirm("是否确认执行全部备份？",function(){
				oc.util.ajax({
					url:oc.resource.getUrl('portal/config/device/backupResourcesByIds.htm'),			            
					successMsg:null,
					timeout:null,
					success:function(d){
						if(d.code&&d.code==200){
							if(d.data!=""&&d.data!=undefined){
								alert(d.data);
							}else{
								alert("备份成功！");
							}
							datagrid.reLoad();
						}else if(d.code==299){alert(d.data);}
					}
				});
			})
		}
	},{
		text:chooseGroupId==null?'移入配置组':'移出配置组',
		iconCls:chooseGroupId==null?'l-btn-icon icon-movein':'l-btn-icon fa fa-arrows',
		onClick:function(){
			openMoveIntoOrOutGroupDialog();
		}
	});
	
//	//左侧菜单折叠按钮
//	function collapse_btn(){
//		$("#collapse_btn").click(function(){
//			if($(this).hasClass('fa-angle-double-left')){
//				$('#oc_config_file_index').layout('collapse','west');
//				$('#oc_config_file_index').layout('panel','center').parent().css('left','0px');
//				$(this).removeClass('fa-angle-double-left');
//				$(this).addClass('fa-angle-double-right');
//			}else{
//				$('#oc_config_file_index').layout('expand','west');
//				$(this).removeClass('fa-angle-double-right');
//				$(this).addClass('fa-angle-double-left');
//			}
//		});
//	}
		
	datagrid=oc.ui.datagrid({
		selector:datagridDiv,
		pageSize:$.cookie('pageSize_device')==null ? 15 : $.cookie('pageSize_device'),
		url:oc.resource.getUrl('portal/config/device/getConfigDevicePage.htm?groupId='+(chooseGroupId==null?"":chooseGroupId)),
		queryForm:queryForm,
		queryConditionPrefix:'',
		hideReset:true,
		hideSearch:true,
		octoolbar:{
			left:[queryForm.selector],
			right:toolbar
		},
		columns:[[
	         {field:'id',title:'-',checkbox:true,width:20},
	         {field:'backupState',title:'备份状态',sortable:true,width:60,formatter: function(value,row,index){
	        	 return "<span>"+((0==value||value==null)?'未备份':(1==value?'成功':'失败'))+"</span>";
	         }},
	         {field:'changeState',title:'配置变更状态',sortable:true,width:60,formatter: function(value,row,index){
	        	 return "<span class='"+
	        	 (0==value ? 'light-ico greenlight oc-fileico' :(1==value?'light-ico yellowlight oc-fileico':''))+"'>"+
	        	 (0!=value&&1!=value?"-":"")+"</span>";
	         }},
	         {field:'intanceName',title:'设备名称',sortable:true,width:70,formatter:function(value){
	        	 return "<span title='点我查看备份历史' class='oc-pointer-operate'>"+value+"</span>";
	         }},
	         {field:'ipAddress',title:'IP地址',sortable:true, width:70},
	         {field:'resourceTypeId',title:'资源类型',width:70},
	         {field:'softVersion',title:'软件版本',width:60},
	         {field:'lastBackupTime',title:'最后一次备份时间',sortable:true,width:120,formatter:function(value){
	        	  var datetime = new Date();
	        	    datetime.setTime(value);
	        	    var year = datetime.getFullYear();
	        	    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
	        	    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
	        	    var hour = datetime.getHours()< 10 ? "0" + datetime.getHours() : datetime.getHours();
	        	    var minute = datetime.getMinutes()< 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
	        	    var second = datetime.getSeconds()< 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
        	 	if(value == undefined||value == ""||value==null) {
        	 		return "";
        	 	}else{
        	 		return     year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second;
        	 	}
//     			var unixTimestamp = new Date(value),hours = unixTimestamp.getHours();
//     			if(0==hours)
//	     			return unixTimestamp.toLocaleString().replace('12:',"0:");
//	     		else
//             	 	return unixTimestamp.toLocaleString();
	         }},
	         {field:'operate',title:'操作',width:70,formatter:operateFormart}
         ]],
         onClickCell:function(rowIndex, field, value){
        	 if("intanceName" == field){
        		 var rows = $(this).datagrid('getRows'),row;
        		 for(var i in rows){
        			 if(i==rowIndex){
        				 row = rows[i];
        				 break;
        			 }
        		 }
        		 viewBackupHitoryWin(row.id,row); 
        	 }
         },
         onLoadSuccess:function(){
        	 $(".loginConfigBtn").each(function(){
        		 $(this).linkbutton({ plain:true, iconCls:
            		 $(this).hasClass('green')?'fa fa-chain light_blue':'fa fa-chain-broken light_blue'})
            		 .attr("title",$(this).hasClass('green')?"已设置连接信息":"未设置连接信息");
        	 })
        	 $(".backupBtn").each(function(){
        		 $(this).linkbutton({ plain:true, iconCls:
        			 $(this).hasClass('green')?'icon-allbackup light_blue':'icon-nobackup light_blue'})
        			 .attr("title",$(this).hasClass('green')?"已选择备份计划":"未选择备份计划");
        	 })
         }
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_device');
				$.cookie('pageSize_device',pageSize);
			}
		});
	}
	
	//添加设备
	function addDevice(){
		var addDeviceWindow = $('<div/>');
		//构建dialog
		addDeviceWindow.dialog({
		  title:"导入资源",
		  href:oc.resource.getUrl('resource/module/config-file/device/addDevice.html'),
		  width:800,
		  height:450,
		  modal:true,
		  buttons:[{
			  	iconCls:'ico ico-ok',
				text:'确定',
				handler:function(){
					var ids = [],rows = oc.module.config.device.getRightRows();
					for(var i=0;i<rows.length;i++){
						ids.push(Number(rows[i].id));
					}
					if(ids.length == 0){
						alert("请选择设备");
						return;
					}
					oc.util.ajax({
						url: oc.resource.getUrl('portal/config/device/addConfigDevice.htm'),
						data:{ids:ids.join(),groupId:(chooseGroupId==null?"":chooseGroupId)},
						successMsg:null,
						success:function(data){
							if(data.code && data.code==200){
								datagrid.reLoad();
//								oc.module.config.index.updateGroupInfoForAddResource(ids);
								addDeviceWindow.dialog('close');
								oc.resource.loadScript('resource/module/config-file/device/js/backup.js?t='+new Date(),function(){
									oc.module.config.device.backup.open({id:ids.join(),backupId:null,datagrid:datagrid});
								});
							}else if(data.code==299){alert(data.data);}
						}
					});
				}
		  	},{
		  		iconCls:'fa fa-times-circle',
				text:'取消',
				handler:function(){
					addDeviceWindow.dialog('close');
			}
		 }]
	  });
	}
	//删除设备
	function delDevice(){
		var ids = datagrid.getSelectIds();
		 if(ids.length<1){
			 alert("请勾选需要删除设备");
		 }else{
			 oc.ui.confirm("是否确认删除设备？",function(){
				 oc.util.ajax({
					 url:oc.resource.getUrl('portal/config/device/delConfigDevice.htm'),			            
					 data:{ids:ids.join()},
					 successMsg:null,
					 success:function(d){
						 if(d.code&&d.code==200){
							 datagrid.reLoad();
//							 if(treeMenuTitleName!='设备一览'){
//								 oc.module.config.index.updateGroupInfoForDeleteResource();
//							 }
							 alert("删除设备成功");
						 }else if(d.code==299){alert(d.data);}
					 }
				 });
			 });
		 }
	}
	//打开移入配置组窗口或者移出配置组确定窗口
	function openMoveIntoOrOutGroupDialog(){
		var ids = datagrid.getSelectIds();
		if(ids.length<1){
			alert("请勾选需要移"+(chooseGroupId==null?"入":"出")+"组的设备");
		}else{
			if(chooseGroupId==null){//移入
				var moveIntoGroupWindow = $('<div/>');
				//构建dialog
				moveIntoGroupWindow.dialog({
				  title:"移入配置组",
				  href:oc.resource.getUrl('resource/module/config-file/device/moveDeviceIntoGroup.html'),
				  width:450,
				  height:450,
				  modal:true,
				  buttons:[{
					    iconCls:'fa fa-check-circle',
						text:'确定',
						handler:function(){
							var groupIds = oc.module.config.device.group.getSelectIds();
							if(groupIds == undefined||groupIds == ""){
								alert("请勾选配置组");
							}else{
								oc.util.ajax({
									 url:oc.resource.getUrl('portal/config/file/moveIntoGroup.htm'),			            
									 data:{groupIds:groupIds.join(),resourceInstanceIds:ids.join()},
									 successMsg:null,
									 success:function(d){
										 if(d.code&&d.code==200){
//											 oc.module.config.index.updateGroupInfoForMoveResource(groupIds,ids);
											 moveIntoGroupWindow.dialog('close');
											 alert("移入配置组成功");
										 }else if(d.code==299){alert(d.data);}
									 }
								 });
							}
						}
				  	},{
				  		iconCls:'fa fa-times-circle',
						text:'取消',
						handler:function(){
							moveIntoGroupWindow.dialog('close');
					}
				 }]
				});
			}else{//移出
				 oc.ui.confirm("是否确认从该配置组中移出设备？",function(){
					 oc.util.ajax({
						 url:oc.resource.getUrl('portal/config/file/delResourceFromCustomGroup.htm'),			            
						 data:{id:chooseGroupId,resourceInstanceIds:ids.join()},
						 successMsg:null,
						 success:function(d){
							 if(d.code&&d.code==200){
								 datagrid.reLoad();
//								 oc.module.config.index.updateGroupInfoForDeleteResource(ids,chooseGroupId);
								 alert("移出配置组成功");
							 }else if(d.code==299){alert(d.data);}
						 }
					 });
				 });
			}
		}
	}
	//格式化操作栏
	function operateFormart(value,row,index){
		var result = $("<span/>");
		result.append($('<span class="loginConfigBtn'+(row.isSave==1?" green":"")+'"/>').on('click',function(){
			oc.resource.loadScript('resource/module/config-file/device/js/loginConfig.js?t='+new Date(),function(){
				oc.module.config.device.detail.open({id:row.id,ipAddress:row.ipAddress,isHandle:row.handle,datagrid:datagrid});
			});
		}));
		result.append($('<span class="backupBtn'+
				(row.backupId!=null&&row.backupId!=undefined&&row.backupId!=""?" green":"")+'"/>').on('click',function(){
			oc.resource.loadScript('resource/module/config-file/device/js/backup.js?t='+new Date(),function(){
				oc.module.config.device.backup.open({id:row.id,backupId:row.backupId,datagrid:datagrid});
			});
		}));
		return result;
	}
	//查看配置历史文件窗口
	function viewBackupHitoryWin(id,row){
		var loginConfigWindow = $('<div/>');
		//构建dialog
		loginConfigWindow.dialog({
			title:"查看配置历史文件",
			href:oc.resource.getUrl('resource/module/config-file/device/backupHistory.html'),
			width:900,
			height:500,
			modal:true,
			buttons:[
			{
				text:'恢复',
				handler:function(){
					var selectRows = oc.module.config.device.backuphistory.recovery('recovery');
					if(selectRows.length == 1){
						oc.ui.confirm("恢复操作会覆盖原配置文件，是否确认恢复?",function(){					
							oc.util.ajax({
							url:oc.resource.getUrl('portal/config/device/recoveryResources.htm?filePath=' + selectRows[0].fileId + '&id=' + id),
							successMsg:null,
							timeout:null,
							success:function(d){
								if(d.code&&d.code==200){
									if(d.data!=""&&d.data!=undefined){
										alert(d.data);
									}else{
										alert("恢复脚本执行完成,建议登录设备进行检查。");
									}
									datagrid.reLoad();
								}else if(d.code==299){
									alert(d.data);
								}
							}
							});
						});
					}else{
						alert("只能选择一个文件!");
					}
				}
			},
			{
				text:'比较',
				handler:function(){
					var selectRows = oc.module.config.device.backuphistory.compare('compare');
					if(selectRows.length==2){
						openCompareWin('compare',selectRows[0],selectRows[1],row.ipAddress);
					}else{
					}
				}
			}],
			onLoad:function(){
				oc.module.config.device.backuphistory.setProperties(row);
				oc.module.config.device.backuphistory.open(id,row.ipAddress);
			}
		});
	}
	//比较两个历史文件窗口
	function openCompareWin(type,id1,id2,ipAddress){
		var compareWin = $('<div/>');
		//构建dialog
		compareWin.dialog({
			title:type=='compare'?"比较文件":"查看文件",
			href:oc.resource.getUrl('resource/module/config-file/device/compareHistory.html'),
			width:900,
			height:510,
			modal:true,
			onLoad:function(){
				oc.module.config.device.backup.history.open(type,id1,id2,ipAddress);
			}
		});
	}
	/**
	 * 手动备份选中的设备配置文件
	 */
	function backupDevice(){
		 var ids = datagrid.getSelectIds(),rows = datagrid.selector.datagrid('getSelections');
		 if(ids.length<1){
			 alert("请勾选需要备份的设备");
		 }else{
			 for(var i=0;i<rows.length;i++){
				if(rows[i].isSave!=1){
					alert(rows[i].intanceName+"未设置连接信息，请先设置连接信息后再执行备份");
					return;
				}
			 }
			 oc.util.ajax({
				 url:oc.resource.getUrl('portal/config/device/backupResourcesByIds.htm'),			            
				 data:{ids:ids.join()},
				 successMsg:null,
				 timeout:null,
				 success:function(d){
					 if(d.code&&d.code==200){
						 if(d.data!=""&&d.data!=undefined){
							 alert(d.data);
						 }else{
							 alert("备份成功！");
						 }
						 datagrid.reLoad();
					 }else if(d.code==299){alert(d.data);}
				 }
			 });
		 }
	}
	oc.ns('oc.module.config.file.device');
	oc.module.config.file.device = {
		openCompareWin:function(type,id1,id2,ipAddress){
			return openCompareWin(type,id1,id2,ipAddress);
		},
		viewBackupHitoryWin:function(id,row){
			return viewBackupHitoryWin(id,row);
		}
	}
});