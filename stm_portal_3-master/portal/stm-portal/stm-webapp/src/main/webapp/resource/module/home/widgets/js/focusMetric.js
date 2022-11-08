function focusMetric(selector,opt){
	this.opt = {
		chartType:'line',
		chartOpt:{
			timeStep:'hour'//x轴的时间间隙划分，可以是按分钟、小时、天
		}
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);
	
	this.end = +new Date;//折线图的开始时间
	this.start = +new Date;//折线图的结束时间


	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}
var fmp = focusMetric.prototype;

fmp.init = function(){
	this.opt.selector.html("");
	this.dataObj = $("<div/>");
	this.dataObj.css({width:'100%',height:'100%','overflow-y':'hidden'});
	this.opt.selector.html(this.dataObj);
	this._init();
}

fmp._init = function(){
	if(this.opt.chartType == 'line'
		|| this.opt.chartType == 'line-s'
		|| this.opt.chartType == 'line-m'){
		
		this.plotLineChart();
	}else if(this.opt.chartType == 'area'){
		//this.plotAreaLineChart();
		this.plotLineChart();

	}else if(this.opt.chartType == 'radar'){
		this.plotRadarChart();
	}else if(this.opt.chartType == 'radarv2'){
		this.plotRadarChartv2();
	}else if(this.opt.chartType == 'funnel'){
		this.plotFunnelChart();
	}else if(this.opt.chartType == 'thermometer'){
		this.plotThermometerChart();
	}else if(this.opt.chartType == 'gauge'){
		this.plotGaugeChart();
	}else if(this.opt.chartType == 'gaugev2'){
		this.plotGaugeChartV2();
	}else if(this.opt.chartType == 'metricCircle'){
		this.plotMetricCircleChart();
	}else if(this.opt.chartType == 'metricBlock'){
		this.plotMetricBlockChart();
	}

}

fmp.refresh = function(){
	var myChart = echarts.getInstanceByDom(this.dataObj[0]);
	echarts.dispose(myChart);
	this.dataObj.remove();
	this.init();
}

fmp._preProcessMetricData = function(){
	var data = this.opt.metrics;
	var metricList = {};
	var metrics = {};

	traveTree(data);
	return metricList;

	function traveTree(trees){
		for(var j=0; j<trees.length; j++){
			var tree = trees[j];
			if(!tree)
				continue;
			if(tree.children){
				if(tree.mainResource){
					getMeritcs(tree)
				}else{
					traveTree(tree.children);
				}
			}
		}
	}

	function getMeritcs(res){
		var mainId = res.id;
		metricList[mainId] = $.extend({},res,true);
		var tp = metricList[mainId];
		if(tp.name.match(/\(*\)/g)){
			var tpd = tp.name.split(')');
			tp.name = tpd[1];
			tp.ip = tpd[0].substring(1);
		}

		var ins = res.children;
		if(res.pid == 'business'){
			ins = [res];
		}
		for(var i=0; i<ins.length;i++){
			var inobj =ins[i];
			if(!inobj.isMetricId && inobj.isSubResource){
				inobj.parentId = mainId;
				getMeritcs(inobj);
				continue;
			}
			var mcl = inobj.children;
			var eobj = metricList[inobj.id];
			eobj = (eobj)?eobj:{};
			var metricObj = $.extend(eobj,inobj,true);
			metricList[inobj.id] = metricObj;
			if(!metrics[inobj.id])
				metrics[inobj.id] ={};
			for(var j=0;j<mcl.length;j++){
				var m = mcl[j];
				metricList[inobj.id][m.id] = m;
				metrics[inobj.id][m.id] = m;
				
				if(parseInt(inobj.id) != parseInt(mainId))
					metricList[inobj.id].parentId = mainId;
			}
		}
		metricList.metrics = metrics;
	}
}

//以时间为坐标轴折线图
fmp.plotLineChart = function(){
	var _this = this;
	_this.loadPerformanceMetricData(function(data,metricList){
		_this._plotLineChart(data,metricList);
	});
}

