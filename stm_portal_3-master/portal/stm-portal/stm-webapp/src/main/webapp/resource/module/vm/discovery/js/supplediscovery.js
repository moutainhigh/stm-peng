$(function(){
	var id = oc.util.generateId(), mainDiv = $('#oc_vm_supplediscovery_main').attr('id',id), zTreeObjs = [];
//	mainDiv.css({'margin-left':'20px','margin-right':'20px'});
	// 初始化发现参数表单
	mainDiv.find('.discoveryParamter').css({'height':'122px'});
	var discoveryParamterFormDiv = mainDiv.find("form[name='discoveryParamterForm']").css({'width':'30%','float':'left','margin-top':'10px'});
	oc.ui.form({
		selector : discoveryParamterFormDiv
	});
	var vmResourcesCombobox = oc.ui.combobox({
		selector : discoveryParamterFormDiv.find('[name=vmResources]'),
		//url : oc.resource.getUrl('portal/vm/discoveryVm/suppleDiscoveryVmResources.htm'),
		panelHeight : 'auto',
		editable : false
	});
	oc.util.ajax({
		url : oc.resource.getUrl('portal/vm/discoveryVm/suppleDiscoveryVmResources.htm'),
		success : function(data){
			vmResourcesCombobox.jq.combobox('loadData', data.data);
			if(data.data.length > 0){
				vmResourcesCombobox.jq.combobox('setValue', data.data[0].id);
			}
		}
	});
	// 发现按钮
	var discoveryParamterOperateDiv = mainDiv.find(".discoveryParamterOperate").css({'width':'70%','float':'right','margin-top':'20px'});
	var startDiscoveryBtn = discoveryParamterOperateDiv.find(".startDiscoveryBtn");
	startDiscoveryBtn.linkbutton('RenderLB', {
		iconCls : 'fa fa-eye',
		onClick : function(){
			suppleDiscovery();
		}
	});
	
	// 发现结果
	var discoveryResult = mainDiv.find('.discoveryResult');
	
	// 发现结果树
	var discoveryResultTreeDiv = discoveryResult.find('.discoveryResultTree').css({'height':'298px','width':'100%'});
	var discoveryResultTreeContentDiv = discoveryResultTreeDiv.find('.discoveryResultTreeContent');
	var discoveryResultAccordionDiv = discoveryResult.find(".discoveryResultAccordion");
	// 发现结果汇总
	var discvoeryResultSummaDiv = discoveryResult.find('.discvoeryResultSumma').css({'margin-top':'26px'});
	
	var discoverySummaDiv = discvoeryResultSummaDiv.find('.discoverySumma').css({'height':'32px','margin-top':'10px'});
	
	var joinMonitorDiv = discoveryResult.find('.joinMonitor').css({'margin-top':'10px', 'float':'left'});
	// 完成按钮
	var findishDiscoveryBtn = discoveryResult.find('.findishDiscoveryBtn').css({'float':'right'});
	findishDiscoveryBtn.linkbutton('RenderLB', {
		onClick : function(){
			var instanceIds = [];
			for(var i = 0; i < zTreeObjs.length; i ++){
				var zTreeObj = zTreeObjs[i].zTree;
				var type = zTreeObjs[i].type;
				var nodes = zTreeObj.getCheckedNodes(true);
				for(var j = 0; j < nodes.length; j++){
					if(type == nodes[j].type){
						instanceIds.push(nodes[j].id);
					}
				}
			}
			if(!!joinMonitorDiv.find("input[name='addDefaultMonitor']").attr('checked')){
				if(instanceIds.length > 0){
					oc.util.ajax({
						url : oc.resource.getUrl('portal/vm/discoveryVm/addMoniterSuppleDiscoveryVm.htm'),
						timeout : null,
						data : {instanceIds : instanceIds.join(',')},
						success : function(json){
							// 启用开发发现按钮
							startDiscoveryBtn.linkbutton('RenderLB', {disabled : false});
							discoveryResult.hide();
							alert('加入监控成功');
						}
					});
				}else{
					// 启用开始发现按钮
					startDiscoveryBtn.linkbutton('RenderLB', {disabled : false});
					discoveryResult.hide();
				}
			}else{
				// 启用开始发现按钮
				startDiscoveryBtn.linkbutton('RenderLB', {disabled : false});
				discoveryResult.hide();
			}
		}
	});
	
	// 隐藏发现结果
	discoveryResult.hide();
	
	// 发现
	function suppleDiscovery(){
		if (validateForm(discoveryParamterFormDiv)) {
			// 发现请求后台
			oc.util.ajax({
				url : oc.resource.getUrl("portal/vm/discoveryVm/suppleDiscovery.htm"),
				timeout:null,
				data : {
					instanceId : vmResourcesCombobox.jq.combobox('getValue')
				},
				success : function(json){
					if(json.code == 200){
						if(json.data.isSuccess){
							organizeDiscoveryInfo(json.data);
						}else{
							var content = oc.local.module.resource.discovery['code_' + json.data.errorCode];
							alert('补充发现出错：' + (content == undefined ? '未知错误' : content));
						}
					}else{
						alert('补充发现出错');
					}
				}
			});
		}else{
			discoveryResult.hide();
		}
	}
	// 组织发现信息
	function organizeDiscoveryInfo(data){
		// 显示发现结果
		discoveryResult.show();
		var setting = {
			view: {
				selectedMulti: false,
				showIcon : false
			},
			check : {
				enable : true
			}
		}, levelNo = 0, types = scanAllType(data.vmResourceTree, []), levelNo = scanAllLevel(data.vmResourceTree, 0), zTreeNodes = data.vmResourceTree;
		
		var accordionNode = $("<div/>");
		discoveryResultAccordionDiv.empty().append(accordionNode);
		var accordion = accordionNode.accordion({
			border : false,
			fit : false,
			height : '298px',
			width : '100%'
		});
		var selected = true;
		for(var i = 0; i < types.length; i ++){
			var type = types[i];
			if(type == 'ClusterComputeResource' || type == 'HostSystem' || type == 'VirtualMachine' || type == 'Datastore'
				|| type == 'XenHost' || type == 'XenVM' || type == 'XenSR'
				|| type == 'FusionComputeCluster'|| type == 'FusionComputeHost' || type == 'FusionComputeDataStore' || type == 'FusionComputeVM'
				|| type == 'DTCenterCloudGlobalDashBoard' || type == 'DTCenterECS'
				|| type == 'KyLinServer' || type == 'KyLinVm'
				|| type == 'FusionComputeOnePointThreeCluster'|| type == 'FusionComputeOnePointThreeHost' || type == 'FusionComputeOnePointThreeDataStore' || type == 'FusionComputeOnePointThreeVM'
			){
				var content = $("<div>");
				var title = '虚拟主机';
				switch (type) {
				case 'XenPool':
					title = 'Xen池';
					break;
				case 'XenHost':
					title = 'Xen服务器';
					break;
				case 'XenVM':
					title = 'XenVM';
					break;
				case 'XenSR':
					title = 'Xen存储库';
					break;
				case 'ClusterComputeResource':
					title = 'Cluster';
					break;
				case 'HostSystem':
					title = 'Esxi主机';
					break;
				case 'Datastore':
					title = '数据存储';
					break;
				case 'FusionComputeSit':
				case 'FusionComputeOnePointThreeSit':
					title = 'FC计算池';
					break;
				case 'FusionComputeHost':
				case 'FusionComputeOnePointThreeHost':
					title = 'FC主机';
					break;
				case 'FusionComputeDataStore':
				case 'FusionComputeOnePointThreeDataStore':
					title = 'FC存储';
					break;
				case 'FusionComputeCluster':
				case 'FusionComputeOnePointThreeCluster':
					title = 'FC集群';
					break;
				case 'FusionComputeVM':
				case 'FusionComputeOnePointThreeVM':
					title = 'FC虚拟机';
					break;
				case 'DTCenterCloudGlobalDashBoard':
					title = '阿里云全球云控制面板';
					break;
				case 'DTCenterECS':
					title = 'DTCenter虚拟机';
					break;
				case 'KyLinServer':
					title = '银河麒麟虚拟化服务器';
					break;
				case 'KyLinVm':
					title = '银河麒麟虚拟机';
					break;
				default:
					title = '虚拟主机';
					break;
				}
				accordion.accordion('add', {
					title : title,
					selected : selected,
					content : content
				});
				var zTreeNode = $("<ul>").attr('id', type).addClass("ztree");
				content.append(zTreeNode);
				var zTreeData = $.extend({}, data).vmResourceTree;
				for(var j = 0; j < levelNo; j++){
					zTreeData = modifyNodes(zTreeData, [], type)
				}
				var zTreeObj = $.fn.zTree.init(zTreeNode, setting, zTreeData);
				zTreeObj.expandAll(true);
				zTreeObjs.push({'zTree':zTreeObj,'type':type});
				selected = false;
			}
		}
		
		// 发现时间
		discvoeryResultSummaDiv.find(".discoveryTime").html(data.suppleDiscoveryTime);
		// 禁用开发发现按钮
		startDiscoveryBtn.linkbutton('RenderLB', {disabled : true});
		// 默认为加入监控
		joinMonitorDiv.find("input[name='addDefaultMonitor']").attr('checked', 'checked');
		if(data.tooltips != undefined){
			tooltipsDlg(data);
		}
	}
	function tooltipsDlg(data){
		var repeatDlg = $("<div/>");
		repeatDlg.append($("<div/>").addClass("messager-icon messager-warning"))
			.append($("<div/>").css('top', '25px').addClass("oc-notice").html(data.tooltips));
		var dlg = repeatDlg.dialog({
			title : '提示',
			closable : false,
			width : '280px',
			height : '150px',
			buttons : [{
				text : '&nbsp;关闭&nbsp;',
				handler : function(){
					dlg.dialog('close');
				}
			}]
		});
	}
	function modifyNodes(data, newData, type){
		for(var i = 0; i < data.length; i++){
			var node = $.extend({}, data[i]);
			node.checked = true;
			if(node.type == type){
				node.children = [];
			}
			if(node.children && node.children.length > 0){
				node.children = modifyNodes(node.children, [], type);
				newData.push(node);
			}else{
				if(node.type == type){
					newData.push(node);
				}
			}
		}
		return newData;
	}
	// 总共有多少类型
	function scanAllType(data, types){
		for(var i = 0; i < data.length; i++){
			var node = data[i];
			var contain = false;
			for(var j = 0; j < types.length; j++){
				if(types[j] == node.type){
					contain = true;
					break;
				}
			}
			if(!contain){
				types.push(node.type);
			}
			if(node.children && node.children.length > 0){
				scanAllType(node.children, types);
			}
		}
		return types;
	}
	// 总共有多少层
	function scanAllLevel(data, levelNo){
		for(var i = 0; i < data.length; i++){
			var node = data[i];
			if(node.children && node.children.length > 0){
				var newLevelNo = scanAllLevel(node.children, ++levelNo);
				if(newLevelNo > levelNo){
					levelNo = newLevelNo;
				}
			}
		}
		return levelNo;
	}
	// 输入验证
	function validateForm(formDiv){
		var that = this;
		// 去掉空格
		formDiv.find('input').each(function(){
			if($(this).attr('type') != 'password'){
				$(this).val($(this).val().trim());
			}
		});
		// 验证可见表单项
		var validPassOrNot = true;
		formDiv.find(".form-group:visible [validType],.form-group:visible [required]").each(function(){
			var validBox = $(this);
			if (validBox.is('select')) {
				validPassOrNot = validBox.combobox('isValid');
				if (!validPassOrNot) {
					validBox.combobox().next('span').find('input').focus();
					return false;
				}
			} else {
				validPassOrNot = validBox.validatebox('isValid');
				if (!validPassOrNot) {
					validBox.focus();
					return false;
				}
			}
		});
		return validPassOrNot;
	}
});