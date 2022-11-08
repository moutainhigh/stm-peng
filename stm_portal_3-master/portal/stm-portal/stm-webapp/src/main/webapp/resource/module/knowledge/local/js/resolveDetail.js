(function($) {
	function ResolveDetail(cfg){
		this.cfg = $.extend({},cfg);
	}
	
	ResolveDetail.prototype={
			constructor : ResolveDetail,
			cfg:undefined,
			_resolveContainer:undefined,
			_resolveDialog:undefined,
			_resolveForm:undefined,
			_umeditor:undefined,
			open:function(){
				var that = this,dlg = that._resolveContainer = $('<div/>'),type = that.cfg.type;
				var buttons=[];
				if(type!="view"){
					buttons.push({
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							if(that._saveResolve()){
								dlg.dialog('close');
							}else
								return false;
						}
					});
				}
				buttons.push({
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						dlg.dialog('close');
					}
				});
				that._resolveDialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/knowledge/local/resolveDetail.html'),
					title : '解决方案-'+((type == 'edit') ?'编辑' : (type == 'view') ?'查看' : '添加'),
					height : 480,
					resizable : false,
					cache : false,
					modal:false,
					onLoad : function() {
						that._initForm();
						type!="add"&&that._loadForm();
					},
					onClose:function(){
						that._umeditor.destroy();//关闭弹出框时销毁umeditor对象
						dlg.dialog('destroy');//关闭弹出框时销毁弹出框对象
						if(that.cfg.callback){
							that.cfg.callback();//关闭弹出框后刷新域列表
						}
					},
					buttons :buttons
				});
			},
			_initForm:function(){
				var that = this,type = that.cfg.type;
				var sessionId;
				oc.util.ajax({
					url : oc.resource.getUrl('system/login/getSessionId.htm'),
					successMsg : null,
					async:false,
					success : function(data) {
						sessionId = data.data;
					}
				});
				that._resolveForm = oc.ui.form({
					selector:that._resolveContainer.find(".knowledge-local-resolve-detail-form"),
					multiFileBox:{
						selector:'#file_upload',
						uploader:oc.resource.getUrl('platform/file/fileUpload.htm?jsessionid='+sessionId),
						showSelector:'#showFileDiv',
						fileItem:'<div id="${fileId}" class="uploadify-queue-item">\
			                    <div class="cancel">\
								<a href="javascript:void(0);">X</a>\
		                    </div>\
		                    <span class="fileName">${fileName}</span>\
		                </div>',
		                uploadLimit:6
					}
				});
				that._umeditor = UM.getEditor('resolveContent');
				$(".edui-container #resolveContent").width("100%");
//				that._umeditor.addListener("selectionchange",function(){
//					that._checkContentLength();
//				});
				
			},
			_loadForm:function(){
				var that = this,id=that.cfg.id,type=that.cfg.type,resolve=that.cfg.resolve;
				if(resolve!=undefined){
					that._resolveForm.val(resolve);
					that._umeditor.setContent(resolve.resolveContent,false);
					if(resolve.fileIds!=undefined && resolve.fileIds!=[]){
						oc.util.ajax({
							url:oc.resource.getUrl("knowledge/local/queryFileModelList.htm"),
							data:{fileIds:resolve.fileIds.join()},
							successMsg:"",
							success:function(data){
								var files = data.data;
								for(var i=0;i<files.length;i++){
									var file = files[i];
									file.id = file.fileId;
									file.name = file.fileName;
								}
								if(files && files.length>0){
									that._resolveForm.multiFileVal(files);
								}
								if(type=="view"){
									that._resolveForm.selector.find("[name=resolveTitle]").attr("disabled",true);
									that._umeditor.setDisabled();
									that._resolveForm.selector.find("#file_upload").hide();
									that._resolveForm.selector.find("#showFileDiv").find(".cancel").hide();
								}
							}
						});
					}
//					that._checkContentLength();
				}else{
					oc.util.ajax({
						url:oc.resource.getUrl("knowledge/local/getKnowledgeResolveById.htm"),
						data:{id:id},
						successMsg:"",
						success:function(data){
							if(data.data){
								that.cfg.resolve = data.data;
								that._loadForm();
							}
						}
					});
				}
			},
			_checkContentLength:function(){
				var that = this,totalNum = 3500;
				var textContent =  that._umeditor.getContentTxt();
				that._resolveForm.find("#numOfInput").text(textContent.length);
				that._resolveForm.find("#numOfSurplus").text(totalNum-textContent.length);
				if(textContent.length>totalNum){
					that._resolveForm.find("#normalLength").hide();
					that._resolveForm.find("#longLength").show();
				}else{
					that._resolveForm.find("#normalLength").show();
					that._resolveForm.find("#longLength").hide();
				}
			},
			_saveResolve:function(){
				var that = this,flag = false;
				if(that._resolveForm.validate()){
					resolve = that._resolveForm.val(),multiFile=that._resolveForm.multiFileVal();
					var textContent =  that._umeditor.getContentTxt();
					resolve.resolveContent = that._umeditor.getContent();
					if(resolve.resolveContent==undefined || $.trim(resolve.resolveContent)==""){
						flag = false;
						alert("解决方案内容不能为空！", 'danger');
					}else{
						if(resolve.resolveContent.length>5000){
							flag = false;
							alert("解决方案允许最大长度为5000字符，当前长度："+resolve.resolveContent.length+"字符！", 'danger');
						}else{
							if(multiFile.fileIds && multiFile.fileIds!=""){
								resolve.fileIds = multiFile.fileIds.split(",");
							}else{
								resolve.fileIds = [];
							}
							if(that.cfg.callback){
								that.cfg.callback(resolve);
							}
							flag = true;
						}
					}
				}
				return flag;
			}
	};
	function disableBtn(str) {
        var div = document.getElementById('btns');
        var btns = domUtils.getElementsByTagName(div, "button");
        for (var i = 0, btn; btn = btns[i++];) {
            if (btn.id == str) {
                domUtils.removeAttributes(btn, ["disabled"]);
            } else {
                btn.setAttribute("disabled", "true");
            }
        }
    }
	oc.ns('oc.module.knowledge.local');
	oc.module.knowledge.resolve = {
		open : function(cfg) {
			new ResolveDetail(cfg).open();
		}
	};
})(jQuery);