function TopoMapEffect(draw){
	this.draw=draw;
	this.init();
};
TopoMapEffect.prototype={
	init:function(){
		//线条光晕效果
		this.lineEffect = this.lineGlow();
	},
	lineGlow:function(){
		var draw = this.draw;
		var gra1 = draw.gradient('radial', function(stop) {
			stop.at({ offset:"0%",color:"white",opacity:1});
			stop.at({ offset:"10%",color:"white",opacity:0.95});
			stop.at({ offset:"15%",color:"white",opacity:0.9});
			stop.at({ offset:"30%",color:"white",opacity:0.35});
			stop.at({ offset:"100%",color:"white",opacity:0});
		});
		var mask = draw.mask().add(draw.circle(20).attr({cx:0,cy:0}).style({fill:gra1}));
		var group = draw.group();
		group.circle(20).attr({
			cx:0,cy:0
		}).maskWith(mask);
		var defs = draw.defs();
		defs.add(group);
		return group;
	}
};