var oldDataTopo = null;
function topoView(selector, opt) {
    this.opt = {
        type: 'topo'
    };
    this.opt.selector = selector;

    this.opt = $.extend(this.opt, opt, true);
    if (this.opt.selector)
        this.init();
    else
        throw('初始化失败!');
}
var tvp = topoView.prototype;

tvp.init = function () {
    this.dataObj = $("<div/>");
    this.dataObj.css({width: '100%', height: '100%'});
    this.opt.selector.html(this.dataObj);

    this.load();
}

tvp.refresh = function () {
    /*首页topo刷新优化 huangping 2017/7/11 start*/
    /*delete this.dataObj[0];
     this.init();*/
    if (this.opt.type == "topo") {
        var id = this.opt.topoId;
        var ctx = this;
        $.post(oc.resource.getUrl("topo/getSubTopo/" + id + ".htm"), function (data) {
            data.id = id;
            // 加载拓扑属性
            $.post(oc.resource.getUrl("topo/subtopo/getAttr.htm"), {
                id: data.id
            }, function (info) {
                data.pageAttr = info;
                var d = JSON.stringify(data);
                if (oldDataTopo != d) {
                    oldDataTopo = d;
                    delete ctx.dataObj[0];
                    ctx.init();
                }
            }, "json");
        }, function () {
            delete ctx.dataObj[0];
            ctx.init();
        }, "json");
    } else {
        delete this.dataObj[0];
        this.init();
    }
    /*首页topo刷新优化 huangping 2017/7/11 end*/
}

tvp.load = function () {
    var _this = this;

    var id = this.opt.topoId;
    this.opt.id = (id == undefined ? 10 : id);

    var type = this.opt.type;
    this.opt.type = (type == undefined ) ? 'topo' : type;

    _this.dataObj.empty();

    var param = $.extend({
        selector: _this.dataObj,
        showText: true,
        data: 0,
        isMax: true,
        id: id
    }, _this.opt);

    param.selector = _this.dataObj;
    param.showBgIm = true;
    oc.resource.loadScripts(['resource/module/home/widgets/js/topoViewHelper.js'], function () {
        var finshied = false;
        var iner = setInterval(function () {
            try {
                if (_this.svg != null && _this.svg.map != null) {
                    delete _this.svg.map.dataObj[0];
                }
                var svg = oc.ui.toposvg(param);
                finshied = true;
                _this.svg = svg;
            } catch (e) {
                //console.log(e);
            }
            if (finshied)
                clearInterval(iner);
        }, 1000);

    });

}