$(function(){

	function metricChooseHelper(opt) {
		this.opt = {
			area : $('.metric-choise'),//默认对象显示区域,
			resourceId:undefined,
			children:[{resourceId:undefined,metrics:[]}]
		};
		this.data = {};

		$.extend(this.opt,opt,true);

		this.area = this.opt.area;
    	this.z1 = undefined;
    	this.z2 = undefined;

    	this.u3 = undefined;
    	this.z3 = undefined;
    	

    	this.metricTypes = 'InformationMetric,PerformanceMetric,AvailabilityMetric';

		this.init();
	}
	var mch = metricChooseHelper.prototype;

	mch.init = function() {
		var _this = this;

		var children = _this.opt.children;
		if(children){  
			_this.data.metrics = {};
			for (var i = 0; i < children.length; i++) {
				var m = children[i];
				var obj = {};
				for(var j=0;j<m.metrics.length;j++){
					var tp = m.metrics[j];
					obj[tp.id] = tp;
				}
				_this.data.metrics[m.resourceId] = {
					resourceId : m.resourceId,
					name : m.name,
					metrics : obj
				};

			}
			_this.data.resourceId = _this.opt.resourceId;
		}

		var $that = $("<div/>").attr('id',oc.util.generateId());

	    var $div1 = $("<div/>").attr('id',oc.util.generateId()).addClass('divtree1');

	    var $u1 = $("<div/>").attr('id',oc.util.generateId());//grid
	    var $dg = $("<div/>").attr('id',oc.util.generateId());
	    $dg.append($u1);

	    $input = $('<input/>').attr('id',oc.util.generateId());
	    $button = $('<button/>').attr('id',oc.util.generateId()).text('查询');

	    $div1.append($input);
	    $div1.append($button);
	    $div1.append($dg);

	    var $div2 = $("<div/>").attr('id',oc.util.generateId()).addClass('divtree');;
	    var $u2 = $("<ul/>").attr('id',oc.util.generateId()).addClass('ztree');
	    $input2 = $('<input/>').attr('id',oc.util.generateId());
	    $button2 = $('<button/>').attr('id',oc.util.generateId()).text('查询');

	    $div2.append($input2);
	    $div2.append($button2);
	    $div2.append($u2);

	    var $div3 = $("<div/>").attr('id',oc.util.generateId()).addClass('divtree');
	    var $u3 = $("<ul/>").attr('id',oc.util.generateId()).addClass('ztree');
	    _this.u3 =$u3;

	    $div3.append($u3);

	    $that.append($div1);
	    $that.append($div2);
	    $that.append($div3);
		this.area.html($that);

		var h = $dg.parent().height() - $input.height();
		$dg.css({width:'100%',height:h+'px'});
		$u1.css({width:'100%',height:'100%'});
	   


		$input.bind('change',function(){
			getResources();
		});
		$button.bind('click',function(){
			getResources();
		});

		$input2.bind('change',function(){
			var id = $u2.attr("rid");
			getResourceDetail(id) ;
			
		});

		$button2.bind('click',function(){
			var id = $u2.attr("rid");
			getResourceDetail(id) ;
			
		});

		var rId = _this.opt.resourceId;

		getResources(rId);

		//resourceId 为当前选中的id
		function getResources(resourceId){
			var searchContent = $input.val();
			_this.z1 = oc.ui.datagrid({
				selector:$u1,
				url: '/resourceManagement/resourceCategory/getLeftResourceDefBySearchContent.htm',
				width:'100%',
		    	height:'100%',
		    	singleSelect:true,
		    	fitColumns:true,
		    	//showFooter:false,
		    	pagination:false,
		    	queryParams:{
			    	dataType:'json',
			    	searchContent:searchContent,
			    	ids:''
			    },
			    columns:[[
			    	{field:'id',title:'id',width:100,hidden:true,},
			    	{field:'ip',title:'IP地址',width:100,align:'left'},
			    	{field:'name',title:'名称',width:100,align:'left'},
			        {field:'resourceType',title:'资源类型',width:100,align:'left'},
			    ]],
			    onClickRow:function(ndex,row){
			    	if(_this.data.resourceId != row.id){
			    		_this.data.resourceId = row.id;
			    		_this.data.metrics = {};
			    	}
			    	getResourceDetail(row.id);
			    },
			    loadFilter:function(data){
			    	if(data.code == 200){
			    		var data = data.data;
			    		var rows = traveTree(data);

			    		var obj = {
			    			size:rows.length,
			    			rows:rows
			    		}
			    		return obj;
			    	}
			    },
			    onLoadSuccess:function(data){
					var p = $u1.datagrid('getRows');
					$.each(p,function(k,v){
						//console.log(v);
						if(v.id == resourceId){
							$u1.datagrid('selectRow',k);
							getResourceDetail(resourceId);
						}
					});
			    }
			});
			

			//遍历所有资源
			function traveTree(children){
				var rows = [];
				for(var i=0;i<children.length;i++){
					var chd = children[i];
					//console.log(chd);
					if(chd.isParent){
						var childs = chd.children;
						for(var j =0;j <childs.length;j++ ){
		    				var oj =childs[j];
		    				oj['resourceType'] = chd.name;
		    				var name = oj.name;
		    				var idx = name.indexOf(')');
		    				oj.ip = name.substring(1,idx);
		    				oj.name = name.substring(idx+1);
				    	}
				    	var tp = traveTree(childs);
						rows = rows.concat(tp);
					}else{
						//console.log(children[i]);
						rows.push(chd);
					}

				}
				return rows;
			}
		}


	    function getResourceDetail(resourceId){
			var zTreeObj;
		   	var setting = {
		   		async: {
					enable: true,
					url: getUrl,
					dataFilter:dataFilter,
					autoParam: ["id", "name"]
				},
				check:{
					chkboxType:{ "Y": "", "N": "" },
					autoCheckTrigger: true,
					enable: true,
					chkStyle:"checkbox"
				},
				callback:{
					onClick:function(event, treeId, treeNode){
						$.fn.zTree.destroy($u3.attr('id'));

						//叶子节点
						if(!treeNode.isParent){
							_this.getMetrics(treeNode.id);
						}

						//根节点
						var p = treeNode.getParentNode();
						if(p == null){
							_this.getMetrics(treeNode.id);
						}
					},
					onCheck:function(event, treeId, treeNode){
						if(!treeNode.checked){
							delete _this.data.metrics[treeNode.id];
			   			}

			   			//叶子节点
						if(!treeNode.isParent){
							_this.getMetrics(treeNode.id);
						}

						//根节点
						var p = treeNode.getParentNode();
						if(p == null){
							_this.getMetrics(treeNode.id);
						}

					},
					onAsyncSuccess:function(event, treeId, treeNode, msg) {
					   expandNodes(_this.z2.getNodes(),_this.z2);
					}
				}
		   	};

		   	function expandNodes(nodes,zTree) {
				if (!nodes) return;
				curStatus = "expand";
				//var zTree = $.fn.zTree.getZTreeObj("treeDemo");
				//var zTree = zTreeObj;
				var metrics = _this.data.metrics;
				
				for (var i=0, l=nodes.length; i<l; i++) {
					zTree.expandNode(nodes[i], true, false, false);
					if(metrics[nodes[i].id]){
						//console.log(nodes[i]);
						//var se = zTree.getSelectedNodes();
						//if(se.length <1) {
							zTree.selectNode(nodes[i]);
							zTree.checkNode(nodes[i], true, false,true);
						//}
						/*//console.log(se);
						var se = zTree.getSelectedNodes();
						if(se.length < 1){
							zTree.selectNode(nodes[i]);
							_this.getMetrics(nodes[i].id);
							//console.log(se);
						}//*/
					}

					if (nodes[i].isParent && nodes[i].zAsync) {
						expandNodes(nodes[i].children,zTree);
					} else {
						goAsync = true;
					}
				}
			}

		   	function dataFilter(treeId, parentNode, childNodes){
				var childs = [];
				if(!parentNode){
					var p = childNodes.data;
					p.id = p.instanceId;
					var tchilds = childNodes.data.childrenType;
					if(tchilds != undefined){
						$.each(tchilds,function(i,obj){
							tchilds[i].isParent = true;
							//tchilds[i].pData =childNodes.data;
						});
						p.children = tchilds;
						delete childNodes.data.childrenType;
					}
					childs.push(p);
				}else{
					cs = childNodes.data;
					var fiter = $input2.val();
					if(fiter != "" && fiter != undefined){
						var tcs  = [];
						for (var i=0; i<cs.length; i++) {
							//console.log(cs[i].name);
							if(cs[i].name.indexOf(fiter)>-1){
								tcs.push(cs[i]);
							}
						}
						childs = tcs;
					}else{
						childs = cs;
					}
				}
				//console.log(childs);
				return childs;
			}
		

		   	function getUrl(treeId, treeNode){
		   		var iid = resourceId;//2657; 
			   	//console.log(treeId);
			   	//console.log(treeNode);
				var url = '/portal/resource/resourceDetailInfo/getResourceInfo.htm?instanceId=' + iid +'&dataType=json'; 
				if(treeNode != null){
					var type = treeNode.type;
					//url = '/portal/resource/resourceDetailInfo/getChildInstance.htm?instanceId=2657&childType=NetInterface&dataType=json';
					url = '/portal/resource/resourceDetailInfo/getChildInstance.htm?';
					url += 'childType=' + type + '&instanceId=' + iid +'&dataType=json';
				}
				return url;
			}

			$u2.attr("rid",resourceId);
			_this.z2 = $.fn.zTree.init($u2, setting);
			$.fn.zTree.destroy(_this.u3.attr('id'));
		}

	};

	mch.getMetrics = function(resourceId){
		var _this = this;

		if(_this.current_resourceId == resourceId)
			return;

		_this.current_resourceId = resourceId;

//		console.log(this.data);

		var metrics = _this.data.metrics[resourceId];
		if(metrics){
			metrics = metrics.metrics;
		}else{
			metrics = {};
		}

		var setting2 = {
	   		async: {
				enable: true,
				url: '/home/workbench/resource/getMetric.htm',
				otherParam:{
					dataType:'json',
					instanceId:resourceId,
					metricTypes: _this.metricTypes
				},
				dataFilter:function(treeId, parentNode, childNodes){
					var childs = childNodes.data;
					$.each(childs,function(i,obj){
						childs[i].name = childs[i].text;
					});
					if(childs.length <1){
						alert("当前资源不存在可统计的指标");
					}
					return childs;
				}
			},
			check:{
				chkboxType:{ "Y": "", "N": "" },
				autoCheckTrigger: true,
				enable: true,
				chkStyle:"checkbox"
			},
			callback:{
				onClick:function(event, treeId, treeNode){
					_this.z3.checkNode(treeNode, !treeNode.checked, false,true);
				},
				onCheck:function(event, treeId, treeNode){

					if(_this.data.metrics[resourceId] == undefined){
						_this.data.metrics[resourceId] = {
							metrics:{}
						};
					}

					var pks = _this.z2.getSelectedNodes();
					var mobj = _this.data.metrics[resourceId];
					var dts = mobj.metrics;
					
					var datas = _this.z3.getCheckedNodes();

					_this.z2.checkNode(pks[0], datas.length>0, false,true);
					mobj.instanceId = pks[0].id;
					mobj.name = pks[0].name;
					
					if(datas.length<0){
						delete _this.data.metrics[resourceId];
						return;
					}

					$.each(datas,function(i,node){
						var data = {
							id:node.id,
							name:node.name,
							unit:node.unit,
							type:node.type,
						}
						dts[node.id] = data;
					});
						

					if(!treeNode.checked){
						delete dts[treeNode.id];
					}
//					console.log(_this.data);

					
					//当前节点被选中，则检查该节点是否满足选择条件
					if(treeNode.checked){
						if(!_this._metricVerify())
							_this.z3.checkNode(treeNode, false, false,true);
					}

				},
				onAsyncSuccess:function(event, treeId, treeNode, msg) {
				   expandMetrics(_this.z3.getNodes(),_this.z3);
				}
			}
		};

		function expandMetrics(nodes,zTree) {
			if (!nodes) return;
			curStatus = "expand";
			
			for (var i=0, l=nodes.length; i<l; i++) {
				zTree.expandNode(nodes[i], true, false, false);
				
				//选中当前节点
				if(metrics[nodes[i].id]){
					zTree.checkNode(nodes[i], true, false,true);
				}
				
				if (nodes[i].isParent && nodes[i].zAsync) {
					expandMetrics(nodes[i].children,zTree);
				} else {
					goAsync = true;
				}
			}
		}
		_this.z3 =$.fn.zTree.init(_this.u3, setting2);
	}

	//注册校验选择的指标是否满足条件的回调函数
	mch.regMetricVerify  = function(fn){
		this.metricVerify  = fn;
	}
	mch._metricVerify = function(){
		var fn = this.metricVerify;
		if(fn){
			return fn();
		}

		return true;
	}

	mch.getData = function(){
		var dat = this.data;
		var pd = {
			resourceId: dat.resourceId
		}
		var metricCount = 0;
		var children = [];
		$.each(dat.metrics,function(k,v){
			var obj = {
						resourceId:k,
						name:v.name
					};
			var metrics =[];
			$.each(v.metrics,function(iv,mt){
				metrics.push(mt);
				metricCount++;
			});
			obj.metrics = metrics;
			children.push(obj);
		});
		pd.children = children;
		pd.metricCount = metricCount;
		return pd;
	}



	mch.setMetricTypes = function(metricTypes){
		this.metricTypes = metricTypes;
		var p = this.data;

		if(this.current_resourceId)
			this.getMetrics(this.current_resourceId);
	}

	mch.reload = function(){
		this.z1 = undefined;
    	this.z2 = undefined;
    	this.z3 = undefined;
		this.init();
	}

    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.metricChooseHelper = function(opt){
    	return new metricChooseHelper(opt);
    }
    
})