function focusResource(selector,opt){
	this.opt = {
		type:'application' // resourceList|host|netdevice|application
	};
	this.opt.selector = selector;

	this.opt = $.extend(true,this.opt,opt,true);
	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}
var frp = focusResource.prototype;

frp.init = function(){
	if($("" + this.opt.selector.selector).length == 0){
		return;
	}
	this.dataObj = $("<div/>");
	this.dataObj.css({width:'100%',height:'100%'});
	this.opt.selector.html(this.dataObj);
	if(this.opt.showMethod == 'businessList'){
		this.genbusinessTable(this.opt.resources);
	}else{
		var res ={};
		try{res =  JSON.parse(this.opt.resources);}catch(e){throw 'wrong data!'}
		if(this.opt.showMethod == 'resourceDetail'){
			var obj = undefined;
			for(var i in res){
				obj = res[i];
				break;
			}
			if(!obj)
				return;

			if(obj.rootId == 'Host'){
				this.genHostGrid(obj);
			}else if(obj.rootId == 'NetworkDevice'){//
				this.genNetdeviceTable(obj,this.opt.showPanel);
			}else{//application
				this.genApplicationGrid(obj);
			}
		}else if(this.opt.showMethod == 'resourceList'){
			var rids = "";
			for(var i in res){
				rids +=  i + ','
			}
			this.genResourceGrid(rids);
		}
	}
}

frp.refresh = function(){
	var _this = this;
	_this.dataObj.remove();
	if(!_this.refreshing){
		_this.refreshing = true;
    	this.init();
    	setTimeout(function(){
    		delete _this.refreshing;
    	},2000);
	}else{
		//console.log('仍在刷新中...');
	}
}

//关注资源
frp.genResourceGrid = function(resourceIds){
	var that = this;
	if(resourceIds.substring(resourceIds.length-1) == ","){
		resourceIds = resourceIds.substring(0,resourceIds.length-1);
	}
	
	//如果appType是URL这删除CPU和内存利用率
	var resources = that.opt.resources;
	if(resources != null && (resources.indexOf('"pid":"URL"') > -1)){
		that.dg =oc.ui.datagrid({
			selector : that.dataObj,
			url : oc.resource.getUrl('home/availability/getById.htm'),
			queryParams:{resourceId:resourceIds},
			pagination : false,
			columns : [[
			 {field:'sourceName',title:'资源名称',sortable:true,align:'left',width:'30%',
		         formatter:function(value,row,rowIndex){
						// 加入手形样式
						var statusLabel = $('<label style="cursor:pointer;"/>').addClass("light-ico_resource " + row.instanceStatus);
						statusLabel.attr('rowIndex', rowIndex).addClass('quickSelectDetailInfo');
						if(value != null){
							statusLabel.attr('title', value).html(value.htmlspecialchars());
					 	}
						return statusLabel;
					}}, {
				field : 'ipAddress',
				title : 'IP地址',
				width : '40%'
			}, {
				field : 'monitorType',
				title : '监控类型',
				width : '30%'
			}]],
			onClickCell : function(rowIndex, field, value){
				if(field == 'sourceName'){
					var row = that.dg.selector.datagrid('getRows')[rowIndex];
					var resourceId = row.id;
					oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
						oc.module.resmanagement.resdeatilinfonew.open({instanceId:resourceId});
				 	});
				}
			},
	         onLoadSuccess : function(data){
	        	 //target.updateTableWidth(datagrid);
	         }
		});
		return;
	}
	that.dg =oc.ui.datagrid({
		selector : that.dataObj,
		url : oc.resource.getUrl('home/availability/getById.htm'),
		queryParams:{resourceId:resourceIds},
		pagination : false,
		columns : [[
		 {field:'sourceName',title:'资源名称',sortable:true,align:'left',width:'20%',
	         formatter:function(value,row,rowIndex){
					// 加入手形样式
					var statusLabel = $('<label style="cursor:pointer;"/>').addClass("light-ico_resource " + row.instanceStatus);
					statusLabel.attr('rowIndex', rowIndex).addClass('quickSelectDetailInfo');
					if(value != null){
						statusLabel.attr('title', value).html(value.htmlspecialchars());
				 	}
					return statusLabel;
				}}, {
			field : 'ipAddress',
			title : 'IP地址',
			width : '15%'
		}, {
			field : 'monitorType',
			title : '监控类型',
			width : '15%'
		}, {field:'cpuAvailability',title:'CPU利用率',sortable:false,align:'left',width:'25%',
	         formatter:function(value,row,rowIndex){
	        	 var cpuPercent = null;
	        	 var cpuStatus = 'gray';
	        	 if(null != row.cpuAvailability){
	        		 cpuPercent = row.cpuAvailability;
	        	 }else{
	        		 cpuPercent = '--';
	        	 }
	        	 if(null != row.cpuStatus){
	        		 if(row.instanceStatus!='gray'){
	        			 cpuStatus = row.cpuStatus;
	        		 }
	        	 }
	        	 
	        	 
	        	 return '<div class="rate rate-t-'+cpuStatus+'"><span class="rate-'+cpuStatus+'" style="width:'+cpuPercent+'"></span></div><span class="rt">'+cpuPercent+'</span>';
	    }}, {field:'memoryAvailability',title:'内存利用率',sortable:false,align:'left',width:'25%',
        	 formatter:function(value,row,rowIndex){
        		 var memoPercent = null;
        		 var memStatus = 'gray';
	        	 if(null != row.memoryAvailability){
	        		 memoPercent = row.memoryAvailability;
	        	 }else{
	        		 memoPercent = '--';
	        	 }
	        	 if(null != row.memoryStatus&&row.instanceStatus!='gray'){
	        		 memStatus = row.memoryStatus;
	        	 }
        	     return '<div class="rate rate-t-'+memStatus+'"><span class="rate-'+memStatus+'" style="width:'+memoPercent+'"></span></div><span class="rt">'+memoPercent+'</span>';
         }}]],
         onClickCell : function(rowIndex, field, value){
				if(field == 'sourceName'){
					var row = that.dg.selector.datagrid('getRows')[rowIndex];
					var resourceId = row.id;
					oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
						oc.module.resmanagement.resdeatilinfonew.open({instanceId:resourceId});
				 	});
				}
			},
         onLoadSuccess : function(data){
        	 //target.updateTableWidth(datagrid);
         }
	});
}

