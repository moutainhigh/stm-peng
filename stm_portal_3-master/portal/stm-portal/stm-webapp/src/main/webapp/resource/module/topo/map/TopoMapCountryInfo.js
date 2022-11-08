function TopoMapCountryInfo(args){
	var ctx = this;
	this.args=$.extend({
		onOk:function(){
			ctx.root.dialog("close");
		},
		onLoad:function(){}
	},args);
	//所选择核心设备
	this.coreInstanceId=null;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/map/TopoMapCountryInfo.html"),
		success:function(htl){
			ctx.init(htl);
		},
		dataType:"html",
		type:"get"
	});
};
TopoMapCountryInfo.prototype={
	init:function(htl){
		this.root=$(htl);
		this.fields={},ctx=this;
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.root.dialog({
			width:1000,height:400,
			title:"下级资源管理",
			buttons:[{
				text:"确定",
				handler:function(){
					ctx.args.onOk.call(ctx);
				}
			},{
				text:"取消",
				handler:function(){
					ctx.root.dialog("close");
				}
			}]
		});
		this.pickgrid=oc.ui.pickgrid({
			leftOptions:{
				idField:"ip",
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				}
			},
			leftColumns:[[{
				field:"id",checkbox:true
			},{
				field:"ip",title:"IP",width:90,ellipsis:true
			},{
				field:"showName",title:"设备名称",width:90,ellipsis:true
			},{
				field:"typeName",title:"设备类型",width:90,ellipsis:true
			}]],
			rightOptions:{
				idField:"ip",
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				}
			},
			rightColumns:[[{
				field:"id",checkbox:true
			},{
				field:"ip",title:"IP",width:80,ellipsis:true
			},{
				field:"showName",title:"设备名称",width:80,ellipsis:true
			},{
				field:"typeName",title:"设备类型",width:80,ellipsis:true
			},{
				field:"instanceId",title:"核心设备",formatter:function(value,row){
					var tmp = $("<input name='core' type='radio'/>");
					tmp.data("instanceid",row.instanceId);
					tmp.on("click",function(e){
						if($(this).prop("checked")){
							ctx.coreInstanceId=row.instanceId;
						}
						e.stopPropagation();
					});
					if(row.instanceId==ctx.coreInstanceId){
						tmp.prop("checked",true);
					}
					return tmp.get(0);
				}
			}]],
			selector:this.fields.pickGrid
		});
		//初始化表格数据
		this.leftGrid=this.pickgrid.leftGrid.selector;
		this.rightGrid=this.pickgrid.rightGrid.selector;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/resource/all.htm"),
			success:function(info){
				ctx.items = info;
				ctx.leftData=ctx.items;
				ctx.leftGrid.datagrid("loadData",ctx.leftData);
				ctx.args.onLoad.call(ctx);
			}
		});
		this.regEvent();
	},
	regEvent:function(){
		var fd = this.fields,
			ctx = this;
		//搜索框回车
		fd.searchIpt.on("keyup",function(e){
			if(e.keyCode==13){
				ctx.search();
			}
		});
		//搜索按钮
		fd.searchBtn.on("click",function(){
			ctx.search();
		});
		//向右选择箭头
		var rightArrow = this.root.find(".tree-arrow-right");
		rightArrow.unbind("click");
		rightArrow.on("click",function(){
			var rows = ctx.leftGrid.datagrid("getSelections");
			ctx.setRightGridValue(rows);
		});
	},
	search:function(){
		var ipt = this.fields.searchIpt.val();
		ipt=ipt.replace(/\./g,"\\.");
		var reg = new RegExp(ipt),
			rows = [];
		for(var i=0;i<this.items.length;i++){
			var item = this.items[i];
			if(reg.test(item.ip) || reg.test(item.showName)){
				rows.push(item);
			}
		}
		this.leftGrid.datagrid("loadData",rows);
	},
	setRightGridValue:function(rows){
		var items = this.rightGrid.datagrid("getRows");
		var map = {},newrows=[];
		for(var i=0;i<items.length;i++){
			var item = items[i];
			map[item.id]=true;
			newrows.push(item);
		}
		for(var i=0;i<rows.length;i++){
			var item = rows[i];
			if(!map[item.id]){
				newrows.push(item);
			}
		}
		this.rightGrid.datagrid("loadData",newrows);
	},
	getValue:function(){
		var items = this.rightGrid.datagrid("getRows");
		var chosedIds=[];
		for(var i=0;i<items.length;i++){
			chosedIds.push(items[i].instanceId);
		}
		var core = this.root.find("[name=core]:checked");
		if(core.length==1){
			return {
				coreInstanceId:core.data("instanceid"),
				ids:chosedIds
			};
		}else{
			alert("请选择核心设备");
			throw "未选择核心设备";
		}
	},
	setValue:function(value){
		if(!value) return ;
		var rows = this.leftGrid.datagrid("getRows");
		var map = {};
		for(var i=0;i<rows.length;i++){
			map[rows[i].id]=rows[i];
		}
		var rightRows = [];
		for(var i=0;i<value.ids.length;i++){
			var id = value.ids[i];
			if(map[id]){
				rightRows.push(map[id]);
			}
		}
		this.coreInstanceId=value.coreInstanceId;
		this.rightGrid.datagrid("loadData",rightRows);
	}
};