function TopoMenu(args,holder){
	var ctx = this;
	this.args=$.extend({
		onLoad:function(){}
	},args);
	this.topo = args.topo;
	this.holder = holder;
	if(!this.topo || !this.holder){
		throw "[TopoMenu.js] topo holder 不能为空";
	}
	//请求该模块需要加载的js文件
	oc.resource.loadScripts(["resource/module/topo/contextMenu/IFrameDia.js"],function(){
		//请求用户信息
		$.post(oc.resource.getUrl("topo/help/userinfo.htm"),function(info){
			ctx.user=info.data;
		},"json");
		//请求视图
		$.get(oc.resource.getUrl("resource/module/topo/contextMenu/TopoMenu.html"),function(html){
			ctx.init(html);
			ctx.args.onLoad.call(ctx);
		},"html");
	});
};
TopoMenu.prototype={
	init:function(html){
		var ctx = this;
		this.root=$(html);
		this.root.appendTo(this.holder);
		this.fields={};
		this.root.find("[data-field]").each(function(idx,dom){
			var tmp = $(dom);
			ctx.fields[tmp.attr("data-field")]=tmp;
		});
		//初始化菜单
		this.fields.nodeMenu.menu();
		this.fields.lineMenu.menu();
		this.fields.svgMenu.menu();
		this.fields.shapeMenu.menu();
		this.fields.multiChoseMenu.menu();
		this.fields.subtopoMenu.menu();
		//初始化布局器
		this.layout = new Layout({
			width:this.topo.width,
			height:this.topo.height,
			data:this.topo.els,
			paper:this.topo.paper,
			holder:this.topo.holder
		});
		this.regEvent();
	},
	regEvent:function(){
		var ctx = this;
		//一般连线
		this.fields.notlinkMenu.menu({
			onClick:function(item){
				var line = ctx.currentNode;
				switch(item.text){
					case "删除":
                        line.remove();
					break;
					case "置于顶层":
						line.toFront();
					break;
					case "置于底层":
						line.toBack();
					break;
					case "转为链路":
						oc.resource.loadScript("resource/module/topo/contextMenu/TopoAddLink.js", function(){
							var tal = new TopoAddLink({
								from:line.fromNode,to:line.toNode,id:line.d.rawId||line.d.id
							});
							tal.on("ok",function(linkInfo){
								linkInfo.id=line.d.rawId||line.d.id;
								//添加链路到后台
								oc.util.ajax({
									url:oc.resource.getUrl("topo/newlink.htm"),
									data:{
										info:JSON.stringify(linkInfo)
									},
									timeout:1000000,
									type:"post",
									dataType:"json",
									success:function(link){
										if(link && link.id){
											link.rawId=link.id;
											link.id="link"+link.id;
											link.visible=true;
											line.d=link;
											ctx.topo.refreshState();
											ctx.topo.refreshLinkData();
										}else{
											alert("添加失败");
										}
									}
								});
							});
						});
					break;
				}
			}
		});
		//多链路
		this.fields.multilinkMenu.menu({
			onClick:function(item){
				var line = ctx.currentNode;
				switch(item.text){
				case "查看所有连线":
					oc.resource.loadScript("resource/module/topo/contextMenu/MutilLink.js",function(){
						new MutilLink({fromNodeId:line.fromNode.d.rawId,toNodeId:line.toNode.d.rawId});
					});
					break;
				case "删除所有连线":
					$.messager.confirm("删除所有连线", "确定删除所有连线吗？", function(isDel) {
						var fromNodeId = line.d.from;
						var toNodeId = line.d.to;
						var subTopoId = line.d.subTopoId;
						var linkId;
						$.each(ctx.topo.els,function(key,el){
							if(key.indexOf("link")>=0){
								if((el.d.from==fromNodeId && el.d.to==toNodeId) || (el.d.from==toNodeId && el.d.to==fromNodeId)){
									linkId = el.d.id.substring(4);
								}
							}
						});
						if(linkId){
							if (isDel) {
								oc.util.ajax({
									url : oc.resource.getUrl("topo/link/remove/allLink.htm"),
									data : {
										subTopoId : subTopoId || 0,	//0:二层；其他数字为子拓扑
										id : linkId
									},
									success : function(result) {
										alert(result.msg);
										if (result.code == 200) {
											eve("topo.refresh");	//刷新整个拓扑
										}
									}
								});
							}
						}
					});
					break;
				}
			}
		});
		//实例链路菜单
		this.fields.lineMenu.menu({
			onClick:function(item){
				var line = ctx.currentNode;
				switch (item.text){
					case "删除":
						line.remove();
					break;
					case "显示链路标注":
						line.setTitle(null,true);
					break;
					case "显示链路下行方向":
						line.addArrow();
					break;
					case "编辑链路":
						oc.resource.loadScript("resource/module/topo/contextMenu/EditLink.js",function(){
							line.isRefreshTopo = true;	//保存后刷新整个拓扑图标记
							new EditLinkDia(line);
						});
					break;
					
					case "置于顶层":
						line.toFront();
					break;
					
					case "置于底层":
						line.toBack();
					break;
					
					case "加入监控":
						ctx.monitor(line, true);
					break;
					case "取消监控":
						ctx.monitor(line, false);
					break;
					case "详细信息":
						//检查是否有权限查看详细信息
						if(line.d.instanceId){
                            /*资源管理中的链路详情页面调用拓扑管理中的链路详情页面，屏蔽拓扑链路详情中的操作列。huangping 2017/6/26 start*/
                            /*oc.resource.loadScript("resource/module/topo/contextMenu/TopoLinkInfo.js",function () {
                             new TopoLinkInfo({
                             onLoad:function(){
                             this.load(line.d.instanceId);
                             }
                             });
                             });*/
                            oc.resource.loadScripts([
                                "resource/module/topo/unitTransform.js",
                                "resource/module/topo/contextMenu/topoLinkInfoChart.js",
                                "resource/module/topo/contextMenu/TopoLinkInfo.js"
                            ], function () {
                                new TopoLinkInfo({
                                    type: "map",
                                    onLoad: function () {
                                        var $this = this;

                                        var showMetric = $('#showMetric');
                                        var showMetricChartsDiv = showMetric.find('#showMetricChartsDiv');
                                        var showHideChart = showMetric.find('#infoCharts').html('指标信息图表');
                                        showMetricChartsDiv.height(showHideChart.parent().height());
                                        var showMetricDatagridDivHeight;
                                        showMetricDatagridDivHeight = showMetric.height() - 10;
                                        var showMetricDatagridDiv = showMetric.find('#showMetricDatagridDiv').height(showMetricDatagridDivHeight);
                                        showMetricDatagridDiv.find('#showMetricDatagridParent').height(showMetricDatagridDivHeight - showHideChart.parent().height() - 15);
                                        showMetricDatagridDiv.find('#showMetricDatagridParent').attr('style', 'width:100%;height:99%;');

                                        //初始化曲线图
                                        var chart = new TopoLinkInfoChart(this.fields.chart, 1, line.d.metricId);
                                        showMetricChartsDiv.attr('style', 'display:none;');
                                        //绑定chartDiv收缩
                                        showMetricChartsDiv.find('.w-table-title').on('click', function () {
                                            $this.showHideChartMethod(showMetric, showMetricDatagridDiv, showMetricChartsDiv, showHideChart);
                                            showMetricChartsDiv.attr('style', 'display:none;');
                                        });
                                        this.root.on("click", ".topo_link_cell", function () {
                                            var tmp = $(this);

                                            showMetricChartsDiv.attr('style', 'display:block;');
                                            showMetric.find(".ico-chartred").each(function () {
                                                $(this).addClass("ico-chart");
                                                $(this).removeClass("ico-chartred");
                                            });
                                            tmp.addClass("ico-chartred");
                                            tmp.removeClass("ico-chart");

                                            var metricId = tmp.attr("metricId");
                                            var instanceId = tmp.attr("instanceId");

                                            chart.setIds(metricId, instanceId);

                                            $this.showHideChartMethod(showMetric, showMetricDatagridDiv, showMetricChartsDiv, showHideChart, 'show');
                                        });
                                        $this.load(line.d.instanceId);
                                    }
                                });
                            });
                            /*资源管理中的链路详情页面调用拓扑管理中的链路详情页面，屏蔽拓扑链路详情中的操作列。huangping 2017/6/26 end*/
						}
					break;
				}
			}
		});
		//实例节点菜单
		this.fields.nodeMenu.menu({
			onClick:function(item){
				var node = ctx.currentNode;
				switch (item.text){
					case "Telnet":
						if(node.d && node.d.ip){
							new IFrameDia({
								title:"Telnet "+node.d.ip,
								url:"module/resource-management/resourceDetailInfo/tinytool/telnet.jsp?port=23&host="+node.d.ip
							});
						}
					break;
					case "MIB":
						if(node.d && node.d.ip){
							new IFrameDia({
								title:"MIB "+node.d.ip,
								url:"module/resource-management/resourceDetailInfo/tinytool/MIBApplet.jsp?address="+node.d.ip
							});
						}
					break;
					case "SSH":
						if(node.d && node.d.ip){
							new IFrameDia({
								title:"SSH "+node.d.ip,
								url:"module/resource-management/resourceDetailInfo/tinytool/ssh.jsp?host="+node.d.ip,
								buttons:[{text:"确定"}]
							});
						}
						break;
					case "Ping":
						if(node.d && node.d.ip && ctx.user){
							var url="module/resource-management/resourceDetailInfo/tinytool/ping.jsp?host="+node.d.ip+"&nodeGroupId="+ctx.user.groupId;
							var left=(window.outerWidth-350)/2;
							window.open(url, 
								"", 
								"height=100,width=350,left="+left+",top=50,toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no"
							);
						}
						break;
					case "SNMP Test":
						if(node.d && node.d.ip && node.d.instanceId){
							oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_snmp.js", function(){
								oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolSnmp.open({
									ip:node.d.ip,
									instanceId:node.d.instanceId,
									discoverNode:ctx.user.groupId
								});
							});
						}
						break;
					case "Web管理":
						if(node.d && node.d.ip){
							oc.resource.loadScript("resource/module/topo/contextMenu/WebManageDia.js", function(){
								new WebManageDia(node.d.ip);
							});
						}
						break;
					case "Trace Route":
						if(node.d && node.d.ip && ctx.user){
							new IFrameDia({
								title:"Trace Route",
								w:800,
								url:"module/resource-management/resourceDetailInfo/tinytool/tracert.jsp?host="+node.d.ip+"&nodeGroupId="+ctx.user.groupId
							});
						}
						break;
					case "设置常用链接":
						oc.resource.loadScript("resource/module/topo/contextMenu/commonLink/commonLink.js", function(){
							new CommonLink();
						});
						break;
					case "地址表":
						dia.addressTable();
						break;
					case "修改基本信息":
						oc.resource.loadScript("resource/module/topo/contextMenu/updateBaseInfo/updateBaseInfo.js", function(){
							var ui = new UpdateBaseInfoDia({
								subTopoId:ctx.topo.id,
								name:node.d.showName,
								id:node.d.rawId||node.d.id,
								ip:node.d.ip
							});
							ui.on("ok",function(info){
								info.id=node.d.rawId||node.d.id;
								info.instanceId=node.d.instanceId;
								//更新基本信息
								oc.util.ajax({
									url:oc.resource.getUrl("topo/updateResourceBaseInfo.htm"),
									data:{
										subTopoId:ctx.topo.id,
										info:JSON.stringify(info)
									},
									success:function(result){
										if(result.state==200){
											node.d.attr=info;
											node.d.showName=info.name;
											node.d.ip=info.manageIp;
											node.setTitle();
											alert(result.msg);
											ui.dia.dialog("close");
										}else if(result.state=700 && result.errorMsg){
											alert(result.errorMsg,"warning");
										}
									}
								});
							});
						});
						break;
					case "替换图标":
						oc.resource.loadScript("resource/module/topo/contextMenu/replaceIcon/replaceIcon.js", function(){
							new ReplaceIconDia(node);
						});
						break;
					case "双击操作设置":
						if(node.d && node.d.id){
							oc.resource.loadScript("resource/module/topo/contextMenu/TopoNodeDblClick.js", function(){
								new TopoNodeDblClick({key:node.linktype+node.d.id});
							});
						}
						break;
					case "路由表":
						if(node.d && node.d.instanceId){
							oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_route.js",function(){
								oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolRouteTable.open({
									instanceId:node.d.instanceId
								});
							});
						}
						break;
					case "ARP表":
						if(node.d && node.d.instanceId){
							oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_arp.js", function(){
								oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolArpTable.open({
									instanceId:node.d.instanceId
								});
							});
						}
						break;
					case "VLAN表":
						if(node.d){
							oc.resource.loadScript("resource/module/topo/contextMenu/TopoVlan.js", function(){
								new TopoVlan({
									id:node.d.rawId||node.d.id
								});
							});
						}
						break;
					case "删除":
						$.messager.confirm("警告","确定删除节点（包括所关联的链路）？",function(r){
							if(r){
								ctx.removeNode({
									id:node.d.rawId||node.d.id,
									node:node
								});
							}
						});
						break;
					case "隐藏":
						ctx.hideNodes([node]);
						break;
					case "面板信息":
						oc.resource.loadScript("resource/module/topo/contextMenu/BackBoardInfoDia.js", function(){
							new BackBoardInfoDia({node:node,downDeviceFlag:true});//downDeviceFlag:显示下联设备标志
						});
						break;
					case "加入监控":
						ctx.monitor(node, true);
						break;
					case "取消监控":
						ctx.monitor(node, false);
						break;
					case "监控设置":
						if(node.d && node.d.instanceId){
							$.post(oc.resource.getUrl("topo/getProfileIdByInstanceId.htm"),{
								instanceId:node.d.instanceId
							},function(info){
								var type=info.type,profileId=info.profileId;
								if(type == 'PERSONALIZE'){
									oc.resource.loadScript('resource/module/resource-management/js/personalize_strategy_detail.js',function(){
										oc.personalizeprofile.strategy.detail.show(profileId);
									});
								}else if(type == 'DEFAULT'){
									oc.resource.loadScript('resource/module/resource-management/js/default_strategy_detail.js',function(){
										oc.defaultprofile.strategy.detail.show(profileId);
									});
								}else if(type == 'SPECIAL'){
									oc.resource.loadScript('resource/module/resource-management/js/custom_strategy_detail.js',function(){
										oc.customprofile.strategy.detail.show(profileId);
									});		  
								}
							},"json");
						}
						break;
					case "详细信息":
						if(node.d && node.d.instanceId){
							//检查权限
							oc.util.ajax({
								url:oc.resource.getUrl("topo/resource/checkauth.htm"),
								data:{
									instanceId:node.d.instanceId
								},
								success:function(result){
									if(result.state==200){
										oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js", function(){
											oc.module.resmanagement.resdeatilinfonew.open({
												instanceId:node.d.instanceId
											});
										});
									}else{
										alert(result.msg,"warning");
									}
								}
							});
						}else{
							alert("非实例化节点","warning");
						}
						break;
					case "资产信息":
						if(ctx.itsmData && ctx.itsmData.CODE && ctx.itsmData.URL){
							var url = ctx.itsmData.URL.substring(0, ctx.itsmData.URL.indexOf("itsm/")+5);
							url += "cmdb/viewByOtherSourceCIAction.action"+"?systemCode="+ctx.itsmData.CODE+"&moId="+node.d.instanceId;
							window.open(url);
						}
						break;
					case "关联资源实例":
						oc.resource.loadScript("resource/module/topo/contextMenu/RelateResourceInstance.js", function(){
							var ri = new RelateResourceInstance({
								id:node.d.rawId||node.d.id,
								instanceId:node.d.instanceId,
								subTopoId:ctx.topo.id
							});
							ri.on("ok",function(result){
								node.d.instanceId=result.instanceId;
								node.d.ip=result.ip;
								node.setTitle();
								ctx.topo.refreshState();
							});
						});
					break;
					
					case "置于顶层":
						node.toFront();
						break;
					case "置于底层":
						node.toBack();
						break;
					
					case "属性":
						oc.resource.loadScript("resource/module/topo/contextMenu/TopoNodeAttr.js", function(){
							new TopoNodeAttr({
								node:node
							});
						});
					break;
					default:	//处理刷新节点
						ctx._freshNOdes(item.text,null);
					break;
				}
			}
		});
		//空白处右键
		this.fields.svgMenu.menu({
			onClick:function(item,e){
				switch (item.text){
					case "标示":
						ctx.topo.addAllLinesArrow();
					break;
					case "不标示":
						ctx.topo.removeAllLinesArrow();
					break;
					case "设为拓扑首页":
						ctx.saveCfg({
							key:"oc_topo_graph_homepage",
							value:JSON.stringify({
								id:ctx.topo.id||0
							})
						});
					break;
					case "星型":
						eve("topo.layout",ctx,"star");
					break;
					case "树型":
						eve("topo.layout",ctx,"tree");
					break;
					case "网络":
						eve("topo.layout",ctx,"grid");
					break;
					case "显示所有资源":
						ctx.getHideNodes(function(result){
							var ids=[];
							$.each(result.items,function(idx,item){
								ids.push(item.id);
							});
							ctx.showNodes(ids);
						});
					break;
					case "资源隐藏列表":
						ctx.getHideNodes(function(result){
							oc.resource.loadScript("resource/module/topo/contextMenu/TopoHideListDia.js", function(){
								var th = new TopoHideListDia({data:result.items});
								th.on("show",function(items){
									var ids = [];
									for(var i=0;i<items.length;i++){
										var item = items[i];
										ids.push(item.id);
									}
									ctx.showNodes(ids);
								});
							});
						});
					break;
					//设备显示
					case "设备IP":
						eve("topo.change.devicetitle",this,"ip");
					break;
					case "设备OID":
						eve("topo.change.devicetitle",this,"oid");
					break;
					case "设备名称":
						eve("topo.change.devicetitle",this,"showName");
					break;
					case "设备类型":
						eve("topo.change.devicetitle",this,"typeName");
					break;
					case "设备厂商":
						eve("topo.change.devicetitle",this,"vendorName");
					break;
					//链路显示标注
					case "无标注":
						eve("topo.change.linktitle",this,"none",true);
					break;
					case "链路备注":
						eve("topo.change.linktitle",this,"note",true);
					break;
					case "链路流量":
						eve("topo.change.linktitle",this,"flow",true);
						break;
					/*case "上行流量":
						eve("topo.change.linktitle",this,"upSpeed",true);
					break;
					case "下行流量":
						eve("topo.change.linktitle",this,"downSpeed",true);
					break;*/
					case "粘贴":
						oc.topo.clipboard.paste(ctx.topo.id);
					break;
					case "返回":
						eve("topo.loadsubtopo",ctx.topo,ctx.topo.parentId||0);
					break;
					case "创建子拓扑":
						oc.resource.loadScript("resource/module/topo/contextMenu/CreateSubTopo.js", function(){
							var cs = new CreateSubTopo({
								parentId:ctx.topo.id
							});
							cs.on("save",function(info){
								eve("oc.topo.addSubTopo.finished",this,0);
								var pos = ctx.topo.util.realPos(ctx.currentPos);
								ctx.topo.addNode({
									name:info.name,
									type:"subtopo",
									x:pos.x,y:pos.y,
									w:40,h:40,
									attr:{
										subtopoId:info.id
									},
									src:"resource/themes/blue/images/topo/topoIcon/admin/kbqy.png"
								});
								ctx.topo.refresh();
							});
						});
					break;
				}
			}
		});
		//一般形状菜单
		this.fields.shapeMenu.menu({
			onClick:function(item){
				var node = ctx.currentNode;
				switch(item.text){
					case "删除":
						$.messager.confirm("警告","确定删除？",function(r){
							if(r){
								if(node.d.rawId<0){//还没保存的节点直接删除
									node.remove();
								}else{
									var url = "";
									if(node.linktype=="other"){
										url="topo/removeOther.htm";
									}else if(node.linktype=="group"){
										url="topo/removeGroup.htm";
									}else{
										url="topo/link/removeLink.htm";
									}
									oc.util.ajax({
										url:oc.resource.getUrl(url),
										data:{
											subTopoId:ctx.topo.id,
											ids:node.d.rawId
										},
										success:function(result){
											var alertType="info";
											if(result.code==200){
												node.remove();
											}else{
												alertType="danger";
											}
											alert(result.msg||"",alertType);
										}
									});
								}
							}
						});
					break;
					case "属性":
						oc.resource.loadScript("resource/module/topo/contextMenu/NodeAttr.js", function(){
							var na = new NodeAttr({
								node:node,
								topoId:ctx.topo.id
							});
							na.on("ok",function(result){
								if(node.setText){
									node.setText(result.value.text);
								}
							});
						});
					break;
					
					case "置于顶层":
						//更新图层属性
						var now = new Date();
						var date = new Date(now.getTime());
						var year = date.getFullYear();
						var month = date.getMonth() + 1;
						var day = date.getDate();
						var hour = date.getHours();
						var minute = date.getMinutes();
						var second = date.getSeconds();
						var currentdate = year+'-'+(month<10?('0'+month):month)+'-'+(day<10?('0'+day):day)+' '+(hour<10?('0'+hour):hour)+':'+(minute<10?('0'+minute):minute)+':'+(second<10?('0'+second):second);	
						oc.util.ajax({
							url:oc.resource.getUrl("topo/updateZindex.htm"),
							data:{
								id:node.id,
								zIndex:1,
								currentdate:currentdate
							},
							startProgress:function(){},
							success:function(result){
								if(result.state==200){
									
								}else{
									alert(result.msg,"warning");
								}
							}
						});
						node.toFront();
					break;
					
					case "置于底层":
						//更新图层属性
						var now = new Date();
						var date = new Date(now.getTime());
						var year = date.getFullYear();
						var month = date.getMonth() + 1;
						var day = date.getDate();
						var hour = date.getHours();
						var minute = date.getMinutes();
						var second = date.getSeconds();
						var currentdate = year+'-'+(month<10?('0'+month):month)+'-'+(day<10?('0'+day):day)+' '+(hour<10?('0'+hour):hour)+':'+(minute<10?('0'+minute):minute)+':'+(second<10?('0'+second):second);	
						oc.util.ajax({
							url:oc.resource.getUrl("topo/updateZindex.htm"),
							data:{
								id:node.id,
								zIndex:-1,
								currentdate:currentdate
							},
							startProgress:function(){},
							success:function(result){
								if(result.state==200){
									
								}else{
									alert(result.msg,"warning");
								}
							}
						});
						node.toBack();
					break;
					
					case "锁定":
						node.setEditable(false);
					break;
					case "取消锁定":
						node.setEditable(true);
					break;
				}
			}
		});
		//多选菜单
		this.fields.multiChoseMenu.menu({
			onClick:function(item){
				if(!ctx.topo) return;
				switch(item.text){
					case "创建子拓扑":
						var data = [];
						$.each(ctx.topo.chosed,function(idx,c){
							var d={};
							$.extend(d,c.d);
							d.id=c.d.rawId;
							data.push(d);
						});
						oc.resource.loadScript("resource/module/topo/contextMenu/CreateSubTopo.js", function(){
							var cs = new CreateSubTopo({
								parentId:ctx.topo.id,
								load:function(){
									this.setValue({
										value:data
									});
								}
							});
							cs.on("save",function(info){
								eve("oc.topo.addSubTopo.finished",this,0);
								var pos = ctx.topo.util.realPos(ctx.currentPos);
								ctx.topo.addNode({
									name:info.name,
									type:"subtopo",
									x:pos.x,y:pos.y,
									w:40,h:40,
									attr:{
										subtopoId:info.id
									},
									src:"resource/themes/blue/images/topo/topoIcon/admin/kbqy.png"
								});
								ctx.topo.refresh();
							});
						});
					break;
					case "创建区域":
						//组织数据
						var data=[];
						for(var i=0;i<ctx.topo.chosed.length;++i){
							var tmp = ctx.topo.chosed[i];
							if(tmp.d){
								data.push(tmp.d);
							}
						}
						//打开设备管理对话框
						var dm = new DeviceManagerDia({
							leftData:data
						});
						//ok之后创建区域
						dm.on("ok",function(info){
							eve("topo.newgroup",this,info);
						});
					break;
					case "加入区域":
					break;
					case "删除":
						$.messager.confirm("警告","确定删除选择的项（"+ctx.topo.chosed.length+"）项",function(r){
							if(r){
								var ids=[];
								//删除图元
								for(var i=0;i<ctx.topo.chosed.length;++i){
									var item = ctx.topo.chosed[i];
									if(item.d.rawId>0){
										ids.push(item.d.rawId);
									}
								}
								//删除后台
								oc.util.ajax({
									url:oc.resource.getUrl("topo/removeNode.htm"),
									data:{
										subTopoId:ctx.topo.id,
										ids:ids.join(",")
									},
									success:function(result){
										if(result.data.code==200){
											for(var i=0;i<ctx.topo.chosed.length;++i){
												ctx.topo.chosed[i].remove();
											}
											alert(result.data.msg,"info");
										}else{
											alert(result.data.msg,"danger");
										}
									}
								});
							}
						});
					break;
					case "隐藏":
						ctx.hideNodes(ctx.topo.chosed);
						break;
					case "复制":
						oc.topo.clipboard.copy($.map(ctx.topo.chosed,function(c){
							return c.d||[];
						}));
						break;
					case "剪切":
						oc.topo.clipboard.cut($.map(ctx.topo.chosed,function(c){
							return c.d||[];
						}));
						break;
					default:	//处理刷新节点
						ctx._freshNOdes(item.text,ctx.topo.chosed);
					break;
				};
			}
		});
		//机柜右键
		this.fields.roomMenu.menu({
			onClick:function(item){
				var node = ctx.currentNode;
				switch(item.text){
				case "删除":
					$.messager.confirm("警告","确定删除机柜"+node.attr.text+"?",function(r){
						if(r){
							oc.util.ajax({
								url:oc.resource.getUrl("topo/other/removeCabinet.htm"),
								data:{
									id:node.d.rawId,
									subTopoId:ctx.topo.id
								},
								success:function(result){
									if(result.status==200){
										alert(result.msg);
										node.remove();
									}else{
										alert(result.msg,"warning");
									}
								}
							});
						}
					});
					break;
				case "编辑":
					oc.resource.loadScript("resource/module/topo/contextMenu/AddCabinet.js", function(){
						new AddCabinet({
							title:"编辑机柜"+node.attr.text,
							subTopoId:ctx.topo.id,
							id:node.d.rawId||node.d.id,
							onLoad:function(){
								var ac=this;
								$.post(oc.resource.getUrl("topo/other/getById.htm"),{
									id:node.d.rawId||node.d.id
								},function(result){
									ac.setValue(JSON.parse(result.attr));
								},"json");
							},
							onSave:function(v){
								node.text.attr("text",v.text);
							}
						});
					});
					break;
				case "置于顶层":
					//更新图层属性
					var now = new Date();
					var date = new Date(now.getTime());
					var year = date.getFullYear();
					var month = date.getMonth() + 1;
					var day = date.getDate();
					var hour = date.getHours();
					var minute = date.getMinutes();
					var second = date.getSeconds();
					var currentdate = year+'-'+(month<10?('0'+month):month)+'-'+(day<10?('0'+day):day)+' '+(hour<10?('0'+hour):hour)+':'+(minute<10?('0'+minute):minute)+':'+(second<10?('0'+second):second);	
					oc.util.ajax({
						url:oc.resource.getUrl("topo/updateZindex.htm"),
						data:{
							id:node.id,
							zIndex:1,
							currentdate:currentdate
						},
						startProgress:function(){},
						success:function(result){
							if(result.state==200){
								
							}else{
								alert(result.msg,"warning");
							}
						}
					});
					node.toFront();
					break;
					
				case "置于底层":
					//更新图层属性
					var now = new Date();
					var date = new Date(now.getTime());
					var year = date.getFullYear();
					var month = date.getMonth() + 1;
					var day = date.getDate();
					var hour = date.getHours();
					var minute = date.getMinutes();
					var second = date.getSeconds();
					var currentdate = year+'-'+(month<10?('0'+month):month)+'-'+(day<10?('0'+day):day)+' '+(hour<10?('0'+hour):hour)+':'+(minute<10?('0'+minute):minute)+':'+(second<10?('0'+second):second);	
					oc.util.ajax({
						url:oc.resource.getUrl("topo/updateZindex.htm"),
						data:{
							id:node.id,
							zIndex:-1,
							currentdate:currentdate
						},
						startProgress:function(){},
						success:function(result){
							if(result.state==200){
								
							}else{
								alert(result.msg,"warning");
							}
						}
					});
					node.toBack();
					break;
				case "设备列表":
					oc.resource.loadScript("resource/module/topo/contextMenu/CabinetDeviceList.js", function(){
						new CabinetDeviceList({
							id:node.d.rawId||node.d.id
						});
					});
					break;
				}
			}
		});
		this.fields.subtopoMenu.menu({
			onClick:function(item){
				var node = ctx.currentNode;
				switch(item.text){
				case "编辑":
					oc.resource.loadScripts(["resource/module/topo/contextMenu/CreateSubTopo.js"],function(){
						var cs = new CreateSubTopo({
							subTopoId:node.d.attr.subtopoId,
							title:"编辑子拓扑"
						});
						cs.on("save",function(info){
							eve("oc.topo.addSubTopo.finished",ctx,0);
							eve("topo.loadsubtopo",this,info.id);
						});
					});
					break;
				case "删除拓扑":
					$.messager.confirm("警告 正在删除  "+(node.d.name||""),"确定要删除该拓扑吗(<span style='color:red'>其子拓扑不会被删除</span>)？",function(r){
						if(r){
							oc.util.ajax({
								url:oc.resource.getUrl("topo/deleteSubtopo.htm"),
								data:{
									id:node.d.attr.subtopoId,
									recursive:false
								},
								success:function(){
									eve("oc.topo.addSubTopo.finished",ctx,0);
									ctx.topo.refresh();
								},
								dataType:"text"
							});
						}
					});
					break;
				case "删除拓扑及其子拓扑":
					$.messager.confirm("警告 正在删除  "+(node.d.name||""),"确定要删除该拓扑吗(<span style='color:red'>其所有子拓扑也会被删除</span>)？",function(r){
						if(r){
							oc.util.ajax({
								url:oc.resource.getUrl("topo/deleteSubtopo.htm"),
								data:{
									id:node.d.attr.subtopoId,
									recursive:true
								},
								success:function(){
									ctx.removeNode({
										id:node.d.rawId||node.d.id,
										node:node,
										isPhysicalDelete:true,
										callBack:function(){
											eve("oc.topo.addSubTopo.finished",ctx,0);
											ctx.topo.refresh();
										}
									});
								},
								dataType:"text"
							});
						}
					});
					break;
				}
			}
		});
		//监听右键菜单触发
		eve.on("element.contextmenu",function(e,node){
			try{
				var pos = {x:e.pageX,y:e.pageY};
				if(!node || (node&&node.ctx&&node.ctx.id!=ctx.topo.id)){
					return;
				}else{
					if(!node.isSvg){
						if(ctx.topo.chosed.length>0){//多选状态
							ctx.show("multiChoseMenu",pos,node);
						}else if(node instanceof Topo){
							ctx.show("svgMenu",pos);
						}else if(node.linktype=="node"){
							if(node.d.type=="subtopo"){
								ctx.show("subtopoMenu",pos,node);
							}else{
								ctx.show("nodeMenu",pos,node);
							}
						}else if(node.linktype=="other"){
							if(node.d.dataType=="cabinet"){
								//停止tip的定时器
								clearTimeout(ctx.topo["cabinet_alarm_tip"]);
								ctx.show("roomMenu",pos,node);
							}else{
								ctx.show("shapeMenu",pos,node);
							}
						}else if(node instanceof Connect || node instanceof BendConnect){
							if(node.isMulti){	//多链路
								ctx.show("multilinkMenu",pos,node);
							}else if(node.d.type=="link"){
								ctx.show("lineMenu",pos,node);
							}else{
								ctx.show("notlinkMenu",pos,node);
							}
						}else{
							ctx.show("shapeMenu",pos,node);
						};
					}else{
						$.post(oc.resource.getUrl("topo/subtopo/getTopoType.htm"),{
							topoId:ctx.topo.id
						},function(info){
							if(info.name && info.name=="机房拓扑"){
								
							}else{
								ctx.show("svgMenu",pos);
							}
						},"json");
					}
				}
				e.stopPropagation(); 
				e.preventDefault();
			}catch(error){
				e.stopPropagation(); 
				e.preventDefault();
			}
		});
		//监听布局方式
		eve.on("topo.layout",function(type,args){
			switch(type){
			case "tree":
				ctx.layout.tree(args||{refEl:ctx.topo.getEl(ctx.topo.tmpRefNodeId),paper:ctx.topo.paper,h:150,r:100});
				break;
			case "star":
				ctx.layout.star($.extend({refEl:ctx.topo.getEl(ctx.topo.tmpRefNodeId),per:Math.PI/6},args));
				break;
			case "grid":
				ctx.layout.grid(args||{refEl:ctx.topo.getEl(ctx.topo.tmpRefNodeId),paper:ctx.topo.paper,r:100});
				break;
			case "viewToCenter"://平移到视图中间位置
				//ctx.layout.viewToCenter(args);
				break;
			}
			ctx.topo.dirty=true;
		});
	},
	//刷新节点大小
	_freshNOdes:function(text,nodes){
		var ctx = this;
		ctx.getInitChosedNodes(nodes);	//获取操作的节点初始状态
		switch(text){
			case "200%":
				ctx.graphResize(2,true);
			break;
			case "150%":
				ctx.graphResize(1.5,true);
				break;
			case "100%":
				ctx.graphResize(1,true);
				break;
			case "自定义":
				oc.resource.loadScript("resource/module/topo/contextMenu/TopoGraphSlider.js", function(){
					new TopoGraphSlider(_resize,_recovery);
				});
				break;
		}
		//修改尺寸，功外部调用
		function _resize(multiple,isSave){
			ctx.graphResize(multiple,isSave);
		}
		//恢复改变前的状态，功外部调用
		function _recovery(){
			ctx.recoverySize();
		}
	},
	//加入监控
	monitor:function(node,isAdd){
		var ctx=this;
		if(node.d && !node.d.type=="link" && !node.d.instanceId){
			alert("未实例化设备","warning");
			return;
		}
		if(isAdd){
			if(node.linktype=="node" && node.d.instanceId){//设备加入监控
				oc.util.ajax({
					url:oc.resource.getUrl("topo/addMonitor/"+node.d.instanceId+".htm"),
					success:function(msg){
						if(msg.state==200){
							node.setLifeState("monitored");
							alert("加入监控成功");
							ctx.topo.refreshState();
							eve("topo.toolbar.refresh");
						}else{
							alert(msg.msg,"warning");
						}
					}
				});
			}else if(!node.linktype){//链路加入监控
                /*BUG #54357 拓扑管理：将链路取消监控后，再重新加入监控，编辑链路页面原有的“指标设置”内容恢复为初始值 huangping 2018/5/8 start*/
                //没搞懂为什么取消监控都是走同一接口 而加入监控链路和图元却分开走
                /*oc.util.ajax({
					url:oc.resource.getUrl("topo/link/addMonitor.htm"),
					data:{
						id:node.d.rawId||node.d.id,
						subTopoId:this.topo.id
					},
					success:function(result){
						if(result.state==200){
							node.setLifeState("monitored");
							ctx.topo.refresh();
							ctx.topo.refreshLinkData();
							alert("加入监控成功");
						}else{
							alert(result.msg,"warning");
						}
					}
				});*/
                oc.util.ajax({
                    url: oc.resource.getUrl("topo/addMonitor/" + node.d.instanceId + ".htm"),
                    success: function (msg) {
                        if (msg.state == 200) {
                            node.setLifeState("monitored");
                            alert("加入监控成功");
                            ctx.topo.refreshState();
                            ctx.topo.refreshLinkData();
                        } else {
                            alert("加入监控失败", "warning");
                        }
                    }
                });
                /*BUG #54357 拓扑管理：将链路取消监控后，再重新加入监控，编辑链路页面原有的“指标设置”内容恢复为初始值 huangping 2018/5/8 end*/
			}
		}else{
			if(node.d.instanceId){
				oc.util.ajax({
					url:oc.resource.getUrl("topo/cancelMonitor/"+node.d.instanceId+".htm"),
					success:function(msg){
						if(msg.state==200){
							alert(msg.msg);
							node.setLifeState("not_monitored");
							eve("topo.toolbar.refresh");
						}else{
							alert(msg.msg,"warning");
						}
					}
				});
			}
		}
	},
	//显示菜单
	show:function(menu,pos,node){
		/*$("[data-field="+menu+"]").each(function(idx,dom){//TODO 这不是最终解决方案
			if(idx>0){
				$(dom).remove();
			}
		});*/
		this.currentNode = node;
		this.currentPos = pos;
		var mm = this.fields[menu];
		if(mm){
			mm.menu("show",{
				left:pos.x,
				top:pos.y
			});
			this.refreshMenu(menu,node);
		}
	},
	showNodes:function(ids){
		if(ids){
			var ctx = this;
			oc.util.ajax({
				url:oc.resource.getUrl("topo/showHideNodes.htm"),
				data:{
					ids:ids.join(","),
					subTopoId:this.topo.id
				},
				success:function(result){
					if(result.state==200){
						ctx.topo.addElement(result.info);
					}else{
						alert(result.msg,"warning");
					}
				}
			});
		}
	},
	//隐藏资源
	hideNodes:function(nodes,callBack){
		var ctx = this;
		var ids = [];
		for(var i=0;i<nodes.length;i++){
			var node = nodes[i];
			ids.push(node.d.rawId||node.d.id);
		}
		oc.util.ajax({
			url:oc.resource.getUrl("topo/hideNodes.htm"),
			data:{
				subTopoId:this.topo.id,
				ids:ids.join(",")
			},
			success:function(result){
				if(result.state==200){
					for(var i=0;i<nodes.length;i++){
						var node = nodes[i];
						node.hide();
					}
				}else{
					alert(result.msg,"warning");
				}
				if(callBack){
					callBack.call(ctx);
				}
			}
		});
	},
	//添加到子拓扑
	addToSubtopo:function(item){
		var items = [];
		$.each(this.topo.chosed,function(idx,c){
			items.push(c.d);
		});
		oc.topo.clipboard.copy(items);
		oc.topo.clipboard.paste(item.id);
	},
	//构建菜单
	buildMenu:function(menu,items,menuText,callBack){
		var mm = this.fields[menu];
		//构建菜单的内部函数
		function _buildMenu(item,parentText){
			var parent = mm.menu("findItem",parentText);
			mm.menu("appendItem",{
				parent:parent.target,
				text:item.text,
				id:item.id,
				onclick:function(args){
					//调用回到
					if(callBack){
						callBack(JSON.parse($(this).attr("data-item")));
					}
					mm.menu("hide");
				}
			});
			var tmpMenu = mm.menu("findItem",item.text);
			//添加拓扑自定义菜单样式
			if(tmpMenu){
				var $tmpMenu = $(tmpMenu.target);
				$tmpMenu.attr("data-item",JSON.stringify(item));
				$tmpMenu.parents(".menu").addClass("topo_menu_frame");
				if(item.children){
					for(var i=0;i<item.children.length;++i){
						var it = item.children[i];
						_buildMenu(it,item.text);
					}
				}
			}
		}
		//生成菜单
		if(items && items instanceof Array){
			var key = "old"+menu+menuText;
			//清空上一次的子菜单项
			if(this[key]){
				for(var i=0;i<this[key].length;++i){
					var okey = this[key][i];
					if(okey){
						var menuItem = mm.menu("findItem",this[key][i]);
						try{
							$(menuItem.target).remove();
						}catch(err){
							console.log(err);
						}
					}
				}
			}
			//准备记录这一次的子菜单项
			this[key]=[];
			for(var i=0;i<items.length;++i){
				var item = items[i];
				//保存上一步的次菜单项
				this[key].push(item.text);
				_buildMenu(item,menuText);
			}
		}
	},
	//刷新菜单项
	refreshMenu:function(menu,node){
		var ctx = this;
		var mm = this.fields[menu];
		//如果是未监控状态
		if(node && node.d){
			var lifeState = node.d.lifeState;
			var cancelM =mm.menu("findItem","取消监控");
			var addM =mm.menu("findItem","加入监控");
			if(cancelM && addM){
				if(lifeState=="monitored"){
					$(cancelM.target).removeClass("hide");
					$(addM.target).addClass("hide");
				}else{
					$(addM.target).removeClass("hide");
					$(cancelM.target).addClass("hide");
				}
			}
		}
		switch(menu){
			case "multiChoseMenu"://多选菜单
				//子拓扑
				var addSubTopoM = mm.menu("findItem","加入子拓扑");
				$(addSubTopoM.target).addClass("hide");
				$.get(oc.resource.getUrl("topo/0/subTopos.htm"),function(topos){
					//如果子拓扑项不为为0则隐藏此项
					if(topos&&topos.length>0){
						$(addSubTopoM.target).removeClass("hide");
					}
					ctx.buildMenu(menu, topos,"加入子拓扑",function(item){
						ctx.addToSubtopo(item);
					});
				},"json");
				//搜索当前图中的区域
				var areas = [],areaRelation={};
				$.each(this.topo.els,function(key,el){
					if(key.indexOf("group")>=0){
						var tmpValue = el.getValue();
						areaRelation[tmpValue.id]=el;
						areas.push({
							text:tmpValue.name,
							id:tmpValue.id
						});
					}
				});
				//区域列表
				this.buildMenu(menu,areas,"加入区域",function(item){
					var area = areaRelation[item.id];
					if(area){
						var chosed = ctx.topo.chosed;
						for(var i=0;i<chosed.length;i++){
							var item = chosed[i];
							area.add(item,0,0);
						}
						if(chosed.length>0){
							area.refresh(area.getPos(),true);
						}
					}
				});
				break;
			case "nodeMenu"://节点菜单
				//如果是主机
				var ifListM = mm.menu("findItem","接口一览");
				var backboardM = mm.menu("findItem","面板信息");
				var infoListM = mm.menu("findItem","信息表");
				var detailInfoM =mm.menu("findItem","详细信息");
				var deleteM=mm.menu("findItem","删除");
				var propertyM=mm.menu("findItem","资产信息");
				var associatedResInsM=mm.menu("findItem","关联资源实例");
				var show = false;
				if(propertyM) show = ctx._checkItsm(propertyM);
				
				if(deleteM){
					if(node.hasNoDelete){
						$(deleteM.target).addClass("hide");
					}else{
						$(deleteM.target).removeClass("hide");
					}
				}
				if(detailInfoM){//未监控的状态需要隐藏"详细信息"
					if(!node.d.instanceId){
						//隐藏菜单项
						$(detailInfoM.target).addClass("hide");
						$(propertyM.target).addClass("hide");
						$(associatedResInsM.target).removeClass("hide");
					}else{//ITM7.2.3版本新增发现出的设备隐藏关联资源实例菜单项
						$(detailInfoM.target).removeClass("hide");
						if(show) $(propertyM.target).removeClass("hide");						
						$(associatedResInsM.target).addClass("hide");
					}
				}
				if(node.d.type=="HOST" || node.d.type=="SERVER"){
					if(ifListM){
						$(ifListM.target).addClass("hide");
					}
					if(backboardM){
						$(backboardM.target).addClass("hide");
					}
					if(infoListM){
						$(infoListM.target).addClass("hide");
					}
				}else{//非主机
					if(ifListM){
						$(ifListM.target).removeClass("hide");
					}
					if(backboardM){
						$(backboardM.target).removeClass("hide");
					}
					if(infoListM){
						$(infoListM.target).removeClass("hide");
					}
				}
				//加载常用链接
				$.post(oc.resource.getUrl("topo/setting/get/commonLink.htm"),function(links){
					var tmpLinks=[];
					for(var i=0;i<links.length;i++){
						var link = links[i];
						link.text=link.name;
						tmpLinks.push(link);
					}
					ctx.buildMenu(menu,tmpLinks,"常用链接",function(item){
						new IFrameDia({
							title:item.name||item.text,
							w:800,
							url:item.url
						});
					});
				},"json");
				/*;*/
				break;
			case "lineMenu":
				//没有instanceId时候，需要隐藏的菜单
				var detailInfoM =mm.menu("findItem","详细信息"),
					editLinkM =mm.menu("findItem","编辑链路");
				if(!node.d || !node.d.instanceId){
					if(detailInfoM){
						$(detailInfoM.target).addClass("hide");
					}
					if(editLinkM){
						$(editLinkM.target).addClass("hide");
					}
				}else{
					if(editLinkM){
						$(detailInfoM.target).removeClass("hide");
					}
					if(editLinkM){
						$(editLinkM.target).removeClass("hide");
					}
				}
				break;
			case "shapeMenu":
				//锁定，和取消锁定
				var lock = $(mm.menu("findItem","锁定").target),unlock = $(mm.menu("findItem","取消锁定").target)
					remove = $(mm.menu("findItem","删除").target),attrMenu=$(mm.menu("findItem","属性").target)
					topMenu = $(mm.menu("findItem","置于顶层").target), bottomMenu = $(mm.menu("findItem","置于底层").target);
				lock.addClass("hide");
				unlock.addClass("hide");
				if(!node.editable){
					remove.addClass("hide");
				}else{
					remove.removeClass("hide");
				}
				if(node instanceof Image){
					attrMenu.removeClass("hide");
					//机房视图新增（或者隐藏）"置于顶层"和"置于底层"的菜单
					//topMenu.addClass("hide");
					//bottomMenu.addClass("hide");
				}else{
					attrMenu.addClass("hide");
					//topMenu.removeClass("hide");
					//bottomMenu.removeClass("hide");

				}
				var value = node.getValue();
				if(value && value.dataType && value.dataType=="map"){
					if(!node.editable){
						lock.addClass("hide");
						unlock.removeClass("hide");
					}else{
						lock.removeClass("hide");
						topMenu.addClass("hide");
						bottomMenu.addClass("hide");
						unlock.addClass("hide");
					}
				}else{
					topMenu.removeClass("hide");
					bottomMenu.removeClass("hide");
				}
				break;
			case "svgMenu":
				var adhereM = mm.menu("findItem","粘贴");
				var backM = mm.menu("findItem","返回");
				if(ctx.topo.id==null || ctx.topo.id==0){
					$(backM.target).addClass("hide");
				}else{
					$(backM.target).removeClass("hide");
				}
				if(oc.topo.clipboard.hasData()){
					$(adhereM.target).removeClass("hide");
				}else{
					$(adhereM.target).addClass("hide");
				}
				break;
		}
	},
	//验证是否配置ITSM信息（资产关联）
	_checkItsm:function(menu){
		var show = false;
		$(menu.target).addClass("hide");
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl('system/itsm/getWebService.htm'),
			success:function(data){
				if(200 == data.code){
					var dataset = $.parseJSON(data.data);
					ctx.itsmData = dataset;
					if(dataset && dataset.AVAILABLE){
						//再查询是否设置CMDB
						oc.util.ajax({
							url:oc.resource.getUrl('system/cmdb/getWebService.htm'),
							success:function(data){
								if(200 == data.code){
									var dataSet = $.parseJSON(data.data);
									if(dataSet && dataSet.open){
										$(menu.target).removeClass("hide");
										show = true;
									}
								}
							}
						});
					}else{
						$(menu.target).addClass("hide");
					}
				}
			}
		});
		return show;
	},
	//保存配置
	saveCfg:function(value){
		oc.util.ajax({
			url:oc.resource.getUrl("topo/setting/save.htm"),
			data:value,
			success:function(){
				alert("首页设置成功","info");
			}
		});
	},
	//获取隐藏的节点
	getHideNodes:function(callBack){
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/getHideNodes.htm"),
			data:{
				subTopoId:ctx.topo.id
			},
			success:function(result){
				if(result.state==200){
					if(callBack){
						callBack.call(ctx,result);
					}
				}else{
					alert(result.msg,"warning");
				}
			}
		});
	},
	//获取改变图元大小前的节点
	getInitChosedNodes:function(nodes){
		var chosed = new Array();
		if(nodes){
			chosed = nodes;
		}else{
			chosed.push(this.currentNode);
		}
		var oldNodes = new Array();
		for(var i=0;i<chosed.length;i++){
			var chose = chosed[i];
			var node = {node:chose,w:chose.d.iconWidth,h:chose.d.iconHeight};
			oldNodes.push(node);
		}
		this.oldChosedNodes = oldNodes;		//记录操作的节点
	},
	//修改图元大小
	graphResize:function(multiple,isSave){
		var chosed = this.oldChosedNodes;
		this._refreshNodes(chosed,multiple,isSave)	//刷新图元大小
	},
	//恢复节点原来的尺寸
	recoverySize:function(){
		if(this.oldChosedNodes){
			this._refreshNodes(this.oldChosedNodes,null,false);
		}
	},
	//刷新节点
	_refreshNodes:function(nodes,multiple,isSave){
		var ctx = this;
		var baseWidth = 30,baseHeight = 30;
		for(var i=0;i<nodes.length;i++){
			var node = nodes[i].node;
			//当前尺寸
			var pos = node.getPos();
			//修改当前尺寸和位置
			var w=baseWidth*multiple,h=baseHeight*multiple,x=pos.x,y=pos.y;
			if(!multiple && 0!=multiple){
				w=nodes[i].w,h=nodes[i].h;
			}
			node.refresh({x:x,y:y,w:w,h:h});
			//保存位置尺寸
			if(isSave) eve("topo.save",this,function(){});
		}
	},
	destroy:function(){
		this.root.remove();
	},
	removeNode:function(args){
		var ctx = this;
		oc.util.ajax({
			url:oc.resource.getUrl("topo/removeNode.htm"),
			data:{
				subTopoId:this.topo.id,
				ids:args.id,
				isPhysicalDelete:!!args.isPhysicalDelete
			},
			success:function(result){
				if(result.data.code==200){
					args.node.remove();
					if(args.callBack){
						args.callBack.call(ctx,result);
					}
				}
				alert(result.data.msg,"info");
			}
		});
	}
};
