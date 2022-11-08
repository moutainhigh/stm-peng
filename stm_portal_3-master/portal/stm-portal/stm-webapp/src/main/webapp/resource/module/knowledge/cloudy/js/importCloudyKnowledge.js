(function(){
	function importCloudyKnowledge(fn){
		this.fn=fn;
	}
	
	importCloudyKnowledge.prototype={
		constructor:importCloudyKnowledge,
		_cancelBtn:0,
		_form:0,
		open:function(){
			var dlg=$('<div/>'),that=this;
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/knowledge/cloudy/importCloudyKnowledge.html'),
				title:'导入云端知识库',
				height:200,
				width:400,
				buttons:[{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						var form = that._form;
//						if(form.validate()){
							if(form.filebox.length>0){
								var filebox=form.filebox[0];
								var filePath = filebox.selector.filebox("getValue");
								if(filePath == "") {
									alert("请选择上传文件!");
									return;
								}
								//判断上传文件的后缀名
								var flieExtension = filePath.substr(filePath.lastIndexOf('.') + 1);
								if(flieExtension == 'DB'||flieExtension == 'db') {
									filebox.selector.filebox("getValue") ? form.jq.ajaxSubmit($.extend({},filebox,{url : oc.resource.getUrl('knowledgezip/importCloudyZip.htm'),
										type : 'POST',
										iframe : true,
										timeout: 300000,
										async:false,
										success : function(responseText, statusText, xhr, $form) {
											that.fn&&that.fn();
											dlg.window("close");
										},
										error : function(XMLHttpRequest, textStatus, errorThrown) {
											alert('文件上传失败！');
											log(XMLHttpRequest, textStatus, errorThrown);
										}
									})) : oc.util.ajax(ajaxOpts);;
									
								}else{
									alert("请选择db文件!");
									return;
								}
								
							}
//						}
						
					}
				},{
					text:'取消',
					iconCls:"fa fa-times-circle",
					handler:function(){
						that._cancelBtn = true;
						dlg.window("close");
					}
				}],
				resizable:true,
				cache:false,
				onLoad:function(){
					that._init(dlg);
				},
				onBeforeClose:function(){
					if(that._cancelBtn){
						return true;
					}
					
					return true;
				}
			});
		},
		_mainDiv:undefined,
		_id:'#oc-module-importCloudyKnowledge',
		_init:function(dlg){
			this._mainDiv = $(this._id).attr("id",oc.util.generateId());
			var that = this;
			this._form = oc.ui.form({
				selector:that._mainDiv.find("form").first(),
				filebox:[{selector:'[name=file]'}]
			});
		}
	};
	
	oc.ns('oc.module.cloudyknowledge');
	
	oc.module.cloudyknowledge={
		open:function(fn){
			new importCloudyKnowledge(fn).open();
		}
	};
})(jQuery);