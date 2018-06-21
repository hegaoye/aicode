webpackJsonp(["styles"],{

/***/ "../../../../../src/styles.css":
/***/ (function(module, exports, __webpack_require__) {

// style-loader: Adds some css to the DOM by adding a <style> tag

// load the styles
var content = __webpack_require__("../../../../css-loader/index.js?{\"sourceMap\":false,\"importLoaders\":1}!../../../../postcss-loader/index.js?{\"ident\":\"postcss\"}!../../../../../src/styles.css");
if(typeof content === 'string') content = [[module.i, content, '']];
// add the styles to the DOM
var update = __webpack_require__("../../../../style-loader/addStyles.js")(content, {});
if(content.locals) module.exports = content.locals;
// Hot Module Replacement
if(false) {
	// When the styles change, update the <style> tags
	if(!content.locals) {
		module.hot.accept("!!../node_modules/css-loader/index.js??ref--7-1!../node_modules/postcss-loader/index.js??postcss!./styles.css", function() {
			var newContent = require("!!../node_modules/css-loader/index.js??ref--7-1!../node_modules/postcss-loader/index.js??postcss!./styles.css");
			if(typeof newContent === 'string') newContent = [[module.id, newContent, '']];
			update(newContent);
		});
	}
	// When the module is disposed, remove the <style> tags
	module.hot.dispose(function() { update(); });
}

/***/ }),

/***/ "../../../../css-loader/index.js?{\"sourceMap\":false,\"importLoaders\":1}!../../../../postcss-loader/index.js?{\"ident\":\"postcss\"}!../../../../../src/assets/css/base.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "/**\r\n * 此文件定义公共的全局样式\r\n */\r\nhtml,body{\r\n  margin:0;\r\n  padding:0;\r\n  height: 100%!important;\r\n}\r\n.ai-code{\r\n  height: 100%;\r\n}\r\n\r\n/******************当前使用的ng-zorro框架中，很多内置样式与当前项目不符，此处进行覆盖重写 start************************/\r\n/*重写表单中label字体大小*/\r\n.ai-code label {\r\n  font-size: 14px;\r\n}\r\n\r\n/*重写input字体大小*/\r\n.ai-code .ant-input {\r\n  font-size: 14px;\r\n  line-height: 1.6;\r\n}\r\n\r\n/*重写单选按钮字体大小*/\r\n.ai-code .ant-radio-wrapper {\r\n  font-size: 14px;\r\n}\r\n\r\n/*单选按钮位置调整（用vatical-align看起来不是很协调）*/\r\n.ai-code .ant-radio {\r\n  padding: 2px 0;\r\n}\r\n\r\n/*下拉选择箭头大小*/\r\n:root .ai-code .ant-cascader-picker-arrow {\r\n  font-size: 14px;\r\n}\r\n\r\n/*联动选择器字体大小*/\r\n.ai-code .ant-cascader-picker {\r\n  font-size: 14px;\r\n}\r\n\r\n/*日期插件中图标大小*/\r\n.ai-code .ant-calendar-picker-icon:after {\r\n  font-size: 14px;\r\n}\r\n\r\n/******************当前使用的ng-zorro框架中，很多内置样式与当前项目不符，此处进行覆盖重写 end**************************/\r\n\r\n/******************当前使用的ng-zorro框架中，很多内置样式与当前项目不符，此处进行扩展 start****************************/\r\n/*步骤条字体大小，及图标大小，用法：<nz-steps class=\"lg ...>*/\r\n.lg .ant-steps .ant-steps-head-inner {\r\n  width: 30px;\r\n  height: 30px;\r\n  line-height: 28px;\r\n  border-radius: 30px;\r\n  font-size: 16px;\r\n}\r\n\r\n.lg .ant-steps .ant-steps-title {\r\n  font-size: 16px;\r\n  line-height: 30px;\r\n}\r\n\r\n/*大号switch开关，给其父元素添加“lg”类名，用法：<nz-switch class=\"lg ...>*/\r\n.lg .ant-switch {\r\n  height: 26px;\r\n  min-width: 52px;\r\n  line-height: 24px;\r\n  border-radius: 24px;\r\n}\r\n\r\n.lg .ant-switch:after {\r\n  width: 24px;\r\n  height: 24px;\r\n  top: 0;\r\n}\r\n\r\n.lg .ant-switch-checked:after {\r\n  left: 100%;\r\n  margin-left: -24px;\r\n}\r\n\r\n.lg .ant-switch-inner {\r\n  font-size: 13px;\r\n  padding: 0 4px;\r\n}\r\n\r\n/******************当前使用的ng-zorro框架中，很多内置样式与当前项目不符，此处进行扩展 end******************************/\r\n\r\n/***************************************公共样式定义 start*************************************************************/\r\n/*遮罩样式*/\r\n.main-mask {\r\n  width: 100%;\r\n  height: 100%;\r\n  position: fixed;\r\n  display: none;\r\n  top: 0;\r\n  left: 0;\r\n  z-index: 99999;\r\n  background: rgba(0, 0, 0, 0.5)\r\n}\r\n\r\n.main-mask.ant-spin-nested-loading {\r\n  position: fixed;\r\n}\r\n\r\n/*页面提示信息（红色）*/\r\n.page-msg-red {\r\n  background-color: #fef0ef;\r\n  border: 1px solid #fcdedc;\r\n  padding: 10px;\r\n  position: relative;\r\n  zoom: 1;\r\n  line-height: 1.8;\r\n}\r\n\r\n/*提示信息标题*/\r\n.page-msg-red > p.title {\r\n  font-size: 14px;\r\n  margin-bottom: 5px;\r\n  font-weight: bold;\r\n}\r\n\r\n/*提示信息内容*/\r\n.page-msg-red > ul {\r\n  font-size: 14px;\r\n}\r\n\r\n.page-msg-red li {\r\n  margin-bottom: 5px;\r\n  padding-left: 30px;\r\n  position: relative;\r\n}\r\n\r\n.page-msg-red li:before {\r\n  position: absolute;\r\n  content: '\\25CF';\r\n  left: 18px;\r\n}\r\n/***************************************公共样式定义\r\nend***************************************************************/\r\n\r\n", ""]);

// exports


/***/ }),

/***/ "../../../../css-loader/index.js?{\"sourceMap\":false,\"importLoaders\":1}!../../../../postcss-loader/index.js?{\"ident\":\"postcss\"}!../../../../../src/assets/css/util.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "/**\r\n * 此文件定义公共的且基础的样式\r\n */\r\n\r\n/*定义margin基础属性*/\r\n.m0 { margin: 0 !important; }\r\n.ml0 { margin-left: 0 !important; }\r\n.mr0 { margin-right: 0 !important; }\r\n.mt0 { margin-top: 0 !important; }\r\n.mb0 { margin-bottom: 0 !important; }\r\n.m-1{margin: 1px !important;}\r\n.m-2{margin: 2px !important;}\r\n.m-3{margin: 3px !important;}\r\n.m-4{margin: 4px !important;}\r\n.m-5  { margin: 5px !important; }\r\n.ml-5 { margin-left: 5px !important; }\r\n.mr-5 { margin-right: 5px !important; }\r\n.mt-5 { margin-top: 5px !important; }\r\n.mb-5 { margin-bottom: 5px !important; }\r\n.m  { margin: 10px !important; }\r\n.ml { margin-left: 10px !important; }\r\n.mr { margin-right: 10px !important; }\r\n.mt { margin-top: 10px !important; }\r\n.mb { margin-bottom: 10px !important; }\r\n.m-15  { margin: 15px !important; }\r\n.ml-15 { margin-left: 15px !important; }\r\n.mr-15 { margin-right: 15px !important; }\r\n.mt-15 { margin-top: 15px !important; }\r\n.mb-15 { margin-bottom: 15px !important; }\r\n.m-20  { margin: 20px !important; }\r\n.ml-20 { margin-left: 20px !important; }\r\n.mr-20 { margin-right: 20px !important; }\r\n.mt-20 { margin-top: 20px !important; }\r\n.mb-20 { margin-bottom: 20px !important; }\r\n\r\n/*定义padding基础属性*/\r\n.p0 { padding: 0 !important; }\r\n.pl0 { padding-left: 0 !important; }\r\n.pr0 { padding-right: 0 !important; }\r\n.pt0 { padding-top: 0 !important; }\r\n.pb0 { padding-bottom: 0 !important; }\r\n.p-1{padding: 1px !important;}\r\n.p-2{padding: 2px !important;}\r\n.p-3{padding: 3px !important;}\r\n.p-4{padding: 4px !important;}\r\n.p-5  { padding: 5px !important; }\r\n.pl-5 { padding-left: 5px !important; }\r\n.pr-5 { padding-right: 5px !important; }\r\n.pt-5 { padding-top: 5px !important; }\r\n.pb-5 { padding-bottom: 5px !important; }\r\n.p  { padding: 10px !important; }\r\n.pl { padding-left: 10px !important; }\r\n.pr { padding-right: 10px !important; }\r\n.pt { padding-top: 10px !important; }\r\n.pb { padding-bottom: 10px !important; }\r\n.p-15  { padding: 15px !important; }\r\n.pl-15 { padding-left: 15px !important; }\r\n.pr-15 { padding-right: 15px !important; }\r\n.pt-15 { padding-top: 15px !important; }\r\n.pt-30 { padding-top: 30px !important; }\r\n.pb-15 { padding-bottom: 15px !important; }\r\n.p-20  { padding: 20px !important; }\r\n.pl-20 { padding-left: 20px !important; }\r\n.pr-20 { padding-right: 20px !important; }\r\n.pt-20 { padding-top: 20px !important; }\r\n.pb-20 { padding-bottom: 20px !important; }\r\n\r\n/*定义border基础属性*/\r\n.b0 { border-width: 0 !important; }\r\n.bl0 { border-left-width: 0 !important; }\r\n.br0 { border-right-width: 0 !important; }\r\n.bt0 { border-top-width: 0 !important; }\r\n.bb0 { border-bottom-width: 0 !important; }\r\n.br { border-right: 1px solid #e9e9e9;}\r\n.bl { border-left: 1px solid #e9e9e9;}\r\n.bt { border-top: 1px solid #e9e9e9;}\r\n.bb { border-bottom: 1px solid #e9e9e9;}\r\n.b, .ba { border:1px solid #e9e9e9; } /*all borders*/\r\n.bb-dashed { border-bottom: 1px dashed #e9e9e9;}  /*虚线下边框*/\r\n\r\n/*样式清除*/\r\n.clear{clear:both;}\r\n.display-inherit{display: inherit;}\r\n\r\n/*字体加粗*/\r\n.strong{font-weight: 600}\r\n\r\n/*a标签下划线*/\r\n.underline{text-decoration: underline!important}\r\n\r\n/*清除浮动*/\r\n.float-none{float: none;}\r\n\r\n/*浮动*/\r\n.fl{float: left;}\r\n.fr{float: right;}\r\n\r\n/*鼠标手型*/\r\n.cursor{cursor: pointer;}\r\n\r\n/*背景色*/\r\n.bg-white{background-color: #ffffff;}\r\n.bg-light-gray{background-color: #fbfbfb;}\r\n\r\n/*信息位置*/\r\n.text-center{text-align: center;}\r\n\r\n.color-red{\r\n  color:red !important;\r\n}\r\n/*文字大小*/\r\n.font12{font-size: 12px}\r\n.font14{font-size: 14px}\r\n.font16{font-size: 16px}\r\n.font18{font-size: 18px}\r\n.font20{font-size: 20px}\r\n.font22{font-size: 22px}\r\n.font24{font-size: 24px}\r\n.font26{font-size: 26px}\r\n.font28{font-size: 28px}\r\n.font30{font-size: 30px}\r\n\r\n/*宽度定义（百分比）*/\r\n.w1{width: 1%}\r\n.w2{width: 2%}\r\n.w3{width: 3%}\r\n.w4{width: 4%}\r\n.w5{width: 5%}\r\n.w6{width: 6%}\r\n.w7{width: 7%}\r\n.w8{width: 8%}\r\n.w9{width: 9%}\r\n.w10{width: 10%}\r\n.w11{width: 11%}\r\n.w12{width: 12%}\r\n.w13{width: 13%}\r\n.w14{width: 14%}\r\n.w15{width: 15%}\r\n.w16{width: 16%}\r\n.w17{width: 17%}\r\n.w18{width: 18%}\r\n.w19{width: 19%}\r\n.w20{width: 20%}\r\n.w21{width: 21%}\r\n.w22{width: 22%}\r\n.w23{width: 23%}\r\n.w24{width: 24%}\r\n.w25{width: 25%}\r\n.w26{width: 26%}\r\n.w27{width: 27%}\r\n.w28{width: 28%}\r\n.w29{width: 29%}\r\n.w30{width: 30%}\r\n.w31{width: 31%}\r\n.w32{width: 32%}\r\n.w33{width: 33%}\r\n.w34{width: 34%}\r\n.w35{width: 35%}\r\n.w36{width: 36%}\r\n.w37{width: 37%}\r\n.w38{width: 38%}\r\n.w39{width: 39%}\r\n.w40{width: 40%}\r\n.w41{width: 41%}\r\n.w42{width: 42%}\r\n.w43{width: 43%}\r\n.w44{width: 44%}\r\n.w45{width: 45%}\r\n.w46{width: 46%}\r\n.w47{width: 47%}\r\n.w48{width: 48%}\r\n.w49{width: 49%}\r\n.w50{width: 50%}\r\n.w51{width: 51%}\r\n.w52{width: 52%}\r\n.w53{width: 53%}\r\n.w54{width: 54%}\r\n.w55{width: 55%}\r\n.w56{width: 56%}\r\n.w57{width: 57%}\r\n.w58{width: 58%}\r\n.w59{width: 59%}\r\n.w60{width: 60%}\r\n.w61{width: 61%}\r\n.w62{width: 62%}\r\n.w63{width: 63%}\r\n.w64{width: 64%}\r\n.w65{width: 65%}\r\n.w66{width: 66%}\r\n.w67{width: 67%}\r\n.w68{width: 68%}\r\n.w69{width: 69%}\r\n.w70{width: 70%}\r\n.w71{width: 71%}\r\n.w72{width: 72%}\r\n.w73{width: 73%}\r\n.w74{width: 74%}\r\n.w75{width: 75%}\r\n.w76{width: 76%}\r\n.w77{width: 77%}\r\n.w78{width: 78%}\r\n.w79{width: 79%}\r\n.w80{width: 80%}\r\n.w81{width: 81%}\r\n.w82{width: 82%}\r\n.w83{width: 83%}\r\n.w84{width: 84%}\r\n.w85{width: 85%}\r\n.w86{width: 86%}\r\n.w87{width: 87%}\r\n.w88{width: 88%}\r\n.w89{width: 89%}\r\n.w90{width: 90%}\r\n.w91{width: 91%}\r\n.w92{width: 92%}\r\n.w93{width: 93%}\r\n.w94{width: 94%}\r\n.w95{width: 95%}\r\n.w96{width: 96%}\r\n.w97{width: 97%}\r\n.w98{width: 98%}\r\n.w99{width: 99%}\r\n.w100{width: 100%}\r\n\r\n/*高度定义*/\r\n.h100{\r\n  height: 100%;\r\n}\r\n\r\n.inlineBlock{\r\n  display: inline-block;\r\n}\r\n", ""]);

// exports


/***/ }),

