function PortAttrDia(args){
	var ctx = this;
	oc.util.ajax({
		type:"get",
		url:oc.resource.getUrl("resource/module/topo/backboard/smallWindow/PortAttrDia.html"),
		success:function(html){
			ctx.init(html);
			ctx.onload();
		},
		dataType:"html"
	});
};
PortAttrDia.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.root.dialog({
			title:"接口操作",
			width:360,height:320,
			buttons:[{
				text:"确定",iconCls:"fa fa-check-circle",handler:function(){
					ctx.onok(ctx.getValue());
					ctx.root.dialog("close");
				}
			},{
				text:"取消",iconCls:"fa fa-times-circle",handler:function(){
					ctx.root.dialog("close");
				}
			}]
		});
		//搜索域
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//初始化方向combobox
		this.fields.angleIpt.combobox({});
		//宽度输入框
		this.fields.widthIpt.numberbox({
			min:1,value:16
		});
		//高度输入框
		this.fields.heightIpt.numberbox({
			min:1,value:16
		});
		//行数输入框
		this.fields.rowsIpt.numberbox({
			min:1,value:1
		});
		//列数输入框
		this.fields.colsIpt.numberbox({
			min:1,value:1
		});
		//索引号输入框
		this.fields.indexIpt.numberbox({
			min:0,value:0
		});
		this.regEvent();
	},
	onok:function(val){},
	onload:function(){},
	on:function(key,callBack){
		this["on"+key]=callBack;
	},
	regEvent:function(){
		
	},
	setValue:function(v){
		this.fields.widthIpt.numberbox("setValue",v.width);
		this.fields.heightIpt.numberbox("setValue",v.height);
	},
	getValue:function(){
		return {
			width:this.fields.widthIpt.val(),
			height:this.fields.heightIpt.val(),
			cols:this.fields.colsIpt.val(),
			rows:this.fields.rowsIpt.val(),
			index:this.fields.indexIpt.val(),
			angle:this.fields.angleIpt.combobox("getValue"),
			reverse:this.fields.reverseIpt.prop("checked")
		};
	}
};