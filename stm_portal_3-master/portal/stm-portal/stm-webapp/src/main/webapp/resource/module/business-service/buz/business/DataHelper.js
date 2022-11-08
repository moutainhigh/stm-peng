/**
 * 数据提供者，负责和后台服务器交互
 */
buzdefine("bz/DataHelper", [], function () {
    var tmp = function (args) {
    	this.nodeStatus={
            //(0正常1警告,2严重,3致命
            '-1':{img:'',name:'不存在的图片'},
            '0':{img:'../resource/module/business-service/buz/img/statusIMG/zhengchang.png',name:'正常'},
            '1':{img:'../resource/module/business-service/buz/img/statusIMG/jingao.png',name:'告警'},
            '2':{img:'../resource/module/business-service/buz/img/statusIMG/yanzhong.png',name:'严重'},
            '3':{img:'../resource/module/business-service/buz/img/statusIMG/zhiming.png',name:'致命'}
        }
        this.node = {w:40,h:40};

        if(!args.topoSVG){
            throw "topoSVG can't be null";
        }
        this.topoSVG = args.topoSVG;

    };
    tmp.prototype = {
    	/**
    	 * 获取业务图数据
    	 * @param id 业务拓扑的id
    	 */
    	getBusiness:function(id,view,callback){
    		
            var _this = this;
            oc.util.ajax({
            url:oc.resource.getUrl('portal/business/canvas/getCanvasData.htm'),
                data:{
                    bizId:id
                },
                successMsg:null,
                success:function(d){
                    //console.log(d);

                    var obj={};
                    obj.nodes = [];
                    obj.links = [];
                    obj.panel = {};

                    var pattr = d.data.attr?d.data.attr:'{}';
                    pattr = eval('(' + pattr + ')');
                    var oldW = pattr.w?pattr.w:view.w;
                    var oldH = pattr.h?pattr.h:view.h;
                    
                    oc.business.radiox = oldW / _this.topoSVG.args.selector.width();
                    oc.business.radioy = oldH / _this.topoSVG.args.selector.height();
                    
                    for(var j=0;j<d.data.nodes.length;j++){
                        var n = d.data.nodes[j];

                        var node = {};
                        if(n.attr != null &&  n.attr != '')
                            node.attr = eval('(' + n.attr +')');
                        else{
                            node.attr = {
                                x:100,y:35,
                                w:_this.node.w,
                                h:_this.node.h,
                                src:'../platform/file/getFileInputStream.htm?fileId='+n.fileId,
                                //statusImg: _this.nodeStatus[n.nodeStatus].img
                            }


                            if(n.instanceId == n.bizId && n.nodeType == 2){
                                node.attr.x = view.w/2;
                                node.attr.y = 20;
                            }

                        }
                        node.attr.src = '../platform/file/getFileInputStream.htm?fileId='+n.fileId;
                        node.attr.statusImg = _this.nodeStatus[n.nodeStatus].img;
                        
                        //修正xy坐标为当前分辨率率的坐标
                        node.attr.x = node.attr.x/oldW * view.w;
                        node.attr.y = node.attr.y/oldH * view.h;
                        if(node.attr.x >= view.w)
                            node.attr.x = view.w-50;
                        if(node.attr.y >= view.h)
                            node.attr.y = view.h-50;

                        //修正图元大小为当前画布下的分辨率
                        if(!isNaN(node.attr.w))
                            node.attr.w = node.attr.w/oldW * view.w;
                        if(!isNaN(node.attr.h))
                            node.attr.h = node.attr.h/oldH * view.h;
                        
                    	if(!node.attr.fontStyle){
                    		node.attr.fontStyle = _this.topoSVG.bizCfg.get('node').attr.fontStyle;
                    	}
                    	node.attr.fontStyle['fill'] = '#fff';

                        //修正字体的大小
                        var minP = (view.w/oldW)<(view.h/oldH)?(view.w/oldW):(view.h/oldH);
                        if(node.attr.fontStyle && minP <1 && minP >0){
                            var fs = node.attr.fontStyle['font-size'];
                            fs = parseInt(fs);
                            fs = parseInt(fs*minP);
                            fs = fs<10?10:fs;
                            node.attr.fontStyle['font-size'] = fs;
                        }


                        node.id = n.id;
                        node.instanceId=n.instanceId;
                        node.type = node.attr.type?node.attr.type:'net';
                        node.attr.type = node.attr.type?node.attr.type:'net';
                        node.bizId = n.bizId;
                        node.fileId = n.fileId;
                        node.nameHidden = n.nameHidden;
                        node.nodeStatus = n.nodeStatus;
                        node.nodeType = n.nodeType;
                        node.showName = n.showName;
                        
                        if(node.nodeStatus  == -1){
                            delete node.attr.statusImg;
                        }
                        if(node.nodeType != 1 &&  node.nodeType != 2 ){
                            delete node.attr.statusImg;
                        }
                        
                        obj.nodes.push(node);
                    }
                    for(var j=0;j<d.data.links.length;j++){
                        var link = d.data.links[j];
                        if(link.attr != ""){
                            link.attr = eval('(' + link.attr +')')
                        }
                        obj.links.push(link);
                    }
                    obj.panel.bizId = d.data.bizId;
                    obj.panel.id = d.data.id;
                    var attr = d.data.attr?d.data.attr:'{}';
                    obj.panel.attr = eval('(' + attr + ')');
                    obj.panel.attr.w = view.w;
                    obj.panel.attr.h = view.h;

                    if(typeof(callback)=='function')
                        callback(obj);
                }
            });
    	},
        /*
         *view：画布的基本属性，以及图元的基本配置属性
         *data:不带坐标信息的绘图信息， 
         */
        generateTopo:function(view,data){
            var _this = this;

            var pdata = {};
            pdata.nodes = [], pdata.links = [];
            pdata.panel = {};

            var w=1300,h=600;
            if(view.w)
                w=view.w;
            if(view.h)
                h=view.h;
            var ndPos = {};
            if(!view.isEmptyPanel){
                var nds= view.data;
                for(var i=0;i<nds.length;i++){
                    var nd = nds[i];
                    var p = nd.attr.x + "_" + nd.attr.y;
                    ndPos[p] = {w:nd.attr.w , h:nd.attr.h};
                }
            }

            var nods=data.data.nodes,liks=data.data.links;
            var tdh = h / 5;//默认绘制5层
            var dh = tdh >100?tdh:100;
            for(var i=0;i<nods.length;i++){
                var y = dh*(i+1);
                y = parseInt(y);

                var dw = w/nods[i].length; 
                for(var j=0;j<nods[i].length;j++){
                    var n = nods[i][j];   
                    var x = (dw*(2*j+1))/2;
                    x = parseInt(x);
                    if(!view.isEmptyPanel){
                        var p = x+"_"+y;
                        var d = ndPos[p];
                        if(d){
                            if(x+d.w < w-100){
                                    x = x+ d.w* (1+Math.random());
                            }else{
                                 x = x- d.w* (1+Math.random());   
                            }
                            x = parseInt(x);
                            y = parseInt(y);
                        }
                    }


                    var node = {};
                    node.attr = {};
                    node.attr.x = x;
                    node.attr.y = y;
                    if(node.attr.x > view.w)
                        node.attr.x = view.w-50;
                    if(node.attr.y >= view.h)
                        node.attr.y = view.h-50;

                    node.attr.w = _this.node.w;
                    node.attr.h = _this.node.h;
                    node.attr.src = '../platform/file/getFileInputStream.htm?fileId='+n.fileId;
                    node.attr.statusImg = this.nodeStatus[n.nodeStatus].img;
                    node.attr.type = 'net';
                	if(!node.attr.fontStyle){
                		node.attr.fontStyle = this.topoSVG.bizCfg.get('node').attr.fontStyle;
                	}
                	node.attr.fontStyle['fill'] = '#fff';

                    node.id = n.id;
                    node.instanceId=n.instanceId;
                    node.type = 'net';
                    node.bizId = n.bizId;
                    node.fileId = n.fileId;
                    node.nameHidden = n.nameHidden;
                    node.nodeStatus = n.nodeStatus;
                    node.nodeType = n.nodeType;
                    node.showName = n.showName;
                    
                    if(node.nodeStatus  == -1){
                        delete node.attr.statusImg;
                    }
                    if(node.nodeType != 1 &&  node.nodeType != 2 ){
                        delete node.attr.statusImg;
                    }
                    pdata.nodes.push(node);
                }
            }

            for(var i=0;i<liks.length;i++){
                var l = liks[i];
                var link = {
                    id:undefined,
                    fromNode:undefined,
                    toNode:undefined,
                    attr:{
                        from:{id:undefined,pose:'s'},
                        to:{id:undefined,pose:'n'},
                        attr:{
                            //"stroke-dasharray":"8,8",
                            "stroke-width":2,
                            stroke:'#666668'
                        },
                        arrow:'single-arrow',
                        type:"poly"
                    }
                }
                link.id = l.id;
                link.fromNode = l.fromNode;
                link.toNode = l.toNode;

                link.attr.from.id = l.fromNode;
                if(!pdata.nodes[link.attr.from.id]){
                    link.attr.from.x = 0;
                    link.attr.from.y = 0;
                }
                link.attr.to.id = l.toNode;
                if(!pdata.nodes[link.attr.to.id]){
                    link.attr.to.x = 100;
                    link.attr.to.y = 100;
                }

                pdata.links.push(link);
            }
            
            if(view.panel){
               pdata.panel = view.panel; 
            }else{
                pdata.panel.bizId = data.data.bizId;
                pdata.panel.id = data.data.id;
                pdata.panel.attr = {};
            }


            return pdata;
        },
        /**
        *把其它地方传过来的数据转化成能够识别的topoSVG数据
        *
        */
        convert:function(view,data){
            var w=1300,h=600;
            if(view.w)
                w=view.w;
            if(view.h)
                h=view.h;
            var pdata = {}; 
            if(data.nodes != undefined){
                pdata.nodes = this.convertNodeData(view,data.nodes);
            }

            return pdata;
        },
        convertNodeData: function(view,nodes){
            var _this = this;
            var gnodes = [];              
            var w=view.w;
            var h=view.h;

            var x = 10,y = 10;
            for(var i=0;i<nodes.length;i++){
                var n = nodes[i];
                x = x + 60;
                if(x+10>w){
                    y = y + 60;
                }
                var node = {};
                node.attr = {};
                node.attr.x = x;
                node.attr.y = y;
                node.attr.w = _this.node.w;
                node.attr.h = _this.node.h;
                node.attr.src = '../platform/file/getFileInputStream.htm?fileId='+n.fileId;
                node.attr.statusImg = this.nodeStatus[n.nodeStatus].img;

                node.id = n.id;
                node.instanceId=n.instanceId;
                node.attr.type = 'net';
                node.bizId = n.bizId;
                node.fileId = n.fileId;
                node.nameHidden = n.nameHidden;
                node.nodeStatus = n.nodeStatus;
                node.nodeType = n.nodeType;
                node.showName = n.showName;
                if(node.nodeStatus  == -1){
                    delete node.attr.statusImg;
                }
                if(node.nodeType != 1 &&  node.nodeType != 2 ){
                    delete node.attr.statusImg;
                }
                    
                gnodes.push(node);
            }
            
            return gnodes;
        },
        convertLinkData : function(view,data){
            
        },
        update:function(data,callback){//更新节点数据到后台

           // console.log(data);
            var obj= {};
            obj.nodes = [];
            var pds = data.nodes;
            for(var i=0;i<pds.length;i++){
                var o ={};
                o.attr = pds[i].attr;
                o.id = pds[i].id;
                obj.bizId = pds[i].bizId;
                obj.nodes.push(o);
            }
            obj.links = data.links;
            
            obj.attr = JSON.stringify(data.panel.attr);
            obj.id = data.panel.id;//这个要处理。。。。
            obj.bizId = data.panel.bizId;

            oc.util.ajax({
            url:oc.resource.getUrl('portal/business/canvas/updateCanvas.htm'),
                data:{
                    canvasData:JSON.stringify(obj)
                },
                successMsg:null,
                success:function(d){
                    //console.log(d);
                    if(typeof(callback)=='function')
                        callback(d);
                }
            });
        },
        addNode:function(data,callback){
            var p = this.topoSVG.businessView.getPanel();
            data.bizId = p.bizId;
            data.id = (+new Date);
            if(!data.attr.type)
               data.attr.type = 'net'; 
            if(data.attr.type == 'rect' || data.attr.type == 'circle' ||
                data.attr.type == 'ellipse'){
                if(!data.attr.bgStyle)
                    data.attr.bgStyle = this.topoSVG.bizCfg.get('node').attr.bgStyle;
            }
            if(data.attr.type == 'textNode' ){
                if(!data.attr.bgStyle)
                    data.attr.bgStyle = this.topoSVG.bizCfg.get('node').attr.bgStyle;
                if(!data.attr.fontStyle)
                    data.attr.fontStyle = this.topoSVG.bizCfg.get('node').attr.fontStyle;
            }
            var dt = {bizId:p.bizId,nodes:[data]};

            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/canvas/insertCanvasNode.htm'),
                data:{
                    canvasData:JSON.stringify(dt)
                },
                successMsg:null,
                success:function(d){
                    //console.log(d);
                    if(d.code != 200){
                        alert("添加失败！");
                    }
                    data = d.data[0];
                    data.attr = eval('(' + data.attr + ')');
                    
                    if(typeof(callback)=='function')
                        callback(data);
                }
            });
        },
        deleteNode:function(data,callback){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/canvas/deleteCanvasNode.htm'),
                data:{
                    ids:data
                },
                successMsg:null,
                success:function(d){
                    //console.log(d);
                    if(d.code != 200 || !d.data){
                        alert("删除失败！");
                    }
                    if(typeof(callback)=='function')
                        callback(d);
                }
            });
        },
        addLink:function(data,callback){//删除节点元素
            ///portal/business/canvas/insertCanvasLink(BizCanvasLinkBo link)
            ///portal/business/canvas/deleteCanvasLink(String ids)
            var p = this.topoSVG.businessView.getPanel();
            data.bizId = p.bizId;
            var dt = {fromNode: data.fromNode,toNode:data.toNode,attr:JSON.stringify(data.attr)};
            if(!dt.fromNode || !dt.fromNode){
                if(typeof(callback)=='function')
                        callback({code:-1});
                return;
            }

            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/canvas/insertCanvasLink.htm'),
                data:{
                    link:JSON.stringify(dt)
                },
                successMsg:null,
                success:function(d){
                    if(typeof(callback)=='function')
                        callback(d);
                }
            });
        },
        deleteLink:function(data,callback){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/canvas/deleteCanvasLink.htm'),
                data:{
                    ids:data
                },
                successMsg:null,
                success:function(d){
                    if(typeof(callback)=='function')
                        callback(d);
                }
            });
        },
        /*右键菜单状态查询*/
        getRightMenuStatus:function(bizId,callback){
            try{
                oc.util.ajax({
                    url:oc.resource.getUrl('portal/business/cap/getResponeTimeMetricInstanceList.htm'),
                    data:{bizid:bizId},
                    async:false,
                    success:function(data){
                        callback && callback(data.data);
                    }
                });
            }catch(e){
                callback && callback();
            }

        }
    };

   /* 获取两个节点的最小连接点
    function getMinConnetP(from,to,nodes){
        var f = nodes[from.id];
        var t = nodes[to.id];

        console.log(nodes);
    }//*/
    return tmp;
});