/***/ "../../../../css-loader/index.js?{\"sourceMap\":false,\"importLoaders\":1}!../../../../postcss-loader/index.js?{\"ident\":\"postcss\"}!../../../../../src/styles.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports
exports.i(__webpack_require__("../../../../css-loader/index.js?{\"sourceMap\":false,\"importLoaders\":1}!../../../../postcss-loader/index.js?{\"ident\":\"postcss\"}!../../../../../src/assets/css/base.css"), "");
exports.i(__webpack_require__("../../../../css-loader/index.js?{\"sourceMap\":false,\"importLoaders\":1}!../../../../postcss-loader/index.js?{\"ident\":\"postcss\"}!../../../../../src/assets/css/util.css"), "");

// module
exports.push([module.i, "/* 平台样式库 */ /*全局公共样式*/ /*主样式*/\r\n", ""]);

// exports


/***/ }),

/***/ "../../../../css-loader/lib/css-base.js":
/***/ (function(module, exports) {

/*
	MIT License http://www.opensource.org/licenses/mit-license.php
	Author Tobias Koppers @sokra
*/
// css base code, injected by the css-loader
module.exports = function(useSourceMap) {
	var list = [];

	// return the list of modules as css string
	list.toString = function toString() {
		return this.map(function (item) {
			var content = cssWithMappingToString(item, useSourceMap);
			if(item[2]) {
				return "@media " + item[2] + "{" + content + "}";
			} else {
				return content;
			}
		}).join("");
	};

	// import a list of modules into the list
	list.i = function(modules, mediaQuery) {
		if(typeof modules === "string")
			modules = [[null, modules, ""]];
		var alreadyImportedModules = {};
		for(var i = 0; i < this.length; i++) {
			var id = this[i][0];
			if(typeof id === "number")
				alreadyImportedModules[id] = true;
		}
		for(i = 0; i < modules.length; i++) {
			var item = modules[i];
			// skip already imported module
			// this implementation is not 100% perfect for weird media query combinations
			//  when a module is imported multiple times with different media queries.
			//  I hope this will never occur (Hey this way we have smaller bundles)
			if(typeof item[0] !== "number" || !alreadyImportedModules[item[0]]) {
				if(mediaQuery && !item[2]) {
					item[2] = mediaQuery;
				} else if(mediaQuery) {
					item[2] = "(" + item[2] + ") and (" + mediaQuery + ")";
				}
				list.push(item);
			}
		}
	};
	return list;
};

function cssWithMappingToString(item, useSourceMap) {
	var content = item[1] || '';
	var cssMapping = item[3];
	if (!cssMapping) {
		return content;
	}

	if (useSourceMap && typeof btoa === 'function') {
		var sourceMapping = toComment(cssMapping);
		var sourceURLs = cssMapping.sources.map(function (source) {
			return '/*# sourceURL=' + cssMapping.sourceRoot + source + ' */'
		});

		return [content].concat(sourceURLs).concat([sourceMapping]).join('\n');
	}

	return [content].join('\n');
}

// Adapted from convert-source-map (MIT)
function toComment(sourceMap) {
	// eslint-disable-next-line no-undef
	var base64 = btoa(unescape(encodeURIComponent(JSON.stringify(sourceMap))));
	var data = 'sourceMappingURL=data:application/json;charset=utf-8;base64,' + base64;

	return '/*# ' + data + ' */';
}


/***/ }),

