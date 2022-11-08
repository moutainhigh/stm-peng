(function($){
	var rowData,canvas,graph,baseArrow;
	//分割线format
	var hr = "M{0},{1}H{2}";
	var baseColor = "#00B308";
	var lanes = {};
	//id:Container
	var getConById = {};
	var util = new Physics();
	var refreshInterval,randomId;
	var templateWidth = 50;
	var templateHeight = 65;
	var unitRowCount = null;
	var serviceRowCount = null;
	var appRowCount = null;
	var physicsRowCount = null;
	//默认设备间距
	var defaultMagin = 20;
	var skin=Highcharts.theme.currentSkin;
	var status = {	
			NORMAL : "themes/"+skin+"/images/bizser/statusAppIMG/zhengchang.png",
			WARN : "themes/"+skin+"/images/bizser/statusAppIMG/jingao.png",
			SERIOUS : "themes/"+skin+"/images/bizser/statusAppIMG/yanzhong.png",
			CRITICAL : "themes/"+skin+"/images/bizser/statusAppIMG/zhiming.png"
		};
	var alarm = {src:status.NORMAL,rx:35,ry:35,w:20,h:20};
	//容器模版
	var templet = {width:templateWidth,height:templateHeight,
			texts:[{text:"财务部",rx:30,ry:65,attr:{fill:"white","font-size": 12}}],
			imgs:[{src:"themes/"+skin+"/images/bizser/resourceimgHD/Host.png",rx:5,ry:5,w:40,h:40,isDrag:true}],
			rects:[],containers:[]};
	/**初始化泳道*/
	function initLane(data){
		//根据数量判断泳道高度
		var unitCount = data.unit.length;
		var serviceCount = data.service.length;
		var appCount = data.app.length;
		var physicsCount = null;
		if(data.app && data.app.length >0){
			for(var i = 0 ; i < data.app.length ; i ++){
				var singleApp = data.app[i];
				if(singleApp.resourceInstances && singleApp.resourceInstances.length > 0){
					if(singleApp.resourceInstances.length <= 2){
						physicsCount += singleApp.resourceInstances.length;
					}else{
						physicsCount += 2;
					}
				}
			}
		}

		//默认设备纵向间距
		var resourceVerticalMargin = 5;
		
		var width = $(graph[0]).width();
		
		//文字区
		var textArea = width*.12;
		//计算出一排能放多少个设备
		var singleRowNum =  Math.floor((width - textArea) / (defaultMagin + templateWidth));
		//判断泳道设备是否超过一排最多设备数
		unitRowCount = Math.ceil(unitCount / singleRowNum);
		serviceRowCount = Math.ceil(serviceCount / singleRowNum);
		appRowCount = Math.ceil(appCount / singleRowNum);
		physicsRowCount = Math.ceil(physicsCount / singleRowNum);
		
		//默认单个泳道摆设行数
		var singleLaneRows = Math.floor(($(graph[0]).height/4)/(templateHeight+resourceVerticalMargin));
		
		var unitHeight = null;
		var serviceHeight = null;
		var appHeight = null;
		var physicsHeight = null;
			
		//判断是否需要滚动条
		if((unitRowCount + serviceRowCount + appRowCount + physicsRowCount) > singleLaneRows * 4){
			//需要滚动条
			var beyondHeight = ((unitRowCount + serviceRowCount + appRowCount + physicsRowCount) - singleLaneRows * 4) * (templateHeight+resourceVerticalMargin);
			$(graph[0]).height($(graph[0]).height() + beyondHeight);
			
			var emptyHeight = ($(graph[0]).height() - 
					(unitRowCount + serviceRowCount + appRowCount + physicsRowCount) * (templateHeight+resourceVerticalMargin))/4;
			
			unitHeight = unitRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
			serviceHeight = serviceRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
			appHeight = appRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
			physicsHeight = physicsHeight * (templateHeight+resourceVerticalMargin) + emptyHeight;
		}else{
			//不需要
			//计算紧凑排列后单条泳道剩余空间
			$(graph[0]).height($(graph[0]).height() - 4);
			var emptyHeight = ($(graph[0]).height() - 
					(unitRowCount + serviceRowCount + appRowCount + physicsRowCount) * (templateHeight+resourceVerticalMargin))/4;
			
			unitHeight = unitRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
			serviceHeight = serviceRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
			appHeight = appRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
			physicsHeight = physicsRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
		}
		
		var adaptHeight = [unitHeight,serviceHeight,appHeight,physicsHeight];
		
		canvas = new Canvas(graph[0]);
		lane = new Lane(canvas);
		lane.create(adaptHeight);
		findLane();
		expand();
	}
	//找到泳道，全局引用
	function findLane(){
		for(var i in canvas.topContainers){
			var con = canvas.topContainers[i];
			if(con.data.type=="unit"){
				lanes.unit = con;
			}else if(con.data.type=="service"){
				lanes.service = con;
			}else if(con.data.type=="app"){
				lanes.app = con;
			}else if(con.data.type=="physics"){
				lanes.physics = con;
			}else{
				break;
			}
			//屏蔽泳道双击事件
			con.dblclick = function(){};
		}
	}	
	function getGraph(data){
		for(var i in data){
    		var lane = data[i];
    		for(var j=0;j<lane.length;j++){
    			var json = clone(templet);
    			json.texts[0].text = lane[j].name;
    			json.imgs[0].src = oc.resource.getUrl('platform/file/getFileInputStream.htm?fileId='+lane[j].fileId);
    			//将业务下的资源数据绑定在业务应用下
    			json.data = {id:lane[j].id,type:i,resourceInstances:lane[j].resourceInstances,bizMainIds:lane[j].bizMainIds};
    			if(i=="app"){
    				//加上报警灯
    				alarm.src = status[lane[j].status];
    				json.imgs.push(alarm);
    			}
    			lanes[i].addContainer(con = canvas.container(0,0,json.width,json.height).set(json),0,lanes[i].h/2-con.h/2);
    			getConById[lane[j].id] = con;
    		}
    	}
    	//业务单元、业务服务、业务应用居中排列
    	var appNum = toArray(lanes.app.containers).length;
		util.MARGIN = defaultMagin;
		setMagin(unitRowCount);
    	rank(lanes.unit,unitRowCount);
    	setMagin(serviceRowCount);
		rank(lanes.service,serviceRowCount);
		setMagin(appRowCount);
		rank(lanes.app,appRowCount);
		//重新设定资源排列间距
		util.MARGIN = appNum>1?10:50;
    	for(var i in lanes.app.containers){
    		var app = lanes.app.containers[i];
    		//应用下面的资源
    		app.to = [];
    		for(var j=0;j<app.data.resourceInstances.length;j++){
    			//如果只有一个业务应用就全部显示,否则只显示应用下面两个资源
    			if(appNum>1 && j>1) break;
    			var firstResource = app.data.resourceInstances[j];
    			var json = clone(templet);
    			if(firstResource && firstResource.text==undefined) continue;
    			json.texts[0].text = firstResource.text;
    			json.imgs[0].src = "themes/"+Highcharts.theme.currentSkin+"/images/bizser/resourceimgHD/"+firstResource.attributes.parentCategoryId+".png";
    			alarm.src = status[firstResource.attributes.status];
				json.imgs.push(alarm);
				json.data = {id:firstResource.id,type:"physics"};
    			var resource = canvas.container(0,0,json.width,json.height).set(json);
    			getConById[firstResource.id] = resource;
    			lanes.physics.addContainer(resource,app.RX,lanes.physics.h/2-resource.h/2);
    			app.to.push(resource);
    		}          		
    		//app.to.length>0 && util.axis_rank(app.to,app.x+app.w/2,2,60);
    		for(var k=0;k<app.to.length;k++){
    			canvas.onLine(app,app.to[k],{type:"S",attr:{stroke:baseColor,"stroke-width":2}});
    		}
    	}
    	setMagin(physicsRowCount);
    	rank(lanes.physics,physicsRowCount);
    	//业务单位
		if(rowData.type==0){
			var unit;
			for(var i in lanes.unit.containers){
				unit = lanes.unit.containers[i];
			}
			if(unit){
				unit.texts[0].attr({fill:"yellow"});
				for(var i in lanes.service.containers){
					var service = lanes.service.containers[i];
					canvas.onLine(unit,service,{type:"S",attr:{stroke:baseColor,"stroke-width":2}});
					var bizMainIds = service.data.bizMainIds;
					if(bizMainIds != null){
						for(var j=0;j<bizMainIds.length;j++){
							var app = getConById[bizMainIds[j]];
							if(app){
								canvas.onLine(service,app,{type:"S",attr:{stroke:baseColor,"stroke-width":2}});
							}
    					}
					}
				}
			}
		}else if(rowData.type==1){
			var service;
			for(var i in lanes.service.containers){
				service = lanes.service.containers[i];
			}
			if(service){
				service.texts[0].attr({fill:"yellow"});
				for(var i in lanes.unit.containers){
					var unit = lanes.unit.containers[i];
					canvas.onLine(unit,service,{type:"S",attr:{stroke:baseColor,"stroke-width":2}});
				}
				for(var i in lanes.app.containers){
					var app = lanes.app.containers[i];
					canvas.onLine(service,app,{type:"S",attr:{stroke:baseColor,"stroke-width":2}});
				}
			}
		}
		canvas.editable = false;
		
		var domTemp = canvas.dom;
		var widthTemp = $(domTemp).width();
		var heightTemp = $(domTemp).height();
		
		var bgTemp = {"src":"themes/" + Highcharts.theme.currentSkin + "/images/bizser/background/bgpng.png","w":widthTemp,"h":heightTemp};
		canvas.raphael.image(bgTemp.src,0,0,widthTemp,heightTemp).toBack();
		
		//开启刷新状态的
		refreshInterval = setInterval(refreshState,1000*30);
		
	}
	//设置间距
	function setMagin(rowCount){
		if(rowCount > 1){
			if(rowCount <= 3){
				util.MARGIN = -9.4 * rowCount;
			}else{
				util.MARGIN = (rowCount - 12.4) * rowCount;
			}
		}else{
			util.MARGIN = defaultMagin;
		}
	}
	function expand(){
		//为容器加上双击事件
		Container.prototype.dblclick = function(){
			var T = this;
			if(this.data.type=="physics"){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
	                oc.module.resmanagement.resdeatilinfonew.open({instanceId:T.data.id});
	            });
			}else if(this.data.type=="unit" || this.data.type=="service"){
				graph.empty();
				var row = {type:this.data.type=="unit"?0:1,id:this.data.id};
				oc.resource.loadScript('resource/module/business-service/js/graph.js?t'+new Date(),function(){
					oc.module.biz.ser.graph.portal(graph,row);
                });
			}else if(this.data.type=="app"){
				graph.empty();
				graph.load(oc.resource.getUrl('resource/module/business-service/business_list_tabs.html'),function(){
					oc.resource.loadScript('resource/module/business-service/js/tabs.js',function(){
				  		oc.business.service.tabs.open(T.data.id);
				  	});
			 	});
			}
		}
	}
	//刷新业务应用和资源状态
	function refreshState(){
		//假如退出了该页面,clearInterval
		if($("#"+randomId).length<1){
			clearInterval(refreshInterval);
			return;
		}
		var appIds = [],resourceIds = [];
		//业务应用泳道里的所有容器
		for(var i in lanes.app.containers){
			appIds.push(lanes.app.containers[i].data.id);
		}
		//支撑层所有已加载容器
		for(var i in lanes.physics.containers){
			resourceIds.push(lanes.physics.containers[i].data.id);
		}
		if(appIds.length>0 || resourceIds.length>0){
			oc.util.ajax({
				url: oc.resource.getUrl('portal/business/service/getNewlyStateByIds.htm'),
	            data:{ids:appIds.join(),resourceIds:resourceIds.join()},
	            successMsg:null,
	            startProgress:null,
	            success: function(rsp){
	            	for(var i=0;i<rsp.data.length;i++){
	            		for(var j=0;j<rsp.data[i].length;j++){
	            			var bo = rsp.data[i][j],con = getConById[bo.id];
		            		var boStatus = bo.status?bo.status:bo.attributes.status;
			        		con && con.imgs[1] && con.imgs[1].attr({src:status[boStatus]});
	            		}
	        		}
	            }
	        });
		}
	}
	//将散列json转换为数组
	function toArray(containers){
		var array = [];
		for(var i in containers){
			array.push(containers[i]);
		}
		return array;
	}
	//居中排列
	function rank(lane,rowCount){
		var arr = toArray(lane.containers);
		if(arr.length==0) return;
		util.axis_rank(arr,lane.w/2+lane.x,rowCount,templateHeight);
	}
	//克隆json
	function clone(obj) {
	    var o;
	    if (typeof obj == "object") {
	        if (obj === null) {
	            o = null;
	        } else {
	            if (obj instanceof Array) {
	                o = [];
	                for (var i = 0, len = obj.length; i < len; i++) {
	                    o.push(clone(obj[i]));
	                }
	            } else {
	                o = {};
	                for (var j in obj) {
	                    o[j] = clone(obj[j]);
	                }
	            }
	        }
	    } else {
	        o = obj;
	    }
	    return o;
	}
	//上层页面入口
	function portal(centerDiv,row){
		graph = centerDiv.empty();
		//生成随机ID的dom元素,用于判断该页面是否dead
		randomId = oc.util.generateId();
		$("<span></span>").attr("id",randomId).appendTo(graph);
		rowData = row;
		
		//获取数据
		oc.util.ajax({
			url: oc.resource.getUrl('portal/business/service/dep/getAllRelationsByIdAndType.htm'),
            data:{id:rowData.id,type:rowData.type},
            successMsg:null,
            success: function(rsp){
            	var data = rsp.data;
        		
        		initLane(data);
        		getGraph(data);
            	
            }         
		});
	}
	oc.ns('oc.module.biz.ser.graph');
	oc.module.biz.ser.graph={
		portal:function(centerDiv,row){
			portal(centerDiv,row);
		}
	};
})(jQuery)