fmp.loadPerformanceMetricData = function(fn){
	var _this = this;

	var data = this.opt.metrics;
	var end = +new Date;
	var start = +new Date;
	switch(this.opt.time){
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
		case 'custom' : {
			//start = +new Date(this.opt.start);
			//end = +new Date(this.opt.end);
			start = getTime(this.opt.start + ":00");
			end = getTime(this.opt.end + ":00");
			break;
		}
		case'all' : {
			start = 0;
			break;
		}
	}

	var summaryType = 'hour';
	var metricList = {};
	var query = [];
	_this.start = start;
	_this.end = end;

	traveTree(data);

	function traveTree(trees){
		for(var j=0; j<trees.length; j++){
			var tree = trees[j];
			if(!tree)
				continue;
			if(tree.children){
				if(tree.mainResource){
					getResourceMeritcs(tree)
				}else{
					traveTree(tree.children);
				}
			}
		}
	}
	
	function getResourceMeritcs(res){
		var mainId = res.id;
		metricList[mainId] = $.extend({},res,true);
		var tp = metricList[mainId];
		delete metricList[mainId].children;
		if(tp.name.match(/\(*\)/g)){
			var tpd = tp.name.split(')');
			tp.name = tpd[1];
			tp.ip = tpd[0].substring(1);
		}
		

		var ins = res.children;
		if(res.pid == 'business'){
			ins = [res];
		}
		for(var i=0; i<ins.length;i++){
			var inobj =ins[i];
			if(!inobj.isMetricId && inobj.isSubResource){
				inobj.parentId = mainId;
				getResourceMeritcs(inobj);
				continue;
			}
			var mcl = inobj.children;
			var eobj = metricList[inobj.id];
			eobj = (eobj)?eobj:{};
			metricList[inobj.id] = $.extend(eobj,inobj,true);
			delete metricList[inobj.id].children;

			var ids = '';
			for(var j=0;j<mcl.length;j++){
				var m = mcl[j];
				ids += m.id + ',';
				metricList[inobj.id][m.id ] = m;
				metricList[inobj.id].parentId = mainId;
			}
			ids = ids.substring(0,ids.length-1);
			var q = {
					resourceType:res.pid,
		            metricId:ids,
		            instanceId:inobj.id,
		            summaryType:summaryType,
		            dateStart:start,
		            dateEnd:end
		        };
		    query.push(q);
		}
	}

	oc.util.ajax({
	    url: oc.resource.getUrl('home/workbench/metric/get.htm'),
        params:{
            query:query,
            dataType:'json'
        },
        stopProgress:function(){},
        startProgress:function(){},
        success: function (data) {
            if(data.code == '200'){
            	fn && fn(data.data,metricList);
            }
        }
    });

    function getTime(s){
    	if(!s)
    		return +new Date();
    	//var s = "2013-12-4 17:00:00"
		var ps = s.split(" ");
		var pd = ps[0].split("-");
		var pt = ps.length>1 ? ps[1].split(":"): [0,0,0];
		var d = +new Date(pd[0],pd[1]-1,pd[2],pt[0],pt[1],pt[2]);
		return d;
    }
}

fmp.loadMetricRealData = function(fn){
	var metricList = this._preProcessMetricData();
	var metrics = metricList.metrics;
	var query = {};
	
	for(iid in metrics){
		var obj  = metrics[iid];
		var item = '';
		var metricType = '';
		for(mid in obj){
			item += ',' + mid;
			if(metricType.indexOf(obj[mid].type)<0)
				metricType += ','+obj[mid].type;

		}
		item = item.substring(1);
		metricType = metricType.substring(1);
		var rtyp = metricList[iid].pid?metricList[iid].pid:metricList[iid].pId;
		query[iid] = {
						metric:item,
						resourceType:rtyp,
						metricType:metricType.split(',')
					};
	}
	oc.util.ajax({
	    url: oc.resource.getUrl('home/workbench/metric/getRealData.htm'),
        params:{
            query:query,
            dataType:'json'
        },
        stopProgress:function(){},
        startProgress:function(){},
        success: function (data) {
            if(data.code == '200'){
            	fn && fn(data.data,metricList);
            }
        }
    });
}

