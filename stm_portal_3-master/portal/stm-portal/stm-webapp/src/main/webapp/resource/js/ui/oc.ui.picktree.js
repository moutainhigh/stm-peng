/**
 * cfg{
 * 	selector:容器选择器,
 * 	leftUrl:左边树URL，
 * 	rightUrl:右边树URL，
 * 	leftParam:左边树额外参数
 * 	rightParam:右边树额外参数
 * 	leftNodes:左边树本地数据(本地数据优先)
 * 	rightNodes:右边树本地数据
 *  leftTreeObj:左边树对象
 *  rightTreeObj:右边树对象
 *  leftFontCss:object || function(treeId, treeNode){return {color:"red"};}
 * 	rightFontCss:object || function(treeId, treeNode){return {color:"red"};}
 * 	dataType:获取数据格式，默认json标准json格式;array:简单数组格式,默认array
 * 	isInteractive:是否显示中间可移动组件，默认显示
 * 	async：true/false 设置请求为异步或同步,默认为true异步
 * 	leftCallback:{}
 * 	rightCallback:{}
 * 	onMoveSuccess(leftTree,rightTree)
 * }
 * 
 * 方法：
 * 	getLeftTreeData() 获取左边树所有数据，返回数据格式通过dataType来决定;
 * 	getRightTreeData() 获取右边树所有数据，返回数据格式通过dataType来决定;
 * 	getLeftTreeeCheckedData() 获取左边树中复选框选中的数据，返回数据格式通过dataType来决定;
 * 	getRightTreeeCheckedData() 获取右边树中复选框选中的数据，返回数据格式通过dataType来决定;
 * 	queryTree(queryString) 通过节点名称快速定位节点位置，queryString 要定位的节点名称
 * 	reload(data,position) 重新加载tree数据，data要加载的数据，position（l/left|r/right)左边或右边
 * 	
 * 
 * @author zhangjunfeng
 */
