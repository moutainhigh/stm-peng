
function widgetManager(option){
    var _this = this;

    _this.option = $.extend({
        managerMode:false,
        gridster:'.gridster.ready',
        isQuery:true,
        widgetLoader:undefined,
        layoutId:undefined
    },option);

    _this.gridster = undefined; //操作网格的句柄
    _this.gridsterObj = undefined;
    _this.isEditPattern = false;
    _this.managerMode = _this.option.managerMode;//管理权限,用于控制编辑相关按钮的显示及隐藏
    _this.isQuery=_this.option.isQuery;//控制是否显示操作按钮
    _this.widgets = {};//已经加载的widget
    _this.data = {};//widget原始数据
    _this.widgetConfiges = {};//页面关联的widget配置信息
    _this.widgetLoader = _this.option.widgetLoader;
    
	_this.gridsterTpl = '<li data-id="{id}" data-row="{r}" data-col="{c}" data-sizex="{x}" data-sizey="{y}">'
		+'    <div class="gridster-mask">'
		+'	  <div class="gridster-box moveBtn">'
		+'	  <span class="gridster-title  moveBtn"></span>'
		// +'    <span class="fa fa-arrows locate-right moveBtn " title="移动"></span>'
		+'    <span class="fa icon-edit light_blue page-edit locate-right setBtn" title="编辑"></span>'
		+'    <span class="fa fa-refresh locate-right refreshBtn" title="刷新" ></span>'
		+'    <span class="fa fa-times-circle locate-right closeBtn" title="删除"></span>'
		+'    <span class="fa fa-arrows-alt locate-right maxBtn" title="全屏" ></span>'
		+'    </div>'
		+'    <div class="gridster-content"></div>'
		+'</div>'
		+'</li>';
	

    _this.init();
}

var wmp = widgetManager.prototype;

wmp.init = function(){
    var _this = this;

    if(_this.gridsterObj != null){
    	delete _this.gridsterObj;
    }
    _this.gridsterObj = $(_this.option.gridster);
    _this.gridsterObj.children().unbind().remove();
    $("#gridster-stylesheet").remove();//清除gridster的样式干扰

    _this.gridsterObj.html("<ul></ul>");

    _this.loadData(function(){
       _this._initData();
	   _this._regAutoRefresh();
    });
}

//从后台获取数据
wmp.loadData = function(fn){
    var _this = this;
    var isPreview = _this.option.isPreview;
    oc.util.ajax({
        url: oc.resource.getUrl('system/home/layout/getHomeLayout.htm'),
        params:{
            layoutId:_this.option.layoutId,
            dataType:'json'
        },
        stopProgress:function(){},
        startProgress:function(){},
        success: function (data) {
            if(data.code == '200'){
                _this.data = data.data;
                var cfg = data.data.homeLayoutModuleConfigList;
                
              //默认首页不存在则自动创建一个默认首页
                if(_this.data.homeLayoutBo == null){
                	oc.util.ajax({
						url: oc.resource.getUrl('system/home/layout/copyLayout.htm'),
						async:false,
						data: {
							layoutId: 2
						},
						timeout: null,
						success: function(copyData) {
							//修改复制页面为默认页面
							if(copyData.code == 200) {
								oc.util.ajax({
									url: oc.resource.getUrl('system/home/layout/updateHomeLayoutDefault.htm'),
									async:false,
									data: {
										layoutId: copyData.data.homeLayoutBo.id
									},
									timeout: null,
									success: function(rData) {
										if(rData.code == 200) {
											//刷新首页模块
											_this.option.layoutId = copyData.data.homeLayoutBo.id;
											_this.init();
										} 
									}
								});
							} else {
								alert("应用首页模板失败");
							}
						}

					});
                	return;
            	}
                //修改当前页签的名称
                if(isPreview == null){	//预览模式下不修改
                	$(".edit-mode .tab:first").text(_this.data.homeLayoutBo.name);
                }
                
                if(!cfg){
                   data.data.homeLayoutModuleConfigList = []; 
                }
                var wd = JSON.parse(_this.data.homeLayoutBo.layout);
                
                var cfgs = {};
                
                if(cfg != null){
                	if(isPreview == null){	//不是预览模式下才显示空白页提示
                		$("#hintHome").remove();
                		if(cfg.length == 0 || wd.widgets.length == 0){
                			var hintStr = "";
                			if(_this.isEditPattern == false){
                				$("#page-open").click();
                				hintStr = "<div id='hintHome'><span class='hintHomeOne'>请点击右上方 ";
                				hintStr += "<span class='page-edit light_blue icon-edit quickSelectDiscoverParamter' title='编辑' style='cursor:none;font-size:16px;'></span>"
                					hintStr += " 图标,将页面切换到编辑模式</span></div>"
                			}else{
                				hintStr = "<div id='hintHome'> ";
                				hintStr += "<span class='hintHomeTwo'>您可以点击“添加图表”按钮添加视图和图表，也可以点击“首页模板”按钮选择您想要的模板</span></div>"
                			}
                			_this.gridsterObj.before(hintStr);
                			if(_this.gridsterObj['selector'] == '.gridster.readall'){
                				var hd = $("#hintHome");
                				hd.css("position","absolute");
                				hd.css("margin-left","150px");
                				hd.css("margin-top","200px");
                			}
                		}
                	}
                	for(var i=0; i<cfg.length; i++){
                		var tp = cfg[i];
                		cfgs[tp.id] = tp;
                	}
                }
                _this.widgetConfiges = cfgs;
                
                fn && fn();
            }
        }
    });
}

