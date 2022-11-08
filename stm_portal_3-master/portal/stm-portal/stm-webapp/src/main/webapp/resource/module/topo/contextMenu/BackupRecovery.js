/**拓扑数据备份/恢复*/
function BackupRecovery(args){
	this.args = args;
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/topo/contextMenu/BackupFiles.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};

BackupRecovery.prototype={
	init:function(html){
		this.datagrid=null;
		this.root = $(html);
		this.root.dialog({
			title:"拓扑数据维护",
			width:500,
			height:400
		});
		//搜索全局域
		this.searchField("field");
		this.initUi();
		this.regEvent();
	},
	searchField:function(type){	//搜索域
		var ctx = this;
		this.fields={};
		this.root.find("[data-"+type+"]").each(function(index,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-"+type)] = tmp;
		});
	},
	//初始化按钮
	initUi:function(){
		this.fields.addBtn.linkbutton("RenderLB",{
			text:"备份"
		});
		this.fields.delBtn.linkbutton("RenderLB",{
			text:"删除"
		});
		//初始化表格
		this.initDatagrid();
	},
	initDatagrid:function(){
		var ctx = this;
		oc.ui.datagrid({
			selector:ctx.fields.fileDatagrid,
			url : oc.resource.getUrl("topo/datas/files.htm"),
			singleSelect:false,
			columns:[[
		        {field:"id",checkbox:true},
		        {field:'fileName',title:'备份时间',width:'68%',sortable:true},
				{field:'opration',title:'操作',width:'30%',formatter:function(value,row,index){
					return '<span class="ico ico-back">恢复</span>';
				}}
	         ]],
	 		onClickCell:function(rowIndex, field, value){
			   	 if(field == "opration"){
			   		var row = ctx.fields.fileDatagrid.datagrid("getRows")[rowIndex];
			   		oc.ui.confirm('确定使用此文件恢复数据吗？',function() {
						oc.util.ajax({
							url:oc.resource.getUrl('topo/datas/recovery.htm'),
							type: 'POST',
							dataType: "json", 
							data:{fileId:row.id},
							successMsg:null,
							success:function(data){
								alert(data.data);
								ctx.root.dialog("close");
								eve("topo.refresh");		//恢复完成后刷新整个拓扑
							}
						});
					});
			   	 }
			}
		});
	},
	//绑定事件
	regEvent:function(){
		var ctx = this;
		//备份
		this.fields.addBtn.on("click",function(){
			oc.util.ajax({
				url : oc.resource.getUrl('topo/datas/backup.htm'),
				failureMsg:'备份失败',
				successMsg:"",
				success : function(data) {
					alert(data.data);
					ctx.fields.fileDatagrid.datagrid("reload");
				}
			});
		});
		//删除文件
		this.fields.delBtn.on("click",function(){
			ctx.delFile();
		});
	},
	//删除备份文件
	delFile:function(){
		var datagrid = this.fields.fileDatagrid;
		var rows = datagrid.datagrid("getSelections");
		if (rows.length == 0) {
			alert('请至少选择一条数据');
			return;
		}
		var fileIds = new Array();
		$(rows).each(function(index,row){
			fileIds.push(row.id);
		});
		log(fileIds)
		oc.ui.confirm('是否删除选中文件？',function() {
			oc.util.ajax({
				url:oc.resource.getUrl('topo/datas/delete.htm'),
				type: 'POST',
				dataType: "json", 
				data:{ids:fileIds.join(",")},
				successMsg:null,
				success:function(data){
					alert(data.data);
					datagrid.datagrid("reload");
				}
			});
		});
	}
};