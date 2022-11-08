function CreateSubTopo(args){
	this.args=$.extend({
		dialog:true,
		w:800,
		h:460,
		load:null,
		parentId:null,
		subTopoId:null,
		title:"新建子拓扑"
	},args);
	var ctx = this;
	this.moveMap={};
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/CreateSubTopo.html"),
		success:function(html){
			ctx.init(html);
			if(ctx.args.load){
				ctx.args.load.apply(ctx);
			}
		},
		type:"get",
		dataType:"html"
	});
};
CreateSubTopo.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		if(this.args.dialog){
			this.root.dialog({
				title:this.args.title,
				width:this.args.w,
				height:this.args.h,
				buttons:[{
					text:"确定",handler:function(){
						ctx.save();
					}
				},{
					text:"取消",handler:function(){
						ctx.root.dialog("close");
					}
				}]
			});
		}
		//搜索域
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp=$(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.pickgrid=oc.ui.pickgrid({
			moveBeforeEvent:function(rows,dire){
				if(dire=="left"){
					$.each(rows,function(idx,row){
						if(ctx.lastMoveItemsMap[row.ip]){
							ctx.leftData.push(row);
						}
					});
				}
				var tmpRows=[];
				if(dire=="right"){
					$.each(rows,function(idx,row){
						if(!row.isMove){
							tmpRows.push(row);
						}
					});
				}
				return tmpRows;
			},
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
				field:"ip",title:"IP",width:100,ellipsis:true,formatter:function(ip,row){
					if(row.isMove){
						var tmp =$("<span>");
						tmp.css({
							color:"red",
							"font-weight":"bolder"
						});
						tmp.text(ip);
						return tmp.get(0);
					}else{
						return ip;
					}
				}
			},{
				field:"showName",title:"设备名称",width:150,ellipsis:true
			},{
				field:"typeName",title:"设备类型",width:90,ellipsis:true
			}]],
			selector:this.fields.pickgrid
		});
		//初始化名称
		this.fields.name.validatebox({
			required:true
		});
		//所属分组初始化
		this.fields.topos.combobox({
			width:100
		});
		$.post(oc.resource.getUrl("topo/0/subTopos.htm"),function(items){
			var topos=[{
				id:0,
				text:"二层拓扑"
			}];
			function traverse(_items){
				$.each(_items,function(idx,item){
					topos.push({
						text:item.text,
						id:item.id
					});
					if(item.children){
						traverse(item.children);
					}
				});
			}
			traverse(items);
			ctx.fields.topos.combobox({
				width:100,
				textField:"text",
				valueField:"id",
				data:topos,
				value:ctx.args.parentId||0
			});
			
		},"json");
		
		//初始化表格数据
		this.leftGrid=this.pickgrid.leftGrid.selector;
		this.rightGrid=this.pickgrid.rightGrid.selector;
		this.loadData(this.args.parentId||0);
		//初始化数据
		if(this.args.subTopoId){
			oc.util.ajax({
				url:oc.resource.getUrl("topo/"+this.args.subTopoId+"/topoinfo.htm"),
				success:function(info){
					ctx.setValue(info);
					ctx._search();
				}
			});
		}else{
			ctx.setValue({
				devices:[]
			});
		}
		this.regEvent();
		//初始化移动箭头
		this._initMoveArrow();
	},
	loadData:function(topoId){
		var ctx = this;
		//初始化筛选数据
		oc.util.ajax({
			url:oc.resource.getUrl("topo/"+topoId+"/topoinfo.htm"),
			success:function(info){
				ctx.leftData=info.devices;
				ctx.leftGrid.datagrid("loadData",ctx.leftData);
				ctx._search();
			}
		});
	},
	_initMoveArrow:function(){
		with(this){
			var timeId=setInterval(function(){
				var pickCenter=fields.pickgrid.find(".pickList-center");
				if(pickCenter && pickCenter.length>0){
					clearInterval(timeId);
					var arrow = pickCenter.find(".tab-tree-arrow");
					arrow.css("top","25%");
					var moveArrow = arrow.clone();
					moveArrow.css("margin-top","40px");
					pickCenter.append(moveArrow);
					var copyLabel = $("<div>复制</div>"),cutLabel = $("<div>移动</div>");
					arrow.prepend(copyLabel);
					moveArrow.prepend(cutLabel);
					moveArrow.find(".tree-arrow-right").on("click",function(){
						_moveRight();
					});
					moveArrow.find(".tree-arrow-left").on("click",function(){
						_moveLeft();
					});
				}
			},200);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(timeId);
			}else{
				tasks = new Array();
				tasks.push(timeId);
				oc.index.indexLayout.data("tasks", tasks);
			}
		}
	},
	_moveRight:function(){
		var srcRows = this.leftGrid.datagrid("getSelections");
		var ctx = this;
		if(srcRows && srcRows.length>0){
			$.each(this._filter(srcRows),function(idx,row){
				if(!ctx.lastSubtopoItemsMap[row.ip] || ctx.lastMoveItemsMap[row.ip]){
					row.isMove=true;
					ctx.moveMap[row.ip]=true;
				}
				ctx.rightGrid.datagrid("appendRow",row);
			});
			ctx._search();
		}else{
			alert("请选择要移动的数据!");
		}
	},
	_moveLeft:function(){
		var desRows = this.rightGrid.datagrid("getSelections");
		var rows = this.rightGrid.datagrid("getRows");
		var ctx = this;
		if(desRows && desRows.length>0){
			var map = {};
			$.each(desRows,function(idx,row){
				map[row.ip]=row;
				delete ctx.moveMap[row.ip];
			});
			var tmpRows = [];
			$.each(rows,function(idx,row){
				if(!map[row.ip]){
					if(ctx.moveMap[row.ip]){
						row.isMove=true;
					}
					tmpRows.push(row);
				}
			});
			$.each(desRows,function(idx,row){
				if(ctx.lastMoveItemsMap[row.ip]){
					ctx.leftData.push(row);
				}
			});
			this.rightGrid.datagrid("loadData",tmpRows);
			this._search();
		}else{
			alert("请选择要移动的数据!");
		}
	},
	//过滤主表格中的数据
	_filter:function(rows){
		var srcRows = rows || this.leftGrid.datagrid("getRows");
		var desRows = this.rightGrid.datagrid("getRows");
		var map = {};
		for(var i=0;i<desRows.length;i++){
			var tmpRow = desRows[i];
			map[tmpRow.ip]=tmpRow;
		}
		var retn = [];
		for(var i=0;i<srcRows.length;i++){
			var tmpRow = srcRows[i];
			if(!map[tmpRow.ip]){
				retn.push(tmpRow);
			}
		}
		return retn;
	},
	_search:function(){
		var fd=this.fields;
		var ctx = this;
		var ip = fd.ip.val();
		if(ctx.leftData){
			var exp = new RegExp(ip);
			var searched=[];//过滤的结果
			for(var i=0;i<ctx.leftData.length;i++){
				var row = ctx.leftData[i];
				row.isMove=false;
				if(row.ip && exp.test(row.ip)){
					searched.push(row);
				}
			}
			//重新设置左边表格的数据
			ctx.leftGrid.datagrid("loadData",ctx._filter(searched));
			ctx.leftGrid.datagrid("uncheckAll");
			ctx.rightGrid.datagrid("uncheckAll");
		}
	},
	regEvent:function(){
		var ctx = this;
		var fd=this.fields;
		//搜索按钮
		fd.search.on("click",function(){
			ctx._search();
		});
		fd.ip.on("keyup",function(e){
			if(e.keyCode==13){
				ctx._search();
			}
		});
		fd.topos.combobox({
			onSelect:function(record){
				ctx.loadData(record.id);
			}
		});
	},
	//对比出移动的节点
	_compare:function(items,callback){
		var ctx=this;
		var tmpItems=[],tmpItemsMap={};
		var timeId=setInterval(function(){
			if(ctx.leftData){
				clearInterval(timeId);
				var map = {};
				$.each(ctx.leftData,function(idx,row){
					map[row.ip]=true;
				});
				$.each(items||[],function(idx,row){
					if(!map[row.ip]){
						tmpItems.push(row);
						tmpItemsMap[row.ip]=row;
						row.isMove=true;
						ctx.moveMap[row.ip]=true;
					}
				});
				if(callback){
					callback.call(ctx,tmpItems,tmpItemsMap);
				}
			}
		},500);
		var tasks = oc.index.indexLayout.data("tasks");
		if(tasks && tasks.length > 0){
			oc.index.indexLayout.data("tasks").push(timeId);
		}else{
			tasks = new Array();
			tasks.push(timeId);
			oc.index.indexLayout.data("tasks", tasks);
		}
	},
	getValue:function(){
		var fd=this.fields,ctx=this;
		if(fd.name.validatebox("isValid")){
			//获取表格的值
			var items = this.rightGrid.datagrid("getRows");
			var copyIds=[],delIds=[],uploadMoveIds=[],downLoadMoveIds=[];
			var tmpLastMap={};tmpAddMap={};
			var map={};
			$.each(this.lastSubtopoItems||[],function(idx,row){
				tmpLastMap[row.ip]=row;
			});
			//查找移动到当前拓扑的节点
			for(var i=0;i<items.length;i++){
				var ite = items[i];
				if(this.moveMap[ite.ip]&&!tmpLastMap[ite.ip]){
					downLoadMoveIds.push(ite.id);
				}else{
					if(!this.moveMap[ite.ip]){
						tmpAddMap[ite.ip]=ite;
					}
				}
				map[ite.ip]=ite;
			}
			//查找移动到父拓扑的节点
			var uploadMoveIdsMap={};
			$.each(this.lastMoveItems||[],function(idx,row){
				if(!map[row.ip]){
					uploadMoveIds.push(row.id);
					uploadMoveIdsMap[row.ip]=row;
				}
			});
			//查找删除的节点
			$.each(this.lastSubtopoItems||[],function(idx,row){
				var tmpRow=tmpAddMap[row.ip];
				if(tmpRow){
					tmpRow.delCheckFlag=true;
				}else{
					if(!ctx.moveMap[row.ip]&&!uploadMoveIdsMap[row.ip]){
						delIds.push(row.id);
					}
				}
			});
			//查找复制的节点
			$.each(tmpAddMap,function(idx,row){
				if(!row.delCheckFlag){
					copyIds.push(row.id);
				}
			});
			var retn = {
				name:fd.name.val(),
				parentId:0,
				ids:copyIds.join(","),
				uploadMoveIds:uploadMoveIds.join(","),
				downLoadMoveIds:downLoadMoveIds.join(","),
				delIds:delIds.join(",")
			};
			if(this.args.subTopoId){
				retn.id=this.args.subTopoId;
			}
			if(this.args.parentId){
				retn.parentId=this.args.parentId;
			}
			return retn;
		}else{
			fd.name.focus();
		}
	},
	setValue:function(info){
		var ctx = this;
		this._compare(info.devices,function(items,map){
			ctx.rightGrid.datagrid("loadData",info.devices||info.value);
			ctx.fields.name.val(info.name);
			if(info.id){
				ctx.args.subTopoId=info.id;
			}
			if(info.parentId){
				ctx.args.parentId=info.parentId;
				/*BUG #45450 拓扑分层情况显示错误 huangping 2017/9/11 start*/
                ctx.fields.topos.combobox('setValue', ctx.args.parentId);
                /*BUG #45450 拓扑分层情况显示错误 huangping 2017/9/11 end*/
			}
			//上一次的移动id
			ctx.lastMoveItems=items;
			ctx.lastMoveItemsMap=map;
			//上一次拓扑节点
			ctx.lastSubtopoItems=[];
			ctx.lastSubtopoItemsMap={};
			$.each(info.devices||[],function(idx,de){
				ctx.lastSubtopoItems.push({
					id:de.id,
					ip:de.ip
				});
				ctx.lastSubtopoItemsMap[de.ip]=de;
			});
			//过滤显示数据
			ctx.leftGrid.datagrid("loadData",this._filter());
			ctx._search();
		});
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	},
	save:function(){
		var result = this.getValue();
		var ctx = this;
		if(result){
			oc.util.ajax({
				url:oc.resource.getUrl("topo/subtopo/createOrUpdateSubtopo.htm"),
				data:result,
				success:function(result){
					if(result.status==200){
						if(result.data && result.data.id){
							if(ctx.onsave){
								ctx.onsave(result.data);
							}
							ctx.root.dialog("close");
						}else{
							alert(result.data.msg);
						}
					}else{
						alert(result.msg,"warning");
					}
				}
			});
		}
	}
};