wmp.refresh=function(index,type){
    var _this = this;
    _this.widgetLoader.refresh(index,type);
}

/**
*重新加载指定Id的单个模块
**/
wmp.reloadWidget =function(id){
    var _this = this;
    _this.loadData(function(){
        var dt = _this.widgetConfiges[id];
        var obj = _this.widgets[id];
        obj.args = $.extend({selector:obj.args.selector},JSON.parse(dt.props),true);
        obj.args.index = id;
        obj.selector = dt.moduleCode;
        _this.widgets[id] = obj;
        _this.widgetLoader.loadWedget(dt.moduleCode,obj.args);
    });
}  

wmp._initData = function(){
    var _this = this;
    var data = _this.data;
    var layout = data.homeLayoutBo.layout;
    layout = JSON.parse(layout);
    data.homeLayoutBo.layout = layout;

    //获取最大宽度
    var winWidth = _this.gridsterObj.width();
    var winHeight = _this.gridsterObj.height();



   // console.log("get--->" + winWidth + " " + winHeight);

    var ow = layout.w, oh = layout.h;

    var cols = 100;
    var rows = 100;
    if(_this.gridsterObj.selector == '.gridster.ready2'){//大屏需要增加单个模块高度
        rows = 99;
    }
    var widgetMargins = [1,1];


    var setWidth  = ow/cols * (winWidth/ow);
    var setHeight = oh/rows * (winHeight/oh);

    //console.log("set--->" + setWidth + " " + setHeight);
    var gul = _this.gridsterObj.find('ul');

    var gid = oc.util.generateId();
    _this.gridsterObj.attr('id',gid);
    
    if(_this.gridster != null){
    	_this.gridster.destroy();
    	_this.gridster = null;
    }

    _this.gridster = gul.gridster({
        widget_margins: widgetMargins,
        namespace:'#'+gid,
        widget_base_dimensions: ['auto', setHeight],
        min_cols:100,
        max_cols:100,
        min_rows:100,
        autogrow_cols:true,
        avoid_overlapped_widgets: true,
        //shift_larger_widgets_down: false,
        //autogenerate_stylesheet: true,
        after_browser_resized:function(a){
            setTimeout(function(){ 
                if($.support.fullscreenStatus()){
                    //console.log('全屏,不进行刷新模块');
                	window.clearInterval(window.timeid);
                    return;
                }
                if($('.tabs .tabs-box .tabs-inner.current-selected .tab').attr("data-type") == "manage-page"){
                	return;
                }
                _this.init();
            },100);
        },
        serialize_params: function($w, wgd) {
            return {
                id: $w.data('id'),
                c: wgd.col,
                r: wgd.row,
                x: wgd.size_x,
                y: wgd.size_y
            };
        },
        resize: { //编辑模式，可以改变大小
            enabled: true,
            start:function(e,ui,$widget){
                _addMask( $('body'));
            },
            stop: function(e, ui, $widget) {
                _removeMask( $('body'));
                _this._updateWidgetSize($widget); 
                $widget.find('.refreshBtn').trigger('click','updateSize');
            }

        }, 
        draggable: {
            handle: '.moveBtn',
            start:function(event, ui){
                $('body').css('cursor','move');
                _addMask( $('body'));
            },
            stop:function(event, ui){
                _removeMask( $('body'));
                $('body').css('cursor','');
            }
        }
    }).data('gridster');

    _this._loadWidgets(data);

    gul.data('layout-id', data.homeLayoutBo.id);

    if(!_this.managerMode){
        if(_this.gridster != null){
            _this.gridster.disable();
            _this.gridster.disable_resize();
        }
    }

    function _addMask(widget){
        widget.append('<div class="data-mask"></div>');
        widget.find(".data-mask").css({
            width:'100%',
            height:'100%',
            'z-index':1000,
            'position': 'absolute',
            'top': '0px',
            'left': '0px',
            'background-color':'#ccc',
            opacity:0.1
        });
    }

    function _removeMask(widget){
        widget.find(".data-mask").remove();
    }
}

