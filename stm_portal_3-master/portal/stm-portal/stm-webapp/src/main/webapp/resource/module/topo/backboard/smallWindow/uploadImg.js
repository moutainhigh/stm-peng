function UploadImgDia(args){
	var ctx = this;
	//加载视图资源
	oc.util.ajax({
		type:"get",
		url:oc.resource.getUrl("resource/module/topo/backboard/smallWindow/uploadImg.html"),
		dataType:"html",
		success:function(html){
			ctx.init(html);
		}
	});
	this.type=args.type || "normal";
}
UploadImgDia.prototype={
	init:function(html){
		var ctx = this;
		this.template = $(html);
		//初始化表单
		this.form = this.template.find("form");
		this.form.form({
			url:oc.resource.getUrl("topo/image/save.htm"),
			success:function(){
				ctx.onsuccess();
				ctx.template.dialog("close");
				alert("上传成功","info");
			},
			onSubmit:function(){}
		});
		//文件名
		this.form.fileName=this.form.find("input[name='fileName']");
		this.form.fileName.validatebox({
			required:true
		});
		//文件
		this.form.file=this.form.find("input[name='image']");
		this.form.file.validatebox({
			required:true
		});
		//设置类型
		this.form.find("input[name='type']").val(this.type);
		//打开对话框
		this.template.dialog({
			width:370,height:260,
			title:"添加自定义图片",
			buttons:[{
				text:"上传",
				handler:function(){
					if(ctx.check()){
						ctx.form.submit();
					}else{
						alert("说明：<br/>1、图片只能上传jpg、jpeg、gif、png这几种格式。<br/>2、图片大小不能超过2MB。","warning");
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
			var name = this.form.fileName.val();
			var fileName = this.form.file.val();
			if(name!="" && fileName.toLowerCase().match(/gif|jpg|jpeg|bmp|png$/)){
				return true;
			}
		}
		return false;
	}
};