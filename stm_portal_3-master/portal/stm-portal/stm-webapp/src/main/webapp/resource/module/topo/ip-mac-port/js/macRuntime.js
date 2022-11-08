$(function(){
	var	mainDiv = $('#realTimeContainer');	//主容器
	var	datagridDiv = mainDiv.find('#realTimeDataGrid');	//数据表格容器
	var runtimeUrl = "topo/mac/runtime/list.htm";
	
	//渲染条件查询表格
	var queryForm  = mainDiv.find('#runtimeSearchForm');
	var conditionForm = oc.ui.form({
		selector:queryForm,
		combobox:[{	//渲染下拉框
			selector:'#searchType',
			data:[
			      {id:'1',name:'IP地址'},
			      {id:'2',name:'MAC地址'},
			      {id:'3',name:'上联设备IP'}
			]
		}]
	});
	
	var datagrid = oc.ui.datagrid({
		selector:datagridDiv,	//数据渲染的地方div
		url : oc.resource.getUrl(runtimeUrl),
		hideReset:true,//是否隐藏重置按钮
		hideSearch:true,//是否隐藏搜索按钮
		singleSelect:false,
    	octoolbar:{
    		 left:[queryForm],
	    	 right:[{
					iconCls: 'icon-search',
					text:"查询",
					onClick: function(){
						search();
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
						data:{ids:ids.join(","),addType:"runtime"},
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
						url:oc.resource.getUrl('topo/mac/refresh.htm'),
						type: 'POST',
						dataType: "json", 
						successMsg:null,
						success:function(data){
							alert(data.data);
							datagrid.datagrid("reload");
						},
						error:function(){
							alert("请求超时，请刷新页面");
						}
					});
				}
			}
			]
    	},
		columns:[[
	         {field:'id',checkbox:true},
	         {field:'hostName',title:'主机名称',ellipsis:true,width:120},
	         {field:'mac',title:'MAC',sortable:true,width:120,formatter: function(value,row,index){
	        	//(0：新增，1：历史)
	        	return (row.exist == 0)?"<lable>"+value+"&nbsp;</lable><span class='top_new_table'></span>":value;
	 			}
	         },
	         {field:'ip',title:'IP地址',ellipsis:true,sortable:true,width:120,formatter: function (value, row, index){
	         	var formatIp = new FormatIp();
	         	return formatIp.formatter(value);
			 	}
			 },
	         {field:'upDeviceName',title:'上联设备名称',width:150},
	         {field:'upDeviceIp',title:'上联设备IP',width:150,formatter : function(value, row, index){
	        	 return formatToSelect(value);
				}},
	         {field:'upDeviceInterface',title:'上联设备接口',width:150},
	         {field:'updateTimeStr',title:'更新时间',width:120},
	         {field:'opration',title:'定位',width:30,formatter: function(value,row,index){
	        	 return (row.positionFlag == 1)?'<span title="可定位" class="ico ico_position"></span>':'<span class="ico ico_notlocate"></span>';
        	 }}
         ]],
         onLoadSuccess:function(data){
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
 		}
	});
	
	//查询按钮绑定Enter查询事件
	$("[name=searchVal]").keyup(function(e){
		if(e.keyCode==13) search();
	});
	
	//查询数据
	function search(){
		var searchType = $("#searchType").combobox('getValue');
		if(!searchType) {
			alert("请选择类型","danger");
			return;
		}
		var searchVal = $.trim($("#searchVal").val());
		var options = $("#realTimeDataGrid").datagrid("options");
		options.queryParams = {searchType:searchType,searchVal:searchVal};
		$("#realTimeDataGrid").datagrid("load");
	}

	//格式化数据为下拉框
	function formatToSelect(value){
		var vals = [];
	 	if(value != null && value != ''){
	 		ips = value.split(",");
	 		if(ips.length > 0){
	 			$.each(ips, function (index,val) {
	 				vals.push({"text":val});
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
});