/**
 * 用于资源选择 RS为resourceSelect缩写
 */
(function(){
	
	function resourceChoise(opt){
		this.target =undefined;//ztree html obj
		this.ztree = undefined;
		
		//基本dom结构
		this._dom = '<div id = "choise-resource" class="choise-resource"> <input type="text" name="search" id="res-search"><button>查询</button> <ul id="resource-tree"  class="ztree"></ul> </div>';

		this.opt = {
			selector:undefined,
			maxSelects:1
		}
		this.opt = $.extend(this.opt,opt,true);

		if(this.opt.selector)
			this.init();
		else
			throw('初始化失败!');
	}

	var rcp = resourceChoise.prototype;

	rcp.init =function(){
		this.target = this.opt.selector.find('ul.ztree');
		this.target.attr('id',oc.util.generateId());
		this._init();
	}

	rcp._init = function(){
		this._loadData();
		this._regEvent();
	}

	rcp._loadData = function(){
		var _this = this,dobj=this.opt.selector;
		
		function NnodeName(childs){
			if(childs.isParent){
				childs.nocheck = true;
				var childs = childs.children;
				for(var i = 0; i < childs.length; ++i){
					NnodeName(childs[i]);
				}
			}else{
				if(childs.name.indexOf("()") == 0){
					childs.name = childs.name.substring(2);
				}
			}
		};

		var searchContent = dobj.find('input').val();
		var setting = {
	   		async: {
				enable: true,
				//url: '/resourceManagement/resourceCategory/getLeftResourceDefBySearchContent.htm',
				url:oc.resource.getUrl('home/workbench/resource/getResourceDefBySearchContent.htm'),
				dataFilter:function(treeId, parentNode, childNodes){
					var childs = childNodes.data;
					$.each(childs,function(i,obj){
						//childs[i].name = childs[i].text;
						NnodeName(childs[i]);	//去掉name开头无用的"()"
					});
					return childs;
				},
				otherParam:{ids:'',searchContent:searchContent,dataType:'json'}
			},
			view:{
				showIcon:false
			},
			check:{
				chkboxType:{ "Y": "", "N": "" },
				autoCheckTrigger: true,
				enable: true,
				chkStyle:"checkbox"
			},
			callback:{
				beforeClick : function(treeId, treeNode, clickFlag){
					var flag = treeNode.isParent;
					if(flag){
						if(treeNode.open){
							_this.zTree.expandNode(treeNode,false,false,false,false,false);
						}else{
							_this.zTree.expandNode(treeNode,true,false,false,false,false);
						}
					}
					return !flag;
				},
				beforeCheck : function(treeId, treeNode){
					var flag = treeNode.isParent;
					if(flag){
						if(treeNode.open){
							_this.zTree.expandNode(treeNode,false,false,false,false,false);
						}else{
							_this.zTree.expandNode(treeNode,true,false,false,false,false);
						}
					}
					return !flag;
				},
				onClick:function(event, treeId, treeNode){
					_this.zTree.checkNode(treeNode, !treeNode.checked, false,true);
				},
				onCheck:function(event, treeId, treeNode){
					//console.log(treeNode);
					var nodes = _this.zTree.getCheckedNodes();

					if(_this.opt.maxSelects == 1){
						for(var i=0;i<nodes.length;i++){
							if(treeNode.id != nodes[i].id){
								nodes[i].checked =false;
								_this.zTree.updateNode(nodes[i]);
							}
						}
					}else{
//						if(nodes.length > _this.opt.maxSelects){
//							alert("选取数量过多");

//							treeNode.checked =false;
//							_this.zTree.updateNode(treeNode);
//							return;
//						}
					}

					//回调函数
					_this.opt.callback && _this.opt.callback(nodes,_this.val(),treeNode);


				},
				onAsyncSuccess:function(event, treeId, treeNode, msg) {
				    if(searchContent){
				    	_this.zTree.expandAll(true);
				    	_this._setChecked(JSON.parse($("#choisedResource").val()));
				    }else if(_this.opt.resources){
				    	_this._setChecked(_this.opt.resources);
				    }
				}
			}
		};

		if(_this.zTree)
			_this.zTree.destroy(_this.target.attr('id'));

		_this.zTree = $.fn.zTree.init(_this.target, setting);
	}

	rcp._regEvent = function(){
		var _this=this,obj=this.opt.selector;

		obj.find('button').unbind().bind('click',function(){
			_this._init();
		});

		obj.find('input').unbind().bind('change',function(){
			_this._init();
		});
	}

	rcp.val = function(v){
		var _this = this;
		if(v){
			_this._setChecked(v);
			return;
		}

		var data = {};
		var nodes = _this.zTree.getCheckedNodes();
		for(var i=0;i<nodes.length;i++){
			var n=nodes[i],p = nodes[i];
			while(p != undefined && p.getParentNode() != undefined){
				p = p.getParentNode();
			}
			var t = {
				id: n.id,
				pid:n.pId,
				rootId:p.id,
				name:n.name,
			}
			data[t.id] = t;
		}
		return data;
	}

	rcp._setChecked = function(v){
		var _this = this;
		var nodes = _this.zTree.getNodes();

		expandNodes(nodes);
		
		function expandNodes(nodes) {
			if (!nodes) return;
			
			for (var i=0, l=nodes.length; i<l; i++) {
				//_this.zTree.expandNode(nodes[i], true, false, false);
				var id = nodes[i].id;

				//选中当前节点
				if(v[id]){
					_this.zTree.checkNode(nodes[i], true, false,true);
					var p = nodes[i];
					while(p != undefined && p.getParentNode() != undefined){
						_this.zTree.expandNode(p, true, false, false);
						p = p.getParentNode();
					}
				}
				
				if (nodes[i].isParent && nodes[i].zAsync) {
					expandNodes(nodes[i].children);
				}
			}
		}
	}

	oc.ns('oc.index.home.widgetedit.resource');	
	
	oc.index.home.widgetedit.resourceSelect = function(opt){
		return new resourceChoise(opt);
	};
	
})();