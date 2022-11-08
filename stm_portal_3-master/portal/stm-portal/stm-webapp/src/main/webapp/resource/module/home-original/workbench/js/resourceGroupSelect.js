(function(){
	var resourceGroup;
	function ResourceGroup(cfg){
		this.cfg = $.extend({},cfg);
	}
	
	ResourceGroup.prototype={
			constructor:ResourceGroup,
			_mainDiv:undefined,
			cfg:undefined,
			_dialog:undefined,
			_GroupAccordion : undefined,
			open:function(fn){
				var that = this,dlg = that._mainDiv = $('<div/>');
				that._dialog = dlg.dialog({
					href : oc.resource.getUrl('resource/module/home/workbench/resourceGroupSelect.html'),
					title : that.cfg.title+"选择资源组",
					width:320,
					height : 400,
					resizable : false,
					cache : false,
					onLoad : function() {
						oc.util.ajax({
							url:oc.resource.getUrl("home/workbench/resource/getResourceGroupByUser2Tree.htm"),
							successMsg:"",
							success:function(data){
								var result = eval(data);
								that.createOrRefreshGroup(result, true);
								
								if(result.code == 200 && that.cfg.selectValue != undefined){
									that.cfg.selectValue = that.hasIdInAllTreeData(result.data, that.cfg.selectValue) ? that.cfg.selectValue : 0;
								}
								// 如果已有选择
								if(that.cfg.selectValue!=undefined){
									that._mainDiv.find(".content").find("[type=radio]:[name=resourceGroup]").each(function(index){
										var thisValue = $(this).val();
										if(thisValue==that.cfg.selectValue){
											$(this).attr("checked",true);
										}else{
											$(this).attr("checked",false);
										}
									});
								}
							}
						});
					},
					onClose:function(){
						dlg.dialog('destroy');
					},
					buttons : [{
						text:"确定",
						iconCls:"fa fa-check-circle",
						handler:function(){
							var checkedval = that._mainDiv.find(".content").find("[type=radio]:[name=resourceGroup]:checked");
							if(that.cfg.fn){
								that.cfg.fn(checkedval.val(),checkedval.attr('title'));
							}
							dlg.dialog('close');
						}
					},{
						text:"取消",
						iconCls:"fa fa-times-circle",
						handler:function(){
							dlg.dialog('close');
						}
					}]
				});
			},
			hasIdInAllTreeData : function(datas, id){
				var that = this;
				for(var i = 0; i < datas.length; i++){
					var data =datas[i];
					if(data.id == id || id == 0){
						return true;
					}else{
						if(data.childCustomGroupVo.length > 0){
							if(that.hasIdInAllTreeData(data.childCustomGroupVo, id)){
								return true;
							}
						}
					}
				}
				return false;
			},
			createOrRefreshGroup : function(result, selected){
				var that = this, accordionDiv = $("<div/>"), accordionOuterDiv = $("<div/>"), accordionOuterHeight = 280;
				that._mainDiv.find(".content").css({'overflow-y':'auto'}).append(accordionOuterDiv.append(accordionDiv));
				accordionOuterDiv.css({
					'height' : result.data.length < 6 ? (accordionOuterHeight + 'px') : (result.data.length * 56 + 'px')
				});
				that._GroupAccordion = accordionDiv.accordion({
					fit : true
				});
				for(var i = 0; i < result.data.length; i++){
					var data = result.data[i];
					that.customGroupAddAccordion(data, selected);
					selected = false;
				}
			},
			customGroupAddAccordion : function(data, selected){
				var that = this;
				var contentDiv = $("<ul  customGroupId='" + data.id + "'/>");
				that._GroupAccordion.accordion('add',{
					selected : selected,
					title : data.name,
					content : contentDiv
				});
				that.createCustomGroupTree(contentDiv, data);
			},
			createCustomGroupTree : function($TreeDiv, data){
				var that = this;
				$TreeDiv.tree({
					fit : true,
					data : [that.createCustomGroupTreeData(data)],
					formatter : function(node){
						return '<div class="select" radio>'+
						'<label><input type="radio" value="'+node.id+'" name="resourceGroup" title="'+node.name+'">'+node.name+'</label>'+
						'</div>'
					}
				});
			},
			createCustomGroupTreeData : function(data){
				var that = this;
				var parentData = {
					id : data.id,
					name : data.name,
					text : data.name,
					pid : data.pid,
					resourceIds : data.resourceInstanceIds,
					children : []
				};
				for(var i = 0; data.childCustomGroupVo && i < data.childCustomGroupVo.length; i++){
					var customGroupVo = data.childCustomGroupVo[i];
					parentData.children.push(that.createCustomGroupTreeData(customGroupVo));
				}
				return parentData;
			}
	}
	
	oc.ns('oc.home.workbench.resourcegroup');
	oc.home.workbench.resourcegroup={
		open:function(cfg){
			resourceGroup=new ResourceGroup(cfg);
			resourceGroup.open();
		}
	};
})();