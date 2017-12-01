/*
 * Copyright (c) 2016. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */
var grid_levels = "";
App.controller('appverController', ['$scope', '$http', "ngDialog", function ($scope, $http, ngDialog) {
    $scope.gridtableid = "appverTableGrids";
    grid_levels = ""; //初始化，必须
    var levels = [
        {
            component: "base",
            operations: [
                {
                    btn: "add",
                    name: "添加",
                    func: "toAdd"
                }
            ],
            name: "外层功能",
            id: "toBase"
        }
        //,{
        //    component: "grid",
        //    operations: [
        //        {
        //            btn: "grid_stop",
        //            name: "暂停",
        //            func: "gridStop"
        //        },
        //        {
        //            btn: "grid_start",
        //            name: "启用",
        //            func: "gridStart"
        //        }
        //    ],
        //    name: "数据列表",
        //    id: "grid"
        //}
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
        window.location.href = "#/app/appver/add";
    });
    //关闭弹出窗口操作
    $scope.toRemove = function () {
        window.location.href = "#/app/appver";
    }

}]);

//列表信息处理
App.controller('appverGridController', ['$scope', "$http", "ngDialog", function ($scope, $http, ngDialog) {
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
        pageLength: 30000,                    //每页显示30000条数据
        scrollX: true,                      //显示横向进度条
        sPaginationType: "full_numbers",
        ajaxSource: "/appver/listAppVerInfo.shtml",//获取数据的url
        fnDrawCallback: function () {
            this.api().column(1).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        fnServerData: retrieveData,           //获取数据的处理函数
        aoColumns: [
            {sTitle: getHtmlInfos("app/views/base/check_all.html"), width: "5%"},
            {sTitle: "序号"},
            {mDataProp: "versionName", sTitle: "版本名"},
            {mDataProp: "version", sTitle: "版本号"},
            {mDataProp: "appType", sTitle: "类型"},
            {mDataProp: "downloadUrl", sTitle: "下载路径"},
            {mDataProp: "summary", sTitle: "摘要"},
            {mDataProp: "isInUse", sTitle: "是否使用",
                render: function (data) {
                    return data == 1 ? '启用' :'停用';
                }
            },
            {mDataProp: "crtime", sTitle: "创建时间"},
            {mDataProp: "isInUse",sTitle: "操作",sWidth:"220px",
                render: function (data) {
                    if( data == 1) return getHtmlInfos("app/views/base/grid_stop.html","停用","gridStop");
                    return getHtmlInfos("app/views/base/grid_start.html","启用","gridStart");
                }
            }
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
    //行内停用
    $("#" + $scope.gridtableid + " tbody").on('click', 'tr td button.gridStop', function () {
        var data = table.row($(this).parent().parent()).data(); //获取爷爷节点 tr 的值
        ngDialog.openConfirm({
            template: 'stopappDialogId',
            className: 'ngdialog-theme-default'
        }).then(function (value) { //用户点击确认
            angularParamString($http); //解决post提交接收问题，json方式改为string方式
            $http({
                url: '/appver/upAppVerInUse.shtml',
                method: 'POST',
                data: {version: data.version,isInUse:0}
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
            template: 'startappDialogId',
            className: 'ngdialog-theme-default'
        }).then(function (value) { //用户点击确认
            angularParamString($http); //解决post提交接收问题，json方式改为string方式
            $http({
                url: '/appver/upAppVerInUse.shtml',
                method: 'POST',
                data: {version: data.version,isInUse:1}
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
App.controller("addAppverController",function ($scope, $http, ngDialog,FileUploader) {
    $scope.appver = null;
    ////上传文件处理 begin
    var uploader = $scope.uploader = new FileUploader({
        url: '/appver/uploadapk.shtml'
    });
    $scope.clearItems = function(){ //重新选择文件时，清空队列，达到覆盖文件的效果
        uploader.clearQueue();
    }
    //定义callback函数
    uploader.onBeforeUploadItem = function(item) {
        var formData = [{
            appType: $scope.appver.appType
        }];
        Array.prototype.push.apply(item.formData, formData);
    };
    uploader.onSuccessItem = function(fileItem, response, status, headers) { //上传成功
        if (response.success) { //信息处理成功，进入用户中心页面
            rzhdialog(ngDialog,"文件上传成功！","success");
            $scope.updatedata();
        } else { //信息处理失败，提示错误信息
            rzhdialog(ngDialog,response.info,"error");
        }
        $scope.clearItems();
    };
    uploader.onErrorItem = function (fileItem, response, status, headers) {
        $scope.clearItems();
        rzhdialog(ngDialog,"文件上传失败！","error");
    };
    ////上传文件处理 end
    //添加数据
    $scope.save = function () {
        angularParamString($http); //解决post提交接收问题，json方式改为string方式
        var table = $('#' + $scope.gridtableid).DataTable();
        //验证通过
        if ($scope.form.$invalid) {
            rzhdialog(ngDialog, "您输入的表单内容的不合法，请修改后提交", "error");
            return;
        } else {
            if(typeof($scope.appver.appType)=="undefined")$scope.appver.appType="android";//默认为apk
            uploader.uploadAll();
        }
    }
    //修改數據
    $scope.updatedata=function(){
        //增加分类属性
        var pData = {
            versionName: $scope.appver.versionName,
            appType: $scope.appver.appType,
            isInUse: $scope.appver.isInUse,
            summary: $scope.appver.summary
        };
        $http({
            url: '/appver/upLatestAppVer.shtml',
            method: 'POST',
            data: pData
        }).success(function (response) { //提交成功
            if (response.success) { //信息处理成功，进入用户中心页面
                $scope.appver = null;
                //上传文件
                var table = $('#'+$scope.gridtableid).DataTable();
                table.draw(); //重新加载数据
                ckClickTr($scope.gridtableid); //单击行，选中复选框
                rzhdialog(ngDialog,response.info,"success");
                $scope.toRemove();
                //刷新页面
            } else { //信息处理失败，提示错误信息
                rzhdialog(ngDialog, response.info, "error");
            }
        }).error(function (response) { //提交失败
            rzhdialog(ngDialog, "操作失败", "error");
        })
    }
});