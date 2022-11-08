var clickId,clickMac,historyDlg;
$(function(){
	var	mainDiv=$('#historyContainer');	//mainDIV
	var historyUrl = "topo/mac/history/list.htm";
	var conditionForm = null;
	initHishtoryForm();
	initHistoryTable();
	
	//查询数据
	function searchHistory(){
		var mac = $.trim($("#macAddress").val());
		if(!mac) {
			alert("请输入MAC地址","danger");
			return;
		}
		var startTime = $('#startTime').datebox('getValue');
		var endTime = $('#endTime').datebox('getValue');
		if(endTime != ""){
			if(startTime == ""){
				alert("起始时间不能为空","danger");
				return;
			}else if(startTime > endTime){
				alert("起始时间不能晚于截止时间","danger");
				return;
			}
		}
		
		var options = $("#historyDatagrid").datagrid("options");
		options.queryParams = {mac:mac,startTime:startTime,endTime:endTime};
		$("#historyDatagrid").datagrid("load");
	}
	
	//查询按钮绑定Enter查询事件
	$("[id=macAddress]").keyup(function(e){
		if(e.keyCode==13) searchHistory();
	});
	
	function initHistoryTable(){
		var	datagridDiv=mainDiv.find('#historyDatagrid');
		var	datagrid = oc.ui.datagrid({
			selector:datagridDiv,
			queryForm:null,
			hideReset:true,
			hideSearch:true,
			url : oc.resource.getUrl(historyUrl),
			singleSelect:false,
			octoolbar:{
				 left:[conditionForm.selector],
		    	 right:[{
						iconCls: 'icon-search',
						text:"查询",
						onClick: function(){
							searchHistory();
						}
					},"&nbsp;",{
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
						var rows = datagrid.getSelections();
						if (rows.length == 0) {
							alert('请至少选择一条数据', 'danger');
							return;
						}
						var macs = [];
						for(var i=0,len=rows.length;i<len;i++){
							macs[i] = rows[i].mac;
						}
						oc.ui.confirm('是否删除选中数据？',function() {
							oc.util.ajax({
								url:oc.resource.getUrl('topo/mac/history/delMacs.htm'),
								type: 'POST',
								dataType: "json", 
								data:{macs:macs.join(",")},
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
		         {field:'mac',title:'MAC',sortable:true,width:120,formatter: function(value,row,index){
		        	 return '<label macHistory="macHistory">'+value+'</label>';
		         	}},
		         {field:'ip',title:'IP地址',sortable:true,width:120,formatter: function (value, row, index){
		         	var formatIp = new FormatIp();
		         	return formatIp.formatter(value);
				 	}
				 },
		         {field:'upDeviceName',title:'上联设备名称',width:150},
		         {field:'upDeviceIp',title:'上联设备IP',width:150,formatter : function(value, row, index){
		        	 return formatHistoryMacSelect(value);
					}},
		         {field:'upDeviceInterface',title:'上联设备接口',width:150},
		         {field:'updateTimeStr',title:'更新时间',width:120},
		         {field:'opration',title:'&nbsp;&nbsp;&nbsp;&nbsp;定位',align:'center',width:30,formatter: function(value,row,index){
		        	 return (row.positionFlag == 1)?'<span title="可定位" class="ico ico_position"></span>':'<span class="ico ico_notlocate"></span>';
	        	 }}
	         ]],
	         onLoadSuccess:function(){
	        	 oc.ui.combobox({
	        		 selector : $("select[name='upDeviceIpAddress']")
	        	 });
				var mac = $("label[macHistory]");
				mac.mouseover(function(){
					$(this).css({"text-decoration":"underline","cursor":"pointer"});
				});
				mac.mouseout(function(){
					$(this).css("text-decoration","none");
				});
	         },
			onClickCell:function(rowIndex, field, value){
			   	var row = datagrid.selector.datagrid("getRows")[rowIndex];
			   	clickId = row.id;
			   	clickMac = row.mac;
			   	 if(field == "mac"){
			   		openDlg(clickId,clickMac);
			   	 }else if(field == "opration"){
			   		if(row.positionFlag && row.positionFlag==1){
			   			var ip = row.ip;
			   			ip_mac_port_Hide();	//隐藏ip-mac-port界面
			   			eve("topo.locate",null,{val:ip});
			   		}
	 		   	 }
			}
		});
	}

	//格式化数据为下拉框
	function formatHistoryMacSelect(value){
		var vals = [];
	 	if(value != null && value != ''){
	 		ips = value.split(",");
	 		if(ips.length > 0){
	 			$.each(ips, function (index,val) {
	 				vals.push({"text":val,"value":index});
	 			});
	 		}
	 	}
	 	var select = $("<select name='upDeviceIpAddress'/>");
		for(var index in vals){
			var option = $("<option/>").html(vals[index].text);
			select.append(option);
		}
		return $("<div/>").append(select).html();
	}

	function initHishtoryForm(){
		var queryForm = mainDiv.find('#histtoryForm');
		conditionForm = oc.ui.form({
			selector:queryForm,
			onChange:function(val){},
			datetimebox:[{
				selector:'[id=startTime]',
				value:''
			},{
				selector:'[id=endTime]',
				value:''
			}]
		});
	}
	
	//弹出变更历史窗口
	function openDlg(){
		historyDlg = $("<div />").dialog({
			title: '历史变更',
			href:oc.resource.getUrl("resource/module/topo/ip-mac-port/subHistoryChange.html"),
		    width: 1200,
		    height: 410,
		    cache: false,
		    modal: true
		})
	}
});

//关闭子历史变更框
function closeDlg(){
	if(historyDlg){
		historyDlg.dialog("close");
	}
}