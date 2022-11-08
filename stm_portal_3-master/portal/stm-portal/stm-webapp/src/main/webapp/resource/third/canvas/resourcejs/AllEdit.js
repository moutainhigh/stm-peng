/**
 * Created with JetBrains WebStorm.
 * User: ms
 * Date: 14-12-22
 * Time: 下午9:36
 * To change this template use File | Settings | File Templates.
 */

    //编辑规则，针对流程全部拉伸
//    针对只有形状，矩形的对象
function fillEditObj(c){
    var edit = new Edit(c);
    edit.edit = function(bbox){
        var width = bbox.x2-bbox.x;
        var height = bbox.y2-bbox.y;
        //效率问题
        if(width<100 && height<100){
            return;
        }
        if(width<100){
            width = 100;
            bbox.x2 = bbox.x+width;
        }
        if(height<100){
            height = 100;
            bbox.y2 = bbox.y+height;
        }
        this.con.handler.attr({width:width,height:height});
        if(this.con.rects[0]){
            this.con.rects[0].attr({width:width,height:height});
        }
        this.con.setBBox(bbox);
    };
    return edit;
}
//按比例拉伸。编辑规则  文字和图片显示位置
//    针对有图片和文字的对象
function scaleEditObj(c){
    var edit = new Edit(c);
    edit.edit = function(bbox){
        var tempwidth=bbox.x2-bbox.x;
        var tempheight=bbox.y2-bbox.y;
        var mark=30;
        if(tempwidth<30 && tempheight<50){
            return;
        }
        if(tempwidth<40){
            tempwidth = 40;
            bbox.x2 = bbox.x+tempwidth;
        }
        if(tempheight<60){
            tempheight = 60;
            bbox.y2 = bbox.y+tempheight;
        }
        if(tempwidth>90){
            tempwidth = 90;
            bbox.x2 = bbox.x+tempwidth;
        }
        if(tempheight>110){
            tempheight = 110;
            bbox.y2 = bbox.y+tempheight;
        }
        this.con.handler.attr({width:tempwidth,height:tempheight,"stroke-opacity":0});
//            动画矩形
        if(this.con.rects[1]){
            this.con.rects[1].rx=tempwidth *.64;
            this.con.rects[1].ry =tempheight *.64;
            this.con.rects[1].attr({width:tempwidth *.12,height:tempheight *.12});
        }
        //            状态图标
        if(this.con.imgs[1]){
//                this.con.imgs[1].rx=tempwidth-mark-4;
//                this.con.imgs[1].ry =tempheight-mark-4-10;
            this.con.imgs[1].rx=tempwidth *.6;
            this.con.imgs[1].ry =tempheight *.6;
            this.con.imgs[1].attr({width:tempwidth *.3,height:tempheight *.3});
        }
        //设备图标
        if(this.con.imgs[0]){
            this.con.imgs[0].rx=tempwidth*0.05;
            this.con.imgs[0].ry=tempheight*0.05;
            this.con.imgs[0].attr({width:tempwidth *.9,height:tempheight*.7});
        }
        if(this.con.texts[0]){
            this.con.texts[0].rx=tempwidth/2
            this.con.texts[0].ry =tempheight*0.9;
        }
        this.con.setBBox(bbox);
    };
    return edit;
}
//针对只有图片的对象
function fillimgEditObj(c){
    var edit = new Edit(c);
    edit.edit = function(bbox){
        var tempwidth=bbox.x2-bbox.x;
        var tempheight=bbox.y2-bbox.y;
        if(tempwidth<100 && tempheight<100){
            return;
        }
        if(tempwidth<100){
            tempwidth = 100;
            bbox.x2 = bbox.x+tempwidth;
        }
        if(tempheight<100){
            tempheight = 100;
            bbox.y2 = bbox.y+tempheight;
        }
        //设备图标
        this.con.handler.attr({width:tempwidth,height:tempheight});
        if(this.con.imgs[0]){
            this.con.imgs[0].rx=tempwidth*0.05;
            this.con.imgs[0].ry=tempheight*0.05;
            this.con.imgs[0].attr({width:tempwidth *.9,height:tempheight *.9});
        }
        this.con.setBBox(bbox);
    };
    return edit;
}
//单独给椭圆加的规则
function fillcircleclEditObj(c){
    var edit = new Edit(c);
    edit.edit = function(bbox){
        var width = bbox.x2-bbox.x;
        var height = bbox.y2-bbox.y;
        //效率问题
        if(width<100 && height<100){
            return;
        }
        if(width<150){
            width = 150;
            bbox.x2 = bbox.x+width;
        }
        if(height<50){
            height = 50;
            bbox.y2 = bbox.y+height;
        }
        if(height>300){
            height=300;
            bbox.y2 = bbox.y+height;
        }
        if(width>500){
            width=500;
            bbox.x2 = bbox.x+width;
        }
        this.con.handler.attr({width:width,height:height});
        if(this.con.rects[0]){
            this.con.rects[0].attr({width:width,height:height});
        }
        this.con.setBBox(bbox);
    };
    return edit;
}
//单独给大容器的规则
function ConTainerObj(c){
    var Mark=100;
    var edit = new Edit(c);
//        edit.EditBotShow();
    edit.edit = function(bbox){
        var width = bbox.x2-bbox.x;
        var height = bbox.y2-bbox.y;
        //效率问题
        var canvaswidth= c.raphael.width;
        if(width<100 && height<100){
            return;
        }
        if(width<1133){
            width = 1133;
            bbox.x2 = bbox.x+width;
        }
        if(width>1133){
            width = 1133;
            bbox.x2 = bbox.x+width;
        }
        if(height<920){
            height = 920;
            bbox.y2 = bbox.y+height;
        }
        if(height>920){
            height=920;
            bbox.y2 = bbox.y+height;
        }
        this.con.handler.attr({width:width,height:height});
        if(this.con.rects[0]) {
            this.con.rects[0].attr({width: 150, height: height});
        }
        this.con.setBBox(bbox);
    };
    return edit;
}
//    泳道
//固定泳道
function swimming(c){

    var ts=this;
    this.businessUnit=null;
    this.businessServe=null;
    this.businessUse=null;
    this.support=null;

    var con1Y= 0,con2Y=150,con3Y=300,con4Y=450;
//        var businessUnit,businessServe,businessUse,support;
//        var con1Y= 0,con2Y=128,con3Y=256,con4Y=384;
//      this.ConTainer=null;
    var positionW=1133;
    var positionH=c.raphael.height;
    this.ConTainer = c.container(150,5,positionW,1520);  //920

    this.rectattr={stroke:"green","stroke-width":1,"stroke-dasharray":"- " ,"fill":"#062307","fill-opacity":0.6};
    var textattr={fill:"#fff","font-size": 14,"font-weight":"bold","font-family":'微软雅黑'};
    var con,graph,baseArrow;
    this.init=function(){
//                    业务单位
        ts.businessUnit =c.container(0,con1Y,positionW,150);
        ts.businessServe =c.container(0,con2Y,positionW,150);
        ts.businessUse =c.container(0,con3Y,positionW,150);
        ts.support =c.container(0,con4Y,positionW,150);
        con=[[ts.businessUnit,ts.businessServe,ts.businessUse,ts.support],
            ["业务单位","业务服务","业务应用","支撑层"],
            [con1Y,con2Y,con3Y,con4Y],
            ["businessUnit","businessServe","businessUse","support"] ];
//            for(var i=0;i<con[0].length;i++){
//                con[0][i] =c.container(0,0,positionW,128);
//            }
        for(var i=0;i<con[0].length;i++){
            this.ConTainer.addContainer(con[0][i],0,con[2][i]);  //大的Container (装四个泳道)
            con[0][i].handler.attr({stroke:"green","stroke-width": 1,"stroke-dasharray":"- "}); //对泳道的样式
            if(!con[0][i].data()){  con[0][i].bind({ type:con[3][i],ID:con[3][i] })}    //给每个泳道绑定数据属性
            con[0][i].addText(c.raphael.text(0, 0,con[1][i]).attr(textattr).toFront(),-75,75);  //里面的左边矩形
            con[0][i].addRect(c.raphael.rect(0, 0,150,150).attr(this.rectattr),-150,0);    //里面的文字
        }
////        设置父容器的样式
        var TopattrAll={"stroke-width": 1,stroke:"#fff","stroke-dasharray":"- ","stroke-opacity":0};
        ts.ConTainer.handler.attr(TopattrAll);
        if(!ts.ConTainer.data()){  ts.ConTainer.bind({ type:"ConTainer",ID:"ConTainer" })}
    };
//    四个泳道的方法
    this.containerJSON=function(){
        return {
            containers:[{
                x:0,y:0,w:65,h:80,
                texts:[{text:"",rx:0,ry:0,attr:{}}],
                imgs:[{src:"",rx:0,ry:0,w:85,h:85},{src:"",rx:0,ry:0,w:85,h:85}],
                rects:[{rx:0,ry:0,w:65,h:80,attr:{}}],
                data:{type:"",id :""},
                containers:[]
            }]
        }
    };
     this.init();
}

