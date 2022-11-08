$(function(){
	var mainDiv = $('#tabList');
	mainDiv.css("height",$("#tabList").parents(".topo-content").height());
	var flags = [];
	mainDiv.tabs({
		fit:true,
		onSelect:function(title,index){
			if(flags.join("").indexOf(index) >= 0){
				if(4 != index){	//不刷新【告警设置】
					var tab = mainDiv.tabs('getSelected');  // 获取选择的面板
					mainDiv.tabs('update', {
						tab: tab,
						options: {}
					});
				}
			}else{
				flags.push(index);
			}
		}
	});
	$(".ico-back").on("click",function(){
		mainDiv.hide();	//自己隐藏掉
		//发布隐藏消息
		eve("oc.topo.ip-mac-port.hide");
	});
});
//隐藏ip-mac-port界面
function ip_mac_port_Hide(){
	$("#ipmac_back").click();
}
//# sourceURL=ipmacPort.js