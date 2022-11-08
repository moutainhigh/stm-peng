function CabinetDeviceList(args){
	this.args=$.extend({
		title:"设备列表",
		w:600,h:400,
		id:null
	},args),
	ctx = this;
	if(this.args.id){
		oc.util.ajax({
			url:oc.resource.getUrl("resource/module/topo/contextMenu/CabinetDeviceList.html"),
			success:function(htl){
				ctx.init(htl);
			},
			type:"get",
			dataType:"html"
		});
	}else{
		throw "bad arguments";
	}
};
CabinetDeviceList.prototype={
	init:function(htl){
		this.fields={};
		this.root=$(htl);
		var ctx = this;
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		var ctx = this;
		this.root.dialog({
			title:this.args.title,
			width:this.args.w,
			height:this.args.h,
			buttons:[{
				text:"确定",handler:function(){
					ctx.root.dialog("close");
				}
			}]
		});
		this.fields.gridCont.css("height",this.root.height()-36);
		this.fields.grid.datagrid({
			loadFilter: function(d){
				return {total:d.length,rows:d};
			},
			pagination:false,
			singleSelect:true,
			idField:"ip",
			columns:[[{
				field:"state",title:"状态",width:"8%",formatter:function(state){
					//bug#21331
					if(!state||state=="- -"){
						return "- -";
					}else{
						var tmp = $("<span/>");
						tmp.addClass("light-ico_resource");
						var clz="res_"+state.toLowerCase();
						if(state.toLowerCase()=="normal"){
							clz="res_"+state.toLowerCase()+"_nothing";
						}
						tmp.addClass(clz);
						return tmp.get(0);
					}
				}
			},{
				field:"showName",title:"设备名称",width:"25%",ellipsis:true,formatter:function(name,row){
					var span = $("<span/>");
					span.addClass("device_name");
					span.text(name);
					span.attr("title",name);
					span.css({
						"cursor":"pointer"
					});
					span.attr("data-instance-id",row.instanceId);
					return span.get(0);
				}
			},{
				field:"ip",title:"IP地址",width:"15%",ellipsis:true
			},{
				field:"typeName",title:"类型",width:"15%",align:"center",ellipsis:true
			},{
				field:"cpuRate",title:"CPU利用率",width:"15%",ellipsis:true
			},{
				field:"ramRate",title:"内存利用率",width:"15%",ellipsis:true
			},{
				field:"instanceId",title:"面板图",width:"10%",ellipsis:true,formatter:function(instanceId,row){
					if(row.isNet===true){
						return $("<span style='margin-left:10px;' class='ico ico_beiban' data-instance-id='"+instanceId+"' data-id='"+row.id+"'><span>");
					}else{
						return "";
					}
				}
			}]]
		});
		
		oc.util.ajax({
			url:oc.resource.getUrl("topo/other/cabinetDeviceList.htm"),
			data:{
				id:this.args.id
			},
			success:function(result){
				if(result.status==200){
					ctx.data=result.rows;
					ctx.fields.grid.datagrid("loadData",result.rows);
				}else{
					alert(result.msg,"warning");
				}
			}
		});
		this.regEvent();
	},
	regEvent:function(){
		this.root.on("click",".ico_beiban",function(){
			var tmp=$(this);
			oc.resource.loadScript("resource/module/topo/contextMenu/BackBoardInfoDia.js", function(){
				new BackBoardInfoDia({
					downDeviceFlag:true,	//是否显示下联设备
					node:{
					d:{
						instanceId:tmp.attr("data-instance-id"),
						rawId:tmp.attr("data-id")
					}
					}
				});
			});
		});
		this.root.on("click",".device_name",function(){
			var tmp=$(this);
			var instanceId = tmp.attr("data-instance-id");
			if(instanceId && !isNaN(instanceId)){
				oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js", function(){
					oc.module.resmanagement.resdeatilinfonew.open({
						instanceId:instanceId
					});
				});
			}
		});
		this.fields.searchBox.on("keyup",function(e){
			if(e.keyCode==13){
				ctx.search($(this).val());
			}
		});
		this.fields.searchBtn.on("click",function(){
			ctx.search(ctx.fields.searchBox.val());
		});
	},
	search:function(searchStr){
		var reg = new RegExp(searchStr,"i");
		if(this.data){
			var result = [];
			$.each(this.data,function(idx,row){
				if(reg.test(row.ip) || reg.test(row.showName)){
					result.push(row);
				}
			});
			this.fields.grid.datagrid("loadData",result);
		}
	}
};
