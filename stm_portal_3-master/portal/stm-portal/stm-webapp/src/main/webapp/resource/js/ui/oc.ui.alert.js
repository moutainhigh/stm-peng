/**
 * 弹出提示框
 * @author ziwenwen
 * @param $
 */
(function($){
	function alert(){}
	
	alert.prototype={
		constructor:alert,
		closeE:'oc.alert.click',
		divD:$('<div class="alert" style="position:absolute;display:none;z-index:9999999;">'+
			'<button type="button" class="close" data-dismiss="alert">&times;</button></div>'),
		position:{my:'right-20 bottom-30',at:'right bottom',of:window,collision:'none'},
		timeout:5000,
		speed:200,
		objs:[],
		show:function(msg, type){
			if(this.objs.length>0){
				this.objs[0].trigger(this.closeE);
			}
			var that=this,div=this.divD.clone(true);
			div.bind(that.closeE,function(){
				that.objs.splice(that.objs.indexOf(div.remove()),1);
				that.reDraw();
			}).find('button').click(function(){
				div.trigger(that.closeE);
			});
			type?div.addClass('alert-'+type):div.addClass('alert-info');
			div.append(msg).appendTo(document.body).position(this.getPosition());
			this.objs.push(div.fadeIn(this.speed));
			setTimeout(function(){
				div.fadeOut(that.speed,function(){
					div.trigger(that.closeE);
				});
			},this.timeout);
		},
		getPosition:function(){
			var p=$.extend({},this.position);
			if(this.objs.length>0){
				p.at='right top';
				p.my='right top';
				p.of=this.objs[this.objs.length-1];
			}
			return p;
		},
		reDraw:function(){
			var p=$.extend({},this.position);
			for(var objs=this.objs,i=0,len=objs.length;i<len;i++){
				if(i>0){
					p.at='right top';
					p.my='right top';
					p.of=objs[i-1];
				}
				objs[i].position(p);
			}
		}
	};
	
	var t=new alert();
	window.palert=window.alert;
	window.alert=function(msg, type){
		t.show(msg, type);
	};
})(jQuery);