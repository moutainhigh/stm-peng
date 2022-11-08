$(function(){
	var id = oc.util.generateId();
	var mainDiv = $("#oc_camera_discovery_main").attr('id', id);
	var discoveryParameterFormDiv = mainDiv.find("form[name='discoveryParameterForm']");
	discoveryParameterFormDiv.css({'width':'90%','float':'left'});
	var paramArray = new Array();
	var stayConnFunction = undefined;
	var uncheckIds = new Array();
	var discoveryIds = new Array();
	oc.util.ajax({
		url: oc.resource.getUrl('portal/resource/discoverResource/getpluginInitParameter.htm'),
		data: { resourceId: 'EastWitPlatform'},
		async: false,
		success: function(data){
			if(data.code == 200){
				var JdbcPlugin = data.data.JdbcPlugin;
				JdbcPlugin = sortParamNodeArray(JdbcPlugin);
				for(var i = 0; i < JdbcPlugin.length; i++){
					var plugin = JdbcPlugin[i];
					if(plugin.edit == false){
						continue;
					}
					var formGroup = $('<div/>').attr('class', 'form-group');
					formGroup.append('<label>'+ plugin.name +'： </label> ');
					var formDiv = $('<div/>');
					var childDiv = undefined;
					var param = new Object();
					param.id = plugin.id;
					switch(plugin.boxStyle){
						case 'IPField':	
						case 'Input':
							childDiv = $('<input name="'+ plugin.id +'" required="required"/>');
							if(plugin.password == true){
								childDiv.attr('type', 'password');
							} else {
								childDiv.attr('type', 'text');
							}
							if(plugin.boxStyle == 'IPField'){
								childDiv.attr('validType', 'ip');
							}
							if(plugin.defaultValue != null && plugin.defaultValue != ""){
								childDiv.attr('value', plugin.defaultValue);
							}
							param.type = 'input';
							param.obj = childDiv;
							break;
						case 'OptionBox':
							childDiv = $('<select name="'+ plugin.id +'" required="required"></select>');
							childDiv.data('supportValues', plugin.supportValues);
							if(plugin.defaultValue != null && plugin.defaultValue != ""){
								childDiv.data('value', plugin.defaultValue);
							}							
							param.type = 'combobox';
							param.obj = childDiv;
							break;
						case 'RadioBox':
							break;
					}
					if(plugin.valueValidate != null && plugin.valueValidate != "" && plugin.valueValidate != undefined){
						var errorInfo = plugin.errorInfo == null || plugin.errorInfo == undefined ? '格式错误': plugin.errorInfo;
						childDiv.attr('validType', "reg["+ plugin.valueValidate +", '"+ errorInfo +"']");	
					}
					if(plugin.display == false){
						formGroup.hide();
					}
					formDiv.append(childDiv);
					formGroup.append(formDiv);
					discoveryParameterFormDiv.append(formGroup);
					paramArray.push(param);
				}
			} else {
				alert('获取发现参数错误.');	
			}
		}
	});
	oc.ui.form({
		selector: discoveryParameterFormDiv
	});
	var dcsCombobox = oc.ui.combobox({
		selector: discoveryParameterFormDiv.find('[name=DCS]'),
		panelHeight: 'auto',
		editable: false
	});
	var domainCombobox = oc.ui.combobox({
		selector: discoveryParameterFormDiv.find('[name=domainId]'),
		placeholder: false,
		editable: false,
		data: oc.index.getDomains(),
		onChange: function(newValue, oldValue){
			var array = new Array();
			var domains = oc.index.getDomains();
			var domain = undefined;
			for(var i = 0; i < domains.length; i++){
				if(newValue == domains[i].id){
					domain = domains[i];
					break;
				}
			}
			if(!!domain){
				for(var index in domain.dcsNodes){
					var dcs = domain.dcsNodes[index];
					var dcsObj = {};
					dcsObj.id = dcs.groupId;
					dcsObj.name = dcs.name;
					array[array.length] = dcsObj;
				}
			}
			dcsCombobox.jq.combobox('loadData', array);
			if(array.length > 0){
				dcsCombobox.jq.combobox('setValue', array[0].id);
			} else {
				dcsCombobox.jq.combobox('setValue', '');
			}
		}
	});
	makeCombobox(paramArray);
//-------------------------- 分界线 --------------------------//
	var operateDiv = mainDiv.find(".discoveryParameterOperate");
	operateDiv.css({
		'width':'10%',
		'float':'right',
		'margin-top':'76px',
	});
	var startDiscoveryBtn = operateDiv.find(".startDiscoveryBtn");
	startDiscoveryBtn.linkbutton('RenderLB', {
		iconCls : 'fa fa-eye',
		onClick : function(){
			discoveryCamera();
		}
	});
	var stopDiscoveryBtn = operateDiv.find(".stopDiscoveryBtn").hide();
	stopDiscoveryBtn.linkbutton('RenderLB', {
		iconCls : 'fa fa-times-circle'
	});
	// 发现结果
	var discoveryResult = mainDiv.find(".discoveryResult");
	var discoveryResultTreeDiv = discoveryResult.find('.discoveryResultTree').css({'height':'350px','width':'100%'});
	var discoveryResultTableDiv = discoveryResultTreeDiv.find(".discoveryResultTable");
	var datagridResult = oc.ui.datagrid({
		selector: discoveryResultTableDiv,
		title: '发现结果',
		pagination: true,
		fit: true,
		columns: [ [
			{ field: 'id', checkbox: true },
			{ field: 'name', title: '名称' },
//			{ field: 'gisX', title: '经度' },
//			{ field: 'gisY', title: '纬度' }
		] ],
		onUncheck: function(index, row){
			uncheckIds.push(row.id);
		},
		onCheck: function(index, row){
			if(uncheckIds.includes(row.id)) {
				var i = uncheckIds.indexOf(row.id);
				uncheckIds.splice(i, 1);
			}
		}
	});
	
	// 发现结果汇总
	var discvoeryResultSummaDiv = discoveryResult.find('.discoveryResultSumma').css({'margin-top':'10px'});	
	var joinMonitorDiv = discoveryResult.find('.joinMonitor').css({'margin-top':'10px', 'float':'left'});
	// 完成按钮
	var finishDiscoveyBtn = discoveryResult.find('.finishDiscoveryBtn').css({'float':'right'});
	
	finishDiscoveyBtn.linkbutton('RenderLB', {
		onClick: function(){
			var instanceIds = new Array();
			for(var i = 0; i < discoveryIds.length; i++) {
				if(!uncheckIds.includes(discoveryIds[i])) {
					instanceIds.push(discoveryIds[i]);
				}
			}
			discoveryIds = [], uncheckIds = [];
			if(!!joinMonitorDiv.find("input[name='addDefaultMonitor']").prop('checked')){
				if(instanceIds.length > 0){
					oc.util.ajax({
						url : oc.resource.getUrl('portal/resource/cameradiscover/addCameraMonitor.htm'),
						timeout : null,
						data : {instanceIds : instanceIds.join(',')},
						success : function(json){
							// 启用开发发现按钮
							startDiscoveryBtn.linkbutton('RenderLB', {disabled : false});
							discoveryResult.hide();
							alert('加入监控成功');
						}
					});
				} else {
					startDiscoveryBtn.linkbutton('RenderLB', {disabled : false});
					discoveryResult.hide();
				}
			} else {
				startDiscoveryBtn.linkbutton('RenderLB', {disabled : false});
				discoveryResult.hide();
			}
		}
	});
	// 隐藏发现结果
	discoveryResult.hide();
//-------------------------- 分界线 --------------------------//
	function getSubmitData(paramArray){
		var data = new Object();
		data.resourceId = 'EastWitPlatform';
		data.domainId = domainCombobox.jq.combobox('getValue');
		data.DCS = dcsCombobox.jq.combobox('getValue');
		for(var i = 0; i < paramArray.length; i++) {
			var tmp = paramArray[i];
			if(tmp.type == 'input'){
				data[""+tmp.id] = tmp.obj.val();	
			} else if(tmp.type == 'combobox'){
				data[""+tmp.id] = tmp.obj.jq.combobox('getValue');
			}
		}
		return data;
	}
	
	function makeCombobox(paramArray) {
		for(var i = 0; i < paramArray.length; i++){
			if(paramArray[i].type != 'combobox'){
				continue;
			}
			var tmp = paramArray[i].obj;
			var comboboxObj = oc.ui.combobox({
				selector: tmp,
				panelHeight: 'auto',
				placeholder: false,
				editable: false,
				valueField:'value',
				textField:'name',
				data: tmp.data('supportValues'),
			});
			comboboxObj.jq.combobox('initValue',tmp.data('value'));
			paramArray[i].obj = comboboxObj;
		}
	}
	
	function sortParamNodeArray(param) {
		for(var i = 0; i < param.length; i++){
			for(var j = i + 1; j < param.length; j++){
				var b = param[j], tmp;
				if(parseInt(param[i].displayOrder) > parseInt(b.displayOrder)){
					tmp = param[i];
					param[i] = param[j];
					param[j] = tmp;
				}
			}
		}
		return param;		
	}
	
	function discoveryCamera(){
		if(validateForm(discoveryParameterFormDiv)){
			stayConnFunction = window.setInterval(stayConnnection, 5 * 60 * 1000);
			oc.util.ajax({
				url: oc.resource.getUrl("portal/resource/cameradiscover/discoverCameraResource.htm"),
				timeout: null,
				data: { paramJson : getSubmitData(paramArray) },
				success: function(json){
					if(json.code == 200){
						if(json.data.isSuccess){
							organizeDiscoveryInfo(json.data);
						} else {
							if(!!json.data.failMessage){
								var dialogDiv = $("<div/>");
								dialogDiv.css({ 'padding-left': '10px' });
								dialogDiv.html("<br/>" + json.data.failMessage + "<br/>");
								var dcsDlg = dialogDiv.dialog({
									title : '提示',
									closable : false,
									width : '280px',
									height : '120px',
									buttons : [{
										text : '&nbsp;关闭&nbsp;',
										handler : function(){
											dcsDlg.dialog('close');
										}
									}]
								});
							} else {
								var content = oc.local.module.resource.discovery['code_' + json.data.errorCode];
								alert('视频设备发现出错：' + (content == undefined ? '未知错误' : content));
							}
						}
					} else {
						alert('视频设备发现出错');
					}
					window.clearInterval(stayConnFunction);
				},
				error: function() {
					window.clearInterval(stauConnFunction);
					alert('视频设备发现出错');
				}
			});
		} else {
			discoveryResult.hide();
		}
	}
	
	function organizeDiscoveryInfo(data){
		discoveryResult.show();
		for(var i = 0; i < data.rows.length; i++){
			discoveryIds.push(data.rows[i].id);
		}
		datagridResult.selector.datagrid('loadData', data.rows.slice(0,15));
		var resultPager = datagridResult.selector.datagrid("getPager");
		resultPager.pagination({
			total: data.rows.length,
			onSelectPage: function(pageNo, pageSize) {
				var start = (pageNo - 1) * pageSize;
				var end = start + pageSize;
				datagridResult.selector.datagrid('loadData', data.rows.slice(start, end));
				resultPager.pagination('refresh', {
					total: data.rows.length,
					pageNumber: pageNo
				});
				var rows = datagridResult.selector.datagrid('getRows');
				for(var j = 0; j < rows.length; j++) {
					if(!uncheckIds.includes(rows[j].id)) {
						datagridResult.selector.datagrid('checkRow', j);
					}
				}
			}
		});
		datagridResult.selector.datagrid('checkAll');
		discvoeryResultSummaDiv.find(".discoverTime").text(data.discoverTime);
		discoveryResultTreeDiv.css('height', (discoveryResultTreeDiv.height() + 10) + 'px');
		startDiscoveryBtn.linkbutton('RenderLB', { disabled: true });
		joinMonitorDiv.find("input[name='addDefaultMonitor']").attr('checked', 'checked');
		if(data.tooltips != undefined){
			tooltipsDlg(data);
		}
	}
	
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
	
	// 用于使发现过程中session不失效
	function stayConnnection() {
		oc.util.ajax({
			url: oc.resource.getUrl('portal/resource/cameradiscover/checkFinishDiscover.htm'),
			startProgress: null,
			stopProgress: null,
			timeout: null,
			success: function(data) {
			}			
		});
	}
	
});
