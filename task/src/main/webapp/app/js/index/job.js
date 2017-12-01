/*
 * Copyright (c) 2016. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */
var grid_levels = "";
App.controller('jobController', ['$scope', '$http', "ngDialog", function ($scope, $http, ngDialog) {
    $scope.gridtableid = "jobTableGrids";
    grid_levels = ""; //初始化，必须
    var levels = [
        {
            component: "grid",
            operations: [
                {
                    btn: "grid_details",
                    name: "详情查看",
                    func: "gridView"
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
    //关闭弹出窗口操作
    $scope.toRemove = function () {
        window.location.href = "#/app/job";
    }

}]);

//列表信息处理
App.controller('jobGridController', ['$scope', "$http", "ngDialog", function ($scope, $http, ngDialog) {
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
        ajaxSource: "/job/Triggers/listJobDetails.shtml",//获取数据的url
        fnDrawCallback: function () {
            this.api().column(1).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        fnServerData: retrieveData,           //获取数据的处理函数
        aoColumns: [
            {sTitle: getHtmlInfos("app/views/base/check_all.html"), width: "5%"},
            {sTitle: "序号"},
            {mDataProp: "jobName", sTitle: "作业"},
            {mDataProp: "jobGroup", sTitle: "分组"},
            {mDataProp: "jobClassName", sTitle: "类名"},
            {mDataProp: "schedName", sTitle: "调度名"},
            {mDataProp: "description", sTitle: "描述"},
            {sTitle: "操作"}
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
    $("#" + $scope.gridtableid + " tbody").on('click', 'tr td button.gridView', function () {
        var data = table.row($(this).parent().parent()).data(); //获取爷爷节点 tr 的值
        viewjobControllerData=data;
        window.location.href = "#/app/job/view";
    });
}]);


var viewjobControllerData=null;
// 查看详情
App.controller("viewjobController", function ($scope, $stateParams,$http, ngDialog) {
    $scope.job=viewjobControllerData;
    if( $scope.job==null){
        $scope.toRemove();
    }
    $scope.job.isDurable=$scope.job.isDurable==1?"是":"否"
    $scope.job.isNonconcurrent=$scope.job.isNonconcurrent==1?"是":"否"
    $scope.job.isUpdateData=$scope.job.isUpdateData==1?"是":"否"
});