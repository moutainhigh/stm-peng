(function($){
	var mainId = oc.util.generateId();
	var mainDiv=$('#custom_strategy_list_main').attr('id',mainId).panel({
		title:'',
		fit:true,
		isOcAutoWidth:true 
	});
	
	var queryForm= oc.ui.form({
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
	var datagridDiv = mainDiv.find('.custom_strategy_list_datagrid');
		
    function gridOpenFormatter(value, rec, index) {    
		return '<a href="javascript:void(0)" onclick="" >O</a>';
    } 
    var datagrid = oc.ui.datagrid({
		selector : datagridDiv,
		pageSize:$.cookie('pageSize_customStrategy')==null ? 15 : $.cookie('pageSize_customStrategy'),
		url : oc.resource.getUrl('strategy/strategyAll/getCustomStrategyAll.htm'),
		queryForm : queryForm,
		hideReset : true,
		hideSearch : true,
		checkOnSelect:false,
		octoolbar:{
			left:[queryForm.selector],
			right:[{
				id : 'createProfileBtn',
				text:'新建',
				iconCls:'fa fa-plus',
				onClick:function(){
					_open('add');
				}
			},'&nbsp;',{
				id : 'delProfileBtn',
				iconCls: 'fa fa-trash-o',
				text:"删除",
				onClick: function(){
					 var nowChecked = datagrid.selector.datagrid('getChecked');
					 if(!nowChecked || nowChecked.length <= 0){
						 alert("请勾选需要删除的策略");
					 }else{
						 var ids = [];
						 for(var i = 0 ; i < nowChecked.length ; i ++){
							 ids[i] = nowChecked[i]['id'];
						 }
			             oc.ui.confirm("是否确认删除策略？",function() {
					         oc.util.ajax({	 
							     url:oc.resource.getUrl('strategy/strategyAll/batchDelCustomStrategy.htm'),			            
							     data:{ids:ids.join()},
							     successMsg:null,
							     success:function(d){
							    	 datagrid.load();
							    	 alert("删除策略成功");
							     }
					         });
			             });
					 }
				}
			}]
		},
		columns:[[
		     {field:'createUser',hidden:true},
		     {field:'updateUser',hidden:true},
		     {field:'id',checkbox:true,sortable:true,isDisabled:function(value,row,index){
	        	 // 非系统管理员且不是创建人则不能勾选策略
	        	var user = oc.index.getUser();
	        	if(!user.systemUser && row.createUser != user.id){
    				return true;
	        	}else{
	        		return false;
	        	}
	         }},
	         {field:'strategyName',title:'策略名称',sortable:true,align:'left',width:'25%',
		    	 formatter:function(value,rowData,rowIndex){ 
		    		 return "<a class='show_profile' id='"+rowData.id+"'>"+'<span title="' + value + '">' + rowData.strategyName + '</span>'+"</a>"; 
		    	 }
		     },
	         {field:'strategyType',title:'策略类型',sortable:true,align:'left',width:'25%'},
	         {field:'domainName',title:'域',sortable:true,align:'left',width:'24%'},
	         {field:'timeline',title:'基线标示',align:'left',width:'25%',
	        	 formatter:function(value,rowData,rowIndex){ 	         
	        		 return "<a class='show_timeline ico ico-mark' id='"+rowData.id+"'></a>";
	        	 }
	         }
         ]],
         onClickCell:function(rowIndex, field, value){
        	 if(field == 'timeline'){
        		 var row = $(this).datagrid('getRows')[rowIndex]; 
        		 var profileId = row.id;
        		 var createUserId = row.createUser;
    			oc.resource.loadScript('resource/module/resource-management/timeline/js/profileTimeline.js',function(){
      				oc.profiletimeline.detail.show(profileId);
    			}); 
        	 }
        	 
        	 if(field == 'strategyName'){
        		 var row = $(this).datagrid('getRows')[rowIndex]; 
        		 var profileId = row.id;
        		 oc.resource.loadScript('resource/module/resource-management/js/custom_strategy_detail.js',function(){
 					oc.customprofile.strategy.detail.show(profileId);
        		 });
        	 }
         }

	});
    
    //cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_customStrategy');
				$.cookie('pageSize_customStrategy',pageSize);
			}
		});
	}
    
	// 新建和删除策略的权限控制
	var user = oc.index.getUser();
	if(!user.domainUser && !user.systemUser){
		$("#createProfileBtn").hide();
		$("#delProfileBtn").hide();
	}
	
	function _open(type){
		var id=undefined;
		oc.resource.loadScript('resource/module/resource-management/strategy/js/customStrategyDetail.js',function(){
			oc.module.custom.detail.open({
				type:type,
				id:id,
				saveCall: function(data){
					datagrid.reLoad();
				}
			});
		});
	}
  
	// 命名空间
	oc.ns('oc.module.custom.strategy');
	oc.module.custom.strategy = {
			querydatagrid : function(){
				datagrid.reLoad();
			}
	};
	
})(jQuery);