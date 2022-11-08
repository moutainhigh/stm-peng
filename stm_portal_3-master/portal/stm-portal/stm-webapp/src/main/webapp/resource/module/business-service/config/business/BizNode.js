/**
 * 节点接口
 */
oc.resource.loadScripts([
    "resource/module/business-service/config/business/BizBase.js",
    "resource/module/business-service/config/business/Bizextend.js"
],function(){
	window.BizNode=Bizextend(function(args){
		BizBase.apply(this,arguments);
	},BizBase,{
	});
});