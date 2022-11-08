//@ sourceURL=resource.js
$(function(){
	function resourceGrid(config){
		this.cfg = $.extend({}, this._defaults, config);
	}
	
	// 清除责任人
	function showDialogPersonClear(resourceIds, that) {
		oc.util.ajax({
			url: oc.resource.getUrl('resource/resourceMonitor/clearLiablePerson.htm'),
			data: {
				instanceIds: resourceIds.join()
			},
			success: function(d) {
				if(d.code == 200) {
					that.monitorDatagrid.selector.datagrid("reload");
					alert("保存成功");
				} else {
					alert("保存失败");
				}
			}
		});
	}

    // 编辑域
    function showDialogDomin(resourceIds) {
        var addWindow = $('<div />');
        var scanContent = $('<div id="dominGrid"/>');
        dialogTitle = '选择域';
        // 构建dialog
        addWindow.dialog({
            title : dialogTitle,
            width : 400,
            height : 400,
            content : scanContent,
            modal : true,
            buttons : [
                {
                    text : '保存',
                    iconCls : "icon-ok",
                    handler : function() {
                        var selectRow = $('#dominGrid').datagrid('getChecked');
                        //	var data = $("input[type='radio']:Checked");
                        if (selectRow && selectRow.length > 0) {
                            oc.util.ajax({
                                url : oc.resource.getUrl('resource/resourceMonitor/update_resource_domain_batch.htm'),
                                timeout : 5000,
                                data : {
                                    instanceIds :resourceIds.join(),
                                    dominId:selectRow[0].id
                                },
                                success : function(d) {
                                    if (d.code == 200) {
                                        alert("保存成功！");
                                        that.monitorDatagrid.selector.datagrid("reload");
                                    } else {
                                        alert("保存失败");
                                    }
                                }
                            });
                            addWindow.dialog('close');
                        } else {
                            alert('请选择域');
                        }
                    }

                }, {
                    text : '取消',
                    iconCls : "icon-cancel",
                    handler : function() {
                        addWindow.dialog('close');
                    }
                } ]
        });

        var dominsGrid = oc.ui.datagrid({
            selector : $('#dominGrid'),
            url : oc.resource.getUrl('system/domain/domainPage.htm'),
            fitColumns : true,
            singleSelect : true,
            pagination : false,
            columns : [ [
                {
                    field : 'id',
                    title : '<input type="radio" style="display:none;"/>',
                    width : 20,
                    formatter : function(value, rowData, rowIndex) {
                        return '<input type="radio" name="selectRadio" id="selectRadio'
                            + rowIndex
                            + '" value="'
                            + rowData.oid
                            + '" rid="'
                            + rowData.id
                            + '" rname="'
                            + rowData.name + '" />';
                    }
                }, {
                    field : 'name',
                    title : '域',
                    sortable : true,
                    width : 80
                }, {
                    field : 'description',
                    title : '备注',
                    sortable : true,
                    width : 80
                } ] ],
            onLoadSuccess : function(data) {
                var item = $('#dominGrid').datagrid('getRows'), index;
                if (item) {
                    for (var i = item.length - 1; i >= 0; i--) {
                        if (item[i].id == $("input[name='managerId']").val()) {
                            index = $('#resibilityGrid').datagrid(
                                'getRowIndex', item[i]);
                            ($("input[type='radio']")[index + 1]).checked = true;
                            break;
                        }
                    }
                }
            },
            onSelect : function(index, row) {
                $('#selectRadio' + index).attr("checked", true);
            }

        });
    }


	// 编辑责任人
	function showDialogPerson(resourceIds, that) {
		var addWindow = $('<div />');
		scanContent = $("<div><div><div class='oc-toolbar datagrid-toolbar insert-btn'><input id='queryManagerUserInput' style='margin-right:40px;width:200px;' type='text' placeholder='输入用户姓名'/>" +
			"<button id='searchManagerUserByName'><span class='l-btn-icon icon-search poas_re' style='top:5px;'></span>查询</button>" +
			"</div><div style='height:235px'><div id='resibilityGrid'/></div></div>");
		dialogTitle = '选择责任人';
		addWindow.dialog({
			title: dialogTitle,
			width: 410,
			height: 340,
			content: scanContent,
			modal: true,
			buttons: [{
				text: '保存',
				iconCls: "fa fa-check-circle",
				handler: function() {
					var selectRow = $('#resibilityGrid').datagrid('getChecked');
					if(selectRow && selectRow.length > 0) {
						oc.util.ajax({
							url: oc.resource.getUrl('resource/resourceMonitor/saveLiablePerson.htm'),
							data: {
								instanceIds: resourceIds.join(),
								userId: selectRow[0].id
							},
							success: function(d) {
								if(d.code == 200) {
									that.monitorDatagrid.selector.datagrid("reload");
									alert("保存成功");
								} else {
									alert("保存失败");
								}
							}
						});
						addWindow.dialog('close');
					} else {
						alert('请选择责任人');
					}
				}
			}, {
				text: '取消',
				iconCls: "fa fa-times-circle",
				handler: function() {
					addWindow.dialog('close');
				}
			}]
		});
	
		var resbonGrid = oc.ui.datagrid({
			selector: $('#resibilityGrid'),
			url: oc.resource.getUrl('portal/business/service/getManagerUsers.htm'),
			queryParams: {
				searchContent: ''
			},
			fitColumns: true,
			singleSelect: true,
			pagination: false,
			columns: [
				[{
					field: 'id',
					title: '<input type="radio" style="display:none;"/>',
					width: 20,
					formatter: function(value, rowData, rowIndex) {
						return '<input type="radio" name="selectRadio" id="selectRadio' +
							rowIndex + '" value="' + rowData.oid + '" rid="' +
							rowData.id + '" rname="' + rowData.name + '" />';
					}
				}, {
					field: 'account',
					title: '用户名',
					sortable: true,
					width: 80
				}, {
					field: 'name',
					title: '姓名',
					sortable: true,
					width: 80
				}]
			],
			onSelect: function(index, row) {
				$('#selectRadio' + index).attr("checked", true);
			}
		});
		$('#searchManagerUserByName').on('click', function() {
			resbonGrid.selector.datagrid('options').queryParams = {
				searchContent: $('#queryManagerUserInput').val()
			};
			resbonGrid.reLoad();
		});
	}
	
	resourceGrid.prototype = {
		constructor: resourceGrid,
		_defaults: {},
		cfg: undefined,
		tabsDiv: undefined,
		freshButton: undefined,
		toolbar: undefined,
		monitorForm: undefined,
		unmonitorForm: undefined,
		monitorDatagrid: undefined,
		unmonitorDatagrid: undefined,
		resourceStatus: undefined,
		monitorDatagridLoadFlag: undefined,
		unmonitorDatagridLoadFlag: undefined,
		init: function() {
			var that = this;
			that.tabsDiv = $("div#cameraResourceList");
			that.toolbar = that.tabsDiv.find(".camera-toolbar");
			// 添加刷新按钮
			that.addFreshButton();
			that.createResourceStatusLight();
			that.createMonitorForm();
			that.createUnmonitorForm();
			that.createMonitorDatagrid();
			that.createUnmonitorDatagrid();
		},
		addFreshButton: function() {
			var that = this;
			var tabsWrap = that.tabsDiv.find(".tabs-header > .tabs-wrap");
			var tabs = tabsWrap.find(".tabs").css("float", "left");
			that.freshButton = $("<span/>").css({
				'height': '33px',
				'float': 'right',
				'margin-right': '10px',
			}).append($("<span/>").addClass("fa fa-refresh").css("margin-top", "9px").attr("title", "刷新"));
			tabsWrap.append(that.freshButton);
			that.freshButton.find("span").on("click", function() {
				that.loadAll();
			});
		},
		addStatusLightActive: function(light) {
			$(light).parent().parent().find("span").removeClass('active');
			$(light).find("span").addClass('active');
		},
		removeStatusLightActive: function() {
			that.toolbar.find('.light span').removeClass('active');
		},
		createResourceStatusLight: function() {
			var that = this;
			that.toolbar.find(".light").each(function(i) {
				var light = $(this);
				if (light.hasClass('all')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('all');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('available')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('available');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('notavailable')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('notavailable');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('resunknown')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('resunknown');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('critical')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('critical');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('serious')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('serious');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('warn')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('warn');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('normal')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('normal');
						that.monitorDatagrid.load();
					});
				} else if (light.hasClass('unknown')) {
					light.on('click', function(){
						that.addStatusLightActive(this);
						that.resourceStatus.val('unknown');
						that.monitorDatagrid.load();
					});
				}
			});
		},
		createMonitorForm: function() {
			var that = this;
			var domains = oc.index.getDomains();
			that.monitorForm = oc.ui.form({
				selector : $("#camera_monitor_form").attr("id", oc.util.generateId()),
				combobox : [{
					selector: '[name=domainId]',
					data: domains,
					width: 90,
					placeholder: '${oc.local.ui.select.placeholder}'
				}],
			});
			that.monitorForm.selector.find(".queryBtn").linkbutton('RenderLB', {
				iconCls: 'icon-search',
				onClick: function() {
					that.monitorDatagrid.load();
				}
			});
			that.monitorForm.selector.find('.resetBtn').linkbutton('RenderLB', {
				iconCls: 'icon-reset',
				onClick: function() {
					that.removeStatusLightActive();
					that.monitorForm.reset();
				}
			});
			that.resourceStatus = that.monitorForm.selector.find("[name='resourceStatus']");
		},
		createUnmonitorForm: function() {
			var that = this;
			var domains = oc.index.getDomains();
			that.unmonitorForm = oc.ui.form({
				selector: $("#camera_unmonitor_form").attr("id", oc.util.generateId()),
				combobox: [{
					selector: "[name=domainId]",
					data: domains,
					width: 90,
					placeholder: '${oc.local.ui.select.placeholder}'
				}],
			});
			that.unmonitorForm.selector.find(".queryBtn").linkbutton('RenderLB', {
				iconCls: 'icon-search',
				onClick: function() {
					that.unmonitorDatagrid.load();
				}
			});
			that.unmonitorForm.selector.find(".resetBtn").linkbutton('RenderLB', {
				iconCls: 'icon-reset',
				onClick: function() {
					that.unmonitorDatagrid.reset();
				}
			});
		},
		createMonitorDatagrid: function() {
			var that = this;
			var user = oc.index.getUser();
			var octoolbarRight = [];
            var hiddenProfile = true;
			if(user.domainUser || user.systemUser) {
				octoolbarRight = [{
					iconCls: 'fa fa-trash-o',
					text: '删除资源',
					onClick: function() {
						that.deleteResource(that.monitorDatagrid);
					}
				}, {
					iconCls: 'fa fa-times-circle',
					text: '取消监控',
					onClick: function() {
						that.cancelMonitor();
					}
				}, {
					id: 'moreOpBtn',
					iconCls: 'fa fa-th-large',
					text: '更多操作',
					onClick: function(e) {
						that.moreOperation(e);
					}
				}].concat(octoolbarRight);

                hiddenProfile = false;
                ipAddressWidth = '15%';
			}
			that.monitorDatagrid = oc.ui.datagrid({
				selector: that.tabsDiv.find(".monitorDatagrid"),
				pageSize: $.cookie('pageSize_camera') == null ? 15 : $.cookie('pageSize_camera'),
				fitColumns: false,
				hideReset: true,
				hideSearch: true,
				fit: true,
				queryForm: that.monitorForm,
				url: oc.resource.getUrl('portal/resource/cameraMonitor/getHaveMonitorCamera.htm'),
				// url: oc.resource.getUrl('portal/resource/cameradiscover/getHaveMonitor.htm'),
				columns: [[
					{ field: 'id', checkbox: true },
					{ field: 'sourceName', title: '设备名称', sortable: true, width: '10%', ellipsis: true,
					  formatter: function(value, row, rowIndex) {
					  	var statusLabel = $("<label/>").addClass("light-ico_resource " + row.instanceStatus + " oc-pointer-operate");
					  	statusLabel.attr('rowIndex', rowIndex).addClass('quickSelectDetailInfo');
					  	if(value != null) {
					  		statusLabel.attr('title', value).html(value.htmlspecialchars());
					  	}
					  	return statusLabel;
					  }
					},
					{ field: 'devIP', title: 'IP地址/域名', sortable: true, width: '10%' },
					/*{ field: 'instanceState', title: '连通状态', sortable: false, width: '10%',
                        formatter: function(value, row, rowIndex) {
                            var state = row.instanceState;
                            if(state == "CRITICAL"){
                                state = "断线";
                            }else{
                                state = "连通";
                            }
                            return state;
                        }
                    },*/
                    { field: 'cameraType', title: '类型', sortable: false, width: '10%', ellipsis: true },
					{ field: 'address', title: '设备地址', sortable: false, width: '10%', ellipsis: true },
					// { field: 'gisX', title: '经度', sortable: false, width: '10%', ellipsis: true },
					// { field: 'gisY', title: '纬度', sortable: false, width: '10%', ellipsis: true },
                    { field: 'groupName', title: '所属组织', sortable: false, width: '10%', ellipsis: true },
                    { field: 'domainName', title: '域名称', sortable: false, width: '10%', ellipsis: true },
					{ field: 'platForm', title: '平台名称', sortable: false, width: '10%', ellipsis: true },
					{ field: 'liablePerson', title: '责任人', sortable: false, width: '10%', ellipsis: true },
                    {
                        field: 'resourceId',
                        title: '操作',
                        width: '8%',
                        hidden: hiddenProfile,
                        formatter: function (value, row, rowIndex) {// 将三个操作合为一列
                            // return html;
                            return '<a data-index="'
                                + rowIndex
                                + '" class="light_blue icon-field a_domain_edit_btn" title="域编辑"></a><a rowIndex="'
                                + rowIndex
                                + '" class="light_blue icon-tactics quickSelectProfile" title="编辑策略"></a><a rowIndex="'
                                + rowIndex
                                + '" class="light_blue icon-edit quickSelectDiscoverParamter" title="编辑信息"></a>';
                        }
                    }
				]],
				octoolbar: {
					left: [that.monitorForm.selector],
					right: octoolbarRight
				},
				loadFilter: function(data) {
					var myData = data.data;
					that.tabsDiv.find('.tabs-header .tabs-wrap .tabs-title:eq(0)').html('已监控('+myData.totalRecord+')');
					return myData;
				},
				onLoadSuccess: function(data) {
					that.monitorDatagridLoadFlag = true;
					// 查看资源详情
					that.tabsDiv.find(".quickSelectDetailInfo").on('click', function(e) {
						var rowIndex = $(this).attr('rowIndex');
						var row = that.monitorDatagrid.selector.datagrid('getRows')[rowIndex];
						oc.resource.loadScript('resource/module/resource-management/cameraDetailInfo/js/cameraDetailInfo.js',
							function() {
								oc.module.resmanagement.cameradeatilinfo.open({
									instanceId : row.id,
									callback : that,
									callbackType : '1',
									type : '0',
									instanceStatus : row.instanceStatus
								});
						});
						e.stopPropagation();
					});
                    // 编辑策略信息
                    that.tabsDiv.find(".quickSelectProfile").on('click',function(e) {
                                var rowIndex = $(this).attr('rowIndex');
                                var row = that.monitorDatagrid.selector.datagrid('getRows')[rowIndex];
                                var instanceId = row.id;
                                var hasRight = row.hasRight;
                                if (hasRight) {
                                    oc.resource.loadScript('resource/module/resource-management/js/quick_strategy_select.js',
                                            function() {
                                                oc.quick.strategy.detail.show(instanceId);
                                            });
                                } else {
                                    alert('没有该资源的权限');
                                }
                                e.stopPropagation();
                            });
                    //编辑发现信息
                    that.tabsDiv.find(".quickSelectDiscoverParamter").on('click',function() {
                        var rowIndex = $(this).attr('rowIndex');
                        that.editdomain.row = that.monitorDatagrid.selector.datagrid('getRows')[rowIndex];
                        that.editdomain.render = function() {
                            this.mainDiv = this.dlg.find("#edit_resource_camera_main").attr("id",oc.util.generateId());
                            this.form = oc.ui.form({
                                    selector : this.mainDiv.find(".edit_resource_camera_form").first(),
                                    combobox : []
                                });
                            that.editdomain.dlg.find('input[name=sourceName]').val(that.editdomain.row.sourceName);
                            // that.editdomain.dlg.find('input[name=devIP]').val(that.editdomain.row.devIP);
                            // that.editdomain.dlg.find('input[name=devUser]').val(that.editdomain.row.devUser == "--" ? "" : that.editdomain.row.devUser );
                            // that.editdomain.dlg.find('input[name=devPwd]').val(that.editdomain.row.devPwd == "--" ? "" : that.editdomain.row.devPwd);
                            // that.editdomain.dlg.find('input[name=devPort]').val(that.editdomain.row.devPort == "--" ? "" : that.editdomain.row.devPort);
                            that.editdomain.dlg.find('select[name=cameraType]').find("option[name="+that.editdomain.row.cameraType+"]").attr("selected",true);
                            // that.editdomain.dlg.find('input[name=address]').val(that.editdomain.row.address == "--" ? "" : that.editdomain.row.address);
                            that.editdomain.dlg.find('input[name=id]').val(that.editdomain.row.id);
                            //增加必填项
                            //var ip = that.editdomain.dlg.find('input[name=ipAddress]');
                            //ip.attr('validType', "reg[/^([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])$/, '请输入正确的IP格式，例如192.168.0.1']");
                            //sourceName.append(helpInfoNod);
                            if (!user.systemUser) {
                                this.form.disable();
                                this.form.coms.domainId.jq.parent().find(":input[type=text]").attr("disabled", true);
                            }
                        };
                        that.editdomain.validateForm = function(formDiv){
                            var that = this;
                            // 去掉空格
                            formDiv.find('input').each(function(){
                                if($(this).attr('type') != 'password'){
                                    $(this).val($(this).val().trim());
                                }
                            });
                            // 验证可见表单项
                            var validPassOrNot = true;
                            formDiv.find(".form-group:visible [validType],.form-group:visible [required]").each(function(){
                                var validBox = $(this);
                                if (validBox.is('select')) {
                                    validPassOrNot = validBox.combobox('isValid');
                                    if (!validPassOrNot) {
                                        validBox.combobox().next('span').find('input').focus();
                                        return false;
                                    }
                                } else {
                                    if(validBox[0].name=="ipAddress"){
                                        var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
                                        validPassOrNot = reg.test(validBox[0].value);
                                    }else{
                                        validPassOrNot = validBox.validatebox('isValid');
                                    }
                                    if (!validPassOrNot) {
                                        validBox.focus();
                                        return false;
                                    }
                                }
                            });
                            return validPassOrNot;
                        };
                        that.editdomain.save = function() {
                            var target = this,
                                data = target.form.val();
                            var isvalid = that.editdomain.validateForm(target.form);
                            if(isvalid){
                                oc.util.ajax({
                                        url : oc.resource.getUrl('portal/resource/cameradiscover/updateCameraInfo.htm'),
                                        timeout : 5000,
                                        data : {"jsonData": JSON.stringify(data)},
                                        success : function(d) {
                                            if (d.code == 200) {
                                                //target.row.domainId = data.domainId;
                                                target.dlg.window("close");
                                                that.monitorDatagrid.selector.datagrid("reload");
                                                alert("修改成功！");
                                            } else {
                                                if (d.code == 202) {
                                                    log("参数为空",data);
                                                }
                                            }
                                        },
                                        error : function(e) {
                                            alert("修改失败....");
                                        }
                                    });
                            }
                        }
                        var buttons = [
                            {
                                text : "确定",
                                iconCls : "icon-ok",
                                handler : function() {
                                    that.editdomain.save();
                                }
                            },
                            {
                                text : '取消',
                                handler : function() {
                                    that.editdomain.dlg
                                        .window("destroy");
                                }
                            } ];
                        that.editdomain.dlg = $("<div/>")
                            .dialog(
                                {
                                    content : "<div class='edit_resource_domain'></div>",
                                    title : '编辑摄像头信息',
                                    height : 200,
                                    width : 360,
                                    cache : false,
                                    modal : true,
                                    _onOpen : function() {
                                        $.ajax({
                                                url : oc.resource.getUrl('resource/module/resource-management/edit_resource_camera.html'),
                                                dataType : 'html',
                                                success : function(
                                                    data) {
                                                    var reg = /<body[^>]*>((.|[\n\r])*)<\/body>/im;
                                                    var body = reg.exec(data);
                                                    var page = $(body ? body[1]: data);
                                                    that.editdomain.dlg.find(".edit_resource_domain").append(page);
                                                    that.editdomain.render();
                                                },
                                                error : function(e) {
                                                    alert("页面被管理员给搞丢了");
                                                }
                                            });
                                    },
                                    buttons : !user.systemUser ? []
                                        : [
                                            {
                                                text : "确定",
                                                iconCls : "icon-ok",
                                                handler : function() {
                                                    that.editdomain
                                                        .save();
                                                }
                                            },
                                            {
                                                text : '取消',
                                                handler : function() {
                                                    that.editdomain.dlg
                                                        .window("destroy");
                                                }
                                            } ]
                                });

                    });

                    /** ********修改资源所属域start******** */
                    resourceGrid.prototype.editdomain = {};
                    var user = oc.index.getUser();
                    that.tabsDiv.find(".a_domain_edit_btn").on(	'click',function() {
                        var index = $(this).data('index');
                        that.editdomain.row = that.monitorDatagrid.selector.datagrid('getRows')[index];

                        that.editdomain.render = function() {
                            this.mainDiv = this.dlg.find("#edit_resource_domain_main").attr("id",oc.util.generateId());
                            this.form = oc.ui.form({
                                    selector : this.mainDiv.find(".edit_resource_domain_form").first(),
                                    combobox : [ {
                                        selector : '[name=domainId]',
                                        data : oc.index	.getDomains(),
                                        value : that.editdomain.row.domainId,
                                        placeholder : false,
                                        onSelect : function(record) {
                                            oc.util.ajax({
                                                    url : oc.resource.getUrl('resource/resourceMonitor/getDomainDcs.htm'),
                                                    data : {
                                                        domainId : record.id
                                                    },
                                                    success : function(d) {
                                                        if (d.data) {
                                                            var dcsInfo = d.data;
                                                            var flag = true;
                                                            for (var i = 0; i < dcsInfo.length; i++) {
                                                                var dcsItem = dcsInfo[i];
                                                                if (dcsItem.isChecked && dcsItem.isChecked == 1) {
                                                                    that.editdomain.dlg .find( 'input[name=dcsGroupName]').val(dcsItem.dcsName);
                                                                    flag = false;
                                                                    break;
                                                                }
                                                            }
                                                            if (flag) {
                                                                that.editdomain.dlg.find('input[name=dcsGroupName]') .val( "");
                                                            }

                                                        }
                                                    }
                                                })
                                        }
                                    } ]
                                });

                            this.domainId = this.row.domainId;
                            this.form.load(this.row);
                            if (!user.systemUser) {
                                this.form.disable();
                                this.form.coms.domainId.jq.parent().find(":input[type=text]").attr("disabled", true);
                            }
                        };
                        that.editdomain.save = function() {
                            var target = this, data = target.form.val();
                            if (target.domainId == data.domainId) {
                                target.dlg.window("close");
                                return;
                            }
                            if (!data || !data.dcsGroupName) {
                                alert('请确认参数');
                                return;
                            }
                            oc.util.ajax({
                                    url : oc.resource.getUrl('resource/resourceMonitor/update_resource_domain.htm'),
                                    timeout : 5000,
                                    data : data,
                                    success : function(d) {
                                        if (d.code == 200) {
                                            target.row.domainId = data.domainId;
                                            target.dlg.window("close");
                                            that.monitorDatagrid.selector.datagrid("reload");
                                            alert("修改成功！");
                                        } else {
                                            if (d.code == 202) {
                                                log("参数为空",data);
                                            }
                                        }
                                    },
                                    error : function(e) {
                                        alert("修改失败....");
                                    }
                                });
                        }
                        var buttons = [
                            {
                                text : "确定",
                                iconCls : "icon-ok",
                                handler : function() {
                                    that.editdomain.save();
                                }
                            },
                            {
                                text : '取消',
                                handler : function() {
                                    that.editdomain.dlg.window("destroy");
                                }
                            } ];
                        that.editdomain.dlg = $("<div/>").dialog(
                                {
                                    content : "<div class='edit_resource_domain'></div>",
                                    title : '编辑域信息',
                                    height : 255,
                                    width : 420,
                                    cache : false,
                                    modal : true,
                                    _onOpen : function() {
                                        $.ajax({
                                                url : oc.resource.getUrl('resource/module/resource-management/edit_resource_domain.html'),
                                                dataType : 'html',
                                                success : function(
                                                    data) {
                                                    var reg = /<body[^>]*>((.|[\n\r])*)<\/body>/im;
                                                    var body = reg.exec(data);
                                                    var page = $(body ? body[1]: data);
                                                    that.editdomain.dlg.find(".edit_resource_domain").append(page);
                                                    that.editdomain.render();
                                                },
                                                error : function(e) {
                                                    alert("页面被管理员给搞丢了");
                                                }
                                            });
                                    },
                                    buttons : !user.systemUser ? []
                                        : [
                                            {
                                                text : "确定",
                                                iconCls : "icon-ok",
                                                handler : function() {
                                                    that.editdomain
                                                        .save();
                                                }
                                            },
                                            {
                                                text : '取消',
                                                handler : function() {
                                                    that.editdomain.dlg
                                                        .window("destroy");
                                                }
                                            } ]
                                });

                    });
					that.disableGlobalProgress();
				},
			});
			var paginationObject = that.tabsDiv.find(".monitorDatagrid").datagrid('getPager');
			if(paginationObject) {
				paginationObject.pagination({
					onChangePageSize: function(pageSize) {
						$.cookie('pageSize_camera', pageSize);
					}
				});
			}
		},
		createUnmonitorDatagrid: function() {
			var that = this;
			var user = oc.index.getUser();
			var octoolbarRight = [];
			if(user.domainUser || user.systemUser) {
				octoolbarRight = [{
					iconCls: 'fa fa-trash-o',
					text: '删除资源',
					onClick: function() {
						that.deleteResource(that.unmonitorDatagrid);
					}
				}, {
					iconCls: 'fa fa-plus',
					text: '开启监控',
					onClick: function() {
						that.openMonitor();
					}
				}].concat(octoolbarRight);
			}
			that.unmonitorDatagrid = oc.ui.datagrid({
				selector: that.tabsDiv.find(".unmonitorDatagrid"),
				pageSize: $.cookie('pageSize_camera') == null ? 15 : $.cookie('pageSize_camera'),
				fitColumns: false,
				fit: true,
				hideReset: true,
				hideSearch: true,
				queryForm: that.unmonitorForm,
				url: oc.resource.getUrl('portal/resource/cameraMonitor/getNotMonitorCamera.htm'),
				columns: [[
					{ field: 'id', checkbox: true },
					{ field: 'sourceName', title: '设备名称', sortable: true, width: '20%', ellipsis: true,
					  formatter: function(value, row, rowIndex) {
					  	return '<label title="' + value + '">' + value.htmlspecialchars() + '</label>';
					  }
					},
//					{ field: 'devIP', title: 'IP地址/域名', sortable: true, width: '20%' },
//					{ field: 'groupName', title: '组织名称', sortable: false, width: '20%', ellipsis: true },
//					{ field: 'address', title: '设备地址', sortable: false, width: '20%', ellipsis: true },
					{ field: 'platForm', title: '平台名称', sortable: false, width: '40%', ellipsis: true },
				]],
				octoolbar: {
					left: [that.unmonitorForm.selector],
					right: octoolbarRight
				},
				loadFilter: function(data) {
					var myData = data.data;
					that.tabsDiv.find('.tabs-header .tabs-wrap .tabs-title:eq(1)').html('未监控('+myData.totalRecord+')');
					return myData;
				},
				onLoadSuccess: function(data) {
					that.unmonitorDatagridLoadFlag = true;
					that.disableGlobalProgress();
				},
			});
			var paginationObject = that.tabsDiv.find(".unmonitorDatagrid").datagrid('getPager');
			if(paginationObject) {
				paginationObject.pagination({
					onChangePageSize: function(pageSize) {
						$.cookie('pageSize_camera', pageSize);
					}
				});
			}
		},
		enableGlobalProgress: function() {
			this.monitorDatagridLoadFlag = false;
			this.unmonitorDatagridLoadFlag = false;
			oc.ui.progress();
		},
		disableGlobalProgress: function() {
			if(this.monitorDatagridLoadFlag && this.unmonitorDatagridLoadFlag) {
				$.messager.progress('close');
			} else {
				oc.ui.progress();	
			}
		},
		loadAll: function() {
			this.enableGlobalProgress();			
			this.monitorDatagrid.load();
			this.unmonitorDatagrid.load();
		},
		reLoadAll: function() {
			this.enableGlobalProgress();			
			this.monitorDatagrid.reLoad();
			this.unmonitorDatagrid.reLoad();
		},
		getDatagridSelectIds : function(datagrid) {
			return this.getSelectIds(datagrid);
		},
		getMonitorDatagridSelectIds : function() {
			return this.getSelectIds(this.monitorDatagrid);
		},
		getUnmonitorDatagridSelectIds : function() {
			return this.getSelectIds(this.unmonitorDatagrid);
		},
		getSelectIds : function(dataGrid) {
			var objs = dataGrid.selector.datagrid('getChecked'), ids = [];
			for(var i=0,len=objs.length;i<len;i++){
				var obj = objs[i];
				ids.push(obj.id);
			}
			return ids;
		},
		deleteResource: function(datagrid) {
			var that = this;
			var ids = that.getDatagridSelectIds(datagrid);
			if(ids == undefined || ids == "") {
				alert('请选择需要删除的资源');
			} else {
				oc.ui.confirm('是否确认删除资源？', function() {
					oc.util.ajax({
						url: oc.resource.getUrl('resource/resourceMonitor/batchDelResource.htm'),
						timeout: null,
						data: { ids: ids.join() },
						success: function(data) {
							if(data.code == 200) {
								that.loadAll();
							} else {
								alert('删除资源失败');
							}
						}
					});
				});
			}
		},
		openMonitor: function() {
			var that = this;
			var ids = that.getUnmonitorDatagridSelectIds();
			if(!!ids && ids.length > 0) {
				 oc.util.ajax({
				 	url: oc.resource.getUrl('resource/resourceMonitor/batchOpenMonitor.htm'),
					type: 'POST',
					dataType: "json", 
					data: { ids:ids.join() },
					success: function(json) {
						if(json.code == 200) {
							if(json.data == 1) {
								alert('加入监控成功');
							} else if(json.data == 0) {
								alert('加入监控失败');
							} else {
								alert('加入监控失败,监控数量超过购买监控数量');
							}
						} else {
							alert('加入监控失败');
						}
						that.loadAll();
					}
				 })
			} else {
				alert('请选择需要开启监控的资源');
			}
		},
		cancelMonitor: function() {
			var that = this;
			var ids = that.getMonitorDatagridSelectIds();
			if(!!ids && ids.length > 0) {
				oc.util.ajax({
					url: oc.resource.getUrl('resource/resourceMonitor/batchCloseMonitor.htm'),			            
					data: { ids:ids.join() },
					async: false,
					success: function(d) {
						that.loadAll();
					}
				});
			} else {
				alert('请选择需要取消监控的资源');	
			}
		},
		moreOperation: function(e) {
			var that = this;
            var user = oc.index.getUser();
			e.stopPropagation();
			var top = $("#moreOpBtn").offset().top + 18;
			var left = $("#moreOpBtn").offset().left - 5;
			$('body').find('.cgtOper').remove();
			cgtOperDiv = $("<div class='cgtOper sh-window' style='top:" + top + "px;left:" + left + "px'>");
			tWindowDiv = $("<div class='t-window'><div class='b-window'><div class='m-window'>"
					+ "<span class='cgtEdit'><a class='ico ico ico-Person'></a>编辑责任人</span>"
					+ "<span class='cgtDel'><a class='ico ico-clearp'></a>清除责任人</span>"
                    + "<span id='cgtEditDominId' class='cgtEditDomin'><a class='ico ico_hardware_local'></a>编辑域</span>"
                    + "</div></div></div>");
			$('body').append(cgtOperDiv.append(tWindowDiv));
			// 编辑责任人
			$(cgtOperDiv).find(".cgtEdit").on('click',function(e) {				                                   
				var ids = that.getMonitorDatagridSelectIds();													
				if (!!ids && ids.length > 0) {
					showDialogPerson(ids, that);
					e.stopPropagation();
				} else {
					alert('请选择需要操作的资源');
				}
			});
			// 清除责任人
			$(cgtOperDiv).find(".cgtDel").on('click',function(e) {				
				var ids = that.getMonitorDatagridSelectIds();	
				if (!!ids && ids.length > 0) {
					showDialogPersonClear(ids, that);
					e.stopPropagation();
				} else {
					alert('请选择需要操作的资源');
				}
			});

            // 编辑域
            if(!user.systemUser){ //域用户不具有编辑域权限
                $('#cgtEditDominId').css("background-color","#ddd");
            }
            else{
                $(cgtOperDiv).find(".cgtEditDomin").on('click', function(e) {
                    var ids = that.getMonitorDatagridSelectIds();
                    if (!!ids	&& ids.length > 0) {
                        showDialogDomin(ids);
                        e.stopPropagation();
                    } else {
                        alert('请选择需要操作的资源');
                    }
                });
            }

            $(cgtOperDiv).hover(function(e) {
				e.stopPropagation();
			}, function(e) {
				e.stopPropagation();
				$('body').find('.cgtOper').remove();
			});
		}
	};
	oc.ns('oc.camera.resourcegrid');
	
	$("#cameraResourceList").tabs({
		fit: true,
	});
	var rgrid = new resourceGrid();
	rgrid.init();
});
