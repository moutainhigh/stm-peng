(function($) {
	function ResourceGroupDetail(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
		this.loginUser = oc.index.getUser();
	}
	ResourceGroupDetail.prototype={
			constructor : ResourceGroupDetail,
			selector:undefined,
			cfg:undefined,
			loginUser:undefined,
			groupDetailForm:undefined,
			resourcePickTree:undefined,
			_mainDiv:undefined,
			pickTreeCfg:{},
			resourceIds:undefined,
			_loadDomainId:undefined,
			_loadGroupId:undefined,
			_closeFlag:false,
			open : function() {//打开添加、编辑域弹出框
				var dlg = this._mainDiv = $('<div/>'), that = this, type = that.cfg.type;
				this._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/system/resourcegroup/resourcegroupDetail.html'),
					title : ((type == 'edit') ? that.cfg.name+' / 编辑' : '添加') + '资源集合',
					height : 450,
					resizable : false,
					cache : false,
					onLoad : function() {
						that.selector = $("#oc_module_system_resource_group_detail").attr("id",oc.util.generateId());
						that._initForm(type,function(){
							(type != 'add') && that._loadForm();//如果是编辑，加载域基本信息
						});
					},
					onClose:function(){
						dlg.dialog('destroy');
						if(that._closeFlag){
							if(that.cfg.callback){
								that.cfg.callback();
							}
						}
					},
					buttons : [{
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							that._closeFlag=true;
							that.saveForm();
						}
					},{
						text:"取消",
						iconCls:"fa fa-times-circle",
						handler:function(){
							that._closeFlag=false;
							dlg.dialog('close');
						}
					}]
				});
			},
			_initForm:function(type,fn){
				var that = this;
				var initDomainId = "";
				var getLeftGridData = function(id){
					oc.util.ajax({
						url:oc.resource.getUrl('resourceManagement/resourceCategory/getAllResourceInstance.htm'),
						data:{domainId:id},
						successMsg:"",
						success:function(data){
							that.resourcePickTree.reload(data.data,"l");
						}
					});
				}
				
				that.groupDetailForm = oc.ui.form({
					selector:that.selector.find(".resource_group_form"),
					combobox:[{
						selector:'[name=domainId]',
						filter:function(data){
							var domainData = data.data;
							if(domainData!=null && domainData.length>0){
								initDomainId = domainData[0].id;
							}
							return domainData;
						},
						placeholder:null,
						url:oc.resource.getUrl('system/domain/queryAllDomain.htm'),
						onSelect:function(r){
							if(type == 'add'){
								that.resourcePickTree.reload([],"r");
								getLeftGridData(r.id);
							}
							if(type=="edit"){
								if(r.id == that._loadDomainId){
									that._loadForm();
								}else{
									that.resourcePickTree.reload([],"r");
									getLeftGridData(r.id);
								}
							}
						},
						onLoadSuccess:function(){
							if(type == 'add'){
								getLeftGridData(initDomainId);
							}
							if(fn)fn();
						}
					}]
				});
				
				that.pickTreeCfg = {
						  selector:that.selector.find("#groupPicktree"),
						  dataType:'array',
						  isInteractive:true,
						  requestType:'sync',
						  leftCallback:{
							  onAsyncSuccess:function(){
							  }
						  },
						  search:{
							  searchText:'搜索资源名称IP类型',
							  onSearch:function(st){
								  var checkedResource = that.resourcePickTree.getRightTreeData();
								  var ids = [];
								  if(checkedResource!=undefined && checkedResource!=null){
									  for(var i=0;i<checkedResource.length;i++){
										  var selectData = checkedResource[i];
										  if(!selectData.isParent){
											  ids.push(selectData.id);
										  }
									  }
								  }
								  var domainId = that.selector.find(".resource_group_form #domainId").combobox('getValue');
								  oc.util.ajax({
									  url:oc.resource.getUrl("resourceManagement/resourceCategory/getLeftResourceInstanceBySearchContent.htm"),
									  data:{ids:ids.join(),domainId:domainId,searchContent:st},
									  successMsg:"",
									  success:function(data){
										  if(data.data){
											  that.resourcePickTree.reload(data.data,'l');
											  st&&that.resourcePickTree.leftTreeObj.expandAll(true);
										  }	
									  }
								  });
							  }
						  }
				};
//				that.pickTreeCfg.lUrl = oc.resource.getUrl('resourceManagement/resourceCategory/getAllResourceInstance.htm?domainId='+initDomainId);
				that.resourcePickTree = oc.ui.picktree(that.pickTreeCfg);
//				that.resourcePickTree.reload([],"l");
			},
			_loadForm:function(){
				var that = this, type = that.cfg.type,groupId=that.cfg.id;
				oc.util.ajax({
					url:oc.resource.getUrl("system/resourceGroup/getResourceGroup.htm"),
					data:{groupId:groupId},
					successMsg:"",
					success:function(data){
						var groupData = data.data;
						that.groupDetailForm.val(groupData);
						that._loadDomainId = groupData.domainId;
						that._loadGroupId = groupData.id;
						that.resourceIds = groupData.resourceInstanceIds;
						oc.util.ajax({
							url:oc.resource.getUrl('resourceManagement/resourceCategory/getLeftResourceInstance.htm'),
							data:{ids:groupData.resourceInstanceIds.join(),domainId:groupData.domainId},
							successMsg:"",
							success:function(data){
								that.resourcePickTree.reload(data.data,"l");
							}
						});
						oc.util.ajax({
							url:oc.resource.getUrl('resourceManagement/resourceCategory/getRightResourceDef.htm'),
							data:{ids: groupData.resourceInstanceIds.join()},
							successMsg:"",
							success:function(data){
								that.resourcePickTree.reload(data.data,"r");
							}
						});
						if(type=="edit"){
							//如果资源组的创建人不是本人，不允许编辑资源组名称
							if(that.loginUser.id!=groupData.entryId){
								that.groupDetailForm.selector.find("#domainId").combobox("disable");
								that.groupDetailForm.selector.find("#resourceGroupName").attr("disabled","disabled");
							}
						}
					}
				});
			},
			saveForm:function(){
				var that = this, type = that.cfg.type;
				var checkData = that.resourcePickTree.getRightTreeData();
				var checkedResourceIDs = [];
				for(var i = 0 ; i < checkData.length ; i++){
					var selectData = checkData[i];
					if(!selectData.isParent){
						checkedResourceIDs.push(selectData.id);
					}
				}
				
				if(that.groupDetailForm.validate()){
					var data = {resourceInstanceIds:""};
					if(checkedResourceIDs!=null && checkedResourceIDs!=undefined && checkedResourceIDs.length>0){
						data = {resourceInstanceIds:checkedResourceIDs.join()};
					}
					data = $.extend(data,that.groupDetailForm.val());
					oc.util.ajax({
						url:oc.resource.getUrl(type=="add"?"system/resourceGroup/insertResourceGroup.htm":"system/resourceGroup/updateResourceGroup.htm"),
						data:data,
						successMsg:"",
						success:function(data){
							if(data.data==true || data.data=='true'){
								alert("资源集合"+(type=="add"?"保存":"更新")+"成功！");
								that._mainDiv.dialog('close');
							}else if(data.data==false || data.data=='false'){
								alert("资源集合"+(type=="add"?"保存":"更新")+"失败！");
							}else if(data.data=="group name is exsit"){
								alert("资源集合名称已存在，请重命名！");
							}
						}
					});
				}
			},
			_defaults : {
				type : 'add',
				id : undefined
			}
	}
	oc.ns('oc.module.system.resourcegroup');

	oc.module.system.resourcegroup = {
		open : function(cfg) {
			new ResourceGroupDetail(cfg).open();
		}
	};
})(jQuery);