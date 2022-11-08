$(function(){
	function discoveryList(){
		
	}
	discoveryList.prototype = {
		constructor : discoveryList,
		datagridDom : undefined,
		datagridJqObj : undefined,
		init : function(){
			var domId=oc.util.generateId();
			this.datagridDom = $('.discoveryList').attr('id', domId);
			this.createDatagrid();
		},
		createDatagrid : function(){
			var that = this;
			var datagrid = oc.ui.datagrid({
				selector : that.datagridDom,
				url : oc.resource.getUrl('portal/vm/discoveryVm/getVmDiscoveryList.htm'),
				hideReset : true,
				hideSearch : true,
				columns : [[{
					field:'instanceId',
					hidden : true
				},{
					field:'ip',
					title:'IP地址',
					align:'left',
					width:'31%'
				},{
					field:'typeName',
					title:'类型',
					align:'left',
					width:'31%',
					formatter : function(value,row,rowIndex){
						if(value && value=="VCenter"){
							return 'vCenter'; 
						}else{
							return value; 
						}
					}
				},{
					field:'domainName',
					title:'所属域',
					align:'left',
					width:'31%'
				},{
					field:'discoverNode',
					title:'操作',
					formatter:function(value,row,rowIndex){
						return '<a rowIndex="'+rowIndex+'" class="icon-edit quickSelectDiscoverParamter"></a>'; 
					},
					width:'5%'
				}]],
				onClickCell : function(rowIndex, field, value){
					if(field == 'discoverNode'){
						var row = $(this).datagrid('getRows')[rowIndex];
						that.openDiscoverParaDlg(row);
					}
				}
			});
			that.datagridJqObj = datagrid;
		},
		openDiscoverParaDlg : function(row){
			var that = this;
			var formDom = this.createFormDom();
			var dlg = $("<div/>").append(formDom).dialog({
				title : '编辑属性信息',
				width : '460px',
				height : '400px',
				buttons : [{
					text : '连接测试',
					handler : function(){
						that.testDiscover(formDom, row.instanceId, dlg);
					}
				},{
					text : '保存发现信息',
					handler : function(){
						that.updateDiscoverPara(formDom, row.instanceId, dlg);
					}
				},{
					text : '重新发现',
					handler : function(){
						that.reDiscover(formDom, row.instanceId, dlg);
					}
				}]
			});
			oc.ui.form({
				selector : formDom
			});
			var domains = oc.index.getDomains();
			var discoveryType = oc.ui.combobox({
				selector : formDom.find("[name='discoveryType']"),
				placeholder : false,
				readonly : true,
				data : [
					{id:'1',name:'vCenter(5.0/5.1)'},
					{id:'9',name:'vCenter5.5'},
				  {id:'10',name:'vCenter6.0'},
				  {id:'11',name:'vCenter6.5'},
				  {id:'2',name:'Esxi'},
				  {id:'3',name:'XenPool'},
				  {id:'4',name:'FusionCompute1.5(默认)'},
				  {id:'5',name:'FusionCompute1.3'},
				  {id:'6',name:'Kvm'},
				  {id:'7',name:'阿里云平台'},
				  {id:'8',name:'Kylin'},  
				]
			});
			discoveryType.jq.combobox('setValue', row.discoveryType);
			var domainId = oc.ui.combobox({
				selector : formDom.find("[name='domainId']"),
				placeholder : false,
				readonly : false,
				data : domains
			});
			domainId.jq.combobox('setValue', row.domainId);
			var discoverNode = oc.ui.combobox({
				selector : formDom.find("[name='DCS']"),
				placeholder : false,
				readonly : true,
				data : [{id:row.discoverNode,name:row.discoverNodeName}]
			});
			discoverNode.jq.combobox('setValue', row.discoverNode);
			
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
			
			formDom.find("[name='IP']").val(row.ip);
			formDom.find("[name='userName']").val(row.userName);
			formDom.find("[name='password']").val(row.password);
			if(row.categoryId == 'DTCenterCloudGlobalDashBoards'){
				formDom.find("[name='regeion']").val(row.regeion);
				formDom.find("div.form-group:has([name='project'])").hide();
			} else if(row.categoryId == 'KyLinServers'){
				formDom.find("[name='project']").val(row.project);
				formDom.find("div.form-group:has([name='regeion'])").hide();
			} else{
				formDom.find("div.form-group:has([name='regeion'])").hide();
				formDom.find("div.form-group:has([name='project'])").hide();
			}
			
		},
		createFormDom : function(){
			var formDom = $("<form class='oc-form col1' name='discoverParaForm'></form>");
			var fieldsetParameter = $("<fieldset class='singdiscparamfieldset'>" +
					"<legend>连接信息<span class='telSSHBtn'></span></legend>" +
					"</fieldset>");
			var paraNodes = [{label: '发现方式：', name: 'discoveryType', type: 'select', required: true, validType: '', readonly : false},
			                 {label: '所属域：', name: 'domainId', type: 'select', required: true, validType: '', readonly : false},
			                 {label: '所属DCS：', name: 'DCS', type: 'select', required: true, validType: '', readonly : false},
			                 {label: 'IP地址：', name: 'IP', type: 'text', required: true, validType: 'ip', readonly : true},
			                 {label: '用户名：', name: 'userName', type: 'text', required: true, validType: '', readonly : false},
			                 {label: '密码：', name: 'password', type: 'password', required: true, validType: '', readonly : false},
			                 {label: '所在域', name: 'regeion', type: 'text', required: true, validType: '',readonly : true},
			                 {label: '所属项目', name: 'project', type: 'text', required: true, validType: '',readonly : true}];
			for(var i = 0; i < paraNodes.length; i++){
				var formGroupDom = $("<div class='form-group'><label></label><div class='inputBox'></div></div>");
				formGroupDom.find("label").html(paraNodes[i].label);
				var node = null;
				if(paraNodes[i].type == 'select'){
					node = $("<select name='" + paraNodes[i].name + "'></select>");
				}else if(paraNodes[i].type == 'text'){
					node = $("<input name='" + paraNodes[i].name + "' type='text'>");
				}else if(paraNodes[i].type == 'password'){
					node = $("<input name='" + paraNodes[i].name + "' type='password'>");
				}
				if(paraNodes[i].required){
					node.attr('required', 'required');
				}
				if(paraNodes[i].validType != ''){
					node.attr('validType', paraNodes[i].validType);
				}
				if(paraNodes[i].readonly){
					node.attr('readonly', 'readonly');
				}
				formGroupDom.find('.inputBox').append(node);
				fieldsetParameter.append(formGroupDom);
			}
			
			var fieldsetAutoRefresh = $("<fieldset class='singdiscparamfieldset'>" +
					"</fieldset>");
//			var autoRefreshCheckbox = $("<input name='ifAutoRefresh' type='checkbox'>自动刷新&nbsp;&nbsp;</input>");
//			var autoRefreshSelectLabel = $('<label>刷新周期:</label>');
//			var autoRefreshSelect = $("<select name='autoRefreshCycleDay'></select>");
			var formGroupDom = $("<div class='form-group'><input name='ifAutoRefresh' type='checkbox'>自动刷新&nbsp;&nbsp;&nbsp;&nbsp;<label style='display:none;'  name='autoRefreshCycleDayLabel' >刷新周期：</label>" +
					"<div name='autoRefreshCycleDayDiv' style='display:none;' class='inputBox'><select name='autoRefreshCycleDay'></select></div></div>");
			
			fieldsetAutoRefresh.append(formGroupDom);
			
			formDom.append(fieldsetParameter);
			formDom.append(fieldsetAutoRefresh);
			return formDom;
		},
		updateDiscoverPara : function(formDiv, instanceId, dlg){
			var that = this;
			var flag = this.validateForm(formDiv);
			if(flag){
				var dataJson = this.constructDiscoverParamter(formDiv);
				oc.util.ajax({
					url : oc.resource.getUrl("portal/vm/discoveryVm/updateDiscoverParamterVm.htm"),
					data : {"jsonData":dataJson, "instanceId":instanceId},
					timeout : null,
					success : function(json){
						if(json.code == 200){
							if(json.data == 1){
								alert('更新成功');
								dlg.dialog('destroy');
								that.datagridJqObj.reLoad();
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
		// 总共有多少类型
		scanAllType:function(data, types){
			var that = this;
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
					that.scanAllType(node.children, types);
				}
			}
			return types;
		},
		// 总共有多少层
		scanAllLevel:function(data, levelNo){
			var that = this;
			for(var i = 0; i < data.length; i++){
				var node = data[i];
				if(node.children && node.children.length > 0){
					var newLevelNo = that.scanAllLevel(node.children, ++levelNo);
					if(newLevelNo > levelNo){
						levelNo = newLevelNo;
					}
				}
			}
			return levelNo;
		},
		modifyNodes:function(data, newData, type){
			var that = this;
			for(var i = 0; i < data.length; i++){
				var node = $.extend({}, data[i]);
				var flagChecked = true;
				var flagUuidRepeat = true;
				switch (node['type']) {
				case 'XenPool':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'XenHost':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'XenVM':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'XenSR':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'VirtualMachine':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'ClusterComputeResource':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'HostSystem':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'Datastore':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'FusionComputeSite':
				case 'FusionComputeOnePointThreeSite':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'FusionComputeHost':
				case 'FusionComputeOnePointThreeHost':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'FusionComputeDataStore':
				case 'FusionComputeOnePointThreeDataStore':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'FusionComputeCluster':
				case 'FusionComputeOnePointThreeCluster':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'FusionComputeVM':
				case 'FusionComputeOnePointThreeVM':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'KvmHost':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'KvmVM':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'KvmDataStore':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;
				case 'KyLinServer':
				case 'KyLinVm':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;			
				case 'DTCenterCloudGlobalDashBoard':
				case 'DTCenterECS':
					if(!node['ifMonitored']){
						flagChecked = false;
					}
					if(!node['uuidRepeat']){
						flagUuidRepeat = false;
					}
					break;			
				default:
					break;
				}
				
				var addStrFlag = '$(新增)$';
				if(!flagUuidRepeat && node['name'].indexOf(addStrFlag)<0){
					node['name'] = node['name']+addStrFlag;
					flagChecked = true;
					flagChecked = true;
				}else if(!flagUuidRepeat && node['name'].indexOf(addStrFlag)>0){
					flagChecked = true;
				}
				node.checked = flagChecked;
				
				//node.chkDisabled = true;
				if(node.type == type){
					node.children = [];
				}
				if(node.children && node.children.length > 0){
					node.children = that.modifyNodes(node.children, [], type);
					newData.push(node);
				}else{
					if(node.type == type){
						newData.push(node);
					}
				}
			}
			return newData;
		},
		zTreeObjs : [],
		organizeDiscoveryInfo:function(data){
			var that = this;
			var dlg = $('<div/>');
			
			// 显示发现结果
//			discoveryResult.show();
			var setting = {
				view: {
					selectedMulti: false,
					showIcon : false
				},
				check : {
					enable : true
				}
			}, levelNo = 0, types = that.scanAllType(data.vmResourceTree, []), levelNo = that.scanAllLevel(data.vmResourceTree, 0), zTreeNodes = data.vmResourceTree;
			
			var accordionNode = $("<div/>");
			var timeStr = '';
			if(data['reDiscoveryTime']) timeStr = data['reDiscoveryTime'];
			
			var timeDiv = $("<div/>").height('50px').html('<br> 用时 : '+timeStr);
			dlg.append(accordionNode);
			dlg.append(timeDiv);
			var accordion = accordionNode.accordion({
				border : false,
				fit : false,
				height : '325px',
				width : '100%'
			});
			var selected = true;
			//调整类型显示顺序
			types = that.ajustTypeArray(types);
			
			for(var i = 0; i < types.length; i ++){
				var type = types[i];
				if(type == 'ClusterComputeResource' 
					|| type == 'HostSystem' 
					|| type == 'VirtualMachine' 
					|| type == 'Datastore'
					|| type == 'XenHost' 
					|| type == 'XenVM' 
					|| type == 'XenSR'
					|| type == 'FusionComputeCluster'
					|| type == 'FusionComputeHost' 
					|| type == 'FusionComputeDataStore' 
					|| type == 'FusionComputeVM'
					|| type == 'FusionComputeOnePointThreeCluster'
					|| type == 'FusionComputeOnePointThreeHost' 
					|| type == 'FusionComputeOnePointThreeDataStore' 
					|| type == 'FusionComputeOnePointThreeVM'
					|| type == 'ResourcePool' 
					|| type == 'KvmHost' 
					|| type == 'KvmVM' 
					|| type == 'KvmDataStore' 
					|| type == 'DTCenterCloudGlobalDashBoard'
					|| type == 'DTCenterECS'
					|| type == 'KyLinServer'
					|| type == 'KyLinVm'
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
					case 'ResourcePool':
						title = '资源池';
						break;
					case 'Datastore':
						title = '数据存储';
						break;
					case 'FusionComputeSite':
					case 'FusionComputeOnePointThreeSite':
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
					case 'KvmHost':
						title = 'kvm服务器';
						break;
					case 'KvmVM':
						title = 'kvmVM';
						break;
					case 'KvmDataStore':
						title = 'kvm存储库';
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
						zTreeData = that.modifyNodes(zTreeData, [], type)
					}
					if('ResourcePool'==type){
						setting.check.enable = false;
					}else{
						setting.check.enable = true;
					}
					var zTreeObj = $.fn.zTree.init(zTreeNode, setting, zTreeData);
					zTreeObj.expandAll(true);
					that.zTreeObjs.push({'zTree':zTreeObj,'type':type});
					selected = false;
				}
			}
			
			//添加 新增标示
			accordionNode.find('span').each(function(){
				var thisObj = $(this);
				var html = thisObj.html();
				var addStrFlag = '$(新增)$';
				if(html.indexOf(addStrFlag)>0){
					thisObj.html(html.replace(addStrFlag,''));
					thisObj.after('<font color="red">(新增)</font>');
				}
			});
			
			dlg.dialog({
			    title: '发现结果',
			    width : 650,
				height : 450,
				buttons : [{
			    	text: '确定',
			    	handler:function(){
			    		var instanceIds = [];
			    		var instanceIdsUnchecked = [];
			    		var zTreeObjs = that.zTreeObjs;
			    		
						for(var i = 0; i < zTreeObjs.length; i ++){
							var zTreeObj = zTreeObjs[i].zTree;
							var type = zTreeObjs[i].type;
							var nodes = zTreeObj.getCheckedNodes(true);
							var nodesUnchecked = zTreeObj.getCheckedNodes(false);
							for(var j = 0; j < nodes.length; j++){
								if(type == nodes[j].type){
									var id = nodes[j].id;
									if(id && !isNaN(id)){
										instanceIds.push(id);
									}
								}
							}
							for(var x = 0; x < nodesUnchecked.length; x++){
								if(type == nodesUnchecked[x].type){
									instanceIdsUnchecked.push(nodesUnchecked[x].id);
								}
							}
						}
						if(instanceIds.length > 0 || instanceIdsUnchecked.length > 0 ){
							oc.util.ajax({
								url : oc.resource.getUrl('portal/vm/discoveryVm/addMoniterReDiscoveryVm.htm'),
								timeout : null,
								data : {instanceIds : instanceIds.join(','),instanceIdsUnchecked : instanceIdsUnchecked.join(',')},
								success : function(json){
									dlg.dialog('destroy');
									alert('加入监控成功');
								}
							});
						}
			    	}
			    }
//				,{
//			    	text: '取消',
//			    	handler:function(){
//			    		dlg.dialog('destroy');
//			    	}
//			    }
			    ],
			    onLoad:function(){
			    	
			    }
			});
			//隐藏右上x按钮
			dlg.parent().find('a[class=panel-tool-close]').hide();
			
//			// 发现时间
//			discvoeryResultSummaDiv.find(".discoveryTime").html(data.autoDiscoveryTime);
//			// 禁用开发发现按钮
//			startDiscoveryBtn.linkbutton('RenderLB', {disabled : true});
//			// 默认为加入监控
//			joinMonitorDiv.find("input[name='addDefaultMonitor']").attr('checked', 'checked');
//			if(data.tooltips != undefined){
//				tooltipsDlg(data);
//			}
		},
		ajustTypeArray : function(types){
			var arrForSort = [["VCenter", "Datacenter", "ClusterComputeResource", "HostSystem", "VirtualMachine", "Datastore","ResourcePool"],
			                  ["XenPool", "XenHost", "XenVM", "XenSR"],
			                  ["FusionComputeSite","FusionComputeCluster","FusionComputeHost","FusionComputeVM","FusionComputeDataStore"],
			                  ["FusionComputeOnePointThreeSite","FusionComputeOnePointThreeCluster","FusionComputeOnePointThreeHost","FusionComputeOnePointThreeVM","FusionComputeOnePointThreeDataStore"],
			                  ["KvmHost", "KvmVM", "KvmDataStore"],
			                  ["DTCenterCloudGlobalDashBoard", "DTCenterECS"],
			                  ["KyLinServer","KyLinVm"]];
			var typesNew = new Array();
			//按arrForSort排序
			for(var i=0;i<arrForSort.length;i++){
				var sortArr = arrForSort[i];
				for(var x=0;x<sortArr.length;x++){
					for(var z=0;z<types.length;z++){
						if(sortArr[x]==types[z]){
							typesNew.push(sortArr[x]);
						}
					}
				}
			}
			return typesNew;
		},
		reDiscover : function(formDiv, instanceId, dlg){
			var that = this;
			
			var dataJson = this.constructDiscoverParamter(formDiv);
			dataJson = eval('(' + dataJson + ')');
			// 发现请求后台
			oc.util.ajax({
				url : oc.resource.getUrl("portal/vm/discoveryVm/reDiscoverVmByTreeResult.htm"),
				timeout : null,
				data : {
					domainId : dataJson['domainId'],
					DCS : dataJson['DCS'],
					discoveryType : dataJson['discoveryType'],
					IP : dataJson['IP'],
					userName : dataJson['userName'],
					password : dataJson['password'],
					regeion : dataJson['regeion'],
					project : dataJson['project'],
					instanceId : instanceId,
					dataJson : dataJson
				},
				success : function(json){
					if(json.code == 200){
						if(json.data.isSuccess){
							that.datagridJqObj.reLoad();
							dlg.dialog('destroy');
							that.organizeDiscoveryInfo(json.data);
						}else{
							if(json.data['errorMsg']){
								alert(json.data['errorMsg']);
								return;
							}else{
								var content = oc.local.module.resource.discovery['code_' + json.data.errorCode];
								alert('自动发现出错：' + (content == undefined ? '未知错误' : content));
							}
						}
					}else{
						alert('自动发现出错');
					}
				}
			});
			
//			var flag = this.validateForm(formDiv);
//			if(flag){
//				var dataJson = this.constructDiscoverParamter(formDiv);
//				oc.util.ajax({
//					url : oc.resource.getUrl("portal/vm/discoveryVm/reDiscoverVm.htm"),
//					data : {"jsonData":dataJson, "instanceId":instanceId},
//					timeout : null,
//					success : function(json){
//						if(json.code == 200){
//							if(json.data == 1){
//								alert('重新发现成功');
//								dlg.dialog('close');
//							}else{
//								alert('重新发现失败');
//							}
//						}
//					}
//				});
//			}
		},
		testDiscover : function(formDiv, instanceId, dlg){
			var flag = this.validateForm(formDiv);
			if(flag){
				var dataJson = this.constructDiscoverParamter(formDiv);
				oc.util.ajax({
					url : oc.resource.getUrl("portal/vm/discoveryVm/testDiscoverVm.htm"),
					data : {"jsonData":dataJson, "instanceId":instanceId},
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
				var tempJson = {};
				tempJson[data.name] = data.value;
				$.extend(dataJson, tempJson);
			}
			return JSON.stringify(dataJson);
		}
	}
	new discoveryList().init();
});