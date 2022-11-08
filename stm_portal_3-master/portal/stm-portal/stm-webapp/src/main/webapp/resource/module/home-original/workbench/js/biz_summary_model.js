/**
 * Created by niexiaoqin on 2016/8/29.
 */

//数字滚动变化效果
(function($) {
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

    //计算模块高宽
    $('#biz-full-main').layout();
    function doFullResize(){
        var leftH = $('.biz-full-left').height(),
            leftW = $('.biz-full-left').width(),
            centerW = $('.biz-full-center').width();
        $('.full-hchats-box .biz-comm-content').css({
            width: leftW,
            height: leftH * 0.5 - 54
        });
        $('.full-healthy-box').css({
            width: centerW,
            height: centerW * 0.37 + 120
        });
        $('.full-operation-box').css({
            width: centerW,
            height: leftH - centerW * 0.37 -120
        });
    }
    doFullResize();

    //业务响应速度
    function responseFullSpeed() {
        var initResponse = new Highcharts.Chart({
            chart: {
                renderTo: 'biz-full-response',
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
                    color: '#ffffff',
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
                        color: '#ffffff'
                    }
                },
                gridLineColor: '#268abf',
                gridLineWidth: 1
            },
            xAxis: {
                categories: [],
                labels: {
                	maxStaggerLines: 1,
                	formatter:function(){
                		if(this.value.length>6){
                			return this.value.substring(0,6)+'...';
                		}
                		return this.value;
                	},
                    style: {
                        color: '#ffffff'
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
                color:'#0bedf4',
                data: [0,0,0,0,0]
            }]
        });
        initResponse.showLoading();
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
                    initResponse.xAxis[0].setCategories(categories);
                    initResponse.series[0].setData(series);
                }
                initResponse.hideLoading();
                initResponse.redraw();
            }
        });
    }
    responseFullSpeed();

    //业务告警次数
    function initFullAlarm(){
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
                    $(alarmNotice).appendTo($('#alarm-num'));
                    return;
                }
                Highcharts.setOptions({
                    colors: ['#61c4f8', '#333557', '#6fce6b', '#fe945d', '#6371fe', '#ec48b4', '#ebd256', '#1c907c', '#ff4663']
                });
                 $('#full-alarm-num').highcharts({
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
                            innerSize: 50,
                            depth: 25
                        },
                        series: {
                            dataLabels: {
                                enabled: true,
                                connectorColor: '#ffffff',
                                color: '#ffffff',
                                formatter:function(){
                         			var tmp = this.point.name;
                         			if(tmp.indexOf('(')>6){
                         				return tmp.substring(0,7)+'...'+tmp.substring(tmp.indexOf('('),tmp.length);
                         			}
                         			return tmp;
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
    initFullAlarm();

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
                            $('#biz-full-disk').before($(healthyNotice));
                            return;
                        } else if (healthTotal >= 5){
                        	$('.full-healthy-box').find('.biz-comm-title').html('');
                            var heaPage = '<a id="full-healthy-time" class="healthy-time" href="javascript:;"></a>';
                            heaPage += '<div class="cap-page">';
                            heaPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            heaPage += '<a class="cap-down" href="javascript:;"></a>';
                            heaPage +=  '</div>';
                            $('.full-healthy-box').find('.biz-comm-title').append($(heaPage));
                            $('#full-healthy-time').on('click', function(){
                                setCycle();
                            });
                            $('.full-healthy-box .cap-page a').on('click', function(){
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
                                    if(oc.home.bizautoscan){
                                    	clearInterval(oc.home.bizautoscan);
                                    	oc.home.bizautoscan = null;
                                    }
                                    oc.home.bizautoscan = setInterval(autoFlipScan, autoScanTime * 1000);
                                }
                            });
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
                    var heaTag = $('.biz-full-degree');
                    $('.biz-full-grades').removeClass('biz-hide');
                    $('.biz-full-scan').removeClass('biz-hide');
                    $('.full-grades-num').prop('number', 0).animateNumber({number: result[0].health},'slow');
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
                        heaStr += '<span class="biz-system-name">' + result[i].name + '</span>';
                        heaTag.eq(i).attr('data-grade', result[i].health);
                        heaTag.eq(i).attr('data-id', result[i].id);
                        heaTag.eq(i).html(heaStr);
                        heaTag.eq(i).removeClass('biz-hide');
                        $('.biz-full-scan').css({
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
            var scanTag = $('.full-healthy-box').find('.biz-degree');
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
                var scanTag = $('.full-healthy-box').find('.biz-scan');
                scanTag.css({
                    'transform':'rotate(' + angle + 'deg)',
                    '-ms-transform':'rotate(' + angle + 'deg)',
                    '-moz-transform':'rotate(' + angle + 'deg)',
                    '-webkit-transform':'rotate(' + angle + 'deg)',
                    '-o-transform':'rotate(' + angle + 'deg)'
                });
                var curNum = parseInt($('.full-healthy-box').find('.grades-num').text());
                $('.full-healthy-box').find('.grades-num').prop('number', curNum).animateNumber({number: grade},'slow');
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
            console.debug('autoScanNum : ' + autoScanNum);
        }
        function autoFlip(){
            var autoCount = parseInt(healthTotal / 5);
            if ((initStart + 1) == autoCount) {
                $('.full-healthy-box').find('.cap-up').removeClass('cap-page-able');
                $('.full-healthy-box').find('.cap-down').addClass('cap-page-able');
            } else if ((initStart + 1) > autoCount){
                $('.full-healthy-box').find('.cap-up').addClass('cap-page-able');
                $('.full-healthy-box').find('.cap-down').removeClass('cap-page-able');
                initStart = -1;
            } else {
                $('.full-healthy-box').find('.cap-up').removeClass('cap-page-able');
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
        function setCycle(){
            var autoStr = '<div class="biz-scan-cycle">';
            autoStr += '<input type="checkbox" name="" id="full-custom-scan" class="custom-cycle" value="2" />自动扫描';
            autoStr += '<span>周期：</span>';
            autoStr += '<input id="full-custom-scan-ipt" class="easyui-numberspinner" style="width:100px" min="1"  max="100000" value="' + autoScanTime + '"/>&nbsp;秒';
            autoStr += '</div>';
            autoStr += '<div id="biz-set-full-cycle" class="biz-set-cycle">';
            autoStr += '<input type="checkbox" name="" class="custom-cycle" value="10" />自动轮播';
            autoStr += '<span>周期：</span>';
            autoStr += '<input class="custom-cycle-ipt easyui-numberspinner" style="width:100px" min="1"  max="100000" type="text" value="' + autoTime + '" />&nbsp;秒';
            autoStr += '</div>';
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
                        var value = $('#biz-set-full-cycle').find('.custom-cycle-ipt').val();
                        var valueScan = $('#full-custom-scan-ipt').val();
                        autoAble = $('#biz-set-full-cycle').find('.custom-cycle').is(':checked');
                        autoScan = $('#full-custom-scan').is(':checked');
                        var tasks = oc.index.indexLayout.data("tasks");
                        if (autoAble) {
                            autoTime = parseInt(value);
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
                        } else {
                        	if(oc.home.bizhealthyautoscan){
                        		clearInterval(oc.home.bizhealthyautoscan);
                        	}
                        }
                        if (autoScan) {
                            autoScanTime = parseInt(valueScan);
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
                        } else {
                            autoScanNum = 0;
                            var grade = $('.biz-full-degree').eq(0).attr('data-grade');
                            $('.biz-full-scan').css({
                                'transform':'rotate(75deg)',
                                '-ms-transform':'rotate(75deg)',
                                '-moz-transform':'rotate(75deg)',
                                '-webkit-transform':'rotate(75deg)',
                                '-o-transform':'rotate(75deg)'
                            });
                            var curNum = parseInt($('.full-grades-num').text());
                            $('.full-grades-num').prop('number', curNum).animateNumber({number: grade},'slow');
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
            $('#biz-set-full-cycle').find('.custom-cycle').attr('checked', autoAble);
            $('#full-custom-scan').attr('checked', autoScan);
        }
        $('.biz-full-degree').on('click', function(){
            var self = $(this);
            var angle = self.attr('data-angle');
            var grade = self.attr('data-grade');
            var scanTag = $('.biz-full-scan');
            scanTag.css({
                'transform':'rotate(' + angle + 'deg)',
                '-ms-transform':'rotate(' + angle + 'deg)',
                '-moz-transform':'rotate(' + angle + 'deg)',
                '-webkit-transform':'rotate(' + angle + 'deg)',
                '-o-transform':'rotate(' + angle + 'deg)'
            });
            //$('.grades-num').text(grade);
            var curNum = parseInt($('.full-grades-num').text());
            $('.full-grades-num').prop('number', curNum).animateNumber({number: grade},'slow');
        });
        /*$('.biz-full-degree').on('dblclick', function(){
            clearInterval(fullHealthyAuto);
            var bizId = $(this).attr('data-id');
            oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/buz/business.html'), function(){
                oc.resource.loadScript('resource/module/business-service/buz/business.js',
                    function(){
                        new businessTopo({
                            bizId:bizId
                        });
                    });
            });
        });*/
    }
    initFullHealthy();



    function initFullStorage(){
        //存储容量
        $('.full-tabs li').on('click', function(){
            var $this = $(this);
            var capIndex = $this.index();
            $this.siblings('li').removeClass('active');
            $this.addClass('active');
            $('.full-cap-tag').addClass('biz-hide');
            $('.full-cap-tag').eq(capIndex).removeClass('biz-hide');
        });
        var capH = $('.bix-full-right').height();
        var count = parseInt(capH / 124);
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
                data:{startNum:start,pageSize:pagesize},
                success:function(data){
                    var result = data.data.datas;
                    var capacity = '';
                    var rate, rateColor;
                    $('#full-storage').empty();
                    if (storageFirst) {
                        storageFirst = false;
                        var storageTotal = data.data.totalRecord;
                        if (storageTotal == 0){
                            var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                            $('#full-storage').append($(capNotice));
                            return;
                        }
                        if (storageTotal > count) {
                            var capPage = '<div id="fullstoragePage" class="cap-page">';
                            capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            capPage += '<a class="cap-down" href="javascript:;"></a>';
                            capPage +=  '</div>';
                            $('#full-storage').after(capPage);
                            //分页
                            $('#fullstoragePage a').on('click', function(){
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
                        rate = result[i].useRate;
                        if (rate >= 90){
                            rateColor = 'cap-orange';
                        } else if (rate < 90 && rate >= 80) {
                            rateColor = 'cap-yllow';
                        } else {
                            rateColor = 'cap-green';
                        }
                        capacity += '<div class="cap-row biz-clearfix">';
                        capacity += '<div class="biz-fl cap-icon ' + rateColor + '">';
                        capacity += '<div class="cap-num">';
                        capacity += '<div class="cap-num-bg" style="height: ' + rate + '%;"></div>';
                        capacity += '<span>' + rate + '%</span>';
                        capacity += '</div>';
                        capacity += '<div class="cap-make"></div>';
                        capacity += '</div>';
                        capacity += '<div class="biz-fr cap-detail">';
                        capacity += '<h4>' + result[i].bizName + '</h4>';
                        capacity += '<div class="cap-total">总容量' + result[i].totalStore + '</div>';
                        capacity += '<div class="cap-used">已使用' + result[i].useStore + '</div>';
                        capacity += '</div>';
                        capacity += '</div>';
                    }
                    $(capacity).appendTo($('#full-storage'));
                }
            });
        }
        function renderDB(start, pagesize){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getDatabaseCapacityInfo.htm'),
                data:{startNum:start,pageSize:pagesize},
                success:function(data){
                    var result = data.data.datas;
                    var capacity = '';
                    var rate = 0, rateColor;
                    $('#full-database').empty();
                    if (dbFirst) {
                        dbFirst = false;
                        var dbTotal = data.data.totalRecord;
                        if (dbTotal == 0){
                            var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                            $('#full-database').append($(capNotice));
                            return;
                        }
                        if (dbTotal > count) {
                            var capPage = '<div id="dbPage" class="cap-page">';
                            capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            capPage += '<a class="cap-down" href="javascript:;"></a>';
                            capPage +=  '</div>';
                            $('#full-database').after(capPage);
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
                        rate = result[i].useRate;
                        if (rate >= 90){
                            rateColor = 'cap-orange';
                        } else if (rate < 90 && rate >= 80) {
                            rateColor = 'cap-yllow';
                        } else {
                            rateColor = 'cap-green';
                        }
                        capacity += '<div class="cap-row biz-clearfix">';
                        capacity += '<div class="biz-fl cap-icon ' + rateColor + '">';
                        capacity += '<div class="cap-num">';
                        capacity += '<div class="cap-num-bg" style="height: ' + rate + '%;"></div>';
                        capacity += '<span>' + rate + '%</span>';
                        capacity += '</div>';
                        capacity += '<div class="cap-make"></div>';
                        capacity += '</div>';
                        capacity += '<div class="biz-fr cap-detail">';
                        capacity += '<h4>' + result[i].bizName + '</h4>';
                        capacity += '<div class="cap-total">总容量' + result[i].totalTableSpace + '</div>';
                        capacity += '<div class="cap-used">已使用' + result[i].useTableSpace+ '</div>';
                        capacity += '</div>';
                        capacity += '</div>';
                    }
                    $(capacity).appendTo($('#full-database'));
                }
            });
        }
        function renderBandwidth(start, pagesize){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getBandwidthCapacityInfo.htm'),
                data:{startNum:start,pageSize:pagesize},
                success:function(data){
                    var result = data.data.datas;
                    var capacity = '';
                    var rate, rateColor;
                    $('#full-bandwidth').empty();
                    if (bandwidthFirst) {
                        bandwidthFirst = false;
                        var bandwidthTotal = data.data.totalRecord;
                        if (bandwidthTotal == 0){
                            var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                            $('#database').append($(capNotice));
                            return;
                        }
                        if (bandwidthTotal > count) {
                            var capPage = '<div id="bandwidthPage" class="cap-page">';
                            capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            capPage += '<a class="cap-down" href="javascript:;"></a>';
                            capPage +=  '</div>';
                            $('#full-bandwidth').after(capPage);
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
                                    if (dbStart == 0){
                                        self.addClass('cap-page-able');
                                    }
                                }
                            });
                        }
                    }
                    for (var i = 0; i < result.length; i++) {
                        rate = result[i].useRate;
                        if (rate >= 80){
                            rateColor = 'cap-orange';
                        } else if (rate < 80 && rate >= 70) {
                            rateColor = 'cap-yllow';
                        } else {
                            rateColor = 'cap-green';
                        }
                        capacity += '<div class="cap-row biz-clearfix">';
                        capacity += '<div class="biz-fl cap-icon ' + rateColor + '">';
                        capacity += '<div class="cap-num">';
                        capacity += '<div class="cap-num-bg" style="height: ' + rate + '%;"></div>';
                        capacity += '<span>' + rate + '%</span>';
                        capacity += '</div>';
                        capacity += '<div class="cap-make"></div>';
                        capacity += '</div>';
                        capacity += '<div class="biz-fr cap-detail">';
                        capacity += '<h4>' + result[i].bizName + '</h4>';
                        capacity += '<div class="cap-total">总容量' + result[i].totalFlow + '</div>';
                        capacity += '<div class="cap-used">已使用' + result[i].useFlow+ '</div>';
                        capacity += '</div>';
                        capacity += '</div>';
                    }
                    $('#full-bandwidth').html(capacity);
                }
            });
        }
        function renderCalculate(start, pagesize){
            oc.util.ajax({
                url:oc.resource.getUrl('portal/business/service/getCalculateCapacityInfo.htm'),
                data:{startNum:start,pageSize:pagesize},
                success:function(data){
                    var result = data.data.datas;
                    var capacity = '';
                    var rate, rateColor;
                    $('#full-calculate').empty;
                    if (calFirst) {
                        calFirst = false;
                        var calTotal = data.data.totalRecord;
                        if (calTotal == 0){
                            var capNotice = '<div class="table-dataRemind">抱歉，暂无存储容量！</div>';
                            $('#full-calculate').append($(capNotice));
                            return;
                        }
                        if (calTotal > count) {
                            var capPage = '<div id="calPage" class="cap-page">';
                            capPage += '<a class="cap-up cap-page-able" href="javascript:;"></a>';
                            capPage += '<a class="cap-down" href="javascript:;"></a>';
                            capPage +=  '</div>';
                            $('#full-calculate').after(capPage);
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
                                    if (dbStart == 0){
                                        self.addClass('cap-page-able');
                                    }
                                }
                            });
                        }
                    }
                    for (var i = 0; i < result.length; i++) {
                        rate = result[i].cpuRate;
                        if (rate >= 90){
                            rateColor = 'cap-orange';
                        } else if (rate < 90 && rate >= 75) {
                            rateColor = 'cap-yllow';
                        } else {
                            rateColor = 'cap-green';
                        }
                        capacity += '<div class="cap-row biz-clearfix">';
                        capacity += '<div class="biz-fl cap-icon ' + rateColor + '">';
                        capacity += '<div class="cap-num">';
                        capacity += '<div class="cap-num-bg" style="height: ' + rate + '%;"></div>';
                        capacity += '<span>' + rate + '%</span>';
                        capacity += '</div>';
                        capacity += '<div class="cap-make"></div>';
                        capacity += '</div>';
                        capacity += '<div class="biz-fr cap-detail">';
                        capacity += '<h4 class="cpuTitle">' + result[i].bizName + '</h4>';
                        //capacity += '<div class="cap-total">共容量' + result[i].totalFlow + '</div>';
                        //capacity += '<div class="cap-used">已使用' + result[i].useFlow+ '</div>';
                        capacity += '</div>';
                        capacity += '</div>';
                    }
                    $('#full-calculate').html(capacity);
                }
            });
        }
        renderCapacity(storeStart, count);
        renderDB(dbStart, count);
        renderBandwidth(bandwidthStart, count);
        renderCalculate(calStart, count);

    }
    initFullStorage();

    //$(window).resize(function (){
    //    setTimeout(function(){
    //        doFullResize();
    //        responseFullSpeed();
    //        initFullStorage();
    //        initFullAlarm();
    //    }, 100);
    //});
});

