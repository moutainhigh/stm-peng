(function($){
	function Host(outerMainDivId){
		this.config = $.extend({}, this._defaults);
		var innerMainDivId = oc.util.generateId();
		this._mainDiv = $("#" + outerMainDivId).find(this.selector).attr('id', innerMainDivId);
//		this._mainDiv = $(this.selector).attr('id', innerMainDivId);
		this._mainDiv.show().next().hide();
	}
	
	Host.prototype = {
		constructor	: Host,
		_defaults : {
			id:undefined
		},
		selector:"#oc-module-home-workbench-mainmachine-main",
		_panelDiv:0,
		_mainDiv:0,
		_CPUChartDiv:0,
		_RAMChartDiv:0,
		_baseInfo:0,
		_ipsClick:0,
		_CPUChart:0,
		_RAMChart:0,
		_alarmStatus : 0,
		_interfaceNoData:0,
		_partionsNoData:0,
		contentDiv:0,
		_snapshot:0,
		config : undefined,
		data:{},
		colors:{
			CRITICAL:'#CE0109',
			SERIOUS:'#FF7D02',
			WARN:'#F1C337',
			NORMAL:'#42b74c',
//			UNKOWN:'#737373'
			UNKOWN:'#42b74c'
		},
		status:{
			NOT_MONITORED: "未监控",
			CRITICAL: "致命",
			SERIOUS: "严重",
			WARN: "警告",
			UNKOWN: "未知",
			NORMAL: "正常"
		},
		refresh:function(id){
			var target = this;
			target.config.type = "refresh";
			target._load(id);
		},
		_load:function(id){
			var target = this;
			oc.util.ajax({
	    		url:oc.resource.getUrl('home/workbench/host/getInitData.htm'),
	    		data:{id:id},
	    		failureMsg:'获取数据失败！',
	    		success:function(data){
	    			if(data.code==200){
	    				var ds =data.data;
	    				target.data = ds;
	    				target._render();
	    			}
	    		},
	    		startProgress:0,
	    		stopProgress:0
	    	});
		},
		_render:function(){
			var target = this,
			data = target.data;
			if(!data){
				target._mainDiv.hide().next().show();
				return ;
			}
			target._mainDiv.fadeIn().next().hide();
			for(var d in data){
				var fn = target._initMethods[d];
				fn&&fn(target,data[d]);
			}
		},
		_init : function(){
			var target = this;
			var chartDiv = target._mainDiv.find("div.charts").first(),
			height = chartDiv.parent().height()/2;
			
			target.contentDiv = target._mainDiv.find(".host_content_div").first();
			target._CPUChartDiv = chartDiv.find(".CPUChart").first().data("height",height);
			target._RAMChartDiv = chartDiv.find(".RAMChart").first().data("height",height);
			target._baseInfo=target._mainDiv.find("div.base-info .mainmachine_base");
			target._ipsClick = target._mainDiv.find(".ips").first();
			target._alarmStatus = oc.util.getDictObj('res_indicator_alarm_status');
			target._interfaceNoData = target._mainDiv.find("div.interface_no_data").first();
			target._partionsNoData = target._mainDiv.find("div.partitions_no_data").first();
			target._snapshot=target._mainDiv.find(".mainmachine_snapshot").first();
			
		},
		_initMethods : {
			base : function(target,data){
				
				//不可用设备显示快照提示
				if(data.status=="CRITICAL"){
					target._mainDiv.find(".h-info-m.base-info").addClass("mainmachine_m");
					target._snapshot.show();
				}else{
					target._mainDiv.find(".h-info-m.base-info").removeClass("mainmachine_m");
					target._snapshot.hide();
				}
				
				if(data){
					target._baseInfo.each(function(){
						var item = $(this),
							field=item.attr("name"),
							value = data[field];
						if(field=='ips'){
							value.length>0&&item.attr("title",value[0].name).text(value[0].name);
							!target.config.type&&target._registerEvents.ipsShow(target,value);
						} else if(field=='status'){
							if(value!='NOT_MONITORED' && value.indexOf('_')!=-1){
								value=value.split('_')[0];
							}
							var status = target._alarmStatus[value];
							var text = {red:'致命', orange:'严重', yellow:'警告',green:'正常'//,gray:'未知'
								};
							status&&item.text(text[status.name] ? text[status.name] : '未监控');
						}else{
							item.attr("title",value).text(value);
						}
					});
					if(data.status == 'NOT_MONITORED'){
						target.contentDiv.hide().next().show();
					}else{
						target.contentDiv.show().next().hide();
					}
				}
			},
			cpu : function(target,data){
				var rate = data ? data.rate : 0;
				if(rate&&!isNaN(rate)){
					rate = parseInt(rate);
				}else{
					rate = 0;
				}
				target._CPUChart = oc.home.highchart.solidgauge({
					selector:target._CPUChartDiv,
					data:rate,
					height:target._CPUChartDiv.data('height'),
					width:target._CPUChartDiv.width(),
					color:target.colors[data.status],
					option:{
						title:{
							text:'CPU平均使用率'
						}
					}
				});
			},
			ram : function(target,data){
				var rate = data ? data.rate : 0;
				if(rate&&!isNaN(rate)){
					rate = parseInt(rate);
				}else{
					rate = 0;
				}
				target._RAMChart = oc.home.highchart.solidgauge({
					selector:target._RAMChartDiv,
					data:rate,
					height:target._RAMChartDiv.data('height'),
					width:target._RAMChartDiv.width(),
					color:target.colors[data.status],
					option:{
						title:{
							text:'内存平均使用率'
						}
					}
				});
			},
			partitions : function(target,data){
				if(data.length==0){
					target._partionsNoData.show();
					target._partionsNoData.next().hide();
					return ;
				}else{
					target._partionsNoData.hide();
					target._partionsNoData.next().show();
				}
				var html = "";
				$.each(data, function(){
					var rate = (!this.rate||this.rate=='UNKOWN') ? 0 : this.rate,
							status =target._alarmStatus[this.status]||{};
					html += '<div class="info-line"><span class="color-'+status.name+' locate-left width100 margin-bf5" title = "'+this.letter+'">分区'+this.letter+'利用率：</span>'+
					'<div class="rate rate-t"><span class="rate-'+status.name+'" style="width:'+rate+'%"></span>&nbsp;<span class="gt color-'+status.name+'">'+rate+'%</span></div></div>';
				});
				target._mainDiv.find("div.partitions").first().html(html);
			},
			interfaces : function(target,data){
				if(data.length==0){
					target._interfaceNoData.show();
					target._interfaceNoData.next().hide();
					return ;
				}else{
					target._interfaceNoData.hide();
					target._interfaceNoData.next().show();
				}
				var html = "";
				$.each(data, function(){
					var status = target.status[this.status]||"",
					alarms = target._alarmStatus[this.status]||{};
					html += "<div class='interfaces-h-bg'><div class='border-right'><span class='interface-eq interface-"+alarms.name+"'></span></div><div  class='interface-right-con'>" +
							"<div class='oc-panel-host' title='"+this.name+"'>"+this.name+"</div><div class='color-"+alarms.name+"'>接口"+status+"</div></div></div>";
				});
				target._mainDiv.find("div.interfaces").first().html(html);
			}
		},
		_registerEvents:{
			ipsShow:function(target,ips){
				var id = oc.util.generateId();
				target._ipsClick.click(function(e){
					e.stopPropagation();
					var box = $("body div.#"+id);
					if(box.size()==0){	//让box只加载一次
						box = $("<div id='"+id+"' style='position: absolute;' class='oc_float_div oc-title-ipbox'></div>");
						for(var i=0,len=ips.length;i<len;i++){
							box.append("<div>"+ips[i].name+"</div>");
						}
						box.appendTo('body');
					}
					oc.util.showFloat(box,e);
				});
			}
		}
	};
	var host =undefined;
	function open(config){
		if(config.id){
			if(!host){
				host = new Host(config.outerMainDivId);
				host._init();
			}
			host._load(config.id);
			return host;
		}
	}
	
	(function(){
		var outerMainDivId = oc.util.generateId();
		var mainDiv = $('#oc-module-home-workbench-mainmachine').attr("id", outerMainDivId);
		var moduleDiv = mainDiv.parent("div"), host=0, icoOk=0, icoRefresh=0;
		icoOk = mainDiv.find('span.r-set,div.no_info').click(function(){
			var target = this;
			oc.home.workbench.resource.select.open({
				type:'Host',//默认展示所有资源
				title:'资源选择',
				value:moduleDiv.attr("data-workbench-ext"),
				confirmFn:function(value,obj){
					$(target).data("value",value);
					
					host ? host.refresh(value) : (host = open({id:value,outerMainDivId:outerMainDivId}));
					
					oc.home.workbench.setExt(moduleDiv.attr("data-workbench-id"), moduleDiv.attr("data-workbench-sort"), value);
					moduleDiv.attr("data-workbench-ext", value);
				}
			});
		});
		
		icoRefresh = mainDiv.find('span.ico-refrash').first().click(function(e, args){
//			if(host){
//				var value = icoOk.data("value");
//				value ? host.refresh(value) : (args&&alert("请先选择主机"));
//			}
			var methods = oc.home.workbench.userWorkbenches[moduleDiv.attr("data-workbench-sort")];
			methods.reLoad();
			methods.render();
		});
		oc.home.workbench.userWorkbenches[moduleDiv.attr("data-workbench-sort")] = {
				reLoad:function(){
					var id = moduleDiv.attr("data-workbench-ext");
					if(id){
						if(id){
							if(!host){
								host = new Host(outerMainDivId);
								host._init();
							}
						}
						icoOk.data("value",id);
						host._load(id);
					}
				},
				render:function(){
					if(!host){
						log("初始化顺序有误");
						return ;
					}
					host._render();
				}
		}
		
	})();
})(jQuery);