buzdefine("bz/NodeControl", function () {
    var f = function(args){
        this.args=$.extend({
            topoSVG:null,
            ctx:null
        },args);

        if(!this.args.ctx || !args.topoSVG){
            throw "drawer can't be null";
        }

        this.topoSVG = args.topoSVG;

        this.init();
    };
    f.prototype = {
        init:function(){
            
            var _this = this;
           
            buzrequire(["bz/ShapeControl"],function(ShapeControl){
                var ctx =_this.args.ctx;
                var id = 'shape_controls_'+ctx.data.id;
                _this.ShapeControl = new ShapeControl({
                    topoSVG:_this.topoSVG,
                    ctx:ctx,
                    drawer:ctx.args.drawer,
                    id:id,
                    selectType:'single'
                }); 
            });

            _this.regEvent();
        },
    	set_ctx:function(ctx){
    		this.ctx=ctx;
    	},
    	get_ctx:function(ctx){
    		return this.ctx;
    	},
        attr:function(field,value){
            if(field){
                if(field instanceof Object){
                    for(var k in field){
                        this._attr(k,field[k]);
                    }
                }else{
                     return this._attr(field,value);
                }
            }
        },
        _attr:function(field,value){
            if(value==undefined && this["get_"+field]){
                return this["get_"+field].call(this);
            }
            if(value!=undefined  && this["set_"+field]){
                this["set_"+field].call(this,value);
            }
            return undefined;
        },
        regEvent:function(){
            var _this = this;
            var ctx = this.args.ctx;

            ctx.node.txt.on('mousedown',function(e){
                //$("#text_modify").unbind();
                //$("#text_modify").remove();//移除可能的文本框
                stopBubble(e);
                stopEvent(e);
            });
            ctx.node.txt.dblclick(function(){
                                
                return;//由于和其它地方有冲突，暂时注释掉此部分功能

                $("#text_modify").unbind();
                $("#text_modify").remove();//移除可能的文本框

                var _text = ctx.node.txt.text();
                var pos = this.node.getClientRects()[0];
                //console.log(pos);
                var html = '<textarea id ="text_modify" type="text" name="" placeholder=' + _text +'></textarea>';
                $("body").append(html);
                $("#text_modify").focus();
                $("#text_modify").css({'display': 'block',
                            'position': 'absolute',
                            'z-index': 100000000,
                            'top': pos.top + 'px',
                            'left': pos.left + 'px',
                            'width': pos.width + 'px',
                            'height': pos.height*2 + 'px'});
                $("#text_modify").bind("blur",function(){
                    var t = $(this).val();
                    if( t != undefined && t != '')
                    $(ctx.node.txt.node).text(t);
                    //console.log($(this).val());
                    $("#text_modify").remove();
                })
            });
            ctx.node.txt.on('mouseover',function(e){
                var _pos = _this.getBoundingClientRect(this.node);
                _this.ShapeControl.showShapeContour(e);
            });


            ctx.node.gdawer.on('mousedown',function(e1){
                var _img = this.node;
                //_img = ctx.node.node;
				var _pos = _this.getBoundingClientRect(_img);
                stopEvent(e1);
                stopBubble(e1);
                
                _this.ShapeControl.showShapeControls(e1);
                _this.ShapeControl.showShapeContour(e1);
                _this.ShapeControl.dragable(e1);

                if(e1.which == 3){
                    
                    document.oncontextmenu = function()
                    {   
                        _this.topoSVG.businessView.showRightMenu({mouse:e1,ctx:ctx,type:'node'});
                        document.oncontextmenu=undefined;
                        return false;
                    }
                    return;
                }
            });
            
            ctx.node.gdawer.on('mouseover',function(e){

				var _pos = _this.getBoundingClientRect(this.node);
				if(_this.ShapeControl)
                	_this.ShapeControl.showShapeContour(e);
               
                _this.topoSVG.showToolTip(ctx,e);
            });

            ctx.node.gdawer.on('mouseout',function(e){
                var dr = _this.args.drawer;//移除当前关联连线控制link结束点
                if(dr.anchor != undefined){
                    delete _this.args.drawer.anchor.pose_mouseover;
                    delete _this.args.drawer.anchor.mouseover_node;
                }

                _this.topoSVG.closeTooltip(ctx,e);

            });

            ctx.node.on('mouseover',function(e){
                $(this.node).css("cursor","pointer");
                var dr = _this.args.drawer;//移除当前关联连线控制link结束点
                if(!dr.anchor){
                    dr.anchor = {};
                }

                dr.anchor.mouseover_node = ctx;
            });
            ctx.node.fg.on('mouseout',function(e){
                $(this.node).css("cursor","");
                var dr = _this.args.drawer;//移除当前关联连线控制link结束点
                if(dr.anchor != undefined){
                    delete _this.args.drawer.anchor.pose_mouseover;
                    delete _this.args.drawer.anchor.mouseover_node;
                }
                $(".shape_contour.shape_contour_hovered").hide();
            });
        },
        reSize:function(info,ctx){//从新绘制形状
            ctx.setValue(info);
        },
        refresh:function(attr){
        },
        getBoundingClientRect :function(obj){
         var _pos = obj.getBoundingClientRect();
         var bs = this.topoSVG.bizCfg.getBaseOffset();
         var pos = {};
         pos.top = _pos.top - bs.top;
         pos.left = _pos.left - bs.left;
         pos.height = _pos.height;
         pos.width = _pos.width;
         
        return pos;
    }

    };

	/*function getBoundingClientRect(obj){
		 var _pos = obj.getBoundingClientRect();
		 var bs = window.topoSVG.bizCfg.getBaseOffset();
		 var pos = {};
		 pos.top = _pos.top - bs.top;
		 pos.left = _pos.left - bs.left;
		 pos.height = _pos.height;
		 pos.width = _pos.width;
		return pos;
	}//*/
	
    function stopEvent (evt) {
        var evt = evt || window.event; 
        if (evt.preventDefault) { 
            evt.preventDefault(); 
        } else { 
            evt.returnValue = false; 
        } 
    }

    return f;
});