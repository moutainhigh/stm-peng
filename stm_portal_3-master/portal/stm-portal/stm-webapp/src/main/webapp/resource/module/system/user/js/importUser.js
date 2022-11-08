(function(){
	function importUser(fn){
		this.fn=fn;
	}
	
	importUser.prototype={
		constructor:importUser,
		_cancelBtn:0,
		_form:0,
		open:function(){
			var dlg=$('<div/>'),that=this;
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/system/user/importUser.html'),
				title:'导入用户信息',
				height:200,
				width:500,
				buttons:[{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						var form = that._form,
						filebox=form.filebox[0],
						filePath = filebox.selector.filebox('getValue');
						if(filePath.substring(filePath.lastIndexOf(".")+1).toUpperCase()!='XLS'){
							alert('文件格式不正确，请重新选择...')
							return ;
						}
						oc.ui.confirm('默认密码都为“111111”，请确认!',function(){
							if(form.filebox.length>0){
								if(filebox.selector.filebox("getValue")){
									form.jq.ajaxSubmit($.extend({}, filebox,{
										url : oc.resource.getUrl('system/user/importUsers.htm'),
										type : 'POST',
										iframe : true,
										data:form.val(),
										timeout: 300000,
										async:false,
										dataType:'json',
										success : function(responseText, statusText, xhr, $form) {
											if(responseText=="success"){
												alert("导入成功");
												that.fn&&that.fn();
											}else{
												alert(responseText.indexOf('[')==0 ? ('发现重复录入的用户信息'+responseText) : responseText);
											}
											dlg.window("close");
										},
										error : function(XMLHttpRequest, textStatus, errorThrown) {
											alert('文件上传失败！');
											log(XMLHttpRequest, textStatus, errorThrown);
										}
									}));
								}
							}
							
						});
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
					that._init();
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
		_id:'#oc-module-importUser',
		_init:function(){
			this._mainDiv = $(this._id).attr("id",oc.util.generateId());
			var that = this;
			this._form = oc.ui.form({
				selector:that._mainDiv.find("form").first(),
				filebox:[{selector:'[name=file]'}]
			});
		}
	};
	
	oc.ns('oc.module.system.importuser');
	
	oc.module.system.importuser={
		open:function(fn){
			new importUser(fn).open();
		}
	};
})(jQuery);