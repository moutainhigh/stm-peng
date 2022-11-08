function AddCabinet(args){
	this.args=$.extend({
		title:"添加机柜",
		w:800,
		h:400,
		id:null,
		subTopoId:null,
		attr:{}
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/AddCabinet.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};
AddCabinet.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.root.dialog({
			title:this.args.title,
			width:this.args.w,
			height:this.args.h,
			buttons:[{
				text:"保存",handler:function(){
					ctx.save();
				}
			},{
				text:"取消",handler:function(){
					ctx.root.dialog("close");
				}
			}]
		});
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp=$(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.fields.grid.css({
			height:this.root.height()-38
		});
		this.fields.name.validatebox({
			required:true
		});
		this.initCabinet();
	},
	save:function(){
		var ctx = this;
		var subTopoId=parseInt(this.args.subTopoId);
		if(!isNaN(subTopoId)){
			if(!isNaN(parseInt(this.args.id))){//更新属性
				oc.util.ajax({
					url:oc.resource.getUrl("topo/other/updateCabinet.htm"),
					data:{
						id:this.args.id,
						subTopoId:this.args.subTopoId,
						attr:JSON.stringify($.extend(this.args.attr,this.getValue()))
					},
					success:function(result){
						if(result.status==200){
							if(ctx.args.onSave){
								ctx.args.onSave.call(ctx,ctx.getValue());
							}
							alert(result.msg);
						}else{
							alert(result.msg,"warning");
						}
						ctx.root.dialog("close");
					}
				});
			}else if(subTopoId>=0){//添加
				oc.util.ajax({
					url:oc.resource.getUrl("topo/other/addCabinet.htm"),
					data:{
						subTopoId:this.args.subTopoId,
						attr:JSON.stringify($.extend(this.args.attr,this.getValue()))
					},
					success:function(result){
						if(result.status==200){
							if(ctx.args.onSave){
								ctx.args.onSave.call(ctx,result);
							}
							alert(result.msg);
						}else{
							alert(result.msg,"warning");
						}
						ctx.root.dialog("close");
					}
				});
			}
		}else{
			throw "subTopoId can't be null";
		}
	},
	//初始化机柜内容
	initCabinet:function(){
		var fd = this.fields;
		var ctx = this;
		//初始化表格
		ctx.cabinetGrid = oc.ui.pickgrid({
			leftOptions:{
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				}
			},
			leftColumns:[[{
				field:"id",checkbox:true
			},{
				field:"ip",title:"IP",width:100,ellipsis:true
			},{
				field:"showName",title:"设备名称",width:150,ellipsis:true
			},{
				field:"typeName",title:"设备类型",width:90,ellipsis:true
			}]],
			rightOptions:{
				loadFilter: function(d){
					if(d instanceof Array){
						return {total:d.length,rows:d};
					}else{
						return {total:0,rows:[]};
					}
				}
			},
			rightColumns:[[{
				field:"id",checkbox:true
			},{
				field:"ip",title:"IP",width:100,ellipsis:true
			},{
				field:"showName",title:"设备名称",width:150,ellipsis:true
			},{
				field:"typeName",title:"设备类型",width:90,ellipsis:true
			}]],
			selector:fd.grid
		});
		//获取表格数据
		oc.util.ajax({
			url:oc.resource.getUrl("topo/all/resources.htm"),
			timeout:300000,
			success:function(info){
				ctx.cabinetRows=info.devices;
				oc.util.ajax({
					url:oc.resource.getUrl("topo/setting/get/topo_room_cabinet_ids.htm"),
					success:function(result){
						ctx.filterIdMap=result;
						if(ctx.args.onLoad){
							ctx.args.onLoad.call(ctx);
						}else{
							ctx.cabinetGrid.leftGrid.selector.datagrid("loadData",ctx.filterCabinetGridResult(ctx.cabinetRows));
						}
					}
				});
			}
		});
		//回车搜素
		fd.ip.on("keyup",function(e){
			if(e.keyCode==13){
				var regExp = new RegExp($(this).val(),"i");
				regExp.ignoreCase=true;
				if(ctx.cabinetRows){
					var result = [];
					$.each(ctx.cabinetRows,function(idx,row){
						if((row.ip && regExp.test(row.ip))||(row.showName && regExp.test(row.showName))){
							result.push(row);
						}
					});
					ctx.cabinetGrid.leftGrid.selector.datagrid("loadData",ctx.filterCabinetGridResult(result));
				}
			}
		});
	},
	getValue:function(){
		var rows = this.cabinetGrid.rightGrid.selector.datagrid("getRows");
		if(this.fields.name.validatebox("isValid")){
			var ids =[];
			$.each(rows,function(idx,row){
				ids.push(row.id);
			});
			return {
				rows:ids,
				dataType:"cabinet",
				text:this.fields.name.val()
			};
		}else{
			this.fields.name.focus();
			throw "can't be null";
		}
	},
	setValue:function(v){
		if(v.rows && this.cabinetRows){
			var rows = [];
			var map = {}, ctx = this;;
			$.each(this.cabinetRows,function(idx,row){
				map[row.id]=row;
			});
			$.each(v.rows,function(idx,row){
				var tmp = map[row];
				if(tmp){
					rows.push(tmp);
				}
			});
			this.cabinetGrid.rightGrid.selector.datagrid("loadData",rows);
			if(this.filterIdMap){
				$.each(rows,function(idx,row){
					if(ctx.filterIdMap[row.id]){
						delete ctx.filterIdMap[row.id];
					}
				});
			}
		}
		if(v.text){
			this.fields.name.val(v.text);
		}
		if(this.cabinetRows){
			this.cabinetGrid.leftGrid.selector.datagrid("loadData",this.filterCabinetGridResult(this.cabinetRows));
		}
	},
	filterCabinetGridResult:function(result){
		var rows = this.cabinetGrid.rightGrid.selector.datagrid("getRows"),
			ctx = this;
		var tmp={};
		$.each(rows,function(idx,row){
			tmp[row.id]=true;
		});
		var retn=[];
		if(this.filterIdMap){
			$.each(result,function(idx,row){
				if(!tmp[row.id] && !ctx.filterIdMap[row.id]){
					retn.push(row);
				}
			});
			return retn;
		}else{
			return result;
		}
	}
};