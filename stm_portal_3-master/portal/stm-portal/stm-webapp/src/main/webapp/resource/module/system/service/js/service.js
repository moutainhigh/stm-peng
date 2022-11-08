$(function() {
	var $container = $('#pageTag-container');
	var nodeId;
	$container.find('#tag-panel').panel('RenderP',{
		title:'系统服务',
		width:'280px',
		tools : [ {
			iconCls : 'fa fa-refresh',
			handler : function() {
				reloadNodeTree();
//				getMemcachedState();
			}
		} ],
		onOpen:function(){
			var panelHeight = $container.find(".system-parent").height(),panelHeadHeight = $container.find(".system-parent .panel-header").height();
			$container.find(".system-parent .pageTag-panel").height(panelHeight-panelHeadHeight);
		}
	});
	var form = $container.find("#pageTag-form"),
		myform = oc.ui.form({selector:form}),
		settingService=$container.find('#settingService').panel('RenderP',{
			title:'设置DCS',
		}),
		cacheStats=$container.find('#cacheStats').panel('RenderP',{
			title:'缓存服务',
		});
	$container.find('#submitForm').linkbutton('RenderLB', {
		text : '应用',
		iconCls : 'fa fa-check-circle',
		onClick : function() {
			var node = myform.val();
			if(node.id){
				if(myform.validate()){
					oc.util.ajax({
						url : oc.resource.getUrl("system/service/updateNodeName.htm"),
						data:{id:node.id,name:node.name,description:node.description},
						successMsg : null,
						success : function(data) {
							if(data.data){
								alert("系统服务修改成功！");
								$container.find("#nodeTree").tree("reload");

							}
						}
					});
				}
			}else{
				alert("请先选择服务！");
			}
		}
	});
	$container.find('#reload').linkbutton('RenderLB', {
		text : '刷新',
		iconCls : 'fa fa-refresh',
		onClick : function() {
			console.info(1);
			var node = myform.val();
			if(node.id){
				nodeId = node.id;
				$container.find("#nodeTree").tree("reload");
				$container.find("#memcachedTree").tree("reload");
			}
		}
	});
	
	/**
	 * 显示系统服务详细信息
	 * */
	function showService(node){
		cacheStats.panel('close');
		settingService.panel('open');
		if(node){
			if(node.func=="collector"){
				form.find("#serviceName").text("DCS名称：");
				settingService.panel("setTitle",'设置DCS');
			}
			if(node.func=="processer"){
				settingService.panel("setTitle",'设置DHS');
				form.find("#serviceName").text("DHS名称：");
			}
			loadForm(node);
		}
	}
	
	/*var keymapper = {
			delete_hits: 'delete命令命中次数',
			bytes: '当前存储占用的字节数',
			total_items: '启动以来存储的数据总数'	,
			listen_disabled_num: '失效的监听数',
			auth_errors: '认证失败数目',
			evictions: 'LRU释放的对象数目',
			version: 'memcache版本',
			pointer_size: '当前操作系统的指针大小',
			time: '服务器当前的unix时间戳',
			incr_hits: 'incr命令命中次数',
			threads: '当前线程数',
			limit_maxbytes: '分配的内存总大小（字节）',
			bytes_read: '读取总字节数',
			curr_connections: '当前连接数量',
			get_misses: 'get命令未命中次数',
			bytes_written: '发送总字节数',
			connection_structures: 'Memcached分配的连接结构数量',
			cas_hits: 'cas命令命中次数',
			delete_misses: 'delete命令未命中次数',
			total_connections: 'Memcached运行以来连接总数',
			cmd_flush: 'flush命令请求次数',
			uptime: '	服务器已运行秒数',
			pid: 'memcache服务器进程ID',
			cas_badval: '使用擦拭次数',
			get_hits: 'get命令命中次数',
			curr_items: '当前存储的数据总数',
			cas_misses: 'cas命令未命中次数',
			accepting_conns: '接受新的连接（1：是）',
			cmd_get: 'get命令请求次数',
			cmd_set: 'set命令请求次数',
			incr_misses: 'incr命令未命中次数',
			auth_cmds: '认证命令处理的次数',
			decr_misses: 'decr命令未命中次数',
			decr_hits: 'decr命令命中次数',
			conn_yields: '连接操作主动放弃数目'
		};*/
	
	/*$container.find("#memcachedTree").tree({
		animate : true,
		method : 'post',
		dataType:'json',
		fit:true,
		onClick : function(node) {
			$('#cacheStats').empty();
			cacheStats.panel('open');
			settingService.panel('close');
			
			var desc = node.description;
			if(!desc){
				return false;
			}
			var stats = JSON.parse(desc);
			$('#cacheStats').append('<form class="oc-form col1" style="overflow: auto;"></form>');
			for(var item in stats){
				$('#cacheStats form').append('<div class="form-group" style="width: 50%;"><label style="width: 50%;">' + (keymapper[item] || item) + '：</label><div style="width: 45%;"><span>' + stats[item] + '</span></div></div>')
			}
		},
		loader : function(param, success) {
			oc.util.ajax({
				url : oc.resource.getUrl("system/service/getMemcachedData.htm"),
				successMsg : null,
				failureMsg : '加载缓存数据失败！',
				success : function(data) {
					if(data.data){
						success(data.data);
					}
				}
			});
		}
	});
	*/
	$container.find("#nodeTree").tree({
		animate : true,
		method : 'post',
		dataTyep:'json',
		fit:true,
		onClick : function(node) {
			nodeId = node.id;
			$container.find("#nodeTree").tree("reload");
		},
		loader : function(param, success) {
			oc.util.ajax({
				url : oc.resource.getUrl("system/service/getNodeForTree.htm"),
				successMsg : null,
				failureMsg : '加载用户域数据失败！',
				success : function(data) {
					if(data.data){
						success(data.data);
					}
					$("#nodeTree").find(".alert-ico").removeClass("tree-icon tree-folder tree-folder-open tree-file");
					
				}
			});
		},
		onLoadSuccess:function(node,data){
			if(nodeId){
				var tnode = $container.find("#nodeTree").tree('find', nodeId);
				$container.find("#nodeTree").find("#"+tnode.domId).addClass("tree-node-selected");/*tree-node-selected*/
				showService(tnode);
			}else{
				var d = data[0];
				$container.find("#nodeTree").find("#"+d.domId).addClass("tree-node-selected");
				showService(d);
			}
		}
	});
	
	function reloadNodeTree(){
		$container.find("#nodeTree").tree("reload");
		$container.find("#memcachedTree").tree("reload");
	}
	
	function loadForm(node){
		myform.val(node);
		form.find("#ip").text(node.ip);
		form.find("#state").attr("class",node.iconCls);
		form.find("#port").text(node.port);
		form.find("#installPath").text(node.installPath==null?"":node.installPath);
		form.find("#startupTime").text(node.startupTime==null?"":node.startupTimeStr);
	}
//	// 获取memcached状态
	getMemcachedState();
	function getMemcachedState(){
		oc.util.ajax({
			url : oc.resource.getUrl("system/service/getMemcachedStatus.htm"),
			data:null,
			successMsg : null,
			success : function(data) {
				if(data.code == 200){
					var memcached = $container.find(".memcachedState").removeClass();
					if(data.data){
						memcached.addClass("memcachedState alert-ico lightnormal");
					} else {
						memcached.addClass("memcachedState alert-ico redlight");
					}
				}else{
					memcached.addClass("memcachedState alert-ico grizzly");
				}
			}
		});
	}
});