(function($){
	var mainDiv=$('#oc_module_config_warn_config').attr('id',oc.util.generateId()).panel({
			fit:true,
			isOcAutoWidth:true
		}),
		datagridDiv=mainDiv.find('.oc_module_config_warn_config_datagrid');
		var datagrid=null,user = oc.index.getUser();
		var _dialog = $('<div class="oc-dialog-float"/>');
		var toolbar = [];
		if(user.systemUser==true){
			toolbar.push({
				text:'添加',
				iconCls:'fa fa-plus',
				onClick:function(){
					openWarn("add");
				}
			},{
				text:'删除',
				iconCls:'fa fa-trash-o',
				onClick:function(){
					delWarn();
				}
			});
		}
		datagrid=oc.ui.datagrid({
			selector:datagridDiv,
			pageSize:$.cookie('pageSize_config')==null ? 15 : $.cookie('pageSize_config'),
			url:oc.resource.getUrl('portal/config/warn/config/getPage.htm?'),
			octoolbar:{
				left:["<span style='font-size:14px;padding-left:5px;'>配置变更管理-告警设置</span>"],
				right:toolbar
			},
			columns:[[
		         {field:'id',title:'-',checkbox:true,width:20},
		         {field:'name',title:'告警名称',sortable:true,width:150,formatter:function(value){
		        	 return "<span title='点我进行编辑' class='oc-pointer-operate'>"+value+"</span>";
		         }},
		         {field:'deviceNum',title:'资源数量',sortable:false,width:50,formatter:function(value,row,rowIndex){
		        	 return row.configWarnResourceBos.length>0?row.configWarnResourceBos.length:"0";
		         }},
		         {field:'warnUserName',title:'告警接收人员',sortable:false,width:100,formatter:function(value,row,rowIndex){
		        	 if(row.configWarnRuleBos.length>0){
		        		 var result="",warnUserName="";
		        		 for(var rule=0;rule<row.configWarnRuleBos.length;rule++){
		        			 result += row.configWarnRuleBos[rule].userName+",";
		        		 }
		        		 result = result.substring(0,result.length - 1);
		        		 warnUserName = result;
		        		 if(result.length > 6){
		        			 result = result.substring(0, 6) + "...";
		        		 }
		        		 return "<span title='"+warnUserName+"'>"+result+"</span>";
		        	 }
		         }},
		         {field:'warnType',title:'告警方式',sortable:false,width:50,formatter:function(value,row,rowIndex){
		        	 if(row.configWarnRuleBos.length>0){
		        		 var result="";
		        		 var emailSpan = '<span class="ico ico-email" title="邮件"></span>',
		        		 	 messageSpan = '<span class="ico ico-message" title="短信"></span>',
		        		 	 alertSpan = '<span class="ico ico-alert" title="Alert"></span>';
		        		 for(var rule=0;rule<row.configWarnRuleBos.length;rule++){
		        			 if(row.configWarnRuleBos[rule].email==1 && result.indexOf(emailSpan)<0){
		        				 result += emailSpan;
		        			 }
		        			 if(row.configWarnRuleBos[rule].message==1 && result.indexOf(messageSpan)<0){
		        				 result += messageSpan;
		        			 }
		        			 if(row.configWarnRuleBos[rule].alert==1 && result.indexOf(alertSpan)<0){
		        				 result += alertSpan;
		        			 }
		        		 }
		        		 if(result.indexOf(alertSpan)>=0){
		        			 result = result.replace(alertSpan,'') + alertSpan;
		        		 }
		        		 return result;
		        	 }
		         }},
	         ]],
	         onClickCell:function(rowIndex, field, value){
	        	 if("name" == field){
	        		 var row = $(this).datagrid('getRows')[rowIndex];
	        		 openWarn("edit",row.id,row);
	        	 }
	         }
		});
		
		//cookie记录pagesize
		var paginationObject = datagridDiv.datagrid('getPager');
		if(paginationObject){
			paginationObject.pagination({
				onChangePageSize:function(pageSize){
					var before_cookie = $.cookie('pageSize_config');
					$.cookie('pageSize_config',pageSize);
				}
			});
		}
		
	$()	
	/**
	 * 添加告警
	 */
	function openWarn(type,id,row){
		var addWarnWin = $('<div/>'),buttons=[];
		//构建dialog
		if(user.systemUser==true){
			buttons.push({
				text:'确定',
				iconCls:'fa fa-check-circle',
				handler:function(){
					oc.module.config.warn.config.save(type,id,datagrid,addWarnWin);
				}
		  	});
		}
		buttons.push({
			text:'取消',
			iconCls:'fa fa-times-circle',
			handler:function(){
				addWarnWin.dialog('close');
			}
		});
		addWarnWin.dialog({
		  title:type=="edit"?"编辑告警":"添加告警",
		  href:oc.resource.getUrl('resource/module/config-file/warn/config/addWarn.html'),
		  width:800,
		  height:510,
		  modal:true,
		  buttons:buttons,
		  onLoad:function(){
			 oc.module.config.warn.config.open(type,id,row);
		 },
		onClose:function(){
			addWarnWin.dialog('destroy');
		}
	  });
	}
	
	//删除设备
	function delWarn(){
		var ids = datagrid.getSelectIds();
		 if(ids == undefined||ids == ""){
			 alert("请勾选需要删除告警");
		 }else{
			 oc.ui.confirm("是否确认删除告警？",function(){
				 oc.util.ajax({
					 url:oc.resource.getUrl('portal/config/warn/config/batchDel.htm'),			            
					 data:{ids:ids.join()},
					 successMsg:null,
					 success:function(d){
						 if(d.code&&d.code==200){
							 datagrid.reLoad();
							 alert("删除成功");
						 }else if(d.code==299){alert(d.data);}
					 }
				 });
			 });
		 }
	}
})(jQuery);