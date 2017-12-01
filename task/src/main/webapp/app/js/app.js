/*!
 * @author 立坤 整理于 2016.06.15
 * @ramark 全站框架核心控制处，框架技术 - Bootstrap + AngularJS
 * 
 */
if (typeof $ === 'undefined') {
    throw new Error(baseInfo.getInfo("unjquery"));
}

var menuItems =
    [
        {
        "code": "002",
        "icon": "fa fa-clock-o",
        "sref": "app.index",
        "text": "trigger管理"
        },
        {
        "code": "001",
        "icon": "fa fa-file-text-o",
        "sref": "app.job",
        "text": "job管理"
        },
        {
            "code": "003",
            "icon": "fa fa-mobile-phone",
            "sref": "app.appver",
            "text": "app管理"
        }
    ]; //权限集合
var moduleId; //模块id
/******************************** 初始化加载基础数据 begin ************************************************************/
var App = angular.module('angle', [
    'ngRoute',
    'ngAnimate',
    'ngStorage',
    'ngCookies',
    'pascalprecht.translate',
    'ui.bootstrap',
    'ui.router',
    'oc.lazyLoad',
    'cfp.loadingBar',
    'ngSanitize',
    'ngResource',
    'ui.utils'
]);
App.run(["$rootScope", "$state", "$stateParams", '$window', '$templateCache', function ($rootScope, $state, $stateParams, $window, $templateCache) {
    // 设置全局访问
    $rootScope.$state = $state;
    $rootScope.$stateParams = $stateParams;
    $rootScope.$storage = $window.localStorage;

    // 注释禁用模板缓存
    /*$rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
     if (typeof(toState) !== 'undefined'){
     $templateCache.remove(toState.templateUrl);
     }
     });*/

    // 常用局部变量
    $rootScope.app = {
        navbarTitle:"首页",
        name: baseInfo.getInfo("name"),
        description: baseInfo.getInfo("description"),
        phone: baseInfo.getInfo("phone"),
        keyword: baseInfo.getInfo("keyword"),
        year: ((new Date()).getFullYear()),
        layout: {
            isFixed: true,
            isCollapsed: false,
            isBoxed: false,
            isRTL: false,
            horizontal: false,
            isFloat: false,
            asideHover: false,
            theme: null
        },
        useFullLayout: false,
        hiddenFooter: false,
        viewAnimation: 'ng-fadeInUp'
    };
    //判断用户是否登录并引流
    $.ajax({
        type: "POST",
        url: "/login/check.shtml",
        async: false,
        "success": function (data) {
            if (data.success) {
                window.location.href = "#/app/index"; //进入首页
            } else {
                window.location.href = "#/page/login"; //进入登录页面
            }
        }
    });
    //当前登录的管理员信息
    $rootScope.user = {
        welcome: '欢迎您',
        //job: '业务管理系统',
        picture: 'app/img/user/02.png'
    };
}]);
/******************************** 初始化加载基础数据 end **************************************************************/

/******************************** 应用地址及配置（angularJs的路由功能实现） begin *************************************/
App.config(['$stateProvider', '$locationProvider', '$urlRouterProvider', 'RouteHelpersProvider',
    function ($stateProvider, $locationProvider, $urlRouterProvider, helper) {
        'use strict';
        $locationProvider.html5Mode(false); // 是否使用html5模式（true、false）
        $urlRouterProvider.otherwise('/page/login'); //默认进入登录页面
        // 路由分配
        $stateProvider
            .state('page.404', {
                url: '/404',
                title: "未找到页面",
                templateUrl: helper.basepath('404.html'),
            })
            .state('page', {
                url: '/page',
                templateUrl: helper.basepath('sys/page.html'),
                resolve: helper.resolveFor('modernizr', 'icons','jqDock', 'screenfull'),
                controller: ["$rootScope", function ($rootScope) {
                    $rootScope.app.layout.isBoxed = false;
                }]
            })
            .state('page.lock', {
                url: '/lock',
                title: "修改密码",
                templateUrl: helper.basepath('sys/lock.html'),
                resolve: helper.resolveFor("lockJs")
            })
            .state('page.login', {
                url: '/login',
                title: "管理员登录",
                templateUrl: helper.basepath('sys/login.html'),
                resolve: helper.resolveFor("loginJs")
            })
            .state('app', {
                url: '/app',
                abstract: true,
                templateUrl: helper.basepath('app.html'),
                controller: 'AppController',
                resolve: helper.resolveFor('modernizr','jqDock', 'screenfull', 'icons')
            })
            .state('app.index', {
                url: '/index',
                title: '任务首页',
                templateUrl: helper.basepath('index/index.html'),
                resolve: helper.resolveFor("ngDialog", 'datatables', "triggerJs", 'ngWig')
            })
            .state('app.index.add', {
                url: '/add',
                title: '添加任务',
                templateUrl: helper.basepath('index/index_add.html')
            })
            .state('app.index.update', {
                url: '/update',
                title: '修改任务',
                templateUrl: helper.basepath('index/index_update.html')
            })
            .state('app.job', {
                url: '/job',
                title: '作业首页',
                templateUrl: helper.basepath('index/job.html'),
                resolve: helper.resolveFor("ngDialog", 'datatables', "jobJs", 'ngWig')
            })
            .state('app.job.view', {
                url: '/view',
                title: '作业详情',
                templateUrl: helper.basepath('index/job_view.html')
            })
            .state('app.appver', {
                url: '/appver',
                title: 'app管理首页',
                templateUrl: helper.basepath('appver/index.html'),
                resolve: helper.resolveFor("ngDialog", 'datatables', "appverJs", 'ngWig')
            })
            .state('app.appver.add', {
                url: '/add',
                title: '添加app',
                templateUrl: helper.basepath('appver/index_add.html'),
                resolve: helper.resolveFor('angularFileUpload', 'filestyle')
            })
    }]).config(['$ocLazyLoadProvider', 'APP_REQUIRES', function ($ocLazyLoadProvider, APP_REQUIRES) {
    'use strict';
    // 延迟加载设置
    $ocLazyLoadProvider.config({
        debug: false,
        events: true,
        modules: APP_REQUIRES.modules
    });
}]).config(['$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
    function ($controllerProvider, $compileProvider, $filterProvider, $provide) {
        'use strict';
        // 引导注册组件
        App.controller = $controllerProvider.register;
        App.directive = $compileProvider.directive;
        App.filter = $filterProvider.register;
        App.factory = $provide.factory;
        App.service = $provide.service;
        App.constant = $provide.constant;
        App.value = $provide.value;
    }]).config(['$translateProvider', function ($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: 'app/i18n/',
        suffix: '.json'
    });
    $translateProvider.preferredLanguage('ch');
    $translateProvider.useLocalStorage();
    $translateProvider.usePostCompiling(true);

}]).config(['cfpLoadingBarProvider', function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeBar = true;
    cfpLoadingBarProvider.includeSpinner = false;
    cfpLoadingBarProvider.latencyThreshold = 500;
    cfpLoadingBarProvider.parentSelector = '.wrapper > section';
}]).config(['$tooltipProvider', function ($tooltipProvider) {
    $tooltipProvider.options({appendToBody: true});
}]);
/******************************** 应用地址及配置（angularJs的路由功能实现） end ***************************************/


