$(function() {
	
	function init(type,cfg,container){
		var host=cfg.ip;
		var instanceId=cfg.instanceId;
		var nodeGroupId=cfg.discoverNode;
		var resourceId=cfg.resourceId;
		
		container.append('<div class="modelnavlist" style="float:right;"><a class="clicktheleft oc-btn-sidebg " style="float:left"><span class="oc-btnsetting fa fa-cog"></span><a class="clicktheleft oc-btn-sidebg " style="float:right"><span class="oc-btnleft fa fa-chevron-left"></span></a></div>');
		
		var oracleTop=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'SQL语句TOP10',
			onClick : function(){
				$('<div/>').dialog({
				    title: 'SQL语句TOP10',
				    fit:false,
				    async:true,
				    width: '1108px', 
				    height: '600px',
				    href: oc.resource.getUrl('resource/module/resource-management/resourceDetailInfo/tinytool/tinytool-oracle-list.html'),
				    onLoad:function(){
				    	oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_orcTop.js',function(){
				    		oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolOrcTable.open(cfg);
				    	});
				    }
				});
				
				
				
//				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_orcTop.js', function(){
//					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolOrcTable.open(cfg);
//				});
			}
				
		});
		var pingBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'Ping',
			onClick : function(){
				window.open('module/resource-management/resourceDetailInfo/tinytool/ping.jsp?host='+host+'&nodeGroupId='+nodeGroupId,
						'Ping',
				'height=400,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
			}
		});
		
		var sshBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'SSH',
			onClick : function(){
				window.open('module/resource-management/resourceDetailInfo/tinytool/ssh.jsp?host='+host,
						'SSH',
						'height=400,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
			}
		});
		var telnetBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'telnet',
			onClick : function(){
				window.open('module/resource-management/resourceDetailInfo/tinytool/telnet.jsp?port=23&host='+host,
						'Telnet',
						'height=400,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
			}
		});

		
		var tracertBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'Traceroute',
			onClick : function(){
				window.open('module/resource-management/resourceDetailInfo/tinytool/tracert.jsp?host='+host+'&nodeGroupId='+nodeGroupId,
						'Traceroute',
						'height=400,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
			}
		});
		
		
		var arpTableBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'ARP表',
			onClick : function(){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_arp.js', function(){
					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolArpTable.open(cfg);
				});
				
//				var arpDialogDiv=$('<div/>');
//				var arpDatagridDiv=$('<table/>');
//				
//				var arpDialog=arpDialogDiv.dialog({
//				    title: 'ARP',    
//				    width: 700,    
//				    height: 600,    
//				    cache: false,    
//				    modal: false  ,
//				    buttons:[{
//						text:'确定',
//						handler:function(){
//							arpDialogDiv.dialog('close');
//						}
//					},{
//						text:'取消',
//						handler:function(){
//							arpDialogDiv.dialog('close');
//						}
//					}]
//				}).append(arpDatagridDiv);
//				
//				oc.ui.datagrid({
//					selector:arpDatagridDiv,
//					fit : true,
//					url: oc.resource.getUrl('portal/resource/timeline/getMetrics.htm'),
//					data:{instanceId:instanceId},
//					columns:[[
//					          {field:'iPAddress',title:'IP地址',width:'25%',align:'center'},
//					          {field:'macAddress',title:'MAC地址',width:'25%',align:'center'},
//					          {field:'ifIndex',title:'接口名称',width:'25%',align:'center'},
//					          {field:'arpType',title:'接口类型',width:'25%',align:'center'}  
//					      ]]
//				});
				
				
			}
		});
		
		var routeTableBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : '路由表',
			onClick : function(){
				
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_route.js', function(){
					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolRouteTable.open(cfg);
				});
