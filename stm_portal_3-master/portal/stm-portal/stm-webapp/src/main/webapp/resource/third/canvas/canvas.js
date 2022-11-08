//对容器边框的设定和定义
function Edit(c){
    var E = this;
    this.con = null;
    var P = {};
    this.P = P; //点

    P.lt = c.raphael.circle(0, 0,0.3+"%").attr({fill: "#fff",stroke: "#fff",cursor: "nw-resize","stroke-opacity":0.5}).hide().drag(function(dx,dy,x,y){
        E.edit({x:x*c.coe-this.rx,y:y*c.coe-this.ry,x2:E.con.x+E.con.w,y2:E.con.y+E.con.h});
    },function(x,y){E.start(this,x,y)},E.end);
    P.t = P.lt.clone().attr({cursor:"n-resize"}).hide().drag(function(dx,dy,x,y){
        E.edit({x:E.con.x,y:y*c.coe-this.ry,x2:E.con.x+E.con.w,y2:E.con.y+E.con.h});
    },function(x,y){E.start(this,x,y)},E.end);
    P.tr = P.lt.clone().attr({cursor:"ne-resize"}).hide().drag(function(dx,dy,x,y){
        E.edit({x:E.con.x,y:y*c.coe-this.ry,x2:x*c.coe-this.rx,y2:E.con.y+E.con.h});
    },function(x,y){E.start(this,x,y)},E.end);
    P.r = P.tr.clone().attr({cursor:"e-resize"}).hide().drag(function(dx,dy,x,y){
        E.edit({x:E.con.x,y:E.con.y,x2:x*c.coe-this.rx,y2:E.con.y+E.con.h});
    },function(x,y){E.start(this,x,y)},E.end);
    P.rb = P.tr.clone().attr({cursor:"se-resize"}).hide().drag(function(dx,dy,x,y){
        E.edit({x:E.con.x,y:E.con.y,x2:x*c.coe-this.rx,y2:y*c.coe-this.ry});
    },function(x,y){E.start(this,x,y)},E.end);
    P.b = P.rb.clone().attr({cursor:"s-resize"}).hide().drag(function(dx,dy,x,y){
        E.edit({x:E.con.x,y:E.con.y,x2:E.con.x+E.con.w,y2:y*c.coe-this.ry});
    },function(x,y){E.start(this,x,y)},E.end);
    P.bl = P.rb.clone().attr({cursor:"sw-resize"}).hide().drag(function(dx,dy,x,y){
        E.edit({x:x*c.coe-this.rx,y:E.con.y,x2:E.con.x+E.con.w,y2:y*c.coe-this.ry});
    },function(x,y){E.start(this,x,y)},E.end);
    P.l = P.bl.clone().attr({cursor:"w-resize"}).hide().drag(function(dx,dy,x,y){
        E.edit({x:x*c.coe-this.rx,y:E.con.y,x2:E.con.x+E.con.w,y2:E.con.y+E.con.h});
    },function(x,y){E.start(this,x,y)},E.end);


    this.start = function(o,x,y){
        o.rx = x*c.coe - o.attrs.cx;
        o.ry = y*c.coe - o.attrs.cy;
    };
    //拖拽完的回调方法
    this.end = function(){
        //you can edit in here when drag onend
    };
    //拖拽的时候对其规则定义
    this.edit = function(bbox){
        this.con.setBBox(bbox);
    };
    //传入容器边界框,让编辑点与容器一起运动
    this.drag = function(con){
        this.con = con;
        for(var i in P){
            switch(i){
                case "lt":
                    P[i].attr({cx:con.x,cy:con.y}).show();
                    break;
                case "t":
                    P[i].attr({cx:con.x+con.w/2,cy:con.y}).show();
                    break;
                case "tr":
                    P[i].attr({cx:con.x+con.w,cy:con.y}).show();
                    break;
                case "r":
                    P[i].attr({cx:con.x+con.w,cy:con.y+con.h/2}).show();
                    break;
                case "rb":
                    P[i].attr({cx:con.x+con.w,cy:con.y+con.h}).show();
                    break;
                case "b":
                    P[i].attr({cx:con.x+con.w/2,cy:con.y+con.h}).show();
                    break;
                case "bl":
                    P[i].attr({cx:con.x,cy:con.y+con.h}).show();
                    break;
                case "l":
                    P[i].attr({cx:con.x,cy:con.y+con.h/2}).show();
                    break;
            }
        }
    };
    //在其他对象上时处理函数
    this.hide = function(){
        for(var i in P){
            P[i].hide();
        }
    };
    this.EditBotShow=function(){
        P.t.attr({"stroke":"black","fill":"black","stroke-width":0,"r":0}).hide().toBack();
        P.lt.attr({"stroke":"black","fill":"black","stroke-width":0,"r":0}).hide().toBack();
        P.tr.attr({"stroke":"black","fill":"black","stroke-width":0,"r":0}).hide().toBack();
    };
    for(var i in P){
        P[i].hover(function(){
            this.attr({"fill":"red","stroke-width":4,"stroke":"red"});
        },function(){
            this.attr({"fill":"#fff","stroke-width":0});
        });
    }
}

