var PointLayer = function(t, i) {
    Map.call(this),
    $.extend(this.config, t),
    $.extend(this.handler, i);
    var e = this,
    s = function() {
        e.createCanvas()
    };
    this.zoom = function(t, i) {
        this.zoomCanvas(t, i),
        this.clearPoint()
    },
    this.addPoint = function(t) {
        var i = Map.createElement("image");
        var xIndex = t.left - t.width / (2 * this.config.scale);
        var yIndex = t.top - t.height / (2 * this.config.scale);
        return i.setAttribute("width", t.width / this.config.scale),
        i.setAttribute("height", t.height / this.config.scale),
        i.setAttribute("x", xIndex),
        i.setAttribute("y", yIndex),
        i.href.baseVal = t.src,
        this.canvas.appendChild(i),
        i
    },
    this.clearPoint = function() {
        for (; this.canvas.childNodes.length > 0;) this.canvas.removeChild(this.canvas.childNodes[0])
    },
    s()
};