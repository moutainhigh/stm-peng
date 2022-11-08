$(function(){

	function ResourceMetricTree(data){
		this.leftTreeObj = $("#metric-left-main");
		this.rightTreeObj = $("#metric-rigth-main");
		this.searchContentObj = $('#metric-res-search');
		this.searchButton = $('metric-res-search-button');
		this.z3;
		this.z4;
		this.treedata = [];
		this.checkedTreeData = [];
		
		this.metricTypes = 'InformationMetric,PerformanceMetric,AvailabilityMetric';

		if(data){
			this.checkedTreeData = data;
		}
		//console.log(data);
		this.init();
	}
	var rmt = ResourceMetricTree.prototype;

	rmt.init = function(){
		this.initLeftTree();
		this.genRightTree();
		this._regEvent();
	}

	rmt._regEvent = function(){
		var _this =this;

		_this.searchContentObj.unbind().bind('change',function(){
			_this.initLeftTree();
		});
		_this.searchButton.unbind().bind('click',function(){
			_this.initLeftTree();
		});

	}

	rmt.initLeftTree = function(){
		var _this = this;
		var scont = this.searchContentObj.val();
		$.post(oc.resource.getUrl('portal/business/service/getBizFocusMetric.htm'),
			{searchContent:scont,
				dataType:'json'},
			function(data){
				if(data.code ==200){
					_this.businessData = data.data;
				}
				_this._initLeftTree();
			});

	}

	rmt._initLeftTree = function(){
		var _this = this;
		var leftTreeObj = this.leftTreeObj;
		var rightTreeObj = this.rightTreeObj;
		var scont = this.searchContentObj.val();
		//var searchContentObj = this.searchContentObj;
		//var z3 = this.z3;
		//var z4 = this.z4;
		var treedata = this.treedata;
		//var checkedTreeData = this.checkedTreeData;

		var leftTreeSetting = {
	   		async: {
				enable: true,
				url: getUrl,
				dataFilter:dataFilter,
				autoParam: ["id", "name"],
				otherParam:{ids:'',
					searchContent: scont,
					dataType:'json'
				}
			},
			check:{
				chkboxType:{ "Y": "p", "N": "ps" },
				autoCheckTrigger: true,
				enable: true,
				chkStyle:"checkbox"
			},
			view:{
				showIcon:false
			},
			data:{
				key:{
					name:"titleName",
					title:"name"
				}
			},
			callback:{
				onClick:function(event, treeId, treeNode){
					//console.log(treeNode);
					if(!treeNode.isParent){
						addCheckedData(treeNode);
					}
				},
				onCheck:function(event, treeId, treeNode){
					if(treeNode.isParent && treeNode.checked){
						_this.z3.expandNode(treeNode, true, false, false);
						return;
					}

					if(!treeNode.checked){
						//removeCheckedData(treeNode)
						_this.checkedTreeData =  _this._removeCheckedData(_this.checkedTreeData,treeNode);
					}else{
						addCheckedData(_this.checkedTreeData,treeNode);
					}

					//当前节点被选中，则检查该节点是否满足选择条件
					if(treeNode.checked){
						if(!_this._metricVerify())
							_this.z3.checkNode(treeNode, false, false,true);
					}

					//g=$.fn.zTree.init($("#main5"), {}, checkedTreeData);
					//g.expandAll(true);

					_this.genRightTree();
				},
				onAsyncSuccess:function(event, treeId, treeNode, msg) {
				  	if(scont){
				  		if(!treeNode)
				    		expandMainNodes(_this.z3.getNodes());
					}
				}
			}
	  	};

		_this.z3=$.fn.zTree.init(this.leftTreeObj, leftTreeSetting);
	
		function getUrl(treeId, treeNode){
			//主资源数
	   		//var url = '/resourceManagement/resourceCategory/getLeftResourceDefBySearchContent.htm';
	   		var url = oc.resource.getUrl('home/workbench/resource/getResourceDefBySearchContent.htm');

	   		while(treeNode == null){
	   			return url;
	   		}

	   		//主资源下级资源
	   		if(treeNode.mainResource){
	   			var iid = treeNode.id;
				url = '/portal/resource/resourceDetailInfo/getResourceInfo.htm?instanceId=' + iid +'&dataType=json'; 
				return url;
			}

			if(treeNode != null && treeNode.type != null){
				var type = treeNode.type;
				var iid = treeNode.getParentNode().id;

				url = '/portal/resource/resourceDetailInfo/getChildInstance.htm?';
				url += 'childType=' + type + '&instanceId=' + iid +'&dataType=json';

				return url;
			}

			//获取指标列表
			if(treeNode.isMetricId){
				var resourctId = treeNode.id;
				url = '/system/home/getResourceMetricList.htm?dataType=json&resourctId='+ resourctId;
			
				url = '/home/workbench/resource/getMetric.htm?instanceId=' + resourctId;
				url += '&metricTypes='  + _this.metricTypes;

				return url;
			}
		}
		
		function subName(node){
			if(node && node.titleName == null){
				//node.titleName = node.name;
				node.titleName = node.name.substring(0,25);
			}
		}

		function dataFilter(treeId, parentNode, childNodes){
			if(!parentNode){
				return mainResourceFilter(treeId, parentNode, childNodes);
			}else if(parentNode.mainResource || parentNode.isSubResource){
				return subResourceFilter(treeId, parentNode, childNodes);
			}else{
				return metricsFilter(treeId, parentNode, childNodes);
			}
		}

		function mainResourceFilter(treeId, parentNode, childNodes){
			subName(parentNode);
			var childs = childNodes.data;
			setPartent(childs);
			if(_this.businessData){
				changeName(_this.businessData);
				childs.push(_this.businessData);

				function changeName(p){
					if(!p)
						return;
					if(p.children){
						$.each(p.children,function(k,child){
							changeName(child);
							subName(child);
							if(child.pid == 'business'){
								child.mainResource = true;
							}
							if(!child.isParent){
								child.isMetricId = true;
								child.isOpen = false;
							}
						});
					}
					subName(p);
				}
			}
			function setPartent(childs){
				$.each(childs,function(i,obj){
					subName(childs[i]);
					//childs[i].name = childs[i].text;
					if(!childs[i].isParent){
						childs[i].isParent = true;
						childs[i].isOpen = false;
						childs[i].mainResource = true;
					}
					if(childs[i].children)
						setPartent(childs[i].children);
				});
			}
			treedata = treedata.concat(childs);
			return childs;
		}

		function subResourceFilter(treeId, parentNode, childNodes){
			subName(parentNode);
			var childs = [];
			if(parentNode.mainResource){
				var p = childNodes.data;
				p.id = p.instanceId;
				
				var tchilds = childNodes.data.childrenType;
				if(tchilds != undefined){
					$.each(tchilds,function(i,obj){
						subName(tchilds[i]);
						tchilds[i].isParent = true;
						tchilds[i].isOpen = false;
						tchilds[i].isSubResource = true;
					});
					delete childNodes.data.childrenType;
				}else{
					tchilds=[];
				}
				
				p.isParent = true;
				p.isMetricId = true;
				//p.isSubResource = true;
				childs.push(p);
				childs = childs.concat(tchilds);
			}else{
				childs = childNodes.data;
				$.each(childs,function(i,obj){
					subName(childs[i]);
					childs[i].isParent = true;
					childs[i].isMetricId = true;
					childs[i].isOpen = false;
				});

				if(childs.length == 0)
					alert('资源指标不存在');
			}
			
			if(childs.length>0)
				appendChildrenToTreeNode(parentNode,childs);

			return childs;
		}

		function metricsFilter(treeId, parentNode, childNodes){
			subName(parentNode);
			var childs = childNodes.data;
			$.each(childs,function(i,obj){
				childs[i].name = childs[i].text;
				subName(childs[i]);
			});
			if(childs.length == 0)
				alert('资源指标不存在');
			else
				appendChildrenToTreeNode(parentNode,childs);

			return childs;
		}

		function appendChildrenToTreeNode(treeNode,children){
			subName(treeNode);
			var pnode = treeNode;
			var parents = [];
			while(pnode != null){
				parents.push(pnode.id);
				pnode = pnode.getParentNode();
			}

			var tTree = treedata;
			var pttree = undefined;
			for(var i=parents.length-1; i>=0;i--){
				var key = parents[i];
				for(var j=0;j<tTree.length; j++){
					subName(tTree[j]);
					if(tTree[j].id == key){
						pttree = tTree[j];
						tTree = tTree[j].children;
						break;
					}
				}
			}
			
			for(var i = 0; i < children.length; ++i){
				subName(children[i]);
			}
			
			pttree.children = children;
		}

		function expandMainNodes(nodes) {
			if (!nodes) return;
			var zTree = _this.z3;
			for (var i=0, l=nodes.length; i<l; i++) {
				if(nodes[i].mainResource)
					return;
				zTree.expandNode(nodes[i], true, false, false);

				if (nodes[i].isParent && nodes[i].zAsync) {
					expandMainNodes(nodes[i].children);
				}
			}
		}

		function addCheckedData(checkTree,treeNode){
			var pnode = treeNode;
			var parents = [];
			while(pnode != null){
				parents.push(pnode.id);
				pnode = pnode.getParentNode();
			}

			var tTree = treedata;
			var pttree = undefined;

			//var checkTree  = checkedTreeData;

			for(var i=parents.length-1; i>=0;i--){
				var key = parents[i];
				for(var j=0;j<tTree.length; j++){
					if(tTree[j].id == key){
						pttree = tTree[j];
						tTree = tTree[j].children;
						
						var tp  = $.extend({},pttree,true);
						if(tp.children) 
							tp.children = [];
						
						var has = false;
						for(var n=0; n<checkTree.length;n++){
							if(checkTree[n].id == tp.id){
								has = true;
								tp = checkTree[n];
								break;
							}
						}
						if(!has){
							checkTree.push(tp);
						}

						checkTree = tp.children;
						pttree = tp;
						break;
					}
				}
			}
		}
	}

	rmt._removeCheckedData = function(dataTree,treeNode){
		var pnode = treeNode;
		var parents = [];
		while(pnode != null){
			parents.push(pnode.id);
			pnode = pnode.getParentNode();
		}

		var tTree = dataTree;
		var pttree = undefined;
		for(var i=parents.length-1; i>0;i--){
			var key = parents[i];
			for(var j=0;j<tTree.length; j++){
				if(tTree[j].id == key){
					pttree = tTree[j];
					tTree = tTree[j].children;
					//tracker.push(pttree);
					break;
				}
			}
		}

		if(pttree){
			var newc =[];
			for(var i=0; i<pttree.children.length; i++){
				var obj = pttree.children[i];
				if(obj.id != treeNode.id){
					newc.push(obj);
				}
			}
			pttree.children = newc;
		}

		return clearTree(dataTree);

		function clearTree(trees){
			for(var j=0; j<trees.length; j++){
				var tree = trees[j];
				if(!tree)
					continue;
				if(tree.children )
					tree.children = clearTree(tree.children);
				if(tree.children && tree.children.length <1)
					delete trees[j]
			}

			var left =[];
			for(var j=0; j<trees.length; j++){
				if(trees[j]){
					left.push(trees[j]);
				}
			}
			return left;
		}
	}

	rmt.genRightTree = function(){
		var _this = this;
		var setting = {
			check:{
				chkboxType:{ "Y": "p", "N": "ps" },
				autoCheckTrigger: true,
				enable: false,
				chkStyle:"checkbox"
			},
			data:{
				key:{
					name:"titleName",
					title:"name"
				}
			},
			view:{
				showIcon:false,
			},
			edit: {
				enable: true,
				editNameSelectAll: false,
				showRenameBtn: false,
				showRemoveBtn: function(treeId, treeNode){
					//return !treeNode.isParent;
					return treeNode.getParentNode() != null;
				},
				removeTitle:'删除指标'
			},
			callback:{
				onClick:function(event, treeId, treeNode){
					//console.log(treeNode);
				},
				onCheck:function(event, treeId, treeNode){
				},
				onRemove:function(event, treeId, treeNode){
					_this.checkedTreeData =  _this._removeCheckedData(_this.checkedTreeData,treeNode);
					_this.genRightTree();
				}
			}
		};

		g=$.fn.zTree.init(this.rightTreeObj, setting, this.checkedTreeData);
		g.expandAll(true);
	}

	rmt.setMetricTypes = function(metricTypes){
		this.metricTypes = metricTypes;
		this.initLeftTree();
	}

	//注册校验选择的指标是否满足条件的回调函数
	rmt.regMetricVerify  = function(fn){
		this.metricVerify  = fn;
	}

	rmt._metricVerify = function(){
		var fn = this.metricVerify;
		if(fn){
			return fn();
		}
		return true;
	}

	rmt.setData = function(data){
		this.checkedTreeData = data;
		this.genRightTree();
	}
	rmt.clearData = function(){
		this.setData([]);
	}
	rmt.getData = function(){
		return $.extend([],this.checkedTreeData,true);
	}

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.metricChooseHelper = function(opt){
    	return new ResourceMetricTree(opt);
    }
    
})