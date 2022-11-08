function WebManageDia(host){
	var webDialogDiv=$("<div style='padding:4px;'/>");
	var webDialog=webDialogDiv.dialog({    
	    title: 'Web管理',    
	    width: 400,    
	    height: 200,    
	    cache: false,    
	    modal: false  ,
	    buttons:[{
			text:'确定',
			handler:function(){
				var url=webDialogDiv.find("#url").val();
				webDialogDiv.dialog('close');
				window.open(url,
						'Web管理',
						'height=400,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
			}
		},{
			text:'取消',
			handler:function(){
				webDialogDiv.dialog('close');
			}
		}]

	}).append('<p>输入设备的Web管理URL，打开Web管理控制台</p>')
	  .append('<label>Web管理台URL：</label><input id="url" type="text" value="http://'+host+'"/>');
};