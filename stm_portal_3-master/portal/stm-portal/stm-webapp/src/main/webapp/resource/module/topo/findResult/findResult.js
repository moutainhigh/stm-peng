function FindResultDia(args){
	this.args=$.extend({
		show:false,
		onLoad:function(){}
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/findResult/findResult.html"),
		type:"get",
		success:function(html){
			ctx.init(html);
		},
		dataType:"html"
	});
};
FindResultDia.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.root.dialog({
			title:"发现结果",
			width:800,
			height:500,
			onClose:function(){}
		});
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var $tmp = $(dom);
			ctx.fields[$tmp.attr("data-field")]=$tmp;
		});
		//初始化按钮
		this.fields.resBtn.linkbutton("RenderLB");
		this.fields.topoBtn.linkbutton("RenderLB");
		this.fields.cancelBtn.linkbutton("RenderLB");
		this.fields.backBtn.linkbutton("RenderLB");
		this.fields.closeBtn.linkbutton("RenderLB");
		this.fields.resBtn.hide();
		this.fields.topoBtn.hide();
		this.fields.closeBtn.hide();
		this.setValue({});
		this.regEvent();
		if(!this.args.show){
			this.hide();
		}
		this.args.onLoad.call(this);
	},
	show:function(){
		this.root.dialog("open");
	},
	hide:function(){
		this.root.dialog("close");
	},
	cancel:function(){
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/find/cancel.htm"),
			dataType:"json",
			success:function(info){
				if(info.code==0){
					alert("取消成功,正在进行后续处理,请稍后...","info");
					ctx.fields.findTitile.text("取消成功,正在进行后续处理,请稍后...");
					ctx.root.dialog("destory");
				}else{
					alert("取消失败","danger");
				}
			}
		});
	},
	_clearTimeId:function(){
		if(this.timeId){
			clearInterval(this.timeId);
			this.timeId=null;
		}
		if(this.timeTakeId){
			clearInterval(this.timeTakeId);
			this.timeTakeId=null;
		}
		eve("topo.statusbar.removeItem",this,"拓扑发现正在进行");
	},
	//后台运行
	backgroundRun:function(){
		var ctx = this;
		eve("topo.statusbar.additem",this,"拓扑发现正在进行",function(){
			ctx.root.dialog("open");
		});
	},
	_refreshResult:function(){
		var ctx = this;
		$.post(oc.resource.getUrl("topo/find/result.htm"),{
			index:this.index||0
		},function(msg){
			if(!msg.isRunning){
				if(!msg.isError){
					ctx.fields.findTitile.text("拓扑发现完成");
				}else{//如果是异常中断，请求重连
					ctx.fields.findTitile.text("拓扑发现异常中断");
				}
				ctx._clearTimeId();
				ctx.show();
				ctx.fields.cancelBtn.hide();
				ctx.fields.backBtn.hide();
				ctx.fields.topoBtn.show();
				ctx.fields.resBtn.show();
				ctx.fields.title.addClass("topo_find_over");
			}
			if(msg.isCanceled){
				ctx.fields.findTitile.text("取消成功,正在进行后续处理,请稍后...");
			}
			ctx.secondPassed=msg.secondPassed;
			ctx._refreshTimeTake();
			ctx.index=msg.index;
			ctx.setValue(msg);
			oc.topo.topoIsRunning=msg.isRunning;
		},"json");
	},
	isShow:function(){
		var retn = true,pts=this.root.parents();
		for(var i=0;i<pts.length;++i){
			var pt = pts[i];
			var dis = $(pt).css("display");
			if(dis=="none"){
				retn = false;
				break;
			}
		}
		return retn;
	},
	_refreshTimeTake:function(){
		var h=Math.floor(this.secondPassed/3600),_h=this.secondPassed%3600;
		var m=Math.floor(_h/60),_m=_h%(60);
		var s=_m;
		this.fields.timeTake.html("已用时"+(h<10?"0"+h:h)+":"+(m<10?"0"+m:m)+":"+(s<10?"0"+s:s));
	},
	regEvent:function(){
		var ctx = this;
		//进入资源管理按钮
		this.fields.resBtn.on("click",function(){
			ctx._clearTimeId();
			ctx.root.dialog("close");
			oc.topo.util.jumpTo(101);
		});
		//进入拓扑管理按钮
		this.fields.topoBtn.on("click",function(){
			ctx._clearTimeId();
			ctx.root.dialog("close");
			oc.topo.util.jumpTo(5);
		});
		//取消按钮
		this.fields.cancelBtn.on("click",function(){
			$.messager.confirm("温馨提示", "你确定要取消吗？", function(r){
				if (r){
					//拓扑发现取消之后，关闭窗口
					ctx.root.dialog("close");
					ctx.cancel();
				}
			});
		});
		//后台运行按钮
		this.fields.backBtn.on("click",function(){
			ctx.backgroundRun();
			ctx.root.dialog("close");
		});
		//关闭按钮
		this.fields.closeBtn.on("click",function(){
			ctx.root.dialog("close");
		});
	},
	start:function(){
		var ctx = this;
		this._clearTimeId();
		this.reset();
		var tasks = oc.index.indexLayout.data("tasks");
		this.timeId=setInterval(function(){
			ctx._refreshResult();
		},5000);
		this.timeTakeId = setInterval(function(){
			ctx.secondPassed+=1;
			ctx._refreshTimeTake();
		},1000);
		if(tasks && tasks.length > 0){
			oc.index.indexLayout.data("tasks").push(this.timeId);
			oc.index.indexLayout.data("tasks").push(this.timeTakeId);
		}else{
			tasks = new Array();
			tasks.push(this.timeId);
			tasks.push(this.timeTakeId);
			oc.index.indexLayout.data("tasks", tasks);
		}
		this._refreshResult();
	},
	setValue:function(obj){
		if(obj){
			//清空计数显示
			$.each(this.fields,function(key,$dom){
				if($dom.hasClass("item")){
					$dom.text(obj[key]||0);
				}
			});
            /*BUG #38251 拓扑管理：未做拓扑发现前，点击【拓扑历史发现结果】按钮，显示：拓扑正在发现 huangping 2017/06/29 start*/
			if(obj.msgs){
                this.fields.findTitile.text("正在发现");
				this.setInfo(obj.msgs,obj.isRunning);
            } else {
                this.fields.findTitile.text("无发现结果");
			}
            /*BUG #38251 拓扑管理：未做拓扑发现前，点击【拓扑历史发现结果】按钮，显示：拓扑正在发现 huangping 2017/06/29 end*/
			//授权信息
			if(obj.hardUnauth||obj.softUnauth){
				this.fields.unAuthBox.removeClass("hide");
				if(obj.softUnauth == 0){
					this.fields.soft.addClass("hide");
				}else{
					this.fields.softUnauth.text(obj.softUnauth);
				}
				if(obj.hardUnauth == 0){
					this.fields.hard.addClass("hide");
				}else{
					this.fields.hardUnauth.text(obj.hardUnauth);
				}
			}else{
				this.fields.unAuthBox.addClass("hide");
			}
		}else{
			this.fields.findTitile.text("拓扑发现未开始");
			this.fields.title.addClass("topo_find_over");
			this.fields.cancelBtn.hide();
			this.fields.backBtn.hide();
			this.fields.topoBtn.show();
			this.fields.resBtn.show();
			this.fields.closeBtn.hide();
			this.fields.timeTake.text("未开始");
		}
	},
	reset:function(){
		this.index=0;
		this.fields.findBody.html("");
		this.fields.findTitile.text("正在发现");
		this.fields.cancelBtn.show();
		this.fields.backBtn.show();
		this.fields.topoBtn.hide();
		this.fields.resBtn.hide();
		this.fields.title.removeClass("topo_find_over");
	},
	//设置发现信息
	setInfo:function(msgs,isRunning){
		for(var i=0;i<msgs.length;++i){
			var msg = msgs[i];
			var div = $("<div></div>");
			div.text(msg.msg);
			div.addClass("findData-body-line");
			this.fields.findBody.append(div);
		}
		if(!isRunning){
			this.fields.title.addClass("topo_find_over");
			this.fields.findTitile.text("拓扑发现完成");
		}
	}
};