//以时间为坐标轴折线图
fmp._plotLineChart = function(data,metrics){
	var properties = $.extend(true,{},this.opt.properties,true);
	var option = $.extend(true,{},properties.focusMetric.plotLineChartOption);
    var serie = option.series[0];
    serie.data = data;
    
    var unit = '';
    var series = [];
    var ldat = [];
    var maxV = -Infinity,minV=Infinity;
    for(iid in data){
    	var mdata = data[iid];
	    for(var type in mdata){
	    	var dt = mdata[type];
	    	var se = $.extend(true,{},serie,true);
			var v = metrics[iid];
			var mm = v;//主资源信息
			while(mm.parentId && parseInt(mm.parentId) != mm.id){
				mm = metrics[mm.parentId];
			}
			var pf ='';
			if(v.id != mm.id){
				pf = mm.name;
			}
			unit = v[type].unit;
			if(!unit)
				unit = '';
	    	p=[];
	    	for(var i=0;i<dt.length;i++){
	    		var vl = getValue(dt[i]);
	    		var q = '--'
	    		if(vl != undefined && vl != ''){
	    			q = properties.UnitTransformUtil.transform(vl,unit);
	    		}else{
	    			vl = '';
	    		}

				p.push({name:getTime(dt[i]),
						value:[getTime(dt[i]),vl],
						instanceid : iid,
						formatValue:q,
					});
				maxV = maxV<parseFloat(vl)?parseFloat(vl):maxV;
				minV = minV>parseFloat(vl)?parseFloat(vl):minV;
			}
			
			se.name = pf + v.name + v[type].name;
			se.instanceid = iid;
			se.data = p;
			series.push(se);
			ldat.push(se.name);
	    }

	    function getValue(dt){
	    	return dt.data?dt.data:dt.value;
	    }
	    function getTime(dt){
	    	if(dt.collectTime)
	    		return dt.collectTime;
	    	if(dt.time)
	    		return dt.time;
	    }
	}

	if(this.opt.chartType == 'area') {//当前是面积视图
		if(series.length>0)
			series[0].areaStyle =  properties.focusMetric.areaStyle;
    }

    option.yAxis.max = maxV;
    if(maxV < 5){
        if(maxV <= 0){
            option.yAxis.splitNumber = 1;
        }else{
            option.yAxis.splitNumber = maxV;
        }
    }

    option.yAxis.axisLabel.formatter = function(value){
    	return properties.UnitTransformUtil.transform(value,unit);

    };


    option.series = series;
    option.legend.data = ldat;
    option.xAxis.max = this.end;
    option.xAxis.min = this.start;
    
	var myChart = echarts.init(this.dataObj[0]);
	myChart.setOption(option);
	option = null;
}

fmp.plotRadarChartv2 = function(){
	var _this = this;
	_this.loadMetricRealData(function(data,metricList){
		_this._plotRadarChartv2(data,metricList);
	});
}

fmp._plotRadarChartv2 = function(data,metricList){
	var _this = this;
	var properties = $.extend(true,{},this.opt.properties,true);
	var option =  $.extend(true,{},properties.focusMetric.plotRadarChartv2Option,true);;

	var objs= {};
	for(iid in data){
		var p = metricList[iid];
		while(p.parentId ){
			p = metricList[p.parentId];
		}
		var mdr=objs[p.id]
		if(!mdr){
			mdr ={
				name:p.name,
				ip:p.ip,
				children:{}
			}	
			objs[p.id] = mdr;
		}
		mdr.children[iid] = data[iid];
		mdr.children[iid]['info'] = metricList[iid];
	}
	var indicator = [];
	var value = []; 
	var units = [];
	for(key in objs){
		var instance = objs[key];
		for (subkey in instance.children){
			var metrics = instance.children[subkey];
			for(metricId in metrics){
				if(metricId == 'info')
					continue;
				var tmetric =metrics[metricId];
				var name = instance.name;
				if(key != subkey){
					name += metrics['info'].name;
				}
				name += tmetric.text;
				
				var v = tmetric.currentVal?tmetric.currentVal:0;
				indicator.push({name:name,max:_this._getMaxValueByUnit(tmetric.unit,v,tmetric.thresholds)});
				value.push(parseFloat(v));
				units.push(tmetric.unit);
			}
		}

	}
	var serie = option.series[0];
	serie.data[0].rawValue = value;

	var cutVlaue = [];
	for(var i =0; i<indicator.length; i++){
		cutVlaue.push(value[i]>indicator[i].max?indicator[i].max:value[i]);
	}
	serie.data[0].value = cutVlaue;
	serie.data[0].indicator = indicator;
	serie.data[0].valueUnit = units;

	option.radar.indicator =indicator;
	var myChart = echarts.init(this.dataObj[0]);
	myChart.setOption(option);
	option = null;
}

