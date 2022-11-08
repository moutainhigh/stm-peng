(function($){
	function NetDevice(config){
		
		this.config = $.extend({}, this._defaults,  config);
		//console.log(this.config);
		var innerMainDivId = oc.util.generateId();
		this._mainDiv = $("#" + config.outerMainDivId).find(this.selector).attr('id', innerMainDivId);
//		this._mainDiv = $(this.selector).attr('id', oc.util.generateId());
		this._mainDiv.show().next().hide();
	}
	
	NetDevice.prototype = {
		constructor	: NetDevice,
		_defaults : {
			id:undefined
		},
		selector:"#oc-module-home-workbench-netdevice-main",
		_mainDiv:undefined,
		_CPUChartDiv:0,
		_RAMChartDiv:0,
		_imgDiv:undefined,
		_interfaceDetails:0,
		config : undefined,
		_ipsClick:0,
		_CPUChart:0,
		_RAMChart:0,
		_alarmStatus : 0,
		_imgActive:0,
		_snapshot:0,
		activeId:undefined,
		_CPUChartIns:undefined,
		_RAMChartIns:undefined,
		colors:{
			CRITICAL:'#CE0109',
			SERIOUS:'#FF7D02',
			WARN:'#F1C337',
			NORMAL:'#42b74c',
			UNKOWN:'#42b74c'//'#737373'
		},
		refresh:function(id, ext1){
			var target = this;
			target.config.type = "refresh";
			target.activeId = undefined;

			if(id ==undefined &&  ext1 == undefined){
				id = this.config.id;
				ext1 = this.config.ext1;
			}
			target._load(id, ext1);
		},
		_refresh:function(){
			var target = this;
			var id = target.config.id;
			var ext1 = target.config.ext1;
			target.config.type = "refresh";
			target.activeId = undefined;
			target._init();
			target._load(id,target._mainDiv,ext1);
		},
		_render : function(data){
			var d = $.extend({},data),
			target = this,
			btn = target._mainDiv.find("span.btn[code=2]").removeClass('visible');
			if(!data && data == null){
				target._mainDiv.hide().next().show();
				alert("此网络设备未监控或者已删除...");
				return ;
			}
			target._mainDiv.fadeIn().next().hide();
			$.each(target._initMethods, function(index, item){
				var fn = target._initMethods[index];
				fn&&fn(target,d[index]);
			});
			$.each(target._registerEvents,function(){
				this.call(target);
			});
			var interfaces = d.interfaces,
			
		
			index = interfaces.length>0 ? interfaces.indexOf(function(obj){return obj.id==target.activeId}) : -1;
			if(index!=-1){
				for(var i=0,len=Math.floor(index/6); i<len; i++){
					btn.trigger("click", "1");
				}
				target._imgDiv.find(".item:first div").eq(index).addClass("active");
			}else{
				target._imgDiv.find(".item:first div").first().trigger("click");
				if(interfaces[0]!=null && interfaces[0]!=undefined){
					target.activeId = interfaces[0].id;
				}
			//	target.activeId = interfaces[0].id;
			}
			
		},
		_load:function(id, div, ext1){
			var target = this;
			oc.util.ajax({
	    		url:oc.resource.getUrl('home/workbench/netdevice/getNewInitData.htm'),
	    		data:{id:id, ext1:ext1},
	    		timeout:300000,
	    		startProgress:false,
				stopProgress:false,
	    		failureMsg:'获取数据失败！',
	    		success:function(data){
	    			if(data.code==200){
	    				if(data.data == null){//查询的资源被删除或不存在
//	    					$("#"+target.config.outerMainDivId).remove();
	    					target._mainDiv.css({"text-align":"center","line-height":target._mainDiv.innerHeight() + "px"});
		    				target._mainDiv.html('<span  class="table-dataRemind">该资源已被删除或不存在</span>');
	    					return;
	    				}
    					target.homeDefaultInterfaceBo = data.data.homeDefaultInterfaceBo;
	    				div&&div.data("data", data.data);
	    				target._render(data.data);
	    				
	    			}
	    		},
	    		startProgress:0,
	    		stopProgress:0
	    	});
		},
		_init : function(){
			var target = this;
			var h1 = target._mainDiv.find('div.layout-first').height();
			var h2 = target._mainDiv.height() - h1-20;
			 target._mainDiv.find('div.sub-box').height(h2);
			 
			var chartDiv = target._mainDiv.find("div.charts").first(),
			height = chartDiv.parent().height()/2;
			
			target.contentDiv = target._mainDiv.find(".netdevice_content_div").first();
			target._CPUChartDiv = chartDiv.find(".CPUChart").first().data("height",height);
			target._RAMChartDiv = chartDiv.find(".RAMChart").first().data("height",height);
			target._interfaceDetails = target._mainDiv.find("div.interface_details").first();
			target._alarmStatus = oc.util.getDictObj('res_indicator_alarm_status');
			target._ipsClick = target._mainDiv.find(".ips").first();
			target._snapshot=target._mainDiv.find(".mainmachine_snapshot").first();
		},
		_initMethods : {
			base : function(target, data){
				//不可用设备显示快照提示
				if(data.status=="CRITICAL"){
					target._mainDiv.find(".h-info-m").addClass("mainmachine_m");
					target._snapshot.show();
				}else{
					target._mainDiv.find(".h-info-m").removeClass("mainmachine_m");
					target._snapshot.hide();
				}
				
				
				if(data){
					target._mainDiv.find("label.netdevice_base").each(function(){
						var item = $(this),
						field=item.attr("name"),
						value = data[field];
						if(field=='ips'){
							value.length>0&&item.text(value[0].name);
							!target.config.type&&target.ipsShow(target,value);
						} else if(field=='status'){
							if(value!='NOT_MONITORED' && value.indexOf('_')!=-1){
								value=value.split('_')[0];
							}
							var status = target._alarmStatus[value];
							var text = {red:'致命', orange:'严重', yellow:'警告',green:'正常',gray:'未知'};
								status&&item.text(text[status.name] ? text[status.name] : '未监控');
								if(data.status=='NORMAL_UNKNOWN'){
									$("label[name='status']").text('正常');
								}else if(data.status=='NORMAL_CRITICAL'){
									$("label[name='status']").text('致命');
								}else if(data.status=='NORMAL_NOTHING'){
									$("label[name='status']").text('正常');
								}else if(data.status=='UNKNOWN_NOTHING'){
									$("label[name='status']").text('正常');
								}else if(data.status=='CRITICAL_NOTHING'){
									$("label[name='status']").text('致命');
								}
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
			cpu : function(target, data){
				var rate = data ? data.rate : 0;
				if(rate&&!isNaN(rate)){
					rate = parseInt(rate);
				}else{
					rate = 0;
				}
				/*target._CPUChart = oc.home.highchart.solidgauge({
					selector:target._CPUChartDiv,
					data:rate,
					height:target._CPUChartDiv.data('height'),
					width:target._CPUChartDiv.width(),
					color:target.colors[data.status],
					option:{
						title:{
							text:'CPU平均使用率',
							verticalAlign:"bottom",
							align:"center" 
						}
					}
				});*/
                var servers =  $.extend(true,{},target.config.properties.focusResource.gaugeOption["series"],true,true);
                var server = servers[0];
                server.name = 'CPU平均使用率';
                server.data[0].value = rate;
                server.data[0].name = 'CPU平均使用率';
                var option = {
                    series: [server]
                };
                var myChart = echarts.init(target._CPUChartDiv[0]);
                myChart.setOption(option);
			},
			ram : function(target, data){
				var rate = data ? data.rate : 0;
				if(rate&&!isNaN(rate)){
					rate = parseInt(rate);
				}else{
					rate = 0;
				}
				/*target._RAMChart = oc.home.highchart.solidgauge({
					selector:target._RAMChartDiv,
					data:rate,
					height:target._RAMChartDiv.data('height'),
					width:target._RAMChartDiv.width(),
					color:target.colors[data.status],
					option:{
						title:{
							text:'内存平均使用率',
							verticalAlign:"bottom",
							align:"center" 
						}
					}
				});*/
                var servers =  $.extend(true,{},target.config.properties.focusResource.gaugeOption["series"],true,true);
                var server = servers[0];
                server.name = '内存平均使用率';
                server.data[0].value = rate;
                server.data[0].name = '内存平均使用率';
                var option = {
                    series: [server]
                };
                var myChart = echarts.init(target._RAMChartDiv[0]);
                myChart.setOption(option);
			},
			interfaces : function(target, datas){
				var data = [];
				for(var j=0, len=datas.length; j<len; j++){
					if(datas[j].status!='NOT_MONITORED'){
						data.push(datas[j]);
					}
				}
				var width=data.length*60;
				var item = $("<div class='item oc-item' style='position:relative;width:" +width+ "px!important;'></div>");
				if(data.length==0){
					for(var i=0;i<6;i++){
						item.append($("<div class='sub-r-icobox border-right'></div>"));
					}
				}else{
					$.each(data, function(i){
						var alarms = target._alarmStatus[this.status]||{};
						// 管理状态操作状态问题
						if(this.status == 'ADMNORMAL_OPERCRITICAL'){
							alarms.name = 'green-red';
						}
						item.append($("<div class='sub-r-icobox border-right' title='"+this.name+"'><span class='interface-eq interface-"+alarms.name+"'></span></div>").data("data",this));
					});
				}
				
				target._imgDiv = target._mainDiv.find("div.interfaces .images").first().html('').append(item);
				var btns = target._mainDiv.find("span.btn");
				btns.filter("[code=1]").addClass('visible');
				((target._imgDiv.parent()[0].clientWidth - width)>=0)&&btns.addClass('visible');
			}
		},
		_registerEvents:{
			btns:function(){
				var target = this;
				var max = target._imgDiv[0].scrollWidth - target._imgDiv.parent()[0].clientWidth;
				//获取接口最大长度
				var maxIntWidth = target._imgDiv.find(".item.oc-item").width();
				//获取接口显示框长度
				var showIntDivWidth = target._imgDiv.parent().parent().width();
				//获取可点击次数
				var clicks = 0;
				clicks = maxIntWidth / showIntDivWidth;
				clicks = parseInt(clicks);
				//已点击次数
				var hasClicks = 0;
				target._mainDiv.find("span.btn").off("click").click(function(e,arg){
					var type = $(this).attr("code");
					if(type == 2){
						if (hasClicks == clicks) {
							return;
						} else{
							++hasClicks;
						}
					}else if(type == 1){
						if (hasClicks == 0) {
							return;
						} else{
							--hasClicks;
						}		
					}
					if(hasClicks == 0){
						$("span[code='1']").addClass("visible");
					}else if(hasClicks == 2){
						$("span[code='2']").css("cursor","inherit");
					}else{
						$("span[code='2']").css("cursor","pointer");
						$("span[code='1']").removeClass("visible");
					}
					var img = target._imgDiv.find("div.item").first();
					//第一个接口图片距离左边边框的距离
					var left = img[0].offsetLeft;
					left = -(showIntDivWidth * hasClicks);
//					if(type==2){
//						$(this).addClass('visible').parent().next().next().find(".btn").removeClass('visible');
//					}else{
//						$(this).parent().next().next().find(".btn").removeClass('visible');
//					}
//					if(type==1){
//						$(this).addClass('visible').parent().prev().prev().find(".btn").removeClass('visible');
//					}else{
//						$(this).parent().prev().prev().find(".btn").removeClass('visible');
//					}
					if(arg){
						img.css({left : left+'px'});
					}else{
						img.animate({left : left+'px'});
					}
				});
			},
			imgs:function(){
				var target = this;
				target._imgDiv.find(".item:first div").off("click").click(function(){
					var html = "<table>",
					data = $(this).data("data");
					if(!data){
						target._interfaceDetails.html('');
						target._interfaceDetails.prev().find(".interface_field").first().text('');
						return ;
					}
					target._imgDiv.find(".item:first div").eq(0).removeClass("active");//将默认的第一个接口的active去掉
					if(target._imgActive){
						target._imgActive.removeClass("active");
					}
					target._imgActive = $(this);
					target._imgActive.addClass("active");
					target.activeId = data.id;
					target._interfaceDetails.prev().find(".interface_field").first().text(data.name);
					target._interfaceDetails.prev().find("#checkInterface").first().attr('InterId',data.id);
					if(data.isCheck==true){
						target._interfaceDetails.prev().find("#checkInterface").attr('checked',true);
					}else{
						target._interfaceDetails.prev().find("#checkInterface").removeAttr('checked');
					}
					var keyTd = '<td class="interface-td-r" style="width:25%;">key</td>',
					valueTd = '<td class="interface-td-l" style="width:25%;"><span class="interface_field">value</span></td>';
					oc.util.ajax({
			    		url:oc.resource.getUrl('home/workbench/netdevice/getInterfaceIndicators.htm'),
			    		data:{id:data.id},
			    		timeout:300000,
			    		failureMsg:'获取数据失败！',
						startProgress:false,
						stopProgress:false,
			    		success:function(result){
			    			if(result.code==200){
			    				var d = result.data||[];
			    				if(d.length == 0){
			    					target.AssemblyInterface(d);
			    				}
			    				if(d.length>0){
									target._interfaceDetails.html('');
									for(var i=0,len=d.length;i<len;i++){
										i%2==0&&(html += '<tr>');
										html += keyTd.replace('key', d[i].text) +
										valueTd.replace('value', d[i].value);
										i%2!=0&&(html += '</tr>');
									};
									d.length%2!=0&&(html += keyTd.replace('key', '') + valueTd.replace('value', '') +'</div>');
								}else{
									for(var i=0;i<3;i++){
										var emptyContent = keyTd.replace('key', '') + valueTd.replace('value', '');
										html+='<tr>'
											+ emptyContent + emptyContent;
											+ '</tr>';
									}
								}
			    				html += "</table>";
								target._interfaceDetails.html(html);
			    			}
			    		}
			    	});
					
				});
			},
			checkInterface:function(){
				var target = this;
				target._mainDiv.find("#checkInterface").off("click").click(function(){
					var interfaceid = -1;
//					var id=moduleDiv.attr("data-workbench-ext");
//					var workbenchId= moduleDiv.attr("data-workbench-id");
//					var sort= moduleDiv.attr("data-workbench-sort");

					if($("#checkInterface").prop("checked") == true){
						interfaceid=$("#checkInterface").attr("InterId");
					}
					target.homeDefaultInterfaceBo.defaultInterfaceId = interfaceid;
//					console.log("这里需要实现函数：" + interfaceid);

//					target._refresh();

					oc.util.ajax({
			    		url:oc.resource.getUrl('home/workbench/netdevice/setDefaultInterface.htm'),
			    		data:{
			    			id: target.homeDefaultInterfaceBo.id,
			    			resourceId: target.homeDefaultInterfaceBo.resourceId,
			    			defaultInterfaceId:target.homeDefaultInterfaceBo.defaultInterfaceId
			    		},
			    		failureMsg:'获取数据失败！',
			    		success:function(data){
			    			if(data.code==200){
			    				if(interfaceid == -1){
			    					alert("取消默认接口成功！");
			    				}else{
			    					alert("保存默认接口成功！");
			    				}
			    			}
			    		}
			    	});
				});
			},
		},
		//空白接口组装数据
		AssemblyInterface:function(data){
			data[0] = JSON.parse('{"id": "ifAlias", "value": "", "text": "接口别名"}');
			data[1] = JSON.parse('{"id": "ifAdminStatus", "value": "", "text": "管理状态"}');
			data[2] = JSON.parse('{"id": "ifDesc", "value": "", "text": "接口描述"}');
			data[3] = JSON.parse('{"id": "ifSpeed", "value": "", "text": "接口带宽"}');
			data[4] = JSON.parse('{"id": "ifOperStatus", "value": "", "text": "操作状态"}');
			data[5] = JSON.parse('{"id": "ifBroadPktsRate", "value": "", "text": "广播包率"}');
			data[6] = JSON.parse('{"id": "ifInBandWidthUtil", "value": "", "text": "接收带宽利用率"}');
			data[7] = JSON.parse('{"id": "ifDiscardsRate", "value": "", "text": "丢包率"}');
			data[8] = JSON.parse('{"id": "ifInOctetsSpeed", "value": "", "text": "接收速率"}');
			data[9] = JSON.parse('{"id": "ifMultiPktsRate", "value": "", "text": "组播包率"}');
			data[10] = JSON.parse('{"id": "ifOctetsSpeed", "value": "", "text": "接口总流量"}');
			data[11] = JSON.parse('{"id": "ifOutOctetsSpeed", "value": "", "text": "发送速率"}');
			data[12] = JSON.parse('{"id": "ifOutBandWidthUtil", "value": "", "text": "发送带宽利用率"}');
			data[13] = JSON.parse('{"id": "ifBandWidthUtil", "value": "", "text": "带宽利用率"}');
		},
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
	};
	

	oc.ns('oc.index.home.widget.focusresource');
	oc.index.home.widget.focusresource.nDevice = function(opt){
  			var nDevice = new NetDevice(opt);
			nDevice._init();
			//动态调整接口栏行高
			var lineHeight = $(opt.moduleDiv.selector).find(".interface-header").height();
			$(opt.moduleDiv.selector).find(".images-fpage").css("line-height",lineHeight+"px");
			nDevice._load(opt.id, undefined, opt.ext1);
    	return nDevice;
    }

})(jQuery);