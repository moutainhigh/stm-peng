/**
 * 调用方式：
 * $('#tag').target(["个",30,80,firstName,secondName],true,true);
 * target方法有两个参数：
 * 1.此参数为数组，第一个是单位；第二个和第三个是阈值，第四个和第五个参数是对应阈值input的name(不是必填)；
 * 2.此参数为boolean值(可选)，这个参数是设置target组件是不否可编辑，true为不可编辑，不填或false为可编辑；
 * 3.第三个参数为是否隐藏单位
 * 
 * 
 * */
(function($){
	$.fn.target = function(targetArr,isEdit,isHiddenUnit){
		var _html = '<div class="target-progress">\
					<div class="pro-yellow progressBar">\
						<label class="rightValue_label"></label>\
					</div>\
					<div class="pro-blue progressBar">\
						<label class="leftValue_label"></label>\
					</div>\
				</div>' ,
			$this = $(this),v0 = targetArr[0],v1 = Number(targetArr[1]),v2 = Number(targetArr[2]),$leftValue,$rightValue,isASC=true,leftIsValidate=true,rightIsValidate=true;
			if(isHiddenUnit){
				v0 = '';
			}
			function getValue(){
				return {
					max:$rightValue.next().val(),
					min:$leftValue.next().val()
				};
			}
			if(v1 > v2){
				isASC = false;
			}
			/*if(isNaN(v1)){
				alert('最小阈值必须为数字');
			}else if(isNaN(v2)){
				alert('最大阈值必须为数字');
			}else if(v1 > v2) {
				alert('最小阈值不能大于最大阈值');
			}else{*/
				$this.html(_html);
				
				$leftValue = $this.find('.leftValue_label');
				$rightValue = $this.find('.rightValue_label');
				$leftValue.text(v1+v0).attr('title',v1+v0).after('<input type="text" class="leftValue_input" value="'+v1+'" />');
				$rightValue.text(v2+v0).attr('title',v2+v0).after('<input type="text" class="rightValue_input" value="'+v2+'" />');
				if(targetArr.length > 3){
					$leftValue.next().attr('name',targetArr[3]);
					$rightValue.next().attr('name',targetArr[4]);
				}
				if(!isEdit){
					$leftValue.on('click',function(){
						var _this = $(this);
							_this.hide().next().show().off().on('blur',function(){
							eve("oc.ui.target.valuechange",this,getValue());
							var $this = $(this), v = Number($this.val());
							if($.trim(v)=="" || isNaN(v)){
								alert("请输入数字！");
								leftIsValidate=false;
								return;
							}else if(targetArr[0] == "%" && v > 100){
								alert("百分比不能超过100！");
								leftIsValidate=false;
								return;
							}else if(v < 0){
								alert("阈值不能小于0！");
								leftIsValidate=false;
								return;
							}
							if(isASC){
								if(v >= v2){
									alert("最小阈值不得大于等于最大阈值！");
									leftIsValidate=false;
									return;
								}
							}else{
								if(v <= v2){
									alert("最大阈值不得小于等于最小阈值！");
									leftIsValidate=false;
									return;
								}
							}
							v1 = v;
							leftIsValidate = true;
							$this.prev().text(v+v0).attr('title',v+v0).show().end().hide();
							
							//$this.focus();
						}).focus().on('keyup',function(e){
							if(e.keyCode==13){
								$(this).blur();
							}
						});
					});
					
					//$leftValue.click().next().blur();
					$rightValue.on('click',function(){
						var _this = $(this)//value = _this.text();
						_this.hide().next().show().off().on('blur',function(){
							eve("oc.ui.target.valuechange",this,getValue());
							var $this = $(this),v = Number($this.val());
							if($.trim(v)=="" || isNaN(v)){
								alert("请输入数字！");
								rightIsValidate=false;
								return;
							}else if(targetArr[0] == "%" && v > 100){
								alert("百分比不能超过100！");
								rightIsValidate=false;
								return;
							}else if(v < 0){
								alert("阈值不能小于0！");
								rightIsValidate=false;
								return;
							}
							if(isASC){
								if(v <= v1){
									alert("最大阈值不能小于等于最小阈值！");
									rightIsValidate=false;
									return;
								}
							}else{
								if(v >= v1){
									alert("最小阈值不得大于等于最大阈值！");
									rightIsValidate=false;
									return;
								}
							}
							v2 = v;
							rightIsValidate = true;
							$this.prev().text(v+v0).attr('title',v+v0).show().end().hide();
							
							//$this.focus();
						}).focus().on('keyup',function(e){
							if(e.keyCode==13){
								$(this).blur();
							}
							$this.focus();
						});
					});
					//$rightValue.click().next().blur();
				}
			//};
			this.getTargetValue = function(){
				var returnArr=[];
				returnArr[0] = v0;
				returnArr[1] = v1;
				returnArr[2] = v2;
				return returnArr;
			},
			this.isValidate=function(){
				if(!leftIsValidate || !rightIsValidate){
					return false;
				}else{
					return true;
				}
			}
			return this;
	}
})(jQuery);