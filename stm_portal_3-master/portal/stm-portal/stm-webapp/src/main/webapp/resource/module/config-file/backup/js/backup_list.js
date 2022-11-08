$(function(){
	var mainDiv=$("#oc_module_config_backup_main").attr('id',oc.util.generateId()).panel({
		fit:true,
		isOcAutoWidth:true
	});
	var datagrid;
	var typeFmt = {1:'每日',2:'每周',3:'每月',4:'每年'};
	var octoolbar = {},user=oc.index.getUser();
	octoolbar.left = ["<span style='font-size:14px;padding-left:5px;'>配置变更管理-备份计划</span>"];
	if(user.systemUser==true){
		octoolbar.right = [{
			iconCls: 'fa fa-plus',
			text:"添加",
			onClick: function(){
				  oc.resource.loadScript('resource/module/config-file/backup/js/backup_add.js',function(){
					  oc.module.config.plan.open({
						  type:'add',
						  datagrid:datagrid
					  });
				  });
			}
		},'&nbsp;',{
			iconCls: 'fa fa-trash-o',
			text:"删除",
			onClick: function(){
				var checkedObj = datagrid.selector.datagrid('getChecked'),ids=[];
				for(var i=0;i<checkedObj.length;i++){
					if(checkedObj[i].id>5) ids.push(checkedObj[i].id);
				}
				if(ids.length<1){
					alert("请选择要删除的计划");
					return;
				}
				$.messager.confirm('提示','确认要删除备份计划？',function(r){
					if(r){
						oc.util.ajax({
							url:oc.resource.getUrl("portal/config/plan/batchRemove.htm"),
							data:{ids:ids.join()},
							success:function(){
								datagrid.reLoad();
							}
						})
					}
				})
			}
		}];
	}
	var dataSource = {
			selector:mainDiv.find('.oc_module_config_backup_datagrid'),
			pageSize:$.cookie('pageSize_backup_list')==null ? 15 : $.cookie('pageSize_backup_list'),
			selectOnCheck:false,
			checkOnSelect:false,
			url:oc.resource.getUrl('portal/config/plan/getPager.htm'),
			columns:[[
		         {field:'id',checkbox:true},
		         {field:'name',title:'任务名称',sortable:true,align:'left',width:'20%',formatter:function(value){
		        	 return "<span title='"+value+"' class='oc-pointer-operate'>"+(value.length>10?(value.substring(0,10)+"..."):value)+"</span>";
		         }},
		         {field:'entryName',title:'创建人',sortable:true,align:'center',width:'10%'},
		         {field:'entryTimeStr',title:'创建时间',sortable:true,align:'center',width:'20%'},
		         {field:'type',title:'备份周期',sortable:true,align:'center',width:'10%',
		        	 formatter:function(value,row,rowIndex){
		        		 return typeFmt[value];
		        	 }},
		         {field:'desc',title:'描述',sortable:true,align:'center',width:'40%',formatter:function(value){
		        	 return "<span title='"+(value?value:"")+"' class='oc-pointer-operate'>"+
		        	 	(value&&value.length>30?(value.substring(0,30)+"..."):(value?value:""))+"</span>";
		         }}
		     ]],
		    octoolbar:octoolbar,
		    onCheckAll:function(rows){
		    	$("input[type='checkbox'][name='id']").each(function(){
					if($(this).val()<5){
						$(this).attr("checked",false)
					}
				})
		    },
			onClickCell:function(rowIndex, field, value){
				var rowData = datagrid.selector.datagrid("getRows")[rowIndex];
				if(field=="name"){
					oc.resource.loadScript('resource/module/config-file/backup/js/backup_add.js',function(){
						  oc.module.config.plan.open({
							  type:'edit',
							  id:rowData.id,
							  datagrid:datagrid
						  });
					  });
				}
			},
			onLoadSuccess:function(data){
				$("input[type='checkbox'][name='id']").each(function(){
					if($(this).val()<5){
						$(this).attr("disabled",true);
					}
				})
			}
		};
	datagrid = oc.ui.datagrid(dataSource);
	
	//cookie记录pagesize
	var paginationObject = mainDiv.find('.oc_module_config_backup_datagrid').datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_backup_list');
				$.cookie('pageSize_backup_list',pageSize);
			}
		});
	}
})