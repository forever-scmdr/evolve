!function () {
    "use strict";
    var t;
    var e = ((t = {})["color-brand"] = "#2196f3", t["color-gull-gray"] = "#9db2bd", t["color-scooter"] = "#38acdb", t["color-curious-blue"] = "#299dcd", t);
    var i = document.createElement("a");

    function r(t) {
        i.href = t, i.host || (i.href = i.href);
        var e = i.host;
        i.pathname;
        return "http:" === i.protocol && (e = e.replace(/:80$/, "")), "https:" === i.protocol && (e = e.replace(/:443$/, "")), {
            host: e,
            pathname: ("/" === i.pathname[0] ? "" : "/") + i.pathname,
            href: i.href
        }
    }

    var n, o, a = {
        events: {
            width: 510,
            height: 600,
            colorTheme: "light",
            isTransparent: !1,
            hideImportanceIndicator: !1,
            autosize: !1
        },
        hotlists: {width: 400, height: 600, isTransparent: !1},
        screener: {
            width: 1100,
            height: 512,
            defaultColumn: "overview",
            defaultScreen: "general",
            market: "forex",
            showToolbar: !0,
            colorTheme: "light",
            isTransparent: !1
        },
        tickers: {colorTheme: "light", isTransparent: !1},
        financials: {
            width: 480,
            height: 830,
            autosize: !1,
            symbol: "NASDAQ:AAPL",
            colorTheme: "light",
            isTransparent: !1,
            displayMode: "regular",
            largeChartUrl: ""
        },
        "crypto-mkt-screener": {
            width: 1e3,
            height: 490,
            defaultColumn: "overview",
            market: "crypto",
            screener_type: "crypto_mkt",
            displayCurrency: "USD",
            colorTheme: "light",
            isTransparent: !1
        },
        "forex-cross-rates": {
            width: 770,
            height: 400,
            currencies: ["EUR", "USD", "JPY", "GBP", "CHF", "AUD", "CAD", "NZD", "CNY"],
            frameElementId: null
        },
        "forex-heat-map": {
            width: 770,
            height: 400,
            currencies: ["EUR", "USD", "JPY", "GBP", "CHF", "AUD", "CAD", "NZD", "CNY"],
            frameElementId: null
        },
        "market-overview": {width: 400, height: 650, isTransparent: !1},
        "market-quotes": {width: 770, height: 450},
        "mini-symbol-overview": {
            width: 350,
            height: 220,
            symbol: "FX:EURUSD",
            dateRange: "12m",
            colorTheme: "light",
            trendLineColor: "#37a6ef",
            underLineColor: "#e3f2fd",
            isTransparent: !1,
            autosize: !1,
            largeChartUrl: ""
        },
        "single-quote": {width: 350, symbol: "FX:EURUSD", colorTheme: "light", isTransparent: !1},
        "symbol-profile": {width: 480, height: 650, symbol: "NASDAQ:AAPL", colorTheme: "light", isTransparent: !1},
        "symbol-info": {width: 1e3, symbol: "NASDAQ:AAPL", colorTheme: "light", isTransparent: !1},
        "technical-analysis": {
            interval: "1m",
            width: 425,
            colorTheme: "light",
            isTransparent: !1,
            height: 450,
            symbol: "NASDAQ:AAPL",
            showIntervalTabs: !0
        },
        "ticker-tape": {colorTheme: "light", isTransparent: !1, displayMode: "adaptive"}
    };
    !function (t) {
        !function (t) {
            t.SetSymbol = "set-symbol", t.SetInterval = "set-interval"
        }(t.Names || (t.Names = {}))
    }(n || (n = {})), function (t) {
        !function (t) {
            t.SymbolClick = "tv-widget-symbol-click", t.WidgetLoad = "tv-widget-load", t.ResizeIframe = "tv-widget-resize-iframe"
        }(t.Names || (t.Names = {}))
    }(o || (o = {}));
    var s = ["locale", "symbol"], c = function () {
        var t = this;
        this._getScriptsInfo().forEach(function (e) {
            t._replaceScript(e)
        })
    }, h = {
        widgetId: {configurable: !0},
        widgetUtmName: {configurable: !0},
        defaultSettings: {configurable: !0},
        propertiesToWorkWith: {configurable: !0},
        propertiesToSkipInHash: {configurable: !0},
        propertiesToAddToGetParams: {configurable: !0}
    };
    h.widgetId.get = function () {
        throw new Error("Method must be overridden")
    }, h.widgetUtmName.get = function () {
        return this.widgetId
    }, h.defaultSettings.get = function () {
        return a[this.widgetId]
    }, h.propertiesToWorkWith.get = function () {
        return []
    }, c.prototype.filterRawSettings = function (t) {
        var e = this, i = {};
        return Object.keys(t).forEach(function (r) {
            -1 !== e.propertiesToWorkWith.indexOf(r) && (i[r] = t[r])
        }), i
    }, h.propertiesToSkipInHash.get = function () {
        return ["customer", "locale"]
    }, h.propertiesToAddToGetParams.get = function () {
        return ["locale"]
    }, c.prototype._getScriptsInfo = function () {
        var t = function () {
            if (document.currentScript) return document.currentScript.src;
            for (var t = document.getElementsByTagName("script"), e = 0; e < t.length; e++) if ("interactive" === t[e].readyState) return t[e].src;
            try {
                throw new Error
            } catch (t) {
                var i = /\((.*?):\d+:\d+\)\s*$/m.exec(t.stack);
                if (i) return i[1]
            }
            return null
        }();
        if (!t) return console.error("Could not self-replace the script, widget embedding has been aborted"), [];
        for (var e = r(t), i = e.host, n = e.href, o = document.getElementsByTagName("script"), a = [], s = 0; s < o.length; s++) {
            var c = o.item(s);
            c.src && r(c.src).href === n && a.push(c)
        }
        var h,
            l = (void 0 === (h = i) && (h = location.host), -1 !== ["i18n.tradingview.com", "partial.tradingview.com", "www.tradingview.com", "wwwcn.tradingview.com"].indexOf(h) || -1 !== ["d33t3vvu2t2yu5.cloudfront.net", "dwq4do82y8xi7.cloudfront.net", "s.tradingview.com", "s3.tradingview.com"].indexOf(h) || h.match(/^[a-z]{2}\.tradingview\.com/) || h.match(/prod-[^.]+.tradingview.com/) ? "battle" : -1 !== h.indexOf("tradingview.com") ? "staging" : h.match(/webcharts/) ? "staging_local" : (h.match(/^localhost(:\d+)?$/), "local"));
        return a.map(function (t) {
            return {scriptHost: i, scriptEnv: l, scriptElement: t}
        })
    }, c.prototype._replaceScript = function (t) {
        var i = this, r = t.scriptEnv, n = t.scriptHost, a = t.scriptElement;
        this.script = a;
        var s = this._scriptContentToJSON(), c = function (t) {
            if (null === t) return null;
            var e = t.querySelector("#tradingview-copyright"), i = t.querySelector("#tradingview-quotes"), r = e || i;
            return r && t.removeChild(r), r
        }(this.script.parentNode), h = !!this.script.parentNode.querySelector(".tradingview-widget-copyright");
        this.hasCopyright = c || h, s && (this.settings = this.filterRawSettings(s)), s && this._isValidSettings() || (console.error("Invalid settings provided, fall back to defaults"), this.settings = this.filterRawSettings(this.defaultSettings));
        var l, d = isNaN(this.settings.height) ? this.settings.height : this.settings.height + "px",
            p = isNaN(this.settings.width) ? this.settings.width : this.settings.width + "px",
            g = this.script.parentNode.classList.contains("tradingview-widget-container");
        this.script.parentNode && g ? this.iframeContainer = this.script.parentNode : this.iframeContainer = document.createElement("div"), this.iframeContainer.style.width = p, this.iframeContainer.style.height = d, this.iframeContainer.appendChild(((l = document.createElement("style")).innerHTML = "\n\t.tradingview-widget-copyright {\n\t\tfont-size: 13px !important;\n\t\tline-height: 32px !important;\n\t\ttext-align: center !important;\n\t\tvertical-align: middle !important;\n\t\tfont-family: 'Trebuchet MS', Arial, sans-serif !important;\n\t\tcolor: " + e["color-gull-gray"] + " !important;\n\t}\n\n\t.tradingview-widget-copyright .blue-text {\n\t\tcolor: " + e["color-brand"] + " !important;\n\t}\n\n\t.tradingview-widget-copyright a {\n\t\ttext-decoration: none !important;\n\t\tcolor: " + e["color-gull-gray"] + " !important;\n\t}\n\n\t.tradingview-widget-copyright a:visited {\n\t\tcolor: " + e["color-gull-gray"] + " !important;\n\t}\n\n\t.tradingview-widget-copyright a:hover .blue-text {\n\t\tcolor: " + e["color-scooter"] + " !important;\n\t}\n\n\t.tradingview-widget-copyright a:active .blue-text {\n\t\tcolor: " + e["color-curious-blue"] + " !important;\n\t}\n\n\t.tradingview-widget-copyright a:visited .blue-text {\n\t\tcolor: " + e["color-brand"] + " !important;\n\t}\n\t", l));
        var u = c && !this.settings.whitelabel, m = this.hasCopyright ? "calc(" + d + " - 32px)" : d;
        this.settings.utm_source = location.hostname, this.settings.utm_medium = h ? "widget_new" : "widget", this.settings.utm_campaign = this.widgetUtmName, this.iframe = this._createIframe(m, p, n, r, a.id);
        var f = this.iframeContainer.querySelector(".tradingview-widget-container__widget");
        if (f ? (this.script.parentNode.replaceChild(this.iframe, f), this.script.parentNode.removeChild(this.script)) : g ? (this.iframeContainer.appendChild(this.iframe), this.script.parentNode.removeChild(this.script)) : (this.iframeContainer.appendChild(this.iframe), this.script.parentNode.replaceChild(this.iframeContainer, this.script)), function (t, e, i) {
            var r = e.contentWindow;
            if (!r) return console.error("Cannot listen to the event from the provided iframe, contentWindow is not available"), function () {
            };

            function n(e) {
                e.source && e.source === r && e.data && e.data.name && e.data.name === t && i(e.data.data)
            }

            window.addEventListener("message", n, !1)
        }(o.Names.ResizeIframe, this.iframe, function (t) {
            t.width && (i.iframe.style.width = t.width + "px", i.iframeContainer.style.width = t.width + "px"), i.iframe.style.height = t.height + "px", i.iframeContainer.style.height = t.height + (i.hasCopyright ? 32 : 0) + "px"
        }), u) {
            var w = document.createElement("div");
            w.style.height = "32px", w.style.lineHeight = "32px", w.style.width = p, w.style.textAlign = "center", w.style.verticalAlign = "middle", w.innerHTML = c.innerHTML, this.iframeContainer.appendChild(w)
        }
    }, c.prototype._iframeSrcBase = function (t, e) {
        var i = "https://s.tradingview.com";
        return "local" === e ? i = "http://" + t : "staging" === e && (i = -1 !== t.indexOf("beta.tradingview.com") ? "https://betacdn.tradingview.com" : "https://" + t), i += "/embed-widget/" + this.widgetId + "/", this.settings.customer && -1 !== this.propertiesToSkipInHash.indexOf("customer") && (i += this.settings.customer + "/"), i
    }, c.prototype._isValidSettings = function () {
        var t = function (t) {
            if (void 0 === t) return !0;
            var e = parseInt(t) + "%" == t + "";
            return parseInt(t) + "" == t + "" || e || "auto" === t
        };
        return t(this.settings.width) && t(this.settings.height)
    }, c.prototype._buildGetQueryString = function () {
        var t = this.propertiesToAddToGetParams.filter(function (t) {
            return -1 !== s.indexOf(t)
        });
        return 0 === t.length ? "" : "?" + function (t) {
            var e = [];
            for (var i in t) t.hasOwnProperty(i) && null != t[i] && e.push({
                key: i,
                pair: encodeURIComponent(i) + "=" + encodeURIComponent(t[i])
            });
            return e.sort(function (t, e) {
                return t.key > e.key ? 1 : t.key < e.key ? -1 : 0
            }).map(function (t) {
                return t.pair
            }).join("&")
        }(function (t, e) {
            for (var i = Object.create(Object.getPrototypeOf(t)), r = 0, n = e; r < n.length; r++) {
                var o = n[r];
                Object.prototype.hasOwnProperty.call(t, o) && (i[o] = t[o])
            }
            return i
        }(this.settings, t))
    }, c.prototype._buildHashString = function (t) {
        var e = this, i = {};
        return t && (i.frameElementId = t), Object.keys(this.settings).forEach(function (t) {
            -1 === e.propertiesToSkipInHash.indexOf(t) && (i[t] = e.settings[t])
        }), Object.keys(i).length > 0 ? "#" + encodeURIComponent(JSON.stringify(i)) : ""
    }, c.prototype._scriptContentToJSON = function () {
        var t = this.script.innerHTML.trim();
        try {
            return JSON.parse(t)
        } catch (t) {
            return console.error("Widget settings parse error: " + t), null
        }
    }, c.prototype._createIframe = function (t, e, i, r, n) {
        var o = document.createElement("iframe");
        n && (o.id = n), this.settings.enableScrolling || o.setAttribute("scrolling", "no"), o.setAttribute("allowtransparency", !0), o.setAttribute("frameborder", 0), o.style.boxSizing = "border-box", o.style.height = t, o.style.width = e;
        var a = this._iframeSrcBase(i, r) + this._buildGetQueryString() + this._buildHashString(n);
        return o.setAttribute("src", a), o
    }, Object.defineProperties(c.prototype, h);
    new (function (t) {
        function e() {
            t.apply(this, arguments)
        }

        t && (e.__proto__ = t), e.prototype = Object.create(t && t.prototype), e.prototype.constructor = e;
        var i = {widgetId: {configurable: !0}, propertiesToWorkWith: {configurable: !0}};
        return i.widgetId.get = function () {
            return "tickers"
        }, i.propertiesToWorkWith.get = function () {
            return ["symbols", "linkPageTemplate", "largeChartUrl", "timeframe", "locale", "whitelabel", "customer", "colorTheme", "isTransparent"]
        }, e.prototype.filterRawSettings = function (e) {
            var i = t.prototype.filterRawSettings.call(this, e);
            return i.width = "100%", i.height = this._calculateHeight(e.whitelabel), i
        }, e.prototype._calculateHeight = function (t) {
            var e = 0, i = this.script.parentNode.querySelector(".tradingview-widget-copyright"),
                r = this.script.parentNode.querySelector("#tradingview-copyright");
            if (!t && (i || r)) {
                e += 32
            }
            return e += 72
        }, Object.defineProperties(e.prototype, i), e
    }(c))
}();
