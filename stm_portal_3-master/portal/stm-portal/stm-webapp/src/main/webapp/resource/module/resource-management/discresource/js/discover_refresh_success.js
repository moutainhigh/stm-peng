$(function() {
	var interDiv=undefined;
	function refdiscoversuccess(cfg) {
		this.cfg = $.extend({}, this._defaults, cfg);
		var id = oc.util.generateId();
		this.successDiv = $("#refresh_result_success").attr('id', id);
	}

	refdiscoversuccess.prototype = {
		constructor : refdiscoversuccess,
		form : undefined, // 操作表单
		cfg : undefined, // 外界传入的配置参数 json对象格式
		successDiv : undefined,
		editShowNameflag : true,
		showName : undefined,
		dlg : undefined,
		interDiv:undefined,
		open : function() {
			var that = this, cfg = this.cfg, data = cfg.data;
			this._initSuccess(data);
		},
		refreshDis:function(){
			var that = this;
			var mainInstance = this.successDiv.find("input[name='instanceId']");
			var mainInstanceId = mainInstance.val();
			var childInstanceIds = new Array();
			var cancleInstanceIds=new Array();
			var delChildInstanceIds = new Array();
			this.successDiv.find("input[type='checkbox']:checked").each(function(){
				if($(this).val()=="on"){
				}else{
					childInstanceIds.push($(this).val());	
				}
				
			});
			this.successDiv.find("input[type='checkbox']").not("input:checked").each(function(){
				if($(this).attr('state')!='removed'){
					if($(this).val()=="on"){
					}else{
					cancleInstanceIds.push($(this).val());
					}
				}
				
			});
			this.successDiv.find('table tr').each(function(){
				if($(this).attr('isDel')==0){
					delChildInstanceIds.push($(this).attr('trid'));
				}
				
				
			});
			var newShowName = this.successDiv.find("input[name='newInstanceName']").val();
			if(newShowName == null || newShowName == undefined || newShowName == ''){
				alert('资源显示名称不能为空');
				return false;
			}
			// 提交资源加入监控
			oc.util.ajax({
				url : oc.resource.getUrl('portal/resource/discoverResource/refreshjoinMonitor.htm'),
				timeout : null,
				data : {
//					resourceGroupId : that.resourceGroup.jq.combobox('getValue'),
					newInstanceName : newShowName,
					mainInstanceId : mainInstanceId,
					childInstanceIds : childInstanceIds.join(","),
					delChildInstanceIds:delChildInstanceIds.join(","),
					cancleInstanceIds:cancleInstanceIds.join(",")
				},
				success : function(json){
					if(json.code == 200){
						if(json.data.status == '1'){
							alert('加入监控成功');
							mainInstance.val('');
						}else if(json.data.status == '0'){
							alert('加入监控失败');
						}else{
							alert('加入监控失败!');
						}
					}else{
						alert('加入监控失败');
					}
				}
			});
			
		},
		_initSuccess : function(data) {
			this.successDiv.panel({
				border : false
			});
			this._initValueResultSuccess(data);
			this._initAccordionResultSuccess(data);
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
			var checkflag=false;
			var isCheckAll=true;
			var childInstances = data.childInstance;
			// 计算页面显示高度
			var childNum = 0, maxChildNum = 6, outerAccorNodeHeight = 260, accorNodeHeight = 251, oneChildHeight = 60;
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
			var resultAccordion = childAccordionNode.accordion({
						border : false,
						fit : false,
						height : (outerAccorNodeHeight) + 'px',//accorNodeHeight + 'px',
						width : '100%'
					});
			var flag = true;
			var childInstancesTemp=new Array();
			for ( var instance in childInstances) {
		
				var contentStr = '';	var accordionTitle="";
				var childInsatnce = childInstances[instance];
				if(instance=="文件" || instance=="进程" || instance=="逻辑卷" || instance=="卷组"){
					continue;
				}
				var count = childInsatnce != null ? childInsatnce.length : 0;
				var unAvail = 0;
				for (var int = 0; null != childInsatnce && int < childInsatnce.length; int++) {
		
				if(childInsatnce[int].lifeState=='DELETED' && childInsatnce[int].State=='deleted'){//删除状态的子资源不显示
					 childInsatnce.splice(int,1);
					count--;
				}
				if(childInsatnce[int].lifeState=='DELETED' && childInsatnce[int].State==''){//删除状态的子资源不显示
					 childInsatnce.splice(int,1);
					count--;
				}
				if(childInsatnce[int].availability == '0'){//不可用
					unAvail++;
				}
				
				
				}
				/*var personDatagrid = *//*oc.ui.datagrid({
					pagination:false,
					selector:ContentStr,
					checkOnSelect:false,
					selectOnCheck:false,
					columns:[[
						 {field:'childInstanceId',checkbox:true,
	 						isDisabled:function(value, row, index){
	 						
						
				        }},
				         {field:'childInstanceId',title:'接口',width:80},
				         {field:'lifeState',title:'状态',width:80},
				          {field:'cz',title:'操作',width:80}
				     ]],
				     onLoadSuccess: function (data) {
				     
				  
				     }
				});*/
				
			/*	contentStr+="<div class='table-datanull'>";
				contentStr+="<table  id='seltab' class='datagrid-btable' border-collapse='separate' border='0' cellspacing='0' cellpadding='0' style='display:block'>";
				isCheckAll=true;
				$.each(childInstances[instance],function(item,i){
					if(i.lifeState=='NOT_MONITORED' && i.State!='add' ){
						isCheckAll=false;
					}
				});
				if(isCheckAll==true){
					contentStr+='<tr class="datagrid-header-row"><td style="text-align:left;width:500px;"><input type="checkbox"   name="pickSel"  n="'+instance+'" style="vertical-align:middle;" checked="checked" />名称</td><td style="text-align:left;width:100px;">状态</td><td style="text-align:left;width:100px;">操作</td><td ></td></tr>';
				}else{
				contentStr+='<tr class="datagrid-header"><td style="text-align:left;width:500px;"><input type="checkbox"   name="pickSel" n="'+instance+'" style="vertical-align:middle;" />名称</td><td style="text-align:left;width:100px;">状态</td><td style="text-align:left;width:100px;">操作</td><td ></td></tr>';
				}
				for (var int = 0; null != childInsatnce && int < childInsatnce.length; int++) {
					var name=childInsatnce[int].name;//'Realtek RTL8168D/8111D 系列 PCI-E 千兆以太网 NIC (NDIS 6.20)-QoS Packet Scheduler-0000千兆以太网 千兆以太网 千兆以太网 千兆以太网 ';
				//	console.info(childInsatnce[int].name);
					var titleName=name;
					if(name!=null){
						titleName=childInsatnce[int].name.replace('"','');
						if(name.length>=59){
							name=name.substr(0, 59)+"...";
						}
					}
					
				
					if(childInsatnce[int].lifeState=='DELETED' && childInsatnce[int].State=='deleted'  ){
						contentStr+="<tr class='datagrid-header-row' trid='"+childInsatnce[int].childInstanceId+"' style='display:none'>";
						contentStr+="<td style='text-align:left;width:500px;display:none'>";
					}else if(childInsatnce[int].lifeState=='DELETED' && childInsatnce[int].State=='' ){
						
					}else{
						contentStr+="<tr class='datagrid-header-row' trid='"+childInsatnce[int].childInstanceId+"' >";
						contentStr+="<td style='text-align:left;width:500px;'>";
					}
					
					
					
					//1.监控/未监控     未监控默认不选中，除此之外默认选中
					
				var	checkbox = "<div title='"+titleName+"'>" +
					"<input name='childInstance' bs='"+instance+"'  type='checkbox' checked='checked'   value= " + childInsatnce[int].childInstanceId +">"
					+ name +
			   "</div>";
					if(childInsatnce[int].lifeState == 'NOT_MONITORED' ){
						checkbox = "<div title='"+titleName+"' >" +
						"<input name='childInstance' bs='"+instance+"'  type='checkbox'   value= " + childInsatnce[int].childInstanceId +">"
						+ name +
			       "</div>";
					}
//					if(childInsatnce[int].lifeState=='NOT_MONITORED' && childInsatnce[int].State=='deleted'){//删除状态的子资源不显示 （未监控状态）
//						checkbox = "<div title='"+titleName+"' style='display:none' >" +
//						"<input name='childInstance' bs='"+instance+"'  type='checkbox'   value= " + childInsatnce[int].childInstanceId +">"
//						+ name +
//			       "</div>";
//						count--;
//						//$("tr[trid='"+childInsatnce[int].childInstanceId+"']").attr('style','display:none');
//					}
					if(childInsatnce[int].lifeState=='DELETED' && childInsatnce[int].State=='deleted'){//删除状态的子资源不显示
						checkbox = "<div title='"+titleName+"' style='display:none' >" +
						"<input name='childInstances' bs='"+instance+"'  type='checkbox'   value= " + childInsatnce[int].childInstanceId +">"
						+ name +
			       "</div>";
						count--;
						//$("tr[trid='"+childInsatnce[int].childInstanceId+"']").attr('style','display:none');
					}
					if(childInsatnce[int].lifeState=='DELETED' && childInsatnce[int].State==''){//删除状态的子资源不显示
						checkbox = "<div  title='"+titleName+"'  style='display:none'>" +
						"<input name='childInstance' bs='"+instance+"'   type='checkbox'   value= " + childInsatnce[int].childInstanceId +">"
						+ name +
						"</div>";
						count--;
						
					}
					if(childInsatnce[int].lifeState=='DELETED' && childInsatnce[int].State=='removed'){//删除状态的子资源显示
						checkbox = "<div  title='"+titleName+"' >" +
						"<input name='childInstance' bs='"+instance+"' state='removed'  type='checkbox' checked='checked'   value= " + childInsatnce[int].childInstanceId +">"
						+ name +
						"</div>";
						
					}
					if(childInsatnce[int].State == 'add'){//添加状态
						checkbox = "<div  title='"+titleName+"'>" +
						"<input name='childInstance' bs='"+instance+"'  type='checkbox'  checked='checked'   value= " + childInsatnce[int].childInstanceId +">"
						+name +
						"</div>";
					}
					//2.可用不可用   如果子资源不可用
					if(childInsatnce[int].availability == '0'){//不可用
						if(childInsatnce[int].lifeState == 'NOT_MONITORED' || childInsatnce[int].lifeState == 'DELETED'){//不可用未监控
							checkbox = "<div  style='color:red;' title='"+titleName+"'>" +
							"<input name='childInstance' bs='"+instance+"'   type='checkbox' value= " + childInsatnce[int].childInstanceId +">"
							+ name +
							"</div>";	
							
							
						}else{//不可用监控
							checkbox = "<div  style='color:red;' title='"+titleName+"'>" +
							"<input name='childInstance' bs='"+instance+"'   type='checkbox'  checked='checked'  value= " + childInsatnce[int].childInstanceId +">"
							+ name +
							"</div>";	
						}
					
						unAvail++;
					}
					
					var checkbox = "<div title='"+titleName+"'>" +
										"<input name='childInstance' bs='"+instance+"'  type='checkbox' checked='checked'   value= " + childInsatnce[int].childInstanceId +">"
										+ name +
								   "</div>";
					// 如果子资源未监控disabled
					if(childInsatnce[int].lifeState == 'NOT_MONITORED' ){//未监控
						checkbox = "<div title='"+titleName+"' >" +
										"<input name='childInstance' bs='"+instance+"'  type='checkbox'   value= " + childInsatnce[int].childInstanceId +">"
										+ name +
							       "</div>";
					}
					if(childInsatnce[int].State == 'add'){//添加状态
						checkbox = "<div  title='"+titleName+"'>" +
						"<input name='childInstance' bs='"+instance+"'  type='checkbox'  checked='checked'   value= " + childInsatnce[int].childInstanceId +">"
						+name +
						"</div>";
					}
					if(childInsatnce[int].State=="removed"){//缺失状态，都是之前监控上的，默认选中
						checkbox = "<div  title='"+titleName+"'>" +
						"<input name='childInstance' bs='"+instance+"'  type='checkbox'  checked='checked'   value= " + childInsatnce[int].childInstanceId +">"
						+name +
						"</div>";
						
					}

					//3.设置状态 
				contentStr+=checkbox+"</td>";
				if(childInsatnce[int].State!="" ){
					if(childInsatnce[int].State=="removed"){//缺失
						contentStr+="<td><span style='color:red;'>缺失</span></td>";
						contentStr+="<td><span class='ico ico-del' name='del' title='删除' value= " + childInsatnce[int].childInstanceId +"></span></td>";
					//	contentStr+="<td></td>";
					}else if(childInsatnce[int].State=="add"){//新增
						contentStr+="<td>新增</td>";
						contentStr+="<td></td>";
					}
				}else{
					contentStr+="<td></td>";
					contentStr+="<td></td>";
				}
				contentStr+="<td></td>";
				contentStr+="</tr>";
				}
				contentStr+="</table>"
					contentStr+="</div>";
				var accordionTitle = '<span style="padding-left:16px">'+instance+'</span>' + '(' + count + ')'
									+ (unAvail > 0 ? ("<span>&nbsp;&nbsp;不可用(" + unAvail + ")&nbsp;&nbsp;&nbsp;注：资源名称为红色表示不可用</span>") : "");*/
				var accordionTitle='<span style="padding-left:16px">'+instance+'</span>' + '(' + count + ')'
				+ (unAvail > 0 ? ("<span>&nbsp;&nbsp;不可用(" + unAvail + ")&nbsp;&nbsp;&nbsp;注：资源名称为红色表示不可用</span>") : "");
			
				resultAccordion.accordion('add', {
					title : accordionTitle,
					selected : flag,
					content :'<div n="'+instance+'" class="oc-datagrid"><div>'// childDiv
				});
				resultAccordion.find(".panel:last").find(".panel-title").css({"padding-top":"5px"});
				flag = false;
			 interDiv=	oc.ui.datagrid({
					pagination:false,
					selector:$("div[n='"+instance+"']"),
					checkOnSelect:false,
					selectOnCheck:false,
					columns:[[
						 {field:'childInstanceId',checkbox:true,
	 						isDisabled:function(value, row, index){
	 						
						
				        }},
				         {field:'name',title:'名称',width:80,
				        	 formatter : function(value, rowData, rowIndex) {
				        		 if(rowData.availability=='0'){//不可用
				        			 if(rowData.lifeState=='DELETED'){
				        				 if(rowData.State!='removed'){
				        					 return "";
				        				 }else{
				        					 return "<span style='color:red;'>"+rowData.name+"</span>"; 
				        				 }
				        			 }else{
				        				 return "<span style='color:red;'>"+rowData.name+"</span>"; 
				        			 }
										
									}else{
										 if(rowData.lifeState=='DELETED'){
					        				 if(rowData.State!='removed'){
					        					 return "";
					        				 }else{
					        					 return "<span >"+rowData.name+"</span>";
					        				 }
					        			 }else{
					        				 return "<span >"+rowData.name+"</span>"; 
					        			 }
										
									}
				        		 
								
					}},
				         {field:'State',title:'状态',width:80,
				        	 formatter : function(value, rowData, rowIndex) {
				        		 if(rowData.State=="removed"){//缺失
										return "<span style='color:red;'>缺失</span>";
									//	contentStr+="<td></td>";
									}else if(rowData.State=="add"){//新增
										return "<span>新增</span>";
									}
				        		 
								
					}},
				          {field:'cz',title:'操作',width:80,
				        	formatter : function(value, rowData, rowIndex) {
				        	
					        		 if(rowData.State=="removed"){//缺失
											return "<span class='ico ico-del' name='del' title='删除' value= " + rowData.childInstanceId +"></span>";
										}else if(rowData.State=="add"){//新增
											return "";
										}
					        		 
									
						}}
				     ]],
				     onLoadSuccess: function (data) {
				    	  var rowData = data.rows;
				    	     $.each(rowData,function(index, value){
				    	    	 var row=rowData[index];
							
							  if(row.lifeState=="MONITORED"){//监控，默认已选中
								  $("div[n='"+instance+"']").datagrid("checkRow", index);
								  
							  }else if(row.State == 'add'){//新增状态，默认选中
								  $("div[n='"+instance+"']").datagrid("checkRow", index); 
								  
							  }//childInsatnce[int].State=='removed' 待确认
							     });
				  
				     }
				});
				var localData = {"data":{"total":0,"rows":childInsatnce},"code":200};
				
				interDiv.selector.datagrid('loadData',localData);	
			}
		
			
			
//删除子资源	
$('span[name="del"]').on('click',function(){
	var instanceId=$(this).attr('value');
	  oc.ui.confirm("清除该子资源的所有历史信息？",function() {
			oc.util.ajax({
				url : oc.resource.getUrl("portal/resource/discoverResource/delResounceInstance.htm"),
				data : {instanceId:instanceId},
				success : function(json){
					if(json.code == 200){
						$("span[value='"+instanceId+"']").parent().parent().parent().css('display','none');
						$("span[value='"+instanceId+"']").parent().parent().parent().attr('isDel',0);
						$("span[value='"+instanceId+"']").parent().parent().parent().attr('trid',instanceId);
						$('input[value="'+instanceId+'"]').removeAttr("checked");
						alert('清除成功');
					}else{
						alert('清除失败');
					}
				}
			});
	  });
	
});
			

		},


		
		_close : function(){
			this.dlg.dialog('close');
		},
		_defaults : {}
	};
	// 命名空间
	oc.ns('oc.module.resmanagement.refreshdiscresource');
	var success = undefined;
	// 对外提供入口方法
	oc.module.resmanagement.discresource.refreshdiscresource = {
		
		open : function(cfg) {
			 success = new refdiscoversuccess(cfg);
			success.open();
		},refreshDis : function(cfg){
			if(success != undefined){
				success.refreshDis(cfg);
			}
		}
	};
});