//关注应用
frp.genApplicationGrid = function(obj){
	var rid = obj.id;
	rid = (rid ==undefined)?12001:rid;
	
	var that = this;
	if(that.app != null){
        echarts.dispose(that.app._AppCPUCharts);
        echarts.dispose(that.app._AppMenoryCharts);
        echarts.dispose(that.app._CPUCharts);
        echarts.dispose(that.app._MenoryCharts);
		/*if(that.app._AppCPUCharts != null){
			that.app._AppCPUCharts.charts.destroy();
		}
		if(that.app._AppMenoryCharts != null){
			that.app._AppMenoryCharts.charts.destroy();
		}
		if(that.app._CPUCharts != null){
			that.app._CPUCharts.charts.destroy();
		}
		if(that.app._MenoryCharts != null){
			that.app._MenoryCharts.charts.destroy();
		}*/
		that.app.selector.remove();
		that.app = null;
	}
	that.dataObj.load(oc.resource.getUrl('resource/module/home/widgets/js/focusResource/app.html'),function(){
		oc.resource.loadScripts([
			'resource/module/home/widgets/js/focusResource/js/workbench.js',
			'resource/module/home/widgets/js/focusResource/js/app.js'], function(){
			$("#oc-module-home-workbench-app-" + rid).remove();
			$("#oc-module-home-workbench-app").attr("id","oc-module-home-workbench-app-" + rid);
            
			setTimeout(function(){
				var dt = {outerMainDivId:'oc-module-home-workbench-app-' + rid, resourctId:rid };
				//如果appType是URL这删除CPU和利用率div
				var resources = that.opt.resources;
				if(resources){
					var data = JSON.parse(resources);
					if(data[rid] && data[rid].pid == 'URL'){
						$("#oc-module-home-workbench-app-" + rid).find(".sub-layout-div:eq(0)").remove();
						$("#oc-module-home-workbench-app-" + rid).find(".sub-layout-div:eq(0)").remove();	
						$("#oc-module-home-workbench-app-" + rid).find(".sub-layout-div:eq(0)").css("width","100%");
					}
				}
                dt.properties = that.opt.properties;
				that.app = new oc.index.home.widget.focusresource.showAppInfo(dt);
//				that.app = null;
			},1500);
			
		});
	});
}

