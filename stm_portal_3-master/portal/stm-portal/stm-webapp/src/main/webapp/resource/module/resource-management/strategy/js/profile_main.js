(function($){

	var mainId=oc.util.generateId(),
		//模块文档元素根节点，所有模块内的文档元素都必须通过该变量去查找,以提高性能和避免选择冲突
		mainDiv=$('#profile_list_main').attr('id',mainId).panel({
			title:'',
			fit:true,
			isOcAutoWidth:true 
		}),
		//表格查询表单对象
		//queryForm=oc.ui.form({selector:mainDiv.find('.oc-form')}),
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
		datagridDiv=mainDiv.find('.profile_main_list_datagrid'),
		//表格实例
		datagrid=null;

	function gridOpenFormatter(value, rec, index) {    
			return '<a href="javascript:void(0)" onclick="" >O</a>';
	} 
	var datagrid=oc.ui.datagrid({
		selector:datagridDiv,
		pageSize:$.cookie('pageSize_profile_main')==null ? 15 : $.cookie('pageSize_profile_main'),
		url:oc.resource.getUrl('strategy/strategyAll/getDefaultStrategyAll.htm'),
		queryForm:queryForm,
		hideReset : true,
		hideSearch : true,
		octoolbar:{
			left:[queryForm.selector],
			right:[{
				id : 'copyProfileBtn',
				text:'复制',
				iconCls:'icon-copy',
				onClick:function(){
					var id=datagrid.getSelectId();
					if(id == undefined||id == ""){
						alert('请选择一条需要复制的策略数据');
					}else if(datagrid.getSelectIds().length > 1){
						alert('只能选择一条数据复制');
					}else{
						_open('copy',id);
					}
				}
			}]
		},
		columns:[[
		     {field:'id',checkbox:true,sortable:true},
	         {field:'strategyName',title:'策略名称',sortable:true,align:'left',width:'50%',
		    	 formatter:function(value,rowData,rowIndex){ 
		    		 return "<a class='show_profile' id='"+rowData.id+"'>"+'<span title="' + value + '">' + rowData.strategyName + '</span>'+"</a>"; 
        	 }},
	         {field:'strategyType',title:'策略类型',sortable:true,align:'left',width:'49%'}
         ]],
         onLoadSuccess:function(data){ 
        	 $(".show_profile").on('click',function(){
        		 var profileId=$(this).attr('id');
    			 oc.resource.loadScript('resource/module/resource-management/js/default_strategy_detail.js',function(){
    					oc.defaultprofile.strategy.detail.show(profileId);
    			});
        	 });
        	 
         }
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_profile_main');
				$.cookie('pageSize_profile_main',pageSize);
			},
		});
	}
	
	// 新建策略的权限控制
	var user = oc.index.getUser();
	if(!user.domainUser && !user.systemUser){
		$("#copyProfileBtn").hide();
	}
	function _open(type,id){
		oc.resource.loadScript('resource/module/resource-management/strategy/js/profile_main_detail.js',function(){
			oc.module.profile.detail.open({
				type:type,
				id:id,
				saveCall: function(data){
					datagrid.reLoad();
				}
			});
		});
	}
})(jQuery);