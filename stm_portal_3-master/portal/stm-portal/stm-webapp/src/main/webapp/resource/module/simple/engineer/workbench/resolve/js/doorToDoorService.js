$(function(){
	var $ids = $('#immediatelySolveWindow');
	close();
	function close(){
		$ids.find('#back').on('click',function(){
			$ids.remove();
		})
		$ids.find('.close_btn').on('click',function(){
			$ids.remove();
		});
	}
})