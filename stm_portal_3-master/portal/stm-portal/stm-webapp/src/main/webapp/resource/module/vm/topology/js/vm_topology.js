$(function(){
	var vmGraphAreaId = oc.util.generateId(), graphArea = $("#vmGraphArea").attr('id',vmGraphAreaId);
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
	
	function graph(data){
		var topologyData,hostStoreRelationData,stateData;
		var templateWidth = 50;
		var templateHeight = 65;
		var defaultMagin = 20;
		var skin=Highcharts.theme.currentSkin;
		if(data.title){
			oc.util.ajax({
		    	url:oc.resource.getUrl('portal/vm/topologyVm/getTopologyData.htm?'),
		    	data:{vmfullname:data.title,datacenteruuid:data.datacenteruuid},
		    	timeout:null,
				startProgress:null,
				stopProgress:null,
				async:false,
		    	success:function(d){
		    		if(d.code && d.code ==200){
		    			topologyData=d.data[0]||[],hostStoreRelationData=d.data[1]||{},stateData=d.data[2]||{};
		    			
		    			//统计三个通道内设备数量
		    			var dataStoreNum = 0;
		    			var hostSystemNum = 0;
		    			var clusterComputeResourceNum = 0;
		    			for(var i=0;i<topologyData.length;i++){
		    				if(topologyData[i].vmtype == "Datastore"){
		    					dataStoreNum++;
		    				}else if(topologyData[i].vmtype == "HostSystem"){
		    					hostSystemNum++;
		    				}else if(topologyData[i].vmtype == "ClusterComputeResource"){
		    					clusterComputeResourceNum++;
		    				}
		    			}
		    			
		    			//分割线format
		    			var hr = "M{0},{1}H{2}";
		    			var lanes = {};
		    			var util = new Physics();
		    			//默认设备间距
		    			util.MARGIN = defaultMagin;
		    			//默认设备纵向间距
		    			var resourceVerticalMargin = 5;
		    			
		    			var status = {
	    					NORMAL : "themes/"+skin+"/images/bizser/statusIMG/zhengchang.png",
	    					UNKOWN : "themes/"+skin+"/images/bizser/statusIMG/weizhi.png",
	    					WARN : "themes/"+skin+"/images/bizser/statusIMG/jingao.png",
	    					SERIOUS : "themes/"+skin+"/images/bizser/statusIMG/yanzhong.png",
	    					CRITICAL : "themes/"+skin+"/images/bizser/statusIMG/zhiming.png",
	    					CRITICAL_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/zhiming_nothing.png",
	    					UNKNOWN_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/weizhi_nothing.png",
	    					NORMAL_CRITICAL : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_zhiming.png",
	    					NORMAL_UNKNOWN : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_weizhi.png",
	    					NORMAL_NOTHING : "themes/"+skin+"/images/bizser/statusIMG/zhengchang_nothing.png"
	    				};
		    			
		    			var width = graphArea.width();
		    			//文字区
		    			var textArea = width*.1;
		    			
		    			//计算出一排能放多少个设备
		    			var singleRowNum =  Math.floor((graphArea.width() - textArea) / (util.MARGIN + templateWidth));
		    			
		    			//判断泳道设备是否超过一排最多设备数
		    			var clusterRowCount = Math.ceil(clusterComputeResourceNum / singleRowNum);
		    			var hostRowCount = Math.ceil(hostSystemNum / singleRowNum);
		    			var dataStoreRowCount = Math.ceil(dataStoreNum / singleRowNum);
		    			
		    			//默认单个泳道摆设行数
		    			var singleLaneRows = Math.floor((graphArea.parent().height()/3)/(templateHeight+resourceVerticalMargin));
		    			
		    			var clusterHeight = null;
		    			var hostHeight = null;
		    			var dataStoreHeight = null;
		    				
	    				//判断是否需要滚动条
	    				if((clusterRowCount + hostRowCount + dataStoreRowCount) > singleLaneRows * 3){
	    					//需要滚动条
	    					var beyondHeight = ((clusterRowCount + hostRowCount + dataStoreRowCount) - singleLaneRows * 3) * (templateHeight+resourceVerticalMargin);
	    					graphArea.height(graphArea.parent().height() + beyondHeight);
	    					
	    					var emptyHeight = (graphArea.height() - 
	    							(clusterRowCount + hostRowCount + dataStoreRowCount) * (templateHeight+resourceVerticalMargin))/3;
	    					
	    					clusterHeight = clusterRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
	    					hostHeight = hostRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
	    					dataStoreHeight = dataStoreRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
	    				}else{
	    					//不需要
	    					//计算紧凑排列后单条泳道剩余空间
	    					graphArea.height(graphArea.parent().height() - 4);
	    					var emptyHeight = (graphArea.height() - 
	    							(clusterRowCount + hostRowCount + dataStoreRowCount) * (templateHeight+resourceVerticalMargin))/3;
	    					
	    					clusterHeight = clusterRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
	    					hostHeight = hostRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
	    					dataStoreHeight = dataStoreRowCount * (templateHeight+resourceVerticalMargin) + emptyHeight;
	    				}
		    			
		    			//泳道区
		    			var laneArea = width - textArea;
		    			//泳道相关data定义
		    			var laneDatas = [
		    			                 {type:"ClusterComputeResource",text:"Cluster",x:textArea,y:0,width:laneArea,height:clusterHeight},
		    			                 {type:"HostSystem",text:"ESXI主机",x:textArea,y:clusterHeight,width:laneArea,height:hostHeight},
		    			                 {type:"Datastore",text:"数据存储",x:textArea,y:(clusterHeight + hostHeight),width:laneArea,height:dataStoreHeight}];
		    			//容器模版
		    			var templet = {width:templateWidth,height:templateHeight,
		    					texts:[{text:"192.168.1.12",rx:30,ry:65,attr:{fill:Highcharts.theme.bizTextColor}}],
		    					imgs:[{src:"themes/"+skin+"/images/bizser/resourceimgHD/Host.png",rx:5,ry:5,w:40,h:40},
		    						{src:"themes/"+skin+"/images/bizser/statusIMG/zhengchang.png",rx:30,ry:30,w:20,h:20}]};
		    			var canvas = graphArea.canvas();	
		    			//左边菜单栏
		    			var left = canvas.raphael.rect(0, 0,textArea,canvas.raphael.height).attr({fill:Highcharts.theme.bizLaneTitleRightBorderColor,stroke:Highcharts.theme.bizLaneBorderColor,"fill-opacity":0.2,"stroke-width": 0});	
		    			for(var i=0;i<laneDatas.length;i++){				
		    				var lane = laneDatas[i];
//		    				var container = canvas.container(lane.x,lane.y,lane.width,lane.height*(lane.type!="Datastore"?1:8));		
//		    				container && container.addText(canvas.raphael.text(0, 0,lane.text).attr({stroke:"white","font-size": 16,fill:"white"}),-lane.x/2,lane.height*(lane.type!="Datastore"?1:7)/2);
		    				var container = canvas.container(lane.x,lane.y,lane.width,lane.height);		
		    				container && container.addText(canvas.raphael.text(0, 0,lane.text).attr({fill:Highcharts.theme.bizTextColor,stroke:"none","font-size":16,"font-family":"微软雅黑"}),-lane.x/2,lane.height/2);
//		    				var container = canvas.container(lane.x,lane.y,lane.width,lane.height);		
//		    				container && container.addText(canvas.raphael.text(0, 0,lane.text).attr({stroke:"white","font-size": 16,fill:"white"}),-lane.x/2,(lane.type!="Datastore"?lane.height:lane.height*7)/2);
		    				container.drag = function(){};
		    				var obj = {};
		    				obj.data = lane;
		    				obj.entity = container;	
		    				lanes[lane.type] = obj;
		    				canvas.raphael.path(Raphael.format(hr,0,lane.y,width)).attr({"stroke-width":1,stroke:Highcharts.theme.bizLaneBorderColor,"stroke-dasharray":"- ","arrow-end":"block"});
//		    				canvas.raphael.path(Raphael.format(hr,0,height*(lane.type!="Datastore"?i:6),width)).attr({"stroke-width":1,stroke:baseColor,"stroke-dasharray":"- ","arrow-end":"block"});
		    			}
		    			for(var i=0;i<topologyData.length;i++){
		    				if(topologyData[i].vmtype!="VirtualMachine"){
		    					var parent = lanes[topologyData[i].vmtype].entity;
		    					var json = clone(templet);
		    					json.texts[0].text = topologyData[i].vmname;
		    					if(stateData[topologyData[i].instanceid]){
		    						json.imgs[1].src = status[stateData[topologyData[i].instanceid]];
		    					}
		    					json.data = topologyData[i];
		    					parent.addContainer(con = canvas.container(0,0,json.width,json.height).set(json),0,parent.h/2-con.h/2);
		    					con.handler.attr({"stroke-width":0});
		    					con.center = {x:con.x+con.w/2,y:con.y+con.h/2}
		    					con.angle = [Raphael.atan(con.x+con.w,con.y,con.center.x,con.center.y),Raphael.atan(con.x+con.w,con.y+con.h,con.center.x,con.center.y),
		    								 Raphael.atan(con.x,con.y+con.h,con.center.x,con.center.y),Raphael.atan(con.x,con.y,con.center.x,con.center.y)];
		    				}
		    			}
		    			setMagin(clusterRowCount);
		    			rank(lanes['ClusterComputeResource'].entity,clusterRowCount);
		    			setMagin(hostRowCount);
		    			rank(lanes['HostSystem'].entity,hostRowCount);
		    			setMagin(dataStoreRowCount);
		    			rank(lanes['Datastore'].entity,dataStoreRowCount);
		    			var ClusterComputeResourceArr = toArrayAndAddEvent(lanes.ClusterComputeResource.entity.containers),
		    				HostSystemArr = toArrayAndAddEvent(lanes.HostSystem.entity.containers),
//		    				VirtualMachineArr = toArray(lanes.VirtualMachine.entity.containers),
		    				DatastoreArr = toArrayAndAddEvent(lanes.Datastore.entity.containers);
		    			online();
		    			canvas.isEditable = false;
		    			function online(){
		    				// ClusterComputeResource to HostSystem
		    				for(var i=0;i<ClusterComputeResourceArr.length;i++){
		    					for(var k=0;k<HostSystemArr.length;k++){
		    						if(HostSystemArr[k].data().puuid == ClusterComputeResourceArr[i].data().uuid){
		    							canvas.onLine(ClusterComputeResourceArr[i],HostSystemArr[k]);
		    						}
		    					}
		    				}
//		    				// HostSystem to VirtualMachine
//		    				for(var i=0;i<HostSystemArr.length;i++){
//		    					for(var k=0;k<VirtualMachineArr.length;k++){
//		    						if(VirtualMachineArr[k].data().puuid == HostSystemArr[i].data().uuid){
//		    							canvas.onLine(HostSystemArr[i],VirtualMachineArr[k]);
//		    						}
//		    					}
//		    				}
		    				//Datastore relation with others(line HostSystem to Datastore for now)
		    				if(hostStoreRelationData){
		    					for(var p in hostStoreRelationData){
		    						for(var i=0;i<HostSystemArr.length;i++){
		    							if(p ==  HostSystemArr[i].data().instanceid){
		    								var hostStoreRelationDataArray = hostStoreRelationData[p][0].split(',');
		    								for(var k=0;k<hostStoreRelationDataArray.length;k++){
		    									for(var m=0;m<DatastoreArr.length;m++){
		    										if(hostStoreRelationDataArray[k] == DatastoreArr[m].data().uuid){
		    											canvas.onLine(HostSystemArr[i],DatastoreArr[m]);
		    										}	
		    									}
		    								}
		    							}
		    						}
		    					}
		    				}
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
		    			
		    			function toArray(containers){
		    				var array = [];
		    				for(var i in containers){
		    					array.push(containers[i]);
		    				}
		    				return array;
		    			}
		    			function toArrayAndAddEvent(containers){
		    				var array = [];
		    				for(var i in containers){
		    					addForwardEvent(containers[i]);
		    					array.push(containers[i]);
		    				}
		    				return array;
		    			}
		    			//为需要点击跳转的加event事件
		    			function addForwardEvent(con){
		    				con.handler.attr({href:"javascript:void(0)",title:"点击查看\""+con.texts[0].attrs.text+"\"详情"}).dblclick(function(){forward(con);});
		    			}
		    			function forward(param){
		    				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
		    					oc.module.resmanagement.resdeatilinfonew.open({instanceId:param.data().instanceid});
		    			 	});
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
		    			
		    		}
		    	}
			});
		}

	}
	
	//左侧菜单折叠按钮
	function collapse_btn(){
		$("#collapse_btn").click(function(){
			if($(this).hasClass('fa-angle-double-left')){
				var gs = $('.oc_vm_index');
				var gt = $('.oc_vm_index').layout('panel','west');
				$('.oc_vm_index').layout('collapse','west');
				$('.oc_vm_index').layout('panel','center').parent().css('left','0px');
				$(this).removeClass('fa-angle-double-left');
				$(this).addClass('fa-angle-double-right');
			}else{
				$('.oc_vm_index').layout('expand','west');
				$(this).removeClass('fa-angle-double-right');
				$(this).addClass('fa-angle-double-left');
			}
		});
	}
	
	oc.ns('oc.module.vm.topology');
	oc.module.vm.topology={
		graph:function(data){
			graph(data);
			var refreshInterval = setInterval(
					function(){
						if($("#"+vmGraphAreaId).length==1){
							oc.vm.indexpage.openVmTopologyClick(data);
						}
						clearInterval(refreshInterval);
					},1000*60);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(refreshInterval);
			}else{
				tasks = new Array();
				tasks.push(refreshInterval);
				oc.index.indexLayout.data("tasks", tasks);
			}
			collapse_btn();
		},
		graphForHome:function(data){
			$('#topologyMainDiv').find('#collapse_btn').remove();
			graph(data);
		}
	};
})
