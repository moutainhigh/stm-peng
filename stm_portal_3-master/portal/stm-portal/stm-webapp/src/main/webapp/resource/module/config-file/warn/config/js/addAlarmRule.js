(function($){
	oc.ns('oc.module.config.warn.config.alarm');
	var id=oc.util.generateId(),beforeDatagrid,addAlarmUserDatagrid;
	var formDiv=$('#addAlarmUser').attr('id',id);
	//初始化
	function _init(dlg){
		_initForm(formDiv,dlg);
	}
	//初始化form
	function _initForm(form,dlg){
		var addAlarmUserDatagridDiv=$('#addAlarmUserDatagrid');
		addAlarmUserDatagrid = oc.ui.datagrid({
			selector:addAlarmUserDatagridDiv,
			url:oc.resource.getUrl('portal/resource/rceceiveAlarmQuery/getUser.htm?domainType=3&domainId=0'),
			columns:[[
		         {field:'id',title:'id',checkbox:true},
		         {field:'name',title:'用户',sortable:true,align:'center',width:100}
		     ]],
		     loadFilter:function(data){
		    	 var result = data.data,arr=[],count=0;
		    	 var selectedDatas = beforeDatagrid.selector.datagrid('getData');
		    	 for(var k=0;k<result.rows.length;k++){
		    		var flag = true;
		    		if(selectedDatas.datas){
			    		for(var m=0;m<selectedDatas.rows.length;m++){
			    			if(result.rows[k].id==selectedDatas.rows[m].userId){
			    				flag = false;
			    				count++;
			    				break;
			    			}
			    		}
		    		}
		    		if(flag){
		    			arr.push(result.rows[k]);
		    		}
		    	 }
		    	 result.rows = arr;
		    	 result.totalRecord = result.totalRecord-count;
		    	 return result;
		     }
		});
		return true;
	}
	
	function open(){
		var dlg = $('<div/>');
		dlg.dialog({
		    title: '添加用户',
		    width : 450,
			height : 450,
		    href: oc.resource.getUrl('resource/module/config-file/warn/config/addAlarmRule.html'),
		    onLoad:function(){
		    	_init(dlg);
		    	$('div.pagination-info').remove();
		    },
		    onClose:function(){
		    	dlg.dialog("destroy");
		    },
			buttons:[{
		    	text: '添加',
		    	handler:function(){
		    		var userSelected = addAlarmUserDatagrid.selector.datagrid('getSelections');
					if(null == userSelected || 0==userSelected.length){
						alert('请选择一个用户！');
					}else{
						for(var i=0;i<userSelected.length;i++){
							var item = beforeDatagrid.selector.datagrid('getRows'),flag=false;
							for(var j=0;j<item.length;j++){
								if(item[j].userId==userSelected[i].id){
									flag = true;
									break;
								}
							}
							if(flag) continue;
							beforeDatagrid.selector.datagrid("appendRow",{
								userId:userSelected[i].id,
								userName:userSelected[i].name
							});
						}
						dlg.dialog('close');
					}
		    	}
		    },{
		    	text: '取消',
		    	handler:function(){
		    		dlg.dialog('close');
		    	}
		    }]
		});
		
		return true;
	}
	
	/**
	 * 提供给外部引用的接口
	 */
	oc.module.config.warn.config.alarm={
		open:function(datagrid){
			beforeDatagrid = datagrid;
			return open();
		}
	};
})(jQuery);


