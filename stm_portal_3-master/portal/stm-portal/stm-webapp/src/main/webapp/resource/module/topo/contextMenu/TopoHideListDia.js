function TopoHideListDia(args){
	var ctx = this;
	this.args = $.extend({
		data:[]
	},args);
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoHideListDia.html"),
		type:"get",
		dataType:"html",
		success:function(html){
			ctx.init(html);
		}
	});
};
TopoHideListDia.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.root.dialog({
			width:684,height:304,
			title:"隐藏的资源列表",
			buttons:[{
				text:"显示",handler:function(){
					if(ctx.onshow){
						ctx.onshow(ctx.getValue());
					}
					ctx.root.dialog("close");
				}
			},{
				text:"关闭",handler:function(){
					ctx.root.dialog("close");
				}
			}]
		});
		this.fields = {};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//初始化表格
//		this.fields.grid.datagrid({
//			loadFilter: function(d){
//				if(d instanceof Array){
//					return {total:d.length,rows:d};
//				}else{
//					return {total:0,rows:[]};
//				}
//			},
//			data:this.args.data,
//			align:"center",
//			halign:"center",
//			pagination:false,	
//			fit : true,
//			fitColumns:false,
//			columns:[[{
//				field:"id",checkbox:true
//			},{
//				field:"showName",title:"名称",width:150
//			},{
//				field:"ip",title:"IP",width:120,
//			},{
//				field:"oid",title:"OID",width:200
//			},{
//				field:"typeName",title:"类型",width:120
//			}]]
//		});
        oc.ui.datagrid({
            selector: this.fields.grid,
            //fitColumns:true,
            pagination: false,
			columns:[[{
				field:"id",checkbox:true
			},{
				field:"showName",title:"名称",width:150
			},{
				field:"ip",title:"IP",width:120,
			},{
				field:"oid",title:"OID",width:200
			},{
				field:"typeName",title:"类型",width:120
			}]],
			loadFilter: function(d){
				if(d instanceof Array){
					return {total:d.length,rows:d};
				}else{
					return {total:0,rows:[]};
				}
			},
			data:this.args.data
        });
	},
	getValue:function(){
		return this.fields.grid.datagrid("getSelections");
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	}
};