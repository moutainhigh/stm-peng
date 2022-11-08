(function () {
    function bizChart(cfg) {
        this.cfg = cfg;
        this.selector = cfg.selector;
    }

    bizChart.prototype = {
        cfg: undefined,
        selector: undefined,
        param: {startTime: '', endTime: '', bizId: '', metricId: '', url: '', queryTimeType: ''},
        init: function () {
            var that = this;
            //set query param
            var param = that.param;
            $.extend(param, that.cfg.param);
            that.renderQueryClick();

            that.selector.find('#oneDayLi').addClass('active');
            that.handleQueryParam(24 * 60 * 1000 * 60, "halfHour");
            that.createChart();

        },
        handleQueryParam: function (timeSub, timeType) {
            var that = this;
            var param = that.param;

            var dateEnd = new Date();
            var dateStart = new Date(dateEnd.getTime() - timeSub);
            //set query time param
            param['timeType'] = timeType;
            param['startTime'] = dateStart.getTime();
            param['endTime'] = dateEnd.getTime();
        },
        renderQueryClick: function () {
            var that = this;
            that.selector.find('li[name=chartsType]').unbind('click');
            that.selector.find('li[name=chartsType]').on('click', function () {
                that.selector.find('li[name=chartsType]').each(function () {
                    var targetIn = $(this);
                    targetIn.removeClass('active');
                })

                var target = $(this);
                target.addClass('active');
//				that.param['queryTimeType'] = target.attr('queryType');
                var timeSub;
                switch (target.attr('queryType')) {
                    case '1H':
                        timeSub = 60 * 1000 * 60;
                        that.handleQueryParam(timeSub, "min");
                        that.createChart();
                        break;
                    case '1D':
                        timeSub = 24 * 60 * 1000 * 60;
                        that.handleQueryParam(timeSub, "halfHour");
                        that.createChart();
                        break;
                    case '7D':
                        timeSub = 7 * 24 * 60 * 1000 * 60;
                        that.handleQueryParam(timeSub, "hour");
                        that.createChart();
                        break;
                    case '30D':
                        timeSub = 30 * 24 * 60 * 1000 * 60;
                        that.handleQueryParam(timeSub, "sixHour");
                        that.createChart();
                        break;
                    case "random":

                        that.randomQueryDlg();

                        break;
                }
            });
        },
        createChart: function () {
            var that = this;

            oc.util.ajax({
                url: that.param.url,
                data: that.param,
                successMsg: null,
                success: function (data) {
                    var dataTemp = data.data;
                    if (!dataTemp) {
                        dataTemp = {};
                    }

                    var dateStr;
                    if (dataTemp['lastCollect']) {
                        dateStr = new Date(dataTemp['lastCollect']);
                        dataTemp['lastCollect'] = that.formatDate(dateStr);
                    } else {
                        dataTemp['lastCollect'] = '--';
                    }

//					dataTemp['unit'] = '分';
                    var tableDiv = that.selector.parent().find('#business_metric_table');
                    that.cfg.callback(tableDiv, {
                        status: dataTemp.status, curValue: dataTemp.curValue,
                        lastCollectTime: dataTemp.lastCollect, threshold: dataTemp.threshold, unit: dataTemp.unit
                    });

                    that.selector.find('#unitSpan').html(dataTemp['unit']);
                    var dataObj = that.handleChartData(dataTemp);
                    that.renderChart(dataObj);
                }
            });
        },
        handleChartData: function (data) {
            var that = this;
            var datas = data.values;

            var seriesDatas = new Array();
            var xAxisTemp;
            var tooltip;
            var yAxisTemp;
            var yAxisMaxValue = 0;
            var yAxisMinValue = 0;

            var dDouArr = new Array();
            var seriesTemp = {data: dDouArr};
            seriesDatas.push(seriesTemp);

            var handledData = new Array();
            if (that.cfg.ifAddPoint) {
//			if(true){
                //补点操作
                var endTime = that.param.endTime;
                var startTime = that.param.startTime;
                var timeSub = endTime - startTime;
                var slotNUM;
                if (timeSub < 24 * 60 * 60 * 1000) {
                    slotNUM = 10;
                    //小于1天,10个时间段
                } else if (timeSub < 30 * 24 * 60 * 60 * 1000) {
                    //小于30天,30个时间段
                    slotNUM = 30;
                } else {
                    slotNUM = 60;
                }
                var timeSlot = parseInt(timeSub / slotNUM);
                var firstPointValue = data.frontFirstHealth;
                //设置补点
                var dataExe = {value: 100, time: startTime};
                if (firstPointValue) {
                    dataExe['value'] = firstPointValue;
                }

                var bizBeginTime = that.cfg.bizCreateTime;
//				if(datas && datas.length>0){
//					var dataItem = datas[0];
//					bizBeginTime = parseInt(dataItem['time']);
//				}

                for (var i = 1; i < slotNUM; i++) {
                    var timeItemSTART = startTime + timeSlot * (i - 1);
                    if (bizBeginTime < timeItemSTART) {
                        var timeItemEND = startTime + timeSlot * i;

                        var flag = true;
                        if (datas) {
                            //补充遗漏
                            if (bizBeginTime > (timeItemSTART - timeSlot)) {
                                var dataItem = datas[0];
                                beginTime = parseInt(dataItem['time']);
                                for (var z = 0; z < datas.length; z++) {
                                    var dataItem = datas[z];
                                    var collectTime = parseInt(dataItem['time']);
                                    if (collectTime > bizBeginTime && (collectTime < timeItemSTART || collectTime == timeItemSTART)) {
                                        var dataItemAdd = $.extend({}, dataItem);
                                        handledData.push(dataItemAdd);
                                        dataExe = dataItem;
                                    }
                                }
                            }

                            for (var x = 0; x < datas.length; x++) {
                                var dataItem = datas[x];
                                var collectTime = parseInt(dataItem['time']);
                                if (collectTime > timeItemSTART && (collectTime < timeItemEND || collectTime == timeItemEND)) {
                                    var dataItemAdd = $.extend({}, dataItem);
                                    handledData.push(dataItemAdd);
                                    dataExe = dataItem;
                                    flag = false;
                                    continue;
                                }
                            }
                        }

                        if (flag) {
                            //补点
//							dataExe['time'] = timeItemEND;
                            var test = $.extend({}, dataExe);
                            handledData.push({value: test['value'], time: timeItemEND});
                        }
                    }
                }
//				if(datas){
//					for(var x=0;x<datas.length;x++){
//						var dataItem = datas[x];
//						var collectTime = parseInt(dataItem['time']);
//						if(collectTime>(startTime+timeSlot*(slotNUM-1))){
//							handledData.push(dataItem);
//							continue;
//						}
//					}
//				}
            } else {
                handledData = datas;
            }

            var dateStart = new Date(that.param['startTime']);
            var dateStartUtc = Date.UTC(dateStart.getFullYear(), dateStart.getMonth(), dateStart.getDate(), parseInt(dateStart.getHours()), parseInt(dateStart.getMinutes()));
            dDouArr.push([dateStartUtc, null]);

            var metricItemData = handledData;

            if (metricItemData)
                for (var i = 0; i < metricItemData.length; i++) {
                    var metricItemDataObj = metricItemData[i];
                    if ("null" == metricItemDataObj['value']) {
                        var date = new Date(parseInt(metricItemDataObj['time']));
                        var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), parseInt(date.getHours()), parseInt(date.getMinutes()));//, parseInt(date.getSeconds())

                        dDouArr.push([dateUtc, null]);
                    } else {
                        var date = new Date(parseInt(metricItemDataObj['time']));
                        var dDouArrData = parseFloat(metricItemDataObj['value']);
                        var dateUtc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), parseInt(date.getHours()), parseInt(date.getMinutes()));//parseInt(date.getSeconds()));

                        if (dDouArrData > yAxisMaxValue) {
                            yAxisMaxValue = dDouArrData;
                        }
                        if (dDouArrData < yAxisMinValue) {
                            yAxisMinValue = dDouArrData;
                        }
                        dDouArr.push([dateUtc, dDouArrData]);
                    }
                }

            var dateEnd = new Date(that.param['endTime']);
            var dateEndUtc = Date.UTC(dateEnd.getFullYear(), dateEnd.getMonth(), dateEnd.getDate(), parseInt(dateEnd.getHours()), parseInt(dateEnd.getMinutes()));
            dDouArr.push([dateEndUtc, null]);

            xAxisTemp = {
                type: 'datetime',
                dateTimeLabelFormats: { // don't display the dummy year
                    minute: '%H:%M',
                    hour: '%H:%M',
                    day: '%b-%e',
                    week: '%b-%e',
                    month: '%y-%b'
                },
                tickmarkPlacement: 'on',
                gridLineColor:Highcharts.theme.areasplinegridLineColor,
	            lineColor:Highcharts.theme.areasplinelineColor,
	            tickColor:Highcharts.theme.areasplinetickColor,
                tickWidth: 1,
                labels: {
                    style: {
                    	color:Highcharts.theme.areasplinelabelsColor
                    }
                },
            };
            if (yAxisMaxValue == 0) {
                yAxisMaxValue = 100;
            }
            yAxisTemp = {
                /*BUG #41379 业务管理：业务详情-指标一览中响应速度坐标轴中出现负数坐标 huangping 2017/7/6 start*/
                max: yAxisMaxValue,
                min: yAxisMinValue,
                /*BUG #41379 业务管理：业务详情-指标一览中响应速度坐标轴中出现负数坐标 huangping 2017/7/6 end*/
                gridLineWidth: 0,
                lineColor:Highcharts.theme.areasplinelineColor,
	            gridLineColor:Highcharts.theme.areasplinegridLineColor,
	            tickColor:Highcharts.theme.areasplinetickColor,
                title: {
                    enabled: false
                },
                labels: {
                    style: {
                    	color:Highcharts.theme.areasplinelabelsColor
                    }
                },
            };

            tooltip = {
                enabled: true,
                formatter: function () {
                    //highchart BUG 该时间比当前时间快8小时
                    var dateTool = new Date(this.x - 8 * 3600 * 1000);
                    var valueNum = this.y;
                    var alertStr = valueNum + data.unit;

                    return '[' + dateTool.getFullYear() + '-' + (dateTool.getMonth() + 1) + '-' + dateTool.getDate() + ' ' +
                        (dateTool.getHours()) + ':' + (dateTool.getMinutes() < 10 ? '0' + dateTool.getMinutes() : dateTool.getMinutes()) + ':00] : ' + alertStr;
                }
            };

            return {
                xAxisTemp: xAxisTemp,
                yAxisTemp: yAxisTemp,
                tooltip: tooltip,
                seriesData: seriesDatas
            };

        },
        renderChart: function (dataObj) {
            var that = this;
            var legend = {enabled: false};
            var markerShowFlag = true;
//			if("1H" == queryTimeType){
//				markerShowFlag = true;
//			}

            Highcharts.setOptions({
                lang: {
                    shortMonths: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
                    weekdays: ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
                }
            });
            that.selector.find('#showMetricCharts').highcharts({
                chart: {
                    type: 'spline',
//                    backgroundColor: 'rgba(0,0,0,0)'
                },
                title: null
                ,
                xAxis: dataObj.xAxisTemp,
                yAxis: dataObj.yAxisTemp,
                colors: ["#2894FF", "#f96e03", "#55BF3B", "#DF5353", "#aaeeee", "#ff0066", "#eeaaee",
                    "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
                credits: {
                    enabled: false
                },
                exporting: {
                    enabled: false
                },
                legend: legend,
                plotOptions: {
                    series: {
                        marker: {
                            enabled: markerShowFlag
                        }
                    },
                    line: {
                        lineWidth: 1
                    }
                },
                tooltip: dataObj.tooltip,
                series: dataObj.seriesData
            });

        },
        randomQueryDlg: function () {
            var that = this;

            var dialogSetIds = $('<div/>');
            dialogSetIds.dialog({
                title: '自定义时间',
                width: 300,
                height: 156,
                buttons: [{
                    text: '</span>确定',
                    iconCls: "fa fa-check-circle",
                    handler: function () {
                        var dateStartStr = dialogSetIds.find('#dateStartSelect').datetimebox('getValue');
                        var dateEndStr = dialogSetIds.find('#dateEndSelect').datetimebox('getValue');

                        if (dateStartStr == '' || dateEndStr == '') {
                            alert('日期输入不能为空！');
                            return false;
                        }

                        var dateStart = new Date(dateStartStr.replace(/-/g, "/"));
                        var dateEnd = new Date(dateEndStr.replace(/-/g, "/"));

                        var timeSub = dateEnd.getTime() - dateStart.getTime();
                        if (timeSub < 0 || timeSub == 0) {
                            alert('开始日期应在结束日期之前！');
                            return false;
                        } else if (timeSub > 60 * 24 * 60 * 60 * 1000) {
                            alert('开始日期,结束日期时间间隔不能大于60天！');
                            return false;
                        } else {
                            destroy = dialogSetIds.dialog('destroy');

                            var dayNum = 24 * 60 * 60 * 1000;
                            var halfHourPosition = 1 * dayNum;
                            var hourPosition = 15 * dayNum;
                            var sixHourPosition = 60 * dayNum;

                            var param = that.param;

                            if (timeSub > halfHourPosition && timeSub < hourPosition) {
                                param['timeType'] = "halfHour";
                            } else if (timeSub > (hourPosition - 1) && timeSub < sixHourPosition) {
                                param['timeType'] = "hour";
                            } else if (timeSub > (sixHourPosition - 1)) {
                                param['timeType'] = "sixHour";
                            }

                            //set query time param
                            param['startTime'] = dateStart.getTime();
                            param['endTime'] = dateEnd.getTime();
                            that.createChart();
                        }
                    }
                }, {
                    text: '取消',
                    iconCls: "fa fa-times-circle",
                    handler: function () {
                        dialogSetIds.dialog('destroy');
                    }
                }]
            });
            //弹出日期选择框
            var form = $('<form/>').addClass('oc-form').attr('id', 'dateForm');
            var dateStartDiv = $('<div/>').addClass('margin10');
            var dateStartLabel = $('<label/>').html('开始时间 : ');
            var dateStartSelect = $('<select/>').attr('name', 'dateSelect').attr('id', 'dateStartSelect');
            dateStartDiv.append(dateStartLabel);
            dateStartDiv.append(dateStartSelect);
            form.append(dateStartDiv);

            var dateEndDiv = $('<div/>').addClass('margin10');
            var dateEndLabel = $('<label/>').html('结束时间 : ');
            var dateEndSelect = $('<select/>').attr('name', 'dateSelect').attr('id', 'dateEndSelect');
            dateEndDiv.append(dateEndLabel);
            dateEndDiv.append(dateEndSelect);
            form.append(dateEndDiv);
            dialogSetIds.append(form);

            var myForm = dialogSetIds.find('#dateForm');

            oc.ui.form({
                selector: myForm,
                datetimebox: [{
                    selector: '[name=dateSelect]'
                }]
            });
        },
        unitTransform: function (value, unit) {
            return unitTransform(value, unit);
        },
        formatDate: function (date) {
            return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + '  ' + (date.getHours() < 10 ? '0' + String(date.getHours()) : String(date.getHours())) + ':' + (date.getMinutes() < 10 ? '0' + String(date.getMinutes()) : String(date.getMinutes())) + ':' + (date.getSeconds() < 10 ? '0' + String(date.getSeconds()) : String(date.getSeconds()));
        }
    }
    //-- 单位转换方法开始 --
    function unitTransform(value, unit) {
        var str;
        var valueTemp;
        if (null == value) {
            return '--';
        }
        if (isNaN(value)) {
            valueTemp = new Number(value.trim());
        } else {
            valueTemp = new Number(value);
        }

        if (isNaN(valueTemp)) {
            return value + unit;
        }
        switch (unit) {
            case "ms":
            case "毫秒":
                str = transformMillisecond(valueTemp);
                break;
            case "s":
            case "秒":
                str = transformSecond(valueTemp);
                break;
            case "Bps":
                str = transformByteps(valueTemp);
                break;
            case "bps":
            case "比特/秒":
                str = transformBitps(valueTemp);
                break;
            case "KB/s":
                str = transformKBs(valueTemp);
                break;
            case "Byte":
                str = transformByte(valueTemp);
                break;
            case "KB":
                str = transformKB(valueTemp);
                break;
            case "MB":
                str = transformMb(valueTemp);
                break;
            default:
                str = value + unit;
                break;
        }
        return str;

    }

    function transformMillisecond(milliseconds) {
        var millisecond = milliseconds;
        var seconds = 0, second = 0;
        var minutes = 0, minute = 0;
        var hours = 0, hour = 0;
        var day = 0;
        if (milliseconds > 1000) {
            millisecond = milliseconds % 1000;
            second = seconds = (milliseconds - millisecond) / 1000;
        }
        if (seconds > 60) {
            second = seconds % 60;
            minute = minutes = (seconds - second) / 60;
        }
        if (minutes > 60) {
            minute = minutes % 60;
            hour = hours = (minutes - minute) / 60;
        }
        if (hours > 24) {
            hour = hours % 24;
            day = (hours - hour) / 24;
        }
        var sb = "";
        sb = day > 0 ? (sb += (day + "天")) : sb;
        sb = hour > 0 ? (sb += (hour + "小时")) : sb;
        sb = minute > 0 ? (sb += (minute + "分")) : sb;
        sb = second > 0 ? (sb += (second + "秒")) : sb;
        sb = millisecond > 0 ? (sb += (millisecond + "毫秒")) : sb;
        sb = "" == sb ? (sb += (millisecond + "毫秒")) : sb;
        return sb;
    }

    function transformSecond(seconds) {
        var second = seconds;
        var minutes = 0, minute = 0;
        var hours = 0, hour = 0;
        var day = 0;
        if (seconds > 60) {
            second = seconds % 60;
            minute = minutes = (seconds - second) / 60;
        }
        if (minutes > 60) {
            minute = minutes % 60;
            hour = hours = (minutes - minute) / 60;
        }
        if (hours > 24) {
            hour = hours % 24;
            day = (hours - hour) / 24;
        }
        var sb = "";
        sb = day > 0 ? (sb += (day + "天")) : sb;
        sb = hour > 0 ? (sb += (hour + "小时")) : sb;
        sb = minute > 0 ? (sb += (minute + "分")) : sb;
        sb = second > 0 ? (sb += (second + "秒")) : sb;
        sb = "" == sb.toString() ? (sb += (second + "秒")) : sb;
        return sb;
    }

    function transformByteps(bpsNum) {
        var sb = "";
        var precision = 2;
        if (bpsNum > 1000 * 1000 * 1000) {
            sb += (bpsNum / (1000 * 1000 * 1000)).toFixed(precision) + "GBps";
        } else if (bpsNum > 1000 * 1000) {
            sb += (bpsNum / (1000 * 1000)).toFixed(precision) + "MBps";
        } else if (bpsNum > 1000) {
            sb += (bpsNum / 1000).toFixed(precision) + "KBps";
        } else {
            sb += bpsNum.toFixed(precision) + "Bps";
        }
        return sb;
    }

    function transformBitps(bpsNum) {
        var sb = "";
        var precision = 2;
        if (bpsNum > 1000 * 1000 * 1000) {
            sb += (bpsNum / (1000 * 1000 * 1000)).toFixed(precision) + "Gbps";
        } else if (bpsNum > 1000 * 1000) {
            sb += (bpsNum / (1000 * 1000)).toFixed(precision) + "Mbps";
        } else if (bpsNum > 1000) {
            sb += (bpsNum / 1000).toFixed(precision) + "Kbps";
        } else {
            sb += bpsNum.toFixed(precision) + "bps";
        }
        return sb;
    }

    function transformKBs(KBsNum) {
        var sb = "";
        var precision = 2;
        if (KBsNum > 1024 * 1024) {
            sb += (KBsNum / (1024 * 1024)).toFixed(precision) + "GB/s";
        } else if (KBsNum > 1024) {
            sb += (KBsNum / 1024).toFixed(precision) + "MB/s";
        } else {
            sb += KBsNum.toFixed(precision) + "KB/s";
        }
        return sb;
    }

    function transformByte(byteNum) {
        var sb = "";
        var precision = 2;

        if (byteNum > 1024 * 1024 * 1024) {
            sb += (byteNum / (1024 * 1024 * 1024)).toFixed(precision) + "GB";
        } else if (byteNum > 1024 * 1024) {
            sb += (byteNum / (1024 * 1024)).toFixed(precision) + "MB";
        } else if (byteNum > 1024) {
            sb += (byteNum / 1024).toFixed(precision) + "KB";
        } else {
            sb += byteNum.toFixed(precision) + "Byte";
        }
        return sb;
    }

    function transformKB(KBNum) {
        var sb = "";
        var precision = 2;

        if (KBNum > 1024 * 1024) {
            sb += (KBNum / (1024 * 1024)).toFixed(precision) + "GB";
        } else if (KBNum > 1024) {
            sb += (KBNum / 1024).toFixed(precision) + "MB";
        } else {
            sb += KBNum.toFixed(precision) + "KB";
        }
        return sb;
    }

    function transformMb(mbNum) {
        var sb = "";
        var precision = 2;

        if (mbNum > 1024) {
            sb += (mbNum / 1024).toFixed(precision) + "GB";
        } else {
            sb += mbNum.toFixed(precision) + "MB";
        }
        return sb;
    }

    //-- 单位转换方法结束 --

    oc.ns('oc.module.biz.bizchart');
    oc.module.biz.bizchart = {
        open: function (cfg) {
            var bizchart = new bizChart(cfg);
            bizchart.init();
        }
    };
})()