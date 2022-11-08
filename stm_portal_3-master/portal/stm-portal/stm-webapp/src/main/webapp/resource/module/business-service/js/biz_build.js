/*业务构建*/
function BusinessBuild(args){
	this.args = $.extend({
		bizId:undefined,			//该业务构建属于的[业务系统]id
		callBack:function(type,data){}	//保存后回调函数（绘制业务图）
	},args);
	this.dialog = undefined;
	this.curPanel = undefined;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/business-service/biz_build.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};

BusinessBuild.prototype={
	init:function(html){
		var ctx = this;
		ctx.root = $(html);
		ctx.dialog = ctx.root.dialog({
			title:"资源选择",
			width:920,height:460,
			draggable:true,
			cache: false,
			modal: true ,
			buttons:[{
				text:'确定',
				iconCls:'fa fa-check-circle',
				handler:function(){
					if("autoBuild" == ctx.curPanel){	//自动构建
						ctx._saveAutoBuild();
					}else{	//手动构建
						ctx._saveManulBuild();
					}
				}
			},{
				text:'取消',
				iconCls:'fa fa-times-circle',
				handler:function(){
					ctx.dialog.dialog("close");
				}
			}]
		});
		//搜索全局域
		ctx._searchField("field");
		//搜索面板
		ctx._searchField("panel");
		ctx.regEvent();
	},
	//搜索域
	_searchField:function(type){
		var ctx = this;
		ctx[type]={};
		ctx.root.find("[data-"+type+"]").each(function(idx,dom){
			var tmp = $(dom);
			ctx[type][tmp.attr("data-"+type)]=tmp;
		});
	},
	//渲染UI
	initUi:function(){
//		this._initautoBuild();
//		this._initmanulBuild();
	},
	//保存手动构建
	_saveManulBuild:function(){
		var ctx = this;
		var datas = this.rightGrid.datagrid("getRows");
		if(datas == null || datas.length == 0){
			alert("请确认已选中资源后再保存");
		}else{
			var nodes = [];
			var canvasData = {
				bizId:ctx.args.bizId,
				nodes:nodes
			};
			$.each(datas,function(index,data){
				var dataTmp = {bizId:ctx.args.bizId};
				dataTmp.instanceId = data.id;
				//节点类型(1.资源2.业务)
				if(data.hasOwnProperty("managerName")){
					dataTmp.nodeType = 2;
					dataTmp.showName = data.name;
				}else{
					dataTmp.nodeType = 1;
					dataTmp.showName = data.showName;
				}
				nodes.push(dataTmp);
			});

			oc.util.ajax({
				url: oc.resource.getUrl('portal/business/canvas/insertCanvasNode.htm'),
				data:{canvasData:canvasData},
				success:function(data){
					if(data.code == 200 && data.data){
						alert("保存成功");
						ctx.dialog.dialog("close");
						ctx.args.callBack("manul",data);
					}else{
						alert("保存失败");
					}
				}
			});
		}
	},
	//保存自动构建
	_saveAutoBuild:function(){
		var ctx = this;
		var datas = this.autoBuildGrid.datagrid("getSelections");
		if(datas == null || datas.length == 0){
			alert("请确认已选中资源后再保存");
		}else{
			var instanceIds = [];
			$.each(datas,function(index,data){
				instanceIds.push(data.id);
			});
			oc.util.ajax({
				url: oc.resource.getUrl('portal/business/service/autoBuildBussiness.htm'),
				data:{bizId:ctx.args.bizId,instanceIds:instanceIds.join(",")},
				success:function(data){
					if(data.code == 200 && data.data){
						alert("保存成功");
						ctx.dialog.dialog("close");
						ctx.args.callBack("auto",data);
					}else{
						alert("保存失败");
					}
				}
			});
		}
	},
	_initautoBuild:function(){
		var ctx = this;
		if(ctx.autoBuild && ctx.autoBuild.init) return;	//只初始化一次，可以保留页面操作
		
		//搜索自动构建域
		ctx._searchField("autoBuild");
		ctx.autoBuild.init = true;
		var auto = ctx.autoBuild;
		auto.searchBtn.linkbutton("RenderLB",{iconCls:"fa fa-search"});
		ctx.autoForm = oc.ui.form({selector:ctx.root.find("form:first")});	//此处需要这样初始化才能使用oc的验证框架
		
		ctx.autoBuildGrid = oc.ui.datagrid({
			selector:auto.grid,
			title:"已选择资源",
			pagination:false,
			singleSelect:false,
			columns:[[
		         {field:"id",checkbox:true},
		         {field:'showName',title:'资源名称',width:89},
		         {field:'discoverIP',title:'IP地址',width:90},
		         {field:'resourceName',title:'资源类型',width:90}
	         ]]
		});
		
		ctx.autoBuildGrid = ctx.autoBuildGrid.selector;
		
		ctx._regAutoBuildEvent();
		auto.searchBtn.click();	//初始化无参数查询
	},
	//加载数据
	_loadData:function(grid,data){
		if(data && data.code == 200){
			grid.datagrid("loadData",data.data);
			if(data.data.length !=0) grid.datagrid("selectAll");
		}else{
			alert("数据查询失败");
		}
	},
	//注册自动构建panel中按钮事件
	_regAutoBuildEvent:function(){
		var ctx = this,auto = ctx.autoBuild;
		auto.searchBtn.on("click",function(){
			var searchIp = $.trim($(auto.autoSearchInput).val());
			if(ctx.autoForm.validate()){
				oc.util.ajax({//获取自动构建的资源列表
					url:oc.resource.getUrl('portal/business/service/getInstancesByAutoBuild.htm'),
					data:{ip:searchIp},
					success:function(data){
						ctx._loadData(ctx.autoBuildGrid,data);
					}
				});
			}
		});
		
		$(auto.autoSearchInput).keyup(function(e){
			if(e.keyCode==13) auto.searchBtn.click();
		});
	},
	_initmanulBuild:function(){
		var ctx = this;
		if(ctx.manulBuild && ctx.manulBuild.init) return;
		
		//搜索手动构建域
		ctx._searchField("manulBuild");
		var manul = ctx.manulBuild;
		ctx.manulBuild.init = true;
		manul.searchBtn.linkbutton("RenderLB",{iconCls:"fa fa-search"});
		manul.delBtn.linkbutton("RenderLB",{iconCls:"fa fa-trash-o"});
		manul.leftToRightBtn.linkbutton("RenderLB",{/*iconCls:"bulid_arrow"*/});
		//初始化资源表格
		ctx._initResourceGrid();
		//初始化业务系统表格
		//ctx._initSystemGrid();
		manul.systemGridParent.hide();
		//注册手动构建按钮事件
		ctx._regManulBuildEvent();
		manul.searchBtn.click();	//加载数据
	},
	//注册手动构建面板按钮事件
	_regManulBuildEvent:function(){
		var ctx = this;
		var manul = ctx.manulBuild;
		manul.resourceRadio.on("click",function(){
			manul.systemGridParent.hide();
			manul.resourceGridParent.show();
			manul.manulSearchInput.attr("placeholder","请输入资源名称或IP搜索");
		});
		manul.bizSystemRadio.on("click",function(){
			manul.resourceGridParent.hide();
//			manul.systemGridParent.html('');
//			manul.systemGridParent.append('<div data-manulBuild="systemGrid"></div>');
			manul.systemGridParent.show();
			ctx._initSystemGrid();
			manul.manulSearchInput.attr("placeholder","请输入业务系统名称搜索");
		});

		manul.searchBtn.on("click",function(){
			var searchVal = $.trim($(manul.manulSearchInput).val());
			var isChecked = manul.resourceRadio.prop("checked");
//			if(isChecked==true){
//				isChecked="checked";
//			}
			if(isChecked){
				oc.util.ajax({//获取手动构建的资源列表
					url:oc.resource.getUrl('portal/business/service/getInstancesByManualBuild.htm'),
					data:{searchContent:searchVal},
					success:function(data){
						ctx._loadData(ctx.resourceGrid,data);
					}
				});
			}else{
				oc.util.ajax({//获取手动构建的业务系统列表
					url:oc.resource.getUrl('portal/business/service/getBussinessByManualBuild.htm'),
					data:{bizId:ctx.args.bizId,searchContent:searchVal},
					success:function(data){
						ctx._loadData(ctx.systemGrid,data);
					}
				});
			}
		});
		$(manul.manulSearchInput).keyup(function(e){
			if(e.keyCode==13) manul.searchBtn.click();
		});
		
		manul.delBtn.on("click",function(){
			var selectRows = ctx.rightGrid.datagrid("getSelections");
			if(null == selectRows || selectRows.length == 0){
				alert("请选择待删除资源","danger");
			}else{
				for(var i=0;i<selectRows.length;i++){
					var index = ctx.rightGrid.datagrid("getRowIndex",selectRows[i]);
					ctx.rightGrid.datagrid("deleteRow",index);
				}
			}
		});
		manul.leftToRightBtn.on("click",function(){	//资源
			var isChecked = manul.resourceRadio.prop("checked");
			var leftSelectRows = [];
			if(isChecked){
				leftSelectRows = ctx.resourceGrid.datagrid("getSelections");
			}else{
				leftSelectRows = ctx.systemGrid.datagrid("getSelections");
				$.each(leftSelectRows,function(index,row){
					row.resourceName = "业务系统";
					row.showName = row.name;
				});
			}
			
			if(null == leftSelectRows || leftSelectRows.length == 0){
				alert("未选中任何资源","danger");
			}else{
				//业务系统不允许重复
				if(!isChecked && ctx._isRepeat(leftSelectRows)){
					alert("业务系统不允许重复","danger");
				}else{
					//此处row需要特殊处理一下，否则移到右侧grid的相同数据的索引是一样的，在删除时就会有奇怪的现象发生
					$.each(leftSelectRows,function(index,leftRow){
						ctx.rightGrid.datagrid("appendRow",$.parseJSON(JSON.stringify(leftRow)));
					});
				}
			}
		});
	},
	//检查重复添加(业务系统不允许重复)
	_isRepeat:function(leftSelectRows){
		var ctx = this;
		var isRepeat = false;
		var rightGridVal = this.rightGrid.datagrid("getRows");
		ctx.rightGrid.datagrid("uncheckAll");	//取消所有选中状态
		$.each(leftSelectRows,function(index,leftRow){
			if(leftRow.hasOwnProperty("remark")){
				$.each(rightGridVal,function(index,rightRow){
					if(rightRow.hasOwnProperty("remark")  && leftRow.name == rightRow.name){
						isRepeat = true;
						var idx = ctx.rightGrid.datagrid("getRowIndex",rightRow);
						ctx.rightGrid.datagrid("selectRow",idx);	//选中重复的行
					}
				});
			}
		});
		return isRepeat;
	},
	//初始化资源gird
	_initResourceGrid:function(){
		var ctx = this;
		ctx.resourceGrid = oc.ui.datagrid({
			selector:ctx.manulBuild.resourceGrid,
			title:"未选中资源",
			pagination:false,
			singleSelect:false,
//			url : oc.resource.getUrl(''),
			columns:[[
	          {field:"id",checkbox:true},
	          {field:'showName',title:'资源名称',ellipsis:true,width:89},
	          {field:'discoverIP',title:'IP地址',ellipsis:true,width:110},
	          {field:'resourceName',title:'资源类型',ellipsis:true,width:110}
	         ]],
	         data:[],
	         onLoadSuccess:function(data){
	        	 ctx._warpTitle("manulBuild");	//不知道为什么渲染是会有title样式的丢失，暂时用此方法解决
	         }
		});
		ctx.resourceGrid = ctx.resourceGrid.selector;
		
		ctx.rightGrid = oc.ui.datagrid({
			selector:ctx.manulBuild.rightGrid,
			title:"已选中资源",
			pagination:false,
			singleSelect:false,
//			url : oc.resource.getUrl(''),
			columns:[[
	          {field:"id",checkbox:true},
	          {field:'showName',title:'资源名称',ellipsis:true,width:89},
	          {field:'discoverIP',title:'IP地址',ellipsis:true,width:110},
	          {field:'resourceName',title:'资源类型',ellipsis:true,width:110}
	          ]],
	          data:[],
	          onLoadSuccess:function(data){
	        	 ctx._warpTitle("manulBuild");
	         }
		});
		ctx.rightGrid = ctx.rightGrid.selector;
	},
	//解决title渲染不全
	_warpTitle:function(panel){
		$("[data-panel='"+panel+"'] .panel-title").wrap("<div cla='oc-header-r'><div class='oc-header-m'></div></div>");
	},
	//初始化业务系统grid
	_initSystemGrid:function(){
		var ctx = this;
		ctx.systemGrid = ctx.manulBuild.systemGrid.datagrid({
			title:"未选中资源",
			pagination:false,
			singleSelect:false,
			fitColumns:true,
			columns:[[
	          {field:"id",checkbox:true,fixed:true},
	          {field:'name',title:'业务系统名称',ellipsis:true,width:130,fixed:true},
	          {field:'remark',title:'备注',align:'left',ellipsis:true,width:176,fixed:true}
	          ]],
			data:[],
	        onLoadSuccess:function(data){
	        	 ctx._warpTitle("manulBuild");
	         }
		});
		ctx.systemGrid.isInit = false;
	},
	//注册事件
	regEvent:function(){
		var ctx = this;
		//左侧菜单列表
		var tabs = this.field.findTabs.find("li");
		tabs.on("click",function(){
			//隐藏右侧面板内容
			ctx.field.findPanels.find("[data-panel]").hide();
			var tmp = $(this);
			ctx.field.findTabs.find(".topo_find_setting_tab_icon").removeClass("activetab");
			ctx.field.findTabs.find(".topo_find_setting_tab_text").removeClass("active");
			tmp.find(".topo_find_setting_tab_icon").addClass("activetab");
			tmp.find(".topo_find_setting_tab_text").addClass("active");
			
			var panelKey = tmp.attr("data-panel");
			var panel = ctx.panel[panelKey];
			panel.fadeIn();				//显示右侧面板
			ctx["_init"+panelKey]();	//初始化右侧面板
			ctx.curPanel = panelKey;	//记录当前所操作的panel
		});
		//初始化自动构建panel(默认选中第一个panel)
		$(tabs.get(0)).trigger("click");
	}
};