/******************************** 设置应用程序的常量begin *************************************************************/
App.constant('APP_COLORS', {
        'primary': '#5d9cec',
        'success': '#27c24c',
        'info': '#23b7e5',
        'warning': '#ff902b',
        'danger': '#f05050',
        'inverse': '#131e26',
        'green': '#37bc9b',
        'pink': '#f532e5',
        'purple': '#7266ba',
        'dark': '#3a3f51',
        'yellow': '#fad732',
        'gray-darker': '#232735',
        'gray-dark': '#3a3f51',
        'gray': '#dde6e9',
        'gray-light': '#e4eaec',
        'gray-lighter': '#edf1f2'
    })
    .constant('APP_MEDIAQUERY', {
        'desktopLG': 1200,
        'desktop': 992,
        'tablet': 768,
        'mobile': 480
    })
    .constant('APP_REQUIRES', {
        // 加载独立的js脚本
        scripts: {
            'modernizr': ['vendor/modernizr/modernizr.js'],
            'maxNavigation': ['vendor/jquery-max-navigation/js/interface.js'],
            'jqDock': ['vendor/jquery-max-navigation/js/jqDock.js'],
            'screenfull': ['vendor/screenfull/dist/screenfull.js'],
            'echartsjs': ['vendor/echarts/echarts.js'],
            'icons': ['vendor/fontawesome/css/font-awesome.min.css',
                'vendor/simple-line-icons/css/simple-line-icons.css',
                'vendor/weather-icons/css/weather-icons.min.css'],
            'filestyle': ['vendor/bootstrap-filestyle/src/bootstrap-filestyle.js'],
            //'flot-chart': ['vendor/Flot/jquery.flot.js'],
            //'flot-chart-plugins': ['vendor/flot.tooltip/js/jquery.flot.tooltip.min.js',
            //    'vendor/Flot/jquery.flot.resize.js',
            //    'vendor/Flot/jquery.flot.pie.js',
            //    'vendor/Flot/jquery.flot.time.js',
            //    'vendor/Flot/jquery.flot.categories.js',
            //    'vendor/flot-spline/js/jquery.flot.spline.min.js'],
        },
        // 基于Angular的脚本
        modules: [
            {
                name: 'datatables', files: ['vendor/datatables/media/css/jquery.dataTables.css',
                'vendor/datatables/media/js/jquery.dataTables.js',
                'vendor/angular-datatables/dist/angular-datatables.js'], serie: true
            },
            {
                name: 'angularBootstrapNavTree', files: ['vendor/angular-bootstrap-nav-tree/dist/abn_tree_directive.js',
                'vendor/angular-bootstrap-nav-tree/dist/abn_tree.css']
            },
            {
                name: 'ngDialog', files: ['vendor/ngDialog/js/ngDialog.min.js',
                'vendor/ngDialog/css/ngDialog.min.css',
                'vendor/ngDialog/css/ngDialog-theme-default.min.css']
            },
            {
                name: 'dateTimePicker', files: [
                'vendor/datetimepicker/js/bootstrap-datetimepicker.min.js',
                'vendor/datetimepicker/css/bootstrap-datetimepicker.min.css']
            },
            {
                name: 'qrcodeJs', files: [
                'vendor/qrcode/jquery.barcode.js',
                'vendor/qrcode/jquery.qrcode.js',
                'vendor/qrcode/qrcode.utf.js']
            },
            {name: 'angularFileUpload', files: ['vendor/angular-file-upload/angular-file-upload.js']},
            {
                name: 'ngImgCrop', files: ['vendor/ng-img-crop/compile/unminified/ng-img-crop.js',
                'vendor/ng-img-crop/compile/unminified/ng-img-crop.css']
            },
            {name: 'ngWig', files: ['vendor/ngWig/dist/ng-wig.min.js']},
            {name: 'loginJs', files: ['app/js/sys/login.js']},
            {name: 'lockJs', files: ['app/js/sys/lock.js']},
            {name: 'triggerJs', files: ['app/js/index/trigger.js']},
            {name: 'appverJs', files: ['app/js/appver/index.js']},
            {name: 'jobJs', files: ['app/js/index/job.js']}
        ]
    });
/******************************** 设置应用程序的常量  end *************************************************************/