fmp._getMaxValueByUnit = function(unit,value,thresholds){
	var max = 10000;
	switch(unit){
		case '%':{
			return 100;
		}
		default : {
			if(thresholds.match(/\[.{1,},\d+,\d+\]/i)){
				var p = thresholds.match(/\d+/g);
				p = parseFloat(p[1]);
				return p * 1.5; //取阈值的最大值的1.5倍作为最大值。
			}
			max = value;
		}
	}
	return max;
}

//漏斗图
fmp.plotFunnelChart = function(){
	var _this = this;
	_this.loadMetricRealData(function(data,metricList){
		_this._plotFunnelChart(data,metricList);
	});
}
fmp._plotFunnelChart = function(data,metricList){
	var properties = $.extend(true,{},this.opt.properties,true);
	var option = properties.focusMetric.plotFunnelChartOption;
	var objs= {};
	for(iid in data){
		var p = metricList[iid];
		var subResource;
		while(p.parentId ){
			if(p.isSubResource)
				subResource = p;
			p = metricList[p.parentId];
			
		}
		var mdr=objs[p.id]
		if(!mdr){
			mdr ={
				name:p.name,
				ip:p.ip,
				children:{}
			}	
			objs[p.id] = mdr;
		}
		mdr.children[iid] = data[iid];
		mdr.children[iid]['info'] = metricList[iid];
		metricList[iid].subResource = subResource;
	}

	var legend = [];
	var serdata = []; 
	var units = [];
	for(key in objs){
		var instance = objs[key];
		for (subkey in instance.children){
			var metrics = instance.children[subkey];
			for(metricId in metrics){
				if(metricId == 'info')
					continue;
				var tmetric =metrics[metricId];
				var name = instance.name;
				var subMetricName = '';
				if(key != subkey){
					subMetricName = metrics['info'].name;
				}
				var v = tmetric.currentVal?tmetric.currentVal:0;
				v=parseFloat(v).toFixed(2);
				var sub = '';
				if(metrics['info'].subResource)
					sub = '(' + metrics['info'].subResource.name + ':' + subMetricName + ')'; 

				name = tmetric.text + ':' + v + tmetric.unit + '\n' + name + sub;

				legend.push(name);
				serdata.push({name:name,value:v,unit:tmetric.unit});
			}
		}
	}
	option.legend.data = legend;
	option.series[0].data = serdata;

	var myChart = echarts.init(this.dataObj[0]);
	myChart.setOption(option);
	option = null;
}

//温度表
fmp.plotThermometerChart = function(){

	var _this = this;
	_this.loadMetricRealData(function(data,metricList){
		_this._plotThermometerChart(data,metricList);
	});
}

