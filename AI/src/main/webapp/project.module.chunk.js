webpackJsonp(["project.module"],{

/***/ "../../../../../src/app/public/pipes/splice-str.pipe.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return SpliceStrPipe; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var SpliceStrPipe = (function () {
    function SpliceStrPipe() {
    }
    SpliceStrPipe.prototype.transform = function (str, args) {
        if (!str)
            return;
        var strr = str.slice(0, 10);
        return strr;
    };
    SpliceStrPipe = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["U" /* Pipe */])({
            name: 'spliceStr'
        })
    ], SpliceStrPipe);
    return SpliceStrPipe;
}());



/***/ }),

/***/ "../../../../../src/app/routes/project/detail/detail.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "#detail{\r\n    background: #fff;\r\n    padding: 20px;\r\n    border-radius: 5px;\r\n}\r\n.back{\r\n    text-align: right;\r\n}\r\n#detail>div{\r\n    margin-bottom: 20px;\r\n}\r\n#detail .info_title{\r\n    font-size: 18px;\r\n    font-weight: bold;\r\n    background: #d9d9d9;\r\n    border-radius: 5px;\r\n    padding: 5px 15px;\r\n    margin-bottom: 15px;\r\n}\r\n#detail .ant-row{\r\n    padding: 5px 15px;\r\n}\r\n#detail ngx-monaco-editor{\r\n    width: 100%;\r\n    height: 800px;\r\n    margin-top: 15px;\r\n}\r\n#detail .info_title nz-tag{\r\n    float: right;\r\n}\r\n.creat_code{\r\n    text-align: center\r\n}\r\n.myLink{\r\n  text-decoration: underline;\r\n}\r\n.myGreen{\r\n  color:#06805E;\r\n}\r\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/routes/project/detail/detail.component.html":
/***/ (function(module, exports) {

module.exports = "<div id=\"detail\">\r\n  <div class=\"back\">\r\n    <button nz-button [nzType]=\"'primary'\" (click)=\"goBack()\">返回</button>\r\n  </div>\r\n  <div class=\"info\">\r\n    <div class=\"info_title\">项目信息\r\n      <nz-tag [nzColor]=\"'green'\" (click)=\"skipTo(0,'edit')\">已配置</nz-tag>\r\n    </div>\r\n    <div nz-row>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"6\">项目名：</div>\r\n        <div nz-col [nzSpan]=\"18\">{{projectData.name}}</div>\r\n      </div>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"6\">项目英文名：</div>\r\n        <div nz-col [nzSpan]=\"18\">{{projectData.englishName}}</div>\r\n      </div>\r\n    </div>\r\n    <div nz-row>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"6\">数据库类型：</div>\r\n        <div nz-col [nzSpan]=\"18\">{{projectData.databaseType}}</div>\r\n      </div>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"6\"> 项目版权信息：</div>\r\n        <div nz-col [nzSpan]=\"18\">{{projectData.copyright}}</div>\r\n      </div>\r\n    </div>\r\n    <div nz-row>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"6\">作者：</div>\r\n        <div nz-col [nzSpan]=\"18\">{{projectData.author}}</div>\r\n      </div>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"6\">作者联系方式：</div>\r\n        <div nz-col [nzSpan]=\"18\">{{projectData.phone}}</div>\r\n      </div>\r\n    </div>\r\n    <div nz-row>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"6\">项目描述：</div>\r\n        <div nz-col [nzSpan]=\"18\">{{projectData.description}}</div>\r\n      </div>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"6\">SQL类型：</div>\r\n        <div nz-col [nzSpan]=\"18\">\r\n          <nz-tag>{{projectData.databaseType}}</nz-tag>\r\n        </div>\r\n      </div>\r\n    </div>\r\n  </div>\r\n  <div class=\"sql_info\">\r\n    <div class=\"info_title\">SQL信息\r\n      <nz-tag [nzColor]=\"projectData.projectSqlList?.length?'green':'red'\"  (click)=\"skipTo(1,this.projectData.projectSqlList?.length?'edit':'add')\">{{projectData.projectSqlList?.length?'已配置':'待配置'}}</nz-tag>\r\n    </div>\r\n    <ngx-monaco-editor [options]=\"editorOptions\" [(ngModel)]=\"code\"></ngx-monaco-editor>\r\n  </div>\r\n  <div class=\"technology_info\">\r\n    <div class=\"info_title\">技术信息\r\n      <nz-tag [nzColor]=\"projectData.projectFramworkList?.length?'green':'red'\" (click)=\"skipTo(2,this.projectData.projectFramworkList?.length?'edit':'add')\">{{projectData.projectFramworkList?.length?'已配置':'待配置'}}</nz-tag>\r\n    </div>\r\n    <div nz-row *ngFor=\"let item of framslist;let i=index\">\r\n      <div nz-col [nzSpan]=\"2\">\r\n        <span *ngIf=\"i==0\">所选技术：</span>\r\n      </div>\r\n      <div nz-col [nzSpan]=\"10\">\r\n        <nz-tag>{{item.frameworks?.name}}</nz-tag>\r\n      </div>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <span>{{item.frameworks?.description}}</span>\r\n        <!--<nz-tag class=\"w100\"></nz-tag>-->\r\n      </div>\r\n    </div>\r\n  </div>\r\n  <div class=\"warehouse_info\">\r\n    <div class=\"info_title\">仓库信息\r\n      <nz-tag [nzColor]=\"projectData.projectRepositoryAccountList?.length?'green':'red'\" (click)=\"skipTo(3,this.projectData.projectRepositoryAccountList?.length?'edit':'add')\">{{projectData.projectRepositoryAccountList?.length?'已配置':'待配置'}}</nz-tag>\r\n    </div>\r\n    <div nz-row>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"4\">\r\n          仓库类型：\r\n        </div>\r\n        <div nz-col [nzSpan]=\"20\">\r\n          {{repositoryInfo?.type}}\r\n        </div>\r\n      </div>\r\n      <div nz-col [nzSpan]=\"12\">\r\n        <div nz-col [nzSpan]=\"4\">\r\n          仓库账号：\r\n        </div>\r\n        <div nz-col [nzSpan]=\"20\">\r\n          {{repositoryInfo?.account}}\r\n        </div>\r\n      </div>\r\n    </div>\r\n    <div nz-row>\r\n      <div nz-col [nzSpan]=\"2\">\r\n        仓库地址：\r\n      </div>\r\n      <div nz-col [nzSpan]=\"22\">\r\n        <a [href]=\"repositoryInfo?.home\" target=\"_blank\" class=\"myLink\" title=\"仓库地址\">{{repositoryInfo?.home}}</a>\r\n      </div>\r\n    </div>\r\n    <div nz-row>\r\n      <div nz-col [nzSpan]=\"2\">\r\n        仓库描述：\r\n      </div>\r\n      <div nz-col [nzSpan]=\"22\">\r\n        {{repositoryInfo?.description}}\r\n      </div>\r\n    </div>\r\n  </div>\r\n  <div class=\"warehouse_info\" *ngIf=\"jobList.length>0\">\r\n    <div class=\"info_title\">下载项目</div>\r\n    <div nz-row>\r\n      <div nz-col [nzSpan]=\"24\">\r\n        <div nz-col [nzSpan]=\"2\">\r\n          项目源码：\r\n        </div>\r\n        <div nz-col [nzSpan]=\"2\">\r\n          <a href=\"{{projectData.downloadUrl}}\" target=\"_blank\" class=\"underline\" title=\"下载源码\">下载源码</a>\r\n        </div>\r\n        <div nz-col [nzSpan]=\"2\">\r\n          <a [routerLink]=\"'/main/project/source'\"  class=\"underline\" title=\"查看源码\" [queryParams]=\"{code:projectCode,filePath:projectData?.englishName}\">查看源码</a>\r\n        </div>\r\n      </div>\r\n    </div>\r\n  </div>\r\n  <div class=\"history\">\r\n    <div class=\"info_title\">构建历史</div>\r\n    <nz-table #nzTable [nzDataSource]=\"jobList\" [nzPageSize]=\"10\">\r\n      <thead nz-thead>\r\n      <tr>\r\n        <th nz-th>\r\n          <span>序号</span>\r\n        </th>\r\n        <th nz-th>\r\n          <span>时间</span>\r\n        </th>\r\n        <th nz-th>\r\n          <span>状态</span>\r\n        </th>\r\n        <th nz-th>\r\n          <span>操作</span>\r\n        </th>\r\n      </tr>\r\n      </thead>\r\n      <tbody nz-tbody>\r\n      <tr nz-tbody-tr *ngFor=\"let item of nzTable.data; let index = index\">\r\n        <td nz-td>{{index+1}}</td>\r\n        <td nz-td>{{item.createTime}}</td>\r\n        <td nz-td>\r\n          <nz-badge [nzStatus]=\"'success'\" *ngIf=\"item.state=='Completed'\" [nzText]=\"item.state\"></nz-badge>\r\n          <nz-badge [nzStatus]=\"'error'\" *ngIf=\"item.state=='Error'\" [nzText]=\"item.state\"></nz-badge>\r\n          <nz-badge [nzStatus]=\"'warning'\" *ngIf=\"item.state=='Waring'\" [nzText]=\"item.state\"></nz-badge>\r\n          <nz-badge [nzStatus]=\"'processing'\" *ngIf=\"item.state=='Create'\" [nzText]=\"item.state\"></nz-badge>\r\n          <nz-badge [nzStatus]=\"'processing'\" *ngIf=\"item.state=='Executing'\" [nzText]=\"item.state\"></nz-badge>\r\n        </td>\r\n        <td nz-td>\r\n          <button nz-button [nzType]=\"'primary'\" (click)=linkLogs(item.code,item.state)>\r\n            <span>查看日志</span>\r\n          </button>\r\n        </td>\r\n      </tr>\r\n      </tbody>\r\n    </nz-table>\r\n  </div>\r\n  <div class=\"creat_code\">\r\n    <button nz-button [nzSize]=\"'large'\" [nzType]=\"'primary'\" (click)=\"buildTask()\">构建项目</button>\r\n  </div>\r\n</div>\r\n"

/***/ }),

