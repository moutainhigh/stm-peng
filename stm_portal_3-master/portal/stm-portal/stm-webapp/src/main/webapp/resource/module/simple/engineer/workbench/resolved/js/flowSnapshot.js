$(function(){
	var $snapshot = $('#snapshot');
	init();
	/**
	 * 此js文件入口
	 * */
	function init(){
		flowSnapshot();
		lookDetails();
	}	
	function lookDetails(){
		$snapshot.on('click','.oc_simp_more',function(e){
			$(this).toggleClass('oc_simp_more_info').siblings('.oc_simp_stxt').toggleClass('oc_simp_stxt_hidden');
			e.stopPropagation();
		});
	}
	/**
	 * 后台获取快照信息
	 * */
	function flowSnapshot(){
		oc.util.ajax({
			url:oc.resource.getUrl('simple/engineer/workbench/getFaultProcessFlow.htm'),
			data:{faultId:$('#alertPageContainer').data('faultId')},//
			success:function(t){
				if(t.data){
					var data = t.data,
					process = $.parseJSON(data.process),
					analyzeListLen = process.analyzeList.length,
					solveListLen = process.solveList.length,
					_analyzeListhtml="",
					_solveListhtml="",
					title = process.title;
					$snapshot.find('.reh3 font').html(title);
					$snapshot.find('.snapshot_time').html(process.time);
					
					for(var i=0;i<analyzeListLen;i++){
						_analyzeListhtml +='<li class="first_no">'+process.analyzeList[i]+'</li>';
					}
					$('#snapshot_analyzeList').html(_analyzeListhtml).find('span.selected').parent('li').addClass('change_cor');
					//$('#snapshot_analyzeList').html(_analyzeListhtml).find('span.selected').parent('a').addClass('re_ul_li_a_select');
					for(var i=0;i<solveListLen;i++){
						_solveListhtml +='<li class="first_no">'+process.solveList[i]+'</li>';
					}
					$('#snapshot_solveList').html(_solveListhtml).find('span.selected').parent('li').addClass('change_cor');
					//$('#snapshot_solveList').html(_solveListhtml).find('span.selected').parent('a').addClass('re_ul_li_a_select');
				}
			}
			});
	}	
});