function TopoMapUtil(){
	this.timeMap={};
};
TopoMapUtil.prototype={
	beginLog:function(name){
		this.timeMap[name]=new Date().getTime();
	},
	endLog:function(name){
		var begin = this.timeMap[name];
		if(begin){
			// console.log(name+"耗时:"+(new Date().getTime()-begin)+"ms");
			delete this.timeMap[name];
		}
	}
};