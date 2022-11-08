function Canvas(dom){
	var _canvas = this;
	this.dom = dom;
	this.isEditable = true;
	this.raphael = null;
	this.containers = {};
	this.topContainers = {};
	this.lines = {};
	this.idMap = {};
	this.clickNode = null;
	this.memento = {};
	//抖动回退的参数
	this.backMemento = 5;
	this.isAuto = false;
	this.seq = 0;
	this.coe = 1;
	this.init = function(){
		this.raphael = Raphael(dom,$(dom).width(),$(dom).height());
		//100%
		this.setViewBox(0,0,$(dom).width(),$(dom).height(),false);	
	}
	this.setSize = function(w,h){
		this.raphael.setSize(w,h);
		//调用此方法会把视窗比例回退到100%(you can't invoke this function when the viewBox change,or back to initializ)
		this.setViewBox(0,0,w,h,false);
	}
	this.setViewBox = function(x,y,w,h,fit){
		this.raphael.setViewBox(x,y,w,h,fit);
		this.coe = w/this.raphael.width;
	}	
	this.container = function(x,y,w,h,ID){
		if(!this.isEditable && !this.isAuto) return;
		this.creatMemento();		
		var con = new Container(x,y,w,h);
		this.containers[con.ID] = this.topContainers[con.ID] = con;
		this.idMap[ID] = con.ID;
		return con;
	}
	this.creatMemento = function(){		
		if(this.isAuto || !this.isEditable) return;
		var data = this.getData();
		var history = this.memento;
		this.memento = {};
		this.memento.prev = eval("("+data+")");
		this.memento.history = history;
	}
	this.prevMemento = function(){
		if(this.isAuto || !this.isEditable) return;
		this.memento = this.memento.history;
	}
	this.onLine = function(f,t,r){
		if(!this.isEditable && !this.isAuto) return;
		if(!r){	
			if(f==t) return;
			var finded = sizeof(f.lines)>=sizeof(t.lines)?t:f;
			var target = finded==f?t:f;
			for(var i in finded.lines)
				if(finded.lines[i].f==target || finded.lines[i].t==target) return;
		}
		this.creatMemento();
		var line =  new Line(f,t);
		f.addLine(line);
		t.addLine(line);
		this.lines[line.ID] = line;		
		return line;
	}
	this.remove = function(id){
		if(!this.isEditable) return;
		if(this.clickNode != null){
			this.creatMemento();
			this.clickNode.remove();
			this.clickNode = null;			
		}
	}
	this.nextSeq = function(){
		return ++this.seq;
	}
	this.click = function(node){
		this.clickNode = node;
	}
	this.getData = function(){
		var canvasObj = new Object();
		var containers = new Array();
		var lines = new Array();
		for(var i in this.topContainers){
			var con = this.topContainers[i];
			containers.push(con.getData());
		}
		for(var i in this.lines){
			var line = this.lines[i];
			lines.push(line.getData());
		}
		canvasObj.lines = lines;
		canvasObj.containers = containers;
		return JSON.stringify(canvasObj);
	}
	this.restore = function(data){
		this.isAuto = true;
		this.topContainers = {};
		this.containers = {};
		this.lines = {};
		this.idMap = {};
		this.clickNode = null;
		this.raphael.clear();
		var containers = data.containers;
		var lines = data.lines;
		for(var i in containers){
			this.container(containers[i].x,containers[i].y,containers[i].w,containers[i].h,containers[i].ID).set(containers[i]);
		}
		for(var i in lines){
			var line = this.onLine(this.containers[this.idMap[lines[i].from]],this.containers[this.idMap[lines[i].to]]);
			if(line) line.path.attr(lines[i].attr);
		}	
		this.isAuto = false;
	}
	function sizeof(a){
		var s = 0;
		for(var i in a)s++;
		return s;
	}
	function connPoint(j,d){
		var c = d,
		e = {
			x: j.x + j.width / 2,
			 y: j.y + j.height / 2
		};
		var l = (e.y - c.y) / (e.x - c.x);
		l = isNaN(l) ? 0 : l;
		var k = j.height / j.width;
		var h = c.y < e.y ? -1 : 1,
		f = c.x < e.x ? -1 : 1,
		g,
		i;
		if (Math.abs(l) > k && h == -1) {
			g = e.y - j.height / 2;
			i = e.x + h * j.height / 2 / l
		} else {
			if (Math.abs(l) > k && h == 1) {
				g = e.y + j.height / 2;
				i = e.x + h * j.height / 2 / l
			} else {
				if (Math.abs(l) < k && f == -1) {
					g = e.y + f * j.width / 2 * l;
					i = e.x - j.width / 2
				} else {
					if (Math.abs(l) < k && f == 1) {
						g = e.y + j.width / 2 * l;
						i = e.x + j.width / 2
					}
				}
			}
		}
		return {
			x: i,
			y: g
		}		
	}
	function Line(f,t){
		var _l = this;
		this.f = f;
		this.t = t;
		this.ID = "Line"+_canvas.nextSeq();
		var format = "M{0},{1}L{2},{3}";
		var point = Point();
		this.path = _canvas.raphael.path(Raphael.format(format, point.from.x, point.from.y, point.to.x, point.to.y))
			.attr({stroke: "white","stroke-width": 0.3}).click(
				function(){
					_canvas.click(_l);
				})
		this.animate = function(){
			this.path.attr({path:Raphael.format(format, point.from.x, point.from.y, point.from.x, point.from.y)});
			this.path.animate({path:Raphael.format(format, point.from.x, point.from.y, point.to.x, point.to.y)},2000);
			return this;
		}
		this.drag = function(){
			var p = Point();
			this.path.attr({path:Raphael.format(format, p.from.x, p.from.y, p.to.x, p.to.y)});
			return this;
		}
		function Point(){
			var F = f.getBBox();
			var T = t.getBBox();
			var from = connPoint(F,{ x: F.x + F.width / 2,y: F.y + F.height / 2})
			var to = connPoint(T,from);
			from = connPoint(F,to);
			return {from:from,to:to};
		}
		this.remove = function(){
			delete f.lines[this.ID];
			delete t.lines[this.ID];
			delete _canvas.lines[this.ID];
			this.path.remove();
			_l = point = null;
		}
		this.getData = function(){
			var line = {};
			line.from = f.ID;
			line.to = t.ID;
			line.ID = this.ID;
			line.attr = this.path.attr();
			return line;
		}
	}
	
	function Container(x,y,w,h){
		var _c = this;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.ID = "Container"+_canvas.nextSeq();
		this.ox = 0;
		this.oy = 0;
		this.rx = 0;
		this.ry = 0;
		this.RX = 0;
		this.RY = 0;
		this.parent = null;
		this.imgs = new Array();
		this.rects = new Array();
		this.texts = new Array();
		this.containers = {};
		this.lines = {};
		this.handler = null;
		this.frame = _canvas.raphael.path(Raphael.format("M{0},{1}h{2}v{3}h{4}z", x, y, w, h, -w)).attr({"stroke-width": 0,"stroke":"white"});
		this.Handler = function(){			
			if(this.handler !=null){
				var history = this.handler;
				this.handler = history.clone();
				this.setHandler({rx:history.rx,ry:history.ry});
				history.remove();
			}else{
				this.handler = _canvas.raphael.rect(x, y, w, h);
				this.setHandler({rx:0,ry:0,attr:{"stroke-width": 0,"fill-opacity":0,fill:"#fff"}});
			}
			this.handler.drag(
				function(x,y,dx,dy){
					_c.drag(x,y,dx*_canvas.coe,dy*_canvas.coe);
				},function(x,y){		
					_canvas.creatMemento();
					_c.rx=x*_canvas.coe-_c.x;
					_c.ry=y*_canvas.coe-_c.y;
					_c.ox = x;
					_c.oy = y;
				},function(e){
					if(Math.abs(_c.x+_c.rx-_c.ox)<_canvas.backMemento && Math.abs(_c.y+_c.ry-_c.oy)<_canvas.backMemento){
						_canvas.prevMemento();
					}
					_c.rx=_c.ry=0;			
				}).click(function(){
					_canvas.click(_c);
				}
			)
			return this;
		}
		this.setHandler = function(j){
			this.handler.rx = j.rx;
			this.handler.ry = j.ry;
			this.handler.attr(j.attr);
			this.handler.attr({x:this.x+j.rx,y:this.y+j.ry});
			return this;
		}
		this.addImg = function(img,rx,ry,attr){
			img.rx = rx;
			img.ry = ry;
			img.attr({x:this.x+rx,y:this.y+ry});
			img.attr(attr);
			this.imgs.push(img);		
			return this.Handler();
		}
		this.addRect = function(rect,rx,ry,attr){
			rect.rx = rx;
			rect.ry = ry;
			rect.attr({x:this.x+rx,y:this.y+ry});
			rect.attr(attr);
			this.rects.push(rect);
			return this.Handler();
		}
		this.addText = function(text,rx,ry,attr){
			text.rx = rx;
			text.ry = ry;
			text.attr({x:this.x+rx,y:this.y+ry});
			text.attr(attr);
			this.texts.push(text);
			return this.Handler();
		}
		this.addLine = function(line){
			this.lines[line.ID] = line;
			this.drag(0,0,this.x,this.y);
			return this;
		}
		this.addContainer = function(con,RX,RY){
			con.RX = RX;
			con.RY = RY;
			con.move(this.x+RX,this.y+RY);
			con.parent = this;
			this.containers[con.ID] = con;
			delete _canvas.topContainers[con.ID];
			return this;
		}
		this.set = function(json){
			var handler = json.handler;
			var frame = json.frame;
			var texts = json.texts;
			var imgs = json.imgs;
			var rects = json.rects;
			var containers = json.containers;		
			for(var i in texts){
				this.addText(_canvas.raphael.text(0, 0,texts[i].text),texts[i].rx,texts[i].ry,texts[i].attr)
			}
			for(var i in imgs){
				this.addImg(_canvas.raphael.image(imgs[i].src, 0, 0, imgs[i].w, imgs[i].h),imgs[i].rx,imgs[i].ry,imgs[i].attr);
			}
			for(var i in rects){
				this.addRect(_canvas.raphael.rect(0, 0, rects[i].w, rects[i].h),rects[i].rx,rects[i].ry,rects[i].attr);
			}
			for(var i in containers){
				this.addContainer(_canvas.container(containers[i].x,containers[i].y,containers[i].w,containers[i].h,containers[i].ID).set(containers[i]),containers[i].RX,containers[i].RY);
			}
			if(handler){
				this.setHandler(handler);
			}		
			if(frame){
				this.frame.attr(frame.attr);
			}
			this.bind(json.data);
			return this;
		}
		this.get = function(){
			return this;
		}
		this.bind = function(data){
			$(this).data("data",data);
		}
		this.data = function(){
			return $(this).data("data");
		}
		this.getBBox = function(){
			return {
				x:this.x,
				y:this.y,
				x2:this.x+this.w,
				y2:this.y+this.h,
				width:this.w,
				height:this.h
			};
		}
		this.move = function(x,y){
			this.drag(0,0,x,y);	
			return this;
		}
		this.drag = function(x,y,dx,dy,callback){
			if(!_canvas.isEditable && !_canvas.isAuto) return;
			var mx = dx-this.rx;
			var my = dy-this.ry;
			if(this.parent != null){
				this.RX = mx - this.parent.x;
				this.RY = my - this.parent.y;
				if(mx<=this.parent.x){
					this.RX = 0;
					mx = this.parent.x;
				}else if(mx>(this.parent.x+this.parent.w-this.w)){
					this.RX = this.parent.x+this.parent.w-this.w;
					mx = this.parent.x+this.parent.w-this.w;
				}
				if(my<=this.parent.y){
					this.RY = 0;
					my = this.parent.y;
				}else if(my>(this.parent.y+this.parent.h-this.h)){
					this.RY = this.parent.y+this.parent.h-this.h;
					my = this.parent.y+this.parent.h-this.h;
				}				
			}
			this.x = mx;
			this.y = my;
			this.handler.attr({x:mx+this.handler.rx,y:my+this.handler.ry})
			this.frame.attr({path:Raphael.format("M{0},{1}h{2}v{3}h{4}z", mx, my, this.w, this.h, -this.w)})
			for(var i in this.imgs){
				this.imgs[i].attr({x:mx+this.imgs[i].rx,y:my+this.imgs[i].ry});
			}
			for(var i in this.rects){
				this.rects[i].attr({x:mx+this.rects[i].rx,y:my+this.rects[i].ry})
			}
			for(var i in this.texts){
				this.texts[i].attr({x:mx+this.texts[i].rx,y:my+this.texts[i].ry});
			}
			for(var i in this.containers){
				this.containers[i].drag(x,y,mx+this.containers[i].RX,my+this.containers[i].RY);
			}
			for(var i in this.lines){
				this.lines[i].drag();
			}
			if(callback)callback(dx,dy);			
			return this;
		}
		this.edit = function(){
					
		}
		this.remove = function(){
			_c.handler.remove();
			_c.frame.remove();
			for(var i in this.imgs){
				this.imgs[i].remove();
			}
			for(var i in this.rects){
				this.rects[i].remove();
			}
			for(var i in this.texts){
				this.texts[i].remove();
			}
			for(var i in this.containers){
				this.containers[i].remove();
			}
			for(var i in this.lines){
				this.lines[i].remove();
			}
			//topContainers
			if(this.parent==null){
				delete _canvas.topContainers[this.ID];
			}else{
				delete this.parent.containers[this.ID];
			}
			delete _canvas.containers[this.ID];
			//let gc do it work
			this.imgs = this.rects = this.texts = this.containers = this.lines = null;
		}
		this.getData = function(){
			var o = {};
			o.x = this.x;
			o.y = this.y;
			o.w = this.w;
			o.h = this.h;
			o.RX = this.RX;
			o.RY = this.RY;
			o.ID = this.ID;
			o.imgs = new Array();
			o.texts = new Array();
			o.rects = new Array();
			o.containers = new Array();			
			for(var j in this.imgs){
				var oj = {};
				var img = this.imgs[j];
				oj.rx = img.rx;
				oj.ry = img.ry;
				oj.attr = img.attr();
				o.imgs.push(oj);
			}
			for(var j in this.texts){
				var oj = {};
				var text = this.texts[j];
				oj.rx = text.rx;
				oj.ry = text.ry;
				oj.attr = text.attr();
				o.texts.push(oj);
			}
			for(var j in this.rects){
				var oj = {};
				var rect = this.rects[j];
				oj.rx = rect.rx;
				oj.ry = rect.ry;
				oj.attr = rect.attr();
				o.rects.push(oj);
			}
			for(var j in this.containers){
				var container = this.containers[j];
				o.containers.push(container.getData());
			}
			var frame = {};
			frame.attr = this.frame.attr();
			var handler = {};			
			handler.attr = this.handler.attr();			
			handler.rx = this.handler.rx;
			handler.ry = this.handler.ry;
			o.frame = frame;
			o.handler = handler;
			o.data = this.data();
			return o;
		}
		this.Handler();
	}
	this.init();
    $(document).keydown(function(e) {
		if (e.keyCode == 46) {
			_canvas.remove();
        }
		if (e.keyCode == 90 && e.ctrlKey){
			if(!_canvas.memento.prev) return;
			_canvas.raphael.clear();
			_canvas.restore(_canvas.memento.prev);
			_canvas.memento = _canvas.memento.history;
		}
    });
}
(function(_$){
	_$.fn.canvas = function(){
		return new Canvas(_$(this)[0]);
	}
})(jQuery)