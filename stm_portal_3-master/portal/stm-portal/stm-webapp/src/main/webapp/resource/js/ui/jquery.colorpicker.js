/**
 * jQuery插件：颜色拾取器
 * 
 * @author  Karson
 * @url     http://blog.iplaybus.com
 * @name    jquery.colorpicker.js
 * @since   2012-6-4 15:58:41
 */
(function($) {
    var ColorHex=new Array('00','33','66','99','CC','FF');
    var SpColorHex=new Array('FF0000','00FF00','0000FF','FFFF00','00FFFF','FF00FF');
    $.fn.colorpicker = function(options) {
        var opts = jQuery.extend({}, jQuery.fn.colorpicker.defaults, options);
        initColor();
        return this.each(function(){
            var obj = $(this);
            var mark=0;
                obj.bind(opts.event,function(){
                    //定位
                    var ttop  = $(this).offset().top;     //控件的定位点高
                    var thei  = $(this).height();  //控件本身的高
                    var tleft = $(this).offset().left;    //控件的定位点宽
                    var $bt= $("#businessToolbar").position().top;
                    $("#colorpanel").slideToggle();
               if(mark%2==0){
                   mark++;
               }else{
                   mark--;
               }
               if($bt>280){
                   $("#colorpanel").css({
                       top:ttop-183,
                       left:tleft
                   });
               }else{
                   $("#colorpanel").css({
                       top:ttop+thei+5,
                       left:tleft
                   });
               }
               var target = opts.target ? $(opts.target) : obj;
               if(target.data("color") == null){
                   target.data("color",target.css("color"));
               }
               if(target.data("value") == null){
                   target.data("value",target.val());
               }
               $("#CT tr td").unbind("click").mouseover(function(){
                   var color=$(this).css("background-color");
                   $(".DisColor").css("background",color);
                   $(".tdboxshow").css({boxShadow:'-1px 14px 15px '+color+' inset'});
                   $("#HexColor").val($(this).attr("rel"));
                 //  $(".tdboxshow").css("boxshadow","1px"+"10px"+"15px"+color+"inset");
               }).click(function(){
                       var color=$(this).attr("rel");
                       color = opts.ishex ? color : getRGBColor(color);
                       target.val(color);
                       if(opts.fillcolor){
                           target.css("color",color);
                       }
                       $("#colorpanel").hide();
                       opts.success(obj,color);
                   });
                });


        });
    
        function initColor(){
            $("body").append('<div id="colorpanel" style="position: absolute; display: none;"></div>');
            var colorTable = '';
            var colorValue = '';
            for(i=0;i<2;i++){
                for(j=0;j<6;j++){
                    colorTable=colorTable+'<tr height=12>'
                    colorTable=colorTable+'<td width=11 rel="#000000" style="background-color:#000000">'
                    colorValue = i==0 ? ColorHex[j]+ColorHex[j]+ColorHex[j] : SpColorHex[j];
                    colorTable=colorTable+'<td width=11 rel="#'+colorValue+'" style="background-color:#'+colorValue+'">'
                    colorTable=colorTable+'<td width=11 rel="#000000" style="background-color:#000000">'
                    for (k=0;k<3;k++){
                        for (l=0;l<6;l++){
                            colorValue = ColorHex[k+i*3]+ColorHex[l]+ColorHex[j];
                            colorTable=colorTable+'<td width=11 rel="#'+colorValue+'" style="background-color:#'+colorValue+'">'
                        }
                    }
                }
            }

            colorTable='<table width=231 border="0" cellspacing="0" cellpadding="0" style="border:1px solid #000;">'
            +'<tr height=30 ><td  colspan=21  class="tdboxshow">'
            +'<table cellpadding="0" cellspacing="1" border="0" style="border-collapse: collapse">'
            +'<tr><td width="3"><td><input type="text" class="DisColor" size="6" disabled style="border:solid 1px #000000;background-color:#ffff00;width:70px;"></td>'
            +'<td width="3"><td><input type="text" id="HexColor" size="7" style="border:inset 1px;font-family:Arial;width:80px;" value="#000000">' +
                '<span id="_cclose" style="font-family: 微软雅黑;color: #fff;">关闭</span></td></tr></table></td></table>'
            +'<table id="CT" border="1" cellspacing="0" cellpadding="0" style="border-collapse: collapse" bordercolor="000000"  style="cursor:pointer;">'
            +colorTable+'</table>';
            $("#colorpanel").html(colorTable);
            $(document).on('click','#_cclose',function(){
                $("#colorpanel").hide();
                return false;
            });
        }
        
        function getRGBColor(color) {
            var result;
            if ( color && color.constructor == Array && color.length == 3 )
                color = color;
            if (result = /rgb\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*\)/.exec(color))
                color = [parseInt(result[1]), parseInt(result[2]), parseInt(result[3])];
            if (result = /rgb\(\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*\)/.exec(color))
                color =[parseFloat(result[1])*2.55, parseFloat(result[2])*2.55, parseFloat(result[3])*2.55];
            if (result = /#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/.exec(color))
                color =[parseInt(result[1],16), parseInt(result[2],16), parseInt(result[3],16)];
            if (result = /#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])/.exec(color))
                color =[parseInt(result[1]+result[1],16), parseInt(result[2]+result[2],16), parseInt(result[3]+result[3],16)];
            return "rgb("+color[0]+","+color[1]+","+color[2]+")";
        }
    };
    jQuery.fn.colorpicker.defaults = {
        ishex : true, //是否使用16进制颜色值
        fillcolor:false,  //是否将颜色值填充至对象的val中
        target: null, //目标对象
        event: 'click', //颜色框显示的事件
        success:function(){}, //回调函数
        reset:function(){}
    };
})(jQuery);