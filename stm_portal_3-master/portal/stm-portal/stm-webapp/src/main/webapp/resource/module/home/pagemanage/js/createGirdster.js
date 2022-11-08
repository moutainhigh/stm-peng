$(function(){
	var myInter=undefined;
	function createGirdster(mainDiv){}

	createGirdster.prototype = {
		constructor:createGirdster,
		//mainDiv:undefined,
		//cfg:undefined,
		widgetMgers:undefined,
		//myInter: undefined,
		open : function(mainDiv){
			widgetMgers = {};
		    
		    var defaulthtml = ' <div class="picdiv gridster ready2 slide0 oc-fullscren" name=".gridster.ready2" style="height:100%; display:block"></div>';
		    mainDiv.mainDiv.append(defaulthtml);
			//initGridster(".gridster.ready2",mainDiv.cfg);
			var anmation="nochoose";//无切换
			var timeout=30*1000;
			oc.util.ajax({
					url:oc.resource.getUrl('system/home/layout/slide/getSlide.htm'),
					timeout:null,
					async:false,
					success:function(data){
					var list=data.data.list;
					if(data.data!=null && list.length!=0){
						anmation=list[0].animation;// 暂时一种无切换效果
						timeout=list[0].slideTime*1000;
					}
				
	    			 //  var html ='';// ' <div class="picdiv gridster.ready2" name=".gridster.ready2" style="height: 100%; "></div>';
	    			$.each(list,function(index,value){
	    				var id=$(this)[0].layoutId;
	    				var name=$(this)[0].name;
						var defaultLayout=$(this)[0].defaultLayout;
						if(index==0){
							initGridster(".gridster.ready2",mainDiv.cfg,id);
						}
						if(index!=0){//默认页面
							var	html=' <div  layoutid="'+id+'" class="picdiv gridster ready'+id+' slide'+index+' oc-fullscren" name=".gridster.ready'+id+'" style="height:100%; display:none"></div>'
		    				mainDiv.mainDiv.append(html);
						}
	    			});
			    }
			});

			startRun(mainDiv.cfg,anmation,timeout);
		},
		close : function(){
			//console.info(widgetMgers);
			for(var o in widgetMgers){
				var wm = widgetMgers[o];
				wm.destroy();
			}
			widgetMgers={};//清空对象
			if(myInter!=undefined){//清除定时器
			clearInterval(myInter);
	    }
		},
		add : function(){
		//	console.info("add function");
		}
	}	

	oc.ns('oc.module.home.page.creategirdster');
	oc.module.home.page.creategirdster = {
		open:function(mainDiv,cfg){
		
			var selfObj = new createGirdster(mainDiv,cfg);
			selfObj.open(mainDiv,cfg);
		
		},	close:function(mainDiv,cfg){
			var selfObj = new createGirdster(mainDiv,cfg);
			selfObj.close();
		
		}
	}
	
	function initGridster(gridster,cfg,id){
	
		var widgetLoader = new oc.index.home.widgetLoader({
            theme:cfg.widgetLoader.getTheme()//肤色方案
        });
		var widgetMger = new widgetManager({
	          managerMode:false,
	          gridster:gridster,
	          widgetLoader:widgetLoader,
	          layoutId:id
	    });
		widgetMgers[gridster] = widgetMger;
	}


	function startRun(cfg,anmation,timeout){  
//console.info(anmation+"===="+timeout);
		var animationIndex=anmation;
		//代码初始化
		var size=$(".picdiv").size();

	   //手动控制轮播效果
	   $(".picdiv").eq(0).show();
	    
	   $(".picdiv").eq(0).addClass("active-page");
	 

	    //自动
		var i = 0;

	    if(myInter!=undefined){
			clearInterval(myInter);
	    }
		myInter = setInterval(move, timeout);
	 	var tasks = oc.index.indexLayout.data("tasks");
		if(tasks && tasks.length > 0){
			oc.index.indexLayout.data("tasks").push(myInter);
		}else{
			tasks = new Array();
			tasks.push(myInter);
			oc.index.indexLayout.data("tasks", tasks);
		}
	 
		//核心向右的函数
		function move() {
			i++;
			if (i == size) {
			   i = 0;
			}
			$(".picdiv").eq(i).addClass("active-page").siblings().removeClass("active-page");

			if(animationIndex=='nochoose'){//无切换
				$(".picdiv").each(function(index,value){
					$(this).hide();
				});
				$(".picdiv").eq(i).show();
				showAndHide(cfg,$(".picdiv").eq(i));

			}else if(animationIndex=='smoothFade'){//淡出
				$(".picdiv").eq(i).fadeIn(1500).siblings().fadeOut(1500);
				showAndHide(cfg,$(".picdiv").eq(i));
			}else if(animationIndex=='wipeLeft'){//向左切出
				var j = i + 1 == size ? j = 0 : i + 1;
				var $current = $(".picdiv.slide"+i);
				$current.fadeIn(600).siblings().animate({left:'1250px'},3000).fadeOut(300).animate({left:'0px'});
				showAndHide(cfg,$current);
				if(size > 2){
					$current.siblings(".slide"+j).insertAfter(".picdiv.slide"+i);
				} else {
					$current.siblings(".slide"+j).insertBefore(".picdiv.slide"+i);
				}
			}else if(animationIndex=='wipeRight'){//向右切出
				var j = i + 1 == size ? j = 0 : i + 1;
				var $current = $(".picdiv.slide"+i);
				$current.fadeIn(600).siblings().animate({left:'-1250px'},3000).fadeOut(300).animate({left:'0px'});
				showAndHide(cfg,$current);
				if(size > 2){
					$current.siblings(".slide"+j).insertAfter(".picdiv.slide"+i);
				} else {
					$current.siblings(".slide"+j).insertBefore(".picdiv.slide"+i);
				}
			}else if(animationIndex=='wipeDown'){//向下切出
				var j = i + 1 == size ? j = 0 : i + 1;
				var $current = $(".picdiv.slide"+i);
				$current.fadeIn(600).siblings().animate({top:'1250px'},3000).fadeOut(300).animate({top:'0px'});
				showAndHide(cfg,$current);
				if(size > 2){
					$current.siblings(".slide"+j).insertAfter(".picdiv.slide"+i);
				} else {
					$current.siblings(".slide"+j).insertBefore(".picdiv.slide"+i);
				}
			}else if(animationIndex=='wipeUp'){//向上切出
				var j = i + 1 == size ? j = 0 : i + 1;
				var $current = $(".picdiv.slide"+i);
				$current.fadeIn(600).siblings().animate({top:'-1250px'},3000).fadeOut(300).animate({top:'0px'});
				showAndHide(cfg,$current);
				if(size > 2){
					$current.siblings(".slide"+j).insertAfter(".picdiv.slide"+i);
				} else {
					$current.siblings(".slide"+j).insertBefore(".picdiv.slide"+i);
				}
			}/*else if(animationIndex=='blackCutOut'){//全黑淡出
				$(".fullscren-home").css("background-color","black");
				$(".fullscren-home").css("color","black");
				$(".fullscren-home").animate({"color":"black"}).animate({"background-color":"black"});
				$(".picdiv").eq(i).fadeIn(600).siblings().fadeOut('slow');
				setTimeout(function(){
					$(".fullscren-home").css("color","");
					$(".fullscren-home").css("background-color","");
				},2000); 
				showAndHide(cfg,$(".picdiv").eq(i));
			}*/
		}
	}

	function showAndHide(cfg,obj){
		var layoutid = obj.attr('layoutid');
		var name = obj.attr('name');
		var target = widgetMgers[name];
		if(!target){
			initGridster(name,cfg,layoutid);
		}else{
			target.refresh();
		}

	}

});
