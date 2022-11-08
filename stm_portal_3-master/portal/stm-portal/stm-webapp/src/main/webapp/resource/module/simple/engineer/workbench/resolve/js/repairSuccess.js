/**
 * 修复成功和失败暂时使用的同一个js
 * */
$(function(){
	var $result = $('.oc_simple_repair_result'),$alertPageContainer = $('#alertPageContainer');
	init();
	function init(){
		showInfo();
		saveResolveEvaluation();
		starEvaluate();
	}
	/**
	 * 显示修复成功后的信息
	 * */
	function showInfo(){
		var id = $alertPageContainer.data('eventId');
		oc.util.ajax({
			url:oc.resource.getUrl('simple/engineer/workbench/toRepairSuccess.htm'),
			data:{"eventId":id},
			startProgress:false,
			stopProgress:false,
			success:function(t){
				var d = t.data
				$result.find('.question').siblings('.sol_span').addClass(d.level);
				$result.find('.question').html(d.content);
				//$result.find('.result').siblings('.sol_span').addClass('SERIOUS');
				//$result.find('.result').siblings('.result_faild').addClass('CRITICAL');
				//$result.find('.result').html(d.recovered+"");
				//修复时间
				$result.find('.re_time').html(d.ext5);
				snapshotStr.time = d.ext5;//保存快照的修复时间
				$('#alarmId'+id).removeClass('startAnalyzeEngine').text('系统验证中');
				saveSnapshot();
				/*$('#li_solve').click();
				$('#li_unsolved').click();*/
			}
		});
	}
	/**
	 * 星际评价
	 * */
	function starEvaluate(){
		var checkStar=0;
		var $starEvaluate = $('.starEvaluate')
		$starEvaluate.on('mouseover','span.assess_star',function(){
			$starEvaluate.find('span.assess_star').addClass('empty_star').removeClass('full_star');
			$starEvaluate.find('span.assess_star:lt('+($(this).index()+1)+')').addClass('full_star').removeClass('empty_star');
		}).on('click','span.assess_star',function(){
			checkStar = $starEvaluate.find('.full_star').length;
		}).on('mouseleave',function(){
			$starEvaluate.find('span.assess_star').addClass('empty_star').removeClass('full_star');
			$starEvaluate.find('span.assess_star:lt('+checkStar+')').addClass('full_star').removeClass('empty_star');
		});
		
	}
	/**
	 * 修复成功之后评论
	 * */
	function saveResolveEvaluation(){
		$('#evaluation').on('click',function(){
			//保存评论
			var content =  $.trim($('#saveResolveEvaluation').val());
			if(content.length == 0){
				alert("请输入您的评论！");
			}else if(content.length > 200){
				alert("评论不得能超过200字！");
			}else{
				oc.util.ajax({
					url:oc.resource.getUrl('simple/engineer/workbench/saveResolveEvaluation.htm'),
					data:{"resolveId":$('#alertPageContainer').data('resolveId'),"content":content,"score":$('.oc_simple_repair_result .full_star').length},
					success:function(t){
						alert("感谢您的评论！");
						$('#alertPageContainer .oc_simple_close_btn').click();
					}
				});
			}
		});
	}
	/**
	 * 查看资源详情
	 * */
	(function lookDetails(){
		$('#lookDetails').on('click',function(){
			oc.resource.loadScripts(['resource/third/highcharts/js/highcharts.js','resource/third/highcharts/js/highcharts-more.js','resource/third/highcharts/js/exporting.js','resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js'], function(){
				oc.module.resmanagement.resdeatilinfonew.open({instanceId:$('#ul_unsolved_question').data('analyzeData').sourceID});
			});
		})
	})();
	/**
	 * 保存快照
	 * */
	function saveSnapshot(){
			oc.util.ajax({
				url:oc.resource.getUrl('simple/engineer/workbench/saveFaultProcessFlow.htm'),
				data:{"faultId":$('#alertPageContainer').data('eventId'),"process":JSON.stringify(snapshotStr)},
				success:function(t){
						if(t.code==200){
							//alert("保存快照成功！");
						}
						
				}
			});
	}
})