//(function(){
//画布对象
function Canvas(dom){
	this.dom = dom;
	this.containers = {};
	this.topContainers = {};
	this.lines = {};
	this.idMap = {};
	this.seq = 0;
	this.coe = 1;
	this.coeH = 1;
	this.raphael = Raphael(dom,$(dom).width(),$(dom).height());
	this.node = null;
	this.editable = true;
	this.isAuto = false;
	this.ratioW = null;
	this.ratioH = null;
	this.ratio = 1;
	this.adaptRatio = null;
	this.svgBg = null;
}
Canvas.prototype = {
	setSize:function(w,h){
		this.raphael.setSize(w,h);
		this.setViewBox(0,0,w,h,false);
	},
	setViewBox:function(x,y,w,h,fit){
		this.raphael.setViewBox(x,y,w,h,fit);
		//this.coe = w/this.raphael.width;
		//this.coeH = h/this.raphael.height;
	},
	container:function(x,y,w,h,ID){
		var con = new Container(x,y,w,h,this);
		this.containers[con.ID] = this.topContainers[con.ID] = con;
		this.idMap[ID] = con.ID;
		this.event("container",con.ID);
		return con;
	},
	onLine:function(f,t,o){
		//不能连接本身
		if(f.ID==t.ID) return;
		for(var i in f.lines){
			var l = f.lines[i];
			//已经连线的不能重复连
			if(l.f.ID==t.ID ||l.t.ID==t.ID) return;
		}
		var line = new Line(f,t,o,this);
		f.addLine(line);
		t.addLine(line);
		this.lines[line.ID] = line;
		this.idMap[o.ID] = line.ID;
		this.event("onLine",line.ID);
		return line;
	},
	nextSeq:function(){
		return ++this.seq;
	},
	click:function(o){
		this.node = o;
	},
	remove:function(){
		if(!this.editable) return;
		if(this.node){
			if(this.node instanceof Container){
				if(this.node.data.type=='bizMain'){
					alert('不能删除业务应用!');
					return;
				}
				var lines = [];
				for(var i in this.node.lines){
					lines.push(this.node.lines[i].getData());
				}
				this.event("removeContainer",{"lines":lines,"container":this.node.getData()});
			}else{
				//删除线条
				this.event("removeLine",this.node.getData());
			}
			this.node.remove();
		}
	},
	beginAuto:function(){//开启自动模式
		this.isAuto = true;
	},
	endAuto:function(){
		this.isAuto = false;
	},
	event:function(fname,obj){
		if(this.isAuto){
			//console.log("restore,don't backup"+fname);
		}
		//console.log(obj);
		//事件,用于备份和还原
	},
	getJson:function(){
		var data = {};
		var containers = [];
		var lines = [];
		for(var i in this.topContainers){
			containers.push(this.topContainers[i].getData());
		}
		for(var i in this.lines){
			lines.push(this.lines[i].getData());
		}
		data.lines = lines;
		data.containers = containers;
		return data;
	},
	getData:function(){
		return JSON.stringify(this.getJson());
	},
	adapt:function(param){
		var canvas = this;
		var edit = new Edit(canvas);
		var unit,service,app,physics;
		var area = new Array();
		var bg = this.bg;
		for(var i in canvas.topContainers){
			var con = canvas.topContainers[i];
			switch(con.data.type){
				case "unit":
					unit = con;
					break;
				case "service":
					service = con;
					break;
				case "app":
					app = con;
					break;
				case "physics":
					physics = con;
					break;
				default :
					area.push(con);
					break;
			}
			if(con.data.type!="rect" && con.data.type!="rectcircle" && con.data.type!="circle" && con.data.type!="circlecl" && con.data.type!="quyupic"){
				con.click = function(){
					if(edit){
						edit.setCon(this);
						//没使用的编辑点
						var unuse = ["lt","tr","r","rb","bl","l","t"];
						for(var i=0;i<unuse.length;i++){
							edit.P[unuse[i]].hide();
						}
						edit.edit = function(bbox){
							getLaneEdit(bbox,this,unit,service,app,physics);
						};
					}
				}
				//不让拖动
				con.proxyDrag = function(dx,dy,x,y){}
			}
		}
		
		//宽度自适应
		var dom_width = $(canvas.dom).width();
		var dom_height = $(canvas.dom).height();
		//debugger
		var ratio = param.adaptRatio;
		var carsoul = param.carsoul;
		if(!ratio){
			ratio = dom_width/dom_height;
		}
//		if(carsoul){
//			var dom_width = carsoul.width;
//			var dom_height = carsoul.height;
//		}
		canvas.raphael.setSize(dom_width,dom_height);
		
		//修改线条宽度
		if(dom_width < (bg.w/2)){
			for(var key in canvas.lines){
    			canvas.lines[key].attr({"stroke-width":1});
    		}
		}
		
		var old_new = null,width = null,textWidthSource = null,textWidth = null;
		if(isIeVersionLt9()){
			canvas.setViewBox(0,0,dom_width,dom_height);
			canvas.svgBg = canvas.raphael.image(bg.src,0,0,dom_width,dom_height).toBack();
			old_new = (physics.y+physics.h)/dom_height;
			width = bg.w;
			textWidth = width*.12;//bg.w*.12;
			
		}else{
			canvas.setViewBox(0,0,bg.w,bg.h);
			var handled_canvasH = bg.w/ratio;
			//如果根据宽高比得出的height比dom,height高,则按dom,height重新计算宽度
			if(handled_canvasH < bg.h){
				//按宽度缩放
				canvas.ratio = canvas.ratioH;
				var newWidth = bg.h*ratio;
				canvas.svgBg = canvas.raphael.image(bg.src,0,0,newWidth,bg.h).toBack();
				//canvas.setViewBox(0,0,newWidth,bg.h);
				old_new = 1;
				width = newWidth;
				textWidth = bg.w*.12;
			}else{
				//canvas.setViewBox(0,0,bg.w,bg.h);
				//按宽度缩放
				canvas.ratio = canvas.ratioW;
				canvas.svgBg = canvas.raphael.image(bg.src,0,0,bg.w,handled_canvasH).toBack();
				old_new = (physics.y+physics.h)/handled_canvasH;
				width = bg.w;
				textWidth = width*.12;
			}
			
		}
		expand(edit);
		var beforeY = physics.y;
		var beforeX = physics.x;
		
		unit.click();
		edit.edit({x:textWidth,y:1,x1:width,y1:unit.h/old_new});
		service.click();
		edit.edit({x:textWidth,y:unit.y+unit.h,x1:width,y1:unit.y+unit.h+service.h/old_new});
		app.click();
		edit.edit({x:textWidth,y:service.y+service.h,x1:width,y1:service.y+service.h+app.h/old_new});
		physics.click();
		edit.edit({x:textWidth,y:app.y+app.h,x1:width,y1:app.y+app.h+physics.h/old_new});
		
		var afterY = physics.y;
		var afterX = physics.x;
		
		for(var i = 0 ; i < area.length ; i ++){
			area[i].drag(0,0,area[i].x + (afterX - beforeX),area[i].y + (afterY - beforeY));
		}
		
		edit.hide();
		if(!canvas.editable){
			Container.prototype.dblclick = function(){};
			unit.click = function(){}
			service.click = function(){}
			app.click = function(){}
			physics.click = function(){}
			
			hideChdEdit(unit);
			hideChdEdit(service);
			hideChdEdit(app);
			hideChdEdit(physics);
		}
	},
	restore:function(data,param){
		var that = this;
		var dom_width = $(that.dom).width();
		var dom_height = $(that.dom).height();
		//console.log($(that.dom).width()+','+$(that.dom).height());
		//console.log(data.bg);
		if(data.bg){
			var bg = data.bg;
			var client$obj = $(that.dom);
			var clientWidth = client$obj.width(),clientHeight = client$obj.height();
			
			this.ratioW = clientWidth/bg.w;
			this.ratioH = clientHeight/bg.h;
			//this.ratioW = 1;
			//this.ratioH = 1;
			
			if(data.id){
				bg['bizId']=data.id;
			}
			this.bg = bg;
			
		}
		
		this.beginAuto();
		this.topContainers = {};
		this.containers = {};
		this.lines = {};
		this.idMap = {};
		this.node = null;
		this.raphael.clear();
		
		var ratioWTemp = this.ratioW,ratioHTemp = this.ratioH;
		for(var i=0;i<data.containers.length;i++){
			var o = data.containers[i];
			if(o.data){
				switch(o.data.type){
				case "unit":
				case "service":
				case "app":
				case "physics":
					
//					o.x = o.x*ratioWTemp;
//					o.y = o.y*ratioHTemp;
//					o.w = o.w*ratioWTemp;
//					o.h = o.h*ratioHTemp;
//					o.RX = o.RX*ratioWTemp;
//					o.RY = o.RY*ratioHTemp;
					
					//修改文字大小
//					if(dom_width < (bg.w/2)){
//						var textArr = o.texts[0];
//						//textArr.attr({"font-size":5});
//						textArr['attr']['font-size'] = '5%';
//						var test = 0;
//					}
					
					break;
					
				default :
//					o.x = o.x*ratioWTemp;
//					o.y = o.y*ratioHTemp;
//					o.w = o.w*ratioWTemp;
//					o.h = o.h*ratioHTemp;
					
					break;
				}
			}
			this.container(o.x,o.y,o.w,o.h,o.ID).set(o);
		}
		
		for(var i=0;i<data.lines.length;i++){
			var o = data.lines[i];
			var f = this.containers[this.idMap[o.from]];
			var t = this.containers[this.idMap[o.to]];
			var line = this.onLine(f,t,o);
		}
		//不影响备忘的使用,保证ID的唯一性
		this.idMap = {};
		this.endAuto();
		
		if(param['ifAdapt'] && true==param['ifAdapt']){
			//ie8编辑泳道有bug
			if(isIeVersionLt9() && param.carsoul){
				that.raphael.setSize(param.carsoul.width,param.carsoul.height);
				that.setViewBox(0,0,bg.w-100,bg.h);
				that.svgBg = that.raphael.image(bg.src,0,0,bg.w,bg.h+300).toBack();
			}else{
				this.adapt(param);
			}
			
		}
		
		//将资源和业务单位置顶
	    for(var name in this.containers){       
	       if(this.containers[name].data.type=='resource'||this.containers[name].data.type=='bizDep'
	    	   ||this.containers[name].data.type=='bizSer'||this.containers[name].data.type=='bizMain'){
		   		for(var i=0;i<this.containers[name].texts.length;i++){
		   			this.containers[name].texts[i].toFront();
				}
				for(var i=0;i<this.containers[name].imgs.length;i++){
					this.containers[name].imgs[i].toFront();
				}
				for(var i=0;i<this.containers[name].rects.length;i++){
					this.containers[imgs].rects[i].ele.toFront();
				}
	       }
	    }  
		
		if(param.id){
			oc.resource.loadScript('resource/module/business-service/js/refresh.js',function(){
				//刷新业务及资源状态
				oc.business.service.canvas.update(param.id,that,$(that.dom).find("svg"));
			});
		}
		
		
	}
}
//连线对象
function Line(f,t,o,C){
	this.f = f;
	this.t = t;
	this.C = C;
	this.ID = "Line"+C.nextSeq();
	//线条类型(折线、贝塞尔曲线、直线)
	this.type = "L";
	//线条粗细
	this.size = 1;
	//是否有箭头
	this.arrow = false;
	//箭头对象
	this.arrowObj = new Arrow(8,50);
	this.path = [];
	this.init(o);
}
Line.prototype = {
	init:function(o){
		if(o){
			if(o.type) this.type = o.type;
			if(o.size) this.size = o.size;
			if(o.arrow) this.arrow = o.arrow;
			if(o.attr) this.old_attr = o.attr;
		}
		this.create();
	},
	create:function(){
		var T = this;
		this.path.push(this.C.raphael.path(""));
		this.path[0].click(function(){T.click();});
		this.pathToClick = this.path[0].clone().attr({"stroke-width":5,"stroke-opacity":0}).click(function(){T.click();});
		if(this.arrow) this.path.push(this.C.raphael.path(""));
		if(this.old_attr) this.attr(this.old_attr);
		this.drag();
		return this;
	},
	click:function(){
		this.C.click(this);
	},
	drag:function(){

		if(this.type=="S"){
			this.path[0].attr({path:Util.pathS(this.f,this.t)});
		}else if(this.type=="Z"){
			var p = Util.pathZ(this.f,this.t);
			var path = "M"+p.x+","+p.y+"L"+p.x1+","+p.y1+"L"+p.x2+","+p.y2+"L"+p.x3+","+p.y3;
			this.path[0].attr({path:path});
			this.path[1] && this.path[1].attr({path:this.arrowObj.rotate(p.x3,p.y3,Util.atan(p.x3,p.y3,p.x2,p.y2)+180)});
		}else if(this.type=="C"){
			this.path[0].attr({path:Util.pathC(this.f,this.t)});
		}else{
			var o = Util.pathL(this.f,this.t);
			this.path[0].attr({path:o.path});
			this.pathToClick.attr({path:o.path});
			this.path[1] && this.path[1].attr({path:this.arrowObj.rotate(o.tx,o.ty,o.r)});
		}
	},
	switchArrowType:function(type){
		if(type == '1'){
			//无箭头
			if(this.path[1]){
				this.path[1].remove();
				this.path.splice(1,1);
				this.arrow = false;
			}
		}else if(type == '2'){
			//有箭头
			if(!this.path[1]){
				var p = Util.pathZ(this.f,this.t);
				this.path.push(this.C.raphael.path(""));
				this.path[1].attr({path:this.arrowObj.rotate(p.x3,p.y3,Util.atan(p.x3,p.y3,p.x2,p.y2)+180)});
				this.attr(this.old_attr);
				this.arrow = true;
			}
		}
	},
	remove:function(){
		delete this.f.lines[this.ID];
        delete this.t.lines[this.ID];
        delete this.C.lines[this.ID];
        this.path[0].remove();
        this.pathToClick.remove();
		this.path[1] && this.path[1].remove();
	},
	getData:function(){
		var line = {};
		var attr = this.path[0].attr();
        line.from = this.f.ID;
        line.to = this.t.ID;
        line.ID = this.ID;
		line.type = this.type;
		line.size = this.size;
		line.arrow = this.arrow;		
        line.attr = {stroke:attr.stroke,"stroke-width":attr["stroke-width"],"stroke-opacity":attr["stroke-opacity"],"stroke-dasharray":attr["stroke-dasharray"]};
        return line;
	},
	attr:function(attr){
		if(!attr) return this.path[0].attr();
		this.path[0].attr(attr);
		this.path[1] && this.path[1].attr({fill:attr.stroke,stroke:attr.stroke,"stroke-opacity":attr["stroke-opacity"]});
	}
}
function Container(x,y,w,h,C){
	this.C = C;
	this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.ID = "Container"+C.nextSeq();
    //记录鼠标拖动前的相对位置
    this.rx = 0;
    this.ry = 0;
    //记录对于父容器的相对位置
    this.RX = 0;
    this.RY = 0;
    this.parent = null;
    this.imgs = [];
    this.rects =[];
    this.texts = [];
    this.containers = {};
    this.lines = {};
    this.data = {};
	this.init();
}
Container.prototype = {
	init:function(){//初始化角度
		this.center = {x:this.x+this.w/2,y:this.y+this.h/2};
		this.angle = [Util.atan(this.x+this.w,this.y,this.center.x,this.center.y),Util.atan(this.x+this.w,this.y+this.h,this.center.x,this.center.y),
							 Util.atan(this.x,this.y+this.h,this.center.x,this.center.y),Util.atan(this.x,this.y,this.center.x,this.center.y)];
		
	},
	addImg:function(o,rx,ry,isDrag){
		o.rx = rx;
		o.ry = ry;
		o.isDrag = isDrag;
		o.attr({x:this.x+rx,y:this.y+ry});
		this.dragabble(o,isDrag);
		this.imgs.push(o);
		return this;
	},
	addText:function(o,rx,ry,isDrag,attr){
		o.rx = rx;
		o.ry = ry;
		o.isDrag = isDrag;
		o.attr({x:this.x+rx,y:this.y+ry}).attr(attr);
		this.dragabble(o,isDrag);
		this.texts.push(o);
		return this;
	},
	addRect:function(o,rx,ry,isDrag,attr){
		o.rx = rx;
		o.ry = ry;
		o.isDrag = isDrag;
		if(o.type==0){
			o.drag(this.x+rx,this.y+ry).attr(attr);
		}else{
			o.drag(this.x+rx,this.y+ry).attr(attr);
		}
		this.dragabble(o.ele,isDrag);
		this.rects.push(o);
		return this;
	},
	addLine:function(line){
		this.drag(0,0,this.x,this.y);
		this.lines[line.ID] = line;
		return this;
	},
	addContainer:function(con,RX,RY){
		con.RX = RX;
		con.RY = RY;
		con.drag(0,0,this.x+RX,this.y+RY);
		con.parent = this;
		this.containers[con.ID] = con;
		delete this.C.topContainers[con.ID];
		return this;
	},
	set:function(json){
//		console.log(json);
//		var ratioWTemp = this.C.ratioW,ratioHTemp = this.C.ratioH;
		this.data = json.data;
		var ratioWTemp = 1,ratioHTemp = 1;
		for(var i=0;i<json.texts.length;i++){
			var o = json.texts[i];
			this.addText(this.C.raphael.text(0,0,o.text),o.rx,o.ry,o.isDrag,o.attr);	
		}
		for(var i=0;i<json.imgs.length;i++){
			var o = json.imgs[i];
			this.addImg(this.C.raphael.image(o.src,0,0,o.w,o.h),o.rx,o.ry,o.isDrag);
		}
		for(var i=0;i<json.rects.length;i++){
			var o = json.rects[i];
			var rect = new BaseRect(o.type,o.w,o.h,o.r,this);	
			this.addRect(rect,o.rx,o.ry,o.isDrag,o.attr);
		}
		for(var i=0;i<json.containers.length;i++){
			var o = json.containers[i];
			
			o.x = o.x*ratioWTemp;
			o.y = o.y*ratioHTemp;
			o.RX = o.RX*ratioWTemp;
			o.RY = o.RY*ratioHTemp;
			
			this.addContainer(this.C.container(o.x,o.y,o.w,o.h,o.ID).set(o),o.RX,o.RY);
		}
		return this;
	},
	dragabble:function(ele,isDrag){
		if(isDrag){
			var T = this;
			ele.drag(
				function(dx,dy,x,y){
					if(T.C.editable){
						T.proxyDrag(dx,dy,x*T.C.coe-T.rx,y*T.C.coe-T.ry);
						//T.proxyDrag(dx,dy,x*T.C.coe-T.rx,y*T.C.coeH-T.ry);
					}
				},
				function(x,y){
					T.start(x,y);
				},
				function(){
					T.end();
				}).click(function(){
					T.click();
				}).dblclick(function(){
					T.dblclick();
				});
		}
	},
	dblclick:function(){
	},
	click:function(){
		this.C.click(this);
	},
	start:function(x,y){
		//记录容器的权重,由权重决定drag的执行频率
		this.weight = 0;
		this.rx = x*this.C.coe - this.x;
		this.ry = y*this.C.coe - this.y;
		//this.ry = y*this.C.coeH - this.y;
		this.eventData = {ID:this.ID,x:this.x,y:this.y};
	},
	end:function(){
		this.rx = this.ry = this.weight = 0;
		//假如x,y拖动均不超过5，认为是误操作，不作备份
		if(!(Math.abs(this.eventData.x-this.x)<5 &&Math.abs(this.eventData.y-this.y)<5)){
			this.C.event("drag",this.eventData);
		}
	},
	//代理拖拽方法，当不想触发容器拖动时可重写该函数，这样保留了drag方法给编辑器调用
	proxyDrag:function(dx,dy,x,y){
		this.drag(dx,dy,x,y);
	},
	drag:function(dx,dy,x,y){
		var mx = x,my = y;
		if(this.parent != null){
			this.RX = mx - this.parent.x;
			this.RY = my - this.parent.y;
			if(mx<=this.parent.x){
				this.RX = 0;
				mx = this.parent.x;
			}else if(mx>(this.parent.x+this.parent.w-this.w)){
				mx = this.parent.x+this.parent.w-this.w;
				this.RX = this.parent.w-this.w;
			}
			if(my<=this.parent.y){
				this.RY = 0;
				my = this.parent.y;
			}else if(my>(this.parent.y+this.parent.h-this.h)){
				my = this.parent.y+this.parent.h-this.h;
				this.RY = this.parent.h-this.h;
			}
			
		}
		this.x = mx;
		this.y = my;
		for(var i=0;i<this.imgs.length;i++){
			var o = this.imgs[i];
			o.attr({x:mx+o.rx,y:my+o.ry});
		}
		for(var i=0;i<this.texts.length;i++){
			var o = this.texts[i];
			o.attr({x:mx+o.rx,y:my+o.ry});
		}
		for(var i=0;i<this.rects.length;i++){
			var o = this.rects[i];
			o.drag(mx+o.rx,my+o.ry);
		}
		for(var i in this.containers) {
			var o = this.containers[i];
			o.drag(0,0,mx+o.RX,my+o.RY);
		}
		for(var i in this.lines){
			this.lines[i].drag();
		}
		return this;
	},
	remove:function(){
		for(var i=0;i<this.imgs.length;i++) this.imgs[i].remove();
		for(var i=0;i<this.texts.length;i++) this.texts[i].remove();
		for(var i=0;i<this.rects.length;i++) this.rects[i].remove();
		for(var i in this.containers) this.containers[i].remove();
		for(var i in this.lines) this.lines[i].remove();
		if(this.parent==null){
			delete this.C.topContainers[this.ID];
		}else{
			delete this.parent.containers[this.ID];
		}
		delete this.C.containers[this.ID];
		//let gc do it work
		this.imgs = this.rects = this.texts = this.containers = this.lines = null;
	},
	//返回存储业务数据的对象
	get:function(){
		return this.data;
	},
	//获取序列化数据
	getData:function(){
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
        for(var j=0;j<this.imgs.length;j++){
            var oj = {};
            var img = this.imgs[j];
            var attr = img.attr();
            oj.rx = img.rx;
            oj.ry = img.ry;
            oj.isDrag = img.isDrag;
            oj.src = attr.src;
            oj.w = attr.width;
            oj.h = attr.height;
            o.imgs.push(oj);
        }
        for(var j=0;j<this.texts.length;j++){
            var oj = {};
            var text = this.texts[j];
            var attr = text.attr();
            oj.rx = text.rx;
            oj.ry = text.ry;
            oj.isDrag = text.isDrag;
            oj.text = attr.text;
            //颜色，字体大小等
            oj.attr = {"font-family":attr["font-family"],fill:attr.fill,stroke:attr.stroke,"font-size":attr["font-size"],"font-weight":attr["font-weight"],"text-anchor":attr["text-anchor"]};
            o.texts.push(oj);
        }
        for(var j=0;j<this.rects.length;j++){
            o.rects.push(this.rects[j].getData());
        }
        for(var j in this.containers){
            var container = this.containers[j];
            o.containers.push(container.getData());
        }
        o.data = this.data;
        return o;
	},
	//设置容器边界
	setBBox:function(bbox){
		this.x = bbox.x;
		this.y = bbox.y;
		this.w = bbox.x1 - bbox.x;
		this.h = bbox.y1 - bbox.y;
		this.drag(0,0,bbox.x,bbox.y);
	},
	//获取容器边界
	getBBox:function(){
		return {x:this.x,y:this.y,x1:this.x+this.w,y1:this.y+this.h};
	}
}
//基本图形，包括矩形、圆角矩形、圆、椭圆、handler
function BaseRect(type,w,h,r,c){
	//0 代表圆(r)  1代表椭圆(w,h)  2代表矩形(w,h) 3 代表圆角矩形(w,h,r)
	this.type = type;
	this.c = c; 
	this.w = w;
	this.h = h;
	this.r = r;
	this.ele = null;
	this.init();
}
BaseRect.prototype = {
	init:function(){
		var C = this.c.C.raphael;	
		var c = this.c;
		if(this.type == 0){
			this.ele = C.circle(0,0,this.r);
		}else if(this.type ==1){
			this.ele = C.ellipse(0,0,this.w,this.h);
		}else if(this.type ==2){
			this.ele = C.rect(0,0,this.w,this.h);
		}else{
			this.ele = C.rect(0,0,this.w,this.h,this.r);
		}
		return this;
	},
	drag:function(x,y){
		//文本框拖拽保持形状
		if(this.c.imgs[0] == undefined){
			if(this.c.texts[0] != undefined){
				if(navigator.userAgent.indexOf("MSIE 8.0")>0){

				}else{
					var last = this.c.texts.length - 1;
					var tspan = $(this.c.texts[last].node).children();
					var fontSize = parseInt(this.c.texts[last].attr("font-size"));
					$(tspan[0]).attr("dy",fontSize/2);
				}
				
				
			}
		}
		if(this.type == 0 || this.type == 1){
			this.ele.attr({cx:x,cy:y});
		}else{
			this.ele.attr({x:x,y:y});
		}
		return this;
	},
	attr:function(attr){
		if(!attr) return this.ele.attr();
		this.ele.attr(attr);
	},
	remove:function(){
		this.ele.remove();
	},
	getData:function(){
		var o = {};
		var attr = this.attr();
		o.type = this.type;
		o.rx = this.rx;
		o.ry = this.ry;
		o.isDrag = this.isDrag;
		if(this.type==1){//椭圆半径为rx,ry
			o.w = attr.rx;
			o.h = attr.ry;
		}else{
			o.w = attr.width;
			o.h = attr.height;
		}
		o.r = attr.r;
		o.attr = {fill:attr.fill,stroke:attr.stroke,"stroke-width":attr["stroke-width"],"fill-opacity":attr["fill-opacity"],"stroke-opacity":attr["stroke-opacity"],"stroke-dasharray":attr["stroke-dasharray"]};
		return o;
	}
}
//算法工具类
function Util(){
	
}
Util.atan = function(x1,y1,x,y){
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
//中心连中心
Util.pathC = function(f,t){
	return "M"+(f.x+f.w/2)+","+(f.y+f.h/2)+"L"+(t.x+t.w/2)+","+(t.y+t.h/2);
}
Util.pathS = function(f,t){
	var a0 = f.x+f.w/2,
	a1 = f.y+f.h,
	a6 = t.x+t.w/2,
	a7 = t.y,
	a2 = a0,
	a3 = (a1+a7)/2,
	a4 = a6,
	a5 = a3;
	return "M"+a0+","+a1+"C"+a2+","+a3+" "+a4+","+a5+" "+a6+","+a7;
}
Util.pathZ = function(f,t){
	var up = (f.y < t.y)?f:t,
		right = (left==f)?t:f,
		left = (f.x < t.x)?f:t,
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
		return {x:p0.x,y:p0.y,x1:p1.x,y1:p1.y,x2:p2.x,y2:p2.y,x3:p3.x,y3:p3.y};
	}else{
		return {x:p3.x,y:p3.y,x1:p2.x,y1:p2.y,x2:p1.x,y2:p1.y,x3:p0.x,y3:p0.y};
	}

}
Util.pathL = function(f,t){
	f.center = {x:f.x+f.w/2,y:f.y+f.h/2};
	t.center = {x:t.x+t.w/2,y:t.y+t.h/2};
	//斜率无穷大
	f.center.x = f.center.x==t.center.x?(f.center.x-.5):f.center.x;
	var fp,tp;
	//斜率,公式:y=kx+b x=(y-b)/k
	var k = (f.center.y-t.center.y)/(f.center.x-t.center.x),
		b = f.center.y-k*f.center.x;
	var f_angle = Util.atan(t.center.x,t.center.y,f.center.x,f.center.y);
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
	return {fx:fp.x,fy:fp.y,tx:tp.x,ty:tp.y,r:t_angle,path:"M"+fp.x+","+fp.y+"L"+(tp.x+Math.cos(t_angle/180*Math.PI)*2)+","+(tp.y+Math.sin(t_angle/180*Math.PI)*2)};
}
//文本框自适应工具
Util.textadapt = function(con){
	if(con.texts[0] != undefined){
		var last = con.texts.length - 1;
		
		var t = con.texts[last].attrs.text.replace(/[\n]/ig,"");

		//con.texts[last].node.textContent.length;
		var r = {
				w:con.w,
				h:con.h,
				x:con.x,
				y:con.y
		};
		r.cx=r.x+r.w/2;
		r.cy=r.y+r.h/2;
		//自适应
		var p = r;
		var fontSize = parseInt(con.texts[last].attr("font-size"));
		var fontWeight = con.texts[last].attr("font-weight");
		var fillColor = con.texts[last].attr("fill");
		var strokeColor = con.texts[last].attr("stroke");
		var js4chrome = rChrome = /.*(chrome)\/([\w.]+).*/;
		var ua = navigator.userAgent.toLowerCase();
		if((js4chrome.exec(ua)!=null)&&js4chrome.exec(ua)[1]=="chrome"){
			if(fontSize<=12){
				fontSize = 12;
			}
		}
		//console.log(fontWeight);
		var margin=4;
		var prw = p.w - 2*fontSize;
		var nText = "";
		//写文字
		var rw=0;var b=0;var preX=p.x+6;var preY=p.y+fontSize;
		for(var j=0;j<t.length;++j){
			if(t[j].match(/[^\x00-\xff]/)){
				rw+=fontSize;
			}else if(t[j].match(/[\-\,\.\?\:\;\'\"\!\`]/)){
				rw+=Math.floor(fontSize/4);
			}else if(t[j].match(/[\x00-\xff\S"]/)){
				rw+=Math.floor(fontSize/2);
			}
			if(rw>=prw){
				nText+=t.substring(b,j+1)+"\n";
				rw=0;
				b=j+1;
				preY+=margin+fontSize;
			}else if(t[j].match(/[\n]/)){
				nText+=t.substring(b,j+1);
				rw=0;
				b=j+1;
			}
		}
		nText += t.substring(b,t.lenght);
		var n=(nText.split('\n')).length;
		con.texts[last].remove();
		con.texts.splice(last,1);
		con.addText(canvas.raphael.text(0,0,nText),0,5,true,{"text-anchor":"start","font-size":fontSize,"font-weight":fontWeight,"fill":fillColor,"stroke":strokeColor});
		con.texts[0].dblclick(function(){
			Util.textDbclickEvent(con);
		});
		if(navigator.userAgent.indexOf("MSIE 8.0")>0){
			var topsize = fontSize*n<100?fontSize*n:100;
			var top = 0;
			if(fontSize*n>p.h){
				top = p.h/2-12;
			}else{
				top = fontSize*n/2-12;
			}
			con.texts[0].shape.style.top = top+"px";
			var textPathString = null;
			for(var i in con.texts[0].shape.children){
			    if(con.texts[0].shape.children[i].nodeName == 'textpath'){
			    	textPathString = con.texts[0].shape.children[i].string;
			    	var nt = nText.split('\n');
			    	var ntl = 0;
			    	for(var j=0;j<n;++j){
			    		if(j*fontSize<(p.h-2*fontSize)){
			    			ntl+=nt[j].length+1;
			    		}
			    	}
			    	con.texts[0].shape.children[i].string = nText.substring(0,ntl);
		 	    }	
			}
		}else{
			var tspan = $(con.texts[last].node).children();
			$(tspan[0]).attr("dy",fontSize/2);
			for(i=1;i<=tspan.length;i++){
				if((i*(fontSize+4))>(p.h)){
					$(tspan[i-1]).attr("display","none");
				}
			}
		}
		
	}
}
//文本框组件双击事件
Util.textDbclickEvent = function(con){
   	var last = (con.texts.length==0)?0:con.texts.length- 1;
	var textVal = "";
	if(con.texts[0] != undefined){
		textVal = con.texts[last].attrs.text.replace(/[\n]/ig,"");
		if(textVal=="双击输入文字"){
			textVal="";
		}
	}
    var selectContent = $('<div></div>');
    selectContent.append('<textarea placeholder="请输入文字"  rows=3 cols=20 style="height:95px;resize:none;">'+textVal+'</textarea>');
	var addMonitorDialog = $('<div/>').dialog({
	    title: '输入文字',
	    width: 315,
	    height: 170,
	    content:selectContent,
	    modal: true,
	    buttons:[{
	    	text:'确定',
			handler:function(){
				var oldFill = '';
				var oldFontSize = '';
				var oldFontWeight = '';
				if(con.texts[0] != undefined){
					oldFill = con.texts[last].attr("fill");
					oldFontSize = con.texts[last].attr("font-size");
					oldFontWeight = con.texts[last].attr("font-weight");
					con.texts[last].remove();
					con.texts.splice(last,1);
				}
				var nText = "";
				var r = {
						w:con.rects[0].ele.attr("width"),
						h:con.rects[0].ele.attr("height"),
						x:con.rects[0].ele.attr("x"),
						y:con.rects[0].ele.attr("y")
					};
					r.cx=r.x+r.w/2;
					r.cy=r.y+r.h/2;
				var p = r;
				if($("textarea").val()!=""){
					var t = $("textarea").val();
					var fontSize = parseInt($("#fontSize").combobox('getText'));
					var js4chrome = rChrome = /.*(chrome)\/([\w.]+).*/;
					var ua = navigator.userAgent.toLowerCase();
					if((js4chrome.exec(ua)!=null)&&js4chrome.exec(ua)[1]=="chrome"){
						if(fontSize<=12){
							fontSize = 12;
						}
					}
					var margin=4;
					var prw = p.w - 2*fontSize;
					//写文字
					var rw=0;var b=0;var preX=p.x+6;var preY=p.y+fontSize;
					for(var j=0;j<t.length;++j){
						if(t[j].match(/[^\x00-\xff]/)){
							rw+=fontSize;
						}else if(t[j].match(/[\-\,\.\?\:\;\'\"\!\`]/)){
							rw+=Math.floor(fontSize/4);
						}else if(t[j].match(/[\x00-\xff\S"]/)){
							rw+=Math.floor(fontSize/2);
						}
						if(rw>=prw){
							nText+=t.substring(b,j+1)+"\n";
							rw=0;
							b=j+1;
							preY+=margin+fontSize;
						}else if(t[j].match(/[\n]/)){
							nText+=t.substring(b,j+1);
							rw=0;
							b=j+1;
						}
					}
					nText += t.substring(b,t.length);
				}else{
					nText = "双击输入文字";
				}
				var n=(nText.split('\n')).length;
				if(oldFill && oldFontSize && oldFontWeight){
					con.addText(canvas.raphael.text(0,0,nText),0,5,true,{"text-anchor":"start","fill":oldFill,"font-size":oldFontSize,"font-weight":oldFontWeight});
				}else{
					con.addText(canvas.raphael.text(0,0,nText),0,5,true,{"text-anchor":"start"});
				}
	            con.texts[0].dblclick(function(){
	            	Util.textDbclickEvent(con);
	            });
				//重新获取texts的数量(拖拽炒作以后对last有影响)
				var lt = con.texts.length;
				if(navigator.userAgent.indexOf("MSIE 8.0")>0){
					var topsize = fontSize*n<100?fontSize*n:100;
					var top = 0;
					if(fontSize*n>p.h){
						top = p.h/2-12;
					}else{
						top = fontSize*n/2-12;
					}
					con.texts[0].shape.style.top = top+"px";
					var textPathString = null;
					for(var i in con.texts[0].shape.children){
						if(con.texts[0].shape.children[i].nodeName == 'textpath'){
					    	textPathString = con.texts[0].shape.children[i].string;
					    	var nt = nText.split('\n');
					    	var ntl = 0;
					    	for(var j=0;j<n;++j){
					    		if(j*fontSize<(p.h-2*fontSize)){
					    			ntl+=nt[j].length+1;
					    		}
					    	}
					    	con.texts[0].shape.children[i].string = nText.substring(0,ntl);
				 	    }	
					}
				}else{
					
					var tspan = $(con.texts[lt-1].node).children();
					$(tspan[0]).attr("dy",fontSize/2);
					for(i=1;i<=tspan.length;i++){
						if((i*(fontSize+4))>(p.h)){
							$(tspan[i-1]).attr("display","none");
						}
					}
				}
				
				addMonitorDialog.panel('destroy');
			}
	    },{
	    	text:'取消',
	    	handler:function(){
	    		addMonitorDialog.panel('destroy');
	    	}
	    }]
	});
}
//缩略图插件
function Tiny(){}
//编辑器插件
function Edit(C){
	this.C = C;
	this.P = {};
	this.points = {lt:"nw-resize",t:"n-resize",tr:"ne-resize",r:"e-resize",rb:"se-resize",b:"s-resize",bl:"sw-resize",l:"w-resize"};
	this.init();
}
Edit.prototype = {
	init:function(){
		var T = this;
		for(var i in this.points){
			var point = this.C.raphael.circle(-10,-10,3).attr({fill: "#fff",stroke: "#fff",cursor: this.points[i],"stroke-opacity":0.5}).hide()
			.drag(function(dx,dy,x,y){
				T.drag(this,dx,dy,x,y);
			},function(x,y){T.start(this,x,y);},function(){T.end();});
			
			/*20150618 使用type属性存放编辑圆点的类型会导致在ie8下。兼容性问题
			 * point.type = i;
			 * */
			point.p_type = i;
			this.P[i] = point;
		}
	},
	start:function(T,x,y){
		T.rx = x*this.C.coe - T.attrs.cx;
		T.ry = y*this.C.coe - T.attrs.cy;
		//T.ry = y*this.C.coeH - T.attrs.cy;
	},
	drag:function(T,dx,dy,x,y){

		var x_ = x*this.C.coe-T.rx,
			y_ = y*this.C.coe-T.ry,
			//y_ = y*this.C.coeH-T.ry,
			x = this.con.x,
			y = this.con.y,
			x1 = this.con.w+x,
			y1 = this.con.h+y;
		switch(T.p_type){
	        case "lt":
	        	if(this.con.parent){
	        		if(x > this.con.parent.x){
	        			x = x_;
	        		}
	        		if(y > this.con.parent.y){
	        			y = y_;
	        		}
	        	}else{
	        		x = x_;
	        		y = y_;
	        	}
	            break;
	        case "t":
	        	if(this.con.parent && y <= this.con.parent.y){
	        		return;
	        	}
	        	y = y_;
	            break;
	        case "tr":
	        	if(this.con.parent){
	        		if(x1 < (this.con.parent.x + this.con.parent.w)){
	        			x1 = x_;
	        		}
	        		if(y > this.con.parent.y){
	        			y = y_;
	        		}
	        	}else{
	        		x1 = x_;
	        		y = y_;
	        	}
	            break;
	        case "r":
	        	if(this.con.parent && x1 >= (this.con.parent.x + this.con.parent.w)){
	        		return;
	        	}
	        	x1 = x_;
	            break;
	        case "rb":
	        	if(this.con.parent){
	        		if(x1 < (this.con.parent.x + this.con.parent.w)){
	        			x1 = x_;
	        		}
	        		if(y1 < (this.con.parent.y + this.con.parent.h)){
	        			y1 = y_;
	        		}
	        	}else{
	        		x1 = x_;
	        		y1 = y_;
	        	}
	            break;
	        case "b":
	        	if(this.con.parent && y1 >= (this.con.parent.y + this.con.parent.h)){
	        		return;
	        	}
	        	y1 = y_;
	            break;
	        case "bl":
	        	if(this.con.parent){
	        		
	        		if(x > this.con.parent.x){
	        			x = x_;
	        		}
	        		if(y1 < (this.con.parent.y + this.con.parent.h)){
	        			y1 = y_;
	        		}
	        	}else{
	        		x = x_;
	        		y1 = y_;
	        	}
	            break;
	        case "l":
	        	if(this.con.parent && x <= this.con.parent.x){
	        		return;
	        	}
	        	x = x_;
	            break;
	    }
		var bbox = {x:x,y:y,x1:x1,y1:y1};
		this.adBBox(T.p_type,bbox);
		this.around(bbox);
		this.dragEdit(bbox);
	},
	
	/**调整编辑框边界bbox数值实现
	 * 1.容器最大最小限制
	 * 2.泳道最高最低最宽限制
	 * 在 this.drag(T,dx,dy,x,y)函数中调用
	 * @param p_type 编辑器圆点方向
	 * @param bbox  左上角，右下角 坐标值
	 */
	adBBox:function(p_type,bbox){
		var _bbox = this.con.getBBox();
		if(
				this.con.data.type == 'unit'
				||this.con.data.type == 'service'
				||this.con.data.type == 'physics'
				||this.con.data.type == 'app'
			){
			this.adLaneBBox(p_type,bbox);
		}else if(this.con.data.type=="rect" || this.con.data.type=="rectcircle" || this.con.data.type=="circle" || this.con.data.type=="circlecl" || this.con.data.type=="quyupic"){
			
		}else{
			var max_height = 100;
			var max_width = 200;
			var min = 20;
			if(this.con.parent != null){
				max_height = this.con.parent.h;
				max_width = this.con.parent.w;
				min += 5;
			}
			this.adConBBox(p_type,bbox,min,max_height,max_width);
		}
	},
	/**
	 * 泳道最高最低限制
	 * @param p_type 编辑器圆点方向
	 * @param bbox  左上角，右下角 坐标值
	 */
	adLaneBBox:function(p_type,bbox){
		var topCon = this.con;
		
		var min = 20;
		if(topCon.containers != null){
			$.each(topCon.containers,function(i,e){
				if(e.h > min){
					min = e.h;
				}
			});
			min += 5;
		}
		
		var max_height = 0;
		if(this.C.topContainers != null){
			$.each(this.C.topContainers,function(i,e){
				if(e.data.type=="rect" || e.data.type=="rectcircle" || e.data.type=="circle" || e.data.type=="circlecl" || e.data.type=="quyupic"){
					return true;
				}
				if(e.ID != topCon.ID){
					max_height += e.h;
				}
			});
		};
		if(max_height){
			max_height = $(this.C.dom).height() - max_height;
		}
		this.adConBBox(p_type,bbox,min,max_height,topCon.w);
	},
	/**
	 * 容器边界调整方法
	 *@param p_type 编辑器圆点方向，
	 *@param bbox 左上角，右下角 坐标值
	 *@param min 容器最小像素高，宽
	 *@param max_height 容器最高像素
	 *@param max_width  容器最宽像素
	 */
	adConBBox:function(p_type,bbox,min,max_height,max_width){
		switch(p_type){
	        case "lt":
	        	if(min && (bbox.x1 - bbox.x < min)){
	        		bbox.x = bbox.x1 - min;
	        	}
	        	if(min && (bbox.y1 - bbox.y < min)){
	        		bbox.y = bbox.y1 - min;
	        	}
	        
	        	if(max_width && (bbox.x1 - bbox.x > max_width)){
	        		bbox.x = bbox.x1 - max_width;
	        	}
	        	if(max_height && (bbox.y1 - bbox.y > max_height)){
	        		bbox.y = bbox.y1 - max_height;
	        	}
//	        	if(this.isMin(x1,x)){
//	        		x = x1 - this.min;
//	        	}
//	        	if(this.isMin(y1,y)){
//	        		y = y1 - this.min;
//	        	}
	            break;
	        case "t":
	        	if(min && (bbox.y1 - bbox.y < min)){
	        		bbox.y = bbox.y1 - min;
	        	}
	        	if(max_height && (bbox.y1 - bbox.y > max_height)){
	        		bbox.y = bbox.y1 - max_height;
	        	}
//	        	if(this.isMin(y1,y)){
//	        		y = y1 - this.min;
//	        	}
	            break;
	        case "tr":
	        	if(min && (bbox.x1 - bbox.x < min)){
	        		bbox.x1 = bbox.x + min;
	        	}
	        	if(min && (bbox.y1 - bbox.y < min)){
	        		bbox.y = bbox.y1 - min;
	        	}
	        	if(max_width && (bbox.x1 - bbox.x > max_width)){
	        		bbox.x1 = bbox.x + max_width;
	        	}
	        	if(max_height && (bbox.y1 - bbox.y > max_height)){
	        		bbox.y = bbox.y1 - max_height;
	        	}
//	        	if(this.isMin(x1,x)){
//	        		x1 = x + this.min;
//	        	}
//	        	if(this.isMin(y1,y)){
//	        		y = y1 - this.min;
//	        	}
	            break;
	        case "r":
	        	if(min && (bbox.x1 - bbox.x < min)){
	        		bbox.x1 = bbox.x + min;
	        	}
	        	if(max_width && (bbox.x1 - bbox.x > max_width)){
	        		bbox.x1 = bbox.x + max_width;
	        	}
	        	
//	        	if(this.isMin(x1,x)){
//	        		x1 = x + this.min;
//	        	}
	            break;
	        case "rb":
	        	if(min && (bbox.x1 - bbox.x < min)){
	        		bbox.x1 = bbox.x + min;
	        	}
	        	if(min && (bbox.y1 - bbox.y < min)){
	        		bbox.y1 = bbox.y + min;
	        	}
	        	if(max_width && (bbox.x1 - bbox.x > max_width)){
	        		bbox.x1 = bbox.x + max_width;
	        	}
	        	if(max_height && (bbox.y1 - bbox.y > max_height)){
	        		bbox.y1 = bbox.y + max_height;
	        	}
//	        	if(this.isMin(x1,x)){
//	        		x1 = x + this.min;
//	        	}
//	        	if(this.isMin(y1,y)){
//	        		y1 = y + this.min;
//	        	}
	            break;
	        case "b":
	        	if(min && (bbox.y1 - bbox.y < min)){
	        		bbox.y1 = bbox.y + min;
	        	}
	        
	        	if(max_height && (bbox.y1 - bbox.y > max_height)){
	        		bbox.y1 = bbox.y + max_height;
	        	}
	        	
//	        	if(this.isMin(y1,y)){
//	        		y1 = y + this.min;
//	        	}
	            break;
	        case "bl":
	        	if(min && (bbox.x1 - bbox.x < min)){
	        		bbox.x = bbox.x1 - min;
	        	}
	        	if(min && (bbox.y1 - bbox.y < min)){
	        		bbox.y1 = bbox.y + min;
	        	}
	        	if(max_width && (bbox.x1 - bbox.x > max_width)){
	        		bbox.x = bbox.x1 - max_width;
	        	}
	        	if(max_height && (bbox.y1 - bbox.y > max_height)){
	        		bbox.y1 = bbox.y + max_height;
	        	}
//	        	if(this.isMin(x1,x)){
//	        		x = x1 - this.min;
//	        	}
//	        	if(this.isMin(y1,y)){
//	        		y1 = y + this.min;
//	        	}
	            break;
	        case "l":
	        	if(min && (bbox.x1 - bbox.x < min)){
	        		bbox.x = bbox.x1 - min;
	        	}
	        	if(max_width && (bbox.x1 - bbox.x > max_width)){
	        		bbox.x = bbox.x1 - max_width;
	        	}
//	        	if(this.isMin(x1,x)){
//	        		x = x1 - this.min;
//	        	}
	            break;
	    }
	},
	
	
	end:function(){
		var bbox = {x:this.P["lt"].attrs.cx,y:this.P["lt"].attrs.cy,x1:this.P["rb"].attrs.cx,y1:this.P["rb"].attrs.cy};
		this.edit(bbox);
		if(this.con.imgs[0] == undefined && this.con.data.type != 'unit' && this.con.data.type != 'service' && this.con.data.type != 'physics' && this.con.data.type != 'app'){
			Util.textadapt(this.con);
		}
	},
	setCon:function(con){//设置编辑器要编辑的容器
		this.con = con;
		this.around(this.con.getBBox());
		this.show();
	},
	around:function(bbox){//将编辑器依附到bbox上
		var x = bbox.x,y = bbox.y,w = bbox.x1-bbox.x,h = bbox.y1-bbox.y;
		for(var i in this.P){
			switch(i){
                case "lt":
                	this.P[i].attr({cx:x,cy:y});
                    break;
                case "t":
                	this.P[i].attr({cx:x+w/2,cy:y});
                    break;
                case "tr":
                	this.P[i].attr({cx:x+w,cy:y});
                    break;
                case "r":
                	this.P[i].attr({cx:x+w,cy:y+h/2});
                    break;
                case "rb":
                	this.P[i].attr({cx:x+w,cy:y+h});
                    break;
                case "b":
                	this.P[i].attr({cx:x+w/2,cy:y+h});
                    break;
                case "bl":
                	this.P[i].attr({cx:x,cy:y+h});
                    break;
                case "l":
                	this.P[i].attr({cx:x,cy:y+h/2});
                    break;
            }
		}
	},
	dragEdit:function(bbox){//代理编辑方法，当不想拖拽时触发可重写该方法
		this.edit(bbox);
	},
	edit:function(bbox){//编辑规则,该编辑器如何编辑容器,不同编辑器重写改规则
		this.con.setBBox(bbox);
	},
	show:function(){
		for(var i in this.P){
			this.P[i].toFront().show();
		}
	},
	hide:function(){//隐藏编辑器
		for(var i in this.P){
			this.P[i].hide();
		}
	}
}
//泳道
function Lane(C){
	this.C = C;	
	this.json = [{type:"unit",text:"业务单位"},
    			{type:"service",text:"业务服务"},
    			{type:"app",text:"业务应用"},
    			{type:"physics",text:"支撑层"}];
	this.textWidth = this.C.raphael.width*.12;
	this.laneWidth = this.C.raphael.width*.88;
	this.laneHeight = this.C.raphael.height/4;
}
Lane.prototype = {
	create:function(adaptHeight){
		for(var i=0;i<this.json.length;i++){
			var lane = this.json[i];
			if(adaptHeight){
				var laneY = null;
				for(var j = 0 ; j < i ; j++){
					laneY += adaptHeight[j];
				}
				var con = this.C.container(this.textWidth,laneY,this.laneWidth,adaptHeight[i])
					.set({texts:[{text:lane.text,rx:-this.textWidth/2,ry:adaptHeight[i]/2,attr:{fill:"#fff",stroke:"none","font-size":16,"font-family":"微软雅黑"}}],imgs:[],
						rects:[{type:2,w:this.C.raphael.width,h:adaptHeight[i],rx:-this.textWidth,ry:0,isDrag:true,attr:{fill:"#fff","fill-opacity":0,stroke:Highcharts.theme.bizLaneTitleBgColor,"fill-opacity":0,"stroke-width":.5,"stroke-dasharray":"- "}},
					       {type:2,w:this.textWidth,h:adaptHeight[i],rx:-this.textWidth,ry:0,attr:{fill:Highcharts.theme.bizLaneTitleRightBorderColor,stroke:Highcharts.theme.bizLaneBorderColor,"fill-opacity":.2,"stroke-width":.5,"stroke-dasharray":"- "}}],containers:[],data:{type:lane.type}});
			}else{
				var con = this.C.container(this.textWidth,this.laneHeight*i,this.laneWidth,this.laneHeight)
					.set({texts:[{text:lane.text,rx:-this.textWidth/2,ry:this.laneHeight/2,attr:{fill:"#fff",stroke:"none","font-size":16,"font-family":"微软雅黑"}}],imgs:[],
						rects:[{type:2,w:this.C.raphael.width,h:this.laneHeight,rx:-this.textWidth,ry:0,isDrag:true,attr:{fill:"#fff","fill-opacity":0,stroke:Highcharts.theme.bizLaneTitleBgColor,"fill-opacity":0,"stroke-width":.5,"stroke-dasharray":"- "}},
					       {type:2,w:this.textWidth,h:this.laneHeight,rx:-this.textWidth,ry:0,attr:{fill:Highcharts.theme.bizLaneTitleRightBorderColor,stroke:Highcharts.theme.bizLaneBorderColor,"fill-opacity":.2,"stroke-width":.5,"stroke-dasharray":"- "}}],containers:[],data:{type:lane.type}});
			}
		}
	},
	adapt:function(edit){
		var unit,service,app,physics;
		for(var i in this.C.topContainers){
			var con = this.C.topContainers[i];
			switch(con.data.type){
				case "unit":
					unit = con;
					break;
				case "service":
					service = con;
					break;
				case "app":
					app = con;
					break;
				case "physics":
					physics = con;
					break;
				default :
					break;
			}
		}
		//宽度自适应
		this.C.raphael.setSize($(this.C.dom).width(),$(this.C.dom).height());
		var old_new = (physics.y+physics.h)/$(this.C.dom).height(),width = $(this.C.dom).width()-5;
		
		unit.click();
		edit.edit({x:this.textWidth,y:1,x1:width,y1:unit.h/old_new});
		service.click();
		edit.edit({x:this.textWidth,y:unit.y+unit.h,x1:width,y1:unit.y+unit.h+service.h/old_new});
		app.click();
		edit.edit({x:this.textWidth,y:service.y+service.h,x1:width,y1:service.y+service.h+app.h/old_new});
		physics.click();
		edit.edit({x:this.textWidth,y:app.y+app.h,x1:width,y1:app.y+app.h+physics.h/old_new});
		edit.hide();
	}
}
function Arrow(r,angle){//angle为箭头夹角
	this.r = r;
	this.angle = angle;
}
Arrow.prototype = {
	rotate:function(x,y,r){//旋转
		var a1 = (r+this.angle/2)/180*Math.PI,
			a2 = (r-this.angle/2)/180*Math.PI;
		return "M"+x+","+y+"L"+(x+Math.cos(a1)*this.r)+","+(y+Math.sin(a1)*this.r)+"L"+(x+Math.cos(a2)*this.r)+","+(y+Math.sin(a2)*this.r)+"Z";
	}
}
//判断浏览器是否是ie9 以下版本,canvas自适应用到
function isIeVersionLt9 (){
	var  browser = $.browser;
	return (browser.msie && parseInt(browser.version) < 9);
}
/**
隐藏子容器编辑框，重写子容器拖拽函数
params C 单指泳道
**/
function hideChdEdit(C){
if(C != null && C.containers!= null){
	for(var chd in C.containers){
		var con = C.C.containers[chd];
		con.proxyDrag = function(dx,dy,x,y){};
	}
}
}
//})();

