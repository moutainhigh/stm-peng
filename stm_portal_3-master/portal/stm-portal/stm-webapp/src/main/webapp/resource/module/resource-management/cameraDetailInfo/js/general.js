$(function() {
	function general(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		var id = oc.util.generateId();
		this.generalDom = $("#generalContent").attr('id', id);
	}
	general.prototype = {
		constructor : general,
		cfg : undefined, // 外界传入的配置参数 json对象格式
		generalDom : undefined,
		// rightDom : undefined,
		flag : true,
		showName : undefined,
		chartObj : undefined,
		layout : {},
		_datagrid : undefined,
		child_no_monitor : 0,
		child_up : 0,
		child_up_down : 0,
		child_down : 0,
		child_unkown : 0,
		_defaults : {},
		open : function() {
			var that = this;
			oc.util
					.ajax({
						url : oc.resource
								.getUrl("portal/resource/cameradetail/getCameraDetail.htm"),
						data : {
							instanceId : this.cfg.instanceId
						},
						timeout : null,
						successMsg : null,
						success : function(json) {

							// json =
							// {"data":[[[{"id":"statusLight","unit":"","colspan":"1","name":"","width":"4%","value":"UNKNOWN_NOTHING","rowspan":"2","type":"text"},{"id":null,"colspan":"1","width":"10.5%","value":"资源名称","rowspan":"1","type":"text"},{"id":"name","colspan":"1","width":"12.5%","value":"52A18956","rowspan":"1","type":"text"},{"id":null,"colspan":"1","width":"12.5%","value":"显示名称","rowspan":"1","type":"text"},{"id":"showname","colspan":"1","width":"12.5%","value":"52A18956","rowspan":"1","type":"text"},{"id":null,"colspan":"1","width":"12.5%","value":"操作系统","rowspan":"1","type":"text"},{"id":"osVersion","unit":"","colspan":"1","name":"","width":"12.5%","value":"","rowspan":"1","type":"text"},{"id":null,"colspan":"1","width":"12.5%","value":"IP地址","rowspan":"1","type":"text"},{"id":"ip","colspan":"1","width":"12.5%","value":[{"id":"10.154.8.20","name":"10.154.8.20"}],"rowspan":"1","type":"text"}],[{"id":null,"colspan":"1","width":"12.5%","value":"当前可用性","rowspan":"1","type":"text"},{"id":"availability","unit":"","colspan":"1","name":"","width":"12.5%","value":"UNKNOWN_NOTHING","rowspan":"1","type":"text"},{"id":null,"colspan":"1","width":"12.5%","value":"运行时长","rowspan":"1","type":"text"},{"id":"sysUpTime","unit":"","colspan":"1","name":"","width":"12.5%","value":"","rowspan":"1","type":"text"},{"id":null,"colspan":"1","width":"12.5%","value":"所属域","rowspan":"1","type":"text"},{"id":"domain","colspan":"1","width":"12.5%","value":"默认域","rowspan":"1","type":"text"},{"id":null,"colspan":"1","width":"12.5%","value":"当前策略","rowspan":"1","type":"text"},{"id":"profile","colspan":"1","width":"12.5%","value":"默认策略VCS
							// Control","rowspan":"1","type":"text"}]],[[{"id":null,"unit":"","title":"Traversal","colspan":"1","name":"","width":"33%","value":[{"metricStatus":"NORMAL","value":"0","metricStr":"TraversalCurrent","metricName":"当前值"},{"metricStatus":"NORMAL","value":"0","metricStr":"TraversalMax","metricName":"峰值"},{"metricStatus":"NORMAL","value":"0","metricStr":"TraversalTotal","metricName":"重启后累计值"}],"rowspan":"1","type":"metricTabs"},{"id":null,"unit":"","title":"Non-Traversal","colspan":"1","name":"","width":"33%","value":[{"metricStatus":"NORMAL","value":"0","metricStr":"NonTraversalCurrent","metricName":"当前值"},{"metricStatus":"NORMAL","value":"3","metricStr":"NonTraversalMax","metricName":"峰值"},{"metricStatus":"NORMAL","value":"12","metricStr":"NonTraversalTotal","metricName":"重启后累计值"}],"rowspan":"1","type":"metricTabs"},{"id":null,"unit":"","title":"Registrations","colspan":"1","name":"","width":"33%","value":[{"metricStatus":"NORMAL","value":"11","metricStr":"RegistrationsCurrent","metricName":"当前值"},{"metricStatus":"NORMAL","value":"14","metricStr":"RegistrationsMax","metricName":"峰值"},{"metricStatus":"NORMAL","value":"78","metricStr":"RegistrationsTotal","metricName":"重启后累计值"}],"rowspan":"1","type":"metricTabs"}],[{"id":null,"unit":"","title":"指标一览","colspan":"3","name":"","width":"100%","childtype":["prefmetric","infometric"],"rowspan":"1","type":"tabs"}]]],"code":200};

							if (json.code == 200) {
								for (var i = 0; i < json.data.length; i++) {
									that.createTable(json.data[i], i);
								}
								var tinyToolDom = $("<div/>");
								that.generalDom.append(tinyToolDom);
								that.createTinyTool(tinyToolDom);

							} else {
								return false;
							}
						}
					});
		},
		createTable : function(tabData, tabNo) {
			var tabOutDom = $("<div/>");
			if (tabNo == 0) {
				tabOutDom.addClass("oc-wbg");
			}
			var tabDom = $("<table/>").width('100%');
			this.generalDom.append(tabOutDom.append(tabDom));
			for (var i = 0; i < tabData.length; i++) {
				this.createTr(tabDom, tabData[i]);
			}
			if (tabNo == 0) {
                tabOutDom.find("td:even").each(function () {
                    $(this).addClass('topo-l-tittle');
                });
            } else {
				//tabDom.css('background-color','#174F99')
			}
		},
		createTr : function(tabDom, trData) {
			var trDom = $("<tr/>").height('30px');
			tabDom.append(trDom);
			for (var i = 0; i < trData.length; i++) {
				this.createTd(trDom, trData[i], trData.length);
			}
		},
		createTd : function(trDom, tdData, tdSize) {
			var that = this;
			// 布局
			var tdDom = $("<td/>").width(tdData.width).attr('colspan',
					tdData.colspan);
			tdDom.attr('rowspan', tdData.rowspan);
			trDom.append(tdDom);

			switch (tdData.type) {
			case "video":
				this.createCameraVideo(tdDom, tdData);
				break;
			case "picture":
				this.createPicture(tdDom);
				break;
			case "text":
				this.createText(tdDom, tdData);
				break;
			case "cameraprogress":
				this.createCameraProgress(tdDom, tdData, tdSize);
				break;
			case "meter":
				this.createMeter(tdDom, tdData, tdSize);
				break;
			case "progress":
				this.createProgress(tdDom, tdData, tdSize);
				break;
			case "multiMeter":
			case "fan":
			case "cpu":
			case "node":
			case "battery":
			case "camera":
			case "microphone":
			case "display":
			case "commonChildrenPanel":
				this.createChildrenRes(tdDom, tdData, tdSize);
				break;
			case "tabs":
			case "tabs-full":
				this.createTabs(tdDom, tdData, tdSize);
				break;
			case "datagrid":
				this.createAvailabilityMetric(tdDom, tdData, tdSize);
				break;
			case "graph":
				this.createGraph(tdDom, tdData, tdSize);
				break;
			case "panel":
				this.createChildPanel(tdDom, tdData, tdSize);
				break;
			case "pie":
				this.createPie(tdDom, tdData, tdSize);
				break;
			case "commonMetricPie":
				this.createPie(tdDom, tdData, tdSize);
				break;
			case "table":
				this.createChildTable(tdDom, tdData, tdSize);
				break;
			case "environmentPanel":
				this.createEnvironmentPanel(tdDom, tdData, tdSize);
				break;
			case "metricTabs":
				this.createMetricTabs(tdDom, tdData, tdSize);
				break;
			case "tableMetric":
				this.createTableMetric(tdDom, tdData, tdSize);
				break;
			case "commonOneChildrenPanel":
				this.createCommonOneChildrenPanel(tdDom, tdData, tdSize);
				break;
			case "commonOneMetricPanel":
				this.createCommonOneMetricPanel(tdDom, tdData, tdSize);
				break;
			case "solidgauge":
				this.createSolidgauge(tdDom, tdData, tdSize);
				break;
			}

		},
		createCameraVideo : function(tdDom, tdData) {
			var that = this;
			var userAgent = navigator.userAgent;
			if (userAgent.indexOf("compatible") > -1
					&& userAgent.indexOf("MSIE") > -1 && !isOpera) {
				tdDom
						.append('<div style="padding-left: 5px" class="panel-title">视频播放: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input stype="display: inline-block; vertical-align: top;  width: auto; line-height: 28px; font-size: 12px;  padding: 0; margin: 0 4px;"  type="button" value="暂停" onclick="StopPlayVideo();"/></div>');
				tdDom
						.append('<object style="border: 1px solid #ccc;" classid="clsid:F84FBCC2-B61C-40FA-BF2D-D60E8F1098D4" id="play" width="510px" height="320px" style="padding-top:2px;"  codebase="dzocx.cab#version=1,0"></object>');
				this.openVideobySystem(tdData);
			} else {
				tdDom.append('<div style="padding-left: 5px" class="panel-title">&nbsp;视频播放:</div>');
				var popUpVideo = $('<img id="videoBKImg" class="videoBKImg" style=" display: block;margin:0 auto;  width="450px" height="280px"  src="'
						+ oc.resource
								.getUrl('resource/themes/blue/images/bk.jpg')
						+ '"></img>');
				tdDom.append(popUpVideo);
				popUpVideo.on('click', function() {
					var ip = tdData.ip;// 服务器IP
					var port = tdData.port;// 服务器端口
					var usr = tdData.usr;// 用户名
					var pwd = tdData.pwd;// 密码
					var chnno = tdData.chnno;// 通道号 写死传0
					var chnid = tdData.chnid;// 待点播摄像机ID
					var systemtype = tdData.systemtype;// 服务器类型 1表示直连设备
					var url = 'video://play?param=' + '{' + '"keep":"' + systemtype
							+ '","serverip":"' + ip + '","port":"' + port
							+ '","user":"' + usr + '","pwd":"' + pwd
							+ '","devid":"' + chnid + '","chnno":"' + chnno
							+ '"}';
					url = url.replace(/"/g, '%22');// 引号转码为%22
					window.location = url;// 地址跳转，实现视频点播
				});
			}

		},
		openVideobySystem : function(tdData) {
			var ip = tdData.ip;// 服务器IP
			var port = tdData.port;// 服务器端口
			var usr = tdData.usr;// 用户名
			var pwd = tdData.pwd;// 密码
			var chnno = tdData.chnno;// 通道号 写死传0
			var chnid = tdData.chnid;// 待点播摄像机ID
			var systemtype = tdData.systemtype;// 服务器类型 1表示直连设备
			var param = '{' + ' "keep":"' + systemtype + '",  '
					+ ' "serverip":"' + ip + '",						' + ' "port":"' + port
					+ '",						' + ' "user":"' + usr + '",						' + ' "pwd": "'
					+ pwd + '",   					' + ' "devid":"' + chnid
					+ '",	        			' + ' "chnno":"' + chnno + '"						' + '}';
			var r = play.PlayVideo(param);
			// var r =
			// play.PlayVideobySystem(ip,port,usr,pwd,chnno,chnid,systemtype,'','');
		},
		StopPlayVideo : function() {
			play.CloseVideo();
		},
		createPicture : function(tdDom) {
			var that = this;
			tdDom.append('<div style="padding-left: 5px" class="panel-title">&nbsp;&nbsp;诊断图片:</div>');
			tdDom
					.append('<img id="cameraImg" style=" display: block;margin:0 auto;border:4px solid #49a1e3;" width="450px" height="280px" src="'
							+ oc.resource
									.getUrl('portal/resource/cameradetail/getFileInputStream.htm?instanceId='
											+ this.cfg.instanceId) + '"></img>');
		},
		createText : function(tdDom, tdData) {
			var that = this;
			var user = undefined;
			// 只有在显示名称的时候才去调用getUser
			if (tdData.id == 'name') {
				user = oc.index.getUser();
			}
			if (tdData.id == 'statusLight') {
				tdDom.append(this.returnStatusLight(tdData.value,
						'mainInstance'));
			} else if (tdData.id == 'liablePerson') {
				oc.util
						.ajax({
							url : oc.resource
									.getUrl('system/login/getUserByInstanceId.htm'),
							data : {
								instanceId : this.cfg.instanceId
							},
							success : function(d) {
								var liableUser = d.data;
								if (liableUser.name != null
										&& liableUser.name != undefined) {
									var content = liableUser.name;
									if (liableUser.mobile == null) {
										liableUser.mobile = "";
									}
									if (liableUser.email == null) {
										liableUser.email = "";
									}
									var tdHtml = liableUser.name;
									if (liableUser.name.length > 4) {
										tdHtml = liableUser.name
												.substring(0, 5)
												+ "...";
										var content = $("<span/>").css({
											"white-space" : "nowrap",
											"width" : "40"
										}).html(tdHtml).attr("title",
												liableUser.name);
										tdDom.append(content);
									} else {
										tdDom.html(tdHtml);
									}
									// tdDom.html(content);
									var liablePersonBtn = $("<span/>")
											.addClass('ico ico-see').css(
													'float', 'right');
									tdDom.append(liablePersonBtn);
									var tooltipHtml = '<div id="tooltip" class="tooltip_selfdefine" style="width:220px">'
											+ '<form style="padding-bottom:5px;">'
											+ '<table class="tab-border" style="word-break:break-all">'
											+ '<tr><td class="td-bgcolor" style="width:10%"><span class="zd-name">姓名</span></td><td><span class="user_info">'
											+ liableUser.name
											+ '</span></td></tr>'
											+ '<tr><td class="td-bgcolor" style="width:10%"><span class="zd-name">手机号码</span></td><td><span class="user_info">'
											+ liableUser.mobile
											+ '</span></td></tr>'
											+ '<tr><td class="td-bgcolor" style="width:10%"><span class="zd-name">邮箱</span></td><td><span class="user_info">'
											+ liableUser.email
											+ '</span></td></tr>'
											+ '</table>'
											+ '</form>' + "</div>";
									tdDom.css({
										"position" : 'relative'
									});
									tdDom.append(tooltipHtml);
									liablePersonBtn.mouseover(function(event) {
										event.stopPropagation();
										$("#tooltip").css({
											"display" : 'block'
										});

									}).mouseout(function() {
										$("#tooltip").css({
											"display" : 'none'
										});
										// $("#tooltip").remove();
									})
								}
							}
						});
			} else if (tdData.id == 'ip') {
				var IP = $("<select/>");
				tdDom.append(IP);
				oc.ui.combobox({
					selector : IP,
					placeholder : false,
					data : tdData.value,
					value : tdData.value[0].id
				});
			} else if (tdData.id == 'name'
					&& typeof (that.cfg.callback) != 'undefined'
					&& (user.domainUser || user.systemUser)) {
				that.showName = tdData.value;
				var outEditNode = $("<div/>").width('120px').height('100%').css('position','relative');
				var editBtn = $("<span/>").addClass('uodatethetext fa fa-edit').css({'float':'right','top':'13px'}
						).attr('title', '编辑名称');
				var editNode = $("<input name='editName' type='text'>").css(
						'float', 'left').width('100px');
				editNode.validatebox({
					validType : 'maxLength[30]'
				});
				var showNode = $("<div name='showName' />")
						.css({'float':'left','line-height':'37px'}).addClass('generalShowName').attr(
								'title', that.showName).html(
								that.showName.htmlspecialchars())
						.width('100px');
				outEditNode.append(editNode.hide()).append(showNode).append(
						editBtn);
				tdDom.append(outEditNode);
				editBtn
						.on(
								'click',
								function() {
									if (that.flag) {
										editNode.val(that.showName).show();
										showNode.html('').hide();
										editBtn.attr('title', '确认名称修改')
												.removeClass().addClass(
														'uodatethetextok fa fa-check').css({'top':'9px'});
										that.flag = false;
									} else {
										if (editNode.validatebox('isValid')) {
											var newShowName = editNode.val();
											if (newShowName != undefined
													&& newShowName
															.replace(
																	/(^\s*)(\s*$)/g,
																	'') != '') {
												oc.util
														.ajax({
															url : oc.resource
																	.getUrl("portal/resource/discoverResource/updateInstanceName.htm"),
															data : {
																newInstanceName : newShowName,
																instanceId : that.cfg.instanceId
															},
															successMsg : null,
															success : function(
																	json) {
																if (json.code == 200) {
																	if (json.data == 0) {
																		alert('修改显示名称失败！');
																	} else if (json.data == 1) {
																		alert('修改显示名称成功！');
																		showNode
																				.html(
																						newShowName
																								.htmlspecialchars())
																				.attr(
																						'title',
																						newShowName)
																				.show();
																		that.showName = newShowName;
																		editNode
																				.val(
																						'')
																				.hide();
																		editBtn
																				.attr(
																						'title',
																						'编辑名称')
																				.removeClass()
																				.addClass(
																						'uodatethetext fa fa-edit');
																		that.flag = true;
																		oc.module.resmanagement.cameradeatilinfo
																				.updateDlgTitle(newShowName);
																	} else {
																		alert('资源显示名称重复');
																	}
																} else {
																	alert('修改显示名称失败！');
																}
															}
														});
											}
										}
									}
								});
			} else if (tdData.id == 'availability') {
			    //取消判断，后台直接返回
				/*if (tdData.value == '1') {
					content = '连通';
				} else if (tdData.value == '0') {
					 content = '断线';
				}else if (tdData.value == '2') {
					 content = '登录异常';
				}else if (tdData.value == '3') {
					 content = '取流异常';
				}else{
					content = '未知';
				}*/
				tdDom.html(tdData.value);
			} else if (tdData.id == 'resourceImg') {
				if (tdData.value) {
					tdDom
							.append('<img id="tvImg" style="width:80px;height:60px;" title="点击更新图片!" src="/mainsteam-stm-webapp/platform/file/getFileInputStream.htm?fileId='
									+ tdData.value + '"></img>');
				} else {
					tdDom
							.append('<img id="tvImg" style="width:80px;height:60px;" title="点击更新图片!" src=""></img>');
				}
				var user = oc.index.getUser();
				// tdDom.append('<p id="tvImgP">点击上传图片!</p>');
				var img = tdDom.find('#tvImg');
				img
						.on(
								'click',
								function() {
									if (!user.systemUser) {
										alert("更新图片,请联系系统管理员!");
										return;
									}

									var dlg = $('<div/>');
									var fileUpload = $('<form id="fileForm" class="oc-form col1 h-pad-mar oc-table-ocformbg">');
									var fileTable = $('<table style="width:100%;" class="octable-pop"/></table>');
									var fileTableTr3 = $('<tr class="form-group h-pad-mar"><td class="tab-l-tittle" align style="width:30%"><label>上传图片：</label></td><td><input name="file" type="text" required="required" /></td></tr>');

									fileTable.append(fileTableTr3);
									fileUpload.append(fileTable);
									dlg.append(fileUpload);

									var form = oc.ui.form({
										selector : dlg.find('#fileForm'),
										filebox : [ {
											selector : '[name=file]'
										} ]
									});

									var buttons = [
											{
												text : '确定',
												iconCls : "icon-ok",
												handler : function() {
													var filebox = form.filebox[0];
													var filePath = filebox.selector
															.filebox('getValue');
													var fileName = filePath
															.substring(filePath
																	.lastIndexOf("\\") + 1);

													if (fileName
															.match(/([^\u0000-\u00FF])/g)) {
														alert('文件名请不要有中文字符!');
														return;
													}
													if (fileName
															&& fileName.length > 99) {
														alert('文件名过长,请不要超过100个字符!');
														return;
													}

													if (filePath) {
														form.jq
																.ajaxSubmit($
																		.extend(
																				{},
																				filebox,
																				{
																					url : oc.resource
																							.getUrl('portal/resource/resourceDetailInfo/resouceImgUpload.htm'),
																					data : {
																						instanceId : that.cfg.instanceId
																					},
																					type : 'POST',
																					iframe : true,
																					timeout : 300000,
																					async : false,
																					dataType : 'json',
																					success : function(
																							data) {

																						if (data > 0) {
																							// tdDom.find('#tvImgP').hide();
																							img
																									.attr(
																											'src',
																											'/mainsteam-stm-webapp/platform/file/getFileInputStream.htm?fileId='
																													+ data);
																							alert("上传成功！");
																						} else {
																							alert("上传失败！");
																						}
																						dlg
																								.window("destroy");
																					},
																					error : function(
																							XMLHttpRequest,
																							textStatus,
																							errorThrown) {
																						alert('文件上传失败！');
																						log(
																								XMLHttpRequest,
																								textStatus,
																								errorThrown);
																					}
																				}));
													}
												}
											}, {
												text : '取消',
												iconCls : "icon-cancel",
												handler : function() {
													dlg.dialog('destroy');
												}
											} ];

									dlg.dialog({
										title : '台标上传',
										width : 350,
										height : 112,
										buttons : buttons,
										onLoad : function() {
										}
									});
								});
			} else if(tdData.id == 'cameraResult'){
				var color = "orange";
				if(tdData.value == "正常"){
					color = "green";
				}
				/*tdDom.append("<div class='panel-title' style='padding-left: 5px;padding-top: 2px; font-weight: bold;'>诊断结果：<span style='color:" + color+"'>" + tdData.value + "</span></div>");*/
				tdDom.append("<div class='panel-title' style='padding-left: 5px;padding-top: 2px; font-weight: bold;'>诊断结果：<span class='camera_color'>" + tdData.value + "</span></div>");
			}else if(tdData.id == 'keep'){
				var keepMay = {
						0:'大华设备',
						6:'海康设备',
						34:'宇视设备',
						99:'Onvif设备',
						109:'国标平台',
						202:'宇视平台',
						203:'海康8200平台3.2',
						204:'大华平台',
						208:'海康8200平台3.0',
						209:'铁标平台',
						210:'烽火平台',
						211:'东方网力平台',
						213:'广成平台'
				};
				if(keepMay[tdData.value] != null){
					tdDom.append(keepMay[tdData.value]);
				}else{
					tdDom.append(tdData.value);
					}
			}else {
				// 处理字符串过长的问题
				var tdHtml = tdData.value;
				if (tdData.value != undefined && tdData.value != null && tdData.id !="channelId"  && tdData.value.length > 17) {
					tdHtml = tdData.value.substring(0, 14) + "...";
				}
				var content = $("<div/>").css({
					"white-space" : "nowrap"
				}).html(
						tdData.value == undefined || tdData.value == null ? ''
								: tdHtml).attr(
						"title",
						tdData.value == undefined || tdData.value == null ? ''
								: tdData.value);
				tdDom.append(content);
			}

		},
		createMeter : function(tdDom, tdData, tdSize) {
			// 如果没有设置告警则状态为正常(特殊设置)
			if (tdData.isAlarm == false) {
				tdData.status = 'NORMAL';
			}
			var meter = this.createPanel(tdDom, tdData, tdSize);
			var color = this.returnColorByStatus(tdData.status);
			var unit = tdData.unit == undefined ? '' : tdData.unit;
			var size = tdSize == 4 ? '130%' : '160%';
			var textSize = tdSize == 4 ? '30px' : '40px';
			var availability = this.cfg.availability;
			meter
					.highcharts(
							{
								chart : {
									type : 'solidgauge',
									margin : [ 0, 0, 0, 0 ],
									spacing : [ 0, 0, -35, 0 ],
									backgroundColor : 'rgba(0,0,0,0)'
								},
								title : null,
								pane : {
									center : [ '50%', '90%' ],
									size : size,
									startAngle : -90,
									endAngle : 89.5, // 在ie8下90度 值为百分百 指针会越界
									background : {
										borderWidth : 1,
										innerRadius : '80%',
										outerRadius : '101%',
										shape : 'arc'
									},
									width : 200
								},
								credits : {
									enabled : false
								},
								exporting : {
									enabled : false
								},
								tooltip : {
									enabled : false
								},
								// the value axis
								yAxis : {
									min : 0,
									max : 100,
									stops : [ [ 0.1, color ], [ 0.5, color ],
											[ 0.9, color ] ],
									lineWidth : 1,
									tickPixelInterval : 80,
									gridLineWidth : 0,
									minorGridLineWidth : 0,
									minorTickWidth : 0,
									tickWidth : 0,
									labels : {
										distance : 10,
										x : 1,
										style : {
											fontSize : '13px'
										}
									}
								},
								plotOptions : {
									solidgauge : {
										dataLabels : {
											y : 10,
											borderWidth : 0,
											useHTML : true,
											shadow : true,
										}
									}
								},
								series : [
										{
											data : [ 0 ],
											dataLabels : {
												align : 'center',
												y : -10,
												style : {
													fontSize : textSize,
													color : '#555555'// #EC5707
												},
												format : tdData.value != null
														&& tdData.value != undefined
														&& availability != 'CRITICAL_NOTHING'
														&& availability != "CRITICAL" ? (tdData.value == 'N/A' ? 'N/A'
														: '{y} ' + unit)
														: '--'
											},
											innerRadius : '81.5%'
										},
										{
											type : 'gauge',
											dial : {
												backgroundColor : Highcharts.theme.plotOptions.gauge.dial.backgroundColor,
												borderColor : Highcharts.theme.plotOptions.gauge.dial.borderColor,
												baseLength : '0%',
												baseWidth : 6,
												topWidth : 1,
												borderWidth : 0,
												radius : '80%',
												rearLength : '0%'
											},
											pivot : {
												backgroundColor : Highcharts.theme.plotOptions.gauge.pivot.backgroundColor,
												radius : 3
											},
											dataLabels : {
												enabled : false
											},
											data : [ 0 ]
										} ]
							},
							function(chart) {
								if (tdData.value != null
										&& tdData.value != undefined
										&& tdData.value != 'N/A') {
									var series = chart.series;
									for ( var index in series) {
										var points = series[index].points;

										if (!!points) {
											if (availability == 'CRITICAL_NOTHING'
													|| availability == "CRITICAL") {
											} else {
												points[0]
														.update(parseFloat(tdData.value));
											}

										}
									}
								}
							});
		},
		createSolidgauge : function(tdDom, tdData, tdSize) {
			// console.log(tdData);
			var green = '#55BF3B', yellow = '#DDDF0D', red = '#DF5353', orange = '#EE9A00';
			var param = {}, labelStr = '';
			param.stops = [ [ 0, green ] ];
			param.value = null;
			param.collectTime = '--';

			if (tdData.collectTime) {
				var date = new Date(tdData.collectTime);
				var str = date.getFullYear()
						+ '-'
						+ (date.getMonth() + 1)
						+ '-'
						+ date.getDate()
						+ '  '
						+ (date.getHours() < 10 ? '0' + String(date.getHours())
								: String(date.getHours()))
						+ ':'
						+ (date.getMinutes() < 10 ? '0'
								+ String(date.getMinutes()) : String(date
								.getMinutes()))
						+ ':'
						+ (date.getSeconds() < 10 ? '0'
								+ String(date.getSeconds()) : String(date
								.getSeconds()));

				param.collectTime = str;
			}

			if ('instancestate' == tdData.metricType) {
				param.value = 100;
				// 此处子资源告警依然认为状态变化
				switch (tdData.status) {
				case "CRITICAL":
				case "CRITICAL_NOTHING":
				case "NORMAL_CRITICAL":
					labelStr = '致命';
					param.stops = [ [ 0, red ] ];
					break;
				case "SERIOUS":
					labelStr = '严重';
					param.stops = [ [ 0, orange ] ];
					break;
				case "WARN":
					labelStr = '告警';
					param.stops = [ [ 0, yellow ] ];
					break;

				default:
					labelStr = '正常';
					param.stops = [ [ 0, green ] ];
					break;
				}
			} else if ('availability' == tdData.metricType) {
				param.value = 100;

				switch (tdData.status) {
				case "CRITICAL":
					param.stops = [ [ 0, red ] ];
					labelStr = '不可用';
					break;
				case "SERIOUS":
				case "WARN":
				default:
					labelStr = '可用';
					param.stops = [ [ 0, green ] ];
					break;
				}
			} else {
				if (tdData.value) {
					if (tdData.soliType && tdData.soliType == 'number') {
						param.value = tdData.value;
					} else {
						param.value = 100;
					}
					labelStr = tdData.value;// +tdData.unit;

					// #55BF3B green #DDDF0D yellow #DF5353 red #EE9A00 orange
					if (tdData.status) {
						switch (tdData.status) {
						case "CRITICAL":
							param.stops = [ [ 0, red ] ];
							break;
						case "SERIOUS":
							param.stops = [ [ 0, orange ] ];
							break;
						case "WARN":
							param.stops = [ [ 0, yellow ] ];
							break;
						default:
							param.stops = [ [ 0, green ] ];
							break;
						}
					}

				} else {
					labelStr = '--';
					param.collectTime = '--';
				}
			}
			param.label = labelStr;

			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({
				'width' : '100%',
				'height' : '100%'
			});
			var divSo = $("<div/>").css({
				'width' : '100%',
				'height' : '80%'
			});
			var divText = $('<div/>').attr('align', 'center').css({
				'width' : '100%',
				'height' : '20%'
			}).html('<span name="textSpan" >' + param.collectTime + '</span>');
			// .attr('name','metricTextDiv').attr('instanceId',dataItem.instanceId).attr('metricType',dataItem.metricType)
			// .attr('metricId',dataItem.metricId).attr('unit',dataItem.metricUnit);

			divPanel.append(divSo).append(divText);
			childTable.append(divPanel);
			divSo
					.highcharts({
						chart : {
							type : 'solidgauge',
							margin : [ 0, 0, 0, 0 ],
							spacing : [ 0, 0, 0, 0 ],
							backgroundColor : 'rgba(0,0,0,0)'
						},
						title : {
							text : ''
						},
						pane : {
							center : [ '50%', '50%' ],
							size : '80%',
							startAngle : 0,
							endAngle : 360,
							background : {
								borderWidth : 0,
								backgroundColor : Highcharts.theme.plotOptions.gauge.dial.backgroundColor,
								innerRadius : '90%',
								outerRadius : '100%',
								shape : 'arc'
							}
						},
						tooltip : {
							enabled : false
						},
						credits : {
							enabled : false
						},
						exporting : {
							enabled : false
						},
						// the value axis
						yAxis : {
							min : 0,
							max : 100,
							gridLineWidth : 0,
							minorGridLineWidth : 0,
							lineWidth : 0,
							minorTickWidth : 0,
							tickWidth : 0,
							labels : {
								enabled : false
							},
							stops : param.stops,
							opposite : true
						},
						plotOptions : {
							solidgauge : {
								dataLabels : {
									y : 10,
									borderWidth : 0,
									useHTML : true,
									shadow : true,
								}
							}
						},
						series : [ {
							data : [ parseInt(param.value) ],
							dataLabels : {
								align : 'center',
								crop : false,
								overflow : 'none',
								y : -11,
								style : {
									fontSize : 12,
									fontWeight : 'normal'
								},
								format : param.label
							},
							innerRadius : '81.5%'
						} ]
					});
		},
		createCameraProgress : function(tdDom, tdData, tdSize) {
			// 如果没有设置告警则状态为正常(特殊设置)
			if (tdData.isAlarm == false) {
				tdData.status = 'NORMAL';
			}
			var green = '#55BF3B', yellow = '#DDDF0D', red = '#DF5353', orange = '#EE9A00';
			tdDom.append("<div id='"+tdData.id+"' align='center' style='height:80px'><div class = 'quality'></div></div>");
			tdDom.find(".quality").append("<div class='"+tdData.id+"'></div>");
			if(tdData.name.length == 2){
				tdData.name = tdData.name + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			}
			if(tdData.name.length == 3){
				tdData.name = tdData.name + "&nbsp;&nbsp;&nbsp;&nbsp;"
			}
			var value = tdData.value;
			var score = 100;
			if(value == "null" || value == null){
				score = 100;
			}else{
				score = tdData.value.split(".")[0];
			}
			
			tdDom.find(".quality").append("<lable class='panel-title' style='position:absolute; top:5px;left:40px;'>" + tdData.name + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + score + "</label>");
			var color = this.returnColorByStatus(tdData.status);
			
			var progressbar = $("<div id='prog"+tdData.id+"' style='position:absolute; top:45px;left:8px;'/>");
			//progressbar.append("俺是progressbar");
			//$( "#prog"+ tdData.id).progressbar({value: 37});
			tdDom.find(".quality").append(progressbar);
			progressbar.camerabar({
				width : 160,    //设置进度条宽度 默认400 
			    height : 12,    //设置进度条高度 默认22 
			    value : score,     //设置进度条值 默认0 
			   text : '', //设置进度条百分比模板 默认 {value}%
			});
			progressbar.find(".camerabar-text").css({background: 'white'})	
			progressbar.find(".camerabar-value").css({background: color})	
		},
		createProgress : function(tdDom, tdData, tdSize) {
			// 如果没有设置告警则状态为正常(特殊设置)
			if (tdData.isAlarm == false) {
				tdData.status = 'NORMAL';
			}
			var progress = this.createPanel(tdDom, tdData, tdSize);
			var color = this.returnColorByStatus(tdData.status);
			var value = tdData.value;
			var width = tdSize == 4 ? 214 : 305;
			var height = tdSize == 4 ? 59 : 58;
			var textSize = tdSize == 4 ? '28px' : '40px';
			var availability = this.cfg.availability;
			var dataLabels_y = tdSize == 4 ? -1 : -5;
			if ($.browser.msie) {
				dataLabels_y = tdSize == 4 ? -1 : -5;
			}
			var subTitle = '';
			if (tdData.totalMemSize != undefined
					&& tdData.memPoolUsed != undefined) {
				// subTitle = "<span class='NHmemSize'>内存总容量：" +
				// tdData.totalMemSize + "</span><span
				// class='NHmemPooluesd'>内存已使用容量：" + tdData.memPoolUsed +
				// "</span>";
				progress.attr('title', "内存总容量：" + tdData.totalMemSize
						+ "\n内存已使用容量：" + tdData.memPoolUsed);
			}
			progress
					.highcharts(
							{
								chart : {
									type : 'bar',
									plotBorderWidth : 0,
									plotBackgroundColor : 'rgba(0,0,0,0)',
									backgroundColor : 'rgba(0,0,0,0)',
									margin : [ 50, 25, 50, 25 ],
									spacing : [ 0, 0, 20, 0 ]
								},
								title : {
									text : ''
								},
								subtitle : {
									text : subTitle,
									useHTML : true
								},
								xAxis : {
									categories : [ '' ],
									lineColor : 'rgba(0,0,0,0)'
								},
								yAxis : {
									min : 0,
									max : 100,

									gridLineWidth : 0,
									tickInterval : 20,
									tickWidth : 1.5,
									tickLength : 7,

									minorGridLineWidth : 0,
									minorTickInterval : 10,
									minorTickLength : 7,
									minorTickWidth : 1.5,
									title : {
										text : null
									},
									labels : {
										style : {
											fontSize : '13px'
										}
									},
									lineColor : 'rgba(0,0,0,0)',
									offset : 19
								},
								credits : {
									enabled : false
								},
								exporting : {
									enabled : false
								},
								tooltip : {
									enabled : false
								},
								legend : {
									enabled : false
								},
								plotOptions : {
									bar : {
										dataLabels : {
											enabled : true,
											format : tdData.value != null
													&& tdData.value != undefined
													&& availability != 'CRITICAL_NOTHING'
													&& availability != "CRITICAL" ? (tdData.value == 'N/A' ? 'N/A'
													: '{y}%')
													: '--',
											shadow : true,
											y : dataLabels_y,
											verticalAlign : 'middle',
											style : {
												fontSize : textSize
											}
										},
										pointWidth : 55,
										borderWidth : 1,
										borderRadius : 5,
										borderColor : 'rgba(0,0,0,0)',
										shadow : true
									}
								},
								series : [ {
									color : color,
									data : [ 0 ]
								} ]
							},
							function(chart) {
								chart.renderer
										.rect(24, 51, width, height, 5)
										.attr(
												{
													'stroke-width' : 2,
													stroke : Highcharts.theme.resourceProgressBorderColor,
													fill : Highcharts.theme.resourceProgressFillColor,
													'z-index' : -1
												}).add();
								if (tdData.value != null
										&& tdData.value != undefined
										&& tdData.value != 'N/A') {
									var series = chart.series;
									for ( var index in series) {
										var points = series[index].points;
										if (!!points) {
											if (availability == 'CRITICAL_NOTHING'
													|| availability == "CRITICAL") {
											} else {
												points[0]
														.update(parseFloat(tdData.value));
											}

										}
									}
								}
							});
		},
		createGraph : function(tdDom, tdData, tdSize) {
			var that = this;
			var graph = this.createPanel(tdDom, tdData, tdSize);
			if (tdData.historyData == 'undefined')
				tdData.historyData = [];
			if (tdData.historyData.length == 0) {
				graph.append('当前无数据');
				return false;
			}
			if (tdData.historyDataColTim == undefined)
				tdData.historyDataColTim = [];
			var step = tdData.historyDataColTim.length > 4 ? Math
					.floor(tdData.historyDataColTim.length / 4) : null;
			graph.highcharts({
				chart : {
					type : 'area',
					margin : [ 10, 20, 25, 57 ],
					backgroundColor : 'rgba(0,0,0,0)'
				},
				title : {
					text : ' '
				},
				subtitle : {
					text : ' '
				},
				credits : {
					enabled : false
				},
				exporting : {
					enabled : false
				},
				legend : {
					enabled : false
				},
				xAxis : {
					categories : tdData.historyDataColTim,
					lineWidth : 0.5,
					gridLineWidth : 0,
					labels : {
						enabled : true,
						// step : step,
						staggerLines : 1
					},
					tickWidth : 1,
					tickInterval : step,
					minorTickWidth : 0,
					tickmarkPlacement : 'on'
				},
				yAxis : {
					min : 0,
					lineWidth : 1,
					gridLineWidth : 0,
					labels : {
						enabled : true
					},
					title : {
						enabled : true,
						margin : 1,
						text : '单位：' + tdData.unit,
						style : {
							"font-weight" : "normal"
						}
					},
					tickWidth : 0.5
				},
				tooltip : {
					enabled : true,
					formatter : function() {
						return unitTransform(this.y, tdData.unit);
					}
				},
				plotOptions : {
					area : {
						color : '#00AAFF',
						lineWidth : 0,
						pointStart : 0,
						marker : {
							enabled : true,
							symbol : 'circle',
							radius : 0.5
						}
					}
				},
				series : [ {
					data : tdData.historyData
				} ]
			});
		},
		createChildrenRes : function(tdDom, tdData, tdSize) {
			var that = this;
			var childrenRes = this.createPanel(tdDom, tdData, tdSize);
			if (tdData.value.length == 0) {
				childrenRes.html('当前无数据');
				return false;
			}
			var outerHigChar = $("<div/>").width('100%').height('136px');
			var higContainer = $("<div/>").width('100%').height('100%');
			var d_page = $("<div/>").attr('style', 'text-align: center;');
			childrenRes.append(outerHigChar.append(higContainer))
					.append(d_page);

			var pageNum = Math.ceil(parseInt(tdData.value.length) / 4);
			for (var i = 0; i < pageNum; i++) {
				var aNode = $("<a/>").attr('pageNo', i + 1)
						.attr('type', 'show').addClass("d-page-ico").addClass(
								'd-page-dot');
				if (i == 0) {
					var left = $("<a/>").attr('type', 'prev').addClass(
							"d-page-ico").addClass("d-page-l");
					aNode.addClass('d-page-greendot');
					d_page.append(left).append(aNode);
				}
				d_page.append(aNode);
				if (i + 1 == pageNum) {
					var rightNode = $("<a/>").attr('type', 'next').addClass(
							"d-page-ico").addClass("d-page-r");
					d_page.append(rightNode);
				}
			}
			// 对分页按钮添加事件
			d_page.find("a").on(
					'click',
					function() {
						var clickA = $(this);
						var currentA = d_page.find(".d-page-greendot");
						var firstA = d_page.find("a[type='show']:first");
						var lastA = d_page.find("a[type='show']:last");

						currentA.removeClass("d-page-greendot").addClass(
								"d-page-dot");
						switch (clickA.attr('type')) {
						case 'prev':
							if (currentA.prev("a").attr('type') == 'show') {
								currentA = currentA.prev("a");
							} else {
								currentA = lastA;
							}
							break;
						case 'next':
							if (currentA.next("a").attr('type') == 'show') {
								currentA = currentA.next("a");
							} else {
								currentA = firstA;
							}
							break;
						case 'show':
							currentA = clickA;
							break;
						default:
							break;
						}
						that.changePageContent(currentA, 4, tdData.type,
								tdData.value, higContainer, tdData.childtype)
					});
			// 如果只有一页则隐藏翻页按钮
			if (pageNum == 1) {
				d_page.hide();
			}
			// 初始化第一页的显示
			this.createPageChildRes(1, 4, tdData.type, tdData.value,
					higContainer, tdData.childtype);

		},
		changePageContent : function(currentA, pageSize, type, value,
				container, childtype) {
			var that = this;
			currentA.removeClass("d-page-dot").addClass("d-page-greendot");
			container.fadeOut('fast', function() {
				that.createPageChildRes(parseInt(currentA.attr('pageNo')),
						pageSize, type, value, container, childtype);
				container.fadeIn('fast');
			});
		},
		createPageChildRes : function(pageNo, pageSize, type, value, container,
				childtype) {
			container.empty();
			var startNo = parseInt(pageNo) == 1 ? 0
					: (parseInt(pageNo - 1) * parseInt(pageSize));
			var endNo = Math.min((parseInt(pageNo) * parseInt(pageSize)),
					value.length);
			for (var i = startNo; i < endNo; i++) {
				switch (type) {
				case "multiMeter":

					var dataResult = value[i];
					var dataTemp;
					var childtypeTemp = childtype[0];
					switch (childtypeTemp) {
					case "Partition":
						// 分区
						dataTemp = {
							isAlarm : dataResult.isAlarm,
							status : dataResult.fileSysRateStatus,
							name : dataResult.fileSysName,
							unUsedSize : dataResult.fileSysFreeSize,
							usedSize : dataResult.fileSysUsedSize,
							rate : dataResult.fileSysRate,
							prefixStr : "分区:"
						};
						break;
					case "VCSHost":
						dataTemp = {
							isAlarm : dataResult.isAlarm,
							status : dataResult.fileSysRateStatus,
							name : dataResult.fileSysName,
							unUsedSize : dataResult.fileSysFreeSize,
							usedSize : dataResult.fileSysUsedSize,
							rate : dataResult.fileSysRate,
							prefixStr : ""
						};
						break;
					default:
						dataTemp = {};
						break;
					}
					this.createLitMeter(container, dataTemp);

					break;
				case "fan":
					this.createLitFan(container, value[i]);
					break;
				case "battery":
					this.createLitBattery(container, value[i]);
					break;
				case "cpu":
					this.createLitCpu(container, value[i]);
					break;
				case "camera":
					this.createCamera(container, value[i]);
					break;
				case "microphone":
					this.createMike(container, value[i]);
					break;
				case "display":
					this.createVideo(container, value[i]);
					break;
				case "node":
					this.createNode(container, value[i]);
					break;
				case "commonChildrenPanel":
					this.createCommonChildrenPanel(container, value[i]);
					break;
				}
				// if(type == 'multiMeter'){
				// var dataResult = value[i];
				// var dataTemp;
				// var childtypeTemp = childtype[0];
				// switch(childtypeTemp){
				// case "Partition":
				// //分区
				// dataTemp =
				// {isAlarm:dataResult.isAlarm,status:dataResult.fileSysRateStatus,
				// name:dataResult.fileSysName,unUsedSize:dataResult.fileSysFreeSize,
				// usedSize:dataResult.fileSysUsedSize,rate:dataResult.fileSysRate,prefixStr:"分区:"};
				// break;
				// case "VCSHost":
				// dataTemp =
				// {isAlarm:dataResult.isAlarm,status:dataResult.fileSysRateStatus,
				// name:dataResult.fileSysName,unUsedSize:dataResult.fileSysFreeSize,
				// usedSize:dataResult.fileSysUsedSize,rate:dataResult.fileSysRate,prefixStr:""};
				// break;
				// default :
				// dataTemp={};
				// break;
				// }
				// this.createLitMeter(container, dataTemp);
				// } else if(type == 'fan'){
				// this.createLitFan(container, value[i]);
				// } else if(type == 'battery'){
				// this.createLitBattery(container, value[i]);
				// } else if(type == 'cpu'){
				// this.createLitCpu(container, value[i]);
				// }else if(type == 'camera'){
				// this.createCamera(container, value[i]);
				// }else if(type == 'microphone'){
				// this.createMike(container, value[i]);
				// }else if(type == 'display'){
				// this.createVideo(container, value[i]);
				// }else if(type == 'node'){
				// this.createNode(container, value[i]);
				// }
			}
		},
		createCommonChildrenPanel : function(container, data) {

			if (!container.hasClass("fan")) {
				container.addClass("fan");
			}
			if (container.find("ul").length == 0) {
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var normalClass = data.normalClass;
			var criticalClass = data.criticalClass;
			var width = data.width;
			var adaptClass = '';
			if (width == "33%" || width == "33%") {
				adaptClass = ' width103';
			}

			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='"
					+ criticalClass + "' />"
					: "<span class='" + normalClass + "' />";
			li += "<span class='hardware_name" + adaptClass + "'>"
					+ data.resourceName + "</span>"
			container.find("ul").append(
					"<li title='" + data.resourceName + "'>" + li + "</li>");
		},
		createCommonOneChildrenPanel : function(tdDom, tdData, tdSize) {
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({
				'width' : '100%'
			});

			var values = tdData.value;
			if (values)
				for (var i = 0; i < values.length; i++) {
					var temp = values[i];
					var nameP = temp['resourceName'];
					var tempClass = 'allocation-margin ';
					var value = temp['instance_state'];

					var green = temp['normalClass'];
					var red = temp['criticalClass'];

					if (value == "CRITICAL" || value == "CRITICAL_NOTHING") {
						tempClass += red;// "ico-allocation ico-source-red";
					} else {
						tempClass += green;// "ico-allocation
											// ico-source-green";
					}

					var classTemp = "";
					if (values.length == 3) {
						classTemp = "margin: 16px 15px 0px 45px";
					}
					var div = $("<div class='video_class clearl textc' style='"
							+ classTemp + "'/>");
					var span = $("<span />").addClass(tempClass);
					var p = $("<p></p>").html(nameP).addClass('p-width');
					div.append(span).append(p);
					divPanel.append(div);
				}

			childTable.addClass('oc-wbg').css({
				"overflow-y" : "auto"
			}).append(divPanel);

		},
		createCommonOneMetricPanel : function(tdDom, tdData, tdSize) {
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({
				'width' : '100%'
			});

			var value = tdData.value;
			var tempClass = 'allocation-margin ';
			tempClass += tdData.normalClass;

			var div = $("<div class='video_class clearl textc' />");
			var span = $("<span />").addClass(tempClass);
			var p = $("<p></p>").html(value).addClass('p-width');
			div.append(span).append(p);
			divPanel.append(div);

			childTable.addClass('oc-wbg').css({
				"overflow-y" : "auto"
			}).append(divPanel);

		},
		createLitMeter : function(container, data) {
			// 如果没有设置告警则状态为正常(特殊设置)
			if (data.isAlarm == false) {
				data.status = 'NORMAL';
			}
			var name = typeof (data.name) == 'undefined' ? '' : data.name;
			var unUsedSize = typeof (data.unUsedSize) == 'undefined' ? ''
					: data.unUsedSize;
			var usedSize = typeof (data.usedSize) == 'undefined' ? ''
					: data.usedSize;
			var rate = typeof (data.rate) != undefined
					&& typeof (data.rate) != 'null' && data.rate != 'N/A' ? parseFloat(data.rate)
					: 0;
			var hd = $("<div/>").addClass("w-parbg").width(161).height(50);

			var nameStr = name;
			if (data.prefixStr) {
				nameStr = data.prefixStr + name;
			}
			var innerhd = $("<div/>").addClass("svg-name").html(nameStr).attr(
					'title', name);
			var innerhd2 = $("<div/>").addClass("svg-panel").append(
					$("<span/>").addClass("svg-num").attr('style',
							'float:left;')).append(
					$("<div/>").addClass("svg-pic").css({
						'width' : '40px',
						'height' : '40px'
					})).append(
					$("<span/>").addClass("svg-num").attr('style',
							'float:right;'));
			container.append(hd.append(innerhd).append(innerhd2));
			var spans = hd.find("span");
			$(spans[0]).attr("title", unUsedSize).html('可用<BR>' + unUsedSize);
			$(spans[1]).attr("title", usedSize).html('已用<BR>' + usedSize);
			var color = this.returnColorByStatus(data.status);
			hd
					.find(".svg-pic")
					.highcharts(
							{
								chart : {
									type : 'solidgauge',
									margin : [ 0, 0, 0, 0 ],
									spacing : [ 0, 0, 0, 0 ],
									backgroundColor : 'rgba(0,0,0,0)'
								},
								title : {
									text : ''
								},
								pane : {
									center : [ '50%', '50%' ],
									size : '100%',
									startAngle : 0,
									endAngle : 360,
									background : {
										borderColor : Highcharts.theme.resourceLitMeterBorderColor,
										borderWidth : 0,
										backgroundColor : Highcharts.theme.resourceLitMeterBackgroundColor,
										innerRadius : '80%',
										outerRadius : '100%',
										shape : 'arc'
									}
								},
								tooltip : {
									enabled : false
								},
								credits : {
									enabled : false
								},
								exporting : {
									enabled : false
								},
								// the value axis
								yAxis : {
									min : 0,
									max : 100,
									stops : [ [ 0.1, color ], [ 0.5, color ],
											[ 0.9, color ] ],
									gridLineWidth : 0,
									minorGridLineWidth : 0,
									lineWidth : 0,
									minorTickWidth : 0,
									tickWidth : 0,
									labels : {
										enabled : false
									},
									opposite : true
								},
								plotOptions : {
									solidgauge : {
										dataLabels : {
											y : 10,
											borderWidth : 0,
											useHTML : true,
											shadow : true,
										}
									}
								},
								series : [ {
									data : [ rate ],
									dataLabels : {
										align : 'center',
										crop : false,
										overflow : 'none',
										y : -11,
										style : {
											fontSize : 12,
											fontWeight : 'normal'
										},
										format : '<span style="font-size:12px;color:#FFFFFF;letter-spacing:-1px">'
												+ (data.rate != null
														&& data.rate != undefined ? (data.rate == 'N/A' ? 'N/A'
														: '{y}%')
														: '--') + '</span>'
									},
									innerRadius : '81.5%'
								} ]
							},
							function(chart) {
								chart.renderer
										.circle(20, 20, 16.2)
										.attr(
												{
													'stroke-width' : 0,
													stroke : Highcharts.theme.resourceLitMeterFillBorderColor,
													fill : Highcharts.theme.resourceLitMeterFillBackgroundColor,
													zIndex : -1
												}).add();
							});
		},
		createMike : function(container, data) {
			if (!container.hasClass("fan")) {
				container.addClass("fan");
			}
			if (container.find("ul").length == 0) {
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='valnet-ico mike-red' />"
					: "<span class='valnet-ico mike-green' />";
			li += "<span class='hardware_name'>" + data.microphoneName
					+ "</span>"
			container.find("ul").append(
					"<li title='" + data.microphoneName + "'>" + li + "</li>");
		},
		createVideo : function(container, data) {
			if (!container.hasClass("fan")) {
				container.addClass("fan");
			}
			if (container.find("ul").length == 0) {
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='valnet-ico video-red' />"
					: "<span class='valnet-ico video-green' />";
			li += "<span class='hardware_name'>" + data.displayName + "</span>"
			container.find("ul").append(
					"<li title='" + data.displayName + "'>" + li + "</li>");
		},
		createNode : function(container, data) {
			if (!container.hasClass("fan")) {
				container.addClass("fan");
			}
			if (container.find("ul").length == 0) {
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='valnet-ico v-node-red' />"
					: "<span class='valnet-ico v-node-red' />";
			li += "<span class='hardware_name'>" + data.nodeName + "</span>"
			container.find("ul").append(
					"<li title='" + data.nodeName + "'>" + li + "</li>");
		},
		createCamera : function(container, data) {
			if (!container.hasClass("fan")) {
				container.addClass("fan");
			}
			if (container.find("ul").length == 0) {
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='valnet-ico camera-red' />"
					: "<span class='valnet-ico camera-green' />";
			li += "<span class='hardware_name'>" + data.cameraName + "</span>"
			container.find("ul").append(
					"<li title='" + data.cameraName + "'>" + li + "</li>");
		},
		createLitFan : function(container, data) {
			if (!container.hasClass("fan")) {
				container.addClass("fan");
			}
			if (container.find("ul").length == 0) {
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='ico_hardware ico_fan_red' />"
					: "<span class='ico_hardware ico_fan_green' />";
			li += "<span class='hardware_name'>" + data.fanName + "</span>"
			container.find("ul").append(
					"<li title='" + data.fanName + "'>" + li + "</li>");
		},
		createLitBattery : function(container, data) {
			if (!container.hasClass("battery")) {
				container.addClass("battery");
			}
			if (container.find("ul").length == 0) {
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='ico_hardware ico_battery_red' />"
					: "<span class='ico_hardware ico_battery_green' />";
			li += "<span class='hardware_name'>" + data.powerName + "</span>"
			container.find("ul").append(
					"<li title='" + data.powerName + "'>" + li + "</li>");
		},
		createLitCpu : function(container, data) {
			if (!container.hasClass("cpu")) {
				container.addClass("cpu");
			}
			if (container.find("ul").length == 0) {
				container.append("<ul/>");
			}
			var metricIdStr = 'instance_state';
			var status = data[metricIdStr];
			var li = status == 'CRITICAL' || status == 'CRITICAL_NOTHING' ? "<span class='ico_hardware ico_cpu_red' />"
					: "<span class='ico_hardware ico_cpu_green' />";
			li += "<span class='hardware_name'>" + data.processorName
					+ "</span>"
			container.find("ul").append(
					"<li title='" + data.processorName + "'>" + li + "</li>");
		},
		createTabs : function(tdDom, tdData, tdSize) {
			var that = this;
			var tabsDom = this.createPanel(tdDom, tdData, tdSize);
			var tabsHeight = '206px';
			for (var i = 0; i < tdData.childtype.length; i++) {
				var childType = tdData.childtype[i];

				switch (childType) {
				case "prefmetric":
					this.createPrefMetric(tabsDom);
					break;
				case "infometric":
					this.createInfoMetric(tabsDom);
					break;
				case "storageDevice":
					tabsHeight = '166px';
					this.createStorageDevice(tdData.value, tabsDom);
					break;
				case "accessDevice":
					tabsHeight = '166px';
					this.createAccessDevice(tdData.value, tabsDom);
					break;
				case "alarm-unRestore":
					tabsHeight = '245px';

					this.createAlarmTabs(tabsDom, 'unRecovered');
					break;
				case "alarm-restore":
					tabsHeight = '245px';
					this.createAlarmTabs(tabsDom, 'recovered');
					break;

				default:
					this.createChildPanel(tabsDom, childType);
					break;
				}
			}

			var insId = this.cfg.instanceId;
			tabsDom
					.addClass('easyui-tabs')
					.tabs(
							{
								width : '100%',
								height : tabsHeight,
								fit : false,
								onSelect : function(title, index) {
									// 信息指标添加刷新按钮
									if (index == 1) {
										var InfoMetricDom = tabsDom
												.find('div .tabs-wrap > ul');
										// InfoMetricDom.append('<span
										// style="height: 27px; float: right;
										// margin-right: 10px;">'+
										// '<span class="ico ico-refrash"
										// style="margin-top: 5px;"
										// title="手动采集"></span>'+
										// '</span>');
										// InfoMetricDom.append(
										// // '<div class="right">'+
										// '<a id="collectBtn" style="height:
										// 27px; float: right; class="l-btn
										// l-btn-small l-btn-plain" group="">'+
										// '<span class="l-btn-left
										// l-btn-icon-left">'+
										// '<div class="btn-l">'+
										// '<div class="btn-r">'+
										// '<div class="btn-m">'+
										// '<span
										// class="l-btn-text">采集信息指标</span>'+
										// '<span class="l-btn-icon
										// icon-refrash"> </span>'+
										// '</div>'+
										// '</div>'+
										// '</div>'+
										// '</span>'+
										// '</a>');
										// '</div>');
										InfoMetricDom
												.find('#collectBtn')
												.click(
														function() {
															oc.util
																	.ajax({
																		url : oc.resource
																				.getUrl('portal/resource/resourceDetailInfo/getMetricHand.htm'),
																		data : {
																			instanceId : insId
																		},
																		timeout : 60000,
																		success : function(
																				data) {
																			if (data.data == "FALSE") {
																				alert("采集失败");
																			} else {
																				alert("指标采集在1分钟以后完成，请稍后进入该页面...");
																				InfoMetricDom
																						.find(
																								'#collectBtn')
																						.unbind(
																								"click");
																			}

																		}
																	});
														})
									} else {
										$('div .tabs-wrap > ul > #collectBtn')
												.remove();
									}
								}
							});
		},
		createAlarmTabs : function(tabsDom, alarmType) {
			var that = this;
			var insId = that.cfg.instanceId;
			var title = '未恢复';
			if ('recovered' == alarmType) {
				title = '已恢复';
			}
			var infoMetricDom = $("<div/>").addClass('infoMetric').attr(
					'title', title);
			var detailDom = $("<div/>").addClass(alarmType);
			infoMetricDom.append(detailDom);
			tabsDom.append(infoMetricDom);

			var detailDg = infoMetricDom.find("." + alarmType);
			var datagrid = oc.ui
					.datagrid({
						selector : detailDg,
						fit : true,
						queryParams : {
							instanceid : insId,// 2172
							alarmType : alarmType
						},
						url : oc.resource
								.getUrl("alarm/alarmManagement/getNotRestoreAlarmForResourceInfoPage.htm"),
						columns : [ [
								{
									field : 'content',
									title : '告警内容',
									width : '70%'
								},
								{
									field : 'collectionTime',
									title : '产生时间',
									width : '30%',
									ellipsis : true,
									formatter : function(value) {
										var date = new Date(value);
										var str = date.getFullYear()
												+ '-'
												+ (date.getMonth() + 1)
												+ '-'
												+ date.getDate()
												+ '  '
												+ (date.getHours() < 10 ? '0'
														+ String(date
																.getHours())
														: String(date
																.getHours()))
												+ ':'
												+ (date.getMinutes() < 10 ? '0'
														+ String(date
																.getMinutes())
														: String(date
																.getMinutes()))
												+ ':'
												+ (date.getSeconds() < 10 ? '0'
														+ String(date
																.getSeconds())
														: String(date
																.getSeconds()));
										return str;
									}
								} ] ],
						// loadFilter : function(data){
						// var rows = new Array();
						// for(var i = 0; i < data.data.rows.length; i ++){
						// var row = data.data.rows[i];
						// if(!row.isTable){
						// var currentVal = row.currentVal;
						// row.currentVal = (currentVal != null && currentVal !=
						// undefined) ? currentVal : "--";
						// var lastCollTime = row.lastCollTime;
						// row.lastCollTime = !!lastCollTime ? lastCollTime :
						// "--";
						// rows.push(row);
						// }
						// }
						// data.data.rows = rows;
						// return data.data;
						// },
						onLoadSuccess : function(data) {
							that.updateTableWidth(datagrid);
						},
						fitColumns : false,
						pagination : true,
						singleSelect : true
					});

			return infoMetricDom;
		},
		createStorageDevice : function(tdData, tabsDom) {
			var storageDeviceDom = $("<div/>").addClass('storageDevice').attr(
					'title', '存储设备');
			tabsDom.append(storageDeviceDom);

			if (tdData == undefined || JSON.stringify(tdData) == "{}") {
				return false;
			}
			var controlComponent = $("<div/>").addClass("Storage"), controlUl = $("<ul/>");
			controlComponent.append("<h6>控制组件</h6>").append(controlUl);
			if (tdData.StorageProcessorSystem != null
					&& tdData.StorageProcessorSystem != undefined) {
				this.createChildResInfo(tdData.StorageProcessorSystem,
						controlUl);
			}
			if (tdData.Node != null && tdData.Node != undefined) {
				this.createChildResInfo(tdData.Node, controlUl);
			}
			this.createChildResInfo(tdData.FCPort, controlUl);

			var storageComponent = $("<div/>").addClass("Storage"), storageUl = $("<ul/>");
			storageComponent.append("<h6>存储组件</h6>").append(storageUl);
			this.createChildResInfo(tdData.DiskDrive, storageUl);
			this.createChildResInfo(tdData.StoragePool, storageUl);
			this.createChildResInfo(tdData.StorageVolume, storageUl);
			// this.createChildResInfo(tdData.MDisk, storageUl);

			storageDeviceDom.append(controlComponent);
			storageDeviceDom.append(storageComponent);
			storageDeviceDom.append("<div class='clear'></div>");
		},
		createAccessDevice : function(tdData, tabsDom) {
			return false;
			var accessDeviceDom = $("<div/>").addClass('accessDevice').attr(
					'title', '接入设备');
			tabsDom.append(accessDeviceDom);
		},
		createPrefMetric : function(tabsDom) {
			var that = this;
			var prefMetricDom = $("<div/>").addClass('perfMetric').attr(
					'title', '性能指标');
			tabsDom.append(prefMetricDom);
			var introDom = $("<div/>").addClass('intro').css('float', 'left')
					.append($("<div/>").append("<ul/>"));
			prefMetricDom.append(introDom);
			var detailDom = $("<div/>").addClass('detail');
			prefMetricDom.append(detailDom);
			var infoDom = $("<div/>").addClass('info').width('300px');
			detailDom.append(infoDom);
			var valueArray = [ 'status', 'thresholds', 'currentVal',
					'lastCollTime' ];
			var descArray = [ '状态', '阈值', '当前值', '最近采集时间' ];
			for (var i = 0; i < valueArray.length; i++) {
				var innerDom = $("<div/>").addClass('oc-info-boxbg');
				var descDom = $("<div/>").addClass('oc-info-title').html(
						descArray[i]);
				var valueDom = $("<div/>").addClass(valueArray[i]).addClass(
						'oc-info-value');
				innerDom.append(descDom).append(valueDom);
				infoDom.append(innerDom);
			}
			var hicharDom = $("<div/>").addClass('hichar')
					.css('float', 'right').width('533px').height('152px');
			var highChartContentDom = $("<div/>").addClass('highChartContent')
					.width('100%').height('100%');
			hicharDom.append(highChartContentDom);
			detailDom.append(hicharDom);

			// 性能指标
			prefMetricDom.find(".intro div").panel({
				width : '210px',
				height : '157px',
				fit : false
			});

			// HIGHCHART
			this.createHigchar(prefMetricDom
					.find(".hichar > .highChartContent"));
			prefMetricDom
					.find("ul")
					.tree(
							{
								fit : true,
								url : oc.resource
										.getUrl("portal/resource/resourceDetailInfo/getMetric.htm?dataType=json"
												+ "&instanceId="
												+ that.cfg.instanceId
												+ "&metricType=PerformanceMetric"),
								loadFilter : function(data, parent) {
									var newData = new Array();
									var rows = data.data.rows;
									for (var i = 0; i < rows.length; i++) {
										var row = rows[i];
										var color = 'green';
										if (row.isAlarm) {
											switch (row.status) {
											case 'CRITICAL':
												color = 'red'
												break;
											case 'SERIOUS':
												color = 'orange'
												break;
											case 'WARN':
												color = 'yellow'
												break;
											// case 'UNKOWN':
											// color = 'gray'
											// break;
											case 'NORMAL':
											case 'NORMAL_NOTHING':
												color = 'green'
												break;
											default:
												color = 'green'
												break;
											}
										}

										// if(that.cfg.availability ==
										// 'CRITICAL' || that.cfg.availability
										// == 'CRITICAL_NOTHING' //||
										// that.cfg.availability == 'UNKOWN' ||
										// that.cfg.availability ==
										// 'UNKNOWN_NOTHING'
										// ){
										// color = 'gray';
										// }

										row.text = "<span class='w-ico w-"
												+ color + "ico'/><span title='"
												+ row.text + "'>" + row.text
												+ "</span>";
										newData.push({
											id : row.id,
											text : row.text,
											attributes : row
										});
										if (i == 0) {
											that.clickTree(newData[0],
													prefMetricDom);
										}
									}
									return newData;
								},
								onBeforeLoad : function(node, param) {
									oc.ui.progress();
								},
								onLoadSuccess : function(node, data) {
									$.messager.progress('close');
									prefMetricDom.find('.tree-icon').each(
											function() {
												var treeIcon = $(this);
												treeIcon.removeClass();
											});
									$(prefMetricDom.find("ul > li").get(0))
											.find(".tree-node").addClass(
													'tree-node-selected');
								},
								onLoadError : function(arguments) {
									$.messager.progress('close');
								},
								onClick : function(node) {
									that.clickTree(node, prefMetricDom);
								}
							});

			return prefMetricDom;
		},
		createInfoMetric : function(tabsDom) {
			var that = this;
			var infoMetricDom = $("<div/>").addClass('infoMetric').attr(
					'title', '信息指标');
			var detailDom = $("<div/>").addClass('detail');
			infoMetricDom.append(detailDom);
			tabsDom.append(infoMetricDom);

			// 信息指标
			var detailDg = infoMetricDom.find(".detail");
			that._datagrid = datagrid = oc.ui
					.datagrid({
						selector : detailDg,
						fit : true,
						url : oc.resource
								.getUrl("portal/resource/resourceDetailInfo/getMetric.htm?dataType=json"
										+ "&instanceId="
										+ that.cfg.instanceId
										+ "&metricType=InformationMetric"),
						columns : [ [ {
							field : 'text',
							title : '指标名称',
							width : '300px'
						}, {
							field : 'currentVal',
							title : '当前值',
							width : '558px',
							ellipsis : true
						}, {
							field : 'lastCollTime',
							title : '最近采集时间',
							width : '200px'
						} ] ],
						loadFilter : function(data) {
							var rows = new Array();
							for (var i = 0; i < data.data.rows.length; i++) {
								var row = data.data.rows[i];
								if (!row.isTable) {
									var currentVal = row.currentVal;
									row.currentVal = (currentVal != null && currentVal != undefined) ? currentVal
											: "--";
									var lastCollTime = row.lastCollTime;
									row.lastCollTime = !!lastCollTime ? lastCollTime
											: "--";
									rows.push(row);
								}
							}
							data.data.rows = rows;
							return data.data;
						},
						onLoadSuccess : function(data) {
							that.updateTableWidth(datagrid);
						},
						fitColumns : false,
						pagination : false,
						singleSelect : true
					});

			return infoMetricDom;
		},
		createAvailabilityMetric : function(tdDom, tdData, tdSize) {
			var that = this;
			var datagridDom = this.createPanel(tdDom, tdData, tdSize);

			var availabilityMetricDom = $("<div/>").addClass(
					'availabilityMetric').attr('title', '可用性指标');
			var detailDom = $("<div/>").addClass('detail');
			availabilityMetricDom.append(detailDom);
			datagridDom.append(availabilityMetricDom);

			// 信息指标
			var detailDg = availabilityMetricDom.find(".detail");
			var datagrid = oc.ui
					.datagrid({
						selector : detailDg,
						fit : false,
						height : '160px',
						url : oc.resource
								.getUrl("portal/resource/resourceDetailInfo/getMetric.htm?dataType=json"
										+ "&instanceId="
										+ that.cfg.instanceId
										+ "&metricType=AvailabilityMetric"),
						columns : [ [ {
							field : 'text',
							title : '指标名称',
							width : '300px',
							formatter : function(value, row, rowIndex) {
								// var colorAndTitle =
								// that.returnStatusLight(row.status, 'metric');
								// colorAndTitle.attr('title',
								// value).html(value);
								return value;
							}
						}, {
							field : 'currentVal',
							title : '当前值',
							width : '558px',
							formatter : function(value, row, rowIndex) {
								if ("1" == value) {
									value = '可用';
								} else if ("0" == value) {
									value = '不可用';
								} else if ("" == value) {
									value = '--';
								}
								return value;
							}
						}, {
							field : 'lastCollTime',
							title : '最近采集时间',
							width : '200px'
						} ] ],
						loadFilter : function(data) {
							var rows = new Array();
							for (var i = 0; i < data.data.rows.length; i++) {
								var row = data.data.rows[i];
								if (!row.isTable) {
									var currentVal = row.currentVal;
									row.currentVal = (currentVal != null && currentVal != undefined) ? currentVal
											: "--";
									var lastCollTime = row.lastCollTime;
									row.lastCollTime = !!lastCollTime ? lastCollTime
											: "--";
									rows.push(row);
								}
							}
							data.data.rows = rows;
							return data.data;
						},
						onLoadSuccess : function(data) {
							that.updateTableWidth(datagrid);
						},
						fitColumns : false,
						pagination : false,
						singleSelect : true
					});

			return availabilityMetricDom;
		},
		clickTree : function(node, prefMetricDom) {
			var that = this;
			var status = prefMetricDom.find(".status");
			var currentVal = prefMetricDom.find(".currentVal");
			var lastCollTime = prefMetricDom.find(".lastCollTime");
			var thresholds = prefMetricDom.find(".thresholds");
			var colorAndTitle = this.returnStatusLight(node.attributes.status,
					'metric');
			status.empty();
			if (!node.attributes.isAlarm) {
				status.append($("<div/>").html("未设置告警"));
			} else {
				status.append(colorAndTitle);
			}
			currentVal.html('').html(node.attributes.currentVal);
			lastCollTime.html('').html(node.attributes.lastCollTime);
			// 阈值
			thresholds.empty();
			if (typeof (node.attributes.thresholds) != 'undefined') {
				thresholds.append($("<div/>").width('140px').target(
						eval(node.attributes.thresholds), true));
			}
			// highcharts
			this.chartObj.setIds(node.id, this.cfg.instanceId);
		},
		createHigchar : function(container) {
			var highChartsId = 'general_metric_data_chart';
			var outerDom = $("<div/>").width('100%').height('100%');

			var innerBtnDom1 = $("<div/>")
					.attr('id', 'metricChartsShowValues')
					.html(
							'<span><font class="resouse-txt-color">Max</font> : </span><span name="valueMax" ></span><span>&nbsp;&nbsp;</span>'
									+ '<span><font class="resouse-txt-color">Min</font> : </span><span name="valueMin" ></span><span>&nbsp;&nbsp;</span>'
									+ '<span><font class="resouse-txt-color">Avg</font> : </span><span name="valueAvg" ></span>'
									+ '<div class="locate-right"><select id="queryTimeType" class="btnlistarry locate-center"></div></select>');

			var innerBtnDom = $("<div/>").addClass('btnlistarry locate-right')
					.attr('align', 'right').attr('style', 'z-index:999;').attr(
							'id', 'metricChartsUl');
			var btnArray = [ '1H', '1D', '7D', '30D', '自定义' ];
			var btnClassArray = [ '1H', '1D', '7D', '30D', 'dateSelect' ];
			var btnDom = $("<ul/>");
			// for(var i = 0; i < btnArray.length; i ++){
			// var btn = btnArray[i];
			// var btnClass = btnClassArray[i];
			// btnDom.append($("<li/>").addClass(btnClass).attr('name',
			// 'chartsType' + highChartsId).html(btn));
			// }
			var innerHigDom = $("<div/>").attr('id', highChartsId)
					.width('100%').height('120px');
			container.append(outerDom.append(innerBtnDom1).append(innerBtnDom)
					.append(innerHigDom));
			this.chartObj = new chartObj(innerHigDom, 1);
		},
		createChildPanel : function(tabsDom, childType) {
			var that = this;
			var panel = $("<div/>").addClass('perfMetric').attr('title', '面板图'); // this.createPanel(tdDom,
																					// tdData,
																					// tdSize);
			tabsDom.append(panel);
			var leftDom = $("<div/>"), leftUpDom = $("<div/>"), leftDownDom = $("<div/>");
			leftDom.append(leftUpDom).append(leftDownDom);
			// this.rightDom = $("<div/>");
			panel.append(leftDom);
			// .append(this.rightDom);

			leftDom.addClass("intro").css('float', 'left').width('100%')
					.height('166px');
			leftUpDom.addClass("intro").css('overflow-y', 'auto').css('border',
					'none').width('100%').height('117px');
			leftDownDom.addClass("intro intro-bgcolor").width('100%').height(
					'30px');
			// this.rightDom.css('float', 'right').css('overflow-y',
			// 'auto').css('display','none').width('20%').height('166px').addClass("intro");

			// var state = ['green', 'green-red', 'red', 'gray', 'white'];
			// var stateName = ['UP', '协议 DOWN', '管理 DOWN', '未知', '未监控'];

			var state = [ 'green', 'green-red', 'red', 'white' ];
			var stateName = [ 'UP', '协议 DOWN', '管理 DOWN', '未监控' ];

			for (var i = 0; i < state.length; i++) {
				var stateNode = $("<div/>").css('float', 'left').css('cursor',
						'pointer').attr('state', state[i]).width('120px')
						.append(
								$("<span/>").addClass(
										"interface-eq interface-" + state[i]))
						.append(
								$("<span/>").addClass('oc-spanlocate').height(
										'100%').html(stateName[i]));
				leftDownDom.append(stateNode);
			}
			leftDownDom
					.append($("<div/>")
							.addClass('locate-right')
							.append(
									"<span><input class='logicInterfaceOnOff' type='checkbox' checked=checked style='vertical-align:middle;'></span>")
							.append("<span>显示逻辑接口</span>"));
			oc.util
					.ajax({
						url : oc.resource
								.getUrl('portal/resource/resourceDetailInfo/getChildInstance.htm'),
						data : {
							instanceId : this.cfg.instanceId,
							childType : childType
						},
						successMsg : null,
						success : function(json) {
							if (json.code == 200) {
								var childArray = [], noLogicInterfaceArray = [], noLogicInterfaceNo = 0;
								for (var i = 0; i < json.data.length; i++) {
									var child = json.data[i];
									child.no = i;
									childArray.push(child);
									if (child.ifType == 'ethernetCsmacd'
											|| child.ifType == 'gigabitEthernet'
											|| child.ifType == 'fibreChannel') {
										child.noLogicNo = noLogicInterfaceNo++;
										noLogicInterfaceArray.push(child);
									}
									// 默认显示第一个接口指标信息
									// if(i == 0){
									// that.childResourceEvent(child.id,
									// child.ifIndex);
									// }
								}
								// 加入子资源
								that.addChild(leftUpDom, childArray, false);
								// 新增接口状态点击事件
								// that.addChildClickEvent(leftUpDom,
								// leftDownDom, childArray, false);
								// 接口状态记数
								that.countInterfaceState(childArray);
								// 修改相应状态总数
								that.updateStateNodeCount(leftDownDom, state,
										stateName);
								// 过滤逻辑接口事件
								leftDownDom
										.find(".logicInterfaceOnOff")
										.on(
												'click',
												function(e) {
													if ($(this).is(":checked")) {
														// 接口状态记数
														that
																.countInterfaceState(childArray);
														// 修改相应状态总数
														that
																.updateStateNodeCount(
																		leftDownDom,
																		state,
																		stateName);
														that.addChild(
																leftUpDom,
																childArray,
																false);
													} else {
														// 接口状态记数
														that
																.countInterfaceState(noLogicInterfaceArray);
														// 修改相应状态总数
														that
																.updateStateNodeCount(
																		leftDownDom,
																		state,
																		stateName);
														that
																.addChild(
																		leftUpDom,
																		noLogicInterfaceArray,
																		true);
													}
												});
							} else {
								alert('没有查询到对应的资源信息');
								return false;
							}
						}
					});
		},
		countInterfaceState : function(childArray) {
			var that = this;
			that.child_up = 0;
			that.child_down = 0;
			that.child_unkown = 0;
			that.child_up_down = 0;
			that.child_no_monitor = 0;
			for (var i = 0; i < childArray.length; i++) {
				var child = childArray[i];
				switch (child.state) {
				case 'NOT_MONITORED':
					that.child_no_monitor++;
					break;
				case 'ADMNORMAL_OPERCRITICAL':
					that.child_up_down++;
					break;
				case 'CRITICAL':
				case 'CRITICAL_NOTHING':
					that.child_down++;
					break;
				// case 'UNKOWN':
				// case 'UNKNOWN_NOTHING':
				// that.child_unkown++;
				// break;
				default:
					that.child_up++;
					break;
				}
			}
		},
		updateStateNodeCount : function(leftDownDom, state, stateName) {
			var that = this;
			for (var i = 0; i < state.length; i++) {
				var stateNode = leftDownDom.find(
						"div[state='" + state[i] + "']").find('.oc-spanlocate');
				var count = 0;
				if (state[i] == 'green') {
					count = that.child_up;
				} else if (state[i] == 'red') {
					count = that.child_down;
				} else if (state[i] == 'gray') {
					count = that.child_unkown;
				} else if (state[i] == 'green-red') {
					count = that.child_up_down;
				} else {
					count = that.child_no_monitor;
				}
				stateNode.html(stateName[i] + " [" + count + "]");
			}
		},
		createDownDevice : function(subInstanceId, ifIndex) {
			var that = this;
			oc.resource.loadScript(
					"resource/module/topo/backboard/DownDeviceDia.js",
					function() {
						$.post(oc.resource
								.getUrl("topo/backboard/downinfo.htm"), {
							subInstanceId : subInstanceId,
							mainInstanceId : that.cfg.instanceId
						}, function(result) {
							new DownDeviceDia({
								ip : result.ip, // 上联 设备ip
								intface : result.IfName,// 上联设备接口
								deviceName : result.deviceShowName,// 上联设备名称
								intfaceIndex : ifIndex
							// 接口索引
							});
						}, "json");
					});
		},
		createChildrenResInfo : function(tdData, name) {
			var div = $("<div/>").addClass("Storage").append(
					"<h6>" + name + "</h6>");
			var ul = $("<ul/>");
			div.append(ul);
		},
		createChildResInfo : function(tdData, ul) {
			if (tdData == undefined) {
				return false;
			}
			var li = $("<li/>");
			var icoDiv = $("<div/>").addClass("stroage_img");
			var desDiv = $("<div/>").addClass("stroage_txt");
			switch (tdData.type) {
			case "StorageProcessorSystem":
				icoDiv.addClass("stroage_img03");
				break;
			case "Node":
				icoDiv.addClass("stroage_img03");
				break;
			case "FCPort":
				icoDiv.addClass("stroage_img05");
				break;
			case "DiskDrive":
				icoDiv.addClass("stroage_img02");
				break;
			case "StoragePool":
				icoDiv.addClass("stroage_img01");
				break;
			case "StorageVolume":
				icoDiv.addClass("stroage_img04");
				break;
			case "MDisk":
				icoDiv.addClass("stroage_img04");
				break;
			default:
				break;
			}
			desDiv.append("<p>" + tdData.name + "</p>");
			desDiv.append("<p>总数：" + tdData.count + "</p>");
			desDiv.append("<p>故障：<span>" + tdData.critical + "</span></p>");
			li.append(icoDiv).append(desDiv);
			ul.append(li);
		},
		addChildClickEvent : function(leftUpDom, leftDownDom, childArray,
				isNoLogicInterface) {
			var that = this;
			leftDownDom
					.find("div")
					.on(
							'click',
							function() {
								var node = $(this);
								var newDataArray = [], childNo = 0;
								for (var j = 0; j < childArray.length; j++) {
									var child = childArray[j];
									switch (node.attr('state')) {
									case 'green':
										if (child.state != 'NOT_MONITORED'
												&& child.state != 'CRITICAL'
												// && child.state != 'UNKOWN' &&
												// child.state !=
												// 'UNKNOWN_NOTHING'
												&& child.state != 'CRITICAL_NOTHING'
												&& child.state != 'ADMNORMAL_OPERCRITICAL') {
											child.no = childNo++;
											newDataArray.push(child);
										}
										break;
									case 'green-red':
										if (child.state == 'ADMNORMAL_OPERCRITICAL') {
											child.no = childNo++;
											newDataArray.push(child);
										}
										break;
									case 'red':
										if (child.state == 'CRITICAL'
												|| child.state == 'CRITICAL_NOTHING') {
											child.no = childNo++;
											newDataArray.push(child);
										}
										break;
									case 'white':
										if (child.state == 'NOT_MONITORED') {
											child.no = childNo++;
											newDataArray.push(child);
										}
										break;
									case 'gray':
										// if(child.state == 'UNKOWN' ||
										// child.state == 'UNKNOWN_NOTHING'){
										// child.no = childNo ++;
										// newDataArray.push(child);
										// }
										break;
									default:
										break;
									}
								}
								that.addChild(leftUpDom, newDataArray,
										isNoLogicInterface);
							});
		},
		addChild : function(leftUpDom, childData, isNoLogicInterface) {
			leftUpDom.empty();
			// this.rightDom.empty();
			// debugger;
			var groupCnt = Math.ceil(parseInt(childData.length) / 6);
			var rowCnt = Math.ceil(parseInt(groupCnt) / 4);
			for (var i = 0; i < rowCnt; i++) {
				var rowDom = $("<div/>").addClass('row').width('100%').height(
						'31px');
				for (var j = 0; j < 4; j++) {
					this.addChildGroup(rowDom, i, j, childData,
							isNoLogicInterface);
				}
				leftUpDom.append(rowDom);
			}
		},
		addChildGroup : function(rowDom, rowNo, groupNo, childData,
				isNoLogicInterface) {
			var that = this;
			var groupDom = $("<div/>").addClass('group').css('float', 'left')
					.width('190px').height('100%');
			var startNo = (rowNo * 4 * 6) + (groupNo * 6);
			var endNo = Math.min((startNo + 6), childData.length);
			for (var i = startNo; i < endNo; i++) {
				var child = childData[i];
				var childDom = $("<span/>").css('cursor', 'pointer').addClass(
						"interface-eq");
				switch (child.state) {
				case 'NOT_MONITORED':
					childDom.addClass("interface-white").attr('no', child.no);
					break;
				case 'ADMNORMAL_OPERCRITICAL':
					childDom.addClass("interface-green-red").attr('no',
							child.no);
					break;
				case 'CRITICAL':
				case 'CRITICAL_NOTHING':
					childDom.addClass("interface-red").attr('no', child.no);
					break;
				// case 'UNKOWN':
				// case 'UNKNOWN_NOTHING':
				// childDom.addClass("interface-gray").attr('no', child.no);
				// break;
				default:
					childDom.addClass("interface-green").attr('no', child.no);
					break;
				}
				if (child.noLogicNo != undefined) {
					childDom.attr('noLogicNo', child.noLogicNo);
				}
				// childDom.tooltip({
				// position : 'right',
				// content : child.name
				// });
				// childDom.attr('title', child.name);
				groupDom.append(childDom);
			}
			// 初始化右键menu
			$('#portMenu')
					.menu(
							{
								onClick : function(item) {
									var condition = '';
									var instanceId = $(item.target).attr(
											'instanceid');
									if (item.text.indexOf('打开') > -1) {
										condition = 'ifSetAdminUp';
									} else if (item.text.indexOf('关闭') > -1) {
										condition = 'ifSetAdminDown';
									}
									oc.util
											.ajax({
												url : oc.resource
														.getUrl("portal/resource/resourceDetailInfo/editPortStatus.htm"),
												data : {
													instanceId : instanceId,
													condition : condition
												},
												success : function(data) {
													if (data.data == 1) {
														alert('操作成功！');
													} else {
														alert('操作失败！');
													}
												}
											});
								},
							});
			groupDom.find('span').each(
					function() {
						var interDom = $(this);
						var no = isNoLogicInterface ? parseInt(interDom
								.attr('noLogicNo')) : parseInt(interDom
								.attr('no'));
						interDom.on('mousedown', function(e) {
							if (1 == e.which) { // 左键点击打开详情列表
								that.childResourceEvent(childData[no].id,
										childData[no].ifIndex);
							} else if (3 == e.which) { // 右键菜单
								var user = oc.index.getUser();
								if (user.systemUser) {
									$(document).bind(
											'contextmenu.interface-eq',
											function(e) {
												e.preventDefault();
												$('#portMenu').find(
														'#menu_open').attr(
														'instanceId',
														childData[no].id);
												$('#portMenu').find(
														'#menu_close').attr(
														'instanceId',
														childData[no].id);
												$('#portMenu').menu('show', {
													left : e.pageX,
													top : e.pageY
												});
												$(document).unbind(
														".interface-eq");
											});
								}
							}
						});
						interDom.on('mouseover', function(e) {
							if (that.tipTimeId) {
								clearTimeout(that.tipTimeId);
							}
							that.tipTimeId = null;
							that.tipTimeId = setTimeout(function() {
								oc.topo.tips.show({
									type : "port",
									x : e.pageX,
									y : e.pageY - 215 * 1.9,
									value : {
										instanceId : childData[no].id,
										ifIndex : childData[no].ifIndex
									}
								});
								that.tipTimeId = null;
							}, 500);
						});
						interDom.mouseout(function() {
							if (that.tipTimeId) {
								clearTimeout(that.tipTimeId);
								that.tipTimeId = null;
							}
							oc.topo.tips.hide();
						});
					});
			if (groupDom.find(".interface-eq").length > 0)
				rowDom.append(groupDom);
		},
		createPie : function(tdDom, tdData, tdSize) {
			var pie = this.createPanel(tdDom, tdData, tdSize);
			var unit = tdData.unit == undefined ? "" : tdData.unit;
			var tmpValue = [];
			var value = tdData.value;
			for (var i = 0; i < tdData.value.length; i++) {
				var onePoint = tdData.value[i];
				tmpValue.push([ onePoint[0], parseFloat(onePoint[1]) ]);
			}
			value = tmpValue;

			var title = tdData.resourceId == 'IBMDS'
					&& tdData.totalSpace != undefined ? {
				text : tdData.totalSpace
			} : null;

			pie
					.highcharts({
						chart : {
							type : 'pie',
							backgroundColor : 'rgba(0,0,0,0)',
							spacing : [ 0, 0, 0, 0 ],
							options3d : {
								enabled : true,
								alpha : 45,
								beta : 0
							}
						},
						title : title,
						legend : {
							layout : 'vertical',
							align : 'right',
							verticalAlign : 'middle',
							itemStyle : {
								"color" : "#9EA0A0",
								"font-size" : "8px"
							},
							navigation : {
								activeColor : '#FFFFFF',
								animation : true,
								arrowSize : 12,
								inactiveColor : '#9EA0A0',
								style : {
									"font-weight" : 'bold',
									"color" : '#9EA0A0',
									"font-size" : '12px'
								}
							},
							labelFormatter : function() {
								var name = this.name;
								if (name.length > 6) {
									name = name.substring(0, 5) + "...";
								}
								return name;
							}
						},
						plotOptions : {
							pie : {
								innerSize : '5',
								showInLegend : true,
								depth : 35,
								borderWidth : 0,
								dataLabels : {
									enabled : false
								}
							}
						},
						series : [ {
							type : 'pie',
							data : value
						} ],
						tooltip : {
							enabled : true,
							formatter : function() {
								var y = this.y;
								var newUnit = unit;
								if (unit == 'Byte') {
									if (this.y > (1024 * 1024 * 1024)) {
										y = (parseFloat(y) / (parseFloat(1024)
												* parseFloat(1024) * parseFloat(1024)))
												.toFixed(2);
										newUnit = 'GB';
									} else if (this.y > (1024 * 1024)) {
										y = (parseFloat(y) / (parseFloat(1024) * parseFloat(1024)))
												.toFixed(2);
										newUnit = 'MB';
									} else if (this.y > 1024) {
										y = (parseFloat(y) / parseFloat(1024))
												.toFixed(2);
										newUnit = 'KB';
									}
								}
								return this.key + " : " + y + newUnit;
							}
						},
						credits : {
							enabled : false
						},
						exporting : {
							enabled : false
						}
					});
		},
		createChildTable : function(tdDom, tdData, tdSize) {
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var table = $("<table/>").css({
				'width' : '100%'
			});
			childTable.addClass('oc-wbg').css({
				"overflow-y" : "auto"
			}).append(table);
			table
					.append("<tr style='height:30px;text-align:center'><th>存储资源</th><th>状态</th><th>容量</th><th>可用空间</th></tr>");
			for (var i = 0; tdData.value != null && i < tdData.value.length; i++) {
				var value = tdData.value[i];
				var name = "<span class='ico ico_vdata ico-vdatatext' title='"
						+ value.Name + "' style='cursor:auto;'>" + value.Name
						+ "</span>";
				// var name = "<span class='ico ico_vdata'
				// title='"+value.Name+"'
				// style='width:120px;cursor:auto;overflow: hidden;white-space:
				// nowrap;text-overflow: ellipsis;'>" + value.Name + "</span>";
				var status = value.availabilityStatus == 'CRITICAL' ? "<span>不可用</span>"
						: "<span class='ico ico_vright' style='cursor:auto;'>正常</span>";
				table.append("<tr style='height:30px;text-align:center'><td>"
						+ name + "</td><td>" + status + "</td><td>"
						+ value.DataStorageVolume + "</td><td>"
						+ value.DataStorageFreeSpace + "</td></tr>");
			}
		},
		createEnvironmentPanel : function(tdDom, tdData, tdSize) {
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var divPanel = $("<div/>").css({
				'width' : '100%'
			});

			var values = tdData.value;
			if (values)
				for (var i = 0; i < values.length; i++) {
					var temp = values[i];
					var nameP, tempClass = "valnet-ico ";
					var metricId = temp['metricStr'];
					var value = temp['metricStatus'];
					switch (metricId) {
					case 'rtcBatteryStatus':
						nameP = "电池";
						if (value == "CRITICAL" || value == "CRITICAL_NOTHING") {
							tempClass += "bat-red";
						} else {
							tempClass += "bat-green";
						}
						break;
					case 'temperatureStatus':
						nameP = "温度";
						if (value == "CRITICAL" || value == "CRITICAL_NOTHING") {
							tempClass += "temper-red";
						} else {
							tempClass += "temper-green";
						}
						break;
					case 'voltagesStatus':
						nameP = "电压";
						if (value == "CRITICAL" || value == "CRITICAL_NOTHING") {
							tempClass += "voltage-red";
						} else {
							tempClass += "voltage-green";
						}
						break;
					case 'fanStatus':
						nameP = "风扇";
						if (value == "CRITICAL" || value == "CRITICAL_NOTHING") {
							tempClass += "v-fan-red";
						} else {
							tempClass += "v-fan-green";
						}
						break;
					}

					var classTemp = "";
					if (values.length == 3) {
						classTemp = "margin: 16px 15px 0px 45px";
					}
					var div = $("<div class='video_class' style='" + classTemp
							+ "'/>");
					var span = $("<span />").addClass(tempClass);
					var p = $("<p></p>").html(nameP);
					div.append(span).append(p);
					divPanel.append(div);
				}

			childTable.addClass('oc-wbg').css({
				"overflow-y" : "auto"
			}).append(divPanel);

		},
		createMetricTabs : function(tdDom, tdData, tdSize) {
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var table = $("<table/>").css({
				'width' : '100%'
			});
			childTable.addClass('oc-wbg').css({
				"overflow-y" : "auto"
			}).append(table);

			var values = tdData.value;
			if (values && values.length > 0) {
				table
						.append("<tr style='height:30px;text-align:center'><th>指标名称</th><th>指标值</th></tr>");
			}
			for (var i = 0; i < values.length; i++) {
				var temp = values[i];
				var name = temp['metricName'];
				var value = temp['value'];

				var nameSpan = "<span style='cursor:auto;'>" + name + "</span>";
				// var nameSpan = "<div title='"+name+"'
				// style='width:120px;cursor:auto;overflow: hidden;white-space:
				// nowrap;text-overflow: ellipsis;'>" + name + "</div>";
				// var status = value.availabilityStatus == 'CRITICAL' ?
				// "<span>不可用</span>" : "<span class='ico ico_vright'
				// style='cursor:auto;'>正常</span>";
				var status = "<span>" + value + "</span>";

				table.append("<tr style='height:30px;text-align:center'><td>"
						+ nameSpan + "</td><td>" + status + "</td></tr>");
			}
		},
		createTableMetric : function(tdDom, tdData, tdSize) {
			var childTable = this.createPanel(tdDom, tdData, tdSize);
			var table = $("<table/>").css({
				'width' : '100%'
			});
			childTable.addClass('oc-wbg').css({
				"overflow-y" : "auto"
			}).append(table);

			var values = tdData.value;
			if (values && values.length > 0) {
				var valueTemp = values[0]['value'] + '';

				if (valueTemp && valueTemp.length > 0) {
					var valueArr = valueTemp.split(',');
					for (var i = 1; i < valueArr.length; i++) {
						var value = valueArr[i];
						var name = "<div title='"
								+ value
								+ "' style='width:220px;cursor:auto;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;'>"
								+ value + "</div>";
						table
								.append("<tr style='height:30px;text-align:left'><td>"
										+ name + "</td></tr>");
					}
				}

			}
		},
		childResourceEvent : function(instanceId, ifIndex) {
			var that = this;
			this.createDownDevice(instanceId, ifIndex);
			/*
			 * var that = this; that.rightDom.empty(); oc.util.ajax({ url :
			 * oc.resource.getUrl('portal/resource/resourceDetailInfo/getAllMetric.htm'),
			 * data : { instanceId : instanceId }, successMsg:null, success :
			 * function(json) { if (json.code == 200) { for(var j = 0; j <
			 * json.data.length; j ++){ var metric = json.data[j];
			 * if(metric.status != 'UNKOWN'){ that.rightDom.append(metric.text + " : " +
			 * (typeof(metric.currentVal) == 'undefined' || metric.currentVal == '' ||
			 * metric.currentVal == null ? '--' : metric.currentVal) + "<br>"); } } } }
			 * });
			 */
		},
		createPanel : function(tdDom, tdData, tdSize) {
			var width = tdData.type == 'tabs' || tdData.type == 'panel'
					|| tdData.type == 'datagrid' ? '100%' : tdSize == 4 ? 263
					: 352;
			if (tdData.type == 'commonChildrenPanel'
					|| tdData.type == 'commonOneChildrenPanel'
					|| tdData.type == 'commonOneMetricPanel') {
				if (tdData.width == '33%' || tdData.width == '34%') {
					width = '352px';
				} else {
					width = '263px';
				}
			} else if (tdData.type == 'commonMetricPie') {
				width = '263px';
			} else if (tdData.type == 'tabs-full') {
				width = '100%';
			}
			width = tdData.type == 'pie'
					&& (tdData.resourceId == 'IBMDS'
							|| tdData.resourceId == 'InspurAS' || tdData.categoryId == 'DiskArray') ? '250px'
					: width;
			var height = (tdData.type == 'tabs' && tdData.metrics != 'none')
					|| tdData.type == 'panel' ? 236 : 190;
			if (tdData.type == 'tabs-full') {
				height = 280;
			}
			var titleName = (tdData.name == undefined || tdData.name == '' || tdData.name == null) ? tdData.title
					: tdData.name;
			if (tdData.id == 'throughput') {
				titleName += '(最近4小时)';
			}
			var container = $("<div/>");
			tdDom.append(container);
			var panelDom = $("<div/>");
			var content = $(
					'<div style="height:100px; width: 100px; background-image:url();"></div>')
					.width('100%').height('100%');

			if (tdData.type == 'environmentPanel') {
				width = 440;
			} else if (tdData.type == 'metricTabs') {
				var widthNum;
				if (tdData.width) {
					widthNum = tdData.width.replace('%', '');
				}
				if (widthNum > 30) {
					width = 352;
				} else {
					// 设置为0,自动分配宽度
					width = 0;
				}
			} else if (tdData.type == 'tableMetric') {
				width = 0;
			} else if (tdData.type == 'node') {
				width = 352;
			}

			if (tdData.type == 'cameraprogress') {
				width = 180;
				height = 60;
			}
			// titleName = "";
			container.append(panelDom.append(content));
			if (tdSize == 3 && tdDom[0].cellIndex == 1) {
				panelDom.panel({
					title : titleName,
					width : width,
					height : height
				// ,headerCls:'panel_kuan'
				});
				container.find('.panel');// .addClass('panel_kuan');
			} else {
				panelDom.panel({
					title : titleName,
					width : width,
					height : height
				});
			}
			return content;

		},
		returnColorByStatus : function(status) {
			var newStatus = status
			// if(this.cfg.availability == 'CRITICAL' || this.cfg.availability
			// == 'CRITICAL_NOTHING'
			// || this.cfg.availability == 'UNKNOWN_NOTHING' ||
			// this.cfg.availability == 'UNKOWN'){
			// newStatus = 'UNKOWN';
			// }
			var color = '#54CA23';
			switch (newStatus) {
			case 'CRITICAL':
				color = '#EC5707';
				break;
			case 'SERIOUS':
				color = '#F96E03';
				break;
			case 'WARN':
				color = '#EFBC0D';
				break;
			// case 'UNKOWN':
			// color = '#797979';
			// break;
			case 'NORMAL':
			case 'NORMAL_NOTHING':
				color = '#54CA23';
				break;
			default:
				color = '#54CA23';
				break;
			}
			return color;
		},
		returnStatusLight : function(status, type) {
			var newStatus = status
			if (type == 'mainInstance') {
				var color = 'light-ico_resource res_unknown_nothing';
				switch (newStatus) {
				case 'CRITICAL':
					color = "light-ico_resource res_critical";
					break;
				case 'CRITICAL_NOTHING':
					color = "light-ico_resource res_critical_nothing";
					break;
				case 'SERIOUS':
					color = "light-ico_resource res_serious";
					break;
				case 'WARN':
					color = "light-ico_resource res_warn";
					break;
				case 'NORMAL':
				case 'NORMAL_NOTHING':
					color = "light-ico_resource res_normal_nothing";
					break;
				case 'NORMAL_CRITICAL':
					color = "light-ico_resource res_normal_critical";
					break;
				case 'NORMAL_UNKNOWN':
					color = "light-ico_resource res_normal_unknown";
					break;
				// case 'UNKNOWN_NOTHING':
				// color = "light-ico_resource res_unknown_nothing";
				// break;
				// case 'UNKOWN':
				// color = "light-ico_resource res_unkown";
				// break;
				default:
					color = "light-ico_resource res_normal_nothing";
					break;
				}
				return $("<label/>")
						.css('cssText', 'min-width:25px !important').addClass(
								color);
			} else {
				// if(this.cfg.availability == 'CRITICAL' ||
				// this.cfg.availability == 'CRITICAL_NOTHING' ||
				// this.cfg.availability == 'UNKOWN' || this.cfg.availability ==
				// 'UNKNOWN_NOTHING'){
				// newStatus = 'UNKOWN';
				// }
				var color = 'green';
				var title = '未知';
				switch (newStatus) {
				case 'CRITICAL':
					color = 'red';
					title = '致命';
					break;
				case 'SERIOUS':
					color = 'orange';
					title = '严重';
					break;
				case 'WARN':
					color = 'yellow';
					title = '警告';
					break;
				// case 'UNKOWN':
				// color = 'gray';
				// title = '未知';
				// break;
				case 'UNKOWN':
				case 'UNKNOWN_NOTHING':
				case 'NORMAL':
					color = 'green';
					title = '正常';
					break;
				}
				return $("<label/>")
						.css('cssText', 'min-width:25px !important').addClass(
								"light-ico " + color + "light").attr('title',
								title);
			}
		},
		createTinyTool : function(container) {
			// 判断业务系统中是否有值
			oc.util
					.ajax({
						url : oc.resource
								.getUrl("portal/business/service/getBizListByInstanceId.htm"),
						data : {
							instanceId : this.cfg.instanceId
						},
						timeout : null,
						successMsg : null,
						success : function(d) {
							if (d.data && d.data.length > 0) {
								var bizBtn = $("<a class='resource_alarm'/>");
								container.append(bizBtn);
								bizBtn.linkbutton("RenderLB", {
									text : '业务系统',
									onClick : function() {
										that.bizBtnEvent(that.cfg.instanceId);
									}
								});
							}
						}
					});
			var that = this;
			// var alarmBtn = $("<a class='resource_alarm'/>");
			// container.append(alarmBtn);
			// alarmBtn.linkbutton("RenderLB", {
			// text : '告警列表',
			// onClick : function(){
			// that.alarmBtnEvent(that.cfg.instanceId);
			// }
			// });

			if (this.cfg.hasCustomMetric) {
				if (this.cfg.resourceType == 'Host'
						|| this.cfg.resourceType == 'NetworkDevice') {
					var customMetric = $("<a class='resource_alarm'/>");
					container.append(customMetric);
					customMetric.linkbutton("RenderLB", {
						text : '自定义指标',
						onClick : function() {
							that.customMetricBtnEvent(that.cfg.instanceId);
						}
					});
				}
			}

			// 创建小工具
			var ResourceCategory = 0;
			if (this.cfg.resourceType == 'Host') {
				ResourceCategory = 1;
			} else if (this.cfg.resourceType == 'NetworkDevice'
					|| this.cfg.resourceType == 'SnmpOthers') {
				ResourceCategory = 2;
			} else if (this.cfg.resourceType == 'Database'
					&& this.cfg.categoryId == 'Oracles') {
				ResourceCategory = 3;
			}
			if (ResourceCategory != 0) {
				oc.resource
						.loadScript(
								'resource/module/resource-management/cameraDetailInfo/tinytool/js/tinytool.js',
								function() {
									oc.module.resmanagement.resdeatilinfo.tinytools
											.init(ResourceCategory, that.cfg,
													container);
								});
			}

		},
		alarmBtnEvent : function(instanceId) {
			$('<div/>')
					.dialog(
							{
								title : '告警信息列表',
								fit : false,
								async : true,
								width : '1108px',
								height : '640px',
								href : oc.resource
										.getUrl('resource/module/alarm-management/alarm-resource-list.html'),
								onLoad : function() {
									oc.resource
											.loadScript(
													'resource/module/alarm-management/js/alarm-resource-list.js',
													function() {
														oc.alarm.resource.list
																.open(instanceId);
													});
								}
							});
		},
		bizBtnEvent : function(instanceId) {
			$('<div/>')
					.dialog(
							{
								title : '业务系统列表',
								fit : false,
								async : true,
								width : '1108px',
								height : '350px',
								href : oc.resource
										.getUrl('resource/module/business-service/business_service_list.html'),
								onLoad : function() {
									oc.resource
											.loadScript(
													'resource/module/business-service/js/business_service_list.js',
													function() {
														oc.business.service.list
																.open(instanceId);
													});
								}
							});
		},
		customMetricBtnEvent : function(instanceId) {
			oc.resource
					.loadScript(
							'resource/module/resource-management/customMetric/js/customMetricList.js',
							function() {
								oc.module.resource.custom.metric.list.open({
									id : instanceId
								});
							});
		},
		updateTableWidth : function(grid) {
			var dataGrid = grid.selector;
			var viewObj = dataGrid.parent().find('div[class=datagrid-view2]');
			var resourceObj = viewObj.find('table[class=datagrid-htable]');
			var targetObj = viewObj.find('table[class=datagrid-btable]');
			targetObj.css('width', resourceObj.css('width'));
		}
	}

	// -- 单位转换方法开始 --
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
	// -- 单位转换方法结束 --

	oc.ns('oc.module.resmanagement.resdeatilinfom');
	oc.module.resmanagement.resdeatilinfom.general = {
		open : function(cfg) {
			var gen = new general(cfg);
			gen.open();
		},
		unitTransform : function(value, unit) {
			return unitTransform(value, unit);
		}
	}
});