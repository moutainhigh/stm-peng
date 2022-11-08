(function(){
	//画箭头
	Raphael.fn.arrow=function(x,y,s){
		s = s||8;
		var arrow = this.path(Raphael.format("M{0},{1}L{2},{3}L{4},{5}Z",x,y,x+s,y+s*.4,x+s,y-s*.4));
		//将平移到中心点再旋转
		arrow.transform("t"+(s*.5)+",0r0");
		return arrow;
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
	//贝赛尔曲线
	Raphael.pathS=function(f,t){
		var a0 = f.x+f.w/2,
		a1 = f.y+f.h,
		a6 = t.x+t.w/2,
		a7 = t.y,
		a2 = a0,
		a3 = (a1+a7)/2,
		a4 = a6,
		a5 = a3;
		return Raphael.format("M{0},{1}C{2},{3} {4},{5} {6},{7}",a0,a1,a2,a3,a4,a5,a6,a7);
	}
	//直角路径
	Raphael.pathZ=function(f,t){
		var up = (f.y < t.y)?f:t,
			left = (f.x < t.x)?f:t,
			right = (left==f)?t:f,
			down = (up==f)?t:f,
			p0,p1,p2,p3,halfX,halfY;
		if(up.y+up.h < down.y){
			halfY = (up.y+up.h+down.y)/2;
			p0 = {x:up.x+up.w/2,y:up.y+up.h};
			p1 = {x:up.x+up.w/2,y:halfY};
			p2 = {x:down.x+down.w/2,y:halfY};
			p3 = {x:down.x+down.w/2,y:down.y};
		}else{
			halfX = (left.x+left.w+right.x)/2;
			p0 = {x:left.x+left.w,y:left.y+left.h/2};
			p1 = {x:halfX,y:left.y+left.h/2};
			p2 = {x:halfX,y:right.y+right.h/2};
			p3 = {x:right.x,y:right.y+right.h/2};		
		}
		if((up==f && halfY) || (left==f && halfX)){
			return Raphael.format("M{0},{1}L{2},{3}L{4},{5}L{6},{7}",
									p0.x,p0.y,p1.x,p1.y,p2.x,p2.y,p3.x,p3.y);
		}else{
			return Raphael.format("M{0},{1}L{2},{3}L{4},{5}L{6},{7}",
									p3.x,p3.y,p2.x,p2.y,p1.x,p1.y,p0.x,p0.y);
		}
	}
	//计算两点之间的角度
	Raphael.atan = function(x1,y1,x,y){
		if(x==x1&&y==y1) return 0;
		var x_ = x1-x,y_ = y-y1;
		var angle = Math.atan(y_/x_)*180/Math.PI;
		if(x_>=0){
			if(angle>0){
				angle = (angle-90)*-1+270;
			}else{
				angle = angle*-1;
			}
		}else{
			if(angle>0){
				angle = (angle-90)*-1+90;
			}else{
				angle = angle*-1+180;
			}
		}
		return angle;
	}
	//计算两个矩形外部连结点(矩形必须先进行Raphael.atan三角坐标系转换)
	Raphael.link = function(f,t){
		f.center = {x:f.x+f.w/2,y:f.y+f.h/2};
		t.center = {x:t.x+t.w/2,y:t.y+t.h/2};
		//斜率无穷大
		f.center.x = f.center.x==t.center.x?(f.center.x-.5):f.center.x;
		var fp,tp;
		//斜率,公式:y=kx+b x=(y-b)/k
		var k = (f.center.y-t.center.y)/(f.center.x-t.center.x),
			b = f.center.y-k*f.center.x;
		var f_angle = Raphael.atan(t.center.x,t.center.y,f.center.x,f.center.y);
		var t_angle = (f_angle+180)%360;
		if(f_angle>=f.angle[1] && f_angle<=f.angle[2]){//bottom
			fp = {x:(f.y+f.h-b)/k,y:f.y+f.h};
		}else if(f_angle>f.angle[2] && f_angle<=f.angle[3]){//left
			fp = {x:f.x,y:k*f.x+b};
		}else if(f_angle>f.angle[3] && f_angle<=f.angle[0]){//top
			fp = {x:(f.y-b)/k,y:f.y};
		}else{//right
			fp = {x:f.x+f.w,y:k*(f.x+f.w)+b};
		}
		if(t_angle>=t.angle[1] && t_angle<=t.angle[2]){//bottom
			tp = {x:(t.y+t.h-b)/k,y:t.y+t.h};
		}else if(t_angle>t.angle[2] && t_angle<=t.angle[3]){//left
			tp = {x:t.x,y:k*t.x+b};
		}else if(t_angle>t.angle[3] && t_angle<=t.angle[0]){//top
			tp = {x:(t.y-b)/k,y:t.y};
		}else{//right
			tp = {x:t.x+t.w,y:k*(t.x+t.w)+b};
		}
		return Raphael.format("M{0},{1}L{2},{3}",fp.x,fp.y,tp.x,tp.y);
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