$(function(){
	var	mainDiv=$('#linkContainer');	//mainDIV
	mainDiv.find('.easyui-linkButton').linkbutton('RenderLB');
	var searchUrl = "topo/instanceTable/link/list/page.htm";
	var	datagridDiv=mainDiv.find('#linkDatagrid');
	var	datagrid = oc.ui.datagrid({
		selector:datagridDiv,
		url : oc.resource.getUrl(searchUrl),
		singleSelect:false,
		columns:[[
	         {field:'id',title:"资源实例ID",checkbox:true},
	         {field:'instanceId',hidden:true},
	         {field:'srcMainInstIP',title:'源IP地址',sortable:true,width:110},
	         {field:'destMainInstIP',title:'目的IP地址',sortable:true,width:110},
	         {field:'srcMainInstName',title:'源接口',ellipsis:true,width:120,formatter: function(value,row,index){
	        	 return (value==null || value=='')?'- -':"<span class='if if-"+row.srcIfColor+"' title='"+value+"'>"+value+"</span>";
	 			}
	         },
	         {field:'destMainInsName',title:'目的接口',ellipsis:true,width:120,formatter: function(value,row,index){
	        	 return (value==null || value=='')?'- -':"<span class='if if-"+row.destIfColor+"' title='"+value+"'>"+value+"</span>";
	 			}
	         },
	         {field:'insStatus',title:'状态',width:80,formatter: function(value,row,index){
	        	 var tmp = ("disabled"==value)?'<span>×</span>':'';
	        	 return '<span class="topo-line line-'+value+'" >'+tmp+'</span>';
	 			}
	         },
	         {field:'getValInterface',title:'取值接口',width:100},
	         {field:'downDirect',title:'下行方向',width:100},
	         {field:'ifInOctetsSpeed',title:'上行流量',width:90,formatter: function(value,row,index){return value==null?"- -":value}},
	         {field:'ifOutOctetsSpeed',title:'下行流量',width:90,formatter: function(value,row,index){return value==null?"- -":value}},
	         {field:'ifInBandWidthUtil',title:'上行带宽利用率',width:120,formatter: function(value,row,index){return value==null?"- -":value}},
	         {field:'ifOutBandWidthUtil',title:'下行带宽利用率',width:120,formatter: function(value,row,index){return value==null?"- -":value}},
	         {field:'bandWidth',title:'链路带宽',width:130,formatter: function(value,row,index){return value==null?"- -":value}},
	         {field:'operation',title:'操作',align:"center",halign:'center',width:82,formatter: function(value,row,index){
	        	 var span=$("<span/>").addClass("");
	        	 if("unmonitor" == row.insStatus){
	        		 span.attr('rowIndex', index).addClass('');
	        	 }else{
	        		 span.attr('rowIndex', index).addClass('icon-edit ');	        		 
	        	 }
	        	 return span;
	        	 //return ("unmonitor" == row.insStatus)?"":'<span class="icon-edit "></span>';
        	 }}
         ]],
/*		onClickCell:function(rowIndex, field, value){
			debugger;
		   	 if(field == "operation"){
		   		var row = datagrid.selector.datagrid("getRows")[rowIndex];
		   		if("unmonitor" != row.insStatus){
		   			var link = {d:{instanceId:row.instanceId},grid:datagrid};
		   			oc.resource.loadScript("resource/module/topo/contextMenu/EditLink.js", function(){
		   				new EditLinkDia(link);
		   			});
		   		}
		   	 }
		}*/
		onLoadSuccess : function() {
			var execution = function(type, rowIndex) {
				if (type == "operation") {
			   		var row = datagrid.selector.datagrid("getRows")[rowIndex];
			   		if("unmonitor" != row.insStatus){
			   			var link = {d:{instanceId:row.instanceId},grid:datagrid};
			   			oc.resource.loadScript("resource/module/topo/contextMenu/EditLink.js", function(){
			   				new EditLinkDia(link);
			   			});
			   		}
				}
			};
			mainDiv.find("span.icon-edit ").bind('click',
					function(e) {
						var rowIndex = $(this).attr('rowIndex');
						execution("operation", rowIndex);
						e.stopPropagation();
					});
		}
	});
	
	//绑定事件
	$("#searchBtn").on("click",function(){
		searchLinkData();
	});
	//查询按钮绑定Enter查询事件
	$("[name='linkSearchVal']").keyup(function(e){
		if(e.keyCode==13){
			$("#searchBtn").trigger('click');
			e.stopPropagation();
		}
	});
	
	//告警设置
	$("#warnSet").on("click",function(){
		new LinkAlarmRules();
	});
	
	//带宽利用率-指标设置
	$("#bandWidthUtilSet").on("click",function(){
		var rows = datagrid.getSelections();
		if (rows.length == 0) {
			alert('请至少选择一条链路', 'danger');
			return;
		}
		var instanceIds = [];
		for(var i=0,len=rows.length;i<len;i++){
			instanceIds[i] = rows[i].instanceId;
		}
		oc.resource.loadScript("resource/module/topo/instanceTable/js/editBandWidthUtil.js", function(){
	   		new EditBindWidthUtilDia(instanceIds);
	   	});
	});
	
	//查询链路数据
	function searchLinkData(){
		var searchVal = $("[name='linkSearchVal']").val();
		var options = $("#linkDatagrid").datagrid("options");
		options.queryParams = {searchVal:searchVal};
		$("#linkDatagrid").datagrid("load");
	}
});