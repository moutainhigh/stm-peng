function topN(selector,opt){
	this.opt = {
		chartType:'horizontal',//Horizontal-水平柱状图，vertical-竖式柱状图，table-表格式图，
		chartVersion:'v2'
	};
	this.opt.selector = selector;

	this.opt = $.extend(this.opt,opt,true);
	if(this.opt.selector)
		this.init();
	else
		throw('初始化失败!');
}

var tnp = topN.prototype;

tnp.init = function(){
	var _this = this;
	var opt = this.opt;
	var cmi = opt.choiseMetricInfo;
	var url = oc.resource.getUrl('system/home/getHomeTopnData.htm');
	var params = {
            resource:cmi.resourceType,
            sortAll:cmi.checkAll,
            metric:cmi.choiseMetric,
            resourceIds:cmi.resourceIds,
            domainId:opt.domains,
            sortMethod:opt.sortMethod,
            top:opt.sortCount,
            showPattern:opt.showPattern,
            subMetrics:cmi.subMetrics,
            groupId:0
        };
     if(cmi.resourceType == 'Business'){
     	url = oc.resource.getUrl('portal/business/service/getBizTopnInfo.htm');
     	params ={
     		resource:cmi.resourceIds,
     		metric:cmi.choiseMetric,
     		resourceIds:cmi.resourceIds,
     		sortMethod:opt.sortMethod,
            top:opt.sortCount
     	}
     }

	oc.util.ajax({
        url: url,
        params:params,
        timeout:0,
        stopProgress:function(){},
        startProgress:function(){},
        success: function (data) {
            if(data.code == '200'){
            	if(data.data.data == null || data.data.data.length<0){
//            		alert('当前topn排序数据为空!');
            		return;
            	}
            	_this.chartData = data.data;
       			var ctg = _this.chartData.categories;
       			for(var i=0;i<ctg.length;i++){
       				ctg[i] = ctg[i].substring(0,15); //限制字符数
       			}     

            	_this._init(_this.chartData);
            	
            	_this.chartData = null;
            	data = null;
            }
        }
    });
}

tnp.refresh = function(){
	this.init();
}

tnp._init = function(data){
	var opt = this.opt;
	var _this = this;
	

	if(this.opt.chartType == 'horizontal'){
		var sm =opt.sortMethod;
		if(sm == 'desc')
			sm = 'asc';
		else
			sm = 'desc';

		_this.srotData(data,sm);
		this.plotHorizontalChartV2();
	}else if(this.opt.chartType == 'vertical'){
		_this.srotData(data,opt.sortMethod);
		this.plotVerticalChart();
	}else if(this.opt.chartType == 'table'){
		_this.srotData(data,opt.sortMethod);
		this.plotTableChart();
	}
}

tnp.srotData = function(data,sortType){
	var ctg = data.categories;
	var dt = data.data;
	var tmp;
	if(sortType == 'desc'){
		for(var i=0;i<dt.length;i++){
			for(var j=dt.length-1; j>i;j--){
				if(_parseFloat(dt[j].y) > _parseFloat(dt[i].y)){
					tmp=dt[i];
					dt[i] = dt[j];
					dt[j] = tmp;

					tmp=ctg[i];
					ctg[i] = ctg[j];
					ctg[j] = tmp; 
				}
			}
		}
	}else{//asc
		for(var i=0;i<dt.length;i++){
			for(var j=dt.length-1; j>i;j--){
				if(_parseFloat(dt[j].y) < _parseFloat(dt[i].y)){
					tmp=dt[i];
					dt[i] = dt[j];
					dt[j] = tmp;

					tmp=ctg[i];
					ctg[i] = ctg[j];
					ctg[j] = tmp; 
				}
			}
		}
	}

	function _parseFloat(v){
		var _t = parseFloat(v);
		if(_t==NaN || isNaN(_t))
			_t=0;
		return _t;
	}

}

