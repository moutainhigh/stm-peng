function UpdateBaseInfoDia(args){
	this.args=$.extend({
		subTopoId:null,
		ip:null,
		name:null,
		id:null
	},args);
	/*if(!this.args.name||!this.args.ip||!this.args.id){
		throw "name,id and ip can't be null";
	}else{*/
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/contextMenu/updateBaseInfo/updateBaseInfo.html"),
			type:"get",
			success:function(html){
				ctx.init(html);
			},
			dataType:"html"
		});
	/*}*/
};
UpdateBaseInfoDia.prototype={
	init:function(html){
		this.dia=$(html);
		var ctx = this;
		this.dia.dialog({
			width:350,height:200,
			title:"修改基本信息",
			buttons:[{
				text:"确定",handler:function(){
					if(ctx.onok){
						ctx.onok(ctx.getValue());
					}
				}
			},{
				text:"取消",handler:function(){
					ctx.dia.dialog("close");
				}
			}]
		});
		//搜索绑定的域
		this.fields={};
		this.dia.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//赋值
		if(this.node){
			this.setValue({
				name:this.node.d.ip,
				manageIp:this.node.d.ip
			});
		}
		this.fields.manageIp.combobox({
			textField:"ip",
			valueField:"ip",
			validType:"ip"
		});
		//初始化数据
		this.initValue();
	},
	initValue:function(){
		var ctx = this;
		//初始化管理ip
		oc.util.ajax({
			url:oc.resource.getUrl("topo/getAllIpsForNode.htm"),
			data:{
				subTopoId:this.args.subTopoId,
				id:this.args.id
			},
			success:function(result){
				if(result.state==200){
					var data = [];
					for(var i=0;i<result.ips.length;i++){
						data.push({
							ip:result.ips[i]
						});
					}
					ctx.fields.manageIp.combobox("loadData",data);
					ctx.fields.manageIp.combobox("setValue",ctx.args.ip);
				};
			}
		});
		//初始化显示名称
		this.fields.name.val(this.args.name);
	},
	getValue:function(){
		var value={
			manageIp:this.fields.manageIp.combobox("getValue"),
			name:this.fields.name.val()
		};
		if(!value.manageIp){
			value.manageIp=this.fields.manageIp.parent().find(".textbox-text").val();
		}
		return value;
	},
	setValue:function(value){
		this.fields.manageIp.val(value.manageIp);
		this.fields.name.val(value.name);
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	}
};