//				var routeDialogDiv=$('<div/>');
//				var routeDatagridDiv=$('<table/>');
//				
//				var routeDialog=routeDialogDiv.dialog({
//				    title: '路由表',    
//				    width: 700,    
//				    height: 600,    
//				    cache: false,    
//				    modal: false  ,
//				    buttons:[{
//						text:'确定',
//						handler:function(){
//							routeDialogDiv.dialog('close');
//						}
//					},{
//						text:'取消',
//						handler:function(){
//							routeDialogDiv.dialog('close');
//						}
//					}]
//				}).append(routeDatagridDiv);
//				
//				oc.ui.datagrid({
//					selector:routeDatagridDiv,
//					fit : true,
//					columns:[[
//					          {field:'destIPAddress',title:'目的地',width:'20%',align:'center'},
//					          {field:'subnetMask',title:'掩码',width:'20%',align:'center'},
//					          {field:'routeProtocol',title:'路由协议',width:'20%',align:'center'},
//					          {field:'nextHop',title:'下一跳',width:'20%',align:'center'} ,
//					          {field:'routeType',title:'类型',width:'20%',align:'center'}  
//					      ]]
//				});
			}
		});
		var sesssionBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'Session',
			onClick : function(){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_session.js', function(){
					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolSession.open(cfg);
				});
			}
		});
		var rollbackBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : '回滚',
			onClick : function(){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_rollback.js', function(){
					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolRollback.open(cfg);
				});
			}
		});
		var webBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'Web管理',
			onClick : function(){
				var webDialogDiv=$('<div/>');
				var webDialog=webDialogDiv.dialog({    
				    title: 'Web管理',    
				    width: 400,    
				    height: 200,    
				    cache: false,    
				    modal: false  ,
				    buttons:[{
						text:'确定',
						iconCls:"fa fa-check-circle",
						handler:function(){
							var url=webDialogDiv.find("#url").val();
							webDialogDiv.dialog('close');
							window.open(url,
									'Web管理',
									'height=400,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
						}
					},{
						text:'取消',
						iconCls:"fa fa-times-circle",
						handler:function(){
							webDialogDiv.dialog('close');
						}
					}]

				}).append('<p class="marginbot-15">输入设备的Web管理URL，打开Web管理控制台</p>')
				  .append('<label>Web管理台URL：</label><input id="url" type="text" value="http://'+host+'"/>');
				
			}
		});
		
		var mibBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'MIB',
			onClick : function(){
				window.open('module/resource-management/resourceDetailInfo/tinytool/MIBApplet.jsp?address='+host,
						'MIB',
						'height=600,width=820,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
				
			}
		});
		
		var mib2Btn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'MIB2',
			onClick : function(){
				window.open('module/resource-management/resourceDetailInfo/tinytool/MIBApplet2.jsp?address='+host,
						'MIB2',
						'height=600,width=820,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
				
			}
		});
		
		
		var netstatBtn=$("<a class='oc-btn-bg' target='_blank'/>").linkbutton({
			text : 'NetStat',
			onClick : function(){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_netstat.js', function(){
					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolNetstat.open(cfg);
				});
			}
		});
		
		
		var snmpTestBtn=$("<a class='oc-btn-bg'/>").linkbutton({
			text : 'SNMP Test',
			onClick : function(){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_snmp.js', function(){
					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolSnmp.open(cfg);
				});
			}
		});
		
		var fulldiedProcessBtn = $("<a class='oc-btn-bg'/>").linkbutton({
			text:'僵死进程',
			onClick:function(){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_fulldiedProcess.js', function(){
					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolDiedProcessTable.open(instanceId);
					//oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolArpTable.open(cfg);
				})
			}
		});
		
		var deadLockInfoBtn = $("<a class='oc-btn-bg'/>").linkbutton({
			text:'死锁信息',
			onClick:function(){
				oc.resource.loadScript('resource/module/resource-management/resourceDetailInfo/tinytool/js/tinytool_deadLockInfo.js', function(){
					oc.module.resmanagement.resdeatilinfo.tinytool.tinytoolDeadLockInfoTable.open(instanceId);
				})
			}
		});
		
		var modelnavlist=container.find(".modelnavlist");
		
		if(type==1){
			modelnavlist.append(pingBtn);
			modelnavlist.append(telnetBtn);
			modelnavlist.append(sshBtn);
			if(resourceId.lastIndexOf('snmp')==-1){
				modelnavlist.append(netstatBtn);
			}
			modelnavlist.append(tracertBtn);
			modelnavlist.append(webBtn);
			if((resourceId.indexOf('aix')>-1 
				|| resourceId.indexOf('AIX')>-1
				|| resourceId.indexOf('HPUX')>-1
				|| resourceId.indexOf('Linux')>-1
				|| resourceId.indexOf('Solaris')>-1
				) && resourceId.lastIndexOf('snmp')==-1){
				modelnavlist.append(fulldiedProcessBtn);
			}
		}else if(type==2){
			modelnavlist.append(pingBtn);
			modelnavlist.append(telnetBtn);
			modelnavlist.append(sshBtn);
			modelnavlist.append(arpTableBtn);
			modelnavlist.append(routeTableBtn);
			modelnavlist.append(webBtn);
			modelnavlist.append(tracertBtn);
			modelnavlist.append(snmpTestBtn);
			modelnavlist.append(mibBtn);
			modelnavlist.append(mib2Btn);
//			modelnavlist.append(netstatBtn);
			
		}else if(type == 3){
			if(resourceId.indexOf('MySQL')>-1 || resourceId.indexOf('SQLServer')>-1){
				modelnavlist.append(deadLockInfoBtn);
			}

			if(resourceId.indexOf('Oracle')>-1){
				modelnavlist.append(deadLockInfoBtn);
				modelnavlist.append(oracleTop);
				modelnavlist.append(sesssionBtn);
				modelnavlist.append(rollbackBtn);
			}	
		}
		
		var activenum=0;
		var modelnavlistWidth = modelnavlist.outerWidth();
		container.find(".clicktheleft").click(function(){
			//oc-btnright
			//oc-btnleft
	        if(activenum%2==0){
	        	$(".oc-btnright").attr("class","oc-btnleft fa fa-chevron-left");
	            $(".modelnavlist").animate({
	                width:"55px",
	                left:"-4px"
	            },300);
	            activenum++;
	        }else{
	        	$(".oc-btnleft").attr("class","oc-btnright fa fa-chevron-right");
	            $(".modelnavlist").animate({
	                width:modelnavlistWidth+1 + 'px',
	                left:"-7px"
	            },500);
	            activenum--;
	        }
			
		});
		
	}
	
	
	oc.ns('oc.module.resmanagement.resdeatilinfo.tinytools');
	oc.module.resmanagement.resdeatilinfo.tinytools = {
		init : function(type,cfg,container) {
			init(type,cfg,container);
		}
	}
});