tnp.plotHorizontalChart = function(){
	var data = [
        {name:'上海-门户数据库',value:65.33},
        {name:'北京-邮件数据库',value:59.35},
        {name:'哈尔滨-短信数据库',value:46.23},
        {name:'上海-金融数据库',value:40.00},
        {name:'天津-办公系统数据库',value:34.55},
    ];
    var liHtml =  "";
    $.each(data,function(k,v){
        liHtml += '<li>';
        liHtml += '    <span class="toph-data-name">' + v.name + '</span>';
        liHtml += '    <span class=" toph-data-bg">';
        liHtml += '        <span class="toph-data-v">';
        liHtml += '        </span>';
        liHtml += '    </span>';
        liHtml += '    <span class="toph-data-p">' + v.value + '%</span>';
        liHtml += '</li>';
    });
    var ulHmtl = '<ul class="toph-data">';
    ulHmtl += liHtml;
    ulHmtl += '</ul>';

    var html = "";
    html += '<div class="toph">';
    html += ulHmtl;
    html += '</div>';

    this.opt.selector.html(html);
    loadAnimation();

	//加载动画
	function loadAnimation(){
	    var c=0;
	    var si = setInterval(function(){
	    $(".toph-data-v").each(function(i,v){
	        var p =  $(v).parent();
	        var w =  p.width();
	        var pt = parseFloat(p.next().text())/100;
	        var tLen = w * pt;
	        var vl = c * (tLen/30);
	        $(v).css({width:vl+'px'});
	    });
	    c +=1;
	    if(c == 31){
	        clearInterval(si);  
	    }
	    },5);
	}
}

tnp.plotHorizontalChartV2 = function(){
	var _this =this;
	var properties = $.extend(true,{},this.opt.properties,true);
	var color = properties.topN.color;
	var option = $.extend(true,{},properties.topN.plotHorizontalChartV2Option,true);

	var minf = this.opt.choiseMetricInfo.metricDetial;
	var cd = this.chartData;
	for(var i = 0; i < cd.categories.length; ++i){
		if(_this.GetLength(cd.categories[i]) > 14){
			cd.categories[i] = _this.Cutstr(cd.categories[i],14);
		}
	}
	option.yAxis[0].data = cd.categories;
	option.yAxis[0].triggerEvent = true;
	var dts =[];
	var ctgv = {};
	for(var i=0; i< cd.data.length;i++){
		var d = cd.data[i];
		var cor = color[d.rawStatus]['color'];
		d.itemStyle={normal:{ color:cor}};
		var n = parseFloat(d.y);
		d.value = isNaN(n)?0:n;
		d.metricName = minf.name;
		d.unit = minf.unit;
		var q = properties.UnitTransformUtil.transform(d.value,d.unit);
		d.formatValue = q;
		
		dts.push(d);
		ctgv[cd.categories[i]] = d;
	}
	option.series[0].data = dts;
	//option.xAxis[0].axisLabel.formatter = function (value, index) {
	//	return properties.UnitTransformUtil.transform(value,dts[0].unit);
	//}
	option.title.text = dts[0].unit;
	//判断是百分比的数值的时候超过50%数字间隔
	if(option.title.text == "%" && option.series[0].data.length > 0){
		var max = 0;
		for(var i = 0; i < option.series[0].data.length; ++i){
			max = option.series[0].data[i].value;
		}
		if(max > 50){
			option.xAxis[0].min = 0;
			option.xAxis[0].max = 100;
		}
	}

	var rt = _this.opt.choiseMetricInfo.resourceType;
	
	var myChart = echarts.getInstanceByDom(this.opt.selector[0]);
	echarts.dispose(myChart);
	
	myChart = echarts.init(this.opt.selector[0]);
	myChart.setOption(option);
	option = null;
	myChart.on('click',function(p){
		var rid;
		if(p.data){
			rid = p.data.resourceInstanceId;
		}else{
			if(ctgv[p.value])
				rid = ctgv[p.value]['resourceInstanceId'];

		}
		if(rid)
			_this.showDeviceDetail(rid,rt,p.value);
		//cosole.log(p);
	});
	
}
tnp.GetLength = function(str) {
  return str.replace(/[\u0391-\uFFE5]/g,"aa").length;  //先把中文替换成两个字节的英文，在计算长度
}; 