/***/ "../../../../../src/app/routes/project/detail/detail.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return DetailComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__project_service__ = __webpack_require__("../../../../../src/app/routes/project/project.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__public_setting_setting_url__ = __webpack_require__("../../../../../src/app/public/setting/setting_url.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};




var DetailComponent = (function () {
    function DetailComponent(project, route, router) {
        this.project = project;
        this.route = route;
        this.router = router;
        this.editorOptions = {
            theme: 'vs-dark',
            language: 'sql',
            scrollBeyondLastLine: false,
            readOnly: true,
        };
        this.projectData = {};
        this.jobList = new Array(); //构建历史
    }
    DetailComponent.prototype.ngOnInit = function () {
        var me = this;
        me.projectCode = me.route.snapshot.queryParams['code'];
        $.when(me.project.getDetail(me.projectCode)).always(function (data) {
            me.projectData = data;
            sessionStorage.setItem('projectName', me.projectData.name);
            sessionStorage.setItem('description', me.projectData.description);
            me.jobList = data.projectJobList;
            me.code = me.enable(data.projectSqlList).tsql;
            me.framslist = data.projectFramworkList;
            me.repositoryInfo = me.enable(data.projectRepositoryAccountList);
        });
    };
    /**
     * 过滤出能够使用的
     */
    DetailComponent.prototype.enable = function (data) {
        for (var i = 0; i < data.length; i++) {
            if (data[i].state == 'Enable') {
                return data[i];
            }
        }
    };
    /**
     * 跳转到任务列表页面
     */
    DetailComponent.prototype.goBack = function () {
        this.router.navigate(['/main/project']);
    };
    /**
     * 跳转页面
     * @param step 跳转到的哪步
     * @param type 新增还是修改
     */
    DetailComponent.prototype.skipTo = function (step, type) {
        this.project.routerSkip(step, type, this.projectCode);
    };
    /**
     * 生成任务
     */
    DetailComponent.prototype.buildTask = function () {
        var me = this;
        me.excuteTask();
    };
    /**
     * 跳转到日志页面
     */
    DetailComponent.prototype.linkLogs = function (code, state) {
        var me = this;
        me.router.navigate([__WEBPACK_IMPORTED_MODULE_3__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.project.logs], { 'queryParams': { 'taskCode': code, 'state': state, 'home': me.repositoryInfo.home } });
    };
    /**
     * 执行任务
     */
    DetailComponent.prototype.excuteTask = function () {
        var me = this;
        me.router.navigate([__WEBPACK_IMPORTED_MODULE_3__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.project.logs], { 'queryParams': { 'code': me.projectCode, 'home': me.repositoryInfo.home } });
    };
    DetailComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["n" /* Component */])({
            selector: 'app-detail',
            template: __webpack_require__("../../../../../src/app/routes/project/detail/detail.component.html"),
            styles: [__webpack_require__("../../../../../src/app/routes/project/detail/detail.component.css")]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_2__project_service__["a" /* ProjectService */],
            __WEBPACK_IMPORTED_MODULE_1__angular_router__["a" /* ActivatedRoute */],
            __WEBPACK_IMPORTED_MODULE_1__angular_router__["b" /* Router */]])
    ], DetailComponent);
    return DetailComponent;
}());



