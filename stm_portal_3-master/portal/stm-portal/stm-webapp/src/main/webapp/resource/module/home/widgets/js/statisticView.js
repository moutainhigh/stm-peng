function statisticView(selector,opt){
	this.opt = {
		type:'warming',
		pieType:'v2',//控制告警的饼图的显示方式
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);
	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}
var sp = statisticView.prototype;

sp.init = function() {
	this.opt.selector.html("");
	if(this.opt.type == 'warming'){
		this.plotwarming();
	}else if(this.opt.type == 'asset'){
		this.plotAsset();
	}else{
		throw('当前类型的图表不存在!');
	}
};

sp.refresh = function(){
	this.init();
}

sp.plotwarming = function(){
	var _this = this;
	var opt =this.opt;
	var end = +new Date;
	var start = +new Date;
	switch(opt.time){
		case '24h': {
			start = start - 24*60*60*1000;
			break;
		}
		case '1w' : {
			start = start - 7*24*60*60*1000;
			break;
		}
		case'1m' : {
			start = start - 30*24*60*60*1000;
			break;
		}
		case'all' : {
			start = 0;
			break;
		}
	}
	oc.util.ajax({
        url: oc.resource.getUrl('system/home/getDetailAlarmCount.htm'),
        params:{
            resources:opt.resources,
            groupId:0,
            domainId:opt.domains,
            start:start,
            end:end,
            dataType:'json'
        },
        stopProgress:function(){},
        startProgress:function(){},
        success: function (data) {
            if(data.code == '200'){
            	_this._plotwarming(data.data);
            }
        }
    });
}

sp._plotwarming = function(data){
	var properties = $.extend(true,{},this.opt.properties,true);
	var option = properties.statisticView.warmingOption;
	var selectedResource = this.opt.selectedResource;
	var total = data.total;
	var t =[];
	for(var i=0;i<total.length;i++){
		var o=total[i];
		o.itemStyle = {
				normal:{
					color:''
				}
			}
		if(o.name == 'critical'){
			o.name = o.value;
			o.rawName = '致命';
			//o.selected = true;
			o.itemStyle.normal.color = option.color[5];
		}else if(o.name == 'serious'){
			o.name = o.value;
			o.rawName = '严重';
			o.itemStyle.normal.color = option.color[2];
		}else if(o.name == 'warn'){
			o.name = o.value;
			o.rawName = '警告';
			o.itemStyle.normal.color = option.color[1];
			o.label = {
					normal:{
						textStyle:{
							color:'black'
						}
					}
				};
		}
		t.push(o);
	}

	if(t.length<1){
		this.opt.selector.html('<span class="no-warn">告警数为零！<span>');
		var mt=(parseInt($('.no-warn').parent().height())-34)/2;
		$('.no-warn').css({'margin-top':mt+'px','margin-left':'25%'});
		return;
	}
	
	var dt = data.detail;
	var detail=[];
	for(var i=0;i<dt.length;i++){
		var objs = dt[i];
		if(dt[i].length<1)
			continue;
		var other = {
			rawName:'其它',
			value:0,
			level: t[i].rawName,
		};
		for(var j=0; j<objs.length; j++){
			var tp = objs[j];
			tp.rawName = selectedResource[tp.name].name;
			
			if(tp.value>0 && (tp.name == 'Host' || tp.name == 'NetworkDevice')){
				tp.itemStyle = {
						normal:{
							color : ""
						}
				}
				if(t[i].rawName == '致命'){
					if(tp.name == 'Host'){
						tp.itemStyle.normal.color = option.color[5];
					}else if(tp.name == 'NetworkDevice'){
						tp.itemStyle.normal.color = option.color[0];
					}else{
						tp.itemStyle.normal.color = option.color[3];
					}
				}else if(t[i].rawName == '严重'){
					if(tp.name == 'Host'){
						tp.itemStyle.normal.color = option.color[2];
					}else if(tp.name == 'NetworkDevice'){
						tp.itemStyle.normal.color = option.color[7];
					}else{
						tp.itemStyle.normal.color = option.color[8];
					}
				}else if(t[i].rawName == '警告'){
					if(tp.name == 'Host'){
						tp.itemStyle.normal.color = option.color[1];
					}else if(tp.name == 'NetworkDevice'){
						tp.itemStyle.normal.color = option.color[4];
					}else{
						tp.itemStyle.normal.color = option.color[6];
					}
				}else{
					;
				}
				tp.name = tp.rawName + '(' + tp.value +')';
				tp.level = t[i].rawName;
				detail.push(tp);
			}else{
				other.value += tp.value;
				var color = "";
				if(other.level == '致命'){
					color = option.color[3];
				}else if(other.level == '严重'){
					color = option.color[8];
				}else{
					color = option.color[6];
				}
				other.itemStyle = {
						normal : {
							color : color
						}
				}
			}
		}
		other.name = other.rawName + '(' + other.value +')';
		detail.push(other);
	}

	option.series[0].data = t;
	
	//值为0的数据线不现实名称和引线
	for(var i = 0; i < detail.length; ++i){
		if(detail[i].value == 0){
			detail[i].label = {
                normal:{
                    show:false
                }
            };
			detail[i].labelLine = {
                 normal:{
                     show:false
                 }
             };
		}
	}
	
	option.series[1].data = detail;
	option.tooltip.formatter=function(p){
    	var val = '';
    	if(p.seriesIndex >0){
    		val +='资源类型：'+ p.data.rawName +'<br/>';
    		val += '告警级别：'+ p.data.level +'<br/>';
    	}else{
    		val +='告警级别：'+ p.data.rawName +'<br/>';
    	}
		val +=  '告警数：' + p.value  +'条<br/>'; 
		val += '占比：' + p.percent + '%';
		return val;
    }
	
	var myChart = echarts.getInstanceByDom(this.opt.selector[0]);
	echarts.dispose(myChart);
	
	myChart = echarts.init(this.opt.selector[0]);
	myChart.setOption(option);
	option = null;
}

