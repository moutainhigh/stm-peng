function TopoAddLink(args){
	this.args=$.extend({
		dialog:true,
		holder:null,
		from:null,
		to:null,
		onOk:function(){}
	},args);
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/TopoAddLink.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
}
TopoAddLink.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		if(this.args.dialog){
			this.root.dialog({
				title:"转为链路",
				width:700,height:428,
				buttons:[{
					text:"添加",handler:function(){
						//获取链路转换规则
						var linkType = null;
                        oc.util.ajax({
                            url:oc.resource.getUrl("topo/setting/get/globalSetting.htm"),
                            async:false,
                            dataType:"json",
                            success:function(cfg){
                                if (cfg.link.defType) {
                                    linkType = cfg.link.defType;
                                } else {
                                    linkType = 0;
                                }
                            }
                        });

						var value = ctx.getValue(linkType);

						if(value){
							if(ctx.onok){
								ctx.onok(value);
							}
							ctx.args.onOk.call(ctx,value);
							ctx.root.dialog("destroy");
						}
					}
				},{
					text:"取消",handler:function(){
						ctx.root.dialog("destroy");
					}
				}]
			});
		}
		this.initUi();
		this.regEvent();
	},
	on:function(key,callBack){
		this["on"+key]=callBack;
	},
	regEvent:function(){
		var ctx = this;
		//源查询按钮，请求源端数据
		this.fields.srcSearchBtn.on("click",function(){
			ctx.loadSrcDatagrid();
		});
		//目的查询按钮，请求目的端数据
		this.fields.desSearchBtn.on("click",function(){
			ctx.loadDesDatagrid();
		});
	},
	initUi:function(){
		//渲染查询按钮
		$('.easyui-linkButton').linkbutton('RenderLB');
		
		var ctx = this;
		this.fields.srcgrid.datagrid({
			width:310,
			pagination:false,
			singleSelect:true,
			loadFilter: function(d){
				if(d instanceof Array){
					return {total:d.length,rows:d};
				}else{
					return {total:0,rows:[]};
				}
			},
			onSelect:function(idx,row){
				ctx.srcRow=row;
				ctx.refreshChosed();
			},
			columns:[[{
				field:"ifIndex",title:"索引号",align:'center',width:45,ellipsis:true
			},{
				field:"name",title:"接口名称",align:'center',width:100,ellipsis:true
			},{
				field:"showName",title:"显示名称",align:'center',width:100,ellipsis:true,formatter:function(value,row,index){
					return "- -" == row.showName ? row.name:row.showName;
				}
			}
			]]
		});
		this.fields.desgrid.datagrid({
			width:310,
			pagination:false,
			singleSelect:true,
			loadFilter: function(d){
				if(d instanceof Array){
					return {total:d.length,rows:d};
				}else{
					return {total:0,rows:[]};
				}
			},
			onSelect:function(idx,row){
				ctx.desRow=row;
				ctx.refreshChosed();
			},
			columns:[[{
				field:"ifIndex",title:"索引号",align:'center',width:45,ellipsis:true
			},{
				field:"name",title:"接口名称",align:'center',width:100,ellipsis:true
			},{
				field:"showName",title:"显示名称",align:'center',width:100,ellipsis:true,formatter:function(value,row,index){
					return "- -" == row.showName ? row.name:row.showName;
				}
			}
			]]
		});
		//请求源端数据
		this.loadSrcDatagrid();
		//请求目的端数据
		this.loadDesDatagrid();
		/*var from = this.args.from,to = this.args.to;
		if(from && from.d.instanceId){
			this.fields.srcip.html(from.d.ip);
			this._loadData(from.d.instanceId, function(ifs){
				ctx.fields.srcgrid.datagrid("loadData",ifs);
			});
		}*/
		/*if(to && to.d.instanceId){
			this.fields.desip.html(to.d.ip);
			this._loadData(to.d.instanceId, function(ifs){
				ctx.fields.desgrid.datagrid("loadData",ifs);
			});
		}*/
	},
	//加载源端表格数据
	loadSrcDatagrid:function(){
		var from = this.args.from;
        /*BUG #41841 拓扑管理：连线无法转为链路 huangping 2017/7/13 start*/
        /*if(from && from.d.id){
         this.fields.srcip.text(oc.topo.map.graph.nodeData[from.d.id].ip);
         this._loadData("src",oc.topo.map.graph.nodeData[from.d.id].instanceId, function(ifs){
         this.fields.srcgrid.datagrid("loadData",ifs);
         });
         }*/
        if (oc.topo.map.graph && oc.topo.map.graph.nodeData[from.d.id]) {
            if (from && from.d.id) {
                this.fields.srcip.text(oc.topo.map.graph.nodeData[from.d.id].ip);
                this._loadData("src", oc.topo.map.graph.nodeData[from.d.id].instanceId, function (ifs) {
                    this.fields.srcgrid.datagrid("loadData", ifs);
                });
            }
        } else {
            if (from && from.d.instanceId) {
                this.fields.srcip.text(from.d.ip);
                this._loadData("src", from.d.instanceId, function (ifs) {
                    this.fields.srcgrid.datagrid("loadData", ifs);
                });
            }
        }
        /*BUG #41841 拓扑管理：连线无法转为链路 huangping 2017/7/13 end*/
	},
	//加载目的端表格数据
	loadDesDatagrid:function(){
		var to = this.args.to;
        /*BUG #41841 拓扑管理：连线无法转为链路 huangping 2017/7/13 start*/
        /*if(to && to.d.id){
         this.fields.desip.text(oc.topo.map.graph.nodeData[to.d.id].ip);
         this._loadData("des",oc.topo.map.graph.nodeData[to.d.id].instanceId, function(ifs){
         this.fields.desgrid.datagrid("loadData",ifs);
         });
         }*/
        if (oc.topo.map.graph && oc.topo.map.graph.nodeData[to.d.id]) {
            if (to && to.d.id) {
                this.fields.desip.text(oc.topo.map.graph.nodeData[to.d.id].ip);
                this._loadData("des", oc.topo.map.graph.nodeData[to.d.id].instanceId, function (ifs) {
                    this.fields.desgrid.datagrid("loadData", ifs);
                });
            }
        } else {
            if (to && to.d.instanceId) {
                this.fields.desip.text(to.d.ip);
                this._loadData("des", to.d.instanceId, function (ifs) {
                    this.fields.desgrid.datagrid("loadData", ifs);
                });
            }
        }
        /*BUG #41841 拓扑管理：连线无法转为链路 huangping 2017/7/13 end*/
	},
	_loadData:function(target,instanceId,callBack){
		var ctx = this;
		var searchVal = ctx.fields.srcSearchVal.val();	//搜索条件
		if(target == "des") searchVal = ctx.fields.desSearchVal.val();
		oc.util.ajax({
			url:oc.resource.getUrl("topo/backboard/interface/all.htm"),
			data:{
				instanceId:instanceId,
				searchVal:searchVal.toLowerCase()
			},
			success:function(result){
				callBack.call(ctx,result);
			}
		});
	},
    getValue:function(type){
        var retn = {};
        var from = this.args.from,to = this.args.to;
        retn.fromId=from.d.rawId||from.d.id;
        retn.toId=to.d.rawId||to.d.id;
        retn.fromType=from.linktype||"node";
        retn.toType=to.linktype||"node";
        if(from.d.instanceId && !this.srcRow && !!this.desRow){
            this.fields.srcChosed.addClass("danger");
            this.fields.srcChosed.html("未选择源端口");
            flag=false;
        }
        if(to.d.instanceId && !this.desRow && !!this.srcRow){
            this.fields.desChosed.addClass("danger");
            this.fields.desChosed.html("未选择目的端口");
            flag=false;
        }

        var typeflag = false;
        if(type=="0"){//必须要有两个接口
            typeflag = this.srcRow && this.desRow;
            if(!typeflag){
            	alert("需要两端都关联接口才能转换为链路");
			}
        }else if(type=="1"){//只要有一个有接口
            typeflag = this.srcRow || this.desRow;
		}

        if(typeflag){
            if(this.srcRow){
                this.fields.srcChosed.removeClass("danger");
                retn.srcIfInstanceId=this.srcRow.instanceId;
                retn.srcIfIndex=this.srcRow.ifIndex;
                retn.valueInstanceId=retn.srcIfInstanceId;
                retn.srcMainInstanceId=from.d.instanceId;
                retn.valueMainInstanceId=retn.srcMainInstanceId;
            }
            if(this.desRow){
                this.fields.desChosed.removeClass("danger");
                retn.desIfInstanceId=this.desRow.instanceId;
                retn.desIfIndex=this.desRow.ifIndex;
                if(!retn.valueInstanceId){
                    retn.valueInstanceId=retn.desIfInstanceId;
                }
                retn.desMainInstanceId=to.d.instanceId;
                if(!retn.valueMainInstanceId){
                    retn.valueMainInstanceId=retn.desMainInstanceId;
                }
            }
            return retn;
        }
        return null;
    },
	//刷新选中的行
	refreshChosed:function(){
		if(this.srcRow){
			this.fields.srcChosed.removeClass("danger");
			this.fields.srcChosed.attr("title",this.srcRow.name);
			this.fields.srcChosed.html("选中接口&nbsp;"+this.srcRow.ifIndex+":"+this.srcRow.name);
		}
		if(this.desRow){
			this.fields.desChosed.removeClass("danger");
			this.fields.desChosed.attr("title",this.desRow.name);
			this.fields.desChosed.html("选中接口&nbsp;"+this.desRow.ifIndex+":"+this.desRow.name);
		}
	},
	updateFromData:function(data){
		this.args.from = data;
	},
	updateToData:function(data){
		this.args.to = data;
	}
};