/******************************** 设置主要的控制器 begin **************************************************************/
App.controller('AppController',
    ['$rootScope', '$scope', '$state', '$translate', '$window', '$localStorage', '$timeout', '$http', 'toggleStateService', 'colors', 'browser', 'cfpLoadingBar',
        function ($rootScope, $scope, $state, $translate, $window, $localStorage, $timeout, $http, toggle, colors, browser, cfpLoadingBar) {
            "use strict";
            //退出当前登录
            $scope.logout = function () {
                $http({
                    url: '/login/loginOut.shtml',
                    method: 'GET'
                }).success(function (data) {
                    //响应成功
                    if (data.success) {
                        window.location.href = "#/page/login"; //进入登录页面
                    } else {
                        alert("处理失败！");
                    }
                }).error(function () {
                    //处理响应失败
                    alert("处理失败！");
                });
            }
            // 布局模式
            $rootScope.app.layout.horizontal = ( $rootScope.$stateParams.layout == 'app-h');
            // 加载 bar
            var thBar;
            $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
                if ($('.wrapper > section').length) // 检查容器是否存在
                    thBar = $timeout(function () {
                        cfpLoadingBar.start();
                    }, 0); // 设置延迟
            });
            $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                event.targetScope.$watch("$viewContentLoaded", function () {
                    $timeout.cancel(thBar);
                    cfpLoadingBar.complete();
                });
            });
            // 未找到 hook
            $rootScope.$on('$stateNotFound',
                function (event, unfoundState, fromState, fromParams) {
                    //console.log(unfoundState.to);
                    //console.log(unfoundState.toParams);
                    //console.log(unfoundState.options);
                });
            // Hook 出错
            $rootScope.$on('$stateChangeError',
                function (event, toState, toParams, fromState, fromParams, error) {
                    console.log(error);
                });
            // Hook 成功
            $rootScope.$on('$stateChangeSuccess',
                function (event, toState, toParams, fromState, fromParams) {
                    // 从顶部显示新视图
                    $window.scrollTo(0, 0);
                    // 设置title
                    $rootScope.currTitle = $state.current.title;
                });
            $rootScope.currTitle = $state.current.title;
            $rootScope.pageTitle = function () {
                var title = $rootScope.app.name + ' - ' + ($rootScope.currTitle || $rootScope.app.description);
                document.title = title;
                return title;
            };
            // iPad 点击问题
            // 崩溃时关闭菜单栏
            $rootScope.$watch('app.layout.isCollapsed', function (newValue, oldValue) {
                if (newValue === false)
                    $rootScope.$broadcast('closeSidebarMenu');
            });
            //恢复布局设置
            if (angular.isDefined($localStorage.layout))
                $scope.app.layout = $localStorage.layout;
            else
                $localStorage.layout = $scope.app.layout;
            $rootScope.$watch("app.layout", function () {
                $localStorage.layout = $scope.app.layout;
            }, true);
            // 隐藏/显示 用户信息
            $scope.toggleUserBlock = function () {
                $scope.$broadcast('toggleUserBlock');
            };
            // 允许使用品牌的颜色与插值
            $scope.colorByName = colors.byName;
            // 国际化
            $scope.language = {
                // 语言栏下拉
                listIsOpen: false,
                // 语言信息列表
                available: {
                    'ch': '中文',
                    'en': 'English'
                },
                // 显示当前用户界面语音
                init: function () {
                    var proposedLanguage = $translate.proposedLanguage() || $translate.use();
                    var preferredLanguage = $translate.preferredLanguage(); // 必须在app.config设置首选项
                    $scope.language.selected = $scope.language.available[(proposedLanguage || preferredLanguage)];
                },
                set: function (localeId, ev) {
                    // 设置新风格
                    $translate.use(localeId);
                    // 保存当前语言引用
                    $scope.language.selected = $scope.language.available[localeId];
                    // 最后切换下拉
                    $scope.language.listIsOpen = !$scope.language.listIsOpen;
                }
            };
            $scope.language.init(); //初始化
            // 恢复应用程序状态
            toggle.restoreState($(document.body));
            // 取消单击事件
            $rootScope.cancel = function ($event) {
                $event.stopPropagation();
            };
        }]);
/******************************** 设置主要的控制器 end ****************************************************************/

/******************************** 处理 sidebar 的侧边框 begin *********************************************************/
App.controller('SidebarController', ['$rootScope', '$scope', '$state', '$http', '$timeout', 'Utils',
    function ($rootScope, $scope, $state, $http, $timeout, Utils) {
        var collapseList = [];
        // 例如：当崩溃时，关闭所有项目
        $rootScope.$watch('app.layout.asideHover', function (oldVal, newVal) {
            if (newVal === false && oldVal === true) {
                closeAllBut(-1);
            }
        });
        // 检测子项目状态
        var isActive = function (item) {
            if (!item) return;
            if (!item.sref || item.sref == '#') {
                var foundActive = false;
                angular.forEach(item.submenu, function (value, key) {
                    if (isActive(value)) foundActive = true;
                });
                return foundActive;
            }
            else return $state.is(item.sref) || $state.includes(item.sref);
        };
        // 加载左导航列表  begin -----------------
        $scope.getMenuItemPropClasses = function (item) {
            return (item.heading ? 'nav-heading' : '') + (isActive(item) ? ' active' : '');
        };
        $scope.loadSidebarMenu = function () {
            var menuJson = 'server/sidebar-menu.json',
                menuURL = menuJson + '?v=' + (new Date().getTime()); // jumps cache
            $scope.menuItems = menuItems;

            //$http.get(menuURL)
            //    .success(function (items) {
            //        $scope.menuItems = items;
            //    })
            //    .error(function (data, status, headers, config) {
            //        alert(baseInfo.getInfo("loadFailure"));
            //    });
        };
        $scope.loadSidebarMenu();
        // 加载左导航列表  end --------------------------

        $scope.addCollapse = function ($index, item) {
            collapseList[$index] = $rootScope.app.layout.asideHover ? true : !isActive(item);
        };

        $scope.isCollapse = function ($index) {
            return (collapseList[$index]);
        };

        $scope.toggleCollapse = function ($index, isParentItem, modId) { //modId为立坤扩展属性
            // 侧边框崩溃时，禁止切换
            if (Utils.isSidebarCollapsed() || $rootScope.app.layout.asideHover) return true;
            // make sure the item index exists
            if (angular.isDefined(collapseList[$index])) {
                if (!$scope.lastEventFromChild) {
                    collapseList[$index] = !collapseList[$index];
                    closeAllBut($index);
                }
            } else if (isParentItem) {
                moduleId = modId; //模块栏目编码  by立坤扩展
                closeAllBut(-1);
            } else if (!isParentItem) {
                moduleId = modId;//模块栏目编码  by立坤扩展
            }
            $scope.lastEventFromChild = isChild($index);
            return true;
        };
        function closeAllBut(index) {
            index += '';
            for (var i in collapseList) {
                if (index < 0 || index.indexOf(i) < 0) collapseList[i] = true;
            }
        }

        function isChild($index) {
            return (typeof $index === 'string') && !($index.indexOf('-') < 0);
        }

    }]);
