oc.ns('oc.topo.graph.dialogs');
oc.topo.graph.dialogs={
		tlenet:function(){
			var dia = $("<div/>").dialog({
				title: 'Telnet',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/telnet/telnet.html"),
			    width: 650,    
			    height: 450,    
			    cache: false,    
			    modal: true ,
			    buttons:[{
					text:'链接',
					handler:function(){
						alert("你 开始了链接");
					}
				},{
					text:'断开',
					handler:function(){
						alert("你断开了链接");
					}
				}]
			})
		},
		addressTable:function(){
			$("<div/>").dialog({
				title: '信息表-地址栏',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/info/addressTable.html"),
			    width: 650,    
			    height: 450,    
			    cache: false,    
			    modal: true
			});
		},
		btnrouteInfo:function(){
			$("<div/>").dialog({
				title: '信息表-路由器表',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/info/routeInfo.html"),
			    width: 650,    
			    height: 450,    
			    cache: false,    
			    modal: true
			});
		},
		SSH:function(){
			$("<div/>").dialog({
				title: 'SSH',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/SSH/SSH.html"),
			    width: 650,    
			    height: 185,    
			    cache: false,    
			    buttons:[{
					text:'连接',
					handler:function(){
						alert("你 开始了链接");
					}
				},{
					text:'断开',
					handler:function(){
						alert("你断开了链接");
					}
				}]
			});
		},
		Ping:function(){
			var dia = $("<div/>").dialog({
				title: 'Ping',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/Ping/Ping.html"),
			    width: 650,    
			    height: 570,    
			    cache: false,    
			    buttons:[{
					text:'确定',
					handler:function(){
						alert("你 点击了确定");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		},
		SNMPTest:function(){
			var dia = $("<div/>").dialog({
				title: 'SNMP Test',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/SNMPTest/SNMPTest.html"),
			    width: 650,    
			    height: 550,    
			    cache: false,    
			    buttons:[{
					text:'保存',
					handler:function(){
						alert("你点击了保存");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		},
		webManager:function(){
			var dia = $("<div/>").dialog({
				title: 'webManager',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/webManager/webManager.html"),
			    width: 300,    
			    height: 150,    
			    buttons:[{
					text:'保存',
					handler:function(){
						alert("你点击了保存");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		},
		TraceRoute:function(){
			var dia = $("<div/>").dialog({
				title: 'TraceRoute',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/TraceRoute/TraceRoute.html"),
			    width: 650,    
			    height: 350,    
			    buttons:[{
					text:'保存',
					handler:function(){
						alert("你点击了保存");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		},
		commonLink:function(){
			var dia = $("<div/>").dialog({
				title: '常用链接',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/commonLink/commonLink.html"),
			    width: 650,    
			    height: 470,    
			    buttons:[{
					text:'保存',
					handler:function(){
						alert("你点击了保存");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		},
		updateBaseInfo:function(){
			var dia = $("<div/>").dialog({
				title: '修改节点基本信息',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/updateBaseInfo/updateBaseInfo.html"),
			    width: 350,    
			    height: 230,    
			    buttons:[{
					text:'保存',
					handler:function(){
						alert("你点击了保存");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		},
		replaceIcon:function(){
			var dia = $("<div/>").dialog({
				title: '替换图标',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/replaceIcon/replaceIcon.html"),
			    width: 470,    
			    height: 370,    
			    buttons:[{
					text:'保存',
					handler:function(){
						alert("你点击了保存");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		},
		doubleClickSet:function(){
			var dia = $("<div/>").dialog({
				title: '双击操作设置',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/doubleClickSet/doubleClickSet.html"),
			    width: 370,    
			    height: 330,    
			    buttons:[{
					text:'确定',
					handler:function(){
						alert("你点击了确定");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		},
		ARPTable:function(){
			var dia = $("<div/>").dialog({
				title: 'ARP表',
				href:oc.resource.getUrl("resource/module/topo/contextMenu/info/ARPTable/ARPTable.html"),
			    width: 470,    
			    height: 370,    
			    buttons:[{
					text:'保存',
					handler:function(){
						alert("你点击了保存");
					}
				},{
					text:'取消',
					handler:function(){
						alert("你点击了取消");
						dia.dialog('close');
					}
				}]
			});
		}
}




