window.BizStart=function(args){
	this.args=$.extend({
		holder:null//画布的父dom
	},args);
	if(this.args.holder){
		this.init();
	}else{
		throw "holder can't be null";
	}
};
window.BizStart.prototype = {
	init:function(){
		var box = this.getBox();
		this.drawer = SVG(this.args.holder.get(0),box.w,box.h);
		this.businessView = new BizBusinessView({
			drawer:this.drawer
		});
		this.db = new BizDataHelper();
		this.loadData(0);
	},
	loadData:function(businessId){
		with(this){
			businessView.setValue(db.getBusiness(businessId));
		}
	},
	saveData:function(){
		var v=this.businessView.getValue();
        // console.log(JSON.stringify(v));
	},
	getBox:function(){
		return {
			w:this.args.holder.width(),
			h:this.args.holder.height()
		};
	}
};
