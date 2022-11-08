(function($) {
	var knowledgeDetail;
	function KnowledgeDetail(cfg){
		this.cfg = $.extend({}, this._defaults, cfg);
		this.knowledgeId = cfg.id;
	}
	
	KnowledgeDetail.prototype={
			constructor : KnowledgeDetail,
			cfg:undefined,
			_dialog:undefined,
			_dialogDiv:undefined,
			_form:undefined,
			_editTypeCode:undefined,
			_editParentTypeCode:undefined,
			_knowledgeResolves:[],
			knowledgeId:undefined,
			_knowledgeTypeCombobox:undefined,
			_defaults:{
				type : 'add',
				id : undefined
			},
			_closeFlag:false,
			initForm:function(callback){
				var that = this,knowledgeTypeData,type = that.cfg.type;
				
				that._knowledgeTypeCombobox = oc.ui.combobox({
					selector:'[name=knowledgeTypeCode]',
					url:oc.resource.getUrl("knowledge/type/queryKnowledgeTypeByParent.htm"),
					width:260,
					valueField:'id',
					textField:'name',
					placeholder:'--请选择--',
					filter:function(data){
						return data.data;
					},
					onLoadSuccess:function(){
						if(type!="add" && that._editTypeCode!=undefined){
							that._form.selector.find("#knowledgeTypeCode").combobox("setValue",that._editTypeCode);
							if(type=="view"){
								that._form.selector.find("#knowledgeTypeCode").combobox("disable");
							}
						}
						
					}
				});
				
				that._form = oc.ui.form({
					selector:that._dialogDiv.find(".knowledge-local-detail-form:first"),
					combotree:[{
						selector:'[name=parentCode]',
						width:300,
						url:oc.resource.getUrl("knowledge/type/queryKnoeledgeParentType.htm"),
						valueField:'id',
						textField:'name',
						pField:'parentId',
						filter:function(data){
							data = data.data
							for(var i=0;i<data.length;i++){
								if(data[i].level == 2) {
									data[i].state='closed';
								}
							}
							return data;
						},
						onSelect:function(node){
							that._knowledgeTypeCombobox.reLoad(oc.resource.getUrl("knowledge/type/queryKnowledgeTypeByParent.htm?parentId="+node.id));
						},onLoadSuccess:function(){
							if(callback && callback.setParentType){
								callback.setParentType();
							}
						}
					}]
				});
				
				this._dialogDiv.find('#btn_addResolve').linkbutton('RenderLB', {
					text:'添加解决方案',
					iconCls : 'fa fa-plus',
					onClick : function(){
						_openResolveInfo("add");
					}
				});
			},
			loadform:function(){
				
				var that = this;
				oc.util.ajax({
					url : oc.resource.getUrl('knowledge/local/getLocalknowledge.htm'),
					data : {id:that.knowledgeId},
					success : function(data) {
						var knowledge = data.data,resolves = knowledge.resolves;
						that._form.val(knowledge);
						var codes = knowledge.knowledgeTypeCode.split("-");
						that._editTypeCode = codes[codes.length-1];
						that._editParentTypeCode = codes[codes.length-2];
						that._form.selector.find("#parentCode").combotree("setValue",codes[codes.length-2]);
						if(resolves!=undefined && resolves.length>0){
							for(var i=0;i<resolves.length;i++){
								var resolve = resolves[i];
								var fileIds = [];
								if(resolve.resolveAttachments!=undefined){
									for(var j=0;j<resolve.resolveAttachments.length;j++){
										var att = resolve.resolveAttachments[j];
										fileIds.push(att.fileId);
									}
								}
								resolve.fileIds = fileIds;
								addOrUpdateResolve(resolve);
							}
						}
						if(that.cfg.type=="view"){
							that._form.disable();//如果是查看模式，禁用表单
							that._dialogDiv.find('#btn_addResolve').hide();//查看框，隐藏添加解决方案列表
							that._form.find("#resolveItem").find(".cancel").hide();
						}
					},
					successMsg:""
				});
			},
			open:function(){
				var dlg = this._dialogDiv = $('<div/>'), that = this, type = that.cfg.type;
				var buttons = [];
				if(type!='view'){
					buttons.push({
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							that._closeFlag = true;
							that.saveForm();
						}
					})
				}
				buttons.push({
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						that._closeFlag = false;
						dlg.dialog('destroy');
					}
				});
				this._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/knowledge/local/knowledgeDetail.html'),
					title : '本地知识-'+((type == 'edit') ?'编辑' : (type == 'view') ?'查看' : '添加'),
					height : 450,
					resizable : false,
					cache : false,
					onLoad : function() {
						//避免combotree还未初始化，就调用setvalue
						var callback = {};
						callback.setParentType = function(){
							if(type!="add"){
								that.loadform();
							}
						}
						that.initForm(callback);
//						if(type!="add"){
//							that.loadform();
//						}
					},
					onClose:function(){
						dlg.dialog('destroy');
						if(that._closeFlag){
							if(that.cfg.callback){
								that.cfg.callback();//关闭弹出框后刷新域列表
							}
						}
					},
					buttons : buttons
				});
			},
			saveForm:function(){
				var that = this;
				if(that._form.validate()){
					var typeVal =that._dialogDiv.find("#knowledgeTypeCode").combobox("getValue");
					if(typeVal!=undefined && typeVal!=""){
						oc.util.ajax({
							url : oc.resource.getUrl((that.cfg.type == 'add') ? 'knowledge/local/saveLocalKnowledge.htm': 'knowledge/local/updateLocalKnowledge.htm'),
							data : $.extend({},{resolvesStr:JSON.stringify(that._knowledgeResolves)},that._form.val()),
							success : function(data) {
								if(data && data.data){
									that._dialogDiv.dialog('close');
								}
							},
							successMsg:"知识"+(that.cfg.type == 'add'?"添加":"更新")+"成功"
						});
					}else{
						alert("知识分类不能为空！","danger");
					}
					
				}
			}
	};
	
	function addOrUpdateResolve(resolve){
		var flag = false;
		if(resolve){
			if(resolve.id!=undefined && resolve.id!=""){
				for(var i=0;i<knowledgeDetail._knowledgeResolves.length;i++){
					var myResolve = knowledgeDetail._knowledgeResolves[i];
					if (myResolve.id == resolve.id){
						flag = true;
						myResolve.id=resolve.id ;
						myResolve.resolveTitle = resolve.resolveTitle ;
						myResolve.resolveContent = resolve.resolveContent;
						myResolve.fileIds = resolve.fileIds;
						knowledgeDetail._dialogDiv.find("#resolveItem").find("#"+resolve.id+" span.fileName a:first").text(myResolve.resolveTitle);
					}
				}
			}else{
				resolve.id=Math.floor(Math.random()*10000);
			}
			if(!flag){
				knowledgeDetail._knowledgeResolves.push(resolve);
				createResolveItem(resolve);
			}
		}
	}
	
	
	function createResolveItem(resolve){
		var html = $('<div id="'+resolve.id+'" class="uploadify-queue-item">'
				+ '<div class="cancel">'
				+ '<a href="javascript:void(0);">X</a>' + '</div>'
				+ ' <span class="fileName"><a href="javascript:void(0);" style="text-decoration:underline">'+resolve.resolveTitle+'</a></span>' + '</div>');
		html.find("span.fileName a:first").bind("click",function(){
			if(knowledgeDetail.cfg.type=="view"){
				_openResolveInfo("view", resolve.id, resolve);
			}else{
				_openResolveInfo("edit", resolve.id, resolve);
			}
		});
		html.find("div.cancel a:first").bind("click",function(){
			deleteResolveItem(resolve);
		});
		html.appendTo(knowledgeDetail._dialogDiv.find("#resolveItem"))
	}
	
	
	function deleteResolveItem(resolve){
		knowledgeDetail._knowledgeResolves.splice(knowledgeDetail._knowledgeResolves.indexOf(resolve), 1)
		knowledgeDetail._dialogDiv.find("#resolveItem").find("#"+resolve.id).remove();
	}
	
	function _openResolveInfo(type,id,resolve){
		oc.resource.loadScript('resource/module/knowledge/local/js/resolveDetail.js', function() {
			oc.module.knowledge.resolve.open({
				type:type,
				id:id,
				resolve:resolve,
				callback:function(resolve){
					addOrUpdateResolve(resolve);
				}
			});
		});
	}
	
	oc.ns('oc.module.knowledge.local');
	oc.module.knowledge.local = {
		open : function(cfg) {
			knowledgeDetail = new KnowledgeDetail(cfg);
			knowledgeDetail.open();
		}
	};
})(jQuery);