function TopoNav(args) {
    this.args = args;
    var ctx = this;
    this.subtopoId = 0;
    $.get(oc.resource.getUrl("resource/module/topo/contextMenu/TopoNav.html"), function (html) {
        ctx.theme = oc.topo.theme;
        oc.util.ajax({
            url: oc.resource.getUrl("topo/subtopo/isTopoRoomEnabled.htm"),
            success: function (result) {
                ctx.topoRoomEnabled = result.enabled;
                ctx.init(html);
            }
        });
    }, "html");
};
TopoNav.prototype = {
    init: function (html) {
        var ctx = this;
        this.root = $(html);
        this.root.appendTo(this.args.parent);
        this.fields = {};
        this.root.find("[data-field]").each(function (idx, dom) {
            var tmp = $(dom);
            ctx.fields[tmp.attr("data-field")] = tmp;
        });
        //结果面板
        this.fields.searchResultPanel.panel("RenderP", {
            closable: true,
            onClose: function () {
                //切换回树面板
                ctx.fields.searchResultPanel.hide();
                ctx.fields.topoAccord.fadeIn();
            }
        });

        //调整关闭按钮位置
        this.fields.searchResultPanel.parent().find(".panel-tool").css({
            height: "24px",
            top: "14px",
            right: "10px"
        });
        if (!this.topoRoomEnabled) {
            this.fields.roomView.remove();
        }
        //初始化子拓扑列表
        var selected = 0;
        if (oc.topo.util.module == "GF") {
            selected = 3;
        }
        this.fields.topoAccord.accordion({
            fit: true,
            width: 220,
            animate: true,
            selected: selected
        });
        //初始化菜单
        this.fields.menu.menu();
        //初始化二层网络拓扑列表
        this._treeCustom(this.fields.twoLevel, 0);
        //初始化三层网络拓扑列表
        this._treeCustom(this.fields.threeLevel, 1);
        //初始化机房视图列表
        if (this.topoRoomEnabled) {
            this._treeCustom(this.fields.roomView, 2);
        }
        //初始化机房视图列表
        this._treeCustom(this.fields.multiplyView, -1);
        //初始化固定图标，使其可拖动
        this.enableDrag(this.fields.panels.find(".icon"));
        this.regEvent();
        //更新背景主题
        this.root.find("img").each(function (idx, dom) {
            var tmp = $(dom);
            var src = tmp.attr("src"), bg = tmp.attr("data-bg");
            if (src) {
                tmp.attr("src", src.replace("default", ctx.theme));
            }
            if (bg) {
                tmp.attr("data-bg", bg.replace("default", ctx.theme));
            }
        });
        //默认隐藏
        this.shrink();
        if (oc.topo.util.module == "GF") {
            setTimeout(function () {
                ctx.loadMultiView();
            }, 3000);
        }
    },
    //自定义tree的显示方式
    _treeCustom: function (parent, parentTopoId) {
        var ctx = this;
        //按钮模板
        var btn = $("<span class='ico' style='background-position-y:-45px;position:absolute'></span>");
        btn.css({
            "background-position-y": "-46px",
            position: "absolute",
            top: "6px",
            right: "4px"
        });
        //初始化tree
        if (parentTopoId >= 0) {
            var showDnd = false, user = oc.index.getUser();
            if (parentTopoId == 0 && (user.systemUser || user.domainUser)) showDnd = true;
            parent.tree({
                url: oc.resource.getUrl("topo/" + parentTopoId + "/subTopos.htm"),
                method: "post",
                dnd: showDnd,
                onClick: function (node) {
                    ctx.curNode = node;
                    var target = $(node.target);
                    //检查这次点击事件是否由按钮触发
                    var flag = target.attr("data-btn-flag");
                    if (!flag) {
                        eve("topo.loadsubtopo", this, node.id);
                    } else {
                        ctx.subtopoId = node.id;
                        ctx.subtopoName = node.text;
                        target.removeAttr("data-btn-flag");
                    }
                },
                onDrop: function (target, source, point) {
                    var targetNode = parent.tree("getNode", target); // 将DOM对象转换为node

                    var param = new Array();	//组装子拓扑层次树
                    var subs = parent.tree("getRoots");
                    ctx._parseSubSort(subs, param, 0);
                    oc.util.ajax({
                        url: oc.resource.getUrl("topo/subtopo/nav/sort.htm"),
                        data: {subtopoTree: param},
                        success: function (msg) {
                            if (msg.code != 200) {
                                alert("排序异常");
                            }
                        }
                    });
                }
            });
            //鼠标移动到每一项时，显示设置按钮
            parent.on("mouseover", ".tree-node", function () {
                var cur = $(this);
                var tmpBtn = cur.find("span.ico");
                if (tmpBtn.length == 0) {
                    cur.css("position", "relative");
                    var tmpBtn = btn.clone();
                    tmpBtn.on("click", function (e) {
                        cur.attr("data-btn-flag", true);
                        cur.trigger("click");
                        ctx.showMenu(e);
                        e.stopPropagation();
                        return false;
                    });
                    cur.append(tmpBtn);
                }
                tmpBtn.show();
            });
            //鼠标离开隐藏设置按钮
            parent.on("mouseleave", ".tree-node", function () {
                var cur = $(this);
                var tmpBtn = cur.find("span.ico");
                if (tmpBtn.length > 0) {
                    tmpBtn.hide();
                }
            });
            //标题栏添加按钮
            var titleDiv = parent.parent().find(".panel-title");
            if (parentTopoId == 0 && 0 == parent.parent().prev().find(".panel-title").find("span").length) {
                titleDiv = parent.parent().prev().find(".panel-title");
            }
            if (titleDiv) {
                titleDiv.css("position", "relative");
                var tmpBtn = btn.clone();
                tmpBtn.on("click", function (e) {
                    ctx.subtopoId = parentTopoId;
                    ctx.showMenu(e);
                    e.stopPropagation();
                    return false;
                });
                titleDiv.append(tmpBtn);
                //标题栏点击事件
                titleDiv.on("click", function (e) {
                    if (parentTopoId < 0) {
                        if (!oc.topo.mapViewer) {
                            ctx.loadMultiView();
                        }
                    } else if (parentTopoId != 2 && parentTopoId >= 0) {
                        if (oc.topo.mapViewer) {
                            oc.topo.mapViewer.hide();
                        }
                        eve("topo.loadsubtopo", this, parentTopoId);
                    } else {
                        if (oc.topo.mapViewer) {
                            oc.topo.mapViewer.hide();
                        }
                    }
                });
            }
        } else {
            parent.tree({
                url: oc.resource.getUrl("resource/module/map/json/chinaTree.json"),
                method: "get",
                onClick: function (node) {
                    try {
                        ctx.loadMultiView(function () {
                            oc.module.topo.map.loadMapData(node.id, node.level);
                        });
                    } catch (e) {
                        console.log(e);
                    }
                }
            });

            var titleDiv = parent.parent().find(".panel-title");
            titleDiv.on("click", function (e) {
                try {
                    ctx.loadMultiView(function () {
                        oc.module.topo.map.loadMapData(1, 0);
                    });
                } catch (e) {
                    console.log(e);
                }
            });
        }
    },
    //转换封装排序
    _parseSubSort: function (tree, result, parentId) {
        var ctx = this;
        $.each(tree, function (index, sub) {
            var parent = {
                id: sub.id,
                text: sub.text,
                parentId: parentId,
                sort: index
            };
            result.push(parent);
            if (sub.children && sub.children.length > 0) {
                parent.children = new Array();
                ctx._parseSubSort(sub.children, parent.children, sub.id);
            }
        });
    },
    //加载多级拓扑
    loadMultiView: function (callBack) {
        try {
            oc.resource.loadScript("resource/module/topo/map/TopoMapView.js", function () {
                if (!oc.topo.mapViewer) {
                    oc.topo.mapViewer = new TopoMapView({
                        onLoad: callBack
                    });
                    oc.topo.mapViewer.show();
                } else {
                    oc.topo.mapCalled = null;
                    oc.topo.mapViewer.show();
                    callBack.call(this);
                }
            });
        } catch (e) {
            console.log(e);
        }
    },
    showMenu: function (e) {
        var ctx = this;
        var delItem = this.fields.menu.menu("findItem", "删除");
        var editItem = this.fields.menu.menu("findItem", "编辑");
        var newRoom = this.fields.menu.menu("findItem", "新建机房");
        var indexpage = this.fields.menu.menu("findItem", "设为拓扑首页");
        var newSubtopoItem = this.fields.menu.menu("findItem", "新建子拓扑");
        $(indexpage.target).show();
        if (this.subtopoId == 0 || this.subtopoId == 1 || this.subtopoId == 2) {
            $(delItem.target).hide();
            $(editItem.target).hide();
            switch (this.subtopoId) {
                case 0:
                    $(newSubtopoItem.target).show();
                    $(newRoom.target).hide();
                    break;
                case 1:
                    $(newSubtopoItem.target).hide();
                    $(newRoom.target).hide();
                    break;
                case 2:
                    $(newSubtopoItem.target).hide();
                    $(newRoom.target).show();
                    $(indexpage.target).hide();
                    break;
            }
        } else {
            $(delItem.target).show();
            $(editItem.target).show();
            $(newSubtopoItem.target).show();
            $(newRoom.target).hide();
        }
        //根据拓扑的类型觉得那些菜单项不显示
        $.post(oc.resource.getUrl("topo/subtopo/getTopoType.htm"), {
            topoId: this.subtopoId
        }, function (topo) {
            if (topo.id == 2) {
                $(newSubtopoItem.target).hide();
                $(newRoom.target).show();
            }
            ctx.fields.menu.menu('show', {
                left: e.pageX,
                top: e.pageY
            });
        }, "json");
    },
    enableDrag: function (dom) {
        dom.draggable({
            proxy: function (src) {
                var p = $(src).clone();
                p.find("img").attr("width", 55);
                p.css({
                    "z-index": "100",
                    position: "absolute"
                });
                p.appendTo("body");
                return p;
            },
            revert: true
        });
    },
    //展开
    expand: function () {
        var fd = this.fields;
        this.root.animate({
            right: "0px"
        }, 200, "linear", function () {
            fd.switchTopo.removeClass("hide");
            fd.switchResource.removeClass("hide");
        });
        fd.hook.addClass("topo_hook_off");
        fd.hook.attr("title", "关闭面板");
        this.isExpand = true;
    },
    //收缩
    shrink: function () {
        var fd = this.fields;
        this.root.animate({
            right: "-220px"
        }, 500, "linear", function () {
            fd.switchTopo.addClass("hide");
            fd.switchResource.addClass("hide");
        });
        fd.hook.removeClass("topo_hook_off");
        fd.hook.attr("title", "打开面板");
        this.isExpand = false;
    },
    regEvent: function () {
        var ctx = this;
        var tabs = this.fields.tabs.find("[data-panel-key]");
        tabs.on("click", function () {
            var tmp = $(this);
            tabs.css("z-index", 0);
            tmp.css("z-index", 1);
            var panelKey = tmp.attr("data-panel-key");
            ctx.fields.panels.find("[data-panel]").hide();
            ctx.fields.panels.find("[data-panel='" + panelKey + "']").fadeIn();
            if (panelKey == "resource") {
                if (ctx.currentPanel) {
                    ctx.currentPanel.fadeIn();
                } else {
                    $(resTabs.get(0)).trigger("click");
                }
            }
            ctx.expand();
        });
        //默认第一个
        $(tabs.get(0)).trigger("click");
        //资源tab
        var resTabs = this.fields.resTabs.find("[data-panel-key]");
        resTabs.on("click", function () {
            var tmp = $(this);
            resTabs.removeClass("active");
            tmp.addClass("active");
            var panelKey = tmp.attr("data-panel-key");
            ctx.currentPanelKey = panelKey;
            ctx.fields.resPanels.find("[data-panel]").hide();
            var panel = ctx.fields.resPanels.find("[data-panel='" + panelKey + "']");
            ctx.currentPanel = panel;
            if (!ctx[panelKey + "flag"]) {
                <!--BUG #41935 （性能测试）拓扑管理：拓扑图片加载建议优化 huangping 2017/7/14 start-->
                var html = "";
                switch (panelKey) {
                    case "net":
                        html = "<div class=\"icon hide template\" data-type=\"device\"><img/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_AIX.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_ap.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_appserver.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_dbserver.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_firewall.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_host.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_HP_UX.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_IDS.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_IPS.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_linux.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_mailserver.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_Proxy.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_router.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_server.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_Solaris.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_switch2.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_switch3.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_VPN.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_web.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/10_Windows.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/other.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/Cisco_FHQ_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/Cisco_GHRYQ_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/Cisco_RYQ_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/H3C_FHQ_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/H3C_JHLYQ_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/H3C_LYQ_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/HuaWei_FHQ_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/HuaWei_GHJ1_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/HuaWei_GHJ2_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/HuaWei_JHLYQ_G.png\"/></div>"
                            + "<div class=\"icon\" data-type=\"device\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/net/HuaWei_LYQ_G.png\"/></div>"
                        break;
                    case "house":
                        html = "<div class=\"icon hide template\"><img/></div>"
                            + "<div class=\"icon\" data-size=\"987,548\"><img src=\"themes/"+Highcharts.theme.currentSkin+"/images/topo/topoIcon/room/jf.png\"/></div>"
                            + '<div class="icon" data-size="88,67"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/motor-room.png"/></div>'
                            + '<div class="icon" data-size="88,248" data-type="cabinet"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/3djj.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="74,119"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/left-aa.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="79,131"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/pei_left.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="59,119"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/right-aa.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="68,119"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/zhen.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="74,148"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/cabinet.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="74,148"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/cabinet_side.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="37,74"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/cabinet_small.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="74,148"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/bigcb.png"/></div>'
                            + '<div class="icon" data-type="cabinet" data-size="74,148"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/cabinet_skew.png"/></div>'
                            + '<div class="icon" data-size="80,65"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/3dwlsb2.png"/></div>'
                            + '<div class="icon" data-size="1183,441"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/3d_room_1.png"/></div>'
                            + '<div class="icon" data-size="94,96"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/3dxsq.png"/></div>'
                            + '<div class="icon" data-size="85,40"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/3dzj1.png"/></div>'
                            + '<div class="icon" data-size="85,35"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/3dzj2.png"/></div>'
                            + '<div class="icon" data-size="59,105"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/3dzj3.png"/></div>'
                            + '<div class="icon" data-size="78,142"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/3dzj4.png"/></div>'
                            + '<div class="icon" data-size="130,350"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/bzjg.png"/></div>'
                            + '<div class="icon" data-size="100,76"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/camera.png"/></div>'
                            + '<div class="icon" data-size="90,232"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/zhbxg.png"/></div>'
                            + '<div class="icon" data-size="972,580"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/zm_jfys.png"/></div>'
                            + '<div class="icon" data-size="100,76"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/smokefeel.png"/></div>'
                            + '<div class="icon" data-size="938,532"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/room/wdttq.png"/></div>'
                        break;
                    case "bg":
                        html = '<div class="bgicon hide template"><img/></div>'
                            + '<div class="bgicon" title="恢复默认"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg_defaultsmall.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg_default.jpg"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg1-small.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg1.jpg"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg2-small.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg2.png"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg3-small.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg3.png"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg4-small.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg4.jpg"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg5-small.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg5.png"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg6-small.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg6.png"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg7-small.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg7.png"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg8-small.png" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/bg8.png"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/12-topobackground-1-small.jpg" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/12-topobackground-1.jpg"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/12-topobackground-2-small.jpg" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/12-topobackground-2.jpg"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/12-topobackground-3-small.jpg" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/12-topobackground-3.jpg"/></div>'
                            + '<div class="bgicon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/12-topobackground-4-small.jpg" data-bg="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/bg/12-topobackground-4.jpg"/></div>'
                        break;
                    case "map":
                        html =
                            '<div class="icon hide template" data-type="map"><img/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/England.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/Indonesia.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_anhui.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_beijing.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_china.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_china2.jpg"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_chongqing.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_fujian.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_gansu.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_guangdong.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_guangxi.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_guizhou.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_hainan.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_hebei.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_heilongjiang.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_henan.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_hubei.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_hunan.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_jiangsu.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_jiangxi.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_jilin.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_liaoning.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_neimenggu.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_ningxia.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_qinghai.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_shandong.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_shanxi.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_shanxi2.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_sichuan.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_taiwan.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_tianjin.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_world.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_xinjiang.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_xizang.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_yunnan.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/map_zhejiang.png"/></div>'
                            + '<div class="icon" data-type="map"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/map/Singapore.png"/></div>'
                        break;
                    case "manage":
                        html =
                            '<div class="icon hide template"><img/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/a12.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/a13.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/hzhb.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/fzjg.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/internet.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/kbqy.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/motor-room.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/officebuilding.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/PSTN.png"/></div>'
                            + '<div class="icon"><img src="themes/'+Highcharts.theme.currentSkin+'/images/topo/topoIcon/admin/ydyh.png"/></div>'
                        break;
                    default:
                        break;
                }
                var reg = new RegExp("\/default\/", "g");
                html = html.replace(reg, "/" + ctx.theme + "/");
                panel.html(html);
                ctx.enableDrag(ctx.fields.panels.find(".icon"));
                <!--BUG #41935 （性能测试）拓扑管理：拓扑图片加载建议优化 huangping 2017/7/14 end-->
                ctx.refreshPanel();
                ctx[panelKey + "flag"] = true;
            }
            //初始化图标
            panel.fadeIn();
        });
        //默认第一个
        // $(resTabs.get(0)).trigger("click");
        //图标添加按钮
        this.fields.iconAddBtn.on("click", function () {
            if (ctx.currentPanelKey) {
                var ud = new UploadImgDia({type: ctx.currentPanelKey});
                ud.on("success", function () {
                    ctx.refreshPanel();
                });
            }
        });
        //图标删除按钮
        this.fields.iconDelBtn.on("click", function () {
            var removeBtn = $(this);
            var isRemove = removeBtn.attr("removeFlag");
            if (!isRemove || isRemove == "false") {//开启删除模式
                isRemove = false;
                /*BUG #54232 拓扑管理：点击删除管理图标按钮后，按钮不应变为小方框 huangping 2018/5/2 start*/
                // removeBtn.removeClass("ico-del");
                // removeBtn.addClass("icon-gabage-open");
                removeBtn.addClass("icon-empty");
                /*BUG #54232 拓扑管理：点击删除管理图标按钮后，按钮不应变为小方框 huangping 2018/5/2 end*/
                ctx.currentPanel.find(".icon,.bgicon").off().on("click", function () {
                    var tmp = $(this);
                    var id = tmp.find("img").attr("data-id");
                    if (!id) return;
                    /*BUG #54232 拓扑管理：点击删除管理图标按钮后，按钮不应变为小方框 huangping 2018/5/2 start*/
                    // tmp.toggleClass("selected");
                    tmp.toggleClass("icon_selected");
                    if (ctx.currentPanel.find(".icon_selected").length > 0) {
                        removeBtn.removeClass("icon-empty");
                        removeBtn.addClass("fa-trash-o");
                    } else {
                        removeBtn.removeClass("fa-trash-o");
                        removeBtn.addClass("icon-empty");
                    }
                    /*BUG #54232 拓扑管理：点击删除管理图标按钮后，按钮不应变为小方框 huangping 2018/5/2 end*/
                });
            } else {
                isRemove = true;
                /*BUG #54232 拓扑管理：点击删除管理图标按钮后，按钮不应变为小方框 huangping 2018/5/2 start*/
                // removeBtn.addClass("ico-del");
                // removeBtn.removeClass("icon-gabage-open");
                removeBtn.removeClass("icon-empty");
                removeBtn.removeClass("fa-trash-o");
                //克隆选中的节点
                // var nodes = ctx.currentPanel.find(".selected").clone();
                var nodes = ctx.currentPanel.find(".icon_selected").clone();
                /*BUG #54232 拓扑管理：点击删除管理图标按钮后，按钮不应变为小方框 huangping 2018/5/2 end*/
                nodes.removeClass("bgicon");
                nodes.addClass("icon");
                //如果有选择的图片
                if (nodes.length > 0) {
                    //显示确认对话框
                    var dia = $("<div style='height:30px;line-height:30px;overflow-y:auto;height:200px;padding:4px;'>确定要删除下列自定义图标么？<div style='margin-top:5px;'></div></div>");
                    dia.find("div").append(nodes);
                    dia.find(".icon").css({
                        display: "inline-block",
                        margin: "2px"
                    });
                    dia.find(".icon img").css({width: 55, height: 55});
                    dia.dialog({
                        width: 600,
                        heigth: 200,
                        title: "提示",
                        onClose: function () {
                            /*BUG #54232 拓扑管理：点击删除管理图标按钮后，按钮不应变为小方框 huangping 2018/5/2 start*/
                            // ctx.currentPanel.find(".icon").removeClass("selected");
                            ctx.currentPanel.find(".icon").removeClass("icon_selected");
                            /*BUG #54232 拓扑管理：点击删除管理图标按钮后，按钮不应变为小方框 huangping 2018/5/2 end*/
                        },
                        buttons: [{
                            text: "确定", handler: function () {
                                var ids = [];
                                dia.find("img").each(function (idx, node) {
                                    var id = $(node).attr("data-id");
                                    ids.push(id);
                                });
                                var idsStr = ids.join(",");
                                //开始后台删除
                                oc.util.ajax({
                                    url: oc.resource.getUrl("topo/image/del.htm"),
                                    data: {ids: idsStr},
                                    success: function (msg) {
                                        if (msg.code == 200) {
                                            //删除界面上的节点
                                            ctx.currentPanel.find(".icon img,.bgicon img").each(function (idx, node) {
                                                var id = $(node).attr("data-id");
                                                if (idsStr.indexOf(id) >= 0) {
                                                    $(node).parent().remove();
                                                }
                                            });
                                        }
                                        // alert(msg.data);
                                        dia.dialog("close");
                                    }
                                });
                            }
                        }, {
                            text: "取消", handler: function () {
                                dia.dialog("close");
                                //重置选中状态
                            }
                        }]
                    });
                }
            }
            ctx.currentPanel.find(".icon").draggable({
                disabled: !isRemove
            });
            removeBtn.attr("removeFlag", !isRemove);
        });
        //按钮
        this.fields.searchBtn.on("click", function () {
            ctx.search();
        });
        //搜索输入IP的框
        this.fields.searchIpt.on("keyup", function (e) {
            if (e.keyCode == 13) {
                ctx.search();
            }
        });
        //菜单项点击
        this.fields.menu.menu({
            onClick: function (item) {
                switch (item.text) {
                    case "编辑":
                        oc.resource.loadScripts(["resource/module/topo/contextMenu/CreateSubTopo.js", "resource/module/topo/contextMenu/CreateRoom.js"], function () {
                            $.post(oc.resource.getUrl("topo/subtopo/getTopoType.htm"), {
                                topoId: ctx.subtopoId
                            }, function (topo) {
                                if (topo.id == 2) {//机房视图的编辑
                                    var cr = new CreateRoom({
                                        title: "编辑机房",
                                        value: {
                                            id: ctx.subtopoId,
                                            name: ctx.subtopoName
                                        },
                                        onOk: function () {
                                            eve("oc.topo.addSubTopo.finished", ctx, 2);
                                        }
                                    });
                                } else {
                                    var cs = new CreateSubTopo({
                                        subTopoId: ctx.subtopoId,
                                        title: "编辑子拓扑"
                                    });
                                    cs.on("save", function (info) {
                                        eve("oc.topo.addSubTopo.finished", ctx, 0, ctx.fields.twoLevel);
                                        eve("topo.loadsubtopo", this, info.id);
                                    });
                                }
                            }, "json");

                        });
                        break;
                    case "新建子拓扑":
                        oc.resource.loadScript("resource/module/topo/contextMenu/CreateSubTopo.js", function () {
                            var cs = new CreateSubTopo({
                                parentId: ctx.subtopoId
                            });
                            cs.on("save", function (info) {
                                eve("oc.topo.addSubTopo.finished", ctx, 0, ctx.fields.twoLevel);
                                eve("topo.loadsubtopo", this, info.id);
                            });
                        });
                        break;
                    case "设为拓扑首页":
                        oc.util.ajax({
                            url: oc.resource.getUrl("topo/setting/save.htm"),
                            data: {
                                key: "oc_topo_graph_homepage",
                                value: JSON.stringify({id: ctx.subtopoId})
                            },
                            success: function () {
                                alert("首页设置成功");
                            }
                        });
                        break;
                    case "删除":
                        $.messager.confirm("警告 正在删除  " + (ctx.subtopoName || ""), "确定要删除吗？", function (r) {
                            if (r) {
                                $.post(oc.resource.getUrl("topo/subtopo/getAttr.htm"), {id: ctx.subtopoId},
                                    function (info) {
                                        oc.util.ajax({
                                            url: oc.resource.getUrl("topo/subtopo/deleteRoom.htm"),
                                            data: {
                                                id: ctx.subtopoId
                                            },
                                            success: function () {
                                                alert("删除成功");
                                                eve("topo.loadsubtopo", ctx, 0);
                                                var attr = JSON.parse(info.attr || "{}");
                                                if (attr.type && attr.type == "room") {
                                                    eve("oc.topo.addSubTopo.finished", ctx, 2, ctx.fields.roomView);
                                                } else {
                                                    eve("oc.topo.addSubTopo.finished", ctx, 0, ctx.fields.twoLevel);
                                                }
                                            },
                                            dataType: "text"
                                        });
                                    }, "json");
                            }
                        });
                        break;
                    case "新建机房":
                        oc.resource.loadScript("resource/module/topo/contextMenu/CreateRoom.js", function () {
                            new CreateRoom({
                                parentId: ctx.subtopoId,
                                onOk: function (info) {
                                    eve("oc.topo.addSubTopo.finished", ctx, 2, ctx.fields.roomView);
                                    eve("topo.loadsubtopo", ctx, info.id);
                                }
                            });
                        });
                        break;
                }
            }
        });
        //监听子拓扑添加完成事件
        eve.on("oc.topo.addSubTopo.finished", function (pid) {
            switch (pid) {
                case 0:
                    $.post(oc.resource.getUrl("topo/0/subTopos.htm"), function (topos) {
                        ctx.fields.twoLevel.tree("loadData", topos);
                    }, "json");
                    break;
                case 1:
                    $.post(oc.resource.getUrl("topo/1/subTopos.htm"), function (topos) {
                        ctx.fields.threeLevel.tree("loadData", topos);
                    }, "json");
                    break;
                case 2:
                    $.post(oc.resource.getUrl("topo/2/subTopos.htm"), function (topos) {
                        ctx.fields.roomView.tree("loadData", topos);
                    }, "json");
                    break;
                default:
                    $.post(oc.resource.getUrl("topo/0/subTopos.htm"), function (topos) {
                        ctx.fields.twoLevel.tree("loadData", topos);
                    }, "json");
                    $.post(oc.resource.getUrl("topo/1/subTopos.htm"), function (topos) {
                        ctx.fields.threeLevel.tree("loadData", topos);
                    }, "json");
                    $.post(oc.resource.getUrl("topo/2/subTopos.htm"), function (topos) {
                        ctx.fields.roomView.tree("loadData", topos);
                    }, "json");
                    break;
            }
        });
        //监听背景更换
        this.fields.bgcontainer.on("click", ".bgicon", function () {
            if (!ctx.fields.iconDelBtn.hasClass("icon-gabage-open")) {
                var item = $(this).find("img");
                var src = item.attr("data-bg") || item.attr("src");
                eve("topo.changebg", this, src);
            }
        });
        //展开收缩
        this.fields.hook.on("click", function () {
            if (ctx.isExpand) {
                ctx.shrink();
            } else {
                ctx.expand();
            }
        });
    },
    //刷新一个面板的图标
    refreshPanel: function () {
        var ctx = this;
        var type = this.currentPanelKey;
        var parent = this.currentPanel;
        //获取一个模板
        var template = parent.find(".template");
        //从服务器获取图片
        $.get(oc.resource.getUrl("topo/image/getByType/" + type + ".htm"), function (imgs) {
            if (imgs) {
                //清空以前的自定义id，重新更新
                ctx.currentPanel.find("img[data-id]").parent().remove();
                //绘制当前图标
                for (var i = 0; i < imgs.length; ++i) {
                    var item = imgs[i];
                    var tmp = template.clone();
                    var image = tmp.find("img");
                    image.attr({
                        src: oc.resource.getUrl("topo/image/change.htm?path=" + item.fileId),
                        width: item.width,
                        height: item.height,
                        "data-id": item.id
                    });
                    tmp.removeClass("hide template");
                    template.after(tmp);
                    if (type != "bg") {
                        ctx.enableDrag(tmp);
                    }
                }
            }
        }, "json");
    },
    search: function () {
        var ctx = this;
        //获取搜索条件
        var val = $.trim(ctx.fields.searchIpt.val());
        //请求后台服务器进行搜索
        if (val) {
            $.post(oc.resource.getUrl("topo/getSubToposByIp.htm"), {
                ip: val
            }, function (info) {
                //切换回搜索结果面板
                ctx.fields.searchResultPanel.panel("open");
                ctx.fields.searchResultPanel.show();
                ctx.fields.topoAccord.hide();
                //展示搜索结果
                ctx.fields.searchResultPanel.html("");
                oc.ui.navsublist({
                    selector: ctx.fields.searchResultPanel,
                    textField: "name",
                    valueField: "id",
                    data: info,
                    click: function (href, item, e) {
                        eve("topo.loadsubtopo", this, item.id);
                    }
                });
            }, "json");
        }
    }
};