wmp._regAutoRefresh = function(){
    var _this = this;
    if(window.timeid != null){
 	   window.clearInterval(window.timeid); 
    }
    var data = _this.data;
    var refreshTime = data.homeLayoutBo.refreshTime;
    if(!isNaN(refreshTime) && refreshTime >0){
        //限制最小的刷新时间为30s
        refreshTime = refreshTime * 1000 <= 30*1000 ? 30 * 1000 : refreshTime * 1000;
        window.timeid = window.setInterval(requestRefresh,refreshTime);
//        requestRefresh();
    }
    
    function requestRefresh(){
       
        setTimeout(function(){
            if(!_this.managerMode && $('.oc-fullscren').length<1){
                _this.refresh();
            }
//            if($(_this.option.gridster).length >0 && !_this.managerMode)
//             var id =window.requestAnimationFrame(requestRefresh);
//        	 window.cancelAnimationFrame(id);
//            _this.refresId = id;
        },1000);
    }
}

wmp._loadWidgets = function(data){
    var _this = this;

    var widgets = data.homeLayoutBo.layout.widgets;
    var cfgs = _this.widgetConfiges;
    var gridster = _this.gridster;
    if(gridster != null){
        gridster.remove_all_widgets();
    }else{
        return;
    }

    var loadingwidgets = {};
    var _loadWidgetObjs = [];
    $.each(widgets,function(index, item){
        var divobj = _this._format(_this.gridsterTpl, {
                'id': item.id,
                'x':item.x,
                'y':item.y,
                'c':item.c,
                'r':item.r
            });

        divobj = gridster.add_widget(divobj, item.x, item.y, item.c, item.r);
        _this._updateWidgetSize(divobj);
        
        _loadWidgetObjs.push({t:divobj,p:item});

        var tcfg = cfgs[item.id];
        var moduleCode = tcfg.moduleCode;
        if(moduleCode == null){
        	return;
        }
        var obj = JSON.parse(tcfg.props);
        obj.index = item.id;
        obj['selector'] = divobj.find(".gridster-content");
        if(!loadingwidgets[moduleCode]){
            loadingwidgets[moduleCode] = [];
        }
        loadingwidgets[moduleCode].push(obj);

        var data = {selector:moduleCode,args:obj};
        _this.widgets[item.id] = data;

    });
    _this.widgetLoader.loadWedgets(loadingwidgets);

    //更新模块的位置
    $.each(_loadWidgetObjs,function(k,tgt){
        var obj =tgt.t;
        var item = tgt.p;
        obj.attr('data-row',item.r).attr('data-col',item.c);
    });

    //注册模块事件
    _this.registerEventToWidget();
    
    //设置模块编辑状态
    _this.setManagerModeState(_this.managerMode);
    _this.setManagerPageShowOrHide(_this.isQuery);
}

wmp.resize = function(){
    this.gridster.recalculate_faux_grid();
}

/**
* 添加一个新的页面模块
* item = {id:'',x,y,c,r}
*/
wmp.addWidget = function(item,homeLayoutModuleConfig){
    var _this = this;
    item = _this.Adjustment(homeLayoutModuleConfig);

    //改变widget的row
    item.r = getMaxRow();

    _this.loadData(function(){
        _this._addWidget(item);

        //保存新的布局数据
        _this.saveData();
    });

    /*
    *获取最大的row
    */
    function getMaxRow(){
        var lis = _this.gridster.$widgets;
        var max = 1;
        for(var i =0; i<lis.length;i++){
            var obj = lis[i].dataset;
            //IE9不兼容改用原生js
            if(!obj){
            	obj = {};
            	obj.row = lis[i].getAttribute("data-row");
            	obj.sizey = lis[i].getAttribute("data-sizey");
            }
            var num = parseInt(obj.row) + parseInt(obj.sizey);
            max = num>max?num:max;
        }
        return max;
    }
}