fmp._plotThermometerChart = function(data,metricList){
	/*//test code,should be removed!
	data = {"1567":{"temperatureValue":{"id":"temperatureValue","isAlarm":false,"unit":"℃","text":"温度当前值","isTable":false,"status":"NORMAL","currentVal":"32","isCustomMetric":false,"lastCollTime":"2017-06-16 18:03:42","type":"InformationMetric"}},
			"1561":{"temperatureValue":{"id":"temperatureValue","isAlarm":false,"unit":"℃","text":"温度当前值","isTable":false,"status":"NORMAL","currentVal":"59","isCustomMetric":false,"lastCollTime":"2017-06-16 18:03:42","type":"InformationMetric"}},
			"1562":{"temperatureValue":{"id":"temperatureValue","isAlarm":false,"unit":"℃","text":"温度当前值","isTable":false,"status":"WARN","currentVal":"72","isCustomMetric":false,"lastCollTime":"2017-06-16 18:03:42","type":"InformationMetric"}},
			"1563":{"temperatureValue":{"id":"temperatureValue","isAlarm":false,"unit":"℃","text":"温度当前值","isTable":false,"status":"SERIOUS","currentVal":"82","isCustomMetric":false,"lastCollTime":"2017-06-16 18:03:42","type":"InformationMetric"}},
			"1564":{"temperatureValue":{"id":"temperatureValue","isAlarm":false,"unit":"℃","text":"温度当前值","isTable":false,"status":"CRITICAL","currentVal":"100","isCustomMetric":false,"lastCollTime":"2017-06-16 18:03:42","type":"InformationMetric"}}
		};//*/
	var _this = this;
	_this.dataObj.find('.thermo-area').remove();
	var count = 0;
	for(var id in data){
		var obj = _this.dataObj.append('<div class="thermo-area"><div class="thero"></div><div class="thero-name"></div></div>');
		count++;
	}
	
	_this.dataObj.find('.thermo-area').css({width:(100/count)+'%',height:'100%',display:'inline-block'});
	
	_this.dataObj.find('.thero').css({width:'100%',display:'block'});
	//_this.dataObj.find('.thero-name').css({width:'100%',display:'block','text-align':'center'});

	count = 0;
	if(_this._plotThermometerChartDivIns != null && _this._plotThermometerChartDivIns.length > 0){
		for(var i = 0; i < _this._plotThermometerChartDivIns.length; ++i){
			var myChartIns = echarts.getInstanceByDom(_this._plotThermometerChartDivIns[i]);
			echarts.dispose(myChartIns);
		}
		_this._plotThermometerChartDivIns = null;
	}
	_this._plotThermometerChartDivIns = [];
	for(var id in data){
		var dat = data[id];
		var $obj = _this.dataObj.find('.thermo-area:eq(' + count +')');
		var dta = dat.temperatureValue;
		dta.metricName = getMetricName(id,metricList);
		dta.title = getTtile(id,metricList);

		_this._drawThermometer(dta,$obj);		
		count++;
	}
	

	function getTtile(id,metricList){
		var p = metricList[id]
		var name = p.name;
		while(p.parentId){
			p = metricList[p.parentId];
		}
		return  {name:name+'温度值',subname:p.name};
	}

	function getMetricName(id,metricList){
		var p = metricList[id]
		var name = p.name;
		while(p.parentId){
			p = metricList[p.parentId];
		}

		return   '<p>' + name + '</p><p>' + p.name + '</p>';
	}
	
}

fmp._drawThermometer = function(data,that){
	var _this = this;
	var properties = this.opt.properties
	var plotThermometerChart = $.extend(true,{},properties.focusMetric.plotThermometerChart,true);
	var colorLevel = plotThermometerChart.colorLevel;

	/*
	//注释掉老版本的温度计插件
	oc.resource.loadScripts([
		"resource/module/home/widgets/js/thermometer/jquery.thermometer.js"
		],function(){
			var run = setInterval(function(){
				try{
					var $thero = that.find('.thero');
					$thero.attr('id',"thero"+(Math.random()*1000).toFixed(0));
					var $thermoarea = that;
					var h = $thermoarea.height(),w=$thermoarea.width();
					
					//225*190.6;温度计比例
					if(h>w){
						h=w/225*190.6;
					}
					$thero.thermometer({
						title:data.title.name,
						subTitle:data.title.subname,
						startValue: parseFloat(data.currentVal),
						height: h,
						bottomText: plotThermometerChart.bottomText,
						topText: plotThermometerChart.topText,
						animationSpeed: plotThermometerChart.animationSpeed,
						maxValue: plotThermometerChart.maxValue,
						minValue: plotThermometerChart.minValue,
						textColour: plotThermometerChart.textColour,
						tickColour: plotThermometerChart.tickColour,
						pathToSVG:plotThermometerChart.pathToSVG,
						liquidColour: function( value ) {
							//var status = data.status;
							var c = colorLevel[data.status];
							if(!c)
								c= colorLevel['NORMAL'];
							return c.color;
							//return blendColors(plotThermometerChart.liquidColour[0],plotThermometerChart.liquidColour[1], value / 100); 
						},
						valueChanged: function(value) {
							var h = that.height();
							var ah = $thero.height();
							$thero.css({'margin-top':((h-ah)/2)+'px'});
						}
					});

					function blendColors(c0, c1, p) {
						var f=parseInt(c0.slice(1),16),t=parseInt(c1.slice(1),16),R1=f>>16,G1=f>>8&0x00FF,B1=f&0x0000FF,R2=t>>16,G2=t>>8&0x00FF,B2=t&0x0000FF;
						return "#"+(0x1000000+(Math.round((R2-R1)*p)+R1)*0x10000+(Math.round((G2-G1)*p)+G1)*0x100+(Math.round((B2-B1)*p)+B1)).toString(16).slice(1);
					}
					clearInterval(run);
					//that.find('.thero-name').html(data.metricName);
				}catch(e){
					
				}
			},100);
	});
	//*/

	oc.resource.loadScripts([
		"resource/third/echarts3.0/liquidfill.gzjs"
		],function(){
			var run = setInterval(function(){
				try{
					var option = plotThermometerChart.option;
					var $thero = that.find('.thero');
					$thero.attr('id',"thero"+(Math.random()*1000).toFixed(0));
					var $thermoarea = that;
					var h = $thermoarea.height(),w=$thermoarea.width();
					$thero.css({width:w+'px',height:h+'px'});

					var c = colorLevel[data.status];
					if(!c)
						c= colorLevel['NORMAL'];

					var value = parseFloat(data.currentVal);
					var maxValue = plotThermometerChart.maxValue;
					var minValue = plotThermometerChart.minValue;
					option.title[0].text = data.title.name;
					option.title[0].subtext = data.title.subname;
					option.title[0].top = h*0.5 + (w>h?h:w)*0.60/2;//画布中间位置+温度计绘制的近似高度一半
					option.series[0].color = [c.color];
					option.series[0].data= [{
					            value: value/(maxValue-minValue),
					            rawValue:value
					        }];
					//option.series[0].label.normal.distance = h*0.175/2-5; 

					if(data.status == 'WARN'){
						option.series[0].label.normal.textStyle.insideColor = '#000';
					}
					_this._plotThermometerChartDivIns.push($thero[0]);
					var myChart = echarts.init($thero[0]);
					myChart.setOption(option);
					option = null;

					clearInterval(run);
				}catch(e){}
			},100);
		});
	

}