/***/ }),

/***/ "../../../../../src/app/routes/project/list/list.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "#list{\r\n    padding-left: 7.5px;\r\n    padding-right: 7.5px;\r\n}\r\n.project{\r\n    margin-bottom: 15px;\r\n}\r\n.project_name{\r\n    padding-bottom: 10px;\r\n    margin-bottom: 10px;\r\n    border-bottom: 1px solid #49a9ee;\r\n}\r\n.project_zh{\r\n    font-size: 18px;\r\n    font-weight: 500;\r\n    overflow: hidden;\r\n    white-space: nowrap;\r\n    text-overflow: ellipsis;\r\n}\r\n.project_en{\r\n    overflow: hidden;\r\n    white-space: nowrap;\r\n    text-overflow: ellipsis;\r\n}\r\n.project_summary{\r\n    height: 54px;\r\n    overflow: hidden;\r\n}\r\n.project_time{\r\n    margin-top: 10px;\r\n}\r\n.detail{\r\n    float: right;\r\n}\r\n#list .ant-btn{\r\n    color: #49a9ee;\r\n    border-color: #49a9ee;\r\n}\r\n.myBb{\r\n  border-color: #cc1635 !important;\r\n  color: #cc1635 !important;\r\n}\r\n.myCss{\r\n  float:right;\r\n}\r\n.myWidth{\r\n  display: inline-block;\r\n  width:130px !important;\r\n  overflow: hidden;\r\n}\r\n.delCss{\r\n  background: #d4d4d0;\r\n}\r\n.myLan{\r\n  color:#4f91c5 !important;\r\n}\r\n.color-gray{\r\n  color:red;\r\n}\r\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/routes/project/list/list.component.html":
/***/ (function(module, exports) {

module.exports = "<div id=\"list\">\r\n  <div nz-row [nzGutter]=\"15\">\r\n    <div nz-col *ngFor=\"let project of projectList.voList\" [nzSpan]=\"6\" class=\"project\">\r\n      <nz-card [class.delCss]=\"project.state=='Delete'\">\r\n        <ng-template #body>\r\n          <div class=\"project_name\">\r\n            <p class=\"project_zh\">\r\n              <span class=\"myWidth\">{{project.name}}</span>\r\n              <span class=\"myCss\">{{project.author}}</span>\r\n            </p>\r\n            <p class=\"project_en\">{{project.englishName}}</p>\r\n          </div>\r\n          <div class=\"project_summary\">{{project.description}}</div>\r\n          <div class=\"project_time\">\r\n            <span class=\"mr-15 myLan\">{{project.language}}</span>\r\n            <nz-tooltip [nzTitle]=\"'创建时间'\" [nzPlacement]=\"'topLeft'\">\r\n              <span nz-tooltip>{{project.createTime | spliceStr}}</span>\r\n            </nz-tooltip>\r\n            <span class=\"ml-20 color-gray\">{{project.buildNumber}}</span>\r\n          </div>\r\n          <button nz-button [nzType]=\"'default'\" [nzSize]=\"'small'\" class=\"detail mt ml\" (click)=\"goDetail(project)\">\r\n            <i class=\"anticon anticon-copy\"></i>\r\n            <span>详情</span>\r\n          </button>\r\n          <nz-popconfirm [nzTitle]=\"'确定要删除这个任务吗？'\"  class=\"detail mt\" (nzOnConfirm)=\"confirm(project)\"  [nzPlacement]=\"'topLeft'\">\r\n            <button nz-popconfirm nz-button  [nzSize]=\"'small'\" class=\"myBb\">删除</button>\r\n          </nz-popconfirm>\r\n        </ng-template>\r\n      </nz-card>\r\n    </div>\r\n  </div>\r\n</div>\r\n\r\n"

/***/ }),