function initFullDatagrid(){
    var nowDate = new Date();
    var endTime = nowDate.getTime();
    var startTime = endTime - 7*24*60*60*1000;
    var val = 1;
    var iptVal;
    var bizOperation = oc.ui.datagrid({
        selector: $('#biz-full-operation'),
        //fitColumns:true,
        pagination: false,
        url: oc.resource.getUrl('portal/business/service/getPageListRunInfo.htm'),
        queryParams:{startTime: startTime.toString(),endTime:endTime.toString()},
        octoolbar: {},
        columns:[[
            {
                field:'bizName',
                title:'<span class="biz-name">业务名称</span>',
                width:'16%',
                resizable:true,
                formatter: function(value,row,index){
                    //return that.formatCheckbox(value,row,index,'查看',that.authSelect);
//                    return '<a class="biz-operation-name" rowIndex="' + index + '" href="javascript:;">' + value + '</a>'
                    return '<span class="biz-name">' + value + '</span>'
                }
            },
            {field:'healthScore',title:'健康度',width:'14%',resizable:true},
            {field:'availableRate',title:'可用率',width:'14%',resizable:true},
            {field:'mttr',title:'MTTR <a class="operation-help" title="Mean Time To Repair"></a>',width:'14%',resizable:true},
            {field:'mtbf',title:'MTBF <a class="operation-help" title="Mean Time To Failure"></a>' ,width:'14%',resizable:true},
            {field:'downTime',title:'宕机时长',width:'14%',resizable:true},
            {field:'outageTimes',title:'宕机次数',width:'14%',resizable:true}
        ]],
        loadFilter:function(data){
            return {total:data.data.length,rows:data.data};
        },
        onLoadSuccess: function(){
//            var panel = $('#biz-full-operation').datagrid("getPanel");
//            panel.find(".biz-operation-name").on('click',function(e){
//                var rowIndex = $(this).attr('rowIndex');
//                var row = $('#biz-full-operation').datagrid("getRows")[rowIndex];
//                oc.resource.loadScript("resource/module/business-service/biz-detail-info/js/business_detail_info.js", function(){
//                    oc.module.biz.detailinfo.open({bizId: row.bizId});
//                });
//            });
            $("#biz-full-operation").datagrid("resize");
        }
    });

    function reloadgrid(queryParams) {
        //查询参数直接添加在queryParams中
        $('#biz-full-operation').datagrid('options').queryParams = queryParams;
        $("#biz-full-operation").datagrid('reload');
    }

    function openSetTime(){
        var setTimeStr = '<div id="biz-set-time" class="biz-set-time">';
        setTimeStr += '<div class="set-time-row">';
        setTimeStr += '<input type="radio" name="setTime" value="0" />最近24小时';
        setTimeStr += '</div>';
        setTimeStr += '<div class="set-time-row">';
        setTimeStr += '<input type="radio" name="setTime" value="1" />最近7天';
        setTimeStr += '</div>';
        setTimeStr += '<div class="set-time-row">';
        setTimeStr += '<input type="radio" name="setTime" value="2" />最近30天';
        setTimeStr += '</div>';
        setTimeStr += '<div class="set-time-row">';
        setTimeStr += '<input type="radio" name="setTime" class="custom-time" value="3" />最近';
        setTimeStr += '<input class="custom-time-ipt" disabled type="number" min="0" /> 天';
        setTimeStr += '</div>';
        setTimeStr += '</div>';
        var _dlg = $("<div></div>");
        _dlg.dialog({
            title: '选择统计时间段',
            width: 400,
            height: 280,
            content: setTimeStr,
            buttons : [{
                text:"确定",
                iconCls:"fa fa-check-circle",
                handler:function(){
                    var mydate = new Date();
                    var endTime = mydate.getTime();
                    var startTime;
                    val = $("input[name='setTime']:checked").val();
                    switch (val) {
                        case '0':
                            startTime = endTime - 24*60*60*1000;
                            break;
                        case '1':
                            startTime = endTime - 7*24*60*60*1000;
                            break;
                        case '2':
                            startTime = endTime - 30*24*60*60*1000;
                            break;
                        case '3':
                            if ($('.custom-time-ipt').val() == '' || $('.custom-time-ipt').val() == '0'){
                                alert('请输入大于0的天数！');
                                return;
                            }
                            iptVal = Number($('.custom-time-ipt').val());
                            startTime = endTime - iptVal*24*60*60*1000;
                            break;
                    }
                    var queryParams = {startTime:startTime.toString(),endTime:endTime.toString()};
                    reloadgrid(queryParams);
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

        $("input[name='setTime']").eq(val).attr('checked', true);
        if (val != 3) {
            $('.custom-time-ipt').attr('disabled', true);
            $('.custom-time-ipt').css('background','#d7d7d7')
        } else {
            $('.custom-time-ipt').val(iptVal);
            $('.custom-time-ipt').attr('disabled', false);
            $('.custom-time-ipt').css('background','#fff');
        }
        $("input[name='setTime']").on('click', function(){
            var $val = $(this).val();
            if ($val != 3) {
                $('.custom-time-ipt').attr('disabled', true);
                $('.custom-time-ipt').css('background','#d7d7d7');
            } else {
                $('.custom-time-ipt').attr('disabled', false);
                $('.custom-time-ipt').css('background','#fff');
            }
        });
    }
    $('.operation-time').on('click', function(){
        openSetTime();
    });
}

//业务运行情况

//initFullDatagrid();

setTimeout("initFullDatagrid()",1000);

function removeToolbar(){
	var toolbars = $('div.panel.datagrid.easyui-fluid').find('div.oc-toolbar.datagrid-toolbar');
	if(toolbars.length>1){
		toolbars[0].remove();
	}
	var toolbars4 = $('div.panel.datagrid.easyui-fluid').find('div.oc-toolbar.datagrid-toolbar');
}
setTimeout("removeToolbar()",2000);