(function($){
	var mainDiv=$('#oc_module_system_role_main').attr('id',oc.util.generateId()).panel({
			fit:true,
			isOcAutoWidth:false
		}),
		datagridDiv=mainDiv.find('.oc_module_system_role_main_datagrid').first(),
		datagrid=null;
	var _dialog=null;
	var loginUserType = oc.index.getUser().userType;
	
	var btns = [],delCfg = undefined;
	if(loginUserType==3||loginUserType==4){
		btns = [{
			text:'添加',
			iconCls:'fa fa-plus',
			onClick:function(){
				_open({type:'add'});
			}
		},{
			text:'删除',
			iconCls:'fa fa-trash-o',
			onClick:function(){
				var selectIds=datagrid.getSelectIds(),
					len=0,
					ids = [];
				for(var i=0; i<selectIds.length;i++){
					selectIds[i]!=1&&selectIds[i]!=2&&ids.push(selectIds[i]);
				}
				len = ids.length;
				if(len>0){
					oc.ui.confirm('确认删除所选择的数据？',function(){
						oc.util.ajax({
							url:oc.resource.getUrl('system/role/checkRelUser.htm'),
							data : {ids:ids.join()},
							successMsg:null,
							failureMsg:null,
							success:function(data){
								data.code==200 ? delRole(ids) : oc.ui.confirm('部分角色已经关联到了用户，是否继续删除？',function(){
									delRole(ids);
								});
							}
						});
					});
				}else{
					alert('请至少选择一条数据进行删除','danger');
				}
			}
		}];
	}
	function delRole(ids){
		oc.util.ajax({
			url:oc.resource.getUrl('system/role/del.htm'),
			data : {ids:ids.join()},
			successMsg:'删除数据成功',
			success:function(data){
				data.code==200&&datagrid.reLoad();
			}
		});
	}
	datagrid=oc.ui.datagrid({
		selector:datagridDiv,
		queryConditionPrefix:'',
		url:oc.resource.getUrl('system/role/rolePage.htm'),
		octoolbar:btns.length>0 ?  {
			left:btns
		} : undefined,
		columns:[[
	         {field:'id',title:'-',checkbox:true,sortable:true,width:20,isDisabled:function(value,row,index){
	        	 return value==1||value==2;
	         }},
	         {field:'name',title:'角色名称',width:30,sortable:true,formatter: function(value,row,index){
	        	 var title = loginUserType==3||loginUserType==4 ? '点击编辑详情' : '',
	        			 pointer = loginUserType==3||loginUserType==4 ? 'cursor:pointer;' : '';
	        	 	return "<span data-index='"+index+"' class='name oc-pointer-operate'     00 title='"+title+"'>"+value+"</span>";
        	 }},
	         {field:'domainUser',title:'域/用户',width:30,formatter: function(value,row,index){
	        	 	return "<span data-index='"+index+"' data-field='domainUser' title='点击查看域用户信息' class='icon icon-Person light_blue'></span>";
	        	 }
	         },
	         {field:'right',title:'权限',width:30,formatter: function(value,row,index){
	        	 return "<span data-index='"+index+"' data-field='right' title='点击查看权限信息' class='right fa fa-lock light_blue'></span>";
        	 }},
	         {field:'description',title:'备注', ellipsis:true,width:50,editor:{
	        	 type:'text'
	         }}
         ]],
         onLoadSuccess:function(){
        	 var panel = datagrid.selector.datagrid("getPanel"),
        	 rows = datagrid.selector.datagrid("getRows");
        	 panel.find(".icon.icon-Person.light_blue,.right.fa.fa-lock.light_blue").on('click' ,function(e){
        		 var $this = $(this);
    			 var index = $this.data("index"),
        		 field = $this.data("field"),
        		 row = rows[index];
        		 _openModel($.extend(row,{field:field, e:e}));
        	 });
        	 panel.find(".name").on('click' ,function(e){
        		 e.stopPropagation();
        		 var $this = $(this);
    			 var index = $this.data("index"),
        		 field = $this.data("field"),
        		 row = rows[index];
        		 if(loginUserType==3||loginUserType==4){
        			 _open({type:'edit',id:row.id, readonly:row.id==1||row.id==2});
        		 }
        	 });
         }
	});
	
	var fieldTitles = {
			'domainUser':'域/用户',
			'right':'权限'
	};
	
	function _open(cfg){
		oc.resource.loadScript('resource/module/system/role/js/roleDetail.js',function(){
			oc.module.system.role.open($.extend({
				datagrid:datagrid
			},cfg));
		});
	}
	var _initWindow = {
			domainUser:function(id,dlg){
				oc.ui.datagrid({
					selector:dlg.find(".user_domain_datagrid"),
					loadFilter:function(d){
						d.total=d ? d.length : 0;
						d.rows=d ? d : [];
						return d;
					},
					loader:function(param,success){
				    	oc.util.ajax({
				    		url:oc.resource.getUrl('system/role/getUserDomain.htm'),
				    		data:{id:id},
				    		failureMsg:'加载用户域数据失败！',
				    		async:false,
				    		success:function(data){
				    			var d = data.data,
				    			users = {},
				    			result = [];
				    			for(var i=0; i<d.length; i++){
				    				var item = d[i],
				    				id = item.userId,
				    				user = users[id];
				    				if(user){
				    					user.domainName += "," + item.domainName;
				    				}else{
				    					result.push(item);
				    					users[id] = item;
				    				}
				    			}
				    			success(result);
				    		}
				    	});
				    },
				    pagination:false,
					columns:[[
						{field:'userStatus',title:'状态',width:40,formatter: function(value,row,index){
							return 1 == value ? '启用':'停用';
						}},
				        {field:'userAccount',title:'用户名',width:100},
				        {field:'userName',title:'姓名',width:100},
				        {field:'domainName',title:'域名',width:120,ellipsis:true}
			         ]]
				});
			},
			right:function(id, div, e){
				var li_div = div.html('<div class="oc-role-conmiddle li_div" style="position: absolute;padding: 5px;z-index: 1002;"/>')
				.find(".li_div").first();
				oc.util.ajax({
		    		url:oc.resource.getUrl('system/role/getRights.htm'),
		    		data:{id:id},
		    		failureMsg:'加载数据失败！',
		    		success:function(result){
		    			var data = result.data||[],
		    			html = "<ul class='oc-role-ulbg'>";
						if(data.length>0){
							for(var i=0; i<data.length;i++){
								if(id!=1&&id!=2&&data[i].roleId!=0&&!data[i].roleId){
									continue;
								}
								html += "<li>"+data[i].rightName+"</li>";
							}
							//如果是管理者或者域管理员,多显示一个‘系统管理’
							html +=addSystemItem(id);
						}
						html=="<ul class='oc-role-ulbg'>"&&(html += "<li>无</li>");
						li_div.html(html + "</ul>");
						oc.util.showFloat(li_div,e);
		    		}
		    	});
			}
	};
	
	function _openModel(row){
		if(row.field=='right'){
			var div = $("body>.oc_float_div_role");
			if(div.size()==0){
				div = $("<div class='oc_float_div_role oc-role-contop'/>")
				.appendTo("body");
			}
			_initWindow[row.field](row.id, div, row.e);
			return ;
		}
		var dlg=_dialog=$("<div class='oc-dialog-float'></div>").dialog({
			content:"<div class='user_domain_datagrid'></div>",
			title:row.name+'/' + fieldTitles[row.field],
			height:200,
			width:400,
			modal:false
		});
		oc.util.showFloat(dlg,row.e);
		_initWindow[row.field](row.id, dlg, row.e);
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
	
	function addSystemItem(id){
		//如果是管理者或者域管理员,多显示一个‘系统管理’
		if(id==1 || id==2){
			return "<li>系统管理</li>";
		}
		return '';
	}
})(jQuery);