wmp.Adjustment = function(data) {
	var _this = this;
	var winWidth = _this.gridsterObj.width();
    var winHeight = _this.gridsterObj.height();
    var colWidth = winWidth / 100;
    var rowHeight = winHeight / 100;
    
	var propObj = JSON.parse(data.props);
	var moduleCode = data.moduleCode;
	var number = 0;
	var x = 25;
    var y = 43;
    //根据不同模块类型调整模块大小
    if(moduleCode == 'virtual'){
    	x = parseInt(675 / colWidth);
		y = parseInt(475 / rowHeight);
    }else if(moduleCode == 'showWarning'){
    	if(propObj.warningCount == 5){
    		x = parseInt(685 / colWidth);
    		y = parseInt(230 / rowHeight);
    	}else{
    		x = parseInt(685 / colWidth);
    		y = parseInt(410 / rowHeight);
    	}
    }else if(moduleCode == 'statisticView'){
    	x = parseInt(365 / colWidth);
		y = parseInt(280 / rowHeight);
    }else if(moduleCode == 'topN'){
    	if(propObj.chartType == 'vertical'){
    		if(propObj.sortCount == 5){
    			x = parseInt(365 / colWidth);
				y = parseInt(340 / rowHeight);
    		}else{
    			x = parseInt(545 / colWidth);
				y = parseInt(350 / rowHeight);
    		}
    	}else if(propObj.chartType == 'horizontal'){
    		if(propObj.sortCount == 5){
    			x = parseInt(365 / colWidth);
				y = parseInt(265 / rowHeight);
    		}else{
    			x = parseInt(365 / colWidth);
				y = parseInt(440 / rowHeight);
    		}
    	}else{
    		if(propObj.sortCount == 5){
    			x = parseInt(400 / colWidth);
				y = parseInt(240 / rowHeight);
    		}else{
    			x = parseInt(400 / colWidth);
				y = parseInt(450 / rowHeight);
    		}
    	}
    }else if(moduleCode == 'topoView'){
    	x = parseInt(675 / colWidth);
		y = parseInt(475 / rowHeight);
    }else if(moduleCode == 'businessView'){
    	x = 100;
		y = 100;
    }else if(moduleCode == 'focusResource'){
    	if(propObj.showMethod == 'resourceDetail'){
    		x = parseInt(660 / colWidth);
			y = parseInt(315 / rowHeight);
    	}else {
    		x = parseInt(660 / colWidth);
			y = parseInt(223 / rowHeight);
    	}
    }else if(moduleCode == 'connectionState'){
    	x = parseInt(530 / colWidth);
		y = parseInt(223 / rowHeight);
    }else if(moduleCode == 'focusMetric'){
    	if(propObj.chartType == 'line-m' || propObj.chartType == 'area'){
    		x = parseInt(500 / colWidth);
			y = parseInt(240 / rowHeight);
    	}else if(propObj.chartType == 'radarv2'){
    		x = parseInt(380 / colWidth);
			y = parseInt(320 / rowHeight);
    	}else if(propObj.chartType == 'gaugev2'){
    		for(var i = 0; i < propObj.metrics.length; ++i){
    			itMetrics(propObj.metrics[i]);
    		}
    		x = parseInt(170 * number / colWidth);
			y = parseInt(200 / rowHeight);
    	}else if(propObj.chartType == 'metricCircle'){
    		for(var i = 0; i < propObj.metrics.length; ++i){
    			itMetrics(propObj.metrics[i]);
    		}
    		x = parseInt(200 * number / colWidth);
			y = parseInt(270 / rowHeight);
    	}else if(propObj.chartType == 'metricBlock'){
    		for(var i = 0; i < propObj.metrics.length; ++i){
    			itMetrics(propObj.metrics[i]);
    		}
    		x = parseInt(160 * number / colWidth);
			y = parseInt(180 / rowHeight);
    	}else if(propObj.chartType == 'thermometer'){
    		for(var i = 0; i < propObj.metrics.length; ++i){
    			IsTemperature(propObj.metrics[i]);
    		}
    		x = parseInt(150 * number / colWidth);
			y = parseInt(255 / rowHeight);
    	}
    }
    
    function itMetrics(data){
    	if(data.isParent){
    		var children = data.children;
    		for(var i = 0 ; i < children.length; ++i){
    			var obj = children[i];
    			itMetrics(obj);
    		}
    	}else{
    		number += 1;
    	}
    }
    
    function IsTemperature(data){
    	if(data.isParent){
    		if(data.type && data.type == 'Temperature'){
    			number = data.children.length;
    			return;
    		}else{
    			var children = data.children;
    			for(var i = 0 ; i < children.length; ++i){
    				var obj = children[i];
    				IsTemperature(obj);
    			}
    		}
    	}else{
    		number += 1;
    	}
    }
    
    return {id:data.id,x:x,y:y};
}

wmp._addWidget = function(item){
    var _this = this;
    if(!item.id){
        alert("模块id不能为空");
        return null;
    }

    var obj = _this.widgets[item.id];
    if(obj){
        alert("当前模块已经存在，不能重复添加");
        return null;
        
    }

    item.x = item.x?item.x:25;
    item.y = item.y?item.y:43;
    item.c = item.c?item.c:1;
    item.r = item.r?item.r:1;
   
    var divobj = _this._format(_this.gridsterTpl, {
        'id': item.id,
        'x':item.x,
        'y':item.y,
        'c':item.c,
        'r':item.r
    });

    divobj = _this.gridster.add_widget(divobj, item.x, item.y, item.c, item.r);
    _this._updateWidgetSize(divobj);

    //首先查询id在内对象中存在不，不存在则去后台获取；

    
    var data = _this.data;
    var widgets = data.homeLayoutBo.layout.widgets;
    var cfg = data.homeLayoutModuleConfigList;
    var cfgs = {};
    for(var i=0; i<cfg.length; i++){
        var tp = cfg[i];
        cfgs[tp.id] = tp;
    }
    var tcfg = cfgs[item.id];
    if(!tcfg){
        alert("模块配置信息不存在!");
        return null;
    }
    var moduleCode = tcfg.moduleCode;
    obj = JSON.parse(tcfg.props);
    obj.index = item.id;
    obj['selector'] = divobj.find(".gridster-content");
    _this.widgets[item.id] = {selector:moduleCode,args:obj};
    _this.widgetLoader.loadWedget(moduleCode,obj);
    _this.registerEventToWidget(divobj);

    return divobj;
}

