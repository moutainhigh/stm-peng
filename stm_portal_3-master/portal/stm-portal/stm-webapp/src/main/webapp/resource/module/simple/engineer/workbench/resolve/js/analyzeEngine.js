$(function(){
	var $alertPageContainer = $('#alertPageContainer'),
		$anal_progress = $('.anal_progress'),
		$sovle_progress = $('.solve_progress'),
		$pop01 = $('#pop01'),
		$analyzeEngine = $alertPageContainer.find('.anal_act'),
		$sovle = $alertPageContainer.find('.solve'),
		skin=Highcharts.theme.currentSkin;
	var i,t;
	
	(function init(){
		startAnalyzeEngine();
	})();
	
	function startAnalyzeEngine(){
		var title = snapshotStr.title;
		$('.pop .Speed_pro').text(title);
		$anal_progress.text('0%');
		//这个定时器是为了模拟进度条，让用户能看到进度是从0开始的
		setTimeout(function(){
			$analyzeEngine.css('background-image','url(themes/'+skin+'/simple/images/PopImages/analyzing.gif)');
			requestAnalyzeData();
			i = 0
			var random=Math.floor(Math.random()*100),//分析引擎请求之前，进度先显示到random等待请求返回
			random2=Math.floor(Math.random()*30),//分析引擎请求之前，进度+1的时间
			t = setInterval(function(){//这个定时器是为了模拟进度条，并在一个随机的时间点停下，等待请求返回；
				if (i > random) {
					clearInterval(t);//清除定时器t有两种情况，1.当i大于随机数2.当requestAnalyzeData方法完成请求
				}
				$anal_progress.text(i+'%');
				i++;
			}, random2);
		},500);
	}
		
	/**
	 * 获取分析引擎分析的数据
	 * */
	function requestAnalyzeData(){
			var _html="";
			oc.util.ajax({
				url:oc.resource.getUrl('simple/engineer/workbench/analyzeFault.htm'),
				data:$('#ul_unsolved_question').data('analyzeData'),//{"sourceID":"5012237","ext0":"Linuxsnmp","ext1":"Linuxs","ext3":"cpuIdleTimePer"},
				startProgress:false,
				stopProgress:false,
				success:function(t){
					var d = t.data,len=d.length,
						random=Math.floor(Math.random()*30),//分析引擎请求成功之后，进度+1的时间
						t1 = setInterval(function(){//这个定时器是为了模拟进度，请求成功之后，在上个定时器的基础上继续走进度
						if (i >= 100) {
							clearInterval(t1);
							setTimeout(function(){
								$anal_progress.text('100%');
							},50)
							$analyzeEngine.css({'background-image':'url(themes/'+skin+'/simple/images/PopImages/analyzeEngine.png)','background-position':'-141px 0px'});
							if(len > 0){
								$.each(d,function(index,obj){
									var content = obj.sourceContent;
									//title="'+content.escapeHtml()+'"
									_html +='<li id='+obj.id+'><span class="oc_simp_stxt oc_simp_stxt_hidden">'+(index+1)+'.'+content+'</span><span class="oc_simp_more"></span></li>';
								});
							}else{
								_html +='<li class="noway_solve">抱歉，暂时没法解决您的问题</li>';
							}
							showAnalyList(_html);
						}else{
							i++;
							$anal_progress.text(i+'%');
						}
					}, random);
					}
				});
	}
	/**
	 * 显示分析列表
	 * */
	function showAnalyList(_html){
		$('.oc_simple_an_list,.solve_default').show();
		var $analyList = $('#analyList'),
			hasData = $analyList.html(_html).find('.noway_solve').length;
		if(hasData == 0){
			$analyList.on('click','.oc_simp_more',function(e){
				$(this).toggleClass('oc_simp_more_info').siblings('.oc_simp_stxt').toggleClass('oc_simp_stxt_hidden');
				e.stopPropagation();
			});
			$analyList.find('li:first').addClass('li_bor_fist').end().on('click','.oc_simp_stxt',function(){
				var $this = $(this).closest('li');
				$analyList.find('li').removeClass('change_cor');
				$this.addClass('change_cor');
				var id=$this.attr('id');
				startSovle(id);
				
				//下面snapshotStr都是为流程快照存储的，这儿是存的是分析列表的
				//selected标示被选中的行，以便高亮
				$analyList.find('.selected').removeClass('selected');
				$this.find('.oc_simp_stxt').addClass('selected');
				/*var selectedli = $analyList.find('li span.selected');
				selectedli.parent().html(selectedli.text());
				$this.wrapInner('<span class="selected"></span>');*/
				//告警ID
				snapshotStr.id = $this.attr('id');
				
				snapshotStr.analyzeList = [];
				var siblingLength = $this.parent().children().length;
				var current = $this.index()+1;
				//快照分析列表
				var analyzeListArr = getList(siblingLength,7,current);
				var len = analyzeListArr.length;
				for(var i = 0;i<len;i++){
					var text = $analyList.find('li:eq('+i+')').html();
					snapshotStr.analyzeList.push(text);
				}
			});
		}
	}
	
	/**
	 * 解决列表显示之前随机进度
	 * */
	function startSovle(id){
		$sovle_progress.text('0%');
		$sovle.css({'background-image':'url(themes/'+skin+'/simple/images/PopImages/analyzeEngine.png)','background-position':'0px 0px'});
		$('.pop_solve_list').hide();
		$('.solve_default').show();
		//这个定时器是为了模拟进度条，让用户能看到进度是从0开始的
		setTimeout(function(){
			$sovle.css({'background-image':'url(themes/'+skin+'/simple/images/PopImages/analyzing.gif)','background-position':'0px 0px'});
			solveList(id);
			i = 0
			var random=Math.floor(Math.random()*100),
				random2=Math.floor(Math.random()*30),
				t = setInterval(function(){//这个定时器是为了模拟进度条，并在一个随机的时间点停下，等待请求返回；
				if (i > random) {
					clearInterval(t);//清除定时器t有两种情况，1.当i大于随机数2.当requestAnalyzeData方法完成请求
				}
				$sovle_progress.text(i+'%');
				i++;
			}, random2);
		},500);
	}
	/**
	 * 解决列表
	 * */
	function solveList(id){
		oc.util.ajax({
			url:oc.resource.getUrl('simple/engineer/workbench/resolveFault.htm'),
			data:{knowledgeId:id},
			startProgress:false,
			stopProgress:false,
			success:function(t){
					var data = t.data,_html="",
						random=Math.floor(Math.random()*30),
						t1 = setInterval(function(){//这个定时器是为了模拟进度，请求成功之后，在上个定时器的基础上继续走进度
						if (i >= 100) {
							clearInterval(t1);
							setTimeout(function(){
								$sovle_progress.text('100%');
							},20)
							$sovle.css({'background-image':'url(themes/'+skin+'/simple/images/PopImages/analyzeEngine.png)','background-position':'-141px 0px'});
							$('.pop_solve_list').hide();
							if(data.length > 0){//data.length应该是大于0，现在调试改为等于0，好进pop02
								$.each(data,function(index,obj){
									var title = obj.resolveTitle||"";
									if(obj.isScript == 0){
										title = "【文档】"+title;
									}else{
										title = "【脚本】"+title;
									}
									_html +='<li id='+obj.id+'><span class="oc_simp_stxt oc_simp_stxt_hidden">'+(index+1)+'.'+title+'</span><span class="oc_simp_more"></span></li>';
								})
								showSolveList(_html);
							}else{
								$('.pop02').show(1000,function(){
									expertHelp();
									loadUploadResolve();
									});
							}
						}else{
							i++
							$sovle_progress.text(i+'%');
						}
					}, random);
				}
			});
	
	}
	function showSolveList(_html){
		$('.pop01').show();
		$('#pop01').off('click.more').on('click.more','.oc_simp_more',function(e){
			$(this).toggleClass('oc_simp_more_info').siblings('.oc_simp_stxt').toggleClass('oc_simp_stxt_hidden');
			e.stopPropagation();
		});
		$pop01.html(_html).find('li:first').addClass('li_bor_fist')
			.end().off('click.text').on('click.text','.oc_simp_stxt',function(){
			var $this = $(this).closest('li');
			$pop01.find('li').removeClass('change_cor');
			$this.addClass('change_cor');
			var id = $this.attr('id');
			loadDetailPage(id);
			$alertPageContainer.data("resolveId",id);
			
			//下面snapshotStr都是为流程快照存储的，这儿是存的是解决列表的
			//selected标示被选中的行，以便高亮
			$pop01.find('.selected').removeClass('selected');
			$this.find('.oc_simp_stxt').addClass('selected');
			/*var selectedli = $pop01.find('li span.selected');
			selectedli.parent().html(selectedli.text());
			$this.wrapInner('<span class="selected"></span>');*/
			//快照解决列表
			snapshotStr.solveList = [];
			var siblingLength = $this.parent().children().length;
			var current = $this.index()+1;
			var solveListArr = getList(siblingLength,7,current);
			var len = solveListArr.length;
			for(var i = 0;i<len;i++){
				var text = $pop01.find('li:eq('+i+')').html();
				snapshotStr.solveList.push(text);
			}
		});
	}
	/**
	 * 解决列表详情
	 * */
	function loadDetailPage(id){
		if($('#solveDetail').length==0){
			$('body').append('<div id="solveDetail"></div>');
		}else{
			$('#solveDetail').show();
		}
		var $solveDetail = $('#solveDetail');
		$solveDetail.data('solveDetail',id);
		$solveDetail.load('module/simple/engineer/workbench/resolve/solveDetail.html',function(){
			$('.transparent').css('opacity','0.7');
			
			$alertPageContainer.hide();
		});
	}
	/**
	 * 上传解决方案
	 * */
	function loadUploadResolve(){
		$('#btnUpload').off('click').on('click',function(){
			if($('#solveDetail').length==0){
				$('body').append('<div id="solveDetail"></div>');
			}
			var $solveDetail = $('#solveDetail');
			$solveDetail.load('module/simple/engineer/workbench/resolve/uploadResolve.html',function(){
				//$('.pop').hide();
				$alertPageContainer.hide();
			});
		})
	}
	/**
	 * 专家帮助
	 * */
	function expertHelp(){
		$('#btnHelp').off('click').on('click',function(){
			$('<div id="helpWindow"></div>').appendTo('body').load('module/simple/engineer/workbench/resolve/help.html');
		})
	}
	/**
	 * 取快照条数
	 * @param total 总共条数
	 * @param size  要取条数
	 * @param current 当前行的index+1 （从0开始的）
	 * @return Array 所需下标数组
	 * */
	function getList(total,size,current){//total=7,size=7,current=4,tmp=3
		var retn = [];
		//两侧大小
		var tmp=Math.floor(size/2);
		var left = current-tmp,right=current+tmp;
		if(left<=1){
			right+=(1-left);
			left=1;
		}
		if(right>=total){
			left-=(right-total);
			right=total;
		}
		for(var i=left;i<=right;++i){
			if(i>0){
				retn.push(i);
			}
		}
		return retn;
	}
});