tnp.Cutstr = function(str, len) {
    var str_length = 0;
    var str_len = 0;
    var str_cut = new String();
    str_len = str.length;
    for (var i = 0; i < str_len; i++) {
      var a = str.charAt(i);
      str_length++;
      if (escape(a).length > 4) {
        //中文字符的长度经编码之后大于4 
        str_length++;
      }
      str_cut = str_cut.concat(a);
      if (str_length >= len) {
        str_cut = str_cut.concat("...");
        return str_cut;
      }
    }
    //如果给定字符串小于指定长度，则返回源字符串； 
    if (str_length < len) {
      return str;
    }
  }


tnp.plotVerticalChart = function(){
	var _this =this;
	var properties = $.extend(true,{},this.opt.properties,true);
	var color = properties.topN.color;
	var option = $.extend(true,{},properties.topN.plotVerticalChartOption,true);
	
	var minf = this.opt.choiseMetricInfo.metricDetial;
	var cd = this.chartData;
	option.xAxis[0].data = cd.categories;
	option.xAxis[0].triggerEvent = true;
	var dts =[];
	var ctgv = {};
	for(var i=0; i< cd.data.length;i++){
		var d = cd.data[i];
		var cor = color[d.rawStatus]['color'];

		d.itemStyle={normal:{ color:cor}};
		var n = parseFloat(d.y);
		d.value = isNaN(n)?0:n;
		d.metricName = minf.name;
		d.unit = minf.unit;
		var q = properties.UnitTransformUtil.transform(d.value,d.unit);
		d.formatValue = q;
		dts.push(d);
		ctgv[cd.categories[i]] = d;
	}
	option.series[0].data = dts;
	if(dts.length == 0){
		dts.push({unit:""});
	}
	option.title.text ='单\n位\n：\n'+ dts[0].unit;
	var rt = _this.opt.choiseMetricInfo.resourceType;
	
	var myChart = echarts.getInstanceByDom(this.opt.selector[0]);
	echarts.dispose(myChart);
	myChart = echarts.init(this.opt.selector[0]);
	myChart.setOption(option);
	option = null;
	myChart.on('click',function(p){
		var rid;
		if(p.data){
			rid = p.data.resourceInstanceId;
		}else{
			if(ctgv[p.value])
				rid = ctgv[p.value]['resourceInstanceId'];

		}
		if(rid)
			if(rt == 'Business' && p.componentType == "series"){
				_this.showDeviceDetail(rid,rt,p.name);
			}else{
				_this.showDeviceDetail(rid,rt,p.value);
			}
		//cosole.log(p);
	});

}

