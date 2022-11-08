$(function(){
	var	mainDiv = $('#deviceContainer');	//mainDIV
	mainDiv.find('.easyui-linkButton').linkbutton('RenderLB');
	//渲染条件查询表格
	var queryForm=mainDiv.find('form:first');
	var conditionForm=oc.ui.form({
		selector:queryForm
	});
	
	var searchUrl = "topo/instanceTable/device/list.htm";
	//查询按钮
	$("#deviceSearchBtn").on("click",function(){
		var searchVal = $.trim($("[name=searchVal]").val());
		var options = $("#deviceDatagrid").datagrid("options");
		options.queryParams = {searchVal:searchVal};
		$("#deviceDatagrid").datagrid("load");
	});
	//查询按钮绑定Enter查询事件
	$("[name=searchVal]").keyup(function(e){
		if(e.keyCode==13){
			$("#deviceSearchBtn").trigger('click');
		}
	});
	
	var	datagridDiv = mainDiv.find('#deviceDatagrid');
	var	datagrid = oc.ui.datagrid({
		selector:datagridDiv,	//数据渲染的地方div
		octoolbar:{
			left:[conditionForm.selector]
		},
		url : oc.resource.getUrl(searchUrl),
		singleSelect:false,
		columns:[[
	         {field:'instanceId',hidden:true},
            {
                field: 'instanceName',
                title: '资源名称', /*sortable:true,*/
                align: 'left',
                width: 150,
                formatter: function (value, row, index) {
	        	var valTmp = (value==null || value=='')?'- -':value;
	        	if("NOT_MONITORED" != row.instanceColor){
	        		 if(null==row.instanceColor){
	        			 return '<label class="light-ico_resource '+'res_normal_nothing'+'">'+valTmp+'</label>';
	        		 }else{
	        			 return '<label class="light-ico_resource '+row.instanceColor+'">'+valTmp+'</label>';
	        		 }
	        		 
	        	 }else{
	        		 return '<label>'+valTmp+'</label>';
	        	 }
	         	}
	         },
            {field: 'ip', title: 'IP地址', /*sortable:true,*/width: 90},
            {field: 'typeName', title: '设备类型', width: 90/*,sortable:true*/},
	         {field:'cpuRate',title:'CPU利用率',width:100,formatter:function(value,row,rowIndex){
	        	var cpuRateColor = row.cpuRateColor;
	        	value = (null==value?"- -":value);
				return '<div class="rate rate-t-'+cpuRateColor+'"><span class="rate-'+cpuRateColor+'" style="width:'+value+'"></span></div><span class="rt">'+value+'</span>';
	         	}
	         },
	         {field:'memeRate',title:'内存利用率',width:100,formatter:function(value,row,rowIndex){
	        	var memeRateColor = row.memeRateColor;
	        	value = (null==value?"- -":value);
				return '<div class="rate rate-t-'+memeRateColor+'"><span class="rate-'+memeRateColor+'" style="width:'+value+'"></span></div><span class="rt">'+value+'</span>';
	         	}
	         },
	         {field:'uptimeString',title:'运行时间',width:100}
         ]],
         onClickCell:function(rowIndex, field, value){
		   	 if(field == "instanceName"){	//打开资源详情页面
		   		var row = datagrid.selector.datagrid("getRows")[rowIndex];
		   		oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js", function(){
					oc.module.resmanagement.resdeatilinfonew.open({
						instanceId:row.instanceId
					});
				});
		   	 }
		},
		onLoadSuccess:function(data){
			var instanceName = $("label[class^='light-ico_resource']");
			instanceName.mouseover(function(){
				$(this).css({"text-decoration":"underline","cursor":"pointer"});
			});
			instanceName.mouseout(function(){
				$(this).css("text-decoration","none");
			});
		}
	});
});