function NewSubTopoDia(args){
	var ctx = this;
	this.args=$.extend({
		data:null,
		topoId:0,
		dialog:true,
		mode:"new",//edit,new两种模式,
		load:null,
		loaded:null
	},args);
	if(oc.topo.topo){
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/contextMenu/NewSubTopoDia.html"),
			type:"get",
			dataType:"html",
			success:function(html){
				ctx.root = $(html);
				//搜索域
				ctx.fields={};
				ctx.root.find("[data-field]").each(function(idx,dom){
					var tmp = $(dom);
					ctx.fields[tmp.attr("data-field")]=tmp;
				});
				if(ctx.args.load){
					ctx.args.load(ctx);
				}
				ctx.init();
			}
		});
	}else{
		throw "还未初始化拓扑视图";
	}
};
NewSubTopoDia.prototype={
	init:function(){
		var ctx = this;
		if(this.args.dialog){
			this.root.dialog({
				title:this.args.mode=="new"?"新建子拓扑":"编辑子拓扑",
				width:800,height:400,
				buttons:[{
					iconCls:"ico-ok",
					text:"确定",handler:function(){
						ctx.save();
					}
				},{
					iconCls:"ico-cancel",
					text:"取消",handler:function(){
						ctx.root.dialog("close");
					}
				}]
			});
		}
		//初始化表格
		this.fields.gridPanel.datagrid({
			loadFilter: function(d){
				return {total:d.length,rows:d};
			},
			pagination:false,
			idField:"ip",
			columns:[[{
				field:"id",checkbox:true
			},{
				field:"showName",title:"名称",width:80
			},{
				field:"ip",title:"IP",width:110,
			},{
				field:"oid",title:"OID",width:180
			},{
				field:"typeName",title:"类型",width:140
			}]]
		});
		//有自定义数据
		if(this.args.data){
			this.fields.gridPanel.datagrid("loadData",this.args.data);
			this.fields.gridPanel.datagrid("selectAll");
		}else{
			if(this.args.mode=="edit"){
				oc.util.ajax({
					url:oc.resource.getUrl("topo/0/topoinfo.htm"),
					success:function(info){
						ctx.idRelation={};
						for(var i=0;i<info.devices.length;i++){
							var dev = info.devices[i];
							ctx.idRelation[dev.id]=true;
						}
						ctx.fields.gridPanel.datagrid("loadData",info.devices);
						oc.util.ajax({
							url:oc.resource.getUrl("topo/"+ctx.args.topoId+"/topoinfo.htm"),
							success:function(_info){
								//保存参照关系
								ctx.realation={};
								//选中当前拓扑的项
								for(var i=0;i<_info.devices.length;++i){
									var _item = _info.devices[i];
									ctx.realation[_item.ip]=_item.id;
									if(!ctx.idRelation[_item.id]){
										ctx.fields.gridPanel.datagrid("appendRow",_item);
									}
									ctx.fields.gridPanel.datagrid("selectRecord",_item.ip);
								}
								ctx.fields.name.val(_info.name);
							},
							type:"get",
							dataType:"json"
						});
					}
				});
			}else{
				oc.util.ajax({
					url:oc.resource.getUrl("topo/"+ctx.args.topoId+"/topoinfo.htm"),
					success:function(info){
						ctx.fields.gridPanel.datagrid("loadData",info.devices);
						ctx.fields.gridPanel.datagrid("selectAll");
						if(ctx.args.mode=="edit"){
							//如果是编辑子拓扑模式，将名称设置进去
							ctx.fields.name.val(info.name);
						}
					},
					type:"get",
					dataType:"json"
				});
			}
		}
		
		//初始化表单
		this.fields.name.validatebox({
			required:true
		});
	},
	//取值
	getValue:function(){
		var ctx = this;
		if(this.fields.name.validatebox("isValid")){
			var retn = {
				name:this.fields.name.val(),
				rows:this.fields.gridPanel.datagrid("getSelections"),
				toAdd:[],
				toDelete:[]
			};
			//检查上联设备
			/*for(var i=0;i<retn.rows.length;i++){
				var row = retn.rows[i];
				if(row){
					var updev = oc.topo.topo.checkCoreDevice(row);
					if(updev){
						retn.toAdd.push(updev.id);
					}
				}
			}*/
			if(this.args.mode=="new"){
				$.each(retn.rows,function(idx,row){
					retn.toAdd.push(row.id);
				});
			}else if(this.args.mode=="edit"){
				if(this.realation){
					var tmpRelation={};
					$.each(retn.rows,function(idx,row){
						if(!ctx.realation[row.ip]){
							retn.toAdd.push(row.id);
						}
						tmpRelation[row.ip]=row;
					});
					for(var key in ctx.realation){
						var id = ctx.realation[key];
						if(!tmpRelation[key]){
							retn.toDelete.push(id);
						}
					}
				}
			}
			return retn;
		}else{
			this.fields.name.focus();
		}
		return null;
	},
	//确定
	save:function(callBack){
		var ctx = this;
		var v=this.getValue();
		if(v){
			if(this.args.mode=="edit"){
				//先保存当前编辑
				eve("topo.save",ctx,function(){
					//更新编辑拓扑
					oc.util.ajax({
						data:{
							name:v.name,
							toDelete:v.toDelete.join(","),
							toAdd:v.toAdd.join(","),
							id:ctx.args.topoId
						},
						url:oc.resource.getUrl("topo/updateSubTopo.htm"),
						success:function(msg){
							ctx.onsuccess(msg);
							if(callBack){
								callBack(msg);
							}
							if(ctx.args.dialog){
								ctx.root.dialog("close");
							}
							//发布添加子拓扑完成事件
							eve("oc.topo.addSubTopo.finished");
							eve("topo.loadsubtopo",ctx,ctx.args.topoId);
						},
						dataType:"text"
					});
				});
			}else{//新建拓扑
				oc.util.ajax({
					data:{
						name:v.name,
						toAdd:v.toAdd.join(",").replace(new RegExp('node',"g"),'')
					},
					url:oc.resource.getUrl("topo/"+ctx.args.topoId+"/addSubTopo.htm"),
					success:function(data){
						if(data == 'InvalidName') {
							alert('子拓扑名称重复.');
						} else {
							ctx.onsuccess(data);
							if(callBack){
								callBack(data);
							}
							if(ctx.args.dialog){
								ctx.root.dialog("close");
							}
							//发布添加子拓扑完成事件
							eve("oc.topo.addSubTopo.finished");
						}
					},
					dataType:"text"
				});
			}
		}
	},
	//注册事件
	on:function(type,callBack){
		this["on"+type]=callBack;
	},
	//成功事件
	onsuccess:function(/*子拓扑的id*/id){},
	//取消
	cancel:function(){
	}
};