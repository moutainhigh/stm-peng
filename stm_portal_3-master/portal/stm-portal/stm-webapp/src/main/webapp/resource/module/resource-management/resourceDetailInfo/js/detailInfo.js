$(function () {
    var hand = false;
    var childResourceHtml = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/generalHostShowMetric.html');
    var childResourceJs = 'resource/module/resource-management/resourceDetailInfo/js/generalHostShowMetric.js';
    var skin = Highcharts.theme.currentSkin;

    function detailInfo(cfg) {
        this.cfg = $.extend({}, this._defaults, cfg);
    }

    detailInfo.prototype = {
        constructor: detailInfo,
        cfg: undefined, // 外界传入的配置参数 json对象格式
        dlg: undefined,
        dlgDom: undefined,
        tabs: undefined,
        tabsDom: undefined,
        childrenType: undefined,
        timeId: undefined,
        resourceInfo: undefined,
        isHandle: false,
        _defaults: {},
        open: function () {
            var that = this;
            // 查询资源详细信息
            oc.util.ajax({
                url: oc.resource.getUrl('portal/resource/resourceDetailInfo/getResourceInfo.htm'),
                successMsg: null,
                data: {
                    instanceId: this.cfg.instanceId
                },
                success: function (json) {
                    if (json.code == 200) {
                        that.resourceInfo = json.data;
                        if (that.resourceInfo.lifeState == 'MONITORED') {
                            if (that.resourceInfo.resourceType == 'StandardService') {
                                that._initStandardServiceInfo(that.resourceInfo);
                            } else {
                                that._initResourceDetailInfo(that.resourceInfo);
                            }
                            //采集状态不可用
                            var CollectState = that.resourceInfo.CollectState;
                            if (CollectState && CollectState == 'UNCOLLECTIBLE') {
//							if(true){
                                that._cannotCollectDialog();
                            }

                        } else {
                            alert('该资源当前不是监控状态');
                        }
                    } else {
                        alert('没有查询到对应的资源信息');
                        return false;
                    }
                }
            });
            //	hand=	that._initIsHandle();
            //	console.info("this.isHandle"+hand);
        },
        _initIsHandle: function () {
        },
        _cannotCollectDialog: function () {
        },
        _initStandardServiceInfo: function () {
            var that = this;
            oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/js/dialog_standard_application.js', function () {
                oc.resource.management.dialog.standard.application.open({
                    instanceId: that.resourceInfo.instanceId,
                    callback: that.cfg.callback
                });
            });
        },
        _initResourceDetailInfo: function () {
            var that = this;
            // 子资源分类
            this.dlgDom = $("<div/>");
            this.tabsDom = $("<div/>");
            var content = $("<div/>").addClass("main-resource");/*.append("<div class='tabPanel' id='all' onclick=showAndHide()></div>");*/
//			this.tabsDom.append('<div class="cediv"></div>')
            this.dlgDom.append(this.tabsDom);
            this.dlgDom.append(content);
            // 新建窗口
            var windowHeight = $(window).height(), dlgHeight = 630, dlgWidth = 1108;
            var dlgTop = (windowHeight != undefined && windowHeight != null && windowHeight > dlgHeight) ? null : 0;
            var title = that.resourceInfo.name.htmlspecialchars() + (this.resourceInfo.CollectState == "UNCOLLECTIBLE" ? "<span class='panel_pointsa'>（该设备无法采集，以下为快照信息！）</span>" : "") + ("<div class='titlePanel'></div>");

            this.dlg = this.dlgDom.dialog({
                title: title,
                width: dlgWidth,
                height: dlgHeight,
                top: dlgTop,
                onClose: function () {
                    that.dlg.panel('destroy');

                },
                onLoad: function () {
                    console.info(this.dlg.find(".panel-header"));

                }
            });
            $(".panel-header.panel-header-noborder.window-header").addClass("resource-panel-head");
            // 新建tabs
            this._initTabs(this.dlg);
            //
            // 定时提示器
            this.setRefreshInterval();
        },
        _initTabs: function (dlg) {
            var that = this;
            this.childrenType = that.resourceInfo.childrenType;
            //<ul class="nav-list" id="menuList"></ul>
            var navehtml = '<div class="oc-nav-menu"><div>';

            //	this.tabsDom.append(navehtml);
            $(".titlePanel").append(navehtml);
            // 对主机的子资源进行排序操作
            if (that.resourceInfo.resourceType == 'Host') {
                for (var index in this.childrenType) {
                    var child = this.childrenType[index];
                    switch (child.type) {
                        case 'CPU':
                            child.No = 1;
                            break;
                        case 'Partition':
                            child.No = 2;
                            break;
                        case 'HardDisk':
                            child.No = 3;
                            break;
                        case 'NetInterface':
                            child.No = 4;
                            break;
                        case 'Process':
                            child.No = 5;
                            break;
                        case 'File':
                            child.No = 6;
                            break;
                        case 'LogicalVolume':
                            child.No = 7;
                            break;
                        case 'VolumeGroup':
                            child.No = 8;
                            break;
                        default:
                            child.No = 999;
                            break;
                    }
                }
                this.childrenType = this.childrenType.sort(function (a, b) {
                    return parseInt(a.No) - parseInt(b.No);
                });
            }
            var obj = {
                No: 0,
                id: 'all',
                name: '资源总览',
                type: 'all',
                imgUrl: '/mainsteam-stm-webapp/resource/themes/' + skin + '/images/default.png'
            };
            //	datas[0]=obj;
            var dataArr = new Array();
            dataArr.push(obj);
            for (var i = 0; i < this.childrenType.length; i++) {
                dataArr.push(this.childrenType[i]);
            }


            var nav = oc.index.nav = $(".oc-nav-menu").oc_nav({
                textField: 'name',
                hrefFiled: 'url',
                click: function (href, d, ds) {

                    var contentDiv = "";
                    var url = "";
                    var jsFile = "";
                    //console.info(d.type);
                    switch (d.type) {
                        case 'all' :
                            //	console.info("init all");
                            url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/general.html');
                            // 'resource/module/resource-management/resourceDetailInfo/js/general.js'
                            jsFile = 'resource/module/resource-management/resourceDetailInfo/js/general.js';
                        case 'Process':
                            url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/ProcessMetricData.html');
                            jsFile = 'resource/module/resource-management/resourceDetailInfo/js/ProcessMetricData.js';
                            break;
                        case 'File':
                            url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/fileMetricData.html');
                            jsFile = 'resource/module/resource-management/resourceDetailInfo/js/fileMetricData.js';
                            break;
                        case 'LogicalVolume':
                            url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/logicalVolume.html');
                            jsFile = 'resource/module/resource-management/resourceDetailInfo/js/logicalVolume.js';
                            break;
                        case 'VolumeGroup':
                            url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/volumeGroup.html');
                            jsFile = 'resource/module/resource-management/resourceDetailInfo/js/volumeGroup.js';
                            break;
                        default:
                            url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/generalHostShowMetric.html');
                            jsFile = 'resource/module/resource-management/resourceDetailInfo/js/generalHostShowMetric.js';
                            break;
                    }
                    if (d.type == 'all') {
                        url = oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/general.html');
                        // 'resource/module/resource-management/resourceDetailInfo/js/general.js'
                        jsFile = 'resource/module/resource-management/resourceDetailInfo/js/general.js';
                        console.info(jsFile);
                        console.info(url);
                    }

                    var arg = {
                        type: d.type,
                        title: d.name,
                        html: url,
                        js: jsFile,
                        instanceId: that.resourceInfo.instanceId,
                        availability: that.resourceInfo.availability,
                        content: $("div[id='" + d.type + "']"),
                        isLoad: false,
                        resourceType: that.resourceInfo.resourceType,
                        resourceInfo: that.resourceInfo,
                        instanceStatus: that.cfg.instanceStatus
                    }
                    $(".tabPanel").each(function (index, value) {
                        if ($(this).attr("id") == d.type) {
                            $(this).attr("style", "display:block");
                            $(this).parent().attr("style", "float:left;");

                            that.loadChildPanelSingle(arg, index);

                        } else {
                            $(this).attr("style", "display:none");
                            $(this).parent().attr("style", "float:left;");

                        }


                    });
                    $("div[id='" + d.type + "']").attr("style", 'display: block;');

                },
                background: true,
                filter: function (datas) {

                    var args = new Array();
                    for (var i = 0, len = datas.length, d; i < len; i++) {
                        d = datas[i];
                        console.info(d);
                        //	/mainsteam-stm-webapp/resource/themes/'+skin+'/images/default.png
                        d.imgUrl = oc.resource.getUrl('resource/themes/' + skin + '/images/default.png');
                        $(".main-resource").append("<div class='tabPanel' style='display:none' id='" + d.type + "'></div>");


                    }

                    return datas;

                },
                datas: dataArr
            });
            nav._ul.find("li").each(function () {
                //$(this).css("float","left");
            });
            nav._ul.find('li:first').click();
            var spans = $(".nav-page span");
            spans.eq(0).addClass('fa fa-sort-up');
            spans.eq(0).removeClass('disabled');
            spans.eq(1).addClass('fa fa-sort-down');

            //	spans.eq(0).addClass('fa fa-sort-up');
            //	spans.eq(1).addClass('fa fa-sort-down');

        },
        //onclick="collectResource()"
        setRefreshInterval: function () {
        },
        loadChildPanelSingle: function (args, currentNo) {
            var arg = args;//[currentNo];
            if (!arg.isLoad)
                arg.content.panel({
                    href: arg.html,
                    onLoad: function () {
                        console.info(arg.js);
                        oc.resource.loadScript(arg.js, function () {
                            switch (arg.type) {
                                case 'all' :
                                    oc.module.resmanagement.resdeatilinfo.general.open(arg.resourceInfo, arg.instanceStatus);
                                    break;
                                case 'vm':
                                    oc.resourced.resource.detail.showmetric.openVMResource(arg.instanceId, arg.resourceId, arg.title, arg.availability, arg.vminstanceIdList);
                                    break;
                                case 'Process':
                                    oc.resourced.resource.detail.process.open(arg.instanceId, arg.availability);
                                    break;
                                case 'File':
                                    oc.resourced.resource.detail.file.open(arg.instanceId, arg.availability);
                                    break;
                                case 'LogicalVolume':
                                    oc.resourced.resource.detail.logicalvolume.open(arg.instanceId, arg.availability);
                                    break;
                                case 'VolumeGroup':
                                    oc.resourced.resource.detail.volumegroup.open(arg.instanceId, arg.availability);
                                    break;
                                default:
                                    oc.resourced.resource.detail.showmetric.open(arg.instanceId, arg.type, arg.title, arg.availability, arg.resourceType);
                                    break;
                            }
                            arg.isLoad = true;
                        });
                    }
                });


        },
        changeTabsHeadHeight: function () {
        },
        updateDlgTitle: function (newShowName) {

            this.dlgDom.dialog('setTitle', "名称：" + newShowName.htmlspecialchars() + (this.resourceInfo.CollectState == "UNCOLLECTIBLE" ? "<span class='panel_pointsa'>（该设备无法采集，以下为快照信息！）</span>" : ""));

        }
    }
    oc.ns('oc.module.resmanagement.resdeatilinfo');
    var detail = undefined;
    oc.module.resmanagement.resdeatilinfo = {
        open: function (cfg) {
            detail = new detailInfo(cfg);
            detail.open();
        },
        updateDlgTitle: function (newShowName) {
            if (detail != undefined) {
                detail.updateDlgTitle(newShowName);
            }
        }
    }
});

function showAndHide() {
}