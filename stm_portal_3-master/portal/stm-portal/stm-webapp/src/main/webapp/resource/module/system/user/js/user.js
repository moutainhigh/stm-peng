(function($){
	var mainDiv=$('#oc_module_system_user_main').attr('id',oc.util.generateId()).panel({
			fit:true,
			isOcAutoWidth:true
		}),
		datagridDiv=mainDiv.find('.oc_module_system_user_main_datagrid').first(),
		user = oc.index.getUser(),
		loginUserType = user.userType,
		loginUserId = user.id,
		form = mainDiv.find(".oc-form").first(),
		queryForm = oc.ui.form({
			selector:form,
			combobox:[{
				selector:'[name=domainId]',
				data:oc.index.getDomains()
			}]
		});
		
		form.find(":input[name=keyword]").keyup(function(e){
			e.keyCode==13&&(datagrid.octoolbar.jq.find(".icon-search").trigger('click'));
		});
		placeholder4ie8();
		var serchValue = form.find(":input[name=keyword]").val();
//		if(serchValue == '用户名/姓名/手机号码/邮箱'){
//			form.find(":input[name=keyword]").val("");
//		}
		var datagrid=null,
		_dialog = null;
		
		var isResigerToPortal = false;
		oc.util.ajax({
			async:false,
			url:oc.resource.getUrl('system/service/getPortalRegisterStatus.htm'),
			success:function(data){
				if(data.code==200){
					isResigerToPortal=data.data;
				}
			}
		});
	
		var userTypes = oc.util.getDictObj('user_type'),
		toolbar = (loginUserType==3||loginUserType==4 ) && !isResigerToPortal ? [{
			text:'添加',
			iconCls:'fa fa-plus',
			onClick:function(){
				_open({type:'add',loginUserType:loginUserType});
			}
		}] : [];
		
		
		if(isResigerToPortal){
			toolbar.push({
				text:'启用',
				iconCls:'fa fa-key',
				onClick:function(){
					_changeStatus(1);
				}
			},{
				text:'停用',
				iconCls:'fa fa-ban',
				onClick:function(){
					_changeStatus(0);
				}
			},{
				text:'导出',
				iconCls:'fa fa-sign-out',
				onClick:function(){
					oc.util.downloadFromUrl(oc.resource.getUrl('system/user/downloadUsers.htm'));
				}
			});
			
		}else{
			
			toolbar.push({
				text:'删除',
				iconCls:'fa fa-trash-o',
				onClick:function(){
					var ids=datagrid.getSelectIds(),
					len=ids.length,
					selectRows = datagrid.getSelections();
					if(len){
						if(ids.indexOf(loginUserId)!=-1){
							alert('不允许删除自己','danger');
							return ;
						}else{
							for(var i=0; i <selectRows.length; i++){
								if(selectRows[i].userType==loginUserType){
									alert('不允许删除与自己同级别的用户','danger');
									return ;
								}
							}
							oc.ui.confirm('确认删除所选择的数据？', function(){
								oc.util.ajax({
									url:oc.resource.getUrl('system/user/del.htm'),
									data : {ids:ids.join()},
									success:function(data){
										if(data.code==200){
											if(data.data != len){
												alert('已设置为业务系统责任人的用户不允许删除！请先修改业务系统设置。');
											}
											if(data.data > 0){
												datagrid.reLoad();
											}
										}
									}
								});
							})
						}
					}else{
						alert('请至少选择一条数据进行删除','danger');
					}
				}
			},{
				text:'启用',
				iconCls:'fa fa-key',
				onClick:function(){
					_changeStatus(1);
				}
			},{
				text:'停用',
				iconCls:'fa fa-ban',
				onClick:function(){
					_changeStatus(0);
				}
			},{
				text:'导入',
				iconCls:'fa fa-sign-in',
				onClick:function(){
					importUser();
				}
			},{
				text:'导出',
				iconCls:'fa fa-sign-out',
				onClick:function(){
					oc.util.downloadFromUrl(oc.resource.getUrl('system/user/downloadUsers.htm'));
				}
			},{
				text:'下载模板',
				iconCls:'fa fa-download',
				onClick:function(){
					oc.util.downloadFromUrl(oc.resource.getUrl('system/user/downloadUserTemplate.htm'));
				}
			});
		}
		
		
		//console.log(queryForm);
	oc.datag=datagrid=oc.ui.datagrid({
		selector:datagridDiv,
		pageSize:$.cookie('pageSize_user')==null ? 15 : $.cookie('pageSize_user'),
		queryForm:queryForm,
		queryConditionPrefix:'',
		url:oc.resource.getUrl('system/user/userPage.htm'),
		octoolbar:{
			left:toolbar,
			right:[queryForm.selector]
		},
//		delCfg:loginUserType!=2 ? {url:oc.resource.getUrl('system/user/del.htm')} : 0,
		columns:[[
	         {field:'id',title:'-',checkbox:true,width:50},
	         {field:'status',title:'状态',sortable:true,width:80,formatter: function(value,row,index){
	        	 return "<span data-index='"+index+"' style='cursor:pointer;' class='oc-top0 locate-left status oc-switch "+(value==1 ? "open" : "close")+"' data-data='"+value+"'></span>";
	         }},
	         {field:'account',title:'用户名',sortable:true,width:80,formatter: function(value,row,index){
	        	 	return "<span data-index='"+index+"' class='oc-pointer-operate'  title='点我编辑详情'>"+value+"</span>";
        	 }},
	         {field:'name',title:'姓名',sortable:true, width:80},
	         {field:'userType',title:'用户类型',width:80,formatter:function(value,row,index){
	        	 
	        	 return value!=0?userTypes[value].name: '';
	         }},
	         {field:'role',title:'域/角色',width:80,formatter: function(value,row,index){
	        	 return row.userType==1 ? "<span data-index='"+index+"' class='icon-domain_user icon-edit light_blue' data='"+row.id+"' title='查看拥有域角色'></span>" : "";
        	 }},
	         {field:'mobile',title:'手机',sortable:true,width:80,formatter:function(value,row,index){
	        	 return row.userType!=4 ? value : '';
	         }},
	         {field:'email',title:'邮箱',sortable:true,width:80,formatter:function(value,row,index){
	        	 return row.userType!=4 ? value : '';
	         }},
         ]],
         onLoadSuccess:function(){
        	 var panel = datagrid.selector.datagrid("getPanel"),
        	 rows = datagrid.selector.datagrid("getRows");
        	 panel.find(".status").on('click' ,function(e){
        		 e.stopPropagation();
        		 var $this = $(this),
        		 index = $this.data("index");
        		 row = rows[index];
        		 if(row.id==loginUserId){
        			 alert('不允许启用或停用自己','danger');
        			 return ;
        		 }
        		 if(row.userType==loginUserType){
        			 alert('不允许启用或停用与自己同级别的用户','danger');
        			 return ;
        		 }
        		 var type = (row.status+1)%2;
        		 oc.util.ajax({
     	    		url:oc.resource.getUrl('system/user/updateStatus.htm'),
     	    		data:{ids:row.id, type:type},
     	    		failureMsg:'操作失败！',
     	    		success:function(data){
     	    			if(data.code==200){
     	    				row.status = type;
     	    				if(type==1){
     	    					$this.removeClass("close").addClass("open");
     	    					panel.find(".table-datanull table tr").eq(index).removeClass("gray_color");
     	    				}else{
     	    					$this.removeClass("open").addClass("close");
     	    					panel.find(".table-datanull table tr").eq(index).addClass("gray_color");
     	    				}
     	    				
     	    			}
     	    		}
     	    	});
        		
        	 });
        	 panel.find(".status").each(function(){
        		var dom = $(this),
        		data = dom.data("data");
        		data!=1&&dom.parent().parent().parent().addClass("gray_color");
        	 });
        	 
        	 panel.find(".oc-pointer-operate").on('click', function(e){
        		 var $this = $(this),
        		 index = $this.data("index");
        		 row = rows[index];
        		 if(row.id==loginUserId||row.userType!=loginUserType){
        			 _open({
            			 type:'edit',
            			 id:row.id,
            			 userType:row.userType,
            			 loginUserType:loginUserType
            		});
        		 }else{
        			 alert('此用户不允许编辑');
        		 }
        		 e.stopPropagation();
        	 });
        	 panel.find(".icon-domain_user").on('click', function(e){
        		 var $this = $(this),
        		 index = $this.data("index");
        		 row = rows[index];
        		 (row.userType==1)&&_openModel(row, e);
//        		 e.stopPropagation();
        	 });
//        	 panel.find(".datagrid-header :input[type=checkbox]").click(function(e){
//        		 panel.find(".datagrid-body :input[name=id]").attr("checked", this.checked);
//        		 e.stopPropagation();
//        	 });
         }
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_user');
				$.cookie('pageSize_user',pageSize);
			},
		});
	}
	
	function _openModel(row, e){
		var dlg = _dialog = $("<div class='oc-dialog-float'></div>").dialog({
			content:"<div class='domain_role_datagrid'></div>",
			title:row.account+'/域/角色',
			height:200,
			width:400,
			top:getEventTop(e),
			left:e.screenX+20,
			cache:false,
			modal:false
		});
		oc.util.showFloat(dlg,e);
		oc.ui.datagrid({
			selector:_dialog.find(".domain_role_datagrid"),
			width:400,
			loadFilter:function(d){
				d.total=d ? d.length : 0;
				d.rows=d ? d : [];
				return d;
			},
			loader:function(param,success){
		    	oc.util.ajax({
		    		url:oc.resource.getUrl('system/user/getDomainRoles.htm'),
		    		data:{id:row.id},
		    		failureMsg:'加载用户域数据失败！',
		    		async:false,
		    		success:function(data){
		    			var d = data.data,
		    			domains = {},
		    			result = [];
		    			for(var i=0; i<d.length; i++){
		    				var item = d[i],
		    				domain = domains[item.id];
		    				if(domain){
		    					domain.roleName += "," + item.roleName;
		    				}else{
		    					result.push(item);
		    					domains[item.id] = item;
		    				}
		    			}
		    			success(result);
		    		}
		    	});
		    },
		    pagination:false,
			columns:[[
				{field:'name',title:'域',width:80},
				{field:'roleName',title:'角色',width:60,ellipsis:true}
	         ]]
		});
	}
	function _open(cfg){
		oc.resource.loadScript('resource/module/system/user/js/userDetail.js',function(){
			oc.module.system.user.open($.extend({
				datagrid:datagrid,
				loginUserId:loginUserId
			},cfg));
		});
	}
	function importUser(){
		oc.resource.loadScript('resource/module/system/user/js/importUser.js',function(){
			oc.module.system.importuser.open(function(){
				datagrid.reLoad();
			});
		});
	}
	
	function _changeStatus(type){
		var selectedRows=datagrid.selector.datagrid('getSelections'),ids=[],
		panel = datagrid.selector.datagrid('getPanel');;
		for(var i=0,len=selectedRows.length;i<len;i++){
			ids[i]=selectedRows[i].id;
			selectedRows[i].status = type;
		}
		if(ids.length>0){
			if(ids.indexOf(loginUserId)!=-1){
				alert('不允许操作自己','danger');
				return ;
			}else if(type==0){
				for(var i=0; i <selectedRows.length; i++){
					if(selectedRows[i].userType==loginUserType){
						alert('不允许停用与自己同级别的用户','danger');
						return ;
					}
				}
			}
			oc.util.ajax({
	    		url:oc.resource.getUrl('system/user/updateStatus.htm'),
	    		data:{ids:ids.join(), type:type},
	    		failureMsg:'操作失败！',
	    		success:function(data){
	    			if(data.code==200){
	    				for(var i=0;i<ids.length;i++){
	    					var row = selectedRows[i];
		    				var index = datagrid.selector.datagrid('getRowIndex', row);
		    				row.status = type;
     	    				if(type==1){
//     	    					$this.removeClass("close").addClass("open");
     	    					panel.find(".table-datanull table tr").eq(index).removeClass("gray_color").find(".status").removeClass("close").addClass("open");
     	    				}else{
//     	    					$this.removeClass("open").addClass("close");
     	    					panel.find(".table-datanull table tr").eq(index).addClass("gray_color").find(".status").removeClass("open").addClass("close");
     	    				}
		    			}
	    			}
	    		}
	    	});
		}else{
			alert('请至少选择一条数据进行操作');
		}
	}
	function getEventTop(e){
		var top = 0;
		if(navigator.userAgent.indexOf('Firefox')!=-1){
			top = 90;
		} else if(navigator.userAgent.indexOf('MSIE')!=-1){
			top = 100;
		} else{
			top = 58;
		}
		return e.screenY - top;
	}
	function placeholder4ie8(){
		if( !('placeholder' in document.createElement('input')) ){  
			$('input[placeholder],textarea[placeholder]').each(function(){   
				var that = $(this),   
				text= that.attr('placeholder');   
				if(that.val()===""){   
					that.val(text).addClass('placeholder');   
				}   
				that.focus(function(){   
					if(that.val()===text){   
						that.val("").removeClass('placeholder');   
					}   
				})   
				.blur(function(){   
					if(that.val()===""){   
						that.val(text).addClass('placeholder');   
					}   
				})   
				.closest('form').submit(function(){   
					if(that.val() === text){   
						that.val('');   
					}   
				});   
			});   
		}  
	}
})(jQuery);