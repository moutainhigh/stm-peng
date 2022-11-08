(function(){
	//画箭头
	Raphael.fn.arrow=function(x,y,s){
		s = s||8;
		var arrow = this.path(Raphael.format("M{0},{1}L{2},{3}L{4},{5}Z",x,y,x+s,y+s*.4,x+s,y-s*.4)).attr({fill:"#000"});
		//将平移到中心点再旋转
		arrow.transform("t"+(s*.5)+",0r0");
		return arrow;
	}
	Raphael.el.dotted = function(){
		return this.attr({"stroke-width":1,stroke:"#00B308","stroke-dasharray":"- "});
	}
})();