/******************************** 处理 sidebar 的侧边框 end ***********************************************************/

/******************************** 导航栏搜索显示开关自动关闭的ESC键 begin *********************************************/
App.directive('searchOpen', ['navSearch', function (navSearch) {
    'use strict';
    return {
        restrict: 'A',
        controller: ["$scope", "$element", function ($scope, $element) {
            $element
                .on('click', function (e) {
                    e.stopPropagation();
                })
                .on('click', navSearch.toggle);
        }]
    };
}]).directive('searchDismiss', ['navSearch', function (navSearch) {
    'use strict';
    var inputSelector = '.navbar-form input[type="text"]';
    return {
        restrict: 'A',
        controller: ["$scope", "$element", function ($scope, $element) {
            $(inputSelector)
                .on('click', function (e) {
                    e.stopPropagation();
                })
                .on('keyup', function (e) {
                    if (e.keyCode == 27) // ESC
                        navSearch.dismiss();
                });
            // 点击任何地方关闭搜索
            $(document).on('click', navSearch.dismiss);
            $element
                .on('click', function (e) {
                    e.stopPropagation();
                })
                .on('click', navSearch.dismiss);
        }]
    };
}]);
/******************************** 导航栏搜索显示开关自动关闭的ESC键 end ***********************************************/

/**=========================================================
 * Module: filestyle.js
 * Initializes the fielstyle plugin
 =========================================================*/

App.directive('filestyle', function () {
    return {
        restrict: 'A',
        controller: ["$scope", "$element", function ($scope, $element) {
            var options = $element.data();

            // old usage support
            options.classInput = $element.data('classinput') || options.classInput;

            $element.filestyle(options);
        }]
    };
});

/******************************** 全屏展示 begin **********************************************************************/
App.directive('toggleFullscreen', function () {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.on('click', function (e) {
                e.preventDefault();
                if (screenfull.enabled) {
                    screenfull.toggle();
                    // 切换图标
                    if (screenfull.isFullscreen)
                        $(this).children('em').removeClass('fa-expand').addClass('fa-compress');
                    else
                        $(this).children('em').removeClass('fa-compress').addClass('fa-expand');
                } else {
                    $.error('Fullscreen not enabled');
                }
            });
        }
    };
});
/******************************** 全屏展示 end ************************************************************************/

/******************************** 用户头像信息 begin ******************************************************************/
App.controller('UserBlockController', ['$scope', function ($scope) {
    $scope.userBlockVisible = true;
    $scope.$on('toggleUserBlock', function (event, args) {
        $scope.userBlockVisible = !$scope.userBlockVisible;
    });
}]);
/******************************** 用户头像信息 end ********************************************************************/

/******************************** sidebar 折叠 begin ******************************************************************/
App.directive('sidebar', ['$rootScope', '$window', 'Utils', function ($rootScope, $window, Utils) {
    var $win = $($window);
    var $body = $('body');
    var $scope;
    var $sidebar;
    var currentState = $rootScope.$state.current.name;
    return {
        restrict: 'EA',
        template: '<nav class="sidebar" ng-transclude></nav>',
        transclude: true,
        replace: true,
        link: function (scope, element, attrs) {
            $scope = scope;
            $sidebar = element;
            var eventName = Utils.isTouch() ? 'click' : 'mouseenter';
            var subNav = $();
            $sidebar.on(eventName, '.nav > li', function () {
                if (Utils.isSidebarCollapsed() || $rootScope.app.layout.asideHover) {
                    subNav.trigger('mouseleave');
                    subNav = toggleMenuItem($(this));
                    // 检测工具栏的点击和触碰事件
                    sidebarAddBackdrop();
                }
            });
            scope.$on('closeSidebarMenu', function () {
                removeFloatingNav();
            });
            // 移动时调整状态
            $win.on('resize', function () {
                if (!Utils.isMobile())
                    $body.removeClass('aside-toggled');
            });
            // 路线调整
            $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
                currentState = toState.name;
                // 停止工具栏自动移动
                $('body.aside-toggled').removeClass('aside-toggled');
                $rootScope.$broadcast('closeSidebarMenu');
            });
            // 允许关闭
            if (angular.isDefined(attrs.sidebarAnyclickClose)) {
                $('.wrapper').on('click.sidebar', function (e) {
                    // 看不到工具栏时停止检测
                    if (!$body.hasClass('aside-toggled')) return;
                    // 没有子集的工具栏
                    if (!$(e.target).parents('.aside').length) {
                        $body.removeClass('aside-toggled');
                    }
                });
            }
        }
    };

    function sidebarAddBackdrop() {
        var $backdrop = $('<div/>', {'class': 'dropdown-backdrop'});
        $backdrop.insertAfter('.aside-inner').on("click mouseenter", function () {
            removeFloatingNav();
        });
    }

    // Open the collapse sidebar submenu items when on touch devices
    // - desktop only opens on hover
    function toggleTouchItem($element) {
        $element
            .siblings('li')
            .removeClass('open')
            .end()
            .toggleClass('open');
    }

    // 在移动设备上打开工具栏崩溃
    function toggleMenuItem($listItem) {
        removeFloatingNav();
        var ul = $listItem.children('ul');
        if (!ul.length) return $();
        if ($listItem.hasClass('open')) {
            toggleTouchItem($listItem);
            return $();
        }
        var $aside = $('.aside');
        var $asideInner = $('.aside-inner'); // 计算上部偏移
        // 为额外的padding设置浮动
        var mar = parseInt($asideInner.css('padding-top'), 0) + parseInt($aside.css('padding-top'), 0);
        var subNav = ul.clone().appendTo($aside);
        toggleTouchItem($listItem);
        var itemTop = ($listItem.position().top + mar) - $sidebar.scrollTop();
        var vwHeight = $win.height();
        subNav
            .addClass('nav-floating')
            .css({
                position: $scope.app.layout.isFixed ? 'fixed' : 'absolute',
                top: itemTop,
                bottom: (subNav.outerHeight(true) + itemTop > vwHeight) ? 0 : 'auto'
            });
        subNav.on('mouseleave', function () {
            toggleTouchItem($listItem);
            subNav.remove();
        });
        return subNav;
    }

    function removeFloatingNav() {
        $('.dropdown-backdrop').remove();
        $('.sidebar-subnav.nav-floating').remove();
        $('.sidebar li.open').removeClass('open');
    }

}]);
/******************************** sidebar 折叠 end ********************************************************************/

