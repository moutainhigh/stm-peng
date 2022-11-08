function EditLinkDia(link){
	this.link = link;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/EditLink.html"),
		type:"post",
		dataType:"html",
		success:function(html){
			ctx.initData(html);
		}
	});
};
EditLinkDia.prototype={
	init:function(html){
		var ctx = this;
		this.dia=$(html);
		this.dia.dialog({
			width:850,height:400,
			title:"编辑链路",
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
		this.fields.resIf.combobox({
			valueField:"instanceId",
			textField:"name",
			width:225
		});
		this.fields.desIf.combobox({
			valueField:"instanceId",
			textField:"name",
			width:225
		});
		this.fields.valIf.combobox({
			valueField:"instanceId",
			textField:"name",
			width:225
		});
		//初始化下行方向
		this.fields.downIf.combobox({
			valueField:"instanceId",
			textField:"ip",
			width:225
		});
		//带宽只读
		this.fields.bandWidth.css("cursor","");
		this.fields.bandWidth.on("focus",function(){
			$(this).blur();
		});
		this.regEvent();
	},
	//初始化数据
	initData:function(html){
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/link/"+this.link.d.instanceId+".htm"),
			success:function(result){
				if(result.state==700){
					alert(result.msg,"warning");
				}else{
					ctx.init(html);
					ctx.setValue(result);
				}
			}
		});
	},
	regEvent:function(){
		var ctx = this;
		//取值接口改变
		this.fields.valIf.combobox({
			onSelect:function(record){
				ctx._changeBandWidth();
				ctx._changeDirection();
			}
		});
		//监听源接口选择变化
		this.fields.resIf.combobox({
			onSelect:function(record){
				ctx.fields.resIf.record=record;
				ctx._changeValIf();
			}
		});
		//监听目的接口选择变化
		this.fields.desIf.combobox({
			onSelect:function(record){
				ctx.fields.desIf.record=record;
				ctx._changeValIf();
			}
		});
		//监听阈值改变
		this.fields.band.on("blur","input",function(){
			ctx._changeFlow();
		});
		//备注字数限制80字符
		this.fields.note.on("keydown",function(e){
			var note = $(this);
			//获取当前输入框的值
			var len = 0,val = "";
			try{
				val=note.val();
				len = val.length;
			}catch(e){
				len = 0;
			}
			//判断len是否大于40
			if(len>=40){
				alert("输入字符在40以内！","warning");
				note.val(val.substring(0,40));
			}
		});
	},
	//改变流量阈值
	_changeFlow:function(){
		var ctx = this;
		var value = this.fields.bandWidth.val();
		if(value){
			oc.util.ajax({
				url:oc.resource.getUrl("topo/link/caculateFlow.htm"),
				data:{
					base:value,
					max:ctx.fields.band.find(".rightValue_input").val(),
					min:ctx.fields.band.find(".leftValue_input").val()
				},
				success:function(result){
					if(result.state=200){
						ctx.fields.flow.target(["Mbps",result.min,result.max,"min","max"],true);
					}else{
						alert(result.msg,"warning");
					}
				}
			});
		}
	},
	//改变下行方向
	_changeDirection:function(){
		var valInstId = this.fields.valIf.combobox("getValue");
		var resInstId = this.fields.resIf.combobox("getValue"),
			desInstId = this.fields.desIf.combobox("getValue");
		if(valInstId==resInstId){
			this.fields.downIf.combobox("setValue",this.value.ifAttr.destSubInstId);
		}else{
			this.fields.downIf.combobox("setValue",this.value.ifAttr.srcSubInstId);
		}
	},
	//改变带宽
	_changeBandWidth:function(){
		var ctx = this;
		var instanceId = this.fields.valIf.combobox("getValue");
		if(instanceId){
			//获取当前取值接口的带宽
			oc.util.ajax({
				url:oc.resource.getUrl("topo/link/bandWidth.htm"),
				data:{
					instanceId:instanceId,
					unit:"Mbps"
				},
				success:function(result){
					if(result.state==200){
						ctx.fields.bandWidth.val(result.bandWidth);
						ctx._changeFlow();
					}else{
						alert(result.msg,"warning");
					}
				}
			});
		}
	},
	//改变取值接口
	_changeValIf:function(){
		//获取源接口和目的接口当前选择的项
		var resRecord = this.fields.resIf.record;
		var desRecord = this.fields.desIf.record;
		var data = this.fields.valIf.combobox("getData");
		var currentValue = this.fields.valIf.combobox("getValue");
		var changeValue=true;
		if(resRecord){
			data[0]={
				instanceId:resRecord.instanceId,
				name:resRecord.name,
				index:resRecord.ifIndex
			};
			if(changeValue && resRecord.instanceId==currentValue){
				changeValue=false;
			}
		}
		if(desRecord){
			data[1]={
				instanceId:desRecord.instanceId,
				name:desRecord.name,
				index:desRecord.ifIndex
			};
			if(changeValue && desRecord.instanceId==currentValue){
				changeValue=false;
			}
		}
		this.fields.valIf.combobox("loadData",data);
		//是否需要重新设置值
		if(changeValue){
			this.fields.valIf.combobox("setValue",data[0].instanceId);
			this._changeBandWidth();
		}
	},
	getDirection:function(){
		var dowInstId = this.fields.downIf.combobox("getValue");
		var resInstId = this.fields.resIf.combobox("getValue");
		if(dowInstId==resInstId){
			return false;
		}else{
			return true;
		}
	},
	getValue:function(){
		//选择的项
		var resRecord = this.fields.resIf.record;
		var desRecord = this.fields.desIf.record;
		//带宽阈值
		var band = this.fields.band.getTargetValue();
		//总流量阈值
		var flow = this.fields.flow.getTargetValue();
		var availabilityAlarm = this.fields.availabilityAlarm.is(":checked");
		var bandAlarm = this.fields.bandAlarm.is(":checked");
		var flowAlarm = this.fields.flowAlarm.is(":checked");
		var retn = {
			instanceId:this.instanceId,
			srcSubInstId:this.fields.resIf.combobox("getValue"),
			destSubInstId:this.fields.desIf.combobox("getValue"),
			collSubInstId:this.fields.valIf.combobox("getValue"),
			direction:this.getDirection(),
			note:this.fields.note.val(),
			availabilityAlarm:availabilityAlarm,
			band:{
				min:band[1],
				max:band[2],
				alarm:bandAlarm
			},
			flow:{
				min:flow[1]*1048576,
				max:flow[2]*1048576,
				alarm:flowAlarm
			},
			note:this.fields.note.val()
		};
		if(resRecord){
			retn.srcIfIndex=resRecord.ifIndex;
			retn.srcIfName=resRecord.name;
		}
		if(desRecord){
			retn.desIfIndex=desRecord.ifIndex;
			retn.desIfName=desRecord.name;
		}
		return retn;
	},
	setValue:function(v){
		this.value=v;
		//带宽利用率阈值
		if(v.bandThreshold && v.bandThreshold.Minor && v.bandThreshold.Major){
			var hold = v.bandThreshold;
			this.fields.band.target(["%",parseFloat(hold.Minor.thresholdValue),parseFloat(hold.Major.thresholdValue),"min","max"],false);
			//带宽利用率改变后，总流量做相应的调整
		}else{
			this.fields.band.addClass("line-disabled");
			this.fields.band.css("height","5px");
		}
		//设置接口
		//源接口
		this.fields.resIf.combobox("loadData",v.srcIfs);
		this.fields.resIf.combobox("setValue",v.ifAttr.srcSubInstId);
		//目的接口
		this.fields.desIf.combobox("loadData",v.desIfs);
		this.fields.desIf.combobox("setValue",v.ifAttr.destSubInstId);
		//取值接口
		this.fields.valIf.combobox("loadData",[{
			instanceId:v.ifAttr.srcSubInstId,
			name:v.ifAttr.srcIfName,
			index:v.ifAttr.srcIfIndex
		},{
			instanceId:v.ifAttr.destSubInstId,
			name:v.ifAttr.destIfName,
			index:v.ifAttr.destIfIndex
		}]);
		if(!v.ifAttr.collSubInstId){
			v.ifAttr.collSubInstId=v.ifAttr.srcSubInstId||v.ifAttr.destSubInstId
		}
		this.fields.valIf.combobox("setValue",v.ifAttr.collSubInstId);
		//下行方向接口
		this.fields.downIf.combobox("loadData",[{
			ip:v.resIp,
			instanceId:v.ifAttr.srcSubInstId
		},{
			ip:v.desIp,
			instanceId:v.ifAttr.destSubInstId
		}]);
		if(v.direction){
			this.fields.downIf.combobox("setValue",v.ifAttr.destSubInstId);
		}else if(v.direction==undefined){
			//参考取值接口的方向
			var colId = v.ifAttr.collSubInstId,
				desId = v.ifAttr.destSubInstId,
				srcId = v.ifAttr.srcSubInstId;
			if(colId==srcId){
				this.fields.downIf.combobox("setValue",v.ifAttr.destSubInstId);
			}else{
				this.fields.downIf.combobox("setValue",v.ifAttr.srcSubInstId);
			}
		}else{
			this.fields.downIf.combobox("setValue",v.ifAttr.srcSubInstId);
		}
		this.fields.resIp.text(v.resIp||"");
		this.fields.desIp.text(v.desIp||"");
		//链路资源实例id
		this.instanceId=v.instanceId;
		//链路图元id
		this.id=v.id;
		//备注
		this.fields.note.val(v.ifAttr.note);
		//带宽利用率
		this.fields.bandWidth.val(v.bandWidth);
		this._changeFlow();
		//告警
		this.fields.availabilityAlarm.attr("checked",v.availabilityAlarm);
		this.fields.bandAlarm.attr("checked",v.bandAlarm);
		this.fields.flowAlarm.attr("checked",v.flowAlarm);
	},
	save:function(){
		var ctx = this;
		var value = ctx.getValue();
		if(null==value.srcSubInstId || ""==value.srcSubInstId || null == value.destSubInstId || ""==value.destSubInstId){
			alert("源接口和目的接口不能为空");
		}else{

			oc.util.ajax({
				url:oc.resource.getUrl("topo/link/updateLink.htm"),
				data:{
					info:JSON.stringify(value)
				},
				success:function(info){
					alert(info.data);
					//从链路编辑过来，保存后刷新表格
					if(ctx.link.grid){
						ctx.link.grid.reLoad();
					}else if(ctx.link.isRefreshTopo){
						eve("topo.refresh");
					}
					ctx.dia.dialog("close");
				},
				dataType:"json"
			});
		}
	},
	cancel:function(){
		this.dia.dialog("close");
	},
	//刷新表格数据
	datagridReload:function(grid){
		grid.datagrid("reload");
	}
};