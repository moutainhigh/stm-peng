/**多链路*/
function MutilLink(args){
	this.args = args;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/MutilLink.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};

MutilLink.prototype={
	init:function(html){
		this.root = $(html);
		this.root.dialog({
			title:"连线编辑",
			width:1100,
			height:500,
			onClose:function(){
				eve("topo.refresh");	//刷新当前拓扑
			}
		});
		//搜索全局域
		this.searchField("field");
		this.initUi();
	},
	searchField:function(type){	//搜索域
		var ctx = this;
		this.fields={};
		this.root.find("[data-"+type+"]").each(function(index,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-"+type)] = tmp;
		});
	},
	//初始化按钮
	initUi:function(){
		//初始化表格
		this.initDatagrid();
		//移动链路状态提示信息到datagrid
		this.initLinkColorDiv();
	},
	//初始化链路状态提示信息
	initLinkColorDiv:function(){
		var firstBtn = $(".oc-toolbar");
		var linkLineColor = $("#linkLineColor");
		linkLineColor.prependTo(firstBtn);
		linkLineColor.css({"margin-top": "8px",float: "left"});
		linkLineColor.show();
	},
	//多链路表格
	initDatagrid:function(){
		var ctx = this;
		this.datagrid = oc.ui.datagrid({
			selector:ctx.fields.multiLinkDatagrid,
			url : oc.resource.getUrl("topo/link/multi/list.htm?from="+ctx.args.fromNodeId+"&to="+ctx.args.toNodeId),
			singleSelect:false,
			pagination:true,
			octoolbar:{
		    	 right:[{
						iconCls: 'fa fa-trash-o',
						text:"删除链路",
						onClick: function(){
							ctx.delMutiLink();
						}
					},"&nbsp;",{
						iconCls: 'fa fa-times-circle',
						text:"取消监控",
						onClick: function(){
							ctx.closeMonitor();
						}
					},"&nbsp;",{
						iconCls:"fa fa-plus",
						text:"开启监控",
						onClick:function(){
							ctx.openMonitor();
						}
					},"&nbsp;",{
						iconCls:"l-btn-icon icon_turnlink",
						text:"转为链路",
						onClick:function(){
							ctx.transToLink();
						}
					}]
			},
			columns:[[
		        {field:"id",checkbox:true},
		        {field:'instanceId',hidden:true},
				{field:'srcMainInstName',title:'源端名称',ellipsis:true,width:110},
				{field:'srcMainInstIP',title:'源端IP',width:70},
				{field:'srcIfName',title:'源端端口名称',ellipsis:true,width:110},
				{field:'destMainInsName',title:'目的端名称',ellipsis:true,width:120},
				{field:'destMainInstIP',title:'目的端IP',width:70},
				{field:'destIfName',title:'目的端端口名称',ellipsis:true,width:120},
				{field:'insStatus',title:'链路状态',width:50,formatter: function(value,row,index){
					if(null !=value || "" == value){
						var tmp = ("disabled"==value)?'<span>×</span>':'';
						return '<span class="topo-line line-'+value+'" >'+tmp+'</span>';
					}else if("link" == row.type && null == row.instanceId){	//未实例化链路
						return "未实例化";
					}else if("line" == row.type){
						return "连线";
					}else {
						return "- -";
					}
		 		}},
				{field:'monitorStatus',title:'监控状态',halign:"center",width:80,formatter: function(value,row,index){
		        	return ctx.formartMonitor(value,row,index);
		         }},
				{field:'operation',title:'操作',width:80,align:"center",halign:"center",formatter: function(value,row,index){
		        	 return ctx.formartOperation(value,row,index);
	        	 }}
	         ]]
		});
	},
	//格式化操作
	formartOperation:function(value,row,index){
		var ctx = this;
		var tmp = $("<div data-index='"+index+"'><span class='icon-edit ' title='编辑' /><span class='ico icon-del' title='删除' /><span class='ico ico-mark' title='详细信息' /><div>");
		tmp.find("[title]").on("click",function(event){event.stopPropagation();ctx._clickOperation($(this))});
		return tmp.get(0);
	},
	//操作栏绑定点击事件
	_clickOperation:function(dom){
		//1.获取行数据
		var row = this.getClickRowData(dom.parent().attr("data-index"));
		var title = dom.attr("title");
		var isLink = ("line" == row.type)?false:true;
		if("编辑" == title){
			if(isLink){	//弹出编辑链路框
				if("monitored" == row.monitorStatus) {
					var link = {d:{instanceId:row.instanceId},grid:this.datagrid};
					oc.resource.loadScript("resource/module/topo/contextMenu/EditLink.js", function(){
						new EditLinkDia(link);
					});
				}else{
					alert("请先【开启监控】后再编辑");
				}
			}else{
				alert("请先【转为链路】后再编辑");
			}
		}else if("删除" == title){
			this._delLink(row.id,row.parentId);
		}else if("详细信息" == title){
			if(isLink){
				oc.resource.loadScript("resource/module/topo/contextMenu/TopoLinkInfo.js",function(){
					new TopoLinkInfo({
						onLoad:function(){
							this.load(row.instanceId);
						}
					});
					/*oc.util.ajax({
						url:oc.resource.getUrl("topo/instanceTable/link/list.htm"),
						type: 'GET',
						dataType: "json", 
						data:{resourceIds:row.instanceId},
						successMsg:null,
						success:function(data){
							new TopoLinkInfo(data);
						}
					});*/
				});
			}else{
				alert("请先【转为链路】后再查看详细信息");
			}
		}
	},
	//格式化监控状态
	formartMonitor:function(value,row,index){
		var ctx = this;
		 var tmp = $("<span data-index='"+index+"' style='cursor:pointer;' class='oc-top0 locate-left status oc-switch "+(value=="monitored" ? "open" : "close")+"' />");
		 tmp.on("click",function(event){event.stopPropagation();ctx._clickMonitor($(this));});
		 return tmp.get(0);
	},
	//点击监控状态
	_clickMonitor:function(dom){
		//1.获取行数据
		var row = this.getClickRowData(dom.attr("data-index"));
		var isLink = ("line" == row.type)?false:true;
		if(isLink){
			var type = ("monitored"==row.monitorStatus)?"cancel":"add";	//判断加入/取消监控
			this._monitor(row.instanceId,type);
		}else{
			alert("请先【转为链路】后再改变监控状态");
		}
	},
	//加入/取消监控
	_monitor:function (instanceIds,type){
		var ctx = this;
		oc.util.ajax({
		url:oc.resource.getUrl("topo/link/batch/monitor.htm"),
		data:{instanceIds:instanceIds,type:type},
		success:function(result){
			alert(result.data);
			if(result.code==200){
				ctx.datagrid.reLoad();
			}
		}
	});
	},
	//删除链路
	_delLink:function(linkIds,subTopoId){
		var ctx = this;
		$.messager.confirm("删除链路", "确定删除此链路？", function(isDel) {
			if (isDel) {
				oc.util.ajax({
					url : oc.resource.getUrl("topo/link/removeLink.htm"),
					data : {
						subTopoId : subTopoId || 0,	//0:二层；其他数字为子拓扑
						ids : linkIds
					},
					success : function(result) {
						alert(result.msg);
						if (result.code == 200) {
							ctx.datagrid.reLoad();
						}
					}
				});
			}
		});
	},
	//获取点击行的数据
	getClickRowData:function(index){
		var rowData;
		var rows = this.fields.multiLinkDatagrid.datagrid("getRows");
		$(rows).each(function(idx,row){
			if(idx == index) rowData = row;
		});
		return rowData;
	},
	//获取选中行的指定属性值
	getSelectedValues:function(field){
		var rows = this.datagrid.getSelections(),values=[];
		for(var i=0,len=rows.length;i<len;i++){
			values[i]=rows[i][field];
		}
		return values; 
	},
	//获取选中行数据
	getSelectedRows:function(field){
		var rows = this.datagrid.getSelections(),values=[];
		for(var i=0,len=rows.length;i<len;i++){
			values[i]=rows[i];
		}
		return values; 
	},
	//转为链路
	_transToLink:function(row){
		var ctx = this;
		//组织from和to数据
		var fromNode = {d:{instanceId:row.srcInstanceId,ip:row.srcMainInstIP}};
		var toNode = {d:{instanceId:row.destInstanceId,ip:row.destMainInstIP}};
		oc.resource.loadScript("resource/module/topo/contextMenu/TopoAddLink.js", function(){
			var tal = new TopoAddLink({
				from:fromNode,to:toNode,id:row.id
			});
			tal.on("ok",function(linkInfo){
				linkInfo.id=row.id;
				//添加链路到后台
				oc.util.ajax({
					url:oc.resource.getUrl("topo/newlink.htm"),
					data:{
						info:JSON.stringify(linkInfo)
					},
					timeout:1000000,
					type:"post",
					dataType:"json",
					success:function(link){
						alert("转为链路成功");
						ctx.datagrid.reLoad();
					}
				});
			});
		});
	},
	////删除链路
	delMutiLink:function(){
		//获取选中链路ids
		var ids = this.datagrid.getSelectIds();
		if(ids.length == 0){
			alert("请选择至少一条数据","danger");
		}else{
			var row0 = this.getClickRowData(0);	//列表都属于同一个父拓扑，所以去第一行的parentId即可知道是属于哪一个拓扑
			this._delLink(ids.join(","),row0.parentId);
		}
	},
	//取消监控
	closeMonitor:function(){
		var instanceIds = this.getSelectedValues("instanceId");
		if(instanceIds.length == 0){
			alert("请选择至少一条数据","danger");
		}else{
			//获取选中的行	
			var rows = this.getSelectedRows();
			var isAllMonitored = true;
			$.each(rows,function(index,row){
				if(row.type=="line" || row.monitorStatus == "not_monitored"){
					isAllMonitored =false;
					return false;
				}
			});
			if(isAllMonitored){
				this._monitor(instanceIds.join(","),"cancel");
			}else{
				alert("请选择都是【已监控】的【链路】","danger");
			}
		}
	},
	//开启监控
	openMonitor:function(){
		var instanceIds = this.getSelectedValues("instanceId");
		if(instanceIds.length == 0){
			alert("请选择至少一条数据","danger");
		}else{
			//获取选中的行
			var rows = this.getSelectedRows();
			var isAllUnonitored = true;
			$.each(rows,function(index,row){
				if(row.type=="line" || row.monitorStatus == "monitored"){
					isAllUnonitored =false;
					return false;
				}
			});
			if(isAllUnonitored){
				this._monitor(instanceIds.join(","),"add");
			}else{
				alert("请选择都是【未监控】的【链路】","danger");
			}
		}
	},
	//转为链路
	transToLink:function(){
		//获取选中的行
		var rows = this.getSelectedRows();
		if(rows.length != 1){
			alert("请仅选择一条【连线】","danger");return;
		}
		var row = rows[0];
		if(row.type=="link"){
			alert("链路不允许重复转换");
		}else{
			this._transToLink(row);
		}
	}
};