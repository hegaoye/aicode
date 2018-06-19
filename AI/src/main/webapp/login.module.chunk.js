webpackJsonp(["login.module"],{

/***/ "../../../../../src/app/routes/login/login.module.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoginModule", function() { return LoginModule; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_common__ = __webpack_require__("../../../common/esm5/common.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__login_login_component__ = __webpack_require__("../../../../../src/app/routes/login/login/login.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__shared_shared_module__ = __webpack_require__("../../../../../src/app/shared/shared.module.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__login_service__ = __webpack_require__("../../../../../src/app/routes/login/login.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__reg_reg_component__ = __webpack_require__("../../../../../src/app/routes/login/reg/reg.component.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};







var routes = [
    { path: '', component: __WEBPACK_IMPORTED_MODULE_2__login_login_component__["a" /* LoginComponent */] },
    { path: 'reg', component: __WEBPACK_IMPORTED_MODULE_6__reg_reg_component__["a" /* RegComponent */] }
];
var LoginModule = (function () {
    function LoginModule() {
    }
    LoginModule = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["J" /* NgModule */])({
            imports: [
                __WEBPACK_IMPORTED_MODULE_1__angular_common__["b" /* CommonModule */],
                __WEBPACK_IMPORTED_MODULE_4__shared_shared_module__["a" /* SharedModule */].forRoot(),
                __WEBPACK_IMPORTED_MODULE_3__angular_router__["c" /* RouterModule */].forChild(routes)
            ],
            declarations: [__WEBPACK_IMPORTED_MODULE_2__login_login_component__["a" /* LoginComponent */], __WEBPACK_IMPORTED_MODULE_6__reg_reg_component__["a" /* RegComponent */]],
            exports: [
                __WEBPACK_IMPORTED_MODULE_3__angular_router__["c" /* RouterModule */]
            ],
            providers: [
                __WEBPACK_IMPORTED_MODULE_5__login_service__["a" /* LoginService */]
            ]
        })
    ], LoginModule);
    return LoginModule;
}());



/***/ }),

/***/ "../../../../../src/app/routes/login/login.service.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return LoginService; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__ = __webpack_require__("../../../../../src/app/public/service/ajax.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__ = __webpack_require__("../../../../../src/app/public/setting/setting_url.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};



var LoginService = (function () {
    function LoginService() {
        /**
         * 用户登录
         * @param account
         * @param pwd
         * @returns {{success: boolean; info: string}}
         */
        this.login = function (account, pwd) {
            var ret = { success: false, info: "登录失败！" };
            __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__["a" /* AjaxService */].get({
                url: __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].URL.login.signin,
                data: { account: account, password: pwd },
                async: false,
                success: function (res) {
                    ret = res;
                }
            });
            return ret;
        };
        /**
         * 用户注册
         * @param account
         * @param pwd
         * @returns {{success: boolean; info: string}}
         */
        this.reg = function (account, pwd) {
            var ret = { success: false, info: "注册失败！" };
            __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__["a" /* AjaxService */].post({
                url: __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].URL.login.reg,
                data: { account: account, password: pwd },
                async: false,
                success: function (res) {
                    ret = res;
                }
            });
            return ret;
        };
    }
    LoginService = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["B" /* Injectable */])(),
        __metadata("design:paramtypes", [])
    ], LoginService);
    return LoginService;
}());



/***/ }),

