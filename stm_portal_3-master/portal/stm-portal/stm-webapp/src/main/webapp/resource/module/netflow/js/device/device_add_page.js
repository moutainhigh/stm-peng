$(function() {
	// cfg = {
	// 					selector:"#resource_select_pickGrid",
	// 					leftColumns:[[{
	// 						field : "",
	// 						title : "",
	// 						checkbox :true,
	// 						sortable : true
	// 					}]],
	// 					rightColumns:rightColumns,
	// 					isInteractive:isAllowUpdateAllProfile,
	// 					moveBeforeEvent:function(data,direction){
							
	// 						var moveData = new Array();
							
	// 						if(direction == 'left'){
								
	// 							var curMonitorSelect = $('#filterInstanceStateFormId').find('input[name=monitorState]:checked').val();
	// 							//向左移动
	// 							for(var i = 0 ; i < data.length ; i ++){

	// 								if(data[i].lifeState == 'MONITORED' && curMonitorSelect == 2 && data[i].strategyID != 0){
	// 									continue;
	// 								}
	// 								data[i].resourceShowName = data[i].resourceShowName.replace('greenlight','graylight');
	// 								moveData.push(data[i]);
									
	// 							}
								
	// 						}else if(direction == 'right'){
								
	// 							//向右移动
	// 							for(var i = 0 ; i < data.length ; i ++){
									
	// 								data[i].resourceShowName = data[i].resourceShowName.replace('graylight','greenlight');
									
	// 							}
								
	// 							moveData = $.extend(true,[],data);
								
	// 						}
							
	// 						return moveData;
	// 					},
	// 					onMoveSuccess:function(srcGridData,targetGridData,direction){
	// 						if(direction == 'left' && $('#filterInstanceStateFormId').find('input[name=monitorState]:checked').val() != 2){
	// 							$('#searchResourceOrIpInput').val('');
	// 							$('#filterInstanceStateFormId').find('input[name=monitorState][value=2]').attr('checked','checked');
	// 							showMainProfileResourceLeftGrid(getRightGridResourceId(),singleStrategyInfo.profileInfo.resourceId,2,'');
	// 						}
	// 					}
	// 			};

	device_add_pickgrid = oc.ui.pickgrid({
		selector:"#add_device_checkout_cc_data",
		leftUrl : oc.resource.getUrl('netflow/device/confList.htm'),
		leftColumns:[[{
			field : "info",
			title : "-",
			checkbox :true,
			sortable : true
		},{
			field : "name",
			title : "设备名称",
			width : "33%",
		}, {
			field : 'ip',
			title : 'IP',
			width : "33%",
		}, {
			field : 'type',
			title : '设备类型',
			width : "32%",
			formatter : function(value,row,index){
				switch(value){
					case "Switch" :
						return "交换机";
					case "Router" :
						return "路由器";
					case "Firewall" :
						return "防火墙";
					case "WirelessAP" :
						return "无线ap";
				}
				return value;
			}
		}]],
		rightColumns:[[{
			field : "info",
			title : "-",
			checkbox :true,
			sortable : true,
			styler : function(value, row, index) {
				return {
					class : 'd_device_cbox'
				};
			},
		},{
			field : "name",
			title : "设备名称",
			width : "33%",
		}, {
			field : 'ip',
			title : 'IP',
			width : "33%",
		}, {
			field : 'type',
			title : '设备类型',
			width : "32%",
			formatter : function(value,row,index){
				switch(value){
					case "Switch" :
						return "交换机";
					case "Router" :
						return "路由器";
					case "Firewall" :
						return "防火墙";
					case "WirelessAP" :
						return "无线ap";
				}
				return value;
			}
		}]],
	});


	// oc.ui.datagrid({
	// 	selector : $("#add_device_checkout_cc_data"),
	// 	queryForm : null,
	// 	url : oc.resource.getUrl('netflow/device/confList.htm'),
	// 	width : '100%',
	// 	striped : true,
	// 	fit : true,
	// 	columns : [ [ {
	// 		field : 'info',
	// 		title : '-',
	// 		styler : function(value, row, index) {
	// 			return {
	// 				class : 'd_device_cbox'
	// 			};
	// 		},
	// 		checkbox : true
	// 	}, {
	// 		field : 'name',
	// 		title : '设备名称',
	// 		width : "33%",
	// 	}, {
	// 		field : 'ip',
	// 		title : 'IP',
	// 		width : "33%",
	// 	}, {
	// 		field : 'type',
	// 		title : '设备类型',
	// 		width : "33%",
	// 		formatter : function(value,row,index){
	// 			switch(value){
	// 				case "Switch" :
	// 					return "交换机";
	// 				case "Router" :
	// 					return "路由器";
	// 				case "Firewall" :
	// 					return "防火墙";
	// 				case "WirelessAP" :
	// 					return "无线ap";
	// 			}
	// 			return value;
	// 		}
	// 	} ] ]
	// });
});