//画布填充  缩略图
function Tiny($d,c){
    var t = this;
    this.maxCoe = 5;
    this.minCoe = .5;
    this.zoomRadix = .1;
    this.area = null;
    this.canvas = null;
    var oz = 0;
    var mousSuo="<div class='mouseSuoFang'>视图比例:<span></span></div>";

    this.init = function(){
        this.canvas = $d.canvas();
        this.canvas.isEditable = false;
        this.refresh();
//        小视图窗口 同步大师图显示内容  初始化默认视图的大小  默认缩略图中小矩形的大小
        this.canvas.setViewBox(0,0,this.maxCoe*c.raphael.width,this.maxCoe*c.raphael.height,false);
        $d.dblclick(function(){
            t.refresh();
        });
//       缩略图中，可移动的窗口
//        整个画布
        c.raphael.rect(0,0,c.raphael.width*this.maxCoe,c.raphael.height*this.maxCoe)
            .attr({fill:"#fff","fill-opacity":0,cursor:"grab","stroke-opacity":0}).toBack()
            // .drag(function(dx,dy,x,y){
            //    var vb0 = this.ox-dx*c.coe,vb1 = this.oy-dy*c.coe;
            //    t.move(0,0,vb0,vb1);
            //},function(x,y){
            //    this.ox =c.raphael._viewBox[0];
            //    this.oy = c.raphael._viewBox[1];
            //})
            .click(function(){
                if(c.edit) c.edit.hide();  //隐藏编辑框
                //   c.clickNode.frame.attr({"stroke-width":0});
            });
//        滚动缩放
        $(c.dom).mousewheel(function(event, delta){
            var dir = delta > 0 ? 'Up' : 'Down';
            c.coe -= delta*t.zoomRadix;
            c.mouseCoe=parseInt(parseFloat(c.coe)*20+"%")-2+"%";
            t.mousewheelObj(c.mouseCoe);
            mousSuo=null;
//			当缩略图移动到100%大小时隐藏
            if(c.coe>5){
                $d.hide();
            }else{
                $d.show();
            }
            if(c.coe>t.maxCoe){
//                console.log("最大细数");
                c.coe = t.maxCoe;
            }
            if(c.coe<t.minCoe){
//                console.log("最小细数");
                c.coe = t.minCoe;
            }
            t.zoom(c.coe);
            return false;
        })

    };
    this.mousewheelObj=function(textObj){
        if(!textObj)return;
        var mouseSuoFang=$(".mouseSuoFang");
        $(c.dom).append(mousSuo);
//      canvas.mouseCoe=parseInt(parseFloat(canvas.coe)*20+"%")-2+"%";
        mouseSuoFang.find("span").text(function(){
            return textObj
        });
        mouseSuoFang.css({ position:"absolute", top:50+"%", left:45+"%"  });
        mouseSuoFang.find("span").css({
            fontSize:"22px",
            color:"#fff"
        });
        mouseSuoFang.show();
        var temp= setTimeout(function(){
            mouseSuoFang.hide();
        },500);
    };
//    缩略图的小框
    this.Area = function(){
        var old = this.area;
        if(old){
            this.area = old.clone();
            old.remove();
        }else{
            this.area = this.canvas.raphael.rect(0,0,this.maxCoe*c.raphael.width,this.maxCoe*c.raphael.height)
                .attr({stroke:"yellow",fill:"#fff","fill-opacity":0});
        }
        this.area.drag(function(dx,dy,x,y){
            t.move(x,y,x*t.canvas.coe-this.rx,y*t.canvas.coe-this.ry);
        },function(x,y){
            t.area.rx = x*t.canvas.coe-t.area.attrs.x;
            t.area.ry = y*t.canvas.coe-t.area.attrs.y;
        },function(){
            t.area.rx = 0;
            t.area.ry = 0;
        })
    };
//    刷新？？？
    this.refresh = function(){
        this.canvas.restore(eval("("+c.getData()+")"));
        var lines = this.canvas.lines;
        //BUG1:line's weight BUG2:text's weight(chrome)
        for(var i in lines){
            lines[i].path.attr({"stroke-width":lines[i].path.attr("stroke-width")/this.maxCoe})
        }
        this.Area();
    };
    this.zoom = function(z){
        if(z<=this.minCoe || z>=this.maxCoe){
            //alert("it's pass");
            return;
        }
//        窗口缩放的时候，缩略图的位置设定
//        this.area.attrs.x+(this.area.attrs.width-c.raphael.width*z)   this.area.attrs.y+(this.area.attrs.height-c.raphael.height*z)
        this.area.attr({
            x: 0 ,
            y:0,
            width:c.raphael.width*z,
            height:c.raphael.height*z});
//        this.area.attrs.y
        this.move(0,0,this.area.attrs.x,this.area.attrs.y);
    };
    this.move = function(dx,dy,x,y){
        if(x<=0){
            x = 0;
        }else if(x>=(this.canvas.raphael._viewBox[2]-this.area.attrs.width)){
            x=this.maxCoe*c.raphael.width-this.area.attrs.width;
        }
        if(y<=0){
            y = 0;
        }else if(y>=(this.canvas.raphael._viewBox[3]-this.area.attrs.height)){
            y = this.maxCoe*c.raphael.height-this.area.attrs.height;
        }
        this.area.attr({x:x,y:y});
        c.setViewBox(x,y,c.raphael.width*c.coe,c.raphael.height*c.coe,false);
    };
    this.init();
}