/***/ "../../../../../src/app/routes/project/list/list.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return ListComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__project_service__ = __webpack_require__("../../../../../src/app/routes/project/project.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__public_util_page__ = __webpack_require__("../../../../../src/app/public/util/page.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__public_setting_setting_url__ = __webpack_require__("../../../../../src/app/public/setting/setting_url.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_ng_zorro_antd__ = __webpack_require__("../../../../ng-zorro-antd/esm5/antd.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};






var ListComponent = (function () {
    function ListComponent(project, router, message) {
        var _this = this;
        this.project = project;
        this.router = router;
        this.message = message;
        this.projectList = new __WEBPACK_IMPORTED_MODULE_3__public_util_page__["a" /* Page */]();
        /**
         * 确认
         */
        this.confirm = function (project) {
            var me = _this;
            me.goDel(project);
        };
    }
    ListComponent.prototype.ngOnInit = function () {
        this.queryList();
    };
    /**
     * 查询项目列表
     */
    ListComponent.prototype.queryList = function () {
        var _this = this;
        $.when(this.project.getProjectList()).always(function (data) {
            _this.projectList = data;
        });
    };
    /**
     * 跳转至项目详情
     * @param param0 项目编号
     */
    ListComponent.prototype.goDetail = function (_a) {
        var code = _a.code;
        this.router.navigate([__WEBPACK_IMPORTED_MODULE_4__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.project.detail], { queryParams: { code: code } });
    };
    /**
     * 删除项目
     * @param project
     */
    ListComponent.prototype.goDel = function (project) {
        var me = this;
        var data = {
            code: project.code
        };
        $.when(this.project.delPro(data)).always(function (data) {
            me.queryList();
        });
    };
    ListComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["n" /* Component */])({
            selector: 'app-list',
            template: __webpack_require__("../../../../../src/app/routes/project/list/list.component.html"),
            styles: [__webpack_require__("../../../../../src/app/routes/project/list/list.component.css")]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__project_service__["a" /* ProjectService */],
            __WEBPACK_IMPORTED_MODULE_2__angular_router__["b" /* Router */],
            __WEBPACK_IMPORTED_MODULE_5_ng_zorro_antd__["b" /* NzMessageService */]])
    ], ListComponent);
    return ListComponent;
}());



/***/ }),

/***/ "../../../../../src/app/routes/project/log-page/log-page.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "#log{\r\n  background: #fff;\r\n  min-height:800px;\r\n  border-radius: 5px;\r\n}\r\n#log ngx-monaco-editor{\r\n  width: 100%;\r\n  height: 800px;\r\n  margin-top: 15px;\r\n}\r\n#log .info_title{\r\n  font-size: 18px;\r\n  font-weight: bold;\r\n  border-radius: 5px;\r\n  padding: 5px 15px;\r\n  margin-bottom: 15px;\r\n  text-align: left;\r\n}\r\n#log .bg{\r\n  background: #d9d9d9;\r\n  clear: both;\r\n}\r\n.text-right{\r\n  text-align: right;\r\n}\r\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/routes/project/log-page/log-page.component.html":
/***/ (function(module, exports) {

module.exports = "<div id=\"log\" class=\"p-20\">\r\n  <div class=\"bg\">\r\n    <div class=\"info_title\">构建日志信息\r\n      <button nz-button [nzType]=\"'primary'\" class=\"fr\" (click)=\"goBack()\">返回</button>\r\n    </div>\r\n  </div>\r\n  <div *ngFor=\"let item of logInfo\">\r\n    <div *ngFor=\"let itemObj of item\">\r\n      <p [innerHtml]=\"itemObj.log\"></p>\r\n    </div>\r\n  </div>\r\n</div>\r\n"

/***/ }),

