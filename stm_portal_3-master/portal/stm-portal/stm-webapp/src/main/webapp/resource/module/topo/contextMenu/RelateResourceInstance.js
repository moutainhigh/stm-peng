function RelateResourceInstance(args){
	var ctx = this;
	this.args=$.extend({
		dialog:true,
		w:600,
		h:480,
		title:"关联资源实例",
		instanceId:null,
		ip:null,
		id:null,
		onConfirm:null,
		subTopoId:null,
		singleSelect:true
	},args);
	if(!this.args.id){
		throw "id or subTopoId can't be null";
		return ;
	}
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/RelateResourceInstance.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
RelateResourceInstance.prototype={
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
						if(ctx.args.onConfirm){
							ctx.args.onConfirm.call(ctx);
						}else{
							ctx.update();
						}
					}
				},{
					text:"取消",handler:function(){
						ctx.root.dialog("close");
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
			singleSelect:this.args.singleSelect,
			idField:"ip",
			onSortColumn:function(sort,order){
				if(ctx.sorter){
					ctx.sorter[order](sort);
					ctx.fields.grid.datagrid("loadData",ctx.sorter.data);
				}
			},
			columns:[[{
				field:"instanceId",checkbox:true
			},{
				field:"ip",title:"IP",width:150,sortable:true
			},{
				field:"showName",title:"名称",width:380,sortable:true
			}]]
		});
		//初始化表格数据
		oc.util.ajax({
			url:oc.resource.getUrl("topo/resource/all.htm"),
			success:function(data){
				oc.resource.loadScript("resource/module/topo/util/TopoSort.js",function(){
					ctx.sorter=new TopoSort(data);
				});
				ctx.resources=data;
				if(ctx.args.instanceId){//查找已经对应的关系
					var flag=false;
					for(var i=0;i<data.length;i++){
						var re = data[i];
						if(re.instanceId==ctx.args.instanceId){
							ctx.fields.iptext.text(re.ip);
							ctx.fields.showName.text(re.showName);
							flag=true;
							break;
						}
					}
					if(flag){
						ctx.fields.info.removeClass("hide");
					}
				}
				ctx.fields.grid.datagrid("loadData",ctx.resources);
				ctx.setChecked(ctx.preCheckeIds);
			}
		});
		this.regEvent();
	},
	setChecked:function(instIds){
		this.preCheckeIds=instIds;
		if(this.resources && this.preCheckeIds){
			var map={};
			for(var i=0;i<this.preCheckeIds.length;i++){
				map[this.preCheckeIds[i]]=true;
			}
			for(var i=0;i<this.resources.length;i++){
				var re = this.resources[i];
				if(map[re.instanceId]){
					this.fields.grid.datagrid("selectRow",i);
				}
			}
		}
	},
	search:function(){
		var fd = this.fields;
		var ip = fd.ip.val();
		if(this.resources){
			var reg = new RegExp(ip.trim(),"i");
			var result = [];
			for(var i=0;i<this.resources.length;i++){
				var re = this.resources[i];
				if(reg.test(re.ip)||reg.test(re.showName)){
					result.push(re);
				}
			}
			this.fields.grid.datagrid("loadData",result);
		}
	},
	on:function(key,cb){
		this["on"+key]=cb;
	},
	getValue:function(){
		return this.fields.grid.datagrid("getSelected");
	},
	getRows:function(){
		return this.fields.grid.datagrid("getSelections");
	},
	update:function(){
		var ctx = this;
		//获取表格选中的项
		var row = this.getValue();
		if(row){
			oc.util.ajax({
				url:oc.resource.getUrl("topo/updateResourceInstance.htm"),
				data:{
					id:ctx.args.id,
					subTopoId:ctx.args.subTopoId,
					instanceId:row.instanceId,
					ip:row.ip
				},
				success:function(result){
					if(result.code==200){
						alert(result.msg);
						ctx.root.dialog("close");
						if(ctx.onok){
							ctx.onok(result);
						}
					}else{
						alert(result.msg);
					}
				}
			});
		}else{
			alert("请在表格中选中一行","warning");
		}
	},
	regEvent:function(){
		var ctx = this;
		var fd = this.fields;
		//ip回车键
		fd.ip.on("keyup",function(e){
			if(e.keyCode==13){
				ctx.search();
			}
		});
		fd.search.on("click",function(){
			ctx.search();
		});
	}
};