tnp.plotTableChart = function(){
	var properties = $.extend(true,{},this.opt.properties,true);
	var option = $.extend(true,{},properties.topN.plotHorizontalChartV2Option,true);

    var minf = this.opt.choiseMetricInfo.metricDetial;
	var cd = this.chartData;

	var data =[];
	var isSubMetrics = cd.isSubMetrics;
	if(isSubMetrics){
        for(var i=0; i< cd.data.length;i++){
            var d = cd.data[i];
            var n = parseFloat(d.y);
            d.value = (isNaN(n)?0:n.toFixed(2)) + minf.unit;;
            d.metricName = minf.name;
            d.unit = minf.unit;
            var subName = d.subName;
            d.index = 1+i;
            d.class = 'top-' + (i+1);
            var q = properties.UnitTransformUtil.transform(d.value,d.unit);
            d.formatValue = q;
            d.title = d.name + " " + subName + " " + minf.name + " " + q;

            data.push(d);
        }

        var head = ['排名','主资源名称','子资源名称',minf.name];

        var html = '<div class="top">';
        html += '     <div class="top-content">';
        html += ' <div class="horn horn-left-top"></div>';
        html += ' <div class="horn horn-right-top"></div>';
        html += ' <div class="horn horn-left-bottom"></div>';
        html += ' <div class="horn horn-right-bottom"></div>';
        html += '        <table cellpadding="0" cellspacing="0">';
        $.each(head,function(k,v){
            html += '<th>' + v + '</th>';
        });

        $.each(data,function(v,dt){
            var lc = "tr-even";
            if(v%2>0)
                lc="";

            html += '<tr class="' + lc + '" title="' + dt.title +'">';
            html += '<td><span class="' + dt.class　+'">' + dt.index +'</span></td>';
            html += '<td class= "resource-name" rid="' + dt.resourceInstanceId +'">' + dt.name + '</td>';
            html += '<td class= "resource-name" rid="' + dt.resourceInstanceId +'">' + dt.subName + '</td>';
            html += '<td class="alert_color" >' + dt.formatValue + '</td>';
            html += '</tr>';
        });
        html += '</table>';
        html += '</div>';
        html += '</div>';
    }else{
        for(var i=0; i< cd.data.length;i++){
            var d = cd.data[i];
            var n = parseFloat(d.y);
            d.value = (isNaN(n)?0:n.toFixed(2)) + minf.unit;;
            d.metricName = minf.name;
            d.unit = minf.unit;
            var subName = '';
            if(d.name !=  cd.categories[i]){
                subName = d.name;
            }
            d.name = cd.categories[i];
            d.index = 1+i;
            d.class = 'top-' + (i+1);
            var q = properties.UnitTransformUtil.transform(d.value,d.unit);
            d.formatValue = q;
            d.title = d.name + " " + subName + " " + minf.name + " " + q;

            data.push(d);
        }

        var head = ['排名','名称',minf.name];

        var html = '<div class="top">';
        html += '     <div class="top-content">';
        html += ' <div class="horn horn-left-top"></div>';
        html += ' <div class="horn horn-right-top"></div>';
        html += ' <div class="horn horn-left-bottom"></div>';
        html += ' <div class="horn horn-right-bottom"></div>';
        html += '        <table cellpadding="0" cellspacing="0">';
        $.each(head,function(k,v){
            html += '<th>' + v + '</th>';
        });

        $.each(data,function(v,dt){
            var lc = "tr-even";
            if(v%2>0)
                lc="";

            html += '<tr class="' + lc + '" title="' + dt.title +'">';
            html += '<td><span class="' + dt.class　+'">' + dt.index +'</span></td>';
            html += '<td class= "resource-name" rid="' + dt.resourceInstanceId +'">' + dt.name + '</td>';
            html += '<td class="alert_color" >' + dt.formatValue + '</td>';
            html += '</tr>';
        });
        html += '</table>';
        html += '</div>';
        html += '</div>';
    }

    this.opt.selector.html(html);
    var _this = this;
    var rt = _this.opt.choiseMetricInfo.resourceType;
    $('.top-content .resource-name').css('cursor','pointer');
    $('.top-content .resource-name').unbind().bind('click',function(){
    	var value = $(this).text();
    	var id = $(this).attr('rid');
    	_this.showDeviceDetail(id,rt,value);
    });
}

tnp.showDeviceDetail = function(resourceId,resourceType,bizname){
		if(resourceType == 'Business'){
			oc.resource.loadScript("resource/module/business-service/biz-detail-info/js/business_detail_info.js", function(){
                oc.module.biz.detailinfo.open({bizId:resourceId,bizName:bizname});
            });
			return;
		}
		if(resourceType == 'Link'){
			oc.resource.loadScript("resource/module/topo/contextMenu/TopoLinkInfo.js",function(){
				new TopoLinkInfo({
					onLoad:function(){
						this.load(resourceId);
					}
				});
			});
			return;
		}
		
		oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js', function(){
			oc.module.resmanagement.resdeatilinfonew.open({instanceId:resourceId});
	 	});
	}