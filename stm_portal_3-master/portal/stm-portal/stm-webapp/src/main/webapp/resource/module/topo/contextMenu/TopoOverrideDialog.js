function TopoOverrideDialog(args){
	this.args=$.extend({
		title:"冲突提示",
		items:[],
		onLoad:function(){},
		onFinished:function(){}
	},args);
	var ctx = this;
	if(this.args.items.length<=0){
		this.args.onFinished.call(this);
	}else{
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoOverrideDialog.html"),
			type:"get",
			dataType:"html",
			success:function(htl){
				ctx.init(htl);
				ctx.args.onLoad.call(ctx);
			}
		});
	}
};
TopoOverrideDialog.prototype={
	init:function(htl){
		this.root=$(htl);
		this.root.dialog({
			width:460,height:208,title:this.args.title
		});
		var ctx = this;
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.regEvent();
		this.setValue();
	},
	regEvent:function(){
		var ctx = this;
		with(this.fields){
			replaceBtn.on("click",function(){
				ctx.replace();
			});
			ignoreBtn.on("click",function(){
				ctx.ignore();
			});
		}
	},
	setValue:function(){
		var item=null;
		this.currentItem = item = this.args.items.pop();
		var len = this.args.items.length;
		with(this.fields){
			if(item && item.ip){
				ip.text(item.ip);
				count.text(len);
			}else{
				this.root.dialog("close");
				this.args.onFinished(this);
			}
		}
	},
	isSameAction:function(){
		return this.fields.flagBtn.prop("checked");
	},
	replace:function(){
		var ctx = this;
		var replaceJson = {};
		replaceJson[this.currentItem.originalId]=this.currentItem.id;
		//如果是相同动作
		if(this.isSameAction()){
			$.each(this.args.items,function(idx,item){
				replaceJson[item.originalId]=item.id;
			});
			//清空冲突列表
			this.args.items=[];
		}
		oc.util.ajax({
			url:oc.resource.getUrl("topo/clipboard/replace.htm"),
			data:{
				replaceStr:JSON.stringify(replaceJson),
				topoId:this.args.topoId,
				isMove:!!this.args.isMove
			},
			success:function(result){
				if(result.status==200){
					ctx.setValue();
					alert(result.msg||"替换成功");
				}else{
					alert(result.msg||"替换失败","warning");
				}
			}
		});
	},
	ignore:function(){
		if(this.isSameAction()){
			this.root.dialog("close");
		}else{
			this.setValue();
		}
	}
};