wmp.remove_widget = function(id){
    var _this = this;
    oc.ui.confirm('确认删除？',function(){
        var gridster = _this.gridster;
        var item = _this.gridsterObj.find('ul>li[data-id=' + id + ']');
        delete _this.widgets[id];
        _this.widgetLoader.deleteWdeget(id);
        gridster.remove_widget(item);
        _this.saveData();
    });
}

wmp._updateWidgetSize = function(divobj){
    var w= divobj.width(),h=divobj.height();
    var margin = 10;
    divobj.find('.gridster-mask').css({
           'margin-left':margin+'px',
            'margin-bottom':margin+'px',
            height:(h-margin) + 'px',
            width:(w-margin) + 'px',
        });
}

wmp.saveData = function(){
    var gridster = this.gridster;
    var gridsterObj = this.gridsterObj;

    var layout = {};
    layout.w = gridsterObj.width();
    layout.h = gridsterObj.height();
    layout.widgets = gridster.serialize();

    var layoutId = gridsterObj.find("ul").data('layout-id');
    oc.util.ajax({
        url: oc.resource.getUrl('system/home/layout/updateLayout.htm'),
        data: {
            id: layoutId,
            layout: JSON.stringify(layout),
            name: 'layout-name',
            refreshTime: 10
        },
        success: function(data) {
            if(data.code == '200'){
                alert(oc.local.util.ajax.success);
            }
        }
    });
}

wmp.registerEventToWidget = function(obj){
    var _this = this;
    if(!obj){
       obj = this.gridsterObj.find("ul>li");
    }

    /*obj.unbind(".resize").bind('resize.resize',function(){
        var id = $(this).data("id");
        _this.refresh(id);
    });//*/

    //绑定刷新事件
    obj.each(function(){
        var refreshBtn = $(this).find(".refreshBtn");
        var id = $(this).data("id");
        refreshBtn.unbind();
        refreshBtn.bind('click',function(e,type){
            _this.refresh(id,type);
        });
    });

    //模块编辑事件
    obj.each(function(){
        var setBtn = $(this).find(".setBtn");
        var id = $(this).data("id");
        setBtn.unbind();
        setBtn.bind('click',function(e){
            var obj = _this.widgetConfiges[id];
            oc.resource.loadScript('resource/module/home/edit/js/widgetEdit.js',function(){
                oc.index.home.widgetedit.showConfigDialog({
                    widgetId: obj.moduleCode,
                    cfg:obj,
                    widgetManager:_this,
                    widgetLoader:_this.widgetLoader
                });
            });
        });
    });

    //模块全屏事件
    obj.each(function(){
        var maxBtn = $(this).find(".maxBtn");
        var id = $(this).data("id");
        _this.isFullScreen = false;
        maxBtn.unbind();
        maxBtn.bind('click',function(e){
        	window.clearInterval(window.fullScreenTimeId); 
        	if(_this.isFullScreen){
//      		$('.oc-m-page-open').css('display','block');
        		_this.isFullScreen = false;
        	}else{
//      		$('.oc-m-page-open').css('display','none');
        		_this.isFullScreen = true;
        	}
        	
        	
        	_this.fullScreen($('html'),function(status){
               
	            setTimeout(function(){
	                _this.widgetFullScreen(id,status);
	            },100);

            });

        });
    });

    //模块删除事件
    obj.each(function(){
        var closeBtn = $(this).find(".closeBtn");
        var id = $(this).data("id");
        closeBtn.unbind();
        closeBtn.bind('click',function(e){            
            _this.remove_widget(id);
        });
    });

    //模块编辑按钮展现开关
    obj.each(function(){
        $(this).unbind(".edit");
        $(this).bind("mouseover.edit",function(){
            $(this).find(".moveBtn .fa").show();
        });
        $(this).bind("mouseout.edit",function(){
            $(this).find(".moveBtn .fa").hide();
        });
    });

    if(_this.managerMode){
        //移动
        obj.each(function(){
            var $moveObj =  $(this).find('.moveBtn');
            $moveObj.unbind(".move").bind("mouseover.move",function(){
                $moveObj.css('cursor','move');
            });
            $moveObj.bind("mouseout.move",function(){
               $moveObj.css('cursor','');
            });
        });
    }
}

