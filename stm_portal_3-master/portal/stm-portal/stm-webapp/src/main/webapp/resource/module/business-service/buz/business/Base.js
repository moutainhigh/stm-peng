/**
 * 基础类
 */
buzdefine("bz/Base",[],function(){
	var tmp = function(args){
		this.args=$.extend({
			drawer:null
		},args);
		if(this.args.drawer){
    		this.data = null;
    		this.events={};
    		this.init();
    	}else{
    		throw "drawer can't be null";
    	}
	};
	tmp.prototype={
    	init:function(){
    		throw "not implement";
    	},
    	getValue:function(){
    		throw "not implement";
    	},
    	setValue:function(info){
    		throw "not implement";
    	},
    	refresh:function(attr){
    		throw "not implement";
    	},
    	attr:function(field,value){
    		if(field){
    			if(field instanceof Object){
    				for(var k in field){
    					this._attr(k,field[k]);
    				}
    			}else{
    				 return this._attr(field,value);
    			}
    		}
    	},
    	_attr:function(field,value){
			if(value==undefined && this["get_"+field]){
				return this["get_"+field].call(this);
			}
			if(value!=undefined  && this["set_"+field]){
				this["set_"+field].call(this,value);
			}
    		return undefined;
    	},
    	on:function(evtStr,cb){
    		var cbs=this.events[evtStr];
    		if(cbs != undefined){
    			cbs.push(cb);
    		}else{
    			this.events[evtStr]=[cb];
    		}
    	},
    	exe:function(evtStr){
    		var cbs=this.events[evtStr];
    		if(cbs!=undefined){
    			for(var i=0;i<cbs.length;i++){
    				var cb = cbs[i];
    				cb.apply(this,arguments[1]);
    			}
    		}
    	}
	};
	return tmp;
});