$(function(){
	var $addressTable = $('#routeInfo');
	log($addressTable.find('.addressTableGrid').length);
	oc.ui.datagrid({
		selector:$addressTable.find('.routeTableGrid'),
		pagination:false,
//		url:oc.resource.getUrl('demo/user/pageSelect.htm'),
		loadFilter:function(d){return {total:d.length,rows:d};},
		columns:[[
	         {field:'DA',title:'目的地址',width:160},
	         {field:'mask',title:'掩码',width:160},
	         {field:'routeProtocol',title:'路由协议',width:160},
	         {field:'nextHop',title:'下一跳'}
	     ]],
	     data:[
	           {DA:"aaa",mask:"aaa",routeProtocol:"port",nextHop:"userName"}
	          ]
	});
	
	
});