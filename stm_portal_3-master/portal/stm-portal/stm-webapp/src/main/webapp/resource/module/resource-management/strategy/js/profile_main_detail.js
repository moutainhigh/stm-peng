(function(){
	function userDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	userDetail.prototype={
		constructor:userDetail,
		form:undefined,//操作表单
		cfg:undefined,//外界传入的配置参数 json对象格式
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,type=cfg.type,
				buttons=[{
					text:'关闭',
					handler:function(){
						dlg.dialog('close');
					}
				}];
		
				buttons.unshift({
					text:'保存',
					handler:function(){
						if(that.form.validate()){
							oc.util.ajax({
								url:oc.resource.getUrl('strategy/strategyAll/copyDefaultStrategy.htm'),
								data:that.form.val(),
								success:function(){
									cfg.saveCall&&cfg.saveCall(that.form.val());
									dlg.dialog('close');
								}
							});
						}
					}
				});
			
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/resource-management/strategy/profile_main_detail.html'),
				title:'复制监控策略',
				height:300,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg);
					
					that._loadResource(cfg.id);
				}
			});
		},
		_defaults:{
			type:'add',//界面类型 可取add、update、view 分别表示增删改查
			id:undefined,//数据id
			saveCall:undefined//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#oc_module_copy_strategy_detail',
		_init:function(dlg){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			this._initForm();
		},
		_initForm:function(){
			var domains = oc.index.getDomains();
			var firstDomainId = typeof(domains[0].id) == "undefined" ? '' : domains[0].id;
		this.form=oc.ui.form({
				selector:this._mainDiv.find('.oc-form'),
				combobox:[{
					selector:'[name=domainId]',
					data : domains,
					placeholder:null,
					value : firstDomainId
				},{
					selector:'[name=prentCapacityName]',
					disabled:true
				},{
					selector:'[name=capacityName]',
					disabled:true
				}]
			});
		},
		_loadResource:function(id){
			var that=this;
			oc.util.ajax({
				url:oc.resource.getUrl('strategy/strategyAll/getDefaultStrategy.htm'),
				data:{id:id},
				successMsg:null,
				success:function(d){
					that.form.val(d.data);
				}
			});
		}
	};
	
	oc.ns('oc.module.profile.detail');
	
	oc.module.profile.detail={
		open:function(cfg){
			var detail=new userDetail(cfg);
			detail.open();
		}
	};
})(jQuery);