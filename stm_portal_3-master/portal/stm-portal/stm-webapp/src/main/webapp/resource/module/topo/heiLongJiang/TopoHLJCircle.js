function TopoHLJCircle(args){
	this.args=$.extend({
		draw:null
	},args);
	if(this.args.draw){
		this.init();
	}
};
TopoHLJCircle.prototype={
	init:function(){
		var x=560.642701169208,y=23.479363320402676;
		var g = this.args.draw;
		g.circle(6).fill("green").stroke({
			width:0
		}).translate(x-1.5,y-1.5).front();
		g.text("10").fill("white").attr({
			x:x,y:y
		});
	}
};