/***/ "../../../../../src/app/routes/project/log-page/log-page.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return LogPageComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__project_service__ = __webpack_require__("../../../../../src/app/routes/project/project.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__angular_common__ = __webpack_require__("../../../common/esm5/common.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};




var LogPageComponent = (function () {
    function LogPageComponent(projectService, router, location, routerInfo) {
        this.projectService = projectService;
        this.router = router;
        this.location = location;
        this.routerInfo = routerInfo;
        this.logInfo = new Array(); //日志信息
    }
    LogPageComponent.prototype.ngOnInit = function () {
        var me = this;
        me.code = me.routerInfo.snapshot.queryParams['code'];
        me.taskCode = me.routerInfo.snapshot.queryParams['taskCode'];
        me.home = me.routerInfo.snapshot.queryParams['home'];
        me.state = me.routerInfo.snapshot.queryParams['state'];
        me.getLog();
    };
    /**
     * 组件销毁的时候执行的发方法
     */
    LogPageComponent.prototype.ngOnDestroy = function () {
        var me = this;
        window.clearInterval(me.timer);
    };
    /**
     * 执行任务生成任务编码，如果构建历史跳转过来的话,就有任务编码直接调用打印日志的方法
     */
    LogPageComponent.prototype.getLog = function () {
        var me = this;
        if (me.taskCode) {
            me.continueRequest(me.taskCode);
            return;
        }
        var data = {
            code: this.code
        };
        $.when(this.projectService.excuteTask(data)).always(function (result) {
            if (result) {
                var taskCode = result.code;
                me.continueRequest(taskCode);
            }
        });
    };
    /**
     * 持续的请求生成日志
     * @param taskCode
     */
    LogPageComponent.prototype.continueRequest = function (taskCode) {
        var me = this;
        if (me.state) {
            var size = void 0;
            if (me.state == 'Completed' || me.state == 'Error' || me.state == 'Waring') {
                var size_1 = '999';
                var init = 0;
                var data = {
                    curPage: ++init,
                    pageSize: size_1,
                    code: taskCode,
                };
                $.when(me.projectService.getLogsList(data)).always(function (data) {
                    sessionStorage.setItem('code', me.code);
                    me.logInfo.push(data.voList);
                });
            }
            else if (me.state == 'Create' || me.state == 'Executing') {
                me.timer = setInterval(function () {
                    var size = '20';
                    var init = 0;
                    var data = {
                        curPage: ++init,
                        pageSize: init == 1 ? size : 3,
                        code: taskCode,
                    };
                    $.when(me.projectService.getLogsList(data)).always(function (data) {
                        sessionStorage.setItem('code', me.code);
                        me.logInfo.push(data.voList);
                        me.linkHome(data.voList);
                    });
                }, 2000);
            }
        }
        else {
            var init_1 = 0;
            me.timer = setInterval(function () {
                var data = {
                    curPage: ++init_1,
                    pageSize: sessionStorage.getItem('code') == me.code ? init_1 == 1 ? 20 : 3 : 3,
                    code: taskCode,
                };
                $.when(me.projectService.getLogsList(data)).always(function (data) {
                    sessionStorage.setItem('code', me.code);
                    me.logInfo.push(data.voList);
                    me.linkHome(data.voList);
                });
            }, 2000);
        }
    };
    /**
     * 判断是否结束跳转到仓库页面
     */
    LogPageComponent.prototype.linkHome = function (data) {
        var me = this;
        for (var i = 0; i < data.length; i++) {
            if (data[i].log == 'End') {
                window.open(me.home);
                window.clearInterval(me.timer);
            }
        }
    };
    /**
     * 返回
     */
    LogPageComponent.prototype.goBack = function () {
        this.location.back();
    };
    LogPageComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["n" /* Component */])({
            selector: 'app-log-page',
            template: __webpack_require__("../../../../../src/app/routes/project/log-page/log-page.component.html"),
            styles: [__webpack_require__("../../../../../src/app/routes/project/log-page/log-page.component.css")]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__project_service__["a" /* ProjectService */],
            __WEBPACK_IMPORTED_MODULE_2__angular_router__["b" /* Router */],
            __WEBPACK_IMPORTED_MODULE_3__angular_common__["f" /* Location */],
            __WEBPACK_IMPORTED_MODULE_2__angular_router__["a" /* ActivatedRoute */]])
    ], LogPageComponent);
    return LogPageComponent;
}());



/***/ }),

