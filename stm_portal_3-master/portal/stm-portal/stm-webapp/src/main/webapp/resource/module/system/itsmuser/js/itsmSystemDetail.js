(function(){
	function systemDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	systemDetail.prototype={
		constructor:systemDetail,
		_form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		_dialog:undefined,
		_saveSuccess:false,
		open:function(){
			var dlg=this._mainDiv=$('<div/>'),that=this,type=that.cfg.type,
				buttons=[{
					text:"保存",
					iconCls:"fa fa-save",
					handler:function(){
						var wsdlURL = that._form.find("input[name='wsdlURL']").val();
						var desc = that._form.find("input[name='describle']").val();
						//判断URL是否合法
						var pattern = /^http.+\\?(wsdl|WSDL)$/;
//						console.log(wsdlURL);
						if(!wsdlURL.match(pattern)) {
							alert("URL必须以http开头,?wsdl结尾");
							return;
						}
						if(desc !=undefined && desc.length > 100) {
							alert("描述不能超过100个字!","danger");
							return;
						}
						
						that._save(that);
						if(that._saveSuccess){
							if(that.cfg.callback){
								that.cfg.callback();//关闭弹出框后刷新域列表
							}
							dlg.dialog('close');
						};
					}
				},{
					text:'关闭',
					iconCls:"fa fa-times-circle",
					handler:function(){
						dlg.dialog('close');
					}
				}];
			this._dialog = dlg.dialog({
				href:oc.resource.getUrl('resource/module/system/itsmuser/itsmSystemDetail.html'),
				title:((type == 'edit') ? '编辑' : '添加') +'系统',
				height:350,
				width:500,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg);
					(type=='edit')&& that._loadForm(that);
				}
			});
		},
		_defaults:{
			type:'add',//界面类型 可取add、update、view 分别表示增删改查
			id:undefined,//数据id
//			saveCall:undefined//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#oc_module_itsmuser_itsmsystem_detail',
		_init:function(dlg){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			this._initForm();
		},
		_initForm:function(){
			var that = this;
			if(!that._form) {
				this._form=oc.ui.form({
					selector:this._mainDiv.find('.oc-form')
				});
			}
		},
		_save:function(that){
			if(that._form.validate()){
				oc.util.ajax({
					url : oc.resource.getUrl((that.cfg.type == 'add') ? 'system/itsmUser/saveItsmSystem.htm': 'system/itsmUser/updateItsmSystem.htm'),
					data : that._form.val(),
					async:false,
					success : function(data) {
//						console.log(data);
						if(data.code == 200){
							that._saveSuccess = true;
						}
					},
					successMsg:"系统"+((that.cfg.type == 'add')?"添加":"更新")+"成功"
				});
			}
		
		},
		_loadForm:function(that) {
			oc.util.ajax({
				url:oc.resource.getUrl('system/itsmUser/getItsmSystemById.htm'),
				data:{
					id:that.cfg.id
				},
				async:false,
				successMsg:null,
				success:function(data){
					that._form.val(data.data);
				}
			});
		}
	};
	
	oc.ns('oc.module.itsmsystem.detail');
	
	oc.module.itsmsystem.detail={
		open:function(cfg){
			var detail=new systemDetail(cfg);
			detail.open();
		}
	};
})(jQuery);