/** 设置[toggle-state="CLASS-NAME-TO-TOGGLE"]避免浏览器在用户没有保存状态时，改变一个类名，导致了全局的改变 begin *****/
App.directive('toggleState', ['toggleStateService', function (toggle) {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var $body = $('body');
            $(element)
                .on('click', function (e) {
                    e.preventDefault();
                    var classname = attrs.toggleState;
                    if (classname) {
                        if ($body.hasClass(classname)) {
                            $body.removeClass(classname);
                            if (!attrs.noPersist)
                                toggle.removeState(classname);
                        }
                        else {
                            $body.addClass(classname);
                            if (!attrs.noPersist)
                                toggle.addState(classname);
                        }
                    }
                });
        }
    };
}]);
/** 设置[toggle-state="CLASS-NAME-TO-TOGGLE"]避免浏览器在用户没有保存状态时，改变一个类名，导致了全局的改变 begin *****/

/******************************** 浏览器检测 begin ********************************************************************/
App.service('browser', function () {
    "use strict";
    var matched, browser;
    var uaMatch = function (ua) {
        ua = ua.toLowerCase();
        var match = /(opr)[\/]([\w.]+)/.exec(ua) ||
            /(chrome)[ \/]([\w.]+)/.exec(ua) ||
            /(version)[ \/]([\w.]+).*(safari)[ \/]([\w.]+)/.exec(ua) ||
            /(webkit)[ \/]([\w.]+)/.exec(ua) ||
            /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua) ||
            /(msie) ([\w.]+)/.exec(ua) ||
            ua.indexOf("trident") >= 0 && /(rv)(?::| )([\w.]+)/.exec(ua) ||
            ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua) ||
            [];
        var platform_match = /(ipad)/.exec(ua) ||
            /(iphone)/.exec(ua) ||
            /(android)/.exec(ua) ||
            /(windows phone)/.exec(ua) ||
            /(win)/.exec(ua) ||
            /(mac)/.exec(ua) ||
            /(linux)/.exec(ua) ||
            /(cros)/i.exec(ua) ||
            [];
        return {
            browser: match[3] || match[1] || "",
            version: match[2] || "0",
            platform: platform_match[0] || ""
        };
    };
    matched = uaMatch(window.navigator.userAgent);
    browser = {};
    if (matched.browser) {
        browser[matched.browser] = true;
        browser.version = matched.version;
        browser.versionNumber = parseInt(matched.version);
    }
    if (matched.platform) {
        browser[matched.platform] = true;
    }
    // 移动设备浏览器
    if (browser.android || browser.ipad || browser.iphone || browser["windows phone"]) {
        browser.mobile = true;
    }
    // 桌面浏览器
    if (browser.cros || browser.mac || browser.linux || browser.win) {
        browser.desktop = true;
    }
    // Chrome, Opera 15+，Safari 等基于 webkit 的浏览器
    if (browser.chrome || browser.opr || browser.safari) {
        browser.webkit = true;
    }
    // 为ie11分配数字证书，以防止访问禁止
    if (browser.rv) {
        var ie = "msie";
        matched.browser = ie;
        browser[ie] = true;
    }
    // Opera 15+ 标注为 opr
    if (browser.opr) {
        var opera = "opera";
        matched.browser = opera;
        browser[opera] = true;
    }
    // Android的浏览器标记为Safari浏览器
    if (browser.safari && browser.android) {
        var android = "android";
        matched.browser = android;
        browser[android] = true;
    }
    // 指定名称和平台变量
    browser.name = matched.browser;
    browser.platform = matched.platform;
    return browser;
});
/******************************** 浏览器检测 end **********************************************************************/

/******************************** 检索全局颜色 begin ******************************************************************/
App.factory('colors', ['APP_COLORS', function (colors) {
    return {
        byName: function (name) {
            return (colors[name] || '#fff');
        }
    };
}]);
/******************************** 检索全局颜色 end ********************************************************************/

/******************************** 服务共享导航栏的搜索功能 begin ******************************************************/
App.service('navSearch', function () {
    var navbarFormSelector = 'form.navbar-form';
    return {
        toggle: function () {
            var navbarForm = $(navbarFormSelector);
            navbarForm.toggleClass('open');
            var isOpen = navbarForm.hasClass('open');
            navbarForm.find('input')[isOpen ? 'focus' : 'blur']();
        },
        dismiss: function () {
            $(navbarFormSelector)
                .removeClass('open')      // Close control
                .find('input[type="text"]').blur() // remove focus
                .val('')                    // Empty input
            ;
        }
    };
});
/******************************** 服务共享导航栏的搜索功能 end ********************************************************/