//仪表盘
fmp.plotGaugeChartV2 = function(){
	var _this = this;
	_this.loadMetricRealData(function(data,metricList){
		_this._plotGaugeChartV2(data,metricList);
	});
}
fmp._plotGaugeChartV2 = function(data,metricList) {
	var _this = this;
	_this.dataObj.find('.gauge-area').remove();
	var count = 0;
	for(id in data){
		for( mid in data[id]){
			var obj = _this.dataObj.append('<div class="gauge-area"></div>');
			count++;
		}
	}

	_this.dataObj.find('.gauge-area').css({width:(100/count)+'%',height:'100%',display:'inline-block'});

	count = 0;
	if(_this._plotGaugeChartV2DivIns != null && _this._plotGaugeChartV2DivIns.length > 0){
		for(var i = 0; i < _this._plotGaugeChartV2DivIns.length; ++i){
			var myChartIns = echarts.getInstanceByDom(_this._plotGaugeChartV2DivIns[i]);
			echarts.dispose(myChartIns);
		}
		_this._plotGaugeChartV2DivIns = null;
	}
	_this._plotGaugeChartV2DivIns = [];
	for(id in data){
		var dat = data[id];
		var pName = getMetricName(id,metricList);
		for( mid in dat){
			var tmetric = dat[mid];

			var $obj = _this.dataObj.find('.gauge-area:eq(' + count +')');
			tmetric.parentName = pName;
			_this._drawGaugeChart(tmetric,$obj);
			count++;
		}
	}

	function getMetricName(id,metricList){
		var p = metricList[id]
		var name = p.name;
		while(p.parentId){
			p = metricList[p.parentId];
		}

		return p.name;
	}

}

fmp._drawGaugeChart = function(data,obj){
	var _this = this;
	var properties = $.extend(true,{},this.opt.properties,true);
	var option = properties.focusMetric.plotGaugeChartV2Option;
	
	this._gengaugeOption(properties,option,data);
	
	_this._plotGaugeChartV2DivIns.push(obj[0]);
	var myChart = echarts.init(obj[0]);
	myChart.setOption(option);
	option = null;
}