wmp.fullScreen = function(domObj,callback){
    var _this = this;

    if(!$.support.fullscreen){
       alert('浏览器不支持全屏');
        return false;
    }
    domObj.fullScreen({
        callback: function(status){
            if(status){
                $('body').css('overflow','hidden');
                $('.view-mode').hide();
               }
            else{
                $('body').css('overflow','');
                $('.view-mode').show();
               }
            if(callback){
                callback(status);
                return;
            }
            /*window.gridster = _this.gridster;
            if(status){
                 //domObj.find('.setBtn,.closeBtn').hide();
                 //设置是否能拖拽，最大化时不能拖拽
                 gridster.disable();
                 gridster.disable_resize();
            }else{
                //domObj.find('.setBtn,.closeBtn').show();
                gridster.enable();
                gridster.enable_resize();
            }//*/
        }
    });
}

/*设置首页模块编辑状态*/
wmp.setManagerModeState = function(state){
	//首页
	var obj = this.gridsterObj.find("ul li");
	if(obj){
		obj.each(function () {
			$(this).on("mouseover",function(event){
				if(state){
					$(this).find("span.setBtn").show();
					$(this).find("span.closeBtn").show();
				}else{
					$(this).find("span.setBtn").hide();
					$(this).find("span.closeBtn").hide();
				}
			})
		});
		this.managerMode = state;
	}

    if(state){
        this.gridster.enable();
        this.gridster.enable_resize();
    }else{
        this.gridster.disable();
        this.gridster.disable_resize();
    }

}
wmp.setManagerPageShowOrHide=function(isQuery){
	var obj = this.gridsterObj.find("ul li");
	if(obj){
		obj.each(function () {
			$(this).on("mouseover",function(event){
				if(isQuery){
			
					$(this).find("span.refreshBtn").show();
					$(this).find("span.maxBtn").show();
				}else{
					$(this).find("span.refreshBtn").hide();
					$(this).find("span.maxBtn").hide();
				}
			})
		});
		this.isQuery = isQuery;
	}

    if(isQuery){
        this.gridster.enable();
        this.gridster.enable_resize();
    }else{
        this.gridster.disable();
        this.gridster.disable_resize();
    }

}
/*模块的全屏事件*/
wmp.widgetFullScreen = function(id,status){
    if(status)
        this._widgetFullScreen(id);
    else
        $('.oc-fullscren').remove();
}

wmp._widgetFullScreen = function(id){
    var _this = this;
    $('.fullscren-widget').remove();
    $('body').append('<div class="oc-fullscren fullscren-widget gridster"/>');
    var $fw =  $('.fullscren-widget');
    $fw.append('<ul/>');
    var w = '100%',h=window.screen.height+'px';
	
    $fw.css({width:w,height:h,position:'absolute',top:'0px',left:'0px','z-index':10});


    var divobj = _this._format(_this.gridsterTpl, {});
    $fw.find('ul').html(divobj);
    $fw.find('li').css({width:'100%',height:'100%'});
    _this._updateWidgetSize($fw.find('li'));

    var pp =$.extend({}, _this.widgets[id],true);
    var type = pp.selector;
    pp = pp.args;
    pp.index = 'fullscreen_' + id;
    pp.selector = $fw.find('.gridster-content');

    _this.widgetLoader.loadWedget(type,pp);

    $fw.find('.setBtn,.closeBtn').remove();
    $fw.find('.maxBtn').unbind().bind('click',function(){
        $.fn.cancelFullScreen(function(){
            $fw.remove();
        });
    });
    $fw.find('.refreshBtn').unbind().bind('click',function(){
        _this.widgetLoader.refresh(pp.index);
    });
    //修改全屏按钮提示文字
    $fw.find('.maxBtn').attr("title","退出全屏");
    
    window.clearInterval(window.fullScreenTimeId); 
    if(_this.isFullScreen){
    	var data = _this.data;
	    var refreshTime = data.homeLayoutBo.refreshTime;
	    if(!isNaN(refreshTime) && refreshTime >0){
	        //限制最小的刷新时间为30s
	        refreshTime = refreshTime * 1000 <= 30*1000 ? 30 * 1000 : refreshTime * 1000;
	        window.fullScreenTimeId = window.setInterval(function(){
	        	$fw.find('.refreshBtn').click();
	        },refreshTime);
	    }
    }
    $('.oc-fullscren  ul li .gridster-box').css('height','50px');
}

wmp.destroy = function(){
    try{
    	if(this.timeid != null){
    		window.clearInterval(this.timeid); 
    	}
        this.gridster.destroy();
    }catch(e){
        throw e;
    }
}

/**
 * 替换字符串中的值
 * @param str 要替换的字符串
 * @param obj 值对象
 * @returns {*} 替换后的字符串
 */