/******************************** 提供路由定义的辅助函数 begin ********************************************************/
App.provider('RouteHelpers', ['APP_REQUIRES', function (appRequires) {
    "use strict";
    // 在此基础上设置相对路径
    // 应用视图
    this.basepath = function (uri) {
        return 'app/views/' + uri;
    };
    // 通过脚本名称生成解决对象
    // 在constant.app_requires先前配置
    this.resolveFor = function () {
        var _args = arguments;
        return {
            deps: ['$ocLazyLoad', '$q', function ($ocLL, $q) {
                // 为每个参数创建一个承诺链
                var promise = $q.when(1); // 空承诺链
                for (var i = 0, len = _args.length; i < len; i++) {
                    promise = andThen(_args[i]);
                }
                return promise;
                // 创建动态承诺链
                function andThen(_arg) {
                    // 支持一个函数，返回一个承诺
                    if (typeof _arg == 'function')
                        return promise.then(_arg);
                    else
                        return promise.then(function () {
                            // 如果是一个模块，通过名称。如果没有，通过数组
                            var whatToLoad = getRequired(_arg);
                            // 简单的错误检查
                            if (!whatToLoad) return $.error('Route resolve: Bad resource name [' + _arg + ']');
                            // 最后，返回一个承诺
                            return $ocLL.load(whatToLoad);
                        });
                }

                // 检查并返回所需的数据
                // 模块项目的形式 [name: '', files: []]
                // 简单的脚本文件数组
                function getRequired(name) {
                    if (appRequires.modules)
                        for (var m in appRequires.modules)
                            if (appRequires.modules[m].name && appRequires.modules[m].name === name)
                                return appRequires.modules[m];
                    return appRequires.scripts && appRequires.scripts[name];
                }
            }]
        };
    };
    this.$get = function () {
    };
}]);
/******************************** 提供路由定义的辅助函数 end **********************************************************/

/******************************** 共享切换状态功能的服务 begin ********************************************************/
App.service('toggleStateService', ['$rootScope', function ($rootScope) {
    var storageKeyName = 'toggleState';
    // 帮助对象在短语中检查单词
    var WordChecker = {
        hasWord: function (phrase, word) {
            return new RegExp('(^|\\s)' + word + '(\\s|$)').test(phrase);
        },
        addWord: function (phrase, word) {
            if (!this.hasWord(phrase, word)) {
                return (phrase + (phrase ? ' ' : '') + word);
            }
        },
        removeWord: function (phrase, word) {
            if (this.hasWord(phrase, word)) {
                return phrase.replace(new RegExp('(^|\\s)*' + word + '(\\s|$)*', 'g'), '');
            }
        }
    };

    // 返回服务公共方法
    return {
        // 将一个状态添加到浏览器存储中，稍后恢复
        addState: function (classname) {
            var data = angular.fromJson($rootScope.$storage[storageKeyName]);
            if (!data) {
                data = classname;
            }
            else {
                data = WordChecker.addWord(data, classname);
            }
            $rootScope.$storage[storageKeyName] = angular.toJson(data);
        },

        // 从浏览器存储中删除一个状态
        removeState: function (classname) {
            var data = $rootScope.$storage[storageKeyName];
            // nothing to remove
            if (!data) return;
            data = WordChecker.removeWord(data, classname);
            $rootScope.$storage[storageKeyName] = angular.toJson(data);
        },

        // 加载合格的恢复名单
        restoreState: function ($elem) {
            var data = angular.fromJson($rootScope.$storage[storageKeyName]);
            // 没有恢复
            if (!data) return;
            $elem.addClass(data);
        }
    };
}]);
/******************************** 共享切换状态功能的服务 end **********************************************************/

/******************************** 在主题内使用jar包 begin *************************************************************/
App.service('Utils', ["$window", "APP_MEDIAQUERY", function ($window, APP_MEDIAQUERY) {
    'use strict';
    var $html = angular.element("html"),
        $win = angular.element($window),
        $body = angular.element('body');
    return {
        support: {
            transition: (function () {
                var transitionEnd = (function () {
                    var element = document.body || document.documentElement,
                        transEndEventNames = {
                            WebkitTransition: 'webkitTransitionEnd',
                            MozTransition: 'transitionend',
                            OTransition: 'oTransitionEnd otransitionend',
                            transition: 'transitionend'
                        }, name;
                    for (name in transEndEventNames) {
                        if (element.style[name] !== undefined) return transEndEventNames[name];
                    }
                }());
                return transitionEnd && {end: transitionEnd};
            })(),
            animation: (function () {
                var animationEnd = (function () {
                    var element = document.body || document.documentElement,
                        animEndEventNames = {
                            WebkitAnimation: 'webkitAnimationEnd',
                            MozAnimation: 'animationend',
                            OAnimation: 'oAnimationEnd oanimationend',
                            animation: 'animationend'
                        }, name;
                    for (name in animEndEventNames) {
                        if (element.style[name] !== undefined) return animEndEventNames[name];
                    }
                }());
                return animationEnd && {end: animationEnd};
            })(),
            requestAnimationFrame: window.requestAnimationFrame ||
            window.webkitRequestAnimationFrame ||
            window.mozRequestAnimationFrame ||
            window.msRequestAnimationFrame ||
            window.oRequestAnimationFrame ||
            function (callback) {
                window.setTimeout(callback, 1000 / 60);
            },
            touch: (
                ('ontouchstart' in window && navigator.userAgent.toLowerCase().match(/mobile|tablet/)) ||
                (window.DocumentTouch && document instanceof window.DocumentTouch) ||
                (window.navigator['msPointerEnabled'] && window.navigator['msMaxTouchPoints'] > 0) || //IE 10
                (window.navigator['pointerEnabled'] && window.navigator['maxTouchPoints'] > 0) || //IE >=11
                false
            ),
            mutationobserver: (window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver || null)
        },
        isInView: function (element, options) {
            var $element = $(element);
            if (!$element.is(':visible')) {
                return false;
            }
            var window_left = $win.scrollLeft(),
                window_top = $win.scrollTop(),
                offset = $element.offset(),
                left = offset.left,
                top = offset.top;
            options = $.extend({topoffset: 0, leftoffset: 0}, options);
            if (top + $element.height() >= window_top && top - options.topoffset <= window_top + $win.height() &&
                left + $element.width() >= window_left && left - options.leftoffset <= window_left + $win.width()) {
                return true;
            } else {
                return false;
            }
        },
        langdirection: $html.attr("dir") == "rtl" ? "right" : "left",
        isTouch: function () {
            return $html.hasClass('touch');
        },
        isSidebarCollapsed: function () {
            return $body.hasClass('aside-collapsed');
        },
        isSidebarToggled: function () {
            return $body.hasClass('aside-toggled');
        },
        isMobile: function () {
            return $win.width() < APP_MEDIAQUERY.tablet;
        }
    };
}]);
/******************************** 在主题内使用jar包 end ***************************************************************/

