$(function(){
	var chooseRowData;
	function open(bizMainId){
		if(bizMainId==undefined||bizMainId==null){
			$('#business-service_index').layout('panel','center').empty();
			return;
		}
		var $business = $("#bizList");
		var bizSerIndexDiv = $('#business-service_index');
		var	drawRectDiv = bizSerIndexDiv.find("#topology_tools_center");
		oc.util.ajax({
			url: oc.resource.getUrl('portal/business/service/get.htm?id='+bizMainId),
			data:null,
			async:false,
			successMsg:null,
			success: function(data){
				chooseRowData = data.data;
			}
		});
		$business.find('.easyui-linkButton').linkbutton('RenderLB');
		var editTopologyLayout = $('#edit_topology').layout({ fit:true });
		if(chooseRowData.topology){
			drawRectDiv.empty().load(oc.resource.getUrl('resource/module/business-service/business_service_draw_new.html'),function(){
			});
		}else{
			drawRectDiv.empty().append($('<div class="oc-business-pic"><a ></a></div>')
			.on('click',function(){
				drawRectDiv.empty().load(oc.resource.getUrl('resource/module/business-service/business_service_draw_new.html'),function(){
				});
			}));
		}
	}
	oc.ns('oc.business.service.tabs');
	oc.business.service.tabs = {
		open:function(bizMainId){
			open(bizMainId);
		},
		datas:function(){
			return {chooseRowData:chooseRowData};
		}
	}
});