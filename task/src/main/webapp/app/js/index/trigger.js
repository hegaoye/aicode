/*
 * Copyright (c) 2016. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */
var grid_levels = "";
App.controller('triggerController', ['$scope', '$http', "ngDialog", function ($scope, $http, ngDialog) {
    $scope.gridtableid = "triggerTableGrids";
    grid_levels = ""; //初始化，必须
    var levels = [
        {
            component: "base",
            operations: [
                {
                    btn: "add",
                    name: "添加",
                    func: "toAdd"
                },
                {
                    btn: "update",
                    name: "mysql",
                    func: "toMysql"
                },
                {
                    btn: "update",
                    name: "redis",
                    func: "toRedis"
                }
            ],
            name: "外层功能",
            id: "toBase"
        },
        {
            component: "grid",
            operations: [
                {
                    btn: "grid_update",
                    name: "更改调度的时间规则",
                    func: "gridUpdate"
                },
                {
                    btn: "grid_delete",
                    name: "删除当前调度",
                    func: "gridDelete"
                },
                {
                    btn: "grid_stop",
                    name: "暂停当前调度",
                    func: "gridStop"
                },
                {
                    btn: "grid_start",
                    name: "启用当前调度",
                    func: "gridStart"
                },
                {
                    btn: "grid_exe",
                    name: "按需立即执行当前调度",
                    func: "gridExe"
                }
            ],
            name: "数据列表",
            id: "grid"
        }
    ]
    var mod = "";
    var lev = "";
    angular.forEach(levels, function (data, index, array) {
        mod = data.id;
        lev = data.operations;
        //处理最外层按钮权限
        if (mod == "toBase") {
            var base_levels = "";
            angular.forEach(lev, function (data, index, array) {
                base_levels += getHtmlInfos("app/views/base/to_" + data.btn + ".html", data.name, data.func) + " ";
            });
            angular.element("#topBar").append(base_levels); //添加功能按钮
        }
        //处理列表及其中按钮权限
        if (mod == "grid") {
            $scope.grid = true; //显示数据列表
            angular.forEach(lev, function (data, index, array) { //列表中按钮处理
                if (data.btn == "grid_update") $scope.areaGridUpdate = data.func;
                grid_levels += getHtmlInfos("app/views/base/" + data.btn + ".html", data.name, data.func) + " ";
            });
        }
    });

    //添加
    $("#toAdd").click(function () {
        window.location.href = "#/app/index/add";
    });
    //dubbo
    $("#toMysql").click(function () {
        //window.location.href = "http://dubbo.lecing.com";
        window.open("http://dubbo.ponddy.com");
    });
    //dubboAdmin
    $("#toRedis").click(function () {
        //window.location.href = "http://dubbo.lecing.com/redisadmin";
        window.open("http://dubbo.ponddy.com/redisadmin");
    });
    //关闭弹出窗口操作
    $scope.toRemove = function () {
        window.location.href = "#/app/index";
    }

}]);