wmp._format =function(str, obj) {
    for (var key in obj) {
        str = str.replace(RegExp("{" + key + "}", "g"), obj[key]);
    }
    return str;
}


//头部控制按钮部分的代码
function pageManage(option){
    var _this = this;
    _this.option = $.extend({
        managerMode:false,
        widgetManager:undefined
    },option);

    _this.widgetManager = _this.option.widgetManager;
    _this.widgetLoader =  _this.option.widgetLoader;

    if(_this.widgetManager == undefined || typeof _this.widgetManager != 'object'){
        throw '初始化参数错误';
    }

    var mgMode =  this.option.managerMode; 
    if(!mgMode){
        $(".page-mg").remove();
        return;
    }
    
    _this.init(); 
}

var pmp = pageManage.prototype;

pmp.init = function(){
    var _this = this;
    _this.regEvent();
     
}

pmp.regEvent = function(){
    var _this = this;
    
    //显示当前页面
    $(".edit-mode .tab").unbind().bind("click",function(){
        var dt = $(this).attr("data-type");
        var $right = $(".edit-mode .right"); 
        if(dt == 'show-page'){
			$('.tabs .tabs-box:nth-child(1) .tabs-inner').addClass("current-selected");
			$('.tabs .tabs-box:nth-child(2) .tabs-inner').removeClass("current-selected");
            $(".main-gridster").show();
            $(".main-gridster-manage").hide();
            $(".managetab").hide();
            $right.find(".manage-page").hide();
            $right.find(".show-page").show().css('display','inline-block');
            //刷新当前页面显示
            _this.widgetLoader.widgets = {};
            _this.widgetManager.init();
            //编辑模式下取消自动刷新
            if(window.timeid != null){
            	window.clearInterval(window.timeid); 
            }
        }else if(dt == 'manage-page'){
        	$('.tabs .tabs-box:nth-child(2) .tabs-inner').addClass("current-selected");
			$('.tabs .tabs-box:nth-child(1) .tabs-inner').removeClass("current-selected");
            $(".main-gridster").hide();
            $(".main-gridster-manage").show();
            $(".managetab").show();
            oc.resource.loadScript("resource/module/home/pagemanage/js/pagemanage.js", function(){
                oc.module.home.page.manage.open({type:'add'});
            });
            $right.find(".manage-page").show().css('display','inline-block');
            $right.find(".show-page").hide();
        }
    });

    $(".edit-mode .right a").unbind().bind("click",function(){
        var type = $(this).attr("data-type");
        _this._tabRightEventProcess(type);
    });


    $("#page-edit").unbind().bind("click",function(){
    	_this.widgetManager.isEditPattern = true;
        _this.widgetManager.setManagerModeState(true);
        //编辑模式下取消自动刷新
        if(window.timeid != null){
        	window.clearInterval(window.timeid); 
        }

    	//首页无默认模块修改提示
    	if($.isEmptyObject(_this.widgetManager.widgets) || 
    			_this.widgetManager.data.homeLayoutModuleConfigList == null || 
    			_this.widgetManager.data.homeLayoutModuleConfigList.length == 0){
    		var hintStr = "";
    		if(_this.widgetManager.isEditPattern == true){
    			hintStr = "<span  class='hintHomeTwo'>您可以点击“添加图表”按钮添加视图和图表，也可以点击“首页模板”按钮选择您想要的模板</span>"; 
    		}else{
    			hintStr = "<span  class='hintHomeOne'>请点击右上方<span class='page-edit light_blue icon-edit quickSelectDiscoverParamter' title='编辑' style='cursor:none;font-size:16px;'></span>"
				hintStr += " 图标,将页面切换到编辑模式</span>"
    		}
    		$("#hintHome").html(hintStr);
    	}
    	
		$('.tabs .tabs-box:nth-child(1) .tabs-inner').addClass("current-selected");
        $(".manage-page").hide();
        $(".show-page").show().css('display','inline-block');
        $(".edit-mode").show();
        $(".view-mode").hide();
        _this._genButton();
    });

	$("#page-close").unbind().bind("click",function(){
		$('.oc-m-page-open').css('background','rgba(0,0,0,0.2)');
		$('.view-mode').css('background','none');
        $(".view-mode-box").css('display','none');
		$("#page-close").css('display','none');
		$("#page-open").css('display','inline-block');
    });
	$("#page-open").unbind().bind("click",function(){
		$('.oc-m-page-open').css('background','none');
		$('.view-mode').css('background','rgba(0,0,0,0.5)');
        $(".view-mode-box").css('display','inline-block');
		$("#page-open").css('display','none');
		$("#page-close").css('display','inline-block');
    });

    $("#page-refesh").unbind().bind("click",function(){
       _this.widgetManager.refresh();
    });

    $("#page-fullscreen").unbind().bind("click",function(){
       _this.widgetManager.fullScreen($('html'),function(status){
            if(status){
                $('.fullscren-home').remove();
                $('body').append('<div class="oc-fullscren fullscren-home"/>');
                var $fw =  $('.fullscren-home');
                $fw.css({width:'100%',height:'100%',position:'absolute',top:'0px',left:'0px','z-index':11});
                oc.resource.loadScript('resource/module/home/pagemanage/js/createGirdster.js',function(){
                	oc.module.home.page.creategirdster.open({mainDiv:$fw,cfg:_this});
                });
            }else{
				  oc.resource.loadScript('resource/module/home/pagemanage/js/createGirdster.js',function(){
			    	oc.module.home.page.creategirdster.close({});
				  });
                  $('.fullscren-home').remove();
            }
       });

    });
}
pmp._genButton = function(){
	var _this = this,loginUser = oc.index.getUser();
    //添加图表
    $('.edit-mode .right a[data-type="edit-widge"]').linkbutton('RenderLB',{
        iconCls: 'fa fa-plus-square',
        onClick : function(){}
    });

    //保存
    $('.edit-mode .right a[data-type="save-change"]').linkbutton('RenderLB',{
        iconCls: 'fa fa-save',
        onClick : function(){}
    });
    
    //首页模板
    $('.edit-mode .right a[data-type="select-temp"]').linkbutton('RenderLB',{
        iconCls: 'fa fa-search',
        onClick : function(){}
    });

    //添加
    $('.edit-mode .right a[data-type="set-add"]').linkbutton('RenderLB',{
        iconCls: 'fa fa-plus-square',
        onClick : function(){}
    });
    //删除
    $('.edit-mode .right a[data-type="set-del"]').linkbutton('RenderLB',{
        iconCls: 'fa fa-trash-o',
        onClick : function(){}
    });
    //轮播设置
    $('.edit-mode .right a[data-type="set-slide"]').linkbutton('RenderLB',{
        iconCls: 'light_blue icon-carousel',
        onClick : function(){}
    });
    //权限设置
    $('.edit-mode .right a[data-type="set-authority"]').linkbutton('RenderLB',{
        iconCls: 'fa fa-cog',
        onClick : function(){}
    });
    
    if(!loginUser.systemUser){
    	$('.edit-mode .right a[data-type="set-authority"]').hide();
    }

    //浏览视图
    $('.edit-mode .right a[data-type="view-change"]').linkbutton('RenderLB',{
        iconCls: 'fa fa-eye',
        onClick : function(){
			$('.tabs .tabs-box:nth-child(2) .tabs-inner').removeClass("current-selected");
			$('.tabs .tabs-box:nth-child(1) .tabs-inner').addClass("current-selected");
			 //刷新当前页面显示
			_this.widgetLoader.widgets = {};
			_this.widgetManager.isEditPattern = false;
            _this.widgetManager.setManagerModeState(false);
			_this.widgetManager.init();
			$("#page-close").click();
        }
    });
}

