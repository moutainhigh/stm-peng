(function() {
	function autoRefreshDetail(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		this._strategyId = cfg.id;
	}
	autoRefreshDetail.prototype = {
		constructor : autoRefreshDetail,
		dataReload:undefined,
		_form : undefined,
		_strategyId:undefined,
		_resourceForm:undefined,
		_domainId:undefined,
		_dialog:undefined,
		_saveBaseFlag:false,
		cfg : undefined,
		_datagrid:undefined,
		_executRepeatInput:undefined,
		_pageSize:8,
		isLoadding:false,
		_lastQueryParameter:null,
		_curSelectResourceList:new Array(),
		_isAutoCheck:false,
		open : function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), that = this, type = that.cfg.type;
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/resource-management/strategy/autoRefreshProfileDetail.html'),
				title : ((type == 'edit') ? '编辑' : '添加') + '策略',
				height : 450,
				width:1000,
				resizable : true,
				cache : false,
				onLoad : function() {
					that._init(dlg);
					(type == 'edit') && that._loadForm(that);//如果是编辑，加载策略基本信息
				},
				buttons : [{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						if(!that._saveBaseFlag){
							alert('请先保存策略基本信息！');
						}else{
							if(!that._form.validate()){
								alert('请检查基本信息表格是否填写完整！');
								return;
							}
							
							oc.util.ajax({
								url : oc.resource.getUrl('portal/autoRefresh/updateProfileResourceRelation.htm'),
								data : {profileId:that._strategyId,instanceIds:that._curSelectResourceList.join(',')},
								async:false,
								success : function(data) {
									if(data.code == 202){
										alert(data.data);
										return;
									}

									var operatorData = $.extend(true,{},that._form.val());
									operatorData.executRepeat = that._executRepeatInput.jq.combo('getValue');
									operatorData.isRemoveHistory = that._mainDiv.find("input[name='isRemoveHistory']:checked").val();
									oc.util.ajax({
										url : oc.resource.getUrl('portal/autoRefresh/updateAutoRefreshProfileBasic.htm'),
										data : operatorData,
										async:false,
										success : function(data) {
											if(data.code == 201){
												alert(data.data);
												return;
											}else{
												alert('保存成功！');
												if(that.cfg.callback){
													that.cfg.callback();//关闭弹出框后刷新域列表
												}
												dlg.dialog('close');
											}
										},
										successMsg:null
									});
									
								},
								successMsg:null
							});
							
						}

					}
				},{
					text:"取消",
					iconCls:"fa fa-times-circle",
					handler:function(){
						dlg.dialog('close');
					}
				}]
			});
		},
		_defaults : {
			type : 'add',
			id : undefined
		},
		_flag:'add',
		_mainDiv : undefined,
		_id : '#oc_module_resource_management_autoRefresh_detail',
		_init : function(dlg) {
			var that = this;
			that._curSelectResourceList = new Array();
			that._lastQueryParameter = null;
			that._flag = that.cfg.type;
			that._mainDiv=dlg.find(that._id).attr('id',oc.util.generateId());
			that._initMethods['基本信息'](that);//初始化基本信息
			if(that.cfg.type=='edit'){
				//更改基本信息保存状态
				that._saveBaseFlag = true;
				that._initMethods['选择资源'](that);//初始化选择资源
//				that._initMethods['规则定义'](that);//初始化规则定义
			}
			that._mainDiv.accordion({
				onSelect:function(title,index){
					if(index == 2){
						return;
					}
					that._initMethods[title](that);
				},
				onBeforeSelect:function(title, index){
					//如果没有保存基本信息
					if(!that._saveBaseFlag && index == 1){
						alert("请先保存基本信息","danger");
						return false;
					}
					this.selectedIdx=index;
					return true;
				}
			});
		},
		_loadForm : function(that) {
			//加载策略基本信息
			var that = this;
			oc.util.ajax({
				url:oc.resource.getUrl('portal/autoRefresh/getAutoProfileDetail.htm'),
				data:{
					profileId:that._strategyId
				},
				async:false,
				successMsg:null,
				success:function(data){
					that._domainId = data.data.domainId;
					that._form.val(data.data);
					that._form.find(".createUser").text(data.data.createUserName);
					that._form.find(".createTime").text(formatDate(new Date(data.data.createTime)));
					that._form.find("input[name='isUse']").val(data.data.isUse);
					if(data.data.updateUserName != null) {
						that._form.find(".updateUser").text(data.data.updateUserName);
					}
					if(data.data.updateTime != null) {
						that._form.find(".updateTime").text(formatDate(new Date(data.data.updateTime)));
					}
					that._form.find("#updateUser").val(oc.index.getUser().id);
					
					if(data.data.profileInstanceRel && data.data.profileInstanceRel.length > 0){
						//记录用户选择的资源
						for(var i = 0 ; i < data.data.profileInstanceRel.length ; i ++){
							that._curSelectResourceList.push(data.data.profileInstanceRel[i].instanceId);
						}
					}
					
					that._executRepeatInput.jq.combobox('setValue',data.data.executRepeat);
					that._mainDiv.find("input[name='isRemoveHistory'][value='" + data.data.isRemoveHistory + "']").attr('checked','checked');
					
				}
			});
		},
		_initMethods : {
			'基本信息' : function(that) {
				if(!that._form) {
					
				   //创建规则的下拉框
				   that._executRepeatInput = oc.ui.combobox({
						  selector:that._mainDiv.find("input[name='executRepeat']"),
						  width:190,
						  placeholder:false,
						  selected:1,
						  data:[{id:"1",name:'1天'},{id:"7",name:'7天'},{id:"15",name:'15天'},{id:"30",name:'30天'}]
					});
					
					var domain = $.extend({},{selector:'[name=domainId]',placeholder:null},
				        that.cfg.loginUserType!=2 ? {data:oc.index.getDomains()} : {url:oc.resource.getUrl('system/user/getLoginUserDomains.htm'),
							filter:function(data){
								return data.data;
							}
						});
					that._form = oc.ui.form({
						selector : that._mainDiv.find("form.autoRefresh_form"),
						combobox :[domain]
					});
					if(that.cfg.type == "add") {
						that._form.find(".createUser").text(oc.index.getUser().account);
						that._form.find("#createUser").val(oc.index.getUser().id);
					}
					that._form.find(".defineBtn").first().linkbutton('RenderLB', {
						iconCls : 'fa fa-save',
						text : '保存',
						onClick : function(){
							var name = that._form.find("input[name='profileName']").val();
							if(name.length > 16) {
								alert("策略名称不能超过16个字!","danger");
								return;
							}
							if(that._strategyId != undefined){
								that._form.find("#updateUser").val(oc.index.getUser().id);
							}
							//如果保存成功刷新域列表
							that._saveMethods.baseInfo(that)
							if(that._saveBaseFlag) {
								if(that.cfg.callback){
									that.cfg.callback();
								}
							}
						}
					});
				}
			},
			'选择资源' : function(that) {
				if(!that._resourceForm) {
					var firstCategory = null;
					that._resourceForm = oc.ui.form({
						selector : that._mainDiv.find('#select_resource_form'),
						combobox :[{
							selector:'[name=categoryId]',
							url:oc.resource.getUrl('portal/autoRefresh/getResourceCategory.htm'),
							filter:function(data){
								if(!firstCategory && data.data && data.data.length > 0){
									firstCategory = data.data[0].id;
								}
								return data.data;
							},
							onLoadSuccess:function(){
								that._resourceForm.find("input[name='domainId']").val(that._domainId);
								that._resourceForm.find(".search_resource").linkbutton('RenderLB', {
									iconCls : 'icon-search',
									text : '查找',
									onClick : function(){
										$('#resource_grid_div').find('.datagrid-view2>.datagrid-body').scrollTop(0);
										var searchValue = that._resourceForm.find("#search_resource_name").val();
										var selectValue = that._resourceForm.find("input[name='categoryId']").val();
										that._showResource(selectValue,searchValue);
									}
								});
								var columns = [[
				                    {field:'instanceId',title:'资源ID',checkbox:true},
				                    {field:'resourceIp',title:'IP地址',width:226},
				                    {field:'resourceShowName',title:'资源名称',width:226},
				                    {field:'resourceName',title:'资源类型',width:226},
				                    {field:'profileName',title:'当前策略',width:226},
				                    {field:'profileId',hidden:true}
			                    ]];
								that._datagrid = oc.ui.datagrid({
									selector:that._mainDiv.find('#resource_select_strategrid'),
									pagination:false,
									selectOnCheck:false,
									fitColumns:false,
									columns:columns,
									onCheck:function(index,row){
										if(that._isAutoCheck){
											that._isAutoCheck = false;
											return;
										}
										that._curSelectResourceList.push(row.instanceId);
									},
									onUncheck:function(index,row){
										if(that._isAutoCheck){
											that._isAutoCheck = false;
											return;
										}
				                    	for(var i = 0 ; i < that._curSelectResourceList.length ; i ++){
				                    		if(that._curSelectResourceList[i] == row.instanceId){
				                    			that._curSelectResourceList.splice(i,1);
				                    			break;
				                    		}
				                    	}
									},
									onCheckAll:function(rows){
										if(that._isAutoCheck){
											that._isAutoCheck = false;
											return;
										}
										
					          			//发送请求获取所有资源进行全选操作
					          			oc.util.ajax({
					          				  successMsg:null,
					          				  url: oc.resource.getUrl('portal/autoRefresh/getAllResourceInstancesID.htm'),
					          				  data:that._lastQueryParameter,
					          				  success:function(data){
					          					  
					          					  for(var i = 0 ; i < data.data.length ; i ++){
					          						  var isContain = false;
					          						  for(var j = 0 ; j < that._curSelectResourceList.length ; j ++){
					          							  if(that._curSelectResourceList[j] == data.data[i]){
						          								isContain = true;
						          								break;
					          							  }
					          						  }
					          						  if(!isContain){
					          							  that._curSelectResourceList.push(data.data[i]);
					          						  }
					          					  }
					          					  
					          				  }
					          				  
					          			});
										
									},
									onUncheckAll:function(rows){
										if(that._isAutoCheck){
											that._isAutoCheck = false;
											return;
										}
				                    	
					          			//发送请求获取所有资源进行全选操作
					          			oc.util.ajax({
					          				  successMsg:null,
					          				  url: oc.resource.getUrl('portal/autoRefresh/getAllResourceInstancesID.htm'),
					          				  data:that._lastQueryParameter,
					          				  success:function(data){
					          					  
					          					  for(var i = 0 ; i < data.data.length ; i ++){
					          						  for(var j = 0 ; j < that._curSelectResourceList.length ; j ++){
							                        		if(that._curSelectResourceList[j] == data.data[i]){
							                        			that._curSelectResourceList.splice(j,1);
							                        			break;
							                        		}
					          						  }
					          					  }
					          					  
					          				  }
					          				  
					          			});
				                    	
									},
									onLoadSuccess:function(data){
										out : for(var i = 0 ; i < data.rows.length ; i ++){
											for(var j = 0 ; j < that._curSelectResourceList.length ; j ++){
												if(data.rows[i].instanceId == that._curSelectResourceList[j]){
													that._isAutoCheck = true;
													that._datagrid.selector.datagrid('checkRow',that._datagrid.selector.datagrid('getRowIndex',data.rows[i]));
													continue out;
												}
											}
										}
									}
								});
								that._showResource(firstCategory,'');
								that._createChildResourcePaging();
							},
							placeholder:false
						}]
					});
				}
			}
		},
		_showResource : function(category,searchContent){
			//展示资源
			var parameter = {};
			parameter.categoryId = category;
			parameter.domainId = this._domainId;
			parameter.start = 0;
			parameter.pageSize = this._pageSize;
			parameter.searchContent = searchContent;
			parameter.profileId = this._strategyId;
			this._lastQueryParameter = $.extend(true,{},parameter);

			var that = this;
			that.isLoadding = true;
			oc.util.ajax({
				  url: oc.resource.getUrl('portal/autoRefresh/getResourceInstances.htm'),
				  data:parameter,
				  success:function(result){
					  if(result.data){
						  that._datagrid.selector.datagrid('loadData',result);
						  that._lastQueryParameter.start = result.data.rows.length;
					  }else{
						  alert('查询资源实例失败!');
					  }
					  that.isLoadding = false;
				  }
			  });
		},
		_createChildResourcePaging : function(){
				var that = this;
				//创建资源表格的分页
			    //注册滚动条滚动到底部翻页事件
				var timeout;
				var scrollDivChild = $('#resource_grid_div').find('.datagrid-view2>.datagrid-body');
				scrollDivChild.on('scroll',function(){
					/*清除定时器，防止滚动滚动条时多次触发请求(IE)*/
					clearTimeout(timeout);
						/*当请求完成并且在页面上显示之后才能继续请求*/
						if(!that.isLoadding){
							 /*当滚动条滚动到底部时才触发请求，请求列表数据*/
							 if($(this).get(0).scrollHeight - $(this).height() <= $(this).scrollTop()){
								 timeout = setTimeout(function(){
			                    		//发送请求获取子资源列表
									 	that.isLoadding = true;
									 	
			            				oc.util.ajax({
				          					  successMsg:null,
				          					  url: oc.resource.getUrl('portal/autoRefresh/getResourceInstances.htm'),
				          					  data:that._lastQueryParameter,
				          					  success:function(data){
				          						  
					          					    if(!data.data.rows || data.data.rows.length <= 0){
					          					    	alert('所有数据已加载完毕');
					          					    	that.isLoadding = false;
					          					    	return;
					          					    }else{
					          					    	that._lastQueryParameter.start = that._lastQueryParameter.start + data.data.rows.length;
					          					    }
					          					    for(var i = 0 ; i < data.data.rows.length ; i ++){
								                    	for(var j = 0 ; j < that._curSelectResourceList.length ; j ++){
								                    		if(that._curSelectResourceList[j] == data.data.rows[i].instanceId){
								                    			data.data.rows[i].checked = 'checked';
								                    			break;
								                    		}
								                    	}
					          					    	that._datagrid.selector.datagrid('appendRow',data.data.rows[i]);
					          					    }
					          					  that.isLoadding = false;
				          					  }
				          					  
				          				 });
										 
								 },200);
							 }
						}
				});
		},
		_saveMethods : {
			'baseInfo' : function(that) {
				if(that._form.validate()){
					
					if(that.cfg.type == 'add' && that._strategyId == undefined){
						//添加默认启用
						that._form.find("input[name='isUse']").val('1');
					}
					
					var operatorData = $.extend(true,{},that._form.val());
					operatorData.executRepeat = that._executRepeatInput.jq.combo('getValue');
					operatorData.isRemoveHistory = that._mainDiv.find("input[name='isRemoveHistory']:checked").val();
					
					oc.util.ajax({
						url : oc.resource.getUrl((that.cfg.type == 'add' && that._strategyId == undefined) ? 'portal/autoRefresh/saveAutoRefreshProfileBasic.htm': 'portal/autoRefresh/updateAutoRefreshProfileBasic.htm'),
						data : operatorData,
						async:false,
						success : function(data) {
							if(data.code == 201){
								alert(data.data);
								return;
							}
							that._strategyId = data.data.id;
							that._form.find("input[name='id']").val(data.data.id);
							that._domainId = that._form.find("input[name='domainId']").val();
							that._form.find("#updateUser").val(oc.index.getUser().id);
							that._form.find(".createTime").text(formatDate(new Date(data.data.createTime)));
							if(data.data.updateTime){
								that._form.find(".updateUser").text(data.data.updateUserName);
								that._form.find(".updateTime").text(formatDate(new Date(data.data.updateTime)));
							}
							that._saveBaseFlag = true;
						},
						successMsg:"基本信息"+((that.cfg.type == 'add'&& that._strategyId == undefined)?"添加":"更新")+"成功"
					});
				}
			}
		}
	};
	function formatDate(date){
		var currentDate = "";
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		
		var hour = date.getHours();
		var minutes = date.getMinutes();
		var sencondes = date.getSeconds();
		currentDate = year+'-'+month+'-'+day
		if(hour<10){
			currentDate+=' '+'0'+hour;
		}else{
			currentDate+=' '+hour;
		}
		if(minutes<10){
			currentDate+=':'+'0'+minutes;
		}else{
			currentDate+=':'+minutes;
		}
		if(sencondes<10){
			currentDate+=':'+'0'+sencondes;
		}else{
			currentDate+=':'+sencondes;
		}
		return currentDate;
	}
	function pushIdToArr(id,array){
		if(array.length==0){
			array.push(id);
		}else{
			var flag = true;
			for(var i=0;i<array.length;i++){
				if(id==array[i]){
					flag = false;
					return ;
				}
			}
			if(flag){
				array.push(id);
			}
		}
	}
	oc.ns('oc.profile.auto.refresh');

	oc.profile.auto.refresh = {
		open : function(cfg) {
			new autoRefreshDetail(cfg).open();
		}
	};
})(jQuery);
function stopProp(event){
	event.stopPropagation();
}