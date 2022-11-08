(function($){
	oc.ns('oc.resource.resourcemanagement.profilelist');
	var id=oc.util.generateId();
	var userIdPage,parentCategoryById,childCategoryById;

	//初始化
	function _init(){
		var formDiv=$('#profileList').attr('id',id);
		_initForm(formDiv);
	}
	
	//初始化form
	function _initForm(form){
		
		var dialogDatagridDiv=form.find('.oc-datagrid');
		
		
		return oc.ui.datagrid({
			selector:dialogDatagridDiv,
			//queryForm:queryForm,
			pagination:true,
			queryConditionPrefix:'',
			url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/getProfileByUserID.htm?userID='+userIdPage+'&parentProfileID='+parentCategoryById+'&childProfileID='+childCategoryById),
			columns:[[
//		         {field:'id',title:'id',checkbox:false,sortable:true},
		         {field:'profileNameArray',title:'策略名称', width:100,formatter:urlFormat}
		     ]]
		});

	}
	
	function open(){
		return $('<div/>').dialog({
		    title: '告警查询列表',
		    width : 500,
			height : 450,
		    href: oc.resource.getUrl('resource/module/resource-management/receiveAlarmQuery/profileList.html'),
		    onLoad:function(){
		    	_init();
		    }
		});
	}
	
	/**
	 * 提供给外部引用的接口
	 */
	oc.resource.resourcemanagement.profilelist={
		open:function(userId,parentProfileID,childProfileID){
			userIdPage = userId;
			parentCategoryById = parentProfileID ;
			childCategoryById = childProfileID;
			
			return open();
		}
	};
})(jQuery);

function urlFormat(value,row,rowIndex){
	
	return '<a href="javascript:void(0)" onClick="openUrl('+row.id+')"> '+ value + ' </a>';
	
}
function openUrl(){
	
	
	
}

