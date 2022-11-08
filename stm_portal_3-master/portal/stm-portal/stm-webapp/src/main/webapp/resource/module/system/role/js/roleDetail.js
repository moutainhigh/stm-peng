(function(){
	function roleDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	roleDetail.prototype={
		constructor:roleDetail,
		_form:undefined,
		_cancelWindowFlag:0,
		_rightsDiv:undefined,
		_loadAccordions:[],
		_updateStatus:false,
		_cache:{
			'基本信息':{data:"", initData:function(target){
					this.data = JSON.stringify(target._form.val());
				},getData:function(target){
					return JSON.stringify(target._form.val());
				}
			},
			'权限':{data:"", initData:function(target){
					var ids=[];
					target._rightsDiv&&target._rightsDiv.find(":input[name=id]:checked").each(function(){
						ids.push(this.value);
					});
					this.data = ids.join();
				},getData:function(target){
					var ids=[];
					target._rightsDiv&&target._rightsDiv.find(":input[name=id]:checked").each(function(){
						ids.push(this.value);
					});
					return ids.join();
				}
			}
		},
		cfg:undefined,
		_cancelBtn:false,
		open:function(){
			var dlg=$('<div/>'),that=this;
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/system/role/roleDetail.html'),
				title:((that.cfg.type=='edit')?'编辑':'添加')+'角色',
				height:400,
				width:900,
				buttons:!that.cfg.readonly&&[{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						var titles  = that._getEditPanels();
						if(titles.length>0){
							for(var i=0;i<titles.length;i++){
								if(that._saveMethods[titles[i]](that)){
									that._cache[titles[i]].data = that._cache[titles[i]].getData(that);
									!that._updateStatus&&(that._updateStatus = true);
								}else{
									that._cancelWindowFlag = 1;
									return ;
								}
							}
						}else{
							//alert("您未进行修改操作！！");
						}
						dlg.window("close");
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
					that.cfg.type=='edit'&&that._loadMethods['基本信息'](that);
				},
				onBeforeClose:function(){
					if(that._cancelBtn||that._cancelWindowFlag||that.cfg.id==1||that.cfg.id==2){
						return true;
					}
					var titles  = that._getEditPanels();
					if(titles.length>0){
						oc.ui.confirm('是否保存当前操作？',function(){
							for(var i=0;i<titles.length;i++){
								if(that._saveMethods[titles[i]](that)){
									that._cache[titles[i]].data = that._cache[titles[i]].getData(that);
									!that._updateStatus&&(that._updateStatus = true);
								}
							}
						});
					}
					return true;
				},
				onClose:function(){
					that._updateStatus&&that.cfg.datagrid.reLoad();
				}
			});
		},
		_defaults:{
			type:'add',
			id:undefined
		},
		_flag:'add',
		_mainDiv:undefined,
		_id:'#oc_module_system_role_detail',
		_init:function(dlg){
			var that = this;
			that._flag = that.cfg.type;
			that._mainDiv=dlg.find(that._id).attr('id',oc.util.generateId());
			if(that.cfg.type=='edit'){
				for(var i in that._initMethods){
					that._initMethods[i](that);
				}
			}else{
				that._initMethods['基本信息'](that);
			}
			
			that._mainDiv.accordion({
				onSelect:function(title,index){
					
					that._initMethods[title](that);
					that._saveMethods['权限'](that);
					if(that.cfg.type=='edit'){
						that._loadAccordions.indexOf(title)&&that._loadMethods[title](that);
					}
					
				},
				onBeforeSelect:function(title, index){
					this.selectedIdx=index;
					if(that.cfg.type=='add'&&index!=0){
						return that._saveMethods['基本信息'](that);
						
						
					}
					return true;
				}
			});
			
			that.cfg.type=='add'&&(function(target){
							for(var i in target._cache){
								target._cache[i].initData(target);
							}
						})(that);
		},
		_getEditPanels:function(){
			var titles = [];
			var panels = this._mainDiv.accordion("panels");
			for(var i=0;i<panels.length;i++){
				var title = panels[i].panel("options").title;
				(this._cache[title].data != this._cache[title].getData(this))&&titles.push(title);
			}
			return titles;
		},
		_initMethods:{
			'基本信息':function(target){
				if(!target._form){
					target._form=oc.ui.form({
						selector:target._mainDiv.find("form.role_form")
					});
					target.cfg.readonly&&target._form.disable();
				}
			},
			'权限':function(target){
				if(!target._rightsDiv||target._flag=='add'){
					target._rightsDiv = target._mainDiv.find(".rights").first();
					oc.util.ajax({
			    		url:oc.resource.getUrl('system/role/getRights.htm'),
			    		data:{id:target.cfg.id},
			    		failureMsg:'加载数据失败！',
			    		success:function(result){
			    			var data = result.data||[],
			    			html = "";
							if(data.length>0){
								html += "<div class='oc-hsys-role'>";
								for(var i=0,len=data.length; i<len;i++){
									html += "<label><input class='oc-checkbox-locate' type='checkbox' "+(data[i].roleId ? "checked='checked'" : '')+" name='id' value='"+data[i].rightId+"' />"+data[i].rightName+"</label>";
								}
								//如果是管理者或者域管理员,多显示一个‘系统管理’
								if(target.cfg.readonly){
									html += "<label><input class='oc-checkbox-locate' type='checkbox'  name='id' value='2' />系统管理</label>";
								}
								html += "</div>";
							}
							var right = target._rightsDiv.html(html);
							
							right.find("label")
							.css("width", '33.3%');
							
							target.cfg.readonly&&right
							.find("[type=checkbox]")
							.attr("checked", true)
							.attr("disabled", true);
			    		}
			    	});
				}
			}
		},
		_loadMethods:{
			'基本信息':function(target){
				oc.util.ajax({
					url:oc.resource.getUrl('system/role/get.htm'),
					data:{id:target.cfg.id},
					successMsg:null,
					success:function(data){
						target._form.val(data.data);
						target._cache['基本信息'].initData(target);
						(function(){
							for(var i in target._cache){
								target._cache[i].initData(target);
							}
						})();
					}
				});
				target._loadAccordions.push('基本信息');
			},
			'权限':function(target){
				target._loadAccordions.push('权限');
			},
		},
		_saveMethods:{
			'基本信息':function(target){
				var flag = false;
				if(target._form.validate()&&target._checkRoleName()){
					oc.util.ajax({
						url:oc.resource.getUrl((target.cfg.type=='add')?'system/role/insert.htm':'system/role/update.htm'),
						data:target._form.val(),
						async:false,
						successMsg:'保存角色基本信息成功',
						success:function(data){
							flag = data.code==200;
							if(flag&&target.cfg.type=='add'){
								target.cfg.type = "edit";
								target._form.val(data.data);
								target.cfg.id = data.data.id;
								target._updateStatus = true;
							}
							try{
								oc.index.reloadLoginUser();
							}catch(e){
								log(e);
							}
						}
					});
				}
				
				flag&&target._cache['基本信息'].initData(target);
				return flag;
			},
			'权限':function(target){
				var flag = false,
				data=[];
				target._rightsDiv.find(":input[name=id]:checked").each(function(i){
					data.push("roleRightRels["+i+"].rightId="+this.value);
					data.push("roleRightRels["+i+"].roleId="+target.cfg.id);
				});
				var DataDeal = {
				          formToJson: function (data) {
				              data=data.replace(/&/g,"\",\"");
				              data=data.replace(/=/g,"\":\"");
				              data="{\""+data+"\"}";
				              return data;
				           },
				};
				var json=DataDeal.formToJson(data.join("&"));
			
				oc.util.ajax({
					url:oc.resource.getUrl('system/role/rightUpdate.htm'),
					data:jQuery.parseJSON(json),
					async:false,
					success:function(data){
						flag = data.code == 200;
						try{
							oc.index.reloadLoginUser();
						}catch(e){
							log(e);
						}
					}
				});
				flag&&target._cache['权限'].initData(target);
				return flag;
			}
		},
		_checkRoleName:function(){
			var that = this;
			var flag = false;
			var name = that._form.jq.find(":input[name=name]");
			var data = {};
			data.name = name.val();
			that.cfg.id&&(data.id=that.cfg.id);
			oc.util.ajax({
				url:oc.resource.getUrl('system/role/checkName.htm'),
				data:data,
				successMsg:null,
				async:false,
				success:function(data){
					flag = data.data==0;
					!flag&&(function(){
						name.focus();
						alert('角色名已存在，请更换');
					})();
				}
			});
			return flag;
		}
	};
	
	oc.ns('oc.module.system.role');
	
	oc.module.system.role={
		open:function(cfg){
			new roleDetail(cfg).open();
		}
	};
})(jQuery);