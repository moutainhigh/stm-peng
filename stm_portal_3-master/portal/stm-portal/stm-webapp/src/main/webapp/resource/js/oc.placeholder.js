/**
 * @Desc   :  修复IE9浏览器对DOM元素placeholder属性支持
 * @Author ： sunhailiang 
 * @Date   :  20170630
 */
(function($) { 
	  $.fn.fixPlaceHolder = function(){
	        var that = $(this),placeHolderFlag = 'placeholder' in document.createElement('input'); 
			if(placeHolderFlag){
			    return that;
			}
			var target = new Array(); 
			if(that.attr('placeholder')){
                target.push(that);
			}
			
			if(that.find('[placeholder]').size()){
				target.push(that.find('[placeholder]'));
			}

            if(!target.length){
			   return that;
			}

	        $(target).each(function(){   
	        	var self = $(this),txt = self.attr('placeholder')||"",holder = null;
				if(!txt.length){
				   return false;
				}

				if(self.is('textarea')){
				   self.nextAll('textarea[name^="textAreaHolder"]') && self.nextAll('textarea[name^="textAreaHolder"]').remove();
				   holder = self.clone().attr('name','textAreaHolder'+Date.now());
				}else if(self.is('input')){
				   self.nextAll('input[name^="inputHolder"]') && self.nextAll('input[name^="inputHolder"]').remove();
				   holder = self.clone().attr('type','text').attr('name','inputHolder'+Date.now()); 
				}

	        	if(holder){
					holder.attr('readonly','readonly').removeAttr('placeholder').unbind().val(txt).insertAfter(self);
					if(!$.trim(self.val() ||"").length){
					   holder.show();
					   self.hide();
					}else{ 
					   holder.hide(); 
					   self.show();
					}

					self.focusin(function(e) {
						e.stopPropagation();
						holder.hide();
					}).focusout(function(e) {
						e.stopPropagation();
						var that = $(this);
						if(!$.trim(that.val()||"").length){
							holder.show();
							that.hide();
						}else{
						   that.show();
						   holder.hide();
						}
					});

                    holder.click(function(e){
						e.stopPropagation();
						$(this).hide();
					    self.show();
						self.focus(); 
					});
	        	} 
			});
	        /*重置按钮事件*/
	        that.closest("form").find('.resetBtn').click(function(e){
               e.stopPropagation();
			   var holderTimeout = window.setTimeout(function(){
			       $(target).each(function(){
					 var current = $(this);
					 current.hide().nextAll('[name*="Holder"]').show().val(current.attr('placeholder'));
					 holderTimeout = null;
			      });
			   },0)
			});
	        
	        return that;
	  }
})(jQuery);