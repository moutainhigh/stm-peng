//修改链路阈值-带宽利用率
function EditBindWidthUtilDia(instanceIds){
	var ctx = this;
	this.instanceIds = instanceIds;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/instanceTable/editBandWidthUtil.html"),
		type:"post",
		dataType:"html",
		success:function(html){
			ctx.init(html);
		}
	});
};
EditBindWidthUtilDia.prototype={
	init:function(html){
		var ctx = this;
		this.dia=$(html);
		this.dia.dialog({
			width:700,height:200,
			title:"指标设置",
			buttons:[{
				text:"保存",handler:function(){
					ctx.save();
				}
			},{
				text:"取消",handler:function(){
					ctx.cancel();
				}
			}]
		});
		//搜索域变量
		this.fields={};
		this.dia.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		
		this.initData();
	},
	//初始化数据
	initData:function(){
		var ctx = this;
		ctx.setValue();
	},
	getValue:function(){
		//带宽阈值
		var band = this.fields.band.getTargetValue();
		return {
			instanceIds:this.instanceIds,
			band:{
				min:band[1],
				max:band[2]
			},
		};
	},
	setValue:function(v){
		//带宽利用率阈值
		this.fields.band.target(["%",90,95,"min","max"],false);

	},
	save:function(){
		var ctx = this;
		var value = this.getValue();
		oc.util.ajax({
			url:oc.resource.getUrl('topo/link/updateBandWidthUtil.htm'),
			type: 'POST',
			dataType: "json", 
			data:{info:JSON.stringify(value)},
			successMsg:null,
			success:function(data){
				alert(data.data)
				ctx.dia.dialog("close");
			}
		});
	},
	cancel:function(){
		this.dia.dialog("close");
	}
};