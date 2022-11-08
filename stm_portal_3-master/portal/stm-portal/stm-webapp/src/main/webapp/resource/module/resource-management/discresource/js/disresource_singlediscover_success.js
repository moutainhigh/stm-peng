$(function() {
	function discoversuccess(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		var id = oc.util.generateId();
		this.successDiv = $("#singleDisc_result_success").attr('id', id);
	}
	discoversuccess.prototype = {
		constructor : discoversuccess,
		form : undefined, // 操作表单
		cfg : undefined, // 外界传入的配置参数 json对象格式
		successDiv : undefined,
		editShowNameflag : true,
		showName : undefined,
		dlg : undefined,
		open : function() {
			var that = this, cfg = this.cfg, data = cfg.data;
			this._initSuccess(data);
			this._createLinkButton();
		},
		_initSuccess : function(data) {
			this.successDiv.panel({
				border : false
			});
			this._initValueResultSuccess(data);
			this._initAccordionResultSuccess(data);
			this._confirmRepeat(data);
		},
		_initValueResultSuccess : function(data) {
			var that = this;
			var instanceId = this.successDiv.find("input[name='instanceId']");
			instanceId.val(data.instanceId);
			var discoverTime = this.successDiv.find(".discoverTime");
			discoverTime.html(data.time);
			var instanceName = this.successDiv.find(".instanceName");
			instanceName.html(data.instanceName.htmlspecialchars());
			// 备用名称用做资源名称修改时
			this.showName = data.instanceShowName;
			var hiddenShowName = this.successDiv.find(".hiddenShowName").hide();
			hiddenShowName.width('150px').html(this.showName.htmlspecialchars()).attr('title', this.showName);
			var newInstanceName = this.successDiv.find("input[name='newInstanceName']");
			newInstanceName.val(this.showName).width('150px')
				.validatebox({
					validType : 'maxLength[30]'
				});
			// 修改显示名称按钮
			var showNameEditBtn = this.successDiv.find(".showNameEditBtn").addClass('uodatethetext').hide();
			showNameEditBtn.on('click', function(){
				if(that.editShowNameflag){
					newInstanceName.val(that.showName).show();
					hiddenShowName.hide();
					showNameEditBtn.removeClass('uodatethetext').addClass('uodatethetextok');
					that.editShowNameflag = false;
				}else{
					var newShowName = newInstanceName.val();
					var isSubmit = newShowName != undefined && newShowName.replace(/(^\s*)(\s*$)/g, '') != '';
					if(newInstanceName.validatebox('isValid') && isSubmit){
						oc.util.ajax({
							url : oc.resource.getUrl("portal/resource/discoverResource/updateInstanceName.htm"),
							data : {newInstanceName:newShowName,instanceId:instanceId.val()},
							success : function(json){
								if(json.code == 200){
									if(json.data == 0){
										alert('修改显示名称失败');
									}else if(json.data == 1){
										alert('修改显示名称成功');
										that.showName = newInstanceName.val();
										hiddenShowName.html(that.showName.htmlspecialchars())
											.attr('title', that.showName).show();
										newInstanceName.hide();
										showNameEditBtn.removeClass('uodatethetextok').addClass('uodatethetext');
										that.editShowNameflag = true;
									}else{
										alert('资源显示名称重复');
									}
								}else{
									alert('修改显示名称失败');
								}
							}
						});
					}
				}
			});
			// 资源类型
			var instanceType = this.successDiv.find(".instanceType");
			instanceType.html(data.instanceType);
			// IP地址
			var ipValue = data.instanceIP.length > 0 ? data.instanceIP[0].id : '';
			var instanceIP = oc.ui.combobox({
				selector : that.successDiv.find("select[name='instanceIP']"),
				placeholder : false,
				data : data.instanceIP,
				value : ipValue
			});
		},
		_initAccordionResultSuccess : function(data) {
			var childInstances = data.childInstance;
			// 计算页面显示高度
			var childNum = 0, maxChildNum = 6, outerAccorNodeHeight = 260, accorNodeHeight = 251, oneChildHeight = 70;
			for ( var instance in childInstances) {
				childNum ++;
			}
			if(childNum > maxChildNum){
				outerAccorNodeHeight = outerAccorNodeHeight + ((childNum - 6) * oneChildHeight);
				accorNodeHeight = accorNodeHeight + ((childNum - 6) * oneChildHeight);
			}
			// 页面资源展示
			var childAccordionOuterNode = this.successDiv.find(".singleDisc_result_success_childinstance_div").height(outerAccorNodeHeight + 'px');
			childAccordionOuterNode.empty();
			var childAccordionNode = $("<div/>").addClass("singleDisc_result_success_childinstance");
			childAccordionOuterNode.append(childAccordionNode);
			accorNodeHeight =accorNodeHeight +9;
			var resultAccordion = childAccordionNode.accordion({
						border : false,
						fit : false,
						height : accorNodeHeight + 'px',
						width : '100%'
				});
			var flag = true;
			for ( var instance in childInstances) {
				var contentStr = '';
				var childInsatnce = childInstances[instance];
				var count = childInsatnce != null ? childInsatnce.length : 0;
				var unAvail = 0;
				for (var int = 0; null != childInsatnce && int < childInsatnce.length; int++) {
					var checkbox = "<div class='discResourceChildResourceStyle'>" +
										"<input name='childInstance' type='checkbox' checked='checked' value= " + childInsatnce[int].childInstanceId +">"
										+ childInsatnce[int].name +
								   "</div>";
					// 如果子资源不可用
					if(childInsatnce[int].availability == '0'){
						checkbox = "<div class='discResourceChildResourceStyle' style='color:red;'>" +
										"<input name='childInstance' type='checkbox' value= " + childInsatnce[int].childInstanceId +">"
										+ childInsatnce[int].name +
							       "</div>";
						unAvail++;
					}
					contentStr += checkbox;
				}
				var accordionTitle = instance + '(' + count + ')'
									+ (unAvail > 0 ? ("<span>&nbsp;&nbsp;不可用(" + unAvail + ")&nbsp;&nbsp;&nbsp;注：资源名称为红色表示不可用</span>") : "");
				resultAccordion.accordion('add', {
					title : accordionTitle,
					selected : flag,
					content : contentStr
				});
				flag = false;
			}
		},
		_createLinkButton : function(){
			var that = this;
			// 加入监控
			this.successDiv.find(".joinMonitor").linkbutton('RenderLB', {
				onClick : function(){
					that._initJoinMonitorPanel();
				}
			});
			// 继续发现
			this.successDiv.find(".continueDisc").linkbutton('RenderLB', {
				onClick : function(){
					oc.module.resmanagement.discresource.singlediscover.selectPanel(1);
				}
			});
			// 完成
			this.successDiv.find(".finish").linkbutton('RenderLB', {
				onClick : function(){
					oc.module.resmanagement.discresource.disc.close();
				}
			});
		},
		_initJoinMonitorPanel : function(){
			var that = this;
			var instanceId = that.successDiv.find("input[name='instanceId']").val();
			var dlg = $("<div />");
			this.dlg = dlg;
			var form = $("<div style='padding:25px;' />");
			var label = $("<label>选择监控策略：</label>");
			var select = $("<select />");
			dlg.append(form.append(label).append(select));
			 var profiledlg = oc.ui.combobox({
				selector : select,
				placeholder : false,
				url:oc.resource.getUrl('portal/resource/discoverResource/getProfileType.htm?instanceId='+instanceId),
				filter:function(data){
					return data.data;
				},
				value : "1"
			});
			dlg.dialog({
				title : '监控策略',
				width : 350,
				height : 180,
				draggable : false,
				buttons : [{
					text : '确定',
					handler : function(){
						var profileId = select.combobox('getValue');
						oc.util.ajax({
							url : oc.resource.getUrl('portal/resource/discoverResource/joinMonitor.htm'),
							data : {instanceId:instanceId,profileId:profileId},
							success : function(json){
								if(json.code == 200 && json.data.status == '1'){
									alert('加入监控成功');
									that._close();
								}else{
									alert('加入监控失败');
								}
							}
						});
					}
				},{
					text : '取消',
					handler : function(){
						that._close();
					}
				}]
			});
		},
		_confirmRepeat : function(data){
			var that = this;
			if(data.repeatPrompt == true){
				var repeatDlg = $("<div/>");
				repeatDlg.append($("<div/>").addClass("messager-icon messager-warning"))
				.append($("<div/>").css('top', '23px').addClass("oc-notice").html("重复发现资源，请确认处理方式！"));
				repeatDlg.dialog({
					title : '温馨提示',
					closable : false,
					width : '300px',
					height : '145px',
					buttons : [{
						text : '&nbsp;新增&nbsp;',
						handler : function(){
							that.handleRepeatRes(repeatDlg, '0');
						}
					},{
						text : '&nbsp;更新&nbsp;',
						handler : function(){
							that.handleRepeatRes(repeatDlg, '1');
						}
					},{
						text : '&nbsp;取消&nbsp;',
						handler : function(){
							that.handleRepeatRes(repeatDlg, '2');
						}
					}]
				});
			}
		},
		// method (0、新增 1、刷新 2、取消)
		handleRepeatRes : function(repeatDlg, method){
			var that = this;
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/discoverResource/handleRepeatInstance.htm'),
				data : {method : method},
				timeout : null,
				success : function(json){
					if(parseInt(method) == 0 || parseInt(method) == 1){
						if (json.code == 200 && json.data.status == '1'){
							that.successDiv.find("input[name='instanceId']").val(json.data.resourceId);
							that._initAccordionResultSuccess(json.data);
							alert('成功');
						} else {
							that.successDiv.find("input[name='instanceId']").val('');
							that.successDiv.find("input[name='newInstanceName']").attr('readonly', 'readonly');
							alert('失败');
						}
					}else{
						if (!json.code == 200 || !json.data.status == '1'){
							alert('出错');
						}
						that.successDiv.find("input[name='instanceId']").val('');
						that.successDiv.find("input[name='newInstanceName']").attr('readonly', 'readonly');
					}
					repeatDlg.dialog('close');
				}
			});
		},
		_close : function(){
			this.dlg.dialog('close');
		},
		_defaults : {}
	};
	// 命名空间
	oc.ns('oc.module.resmanagement.discresource');
	// 对外提供入口方法
	oc.module.resmanagement.discresource.discoversuccess = {
		open : function(cfg) {
			var success = new discoversuccess(cfg);
			success.open();
		}
	};
});