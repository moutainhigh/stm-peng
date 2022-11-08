/**
 * Created by niexiaoqin on 2016/8/29.
 * Modify by tandl on 2017/05/11
 */

//数字滚动变化效果
(function($) {
	
	if(!oc.home){
		oc.home = {};
	}
	
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
	
	//命名空间
	oc.ns('oc.module.home.biz.summary');
	
    oc.resource.loadScript("resource/module/business-service/js/biz_edit_permissions.js",function(){
        var run = setInterval(function(){
            try{
                oc.business.service.edit.permissions.init();
                var addBtn = oc.business.service.edit.permissions.checkAdd();
                if (addBtn) {
                    $('#addBiz').removeClass('biz-hide');
                }
                clearInterval(run);
            }catch(e){}
        },100);
    });

    var $bizFllMain = $('#biz-full-main').attr('id',oc.util.generateId());

	var initResponseChart = null;
	var initAlarmChart = null;
	var initDatagrid = null;
    
    //计算模块高宽
    $bizFllMain.layout();
    doFullResize();
    responseFullSpeed();
    initFullAlarm();
    initFullHealthy();
    
    var capH = $bizFllMain.find('.bix-full-right').height();
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
    
    var curShowStorage = 0;
    
    initFullStorage();
    
    initFullDatagrid();

    function doFullResize(){
        var leftH = $bizFllMain.find('.biz-full-left').height(),
            leftW = $bizFllMain.find('.biz-full-left').width(),
            centerW = $bizFllMain.find('.biz-full-center').width();
        $bizFllMain.find('#biz-left .hchats-box .biz-comm-content').css({
            width: leftW,
            height: leftH * 0.5 - 54
        });
        $bizFllMain.find('.full-healthy-box').css({
            width: centerW,
            height: centerW * 0.37 + 120
        });
        $bizFllMain.find('.full-operation-box').css({
            width: centerW,
            height: leftH - centerW * 0.37 -120
        });
    }

    //业务响应速度
    function responseFullSpeed() {

        if(!initResponseChart){
        	
        	var bfrID = oc.util.generateId();
        	$('#biz-full-response').attr('id',bfrID);
        	
        	initResponseChart = new Highcharts.Chart({
        		chart: {
        			renderTo: bfrID,
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
        				color: Highcharts.theme.bizSubTitleColor,
        				fontSize:'12px'
        			}
        		},
        		yAxis: {
        			title: {
        				text: null
        			},
        			min: 0,
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
        			},
        			labels: {
        				style: {
        					color: Highcharts.theme.bizlabelColor
        				}
        			},
        			gridLineColor: Highcharts.theme.bizgridColor,
        			gridLineWidth: 1
        		},
        		xAxis: {
        			categories: [],
        			labels: {
        				maxStaggerLines: 1,
        				formatter:function(){
        					if(this.value.length>4){
        						if($('#biz-left').width() <= 330){
        							return '<label title=' + this.value + '>' + this.value.substring(0,3)+'...' + '</label>';
        						}else{
        							return '<label title=' + this.value + '>' + this.value.substring(0,4)+'...' + '</label>';
        						}
        					}
        					return '<label title=' + this.value + '>' + this.value + '</label>';
        				},
        				style: {
        					color: Highcharts.theme.bizlabelColor
        				}
        			},
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
        }
        
        initResponseChart.showLoading();
        oc.util.ajax({
            url:oc.resource.getUrl('portal/business/service/getTopFiveResponseTime.htm'),
            success:function(data){
                var response = data.data;
                if (response != null){
                    var categories = [];
                    var series = [];
                    for (var i = 0; i < response.length; i++) {
                        categories.push(response[i].bizName);
                        series.push(Number(response[i].responseTime));
                    }
                    initResponseChart.xAxis[0].setCategories(categories);
                    initResponseChart.series[0].setData(series);
                }
                initResponseChart.hideLoading();
                initResponseChart.redraw();
            }
        });
    }

    //业务告警次数
    function initFullAlarm(){
    	
    	if(!initAlarmChart){
    		
    	      	var fanID = oc.util.generateId();
    	        var $fan = $('#full-alarm-num').attr('id',fanID);
    	        
                Highcharts.setOptions({
                    colors: ['#61c4f8', '#333557', '#6fce6b', '#fe945d', '#6371fe', '#ec48b4', '#ebd256', '#1c907c', '#ff4663']
                });
                
                initAlarmChart = new Highcharts.Chart({
                    chart: {
                    	renderTo: fanID,
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
                            innerSize: 50,
                            depth: 25
                        },
                        series: {
                            dataLabels: {
                            	useHTML: true,
                                enabled: true,
                             /*   connectorColor: '#ffffff',
                                color: '#ffffff',*/
                                formatter:function(){
                         			var tmp = this.point.name;
                         			if(tmp.indexOf('(')>=10){
                         				return '<div><label>' + tmp.substring(0,5) + '</label><br>' + '<label>' + tmp.substring(5,10) + '</label><br><label>' + tmp.substring(10,tmp.length) + '</label></div>';
                         			}else if(tmp.indexOf('(')>5){
                         				return '<div><label>' + tmp.substring(0,5) + '</label><br>' + '<label>' + tmp.substring(5,tmp.length) + '</label></div>';
                         			}
                         			return '<label>' + tmp + '</label>';
                                }
                            }
                        }
                    },
                    series: [{
                        name: '告警次数',
                        data: []
                    }]
                });
    		
    	}
    	
        oc.util.ajax({
            url:oc.resource.getUrl('portal/business/service/getBizAlarmCount.htm'),
            success:function(data){
                var alarm = data.data;
                var alarmData = [];
                for (var i = 0; i < alarm.length; i++) {
                    if (alarm[i].alarmCount != 0) {
                        alarmData.push([alarm[i].bizName + '(' + alarm[i].alarmCount + ')', alarm[i].alarmCount]);
                    }
                }
                if (alarmData.length == 0){
                    var alarmNotice = '<div class="table-dataRemind">抱歉，暂无告警！</div>';
                    if($fan == null){
                        return;
                    }
                    $fan.html('');
                    $(alarmNotice).appendTo($fan);
                    return;
                }
                initAlarmChart.series[0].setData(alarmData);
            }
        });
  
    }

    //业务健康度
    function initFullHealthy(){
        var initStart = 0;
        var requestNum = true;
        var healthTotal;
        var autoTime = 10;
        var autoScanTime = 2;
        var autoAble = true;
        var autoScan = true;
        var autoScanNum = 0;
        function renderHealthy(heaStart){
            var $bfd =  $('#biz-full-disk').attr('id',oc.util.generateId());
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getPageListForSummary.htm'),
                data:{startNum:heaStart,pageSize:5},
                startProgress: null,
                success:function(data){
                    var result = data.data.datas;
                    if (requestNum) {
                        requestNum = false;
                        healthTotal = data.data.totalRecord;
                        if (healthTotal == 0) {
                            var healthyNotice = '<div class="table-dataRemind">抱歉，暂无业务系统！</div>';
                            $bfd.before($(healthyNotice));
                            return;
                        } else if (healthTotal >= 5){
                        	$bizFllMain.find('.full-healthy-box').find('.biz-comm-title').html('');

                            var tasks = oc.index.indexLayout.data("tasks");
                            if (autoAble) {
                                if(oc.home.bizhealthyautoscan){
                                	clearInterval(oc.home.bizhealthyautoscan);
                                	oc.home.bizhealthyautoscan = null;
                                }
                                oc.home.bizhealthyautoscan = setInterval(autoFlip, autoTime * 1000);
            					if(tasks && tasks.length > 0){
            						oc.index.indexLayout.data("tasks").push(oc.home.bizhealthyautoscan);
            					}else{
            						tasks = new Array();
            						tasks.push(oc.home.bizhealthyautoscan);
            						oc.index.indexLayout.data("tasks", tasks);
            					}
                            }
                            if (autoScan) {
                            	autoScanNum = 0;
                                if(oc.home.bizautoscan){
                                	clearInterval(oc.home.bizautoscan);
                                	oc.home.bizautoscan = null;
                                }
                                oc.home.bizautoscan = setInterval(autoFlipScan, autoScanTime * 1000);
                                if(tasks && tasks.length > 0){
                                    oc.index.indexLayout.data("tasks").push(oc.home.bizautoscan);
                                }else{
                                    tasks = new Array();
                                    tasks.push(oc.home.bizautoscan);
                                    oc.index.indexLayout.data("tasks", tasks);
                                }
                            }
                        }
                    }
                    var heaTag = $bizFllMain.find('.biz-full-degree');
                    $bizFllMain.find('.biz-full-grades').removeClass('biz-hide');
                    $bizFllMain.find('.biz-full-scan').removeClass('biz-hide');
                    $bizFllMain.find('.full-grades-num').prop('number', 0).animateNumber({number: result[0].health},'slow');
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
                        $bizFllMain.find('.biz-full-scan').css({
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
            var scanTag = $bizFllMain.find('.full-healthy-box').find('.biz-degree');
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
                var scanTag = $bizFllMain.find('.full-healthy-box').find('.biz-scan');
                scanTag.css({
                    'transform':'rotate(' + angle + 'deg)',
                    '-ms-transform':'rotate(' + angle + 'deg)',
                    '-moz-transform':'rotate(' + angle + 'deg)',
                    '-webkit-transform':'rotate(' + angle + 'deg)',
                    '-o-transform':'rotate(' + angle + 'deg)'
                });
                var curNum = parseInt($bizFllMain.find('.full-healthy-box').find('.grades-num').text());
                $bizFllMain.find('.full-healthy-box').find('.grades-num').prop('number', curNum).animateNumber({number: grade},'slow');
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
            var autoCount = parseInt(healthTotal / 5);
            if ((initStart + 1) == autoCount) {
                $bizFllMain.find('.full-healthy-box').find('.cap-up').removeClass('cap-page-able');
                $bizFllMain.find('.full-healthy-box').find('.cap-down').addClass('cap-page-able');
            } else if ((initStart + 1) > autoCount){
                $bizFllMain.find('.full-healthy-box').find('.cap-up').addClass('cap-page-able');
                $bizFllMain.find('.full-healthy-box').find('.cap-down').removeClass('cap-page-able');
                initStart = -1;
            } else {
                $bizFllMain.find('.full-healthy-box').find('.cap-up').removeClass('cap-page-able');
            }
            renderHealthy(++initStart * 5);
            if (autoScan) {
                autoScanNum = 0;
                if(oc.home.bizautoscan){
                	clearInterval(oc.home.bizautoscan);
                	oc.home.bizautoscan = null;
                }
                oc.home.bizautoscan = setInterval(autoFlipScan, autoScanTime * 1000);
            }
        }
    }
    
    //存储容量
    function renderCapacity(start, pagesize){
    	
    	clearEchartObject();
    	
        oc.util.ajax({
            url:oc.resource.getUrl('portal/business/service/getStoreCapacityInfo.htm'),
            data:{startNum:start,pageSize:pagesize},
            success:function(data){
                var result = data.data.datas;
                var $fs = $('.full-storage');
                $fs.empty();
                if (storageFirst) {
                    storageFirst = false;
                    var storageTotal = data.data.totalRecord;
                    if (storageTotal == 0){
                        var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                        $fs.append($(capNotice));
                        return;
                    }
                    if (storageTotal > count) {
                        var capPage = '<div id="fullstoragePage" class="fullstoragePage cap-page">';
                        capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                        capPage += '<a class="cap-down" href="javascript:;"></a>';
                        capPage +=  '</div>';
                        $fs.after(capPage);
                        //分页
                        $bizFllMain.find('.fullstoragePage a').on('click', function(){
                            var self = $(this);
                            if (self.hasClass('cap-page-able')) {
                                return;
                            }
                            if (self.hasClass('cap-down')){
                                self.siblings('a').removeClass('cap-page-able');
                                storeStart += count;
                                renderCapacity(storeStart, count);
                                if ((storeStart + 1) * count >= storageTotal) {
                                    self.addClass('cap-page-able');
                                }
                            } else {
                                self.siblings('a').removeClass('cap-page-able');
                                storeStart -= count;
                                renderCapacity(storeStart, count);
                                if (storeStart == 0){
                                    self.addClass('cap-page-able');
                                }
                            }
                        });
                    }
                }

                for (var i = 0; i < result.length; i++) {
                	
                	var capacity = '';
                    capacity += '<div class="cap-row biz-clearfix" style="margin:' + padding + 'px 0">';
                    capacity += '<div class="biz-fl storage_' + result[i].bizId + '" style="width: 50%;height: 84px;padding: 7px 0;position: relative;">';
                    capacity += '</div>';
                    capacity += '<div class="biz-fr cap-detail">';
                    capacity += '<h4>' + result[i].bizName + '</h4>';
                    capacity += '<div class="cap-total">总容量' + result[i].totalStore + '</div>';
                    capacity += '<div class="cap-used">已使用' + result[i].useStore + '</div>';
                    capacity += '</div>';
                    capacity += '</div>';
                    
                    $fs.append(capacity);
                    initPieChart($bizFllMain.find('.storage_' + result[i].bizId),result[i].useRate);
                    
                }
            }
        });
    }
    
    function renderDB(start, pagesize){
    	
    	clearEchartObject();
        var $fd =  $('.full-database').empty();
        oc.util.ajax({
            url:oc.resource.getUrl('portal/business/service/getDatabaseCapacityInfo.htm'),
            data:{startNum:start,pageSize:pagesize},
            success:function(data){
                var result = data.data.datas;
                $fd.empty();
                if (dbFirst) {
                    dbFirst = false;
                    var dbTotal = data.data.totalRecord;
                    if (dbTotal == 0){
                        var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                        $fd.append($(capNotice));
                        return;
                    }
                    if (dbTotal > count) {
                        var capPage = '<div id="dbPage" class="dbPage cap-page">';
                        capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                        capPage += '<a class="cap-down" href="javascript:;"></a>';
                        capPage +=  '</div>';
                        $fd.after(capPage);
                        //分页
                        $bizFllMain.find('.dbPage a').on('click', function(){
                            var self = $(this);
                            if (self.hasClass('cap-page-able')) {
                                return;
                            }
                            if (self.hasClass('cap-down')){
                                self.siblings('a').removeClass('cap-page-able');
                                dbStart += count;
                                renderDB(dbStart, count);
                                if ((dbStart + 1) * count >= dbTotal) {
                                    self.addClass('cap-page-able');
                                }
                            } else {
                                self.siblings('a').removeClass('cap-page-able');
                                dbStart -= count;
                                renderDB(dbStart, count);
                                if (dbStart == 0){
                                    self.addClass('cap-page-able');
                                }
                            }
                        });
                    }
                }
                for (var i = 0; i < result.length; i++) {
                	
                	var capacity = '';
                    capacity += '<div class="cap-row biz-clearfix" style="margin:' + padding + 'px 0">';
                    capacity += '<div class="biz-fl db_' + result[i].bizId + '" style="width: 50%;height: 84px;padding: 7px 0;position: relative;">';
                    capacity += '</div>';
                    capacity += '<div class="biz-fr cap-detail">';
                    capacity += '<h4>' + result[i].bizName + '</h4>';
                    capacity += '<div class="cap-total">总容量' + result[i].totalTableSpace + '</div>';
                    capacity += '<div class="cap-used">已使用' + result[i].useTableSpace+ '</div>';
                    capacity += '</div>';
                    capacity += '</div>';
                    
                    $fd.append(capacity);
                    initPieChart($bizFllMain.find('.db_' + result[i].bizId),result[i].useRate);
                    
                }
            }
        });
    }
    
    function renderBandwidth(start, pagesize){
    	clearEchartObject();
        var $fb = $('.full-bandwidth').empty();

        oc.util.ajax({
            url:oc.resource.getUrl('portal/business/service/getBandwidthCapacityInfo.htm'),
            data:{startNum:start,pageSize:pagesize},
            success:function(data){
                var result = data.data.datas;
                $fb.empty();
                if (bandwidthFirst) {
                    bandwidthFirst = false;
                    var bandwidthTotal = data.data.totalRecord;
                    if (bandwidthTotal == 0){
                        var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                        $('#database').append($(capNotice));
                        return;
                    }
                    if (bandwidthTotal > count) {
                        var capPage = '<div id="bandwidthPage" class="bandwidthPage cap-page">';
                        capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                        capPage += '<a class="cap-down" href="javascript:;"></a>';
                        capPage +=  '</div>';
                        $fb.after(capPage);
                        //分页
                        $bizFllMain.find('.bandwidthPage a').on('click', function(){
                            var self = $(this);
                            if (self.hasClass('cap-page-able')) {
                                return;
                            }
                            if (self.hasClass('cap-down')){
                                self.siblings('a').removeClass('cap-page-able');
                                bandwidthStart += count;
                                renderBandwidth(bandwidthStart, count);
                                if ((bandwidthStart + 1) * count >= bandwidthTotal) {
                                    self.addClass('cap-page-able');
                                }
                            } else {
                                self.siblings('a').removeClass('cap-page-able');
                                bandwidthStart -= count;
                                renderBandwidth(bandwidthStart, count);
                                if (bandwidthStart == 0){
                                    self.addClass('cap-page-able');
                                }
                            }
                        });
                    }
                }
                for (var i = 0; i < result.length; i++) {
                	
                	var capacity = '';
                    capacity += '<div class="cap-row biz-clearfix" style="margin:' + padding + 'px 0">';
                    capacity += '<div class="biz-fl band_' + result[i].bizId + '" style="width: 50%;height: 84px;padding: 7px 0;position: relative;" id="band_' + result[i].bizId + '">';
                    capacity += '</div>';
                    capacity += '<div class="biz-fr cap-detail">';
                    capacity += '<h4>' + result[i].bizName + '</h4>';
                    capacity += '<div class="cap-total">总容量' + result[i].totalFlow + '</div>';
                    capacity += '<div class="cap-used">已使用' + result[i].useFlow+ '</div>';
                    capacity += '</div>';
                    capacity += '</div>';
                    
                    $fb.append(capacity);
                    initPieChart($bizFllMain.find('.band_' + result[i].bizId),result[i].useRate);
                    
                }
            }
        });
    }
    
    function renderCalculate(start, pagesize){
    	clearEchartObject();
        var $fc = $('.full-calculate').empty();

        oc.util.ajax({
            url:oc.resource.getUrl('portal/business/service/getCalculateCapacityInfo.htm'),
            data:{startNum:start,pageSize:pagesize},
            success:function(data){
                var result = data.data.datas;
                $fc.empty();
                if (calFirst) {
                    calFirst = false;
                    var calTotal = data.data.totalRecord;
                    if (calTotal == 0){
                        var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                        $fc.append($(capNotice));
                        return;
                    }
                    if (calTotal > count) {
                        var capPage = '<div id="calPage" class="calPage cap-page">';
                        capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                        capPage += '<a class="cap-down" href="javascript:;"></a>';
                        capPage +=  '</div>';
                        $fc.after(capPage);
                        //分页
                        $bizFllMain.find('.calPage a').on('click', function(){
                            var self = $(this);
                            if (self.hasClass('cap-page-able')) {
                                return;
                            }
                            if (self.hasClass('cap-down')){
                                self.siblings('a').removeClass('cap-page-able');
                                calStart += count;
                                renderCalculate(calStart, count);
                                if ((calStart + 1) * count >= calTotal) {
                                    self.addClass('cap-page-able');
                                }
                            } else {
                                self.siblings('a').removeClass('cap-page-able');
                                calStart -= count;
                                renderCalculate(calStart, count);
                                if (calStart == 0){
                                    self.addClass('cap-page-able');
                                }
                            }
                        });
                    }
                }
                for (var i = 0; i < result.length; i++) {
                	
                	var capacity = '';
                    capacity += '<div class="cap-row biz-clearfix" style="margin:' + padding + 'px 0">';
                    capacity += '<div class="biz-fl calculate_' + result[i].bizId + '" style="width: 50%;height: 84px;padding: 7px 0;position: relative;" id="calculate_' + result[i].bizId + '">';
                    capacity += '</div>';
                    capacity += '<div class="biz-fr cap-detail">';
                    capacity += '<h4 class="cpuTitle">' + result[i].bizName + '</h4>';
                    //capacity += '<div class="cap-total">共容量' + result[i].totalFlow + '</div>';
                    //capacity += '<div class="cap-used">已使用' + result[i].useFlow+ '</div>';
                    capacity += '</div>';
                    capacity += '</div>';
                    
                    $fc.append(capacity);
                    initPieChart($bizFllMain.find('.calculate_' + result[i].bizId),result[i].cpuRate);
                }
            }
        });
    }
    
    function clearEchartObject(){
    	$bizFllMain.find('.biz-fl').each(function(){
    		echarts.dispose(echarts.getInstanceByDom(this));
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
    
    function initFullStorage(){
        //存储容量
        $bizFllMain.find('.full-tabs li').on('click', function(){
            var $this = $(this);
            var capIndex = $this.index();
            $this.siblings('li').removeClass('active');
            $this.addClass('active');
            $bizFllMain.find('.full-cap-tag').addClass('biz-hide');
            $bizFllMain.find('.full-cap-tag').eq(capIndex).removeClass('biz-hide');
            
            if(capIndex == 0){
            	curShowStorage = 0;
                renderCapacity(storeStart, count);
            }else if(capIndex == 1){
            	curShowStorage = 1;
            	renderDB(dbStart, count);
            }else if(capIndex == 2){
            	curShowStorage = 2;
            	renderBandwidth(bandwidthStart, count);
            }else if(capIndex == 3){
            	curShowStorage = 3;
            	renderCalculate(calStart, count);
            }
        });
        
        renderCapacity(storeStart, count);

    }

    //业务运行情况
    function initFullDatagrid(){
    	
    	if(!initDatagrid){
    		var $bfo = $('#biz-full-operation').attr('id',oc.util.generateId());
    		
    		var nowDate = new Date();
    		var endTime = nowDate.getTime();
    		var startTime = endTime - 7*24*60*60*1000;
    		initDatagrid = oc.ui.datagrid({
    			selector: $bfo,
    			//fitColumns:true,
    			pagination: false,
    			url: oc.resource.getUrl('portal/business/service/getPageListRunInfo.htm'),
    			queryParams:{startTime: startTime.toString(),endTime:endTime.toString()},
    			octoolbar: {},
    			columns:[[
    			          {
    			        	  field:'bizName',
    			        	  title:'<span class="biz-name">业务名称</span>',
    			        	  width:'17%',
    			        	  resizable:true,
    			        	  formatter: function(value,row,index){
    			        		  return '<span title="' + value + '" class="biz-name">' + value + '</span>';
    			        	  }
    			          },
    			          {field:'healthScore',title:'健康度',width:'14%',resizable:true},
    			          {field:'availableRate',title:'可用率',width:'15%',resizable:true},
    			          {field:'mttr',title:'MTTR <a class="operation-help fa  fa-question-circle light_blue" title="Mean Time To Repair"></a>',width:'14%',resizable:true},
    			          {field:'mtbf',title:'MTBF <a class="operation-help fa  fa-question-circle light_blue" title="Mean Time To Failure"></a>' ,width:'14%',resizable:true},
    			          {field:'downTime',title:'宕机时长',width:'14%',resizable:true},
    			          {field:'outageTimes',title:'宕机次数',width:'14%',resizable:true}
    			          ]],
    			          loadFilter:function(data){
    			        	  return {total:data.data.length,rows:data.data};
    			          },
    			          onLoadSuccess: function(){
    			        	  $bfo.datagrid("resize");
    			          }
    		});
    	}else{
    		
    		var nowDate = new Date();
    		var endTime = nowDate.getTime();
    		var startTime = endTime - 7*24*60*60*1000;
    		
    		initDatagrid.selector.datagrid('options').queryParams = {startTime: startTime.toString(),endTime:endTime.toString()};
    		initDatagrid.selector.datagrid('reload');
    		
    	}

        
    }
    
    oc.module.home.biz.summary = {
    		refresh:function(){
    		    responseFullSpeed();
    		    initFullAlarm();
    		    initFullHealthy();
    		    if(curShowStorage == 0){
    		    	renderCapacity(storeStart, count);
    		    }else if(curShowStorage == 1){
    		    	renderDB(dbStart, count);
    		    }else if(curShowStorage == 2){
    		    	renderBandwidth(bandwidthStart, count);
    		    }else{
    		    	renderCalculate(calStart, count);
    		    }
    		    initFullDatagrid();
    		}
    };

});