fmp._gengaugeOption = function(properties,option,data){

	var value = data.currentVal;
	var thresholds = data.thresholds;
	
	value = (isNaN(value)||value=='')?0:value;

	var min = 0;
	var middle1 = 50;
	var middle2 = 80;
	var max = 100; //取阈值的最大值的1.5倍作为最大值。

	if(thresholds && thresholds.match(/\[.{1,},\d+,\d+\]/i)){
		var p = thresholds.match(/\d+/g);
		min = 0;
		middle1 = parseFloat(p[0]);
		middle2 = parseFloat(p[1]);
		max = middle2 * 1.5;
		if(data.unit == '%')
			max = 100;
	}

	var scal = 1;
	if(max>100){
		//scal = 100;
		scal = Math.pow(10,(parseInt(max/100)+'').length);
	}
	if(max /scal%10 != 0){
		max = ((10-max/scal %10)+max/scal)*scal;
	}
	option.series[0].axisLine={
	                show: true,
	                lineStyle: { 
	                    color: [[middle1/max, option.series[0].axisLine.lineStyle.color[0][1]],[middle2/max, option.series[0].axisLine.lineStyle.color[1][1]],[1, option.series[0].axisLine.lineStyle.color[2][1]]], 
	                	width:6,
	                }
	            }
	option.series[0].axisLabel= {
	                show: true,
	                formatter:function(v){
	                	if(v/10%2 !=0)
	                		return '';
	                	v=parseInt(v*max/100/scal);
	                	return v;
	                },
	                textStyle:properties.focusMetric.plotGaugeChartV2Option.title.textStyle
	            }
	option.series[0].detail={
					show : true,
					offsetCenter:[0,'90%'],
					formatter:function(v){
						
						if(data.currentVal != undefined && data.currentVal != '')
							return properties.UnitTransformUtil.transform(data.currentVal , data.unit);
						else
							return '--';
					},
					textStyle: {
					    color: 'auto',
					    fontSize : 12
					}
				}
	option.series[0].data = [{
			value:value,
			unit:data.unit,
			name:data.text,
			pname:data.parentName
		}];
	option.title.text = data.text;
	option.title.subtext = data.parentName;
	option.tooltip.formatter = function(p){
		var t = '';
		t += p.name + ":";
		if(data.currentVal != undefined && data.currentVal != '')
			t +=  properties.UnitTransformUtil.transform(data.currentVal , data.unit);
		else
			t += '--';
		return t;
	}
		    
}

fmp._genCircleBlockChartData= function(data,metricList){
	var properties = this.opt.properties;
	var alarm = properties.focusMetric.metricCircle;
	var idata = [];
	for (id in data) {
		var pt = metricList[id];
		var subResource;
		while(pt.parentId){
			if(pt.isSubResource)
				subResource = pt;
			pt = metricList[pt.parentId];
			
		}

		for(var mid in data[id]){
			var mer = data[id][mid];
			var label = mer.currentVal + mer.unit;
			if(mer.type == 'AvailabilityMetric'){
				if(mer.status == 'CRITICAL')
					label = '不可用';
				else
					label = '可用';
			}else if(mer.type == 'InformationMetric'){
				if(isNaN(mer.currentVal) || mer.currentVal== undefined){
					label = (mer.currentVal==undefined)?'--':mer.currentVal;
					mer.currentVal = 100;
				}else{
					label = properties.UnitTransformUtil.transform(mer.currentVal,mer.unit);
				}
				if(!mer.status)
					mer.status = 'NORMAL';
			}else{
				if(mer.currentVal == '' || mer.currentVal == undefined)
					mer.currentVal = 0;
				label = properties.UnitTransformUtil.transform(mer.currentVal,mer.unit);
			}

			var metricText = mer.text;
			var subr ='';
			if(subResource){
				metricText = mer.text + '(' +  subResource.name + metricList[id].name  + ')';

				subr = subResource.name + metricList[id].name 
			}

			idata.push({
				id:mer.id,
				rootId:pt.id,
				rawMetric:mer.text,
				metric:metricText,
				subResource:subr,
				name:pt.name,
				value:mer.currentVal,
				status:mer.status,
				label:label,
				unit:mer.unit,
				color:alarm[mer.status].color
			});
		}
	}

	return idata;
}


//指标圈
fmp.plotMetricCircleChart = function(){

	var _this = this;
	_this.loadMetricRealData(function(data,metricList){
		_this._plotMetricCircleChart(data,metricList);
	});
}

