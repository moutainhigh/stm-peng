function Topo(args) {
    args = $.extend({
        editable: true,
        id: null
    }, args);
    var ctx = this;
    this.holder = args.holder;
    this.$holder = $(this.holder);
    this.mode = args.mode || "normal";
    this.$holder.css({
        "overflow": "hidden"
    });
    this.width = parseInt(this.$holder.css("width"));
    this.height = parseInt(this.$holder.css("height"));
    this.paper = Raphael(args.holder, this.width, this.height);
    this.drawer = SVG(ctx.$holder.find("svg").get(0));

    this.id = args.id;
    //console.log(this.id);
    this.viewBox = {
        x: 0,
        y: 0,
        w: this.width,
        h: this.height
    };
    this.paper.setViewBox(this.viewBox.x, this.viewBox.y, this.viewBox.w, this.viewBox.h);
    this.editable = !!args.editable;
    this.subtopoNodes = [];	//拓扑节点
    this.util = new TopoUtil({
        paper: this.paper,
        holder: this.holder
    });
    this.util.getSvg().attr("data-svg", "topo");
    // 是否加载完
    this.finishedFlag = false;
    this.setDirty(false);
    // 多选的元素
    this.chosed = [];
    // 要删除的节点，线，组
    this.deleteItems = {
        links: [],
        groups: [],
        nodes: [],
        others: []
    };
    this.cfg = {
        "topo": {
            "freshType": "auto",
            "showRes": ["net", "server"],
            "fontColorReserve": true,
            "fontColor": "rgb(255, 255, 255)",
            "fontsizeReserve": false,
            "pelSize": "1",
            "fontSize": 10,
            "refreshTime": 300000
        },
        "link": {"singleSettingTag": true, "hasTag": false, "tagField": "flow", "colorWarning": "device"},
        "device": {"tagField": "ip", "colorWarning": "device"},
        "effect": {"flow": false}
    };
    this.args = args;
    this.init();
};
// 是否被编辑过
Topo.prototype.setDirty = function (flag) {
    this._dirty = flag;
};
Topo.prototype.addOther = function (attr) {
    if (oc.topo.topoIsRunning) {
        alert("拓扑发现正在进行,暂时不能进行保存操作", "warning");
        return;
    }
    var ctx = this;
    oc.util.ajax({
        url: oc.resource.getUrl("topo/addOther.htm"),
        data: {
            attr: JSON.stringify(attr),
            subTopoId: ctx.id
        }, success: function (data) {
            if (data.state == 200) {
                var img = new Image({
                    paper: ctx.paper,
                    holder: ctx.holder,
                    attr: $.extend({
                        w: 200,
                        h: 200
                    }, JSON.parse(data.attr))
                });
                img.id = data.id;
                ctx.drawUtil.pushEl(img);
                img.d.visible = true;
                img.d.subTopoId = data.subTopoId;
                img.d.dataType = attr.type;
                if (attr.type == "map") {// 如果是地图
                    img.toBack();
                }
                alert(data.msg);
            } else {
                alert(data.msg, "warning");
            }
        }
    });
};
//添加节点
Topo.prototype.addNode = function (args) {
    var ctx = this;
    if (oc.topo.topoIsRunning) {
        alert("拓扑发现正在进行,暂时不能进行保存操作", "warning");
        return;
    }
    oc.util.ajax({
        url: oc.resource.getUrl("topo/addNode.htm"),
        data: {
            ip: args.manageIp,
            attr: JSON.stringify($.extend({
                name: args.name
            }, args.attr)),
            visible: true,
            type: args.type,
            subTopoId: this.id,
            x: args.x,
            y: args.y,
            iconWidth: args.w,
            iconHeight: args.h,
            icon: args.src
        },
        success: function (result) {
            if (result.state == 200) {
                var ncfg = result.node;
                ncfg.rawId = ncfg.id;
                ncfg.id = "node" + ncfg.id;
                ncfg.subTopoId = ctx.id;
                var node = ctx.getNode({
                    src: ncfg.icon,
                    x: ncfg.x,
                    y: ncfg.y,
                    w: ncfg.iconWidth,
                    h: ncfg.iconHeight,
                    iw: ncfg.iconWidth,
                    ih: ncfg.iconHeight,
                    type: "node",
                    data: ncfg
                });
                node.d.attr = result.node.attrJson;
                alert("添加成功", "info");
                node.icon.hide();
                if (args.onSuccess) {
                    args.onSuccess.call(ctx, node);
                }
            } else {
                alert(result.msg, "warning");
            }
        }
    });
};
Topo.prototype.init = function () {
    // 初始化界面容器
    this.els = {};
    // 初始化系统事件发布器
    if (this.mode == "normal") {
        var svgEvent = new SvgEvent({
            paper: this.paper,
            holder: this.holder
        });
        this.svgEvent = svgEvent;
        this.$svg = svgEvent.$svg;
        this.drawUtil = new DrawUtil({
            paper: this.paper,
            holder: this.holder
        });
        // 初始化连线
        this.link = new Link({
            paper: this.paper,
            holder: this.holder
        });
        //注册各种监听事件
        this.initBaseElement();
        this.menuEvent();
        //发布加载数据事件
        if (this.args.showBgIm != null) {
            this.loadData(oc.resource.getUrl("topo/getSubTopo/" + this.id + ".htm"));
        } else {
            if (this.id != undefined) {
                eve("topo.loadsubtopo", this, this.id);
            }
        }
    }
};
Topo.prototype.getIndex = function () {
    if (!this.index)
        this.index = 0;
    return --this.index;
};
Topo.prototype.loadCfg = function () {
    var ctx = this;
    if (this.ignoreLoadCfg) return;// 如果忽略加载配置
    $.post(oc.resource.getUrl("topo/setting/get/globalSetting.htm"), function (cfg) {
        ctx.setCfg(cfg);
    }, "json");
};
// 组容器
Topo.prototype.groupCon = function (args) {
    var ctx = this;
    var group = new TopoGroup({
        paper: this.paper,
        holder: this.holder,
        x: args.x,
        y: args.y,
        w: args.w,
        h: args.h,
        r: 4,
        name: args.name || ""
    });
    group.d = args.data;
    group.linktype = "group";
    group.getValue = function () {
        var children = [];
        var p = this.getPos();
        for (var i = 0; i < this.children.length; ++i) {
            children.push(this.children[i].d.rawId);
        }
        return {
            name: this.name.attr("text"),
            id: this.d.rawId,
            x: p.x,
            y: p.y,
            rx: 0,
            ry: 0,
            width: p.w,
            height: p.h,
            children: children,
            subTopoId: ctx.id,
            visible: this.d.visible
        };
    };
    group.setTitle = function (field) {
        if (this.d && this.d[field]) {
            this.name.attr("text", this.d[field]);
        }
    };
    group.update = function (info) {
        this.name.attr("text", info.name);
        // 清空容器里的绑定关系
        for (var i = 0; i < this.children.length; ++i) {
            var child = this.children[i];
            child.refresh = child.oldRefresh;
            child.oldRefresh = null;
            child.groupFlag = false;
        }
        this.children = [];
        // 添加子节点
        for (var i = 0; i < info.items.length; ++i) {
            var item = info.items[i];
            var el = ctx.getEl(item.id);
            if (el) {
                this.add(el, el.d.rx, el.d.ry);
            }
        }
        this.refresh(this.getPos());
    };
    group.oldRemove = group.remove;
    group.remove = function () {
        group.oldRemove();
        ctx.deleteItems.groups.push(ctx.getRealId(this.d.id));
        delete ctx.els[this.d.id];
    };
    // 边界安全检查
    group.rangeCheck = function (pos) {
        if (pos.w < 170 || pos.h < 140) {
            return false;
        } else {
            return true;
        }
    };
    // 标题按钮
    this.paper.set(group.titleBtn, group.titleBtnText).click(function () {
        eve("topo.group.title.btn", group, group);
    });
    group.d = args.data;
    group.bg.attr({
        fill: "white",
        stroke: "none",
        "fill-opacity": 0.4
    });
    ctx.putEl(group.d.id, group);
    return group;
};
Topo.prototype.getRoundRectPath = function (args) {
    var x = args.x, y = args.y, w = args.w, h = args.h, r = args.r, rtl = args.rtl
        || args.r, rtr = args.rtr || args.r, rbr = args.rbr || args.r, rbl = args.rbl
        || args.r;
    var npath = [];
    npath.push(["M", x + rtl, y], ["H", x + w - rtr], ["C", x + w - rtr,
            y, x + w, y, x + w, y + rtr], ["V", y + h - rbr], ["C", x + w,
            y + h - rbr, x + w, y + h, x + w - rbr, y + h], ["H", x + rbl],
        ["C", x + rbl, y + h, x, y + h, x, y + h - rbl],
        ["V", y + rtl], ["C", x, y + rtl, x, y, x + rtl, y], ["Z"]);
    return npath;
};
// 初始化,横切自己的逻辑
Topo.prototype.initBaseElement = function () {
    var ctx = this;
    // 系统右键
    eve.on("svg.contextmenu", function (p, e) {
        if (ctx.editable && $(this).attr("data-svg") == "topo" && ctx.id != 1) {
            eve("element.contextmenu", ctx, e, {
                isSvg: true,
                paper: ctx.paper
            });
        }
        e.stopPropagation();
        return false;
    });
    // sizer的大小变更
    eve.on("element.sizer.dragend." + ctx.drawUtil.sizer.rect.id, function () {
        $.post(oc.resource.getUrl("topo/other/updateAttr.htm"), {
            subTopoId: ctx.id,
            attr: JSON.stringify(this.node.getValue()),
            id: this.node.d.rawId || this.node.d.id
        }, function (result) {
            alert(result.msg, (result.state == 200 ? null : "warning"));
        }, "json");
    });
    // 监听设备管理
    eve.on("topo.group.title.btn", function (con) {
        var rightData = [], leftData = [], selected = {};
        // 容器中已存在的
        $.each(con.children, function (key, c) {
            if (c.d) {
                selected[c.d.id] = true;
                rightData.push(c.d);
            }
        });
        // 容器外的
        $.each(ctx.els, function (key, el) {
            if (el.d && el.d.id.indexOf("node") >= 0) {// 排除那些已经添加过的节点
                if (!el.groupFlag && !selected[el.d.id]) {
                    leftData.push(el.d);
                }
            }
        });
        // 打开设备管理对话框
        var value = con.getValue();
        var dm = new DeviceManagerDia({
            leftData: leftData,
            name: value.name,
            rightData: rightData
        });
        // ok
        dm.on("ok", function (info) {
            // 更新容器
            con.update(info);
        });
    });
    // 拖拽图标
    this.$holder.droppable({
        accept: ".icon",
        onDrop: function (e, dom) {
            if (ctx.editable) {
                // 三层拓扑不允许拖拽
                if (ctx.id == 1) {
                    return;
                }
                var top = $(dom).parent().scrollTop() || 0;
                var $dom = $(dom).draggable("proxy");
                var $img = $dom.find("img");
                var offset = $(this).offset();
                var box = ctx.util.getViewBox();
                var pos = {
                    x: $dom.offset().left - offset.left,
                    y: $dom.offset().top - offset.top - top,
                    w: 30,
                    h: 30
                };
                var svg = ctx.util.getSvg();
                var sw = svg.width(), sh = svg.height();
                /*BUG #54234 拓扑管理：点击添加图标，显示添加成功，但添加的图标在当前页面无法看见 huangping 2018/5/3 start*/
                //判断拖拽区域是否在面板外
                if ((sw - $(dom).parent().width()) < pos.x) return;
                /*BUG #54234 拓扑管理：点击添加图标，显示添加成功，但添加的图标在当前页面无法看见 huangping 2018/5/3 end*/
                pos.x = box.x + (pos.x * (box.w / sw));
                pos.y = box.y + (pos.y * (box.h / sh));
                var attr = {
                    x: pos.x,
                    y: pos.y,
                    w: pos.w,
                    h: pos.h,
                    src: $img.attr("src"),
                    visible: true,
                    type: "image"
                };
                var sizeStr = $dom.attr("data-size");
                if (sizeStr) {
                    var tmp = sizeStr.split(",");
                    attr.w = parseInt(tmp[0]);
                    attr.h = parseInt(tmp[1]);
                }
                var id = ctx.getIndex();
                if ($dom.data("cmd") == "group") {
                    ctx.getNode({
                        x: pos.x,
                        y: pos.y,
                        w: 200,
                        h: 200,
                        type: "group",
                        visible: true,
                        data: {
                            id: "group" + id,
                            rawId: id
                        }
                    });
                } else {
                    // 拖拽绘图类型
                    var type = $dom.attr("data-type");
                    if (type == "device") {
                        oc.resource.loadScript("resource/module/topo/contextMenu/AddNetDevice.js",
                            function () {
                                var tmp = new AddNetDevice();
                                tmp.on("ok", function (info) {
                                    // 判断是否重复
                                    ctx.getAllIps(function (map) {
                                        var id = map[info.manageIp];
                                        if (id && info.manageIp != "") {// 如果ip地址已经存在
                                            alert("该设备已经存在", "info");
                                            ctx.getEl("node" + id).setState("found");
                                        } else {
                                            // 是否拓扑发现
                                            if (info.isFind) {
                                                //回收站中是否有此设备
                                                var isHave = false;
                                                $.post(oc.resource.getUrl("topo/recycle/list.htm"), function (result) {
                                                    if (result.status == 200) {
                                                        var ids = [];
                                                        for (var i = 0; i < result.items.length; i++) {
                                                            var node = result.items[i];
                                                            //回收站中存在此设备，将其恢复
                                                            if (node.ip == info.manageIp) {
                                                                isHave = true;
                                                                ids.push(node.id);
                                                                oc.util.ajax({
                                                                    url: oc.resource.getUrl("topo/recycle/recover.htm"),
                                                                    data: {
                                                                        ids: ids.join(",")
                                                                    },
                                                                    success: function (result) {
                                                                        if (result.status == 200) {
                                                                            alert(result.msg || "恢复成功");
                                                                            ctx.refresh();
                                                                        } else {
                                                                            alert(result.msg || "恢复失败", "warning");
                                                                        }
                                                                    }
                                                                });
                                                                return;
                                                            }
                                                        }
                                                    }
                                                }, "json");
                                                if (!isHave) {
                                                    // 请求服务器単资源发现
                                                    oc.util.ajax({
                                                        url: oc.resource.getUrl("topo/find/single.htm"),
                                                        data: {
                                                            info: JSON.stringify({
                                                                ip: info.manageIp,
                                                                commonBodyName: info.commonBodyName,
                                                                name: info.name,
                                                                src: $img.attr("src"),
                                                                x: pos.x,
                                                                y: pos.y,
                                                                type: "node"
                                                            })
                                                        },
                                                        timeout: 9999999,
                                                        success: function (result) {
                                                            var tmp = result.data;
                                                            if (tmp) {
                                                                if (!tmp.isRepeat) {
                                                                    alert(tmp.msg, "info");
                                                                    // 刷新拓扑图
                                                                    if (tmp.node) {
                                                                        ctx.refresh();
                                                                    }
                                                                } else {
                                                                    alert(tmp.msg, "warning");
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            } else {
                                                //添加节点
                                                ctx.addNode({
                                                    manageIp: info.manageIp,
                                                    name: info.name,
                                                    type: info.type,
                                                    x: pos.x, y: pos.y, w: pos.w, h: pos.h,
                                                    src: $img.attr("src")
                                                });
                                            }
                                        }
                                    });
                                });
                            });
                    } else if (type == "cabinet" && ctx.topoType == "机房拓扑") {// 机柜
                        oc.resource.loadScript("resource/module/topo/contextMenu/AddCabinet.js",
                            function () {
                                new AddCabinet({
                                    subTopoId: ctx.id,
                                    attr: attr,
                                    onSave: function (result) {
                                        var img = new Image({
                                            paper: ctx.paper,
                                            holder: ctx.holder,
                                            attr: $.extend({
                                                    w: 200,
                                                    h: 200
                                                },
                                                JSON.parse(result.data.attr))
                                        });
                                        img.id = result.data.id;
                                        ctx.drawUtil.pushEl(img);
                                        img.d.visible = true;
                                        img.d.subTopoId = result.data.subTopoId;
                                        img.d.dataType = type;
                                    }
                                });
                            });
                    } else {
                        ctx.addOther($.extend(attr, {type: type}));
                    }
                }
            }
        }
    });
    // 监听保存
    eve.on("topo.save", function (callBack) {
        ctx.save(callBack);
    });
    // 监听刷新
    eve.on("topo.refresh", function () {
        ctx.refresh();
    });
    // 监听连线
    eve.on("element.click", function () {
        if (ctx.editable) {
            ctx.link.add(this);
        }
    });
    // 监听连线完成
    this.link.onEnd = function (from, to) {
        // 链路
        /*
		 * if(from.d.instanceId || to.d.instanceId){
		 * oc.resource.loadScript("resource/module/topo/contextMenu/TopoAddLink.js",
		 * function(){ var tal = new TopoAddLink({ from:from,to:to });
		 * tal.on("ok",function(linkInfo){ //添加链路到后台 oc.util.ajax({
		 * url:oc.resource.getUrl("topo/newlink.htm"), data:{
		 * info:JSON.stringify(linkInfo) }, timeout:1000000, type:"post",
		 * dataType:"json", success:function(link){ if(link && link.id){
		 * link.rawId=link.id; link.id="link"+link.id; var con = ctx.connect({
		 * conA:from, conB:to, data:link }); con.d.visible=true;
		 * ctx.refreshState(); ctx.refreshLinkData(); }else{ alert("添加失败"); } }
		 * }); }); }); } //一般连接 else{
		 */
        oc.util.ajax({
            url: oc.resource.getUrl("topo/link/addLink.htm"),
            data: {
                fromType: from.linktype,
                toType: to.linktype,
                from: from.d.rawId || from.d.id,
                to: to.d.rawId || to.d.id,
                subTopoId: ctx.id
            },
            success: function (result) {
                var tmp = result;
                if (tmp.state == 200) {
                    alert(tmp.msg, "info");
                    //划线完成后，处理多链路
                    eve("topo.save", this, function () {
                        eve("topo.refresh");
                    });
                } else {
                    alert(tmp.msg, "warning");
                }
            }
        });
        /* } */
    };
    // 监听绘制新区域
    eve.on("topo.newgroup", function (info) {
        // 获取中心点
        var vb = ctx.util.getViewBox();
        var group = ctx.getNode({
            x: vb.cx - 200,
            y: vb.cy - 100,
            w: 400,
            h: 200,
            type: "group",
            data: {
                id: "group" + ctx.getIndex(),
                name: info.name
            }
        });
        group.update(info);
    });
    // 绘制区域
    eve.on("topo.drawgroup.command", function () {
        ctx.drawUtil.cb.setEditable(true);
        eve.once("element.choserbox.finished", function (p) {
            if (this == ctx.drawUtil.cb) {
                var idx = ctx.getIndex();
                var group = ctx.getNode({
                    x: p.x1,
                    y: p.y1,
                    w: p.w < 170 ? 170 : p.w,
                    h: p.h < 140 ? 140 : p.h,
                    type: "group",
                    data: {
                        id: "group" + idx,
                        name: "",
                        rawId: idx,
                        subTopoId: ctx.id
                    }
                });
            }
        });
    });
    this.drawUtil.onFinished = function (el, cb) {
        if (el) {
            var _ctx = this;
            oc.util.ajax({
                url: oc.resource.getUrl("topo/addOther.htm"),
                data: {
                    attr: JSON.stringify(el.getValue()),
                    subTopoId: ctx.id
                },
                success: function (result) {
                    cb.call(_ctx, result);
                }
            });
        }
    };
    // 监听启用连线
    eve.on("topo.enablelink", function () {
        eve("doc_key_down", this, {
            altKey: true,
            keyCode: 90
        });
    });
    // 显示所有资源
    eve.on("topo.show.allresource", function () {
        $.each(ctx.els, function (key, el) {
            if (el.d && !el.d.visible) {
                el.show();
            }
        });
    });
    // 显示隐藏资源列表
    eve.on("topo.show.hidelist", function () {
        var list = [];
        $.each(ctx.els, function (key, el) {
            if (el.d && !el.d.visible && el.d.id.indexOf("node") >= 0) {
                list.push(el.d);
            }
        });
        var listDia = new TopoHideListDia({
            data: list
        });
        listDia.on("show", function (rows) {
            for (var i = 0; i < rows.length; ++i) {
                var row = rows[i];
                var el = ctx.getEl(row.id);
                if (el) {
                    el.show();
                }
            }
        });
    });
    // 切换设备显示标题类型
    eve.on("topo.change.devicetitle", function (type) {
        $.each(ctx.els, function (key, el) {
            if (el.d && el.d.id.indexOf("node") >= 0) {
                el.setTitle(type);
            }
        });
    });
    // 切换链路标题显示类型
    eve.on("topo.change.linktitle", function (type, force) {
        if (type == "none") {
            ctx.removeAllLinesText(true);
        } else {
            $.each(ctx.els, function (key, el) {
                if (el.d && el.d.id.indexOf("link") >= 0) {
                    //多链路不显示设置
                    if (!el.isMulti) el.setTitle(type, force);
                }
            });
        }
    });
    // 监听自定义类型单击
    eve.on("element.click", function () {
        ctx.link.add(this);
    });
    // 监听删除自定义类型
    eve.on("element.remove", function () {
        ctx.deleteItems.others.push(this.id);
    });
    // 监听背景更换
    eve.on("topo.changebg", function (src) {
        ctx.updateAttr({
            bgsrc: src
        }, function () {
            this.bgsrc = src;
            this.$svg.css({
                background: "url(" + (src || "") + ") no-repeat",
                "background-size": "100% 100%"
            });
        });
    });
    // 设置拓扑配置
    eve.on("topo.setcfg", function (cfg) {
        ctx.setCfg(cfg);
    });
    // 开始拖动
    eve.on("element.drag.start", function () {
        for (var i = 0; i < ctx.chosed.length; ++i) {
            var e = ctx.chosed[i];
            if (e) {
                e._choser_old_pos = e.getPos();
            }
        }
    });
    // 结束拖动
    eve.on("element.drag.move", function (pos) {
        //检查当前结点是否在多选行列中
        var inChosed = false;
        for (var i = 0; i < ctx.chosed.length; ++i) {
            var e = ctx.chosed[i];
            if (!this.d) {
                break;
            }
            if (e.d.id == this.d.id) {
                inChosed = true;
                break;
            }
        }
        if (!inChosed) {
            ctx.resetChose();
        } else {
            //一起拖动
            for (var i = 0; i < ctx.chosed.length; ++i) {
                var e = ctx.chosed[i];
                if (e && e.getPos && e.refresh) {
                    var p = e.getPos();
                    var op = e._choser_old_pos;
                    p.x = pos.x + op.x;
                    p.y = pos.y + op.y;
                    e.refresh(p);
                }
            }
        }
    });
    // 节点双击操作
    eve.on("element.dblclick", function () {
        //停止tip的定时器
        clearTimeout(ctx["cabinet_alarm_tip"]);
        var _ctx = this;
        if (this.d && this.d.id) {
            if (this.d.type && "net" == this.d.type.toLowerCase()) {// 如果是子网节点-打开该子网的设备列表
                oc.resource.loadScript("resource/module/topo/contextMenu/SubnetNodes.js",
                    function () {
                        new SubnetNodes({
                            node: _ctx,
                            dialog: true
                        });
                    });
            } else if (this.d.dataType && this.d.dataType == "cabinet") {// 如果是机柜
                oc.resource.loadScript("resource/module/topo/contextMenu/CabinetDeviceList.js",
                    function () {
                        new CabinetDeviceList({
                            id: _ctx.d.rawId || _ctx.d.id
                        });
                    });
            } else if (this.d.type == "subtopo") {
                eve("topo.loadsubtopo", this, parseInt(this.d.attr.subtopoId));
            } else {
                $.post(oc.resource.getUrl("topo/setting/get/" + this.linktype + this.d.id + ".htm"), function (info) {
                    if (!info || !info.openType) {// 默认打开资源详情页面
                        /*
						 * if(!_ctx.d.instanceId){
						 * info={openType:"url"};
						 * }else{
						 */
                        info = {
                            openType: "resource"
                        };
                        /* } */
                    }
                    switch (info.openType) {
                        case "resource":
                            if (_ctx.d.instanceId && _ctx.d.lifeState == "monitored") {// 必须是监控状态才能打开资源详情页面
                                // 坚持权限
                                oc.util.ajax({
                                    url: oc.resource.getUrl("topo/resource/checkauth.htm"),
                                    data: {
                                        instanceId: _ctx.d.instanceId
                                    },
                                    success: function (result) {
                                        if (result.state == 200) {
                                            oc.resource.loadScript("resource/module/resource-management/resourceDetailInfo/js/detailnewInfo.js",
                                                function () {
                                                    oc.module.resmanagement.resdeatilinfonew.open({
                                                        instanceId: _ctx.d.instanceId
                                                    });
                                                });
                                        } else {
                                            alert(result.msg, "warning");
                                        }
                                    }
                                });
                            } else {
                                if (_ctx) {
                                    // alert("非实例化节点不能打开（资源详情页面或在双击操作做设置其他的感兴趣的行为）","warning");
                                }
                            }
                            break;
                        case "url":
                            if (info.url) {
                                oc.resource.loadScript(
                                    "resource/module/topo/contextMenu/IFrameDia.js", function () {
                                        new IFrameDia({
                                            w: 900,
                                            h: 500,
                                            url: info.url,
                                            title: info.url
                                        });
                                    });
                            }
                            break;
                        case "map":
                            if (info.map) {
                                eve("topo.loadsubtopo", this, parseInt(info.map));
                            }
                            break;
                        case "group":
                            if (info.map) {
                                eve("topo.loadsubtopo", this, parseInt(info.map));
                            }
                            break;
                    }
                }, "json");
            }
        }
    });
    //点击其他地方，隐藏文本框
    eve.on("svg_mouse_click", function (p, e) {
        ctx.drawUtil.sizer.hide();
    });
};
Topo.prototype.updateAttr = function (data, callBack) {
    data.id = this.id;
    var ctx = this;
    $.post(oc.resource.getUrl("topo/subtopo/updateAttr.htm"), data, function (
        result) {
        if (result.state == 200) {
            alert("更新拓扑属性成功");
            if (callBack) {
                callBack.apply(ctx);
            }
        } else {
            alert(result.msg, "warning");
        }
    }, "json");
};
Topo.prototype.setAttr = function (topoif) {
    if (topoif) {
        var ctx = this;
        this.bgsrc = topoif.bgsrc;
        this.parentId = topoif.parentId;
        this.topoType = topoif.topoType || "二层拓扑";
        //判断是否是首页模块显示，参数为空则不是首页
        if (ctx.args.showBgIm == null) {
            this.$svg.css({
                background: "url(" + (topoif.bgsrc || "themes/" + Highcharts.theme.currentSkin + "/images/topo/topoIcon/bg/bg_default.png") + ") no-repeat",
                "background-size": "100% 100%"
            });
        }

        topoif.attr = JSON.parse(topoif.attr || "{}");
        ctx.paperAttr = topoif;

        switch (this.topoType) {
            case "地图拓扑":
                if (ctx.topoSizerBar) ctx.topoSizerBar.hide();
                break;
            case "机房拓扑":
                if (ctx.topoSizerBar) {
                    ctx.topoSizerBar.show();
                    ctx.topoSizerBar.setValue(topoif.attr);
                }
                //刷新机柜的状态
                var ids = [], map = {};
                $.each(this.drawUtil.getCabinets(), function (idx, cab) {
                    ids.push(cab.d.rawId);
                    map[cab.d.rawId] = cab;
                });
                oc.util.ajax({
                    url: oc.resource.getUrl("topo/other/getCabinetState.htm"),
                    data: {
                        ids: ids.join(",")
                    },
                    success: function (result) {
                        if (result.status == 200) {
                            $.each(result.info, function (id, item) {
                                var cabinet = map[id];
                                if (cabinet) {
                                    //保存告警信息
                                    cabinet.d.alarmInfo = item;
                                    var picState = "";
                                    switch (item.state) {
                                        case "NORMAL":
                                            picState = "normal";
                                            break;
                                        case "NORMAL_CRITICAL":
                                            picState = "danger";
                                            break;
                                        case "NORMAL_UNKNOWN":
                                            picState = "normal";
                                            break;
                                        case "NORMAL_NOTHING":
                                            picState = "normal";
                                            break;
                                        case "MONITORED":
                                            picState = "normal";
                                            break;
                                        case "NOT_MONITORED":
                                            picState = "normal";
                                            break;
                                        case "UNKOWN":
                                            picState = "normal";
                                            break;
                                        case "UNKNOWN_NOTHING":
                                            picState = "normal";
                                            break;
                                        case "DELETED":
                                            break;
                                        case "CRITICAL":
                                            picState = "danger";
                                            break;
                                        case "CRITICAL_NOTHING":
                                            picState = "danger";
                                            break;
                                        case "SERIOUS":
                                            picState = "severity";
                                            break;
                                        case "WARN":
                                            picState = "warning";
                                            break;
                                        default:
                                            picState = "normal";
                                    }
                                    if (picState != "normal") {	//告警时改变颜色和图片
                                        cabinet.setImageSrcWithoutSave(oc.resource.getUrl("topo/image/change.htm?path=" + cabinet.img.attr("src") + "&type=" + picState));
                                    }
                                    if (item.msg) {	//注册告警显示事件
                                        ctx.showCabinetMsg(cabinet);
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            default:
                if (ctx.topoSizerBar) {
                    ctx.topoSizerBar.show();
                    ctx.topoSizerBar.setValue(topoif.attr);
                }
        }
    }
};
Topo.prototype.showCabinetMsg = function (cabinet) {
    if (cabinet && cabinet.img) {
        var ctx = this;
        cabinet.img.mouseover(function (e) {
            ctx.timeAfter(function () {
                oc.topo.tips.show({
                    type: "cabinetAlarm",
                    x: e.x, y: e.y,
                    value: {
                        msg: cabinet.d.alarmInfo.msg,
                        name: cabinet.d.text
                    }
                });
            }, 800, "cabinet_alarm_tip", ctx);
        });
    }
};
// hover-鼠标一上去的效果
Topo.prototype.addHover = function (pos) {
    var ctx = this;
    var hover = this.paper.path("M0 0L0 0");
    hover.init = function () {
        this.attr({
            "fill": "lightgrey",
            "stroke": null,
            "fill-opacity": 0
        });
        this.refresh(pos);
    };
    hover.refresh = function (pos) {
        var path = ctx.getRoundRectPath($.extend({
            r: 5
        }, pos));
        this.attr("path", path);
    };
    hover.init();
    return hover;
};
Topo.prototype.getIcon = function (src, type) {
    return "topo/image/change.htm?path=" + src + "&type=" + type;
};
// 一般节点
Topo.prototype.getNormalNode = function (args) {
    var ctx = this;
    var con = {};
    // 业务数据
    con.d = args.data;
    if (args.data.attr) {
        con.d.attr = JSON.parse(args.data.attr);
    } else {
        con.d.attr = {};
    }
    con.linktype = "node";
    con.getMaster = function () {
        return this.handler;
    };
    con.paper = this.paper;
    con.ctx = this;
    // 获取容器的位置信息
    con.getPos = function () {
        var r = {
            x: this.handler.attr("x"),
            y: this.handler.attr("y"),
            w: this.handler.attr("width"),
            h: this.handler.attr("height")
        };
        r.cx = r.x + r.w / 2;
        r.cy = r.y + r.h / 2;
        return r;
    };
    con.addIcon = function (ags) {
        var rx = ags.iw / 2, ry = ags.ih - 16;
        this.icon = ctx.paper.image(ags.iconSrc, rx, ry, 15, 15);
        this.icon.rx = rx;
        this.icon.ry = ry;
        this.icon.node.removeAttribute("preserveAspectRatio");
    };
    con.init = function () {
        var _ctx = this;
        this.d = $.extend({
            id: "node" + ctx.getIndex()
        }, this.d);
        this.bg = ctx.paper.rect(args.x, args.y, args.w, args.h, 4).attr({
            "fill-opacity": 0,
            "stroke": "none"
        });
        this.handler = ctx.paper.rect(args.x, args.y, args.w, args.h).attr({
            fill: "white",
            "fill-opacity": 0,
            "stroke": "none"
        });
        if (!this.hover) {
            this.hover = ctx.paper.rect(args.x, args.y, args.w, args.h, 4).attr({
                "fill-opacity": 0, "fill": "lightgrey",
                "stroke": "none"
            });
        }
        this.hover.drag(function (dx, dy) {
            if (ctx.editable) {
                var p = {
                    x: dx,
                    y: dy
                };
                eve("element.drag.beforemove", con, p);
                eve("element.drag.move", con, p);
                ctx.setDirty(true);
                var tp = con.getPos();
                tp.x = this.ox + p.x + 24;
                tp.y = this.oy + p.y + 10;
                con.refresh(tp);
            }
        }, function () {
            eve("element.drag.start", con);
            this.ox = this.attr("x");
            this.oy = this.attr("y");
            // 停止播放
            if (con.connects && (!ctx.cfg || !ctx.cfg.noline)) {
                $.each(con.connects, function (idx, line) {
                    if (line && line.flow) {
                        line.flow.stopPlay();
                    }
                });
            }
            if (con.glow) {
                con.glow.stopPlay();
            }
        }, function () {
            // 开始播放
            if (con.connects && (!ctx.cfg || !ctx.cfg.noline)) {
                $.each(con.connects, function (idx, line) {
                    if (line && line.flow) {
                        line.flow.startPlay();
                    }
                });
            }
            if (con.glow) {
                con.glow.startPlay();
            }
            if (ctx.chosed.length > 0) {
                ctx.checkSubtopoNode(con.getPos(), con.d.rawId);
            }
        });
        // 节点右键菜单
        $(this.hover.node).on("contextmenu", function (e) {
            //clearInterval("tipTimeId");
            clearInterval(ctx["tipTimeId"]);
            oc.topo.tips.hide();
            if (ctx.editable && ctx.id != 1) {
                eve("element.contextmenu", _ctx, e, _ctx);
            }
            e.stopPropagation();
            e.preventDefault();
            return false;
        });
        this.setSrc(args.src);
        if (!this.img) {
            var rx = 1, ry = 1;
            this.img = ctx.paper.image(oc.resource.getUrl(this.src), rx, ry, 30, 30);
            this.img.rx = 1;
            this.img.ry = 1;
            this.img.node.removeAttribute("preserveAspectRatio");
        }
        if (!this.icon) {
            args.iconSrc = oc.resource.getUrl("resource/themes/" + Highcharts.theme.currentSkin + "/images/topo/not_monitored.png");
            this.addIcon(args);
        }
        if (!this.text) {
            this.text = ctx.paper.text(0, 0, this.d[ctx.cfg.device.tagField] || "")
                .attr({
                    "fill": ctx.cfg.fontColor || "white",
                    "font-size": ctx.cfg.fontSize
                });
        }
        this.hover.mouseover(function () {
            if (con.hover && !con.checked) {
                con.hover.attr("fill-opacity", 0.4);
                ctx.timeAfter(function () {
                    var p = $(con.handler.node).position();
                    if (!p || !p.left) return;
                    var tp = con.getPos();
                    var txt = con.text.attr("text");
                    if (txt && txt.indexOf("岳阳") >= 0) {
                        oc.topo.tips.show({
                            type: "gaofa",
                            x: p.left - 10 + tp.w,
                            y: p.top + tp.h + 86,
                            value: {
                                src: "module/topo/yueyang.gif"
                            }
                        });
                    } else if (txt && txt.indexOf("长沙") >= 0) {
                        oc.topo.tips.show({
                            type: "gaofa",
                            x: p.left - 10 + tp.w,
                            y: p.top + tp.h + 86,
                            value: {
                                src: "module/topo/changsha.gif"
                            }
                        });
                    } else if (txt && txt.indexOf("酒泉") >= 0) {
                        oc.topo.tips.show({
                            type: "gaofa",
                            x: p.left - 10 + tp.w,
                            y: p.top + tp.h + 86,
                            value: {
                                src: "module/topo/jiuquan.gif"
                            }
                        });
                    } else {
                        if (oc.topo.tips) {
                            oc.topo.tips.show({
                                type: "node",
                                x: p.left - 10 + tp.w,
                                y: p.top + tp.h,
                                value: {
                                    name: con.d.showName || con.d.attr.name,
                                    ip: con.d.ip,
                                    vendor: con.d.vendorName,
                                    series: con.d.series,
                                    instanceId: con.d.instanceId
                                }
                            });
                        }
                    }
                }, 800, "tipTimeId", con);
            }
        });
        this.hover.mouseout(function () {
            if (ctx.tipTimeId) {
                clearInterval(ctx.tipTimeId);
                ctx.tipTimeId = null;
            }
        });
        $(this.hover.node).click(function (e) {
            eve("element.click." + this.id, con);
            e.stopPropagation();
            e.preventDefault();
            return false;
        });
        // 双击事件
        $(this.hover.node).on("dblclick", function () {
            eve("element.dblclick", con);
        });
        $(this.hover.node).on("mouseout", function () {
            if (con.hover) {
                con.hover.attr("fill-opacity", 0);
            }
        });
        // 光晕
        if (this.d.rx > 0) {
            this.playGlow(this.d.rx);
        }
        this.refresh();
        this.checked = false;
        this.toFront();
        this.setTitle();
    };
    // 添加光晕
    con.playGlow = function (level) {
        /*
		 * if(!this.glow){ this.glow = new TopoDeviceGlow({ device:con,
		 * paper:ctx.paper, level:level||1 }); }else{
		 * this.glow.args.level=level; }
		 */
    }, con.toFront = function () {
        this.handler.toFront();
        eve("element.tofront." + con.getMaster().id, con);
        this.img.toFront();
        if (this.icon)
            this.icon.toFront();
        this.text.toFront();
        if (this.hover) {
            this.hover.toFront();
        }
    };
    con.toBack = function () {
        this.handler.toBack();
        eve("element.toback." + con.getMaster().id, con);
        this.img.toBack();
        /*		if (this.icon)
			this.icon.toBack();
		this.text.toBack();
		if (this.hover) {
			this.hover.toBack();
		}*/
    };
    // 获取层次
    con.getZIndex = function () {
        return this.zindex || 0;
    };
    con.setZIndex = function (zindex) {
        this.zindex = zindex || 0;
    };
    con.getValue = function () {
        var p = this.getPos();
        var r = {
            "id": this.d.rawId,
            "icon": this.src || ctx.getIcon(this.img.attr("src"), "normal"),
            "x": p.x,
            "y": p.y,
            "rx": this.d.rx,
            "ry": this.d.ry,
            "iconWidth": this.img.attr("width"),
            "iconHeight": this.img.attr("height"),
            visible: this.d.visible,
            subTopoId: ctx.id,
            attr: JSON.stringify(this.d.attr)
        };
        // 如果是自定义的网络节点
        if (this.d && this.d.rawId <= 0) {
            r.ip = this.d.ip;
            r.commonBody = this.d.commonBody;
            r.showName = this.d.showName;
        }
        return r;
    };
    // 覆盖remove方法
    con.remove = function (cfg) {
        if (this.img)
            this.img.remove();
        if (this.text)
            this.text.remove();
        if (this.hover)
            this.hover.remove();
        if (this.icon)
            this.icon.remove();
        ctx.deleteItems.nodes.push(ctx.getRealId(this.d.id));
        // 删除链路
        if (this.connects && (!cfg || !cfg.noline)) {
            $.each(this.connects, function (idx, line) {
                if (line) {
                    line.remove(true);
                }
            });
        }
        this.handler.remove();
        delete ctx.els[this.d.id];
        if (con.glow) {
            con.glow.remove();
        }
        this.bg.remove();
    };
    // 覆盖hide方法
    con.hide = function () {
        if (this.img)
            this.img.hide();
        if (this.text)
            this.text.hide();
        if (this.hover)
            this.hover.hide();
        this.handler.hide();
        // 隐藏链路
        if (this.connects) {
            $.each(this.connects, function (key, c) {
                if (c && c.hide) {
                    c.hide();
                }
            });
        }
        // 将标记设为隐藏
        if (con.d) {
            con.d.visible = false;
        }
        this.icon.hide();
        this.bg.hide();
    };
    con.show = function () {
        if (this.img)
            this.img.show();
        if (this.text)
            this.text.show();
        if (this.hover)
            this.hover.show();
        this.handler.show();
        // 显示链路
        if (this.connects) {
            $.each(con.connects, function (key, c) {
                if (c && c.show) c.show();
            });
        }
        // 将标记设为显示
        if (con.d) {
            con.d.visible = true;
        }
        if (this.d.instanceId && this.d.lifeState == "not_monitored") {
            this.icon.show();
        }
        this.bg.show();
    };
    con.setLifeState = function (type) {
        this.d.lifeState = type;
        switch (type) {
            case "not_monitored":
                this.icon.attr("src", oc.resource.getUrl("resource/themes/" + Highcharts.theme.currentSkin + "/images/topo/not_monitored.png"));
                this.setState("NOT_MONITORED");
                this.icon.show();
                break;
            default:
                this.icon.hide();
                break;
        }
    };
    con.setSrc = function (src) {
        if (!src)
            return;
        if (src[0] == "/") {
            src = src.substring(1);
        }
        if (src.indexOf("topo/image/change.htm") != 0) {
            var idx = src.indexOf("resource");
            if (idx < 0) {
                src = "resource/" + src;
            } else {
                src = src.substring(idx, args.src.length);
            }
        }
        this.src = src;
    };
    // 设置状态
    con.setState = function (type) {
        this.state = type || this.state || "normal";
        // 映射两种其他状态
        this.checked = false;
        var picState = "normal", alarmState = "none", actionFlag = false;
        switch (this.state) {
            case "NORMAL":
                this.img.attr("src", oc.resource.getUrl(this.src));
                picState = "normal";
                break;
            case "NORMAL_CRITICAL":
                picState = "normal";
                alarmState = "danger";
                break;
            case "NORMAL_UNKNOWN":
                picState = "normal";
                break;
            case "NORMAL_NOTHING":
                picState = "normal";
                break;
            case "MONITORED":
                picState = "normal";
                break;
            case "NOT_MONITORED":
                picState = "disabled";
                alarmState = "not_monitored";
                break;
            case "WARN":
                picState = "normal";
                alarmState = "warning";
                break;
            case "SERIOUS":
                picState = "normal";
                alarmState = "severity";
                break;
            case "CRITICAL":
                picState = "danger";
                alarmState = "danger";
                break;
            case "CRITICAL_NOTHING":
                picState = "danger";
                break;
            case "UNKOWN":
                break;
            case "UNKNOWN_NOTHING":
                break;
            case "DELETED":
                break;
            case "choosed":
                actionFlag = true;
                this.bg.attr({
                    "stroke": "#fff",
                    "stroke-width": 1,
                    "stroke-opacity": 0.3,
                    "fill-opacity": 0.2,
                    "fill": "#d1d1d1"
                });
                break;
            case "unchoosed":
                this.bg.attr({
                    "stroke": "none",
                    "fill-opacity": 0
                });
                actionFlag = true;
                break;
            case "found":
                actionFlag = true;
                this.hover.attr("fill-opacity", 0.4);
                break;
            case "unfound":
                actionFlag = true;
                this.hover.attr("fill-opacity", 0);
                break;
        }
        //操作动作状态不改变告警状态
        if (!actionFlag) {
            //图片状态
            if (this.src.indexOf("topo/image/change.htm") < 0) {
                this.img.attr("src", oc.resource.getUrl("topo/image/change.htm?path=" + this.src + "&type=" + picState));
            } else {
                this.img.attr("src", oc.resource.getUrl(this.src + "&type=" + picState));
            }
            //三角形状态
            if (alarmState != "none") {
                var iconSrc = oc.resource.getUrl("resource/themes/blue/images/topo/" + alarmState + ".png");
                if (this.icon) {
                    this.icon.attr("src", iconSrc);
                    this.icon.show();
                } else {
                    this.addIcon({
                        iconSrc: iconSrc,
                        iw: args.iw,
                        ih: args.ih
                    });
                }
            } else {
                this.icon.hide();
            }
        }

    };
    con.refresh = function (pos, isSilence) {
        if (!ctx.editable)
            return;
        pos = $.extend({
            x: 0, y: 0
        }, pos || this.getPos());
        if (this.img) {
            this.img.attr({
                x: pos.x + this.img.rx,
                y: pos.y + this.img.ry,
                width: pos.w,
                height: pos.h
            });
        }
        if (this.icon) {
            var rw = (pos.w / 35), rh = (pos.h / 35);
            this.icon.attr({
                x: pos.x + pos.w / 2,
                y: pos.y + pos.h / 2,
                width: 15 * rw,
                height: 15 * rh
            });
        }
        if (this.text) {
            this.text.attr({
                x: pos.x + pos.w / 2,
                y: this.img.attr("y") + this.img.attr("height") + 10
            });
        }
        var extendX = 24, extendY = 20;
        this.hover.attr({
            x: pos.x - extendX,
            y: pos.y - 10,
            width: pos.w + 2 * extendX,
            height: pos.h + 2 * extendY
        });
        this.handler.attr({
            x: pos.x,
            y: pos.y,
            width: pos.w,
            height: pos.h
        });
        this.bg.attr({
            x: pos.x - extendX,
            y: pos.y - 10,
            width: pos.w + 2 * extendX,
            height: pos.h + 2 * extendY
        });
        if (!isSilence) {
            eve("element.refresh." + this.getMaster().id, this, pos);
        }
    };
    con.setTitle = function (field) {
        if (this.text) {
            field = field || ctx.cfg.device.tagField;
            var tmpTxt = this.d[field];
            if (this.d.type == "subtopo") {
                tmpTxt = this.d.attr.name;
            }
            if ((!tmpTxt || tmpTxt == "") && this.d.attr) {
                tmpTxt = this.d.attr.name || "";
            }
            this.text.attr({
                "text": tmpTxt || this.d.ip
            });
        }
        ;
    };
    con.init();
    ctx.putEl(con.d.id, con);
    return con;
};
Topo.prototype.setState = function (items) {
    var ctx = this;
    $.each(items, function (key, item) {
        var el = ctx.getEl(item.type + item.id);
        if (el)
            el.setState(item.state);
    });
};
Topo.prototype.getRealId = function (idStr) {
    var reg = new RegExp(/[\-0-9]*$/);
    return parseInt(("" + idStr).match(reg)[0]);
};
Topo.prototype.getNode = function (args) {
    args.type = args.type || "node";
    if (args.type == "node") {
        return this.getNormalNode(args);
    } else if (args.type == "group") {
        return this.groupCon(args);
    }
};
Topo.prototype.enableDrawLine = function (flag) {
    this.drawLineFlag = flag;
    if (this.drawHelpLine)
        this.drawHelpLine.remove();
    this.drawHelpLine = null;
    this.highDrag = false;
};
// 查找上联核心设备
Topo.prototype.checkCoreDevice = function (obj) {
    var retn = [];
    // 检查该ID的类型
    if (obj) {
        var dt = "node";
        if (!obj.type) {
            dt = "other";
        }
        var el = this.els[dt + obj.id];
        // 检查该节点的所有链路的另一端是否是网络设备（核心设备）
        if (el && el.getConnects) {
            var connects = el.getConnects();
            if (connects) {
                for (var i = 0; i < connects.length; ++i) {
                    var con = connects[i];
                    if (con.fromNode.d.rawId == obj.id
                        && con.toNode.d.type != "HOST"
                        && con.toNode.d.type != "SERVER") {
                        retn.push(con.toNode.d);
                    } else if (con.fromNode.d.type != "HOST"
                        && con.fromNode.d.type != "SERVER") {
                        retn.push(con.fromNode.d);
                    }
                }
            }
        }
    }
    return retn;
};
Topo.prototype._hasConnects = function (node, line) {
    for (var i = 0; i < node.connects.length; i++) {
        var _con = node.connects[i];
        if (line && _con && _con.d && line.d && _con.d.rawId == line.d.rawId) {
            return true;
        }
    }
    return false;
};
Topo.prototype.getAllIps = function (callBack) {
    var ctx = this;
    $.post(oc.resource.getUrl("topo/getAllIpsForSubtopo.htm"), {
        subTopoId: this.id
    }, function (result) {
        if (result.state == 200 && callBack) {
            callBack.call(ctx, result.info);
        } else {
            alert(result.msg, "warning");
        }
    }, "json");
};
// 连线
Topo.prototype.connect = function (args) {
    var ctx = this;
    var attr = JSON.parse(args.attr || "{}");
    var conA = args.conA, conB = args.conB;
    if (!conA || !conB) return null;
    //实例化一条连线
    var line = null;
    //判断是否需要使用曲线显示
    var key1 = args.data.from + "-" + args.data.to, key2 = args.data.to + "-" + args.data.from;
    if (this.lineMap[key1] || this.lineMap[key2]) {
        var eline = null, ekey = null;
        if (this.lineMap[key1]) {
            line = new BendConnect({
                from: conB,
                to: conA,
                drawer: this.drawer,
                paper: this.paper,
                holder: this.holder
            });
            eline = this.lineMap[key1];
            ekey = key1;
        } else {
            line = new BendConnect({
                from: conA,
                to: conB,
                drawer: this.drawer,
                paper: this.paper,
                holder: this.holder
            });
            eline = this.lineMap[key2];
            ekey = key2;
        }
        if (eline instanceof Connect) {
            var data = $.extend({}, eline.d);
            data.id = data.rawId;
            this.removeEl(eline.d.id);
            if (key1 == ekey) {
                this.lineMap[key2] = line;
            }
            delete this.lineMap[ekey];
            this.drawLine(data);
        }
    } else {
        line = new Connect({
            from: conA,
            to: conB,
            paper: this.paper,
            holder: this.holder
        });
    }
    this.lineMap[key1] = line;
    line.paper = this.paper;
    line.fromNode = conA;
    line.toNode = conB;
    // 业务数据
    line.d = args.data || {};
    line.d.subTopoId = this.id;
    // 让节点保存该链路
    if (!conA.connects)
        conA.connects = [];
    if (!conB.connects)
        conB.connects = [];
    if (!this._hasConnects(conA, line)) {
        conA.connects.push(line);
    }
    if (!this._hasConnects(conB, line)) {
        conB.connects.push(line);
    }
    // 隐藏
    line.hide = function () {
        this.line.hide();
        this.removeText();
        this.d.visible = false;
        if (this.line.disableFlagText) {
            this.line.disableFlagText.hide();
        }
        //隐藏多链路
        if (ctx.multilink) ctx.multilink.hide(line);
        //隐藏上下行流量
        this.hideSpeedLineTitle();
        if (this.flow) {
            this.flow.remove();
            this.flow = null;
        }
    };
    line.setLifeState = function (type) {
        this.d.lifeState = type;
        this.setState(type);
    };
    line.danger = function () {
        if (this instanceof BendConnect) {
            this.line.danger();
        } else {
            var p = this.line.getPos();
            if (!this.line.disableFlagText) {
                this.line.disableFlagText = this.paper.text(p.cx, p.cy, "×")
                    .attr({
                        fill: "red",
                        "font-size": 30
                    });
                this.line.on("refresh", function (p) {
                    if (this.disableFlagText) {
                        //如果是多链路移动叉叉位置
                        var cx = p.cx, cy = p.cy;
                        if (line.d.multiNumber > 1) {
                            cx = ((p.x2 + p.cx) / 2 + p.cx) / 2;
                            cy = ((p.y2 + p.cy) / 2 + p.cy) / 2;
                        }
                        this.disableFlagText.attr({
                            x: cx,
                            y: cy
                        });
                    }
                });
            }
        }
    };
    // 线状态
    line.setState = function (type) {
        // 清空禁用状态的效果
        if (this.line.disableFlagText && type != "disabled") {
            this.line.disableFlagText.remove();
            this.line.disableFlagText = null;
        }
        if (this instanceof BendConnect) {
            if (this.line.dangerText) {
                this.line.dangerText.remove();
                this.line.dangerText = null;
            }
        }
        if (type != "hover") {
            this.state = type;
        }
        this.show();
        var color = "#47C21F";
        switch (type) {
            case "NORMAL":
                color = "#47C21F";//绿色
                break;
            case "NORMAL_CRITICAL":
                color = "#47C21F";//绿色
                break;
            case "NORMAL_UNKNOWN":
                color = "#47C21F";//绿色
                break;
            case "NORMAL_NOTHING":
                color = "#47C21F";//绿色
                break;
            case "MONITORED":
                color = "#47C21F";//绿色
                break;
            case "NOT_MONITORED":
                color = "blue";
                break;
            case "WARN":
                color = "#F5CC00";// 黄色
                break;
            case "SERIOUS":
                color = "#F96E03";// 橙色
                break;
            case "CRITICAL":
                color = "#71716D";//灰色
                this.danger();
                break;
            case "CRITICAL_NOTHING":
                color = "#71716D";//灰色
                this.danger();
                break;
            case "UNKOWN":
                color = "#47C21F";//灰色
                break;
            case "UNKNOWN_NOTHING":
                color = "#47C21F";//灰色
                break;
            case "DELETED":
                this.color = "white";
                break;
            case "notlink":
                color = "white";
                break;
            case "normal":
                color = "#47C21F";//绿色
                break;
            case "warning":
                color = "#F5CC00";// 黄色
                break;
            case "disabled":
                color = "#71716D";//灰色
                break;
            case "nodata"://灰色
                color = "#71716D";
                break;
            case "danger"://断开有叉
                color = "#71716D";//灰色
                this.danger();
                break;
            case "severity":
                color = "#F96E03";// 橙色
                break;
            case "hover"://鼠标hover
                color = "blue";
                break;
            case "not_monitored"://未监控
                color = "blue";
                break;
            default ://默认不可显示
                this.hide();
                break;
        }
        ;
        this.line.setColor(color);
    };
    // 显示
    line.show = function () {
        if (this.fromNode.d && this.fromNode.d.visible && this.toNode.d && this.toNode.d.visible) {
            this.line.show();
            this.d.visible = true;
            if (this.line.disableFlagText) {
                this.line.disableFlagText.show();
            }
            //显示多链路
            if (ctx.multilink) ctx.multilink.show(line);
            //显示上下行流量
            if (ctx.cfg.link.hasTag) {
                this.showSpeedLineTitle();
            }
            if (ctx.cfg.effect.flow) {
                this.addFlow();
            }
            this.setTitle(ctx.cfg.link.tagField);
        }
    };
    var oldRemove = line.remove;
    line.remove = function (force) {
        var self = this;

        function _delete() {
            // 查看是否有虚拟的核心设备删除
            if (self.d.type == "dash" && self.fromNode && self.toNode) {
                var node = null;
                if (self.fromNode.d.type != "HOST"
                    && self.fromNode.d.type != "SERVER") {
                    node = self.fromNode;
                } else {
                    node = self.toNode;
                }
                if (node.connects && node.connects.length == 1) {
                    node.remove({
                        noline: true
                    });
                }
            }
            oldRemove.call(self);
            //ctx.deleteItems.links.push(ctx.getRealId(self.d.id));
            if (self.line.disableFlagText) {
                self.line.disableFlagText.remove();
                self.line.disableFlagText = null;
            }
            delete ctx.els[self.d.id];
            //删除多链路信息
            ctx.multilink.remove(self);
            //删除上下行流量
            self._removeSpeedLineTitle();
            if (self.flow) {
                self.flow.remove();
                self.flow = null;
            }
        }

        if (force) {
            _delete();
            return;
        }
        $.messager.confirm("警告", "确定删除此链路？", function (r) {
            if (r) {
                oc.util.ajax({
                    url: oc.resource.getUrl("topo/link/removeLink.htm"),
                    data: {
                        subTopoId: ctx.id,
                        ids: line.d.rawId
                    },
                    success: function (result) {
                        var alertType = "info";
                        if (result.code == 200) {
                            _delete();
                        } else {
                            alertType = "danger";
                        }
                        alert(result.msg || "", alertType);
                    }
                });
            }
        });
    };
    // 获取线位置
    line.getPos = function () {
        return this.line.getPos();
    };
    // 添加方向
    line.addArrow = function () {
        this.line.showArrow(true);
        return this.line.arrow;
    };
    // 删除方向
    line.removeArrow = function () {
        this.line.showArrow(false);
    };
    // 删除线标
    line.removeText = function () {
        if (this.text) {
            this.text.remove();
            this.text = null;
        }
        if (this instanceof BendConnect) {
            this.removeBendText();
        }
    };
    // 添加光晕
    line.addGlow = function (color) {
        this.line.setColor(color);
        return this.line.glowSet;
    };
    // toFront
    line.toFront = function () {
        this.line.toFront();
        if (this.text) {
            this.text.toFront();
        }
        if (this.multiRect && this.multiNumber) {
            this.multiRect.toFront();
            this.multiNumber.toFront();
        }
    };
    line.toBack = function () {
        this.line.toBack();
        if (this.text) {
            this.text.toBack();
        }
    };
    // 初始化连线
    line.init = function (cfg) {
        this.state = "unvisible";
        if (!this.d) {
            this.d = {
                id: "link" + ctx.getIndex()
            };
        }
        ctx.putEl(this.d.id, this);
        if (true) {
            // this.glow=this.addGlow("green");
            // 线的hover
            this.line.line.mouseover(function (e) {
                //鼠标移上去变粗连线
                this.attr({
                    "stroke-width": 3 * this.attr("stroke-width"),
                    "stroke": line.line.line.attr("stroke")
                });

                //多链路暂时鼠标移上去不提示任何信息，待确定显示内容后再开发
                if (line.isMulti) return;

                ctx.timeAfter(function () {
                    if (line.d.instanceId) {
                        var p = line.getPos();
                        oc.topo.tips.show({
                            type: "link",
                            x: e.pageX || p.cx,
                            y: e.pageY || p.cy,
                            value: {
                                instanceId: line.d.instanceId
                            }
                        });
                    } else {
                        if (line.d.type == "link") {
                            alert("未监控的链路", "warning");
                        }
                    }
                }, 800, "tipTimeId", ctx);

                if (oc.topo.flow) {
                    oc.topo.flow.setLine(line.line.line);
                    oc.topo.flow.update();
                    oc.topo.flow.startPlay();
                }
            });
            this.line.line.click(function () {
                eve("element.click." + this.id, line);
            });
            this.line.line.mouseout(function () {
                this.attr({
                    "stroke-width": this.attr("stroke-width") / 3,
                    "stroke": line.line.line.attr("stroke")
                });
                if (ctx.tipTimeId) {
                    clearInterval(ctx.tipTimeId);
                    ctx.tipTimeId = null;
                }
                oc.topo.tips.hide();
            });
            // 单击
            // 线上右键菜单
            /*
			 * for(var i=0;i<this.glow.length;++i){
			 * $(this.glow[i].node).on("contextmenu",function(e){
			 * if(ctx.editable && ctx.id!=1){
			 * eve("element.contextmenu",ctx,e,line); } e.stopPropagation();
			 * return false; }); }
			 */
            $(this.line.line.node).on("contextmenu", function (e) {
                clearInterval("tipTimeId");
                oc.topo.tips.hide();
                if (ctx.editable && ctx.id != 1) {
                    eve("element.contextmenu", ctx, e, line);
                }
                e.stopPropagation();
                return false;
            });
            this.line.line.attr("stroke-width", 2);
            //画多链路数量
            ctx.multilink.drawNumber(ctx, this);
        }
        this.toFront();
        this.refresh();
    };
    //显示上下行流量箭头
    line.showSpeedLineTitle = function () {
        if (this.upSpeedLineTitle) this.upSpeedLineTitle.show();
        if (this.downSpeedLineTitle) this.downSpeedLineTitle.show();
    };
    //隐藏上下行流量箭头
    line.hideSpeedLineTitle = function () {
        if (this.upSpeedLineTitle) this.upSpeedLineTitle.hide();
        if (this.downSpeedLineTitle) this.downSpeedLineTitle.hide();
    };

    //删除流量标题
    line._removeSpeedLineTitle = function () {
        if (this.upSpeedLineTitle) {
            this.upSpeedLineTitle.remove();
            this.upSpeedLineTitle = null;
        }
        if (this.downSpeedLineTitle) {
            this.downSpeedLineTitle.remove();
            this.downSpeedLineTitle = null;
        }
    }
    //画流量标题
    line._drawSpeedLineTitle = function () {
        //上行title
        if (!line.upSpeedLineTitle || !line.upSpeedLineTitle.id) {
            this.upSpeedLineTitle = this.paper.text(0, 0, "");
            this.upSpeedLineTitle.refresh = function (text) {
                var lp = line.line.getPos(), arrowl = " →", arrowr = "← ";
                var fromX = lp.x1, fromY = lp.y1, toX = lp.x2, toY = lp.y2;
                var p = line._getSpeedPoint(fromX, fromY, toX, toY);
                var angle = Raphael.angle(fromX, fromY, toX, toY);//得到两点之间的角度
                if (text && text.toLowerCase().indexOf("mbps") == -1 && text.toLowerCase().indexOf("kbps") == -1) {
                    var index = text.indexOf("bps");
                    if (index != -1) {	//bps，换算成Kbps
                        var val = text.substr(0, index);
                        val /= 1000;
                        text = val + text.substr(index);
                    }
                }
                text = (text) ? arrowr + text : this.attr("text");
                if (p.x1 < p.x3) {
                    var titleX = p.x3, titleY = p.y3;
                    angle += 180;
                    if (text.indexOf(arrowr) != -1 && text.indexOf(arrowl) == -1) {//由->变为<-
                        text = text.replace(arrowr, "");
                        text = (text) ? text + arrowl : this.attr("text");
                    }
                } else {
                    var titleX = p.x1, titleY = p.y1;
                    if (text.indexOf(arrowl) != -1 && text.indexOf(arrowr) == -1) {//由<-变为->
                        text = text.replace(arrowl, "");
                        text = (text) ? arrowr + text : this.attr("text");
                    }
                }
                var textVal = text;
                text = text.toLowerCase();
                if (text.indexOf("mbps") != -1) {
                    text = text.replace("mbps", " M/s");
                } else if (text.indexOf(" mbps") != -1) {
                    text = text.replace(" mbps", " M/s");
                } else if (text.indexOf("kbps") != -1) {
                    text = text.replace("kbps", " K/s");
                } else if (text.indexOf(" kbps") != -1) {
                    text = text.replace(" kbps", " K/s");
                } else if (text.indexOf("m/s") != -1) {
                    text = text.replace("m/s", "M/s");
                } else if (text.indexOf("k/s") != -1) {
                    text = text.replace("k/s", "K/s");
                } else if (text.indexOf("bps")) {	//前台不显示bps，换算成Kbps
                    text = text.replace("bps", " K/s");
                }
                this.attr({
                    x: titleX,
                    y: titleY,
                    text: text,
                    fill: ctx.cfg.topo.fontColor,
                    "font-size": ctx.cfg.topo.fontSize
                });
                //旋转角度
                this.transform("r" + angle + "," + titleX + "," + titleY);
            }
            this.upSpeedLineTitle.refresh(line.d.upSpeed);
        }

        //下行title
        if (!line.downSpeedLineTitle || !line.downSpeedLineTitle.id) {
            this.downSpeedLineTitle = this.paper.text(0, 0, "");
            this.downSpeedLineTitle.refresh = function (text) {
                var lp = line.line.getPos(), arrowl = " →", arrowr = "← ";
                var fromX = lp.x1, fromY = lp.y1, toX = lp.x2, toY = lp.y2;
                var p = line._getSpeedPoint(fromX, fromY, toX, toY);
                var angle = Raphael.angle(fromX, fromY, toX, toY);//得到两点之间的角度
                if (text && text.toLowerCase().indexOf("mbps") == -1 && text.toLowerCase().indexOf("kbps") == -1) {
                    var index = text.indexOf("bps");
                    if (index != -1) {	//bps，换算成Kbps
                        var val = text.substr(0, index);
                        val /= 1000;
                        text = val + text.substr(index);
                    }
                }
                text = (text) ? text + arrowl : this.attr("text");
                if (p.x2 < p.x4) {
                    var titleX = p.x4, titleY = p.y4;
                    angle += 180;
                    if (text.indexOf(arrowl) != -1 && text.indexOf(arrowr) == -1) {//由<-变为->
                        text = text.replace(arrowl, "");
                        text = (text) ? arrowr + text : this.attr("text");
                    }
                } else {
                    var titleX = p.x2, titleY = p.y2;
                    if (text.indexOf(arrowr) != -1 && text.indexOf(arrowl) == -1) {//由->变为<-
                        text = text.replace(arrowr, "");
                        text = (text) ? text + arrowl : this.attr("text");
                    }
                }
                text = text.toLowerCase();
                if (text.indexOf("mbps") != -1) {
                    text = text.replace("mbps", " M/s");
                } else if (text.indexOf(" mbps") != -1) {
                    text = text.replace(" mbps", " M/s");
                } else if (text.indexOf("kbps") != -1) {
                    text = text.replace("kbps", " K/s");
                } else if (text.indexOf(" kbps") != -1) {
                    text = text.replace(" kbps", " K/s");
                } else if (text.indexOf("m/s") != -1) {
                    text = text.replace("m/s", "M/s");
                } else if (text.indexOf("k/s") != -1) {
                    text = text.replace("k/s", "K/s");
                } else if (text.indexOf("bps")) {	//前台不显示bps，换算成Kbps
                    text = text.replace("bps", " K/s");
                }
                this.attr({
                    x: titleX,
                    y: titleY,
                    text: text,
                    fill: ctx.cfg.topo.fontColor,
                    "font-size": ctx.cfg.topo.fontSize
                });
                //旋转角度
                this.transform("r" + angle + "," + titleX + "," + titleY);
            }
            this.downSpeedLineTitle.refresh(line.d.downSpeed);
        }
    };

    //获取链路外经过(x,y)且距离d的点坐标
    line._getNewSpeedPoint = function (x, y, k, d) {
        var x1, x2, y1, y2;
        if (k) {
            if (k != 0) {
                var b = y - k * x;
                //垂直(x,y)点的直线方程y=k1x+b1
                var k1 = -1 / k;
                var b1 = y - k1 * x;
                //垂直链路通过(x,y)点且距离d的点坐标
                x1 = (d * Math.sqrt(k * k + 1) + b1 - b) / (k - k1);
                y1 = k1 * x1 + b1;
                x2 = (-d * Math.sqrt(k * k + 1) + b1 - b) / (k - k1);
                y2 = k1 * x2 + b1;
            } else {	//垂直y轴
                x1 = x2 = x, y1 = y + d, y2 = y - d;
            }
        } else {	//垂直x轴，不存在斜率
            x1 = x - d, x2 = x + d, y1 = y2 = y;
        }
        return {"x1": x1, "y1": y1, "x2": x2, "y2": y2};
    },
        //根据链路两端坐标求某点的垂直对称，距离d的点坐标
        line._getSpeedPoint = function (fromX, fromY, toX, toY) {
            var distance = 10;	//到链路的距离
            var centerX = (fromX + toX) / 2, centerY = (fromY + toY) / 2;
            var upX = ((((fromX + centerX) / 2 + centerX) / 2 + centerX) / 2 + centerX) / 2,
                upY = ((((fromY + centerY) / 2 + centerY) / 2 + centerY) / 2 + centerY) / 2;
            var downX = ((((centerX + toX) / 2 + centerX) / 2 + centerX) / 2 + centerX) / 2,
                downY = ((((centerY + toY) / 2 + centerY) / 2 + centerY) / 2 + centerY) / 2;

            var length = Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2));
            if (length > 50) length = 50;
            if (length < 10) length = 10;

            var result = {};
            if (fromX == toX) {	//垂直X轴，则平移x点d,y不变
                result = {
                    "x1": upX - distance,
                    "y1": upY,
                    "x2": upX + distance,
                    "y2": upY,
                    "x3": downX - distance,
                    "y3": downY,
                    "x4": downX + distance,
                    "y4": downY
                };
            } else if (fromY == toY) {	//垂直Y轴，则平移y点d,x不变
                line.slope = 0;	//斜率
                result = {
                    "x1": upX,
                    "y1": upY - distance,
                    "x2": upX,
                    "y2": upY + distance,
                    "x3": downX,
                    "y3": downY - distance,
                    "x4": downX,
                    "y4": downY + distance
                }
            } else {//链路直线方程y=kx+b => kx+(-1)*y+b=0
                var k = (toY - fromY) / (toX - fromX);
                line.slope = k;	//链路斜率
                //计算上下行流量连线坐标点
                result = this._getNewSpeedPoint(upX, upY, k, distance);
                var result1 = this._getNewSpeedPoint(downX, downY, k, distance);
                result["x3"] = result1.x1;
                result["y3"] = result1.y1;
                result["x4"] = result1.x2;
                result["y4"] = result1.y2;
            }
            return result;
        },
        line.addFlow = function () {
            if (!this.addFlowCount) {
                this.addFlowCount = 1;
            } else {
                return;
            }
            if (!this.flow) {
                if (this instanceof BendConnect) {
                } else {
                    new TopoFlow({
                        holder: ctx.$holder,
                        paper: ctx.paper,
                        onLoad: function () {
                            this.setLine(line.line.line);
                            this.update();
                            this.startPlay();
                        }
                    });
                }
            }
        };
    line.getValue = function () {
        var attr = {
            stroke: this.line.line.attr("stroke"),
            style: this.line.line.attr("stroke-dasharray"),
            width: this.line.line.attr("stroke-width")
        };
        return {
            fromType: this.fromNode.linktype,
            toType: this.toNode.linktype,
            from: ctx.getRealId(this.fromNode.d.id),
            to: ctx.getRealId(this.toNode.d.id),
            id: ctx.getRealId(this.d.id),
            attr: JSON.stringify(attr)
        };
    };
    line.setValue = function (info) {
        if (info && info.attr && this.d.type == "line") {
            var attr = JSON.parse(info.attr);
            this.line.setColor(attr.stroke);
            this.line.setStyle(attr.style);
            this.line.setWeight(attr.weight);
        }
    };
    line.isVisible = function () {
        return this.fromNode.d.visible && this.toNode.d.visible;
    };
    line.getLabel = function (field) {
        var retn = "";
        switch (field) {
            case "type":
                //retn = "类型";
                retn = "";
                break;
            case "note":
                //retn = "备注";
                retn = "";
                break;
            case "upSpeed":
                retn = "↑";	//上行流量
                break;
            case "downSpeed":
                retn = "↓";	//下行流量
                break;
        }
        return retn;
    };
    line.setTitle = function (field, force) {
        //三层拓扑不操作
        if (ctx.id == 1) return;
        //设置为无标注并且不是强制显示标注，不继续操作
        if (!ctx.cfg.link.hasTag && !force) return;
        if (this instanceof BendConnect && "flow" == field && this.d.type == "link") {
            //显示链路上下行流量
            if (this.d.upSpeed) {
                this.line.args.upFlag = "→";
                this.line.setTitle(this.d.upSpeed, "up");
            }
            if (this.d.downSpeed) {
                this.line.args.downFlag = "←";
                this.line.setTitle(this.d.downSpeed, "down");
            }
            this.setTextAttr({
                fill: ctx.cfg.topo.fontColor,
                "font-size": ctx.cfg.topo.fontSize
            });
            return;
        } else {
            if (this.d.multiNumber > 1 || this.d.type == "line" || line.d.visible == false) return;	//多链路暂时不显示任何提示
            //显示链路上下行流量
            if ("flow" == field && this.d.type == "link") {
                //设置上下行流量值
                this.removeText();
                if (this.upSpeedLineTitle && this.downSpeedLineTitle) {
                    this.upSpeedLineTitle.refresh(this.d.upSpeed);
                    this.downSpeedLineTitle.refresh(this.d.downSpeed);
                } else {
                    this._drawSpeedLineTitle();
                }
                return;
            } else if ("note" == field) {	//显示链路备注,删除链路流量线段
                this._removeSpeedLineTitle();
            }
            //组装文本
            this.titleField = field || ctx.cfg.link.tagField;
            var txt = "";
            if (this.titleField instanceof Array) {
                for (var i = 0; i < this.titleField.length; ++i) {
                    var tmpTxt = this.d[this.titleField[i]];
                    txt += this.getLabel(this.titleField[i]) + (tmpTxt || "") + " ";
                    //txt += this.getLabel(this.titleField[i]) + ":" + (tmpTxt || "--") + " ";
                }
            } else {
                txt = this.getLabel(this.titleField) + (this.d[this.titleField] || "");
                //txt = this.getLabel(this.titleField) + ":" + (this.d[this.titleField] || "--");
            }
            if (this instanceof BendConnect) {
                this.line.args.upFlag = null;
                this.line.args.downFlag = null;
                this.removeText();
            }
            this.setText(txt).attr({
                fill: ctx.cfg.topo.fontColor,
                "font-size": ctx.cfg.topo.fontSize
            });
        }
    };
    line.init();
    return line;
};
Topo.prototype.timeAfter = function (cb, dur, id, context) {
    var ctx = this;
    if (!ctx[id]) {
        ctx[id] = setTimeout(function () {
            cb.apply(context || ctx);
            ctx[id] = null;
        }, dur || 500);
    } else {
        clearInterval(ctx[id]);
        ctx[id] = null;
    }
};
Topo.prototype.addAllLinesArrow = function () {
    $.each(this.els, function (key, line) {
        if (key.indexOf("link") >= 0 && line.d.visible) {
            //多链路不显示链路下行方向
            if (!line.isMulti) line.addArrow();
        }
    });
    this.cfg.showLineArrow = true;
};
Topo.prototype.removeAllLinesArrow = function () {
    $.each(this.els, function (key, line) {
        if (key.indexOf("link") >= 0) {
            line.removeArrow();
        }
    });
    this.cfg.showLineArrow = false;
};
Topo.prototype.removeAllLinesText = function (delSpeedLineTitle) {
    $.each(this.els, function (key, line) {
        if (key.indexOf("link") >= 0) {
            line.removeText();
            if (delSpeedLineTitle == true) {
                //删除链路流量线段
                line._removeSpeedLineTitle();
            }
        }
    });
    this.cfg.showLineTitle = false;
};
Topo.prototype.showAllLinesArrow = function () {
    $.each(this.els, function (key, line) {
        if (key.indexOf("link") >= 0) {
            line.setText();
        }
    });
    this.cfg.showLineTitle = true;
};
Topo.prototype.getValue = function () {
    var ctx = this;
    var othersTmp = this.drawUtil.getValue();
    var values = [];
    $.each(othersTmp, function (idx, val) {
        values.push({
            id: val.id,
            attr: JSON.stringify(val),
            subTopoId: ctx.id
        });
    });
    var vals = {
        id: this.id,
        links: [],
        groups: [],
        nodes: [],
        others: values,
        newNodes: []
    };
    $.each(this.els, function (key, el) {
        if (key.indexOf("group") >= 0) {
            var v = el.getValue();
            v.subTopoId = ctx.id;
            vals.groups.push(v);
        } else if (key.indexOf("node") >= 0) {
            var v = el.getValue();
            v.subTopoId = ctx.id;
            if (v.id <= 0) {
                vals.newNodes.push(v);
            }
            vals.nodes.push(v);
        } else if (key.indexOf("link") >= 0) {
            var v = el.getValue();
            v.subTopoId = ctx.id;
            vals.links.push(v);
        }
    });
    return vals;
};
Topo.prototype.save = function (callBack) {
    if (!this.finishedFlag)
        return;
    var ctx = this;
    var vals = this.getValue();
    if (this.id < 0)
        return;
    if (!oc.topo.topoIsRunning) {
        // 保存
        oc.util.ajax({
            url: oc.resource.getUrl("topo/save.htm"),
            data: {
                groups: JSON.stringify(vals.groups),
                nodes: JSON.stringify(vals.nodes),
                links: JSON.stringify(vals.links),
                rgroups: JSON.stringify(this.deleteItems.groups),
                rnodes: JSON.stringify(this.deleteItems.nodes),
                rlinks: JSON.stringify(this.deleteItems.links),
                rothers: this.deleteItems.others.join(","),
                others: JSON.stringify(vals.others),
                topoId: this.id
            },
            success: function (info) {
                if (info.status == 200) {
                    alert(info.msg);
                    if (callBack) {
                        callBack.call(ctx);
                    }
                    ctx.setDirty(false);
                } else {
                    alert(info.msg, "warning");
                }
            }
        });
        // 保存拓扑内容和背景图片
        /*BUG #44110 子拓扑顺序会自动跳乱 huangping 2017/8/22 start*/
        // var parentId=0;
        // if(ctx.topoType=="机房拓扑"){
        // 	parentId=2;
        // }
        /*BUG #44110 子拓扑顺序会自动跳乱 huangping 2017/8/22 end*/
        $.post(oc.resource.getUrl("topo/updateSubTopo.htm"), {
            bgsrc: ctx.bgsrc || "",
            id: ctx.id
            // parentId:parentId
        }, function () {
        }, "json");
    } else if (oc.topo.topoIsRunning) {
        alert("拓扑发现正在进行,暂时不能进行保存操作", "warning");
    }
};
Topo.prototype.menuEvent = function () {
    var ctx = this;
    // 初始化布局器
    this.els = this.els || {};
    // 监听更换拓扑图事件
    eve.on("topo.loadsubtopo", function (id, forceLoad) {
        // forceLoad
        if (ctx._dirty && !forceLoad) {
            $.messager.confirm("温馨提示", "拓扑已经被修改，是否保存?", function (r) {
                if (r) {
                    ctx.save(function () {
                        ctx.loadData(oc.resource.getUrl("topo/getSubTopo/" + id
                            + ".htm"));
                    });
                } else {
                    ctx.loadData(oc.resource.getUrl("topo/getSubTopo/" + id
                        + ".htm"));
                }
            });
        } else {
            ctx.loadData(oc.resource.getUrl("topo/getSubTopo/" + id + ".htm"));
        }
    });
};
Topo.prototype.putEl = function (key, el) {
    this.els = this.els || {};
    this.els[key] = el;
};
Topo.prototype.getEl = function (key) {
    this.els = this.els || {};
    return this.els[key];
};
Topo.prototype.removeEl = function (key) {
    this.els = this.els || {};
    var el = this.getEl(key);
    if (el) {
        el.remove(true);
        delete this.els[key];
    }
};
Topo.prototype.reset = function () {
    var ctx = this;
    if (this.drawUtil) {
        this.drawUtil.reset();
    }
    this.subtopoNodes = [];
    if (this.els) {
        $.each(this.els, function (idx, el) {
            try {
                ctx.removeEl(el.d.id);
            } catch (e) {
                console.log(e);
            }
        });
        //this.paper.clear();
    }
    this.id = -1;
    delete ctx.deleteItems.groups;
    delete ctx.deleteItems.links;
    delete ctx.deleteItems.nodes;
    delete ctx.deleteItems.others;
    this.deleteItems = {
        groups: [],
        links: [],
        nodes: [],
        others: []
    };
    this.highDrag = false;
    this.firstDrawNode = null;
    this.svgDrag = false;
    this.finishedFlag = false;
    // 清除定时器
    if (this.freshStateTimeId) {
        clearInterval(this.freshStateTimeId);
    }
    eve("topo.graphreset");
};
// 更新视图
Topo.prototype.refresh = function () {
    this.reset();
    //取消连线
    eve("doc_key_down", this, {
        keyCode: 27
    });
    if (this.url) {
        this.loadData(this.url);
    }
};
Topo.prototype.setEditable = function (flag) {
    this.editable = flag;
    if (this.drawUtil) {
        this.drawUtil.setEditable(flag);
    }
    if (this.svgEvent) {
        this.svgEvent.setEditable(flag);
    }
};
// 显示隐藏列表
Topo.prototype.showHideList = function (list) {
    for (var i = 0; i < list.length; ++i) {
        var item = list[i];
        item.show();
    }
};
Topo.prototype.addElement = function (info) {
    if (info) {
        if (info.nodes) {// 增加节点
            for (var i = 0; i < info.nodes.length; i++) {
                this.drawNode(info.nodes[i]);
            }
        }
        if (info.links) {// 增加链路
            for (var i = 0; i < info.links.length; i++) {
                this.drawLine(info.links[i]);
            }
            // 刷新链路数据
            this.refreshLinkData();
        }
        this.refreshState();
    }
};
Topo.prototype.drawNode = function (node) {
    node.rawId = node.id;
    node.id = "node" + node.id;
    this.tmpRefNodeId = node.id;// 替换这儿的tmpRefNodeId
    var el = this.getNode({
        src: node.icon,
        x: node.x,
        y: node.y,
        w: node.iconWidth,
        h: node.iconHeight,
        iw: node.iconWidth,
        ih: node.iconHeight,
        type: "node",
        data: node
    });
    el.setState("normal");
    if (el.d.attr) {
        el.d.showName = el.d.attr.name;
    }
    el.icon.hide();
    if (!node.visible) {// 如果不可视，就隐藏
        el.hide();
    }
    if (node.type == "subtopo") {
        this.subtopoNodes.push(el);
    }
},
    Topo.prototype.drawLine = function (link) {
        //相同的节点不能有连线
        if (link.from == link.to && link.fromType == link.toType) return;
        link.rawId = link.id;
        link.id = "link" + link.id;
        // 查找链路所连接的节点
        var conA = null, conB = null;
        if (link.fromType == "node") {
            conA = this.getEl("node" + link.from);
        } else if (link.fromType == "other") {
            conA = this.drawUtil.els[link.from];
        } else if (link.fromType == "group") {
            conA = this.getEl("group" + link.from);
            conA.d.visible = true;
        }
        if (link.toType == "node") {
            conB = this.getEl("node" + link.to);
        } else if (link.toType == "other") {
            conB = this.drawUtil.els[link.to];
        } else if (link.toType == "group") {
            conB = this.getEl("group" + link.to);
            conB.d.visible = true;
        }
        // 必须两端都
        if (conA && conB) {
            // 连接
            var line = null;
            if (!link.direction) {
                line = this.connect({
                    conA: conB,
                    conB: conA,
                    data: link
                });
            } else {
                line = this.connect({
                    conA: conA,
                    conB: conB,
                    data: link
                });
            }
            line.d.visible = true;
            if (!conA.d.visible || !conB.d.visible) {
                line.hide();
            }
            if (this.id == 1) {
                line.setState("notlink");
            } else {
                if (line.d && line.d.type == "link") {
                    if (line.d.instanceId) {
                        line.setState();
                    } else {
                        line.setState("not_monitored");
                    }
                } else {
                    line.setState("notlink");
                }
            }
            if ("dash" == link.type) {
                line.line.line.attr("stroke-dasharray", "- ");
                var core = null;
                if (conA.d.type == "SERVER" || conA.d.type == "HOST") {
                    core = conB;
                } else {
                    core = conA;
                }
                core.hasNoDelete = true;
            }
            line.setValue(link);
        }
    };
//多链路
Topo.prototype.multilink = {
    //删除多链路数量信息
    remove: function (line) {
        if (line.multiRect && line.multiNumber) {
            line.multiRect.remove();
            line.multiNumber.remove();
        }
    },
    //添加连线数量
    addNumber: function (key) {
        if (oc.topo.multilink.number[key]) {	//存在则+1条
            oc.topo.multilink.number[key]++;
        } else {	//不存在，初始化=1
            oc.topo.multilink.number[key] = 1;
        }
    },
    //获取链路已有的数量
    getLine: function (ctx, lineKey) {
        var line;
        for (var key in ctx.els) {
            if (key.indexOf("link") >= 0) {
                var el = ctx.els[key];
                var fromId = el.fromNode.d.id.substring(4);
                var toId = el.toNode.d.id.substring(4);
                var from_to = fromId + "_" + toId, to_from = toId + "_" + fromId;

                if (lineKey == from_to || lineKey == to_from) {	//找到已有的连线
                    line = el;
                    break;
                }
            }
        }
        return line;
    },
    //新画连线
    newDrawNumber: function (ctx, fromNode, toNode, newLink) {
        //1.获取已有的链路
        var lineKey = fromNode.d.id.substring(4) + "_" + toNode.d.id.substring(4);
        var line = this.getLine(ctx, lineKey);
        if (line && line.d.multiNumber > 1) {	//以前已经有连线
            //找到以前的连线，数量+1
            ctx.multilink.remove(line);
            line.d.multiNumber++;
            ctx.multilink.drawNumber(ctx, line);
            ctx.multilink.show(line);
            //隐藏上下行流量
            line._removeSpeedLineTitle();
        } else {	//新画的连线
            var con = ctx.connect({
                conA: fromNode,
                conB: toNode
            });
            con.d = newLink;
            con.d.rawId = newLink.id;
            con.d.id = "link" + newLink.id;
            con.d.type = "line";
            con.d.visible = true;
            con.setState("notlink");
            //新画的链路加入链路数据中(下次画线才能获取到已画的连线)
            ctx.putEl(con.d.id, con);
        }
    },
    //画数字
    drawNumber: function (ctx, line) {
        if (line.d.multiNumber && line.d.multiNumber < 3) return;
        //1.获取到两端的点坐标
        var fromPoint = line.fromNode.getConnectPoint();
        var toPoint = line.toNode.getConnectPoint();
        var number_x = (fromPoint.x + toPoint.x) / 2;
        var number_y = (fromPoint.y + toPoint.y) / 2;
        var number_rect_width = 12, number_rect_height = 20;
        //2.画出数字矩形框
        var number = line.d.multiNumber;
        number = String(number);
        if (number > 1) {
            var numberLength = number.length;
            switch (numberLength) {
                case 1:
                    number_rect_width = 12;
                    break;
                case 2:
                    number_rect_width = 20;
                    break;
                case 3:
                    number_rect_width = 25;
                    break;
                case 4:
                    number_rect_width = 35;
                    break;
            }
            line.multiRect = line.paper.rect(number_x - (number_rect_width / 2), number_y - (number_rect_height / 2), number_rect_width, number_rect_height).attr({
                "fill": "#FFFFFF",
                "stroke": "#FFFFFF",
                "stroke-width": 1
            });
            //3.数量>1,则画出数字
            line.multiNumber = line.paper.text(number_x, number_y, number);

            //4.添加多链路标记
            line.isMulti = true;

            //5.先隐藏，待刷新完状态后再显示
            if ("link" == line.d.type) this.hide(line);
            line.showMultiLink = function (line) {
                line.multiNumber.show();
                line.multiRect.show();
            };
        }
    },
    //隐藏多链路数字和矩形框
    hide: function (line) {
        if (line && line.multiNumber && line.multiRect) {
            line.multiNumber.hide();
            line.multiRect.hide();
        }
    },
    //显示多链路数字和矩形框
    show: function (line) {
        if (line && line.multiNumber && line.multiRect) {
            line.multiNumber.show();
            line.multiRect.show();
        }
    }
};
Topo.prototype.draw = function (data) {
    this.bgsrcObj = data.bgsrc;
    var ctx = this;
    // 清空当前画布
    if (ctx.args.showBgIm == null) {
        this.reset();
    }
    ctx.id = data.id;

    // 加载拓扑属性
    var subTopoId = (data.id == 10) ? 0 : data.id;	//特殊处理id=10，大屏二层拓扑不知道为什么传递的就是10(有点无奈……)
    $.post(oc.resource.getUrl("topo/subtopo/getAttr.htm"), {
        id: subTopoId
    }, function (info) {
        ctx.setAttr(info);
    }, "json");
    ctx.id = data.id;
    ctx.setAttr(data.pageAttr);
    // 是否使用默认布局
    var useDefaultLayout = true;
    // 先画普通节点
    var _y = ctx.height - 100;
    var _idx = 0;
    $.each(data.nodes, function (idx, node) {
        if (node.x != 0 || node.y != 0) {
            useDefaultLayout = false;
        }
        if (node.x == 0 && node.y == 0) {
            node.x = 40 + ((_idx++) % 8) * 40;
            node.y = _y;
        }
        ctx.drawNode(node);
    });
    // 组节点
    $.each(data.groups, function (idx, group) {
        group.rawId = group.id;
        group.id = "group" + group.id;
        group.visible = true;
        var tmpGroup = ctx.getNode({
            x: group.x,
            y: group.y,
            w: group.width,
            h: group.height,
            type: "group",
            data: group
        });
        if (group.x != 0 || group.y != 0) {
            useDefaultLayout = false;
        }
        // 添加子节点
        $.each(group.children, function (cidx, nodeId) {
            var el = ctx.getEl("node" + nodeId);
            if (el) {
                tmpGroup.add(el, el.d.rx, el.d.ry);
                el.toFront();
            }
        });
    });
    //其他
    var others = [];
    $.each(data.others || [], function (idx, other) {
        var attr = JSON.parse(other.attr);
        attr.id = other.id;
        attr.subTopoId = other.subTopoId;
        attr.zIndex = other.zIndex;
        others.push(attr);
    });

    if (this.drawUtil) {
        this.drawUtil.setValue(others);
    }
    // 链路
    this.lineMap = {};
    $.each(data.links, function (idx, link) {
        //画链路
        ctx.drawLine(link);
    });
    if (this.mode == "normal") {
        // 初始化配置
        ctx.loadCfg();
        // 刷新链路数据
        this.refreshLinkData();
    }
    // 拓扑加载完成
    this.finishedFlag = true;
    // 刷新状态
    if (useDefaultLayout) {
        eve("topo.layout", ctx, "star", {
            callBack: function () {
                ctx.save();
            }
        });
    }
    //绘图完成调用回调函数
    if (this.args.onDrawed) {
        this.args.onDrawed.call(this);
    }
};
// 刷新链路数据
Topo.prototype.refreshLinkData = function () {
    var ctx = this;
    // 过滤出有资源实例的
    var currentEls = {}, hasEl = false, ids = [];
    $.each(this.els, function (key, el) {
        try {
            if (el.d && el.d.instanceId && el.d.id.indexOf("link") >= 0) {
                if (currentEls[el.d.instanceId]) {
                    currentEls[el.d.instanceId].push(el);
                } else {
                    currentEls[el.d.instanceId] = [el];
                    ids.push(el.d.instanceId);
                }
                hasEl = true;
            }
        } catch (e) {
            console.log(e);
        }
    });
    $.post(oc.resource.getUrl("topo/refreshLinkDataByIds.htm"), {
        ids: ids.join(",")
    }, function (info) {
        for (var i = 0; i < info.length; ++i) {
            var item = info[i];
            var els = currentEls[item.instanceId];
            if (els) {
                for (var j = 0; j < els.length; j++) {
                    var el = els[j];
                    el.d = $.extend(el.d, item);
                    el.setTitle();
                }
            }
        }
    }, "json");
};
// 刷新状态
Topo.prototype.refreshState = function () {
    var ctx = this;
    var nodeIds = [], linkIds = [], nodeInstIds = [], linkInstIds = [], stateEls = {nodes: {}, links: {}},
        currentEls = {}, rawNodeIds = [], rawNodeIdRelation = {}, hasEl = false;
    // 过滤出有资源实例的
    $.each(this.els, function (key, el) {
        try {
            if (el.d && el.d.instanceId) {
                if (el.d.id.indexOf("node") >= 0) {
                    nodeIds.push(el.d.rawId);
                    nodeInstIds.push(el.d.instanceId);
                    stateEls.nodes[el.d.rawId] = el;
                } else if (el.d.id.indexOf("link") >= 0) {
                    linkIds.push(el.d.rawId);
                    stateEls.links[el.d.rawId] = el;
                    linkInstIds.push(el.d.instanceId);
                }
                if (currentEls[el.d.instanceId]) {
                    currentEls[el.d.instanceId].push(el);
                } else {
                    currentEls[el.d.instanceId] = [el];
                }
                hasEl = true;
            }
            if (el.d && el.d.rawId && el.d.id.indexOf("node") >= 0) {
                rawNodeIds.push(el.d.rawId);
                rawNodeIdRelation[el.d.rawId] = el;
            }
        } catch (e) {
            console.log(e);
        }
    });

    // 当前全局关于颜色指标的配置
    var cfg = this.cfg;
    var linkType = "device";
    var nodeType = "device";
    if (cfg.link) {
        linkType = cfg.link.colorWarning;
    }
    if (cfg.device) {
        nodeType = cfg.device.colorWarning;
    }
    //刷新其他节点状态
    this.refreshOthersState(linkType, nodeType);

    if (!hasEl) return;

    function _setState(state, cont) {
        var el = cont[state.id || state.instanceId];
        if (el && el.setState) {
            if (el.d.instanceId) {
                //多链路特殊处理状态,并显示出来
                if (el.d.multiNumber && el.d.multiNumber > 1 && state.state == "not_monitored") {
                    el.setLifeState("not_monitored");
                } else {
                    if (state.state == "not_monitored") {
                        el.setLifeState("not_monitored");
                    } else {
                        el.setState(state.state);
                        if (el.d.multiNumber) {
                            if (el.d.multiNumber > 2) {
                                el.showMultiLink(el);	//显示多链路数字
                            } else if (el.d.multiNumber == 1 && ctx.cfg.link.showDownDirection) {
                                el._drawSpeedLineTitle();	//根据全局设置显示链路流量
                            }
                        }
                    }
                }
            }
        }
        ;
    }

    //服务器无响应警告提示框
    function _openWarnDialog() {
        var hintDlg = $("<div/>");
        hintDlg.append($("<div/>").addClass("messager-icon messager-warning"))
            .append($("<div/>").css('top', '23px').addClass("oc-notice").html("  服务器无响应!"));
        ctx.startTime = new Date().getTime();
        ctx.topoNetWarnDialog = hintDlg.dialog({
            title: '警告',
            width: '250px',
            height: '110px',
            onOpen: function () {
                setTimeout(function () {
                    if (ctx.topoNetWarnDialog) {
                        ctx.topoNetWarnDialog.dialog("close");
                        delete ctx.topoNetWarnDialog;	//删除属性
                    }
                }, 2000);
            }
        });
    }

    //从服务器获取状态
    function _getState(linkType, nodeType) {
        oc.util.ajax({
            url: oc.resource.getUrl("topo/refreshState.htm"),
            data: {
                nodeIds: nodeIds.join(","),
                linkIds: linkIds.join(","),
                linkMetricId: linkType,
                nodeMetricId: nodeType
            },
            failed: function () {
                _openWarnDialog();
            },
            error: function () {
                _openWarnDialog();
            },
            success: function (info) {
                if (info.status == 200) {
                    for (var i = 0; i < info.nodes.length; i++) {
                        _setState(info.nodes[i], stateEls.nodes);
                    }
                    var linkAlarm = [];	//告警的链路
                    for (var i = 0; i < info.links.length; i++) {
                        var state = info.links[i];
                        _setState(state, stateEls.links);
                        //处理链路告警
                        if (state && state.state) {
                            var status = state.state;
                            if (status == "SERIOUS" || status == "WARN" || status == "CRITICAL" || status == "CRITICAL_NOTHING" || status == "severity" || status == "danger" || status == "warning" || status == "disabled") {
                                state.instanceId = stateEls.links[state.id].d.instanceId;
                                linkAlarm.push(state);
                            }
                        }
                    }
                    if (linkAlarm.length > 0) {
                        if (!oc.topo.topo) oc.topo.topo = {};
                        oc.topo.topo.linkAlarm = linkAlarm;
                    }
                }
            }
        });
    }

    // 获取生命状态
    $.post(oc.resource.getUrl("topo/refreshLifeState.htm"), {
        ids: nodeInstIds.concat(linkInstIds).join(",")
    }, function (result) {
        var info = result.data;
        for (var i = 0; i < info.length; ++i) {
            var item = info[i];
            var els = currentEls[item.instanceId];
            if (els) {
                for (var j = 0; j < els.length; j++) {
                    var el = els[j];
                    el.d.lifeState = item.lifeState;
                    if (el.d.type != "link") {
                        if (el.setLifeState) {
                            el.setLifeState(el.d.lifeState);
                        } else {
                            el.setState(el.d.lifeState);
                        }
                        if (item.showName) {
                            el.d.showName = item.showName;
                        }
                    } else {
                        el.setState();
                    }
                    el.setTitle();
                }
            }
        }
        _getState(linkType, nodeType);
        eve("topo.draw.finished", this);
    }, "json");
    // 获取厂商型号
    $.post(oc.resource.getUrl("topo/refreshVendorName.htm"), {
        ids: rawNodeIds.join(",")
    }, function (info) {
        for (var i = 0; i < info.length; ++i) {
            var t = info[i];
            var el = rawNodeIdRelation[t.id];
            if (el) {
                el.d.vendorName = t.vendorName;
                el.d.series = t.series;
                el.setTitle();
            }
        }
    }, "json");
    if (this.freshStateTimeId) {
        clearInterval(this.freshStateTimeId);
    }
    if (cfg.topo && cfg.topo.freshType == "auto" && cfg.topo.refreshTime) {// 如果是自动刷新状态
        ctx.freshStateTimeId = setInterval(function () {
            _getState(linkType, nodeType);
            /*BUG #47794 亳州医院项目--拓扑图自动刷新-告警图元没有警示颜色出现 huangping 2017/11/10 start*/
            ctx.refreshOthersState(linkType, nodeType);
            /*BUG #47794 亳州医院项目--拓扑图自动刷新-告警图元没有警示颜色出现 huangping 2017/11/10 end*/
        }, cfg.topo.refreshTime);
        var tasks = oc.index.indexLayout.data("tasks");
        if (tasks && tasks.length > 0) {
            oc.index.indexLayout.data("tasks").push(ctx.freshStateTimeId);
        } else {
            tasks = new Array();
            tasks.push(ctx.freshStateTimeId);
            oc.index.indexLayout.data("tasks", tasks);
        }
    }
};

//刷新其他节点状态
Topo.prototype.refreshOthersState = function (linkType, nodeType) {
    var ctx = this;
    var subTopoId = (this.id == 10) ? 0 : this.id;	//特殊处理id=10，大屏二层拓扑不知道为什么传递的就是10(有点无奈……)
    oc.util.ajax({
        url: oc.resource.getUrl("topo/refreshOtherState.htm"),
        data: {
            subTopoId: subTopoId,
            linkMetricId: linkType,
            nodeMetricId: nodeType
        },
        success: function (result) {
            if (result.code == 200) {
                if (result.data != null && result.data.length != 0) {
                    for (var i = 0; i < result.data.length; i++) {
                        var other = result.data[i];
                        var el = ctx.drawUtil.els[other.otherNodeId];
                        el.img.attr("src", oc.resource.getUrl("topo/image/change.htm?path=" + el.src + "&type=" + other.state));
                    }
                }
            } else {
                console.log("其他图元状态刷新错误");
            }
        }
    });

}

//请求加载拓扑数据
Topo.prototype.loadData = function (url, callBack) {
    this.setDirty(false);
    this.finishedFlag = false;
    var ctx = this;
    this.url = url;
    var begin = url.lastIndexOf("/") + 1, end = url.lastIndexOf("\.");
    var topoId = parseInt(url.substr(begin, end - begin));
    var oldEditable = this.editable;
    if (!oldEditable) {
        this.setEditable(true);
    }
    $.post(url, function (data) {
        if (data.msg) {
            alert(data.msg);
        } else if (data.name) {
            data.id = topoId;

            // 加载拓扑属性
            $.post(oc.resource.getUrl("topo/subtopo/getAttr.htm"), {
                id: data.id
            }, function (info) {
                data.pageAttr = info;
                ctx.draw(data);
                if (!oldEditable) {
                    ctx.setEditable(false);
                }
                if (callBack) {
                    callBack();
                }
            }, "json");
        }
    }, function () {
        //加载完成后清空上次画布上存储的值
        ctx.reset();
    }, "json");
    eve("topo.topotip.hide");
};
// 设置配置
Topo.prototype.setCfg = function (cfg) {
    var ctx = this;
    cfg = $.extend(this.cfg, cfg);
    this.cfg = cfg;
    // 更标识
    $.each(this.els, function (key, el) {
        var field = null, isShow = true;
        try {
            if (el.d.id.indexOf("group") == 0) {
                field = cfg.groupTitle;
            } else if (el.d.id.indexOf("node") == 0) {
                field = cfg.device.tagField;
            } else if (el.d.id.indexOf("link") == 0) {
                field = cfg.link.tagField;
                isShow = cfg.link.hasTag;
            }
            if (isShow) {
                el.setTitle(field);
            }
            if (el.text) {
                el.text.attr({
                    "font-size": cfg.topo.fontSize,
                    "fill": cfg.topo.fontColor
                });
            }
        } catch (e) {
            console.log(e);
        }
    });
    // 是否添加流量效果
    if (cfg.effect.flow) {
        ctx.addAllLineFlow();
    }
    // 是否显示线标注
    if (!cfg.link.hasTag) {
        this.removeAllLinesText(true);
    }
    // 刷新状态
    this.refreshState();
};
Topo.prototype.resetChose = function () {
    var els = this.els;
    for (key in els) {
        if (key.indexOf("node") >= 0) {
            var el = els[key];
            if (el && el.getMaster) {
                el.setState("unchoosed");
            }
        }
    }
    this.chosed = [];
};
Topo.prototype.addAllLineFlow = function () {
    $.each(this.els, function (key, line) {
        if (key.indexOf("link") >= 0 && line.d.visible) {
            line.addFlow();
        }
    });
};
//检查p是否和子拓扑点重合
Topo.prototype.checkSubtopoNode = function (p, id) {
    var els = this.subtopoNodes, ctx = this;
    for (key in els) {
        var el = els[key];
        if (Raphael.isPointInsideBBox(el.handler.getBBox(), p.cx, p.cy) && el.d.rawId != id) {
            $.messager.confirm("确认", "是否将选中的资源(共" + this.chosed.length + "项)移动到 (" + el.d.attr.name + ")？", function (r) {
                if (r) {
                    ctx.moveChosed(el.d.attr.subtopoId);
                }
            });
            break;
        }
    }
};
//移动选中的节点到指定的拓扑中去
Topo.prototype.moveChosed = function (topoId) {
    var ctx = this;
    var _tmpItems = [];
    $.each(this.chosed, function (idx, it) {
        _tmpItems.push(it.d);
    });
    oc.topo.clipboard.cut(_tmpItems);
    oc.topo.clipboard.paste(topoId, function () {
        ctx.refresh();
        ctx.resetChose();
    });
};
//获取当前拓扑的最佳尺寸范围
Topo.prototype.getRange = function () {
    var x1 = 1000000, y1 = 1000000, x2 = -10000, y2 = -10000;
    //搜索节点的范围
    var els = this.els;
    for (key in els) {
        var el = els[key];
        if (el && el.getPos) {
            var p = el.getPos();
            if (p.x < x1) x1 = p.x;
            if (p.y < y1) y1 = p.y;
            if (p.x > x2) x2 = p.x;
            if (p.y > y2) y2 = p.y;
        }
    }
    //搜索其他节点
    for (key in this.drawUtil.els) {
        var el = this.drawUtil.els[key];
        if (el && el.getPos) {
            var p = el.getPos();
            if (p.x < x1) x1 = p.x;
            if (p.y < y1) y1 = p.y;
            if (p.x > x2) x2 = p.x;
            if (p.y > y2) y2 = p.y;
        }
    }
    return {
        x: x1 - 50, y: y1 - 50, w: (x2 - x1) + 100, h: (y2 - y1) + 100
    };
};
Topo.prototype.destroy = function () {
    this.menu.destroy();
    this.menu = null;
    this.$svg.remove();
};