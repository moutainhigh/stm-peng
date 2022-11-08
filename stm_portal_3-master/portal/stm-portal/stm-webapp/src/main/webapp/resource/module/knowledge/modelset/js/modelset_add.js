(function($){
	function ModelSetAdd(opts){
		this.args = opts;
		this.open = function(){
			this._open();
		}
	}
	
	ModelSetAdd.prototype = {
			_id:"oc_module_knowledge_modelset_add",
			_mainDiv:0,
			_form:0,
			_cacth:{},
			_status:0,
			dialog:0,
			_open:function(){
				var target = this,
				status = false,
				isCancelBtn = false,
				isOkBtn = false,
				dialog = $("<div/>").dialog({
					href:oc.resource.getUrl('resource/module/knowledge/modelset/modelset_add.html'),
					title:'添加Sysoid',
					height:400,
					width:400,
					buttons:[{
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							isOkBtn = true;
							dialog.dialog("close");
						}
					},{
						text:'取消',
						iconCls:"fa fa-times-circle",
						handler:function(){
							isCancelBtn = true;
							dialog.dialog("close");
						}
					}],
					resizable:true,
					cache:false,
					onLoad:function(){
						target._init(dialog);
					},
					onBeforeClose:function(){
						if(isCancelBtn||target._status){
							return true;
						}
						if(isOkBtn){
							isOkBtn = false;
							target._status = target._save();
							return target._status;
						}
						if(!target._form.validate()){
							return true;
						}else{
							oc.ui.confirm('是否保存当前操作？',function(){
								target._save()&&target.args.datagrid.reLoad();
							}, function(){
								target._status = true;
								dialog.dialog("close");
							});
						}
						return false;
					},
					onClose:function(){
						target._status&&target.args.datagrid.reLoad();
					}
				});
				target.dialog = dialog;
			},
			_init:function(dialog){
				var target = this;
				this._mainDiv = dialog.find("#" + this._id).attr("id", oc.util.generateId());
				this._form = oc.ui.form({
					selector:this._mainDiv.find(".searchForm").first(),
					combobox:[{
						selector:'[name=resourceType]',
						data:[],
						onSelect:function(data){
							if(data&&data.id){
								var categorys = target._cacth[data.id];
								if(categorys){
									target._form.coms['resourceId'].load(categorys);
								}else{
									oc.util.ajax({
							    		url:oc.resource.getUrl('knowledge/modelset/getModuleType.htm'),
							    		data: {categoryId:data.id},
							    		failureMsg:'数据加载失败',
							    		success:function(datas){
							    			target._form.coms['resourceId'].load(datas);
							    			target._cacth[data.id] = datas;
							    		}
							    	});
								}
							}
						}
					},{
						selector:'[name=resourceId]',
						data:[]
					}]
				});
				this._initForm();
			},
			_initForm:function(){
				var target = this,
				type = this._form.jq.find(":input[name=type]")
				.click(function(){
					if(this.checked){
						oc.util.ajax({
				    		url:oc.resource.getUrl('knowledge/modelset/getFirstResourceType.htm'),
				    		data: {type:this.value},
				    		failureMsg:'数据加载失败',
				    		success:function(datas){
				    			target._form.coms['resourceType'].load(datas);
				    			target._form.coms['resourceId'].load([]);
				    		}
				    	});
					}
				})
				.filter(":checked").val();
				if(type){
					oc.util.ajax({
			    		url:oc.resource.getUrl('knowledge/modelset/getFirstResourceType.htm'),
			    		data: {type:type},
			    		failureMsg:'数据加载失败',
			    		success:function(datas){
			    			target._form.coms['resourceType'].load(datas);
			    		}
			    	});
				}
			},
			_save:function(){
				var flag = false,
				target = this,
				formData = this._form.val();
				if(this._form.validate()&&!this.checkSysOid(formData.sysOid)){
					oc.util.ajax({
			    		url:oc.resource.getUrl('knowledge/modelset/save.htm'),
			    		data: this._form.val(),
			    		async:false,
			    		success:function(data){
			    			var result = data.data;
			    			if(result==-1){
			    				alert("保存失败");
			    			}else{
			    				target._status = flag = result.errorCode=="OK";
			    				var msg = flag ? "保存成功" : "保存失败";
			    				if(result.errorCode=="ADD_DEVICE_TYPE_01"){
			    					msg += ",参数为空错误";
			    				} else if(result.errorCode=="ADD_DEVICE_TYPE_02"){
			    					msg += ",模型已经存在";
			    				}else if(result.errorCode=="ADD_DEVICE_TYPE_03"){
			    					msg += ",写入模型文件失败";
			    				} else if(result.errorCode=="ADD_DEVICE_TYPE_05"){
			    					msg += ",不存在此模型";
			    				}else{
			    					if(result.errorCode!="OK"){
			    						msg += ",连接模型失败";
			    					}
			    				}
				    			alert(msg);
			    			}
			    			
			    		}
			    	});
				}
				return flag;
			},
			checkSysOid:function(sysOid){
				var flag = false;
				oc.util.ajax({
		    		url:oc.resource.getUrl('knowledge/modelset/checkSysOid.htm'),
		    		data: {sysOid:sysOid},
		    		async:false,
		    		success:function(data){
		    			flag = data;
		    		}
		    	});
				flag&&alert("sysOid已经存在，请更换。。。", 'warning');
				return flag;
			}
	};
	oc.ns("oc.module.knowledge.modelsetadd");
	oc.module.knowledge.modelsetadd = {
			open:function(opts){
				new ModelSetAdd(opts).open();
			}
	}
	
})(jQuery);