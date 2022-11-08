function TopoSvgElement(svg){
	this.svg=svg;
};
TopoSvgElement.prototype={
	createTag:function(tag){
		return $(document.createElementNS("http://www.w3.org/2000/svg",tag));
	},
	createStop:function(attr){
		var stop = this.createTag("stop");
		this.renderElement(stop,attr);
		return stop;
	},
	createRadialGradient:function(attr){
		var radia = this.createTag("radialGradient");
		var stops=attr.stops||[];
		delete attr["stops"];
		this.renderElement(radia,attr);
		for(var i=0;i<stops.length;i++){
			radia.append(this.createStop(stops[i]).get(0));
		}
		return radia;
	},
	mask:function(attr){
		var mask = this.createTag("mask");
		this.renderElement(mask,attr);
		return mask;
	},
	circle:function(attr){
		var circle = this.createTag("circle");
		this.renderElement(circle,attr);
		return circle;
	},
	g:function(attr){
		var g = this.createTag("g");
		this.renderElement(g,attr);
		return g;
	},
	use:function(attr){
		var use = this.createTag("use");
		this.renderElement(use,attr);
		return use;
	},
	renderElement:function(ele,attr){
		ele.attr(attr);
		ele.removeAttr("style");
		ele.css(attr.style||{});
	},
	getUuid:function(){
		return Raphael.createUUID();
	}
};