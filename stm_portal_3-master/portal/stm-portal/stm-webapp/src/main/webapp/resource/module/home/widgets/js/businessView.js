function businessView(selector,opt){
	this.opt = {
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);
	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}
var bvp = businessView.prototype;

bvp.init = function(){
	this.dataObj = $("<div/>");
	this.dataObj.css({width:'100%',height:'100%'});
	this.opt.selector.html(this.dataObj);
	
	this.load();
}
bvp.load = function(type){

	var bizId = this.opt.bizId;
	var divobj = this.dataObj;

	if(bizId == 'ywhz'){
		divobj.load(oc.resource.getUrl('resource/module/home/widgets/js/bizSummary/biz_summary_model.html'),function(){});
	}else{
		this.loadBuzContent();
	}
	
}

bvp.loadBuzContent = function(){
	var bizId = this.opt.bizId;
	var divobj = this.dataObj;
	divobj.load(oc.resource.getUrl('resource/module/business-service/buz/business.html'),function(){
			oc.resource.loadScripts([
				'resource/module/business-service/js/biz_edit_permissions.js',
				'resource/module/business-service/buz/business.js'
				], function(){
					var count = 10;
					//$.getScript("module/business-service/buz/business.js",function(){	});
					var iner = setInterval(function(){//用于解决js加载完后还未执行的问题
						try{
							oc.business.service.edit.permissions.init();
							new businessTopo({showControlPanel:false,editAble:false,
								selector:divobj,
								bizId:bizId
							});
							clearInterval(iner);
						}catch(e){
							console.log(e);
							!(count --) && clearInterval(iner);
						}
					},100);
			});
	 });
}

bvp.refresh = function(type){
	var bizId = this.opt.bizId;
	var divobj = this.dataObj;

	if(bizId == 'ywhz'){
		if(type == 'updateSize'){
			divobj.load(oc.resource.getUrl('resource/module/home/widgets/js/bizSummary/biz_summary_model.html'),function(){});
		}else{
			oc.module.home.biz.summary.refresh();
		}
	}else{
		this.loadBuzContent();
	}
}