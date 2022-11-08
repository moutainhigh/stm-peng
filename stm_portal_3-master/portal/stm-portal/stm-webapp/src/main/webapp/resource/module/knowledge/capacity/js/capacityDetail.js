(function($) {
	var capacityDetail;
	function CapacityDetail(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
		this._capacityKnowledgeId = cfg.id;
	}
	
	CapacityDetail.prototype={
			constructor : CapacityDetail,
			selector:undefined,
			cfg:undefined,
			_capacityKnowledgeId:undefined,
			_dialog:undefined,
			_dialogDiv:undefined,
			_form:undefined,
			_capacityKnowledgeId:0,
			_deploymentGrid:undefined,
			_capacityKnowledgeInfo:undefined,
			_deploymentResult:[],
			_files:[],
			_defaults:{
				type : 'add',
				id : undefined
			},
			initForm:function(){
				var that = this;
				that.selector.find("#uploadModel").accordion({
					fit : false,
					border : true,
					height : '150px'
				});
				that.selector.find("#deploymentResult").accordion({
					fit : false,
					border : true,
					height : '215px'
				});
				that.selector.find("form.uploadModelForm").show();
				that.selector.find("form.reDeploymentInfo").hide();
				that.selector.find("#deployment").linkbutton("RenderLB",{
					text:'部署',
					onClick : function(){
						var fileId = that._form.multiFileVal().fileIds;
						if(fileId){
							oc.util.ajax({
								url:oc.resource.getUrl("knowledge/capacity/deployment.htm"),
								data:{fileId:fileId},
								successMsg:"",
								success:function(data){
									if(data.data){
										that._capacityKnowledgeId = data.data.id;
										that.loadForm();
									}
								}
							});
						}else{
							alert("请选择模型文件！");
							return false;
						}
					}
				});
				
				
				that._form = oc.ui.form({
					selector:that.selector.find(".uploadModelForm"),
					multiFileBox:{
						selector:'#file_upload',
						showSelector:'#showFileDiv',
						multi:false,
						removeCompleted:true,
						fileTypeDesc: '*.zip;',//文件后缀描述
		                fileTypeExts: '*.zip;',//文件后缀限制
						queueSizeLimit:1,
						fileItem:'<div id="${fileId}" class="uploadify-queue-item">\
			                    <div class="cancel">\
								<a href="javascript:void(0);">X</a>\
		                    </div>\
		                    <span class="fileName">${fileName}</span>\
		                </div>',
		                uploadSuccess:function(file, data, response){
		                	that._files.push(file);
		                	that.selector.find(".uploadModelForm").find('#file_upload').uploadify('settings','buttonText','重新上传');
		                	var fileIds = that._form.multiFileIds;
		                	if(fileIds!=undefined && fileIds.length>0){
		                		for(var i=0;i<fileIds.length;i++){
		                			fileIds[i]!=data&&that._form._deleteMultiFile(fileIds[i]);
		                		}
		                	}
		                	for(var j=0;j<that._files.length;j++){
		                		var myFile = that._files[j];
		                		if(myFile.id != file.id){
		                			that.selector.find(".uploadModelForm").find('#file_upload').uploadify('cancel',myFile.id);
		                		}
		                	}
		                }
					}
				});
			},
			initResultGrid:function(){
				var that = this;
				that._deploymentGrid = oc.ui.datagrid({
					selector : that.selector.find(".deployment_result_datagrid"),
					url : oc.resource.getUrl('knowledge/capacity/queryDeploymentResultById.htm'),
					queryParams:{id:that._capacityKnowledgeId},
					fit:true,
					pagination : false,
					selectedCfg:{
						field:'isChecked',
						fieldValue:1
					},
					columns : [[ 
			              {field : 'id',title : '编号',width : 20}, 
			              {field : 'name',title : '组件',width : 30}, 
			              {field : 'status',title : '结果',width : 60,formatter:function(value,row){
			            	  if(!value){
			            		  that._deploymentResult.push(row.id)
			            		  return "失败";
			            	  }else{
			            		  return "成功";
			            	  }
			              }}
		             ]],
		             onLoadSuccess:function(){
		            	 that.reDeployment();
		             }
				});
			},
			reDeployment:function(){
				var that = this;
				that.selector.find("form.uploadModelForm").hide();
				that.selector.find("form.reDeploymentInfo").show();
				oc.util.ajax({
					url:oc.resource.getUrl("knowledge/capacity/getCapacityKnowledgeInfo.htm"),
					data:{id:that._capacityKnowledgeId},
					successMsg:"",
					async:false,
					success:function(data){
						var obj = data.data
						if(obj){
							that.selector.find("form.reDeploymentInfo").find("#modelName").text(obj.name);
							that.selector.find("form.reDeploymentInfo").find("#deployTime").text(new Date(obj.deployTime).stringify("yyyy-mm-dd hh:MM:ss"));
						}
					}
				});
				
				if(that._deploymentResult.length>0){
					that.selector.find("#reDeploymentShowInfo span:first").text("部分组件未部署或部署失败，请重新部署。");
					that.selector.find("#reDeployment").linkbutton("RenderLB",{
						text:'重新部署',
						onClick : function(){
							oc.util.ajax({
								url:oc.resource.getUrl("knowledge/capacity/reDeployment.htm"),
								data:{id:that._capacityKnowledgeId},
								successMsg:"",
								success:function(data){
									that.loadForm();
								}
							});
						}
					});
				}else{
					that.selector.find("#reDeploymentShowInfo").hide();
				}
			},
			loadForm:function(type){
				this.initResultGrid();
			},
			open:function(){
				var dlg = this._dialogDiv = $('<div/>'), that = this, type = that.cfg.type;
				this._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/knowledge/capacity/capacityDetail.html'),
					title : '模型部署',
					height : 450,
					resizable : false,
					cache : false,
					onLoad : function() {
						that.selector = $("#oc-knowledge-capacity-detail").attr("id",oc.util.generateId());
						that.initForm();
						if(that.cfg.type!="add")
							that.loadForm();
					},
					onClose:function(){
						dlg.dialog('destroy');
						if(that.cfg.callback){
							that.cfg.callback();//关闭弹出框后刷新域列表
						}
					},
					buttons : [{
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							dlg.dialog('close');
						}
					}]
				});
			}
	};
	
	oc.ns('oc.module.knowledge.capacity');
	oc.module.knowledge.capacity = {
		open : function(cfg) {
			knowledgeDetail = new CapacityDetail(cfg);
			knowledgeDetail.open();
		}
	};
})(jQuery);