//关注主机
frp.genHostGrid = function(obj){
	var rid = obj.id;
	rid = (rid ==undefined)?2116:rid;

	var that = this;
	if(that.host != null){
		/*if(that.host._CPUChart != null){
			that.host._CPUChart.charts.destroy();
		}
		if(that.host._RAMChart != null){
			that.host._RAMChart.charts.destroy();
		}*/
        var myChart = echarts.getInstanceByDom(that.host._CPUChartDiv[0]);
        echarts.dispose(myChart);
        myChart = echarts.getInstanceByDom(that.host._RAMChartDiv[0]);
        echarts.dispose(myChart);

		that.host._mainDiv.remove();
		that.host._CPUChartDiv.remove();
		that.host._RAMChartDiv.remove();
		that.host = null;
	}
	
	that.dataObj.load(oc.resource.getUrl('resource/module/home/widgets/js/focusResource/mainmachine.html'),function(){
			oc.resource.loadScripts([
				'resource/module/home/widgets/js/focusResource/js/workbench.js',
				'resource/module/home/widgets/js/focusResource/js/mainmachine.js'], function(){
				setTimeout(function(){
					$("#oc-module-home-workbench-mainmachine-" + rid).remove();
					$("#oc-module-home-workbench-mainmachine").attr("id","oc-module-home-workbench-mainmachine-" + rid);
					var dt = {outerMainDivId:'oc-module-home-workbench-mainmachine-' + rid, id:rid};
                    dt.properties = that.opt.properties;
					that.host = new oc.index.home.widget.focusresource.mainmachine(dt);
//					that.host = null;
				},1000);
			});
	});

}

//关注网络设备
frp.genNetdeviceTable = function(obj,ext){
	var rid = obj.id;
	rid = (rid ==undefined)?2123:rid;
	ext = (ext == undefined)?'all':ext;
	var that = this;
	if(that.nDevice != null){
		/*if(that.nDevice._CPUChart != null){
			that.nDevice._CPUChart.charts.destroy();
		}
		if(that.nDevice._RAMChart != null){
			that.nDevice._RAMChart.charts.destroy();
		}*/
        var myChart = echarts.getInstanceByDom(that.nDevice._CPUChartDiv[0]);
        echarts.dispose(myChart);
        myChart = echarts.getInstanceByDom(that.nDevice._RAMChartDiv[0]);
        echarts.dispose(myChart);
		that.nDevice._mainDiv.remove();
		that.nDevice.contentDiv.remove();
		that.nDevice._CPUChartDiv.remove();
		that.nDevice._RAMChartDiv.remove();
		if(that.nDevice._imgDiv != null){
            that.nDevice._imgDiv.remove();
        }
        if(that.nDevice._imgActive instanceof Object){
            that.nDevice._imgActive.remove();
        }
		that.nDevice._interfaceDetails.remove();
		that.nDevice = null;
	}
	that.dataObj.load(oc.resource.getUrl('resource/module/home/widgets/js/focusResource/netdevice.html'),function(){
		oc.resource.loadScripts([
					'resource/module/home/widgets/js/focusResource/js/workbench.js',
					'resource/module/home/widgets/js/focusResource/js/netdevice.js'], function(){
			setTimeout(function(){
				$("#oc-module-home-workbench-netdevice-" + rid).remove();
				$("#oc-module-home-workbench-netdevice").attr("id","oc-module-home-workbench-netdevice-" + rid);	
				var dt = {id:rid,outerMainDivId:'oc-module-home-workbench-netdevice-' + rid, moduleDiv:$('#oc-module-home-workbench-netdevice-' + rid), ext1:ext};
                dt.properties = that.opt.properties;
				that.nDevice = new oc.index.home.widget.focusresource.nDevice(dt);
//				that.nDevice = null;
			},1000);
		});
	});
}

