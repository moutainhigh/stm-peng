function ImportExcelDia(args){
	var ctx = this;
	//加载视图资源
	oc.util.ajax({
		type:"get",
		url:oc.resource.getUrl("resource/module/topo/ip-mac-port/importExcel.html"),
		dataType:"html",
		success:function(html){
			ctx.init(html);
		}
	});
}

ImportExcelDia.prototype={
	init:function(html){
		var ctx = this;
		this.template = $(html);
		//初始化表单
		this.form = this.template.find("form");
		this.form.form({
			url:oc.resource.getUrl("topo/mac/import.htm"),
			success:function(data){
				var rst = eval('('+data+')');
				if(200 != rst.code){
					alert(rst.data,"danger");
				}else{
					alert(rst.data);
					ctx.template.dialog("close");
					ctx.onsuccess();
				}
			},
			onSubmit:function(){}
		});
		//文件
		this.form.file=this.form.find("input[type='file']");
		this.form.file.validatebox({
			required:true
		});
		//下载模板
		this.form.download = this.form.find("#download");
		this.form.download.mouseover(function(){
			$(this).css({"color":"#fff"});
		});
		this.form.download.mouseout(function(){
			$(this).css("color","#C0D9EF");
		});
		this.form.download.click(function(){
			var ef = new ExportFile();
			ef.exportFile(oc.resource.getUrl("topo/mac/export.htm?exportType=template"));
		});
		//打开对话框
		this.template.dialog({
			width:600,height:180,
			title:"导入设备基准信息",
			buttons:[{
				text:"导入",
				handler:function(){
					if(ctx.check()){
						ctx.form.submit();
					}else{
						alert("请选择excel文档文件","danger");
					}
				}
			},{
				text:"取消",
				handler:function(){
					ctx.template.dialog("close");
				}
			}]
		});
	},
	onsuccess:function(){},
	on:function(type,callBack){
		this["on"+type]=callBack;
	},
	//检查是否符合上传要求
	check:function(){
		if(this.form.form("validate")){
			var file = this.form.file.val();
			if(file.match(/xls|xlsx$/)){
				return true;
			}
		}
		return false;
	}
};