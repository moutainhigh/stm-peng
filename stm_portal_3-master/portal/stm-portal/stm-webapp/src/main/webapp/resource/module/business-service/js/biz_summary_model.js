/**
 * Created by niexiaoqin on 2016/8/29.
 */

//数字滚动变化效果
(function($) {
	$('#biz-disk').attr('src','themes/' + Highcharts.theme.currentSkin + '/images/bizser/summary/disk.png');
	var runPageListData = null;
	
    var reverse = function(value) {
        return value.split('').reverse().join('');
    };

    var defaults = {
        numberStep: function(now, tween) {
            var floored_number = Math.floor(now),
                target = $(tween.elem);

            target.text(floored_number);
        }
    };

    var handle = function( tween ) {
        var elem = tween.elem;
        if ( elem.nodeType && elem.parentNode ) {
            var handler = elem._animateNumberSetter;
            if (!handler) {
                handler = defaults.numberStep;
            }
            handler(tween.now, tween);
        }
    };

    if (!$.Tween || !$.Tween.propHooks) {
        $.fx.step.number = handle;
    } else {
        $.Tween.propHooks.number = {
            set: handle
        };
    }

    var extract_number_parts = function(separated_number, group_length) {
        var numbers = separated_number.split('').reverse(),
            number_parts = [],
            current_number_part,
            current_index,
            q;

        for(var i = 0, l = Math.ceil(separated_number.length / group_length); i < l; i++) {
            current_number_part = '';
            for(q = 0; q < group_length; q++) {
                current_index = i * group_length + q;
                if (current_index === separated_number.length) {
                    break;
                }

                current_number_part = current_number_part + numbers[current_index];
            }
            number_parts.push(current_number_part);
        }

        return number_parts;
    };

    var remove_precending_zeros = function(number_parts) {
        var last_index = number_parts.length - 1,
            last = reverse(number_parts[last_index]);

        number_parts[last_index] = reverse(parseInt(last, 10).toString());
        return number_parts;
    };

    $.animateNumber = {
        numberStepFactories: {
            append: function(suffix) {
                return function(now, tween) {
                    var floored_number = Math.floor(now),
                        target = $(tween.elem);

                    target.prop('number', now).text(floored_number + suffix);
                };
            },
            separator: function(separator, group_length, suffix) {
                separator = separator || ' ';
                group_length = group_length || 3;
                suffix = suffix || '';

                return function(now, tween) {
                    var floored_number = Math.floor(now),
                        separated_number = floored_number.toString(),
                        target = $(tween.elem);

                    if (separated_number.length > group_length) {
                        var number_parts = extract_number_parts(separated_number, group_length);

                        separated_number = remove_precending_zeros(number_parts).join(separator);
                        separated_number = reverse(separated_number);
                    }

                    target.prop('number', now).text(separated_number + suffix);
                };
            }
        }
    };

    $.fn.animateNumber = function() {
        var options = arguments[0],
            settings = $.extend({}, defaults, options),

            target = $(this),
            args = [settings];

        for(var i = 1, l = arguments.length; i < l; i++) {
            args.push(arguments[i]);
        }

        // needs of custom step function usage
        if (options.numberStep) {
            // assigns custom step functions
            var items = this.each(function(){
                this._animateNumberSetter = options.numberStep;
            });

            // cleanup of custom step functions after animation
            var generic_complete = settings.complete;
            settings.complete = function() {
                items.each(function(){
                    delete this._animateNumberSetter;
                });

                if ( generic_complete ) {
                    generic_complete.apply(this, arguments);
                }
            };
        }

        return target.animate.apply(target, args);
    };

}(jQuery));


