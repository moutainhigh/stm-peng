//@ sourceURL=video_report.js
$(function(){
	var char = null;	//highchart对象
    var theme = Highcharts.theme.currentSkin;
    var properties;
    //加载报表图js
    oc.resource.loadScripts(['resource/module/video-diagnostic-report/widget.' + theme +'.properties.js'],function () {
        if(!properties)
            properties = oc.index.camera.report.properties;
    });
	//选择指标初始化事件
	var repotType = $(".video-title li.tabs-selected").attr("repotType");
	$(".video-title li").click( function () {
		repotType = $(this).attr("repotType");
		$(".video-title li").removeClass("tabs-selected");
		$(this).attr("class","tabs-selected");
		$("#video-type-title").text($(this).text());
		//判断是否有选中节点，并清空显示区域
		if(curZtree != null){
			ClickByVideoType(curZtree,rootNode);
		}else{
			alert("请选择组织节点!");
		}
	});
	
	//初始化日期组件
	var nowDate = new Date();
	var eTime = nowDate.getFullYear() + "-" + (nowDate.getMonth() + 1) + "-" + nowDate.getDate();
	var yesterDate = new Date(nowDate.getTime() - (24 * 60 * 60 * 1000));
	var sTime = yesterDate.getFullYear() + "-" + (yesterDate.getMonth() + 1) + "-" + yesterDate.getDate();
	$('#startTime').datebox({    
	    required:true,
	    editable:false,
	    value: sTime,
	    onSelect: function(newValue, oldValue){
    		var s = $('#startTime').datebox('getValue');
    		var e = $('#endTime').datebox('getValue');
    		if(e < s){
    			alert('开始日期应在结束日期之前！');	
    		}
	    },
	    onHidePanel: function(){
	    	if($('#startTime').datebox('getValue') == ""){
	    		alert('开始日期不能为空');	
	    	}
	    }
	});
	$('#endTime').datebox({    
	    required:true,
	    editable:false,
	    value: eTime,
	    onSelect: function(newValue, oldValue){
	    	var s = $('#startTime').datebox('getValue');
	    	var e = $('#endTime').datebox('getValue');
	    	if(e < s){
	    		alert('开始日期应在结束日期之前！');	
	    	}
	    },
	    onHidePanel: function(){
	    	if($('#endTime').datebox('getValue') == ""){
	    		alert('结束日期不能为空');	
	    	}
	    }
	});
	
	//初始化组织树结构
	var zTree = null;
	var curZtree = null;	//当前选中节点
    var rootNode = null;    //根节点
	var setting = {
	   		async: {
				enable: true,
				url:'/portal/video/report/getAllOrg.htm',
				dataType:"json",
				dataFilter:function(treeId, parentNode, responseData){
					var childs = responseData.data;
					return childs;
				}
			},
			view:{
				showIcon:false,
				showTitle:true,
				showLine:true
			},
			callback:{
				onClick:function(event, treeId, treeNode){
					curZtree = treeNode;
					var node = curZtree.getParentNode();
                    if(node != null){
                        while(node != null){
                            if(node.getParentNode() == null){
                                rootNode = node;
                            }
                            node = node.getParentNode();
                        }
                    }else{
                        rootNode = curZtree;
                    }
                    var curCopy = $.extend(true,{},curZtree,{});
                    var rootCopy = $.extend(true,{},rootNode,{});
                    rootCopy.children = null;
					ClickByVideoType(curCopy,rootCopy);
				},
				onAsyncSuccess:function(event, treeId, treeNode, msg) {
                    if(zTree.getNodes().length > 0){
                        var node = zTree.getNodes()[0].children[0];
                        //                 var node = zTree.getNodeByParam('id', 100000);
                        zTree.selectNode(node);//选择点  
                        zTree.setting.callback.onClick(null, zTree.setting.treeId, node);//调用事件
                    }
				}
			}
		};
	zTree = $.fn.zTree.init($("#organizational-structure").find("ul.ztree"), setting);
	
	//查询按钮时间
	$("#queryBtn").click(function(){
		if(curZtree != null){
			ClickByVideoType(curZtree,rootNode);
		}else{
			alert("请选择组织节点!");
		}
	})
	//点击节点事件
	function ClickByVideoType(treeNode,rootNode){
		var dataInfo = null;
		//查询
		oc.util.ajax({
			type: 'POST',
			async:false,
			url : oc.resource.getUrl('portal/video/report/getReportInfoByOrg.htm'),
			data : {
				 "node" : JSON.stringify(treeNode),
                 "rootNode" : JSON.stringify(rootNode),
				 "type" : repotType,
				 "startTime": $('#startTime').datebox('getValue'),
				 "endTime": $('#endTime').datebox('getValue')
			},
			success : function(data) {
				dataInfo = data.data;
				if(repotType == 'kh'){
					dataInfo = Analysis(dataInfo,treeNode);
				}else{
					dataInfo.sort(Compare);
				}
				$("#orgName").val(treeNode.name);
				$("#tableData").val(JSON.stringify(dataInfo));
			}
		});	
		
		if(repotType == 'zx'){
			ZxTable(dataInfo);
			ZxHybridGraph(dataInfo);
		}else if(repotType == 'gz'){
			GzTable(dataInfo);
			GzHybridGraph(dataInfo);
		}else if(repotType == 'ls'){
			WhTable(dataInfo);
			WhHybridGraph(dataInfo);
		}else if(repotType == 'kh'){
			KhTable(dataInfo);
			KhHybridGraph(dataInfo);
		}
	}
	//解析考核报表
	function Analysis(data,treeNode){
		var khData = [];
		for(var i = 0; i < data.length; ++i){
			var dataInfo = data[i].value.split('#--!!--#');
			var info = {
					number : dataInfo[0],
					name : dataInfo[1],
					indicatorNumber : dataInfo[2],
					vedioSum : dataInfo[3],
					accessRate : dataInfo[4],
					onlineQuality : dataInfo[6],
					onlineRate : dataInfo[7],
					intactQuality : dataInfo[9],
					intactRate : dataInfo[10],
					gisnum : dataInfo[11],
					gisRate : dataInfo[12],
					score : dataInfo[13]
			};
			if(data.length == 2){
				khData.push(info);
			}else{
				if(info.name != treeNode.name){
					khData.push(info);
				}
			}
		}
		var temp = khData.slice(0,-1);
		temp = temp.sort(Compare);
		khData[khData.length-1].gisRate = khData[khData.length-1].gisRate + "%";
		temp.push(khData[khData.length-1]);
		khData = temp;
		return khData;
	}
	
	
	//数组排序
	var Compare = function (obj1, obj2) {
		var val1 = null;
	    var val2 = null;
		if(repotType == 'zx'){
			val1 = obj1.score;
			val2 = obj2.score;
		}else if(repotType == 'gz'){
			val1 = obj1.damagedEquipment;
			val2 = obj2.damagedEquipment;
		}else if(repotType == 'ls'){
			val1 = obj1.score;
			val2 = obj2.score;
		}else if(repotType == 'kh'){
			val1 = obj1.score;
			val2 = obj2.score;
		}
	   
	    if (val2 < val1) {
	        return -1;
	    } else if (val2 > val1) {
	        return 1;
	    } else {
	        return 0;
	    }            
	} 
	
	//报表导出
	$(".export").click(function(){
		if(curZtree == null){
			alert("请选择组织节点!");
			return;
		}
		$("#exportType").val($(this).attr("type"));
		$("#reportType").val(repotType);
		$("#svgCode").val($("#viedo-chart").highcharts().getSVG());
		$("#sTime").val($('#startTime').datebox('getValue'));
		$("#eTime").val($('#endTime').datebox('getValue'));
		//导出报表
		$("#exportRe").submit();
	})
	
	//在线率表格
	function ZxTable(data){
		$(".video-table-info").html('');
		var div = $('<div class="video_tab" postion="76">');
		var table = $('<table class="tab-border">');
		var tbody = $('<tbody>')
		var trHead1 = $('<tr><th  class="td-bgcolor" colspan="5" >设备在线率表</th></tr>');
		var trHead2 = $('<tr><th class="td-bgcolor">单位名称</th><th class="td-bgcolor">在线数量</th><th class="td-bgcolor">总数</th><th class="td-bgcolor">在线率</th><th class="td-bgcolor">得分</th></tr>');
		tbody.append(trHead1);
		tbody.append(trHead2);
		//填入数据
		for(var i = 0; i < data.length; ++i){
			var str = '<tr>';
			str += '<td>'+data[i].name+'</td>';
			str += '<td>'+data[i].onlineQuality+'</td>';
			str += '<td>'+data[i].vedioSum+'</td>';
			str += '<td>'+data[i].onlineRate+'</td>';
			str += '<td>'+data[i].score+'</td>';
			str += '</tr>';
			tbody.append($(str));
		}
		
		table.append(tbody);
		div.append(table);
		$(".video-table-info").append(div);
	}
	//在线率混合图
	function ZxHybridGraph(data){
		var categories = [];
		var onlineData = [];
		var sumData = [];
		var scoreData = [];
		var index = data.length;
		if(data.length > 10){
			index = 10;
		}
		for(var i = 0 ; i < index; ++i){
			categories.push(data[i].name);
			onlineData.push(data[i].onlineQuality);
			sumData.push(data[i].vedioSum);
			scoreData.push(parseFloat(data[i].score));
		}
		var onlineProp = $.extend(true,{},properties.cameraReport,{});
		onlineProp.onlineRate.series[0].data = onlineData;
		onlineProp.onlineRate.series[1].data = sumData;
		onlineProp.onlineRate.series[2].data = scoreData;
        onlineProp.onlineRate.xAxis[0].categories = categories;
        $('#viedo-chart').highcharts(onlineProp.onlineRate);
	}
	
	//完好率表格
	function WhTable(data){
		$(".video-table-info").html('');
		var div = $('<div  class="video_tab" postion="76">');
		var table = $('<table class="tab-border">');
		var tbody = $('<tbody>')
		var trHead1 = $('<tr><th class="td-bgcolor" colspan="6">设备完好率表</th></tr>');
		var trHead2 = $('<tr><th class="td-bgcolor">单位名称</th><th class="td-bgcolor">完好数量</th><th class="td-bgcolor">在线数量</th><th class="td-bgcolor">总数</th><th class="td-bgcolor">完好率</th><th class="td-bgcolor">得分</th></tr>');
		tbody.append(trHead1);
		tbody.append(trHead2);
		//填入数据
		for(var i = 0; i < data.length; ++i){
			var str = '<tr>';
			str += '<td>'+data[i].name+'</td>';
			str += '<td>'+data[i].intactQuality+'</td>';
			str += '<td>'+data[i].onlineQuality+'</td>';
			str += '<td>'+data[i].vedioSum+'</td>';
			str += '<td>'+data[i].intactRate+'</td>';
			str += '<td>'+data[i].score+'</td>';
			str += '</tr>';
			tbody.append($(str));
		}
		
		table.append(tbody);
		div.append(table);
		$(".video-table-info").append(div);
	}
	
	//完好率混合图
	function WhHybridGraph(data){
		var categories = [];
		var intactData = [];
		var sumData = [];
		var scoreData = [];
		var index = data.length;
		if(data.length > 10){
			index = 10;
		}
		for(var i = 0 ; i < index; ++i){
			categories.push(data[i].name);
			intactData.push(data[i].intactQuality);
			sumData.push(data[i].vedioSum);
			scoreData.push(parseFloat(data[i].score));
		}
        var serviceabilityProp = $.extend(true,{},properties.cameraReport,{});
        serviceabilityProp.serviceabilityRate.series[0].data = intactData;
        serviceabilityProp.serviceabilityRate.series[1].data = sumData;
        serviceabilityProp.serviceabilityRate.series[2].data = scoreData;
        serviceabilityProp.serviceabilityRate.xAxis[0].categories = categories;
        $('#viedo-chart').highcharts(serviceabilityProp.serviceabilityRate);
	}
	
	//故障分析报表
	function GzTable(data){
		$(".video-table-info").html('');
		var div = $('<div class="video_tab" postion="76">');
		var table = $('<table class="tab-border">');
		var tbody = $('<tbody>')
		var trHead1 = $('<tr><th class="td-bgcolor" colspan="13">故障分析表</th></tr>');
		var trHead2 = $('<tr><th class="td-bgcolor">单位名称</th><th class="td-bgcolor">设备总数</th><th class="td-bgcolor">清晰度</th><th class="td-bgcolor">信号丢失</th><th class="td-bgcolor">亮度</th><th class="td-bgcolor">条纹干扰</th>'
				+'<th class="td-bgcolor">雪花干扰</th><th class="td-bgcolor">画面偏色</th><th class="td-bgcolor">PTZ速度</th><th class="td-bgcolor">PTZ角度</th><th class="td-bgcolor">画面冻结</th><th class="td-bgcolor">场景变换</th><th class="td-bgcolor">人为遮挡</th></tr>');
		tbody.append(trHead1);
		tbody.append(trHead2);
		//填入数据
		for(var i = 0; i < data.length; ++i){
			var str = '<tr>';
			str += '<td>'+data[i].name+'</td>';
			str += '<td>'+data[i].vedioSum+'</td>';
			str += '<td>'+data[i].legibility+'</td>';
			str += '<td>'+data[i].lostSignal+'</td>';
			str += '<td>'+data[i].brightness+'</td>';
			str += '<td>'+data[i].streakDisturb+'</td>';
			str += '<td>'+data[i].snowflakeDisturb+'</td>';
			str += '<td>'+data[i].colourCast+'</td>';
			str += '<td>'+data[i].ptzspeed+'</td>';
			str += '<td>'+data[i].cloudPlatFormInvalid+'</td>';
			str += '<td>'+data[i].screenFreezed+'</td>';
			str += '<td>'+data[i].sightChange+'</td>';
			str += '<td>'+data[i].keepOut+'</td>';
			str += '</tr>';
			tbody.append($(str));
		}
		
		table.append(tbody);
		div.append(table);
		$(".video-table-info").append(div);
	}
	//设备故障图
	function GzHybridGraph(data){
        data.sort(function (a,b) {
            return parseInt(a.id) - parseInt(b.id);
        })
		var categories = [];
		var sumData = [];
		var index = data.length;
		if(data.length > 10){
			index = 10;
		}
		for(var i = 0 ; i < index; ++i){
			var videoInfo = data[i];
			var sumInfo = videoInfo.legibility + videoInfo.lostSignal + videoInfo.brightness + videoInfo.streakDisturb
				+ videoInfo.snowflakeDisturb + videoInfo.colourCast + videoInfo.ptzspeed + videoInfo.cloudPlatFormInvalid
				+ videoInfo.screenFreezed + videoInfo.sightChange + videoInfo.keepOut;
			sumData.push(sumInfo);
			categories.push(data[i].name);
		}
        var failureProp = $.extend(true,{},properties.cameraReport,{});
        failureProp.failureRate.series[0].data = sumData;
        failureProp.failureRate.xAxis[0].categories = categories;
        $('#viedo-chart').highcharts(failureProp.failureRate);
	}
	
	//考核分析报表
	function KhTable(data){
		$(".video-table-info").html('');
		var div = $('<div class="video_tab" postion="76">');
		var table = $('<table class="tab-border">');
		var tbody = $('<tbody>')
		var trHead1 = $('<tr><th class="td-bgcolor" colspan="14">视频设备考核报表</th></tr>');
		var trHead2 = $('<tr><th class="td-bgcolor" rowspan="2">序号</th><th class="td-bgcolor" rowspan="2">地市名</th><th class="td-bgcolor" colspan="3">上联统计</th>' +
				'<th class="td-bgcolor" colspan="3">在线率统计</th><th class="td-bgcolor" colspan="3">完好率统计</th><th class="td-bgcolor" colspan="2">GIS采集率</th><th class="td-bgcolor" rowspan="2">得分</th></tr>');
		var trHead3 = $('<tr><th class="td-bgcolor">考核指标数</th><th class="td-bgcolor">上联数量</th><th class="td-bgcolor">接入率</th><th class="td-bgcolor">上联数量</th><th class="td-bgcolor">在线数</th><th class="td-bgcolor">在线率</th>'
				+'<th class="td-bgcolor">在线数</th><th class="td-bgcolor">完好数</th><th class="td-bgcolor">图像完好率</th><th class="td-bgcolor">GIS数</th><th class="td-bgcolor">GIS率</th></tr>');
		tbody.append(trHead1);
		tbody.append(trHead2);
		tbody.append(trHead3);
		//填入数据
		for(var i = 0; i < data.length; ++i){
			var str = '<tr>';
			str += '<td>'+(i + 1)+'</td>';
			str += '<td>'+data[i].name+'</td>';
			str += '<td>'+data[i].indicatorNumber+'</td>';
			str += '<td>'+data[i].vedioSum+'</td>';
			str += '<td>'+data[i].accessRate+'</td>';
			str += '<td>'+data[i].vedioSum+'</td>';
			str += '<td>'+data[i].onlineQuality+'</td>';
			str += '<td>'+data[i].onlineRate+'</td>';
			str += '<td>'+data[i].onlineQuality+'</td>';
			str += '<td>'+data[i].intactQuality+'</td>';
			str += '<td>'+data[i].intactRate+'</td>';
			str += '<td>'+data[i].gisnum+'</td>';
			str += '<td>'+data[i].gisRate+'</td>';
			str += '<td>'+data[i].score+'</td>';
			str += '</tr>';
			tbody.append($(str));
		}
		
		table.append(tbody);
		div.append(table);
		$(".video-table-info").append(div);
	}
	//考核图
	function KhHybridGraph(data){
		data = data.slice(0,-1);
		var index = data.length;
		if(data.length > 10){
			index = 10;
		}
		var series = [];
		var serie = {
				name: '考核得分',
				colorByPoint: true
		};
		serie.data = [];
		
		for(var i = 0 ; i < index; ++i){
			var videoInfo = data[i];
			var score = parseFloat(data[i].score);
			if(score == 0.0){
				serie.data.push({
					name : data[i].name
				});
			}else{
				serie.data.push({
					name : data[i].name,
					y : parseFloat(data[i].score),
				});
			}
			
		}
		series.push(serie);
        var assessProp = $.extend(true,{},properties.cameraReport,{});
        assessProp.assessRate.title.text = serie.name;
        assessProp.assessRate.series = series;
        $('#viedo-chart').highcharts(assessProp.assessRate);
	}
});