(function($){
	function NetDevice(config){
		this.config = $.extend({}, this._defaults,  config);
		this._mainDiv = $(this.selector).attr('id', oc.util.generateId());
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
			target._load(id, ext1);
		},
		_render : function(data){
			var d = $.extend({},data),
			target = this,
			btn = target._mainDiv.find("span.btn[code=2]").removeClass('visible');
			if(!data && data!=undefined){
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
	    		url:oc.resource.getUrl('home/workbench/netdevice/getInitData.htm'),
	    		data:{id:id, ext1:ext1},
	    		timeout:300000,
	    		startProgress:false,
				stopProgress:false,
	    		failureMsg:'获取数据失败！',
	    		success:function(data){
	    			if(data.code==200){
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
			ram : function(target, data){
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
				target._mainDiv.find("span.btn").off("click").click(function(e,arg){
					var type = $(this).attr("code");
					var img = target._imgDiv.find("div.item").first();
					var left = img[0].offsetLeft;
					var mod = left%360;
					mod!=0&&(left -= mod);
					if((type==2&&Math.abs(left)>=max)||(type==1&&left>=0)){
						return ;
					}
					left = left + (type==2 ? -360 : 360);
					if(type==2&&Math.abs(left)>=max){
						$(this).addClass('visible').parent().next().next().find(".btn").removeClass('visible');
					}else{
						$(this).parent().next().next().find(".btn").removeClass('visible');
					}
					if(type==1&&left>=0){
						$(this).addClass('visible').parent().prev().prev().find(".btn").removeClass('visible');
					}else{
						$(this).parent().prev().prev().find(".btn").removeClass('visible');
					}
//					if((type==2&&Math.abs(left)>=max)||(type==1&&left>=0)){
//						$(this).addClass('visible');
//					}else{
//						target._mainDiv.find("span.btn").removeClass('visible');
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
			}
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
	var netDevice = undefined;
	
	function open(config){
		if(netDevice){
			netDevice._load(config.id, undefined, config.ext1);
			return netDevice;
		}
		if(config.id){
			netDevice = new NetDevice(config.outerMainDivId);
			netDevice._init();
			netDevice._load(config.id, undefined, config.ext1);
			return netDevice;
		}
	}
	
	(function(){
		var outerMainDivId = oc.util.generateId();
//		var mainDiv = $('#oc-module-home-workbench-netdevice').attr("id",oc.util.generateId()),
		var mainDiv = $('#oc-module-home-workbench-netdevice').attr("id", outerMainDivId);
		var moduleDiv = mainDiv.parent("div"),
		net=0,
		icoOk=0,
		icoRefresh=0;
		
		icoOk = mainDiv.find('span.r-set').first().click(function(){
			var target = this;
			oc.home.workbench.resource.select.open({
				type:'NetworkDevice',//默认展示所有资源
				title:'资源设置',
				value:moduleDiv.attr("data-workbench-ext"),
				ext1Value:moduleDiv.attr("data-workbench-ext1"),
				confirmFn:function(value, obj){
					$(target).data("value", value);
					var ext1 = obj.selfExt1;
					net ? net.refresh(value, ext1) : (net = open({id:value,outerMainDivId:outerMainDivId}));
					oc.home.workbench.setExt(moduleDiv.attr("data-workbench-id"), moduleDiv.attr("data-workbench-sort"), value, undefined, undefined, obj);
					moduleDiv.attr("data-workbench-ext", value);
					moduleDiv.attr("data-workbench-ext1", ext1);
					/***自动刷新一次***/
					var methods = oc.home.workbench.userWorkbenches[moduleDiv.attr("data-workbench-sort")];
					methods.reLoad();
				}
			});
		});
		mainDiv.find("#checkInterface").click(function(){
			var interfaceid=0;
			var id=moduleDiv.attr("data-workbench-ext");
			var workbenchId= moduleDiv.attr("data-workbench-id"),sort= moduleDiv.attr("data-workbench-sort");
			if($("#checkInterface").attr("checked")!=undefined){
				interfaceid=$("#checkInterface").attr("InterId");
			}
				oc.util.ajax({
		    		url:oc.resource.getUrl('home/workbench/main/setUserWorkBenchByDefaultId.htm'),
		    		data:{workbenchId:workbenchId,interfaceid:interfaceid,sort:sort},
		    		failureMsg:'获取数据失败！',
		    		success:function(data){
		    			if(data.code==200){
		    				if(interfaceid==0){
		    					alert("取消默认接口成功！");
		    				}else{
		    					alert("保存默认接口成功！");
		    				}
		    				
			    			/***自动刷新一次***/
							var methods = oc.home.workbench.userWorkbenches[moduleDiv.attr("data-workbench-sort")];
							methods.reLoad();
		    			}
		    		
		    		}
		    	});
		});
	
		mainDiv.find(".netdevice_no_data").click(function(){
			icoOk.trigger("click");
		});
		//checkInterface
		icoRefresh = mainDiv.find('span.ico-refrash:first,div.no_info').first().click(function(e, args){
			var methods = oc.home.workbench.userWorkbenches[moduleDiv.attr("data-workbench-sort")];
			methods.reLoad();
			methods.render();
//			debugger
//			if(net){
//				var value = icoOk.data("value");
//				value ? net.refresh(value) : (args&&alert("请先选择网络设备"));
//			}
		});
		
		oc.home.workbench.userWorkbenches[moduleDiv.attr("data-workbench-sort")] = {
				reLoad:function(){
					var id = moduleDiv.attr("data-workbench-ext"), ext1 = moduleDiv.attr("data-workbench-ext1");
					if(id){
						if(!netDevice){
							netDevice = new NetDevice(outerMainDivId);
							netDevice._init();
						}
						icoOk.data("value",id);
						netDevice._load(id, moduleDiv, ext1);
					}
				},
				render:function(){
					if(!netDevice){
						log("初始化顺序有误");
						return ;
					}
					netDevice._render(moduleDiv.data("data"));
				}
		};
	})();
})(jQuery);