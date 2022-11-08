function TopoRecycleStation(args){
	this.args=$.extend({
		title:"回收站",
		dialog:true,
		width:600,height:400,
		onLoad:function(){}
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoRecycleStation.html"),
		success:function(htl){
			ctx.init(htl);
			ctx.args.onLoad.call(ctx);
		},
		dataType:"html",
		type:"get"
	});
};
TopoRecycleStation.prototype={
	init:function(htl){
		this.root=$(htl);
		this.scope={};
		with(this){
			root.find("[data-field]").each(function(idx,dom){
				var tmp=$(dom);
				scope[tmp.attr("data-field")]=tmp;
			});
			if(args.dialog){
				root.dialog({
					title:args.title,
					width:args.width,
					height:args.height,
					onClose:function(){
						eve("topo.refresh");
					}
				});
			}
			//恢复列表
			scope.recoverList.datagrid({
				loadFilter: function(d){
					return {total:d.length,rows:d};
				},
				pagination:false,
				idField:"id",
				columns:[[{
					field:"id",checkbox:true
				},{
					field:"showName",title:"名称",width:160
				},{
					field:"ip",title:"IP地址",width:136,
				},{
					field:"typeName",title:"类型",width:117,
				},{
					field:"topoName",title:"所属拓扑",width:136,
				}]]
			});
			//按钮
			scope.recoverBtn.linkbutton("RenderLB");
			scope.delBtn.linkbutton("RenderLB");
			//初始化事件
			regEvent();
			//初始化数据
			refresh();
		}
	},
	//刷新数据
	refresh:function(){
		var scope=this.scope;
		$.post(oc.resource.getUrl("topo/recycle/list.htm"),function(result){
			if(result.status==200){
				scope.recoverList.datagrid("loadData",result.items);
			}else{
				alert(result.msg,"warning");
			}
		},"json");
	},
	regEvent:function(){
		with(this){
			//恢复按钮
			scope.recoverBtn.on("click",function(){
				var items = getValue();
				if(items&&items.length>0){
					recover();
				}else{
					$.messager.alert('警告','至少选择一条要恢复的信息','warning');
				}
				
			});
			//删除按钮
			scope.delBtn.on("click",function(){
				var items = getValue();
				if(items&&items.length>0){
					$.messager.confirm("警告","从回收站删除的资源不可恢复，确定要删除?",function(r){
						if(r){
							del();
						}
					});
				}else{
					$.messager.alert('警告','至少选择一条要删除的信息','warning');
				}
			});
		}
	},
	getValue:function(){
		return this.scope.recoverList.datagrid("getSelections");
	},
	del:function(){
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/recycle/del.htm"),
			data:{
				ids:this.getValue().map(function(item){
					return item.id;
				}).join(",")
			},
			success:function(result){
				if(result.status==200){
					alert(result.msg||"删除成功");
					ctx.refresh();
				}else{
					alert(result.msg||"删除失败","warning");
				}
			}
		});
	},
	recover:function(){
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/recycle/recover.htm"),
			data:{
				ids:this.getValue().map(function(item){
					return item.id;
				}).join(",")
			},
			success:function(result){
				if(result.status==200){
					alert(result.msg||"恢复成功");
					ctx.refresh();
				}else{
					alert(result.msg||"恢复失败","warning");
				}
			}
		});
	}
};