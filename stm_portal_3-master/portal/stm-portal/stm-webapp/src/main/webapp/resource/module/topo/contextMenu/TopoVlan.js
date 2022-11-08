function TopoVlan(args){
	var ctx = this;
	this.args=$.extend({
		dialog:true,
		w:600,
		h:480,
		title:"Vlan列表",
		id:null
	},args);
	if(!this.args.id){
		throw "id can't be null";
		return ;
	}
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoVlan.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
TopoVlan.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		if(this.args.dialog){
			this.root.dialog({
				title:this.args.title,
				width:this.args.w,
				height:this.args.h,
				buttons:[{
					text:"确定",handler:function(){
						ctx.root.dialog("destroy");
					}
				},{
					text:"取消",handler:function(){
						ctx.root.dialog("destroy");
					}
				}]
			});
		}
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//初始化grid
		this.fields.grid.datagrid({
			loadFilter: function(d){
				return {total:d.length,rows:d};
			},
			pagination:false,
			singleSelect:true,
			idField:"ip",
			columns:[[{
				field:"id",checkbox:true
			},{
				field:"vlanId",title:"vlanId",width:"10%"
			},{
				field:"vlanName",title:"vlan名称",width:"35%",ellipsis:true
			},{
				field:"portsName",title:"端口名称",width:"50%",ellipsis:true
			}]]
		});
		oc.util.ajax({
			url:oc.resource.getUrl("topo/vlan/getVlanForNodeBo.htm"),
			data:{
				nodeId:this.args.id
			},
			success:function(result){
				if(result.status==200){
					ctx.fields.grid.datagrid("loadData",result.vlans||[]);
				}
			}
		});
	}
};