$(function(){
	oc.ns('oc.business.service.south');
	var mainId=oc.util.generateId(),chooseRowData = null,
	//模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
	mainDiv=$('#oc_business_service_south').attr('id',mainId).panel({
		fit:true
	}),
	//表格文档对象
	datagridDiv=mainDiv.find('.oc_module_business_service_south_datagrid'),
	//表格实例
	datagrid=null;
	
	var datagrid = oc.ui.datagrid({
		selector:datagridDiv,
		pagination:true,
		pageList:[5],
		url:oc.resource.getUrl('portal/business/service/getListPage.htm'),
		height:50,
		columns:[[
	         {field:'id',title:'业务服务id',hidden:true},
	         {field:'status',title:'状态',sortable:true,align:'center',width:80,
	        	 formatter:statusFormatter},
	         {field:'name',title:'业务应用',sortable:true,align:'center',width:80},
	         {field:'entryName',title:'责任人',sortable:true,align:'center',width:80},
	         {field:'remark',title:'备注',sortable:true,align:'center',width:200,formatter:function(value){
	        	 return "<span title='"+(value?value:"")+"'>"+
	        	 	(value&&value.length>15?(value.substring(0,15)+"..."):(value?value:""))+"</span>";
	         }}
	     ]],
	     onClickRow:function(rowIndex, rowData){
	    	 chooseRowData = rowData;
	    	 oc.index.activeContent.load(oc.resource.getUrl('resource/module/business-service/business_service_index.html'),
    			 function(){
		    		 oc.resource.loadScript('resource/module/business-service/js/business_service_index.js',function(){
		    			 oc.business.service.index.initRow(rowData);
		    		 });
	    	 });
	     },
	     onLoadSuccess:function(data){
	    	 oc.business.service.center.reloadCarousel(data);
	    	 if(data.rows.length==0){
	    		 datagridDiv.datagrid('appendRow',{
	    				name: '暂无数据'
	    			});
	    	 }
	    	 var me = $(this);
	    	 me.parent().find("tr").each(function(){
	    		 $(this).attr("title","点我进入业务应用视图");
	    	 });
	     }
	});
	function statusFormatter(value){
		if(!value){
			return "<span class='light-ico greenlight'></span>";
	   	}else if(value =="CRITICAL"){
	   		return "<span class='light-ico redlight'></span>";
	   	}else if(value =="SERIOUS"){
	   		return "<span class='light-ico orangelight'></span>";
	   	}else if(value =="WARN"){
	   		return "<span class='light-ico yellowlight'></span>";
	   	}else if(value =="NORMAL"){
	   		return "<span class='light-ico greenlight'></span>";
	   	}
	}
});
