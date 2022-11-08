$(function(){
	function discoveryList(){
		
	}
	var uncheckIds = new Array();
	var discoveryIds = new Array();
	var stayConnFunction = undefined;
	discoveryList.prototype = {
		constructor: discoveryList,
		datagridDom: undefined,
		paramArray: undefined,
		init: function(){
			var domId = oc.util.generateId();
			this.datagridDom = $(".discoveryList").attr("id", domId);
			this.createDatagrid();			
		},
		createDatagrid: function(){
			var that = this;
			var datagrid = oc.ui.datagrid({
				selector: that.datagridDom,
				url: oc.resource.getUrl('portal/resource/cameradiscover/getDiscoveryList.htm'),
				hideReset: true,
				hideSearch: true,
				columns:[ [
					{ field: 'id', hidden: true },
					{ field: 'ip', title: 'Ip地址', align: 'left', width: '16%' },
					{ field: 'dbType', title: '数据库类型', align: 'left', width: '16%' },
					{ field: 'dbName', title: '数据库名', align: 'left', width: '16%' },
					{ field: 'jdbcPort', title: '端口', align: 'left', width: '16%' },
					{ field: 'domainName', title: '所属域', align: 'left', width: '16%' },
					{ 
					  field: 'discoverNode',
					  title: '操作',
					  align: 'center',
					  width: '10%',
					  formatter: function(value, row, rowIndex){
					  	return '<a rowIndex="'+rowIndex+'" class="icon-edit quickSelectDiscoverParamter"></a>';
					  }
					}
				] ],
				onLoadSuccess: function(data){
					// 使数据表格列宽自适应
					that.datagridDom.datagrid('autoSizeColumn');	
					var view = that.datagridDom.parent().find('div[class=datagrid-view2]');
					var optionBtn = view.find('div.datagrid-body').find('td[field=discoverNode]').find('a.icon-edit');
					optionBtn.each(function(){
						$(this).click(function(){
							var rowIndex = $(this).attr('rowIndex');
							var row = that.datagridDom.datagrid('getRows')[rowIndex];
							that.openDiscoverParaDialog(row);
						});
					});
				}
			});
		},
		openDiscoverParaDialog: function(row){
			var that = this;
			var formDom = that.createFormDom(row.resourceId);
			var dialog = $("<div/>").append(formDom).dialog({
				title: '编辑属性信息',
				width: '460px',
				height: '430px',
				buttons: [{
					text: '连接测试',
					handler: function(){
						that.testDiscover(formDom, row, dialog);
					}
				}, {
					text: '保存发现信息',
					handler: function(){
						that.updateDiscoverPara(formDom, row, dialog);
					}
				}, {
					text: '重新发现',
					handler: function(){
						that.reDiscover(formDom, row, dialog);
					}
				}]
			});
			oc.ui.form({ selector: formDom });
			that.makeComboboxFillData(row);
			
			var ifEnableRefresh = row['ifAutoRefresh'];
			var checkBoxObj = formDom.find("input[name='ifAutoRefresh']").attr('checked',ifEnableRefresh);
			checkBoxObj.on('click',function(){
				var tempObj = $(this);
				if(tempObj.prop('checked')){
					alert('如果对资源进行自动刷新操作，新增的资源会自动加入监控，<br>消失的资源系统会自动删除信息!');
					formDom.find("[name='autoRefreshCycleDayDiv']").show();
					formDom.find("[name='autoRefreshCycleDayLabel']").show();
				}else{
					formDom.find("[name='autoRefreshCycleDayDiv']").hide();
					formDom.find("[name='autoRefreshCycleDayLabel']").hide();
				}
			})
			if(ifEnableRefresh){
				formDom.find("[name='autoRefreshCycleDayDiv']").show();
				formDom.find("[name='autoRefreshCycleDayLabel']").show();
			}
			var autoRefreshCycleDay = oc.ui.combobox({
				selector : formDom.find("[name='autoRefreshCycleDay']"),
				placeholder : false,
				readonly : false,//!ifEnableRefresh,
				data : [
					{id:'1',name:'1天'},
					{id:'7',name:'7天'},
				    {id:'15',name:'15天'},
				    {id:'30',name:'30天'}
				]
			});
			autoRefreshCycleDay.jq.combobox('setValue', 7);
			if(row.autoRefreshCycleDay && row.autoRefreshCycleDay>0){
				autoRefreshCycleDay.jq.combobox('setValue', row.autoRefreshCycleDay);
			}
		},
		createFormDom: function(resourceId){
			var that = this;
			var formDom = $("<form class='oc-form col1' name='discoverParaForm'></form>");
			var fieldsetParameter = $("<fieldset class='singdiscparamfieldset'>" +
					"<legend>连接信息<span class='telSSHBtn'></span></legend>" +
					"</fieldset>");
			var paramArray = new Array();
			oc.util.ajax({
				url: oc.resource.getUrl('portal/resource/discoverResource/getpluginInitParameter.htm'),
				data: { resourceId: resourceId},
				async: false,
				success: function(data){
					if(data.code == 200){
						var JdbcPlugin = data.data.JdbcPlugin;
						var domainId = { boxStyle: 'OptionBox', display: true, edit: true, displayOrder: '0', id: 'domainId', name: '域' };
						var DCS = { boxStyle: 'OptionBox', display: true, edit: true, displayOrder: '0', id: 'DCS', name: 'DCS' };
						JdbcPlugin.push(domainId);
						JdbcPlugin.push(DCS);
						JdbcPlugin = sortParamNodeArray(JdbcPlugin);
						for(var i = 0; i < JdbcPlugin.length; i++){
							var plugin = JdbcPlugin[i];
							if(plugin.edit == false){
								continue;
							}
							var formGroup = $('<div/>').attr('class', 'form-group');
							formGroup.append('<label>'+ plugin.name +'： </label> ');
							var formDiv = $('<div/>').attr('class', 'inputBox');
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
							fieldsetParameter.append(formGroup);
							paramArray.push(param);
						}
					} else {
						alert('获取发现参数错误.');
					}
				}
			});
//			var fieldsetAutoRefresh = $("<fieldset class='singdiscparamfieldset'></fieldset>");
//			var formGroupDom = $("<div class='form-group'><input name='ifAutoRefresh' type='checkbox'>自动刷新&nbsp;&nbsp;&nbsp;&nbsp;<label style='display:none;'  name='autoRefreshCycleDayLabel' >刷新周期：</label>" +
//					"<div name='autoRefreshCycleDayDiv' style='display:none;' class='inputBox'><select name='autoRefreshCycleDay'></select></div></div>");
//			fieldsetAutoRefresh.append(formGroupDom);
			
			formDom.append(fieldsetParameter);
//			formDom.append(fieldsetAutoRefresh);
			
			that.paramArray = paramArray;
			return formDom;
		},
		makeComboboxFillData: function(rowData){
			var that = this;
			var paramArray = that.paramArray;
			for(var i = 0; i < paramArray.length; i++){
				var tmp = paramArray[i].obj;
				var data = rowData["" + paramArray[i].id];
				if(paramArray[i].type != 'combobox'){
					if(paramArray[i].id == 'IP'){
						tmp.val(rowData['ip']);
					} else{
						tmp.val(data);
					}
					continue;
				}
				var comboboxObj = undefined;
				if(paramArray[i].id == 'domainId'){
					comboboxObj = oc.ui.combobox({
						selector: tmp,
						placeholder : false,
						readonly : false,
						data: oc.index.getDomains(),
					});
				} else if(paramArray[i].id == 'DCS'){
					comboboxObj = oc.ui.combobox({
						selector: tmp,
						placeholder : false,
						readonly : true,
						data: [{ id: rowData.discoverNode, name: rowData.discoverNodeName }],
					});
				} else {
					comboboxObj = oc.ui.combobox({
						selector: tmp,
						panelHeight: 'auto',
						placeholder: false,
						editable: false,
						valueField:'value',
						textField:'name',
						data: tmp.data('supportValues'),
					});	
				}
				if(data && data != undefined){
					comboboxObj.jq.combobox('setValue', data);
				}
				paramArray[i].obj = comboboxObj;
			}
			that.paramArray = paramArray;
		},
		testDiscover: function(formDiv, row, dialog){
			var that = this;
			var flag = validateForm(formDiv);
			if(flag){
				var dataJson = that.constructDiscoverParamter(formDiv, row.resourceId);
				oc.util.ajax({
					url : oc.resource.getUrl("portal/resource/cameradiscover/testDiscover.htm"),
					data : { "paramData":dataJson, "instanceId":row.id },
					timeout : null,
					success : function(json){
						if(json.code == 200){
							if(json.data == 1){
								alert('测试连接成功');
							}else if(json.data == 0){
								alert('测试连接失败');
							}else{
								var content = oc.local.module.resource.discovery['code_' + json.data];
								alert('测试连接失败，' + (content == undefined ? ('失败代码:' + json.data) : content));
							}
						}
					}
				});
			}
		},
		updateDiscoverPara : function(formDiv, row, dialog){
			var that = this;
			var flag = validateForm(formDiv);
			if(flag){
				var dataJson = that.constructDiscoverParamter(formDiv, row.resourceId);
				oc.util.ajax({
					url : oc.resource.getUrl("portal/resource/cameradiscover/updateDiscoverParam.htm"),
					data : { "paramData":dataJson, "instanceId":row.id },
					timeout : null,
					success : function(json){
						if(json.code == 200){
							if(json.data == 1){
								alert('更新成功');
								dialog.dialog('destroy');
								that.datagridDom.datagrid('reload');
							}else if(json.data == 2){
								alert('发现参数不正确');
							}else{
								alert('更新失败');
							}
						}
					}
				});
			}
		},
		reDiscover : function(formDiv, row, dialog){
			var that = this;
			var dataJson = that.constructDiscoverParamter(formDiv, row.resourceId);
			dataJson = eval('(' + dataJson + ')');
			// 发现请求后台
			stayConnFunction = window.setInterval(stayConnFunction, 5 * 60 * 1000);
			oc.util.ajax({
				url : oc.resource.getUrl("portal/resource/cameradiscover/reDiscover.htm"),
				timeout : null,
				data : {
					"paramData" : dataJson,
                    "instanceId" : row.id,
				},
				success : function(json){
					if(json.code == 200){
						if(json.data.isSuccess){
							that.datagridDom.datagrid('reload');
							dialog.dialog('destroy');
							that.organizeDiscoveryInfo(json.data);
						}else{
							var content = oc.local.module.resource.discovery['code_' + json.data.errorCode];
							alert('视频重新发现出错：' + (content == undefined ? '未知错误' : content));
						}
					}else{
						alert('视频重新发现出错');
					}
					window.clearInterval(stayConnFunction);
				},
				error: function() {
					window.clearInterval(stayConnFunction);
					alert('视频重新发现出错');
				}
			});
		},
        organizeDiscoveryInfo: function(data){
            var that = this;
            var dialog = $('<div/>');
			dialog.dialog({
                title: '发现结果',
                width: 650,
                height: 450,
                buttons: [{
                    text: '确定',
                    handler: function(){
                        var instanceIds = [];
                        for(var i = 0; i < discoveryIds.length; i++) {
            				if(!uncheckIds.includes(discoveryIds[i])) {
            					instanceIds.push(discoveryIds[i]);
            				}
            			}
                        discoveryIds = [], uncheckIds = [];
                        if(instanceIds.length > 0){
								oc.util.ajax({
									url : oc.resource.getUrl('portal/resource/cameradiscover/addCameraMonitor.htm'),
									timeout : null,
									data : {instanceIds : instanceIds.join(',')},
									success : function(json){
										dialog.dialog('destroy');
										alert('加入监控成功');
									}
								});
						} else {
							dialog.dialog('destroy');
						}
                    }
                }],
            });
            var gridDiv = $('<div/>');
			dialog.append(gridDiv);
            var datagridResult = oc.ui.datagrid({
				selector: gridDiv,
				pagination: true,
				fit: false,
				fitColumns: true,
				height: 350,
				columns: [ [
					{ field: 'id', checkbox: true },
					{ field: 'name', title: '名称' },
//					{ field: 'gisX', title: '经度' },
//					{ field: 'gisY', title: '纬度' }
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
            for(var i = 0; i < data.rows.length; i++){
    			discoveryIds.push(data.rows[i].id);
    		}
			datagridResult.selector.datagrid('loadData', data.rows.slice(0, 15));
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
			
            var timeDiv = $('<div/>').height('50px').html('<br/>用时：'+ data.discoverTime);
            dialog.append(timeDiv);
            dialog.parent().find('a[class=panel-tool-close]').hide();  
        },
		constructDiscoverParamter : function(formDiv, resourceId){
			var dataArray = formDiv.serializeArray();
			var dataJson = {};
			for(var index in dataArray){
				var data = dataArray[index];
				var tempJson = {};
				tempJson[data.name] = data.value;
				$.extend(dataJson, tempJson);
			}
			dataJson.resourceId = resourceId;
			return JSON.stringify(dataJson);
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
	
	var dcListObj = new discoveryList();
	dcListObj.init();
	
});