//列表信息处理
App.controller('triggerGridController', ['$scope', "$http", "ngDialog", function ($scope, $http, ngDialog) {
    //分页动态加载数据
    function retrieveData(sSource, aoData, fnCallback) {
        //将客户名称加入参数数组
        $.ajax({
            type: "POST",
            url: sSource,
            dataType: 'json',
            //data: {"aoData": JSON.stringify(aoData),"typecode":global_type_code,"type":$scope.global_type}, //以json格式传递
            async: false,
            "success": function (response) {
                if(response.success){
                    fnCallback(response.data); //服务器端返回的对象的returnObject部分是要求的格式
                }else{
                    rzhdialog(ngDialog,response.info,"error");
                }
            }
        });
    }

    //数据列表信息
    var table = $('#' + $scope.gridtableid).DataTable({
        processing: true,                    //加载数据时显示正在加载信息
        showRowNumber: true,
        serverSide: true,                    //指定从服务器端获取数据
        pageLength: 100,                    //每页显示25条数据
        scrollX: true,                      //显示横向进度条
        sPaginationType: "full_numbers",
        ajaxSource: "/job/Triggers/listTriggers.shtml",//获取数据的url
        fnDrawCallback: function () {
            this.api().column(1).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        fnServerData: retrieveData,           //获取数据的处理函数
        aoColumns: [
            {sTitle: getHtmlInfos("app/views/base/check_all.html"), width: "5%"},
            {sTitle: "序号"},
            {mDataProp: "triggerName", sTitle: "触发器"},
            {mDataProp: "priority", sTitle: "优先级"},
            {mDataProp: "triggerState", sTitle: "状态"},
            {mDataProp: "triggerType", sTitle: "类型"},
            {mDataProp: "nextFireTime", sTitle: "下次执行时间"},
            {mDataProp: "prevFireTime", sTitle: "上次执行时间"},
            {mDataProp: "startTime", sTitle: "开始时间"},
            {mDataProp: "endTime", sTitle: "结束时间"},
            {mDataProp: "misfireInstr", sTitle: "错失"},
            {mDataProp: "description", sTitle: "描述"},
            {sTitle: "操作",sWidth:"230px"}
        ],
        aoColumnDefs: [//设置列的属性
            {
                bSortable: false,
                targets: 0,
                defaultContent: getHtmlInfos("app/views/base/check.html")
            },
            {
                bSortable: false,
                data: null,
                targets: 1
            },
            {
                targets: -1, //最后一列
                data: null,//数据为空
                bSortable: false,//不排序
                defaultContent: grid_levels
            }
        ]
    });
    table.draw();
    //选项框
    gridInfoCheckAll($scope.gridtableid); // 全选/全不选
    ckClickTr($scope.gridtableid); //单击行，选中复选框
    //行内修改
    $("#" + $scope.gridtableid + " tbody").on('click', 'tr td button.gridUpdate', function () {
        var data = table.row($(this).parent().parent()).data(); //获取爷爷节点 tr 的值
        updatetriggerControllerData=data;
        window.location.href = "#/app/index/update";
    });
    //行内删除
    $("#" + $scope.gridtableid + " tbody").on('click', 'tr td button.gridDelete', function () {
        var data = table.row($(this).parent().parent()).data(); //获取爷爷节点 tr 的值
        ngDialog.openConfirm({
            template: 'deltriggerDialogId',
            className: 'ngdialog-theme-default'
        }).then(function (value) { //用户点击确认
            angularParamString($http); //解决post提交接收问题，json方式改为string方式
            $http({
                url: '/job/Triggers/removeTrigger.shtml',
                method: 'POST',
                data: {triggerName: data.triggerName,triggerGroup:data.jobGroup}
            }).success(function (response) { //提交成功
                if (response.success) { //信息处理成功，进入用户中心页面
                    table.order([[1, 'asc']]).draw(false); //刷新表格并维持当前分页
                    ckClickTr($scope.gridtableid); //单击行，选中复选框
                    rzhdialog(ngDialog,response.info,"success");
                } else { //信息处理失败，提示错误信息
                    rzhdialog(ngDialog,response.info,"error");
                }
            }).error(function (response) { //提交失败
                rzhdialog(ngDialog,"服务器繁忙,请重试！","error");
            })
        }, function (response) {
            //用户点击取消
        });
    });

    //行内停用
    $("#" + $scope.gridtableid + " tbody").on('click', 'tr td button.gridStop', function () {
        var data = table.row($(this).parent().parent()).data(); //获取爷爷节点 tr 的值
        ngDialog.openConfirm({
            template: 'stoptriggerDialogId',
            className: 'ngdialog-theme-default'
        }).then(function (value) { //用户点击确认
            angularParamString($http); //解决post提交接收问题，json方式改为string方式
            $http({
                url: '/job/Triggers/stopTrigger.shtml',
                method: 'POST',
                data: {triggerName: data.triggerName,triggerGroup:data.jobGroup}
            }).success(function (response) { //提交成功
                if (response.success) { //信息处理成功，进入用户中心页面
                    table.order([[1, 'asc']]).draw(false); //刷新表格并维持当前分页
                    ckClickTr($scope.gridtableid); //单击行，选中复选框
                    rzhdialog(ngDialog,response.info,"success");
                } else { //信息处理失败，提示错误信息
                    rzhdialog(ngDialog,response.info,"error");
                }
            }).error(function (response) { //提交失败
                rzhdialog(ngDialog,"服务器繁忙,请重试！","error");
            })
        }, function (response) {
            //用户点击取消
        });
    });
    //行内启用
    $("#" + $scope.gridtableid + " tbody").on('click', 'tr td button.gridStart', function () {
        var data = table.row($(this).parent().parent()).data(); //获取爷爷节点 tr 的值
        ngDialog.openConfirm({
            template: 'starttriggerDialogId',
            className: 'ngdialog-theme-default'
        }).then(function (value) { //用户点击确认
            angularParamString($http); //解决post提交接收问题，json方式改为string方式
            $http({
                url: '/job/Triggers/restartTrigger.shtml',
                method: 'POST',
                data: {triggerName: data.triggerName,triggerGroup:data.jobGroup}
            }).success(function (response) { //提交成功
                if (response.success) { //信息处理成功，进入用户中心页面
                    table.order([[1, 'asc']]).draw(false); //刷新表格并维持当前分页
                    ckClickTr($scope.gridtableid); //单击行，选中复选框
                    rzhdialog(ngDialog,response.info,"success");
                } else { //信息处理失败，提示错误信息
                    rzhdialog(ngDialog,response.info,"error");
                }
            }).error(function (response) { //提交失败
                rzhdialog(ngDialog,"服务器繁忙,请重试！","error");
            })
        }, function (response) {
            //用户点击取消
        });
    });
    //行内执行
    $("#" + $scope.gridtableid + " tbody").on('click', 'tr td button.gridExe', function () {
        var data = table.row($(this).parent().parent()).data(); //获取爷爷节点 tr 的值
        ngDialog.openConfirm({
            template: 'exetriggerDialogId',
            className: 'ngdialog-theme-default'
        }).then(function (value) { //用户点击确认
            angularParamString($http); //解决post提交接收问题，json方式改为string方式
            $http({
                url: '/job/Triggers/exeTrigger.shtml',
                method: 'POST',
                data: {jobName: data.jobName,jobGroup:data.jobGroup}
            }).success(function (response) { //提交成功
                if (response.success) { //信息处理成功，进入用户中心页面
                    table.order([[1, 'asc']]).draw(false); //刷新表格并维持当前分页
                    ckClickTr($scope.gridtableid); //单击行，选中复选框
                    rzhdialog(ngDialog,response.info,"success");
                } else { //信息处理失败，提示错误信息
                    rzhdialog(ngDialog,response.info,"error");
                }
            }).error(function (response) { //提交失败
                rzhdialog(ngDialog,"服务器繁忙,请重试！","error");
            })
        }, function (response) {
            //用户点击取消
        });
    });
}]);

//添加信息
App.controller("addtriggerController", function ($scope, $http, ngDialog) {
    $scope.trigger = null;
    //添加数据
    $scope.save = function () {
        angularParamString($http); //解决post提交接收问题，json方式改为string方式
        var table = $('#' + $scope.gridtableid).DataTable();
        //验证通过
        if ($scope.form.$invalid) {
            rzhdialog(ngDialog, "您输入的表单内容的不合法，请修改后提交", "error");
            return;
        } else {
            //增加分类属性
            var pData = {
                jobName: $scope.trigger.jobName,
                jobGroup: $scope.trigger.jobGroup,
                jobClassName: $scope.trigger.jobClassName,
                triggerName: $scope.trigger.triggerName,
                cronExpression: $scope.trigger.cronExpression,
                description: $scope.trigger.description
            };
            $http({
                url: '/job/Triggers/buildTrigger.shtml',
                method: 'POST',
                data: pData
            }).success(function (response) { //提交成功
                if (response.success) { //信息处理成功，进入用户中心页面
                    $scope.trigger = null;
                    table.draw(); //重新加载数据
                    ckClickTr($scope.gridtableid); //单击行，选中复选框
                    rzhdialog(ngDialog, response.info, "success");
                    $scope.toRemove();
                } else { //信息处理失败，提示错误信息
                    rzhdialog(ngDialog, response.info, "error");
                }
            }).error(function (response) { //提交失败
                rzhdialog(ngDialog, "操作失败", "error");
            })
        }
    }
});

var updatetriggerControllerData=null;
//修改信息
App.controller("updatetriggerController", function ($scope, $http, ngDialog) {
    $scope.trigger=updatetriggerControllerData;
    //修改信息
    $scope.save = function () {
        angularParamString($http); //解决post提交接收问题，json方式改为string方式
        var table = $('#' + $scope.gridtableid).DataTable();
        //验证通过
        if ($scope.form.$invalid) {
            rzhdialog(ngDialog, "您输入的表单内容的不合法，请修改后提交", "error");
            return;
        } else {
            //增加分类属性
            var pData = {
                triggerGroup: $scope.trigger.jobGroup,
                triggerName: $scope.trigger.triggerName,
                cronExpression: $scope.trigger.cronExpression,
                description: $scope.trigger.description
            };
            $http({
                url: '/job/Triggers/reBuildTrigger.shtml',
                method: 'POST',
                data: pData
            }).success(function (response) { //提交成功
                if (response.success) { //信息处理成功，进入用户中心页面
                    $scope.trigger = null;
                    table.draw(); //重新加载数据
                    ckClickTr($scope.gridtableid); //单击行，选中复选框
                    rzhdialog(ngDialog, response.info, "success");
                    $scope.toRemove();
                } else { //信息处理失败，提示错误信息
                    rzhdialog(ngDialog, response.info, "error");
                }
            }).error(function (response) { //提交失败
                rzhdialog(ngDialog, "操作失败", "error");
            })
        }
    }
});