fmp._plotMetricCircleChart = function(data,metricList){
	var _this = this;
	var idata = this._genCircleBlockChartData(data,metricList);

	var w = this.dataObj.width() / idata.length-1;
	var h = this.dataObj.height();
	w = w>h?h:w;
	var mh = ((h-w)/2) + 'px';
	w = w+'px'; 
	
	if(_this._plotCircleDivIns != null && _this._plotCircleDivIns.length > 0){
		for(var i = 0; i < _this._plotCircleDivIns.length; ++i){
			var myChartIns = echarts.getInstanceByDom(_this._plotCircleDivIns[i]);
			echarts.dispose(myChartIns);
		}
		_this._plotCircleDivIns = null;
	}
	_this._plotCircleDivIns = [];
	this.dataObj.html('');
	for(var i=0; i< idata.length;i++){
		var obj = $('<div/>');
		obj.css({width:w,
			height:h,
			'margin-top':0,
			display:'inline-block'});
		_plotCircle(obj[0],idata[i],this);
		this.dataObj.append(obj);
	}


	function _plotCircle(div,data,_this){
		var properties = $.extend(true,{},_this.opt.properties,true);
		var option = properties.focusMetric.plotMetricCircleChartOption;
		option.color = [data.color];
		option.series[0].name = data.name;

		var dts= [data];
		if(data.unit =='%'){
			var tdata = $.extend({},data,true);
			tdata.value = 100 - data.value;
			tdata.name = '空闲：<br/>';
			tdata.tooltip={show:false};//不显示tooltip
			tdata.label = ''; 
			dts.push(tdata);
			option.color = [data.color,properties.focusMetric.plotMetricCircleChartColor[0]];
		}

		option.series[0].data = dts;
		
		var me  = data.metric;
		me = me.length>20 ?(me.substring(0,20) + '...'):me;
		option.title.text = me;
		option.title.subtext = data.name;
		option.title.sublink='javascript:_showDeviceDetail("' + data.rootId + '")';
		option.title.subtarget='self';

		_this._plotCircleDivIns.push(div);
		var myChart = echarts.init(div);
		myChart.setOption(option);
		option = null;
		myChart.on('click',function(p){
			console.log(p);
		});
	}

	window._showDeviceDetail = function(resourceId){
		oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
			oc.module.resmanagement.resdeatilinfonew.open({instanceId:resourceId});
	 	});
	}
}


 
//指标块
fmp.plotMetricBlockChart = function(){
	var _this = this;
	_this.loadMetricRealData(function(data,metricList){
		_this._plotMetricBlockChart(data,metricList);
	});
}

fmp._plotMetricBlockChart = function(data,metricList){
	var data = this._genCircleBlockChartData(data,metricList);

	this.dataObj.html('');
	var cw=this.dataObj.width()/data.length;
	var w = (this.dataObj.width()-cw*0.05*2*data.length-4) / data.length;
	var h = this.dataObj.height();
	w=w>186?186:w;
	var mh = ((h-w)/2) + 'px';
	for(var i=0; i< data.length;i++){
		var obj = $('<div/>');
		obj.addClass('squareBox');
		obj.css({display:'inline-block',margin:cw*0.05,width:w+'px',height:w+20+'px'});
		_plotSquare(obj,data[i]);
		this.dataObj.append(obj);
	}
	var sh=this.dataObj.find('.square').height();
	this.dataObj.find('.square-value').css({'font-size':parseInt(0.1*sh)+'px'});
	this.dataObj.find('.square-name').css({'font-size':parseInt(0.2*sh)+'px'});
	this.dataObj.find('.square-word').css({'font-size':parseInt(0.1*sh)+'px'});

	function _plotSquare(div,data){
		var html = '<div class="square">';
		/*html += '<img src="/resource/themes/blue/images/home/zhibiao.png"></img>';*/
		html += '<div class = "square-fluid">' ;
		if(data.subResource !='' ){
			html += '<span  class = "square-value" title= "' + data.name +'">' + data.subResource +'</span>';
		}else{
			html += '<span  class = "square-value" title= "' + data.name +'">' + data.name +'</span>';
		}
		if(data.label == ''){
			data.label ='&nbsp;';
		}
		html += '<span class="square-name" title= "' + data.label +'">' +  data.label +'</span>';
		html += '</div>';
		html += '<div class="square-word" title="' + data.rawMetric +'">' + data.rawMetric + '</div>';
		html += '</div>';
		div.html(html);
	}

}


