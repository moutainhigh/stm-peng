//自定义组件(消除editable的全局BUG)
function Canvas(dom){
	var _canvas = this;
	//画布的DOM承载容器	
	var width = $(dom).width()-10,height=$(dom).height();
	this.dom = dom;
	//画布对象
	this.raphael = Raphael(dom,width,600);
	this.raphael.width=width;
	this.raphael.height=600;
	this.data = "";
	this.allContainer = new Array();
	this.topContainer = new Array();
	//同步ID,保存allContainer里的ID,采用32位分段位ID+Index
	this.id_index = new Array();
	this.lines = new Array();
	this.config = null;
	this.clickNode = null;
	//是否可编辑
	this.isEditable = false;
	//序列
	this.seq = 0;
	//外部调用绘矩形
	this.rect = function(x,y){
		return new Rect(x,y,100,140);
	}
	this.childcontainer = function(x,y,w,h,ID){
		var con = new Container(x,y,w,h);
		con.ID = ID?ID:this.nextSeq();
		var index = _canvas.allContainer.push(con)-1;
		_canvas.id_index.push(con.ID<<16 | index);
		return con;
	}
	this.container = function(x,y,w,h,ID){
		var con = new Container(x,y,w,h);
		con.ID = ID?ID:this.nextSeq();
		_canvas.topContainer.push(con);
		var index = _canvas.allContainer.push(con)-1;
		_canvas.id_index.push(con.ID<<16 | index);
		return con;
	}
	//外部调用绘路径
	this.onLine = function(f,t){
		var line =  new Line(f,t);
		f.addLine(line);
		t.addLine(line);
		return line;
	}
	this.remove = function(id){
		if(_canvas.clickNode != null){
			_canvas.clickNode.remove();
			_canvas.clickNode = null;
		}
	}
	//通过条件查询
	this.get = function(ID){
		//先排序,线性数组才能做二分查找
		_canvas.id_index.sort();
		var index = search(_canvas.id_index,_canvas.id_index.length,ID);
		if(index != -1){
			//找到的元素
			return _canvas.id_index[index]&0xffff;
		}
		return -1;
	}
	//得到下一个ID
	this.nextSeq = function(){
		return ++this.seq;
	}
	//得到最新的ID
	this.currSeq = function(){
		return this.seq;
	}
	//得到当前点击的对象
	this.getNode = function(){
		return _canvas.clickNode;
	}
	this.setNode = function(node){
		if(_canvas.clickNode != null){
			if(_canvas.isEditable){
				_canvas.onLine(_canvas.clickNode,node);
			}
		}
		_canvas.clickNode = node;
	}
	this.getData = function(){
		var canvasObj = new Object();
		var containers = new Array();
		var lines = new Array();
		for(var i in _canvas.topContainer){
			var con = _canvas.topContainer[i];
			containers.push(con.getData());
		}
		for(var i in _canvas.lines){
			var line = _canvas.lines[i];
			lines.push(line.getData());
		}
		canvasObj.lines = lines;
		canvasObj.containers = containers;
		return JSON.stringify(canvasObj);
	}
	//还原数据
	this.restore = function(data){	
		_canvas.topContainer = new Array();
		_canvas.allContainer = new Array();
		_canvas.id_index = new Array();
		_canvas.lines = new Array();
		var containers = data.containers;
		var lines = data.lines;
		for(var i in containers){
			_canvas.container(containers[i].x,containers[i].y,containers[i].w,containers[i].h,containers[i].ID).set(containers[i]);
		}
		for(var i in lines){
			var from = _canvas.get(lines[i].from);
			var to = _canvas.get(lines[i].to);
			if(from!=-1 && to!=-1){
//				console.log("from"+from+" to"+to);
				_canvas.onLine(_canvas.allContainer[from],_canvas.allContainer[to]);
			}
		}
	}
	//设置是否可编辑
	this.editable = function(b){
		this.isEditable = b;
		return this;
	}
	//解二元一次线性方程组f:起点中心,t:终点中心
	function equation(f,t){
		
	}
	//二分查找
	function search(a,len,ele){
		ele = ele<<16;
		var low = 0;
		var high = len - 1;
		while(low <= high)
		{
			var middle = parseInt((low + high)/2);
			if((a[middle]>>16) == (ele>>16))
				return middle;
			//在左半边
			else if(a[middle] > ele)
				high = middle - 1;
			//在右半边
			else
				low = middle + 1;
		}
		//没找到
		return -1;
	}
	function connPoint(j,d){
		var c = d,
		//源中心
		e = {
			x: j.x + j.width / 2,
			 y: j.y + j.height / 2
		};
		//tanα
		var l = (e.y - c.y) / (e.x - c.x);
		l = isNaN(l) ? 0 : l;
		//tanβ
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
	/**组的概念
	Container:{
		margin:5,
		type:"container",(方便通过该type在容器中寻找相应的元素)		
		attr:{
			x:100,
			y:200,
			w:100,
			h:100,
			r:5
		}
		rect:[{rx",ry:,img:,"z-index":},{}]//相对位置
		img:[{src:"",type:"bg",rx:0,ry:0,width:0,height:0},
			{}	
		]
	
	
	
	
	
	}
	*/
	//连线对象(连线保存两边对象,对象保存自身的连线集合)
	function Line(f,t){
		var _l = this;
		//from container
		this.f = f;
		//to container
		this.t = t;
		//line 上面的编辑点
		this.dots = new Array();
		var path = _canvas.raphael.path(Raphael.format("M{0},{1}L{2},{3}", f.x+f.w/2, f.y+f.h/2, t.x+t.w/2, t.y+f.h/2))
			.attr({stroke: "green",
                   fill: "green",
                   "stroke-width": 3});
		_canvas.lines.push(this);
		this.drag = function(){
			//使用路径交叉算法
//			var pathStr = Raphael.format("M{0},{1}L{2},{3}", f.x+f.w/2, f.y+f.h/2, t.x+t.w/2, t.y+f.h/2);
//			var from = Raphael.pathIntersection(f.frame.attr("path"),pathStr);
//			var to = Raphael.pathIntersection(t.frame.attr("path"),pathStr);
//			if(from.length>0 &&to.length>0){
//				path.attr({path:Raphael.format("M{0},{1}L{2},{3}", from[0].x, from[0].y, to[0].x, to[0].y)})
//			}
			var F = f.getBBox();
			var T = t.getBBox();
			var from = connPoint(F,{ x: F.x + F.width / 2,y: F.y + F.height / 2})
			var to = connPoint(T,from);
			from = connPoint(F,to);
			path.attr({path:Raphael.format("M{0},{1}L{2},{3}", from.x, from.y, to.x, to.y)})
		}
		this.attr = function(attr){
			path.attr(attr);
		}
		this.edit = function(){
			
		}
		this.remove = function(){
			for(var i in f.lines){
				if(f.lines[i] == _l){
					delete f.lines[i];
				}
			}
			for(var i in t.lines){
				if(t.lines[i] == _l){
					delete t.lines[i];
				}
			}
			path.remove();
			for(var i in _l.dots){
				_l.dots[i].remove();
			}
			_l = _l.dots = null;
		}
		this.getData = function(){
			var line = new Object();
			line.from = f.ID;
			line.to = t.ID;
			return line;
		}
	}
	//容器对象(拖拽时是以容器为对象)
	function Container(x,y,w,h){
		var _c = this;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.ID = 0;
		//遮罩层边距
		this.margin = 0;
		//围绕container的路径
		this.frame = null;
		//点击和拖动的事件处理者
		this.handler = null;
		//鼠标相对容器x坐标
		this.rx = 0;
		//鼠标相对容器y坐标
		this.ry = 0;
		//容器相对父容器的X坐标
		this.RX = 0;
		//容器相对父容器的Y坐标
		this.RY = 0;
		//父容器
		this.parent = null;
		//容器里的图片
		this.imgs = new Array();
		//容器里的图形
		this.rects = new Array();
		//容器里的文字
		this.texts = new Array();
		//容器里的容器
		this.containers = new Array();
		//容器上连的线
		this.lines = new Array();	
		//生成handler和frame
		this.ready = function(arg1,arg2,arg3,arg4,arg5){
			_c.frame =_canvas.raphael.path(Raphael.format("M{0},{1}h{2}v{3}h{4}z", x, y, w, h, -w)).attr({"stroke-width": 0});
			var dx,dy,dw,dh;
			if(arg3){
				dx = arg1+x;
				dy = arg2+y;
				dw = arg3;
				dh = arg4;
			}else{
				dx = x;
				dy = y;
				dw = w;
				dh = h;
			}
			_c.handler = _canvas.raphael.rect(dx, dy, dw, dh).attr({stroke:"white",fill: "white","stroke-width": 0,"fill-opacity":0}).drag(
			function(x,y,dx,dy){
					_c.drag(x,y,dx,dy);//移动处理函数
			},function(x,y){
				eve("drag.start",_c,x,y);
				_c.rx=x-_c.handler.attr("x");_c.ry=y-_c.handler.attr("y")
			},function(){
				_c.rx=0;
				_c.ry=0;
				eve("drag.end");
/**				//消除路径交叉算法的BUG
				for(var i in _c.lines){
					_c.lines[i].drag();
					_c.margin = 0;
				}*/
			}).click(function(){
				//点击的时候改变点击的对象
				_canvas.setNode(_c);
			})
			//如果第五个参数为true,则显示边框
			if(arg5){
				_c.frame.attr({"stroke-width": 1,stroke:"white"});
				_c.handler.attr({"stroke-width": 1}).showFrame = arg5;
				_c.handler.rx = arg1;
				_c.handler.ry = arg2;
			}
			return _c;
		}
		//添加图片
		this.addImg = function(img,rx,ry){
			img.rx = rx;
			img.ry = ry;
			img.attr({x:x+rx,y:y+ry});
			_c.imgs.push(img);		
			return _c;
		}
		//添加图形
		this.addRect = function(rect,rx,ry){
			rect.rx = rx;
			rect.ry = ry;
			rect.attr({x:x+rx,y:y+ry});
			_c.rects.push(rect);
			return _c;
		}
		//添加连线
		this.addLine = function(line){
			_c.lines.push(line);
			_c.drag(0,0,_c.x,_c.y);
			return _c;
		}
		//添加文字(rx为容器x中心)
		this.addText = function(text,rx,ry){
			text.rx = rx;
			text.ry = ry;
			text.attr({x:x+rx,y:y+ry});
			_c.texts.push(text);
			return _c;
		}
		//添加其他容器
		this.addContainer = function(con,rx,ry){
			//RX、RY为容器相对于容器的相对位置
			con.RX = rx;
			con.RY = ry;
			con.attr({x:_c.x+rx,y:_c.y+ry});
			//设置子容器的父容器
			con.parent = _c;
			_c.containers.push(con);
			return _c;
		}
		//生成容器(解析json,调用生成函数),配置x,y,width,height,id这些
		this.set = function(json){
			var handler = json.handler;
			var texts = json.texts;
			var imgs = json.imgs;
			var rects = json.rects;
			var containers = json.containers;
			for(var i in texts){
				_c.addText(_canvas.raphael.text(0, 0,texts[i].text).white(),texts[i].rx,texts[i].ry)
			}
			for(var i in imgs){
				_c.addImg(_canvas.raphael.image(imgs[i].src, 0, 0, imgs[i].w, imgs[i].h),imgs[i].rx,imgs[i].ry);
			}
			for(var i in rects){
				_c.addRect(_canvas.raphael.rect(0, 0, rects[i].w, rects[i].h),rects[i].rx,rects[i].ry);
			}
			for(var i in containers){
				_c.addContainer(_canvas.childcontainer(containers[i].x,containers[i].y,containers[i].w,containers[i].h,containers[i].ID).set(containers[i]),containers[i].RX,containers[i].RY);
			}
			if(handler){
				_c.ready(handler.rx,handler.ry,handler.w,handler.h,true);
			}else{
				//全遮蔽
				_c.ready();
			}			
			return _c;
		}
		//通过相应type或者id查找元素
		this.get = function(){
			return _c.imgs[2];
		}
		//在该对象上绑定的业务数据
		this.bind = function(data){
			$(_c).data("data",data);
		}
		//获取绑定在该对象上的业务数据
		this.data = function(){
			return $(_c).data("data");
		}
		//得到容器的边界框
		this.getBBox = function(){
			return {x:_c.x,y:_c.y,x2:_c.x+_c.w,y2:_c.y+_c.h,width:_c.w,height:_c.h};
		}
		//所有元素的attr(主要是x,y)
		this.attr = function(attr){
			_c.drag(0,0,attr.x,attr.y);		
		}
		//拖动(容器调用容器里各元素的drag方法)
		this.drag = function(x,y,dx,dy){
			//容器运动后的位置
			var mx = dx-_c.rx;
			var my = dy-_c.ry;	
			//防止容器拖拽出父容器
			if(_c.parent != null){
				_c.RX = mx - _c.parent.x;
				_c.RY = my - _c.parent.y;
				if(mx<=_c.parent.x){
					_c.RX = 0;
					mx = _c.parent.x;
				}else if(mx>(_c.parent.x+_c.parent.w-_c.w)){
					_c.RX = _c.parent.x+_c.parent.w-_c.w;
					mx = _c.parent.x+_c.parent.w-_c.w;
				}
				if(my<=_c.parent.y){
					_c.RY = 0;
					my = _c.parent.y;
				}else if(my>(_c.parent.y+_c.parent.h-_c.h)){
					_c.RY = _c.parent.y+_c.parent.h-_c.h;
					my = _c.parent.y+_c.parent.h-_c.h;
				}				
			}
			_c.x = mx;
			_c.y = my;
			//矩形边框
			_c.handler.attr({x:mx,y:my})
			//路径边框
			_c.frame.attr({path:Raphael.format("M{0},{1}h{2}v{3}h{4}z", mx, my, w, h, -w)})
			for(var i in _c.imgs){
				_c.imgs[i].attr({x:mx+_c.imgs[i].rx,y:my+_c.imgs[i].ry});
			}
			for(var i in _c.rects){
				_c.rects[i].attr({x:mx+_c.rects[i].rx,y:my+_c.rects[i].ry})
			}
			for(var i in _c.texts){
				_c.texts[i].attr({x:mx+_c.texts[i].rx,y:my+_c.texts[i].ry});
			}
			for(var i in _c.containers){
				_c.containers[i].drag(x,y,mx+_c.containers[i].RX,my+_c.containers[i].RY);
			}
			for(var i in _c.lines){
				_c.lines[i].drag();
			}
		}
		//编辑大小的时候(暂不提供,相对位置算法还未定)
		this.edit = function(){
						
		}
		//删除
		this.remove = function(){			
			for(var i in _c.imgs){
				_c.imgs[i].remove();
			}
			for(var i in _c.rects){
				_c.rects[i].remove();
			}
			for(var i in _c.texts){
				_c.texts[i].remove();
			}
			for(var i in _c.containers){
				_c.containers[i].remove();
			}
			for(var i in _c.lines){
				_c.lines[i].remove();
			}
			//在父容器中删除
			if(_c.parent != null){
				var array = _c.parent.containers;
				for(var i in array){
					if(array[i] == _c){
						delete array[i];
					}
				}
			}
			_c.handler.remove();
			_c.frame.remove();
			//数组元素remove了,但数组还在,垃圾回收
			_c = _c.imgs = _c.rects = _c.texts = _c.containers = _c.lines = null;
		}
		this.getData = function(){
			var o = new Object();
			o.x = _c.x;
			o.y = _c.y;
			o.w = _c.w;
			o.h = _c.h;
			o.RX = _c.RX;
			o.RY = _c.RY;
			o.ID = _c.ID;
			o.imgs = new Array();
			o.texts = new Array();
			o.containers = new Array();
			o.rects = new Array();
			for(var j in _c.imgs){
				var img = _c.imgs[j];
				var oj = new Object();
				oj.src = img.attr("src");
				oj.type = img.type;
				oj.rx = img.rx;
				oj.ry = img.ry;
				oj.w = img.attr("width");
				oj.h = img.attr("height");
				o.imgs.push(oj);
			}
			for(var j in _c.texts){
				var text = _c.texts[j];
				var oj = new Object();
				oj.text = text.attr("text");
				oj.rx = text.rx;
				oj.ry = text.ry;
				oj.type = text.type;
				o.texts.push(oj);
			}
			for(var j in _c.rects){
				var rect = _c.rects[j];
				var oj = new Object();
				oj.rx = rect.rx;
				oj.ry = rect.ry;
				oj.w = rect.attr("width");
				oj.h = rect.attr("height");
				o.rects.push(oj);
			}
			for(var j in _c.containers){
				var container = _c.containers[j];
				o.containers.push(container.getData());
			}
			var handler = new Object();
			if(_c.handler.showFrame){
				o.handler = handler;
			}
			handler.rx = _c.handler.rx;
			handler.ry = _c.handler.ry;
			handler.w = _c.handler.attr("width");
			handler.h = _c.handler.attr("height");
			return o;
		}
	}
	
	
	/**
	//构造矩形()
	var Rect = function(x,y,w,h){
		var _this = this;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.type="Rect";
		this.id = this.type+_canvas.nextSeq();
		
		//添加图片(即存在报警,又存在email灯)
		this.addImg = function(){
			
		}
		this.img = function(){
			//显示图片
			var aa = _canvas.raphael.image("start_event_empty.png", x, y, w, h).drag(function(x,y,dx,dy){
				aa.attr({x:dx,y:dy})
			},function(){},function(){})
			return _this;
		};
		this.polic = function(){
			//显示报警灯
			alert("polic");
			return _this;
		};
		this.email = function(){
			//显示邮件通知
			alert("email");
			return _this;
		}
		this.text = function(){
			//显示文字
			alert("text")
			return _this;
		};
		this.edit = function(){
			//显示编辑框
			alert("edit")
			return _this;
		}
		//通过json数据配置所有东西
		this.set = function(data){
			
		}
		//里面存放的小矩形
		this.Rect = function(){
			
		}
		//外层包裹的容器对象
		this.rect = function(){
			_canvas.raphael.rect(x, y, w, h).attr({fill: "90-#fff-#C0C0C0",stroke: "#000","stroke-width": 1});
			return _this;
		}	
		this.remove = function(){
			_this.bind(null);
			_this = null;
		}
	}
	//构造路径
	var Path = function(f,t){
		this.f = f;
		this.t = t;
		//可用于联动报警
		this.set =  _canvas.raphael.set();
		this.path = _canvas.raphael.path(Raphael.format("M{0},{1}h{2}v{3}h{4}z", f.x, f.y, t.x, t.y, -t.x))
	}*/
	//键盘事件
    $(document).keydown(function(e) {
		//DELETE事件
		if (e.keyCode == 46) {
			_canvas.remove();
        }
		//Ctrl+Z
		if (e.keyCode == 90 && e.ctrlKey){
			alert("撤销功能完善中~");
		}
    });
}
(function(_$){
	//提供jquery方式调用
	_$.fn.canvas = function(data){
		return new Canvas(_$(this)[0]);
	}
})(jQuery)