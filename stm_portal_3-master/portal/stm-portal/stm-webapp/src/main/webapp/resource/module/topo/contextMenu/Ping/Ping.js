function TopoPing(args,holder){
	this.holder=holder;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/Ping/Ping.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
TopoPing.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		if(this.holder){
			this.root.appendTo(this.root);
		}
		//打开对话框
		this.root.dialog({
			width:800,height:468,
			title:"Ping"
		});
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//初始化tabs
		this.fields.tabContainer.tabs({});
		//连接按钮
		this.fields.connectBtn.linkbutton("RenderLB",{});
		//发送包数目
		this.fields.packageCount.numberbox({    
		    min:0,
		    value:1,
		    suffix:"个"
		});
		//延时
		this.fields.timeout.numberbox({
			min:0,
			value:1000,
			suffix:"毫秒"
		});
		//包长度
		this.fields.packageLength.numberbox({
			min:0,
			value:0
		});
		//ip
		this.fields.host.validatebox({
			required: true,
			validType:"ip"
		});
		//form
		this.fields.cfgForm.form({ 
		    onSubmit: function(){       
		    },    
		    success:function(data){    
		        alert(data);
		    }
		});    
		this.regEvent();
	},
	//获取表单的值
	getFormValue:function(){
		if(this.fields.host.validatebox("isValid")){
			return {
				host:this.fields.host.val(),
				packageLength:this.fields.packageLength.val(),
				host:this.fields.host.val(),
				timeout:this.fields.timeout.val(),
				packageCount:this.fields.packageCount.val(),
				groupId:this.fields.groupId.val()
			};
		}
	},
	regEvent:function(){
		var ctx = this;
		//连接按钮
		this.fields.connectBtn.on("click",function(){
			ctx.fields.cfgForm.submit();
			ctx.fields.tabContainer.tabs("select","命令行");
		});
	}
};