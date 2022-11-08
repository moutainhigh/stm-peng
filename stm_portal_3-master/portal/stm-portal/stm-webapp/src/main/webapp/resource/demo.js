$(function(){
	
	oc.index.indexLayout = $('.main-content');
	oc.index.loadLoginUser();
	oc.index.activeContent = $('.main-content');
	//菜单点击事件
	$('.module-href').click(function(e){
		oc.index._activeRight=1;
		$('.main-content').html('');
		$('.main-content').height(document.body.scrollHeight);
		$('.main-content').load($(e.target).parents('li:first').attr('module'));
		$('#sidebar').find('li').removeClass('highlight');
		$(e.target).parents('li').addClass('highlight');
	});
	
})