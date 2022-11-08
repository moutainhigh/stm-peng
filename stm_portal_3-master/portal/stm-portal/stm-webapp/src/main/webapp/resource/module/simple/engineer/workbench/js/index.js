/**
 * 格式化时间
 * */
Date.prototype.format = function(fmt)   
{ //author: meizz   
  var o = {   
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "h+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}  

String.prototype.getByteLength=function(){
    //x00-\xff 代表非汉字编码 
    //一个汉字代表两个字节，所以那样了，不过应该是 
    return this.replace(/[^\x00-\xff]/g,"rr").length;           
}          

/**
 * n为要结束的下标数字
 * */
String.prototype.subByteLength = function(n) {    
    var r = /[^\x00-\xff]/g;    
    if(this.replace(r, "mm").length <= n) return this;    
    // n = n - 3;    
    var m = Math.floor(n/2);    
    for(var i=m; i<this.length; i++) {    
        if(this.substr(0, i).replace(r, "mm").length>=n) {    
            return this.substr(0, i) +"..."; 
        }    
    } 
    return this;    
}; 
String.prototype.escapeHtml = function () {
    var entityMap = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': '&quot;',
        "'": '&#39;',
        "/": '&#x2F;'
    };
    return String(this).replace(/[&<>"'\/]/g, function (s) {
        return entityMap[s];
    });
}
var snapshotStr={};//存储快照对象
$(function(){
var $ul_unsolved_question = $('#ul_unsolved_question'),
	$ul_solve_question = $('#ul_solve_question'),
	$li_unsolved = $('#li_unsolved'),
	$li_solve = $('#li_solve'),
	$oc_simple_desk_ul = $('.oc_simple_desk_ul'),
	$container = $('#alertPageContainer'),
	$searchInput = $('#searchInput'),
	$oc_simple_content_right = $('.oc_simple_content_right'),
	isLoadding = true,
	unsolvedSearchIp = "",//要查询的未解决问题IP
	solveSearchIp = "",//要查询的已解决问题IP
	idArr=[];
/**
 * 初始化
 * */
(function (){
	//因为要显示问题条数，所以要先请求一次
	loadUnsolvedList();
	loadSolveList();
	/*document.body.onselectstart = function () { 
	    return false; 
	};*/
})();
/**
 *控制IP查询框 
 * */
(function(){
	$oc_simple_content_right.find('.datagrid-title-ico').on('mouseover',function(){
		$searchInput.show().find('.hide').siblings('input').focus();
	});
	$searchInput.on('mouseleave',function(){
		$searchInput.hide();
		var id = $('.li_hover').attr('id');
		if("li_unsolved" == id){
			loadUnsolvedList('mouseleave');
		}else{
			loadSolveList('mouseleave');
		}
	}).on('blur','input',function(){
		$searchInput.hide();
	}).on('keyup','input',function(e){
		if(e.keyCode == 13){
			$searchInput.mouseleave();
		}
	});
})();
/**
 * 切换解决问题和未解决问题
 * */
(function (){
	var $oc_simple_content_left_ul = $('.oc_simple_content_left_ul');
	/*点击左边ul，切换未解决列表和已解决列表*/
	$oc_simple_content_left_ul.on('click','li',function(){
		if(!$(this).hasClass('li_hover')){
			$oc_simple_content_left_ul.find('.li_hover').removeClass('li_hover');
			$(this).addClass('li_hover');
			$searchInput.find('input').toggleClass('hide');
			$('.oc_simple_content_right').toggleClass('content_right_ul_show');
		}
	});
	$li_unsolved.click();
})();
/**
 * 当滚动条即将滚动到底部时懒加载列表
 * */
(function (){

	var timeout;
	$('.oc_simple_content_right').on('scroll',function(){
		/*清除定时器，防止滚动滚动条是多次触发请求(IE)*/
		clearTimeout(timeout);
			/*当请求完成并且在页面上显示之后才能继续请求*/
			if(!isLoadding){
					/*40代表表头的高度*/
				 var $this = $(this),
				 	 scorllBottom = $this.find('.oc_simple_desk_ul').height() +20 - $(this).height();
				 		
				 	 scrollTop = $this.scrollTop();
				 	
	
				 /*当滚动条滚动到底部时才触发请求，请求列表数据*/
				 if(scrollTop >= scorllBottom){

					 timeout = setTimeout(function(){
						 if($this.find('ul').attr('id')=="ul_unsolved_question"){
							 loadUnsolvedList();
						 }else{
							 loadSolveList();
						 }
					 },200);
				 }
			}
	});
})();
/**
 * 加载未解决问题列表
 * */
function loadUnsolvedList(){
	var nodeLength = $ul_unsolved_question.find('li').length,
		end=true;
	if(end){
		var searchIp = $searchInput.find('.hide').siblings('input').val(),data = {startRow:nodeLength,rowCount:40};
		if(arguments[0]=="mouseleave"){//改变输入框的查询IP则清空列表并查询
			if(unsolvedSearchIp != searchIp){
				unsolvedSearchIp = searchIp;
				$ul_unsolved_question.empty();
				idArr=[];
				nodeLength = 0;
				data = {startRow:nodeLength,rowCount:40,likeSourceIP:searchIp}; 
			}else{
				return;
			}	
		}else{
			if(searchIp){
				data = {startRow:nodeLength,rowCount:40,likeSourceIP:searchIp}; 
			}
		}
	oc.util.ajax({
		url:oc.resource.getUrl('simple/engineer/workbench/getResolveFault.htm'),
		data:data,
		success:function(t){
			isLoadding = true;
			var count = t.data.totalRecord,titleShow = count;
			if(count > 99){
				count = 99+'<a class="superscript">+</a>';
			}
			$li_unsolved.find('.unsolve_count').html(count).attr('titile',titleShow);
			var rowsArr = t.data.rows,len = rowsArr.length,currentArr=[],_html = "",i = 0;
			if(titleShow > 0){
				if(len > 0){
					for(;i<len;i++){
						nodeLength = nodeLength + 1;
						currentArr = rowsArr[i];
						var id = "alarmId"+currentArr.eventID,content,sourceName,produceTime,IP;
						idArr.push({"id":id,"sourceID":currentArr.sourceID,"ext0":currentArr.ext0,"ext1":currentArr.ext1,"ext2":currentArr.ext2,"ext3":currentArr.ext3,"eventId":currentArr.eventID});
						produceTime = currentArr.collectionTime;
						if(produceTime){
							produceTime = currentArr.collectionTime.toDate().stringify();
						}else{
							produceTime = '--';
						}
						IP = currentArr.sourceIP;
						if(!IP){IP = '--';}
						content = currentArr.content;
						if(!content){content = '--';}
						sourceName = currentArr.sourceName;
						if(!sourceName){sourceName = '--'}
						_html += '<li><span class="title_span_00">'+nodeLength+'</span><span class="oc_simple_light title_span_01 '+currentArr.level+
								'"></span><span title="'+content.escapeHtml()+'" class="title_span_02">'+content+'</span><span title="'+sourceName.escapeHtml()+'" class="title_span_03">'+sourceName+'</span> <span  class="title_span_04">'+IP+'</span>'+
								 '<span  class="title_span_05">'+currentArr.alarmTypeName+'</span> <span  class="title_span_06" title="'+produceTime.escapeHtml()+'">'+produceTime+'</span>';
						if(currentArr.ext4 == 1){//0表示未解决，1表示已解决
							_html += '<span  class="title_span_07 open_btn" id="alarmId'+currentArr.eventID+'">系统验证中</span></li>';
						}else{
							_html += '<span  class="title_span_07 open_btn startAnalyzeEngine" id="alarmId'+currentArr.eventID+'">启动分析引擎</span></li>';
						}
					}
					
					$ul_unsolved_question.append(_html)
				}else{
					end=false;
					alert("数据加载完毕！");
				}
			}else{
				$ul_unsolved_question.html('<div class="table-dataRemind">抱歉，没有可展示的数据！</div>');
			}
			
			isLoadding = false;
			}
		});
	}
}
/**
 * 加载已解决问题列表
 * */
function loadSolveList(){
	var nodeLength = $ul_solve_question.find('li').length,
		end=true;
	//数据全部请求完之后不再发请求
	if(end){
		var searchIp = $searchInput.find('.hide').siblings('input').val(),data = {startRow:nodeLength,rowCount:40};
		if(arguments[0]=="mouseleave"){//改变输入框的查询IP则清空列表并查询
			if(solveSearchIp != searchIp){
				solveSearchIp = searchIp;
				$ul_solve_question.empty();
				nodeLength = 0;
				data = {startRow:nodeLength,rowCount:40,likeSourceIP:searchIp}; 
			}else{
				return;
			}	
		}else{
			if(searchIp){
				data = {startRow:nodeLength,rowCount:40,likeSourceIP:searchIp}; 
			}
		}
	oc.util.ajax({
		url:oc.resource.getUrl('simple/engineer/workbench/getResolvedFault.htm'),
		data:data,
		success:function(t){
			isLoadding = true;
			var count = t.data.totalRecord,titleShow = count;
			if(count > 99){
				count = 99+'<a class="superscript">+</a>';
			}
			$li_solve.find('.solve_count').html(count).attr('titile',titleShow);;
			var rowsArr = t.data.rows,len = rowsArr.length,currentArr=[],_html = "",i = 0,produceTime,IP;
			if(titleShow > 0){
				if(len > 0){
					for(;i<len;i++){
						nodeLength = nodeLength + 1;
						currentArr = rowsArr[i];
						produceTime = currentArr.collectionTime;
						if(produceTime){
							produceTime = currentArr.collectionTime.toDate().stringify();
						}else{
							produceTime = '--';
						}
						IP = currentArr.sourceIP;
						if(!IP){IP = '--';}
						content = currentArr.content;
						if(!content){content = '--';}
						sourceName = currentArr.sourceName;
						if(!sourceName){sourceName = '--'}
						_html += '<li><span class="title_span_00">'+nodeLength+'</span><span class="oc_simple_light title_span_01 '+currentArr.level+'"></span><span title="'+content.escapeHtml()+'"  class="title_span_02">'+content+'</span><span title="'+sourceName.escapeHtml()+'" class="title_span_03">'+sourceName+'</span> <span  class="title_span_04">'+IP+'</span>'+
						 '<span  class="title_span_05">'+currentArr.ext1+'</span> <span  class="title_span_06">'+produceTime+'</span><span name="'+currentArr.eventID+'"  class="title_span_07 open_btn" id="'+currentArr.eventID+'">查看</span></li>';
					}
					$ul_solve_question.append(_html);
				}else{
					end=false;
					alert("数据加载完毕！");
				}
			}else{
				$ul_solve_question.html('<div class="table-dataRemind">抱歉，没有可展示的数据！</div>');
			}
			isLoadding = false;
			}
	});
	}
}
/**
 * 已解决问题处理流程快照
 * */
(function (){
	$ul_solve_question.on('click','.open_btn',function(){
		$container.data('faultId',$(this).attr('name'));
		$container.load('module/simple/engineer/workbench/resolved/flowSnapshot.html',function(){
			$('.transparent').css('opacity','0.7');
		});
	});
})();
/**
 * 启动分析引擎
 * */
(function (){
	$ul_unsolved_question.on('click','.startAnalyzeEngine',function(nodeLength){
		//绑定数据
		snapshotStr.title = $(this).siblings('.title_span_02').attr('title');
		var title = snapshotStr.title;
		var index = $ul_unsolved_question.find('li').index($(this).parents('li'));
			nowArr = idArr[index];
		$ul_unsolved_question.data("analyzeData",{"sourceID":nowArr.sourceID,"ext0":nowArr.ext0,"ext1":nowArr.ext1,"ext2":nowArr.ext2,"ext3":nowArr.ext3});
		$container.data("eventId",nowArr.eventId);
		
		$container.load('module/simple/engineer/workbench/resolve/analyzeEngine.html',function(){
			$container.show();
			$('.transparent').css('opacity','0.7');
		});
	});
})();
/**
 * 关闭分析引擎界面或关闭流程查看快照
 * */
(function (){
	$container.on('click','.oc_simple_close_btn',function(){
		$container.empty();
	});
})();
});
