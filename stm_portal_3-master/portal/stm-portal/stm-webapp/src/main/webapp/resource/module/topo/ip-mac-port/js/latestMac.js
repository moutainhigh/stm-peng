$(function(){
	var	mainDiv = $('#macContainer');	//mainDIV
	var	datagridDiv = mainDiv.find('#macDatagrid');	//数据表格div
	var latestUrl = "topo/mac/newmac/list.htm";
	var conditionForm = null;
	
	initMacForm();
	initMacTable();

	//查询数据
	function searchLatestMac(){
		var searchType = $("#macSearchType").combobox('getValue');
		if(!searchType) {
			alert("请选择类型","danger");
			return;
		}
		var searchVal = $.trim($("#macSearchVal").val());
		var options = $("#macDatagrid").datagrid("options");
		options.queryParams = {searchType:searchType,searchVal:searchVal};
		$("#macDatagrid").datagrid("load");
	}

	//查询按钮绑定Enter查询事件
	$("[id=macSearchVal]").keyup(function(e){
		if(e.keyCode==13) searchLatestMac();
	});
	
	//格式化数据为下拉框
	function formatLatestMacSelect(value){
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

	function initMacForm(){
		var queryForm=mainDiv.find('#addMacSearchForm');
		conditionForm = oc.ui.form({
			selector:queryForm,
			combobox:[{	//渲染下拉框
				selector:'[id="macSearchType"]',
				data:[
				      {id:'1',name:'IP地址'},
				      {id:'2',name:'MAC地址'},
				      {id:'3',name:'上联设备IP'}
				]
			}]
		});
	}

	function initMacTable(){
		var	datagrid = oc.ui.datagrid({
			selector:datagridDiv,
			hideReset:true,
			hideSearch:true,
			url : oc.resource.getUrl(latestUrl),
			singleSelect:false,
			columns:[[
		         {field:'id',checkbox:true},
		         {field:'hostName',title:'主机名称',ellipsis:true,width:120},
		         {field:'mac',title:'MAC',sortable:true,width:120},
		         {field:'ip',title:'IP地址',sortable:true,width:120,formatter: function (value, row, index){
		         	var formatIp = new FormatIp();
		         	return formatIp.formatter(value);
				 	}
				 },
		         {field:'upDeviceName',title:'上联设备名称',width:150},
		         {field:'upDeviceIp',title:'上联设备IP',width:150,formatter : function(value, row, index){
		        	 return formatLatestMacSelect(value);
					}},
		         {field:'upDeviceInterface',title:'上联设备接口',width:150},
		         {field:'updateTimeStr',title:'更新时间',width:120},
		         {field:'opration',title:'定位',width:30,formatter: function(value,row,index){
		        	 return (row.positionFlag == 1)?'<span title="可定位" class="ico ico_position"></span>':'<span class="ico ico_notlocate"></span>';
	        	 }}
	         ]],
	         onLoadSuccess:function(){
	        	 oc.ui.combobox({
	        		 selector : $("select[name='upDeviceIpAddress']")
	        	 });
	         },
	  		onClickCell:function(rowIndex, field, value){
	 		   	 if(field == "opration"){
	 		   		var row = datagrid.selector.datagrid("getRows")[rowIndex];
	 		   		if(row.positionFlag && row.positionFlag==1){
	 		   			var ip = row.ip;
	 		   			ip_mac_port_Hide();	//隐藏ip-mac-port界面
	 		   			eve("topo.locate",null,{val:ip});
	 		   		}
	 		   	 }
	 		},
	    	octoolbar:{
	    		 left:[conditionForm.selector],
		    	 right:[{
						iconCls: 'icon-search',
						text:"查询",
						onClick: function(){
							searchLatestMac();
						}
					},"&nbsp;",{
						iconCls: 'fa fa-plus',
						text:"全部加入基准表",
						onClick: function(){
							oc.ui.confirm('是否全部加入基准表？',function() {
								oc.util.ajax({
									url:oc.resource.getUrl('topo/mac/addbse/all.htm'),
									type: 'POST',
									dataType: "json", 
									successMsg:null,
									success:function(data){
										alert(data.data);
										datagrid.reLoad();
									}
								});
							})
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
							data:{ids:ids.join(","),addType:"latest"},
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
					iconCls:"fa fa-refresh",
					text:"刷新",
					onClick:function(){
						oc.util.ajax({
							url:oc.resource.getUrl('topo/mac/latest/refresh.htm'),
							type: 'POST',
							dataType: "json", 
							successMsg:null,
							success:function(data){
								alert(data.data);
								datagrid.reLoad();
							}
						});
					}
				}]
		}
		});
	}
});