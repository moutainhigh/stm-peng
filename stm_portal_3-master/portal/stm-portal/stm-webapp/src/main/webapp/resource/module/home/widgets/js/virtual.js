function virtual(selector,opt){
	this.opt = {
		
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);

	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');


}
var vlp = virtual.prototype;

vlp.init = function(){
	this.dataObj = $("<div/>");
	this.dataObj.css({width:'100%',height:'100%'});
	this.opt.selector.html(this.dataObj);

	this.load();
}

vlp.refresh = function(){
	this.init();
}

vlp.load = function(){
	var _this = this;
	var data = this.opt.item;
	
	//由于第二次刷新的时候Raphael缺少atan方法，找不出原因，所以把第一次初始化的Raphael赋值给当前对象
	if(!_this.Raphael){
		_this.Raphael = window.Raphael;
	}
	if(!window.Raphael.atan){
		window.Raphael.atan = _this.Raphael.atan;
	}
	
	_this.dataObj.empty();

	var dataTemp = {datacenteruuid:"75467825-a82e-3dde-a7b7-d3dacc3df357",
			href:oc.resource.getUrl('resource/module/vm/topology/topology.html'),
			name:"基础架构拓扑",
			title:"基础架构拓扑"};
			
	if(data){
		dataTemp.datacenteruuid = data.id;
		dataTemp.name = data.vmname;
		dataTemp.title = data.vmname;
	}

	_this.dataObj.panel('RenderP',{
		href : dataTemp.href,
		title: dataTemp.title,
		onLoad : function(){
			oc.resource.loadScript('resource/module/vm/topology/js/vm_topology.js?t='+new Date(),function(){
				oc.module.vm.topology.graphForHome(dataTemp);
			});
		}
	});

}