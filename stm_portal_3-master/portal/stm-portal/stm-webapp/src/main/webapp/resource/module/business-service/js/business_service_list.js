(function($) {
	function businessListClass() {
	}
	businessListClass.prototype = {
		constructor : businessListClass,	
		onselects: false,
		open : function(resourceId) {
			var mainId=oc.util.generateId(),chooseRowData = null,
			//模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
			mainDiv=$('#oc_business_service_list').attr('id',mainId).panel({
				fit:true
			}),
			//表格文档对象
			datagridDiv=mainDiv.find('.oc_module_business_service_south_datagrid'),
			//表格实例
			datagrid=null;
			
			var datagrid = oc.ui.datagrid({
				selector:datagridDiv,
				pagination:false,
				url:oc.resource.getUrl('portal/business/service/getBizListByInstanceId.htm?instanceId='+resourceId),
				//data:[{instanceId:resourceId}],
				height:50,
				loadFilter:function(data){
					for(var i = 0 ; i < data.data.length ; i ++){
						var value = data.data[i].statusDefine;
						if(value){
							data.data[i].email = value.split('&')[0];
							data.data[i].phone = value.split('&')[1];
						}
					}
					return {rows:data.data};
				},
				selectOnCheck:false,
				columns:[[
			         {field:'id',hidden:true},
			         {field:'status',title:'状态',sortable:true,width:80,
			        	 formatter:statusFormatter},
			         {field:'name',title:'名称',sortable:true,width:80},
			         {field:'managerName',title:'责任人',sortable:true,width:80},
			         {field:'phone',title:'电话',sortable:true,width:80,formatter:function(value){
			        	 if(!value || value == 'null'){
			        		 return ' ';
			        	 }else{
			        		 return value;
			        	 }
			         }},
			         {field:'email',title:'邮箱',sortable:true,width:80,formatter:function(value){
			        	 if(!value || value == 'null'){
			        		 return ' ';
			        	 }else{
			        		 return value;
			        	 }
			         }},
			         {field:'remark',title:'备注',sortable:true,width:200,formatter:function(value){
			        	 return "<span title='"+(value?value:"")+"'>"+
			        	 	(value&&value.length>15?(value.substring(0,15)+"..."):(value?value:""))+"</span>";
			         }}
			     ]],
			     onLoadSuccess:function(data){
			    	 var me = $(this);
			    	 me.parent().find("tr").each(function(){
			    		 $(this).css("cursor","auto");
			    	 });
			     }
			});
		}
	};
	function statusFormatter(value){
		if(!value){
			return "<span class='light-ico greenlight'></span>";
	   	}else if(value ==3){
	   		return "<span class='light-ico redlight'></span>";
	   	}else if(value ==2){
	   		return "<span class='light-ico orangelight'></span>";
	   	}else if(value ==1){
	   		return "<span class='light-ico yellowlight'></span>";
	   	}else if(value ==0){
	   		return "<span class='light-ico greenlight'></span>";
	   	}
	}
	 //命名空间
	oc.ns('oc.business.service.list');
	oc.business.service.list={
			open:function(resourceId){
				return new businessListClass().open(resourceId);
			}
	};
	
})(jQuery);