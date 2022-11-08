var require, define; !
function(e) {
    function r(e, r) {
        function t() {
            clearTimeout(o)
        }
        if (! (e in u)) {
            u[e] = !0;
            var i = document.createElement("script");
            if (r) {
                var o = setTimeout(r, require.timeout);
                i.onerror = function() {
                    clearTimeout(o),
                    r()
                },
                "onload" in i ? i.onload = t: i.onreadystatechange = function() { ("loaded" == this.readyState || "complete" == this.readyState) && t()
                }
            }
            return i.type = "text/javascript",
            i.src = e,
            n.appendChild(i),
            i
        }
    }
    function t(e, t, n) {
        var o = i[e] || (i[e] = []);
        o.push(t);
        var a, u = s[e] || {},
        f = u.pkg;
        a = f ? c[f].url: u.url || e,
        r(a, n &&
        function() {
            n(e)
        })
    }
    var n = document.getElementsByTagName("head")[0],
    i = {},
    o = {},
    a = {},
    u = {},
    s = {},
    c = {};
    define = function(e, r) {
        o[e] = r;
        var t = i[e];
        if (t) {
            for (var n = 0,
            a = t.length; a > n; n++) t[n]();
            delete i[e]
        }
    },
    require = function(e) {
        e = require.alias(e);
        var r = a[e];
        if (r) return r.exports;
        var t = o[e];
        if (!t) throw "[ModJS] Cannot find module `" + e + "`";
        r = a[e] = {
            exports: {}
        };
        var n = "function" == typeof t ? t.apply(r, [require, r.exports, r]) : t;
        return n && (r.exports = n),
        r.exports
    },
    require.async = function(r, n, i) {
        function a(e) {
            for (var r = 0,
            n = e.length; n > r; r++) {
                var c = e[r];
                if (! (c in o || c in l)) {
                    l[c] = !0,
                    p++,
                    t(c, u, i);
                    var f = s[c];
                    f && "deps" in f && a(f.deps)
                }
            }
        }
        function u() {
            if (0 == p--) {
                for (var t = [], i = 0, o = r.length; o > i; i++) t[i] = require(r[i]);
                n && n.apply(e, t)
            }
        }
        "string" == typeof r && (r = [r]);
        for (var c = 0,
        f = r.length; f > c; c++) r[c] = require.alias(r[c]);
        var l = {},
        p = 0;
        a(r),
        u()
    },
    require.resourceMap = function(e) {
        var r, t;
        t = e.res;
        for (r in t) t.hasOwnProperty(r) && (s[r] = t[r]);
        t = e.pkg;
        for (r in t) t.hasOwnProperty(r) && (c[r] = t[r])
    },
    require.loadJs = function(e) {
        r(e)
    },
    require.loadCss = function(e) {
        if (e.content) {
            var r = document.createElement("style");
            r.type = "text/css",
            r.styleSheet ? r.styleSheet.cssText = e.content: r.innerHTML = e.content,
            n.appendChild(r)
        } else if (e.url) {
            var t = document.createElement("link");
            t.href = e.url,
            t.rel = "stylesheet",
            t.type = "text/css",
            n.appendChild(t)
        }
    },
    require.alias = function(e) {
        return e
    },
    require.timeout = 5e3
} (this);;
define("wiki-common:widget/lib/jquery/jquery.mousewheel.js",
function(e, t, i) { !
    function(e) {
        "function" == typeof define && define.amd ? define(["jquery"], e) : "object" == typeof t ? i.exports = e: e(jQuery)
    } (function(e) {
        function t(t) {
            var s = t || window.event,
            a = h.call(arguments, 1),
            u = 0,
            f = 0,
            d = 0,
            c = 0,
            m = 0,
            g = 0;
            if (t = e.event.fix(s), t.type = "mousewheel", "detail" in s && (d = -1 * s.detail), "wheelDelta" in s && (d = s.wheelDelta), "wheelDeltaY" in s && (d = s.wheelDeltaY), "wheelDeltaX" in s && (f = -1 * s.wheelDeltaX), "axis" in s && s.axis === s.HORIZONTAL_AXIS && (f = -1 * d, d = 0), u = 0 === d ? f: d, "deltaY" in s && (d = -1 * s.deltaY, u = d), "deltaX" in s && (f = s.deltaX, 0 === d && (u = -1 * f)), 0 !== d || 0 !== f) {
                if (1 === s.deltaMode) {
                    var w = e.data(this, "mousewheel-line-height");
                    u *= w,
                    d *= w,
                    f *= w
                } else if (2 === s.deltaMode) {
                    var v = e.data(this, "mousewheel-page-height");
                    u *= v,
                    d *= v,
                    f *= v
                }
                if (c = Math.max(Math.abs(d), Math.abs(f)), (!l || l > c) && (l = c, n(s, c) && (l /= 40)), n(s, c) && (u /= 40, f /= 40, d /= 40), u = Math[u >= 1 ? "floor": "ceil"](u / l), f = Math[f >= 1 ? "floor": "ceil"](f / l), d = Math[d >= 1 ? "floor": "ceil"](d / l), r.settings.normalizeOffset && this.getBoundingClientRect) {
                    var p = this.getBoundingClientRect();
                    m = t.clientX - p.left,
                    g = t.clientY - p.top
                }
                return t.deltaX = f,
                t.deltaY = d,
                t.deltaFactor = l,
                t.offsetX = m,
                t.offsetY = g,
                t.deltaMode = 0,
                a.unshift(t, u, f, d),
                o && clearTimeout(o),
                o = setTimeout(i, 200),
                (e.event.dispatch || e.event.handle).apply(this, a)
            }
        }
        function i() {
            l = null
        }
        function n(e, t) {
            return r.settings.adjustOldDeltas && "mousewheel" === e.type && t % 120 === 0
        }
        var o, l, s = ["wheel", "mousewheel", "DOMMouseScroll", "MozMousePixelScroll"],
        a = "onwheel" in document || document.documentMode >= 9 ? ["wheel"] : ["mousewheel", "DomMouseScroll", "MozMousePixelScroll"],
        h = Array.prototype.slice;
        if (e.event.fixHooks) for (var u = s.length; u;) e.event.fixHooks[s[--u]] = e.event.mouseHooks;
        var r = e.event.special.mousewheel = {
            version: "3.1.12",
            setup: function() {
                if (this.addEventListener) for (var i = a.length; i;) this.addEventListener(a[--i], t, !1);
                else this.onmousewheel = t;
                e.data(this, "mousewheel-line-height", r.getLineHeight(this)),
                e.data(this, "mousewheel-page-height", r.getPageHeight(this))
            },
            teardown: function() {
                if (this.removeEventListener) for (var i = a.length; i;) this.removeEventListener(a[--i], t, !1);
                else this.onmousewheel = null;
                e.removeData(this, "mousewheel-line-height"),
                e.removeData(this, "mousewheel-page-height")
            },
            getLineHeight: function(t) {
                var i = e(t),
                n = i["offsetParent" in e.fn ? "offsetParent": "parent"]();
                return n.length || (n = e("body")),
                parseInt(n.css("fontSize"), 10) || parseInt(i.css("fontSize"), 10) || 16
            },
            getPageHeight: function(t) {
                return e(t).height()
            },
            settings: {
                adjustOldDeltas: !0,
                normalizeOffset: !0
            }
        };
        e.fn.extend({
            mousewheel: function(e) {
                return e ? this.bind("mousewheel", e) : this.trigger("mousewheel")
            },
            unmousewheel: function(e) {
                return this.unbind("mousewheel", e)
            }
        })
    })
});;
define("wiki-common:widget/lib/jquery/jquery_1.11.1.js",
function(e, t, n) { !
    function(e, t) {
        "object" == typeof n && "object" == typeof n.exports ? n.exports = e.document ? t(e, !0) : function(e) {
            if (!e.document) throw new Error("jQuery requires a window with a document");
            return t(e)
        }: t(e)
    } ("undefined" != typeof window ? window: this,
    function(e, t) {
        function n(e) {
            var t = e.length,
            n = it.type(e);
            return "function" === n || it.isWindow(e) ? !1 : 1 === e.nodeType && t ? !0 : "array" === n || 0 === t || "number" == typeof t && t > 0 && t - 1 in e
        }
        function r(e, t, n) {
            if (it.isFunction(t)) return it.grep(e,
            function(e, r) {
                return !! t.call(e, r, e) !== n
            });
            if (t.nodeType) return it.grep(e,
            function(e) {
                return e === t !== n
            });
            if ("string" == typeof t) {
                if (ft.test(t)) return it.filter(t, e, n);
                t = it.filter(t, e)
            }
            return it.grep(e,
            function(e) {
                return it.inArray(e, t) >= 0 !== n
            })
        }
        function i(e, t) {
            do e = e[t];
            while (e && 1 !== e.nodeType);
            return e
        }
        function o(e) {
            var t = xt[e] = {};
            return it.each(e.match(bt) || [],
            function(e, n) {
                t[n] = !0
            }),
            t
        }
        function a() {
            ht.addEventListener ? (ht.removeEventListener("DOMContentLoaded", s, !1), e.removeEventListener("load", s, !1)) : (ht.detachEvent("onreadystatechange", s), e.detachEvent("onload", s))
        }
        function s() { (ht.addEventListener || "load" === event.type || "complete" === ht.readyState) && (a(), it.ready())
        }
        function l(e, t, n) {
            if (void 0 === n && 1 === e.nodeType) {
                var r = "data-" + t.replace(Et, "-$1").toLowerCase();
                if (n = e.getAttribute(r), "string" == typeof n) {
                    try {
                        n = "true" === n ? !0 : "false" === n ? !1 : "null" === n ? null: +n + "" === n ? +n: Nt.test(n) ? it.parseJSON(n) : n
                    } catch(i) {}
                    it.data(e, t, n)
                } else n = void 0
            }
            return n
        }
        function u(e) {
            var t;
            for (t in e) if (("data" !== t || !it.isEmptyObject(e[t])) && "toJSON" !== t) return ! 1;
            return ! 0
        }
        function c(e, t, n, r) {
            if (it.acceptData(e)) {
                var i, o, a = it.expando,
                s = e.nodeType,
                l = s ? it.cache: e,
                u = s ? e[a] : e[a] && a;
                if (u && l[u] && (r || l[u].data) || void 0 !== n || "string" != typeof t) return u || (u = s ? e[a] = J.pop() || it.guid++:a),
                l[u] || (l[u] = s ? {}: {
                    toJSON: it.noop
                }),
                ("object" == typeof t || "function" == typeof t) && (r ? l[u] = it.extend(l[u], t) : l[u].data = it.extend(l[u].data, t)),
                o = l[u],
                r || (o.data || (o.data = {}), o = o.data),
                void 0 !== n && (o[it.camelCase(t)] = n),
                "string" == typeof t ? (i = o[t], null == i && (i = o[it.camelCase(t)])) : i = o,
                i
            }
        }
        function d(e, t, n) {
            if (it.acceptData(e)) {
                var r, i, o = e.nodeType,
                a = o ? it.cache: e,
                s = o ? e[it.expando] : it.expando;
                if (a[s]) {
                    if (t && (r = n ? a[s] : a[s].data)) {
                        it.isArray(t) ? t = t.concat(it.map(t, it.camelCase)) : t in r ? t = [t] : (t = it.camelCase(t), t = t in r ? [t] : t.split(" ")),
                        i = t.length;
                        for (; i--;) delete r[t[i]];
                        if (n ? !u(r) : !it.isEmptyObject(r)) return
                    } (n || (delete a[s].data, u(a[s]))) && (o ? it.cleanData([e], !0) : nt.deleteExpando || a != a.window ? delete a[s] : a[s] = null)
                }
            }
        }
        function f() {
            return ! 0
        }
        function p() {
            return ! 1
        }
        function h() {
            try {
                return ht.activeElement
            } catch(e) {}
        }
        function m(e) {
            var t = Ot.split("|"),
            n = e.createDocumentFragment();
            if (n.createElement) for (; t.length;) n.createElement(t.pop());
            return n
        }
        function g(e, t) {
            var n, r, i = 0,
            o = typeof e.getElementsByTagName !== Ct ? e.getElementsByTagName(t || "*") : typeof e.querySelectorAll !== Ct ? e.querySelectorAll(t || "*") : void 0;
            if (!o) for (o = [], n = e.childNodes || e; null != (r = n[i]); i++) ! t || it.nodeName(r, t) ? o.push(r) : it.merge(o, g(r, t));
            return void 0 === t || t && it.nodeName(e, t) ? it.merge([e], o) : o
        }
        function v(e) {
            Dt.test(e.type) && (e.defaultChecked = e.checked)
        }
        function y(e, t) {
            return it.nodeName(e, "table") && it.nodeName(11 !== t.nodeType ? t: t.firstChild, "tr") ? e.getElementsByTagName("tbody")[0] || e.appendChild(e.ownerDocument.createElement("tbody")) : e
        }
        function b(e) {
            return e.type = (null !== it.find.attr(e, "type")) + "/" + e.type,
            e
        }
        function x(e) {
            var t = Vt.exec(e.type);
            return t ? e.type = t[1] : e.removeAttribute("type"),
            e
        }
        function w(e, t) {
            for (var n, r = 0; null != (n = e[r]); r++) it._data(n, "globalEval", !t || it._data(t[r], "globalEval"))
        }
        function T(e, t) {
            if (1 === t.nodeType && it.hasData(e)) {
                var n, r, i, o = it._data(e),
                a = it._data(t, o),
                s = o.events;
                if (s) {
                    delete a.handle,
                    a.events = {};
                    for (n in s) for (r = 0, i = s[n].length; i > r; r++) it.event.add(t, n, s[n][r])
                }
                a.data && (a.data = it.extend({},
                a.data))
            }
        }
        function C(e, t) {
            var n, r, i;
            if (1 === t.nodeType) {
                if (n = t.nodeName.toLowerCase(), !nt.noCloneEvent && t[it.expando]) {
                    i = it._data(t);
                    for (r in i.events) it.removeEvent(t, r, i.handle);
                    t.removeAttribute(it.expando)
                }
                "script" === n && t.text !== e.text ? (b(t).text = e.text, x(t)) : "object" === n ? (t.parentNode && (t.outerHTML = e.outerHTML), nt.html5Clone && e.innerHTML && !it.trim(t.innerHTML) && (t.innerHTML = e.innerHTML)) : "input" === n && Dt.test(e.type) ? (t.defaultChecked = t.checked = e.checked, t.value !== e.value && (t.value = e.value)) : "option" === n ? t.defaultSelected = t.selected = e.defaultSelected: ("input" === n || "textarea" === n) && (t.defaultValue = e.defaultValue)
            }
        }
        function N(t, n) {
            var r, i = it(n.createElement(t)).appendTo(n.body),
            o = e.getDefaultComputedStyle && (r = e.getDefaultComputedStyle(i[0])) ? r.display: it.css(i[0], "display");
            return i.detach(),
            o
        }
        function E(e) {
            var t = ht,
            n = Zt[e];
            return n || (n = N(e, t), "none" !== n && n || (Kt = (Kt || it("<iframe frameborder='0' width='0' height='0'/>")).appendTo(t.documentElement), t = (Kt[0].contentWindow || Kt[0].contentDocument).document, t.write(), t.close(), n = N(e, t), Kt.detach()), Zt[e] = n),
            n
        }
        function k(e, t) {
            return {
                get: function() {
                    var n = e();
                    if (null != n) return n ? void delete this.get: (this.get = t).apply(this, arguments)
                }
            }
        }
        function S(e, t) {
            if (t in e) return t;
            for (var n = t.charAt(0).toUpperCase() + t.slice(1), r = t, i = pn.length; i--;) if (t = pn[i] + n, t in e) return t;
            return r
        }
        function A(e, t) {
            for (var n, r, i, o = [], a = 0, s = e.length; s > a; a++) r = e[a],
            r.style && (o[a] = it._data(r, "olddisplay"), n = r.style.display, t ? (o[a] || "none" !== n || (r.style.display = ""), "" === r.style.display && At(r) && (o[a] = it._data(r, "olddisplay", E(r.nodeName)))) : (i = At(r), (n && "none" !== n || !i) && it._data(r, "olddisplay", i ? n: it.css(r, "display"))));
            for (a = 0; s > a; a++) r = e[a],
            r.style && (t && "none" !== r.style.display && "" !== r.style.display || (r.style.display = t ? o[a] || "": "none"));
            return e
        }
        function j(e, t, n) {
            var r = un.exec(t);
            return r ? Math.max(0, r[1] - (n || 0)) + (r[2] || "px") : t
        }
        function D(e, t, n, r, i) {
            for (var o = n === (r ? "border": "content") ? 4 : "width" === t ? 1 : 0, a = 0; 4 > o; o += 2)"margin" === n && (a += it.css(e, n + St[o], !0, i)),
            r ? ("content" === n && (a -= it.css(e, "padding" + St[o], !0, i)), "margin" !== n && (a -= it.css(e, "border" + St[o] + "Width", !0, i))) : (a += it.css(e, "padding" + St[o], !0, i), "padding" !== n && (a += it.css(e, "border" + St[o] + "Width", !0, i)));
            return a
        }
        function L(e, t, n) {
            var r = !0,
            i = "width" === t ? e.offsetWidth: e.offsetHeight,
            o = en(e),
            a = nt.boxSizing && "border-box" === it.css(e, "boxSizing", !1, o);
            if (0 >= i || null == i) {
                if (i = tn(e, t, o), (0 > i || null == i) && (i = e.style[t]), rn.test(i)) return i;
                r = a && (nt.boxSizingReliable() || i === e.style[t]),
                i = parseFloat(i) || 0
            }
            return i + D(e, t, n || (a ? "border": "content"), r, o) + "px"
        }
        function H(e, t, n, r, i) {
            return new H.prototype.init(e, t, n, r, i)
        }
        function q() {
            return setTimeout(function() {
                hn = void 0
            }),
            hn = it.now()
        }
        function _(e, t) {
            var n, r = {
                height: e
            },
            i = 0;
            for (t = t ? 1 : 0; 4 > i; i += 2 - t) n = St[i],
            r["margin" + n] = r["padding" + n] = e;
            return t && (r.opacity = r.width = e),
            r
        }
        function M(e, t, n) {
            for (var r, i = (xn[t] || []).concat(xn["*"]), o = 0, a = i.length; a > o; o++) if (r = i[o].call(n, t, e)) return r
        }
        function O(e, t, n) {
            var r, i, o, a, s, l, u, c, d = this,
            f = {},
            p = e.style,
            h = e.nodeType && At(e),
            m = it._data(e, "fxshow");
            n.queue || (s = it._queueHooks(e, "fx"), null == s.unqueued && (s.unqueued = 0, l = s.empty.fire, s.empty.fire = function() {
                s.unqueued || l()
            }), s.unqueued++, d.always(function() {
                d.always(function() {
                    s.unqueued--,
                    it.queue(e, "fx").length || s.empty.fire()
                })
            })),
            1 === e.nodeType && ("height" in t || "width" in t) && (n.overflow = [p.overflow, p.overflowX, p.overflowY], u = it.css(e, "display"), c = "none" === u ? it._data(e, "olddisplay") || E(e.nodeName) : u, "inline" === c && "none" === it.css(e, "float") && (nt.inlineBlockNeedsLayout && "inline" !== E(e.nodeName) ? p.zoom = 1 : p.display = "inline-block")),
            n.overflow && (p.overflow = "hidden", nt.shrinkWrapBlocks() || d.always(function() {
                p.overflow = n.overflow[0],
                p.overflowX = n.overflow[1],
                p.overflowY = n.overflow[2]
            }));
            for (r in t) if (i = t[r], gn.exec(i)) {
                if (delete t[r], o = o || "toggle" === i, i === (h ? "hide": "show")) {
                    if ("show" !== i || !m || void 0 === m[r]) continue;
                    h = !0
                }
                f[r] = m && m[r] || it.style(e, r)
            } else u = void 0;
            if (it.isEmptyObject(f))"inline" === ("none" === u ? E(e.nodeName) : u) && (p.display = u);
            else {
                m ? "hidden" in m && (h = m.hidden) : m = it._data(e, "fxshow", {}),
                o && (m.hidden = !h),
                h ? it(e).show() : d.done(function() {
                    it(e).hide()
                }),
                d.done(function() {
                    var t;
                    it._removeData(e, "fxshow");
                    for (t in f) it.style(e, t, f[t])
                });
                for (r in f) a = M(h ? m[r] : 0, r, d),
                r in m || (m[r] = a.start, h && (a.end = a.start, a.start = "width" === r || "height" === r ? 1 : 0))
            }
        }
        function F(e, t) {
            var n, r, i, o, a;
            for (n in e) if (r = it.camelCase(n), i = t[r], o = e[n], it.isArray(o) && (i = o[1], o = e[n] = o[0]), n !== r && (e[r] = o, delete e[n]), a = it.cssHooks[r], a && "expand" in a) {
                o = a.expand(o),
                delete e[r];
                for (n in o) n in e || (e[n] = o[n], t[n] = i)
            } else t[r] = i
        }
        function B(e, t, n) {
            var r, i, o = 0,
            a = bn.length,
            s = it.Deferred().always(function() {
                delete l.elem
            }),
            l = function() {
                if (i) return ! 1;
                for (var t = hn || q(), n = Math.max(0, u.startTime + u.duration - t), r = n / u.duration || 0, o = 1 - r, a = 0, l = u.tweens.length; l > a; a++) u.tweens[a].run(o);
                return s.notifyWith(e, [u, o, n]),
                1 > o && l ? n: (s.resolveWith(e, [u]), !1)
            },
            u = s.promise({
                elem: e,
                props: it.extend({},
                t),
                opts: it.extend(!0, {
                    specialEasing: {}
                },
                n),
                originalProperties: t,
                originalOptions: n,
                startTime: hn || q(),
                duration: n.duration,
                tweens: [],
                createTween: function(t, n) {
                    var r = it.Tween(e, u.opts, t, n, u.opts.specialEasing[t] || u.opts.easing);
                    return u.tweens.push(r),
                    r
                },
                stop: function(t) {
                    var n = 0,
                    r = t ? u.tweens.length: 0;
                    if (i) return this;
                    for (i = !0; r > n; n++) u.tweens[n].run(1);
                    return t ? s.resolveWith(e, [u, t]) : s.rejectWith(e, [u, t]),
                    this
                }
            }),
            c = u.props;
            for (F(c, u.opts.specialEasing); a > o; o++) if (r = bn[o].call(u, e, c, u.opts)) return r;
            return it.map(c, M, u),
            it.isFunction(u.opts.start) && u.opts.start.call(e, u),
            it.fx.timer(it.extend(l, {
                elem: e,
                anim: u,
                queue: u.opts.queue
            })),
            u.progress(u.opts.progress).done(u.opts.done, u.opts.complete).fail(u.opts.fail).always(u.opts.always)
        }
        function P(e) {
            return function(t, n) {
                "string" != typeof t && (n = t, t = "*");
                var r, i = 0,
                o = t.toLowerCase().match(bt) || [];
                if (it.isFunction(n)) for (; r = o[i++];)"+" === r.charAt(0) ? (r = r.slice(1) || "*", (e[r] = e[r] || []).unshift(n)) : (e[r] = e[r] || []).push(n)
            }
        }
        function R(e, t, n, r) {
            function i(s) {
                var l;
                return o[s] = !0,
                it.each(e[s] || [],
                function(e, s) {
                    var u = s(t, n, r);
                    return "string" != typeof u || a || o[u] ? a ? !(l = u) : void 0 : (t.dataTypes.unshift(u), i(u), !1)
                }),
                l
            }
            var o = {},
            a = e === In;
            return i(t.dataTypes[0]) || !o["*"] && i("*")
        }
        function W(e, t) {
            var n, r, i = it.ajaxSettings.flatOptions || {};
            for (r in t) void 0 !== t[r] && ((i[r] ? e: n || (n = {}))[r] = t[r]);
            return n && it.extend(!0, e, n),
            e
        }
        function $(e, t, n) {
            for (var r, i, o, a, s = e.contents,
            l = e.dataTypes;
            "*" === l[0];) l.shift(),
            void 0 === i && (i = e.mimeType || t.getResponseHeader("Content-Type"));
            if (i) for (a in s) if (s[a] && s[a].test(i)) {
                l.unshift(a);
                break
            }
            if (l[0] in n) o = l[0];
            else {
                for (a in n) {
                    if (!l[0] || e.converters[a + " " + l[0]]) {
                        o = a;
                        break
                    }
                    r || (r = a)
                }
                o = o || r
            }
            return o ? (o !== l[0] && l.unshift(o), n[o]) : void 0
        }
        function z(e, t, n, r) {
            var i, o, a, s, l, u = {},
            c = e.dataTypes.slice();
            if (c[1]) for (a in e.converters) u[a.toLowerCase()] = e.converters[a];
            for (o = c.shift(); o;) if (e.responseFields[o] && (n[e.responseFields[o]] = t), !l && r && e.dataFilter && (t = e.dataFilter(t, e.dataType)), l = o, o = c.shift()) if ("*" === o) o = l;
            else if ("*" !== l && l !== o) {
                if (a = u[l + " " + o] || u["* " + o], !a) for (i in u) if (s = i.split(" "), s[1] === o && (a = u[l + " " + s[0]] || u["* " + s[0]])) {
                    a === !0 ? a = u[i] : u[i] !== !0 && (o = s[0], c.unshift(s[1]));
                    break
                }
                if (a !== !0) if (a && e["throws"]) t = a(t);
                else try {
                    t = a(t)
                } catch(d) {
                    return {
                        state: "parsererror",
                        error: a ? d: "No conversion from " + l + " to " + o
                    }
                }
            }
            return {
                state: "success",
                data: t
            }
        }
        function I(e, t, n, r) {
            var i;
            if (it.isArray(t)) it.each(t,
            function(t, i) {
                n || Jn.test(e) ? r(e, i) : I(e + "[" + ("object" == typeof i ? t: "") + "]", i, n, r)
            });
            else if (n || "object" !== it.type(t)) r(e, t);
            else for (i in t) I(e + "[" + i + "]", t[i], n, r)
        }
        function X() {
            try {
                return new e.XMLHttpRequest
            } catch(t) {}
        }
        function U() {
            try {
                return new e.ActiveXObject("Microsoft.XMLHTTP")
            } catch(t) {}
        }
        function V(e) {
            return it.isWindow(e) ? e: 9 === e.nodeType ? e.defaultView || e.parentWindow: !1
        }
        var J = [],
        Y = J.slice,
        G = J.concat,
        Q = J.push,
        K = J.indexOf,
        Z = {},
        et = Z.toString,
        tt = Z.hasOwnProperty,
        nt = {},
        rt = "1.11.1",
        it = function(e, t) {
            return new it.fn.init(e, t)
        },
        ot = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,
        at = /^-ms-/,
        st = /-([\da-z])/gi,
        lt = function(e, t) {
            return t.toUpperCase()
        };
        it.fn = it.prototype = {
            jquery: rt,
            constructor: it,
            selector: "",
            length: 0,
            toArray: function() {
                return Y.call(this)
            },
            get: function(e) {
                return null != e ? 0 > e ? this[e + this.length] : this[e] : Y.call(this)
            },
            pushStack: function(e) {
                var t = it.merge(this.constructor(), e);
                return t.prevObject = this,
                t.context = this.context,
                t
            },
            each: function(e, t) {
                return it.each(this, e, t)
            },
            map: function(e) {
                return this.pushStack(it.map(this,
                function(t, n) {
                    return e.call(t, n, t)
                }))
            },
            slice: function() {
                return this.pushStack(Y.apply(this, arguments))
            },
            first: function() {
                return this.eq(0)
            },
            last: function() {
                return this.eq( - 1)
            },
            eq: function(e) {
                var t = this.length,
                n = +e + (0 > e ? t: 0);
                return this.pushStack(n >= 0 && t > n ? [this[n]] : [])
            },
            end: function() {
                return this.prevObject || this.constructor(null)
            },
            push: Q,
            sort: J.sort,
            splice: J.splice
        },
        it.extend = it.fn.extend = function() {
            var e, t, n, r, i, o, a = arguments[0] || {},
            s = 1,
            l = arguments.length,
            u = !1;
            for ("boolean" == typeof a && (u = a, a = arguments[s] || {},
            s++), "object" == typeof a || it.isFunction(a) || (a = {}), s === l && (a = this, s--); l > s; s++) if (null != (i = arguments[s])) for (r in i) e = a[r],
            n = i[r],
            a !== n && (u && n && (it.isPlainObject(n) || (t = it.isArray(n))) ? (t ? (t = !1, o = e && it.isArray(e) ? e: []) : o = e && it.isPlainObject(e) ? e: {},
            a[r] = it.extend(u, o, n)) : void 0 !== n && (a[r] = n));
            return a
        },
        it.extend({
            expando: "jQuery" + (rt + Math.random()).replace(/\D/g, ""),
            isReady: !0,
            error: function(e) {
                throw new Error(e)
            },
            noop: function() {},
            isFunction: function(e) {
                return "function" === it.type(e)
            },
            isArray: Array.isArray ||
            function(e) {
                return "array" === it.type(e)
            },
            isWindow: function(e) {
                return null != e && e == e.window
            },
            isNumeric: function(e) {
                return ! it.isArray(e) && e - parseFloat(e) >= 0
            },
            isEmptyObject: function(e) {
                var t;
                for (t in e) return ! 1;
                return ! 0
            },
            isPlainObject: function(e) {
                var t;
                if (!e || "object" !== it.type(e) || e.nodeType || it.isWindow(e)) return ! 1;
                try {
                    if (e.constructor && !tt.call(e, "constructor") && !tt.call(e.constructor.prototype, "isPrototypeOf")) return ! 1
                } catch(n) {
                    return ! 1
                }
                if (nt.ownLast) for (t in e) return tt.call(e, t);
                for (t in e);
                return void 0 === t || tt.call(e, t)
            },
            type: function(e) {
                return null == e ? e + "": "object" == typeof e || "function" == typeof e ? Z[et.call(e)] || "object": typeof e
            },
            globalEval: function(t) {
                t && it.trim(t) && (e.execScript ||
                function(t) {
                    e.eval.call(e, t)
                })(t)
            },
            camelCase: function(e) {
                return e.replace(at, "ms-").replace(st, lt)
            },
            nodeName: function(e, t) {
                return e.nodeName && e.nodeName.toLowerCase() === t.toLowerCase()
            },
            each: function(e, t, r) {
                var i, o = 0,
                a = e.length,
                s = n(e);
                if (r) {
                    if (s) for (; a > o && (i = t.apply(e[o], r), i !== !1); o++);
                    else for (o in e) if (i = t.apply(e[o], r), i === !1) break
                } else if (s) for (; a > o && (i = t.call(e[o], o, e[o]), i !== !1); o++);
                else for (o in e) if (i = t.call(e[o], o, e[o]), i === !1) break;
                return e
            },
            trim: function(e) {
                return null == e ? "": (e + "").replace(ot, "")
            },
            makeArray: function(e, t) {
                var r = t || [];
                return null != e && (n(Object(e)) ? it.merge(r, "string" == typeof e ? [e] : e) : Q.call(r, e)),
                r
            },
            inArray: function(e, t, n) {
                var r;
                if (t) {
                    if (K) return K.call(t, e, n);
                    for (r = t.length, n = n ? 0 > n ? Math.max(0, r + n) : n: 0; r > n; n++) if (n in t && t[n] === e) return n
                }
                return - 1
            },
            merge: function(e, t) {
                for (var n = +t.length,
                r = 0,
                i = e.length; n > r;) e[i++] = t[r++];
                if (n !== n) for (; void 0 !== t[r];) e[i++] = t[r++];
                return e.length = i,
                e
            },
            grep: function(e, t, n) {
                for (var r, i = [], o = 0, a = e.length, s = !n; a > o; o++) r = !t(e[o], o),
                r !== s && i.push(e[o]);
                return i
            },
            map: function(e, t, r) {
                var i, o = 0,
                a = e.length,
                s = n(e),
                l = [];
                if (s) for (; a > o; o++) i = t(e[o], o, r),
                null != i && l.push(i);
                else for (o in e) i = t(e[o], o, r),
                null != i && l.push(i);
                return G.apply([], l)
            },
            guid: 1,
            proxy: function(e, t) {
                var n, r, i;
                return "string" == typeof t && (i = e[t], t = e, e = i),
                it.isFunction(e) ? (n = Y.call(arguments, 2), r = function() {
                    return e.apply(t || this, n.concat(Y.call(arguments)))
                },
                r.guid = e.guid = e.guid || it.guid++, r) : void 0
            },
            now: function() {
                return + new Date
            },
            support: nt
        }),
        it.each("Boolean Number String Function Array Date RegExp Object Error".split(" "),
        function(e, t) {
            Z["[object " + t + "]"] = t.toLowerCase()
        });
        var ut = function(e) {
            function t(e, t, n, r) {
                var i, o, a, s, l, u, d, p, h, m;
                if ((t ? t.ownerDocument || t: R) !== H && L(t), t = t || H, n = n || [], !e || "string" != typeof e) return n;
                if (1 !== (s = t.nodeType) && 9 !== s) return [];
                if (_ && !r) {
                    if (i = yt.exec(e)) if (a = i[1]) {
                        if (9 === s) {
                            if (o = t.getElementById(a), !o || !o.parentNode) return n;
                            if (o.id === a) return n.push(o),
                            n
                        } else if (t.ownerDocument && (o = t.ownerDocument.getElementById(a)) && B(t, o) && o.id === a) return n.push(o),
                        n
                    } else {
                        if (i[2]) return Z.apply(n, t.getElementsByTagName(e)),
                        n;
                        if ((a = i[3]) && w.getElementsByClassName && t.getElementsByClassName) return Z.apply(n, t.getElementsByClassName(a)),
                        n
                    }
                    if (w.qsa && (!M || !M.test(e))) {
                        if (p = d = P, h = t, m = 9 === s && e, 1 === s && "object" !== t.nodeName.toLowerCase()) {
                            for (u = E(e), (d = t.getAttribute("id")) ? p = d.replace(xt, "\\$&") : t.setAttribute("id", p), p = "[id='" + p + "'] ", l = u.length; l--;) u[l] = p + f(u[l]);
                            h = bt.test(e) && c(t.parentNode) || t,
                            m = u.join(",")
                        }
                        if (m) try {
                            return Z.apply(n, h.querySelectorAll(m)),
                            n
                        } catch(g) {} finally {
                            d || t.removeAttribute("id")
                        }
                    }
                }
                return S(e.replace(lt, "$1"), t, n, r)
            }
            function n() {
                function e(n, r) {
                    return t.push(n + " ") > T.cacheLength && delete e[t.shift()],
                    e[n + " "] = r
                }
                var t = [];
                return e
            }
            function r(e) {
                return e[P] = !0,
                e
            }
            function i(e) {
                var t = H.createElement("div");
                try {
                    return !! e(t)
                } catch(n) {
                    return ! 1
                } finally {
                    t.parentNode && t.parentNode.removeChild(t),
                    t = null
                }
            }
            function o(e, t) {
                for (var n = e.split("|"), r = e.length; r--;) T.attrHandle[n[r]] = t
            }
            function a(e, t) {
                var n = t && e,
                r = n && 1 === e.nodeType && 1 === t.nodeType && (~t.sourceIndex || J) - (~e.sourceIndex || J);
                if (r) return r;
                if (n) for (; n = n.nextSibling;) if (n === t) return - 1;
                return e ? 1 : -1
            }
            function s(e) {
                return function(t) {
                    var n = t.nodeName.toLowerCase();
                    return "input" === n && t.type === e
                }
            }
            function l(e) {
                return function(t) {
                    var n = t.nodeName.toLowerCase();
                    return ("input" === n || "button" === n) && t.type === e
                }
            }
            function u(e) {
                return r(function(t) {
                    return t = +t,
                    r(function(n, r) {
                        for (var i, o = e([], n.length, t), a = o.length; a--;) n[i = o[a]] && (n[i] = !(r[i] = n[i]))
                    })
                })
            }
            function c(e) {
                return e && typeof e.getElementsByTagName !== V && e
            }
            function d() {}
            function f(e) {
                for (var t = 0,
                n = e.length,
                r = ""; n > t; t++) r += e[t].value;
                return r
            }
            function p(e, t, n) {
                var r = t.dir,
                i = n && "parentNode" === r,
                o = $++;
                return t.first ?
                function(t, n, o) {
                    for (; t = t[r];) if (1 === t.nodeType || i) return e(t, n, o)
                }: function(t, n, a) {
                    var s, l, u = [W, o];
                    if (a) {
                        for (; t = t[r];) if ((1 === t.nodeType || i) && e(t, n, a)) return ! 0
                    } else for (; t = t[r];) if (1 === t.nodeType || i) {
                        if (l = t[P] || (t[P] = {}), (s = l[r]) && s[0] === W && s[1] === o) return u[2] = s[2];
                        if (l[r] = u, u[2] = e(t, n, a)) return ! 0
                    }
                }
            }
            function h(e) {
                return e.length > 1 ?
                function(t, n, r) {
                    for (var i = e.length; i--;) if (!e[i](t, n, r)) return ! 1;
                    return ! 0
                }: e[0]
            }
            function m(e, n, r) {
                for (var i = 0,
                o = n.length; o > i; i++) t(e, n[i], r);
                return r
            }
            function g(e, t, n, r, i) {
                for (var o, a = [], s = 0, l = e.length, u = null != t; l > s; s++)(o = e[s]) && (!n || n(o, r, i)) && (a.push(o), u && t.push(s));
                return a
            }
            function v(e, t, n, i, o, a) {
                return i && !i[P] && (i = v(i)),
                o && !o[P] && (o = v(o, a)),
                r(function(r, a, s, l) {
                    var u, c, d, f = [],
                    p = [],
                    h = a.length,
                    v = r || m(t || "*", s.nodeType ? [s] : s, []),
                    y = !e || !r && t ? v: g(v, f, e, s, l),
                    b = n ? o || (r ? e: h || i) ? [] : a: y;
                    if (n && n(y, b, s, l), i) for (u = g(b, p), i(u, [], s, l), c = u.length; c--;)(d = u[c]) && (b[p[c]] = !(y[p[c]] = d));
                    if (r) {
                        if (o || e) {
                            if (o) {
                                for (u = [], c = b.length; c--;)(d = b[c]) && u.push(y[c] = d);
                                o(null, b = [], u, l)
                            }
                            for (c = b.length; c--;)(d = b[c]) && (u = o ? tt.call(r, d) : f[c]) > -1 && (r[u] = !(a[u] = d))
                        }
                    } else b = g(b === a ? b.splice(h, b.length) : b),
                    o ? o(null, a, b, l) : Z.apply(a, b)
                })
            }
            function y(e) {
                for (var t, n, r, i = e.length,
                o = T.relative[e[0].type], a = o || T.relative[" "], s = o ? 1 : 0, l = p(function(e) {
                    return e === t
                },
                a, !0), u = p(function(e) {
                    return tt.call(t, e) > -1
                },
                a, !0), c = [function(e, n, r) {
                    return ! o && (r || n !== A) || ((t = n).nodeType ? l(e, n, r) : u(e, n, r))
                }]; i > s; s++) if (n = T.relative[e[s].type]) c = [p(h(c), n)];
                else {
                    if (n = T.filter[e[s].type].apply(null, e[s].matches), n[P]) {
                        for (r = ++s; i > r && !T.relative[e[r].type]; r++);
                        return v(s > 1 && h(c), s > 1 && f(e.slice(0, s - 1).concat({
                            value: " " === e[s - 2].type ? "*": ""
                        })).replace(lt, "$1"), n, r > s && y(e.slice(s, r)), i > r && y(e = e.slice(r)), i > r && f(e))
                    }
                    c.push(n)
                }
                return h(c)
            }
            function b(e, n) {
                var i = n.length > 0,
                o = e.length > 0,
                a = function(r, a, s, l, u) {
                    var c, d, f, p = 0,
                    h = "0",
                    m = r && [],
                    v = [],
                    y = A,
                    b = r || o && T.find.TAG("*", u),
                    x = W += null == y ? 1 : Math.random() || .1,
                    w = b.length;
                    for (u && (A = a !== H && a); h !== w && null != (c = b[h]); h++) {
                        if (o && c) {
                            for (d = 0; f = e[d++];) if (f(c, a, s)) {
                                l.push(c);
                                break
                            }
                            u && (W = x)
                        }
                        i && ((c = !f && c) && p--, r && m.push(c))
                    }
                    if (p += h, i && h !== p) {
                        for (d = 0; f = n[d++];) f(m, v, a, s);
                        if (r) {
                            if (p > 0) for (; h--;) m[h] || v[h] || (v[h] = Q.call(l));
                            v = g(v)
                        }
                        Z.apply(l, v),
                        u && !r && v.length > 0 && p + n.length > 1 && t.uniqueSort(l)
                    }
                    return u && (W = x, A = y),
                    m
                };
                return i ? r(a) : a
            }
            var x, w, T, C, N, E, k, S, A, j, D, L, H, q, _, M, O, F, B, P = "sizzle" + -new Date,
            R = e.document,
            W = 0,
            $ = 0,
            z = n(),
            I = n(),
            X = n(),
            U = function(e, t) {
                return e === t && (D = !0),
                0
            },
            V = "undefined",
            J = 1 << 31,
            Y = {}.hasOwnProperty,
            G = [],
            Q = G.pop,
            K = G.push,
            Z = G.push,
            et = G.slice,
            tt = G.indexOf ||
            function(e) {
                for (var t = 0,
                n = this.length; n > t; t++) if (this[t] === e) return t;
                return - 1
            },
            nt = "checked|selected|async|autofocus|autoplay|controls|defer|disabled|hidden|ismap|loop|multiple|open|readonly|required|scoped",
            rt = "[\\x20\\t\\r\\n\\f]",
            it = "(?:\\\\.|[\\w-]|[^\\x00-\\xa0])+",
            ot = it.replace("w", "w#"),
            at = "\\[" + rt + "*(" + it + ")(?:" + rt + "*([*^$|!~]?=)" + rt + "*(?:'((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\"|(" + ot + "))|)" + rt + "*\\]",
            st = ":(" + it + ")(?:\\((('((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\")|((?:\\\\.|[^\\\\()[\\]]|" + at + ")*)|.*)\\)|)",
            lt = new RegExp("^" + rt + "+|((?:^|[^\\\\])(?:\\\\.)*)" + rt + "+$", "g"),
            ut = new RegExp("^" + rt + "*," + rt + "*"),
            ct = new RegExp("^" + rt + "*([>+~]|" + rt + ")" + rt + "*"),
            dt = new RegExp("=" + rt + "*([^\\]'\"]*?)" + rt + "*\\]", "g"),
            ft = new RegExp(st),
            pt = new RegExp("^" + ot + "$"),
            ht = {
                ID: new RegExp("^#(" + it + ")"),
                CLASS: new RegExp("^\\.(" + it + ")"),
                TAG: new RegExp("^(" + it.replace("w", "w*") + ")"),
                ATTR: new RegExp("^" + at),
                PSEUDO: new RegExp("^" + st),
                CHILD: new RegExp("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\(" + rt + "*(even|odd|(([+-]|)(\\d*)n|)" + rt + "*(?:([+-]|)" + rt + "*(\\d+)|))" + rt + "*\\)|)", "i"),
                bool: new RegExp("^(?:" + nt + ")$", "i"),
                needsContext: new RegExp("^" + rt + "*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\(" + rt + "*((?:-\\d)?\\d*)" + rt + "*\\)|)(?=[^-]|$)", "i")
            },
            mt = /^(?:input|select|textarea|button)$/i,
            gt = /^h\d$/i,
            vt = /^[^{]+\{\s*\[native \w/,
            yt = /^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/,
            bt = /[+~]/,
            xt = /'|\\/g,
            wt = new RegExp("\\\\([\\da-f]{1,6}" + rt + "?|(" + rt + ")|.)", "ig"),
            Tt = function(e, t, n) {
                var r = "0x" + t - 65536;
                return r !== r || n ? t: 0 > r ? String.fromCharCode(r + 65536) : String.fromCharCode(r >> 10 | 55296, 1023 & r | 56320)
            };
            try {
                Z.apply(G = et.call(R.childNodes), R.childNodes),
                G[R.childNodes.length].nodeType
            } catch(Ct) {
                Z = {
                    apply: G.length ?
                    function(e, t) {
                        K.apply(e, et.call(t))
                    }: function(e, t) {
                        for (var n = e.length,
                        r = 0; e[n++] = t[r++];);
                        e.length = n - 1
                    }
                }
            }
            w = t.support = {},
            N = t.isXML = function(e) {
                var t = e && (e.ownerDocument || e).documentElement;
                return t ? "HTML" !== t.nodeName: !1
            },
            L = t.setDocument = function(e) {
                var t, n = e ? e.ownerDocument || e: R,
                r = n.defaultView;
                return n !== H && 9 === n.nodeType && n.documentElement ? (H = n, q = n.documentElement, _ = !N(n), r && r !== r.top && (r.addEventListener ? r.addEventListener("unload",
                function() {
                    L()
                },
                !1) : r.attachEvent && r.attachEvent("onunload",
                function() {
                    L()
                })), w.attributes = i(function(e) {
                    return e.className = "i",
                    !e.getAttribute("className")
                }), w.getElementsByTagName = i(function(e) {
                    return e.appendChild(n.createComment("")),
                    !e.getElementsByTagName("*").length
                }), w.getElementsByClassName = vt.test(n.getElementsByClassName) && i(function(e) {
                    return e.innerHTML = "<div class='a'></div><div class='a i'></div>",
                    e.firstChild.className = "i",
                    2 === e.getElementsByClassName("i").length
                }), w.getById = i(function(e) {
                    return q.appendChild(e).id = P,
                    !n.getElementsByName || !n.getElementsByName(P).length
                }), w.getById ? (T.find.ID = function(e, t) {
                    if (typeof t.getElementById !== V && _) {
                        var n = t.getElementById(e);
                        return n && n.parentNode ? [n] : []
                    }
                },
                T.filter.ID = function(e) {
                    var t = e.replace(wt, Tt);
                    return function(e) {
                        return e.getAttribute("id") === t
                    }
                }) : (delete T.find.ID, T.filter.ID = function(e) {
                    var t = e.replace(wt, Tt);
                    return function(e) {
                        var n = typeof e.getAttributeNode !== V && e.getAttributeNode("id");
                        return n && n.value === t
                    }
                }), T.find.TAG = w.getElementsByTagName ?
                function(e, t) {
                    return typeof t.getElementsByTagName !== V ? t.getElementsByTagName(e) : void 0
                }: function(e, t) {
                    var n, r = [],
                    i = 0,
                    o = t.getElementsByTagName(e);
                    if ("*" === e) {
                        for (; n = o[i++];) 1 === n.nodeType && r.push(n);
                        return r
                    }
                    return o
                },
                T.find.CLASS = w.getElementsByClassName &&
                function(e, t) {
                    return typeof t.getElementsByClassName !== V && _ ? t.getElementsByClassName(e) : void 0
                },
                O = [], M = [], (w.qsa = vt.test(n.querySelectorAll)) && (i(function(e) {
                    e.innerHTML = "<select msallowclip=''><option selected=''></option></select>",
                    e.querySelectorAll("[msallowclip^='']").length && M.push("[*^$]=" + rt + "*(?:''|\"\")"),
                    e.querySelectorAll("[selected]").length || M.push("\\[" + rt + "*(?:value|" + nt + ")"),
                    e.querySelectorAll(":checked").length || M.push(":checked")
                }), i(function(e) {
                    var t = n.createElement("input");
                    t.setAttribute("type", "hidden"),
                    e.appendChild(t).setAttribute("name", "D"),
                    e.querySelectorAll("[name=d]").length && M.push("name" + rt + "*[*^$|!~]?="),
                    e.querySelectorAll(":enabled").length || M.push(":enabled", ":disabled"),
                    e.querySelectorAll("*,:x"),
                    M.push(",.*:")
                })), (w.matchesSelector = vt.test(F = q.matches || q.webkitMatchesSelector || q.mozMatchesSelector || q.oMatchesSelector || q.msMatchesSelector)) && i(function(e) {
                    w.disconnectedMatch = F.call(e, "div"),
                    F.call(e, "[s!='']:x"),
                    O.push("!=", st)
                }), M = M.length && new RegExp(M.join("|")), O = O.length && new RegExp(O.join("|")), t = vt.test(q.compareDocumentPosition), B = t || vt.test(q.contains) ?
                function(e, t) {
                    var n = 9 === e.nodeType ? e.documentElement: e,
                    r = t && t.parentNode;
                    return e === r || !(!r || 1 !== r.nodeType || !(n.contains ? n.contains(r) : e.compareDocumentPosition && 16 & e.compareDocumentPosition(r)))
                }: function(e, t) {
                    if (t) for (; t = t.parentNode;) if (t === e) return ! 0;
                    return ! 1
                },
                U = t ?
                function(e, t) {
                    if (e === t) return D = !0,
                    0;
                    var r = !e.compareDocumentPosition - !t.compareDocumentPosition;
                    return r ? r: (r = (e.ownerDocument || e) === (t.ownerDocument || t) ? e.compareDocumentPosition(t) : 1, 1 & r || !w.sortDetached && t.compareDocumentPosition(e) === r ? e === n || e.ownerDocument === R && B(R, e) ? -1 : t === n || t.ownerDocument === R && B(R, t) ? 1 : j ? tt.call(j, e) - tt.call(j, t) : 0 : 4 & r ? -1 : 1)
                }: function(e, t) {
                    if (e === t) return D = !0,
                    0;
                    var r, i = 0,
                    o = e.parentNode,
                    s = t.parentNode,
                    l = [e],
                    u = [t];
                    if (!o || !s) return e === n ? -1 : t === n ? 1 : o ? -1 : s ? 1 : j ? tt.call(j, e) - tt.call(j, t) : 0;
                    if (o === s) return a(e, t);
                    for (r = e; r = r.parentNode;) l.unshift(r);
                    for (r = t; r = r.parentNode;) u.unshift(r);
                    for (; l[i] === u[i];) i++;
                    return i ? a(l[i], u[i]) : l[i] === R ? -1 : u[i] === R ? 1 : 0
                },
                n) : H
            },
            t.matches = function(e, n) {
                return t(e, null, null, n)
            },
            t.matchesSelector = function(e, n) {
                if ((e.ownerDocument || e) !== H && L(e), n = n.replace(dt, "='$1']"), !(!w.matchesSelector || !_ || O && O.test(n) || M && M.test(n))) try {
                    var r = F.call(e, n);
                    if (r || w.disconnectedMatch || e.document && 11 !== e.document.nodeType) return r
                } catch(i) {}
                return t(n, H, null, [e]).length > 0
            },
            t.contains = function(e, t) {
                return (e.ownerDocument || e) !== H && L(e),
                B(e, t)
            },
            t.attr = function(e, t) { (e.ownerDocument || e) !== H && L(e);
                var n = T.attrHandle[t.toLowerCase()],
                r = n && Y.call(T.attrHandle, t.toLowerCase()) ? n(e, t, !_) : void 0;
                return void 0 !== r ? r: w.attributes || !_ ? e.getAttribute(t) : (r = e.getAttributeNode(t)) && r.specified ? r.value: null
            },
            t.error = function(e) {
                throw new Error("Syntax error, unrecognized expression: " + e)
            },
            t.uniqueSort = function(e) {
                var t, n = [],
                r = 0,
                i = 0;
                if (D = !w.detectDuplicates, j = !w.sortStable && e.slice(0), e.sort(U), D) {
                    for (; t = e[i++];) t === e[i] && (r = n.push(i));
                    for (; r--;) e.splice(n[r], 1)
                }
                return j = null,
                e
            },
            C = t.getText = function(e) {
                var t, n = "",
                r = 0,
                i = e.nodeType;
                if (i) {
                    if (1 === i || 9 === i || 11 === i) {
                        if ("string" == typeof e.textContent) return e.textContent;
                        for (e = e.firstChild; e; e = e.nextSibling) n += C(e)
                    } else if (3 === i || 4 === i) return e.nodeValue
                } else for (; t = e[r++];) n += C(t);
                return n
            },
            T = t.selectors = {
                cacheLength: 50,
                createPseudo: r,
                match: ht,
                attrHandle: {},
                find: {},
                relative: {
                    ">": {
                        dir: "parentNode",
                        first: !0
                    },
                    " ": {
                        dir: "parentNode"
                    },
                    "+": {
                        dir: "previousSibling",
                        first: !0
                    },
                    "~": {
                        dir: "previousSibling"
                    }
                },
                preFilter: {
                    ATTR: function(e) {
                        return e[1] = e[1].replace(wt, Tt),
                        e[3] = (e[3] || e[4] || e[5] || "").replace(wt, Tt),
                        "~=" === e[2] && (e[3] = " " + e[3] + " "),
                        e.slice(0, 4)
                    },
                    CHILD: function(e) {
                        return e[1] = e[1].toLowerCase(),
                        "nth" === e[1].slice(0, 3) ? (e[3] || t.error(e[0]), e[4] = +(e[4] ? e[5] + (e[6] || 1) : 2 * ("even" === e[3] || "odd" === e[3])), e[5] = +(e[7] + e[8] || "odd" === e[3])) : e[3] && t.error(e[0]),
                        e
                    },
                    PSEUDO: function(e) {
                        var t, n = !e[6] && e[2];
                        return ht.CHILD.test(e[0]) ? null: (e[3] ? e[2] = e[4] || e[5] || "": n && ft.test(n) && (t = E(n, !0)) && (t = n.indexOf(")", n.length - t) - n.length) && (e[0] = e[0].slice(0, t), e[2] = n.slice(0, t)), e.slice(0, 3))
                    }
                },
                filter: {
                    TAG: function(e) {
                        var t = e.replace(wt, Tt).toLowerCase();
                        return "*" === e ?
                        function() {
                            return ! 0
                        }: function(e) {
                            return e.nodeName && e.nodeName.toLowerCase() === t
                        }
                    },
                    CLASS: function(e) {
                        var t = z[e + " "];
                        return t || (t = new RegExp("(^|" + rt + ")" + e + "(" + rt + "|$)")) && z(e,
                        function(e) {
                            return t.test("string" == typeof e.className && e.className || typeof e.getAttribute !== V && e.getAttribute("class") || "")
                        })
                    },
                    ATTR: function(e, n, r) {
                        return function(i) {
                            var o = t.attr(i, e);
                            return null == o ? "!=" === n: n ? (o += "", "=" === n ? o === r: "!=" === n ? o !== r: "^=" === n ? r && 0 === o.indexOf(r) : "*=" === n ? r && o.indexOf(r) > -1 : "$=" === n ? r && o.slice( - r.length) === r: "~=" === n ? (" " + o + " ").indexOf(r) > -1 : "|=" === n ? o === r || o.slice(0, r.length + 1) === r + "-": !1) : !0
                        }
                    },
                    CHILD: function(e, t, n, r, i) {
                        var o = "nth" !== e.slice(0, 3),
                        a = "last" !== e.slice( - 4),
                        s = "of-type" === t;
                        return 1 === r && 0 === i ?
                        function(e) {
                            return !! e.parentNode
                        }: function(t, n, l) {
                            var u, c, d, f, p, h, m = o !== a ? "nextSibling": "previousSibling",
                            g = t.parentNode,
                            v = s && t.nodeName.toLowerCase(),
                            y = !l && !s;
                            if (g) {
                                if (o) {
                                    for (; m;) {
                                        for (d = t; d = d[m];) if (s ? d.nodeName.toLowerCase() === v: 1 === d.nodeType) return ! 1;
                                        h = m = "only" === e && !h && "nextSibling"
                                    }
                                    return ! 0
                                }
                                if (h = [a ? g.firstChild: g.lastChild], a && y) {
                                    for (c = g[P] || (g[P] = {}), u = c[e] || [], p = u[0] === W && u[1], f = u[0] === W && u[2], d = p && g.childNodes[p]; d = ++p && d && d[m] || (f = p = 0) || h.pop();) if (1 === d.nodeType && ++f && d === t) {
                                        c[e] = [W, p, f];
                                        break
                                    }
                                } else if (y && (u = (t[P] || (t[P] = {}))[e]) && u[0] === W) f = u[1];
                                else for (; (d = ++p && d && d[m] || (f = p = 0) || h.pop()) && ((s ? d.nodeName.toLowerCase() !== v: 1 !== d.nodeType) || !++f || (y && ((d[P] || (d[P] = {}))[e] = [W, f]), d !== t)););
                                return f -= i,
                                f === r || f % r === 0 && f / r >= 0
                            }
                        }
                    },
                    PSEUDO: function(e, n) {
                        var i, o = T.pseudos[e] || T.setFilters[e.toLowerCase()] || t.error("unsupported pseudo: " + e);
                        return o[P] ? o(n) : o.length > 1 ? (i = [e, e, "", n], T.setFilters.hasOwnProperty(e.toLowerCase()) ? r(function(e, t) {
                            for (var r, i = o(e, n), a = i.length; a--;) r = tt.call(e, i[a]),
                            e[r] = !(t[r] = i[a])
                        }) : function(e) {
                            return o(e, 0, i)
                        }) : o
                    }
                },
                pseudos: {
                    not: r(function(e) {
                        var t = [],
                        n = [],
                        i = k(e.replace(lt, "$1"));
                        return i[P] ? r(function(e, t, n, r) {
                            for (var o, a = i(e, null, r, []), s = e.length; s--;)(o = a[s]) && (e[s] = !(t[s] = o))
                        }) : function(e, r, o) {
                            return t[0] = e,
                            i(t, null, o, n),
                            !n.pop()
                        }
                    }),
                    has: r(function(e) {
                        return function(n) {
                            return t(e, n).length > 0
                        }
                    }),
                    contains: r(function(e) {
                        return function(t) {
                            return (t.textContent || t.innerText || C(t)).indexOf(e) > -1
                        }
                    }),
                    lang: r(function(e) {
                        return pt.test(e || "") || t.error("unsupported lang: " + e),
                        e = e.replace(wt, Tt).toLowerCase(),
                        function(t) {
                            var n;
                            do
                            if (n = _ ? t.lang: t.getAttribute("xml:lang") || t.getAttribute("lang")) return n = n.toLowerCase(),
                            n === e || 0 === n.indexOf(e + "-");
                            while ((t = t.parentNode) && 1 === t.nodeType);
                            return ! 1
                        }
                    }),
                    target: function(t) {
                        var n = e.location && e.location.hash;
                        return n && n.slice(1) === t.id
                    },
                    root: function(e) {
                        return e === q
                    },
                    focus: function(e) {
                        return e === H.activeElement && (!H.hasFocus || H.hasFocus()) && !!(e.type || e.href || ~e.tabIndex)
                    },
                    enabled: function(e) {
                        return e.disabled === !1
                    },
                    disabled: function(e) {
                        return e.disabled === !0
                    },
                    checked: function(e) {
                        var t = e.nodeName.toLowerCase();
                        return "input" === t && !!e.checked || "option" === t && !!e.selected
                    },
                    selected: function(e) {
                        return e.parentNode && e.parentNode.selectedIndex,
                        e.selected === !0
                    },
                    empty: function(e) {
                        for (e = e.firstChild; e; e = e.nextSibling) if (e.nodeType < 6) return ! 1;
                        return ! 0
                    },
                    parent: function(e) {
                        return ! T.pseudos.empty(e)
                    },
                    header: function(e) {
                        return gt.test(e.nodeName)
                    },
                    input: function(e) {
                        return mt.test(e.nodeName)
                    },
                    button: function(e) {
                        var t = e.nodeName.toLowerCase();
                        return "input" === t && "button" === e.type || "button" === t
                    },
                    text: function(e) {
                        var t;
                        return "input" === e.nodeName.toLowerCase() && "text" === e.type && (null == (t = e.getAttribute("type")) || "text" === t.toLowerCase())
                    },
                    first: u(function() {
                        return [0]
                    }),
                    last: u(function(e, t) {
                        return [t - 1]
                    }),
                    eq: u(function(e, t, n) {
                        return [0 > n ? n + t: n]
                    }),
                    even: u(function(e, t) {
                        for (var n = 0; t > n; n += 2) e.push(n);
                        return e
                    }),
                    odd: u(function(e, t) {
                        for (var n = 1; t > n; n += 2) e.push(n);
                        return e
                    }),
                    lt: u(function(e, t, n) {
                        for (var r = 0 > n ? n + t: n; --r >= 0;) e.push(r);
                        return e
                    }),
                    gt: u(function(e, t, n) {
                        for (var r = 0 > n ? n + t: n; ++r < t;) e.push(r);
                        return e
                    })
                }
            },
            T.pseudos.nth = T.pseudos.eq;
            for (x in {
                radio: !0,
                checkbox: !0,
                file: !0,
                password: !0,
                image: !0
            }) T.pseudos[x] = s(x);
            for (x in {
                submit: !0,
                reset: !0
            }) T.pseudos[x] = l(x);
            return d.prototype = T.filters = T.pseudos,
            T.setFilters = new d,
            E = t.tokenize = function(e, n) {
                var r, i, o, a, s, l, u, c = I[e + " "];
                if (c) return n ? 0 : c.slice(0);
                for (s = e, l = [], u = T.preFilter; s;) { (!r || (i = ut.exec(s))) && (i && (s = s.slice(i[0].length) || s), l.push(o = [])),
                    r = !1,
                    (i = ct.exec(s)) && (r = i.shift(), o.push({
                        value: r,
                        type: i[0].replace(lt, " ")
                    }), s = s.slice(r.length));
                    for (a in T.filter) ! (i = ht[a].exec(s)) || u[a] && !(i = u[a](i)) || (r = i.shift(), o.push({
                        value: r,
                        type: a,
                        matches: i
                    }), s = s.slice(r.length));
                    if (!r) break
                }
                return n ? s.length: s ? t.error(e) : I(e, l).slice(0)
            },
            k = t.compile = function(e, t) {
                var n, r = [],
                i = [],
                o = X[e + " "];
                if (!o) {
                    for (t || (t = E(e)), n = t.length; n--;) o = y(t[n]),
                    o[P] ? r.push(o) : i.push(o);
                    o = X(e, b(i, r)),
                    o.selector = e
                }
                return o
            },
            S = t.select = function(e, t, n, r) {
                var i, o, a, s, l, u = "function" == typeof e && e,
                d = !r && E(e = u.selector || e);
                if (n = n || [], 1 === d.length) {
                    if (o = d[0] = d[0].slice(0), o.length > 2 && "ID" === (a = o[0]).type && w.getById && 9 === t.nodeType && _ && T.relative[o[1].type]) {
                        if (t = (T.find.ID(a.matches[0].replace(wt, Tt), t) || [])[0], !t) return n;
                        u && (t = t.parentNode),
                        e = e.slice(o.shift().value.length)
                    }
                    for (i = ht.needsContext.test(e) ? 0 : o.length; i--&&(a = o[i], !T.relative[s = a.type]);) if ((l = T.find[s]) && (r = l(a.matches[0].replace(wt, Tt), bt.test(o[0].type) && c(t.parentNode) || t))) {
                        if (o.splice(i, 1), e = r.length && f(o), !e) return Z.apply(n, r),
                        n;
                        break
                    }
                }
                return (u || k(e, d))(r, t, !_, n, bt.test(e) && c(t.parentNode) || t),
                n
            },
            w.sortStable = P.split("").sort(U).join("") === P,
            w.detectDuplicates = !!D,
            L(),
            w.sortDetached = i(function(e) {
                return 1 & e.compareDocumentPosition(H.createElement("div"))
            }),
            i(function(e) {
                return e.innerHTML = "<a href='#'></a>",
                "#" === e.firstChild.getAttribute("href")
            }) || o("type|href|height|width",
            function(e, t, n) {
                return n ? void 0 : e.getAttribute(t, "type" === t.toLowerCase() ? 1 : 2)
            }),
            w.attributes && i(function(e) {
                return e.innerHTML = "<input/>",
                e.firstChild.setAttribute("value", ""),
                "" === e.firstChild.getAttribute("value")
            }) || o("value",
            function(e, t, n) {
                return n || "input" !== e.nodeName.toLowerCase() ? void 0 : e.defaultValue
            }),
            i(function(e) {
                return null == e.getAttribute("disabled")
            }) || o(nt,
            function(e, t, n) {
                var r;
                return n ? void 0 : e[t] === !0 ? t.toLowerCase() : (r = e.getAttributeNode(t)) && r.specified ? r.value: null
            }),
            t
        } (e);
        it.find = ut,
        it.expr = ut.selectors,
        it.expr[":"] = it.expr.pseudos,
        it.unique = ut.uniqueSort,
        it.text = ut.getText,
        it.isXMLDoc = ut.isXML,
        it.contains = ut.contains;
        var ct = it.expr.match.needsContext,
        dt = /^<(\w+)\s*\/?>(?:<\/\1>|)$/,
        ft = /^.[^:#\[\.,]*$/;
        it.filter = function(e, t, n) {
            var r = t[0];
            return n && (e = ":not(" + e + ")"),
            1 === t.length && 1 === r.nodeType ? it.find.matchesSelector(r, e) ? [r] : [] : it.find.matches(e, it.grep(t,
            function(e) {
                return 1 === e.nodeType
            }))
        },
        it.fn.extend({
            find: function(e) {
                var t, n = [],
                r = this,
                i = r.length;
                if ("string" != typeof e) return this.pushStack(it(e).filter(function() {
                    for (t = 0; i > t; t++) if (it.contains(r[t], this)) return ! 0
                }));
                for (t = 0; i > t; t++) it.find(e, r[t], n);
                return n = this.pushStack(i > 1 ? it.unique(n) : n),
                n.selector = this.selector ? this.selector + " " + e: e,
                n
            },
            filter: function(e) {
                return this.pushStack(r(this, e || [], !1))
            },
            not: function(e) {
                return this.pushStack(r(this, e || [], !0))
            },
            is: function(e) {
                return !! r(this, "string" == typeof e && ct.test(e) ? it(e) : e || [], !1).length
            }
        });
        var pt, ht = e.document,
        mt = /^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/,
        gt = it.fn.init = function(e, t) {
            var n, r;
            if (!e) return this;
            if ("string" == typeof e) {
                if (n = "<" === e.charAt(0) && ">" === e.charAt(e.length - 1) && e.length >= 3 ? [null, e, null] : mt.exec(e), !n || !n[1] && t) return ! t || t.jquery ? (t || pt).find(e) : this.constructor(t).find(e);
                if (n[1]) {
                    if (t = t instanceof it ? t[0] : t, it.merge(this, it.parseHTML(n[1], t && t.nodeType ? t.ownerDocument || t: ht, !0)), dt.test(n[1]) && it.isPlainObject(t)) for (n in t) it.isFunction(this[n]) ? this[n](t[n]) : this.attr(n, t[n]);
                    return this
                }
                if (r = ht.getElementById(n[2]), r && r.parentNode) {
                    if (r.id !== n[2]) return pt.find(e);
                    this.length = 1,
                    this[0] = r
                }
                return this.context = ht,
                this.selector = e,
                this
            }
            return e.nodeType ? (this.context = this[0] = e, this.length = 1, this) : it.isFunction(e) ? "undefined" != typeof pt.ready ? pt.ready(e) : e(it) : (void 0 !== e.selector && (this.selector = e.selector, this.context = e.context), it.makeArray(e, this))
        };
        gt.prototype = it.fn,
        pt = it(ht);
        var vt = /^(?:parents|prev(?:Until|All))/,
        yt = {
            children: !0,
            contents: !0,
            next: !0,
            prev: !0
        };
        it.extend({
            dir: function(e, t, n) {
                for (var r = [], i = e[t]; i && 9 !== i.nodeType && (void 0 === n || 1 !== i.nodeType || !it(i).is(n));) 1 === i.nodeType && r.push(i),
                i = i[t];
                return r
            },
            sibling: function(e, t) {
                for (var n = []; e; e = e.nextSibling) 1 === e.nodeType && e !== t && n.push(e);
                return n
            }
        }),
        it.fn.extend({
            has: function(e) {
                var t, n = it(e, this),
                r = n.length;
                return this.filter(function() {
                    for (t = 0; r > t; t++) if (it.contains(this, n[t])) return ! 0
                })
            },
            closest: function(e, t) {
                for (var n, r = 0,
                i = this.length,
                o = [], a = ct.test(e) || "string" != typeof e ? it(e, t || this.context) : 0; i > r; r++) for (n = this[r]; n && n !== t; n = n.parentNode) if (n.nodeType < 11 && (a ? a.index(n) > -1 : 1 === n.nodeType && it.find.matchesSelector(n, e))) {
                    o.push(n);
                    break
                }
                return this.pushStack(o.length > 1 ? it.unique(o) : o)
            },
            index: function(e) {
                return e ? "string" == typeof e ? it.inArray(this[0], it(e)) : it.inArray(e.jquery ? e[0] : e, this) : this[0] && this[0].parentNode ? this.first().prevAll().length: -1
            },
            add: function(e, t) {
                return this.pushStack(it.unique(it.merge(this.get(), it(e, t))))
            },
            addBack: function(e) {
                return this.add(null == e ? this.prevObject: this.prevObject.filter(e))
            }
        }),
        it.each({
            parent: function(e) {
                var t = e.parentNode;
                return t && 11 !== t.nodeType ? t: null
            },
            parents: function(e) {
                return it.dir(e, "parentNode")
            },
            parentsUntil: function(e, t, n) {
                return it.dir(e, "parentNode", n)
            },
            next: function(e) {
                return i(e, "nextSibling")
            },
            prev: function(e) {
                return i(e, "previousSibling")
            },
            nextAll: function(e) {
                return it.dir(e, "nextSibling")
            },
            prevAll: function(e) {
                return it.dir(e, "previousSibling")
            },
            nextUntil: function(e, t, n) {
                return it.dir(e, "nextSibling", n)
            },
            prevUntil: function(e, t, n) {
                return it.dir(e, "previousSibling", n)
            },
            siblings: function(e) {
                return it.sibling((e.parentNode || {}).firstChild, e)
            },
            children: function(e) {
                return it.sibling(e.firstChild)
            },
            contents: function(e) {
                return it.nodeName(e, "iframe") ? e.contentDocument || e.contentWindow.document: it.merge([], e.childNodes)
            }
        },
        function(e, t) {
            it.fn[e] = function(n, r) {
                var i = it.map(this, t, n);
                return "Until" !== e.slice( - 5) && (r = n),
                r && "string" == typeof r && (i = it.filter(r, i)),
                this.length > 1 && (yt[e] || (i = it.unique(i)), vt.test(e) && (i = i.reverse())),
                this.pushStack(i)
            }
        });
        var bt = /\S+/g,
        xt = {};
        it.Callbacks = function(e) {
            e = "string" == typeof e ? xt[e] || o(e) : it.extend({},
            e);
            var t, n, r, i, a, s, l = [],
            u = !e.once && [],
            c = function(o) {
                for (n = e.memory && o, r = !0, a = s || 0, s = 0, i = l.length, t = !0; l && i > a; a++) if (l[a].apply(o[0], o[1]) === !1 && e.stopOnFalse) {
                    n = !1;
                    break
                }
                t = !1,
                l && (u ? u.length && c(u.shift()) : n ? l = [] : d.disable())
            },
            d = {
                add: function() {
                    if (l) {
                        var r = l.length; !
                        function o(t) {
                            it.each(t,
                            function(t, n) {
                                var r = it.type(n);
                                "function" === r ? e.unique && d.has(n) || l.push(n) : n && n.length && "string" !== r && o(n)
                            })
                        } (arguments),
                        t ? i = l.length: n && (s = r, c(n))
                    }
                    return this
                },
                remove: function() {
                    return l && it.each(arguments,
                    function(e, n) {
                        for (var r; (r = it.inArray(n, l, r)) > -1;) l.splice(r, 1),
                        t && (i >= r && i--, a >= r && a--)
                    }),
                    this
                },
                has: function(e) {
                    return e ? it.inArray(e, l) > -1 : !(!l || !l.length)
                },
                empty: function() {
                    return l = [],
                    i = 0,
                    this
                },
                disable: function() {
                    return l = u = n = void 0,
                    this
                },
                disabled: function() {
                    return ! l
                },
                lock: function() {
                    return u = void 0,
                    n || d.disable(),
                    this
                },
                locked: function() {
                    return ! u
                },
                fireWith: function(e, n) {
                    return ! l || r && !u || (n = n || [], n = [e, n.slice ? n.slice() : n], t ? u.push(n) : c(n)),
                    this
                },
                fire: function() {
                    return d.fireWith(this, arguments),
                    this
                },
                fired: function() {
                    return !! r
                }
            };
            return d
        },
        it.extend({
            Deferred: function(e) {
                var t = [["resolve", "done", it.Callbacks("once memory"), "resolved"], ["reject", "fail", it.Callbacks("once memory"), "rejected"], ["notify", "progress", it.Callbacks("memory")]],
                n = "pending",
                r = {
                    state: function() {
                        return n
                    },
                    always: function() {
                        return i.done(arguments).fail(arguments),
                        this
                    },
                    then: function() {
                        var e = arguments;
                        return it.Deferred(function(n) {
                            it.each(t,
                            function(t, o) {
                                var a = it.isFunction(e[t]) && e[t];
                                i[o[1]](function() {
                                    var e = a && a.apply(this, arguments);
                                    e && it.isFunction(e.promise) ? e.promise().done(n.resolve).fail(n.reject).progress(n.notify) : n[o[0] + "With"](this === r ? n.promise() : this, a ? [e] : arguments)
                                })
                            }),
                            e = null
                        }).promise()
                    },
                    promise: function(e) {
                        return null != e ? it.extend(e, r) : r
                    }
                },
                i = {};
                return r.pipe = r.then,
                it.each(t,
                function(e, o) {
                    var a = o[2],
                    s = o[3];
                    r[o[1]] = a.add,
                    s && a.add(function() {
                        n = s
                    },
                    t[1 ^ e][2].disable, t[2][2].lock),
                    i[o[0]] = function() {
                        return i[o[0] + "With"](this === i ? r: this, arguments),
                        this
                    },
                    i[o[0] + "With"] = a.fireWith
                }),
                r.promise(i),
                e && e.call(i, i),
                i
            },
            when: function(e) {
                var t, n, r, i = 0,
                o = Y.call(arguments),
                a = o.length,
                s = 1 !== a || e && it.isFunction(e.promise) ? a: 0,
                l = 1 === s ? e: it.Deferred(),
                u = function(e, n, r) {
                    return function(i) {
                        n[e] = this,
                        r[e] = arguments.length > 1 ? Y.call(arguments) : i,
                        r === t ? l.notifyWith(n, r) : --s || l.resolveWith(n, r)
                    }
                };
                if (a > 1) for (t = new Array(a), n = new Array(a), r = new Array(a); a > i; i++) o[i] && it.isFunction(o[i].promise) ? o[i].promise().done(u(i, r, o)).fail(l.reject).progress(u(i, n, t)) : --s;
                return s || l.resolveWith(r, o),
                l.promise()
            }
        });
        var wt;
        it.fn.ready = function(e) {
            return it.ready.promise().done(e),
            this
        },
        it.extend({
            isReady: !1,
            readyWait: 1,
            holdReady: function(e) {
                e ? it.readyWait++:it.ready(!0)
            },
            ready: function(e) {
                if (e === !0 ? !--it.readyWait: !it.isReady) {
                    if (!ht.body) return setTimeout(it.ready);
                    it.isReady = !0,
                    e !== !0 && --it.readyWait > 0 || (wt.resolveWith(ht, [it]), it.fn.triggerHandler && (it(ht).triggerHandler("ready"), it(ht).off("ready")))
                }
            }
        }),
        it.ready.promise = function(t) {
            if (!wt) if (wt = it.Deferred(), "complete" === ht.readyState) setTimeout(it.ready);
            else if (ht.addEventListener) ht.addEventListener("DOMContentLoaded", s, !1),
            e.addEventListener("load", s, !1);
            else {
                ht.attachEvent("onreadystatechange", s),
                e.attachEvent("onload", s);
                var n = !1;
                try {
                    n = null == e.frameElement && ht.documentElement
                } catch(r) {}
                n && n.doScroll && !
                function i() {
                    if (!it.isReady) {
                        try {
                            n.doScroll("left")
                        } catch(e) {
                            return setTimeout(i, 50)
                        }
                        a(),
                        it.ready()
                    }
                } ()
            }
            return wt.promise(t)
        };
        var Tt, Ct = "undefined";
        for (Tt in it(nt)) break;
        nt.ownLast = "0" !== Tt,
        nt.inlineBlockNeedsLayout = !1,
        it(function() {
            var e, t, n, r;
            n = ht.getElementsByTagName("body")[0],
            n && n.style && (t = ht.createElement("div"), r = ht.createElement("div"), r.style.cssText = "position:absolute;border:0;width:0;height:0;top:0;left:-9999px", n.appendChild(r).appendChild(t), typeof t.style.zoom !== Ct && (t.style.cssText = "display:inline;margin:0;border:0;padding:1px;width:1px;zoom:1", nt.inlineBlockNeedsLayout = e = 3 === t.offsetWidth, e && (n.style.zoom = 1)), n.removeChild(r))
        }),
        function() {
            var e = ht.createElement("div");
            if (null == nt.deleteExpando) {
                nt.deleteExpando = !0;
                try {
                    delete e.test
                } catch(t) {
                    nt.deleteExpando = !1
                }
            }
            e = null
        } (),
        it.acceptData = function(e) {
            var t = it.noData[(e.nodeName + " ").toLowerCase()],
            n = +e.nodeType || 1;
            return 1 !== n && 9 !== n ? !1 : !t || t !== !0 && e.getAttribute("classid") === t
        };
        var Nt = /^(?:\{[\w\W]*\}|\[[\w\W]*\])$/,
        Et = /([A-Z])/g;
        it.extend({
            cache: {},
            noData: {
                "applet ": !0,
                "embed ": !0,
                "object ": "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
            },
            hasData: function(e) {
                return e = e.nodeType ? it.cache[e[it.expando]] : e[it.expando],
                !!e && !u(e)
            },
            data: function(e, t, n) {
                return c(e, t, n)
            },
            removeData: function(e, t) {
                return d(e, t)
            },
            _data: function(e, t, n) {
                return c(e, t, n, !0)
            },
            _removeData: function(e, t) {
                return d(e, t, !0)
            }
        }),
        it.fn.extend({
            data: function(e, t) {
                var n, r, i, o = this[0],
                a = o && o.attributes;
                if (void 0 === e) {
                    if (this.length && (i = it.data(o), 1 === o.nodeType && !it._data(o, "parsedAttrs"))) {
                        for (n = a.length; n--;) a[n] && (r = a[n].name, 0 === r.indexOf("data-") && (r = it.camelCase(r.slice(5)), l(o, r, i[r])));
                        it._data(o, "parsedAttrs", !0)
                    }
                    return i
                }
                return "object" == typeof e ? this.each(function() {
                    it.data(this, e)
                }) : arguments.length > 1 ? this.each(function() {
                    it.data(this, e, t)
                }) : o ? l(o, e, it.data(o, e)) : void 0
            },
            removeData: function(e) {
                return this.each(function() {
                    it.removeData(this, e)
                })
            }
        }),
        it.extend({
            queue: function(e, t, n) {
                var r;
                return e ? (t = (t || "fx") + "queue", r = it._data(e, t), n && (!r || it.isArray(n) ? r = it._data(e, t, it.makeArray(n)) : r.push(n)), r || []) : void 0
            },
            dequeue: function(e, t) {
                t = t || "fx";
                var n = it.queue(e, t),
                r = n.length,
                i = n.shift(),
                o = it._queueHooks(e, t),
                a = function() {
                    it.dequeue(e, t)
                };
                "inprogress" === i && (i = n.shift(), r--),
                i && ("fx" === t && n.unshift("inprogress"), delete o.stop, i.call(e, a, o)),
                !r && o && o.empty.fire()
            },
            _queueHooks: function(e, t) {
                var n = t + "queueHooks";
                return it._data(e, n) || it._data(e, n, {
                    empty: it.Callbacks("once memory").add(function() {
                        it._removeData(e, t + "queue"),
                        it._removeData(e, n)
                    })
                })
            }
        }),
        it.fn.extend({
            queue: function(e, t) {
                var n = 2;
                return "string" != typeof e && (t = e, e = "fx", n--),
                arguments.length < n ? it.queue(this[0], e) : void 0 === t ? this: this.each(function() {
                    var n = it.queue(this, e, t);
                    it._queueHooks(this, e),
                    "fx" === e && "inprogress" !== n[0] && it.dequeue(this, e)
                })
            },
            dequeue: function(e) {
                return this.each(function() {
                    it.dequeue(this, e)
                })
            },
            clearQueue: function(e) {
                return this.queue(e || "fx", [])
            },
            promise: function(e, t) {
                var n, r = 1,
                i = it.Deferred(),
                o = this,
                a = this.length,
                s = function() {--r || i.resolveWith(o, [o])
                };
                for ("string" != typeof e && (t = e, e = void 0), e = e || "fx"; a--;) n = it._data(o[a], e + "queueHooks"),
                n && n.empty && (r++, n.empty.add(s));
                return s(),
                i.promise(t)
            }
        });
        var kt = /[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/.source,
        St = ["Top", "Right", "Bottom", "Left"],
        At = function(e, t) {
            return e = t || e,
            "none" === it.css(e, "display") || !it.contains(e.ownerDocument, e)
        },
        jt = it.access = function(e, t, n, r, i, o, a) {
            var s = 0,
            l = e.length,
            u = null == n;
            if ("object" === it.type(n)) {
                i = !0;
                for (s in n) it.access(e, t, s, n[s], !0, o, a)
            } else if (void 0 !== r && (i = !0, it.isFunction(r) || (a = !0), u && (a ? (t.call(e, r), t = null) : (u = t, t = function(e, t, n) {
                return u.call(it(e), n)
            })), t)) for (; l > s; s++) t(e[s], n, a ? r: r.call(e[s], s, t(e[s], n)));
            return i ? e: u ? t.call(e) : l ? t(e[0], n) : o
        },
        Dt = /^(?:checkbox|radio)$/i; !
        function() {
            var e = ht.createElement("input"),
            t = ht.createElement("div"),
            n = ht.createDocumentFragment();
            if (t.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>", nt.leadingWhitespace = 3 === t.firstChild.nodeType, nt.tbody = !t.getElementsByTagName("tbody").length, nt.htmlSerialize = !!t.getElementsByTagName("link").length, nt.html5Clone = "<:nav></:nav>" !== ht.createElement("nav").cloneNode(!0).outerHTML, e.type = "checkbox", e.checked = !0, n.appendChild(e), nt.appendChecked = e.checked, t.innerHTML = "<textarea>x</textarea>", nt.noCloneChecked = !!t.cloneNode(!0).lastChild.defaultValue, n.appendChild(t), t.innerHTML = "<input type='radio' checked='checked' name='t'/>", nt.checkClone = t.cloneNode(!0).cloneNode(!0).lastChild.checked, nt.noCloneEvent = !0, t.attachEvent && (t.attachEvent("onclick",
            function() {
                nt.noCloneEvent = !1
            }), t.cloneNode(!0).click()), null == nt.deleteExpando) {
                nt.deleteExpando = !0;
                try {
                    delete t.test
                } catch(r) {
                    nt.deleteExpando = !1
                }
            }
        } (),
        function() {
            var t, n, r = ht.createElement("div");
            for (t in {
                submit: !0,
                change: !0,
                focusin: !0
            }) n = "on" + t,
            (nt[t + "Bubbles"] = n in e) || (r.setAttribute(n, "t"), nt[t + "Bubbles"] = r.attributes[n].expando === !1);
            r = null
        } ();
        var Lt = /^(?:input|select|textarea)$/i,
        Ht = /^key/,
        qt = /^(?:mouse|pointer|contextmenu)|click/,
        _t = /^(?:focusinfocus|focusoutblur)$/,
        Mt = /^([^.]*)(?:\.(.+)|)$/;
        it.event = {
            global: {},
            add: function(e, t, n, r, i) {
                var o, a, s, l, u, c, d, f, p, h, m, g = it._data(e);
                if (g) {
                    for (n.handler && (l = n, n = l.handler, i = l.selector), n.guid || (n.guid = it.guid++), (a = g.events) || (a = g.events = {}), (c = g.handle) || (c = g.handle = function(e) {
                        return typeof it === Ct || e && it.event.triggered === e.type ? void 0 : it.event.dispatch.apply(c.elem, arguments)
                    },
                    c.elem = e), t = (t || "").match(bt) || [""], s = t.length; s--;) o = Mt.exec(t[s]) || [],
                    p = m = o[1],
                    h = (o[2] || "").split(".").sort(),
                    p && (u = it.event.special[p] || {},
                    p = (i ? u.delegateType: u.bindType) || p, u = it.event.special[p] || {},
                    d = it.extend({
                        type: p,
                        origType: m,
                        data: r,
                        handler: n,
                        guid: n.guid,
                        selector: i,
                        needsContext: i && it.expr.match.needsContext.test(i),
                        namespace: h.join(".")
                    },
                    l), (f = a[p]) || (f = a[p] = [], f.delegateCount = 0, u.setup && u.setup.call(e, r, h, c) !== !1 || (e.addEventListener ? e.addEventListener(p, c, !1) : e.attachEvent && e.attachEvent("on" + p, c))), u.add && (u.add.call(e, d), d.handler.guid || (d.handler.guid = n.guid)), i ? f.splice(f.delegateCount++, 0, d) : f.push(d), it.event.global[p] = !0);
                    e = null
                }
            },
            remove: function(e, t, n, r, i) {
                var o, a, s, l, u, c, d, f, p, h, m, g = it.hasData(e) && it._data(e);
                if (g && (c = g.events)) {
                    for (t = (t || "").match(bt) || [""], u = t.length; u--;) if (s = Mt.exec(t[u]) || [], p = m = s[1], h = (s[2] || "").split(".").sort(), p) {
                        for (d = it.event.special[p] || {},
                        p = (r ? d.delegateType: d.bindType) || p, f = c[p] || [], s = s[2] && new RegExp("(^|\\.)" + h.join("\\.(?:.*\\.|)") + "(\\.|$)"), l = o = f.length; o--;) a = f[o],
                        !i && m !== a.origType || n && n.guid !== a.guid || s && !s.test(a.namespace) || r && r !== a.selector && ("**" !== r || !a.selector) || (f.splice(o, 1), a.selector && f.delegateCount--, d.remove && d.remove.call(e, a));
                        l && !f.length && (d.teardown && d.teardown.call(e, h, g.handle) !== !1 || it.removeEvent(e, p, g.handle), delete c[p])
                    } else for (p in c) it.event.remove(e, p + t[u], n, r, !0);
                    it.isEmptyObject(c) && (delete g.handle, it._removeData(e, "events"))
                }
            },
            trigger: function(t, n, r, i) {
                var o, a, s, l, u, c, d, f = [r || ht],
                p = tt.call(t, "type") ? t.type: t,
                h = tt.call(t, "namespace") ? t.namespace.split(".") : [];
                if (s = c = r = r || ht, 3 !== r.nodeType && 8 !== r.nodeType && !_t.test(p + it.event.triggered) && (p.indexOf(".") >= 0 && (h = p.split("."), p = h.shift(), h.sort()), a = p.indexOf(":") < 0 && "on" + p, t = t[it.expando] ? t: new it.Event(p, "object" == typeof t && t), t.isTrigger = i ? 2 : 3, t.namespace = h.join("."), t.namespace_re = t.namespace ? new RegExp("(^|\\.)" + h.join("\\.(?:.*\\.|)") + "(\\.|$)") : null, t.result = void 0, t.target || (t.target = r), n = null == n ? [t] : it.makeArray(n, [t]), u = it.event.special[p] || {},
                i || !u.trigger || u.trigger.apply(r, n) !== !1)) {
                    if (!i && !u.noBubble && !it.isWindow(r)) {
                        for (l = u.delegateType || p, _t.test(l + p) || (s = s.parentNode); s; s = s.parentNode) f.push(s),
                        c = s;
                        c === (r.ownerDocument || ht) && f.push(c.defaultView || c.parentWindow || e)
                    }
                    for (d = 0; (s = f[d++]) && !t.isPropagationStopped();) t.type = d > 1 ? l: u.bindType || p,
                    o = (it._data(s, "events") || {})[t.type] && it._data(s, "handle"),
                    o && o.apply(s, n),
                    o = a && s[a],
                    o && o.apply && it.acceptData(s) && (t.result = o.apply(s, n), t.result === !1 && t.preventDefault());
                    if (t.type = p, !i && !t.isDefaultPrevented() && (!u._default || u._default.apply(f.pop(), n) === !1) && it.acceptData(r) && a && r[p] && !it.isWindow(r)) {
                        c = r[a],
                        c && (r[a] = null),
                        it.event.triggered = p;
                        try {
                            r[p]()
                        } catch(m) {}
                        it.event.triggered = void 0,
                        c && (r[a] = c)
                    }
                    return t.result
                }
            },
            dispatch: function(e) {
                e = it.event.fix(e);
                var t, n, r, i, o, a = [],
                s = Y.call(arguments),
                l = (it._data(this, "events") || {})[e.type] || [],
                u = it.event.special[e.type] || {};
                if (s[0] = e, e.delegateTarget = this, !u.preDispatch || u.preDispatch.call(this, e) !== !1) {
                    for (a = it.event.handlers.call(this, e, l), t = 0; (i = a[t++]) && !e.isPropagationStopped();) for (e.currentTarget = i.elem, o = 0; (r = i.handlers[o++]) && !e.isImmediatePropagationStopped();)(!e.namespace_re || e.namespace_re.test(r.namespace)) && (e.handleObj = r, e.data = r.data, n = ((it.event.special[r.origType] || {}).handle || r.handler).apply(i.elem, s), void 0 !== n && (e.result = n) === !1 && (e.preventDefault(), e.stopPropagation()));
                    return u.postDispatch && u.postDispatch.call(this, e),
                    e.result
                }
            },
            handlers: function(e, t) {
                var n, r, i, o, a = [],
                s = t.delegateCount,
                l = e.target;
                if (s && l.nodeType && (!e.button || "click" !== e.type)) for (; l != this; l = l.parentNode || this) if (1 === l.nodeType && (l.disabled !== !0 || "click" !== e.type)) {
                    for (i = [], o = 0; s > o; o++) r = t[o],
                    n = r.selector + " ",
                    void 0 === i[n] && (i[n] = r.needsContext ? it(n, this).index(l) >= 0 : it.find(n, this, null, [l]).length),
                    i[n] && i.push(r);
                    i.length && a.push({
                        elem: l,
                        handlers: i
                    })
                }
                return s < t.length && a.push({
                    elem: this,
                    handlers: t.slice(s)
                }),
                a
            },
            fix: function(e) {
                if (e[it.expando]) return e;
                var t, n, r, i = e.type,
                o = e,
                a = this.fixHooks[i];
                for (a || (this.fixHooks[i] = a = qt.test(i) ? this.mouseHooks: Ht.test(i) ? this.keyHooks: {}), r = a.props ? this.props.concat(a.props) : this.props, e = new it.Event(o), t = r.length; t--;) n = r[t],
                e[n] = o[n];
                return e.target || (e.target = o.srcElement || ht),
                3 === e.target.nodeType && (e.target = e.target.parentNode),
                e.metaKey = !!e.metaKey,
                a.filter ? a.filter(e, o) : e
            },
            props: "altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),
            fixHooks: {},
            keyHooks: {
                props: "char charCode key keyCode".split(" "),
                filter: function(e, t) {
                    return null == e.which && (e.which = null != t.charCode ? t.charCode: t.keyCode),
                    e
                }
            },
            mouseHooks: {
                props: "button buttons clientX clientY fromElement offsetX offsetY pageX pageY screenX screenY toElement".split(" "),
                filter: function(e, t) {
                    var n, r, i, o = t.button,
                    a = t.fromElement;
                    return null == e.pageX && null != t.clientX && (r = e.target.ownerDocument || ht, i = r.documentElement, n = r.body, e.pageX = t.clientX + (i && i.scrollLeft || n && n.scrollLeft || 0) - (i && i.clientLeft || n && n.clientLeft || 0), e.pageY = t.clientY + (i && i.scrollTop || n && n.scrollTop || 0) - (i && i.clientTop || n && n.clientTop || 0)),
                    !e.relatedTarget && a && (e.relatedTarget = a === e.target ? t.toElement: a),
                    e.which || void 0 === o || (e.which = 1 & o ? 1 : 2 & o ? 3 : 4 & o ? 2 : 0),
                    e
                }
            },
            special: {
                load: {
                    noBubble: !0
                },
                focus: {
                    trigger: function() {
                        if (this !== h() && this.focus) try {
                            return this.focus(),
                            !1
                        } catch(e) {}
                    },
                    delegateType: "focusin"
                },
                blur: {
                    trigger: function() {
                        return this === h() && this.blur ? (this.blur(), !1) : void 0
                    },
                    delegateType: "focusout"
                },
                click: {
                    trigger: function() {
                        return it.nodeName(this, "input") && "checkbox" === this.type && this.click ? (this.click(), !1) : void 0
                    },
                    _default: function(e) {
                        return it.nodeName(e.target, "a")
                    }
                },
                beforeunload: {
                    postDispatch: function(e) {
                        void 0 !== e.result && e.originalEvent && (e.originalEvent.returnValue = e.result)
                    }
                }
            },
            simulate: function(e, t, n, r) {
                var i = it.extend(new it.Event, n, {
                    type: e,
                    isSimulated: !0,
                    originalEvent: {}
                });
                r ? it.event.trigger(i, null, t) : it.event.dispatch.call(t, i),
                i.isDefaultPrevented() && n.preventDefault()
            }
        },
        it.removeEvent = ht.removeEventListener ?
        function(e, t, n) {
            e.removeEventListener && e.removeEventListener(t, n, !1)
        }: function(e, t, n) {
            var r = "on" + t;
            e.detachEvent && (typeof e[r] === Ct && (e[r] = null), e.detachEvent(r, n))
        },
        it.Event = function(e, t) {
            return this instanceof it.Event ? (e && e.type ? (this.originalEvent = e, this.type = e.type, this.isDefaultPrevented = e.defaultPrevented || void 0 === e.defaultPrevented && e.returnValue === !1 ? f: p) : this.type = e, t && it.extend(this, t), this.timeStamp = e && e.timeStamp || it.now(), void(this[it.expando] = !0)) : new it.Event(e, t)
        },
        it.Event.prototype = {
            isDefaultPrevented: p,
            isPropagationStopped: p,
            isImmediatePropagationStopped: p,
            preventDefault: function() {
                var e = this.originalEvent;
                this.isDefaultPrevented = f,
                e && (e.preventDefault ? e.preventDefault() : e.returnValue = !1)
            },
            stopPropagation: function() {
                var e = this.originalEvent;
                this.isPropagationStopped = f,
                e && (e.stopPropagation && e.stopPropagation(), e.cancelBubble = !0)
            },
            stopImmediatePropagation: function() {
                var e = this.originalEvent;
                this.isImmediatePropagationStopped = f,
                e && e.stopImmediatePropagation && e.stopImmediatePropagation(),
                this.stopPropagation()
            }
        },
        it.each({
            mouseenter: "mouseover",
            mouseleave: "mouseout",
            pointerenter: "pointerover",
            pointerleave: "pointerout"
        },
        function(e, t) {
            it.event.special[e] = {
                delegateType: t,
                bindType: t,
                handle: function(e) {
                    var n, r = this,
                    i = e.relatedTarget,
                    o = e.handleObj;
                    return (!i || i !== r && !it.contains(r, i)) && (e.type = o.origType, n = o.handler.apply(this, arguments), e.type = t),
                    n
                }
            }
        }),
        nt.submitBubbles || (it.event.special.submit = {
            setup: function() {
                return it.nodeName(this, "form") ? !1 : void it.event.add(this, "click._submit keypress._submit",
                function(e) {
                    var t = e.target,
                    n = it.nodeName(t, "input") || it.nodeName(t, "button") ? t.form: void 0;
                    n && !it._data(n, "submitBubbles") && (it.event.add(n, "submit._submit",
                    function(e) {
                        e._submit_bubble = !0
                    }), it._data(n, "submitBubbles", !0))
                })
            },
            postDispatch: function(e) {
                e._submit_bubble && (delete e._submit_bubble, this.parentNode && !e.isTrigger && it.event.simulate("submit", this.parentNode, e, !0))
            },
            teardown: function() {
                return it.nodeName(this, "form") ? !1 : void it.event.remove(this, "._submit")
            }
        }),
        nt.changeBubbles || (it.event.special.change = {
            setup: function() {
                return Lt.test(this.nodeName) ? (("checkbox" === this.type || "radio" === this.type) && (it.event.add(this, "propertychange._change",
                function(e) {
                    "checked" === e.originalEvent.propertyName && (this._just_changed = !0)
                }), it.event.add(this, "click._change",
                function(e) {
                    this._just_changed && !e.isTrigger && (this._just_changed = !1),
                    it.event.simulate("change", this, e, !0)
                })), !1) : void it.event.add(this, "beforeactivate._change",
                function(e) {
                    var t = e.target;
                    Lt.test(t.nodeName) && !it._data(t, "changeBubbles") && (it.event.add(t, "change._change",
                    function(e) { ! this.parentNode || e.isSimulated || e.isTrigger || it.event.simulate("change", this.parentNode, e, !0)
                    }), it._data(t, "changeBubbles", !0))
                })
            },
            handle: function(e) {
                var t = e.target;
                return this !== t || e.isSimulated || e.isTrigger || "radio" !== t.type && "checkbox" !== t.type ? e.handleObj.handler.apply(this, arguments) : void 0
            },
            teardown: function() {
                return it.event.remove(this, "._change"),
                !Lt.test(this.nodeName)
            }
        }),
        nt.focusinBubbles || it.each({
            focus: "focusin",
            blur: "focusout"
        },
        function(e, t) {
            var n = function(e) {
                it.event.simulate(t, e.target, it.event.fix(e), !0)
            };
            it.event.special[t] = {
                setup: function() {
                    var r = this.ownerDocument || this,
                    i = it._data(r, t);
                    i || r.addEventListener(e, n, !0),
                    it._data(r, t, (i || 0) + 1)
                },
                teardown: function() {
                    var r = this.ownerDocument || this,
                    i = it._data(r, t) - 1;
                    i ? it._data(r, t, i) : (r.removeEventListener(e, n, !0), it._removeData(r, t))
                }
            }
        }),
        it.fn.extend({
            on: function(e, t, n, r, i) {
                var o, a;
                if ("object" == typeof e) {
                    "string" != typeof t && (n = n || t, t = void 0);
                    for (o in e) this.on(o, t, n, e[o], i);
                    return this
                }
                if (null == n && null == r ? (r = t, n = t = void 0) : null == r && ("string" == typeof t ? (r = n, n = void 0) : (r = n, n = t, t = void 0)), r === !1) r = p;
                else if (!r) return this;
                return 1 === i && (a = r, r = function(e) {
                    return it().off(e),
                    a.apply(this, arguments)
                },
                r.guid = a.guid || (a.guid = it.guid++)),
                this.each(function() {
                    it.event.add(this, e, r, n, t)
                })
            },
            one: function(e, t, n, r) {
                return this.on(e, t, n, r, 1)
            },
            off: function(e, t, n) {
                var r, i;
                if (e && e.preventDefault && e.handleObj) return r = e.handleObj,
                it(e.delegateTarget).off(r.namespace ? r.origType + "." + r.namespace: r.origType, r.selector, r.handler),
                this;
                if ("object" == typeof e) {
                    for (i in e) this.off(i, t, e[i]);
                    return this
                }
                return (t === !1 || "function" == typeof t) && (n = t, t = void 0),
                n === !1 && (n = p),
                this.each(function() {
                    it.event.remove(this, e, n, t)
                })
            },
            trigger: function(e, t) {
                return this.each(function() {
                    it.event.trigger(e, t, this)
                })
            },
            triggerHandler: function(e, t) {
                var n = this[0];
                return n ? it.event.trigger(e, t, n, !0) : void 0
            }
        });
        var Ot = "abbr|article|aside|audio|bdi|canvas|data|datalist|details|figcaption|figure|footer|header|hgroup|mark|meter|nav|output|progress|section|summary|time|video",
        Ft = / jQuery\d+="(?:null|\d+)"/g,
        Bt = new RegExp("<(?:" + Ot + ")[\\s/>]", "i"),
        Pt = /^\s+/,
        Rt = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi,
        Wt = /<([\w:]+)/,
        $t = /<tbody/i,
        zt = /<|&#?\w+;/,
        It = /<(?:script|style|link)/i,
        Xt = /checked\s*(?:[^=]|=\s*.checked.)/i,
        Ut = /^$|\/(?:java|ecma)script/i,
        Vt = /^true\/(.*)/,
        Jt = /^\s*<!(?:\[CDATA\[|--)|(?:\]\]|--)>\s*$/g,
        Yt = {
            option: [1, "<select multiple='multiple'>", "</select>"],
            legend: [1, "<fieldset>", "</fieldset>"],
            area: [1, "<map>", "</map>"],
            param: [1, "<object>", "</object>"],
            thead: [1, "<table>", "</table>"],
            tr: [2, "<table><tbody>", "</tbody></table>"],
            col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],
            td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
            _default: nt.htmlSerialize ? [0, "", ""] : [1, "X<div>", "</div>"]
        },
        Gt = m(ht),
        Qt = Gt.appendChild(ht.createElement("div"));
        Yt.optgroup = Yt.option,
        Yt.tbody = Yt.tfoot = Yt.colgroup = Yt.caption = Yt.thead,
        Yt.th = Yt.td,
        it.extend({
            clone: function(e, t, n) {
                var r, i, o, a, s, l = it.contains(e.ownerDocument, e);
                if (nt.html5Clone || it.isXMLDoc(e) || !Bt.test("<" + e.nodeName + ">") ? o = e.cloneNode(!0) : (Qt.innerHTML = e.outerHTML, Qt.removeChild(o = Qt.firstChild)), !(nt.noCloneEvent && nt.noCloneChecked || 1 !== e.nodeType && 11 !== e.nodeType || it.isXMLDoc(e))) for (r = g(o), s = g(e), a = 0; null != (i = s[a]); ++a) r[a] && C(i, r[a]);
                if (t) if (n) for (s = s || g(e), r = r || g(o), a = 0; null != (i = s[a]); a++) T(i, r[a]);
                else T(e, o);
                return r = g(o, "script"),
                r.length > 0 && w(r, !l && g(e, "script")),
                r = s = i = null,
                o
            },
            buildFragment: function(e, t, n, r) {
                for (var i, o, a, s, l, u, c, d = e.length,
                f = m(t), p = [], h = 0; d > h; h++) if (o = e[h], o || 0 === o) if ("object" === it.type(o)) it.merge(p, o.nodeType ? [o] : o);
                else if (zt.test(o)) {
                    for (s = s || f.appendChild(t.createElement("div")), l = (Wt.exec(o) || ["", ""])[1].toLowerCase(), c = Yt[l] || Yt._default, s.innerHTML = c[1] + o.replace(Rt, "<$1></$2>") + c[2], i = c[0]; i--;) s = s.lastChild;
                    if (!nt.leadingWhitespace && Pt.test(o) && p.push(t.createTextNode(Pt.exec(o)[0])), !nt.tbody) for (o = "table" !== l || $t.test(o) ? "<table>" !== c[1] || $t.test(o) ? 0 : s: s.firstChild, i = o && o.childNodes.length; i--;) it.nodeName(u = o.childNodes[i], "tbody") && !u.childNodes.length && o.removeChild(u);
                    for (it.merge(p, s.childNodes), s.textContent = ""; s.firstChild;) s.removeChild(s.firstChild);
                    s = f.lastChild
                } else p.push(t.createTextNode(o));
                for (s && f.removeChild(s), nt.appendChecked || it.grep(g(p, "input"), v), h = 0; o = p[h++];) if ((!r || -1 === it.inArray(o, r)) && (a = it.contains(o.ownerDocument, o), s = g(f.appendChild(o), "script"), a && w(s), n)) for (i = 0; o = s[i++];) Ut.test(o.type || "") && n.push(o);
                return s = null,
                f
            },
            cleanData: function(e, t) {
                for (var n, r, i, o, a = 0,
                s = it.expando,
                l = it.cache,
                u = nt.deleteExpando,
                c = it.event.special; null != (n = e[a]); a++) if ((t || it.acceptData(n)) && (i = n[s], o = i && l[i])) {
                    if (o.events) for (r in o.events) c[r] ? it.event.remove(n, r) : it.removeEvent(n, r, o.handle);
                    l[i] && (delete l[i], u ? delete n[s] : typeof n.removeAttribute !== Ct ? n.removeAttribute(s) : n[s] = null, J.push(i))
                }
            }
        }),
        it.fn.extend({
            text: function(e) {
                return jt(this,
                function(e) {
                    return void 0 === e ? it.text(this) : this.empty().append((this[0] && this[0].ownerDocument || ht).createTextNode(e))
                },
                null, e, arguments.length)
            },
            append: function() {
                return this.domManip(arguments,
                function(e) {
                    if (1 === this.nodeType || 11 === this.nodeType || 9 === this.nodeType) {
                        var t = y(this, e);
                        t.appendChild(e)
                    }
                })
            },
            prepend: function() {
                return this.domManip(arguments,
                function(e) {
                    if (1 === this.nodeType || 11 === this.nodeType || 9 === this.nodeType) {
                        var t = y(this, e);
                        t.insertBefore(e, t.firstChild)
                    }
                })
            },
            before: function() {
                return this.domManip(arguments,
                function(e) {
                    this.parentNode && this.parentNode.insertBefore(e, this)
                })
            },
            after: function() {
                return this.domManip(arguments,
                function(e) {
                    this.parentNode && this.parentNode.insertBefore(e, this.nextSibling)
                })
            },
            remove: function(e, t) {
                for (var n, r = e ? it.filter(e, this) : this, i = 0; null != (n = r[i]); i++) t || 1 !== n.nodeType || it.cleanData(g(n)),
                n.parentNode && (t && it.contains(n.ownerDocument, n) && w(g(n, "script")), n.parentNode.removeChild(n));
                return this
            },
            empty: function() {
                for (var e, t = 0; null != (e = this[t]); t++) {
                    for (1 === e.nodeType && it.cleanData(g(e, !1)); e.firstChild;) e.removeChild(e.firstChild);
                    e.options && it.nodeName(e, "select") && (e.options.length = 0)
                }
                return this
            },
            clone: function(e, t) {
                return e = null == e ? !1 : e,
                t = null == t ? e: t,
                this.map(function() {
                    return it.clone(this, e, t)
                })
            },
            html: function(e) {
                return jt(this,
                function(e) {
                    var t = this[0] || {},
                    n = 0,
                    r = this.length;
                    if (void 0 === e) return 1 === t.nodeType ? t.innerHTML.replace(Ft, "") : void 0;
                    if (! ("string" != typeof e || It.test(e) || !nt.htmlSerialize && Bt.test(e) || !nt.leadingWhitespace && Pt.test(e) || Yt[(Wt.exec(e) || ["", ""])[1].toLowerCase()])) {
                        e = e.replace(Rt, "<$1></$2>");
                        try {
                            for (; r > n; n++) t = this[n] || {},
                            1 === t.nodeType && (it.cleanData(g(t, !1)), t.innerHTML = e);
                            t = 0
                        } catch(i) {}
                    }
                    t && this.empty().append(e)
                },
                null, e, arguments.length)
            },
            replaceWith: function() {
                var e = arguments[0];
                return this.domManip(arguments,
                function(t) {
                    e = this.parentNode,
                    it.cleanData(g(this)),
                    e && e.replaceChild(t, this)
                }),
                e && (e.length || e.nodeType) ? this: this.remove()
            },
            detach: function(e) {
                return this.remove(e, !0)
            },
            domManip: function(e, t) {
                e = G.apply([], e);
                var n, r, i, o, a, s, l = 0,
                u = this.length,
                c = this,
                d = u - 1,
                f = e[0],
                p = it.isFunction(f);
                if (p || u > 1 && "string" == typeof f && !nt.checkClone && Xt.test(f)) return this.each(function(n) {
                    var r = c.eq(n);
                    p && (e[0] = f.call(this, n, r.html())),
                    r.domManip(e, t)
                });
                if (u && (s = it.buildFragment(e, this[0].ownerDocument, !1, this), n = s.firstChild, 1 === s.childNodes.length && (s = n), n)) {
                    for (o = it.map(g(s, "script"), b), i = o.length; u > l; l++) r = s,
                    l !== d && (r = it.clone(r, !0, !0), i && it.merge(o, g(r, "script"))),
                    t.call(this[l], r, l);
                    if (i) for (a = o[o.length - 1].ownerDocument, it.map(o, x), l = 0; i > l; l++) r = o[l],
                    Ut.test(r.type || "") && !it._data(r, "globalEval") && it.contains(a, r) && (r.src ? it._evalUrl && it._evalUrl(r.src) : it.globalEval((r.text || r.textContent || r.innerHTML || "").replace(Jt, "")));
                    s = n = null
                }
                return this
            }
        }),
        it.each({
            appendTo: "append",
            prependTo: "prepend",
            insertBefore: "before",
            insertAfter: "after",
            replaceAll: "replaceWith"
        },
        function(e, t) {
            it.fn[e] = function(e) {
                for (var n, r = 0,
                i = [], o = it(e), a = o.length - 1; a >= r; r++) n = r === a ? this: this.clone(!0),
                it(o[r])[t](n),
                Q.apply(i, n.get());
                return this.pushStack(i)
            }
        });
        var Kt, Zt = {}; !
        function() {
            var e;
            nt.shrinkWrapBlocks = function() {
                if (null != e) return e;
                e = !1;
                var t, n, r;
                return n = ht.getElementsByTagName("body")[0],
                n && n.style ? (t = ht.createElement("div"), r = ht.createElement("div"), r.style.cssText = "position:absolute;border:0;width:0;height:0;top:0;left:-9999px", n.appendChild(r).appendChild(t), typeof t.style.zoom !== Ct && (t.style.cssText = "-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box;display:block;margin:0;border:0;padding:1px;width:1px;zoom:1", t.appendChild(ht.createElement("div")).style.width = "5px", e = 3 !== t.offsetWidth), n.removeChild(r), e) : void 0
            }
        } ();
        var en, tn, nn = /^margin/,
        rn = new RegExp("^(" + kt + ")(?!px)[a-z%]+$", "i"),
        on = /^(top|right|bottom|left)$/;
        e.getComputedStyle ? (en = function(e) {
            return e.ownerDocument.defaultView.getComputedStyle(e, null)
        },
        tn = function(e, t, n) {
            var r, i, o, a, s = e.style;
            return n = n || en(e),
            a = n ? n.getPropertyValue(t) || n[t] : void 0,
            n && ("" !== a || it.contains(e.ownerDocument, e) || (a = it.style(e, t)), rn.test(a) && nn.test(t) && (r = s.width, i = s.minWidth, o = s.maxWidth, s.minWidth = s.maxWidth = s.width = a, a = n.width, s.width = r, s.minWidth = i, s.maxWidth = o)),
            void 0 === a ? a: a + ""
        }) : ht.documentElement.currentStyle && (en = function(e) {
            return e.currentStyle
        },
        tn = function(e, t, n) {
            var r, i, o, a, s = e.style;
            return n = n || en(e),
            a = n ? n[t] : void 0,
            null == a && s && s[t] && (a = s[t]),
            rn.test(a) && !on.test(t) && (r = s.left, i = e.runtimeStyle, o = i && i.left, o && (i.left = e.currentStyle.left), s.left = "fontSize" === t ? "1em": a, a = s.pixelLeft + "px", s.left = r, o && (i.left = o)),
            void 0 === a ? a: a + "" || "auto"
        }),
        function() {
            function t() {
                var t, n, r, i;
                n = ht.getElementsByTagName("body")[0],
                n && n.style && (t = ht.createElement("div"), r = ht.createElement("div"), r.style.cssText = "position:absolute;border:0;width:0;height:0;top:0;left:-9999px", n.appendChild(r).appendChild(t), t.style.cssText = "-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;display:block;margin-top:1%;top:1%;border:1px;padding:1px;width:4px;position:absolute", o = a = !1, l = !0, e.getComputedStyle && (o = "1%" !== (e.getComputedStyle(t, null) || {}).top, a = "4px" === (e.getComputedStyle(t, null) || {
                    width: "4px"
                }).width, i = t.appendChild(ht.createElement("div")), i.style.cssText = t.style.cssText = "-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box;display:block;margin:0;border:0;padding:0", i.style.marginRight = i.style.width = "0", t.style.width = "1px", l = !parseFloat((e.getComputedStyle(i, null) || {}).marginRight)), t.innerHTML = "<table><tr><td></td><td>t</td></tr></table>", i = t.getElementsByTagName("td"), i[0].style.cssText = "margin:0;border:0;padding:0;display:none", s = 0 === i[0].offsetHeight, s && (i[0].style.display = "", i[1].style.display = "none", s = 0 === i[0].offsetHeight), n.removeChild(r))
            }
            var n, r, i, o, a, s, l;
            n = ht.createElement("div"),
            n.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>",
            i = n.getElementsByTagName("a")[0],
            r = i && i.style,
            r && (r.cssText = "float:left;opacity:.5", nt.opacity = "0.5" === r.opacity, nt.cssFloat = !!r.cssFloat, n.style.backgroundClip = "content-box", n.cloneNode(!0).style.backgroundClip = "", nt.clearCloneStyle = "content-box" === n.style.backgroundClip, nt.boxSizing = "" === r.boxSizing || "" === r.MozBoxSizing || "" === r.WebkitBoxSizing, it.extend(nt, {
                reliableHiddenOffsets: function() {
                    return null == s && t(),
                    s
                },
                boxSizingReliable: function() {
                    return null == a && t(),
                    a
                },
                pixelPosition: function() {
                    return null == o && t(),
                    o
                },
                reliableMarginRight: function() {
                    return null == l && t(),
                    l
                }
            }))
        } (),
        it.swap = function(e, t, n, r) {
            var i, o, a = {};
            for (o in t) a[o] = e.style[o],
            e.style[o] = t[o];
            i = n.apply(e, r || []);
            for (o in t) e.style[o] = a[o];
            return i
        };
        var an = /alpha\([^)]*\)/i,
        sn = /opacity\s*=\s*([^)]*)/,
        ln = /^(none|table(?!-c[ea]).+)/,
        un = new RegExp("^(" + kt + ")(.*)$", "i"),
        cn = new RegExp("^([+-])=(" + kt + ")", "i"),
        dn = {
            position: "absolute",
            visibility: "hidden",
            display: "block"
        },
        fn = {
            letterSpacing: "0",
            fontWeight: "400"
        },
        pn = ["Webkit", "O", "Moz", "ms"];
        it.extend({
            cssHooks: {
                opacity: {
                    get: function(e, t) {
                        if (t) {
                            var n = tn(e, "opacity");
                            return "" === n ? "1": n
                        }
                    }
                }
            },
            cssNumber: {
                columnCount: !0,
                fillOpacity: !0,
                flexGrow: !0,
                flexShrink: !0,
                fontWeight: !0,
                lineHeight: !0,
                opacity: !0,
                order: !0,
                orphans: !0,
                widows: !0,
                zIndex: !0,
                zoom: !0
            },
            cssProps: {
                "float": nt.cssFloat ? "cssFloat": "styleFloat"
            },
            style: function(e, t, n, r) {
                if (e && 3 !== e.nodeType && 8 !== e.nodeType && e.style) {
                    var i, o, a, s = it.camelCase(t),
                    l = e.style;
                    if (t = it.cssProps[s] || (it.cssProps[s] = S(l, s)), a = it.cssHooks[t] || it.cssHooks[s], void 0 === n) return a && "get" in a && void 0 !== (i = a.get(e, !1, r)) ? i: l[t];
                    if (o = typeof n, "string" === o && (i = cn.exec(n)) && (n = (i[1] + 1) * i[2] + parseFloat(it.css(e, t)), o = "number"), null != n && n === n && ("number" !== o || it.cssNumber[s] || (n += "px"), nt.clearCloneStyle || "" !== n || 0 !== t.indexOf("background") || (l[t] = "inherit"), !(a && "set" in a && void 0 === (n = a.set(e, n, r))))) try {
                        l[t] = n
                    } catch(u) {}
                }
            },
            css: function(e, t, n, r) {
                var i, o, a, s = it.camelCase(t);
                return t = it.cssProps[s] || (it.cssProps[s] = S(e.style, s)),
                a = it.cssHooks[t] || it.cssHooks[s],
                a && "get" in a && (o = a.get(e, !0, n)),
                void 0 === o && (o = tn(e, t, r)),
                "normal" === o && t in fn && (o = fn[t]),
                "" === n || n ? (i = parseFloat(o), n === !0 || it.isNumeric(i) ? i || 0 : o) : o
            }
        }),
        it.each(["height", "width"],
        function(e, t) {
            it.cssHooks[t] = {
                get: function(e, n, r) {
                    return n ? ln.test(it.css(e, "display")) && 0 === e.offsetWidth ? it.swap(e, dn,
                    function() {
                        return L(e, t, r)
                    }) : L(e, t, r) : void 0
                },
                set: function(e, n, r) {
                    var i = r && en(e);
                    return j(e, n, r ? D(e, t, r, nt.boxSizing && "border-box" === it.css(e, "boxSizing", !1, i), i) : 0)
                }
            }
        }),
        nt.opacity || (it.cssHooks.opacity = {
            get: function(e, t) {
                return sn.test((t && e.currentStyle ? e.currentStyle.filter: e.style.filter) || "") ? .01 * parseFloat(RegExp.$1) + "": t ? "1": ""
            },
            set: function(e, t) {
                var n = e.style,
                r = e.currentStyle,
                i = it.isNumeric(t) ? "alpha(opacity=" + 100 * t + ")": "",
                o = r && r.filter || n.filter || "";
                n.zoom = 1,
                (t >= 1 || "" === t) && "" === it.trim(o.replace(an, "")) && n.removeAttribute && (n.removeAttribute("filter"), "" === t || r && !r.filter) || (n.filter = an.test(o) ? o.replace(an, i) : o + " " + i)
            }
        }),
        it.cssHooks.marginRight = k(nt.reliableMarginRight,
        function(e, t) {
            return t ? it.swap(e, {
                display: "inline-block"
            },
            tn, [e, "marginRight"]) : void 0
        }),
        it.each({
            margin: "",
            padding: "",
            border: "Width"
        },
        function(e, t) {
            it.cssHooks[e + t] = {
                expand: function(n) {
                    for (var r = 0,
                    i = {},
                    o = "string" == typeof n ? n.split(" ") : [n]; 4 > r; r++) i[e + St[r] + t] = o[r] || o[r - 2] || o[0];
                    return i
                }
            },
            nn.test(e) || (it.cssHooks[e + t].set = j)
        }),
        it.fn.extend({
            css: function(e, t) {
                return jt(this,
                function(e, t, n) {
                    var r, i, o = {},
                    a = 0;
                    if (it.isArray(t)) {
                        for (r = en(e), i = t.length; i > a; a++) o[t[a]] = it.css(e, t[a], !1, r);
                        return o
                    }
                    return void 0 !== n ? it.style(e, t, n) : it.css(e, t)
                },
                e, t, arguments.length > 1)
            },
            show: function() {
                return A(this, !0)
            },
            hide: function() {
                return A(this)
            },
            toggle: function(e) {
                return "boolean" == typeof e ? e ? this.show() : this.hide() : this.each(function() {
                    At(this) ? it(this).show() : it(this).hide()
                })
            }
        }),
        it.Tween = H,
        H.prototype = {
            constructor: H,
            init: function(e, t, n, r, i, o) {
                this.elem = e,
                this.prop = n,
                this.easing = i || "swing",
                this.options = t,
                this.start = this.now = this.cur(),
                this.end = r,
                this.unit = o || (it.cssNumber[n] ? "": "px")
            },
            cur: function() {
                var e = H.propHooks[this.prop];
                return e && e.get ? e.get(this) : H.propHooks._default.get(this)
            },
            run: function(e) {
                var t, n = H.propHooks[this.prop];
                return this.pos = t = this.options.duration ? it.easing[this.easing](e, this.options.duration * e, 0, 1, this.options.duration) : e,
                this.now = (this.end - this.start) * t + this.start,
                this.options.step && this.options.step.call(this.elem, this.now, this),
                n && n.set ? n.set(this) : H.propHooks._default.set(this),
                this
            }
        },
        H.prototype.init.prototype = H.prototype,
        H.propHooks = {
            _default: {
                get: function(e) {
                    var t;
                    return null == e.elem[e.prop] || e.elem.style && null != e.elem.style[e.prop] ? (t = it.css(e.elem, e.prop, ""), t && "auto" !== t ? t: 0) : e.elem[e.prop]
                },
                set: function(e) {
                    it.fx.step[e.prop] ? it.fx.step[e.prop](e) : e.elem.style && (null != e.elem.style[it.cssProps[e.prop]] || it.cssHooks[e.prop]) ? it.style(e.elem, e.prop, e.now + e.unit) : e.elem[e.prop] = e.now
                }
            }
        },
        H.propHooks.scrollTop = H.propHooks.scrollLeft = {
            set: function(e) {
                e.elem.nodeType && e.elem.parentNode && (e.elem[e.prop] = e.now)
            }
        },
        it.easing = {
            linear: function(e) {
                return e
            },
            swing: function(e) {
                return.5 - Math.cos(e * Math.PI) / 2
            }
        },
        it.fx = H.prototype.init,
        it.fx.step = {};
        var hn, mn, gn = /^(?:toggle|show|hide)$/,
        vn = new RegExp("^(?:([+-])=|)(" + kt + ")([a-z%]*)$", "i"),
        yn = /queueHooks$/,
        bn = [O],
        xn = {
            "*": [function(e, t) {
                var n = this.createTween(e, t),
                r = n.cur(),
                i = vn.exec(t),
                o = i && i[3] || (it.cssNumber[e] ? "": "px"),
                a = (it.cssNumber[e] || "px" !== o && +r) && vn.exec(it.css(n.elem, e)),
                s = 1,
                l = 20;
                if (a && a[3] !== o) {
                    o = o || a[3],
                    i = i || [],
                    a = +r || 1;
                    do s = s || ".5",
                    a /= s,
                    it.style(n.elem, e, a + o);
                    while (s !== (s = n.cur() / r) && 1 !== s && --l)
                }
                return i && (a = n.start = +a || +r || 0, n.unit = o, n.end = i[1] ? a + (i[1] + 1) * i[2] : +i[2]),
                n
            }]
        };
        it.Animation = it.extend(B, {
            tweener: function(e, t) {
                it.isFunction(e) ? (t = e, e = ["*"]) : e = e.split(" ");
                for (var n, r = 0,
                i = e.length; i > r; r++) n = e[r],
                xn[n] = xn[n] || [],
                xn[n].unshift(t)
            },
            prefilter: function(e, t) {
                t ? bn.unshift(e) : bn.push(e)
            }
        }),
        it.speed = function(e, t, n) {
            var r = e && "object" == typeof e ? it.extend({},
            e) : {
                complete: n || !n && t || it.isFunction(e) && e,
                duration: e,
                easing: n && t || t && !it.isFunction(t) && t
            };
            return r.duration = it.fx.off ? 0 : "number" == typeof r.duration ? r.duration: r.duration in it.fx.speeds ? it.fx.speeds[r.duration] : it.fx.speeds._default,
            (null == r.queue || r.queue === !0) && (r.queue = "fx"),
            r.old = r.complete,
            r.complete = function() {
                it.isFunction(r.old) && r.old.call(this),
                r.queue && it.dequeue(this, r.queue)
            },
            r
        },
        it.fn.extend({
            fadeTo: function(e, t, n, r) {
                return this.filter(At).css("opacity", 0).show().end().animate({
                    opacity: t
                },
                e, n, r)
            },
            animate: function(e, t, n, r) {
                var i = it.isEmptyObject(e),
                o = it.speed(t, n, r),
                a = function() {
                    var t = B(this, it.extend({},
                    e), o); (i || it._data(this, "finish")) && t.stop(!0)
                };
                return a.finish = a,
                i || o.queue === !1 ? this.each(a) : this.queue(o.queue, a)
            },
            stop: function(e, t, n) {
                var r = function(e) {
                    var t = e.stop;
                    delete e.stop,
                    t(n)
                };
                return "string" != typeof e && (n = t, t = e, e = void 0),
                t && e !== !1 && this.queue(e || "fx", []),
                this.each(function() {
                    var t = !0,
                    i = null != e && e + "queueHooks",
                    o = it.timers,
                    a = it._data(this);
                    if (i) a[i] && a[i].stop && r(a[i]);
                    else for (i in a) a[i] && a[i].stop && yn.test(i) && r(a[i]);
                    for (i = o.length; i--;) o[i].elem !== this || null != e && o[i].queue !== e || (o[i].anim.stop(n), t = !1, o.splice(i, 1)); (t || !n) && it.dequeue(this, e)
                })
            },
            finish: function(e) {
                return e !== !1 && (e = e || "fx"),
                this.each(function() {
                    var t, n = it._data(this),
                    r = n[e + "queue"],
                    i = n[e + "queueHooks"],
                    o = it.timers,
                    a = r ? r.length: 0;
                    for (n.finish = !0, it.queue(this, e, []), i && i.stop && i.stop.call(this, !0), t = o.length; t--;) o[t].elem === this && o[t].queue === e && (o[t].anim.stop(!0), o.splice(t, 1));
                    for (t = 0; a > t; t++) r[t] && r[t].finish && r[t].finish.call(this);
                    delete n.finish
                })
            }
        }),
        it.each(["toggle", "show", "hide"],
        function(e, t) {
            var n = it.fn[t];
            it.fn[t] = function(e, r, i) {
                return null == e || "boolean" == typeof e ? n.apply(this, arguments) : this.animate(_(t, !0), e, r, i)
            }
        }),
        it.each({
            slideDown: _("show"),
            slideUp: _("hide"),
            slideToggle: _("toggle"),
            fadeIn: {
                opacity: "show"
            },
            fadeOut: {
                opacity: "hide"
            },
            fadeToggle: {
                opacity: "toggle"
            }
        },
        function(e, t) {
            it.fn[e] = function(e, n, r) {
                return this.animate(t, e, n, r)
            }
        }),
        it.timers = [],
        it.fx.tick = function() {
            var e, t = it.timers,
            n = 0;
            for (hn = it.now(); n < t.length; n++) e = t[n],
            e() || t[n] !== e || t.splice(n--, 1);
            t.length || it.fx.stop(),
            hn = void 0
        },
        it.fx.timer = function(e) {
            it.timers.push(e),
            e() ? it.fx.start() : it.timers.pop()
        },
        it.fx.interval = 13,
        it.fx.start = function() {
            mn || (mn = setInterval(it.fx.tick, it.fx.interval))
        },
        it.fx.stop = function() {
            clearInterval(mn),
            mn = null
        },
        it.fx.speeds = {
            slow: 600,
            fast: 200,
            _default: 400
        },
        it.fn.delay = function(e, t) {
            return e = it.fx ? it.fx.speeds[e] || e: e,
            t = t || "fx",
            this.queue(t,
            function(t, n) {
                var r = setTimeout(t, e);
                n.stop = function() {
                    clearTimeout(r)
                }
            })
        },
        function() {
            var e, t, n, r, i;
            t = ht.createElement("div"),
            t.setAttribute("className", "t"),
            t.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>",
            r = t.getElementsByTagName("a")[0],
            n = ht.createElement("select"),
            i = n.appendChild(ht.createElement("option")),
            e = t.getElementsByTagName("input")[0],
            r.style.cssText = "top:1px",
            nt.getSetAttribute = "t" !== t.className,
            nt.style = /top/.test(r.getAttribute("style")),
            nt.hrefNormalized = "/a" === r.getAttribute("href"),
            nt.checkOn = !!e.value,
            nt.optSelected = i.selected,
            nt.enctype = !!ht.createElement("form").enctype,
            n.disabled = !0,
            nt.optDisabled = !i.disabled,
            e = ht.createElement("input"),
            e.setAttribute("value", ""),
            nt.input = "" === e.getAttribute("value"),
            e.value = "t",
            e.setAttribute("type", "radio"),
            nt.radioValue = "t" === e.value
        } ();
        var wn = /\r/g;
        it.fn.extend({
            val: function(e) {
                var t, n, r, i = this[0]; {
                    if (arguments.length) return r = it.isFunction(e),
                    this.each(function(n) {
                        var i;
                        1 === this.nodeType && (i = r ? e.call(this, n, it(this).val()) : e, null == i ? i = "": "number" == typeof i ? i += "": it.isArray(i) && (i = it.map(i,
                        function(e) {
                            return null == e ? "": e + ""
                        })), t = it.valHooks[this.type] || it.valHooks[this.nodeName.toLowerCase()], t && "set" in t && void 0 !== t.set(this, i, "value") || (this.value = i))
                    });
                    if (i) return t = it.valHooks[i.type] || it.valHooks[i.nodeName.toLowerCase()],
                    t && "get" in t && void 0 !== (n = t.get(i, "value")) ? n: (n = i.value, "string" == typeof n ? n.replace(wn, "") : null == n ? "": n)
                }
            }
        }),
        it.extend({
            valHooks: {
                option: {
                    get: function(e) {
                        var t = it.find.attr(e, "value");
                        return null != t ? t: it.trim(it.text(e))
                    }
                },
                select: {
                    get: function(e) {
                        for (var t, n, r = e.options,
                        i = e.selectedIndex,
                        o = "select-one" === e.type || 0 > i,
                        a = o ? null: [], s = o ? i + 1 : r.length, l = 0 > i ? s: o ? i: 0; s > l; l++) if (n = r[l], !(!n.selected && l !== i || (nt.optDisabled ? n.disabled: null !== n.getAttribute("disabled")) || n.parentNode.disabled && it.nodeName(n.parentNode, "optgroup"))) {
                            if (t = it(n).val(), o) return t;
                            a.push(t)
                        }
                        return a
                    },
                    set: function(e, t) {
                        for (var n, r, i = e.options,
                        o = it.makeArray(t), a = i.length; a--;) if (r = i[a], it.inArray(it.valHooks.option.get(r), o) >= 0) try {
                            r.selected = n = !0
                        } catch(s) {
                            r.scrollHeight
                        } else r.selected = !1;
                        return n || (e.selectedIndex = -1),
                        i
                    }
                }
            }
        }),
        it.each(["radio", "checkbox"],
        function() {
            it.valHooks[this] = {
                set: function(e, t) {
                    return it.isArray(t) ? e.checked = it.inArray(it(e).val(), t) >= 0 : void 0
                }
            },
            nt.checkOn || (it.valHooks[this].get = function(e) {
                return null === e.getAttribute("value") ? "on": e.value
            })
        });
        var Tn, Cn, Nn = it.expr.attrHandle,
        En = /^(?:checked|selected)$/i,
        kn = nt.getSetAttribute,
        Sn = nt.input;
        it.fn.extend({
            attr: function(e, t) {
                return jt(this, it.attr, e, t, arguments.length > 1)
            },
            removeAttr: function(e) {
                return this.each(function() {
                    it.removeAttr(this, e)
                })
            }
        }),
        it.extend({
            attr: function(e, t, n) {
                var r, i, o = e.nodeType;
                if (e && 3 !== o && 8 !== o && 2 !== o) return typeof e.getAttribute === Ct ? it.prop(e, t, n) : (1 === o && it.isXMLDoc(e) || (t = t.toLowerCase(), r = it.attrHooks[t] || (it.expr.match.bool.test(t) ? Cn: Tn)), void 0 === n ? r && "get" in r && null !== (i = r.get(e, t)) ? i: (i = it.find.attr(e, t), null == i ? void 0 : i) : null !== n ? r && "set" in r && void 0 !== (i = r.set(e, n, t)) ? i: (e.setAttribute(t, n + ""), n) : void it.removeAttr(e, t))
            },
            removeAttr: function(e, t) {
                var n, r, i = 0,
                o = t && t.match(bt);
                if (o && 1 === e.nodeType) for (; n = o[i++];) r = it.propFix[n] || n,
                it.expr.match.bool.test(n) ? Sn && kn || !En.test(n) ? e[r] = !1 : e[it.camelCase("default-" + n)] = e[r] = !1 : it.attr(e, n, ""),
                e.removeAttribute(kn ? n: r)
            },
            attrHooks: {
                type: {
                    set: function(e, t) {
                        if (!nt.radioValue && "radio" === t && it.nodeName(e, "input")) {
                            var n = e.value;
                            return e.setAttribute("type", t),
                            n && (e.value = n),
                            t
                        }
                    }
                }
            }
        }),
        Cn = {
            set: function(e, t, n) {
                return t === !1 ? it.removeAttr(e, n) : Sn && kn || !En.test(n) ? e.setAttribute(!kn && it.propFix[n] || n, n) : e[it.camelCase("default-" + n)] = e[n] = !0,
                n
            }
        },
        it.each(it.expr.match.bool.source.match(/\w+/g),
        function(e, t) {
            var n = Nn[t] || it.find.attr;
            Nn[t] = Sn && kn || !En.test(t) ?
            function(e, t, r) {
                var i, o;
                return r || (o = Nn[t], Nn[t] = i, i = null != n(e, t, r) ? t.toLowerCase() : null, Nn[t] = o),
                i
            }: function(e, t, n) {
                return n ? void 0 : e[it.camelCase("default-" + t)] ? t.toLowerCase() : null
            }
        }),
        Sn && kn || (it.attrHooks.value = {
            set: function(e, t, n) {
                return it.nodeName(e, "input") ? void(e.defaultValue = t) : Tn && Tn.set(e, t, n)
            }
        }),
        kn || (Tn = {
            set: function(e, t, n) {
                var r = e.getAttributeNode(n);
                return r || e.setAttributeNode(r = e.ownerDocument.createAttribute(n)),
                r.value = t += "",
                "value" === n || t === e.getAttribute(n) ? t: void 0
            }
        },
        Nn.id = Nn.name = Nn.coords = function(e, t, n) {
            var r;
            return n ? void 0 : (r = e.getAttributeNode(t)) && "" !== r.value ? r.value: null
        },
        it.valHooks.button = {
            get: function(e, t) {
                var n = e.getAttributeNode(t);
                return n && n.specified ? n.value: void 0
            },
            set: Tn.set
        },
        it.attrHooks.contenteditable = {
            set: function(e, t, n) {
                Tn.set(e, "" === t ? !1 : t, n)
            }
        },
        it.each(["width", "height"],
        function(e, t) {
            it.attrHooks[t] = {
                set: function(e, n) {
                    return "" === n ? (e.setAttribute(t, "auto"), n) : void 0
                }
            }
        })),
        nt.style || (it.attrHooks.style = {
            get: function(e) {
                return e.style.cssText || void 0
            },
            set: function(e, t) {
                return e.style.cssText = t + ""
            }
        });
        var An = /^(?:input|select|textarea|button|object)$/i,
        jn = /^(?:a|area)$/i;
        it.fn.extend({
            prop: function(e, t) {
                return jt(this, it.prop, e, t, arguments.length > 1)
            },
            removeProp: function(e) {
                return e = it.propFix[e] || e,
                this.each(function() {
                    try {
                        this[e] = void 0,
                        delete this[e]
                    } catch(t) {}
                })
            }
        }),
        it.extend({
            propFix: {
                "for": "htmlFor",
                "class": "className"
            },
            prop: function(e, t, n) {
                var r, i, o, a = e.nodeType;
                if (e && 3 !== a && 8 !== a && 2 !== a) return o = 1 !== a || !it.isXMLDoc(e),
                o && (t = it.propFix[t] || t, i = it.propHooks[t]),
                void 0 !== n ? i && "set" in i && void 0 !== (r = i.set(e, n, t)) ? r: e[t] = n: i && "get" in i && null !== (r = i.get(e, t)) ? r: e[t]
            },
            propHooks: {
                tabIndex: {
                    get: function(e) {
                        var t = it.find.attr(e, "tabindex");
                        return t ? parseInt(t, 10) : An.test(e.nodeName) || jn.test(e.nodeName) && e.href ? 0 : -1
                    }
                }
            }
        }),
        nt.hrefNormalized || it.each(["href", "src"],
        function(e, t) {
            it.propHooks[t] = {
                get: function(e) {
                    return e.getAttribute(t, 4)
                }
            }
        }),
        nt.optSelected || (it.propHooks.selected = {
            get: function(e) {
                var t = e.parentNode;
                return t && (t.selectedIndex, t.parentNode && t.parentNode.selectedIndex),
                null
            }
        }),
        it.each(["tabIndex", "readOnly", "maxLength", "cellSpacing", "cellPadding", "rowSpan", "colSpan", "useMap", "frameBorder", "contentEditable"],
        function() {
            it.propFix[this.toLowerCase()] = this
        }),
        nt.enctype || (it.propFix.enctype = "encoding");
        var Dn = /[\t\r\n\f]/g;
        it.fn.extend({
            addClass: function(e) {
                var t, n, r, i, o, a, s = 0,
                l = this.length,
                u = "string" == typeof e && e;
                if (it.isFunction(e)) return this.each(function(t) {
                    it(this).addClass(e.call(this, t, this.className))
                });
                if (u) for (t = (e || "").match(bt) || []; l > s; s++) if (n = this[s], r = 1 === n.nodeType && (n.className ? (" " + n.className + " ").replace(Dn, " ") : " ")) {
                    for (o = 0; i = t[o++];) r.indexOf(" " + i + " ") < 0 && (r += i + " ");
                    a = it.trim(r),
                    n.className !== a && (n.className = a)
                }
                return this
            },
            removeClass: function(e) {
                var t, n, r, i, o, a, s = 0,
                l = this.length,
                u = 0 === arguments.length || "string" == typeof e && e;
                if (it.isFunction(e)) return this.each(function(t) {
                    it(this).removeClass(e.call(this, t, this.className))
                });
                if (u) for (t = (e || "").match(bt) || []; l > s; s++) if (n = this[s], r = 1 === n.nodeType && (n.className ? (" " + n.className + " ").replace(Dn, " ") : "")) {
                    for (o = 0; i = t[o++];) for (; r.indexOf(" " + i + " ") >= 0;) r = r.replace(" " + i + " ", " ");
                    a = e ? it.trim(r) : "",
                    n.className !== a && (n.className = a)
                }
                return this
            },
            toggleClass: function(e, t) {
                var n = typeof e;
                return "boolean" == typeof t && "string" === n ? t ? this.addClass(e) : this.removeClass(e) : this.each(it.isFunction(e) ?
                function(n) {
                    it(this).toggleClass(e.call(this, n, this.className, t), t)
                }: function() {
                    if ("string" === n) for (var t, r = 0,
                    i = it(this), o = e.match(bt) || []; t = o[r++];) i.hasClass(t) ? i.removeClass(t) : i.addClass(t);
                    else(n === Ct || "boolean" === n) && (this.className && it._data(this, "__className__", this.className), this.className = this.className || e === !1 ? "": it._data(this, "__className__") || "")
                })
            },
            hasClass: function(e) {
                for (var t = " " + e + " ",
                n = 0,
                r = this.length; r > n; n++) if (1 === this[n].nodeType && (" " + this[n].className + " ").replace(Dn, " ").indexOf(t) >= 0) return ! 0;
                return ! 1
            }
        }),
        it.each("blur focus focusin focusout load resize scroll unload click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup error contextmenu".split(" "),
        function(e, t) {
            it.fn[t] = function(e, n) {
                return arguments.length > 0 ? this.on(t, null, e, n) : this.trigger(t)
            }
        }),
        it.fn.extend({
            hover: function(e, t) {
                return this.mouseenter(e).mouseleave(t || e)
            },
            bind: function(e, t, n) {
                return this.on(e, null, t, n)
            },
            unbind: function(e, t) {
                return this.off(e, null, t)
            },
            delegate: function(e, t, n, r) {
                return this.on(t, e, n, r)
            },
            undelegate: function(e, t, n) {
                return 1 === arguments.length ? this.off(e, "**") : this.off(t, e || "**", n)
            }
        });
        var Ln = it.now(),
        Hn = /\?/,
        qn = /(,)|(\[|{)|(}|])|"(?:[^"\\\r\n]|\\["\\\/bfnrt]|\\u[\da-fA-F]{4})*"\s*:?|true|false|null|-?(?!0\d)\d+(?:\.\d+|)(?:[eE][+-]?\d+|)/g;
        it.parseJSON = function(t) {
            if (e.JSON && e.JSON.parse) return e.JSON.parse(t + "");
            var n, r = null,
            i = it.trim(t + "");
            return i && !it.trim(i.replace(qn,
            function(e, t, i, o) {
                return n && t && (r = 0),
                0 === r ? e: (n = i || t, r += !o - !i, "")
            })) ? Function("return " + i)() : it.error("Invalid JSON: " + t)
        },
        it.parseXML = function(t) {
            var n, r;
            if (!t || "string" != typeof t) return null;
            try {
                e.DOMParser ? (r = new DOMParser, n = r.parseFromString(t, "text/xml")) : (n = new ActiveXObject("Microsoft.XMLDOM"), n.async = "false", n.loadXML(t))
            } catch(i) {
                n = void 0
            }
            return n && n.documentElement && !n.getElementsByTagName("parsererror").length || it.error("Invalid XML: " + t),
            n
        };
        var _n, Mn, On = /#.*$/,
        Fn = /([?&])_=[^&]*/,
        Bn = /^(.*?):[ \t]*([^\r\n]*)\r?$/gm,
        Pn = /^(?:about|app|app-storage|.+-extension|file|res|widget):$/,
        Rn = /^(?:GET|HEAD)$/,
        Wn = /^\/\//,
        $n = /^([\w.+-]+:)(?:\/\/(?:[^\/?#]*@|)([^\/?#:]*)(?::(\d+)|)|)/,
        zn = {},
        In = {},
        Xn = "*/".concat("*");
        try {
            Mn = location.href
        } catch(Un) {
            Mn = ht.createElement("a"),
            Mn.href = "",
            Mn = Mn.href
        }
        _n = $n.exec(Mn.toLowerCase()) || [],
        it.extend({
            active: 0,
            lastModified: {},
            etag: {},
            ajaxSettings: {
                url: Mn,
                type: "GET",
                isLocal: Pn.test(_n[1]),
                global: !0,
                processData: !0,
                async: !0,
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                accepts: {
                    "*": Xn,
                    text: "text/plain",
                    html: "text/html",
                    xml: "application/xml, text/xml",
                    json: "application/json, text/javascript"
                },
                contents: {
                    xml: /xml/,
                    html: /html/,
                    json: /json/
                },
                responseFields: {
                    xml: "responseXML",
                    text: "responseText",
                    json: "responseJSON"
                },
                converters: {
                    "* text": String,
                    "text html": !0,
                    "text json": it.parseJSON,
                    "text xml": it.parseXML
                },
                flatOptions: {
                    url: !0,
                    context: !0
                }
            },
            ajaxSetup: function(e, t) {
                return t ? W(W(e, it.ajaxSettings), t) : W(it.ajaxSettings, e)
            },
            ajaxPrefilter: P(zn),
            ajaxTransport: P(In),
            ajax: function(e, t) {
            	e.url="resource/"+e.url;
                function n(e, t, n, r) {
                    var i, c, v, y, x, T = t;
                    2 !== b && (b = 2, s && clearTimeout(s), u = void 0, a = r || "", w.readyState = e > 0 ? 4 : 0, i = e >= 200 && 300 > e || 304 === e, n && (y = $(d, w, n)), y = z(d, y, w, i), i ? (d.ifModified && (x = w.getResponseHeader("Last-Modified"), x && (it.lastModified[o] = x), x = w.getResponseHeader("etag"), x && (it.etag[o] = x)), 204 === e || "HEAD" === d.type ? T = "nocontent": 304 === e ? T = "notmodified": (T = y.state, c = y.data, v = y.error, i = !v)) : (v = T, (e || !T) && (T = "error", 0 > e && (e = 0))), w.status = e, w.statusText = (t || T) + "", i ? h.resolveWith(f, [c, T, w]) : h.rejectWith(f, [w, T, v]), w.statusCode(g), g = void 0, l && p.trigger(i ? "ajaxSuccess": "ajaxError", [w, d, i ? c: v]), m.fireWith(f, [w, T]), l && (p.trigger("ajaxComplete", [w, d]), --it.active || it.event.trigger("ajaxStop")))
                }
                "object" == typeof e && (t = e, e = void 0),
                t = t || {};
                var r, i, o, a, s, l, u, c, d = it.ajaxSetup({},
                t),
                f = d.context || d,
                p = d.context && (f.nodeType || f.jquery) ? it(f) : it.event,
                h = it.Deferred(),
                m = it.Callbacks("once memory"),
                g = d.statusCode || {},
                v = {},
                y = {},
                b = 0,
                x = "canceled",
                w = {
                    readyState: 0,
                    getResponseHeader: function(e) {
                        var t;
                        if (2 === b) {
                            if (!c) for (c = {}; t = Bn.exec(a);) c[t[1].toLowerCase()] = t[2];
                            t = c[e.toLowerCase()]
                        }
                        return null == t ? null: t
                    },
                    getAllResponseHeaders: function() {
                        return 2 === b ? a: null
                    },
                    setRequestHeader: function(e, t) {
                        var n = e.toLowerCase();
                        return b || (e = y[n] = y[n] || e, v[e] = t),
                        this
                    },
                    overrideMimeType: function(e) {
                        return b || (d.mimeType = e),
                        this
                    },
                    statusCode: function(e) {
                        var t;
                        if (e) if (2 > b) for (t in e) g[t] = [g[t], e[t]];
                        else w.always(e[w.status]);
                        return this
                    },
                    abort: function(e) {
                        var t = e || x;
                        return u && u.abort(t),
                        n(0, t),
                        this
                    }
                };
                if (h.promise(w).complete = m.add, w.success = w.done, w.error = w.fail, d.url = ((e || d.url || Mn) + "").replace(On, "").replace(Wn, _n[1] + "//"), d.type = t.method || t.type || d.method || d.type, d.dataTypes = it.trim(d.dataType || "*").toLowerCase().match(bt) || [""], null == d.crossDomain && (r = $n.exec(d.url.toLowerCase()), d.crossDomain = !(!r || r[1] === _n[1] && r[2] === _n[2] && (r[3] || ("http:" === r[1] ? "80": "443")) === (_n[3] || ("http:" === _n[1] ? "80": "443")))), d.data && d.processData && "string" != typeof d.data && (d.data = it.param(d.data, d.traditional)), R(zn, d, t, w), 2 === b) return w;
                l = d.global,
                l && 0 === it.active++&&it.event.trigger("ajaxStart"),
                d.type = d.type.toUpperCase(),
                d.hasContent = !Rn.test(d.type),
                o = d.url,
                d.hasContent || (d.data && (o = d.url += (Hn.test(o) ? "&": "?") + d.data, delete d.data), d.cache === !1 && (d.url = Fn.test(o) ? o.replace(Fn, "$1_=" + Ln++) : o + (Hn.test(o) ? "&": "?") + "_=" + Ln++)),
                d.ifModified && (it.lastModified[o] && w.setRequestHeader("If-Modified-Since", it.lastModified[o]), it.etag[o] && w.setRequestHeader("If-None-Match", it.etag[o])),
                (d.data && d.hasContent && d.contentType !== !1 || t.contentType) && w.setRequestHeader("Content-Type", d.contentType),
                w.setRequestHeader("Accept", d.dataTypes[0] && d.accepts[d.dataTypes[0]] ? d.accepts[d.dataTypes[0]] + ("*" !== d.dataTypes[0] ? ", " + Xn + "; q=0.01": "") : d.accepts["*"]);
                for (i in d.headers) w.setRequestHeader(i, d.headers[i]);
                if (d.beforeSend && (d.beforeSend.call(f, w, d) === !1 || 2 === b)) return w.abort();
                x = "abort";
                for (i in {
                    success: 1,
                    error: 1,
                    complete: 1
                }) w[i](d[i]);
                if (u = R(In, d, t, w)) {
                    w.readyState = 1,
                    l && p.trigger("ajaxSend", [w, d]),
                    d.async && d.timeout > 0 && (s = setTimeout(function() {
                        w.abort("timeout")
                    },
                    d.timeout));
                    try {
                        b = 1,
                        u.send(v, n)
                    } catch(T) {
                        if (! (2 > b)) throw T;
                        n( - 1, T)
                    }
                } else n( - 1, "No Transport");
                return w
            },
            getJSON: function(e, t, n) {
                return it.get(e, t, n, "json")
            },
            getScript: function(e, t) {
                return it.get(e, void 0, t, "script")
            }
        }),
        it.each(["get", "post"],
        function(e, t) {
            it[t] = function(e, n, r, i) {
                return it.isFunction(n) && (i = i || r, r = n, n = void 0),
                it.ajax({
                    url: e,
                    type: t,
                    dataType: i,
                    data: n,
                    success: r
                })
            }
        }),
        it.each(["ajaxStart", "ajaxStop", "ajaxComplete", "ajaxError", "ajaxSuccess", "ajaxSend"],
        function(e, t) {
            it.fn[t] = function(e) {
                return this.on(t, e)
            }
        }),
        it._evalUrl = function(e) {
            return it.ajax({
                url: e,
                type: "GET",
                dataType: "script",
                async: !1,
                global: !1,
                "throws": !0
            })
        },
        it.fn.extend({
            wrapAll: function(e) {
                if (it.isFunction(e)) return this.each(function(t) {
                    it(this).wrapAll(e.call(this, t))
                });
                if (this[0]) {
                    var t = it(e, this[0].ownerDocument).eq(0).clone(!0);
                    this[0].parentNode && t.insertBefore(this[0]),
                    t.map(function() {
                        for (var e = this; e.firstChild && 1 === e.firstChild.nodeType;) e = e.firstChild;
                        return e
                    }).append(this)
                }
                return this
            },
            wrapInner: function(e) {
                return this.each(it.isFunction(e) ?
                function(t) {
                    it(this).wrapInner(e.call(this, t))
                }: function() {
                    var t = it(this),
                    n = t.contents();
                    n.length ? n.wrapAll(e) : t.append(e)
                })
            },
            wrap: function(e) {
                var t = it.isFunction(e);
                return this.each(function(n) {
                    it(this).wrapAll(t ? e.call(this, n) : e)
                })
            },
            unwrap: function() {
                return this.parent().each(function() {
                    it.nodeName(this, "body") || it(this).replaceWith(this.childNodes)
                }).end()
            }
        }),
        it.expr.filters.hidden = function(e) {
            return e.offsetWidth <= 0 && e.offsetHeight <= 0 || !nt.reliableHiddenOffsets() && "none" === (e.style && e.style.display || it.css(e, "display"))
        },
        it.expr.filters.visible = function(e) {
            return ! it.expr.filters.hidden(e)
        };
        var Vn = /%20/g,
        Jn = /\[\]$/,
        Yn = /\r?\n/g,
        Gn = /^(?:submit|button|image|reset|file)$/i,
        Qn = /^(?:input|select|textarea|keygen)/i;
        it.param = function(e, t) {
            var n, r = [],
            i = function(e, t) {
                t = it.isFunction(t) ? t() : null == t ? "": t,
                r[r.length] = encodeURIComponent(e) + "=" + encodeURIComponent(t)
            };
            if (void 0 === t && (t = it.ajaxSettings && it.ajaxSettings.traditional), it.isArray(e) || e.jquery && !it.isPlainObject(e)) it.each(e,
            function() {
                i(this.name, this.value)
            });
            else for (n in e) I(n, e[n], t, i);
            return r.join("&").replace(Vn, "+")
        },
        it.fn.extend({
            serialize: function() {
                return it.param(this.serializeArray())
            },
            serializeArray: function() {
                return this.map(function() {
                    var e = it.prop(this, "elements");
                    return e ? it.makeArray(e) : this
                }).filter(function() {
                    var e = this.type;
                    return this.name && !it(this).is(":disabled") && Qn.test(this.nodeName) && !Gn.test(e) && (this.checked || !Dt.test(e))
                }).map(function(e, t) {
                    var n = it(this).val();
                    return null == n ? null: it.isArray(n) ? it.map(n,
                    function(e) {
                        return {
                            name: t.name,
                            value: e.replace(Yn, "\r\n")
                        }
                    }) : {
                        name: t.name,
                        value: n.replace(Yn, "\r\n")
                    }
                }).get()
            }
        }),
        it.ajaxSettings.xhr = void 0 !== e.ActiveXObject ?
        function() {
            return ! this.isLocal && /^(get|post|head|put|delete|options)$/i.test(this.type) && X() || U()
        }: X;
        var Kn = 0,
        Zn = {},
        er = it.ajaxSettings.xhr();
        e.ActiveXObject && it(e).on("unload",
        function() {
            for (var e in Zn) Zn[e](void 0, !0)
        }),
        nt.cors = !!er && "withCredentials" in er,
        er = nt.ajax = !!er,
        er && it.ajaxTransport(function(e) {
            if (!e.crossDomain || nt.cors) {
                var t;
                return {
                    send: function(n, r) {
                        var i, o = e.xhr(),
                        a = ++Kn;
                        if (o.open(e.type, oc.resource.getUrl(e.url), e.async, e.username, e.password), e.xhrFields) for (i in e.xhrFields) o[i] = e.xhrFields[i];
                        e.mimeType && o.overrideMimeType && o.overrideMimeType(e.mimeType),
                        e.crossDomain || n["X-Requested-With"] || (n["X-Requested-With"] = "XMLHttpRequest");
                        for (i in n) void 0 !== n[i] && o.setRequestHeader(i, n[i] + "");
                        o.send(e.hasContent && e.data || null),
                        t = function(n, i) {
                            var s, l, u;
                            if (t && (i || 4 === o.readyState)) if (delete Zn[a], t = void 0, o.onreadystatechange = it.noop, i) 4 !== o.readyState && o.abort();
                            else {
                                u = {},
                                s = o.status,
                                "string" == typeof o.responseText && (u.text = o.responseText);
                                try {
                                    l = o.statusText
                                } catch(c) {
                                    l = ""
                                }
                                s || !e.isLocal || e.crossDomain ? 1223 === s && (s = 204) : s = u.text ? 200 : 404
                            }
                            u && r(s, l, u, o.getAllResponseHeaders())
                        },
                        e.async ? 4 === o.readyState ? setTimeout(t) : o.onreadystatechange = Zn[a] = t: t()
                    },
                    abort: function() {
                        t && t(void 0, !0)
                    }
                }
            }
        }),
        it.ajaxSetup({
            accepts: {
                script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
            },
            contents: {
                script: /(?:java|ecma)script/
            },
            converters: {
                "text script": function(e) {
                    return it.globalEval(e),
                    e
                }
            }
        }),
        it.ajaxPrefilter("script",
        function(e) {
            void 0 === e.cache && (e.cache = !1),
            e.crossDomain && (e.type = "GET", e.global = !1)
        }),
        it.ajaxTransport("script",
        function(e) {
            if (e.crossDomain) {
                var t, n = ht.head || it("head")[0] || ht.documentElement;
                return {
                    send: function(r, i) {
                        t = ht.createElement("script"),
                        t.async = !0,
                        e.scriptCharset && (t.charset = e.scriptCharset),
                        t.src = e.url,
                        t.onload = t.onreadystatechange = function(e, n) { (n || !t.readyState || /loaded|complete/.test(t.readyState)) && (t.onload = t.onreadystatechange = null, t.parentNode && t.parentNode.removeChild(t), t = null, n || i(200, "success"))
                        },
                        n.insertBefore(t, n.firstChild)
                    },
                    abort: function() {
                        t && t.onload(void 0, !0)
                    }
                }
            }
        });
        var tr = [],
        nr = /(=)\?(?=&|$)|\?\?/;
        it.ajaxSetup({
            jsonp: "callback",
            jsonpCallback: function() {
                var e = tr.pop() || it.expando + "_" + Ln++;
                return this[e] = !0,
                e
            }
        }),
        it.ajaxPrefilter("json jsonp",
        function(t, n, r) {
            var i, o, a, s = t.jsonp !== !1 && (nr.test(t.url) ? "url": "string" == typeof t.data && !(t.contentType || "").indexOf("application/x-www-form-urlencoded") && nr.test(t.data) && "data");
            return s || "jsonp" === t.dataTypes[0] ? (i = t.jsonpCallback = it.isFunction(t.jsonpCallback) ? t.jsonpCallback() : t.jsonpCallback, s ? t[s] = t[s].replace(nr, "$1" + i) : t.jsonp !== !1 && (t.url += (Hn.test(t.url) ? "&": "?") + t.jsonp + "=" + i), t.converters["script json"] = function() {
                return a || it.error(i + " was not called"),
                a[0]
            },
            t.dataTypes[0] = "json", o = e[i], e[i] = function() {
                a = arguments
            },
            r.always(function() {
                e[i] = o,
                t[i] && (t.jsonpCallback = n.jsonpCallback, tr.push(i)),
                a && it.isFunction(o) && o(a[0]),
                a = o = void 0
            }), "script") : void 0
        }),
        it.parseHTML = function(e, t, n) {
            if (!e || "string" != typeof e) return null;
            "boolean" == typeof t && (n = t, t = !1),
            t = t || ht;
            var r = dt.exec(e),
            i = !n && [];
            return r ? [t.createElement(r[1])] : (r = it.buildFragment([e], t, i), i && i.length && it(i).remove(), it.merge([], r.childNodes))
        };
        var rr = it.fn.load;
        it.fn.load = function(e, t, n) {
            if ("string" != typeof e && rr) return rr.apply(this, arguments);
            var r, i, o, a = this,
            s = e.indexOf(" ");
            return s >= 0 && (r = it.trim(e.slice(s, e.length)), e = e.slice(0, s)),
            it.isFunction(t) ? (n = t, t = void 0) : t && "object" == typeof t && (o = "POST"),
            a.length > 0 && it.ajax({
                url: e,
                type: o,
                dataType: "html",
                data: t
            }).done(function(e) {
                i = arguments,
                a.html(r ? it("<div>").append(it.parseHTML(e)).find(r) : e)
            }).complete(n &&
            function(e, t) {
                a.each(n, i || [e.responseText, t, e])
            }),
            this
        },
        it.expr.filters.animated = function(e) {
            return it.grep(it.timers,
            function(t) {
                return e === t.elem
            }).length
        };
        var ir = e.document.documentElement;
        it.offset = {
            setOffset: function(e, t, n) {
                var r, i, o, a, s, l, u, c = it.css(e, "position"),
                d = it(e),
                f = {};
                "static" === c && (e.style.position = "relative"),
                s = d.offset(),
                o = it.css(e, "top"),
                l = it.css(e, "left"),
                u = ("absolute" === c || "fixed" === c) && it.inArray("auto", [o, l]) > -1,
                u ? (r = d.position(), a = r.top, i = r.left) : (a = parseFloat(o) || 0, i = parseFloat(l) || 0),
                it.isFunction(t) && (t = t.call(e, n, s)),
                null != t.top && (f.top = t.top - s.top + a),
                null != t.left && (f.left = t.left - s.left + i),
                "using" in t ? t.using.call(e, f) : d.css(f)
            }
        },
        it.fn.extend({
            offset: function(e) {
                if (arguments.length) return void 0 === e ? this: this.each(function(t) {
                    it.offset.setOffset(this, e, t)
                });
                var t, n, r = {
                    top: 0,
                    left: 0
                },
                i = this[0],
                o = i && i.ownerDocument;
                if (o) return t = o.documentElement,
                it.contains(t, i) ? (typeof i.getBoundingClientRect !== Ct && (r = i.getBoundingClientRect()), n = V(o), {
                    top: r.top + (n.pageYOffset || t.scrollTop) - (t.clientTop || 0),
                    left: r.left + (n.pageXOffset || t.scrollLeft) - (t.clientLeft || 0)
                }) : r
            },
            position: function() {
                if (this[0]) {
                    var e, t, n = {
                        top: 0,
                        left: 0
                    },
                    r = this[0];
                    return "fixed" === it.css(r, "position") ? t = r.getBoundingClientRect() : (e = this.offsetParent(), t = this.offset(), it.nodeName(e[0], "html") || (n = e.offset()), n.top += it.css(e[0], "borderTopWidth", !0), n.left += it.css(e[0], "borderLeftWidth", !0)),
                    {
                        top: t.top - n.top - it.css(r, "marginTop", !0),
                        left: t.left - n.left - it.css(r, "marginLeft", !0)
                    }
                }
            },
            offsetParent: function() {
                return this.map(function() {
                    for (var e = this.offsetParent || ir; e && !it.nodeName(e, "html") && "static" === it.css(e, "position");) e = e.offsetParent;
                    return e || ir
                })
            }
        }),
        it.each({
            scrollLeft: "pageXOffset",
            scrollTop: "pageYOffset"
        },
        function(e, t) {
            var n = /Y/.test(t);
            it.fn[e] = function(r) {
                return jt(this,
                function(e, r, i) {
                    var o = V(e);
                    return void 0 === i ? o ? t in o ? o[t] : o.document.documentElement[r] : e[r] : void(o ? o.scrollTo(n ? it(o).scrollLeft() : i, n ? i: it(o).scrollTop()) : e[r] = i)
                },
                e, r, arguments.length, null)
            }
        }),
        it.each(["top", "left"],
        function(e, t) {
            it.cssHooks[t] = k(nt.pixelPosition,
            function(e, n) {
                return n ? (n = tn(e, t), rn.test(n) ? it(e).position()[t] + "px": n) : void 0
            })
        }),
        it.each({
            Height: "height",
            Width: "width"
        },
        function(e, t) {
            it.each({
                padding: "inner" + e,
                content: t,
                "": "outer" + e
            },
            function(n, r) {
                it.fn[r] = function(r, i) {
                    var o = arguments.length && (n || "boolean" != typeof r),
                    a = n || (r === !0 || i === !0 ? "margin": "border");
                    return jt(this,
                    function(t, n, r) {
                        var i;
                        return it.isWindow(t) ? t.document.documentElement["client" + e] : 9 === t.nodeType ? (i = t.documentElement, Math.max(t.body["scroll" + e], i["scroll" + e], t.body["offset" + e], i["offset" + e], i["client" + e])) : void 0 === r ? it.css(t, n, a) : it.style(t, n, r, a)
                    },
                    t, o ? r: void 0, o, null)
                }
            })
        }),
        it.fn.size = function() {
            return this.length
        },
        it.fn.andSelf = it.fn.addBack,
        "function" == typeof define && define.amd && define("jquery", [],
        function() {
            return it
        });
        var or = e.jQuery,
        ar = e.$;
        return it.noConflict = function(t) {
            return e.$ === it && (e.$ = ar),
            t && e.jQuery === it && (e.jQuery = or),
            it
        },
        typeof t === Ct && (e.jQuery = e.$ = it),
        it
    })
});;
define("wiki-common:widget/lib/jsmart/jsmart.js",
function(require, exports, module) {
    function obMerge(e) {
        for (var r = 1; r < arguments.length; ++r) for (var t in arguments[r]) e[t] = arguments[r][t];
        return e
    }
    function countProperties(e) {
        var r = 0;
        for (var t in e) e.hasOwnProperty(t) && r++;
        return r
    }
    function findInArray(e, r) {
        if (Array.prototype.indexOf) return e.indexOf(r);
        for (var t = 0; t < e.length; ++t) if (e[t] === r) return t;
        return - 1
    }
    function evalString(e) {
        return e.replace(/\\t/, "	").replace(/\\n/, "\n").replace(/\\(['"\\])/g, "$1")
    }
    function trimQuotes(e) {
        return evalString(e.replace(/^['"](.*)['"]$/, "$1")).replace(/^\s+|\s+$/g, "")
    }
    function findTag(e, r) {
        for (var t = 0,
        a = 0,
        n = jSmart.prototype.left_delimiter,
        s = jSmart.prototype.right_delimiter,
        i = jSmart.prototype.auto_literal,
        o = /^\s*(.+)\s*$/i,
        p = e ? new RegExp("^\\s*(" + e + ")\\s*$", "i") : o, l = 0; l < r.length; ++l) if (r.substr(l, n.length) == n) {
            if (i && l + 1 < r.length && r.substr(l + 1, 1).match(/\s/)) continue;
            t || (r = r.slice(l), a += parseInt(l), l = 0),
            ++t
        } else if (r.substr(l, s.length) == s) {
            if (i && l - 1 >= 0 && r.substr(l - 1, 1).match(/\s/)) continue;
            if (!--t) {
                var c = r.slice(n.length, l).replace(/[\r\n]/g, " "),
                u = c.match(p);
                if (u) return u.index = a,
                u[0] = r.slice(0, l + s.length),
                u
            }
            0 > t && (t = 0)
        }
        return null
    }
    function findCloseTag(e, r, t) {
        var a = "",
        n = null,
        s = null,
        i = 0;
        do {
            if (n && (i += n[0].length), n = findTag(e, t), !n) throw new Error("Unclosed {" + r + "}");
            a += t.slice(0, n.index), i += n.index, t = t.slice(n.index + n[0].length), s = findTag(r, a), s && (a = a.slice(s.index + s[0].length))
        } while ( s );
        return n.index = i,
        n
    }
    function findElseTag(e, r, t, a) {
        for (var n = 0,
        s = findTag(t, a); s; s = findTag(t, a)) {
            var i = findTag(e, a);
            if (!i || i.index > s.index) return s.index += n,
            s;
            a = a.slice(i.index + i[0].length),
            n += i.index + i[0].length;
            var o = findCloseTag(r, e, a);
            a = a.slice(o.index + o[0].length),
            n += o.index + o[0].length
        }
        return null
    }
    function execute(code, data) {
        if ("string" == typeof code) with({
            __code: code
        }) with(modifiers) with(data) try {
            return eval(__code)
        } catch(e) {
            throw new Error(e.message + " in \n" + code)
        }
        return code
    }
    function assignVar(e, r, t) {
        e.match(/\[\]$/) ? t[e.replace(/\[\]$/, "")].push(r) : t[e] = r
    }
    function parse(e, r) {
        for (var t = findTag("", e); t; t = findTag("", e)) {
            t.index && parseText(e.slice(0, t.index), r),
            e = e.slice(t.index + t[0].length);
            var a = t[1].match(/^\s*(\w+)(.*)$/);
            if (a) {
                var n = a[1],
                s = a.length > 2 ? a[2].replace(/^\s+|\s+$/g, "") : "";
                if (n in buildInFunctions) {
                    var i = buildInFunctions[n],
                    o = ("parseParams" in i ? i.parseParams: parseParams)(s);
                    if ("block" == i.type) {
                        e = e.replace(/^\n/, "");
                        var p = findCloseTag("/" + n, n + " +[^}]*", e);
                        i.parse(o, r, e.slice(0, p.index)),
                        e = e.slice(p.index + p[0].length)
                    } else i.parse(o, r),
                    "extends" == n && (r = []);
                    e = e.replace(/^\n/, "")
                } else if (n in plugins) {
                    var l = plugins[n];
                    if ("block" == l.type) {
                        var p = findCloseTag("/" + n, n + " +[^}]*", e);
                        parsePluginBlock(n, parseParams(s), r, e.slice(0, p.index)),
                        e = e.slice(p.index + p[0].length)
                    } else "function" == l.type && parsePluginFunc(n, parseParams(s), r); ("append" == n || "assign" == n || "capture" == n || "eval" == n || "include" == n) && (e = e.replace(/^\n/, ""))
                } else buildInFunctions.expression.parse(t[1], r)
            } else {
                var c = buildInFunctions.expression.parse(t[1], r);
                "build-in" == c.type && "operator" == c.name && "=" == c.op && (e = e.replace(/^\n/, ""))
            }
        }
        return e && parseText(e, r),
        r
    }
    function parseText(e, r) {
        if (parseText.parseEmbeddedVars) for (var t = /([$][\w@]+)|`([^`]*)`/,
        a = t.exec(e); a; a = t.exec(e)) r.push({
            type: "text",
            data: e.slice(0, a.index)
        }),
        r.push(parseExpression(a[1] ? a[1] : a[2]).tree),
        e = e.slice(a.index + a[0].length);
        return r.push({
            type: "text",
            data: e
        }),
        r
    }
    function parseFunc(e, r, t) {
        return r.__parsed.name = parseText(e, [])[0],
        t.push({
            type: "plugin",
            name: "__func",
            params: r
        }),
        t
    }
    function parseOperator(e, r, t, a) {
        a.push({
            type: "build-in",
            name: "operator",
            op: e,
            optype: r,
            precedence: t,
            params: {}
        })
    }
    function parseVar(e, r, t) {
        for (var a = r.token,
        n = [{
            type: "text",
            data: t.replace(/^(\w+)@(key|index|iteration|first|last|show|total)/gi, "$1__$2")
        }], s = /^(?:\.|\s*->\s*|\[\s*)/, i = e.match(s); i; i = e.match(s)) {
            r.token += i[0],
            e = e.slice(i[0].length);
            var o = {
                value: "",
                tree: []
            };
            if (i[0].match(/\[/)) {
                o = parseExpression(e),
                o && (r.token += o.value, n.push(o.tree), e = e.slice(o.value.length));
                var p = e.match(/\s*\]/);
                p && (r.token += p[0], e = e.slice(p[0].length))
            } else {
                var l = parseModifiers.stop;
                if (parseModifiers.stop = !0, lookUp(e, o)) {
                    r.token += o.value;
                    var c = o.tree[0];
                    "plugin" == c.type && "__func" == c.name && (c.hasOwner = !0),
                    n.push(c),
                    e = e.slice(o.value.length)
                } else o = !1;
                parseModifiers.stop = l
            }
            o || n.push({
                type: "text",
                data: ""
            })
        }
        return r.tree.push({
            type: "var",
            parts: n
        }),
        r.value += r.token.substr(a.length),
        onParseVar(r.token),
        e
    }
    function onParseVar() {}
    function parseModifiers(e, r) {
        if (!parseModifiers.stop) {
            var t = e.match(/^\|(\w+)/);
            if (t) {
                r.value += t[0];
                var a = "default" == t[1] ? "defaultValue": t[1];
                e = e.slice(t[0].length).replace(/^\s+/, ""),
                parseModifiers.stop = !0;
                for (var n = [], s = e.match(/^\s*:\s*/); s; s = e.match(/^\s*:\s*/)) {
                    r.value += e.slice(0, s[0].length),
                    e = e.slice(s[0].length);
                    var i = {
                        value: "",
                        tree: []
                    };
                    lookUp(e, i) ? (r.value += i.value, n.push(i.tree[0]), e = e.slice(i.value.length)) : parseText("", n)
                }
                parseModifiers.stop = !1,
                n.unshift(r.tree.pop()),
                r.tree.push(parseFunc(a, {
                    __parsed: n
                },
                [])[0]),
                parseModifiers(e, r)
            }
        }
    }
    function lookUp(e, r) {
        if (!e) return ! 1;
        if (e.substr(0, jSmart.prototype.left_delimiter.length) == jSmart.prototype.left_delimiter) {
            var t = findTag("", e);
            if (t) return r.token = t[0],
            r.value += t[0],
            parse(t[0], r.tree),
            parseModifiers(e.slice(r.value.length), r),
            !0
        }
        for (var a = 0; a < tokens.length; ++a) if (e.match(tokens[a].re)) return r.token = RegExp.lastMatch,
        r.value += RegExp.lastMatch,
        tokens[a].parse(r, e.slice(r.token.length)),
        !0;
        return ! 1
    }
    function bundleOp(e, r, t) {
        var a = r[e];
        if ("operator" == a.name && a.precedence == t && !a.params.__parsed) {
            if ("binary" == a.optype) return a.params.__parsed = [r[e - 1], r[e + 1]],
            r.splice(e - 1, 3, a),
            !0;
            if ("post-unary" == a.optype) return a.params.__parsed = [r[e - 1]],
            r.splice(e - 1, 2, a),
            !0;
            a.params.__parsed = [r[e + 1]],
            r.splice(e, 2, a)
        }
        return ! 1
    }
    function composeExpression(e) {
        var r = 0;
        for (r = 0; r < e.length; ++r) e[r] instanceof Array && (e[r] = composeExpression(e[r]));
        for (var t = 1; 14 > t; ++t) if (2 == t || 10 == t) for (r = e.length; r > 0; --r) r -= bundleOp(r - 1, e, t);
        else for (r = 0; r < e.length; ++r) r -= bundleOp(r, e, t);
        return e[0]
    }
    function parseExpression(e) {
        for (var r = {
            value: "",
            tree: []
        }; lookUp(e.slice(r.value.length), r););
        return r.tree.length ? (r.tree = composeExpression(r.tree), r) : !1
    }
    function parseParams(e, r, t) {
        var a = e.replace(/\n/g, " ").replace(/^\s+|\s+$/g, ""),
        n = [];
        n.__parsed = [];
        var e = "";
        if (!a) return n;
        for (r || (r = /^\s+/, t = /^(\w+)\s*=\s*/); a;) {
            var s = null;
            if (t) {
                var i = a.match(t);
                i && (s = trimQuotes(i[1]), e += a.slice(0, i[0].length), a = a.slice(i[0].length))
            }
            var o = parseExpression(a);
            if (!o) break;
            s ? (n[s] = o.value, n.__parsed[s] = o.tree) : (n.push(o.value), n.__parsed.push(o.tree)),
            e += a.slice(0, o.value.length),
            a = a.slice(o.value.length);
            var p = a.match(r);
            if (!p) break;
            e += a.slice(0, p[0].length),
            a = a.slice(p[0].length)
        }
        return n.toString = function() {
            return e
        },
        n
    }
    function parsePluginBlock(e, r, t, a) {
        t.push({
            type: "plugin",
            name: e,
            params: r,
            subTree: parse(a, [])
        })
    }
    function parsePluginFunc(e, r, t) {
        t.push({
            type: "plugin",
            name: e,
            params: r
        })
    }
    function getActualParamValues(e, r) {
        var t = [];
        for (var a in e.__parsed) if (e.__parsed.hasOwnProperty(a)) {
            var n = process([e.__parsed[a]], r);
            t[a] = n
        }
        return t.__get = function(e, r, a) {
            if (e in t && "undefined" != typeof t[e]) return t[e];
            if ("undefined" != typeof a && "undefined" != typeof t[a]) return t[a];
            if (null === r) throw new Error("The required attribute '" + e + "' is missing");
            return r
        },
        t
    }
    function getVarValue(e, r, t) {
        for (var a = r,
        n = "",
        s = 0; s < e.parts.length; ++s) {
            var i = e.parts[s];
            if ("plugin" == i.type && "__func" == i.name && i.hasOwner) r.__owner = a,
            a = process([e.parts[s]], r),
            delete r.__owner;
            else if (n = process([i], r), n in r.smarty.section && "text" == i.type && "smarty" != process([e.parts[0]], r) && (n = r.smarty.section[n].index), !n && "undefined" != typeof t && a instanceof Array && (n = a.length), "undefined" != typeof t && s == e.parts.length - 1 && (a[n] = t), "object" == typeof a && null !== a && n in a) a = a[n];
            else {
                if ("undefined" == typeof t) return t;
                a[n] = {},
                a = a[n]
            }
        }
        return a
    }
    function process(e, r) {
        for (var t = "",
        a = 0; a < e.length; ++a) {
            var n = "",
            s = e[a];
            if ("text" == s.type) n = s.data;
            else if ("var" == s.type) n = getVarValue(s, r);
            else if ("build-in" == s.type) n = buildInFunctions[s.name].process(s, r);
            else if ("plugin" == s.type) {
                var i = plugins[s.name];
                if ("block" == i.type) {
                    var o = {
                        value: !0
                    };
                    for (i.process(getActualParamValues(s.params, r), "", r, o); o.value;) o.value = !1,
                    n += i.process(getActualParamValues(s.params, r), process(s.subTree, r), r, o)
                } else "function" == i.type && (n = i.process(getActualParamValues(s.params, r), r))
            }
            if ("boolean" == typeof n && (n = n ? "1": ""), 1 == e.length) return n;
            if (t += n, r.smarty["continue"] || r.smarty["break"]) return t
        }
        return t
    }
    function getTemplate(e, r, t) {
        if (!t && e in files) r = files[e];
        else {
            var a = jSmart.prototype.getTemplate(e);
            if ("string" != typeof a) throw new Error("No template for " + e);
            parse(applyFilters(jSmart.prototype.filters_global.pre, stripComments(a.replace(/\r\n/g, "\n"))), r),
            files[e] = r
        }
        return r
    }
    function stripComments(e) {
        for (var r = "",
        t = e.match(/{\*/); t; t = e.match(/{\*/)) {
            r += e.slice(0, t.index),
            e = e.slice(t.index + t[0].length);
            var a = e.match(/\*}/);
            if (!a) throw new Error("Unclosed {*");
            e = e.slice(a.index + a[0].length)
        }
        return r + e
    }
    function applyFilters(e, r) {
        for (var t = 0; t < e.length; ++t) r = e[t](r);
        return r
    }
    var buildInFunctions = {
        expression: {
            parse: function(e, r) {
                var t = parseExpression(e);
                return r.push({
                    type: "build-in",
                    name: "expression",
                    expression: t.tree,
                    params: parseParams(e.slice(t.value.length).replace(/^\s+|\s+$/g, ""))
                }),
                t.tree
            },
            process: function(e, r) {
                var t = getActualParamValues(e.params, r),
                a = process([e.expression], r);
                if (findInArray(t, "nofilter") < 0) {
                    for (var n = 0; n < default_modifiers.length; ++n) {
                        var s = default_modifiers[n];
                        s.params.__parsed[0] = {
                            type: "text",
                            data: a
                        },
                        a = process([s], r)
                    }
                    escape_html && (a = modifiers.escape(a)),
                    a = applyFilters(varFilters, a),
                    tpl_modifiers.length && (__t = function() {
                        return a
                    },
                    a = process(tpl_modifiers, r))
                }
                return a
            }
        },
        operator: {
            process: function(e, r) {
                var t = getActualParamValues(e.params, r),
                a = t[0];
                if ("binary" != e.optype) {
                    if ("!" == e.op) return ! a;
                    var n = "var" == e.params.__parsed[0].type;
                    n && (a = getVarValue(e.params.__parsed[0], r));
                    var s = a;
                    if ("pre-unary" == e.optype) {
                        switch (e.op) {
                        case "-":
                            s = -a;
                            break;
                        case "++":
                            s = ++a;
                            break;
                        case "--":
                            s = --a
                        }
                        n && getVarValue(e.params.__parsed[0], r, a)
                    } else {
                        switch (e.op) {
                        case "++":
                            a++;
                            break;
                        case "--":
                            a--
                        }
                        getVarValue(e.params.__parsed[0], r, a)
                    }
                    return s
                }
                var i = t[1];
                if ("=" == e.op) return getVarValue(e.params.__parsed[0], r, i),
                "";
                if (e.op.match(/(\+=|-=|\*=|\/=|%=)/)) {
                    switch (a = getVarValue(e.params.__parsed[0], r), e.op) {
                    case "+=":
                        a += i;
                        break;
                    case "-=":
                        a -= i;
                        break;
                    case "*=":
                        a *= i;
                        break;
                    case "/=":
                        a /= i;
                        break;
                    case "%=":
                        a %= i
                    }
                    return getVarValue(e.params.__parsed[0], r, a)
                }
                if (e.op.match(/div/)) return "div" != e.op ^ a % i == 0;
                if (e.op.match(/even/)) return "even" != e.op ^ a / i % 2 == 0;
                if (e.op.match(/xor/)) return (a || i) && !(a && i);
                switch (e.op) {
                case "==":
                    return a == i;
                case "!=":
                    return a != i;
                case "+":
                    return parseInt(a, 10) + parseInt(i, 10);
                case "-":
                    return parseInt(a, 10) - parseInt(i, 10);
                case "*":
                    return parseInt(a, 10) * parseInt(i, 10);
                case "/":
                    return parseInt(a, 10) / parseInt(i, 10);
                case "%":
                    return parseInt(a, 10) % parseInt(i, 10);
                case "&&":
                    return a && i;
                case "||":
                    return a || i;
                case "<":
                    return i > a;
                case "<=":
                    return i >= a;
                case ">":
                    return a > i;
                case ">=":
                    return a >= i;
                case "===":
                    return a === i;
                case "!==":
                    return a !== i
                }
            }
        },
        section: {
            type: "block",
            parse: function(e, r, t) {
                var a = [],
                n = [];
                r.push({
                    type: "build-in",
                    name: "section",
                    params: e,
                    subTree: a,
                    subTreeElse: n
                });
                var s = findElseTag("section [^}]+", "/section", "sectionelse", t);
                s ? (parse(t.slice(0, s.index), a), parse(t.slice(s.index + s[0].length).replace(/^[\r\n]/, ""), n)) : parse(t, a)
            },
            process: function(e, r) {
                var t = getActualParamValues(e.params, r),
                a = {};
                r.smarty.section[t.__get("name", null, 0)] = a;
                var n = t.__get("show", !0);
                if (a.show = n, !n) return process(e.subTreeElse, r);
                var s = parseInt(t.__get("start", 0)),
                i = t.loop instanceof Object ? countProperties(t.loop) : isNaN(t.loop) ? 0 : parseInt(t.loop),
                o = parseInt(t.__get("step", 1)),
                p = parseInt(t.__get("max"));
                isNaN(p) && (p = Number.MAX_VALUE),
                0 > s ? (s += i, 0 > s && (s = 0)) : s >= i && (s = i ? i - 1 : 0);
                for (var l = 0,
                c = 0,
                u = s; u >= 0 && i > u && p > l; u += o, ++l) c = u;
                a.total = l,
                a.loop = l,
                l = 0;
                var f = "";
                for (u = s; u >= 0 && i > u && p > l && !r.smarty["break"]; u += o, ++l) a.first = u == s,
                a.last = 0 > u + o || u + o >= i,
                a.index = u,
                a.index_prev = u - o,
                a.index_next = u + o,
                a.iteration = a.rownum = l + 1,
                f += process(e.subTree, r),
                r.smarty["continue"] = !1;
                return r.smarty["break"] = !1,
                l ? f: process(e.subTreeElse, r)
            }
        },
        setfilter: {
            type: "block",
            parseParams: function(e) {
                return [parseExpression("__t()|" + e).tree]
            },
            parse: function(e, r, t) {
                r.push({
                    type: "build-in",
                    name: "setfilter",
                    params: e,
                    subTree: parse(t, [])
                })
            },
            process: function(e, r) {
                tpl_modifiers = e.params;
                var t = process(e.subTree, r);
                return tpl_modifiers = [],
                t
            }
        },
        "for": {
            type: "block",
            parseParams: function(e) {
                var r = e.match(/^\s*\$(\w+)\s*=\s*([^\s]+)\s*to\s*([^\s]+)\s*(?:step\s*([^\s]+))?\s*(.*)$/);
                if (!r) throw new Error("Invalid {for} parameters: " + e);
                return parseParams("varName='" + r[1] + "' from=" + r[2] + " to=" + r[3] + " step=" + (r[4] ? r[4] : "1") + " " + r[5])
            },
            parse: function(e, r, t) {
                var a = [],
                n = [];
                r.push({
                    type: "build-in",
                    name: "for",
                    params: e,
                    subTree: a,
                    subTreeElse: n
                });
                var s = findElseTag("for\\s[^}]+", "/for", "forelse", t);
                s ? (parse(t.slice(0, s.index), a), parse(t.slice(s.index + s[0].length), n)) : parse(t, a)
            },
            process: function(e, r) {
                var t = getActualParamValues(e.params, r),
                a = parseInt(t.__get("from")),
                n = parseInt(t.__get("to")),
                s = parseInt(t.__get("step"));
                isNaN(s) && (s = 1);
                var i = parseInt(t.__get("max"));
                isNaN(i) && (i = Number.MAX_VALUE);
                for (var o = 0,
                p = "",
                l = Math.min(Math.ceil(((s > 0 ? n - a: a - n) + 1) / Math.abs(s)), i), c = parseInt(t.from); l > o && !r.smarty["break"]; c += s, ++o) r[t.varName] = c,
                p += process(e.subTree, r),
                r.smarty["continue"] = !1;
                return r.smarty["break"] = !1,
                o || (p = process(e.subTreeElse, r)),
                p
            }
        },
        "if": {
            type: "block",
            parse: function(e, r, t) {
                var a = [],
                n = [];
                r.push({
                    type: "build-in",
                    name: "if",
                    params: e,
                    subTreeIf: a,
                    subTreeElse: n
                });
                var s = findElseTag("if\\s+[^}]+", "/if", "else[^}]*", t);
                if (s) {
                    parse(t.slice(0, s.index), a),
                    t = t.slice(s.index + s[0].length);
                    var i = s[1].match(/^else\s*if(.*)/);
                    i ? buildInFunctions["if"].parse(parseParams(i[1]), n, t.replace(/^\n/, "")) : parse(t.replace(/^\n/, ""), n)
                } else parse(t, a)
            },
            process: function(e, r) {
                return getActualParamValues(e.params, r)[0] ? process(e.subTreeIf, r) : process(e.subTreeElse, r)
            }
        },
        foreach: {
            type: "block",
            parseParams: function(e) {
                var r = e.match(/^\s*([$].+)\s*as\s*[$](\w+)\s*(=>\s*[$](\w+))?\s*$/i);
                return r && (e = "from=" + r[1] + " item=" + (r[4] || r[2]), r[4] && (e += " key=" + r[2])),
                parseParams(e)
            },
            parse: function(e, r, t) {
                var a = [],
                n = [];
                r.push({
                    type: "build-in",
                    name: "foreach",
                    params: e,
                    subTree: a,
                    subTreeElse: n
                });
                var s = findElseTag("foreach\\s[^}]+", "/foreach", "foreachelse", t);
                s ? (parse(t.slice(0, s.index), a), parse(t.slice(s.index + s[0].length).replace(/^[\r\n]/, ""), n)) : parse(t, a)
            },
            process: function(e, r) {
                var t = getActualParamValues(e.params, r),
                a = t.from;
                if (void 0 == a) return "";
                a instanceof Object || (a = [a]);
                var n = countProperties(a);
                r[t.item + "__total"] = n,
                "name" in t && (r.smarty.foreach[t.name] = {},
                r.smarty.foreach[t.name].total = n);
                var s = "",
                i = 0;
                for (var o in a) if (a.hasOwnProperty(o)) {
                    if (r.smarty["break"]) break;
                    r[t.item + "__key"] = isNaN(o) ? o: parseInt(o),
                    "key" in t && (r[t.key] = r[t.item + "__key"]),
                    r[t.item] = a[o],
                    r[t.item + "__index"] = parseInt(i),
                    r[t.item + "__iteration"] = parseInt(i + 1),
                    r[t.item + "__first"] = 0 === i,
                    r[t.item + "__last"] = i == n - 1,
                    "name" in t && (r.smarty.foreach[t.name].index = parseInt(i), r.smarty.foreach[t.name].iteration = parseInt(i + 1), r.smarty.foreach[t.name].first = 0 === i ? 1 : "", r.smarty.foreach[t.name].last = i == n - 1 ? 1 : ""),
                    ++i,
                    s += process(e.subTree, r),
                    r.smarty["continue"] = !1
                }
                return r.smarty["break"] = !1,
                r[t.item + "__show"] = i > 0,
                t.name && (r.smarty.foreach[t.name].show = i > 0 ? 1 : ""),
                i > 0 ? s: process(e.subTreeElse, r)
            }
        },
        "function": {
            type: "block",
            parse: function(e, r, t) {
                var a = [];
                plugins[trimQuotes(e.name ? e.name: e[0])] = {
                    type: "function",
                    subTree: a,
                    defautParams: e,
                    process: function(e, r) {
                        var t = getActualParamValues(this.defautParams, r);
                        return delete t.name,
                        process(this.subTree, obMerge({},
                        r, t, e))
                    }
                },
                parse(t, a)
            }
        },
        php: {
            type: "block",
            parse: function() {}
        },
        "extends": {
            type: "function",
            parse: function(e, r) {
                r.splice(0, r.length),
                getTemplate(trimQuotes(e.file ? e.file: e[0]), r)
            }
        },
        block: {
            type: "block",
            parse: function(e, r, t) {
                r.push({
                    type: "build-in",
                    name: "block",
                    params: e
                }),
                e.append = findInArray(e, "append") >= 0,
                e.prepend = findInArray(e, "prepend") >= 0,
                e.hide = findInArray(e, "hide") >= 0,
                e.hasChild = e.hasParent = !1,
                onParseVar = function(r) {
                    r.match(/^\s*[$]smarty.block.child\s*$/) && (e.hasChild = !0),
                    r.match(/^\s*[$]smarty.block.parent\s*$/) && (e.hasParent = !0)
                };
                var r = parse(t, []);
                onParseVar = function() {};
                var a = trimQuotes(e.name ? e.name: e[0]);
                a in blocks || (blocks[a] = []),
                blocks[a].push({
                    tree: r,
                    params: e
                })
            },
            process: function(e, r) {
                r.smarty.block.parent = r.smarty.block.child = "";
                var t = trimQuotes(e.params.name ? e.params.name: e.params[0]);
                return this.processBlocks(blocks[t], blocks[t].length - 1, r),
                r.smarty.block.child
            },
            processBlocks: function(e, r, t) {
                if (!r && e[r].params.hide) return void(t.smarty.block.child = "");
                for (var a = !0,
                n = !1; r >= 0; --r) {
                    if (e[r].params.hasParent) {
                        var s = t.smarty.block.child;
                        t.smarty.block.child = "",
                        this.processBlocks(e, r - 1, t),
                        t.smarty.block.parent = t.smarty.block.child,
                        t.smarty.block.child = s
                    }
                    var s = t.smarty.block.child,
                    i = process(e[r].tree, t);
                    t.smarty.block.child = s,
                    e[r].params.hasChild ? t.smarty.block.child = i: a ? t.smarty.block.child = i + t.smarty.block.child: n && (t.smarty.block.child += i),
                    a = e[r].params.append,
                    n = e[r].params.prepend
                }
            }
        },
        strip: {
            type: "block",
            parse: function(e, r, t) {
                parse(t.replace(/[ \t]*[\r\n]+[ \t]*/g, ""), r)
            }
        },
        literal: {
            type: "block",
            parse: function(e, r, t) {
                parseText(t, r)
            }
        },
        ldelim: {
            type: "function",
            parse: function(e, r) {
                parseText(jSmart.prototype.left_delimiter, r)
            }
        },
        rdelim: {
            type: "function",
            parse: function(e, r) {
                parseText(jSmart.prototype.right_delimiter, r)
            }
        },
        "while": {
            type: "block",
            parse: function(e, r, t) {
                r.push({
                    type: "build-in",
                    name: "while",
                    params: e,
                    subTree: parse(t, [])
                })
            },
            process: function(e, r) {
                for (var t = ""; getActualParamValues(e.params, r)[0] && !r.smarty["break"];) t += process(e.subTree, r),
                r.smarty["continue"] = !1;
                return r.smarty["break"] = !1,
                t
            }
        }
    },
    plugins = {},
    modifiers = {},
    files = {},
    blocks = null,
    scripts = null,
    tpl_modifiers = [],
    tokens = [{
        re: /^\$([\w@]+)/,
        parse: function(e, r) {
            parseModifiers(parseVar(r, e, RegExp.$1), e)
        }
    },
    {
        re: /^(true|false)/i,
        parse: function(e) {
            parseText(e.token.match(/true/i) ? "1": "", e.tree)
        }
    },
    {
        re: /^'([^'\\]*(?:\\.[^'\\]*)*)'/,
        parse: function(e, r) {
            parseText(evalString(RegExp.$1), e.tree),
            parseModifiers(r, e)
        }
    },
    {
        re: /^"([^"\\]*(?:\\.[^"\\]*)*)"/,
        parse: function(e, r) {
            var t = evalString(RegExp.$1),
            a = t.match(tokens[0].re);
            if (a) {
                var n = {
                    token: a[0],
                    tree: []
                };
                if (parseVar(t, n, a[1]), n.token.length == t.length) return void e.tree.push(n.tree[0])
            }
            parseText.parseEmbeddedVars = !0,
            e.tree.push({
                type: "plugin",
                name: "__quoted",
                params: {
                    __parsed: parse(t, [])
                }
            }),
            parseText.parseEmbeddedVars = !1,
            parseModifiers(r, e)
        }
    },
    {
        re: /^(\w+)\s*[(]([)]?)/,
        parse: function(e, r) {
            var t = RegExp.$1,
            a = RegExp.$2,
            n = parseParams(a ? "": r, /^\s*,\s*/);
            parseFunc(t, n, e.tree),
            e.value += n.toString(),
            parseModifiers(r.slice(n.toString().length), e)
        }
    },
    {
        re: /^\s*\(\s*/,
        parse: function(e) {
            var r = [];
            e.tree.push(r),
            r.parent = e.tree,
            e.tree = r
        }
    },
    {
        re: /^\s*\)\s*/,
        parse: function(e) {
            e.tree.parent && (e.tree = e.tree.parent)
        }
    },
    {
        re: /^\s*(\+\+|--)\s*/,
        parse: function(e) {
            e.tree.length && "var" == e.tree[e.tree.length - 1].type ? parseOperator(RegExp.$1, "post-unary", 1, e.tree) : parseOperator(RegExp.$1, "pre-unary", 1, e.tree)
        }
    },
    {
        re: /^\s*(===|!==|==|!=)\s*/,
        parse: function(e) {
            parseOperator(RegExp.$1, "binary", 6, e.tree)
        }
    },
    {
        re: /^\s+(eq|ne|neq)\s+/i,
        parse: function(e) {
            var r = RegExp.$1.replace(/ne(q)?/, "!=").replace(/eq/, "==");
            parseOperator(r, "binary", 6, e.tree)
        }
    },
    {
        re: /^\s*!\s*/,
        parse: function(e) {
            parseOperator("!", "pre-unary", 2, e.tree)
        }
    },
    {
        re: /^\s+not\s+/i,
        parse: function(e) {
            parseOperator("!", "pre-unary", 2, e.tree)
        }
    },
    {
        re: /^\s*(=|\+=|-=|\*=|\/=|%=)\s*/,
        parse: function(e) {
            parseOperator(RegExp.$1, "binary", 10, e.tree)
        }
    },
    {
        re: /^\s*(\*|\/|%)\s*/,
        parse: function(e) {
            parseOperator(RegExp.$1, "binary", 3, e.tree)
        }
    },
    {
        re: /^\s+mod\s+/i,
        parse: function(e) {
            parseOperator("%", "binary", 3, e.tree)
        }
    },
    {
        re: /^\s*(\+|-)\s*/,
        parse: function(e) {
            e.tree.length && "operator" != e.tree[e.tree.length - 1].name ? parseOperator(RegExp.$1, "binary", 4, e.tree) : parseOperator(RegExp.$1, "pre-unary", 4, e.tree)
        }
    },
    {
        re: /^\s*(<=|>=|<>|<|>)\s*/,
        parse: function(e) {
            parseOperator(RegExp.$1.replace(/<>/, "!="), "binary", 5, e.tree)
        }
    },
    {
        re: /^\s+(lt|lte|le|gt|gte|ge)\s+/i,
        parse: function(e) {
            var r = RegExp.$1.replace(/lt/, "<").replace(/l(t)?e/, "<=").replace(/gt/, ">").replace(/g(t)?e/, ">=");
            parseOperator(r, "binary", 5, e.tree)
        }
    },
    {
        re: /^\s+(is\s+(not\s+)?div\s+by)\s+/i,
        parse: function(e) {
            parseOperator(RegExp.$2 ? "div_not": "div", "binary", 7, e.tree)
        }
    },
    {
        re: /^\s+is\s+(not\s+)?(even|odd)(\s+by\s+)?\s*/i,
        parse: function(e) {
            var r = RegExp.$1 ? "odd" == RegExp.$2 ? "even": "even_not": "odd" == RegExp.$2 ? "even_not": "even";
            parseOperator(r, "binary", 7, e.tree),
            RegExp.$3 || parseText("1", e.tree)
        }
    },
    {
        re: /^\s*(&&)\s*/,
        parse: function(e) {
            parseOperator(RegExp.$1, "binary", 8, e.tree)
        }
    },
    {
        re: /^\s*(\|\|)\s*/,
        parse: function(e) {
            parseOperator(RegExp.$1, "binary", 9, e.tree)
        }
    },
    {
        re: /^\s+and\s+/i,
        parse: function(e) {
            parseOperator("&&", "binary", 11, e.tree)
        }
    },
    {
        re: /^\s+xor\s+/i,
        parse: function(e) {
            parseOperator("xor", "binary", 12, e.tree)
        }
    },
    {
        re: /^\s+or\s+/i,
        parse: function(e) {
            parseOperator("||", "binary", 13, e.tree)
        }
    },
    {
        re: /^#(\w+)#/,
        parse: function(e, r) {
            var t = {
                token: "$smarty",
                tree: []
            };
            parseVar(".config." + RegExp.$1, t, "smarty"),
            e.tree.push(t.tree[0]),
            parseModifiers(r, e)
        }
    },
    {
        re: /^\s*\[\s*/,
        parse: function(e, r) {
            var t = parseParams(r, /^\s*,\s*/, /^('[^'\\]*(?:\\.[^'\\]*)*'|"[^"\\]*(?:\\.[^"\\]*)*"|\w+)\s*=>\s*/);
            parsePluginFunc("__array", t, e.tree),
            e.value += t.toString();
            var a = r.slice(t.toString().length).match(/\s*\]/);
            a && (e.value += a[0])
        }
    },
    {
        re: /^[\d.]+/,
        parse: function(e, r) {
            e.token = e.token.indexOf(".") > -1 ? parseFloat(e.token) : parseInt(e.token, 10),
            parseText(e.token, e.tree),
            parseModifiers(r, e)
        }
    },
    {
        re: /^\w+/,
        parse: function(e, r) {
            parseText(e.token, e.tree),
            parseModifiers(r, e)
        }
    }],
    jSmart = function(e) {
        this.tree = [],
        this.tree.blocks = {},
        this.scripts = {},
        this.default_modifiers = [],
        this.filters = {
            variable: [],
            post: []
        },
        this.smarty = {
            smarty: {
                block: {},
                "break": !1,
                capture: {},
                "continue": !1,
                counter: {},
                cycle: {},
                foreach: {},
                section: {},
                now: Math.floor((new Date).getTime() / 1e3),
                "const": {},
                config: {},
                current_dir: "/",
                template: "",
                ldelim: jSmart.prototype.left_delimiter,
                rdelim: jSmart.prototype.right_delimiter,
                version: "2.13.1"
            }
        },
        blocks = this.tree.blocks,
        parse(applyFilters(jSmart.prototype.filters_global.pre, stripComments(new String(e ? e: "").replace(/\|f_escape_\w+/g, "").replace(/\r\n/g, "\n"))), this.tree)
    };
    jSmart.prototype.fetch = function(e) {
        blocks = this.tree.blocks,
        scripts = this.scripts,
        escape_html = this.escape_html,
        default_modifiers = jSmart.prototype.default_modifiers_global.concat(this.default_modifiers),
        this.data = obMerge("object" == typeof e ? e: {},
        this.smarty),
        varFilters = jSmart.prototype.filters_global.variable.concat(this.filters.variable);
        var r = process(this.tree, this.data);
        return jSmart.prototype.debugging && plugins.debug.process([], this.data),
        applyFilters(jSmart.prototype.filters_global.post.concat(this.filters.post), r)
    },
    jSmart.prototype.escape_html = !1,
    jSmart.prototype.registerPlugin = function(e, r, t) {
        "modifier" == e ? modifiers[r] = t: plugins[r] = {
            type: e,
            process: t
        }
    },
    jSmart.prototype.registerFilter = function(e, r) { (this.tree ? this.filters: jSmart.prototype.filters_global)["output" == e ? "post": e].push(r)
    },
    jSmart.prototype.filters_global = {
        pre: [],
        variable: [],
        post: []
    },
    jSmart.prototype.configLoad = function(e, r, t) {
        t = t ? t: this.data;
        for (var a = e.replace(/\r\n/g, "\n").replace(/^\s+|\s+$/g, ""), n = /^\s*(?:\[([^\]]+)\]|(?:(\w+)[ \t]*=[ \t]*("""|'[^'\\\n]*(?:\\.[^'\\\n]*)*'|"[^"\\\n]*(?:\\.[^"\\\n]*)*"|[^\n]*)))/m, s = "", i = a.match(n); i; i = a.match(n)) {
            if (a = a.slice(i.index + i[0].length), i[1]) s = i[1];
            else if ((!s || s == r) && "." != s.substr(0, 1)) if ('"""' == i[3]) {
                var o = a.match(/"""/);
                o && (t.smarty.config[i[2]] = a.slice(0, o.index), a = a.slice(o.index + o[0].length))
            } else t.smarty.config[i[2]] = trimQuotes(i[3]);
            var p = a.match(/\n+/);
            if (!p) break;
            a = a.slice(p.index + p[0].length)
        }
    },
    jSmart.prototype.clearConfig = function(e) {
        e ? delete this.data.smarty.config[e] : this.data.smarty.config = {}
    },
    jSmart.prototype.addDefaultModifier = function(e) {
        e instanceof Array || (e = [e]);
        for (var r = 0; r < e.length; ++r) {
            var t = {
                value: "",
                tree: [0]
            };
            parseModifiers("|" + e[r], t),
            (this.tree ? this.default_modifiers: this.default_modifiers_global).push(t.tree[0])
        }
    },
    jSmart.prototype.default_modifiers_global = [],
    jSmart.prototype.getTemplate = function(e) {
        return __inline(e)
    },
    jSmart.prototype.getFile = function(e) {
        return __inline(e)
    },
    jSmart.prototype.getJavascript = function(e) {
        return __inline(e)
    },
    jSmart.prototype.getConfig = function(e) {
        return __inline(e)
    },
    jSmart.prototype.auto_literal = !0,
    jSmart.prototype.left_delimiter = "{%",
    jSmart.prototype.right_delimiter = "%}",
    jSmart.prototype.debugging = !1,
    jSmart.prototype.PHPJS = function(fnm, modifier) {
        if ("function" == eval("typeof " + fnm)) return "object" == typeof window ? window: global;
        if ("function" == typeof PHP_JS) return new PHP_JS;
        throw new Error("Modifier '" + modifier + "' uses JavaScript port of PHP function '" + fnm + "'. You can find one at http://phpjs.org")
    },
    jSmart.prototype.makeTimeStamp = function(e) {
        if (!e) return Math.floor((new Date).getTime() / 1e3);
        if (isNaN(e)) {
            var r = jSmart.prototype.PHPJS("strtotime", "date_format").strtotime(e);
            return - 1 == r || r === !1 ? Math.floor((new Date).getTime() / 1e3) : r
        }
        return e = new String(e),
        14 == e.length ? Math.floor(new Date(e.substr(0, 4), e.substr(4, 2) - 1, e.substr(6, 2), e.substr(8, 2), e.substr(10, 2)).getTime() / 1e3) : parseInt(e)
    },
    jSmart.prototype.registerPlugin("function", "__array",
    function(e) {
        var r = [];
        for (var t in e) e.hasOwnProperty(t) && e[t] && "function" != typeof e[t] && (r[t] = e[t]);
        return r
    }),
    jSmart.prototype.registerPlugin("function", "__func",
    function(e, r) {
        for (var t = [], a = {},
        n = 0; n < e.length; ++n) t.push(e.name + "__p" + n),
        a[e.name + "__p" + n] = e[n];
        var s = "__owner" in r && e.name in r.__owner ? "__owner." + e.name: e.name;
        return execute(s + "(" + t.join(",") + ")", obMerge({},
        r, a))
    }),
    jSmart.prototype.registerPlugin("function", "__quoted",
    function(e) {
        return e.join("")
    }),
    jSmart.prototype.registerPlugin("function", "append",
    function(e, r) {
        var t = e.__get("var", null, 0);
        t in r && r[t] instanceof Array || (r[t] = []);
        var a = e.__get("index", !1),
        n = e.__get("value", null, 1);
        return a === !1 ? r[t].push(n) : r[t][a] = n,
        ""
    }),
    jSmart.prototype.registerPlugin("function", "assign",
    function(e, r) {
        return assignVar(e.__get("var", null, 0), e.__get("value", null, 1), r),
        ""
    }),
    jSmart.prototype.registerPlugin("function", "break",
    function(e, r) {
        return r.smarty["break"] = !0,
        ""
    }),
    jSmart.prototype.registerPlugin("function", "call",
    function(e, r) {
        var t = e.__get("name", null, 0);
        delete e.name;
        var a = e.__get("assign", !1);
        delete e.assign;
        var n = plugins[t].process(e, r);
        return a ? (assignVar(a, n, r), "") : n
    }),
    jSmart.prototype.registerPlugin("block", "capture",
    function(e, r, t) {
        if (r) {
            r = r.replace(/^\n/, ""),
            t.smarty.capture[e.__get("name", "default", 0)] = r,
            "assign" in e && assignVar(e.assign, r, t);
            var a = e.__get("append", !1);
            a && (a in t ? t[a] instanceof Array && t[a].push(r) : t[a] = [r])
        }
        return ""
    }),
    jSmart.prototype.registerPlugin("function", "continue",
    function(e, r) {
        return r.smarty["continue"] = !0,
        ""
    }),
    jSmart.prototype.registerPlugin("function", "counter",
    function(e, r) {
        var t = e.__get("name", "default");
        if (t in r.smarty.counter) {
            var a = r.smarty.counter[t];
            "start" in e ? a.value = parseInt(e.start) : (a.value = parseInt(a.value), a.skip = parseInt(a.skip), "down" == a.direction ? a.value -= a.skip: a.value += a.skip),
            a.skip = e.__get("skip", a.skip),
            a.direction = e.__get("direction", a.direction),
            a.assign = e.__get("assign", a.assign)
        } else r.smarty.counter[t] = {
            value: parseInt(e.__get("start", 1)),
            skip: parseInt(e.__get("skip", 1)),
            direction: e.__get("direction", "up"),
            assign: e.__get("assign", !1)
        };
        return r.smarty.counter[t].assign ? (r[r.smarty.counter[t].assign] = r.smarty.counter[t].value, "") : e.__get("print", !0) ? r.smarty.counter[t].value: ""
    }),
    jSmart.prototype.registerPlugin("function", "cycle",
    function(e, r) {
        var t = e.__get("name", "default"),
        a = e.__get("reset", !1);
        t in r.smarty.cycle || (r.smarty.cycle[t] = {
            arr: [""],
            delimiter: e.__get("delimiter", ","),
            index: 0
        },
        a = !0),
        e.__get("delimiter", !1) && (r.smarty.cycle[t].delimiter = e.delimiter);
        var n = e.__get("values", !1);
        if (n) {
            var s = [];
            if (n instanceof Object) for (nm in n) s.push(n[nm]);
            else s = n.split(r.smarty.cycle[t].delimiter); (s.length != r.smarty.cycle[t].arr.length || s[0] != r.smarty.cycle[t].arr[0]) && (r.smarty.cycle[t].arr = s, r.smarty.cycle[t].index = 0, a = !0)
        }
        return e.__get("advance", "true") && (r.smarty.cycle[t].index += 1),
        (r.smarty.cycle[t].index >= r.smarty.cycle[t].arr.length || a) && (r.smarty.cycle[t].index = 0),
        e.__get("assign", !1) ? (assignVar(e.assign, r.smarty.cycle[t].arr[r.smarty.cycle[t].index], r), "") : e.__get("print", !0) ? r.smarty.cycle[t].arr[r.smarty.cycle[t].index] : ""
    }),
    jSmart.prototype.print_r = function(e, r) {
        if (e instanceof Object) {
            var t = (e instanceof Array ? "Array[" + e.length + "]": "Object") + "<br>";
            for (var a in e) e.hasOwnProperty(a) && (t += r + "&nbsp;&nbsp;<strong>" + a + "</strong> : " + jSmart.prototype.print_r(e[a], r + "&nbsp;&nbsp;&nbsp;") + "<br>");
            return t
        }
        return e
    },
    jSmart.prototype.registerPlugin("function", "debug",
    function(e, r) {
        "undefined" != typeof dbgWnd && dbgWnd.close(),
        dbgWnd = window.open("", "", "width=680,height=600,resizable,scrollbars=yes");
        var t = "",
        a = 0;
        for (var n in r) t += "<tr class=" + (++a % 2 ? "odd": "even") + "><td><strong>" + n + "</strong></td><td>" + jSmart.prototype.print_r(r[n], "") + "</td></tr>";
        return dbgWnd.document.write("                <html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en'>                <head>                 <title>jSmart Debug Console</title>                   <style type='text/css'>                      table {width: 100%;}                      td {vertical-align:top;width: 50%;}                      .even td {background-color: #fafafa;}                   </style>                </head>                <body>                   <h1>jSmart Debug Console</h1>                   <h2>assigned template variables</h2>                   <table>" + t + "</table>                </body>                </html>             "),
        ""
    }),
    jSmart.prototype.registerPlugin("function", "eval",
    function(e, r) {
        var t = [];
        parse(e.__get("var", "", 0), t);
        var a = process(t, r);
        return "assign" in e ? (assignVar(e.assign, a, r), "") : a
    }),
    jSmart.prototype.registerPlugin("function", "fetch",
    function(e, r) {
        var t = jSmart.prototype.getFile(e.__get("file", null, 0));
        return "assign" in e ? (assignVar(e.assign, t, r), "") : t
    }),
    jSmart.prototype.registerPlugin("function", "html_checkboxes",
    function(e, r) {
        var t, a, n, s = e.__get("type", "checkbox"),
        i = e.__get("name", s),
        o = e.__get("name", s),
        p = e.__get("values", e.options),
        l = e.__get("options", []),
        c = "options" in e,
        u = e.__get("selected", !1),
        f = e.__get("separator", ""),
        g = Boolean(e.__get("labels", !0)),
        m = Boolean(e.__get("label_ids", !1)),
        d = [],
        h = 0,
        y = "";
        if ("checkbox" == s && (i += "[]"), !c) for (t in e.output) l.push(e.output[t]);
        for (t in p) p.hasOwnProperty(t) && (a = c ? t: p[t], n = o + "_" + a, y = g ? m ? '<label for="' + n + '">': "<label>": "", y += '<input type="' + s + '" name="' + i + '" value="' + a + '" ', m && (y += 'id="' + n + '" '), u == (c ? t: p[t]) && (y += 'checked="checked" '), y += "/>" + l[c ? t: h++], y += g ? "</label>": "", y += f, d.push(y));
        return "assign" in e ? (assignVar(e.assign, d, r), "") : d.join("\n")
    }),
    jSmart.prototype.registerPlugin("function", "html_image",
    function(e) {
        var r, t = e.__get("file", null),
        a = e.__get("width", !1),
        n = e.__get("height", !1),
        s = e.__get("alt", ""),
        i = e.__get("href", e.__get("link", !1)),
        o = e.__get("path_prefix", ""),
        p = {
            file: 1,
            width: 1,
            height: 1,
            alt: 1,
            href: 1,
            basedir: 1,
            path_prefix: 1,
            link: 1
        },
        l = '<img src="' + o + t + '" alt="' + s + '"' + (a ? ' width="' + a + '"': "") + (n ? ' height="' + n + '"': "");
        for (r in e) e.hasOwnProperty(r) && "string" == typeof e[r] && (r in p || (l += " " + r + '="' + e[r] + '"'));
        return l += " />",
        i ? '<a href="' + i + '">' + l + "</a>": l
    }),
    jSmart.prototype.registerPlugin("function", "html_options",
    function(e) {
        var r, t = e.__get("values", e.options),
        a = e.__get("options", []),
        n = "options" in e;
        if (!n) for (r in e.output) a.push(e.output[r]);
        var s = e.__get("selected", !1),
        i = [],
        o = "",
        p = 0;
        for (r in t) t.hasOwnProperty(r) && (o = '<option value="' + (n ? r: t[r]) + '"', s == (n ? r: t[r]) && (o += ' selected="selected"'), o += ">" + a[n ? r: p++] + "</option>", i.push(o));
        var l = e.__get("name", !1);
        return (l ? '<select name="' + l + '">\n' + i.join("\n") + "\n</select>": i.join("\n")) + "\n"
    }),
    jSmart.prototype.registerPlugin("function", "html_radios",
    function(e, r) {
        return e.type = "radio",
        plugins.html_checkboxes.process(e, r)
    }),
    jSmart.prototype.registerPlugin("function", "html_select_date",
    function(e) {
        var r = e.__get("prefix", "Date_"),
        t = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
        a = "";
        a += '<select name="' + r + 'Month">\n';
        var n = 0;
        for (n = 0; n < t.length; ++n) a += '<option value="' + n + '">' + t[n] + "</option>\n";
        for (a += "</select>\n", a += '<select name="' + r + 'Day">\n', n = 0; 31 > n; ++n) a += '<option value="' + n + '">' + n + "</option>\n";
        return a += "</select>\n"
    }),
    jSmart.prototype.registerPlugin("function", "html_table",
    function(e) {
        var r, t = [];
        if (e.loop instanceof Array) t = e.loop;
        else for (r in e.loop) e.loop.hasOwnProperty(r) && t.push(e.loop[r]);
        var a = e.__get("rows", !1),
        n = e.__get("cols", !1);
        n || (n = a ? Math.ceil(t.length / a) : 3);
        var s = [];
        if (isNaN(n)) {
            if ("object" == typeof n) for (r in n) n.hasOwnProperty(r) && s.push(n[r]);
            else s = n.split(/\s*,\s*/);
            n = s.length
        }
        a = a ? a: Math.ceil(t.length / n);
        var i = e.__get("inner", "cols"),
        o = e.__get("caption", ""),
        p = e.__get("table_attr", 'border="1"'),
        l = e.__get("th_attr", !1);
        l && "object" != typeof l && (l = [l]);
        var c = e.__get("tr_attr", !1);
        c && "object" != typeof c && (c = [c]);
        var u = e.__get("td_attr", !1);
        u && "object" != typeof u && (u = [u]);
        for (var f = e.__get("trailpad", "&nbsp;"), g = e.__get("hdir", "right"), m = e.__get("vdir", "down"), d = "", h = 0; a > h; ++h) {
            d += "<tr" + (c ? " " + c[h % c.length] : "") + ">\n";
            for (var y = 0; n > y; ++y) {
                var _ = "cols" == i ? ("down" == m ? h: a - 1 - h) * n + ("right" == g ? y: n - 1 - y) : ("right" == g ? y: n - 1 - y) * a + ("down" == m ? h: a - 1 - h);
                d += "<td" + (u ? " " + u[y % u.length] : "") + ">" + (_ < t.length ? t[_] : f) + "</td>\n"
            }
            d += "</tr>\n"
        }
        var v = "";
        if (s.length) {
            v = "\n<thead><tr>";
            for (var b = 0; b < s.length; ++b) v += "\n<th" + (l ? " " + l[b % l.length] : "") + ">" + s["right" == g ? b: s.length - 1 - b] + "</th>";
            v += "\n</tr></thead>"
        }
        return "<table " + p + ">" + (o ? "\n<caption>" + o + "</caption>": "") + v + "\n<tbody>\n" + d + "</tbody>\n</table>\n"
    }),
    jSmart.prototype.registerPlugin("function", "include",
    function(e, r) {
        var t = e.__get("file", null, 0),
        a = obMerge({},
        r, e);
        a.smarty.template = t;
        var n = process(getTemplate(t, [], findInArray(e, "nocache") >= 0), a);
        return "assign" in e ? (assignVar(e.assign, n, r), "") : n
    }),
    jSmart.prototype.registerPlugin("function", "include_javascript",
    function(e, r) {
        var t = e.__get("file", null, 0);
        if (e.__get("once", !0) && t in scripts) return "";
        scripts[t] = !0;
        var a = execute(jSmart.prototype.getJavascript(t), {
            $this: r
        });
        return "assign" in e ? (assignVar(e.assign, a, r), "") : a
    }),
    jSmart.prototype.registerPlugin("function", "include_php",
    function(e, r) {
        return plugins.include_javascript.process(e, r)
    }),
    jSmart.prototype.registerPlugin("function", "insert",
    function(params, data) {
        var fparams = {};
        for (var nm in params) params.hasOwnProperty(nm) && isNaN(nm) && params[nm] && "string" == typeof params[nm] && "name" != nm && "assign" != nm && "script" != nm && (fparams[nm] = params[nm]);
        var prefix = "insert_";
        "script" in params && (eval(jSmart.prototype.getJavascript(params.script)), prefix = "smarty_insert_");
        var func = eval(prefix + params.__get("name", null, 0)),
        s = func(fparams, data);
        return "assign" in params ? (assignVar(params.assign, s, data), "") : s
    }),
    jSmart.prototype.registerPlugin("block", "javascript",
    function(e, r, t) {
        return t.$this = t,
        execute(r, t),
        delete t.$this,
        ""
    }),
    jSmart.prototype.registerPlugin("function", "config_load",
    function(e, r) {
        return jSmart.prototype.configLoad(jSmart.prototype.getConfig(e.__get("file", null, 0)), e.__get("section", "", 1), r),
        ""
    }),
    jSmart.prototype.registerPlugin("function", "mailto",
    function(e) {
        var r = e.__get("address", null),
        t = e.__get("encode", "none"),
        a = e.__get("text", r),
        n = jSmart.prototype.PHPJS("rawurlencode", "mailto").rawurlencode(e.__get("cc", "")).replace("%40", "@"),
        i = jSmart.prototype.PHPJS("rawurlencode", "mailto").rawurlencode(e.__get("bcc", "")).replace("%40", "@"),
        o = jSmart.prototype.PHPJS("rawurlencode", "mailto").rawurlencode(e.__get("followupto", "")).replace("%40", "@"),
        p = jSmart.prototype.PHPJS("rawurlencode", "mailto").rawurlencode(e.__get("subject", "")),
        l = jSmart.prototype.PHPJS("rawurlencode", "mailto").rawurlencode(e.__get("newsgroups", "")),
        c = e.__get("extra", "");
        if (r += n ? "?cc=" + n: "", r += i ? (n ? "&": "?") + "bcc=" + i: "", r += p ? (n || i ? "&": "?") + "subject=" + p: "", r += l ? (n || i || p ? "&": "?") + "newsgroups=" + l: "", r += o ? (n || i || p || l ? "&": "?") + "followupto=" + o: "", s = '<a href="mailto:' + r + '" ' + c + ">" + a + "</a>", "javascript" == t) {
            s = "document.write('" + s + "');";
            for (var u = "",
            f = 0; f < s.length; ++f) u += "%" + jSmart.prototype.PHPJS("bin2hex", "mailto").bin2hex(s.substr(f, 1));
            return '<script type="text/javascript">eval(unescape(\'' + u + "'))</script>"
        }
        if ("javascript_charcode" == t) {
            for (var g = [], f = 0; f < s.length; ++f) g.push(jSmart.prototype.PHPJS("ord", "mailto").ord(s.substr(f, 1)));
            return '<script type="text/javascript" language="javascript">\n<!--\n{document.write(String.fromCharCode(' + g.join(",") + "))}\n//-->\n</script>\n"
        }
        if ("hex" == t) {
            if (r.match(/^.+\?.+$/)) throw new Error("mailto: hex encoding does not work with extra attributes. Try javascript.");
            for (var m = "",
            f = 0; f < r.length; ++f) m += r.substr(f, 1).match(/\w/) ? "%" + jSmart.prototype.PHPJS("bin2hex", "mailto").bin2hex(r.substr(f, 1)) : r.substr(f, 1);
            for (var d = "",
            f = 0; f < a.length; ++f) d += "&#x" + jSmart.prototype.PHPJS("bin2hex", "mailto").bin2hex(a.substr(f, 1)) + ";";
            return '<a href="&#109;&#97;&#105;&#108;&#116;&#111;&#58;' + m + '" ' + c + ">" + d + "</a>"
        }
        return s
    }),
    jSmart.prototype.registerPlugin("function", "math",
    function(params, data) {
        with(Math) with(params) var res = eval(params.__get("equation", null).replace(/pi\(\s*\)/g, "PI"));
        return "format" in params && (res = jSmart.prototype.PHPJS("sprintf", "math").sprintf(params.format, res)),
        "assign" in params ? (assignVar(params.assign, res, data), "") : res
    }),
    jSmart.prototype.registerPlugin("block", "nocache",
    function(e, r) {
        return r
    }),
    jSmart.prototype.registerPlugin("block", "textformat",
    function(e, r, t) {
        if (!r) return "";
        var a = e.__get("wrap", 80),
        n = e.__get("wrap_char", "\n"),
        s = e.__get("wrap_cut", !1),
        i = e.__get("indent_char", " "),
        o = e.__get("indent", 0),
        p = new Array(o + 1).join(i),
        l = e.__get("indent_first", 0),
        c = new Array(l + 1).join(i),
        u = e.__get("style", "");
        "email" == u && (a = 72);
        for (var f = r.split(/[\r\n]{2}/), g = 0; g < f.length; ++g) {
            var m = f[g];
            m && (m = m.replace(/^\s+|\s+$/, "").replace(/\s+/g, " "), l > 0 && (m = c + m), m = modifiers.wordwrap(m, a - o, n, s), o > 0 && (m = m.replace(/^/gm, p)), f[g] = m)
        }
        var d = f.join(n + n);
        return "assign" in e ? (assignVar(e.assign, d, t), "") : d
    }),
    jSmart.prototype.registerPlugin("modifier", "capitalize",
    function(e, r) {
        var t = new RegExp(r ? "[\\W\\d]+": "\\W+"),
        a = null,
        n = "";
        for (a = e.match(t); a; a = e.match(t)) {
            var s = e.slice(0, a.index);
            n += s.match(/\d/) ? s: s.charAt(0).toUpperCase() + s.slice(1),
            n += e.slice(a.index, a.index + a[0].length),
            e = e.slice(a.index + a[0].length)
        }
        return e.match(/\d/) ? n + e: n + e.charAt(0).toUpperCase() + e.slice(1)
    }),
    jSmart.prototype.registerPlugin("modifier", "cat",
    function(e, r) {
        return r = r ? r: "",
        e + r
    }),
    jSmart.prototype.registerPlugin("modifier", "count",
    function(e, r) {
        if (null === e || "undefined" == typeof e) return 0;
        if (e.constructor !== Array && e.constructor !== Object) return 1;
        r = Boolean(r);
        var t, a = 0;
        for (t in e) e.hasOwnProperty(t) && (a++, r && e[t] && (e[t].constructor === Array || e[t].constructor === Object) && (a += modifiers.count(e[t], !0)));
        return a
    }),
    jSmart.prototype.registerPlugin("modifier", "count_characters",
    function(e, r) {
        return r ? e.length: e.replace(/\s/g, "").length
    }),
    jSmart.prototype.registerPlugin("modifier", "count_paragraphs",
    function(e) {
        var r = e.match(/\n+/g);
        return r ? r.length + 1 : 1
    }),
    jSmart.prototype.registerPlugin("modifier", "count_sentences",
    function(e) {
        var r = e.match(/[^\s]\.(?!\w)/g);
        return r ? r.length: 0
    }),
    jSmart.prototype.registerPlugin("modifier", "count_words",
    function(e) {
        var r = e.match(/\w+/g);
        return r ? r.length: 0
    }),
    jSmart.prototype.registerPlugin("modifier", "date_format",
    function(e, r, t) {
        return jSmart.prototype.PHPJS("strftime", "date_format").strftime(r ? r: "%b %e, %Y", jSmart.prototype.makeTimeStamp(e ? e: t))
    }),
    jSmart.prototype.registerPlugin("modifier", "defaultValue",
    function(e, r) {
        return void 0 == e || "" === e || "array" == typeof e && 0 == e.length ? void 0 == r || "" === r || "array" == typeof r && 0 == e.length ? "": r.toString() : e.toString()
    }),
    jSmart.prototype.registerPlugin("modifier", "unescape",
    function(e, r, t) {
        switch (e = new String(e), r = r || "html", t = t || "UTF-8", r) {
        case "html":
            return e.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&#039;/g, "'").replace(/&quot;/g, '"');
        case "entity":
        case "htmlall":
            return jSmart.prototype.PHPJS("html_entity_decode", "unescape").html_entity_decode(e, t);
        case "url":
            return jSmart.prototype.PHPJS("rawurldecode", "unescape").rawurldecode(e)
        }
        return e
    }),
    jSmart.prototype.registerPlugin("modifier", "escape",
    function(e, r, t, a) {
        switch (e = new String(e), r = r || "html", t = t || "UTF-8", a = "undefined" != typeof a ? Boolean(a) : !0, r) {
        case "html":
            return a && (e = e.replace(/&/g, "&amp;")),
            e.replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/'/g, "&#039;").replace(/"/g, "&quot;");
        case "htmlall":
            return jSmart.prototype.PHPJS("htmlentities", "escape").htmlentities(e, 3, t);
        case "url":
            return jSmart.prototype.PHPJS("rawurlencode", "escape").rawurlencode(e);
        case "urlpathinfo":
            return jSmart.prototype.PHPJS("rawurlencode", "escape").rawurlencode(e).replace(/%2F/g, "/");
        case "quotes":
            return e.replace(/(^|[^\\])'/g, "$1\\'");
        case "hex":
            for (var n = "",
            s = 0; s < e.length; ++s) n += "%" + jSmart.prototype.PHPJS("bin2hex", "escape").bin2hex(e.substr(s, 1));
            return n;
        case "hexentity":
            for (var n = "",
            s = 0; s < e.length; ++s) n += "&#x" + jSmart.prototype.PHPJS("bin2hex", "escape").bin2hex(e.substr(s, 1)).toUpperCase() + ";";
            return n;
        case "decentity":
            for (var n = "",
            s = 0; s < e.length; ++s) n += "&#" + jSmart.prototype.PHPJS("ord", "escape").ord(e.substr(s, 1)) + ";";
            return n;
        case "mail":
            return e.replace(/@/g, " [AT] ").replace(/[.]/g, " [DOT] ");
        case "nonstd":
            for (var n = "",
            s = 0; s < e.length; ++s) {
                var i = jSmart.prototype.PHPJS("ord", "escape").ord(e.substr(s, 1));
                n += i >= 126 ? "&#" + i + ";": e.substr(s, 1)
            }
            return n;
        case "javascript":
            return e.replace(/\\/g, "\\\\").replace(/'/g, "\\'").replace(/"/g, '\\"').replace(/\r/g, "\\r").replace(/\n/g, "\\n").replace(/<\//g, "</");
        case "none":
            return e
        }
        return e
    }),
    jSmart.prototype.registerPlugin("modifier", "indent",
    function(e, r, t) {
        r = r ? r: 4,
        t = t ? t: " ";
        for (var a = ""; r--;) a += t;
        var n = e.match(/\n+$/);
        return a + e.replace(/\n+$/, "").replace(/\n/g, "\n" + a) + (n ? n[0] : "")
    }),
    jSmart.prototype.registerPlugin("modifier", "lower",
    function(e) {
        return e.toLowerCase()
    }),
    jSmart.prototype.registerPlugin("modifier", "nl2br",
    function(e) {
        return e.replace(/\n/g, "<br />\n")
    }),
    jSmart.prototype.registerPlugin("modifier", "regex_replace",
    function(e, r, t) {
        var a = r.match(/^ *\/(.*)\/(.*) *$/);
        return new String(e).replace(new RegExp(a[1], "g" + (a.length > 1 ? a[2] : "")), t)
    }),
    jSmart.prototype.registerPlugin("modifier", "replace",
    function(e, r, t) {
        if (!r) return e;
        e = new String(e),
        r = new String(r),
        t = new String(t);
        var a = "",
        n = -1;
        for (n = e.indexOf(r); n >= 0; n = e.indexOf(r)) a += e.slice(0, n) + t,
        n += r.length,
        e = e.slice(n);
        return a + e
    }),
    jSmart.prototype.registerPlugin("modifier", "spacify",
    function(e, r) {
        return r || (r = " "),
        e.replace(/(\n|.)(?!$)/g, "$1" + r)
    }),
    jSmart.prototype.registerPlugin("modifier", "noprint",
    function() {
        return ""
    }),
    jSmart.prototype.registerPlugin("modifier", "string_format",
    function(e, r) {
        return jSmart.prototype.PHPJS("sprintf", "string_format").sprintf(r, e)
    }),
    jSmart.prototype.registerPlugin("modifier", "strip",
    function(e, r) {
        return r = r ? r: " ",
        new String(e).replace(/[\s]+/g, r)
    }),
    jSmart.prototype.registerPlugin("modifier", "strip_tags",
    function(e, r) {
        return r = null == r ? !0 : r,
        new String(e).replace(/<[^>]*?>/g, r ? " ": "")
    }),
    jSmart.prototype.registerPlugin("modifier", "truncate",
    function(e, r, t, a, n) {
        return r = r ? r: 80,
        t = null != t ? t: "...",
        e.length <= r ? e: (r -= Math.min(r, t.length), n ? e.slice(0, Math.floor(r / 2)) + t + e.slice(e.length - Math.floor(r / 2)) : (a || (e = e.slice(0, r + 1).replace(/\s+?(\S+)?$/, "")), e.slice(0, r) + t))
    }),
    jSmart.prototype.registerPlugin("modifier", "upper",
    function(e) {
        return e.toUpperCase()
    }),
    jSmart.prototype.registerPlugin("modifier", "wordwrap",
    function(e, r, t, a) {
        r = r || 80,
        t = t || "\n";
        for (var n = e.split("\n"), s = 0; s < n.length; ++s) {
            for (var i = n[s], o = ""; i.length > r;) {
                for (var p = 0,
                l = i.slice(p).match(/\s+/); l && p + l.index <= r; l = i.slice(p).match(/\s+/)) p += l.index + l[0].length;
                p = p || (a ? r: l ? l.index + l[0].length: i.length),
                o += i.slice(0, p).replace(/\s+$/, ""),
                p < i.length && (o += t),
                i = i.slice(p)
            }
            n[s] = o + i
        }
        return n.join("\n")
    }),
    String.prototype.fetch = function(e) {
        var r = new jSmart(this);
        return r.fetch(e)
    },
    "undefined" != typeof module && module.exports ? module.exports = jSmart: window.jSmart = jSmart
});;
define("wiki-common:widget/util/browser.js",
function(t, e, i) {
    var n = navigator.userAgent;
    i.exports = {
        chrome: function() {
            return /chrome\/(\d+\.\d+)/i.test(n) ? +RegExp.$1: void 0
        },
        firefox: function() {
            return /firefox\/(\d+\.\d+)/i.test(n) ? +RegExp.$1: void 0
        },
        ie: function() {
            return /(?:msie |rv:)(\d+\.\d+)/i.test(n) ? document.documentMode || +RegExp.$1: void 0
        },
        safari: function() {
            return /(\d+\.\d)?(?:\.\d)?\s+safari\/?(\d+\.\d+)?/i.test(n) && !/chrome/i.test(n) ? +(RegExp.$1 || RegExp.$2) : void 0
        },
        isStandard: function() {
            return "CSS1Compat" == document.compatMode
        },
        isGecko: function() {
            return /gecko/i.test(n) && !/like gecko/i.test(n)
        },
        isWebkit: function() {
            return /webkit/i.test(n)
        },
        ipad: function() {
            return /ipad/i.test(n)
        }
    }
});;
define("wiki-common:widget/lib/jquery/jquery.js",
function(e, i, j) {
    var o = e("wiki-common:widget/lib/jquery/jquery_1.11.1.js");
    e("wiki-common:widget/lib/jquery/jquery.mousewheel.js")(o),
    j.exports = o
});;
define("wiki-common:widget/util/safeCall.js",
function(i, t, e) {
    var n = i("wiki-common:widget/lib/jquery/jquery.js");
    e.exports = function(i, t, e) {
        "use strict";
        return i && "function" == n.type(i) ? i.apply("object" == n.type(e) ? e: null, void 0 !== t ? [].concat(t) : []) : void 0
    }
});;
define("wiki-common:widget/component/psLink/psLink.js",
function(t) {
    var i = t("wiki-common:widget/lib/jquery/jquery.js"),
    e = t("wiki-common:widget/util/browser.js"),
    o = !1,
    w = new RegExp("^https?:\\/\\/www.baidu.com\\/s\\?", "i");
    window.BaiduHttps = window.BaiduHttps || {},
    window.BaiduHttps.callbacks = function() {},
    (e.chrome() || e.firefox() || e.safari() || e.ie() > 7) && i("body").on("mouseenter", "a",
    function() {
        var t = i(this).attr("href"),
        e = i(this).attr("data-href") || ""; (w.test(t) || w.test(e)) && (t && i(this).attr("href", t.replace(w, "https://www.baidu.com/s?")), e && i(this).attr("data-href", e.replace(w, "https://www.baidu.com/s?")), o || (o = !0, i.getScript("https://www.baidu.com/con?from=baike")))
    })
});;
define("wiki-common:widget/component/searchbar/searchbar.js",
function(e, t, i) {
    function s(e) {
        function t(e) {
            window.baidu = window.baidu || {},
            window.baidu.sug = function(t) {
                var i = t.q || "",
                n = ("bollean" == typeof t.p ? req.p: !1, t.s || []);
                n.unshift(i),
                s.list = n,
                s.renderSuggestion(e)
            }
        }
        var i = "",
        s = {
            selected: 0,
            list: [],
            searchInOtherProducts: function(e, t, i) {
                var s = n.trim(e.find("#query").val());
                location.href = s ? t + encodeURIComponent(s) : i
            },
            requestSuggestion: function(e) {
                e && "" != n.trim(e) ? n.ajax({
                    url: "http://nssug.baidu.com/su?wd=" + encodeURIComponent(e) + "&prod=baike",
                    dataType: "script",
                    scriptCharset: "GBK"
                }) : window.baidu.sug({
                    q: "",
                    p: !1,
                    s: []
                })
            },
            renderSuggestion: function(e) {
                if (s.list.length <= 1) e.find("#suggestion").hide();
                else {
                    for (var t = "",
                    i = 1; i < s.list.length; i++) t += s.list[i].match(s.list[0]) ? '<li class="matched">' + s.list[i].replace(s.list[0], '<span class="highlight">' + s.list[0] + "</span>") + "</li>": "<li>" + s.list[i] + "</li>";
                    e.find("#suggestion > div").html(t),
                    e.find("#suggestion").show()
                }
                s.selected = 0
            },
            hoverSuggestion: function(e) {
                s.list.length > 0 ? (e.find("#suggestion div li").removeClass("selected"), s.selected - 1 >= 0 && e.find("#suggestion div li").eq(s.selected - 1).addClass("selected")) : s.requestSuggestion(n.trim(e.find("#query").val()))
            },
            selectSuggestion: function(e) {
                s.list.length > 0 && e.find("#query").val(s.list[s.selected])
            },
            closeSuggestion: function(e) {
                s.selected = 0,
                s.list = [],
                e.find("#suggestion > div").html(""),
                e.find("#suggestion").hide()
            },
            input: function(e) {
                clearTimeout(i);
                var t = n.trim(e.find("#query").val());
                i = setTimeout(function() {
                    s.requestSuggestion(t)
                },
                200)
            },
            search: function(e) {
                var t = n.trim(e.find("#query").val());
                "" == t && e.find("#query").val("百度百科"),
                e.find("#searchForm").submit(),
                s.closeSuggestion(e)
            },
            searchLemma: function(e) {
                var t = n.trim(e.find("#query").val());
                "" == t && e.find("#query").val("百度百科"),
                e.find("#searchLemmaQuery").val(e.find("#query").val()),
                e.find("#searchLemmaForm").submit()
            }
        };
        e.on("searchInOtherProducts",
        function(e, t) {
            var i = t.attr("data-href"),
            o = t.attr("href");
            i && o && s.searchInOtherProducts(n(this), i, o)
        }),
        e.on("suggestion:shiftUp",
        function() {
            s.selected = (s.selected - 1) % s.list.length >= 0 ? (s.selected - 1) % s.list.length: (s.selected - 1) % s.list.length + s.list.length,
            s.hoverSuggestion(n(this)),
            s.selectSuggestion(n(this))
        }),
        e.on("suggestion:shiftDown",
        function() {
            s.selected = (s.selected + 1) % s.list.length < s.list.length ? (s.selected + 1) % s.list.length: (s.selected + 1) % s.list.length - s.list.length,
            s.hoverSuggestion(n(this)),
            s.selectSuggestion(n(this))
        }),
        e.on("suggestion:hover",
        function(e, t) {
            s.selected = t.index() + 1,
            s.hoverSuggestion(n(this))
        }),
        e.on("suggestion:leave",
        function() {
            s.selected = 0,
            s.hoverSuggestion(n(this))
        }),
        e.on("suggestion:close",
        function() {
            s.closeSuggestion(n(this))
        }),
        e.on("suggestion:select",
        function() {
            s.selectSuggestion(n(this))
        }),
        e.on("searchbar:focus",
        function() {
            t(n(this))
        }),
        e.on("searchbar:input",
        function() {
            t(n(this)),
            s.input(n(this))
        }),
        e.on("searchbar:search",
        function() {
            s.search(n(this))
        }),
        e.on("searchbar:searchLemma",
        function() {
            s.searchLemma(n(this))
        }),
        e.find(".search .nav a").on("click",
        function() {
            return n(this).trigger("searchInOtherProducts", [n(this)]),
            !1
        }),
        e.find("#query").on("keydown",
        function(e) {
            switch (e.keyCode) {
            case 38:
                n(this).trigger("suggestion:shiftUp"),
                e.preventDefault();
                break;
            case 40:
                n(this).trigger("suggestion:shiftDown"),
                e.preventDefault();
                break;
            case 9:
            case 27:
                n(this).trigger("suggestion:close");
                break;
            case 13:
                n(this).trigger("searchbar:search"),
                e.preventDefault()
            }
        }),
        e.find("#query").on("focus",
        function() {
            n(this).trigger("searchbar:focus")
        }),
        e.find("#query").on("keyup",
        function(e) {
            var t = e.keyCode; (t >= 48 && 57 >= t || t >= 65 && 90 >= t || t >= 186 && 192 >= t || 8 == t || 32 == t || 220 == t) && n(this).trigger("searchbar:input")
        }),
        e.find("#suggestion div").on("mouseover", "li",
        function() {
            n(this).trigger("suggestion:hover", [n(this)])
        }),
        e.find("#suggestion div").on("mouseleave",
        function() {
            n(this).trigger("suggestion:leave")
        }),
        e.find("#suggestion div").on("click", "li",
        function() {
            n(this).trigger("suggestion:select"),
            n(this).trigger("searchbar:search")
        }),
        e.find("#suggestion #close").on("click",
        function() {
            n(this).trigger("suggestion:close")
        }),
        e.find("#search").on("click",
        function() {
            n(this).trigger("searchbar:search")
        }),
        e.find("#searchLemma").on("click",
        function() {
            n(this).trigger("searchbar:searchLemma")
        }),
        n("*").on("click",
        function(t) {
            n(this).parents(".wgt-searchbar") === e ? t.stopPropagation() : e.trigger("suggestion:close")
        }),
        function() {
            var i = e.find("#query").val();
            o.ipad() || e.each(function() {
                n(this).parents(".header-wrapper").length > 0 && (n(this).find("#query").val("").focus().val(i), t(n(this)))
            })
        } ()
    }
    var n = e("wiki-common:widget/lib/jquery/jquery.js"),
    o = e("wiki-common:widget/util/browser.js");
    i.exports = s
});;
define("wiki-common:widget/component/userLogin/userLogin.js",
function(o, n, i) {
    var t = o("wiki-common:widget/lib/jquery/jquery.js"),
    e = {
        onLogin: function() {},
        onUnlogin: function() {},
        onLoginSuccess: function() {
            window.location.href = window.location.href.replace(/#.*$/g, "")
        },
        onLoginFail: function() {}
    },
    s = [],
    a = {
        api: {
            login: function(o, n) {
                t.ajax(t.extend({
                    type: "GET",
                    url: "/api/usercenter/login",
                    dataType: "JSON",
                    cache: !1,
                    success: function(o) {
                        n(o)
                    }
                },
                o.ifMsg ? {
                    data: {
                        msg: 1
                    }
                }: {}))
            },
            loginPop: function(o, n) {
                t.getScript("http://passport.baidu.com/passApi/js/uni_login_wrapper.js?cdnversion=" + (new Date).getTime(),
                function() {
                    n()
                })
            }
        }
    },
    c = {
        checkIfLogin: function(o) {
            var o = o || {},
            n = o.onLogin || e.onLogin,
            i = o.onUnlogin || e.onUnlogin;
            a.api.login(o,
            function(o) {
                if (o.isLogin) {
                    o.name = o.displayName,
                    n(o);
                    for (var t = 0; t < s.length; t++) !! s[t].handleLogon && s[t].handleLogon(o),
                    "function" == typeof s[t].onLogin && s[t].onLogin(o)
                } else i()
            })
        },
        showLoginPop: function(o) {
            var o = o || {},
            n = o.onLoginSuccess || e.onLoginSuccess,
            i = o.onLoginFail || e.onLoginFail,
            t = function() {
                window.passport._loginPop = window.passport.pop.init({
                    tangram: !0,
                    apiOpt: {
                        product: "wk",
                        staticPage: (window.location.protocol + "//" + window.location.host || "http://baike.baidu.com") + "/static/wiki-common/html/v3Jump.html",
                        memberPass: !0,
                        safeFlag: 0,
                        u: window.location.href.replace(/#.*$/g, ""),
                        qrcode: 3
                    },
                    authsiteCfg: {
                        act: "implicit"
                    },
                    cache: !0,
                    authsite: ["renren", "tsina", "tqq", "qzone", "fetion"],
                    registerLink: "https://passport.baidu.com/v2/?reg&regType=1&tpl=wk",
                    onLoginSuccess: function(o) {
                        n(),
                        window.passport._loginPop.hide(),
                        o.returnValue = !1
                    },
                    onSubmitedErr: function() {
                        i()
                    }
                }),
                window.passport._loginPop.show()
            },
            s = function() {
                window.passport.hook.login.loginSuccess = function(o, i) {
                    n(),
                    window.passport._loginPop.hide(),
                    i.returnValue = !1
                },
                window.passport.hook.login.loginErr = function() {
                    i()
                },
                window.passport._loginPop.show()
            };
            window.passport ? s() : a.api.loginPop({},
            t)
        },
        logout: function() {
            window.location.href = "https://passport.baidu.com/?logout&u=" + encodeURIComponent(window.location.href)
        },
        regist: function(o) {
            s.push(o)
        }
    };
    i.exports = c
});;
define("wiki-common:widget/component/userMsg/userMsg.js",
function(n, e, s) {
    var t = n("wiki-common:widget/lib/jquery/jquery.js"),
    o = n("wiki-common:widget/component/userLogin/userLogin.js"),
    r = {
        api: {
            msg: function(n, e) {
                t.when(t.getScript("http://msg.baidu.com/ms?ct=18&cm=3&tn=bmSelfUsrStat&mpn=13227114&un=" + n.userInfo.username)).done(function() {
                    var s = redmsg ? {
                        redmsg: redmsg,
                        msgnum: msgnum,
                        sysnum: sysnum
                    }: {
                        redmsg: 0,
                        msgnum: 0,
                        sysnum: 0
                    };
                    e(s, n.userInfo.msgStatus)
                })
            }
        }
    },
    u = {
        getUserMsg: function() {
            var n = {};
            if ("object" == typeof arguments[0]) {
                if (n = arguments[0], "function" != typeof arguments[1]) throw new Error('[userMsg Exception]: Invalid argument "callback".');
                var e = arguments[1]
            } else {
                if ("function" != typeof arguments[0]) throw new Error('[userMsg Exception]: Invalid argument "callback".');
                var e = arguments[0]
            }
            var s = function(s) {
                r.api.msg(t.extend({
                    userInfo: s
                },
                {
                    getBaikeMsg: "undefined" != typeof n.getBaikeMsg ? n.getBaikeMsg: !0
                }),
                function(n, s) {
                    e(n, s)
                })
            };
            n.userInfo ? s(n.userInfo) : o.checkIfLogin({
                ifMsg: !0,
                onUnlogin: function() {
                    return ! 1
                },
                onLogin: function(n) {
                    s(n)
                }
            })
        }
    };
    s.exports = u
});;
define("wiki-common:widget/ui/bubble/bubble.js",
function(t, i, e) {
    function o(t) {
        if (!t) throw new Error("[Bubble Exception]: No arguments.");
        if (!n(t.host).length) throw new Error('[Bubble Exception]: Invalid argument "host".');
        if (this.host = n(t.host), this.extClassNames = t.classNames || "", this.showTail = "boolean" == n.type(t.showTail) ? t.showTail: !1, this.marginFromHost = isNaN(parseInt(t.marginFromHost)) ? this.showTail ? 10 : 0 : parseInt(t.marginFromHost), this.offset = n.extend({
            top: 0,
            left: 0
        },
        this.marginFromHost ? null: t.offset), this.hideWhenBlur = "boolean" == n.type(t.hideWhenBlur) ? t.hideWhenBlur: !0, this.animation = /^FADE|SLIDE$/i.test(t.animation) ? t.animation: "", this.hideAfterDelay = parseInt(t.hideAfterDelay) ? parseInt(t.hideAfterDelay) + (this.animation ? 200 : 0) : 0, this.content = t.content || "", this.onShow = "function" == n.type(t.onShow) ? t.onShow: null, this.onHide = "function" == n.type(t.onHide) ? t.onHide: null, this.isVisible = !1, this.blurProtect = [this.host[0]], t.blurProtect) {
            t.blurProtect = [].concat(t.blurProtect);
            var i = this;
            n(t.blurProtect).each(function(t, e) {
                var o = n(e);
                o.length && o[0] != i.host[0] && !n.contains(i.host[0], o[0]) && i.blurProtect.push(o[0])
            })
        }
        var e = t.pos && t.pos.match(/^auto|(?:(top|right|bottom|left|hor|ver)(?:[- |]?(top|right|bottom|left|center|auto))?)$/i);
        this.pos = e ? (e[1] || e[0]).toLowerCase() : "auto",
        this.align = "auto" == this.pos ? "auto": e && e[2] && e[2] != e[1] ? e[2].toLowerCase() : /ver|hor/.test(this.pos) ? "auto": "center",
        "hor" == this.pos && (this.align = /^top|center|bottom|auto$/.test(this.align) ? RegExp.$_: "auto"),
        "ver" == this.pos && (this.align = /^left|center|right|auto$/.test(this.align) ? RegExp.$_: "auto")
    }
    function s(t) {
        var i = n.extend("http://www.w3.org/2000/svg" == this.host[0].namespaceURI ? this.host[0].getBBox() : {
            width: this.host.outerWidth(),
            height: this.host.outerHeight()
        },
        this.host.offset()),
        e = n(document),
        o = n(window);
        n.extend(i, {
            viewTop: i.top - e.scrollTop(),
            viewRight: o.width() - (i.left - e.scrollLeft() + i.width),
            viewBottom: o.height() - (i.top - e.scrollTop() + i.height),
            viewLeft: i.left - e.scrollLeft()
        });
        var s = {
            width: this.node.outerWidth(),
            height: this.node.outerHeight()
        },
        h = [o.width() * i.viewTop, o.height() * i.viewRight, o.width() * i.viewBottom, o.height() * i.viewLeft],
        l = [h[1], h[3]],
        a = [h[0], h[2]];~this.pos.indexOf("auto") ? this.pos = "auto-" + ["top", "right", "bottom", "left"][n(h).index(Math.max.apply(null, h))] : ~this.pos.indexOf("hor") ? this.pos = "hor-" + ["right", "left"][n(l).index(Math.max.apply(null, l))] : ~this.pos.indexOf("ver") && (this.pos = "ver-" + ["top", "bottom"][n(a).index(Math.max.apply(null, a))]);
        var r = {},
        d = "SLIDE" == this.animation,
        u = this.pos.replace(/(?:auto|ver|hor)-/, ""),
        p = "pos_" + (u || "bottom");
        switch (u) {
        case "top":
            this.aniSlide = {
                direction: "top",
                len: d ? -20 : 0
            },
            r.top = i.top - s.height - this.marginFromHost + (t ? 0 : this.aniSlide.len);
            break;
        case "right":
            this.aniSlide = {
                direction: "left",
                len: d ? 20 : 0
            },
            r.left = i.left + i.width + this.marginFromHost + (t ? 0 : this.aniSlide.len);
            break;
        case "left":
            this.aniSlide = {
                direction: "left",
                len: d ? -20 : 0
            },
            r.left = i.left - s.width - this.marginFromHost + (t ? 0 : this.aniSlide.len);
            break;
        default:
            this.aniSlide = {
                direction: "top",
                len: d ? 20 : 0
            },
            r.top = i.top + i.height + this.marginFromHost + (t ? 0 : this.aniSlide.len)
        }~this.align.indexOf("auto") && (this.align = "auto-", this.align += /top|bottom/.test(this.pos) ? i.viewLeft * i.viewRight < 0 ? i.viewLeft < i.viewRight ? "right": "left": ((i.width - s.width) / 2 + i.viewLeft) * ((i.width - s.width) / 2 + i.viewRight) > 0 ? "center": i.viewLeft > i.viewRight ? "right": "left": i.viewTop * i.viewBottom < 0 ? i.viewTop < i.viewBottom ? "bottom": "top": ((i.height - s.height) / 2 + i.viewTop) * ((i.height - s.height) / 2 + i.viewBottom) > 0 ? "center": i.viewTop > i.viewBottom ? "bottom": "top");
        var c = this.align.replace(/auto-/, "");
        switch (p += " align_" + (c || "center"), c) {
        case "top":
            r.top = i.top;
            break;
        case "right":
            r.left = i.left + i.width - s.width;
            break;
        case "bottom":
            r.top = i.top + i.height - s.height;
            break;
        case "left":
            r.left = i.left;
            break;
        default:
            isNaN(parseInt(r.left)) ? r.left = i.left + (i.width - s.width) / 2 : r.top = i.top + (i.height - s.height) / 2
        }
        this.node.css({
            top: r.top + this.offset.top,
            left: r.left + this.offset.left
        }).removeClass("pos_top pos_right pos_bottom pos_left align_top align_right align_bottom align_left align_center").addClass(p)
    }
    function h(t) {
        o.call(this, t),
        this.node = n('<div class="wgt_bubble"></div>').append(this.content).append(this.showTail ? '<em class="tail"></em>': "").addClass(this.extClassNames).appendTo(n("body")).hide(),
        this.blurProtect.push(this.node[0]);
        var i = this;
        this.hideWhenBlur && n("body").on("click.bubble",
        function(t) {
            var e = !1;
            n.each(i.blurProtect,
            function(i, o) {
                try {
                    if (t.target == o || n.contains(o, t.target)) return e = !0,
                    !1
                } catch(s) {}
            }),
            !e && i.hide()
        }),
        n(window).on("resize.bubble",
        function() {
            i.resetPos()
        }).on("scroll.bubble",
        function() {
            i.resetPos()
        })
    }
    var n = t("wiki-common:widget/lib/jquery/jquery.js"),
    l = t("wiki-common:widget/util/safeCall.js");
    h.prototype = {
        constructor: h,
        setHost: function(t) {
            return n(t).length ? (this.blurProtect.splice(n.inArray(this.host[0], this.blurProtect), 1), this.host = n(t), this.blurProtect.push(this.host[0]), this) : void 0
        },
        setContent: function(t) {
            return this.content = t,
            this.node.empty().append(this.content),
            this.showTail && this.node.append('<em class="tail"></em>'),
            this
        },
        resetPos: function() {
            return this.isVisible && s.call(this, !0),
            this
        },
        getNode: function() {
            return this.node
        },
        show: function(t) {
            if (!this.isVisible) {
                var i = this;
                if (this.isVisible = !0, this.node.show(), s.call(i), i.animation) {
                    var e = {
                        opacity: 1
                    };
                    e[i.aniSlide.direction] = "-=" + i.aniSlide.len,
                    i.node.css("opacity", 0).animate(e, {
                        duration: 200,
                        queue: !1,
                        complete: function() {
                            i.node.show(),
                            l(t, null, i),
                            l(i.onShow, null, i)
                        }
                    })
                } else l(t, null, i),
                l(i.onShow, null, i);
                return clearTimeout(i.hideDelayTimer),
                i.hideAfterDelay && (i.hideDelayTimer = setTimeout(function() {
                    i.hide()
                },
                i.hideAfterDelay)),
                this
            }
            s.call(this, !0)
        },
        hide: function(t) {
            if (this.isVisible) {
                var i = this;
                if (this.isVisible = !1, this.animation) {
                    var e = {
                        opacity: 0
                    };
                    e[this.aniSlide.direction] = "+=" + this.aniSlide.len,
                    this.node.animate(e, {
                        duration: 200,
                        queue: !1,
                        complete: function() {
                            i.node.hide(),
                            l(t, null, i),
                            l(i.onHide, null, i)
                        }
                    })
                } else this.node.hide(),
                l(t, null, i),
                l(this.onHide, null, i);
                return this
            }
        }
    },
    e.exports = h
});;
define("wiki-common:widget/component/userbar/userbar.js",
function(e, a, s) {
    var n = e("wiki-common:widget/lib/jquery/jquery.js"),
    i = e("wiki-common:widget/component/userLogin/userLogin.js"),
    t = e("wiki-common:widget/component/userMsg/userMsg.js"),
    r = e("wiki-common:widget/lib/jsmart/jsmart.js"),
    l = e("wiki-common:widget/util/browser.js"),
    o = e("wiki-common:widget/ui/bubble/bubble.js"),
    m = {
        userbar: {
            unlogin: new r('<li>\n<a href="http://www.baidu.com/">百度首页</a>\n</li>\n<li>\n<a data-action=\'login\' href="javascript:;">登录</a>\n</li>\n<li>\n<a href="https://passport.baidu.com/v2/?reg&regType=1&tpl=wk">注册<a>\n</li>'),
            login: new r('<li class="userbar_user" data-action="showUserMenu">\n<a href="javascript:;"><span>{%$displayName|escape:html|escape:none%}</span><em class="cmn-inline-block cmn-icon cmn-icons cmn-icons_userbar-up"></em></a>\n</li>\n{%if $UIConfig.btns|hasElement:"settings"%}\n<li class="userbar_setting">\n<a href="/usercenter/settings" target="_blank"><em class="cmn-icon cmn-icons cmn-icons_cog"></em>设置</a>\n</li>\n{%/if%}\n<li class="userbar_mall">\n<a href="/mall/" target="_blank"><em class="cmn-inline-block cmn-icon cmn-icons cmn-icons_userbar-shop"></em>商城</a>\n</li>\n<li class="userbar_message" data-action="showUserMsg">\n<a href="javascript:;"><em class="cmn-inline-block cmn-icon cmn-icons cmn-icons_userbar-message"></em><em class="cmn-icon cmn-icons cmn-icons_userbar-new userbar_message_new"></em>消息</a>\n</li>\n<li class="split">|</li>\n<li>\n<a href="http://www.baidu.com/">百度首页</a>\n</li>')
        },
        menu: {
            userMenu: new r('<ul>\n<li class="top"><a href="/usercenter" target="_blank">我的百科</a></li>\n<li><a href="/usercenter/tasks#mine" target="_blank">我的任务</a></li>\n<li><a href="/usercenter/lemmas" target="_blank">我的词条</a></li>\n{%if $isAuthLemma%}\n<li><a href="/member/mylemmas" target="_blank">我的订单</a></li>\n{%/if%}\n<li><a href="http://passport.baidu.com/center" target="_blank">账号设置</a></li>\n<li class="bottom logout"><a data-action="logout" href="javascript:;">退出</a></li>\n</ul>'),
            userMsg: new r('<ul>\n<li class="top"><a href="/messages" target="_blank"><span class="userMsgType">通知</span>{%if $baikeMsg.notice && $baikeMsg.notice.num > 0%}<span class="userMsgCount">{%if $baikeMsg.notice.num < 100%}{%$baikeMsg.notice.num|f_escape_xml%}{%else%}99{%/if%}</span>{%/if%}</a></li><li class="bottom"><a href="http://msg.baidu.com/" target="_blank"><span class="userMsgType">私信</span>{%if $baiduMsg.msgnum > 0%}<span class="userMsgCount">{%if $baiduMsg.msgnum < 100%}{%$baiduMsg.msgnum|f_escape_xml%}{%else%}99{%/if%}</span>{%/if%}</a></li>\n</ul>')
        },
        bubble: {
            lemmaMsg: new r('<ul>\n{%foreach $baikeMsg as $idx => $msg%}\n{%if $idx === "passedVersion" && $msg.num > 0%}\n<li><a href="/usercenter/lemmas#passed" data-action="readMessage" data-msgType="3" target="_blank">您有{%$msg.num|f_escape_xml%}个版本已通过</a></li>\n{%else if $idx === "failedVersion" && $msg.num > 0%}\n<li><a href="/usercenter/lemmas#rejected" data-action="readMessage" data-msgType="4" target="_blank">您有{%$msg.num|f_escape_xml%}个版本未通过</a></li>\n{%else if $idx === "deletedVersion" && $msg.num > 0%}\n<li><a href="/usercenter/lemmas#rejected" data-action="readMessage" data-msgType="6" target="_blank">您有{%$msg.num|f_escape_xml%}个版本被删除</a></li>\n{%else if $idx === "goodVersion" && $msg.num > 0%}\n<li><a href="/usercenter/lemmas#passed-good" data-action="readMessage" data-msgType="7" target="_blank">您有{%$msg.num|f_escape_xml%}个版本被评为优质版本</a></li>\n{%else if $idx === "featuredLemma" && $msg.num > 0%}\n<li><a href="/usercenter/lemmas#passed-featured" data-action="readMessage" data-msgType="8" target="_blank">您有{%$msg.num|f_escape_xml%}个词条被评为特色词条</a></li>\n{%else if $idx === "deletedLemma" && $msg.num > 0%}\n<li><a href="/usercenter/lemmas#rejected" data-action="readMessage" data-msgType="5" target="_blank">您有{%$msg.num|f_escape_xml%}个词条被删除</a></li>\n{%else if $idx === "featuredModified" && $msg.num > 0%}\n<li><a href="/usercenter/lemmas#passed-featured" data-action="readMessage" data-msgType="9" target="_blank">您有{%$msg.num|f_escape_xml%}个特色词条被修改</a></li>\n{%else if $idx === "tousu" && $msg.num > 0%}\n<li><a href="/messages" data-action="readMessage" data-msgType="2" target="_blank">您有{%$msg.num|f_escape_xml%}个投诉处理消息</a></li>\n{%/if%}\n{%/foreach%}\n<li class="cancelAlarm"><a href="/usercenter/settings#message" target="_blank">不再接收此类消息</a></li>\n</ul>\n<a class="wgt-bubble-lemmaMsg_close" data-action="cancelAlarm" href="javascript:;"><em class="cmn-icon cmn-icons cmn-icons_close"></em></a>')
        }
    };
    r.prototype.registerPlugin("modifier", "hasElement",
    function(e, a) {
        for (var s in e) if (e[s] == a) return ! 0;
        return ! 1
    });
    var c = {
        render: {
            userbar: function(e, a, s) {
                switch (a) {
                case "unlogin":
                    e.html(m.userbar[a].fetch(s));
                    break;
                case "login":
                    e.html(m.userbar[a].fetch(s))
                }
            },
            menu: function(e, a, s, t) {
                var r = new o({
                    host: a,
                    content: m.menu[s].fetch(t),
                    pos: "bottom-center",
                    offset: {
                        top: 7
                    },
                    showTail: "undefined" == typeof l.ie() || l.ie() > 9 ? !0 : !1,
                    classNames: "wgt-bubble-" + s
                }),
                c = n(r.getNode());
                return a.mouseover(function() {
                    var s = this;
                    "showUserMenu" == n(s).attr("data-action") && e.find(".userbar_user").addClass("spreadUserMenu"),
                    r.show(function() {
                        var i = "";
                        a.on("mouseleave",
                        function() {
                            i = setTimeout(function() {
                                "showUserMenu" == n(s).attr("data-action") && e.find(".userbar_user").removeClass("spreadUserMenu"),
                                r.hide()
                            },
                            100)
                        }),
                        c.on("mouseenter",
                        function() {
                            clearTimeout(i),
                            c.on("mouseleave",
                            function() {
                                "showUserMenu" == n(s).attr("data-action") && e.find(".userbar_user").removeClass("spreadUserMenu"),
                                r.hide()
                            })
                        })
                    })
                }),
                c.on("click", "a",
                function() {
                    var e = n(this).attr("data-action");
                    if (e) {
                        switch (e) {
                        case "logout":
                            i.logout()
                        }
                        return ! 1
                    }
                }),
                c
            },
            bubble: function(e, a, s) {
                var i = new o({
                    host: e,
                    content: m.bubble[a].fetch(s),
                    pos: "bottom-center",
                    showTail: "undefined" == typeof l.ie() || l.ie() > 9 ? !0 : !1,
                    hideWhenBlur: !1,
                    classNames: "wgt-bubble-" + a,
                    offset: {
                        top: 7,
                        left: -80
                    }
                }),
                t = n(i.getNode());
                return i.show(),
                t
            }
        }
    },
    u = {
        buildUserbar: function(e, a) {
            var a = "object" == typeof a && a instanceof Array ? a: [];
            i.checkIfLogin({
                ifMsg: !0,
                onUnlogin: function() {
                    c.render.userbar(e, "unlogin")
                },
                onLogin: function(s) {
                    c.render.userbar(e, "login", n.extend(s, {
                        UIConfig: {
                            btns: a
                        }
                    })),
                    c.render.menu(e, e.find('li[data-action="showUserMenu"]'), "userMenu", {
                        isAuthLemma: s.isAuthLemma
                    }),
                    t.getUserMsg({
                        userInfo: s,
                        getBaikeMsg: !1
                    },
                    function(a) {
                        var i = {
                            baiduMsg: a,
                            baikeMsg: s.msgStatus
                        };
                        i.baiduMsg.msgnum + i.baikeMsg.notice.num > 0 && e.find(".userbar_message").addClass("newMessage"),
                        c.render.menu(e, e.find('li[data-action="showUserMsg"]'), "userMsg", i);
                        var t = 0;
                        for (var r in i.baikeMsg)"notice" !== r && (t += i.baikeMsg[r].num);
                        if (t > 0) {
                            var l = c.render.bubble(e.find('li[data-action="showUserMenu"]'), "lemmaMsg", i);
                            l.on("click", "a",
                            function() {
                                var e = n(this).attr("data-action");
                                switch (e) {
                                case "readMessage":
                                    var a = n(this).attr("data-msgType");
                                    break;
                                case "cancelAlarm":
                                    var a = "all"
                                }
                                return n.ajax({
                                    type: "GET",
                                    url: "/api/wikimessage/readmsgstatus",
                                    data: {
                                        type: a
                                    },
                                    dataType: "JSON",
                                    complete: function() {
                                        l.remove()
                                    }
                                }),
                                "cancelAlarm" === e ? !1 : void 0
                            })
                        }
                    })
                }
            }),
            e.on("click", "a",
            function() {
                var e = n(this).attr("data-action");
                if (e) {
                    switch (e) {
                    case "login":
                        i.showLoginPop()
                    }
                    return ! 1
                }
            })
        }
    };
    s.exports = u
});