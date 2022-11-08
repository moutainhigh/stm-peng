$(function() {
	function singleDiscover(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
	}
	singleDiscover.prototype = {
		construtor : singleDiscover,
		form : undefined,
		cfg : undefined,
		_defaults : {},
		singleDiscDiv : undefined,
		handleMonitor : undefined,
		resourceGroupDescripttion : undefined,
		resourceGroup : undefined,
		continueBtn : undefined,
		finishBtn : undefined,
		id : undefined,
		allresPrototype : undefined,
		rootNode : undefined,
		firstLevel : undefined,
		open : function() {
			this.id = oc.util.generateId();
			this.singleDiscDiv = $("#singleDisc").attr('id', this.id);
			this.handleMonitor = $("#handleMonitor").attr('id', oc.util.generateId());
			var form = this.singleDiscDiv.find("form[name='categoryForm']"), that = this;
			var categoryCombotree = form.find("select[name='singleDisc_category']");
			oc.util.ajax({
				url:oc.resource.getUrl('portal/resource/discoverResource/getResPrototype.htm'),
				timeout : null,
				success : function(data){
					that.allresPrototype = data.data;
					for(var i = 0; i < that.allresPrototype.length; i ++){
						var tmpNode = that.allresPrototype[i];
						if(!!!tmpNode.pid){
							that.rootNode = tmpNode;
							break;
						}
					}
					that.firstLevel = new Array();
					for(var i = 0; i < that.allresPrototype.length; i ++){
						var tmpNode = that.allresPrototype[i];
						if(tmpNode.pid == that.rootNode.id){
							// 如果是虚拟化则不显示
							if(tmpNode.id != 'VM'){
								that.firstLevel.push(tmpNode);
							}
						}
					}
					that.initpage();
				}
			});
			this.singleDiscDiv.accordion({
				fit : false,
				border : true,
				height : '475px'
			});
			// 自定义资源组
			this.resourceGroupDescripttion = that.handleMonitor.find(".resourceGroupDescripttion").hide();
			this.resourceGroup = oc.ui.combobox({
				selector : that.handleMonitor.find("select[name='resourceGroup']"),
				placeholder : true,
				panelHeight : 'auto'
			});
			
			this.handleMonitor.find(".handleMonitor_handle").css({
				float : 'right'
			});
			// 继续发现
			this.continueBtn = this.handleMonitor.find(".continueDisc").linkbutton('RenderLB', {
				disabled : true,
				iconCls : 'fa fa-eye',
				onClick : function(){
					that.addDefaultMonitor('continue');
				}
			});
			// 完成按钮
			this.finishBtn = this.handleMonitor.find(".finish").linkbutton('RenderLB', {
				disabled : true,
				onClick : function(){
					that.addDefaultMonitor('finish');
				}
			});
		},
		initpage : function(){
			var firstLevel = this.getlevelNode(0);
			for(var i = 0; i < this.firstLevel.length; i ++){
				var tmpNode = this.firstLevel[i];
				var firstLevelNode = this.createTypeNameNode(tmpNode);
				firstLevel.append(firstLevelNode);
				this.addFirstLevelEvent(firstLevelNode);
			}
		},
		addFirstLevelEvent : function(firstLevelNode){
			var that = this;
			firstLevelNode.on('click', function(){
				that.firstLevelEventEffect();
				firstLevelNode.addClass('active');
				var data = firstLevelNode.data('data');
				for(var i = 0; i < that.allresPrototype.length; i ++){
					var tmpNode = that.allresPrototype[i];
					if(tmpNode.pid == data.id){
						var secondLevelNode = that.createTypeNameNode(tmpNode);
						that.getlevelNode(1).append(secondLevelNode);
						that.addSecondLevelEvent(secondLevelNode);
					}
				}
				var chooseNode = that.createChooseNode(data);
				that.getChooseLevelNode().append(chooseNode);
				chooseNode.find(".fa-times-circle").on('click', function(){
					that.firstLevelEventEffect();
				});
			});
		},
		firstLevelEventEffect : function(){
			var chooseLevel = this.getChooseLevelNode();
			var firstLevel = this.getlevelNode(0);
			var secondeLevel = this.getlevelNode(1);
			var thirdLevel = this.getlevelNode(2);
			chooseLevel.find(".type-name").remove();
			firstLevel.find(".type-name").removeClass('active');
			secondeLevel.find(".type-name").remove();
			thirdLevel.find(".type-name").remove();
		},
		addSecondLevelEvent : function(secondeLevelNode){
			var that = this;
			var chooseLevel = this.getChooseLevelNode();
			secondeLevelNode.on('click', function(){
				that.secondLevelEventEffect();
				secondeLevelNode.addClass('active');
				var data = secondeLevelNode.data('data');
				var firstChooseNode = chooseLevel.find(".type-name:first");
				for(var i = 0; i < that.allresPrototype.length; i ++){
					var tmpNode = that.allresPrototype[i];
					if(tmpNode.pid == data.id){
						if(firstChooseNode.data('data').id == 'NetworkDevice'){
							tmpNode.name = "SNMP";
						}
						var thirdLevelNode = that.createTypeNameNode(tmpNode);
						that.getlevelNode(2).append(thirdLevelNode);
						that.addThirdLevelEvent(thirdLevelNode);
						if(firstChooseNode.data('data').id == 'NetworkDevice'){
							break;
						}
					}
				}
				var chooseNode=  that.createChooseNode(data);
				chooseLevel.append(chooseNode);
				
				chooseNode.find(".fa-times-circle").on('click', function(){
					that.secondLevelEventEffect();
				});
			});
		},
		secondLevelEventEffect : function(){
			var chooseLevel = this.getChooseLevelNode();
			var secondeLevel = this.getlevelNode(1);
			var thirdLevel = this.getlevelNode(2);
			chooseLevel.find(".type-name:not(:first)").remove();
			secondeLevel.find(".type-name").removeClass('active');
			thirdLevel.find(".type-name").remove();
		},
		addThirdLevelEvent : function(thirdLevelNode){
			var that = this;
			var chooseLevel = this.getChooseLevelNode();
			var thirdLevel = this.getlevelNode(2);
			var data = thirdLevelNode.data('data');
			thirdLevelNode.on('click', function(){
				$(chooseLevel.find(".type-name")[2]).remove();
				thirdLevel.find(".type-name").removeClass('active');
				thirdLevelNode.addClass('active');
				var chooseNode=  that.createChooseNode(data);
				chooseLevel.append(chooseNode);
				chooseNode.find(".fa-times-circle").on('click', function(){
					$(chooseLevel.find(".type-name")[2]).remove();
					thirdLevel.find(".type-name").remove();
				});
				// 加载发现参数
				that._getpluginInitParameter(data.id, data);
				// 初始化系统资源组下拉列表
				that.reloadResourceGroup([]);
			});
		},
		getlevelNode : function(no){
			return $(this.singleDiscDiv.find(".resource-type > .resource-attr")[no]).find(".type-contents");
		},
		getChooseLevelNode : function(){
			return $(this.singleDiscDiv.find(".resource-type > .choose-type")[0]).find(".type-contents");
		},
		createTypeNameNode : function(tmpNode){
			return $("<span/>").addClass('type-name').data('data', tmpNode).html(tmpNode.name).attr('title', tmpNode.name);
		},
		createChooseNode : function(tmpNode){
			var span = $("<span/>").addClass('type-name');
			span.data('data', tmpNode).append($("<a/>").html(tmpNode.name))
				.append($("<a/>").addClass("fa fa-times-circle marginleft6"));
			return span;
		},
		_getpluginInitParameter : function(newValue, newJson){
			var that = this;
			if(newJson.type == 'resource'){
				var resourceId = newValue;
				if (resourceId == '' || resourceId == undefined) {
					alert('请选择相应的资源');
				} else {
					oc.util.ajax({//
						url : oc.resource.getUrl("portal/resource/discoverResource/getpluginInitParameter.htm"),
						timeout : null,
						data : {resourceId:resourceId},
						success : function(json) {
							if (json.data) {
								oc.resource.loadScript('resource/module/resource-management/discresource/js/disresource_singlediscover_paramter.js', function(){
									oc.module.resmanagement.discresource.singlediscparam.open({id : that.id, data:json.data, resourceId :resourceId});
									that.singleDiscDiv.accordion('select', 1);
								});
							}
						}
					});
				}
			}else{
				that.singleDiscDiv.find(".singleDisc_discPara").empty();
				that.singleDiscDiv.find(".singleDisc_result").empty();
			}
		},
		addDefaultMonitor : function(btnName){
			var that = this;
			var mainInstance = this.singleDiscDiv.find("input[name='instanceId']");
			var mainInstanceId = mainInstance.val();
			var childInstanceIds = new Array();
			this.singleDiscDiv.find("input[name='childInstance']:checked").each(function(){
				childInstanceIds.push($(this).val());
			});
			// 加入监控
			if(!!this.handleMonitor.find("input[name='addDefaultMonitor']").prop('checked')
					&& !!mainInstanceId && (mainInstanceId != '')){
				
				// 资源显示名称
				var newShowName = this.singleDiscDiv.find("input[name='newInstanceName']").val();
				if(newShowName == null || newShowName == undefined || newShowName == ''){
					alert('资源显示名称不能为空');
					return false;
				}
				
				// 提交资源加入监控
				oc.util.ajax({
					url : oc.resource.getUrl('portal/resource/discoverResource/joinMonitor.htm'),
					timeout : null,
					data : {
						resourceGroupId : that.resourceGroup.jq.combobox('getValue'),
						newInstanceName : newShowName,
						mainInstanceId : mainInstanceId,
						childInstanceIds : childInstanceIds.join(",")
					},
					success : function(json){
						if(json.code == 200){
							if(json.data.status == '1'){
								alert('加入监控成功');
								mainInstance.val('');
								if(btnName == 'finish'){
									oc.module.resmanagement.discresource.disc.close();
								}
							}else if(json.data.status == '0'){
								alert('加入监控失败');
							}else{
								alert('加入监控失败,监控数量超过购买监控数量!');
							}
						}else{
							alert('加入监控失败');
						}
					}
				});
			}
			if(btnName == 'finish'){
				oc.module.resmanagement.discresource.disc.close();
			}
			if(btnName == 'continue'){
				that.setContinueBtn('继续发现');
				that.disableContinueBtn();
				that.selectPanel(1);
			}
		},
		selectPanel : function(index){
			this.singleDiscDiv.accordion('select', index);
		},
		reloadResourceGroup : function(data){
			var array = new Array();
			array.unshift({
				id : '',
				name : oc.local.ui.select.placeholder
			});
			for(var i = 0; i < data.length; i ++){
				array.push({
					id : data[i].id,
					name : data[i].name
				})
			}
			this.resourceGroup.jq.combobox('loadData', array);
			if(array.length > 1){
				this.resourceGroup.jq.combobox('setValue', '');
				this.resourceGroupDescripttion.show();
			}else{
				this.resourceGroup.jq.combobox('setValue', '');
				this.resourceGroupDescripttion.hide();
			}
		},
		disableFinishBtn : function(){
			this.finishBtn.linkbutton('disable');
		},
		enableFinishBtn : function(){
			this.finishBtn.linkbutton('enable');
		},
		disableContinueBtn : function(){
			this.continueBtn.linkbutton('disable');
		},
		enableContinueBtn : function(){
			this.continueBtn.linkbutton('enable');
		},
		setContinueBtn : function(newName){
			this.continueBtn.find('.l-btn-text').html(newName);
		}
	};
	oc.ns('oc.module.resmanagement.discresource.singlediscover');
	var singlediscover = undefined;
	oc.module.resmanagement.discresource.singlediscover = {
		open : function(cfg){
			singlediscover = new singleDiscover(cfg);
			singlediscover.open();
		},
		selectPanel : function(index){
			singlediscover.selectPanel(index);
		},
		reloadResourceGroup : function(data){
			singlediscover.reloadResourceGroup(data);
		},
		disableFinishBtn : function(){
			singlediscover.disableFinishBtn();
		},
		enableFinishBtn : function(){
			singlediscover.enableFinishBtn();
		},
		disableContinueBtn : function(){
			singlediscover.disableContinueBtn();
		},
		enableContinueBtn : function(){
			singlediscover.enableContinueBtn();
		},
		setContinueBtn : function(newName){
			singlediscover.setContinueBtn(newName);
		}
	};
	oc.module.resmanagement.discresource.singlediscover.open();
});