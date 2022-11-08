function TopoLinkInfo(args) {
    this.args = $.extend({
        onLoad: function () {
        },
        dialog: true,
        parent: null,
        type: null,
        data: []
    }, args);
    var ctx = this;
    oc.util.ajax({
        url: "module/topo/contextMenu/TopoLinkInfo.html",
        dataType: "html",
        type: "get",
        success: function (html) {
            ctx.init(html);
        }
    });
};
TopoLinkInfo.prototype = {
    init: function (html) {
        var ctx = this;
        this.root = $(html);
        if (this.args.dialog) {//如果展现是一个对话框
            this.root.dialog({
                width: 1260, height: 595,
                title: "链路详细信息",
                buttons: [{
                    text: "确定", handler: function () {
                        ctx.root.dialog("close");
                    }
                }, {
                    text: "关闭", handler: function () {
                        ctx.root.dialog("close");
                    }
                }]
            });
        }
        if (this.args.parent) {
            this.args.parent.append(this.root);
        }
        this.fields = {};
        this.root.find("[data-field]").each(function (idx, dom) {
            var tmp = $(dom);
            ctx.fields[tmp.attr("data-field")] = tmp;
        });
        this.args.onLoad.call(this);//调用回调
        //初始化表格
        this.fields.grid.datagrid({
            loadFilter: function (d) {
                if (d instanceof Array) {
                    return {total: d.length, rows: d};
                } else {
                    return {total: 0, rows: []};
                }
            },
            data: [],
            pagination: false,
            columns: [[
                {field: 'id', hidden: true},
                {field: 'instanceId', hidden: true},
                {field: 'srcMainInstIP', title: '源IP地址', sortable: true, ellipsis: true, width: 90},
                {field: 'destMainInstIP', title: '目的IP地址', sortable: true, ellipsis: true, width: 90},
                {
                    field: 'srcMainInstName',
                    title: '源接口',
                    ellipsis: true,
                    width: 110,
                    formatter: function (value, row, index) {
                        return "<span class='if if-" + row.srcIfColor + "' title='" + value + "'>" + value + "</span>";
                    }
                },
                {
                    field: 'destMainInsName',
                    title: '目的接口',
                    ellipsis: true,
                    width: 110,
                    formatter: function (value, row, index) {
                        return "<span class='if if-" + row.destIfColor + "' title='" + value + "'>" + value + "</span>";
                    }
                },
                {
                    field: 'insStatus', title: '状态', width: 80, formatter: function (value, row, index) {
                    var tmp = ("disabled" == value) ? '<span>×</span>' : '';
                    return '<span class="topo-line line-' + value + '" >' + tmp + '</span>';
                }
                },
                {field: 'getValInterface', title: '取值接口', width: 100},
                {field: 'downDirect', title: '下行方向', width: 100},
                {
                    field: 'ifOutOctetsSpeed', title: '上行流量', width: 90, formatter: function (value, row, index) {
                    return ctx._formatter(value, row, index, "ifOutOctetsSpeed");
                }
                },
                {
                    field: 'ifInOctetsSpeed', title: '下行流量', width: 90, formatter: function (value, row, index) {
                    return ctx._formatter(value, row, index, "ifInOctetsSpeed");
                }
                },
                {
                    field: 'ifOutBandWidthUtil', title: '上行带宽利用率', width: 110, formatter: function (value, row, index) {
                    return ctx._formatter(value, row, index, "ifOutBandWidthUtil");
                }
                },
                {
                    field: 'ifInBandWidthUtil', title: '下行带宽利用率', width: 110, formatter: function (value, row, index) {
                    return ctx._formatter(value, row, index, "ifInBandWidthUtil");
                }
                },
                {
                    field: 'bandWidth', title: '链路带宽', width: 130, formatter: function (value, row, index) {
                    return value == null ? "- -" : value
                }
                }
                /*资源管理中的链路详情页面调用拓扑管理中的链路详情页面，屏蔽拓扑链路详情中的操作列。huangping 2017/6/26 start*/
                /*
                 {field:'opration',title:'操作',halign:'center',width:82,formatter: function(value,row,index){
                 return '<span class="icon-edit "></span>';
                 }}*/
                /*资源管理中的链路详情页面调用拓扑管理中的链路详情页面，屏蔽拓扑链路详情中的操作列。huangping 2017/6/26 end*/
            ]],
            onClickCell: function (rowIndex, field, value) {
                if (field == "opration") {
                    var row = ctx.fields.grid.datagrid("getRows")[rowIndex];
                    var link = {d: {instanceId: row.instanceId}};
                    oc.resource.loadScript("resource/module/topo/contextMenu/EditLink.js", function () {
                        new EditLinkDia(link);
                    });
                }

            }
        });

    },
    _formatter: function (value, row, index, metricId) {
        var retn = value == null ? "- -" : value;
        if (this.args.type == "map") {
            if (retn != "- -") {
                var span = $("<span/>");
                span.attr({
                    metricId: metricId,
                    instanceId: row.instanceId,
                    "class": "ico ico-chart margin-top-seven show_chartsNetInterface"
                });
                span.text(retn);
                span.addClass("topo_link_cell");
                return span.get(0);
            }
        }
        return retn;
    },
    load: function (ids, type) {
        var ctx = this;
        oc.util.ajax({
            url: oc.resource.getUrl("topo/instanceTable/link/list.htm"),
            data: {resourceIds: ids, type: type},
            successMsg: null,
            success: function (data) {
                ctx.fields.grid.datagrid("loadData", data.datas);
            }
        });
    },
    /********************
     * 图标tab显示隐藏
     *******************/
    showHideChartMethod: function (rootDiv, datagridDiv, chartDiv, tabTitleDiv, showType) {
        var rootDivHeight = rootDiv.height();
        //显示图标时,datagridDiv高度
        var showMetricDatagridDivHeight = rootDivHeight * 0.52;
        var tabTitleHeight = tabTitleDiv.parent().height();

        //有showType表示点击小图标展示线图
        if (showType && 'show' == showType) {

        } else {
            showMetricDatagridDivHeight = rootDivHeight;//- tabTitleHeight;
        }

        //表格和图标高度控制
        datagridDiv.height(showMetricDatagridDivHeight);
        var showMetricDatagridParentDiv = datagridDiv.find('#showMetricDatagridParent');
        showMetricDatagridParentDiv.height(showMetricDatagridDivHeight - tabTitleHeight - 15);
        chartDiv.height(rootDivHeight - showMetricDatagridDivHeight + 15);

        var headerHeight = showMetricDatagridParentDiv.find('.datagrid-header').height();
        showMetricDatagridParentDiv.find('.datagrid-body').each(function (e) {
            var targetTemp = $(this);
            targetTemp.height(showMetricDatagridDivHeight - tabTitleHeight - 15 - headerHeight);
        });
    }
};