//画布对象
function Canvas(dom){
    var _canvas = this;
    this.dom = dom;
    this.isEditable = true;
    this.edit = null;
    this.raphael = null;
    this.containers = {};    //容器 (子容器)
    this.topContainers = {}; //父容器
    this.lines = {};       //线条
    this.idMap = {};
    this.clickNode = null;   //点击的对象
    this.bbox = {x:0,y:0,x2:0,y2:0};
    this.memento = {};
    this.backMemento = 5;
    this.isAuto = false;
    this.seq = 0;
    this.coe = 1;  //缩放的细数
    this.bg = null; //背景
    this.TextObjs=[]; //文本框
    this.mark=null;
    this.mouseCoe=1;
    //初始化 当进入浏览视图
    this.init = function(){
        this.raphael = Raphael(dom,$(dom).width(),$(dom).height());
        //100% 初始化缩略图的大小默认100% 边框
        this.setViewBox(0,0,$(dom).width(),$(dom).height());
    };

    this.setSize = function(w,h){
        this.raphael.setSize(w,h);
        //you can't invoke this function when the viewBox change,or back to initializ 100%
        this.setViewBox(0,0,w,h,false);
    };
    this.setViewBox = function(x,y,w,h,fit){
        this.raphael.setViewBox(x,y,w,h,fit);
        this.coe = w/this.raphael.width;
    };
    //定义容器
    this.container =function(x,y,w,h,ID){
        if(!this.isEditable && !this.isAuto) return;
        this.creatMemento();
        var con = new Container(x,y,w,h);
        this.containers[con.ID] = this.topContainers[con.ID] = con;
        this.idMap[ID] = con.ID;
        if(this.bbox.x>x){
            this.bbox.x = x;
        }
        if(this.bbox.y<y){
            this.bbox.y = y;
        }
        if(this.bbox.x2<x+w){
            this.bbox.x2=x+w;
        }
        if(this.bbox.y2<y+h){
            this.bbox.y2=y+h;
        }
        return con ;
    };

    //返回上一次操作
    this.creatMemento = function(){
        if(this.isAuto || !this.isEditable) return;
        var data = this.getData();
        var history = this.memento;
        this.memento = {};
        this.memento.prev = eval("("+data+")");
        this.memento.history = history;
    };
    //上一个操作()
    this.prevMemento = function(){
        if(this.isAuto || !this.isEditable) return;
        this.memento = this.memento.history;
    };
    this.onLine = function(f,t,r){
        if(!this.isEditable && !this.isAuto) return;
        if(!r){
            if(f==t || f==null || f==undefined || t==null || t==undefined) return;
            var finded = sizeof(f.lines)>=sizeof(t.lines)?t:f;
            var target = finded==f?t:f;
            for(var i in finded.lines){
                if(finded.lines[i].f==target || finded.lines[i].t==target) return;
            }
        }
        this.creatMemento();
        var line =  new Line(f,t);
        f.addLine(line);
        t.addLine(line);
        this.lines[line.ID] = line;
        return line;
    };
    //删除对象的时候
    this.remove = function(id){
        if(!this.isEditable) return;
        if(this.clickNode != null){
            this.creatMemento();
            this.clickNode.remove();
            this.clickNode = null;
        }
    };
    this.nextSeq = function(){
        return ++this.seq;
    };
    this.click = function(node){
        this.clickNode = node;
    };
    this.getData = function(){
        var canvasObj = new Object();
        var containers = new Array();
        var lines = new Array();
        var textObjs=new Array();
        for(var i in this.topContainers){
            var con = this.topContainers[i];
            containers.push(con.getData());
        }
        for(var i in this.lines){
            var line = this.lines[i];
            lines.push(line.getData());
        }
//        前面不为空 为真 执行后面代码   进入编辑
        if(this.bg){
            canvasObj.bg={src:this.bg.attrs.src,x:this.bg.attrs.x,y:this.bg.attrs.y,w:this.bg.attrs.width,h:this.bg.attrs.height,attr:this.bg.attrs.attr};
            //this.bg.show();
        }
        //多个文本框
        for(var i=0;i<_canvas.TextObjs.length;i++){
            var val = _canvas.TextObjs[i].getValue();
            textObjs.push(val);
        }
        if(this.mark){
            canvasObj.mark=_canvas.mark;
        }
        canvasObj.lines = lines;
        canvasObj.containers = containers;
        canvasObj.TextObjs=textObjs;     //文本框的
        return JSON.stringify(canvasObj);
    };
    this.bgObj=function(src,x,y,w,h,attr){
        this.bg = this.raphael.image(src,x,y,w,h,attr).toBack();
        this.bg.hide();
    };
    this.TextAreaObj=function(x,y,w,h,text){
        var ta = new TextArea({
            paper:_canvas.raphael,
            pos:{
                x:x, y:y, w:w, h:h
            },
            holder:"#topology_tools_center"
        });
        ta.setText(text);
//        eve.on("oc_topo_chose",function(){
//            this.bg.attr({"fill":"red"});
//            ta.bg.attr({"fill":"#fff"})
//        });
//        ta.setEditable(true);
        _canvas.TextObjs.push(ta);
        return {ta:ta,text:text};
    };
    //还原数据 进入浏览视图
    this.restore = function(data){
        this.isAuto = true;
        this.topContainers = {};
        this.containers = {};
        this.lines = {};
        this.bg=null;
        this.idMap = {};
        this.clickNode = null;
        this.raphael.clear();
        var containers = data.containers;
        var lines = data.lines;
        var bgObj=data.bg;  //得到bg对象的所有数据(参数)
        var textinfos=data.TextObjs; //得到文本框对象的所有数据
        var markObj=data.mark;
//        调用bgObj方法,反序列化背景 如果背景不为空的时候
        if(bgObj){
            this.bgObj(bgObj.src,bgObj.x,bgObj.y,bgObj.w,bgObj.h,bgObj.attr);
            this.bg.hide();   //控制背景显示与隐藏,"backgroundSize":100+"%"
            $(dom).css({"background":'url('+bgObj.src+')',"backgroundRepeat":'no-repeat'});
        }
        else{
            $(dom).css({"background":'themes/default/images/bizser/background/bg1.png',"backgroundRepeat":'no-repeat'});
        }
        if(containers){
        	for(var i=0;i<containers.length;i++){
	            this.container(containers[i].x,containers[i].y,containers[i].w,containers[i].h,containers[i].ID).set(containers[i]);
	        }
        }
        if(lines){
        	for(var i=0;i<lines.length;i++){
                var line = this.onLine(this.containers[this.idMap[lines[i].from]],this.containers[this.idMap[lines[i].to]]);
                if(line) {
                    line.path.attr(lines[i].attr);
                }
            }
        }
        if(textinfos){
        	for(var i=0;i<textinfos.length;i++){
                // var ta= this.TextAreaObj(textinfos[i].x,textinfos[i].y,textinfos[i].w,textinfos[i].h,textinfos[i].text);
                  var ta = new TextArea({
                       paper:_canvas.raphael,
                       pos:{
                           x:textinfos[i].x, y:textinfos[i].y, w:textinfos[i].w, h:textinfos[i].h
                       },
                       holder:"#topology_tools_center"
                  });
                  ta.setText(textinfos[i].text);
                  // ta.setEditable(false)
               }
        }
        if(markObj){
         _canvas.mark=markObj;
        }
        this.isAuto = false;
    };

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
    //线条控制
    /**
     折线算法
     相交算法和L线还未实现.
     */
    function brokenLine(fNode,tNode){
        var upNode = (fNode.y < tNode.y)?fNode:tNode,
            downNode = (upNode==fNode)?tNode:fNode,
            leftNode = (fNode.x < tNode.x)?fNode:tNode,
            rightNode = (leftNode==fNode)?tNode:fNode,
            index0,index1,index2,index3,halfX,halfY;
        if(upNode.y+upNode.h < downNode.y){
            halfY = (upNode.y+upNode.h+downNode.y)/2;
            index0 = {x:upNode.x+upNode.w/2,y:upNode.y+upNode.h};
            index1 = {x:upNode.x+upNode.w/2,y:halfY};
            index2 = {x:downNode.x+downNode.w/2,y:halfY};
            index3 = {x:downNode.x+downNode.w/2,y:downNode.y};
        }else{
            halfX = (leftNode.x+leftNode.w+rightNode.x)/2;
            index0 = {x:leftNode.x+leftNode.w,y:leftNode.y+leftNode.h/2};
            index1 = {x:halfX,y:leftNode.y+leftNode.h/2};
            index2 = {x:halfX,y:rightNode.y+rightNode.h/2};
            index3 = {x:rightNode.x,y:rightNode.y+rightNode.h/2};
        }
        if((upNode==fNode && halfY) || (leftNode==fNode && halfX)){
            return {index0:index0,index1:index1,index2:index2,index3:index3};
        }else{
            return {index0:index3,index1:index2,index2:index1,index3:index0};
        }
    };
    function Line(f,t){
        var size = 5;
        var _l = this;
        this.f = f;
        this.t = t;   //连接到
        this.ID = "Line"+_canvas.nextSeq(); //唯一ID
        var BrokenLine=brokenLine(f,t);
        var format = "M{0},{1}L{2},{3}";
        var forBroken="M{0},{1}C{2},{3} {4},{5} {6},{7}";
        var point = Point(),
            linefor={fill:"#00B308",stroke: "#00B308","stroke-width": 4},
            lineto={fill:"#00B308",stroke: "#00B308","stroke-width": 4};
        this.baseArrow = _canvas.raphael.path(Raphael.format("M{0},{1}L{2},{3}L{4},{5}Z",0,0,3,0,1.5,-3)).attr(linefor);

        var a0 = f.x+f.w/2,
    	a1 = f.y+f.h,
    	a6 = t.x+t.w/2,
    	a7 = t.y,
    	a2 = a0,
    	a3 = (a1+a7)/2,
    	a4 = a6,
    	a5 = a3;
        
        this.path = _canvas.raphael.path(Raphael.format(forBroken,a0,a1, a2,a3,a4,a5,a6,a7))
//            .hover(function(){
//                this.attr({cursor:"pointer"}).toFront();
//                this.glows = this.glow({color:this.attrs.stroke,width:10});
//            },function(){
//                this.glows.remove();
//            })
            .attr({stroke: "#00B308","stroke-width":3,"arrow-end":"block" })
            .click(function(){
                var fff="#fff";
                var tscolor=_l.path.attrs.stroke;
                if(_canvas.edit) _canvas.edit.hide();
                for(var i in _canvas.lines){
                    if(  _canvas.lines[i].path.glowsobj){
                        _canvas.lines[i].path.glowsobj.remove();
                    }
                }
                _l.path.glowsobj=this.glow({color:_l.path.attrs.stroke,width:10});
                _l.baseArrow&& _l.baseArrow.attr({"stroke":_l.path.attrs.stroke,"fill":_l.path.attrs.stroke});
                _canvas.clickNode=_l;
            });
        //线条的动画
        this.animate = function(time){
            this.path.attr({path:Raphael.format(format, point.from.x, point.from.y, point.from.x, point.from.y)});
            this.path.animate({path:Raphael.format(format, point.from.x, point.from.y, point.to.x, point.to.y)},time);
            return this;
        };
        this.drag = function(idobj){
            //  var p=Point();
            BrokenLine=brokenLine(f,t);
            var jaintou=new arrowNew(BrokenLine);
            // var x1 =  p.from.x;//单线段起点
            // var y1 = p.from.y;//单线段起点
            // var x2 = p.to.x;//单线段终点
            // var y2 = p.to.y;//单线段终点
            var nx1=BrokenLine.index0.x,ny1=BrokenLine.index0.y,nx2=BrokenLine.index3.x,ny2=BrokenLine.index3.y;
            this.path.attr({path:Raphael.format(forBroken,
                BrokenLine.index0.x,BrokenLine.index0.y, BrokenLine.index1.x,BrokenLine.index1.y,
                BrokenLine.index2.x,BrokenLine.index2.y,BrokenLine.index3.x,BrokenLine.index3.y )});
            for(var i in _canvas.lines){
                if(_canvas.mark==1||idobj==1){
                    _canvas.lines[i].baseArrow.hide();
                }else if(_canvas.mark==2|| idobj==2) {
                    _canvas.lines[i].baseArrow.show();
                }
            }
  //this.baseArrow.transform("t"+(BrokenLine.index3.x+1.5*jaintou.x)+","+(BrokenLine.index3.y+5*jaintou.y)+"r"+jaintou.r);  //向上的箭头

        };
        //绘制箭头
        function arrowNew(ps){
            var r = y = x = 0;
            //up down
            if(ps.index2.x==ps.index3.x){
                x = -1;
                if(ps.index2.y < ps.index3.y){
                    r = 180;
                    y = -1;
                }else{
                    r = 0;
                    y = 1;
                }
            }else if(ps.index2.y==ps.index3.y){
                y = 0.4;
                if(ps.index2.x < ps.index3.x){
                    r = 90;
                    x = -4;
                }else{
                    r = 270;
                    x = 3;
                }
            }
            _l.baseArrow.transform("t"+(ps.index3.x+1.5*x)+","+(ps.index3.y+5*y)+"r"+r);

            return{x:x,y:y,r:r}
        }
        function Point(){
            var F = f.getBBox();
            var T = t.getBBox();
            var from = connPoint(F,{ x: F.x + F.width / 2,y: F.y + F.height / 2});
            var to = connPoint(T,from);
            from = connPoint(F,to);
            return {from:from,to:to};
        }
        //删除的时候，
        this.remove = function(){
            delete f.lines[this.ID];
            delete t.lines[this.ID];
            delete _canvas.lines[this.ID];
            this.path.remove();
            this.baseArrow.remove();
            _l = point = null;
        };
        this.getData = function(){
            var line = {};
            line.from = f.ID;
            line.to = t.ID;
            line.ID = this.ID;
            //  line.arrowdirect=this.path.arrowdirect(_canvas.mark);
            line.attr = this.path.attr();
            return line;
        };
    }
    //定义容器
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
        this.frame = _canvas.raphael.path(Raphael.format("M{0},{1}h{2}v{3}h{4}z", x, y, w, h, -w))
            .attr({"stroke-width": 0,"stroke":"#fff"});
        //对容器大小位置设定
        this.setBBox = function(bbox){
            this.x = bbox.x;
            this.y = bbox.y;
            this.w = bbox.x2 - bbox.x;
            this.h = bbox.y2 - bbox.y;
            this.drag(0,0,this.x,this.y);
        };
        //处理函数
        this.Handler = function(){
            if(this.handler !=null){
                var history = this.handler;
                this.handler = history.clone();
                this.setHandler({rx:history.rx,ry:history.ry});
                history.remove();
            }else{
                this.handler = _canvas.raphael.rect(x, y, w, h);
                this.setHandler({rx:0,ry:0,attr:{"fill-opacity":0,fill:"#fff",stroke:"none"}});
            }
            this.handler.drag(
                //移动处理函数
                function(dx,dy,x,y){
                    _c.drag(dx,dy,x*_canvas.coe-_c.rx,y*_canvas.coe-_c.ry);
                    _canvas.click(_c);
                }
                //开始拖拽处理函数
                ,function(x,y){
                    _canvas.creatMemento();
                    _c.rx=x*_canvas.coe-_c.x;
                    _c.ry=y*_canvas.coe-_c.y;
                    _c.ox = x;
                    _c.oy = y;
                    //_canvas.click(_c);
                }
                //拖拽结束处理函数
                ,function(e){
                    if(Math.abs(_c.x+_c.rx-_c.ox)<_canvas.backMemento && Math.abs(_c.y+_c.ry-_c.oy)<_canvas.backMemento){
                        _canvas.prevMemento();
                    }
                    _c.rx=_c.ry=0;
                }).click(function(){
                    _canvas.click(_c);
                }
            );
            return this;
        };
        this.setHandler = function(j){
            this.handler.rx = j.rx;
            this.handler.ry = j.ry;
            this.handler.attr(j.attr);
            this.handler.attr({x:this.x+j.rx,y:this.y+j.ry});
            return this;
        };
        //直接创建的方法 add
        this.addImg = function(img,rx,ry,attr){
            img.rx = rx;
            img.ry = ry;
            img.attr({x:this.x+rx,y:this.y+ry});
            img.attr(attr);
            this.imgs.push(img);
            return this.Handler();
        };
        this.addRect = function(rect,rx,ry,attr){
            rect.rx = rx;
            rect.ry = ry;
            rect.attr({x:this.x+rx,y:this.y+ry});
            rect.attr(attr);
            this.rects.push(rect);
            return this.Handler();
        };
        this.addText = function(text,rx,ry,attr){
            text.rx = rx;
            text.ry = ry;
            text.attr({x:this.x+rx,y:this.y+ry});
            text.attr(attr);
            this.texts.push(text);
            return this.Handler();
        };
        this.addLine = function(line){
            this.lines[line.ID] = line;
            this.drag(0,0,this.x,this.y);
            return this;
        };
        this.addContainer = function(con,RX,RY){
            con.RX = RX;
            con.RY = RY;
            con.move(this.x+RX,this.y+RY);
            con.parent = this;
            this.containers[con.ID] = con;
            delete _canvas.topContainers[con.ID];
            return this;
        };
        //额外的set方法
        this.set = function(json){
            var handler = json.handler;
            var frame = json.frame;
            var texts = json.texts;
            var imgs = json.imgs;
            var rects = json.rects;
            var containers = json.containers;
            if(texts){
            	for(var i=0;i<texts.length;i++){
                    this.addText(_canvas.raphael.text(0, 0,texts[i].text),texts[i].rx,texts[i].ry,texts[i].attr)
                }
            }
            if(imgs){
            	for(var i=0;i<imgs.length;i++){
                    this.addImg(_canvas.raphael.image(imgs[i].src, 0, 0, imgs[i].w, imgs[i].h),imgs[i].rx,imgs[i].ry,imgs[i].attr);
                }
            }
            if(rects){
            	for(var i=0;i<rects.length;i++){
                    this.addRect(_canvas.raphael.rect(0, 0, rects[i].w, rects[i].h),rects[i].rx,rects[i].ry,rects[i].attr);
                }
            }
            if(containers){
            	for(var i=0;i<containers.length;i++){
                    this.addContainer(_canvas.container(containers[i].x,containers[i].y,containers[i].w,containers[i].h,containers[i].ID).set(containers[i]),containers[i].RX,containers[i].RY);
                }
            }
            if(handler){
                this.setHandler(handler);
            }
            if(frame){
                this.frame.attr(frame.attr);
            }
            this.bind(json.data);
            return this;
        };
        this.get = function(){
            return this;
        };
        this.bind = function(data){
            $(this).data("data",data);
        };
        this.data = function(){
            return $(this).data("data");
        };
        //返回容器的属性定义
        this.getBBox = function(){
            return {
                x:this.x,
                y:this.y,
                x2:this.x+this.w,
                y2:this.y+this.h,
                width:this.w,
                height:this.h
            };
        };
        this.move = function(x,y){
            this.drag(0,0,x,y);
            return this;
        };
        //拖动容器的时候，
        this.drag = function(dx,dy,x,y,callback){
            if(!_canvas.isEditable && !_canvas.isAuto) return;
            var mx = x,my = y;
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
            this.handler.attr({x:mx+this.handler.rx,y:my+this.handler.ry});
            this.frame.attr({path:Raphael.format("M{0},{1}h{2}v{3}h{4}z", mx, my, this.w, this.h, -this.w)});
            //里面的所有对象也会跟着拖动
            for(var i=0;i<this.imgs.length;i++){
                this.imgs[i].attr({x:mx+this.imgs[i].rx,y:my+this.imgs[i].ry});
            }
            for(var i=0;i<this.rects.length;i++){
                this.rects[i].attr({x:mx+this.rects[i].rx,y:my+this.rects[i].ry})
            }
            for(var i=0;i<this.texts.length;i++){
                this.texts[i].attr({x:mx+this.texts[i].rx,y:my+this.texts[i].ry});
            }
            for(var i in this.containers){
                this.containers[i].drag(x,y,mx+this.containers[i].RX,my+this.containers[i].RY);
            }
            for(var i in this.lines){
                this.lines[i].drag();
            }
            if(_canvas.edit) _canvas.edit.drag(this);
            if(callback)callback(x,y);
            return this;
        };
        //删除的时候，也会把自己容器内所属的对象删除
        this.remove = function(){
            _c.handler.remove();
            _c.frame.remove();
            for(var i=0;i<this.imgs.length;i++){
                this.imgs[i].remove();
            }
            for(var i=0;i<this.rects.length;i++){
                this.rects[i].remove();
            }
            for(var i=0;i<this.texts.length;i++){
                this.texts[i].remove();
            }
            for(var i in this.containers){
                this.containers[i].remove();
            }
            for(var i in this.lines){
                this.lines[i].remove();
            }
            //topContainers 删除的时候，会保留父容器，子容器删除
            if(this.parent==null){
                delete _canvas.topContainers[this.ID];
            }else{
                delete this.parent.containers[this.ID];
            }
            delete _canvas.containers[this.ID];
            //let gc do it work 垃圾回收
            this.imgs = this.rects = this.texts = this.containers = this.lines = null;
        };
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
            for(var j=0;j<this.imgs.length;j++){
                var oj = {};
                var img = this.imgs[j];
                oj.rx = img.rx;
                oj.ry = img.ry;
                oj.attr = img.attr();
                o.imgs.push(oj);
            }
            for(var j=0;j<this.texts.length;j++){
                var oj = {};
                var text = this.texts[j];
                oj.rx = text.rx;
                oj.ry = text.ry;
                oj.attr = text.attr();
                o.texts.push(oj);
            }
            for(var j=0;j<this.rects.length;j++){
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
            var handler ={};
            handler.attr = this.handler.attr();
            handler.rx = this.handler.rx;
            handler.ry = this.handler.ry;
            o.frame = frame;
            o.handler = handler;
            o.data = this.data();
            return o;
        };
        this.Handler();
    }
    this.init();

}
(function(_$){
    _$.fn.canvas = function(){
        return new Canvas(_$(this)[0]);
    }
})(jQuery)