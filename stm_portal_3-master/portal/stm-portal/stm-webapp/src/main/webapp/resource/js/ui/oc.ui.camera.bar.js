(function($) {
	function init(_1cd) {
		$(_1cd).addClass("camerabar");
		$(_1cd)
				.html(
						"<div class=\"camerabar-text\"></div><div class=\"camerabar-value\"></div>");
		$(_1cd).bind("_resize", function(e, _1ce) {
			if ($(this).hasClass("easyui-fluid") || _1ce) {
				_1cf(_1cd);
			}
			return false;
		});
		return $(_1cd);
	}
	;
	function _1cf(_1d0, _1d1) {
		var opts = $.data(_1d0, "camerabar").options;
		var bar = $.data(_1d0, "camerabar").bar;
		if (_1d1) {
			opts.width = _1d1;
		}
		bar._size(opts);
		bar.find("div.camerabar-text").css("width", bar.width());
		bar.find("div.camerabar-text,div.camerabar-value").css({
			height : bar.height() + "px",
			lineHeight : bar.height() + "px"
		});
	}
	;
	$.fn.camerabar = function(_1d2, _1d3) {
		if (typeof _1d2 == "string") {
			var _1d4 = $.fn.camerabar.methods[_1d2];
			if (_1d4) {
				return _1d4(this, _1d3);
			}
		}
		_1d2 = _1d2 || {};
		return this.each(function() {
			var _1d5 = $.data(this, "camerabar");
			if (_1d5) {
				$.extend(_1d5.options, _1d2);
			} else {
				_1d5 = $.data(this, "camerabar", {
					options : $.extend({}, $.fn.camerabar.defaults,
							$.fn.camerabar.parseOptions(this), _1d2),
					bar : init(this)
				});
			}
			$(this).camerabar("setValue", _1d5.options.value);
			_1cf(this);
		});
	};
	$.fn.camerabar.methods = {
		options : function(jq) {
			return $.data(jq[0], "camerabar").options;
		},
		resize : function(jq, _1d6) {
			return jq.each(function() {
				_1cf(this, _1d6);
			});
		},
		getValue : function(jq) {
			return $.data(jq[0], "camerabar").options.value;
		},
		setValue : function(jq, _1d7) {
			if (_1d7 < 0) {
				_1d7 = 0;
			}
			if (_1d7 > 100) {
				_1d7 = 100;
			}
			return jq.each(function() {
				var opts = $.data(this, "camerabar").options;
				var text = opts.text.replace(/{value}/, _1d7);
				var _1d8 = opts.value;
				opts.value = _1d7;
				$(this).find("div.camerabar-value").width(_1d7 + "%");
				$(this).find("div.camerabar-text").html(text);
				if (_1d8 != _1d7) {
					opts.onChange.call(this, _1d7, _1d8);
				}
			});
		}
	};
	$.fn.camerabar.parseOptions = function(_1d9) {
		return $.extend({}, $.parser.parseOptions(_1d9, [ "width", "height",
				"text", {
					value : "number"
				} ]));
	};
	$.fn.camerabar.defaults = {
		width : "auto",
		height : 22,
		value : 0,
		text : "{value}%",
		onChange : function(_1da, _1db) {
		}
	};
})(jQuery);