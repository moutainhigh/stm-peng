/*替换图片*/
function BizPicReplace(args){
	this.args = args;
	this.callBack = args.callBack;	//回调函数（关闭窗口后替换图标）
	this.curPicSrc = undefined;		//当前选中的图片
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/business-service/biz_pic_replace.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};

BizPicReplace.prototype={
	init:function(html){
		var ctx = this;
		ctx.root = $(html);
		ctx.dialog = ctx.root.dialog({
			title:"选择图标",
			width:582,height:363,
			draggable:true,
			cache: false,
			modal: true ,
			buttons:[{
				text:'确定',
				iconCls:'ico ico-ok',
				handler:function(){
					ctx.save();
				}
			},{
				text:'取消',
				iconCls:'ico ico-cancel',
				handler:function(){
					ctx.dialog.dialog("close");
				}
			}]
		});
		//搜索全局域
		ctx._searchField("field");
		ctx.initUi();
		ctx.regEvent();
	},
	//搜索域
	_searchField:function(type){
		var ctx = this;
		ctx[type]={};
		ctx.root.find("[data-"+type+"]").each(function(idx,dom){
			var tmp = $(dom);
			ctx[type][tmp.attr("data-"+type)]=tmp;
		});
	},
	//渲染UI
	initUi:function(){
		//加载上传图片
		this._loadSelfImgs();
	},
	//保存选中值
	save:function(){
		this.callBack(this.curPicSrc);
		this.dialog.dialog("close");
	},
	//加载系统上传的自定义图片
	_loadSelfImgs:function(){
		var ctx = this,type="1";	//只加载资源图标
		//获取模板
		var template = ctx.field.picContainer.find(".template");
		//从服务器获取图片
		oc.util.ajax({
			url:oc.resource.getUrl("portal/biz/image/list/"+type+".htm"),
			success:function(data){
				if(data && data.code == 200){
					//清空以前的上传的自定义图片（data-id区分），重新更新
					ctx.field.picContainer.find("img[data-id]").parent().remove();
					//绘制当前图标
					for(var i=0;i<data.data.length;i++){
						var item = data.data[i];
						var tmp = template.clone();
						var image = tmp.find("img");
						image.attr({
							src:oc.resource.getUrl("platform/file/getFileInputStream.htm?fileId="+item.fileId),
							"data-id":item.id
						});
						tmp.removeClass("hide template");
						template.after(tmp);	//模板后添加新图片
						ctx.onImgClick(tmp);
					}
				}
			}
		});
	},
	//注册事件
	regEvent:function(){
		var ctx = this;
		var imgDivs = ctx.field.picContainer.find("div");
		ctx.onImgClick(imgDivs);
	},
	//图片绑定点击事件
	onImgClick:function(imgDivs){
		var ctx = this;
		imgDivs.on("click",function(){
			ctx.field.picContainer.find("div").css("border-color","#C8D4E3");
			var imgDiv = $(this);
			imgDiv.css({"border-color":"#E59D7A","border-width":"2px"});
			ctx.curPicSrc = imgDiv.find("img").attr("src");
		});
	}
};