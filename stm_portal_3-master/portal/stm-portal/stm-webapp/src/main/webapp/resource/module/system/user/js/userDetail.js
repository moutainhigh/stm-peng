(function($){
	function userDetail(cfg){
		this.cfg=$.extend({},this._defaults,cfg);
	}
	
	userDetail.prototype={
		constructor:userDetail,
		_cancelWindowFlag:0,
		_mainDiv:undefined,
		_id:'#oc_module_system_user_detail',
		_accordionDiv:undefined,
		_otherDiv:undefined,
		_form:undefined,
		_resourceForm:0,
		_treegrid:undefined,
		_resourceTreegrid:0,
		_datagrid:undefined,
		_roleDomainDiv:undefined,
		_resourcePurviewDiv:undefined,
		_domainDiv:undefined,
		_userType:undefined,
		_loadAccordions:[],
		_treeLeafDatas:[],
		_updateStatus:false,
		_cache:{
			'基本信息':{data:"", initData:function(target){
					this.data = JSON.stringify(target._form.val());
				},getData:function(target){
					return JSON.stringify(target._form.val());
				}
			},
			'角色':{data:"", initData:function(target){
					if(!target._treegrid){
						this.data = "";
						return ;
					}
					selected = target._treegrid.getSelected(),
					result = "";
					if(selected){
						selected.each(function(i){
							var that = $(this);
							result += that.data("parent") + this.value;
						});
					}
					this.data = result;
				},getData:function(target){
					
					if(!target._treegrid){
						return "";
					}
					selected = target._treegrid.getSelected(),
					result = "";
					if(selected){
						selected.each(function(i){
							var that = $(this);
							result += that.data("parent") + this.value;
						});
					}
					return result;
				}
			},
			'资源权限':{data:"", initData:function(target){
					var contentDiv = target._accordionDiv.data("purview_content");
					if(!contentDiv){
						this.data = "";
						return ;
					}
					var ids = [];
					contentDiv.find(".resource_purview_content_r :input[type=checkbox]").each(function(){
						ids.push(this.value);
					});
					ids.sort();
					this.data = ids.join();
				},getData:function(target){
					var contentDiv = target._accordionDiv.data("purview_content");
					if(!contentDiv){
						return "";
					}
					var ids = [];
					contentDiv.find(".resource_purview_content_r :input[type=checkbox]").each(function(){
						ids.push(this.value);
					});
					ids.sort();
					return ids.join();
				}
			}
		},
		cfg:undefined,
		_cancelBtn:false,
		_saveWithCloseFlag:0,
		open:function(){
			var dlg=$('<div/>'),that=this,cfg=this.cfg,type=cfg.type;
			this._userType = cfg.userType ? cfg.userType : '1';
			dlg.dialog({
				href:oc.resource.getUrl('resource/module/system/user/userDetail.html'),
				title:((type=='edit')?'编辑':'添加')+'用户',
				height:500,
				width:920,
				resizable:true,
				buttons:[{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						var titles  = that._getEditPanels();
						if(titles.length>0){
							for(var i=0;i< titles.length;i++){
								if(that._saveMethods[titles[i]](that)){
									that._cache[titles[i]].data = that._cache[titles[i]].getData(that);
									!that._updateStatus&&(that._updateStatus = true);
								}else{
									that._cancelWindowFlag = 1;
									return ;
								}
							}
						}else{
//							alert("您未进行修改操作！！");
						}
						dlg.window("close");
					}
				},{
					text:'取消',
					iconCls: 'fa fa-times-circle',
					handler:function(){
						that._cancelBtn = true;
						dlg.window("close");
					}
				}],
				cache:false,
				onLoad:function(){
					that._init(dlg);
					(type=='edit')&&that._loadMethods['基本信息'](that);
				},
				onBeforeClose:function(){
					if(that._cancelBtn||that._cancelWindowFlag||that._saveWithCloseFlag){
						return true;
					}
					var titles  = that._getEditPanels();
					if(titles.length>0){
						oc.ui.confirm('是否保存当前操作？',function(){
							for(var i=0;i< titles.length;i++){
								if(that._saveMethods[titles[i]](that)){
									that._cache[titles[i]].data = that._cache[titles[i]].getData(that);
									!that._updateStatus&&(that._updateStatus = true);
								}else{
									that._saveWithCloseFlag = 0;
									return ;
								}
							}
							that._saveWithCloseFlag = 1;
							dlg.window("close");
						},function(){
							that._saveWithCloseFlag = 1;
							dlg.window("close");
						});
						return false;
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
			id:undefined,
			userType:1
		},
		_flag:'add',
		_resourceTypeCacheKey:'resource_type_cache_key',
		_initResourceType:function(){
			var that = this;
			!oc.util.dictCache[that._resourceTypeCacheKey]&&
			oc.util.ajax({
	    		url:oc.resource.getUrl('system/user/getResourceType.htm'),
	    		failureMsg:'加载资源类型数据失败！',
	    		async:false,
	    		success:function(result){
	    			result.code==200&&(oc.util.dictCache[that._resourceTypeCacheKey] = result.data);
	    		}
	    	});
		},
		_getResourceType:function(){
			var resourceType = oc.util.dictCache[this._resourceTypeCacheKey];
			!resourceType&&this._initResourceType();
			var results = [];
			$.each( resourceType, function(){
				results.push($.extend(true,{},this));//返回对象的深拷贝
			});
			return results;
		},
		_init:function(dlg){
			var that = this;
//			//扩展validatebox
//			$.extend($.fn.validatebox.defaults.rules, {    
//				mobile4admin:{
//					validator:function(v){
//						var mb =/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/;
//						return mb.test(v);
//					},
//					message:'请输入正确的mobile'
//				}  
//			}); 
			that._flag = that.cfg.type;
			that._mainDiv=dlg.find(that._id).attr('id',oc.util.generateId());
			that._accordionDiv = that._mainDiv.find(".oc_module_system_user_accordion");
			that._otherDiv = that._mainDiv.find(".oc_module_system_user_other");
			
			that._roleDomainDiv = that._otherDiv.find(".accordions_role_domain");
			that._resourcePurviewDiv = that._otherDiv.find(".accordions_resource_purview");
			that._domainDiv = that._otherDiv.find(".accordions_domain");
			that._initResourceType();
			that._initAccordion();
			that._loadAccordions = [];
			if(that._userType==4){
				$(".mobile").hide();
			}else{
				$(".phone").hide();
			}
			that._accordionDiv.accordion({
				onSelect:function(title,index){
					that._initMethods[title](that);
					if(title=='角色'){
						that._loadAccordions.indexOf(title)==-1&&that._loadMethods[title](that);
						return ;
					}
					if(title=='资源权限'){
						that._loadMethods[title](that);
						return ;
					}
					if(that.cfg.type=='edit'){
						that._loadAccordions.indexOf(title)==-1&&that._loadMethods[title](that);
					}
				},
				onBeforeSelect:function(title, index){
					
					if(index!=0){
						var valid = that._form.validate()&&that._checkUserName();
						if(!valid){
							return false;
						}else{
							if(that._cache['基本信息'].data != that._cache['基本信息'].getData(that)){
								that._saveMethods['基本信息'](that);
							}
						}
					}
					if(that.cfg.type=='add'&&index!=0){
						if(title!='资源权限'&&that._saveMethods['基本信息'](that)){
							this.selectedIdx=index;
							return true;
						}else{
							return false;
						}
					}
					
					if(title=='资源权限'&&that._cache['角色'].data != that._cache['角色'].getData(that)){
						if(that._saveMethods['角色'](that)){
							this.selectedIdx=index;
							return true;
						}else{
							return false;
						}
					}
					this.selectedIdx=index;
					return true;
				}
			});
			that.cfg.type=='add'&&that._initCacheData(that._accordionDiv.accordion("panels"));
		},
		_initAccordion:function(){
			var that = this;
			var panels = that._accordionDiv.accordion("panels");
			for(var i=panels.length-1;i>0;i--){
				that._accordionDiv.accordion("remove",panels[i].panel('options').title);
			}
			var userType = that._userType;
			if(userType==1){
				that._accordionDiv.accordion('add',{
					title: '角色',
					content: that._roleDomainDiv.html(),
					selected: false
				});
				that._accordionDiv.accordion('add',{
					title: '资源权限',
					content: that._resourcePurviewDiv.html(),
					selected: false
				});
			}
			if(that.cfg.type=='add'){
				that._initMethods['基本信息'](that);
			}else{
				for(var i=0; i< panels.length;i++){
					that._initMethods[panels[i].panel('options').title](that);
				}
			}
			
		},
		_initCacheData:function(panels){
			for(var i=0; i< panels.length;i++){
				var title = panels[i].panel("options").title;
				this._cache[title].initData(this);
			}
		},
		_getEditPanels:function(){
			var titles = [];
			var panels = this._accordionDiv.accordion("panels");
			for(var i=0; i< panels.length;i++){
				var title = panels[i].panel("options").title;
				(this._cache[title].data != this._cache[title].getData(this))&&titles.push(title);
			}
			return titles;
		},
		_initMethods:{
			'基本信息':function(that){
				if(!that._form){
					that._form=oc.ui.form({
						selector:that._accordionDiv.find("form.user_form").first()
					});
					var loginUserType = that.cfg.loginUserType,
					userType = that._form.jq.find("[name=userType]");
					if(loginUserType==4){
						userType.filter("[value=3]").parent().parent().parent().show();
					}
					userType.change(function(){
						that._userType = that._form.jq.find("[name=userType]:checked").val();
						that._initAccordion();
					});
				}
			},
			'角色':function(target){
				if(!target._treegrid){
					target._treegrid = target._accordionDiv.find("div.accordions_role_domain_treegrid").first();
					target._treegrid = system.ui.accodtion({
						selector:target._treegrid,
						url:oc.resource.getUrl('system/user/getDomainRole.htm'),
						contentEllipsis:true,
						columns:[
						    {field:'name', title:'域', width:120},
						    {field:'角色', title:'角色', label:true,format:function(index, row){
						    	return "";
						    }}
						]
					});
				}
			},
			'资源权限':function(that){
			}
		},
		_loadMethods:{
			'基本信息':function(target){
				oc.util.ajax({
					url:oc.resource.getUrl('system/user/get.htm'),
					data:{id:target.cfg.id},
					async:false,	//同步保证缓存数据的正确
					success:function(data){
						if(data.code==200){
							var d = data.data;
							target._form.jq.find(":input[name=account]").attr('readonly','readonly');
							target._form.jq.find(":input[name=confirmPassword]").data("password", d.password).val(d.password);
							target._form.val(d);
							if(target.cfg.id==target.cfg.loginUserId){
								
								target._form.jq.find(":input[name=status]").attr("disabled", true);
								var userType = target._form.jq.find(":input[name=userType]");
								userType.parent().parent().parent().hide();
								userType.filter(":checked").hide().parent().parent().parent().show();
							}
							if(d.userType==4){
								target._form.jq.find(".mobile").hide();
								$(":input[name=phone]").val(d.mobile);
							}else{
								target._form.jq.find(".phone").hide();
								target._form.jq.find(".not_admin_show").show().next().next().show();
							}
							target._initCacheData(target._accordionDiv.accordion("panels"));
						}
					}
				});
				target._loadAccordions.push('基本信息');
			},
			'角色':function(target){
				oc.util.ajax({
					url:oc.resource.getUrl('system/user/getDomainRoleRel.htm'),
					data:{id:target.cfg.id},
					async:false,	//同步保证缓存数据的正确
					success:function(data){
						if(data.code==200){
							var d = data.data||[];
							for(var i=0; i<d.length;i++){
								target._treegrid.select(d[i].domainId, d[i].roleId);
							}
							target._initCacheData(target._accordionDiv.accordion("panels"));
						}
					}
				});
				target._loadAccordions.push('角色');
			},
			'资源权限':function(that){
				
				var contentDiv = that._accordionDiv.find("div.accordions_resource_purview_content").first(),
				left = contentDiv.find(".resource_purview_content_l"),
				right = contentDiv.find(".resource_purview_content_r"),
				selected = that._treegrid.getSelected(),
				domainSysIds = [];
				domains = [],
				domainIds = [];
				roleIds = [];
				that._accordionDiv.data("purview_content", contentDiv);
				
				if(selected.size()==0){	//如果未选中从后台获取
					oc.util.ajax({
			    		url:oc.resource.getUrl('system/user/getCommonUserDomains.htm'),
			    		data:{id:that.cfg.id},
			    		async:false,
			    		failureMsg:'加载数据失败！',
			    		success:function(data){
			    			domains = data||[];
			    			for(var i=0;i<domains.length;i++){
			    				var domainId = domains[i].id;
			    				if(domains[i].adminUser=='2'){
			    					domainSysIds.push(domainId);
			    				}
			    				domainIds.push(domainId);
			    			}
			    		}
			    	});
				}else{
					selected.each(function(){
						var $this = $(this),
						role = $(this).data("row"),
						domain = $this.data("parentRow"),
						domainId = domain.id;
						if(domainIds.indexOf(domainId)==-1){
							domains.push(domain);
							domainIds.push(domainId);
						}
					//	roleIds.push(role.id);
					
						if(role.id==1||role.id==2){
							domainSysIds.indexOf(domainId)==-1&&domainSysIds.push(domainId);
						}
					});
				}
				if(that._resourceForm){
					that._resourceForm.ocui[0].load(domains);
					domains[0]&&that._resourceForm.ocui[0].jq.combobox("setValue", domains[0].id);
				}else{
					that._resourceForm=oc.ui.form({
						selector:that._accordionDiv.find("form.resource_form").first(),
						combobox:[{
							selector:'[name=domain]',
							placeholder:null,
							data:domains,
							onSelect:function(data){
								var domainId = data.id;
								log(domainId);
								if(domainSysIds.indexOf(domainId)!=-1){
								
									loadLeftBlank.call(that, left);
								}else{
									if(domainId){
										var val = contentDiv.find(":input[name=type]:checked").val();
										var datas =  contentDiv.data(val==1 ? "resource" : "resourceGroup")||{};
										loadLeft.call(that, val,datas[data.id], left);
									}
								}
							}
						}]
					});
				}
				if(domainIds.length==0){
					return ;
				}
				oc.util.ajax({
		    		url:oc.resource.getUrl('system/user/getResources.htm'),
		    		data:{domainIds:domainIds.join(),userId:that.cfg.id},
//		    		startProgress:false,
//		    		stopProgress:false,
		    		failureMsg:'加载资源权限数据失败！',
		    		success:function(data){
		    			var d = data.data;
		    			filter(d)
		    			contentDiv.data("resourceGroup", d.resourceGroup)
		    			.data("resource", d.resource);
		    			loadRight(d.userResources);
		    			var domainId = that._resourceForm.val().domain,
		    			checkedbox = contentDiv.find(":input[name=type]:checked"),
		    			data =  contentDiv.data(checkedbox.val()==1 ? "resource" : "resourceGroup")||{};
		    			if(domainSysIds.indexOf(domainId)!=-1){
		    				var id = that._form.jq.find(":input[name=id]");
		    		    	oc.util.ajax({
		    		    		url:oc.resource.getUrl('system/user/getDomainRoles.htm'),
		    		    		data:{id:id[0].value},
		    		    		failureMsg:'加载用户域数据失败！',
		    		    		async:false,
		    		    		success:function(data){
		    		    			var d = data.data;
		    		    			for(var i=0; i<d.length; i++){
		    		    				if(d.length==1 && d[i].roleName=="管理者"){
		    		    					loadLeftBlank2.call(that, left);
		    		    				}else{
		    		    					loadLeftBlank.call(that, left);
		    		    				}
		    		    			}
		    		    		}
		    		    	});
							
						}else{
							loadLeft.call(that, contentDiv.find(":input[name=type]:checked").val()
			    					,data[domainId], left);
						}
		    			that._initCacheData(that._accordionDiv.accordion("panels"));
		    		}
		    	});
				function filter(data){
					var resourceGroup = data.resourceGroup,
	    			domainResource = data.resource,
	    			userResources = data.userResources;
					for(var i=0; i<userResources.length; i++){
						if(userResources[i].type==1){
							var domainId = userResources[i].domainId,
							resource = domainResource[domainId];
							for(var k=0;k<resource.length;k++){
								if(resource[k].id==userResources[i].resourceId){
									userResources[i].domainId = domainId;
									userResources[i].name = resource[k].resourceName;
									resource.splice(k,1);
									break;
								}
							}
						}else{
							var domainId = userResources[i].domainId,
							resource = resourceGroup[domainId]||[];
							for(var k=0;k<resource.length;k++){
								if(resource[k].id==userResources[i].resourceId){
									userResources[i].name = resource[k].name;
									resource.splice(k,1);
									break;
								}
							}
						}
					}
				}
				
				function loadRight(userResources){
					right.html("")
					for(var i=0; i<userResources.length; i++){
						$("<label><input class='oc-checkbox-locate' name='resourceId' type='checkbox' data-type='"+userResources[i].type+"' value='"+userResources[i].resourceId+"' />" + userResources[i].name+"</label>")
						.data("domainId", userResources[i].domainId)
						.appendTo($("<div class='oc-pickspace'/>").appendTo(right));
//						rightResourceIds.push(userResources[i].resourceId);
					}
				}
				
				contentDiv.find(":input[name=type]").click(function(){
					if(this.checked){
						var data =  contentDiv.data(this.value==1 ? "resource" : "resourceGroup")||{},
						domainId = that._resourceForm.val().domain;
						if(domainSysIds.indexOf(domainId)!=-1){
							loadLeftBlank.call(that, left);
							return 
						}
						loadLeft.call(that, this.value,data[domainId], left);
					}
				});
				function getParent(obj, nodeName){
					var parent = obj.parent();
					if(parent[0].nodeName==nodeName.toUpperCase()){
						return parent;
					}else{
						return arguments.callee(parent, nodeName);
					}
				}
				contentDiv.find(".resource_purview_content_c .to_left").click(function(){
					var checkedbox = left.find(":input[name=resourceId]:checked"),
						type = contentDiv.find(":input[name=type]:checked").val(),
						domainId = that._resourceForm.val().domain,
						domainResourceObj = contentDiv.data(type==1 ? "resource" : "resourceGroup")||{},
						resource = domainResourceObj[domainId]||[];
					checkedbox.each(function(){
						var $this = $(this),val=this.value,
						type = $this.data("type"),
						text = $this.attr("text");
						$("<label/>").append($this.parent().html()+(text||"")).data("domainId", domainId).appendTo($("<div class='oc-pickspace'/>").appendTo(right));
						for(var i=0;i<resource.length;i++){
							if(resource[i].id==val){
								resource.splice(i,1);
								break;
							}
						}
					});
					if(type==2){
						checkedbox.parent().parent().remove();
					}else{
						var removeNodes = getParent(checkedbox, 'tr'),
						sNodes = getParent(removeNodes, 'tr'),
						fNodes = getParent(sNodes, 'tr');
						removeNodes.remove();
						sNodes.each(function(){
							var $this = $(this);
							if($this.find(":input[name=resourceId]").size()==0){
								$this.prev().remove();
								$this.remove();
							}
						});
						
						fNodes.each(function(){
							var $this = $(this);
							if($this.find(":input[name=sResourceType]").size()==0){
								$this.prev().remove();
								$this.remove();
							}
						});
					}
				}).parent().find(".to_right").click(function(){
					var checkedbox = right.find(":input[type=checkbox]:checked");
					checkedbox.each(function(){
						var $this = $(this),val=this.value,
						type = $this.data("type"),
						domainId = $this.parent().data("domainId"),
						resource = {},
						domainResourceObj = contentDiv.data(type==1 ? "resource" : "resourceGroup")||{};;
						resource.id = val;
						if(type==1){
							resource.resourceName = $this.parent().text();
							resource.type =  $this.data("resourcetype");
							resource.name = "";
						}else{
							resource.name = $this.parent().text();
						}
						
						domainResourceObj[domainId].push(resource);
					});
					checkedbox.parent().parent().remove();
					contentDiv.find(":input[name=type]:checked").trigger('click');
				});
				function loadLeftBlank(div){
					div.html("<div class='user_all_resource_pic'></div>");
				}
				function loadLeftBlank2(div){
					div.html("<div class='user_all_resource_pic'></div>");
				}
				function loadLeft(type, data, div){	//渲染左边div
					var that = this,
					data=data||[],
					rightResourceIds = [];
					right.find(":input[type=checkbox]").each(function(){
						rightResourceIds.push(this.value);
					});
					if(type==2){
						var html = "";
						for(var i=0; i<data.length; i++){
							if(rightResourceIds.indexOf(data[i].id)!=-1){
								return ;
							}
							html += "<div class='oc-pickspace'><label><input class='oc-checkbox-locate' name='resourceId' type='checkbox' data-type='2' value='"+data[i].id+"' />"+data[i].name+"</label></div>"
						}
						div.html(html);
					}else{
						div.html("");
						if(data.length==0){
							return ;
						}
						
						var resourceTreegrid = $('<div style="width:100%;height:100%;"></div>').appendTo(div).treegrid({
							fit:true,
						    idField:'id',
						    treeField:'name',
						    fitColumns:true,
						    singleSelect:false,
						    animate:true,
						    pagination:false,
						    loader:function(param,success){
						    	var resources = data,
			    					resourcesTypes = that._getResourceType(),
			    					allResourcesTypes = {};
				    			(function(data, result, pid){
				    				for(var i=0 ; i<data.length; i++){
				    					var id = data[i].id,
				    					child = data[i].childCategorys;
				    					
				    					result[id] = data[i];
				    					data[i]._parentId = pid;
				    					data[i].iconCls = "oc-bgnong";
				    					data[i].children = child ? child : [];
				    					child&&arguments.callee(child, result, id);
				    				}
				    			})(resourcesTypes, allResourcesTypes, null);
				    			
				    			for(var i=0, len=resources.length; i<len; i++){
				    				if(rightResourceIds.indexOf(resources[i].id)!=-1){
										return ;
									}
				    				var id = resources[i].type,
				    				resourceType= allResourcesTypes[id];
				    				if(!id){
				    					continue;
				    				}
				    				if(!resourceType){
				    					continue;
				    				}
				    				!resourceType.children&&(resourceType.children = []);
				    				resources[i]._parentId = id;
				    				resources[i].iconCls = "oc-bgnong";
				    				
				    				allResourcesTypes[id].children.push(resources[i]);
				    			}
				    			for(var i=0,len=resourcesTypes.length; i<len; i++){	//去除第三级无资源的资源类型
				    				var children = resourcesTypes[i].children;
				    				if(children&&children.length>0){
				    					for(var j=0; j<children.length;j++){
				    						var children2 = children[j].children;
				    						if(children2 && children2.length > 0){
			    								for(var x=0; x<children2.length;x++){
			    									var children3 = children2[x].children;
			    									if((!children3||children3.length==0) && !children2[x].type){
			    										children2.splice(x,1);
						    							x -= 1;
						    						}
			    								}
				    						}
				    						
				    					}
				    				}
				    			}
				    			for(var i=0,len=resourcesTypes.length; i<len; i++){	//去除第二级无资源的资源类型
				    				var children = resourcesTypes[i].children;
				    				if(children&&children.length>0){
				    					for(var j=0; j<children.length;j++){
				    						var children2 = children[j].children;
				    						if(!children2||children2.length==0){
				    							children.splice(j,1);
				    							j -= 1;
				    						}
				    						
				    					}
				    				}
				    			}
				    			for(var i=0; i<resourcesTypes.length; i++){	//去除第一级无资源的资源类型
				    				var children = resourcesTypes[i].children;
				    				if(!children||children.length==0){
				    					resourcesTypes.splice(i,1);
		    							i -= 1;
		    						}
				    			}
				    			success(resourcesTypes);
						    },
						    columns:[[
						        {field:'name',title:'资源类型',formatter: function(value,row,index){
						        	return (value? '<input type="checkbox" name="'+(row._parentId ? 'sResourceType' : 'fResourceType')+'" />' : '') + value;
						        }
						        },
						        {field:'resourceName',title:'资源名称',width:200,formatter: function(value,row,index){
						        	return value ? "<div class='datagrid-cell-check' style='float:left'><label><input class='oc-checkbox-locate' data-resourcetype='"+row._parentId+"' type='checkbox' data-type='1' name='resourceId' value='"+row.id+"' text='"+value+"'/></label></div><div style='position:relative;top:4px;'>"+value+"</div>" : '';
						        }}
						    ]],
						    onLoadSuccess:function(row, data){
						    	div.find(":input[name=sResourceType],:input[name=fResourceType]").click(function(e){
						    		getParent($(this), 'tr').next().find(":input[type=checkbox]").attr("checked", this.checked);
						    	});
						    }
						});
					}
				}
				that._loadAccordions.push('资源权限');
			},
			'域管理':function(that){
				
			}
		},
		getParent:function(obj, parent){
			
		},
		_saveMethods:{
			'基本信息':function(target){
				var flag = false;
				if(target._form.val().account=="admin"){
					$(":input[name=mobile]").val("");
				}
				if(target._form.validate()&&target._checkUserName()){
					var formData = target._form.val();
					if(target.cfg.type == "edit"){
						var password = target._form.jq.find(":input[name=password]"),
						cfm = target._form.jq.find(":input[name=confirmPassword]");
						password.val()==cfm.data("password")&&(formData.password='');
						$.extend(formData,{
							mobile:target._form.jq.find(":input[name=mobile]").val(),
							email:target._form.jq.find(":input[name=email]").val()
						});
					}
					
					if(target.cfg.id==target.cfg.loginUserId){	//如果为用户自己，需要默认不许修改
						var user = oc.index.getUser();
						formData.status = user.status;
						formData.userType = user.userType;
					}
					oc.util.ajax({
						url:oc.resource.getUrl((target.cfg.type=='add')?'system/user/insert.htm':'system/user/update.htm'),
						data:formData,
						async:false,
						successMsg:'保存用户基本信息成功',
						success:function(data){
							flag = data.code==200;
							if(flag&&(target.cfg.type=='add')){
								target.cfg.type = "edit";
								var d = data.data;
								target._form.jq.find(":input[name=account]").attr('readonly','readonly');
								target._form.jq.find(":input[name=id]").val(d.id);
								target.cfg.id = d.id;
							}
							flag&&(target._updateStatus = true);
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
			'角色':function(target){
				var flag = false,
				selected = target._treegrid.getSelected(),
				data = [];;
				if(!selected){
					return flag;
				}else{
					selected.each(function(i){
						var that = $(this);
						data.push("umRelations["+i+"].domainId="+that.data("parent"));
						data.push("umRelations["+i+"].userId="+target.cfg.id);
						data.push("umRelations["+i+"].roleId="+this.value);
					});
				}
				data.push("id="+target.cfg.id);
				oc.util.ajax({
					url:oc.resource.getUrl('system/user/domainRoleUpdate.htm')+"?"+data.join('&'),
				//	data:data.join('&'),
					async:false,
					success:function(data){
						flag = data.code==200;
						if(flag){
							target._cache['角色'].initData(target);
							try{
								oc.index.reloadLoginUser();
							}catch(e){
								log(e);
							}
						}
					}
				});
				return flag ;
			},
			'资源权限':function(target){
				var flag = false,
				contentDiv = target._accordionDiv.data("purview_content"),
				data = [];
				if(!contentDiv){
					return true;
				}
				
				contentDiv.find(".resource_purview_content_r").first().find(":input[type=checkbox]").each(function(i){
					var $this = $(this);
					data.push({userId:target.cfg.id,resourceId:this.value,type:$this.data("type"),domainId:$this.parent().data("domainId")});
				});
				var prameter = {userResources:data,id:target.cfg.id};
				oc.util.ajax({
					url:oc.resource.getUrl('system/user/userResourcesSave.htm'),
					data:{parameterVo:JSON.stringify(prameter)},
					async:false,
					success:function(data){
						flag = data.code==200;
						try{
							oc.index.reloadLoginUser();
						}catch(e){
							log(e);
						}
					}
				});
				return flag;
			}
		},
		_checkUserName:function(){
			var that = this;
			var flag = false;
			var account = that._form.jq.find(":input[name=account]");
			var data = {};
			data.account = account.val();
			that.cfg.id&&(data.id=that.cfg.id);
			oc.util.ajax({
				url:oc.resource.getUrl('system/user/checkAccount.htm'),
				data:data,
				async:false,
				success:function(data){
					flag = data.data==0;
					!flag&&(function(){
						account.focus();
						alert('用户名已存在，请更换');
					})();
				}
			});
			return flag;
		}
	};
	
	oc.ns('oc.module.system.user');
	
	oc.module.system.user={
		open:function(cfg){
			new userDetail(cfg).open();
		}
	};
})(jQuery);