/***/ "../../../../../src/app/routes/project/project.module.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProjectModule", function() { return ProjectModule; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__list_list_component__ = __webpack_require__("../../../../../src/app/routes/project/list/list.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__shared_shared_module__ = __webpack_require__("../../../../../src/app/shared/shared.module.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__detail_detail_component__ = __webpack_require__("../../../../../src/app/routes/project/detail/detail.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__project_service__ = __webpack_require__("../../../../../src/app/routes/project/project.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__public_pipes_splice_str_pipe__ = __webpack_require__("../../../../../src/app/public/pipes/splice-str.pipe.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7__log_page_log_page_component__ = __webpack_require__("../../../../../src/app/routes/project/log-page/log-page.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8__source_code_source_code_component__ = __webpack_require__("../../../../../src/app/routes/project/source-code/source-code.component.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};









var routes = [
    { path: '', component: __WEBPACK_IMPORTED_MODULE_1__list_list_component__["a" /* ListComponent */] },
    { path: 'detail', component: __WEBPACK_IMPORTED_MODULE_4__detail_detail_component__["a" /* DetailComponent */] },
    { path: 'logs', component: __WEBPACK_IMPORTED_MODULE_7__log_page_log_page_component__["a" /* LogPageComponent */] },
    { path: 'source', component: __WEBPACK_IMPORTED_MODULE_8__source_code_source_code_component__["a" /* SourceCodeComponent */] },
];
var ProjectModule = (function () {
    function ProjectModule() {
    }
    ProjectModule = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["J" /* NgModule */])({
            imports: [
                __WEBPACK_IMPORTED_MODULE_3__shared_shared_module__["a" /* SharedModule */],
                __WEBPACK_IMPORTED_MODULE_2__angular_router__["c" /* RouterModule */].forChild(routes)
            ],
            providers: [__WEBPACK_IMPORTED_MODULE_5__project_service__["a" /* ProjectService */]],
            declarations: [
                __WEBPACK_IMPORTED_MODULE_1__list_list_component__["a" /* ListComponent */],
                __WEBPACK_IMPORTED_MODULE_4__detail_detail_component__["a" /* DetailComponent */],
                __WEBPACK_IMPORTED_MODULE_6__public_pipes_splice_str_pipe__["a" /* SpliceStrPipe */],
                __WEBPACK_IMPORTED_MODULE_7__log_page_log_page_component__["a" /* LogPageComponent */],
                __WEBPACK_IMPORTED_MODULE_7__log_page_log_page_component__["a" /* LogPageComponent */],
                __WEBPACK_IMPORTED_MODULE_8__source_code_source_code_component__["a" /* SourceCodeComponent */]
            ]
        })
    ], ProjectModule);
    return ProjectModule;
}());



/***/ }),

/***/ "../../../../../src/app/routes/project/project.service.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return ProjectService; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__ = __webpack_require__("../../../../../src/app/public/service/ajax.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__ = __webpack_require__("../../../../../src/app/public/setting/setting_url.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_ng_zorro_antd__ = __webpack_require__("../../../../ng-zorro-antd/esm5/antd.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};





var ProjectService = (function () {
    function ProjectService(_notification, router) {
        this._notification = _notification;
        this.router = router;
    }
    /**
     * 获取项目列表
     * @param curPage   当前页
     * @param pageSize  分页大小
     */
    ProjectService.prototype.getProjectList = function (curPage, pageSize) {
        if (curPage === void 0) { curPage = 1; }
        if (pageSize === void 0) { pageSize = 12; }
        var me = this, defer = $.Deferred(); // 封装异步请求结果
        __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__["a" /* AjaxService */].get({
            url: __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].URL.projectCtrl.list,
            data: { curPage: curPage, pageSize: pageSize },
            success: function (res) {
                if (res.success) {
                    defer.resolve(res.data);
                }
                else {
                    me._notification.error("\u51FA\u9519\u4E86", res.info);
                }
            },
            error: function () {
                me._notification.error("\u51FA\u9519\u4E86", '失败，请稍后重试');
            }
        });
        return defer.promise();
    };
    /**
     * 删除项目
     * requestDate: any 传递的数据
     */
    ProjectService.prototype.delPro = function (requestDate) {
        var me = this, defer = $.Deferred(); //封装异步请求结果
        __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__["a" /* AjaxService */].del({
            url: __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].URL.projectCtrl.delete,
            data: requestDate,
            success: function (res) {
                if (res.success) {
                    me._notification.success("\u6210\u529F\u4E86", res.info);
                    defer.resolve(res.data);
                }
                else {
                    me._notification.error("\u51FA\u9519\u4E86", res.info);
                }
            },
            error: function () {
                me._notification.error("\u51FA\u9519\u4E86", '失败，请稍后重试');
            }
        });
        return defer.promise();
    };
    /**
     * 得到日志列表
     * requestDate: any 传递的数据
     */
    ProjectService.prototype.getLogsList = function (requestDate) {
        var me = this, defer = $.Deferred(); //封装异步请求结果
        __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__["a" /* AjaxService */].get({
            url: __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].URL.projectJobLogsCtrl.logs,
            data: requestDate,
            success: function (res) {
                if (res.success) {
                    defer.resolve(res.data);
                }
                else {
                    me._notification.error("\u51FA\u9519\u4E86", res.info);
                }
            },
            error: function () {
                me._notification.error("\u51FA\u9519\u4E86", '失败，请稍后重试');
            }
        });
        return defer.promise();
    };
    /**
     * 创建任务
     * requestDate: any 传递的数据
     */
    ProjectService.prototype.excuteTask = function (requestDate) {
        var me = this, defer = $.Deferred(); //封装异步请求结果
        __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__["a" /* AjaxService */].get({
            url: __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].URL.projectJobCtrl.execute,
            data: requestDate,
            success: function (res) {
                if (res.success) {
                    me._notification.success("\u6210\u529F\u4E86", res.info);
                    defer.resolve(res.data);
                }
                else {
                    me._notification.error("\u51FA\u9519\u4E86", res.info);
                }
            },
            error: function () {
                me._notification.error("\u51FA\u9519\u4E86", '失败，请稍后重试');
            }
        });
        return defer.promise();
    };
    /**
     * 查询项目详情
     * @param code 项目编号
     */
    ProjectService.prototype.getDetail = function (code) {
        var me = this, defer = $.Deferred(); // 封装异步请求结果
        __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__["a" /* AjaxService */].get({
            url: __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].URL.projectCtrl.load,
            data: { code: code },
            success: function (res) {
                if (res.success) {
                    defer.resolve(res.data);
                }
                else {
                    me._notification.error("\u51FA\u9519\u4E86", res.info);
                }
            },
            error: function () {
                me._notification.error("\u51FA\u9519\u4E86", '失败，请稍后重试');
            }
        });
        return defer.promise();
    };
    /**
     * 根据操作步骤跳到相应页面
     * @param current （当前步骤）
     */
    ProjectService.prototype.routerSkip = function (current, type, projectCode) {
        switch (current) {
            case 0:
                this.router.navigate([__WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.buildPro.proInfo], {
                    'queryParams': {
                        'type': type,
                        'projectCode': projectCode
                    }
                });
                break;
            case 1:
                this.router.navigate([__WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.buildPro.proSql], {
                    'queryParams': {
                        'type': type,
                        'projectCode': projectCode
                    }
                });
                break;
            case 2:
                this.router.navigate([__WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.buildPro.proFrames], {
                    'queryParams': {
                        'type': type,
                        'projectCode': projectCode
                    }
                });
                break;
            case 3:
                this.router.navigate([__WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].ROUTERLINK.buildPro.proRepository], {
                    'queryParams': {
                        'type': type,
                        'projectCode': projectCode
                    }
                });
                break;
        }
    };
    /**
     * 查询文件路径
     * requestDate: any 传递的数据
     */
    ProjectService.prototype.getSourceCode = function (requestDate) {
        var me = this, defer = $.Deferred(); //封装异步请求结果
        __WEBPACK_IMPORTED_MODULE_1__public_service_ajax_service__["a" /* AjaxService */].get({
            url: __WEBPACK_IMPORTED_MODULE_2__public_setting_setting_url__["a" /* SettingUrl */].URL.projectCtrl.path,
            data: requestDate,
            success: function (res) {
                if (res.success) {
                    defer.resolve(res.data);
                }
                else {
                    me._notification.error("\u51FA\u9519\u4E86", res.info);
                }
            },
            error: function () {
                me._notification.error("\u51FA\u9519\u4E86", '失败，请稍后重试');
            }
        });
        return defer.promise();
    };
    ProjectService = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["B" /* Injectable */])(),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_3_ng_zorro_antd__["c" /* NzNotificationService */], __WEBPACK_IMPORTED_MODULE_4__angular_router__["b" /* Router */]])
    ], ProjectService);
    return ProjectService;
}());



