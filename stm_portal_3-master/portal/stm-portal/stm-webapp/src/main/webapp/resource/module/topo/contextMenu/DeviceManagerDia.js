function DeviceManagerDia(args){
	var ctx = this;
	this.group = args.group;
	this.leftData = args.leftData;
	this.rightData = args.rightData;
	this.name = args.name;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/DeviceManagerDia.html"),
		type:"get",
		dataType:"html",
		success:function(html){
			ctx.init(html);
		}
	});
}
DeviceManagerDia.prototype={
	init:function(html){
		var ctx = this;
		this.dia=$(html);
		this.dia.dialog({
			width:730,height:420,
			title:"区域管理",
			buttons:[{
				text:"取消",handler:function(){
					ctx.cancel();
					ctx.dia.dialog("close");
				}
			},{
				text:"确定",handler:function(){
					if(ctx.ok()){
						ctx.dia.dialog("close");
					}
				}
			}]
		});
		//搜索域
		this.fields={};
		this.dia.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.fields.searchType.combobox({
			editable:false,
			valueField:"value",
			textField:"text",
			data:[{text:"IP",value:"ip"},{text:"设备名称",value:"showName"},{text:"设备类型",value:"type"}]
		});
		//名称
		this.fields.name.validatebox({
			required:true
		});
		this.fields.name.val(this.name);
		this.fields.searchType.combobox("select","ip");
		//初始化grid
		this.pickgrid=oc.ui.pickgrid({
			leftColumns:[[{
				field:"id",checkbox:true
			},{
				field:"ip",title:"IP",width:90,ellipsis:true
			},{
				field:"showName",title:"设备名称",width:90,ellipsis:true
			},{
				field:"type",title:"设备类型",width:90,ellipsis:true
			}]],
			rightColumns:[[{
				field:"id",checkbox:true
			},{
				field:"ip",title:"IP",width:80,ellipsis:true
			},{
				field:"showName",title:"设备名称",width:80,ellipsis:true
			},{
				field:"type",title:"设备类型",width:80,ellipsis:true
			}]],
			selector:this.fields.grid
		});
		this.leftGrid=this.pickgrid.leftGrid.selector;
		this.rightGrid=this.pickgrid.rightGrid.selector;
		//初始化过滤器
		this.leftGrid.datagrid({
			loadFilter: function(d){
				if(d instanceof Array){
					return {total:d.length,rows:d};
				}else{
					return {total:0,rows:[]};
				}
			}
		});
		this.rightGrid.datagrid({
			loadFilter: function(d){
				if(d instanceof Array){
					return {total:d.length,rows:d};
				}else{
					return {total:0,rows:[]};
				}
			}
		});
		//如果传入了数据-初始化数据
		if(this.leftData){
			this.leftGrid.datagrid("loadData",this.leftData);
		}
		//初始化已经选中的设备
		if(this.rightData){
			this.rightGrid.datagrid("loadData",this.rightData);
		}
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this;
		//搜索按钮
		this.fields.searchBtn.on("click",function(){
			var param = ctx.getSearchValue();
			var p={};
			p[param.searchType]=param.searchVal;
			var result=[];
			for(var i=0;i<ctx.leftData.length;++i){
				var item = ctx.leftData[i];
				var sv = item[param.searchType];
				if(sv && sv.toLowerCase().indexOf(param.searchVal.trim())>=0){
					result.push(item);
				}
			}
			ctx.leftGrid.datagrid("loadData",result);
		});
	},
	getSearchValue:function(){
		return {
			searchType:this.fields.searchType.combobox("getValue"),
			searchVal:this.fields.searchVal.val()
		};
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	},
	cancel:function(){
		
	},
	getValue:function(){
		var retn = {};
		var items = this.rightGrid.datagrid("getRows");
		retn.items=items;
		retn.name=this.fields.name.val();
		return retn;
	},
	//点击ok按钮
	ok:function(){
		if(this.fields.name.validatebox("isValid")){
			if(this.onok){
				this.onok(this.getValue());
			}
			return true;
		}else{
			this.fields.name.focus();
			return false;
		}
	}
};