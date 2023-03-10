webpackJsonpjwplayer([5], {
    154 : function(a, b, c) {
        var d, e;
        d = [c(112), c(64), c(55), c(156), c(158), c(155), c(157), c(42), c(62), c(45)],
        e = function(a, b, c, d, e, f, g, h, i, j) {
            var k = function() {},
            l = {},
            m = function(m, n) {
                function o() {
                    return ! j.isUndefined(m._instreamAdapter)
                }
                function p(a, b) {
                    g.log("send command", a, b);
                    var c = {
                        command: a
                    };
                    void 0 !== b && (c.args = b),
                    A.sendMessage(g.NS, c, k,
                    function(a) {
                        g.log("error message", a),
                        "Invalid namespace" === a.description && G.stopCasting()
                    })
                }
                function q(a) {
                    var b = g.available(a.availability);
                    s(b)
                }
                function r(a, b) {
                    var c = JSON.parse(b);
                    if (!c) throw "Message not proper JSON";
                    if (c.reconcile) {
                        E.removeMessageListener(g.NS, this.listenForHandshakeHandler);
                        var d = c.diff,
                        e = E;
                        d.id && c.appid && c.pageUrl || (d.id = window.jwplayer().id, c.appid = z.appid, c.pageUrl = C, E = A = null),
                        d.id === m.id && c.appid === z.appid && c.pageUrl === C && (A || (o() && m.instreamDestroy(), this.sessionStarted(e), this.castModel.set("state", i.IDLE)), this.handleMessage(c)),
                        E = null
                    }
                }
                function s(a) {
                    var b = !!a;
                    B.available !== b && (B.available = b, n.set("castAvailable", b))
                }
                function t() {
                    return A && A.receiver ? A.receiver.friendlyName: ""
                }
                function u(a) {
                    g.log("Cast Session Error:", a, A);
                    var b = window.chrome;
                    a.code !== b.cast.ErrorCode.CANCEL && (g.log("Cast Session Error:", a, A), a.code === b.cast.ErrorCode.SESSION_ERROR && this.stopCasting())
                }
                function v(a) {
                    var b = window.chrome;
                    a.code !== b.cast.ErrorCode.CANCEL && (g.log("Cast Session Error:", a, A), a.code === b.cast.ErrorCode.SESSION_ERROR && this.stopCasting())
                }
                function w(a) {
                    a /= 100,
                    a = Math.max(0, Math.min(a, 1)),
                    A.setReceiverVolumeLevel(a, G.mirrorVolume.bind(G),
                    function(a) {
                        g.error("set volume error", a)
                    })
                }
                function x(a) {
                    A.setReceiverMuted(a, G.mirrorVolume.bind(G),
                    function(a) {
                        g.error("set muted error", a)
                    })
                }
                function y(a, b) {
                    function c(a) {
                        return function() {
                            b.sendCommand(a)
                        }
                    }
                    return {
                        getContainer: function() {
                            return document.createElement("div")
                        },
                        on: k,
                        onAdPlay: k,
                        onAdSkipped: k,
                        onAdComplete: k,
                        onAdError: k,
                        onCaptionsList: k,
                        onCaptionsChange: k,
                        onPlaylistItem: k,
                        onPlaylistComplete: k,
                        onError: k,
                        onResize: k,
                        onReady: k,
                        onFullscreen: k,
                        getState: k,
                        castToggle: k,
                        setFullscreen: k,
                        setVolume: w,
                        setMute: function() {
                            x(!a.get("mute"))
                        },
                        play: b.play.bind(b),
                        pause: b.pause.bind(b),
                        seek: b.seek.bind(b),
                        playlistNext: c("next"),
                        playlistPrev: c("prev"),
                        load: b.load.bind(b)
                    }
                }
                var z, A = null,
                B = {
                    available: !1,
                    active: !1,
                    deviceName: ""
                },
                C = h.getAbsolutePath(window.location.href),
                D = null,
                E = null,
                F = null;
                z = j.extend({},
                l, n.get("cast"));
                var G = this;
                this.onExistingSession = function(a) {
                    g.log("existing session", a),
                    A || E || (E = a.session, E.addMessageListener(g.NS, this.listenForHandshakeHandler))
                },
                this.setActive = function(a) {
                    B.active = !!a,
                    B.deviceName = t(),
                    this.castModel.set("castActive", !!a),
                    this.castModel.set("deviceName", t()),
                    this.castModel.set("castState", B)
                },
                this.startCasting = function() {
                    if (!A && !o()) {
                        var a = window.chrome;
                        a.cast.requestSession(this.sessionStarted.bind(this), u)
                    }
                },
                this.openExtension = function() {
                    if (!o()) {
                        var a = window.chrome;
                        a.cast.requestSession(this.sessionStarted.bind(this), v)
                    }
                },
                this.stopCasting = function() {
                    return A ? (this.removeSessionListeners(), void A.stop(this.sessionStopped.bind(this), this.sessionStopError.bind(this))) : void this.sessionStopped()
                },
                this.resumePlayback = function() {
                    m.showView(m._view.element()),
                    m.getProvider().name.indexOf("flash") >= 0 && n.resetProvider(),
                    m.load(this.castModel.get("item")),
                    m.play()
                },
                this.sessionStopError = function(a) {
                    g.error("Cast Session Stop error:", a, A),
                    this.sessionStopped()
                },
                this.sessionStarted = function(a) {
                    if (g.log("Session started:", a), A) this.stopCasting(),
                    F = a;
                    else if (A = a, A.addMessageListener(g.NS, this.onMessageHandler), A.addUpdateListener(this.sessionStatusHandler), this.setupControllerForCasting(), a !== E) {
                        var b = f.setupCastConfig(n, z);
                        D.setup(b),
                        d.sendDummyMedia(a)
                    }
                },
                this.sessionStatus = function(a) {
                    g.log("Cast Session status", a),
                    a ? this.mirrorVolume() : (this.castModel.set("state", i.BUFFERING), this.sessionStopped())
                },
                this.sessionStopped = function() {
                    g.log("_sessionStopped"),
                    A && (this.removeSessionListeners(), A = null),
                    D && (D.destroy(), D = null),
                    this.setActive(!1),
                    null !== F && (this.sessionStarted(F), F = null),
                    this.resumePlayback()
                },
                this.removeSessionListeners = function() {
                    A.removeUpdateListener(this.sessionStatusHandler),
                    A.removeMessageListener(g.NS, this.onMessageHandler)
                },
                this.onMessage = function(a, b) {
                    var c = JSON.parse(b);
                    if (!c) throw "Message not proper JSON";
                    this.handleMessage(c)
                },
                this.handleMessage = function(a) {
                    switch (a.type) {
                    case "state":
                        this.handleMessageState(a);
                        break;
                    case "ad":
                        this.handleMessageAd(a);
                        break;
                    case "connection":
                        this.handleMessageConnection(a);
                        break;
                    default:
                        g.error("received unhandled message", a.type, a)
                    }
                },
                this.handleMessageState = function(a) {
                    this.castModel.get("castAdPlaying") && (a.diff.newstate || a.diff.position) && this.castModel.set("castAdPlaying", !1),
                    D.updateState(a.diff)
                },
                this.handleMessageAd = function(a) {
                    this.castModel.set("castAdPlaying", !0),
                    D.updateState(a.diff)
                },
                this.handleMessageConnection = function(a) {
                    a.closed === !0 && this.stopCasting()
                },
                this.mirrorVolume = function() {
                    if (A && A.receiver) {
                        var a = A.receiver.volume;
                        if (a) {
                            var b = 100 * a.level | 0,
                            c = !!a.muted;
                            this.castModel.set("mute", c),
                            this.castModel.set("volume", b)
                        }
                    }
                },
                this.castToggle = function() {
                    B.active ? this.openExtension() : this.startCasting()
                },
                this.setupControllerForCasting = function() {
                    m.pause(),
                    m.setFullscreen(!1);
                    var d = new c({
                        castAvailable: !0,
                        playlist: n.get("playlist"),
                        playlistItem: n.get("playlistItem"),
                        edition: n.get("edition")
                    }),
                    f = new b;
                    this.castModel = f,
                    f.setup(d),
                    f.set("state", "playing"),
                    f.setItem(0),
                    f.changeVideoProvider(e),
                    D = f.getVideo(),
                    D.init(p, this.castModel);
                    var g = y(f, D);
                    g.castToggle = function() {
                        this.stopCasting()
                    }.bind(this);
                    var h = new a(g, f);
                    h.setup(),
                    m.showView(h.element()),
                    this.mirrorVolume(),
                    this.setActive(!0)
                },
                !z.appid || window.cast && window.cast.receiver || (d.addEventListener("availability", q), d.addEventListener("session", this.onExistingSessionHandler), d.initialize(z.appid)),
                this.onExistingSessionHandler = this.onExistingSession.bind(this),
                this.sessionStatusHandler = this.sessionStatus.bind(this),
                this.onMessageHandler = this.onMessage.bind(this),
                this.listenForHandshakeHandler = r.bind(this)
            };
            return m
        }.apply(b, d),
        !(void 0 !== e && (a.exports = e))
    },
    155 : function(a, b, c) {
        var d, e;
        d = [c(42), c(45), c(54)],
        e = function(a, b, c) {
            function d(a, d) {
                var f = b.pick(a.getConfiguration(), ["displaytitle", "id", "item", "key", "mute", "playlist", "position", "repeat", "volume"]);
                return f.cast = b.extend({
                    pageUrl: h
                },
                d),
                e(f),
                f.edition = a.get("edition"),
                f.playerVersion = c,
                f
            }
            function e(a) {
                var b = a.plugins;
                delete a.plugins;
                for (var c in b) if (b.hasOwnProperty(c)) {
                    var d = b[c];
                    d.client && (/[\.\/]/.test(d.client) && g(d, "client"), d.client.indexOf("vast") > -1 && (a.advertising = f(d)))
                }
            }
            function f(a) {
                var c = b.extend({},
                a);
                if (c.client = "vast", delete c.companiondiv, c.schedule) for (var d in c.schedule) if (c.schedule.hasOwnProperty(d)) {
                    var e = c.schedule[d].ad || c.schedule[d];
                    g(e, "tag")
                }
                return g(c, "tag"),
                c
            }
            function g(b, c) {
                b[c] && (b[c] = a.getAbsolutePath(b[c]))
            }
            var h = window.location.href;
            return {
                setupCastConfig: d
            }
        }.apply(b, d),
        !(void 0 !== e && (a.exports = e))
    },
    156 : function(a, b, c) {
        var d, e;
        d = [c(157), c(63), c(94), c(68), c(45)],
        e = function(a, b, c, d, e) {
            function f(d) {
                t = d,
                null !== a.availability ? u.sendEvent("availability", {
                    availability: a.availability
                }) : p && p.cast ? h() : n || (n = new c(q), n.addEventListener(b.ERROR, i), n.addEventListener(b.COMPLETE, h), n.load())
            }
            function g(a) {
                var b = new p.cast.media.MediaInfo("");
                b.contentType = "video/mp4";
                var c = new p.cast.media.LoadRequest(b);
                c.autoplay = !1,
                a.loadMedia(c)
            }
            function h() {
                p && p.cast && p.cast.isAvailable && !o ? (o = new p.cast.ApiConfig(new p.cast.SessionRequest(t), l, m, p.cast.AutoJoinPolicy.ORIGIN_SCOPED), p.cast.initialize(o, k, j)) : s++<15 && (r = setTimeout(h, 1e3))
            }
            function i() {
                n && (n.resetEventListeners(), n = null)
            }
            function j() {
                o = null
            }
            function k() {}
            function l(b) {
                u.sendEvent("session", {
                    session: b
                }),
                b.sendMessage(a.NS, {
                    whoami: 1
                }),
                0 === b.media.length && g(b)
            }
            function m(b) {
                a.availability = b,
                u.sendEvent("availability", {
                    availability: b
                })
            }
            var n, o, p = window.chrome,
            q = "https://www.gstatic.com/cv/js/sender/v1/cast_sender.js",
            r = -1,
            s = 0,
            t = null,
            u = e.extend({
                initialize: f,
                sendDummyMedia: g
            },
            new d("cast.loader"));
            return u
        }.apply(b, d),
        !(void 0 !== e && (a.exports = e))
    },
    158 : function(a, b, c) {
        var d, e;
        d = [c(42), c(159), c(63), c(68), c(62), c(45), c(157)],
        e = function(a, b, c, d, e, f, g) {
            function h(c) {
                var d, e = c.get("castLoading"),
                f = c.get("playlistItem").title;
                d = e ? f ? "Loading " + f + " on ": "Loading on ": f ? "Casting " + f + " to ": "Casting to";
                var g = {
                    message: d,
                    deviceName: c.get("deviceName") || "Google Cast"
                };
                return a.createElement(b(g))
            }
            var i = function() {
                var b, i = -1,
                j = a.noop;
                f.extend(this, new d("cast.provider")),
                this.setState = function(a) {
                    this.sendEvent(c.JWPLAYER_PLAYER_STATE, {
                        newstate: a
                    }),
                    this.model.set("state", a)
                },
                this.destroy = function() {
                    clearTimeout(i)
                },
                this.updateState = function(a) {
                    if (a.newstate && this.setState(a.newstate.toLowerCase()), a.item && (this.setState(e.BUFFERING), this.model.setItem(a.item)), (void 0 !== a.position || void 0 !== a.duration) && (void 0 !== a.position && (this.lastPosition = a.position), void 0 !== a.duration && (this.lastDuration = a.duration), this.model.get("state") === e.BUFFERING && this.setState(e.PLAYING), this.sendEvent(c.JWPLAYER_MEDIA_TIME, {
                        position: this.lastPosition,
                        duration: this.lastDuration
                    })), void 0 !== a.buffer && this.sendEvent(c.JWPLAYER_MEDIA_BUFFER, {
                        bufferPercent: a.buffer
                    }), a.tag) {
                        this.model.set("adMode", a.complete);
                        var b = ["clickthrough", "companions", "message", "podMessage", "podCount", "sequence", "skipMessage", "skipText", "skipOffset", "tag"];
                        f.each(b,
                        function(b) {
                            f.isUndefined(a[b]) || this.model.set(b, a[b])
                        },
                        this)
                    }
                },
                this.supportsFullscreen = function() {
                    return ! 1
                },
                this.init = function(a, b) {
                    j = a,
                    this.model = b,
                    this.model.on("change:item", this.updateScreen, this),
                    this.model.on("change:deviceName", this.updateScreen, this),
                    this._castingScreen = h(this.model)
                },
                this.setup = function(a) {
                    this.setState(e.BUFFERING),
                    this.sendCommand("setup", a)
                },
                this.playlistItem = function(a) {
                    this.setState(e.BUFFERING),
                    this.sendCommand("item", a)
                },
                this.load = function(a) {
                    this.setState(e.BUFFERING),
                    this.sendCommand("load", a)
                },
                this.stop = function() {
                    clearTimeout(i),
                    i = setTimeout(function() {
                        this.setState(e.IDLE),
                        this.sendCommand("stop")
                    }.bind(this), 0)
                },
                this.play = function() {
                    this.sendCommand("play")
                },
                this.pause = function() {
                    this.setState(e.PAUSED),
                    this.sendCommand("pause")
                },
                this.seek = function(a) {
                    this.setState(e.BUFFERING),
                    this.sendEvent(c.JWPLAYER_MEDIA_SEEK, {
                        position: this.model.position,
                        offset: a
                    }),
                    this.sendCommand("seek", a)
                },
                this.skipAd = function(a) {
                    this.sendCommand("skipAd", {
                        tag: a.tag
                    })
                },
                this.clickAd = function(a) {
                    this.sendCommand("clickAd", {
                        tag: a.tag
                    })
                },
                this.audioMode = function() {
                    return this.model.get("audioMode")
                },
                this.sendCommand = function(a, b) {
                    j(a, b)
                },
                this.detachMedia = function() {
                    return g.error("detachMedia called while casting"),
                    document.createElement("video")
                },
                this.attachMedia = function() {
                    g.error("attachMedia called while casting")
                },
                this.updateScreen = function() {
                    b.innerHTML = h(this.model).innerHTML
                },
                this.setContainer = function(a) {
                    b = a,
                    a.appendChild(this._castingScreen)
                },
                this.getContainer = function() {
                    return b
                },
                this.remove = function() {
                    b === this._castingScreen.parentNode && b.removeChild(this._castingScreen)
                },
                this.volume = this.mute = this.setControls = this.setCurrentQuality = this.resize = this.seekDrag = this.addCaptions = this.resetCaptions = this.setVisibility = this.fsCaptions = a.noop,
                this.setFullScreen = this.getFullScreen = this.checkComplete = f.constant(!1),
                this.getWidth = this.getHeight = this.getCurrentQuality = f.constant(0),
                this.getQualityLevels = f.constant(["Auto"])
            };
            return i.prototype = {
                getName: function() {
                    return {
                        name: "chromecast"
                    }
                }
            },
            i
        }.apply(b, d),
        !(void 0 !== e && (a.exports = e))
    },
    159 : function(a, b, c) {
        var d = c(117);
        a.exports = (d["default"] || d).template({
            compiler: [6, ">= 2.0.0-beta.1"],
            main: function(a, b, c, d) {
                var e = this.lambda,
                f = this.escapeExpression;
                return '<div class="jw-cast jw-reset" style="display: block;">\n    <div class="jw-cast-label jw-reset">\n        <span class="jw-reset">' + f(e(null != a ? a.message: a, a)) + '</span>\n        <span class="jw-cast-name jw-reset">' + f(e(null != a ? a.deviceName: a, a)) + "</span>\n    </div>\n</div>"
            },
            useData: !0
        })
    }
});