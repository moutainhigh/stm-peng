//对容器边框的设定和定义
function Edit(c){
    var E = this;
    this.con = null;
    var P = {};
    this.P = P; //点
    P.lt = c.raphael.circle(0, 0,3).attr({fill: "#fff",stroke: "#fff",cursor: "nw-resize","stroke-opacity":0.5}).hide().drag(function(dx,dy,x,y){
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
    this.end = function(e){
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
    for(var i in P){
        P[i].hover(function(){
            this.attr({"fill":"red","stroke-width":20,"stroke":"red"});
        },function(){
            this.attr({"fill":"#fff","stroke-width":0,"stroke":"#fff"});
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
    this.init = function(){
        this.canvas = $d.canvas();
        this.canvas.isEditable = false;
        this.refresh();
//        小视图窗口 同步大师图显示内容  初始化默认视图的大小  默认缩略图中小矩形的大小
        this.canvas.setViewBox(0,0,this.maxCoe*c.raphael.width,this.maxCoe*c.raphael.height,false);
        $d.dblclick(function(){
            t.refresh();
        });
        if(c.bg ==null){
            c.bgObj("themes/default/images/bizser/background/bg_09.png",0,0,c.raphael.width*this.maxCoe,c.raphael.height*this.maxCoe)
        }
//       缩略图中，可移动的窗口
//        整个画布
        c.bg.drag(function(dx,dy,x,y){
            var vb0 = this.ox-dx*c.coe,vb1 = this.oy-dy*c.coe;
            t.move(0,0,vb0,vb1);
        },function(x,y){
            this.ox =c.raphael._viewBox[0];
            this.oy = c.raphael._viewBox[1];
        }).click(function(){
                if(c.edit) c.edit.hide();  //隐藏编辑框
                //   c.clickNode.frame.attr({"stroke-width":0});
            });
//        滚动缩放
        $(c.dom).mousewheel(function(event, delta){
            var dir = delta > 0 ? 'Up' : 'Down';
            c.coe -= delta*t.zoomRadix;
//			当缩略图移动到100%大小时隐藏
            if(c.coe>5){
                $d.hide();
            }else{
                $d.show();
            }
            if(c.coe>t.maxCoe){
//                console.log("最大细数");
                c.coe = t.maxCoe;
            }else if(c.coe<t.minCoe){
                // console.log("最小细数");
//                c.coe = t.minCoe;
            }
            t.zoom(c.coe);
            return false;
        })
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
            x:0,
            y:0,
            width:c.raphael.width*z,
            height:c.raphael.height*z});
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

    //初始化
    this.init = function(){
        this.raphael = Raphael(dom,$(dom).width(),$(dom).height());
        //100% 初始化缩略图的大小默认100% 边框
        this.setViewBox(0,0,$(dom).width(),$(dom).height(),true);
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
    this.container = function(x,y,w,h,ID){
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
        return con;
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
        for(var i in this.topContainers){
            var con = this.topContainers[i];
            containers.push(con.getData());
        }
        for(var i in this.lines){
            var line = this.lines[i];
            lines.push(line.getData());
        }
//        前面不为空 为真 执行后面代码
        if(this.bg){
            canvasObj.bg={src:this.bg.attrs.src,x:this.bg.attrs.x,y:this.bg.attrs.y,w:this.bg.attrs.width,h:this.bg.attrs.height};
        }
        canvasObj.lines = lines;
        canvasObj.containers = containers;
        return JSON.stringify(canvasObj);
    };
    this.bgObj=function(src,x,y,w,h){
        this.bg = this.raphael.image(src,x,y,w,h).toBack();
    };
    //还原数据
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

//        调用bgObj方法,反序列化背景 如果背景不为空的时候
        if(bgObj){
            this.bgObj(bgObj.src,bgObj.x,bgObj.y,bgObj.w,bgObj.h);
        }
        for(var i in containers){
            this.container(containers[i].x,containers[i].y,containers[i].w,containers[i].h,containers[i].ID).set(containers[i]);
        }
        for(var i in lines){
            var line = this.onLine(this.containers[this.idMap[lines[i].from]],this.containers[this.idMap[lines[i].to]]);
            if(line) line.path.attr(lines[i].attr);
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

    function Line(f,t){
        var size = 5;
        var _l = this;
        this.f = f;
        this.t = t;   //连接到
        this.ID = "Line"+_canvas.nextSeq(); //唯一ID
        var format = "M{0},{1}L{2},{3}";
        var point = Point();
        this.path = _canvas.raphael.path(Raphael.format(format, point.from.x, point.from.y, point.to.x, point.to.y))
            .attr({stroke: "yellow","stroke-width": 3})
            .click(function(){
                _canvas.click(_l);
            }).hover(function(){
                this.attr({cursor:"pointer"});
                this.glows = this.glow({color:this.attrs.stroke,width:10});
            },function(){
                this.glows.remove();
            });
        //线条的动画
        this.animate = function(){
            this.path.attr({path:Raphael.format(format, point.from.x, point.from.y, point.from.x, point.from.y)});
            this.path.animate({path:Raphael.format(format, point.from.x, point.from.y, point.to.x, point.to.y)},2000);
            return this;
        };
        this.drag = function(){
            var p = Point();
            this.path.attr({path:Raphael.format(format, p.from.x, p.from.y, p.to.x, p.to.y)});
            return this;
        };
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
            _l = point = null;
        };
        this.getData = function(){
            var line = {};
            line.from = f.ID;
            line.to = t.ID;
            line.ID = this.ID;
            line.attr = this.path.attr();
            return line;
        } ;
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
                this.setHandler({rx:0,ry:0,attr:{"fill-opacity":0,fill:"#fff"}});
            }
            this.handler.drag(
                //移动处理函数
                function(dx,dy,x,y){
                    _c.drag(dx,dy,x*_canvas.coe-_c.rx,y*_canvas.coe-_c.ry);
                }
                //开始拖拽处理函数
                ,function(x,y){
                    _canvas.creatMemento();
                    _c.rx=x*_canvas.coe-_c.x;
                    _c.ry=y*_canvas.coe-_c.y;
                    _c.ox = x;
                    _c.oy = y;
                    _canvas.click(_c);
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
            if(_canvas.edit) _canvas.edit.drag(this);
            if(callback)callback(x,y);
            return this;
        };
        //删除的时候，也会把自己容器内所属的对象删除
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
        };
        this.Handler();
    }
    this.init();
    //点击delete按钮
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