/**
 * 元素工厂
 */
window.BizElementsFactory=function(args){
	this.args=$.extend({
		drawer:null
	},args);
	if(!this.args.drawer){
		throw "drawer can't be null";
	}
	this.linkDrawer=this.args.drawer.group();
	this.nodeDrawer=this.args.drawer.group();
};
window.BizElementsFactory.prototype = {
	getNode:function(info){
		var nn = new BizNetNode({
			drawer:this.nodeDrawer
		});
		nn.setValue(info);
		return nn;
	},
	getLink:function(info,fromNode,toNode){
		var lk=undefined;
		switch(info.type){
		case "poly":
    		lk = new BizPolyLink({
    			fromNode:fromNode,
    			toNode:toNode,
    			drawer:this.linkDrawer
    		});
    		break;
		default:
			lk = new BizNormalLink({
    			fromNode:fromNode,
    			toNode:toNode,
    			drawer:this.linkDrawer
    		});
		}
		lk.setValue(info);
		return lk;
	}
};
