var user = oc.index.getUser();
var memo = [];
var canvas,edit;
var skin=Highcharts.theme.currentSkin;
//状态灯样式
var statusCss = {
	NORMAL : "light-ico greenlight",
	WARN : "light-ico yellowlight",
	SERIOUS : "light-ico orangelight",
	CRITICAL : "light-ico redlight"
};
// 状态灯图片
var statusImg = {
	NORMAL : "themes/"+skin+"/images/bizser/statusIMG/zhengchang.png",
	WARN : "themes/"+skin+"/images/bizser/statusIMG/jingao.png",
	SERIOUS : "themes/"+skin+"/images/bizser/statusIMG/yanzhong.png",
	CRITICAL : "themes/"+skin+"/images/bizser/statusIMG/zhiming.png",
	CRITICAL_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/zhiming_nothing.png",
	UNKNOWN_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/weizhi_nothing.png",
	NORMAL_CRITICAL : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_zhiming.png",
	NORMAL_UNKNOWN : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_weizhi.png",
	NORMAL_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_nothing.png"
};
//状态灯图片
var statusAppImg = {
	NORMAL : "themes/"+skin+"/images/bizser/statusAppIMG/zhengchang.png",
	WARN : "themes/"+skin+"/images/bizser/statusAppIMG/jingao.png",
	SERIOUS : "themes/"+skin+"/images/bizser/statusAppIMG/yanzhong.png",
	CRITICAL : "themes/"+skin+"/images/bizser/statusAppIMG/zhiming.png"
};
// 资源图片
var resourceImg = {
	Host : "Host.png",
	NetworkDevice : "NetworkDevice.png",
	Link : "Link.png",
	Storage : "Storage.png",
	Database : "Database.png",
	Middleware : "Middleware.png",
	J2EEAppServer : "J2EEAppServer.png",
	WebServer : "WebServer.png",
	StandardService : "StandardService.png",
	VM : "VM.png",
	MailServer : "MailServer.png",
	Directory : "Directory.png",
	LotusDomino : "LotusDomino.png",
	SnmpOthers : "SnmpOthers.png"
};
//编辑器的选择
function checkEdit(con){
	edit.setCon(con);
	var type = con.data.type;
	if(type=="rect" || type=="rectcircle" || type=="circle" || type=="circlecl"){
		edit.edit = getRectEdit;
	}else if(type=="quyupic"){
		edit.edit = getImgEdit;
	}else{
		edit.edit = getEdit;
	}
	
}
//一般的编辑规则，针对业务单位、业务服务、业务应用、资源
function getEdit(bbox){
	this.con.imgs[0].attr({width:bbox.x1-bbox.x,height:(bbox.y1-bbox.y)*.8});
	if(this.con.imgs[1]){
		this.con.imgs[1].rx = bbox.x1 - bbox.x - 20;
		this.con.imgs[1].ry = (bbox.y1-bbox.y)*.6;
	}
	this.con.texts[0].rx = (bbox.x1 - bbox.x)/2;
	this.con.texts[0].ry = bbox.y1 -bbox.y - 5;
	this.con.setBBox(bbox);
}
//单张图片编辑规则，针对区域和节点
function getImgEdit(bbox){
	this.con.imgs[0].attr({width:bbox.x1-bbox.x,height:bbox.y1-bbox.y});
	this.con.setBBox(bbox);
}
//图形编辑规则、针对基本图形
function getRectEdit(bbox){
	var type = this.con.rects[0].type;
	if(type==0){
		var r = Math.min((bbox.x1-bbox.x),(bbox.y1-bbox.y))/2;
		this.con.rects[0].rx = this.con.rects[0].ry = r;
		this.con.rects[0].attr({r:r});
		//bbox = {x:bbox.x1-2*r,y:bbox.y1-2*r,x1:bbox.x1,y1:bbox.y1};
	}else if(type==1){
		this.con.rects[0].rx = (bbox.x1+bbox.x)/2-this.con.x;
		this.con.rects[0].ry = (bbox.y1+bbox.y)/2-this.con.y;
		this.con.rects[0].attr({rx:(bbox.x1-bbox.x)/2,ry:(bbox.y1-bbox.y)/2});
	}else if(type==2){
		this.con.rects[0].attr({width:bbox.x1-bbox.x,height:bbox.y1-bbox.y});
	}else if(type==3){
		this.con.rects[0].attr({width:bbox.x1-bbox.x,height:bbox.y1-bbox.y,r:Math.min((bbox.x1-bbox.x),(bbox.y1-bbox.y))/10});
	}
	this.con.setBBox(bbox);
}
//泳道的编辑规则
function getLaneEdit(bbox,_this,unit,service,app,physics){
	
	_this.con.texts[0].ry = (bbox.y1-bbox.y)/2;
	_this.con.rects[0].attr({height:bbox.y1-bbox.y,width:bbox.x1});
	_this.con.rects[1].attr({height:bbox.y1-bbox.y});
	_this.con.setBBox(bbox);
	unit.drag(0,0,unit.x,unit.y);
	service.drag(0,0,service.x,unit.y+unit.h);
	app.drag(0,0,app.x,service.y+service.h);
	physics.drag(0,0,physics.x,app.y+app.h);
}
function getByID(ID){
	var id = ID;
	//递归寻找出还原后的元素
	while(canvas.idMap[id]){
		id = canvas.idMap[id];
	}
	var char = id.charAt(0);
	if(char=="C"){
		return canvas.containers[id];
	}else if(char=="L"){
		return canvas.lines[id];
	}
}
//撤销重做
function undo(){
	edit.hide();
	var prev = memo.pop();
    if(!prev) return;
    //还原的时候开启自动模式
    canvas.beginAuto();
    if(prev.fname=="container"){
    	var con = getByID(prev.obj);
    	con && con.remove();
    }else if(prev.fname=="onLine"){
    	var line = getByID(prev.obj);
    	line.remove();
    }else if(prev.fname=="removeContainer"){
    	var con = prev.obj.container;
    	canvas.container(con.x,con.y,con.w,con.h,con.ID).set(con);
    	for(var i=0;i<prev.obj.lines.length;i++){
    		var o = prev.obj.lines[i];
    		var f = getByID(o.from);
    		var t = getByID(o.to);
			var line = canvas.onLine(f,t,o);
    	}
    }else if(prev.fname=="removeLine"){
    	var o = prev.obj;
    	var f = getByID(o.from);
    	var t = getByID(o.to);
		var line = canvas.onLine(f,t,o);
    }else if(prev.fname=="drag"){
    	var con = getByID(prev.obj.ID);
    	con && con.drag(0,0,prev.obj.x,prev.obj.y);
    }else{
    	//setBBox的时候
    }
    canvas.endAuto();
}
//对Container原型的扩展、集成edit、增加双击事件等
function expand(){
	Canvas.prototype.event = function(fname,obj){
		//假如是默认操作如还原等，不就行备忘
		if(this.isAuto)	return;
		memo.push({fname:fname,obj:obj});
	};
	
	//将编辑器集成到对容器的操作上
	Container.prototype.click = function(){
		if(edit){
			checkEdit(this);
		}
		this.C.click(this);
	};
	Container.prototype.proxyDrag = function(dx,dy,x,y){
		edit && edit.hide();
		this.drag(dx,dy,x,y);		
	};
	//为容器加上双击事件
	Container.prototype.dblclick = function(){
		var T = this;
		if(this.data.type=="resource"){
			oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
                oc.module.resmanagement.resdeatilinfonew.open({instanceId:T.data.id});
            });
		}else if(this.data.type=="bizDep" || this.data.type=="bizSer"){
			if(user.systemUser==true){
				if(memo.length>0){
					 $.messager.confirm('提示','业务数据未保存，是否保存后离开？',function(r){
						 if(r){
							 $('#businessToolbar').find(".save").click();
							 oc.resource.loadScript('resource/module/business-service/js/graph.js?t'+new Date(),function(){
				                    oc.module.biz.ser.graph.portal($('#business-service_index').layout('panel','center'),
				                            {type:T.data.type=="bizDep"?0:1,id:T.data.id});
				                });
						 }
					 });
				}else{
					oc.resource.loadScript('resource/module/business-service/js/graph.js?t'+new Date(),function(){
	                    oc.module.biz.ser.graph.portal($('#business-service_index').layout('panel','center'),
	                            {type:T.data.type=="bizDep"?0:1,id:T.data.id});
	                });
				}
			 }
		}
	}
}