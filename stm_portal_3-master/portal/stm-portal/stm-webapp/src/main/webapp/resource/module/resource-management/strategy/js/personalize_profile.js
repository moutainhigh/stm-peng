(function($){
	var mainId=oc.util.generateId(),
		//模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
		mainDiv=$('#personalize_profile_list_main').attr('id',mainId).panel({
			title:'',
			fit:true,
			isOcAutoWidth:true
		}),
		//表格查询表单对象
		
		queryForm= oc.ui.form({
			selector:mainDiv.find('.oc-form'),
			combobox:[{
				selector:mainDiv.find('.oc-form').find('[name=profileDesc]'),
				filter:function(data){
					return data.data;
				},
				url:oc.resource.getUrl('strategy/strategyAll/getPrentStrategyType.htm'),
				onChange : function(id){
					queryForm.ocui[1].reLoad(oc.resource.getUrl('strategy/strategyAll/getChildCategoryDefById.htm?id='+ id));
					mainDiv.find('.oc-form').find('[name=resourceId]').val("");
					datagrid.reLoad();
				}
			},{
				selector:'[name=resourceId]',
				filter:function(data){
					return data.data;
				},
				onChange : function(id){
					datagrid.reLoad();
				}
			}]
		});
		//表格文档对象
		datagridDiv=mainDiv.find('.personalize_profile_list_datagrid'),
		//表格实例
		datagrid=null;
	
	datagrid=oc.ui.datagrid({
		selector:datagridDiv,
		pageSize:$.cookie('pageSize_personalize_profile')==null ? 15 : $.cookie('pageSize_personalize_profile'),
		url:oc.resource.getUrl('strategy/strategyAll/getPersonalizeStrategyAll.htm'),
		queryForm:queryForm,
		hideReset : true,
		hideSearch : true,
		octoolbar:{
			left:[queryForm.selector]
		},
		columns:[[
		     {field:'id',hidden : true},
		     {field:'isUse',title:'策略状态',sortable:true,align:'left',width:'10%'},
	         {field:'strategyName',title:'策略名称',sortable:true,align:'left',width:'35%',
		    	 formatter:function(value,rowData,rowIndex){ 
		 	     		return "<a class='show_profile' id='"+rowData.id+"'>"+'<span title="' + value + '">' + rowData.strategyName + '</span>'+"</a>"; 
		        	 }},
	         {field:'strategyIp',title:'IP地址',align:'left',width:'35%',
		        formatter:function(value,rowData,rowIndex){
		        	var select = $("<select name='personalzeIpAddress' />");
		        	var ips = rowData.strategyIps.instanceIP;
		        	for(var index in ips){
		        		var option = $("<option/>").val(ips[index].id).html(ips[index].name);
		        		select.append(option);
		        	}
		        	return $("<div/>").append(select).html();
		        }},
	         {field:'strategyType',title:'策略类型',sortable:true,align:'left',width:'20%'}
         ]],
         onLoadSuccess:function(data){ 
        	 
        	 $(".show_profile").on('click',function(){
        		 var profileId=$(this).attr('id');
    			 oc.resource.loadScript('resource/module/resource-management/js/personalize_strategy_detail.js',function(){
    					oc.personalizeprofile.strategy.detail.show(profileId);
    			});
        	 });
        	 
        	 oc.ui.combobox({
        		 selector : $("select[name='personalzeIpAddress']")
        	 });
         }
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_personalize_profile');
				$.cookie('pageSize_personalize_profile',pageSize);
			}
		});
	}
	
	// 命名空间
	oc.ns('oc.module.personalize.profile');
	oc.module.personalize.profile = {
			querydatagrid : function(){
				datagrid.reLoad();
			}
	};
	
})(jQuery);