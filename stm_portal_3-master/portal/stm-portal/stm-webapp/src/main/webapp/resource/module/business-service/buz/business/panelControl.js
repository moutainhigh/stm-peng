buzdefine("bz/panelControl", ["bz/Base","bz/extend"], function (Base,extend) {
    var f = extend(function(args){
    	this.args=$.extend({
    		bizCfg:null,
            bizId:null
    	},args);
        if(args.topoSVG && args.bizCfg){
            this.bizCfg  = args.bizCfg;
            this.topoSVG = args.topoSVG;

            this.init();
        }else{
            throw "bizCfg can't be null";
        }
    },Base,{
        init:function(){

            var bizId = this.args.bizId;
            
            this.editAble = this.bizCfg.get("editAble");
            var scp = this.bizCfg.get("showControlPanel");
            this.setEditAble(this.editAble);
            this.showControlPanel(scp);

            this.updateStatus();
            this.bizCfg.addPanelTools(this);
            this.regEvent();
        },
        updateStatus:function(){

            var nds= this.bizCfg.selected.nodes;
            var node=undefined,link=undefined;
            for(var o in nds){
                node = nds[o].ctx.data;
                //console.log(node);
                break;
            }

            var lks= this.bizCfg.selected.links;
            for(var o in lks){
                link = lks[o].data;
                //console.log(link);
                break;
            }
            if(!link){
               link = this.bizCfg.get('link');
            }else{
                this.bizCfg.set('link',link);
            }

            $(".line-type-box").hide();
            var type = link.attr.type;
            var tobj = $(".bar_div.line-type-style .line-type span.tool_bar_ico ");
            if(type == 'poly'){
                tobj.attr("title","折线");
                tobj.removeClass("line").addClass("poly");
            }else if(type == 'line'){
                type = 'line';
                tobj.attr("title","直线");
                tobj.removeClass("poly").addClass("line");
            }

            var arrow = link.attr.arrow;
            tobj = $(".bar_div.line-arrow .line-type span.tool_bar_ico ");

            if(arrow == 'none'){ 
                type = 'none';
                tobj.attr("title","不带箭头");
                tobj.removeClass("singal-arrow double-arrow").addClass("none-arrow");
            }else if(arrow == 'single-arrow'){
                type = 'single-arrow';
                tobj.attr("title","单箭头");
                tobj.removeClass("none-arrow double-arrow").addClass("singal-arrow");
            }else if(arrow == 'double-arrow'){
                type = 'double-arrow';
                tobj.attr("title","双箭头");
                tobj.removeClass("none-arrow singal-arrow").addClass("double-arrow");
            }

            tobj = $(".bar_div.line-dash .line-type span.tool_bar_ico ");
            var dash = link.attr.attr['stroke-dasharray'];
            if(dash && dash.indexOf(',')>-1){
                tobj.attr("title","虚线");
                tobj.removeClass("solid-line").addClass("dash-line");
            }else{
                tobj.attr("title","实线");
                tobj.removeClass("dash-line").addClass("solid-line");
            }

            var lw = link.attr.attr['stroke-width'];
            tobj = $(".bar_div.line-width .line-type-box>div")
            tobj.each(function(k,v){
                //var $(this)
                var val = parseInt($(this).text());
                if(val == lw){
                    $(this).addClass("actice_tool");
                }else{
                    $(this).removeClass("actice_tool"); 
                }
            });

            if(!this.drawTextBox){
                $(".line-type.add-text-box").removeClass("actice_tool add-text-box");
            }
            if(!node){
                node = this.bizCfg.get('node');
            }else{
                if(!node.attr.bgStyle){
                    node.attr.bgStyle = this.bizCfg.get('node').attr.bgStyle;
                }
                if(!node.attr.fontStyle){
                    node.attr.fontStyle = this.bizCfg.get('node').attr.fontStyle;
                }
                this.bizCfg.set('node',node);
            }

            var t=node.attr.fontStyle;
            if(t['font-size']){
                $(".font-input-value").val(t['font-size']);
            }

            if(t['font-weight']){
                $(".font-weight").css({'font-weight':t['font-weight']});
                if(t['font-weight'] == 'bold')
                    $(".bar_div.font-weight .line-type").addClass("actice_tool font-weight-bold");
                else
                    $(".bar_div.font-weight .line-type").removeClass("actice_tool font-weight-bold");
            }else{
                $(".bar_div.font-weight .line-type").removeClass("actice_tool font-weight-bold");
            }
        },
        showControlPanel:function(showControlPanel){
            if(!showControlPanel){
            	$('#navigation_hide').hide();
                $("#bs_toolbar").hide();
                $("#right-buttons").hide();
                $(".panel-control").hide();
            }else{
                if($("#bs_toolbar").is(":hidden")){
                    $("#bs_toolbar").show();
                    $("#right-buttons").show();
                    $(".panel-control").show();
                }
            }
        },
        setEditAble:function(able){
            this.editAble = able;
            
            if(!able){
                $("#right-buttons button").hide();
                $("#right-buttons #b-return").show();
                
                $("#bs_toolbar .line-type span,#bs_toolbar .font-size").css({
                     'filter':'alpha(opacity=50)',
                     '-moz-opacity':0.2,
                     '-khtml-opacity':0.2,
                      'opacity': 0.2,
                });

                $("#bs_toolbar .modify-attr .refresh span,#bs_toolbar .other-attr .line-type span").css({
                     'filter':'alpha(opacity=100)',
                     '-moz-opacity':1,
                     '-khtml-opacity':1,
                      'opacity':1,
                });

                $('#navigation_hide').hide();
            }else {
                $("#right-buttons button").show();

                $("#bs_toolbar .line-type span,#bs_toolbar .font-size").css({
                     'filter':'alpha(opacity=100)',
                     '-moz-opacity':1,
                     '-khtml-opacity':1,
                      'opacity':1,
                });

                $('#navigation_hide').show();
            }
        },
        isDrawTextBox:function(){
            return (this.drawTextBox == true);
        },
        setDrawTextBox:function(able){
            this.drawTextBox = (able == true);
            if(this.drawTextBox)
                $(".bar_div.text-box .line-type").addClass('add-text-box');
            else
                $(".bar_div.text-box .line-type").removeClass('actice_tool add-text-box');
        },
        regEvent:function(){
            var _this = this;
            var pbs = $("#panel-control").offset();
            $("#color-specturm").css({top:'100px',left:'100px','min-width':'280px','max-width':'440px',position:'absolute'}).hide();
            $("#bs_toolbar .drag_area").unbind().bind('mousedown',function(e0){//拖动工具栏
                if(e0.which !=1)
                    return;
                var sx = e0.clientX, sy = e0.clientY;
                var bspbs = $("#bs_toolbar").offset();
                $(document).unbind('.bs_toolbar');
                $(document).bind('mousemove.bs_toolbar',function(e){
                    var detx = e.clientX-sx, dety = e.clientY-sy;
                    var x = bspbs.left + detx - pbs.left,y = bspbs.top+dety -pbs.top;
                    x = x<0?0:x;
                    y = y<0?0:y
                    
                    $("#bs_toolbar").css({top: y +'px',left: x+'px'});
                    stopEvent(e);
                    stopBubble(e);

                    //这部分主要用于屏蔽处理IE自身的选中功能
                    try{
                          if(document.selection){//IE ,Opera
                                if(document.selection.empty)
                                    document.selection.empty();//IE
                                else{//Opera
                                    document.selection = null;
                              }
                          }else if(window.getSelection){//FF,Safari
                               window.getSelection().removeAllRanges();
                          }
                    }catch(e){}

                });
                $(document).bind('mouseup.bs_toolbar',function(e){
                    $(document).unbind('.bs_toolbar');
                });
                stopEvent(e0);
                stopBubble(e0);
            });

            $(".line-type").bind('mouseover',function(){
                if(!_this.editAble){
                    return;
                }
                $(this).addClass("line-type-hover");
            });
            $(".line-type").bind('mouseout',function(){
                $(this).removeClass("line-type-hover");
            });
            $(".line-type").bind('mouseup',function (e) {
                if(!_this.editAble){
                    return;
                }

                $(".line-type-box").hide();
                
                var p = $(this).parent();
                var triger =(p.hasClass("save") ||  p.hasClass("refresh") ||p.hasClass("delete") || p.hasClass("health-btn") )
                if(!triger){
                    $(".bar_div .line-type.actice_tool").removeClass("actice_tool");
                    $(this).addClass("actice_tool");
                }

                if($(this).next().length>0)
                    $(this).next().show();
            });
            $(".bar_div.line-type-style .line-type-box>div").bind('click',function (e) {//线条类型
                if(!_this.editAble){
                    return;
                }

                var obj = $(this).find("span");
                var tobj = $(this).parent().parent().find(".line-type span.tool_bar_ico ");
                var type = 'poly';
                if(obj.hasClass("poly")){
                    tobj.attr("title","折线");
                    tobj.removeClass("line").addClass("poly");
                }else if(obj.hasClass("line")){
                    type = 'line';
                    tobj.attr("title","直线");
                    tobj.removeClass("poly").addClass("line");
                }
                //console.log(type);
                $(".line-type-box").hide();

                var val = type;
                var objs = _this.bizCfg.selected.links;
                for(o in objs){
                    var obj = objs[o];
                    var data = obj.getValue();
                    data.attr.type = val;
                    obj.setValue(data); 
                }

                var p = _this.bizCfg.get('link');
                p.attr.type = val;
                _this.bizCfg.set('link',p);
            });

            $(".bar_div.line-arrow .line-type-box>div").bind('click',function (e) {//线条箭头
                if(!_this.editAble){
                    return;
                }
                var obj = $(this).find("span");
                var tobj = $(this).parent().parent().find(".line-type span.tool_bar_ico ");
                var type = 'none';
                if(obj.hasClass("none-arrow")){ 
                    type = 'none';
                    tobj.attr("title","不带箭头");
                    tobj.removeClass("singal-arrow double-arrow").addClass("none-arrow");
                }else if(obj.hasClass("singal-arrow")){
                    type = 'single-arrow';
                    tobj.attr("title","单箭头");
                    tobj.removeClass("none-arrow double-arrow").addClass("singal-arrow");
                }else if(obj.hasClass("double-arrow")){
                    type = 'double-arrow';
                    tobj.attr("title","双箭头");
                    tobj.removeClass("none-arrow singal-arrow").addClass("double-arrow");
                }
                //console.log(type);

                $(".line-type-box").hide();

                var val = type;
                var objs = _this.bizCfg.selected.links;
                for(o in objs){
                    var obj = objs[o];
                    var data = obj.getValue();
                    data.attr.arrow = val;
                    obj.setValue(data); 
                }

                var p = _this.bizCfg.get('link');
                p.attr.arrow = val;
                _this.bizCfg.set('link',p);

            });

            $(".bar_div.line-dash .line-type-box>div").bind('click',function (e) {//设置虚线
                if(!_this.editAble){
                    return;
                }
                var obj = $(this).find("span");
                var tobj = $(this).parent().parent().find(".line-type span.tool_bar_ico ");
                var val = '0';
                if(obj.hasClass("solid-line")){ 
                    val = '0';
                    tobj.attr("title","实线");
                    tobj.removeClass("dash-line").addClass("solid-line");
                }else if(obj.hasClass("dash-line")){
                    val = 'dot-line';
                    tobj.attr("title","虚线");
                    tobj.removeClass("solid-line").addClass("dash-line");
                }
                if(val == 'dot-line')
                    val = '8,8';
                else
                    val = '0';
               
                $(".line-type-box").hide();

                var objs = _this.bizCfg.selected.links;
                for(o in objs){
                    var obj = objs[o];
                    var data = obj.getValue();
                    data.attr.attr['stroke-dasharray'] = val;
                    obj.setValue(data); 
                }

                var p = _this.bizCfg.get('link');
                p.attr.attr['stroke-dasharray'] = val;
                _this.bizCfg.set('link',p);
            });

            $(".bar_div.line-width .line-type-box>div").bind('click',function (e) {//线条宽度
                if(!_this.editAble){
                    return;
                }
                var obj = $(this).find("span");
                var tobj = $(this).parent().parent().find(".line-type span.tool_bar_ico");
                var val = parseInt(obj.text());
                $(".bar_div.line-width .line-type-box>div").removeClass("actice_tool");
                $(this).addClass("actice_tool");
                //console.log(val);
                $(".line-type-box").hide();
                var objs = _this.bizCfg.selected.links;
                for(o in objs){
                    var obj = objs[o];
                    var data = obj.getValue();                    
                    data.attr.attr['stroke-width'] = val;
                    obj.setValue(data); 
                }

                var p = _this.bizCfg.get('link');
                p.attr.attr['stroke-width'] = val;
                _this.bizCfg.set('link',p);
            });

            $(".line-color").bind('click',function(e){//连线颜色
                if(!_this.editAble){
                    return;
                }
                var x = e.clientX-pbs.left+10,y=e.clientY-pbs.top+10;
                $("#color-specturm").css({top: y +'px',left: x+'px'});
                if($("#color-specturm").is(":hidden"))
                    $("#color-specturm").show();
                //console.log('tt++ii+oo' +  $("#color-specturm").is(":hidden") );

                var arg={};
                var c = _this.bizCfg.get('link');
                arg.color = c.attr.attr['stroke'];
                arg.showAlpha = false;

                arg.call = function(e){
                    arg.color = e.toHslString();
                    //console.log(e.toHslString());
                    //console.log("Alpha：" + e.getAlpha() + ",color：" + e.toHexString());
                    //$(".line-color").css({background:e.toHexString(),opticy: e.getAlpha()});
                    //$(".line-color").attr("value",e.toHslString());
                    var objs = _this.bizCfg.selected.links;
                    for(o in objs){
                        var obj = objs[o];
                        var data = obj.getValue();
                        data.attr.attr['stroke'] = e.toHexString();
                        obj.setValue(data); 
                    }
                    var p = _this.bizCfg.get('link');
                    p.attr.attr['stroke'] = e.toHexString();
                    _this.bizCfg.set('link',p);

                }
                stopBubble(e);
                getSpectrum(arg);
            });

            $(".bg-fill-color").bind('click',function(e){
                if(!_this.editAble){
                    return;
                }
                var x = e.clientX-pbs.left+10,y=e.clientY-pbs.top+10;
                $("#color-specturm").css({top: y +'px',left: x+'px'});
                if($("#color-specturm").is(":hidden"))
                    $("#color-specturm").show();
                var arg={};
                var c = _this.bizCfg.get('node');
                arg.color = c.attr.bgStyle['fill'];
                arg.call = function(e){
                    for(var o in _this.bizCfg.selected.nodes){
                        ctx = _this.bizCfg.shapeControls[o].ctx;
                        if(!ctx)
                            continue;
                        if(!ctx.data.attr.bgStyle){
                            ctx.data.attr.bgStyle = {};
                        }
                        ctx.data.attr.bgStyle['fill'] = e.toHexString();
                        ctx.data.attr.bgStyle['fill-opacity'] = e.getAlpha();
                        ctx.refresh();
                    }

                    var p = _this.bizCfg.get('node');
                    p.attr.bgStyle['fill'] = e.toHexString();
                    p.attr.bgStyle['fill-opacity'] = e.getAlpha();
                    _this.bizCfg.set('node',p);
                }
                stopBubble(e);
                getSpectrum(arg);
            });

            $(".bg-border").bind('click',function(e){
                if(!_this.editAble){
                    return;
                }
                var x = e.clientX-pbs.left+10,y=e.clientY-pbs.top+10;
                $("#color-specturm").css({top: y +'px',left: x+'px'});
                if($("#color-specturm").is(":hidden"))
                    $("#color-specturm").show();
                var arg={};
                var c = _this.bizCfg.get('node');
                arg.color = c.attr.bgStyle['stroke'];
                arg.call = function(e){
                    for(var o in _this.bizCfg.selected.nodes){
                        ctx = _this.bizCfg.shapeControls[o].ctx;
                        if(!ctx)
                            continue;
                        if(!ctx.data.attr.bgStyle){
                            ctx.data.attr.bgStyle = {};
                        }
                        ctx.data.attr.bgStyle['stroke'] = e.toHexString();
                        ctx.data.attr.bgStyle['stroke-opacity'] = e.getAlpha();
                        ctx.refresh();
                    }

                    var p = _this.bizCfg.get('node');
                    p.attr.bgStyle['stroke'] = e.toHexString();
                    _this.bizCfg.set('node',p);
                }
                stopBubble(e);
                getSpectrum(arg);
            });
        
            $(".bar_div.text-box .line-type").bind('click',function(e){//绘制文本框
                if(!_this.editAble){
                    return;
                }
                if(!$(this).hasClass('add-text-box')){
                    $(this).addClass('add-text-box');
                    _this.drawTextBox = true;
                }else{
                    $(this).removeClass('actice_tool add-text-box');
                    _this.drawTextBox = false;
                }
            });

            //字体颜色
            $(".font-color").bind('click',function(e){
                if(!_this.editAble){
                    return;
                }
                var x = e.clientX-pbs.left+10,y=e.clientY-pbs.top+10;
                $("#color-specturm").css({top: y +'px',left: x+'px'});
                if($("#color-specturm").is(":hidden"))
                    $("#color-specturm").show();
                var arg={};
                var c = _this.bizCfg.get('node');
                arg.color = c.attr.fontStyle['fill'];
                arg.call = function(e){
                   
                    for(var o in _this.bizCfg.selected.nodes){
                        ctx = _this.bizCfg.shapeControls[o].ctx;
                        if(!ctx)
                            continue;
                        if(!ctx.data.attr.fontStyle){
                            ctx.data.attr.fontStyle = {};
                        }
                        ctx.data.attr.fontStyle['fill'] = e.toHexString();
                        ctx.refresh();
                    }
                    var p = _this.bizCfg.get('node');
                    p.attr.fontStyle['fill'] = e.toHexString();
                    _this.bizCfg.set('node',p);
                }
                stopBubble(e);
                getSpectrum(arg);
            });        
            $(".bar_div.font-size .font-show-select").bind('click',function(e){
                if(!_this.editAble){
                    return;
                }
                var val = $(".font-input-value").val();
                $(".font-select-div>div").each(function(){
                    var v = $(this).text();
                    if(v == val){
                        $(this).addClass("font-selected");
                    }else{
                        $(this).removeClass("font-selected");
                    }
                });
                if($(".font-select-div").is(":hidden"))
                    $(".font-select-div").show();
                else
                    $(".font-select-div").hide();
            });
            $(".font-select-div>div").bind('click',function(){
                if(!_this.editAble){
                    return;
                }
                var v = $(this).text();
                $(".font-input-value").val(v);
                changeFont(v);
                $('.font-selected').removeClass("font-selected");
                $(this).addClass("font-selected");
                $(".font-select-div").hide();
            });
            $(".font-input-value").bind('change',function(){
                var val = $(this).val();
                $(".font-select-div").hide();
               changeFont(val);
            });
            function changeFont(val){ 

                for(var o in _this.bizCfg.selected.nodes){
                    ctx = _this.bizCfg.shapeControls[o].ctx;
                    if(!ctx)
                        continue;
                    if(!ctx.data.attr.fontStyle){
                        ctx.data.attr.fontStyle = {};
                    }
                    ctx.data.attr.fontStyle['font-size']=val;
                    ctx.refresh();
                }

                var p = _this.bizCfg.get('node');
                p.attr.fontStyle['font-size']=val;
                _this.bizCfg.set('node',p);
            }

            //字体粗细
            $(".bar_div.font-weight .line-type").bind('click',function(e){
                if(!_this.editAble){
                    return;
                }
                var fw ='normal';
                if(!$(this).hasClass("font-weight-bold")){
                    fw = 'bold';
                    $(this).addClass("actice_tool font-weight-bold");
                }else{
                    fw ='normal';
                    $(this).removeClass("actice_tool font-weight-bold");
                }

                for(var o in _this.bizCfg.selected.nodes){
                    ctx = _this.bizCfg.shapeControls[o].ctx;
                    if(!ctx)
                        continue;
                    if(!ctx.data.attr.fontStyle){
                        ctx.data.attr.fontStyle = {};
                    }
                    ctx.data.attr.fontStyle['font-weight']=fw;
                    ctx.refresh();
                }

                var p = _this.bizCfg.get('node');
                p.attr.fontStyle['font-weight']=fw;
                _this.bizCfg.set('node',p);

            });
            
            $(".refresh").bind('click',function(e){
                _this.topoSVG.refresh();
            });

            $("#saveBtn").on("click",function(){
                if(!_this.editAble){
                    return;
                }
                _this.topoSVG.saveData();
            });

            $(".modify-attr .delete").on("click",function(){
                if(!_this.editAble){
                    return;
                }
                _this.topoSVG.confirm("确认删除？",function(){ 
                    var sets = _this.bizCfg.selected;
                    var links= sets.links,nodes = {};
                    for(var o in sets.nodes){
                        var ctx = sets.nodes[o].ctx;
                        if(ctx.links != undefined && ctx.links.length > 0){
                           for(var i=0; i<ctx.links.length; i++ ){
                                links[ctx.links[i].data.id] = ctx.links[i];
                            }
                        }
                        nodes[ctx.data.id] = ctx;
                    }
                    _this.topoSVG.businessView.removeLinks(links);
                    _this.topoSVG.businessView.removeNodes(nodes);
                });

            });

           $(".other-attr .health-btn").on("click",function(){
                _this.topoSVG.showHealth();
            });

           $(".align-left").on("click",function(){
                if(!_this.editAble){
                    return;
                }
                align('left');
           });

           $(".align-bottom").on("click",function(){
                if(!_this.editAble){
                    return;
                }
                align('bottom');
           });

           function align(type){
                var nds = _this.bizCfg.selected.nodes;
                var room= _this.topoSVG.drawer.rbox();
                var rct ={
                    minX:room.w,
                    minY:room.h,
                    maxX:0,
                    maxY:0
                }
                for(var o in nds){
                    var nd = nds[o];
                    var attr = nd.ctx.data.attr;
                    var x=attr.x,y=attr.y;

                    rct.minX=x<rct.minX?x:rct.minX;
                    rct.minY=y<rct.minY?y:rct.minY;
                    rct.maxX=x>rct.maxX?x:rct.maxX;
                    rct.maxY=y>rct.maxY?y:rct.maxY;

                }

                for(var o in nds){
                    var nd = nds[o];
                    var attr = nd.ctx.data.attr;
                    if(type == 'left')
                        attr.x = rct.minX;
                    if(type == 'bottom')
                        attr.y = rct.maxY;
                    nd.ctx.refresh();
                    nds[o].refresh();
                }
            }
        },
    	set_nodes:function(nodes){
    		this.nodes=nodes;
    	},
    	get_nodes:function(nodes){
    		return this.nodes;
    	}
    });

    function getSpectrum(args){
        var color = args.color;
        var showAlpha = true;
        if(args.showAlpha != undefined){
            showAlpha = args.showAlpha;
        }
        if(!color){
            color ="#fff";
        }
        var obj = $("#flat").spectrum({
            color: color,
            flat: true,
            showInput: true,
            className: "full-spectrum",
            showAlpha:showAlpha,
            preferredFormat: "hex",
            showPalette: true,
            showPaletteOnly: true,
            //clickoutFiresChange: true,
            showInitial: true,
            togglePaletteOnly:true,
            showSelectionPalette: true,
            maxPaletteSize: 10,
            cancelText: "取消",
            chooseText: "确认",
            togglePaletteMoreText: "更多",
            togglePaletteLessText: "关闭",
            change:function(e){

                if(args && args.call){
                    args.call(e);
                }
                $(".color").hide();
                obj.spectrum('destroy');
                $(document).unbind('.spectrum');
            },
            palette: [
                ["rgb(0, 0, 0)", "rgb(67, 67, 67)", "rgb(102, 102, 102)", "rgb(153, 153, 153)","rgb(183, 183, 183)",
                "rgb(204, 204, 204)", "rgb(217, 217, 217)", "rgb(239, 239, 239)", "rgb(243, 243, 243)", "rgb(255, 255, 255)"],
                ["rgb(152, 0, 0)", "rgb(255, 0, 0)", "rgb(255, 153, 0)", "rgb(255, 255, 0)", "rgb(0, 255, 0)",
                "rgb(0, 255, 255)", "rgb(74, 134, 232)", "rgb(0, 0, 255)", "rgb(153, 0, 255)", "rgb(255, 0, 255)"],
                ["rgb(230, 184, 175)", "rgb(244, 204, 204)", "rgb(252, 229, 205)", "rgb(255, 242, 204)", "rgb(217, 234, 211)",
                "rgb(208, 224, 227)", "rgb(201, 218, 248)", "rgb(207, 226, 243)", "rgb(217, 210, 233)", "rgb(234, 209, 220)",
                "rgb(221, 126, 107)", "rgb(234, 153, 153)", "rgb(249, 203, 156)", "rgb(255, 229, 153)", "rgb(182, 215, 168)",
                "rgb(162, 196, 201)", "rgb(164, 194, 244)", "rgb(159, 197, 232)", "rgb(180, 167, 214)", "rgb(213, 166, 189)",
                "rgb(204, 65, 37)", "rgb(224, 102, 102)", "rgb(246, 178, 107)", "rgb(255, 217, 102)", "rgb(147, 196, 125)",
                "rgb(118, 165, 175)", "rgb(109, 158, 235)", "rgb(111, 168, 220)", "rgb(142, 124, 195)", "rgb(194, 123, 160)",
                "rgb(166, 28, 0)", "rgb(204, 0, 0)", "rgb(230, 145, 56)", "rgb(241, 194, 50)", "rgb(106, 168, 79)",
                "rgb(69, 129, 142)", "rgb(60, 120, 216)", "rgb(61, 133, 198)", "rgb(103, 78, 167)", "rgb(166, 77, 121)",
                "rgb(133, 32, 12)", "rgb(153, 0, 0)", "rgb(180, 95, 6)", "rgb(191, 144, 0)", "rgb(56, 118, 29)",
                "rgb(19, 79, 92)", "rgb(17, 85, 204)", "rgb(11, 83, 148)", "rgb(53, 28, 117)", "rgb(116, 27, 71)",
                "rgb(91, 15, 0)", "rgb(102, 0, 0)", "rgb(120, 63, 4)", "rgb(127, 96, 0)", "rgb(39, 78, 19)",
                "rgb(12, 52, 61)", "rgb(28, 69, 135)", "rgb(7, 55, 99)", "rgb(32, 18, 77)", "rgb(76, 17, 48)"]
            ]//*/
        });

        $(".full-spectrum .sp-cancel").bind('click.spectrum',function(e){
            $(".color").hide();
            obj.spectrum('destroy');
            $(document).unbind('.spectrum');
            stopEvent(e)
            stopBubble(e);
        });
        $(".full-spectrum .sp-choose").bind('click.spectrum',function(e){
            $(".color").hide();
            obj.spectrum('destroy');
            $(document).unbind('.spectrum');
            stopBubble(e);
        });
        
        $(document).bind('click.spectrum',function(e){
            if($(".color").is("hidden"))
                return;
            $(".color").hide();
            obj.spectrum('destroy');
            $(document).unbind('.spectrum');
        });
        
    }

    function stopEvent(evt) {
        var evt = evt || window.event; 
        if (evt.preventDefault) { 
            evt.preventDefault(); 
        } else { 
            evt.returnValue = false; 
        } 
    }
    return f;
});