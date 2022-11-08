MapLayer = function(t, e) {
    Map.call(this),
    $.extend(this.config, t),
    $.extend(this.handler, e),
    this.textGroup = null;
    var a = this,
    n = function() {
        a.createCanvas(),
        a.createTextGroup(),
        $(a.canvas).bind("dblclick",
        function(t) {
            var e = $(t.target),
            n = e.attr("key");
            n && a.handler.handleAreaClick(e)
        })
    };
    this.createTextGroup = function() {
        this.canvas.setAttribute("class", "layer"),
        1 == this.handler.level ? this.canvas.setAttribute("class", "layer level1") : 2 == this.handler.level ? this.canvas.setAttribute("class", "layer level2") : 3 == this.handler.level && this.canvas.setAttribute("class", "layer level3"),
        this.canvas.setAttribute("font-size", this.config.fontSize / this.config.scale),
        this.canvas.setAttribute("font-family", this.config.fontFamily)
    },
    this.appendTo = function(t) {
        return t.appendChild(this.canvas),
        a
    },
    this.zoom = function(t, e) {
        this.zoomCanvas(t, e);
        for (var a = this.canvas.getElementsByTagName("text"), n = 0; n < a.length; n++) {
            var i = parseFloat(a[n].getAttribute("ox")),
            r = parseFloat(a[n].getAttribute("oy"));
            a[n].setAttribute("x", i * t),
            a[n].setAttribute("y", r * t),
            a[n].setAttribute("transform", "scale(" + 1 / t + "," + 1 / t + ")")
        }
    },
    this.draw = function(t) {
        var e = Map.createElement("g");
//    	var mapColor = ["#0b5897","#0e71c1","#1167b7","#0f5ba2","#0a3f74","#0f5fad","#0d5696",
//  		              "#09396f","#0f73c4","#06375d","#0c599a","#0e72c3","#0f60a5",
//  		              "#0e5ba4","#0c477c","#0e4f8e","#0c4f8f","#0b355a",
//  		              "#1061a6","#073e71","#0e4d8b", "#115fa4","#0e5799","#106fbc","#0d70be"];
//    	var fillColor = mapColor[Math.floor(Math.random() * mapColor.length + 1) - 1];
//        e.setAttribute('fill',fillColor);
        var a = this.drawCanvas(t);
        t.textCenter && !t.loaded && (t.textCenter = this.convertToXY(t.textCenter[0], t.textCenter[1]), t.loaded = !0);
        var n = this.drawText(decodeURIComponent(t.name), t.textCenter ? t.textCenter: t.center, t.code);
        return e.appendChild(a),
        e.appendChild(n),
        this.canvas.appendChild(e),
        a
    },
    this.drawText = function(t, e, a) {
        var n = Map.createElement("text");
        return n.setAttribute("x", e[0]),
        n.setAttribute("y", e[1]),
        n.setAttribute("ox", e[0]),
        n.setAttribute("oy", e[1]),
        n.textContent = t,
        n.setAttribute("key", a),
        n
    },
    n()
};