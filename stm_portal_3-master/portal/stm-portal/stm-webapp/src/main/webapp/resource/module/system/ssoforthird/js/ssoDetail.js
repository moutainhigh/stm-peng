(function(){
	function ssoDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	ssoDetail.prototype={
		constructor:ssoDetail,
		_form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		_dialog:undefined,
		_saveSuccess:false,
		_prjName:undefined,
		open:function(){
			var dlg=this._mainDiv=$('<div/>'),that=this,type=that.cfg.type,
				buttons=[{
					text:"保存",
					iconCls:"fa fa-save",
					handler:function(){
						var name = that._form.find("input[name='name']").val();
						var wsdlURL = that._form.find("input[name='wsdlURL']").val();
						var desc = that._form.find("input[name='describle']").val();
						//判断name长度
						if(name !=undefined && name.length > 20) {
							alert("系统名字不能超过20个字!","danger");
							return;
						}
						//判断URL是否合法
						var pattern = /http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/;
//						console.log(wsdlURL);
						if(!wsdlURL.match(pattern)) {
							alert("URL不合法,请输入正确的地址");
							return;
						}
						if(desc !=undefined && desc.length > 100) {
							alert("描述不能超过100个字!","danger");
							return;
						}

						//获取页签集合，遍历是否与新建名称冲突
						var newTabName = name.trim();
		    			var tabtips = new Array();
		    			$('span.oc-panel-font').each(function(){
		    				tabtips.push($(this).text());
		    			});
		    			
		    			var sign_newTabNameIsAvailable = true;
		    			for (var i = 0; i < tabtips.length; i++) {
							if(newTabName == tabtips[i]){
								sign_newTabNameIsAvailable = false;
								break;
							}
						}
		    			if(sign_newTabNameIsAvailable || (that.cfg.type=='edit' && _prjName==newTabName )){
		    				that._save(that);
		    				if(that._saveSuccess){
		    					if(that.cfg.callback){
		    						that.cfg.callback();//关闭弹出框后刷新域列表
		    					}
		    					dlg.dialog('close');
		    				};
		    			}else{
		    				alert('系统名称重复');
		    			}
					}
				},{
					text:'关闭',
					iconCls:"fa fa-times-circle",
					handler:function(){
						dlg.dialog('close');
					}
				}];
			this._dialog = dlg.dialog({
				href:oc.resource.getUrl('resource/module/system/ssoforthird/ssoDetail.html'),
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
					url : oc.resource.getUrl((that.cfg.type == 'add') ? 'system/SSOForThird/saveSSOForThird.htm': 'system/SSOForThird/updateSSOForThird.htm'),
					data : that._form.val(),
					async:false,
					success : function(data) {
//						console.log(data);
// 						console.log(that._form.val());
						if(data.code == 200){
							that._saveSuccess = true;
                            // console.log();
							oc.util.ajax({
					    		url:oc.resource.getUrl('system/role/getRights.htm'),
					    		data:{id:oc.index.getUser().id},
					    		success:function(result){
					    			$.each(result.data,function(index,item){
					    				if(item.rightName==that._form.val().name){
					    				
					    					oc.index.navigator.moduleChange(item.rightId.toString(),true);
					    				}
					    			})
					    			
					    		}
							
							
							
							
							
							})
						}
					},
					successMsg:"系统"+((that.cfg.type == 'add')?"添加":"更新")+"成功"
				});
			}
		
		},
		_loadForm:function(that) {
			oc.util.ajax({
				url:oc.resource.getUrl('system/SSOForThird/getSSOForThirdById.htm'),
				data:{
					id:that.cfg.id
				},
				async:false,
				successMsg:null,
				success:function(data){
					that._form.val(data.data);
					_prjName=data.data.name;
				}
			});
		}
	};
	
	oc.ns('oc.module.sso.detail');
	
	oc.module.sso.detail={
		open:function(cfg){
			var detail=new ssoDetail(cfg);
			detail.open();
		}
	};
})(jQuery);