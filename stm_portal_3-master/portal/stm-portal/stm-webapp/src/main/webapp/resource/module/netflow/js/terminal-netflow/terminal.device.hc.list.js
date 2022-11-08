$(function() {
	// 图片放大缩小显示
	var b = $(".player ul>li").size();

	$("#icon_left").click(function() {
		$(".player ul>li").each(function() {
			var a = $(this).attr("class"), c = parseInt(a.charAt(1)) - 1;
			c == 0 && (c = b);
			$(this).removeClass(a).addClass("p" + c);
		});
	}), $("#icon_right").click(function() {
		$(".player ul>li").each(function() {
			var a = $(this).attr("class");
			c = parseInt(a.charAt(1)) + 1;
			c > b && (c = 1);
			$(this).removeClass(a).addClass("p" + c);
		});
	});
	$(".player ul>li.p1,.player ul>li.p2,.player ul>li.p3").live("click", function() {
		$("#icon_right").click();
	}), $(".player ul>li.p5,.player ul>li.p6,.player ul>li.p7").live("click", function() {
		$("#icon_left").click();
	});
	
//	setInterval(function() {
//		$("#icon_right").click();
//	}, 1500);
	
});