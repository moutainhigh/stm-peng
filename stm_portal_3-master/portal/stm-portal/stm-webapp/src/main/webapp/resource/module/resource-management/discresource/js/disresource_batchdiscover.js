$(function() {
	function batchDiscover(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		var id = oc.util.generateId();
		this.batchDiv = $("#batchDiv").attr('id', id);
	}
	batchDiscover.prototype = {
		constructor : batchDiscover,
		batchDiv : undefined,
		domain : undefined,
		dcs : undefined,
		timeId : undefined,
		isBatchDiscoveryCancel : undefined,
		progressDlg : undefined,
		open : function() {
			this._initForm();
			this.batchDiv.panel({
				fit : true
			});
			this._init();
		},
		_init : function() {
			var that = this;
			
			var form = oc.ui.form({
				selector : this.batchDiv.find("form[name='batchDiscForm']")
			});
			var resultDiv = this.batchDiv.find(".batchDiscResult");
			resultDiv.hide();
			this.dcs = oc.ui.combobox({
				selector : this.batchDiv.find("[name='dcs']"),
				placeholder : false
			});
			var domains = oc.index.getDomains();
			var firstDomainId = typeof(domains[0]) == "undefined" ? '' : domains[0].id;
			this.domain = oc.ui.combobox({
				selector : this.batchDiv.find("[name='domain']"),
				placeholder : false,
				data : domains,
				value : firstDomainId,
				onLoadSuccess : function(){
					that.loadDscData(oc.index.getDomains()[0]);
				},
				onSelect : function(record){
					that.loadDscData(record);
				}
			});
			// 模版文件下载
			var downloadtemplatePath = oc.resource.getUrl("portal/resource/discoverResource/downloadTemplateFile.htm");
			this.batchDiv.find(".downloadTemplateBtn").attr('href', downloadtemplatePath);
			// 结果文件下载
			var downloadresultPath = oc.resource.getUrl("portal/resource/discoverResource/downloadBatchResultFile.htm");
			this.batchDiv.find(".downloadBatchResultBtn").attr('href', downloadresultPath).linkbutton('RenderLB');
			// 文件上传
			this.batchDiv.find(".chooseFile").linkbutton("RenderLB", {
				height : '22px',
				width : '52px'
			});
			var filebox = this.batchDiv.find(".showExcelFilePath");
			this.batchDiv.find("input[name='file']").change(function(){
				var docObj = $(this).get(0);
				filebox.val($(this).val());
			});
			
			var options = {
				url : oc.resource.getUrl("portal/resource/discoverResource/batchDiscoverResouce.htm"),
				type : 'POST',
				target : '#output',
				iframe : true,
				dataType : 'json',
				//timeout: 3000,
				success : function(responseText, statusText, xhr, $form) {
					if(statusText == 'success' && responseText.code == 200){
						that.batchDiv.find(".batchDiscResult").show();
						var timeNode = that.batchDiv.find(".batchDiscTime");
						timeNode.html(responseText.data.time);
						var countTitle = '数量';
						if(responseText.data.isErrorMsg){
							countTitle = '错误信息';
						}
						var dg = that.batchDiv.find(".batchDiscResultDatagrid");
						var grid = oc.ui.datagrid({
							selector : dg,
							pagination : false,
							singleSelect : true,
							columns : [ [ {
								field : 'code',
								title : '结果代码',
								hidden : true
							},{
								field : 'result',
								title : '结果',
								width : '50%'
							}, {
								field : 'count',
								title : countTitle,
								width : '50%',
								formatter:function(value,row,rowIndex){
									if(row.code == '1' && value != null && value != undefined
											&& value != '' && parseFloat(value) > 0){
										oc.module.resmanagement.discresource.disc.setDiscoveryFlag();
									}
									return value;
								}
							} ] ]
						});
						var isAllowUpdate = true;
			        	$(window).resize(function() {
			        		if(isAllowUpdate){
			        			isAllowUpdate = false;
			        			grid.selector.datagrid();
			        			var item = grid.selector.datagrid('getRows'); 
			        			if (item) { 
			        				for (var i = item.length - 1; i >= 0; i--) { 
			        					var index = grid.selector.datagrid('getRowIndex', item[i]); 
			        					grid.selector.datagrid('deleteRow', index); 
			        				}
			        			} 
			        			var object = responseText.data.datagrid[i];
			        			for(var i = 0; i < responseText.data.datagrid.length; i++){
			        				grid.selector.datagrid('appendRow',responseText.data.datagrid[i]);
			        			}
			        			isAllowUpdate = true;
			        		}
						});
						var dlResultBtn = that.batchDiv.find(".downloadBatchResultBtn");
						if(responseText.data.datagrid.length == 1){
							dlResultBtn.linkbutton('disable');
						}else{
							dlResultBtn.linkbutton('enable');
						}
						var datagridData = '{"data":{"startRow":0,"rowCount":10,"totalRecord":0,"total":2,"rows":[';
						for(var i = 0; i < responseText.data.datagrid.length; i++){
							object = responseText.data.datagrid[i];
							datagridData += '{';
							for(var key in object){
								datagridData += '"' + key + '":"' + object[key] + '",';
							}
							datagridData = datagridData.substring(0, datagridData.lastIndexOf(','));
							datagridData += '}';
							if(i < responseText.data.datagrid.length - 1){
								datagridData += ',';
							}
						}
						datagridData += ']},"code":200}';
						dg.datagrid('loadData', jQuery.parseJSON(datagridData));
						
						// 关闭按钮
						that.batchDiv.find(".btnDiscCloseDlgBtn").linkbutton('RenderLB', {
							onClick : function(){
								oc.module.resmanagement.discresource.disc.close();
							}
						}).show();
					}
					that.clearDiscoverInterval();
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
					that.clearDiscoverInterval();
				}
			};
			this.batchDiv.find(".discoverBtn").linkbutton('RenderLB', {
				onClick : function() {
					if(that.domain.jq.combobox('getValue') == ''){
						that.domain.jq.combobox().next('span').find('input').focus();
						return false;
					}
					if(that.dcs.jq.combobox('getValue') == ''){
						that.dcs.jq.combobox().next('span').find('input').focus();
						return false;
					}
					if(filebox.val() != null && filebox.val() != ''){
						resultDiv.hide();
						that.batchDiv.find("form[name='batchDiscForm']").ajaxSubmit(options);
						that.getDiscoverStatus();
					} else {
						filebox.focus();
						alert('上传Excel文件不能为空');
						return false;
					}
				}
			});
		},
		
		_initForm:function(){
			var that = this;
			this.form=oc.ui.form({
				selector:this.batchDiv.find("form[name='batchDiscForm']"),
				filebox:[{
					selector:'[name=file]',
					width:160,
					onChange:function(newValue,oldValue){
						that.form.filebox[0].selector.textbox('setText',newValue.substr(newValue.lastIndexOf("\\") + 1));	
						
					}
				}]
			});
		},
		loadDscData : function(record){
			var array = new Array();
			for(var index in record.dcsNodes){
				var dcs = record.dcsNodes[index];
				var dcsObj = {};
				dcsObj.id = dcs.groupId;
				dcsObj.name = dcs.name;
				array[array.length] = dcsObj;
			}
			this.dcs.jq.combobox('loadData', array);
			if(array.length == 0){
				this.dcs.jq.combobox('setText', '');
				this.batchDiv.find("[name='dcs']").val('');
			}else{
				this.dcs.jq.combobox('setValue', array[0].id);
			}
		},
		getDiscoverStatus : function(){
			var that = this;
			this.isBatchDiscoveryCancel = false;
			var content = $("<div/>").addClass('l-btn-tanbg');
			this.progressDlg = $("<div/>").append(content).dialog({
				title : '发现信息',
				width : '300px',
				height : '140px',
				closable : false,
				buttons : [{
					text : '取消',
					handler : function(){
						content.empty();
						that.isBatchDiscoveryCancel = true;
						content.append($("<div>").html('正在取消...'));
						oc.util.ajax({
							url : oc.resource.getUrl("portal/resource/discoverResource/batchDiscoverCancel.htm"),
							timeout : null,
							success : function(json){
								if(json.code != 200){
									alert('取消失败');
								}
							}
						});
					}
				}]
			});
			content.css({
				"width" : "100%",
				"height" : "100%"
			}).html("正在发现...");
			//clearInterval(that.timeId);//清定时器
			this.timeId = setInterval(function(){
				$.ajax({
					url:oc.resource.getUrl("portal/resource/discoverResource/batchDiscoverStatus.htm"),
					success:function(json){
						var code = json.code;
						var data = json.data;
						if(code == 200){
							if(!data.batchDiscoverFinish){
								if(data.batchDiscoverAmount != null && data.batchDiscoverAmount != undefined && !that.isBatchDiscoveryCancel){
									content.empty();
									var amountDiv = $("<div/>").html("待发现总数：" + data.batchDiscoverAmount + "个");
									var successDiv = $("<div/>").html("已发现成功：" + data.batchDiscoverSuccessNum + "个");
									var failureDiv = $("<span/>").html("已发现失败：" + data.batchDiscoverFailureNum + "个").css("float", "left");
									var progressDiv = $("<span/>").addClass("oc-icon pagination-loading");
									content.append(amountDiv).append(successDiv).append(failureDiv).append(progressDiv);
								}
							}else{
								that.clearDiscoverInterval();
							}
						}else{
							that.clearDiscoverInterval();
						}
					},
					error:function(){
						that.clearDiscoverInterval();
					},
					dataType:"json",
					method:"get"
				});
			}, 2000);
			var tasks = oc.index.indexLayout.data("tasks");
			if(tasks && tasks.length > 0){
				oc.index.indexLayout.data("tasks").push(this.timeId);
			}else{
				tasks = new Array();
				tasks.push(this.timeId);
				oc.index.indexLayout.data("tasks", tasks);
			}
		},
		clearDiscoverInterval : function(){
			var that = this;
			that.progressDlg.dialog("close");
			clearInterval(that.timeId);//清定时器
		},
		_defaults : {}
	};

	oc.ns('oc.module.resmanagement.discresource');
	oc.module.resmanagement.discresource.batchdiscover = {
		open : function(cfg) {
			var batchDisc = new batchDiscover(cfg);
			batchDisc.open();
		}
	};
});