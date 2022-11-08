(function(){
	
	var childProfileNum = null;
	
	function customDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	customDetail.prototype={
		constructor:customDetail,
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
					text:'下一步',
					handler:function(){
						if(that.form.validate()){
							
							var childProfileIds = "";							
							for(var i = 0 ; i < childProfileNum ; i++){								
								if($('#dataId' + i).prop("checked")){
									childProfileIds += $('#dataId' + i).attr("value") + ",";
								}	
							}
							childProfileIds = childProfileIds.substr(0,childProfileIds.length - 1);
							var myForm = that.form.val();
							myForm.childProfileIds = childProfileIds;
							oc.util.ajax({
								url:oc.resource.getUrl('strategy/strategyAll/insertCustomStrategy.htm'),
								data:myForm,
								success:function(data){
									if(data.data == 0) {
										alert("该策略名已存在!");
										return;
									}
									cfg.saveCall&&cfg.saveCall(that.form.val());
									dlg.dialog('close');
					        		 oc.resource.loadScript('resource/module/resource-management/js/custom_strategy_detail.js',function(){
					  					oc.customprofile.strategy.detail.show(data.data);
					         		 });
								}
							});
						}
					}
				});
			
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/resource-management/strategy/customStrategyDetail.html'),
				title:'新建监控策略',
				height:360,
				width:820,
				resizable:true,
				buttons:buttons,
				cache:false,
				onLoad:function(){
					that._init(dlg);
					
				}
			});
		},
		_defaults:{
			type:'add',//界面类型 可取add、update、view 分别表示增删改查
			id:undefined,//数据id
			saveCall:undefined//点击保存后用户回调 参数为表单数据
		},
		_mainDiv:undefined,
		_id:'#oc_module_custom_strategy_detail',
		_init:function(dlg){
			this.id=oc.util.generateId();
			this._mainDiv=dlg.find(this._id).attr('id',this.id);
			this._initForm();
		},
		_initForm:function(){
			var that = this;
			var domains = oc.index.getDomains();
			var firstDomainId = typeof(domains[0].id) == "undefined" ? '' : domains[0].id;
			
			var parentCategoryComboboxCfg = {
					selector:'[name=prentCapacityName]',
					filter:function(data){
						return data.data;
					},
					url:oc.resource.getUrl('strategy/strategyAll/getPrentStrategyType.htm'),
					onChange : function(id){
						if(!that.form.ocui[0].jq.combobox('getValue')){
							that.form.ocui[1].jq.combobox('setValue','');
							that.form.ocui[1].load({data:[]});
							that.form.ocui[2].jq.combobox('setValue','');
							that.form.ocui[2].load({data:[]});
						}
						that.form.ocui[1].reLoad(oc.resource.getUrl('strategy/strategyAll/getChildCategoryDefById.htm?id='+ id));
						$("#childResourceId").empty();
						that.form.ocui[2].jq.combobox('setValue','');
						that.form.ocui[2].load({data:[]});
					}
			};
			if(that.cfg.isVm){
				//若为创建虚拟化策略
				parentCategoryComboboxCfg = {
						selector:'[name=prentCapacityName]',
						url:oc.resource.getUrl('portal/vm/vmProfileAction/getVmResourceCategory.htm'),
						filter:function(data){
							return data.data;
						},
//						disabled:true, // BUG #10427 （IE8）虚拟化--用户自定义策略--新建--“虚拟化资源”字体虚化
						selected:0,
						placeholder:false,
						onChange : function(id){
							that.form.ocui[1].reLoad(oc.resource.getUrl('strategy/strategyAll/getChildCategoryDefById.htm?id='+ id));
							$("#childResourceId").empty();
						}
				};
			}
			
			this.form=oc.ui.form({
				selector:this._mainDiv.find('.oc-form'),
				combobox:[
			        parentCategoryComboboxCfg,
				{
					selector:'[name=capacityName]',
					filter:function(data){
						return data.data;
					},
					
					onChange : function(id){
						$("#childResourceId").empty();
						if(!that.form.ocui[1].jq.combobox('getValue')){
							that.form.ocui[2].jq.combobox('setValue','');
							that.form.ocui[2].load({data:[]});
						}else{
							that.form.ocui[2].reLoad(oc.resource.getUrl('strategy/strategyAll/getResourceDefAll.htm?id='+ id));
						}
					}
				},
				{
					selector:'[name=resourceName]',
					filter:function(data){
						return data.data;
					},
					
					onChange : function(id){
						$("#childResourceId").empty();
						if(!that.form.ocui[2].jq.combobox('getValue')){
							return;
						}
						oc.util.ajax({
							url:oc.resource.getUrl('strategy/strategyAll/getChildResourceAll.htm?id='+ id),			            
							successMsg:null,
							async:false,
							success:function(d){
								var checkboxstr = '';
								if(d != null || d != undefined|| d != ""){
									childProfileNum = d.data.length;
									
									for(var i = 0; i < d.data.length; i++){
									   var obj = d.data[i];
									   var id = obj['id'];
									   var name = obj['name'];
									   var checkbox = '<input id="dataId' + i + '" type="checkbox" value='+id+' >' + name+'&nbsp;&nbsp;' ;
									  
									   checkboxstr += checkbox;
									}
								}
							  $("#childResourceId").append(checkboxstr);
							}
							});
					}
				},{
					selector:'[name=domainId]',
					data : domains,
					placeholder:null,
					value : firstDomainId
				}
				]
			});
		}
	};
	
	oc.ns('oc.module.custom.detail');
	
	oc.module.custom.detail={
		open:function(cfg){
			var detail=new customDetail(cfg);
			detail.open();
		}
	};
})(jQuery);