/***/ "../../../../../src/app/routes/login/login/login.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, ".login-logo {\r\n  height: 31px;\r\n  margin: 16px 24px 16px 0;\r\n}\r\n\r\n.login-layout .login-header {\r\n  background: #ececec;\r\n  height: 76px;\r\n}\r\n\r\n.login-layout .login-center {\r\n  height: 558px;\r\n  padding: 0;\r\n  background: #fff;\r\n}\r\n\r\n.login-layout .login-footer {\r\n  text-align: center;\r\n  padding-top: 60px;\r\n}\r\n\r\n.login-layout .login-center .login-center-banner {\r\n  width: 100%;\r\n  height: 558px;\r\n}\r\n\r\n.login-layout .login-center .login-center-banner img {\r\n  width: 100%;\r\n  height: 100%;\r\n}\r\n\r\n.login-form-button {\r\n  height: 44px;\r\n  width: 80%;\r\n  margin-left: 9%;\r\n  color: white;\r\n}\r\n.ant-btn-primary:hover{\r\n  color: white;\r\n}\r\n.ant-btn-primary:focus{\r\n  color: white;\r\n}\r\n.login-form {\r\n  width: 22%;\r\n  height: 472px;\r\n  background: #ffffff;\r\n  position: absolute;\r\n  top: 120px;\r\n  right: 20%;\r\n}\r\n\r\n.login-form .login-form-information {\r\n  width: 80%;\r\n  height: 44px;\r\n  margin-left: 9%;\r\n}\r\n\r\n.headings h1 {\r\n  height: 100px;\r\n  line-height: 100px;\r\n  text-align: center;\r\n  color: #e82d4d;\r\n}\r\n\r\n.login-form .passed ul li {\r\n  display: inline;\r\n  padding: 0 15px;\r\n}\r\n\r\n.login-form .passed ul li:focus {\r\n    outline: none;\r\n}\r\n\r\n.login-form .passed ul li:hover {\r\n  color: #e82d4d;\r\n  cursor: pointer;\r\n}\r\n\r\n.login-form .ant-btn-primary {\r\n  background-color: #e82d4d;\r\n  border-color:#e82d4d;\r\n}\r\n\r\n.login-form .ant-btn-primary:hover, .ant-btn-primary:focus {\r\n  background-color: #e82d4d;\r\n  border-color: #e82d4d;\r\n}\r\n\r\n.login-form nz-input {\r\n  height: 40px;\r\n  font-size: 14px;\r\n}\r\n\r\n.login-form .ant-form-item-with-help {\r\n  margin-bottom: 24px;\r\n}\r\n\r\n\r\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/routes/login/login/login.component.html":
/***/ (function(module, exports) {

module.exports = "<nz-layout class=\"login-layout\">\r\n  <!--登录页的顶部start-->\r\n  <nz-header class=\"login-header\">\r\n    <!--登录页logo start-->\r\n    <div class=\"login-logo\">\r\n      <img [src]=\"app.logoDark\">\r\n    </div>\r\n    <!--登录页logo end-->\r\n  </nz-header>\r\n  <!--登录页的顶部end-->\r\n  <!--登录页中心的广告banner start-->\r\n  <nz-content>\r\n    <div class=\"login-center\" style=\"padding: 0;\">\r\n      <nz-carousel [nzAutoPlay]=\"true\">\r\n        <div class=\"login-center-banner\" nz-carousel-content *ngFor=\"let index of array\">\r\n          <img [src]=\"index\">\r\n        </div>\r\n      </nz-carousel>\r\n    </div>\r\n  </nz-content>\r\n  <!--登录页中心的广告banner end-->\r\n  <!--登录页底部 start-->\r\n  <nz-footer class=\"login-footer\">{{app.copyright}}</nz-footer>\r\n  <!--登录页底 部 end-->\r\n</nz-layout>\r\n<!--登录验证表单start-->\r\n<form nz-form [formGroup]=\"validateForm\" class=\"login-form\">\r\n  <!--表单头部start-->\r\n  <div class=\"headings\"><h1>账号登录</h1></div>\r\n  <!--表单头部end-->\r\n  <!--表单用户名input框start-->\r\n  <div nz-form-item class=\"login-form-information\">\r\n    <div nz-form-control>\r\n      <nz-input formControlName=\"account\" [nzPlaceHolder]=\"'请填写用户名'\" required [(ngModel)]=\"account\" [nzSize]=\"'large'\">\r\n        <ng-template #prefix>\r\n          <i class=\"anticon anticon-user font18\"></i>\r\n        </ng-template>\r\n      </nz-input>\r\n    </div>\r\n  </div>\r\n  <!--表单用户名input框end-->\r\n  <!--表单密码input框start-->\r\n  <div nz-form-item class=\"login-form-information\">\r\n    <div nz-form-control>\r\n      <nz-input formControlName=\"pwd\" [nzType]=\"'password'\" required [(ngModel)]=\"pwd\" [nzPlaceHolder]=\"'密码'\" [nzSize]=\"'large'\">\r\n        <ng-template #prefix>\r\n          <i class=\"anticon anticon-lock font18\"></i>\r\n        </ng-template>\r\n      </nz-input>\r\n    </div>\r\n  </div>\r\n  <!--表单密码input框end-->\r\n\r\n  <!--表单底部start-->\r\n  <div nz-form-item>\r\n    <div nz-form-control>\r\n      <button nz-button class=\"login-form-button\" [nzType]=\"'primary'\" [nzSize]=\"'large'\"\r\n              (click)=\"login()\">登录\r\n      </button>\r\n      <div class=\"passed\">\r\n        <ul class=\"fr mr-20 mt-5\">\r\n          <li [routerLink]=\"registerUrl\"><u>注册</u></li>\r\n        </ul>\r\n      </div>\r\n    </div>\r\n  </div>\r\n  <!--表单底部end-->\r\n</form>\r\n<!--登录验证的表单end-->\r\n\r\n"

/***/ }),

/***/ "../../../../../src/app/routes/login/login/login.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return LoginComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_forms__ = __webpack_require__("../../../forms/esm5/forms.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__public_setting_setting__ = __webpack_require__("../../../../../src/app/public/setting/setting.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_ng_zorro_antd__ = __webpack_require__("../../../../ng-zorro-antd/esm5/antd.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__login_service__ = __webpack_require__("../../../../../src/app/routes/login/login.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__public_setting_setting_url__ = __webpack_require__("../../../../../src/app/public/setting/setting_url.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};







var LoginComponent = (function () {
    function LoginComponent(fb, router, _notification, loginService) {
        var _this = this;
        this.fb = fb;
        this.router = router;
        this._notification = _notification;
        this.loginService = loginService;
        this.array = ['../../../assets/img/1.jpg']; //广告banner
        this.app = __WEBPACK_IMPORTED_MODULE_2__public_setting_setting__["a" /* Setting */].APP; //平台基本信息
        this.registerUrl = __WEBPACK_IMPORTED_MODULE_6__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.login.reg; //注册页面路由
        /**
         * 登录
         * @param $event
         * @param value
         */
        this.login = function () {
            var me = _this;
            if (!_this.account) {
                me._notification.error('警告', "用户名必填！");
                return false;
            }
            if (!_this.pwd) {
                me._notification.error('警告', "密码必填！");
                return false;
            }
            var ret = _this.loginService.login(_this.account, _this.pwd);
            if (ret.success) {
                me._notification.success('成功', "登录成功！");
                sessionStorage.setItem('token', ret.data); //设置token
                _this.router.navigate([__WEBPACK_IMPORTED_MODULE_6__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.project.list]); //路由跳转（首页）
            }
            else {
                me._notification.error('警告', ret.info);
            }
        };
    }
    //用于登录时的表单
    LoginComponent.prototype._submitForm = function () {
        for (var i in this.validateForm.controls) {
            this.validateForm.controls[i].markAsDirty();
        }
    };
    LoginComponent.prototype.ngOnInit = function () {
        var me = this;
        /**
         *广告banner定时器
         */
        setTimeout(function (_) {
            me.array = ['../../../assets/img/1.jpg'];
        }, 5000);
        /**
         * 用于登录时的表单验证
         * @type {FormGroup}
         */
        me.validateForm = this.fb.group({
            account: [null, [__WEBPACK_IMPORTED_MODULE_1__angular_forms__["f" /* Validators */].required]],
            pwd: [null, [__WEBPACK_IMPORTED_MODULE_1__angular_forms__["f" /* Validators */].required]],
            remember: [true],
        });
        // //判断是否已经登录，已经登录，引导进入首页
        // let loginCookie = this.cookieService.get(Setting.cookie.szhLinfoStore);
        // if (loginCookie) this.router.navigate([SettingUrl.ROUTERLINK.store.home]); //路由跳转（首页）
    };
    LoginComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["n" /* Component */])({
            selector: 'app-login',
            template: __webpack_require__("../../../../../src/app/routes/login/login/login.component.html"),
            styles: [__webpack_require__("../../../../../src/app/routes/login/login/login.component.css")]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__angular_forms__["a" /* FormBuilder */], __WEBPACK_IMPORTED_MODULE_3__angular_router__["b" /* Router */], __WEBPACK_IMPORTED_MODULE_4_ng_zorro_antd__["c" /* NzNotificationService */], __WEBPACK_IMPORTED_MODULE_5__login_service__["a" /* LoginService */]])
    ], LoginComponent);
    return LoginComponent;
}());



