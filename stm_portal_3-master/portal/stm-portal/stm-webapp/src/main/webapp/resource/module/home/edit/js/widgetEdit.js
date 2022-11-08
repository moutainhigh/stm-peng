$(function(){

    function genDialog(args) {
        this.args = $.extend({
            widgetManager:undefined,
            widgetId: undefined,//widgetId 对应的模块标识
            cfg:undefined,//widgetId 对应模块配置信息,
            widgetLoader:undefined
        },args);

        //当前是对某个widge的配置信息进行修改
        this.updateMode = (this.args.widgetId != undefined);

        this.dialog = undefined; //配置页面
        this.customColseDialog = false;//是否自行控制dialog的关闭

        this.confirmFunction = undefined;//回调函数
        this.cancelFunction = undefined;//回调函数


        this.widgetManager = this.args.widgetManager;

        if(this.updateMode && !this.args.cfg)
            throw '模块配置信息不能为空！';

        if(!this.widgetManager)
            throw 'widgetManager can not be null!';
        
        //console.log(this.args);

        this.init();
    }

    gdp = genDialog.prototype;
    
    gdp.init = function(){
        var _this =this;
        _this.dialog =$("<div id='edit-widge-dialog'/>").dialog({
            title:'视图和图表',
            width:956,
            height:610,
            href:oc.resource.getUrl('resource/module/home/edit/widgetEdit.html'),
            onLoad:function(){
                _this._initDialogPage();
            },
            buttons:[
            {
                text:'确定',
                iconCls:'fa fa-check-circle',
                handler:function(){
                    //由各个配置项自行实现各自的确认事件
                    var fn = _this.confirmFunction;
                    
                    var result = true;
                    fn && (result = fn());
                    if(result != null && result == false){
                    	return;
                    }
                    if(!_this.customColseDialog)
                    	_this.closeDialog();
                }
            },{
                text:'取消',
                iconCls:'fa fa-times-circle',
                handler:function(){
                    //由各个配置项自行实现各自的确认事件
                    var fn = _this.cancelFunction;
                    fn && fn();

                    if(!_this.customColseDialog){
                        _this.closeDialog();
                    }
                }
            }]
        });

    }

    /**
    *注册确定事件回调函数
    */
    gdp.regConfirmFunction = function(fn){
        this.confirmFunction = fn;
    }

    /**
    *注册cancel事件回调函数
    */
    gdp.regCancelFunction = function(fn){
        this.cancelFunction = fn;
    }

    /*
    * 注册是否用户自行控制弹出层的关闭
    */
    gdp.setCustomColseDialog = function(cu){
        this.customColseDialog = cu?true:false;
    }

    /**
    *关闭配置页面
    */
    gdp.closeDialog = function(fn){
        fn && fn();
        try{
            if(this.dialog)
                this.dialog.dialog('close');
        }catch(e){}
    }


    gdp._initDialogPage = function(){
        var _this = this;
        /*if(this.updateMode){//模块配置信息修改模式
            _this._genUpdateWidgetConfPage();
        }else{}
        //*/

        var widgetId = this.args.widgetId ? this.args.widgetId :'statisticView';
        _this._genWidgetConfPage(widgetId);

        _this.regEvent();
    }

    //根据widgetId生成Widget的配置页面
    gdp._genWidgetConfPage = function(widgetId){
        var _this = this;

        $(".widget-edit-panel .left").find('li>div').removeClass("active");
        $(".widget-edit-panel .left").find('li[data-role = "' + widgetId + '"]>div').addClass("active");
       
        var $obj = $(".widget-edit-panel .right");
        oc.resource.loadScript(['resource/module/home/edit/js/' + widgetId +'Config.js'], function(){
            var fn = oc.index.home.widgetedit[widgetId + 'Config'];
            if(!fn){
                console.log('oc.index.home.widgetedit.' + widgetId + 'Config' + '该函数未实现！');
                return;
            }
            new fn({
                showConfigDialog:_this,
                panelArea: $obj,
                args:_this.args,
                widgetLoader:_this.args.widgetLoader
            });
        });
    }

    gdp.regEvent = function(){
        var _this = this;

        $(".widget-edit-panel .list-menu li").bind('click',function(){
            var $obj = $(".widget-edit-panel .right");
            var dt = $(this).attr('data-role');    
            if($obj.data('role') == dt ){
                return;
            }

            _this.cancelFunction = undefined;
            _this.confirmFunction = undefined;
            _this.customColseDialog = false;
            
            //加载右侧页面
            $obj.data('role',dt);
            $obj.html('');
            _this._genWidgetConfPage(dt);
        });
    }

    /*
    * 保存配置页面表单数据，
    * moduleCode 模块标识
    *　cfg 模块配置信息
    */
    gdp.save = function(moduleCode,cfg) {
        var _this = this;

        var layout =  _this.widgetManager.data.homeLayoutBo;
        var props = cfg.props;

        //console.log({layout:layout,cfg:cfg});
        
        var id = -1; 
        if(cfg){
            id = cfg.id;
        }

        var type = id >-1?'modify':'add';

        var url = oc.resource.getUrl('system/home/layout/addLayoutModuleConfig.htm');//新增
        if(type == 'modify'){//表示对现有数据进行修改
            url = oc.resource.getUrl('system/home/layout/updateLayoutModuleConfig.htm');//修改
        }
        oc.util.ajax({
            url: url,
            data: {
                id: id,
                moduleId: -1,
                moduleCode: moduleCode,
                layoutId: layout.id,
                props: JSON.stringify(props)
            },
            success: function(data) {
                if(data.code == '200'){
                    var widgetMger = _this.widgetManager;
                    if(type == 'modify'){   
                        widgetMger.reloadWidget(id);
                    }else {
                        var data = data.data.homeLayoutModuleConfig;
                        //插入成功后增加模块到页面上
                        
                        widgetMger.addWidget({id:data.id},data);
                    }
                }
            }
        });
    }
    
    
    oc.ns('oc.index.home.widgetedit');
    oc.index.home.widgetedit.showConfigDialog = function(args){
        return new genDialog(args);
    }

})