// 调用 myappname模块，运营程序
var myApp = angular.module('myAppName', ['angle']);
myApp.run(["$log", function ($log) {
    $log.log('I\'m a line from custom.js');
}]);

myApp.config(["RouteHelpersProvider", function (RouteHelpersProvider) {
    // 自定义路由
}]);

myApp.controller('oneOfMyOwnController', ["$scope", function ($scope) {
    /* 控制器代码 */
}]);

myApp.directive('oneOfMyOwnDirectives', function () {
    /*指令代码*/
});

myApp.config(["$stateProvider", function ($stateProvider /* ... */) {
    /* 具体地址 (config.js文件) */
}]);


/**=========================================================
 * Module: form-wizard.js
 * Handles form wizard plugin and validation
 =========================================================*/
App.directive('formWizard', ["$parse", function ($parse) {
    'use strict';
    return {
        restrict: 'A',
        scope: true,
        link: function (scope, element, attribute) {
            var validate = $parse(attribute.validateSteps)(scope),
                wiz = new Wizard(attribute.steps, !!validate, element);
            scope.wizard = wiz.init();
        }
    };

    function Wizard(quantity, validate, element) {
        var self = this;
        self.quantity = parseInt(quantity, 10);
        self.validate = validate;
        self.element = element;
        self.init = function () {
            self.createsteps(self.quantity);
            self.go(1); // always start at fist step
            return self;
        };
        self.go = function (step) {
            if (angular.isDefined(self.steps[step])) {

                if (self.validate && step !== 1) {
                    var form = $(self.element),
                        group = form.children().children('div').get(step - 2);
                    if (false === form.parsley().validate(group.id)) {
                        return false;
                    }
                }
                self.cleanall();
                self.steps[step] = true;
            }
        };

        self.active = function (step) {
            return !!self.steps[step];
        };

        self.cleanall = function () {
            for (var i in self.steps) {
                self.steps[i] = false;
            }
        };

        self.createsteps = function (q) {
            self.steps = [];
            for (var i = 1; i <= q; i++) self.steps[i] = false;
        };

    }

}]);
/**=========================================================
 *  echarts 图表组件
 =========================================================*/
App.directive('rzhecharts', function () {
    return {
        scope: {
            rid: "@",
            option: "=",
            width: "=",
            height: "="
        },
        restrict: 'E',
        template: '<div></div>',
        replace: true,
        link: function ($scope, element, attrs, controller) {
            var opr;
            if ($scope.height > 0) {
                opr = {
                    height: $scope.height
                };
            }
            var myChart = echarts.init(document.getElementById($scope.rid), 'macarons', opr);

            $scope.$watch('option', function(n, o) {
                if (n === o || !n) return;
                myChart.setOption(n,true);
            });

            $scope.$on('$destory', function() {
                window.removeEventListener('resize', chartResize);
            })

            function chartResize() {
                myChart.resize();
            }
            myChart.setOption($scope.option);
        }
    };
});
/************************************************* 扩展  by 立坤  *****************************************************/
/**
 * @author 立坤 更新于2016.06.27
 * 获取一个静态页面的html代码信息并进行简单操作
 * @param url
 * @param but_name
 * @param but_id
 * @returns {*}
 */