/***/ }),

/***/ "../../../../../src/app/routes/login/reg/reg.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, ".login-logo {\r\n  height: 31px;\r\n  margin: 16px 24px 16px 0;\r\n}\r\n\r\n.login-layout .login-header {\r\n  background: #ececec;\r\n  height: 76px;\r\n}\r\n\r\n.login-layout .login-center {\r\n  height: 558px;\r\n  padding: 0;\r\n  background: #fff;\r\n}\r\n\r\n.login-layout .login-footer {\r\n  text-align: center;\r\n  padding-top: 60px;\r\n}\r\n\r\n.login-layout .login-center .login-center-banner {\r\n  width: 100%;\r\n  height: 558px;\r\n}\r\n\r\n.login-layout .login-center .login-center-banner img {\r\n  width: 100%;\r\n  height: 100%;\r\n}\r\n\r\n.login-form-button {\r\n  height: 44px;\r\n  width: 80%;\r\n  margin-left: 9%;\r\n  color: white;\r\n}\r\n.ant-btn-primary:hover{\r\n  color: white;\r\n}\r\n.ant-btn-primary:focus{\r\n  color: white;\r\n}\r\n.login-form {\r\n  width: 22%;\r\n  height: 472px;\r\n  background: #ffffff;\r\n  position: absolute;\r\n  top: 120px;\r\n  right: 20%;\r\n}\r\n\r\n.login-form .login-form-information {\r\n  width: 80%;\r\n  height: 44px;\r\n  margin-left: 9%;\r\n}\r\n\r\n.headings h1 {\r\n  height: 100px;\r\n  line-height: 100px;\r\n  text-align: center;\r\n  color: #e82d4d;\r\n}\r\n\r\n.reg-info{\r\n  width: 350px;\r\n  margin: 0 auto;\r\n  padding-top: 20px;\r\n}\r\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/routes/login/reg/reg.component.html":
/***/ (function(module, exports) {

module.exports = "<nz-layout class=\"login-layout\">\r\n  <!--登录页的顶部start-->\r\n  <nz-header class=\"login-header\">\r\n    <!--登录页logo start-->\r\n    <div class=\"login-logo\">\r\n      <img [src]=\"app.logoDark\">\r\n    </div>\r\n    <!--登录页logo end-->\r\n  </nz-header>\r\n  <!--登录页的顶部end-->\r\n  <!--注册中心 start-->\r\n  <nz-content>\r\n    <div class=\"login-center\" style=\"padding: 0;\">\r\n      <div class=\"reg-info\">\r\n        <div class=\"mt-20\">\r\n          <nz-input [nzPlaceHolder]=\"'登录账户'\" required [(ngModel)]=\"account\" [nzSize]=\"'large'\">\r\n          </nz-input>\r\n        </div>\r\n        <div class=\"mt-20\">\r\n          <nz-input [nzPlaceHolder]=\"'登录密码'\" required [(ngModel)]=\"pwd\" [nzType]=\"'password'\" [nzSize]=\"'large'\">\r\n          </nz-input>\r\n        </div>\r\n        <div class=\"mt-20 fr\">\r\n          <button nz-button [nzType]=\"'default'\" [routerLink]=\"loginUrl\">\r\n            <span>去登录</span>\r\n          </button>\r\n          <button nz-button [nzType]=\"'primary'\" (click)=\"reg()\">\r\n            <span>提交注册</span>\r\n          </button>\r\n        </div>\r\n      </div>\r\n    </div>\r\n  </nz-content>\r\n  <!--注册中心 end-->\r\n  <!--登录页底部 start-->\r\n  <nz-footer class=\"login-footer\">{{app.copyright}}</nz-footer>\r\n  <!--登录页底 部 end-->\r\n</nz-layout>\r\n"

/***/ }),

/***/ "../../../../../src/app/routes/login/reg/reg.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return RegComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__public_setting_setting__ = __webpack_require__("../../../../../src/app/public/setting/setting.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_ng_zorro_antd__ = __webpack_require__("../../../../ng-zorro-antd/esm5/antd.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__login_service__ = __webpack_require__("../../../../../src/app/routes/login/login.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__public_setting_setting_url__ = __webpack_require__("../../../../../src/app/public/setting/setting_url.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};






var RegComponent = (function () {
    function RegComponent(_notification, loginService, router) {
        this._notification = _notification;
        this.loginService = loginService;
        this.router = router;
        this.app = __WEBPACK_IMPORTED_MODULE_1__public_setting_setting__["a" /* Setting */].APP; //平台基本信息
        this.loginUrl = __WEBPACK_IMPORTED_MODULE_4__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.login.login; //登录
    }
    RegComponent.prototype.ngOnInit = function () {
    };
    /**
     * 注册
     * @returns {boolean}
     */
    RegComponent.prototype.reg = function () {
        var me = this;
        if (!this.account) {
            me._notification.error('警告', "用户名必填！");
            return false;
        }
        if (!this.pwd) {
            me._notification.error('警告', "密码必填！");
            return false;
        }
        var ret = me.loginService.reg(this.account, this.pwd);
        if (ret.success) {
            me._notification.success('成功', "注册成功，请登录！");
            this.router.navigate([__WEBPACK_IMPORTED_MODULE_4__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.login.login]); //路由跳转（去登录页面）
        }
        else {
            me._notification.error('警告', ret.info);
        }
    };
    RegComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["n" /* Component */])({
            selector: 'app-reg',
            template: __webpack_require__("../../../../../src/app/routes/login/reg/reg.component.html"),
            styles: [__webpack_require__("../../../../../src/app/routes/login/reg/reg.component.css")]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_2_ng_zorro_antd__["c" /* NzNotificationService */], __WEBPACK_IMPORTED_MODULE_3__login_service__["a" /* LoginService */], __WEBPACK_IMPORTED_MODULE_5__angular_router__["b" /* Router */]])
    ], RegComponent);
    return RegComponent;
}());



/***/ })

});
//# sourceMappingURL=login.module.chunk.js.map