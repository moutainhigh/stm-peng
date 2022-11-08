(function(){
	//画箭头
	Raphael.fn.arrow=function(f,s,t,attr){
		var p = "M" + f.x + " " + f.y + "L" + s.x + " " + s.y + "L" + t.x + " " + t.y + "z";
		return this.path(p).attr(attr);
	}
	//一个有动画效果的报警灯
	Raphael.fn.polic=function(x,y,r,attr){
		var time = 50;
		var circle = null;
		//stroke-width为r的2倍
		var anim = Raphael.animation({stroke: "red", "stroke-opacity": 0,r:r+50,"stroke-width": 100}, 800,"linear",function(){
			if(--time<=0){
				circle.remove();
			}				
		})
		this.image("start_event_empty.png",x-r,y-r,2*r,2*r).rotate(150);
		circle = this.circle(x, y, r).animate(anim.repeat(time));
	}
	//S型路径
	Raphael.fn.pathS=function(x){
		alert("I'm going draw pathS");
		//this.path()
		return this;
	}
	//直角路径
	Raphael.fn.pathZ=function(x){
		alert("I'm going draw pathZ");
		return this;
	}
	//改变图标
	Raphael.el.red = function () {
		return this.attr({stroke:"#f00"});
	}
	//改变图标
	Raphael.el.green = function () {
		return this.attr({fill: "green",stroke:"green"});
	}
	Raphael.el.white = function () {
		return this.attr({fill: "#fff",stroke:"#fff"});
	}
	//转盘
	Raphael.fn.turn = function(){
		var img = this.image("start_event_empty.png",500,100,250,250);
		img.drag(function(dx,dy,x,y){
			img.transform("r"+Raphael.angle(x,y,img.attr("x"),img.attr("y")))
		},function(){},function(){})
		
	}
})();