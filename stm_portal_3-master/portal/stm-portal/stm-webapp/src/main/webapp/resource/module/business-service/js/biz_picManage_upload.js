function BizImgUpload(args){
	var ctx = this;
	this.args = $.extend({
		type:undefined,
		callBack:function(){}
	},args);
	oc.util.ajax({
		type:"get",
		url:oc.resource.getUrl("resource/module/business-service/biz_picManage_upload.html"),
		dataType:"html",
		success:function(html){
			ctx.init(html);
		}
	});
}

BizImgUpload.prototype={
	init:function(html){
		var ctx = this;
		this.template = $(html);
		this.form=oc.ui.form({
			selector:ctx.template.find("form"),
			filebox:[{
				selector:'[name=file]',
				width:220,
				onChange:function(newValue,oldValue){
					ctx._setImagePreview($("input[type=file]"),$("#localImag"),$("#preview"));
				}
			}]
		});
		//设置类型
		this.template.find("input[name='imgType']").val(ctx.args.imgType);
		//打开对话框
		this.template.dialog({
			width:460,height:200,
			title:"添加自定义图片",
			buttons:[{
				text:"上传",
				iconCls:'fa fa-check-circle',
				handler:function(){
					if(ctx._checkImg()){
						ctx._submit();
					}else{
						alert("说明：<br/>1、图片只能上传jpg、jpeg、gif、png这几种格式<br/>2、图片大小不能超过3MB","warning");
					}
				}
			},{
				text:"取消",
				iconCls:'fa fa-times-circle',
				handler:function(){
					ctx.template.dialog("close");
				}
			}]
		});
	},
	//提交表单
	_submit:function(){
		var ctx = this;
		ctx.form.submitWithFile({
			url:oc.resource.getUrl("portal/biz/image/save.htm"),
			data:ctx.form.val(),
			success:function(data){
				if(data && 200 == data.code){
					ctx.args.callBack(data.data);	//刷新面板
					ctx.template.dialog("close");
					alert("上传成功","info");
				}else{
					alert("上传图片失败");
				}
			}
		});
	},
	//设置预览
	_setImagePreview:function(docObj,localImagId,imgObjPreview){
		var ctx = this;
		var isImg = ctx._checkImg();	//是图片
		
		if(isImg) {
			docObj = docObj.get(0);
			if(docObj.files && docObj.files[0]){
				//火狐下，直接设img属性
				imgObjPreview.attr("src",window.URL.createObjectURL(docObj.files[0]));
			}else{
				//IE下，使用滤镜
				docObj.select();
				var imgSrc = document.selection.createRange().text;
				//图片异常的捕捉，防止用户修改后缀来伪造图片
				try{
					localImagId.get(0).style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
					localImagId.get(0).filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
				}catch(e){
					alert("您上传的图片格式不正确，请重新选择!");
				}                         
				imgObjPreview.style.display = 'none';
				document.selection.empty();
			}
		}else{
			alert("您上传的图片格式不正确，请重新选择!");
		}
    },
	//检测是否图片
	_checkImg:function(){
		var isImg = false;	//是图片
		var fileName = $("input[type=file]").val();
		if(fileName!="" && fileName!=null && fileName.toLowerCase().match(/gif|jpg|jpeg|png$/)) isImg = true;
		return isImg;
	}
};