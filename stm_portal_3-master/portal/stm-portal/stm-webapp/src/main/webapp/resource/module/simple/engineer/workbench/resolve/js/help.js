$(function(){
	var $hw = $('#helpWindow');
	init();
	function init(){
		immediatelySolve();
		helpWay();
		close();
	}
	function immediatelySolve(){
		/*$hw.find('#immediatelySolve').on('click',function(){
			$('<div id="immediatelySolveWindow"></div>').appendTo('body').load('module/simple/engineer/workbench/resolve/doorToDoorService.html');
		})*/	
	}
	function helpWay(){
		/*$('#forum').on('click',function(){
			alert('敬请期待！');
		})*/
	}
	function close(){
		$hw.find('#back').on('click',function(){
			$hw.remove();
		});
		$hw.find('.close_btn').on('click',function(){
			$hw.remove();
		});
	}
});	