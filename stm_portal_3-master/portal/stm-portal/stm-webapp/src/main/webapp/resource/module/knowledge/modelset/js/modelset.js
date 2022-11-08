(function($){
	function ModelSet(){
		this.open = function(){
			this._init();
//			$("#mainMenuContent").find(".datagrid-wrap").css("height","900px");
			$("#mainMenuContent").find(".datagrid-view").css("height","85%");
			$("#mainMenuContent").find(".datagrid-body").css("height","85%");
		}
	}
	
	ModelSet.prototype = {
			_id:"oc_module_knowledge_modelset_main",
			_mainDiv:0,
			_queryForm:0,
			_datagrid:0,
			_init:function(){
				var target = this,
				moduleTypes = {};
				this._mainDiv = $("#" + this._id).attr("id", oc.util.generateId());
				this._queryForm = oc.ui.form({
					selector:this._mainDiv.find(".searchForm").first(),
					combobox:[{
						selector:'[name=resourceType]',
						data:[{id:'Host', name:'主机'},
						      {id:'NetworkDevice', name:'网络设备'}]
					}]
				});
				oc.util.ajax({
		    		url:oc.resource.getUrl('knowledge/modelset/getAllModules.htm'),
		    		failureMsg:'数据加载失败',
		    		async:false,
		    		success:function(datas){
		    			moduleTypes = datas;
		    		}
		    	});
				this._datagrid = oc.ui.datagrid({
					selector:this._mainDiv.find(".oc_module_knowledge_modelset_main_datagrid").first(),
					queryForm:this._queryForm,
					queryConditionPrefix:'',
					url:oc.resource.getUrl('knowledge/modelset/page.htm'),
					octoolbar:{
						left:[{
								text:'添加',
								iconCls:'fa fa-plus',
								onClick:function(){
									target._open();
								}
						}],
						right:[this._queryForm.selector]
					},
					columns:[[
					         {field:'resourceId',title:'资源',width:30,formatter: function(value,row,index){
					        	 return moduleTypes[value];
					         }},
					         {field:'sysOid',title:'System OID',width:40},
					         {field:'resourceType',title:'资源类型', width:30, formatter:function(value){
					        	 return value == 'Host'  ? '主机' : (value =='NetworkDevice' ? '网络设备' : '');
					         }},
					         {field:'vendorName',title:'厂商',width:40},
					         {field:'modelNumber',title:'型号',width:30},
					         {field:'isSystem',title:'操作',width:30,formatter:function(value,row){
					        	 return "<a data-sysoid='"+row.sysOid+"' class='"+(value ? "disable" : "enabled") +"'>删除</a>";
					         }}
				     ]],
				     onLoadSuccess:function(){
				    	 var panel = target._datagrid.selector.datagrid("getPanel");
				    	 panel.find(".enabled").click(function(){
				    		 var btn = this;
				    		 oc.ui.confirm('确认删除？',function(){
				    			 var sysOid = $(btn).data('sysoid');
					    		 oc.util.ajax({
					     	    		url:oc.resource.getUrl('knowledge/modelset/delete.htm'),
					     	    		data:{sysOid:sysOid},
					     	    		failureMsg:'操作失败！',
					     	    		success:function(data){
					     	    			if(data.code==200){
					     	    				var result = data.data;
					     	    				if(result.errorCode=="OK"){
					     	    					alert("删除成功");
					     	    					target._datagrid.reLoad();
					     	    				}else{
					     	    					if(result.errorCode=="ADD_DEVICE_TYPE_03"){
					     	    						alert("写文件失败", "danger");
					     	    					} else if(result.errorCode=="ADD_DEVICE_TYPE_04"){
					     	    						alert("此sysoid不存在", "warning");
					     	    					} else if(result.errorCode=="ADD_DEVICE_TYPE_05"){
					     	    						alert("此sysoid是系统默认，不允许删除", "warning");
					     	    					} else{
					     	    						alert("删除失败", "danger");
					     	    					}
					     	    				}
					     	    			}
					     	    		}
					     	    	});
								});
				    	 });
				     }
				});
			},
			_open:function(){
				var target = this;
				oc.resource.loadScript('resource/module/knowledge/modelset/js/modelset_add.js',function(){
					oc.module.knowledge.modelsetadd.open({
						datagrid:target._datagrid
					});
				});
			}
	};
	
	var modelSet = new ModelSet();
	modelSet.open();
})(jQuery);