function getHtmlInfos(url, but_name, but_id) {
    var result;
    $.ajax({ //ajax方式获取html的信息
        cache: false,
        async: false,
        url: url, //这里是静态页的地址
        type: "GET", //静态页用get方法，否则服务器会抛出405错误
        success: function (data) {
            result = data;
        }
    });
    //改变按钮名称
    if (result != null && result != "" && but_name != null && but_name != "") {
        if (url.indexOf("to_add") > -1 || url.indexOf("grid_add") > -1) result = result.replace(new RegExp("添加", "g"), but_name);
        if (url.indexOf("to_build") > -1 || url.indexOf("grid_build") > -1) result = result.replace(new RegExp("生成", "g"), but_name);
        if (url.indexOf("to_delete") > -1 || url.indexOf("grid_delete") > -1) result = result.replace(new RegExp("删除", "g"), but_name);
        if (url.indexOf("to_download") > -1 || url.indexOf("grid_download") > -1) result = result.replace(new RegExp("下载", "g"), but_name);
        if (url.indexOf("to_remove") > -1 || url.indexOf("grid_remove") > -1) result = result.replace(new RegExp("取消", "g"), but_name);
        if (url.indexOf("to_search") > -1 || url.indexOf("grid_search") > -1) result = result.replace(new RegExp("搜索", "g"), but_name);
        if (url.indexOf("to_start") > -1 || url.indexOf("grid_start") > -1) result = result.replace(new RegExp("启用", "g"), but_name);
        if (url.indexOf("to_stop") > -1 || url.indexOf("grid_stop") > -1) result = result.replace(new RegExp("停用", "g"), but_name);
        if (url.indexOf("to_submit") > -1 || url.indexOf("grid_submit") > -1) result = result.replace(new RegExp("提交", "g"), but_name);
        if (url.indexOf("to_update") > -1 || url.indexOf("grid_update") > -1) result = result.replace(new RegExp("修改", "g"), but_name);
        if (url.indexOf("to_upload") > -1 || url.indexOf("grid_upload") > -1) result = result.replace(new RegExp("上传", "g"), but_name);
        if (url.indexOf("to_details") > -1 || url.indexOf("grid_details") > -1) result = result.replace(new RegExp("详情", "g"), but_name);
        if (url.indexOf("to_back") > -1 || url.indexOf("grid_back") > -1) result = result.replace(new RegExp("返回", "g"), but_name);
        if (url.indexOf("to_refresh") > -1 || url.indexOf("grid_refresh") > -1) result = result.replace(new RegExp("刷新", "g"), but_name);
        if (url.indexOf("to_restart") > -1 || url.indexOf("grid_restart") > -1) result = result.replace(new RegExp("重启", "g"), but_name);
        if (url.indexOf("to_exe") > -1 || url.indexOf("grid_exe") > -1) result = result.replace(new RegExp("执行", "g"), but_name);
    }
    //改变对应的id
    if (result != null && result != "" && but_id != null && but_id != "") {
        if (url.indexOf("to_add") > -1) result = result.replace("toAdd", but_id);
        if (url.indexOf("to_build") > -1) result = result.replace("toBuild", but_id);
        if (url.indexOf("to_delete") > -1) result = result.replace("toDelete", but_id);
        if (url.indexOf("to_download") > -1) result = result.replace("toDownload", but_id);
        if (url.indexOf("to_remove") > -1) result = result.replace("toRemove", but_id);
        if (url.indexOf("to_search") > -1) result = result.replace("toSearch", but_id);
        if (url.indexOf("to_start") > -1) result = result.replace("toStart", but_id);
        if (url.indexOf("to_stop") > -1) result = result.replace("toStop", but_id);
        if (url.indexOf("to_submit") > -1) result = result.replace("toSubmit", but_id);
        if (url.indexOf("to_update") > -1) result = result.replace("toUpdate", but_id);
        if (url.indexOf("to_upload") > -1) result = result.replace("toUpload", but_id);
        if (url.indexOf("to_details") > -1) result = result.replace("toDetails", but_id);
        if (url.indexOf("to_back") > -1) result = result.replace("toBack", but_id);
        if (url.indexOf("grid_add") > -1) result = result.replace("gridAdd", but_id);
        if (url.indexOf("grid_build") > -1) result = result.replace("gridBuild", but_id);
        if (url.indexOf("grid_delete") > -1) result = result.replace("gridDelete", but_id);
        if (url.indexOf("grid_download") > -1) result = result.replace("gridDownload", but_id);
        if (url.indexOf("grid_remove") > -1) result = result.replace("gridRemove", but_id);
        if (url.indexOf("grid_search") > -1) result = result.replace("gridSearch", but_id);
        if (url.indexOf("grid_refresh") > -1) result = result.replace("gridRefresh", but_id);
        if (url.indexOf("grid_restart") > -1) result = result.replace("gridRestart", but_id);
        if (url.indexOf("grid_exe") > -1) result = result.replace("gridExe", but_id);
        if (url.indexOf("grid_start") > -1) result = result.replace("gridStart", but_id);
        if (url.indexOf("grid_stop") > -1) result = result.replace("gridStop", but_id);
        if (url.indexOf("grid_agree") > -1) result = result.replace("gridAgree", but_id);
        if (url.indexOf("grid_reject") > -1) result = result.replace("gridReject", but_id);
        if (url.indexOf("grid_submit") > -1) result = result.replace("gridSubmit", but_id);
        if (url.indexOf("grid_update") > -1) result = result.replace("gridUpdate", but_id);
        if (url.indexOf("grid_upload") > -1) result = result.replace("gridUpload", but_id);
        if (url.indexOf("grid_details") > -1) result = result.replace("gridDetails", but_id);
        if (url.indexOf("grid_back") > -1) result = result.replace("gridBack", but_id);
    }
    return result;
}

/**
 * @author 立坤 更新于2016.07.30
 * 格式化日期
 * @param str
 * @returns {string}
 */
function formatDate(str) {
   return str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8) + " " + str.substring(8, 10) + ":" + str.substring(10, 12) + ":" + str.substring(12, 14);
}

function formatDateNum14(str){
    return str.replace(/-/g, "").replace(" ", "").replace(/:/g, "");
}


/**
 * @author 伯谦 更新于2016.12.27
 * 格式化日期
 * @param str
 * @returns {string}
 */
function formatDay(str) {
    return str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
}

function formatDayNum8(str){
    return str.replace(/-/g, "");
}
//列表信息控制
// 全选
function gridInfoCheckAll(id) {
    $('#' + id + ' #grid-check-all').click(function () {
        $('#' + id + " :checkbox:not(#grid-check-all)").attr("checked", this.checked);
        $('#' + id + " :checkbox:not(#grid-check-all)").prop("checked", this.checked);
        $('#' + id + " tr").toggleClass('checked');
    });
}
//点击列表，自动勾选
function ckClickTr(id) {
    var isClick = true;
    $("#" + id + " tbody tr button").click(function () {
        isClick = false;
    });
    $("#" + id + " tbody tr").click(function (e) {
        if (isClick) {
            var _this = $(this);
            var ckeck = _this.find("input[type=checkbox]:checked");
            if (ckeck.is(':checked')) {
                _this.find("input[type=checkbox]").attr("checked", false);
                _this.find("input[type=checkbox]").prop("checked", false);
                _this.removeClass("checked");
            } else {
                _this.find("input[type=checkbox]").attr("checked", true);
                _this.find("input[type=checkbox]").prop("checked", true);
                _this.addClass('checked');
            }
        }
        isClick = true;
    });
}

//封装弹出框
function rzhdialog(ngDialog,msg,type){
    //如果是登录标识
    if(typeof(msg)!="undefined"&&msg.indexOf("{{TOLOGIN}}")>-1){
        window.location.href = "#/page/login"; //进入登录页面
        return;
    }
    var html="";
    if(type=="help"){
        html='<p class="fa fa-question-circle" style="color:dodgerblue;"> ' + msg + '</p>';
    }else if(type=="success"){
        html= '<p class="fa fa-check-circle" style="color:green;"> ' + msg + '</p>';
    }else if(type=="error"){
        html='<p class="fa fa-exclamation-circle" style="color:orangered;"> ' + msg + '</p>';
    }else{
        html= '<p class="fa fa-info-circle"> ' + msg + '</p>';
    }
    var dialog = ngDialog.open({
        template: html,
        plain: true,
        showClose: false,
        closeByDocument: false,
        closeByEscape: false
    });
    setTimeout(function () {
        dialog.close();
    }, 2000);
}


//获得选中行数
function getTableDataList(table){
    var datas = table.rows(".checked").data();
    return datas;
}


//获得选中行数
function getTableData(table){
    var datas = table.rows(".checked").data();
    if(datas.length==1)return datas[0];
    return null;
}

//获得选中行数
function getTableSelectedCount(table){
    var datas = table.rows(".checked").data();
    return datas.length;
}