//业务总览
frp.genbusinessTable = function(resources){
	if(!resources || resources.length <1)
		return;
	var _this = this;

	var bizIds = "";
	for(var i=0; i <resources.length;i++){
		bizIds += resources[i].id + ',';
	}
	if(bizIds.length>0){
		bizIds = bizIds.substring(0,bizIds.length-1);
	}

	var endTime =  +new Date;
	var startTime = endTime - 24*60*60*1000;


	var that = this;
	var $obj = $('<div/>');
	$obj.attr('id',oc.util.generateId());
	that.dataObj.html($obj);

	var queryParams = {
            	startTime:startTime,
            	endTime:endTime,
            	bizIds:bizIds
            };

    var runPageListData = undefined;

	that.bs = oc.ui.datagrid({
			selector:$obj,
            pagination: false,
            remoteSort: false,
            columns:[[
                {
                    field:'bizName',
                    title:'<span class="biz-name">业务名称</span>',
                    width:'16%',
                    formatter: function(value,row,index){
                        var light = '';
                        if(row.status==0){
                            light='<label style="cursor:pointer" title="'+row.bizName+'" rowIndex="'+index+'" class="light-ico greenlight bizname">'+row.bizName+'</label>';
                        }else if(row.status==1){
                            light='<label style="cursor:pointer" title="'+row.bizName+'" rowIndex="'+index+'" class="light-ico yellowlight bizname">'+row.bizName+'</label>';
                        }else if(row.status==2){
                            light='<label style="cursor:pointer" title="'+row.bizName+'" rowIndex="'+index+'" class="light-ico orangelight bizname">'+row.bizName+'</label>';
                        }else if(row.status==3){
                            light='<label style="cursor:pointer" title="'+row.bizName+'" rowIndex="'+index+'"  class="light-ico redlight bizname">'+row.bizName+'</label>';
                        }
                        return light;
                       //return '<a class="biz-operation-name" rowIndex="' + index + '" href="javascript:;">' + value + '</a>'
                    }
                },
                {field:'healthScore',title:'健康度',width:'14%',sortable:true,
                    formatter: function(value,row,index){
                        return value + '分';
                    }},
                {field:'availableRate',title:'可用率',sortable:true,width:'14%'},
                {field:'mttr',title:'MTTR <a class="operation-help" title="Mean Time To Repair"></a>',sortable:true,width:'14%'},
                {field:'mtbf',title:'MTBF <a class="operation-help" title="Mean Time To Failure"></a>' ,sortable:true,width:'14%'},
                {field:'downTime',title:'宕机时长',sortable:true,width:'14%'},
                {field:'outageTimes',title:'宕机次数',sortable:true,width:'14%'}
            ]],
            onClickCell : function(rowIndex, field, value){
				if(field == 'bizName'){
					var row = that.bs.datagrid('getRows')[rowIndex];
					var resourceId = row.id;
					 oc.resource.loadScript("resource/module/business-service/biz-detail-info/js/business_detail_info.js", function(){
                        oc.module.biz.detailinfo.open({bizId: row.bizId,bizName:row.bizName});
                    });
				}
			},
            onLoadSuccess: function(data){
            	//console.log(data);
            	//_this.bizdatas = data.rows;

            },
            onSortColumn : function(sort,order){
            	var sortData = runPageListData;
            	sortDatas(sortData, sort,order);
            	$obj.datagrid('loadData',{"code":200,"data":{"total":0,"rows":sortData}});
            }
        });
	
		that.bs = that.bs.selector;

		loadgrid(queryParams);

		function loadgrid(queryParams) {
            //查询参数直接添加在queryParams中
            oc.util.ajax({
                url:oc.resource.getUrl('home/biz/getTable.htm?dataType=json'),
                timeout:null,
                data:queryParams,
                success:function(data){
                	if(data.code == 200){
                		runPageListData = data.data.rows;
                		$obj.datagrid('loadData',data);
                	}else{
                		alert('获取运行情况失败！');
                	}
                		
                }
            });
        }

		function sortDatas(sortData, sort,order){
			if(order == 'asc'){
            		if(sort == 'healthScore'){
            			sortData.sort(function(o1,o2){
            				var value1 = parseInt(o1.healthScore);
            				var value2 = parseInt(o2.healthScore);
            				if(value1 > value2){
            					return 1;
            				}else{
            					return -1;
            				}
            			});
            		}else if(sort == 'mtbf'){
            			sortData.sort(function(o1,o2){
            				var value1 = getSecondValue(o1.mtbf);
            				var value2 = getSecondValue(o2.mtbf);
            				if(value1 > value2){
            					return 1;
            				}else{
            					return -1;
            				}
            			});
            		}else if(sort == 'availableRate'){
            			sortData.sort(function(o1,o2){
            				var value1 = parseInt(o1.availableRate.replace('%',''));
            				var value2 = parseInt(o2.availableRate.replace('%',''));
            				if(value1 > value2){
            					return 1;
            				}else{
            					return -1;
            				}
            			});
            		}else if(sort == 'mttr'){
            			sortData.sort(function(o1,o2){
            				var value1 = getSecondValue(o1.mttr);
            				var value2 = getSecondValue(o2.mttr);
            				if(value1 > value2){
            					return 1;
            				}else{
            					return -1;
            				}
            			});
            		}else if(sort == 'downTime'){
            			sortData.sort(function(o1,o2){
            				var value1 = getSecondValue(o1.downTime);
            				var value2 = getSecondValue(o2.downTime);
            				if(value1 > value2){
            					return 1;
            				}else{
            					return -1;
            				}
            			});
            		}else if(sort == 'outageTimes'){
            			sortData.sort(function(o1,o2){
            				var value1 = parseInt(o1.outageTimes);
            				var value2 = parseInt(o2.outageTimes);
            				if(value1 > value2){
            					return 1;
            				}else{
            					return -1;
            				}
            			});
            		}		
        	}else{
        		if(sort == 'healthScore'){
        			sortData.sort(function(o1,o2){
        				var value1 = parseInt(o1.healthScore);
        				var value2 = parseInt(o2.healthScore);
        				if(value1 < value2){
        					return 1;
        				}else{
        					return -1;
        				}
        			});
        		}else if(sort == 'mtbf'){
        			sortData.sort(function(o1,o2){
        				var value1 = getSecondValue(o1.mtbf);
        				var value2 = getSecondValue(o2.mtbf);
        				if(value1 < value2){
        					return 1;
        				}else{
        					return -1;
        				}
        			});
        		}else if(sort == 'availableRate'){
        			sortData.sort(function(o1,o2){
        				var value1 = parseInt(o1.availableRate.replace('%',''));
        				var value2 = parseInt(o2.availableRate.replace('%',''));
        				if(value1 < value2){
        					return 1;
        				}else{
        					return -1;
        				}
        			});
        		}else if(sort == 'mttr'){
        			sortData.sort(function(o1,o2){
        				var value1 = getSecondValue(o1.mttr);
        				var value2 = getSecondValue(o2.mttr);
        				if(value1 < value2){
        					return 1;
        				}else{
        					return -1;
        				}
        			});
        		}else if(sort == 'downTime'){
        			sortData.sort(function(o1,o2){
        				var value1 = getSecondValue(o1.downTime);
        				var value2 = getSecondValue(o2.downTime);
        				if(value1 < value2){
        					return 1;
        				}else{
        					return -1;
        				}
        			});
        		}else if(sort == 'outageTimes'){
        			sortData.sort(function(o1,o2){
        				var value1 = parseInt(o1.outageTimes);
        				var value2 = parseInt(o2.outageTimes);
        				if(value1 < value2){
        					return 1;
        				}else{
        					return -1;
        				}
        			});
        		}
        	}
			return sortData;
		}

		//将不同单位的时间值转为秒
        function getSecondValue(value){
        	if(value.indexOf('天') != -1){
        		return parseInt(value.replace('(天)','')) * 24 * 60 * 60;
        	}else if(value.indexOf('小时') != -1){
        		return parseInt(value.replace('(小时)','')) * 60 * 60;
        	}else if(value.indexOf('分钟') != -1){
        		return parseInt(value.replace('(分钟)','')) * 60;
        	}else{
        		return parseInt(value.replace('(秒)',''));
        	}
        }

}
