$(function(){
	var $alertPageContainer= $('#alertPageContainer'),
		$solveDetail = $('#solveDetail');
	init();
	function init(){
		solveDetail();
		closeDetailPage();
		backAnalyzeEngine();
	}
	function solveDetail(){
		var $container=$('.pop_middle_middle'),
			id = $solveDetail.data('solveDetail');
		oc.util.ajax({
			url:oc.resource.getUrl('simple/engineer/workbench/getResolveDetail.htm'),
			data:{resolveId:id},
			startProgress:false,
			stopProgress:false,
			success:function(t){
				var fileArr = t.data.resolveAttachments,
					fileArrLen = fileArr.length,
					_html;
				if(fileArrLen > 0){
					_html="<ul id='downLoadFile' class='inlie-list'>";
					for(var i=0;i<fileArrLen;i++){
						if(!/(\.bat|\.sh)$/.test(fileArr[i].fileName)){
							var fileName = fileArr[i].fileName;
							_html +="<li class='down_file' name="+fileArr[i].fileId+" title='点击下载"+fileName.escapeHtml()+"'>"+fileName+"</li>";
						}
					}
					_html +="</ul>";
				}
				$('.oc_simple_script_solve .scroll').html(t.data.resolveContent).append(_html);
				$('#downLoadFile').on('click','li',function(){
					oc.util.download($(this).attr('name'));
				})
				//判断是否是脚本
				var current = $('.pop01').find('#'+id).text(),
					dotLocation = current.indexOf('.'),
					isScript =  current.substring(dotLocation+1,dotLocation+5);
					isScript=(isScript=="【脚本】")?true:false;
					var $autoRepair = $('#autoRepair');
					if(isScript){
						$autoRepair.css('display','block').on('click',function(){
							//$solveDetail.find('.oc_simple_close_btn_bg02').click();
							$('<div id="auoRepairWindow"></div>').appendTo('body').load('module/simple/engineer/workbench/resolve/autoRepair.html',function(){
								$('.transparent').css('opacity','0.7');
								$solveDetail.hide();
							});
						});
					}else{
						$autoRepair.css('display','none');
					}
			}
			});
	}
	/**
	 * 关闭分析详情界面
	 * */
	function closeDetailPage(){
		$solveDetail.off('click.close').on('click.close','.oc_simple_close_btn_bg02',function(){
			$solveDetail.remove();
			$alertPageContainer.empty();
		});
	}
	function backAnalyzeEngine(){
		$solveDetail.off('click.back').on('click.back','#back',function(){
			$solveDetail.remove();
			$alertPageContainer.show();
		});
	}
});