(function($){
	function configDeviceDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	configDeviceDetail.prototype={
		constructor:configDeviceDetail,
		form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,deviceDatagrid=cfg.datagrid,
			buttons=[{
				text:'关闭',
				iconCls:'fa fa-times-circle',
				handler:function(){
					dlg.dialog('close');
				}
			}];
			var user = oc.index.getUser();
			var that = this;
//			if(user.systemUser==true){
			if(that.cfg.isHandle){
				buttons.unshift({
					text:'保存',
					iconCls:'fa fa-check-circle',
					handler:function(){
						if(that.form.validate()){
							oc.util.ajax({
								url:oc.resource.getUrl('portal/config/device/update.htm'),
								data:{id:that.form.find("input[name='id']").val(),
									userName:that.form.find("input[name='userName']").val(),
									ipAddress:that.form.find("input[name='ipAddress']").val(),
									password:that.form.find("input[name='password']").val(),
									enableUserName:that.form.find("input[name='enableUserName']").val(),
									enablePassword:that.form.find("input[name='enablePassword']").val(),
									loginType:that.form.find("input[name='loginType']").val(),
									publicName:that.form.find("input[name='publicName']").val(),
									snmpPort:that.form.find("input[name='snmpPort']").val()
								},
								successMsg:null,
								success:function(data){
									dlg.dialog('close');
									if(data.code&&data.code==200){
										deviceDatagrid.reLoad();
										alert("保存成功");
									}else if(data.code==299 || data.code==444){
										alert(data.data);
									}
								}
							});
						}
					}
				});
			}
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/config-file/device/loginConfig.html'),
				title:'连接信息',
				width:400,
				height:410,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg);
					that._loadResource(cfg.id,cfg.ipAddress);
				}
			});
		},
		_defaults:{
			id:undefined,//数据id
			saveCall:undefined//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#configDeviceDetail',
		_init:function(dlg){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			this._initForm();
		},
		_initForm:function(){
			this.form=oc.ui.form({
				selector:this._mainDiv.find('.configDeviceDetailForm'),
				combobox:[{
					selector:'[name=loginType]',
					data:[
					      {id:'0',name:'Telnet',selected:true},
					      {id:'1',name:'SSH'}
			        ],
			        placeholder : false,
			        value:0
				}]
			});
		},
		_loadResource:function(id,ipAddress){
			var that=this;
			oc.util.ajax({
				url:oc.resource.getUrl('portal/config/device/get.htm'),
				data:{id:id},
				successMsg:null,
				success:function(d){
					that.form.val(d.data);
					var snmpPortVal = that.form.find("input[name='snmpPort']").val();
					if(snmpPortVal=="" || snmpPortVal==undefined || null==snmpPortVal){
						that.form.find("input[name='snmpPort']").val('161');
					}
					that.form.find("input[name='ipAddress']").val(ipAddress);
					if(eval(d.data).ipAddress==null)
						that.form.find("#ip").val(ipAddress).attr("disabled","disabled");
					else
						that.form.find("#ip").val(eval(d.data).ipAddress).attr("disabled","disabled");
					if(d.data.loginType!=1) $("#loginType").combobox('setValue',0);
				}
			});
		}
	};
	
	oc.ns('oc.module.config.device.detail');
	oc.module.config.device.detail={
		open:function(cfg){
			var detail=new configDeviceDetail(cfg);
			detail.open();
		}
	};
})(jQuery);
