$(function() {
	var id = oc.util.generateId(), 
		indexDiv = $('#oc_netflow_index').attr('id',	id);
		indexDiv.layout().find('.easyui-accordion').accordion();
	var centerDiv = indexDiv.layout('panel', 'center');

	function _click(href, data, e) {
		centerDiv.children().remove();
		$('<div style="width:100%;height:100%;"></div>').appendTo(centerDiv)
				.panel('RenderP', {
					title : data.hideTitle ? undefined : data.name,
					href : href
				});
	}

	var flowGeneral = oc.ui	.navsublist({
				selector : $('.flowGeneral:first'),
				data : [
						{
							hideTitle:true,
							name : '设备',
							href : oc.resource.getUrl('resource/module/netflow/device-netflow/device_netflow_v2.html')
						},
						{
							hideTitle:true,
							name : '接口',
							href : oc.resource.getUrl('resource/module/netflow/interface-netflow/interface_netflow_v2.html')
						},
						{
							hideTitle:true,
							name : '终端',
							href : oc.resource.getUrl('resource/module/netflow/terminal-netflow/terminal_netflow_v2.html')
						},
						{
							hideTitle:true,
							name : '会话',
							href : oc.resource.getUrl('resource/module/netflow/session-netflow/session_netflow_v2.html')
						},
						{
							hideTitle:true,
							name : '应用',
							href : oc.resource.getUrl('resource/module/netflow/application-netflow/application_netflow_v2.html')
						},
						{
							hideTitle:true,
							name : '设备组',
							href : oc.resource.getUrl('resource/module/netflow/device-group-netflow/devicegroup_netflow_v2.html')
						},
						{
							hideTitle:true,
							name : '接口组',
							href : oc.resource.getUrl('resource/module/netflow/ifgroup-netflow/interfacegroup_netflow_v2.html')
						},
						{
							hideTitle:true,
							name : 'IP分组',
							href : oc.resource.getUrl('resource/module/netflow/ipgroup-netflow/ipgroup_netflow_v2.html')
						} ],
				click : _click
			});
	flowGeneral.get(0).trigger('click');

	oc.ui
			.navsublist({
				selector : $('.sysConf:first'),
				data : [
						{
							name : '设备管理',
							href : oc.resource
									.getUrl('resource/module/netflow/device/device_index.html')
						},
						{
							name : '设备组管理',
							href : oc.resource
									.getUrl('resource/module/netflow/deviceGroup/list.html')
						},
						{
							name : '接口分组管理',
							href : oc.resource
									.getUrl('resource/module/netflow/interfaceGroup/list.html')
						},
						{
							name : 'IP分组管理',
							href : oc.resource
									.getUrl('resource/module/netflow/ipGroup/list.html')
						},
						{
							name : '应用管理',
							href : oc.resource
									.getUrl('resource/module/netflow/applicationManager/list.html')
						},
						{
							name : '采集服务器配置',
							href : oc.resource
									.getUrl('resource/module/netflow/servicePort/get.html')
						},{
							name: '告警设置',
							href: oc.resource.getUrl('resource/module/netflow/alarm/alarm_list.html')
						} ],
				click : _click
			});

});
