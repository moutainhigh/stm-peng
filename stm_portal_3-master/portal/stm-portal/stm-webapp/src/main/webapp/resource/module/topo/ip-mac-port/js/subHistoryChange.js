$(function(){
	var	mainDiv=$('#subHistoryContainer');	//mainDIV
	var	datagridDiv = mainDiv.find('#subHistoryDatagrid');	//数据表格div
	var subHistoryUrl = "topo/mac/subHistory/list.htm?";
	
	initSubHistoryTable();
	
	//初始化表格
	function initSubHistoryTable(){
		var	datagrid = oc.ui.datagrid({	//数据表格
			selector:datagridDiv,	//数据渲染的地方div
			url : oc.resource.getUrl(subHistoryUrl+"id="+clickId+"&mac="+clickMac),
			singleSelect:false,
			octoolbar:{
		    	 right:[{
					iconCls: 'fa fa-plus',
					text:"加入基准表",
					onClick: function(){
						var ids = datagrid.getSelectIds();
						if(ids.length == 0){
							alert("请选择至少一条数据","danger");
							return;
						}
						oc.util.ajax({
							url:oc.resource.getUrl('topo/mac/exist.htm'),
							type: 'POST',
							dataType: "json", 
							data:{ids:ids.join(","),addType:"history"},
							successMsg:null,
							success:function(data){
								if("exist"==data.data){
									oc.ui.confirm('重复添加,是否覆盖？',function() {
										oc.util.ajax({
											url:oc.resource.getUrl('topo/mac/addbse.htm'),
											type: 'POST',
											dataType: "json", 
											data:{ids:ids.join(","),addType:"runtime"},
											successMsg:null,
											success:function(data){
												alert(data.data);
												datagrid.reLoad();
											}
										});
									});
								}else{
									alert(data.data);
								}
							}
						});
					}
				},"&nbsp;",{
					iconCls:"fa fa-trash-o",
					text:"删除",
					onClick:function(){
						var ids = datagrid.getSelectIds();
						if (ids.length == 0) {
							alert('请至少选择一条数据', 'danger');
							return;
						}
						oc.ui.confirm('是否删除选中数据？',function() {
							oc.util.ajax({
								url:oc.resource.getUrl('topo/mac/history/delIds.htm'),
								type: 'POST',
								dataType: "json", 
								data:{ids:ids.join(",")},
								successMsg:null,
								success:function(data){
									alert(data.data);
									datagrid.reLoad();
								}
							});
						});
					}
				}]
			},
			columns:[[
		         {field:'id',checkbox:true},
		         {field:'hostName',title:'主机名称',ellipsis:true,width:120},
		         {field:'mac',title:'MAC',sortable:true,width:120},
		         {field:'ip',title:'IP地址',sortable:true,width:120,formatter: function (value, row, index){
		         	var formatIp = new FormatIp();
		         	return formatIp.formatter(value);
				 	}
				 },
		         {field:'upDeviceName',title:'上联设备名称',width:100},
		         {field:'upDeviceIp',title:'上联设备IP',width:120,formatter : function(value, row, index){
		        	 return formatSubHistoryMacSelect(value);
					}},
		         {field:'upDeviceInterface',title:'上联设备接口',width:120},
		         {field:'updateTimeStr',title:'更新时间',width:120},
		         {field:'opration',title:'定位',width:30,formatter: function(value,row,index){
		        	 return (row.positionFlag == 1)?'<span title="可定位" class="ico ico_position"></span>':'<span class="ico ico_notlocate"></span>';
	        	 }}
	         ]],
	         onClickCell:function(rowIndex, field, value){
	        	 if(field == "opration"){
	  		   		var row = datagrid.selector.datagrid("getRows")[rowIndex];
		  		   	if(row.positionFlag && row.positionFlag==1){
			  		   	var ip = row.ip;
		  		   		closeDlg();	//关闭历史弹出窗口
		  		   		ip_mac_port_Hide();	//隐藏ip-mac-port界面
		  		   		eve("topo.locate",null,{val:ip});
		  		   	}
	  		   	 }
			}
		});
	}

	//格式化数据为下拉框
	function formatSubHistoryMacSelect(value){
		var vals = [];
	 	if(value != null && value != ''){
	 		ips = value.split(",");
	 		if(ips.length > 0){
	 			$.each(ips, function (index,val) {
	 				vals.push({"text":val,"value":index});
	 			});
	 		}
	 	}
	 	
		var tmp = $("<div style='width:140px;'></div>");
	 	setTimeout(function(){
	 		tmp.combobox({
		 		textField: 'text',
		 		valueField:'value',
		 		data:vals,
		 		editable:false
		 	});
	 	},0);
	 	return tmp.get(0);
	}
});