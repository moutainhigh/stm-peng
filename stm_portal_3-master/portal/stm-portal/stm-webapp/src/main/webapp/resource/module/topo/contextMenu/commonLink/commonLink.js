function CommonLink(args){
	var ctx = this;
	oc.util.ajax({
		type:"get",
		url:oc.resource.getUrl("resource/module/topo/contextMenu/commonLink/commonLink.html"),
		success:function(html){
			ctx.init(html);
		},
		dataType:"html"
	});
}
CommonLink.prototype={
	init:function(html){
		var ctx = this;
		this.root = $(html);
		this.root.dialog({
			height:400,width:750,
			title:"常用链接",
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
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		this.fields.grid.datagrid({
			loadFilter: function(d){
				if(d instanceof Array){
					return {total:d.length,rows:d};
				}else{
					return {total:0,rows:[]};
				}
			},
			data:[],
			columns:[[
			     {
			    	 field:'id',checkbox:true,width:50
			     },
		         {
			    	 field:'name',title:'名称',width:200,
			    	 editor:{
			        	 type:"validatebox",
			        	 options:{
			        		 required:true
			        	 }
			    	 }
			     },
		         {
			    	 field:'url',title:'URL地址',width:400,
			    	 editor:{
			        	 type:"validatebox",
			        	 options:{
			        		 validType:"url",
			        		 required:true,
			        		 invalidMessage:"输入正确的url地址(http://www.xxx.com)"
			        	 }
			    	 }
			     }
		     ]]
		});
		//加载数据
		this.load();
		this.regEvent();
	},
	getIdx:function(){
		if(!this.idx){
			this.idx=0;
		}
		return --this.idx;
	},
	regEvent:function(){
		var ctx = this;
		//添加行
		this.fields.addBtn.on("click",function(){
			var length=ctx.fields.grid.datagrid("getRows").length;
			if(length>=5){
				alert("最多只能设置5个常用链接","warning");
			}else{
				ctx.fields.grid.datagrid("appendRow",{
					id:ctx.getIdx()
				});
				ctx.fields.grid.datagrid("beginEdit",length);
			}
		});
		//删除行
		this.fields.delBtn.on("click",function(){
			var rows = ctx.fields.grid.datagrid("getSelections");
			if(rows.length == 0){
				alert('请至少选择一条数据', 'warning');
			}else{
				oc.ui.confirm('是否删除选中数据？',function() {
					for(var i=0;i<rows.length;++i){
						ctx.fields.grid.datagrid("deleteRow",ctx.fields.grid.datagrid("getRowIndex",rows[i]));
					}
				});
			}
		});
	},
	load:function(){
		var ctx = this;
		$.get(oc.resource.getUrl("topo/setting/get/commonLink.htm"),function(data){
			ctx.fields.grid.datagrid("loadData",data);
			for(var i=0;i<data.length;++i){
				ctx.fields.grid.datagrid("beginEdit",i);
			}
		},"json");
	},
	getValue:function(){
		//验证是否输入合法
		var invalidIpts=this.root.find(".validatebox-invalid");
		if(invalidIpts && invalidIpts.length>0){
			$(invalidIpts[0]).focus();
			return null;
		}else{
			this.root.find(".closeBtn").trigger("click");
			var tmpRows = this.fields.grid.datagrid("getRows");
			for(var i=0;i<tmpRows.length;++i){
				this.fields.grid.datagrid("endEdit",i);
			}
			var rows = $.grep(tmpRows,function(row){
				return row.name!="" && row.url!="";
			});
			return rows;
		}
	},
	save:function(){
		var ctx = this;
		var value = this.getValue();
		if(value){
			oc.util.ajax({
				url:oc.resource.getUrl("topo/setting/save.htm"),
				data:{
					key:"commonLink",
					value:JSON.stringify(value)
				},
				success:function(msg){
					alert("保存成功");
					ctx.root.dialog("close");
				},
				urlMsg:"正在保存常用链接配置..."
			});
		}
	}
};