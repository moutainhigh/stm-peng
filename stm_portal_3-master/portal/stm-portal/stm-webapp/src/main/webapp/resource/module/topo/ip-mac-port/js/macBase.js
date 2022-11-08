$(function () {
    var mainDiv = $('#standardContainer');	//mainDIV
    var datagridDiv = mainDiv.find('#standardDatagrid');	//数据表格div
    var searchForm = null;
    var datagrid = null;
    var baseUrl = "topo/mac/base/list.htm";

    intitSearchForm();
    initDataGrid();

    //查询数据
    function searchMacBase() {
        var searchType = $("#baseSearchType").combobox('getValue');
        if (!searchType) {
            alert("请选择类型", "danger");
            return;
        }
        var searchVal = $("#baseSearchVal").val();
        var options = $("#standardDatagrid").datagrid("options");
        options.queryParams = {searchType: searchType, searchVal: searchVal};
        $("#standardDatagrid").datagrid("load");
    }

    //查询按钮绑定Enter查询事件
    $("[id=baseSearchVal]").keyup(function (e) {
        if (e.keyCode == 13) {
            searchMacBase();
        }
    });

    //渲染form
    function intitSearchForm() {
        //渲染条件查询表格
        searchForm = mainDiv.find('#baseSearchForm');
        var conditionForm = oc.ui.form({
            selector: searchForm,
            combobox: [{	//渲染下拉框
                selector: '[id="baseSearchType"]',
                onChange: function (val) {
                },
                data: [
                    {id: '1', name: 'IP地址'},
                    {id: '2', name: 'MAC地址'},
                    {id: '3', name: '上联设备IP'}
                ]
            }]
        });
    }

    //渲染表格
    function initDataGrid() {
        datagrid = oc.ui.datagrid({
            selector: datagridDiv,
            queryForm: null,
            hideReset: true,
            hideSearch: true,
            url: oc.resource.getUrl(baseUrl),
            singleSelect: false,	//是否单行选中
            octoolbar: {
                left: [searchForm],
                right: [{
                    iconCls: 'icon-search',
                    text: "查询",
                    onClick: function () {
                        searchMacBase();
                    }
                }, "&nbsp;", {
                    iconCls: 'fa fa-plus',
                    text: "添加",
                    onClick: function () {
                        openAddBaseDlg();
                    }
                }, "&nbsp;", {
                    iconCls: "fa fa-trash-o",
                    text: "删除",
                    onClick: function () {
                        var ids = datagrid.getSelectIds();
                        if (ids.length == 0) {
                            alert('请至少选择一条数据', 'danger');
                            return;
                        }
                        oc.ui.confirm('是否删除选中数据？', function () {
                            oc.util.ajax({
                                url: oc.resource.getUrl('topo/mac/base/del.htm'),
                                type: 'POST',
                                dataType: "json",
                                data: {ids: ids.join(",")},
                                successMsg: null,
                                success: function (data) {
                                    alert(data.data);
                                    datagrid.reLoad();
                                }
                            });
                        });
                    }
                }, "&nbsp;", {
                    iconCls: "fa fa-trash-o",
                    text: "清空",
                    onClick: function () {
                        oc.util.ajax({
                            url: oc.resource.getUrl(baseUrl),
                            success: function (result) {
                                if (result.data.datas.length > 0) {
                                    oc.ui.confirm('是否清空所有数据？', function () {
                                        oc.util.ajax({
                                            url: oc.resource.getUrl('topo/mac/base/del.htm'),
                                            type: 'POST',
                                            dataType: "json",
                                            successMsg: null,
                                            success: function (data) {
                                                alert(data.data);
                                                datagrid.reLoad();
                                            }
                                        });
                                    });
                                } else {
                                    alert("没有可清空的数据", "warning");
                                }
                            }
                        });
                    }
                }, "&nbsp;", {
                    iconCls: "fa fa-sign-in",
                    text: "导入",
                    onClick: function () {
                        var ie = new ImportExcelDia();
                        ie.on("success", function () {
                            //成功后续操作
                            datagrid.reLoad();
                        });
                    }
                }, "&nbsp;", {
                    iconCls: "fa fa-sign-out",
                    text: "导出",
                    onClick: function () {
                        var ids = datagrid.getSelectIds();
                        if (ids.length == 0) {
                            alert("请选择至少一条数据", "danger");
                            return;
                        }
                        var ef = new ExportFile();
                        ef.exportFile(oc.resource.getUrl("topo/mac/export.htm?exportType=selected&ids=" + ids));
                    }
                }, "&nbsp;", {
                    iconCls: "fa fa-sign-out",
                    text: "全部导出",
                    onClick: function () {
                        var ef = new ExportFile();
                        ef.exportFile(oc.resource.getUrl("topo/mac/export.htm?exportType=all"));
                    }
                }]
            },
            columns: [[
                {field: 'id', checkbox: true},
                {field: 'hostName', title: '主机名称', ellipsis: true, width: 120},
                {field: 'mac', title: 'MAC', sortable: true, width: 120},
                {
                    field: 'ip',
                    title: 'IP地址',
                    ellipsis: true,
                    sortable: true,
                    width: 120,
                    formatter: function (value, row, index) {
                        var formatIp = new FormatIp();
                        return formatIp.formatter(value);
                    }
                },
                {field: 'upDeviceName', title: '上联设备名称', width: 120},
                {
                    field: 'upDeviceIp', title: '上联设备IP', width: 130, formatter: function (value, row, index) {
                    return formatBaseSelect(value);
                }
                },
                {
                    field: 'upRemarks', title: '上联备注', width: 120, formatter: function (value, row, rowIndex) {
                    if (value == null || value == "") {
                        value = "";
                    }
                    return '<div id="upRemarks-show-' + rowIndex + '" style="width:100%;position:relative;"><span markId="upRemarksSpan" name="upRemarks-' + rowIndex + '" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;" class="res_unknown_nothing oc-datagrid-spanwdth" title="' + value + '">' + value + '</span><span name="edit-' + rowIndex + '" class="uodatethetext fa fa-edit" style="display:none;" title="编辑" index=' + rowIndex + '></span></div>' +
                        '<div id="upRemarks-edit-' + rowIndex + '" style="width:100%;display:none;position:relative;"><input markId="upRemarksInput" type="text" value="' + value + '" instanceId="' + row.id + '" class="validatebox-box"/><span class="uodatethetextok fa fa-check" title="确认修改" index=' + rowIndex + '  ></span></div>';
                }
                },
                {field: 'upDeviceInterface', title: '上联设备接口', width: 120},
                {
                    field: 'opration',
                    title: '&nbsp;&nbsp;&nbsp;&nbsp;定位',
                    align: 'center',
                    width: 30,
                    formatter: function (value, row, index) {
                        return (row.positionFlag == 1) ? '<span title="可定位" class="ico ico_position"></span>' : '<span class="ico ico_notlocate"></span>';
                    }
                }
            ]],
            onLoadSuccess: function () {
                oc.ui.combobox({
                    selector: $("select[name='upDeviceIpAddress']")
                });
                //修改资源名称浮动显示
                mainDiv.find('.datagrid-row').mouseenter(function () {
                    var target = $(this);
                    var index = target.attr('datagrid-row-index');
                    target.find('span[name=edit-' + index + ']').attr('style', 'display:block');
                });
                mainDiv.find('.datagrid-row').mouseleave(function () {
                    var target = $(this);
                    var index = target.attr('datagrid-row-index');
                    target.find('span[name=edit-' + index + ']').attr('style', 'display:none');
                });
                //修改资源显示名字
                mainDiv.find('.uodatethetext').on('click', function () {
                    var target = $(this);
                    var index = target.attr('index');

                    mainDiv.find('#upRemarks-show-' + index).css('display', 'none');
                    mainDiv.find('#upRemarks-edit-' + index).css('display', 'block');

                });
                mainDiv.find('.uodatethetextok').on('click', function () {
                    var target = $(this);
                    var index = target.attr('index');

                    var showObj = mainDiv.find('#upRemarks-show-' + index);
                    var editObj = mainDiv.find('#upRemarks-edit-' + index);
                    var showSpan = showObj.find('[name=upRemarks-' + index + ']');
                    var inputObj = editObj.find('.validatebox-box');
                    var id = inputObj.attr('instanceId');
                    var showName = showSpan.html();
                    var editName = inputObj.val();

                    if (!editName) {
                        alert('请填写备注!');
                        return;
                    } else if (editName == showName) {
                        showObj.css('display', 'block');
                        editObj.css('display', 'none');
                        return;
                    }

                    oc.util.ajax({
                        url: oc.resource.getUrl('topo/mac/updateUpRemarks.htm'),
                        data: {id: id, upRemarks: editName},
                        successMsg: null,
                        type: 'post',
                        success: function (data) {
                            if (data.data && true == data.data) {
                                showObj.css('display', 'block');
                                editObj.css('display', 'none');
                                alert("修改上联备注成功！");
                                //将修改后的名字更新至缓存及页面
                                showSpan.html(editName).attr('title', editName);
                            } else {
                                alert("修改失败,请刷新重试!");
                            }
                        }
                    });
                });
            },
            onClickCell: function (rowIndex, field, value) {
                if (field == "opration") {
                    var row = datagrid.selector.datagrid("getRows")[rowIndex];
                    if (row.positionFlag && row.positionFlag == 1) {
                        var ip = row.ip;
                        ip_mac_port_Hide();	//隐藏ip-mac-port界面
                        eve("topo.locate", null, {val: ip});
                    }
                }
            }
        });
    }

    //格式化数据为下拉框
    function formatBaseSelect(value) {
        var vals = [];
        if (value != null && value != '') {
            ips = value.split(",");
            if (ips.length > 0) {
                $.each(ips, function (index, val) {
                    vals.push({"text": val, "value": index});
                });
            }
        }
        var select = $("<select name='upDeviceIpAddress'/>");
        for (var index in vals) {
            var option = $("<option/>").html(vals[index].text);
            select.append(option);
        }
        return $("<div/>").append(select).html();
    }

    //导入基准表数据
    function importMacExcel(dia) {
        var file = $("[name=file]").val();
        var suffix = new RegExp("(.xls|.xlsx)$");
        if (suffix.test(file)) {
            var fm = $("#excelForm");
            fm.attr("action", "");
            fm.attr("action", oc.resource.getUrl("topo/mac/import.htm"));
            fm.submit();
            setTimeout(function () {
                alert("导入成功");
                dia.dialog("close");
                datagrid.reLoad();
            }, 20);
        } else {
            alert("请选择excel文档文件", "danger");
        }
    }

    //弹出添加窗口
    function openAddBaseDlg() {
        var basInfo = null;
        var dia = $("<div/>").dialog({
            title: '添加基准信息',
            href: oc.resource.getUrl("resource/module/topo/ip-mac-port/addMacBase.html"),
            width: 500,
            height: 350,
            cache: false,
            modal: true,
            buttons: [{
                text: '保存',
                handler: function () {
                    saveMacBase(datagrid, dia);
                }
            }, {
                text: '取消',
                handler: function () {
                    dia.dialog("close");
                }
            }]
        })
    }
});