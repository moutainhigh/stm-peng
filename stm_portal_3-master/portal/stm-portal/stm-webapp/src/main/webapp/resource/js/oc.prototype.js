/**
 * 对js原生对象的兼容性以及易用性的改造
 * 兼容性改造指不同浏览器原生对象的兼容性改造，如某些浏览器缺失的函数、有的命名相同但行为意义不同的函数等
 * 易用性改造指方便开发使用的改造（此种改造不能滥用）
 * @author ziwenwen
 */

(function($){
	if(!window.log){
		log=function(){
			if(window.console&&console.log){
				for(var i=0,len=arguments.length;i<len;i++){
					console.log(arguments[i]);
				}
			}
		};
	}

	$.extend(Date.prototype, {
		stringify : function(format) {
			format = format || 'yyyy-mm-dd hh:MM:ss';
			format = format.replace('yyyy', this.getFullYear());
			var month = this.getMonth() + 1,d=this.getDate(),
			h=this.getHours(),m=this.getMinutes(),s=this.getSeconds();
			format = format.replace('mm', month < 10 ? '0' + month : month);
			format = format.replace('dd', d < 10 ? '0' + d : d);
			format = format.replace('hh', h < 10 ? '0' + h : h);
			format = format.replace('MM', m < 10 ? '0' + m : m);
			format = format.replace('ss', s < 10 ? '0' + s : s);
			return format;
		}
	});

	$.extend(Number.prototype, {
		toDate : function() {
			return isNaN(this)?new Date():new Date(this);
		}
	});

	$.extend(String.prototype,{
		trim:function(){
			return this.replace(/(^\s*)|(\s*$)/g,'');
		},
		toDate : function() {
			if (this) {// 目前支持格式 yyyy-mm-dd和yyyy-mm-dd hh:MM:ss
				var vals = /((\d+)-(\d+)-(\d+))?( (\d+):(\d+):(\d+))?/.exec(this);
				if (vals)return new Date(vals[2], vals[3], vals[4], vals[6] || 0, vals[7] || 0, vals[8] || 0);
				return null;
			}
			return null;
		},
		toI18n:function(){
			var str=this.toString(),ms=str.match(/\$\{([a-zA-Z]+(?:\.[a-zA-Z]+)*)\}/g);
			if(ms){
				for(var i=0,len=ms.length,m,v;i<len;i++){
					m=ms[i];
					try{
						v=eval(m.substr(2).slice(0,-1))||m;
						str=str.replace(m,v);
					}catch(e){
						log("没有配置国际化键："+m);
					}
				}
				return str;
			}else{
				return str;
			}
		},
		htmlspecialchars : function() {
			var str = this.toString();
			var s = "";
			if (str.length == 0 || str == undefined || str == null){
				return "";
			}
			for (var i = 0; i < str.length; i++) {
				switch (str.substr(i, 1)) {
				case "<":
					s += "&lt;";
					break;
				case ">":
					s += "&gt;";
					break;
				case "&":
					s += "&amp;";
					break;
				case " ":
					if (str.substr(i + 1, 1) == " ") {
						s += " &nbsp;";
						i++;
					} else {
						s += " ";
					}
					break;
				case "\"":
					s += "&quot;";
					break;
				case "\n":
					s += "<br>";
					break;
				default:
					s += str.substr(i, 1);
					break;
				}
			}
			return s;
		}
	});
	
	_sortFn=function(o1,o2){// 返回负数,第一个值放前面，返回正数，第二个值放前面
		return o1-o2;
	};
	$.extend(Array.prototype,{
		indexOf:function(o){
			if(typeof o=='function'){
				for(var i=0,len=this.length;i<len;i++){
					if(o(this[i]))return i;
				}
			}else{
				for(var i=0,len=this.length;i<len;i++){
					if(o==this[i])return i;
				}
			}
			return -1;
		},
		sort:function(fn){
			var dest=[],src=this;
			fn=fn||_sortFn;
			for(var i=0,len=src.length,j,dLen=0,o;i<len;i++,dLen++){
				o=src[i];
				for(j=0;j<dLen;j++){
					if(fn(o,dest[j])<0)break;
				}
				dest.splice(j,0,o);
			}
			$.extend(this,dest);
			return this;
		}
	});
})(jQuery);
