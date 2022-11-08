$(function() {
	var $container = $('#pageTag-container'),devices={},products={},models=[],threedInfo={};
	$container.find('#tag-panel').panel('RenderP',{
		title:'机房结构',
		width:'280px',
		tools:[{iconCls:'fa fa-refresh',handler:function(){reloadNodeTree();}}]
	}).find(".pageTag-panel").css({height:$container.height()});
	
	var form = $container.find("#pageTag-form"),
		settingService=$container.find('#settingService').panel('RenderP',{
			title:'机柜中资源列表',
		});
	
	$container.find("#nodeTree").tree({
		animate : true,
		dataTyep:'json',
		method:'get',
		fit:true,
		onClick : function(node) {
			loadRight(node);
		},
		loader : function(param, success) {
			oc.util.ajax({
				url : oc.resource.getUrl("portal/3d/cabinet/getNodeTree.htm"),
				successMsg : null,
				failureMsg : '加载机房结构数据失败！',
				success : function(data) {
					getDevices();
					getProducts();
					get3dInfo();
					getModels();
					var node = handTreeData(JSON.parse(data.data).data);
					success([node]);
					$('.tree-folder').hide();
					$('.tree-file').hide();
				}
			});
		}
	});
	
	/**
	 * 获取所有资源
	 */
	function getDevices(){
		oc.util.ajax({
			url : oc.resource.getUrl("portal/3d/cabinet/getAllResource.htm"),
			successMsg : null,
			async:false,
			timeout:300000,
			success : function(data) {
				if(data.code&&data.code==200){
					for(var i in data.data){
						devices[data.data[i].id] = data.data[i];
					}
				}else{alert(data.data);}
			}
		});
	}
	/**
	 * 获取所有的产品库信息(导入到3d的产品模型)
	 */
	function getProducts(){
		oc.util.ajax({
			url : oc.resource.getUrl("portal/3d/model/getProductInfo.htm"),
			successMsg : null,
			async:false,
			timeout:300000,
			success : function(data) {
				if(data.code&&data.code==200){
					products = JSON.parse(data.data).data;
				}else{alert(data.data);}
			}
		});
	}
	/**
	 * 获取3dInfo信息
	 */
	function get3dInfo(){
		oc.util.ajax({
			url : oc.resource.getUrl('portal/3d/url/get3DInfo.htm'),
			successMsg : null,
			async:false,
			timeout:300000,
			success : function(data) {
				if(data.code&&data.code==200){
					threedInfo = data.data;
				}else{alert(data.data);}
			}
		});
	}
	/**
	 * 获取Models信息
	 */
	function getModels(){
		oc.util.ajax({
			url : oc.resource.getUrl("portal/3d/model/getAll.htm"),
			successMsg : null,
			async:false,
			timeout:300000,
			success : function(data) {
				if(data.code&&data.code==200){
					models = data.data;
				}else{alert(data.data);}
			}
		});
	}
	/**
	 * 刷新树结构
	 */
	function reloadNodeTree(){
		$container.find("#nodeTree").tree("reload");
		$container.find("#3dResource").empty();
	}
	/**
	 * 递归处理树数据
	 */
	function handTreeData(data){
		var node = {};
		node.text = data._ID_;
		node.state = data._CHILDREN_?"closed":"open";
		node.iconCls=data._CHILDREN_?"ico ico-unfold":"ico ico-shrinkage";
		node.attributes = {type:data._TYPE_};
		if(data._CHILDREN_ ){
			node.children = [];
			for(var i in data._CHILDREN_){
				node.children.push(handTreeData(data._CHILDREN_[i]));
			}
			//原生sort不起作用，改为冒泡排序
			for(var i=0;i<node.children.length;i++){
				for(var j = i+1;j<node.children.length;j++){
					if(node.children[j].text<node.children[i].text){
						var temp = node.children[j];
						node.children[j] = node.children[i];
						node.children[i] = temp;
					}
				}
			}
		}
		return node;
	}
	/**
	 * 单击节点重新加载右侧部份数据
	 */
	function loadRight(node){
		if(node.attributes.type=="机柜"){
			oc.resource.loadScript('resource/module/3d/device/js/resource.js?t'+new Date(),function(){
				oc.module.thirdd.resource.open({openFlag:false,belong:node.text,devices:devices,
					products:products,models:models,threedInfo:threedInfo});
			});
		}
	}
});