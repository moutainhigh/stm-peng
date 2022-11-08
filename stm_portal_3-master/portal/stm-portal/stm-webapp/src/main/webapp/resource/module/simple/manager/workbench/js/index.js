$(function(){
	var $alertWindow = $('#alertPageContainer');
	minHeight();
	switchMenu();
	reportManageList();
	businessManageList();
	businessCarsoul();
	closeAlertWindow();
	/**
	 * 报表输出台最小高度
	 * */
	function minHeight(){
		$('.Report_desk_M').css('min-height',$(window).height()-370-77);
	}
	/**
	 * 切换报表菜单和业务管理菜单
	 * */
	function switchMenu(){
		var $managerMenu = $('#managerMenu'),$leftMenu = $('.oc_simple_Mcontent_left'),clearSetTimeout,
			$menu = $('.M_lmenuDevelop_menu'),$menuList = $('#menuList'),
			jumpHeight = $leftMenu.find('#menuList').height(),$currentMenu,top,isClick=true,
			$menUp = $leftMenu.find('#menuUp'),$menuDown = $leftMenu.find('#menuDown');
		//切换菜单hover样式
		$managerMenu.on('click','li',function(){
			$(this).addClass('li_hover').siblings().removeClass('li_hover');
			$menuList.children('.currentMenu').removeClass('currentMenu');
			$menuList.children('ul').hide().eq($(this).index()).show().addClass('currentMenu');
			moveBtn();
		})
		.find('li:first').click().end()
		//显示菜单
		.on('mouseover',function(){
			//$leftMenu.animate({'left':'0'});
			$menu.show();
		});
		//鼠标离开菜单3秒后，菜单隐藏
		$leftMenu.on('mouseleave',function(){
			/*clearSetTimeout = setTimeout(function(){
				$leftMenu.animate({'left':'-165px'});
			},3000);*/
			$menu.hide();
		})
		/*.on('mouseover',function(){
			clearTimeout(clearSetTimeout);
		});*/
		//点击上下按钮模拟翻页
		$menUp.on('click',function(){
			if(isClick){
				$currentMenu = $leftMenu.find('.currentMenu'),top = $currentMenu.position().top;
				if(top < 0){
					isClick = false;
					$currentMenu.animate({'top':top+jumpHeight},200,function(){
						isClick = true;
						moveBtn();
					});
				};
			}
		})
		$menuDown.on('click',function(){
			if(isClick){
				$currentMenu = $leftMenu.find('.currentMenu'),top = $currentMenu.position().top;
				if($currentMenu.height() + top > jumpHeight){
					isClick = false;
					$currentMenu.animate({'top':top-jumpHeight},200,function(){
						isClick = true;
						moveBtn();
					});
				};
			}
		});
		/**
		 * 控制菜单翻页按钮的显示隐藏
		 * */
		function moveBtn(){
			var top = $menuList.find('.currentMenu').position().top;
			if(top < 0){
				$menUp.removeClass('oc_simp_gray_up').addClass('oc_simp_up');
			}else{
				$menUp.removeClass('oc_simp_up').addClass('oc_simp_gray_up');
			}
			if($menuList.find('.currentMenu').height() + top > jumpHeight){
				$menuDown.removeClass('oc_simp_gray_down').addClass('oc_simp_down');
			}else{
				$menuDown.removeClass('oc_simp_down').addClass('oc_simp_gray_down');
			}
		}
	}
	/**
	 * 切换报表
	 * */
	function switchReportList(){
		var $reportManage = $('#reportManage');
		$reportManage.off('click.switch').on('click.switch','li',function(){
			if(!$(this).find('.oc_simp_Mico').hasClass('symbol_hover')){
				$reportManage.find('.oc_simp_Mico').removeClass('symbol_hover');
				$reportManage.find('.develop_ol').remove();
				$(this).find('span:first').toggleClass('symbol_hover');
				analyzeReportList(this,$(this).attr('name'));
			}else{
				$(this).find('span:first').toggleClass('symbol_hover');
				$(this).find('.develop_ol').remove();
			}
		}).find('li:eq(0)').click();
		
		
	}
	/**
	 * 报表管理列表
	 * */
	function reportManageList(){
		oc.util.ajax({
			url:oc.resource.getUrl('simple/manager/workbench/queryReportTemplate.htm'),
			success:function(t){
				var html='',i=0,data=t.data||'',len=data.length,$reportMenu = $('#reportMenu');
				if(len > 0){
					for(;i<len;i++){
						var name = data[i].name;
							html +='<li class="oc_simp_de_bg  M_menu_img" name="'+data[i].id+'"><span class="Mtxt" title="'+name+'">'+name+'</span></li>'
					}
					$reportMenu.html(html).on('click','li',function(e){
						e.stopPropagation();
						var $this = $(this);
						$reportMenu.children('.oc_simp_de_bg_h').removeClass('oc_simp_de_bg_h');
						$this.addClass('oc_simp_de_bg_h');
						var id = $this.attr('name');
						JSON.parse(id);
						reportManage(id);
					}).find('li:first').click();
				}else{
					$('#reportMenu').html('<div style="padding:35px;">暂无列表数据</div>');
				}
			}
		});
	}
	/**
	 * 获取指定报表列表
	 * */
	function reportManage(id){
		var $reportManage = $('#reportManage'),html='';
		oc.util.ajax({
			url:oc.resource.getUrl('simple/manager/workbench/queryReportByTemplate.htm?templateId='+id),
			/*data:'templateId'+id,*/
			startProgress:false,
			stopProgress:false,
			success:function(data){
				//{id: 1, title: "业务运行报表(1)月", date: null, creater: "2014-12-19 10:42:46"}
				var i=0,d=data.data||'',len=d.length,t;
				if(len>0){
					for(;i<len;i++){
						t = d[i];
						html +='<li name="'+t.id+'" data-reportName="'+t.title+'" data-fileId="'+t.reportFileId+'"><span class="symbol oc_simp_Mico"></span><span class="ms_01">'+t.title+'</span><span class="ms_02">创建人：'+t.creater+'</span>\
							<span class="ms_03">生成日期：'+t.date+'</span><span class="ms_04 btn_select_info01">查看详情</span></li>';
					}
				}else{
					html ='<div class="table-dataRemind"></div>';
				}
				$reportManage.html(html).off('click.btn').on('click.btn','.btn_select_info01',function(e){
					e.stopPropagation();
					$alertWindow.data('currentObj',$(this));
					$alertWindow.data('reportFileId',$(this).closest('li').attr('data-fileId'));
					$alertWindow.data('reportId',$(this).closest('li').attr('name'));
					$alertWindow.data('reportName',$(this).closest('li').attr('data-reportName'));
					$alertWindow.load('module/simple/manager/workbench/report/mothReportDetails.html');
				});
				switchReportList();
			}
		})
	}
	/**
	 * 绑定数据
	 * */
	function bindReportData(id){
		oc.util.ajax({
			url:oc.resource.getUrl('simple/manager/workbench/getExpectById.htm?id='+id),
			data:{"id":id},
			success:function(data){
				$alertWindow.data('expect',data.data);
				$alertWindow.load('module/simple/manager/workbench/report/mothReportDetails.html');
			}
		});
	}
	
	/**
	 * 报表分析列表
	 * */
	function analyzeReportList(_this,id){
		oc.util.ajax({
			url:oc.resource.getUrl('simple/manager/workbench/queryExpectByReport.htm?reportId='+id),
			/*data:'reportId='+id,*/
			startProgress:false,
			stopProgress:false,
			success:function(data){
				var t = data.data||'',len = t.length,i=0,html='',$ol=$(_this).find('.develop_ol'),cls;
				/*if($ol.length>0){
					$ol.show();
				}else{*/
				//	$ol.remove();
					$(_this).append('<ol class="develop_ol"><ol>');
					var title = $(_this).children("span.ms_01").text();
					for(;i<len;i++){
						d=t[i];
						if(d.state==1){cls="oc_simp_Mprompt"}else{cls="oc_simp_Mwait";}
						html +='<li name="'+d.id+'"><span class="oc_simp_Mico '+cls+' ms_00"></span><span class="ms_01">'+title+'</span><span class="ms_02">创建人：'+d.creatorName+'</span><span class="ms_03">生成日期：'+d.createTimeStr+'</span><span class="ms_04 mb_handle btn_select_info dispose">处理解决</span><span class="ms_04 mb_handle btn_select_info btn_Mdelect" ><span class="btn_Mdelect_ico"></span>删除</span></li>';
					}
					$(_this).find('.develop_ol').html(html).off('click.stop').on('click.stop',function(e){
						e.stopPropagation();
					}).off('click.delete').on('click.delete','.btn_Mdelect',function(){
						_this = this;
						$.messager.confirm('确认','您确认想要删除记录吗？',function(r){    
						    if (r){    
						    	deleteAnalyzeReportById(_this);  
						    }    
						});
					}).off('click.dispose').on('click.dispose','.dispose',function(){
						var currentId = $(this).closest('li').attr('name');
						$alertWindow.data('reportFileId',$(this).parents('ol').closest('li').attr('data-fileId'));
						$alertWindow.data('reportId',id);
						$alertWindow.data('currentDispose',currentId);
						
						bindReportData(currentId);
					});
				//}
			}
		})
	}
	/**
	 * 删除指定报表分析
	 * */
	function deleteAnalyzeReportById(_this){
		var id = $(_this).closest('li').attr('name');
		oc.util.ajax({
			url:oc.resource.getUrl('simple/manager/workbench/deleteReportExpect.htm?expectId='+id),
			/*data:'expectId='+id,*/
			success:function(data){
				if(data.code==200){
					$(_this).closest('li').remove();
				}
			}
		});
	}
	/**
	 * 获取业务管理列表
	 * */
	function businessManageList(){
		var svgArr=[];
	    oc.resource.loadScript("resource/module/business-service/js/biz_edit_permissions.js",function(){
	        oc.business.service.edit.permissions.init();
			oc.util.ajax({
				//{"data":[{"id":1,"name":"业务运行报表（月）","cycleType":0}],"code":200}
				url:oc.resource.getUrl('portal/business/service/getAllViewBiz.htm'),
				success:function(data){
					var html='',i=0,d=data.data||'',len=d.length,t,$businessMenu = $('#businessMenu');
					if(len > 0){
						for(;i<len;i++){
							t = d[i];
							html +='<li class="oc_simp_de_bg  M_menu_img" bizid="' + t.id + '"><span class="'+statusFormatter(t.status)+' span_b"></span><span class="Mtxt">'+t.name+'</span> </li>';
						}
						$('#businessMenu').html(html).on('click','li',function(e){
							var $this = $(this);
							e.stopPropagation();
							$businessMenu.children('.oc_simp_de_bg_h').removeClass('oc_simp_de_bg_h');
							$this.addClass('oc_simp_de_bg_h');
							$alertWindow.load('module/simple/manager/workbench/biz/businessWindow.html',function(){
								$('#businessSvg').load(oc.resource.getUrl('resource/module/business-service/buz/business.html'),function(){
									oc.resource.loadScript('resource/module/business-service/buz/business.js',
					                        function(){
					                            new businessTopo({
					                                bizId:$this.attr('bizid'),
					                                showControlPanel:false
				                            });
				                    });
									
								});
								$('#ywxt').find('.pop_title').text($this.find('.Mtxt').text());
							});
							
						});
					}else{
						$('#businessMenu').html('<div style="padding:35px;">暂无列表数据</div>');
					}
				}
			});
	    });

	}
    function statusFormatter(value){
    	var appendCss = '';//' simple-light';
        if(!value){
           // return "light-ico graylight"+appendCss;
            return "light-ico greenlight"+appendCss;
        }else if(value ==3){
            return "light-ico redlight"+appendCss;
        }else if(value ==2){
            return "light-ico orangelight"+appendCss;
        }else if(value ==1){
            return "light-ico yellowlight"+appendCss;
        }else if(value ==0){
            return "light-ico greenlight"+appendCss;
        }
           
    }
	/**
	 * 业务管理自适应
	 */
	function adapt(canvas){
		edit = new Edit(canvas);
		var unit,service,app,physics;
		for(var i in canvas.topContainers){
			var con = canvas.topContainers[i];
			switch(con.data.type){
				case "unit":
					unit = con;
					break;
				case "service":
					service = con;
					break;
				case "app":
					app = con;
					break;
				case "physics":
					physics = con;
					break;
				default :
					break;
			}
		}
		//宽度自适应
		var editWidth = $(edit.C.dom).width()-18,editHeight = $(edit.C.dom).height()-5;
		edit.C.raphael.setSize(editWidth,editHeight);
		var old_new = (physics.y+physics.h)/editHeight,width = $(edit.C.dom).width()-20;
		var textWidth = edit.C.raphael.width*.16;
		findLane(canvas,edit,getLaneEdit);
		
		unit.click();
		edit.edit({x:textWidth,y:1,x1:width,y1:unit.h/old_new});
		service.click();
		edit.edit({x:textWidth,y:unit.y+unit.h,x1:width,y1:unit.y+unit.h+service.h/old_new});
		app.click();
		edit.edit({x:textWidth,y:service.y+service.h,x1:width,y1:service.y+service.h+app.h/old_new});
		physics.click();
		edit.edit({x:textWidth,y:app.y+app.h,x1:width,y1:app.y+app.h+physics.h/old_new});
		edit.hide();
		
	}
	function findLane(canvas,edit,fn){
		var unit,service,app,physics;
		for(var i in canvas.topContainers){
			var con = canvas.topContainers[i];
			if(con.data.type=="unit"){
				unit = con;
			}else if(con.data.type=="service"){
				service = con;
			}else if(con.data.type=="app"){
				app = con;
			}else if(con.data.type=="physics"){
				physics = con;
			}else{
				break;
			}
			con.click = function(){
				if(edit){
					edit.setCon(this);
					//没使用的编辑点
		            var unuse = ["lt","tr","r","rb","bl","l","t"];
		            for(var i=0;i<unuse.length;i++){
		            	edit.P[unuse[i]].hide();
		            }
					edit.edit = function(bbox){
						fn(bbox,this,unit,service,app,physics);
					};
				}
			}
			//不让拖动
			con.proxyDrag = function(dx,dy,x,y){}
		}
	}
	function getLaneEdit(bbox,_this,unit,service,app,physics){
		_this.con.texts[0].ry = (bbox.y1-bbox.y)/2 ;
		_this.con.rects[0].attr({height:bbox.y1-bbox.y,width:bbox.x1});
		_this.con.rects[1].attr({height:bbox.y1-bbox.y});
		_this.con.setBBox(bbox);
		unit.drag(0,0,unit.x,unit.y);
		service.drag(0,0,service.x,unit.y+unit.h);
		app.drag(0,0,app.x,service.y+service.h);
		physics.drag(0,0,physics.x,app.y+app.h);
		
	}
	/**
	 * load业务转盘
	 * */
	function businessCarsoul(){
		$('#businessCarsoul').load('module/simple/manager/workbench/biz/businessCarsoul.html',function(){
			$('#alertPageContainer').on('mousedown',function(e){
				e.stopPropagation(); //阻止弹出层之后，在弹出层拖动会导致转盘切换
			});
		});
	}
	/**
	 * 关闭窗口
	 * */
	function closeAlertWindow(){
		$alertWindow.on('click','.close_btn',function(){
			$alertWindow.empty();
		})
	}
});
/**
 * webkit字体不能小于12px的解决方案
 * */
function webkitFont(){
	 if($.browser.safari){  //苹果+chromen Webkit内核
         $("text").each(function(){
             var tspan = $(this).find("tspan");
             var html = tspan.html();
             if(html != "业务应用" && html != "业务单位" && html != "业务服务" && html != "支撑层"){
                 tspan.html("...");
             }else{
                 $(this).attr("transform","translate(-10,0)");
                 tspan.html(tspan.html().replace("业务应用","应用层").replace("业务单位","单位层").replace("业务服务","服务层"));
             }
         })
     }

}