//正对容器的排列
//容器的排列
var util=new Physics();
util.MARGIN=30;
//居中排列
function rank(lane){
    var arr = toArray(lane.containers);
    if(arr.length==0) return;
    util.axis_rank(arr,lane.w/2+lane.x);
}
//左对齐
function leftrank(lane){
    var arr = toArray(lane.containers);
    if(arr.length==0) return;
    util.y_rank(arr);
}

//垂直对齐
function verticalrank(lane){
    var arr = toArray(lane.containers);
    if(arr.length==0) return;
    util.x_rank(arr);
}

//将散列json转换为数组
function toArray(containers){
    var array = [];
    for(var i in containers){
        array.push(containers[i]);
    }
    return array;
}

//鼠标位置
//同时兼容ie和ff的写法 的
function getEvent(){
    var func;
    if(document.all)   return window.event;
    func=getEvent.caller;
    while(func!=null){
        var arg0=func.arguments[0];
        if(arg0)
        {
            if((arg0.constructor==Event || arg0.constructor ==MouseEvent) || (typeof(arg0)=="object" && arg0.preventDefault && arg0.stopPropagation))
            {
                return arg0;
            }
        }
        func=func.caller;
    }
    return null;
}
var __is_ff = (navigator.userAgent.indexOf("Firefox")!=-1);//Firefox
function getMouseLocation(){
    var e = getEvent();
    var mouseX = 0;
    var mouseY = 0;
    if(__is_ff){
        mouseX = e.layerX + document.body.scrollLeft;
        mouseY = e.layerY + document.body.scrollLeft;
    }else{
        mouseX = e.x + document.body.scrollLeft;
        mouseY = e.y + document.body.scrollTop;
    }
    return {x:mouseX,y:mouseY};
}
