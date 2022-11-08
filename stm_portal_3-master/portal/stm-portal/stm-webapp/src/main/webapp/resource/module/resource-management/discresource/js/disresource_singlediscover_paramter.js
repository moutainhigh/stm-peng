$(function() {
	function singlediscparam(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	singlediscparam.prototype = {
		constructor : singlediscparam,
		formNode : undefined,
		form : undefined,
		domainCombobox : undefined,
		cfg : undefined,
		_defaults : undefined,
		discParaDiv : undefined,
		id : undefined,
		resourceDiscData : undefined,
		open : function() {
			var cfg = this.cfg;
			this.id = cfg.id;
			this.discParaDiv = $("#" + this.id).find(".singleDisc_discPara_content");
			var json = cfg.data, resourceId = cfg.resourceId;
			this._parseJson2Input(json, resourceId);
		},
		/**
		 * 1.把页面参数分成三大块nodeGroup,option,required
		 * 2.添加发现按钮
		 * 3.对下拉，单选等添加事件
		 * @param json
		 * @param resourceId
		 */
		_parseJson2Input : function(json, resourceId) {
			var that = this;
			// 清空singleDisc_discPara_content
			var discParaDiv = this.discParaDiv.hide();
			discParaDiv.empty();
			var formDom = $("<form/>").addClass('oc-form col1');
			// 加入资源模型ID
			formDom.append($("<input/>").attr('type', 'hidden').attr('name', 'resourceId').val(resourceId));
			// 加入域ID -- name='domainid'
			var domainFieldsetDom = $("<fieldset/>").attr('name', 'domainfieldset').append("<legend>域</legend>");
			var domainDivDom = $("<div/>").addClass('form-group').attr('style', 'text-align: left;');
			var domainLabelDom = $("<label/>").html('域：').css('width','150px');
			var domainInnerDivDom = $("<div/>");
			var domainSelectDom = $("<select/>").attr('name', 'domainId').attr('required', 'required');
			var domainToolTips = $("<span/>").addClass('domainTooltips').css('margin-left', '50px');
			var domains = oc.index.getDomains();
			for(var i = 0; i < domains.length; i ++){
				var domain = domains[i];
				var domainOption = $("<option />").attr('value', domain.id).html(domain.name);
				//domainSelectDom.append(domainOption);
			}
			domainDivDom.append(domainLabelDom).append(domainInnerDivDom.append(domainSelectDom).append(domainToolTips));
			formDom.append(domainFieldsetDom.append(domainDivDom));
			discParaDiv.append(formDom);
			/**
			 *  1.向formNode里面添加相应的节点
			 *  2.节点有三种一、nodeGroup DCS，二、option 可选发现方式，三、必选发现方式
			 *  3.在最后放入发现按钮
			 */
			var dcscontent = "";
			var optioncontent = undefined;
			var requiredcontent = $("<div/>")
			for ( var pluginId in json) {
				var plugin = json[pluginId];
				if (pluginId == 'nodeGroup') {
					dcscontent = $("<fieldset/>").attr('name', 'DCS').append("<legend>DCS</legend>");
					var nodeGroup = this._createNodeGroup(plugin);
					// 清空后台添加的数据在后面加入前台js对其动态加载
					nodeGroup.find("select").empty();
					dcscontent.append(nodeGroup);
				} else if (pluginId == 'option') {
					optioncontent = this._createOptionPluginDiv(resourceId, plugin);
				} else {
					var requiredFieldset = $("<fieldset/>");
					for(var i=0; i < plugin.length; i++){
						if(plugin[i].boxStyle == 'GroupBox'){
							requiredFieldset.attr('name', plugin[i].name);
							
						}
					}
					requiredFieldset.append($("<legend/>").html(requiredFieldset.attr('name')));
					var required = this._createInitParamNodeArray(plugin);
					for (var i = 0; i < required.length; i++) {
						var flag = true;
						var attrName = required[i].find('input').attr('name');
						// 判断重复节点
						if(requiredcontent.find("input[name="+attrName+"]").length > 0){
							flag = false;
						}
						if(flag){
							requiredFieldset.append(required[i]);
						}
					}
					requiredcontent.append(requiredFieldset);
				}
			}
			formDom.append(dcscontent).append(optioncontent).append(requiredcontent);
			// 加入样式的class
			discParaDiv.show().find("fieldset").attr('class', 'singdiscparamfieldset');
			// 加入发现按钮
			var outerDivNode = $("<div/>").addClass('form-group').attr('style', 'text-align:right;').append($("<label/>"));
			var discoverBtn = $("<a/>").attr('name', 'discoverBtn');
			formDom.append(outerDivNode.append(discoverBtn));
			// 先渲染form
			this.formNode = formDom;
			this.form = oc.ui.form({
				selector : formDom
			});
			// 增加回车事件
			this.setFocus2NextInput(formDom);
			// 发现按钮渲染
			discoverBtn.linkbutton('RenderLB', {
				text : '开始发现',
				iconCls : 'fa fa-eye',
				onClick : function(){
					that.discover();
				}
			});
			// 可选插件combobox渲染
			if(optioncontent){
				var optionCombobox = optioncontent.find('select').attr('optionName', 'optionSelect');
				oc.ui.combobox({
					selector : optionCombobox,
					onChange : function(newValue, oldValue){
					$("div.myspan").remove();
						optioncontent.find(".form-group:gt(0)").remove();
						that._createOptionPluginContent(resourceId, newValue, optioncontent,formDom);
					}
				});
				this._createOptionPluginContent(resourceId,
						optioncontent.find("[optionName='optionSelect']").combobox('getValue'),
						optioncontent,formDom);
			}
			// 下拉列表
			formDom.find("select[optionName!='optionSelect']").each(function(){
				var select = $(this);
				oc.ui.combobox({
					selector : select,
					editable : false,
					panelHeight : 'auto',
					onChange : function(newValue, oldValue){
						that.showAndHideObj(formDom, this, newValue);
					}
				});
				if(select.parent().parent('.form-group').attr('group') == undefined){
					that.showAndHideObj(formDom, select, select.find('option:selected').val());
				}
			});
			// 单选框
			formDom.find(":radio").each(function(){
				var radio = $(this);
				radio.on('click', function(){
					that.showAndHideObj4Radio(formDom, radio);//	var securityLevel=form.find("input[name='securityLevel']").val();
				var currentRadion=radio.context.value;
				var newValue=securityLevel=formDom.find("input[name='securityLevel']").val();
				if(currentRadion==3){
					for ( var pluginId in json) {
						var plugin = json[pluginId];
						if (pluginId == 'SnmpPlugin') {
							//change事件动态加载节点
							for(var i = 0; i < plugin.length; i ++){
								if(plugin[i].boxStyle=='OptionBox'){
									var supportValues=plugin[i].supportValues;
									for(var k=0;k<supportValues.length;k++){
										if(supportValues[k].value==newValue){
											hideGroup=supportValues[k].hideGroups?supportValues[k].hideGroups:null;
											showGroup=supportValues[k].showGroups?supportValues[k].showGroups:null;

										}
									}
								}
							}
							if(hideGroup!=null){
								for(var i=0;i<hideGroup.length;i++){
									formDom.find("div[group='"+hideGroup[i]+"']").hide().val('');	
								}
							}
							if(showGroup!=null){
							for(var i=0;i<showGroup.length;i++){
								formDom.find("div[group='"+showGroup[i]+"']").show();	
							}
						}
						
							
							
						}
						
					}
				}
			
				
				});
				if(radio.parent().parent().parent('.form-group').attr('group') == undefined
						&& radio.attr('checked')){
					that.showAndHideObj4Radio(formDom, radio);
				}
			});
			// 加入域和DCS的关联
			var dcsCombobox = oc.ui.combobox({
				selector : dcscontent.find("select"),
				panelHeight : 'auto',
				editable : false
			});
			
			var domainData = [], user = oc.index.getUser();
			if(user.systemUser){
				domainData = oc.index.getDomains();
			}else if(user.domainUser && user.domainManageDomains){
				var manageDomains = user.domainManageDomains, allDomains = oc.index.getDomains();
				for(var i = 0; i < manageDomains.length; i++){
					for(var j = 0; j < allDomains.length; j++){
						if(manageDomains[i].id == allDomains[j].id){
							domainData.push(allDomains[j]);
							break;
						}
					}
				}
			}
			
			this.domainCombobox = oc.ui.combobox({
				selector : domainSelectDom,
				placeholder : false,
				editable : false,
				data : domainData,
				onChange : function(newValue, oldValue){
					var array = new Array();
					var domains = oc.index.getDomains();
					var domain = undefined;
					for(var i = 0; i < domains.length; i ++){
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
						dcsCombobox.jq.combobox('setValue', array[0].d);
						domainToolTips.html('');
					}else{
						dcsCombobox.jq.combobox('setValue', '');
						domainToolTips.html('该域没有关联DCS');
					}
					// 资源列表为会调这个加载方法
					if($("#" + that.id).parent().find("[name='resourceGroup']").length > 0){
						oc.util.ajax({
							url : oc.resource.getUrl("system/resourceGroup/queryAllResourceGroupByDomain.htm"),
							data : {domainId : newValue},
							success : function(data){
								oc.module.resmanagement.discresource.singlediscover.reloadResourceGroup(data.data);
							}
						});
					}
				}
			});
		},
		/**
		 * 把容器中的所有输入框加上回车事件
		 * @param container
		 */
		setFocus2NextInput : function(container){
			container.find(":input,select,textarea").each(function(){
				var eventInput = $(this);
				var allVisibleInput = container.find(":input,select,textarea");
				eventInput.keypress(function(event){
					var key = event.which;
					if(key == '13'){
						var flag = false;
						for(var i = 0; i < allVisibleInput.length; i ++){
							visibleInput = $(allVisibleInput[i]);
							if(flag && visibleInput.is(":visible")){
								visibleInput.focus();
								visibleInput.select();
								break;
							}
							if(eventInput.attr('name') == visibleInput.attr('name')){
								flag = true;
							}
						}
					}
				})
			});
		},
		/**
		 * 创建一个可选下拉列表
		 * 1.把下拉列表节点创建
		 * 2.节点下面要有一个对应的div
		 * 3.对下拉列表创建一个事件关联到对应的div上进行动态加载div内容
		 * @param pluginId
		 * @param pluginData
		 */
		_createOptionPluginDiv : function(resourceId, pluginData){
			var optionFieldset = $("<fieldset />").append("<legend/>");
			var that = this;
			// 这里数组里只有一个节点
			var optionArray = this._createInitParamNodeArray(pluginData);
			var optionNode = optionArray[0];
			optionFieldset.append(optionNode);
			return optionFieldset;
		},
		_createOptionPluginContent : function(resourceId,value,optioncontent,formDom){
			var that = this;
			// 初始化的时候要运行一次
			oc.util.ajax({
				async : false,
				url : oc.resource.getUrl('portal/resource/discoverResource/getPlugParamByCollType.htm'),
				data : {resourceId:resourceId,pluginId:value},
				success : function(json){
					if(json.code == 200){
						var hideGroup=new Array();
						var showGroup=new Array();
						for(var i = 0; i < json.data.length; i ++){
							if(json.data[i].boxStyle == 'GroupBox'){
								optioncontent.attr('name', json.data[i].name);
							}
						
							// 去除重复节点
							that.discParaDiv.find("[name='"+json.data[i].id+"']").each(function(){
								$(this).parent().parent().remove();
							});
						}
						optioncontent.find("legend").html(optioncontent.attr('name'));
						var nodeArray = that._createInitParamNodeArray(json.data);
						for (var i = 0; i < nodeArray.length; i ++){
							var newNode = nodeArray[i];
							newNode.find('[required]').each(function(index, domEle){
								$(domEle).after('<span class="oc-valid-required-star">*</span>');
							});
							// 如果是combobox
							var newNodeSelect = newNode.find('select').attr('optionName', 'optionSelect');
							if(newNode.find("select").length > 0){
								oc.ui.combobox({
									selector : newNodeSelect,
									panelHeight : 'auto',
									onChange : function(newValue, oldValue){//change事件动态加载节点
										for(var i = 0; i < json.data.length; i ++){
											if(json.data[i].boxStyle=='OptionBox'){
												var supportValues=json.data[i].supportValues;
												for(var k=0;k<supportValues.length;k++){
													if(supportValues[k].value==newValue){
														hideGroup=supportValues[k].hideGroups?supportValues[k].hideGroups:null;
														showGroup=supportValues[k].showGroups?supportValues[k].showGroups:null;

													}
												}
											}
										}
										if(hideGroup!=null){
											for(var i=0;i<hideGroup.length;i++){
												formDom.find("div[group='"+hideGroup[i]+"']").hide().val('');	
											}
										}
										if(showGroup!=null){
										for(var i=0;i<showGroup.length;i++){
											formDom.find("div[group='"+showGroup[i]+"']").show();	
										}
									}
									}
								});
							}
							// oc.ui.form.js No.349 row function _initValid
							newNode.find('[validType],[required]').each(function(index, domEle){
								domEle=$(domEle);
								var isRequired=domEle.attr('required');
								domEle.validatebox({
									required:isRequired,
									validType:domEle.attr('validType')
								});
							});
							optioncontent.append(newNode);
						}
						that.setFocus2NextInput(that.formNode);
					}
				}
			});
		},
		_createNodeGroup : function(pluginId, pluginData){
			var nodeArray = this._createInitParamNodeArray(pluginId, pluginData);
			// 这里只有一个节点
			var groupNode = nodeArray[0];
			return groupNode;
		},
		
		// 创建一个节点数组并进行排序工作
		_createInitParamNodeArray : function(paramter) {
			var nodeArray = new Array();
			for (var i = 0; i < paramter.length; i++) {
				var outerNode = this._createInitParamNode(paramter[i]);
				if (outerNode != '' && outerNode != undefined) {
					nodeArray[nodeArray.length] = outerNode;
				}
			}
			$('a.r-h-ico.r-h-help').tooltip({
				position : 'right'
			});
			// 排序
			for(var i = 0; i < nodeArray.length; i++){
				for(var j = i + 1; j < nodeArray.length; j++){
					var b = nodeArray[j], tmp;
					if(parseInt(nodeArray[i].attr('displayOrder')) > parseInt(b.attr('displayOrder'))){
						tmp = nodeArray[i];
						nodeArray[i] = nodeArray[j];
						nodeArray[j] = tmp;
					}
				}
			}
			/**  这个oc中的sort函数有错误
			nodeArray = nodeArray.sort(function(a, b){
				return parseInt(a.attr('displayOrder')) < parseInt(b.attr('displayOrder'));
			});
			*/
			return nodeArray;
		},
		// 创建一个节点
		_createInitParamNode : function(paramter) {
		
			var that = this, displayOrder = paramter.displayOrder;
			var outerDivNode = $("<div/>").addClass('form-group').css('text-align', 'left').attr('group', paramter.group)
				.attr('displayOrder', displayOrder == null || displayOrder == undefined || displayOrder == '' ? 1000 : displayOrder);
			var labelNode = $("<label/>").html(paramter.name + "：").css('width','150px').css('margin-top','5px');
			var innerDivNode = $("<div/>");
			// 创建节点
			var inputNode = '';
			switch (paramter.boxStyle) {
			case 'IPField':
			case 'Input':
				// 是否为密码输入框
				if (paramter.password == true) {
					inputNode = $("<input/>").attr('type', 'password');
				} else {
					inputNode = $("<input/>").attr('type', 'text');
				}
				break;
			case 'OptionBox':
				if(paramter.supportValues != null){
					inputNode = $("<select/>");
					for (var i = 0; i < paramter.supportValues.length; i++) {
						var option = paramter.supportValues[i];
						var optionNode = $("<option/>").attr('value', option.value).html(option.name);
						if(option.showGroups != null){
							optionNode.attr('showGroups', option.showGroups.join());
						}
						if (option.hideGroups != null) {
							optionNode.attr('hideGroups', option.hideGroups.join());
						}
						inputNode.append(optionNode);
					}
				}
				break;
			case 'RadioBox':
				if(paramter.supportValues != null){
					inputNode = $("<div/>");
					for (var j = 0; j < paramter.supportValues.length; j++) {
						var option = paramter.supportValues[j];
						var innerLabelNode = $("<label/>");
						var radioNode = $("<input/>").attr('type', 'radio').attr('name', paramter.id);
						radioNode.attr('value', option.value);
						if(option.showGroups != null){
							radioNode.attr('showGroups', option.showGroups.join())
						}
						if (option.hideGroups != null) {
							radioNode.attr('hideGroups', option.hideGroups.join());
						}
						if(paramter.defaultValue == option.value){
							radioNode.attr('checked', true);
						}
						innerLabelNode.append(radioNode).append(option.name);
						inputNode.append(innerLabelNode);
					}
				}
				break;
	}
		
			/**
			 * 添加节点属性
			 * 1.inputNode不为空
			 * 2.display为false在页面不添加
			 */
		
			if (inputNode != '') {
				innerDivNode.append(inputNode);
				
				// 添加外层节点属性
				inputNode.attr('name', paramter.id);
				if (paramter.group != null) {
					outerDivNode.hide();
				}
				if (paramter.isEdit == false) {
					outerDivNode.attr('disable', 'disable');
					
				}
				// 添加内层节点属性
				if (paramter.mustInput == true) {
					inputNode.attr('required', 'required');
				}
				// 默认值
				if (paramter.defaultValue != null && paramter.defaultValue != "" && paramter.defaultValue != undefined) {
					inputNode.val(paramter.defaultValue);
				}
				// 帮助信息
				if (paramter.helpInfoId != null && paramter.helpInfoId != "" && paramter.helpInfoId != undefined) {
					var helpInfoNod = $("<a href='javascript:void(0);' class='r-h-ico r-h-help' title='"+paramter.helpInfoId+"' style='display:inline-block; zoom:1; *display:inline; position: relative;top:7px;'></a>");
//					helpInfoNod.attr('title', paramter.helpInfoId);
					
					if(paramter.boxStyle == 'RadioBox'){
						inputNode.append(helpInfoNod);
					}else{
						innerDivNode.append(helpInfoNod);
					}
				}
				$.ajax({
					url:"",
					data:"",
					
					
				});
				// 如果是用户名则加入一个按钮
				if (paramter.id == 'username') {
					var accountBtn = $("<a/>").css({
						"display" : "inline-block",
						"zoom" : "1",
						"*display" : "inline",
						"position" : "relative",
						"top" : "7px"
					}).append($("<span name='usernameBtn'/>").addClass("r-h-ico r-h-man"));
					var accountIdInput = $("<input/>").attr('type', 'hidden').attr('name', 'account_id');
					innerDivNode.append(accountBtn).append(accountIdInput);
					oc.util.ajax({
						url : oc.resource.getUrl("portal/resource/discoverResource/getParameter.htm"),
						dataType:"json",
						data : {
							resourceId : this.cfg.resourceId
						},
						success : function(json){
							if(json.code == 200){
								var text=$("fieldset[name='主机连接信息'] legend")[0].innerText;
								$("div[name='myspan']").remove();
								if(text=="主机连接信息" && (json.data=="数据库" ||json.data=="中间件")){
									if($("div[name='myspan']").length==0){
										$("fieldset[name='主机连接信息'] legend").after("<div class='myspan' style='padding-left:20px;'><span class='notice-ico'>如不填写主机信息,则采集不到主机指标,如:主机CPU利用率、内存利用率等.</span></div>");	
									}
									
								}
							}
						}
					});
					
					// 添加点击事件并且加入是否为资源发现页面的标识
					accountBtn.attr('isDiscovery', 'discovery').on('click', function(){
						that.showUserPanel(this);
					});
				}
				outerDivNode.append(labelNode).append(innerDivNode);
				if (paramter.valueValidate != null && paramter.valueValidate != "" && paramter.valueValidate != undefined){
					var errorInfo = paramter.errorInfo == null || paramter.errorInfo == undefined ? '格式错误' : paramter.errorInfo;
					inputNode.attr('validType', "reg["+ paramter.valueValidate +", '"+ errorInfo +"']");
				}
				// 不显示
				if(paramter.display == false){
					outerDivNode.hide();
				}
				// 不编辑、不显示返回空
				if(paramter.display == false && paramter.edit == false){
					outerDivNode = '';
				}
			} else {
				outerDivNode = '';
			}
			return outerDivNode;
		},
		
		showAndHideObj : function(form, node, newValue){
			var option = $(node).find("option[value=" + newValue + "]");
			var showGroups = !!option.attr('showGroups') ? option.attr('showGroups').split(",") : [];
			var hideGroups = !!option.attr('hideGroups') ? option.attr('hideGroups').split(",") : [];
			for(var i = 0; i < showGroups.length; i ++){
				form.find("div[group='" + showGroups[i] + "']").show();
			}
			for(var i = 0; i < hideGroups.length; i ++){
				form.find("div[group='" + hideGroups[i] + "']").hide().val('');
			}
		},
		showAndHideObj4Radio : function(form, radio){
			var showGroups = !!radio.attr('showGroups') ? radio.attr('showGroups').split(",") : [];
			var hideGroups = !!radio.attr('hideGroups') ? radio.attr('hideGroups').split(",") : [];
			for(var i = 0; i < showGroups.length; i ++){
				form.find("div[group='" + showGroups[i] + "']").show();
			}
			for(var i = 0; i < hideGroups.length; i ++){
				form.find("div[group='" + hideGroups[i] + "']").hide().val('');
			}
		},
		showUserPanel : function(btn){
			var that = this;
			var isDiscovery = true;
			if($(btn).attr('isDiscovery') != 'discovery'){
				isDiscovery = false;
			}
			var accountDatagrid;
			var formDiv = $("#" + that.id).find(".singleDisc_discPara_content").find('form');
			var userName = formDiv.find("input[name='username']");
			var password = formDiv.find("input[name='password']");
			var account_id = formDiv.find("input[name='account_id']");
			var domainId = formDiv.find("input[name='domainId']");
			var top = parseInt($(btn).offset().top);
			var left = parseInt($(btn).offset().left + $(btn).width());
			var initParamterUserPanel = $("<div ><table></table></div>").dialog({
				title : '预置账户',
				width : 400,
				height : 200,
				left : left,
				top : top,
				buttons : [{
					text : '新增用户',
					handler : function(){
						if(!!!!! domainId.val()){
							alert("域不能为空，请先选择域!");
							return false;
						}
						if(!! domainId.val() && !!userName.val() && !!password.val()){
							oc.util.ajax({
								url : oc.resource.getUrl("portal/resource/account/insert.htm?dataType=json"),
								data : {
									account_id : '',
									domain_id : domainId.val(),
									username : userName.val(),
									password : password.val(),
									repassword : password.val(),
									comments : ''
								},
								success : function(json){
									if(json.code == 200){
										accountDatagrid.selector.datagrid('reload');
										alert("保存成功!");
									}
									initParamterUserPanel.dialog('close');
								}
							});
						}else{
							alert('用户名、密码和域不能为空');
						}
					}
				},{
					text : '确认',
					handler : function(){
						var radioChecked = initParamterUserPanel.find(".accountRadio:checked");
						if(radioChecked.length > 0){
							var rowIndex = radioChecked.val();
							var row = accountDatagrid.selector.datagrid('getRows')[rowIndex];
							userName.val(row.username);
							password.val(row.password);
							account_id.val(row.account_id);
							if(isDiscovery){
								that.domainCombobox.jq.combobox('setValue', row.domain_id);
							}
							initParamterUserPanel.dialog('close');
						}
					}
				}]
			});
			accountDatagrid = oc.ui.datagrid({
				selector : initParamterUserPanel.find('table'),
				url : oc.resource.getUrl("portal/resource/account/getList.htm?dataType=json"),
				fit : true,
				pagination : false,
				singleSelect : true,
				fitColumns : true,
				remoteSort : false,
				columns : [ [ {
					field : 'domain_id',
					title : '域ID',
					hidden : true
				}, {
					field : 'account_id',
//					title : '用户ID',
					align : 'center',
					width : '10%',
					formatter : function(value, rowData, rowIndex){
						return "<input class='accountRadio' style='margin-top:5px;' type='radio' name='accountId' value='"+rowIndex+"'>";
					}
				}, {
					field : 'domainname',
					title : '域',
					width : '15%',
					align : 'center',
					sortable : true,
					formatter : function(value, rowData, rowIndex){
						return "<label title='" + value + "'>" + value + "</label>";
					}
				}, {
					field : 'username',
					title : '用户名',
					align : 'center',
					width : '25%',
					sortable : true,
					formatter : function(value, rowData, rowIndex){
						return "<label title='" + value + "'>" + value + "</label>";
					}
				}, {
					field : 'comments',
					align : 'center',
					title : '备注',
					width : '50%',
					sortable : true,
					formatter : function(value, rowData, rowIndex){
						if(value==null || value=='null'){
							return '';
						}
						return "<label title='" + value + "'>" + value + "</label>";
					}
				}, {
					field : 'password',
					align : 'center',
					title : '密码',
					hidden : true
				} ] ],
				loadFilter : function(data) {
					var dataFiltered = data.data;
					if(data.code != 200){
						dataFiltered = data;
					}
					return dataFiltered;
				}
			});
			
		},
		validateForm : function(formDiv){
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
		},
		constructDiscoverParamter : function(formDiv){
			var dataArray = formDiv.serializeArray();
			var dataJson = {};
			for(var index in dataArray){
				var data = dataArray[index];
				if(data.name == 'collectType'){
					data.value = formDiv.find("[optionName='optionSelect']").combobox('getText');
				}
				var tempJson = {};
				tempJson[data.name] = data.value;
				$.extend(dataJson, tempJson);
			}
			dataJson.u = dataJson.username;
			dataJson.p = dataJson.password;
			delete dataJson.username;
			delete dataJson.password;
			return JSON.stringify(dataJson);
		},
		discover : function(){
			var that = this;
			var formDiv = this.discParaDiv.find('form');
		/*	if(formDiv.find("input[name='password']").val()==""){//没有主机信息,清空主机信息
				formDiv.find("input[name='username']").val(null);
				formDiv.find("input[name='port']").val(null);
			}*/
			if (this.validateForm(formDiv)) {
				var dataJson = this.constructDiscoverParamter(formDiv);
				oc.util.ajax({
					url : oc.resource
							.getUrl("portal/resource/discoverResource/discoverResource.htm"),
					data : {"jsonData":dataJson},
					timeout : 1000 * 60 * 20,
					success : function(json) {
						that.resourceDiscData = json.data;
						if (json.code == 200) {
							// 开启进度条
							oc.ui.progress();
							if (parseInt(that.resourceDiscData.status) == 1) {
								that._initDiscSuccess();
							} else {
								that._initDiscFailure();
							}
							// 关闭进度条
							$.messager.progress('close');
						} else {
							alert('发现资源出错');
						}
					}
				});
			}
		},
		handRepeatRes : function(data){
			var that = this;
			// 提前处理资源重复
			if(this.resourceDiscData.repeatPrompt == true){
				var repeatDlg = $("<div/>");
				repeatDlg.append($("<div/>").addClass("messager-icon messager-warning"))
					.append($("<div/>").css('top', '25px').addClass("oc-notice")
					.html("此IP对应的资源已经存在，是否再次当新资源再次发现？"));
				var repeatTable = $("<table/>").width("100%").height('104px').append("<tr><td>名称</td><td>IP</td></tr>");
				for(var i = 0; i < data.repeatRes.length; i++){
					var repeatRes = data.repeatRes[i];
					var disc_ip=repeatRes.ip;
					if(disc_ip==null || disc_ip=="" || disc_ip==undefined){
						disc_ip="无IP资源";
					}
					repeatTable.append("<tr><th>" + repeatRes.name + "</th><th>" + disc_ip + "</th></tr>");
				}
				repeatTable = $("<div/>").addClass("oc-wbg").css({
					"margin-top" : "50px"
				}).append(repeatTable);
				repeatDlg.append(repeatTable);
				repeatDlg.dialog({
					title : '温馨提示',
					closable : false,
					width : '450px',
					height : '250px',
					buttons : [{
						text : '&nbsp;是&nbsp;',
						handler : function(){
							that.handRepeatResRequest(repeatDlg, '0');
						}
					},{
						text : '&nbsp;否&nbsp;',
						handler : function(){
							that.handRepeatResRequest(repeatDlg, '2');
						}
					}]
				});
			}
		},
		// method (0、新增 1、刷新 2、取消)
		handRepeatResRequest : function(repeatDlg, method){
			var that = this;
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/discoverResource/handleRepeatInstance.htm'),
				data : {method : method},
				timeout : null,
				success : function(json){
					var data = json.data;
					if(parseInt(method) == 0 || parseInt(method) == 1){
						if (json.code == 200 && data.status == '1'){
							// 新增后的资源属性
							that.resourceDiscData.childInstance = data.childInstance;
							that.resourceDiscData.repeatPrompt = false;
							that.resourceDiscData.instanceId = data.instanceId;
							that.loadDiscSuccessPage(that.resourceDiscData);
							oc.module.resmanagement.discresource.singlediscover.enableContinueBtn();
							oc.module.resmanagement.discresource.singlediscover.enableFinishBtn();
							oc.module.resmanagement.discresource.disc.setDiscoveryFlag();
						} else {
							alert('失败');
						}
					}else{
						if (!json.code == 200 || !data.status == '1'){
							alert('出错');
						}
					}
					repeatDlg.dialog('close');
				}
			});
		},
		_initDiscSuccess : function(){
			var that = this;
			if(that.resourceDiscData.repeatPrompt == true){
				this.handRepeatRes(that.resourceDiscData);
			}else{
				this.loadDiscSuccessPage(that.resourceDiscData);
				oc.module.resmanagement.discresource.singlediscover.enableContinueBtn();
				oc.module.resmanagement.discresource.singlediscover.enableFinishBtn();
				oc.module.resmanagement.discresource.disc.setDiscoveryFlag();
			}
		},
		loadDiscSuccessPage : function(data){
			var that = this;
			$("#" + that.id).accordion('select', 2);
			$("#" + this.id).find(".singleDisc_result").load(
					oc.resource.getUrl('resource/module/resource-management/discresource/disresource_singlediscover_success.html'),
					function() {
						oc.module.resmanagement.discresource.discoversuccess.open({data:data});
					});
		},
		_initDiscFailure : function(){
			var that = this;
			$("#" + that.id).accordion('select', 2);
			oc.module.resmanagement.discresource.singlediscover.enableContinueBtn();
			oc.module.resmanagement.discresource.singlediscover.enableFinishBtn();
			oc.module.resmanagement.discresource.singlediscover.setContinueBtn('重新发现');
			$("#" + this.id).find(".singleDisc_result").load(
					oc.resource.getUrl('resource/module/resource-management/discresource/disresource_singlediscover_failure.html'),
					function() {
						oc.module.resmanagement.discresource.discoverfailure.open({data:that.resourceDiscData});
					});
		}
	};
	// 对外提供调用接口
	oc.ns('oc.module.resmanagement.discresource');
	var discParam = undefined;
	oc.module.resmanagement.discresource.singlediscparam = {
		open : function(cfg) {
			discParam = new singlediscparam(cfg);
			discParam.open();
		},
		discover : function(){
			if(discParam != undefined){
				discParam.discover();
			}
		},
		validateForm : function(formDiv){
			if(discParam != undefined){
				return discParam.validateForm(formDiv);
			}
		},
		constructDiscoverParamter : function(formDiv){
			if(discParam != undefined){
				return discParam.constructDiscoverParamter(formDiv);
			}
		}
	};
});