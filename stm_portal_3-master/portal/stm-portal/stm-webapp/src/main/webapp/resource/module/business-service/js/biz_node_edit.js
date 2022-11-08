/*节点编辑*/
function BizNodeEdit(args){
	this.args = $.extend({
		canvasNodeId:null,			//节点Id，用于查询数据
		saveCallBack:function(){}	//编辑成功后回调函数
	},args);
	this.curStatus = "main";	//主资源：main（默认）、子资源：sub、指标：target
	this.nodeData = null;		//节点基本信息数据
	this.fontColor = "#ea4c63 !important;";	//提示颜色
	this.isTypeComboboxInit = false;	//类型初始化标志
	this.initTreeData = {		//记录ztree加载完后的红色节点数据
		subTree:[],
		targetTree:[]
	};		
	var ctx = this;
	oc.util.ajax({
		url:oc.resource.getUrl("resource/module/business-service/biz_node_edit.html"),
		success:function(html){
			ctx.init(html);
		},
		type:"get",
		dataType:"html"
	});
};

BizNodeEdit.prototype={
	init:function(html){
		var ctx = this;
		ctx.root = $(html);
		ctx.dialog = ctx.root.dialog({
			title:"编辑节点",
			width:750,height:570,
			draggable:true,
			cache: false,
			modal: true ,
			buttons:[{
				text:'确定',
				iconCls:'fa fa-check-circle',
				handler:function(){
					ctx.save();
				}
			},{
				text:'取消',
				iconCls:'fa fa-times-circle',
				handler:function(){
					ctx.dialog.dialog("close");
				}
			}]
		});
		//搜索全局域
		ctx._searchField("field");
		//初始化
		ctx._initUi();
		//注册事件
		ctx._regEvent();
		//加载节点基本信息
		ctx._loadNodeData();
	},
	//搜索域
	_searchField:function(type){
		var ctx = this;
		ctx[type]={};
		ctx.root.find("[data-"+type+"]").each(function(idx,dom){
			var tmp = $(dom);
			ctx[type][tmp.attr("data-"+type)]=tmp;
		});
	},
	//加载节点基本信息
	_loadNodeData:function(){
		var ctx = this;
		oc.util.ajax({
		  url: oc.resource.getUrl('portal/business/canvas/getInstanceNode.htm'),
		  data:{canvasNodeId:ctx.args.canvasNodeId},
		  successMsg:null,
		  success: function(data){
			  if(data && data.code == 200){
				 ctx.nodeData = data.data;	//节点信息
				 ctx._setNodeVal(data.data);
			  }else{
				  alert("查询节点数据异常");
			  } 	  	  
		  }
		});
	},
	//设置节点值
	_setNodeVal:function(data){
		this.field.nodeNameInput.val(data.showName);
		this.field.isHideNodeName.prop("checked",(data.nameHidden == 0?false:true));	//0:不隐藏，1：隐藏
		var picSrc = oc.resource.getUrl("platform/file/getFileInputStream.htm?fileId="+data.fileId);
		this.field.nodePic.attr("src",picSrc);
		//1：主资源（默认），2：子资源，3：指标
		if(2 == data.type){
			this.field.subRadio.click();
		}else if(3 == data.type){
			this.field.targetRadio.click();
		}
	},
	//渲染UI
	_initUi:function(){
		var ctx = this;
		this.nodeForm = oc.ui.form({selector:ctx.root.find("form:first")});	//此处需要这样初始化才能使用oc的验证框架
		this.field.selectPicBtn.linkbutton("RenderLB",{iconCls:"icon-browse"});
		this.field.searchBtn.linkbutton("RenderLB",{iconCls:"fa fa-search"});
		this.field.subResourceType.combobox({
			width:100,height:"auto",
			valueField: 'id',
			textField: 'name',
		});
	},
	//初始化指标tree
	_initTargetPicTree:function(){
		var ctx = this;
		if(ctx.targetPicTree) return;	//只加载一次
		
		var pickTreeCfg = ctx._getTargetPicTreeCfg(ctx.canvasNodeId);
		ctx.targetPicTree = oc.ui.picktree(pickTreeCfg);
	},
	_getTargetPicTreeCfg :function(canvasNodeId){
		var ctx = this;
		var pickTreeCfg = {
				selector:ctx.field.targetPickTree,
				dataType:'array',
				isInteractive:true,
				leftParam:{canvasNodeId:ctx.args.canvasNodeId},
				lUrl:oc.resource.getUrl('portal/business/canvas/getUnSelectMetricTree.htm'),
				leftFontCss:function(treeId, treeNode){
					return ctx._setFontColor("targetTree", treeNode);
				},
				rightParam:{canvasNodeId:ctx.args.canvasNodeId},
				rUrl:oc.resource.getUrl('portal/business/canvas/getSelectMetricTree.htm'),
				rightFontCss:function(treeId, treeNode){
					return ctx._setFontColor("targetTree", treeNode);
				},
				onMoveSuccess:function(left,right){
					ctx._resetFontColor("targetPickTree",ctx.initTreeData.targetTree);
				}
		};
		return pickTreeCfg;
	},
	//初始化子资源tree
	_initSubResourcePicTree:function(){
		var ctx = this;
		if(ctx.subResourcePickTree) return;	//只加载一次
		
		var pickTreeCfg = ctx._getSubResourcePicTreeCfg(ctx.args.canvasNodeId);
		ctx.subResourcePickTree = oc.ui.picktree(pickTreeCfg);
	},
	//获取子资源pictree配置
	_getSubResourcePicTreeCfg :function(){
		var ctx = this;
		var pickTreeCfg = {
				selector:ctx.field.subResourcePickTree,
				dataType:'array',
				isInteractive:true,
				leftParam:{canvasNodeId:ctx.args.canvasNodeId},
				lUrl:oc.resource.getUrl('portal/business/canvas/getUnSelectChildInstanceTree.htm'),
				leftFontCss:function(treeId, treeNode){
					return ctx._setFontColor("subTree", treeNode);
				},
				rightParam:{canvasNodeId:ctx.args.canvasNodeId},
				rUrl:oc.resource.getUrl('portal/business/canvas/getSelectChildInstanceTree.htm'),
				rightFontCss:function(treeId, treeNode){
					return ctx._setFontColor("subTree", treeNode);
				},
				leftCallback:{
					onAsyncSuccess:function(data){
						//防止点击左侧树内容过长后会撑宽布局
						$("#showZTreeInfoPanel").css("width",325);
					}
				},
				onMoveSuccess:function(left,right){
					ctx._resetFontColor("subResourcePickTree",ctx.initTreeData.subTree);
				}
		};
		return pickTreeCfg;
	},
	//左右移动后重新渲染颜色
	_resetFontColor:function(treeId,treeData){
		var ctx = this;
		var doms = $("div[data-field='"+treeId+"'] li a[title]");
		$.each(doms,function(index,nodeDom){
			var dom = $(this).find("span:last"),nodeName = dom.text();
			$.each(treeData,function(index,node){
				if(nodeName == node){
					dom.removeAttr("style");
					dom.attr("style","color:"+ctx.fontColor);
					return false;
				}
			});
		});
	},
	//设置告警颜色
	_setFontColor:function(treeId, treeNode){
		//0：启用告警，1：未启用告警（标红色）
		if(1 == treeNode.status) {
			if("subTree" == treeId){
				this.initTreeData.subTree.push(treeNode.name);
			}else{
				this.initTreeData.targetTree.push(treeNode.name);
			}
			return {"color":this.fontColor};
		}
	},
	//保存
	save:function(){
		var ctx = this;
		if(!ctx.nodeForm.validate()) return;
		
		var data = this._getSaveData();
		if(ctx.curStatus != "main" && (!data.bind || data.bind.length == 0)) {
			alert("未选择要保存的值","danger");
		}else{
			oc.util.ajax({
				url: oc.resource.getUrl('portal/business/canvas/updateInstanceNode.htm'),
				data:{instanceNode:data},
				success: function(data){
					if(data.data && data.code == 200){
						alert("保存成功");
						ctx.dialog.dialog("close");
						ctx.args.saveCallBack();
					}else{
						alert("保存失败");
					}
				}
			});
		}
	},
	_getSaveData:function(){
		var ctx = this;
		with(this.nodeData){
			showName =	ctx.field.nodeNameInput.val();
			nameHidden = ctx.field.isHideNodeName.prop("checked")?1:0;	//0:不隐藏，1：隐藏
			var picSrc = ctx.field.nodePic.attr("src");
			var index = picSrc.indexOf("=");
			if(picSrc && index >= 0) picSrc = picSrc.substring(index+1);
			fileId = picSrc;
			//主资源：main（默认）、子资源：sub、指标：target	(1：主资源（默认），2：子资源，3：指标)
			if("main" == this.curStatus){
				type=	1;
			}else if("sub" == this.curStatus){
				type=	2;
				bind = ctx._parseBindData("sub",ctx.subResourcePickTree.getRightTreeData());
			}else{
				type = 3;
				bind = ctx._parseBindData("target",ctx.targetPicTree.getRightTreeData());
			}
		}
		return ctx.nodeData;
	},
	//转换绑定值
	_parseBindData:function(type,bind){
		if(!bind || bind.length == 0) return bind;
		var bindTmp = [];
		$.each(bind,function(index,data){
			var param = {};
			if("sub" == type){	//只存储子节点
				if(data.pId != 0){
					param.childInstanceId = data.id;
					bindTmp.push(param);
				}
			}else{
				if(data.pId == 0){	//父节点指标
					var isParent = true;
					$.each(bind,function(index,subData){
						if(data.id == subData.pId){
							isParent = false;
							return false;
						}
					});
					
					if(isParent){
						param.metricId = data.id;
						bindTmp.push(param);
					}
				}else{	//子节点指标
					if(data.id.indexOf('_') >= 0){
						param.metricId = data.id.split('_')[0];
					}else{
						param.metricId = data.id;
					}
					param.childInstanceId = data.pId;
					bindTmp.push(param);
				}
			}
		});
		return bindTmp;
	},
	//注册事件
	_regEvent:function(){
		var ctx = this;
		ctx.field.nodeNameInput.keyup(function(){
			var regexp = /[*()&{}]/;	//特殊字符不允许输入
			var val = $(this).val();
			if(regexp.test(val)){           
			    $(this).val(val.replace(regexp,""));     
			} 
		});
		ctx.field.selectPicBtn.on("click",function(){
			oc.resource.loadScript("resource/module/business-service/js/biz_pic_replace.js", function(){
				var args = {callBack:function(newSrc){
					ctx.field.nodePic.attr("src",newSrc);
				}};
				new BizPicReplace(args);
			});
		});
		ctx.field.searchBtn.on("click",function(){
			var searchVal = $.trim(ctx.field.searchInput.val());
			var searchType = ctx.field.subResourceType.combobox('getValue');
			//未绑定子资源信息
			var url = "portal/business/canvas/getUnSelectChildInstanceTree.htm";
			//未绑定指标信息
			if("target" == ctx.curStatus) url = "portal/business/canvas/getUnSelectMetricTree.htm";
			
			oc.util.ajax({
				  url: oc.resource.getUrl(url),
				  data:{canvasNodeId:ctx.args.canvasNodeId,childResource:searchType,searchContent:searchVal},
				  successMsg:null,
				  success: function(data){
					  if(data){
						  if("sub" == ctx.curStatus){
							  ctx.subResourcePickTree.reload(data.data,'left');
							  ctx.subResourcePickTree.leftTreeObj.checkAllNodes(true);
							  ctx.subResourcePickTree.leftTreeObj.expandAll(true);
						  }else if("target" == ctx.curStatus){
							  ctx.targetPicTree.reload(data.data,'left');
							  ctx.targetPicTree.leftTreeObj.checkAllNodes(true);
							  ctx.targetPicTree.leftTreeObj.expandAll(true);
						  }
					  }		  	  	  
				  }
			});
		});
		ctx.field.searchInput.keyup(function(e){
			if(e.keyCode==13) ctx.field.searchBtn.click();
		});
		ctx.field.mainRadio.on("click",function(){
			ctx.curStatus = "main";
			//显示主资源div,隐藏主资源、指标div
			ctx.field.subDiv.hide();
			ctx.field.targetDiv.hide();
			ctx.field.toolbar.hide();
			ctx.field.targetTip.hide();
			ctx.field.mainDiv.show();
		});
		ctx.field.subRadio.on("click",function(){
			ctx.curStatus = "sub";
			ctx._loadComboboxData();	//加载搜索类型
			ctx._initSubResourcePicTree();
			//显示主资源div,隐藏主资源、指标div
			ctx.field.mainDiv.hide();
			ctx.field.targetDiv.hide();
			ctx.field.toolbar.show();
			ctx.field.targetTip.show();
			ctx.field.targetTip.text("红色资源表示未监控");
			ctx.field.targetTip.css("margin-right",113);
			ctx.field.subDiv.show();
		});
		ctx.field.targetRadio.on("click",function(){
			ctx.curStatus = "target";
			ctx._loadComboboxData();	//加载搜索类型
			ctx._initTargetPicTree();
			//显示主资源div,隐藏主资源、指标div
			ctx.field.mainDiv.hide();
			ctx.field.subDiv.hide();
			ctx.field.toolbar.show();
			ctx.field.targetDiv.show();
			ctx.field.targetTip.show();
			ctx.field.targetTip.css("margin-right",89);
			ctx.field.targetTip.text("红色指标表示未启用告警");
		});
	},
	//加载子资源、指标下拉框数据
	_loadComboboxData:function(){
		if(this.isTypeComboboxInit) return;	//共用一个只渲染一次
		
		var ctx = this;
		oc.util.ajax({
		  url: oc.resource.getUrl('portal/business/canvas/getChildResourceList.htm'),
		  data:{instanceId:ctx.nodeData.instanceId},
		  successMsg:null,
		  success: function(data){
			  if(data && data.code == 200){
				  data.data.unshift({"id":"","name":"--请选择类型--"});
				  ctx.field.subResourceType.combobox("loadData", data.data);
				  ctx.field.subResourceType.combobox("setValue",data.data[0].id);	//默认选中第一个
				  ctx.isTypeComboboxInit = true;
			  }else{
				  alert("查询资源类型异常");
			  } 	  	  
		  }
		});
	}
};