sp.plotAsset = function(){
	var _this = this;
	var opt =this.opt;

	oc.util.ajax({
        url: oc.resource.getUrl('home/workbench/resource/getByTypeIds.htm'),
        params:{
            typeIds:opt.resources,
            domainId:opt.domains,
            dataType:'json'
        },
        stopProgress:function(){},
        startProgress:function(){},
        success: function (data) {
            if(data.code == '200'){
            	_this._plotTotalAsset(data.data);
            	data = null;
            }
        }
    });
}

sp._plotTotalAsset = function(data){
	var _this = this;
	var properties = $.extend(true,{},this.opt.properties,true);
	var option = properties.statisticView.assetOption;
	var sdr = this.opt.selectedResource;

	var ser =option.series[0];
	
	ser.name = '全部资源';
	var dat = [];
	$.each(data,function(k,obj){
		var itm = obj.data;
		var count = 0;
		for(var i=0;i<itm.length;i++){
			count += itm[i];
		}
		if(count>0)
			dat.push({name:sdr[k].name +'(' + count + ')',
				rawName:sdr[k].name, 
				dat:obj,
				value:count});
	});
	if(dat.length<1){
		this.opt.selector.html('<span class="no-asset">资产数为零！<span>');
		var mt=(parseInt($('.no-asset').parent().height())-34)/2;
		$('.no-asset').css({'margin-top':mt+'px','margin-left':'25%'});
		return;
	}
	ser.data = dat.sort(function (a, b) { return a.value - b.value;});
	var series = [];
	series.push(ser);
	option.series = series;
	option.legend.data = ['全部资源'];
	option.legend.show = false;
	option.tooltip.formatter = function(p){
		var o = '';
		o += '资源类型：'+ p.data.rawName + '<br/>';
		o += '数量：'+ p.value + '<br/>';
		o += '占比：'+ p.percent + '%<br/>';	
		return o
	};
	
	//console.log(option);
	
	var myChart = echarts.getInstanceByDom(this.opt.selector[0]);
	echarts.dispose(myChart);

	myChart = echarts.init(this.opt.selector[0]);
	myChart.setOption(option);
	option = null;

	myChart.on('click',function(pm){
		//console.log(pm);
		_this._plotAssetDetail(pm.data);
	});
}

sp._plotAssetDetail = function(data){
	var _this = this;
	var properties = $.extend(true,{},this.opt.properties,true);
	var option = properties.statisticView.assetOption;//$.extend({},,true);;
	var sdr = this.opt.selectedResource;

	var ser = option.series[0];
	
	ser.name = data.rawName;
	var dat = [];
	
	var ctgs = data.dat.categories;
		
	for(var i=0;i<ctgs.length;i++){
		var name = ctgs[i];
		var count = data.dat.data[i];
		if(count>0)
			dat.push({name:name+'(' + count + ')',
				rawName:name,
				value:count});
	}
	
	ser.data = dat;
	var series = [];
	series.push(ser);
	option.series = series;
	option.legend.data = [data.rawName];
	//option.legend.show = true;
	option.tooltip.formatter = function(p){
		var o = '';
		o += '资源类型：'+ p.data.rawName + '<br/>';
		o += '数量：'+ p.value + '<br/>';
		o += '占比：'+ p.percent + '%<br/>';
		
		return o
	};
	option.toolbox={  
        show : true,  
        feature : {
            myTool : {  
            	show : true,  
            	title : '返回',
            	icon : 'path://M7,4V0L0,6l7,5V7c6.369,0,9,9,9,9C16,3.664,7,4,7,4z',
            	iconStyle: {
                    normal: {
                        color:'white'
                    }  
                },
                onclick : function (){  
                   _this.refresh();
                }
            }
        },
        x:'left',
        y:'bottom'
    };
	//console.log(option);
	var myChart = echarts.getInstanceByDom(this.opt.selector[0]);
	echarts.dispose(myChart);

	myChart = echarts.init(this.opt.selector[0]);
	myChart.setOption(option);

	myChart.on('click',function(pm){
		if(pm.name == '返回')
			_this.refresh();
	});
}

//@Deprecated
sp._plotAsset = function(data){
	var properties = $.extend(true,{},this.opt.properties,true);
	var option = properties.statisticView.assetOption;
	
	var serie = option.series[0];

	var sdr = this.opt.selectedResource;
	
	var cat = [];
	var series = [];
	$.each(data,function(k,obj){
		var ser = $.extend({},serie,true);
		ser.name = sdr[k].name;

		var ctg = obj.categories;
		var res = obj.data;
		var dat = [];
		var tc = 0;
		for(var i=0; i<ctg.length; i++){
			var cont = res[i];
			if(cont>0){
				dat.push({name:ctg[i],value:cont});
			}
			tc += cont;
		}
		if(tc >0){//只显示数量大于0的资源
			cat.push(sdr[k].name);
			ser.data = dat;
			series.push(ser);
		}
	});
	
	option.legend.data = cat;
	option.series = series;
	
	var myChart = echarts.getInstanceByDom(this.opt.selector[0]);
	echarts.dispose(myChart);

	myChart = echarts.init(this.opt.selector[0]);
	myChart.setOption(option);
	option = null;
}