/***/ }),

/***/ "../../../../../src/app/routes/project/source-code/source-code.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, ".myHeight{\r\n  height:30px;line-height: 30px;\r\n  border-bottom:1px dashed #e4dede;\r\n}\r\n.source{\r\n  background: #fff;\r\n  padding: 20px;\r\n  border-radius: 5px;\r\n  height: 100%;\r\n}\r\n.myClass{\r\n  color:#108ee9;\r\n}\r\n.hand{\r\n  cursor:pointer !important;\r\n}\r\n#myId ngx-monaco-editor{\r\n  width: 100%;\r\n  height: 800px;\r\n}\r\n:host /deep/ .ant-breadcrumb-separator{\r\n  margin: 0px !important;\r\n}\r\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/routes/project/source-code/source-code.component.html":
/***/ (function(module, exports) {

module.exports = "<p class=\"mb font16\">【{{projectName}}】{{description}}</p>\r\n<div class=\"source\">\r\n  <button nz-button [nzType]=\"'primary'\" (click)=\"goBack()\" class=\"fr\">返回</button>\r\n  <nz-breadcrumb [nzSeparator]=\"'/'\" class=\"mt-5 mb-5\">\r\n    <nz-breadcrumb-item *ngFor=\"let item of paths;let i=index;\" class=\"hand\">\r\n      <span (click)=\"slicePaths(item.filePath,item.type,i)\">{{item.filePath |path}}</span>\r\n    </nz-breadcrumb-item>\r\n  </nz-breadcrumb>\r\n  <ng-container *ngIf=\"!sourceCode\">\r\n    <p *ngIf=\"paths.length>1\" (click)=\"back()\" class=\"myHeight\">\r\n      <i class=\"anticon anticon-rollback\"></i>\r\n    </p>\r\n    <div *ngFor=\"let item of filePathData;let i=index;\" class=\"hand\">\r\n      <p (click)=\"addPaths(item.filePath,item.type)\" class=\"myHeight\">\r\n        <i *ngIf=\"item.type=='Directory'\" class=\"anticon anticon-folder myClass\"></i>\r\n        <i *ngIf=\"item.type=='File'\" class=\"anticon anticon-file\"></i>\r\n        <span>{{item.filePath |path}}</span>\r\n      </p>\r\n    </div>\r\n  </ng-container>\r\n  <ng-container *ngIf=\"sourceCode\">\r\n    <div id=\"myId\">\r\n      <ngx-monaco-editor [options]=\"editorOptions\" [(ngModel)]=\"sourceCode\" class=\"mt\"></ngx-monaco-editor>\r\n    </div>\r\n  </ng-container>\r\n</div>\r\n"

/***/ }),

/***/ "../../../../../src/app/routes/project/source-code/source-code.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return SourceCodeComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__project_service__ = __webpack_require__("../../../../../src/app/routes/project/project.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__angular_common__ = __webpack_require__("../../../common/esm5/common.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_util__ = __webpack_require__("../../../../util/util.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_util___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_util__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_ng_zorro_antd__ = __webpack_require__("../../../../ng-zorro-antd/esm5/antd.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};






var SourceCodeComponent = (function () {
    function SourceCodeComponent(routerInfo, location, _notification, router, project) {
        this.routerInfo = routerInfo;
        this.location = location;
        this._notification = _notification;
        this.router = router;
        this.project = project;
        this.paths = new Array(); //路径的集合
        this.editorOptions = {
            theme: 'vs-dark',
            language: 'sql',
            scrollBeyondLastLine: false,
            readOnly: true,
        };
    }
    SourceCodeComponent.prototype.ngOnInit = function () {
        var me = this;
        me.code = me.routerInfo.snapshot.queryParams['code'];
        me.filePath = me.routerInfo.snapshot.queryParams['filePath'];
        me.projectName = sessionStorage.getItem('projectName');
        me.description = sessionStorage.getItem('description');
        var obj = { type: 'Directory', filePath: me.filePath };
        me.paths.push(obj);
        me.getSourceCode();
    };
    /**
     * 获取源码和路径
     */
    SourceCodeComponent.prototype.getSourceCode = function (filePath, type) {
        var me = this;
        var data = {
            code: me.code,
            filePath: filePath || me.filePath
        };
        var index = data.filePath.lastIndexOf('.');
        if (index != -1) {
            var slicePath = data.filePath.slice(index + 1);
            if (slicePath == 'java') {
                me.editorOptions.language = 'java';
            }
            else if (slicePath == 'js') {
                me.editorOptions.language = 'javascript';
            }
            else if (slicePath == 'sql') {
                me.editorOptions.language = 'sql';
            }
            else {
                me.editorOptions.language = 'text/plain';
            }
        }
        $.when(me.project.getSourceCode(data)).always(function (result) {
            if (result) {
                if (type == 'File') {
                    me.sourceCode = result;
                }
                else {
                    me.filePathData = me.resetData(result);
                }
            }
        });
    };
    /**
     * 增加面包屑的路径
     */
    SourceCodeComponent.prototype.addPaths = function (path, type) {
        var me = this;
        var obj = { type: type, filePath: path };
        me.paths.push(obj);
        me.getSourceCode(path, type);
    };
    /**
     * 重组数据（文件夹和文件在一块）
     */
    SourceCodeComponent.prototype.resetData = function (result) {
        var directory = new Array(), file = new Array(), me = this;
        if (Object(__WEBPACK_IMPORTED_MODULE_4_util__["isArray"])(result)) {
            for (var i = 0; i < result.length; i++) {
                if (result[i].type == 'Directory') {
                    directory.push(result[i]);
                }
                else {
                    file.push(result[i]);
                }
            }
            return directory.concat(file);
        }
        else {
            me._notification.info("\u6E29\u99A8\u63D0\u793A", '请先进行构建');
            me.goBack();
        }
    };
    /**
     * 点击导航截取路径并加载信息
     * @param path
     * @param type
     * @param type 下标
     */
    SourceCodeComponent.prototype.slicePaths = function (path, type, i) {
        var me = this;
        me.paths.splice(i + 1);
        if (type != 'File') {
            me.sourceCode = null;
        }
        me.getSourceCode(path, type);
    };
    /**
     * 返回上一级
     */
    SourceCodeComponent.prototype.back = function () {
        var me = this;
        me.paths.pop();
        me.getSourceCode(me.paths[me.paths.length - 1]['filePath']);
    };
    /**
     * 跳转到任务列表页面
     */
    SourceCodeComponent.prototype.goBack = function () {
        var me = this;
        this.router.navigate(['/main/project/detail'], { queryParams: { code: me.code } });
    };
    SourceCodeComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["n" /* Component */])({
            selector: 'app-source-code',
            template: __webpack_require__("../../../../../src/app/routes/project/source-code/source-code.component.html"),
            styles: [__webpack_require__("../../../../../src/app/routes/project/source-code/source-code.component.css")]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__angular_router__["a" /* ActivatedRoute */],
            __WEBPACK_IMPORTED_MODULE_3__angular_common__["f" /* Location */],
            __WEBPACK_IMPORTED_MODULE_5_ng_zorro_antd__["c" /* NzNotificationService */],
            __WEBPACK_IMPORTED_MODULE_1__angular_router__["b" /* Router */],
            __WEBPACK_IMPORTED_MODULE_2__project_service__["a" /* ProjectService */]])
    ], SourceCodeComponent);
    return SourceCodeComponent;
}());



/***/ })

});
//# sourceMappingURL=project.module.chunk.js.map