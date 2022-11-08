function SubnetNodes(args){
	this.args = $.extend({
		dialog:true,
		node:null
	},args);
	if(this.args.node){
		this.init("<div><div data-field='grid'></div></div>");
	}
};
SubnetNodes.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.fields = {};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.fields.grid.datagrid({
			method:"get",
			loadFilter: function(d){
				return {total:d.length,rows:d};
			},
			idField:"ip",
			pagination:false,
			columns:[[{
				field:"id",hidden:true
			},{
				field:"showName",title:"名称",width:80
			},{
				field:"ip",title:"IP",width:110,
			},{
				field:"oid",title:"OID",width:180
			},{
				field:"typeName",title:"类型",width:140
			}]]
		});
		this.root.dialog({
			width:700,height:400,
			title:"子网-"+this.args.node.d.ip,
			buttons:[{
				text:"关闭",handler:function(){
					ctx.root.dialog("close");
				}
			}]
		});
		//加载数据
		oc.util.ajax({
			url:oc.resource.getUrl("topo/findNodesBySubnetIp.htm"),
			data:{
				ip:this.args.node.d.ip
			},
			success:function(info){
				ctx.fields.grid.datagrid("loadData",info.nodes);
			},
			dataType:"json"
		});
	}
};