(function($) {
	var myPickTree;
	function picktree(cfg) {
		if (this.checkCfg(cfg)) {
			this._cfg = cfg;
			this.selector = (typeof cfg.selector == 'string') ? $(cfg.selector): cfg.selector;
			this._initPanel(cfg);
			this._initTree();
		}
	}
	
	function moveTreeNode(srcTree, targetTree) {
		var srcObj = $.fn.zTree.getZTreeObj(srcTree);
		var srcNode = srcObj.getCheckedNodes(true);
		if (!srcNode || srcNode == null || srcNode.length == 0) {
			alert("请先选择需要移动的节点！");
			return false;
		}
		oc.ui.progress();
		setTimeout(function(){
			//删除已经被添加的节点
			for (var i = 0; i < srcNode.length; i++) {
				if (srcNode[i].check_Child_State != 1
						&& srcNode[i].check_Child_State != 0) {
					srcObj.removeNode(srcNode[i]);
				}
			}
			var targetObj = $.fn.zTree.getZTreeObj(targetTree);
			var srcSimNodes = getCheckedData([], srcNode);
			var mySrcSimNode = [];
			for(var k = 0;k<srcSimNodes.length;k++){
				var isNode = targetObj.getNodesByParam("id", srcSimNodes[k].id);
				if (isNode == null || isNode.length == 0) {
					mySrcSimNode.push(srcSimNodes[k]);
				}
			}
			//构建要添加的节点树
			var srcJsonNode = targetObj.transformTozTreeNodes(mySrcSimNode);
			for(var j = 0;j<srcJsonNode.length;j++){
				var targetNode = targetObj.getNodesByParam("id",srcJsonNode[j].pId,null);
				targetObj.addNodes(targetNode[0],srcJsonNode[j]);
				if(targetNode[0]){
					checkParentNode(targetObj,targetNode[0].id);
				}
			}
			if(myPickTree._cfg.onMoveSuccess){
				myPickTree._cfg.onMoveSuccess(myPickTree.getLeftTreeData(),myPickTree.getRightTreeData());
			}
			$.messager.progress('close');
		},350);
	}
	
	function checkParentNode(nodeObj,pid){
		var cnodes = nodeObj.getNodesByParam("id",pid,null);
		if(cnodes){
			var cnode = cnodes[0];
			nodeObj.checkNode(cnode,true,false);
			if(cnode.pId!=null && cnode.pId!=0){
				checkParentNode(nodeObj,cnode.pId);
			}
		}
	}
	
	// 获取复选框选中的数据
	function getCheckedData(arry, json) {
		for (var i = 0; i < json.length; i++) {
			var node = new Object();
			node.id = json[i].id;
			node.pId = json[i].pId==null?0:json[i].pId;
			node.open = json[i].open;
			node.name = json[i].name;
			node.children = [];
			node.isParent = json[i].isParent;
			node.nocheck = json[i].nocheck;
			node.checked = json[i].checked;
			node.level = json[i].level;
			node.tId = json[i].tId;
			node.parentTId = json[i].parentTId;
			node.zAsync = json[i].zAsync;
			node.isFirstNode = json[i].isFirstNode;
			node.isLastNode = json[i].isLastNode;
			node.isAjaxing = json[i].isAjaxing;
			node.checkedOld = json[i].checkedOld;
			node.chkDisabled = json[i].chkDisabled;
			node.halfCheck = json[i].halfCheck;
			node.check_Child_State = json[i].check_Child_State;
			node.check_Focus = json[i].check_Focus;
			node.isHover = json[i].isHover;
			node.editNameFlag = json[i].editNameFlag;
			node.extendAttribute = json[i].extendAttribute;
			arry.push(node);
		}
		return arry;
	}
	picktree.prototype = {
		isInteractive:true,//是否显示可移动箭头
		selector : '',// 放置pickTree的目标容器
		constructor : picktree,
		leftUrl : '',// 左边树URL地址
		rightUrl : '',// 右边树URL地址
		leftTreeObj:undefined,
		rightTreeObj:undefined,
		leftNodes:[],
		rightNodes:[],
		dataType : 'array',
		_cfg:{},
		setting : {// zTree默认setting
			check : {
				enable : true,
				chkboxType : {"Y" : "ps","N" : "ps"}
			},
			data : {
				simpleData : {
					enable : true,
					idKey : "id",// id
					pIdKey : "pId",// 父亲节点
					rootPId : null
				}
			},
			view : {
				showIcon : false,
				nameIsHTML: true,
				fontCss:{}
			},
			async : {
				enable : true,
				url : "",
				autoParam : ["id"],
				dataType : "json",
				dataFilter : function(id, node, newNodes) {
					return newNodes.data;
				},
				otherParam:undefined
			},
			callback:{
			}
		},
		leftTreeId : undefined,// pickTree左边树的id，自动生成随机ID
		rightTreeId : undefined,// pickTree左边树的id，自动生成随机ID
		_dom : {
			div : '<div class="oc-pickList" style="height:100%;">'
					+ '<div class="pickList-left-width">'
					+ '<div class="pickList-left">' + '<div id="showZTreeInfoPanel" style="width: 100%; height: 100%; overflow: auto;"><ul class="ztree"></ul></div>'
					+ '</div>' + '</div>' + '<div class="pickList-center">'
					+ '<div class="tab-tree-arrow">'
					+ '<a class="tree-arrow-right"></a>'
					+ '<a class="tree-arrow-left"></a>' + '</div>' + '</div>'
					+ '<div class="pickList-right-width" style="float:right;">'
					+ '<div class="pickList-right">'
					+ '<div style="width: 100%; height: 94%; overflow: auto;"><ul class="ztree"></ul></div>' + '</div>' + '</div>'
					+ '</div>'
		},
		_initPanel : function(cfg) {// 初始化pickTree面板
			var that = this;
			var leftId = this.leftTreeId = oc.util.generateId();// 生成左边树ID
			var rightId = this.rightTreeId = oc.util.generateId();// 生成右边树ID
			var div = $(this._dom.div);
			div.find('.pickList-left .ztree').attr('id', leftId);
			div.find('.pickList-right .ztree').attr('id', rightId);
			div.appendTo(this.selector);
			var search = cfg.search
			if(search){
				var searchHtml = '<div style="position:relative;width:100%;display:inline-block;height: 25px;">'+
									'<input id="pickTreeSearchText" type="text" placeholder="'+search.searchText+'" style="width:99%; line-height: 22px;">'+
									'<span id="pickTreeSearchBtn" class="ico ico-search" style="position:absolute;top:2px;right:4px;" data-field="searchBtn"></span>'+
								'</div>';
				div.find('.pickList-left-width .pickList-left').prepend(searchHtml)
				
				div.find('#pickTreeSearchText').fixPlaceHolder().bind("keyup",function(e){
					if(e.keyCode==13){
						var searchValue = div.find('#pickTreeSearchText').val();
						var result = search.onSearch(searchValue);
						result&&that.reload(result, 'left');
					}
				});
				
				div.find('#pickTreeSearchBtn').bind('click', function() {
					var searchValue = div.find('#pickTreeSearchText').val();
					var result = search.onSearch(searchValue);
					result&&that.reload(result, 'left');
				});
				
				div.find("#showZTreeInfoPanel").height("92%");
			}
			if(!this.isInteractive){
				div.find('.tab-tree-arrow').hide();
			}else{
				div.find('.tree-arrow-right').bind('click', function() {
					moveTreeNode(leftId, rightId);// 绑定向左移箭头事件
				});
				div.find('.tree-arrow-left').bind('click', function() {
					moveTreeNode(rightId, leftId);// 绑定向右移箭头事件
				});
			}
			
		},
		_initTree : function() {// 初始化Tree
			oc.ui.progress();
			var that = this;
			var lSetting = $.extend(true,{},this.setting);
			lSetting.callback =$.extend({},this.setting.callback,this._cfg.leftCallback);
			lSetting.async.url = this.leftUrl;
			lSetting.callback.onAsyncSuccess = function(){
				if(that._cfg.leftCallback&&that._cfg.leftCallback.onAsyncSuccess)that._cfg.leftCallback.onAsyncSuccess();
				$.messager.progress('close');
			}
			if(that._cfg.leftParam){
				lSetting.async.otherParam = that._cfg.leftParam;
			}
			if(that._cfg.leftFontCss){
				lSetting.view.fontCss = that._cfg.leftFontCss;
			}
			this.leftTreeObj = $.fn.zTree.init($("#" + this.leftTreeId), lSetting,this.leftNodes);
			var rSetting =  $.extend(true,{},this.setting); 
			rSetting.callback = $.extend({},this.setting.callback,this._cfg.rightCallback);
			rSetting.async.url = this.rightUrl;
			if(that._cfg.rightParam){
				rSetting.async.otherParam = that._cfg.rightParam;
			}
			if(that._cfg.rightFontCss){
				rSetting.view.fontCss = that._cfg.rightFontCss;
			}
			this.rightTreeObj = $.fn.zTree.init($("#" + this.rightTreeId), rSetting,this.rightNodes);
		},
		getLeftTreeData : function() {// 获取左边树的所有数据
			var treeObj = $.fn.zTree.getZTreeObj(this.leftTreeId);
			if (this.dataType == "json") {
				return treeObj.getNodes();
			} else {
				var nodes = treeObj.transformToArray(treeObj.getNodes());
				return getCheckedData([],nodes);
			}
		},
		getRightTreeData : function() {// 获取右边树所有数据
			var treeObj = $.fn.zTree.getZTreeObj(this.rightTreeId);
			if (this.dataType == "json") {
				return treeObj.getNodes();
			} else {
				var nodes = treeObj.transformToArray(treeObj.getNodes());
				return getCheckedData([],nodes);
			}
		},
		getLeftTreeeCheckedData : function() {// 获取左边树checkbox选中的数据
			var treeObj = $.fn.zTree.getZTreeObj(this.leftTreeId);
			var arrayData = getCheckedData([], treeObj.getCheckedNodes(true));
			if (this.dataType == "json") {
				var nodes = treeObj.transformTozTreeNodes(arrayData);
				return nodes;
			} else {
				return arrayData;
			}
		},
		getRightTreeeCheckedData : function() {// 获取右边树checkbox选中的数据
			var treeObj = $.fn.zTree.getZTreeObj(this.rightTreeId);
			var arrayData = getCheckedData([], treeObj.getCheckedNodes(true));
			if (this.dataType == "json") {
				var nodes = treeObj.transformTozTreeNodes(arrayData);
				return nodes;
			} else {
				return arrayData;
			}
		},
		reload:function(data,position){
			if(position=='l' || position=='left'){
				this.leftNodes = data;
				$.fn.zTree.init($("#" + this.leftTreeId), this.setting,this.leftNodes);
			}else if(position=='r' || position=='right'){
				this.rightNodes = data;
				$.fn.zTree.init($("#" + this.rightTreeId), this.setting,this.rightNodes);
			}
			
		},
		queryTree:function(queryString){//通过一个字符串查询树节点，如果节点存在选中节点，展开节点，勾选节点
			var copyNodes;
			copyIdx = 0;
			var treeObj = $.fn.zTree.getZTreeObj(this.leftTreeId);
			copyNodes = treeObj.getNodesByParamFuzzy("name", queryString, null);
			if(copyIdx<copyNodes.length){
				treeObj.expandNode(copyNodes[copyIdx], true, true, true);
				treeObj.selectNode(copyNodes[copyIdx]);
			}
		},
		checkCfg : function(cfg) {
			if (cfg) {
				if (!cfg.selector) {
					alert('没有提供选择器或者jquery对象', 'error');
					return false;
				}
				
				if(cfg.leftNodes){
					this.leftNodes = cfg.leftNodes;
				}else{
					if(cfg.lUrl){
						if(cfg.async!=undefined && !cfg.async){
							var syncNodes = [];
							oc.util.ajax({url:cfg.lUrl,async:false,success:function(data){
								syncNodes = data.data;
							}});
							this.leftNodes = syncNodes;
						}else{
							this.leftUrl = cfg.lUrl;
						}
					}
				}
				if(cfg.rightNodes){
					this.rightNodes = cfg.rightNodes;
				}else{
					if(cfg.rUrl ){
						if(cfg.async!=undefined && !cfg.async){
							var syncNodes = [];
							oc.util.ajax({url: cfg.rUrl,async:false,success:function(data){
								syncNodes = data.data;
							}});
							this.rightNodes = syncNodes;
						}else{
							this.rightUrl = cfg.rUrl;
						}
					}
				}
				if (cfg.dataType) {
					this.dataType = cfg.dataType;
				}
				if(!cfg.isInteractive){
					this.isInteractive = cfg.isInteractive;
				}
			}
			return true;
		}
	};
	oc.ui.picktree = function(cfg) {
		myPickTree = new picktree(cfg);
		return myPickTree;
	};

})(jQuery);

function buildNodes(arrayNode){
	var ids = "";
	for(var i = 0;i<arrayNode.length;i++){
		if(arrayNode[i].isParent){
			var pnode = new arrayNode[i];
			ids+=pnode.id+",";
			for(var j = 0;j<arrayNode.length;j++){
				if(arrayNode[j].pId == pnode.id){
					pnode.children.push(arrayNode[j]);
					ids+=arrayNode[j].id+",";
				}
			}
		}
	}
}
