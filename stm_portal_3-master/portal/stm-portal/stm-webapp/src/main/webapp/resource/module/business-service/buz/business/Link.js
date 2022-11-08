buzdefine("bz/Link", ["bz/Base","bz/extend"], function (Base,extend) {
    return extend(function(args){
    	args=$.extend({
    		nodes:null
    	},args);
    	if(args.nodes){
    		this.attr({
    			nodes:args.nodes
    		});
    		Base.apply(this,arguments);
    	}else{
    		throw "nodes can't be null";
    	}
    },Base,{
    	set_nodes:function(nodes){
    		this.nodes=nodes;
    	},
    	get_nodes:function(nodes){
    		return this.nodes;
    	}
    });
});