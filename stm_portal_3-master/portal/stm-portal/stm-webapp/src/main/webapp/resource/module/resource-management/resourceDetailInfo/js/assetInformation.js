//# sourceURL=assetInformation.js
$(function(){
	oc.ns('oc.resourced.resource.detail.asset');
	function asset(itsmInfo){
		
	}
	var asset = asset.prototype;
	
	asset.initInfo = function(itsmInfo){
		var url = "http://" + itsmInfo.ip + ":" + itsmInfo.port + "/itsm/cmdb/viewByOtherSourceCIAction.action?systemCode="+itsmInfo.code+"&moId=" + itsmInfo.instanceId;
		//判断url是否是连通状态
        var ifram = '<iframe id="itsmInfo" name="itsmInfo" src="'+ url +'" width="100%" height="100%" frameborder="0"></iframe>';
        $("#asset_information_Div").html(ifram);

        /*$.ajax({
            type: "GET",
            cache: false,
            url: url,
            data: "",
            success: function() {
                var ifram = '<iframe id="itsmInfo" name="itsmInfo" src="'+ url +'" width="100%" height="100%" frameborder="0"></iframe>';
                $("#asset_information_Div").html(ifram);
            },
            error: function(error) {
                var span = '<span><img src="/resource/themes/blue/images/cry.png">  哦...页面走丢了</span>';
                $("#asset_information_Div").html(span);
            }
        });*/

	}
	
	oc.resourced.resource.detail.asset = {
		//入口方法
		open : function(itsmInfo){
			asset.initInfo(itsmInfo);
		}
	};
});