pmp._tabRightEventProcess = function(type){
    var _this = this;
    switch(type){
        case 'view-change' : {
            $(".edit-mode").hide();
            $(".view-mode").show();
            $('.managetab').hide();
            $('.main-gridster').show();
			
            break;
        }
        case 'edit-widge' : {
            oc.resource.loadScript('resource/module/home/edit/js/widgetEdit.js?time',function(){
                oc.index.home.widgetedit.showConfigDialog({
                    widgetManager: _this.widgetManager,
                    widgetLoader:_this.widgetLoader
                });
            });
			
            break;
        }
        
        case 'select-temp' : {
        	oc.resource.loadScript('resource/module/home/pagemanage/js/homeTemplate.js',function(){
        		var tp = new homeTemplate(_this.widgetManager);
            });
            break;
        }
        
        case 'set-add' :{
              oc.resource.loadScript("resource/module/home/pagemanage/js/pagemanage.js", function(){
                      oc.module.home.page.manage.add({});
                    });
              break;
        }
        
        case 'set-del' :{
            oc.resource.loadScript("resource/module/home/pagemanage/js/pagemanage.js", function(){
            //	console.info("del");
                      oc.module.home.page.manage.del({});
                    });
            break;
        }
        case 'set-slide' :{
             oc.resource.loadScript("resource/module/home/pagemanage/js/autoFun.js", function(){
                      oc.module.home.page.auto.open({});
                    });
             break;
        }
        case 'save-change':{
            _this.widgetManager.saveData();
            break;
        }
        case 'set-authority':{
        	oc.resource.loadScript('resource/module/home/pagemanage/js/authority.js', function(){
        		oc.module.home.edit.authority.open();
    		});
        	break;
        }
        default:{
            alert("need peocess dataType:" + type);
            break;
        }
    }
}
