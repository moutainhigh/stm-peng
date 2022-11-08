$(function(){
	var $addressTable = $('#addressInfo');
	log($addressTable.find('.addressTableGrid').length);
	oc.ui.datagrid({
		selector:$addressTable.find('.addressTableGrid'),
		pagination:false,
//		url:oc.resource.getUrl('demo/user/pageSelect.htm'),
		loadFilter:function(d){return {total:d.length,rows:d};},
		columns:[[
	         {field:'serial',title:'序号',width:50},
	         {field:'IPAddress',title:'IP地址',width:200},
	         {field:'netMask',title:'子网掩码',width:200},
	         {field:'interfaceInx',title:'接口索引',width:200}
	     ]],
	     data:[
	           {serial:"aaa",IPAddress:"aaa",netMask:"port",interfaceInx:"userName"}
	          ]
	});
	
	
});