$(function () {
    oc.resource.loadScript("resource/module/business-service/js/biz_edit_permissions.js",function(){
        oc.business.service.edit.permissions.init();
        var addBtn = oc.business.service.edit.permissions.checkAdd();
        if (addBtn) {
            $('#addBiz').removeClass('biz-hide');
        }
    });

    var healthyAuto;
    var healthyAutoScan;
   	var moduleAutoInterval;
    var requestNum = true;
    var isCreateRunGrid = false;

    //计算模块高宽
    $('#biz-main').layout();
    function doResize(){
        var leftH = $('#biz-left').height(),
            leftW = $('#biz-left').width(),
            centerW = $('#biz-center').width();
        $('#biz-left .hchats-box .biz-comm-content').css({
            width: leftW,
            height: leftH * 0.5 - 54
        });
        $('#healthy-box').css({
            width: centerW,
            height: centerW * 0.37 + 120
        });
        $('#operation-box').css({
            width: centerW,
            height: leftH - centerW * 0.37 -120
        });
    }
    doResize();

    //到列表
    $('#tableModel').on('click',function(){
        clearInterval(healthyAuto);
        clearInterval(healthyAutoScan);
   		clearInterval(moduleAutoInterval);
        oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/biz_table_model.html'));
    });

    //业务响应速度
    function responseSpeed() {
        var initResponse = new Highcharts.Chart({
            chart: {
                renderTo: 'biz-response',
                type: 'column',
                options3d: {
                    enabled: true,
                    alpha: 0,
                    beta: 0,
                    depth: 0,
                    viewDistance: 25
                }
            },
            title: {
                text: null
            },
            subtitle: {
                text: '单位（ms）',
                align:'left',
                style: {
                    color:Highcharts.theme.bizSubTitleColor,
                    fontSize:'12px'
                    
                }
            },
            yAxis: {
                title: {
                    text: null
                },
                min: 0,
                gridLineColor:Highcharts.theme.bizgridColor,
                gridLineWidth:1,
                labels:{
                	style:{
                		color:Highcharts.theme.bizlabelColor
                	}
                },
                tickPositioner: function () {
                    var positions = [];
                    if(this.dataMax<5){
                        positions = [0,1,2,3,4,5];
                    }else{
                        var increment = Math.ceil(this.dataMax / 5);
                        var c = (5*increment-this.dataMax)*2;
                        if(c<increment){
                            increment = Math.ceil(this.dataMax / 4);
                        }
                        for (var tick = 0; tick <= 5; tick ++) {
                            positions.push(tick*increment);
                        }
                    }
                    return positions;
                }
            },
            xAxis: {
                categories: [],
             //   gridLineColor:"#0f294d",
                labels: {
                	useHTML: true,
                	style:{
                		color:Highcharts.theme.bizlabelColor
                	},
                	maxStaggerLines: 1,
                    /*BUG #50869 业务管理：业务视图首页中业务响应速度表中，业务名称过长时，坐标轴中显示为空白 huangping 2017/1/17 start*/
                    rotation: -15,
                    /*BUG #50869 业务管理：业务视图首页中业务响应速度表中，业务名称过长时，坐标轴中显示为空白 huangping 2017/1/17 end*/
                	formatter:function(){
                		if(this.value.length>4){
                			if($('#biz-left').width() <= 330){
                				return '<label title=' + this.value + '>' + this.value.substring(0,3)+'...' + '</label>';
                			}else{
                				return '<label title=' + this.value + '>' + this.value.substring(0,4)+'...' + '</label>';
                			}
                		}
                		return '<label title=' + this.value + '>' + this.value + '</label>';
                	}
                }
            },
            plotOptions: {
                column: {
                    depth: 40
                }
            },
            legend: {
                enabled: false
            },
            series: [{
                name:'响应速度',
                color:Highcharts.theme.bizseriesColor,
                data: [0,0,0,0,0]
            }]
        });
        initResponse.showLoading();
        oc.util.ajax({
            url:oc.resource.getUrl('portal/business/service/getTopFiveResponseTime.htm'),
            timeout:null,
            success:function(data){
                var response = data.data;
                if (response != null){
                    var categories = [];
                    var series = [];
                    for (var i = 0; i < response.length; i++) {
                        categories.push(response[i].bizName);
                        series.push(Number(response[i].responseTime));
                    }
                    initResponse.xAxis[0].setCategories(categories);
                    initResponse.series[0].setData(series);
                }
                initResponse.hideLoading();
                initResponse.redraw();
            }
        });
    }
    responseSpeed();

    //业务告警次数
    var alarmTimeline = 7*24*60*60*1000;
	function openSetTime() {
		var setTimeStr = '<div id="biz-set-time" class="biz-set-time">';
		setTimeStr += '<div class="set-time-row">';
		setTimeStr += '<label><input type="radio" name="setTime" value="0" />最近24小时</label>';
		setTimeStr += '</div>';
		setTimeStr += '<div class="set-time-row">';
		setTimeStr += '<label><input type="radio" name="setTime" value="1" />最近7天</label>';
		setTimeStr += '</div>';
		setTimeStr += '<div class="set-time-row">';
		setTimeStr += '<label><input type="radio" name="setTime" value="2" />最近30天</label>';
		setTimeStr += '</div>';
		setTimeStr += '<div class="set-time-row">';
		setTimeStr += '<label><input type="radio" name="setTime" value="3" />最近1年</label>';
		setTimeStr += '</div>';
		setTimeStr += '<div class="set-time-row">';
		setTimeStr += '<label><input type="radio" name="setTime" class="custom-time" value="4" />最近';
		setTimeStr += '<input class="custom-time-ipt" disabled type="number" min="0" /> 天</label>';
		setTimeStr += '</div>';
		setTimeStr += '</div>';
		var _alarmdlg = $("<div></div>");
		var val = $("#alarm-time").attr('selectVal'),iptVal;
		_alarmdlg.dialog({
			title : '选择统计时间段',
			width : 400,
			height : 300,
			content : setTimeStr,
			buttons : [{
				text : "确定",
				iconCls : "fa fa-check-circle",
				handler : function() {
					val = _alarmdlg.find("input[name='setTime']:checked").val();
					var buttonText;
					switch (val) {
					case '0':
						alarmTimeline = 24 * 60 * 60 * 1000;
						buttonText = '最近24小时';
						break;
					case '1':
						alarmTimeline = 7 * 24 * 60 * 60 * 1000;
						buttonText = '最近7天';
						break;
					case '2':
						alarmTimeline = 30 * 24 * 60 * 60 * 1000;
						buttonText = '最近30天';
						break;
					case '3':
						alarmTimeline = 365 * 24 * 60 * 60 * 1000;
						buttonText = '最近1年';
						break;
					case '4':
						if ($('.custom-time-ipt').val() == ''
								|| $('.custom-time-ipt').val() == '0') {
							alert('请输入大于0的天数！');
							return;
						}
						iptVal = Number($('.custom-time-ipt').val());
						alarmTimeline = iptVal * 24 * 60 * 60 * 1000;
						buttonText = '最近' + iptVal + '天';
						break;
					}
					initAlarm();
					_alarmdlg.dialog("destroy");
					$("#alarm-time").val(buttonText);
					$("#alarm-time").attr('selectVal',val);
				}
			}, {
				text : "取消",
				iconCls : "fa fa-times-circle",
				handler : function() {
					_alarmdlg.dialog("destroy");
				}
			}]
		});
		_alarmdlg.find("input[name='setTime']").eq(val).attr('checked', true);
		if (val != 4) {
			$('#biz-set-time').find('.custom-time-ipt').attr('disabled', true);
			$('#biz-set-time').find('.custom-time-ipt').css('background', '#d7d7d7')
		} else {
			$('#biz-set-time').find('.custom-time-ipt').val(iptVal);
			$('#biz-set-time').find('.custom-time-ipt').attr('disabled', false);
			$('#biz-set-time').find('.custom-time-ipt').css('background', '#fff');
		}
		$("input[name='setTime']").on('click', function() {
			var $val = $(this).val();
			if ($val != 4) {
				$('#biz-set-time').find('.custom-time-ipt').attr('disabled', true);
					$('#biz-set-time').find('.custom-time-ipt').css('background', '#d7d7d7');
				} else {
					$('#biz-set-time').find('.custom-time-ipt').attr('disabled', false);
					$('#biz-set-time').find('.custom-time-ipt').css('background', '#fff');
				}
			});
	};
	$('#alarm-time').on('click', function(){
		openSetTime();
	});
    function initAlarm(){
    	var nowDate = new Date();
    	var endTime = nowDate.getTime();
    	var startTime = endTime - alarmTimeline;
        oc.util.ajax({
            url:oc.resource.getUrl('portal/business/service/getBizAlarmCount.htm'),
            timeout:null,
            data:{startTime:startTime.toString(),endTime:endTime.toString()},
            success:function(data){
                var alarm = data.data;
                var alarmData = [];
                var alarmText = [];
                for (var i = 0; i < alarm.length; i++) {
                    if (alarm[i].alarmCount != 0) {
                        alarmData.push([alarm[i].bizName + '(' + alarm[i].alarmCount + ')', alarm[i].alarmCount]);
                        alarmText.push(alarm[i].bizName + '(' + alarm[i].alarmCount + ')');
                    }
                }
                if (alarmData.length == 0){
                    var alarmNotice = '<div class="table-dataRemind">抱歉，暂无告警！</div>';
                    /*BUG #50871 业务管理：业务视图首页中告警次数在选择其他统计时间段时，图表和默认的时间段图表重合 huangping 2017/1/17 start*/
                    $('#alarm-num').html("");
                    /*BUG #50871 业务管理：业务视图首页中告警次数在选择其他统计时间段时，图表和默认的时间段图表重合 huangping 2017/1/17 end*/
                    $(alarmNotice).appendTo($('#alarm-num'));
                    return;
                }
                var initAlarm = $('#alarm-num').highcharts({
                    chart: {
                        type: 'pie',
                        options3d: {
                            enabled: true,
                            alpha: 45
                        }
                    },
                    title: {
                        text: null
                    },
                    subtitle: {
                        text: null
                    },
                    plotOptions: {
                        pie: {
                            innerSize: 100,
                            depth: 25,
                            dataLabels: {
                                
                            },
                        },
                        series: {
                            dataLabels: {
                            	useHTML: true,
                                enabled: true,
                         		formatter:function(){
                         			var tmp = this.point.name;
                         			if(tmp.indexOf('(')>=10){
                         				return '<div  style="color:'+Highcharts.theme.bizlabelColor+'"><label>' + tmp.substring(0,5) + '</label><br>' + '<label>' + tmp.substring(5,10) + '</label><br><label>' + tmp.substring(10,tmp.length) + '</label></div>';
                         			}else if(tmp.indexOf('(')>5){
                         				return '<div  style="color:'+Highcharts.theme.bizlabelColor+'"><label>' + tmp.substring(0,5) + '</label><br>' + '<label>' + tmp.substring(5,tmp.length) + '</label></div>';
                         			}
                         			return '<label style="color:'+Highcharts.theme.bizlabelColor+'">' + tmp + '</label>';
                                }
                            }
                        }
                    },
                    series: [{
                        name: '告警次数',
                        data: alarmData
                    }]
                });
            }
        });
    }
    initAlarm();

    //业务健康度
    function initHealthy(){
        var initStart = 0;
        var healthTotal;
        var autoTime = 10;
        var autoScanTime = 2;
        var autoAble = true;
        var autoScan = true;
        var autoScanNum = 0;
        
        function renderHealthy(heaStart){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getPageListForSummary.htm'),
                timeout:null,
                data:{startNum:heaStart,pageSize:5},
                startProgress: null,
                success:function(data){
                	//修改圆桌皮肤
                	$('#biz-disk').attr('src','themes/' + Highcharts.theme.currentSkin + '/images/bizser/summary/disk.png');
                	
                    var result = data.data.datas;
                    
                    if(data.data.totalRecord <= 5){
                    	autoAble = false;
                    }
                    
                    if (requestNum) {
                        requestNum = false;
                        healthTotal = data.data.totalRecord;
                        if (healthTotal == 0) {
                            var healthyNotice = '<div class="table-dataRemind">抱歉，暂无业务系统！</div>';
                            $('#biz-disk').before($(healthyNotice));
                            return;
                        } else if (healthTotal > 0){
                       var heaPage = '<a id="healthy-time" class="biz-fr  healthy-time fa fa-clock-o white" href="javascript:;"></a>';
                        	if(healthTotal > 5){
                        		heaPage += '<div class="cap-page">';
                        		heaPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                        		heaPage += '<a class="cap-down" href="javascript:;"></a>';
                        		heaPage +=  '</div>';
                        	}
							     
                            $('#healthy-box').find('.biz-comm-title').append($(heaPage));
                            $('#healthy-time').on('click', function(){
                                setCycle(healthTotal);
                            });
                            $('#healthy-box .cap-page a').on('click', function(){
                                clearInterval(healthyAutoScan);
                                var self = $(this);
                                if (self.hasClass('cap-page-able')) {
                                    return;
                                }
                                if (self.hasClass('cap-down')){
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderHealthy((++initStart) * 5);
                                    if ((initStart + 1) * 5 >= healthTotal) {
                                        self.addClass('cap-page-able');
                                    }
                                } else {
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderHealthy((--initStart) * 5);
                                    if (initStart == 0){
                                        self.addClass('cap-page-able');
                                    }
                                }
                                if (autoScan) {
                                    autoScanNum = 0;
                                    healthyAutoScan = setInterval(autoFlipScan, autoScanTime * 1000);
                                }
								
                            });
                            var tasks = oc.index.indexLayout.data("tasks");
                            if (autoAble) {
                                clearInterval(healthyAuto);
                                healthyAuto = setInterval(autoFlip, autoTime * 1000);
                                if(tasks && tasks.length > 0){
                                    oc.index.indexLayout.data("tasks").push(healthyAuto);
                                }else{
                                    tasks = new Array();
                                    tasks.push(healthyAuto);
                                    oc.index.indexLayout.data("tasks", tasks);
                                }
                            }
                            if (autoScan) {
                                clearInterval(healthyAutoScan);
                                healthyAutoScan = setInterval(autoFlipScan, autoScanTime * 1000);
                                if(tasks && tasks.length > 0){
                                    oc.index.indexLayout.data("tasks").push(healthyAutoScan);
                                }else{
                                    tasks = new Array();
                                    tasks.push(healthyAutoScan);
                                    oc.index.indexLayout.data("tasks", tasks);
                                }
                            }
                        }
                    }
                    var heaTag = $('#healthy-box').find('.biz-degree');
                    $('#healthy-box').find('.biz-grades').removeClass('biz-hide');
                    $('#healthy-box').find('.biz-scan').removeClass('biz-hide');
                    $('#healthy-box').find('.grades-num').prop('number', 0).animateNumber({number: result[0].health},'slow');
                    //$('.grades-num').text(result[0].health);
                    heaTag.addClass('biz-hide');
                    for(var i = 0; i < result.length; i++){
                        var heaStr = '';
                        switch (result[i].status){
                            case 0:
                                heaStr += '<span class="biz-state biz-greenstate"></span>';
                                break;
                            case 1:
                                heaStr += '<span class="biz-state biz-yellowstate"></span>';
                                break;
                            case 2:
                                heaStr += '<span class="biz-state biz-orangestate"></span>';
                                break;
                            case 3:
                                heaStr += '<span class="biz-state biz-redstate"></span>';
                                break;
                        }
                        heaStr += '<span class="biz-system-name" title="' + result[i].name + '">' + result[i].name + '</span>';
                        heaTag.eq(i).attr('data-grade', result[i].health);
                        heaTag.eq(i).attr('title',result[i].name);
                        heaTag.eq(i).attr('data-id', result[i].id);
                        heaTag.eq(i).html(heaStr);
                        heaTag.eq(i).removeClass('biz-hide');
                        $('#healthy-box').find('.biz-scan').css({
                            'transform':'rotate(75deg)',
                            '-ms-transform':'rotate(75deg)',
                            '-moz-transform':'rotate(75deg)',
                            '-webkit-transform':'rotate(75deg)',
                            '-o-transform':'rotate(75deg)'
                        });
                    }
                }
            });
        }
        renderHealthy(initStart);

        function autoFlipScan(){
            var scanTag = $('#healthy-box').find('.biz-degree');
            var scanTagAble = [];
            autoScanNum ++;
            for (var i = 0; i < scanTag.length; i++) {
                if (!scanTag.eq(i).hasClass('biz-hide')) {
                    scanTagAble.push(scanTag.eq(i));
                }
            }
            var temp, temp1;
            if (scanTagAble.length == 4) {
                temp = scanTagAble[2];
                scanTagAble[2] = scanTagAble[3];
                scanTagAble[3] = temp;
            } else if (scanTagAble.length == 5) {
                temp = scanTagAble[2];
                temp1 = scanTagAble[3];
                scanTagAble[2] = temp1;
                scanTagAble[3] = scanTagAble[4];
                scanTagAble[4] = temp;
            }
            if (autoScanNum <= scanTagAble.length) {
                var curTag = $(scanTagAble[autoScanNum]);
                var angle = curTag.attr('data-angle');
                var grade = curTag.attr('data-grade');
                var scanTag = $('#healthy-box').find('.biz-scan');
                scanTag.css({
                    'transform':'rotate(' + angle + 'deg)',
                    '-ms-transform':'rotate(' + angle + 'deg)',
                    '-moz-transform':'rotate(' + angle + 'deg)',
                    '-webkit-transform':'rotate(' + angle + 'deg)',
                    '-o-transform':'rotate(' + angle + 'deg)'
                });
                var curNum = parseInt($('#healthy-box').find('.grades-num').text());
                $('#healthy-box').find('.grades-num').prop('number', curNum).animateNumber({number: grade},'slow');
            }
            if (autoScanNum >= scanTagAble.length - 1) {
                if (autoAble) {
                    autoScanNum = 0;
                } else {
                    autoScanNum = -1;
                }
                if (scanTagAble.length < 5) {
                    autoScanNum = -1;
                }
            }
        }
        function autoFlip(){
            clearInterval(healthyAutoScan);
            var autoCount = parseInt(healthTotal / 5);
            if ((initStart + 1) == autoCount) {
                $('#healthy-box').find('.cap-up').removeClass('cap-page-able');
                $('#healthy-box').find('.cap-down').addClass('cap-page-able');
            } else if ((initStart + 1) > autoCount){
                $('#healthy-box').find('.cap-up').addClass('cap-page-able');
                $('#healthy-box').find('.cap-down').removeClass('cap-page-able');
                initStart = -1;
            } else {
                $('#healthy-box').find('.cap-up').removeClass('cap-page-able');
            }
            renderHealthy(++initStart * 5);
            if (autoScan) {
                autoScanNum = 0;
                healthyAutoScan = setInterval(autoFlipScan, autoScanTime * 1000);
            }
        }
        function setCycle(healthTotal){
            var autoStr = '<div class="biz-scan-cycle">';
            autoStr += '<input type="checkbox" name="" id="custom-scan" class="custom-cycle" value="2" />自动扫描';
            autoStr += '<span>周期：</span>';
            autoStr += '<input id="custom-scan-ipt" class="easyui-numberspinner" style="width:100px" min="1"  max="100000" value="' + autoScanTime + '"/>&nbsp;秒';
            autoStr += '</div>';
            if(healthTotal > 5){
            	autoStr += '<div id="biz-set-cycle" class="biz-set-cycle">';
            	autoStr += '<input type="checkbox" name="" id="custom-cycle" class="custom-cycle" value="10" />自动轮播';
            	autoStr += '<span>周期：</span>';
            	autoStr += '<input id="custom-cycle-ipt" class="easyui-numberspinner" style="width:100px" min="1"  max="100000" value="' + autoTime + '"/>&nbsp;秒';
            	autoStr += '</div>';
            }
            
            var _dlg = $("<div></div>");
            _dlg.dialog({
                title: '播放设置',
                width: 350,
                height: 200,
                content: autoStr,
                buttons : [{
                    text:"确定",
                    iconCls:"fa fa-check-circle",
                    handler:function(){
                    	var value = $('#custom-cycle-ipt').val();
                        var valueScan = $('#custom-scan-ipt').val();
                        autoAble = $('#custom-cycle').is(':checked');
                        autoScan = $('#custom-scan').is(':checked');
                        var tasks = oc.index.indexLayout.data("tasks");
                        if (autoAble) {
                            if ((/^(\+|-)?\d+$/.test(value))&&value>0) {
                                clearInterval(healthyAuto);
                                autoTime = parseInt(value);
                                healthyAuto = setInterval(autoFlip, autoTime * 1000);
                                if(tasks && tasks.length > 0){
                                    oc.index.indexLayout.data("tasks").push(healthyAuto);
                                }else{
                                    tasks = new Array();
                                    tasks.push(healthyAuto);
                                    oc.index.indexLayout.data("tasks", tasks);
                                }
                            } else {
                                alert("请输入正整数！");
                            }
                        } else {
                            clearInterval(healthyAuto);
                        }
                        if (autoScan) {
                            if ((/^(\+|-)?\d+$/.test(valueScan))&&valueScan>0) {
                                clearInterval(healthyAutoScan);
                                autoScanTime = parseInt(valueScan);
                                healthyAutoScan = setInterval(autoFlipScan, autoScanTime * 1000);
                                if(tasks && tasks.length > 0){
                                    oc.index.indexLayout.data("tasks").push(healthyAuto);
                                }else{
                                    tasks = new Array();
                                    tasks.push(healthyAuto);
                                    oc.index.indexLayout.data("tasks", tasks);
                                }
                            } else {
                                alert("请输入正整数！");
                            }
                        } else {
                            autoScanNum = 0;
                            var grade = $('#healthy-box').find('.biz-degree').eq(0).attr('data-grade');
                            $('#healthy-box').find('.biz-scan').css({
                                'transform':'rotate(75deg)',
                                '-ms-transform':'rotate(75deg)',
                                '-moz-transform':'rotate(75deg)',
                                '-webkit-transform':'rotate(75deg)',
                                '-o-transform':'rotate(75deg)'
                            });
                            var curNum = parseInt($('#healthy-box').find('.grades-num').text());
                            $('#healthy-box').find('.grades-num').prop('number', curNum).animateNumber({number: grade},'slow');
                            clearInterval(healthyAutoScan);
                        }
                        _dlg.dialog("close");
                    }
                },{
                    text:"取消",
                    iconCls:"fa fa-times-circle",
                    handler:function(){
                        _dlg.dialog("close");
                    }
                }]
            });
            $('#custom-cycle').attr('checked', autoAble);
            $('#custom-scan').attr('checked', autoScan);
        }
        $('#healthy-box').find('.biz-degree').on('click', function(){
            var self = $(this);
            var angle = self.attr('data-angle');
            var grade = self.attr('data-grade');
            var scanTag = $('.biz-scan');
            scanTag.css({
                'transform':'rotate(' + angle + 'deg)',
                '-ms-transform':'rotate(' + angle + 'deg)',
                '-moz-transform':'rotate(' + angle + 'deg)',
                '-webkit-transform':'rotate(' + angle + 'deg)',
                '-o-transform':'rotate(' + angle + 'deg)'
            });
            //$('.grades-num').text(grade);
            var curNum = parseInt($('#healthy-box').find('.grades-num').text());
            $('#healthy-box').find('.grades-num').prop('number', curNum).animateNumber({number: grade},'slow');
        });
        //健康度双击
        $('#healthy-box').find('.biz-degree').on('dblclick', function(){
            clearInterval(healthyAuto);
       		clearInterval(moduleAutoInterval);
            var bizId = $(this).attr('data-id');
            oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/buz/business.html'), function(){
                oc.resource.loadScript('resource/module/business-service/buz/business.js',
                    function(){
                		oc.business.service.edit.permissions.setLastEnter(0);
                        new businessTopo({
                            bizId:bizId
                        });
                    });
            });
        });
    }
    initHealthy();

    //业务运行情况
    var RunTimeline = 7*24*60*60*1000;
    function initDatagrid(){
        var nowDate = new Date();
        var endTime = nowDate.getTime();
        var startTime = endTime - RunTimeline;
        var val = $("#operation-time").attr('selectVal');
        var iptVal;
        if(isCreateRunGrid){
        	reloadgrid({startTime: startTime.toString(),endTime:endTime.toString()});
        	return;
        }
        
        var bizOperation = oc.ui.datagrid({
        	selector: $('#biz-operation'),
        	//fitColumns:true,
        	pagination: false,
        	remoteSort: false,
        	octoolbar: {},
        	columns:[[
        	          {
        	        	  field:'bizName',
        	        	  title:'<span class="biz-name">业务名称</span>',
        	        	  width:'16%',
        	        	  formatter: function(value,row,index){
        	        		  //return that.formatCheckbox(value,row,index,'查看',that.authSelect);
        	        		  return '<a class="biz-operation-name" title="' + value + '" rowIndex="' + index + '" href="javascript:;">' + value + '</a>'
        	        	  }
        	          },
        	          {field:'healthScore',title:'健康度',width:'14%',sortable:true,
        	        	  formatter: function(value,row,index){
        	        		  return value + '分';
        	        	  }},
        	        	  {field:'availableRate',title:'可用率',sortable:true,width:'14%'},
        	        	  {field:'mttr',title:'MTTR <a class="operation-help fa  fa-question-circle light_blue" title="Mean Time To Repair"></a>',sortable:true,width:'14%'},
        	        	  {field:'mtbf',title:'MTBF <a class="operation-help fa  fa-question-circle light_blue" title="Mean Time To Failure"></a>' ,sortable:true,width:'14%'},
        	        	  {field:'downTime',title:'宕机时长',sortable:true,width:'14%'},
        	        	  {field:'outageTimes',title:'宕机次数',sortable:true,width:'14%'}
        	        	  ]],
        	        	  onLoadSuccess: function(data){
        	        		
/*        	        		  var toolbar=$('#biz-operation').find("div.datagrid-toolbar");
        	        		console.info(toolbar);
        	        		  if(toolbar.length>1){
        	        			  $.each(toolbar,function(i){
        	        				  if(i>0){
        	        					  $(this).remove()
        	        				  }
        	        			  });
        	        		  }*/
        	        		  var panel = $('#biz-operation').datagrid("getPanel");
        	        		  panel.find(".biz-operation-name").on('click',function(e){
        	        			 //    clearInterval(healthyAuto);
        	        			  // 		clearInterval(moduleAutoInterval);
        	        			  var rowIndex = $(this).attr('rowIndex');
        	        			  var row = $('#biz-operation').datagrid("getRows")[rowIndex];
        	        			  oc.resource.loadScript("resource/module/business-service/biz-detail-info/js/business_detail_info.js", function(){
        	        				  oc.module.biz.detailinfo.open({bizId: row.bizId,bizName:row.bizName});
        	        			  });
        	        		  });
        	        	  },
        	        	  onSortColumn : function(sort,order){
        	        		  var sortData = $.extend(true,[],runPageListData);
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
        	        		  $('#biz-operation').datagrid('loadData',{"code":200,"data":{"total":0,"rows":sortData}});
        	        	  }
        });
        reloadgrid({startTime: startTime.toString(),endTime:endTime.toString()});
        
        function reloadgrid(queryParams) {
            //查询参数直接添加在queryParams中
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getPageListRunInfo.htm'),
                timeout:null,
                data:queryParams,
                success:function(data){
                	if(data.code == 200){
                		runPageListData = data.data;
                		$('#biz-operation').datagrid('loadData',{"code":200,"data":{"total":0,"rows":data.data}});
                	}else{
                		alert('获取运行情况失败！');
                	}
                		
                }
            });
        }
        
        //将不同单位的时间值转为秒
        function getSecondValue(value){
        	if(value.indexOf('年') != -1){
        		return parseInt(value.replace('(年)','')) * 24 * 60 * 60 * 365;
        	}else if(value.indexOf('天') != -1){
        		return parseInt(value.replace('(天)','')) * 24 * 60 * 60;
        	}else if(value.indexOf('小时') != -1){
        		return parseInt(value.replace('(小时)','')) * 60 * 60;
        	}else if(value.indexOf('分钟') != -1){
        		return parseInt(value.replace('(分钟)','')) * 60;
        	}else{
        		return parseInt(value.replace('(秒)',''));
        	}
        }
        
        function openSetTime(){
            var setTimeStr = '<div id="biz-set-time" class="biz-set-time">';
            setTimeStr += '<div class="set-time-row">';
            setTimeStr += '<label><input type="radio" name="setTime" value="0" />最近24小时</label>';
            setTimeStr += '</div>';
            setTimeStr += '<div class="set-time-row">';
            setTimeStr += '<label><input type="radio" name="setTime" value="1" />最近7天</label>';
            setTimeStr += '</div>';
            setTimeStr += '<div class="set-time-row">';
            setTimeStr += '<label><input type="radio" name="setTime" value="2" />最近30天</label>';
            setTimeStr += '</div>';
            setTimeStr += '<div class="set-time-row">';
            setTimeStr += '<label><input type="radio" name="setTime" value="3" />最近1年</label>';
            setTimeStr += '</div>';
            setTimeStr += '<div class="set-time-row">';
            setTimeStr += '<label><input type="radio" name="setTime" class="custom-time" value="4" />最近';
            setTimeStr += '<input class="custom-time-ipt" disabled type="number" min="0" /> 天</label>';
            setTimeStr += '</div>';
            setTimeStr += '</div>';
            var _dlg = $("<div></div>");
            _dlg.dialog({
                title: '选择统计时间段',
                width: 400,
                height: 300,
                content: setTimeStr,
                buttons : [{
                    text:"确定",
                    iconCls:"fa fa-check-circle",
                    handler:function(){
                        var mydate = new Date();
                        var endTime = mydate.getTime();
                        var startTime;
                        val = _dlg.find("input[name='setTime']:checked").val();
                        var buttonText; 
                        switch (val) {
                            case '0':
                            	RunTimeline = 24*60*60*1000;
                                buttonText = '最近24小时';
                                break;
                            case '1':
                            	RunTimeline = 7*24*60*60*1000;
                                buttonText = '最近7天';
                                break;
                            case '2':
                            	RunTimeline = 30*24*60*60*1000;
                                buttonText = '最近30天';
                                break;
                            case '3':
                            	RunTimeline = 365*24*60*60*1000;
                            	buttonText = '最近1年';
                            	break;
                            case '4':
                                if ($('.custom-time-ipt').val() == '' || $('.custom-time-ipt').val() == '0'){
                                    alert('请输入大于0的天数！');
                                    return;
                                }
                                iptVal = Number($('.custom-time-ipt').val());
                                RunTimeline = iptVal*24*60*60*1000;
                                buttonText = '最近'+ iptVal + '天';
                                break;
                        }
                        startTime = endTime - RunTimeline;
                        var queryParams = {startTime:startTime.toString(),endTime:endTime.toString()};
                        reloadgrid(queryParams);
                        _dlg.dialog("close");
                        $("input[id=operation-time]").val(buttonText);
                        $("input[id=operation-time]").attr('selectVal',val);
                    }
                },{
                    text:"取消",
                    iconCls:"fa fa-times-circle",
                    handler:function(){
                        _dlg.dialog("close");
                    }
                }]
            });
            _dlg.find("input[name='setTime']").eq(val).attr('checked', true);
            if (val != 4) {
                $('#biz-set-time').find('.custom-time-ipt').attr('disabled', true);
                $('#biz-set-time').find('.custom-time-ipt').css('background','#d7d7d7')
            } else {
                $('#biz-set-time').find('.custom-time-ipt').val(iptVal);
                $('#biz-set-time').find('.custom-time-ipt').attr('disabled', false);
                $('#biz-set-time').find('.custom-time-ipt').css('background','#fff');
            }
            $("input[name='setTime']").on('click', function(){
                var $val = $(this).val();
                if ($val != 4) {
                    $('#biz-set-time').find('.custom-time-ipt').attr('disabled', true);
                    $('#biz-set-time').find('.custom-time-ipt').css('background','#d7d7d7');
                } else {
                    $('#biz-set-time').find('.custom-time-ipt').attr('disabled', false);
                    $('#biz-set-time').find('.custom-time-ipt').css('background','#fff');
                }
            });
        }
        $('#operation-time').on('click', function(){
            openSetTime();
        });
        
        isCreateRunGrid = true;
    }
    initDatagrid();

    //存储容量
    function initStorage(){
    	//tooltip
    	$('.tooltip-style').tooltip({
    		 track: true
    	});
    	
        $('#tabs li').on('click', function(){
        	
            var $this = $(this);
            var capIndex = $this.index();
            $this.siblings('li').removeClass('active');
            $this.addClass('active');
            $('#cap-tabs-wrap').find('.cap-tag').addClass('biz-hide');
            $('#cap-tabs-wrap').find('.cap-tag').eq(capIndex).removeClass('biz-hide');
            
            if(capIndex == 0){
                renderCapacity(storeStart, count);
            }else if(capIndex == 1){
            	renderDB(dbStart, count);
            }else if(capIndex == 2){
            	renderBandwidth(bandwidthStart, count);
            }else if(capIndex == 3){
            	renderCalculate(calStart, count);
            }
            
        });
        var capH = $('#bix-right').height();
        var count = parseInt(capH / 124);
        var padding = capH / (count + 1) - 84;
        var storeStart = 0,
            dbStart = 0,
            bandwidthStart = 0,
            calStart = 0,
            storageFirst = true,
            dbFirst = true,
            bandwidthFirst = true,
            calFirst = true;
        //存储容量
        function renderCapacity(start, pagesize){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getStoreCapacityInfo.htm'),
                timeout:null,
                data:{startNum:start,pageSize:pagesize},
                success:function(data){
                    var result = data.data.datas;
                    $('#storage').empty();
                    if (storageFirst) {
                        storageFirst = false;
                        var storageTotal = data.data.totalRecord;
                        if (storageTotal == 0){
                            var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                            $('#storage').append($(capNotice));
                            return;
                        }
                        if (storageTotal > count) {
                            var capPage = '<div id="storagePage" class="cap-page">';
                            capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            capPage += '<a class="cap-down" href="javascript:;"></a>';
                            capPage +=  '</div>';
                            $('#storage').after(capPage);
                            //分页
                            $('#storagePage a').on('click', function(){
                                var self = $(this);
                                if (self.hasClass('cap-page-able')) {
                                    return;
                                }
                                if (self.hasClass('cap-down')){
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderCapacity((++storeStart) * count, count);
                                    if ((storeStart + 1) * count >= storageTotal) {
                                        self.addClass('cap-page-able');
                                    }
                                } else {
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderCapacity((--storeStart) * count, count);
                                    if (storeStart == 0){
                                        self.addClass('cap-page-able');
                                    }
                                }
                            });
                        }
                    }

                    for (var i = 0; i < result.length; i++) {
                    	
                    	var capacity = '';
                        var bizName_tmp = result[i].bizName.length>8 ? result[i].bizName.substring(0,8)+'...' : result[i].bizName;
                        
                        capacity += '<div class="cap-row biz-clearfix" style="margin:' + padding + 'px 0">';
                        capacity += '<div class="biz-fl storageCapacityClass" style="width: 50%;height: 84px;padding: 7px 0;position: relative;" id="storage_' + result[i].bizId + '">';
                        capacity += '</div>';
                        capacity += '<div class="biz-fr cap-detail">';
                        capacity += '<h4 class="tooltip-style" title="'+result[i].bizName+'">' + bizName_tmp + '</h4>';
                        capacity += '<div class="cap-total">总容量' + result[i].totalStore + '</div>';
                        capacity += '<div class="cap-used">已使用' + result[i].useStore + '</div>';
                        capacity += '</div>';
                        capacity += '</div>';
                        
                        $('#storage').append(capacity);
                        initPieChart($('#storage_' + result[i].bizId),result[i].useRate);
                        
                    }
                }
            });
        }
        function renderDB(start, pagesize){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getDatabaseCapacityInfo.htm'),
                timeout:null,
                data:{startNum:start,pageSize:pagesize},
                success:function(data){
                    var result = data.data.datas;
                    $('#database').empty();
                    if (dbFirst) {
                        dbFirst = false;
                        var dbTotal = data.data.totalRecord;
                        if (dbTotal == 0){
                            var capNotice = '<div class="table-dataRemind">抱歉，暂无数据库容量！</div>';
                            $('#database').append($(capNotice));
                            return;
                        }
                        if (dbTotal > count) {
                            var capPage = '<div id="dbPage" class="cap-page">';
                            capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            capPage += '<a class="cap-down" href="javascript:;"></a>';
                            capPage +=  '</div>';
                            $('#database').after(capPage);
                            //分页
                            $('#dbPage a').on('click', function(){
                                var self = $(this);
                                if (self.hasClass('cap-page-able')) {
                                    return;
                                }
                                if (self.hasClass('cap-down')){
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderDB((++dbStart) * count, count);
                                    if ((dbStart + 1) * count >= dbTotal) {
                                        self.addClass('cap-page-able');
                                    }
                                } else {
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderDB((--dbStart) * count, count);
                                    if (dbStart == 0){
                                        self.addClass('cap-page-able');
                                    }
                                }
                            });
                        }
                    }
                    for (var i = 0; i < result.length; i++) {
                    	
                    	var capacity = '';
                        var bizName_tmp = result[i].bizName.length>8 ? result[i].bizName.substring(0,8)+'...' : result[i].bizName;
                        
                        capacity += '<div class="cap-row biz-clearfix" style="margin:' + padding + 'px 0">';
                        capacity += '<div class="biz-fl dbCapacityClass" style="width: 50%;height: 84px;padding: 7px 0;position: relative;" id="db_' + result[i].bizId + '">';
                        capacity += '</div>';
                        capacity += '<div class="biz-fr cap-detail">';
                        capacity += '<h4 class="tooltip-style" title="'+result[i].bizName+'">' + bizName_tmp + '</h4>';
                        capacity += '<div class="cap-total">总容量' + result[i].totalTableSpace + '</div>';
                        capacity += '<div class="cap-used">已使用' + result[i].useTableSpace+ '</div>';
                        capacity += '</div>';
                        capacity += '</div>';
                        
                        $('#database').append(capacity);
                        initPieChart($('#db_' + result[i].bizId),result[i].useRate);
                        
                    }
                }
            });
        }
        function renderBandwidth(start, pagesize){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getBandwidthCapacityInfo.htm'),
                timeout:null,
                data:{startNum:start,pageSize:pagesize},
                success:function(data){
                    var result = data.data.datas;
                    $('#bandwidth').empty();
                    if (bandwidthFirst) {
                        bandwidthFirst = false;
                        var bandwidthTotal = data.data.totalRecord;
                        if (bandwidthTotal == 0){
                            var capNotice = '<div class="table-dataRemind">抱歉，暂无带宽容量！</div>';
                            $('#database').append($(capNotice));
                            return;
                        }
                        if (bandwidthTotal > count) {
                            var capPage = '<div id="bandwidthPage" class="cap-page">';
                            capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            capPage += '<a class="cap-down" href="javascript:;"></a>';
                            capPage +=  '</div>';
                            $('#bandwidth').after(capPage);
                            //分页
                            $('#bandwidthPage a').on('click', function(){
                                var self = $(this);
                                if (self.hasClass('cap-page-able')) {
                                    return;
                                }
                                if (self.hasClass('cap-down')){
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderBandwidth((++bandwidthStart) * count, count);
                                    if ((bandwidthStart + 1) * count >= bandwidthTotal) {
                                        self.addClass('cap-page-able');
                                    }
                                } else {
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderBandwidth((--bandwidthStart) * count, count);
                                    if (bandwidthStart == 0){
                                        self.addClass('cap-page-able');
                                    }
                                }
                            });
                        }
                    }
                    for (var i = 0; i < result.length; i++) {
                    	
                    	var capacity = '';
                        var bizName_tmp = result[i].bizName.length>8 ? result[i].bizName.substring(0,8)+'...' : result[i].bizName;
                        
                        capacity += '<div class="cap-row biz-clearfix" style="margin:' + padding + 'px 0">';
                        capacity += '<div class="biz-fl bandCapacityClass" style="width: 50%;height: 84px;padding: 7px 0;position: relative;" id="band_' + result[i].bizId + '">';
                        capacity += '</div>';
                        capacity += '<div class="biz-fr cap-detail">';
                        capacity += '<h4 class="tooltip-style" title="'+result[i].bizName+'">' + bizName_tmp + '</h4>';
                        capacity += '<div class="cap-total">总容量' + result[i].totalFlow + '</div>';
                        capacity += '<div class="cap-used">已使用' + result[i].useFlow+ '</div>';
                        capacity += '</div>';
                        capacity += '</div>';
                        
                        $('#bandwidth').append(capacity);
                        initPieChart($('#band_' + result[i].bizId),result[i].useRate);
                    }
                }
            });
        }
        function renderCalculate(start, pagesize){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getCalculateCapacityInfo.htm'),
                timeout:null,
                data:{startNum:start,pageSize:pagesize},
                success:function(data){
                    var result = data.data.datas;
                    $('#calculate').empty();
                    if (calFirst) {
                        calFirst = false;
                        var calTotal = data.data.totalRecord;
                        if (calTotal == 0){
                            var capNotice = '<div class="table-dataRemind">抱歉，暂无计算容量！</div>';
                            $('#calculate').append($(capNotice));
                            return;
                        }
                        if (calTotal > count) {
                            var capPage = '<div id="calPage" class="cap-page">';
                            capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            capPage += '<a class="cap-down" href="javascript:;"></a>';
                            capPage +=  '</div>';
                            $('#calculate').after(capPage);
                            //分页
                            $('#calPage a').on('click', function(){
                                var self = $(this);
                                if (self.hasClass('cap-page-able')) {
                                    return;
                                }
                                if (self.hasClass('cap-down')){
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderCalculate((++calStart) * count, count);
                                    if ((calStart + 1) * count >= calTotal) {
                                        self.addClass('cap-page-able');
                                    }
                                } else {
                                    self.siblings('a').removeClass('cap-page-able');
                                    renderCalculate((--calStart) * count, count);
                                    if (calStart == 0){
                                        self.addClass('cap-page-able');
                                    }
                                }
                            });
                        }
                    }
                    for (var i = 0; i < result.length; i++) {
                    	
                    	var capacity = '';
                        var bizName_tmp = result[i].bizName.length>8 ? result[i].bizName.substring(0,8)+'...' : result[i].bizName;
                        
                        capacity += '<div class="cap-row biz-clearfix" style="margin:' + padding + 'px 0">';
                        capacity += '<div class="biz-fl calculateCapacityClass" style="width: 50%;height: 84px;padding: 7px 0;position: relative;" id="calculate_' + result[i].bizId + '">';
                        capacity += '</div>';
                        capacity += '<div class="biz-fr cap-detail">';
                        capacity += '<h4 class="tooltip-style" title="'+result[i].bizName+'">' + bizName_tmp + '</h4>';
                        //capacity += '<div class="cap-total">共容量' + result[i].totalFlow + '</div>';
                        //capacity += '<div class="cap-used">已使用' + result[i].useFlow+ '</div>';
                        capacity += '</div>';
                        capacity += '</div>';
                        
                        $('#calculate').append(capacity);
                        initPieChart($('#calculate_' + result[i].bizId),result[i].cpuRate);
                        
                    }
                }
            });
        }
        function initPieChart(div,rate){
			
        	var rateColor;
            if (rate >= 80){
                rateColor = '#F96E03';
            } else if (rate < 80 && rate >= 70) {
                rateColor = '#FAEA11';
            } else {
                rateColor = '#00C803';
            }
			function getData() {
			    return [{
			        value: rate,
			        itemStyle: {
			            normal: {
			                color: rateColor
			            }
			        }
			    }, {
			        value: 100 - rate,
			        itemStyle: {
			            normal: {
			                color: Highcharts.theme.pieitemColor
			            }
			        }
			    }];
			}
			var option = {
			    title: {
			        text: rate + "%",
			        subtext: '',
			        x: 'center',
			        y: '35%',
			        textStyle: {
			            color: Highcharts.theme.pieaxisLabelColor,
			            fontSize: 15
			        }
			    },
			    animation: false,
			    series: [{
			        name: 'main',
			        type: 'pie',
			        radius: ['90%', '98%'],
			        label: {
			            normal: {
			                show: false
			            }
			        },
			        data: getData()
			    }]
			};

			var myChart = echarts.init(div.get(0));
			myChart.setOption(option);
		
		}
        renderCapacity(storeStart, count);

    }
    initStorage();


    //获取后台配置的刷新时间  ，分钟为单位 
   	function getRefreshTime(){
   	var time=0;
    	 oc.util.ajax({
   		url:oc.resource.getUrl("portal/business/service/getMuduleTime.htm"),
   		async:false,
   		success:function(data){
   			if(data.data){
   				time=data.data;
   				
   			}
   		}
   		
   });
    	return time ;
     }
   	var refreshTime=getRefreshTime();
   	//设置定时器，定时刷新业务大屏的各种模块数据
//   	console.info(moduleAutoInterval);
   	if(moduleAutoInterval != undefined){
   		clearInterval(moduleAutoInterval);
   	}
   	 moduleAutoInterval = setInterval(function(){
//   		console.info("refresh......");
   		  	responseSpeed();//业务响应速度
   		    initAlarm();//业务告警数据
   		    initHealthy();//业务健康度
   		    initDatagrid();//业务运行情况
   		    initStorage();//存储数据
   	},refreshTime*60000);
   	var tasks = oc.index.indexLayout.data("tasks");
   	if(tasks && tasks.length > 0){
   		oc.index.indexLayout.data("tasks").push(moduleAutoInterval);
   	}else{
   		tasks = new Array();
   		tasks.push(moduleAutoInterval);
   		oc.index.indexLayout.data("tasks", tasks);
   	}

       $(window).resize(function (){
           setTimeout(function(){
               doResize();
               responseSpeed();
               initStorage();
               initAlarm();

               $('#biz-operation').datagrid('resize');
           }, 100);
       });
   });