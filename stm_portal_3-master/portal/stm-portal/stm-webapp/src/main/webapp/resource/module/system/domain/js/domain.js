(/**
 * @param $
 */
function($) {
	var loginUser = oc.index.getUser(),userType=loginUser.userType;
	var mainId = oc.util.generateId(),
	// 模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
	mainDiv = $('#oc_module_system_domain_main').attr('id', mainId).panel({
		fit : true,
		isOcAutoWidth : true
	}),
	// 域列表文档对象
	datagridDiv = mainDiv.find('.oc_module_system_domain_main_datagrid'),
	// 域列表实例
	datagrid = null;
	var octoolbar = loginUser.domainUser?null:{
			left : [ {
				text : '添加',
				iconCls : 'fa fa-plus',
				onClick : function() {
					_open('add');
				}
			},{
				text : '删除',
				iconCls : "fa fa-trash-o",
				onClick : function() {
					delDomain();
				}
			} ]
		};
	var dlg = null;
	// 初始化域列表
	datagrid = oc.ui
			.datagrid({
				selector : datagridDiv,
				url : oc.resource.getUrl('system/domain/domainPage.htm'),
				width : 'auto',
				height : 'auto',
				sortName:'CREATED_TIME',
				sortOrder:'DESC',
				octoolbar :octoolbar,
				columns : [[
						{
							field : 'id',
							title : '-',
							checkbox : true,
							width : 20,
							isDisabled:function(value,row,index){
					        	 return value==1;
					         }
						},
						{
							field : 'name',
							title : '域',
							width : 120,
							ellipsis:true,
							sortable : true,
							formatter : function(value, row, index) {
								var title = userType==1 ? '点击查看域详情' : '点击编辑域信息';
								return "<span title='"+title+"' class='oc-pointer-operate'   domain-id='"+row.id+"' domain-name='"+row.name+"'>" + value + "</span>";
							}
						},{
							field : 'adminUser',
							title : '域管理员',
							width : 120,
							ellipsis:true
//							sortable : true,
//							formatter : function(value, row, index) {
//								return "<span class='icon-domain-detail' style='text-decoration:underline;' index='"
//										+ index + "'>" + value + "</span>";
//							}
						},
						// {field:'status',title:'状态',sortable:true,
						// width:30,formatter:function(value,row,index){
						// return "<span class='"+(1==value ? 'light-ico greenlight' : 'light-ico orangelight')+"'></span>";
						// }},
						{
							field : 'user',
							title : '用户',
							width : 30,
							formatter : function(value, row, index) {
								return "<span class='icon icon-Person light_blue oc-pointer-operate domainUser' title='点我查看用户信息' domain-id='"+row.id+"' domain-name='"+row.name+"'></span>";
							}
						},
						{
							field : 'dcs',
							title : 'DCS',
							width : 30,
							formatter : function(value, row, index) {
								return "<span class='right fa fa-lock light_blue oc-pointer-operate' title='点我查看拥有DCS'  domain-id='"+row.id+"' domain-name='"+row.name+"'></span>";
							}
						},
						{
							field : 'description',
							title : '备注',
							width : 200,
							ellipsis:true
						}] ],
				onLoadSuccess : function() {
					var execution = function(type, id,name,e) {
						if (type == "user") {
							openDomainUser(id, name,e);
						} else if (type == "dcs") {
							openDomainDcs(id, name,e);
						} else if (type == "detail") {
							_open('edit', name,id);
						}
					};
					mainDiv.find("span.icon.icon-Person.light_blue").bind('click',
							function(e) {
								execution("user", $(this).attr("domain-id"),$(this).attr("domain-name"),e);
								e.stopPropagation();
							});
					mainDiv.find("span.right.fa.fa-lock.light_blue").bind('click',
							function(e) {
								execution("dcs",$(this).attr("domain-id"),$(this).attr("domain-name"),e);
								e.stopPropagation();
							});
					mainDiv.find("span.icon-domain-detail").bind('click',
							function(e) {
								execution("detail",$(this).attr("domain-id"),$(this).attr("domain-name"));
								e.stopPropagation();
							});
				}
			});

	/**
	 * 打开该域管理的用户列表
	 */
	function openDomainUser(domainId, title,e) {
		dlg = $("<div class = 'oc-dialog-float'/>").dialog({
					content : "<div id=\"user_datagrid\"></div>",
					title : "",// title+' / 用户',
					draggable : true,
					height : 300,
					width:600,
					resizable : true,
					cache : false,
					modal : false
				});
		oc.util.showFloat(dlg,e);
		oc.ui.datagrid({
			selector : dlg.find("#user_datagrid"),
			url : oc.resource
					.getUrl('system/domain/getDomainUserIsChecked.htm'),
			queryParams : {
				id : domainId
			},
			fit : true,
			fitColumns:false,
			pagination : false,
			columns : [ [
					{
						field : 'userStatus',
						title : '状态',
						width : 50,
						align : 'center',
						formatter : function(value,	row, index) {
							return 1 == value ? '启用' : '停用';
						}
					},
					{
						field : 'userAccount',
						title : '用户名',
						align : 'left',
						width : 100
					},
					{
						field : 'userName',
						title : '姓名',
						align : 'left',
						width : 100
					},
					{
						field : 'roleName',
						title : '角色',
						align : 'left',
						width : 350,
						ellipsis:true
					} ] ]
		});
	}
	/**
	 * 打开该域管理的dcs列表
	 */
	function openDomainDcs(domainId, title,e) {
		dlg = $("<div class = 'oc-dialog-float'/>").dialog({
					content : "<div id=\"dcs_datagrid\"></div>",
					title : "",// title+' / DCS',
					draggable : true,
					width:450,
					height : 200,
					resizable : true,
					cache : false,
					modal : false
				});
		oc.util.showFloat(dlg,e);
		oc.ui.datagrid({
			selector : dlg.find("#dcs_datagrid"),
			url : oc.resource
					.getUrl('system/domain/getDomainDcsIsChecked.htm'),
			queryParams : {
				domainId : domainId
			},
			fit : true,
			pagination : false,
			fitColumns:false,
			selectedCfg : {
				field : 'isChecked',
				fieldValue : 1
			},
			columns : [ [
					{
						field : 'dcsId',
						title : '-',
						checkbox : false,
						hidden : true
					},
					{
						field : 'dcsName',
						title : '名称',
						width : 130,
						ellipsis:true
					},
					{
						field : 'dcsIp',
						title : 'IP地址',
						width : 100,
						ellipsis:true
					},
					{
						field : 'dcsState',
						title : '可用状态',
						width : 90,
						align : 'center',
						formatter : function(value,
								row, index) {
							var ico = value == 1 ? 'light-ico greenlight'
									: 'light-ico redlight';
							return "<span class='"
									+ ico + "'>";
						}
					}, {
						field : 'dhs',
						title : '所属DHS',
						width : 130,
						ellipsis:true
					}] ]
		});
	}

	function delDomain(){
		var ids=datagrid.getSelectIds(),len=ids.length;
		var delIds = [];
		if(ids && len>0){
			for(var i=0;i<len;i++){
				ids[i]!=1&&delIds.push(ids[i]);
			}
		}
		if(delIds && delIds.length>0){
			oc.ui.confirm("确认删除所选的域吗？",function(){
				oc.util.ajax({
					url:oc.resource.getUrl('system/domain/isDelete.htm'),
					data:{ids:delIds.join()},
					successMsg:"",
					success:function(data){
						if(data && data.code==200){
							var d = data.data;
							if(d.length==0){
								alert("域删除成功");
							}else if(d.length>0){
								var del = delIds.length,isNotDel = d.length,isDel = del-isNotDel;
								alert("成功删除"+isDel+"个域，域【"+d.join()+"】正在被使用，无法删除！");
							}
							oc.index.loadLoginUser();
							datagrid.reLoad();
						}else{
							alert("域删除失败");
						}
					}
				});
			});
		}else{
			alert("请选择至少一条域信息！",'danger');
		}
	}
	
	function _open(type, name,id) {
		oc.resource.loadScript(
				'resource/module/system/domain/js/domainDetail.js', function() {
					oc.module.system.domain.open({
						type : type,
						name : name,
						id : id,
						userType:userType,
						isDomainManager:loginUser.domainUser,//是否为域管理员
						callback : function() {
							datagrid.reLoad();
						}
					});
				});
	}
	
})(jQuery);