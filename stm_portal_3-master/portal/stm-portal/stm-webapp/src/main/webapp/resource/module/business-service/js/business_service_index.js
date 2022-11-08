(function($){
	var rowData,bizSerIndexDiv,bizSerIndexCenterDiv;
	function initIndex(chooseRowData){
		rowData = chooseRowData;
		bizSerIndexDiv = $('#business-service_index').layout({
			fit:true
		});
		bizSerIndexDiv.layout('panel','west').load(oc.resource.getUrl('resource/module/business-service/business_service_west.html'),
				function(){
		});
		bizSerIndexCenterDiv = bizSerIndexDiv.layout('panel','center');
	}
	oc.ns('oc.business.service.index');
	oc.business.service.index = {
		initRow:function(chooseRowData){
			return initIndex(chooseRowData);
		},
		datas:function(){
			return {
				chooseRowData:rowData,
				bizSerIndexDiv:bizSerIndexDiv,
				bizSerIndexCenterDiv:bizSerIndexCenterDiv
			}
		}
	}
})(jQuery);
