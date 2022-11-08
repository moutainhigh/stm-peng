$(function(){
	var $addressTable = $('#ARPTable');
	oc.ui.datagrid({
		selector:$addressTable.find('.ARPTableGrid'),
		pagination:false,
		url:oc.resource.getUrl('resource/module/topo/test/ARPTable.json'),
		loadFilter:function(d){return {total:d.length,rows:d};},
		columns:[[
	         {field:'IP',title:'IP地址',width:200},
	         {field:'MAC',title:'MAC地址',width:200},
	         {field:'interface',title:'接口名称',width:200}
	     ]]
	});
});