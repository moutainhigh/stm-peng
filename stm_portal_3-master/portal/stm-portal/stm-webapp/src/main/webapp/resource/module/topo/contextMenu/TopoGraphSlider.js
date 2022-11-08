/**
 * 拓扑框选改变图元大小
 */
function TopoGraphSlider(resizeCallback,recoveryCallback){
	this.resizeCallback=resizeCallback;
	this.recoveryCallback=recoveryCallback;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoGraphSlider.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"post",
		dataType:"html"
	});
};
TopoGraphSlider.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.root.dialog({
			title:"缩放比例",
//			top:0,
			buttons:[{
				text:"保存",handler:function(){
					ctx.save();
				}
			},{
				text:"取消",handler:function(){
					ctx.root.dialog("close");
				}
			}],
			onBeforeClose:function(){
				ctx.recoveryCallback();		//恢复原来状态
			}
		});
		//搜索域对象
		ctx.searchFields();
		//初始化UI
		ctx.initUI();
	},
	//搜索dom对象 
	searchFields:function(){
		var ctx = this;
		ctx.fields={};
		ctx.root.find("[data-field]").each(function(index,dom){
			var tmp=$(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
	},
	initUI:function(){
		var ctx = this;
		this.fields["topoSlider"].slider({
		    mode: 'h',
		    showTip:true,
		    value:100,
		    max:200,
//		    rule:[0,'|',25,'|',50,'|',75,'|',100,'|',125,'|',150,'|',175,'|',200],
		    tipFormatter: function(value){    
		        return value + '%';
		    },
		    //改变大小后，重新设置图元参数
		    onChange:function(newValue,oldValue){
		    	ctx.resizeCallback(newValue/100,false);
		    }
		});  
	},
	//保存设置值
	save:function(){
		var ctx = this;
		var value = ctx.fields["topoSlider"].slider("getValue");
		ctx.resizeCallback(value/100,true);
		this.root.dialog("close",true);
	}
};