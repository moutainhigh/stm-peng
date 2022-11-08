buzdefine("bz/TextNodeControl", function () {
    var f = function(args){
        this.args=$.extend({
            ctx:null
        },args);
       
        if(!this.args.ctx || !args.topoSVG){
            throw "ctx can't be null";
        }

        this.topoSVG = args.topoSVG;
        this.init();
    };
    f.prototype = {
        init:function(){
            var _this = this;
            this.regEvent();
            var _this = this;
            var ctx = this.args.ctx;
            buzrequire(["bz/ShapeControl"],function(ShapeControl){
                ctx.ShapeControl = new ShapeControl({
                    topoSVG:_this.topoSVG,
                    ctx:ctx,
                    controller:_this,
                    drawer:ctx.args.drawer,
                    id:'shape_controls_'+ctx.data.id,
                    selectType:'single'
                    });
                _this.ShapeControl = ctx.ShapeControl;
            });
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
        showTextEditArea:function(){
            var ctx = this.args.ctx;
            var _this = this;

            //console.log(ctx.node.txt.text());
            $("#text_modify").unbind();
            $("#text_modify").remove();//移除可能的文本框

            var _text = ctx.node.text.text();
            var pos = ctx.node.node.getBoundingClientRect();
            //console.log(pos);
            var html = '<textarea id ="text_modify" type="text" name="" placeholder="' + _text +'"></textarea>';
            $("body").append(html);
            $("#text_modify").focus();
            $("#text_modify").css({'display': 'block',
                        'position': 'absolute',
                        'background':'white',
                        'z-index': 100000000,
                        'top': pos.top + 'px',
                        'left': pos.left + 'px',
                        'width': ctx.data.attr.w + 'px',
                        'height': ctx.data.attr.h + 'px',
                        'min-width': ctx.data.attr.w + 'px',
                        'min-height': ctx.data.attr.h + 'px'});
            $("#text_modify").bind("blur",function(){
                var t = $(this).val();
                if( t != undefined && t != ''){
                    var info = ctx.getValue();
                    info.attr.text = t;
                    ctx.setValue(info);
                }
                $("#text_modify").remove();
            });
        },
        regEvent:function(){
            var ctx = this.args.ctx;
            var _this = this;
            
            ctx.node.text.dblclick(function(e){
                _this.showTextEditArea();
            });

            ctx.node.on('mousedown',function(e){
                stopEvent(e);
                stopBubble(e);

                ctx.ShapeControl.showShapeControls(e);
                ctx.ShapeControl.setShowContour(false);
                ctx.ShapeControl.dragable(e);

                if(e.which == 3){
                    
                    document.oncontextmenu = function()
                    {   
                        _this.topoSVG.businessView.showRightMenu({mouse:e,ctx:ctx,type:'node'});
                        document.oncontextmenu=undefined;
                        return false;
                    }
                    return;
                }
            });

        }
    };
    return f;
});