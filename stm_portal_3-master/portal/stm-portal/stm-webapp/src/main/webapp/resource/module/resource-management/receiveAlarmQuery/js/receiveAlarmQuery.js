$(function() {
	var div=$('#receiveAlarmQuery').attr('id',oc.util.generateId());
	var datagridDiv=div.find('.oc-datagrid');
	var	form=div.find("form[name='receiveAlarmQueryBar']");
	//表格查询表单对象
	queryForm=oc.ui.form({selector:form});
	
	var datagrid = oc.ui.datagrid({
		selector:datagridDiv,
		pageSize:$.cookie('pageSize_receiveAlarmQuery')==null ? 15 : $.cookie('pageSize_receiveAlarmQuery'),
		url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/userProfileQuery.htm'),  
		queryForm:queryForm,
		queryConditionPrefix:'',
		octoolbar:{
			left:[queryForm.selector]
		},
		columns:[[
//	         {field:'id',title:'id',checkbox:false},
	         {field:'userName',title:'用户',width:25},
	         {field:'profileNameArray',title:'策略名称',width:75,formatter:openProfileList}
	     ]]
	});
	
	//cookie记录pagesize
	var paginationObject = datagridDiv.datagrid('getPager');
	if(paginationObject){
		paginationObject.pagination({
			onChangePageSize:function(pageSize){
				var before_cookie = $.cookie('pageSize_receiveAlarmQuery');
				$.cookie('pageSize_receiveAlarmQuery',pageSize);
			}
		});
	}
	
	var myForm = oc.ui.form({
		selector:form,
		combobox:[{
			selector:'[name=parentProfileID]',
			filter:function(data){
				//alert('call back--'+data.data.users[0].name);
				return data.data;
			},
			url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/getParentProfile.htm'),
			onChange : function(id){
				form.find('[name=childProfileID]').val("");
				if(id){
					myForm.ocui[1].reLoad(oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/getChildProfile.htm?profileID='+id));
				}
				datagrid.reLoad();
			}
		},{
			selector:'[name=childProfileID]',
			filter:function(data){
				return data.data;
			},
			onChange : function(id){
				datagrid.reLoad();
			}
		}]
	});
	
});

function openProfileList(value,row,rowIndex){
	
	if(value.split(',').length>4){
		return '<a href="javascript:void(0)" onClick="openDialog('+row.userID+')"> '+ value + ' </a>';
	}else{
		return '<a href="javascript:void(0)" > '+ value + ' </a>';
	}
	
}

function openDialog(userId){
	var parentProfileID=$('#parentProfileID').combobox('getValue');
	var childProfileID=$('#childProfileID').combobox('getValue');
//	alert('row.userID='+userId+','+parentProfileID+','+childProfileID);
	oc.resource.loadScript('resource/module/resource-management/receiveAlarmQuery/js/profileList.js',function(){
		oc.resource.resourcemanagement.profilelist.open(userId,parentProfileID,childProfileID);
	});
}