/***/ "../../../../style-loader/addStyles.js":
/***/ (function(module, exports) {

/*
	MIT License http://www.opensource.org/licenses/mit-license.php
	Author Tobias Koppers @sokra
*/
var stylesInDom = {},
	memoize = function(fn) {
		var memo;
		return function () {
			if (typeof memo === "undefined") memo = fn.apply(this, arguments);
			return memo;
		};
	},
	isOldIE = memoize(function() {
		return /msie [6-9]\b/.test(self.navigator.userAgent.toLowerCase());
	}),
	getHeadElement = memoize(function () {
		return document.head || document.getElementsByTagName("head")[0];
	}),
	singletonElement = null,
	singletonCounter = 0,
	styleElementsInsertedAtTop = [];

module.exports = function(list, options) {
	if(typeof DEBUG !== "undefined" && DEBUG) {
		if(typeof document !== "object") throw new Error("The style-loader cannot be used in a non-browser environment");
	}

	options = options || {};
	// Force single-tag solution on IE6-9, which has a hard limit on the # of <style>
	// tags it will allow on a page
	if (typeof options.singleton === "undefined") options.singleton = isOldIE();

	// By default, add <style> tags to the bottom of <head>.
	if (typeof options.insertAt === "undefined") options.insertAt = "bottom";

	var styles = listToStyles(list);
	addStylesToDom(styles, options);

	return function update(newList) {
		var mayRemove = [];
		for(var i = 0; i < styles.length; i++) {
			var item = styles[i];
			var domStyle = stylesInDom[item.id];
			domStyle.refs--;
			mayRemove.push(domStyle);
		}
		if(newList) {
			var newStyles = listToStyles(newList);
			addStylesToDom(newStyles, options);
		}
		for(var i = 0; i < mayRemove.length; i++) {
			var domStyle = mayRemove[i];
			if(domStyle.refs === 0) {
				for(var j = 0; j < domStyle.parts.length; j++)
					domStyle.parts[j]();
				delete stylesInDom[domStyle.id];
			}
		}
	};
}

function addStylesToDom(styles, options) {
	for(var i = 0; i < styles.length; i++) {
		var item = styles[i];
		var domStyle = stylesInDom[item.id];
		if(domStyle) {
			domStyle.refs++;
			for(var j = 0; j < domStyle.parts.length; j++) {
				domStyle.parts[j](item.parts[j]);
			}
			for(; j < item.parts.length; j++) {
				domStyle.parts.push(addStyle(item.parts[j], options));
			}
		} else {
			var parts = [];
			for(var j = 0; j < item.parts.length; j++) {
				parts.push(addStyle(item.parts[j], options));
			}
			stylesInDom[item.id] = {id: item.id, refs: 1, parts: parts};
		}
	}
}

function listToStyles(list) {
	var styles = [];
	var newStyles = {};
	for(var i = 0; i < list.length; i++) {
		var item = list[i];
		var id = item[0];
		var css = item[1];
		var media = item[2];
		var sourceMap = item[3];
		var part = {css: css, media: media, sourceMap: sourceMap};
		if(!newStyles[id])
			styles.push(newStyles[id] = {id: id, parts: [part]});
		else
			newStyles[id].parts.push(part);
	}
	return styles;
}

function insertStyleElement(options, styleElement) {
	var head = getHeadElement();
	var lastStyleElementInsertedAtTop = styleElementsInsertedAtTop[styleElementsInsertedAtTop.length - 1];
	if (options.insertAt === "top") {
		if(!lastStyleElementInsertedAtTop) {
			head.insertBefore(styleElement, head.firstChild);
		} else if(lastStyleElementInsertedAtTop.nextSibling) {
			head.insertBefore(styleElement, lastStyleElementInsertedAtTop.nextSibling);
		} else {
			head.appendChild(styleElement);
		}
		styleElementsInsertedAtTop.push(styleElement);
	} else if (options.insertAt === "bottom") {
		head.appendChild(styleElement);
	} else {
		throw new Error("Invalid value for parameter 'insertAt'. Must be 'top' or 'bottom'.");
	}
}

function removeStyleElement(styleElement) {
	styleElement.parentNode.removeChild(styleElement);
	var idx = styleElementsInsertedAtTop.indexOf(styleElement);
	if(idx >= 0) {
		styleElementsInsertedAtTop.splice(idx, 1);
	}
}

function createStyleElement(options) {
	var styleElement = document.createElement("style");
	styleElement.type = "text/css";
	insertStyleElement(options, styleElement);
	return styleElement;
}

function createLinkElement(options) {
	var linkElement = document.createElement("link");
	linkElement.rel = "stylesheet";
	insertStyleElement(options, linkElement);
	return linkElement;
}

function addStyle(obj, options) {
	var styleElement, update, remove;

	if (options.singleton) {
		var styleIndex = singletonCounter++;
		styleElement = singletonElement || (singletonElement = createStyleElement(options));
		update = applyToSingletonTag.bind(null, styleElement, styleIndex, false);
		remove = applyToSingletonTag.bind(null, styleElement, styleIndex, true);
	} else if(obj.sourceMap &&
		typeof URL === "function" &&
		typeof URL.createObjectURL === "function" &&
		typeof URL.revokeObjectURL === "function" &&
		typeof Blob === "function" &&
		typeof btoa === "function") {
		styleElement = createLinkElement(options);
		update = updateLink.bind(null, styleElement);
		remove = function() {
			removeStyleElement(styleElement);
			if(styleElement.href)
				URL.revokeObjectURL(styleElement.href);
		};
	} else {
		styleElement = createStyleElement(options);
		update = applyToTag.bind(null, styleElement);
		remove = function() {
			removeStyleElement(styleElement);
		};
	}

	update(obj);

	return function updateStyle(newObj) {
		if(newObj) {
			if(newObj.css === obj.css && newObj.media === obj.media && newObj.sourceMap === obj.sourceMap)
				return;
			update(obj = newObj);
		} else {
			remove();
		}
	};
}

var replaceText = (function () {
	var textStore = [];

	return function (index, replacement) {
		textStore[index] = replacement;
		return textStore.filter(Boolean).join('\n');
	};
})();

function applyToSingletonTag(styleElement, index, remove, obj) {
	var css = remove ? "" : obj.css;

	if (styleElement.styleSheet) {
		styleElement.styleSheet.cssText = replaceText(index, css);
	} else {
		var cssNode = document.createTextNode(css);
		var childNodes = styleElement.childNodes;
		if (childNodes[index]) styleElement.removeChild(childNodes[index]);
		if (childNodes.length) {
			styleElement.insertBefore(cssNode, childNodes[index]);
		} else {
			styleElement.appendChild(cssNode);
		}
	}
}

function applyToTag(styleElement, obj) {
	var css = obj.css;
	var media = obj.media;

	if(media) {
		styleElement.setAttribute("media", media)
	}

	if(styleElement.styleSheet) {
		styleElement.styleSheet.cssText = css;
	} else {
		while(styleElement.firstChild) {
			styleElement.removeChild(styleElement.firstChild);
		}
		styleElement.appendChild(document.createTextNode(css));
	}
}

function updateLink(linkElement, obj) {
	var css = obj.css;
	var sourceMap = obj.sourceMap;

	if(sourceMap) {
		// http://stackoverflow.com/a/26603875
		css += "\n/*# sourceMappingURL=data:application/json;base64," + btoa(unescape(encodeURIComponent(JSON.stringify(sourceMap)))) + " */";
	}

	var blob = new Blob([css], { type: "text/css" });

	var oldSrc = linkElement.href;

	linkElement.href = URL.createObjectURL(blob);

	if(oldSrc)
		URL.revokeObjectURL(oldSrc);
}


/***/ }),

/***/ 2:
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__("../../../../../src/styles.css");


/***/ })

},[2]);
//# sourceMappingURL=styles.bundle.js.map