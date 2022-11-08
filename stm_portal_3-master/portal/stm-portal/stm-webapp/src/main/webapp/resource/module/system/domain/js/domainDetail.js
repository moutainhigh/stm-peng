(function() {
	function domainDetail(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}

	domainDetail.prototype = {
		constructor : domainDetail,
		_form : undefined,
		_dcsDatagrid : undefined,//域DCSDataGrid
		domainUserGrid:undefined,//域用户角色对象
		_dialog:undefined,
		cfg : undefined,
		_loadDomainUserIds:[],//编辑时加载的用户角色信息
		_operDomainUserIds:[],
		_loadDomainDcsIds:[],//编辑时加载的域 DCS
		_loadBaseInfo:{},//编辑时加载的基本信息
		_cancelBtn:false,
		open : function() {//打开添加、编辑域弹出框
			var dlg = this._mainDiv = $('<div/>'), that = this, type = that.cfg.type;
			this._dialog = dlg.dialog({
				href : oc.resource.getUrl('resource/module/system/domain/domainDetail.html'),
				title : ((type == 'edit') ? that.cfg.name+' / 编辑' : '添加') + '域',
				height : 450,
				resizable : false,
				cache : false,
				onLoad : function() {
					dlg.find(".easyui-linkbutton").linkbutton();
					that._init(dlg);
					(type != 'add') && that._loadForm();//如果是编辑，加载域基本信息
				},
				onClose:function(){
					if(that._cancelBtn){
						dlg.dialog('destroy');
						return true;
					}else{
						if(that.cfg.type!="add"){
							if(!that._checkIsUpdate(that)){/*
								oc.ui.confirm('你修改过域信息，是否保存并关闭？',function(){
									if(that.saveDomain(that)){
										if(that.cfg.callback){
											that.cfg.callback();//关闭弹出框后刷新域列表
										}
										dlg.dialog('destroy');
										return true;
									}
								},function(){
									dlg.dialog('destroy');
									return true;
								});
							*/}
						}
						dlg.dialog('destroy');
						return true;
					}
				},
				buttons : [{
					text:"确定",
					iconCls:"fa fa-check-circle",
					handler:function(){
						var flag = false;
						if(that.cfg.type=='edit'){
							if(!that._checkIsUpdate(that)){
								flag = that.saveDomain(that);
							}else{
								flag=true;
							}
						}else{
							flag = that.saveDomain(that);
						}
						
						if(flag){
							if(that.cfg.callback){
								that.cfg.callback();//关闭弹出框后刷新域列表
							}
							that._cancelBtn=true;
							dlg.dialog('close');
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
		_mainDiv : undefined,
		_id : '#oc_module_system_domain_detail',
		_init : function(dlg) {
			//初始化新增、编辑域弹出框里的form、datagrid、treegrid
			var that = this;
			that._initMethods.baseInfo(that);//初始化基本信息
			that._initMethods.domainUser(that);//初始化域用户
			that._initMethods.domainDCS(that);//初始化域DCS
			that._mainDiv = dlg.find(that._id).attr('id', oc.util.generateId());
			that._mainDiv.accordion({
				onBeforeSelect:function(title, index){
					this.selectedIdx=index;
					return that._form.validate();
				}
			});
			
			
		},
		_checkIsSaveBaseInfo:function(){//检查域基本信息是否保存
			return this.cfg.id?true:false;
		},
		_initMethods : {
			'baseInfo' : function(that) {
				that._form = oc.ui.form({
					selector : that._mainDiv.find("form.domain_form")
				});
				if(that.cfg.isDomainManager){
					that._form.disable();
				}
			},
			'domainUser' : function(that) {
				var myform = that._mainDiv.find("#domainUserSearchForm");
				var initDomainUser = function(userName){
					that._mainDiv.find("#domain_user_div").empty();
					that.domainUserGrid = system.ui.accodtion({
						selector:that._mainDiv.find("#domain_user_div"),
						url:oc.resource.getUrl('system/domain/getAllUserRole.htm'),
						params:{userName:userName},
						contentEllipsis:true,
						columns:[
						    {field:'account', title:'用户名', width: 120},
						    {field:'name', title:'姓名', width: 120},
						    {field:'角色', title:'角色', width: 410, label:true,format:function(index, row){
						    	return "";
						    }}
						]
					});
					
					if(that.cfg.type=='edit'){
						oc.util.ajax({
							url : oc.resource.getUrl('system/domain/queryUserRoleByDomain.htm'),
							data : {domainId:that.cfg.id},
							async:false,
							successMsg : null,
							success : function(data) {
								that._loadDomainUserIds = [];
								that._operDomainUserIds = [];
								var rels = data.data;
								if(rels!=null && rels.length>0){
									for(var i =0;i<rels.length;i++){
										var userRole = new Object();
										userRole.userId = rels[i].userId;
										userRole.roleId = rels[i].roleId;
										that.domainUserGrid.select(rels[i].userId,rels[i].roleId);
										that._loadDomainUserIds.push(userRole);
										that._operDomainUserIds.push(userRole);
									}
								}
									
								/**
								 * 如果是域管理员，禁用所有checkbox
								 * */
								if(that.cfg.isDomainManager){
									that.domainUserGrid.disabledAll();
								}
							}
						});
					}
				}
				
				myform.find("#domainUserName").keydown(function(e){
					if(e.keyCode==13)return false;
				});
				myform.find("#domainUserName").keyup(function(e){	//此处用keyup的目的是避免按着键不放导致重复请求
					e.keyCode==13&&(myform.find("#searchUserName").trigger('click'));
				});
				
				myform.find("#searchUserName").linkbutton('RenderLB', {
					iconCls : 'icon-search',
					text:'搜索',
					onClick : function(){
						var searchValue = $("#domainUserName").val();
						initDomainUser(searchValue);
					}
				});
				initDomainUser("");
				
				/**
				 * 操作用户角色
				 * */
				var operDomainUser = function(du,type){
					var dulist = that._operDomainUserIds;
					var flag = false;
					var flagIndex;
					for(var i=0;i<dulist.length;i++){
						var odu = dulist[i];
						if(odu.userId == du.userId && odu.roleId==du.roleId){
							flag=true;
							flagIndex = i;
						}
					}
					
					if(type=="add"){
						if(!flag){
							that._operDomainUserIds.push(du);
						}
					}else if(type=="remove"){
						if(flag){
							that._operDomainUserIds.splice(flagIndex,1);
						}
					}
				}
				
				that._mainDiv.find("#domain_user_div input[type=checkbox]").change(function(){
					var du = new Object();
					du.userId = $(this).data("parent");
					du.roleId = $(this).val();
					if($(this).attr("checked")==true || $(this).attr("checked")=="checked"){
						operDomainUser(du,"add");
					}else{
						operDomainUser(du,"remove");
					}
				});
				
			},
			'domainDCS' : function(that) {
				that._dcsDatagrid = oc.ui.datagrid({
					selector : that._mainDiv.find(".dcs_datagrid"),
					url : oc.resource.getUrl('system/domain/getDomainDcs.htm'),
					queryParams:{domainId:that.cfg.id},
					width:750,
					fit:true,
					pagination : false,
					fitColumns:false,
					selectedCfg:{
						field:'isChecked',
						fieldValue:1
					},
					columns : [[ 
			              {field : 'dcsId',title : '-',checkbox : true,width : 20}, 
			              {field : 'dcsName',title : '名称',width : 195,ellipsis:true}, 
			              {field : 'dcsIp',title : 'IP地址',width : 150}, 
			              {field : 'dcsState',title : '可用状态',width : 170,align:'center',formatter:function(value,row,index){
			            	  var ico = value==1?'light-ico greenlight':'light-ico redlight';
			            	  return "<span class='"+ico+"'></span>";
			              }}, 
			              {field : 'dhs',title : '所属DHS',width : 200,ellipsis:true} 
		             ]],
		             onLoadSuccess:function(data){
		            	var datas = data.datas;
						if(datas!=null && datas.length>0){
							that._loadDomainDcsIds=[];
							for(var i=0;i<datas.length;i++){
								if(datas[i].isChecked==1){
									that._loadDomainDcsIds.push(datas[i].dcsId);
								}
							}
						}
						
						if(that.cfg.isDomainManager){
							that._dcsDatagrid.selector.prev().find("input[type='checkbox']").each(function(index, el){
				              el.disabled=true; 
				            });
							var s = that._dcsDatagrid.selector.datagrid('getPanel');
							var rows = s.find('tr.datagrid-row');
							var rows1 = s.find('tr.datagrid-row td[field!=ck]');
							rows1.unbind('click').bind('click',function(e) {
								return false;
							});
						}
					}
				});
			}
		},
		_loadForm : function() {
			//加载域基本信息
			var that = this;
			oc.util.ajax({
				url : oc.resource.getUrl('system/domain/get.htm'),
				data : {
					id : that.cfg.id
				},
				async:false,
				successMsg : null,
				success : function(data) {
					that._loadBaseInfo = data.data;
					that.cfg.domainName = data.data.name;
					that._form.val(data.data);
				}
			});
		},
		saveDomain:function(that){//保存域信息
			var flag = false;
			if (that._form.validate() && that._checkDomainName()) {
				//--------基本信息
				var baseInfo = that._form.val();
				//--------用户角色
				var userRole =that.domainUserGrid.getSelected(),userRoleData = [];
				if(userRole){
					userRole.each(function(i){
						var that = $(this);
						var relations = new Object();
						relations.userId = that.data("parent");
						relations.roleId = this.value;
						userRoleData.push(relations);
					});
				}
				var domainUserRole = JSON.stringify(userRoleData)
				//--------DCS
				var selRows = that._dcsDatagrid.selector.datagrid("getSelections");
				var domainDcsdata = [];
				for(var i=0;i<selRows.length;i++){
					var domainDcs  = new Object();
					domainDcs.domainId=that.cfg.id;
					domainDcs.dcsId=selRows[i].dcsId;
					domainDcsdata.push(domainDcs);
				}
				var domainDcs = JSON.stringify(domainDcsdata);
				var data = {userRoleStr:domainUserRole,dcsStr:domainDcs};
				data = $.extend(that._form.val(),data);
				oc.util.ajax({
					url : oc.resource.getUrl((that.cfg.type == 'add') ? 'system/domain/insert.htm': 'system/domain/update.htm'),
					data : data,
					async:false,
					success : function(data) {
						flag = true;
						oc.index.loadLoginUser();
					},
					successMsg:"域"+(that.cfg.type == 'add'?"添加":"更新")+"成功"
				});
			}else{
				that._mainDiv.accordion("select",0);
				flag =  false;
			}
			return flag;
		},
		_checkDomainName : function() {
			var that = this;
			var flag = false;
			var name = that._form.jq.find(":input[name=name]");
			var data = {};
			data.name = name.val();
			that.cfg.id && (data.id = that.cfg.id);
			var check = function(){
				oc.util.ajax({
					url : oc.resource.getUrl('system/domain/queryByname.htm'),
					data : data,
					async:false,
					successMsg : null,
					success : function(data) {
						if(data.data!=null && data.data!=0){
							name.focus();
							alert('域名称已经存在，请重新输入');
						}else{
							flag=true;
						} 
					}
				});
			};
			if(that.cfg.type=="edit"){
				if(data.name != that.cfg.domainName){
					check();
				}else{
					flag=true;
				}
			}else{
				check();
			}
			return flag;
		},
		_checkIsUpdate:function(that){
			var check = true;
			
			if(that._form!=null && that._form!=undefined){
		
				that._form.enable();
				var tarBaseInfo = that._form.val();
				var srcBaseInfo = that._loadBaseInfo;
				if(srcBaseInfo.id!=tarBaseInfo.id || srcBaseInfo.name!=tarBaseInfo.name || srcBaseInfo.description==tarBaseInfo.description){
					check=false;
				}
			}
			if(that.domainUserGrid!=null && that.domainUserGrid!=undefined){
				var srcDomainUserIds = that._loadDomainUserIds;
				var tarDomainUserIds = that._operDomainUserIds;
				if(JSON.stringify(srcDomainUserIds.sort())!=JSON.stringify(tarDomainUserIds.sort())){
					check=false;
				}
			}
			if(that._dcsDatagrid!=null && that._dcsDatagrid!=undefined){
				var srcDomainDcs = that._loadDomainDcsIds;
				var tarDomainDcs=[];
				var dcsRow = that._dcsDatagrid.selector.datagrid("getSelections");
				for(var i=0;i<dcsRow.length;i++){
					tarDomainDcs.push(dcsRow[i].dcsId);
				}
				if(JSON.stringify(srcDomainDcs.sort())!=JSON.stringify(tarDomainDcs.sort())){
					check=false;
				}
			}
			return check;
		}
	};
	
	function reloadAll(that){
		that._loadForm();
		that._dcsDatagrid.reLoad();
	}
	
	oc.ns('oc.module.system.domain');

	oc.module.system.domain = {
		open : function(cfg) {
			new domainDetail(cfg).open();
		}
	};
})(jQuery);