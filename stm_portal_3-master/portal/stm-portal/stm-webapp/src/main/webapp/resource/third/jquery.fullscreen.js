/**
 * @name        jQuery FullScreen Plugin
 * @author      Martin Angelov, Morten Sjøgren
 * @version     1.2
 * @url         http://tutorialzine.com/2012/02/enhance-your-website-fullscreen-api/
 * @license     MIT License
 */

/*jshint browser: true, jquery: true */
(function($){
	"use strict";

	function supportFullScreen(){
		var doc = document.documentElement;

		return ('requestFullscreen' in doc) ||
				('mozRequestFullScreen' in doc && document.mozFullScreenEnabled) ||
				('webkitRequestFullScreen' in doc) || 'ActiveXObject' in window;
	}

	function requestFullScreen(elem){
		if (elem.requestFullscreen) {
			elem.requestFullscreen();
		} else if (elem.mozRequestFullScreen) {
			elem.mozRequestFullScreen();
		} else if (elem.webkitRequestFullScreen) {
			elem.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
		}else if ('ActiveXObject' in window || new ActiveXObject("WScript.Shell")) {
			try{
				var wscript = new ActiveXObject("WScript.Shell");
				if (wscript != null) {
					wscript.SendKeys("{F11}");
				}
			}catch (e) {
				log(e);
			}
		}
	}

	function fullScreenStatus(){
		var stats = document.fullscreen ||
				document.mozFullScreen ||
				document.webkitIsFullScreen ||
				'ActiveXObject' in window ||
				false;

		if('ActiveXObject' in window){
			stats = (window.outerHeight==window.screen.height && window.outerWidth==window.screen.width);
		}

		return stats;
	}

	function cancelFullScreen(fn){
		if (document.exitFullscreen) {
			document.exitFullscreen();
		} else if (document.mozCancelFullScreen) {
			document.mozCancelFullScreen();
		} else if (document.webkitCancelFullScreen) {
			document.webkitCancelFullScreen();
		}else if ('ActiveXObject' in window || new ActiveXObject("WScript.Shell")) {
			try{
				var wscript = new ActiveXObject("WScript.Shell");
				if (wscript != null) {
					wscript.SendKeys("{F11}");
				}
			}catch (e) {
				log(e);
			}
		}
		fn && fn();
	}

	function onFullScreenEvent(callback){
		$(document).on("fullscreenchange mozfullscreenchange webkitfullscreenchange", function(){
			callback(fullScreenStatus());
		});

		//ie
		if('ActiveXObject' in window){
			$(window).on("resize.iefullscreen", function(){
				callback(fullScreenStatus());
			});
		}

	}

	$.support.fullscreen = supportFullScreen();
	$.support.fullscreenStatus = fullScreenStatus;

	$.fn.fullScreen = function(props){
		if(!$.support.fullscreen || this.length !== 1) {
		
			return this;
		}

		if(fullScreenStatus()){
			cancelFullScreen();
			return this;
		}

		var options = $.extend({
			'background'      : '#fff',
			'callback'        : $.noop( ),
			'fullscreenClass' : 'fullScreen'
		}, props),

		elem = this;
		requestFullScreen(elem.get(0));
		elem.click(function(e){
			if(e.target == this){
				cancelFullScreen();
			}
		});

		elem.cancel = function(){
			cancelFullScreen();
			return elem;
		};

		onFullScreenEvent(function(fullScreen){
			if(!fullScreen){
			    $(document).off( 'fullscreenchange mozfullscreenchange webkitfullscreenchange');
			    //ie
			    if(window.ActiveXObject){
					$(window).off("resize.iefullscreen");
				}
			}
			if(options.callback) {
            	options.callback(fullScreen);
            }
		});

		return elem;
	};

	$.fn.cancelFullScreen = function(fn) {
			cancelFullScreen(fn);
			return this;
	};
}(jQuery));
