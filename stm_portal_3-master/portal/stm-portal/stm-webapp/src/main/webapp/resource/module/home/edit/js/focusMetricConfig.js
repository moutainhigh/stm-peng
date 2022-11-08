$(function(){
	function focusMetricConfig(opt) {
		this.opt = {
			panelArea : $(".widget-edit-panel .right"),//默认对象显示区域
			showConfigDialog: undefined,//注册父级调用函数
			args:undefined
		};
		$.extend(this.opt,opt,true);
		
		this.showConfigDialog = this.opt.showConfigDialog;
		if(!this.showConfigDialog){
			throw '调用失败！';
		}

		this.moduleCode = 'focusMetric';

		this.systemResoures = {};//供选择的资源类型
		this.metricChooseHelper = undefined;
		//console.log(opt);
		this.init();
	}
	var fmcp = focusMetricConfig.prototype;

	fmcp.init = function(){
		var _this = this;
		var $obj = this.opt.panelArea;
		$obj.load(oc.resource.getUrl('resource/module/home/edit/focusMetricConfig.html'),function(){
			oc.resource.loadScript([
				//'resource/module/home/edit/js/MetricChooseHelper.js'，
				'resource/module/home/edit/js/ResourceMetricTree.js'], function(){
			 	_this._init();
			});
		});
	}

	fmcp._init = function(){
		var _this = this;
		
		$('#time').combobox({
			width:160,
			panelHeight:'auto',
			editable:false,
			data:[
			    {value:'24h',text:'最近24小时'},
			    {value:'1w',text:'最近一周'},
			    {value:'1m',text:'最近一个月'},
			    {value:'custom',text:'自定义'}
	      ]
		});
		$('#time').combobox({
			onSelect : function(record){
				var v = record.value;
				var p = $("#time").parent();
				if(v == 'custom'){
					_this._addCustomTime(p);
				}else{
					p.find(".inputArea").remove();
				}
			}
		});
		
		var data = {};
		if(_this.opt.args && _this.opt.args.cfg){
			var pros = _this.opt.args.cfg.props;
			var data = JSON.parse(pros);
		}
		_this.metricChooseHelper = oc.index.home.widgetedit.metricChooseHelper(data.metrics);
		_this.metricChooseHelper.regMetricVerify(function(){
			return _this._checkData();
		});
		if(data.title == '' || data.title == null){
			$("#title").val("关注指标");
		}else{
			$("#title").val(data.title);
		}
		$("#showName").attr('checked',data.showName);
		var typ = data.chartType;
		if($('.graph-list-d[data-val="' + typ+'"] ').length ==0)
			typ = 'line-m';
		$('.graph-list-d[data-val="' + typ+'"] ').addClass('graph-list-d-selected');
		_this._setMetricTypes(typ);

		if(data.time == undefined || data.time == ''){
			data.time = '24h';
		}
		$('#time').combobox('setValue', data.time);
		if(data.time == 'custom'){
			var p = $("#time").parent();
        	_this._addCustomTime(p,data.start,data.end);
		}
		_this.regEvent();
	}



	fmcp._getData = function(){
		var data = {};
		if($("#title").val() == ''){
			data.title = "关注指标";
		}else{
			data.title = $("#title").val();
		}
        data.showName = $("#showName").is(":checked");
        data.chartType = $(".graph-list-d-selected").attr('data-val');
       // data.type = $("[name=type]:checked").val();
       // data.domains = $("#domains").val();
        data.time = $('#time').combobox('getValue');
        data.metrics = this.metricChooseHelper.getData();//选中的指标
        if(data.time == 'custom'){
        	data.start = $("#start").datetimebox('getValue');
        	data.end = $("#end").datetimebox('getValue');
    	}
        return data;
	}

	fmcp._checkData = function(){
		var data = this._getData();
		//console.log(data);

		var metrics = data.metrics;
		switch(data.chartType){

			case 'area' :
			{
				var ct = getMetricCount(metrics);
				if(ct[1] > 1){
					alert('只能选择一个指标');
					return false;
				}

				var sp = checkIsSupportBusinessMetric(metrics,'history');
				if(!sp){
					alert('该业务指标不支持该图表类型！');
					return false;
				}
				
				break;

			}
			case 'line-s' :{}
			case 'line-m' : {
				var ct = getMetricCount(metrics);
				if(ct[1]<1){
					alert('请选择指标');
					return false;
				}
				if(ct[1] > 5){
					alert('抱歉，您最多只能选择5个指标');
					return false;
				}
				var cs = isSameUnit(metrics);
				if(!cs){
					alert('只能选择单位相同指标');
					return false;
				}
				var sp = checkIsSupportBusinessMetric(metrics,'history');
				if(!sp){
					alert('该业务指标不支持该图表类型！');
					return false;
				}
				break;
			}
			case 'radarv2' : {
				var ct = getMetricCount(metrics);
				if(ct[1] > 10){
					alert('抱歉，您最多只能选择10个指标');
					return false;
				}
				var ipu = isPercentUnit(metrics);
				if(!ipu){
					alert('只能选择单位为百分比的指标');
					return false;
				}
				break;
			}
			case 'funnel' : {
				var ct = getMetricCount(metrics);
				if(ct[0]>1){
					alert('抱歉，您只能选择单个资源');
					return false;
				}
				if(ct[1] > 10){
					alert('抱歉，您最多只能选择10个指标');
					return false;
				}
				var cs = isSameUnit(metrics);
				if(!cs){
					alert('只能选择单位相同指标');
					return false;
				}
				break;
			}
			case 'thermometer' : {
				if(!isThermometerMetric(metrics)){
					alert('抱歉，您只能选择温度指标');
					return false;
				}
				var ct = getMetricCount(metrics);
				if(ct[1] > 5){
					alert('抱歉，您最多只能选择5个指标');
					return false;
				}
				break;
			}
			case 'gaugev2' : {
				var ct = getMetricCount(metrics);
				if(ct[1] > 5){
					alert('抱歉，您最多只能选择5个指标');
					return false;
				}
				break;
			}
			case 'metricCircle' : {
				var ct = getMetricCount(metrics);
				if(ct[1] > 5){
					alert('抱歉，您最多只能选择5个指标');
					return false;
				} else if(ct[1]<1){
					alert('请选择指标');
					return false;
				}
				break;
				
			}
			case 'metricBlock' : {
				var ct = getMetricCount(metrics);
				if(ct[1] > 5){
					alert('抱歉，您最多只能选择5个指标');
					return false;
				} else if(ct[1]<1){
					alert('请选择指标');
					return false;
				}
				break;
			}
			default:{
				var ct = getMetricCount(metrics);
				if(ct[1]<1){
					alert('请选择指标');
					return false;
				}
			}

		}
		return true;

		/*返回[主资源数量,指标数量]*/
		function getMetricCount(data){
			var mainResourceCount = 0;

			if(data == undefined || data.length ==0)
				return [0,0];

			var cont = traveTree(data);
			return [mainResourceCount,cont];

			function traveTree(trees){
				var ta = 0;
				for(var j=0; j<trees.length; j++){
					var tree = trees[j];
					if(!tree)
						continue;
					if(tree.mainResource)
						mainResourceCount +=1;
					if(tree.children)
						ta+=traveTree(tree.children);
					else{
						ta+=1;
					}
				}
				return ta;
			}
		}

		function isSameUnit(data){
			if(data == undefined || data.length ==0)
				return true;
			return traveTree(data);

			function traveTree(trees){
				var unit = undefined;
				var ta = true;
				
				for(var j=0; j<trees.length; j++){
					var tree = trees[j];
					if(!tree)
						continue;
					if(tree.children)
						ta = ta && traveTree(tree.children);
					else{
						if(unit == undefined){
							unit = tree.unit;
						}
						ta = ta && (tree.unit == unit);

						if(!ta)
							return false;
					}
					
				}

				return ta;
			}
		}

		function isThermometerMetric(data){
			var isThermometer = true;
			traveTree(data);
			return isThermometer;
			 
			function traveTree(trees){
				for(var j=0; j<trees.length; j++){
					var tree = trees[j];
					if(!tree)
						continue;
					if(tree.children){
						if(isThermometer)
							traveTree(tree.children);
					}
					else{
						if(!isThermometer)
							return;
						isThermometer = (tree.id == 'temperatureValue');
					}
					
				}
			}
		}
		
		/*如果是业务指标，则检查指标是否支持当前图表类型
		* supportType = history | point
		* history 表示可以查历史值指标，point表示支持查询瞬时值指标
		*/
		function checkIsSupportBusinessMetric(data,supportType){
			

			if(data == undefined || data.length ==0)
				return true;

			return traveTree(data);

			function traveTree(trees){
				var support = true;
				for(var j=0; j<trees.length; j++){
					var tree = trees[j];
					if(!tree)
						continue;
					if(tree.children)
						support = support && traveTree(tree.children);
					else{
						if(tree.extendAttribute){
							support = support &&  tree.extendAttribute[supportType] == 'true';
						}
					}
					if(!support)
						return support;
				}
				return support;
			}
		}

		/*
		*检查指标是否是百分比指标
		*
		*/
		function isPercentUnit(data){
			if(data == undefined || data.length ==0)
				return true;
			return traveTree(data);

			function traveTree(trees){
				var unit = '%';
				var ta = true;
				
				for(var j=0; j<trees.length; j++){
					var tree = trees[j];
					if(!tree)
						continue;
					if(tree.children)
						ta = ta && traveTree(tree.children);
					else{
						if(unit == undefined){
							unit = tree.unit;
						}
						ta = ta && (tree.unit == unit);

						if(!ta)
							return false;
					}
					
				}

				return ta;
			}
		}

	}

	fmcp._addCustomTime = function(p,start,end){
		start = (start == undefined)?new Date().stringify():start;
		end = (end == undefined)?new Date().stringify():end;
		var $div1 = $("<div/>").attr('id',oc.util.generateId()).addClass('inputArea').css({display: 'inline-block'});
		var $u1 = $("<ul/>").attr('id','start').css({display: 'inline-block',width:'150px'});
		var $u2 = $("<ul/>").attr('id','end').css({display: 'inline-block',width:'150px'});;
		$div1.append($u1);
		$div1.append($u2);
		p.append($div1);
		$u1.datetimebox({
		    value: start,
		    required: true,
		    showSeconds: false
		});
		$u2.datetimebox({
		    value: end,
		    required: true,
		    showSeconds: false
		});
	}
	fmcp.regEvent = function(){
		var _this = this;

		$(".graph-list-d").bind('click',function(){
			if($(this).hasClass('graph-list-d-selected'))
				return;
			$(this).parent().find('.graph-list-d-selected').removeClass('graph-list-d-selected');
			$(this).addClass('graph-list-d-selected');
			var type = $(this).attr('data-val');
			_this._setMetricTypes(type);
		});

//		$("#time").unbind().bind('change',function(){
//			var v = $(this).val();
//			var p = $(this).parent();
//			if(v == 'custom'){
//				_this._addCustomTime(p);
//			}else{
//				p.find(".inputArea").remove();
//			}
//		});
		
		

		
		//截取标题字段
        $("#title").bind('change',function(){
        	var maxlength = parseInt($(this).attr('maxlength'));
        	var inData = $(this).val();
        	var inlength = $(this).val().length;
        	if(inlength > maxlength){
        		inData = inData.substring(0,maxlength - 1);
        		$(this).val(inData);
        	}
        });

		_this.showConfigDialog.setCustomColseDialog(true);
		_this.showConfigDialog.regConfirmFunction(function(){
            var cfg = {};
            if(_this.opt.args && _this.opt.args.cfg){
                cfg =$.extend({},_this.opt.args.cfg,true);
            }

            //校验数据是否是正确的满足格式的
            if(!_this._checkData())
            	return;
           
            cfg.props = _this._getData();
            _this.showConfigDialog.save(_this.moduleCode,cfg);
            _this.showConfigDialog.closeDialog();
            _this.showConfigDialog.setCustomColseDialog(false);
        });

        _this.showConfigDialog.regCancelFunction(function(){
        	_this.showConfigDialog.closeDialog();
            _this.showConfigDialog.setCustomColseDialog(false);
        });
	}

	fmcp._setMetricTypes = function(type,clearData){
		var metricTypes = 'PerformanceMetric';	
		if(type == 'metricCircle' || type == 'metricBlock'){
			metricTypes = 'InformationMetric,PerformanceMetric,AvailabilityMetric';
		}
		
		if(type == 'thermometer'){
			metricTypes = 'InformationMetric';
		}
		if(type == 'line-m' || type =='area'){
			$('.timearea').show();
		}else{
			$('.timearea').hide();
		}

		this.metricChooseHelper.setMetricTypes(metricTypes);
		
		if(clearData)
			this.metricChooseHelper.clearData();

	}

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.focusMetricConfig = function(opt){
    	return new focusMetricConfig(opt);
    }
})